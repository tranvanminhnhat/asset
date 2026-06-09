package com.minhnhat.demo.features.investment.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.*;
import com.minhnhat.demo.common.db.repository.*;
import com.minhnhat.demo.common.db.repository.InvestmentSessionRepository;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.response.ReviewSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewInvestmentSessionService {

    private final InvestmentSessionRepository sessionRepository;
    private final InvestmentSessionEventRepository eventRepository;
    private final MessageService messageService;
    private final SecurityUtils securityUtils;

    public ApiResponse<ReviewSessionResponse> execute(Long sessionId) {
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
        ctx.session = sessionRepository.findByIdWithAllocations(ctx.sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Investment session not found"));

        // Verify ownership after loading
        if (!ctx.session.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Investment session not found");
        }
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

        // Update session status and step
        ctx.session.setStatus(InvestmentWorkflowConstant.STATUS_REVIEW);
        ctx.session.setCurrentStep(InvestmentWorkflowConstant.STEP_REVIEW);
        sessionRepository.save(ctx.session);

        // Create event
        InvestmentSessionEventEntity event = InvestmentSessionEventEntity.builder()
                .sessionId(ctx.sessionId)
                .eventType(InvestmentWorkflowConstant.EVENT_SESSION_REVIEWED)
                .payload("{\"totalInvested\": " + totalInvested + ", \"totalResidual\": " + totalResidual + "}")
                .build();
        eventRepository.save(event);
    }

    private ApiResponse<ReviewSessionResponse> createResponse(Context ctx) {
        ReviewSessionResponse response = ReviewSessionResponse.builder()
                .sessionId(ctx.sessionId)
                .status(ctx.session.getStatus())
                .totalInvested(ctx.totalInvested)
                .totalResidual(ctx.totalResidual)
                .updatedAt(ctx.session.getUpdatedAt())
                .build();

        return ApiResponse.<ReviewSessionResponse>builder()
                .data(response)
                .message(InvestmentWorkflowConstant.REVIEW_SESSION_INFO,
                        messageService.get(InvestmentWorkflowConstant.REVIEW_SESSION_INFO),
                        InvestmentWorkflowConstant.SERVICE_NAME)
                .build();
    }

    private String getCurrentUserId() {
        return securityUtils.getCurrentUser().getUserId();
    }

    private static class Context {
        Long sessionId;
        InvestmentSessionEntity session;
        BigDecimal totalInvested;
        BigDecimal totalResidual;
    }
}

