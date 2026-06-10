package com.minhnhat.demo.features.investment.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.*;
import com.minhnhat.demo.common.db.repository.*;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.request.FinalizeAllocationItem;
import com.minhnhat.demo.features.investment.dto.request.FinalizeSessionRequest;
import com.minhnhat.demo.features.investment.dto.response.FinalizeSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FinalizeInvestmentSessionService {

    private final InvestmentSessionRepository sessionRepository;
    private final InvestmentSessionAllocationRepository allocationRepository;
    private final InvestmentAssetSelectionRepository selectionRepository;
    private final PortfolioRepository portfolioRepository;
    private final InvestmentSessionEventRepository eventRepository;
    private final MessageService messageService;
    private final SecurityUtils securityUtils;

    public ApiResponse<FinalizeSessionResponse> execute(Long sessionId, FinalizeSessionRequest request) {
        Context ctx = new Context();
        ctx.sessionId = sessionId;
        ctx.request = request;
        this.validateRequest(ctx);
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        ctx.userId = getCurrentUserId();
        // Validation moved to loadData to use single query with allocations
    }

    private void loadData(Context ctx) {
        log.info("Loading investment session: sessionId={}, userId={}", ctx.sessionId, ctx.userId);

        ctx.session = sessionRepository.findByIdWithAllocations(ctx.sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Investment session not found"));

        // Verify user ownership
        if (!ctx.session.getUserId().equals(ctx.userId)) {
            throw new IllegalArgumentException("Investment session not found");
        }

        // Session must be in REVIEW status before finalization
        if (!InvestmentWorkflowConstant.STATUS_REVIEW.equals(ctx.session.getStatus())) {
            throw new IllegalArgumentException("Session must be in REVIEW status to finalize");
        }

        log.info("Session loaded successfully: sessionId={}, status={}", ctx.sessionId, ctx.session.getStatus());
    }

    private void processBusiness(Context ctx) {
        log.info("Starting finalization process for sessionId={}", ctx.sessionId);

        // Step 1: Update allocations with final amounts from FE
        // Note: Residual calculation is handled by FE and accumulated into EMERGENCY
        if (ctx.request != null && ctx.request.getAllocations() != null && !ctx.request.getAllocations().isEmpty()) {
            Map<String, FinalizeAllocationItem> finalAmountsMap = ctx.request.getAllocations().stream()
                    .collect(Collectors.toMap(FinalizeAllocationItem::getAssetType, item -> item));

            for (var allocation : ctx.session.getAllocations()) {
                FinalizeAllocationItem finalItem = finalAmountsMap.get(allocation.getAssetType());
                if (finalItem != null && finalItem.getFinalAmount() != null) {
                    BigDecimal finalAmount = finalItem.getFinalAmount();
                    BigDecimal recommendedAmount = allocation.getRecommendedAmount();

                    // Validation: finalAmount must be non-negative
                    if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("Final amount cannot be negative for " + allocation.getAssetType());
                    }

                    // Validation: finalAmount must not exceed recommendedAmount
                    // EXCEPTION: EMERGENCY fund can exceed recommended amount
                    // (it accumulates residual amounts from other asset types)
                    boolean isEmergency = InvestmentWorkflowConstant.ASSET_TYPE_EMERGENCY.equals(allocation.getAssetType());
                    if (!isEmergency && finalAmount.compareTo(recommendedAmount) > 0) {
                        log.warn("Final amount exceeds recommended for assetType={}, final={}, recommended={}",
                                allocation.getAssetType(), finalAmount, recommendedAmount);
                        throw new IllegalArgumentException(
                                String.format("Final amount %.0f exceeds recommended amount %.0f for %s",
                                        finalAmount, recommendedAmount, allocation.getAssetType())
                        );
                    }


                    // Update allocation
                    allocation.setInvestedAmount(finalAmount);
                    allocation.setStatus(InvestmentWorkflowConstant.ALLOCATION_COMPLETED);
                    allocationRepository.save(allocation);

                    log.info("Allocation finalized: assetType={}, amount={}, status=COMPLETED",
                            allocation.getAssetType(), finalAmount);
                }
            }
        }

        // Step 2: Insert/Update portfolio entries for all assets
        // This records the actual investment in portfolio table
        for (var allocation : ctx.session.getAllocations()) {
            if (allocation.getInvestedAmount() != null && allocation.getInvestedAmount().compareTo(BigDecimal.ZERO) > 0) {
                // Calculate total quantity for this allocation's asset selections
                BigDecimal totalQuantity = calculateAllocationQuantity(allocation.getId());
                insertOrUpdatePortfolio(ctx.userId, allocation.getAssetType(), allocation.getInvestedAmount(), totalQuantity);
            }
        }

        // Step 3: Update session status to COMPLETED
        ctx.session.setStatus(InvestmentWorkflowConstant.STATUS_COMPLETED);
        ctx.session.setCurrentStep(InvestmentWorkflowConstant.STEP_FINALIZE);
        ctx.session.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(ctx.session);

        log.info("Session finalized: sessionId={}, status=COMPLETED", ctx.sessionId);

        // Step 4: Create audit event
        InvestmentSessionEventEntity event = InvestmentSessionEventEntity.builder()
                .sessionId(ctx.sessionId)
                .eventType(InvestmentWorkflowConstant.EVENT_SESSION_FINALIZED)
                .payload(buildEventPayload(ctx))
                .build();
        eventRepository.save(event);

        log.info("Finalization completed: sessionId={}", ctx.sessionId);
    }

    /**
     * Calculate total quantity for an allocation from its asset selections
     * Sums up all quantities of selected assets for this allocation
     */
    private BigDecimal calculateAllocationQuantity(Long allocationId) {
        List<InvestmentAssetSelectionEntity> selections = selectionRepository.findByAllocationId(allocationId);
        if (selections == null || selections.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return selections.stream()
                .map(InvestmentAssetSelectionEntity::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Insert or update portfolio entry for an asset
     * Handles accumulation of investments from multiple rounds
     * Special case: EMERGENCY fund only accumulates (no profit/loss calculation)
     *
     * For non-EMERGENCY assets:
     * - Uses actual quantity from investment selections
     * - Accumulates to existing portfolio quantity
     *
     * @param userId the user ID
     * @param assetType the asset type (GOLD, ETF, STOCK, EMERGENCY)
     * @param investedAmount the final amount invested in this asset
     * @param selectionQuantity total quantity from asset selections for this allocation
     */
    private void insertOrUpdatePortfolio(String userId, String assetType, BigDecimal investedAmount, BigDecimal selectionQuantity) {
        Optional<PortfolioEntity> existing = portfolioRepository.findByUserIdAndAssetType(userId, assetType);

        if (existing.isPresent()) {
            // Update existing portfolio: accumulate investment
            PortfolioEntity portfolio = existing.get();
            portfolio.setTotalInvested(portfolio.getTotalInvested().add(investedAmount));

            // For EMERGENCY: quantity = total (since it's cash equivalent, no price conversion)
            if (InvestmentWorkflowConstant.ASSET_TYPE_EMERGENCY.equals(assetType)) {
                portfolio.setQuantity(portfolio.getTotalInvested());
            } else {
                // For other assets: accumulate the quantity from selections
                portfolio.setQuantity(portfolio.getQuantity().add(selectionQuantity));
            }

            portfolioRepository.save(portfolio);
            log.info("Portfolio updated: userId={}, assetType={}, newTotal={}, quantity={}",
                    userId, assetType, portfolio.getTotalInvested(), portfolio.getQuantity());
        } else {
            // Create new portfolio entry
            BigDecimal quantity;
            if (InvestmentWorkflowConstant.ASSET_TYPE_EMERGENCY.equals(assetType)) {
                // For EMERGENCY: quantity = total amount (cash equivalent)
                quantity = investedAmount;
            } else {
                // For other assets: use selection quantity
                quantity = selectionQuantity;
            }

            PortfolioEntity portfolio = PortfolioEntity.builder()
                    .userId(userId)
                    .assetType(assetType)
                    .totalInvested(investedAmount)
                    .quantity(quantity)
                    .build();
            portfolioRepository.save(portfolio);
            log.info("Portfolio created: userId={}, assetType={}, amount={}, quantity={}",
                    userId, assetType, investedAmount, quantity);
        }
    }

    private String buildEventPayload(Context ctx) {
        StringBuilder payload = new StringBuilder("{");
        payload.append("\"totalInvested\": ").append(getTotalInvested(ctx)).append(", ");
        payload.append("\"status\": \"COMPLETED\", ");
        payload.append("\"allocations\": [");

        var allocations = ctx.session.getAllocations();
        for (int i = 0; i < allocations.size(); i++) {
            var a = allocations.get(i);
            payload.append("{\"assetType\": \"").append(a.getAssetType())
                    .append("\", \"amount\": ").append(a.getInvestedAmount()).append("}");
            if (i < allocations.size() - 1) payload.append(", ");
        }
        payload.append("]}");
        return payload.toString();
    }

    private BigDecimal getTotalInvested(Context ctx) {
        return ctx.session.getAllocations().stream()
                .map(a -> a.getInvestedAmount() != null ? a.getInvestedAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ApiResponse<FinalizeSessionResponse> createResponse(Context ctx) {
        log.info("Creating finalization response for sessionId={}", ctx.sessionId);

        // Build allocation details from finalized session
        List<FinalizeSessionResponse.AllocationDetail> allocationDetails = ctx.session.getAllocations().stream()
                .map(allocation -> FinalizeSessionResponse.AllocationDetail.builder()
                        .allocationId(allocation.getId())
                        .assetType(allocation.getAssetType())
                        .recommendedAmount(allocation.getRecommendedAmount())
                        .investedAmount(allocation.getInvestedAmount())
                        .status(allocation.getStatus())
                        .build())
                .toList();

        BigDecimal totalInvested = getTotalInvested(ctx);

        FinalizeSessionResponse response = FinalizeSessionResponse.builder()
                .sessionId(ctx.sessionId)
                .status(ctx.session.getStatus())
                .totalInvested(totalInvested)
                .completedAt(ctx.session.getUpdatedAt())
                .allocations(allocationDetails)
                .build();

        return ApiResponse.<FinalizeSessionResponse>builder()
                .data(response)
                .message(InvestmentWorkflowConstant.FINALIZE_SESSION_INFO,
                        messageService.get(InvestmentWorkflowConstant.FINALIZE_SESSION_INFO),
                        InvestmentWorkflowConstant.SERVICE_NAME)
                .build();
    }

    private String getCurrentUserId() {
        return securityUtils.getCurrentUser().getUserId();
    }

    private static class Context {
        Long sessionId;
        String userId;
        FinalizeSessionRequest request;
        InvestmentSessionEntity session;
    }
}

