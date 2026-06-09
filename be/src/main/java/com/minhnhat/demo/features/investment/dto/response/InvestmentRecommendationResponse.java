package com.minhnhat.demo.features.investment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentRecommendationResponse {
    private Long sessionId;
    private List<AllocationRecommendation> recommendations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllocationRecommendation {
        private String type;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
        private BigDecimal amount;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
        private BigDecimal percent;
    }
}

