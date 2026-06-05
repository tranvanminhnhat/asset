package com.minhnhat.demo.features.investmentComplete.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.investmentComplete.dto.response.InvestmentPlanResponse;
import com.minhnhat.demo.features.investmentComplete.service.GetUserInvestmentPlansService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/investment-plans")
@RequiredArgsConstructor
@Slf4j
public class InvestmentPlanController {

    private final GetUserInvestmentPlansService getUserInvestmentPlansService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ApiResponse<List<InvestmentPlanResponse>> getUserPlans() {
        log.info("Fetching investment plans for user");

        CurrentUserInfo user = securityUtils.getCurrentUser();
        List<InvestmentPlanResponse> responses = getUserInvestmentPlansService.execute(user.getUserId());

        return ApiResponse.<List<InvestmentPlanResponse>>builder()
                .data(responses)
                .message("SUCCESS", "Plans retrieved", "InvestmentPlanService")
                .build();
    }
}

