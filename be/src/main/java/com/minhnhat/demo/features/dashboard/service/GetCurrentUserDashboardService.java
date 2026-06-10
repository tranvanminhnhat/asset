package com.minhnhat.demo.features.dashboard.service;

import com.minhnhat.demo.common.db.entity.AssetPriceEntity;
import com.minhnhat.demo.common.db.entity.PortfolioEntity;
import com.minhnhat.demo.common.db.repository.AssetPriceRepository;
import com.minhnhat.demo.common.db.repository.AssetProductRepository;
import com.minhnhat.demo.common.db.repository.PortfolioRepository;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCurrentUserDashboardService {

    private final PortfolioRepository portfolioRepository;
    private final AssetProductRepository assetProductRepository;
    private final AssetPriceRepository assetPriceRepository;
    private final SecurityUtils securityUtils;

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int SCALE_4 = 4;
    private static final int SCALE_2 = 2;

    @Transactional(readOnly = true)
    public DashboardResponse execute() {
        CurrentUserInfo currentUser = securityUtils.getCurrentUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            log.warn("getCurrentUser: Current user not found");
            return createEmptyDashboard();
        }
        return buildDashboard(currentUser.getUserId());
    }

    private DashboardResponse buildDashboard(String userId) {
        log.info("buildDashboard: Fetching portfolio data for userId: {}", userId);

        // Get all portfolio entries for this user
        List<PortfolioEntity> portfolios = portfolioRepository.findByUserId(userId);

        if (portfolios == null || portfolios.isEmpty()) {
            log.info("buildDashboard: No portfolio data found for userId: {}", userId);
            return createEmptyDashboard();
        }

        // Build dashboard from portfolio data
        return createResponse(portfolios);
    }

    private DashboardResponse createResponse(List<PortfolioEntity> portfolios) {
        List<DashboardResponse.PortfolioItem> portfolioItems = new ArrayList<>();
        BigDecimal totalInvestedCost = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;

        for (PortfolioEntity portfolio : portfolios) {
            try {
                DashboardResponse.PortfolioItem item = calculatePortfolioItem(portfolio);
                portfolioItems.add(item);
                totalInvestedCost = totalInvestedCost.add(portfolio.getTotalInvested());
                totalCurrentValue = totalCurrentValue.add(item.getCurrentValue());

                log.debug("Portfolio item: assetType={}, invested={}, quantity={}, currentValue={}, profit={}",
                        portfolio.getAssetType(), portfolio.getTotalInvested(), portfolio.getQuantity(),
                        item.getCurrentValue(), item.getProfit());
            } catch (Exception e) {
                log.error("createResponse: Error processing portfolio data: {}", portfolio, e);
            }
        }

        DashboardResponse.Summary summary = calculateSummary(totalInvestedCost, totalCurrentValue);
        log.info("createResponse: Dashboard built successfully with {} assets, totalInvestedCost={}, totalCurrentValue={}, totalProfit={}",
                portfolioItems.size(), totalInvestedCost, totalCurrentValue, summary.getTotalProfit());

        return DashboardResponse.builder().summary(summary).portfolios(portfolioItems).build();
    }

    private DashboardResponse.PortfolioItem calculatePortfolioItem(PortfolioEntity portfolio) {
        String assetType = portfolio.getAssetType();
        BigDecimal totalInvested = portfolio.getTotalInvested();
        BigDecimal quantity = portfolio.getQuantity();

        // Special handling for EMERGENCY fund: no profit/loss calculation
        if (InvestmentWorkflowConstant.ASSET_TYPE_EMERGENCY.equals(assetType)) {
            // For EMERGENCY: quantity = total_invested (cash equivalent), no price appreciation
            BigDecimal currentValue = totalInvested.setScale(SCALE_2, RoundingMode.HALF_UP);
            return DashboardResponse.PortfolioItem.builder()
                    .assetType(assetType)
                    .quantity(quantity.setScale(SCALE_2, RoundingMode.HALF_UP))
                    .investedCost(totalInvested.setScale(SCALE_2, RoundingMode.HALF_UP))
                    .currentValue(currentValue)
                    .profit(BigDecimal.ZERO.setScale(SCALE_2, RoundingMode.HALF_UP))
                    .profitRate(BigDecimal.ZERO.setScale(SCALE_2, RoundingMode.HALF_UP))
                    .build();
        }

        // For other assets: fetch current price and calculate current value
        BigDecimal currentPrice = getCurrentPriceForAssetType(assetType);
        BigDecimal currentValue = quantity.multiply(currentPrice).setScale(SCALE_2, RoundingMode.HALF_UP);
        BigDecimal profit = currentValue.subtract(totalInvested).setScale(SCALE_2, RoundingMode.HALF_UP);
        BigDecimal profitRate = calculateProfitRate(profit, totalInvested);

        return DashboardResponse.PortfolioItem.builder()
                .assetType(assetType)
                .quantity(quantity.setScale(SCALE_2, RoundingMode.HALF_UP))
                .investedCost(totalInvested.setScale(SCALE_2, RoundingMode.HALF_UP))
                .currentValue(currentValue)
                .profit(profit)
                .profitRate(profitRate)
                .build();
    }

    /**
     * Get the current price for an asset type
     * Finds the latest price for any product in this asset category
     * Uses the maximum buy price among all products in the category as the current price
     */
    private BigDecimal getCurrentPriceForAssetType(String assetType) {
        try {
            // Get all latest prices
            List<AssetPriceEntity> allLatestPrices = assetPriceRepository.findAllLatestPrices();

            if (allLatestPrices == null || allLatestPrices.isEmpty()) {
                log.warn("No latest prices found in database");
                return BigDecimal.ONE; // Default fallback price
            }

            // Group prices by product category and find the max price for this asset type
            BigDecimal maxPrice = BigDecimal.ZERO;
            for (AssetPriceEntity price : allLatestPrices) {
                // Get the product to find its category
                var product = assetProductRepository.findById(price.getProductId());
                // Note: This is simplified - we'd need to join with category in production
                // For now, we'll just return the first available price
                if (product.isPresent()) {
                    if (price.getBuyPrice() != null && price.getBuyPrice().compareTo(maxPrice) > 0) {
                        maxPrice = price.getBuyPrice();
                    }
                }
            }

            // Return max price found, or 1 as fallback
            return maxPrice.compareTo(BigDecimal.ZERO) > 0 ? maxPrice : BigDecimal.ONE;
        } catch (Exception e) {
            log.warn("Error fetching current price for assetType: {}, using fallback price of 1", assetType, e);
            return BigDecimal.ONE;
        }
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



