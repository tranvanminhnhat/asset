package com.minhnhat.demo.features.investment.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.*;
import com.minhnhat.demo.common.db.repository.*;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.request.CreateInvestmentSessionRequest;
import com.minhnhat.demo.features.investment.dto.response.CreateInvestmentSessionResponse;
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
public class CreateInvestmentSessionService {

    private final InvestmentSessionRepository sessionRepository;
    private final InvestmentSessionAllocationRepository allocationRepository;
    private final AllocationStrategyRepository strategyRepository;
    private final AllocationStrategyDetailRepository detailRepository;
    private final InvestmentSessionEventRepository eventRepository;
    private final MessageService messageService;
    private final SecurityUtils securityUtils;

    public ApiResponse<CreateInvestmentSessionResponse> execute(CreateInvestmentSessionRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        this.validateRequest(ctx);
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        // Validate strategy code exists
        ctx.strategy = strategyRepository.findByCode(ctx.request.getStrategyCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid strategy code"));

        // Validate total amount
        if (ctx.request.getTotalAmount() == null || ctx.request.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than 0");
        }
    }

    private void loadData(Context ctx) {
        ctx.userId = getCurrentUserId();
        ctx.strategyDetails = detailRepository.findByStrategyId(ctx.strategy.getId());
    }

    private void processBusiness(Context ctx) {
        // Create session
        InvestmentSessionEntity session = InvestmentSessionEntity.builder()
                .userId(ctx.userId)
                .strategyCode(ctx.request.getStrategyCode())
                .totalAmount(ctx.request.getTotalAmount())
                .status(InvestmentWorkflowConstant.STATUS_DRAFT)
                .currentStep(InvestmentWorkflowConstant.STEP_RECOMMEND)
                .build();
        ctx.session = sessionRepository.save(session);

        // Create allocations based on strategy details
        for (AllocationStrategyDetailEntity detail : ctx.strategyDetails) {
            BigDecimal recommendedAmount = ctx.request.getTotalAmount()
                    .multiply(detail.getPercentage())
                    .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);

            String allocationStatus = detail.getAssetType().equals(InvestmentWorkflowConstant.ASSET_TYPE_EMERGENCY)
                    ? InvestmentWorkflowConstant.ALLOCATION_LOCKED
                    : InvestmentWorkflowConstant.ALLOCATION_NOT_STARTED;

            InvestmentSessionAllocationEntity allocation = InvestmentSessionAllocationEntity.builder()
                    .sessionId(ctx.session.getId())
                    .assetType(detail.getAssetType())
                    .recommendedAmount(recommendedAmount)
                    .percentage(detail.getPercentage())
                    .status(allocationStatus)
                    .investedAmount(BigDecimal.ZERO)
                    .residualAmount(recommendedAmount)
                    .build();
            allocationRepository.save(allocation);
        }

        // Create event
        InvestmentSessionEventEntity event = InvestmentSessionEventEntity.builder()
                .sessionId(ctx.session.getId())
                .eventType(InvestmentWorkflowConstant.EVENT_SESSION_CREATED)
                .payload("{\"sessionId\": " + ctx.session.getId() + "}")
                .build();
        eventRepository.save(event);
    }

    private ApiResponse<CreateInvestmentSessionResponse> createResponse(Context ctx) {
        CreateInvestmentSessionResponse response = CreateInvestmentSessionResponse.builder()
                .sessionId(ctx.session.getId())
                .userId(ctx.userId)
                .strategyCode(ctx.session.getStrategyCode())
                .totalAmount(ctx.session.getTotalAmount())
                .status(ctx.session.getStatus())
                .currentStep(ctx.session.getCurrentStep())
                .createdAt(ctx.session.getCreatedAt())
                .build();

        return ApiResponse.<CreateInvestmentSessionResponse>builder()
                .data(response)
                .message(InvestmentWorkflowConstant.CREATE_SESSION_INFO,
                        messageService.get(InvestmentWorkflowConstant.CREATE_SESSION_INFO),
                        InvestmentWorkflowConstant.SERVICE_NAME)
                .build();
    }

    private String getCurrentUserId() {
        return securityUtils.getCurrentUser().getUserId();
    }

    private static class Context {
        CreateInvestmentSessionRequest request;
        String userId;
        AllocationStrategyEntity strategy;
        List<AllocationStrategyDetailEntity> strategyDetails;
        InvestmentSessionEntity session;
    }
}




