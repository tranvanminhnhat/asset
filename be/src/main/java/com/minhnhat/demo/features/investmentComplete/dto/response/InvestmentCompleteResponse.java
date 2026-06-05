package com.minhnhat.demo.features.investmentComplete.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentCompleteResponse {
    private String sessionId;
    private Long planId;
    private String userId;
    private String type;
    private BigDecimal totalAmount;
    private String status;
    private String recommendationSnapshot;
    private LocalDateTime createdAt;

    private List<Allocation> allocations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Allocation {
        private String type;
        private BigDecimal amount;
        private Integer quantity;
        private BigDecimal percent;
        private BigDecimal residualAmount;
    }
}

