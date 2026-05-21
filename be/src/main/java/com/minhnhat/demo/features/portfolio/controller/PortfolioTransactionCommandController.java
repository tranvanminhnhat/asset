package com.minhnhat.demo.features.portfolio.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.portfolio.dto.request.AddTransactionRequest;
import com.minhnhat.demo.features.portfolio.dto.response.PortfolioTransactionResponse;
import com.minhnhat.demo.features.portfolio.service.AddPortfolioTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
@Slf4j
public class PortfolioTransactionCommandController {

    private final AddPortfolioTransactionService addPortfolioTransactionService;
    private final SecurityUtils securityUtils;

    @PostMapping("/transactions")
    public ApiResponse<PortfolioTransactionResponse> addTransaction(@RequestBody AddTransactionRequest request) {
        log.info("Adding transaction for asset: {}", request.getAssetType());

        CurrentUserInfo user = securityUtils.getCurrentUser();
        PortfolioTransactionResponse transaction = addPortfolioTransactionService.execute(
                user.getUserId(),
                request.getTransactionType(),
                request.getAssetType(),
                request.getQuantity(),
                request.getPrice(),
                request.getTotalAmount(),
                request.getFee(),
                request.getTax(),
                request.getReferenceId(),
                request.getNote()
        );

        return ApiResponse.<PortfolioTransactionResponse>builder()
                .data(transaction)
                .message("SUCCESS", "Transaction recorded", "PortfolioService")
                .build();
    }
}

