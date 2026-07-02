package com.minhnhat.demo.features.dashboard.service;

import com.minhnhat.demo.common.db.entity.AssetPriceEntity;
import com.minhnhat.demo.common.db.entity.InvestmentAssetSelectionEntity;
import com.minhnhat.demo.common.db.entity.PortfolioEntity;
import com.minhnhat.demo.common.db.repository.AssetPriceRepository;
import com.minhnhat.demo.common.db.repository.InvestmentAssetSelectionRepository;
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
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCurrentUserDashboardService {

    private final PortfolioRepository portfolioRepository;
    private final AssetPriceRepository assetPriceRepository;
    private final InvestmentAssetSelectionRepository selectionRepository;
    private final SecurityUtils securityUtils;

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int SCALE_6 = 6;
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

        // For other assets: Calculate market profit using dynamic market prices
        // Option 2 (Complete): Always use current market prices from asset_prices table
        // This ensures profit/loss updates automatically when asset_prices.current_price changes

        // Calculate cost basis per unit from original investment
        BigDecimal costPerUnit = totalInvested.divide(quantity, SCALE_6, RoundingMode.HALF_UP);

        // Get current market price from asset_prices table
        // This reflects real-time market prices, not the historical buy price
        BigDecimal currentMarketPrice = getCurrentPriceForAssetType(assetType);

        // Calculate current value based on current market prices
        // currentValue = quantity × currentMarketPrice
        BigDecimal currentValue = quantity.multiply(currentMarketPrice).setScale(SCALE_2, RoundingMode.HALF_UP);

        // Calculate market profit/loss based on market price changes
        // profit = currentValue - totalInvested = (currentMarketPrice - costPerUnit) × quantity
        // This will be 0 when prices match cost basis, and >0/<0 when market price is higher/lower
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
     * Get the current average price for an asset type
     *
     * Strategy:
     * 1. Find all actual product selections (InvestmentAssetSelectionEntity) for this assetType
     * 2. Get the latest prices for those specific products (from AssetPriceRepository)
     * 3. Calculate weighted average price: sum(price × quantity) / sum(quantity)
     * 4. This gives us a market-representative price based on actual holdings
     *
     * Special case: For EMERGENCY (cash), returns 1.0 (not used in calculation)
     */
    private BigDecimal getCurrentPriceForAssetType(String assetType) {
        try {
            // EMERGENCY is cash equivalent - no price lookup needed
            if (InvestmentWorkflowConstant.ASSET_TYPE_EMERGENCY.equals(assetType)) {
                return BigDecimal.ONE; // Won't multiply with quantity anyway
            }

            // Step 1: Get all product selections for this assetType
            // These are the actual products the user invested in
            List<InvestmentAssetSelectionEntity> selections = selectionRepository.findByAssetType(assetType);

            if (selections == null || selections.isEmpty()) {
                log.warn("No asset selections found for assetType: {}", assetType);
                return BigDecimal.ONE;
            }

            // Step 2: Extract unique product IDs
            Set<Long> productIds = new java.util.HashSet<>();
            for (InvestmentAssetSelectionEntity selection : selections) {
                if (selection.getProductId() != null) {
                    productIds.add(selection.getProductId());
                }
            }

            if (productIds.isEmpty()) {
                log.warn("No productId found in asset selections for assetType: {}", assetType);
                return BigDecimal.ONE;
            }

            // Step 3: Get latest prices for these specific products
            List<AssetPriceEntity> allLatestPrices = assetPriceRepository.findAllLatestPrices();

            if (allLatestPrices == null || allLatestPrices.isEmpty()) {
                log.warn("No latest prices found in database");
                return BigDecimal.ONE;
            }

            // Build a map of productId -> latest price
            Map<Long, BigDecimal> priceMap = new java.util.HashMap<>();
            for (AssetPriceEntity price : allLatestPrices) {
                if (productIds.contains(price.getProductId()) && price.getCurrentPrice() != null) {
                    priceMap.put(price.getProductId(), price.getCurrentPrice());
                }
            }

            if (priceMap.isEmpty()) {
                log.warn("No prices found for selected products of assetType: {}", assetType);
                return BigDecimal.ONE;
            }

            // Step 4: Calculate weighted average price
            // weightedAvg = sum(price × quantity) / sum(quantity)
            BigDecimal totalWeightedPrice = BigDecimal.ZERO;
            BigDecimal totalQuantity = BigDecimal.ZERO;

            for (InvestmentAssetSelectionEntity selection : selections) {
                Long productId = selection.getProductId();
                if (productId != null && priceMap.containsKey(productId)) {
                    BigDecimal price = priceMap.get(productId);
                    BigDecimal quantity = selection.getQuantity();

                    // Add weighted price: price × quantity
                    totalWeightedPrice = totalWeightedPrice.add(
                            price.multiply(quantity)
                    );
                    totalQuantity = totalQuantity.add(quantity);
                }
            }

            if (totalQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Total quantity is zero for assetType: {}", assetType);
                return BigDecimal.ONE;
            }

            // Calculate final weighted average
            BigDecimal weightedAvgPrice = totalWeightedPrice.divide(
                    totalQuantity,
                    SCALE_4,
                    RoundingMode.HALF_UP
            );

            log.debug("Calculated weighted average price for assetType: {}, avgPrice: {}, productCount: {}, totalQuantity: {}",
                    assetType, weightedAvgPrice, priceMap.size(), totalQuantity);

            return weightedAvgPrice;
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
