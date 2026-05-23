package com.minhnhat.demo.features.master.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentAllocationMasterCreateRequest {

    @NotBlank(message = "Code is required")
    @Size(max = 30, message = "Code length must be <= 30")
    private String code;

    @Size(max = 100, message = "Name length must be <= 100")
    private String name;

    @NotEmpty(message = "Details are required")
    private List<@Valid DetailRequest> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailRequest {
        @NotBlank(message = "Asset type is required")
        @Size(max = 50, message = "Asset type length must be <= 50")
        private String assetType;

        @NotNull(message = "Percentage is required")
        @DecimalMin(value = "0", message = "Percentage must be >= 0")
        @DecimalMax(value = "100", message = "Percentage must be <= 100")
        private BigDecimal percentage;

        @DecimalMin(value = "0", message = "Minimum lot size amount must be >= 0")
        private BigDecimal minimumLotSizeAmount;
    }
}

