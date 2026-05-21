package com.minhnhat.demo.features.investmentComplete.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investmentComplete.dto.response.InvestmentPlanResponse;
import com.minhnhat.demo.common.db.entity.InvestmentPlanEntity;
import com.minhnhat.demo.features.investmentComplete.service.InvestmentPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/investment-plans")
@RequiredArgsConstructor
@Slf4j
public class InvestmentPlanByIdController {

    private final InvestmentPlanService investmentPlanService;

    @GetMapping("/{planId}")
    public ApiResponse<InvestmentPlanResponse> getPlan(@PathVariable Long planId) {
        log.info("Fetching investment plan: {}", planId);

        InvestmentPlanEntity plan = investmentPlanService.getPlanWithAllocations(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        return ApiResponse.<InvestmentPlanResponse>builder()
                .data(mapToResponse(plan))
                .message("SUCCESS", "Plan retrieved", "InvestmentPlanService")
                .build();
    }

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

