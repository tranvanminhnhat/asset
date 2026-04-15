package com.minhnhat.demo.features.investmentRecommendations.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.investmentRecommendations.service.InvestmentRecommendationsService;
import com.minhnhat.demo.features.investmentRecommendations.constant.InvestmentRecommendationsConstant;
import com.minhnhat.demo.features.investmentRecommendations.dto.request.InvestmentRecommendationsRequest;
import com.minhnhat.demo.features.investmentRecommendations.dto.response.InvestmentRecommendationsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/investment-amount")
@RequiredArgsConstructor
public class InvestmentRecommendationsController {

    private final InvestmentRecommendationsService service;

    private final MessageService messageService;

    private static final String SERVICE_NAME = "InvestmentRecommendationsService";

    

    @PostMapping
    public ApiResponse<InvestmentRecommendationsResponse> allocate(@RequestBody InvestmentRecommendationsRequest request) {
        return ApiResponse.<InvestmentRecommendationsResponse>builder()
                .data(service.allocate(request))
                .message(InvestmentRecommendationsConstant.ALLOCATE_INFO_001, messageService.get(InvestmentRecommendationsConstant.ALLOCATE_INFO_001), SERVICE_NAME)
                .build();
    }
}





