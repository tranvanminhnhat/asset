package com.minhnhat.demo.features.investmentComplete.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.investmentComplete.dto.response.InvestmentPlanResponse;
import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanEntity;
import com.minhnhat.demo.features.investmentComplete.service.InvestmentPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Investment Plan Controller - Example implementation
 * Demonstrates investment plan management with allocations
 */
@RestController
@RequestMapping("/api/v1/investment-plans")
@RequiredArgsConstructor
@Slf4j
public class InvestmentPlanController {

    private final InvestmentPlanService investmentPlanService;
    private final SecurityUtils securityUtils;

    /**
     * Get all investment plans for user
     */
    @GetMapping
    public ApiResponse<List<InvestmentPlanResponse>> getUserPlans() {
        log.info("Fetching investment plans for user");

        // Validate & Load
        CurrentUserInfo user = securityUtils.getCurrentUser();
        List<InvestmentPlanEntity> plans = investmentPlanService.getUserPlans(user.getUserId());

        // Build Response
        List<InvestmentPlanResponse> responses = plans.stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.<List<InvestmentPlanResponse>>builder()
                .data(responses)
                .message("SUCCESS", "Plans retrieved", "InvestmentPlanService")
                .build();
    }

    /**
     * Get investment plan by ID with allocations
     */
    @GetMapping("/{planId}")
    public ApiResponse<InvestmentPlanResponse> getPlan(
            @PathVariable Long planId) {
        log.info("Fetching investment plan: {}", planId);

        // Load Data
        InvestmentPlanEntity plan = investmentPlanService.getPlanWithAllocations(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        // Build Response
        InvestmentPlanResponse response = mapToResponse(plan);

        return ApiResponse.<InvestmentPlanResponse>builder()
                .data(response)
                .message("SUCCESS", "Plan retrieved", "InvestmentPlanService")
                .build();
    }

    /**
     * Get plans by status
     */
    @GetMapping("/status/{status}")
    public ApiResponse<List<InvestmentPlanResponse>> getPlansByStatus() {

        // Validate & Load
        CurrentUserInfo user = securityUtils.getCurrentUser();
        List<InvestmentPlanEntity> plans = investmentPlanService.getPlansByStatus(user.getUserId());

        // Build Response
        List<InvestmentPlanResponse> responses = plans.stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.<List<InvestmentPlanResponse>>builder()
                .data(responses)
                .message("SUCCESS", "Plans retrieved", "InvestmentPlanService")
                .build();
    }

    /**
     * Helper: Map entity to response
     */
    private InvestmentPlanResponse mapToResponse(InvestmentPlanEntity entity) {
        List<InvestmentPlanResponse.AllocationDetail> allocations = entity.getAllocations().stream()
                .map(a -> InvestmentPlanResponse.AllocationDetail.builder()
                        .allocationId(a.getId())
                        .assetType(a.getAssetType())
                        .amount(a.getAmount())
                        .percentage(a.getPercentage())
                        .build())
                .toList();

        return InvestmentPlanResponse.builder()
                .planId(entity.getId())
                .userId(entity.getUserId())
                .totalAmount(entity.getTotalAmount())
                .allocations(allocations)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

