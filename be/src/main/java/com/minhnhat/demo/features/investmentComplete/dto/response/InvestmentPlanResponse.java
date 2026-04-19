package com.minhnhat.demo.features.investmentComplete.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvestmentPlanResponse {

    private Long planId;
    private String userId;
    private Long strategyId;
    private BigDecimal totalAmount;
    private String status;
    private String recommendationSnapshot;
    private List<AllocationDetail> allocations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AllocationDetail {
        private Long allocationId;
        private String assetType;
        private BigDecimal amount;
        private BigDecimal percentage;
    }
}

