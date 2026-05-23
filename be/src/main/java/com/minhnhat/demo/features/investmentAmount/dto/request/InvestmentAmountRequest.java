package com.minhnhat.demo.features.investmentAmount.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentAmountRequest {

    // Tổng số tiền muốn đầu tư
    private BigDecimal amount;

    // Loại phân bổ / hồ sơ rủi ro (ví dụ: SAFE, BALANCED, GROWTH)
    private String type;
}

