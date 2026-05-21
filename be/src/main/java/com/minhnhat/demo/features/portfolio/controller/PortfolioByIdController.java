package com.minhnhat.demo.features.portfolio.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.portfolio.dto.response.PortfolioSummaryResponse;
import com.minhnhat.demo.features.portfolio.service.GetPortfolioByIdService;
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

    private final GetPortfolioByIdService getPortfolioByIdService;

    @GetMapping("/{portfolioId}")
    public ApiResponse<PortfolioSummaryResponse> getPortfolio(@PathVariable Long portfolioId) {
        log.info("Fetching portfolio: {}", portfolioId);

        PortfolioSummaryResponse portfolio = getPortfolioByIdService.execute(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        return ApiResponse.<PortfolioSummaryResponse>builder()
                .data(portfolio)
                .message("SUCCESS", "Portfolio retrieved", "PortfolioService")
                .build();
    }
}

