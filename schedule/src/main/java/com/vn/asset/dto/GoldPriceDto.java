package com.vn.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoldPriceDto {
    private String code;
    private String name;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    private String time;
    private String date;
}


