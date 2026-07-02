package com.minhnhat.demo.features.investment.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.*;
import com.minhnhat.demo.common.db.repository.*;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.request.SelectAssetRequest;
import com.minhnhat.demo.features.investment.dto.response.SelectAssetResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SelectAssetForAllocationService {

    private final InvestmentSessionRepository sessionRepository;
    private final InvestmentSessionAllocationRepository allocationRepository;
    private final InvestmentAssetSelectionRepository selectionRepository;
    private final AssetPriceRepository assetPriceRepository;
    private final AssetProductRepository assetProductRepository;
    private final InvestmentSessionEventRepository eventRepository;
    private final MessageService messageService;
    private final SecurityUtils securityUtils;

    public ApiResponse<SelectAssetResponse> execute(Long sessionId, Long allocationId, SelectAssetRequest request) {
        Context ctx = new Context();
        ctx.sessionId = sessionId;
        ctx.allocationId = allocationId;
        ctx.request = request;
        this.validateRequest(ctx);
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        String userId = getCurrentUserId();
        log.debug("Validating request: sessionId={}, allocationId={}, userId={}",
                ctx.sessionId, ctx.allocationId, userId);

        // Verify session exists and belongs to user
        ctx.session = sessionRepository.findByIdAndUserId(ctx.sessionId, userId)
                .orElseThrow(() -> {
                    log.error("Session not found: sessionId={}, userId={}", ctx.sessionId, userId);
                    return new IllegalArgumentException("Investment session not found");
                });

        // Verify allocation exists
        ctx.allocation = allocationRepository.findByIdAndSessionId(ctx.allocationId, ctx.sessionId)
                .orElseThrow(() -> {
                    log.error("Allocation not found: allocationId={}, sessionId={}", ctx.allocationId, ctx.sessionId);
                    return new IllegalArgumentException("Allocation not found");
                });

        // Check if allocation is locked
        if (InvestmentWorkflowConstant.ALLOCATION_LOCKED.equals(ctx.allocation.getStatus())) {
            log.warn("Allocation is locked: allocationId={}", ctx.allocationId);
            throw new IllegalArgumentException("This allocation is locked and cannot be processed");
        }

        // Validate quantity
        if (ctx.request.getQuantity() == null || ctx.request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid quantity: {}", ctx.request.getQuantity());
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        log.debug("Request validation successful");
    }

    private void loadData(Context ctx) {
        log.debug("Loading asset data: productId={}, assetType={}",
                ctx.request.getProductId(), ctx.request.getAssetType());

        // Step 1: Validate assetType matches allocation's assetType
        String allocationAssetType = ctx.allocation.getAssetType();
        String requestAssetType = ctx.request.getAssetType();

        if (!allocationAssetType.equals(requestAssetType)) {
            log.error("Asset type mismatch: allocation expects {}, request provides {}",
                    allocationAssetType, requestAssetType);
            throw new IllegalArgumentException("Asset type does not match allocation type");
        }

        // Step 2: Determine productId (use from request or find by assetCode)
        Long productId = ctx.request.getProductId();

        if (productId == null) {
            // Fallback to assetCode if productId not provided (backward compatibility)
            if (ctx.request.getAssetCode() != null) {
                AssetProductEntity product = assetProductRepository.findByCode(ctx.request.getAssetCode())
                        .orElseThrow(() -> {
                            log.error("Product not found for assetCode={}", ctx.request.getAssetCode());
                            return new IllegalArgumentException("Asset not found");
                        });
                productId = product.getId();
                ctx.assetCode = product.getCode();
                ctx.assetName = product.getName();
                log.debug("Product found by assetCode: id={}, code={}, name={}",
                        product.getId(), product.getCode(), product.getName());
            } else {
                throw new IllegalArgumentException("Either productId or assetCode must be provided");
            }
        } else {
            // Find product by productId
            final Long finalProductId = productId;
            AssetProductEntity product = assetProductRepository.findByIdAndIsActiveTrue(finalProductId)
                    .orElseThrow(() -> {
                        log.error("Product not found for productId={}", finalProductId);
                        return new IllegalArgumentException("Asset not found");
                    });
            ctx.assetCode = product.getCode();
            ctx.assetName = product.getName();
            log.debug("Product found by productId: id={}, code={}, name={}",
                    product.getId(), product.getCode(), product.getName());
        }

        // Step 3: Find latest price for this product
        final Long finalProductId = productId;
        ctx.asset = assetPriceRepository.findByProductIdAndIsLatestTrue(finalProductId)
                .orElseThrow(() -> {
                    log.error("Price not found for productId={}", finalProductId);
                    return new IllegalArgumentException("Asset price not found");
                });

        log.debug("Asset loaded: productId={}, assetCode={}, assetType={}, buyPrice={}",
                finalProductId, ctx.assetCode, requestAssetType, ctx.asset.getCurrentPrice());

        // Validate total invested amount
        BigDecimal investedAmount = ctx.request.getQuantity().multiply(ctx.asset.getCurrentPrice());
        BigDecimal totalInvested = ctx.allocation.getInvestedAmount().add(investedAmount);

        if (totalInvested.compareTo(ctx.allocation.getRecommendedAmount()) > 0) {
            log.error("Investment amount exceeds limit: total={}, recommended={}",
                    totalInvested, ctx.allocation.getRecommendedAmount());
            throw new IllegalArgumentException("Invested amount cannot exceed recommended amount");
        }

        log.debug("Data loading successful");
    }

    private void processBusiness(Context ctx) {
        log.debug("Processing business logic: allocationId={}", ctx.allocationId);

        BigDecimal investedAmount = ctx.request.getQuantity().multiply(ctx.asset.getCurrentPrice());

        // Create asset selection record
        InvestmentAssetSelectionEntity selection = InvestmentAssetSelectionEntity.builder()
                .sessionId(ctx.sessionId)
                .allocationId(ctx.allocationId)
                .assetCode(ctx.assetCode)
                .assetName(ctx.assetName)  // Use product name from DB
                .categoryCode(ctx.request.getAssetType())  // Store asset type as categoryCode
                .quantity(ctx.request.getQuantity())
                .buyPrice(ctx.asset.getCurrentPrice())
                .investedAmount(investedAmount)
                .productId(ctx.request.getProductId())  // Store productId reference
                .build();
        ctx.selection = selectionRepository.save(selection);
        log.debug("Asset selection created: selectionId={}, assetCode={}, assetName={}, assetType={}, quantity={}, investedAmount={}",
                ctx.selection.getId(), ctx.selection.getAssetCode(), ctx.assetName, ctx.request.getAssetType(),
                ctx.selection.getQuantity(), investedAmount);

        // Update allocation status and amounts
        ctx.allocation.setStatus(InvestmentWorkflowConstant.ALLOCATION_IN_PROGRESS);
        BigDecimal newInvestedAmount = ctx.allocation.getInvestedAmount().add(investedAmount);
        ctx.allocation.setInvestedAmount(newInvestedAmount);
        ctx.allocation.setResidualAmount(ctx.allocation.getRecommendedAmount().subtract(newInvestedAmount));
        allocationRepository.save(ctx.allocation);
        log.debug("Allocation updated: status={}, investedAmount={}, residualAmount={}",
                ctx.allocation.getStatus(), newInvestedAmount, ctx.allocation.getResidualAmount());

        // Create event log
        InvestmentSessionEventEntity event = InvestmentSessionEventEntity.builder()
                .sessionId(ctx.sessionId)
                .eventType(InvestmentWorkflowConstant.EVENT_ASSET_SELECTED)
                .payload("{\"allocationId\": " + ctx.allocationId + ", \"assetCode\": \"" + ctx.assetCode + "\"}")
                .build();
        eventRepository.save(event);
        log.debug("Event created: eventType={}", InvestmentWorkflowConstant.EVENT_ASSET_SELECTED);
    }

    private ApiResponse<SelectAssetResponse> createResponse(Context ctx) {
        log.debug("Creating response for selection: selectionId={}", ctx.selection.getId());

        SelectAssetResponse response = SelectAssetResponse.builder()
                .selectionId(ctx.selection.getId())
                .allocationId(ctx.allocationId)
                .assetCode(ctx.selection.getAssetCode())
                .assetType(ctx.selection.getCategoryCode())  // Use categoryCode as assetType
                .assetName(ctx.selection.getAssetName())
                .quantity(ctx.selection.getQuantity())
                .buyPrice(ctx.selection.getBuyPrice())
                .investedAmount(ctx.selection.getInvestedAmount())
                .residualAmount(ctx.allocation.getResidualAmount())
                .createdAt(ctx.selection.getCreatedAt())
                .build();

        log.debug("Response created successfully: assetCode={}, assetType={}, assetName={}",
                response.getAssetCode(), response.getAssetType(), response.getAssetName());

        return ApiResponse.<SelectAssetResponse>builder()
                .data(response)
                .message(InvestmentWorkflowConstant.SELECT_ASSET_INFO,
                        messageService.get(InvestmentWorkflowConstant.SELECT_ASSET_INFO),
                        InvestmentWorkflowConstant.SERVICE_NAME)
                .build();
    }

    private String getCurrentUserId() {
        return securityUtils.getCurrentUser().getUserId();
    }

    private static class Context {
        Long sessionId;
        Long allocationId;
        SelectAssetRequest request;
        InvestmentSessionEntity session;
        InvestmentSessionAllocationEntity allocation;
        AssetPriceEntity asset;
        String assetCode;      // Asset/product code from DB
        String assetName;      // Asset name from product entity
        InvestmentAssetSelectionEntity selection;
    }
}






