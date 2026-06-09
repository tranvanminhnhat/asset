package com.minhnhat.demo.features.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssetProductRequest {
    private Long id;
    private String code;
    private String name;
    private Integer categoryId;
    private Long platformId;
    private BigDecimal minimumQuantity;
    private String unitName;
    private String description;
}

