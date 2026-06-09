package com.minhnhat.demo.features.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetProductResponse {
    private Long id;
    private String code;
    private String name;
    private Integer categoryId;
    private Long platformId;
    private BigDecimal minimumQuantity;
    private String unitName;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

