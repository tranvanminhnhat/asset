package com.minhnhat.demo.features.investment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalizeSessionResponse {
    private Long sessionId;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal totalInvested;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal totalResidual;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal updatedCashBalance;
    private LocalDateTime completedAt;
}

