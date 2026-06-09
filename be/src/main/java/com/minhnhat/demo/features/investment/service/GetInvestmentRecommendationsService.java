package com.minhnhat.demo.features.investment.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.InvestmentSessionAllocationEntity;
import com.minhnhat.demo.common.db.entity.InvestmentSessionEntity;
import com.minhnhat.demo.common.db.repository.InvestmentSessionRepository;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.response.InvestmentRecommendationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetInvestmentRecommendationsService {

    private final InvestmentSessionRepository sessionRepository;
    private final MessageService messageService;
    private final SecurityUtils securityUtils;

    public ApiResponse<InvestmentRecommendationResponse> execute(Long sessionId) {
        Context ctx = new Context();
        ctx.sessionId = sessionId;
        this.validateRequest(ctx);
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        // Don't have any to validate
    }

    private void loadData(Context ctx) {
        String userId = getCurrentUserId();
        ctx.session = sessionRepository.findByIdWithAllocations(ctx.sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Investment session not found"));

        // Verify ownership after loading
        if (!ctx.session.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Investment session not found");
        }
    }

    private void processBusiness(Context ctx) {
        ctx.recommendations = ctx.session.getAllocations().stream()
                .map(allocation -> InvestmentRecommendationResponse.AllocationRecommendation.builder()
                        .type(allocation.getAssetType())
                        .amount(allocation.getRecommendedAmount())
                        .percent(allocation.getPercentage())
                        .build())
                .collect(Collectors.toList());
    }

    private ApiResponse<InvestmentRecommendationResponse> createResponse(Context ctx) {
        InvestmentRecommendationResponse response = InvestmentRecommendationResponse.builder()
                .sessionId(ctx.sessionId)
                .recommendations(ctx.recommendations)
                .build();

        return ApiResponse.<InvestmentRecommendationResponse>builder()
                .data(response)
                .message(InvestmentWorkflowConstant.GET_RECOMMENDATION_INFO,
                        messageService.get(InvestmentWorkflowConstant.GET_RECOMMENDATION_INFO),
                        InvestmentWorkflowConstant.SERVICE_NAME)
                .build();
    }

    private String getCurrentUserId() {
        return securityUtils.getCurrentUser().getUserId();
    }

    private static class Context {
        Long sessionId;
        InvestmentSessionEntity session;
        List<InvestmentRecommendationResponse.AllocationRecommendation> recommendations;
    }
}


