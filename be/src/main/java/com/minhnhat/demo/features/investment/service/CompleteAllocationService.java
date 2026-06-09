package com.minhnhat.demo.features.investment.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.*;
import com.minhnhat.demo.common.db.repository.*;
import com.minhnhat.demo.common.db.repository.InvestmentSessionEventRepository;
import com.minhnhat.demo.common.db.repository.InvestmentSessionRepository;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.response.CompleteAllocationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompleteAllocationService {

    private final InvestmentSessionRepository sessionRepository;
    private final InvestmentSessionAllocationRepository allocationRepository;
    private final InvestmentSessionEventRepository eventRepository;
    private final MessageService messageService;
    private final SecurityUtils securityUtils;

    public ApiResponse<CompleteAllocationResponse> execute(Long sessionId, Long allocationId) {
        Context ctx = new Context();
        ctx.sessionId = sessionId;
        ctx.allocationId = allocationId;
        this.validateRequest(ctx);
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        String userId = getCurrentUserId();
        ctx.session = sessionRepository.findByIdAndUserId(ctx.sessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Investment session not found"));

        ctx.allocation = allocationRepository.findByIdAndSessionId(ctx.allocationId, ctx.sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found"));

        // Check if allocation is LOCKED
        if (InvestmentWorkflowConstant.ALLOCATION_LOCKED.equals(ctx.allocation.getStatus())) {
            throw new IllegalArgumentException("Locked allocations cannot be completed");
        }

        // Check if allocation is already completed
        if (InvestmentWorkflowConstant.ALLOCATION_COMPLETED.equals(ctx.allocation.getStatus())) {
            throw new IllegalArgumentException("Allocation is already completed");
        }
    }

    private void loadData(Context ctx) {
        // Data already loaded in validateRequest
    }

    private void processBusiness(Context ctx) {
        // Mark allocation as COMPLETED
        ctx.allocation.setStatus(InvestmentWorkflowConstant.ALLOCATION_COMPLETED);
        allocationRepository.save(ctx.allocation);

        // Create event
        InvestmentSessionEventEntity event = InvestmentSessionEventEntity.builder()
                .sessionId(ctx.sessionId)
                .eventType(InvestmentWorkflowConstant.EVENT_ALLOCATION_COMPLETED)
                .payload("{\"allocationId\": " + ctx.allocationId + "}")
                .build();
        eventRepository.save(event);
    }

    private ApiResponse<CompleteAllocationResponse> createResponse(Context ctx) {
        CompleteAllocationResponse response = CompleteAllocationResponse.builder()
                .allocationId(ctx.allocation.getId())
                .assetType(ctx.allocation.getAssetType())
                .status(ctx.allocation.getStatus())
                .investedAmount(ctx.allocation.getInvestedAmount())
                .residualAmount(ctx.allocation.getResidualAmount())
                .updatedAt(ctx.allocation.getUpdatedAt())
                .build();

        return ApiResponse.<CompleteAllocationResponse>builder()
                .data(response)
                .message(InvestmentWorkflowConstant.COMPLETE_ALLOCATION_INFO,
                        messageService.get(InvestmentWorkflowConstant.COMPLETE_ALLOCATION_INFO),
                        InvestmentWorkflowConstant.SERVICE_NAME)
                .build();
    }

    private String getCurrentUserId() {
        return securityUtils.getCurrentUser().getUserId();  // ← Lấy userId chính xác
    }

    private static class Context {
        Long sessionId;
        Long allocationId;
        InvestmentSessionEntity session;
        InvestmentSessionAllocationEntity allocation;
    }
}



