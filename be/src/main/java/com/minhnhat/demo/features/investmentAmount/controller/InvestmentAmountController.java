package com.minhnhat.demo.features.investmentAmount.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;
import com.minhnhat.demo.features.investmentAmount.service.InvestmentAmountService;
import com.minhnhat.demo.features.investmentAmount.constant.InvestmentAmountConstant;
import com.minhnhat.demo.features.investmentAmount.dto.request.InvestmentAmountRequest;
import com.minhnhat.demo.features.investmentAmount.dto.response.InvestmentAmountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/investment-amount")
@RequiredArgsConstructor
public class InvestmentAmountController {

    private final InvestmentAmountService service;

    private final MessageService messageService;

    private static final String SERVICE_NAME = "InvestmentAmountService";

    

    @PostMapping
    public ApiResponse<InvestmentAmountResponse> allocate(@RequestBody InvestmentAmountRequest request) {
        return ApiResponse.<InvestmentAmountResponse>builder()
                .data(service.allocate(request))
                .message(InvestmentAmountConstant.ALLOCATE_INFO_001, messageService.get(InvestmentAmountConstant.ALLOCATE_INFO_001), SERVICE_NAME)
                .build();
    }
}




