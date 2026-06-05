package com.minhnhat.demo.features.portfolio.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddTransactionRequest {

    private String transactionType; // BUY, SELL, DIVIDEND, ADJUSTMENT
    private String assetType;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private BigDecimal fee;
    private BigDecimal tax;
    private String referenceId;
    private String note;
}



