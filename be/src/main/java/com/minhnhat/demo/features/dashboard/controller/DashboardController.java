package com.minhnhat.demo.features.dashboard.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;
import com.minhnhat.demo.features.dashboard.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.minhnhat.demo.features.dashboard.constant.DashboardConstant.DASHBOARD_INFO_001;
import static com.minhnhat.demo.features.dashboard.constant.DashboardConstant.SERVICE_NAME;

/**
 * Dashboard Controller
 * 
 * Provides endpoints to fetch aggregated portfolio dashboard data
 * All endpoints return financial metrics with BigDecimal precision
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;
    private final MessageService messageService;

    /**
     * Get dashboard for current authenticated user
     * 
     * Endpoint: GET /api/v1/dashboard
     * 
     * @return ApiResponse containing DashboardResponse with:
     *         - Summary: totalInvestedCost, totalCurrentValue, totalProfit, totalProfitRate
     *         - Portfolios: list of assets with quantity, investedCost, currentValue, profit, profitRate
     */
    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard() {
        DashboardResponse data = dashboardService.getDashboard();

        return ApiResponse.<DashboardResponse>builder()
                .data(data)
                .message(DASHBOARD_INFO_001, messageService.get(DASHBOARD_INFO_001), SERVICE_NAME)
                .build();
    }

    /**
     * Get dashboard for a specific user (admin feature)
     * 
     * Endpoint: GET /api/v1/dashboard/{userId}
     * 
     * @param userId The user ID to fetch dashboard for
     * @return ApiResponse containing DashboardResponse
     */
    @GetMapping("/{userId}")
    public ApiResponse<DashboardResponse> getDashboardByUserId(
            @PathVariable String userId) {
        DashboardResponse data = dashboardService.getDashboardByUserId(userId);

        return ApiResponse.<DashboardResponse>builder()
                .data(data)
                .message(DASHBOARD_INFO_001, messageService.get(DASHBOARD_INFO_001), SERVICE_NAME)
                .build();
    }
}




