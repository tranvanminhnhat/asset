package com.minhnhat.demo.features.dashboard.service;

import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;
import com.minhnhat.demo.features.investmentComplete.repository.InvestmentPlanAllocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dashboard Service Implementation
 * Handles aggregated portfolio data calculation with financial precision
 * 
 * Key Features:
 * - One-query optimization (no N+1 problem)
 * - BigDecimal for all financial calculations
 * - Proper rounding (HALF_UP)
 * - Handles division by zero
 * - Null-safe operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService implements IDashboardService {

    private final InvestmentPlanAllocationRepository allocationRepository;
    private final SecurityUtils securityUtils;

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int SCALE_4 = 4;
    private static final int SCALE_2 = 2;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        // Step 1: Validate & Load current user
        CurrentUserInfo currentUser = securityUtils.getCurrentUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            log.warn("getCurrentUser: Current user not found");
            return createEmptyDashboard();
        }

        // Step 2: Get userId and fetch data
        String userId = currentUser.getUserId();
        return getDashboardByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardByUserId(String userId) {
        log.info("getDashboardByUserId: Fetching dashboard data for userId: {}", userId);

        // Step 1: Validate userId
        if (userId == null || userId.isBlank()) {
            log.warn("getDashboardByUserId: userId is null or blank");
            return createEmptyDashboard();
        }

        // Step 2: Load data - ONE optimized query
        List<Map<String, Object>> dashboardData = loadData(userId);

        // Step 3: Execute business logic
        return createResponse(dashboardData);
    }

    /**
     * Load data using optimized query (no N+1 problem)
     * Joins investment_plan_allocations with asset_prices and groups by asset_type
     */
    private List<Map<String, Object>> loadData(String userId) {
        log.debug("loadData: Executing optimized dashboard query for userId: {}", userId);
        List<Map<String, Object>> data = allocationRepository.findDashboardDataByUserId(userId);
        log.debug("loadData: Found {} asset types", data.size());
        return data;
    }

    /**
     * Build response with calculated financial metrics
     * 
     * Calculation Logic:
     * For each asset:
     *   - quantity = totalQuantity from DB
     *   - investedCost = totalAmount from DB
     *   - currentValue = quantity * currentPrice
     *   - profit = currentValue - investedCost
     *   - profitRate = profit / investedCost (handles division by zero)
     * 
     * For summary:
     *   - totalInvestedCost = SUM(investedCost)
     *   - totalCurrentValue = SUM(currentValue)
     *   - totalProfit = totalCurrentValue - totalInvestedCost
     *   - totalProfitRate = totalProfit / totalInvestedCost
     */
    private DashboardResponse createResponse(List<Map<String, Object>> dashboardData) {
        if (dashboardData == null || dashboardData.isEmpty()) {
            log.info("createResponse: No dashboard data found, returning empty dashboard");
            return createEmptyDashboard();
        }

        // Build portfolio items and aggregate summary
        List<DashboardResponse.PortfolioItem> portfolios = new ArrayList<>();
        BigDecimal totalInvestedCost = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;

        for (Map<String, Object> row : dashboardData) {
            try {
                // Parse data from aggregated query result
                String assetType = (String) row.get("assetType");
                BigDecimal quantity = convertToBigDecimal(row.get("totalQuantity"));
                BigDecimal investedCost = convertToBigDecimal(row.get("totalAmount"));
                BigDecimal currentPrice = convertToBigDecimal(row.get("currentPrice"));

                // Business logic: Calculate metrics for this asset
                DashboardResponse.PortfolioItem item = calculatePortfolioItem(
                    assetType,
                    quantity,
                    investedCost,
                    currentPrice
                );

                portfolios.add(item);

                // Accumulate summary totals
                totalInvestedCost = totalInvestedCost.add(investedCost);
                totalCurrentValue = totalCurrentValue.add(item.getCurrentValue());

            } catch (Exception e) {
                log.error("createResponse: Error processing asset data: {}", row, e);
                // Skip this asset and continue
            }
        }

        // Build summary with calculated profit metrics
        DashboardResponse.Summary summary = calculateSummary(
            totalInvestedCost,
            totalCurrentValue
        );

        log.info("createResponse: Dashboard built successfully with {} assets, " +
                "totalInvestedCost={}, totalCurrentValue={}, totalProfit={}",
                portfolios.size(),
                totalInvestedCost,
                totalCurrentValue,
                summary.getTotalProfit());

        return DashboardResponse.builder()
                .summary(summary)
                .portfolios(portfolios)
                .build();
    }

    /**
     * Calculate metrics for a single asset
     * 
     * @param assetType Asset type name
     * @param quantity Total quantity held
     * @param investedCost Total amount invested (at purchase)
     * @param currentPrice Current market price per unit
     * @return PortfolioItem with calculated metrics
     */
    private DashboardResponse.PortfolioItem calculatePortfolioItem(
            String assetType,
            BigDecimal quantity,
            BigDecimal investedCost,
            BigDecimal currentPrice) {

        // Current value = quantity * currentPrice
        BigDecimal currentValue = quantity.multiply(currentPrice)
                .setScale(SCALE_2, RoundingMode.HALF_UP);

        // Profit = currentValue - investedCost
        BigDecimal profit = currentValue.subtract(investedCost)
                .setScale(SCALE_2, RoundingMode.HALF_UP);

        // Profit rate = profit / investedCost (with zero-handling)
        BigDecimal profitRate = calculateProfitRate(profit, investedCost);

        return DashboardResponse.PortfolioItem.builder()
                .assetType(assetType)
                .quantity(quantity.setScale(SCALE_2, RoundingMode.HALF_UP))
                .investedCost(investedCost.setScale(SCALE_2, RoundingMode.HALF_UP))
                .currentValue(currentValue)
                .profit(profit)
                .profitRate(profitRate)
                .build();
    }

    /**
     * Calculate summary metrics across all assets
     * 
     * @param totalInvestedCost Sum of all invested costs
     * @param totalCurrentValue Sum of all current values
     * @return Summary with total metrics
     */
    private DashboardResponse.Summary calculateSummary(
            BigDecimal totalInvestedCost,
            BigDecimal totalCurrentValue) {

        // Total profit = totalCurrentValue - totalInvestedCost
        BigDecimal totalProfit = totalCurrentValue.subtract(totalInvestedCost)
                .setScale(SCALE_2, RoundingMode.HALF_UP);

        // Total profit rate = totalProfit / totalInvestedCost
        BigDecimal totalProfitRate = calculateProfitRate(totalProfit, totalInvestedCost);

        return DashboardResponse.Summary.builder()
                .totalInvestedCost(totalInvestedCost.setScale(SCALE_2, RoundingMode.HALF_UP))
                .totalCurrentValue(totalCurrentValue.setScale(SCALE_2, RoundingMode.HALF_UP))
                .totalProfit(totalProfit)
                .totalProfitRate(totalProfitRate)
                .build();
    }

    /**
     * Calculate profit rate with zero-division handling
     * 
     * @param profit Total profit amount
     * @param investedCost Total invested cost
     * @return Profit rate (0 if investedCost is zero)
     */
    private BigDecimal calculateProfitRate(BigDecimal profit, BigDecimal investedCost) {
        if (investedCost == null || investedCost.compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("calculateProfitRate: investedCost is zero or null, returning 0");
            return BigDecimal.ZERO;
        }

        // profitRate = (profit / investedCost) * 100
        return profit
                .divide(investedCost, SCALE_4, RoundingMode.HALF_UP)
                .multiply(HUNDRED)
                .setScale(SCALE_2, RoundingMode.HALF_UP);
    }

    /**
     * Convert raw database value to BigDecimal
     * Handles null, String, Double, BigDecimal types
     */
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        return switch (value) {
            case BigDecimal bd -> bd;
            case Double d -> BigDecimal.valueOf(d);
            case Long l -> BigDecimal.valueOf(l);
            case Integer i -> BigDecimal.valueOf(i);
            case String s -> {
                try {
                    yield new BigDecimal(s);
                } catch (NumberFormatException e) {
                    log.warn("convertToBigDecimal: Failed to parse string value: {}", value);
                    yield BigDecimal.ZERO;
                }
            }
            default -> {
                log.warn("convertToBigDecimal: Unknown type: {}", value.getClass());
                yield BigDecimal.ZERO;
            }
        };
    }

    /**
     * Create empty dashboard response (no data)
     */
    private DashboardResponse createEmptyDashboard() {
        log.info("createEmptyDashboard: Creating empty dashboard response");

        DashboardResponse.Summary emptySummary = DashboardResponse.Summary.builder()
                .totalInvestedCost(BigDecimal.ZERO)
                .totalCurrentValue(BigDecimal.ZERO)
                .totalProfit(BigDecimal.ZERO)
                .totalProfitRate(BigDecimal.ZERO)
                .build();

        return DashboardResponse.builder()
                .summary(emptySummary)
                .portfolios(new ArrayList<>())
                .build();
    }
}



