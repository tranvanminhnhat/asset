package com.minhnhat.demo.features.master.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentAllocationMasterResponse {
    private String code;
    private BigDecimal stockPercent;
    private BigDecimal emergencyPercent;
    private BigDecimal goldPercent;
    private BigDecimal etfPercent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

