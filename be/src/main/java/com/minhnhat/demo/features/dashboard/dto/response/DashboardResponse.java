package com.minhnhat.demo.features.dashboard.dto.response;

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
public class DashboardResponse {

    private Summary summary;
    private List<PortfolioItem> portfolios;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private BigDecimal totalInvested;
        private Long totalAssetTypes;
        private Long totalTransactions;
        private String currency;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortfolioItem {
        private Long portfolioId;
        private String assetType;
        private BigDecimal quantity;
        private BigDecimal totalInvested;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}


