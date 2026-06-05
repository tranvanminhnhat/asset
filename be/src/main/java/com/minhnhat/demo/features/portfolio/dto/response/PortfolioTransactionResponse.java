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
public class PortfolioTransactionResponse {

    private Long transactionId;
    private String transactionType;
    private String assetType;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private BigDecimal fee;
    private BigDecimal tax;
    private String referenceId;
    private String note;
    private LocalDateTime createdAt;
}

