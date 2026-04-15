package com.minhnhat.demo.features.investmentRecommendations.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentRecommendationsRequest {
    // total investment amount
    private BigDecimal amount;

    // allocation type/risk profile: will be matched against master table (e.g., SAFE, BALANCED, GROWTH)
    private String type;
}



