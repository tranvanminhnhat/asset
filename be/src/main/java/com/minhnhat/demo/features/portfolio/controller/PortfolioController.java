package com.minhnhat.demo.features.portfolio.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.portfolio.dto.response.PortfolioSummaryResponse;
import com.minhnhat.demo.features.portfolio.service.GetUserPortfoliosService;
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

    private final GetUserPortfoliosService getUserPortfoliosService;
    private final SecurityUtils securityUtils;

    /**
     * Get all user portfolios
     */
    @GetMapping
    public ApiResponse<List<PortfolioSummaryResponse>> getUserPortfolios() {
        log.info("Fetching user portfolios");

        // Validate & Load
        CurrentUserInfo user = securityUtils.getCurrentUser();
        List<PortfolioSummaryResponse> responses = getUserPortfoliosService.execute(user.getUserId());

        return ApiResponse.<List<PortfolioSummaryResponse>>builder()
                .data(responses)
                .message("SUCCESS", "Portfolios retrieved", "PortfolioService")
                .build();
    }

}

