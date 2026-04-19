package com.minhnhat.demo.features.portfolio.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortfolioSummaryResponse {

    private Long portfolioId;
    private String assetType;
    private BigDecimal quantity;
    private BigDecimal totalInvested;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

