package com.minhnhat.demo.features.portfolio.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.portfolio.dto.response.PortfolioSummaryResponse;
import com.minhnhat.demo.common.db.entity.PortfolioEntity;
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

}

