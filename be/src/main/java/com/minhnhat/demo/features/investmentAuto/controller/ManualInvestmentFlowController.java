package com.minhnhat.demo.features.investmentAuto.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investment.dto.response.CreateInvestmentSessionResponse;
import com.minhnhat.demo.features.investment.service.CreateInvestmentSessionService;
import com.minhnhat.demo.features.investment.dto.request.CreateInvestmentSessionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/investment/manual")
@RequiredArgsConstructor
public class ManualInvestmentFlowController {

    private final CreateInvestmentSessionService createInvestmentSessionService;

    @PostMapping
    public ApiResponse<CreateInvestmentSessionResponse> createSession(@RequestBody CreateInvestmentSessionRequest request) {
        ApiResponse<CreateInvestmentSessionResponse> response = createInvestmentSessionService.execute(request);
        return ApiResponse.<CreateInvestmentSessionResponse>builder()
                .data(response.getData())
                .message("SUCCESS", "Manual investment session created", "ManualInvestmentService")
                .build();
    }
}


