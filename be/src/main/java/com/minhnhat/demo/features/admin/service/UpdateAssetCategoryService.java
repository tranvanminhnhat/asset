package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetCategoryEntity;
import com.minhnhat.demo.common.db.repository.AssetCategoryRepository;
import com.minhnhat.demo.features.admin.dto.request.UpdateAssetCategoryRequest;
import com.minhnhat.demo.features.admin.dto.response.AssetCategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateAssetCategoryService {
    private final AssetCategoryRepository categoryRepository;
    private final MessageService messageService;

    public ApiResponse<AssetCategoryResponse> execute(UpdateAssetCategoryRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        this.validateRequest(ctx);
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        if (ctx.request.getId() == null) {
            throw new IllegalArgumentException("Category id is required");
        }
        if (ctx.request.getCode() == null || ctx.request.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Category code is required");
        }
        if (ctx.request.getName() == null || ctx.request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }
    }

    private void loadData(Context ctx) {
        ctx.category = categoryRepository.findById(ctx.request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + ctx.request.getId()));

        // Check if new code already exists for a different category
        categoryRepository.findByCode(ctx.request.getCode()).ifPresent(existing -> {
            if (!existing.getId().equals(ctx.request.getId())) {
                throw new IllegalArgumentException("Category code already exists for another category");
            }
        });
    }

    private void processBusiness(Context ctx) {
        ctx.category.setCode(ctx.request.getCode());
        ctx.category.setName(ctx.request.getName());
        ctx.category.setDescription(ctx.request.getDescription());
        ctx.category = categoryRepository.save(ctx.category);
        log.info("Asset category updated: id={}, code={}, name={}", ctx.request.getId(), ctx.request.getCode(), ctx.request.getName());
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
                .message("admin.category.update.success", "Update danh muc tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        UpdateAssetCategoryRequest request;
        AssetCategoryEntity category;
    }
}

