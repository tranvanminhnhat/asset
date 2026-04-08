package com.minhnhat.demo.features.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private Summary summary;
    private List<PortfolioItem> portfolios;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private BigDecimal totalCurrentValue;
        private BigDecimal totalPurchaseValue;
        private BigDecimal totalProfit;
        private BigDecimal profitPercentage;
        private String currency;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortfolioItem {
        private Long id;
        private String name;
        private String type;
        private BigDecimal purchaseValue;
        private BigDecimal currentValue;
        private BigDecimal profit;
        private BigDecimal profitPercentage;
        private Boolean isProfit;
    }
}


