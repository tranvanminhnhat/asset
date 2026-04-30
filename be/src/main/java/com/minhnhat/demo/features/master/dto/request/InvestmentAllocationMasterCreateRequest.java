package com.minhnhat.demo.features.master.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentAllocationMasterCreateRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Stock percentage is required")
    @DecimalMin(value = "0", message = "Stock percentage must be >= 0")
    @DecimalMax(value = "100", message = "Stock percentage must be <= 100")
    private BigDecimal stockPercent;

    @NotNull(message = "Emergency percentage is required")
    @DecimalMin(value = "0", message = "Emergency percentage must be >= 0")
    @DecimalMax(value = "100", message = "Emergency percentage must be <= 100")
    private BigDecimal emergencyPercent;

    @NotNull(message = "Gold percentage is required")
    @DecimalMin(value = "0", message = "Gold percentage must be >= 0")
    @DecimalMax(value = "100", message = "Gold percentage must be <= 100")
    private BigDecimal goldPercent;

    @NotNull(message = "ETF percentage is required")
    @DecimalMin(value = "0", message = "ETF percentage must be >= 0")
    @DecimalMax(value = "100", message = "ETF percentage must be <= 100")
    private BigDecimal etfPercent;
}

