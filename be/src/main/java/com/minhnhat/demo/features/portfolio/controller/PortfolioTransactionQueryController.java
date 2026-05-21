package com.minhnhat.demo.features.portfolio.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.portfolio.dto.response.PortfolioTransactionResponse;
import com.minhnhat.demo.common.db.entity.PortfolioTransactionEntity;
import com.minhnhat.demo.features.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
@Slf4j
public class PortfolioTransactionQueryController {

    private final PortfolioService portfolioService;
    private final SecurityUtils securityUtils;

    @GetMapping("/transactions")
    public ApiResponse<List<PortfolioTransactionResponse>> getTransactionHistory() {
        log.info("Fetching transaction history");

        CurrentUserInfo user = securityUtils.getCurrentUser();
        List<PortfolioTransactionEntity> transactions = portfolioService.getUserTransactionHistory(user.getUserId());

        List<PortfolioTransactionResponse> responses = transactions.stream()
                .map(this::mapToTransactionResponse)
                .toList();

        return ApiResponse.<List<PortfolioTransactionResponse>>builder()
                .data(responses)
                .message("SUCCESS", "Transactions retrieved", "PortfolioService")
                .build();
    }

    private PortfolioTransactionResponse mapToTransactionResponse(PortfolioTransactionEntity entity) {
        return PortfolioTransactionResponse.builder()
                .transactionId(entity.getId())
                .transactionType(entity.getTransactionType())
                .assetType(entity.getAssetType())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .totalAmount(entity.getTotalAmount())
                .fee(entity.getFee())
                .tax(entity.getTax())
                .referenceId(entity.getReferenceId())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

