package com.minhnhat.demo.features.portfolio.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.portfolio.dto.request.AddTransactionRequest;
import com.minhnhat.demo.features.portfolio.dto.response.PortfolioSummaryResponse;
import com.minhnhat.demo.features.portfolio.dto.response.PortfolioTransactionResponse;
import com.minhnhat.demo.features.portfolio.entity.PortfolioEntity;
import com.minhnhat.demo.features.portfolio.entity.PortfolioTransactionEntity;
import com.minhnhat.demo.features.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Portfolio Controller - Transaction-based portfolio management
 */
@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
@Slf4j
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final SecurityUtils securityUtils;

    /**
     * Get all user portfolios
     */
    @GetMapping
    public ApiResponse<List<PortfolioSummaryResponse>> getUserPortfolios() {
        log.info("Fetching user portfolios");

        // Validate & Load
        CurrentUserInfo user = securityUtils.getCurrentUser();
        List<PortfolioEntity> portfolios = portfolioService.getUserPortfolios(user.getUserId());

        // Build Response
        List<PortfolioSummaryResponse> responses = portfolios.stream()
                .map(this::mapToSummaryResponse)
                .toList();

        return ApiResponse.<List<PortfolioSummaryResponse>>builder()
                .data(responses)
                .message("SUCCESS", "Portfolios retrieved", "PortfolioService")
                .build();
    }

    /**
     * Get portfolio by ID
     */
    @GetMapping("/{portfolioId}")
    public ApiResponse<PortfolioSummaryResponse> getPortfolio(
            @PathVariable Long portfolioId) {
        log.info("Fetching portfolio: {}", portfolioId);

        // Load Data
        PortfolioEntity portfolio = portfolioService.getPortfolioById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        // Build Response
        PortfolioSummaryResponse response = mapToSummaryResponse(portfolio);

        return ApiResponse.<PortfolioSummaryResponse>builder()
                .data(response)
                .message("SUCCESS", "Portfolio retrieved", "PortfolioService")
                .build();
    }

    /**
     * Add transaction to portfolio
     */
    @PostMapping("/transactions")
    public ApiResponse<PortfolioTransactionResponse> addTransaction(
            @RequestBody AddTransactionRequest request) {
        log.info("Adding transaction for asset: {}", request.getAssetType());

        // Validate
        CurrentUserInfo user = securityUtils.getCurrentUser();

        // Process & Persist
        PortfolioTransactionEntity transaction = portfolioService.addTransaction(
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

        // Build Response
        PortfolioTransactionResponse response = mapToTransactionResponse(transaction);

        return ApiResponse.<PortfolioTransactionResponse>builder()
                .data(response)
                .message("SUCCESS", "Transaction recorded", "PortfolioService")
                .build();
    }

    /**
     * Get transaction history for user
     */
    @GetMapping("/transactions")
    public ApiResponse<List<PortfolioTransactionResponse>> getTransactionHistory() {
        log.info("Fetching transaction history");

        // Validate & Load
        CurrentUserInfo user = securityUtils.getCurrentUser();
        List<PortfolioTransactionEntity> transactions = portfolioService.getUserTransactionHistory(user.getUserId());

        // Build Response
        List<PortfolioTransactionResponse> responses = transactions.stream()
                .map(this::mapToTransactionResponse)
                .toList();

        return ApiResponse.<List<PortfolioTransactionResponse>>builder()
                .data(responses)
                .message("SUCCESS", "Transactions retrieved", "PortfolioService")
                .build();
    }

    /**
     * Helper: Map entity to summary response
     */
    private PortfolioSummaryResponse mapToSummaryResponse(PortfolioEntity entity) {
        return PortfolioSummaryResponse.builder()
                .portfolioId(entity.getId())
                .assetType(entity.getAssetType())
                .quantity(entity.getQuantity())
                .totalInvested(entity.getTotalInvested())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Helper: Map entity to transaction response
     */
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

