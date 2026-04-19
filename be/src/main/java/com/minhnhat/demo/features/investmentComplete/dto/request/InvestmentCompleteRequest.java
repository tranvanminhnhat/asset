package com.minhnhat.demo.features.investmentComplete.dto.request;

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
public class InvestmentCompleteRequest {
    // total investment amount
    private BigDecimal amount;

    // allocation strategy ID
    private Long strategyId;

    // allocation type/risk profile (e.g., SAFE, BALANCED, GROWTH)
    private String type;

    // list of allocations that can be edited by user
    private List<Allocation> allocations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Allocation {
        private String type;
        private BigDecimal amount;
        private BigDecimal percent;
    }
}
