package com.minhnhat.demo.features.investmentRecommendations.dto.response;

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
public class InvestmentRecommendationsResponse {
    private List<Allocation> allocations;

    // type of allocation (e.g. "EMERGENCY", "GOLD", "ETF", "STOCK") - maps to DB keys
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Allocation {
        private String type;

        // allocated amount (currency)
        private BigDecimal amount;

        // Tiền dư kỳ trước
        private BigDecimal carryOverAmount;

        private BigDecimal investableAmount;

        private BigDecimal residualAmount;

        // percent as decimal (e.g. 0.25 for 25%)
        private BigDecimal percent;
    }
}


