package com.minhnhat.demo.features.investmentAmount.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investmentAmount.dto.response.InvestmentAmountStrategyResponse;
import com.minhnhat.demo.features.investmentAmount.service.GetAllAllocationStrategyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/investment-amount")
@RequiredArgsConstructor
public class GetAllAllocationStrategyController {

    private final GetAllAllocationStrategyService getAllAllocationStrategyService;

    @GetMapping("/allocation-strategies")
    public ApiResponse<List<InvestmentAmountStrategyResponse>> getAllAllocationStrategies() {
        return getAllAllocationStrategyService.execute();
    }
}

