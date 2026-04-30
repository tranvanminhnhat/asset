package com.minhnhat.demo.features.dashboard.service;

import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;

/**
 * DEPRECATED: Use DashboardService instead
 * 
 * This service should be removed as it uses incorrect data source (PortfolioEntity)
 * and has calculation errors.
 * 
 * The new DashboardService correctly fetches data from:
 * - investment_plans
 * - investment_plan_allocations
 * - asset_prices
 * 
 * And performs correct financial calculations with BigDecimal precision.
 */
@Deprecated(since = "2026-04-27", forRemoval = true)
public class GetDashboardService {

    /**
     * This service is deprecated.
     * Use DashboardService (IDashboardService) instead.
     * 
     * The new service:
     * - Fetches data from correct tables (investment_plans, investment_plan_allocations, asset_prices)
     * - Uses optimized native query (no N+1 problem)
     * - Calculates currentValue = quantity × currentPrice from asset_prices
     * - Properly calculates profit/loss and profitRate
     * - Uses BigDecimal for all financial calculations
     * - Includes comprehensive error handling
     */
    public DashboardResponse getDashboard() {
        throw new UnsupportedOperationException(
            "GetDashboardService is deprecated. Use DashboardService (implement IDashboardService) instead. " +
            "The new service fetches data from investment_plans, investment_plan_allocations, and asset_prices tables."
        );
    }
}


