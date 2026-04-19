package com.minhnhat.demo.features.investmentComplete.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.investmentComplete.dto.request.InvestmentCompleteRequest;
import com.minhnhat.demo.features.investmentComplete.dto.response.InvestmentCompleteResponse;
import com.minhnhat.demo.features.investmentComplete.service.InvestmentCompleteService;
import com.minhnhat.demo.features.investmentRecommendations.constant.InvestmentRecommendationsConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/investment-complete")
@RequiredArgsConstructor
public class InvestmentCompleteController {

    private final InvestmentCompleteService service;

    private final MessageService messageService;

    private static final String SERVICE_NAME = "InvestmentCompleteService";

    @PostMapping
    public ApiResponse<InvestmentCompleteResponse> create(@RequestBody InvestmentCompleteRequest request) {
        InvestmentCompleteResponse session = service.createSession(request);
        return ApiResponse.<InvestmentCompleteResponse>builder()
                .data(session)
                .message(InvestmentRecommendationsConstant.ALLOCATE_INFO_001, messageService.get(InvestmentRecommendationsConstant.ALLOCATE_INFO_001), SERVICE_NAME)
                .build();
    }
}
