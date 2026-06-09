package com.minhnhat.demo.features.investment.dto.response;

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
public class AllocationMasterResponse {
    private List<AllocationMaster> allocations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllocationMaster {
        private String strategyCode;
        private String strategyName;
        private List<AllocationDetail> details;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllocationDetail {
        private String assetType;
        private BigDecimal percentage;
        private BigDecimal minimumLotSizeAmount;
    }
}

