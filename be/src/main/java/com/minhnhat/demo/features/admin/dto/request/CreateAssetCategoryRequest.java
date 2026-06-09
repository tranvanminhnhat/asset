package com.minhnhat.demo.features.admin.dto.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssetCategoryRequest {
    private String code;
    private String name;
    private String description;
}
