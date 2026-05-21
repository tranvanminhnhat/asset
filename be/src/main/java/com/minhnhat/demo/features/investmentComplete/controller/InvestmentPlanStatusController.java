package com.minhnhat.demo.features.investmentComplete.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.investmentComplete.dto.response.InvestmentPlanResponse;
import com.minhnhat.demo.common.db.entity.InvestmentPlanEntity;
import com.minhnhat.demo.features.investmentComplete.service.InvestmentPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/investment-plans")
@RequiredArgsConstructor
public class InvestmentPlanStatusController {

    private final InvestmentPlanService investmentPlanService;
    private final SecurityUtils securityUtils;

    @GetMapping("/status/{status}")
    public ApiResponse<List<InvestmentPlanResponse>> getPlansByStatus() {
        CurrentUserInfo user = securityUtils.getCurrentUser();
        List<InvestmentPlanEntity> plans = investmentPlanService.getPlansByStatus(user.getUserId());

        List<InvestmentPlanResponse> responses = plans.stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.<List<InvestmentPlanResponse>>builder()
                .data(responses)
                .message("SUCCESS", "Plans retrieved", "InvestmentPlanService")
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

