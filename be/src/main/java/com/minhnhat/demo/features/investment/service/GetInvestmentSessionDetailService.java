package com.minhnhat.demo.features.investment.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.InvestmentAssetSelectionEntity;
import com.minhnhat.demo.common.db.entity.InvestmentSessionAllocationEntity;
import com.minhnhat.demo.common.db.entity.InvestmentSessionEntity;
import com.minhnhat.demo.common.db.repository.InvestmentAssetSelectionRepository;
import com.minhnhat.demo.common.db.repository.InvestmentSessionRepository;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.response.InvestmentSessionDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetInvestmentSessionDetailService {

    private final InvestmentSessionRepository sessionRepository;
    private final InvestmentAssetSelectionRepository selectionRepository;
    private final MessageService messageService;
    private final SecurityUtils securityUtils;

    public ApiResponse<InvestmentSessionDetailResponse> execute(Long sessionId) {
        Context ctx = new Context();
        ctx.sessionId = sessionId;
        this.validateRequest(ctx);
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        // Validation moved to loadData to use single query with allocations
    }

    private void loadData(Context ctx) {
        String userId = getCurrentUserId();
        log.debug("Loading investment session: sessionId={}, userId={}", ctx.sessionId, userId);

        ctx.session = sessionRepository.findByIdWithAllocations(ctx.sessionId)
                .orElseThrow(() -> {
                    log.error("Investment session not found: sessionId={}, userId={}", ctx.sessionId, userId);
                    return new IllegalArgumentException("Investment session not found");
                });

        log.debug("Session loaded: id={}, userId={}, status={}, allocations={}",
            ctx.session.getId(), ctx.session.getUserId(), ctx.session.getStatus(),
            ctx.session.getAllocations().size());

        // Verify ownership after loading
        if (!ctx.session.getUserId().equals(userId)) {
            log.warn("Session ownership mismatch: sessionId={}, expectedUserId={}, actualUserId={}",
                ctx.sessionId, userId, ctx.session.getUserId());
            throw new IllegalArgumentException("Investment session not found");
        }
    }

    private void processBusiness(Context ctx) {
        List<InvestmentSessionDetailResponse.AllocationDetail> allocations = ctx.session.getAllocations().stream()
                .map(allocation -> {
                    List<InvestmentAssetSelectionEntity> selections = selectionRepository.findByAllocationId(allocation.getId());
                    List<InvestmentSessionDetailResponse.AssetSelection> selectionResponses = selections.stream()
                            .map(selection -> InvestmentSessionDetailResponse.AssetSelection.builder()
                                    .selectionId(selection.getId())
                                    .assetCode(selection.getAssetCode())
                                    .assetName(selection.getAssetName())
                                    .quantity(selection.getQuantity())
                                    .buyPrice(selection.getBuyPrice())
                                    .investedAmount(selection.getInvestedAmount())
                                    .createdAt(selection.getCreatedAt())
                                    .build())
                            .collect(Collectors.toList());

                    return InvestmentSessionDetailResponse.AllocationDetail.builder()
                            .allocationId(allocation.getId())
                            .assetType(allocation.getAssetType())
                            .recommendedAmount(allocation.getRecommendedAmount())
                            .percentage(allocation.getPercentage())
                            .status(allocation.getStatus())
                            .investedAmount(allocation.getInvestedAmount())
                            .residualAmount(allocation.getResidualAmount())
                            .selections(selectionResponses)
                            .build();
                })
                .collect(Collectors.toList());

        ctx.response = InvestmentSessionDetailResponse.builder()
                .sessionId(ctx.session.getId())
                .userId(ctx.session.getUserId())
                .strategyCode(ctx.session.getStrategyCode())
                .totalAmount(ctx.session.getTotalAmount())
                .status(ctx.session.getStatus())
                .currentStep(ctx.session.getCurrentStep())
                .createdAt(ctx.session.getCreatedAt())
                .updatedAt(ctx.session.getUpdatedAt())
                .allocations(allocations)
                .build();
    }

    private ApiResponse<InvestmentSessionDetailResponse> createResponse(Context ctx) {
        return ApiResponse.<InvestmentSessionDetailResponse>builder()
                .data(ctx.response)
                .message(InvestmentWorkflowConstant.GET_DETAIL_INFO,
                        messageService.get(InvestmentWorkflowConstant.GET_DETAIL_INFO),
                        InvestmentWorkflowConstant.SERVICE_NAME)
                .build();
    }

    private String getCurrentUserId() {
        return securityUtils.getCurrentUser().getUserId();  // ← Lấy userId chính xác
    }

    private static class Context {
        Long sessionId;
        InvestmentSessionEntity session;
        InvestmentSessionDetailResponse response;
    }
}

