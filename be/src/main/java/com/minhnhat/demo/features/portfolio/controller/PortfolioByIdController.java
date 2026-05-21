package com.minhnhat.demo.features.portfolio.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.portfolio.dto.response.PortfolioSummaryResponse;
import com.minhnhat.demo.common.db.entity.PortfolioEntity;
import com.minhnhat.demo.features.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
@Slf4j
public class PortfolioByIdController {

    private final PortfolioService portfolioService;

    @GetMapping("/{portfolioId}")
    public ApiResponse<PortfolioSummaryResponse> getPortfolio(@PathVariable Long portfolioId) {
        log.info("Fetching portfolio: {}", portfolioId);

        PortfolioEntity portfolio = portfolioService.getPortfolioById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        return ApiResponse.<PortfolioSummaryResponse>builder()
                .data(mapToSummaryResponse(portfolio))
                .message("SUCCESS", "Portfolio retrieved", "PortfolioService")
                .build();
    }

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

