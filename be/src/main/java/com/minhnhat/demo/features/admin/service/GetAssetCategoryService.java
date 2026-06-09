package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetCategoryEntity;
import com.minhnhat.demo.common.db.repository.AssetCategoryRepository;
import com.minhnhat.demo.features.admin.dto.response.AssetCategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAssetCategoryService {
    private final AssetCategoryRepository categoryRepository;
    private final MessageService messageService;

    public ApiResponse<AssetCategoryResponse> execute(Integer id) {
        Context ctx = new Context();
        ctx.id = id;
        this.loadData(ctx);
        return this.createResponse(ctx);
    }

    private void loadData(Context ctx) {
        ctx.category = categoryRepository.findById(ctx.id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + ctx.id));
    }

    private ApiResponse<AssetCategoryResponse> createResponse(Context ctx) {
        AssetCategoryResponse response = AssetCategoryResponse.builder()
                .id(ctx.category.getId())
                .code(ctx.category.getCode())
                .name(ctx.category.getName())
                .description(ctx.category.getDescription())
                .isActive(ctx.category.getIsActive())
                .createdAt(ctx.category.getCreatedAt())
                .updatedAt(ctx.category.getUpdatedAt())
                .build();

        return ApiResponse.<AssetCategoryResponse>builder()
                .data(response)
                .message("admin.category.get.success", "Get danh muc tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        Integer id;
        AssetCategoryEntity category;
    }
}

