package com.minhnhat.demo.features.investment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvestmentSessionResponse {
    private Long sessionId;
    private String userId;
    private String strategyCode;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal totalAmount;
    private String status;
    private String currentStep;
    private LocalDateTime createdAt;
}

