package com.minhnhat.demo.features.investment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentSessionDetailResponse {
    private Long sessionId;
    private String userId;
    private String strategyCode;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal totalAmount;
    private String status;
    private String currentStep;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AllocationDetail> allocations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllocationDetail {
        private Long allocationId;
        private String assetType;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
        private BigDecimal recommendedAmount;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
        private BigDecimal percentage;
        private String status;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
        private BigDecimal investedAmount;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
        private BigDecimal residualAmount;
        private List<AssetSelection> selections;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssetSelection {
        private Long selectionId;
        private String assetCode;
        private String assetName;
        private BigDecimal quantity;
        private BigDecimal buyPrice;
        private BigDecimal investedAmount;
        private LocalDateTime createdAt;
    }
}

