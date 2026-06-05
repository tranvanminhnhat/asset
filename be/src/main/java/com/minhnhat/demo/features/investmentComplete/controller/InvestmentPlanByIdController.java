package com.minhnhat.demo.features.investmentComplete.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investmentComplete.dto.response.InvestmentPlanResponse;
import com.minhnhat.demo.features.investmentComplete.service.GetInvestmentPlanWithAllocationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/investment-plans")
@RequiredArgsConstructor
@Slf4j
public class InvestmentPlanByIdController {

    private final GetInvestmentPlanWithAllocationsService getInvestmentPlanWithAllocationsService;

    @GetMapping("/{planId}")
    public ApiResponse<InvestmentPlanResponse> getPlan(@PathVariable Long planId) {
        log.info("Fetching investment plan: {}", planId);

        InvestmentPlanResponse plan = getInvestmentPlanWithAllocationsService.execute(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        return ApiResponse.<InvestmentPlanResponse>builder()
                .data(plan)
                .message("SUCCESS", "Plan retrieved", "InvestmentPlanService")
                .build();
    }
}

