package com.minhnhat.demo.features.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Dashboard Response DTO
 * 
 * Contains aggregated portfolio data:
 * - Summary: total metrics across all assets
 * - Portfolios: individual asset allocation details
 * 
 * All financial values use BigDecimal for precision
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    /**
     * Summary metrics across all assets
     */
    private Summary summary;

    /**
     * List of portfolio items (one per asset type)
     */
    private java.util.List<PortfolioItem> portfolios;

    /**
     * Summary metrics
     * 
     * Calculations:
     * - totalInvestedCost: SUM(investedCost) across all assets
     * - totalCurrentValue: SUM(currentValue) across all assets
     * - totalProfit: totalCurrentValue - totalInvestedCost
     * - totalProfitRate: (totalProfit / totalInvestedCost) * 100 (%)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        /**
         * Total amount invested across all assets (at purchase price)
         * Scale: 2 decimal places (e.g., 1000.00)
         */
        private BigDecimal totalInvestedCost;

        /**
         * Total current value of all assets (at current market price)
         * = SUM(quantity * currentPrice) for all assets
         * Scale: 2 decimal places
         */
        private BigDecimal totalCurrentValue;

        /**
         * Total profit/loss across all assets
         * = totalCurrentValue - totalInvestedCost
         * Scale: 2 decimal places
         */
        private BigDecimal totalProfit;

        /**
         * Total profit rate as percentage
         * = (totalProfit / totalInvestedCost) * 100
         * Scale: 2 decimal places (e.g., 5.25 = 5.25%)
         */
        private BigDecimal totalProfitRate;
    }

    /**
     * Individual asset portfolio item
     * 
     * Calculations:
     * - currentValue = quantity * currentPrice (from asset_prices)
     * - profit = currentValue - investedCost
     * - profitRate = (profit / investedCost) * 100 (%)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortfolioItem {
        /**
         * Asset type (e.g., "STOCK", "GOLD", "ETF", "EMERGENCY")
         */
        private String assetType;

        /**
         * Total quantity held for this asset type
         * Aggregated from all investment plans
         * Scale: 2 decimal places
         */
        private BigDecimal quantity;

        /**
         * Total amount invested in this asset (at purchase price)
         * Aggregated from all investment plans
         * Scale: 2 decimal places
         */
        private BigDecimal investedCost;

        /**
         * Current value of this asset (at current market price)
         * = quantity * currentPrice
         * Scale: 2 decimal places
         */
        private BigDecimal currentValue;

        /**
         * Profit/loss for this asset
         * = currentValue - investedCost
         * Scale: 2 decimal places
         */
        private BigDecimal profit;

        /**
         * Profit rate for this asset as percentage
         * = (profit / investedCost) * 100
         * Scale: 2 decimal places (e.g., 10.50 = 10.50%)
         */
        private BigDecimal profitRate;
    }
}




