package com.minhnhat.demo.features.investment.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.*;
import com.minhnhat.demo.common.db.repository.*;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.response.FinalizeSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FinalizeInvestmentSessionService {

    private final InvestmentSessionRepository sessionRepository;
    private final UserCashBalanceRepository cashBalanceRepository;
    private final InvestmentSessionEventRepository eventRepository;
    private final MessageService messageService;
    private final SecurityUtils securityUtils;

    public ApiResponse<FinalizeSessionResponse> execute(Long sessionId) {
        Context ctx = new Context();
        ctx.sessionId = sessionId;
        this.validateRequest(ctx);
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        String userId = getCurrentUserId();
        ctx.userId = userId;
        // Validation moved to loadData to use single query with allocations
    }

    private void loadData(Context ctx) {
        ctx.session = sessionRepository.findByIdWithAllocations(ctx.sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Investment session not found"));

        // Verify ownership after loading
        if (!ctx.session.getUserId().equals(ctx.userId)) {
            throw new IllegalArgumentException("Investment session not found");
        }

        // Session must be in REVIEW status
        if (!InvestmentWorkflowConstant.STATUS_REVIEW.equals(ctx.session.getStatus())) {
            throw new IllegalArgumentException("Session must be in REVIEW status to finalize");
        }

        ctx.cashBalance = cashBalanceRepository.findByUserId(ctx.userId)
                .orElseThrow(() -> new IllegalArgumentException("User cash balance not found"));
    }

    private void processBusiness(Context ctx) {
        // Calculate totals
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalResidual = BigDecimal.ZERO;

        for (var allocation : ctx.session.getAllocations()) {
            totalInvested = totalInvested.add(allocation.getInvestedAmount());
            totalResidual = totalResidual.add(allocation.getResidualAmount());
        }

        ctx.totalInvested = totalInvested;
        ctx.totalResidual = totalResidual;

        // Update cash balance: add residual to cash
        BigDecimal newCashAmount = ctx.cashBalance.getCashAmount().add(totalResidual);
        ctx.cashBalance.setCashAmount(newCashAmount);
        ctx.cashBalance.setUpdatedAt(LocalDateTime.now());
        cashBalanceRepository.save(ctx.cashBalance);

        ctx.updatedCashBalance = newCashAmount;

        // Update session status and step to COMPLETED
        ctx.session.setStatus(InvestmentWorkflowConstant.STATUS_COMPLETED);
        ctx.session.setCurrentStep(InvestmentWorkflowConstant.STEP_FINALIZE);
        sessionRepository.save(ctx.session);

        // Create event
        InvestmentSessionEventEntity event = InvestmentSessionEventEntity.builder()
                .sessionId(ctx.sessionId)
                .eventType(InvestmentWorkflowConstant.EVENT_SESSION_FINALIZED)
                .payload("{\"totalInvested\": " + totalInvested + ", \"totalResidual\": " + totalResidual + ", \"newCashBalance\": " + newCashAmount + "}")
                .build();
        eventRepository.save(event);
    }

    private ApiResponse<FinalizeSessionResponse> createResponse(Context ctx) {
        FinalizeSessionResponse response = FinalizeSessionResponse.builder()
                .sessionId(ctx.sessionId)
                .status(ctx.session.getStatus())
                .totalInvested(ctx.totalInvested)
                .totalResidual(ctx.totalResidual)
                .updatedCashBalance(ctx.updatedCashBalance)
                .completedAt(ctx.session.getUpdatedAt())
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
        InvestmentSessionEntity session;
        UserCashBalanceEntity cashBalance;
        BigDecimal totalInvested;
        BigDecimal totalResidual;
        BigDecimal updatedCashBalance;
    }
}

