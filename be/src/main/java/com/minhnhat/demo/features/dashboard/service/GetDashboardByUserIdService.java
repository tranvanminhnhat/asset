package com.minhnhat.demo.features.dashboard.service;

import com.minhnhat.demo.common.db.repository.InvestmentPlanAllocationRepository;
import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetDashboardByUserIdService {

    private final InvestmentPlanAllocationRepository allocationRepository;

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int SCALE_4 = 4;
    private static final int SCALE_2 = 2;

    @Transactional(readOnly = true)
    public DashboardResponse execute(String userId) {
        log.info("getDashboardByUserId: Fetching dashboard data for userId: {}", userId);
        if (userId == null || userId.isBlank()) {
            log.warn("getDashboardByUserId: userId is null or blank");
            return createEmptyDashboard();
        }
        List<Map<String, Object>> dashboardData = allocationRepository.findDashboardDataByUserId(userId);
        return createResponse(dashboardData);
    }

    private DashboardResponse createResponse(List<Map<String, Object>> dashboardData) {
        if (dashboardData == null || dashboardData.isEmpty()) {
            log.info("createResponse: No dashboard data found, returning empty dashboard");
            return createEmptyDashboard();
        }

        List<DashboardResponse.PortfolioItem> portfolios = new ArrayList<>();
        BigDecimal totalInvestedCost = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;

        for (Map<String, Object> row : dashboardData) {
            try {
                String assetType = (String) row.get("assetType");
                BigDecimal quantity = convertToBigDecimal(row.get("totalQuantity"));
                BigDecimal investedCost = convertToBigDecimal(row.get("totalAmount"));
                BigDecimal currentPrice = convertToBigDecimal(row.get("currentPrice"));
                DashboardResponse.PortfolioItem item = calculatePortfolioItem(assetType, quantity, investedCost, currentPrice);
                portfolios.add(item);
                totalInvestedCost = totalInvestedCost.add(investedCost);
                totalCurrentValue = totalCurrentValue.add(item.getCurrentValue());
            } catch (Exception e) {
                log.error("createResponse: Error processing asset data: {}", row, e);
            }
        }

        DashboardResponse.Summary summary = calculateSummary(totalInvestedCost, totalCurrentValue);
        log.info("createResponse: Dashboard built successfully with {} assets, totalInvestedCost={}, totalCurrentValue={}, totalProfit={}", portfolios.size(), totalInvestedCost, totalCurrentValue, summary.getTotalProfit());
        return DashboardResponse.builder().summary(summary).portfolios(portfolios).build();
    }

    private DashboardResponse.PortfolioItem calculatePortfolioItem(String assetType, BigDecimal quantity, BigDecimal investedCost, BigDecimal currentPrice) {
        BigDecimal currentValue = quantity.multiply(currentPrice).setScale(SCALE_2, RoundingMode.HALF_UP);
        BigDecimal profit = currentValue.subtract(investedCost).setScale(SCALE_2, RoundingMode.HALF_UP);
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

    private DashboardResponse.Summary calculateSummary(BigDecimal totalInvestedCost, BigDecimal totalCurrentValue) {
        BigDecimal totalProfit = totalCurrentValue.subtract(totalInvestedCost).setScale(SCALE_2, RoundingMode.HALF_UP);
        BigDecimal totalProfitRate = calculateProfitRate(totalProfit, totalInvestedCost);
        return DashboardResponse.Summary.builder()
                .totalInvestedCost(totalInvestedCost.setScale(SCALE_2, RoundingMode.HALF_UP))
                .totalCurrentValue(totalCurrentValue.setScale(SCALE_2, RoundingMode.HALF_UP))
                .totalProfit(totalProfit)
                .totalProfitRate(totalProfitRate)
                .build();
    }

    private BigDecimal calculateProfitRate(BigDecimal profit, BigDecimal investedCost) {
        if (investedCost == null || investedCost.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return profit.divide(investedCost, SCALE_4, RoundingMode.HALF_UP).multiply(HUNDRED).setScale(SCALE_2, RoundingMode.HALF_UP);
    }

    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        return switch (value) {
            case BigDecimal bd -> bd;
            case Double d -> BigDecimal.valueOf(d);
            case Long l -> BigDecimal.valueOf(l);
            case Integer i -> BigDecimal.valueOf(i);
            case String s -> {
                try {
                    yield new BigDecimal(s);
                } catch (NumberFormatException e) {
                    yield BigDecimal.ZERO;
                }
            }
            default -> BigDecimal.ZERO;
        };
    }

    private DashboardResponse createEmptyDashboard() {
        DashboardResponse.Summary emptySummary = DashboardResponse.Summary.builder()
                .totalInvestedCost(BigDecimal.ZERO)
                .totalCurrentValue(BigDecimal.ZERO)
                .totalProfit(BigDecimal.ZERO)
                .totalProfitRate(BigDecimal.ZERO)
                .build();
        return DashboardResponse.builder().summary(emptySummary).portfolios(new ArrayList<>()).build();
    }
}

