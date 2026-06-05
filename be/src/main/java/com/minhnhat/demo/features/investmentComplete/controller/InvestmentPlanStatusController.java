package com.minhnhat.demo.features.investmentComplete.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.investmentComplete.dto.response.InvestmentPlanResponse;
import com.minhnhat.demo.features.investmentComplete.service.GetInvestmentPlansByStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/investment-plans")
@RequiredArgsConstructor
public class InvestmentPlanStatusController {

    private final GetInvestmentPlansByStatusService getInvestmentPlansByStatusService;
    private final SecurityUtils securityUtils;

    @GetMapping("/status/{status}")
    public ApiResponse<List<InvestmentPlanResponse>> getPlansByStatus() {
        CurrentUserInfo user = securityUtils.getCurrentUser();
        List<InvestmentPlanResponse> responses = getInvestmentPlansByStatusService.execute(user.getUserId());

        return ApiResponse.<List<InvestmentPlanResponse>>builder()
                .data(responses)
                .message("SUCCESS", "Plans retrieved", "InvestmentPlanService")
                .build();
    }
}

