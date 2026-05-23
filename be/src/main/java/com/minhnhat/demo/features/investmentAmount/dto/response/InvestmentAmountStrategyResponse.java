package com.minhnhat.demo.features.investmentAmount.dto.response;

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
public class InvestmentAmountStrategyResponse {
    private Long id;
    private String code;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DetailResponse> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailResponse {
        private Long id;
        private Long strategyId;
        private String assetType;
        private BigDecimal percentage;
        private BigDecimal minimumLotSizeAmount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}

