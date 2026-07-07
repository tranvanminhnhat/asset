package com.vn.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EtfQuoteDto {
    private String code;
    private String name;
    private BigDecimal price;
    private BigDecimal nav;
    private BigDecimal priceChange;
    private BigDecimal percentChange;
    private Long volume;
    private String exchange;
}

