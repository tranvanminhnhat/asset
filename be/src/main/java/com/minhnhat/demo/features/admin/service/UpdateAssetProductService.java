package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetProductEntity;
import com.minhnhat.demo.common.db.repository.AssetCategoryRepository;
import com.minhnhat.demo.common.db.repository.AssetProductRepository;
import com.minhnhat.demo.common.db.repository.AssetPlatformRepository;
import com.minhnhat.demo.features.admin.dto.request.UpdateAssetProductRequest;
import com.minhnhat.demo.features.admin.dto.response.AssetProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateAssetProductService {
    private final AssetProductRepository productRepository;
    private final AssetCategoryRepository categoryRepository;
    private final AssetPlatformRepository platformRepository;
    private final MessageService messageService;

    public ApiResponse<AssetProductResponse> execute(UpdateAssetProductRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        this.validateRequest(ctx);
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        if (ctx.request.getId() == null) {
            throw new IllegalArgumentException("Product id is required");
        }
        if (ctx.request.getCode() == null || ctx.request.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Product code is required");
        }
        if (ctx.request.getName() == null || ctx.request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (ctx.request.getCategoryId() == null) {
            throw new IllegalArgumentException("Category id is required");
        }
        if (ctx.request.getPlatformId() == null) {
            throw new IllegalArgumentException("Platform id is required");
        }

        categoryRepository.findById(ctx.request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + ctx.request.getCategoryId()));

        platformRepository.findById(ctx.request.getPlatformId())
                .orElseThrow(() -> new IllegalArgumentException("Platform not found with id: " + ctx.request.getPlatformId()));
    }

    private void loadData(Context ctx) {
        ctx.product = productRepository.findById(ctx.request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + ctx.request.getId()));

        productRepository.findByCode(ctx.request.getCode()).ifPresent(existing -> {
            if (!existing.getId().equals(ctx.request.getId())) {
                throw new IllegalArgumentException("Product code already exists for another product");
            }
        });
    }

    private void processBusiness(Context ctx) {
        ctx.product.setCode(ctx.request.getCode());
        ctx.product.setName(ctx.request.getName());
        ctx.product.setCategoryId(ctx.request.getCategoryId());
        ctx.product.setPlatformId(ctx.request.getPlatformId());
        ctx.product.setMinimumQuantity(ctx.request.getMinimumQuantity());
        ctx.product.setUnitName(ctx.request.getUnitName());
        ctx.product.setDescription(ctx.request.getDescription());
        ctx.product = productRepository.save(ctx.product);
        log.info("Asset product updated: id={}, code={}, name={}", ctx.request.getId(), ctx.request.getCode(), ctx.request.getName());
    }

    private ApiResponse<AssetProductResponse> createResponse(Context ctx) {
        AssetProductResponse response = AssetProductResponse.builder()
                .id(ctx.product.getId())
                .code(ctx.product.getCode())
                .name(ctx.product.getName())
                .categoryId(ctx.product.getCategoryId())
                .platformId(ctx.product.getPlatformId())
                .minimumQuantity(ctx.product.getMinimumQuantity())
                .unitName(ctx.product.getUnitName())
                .description(ctx.product.getDescription())
                .isActive(ctx.product.getIsActive())
                .createdAt(ctx.product.getCreatedAt().toLocalDateTime())
                .updatedAt(ctx.product.getUpdatedAt().toLocalDateTime())
                .build();

        return ApiResponse.<AssetProductResponse>builder()
                .data(response)
                .message("admin.product.update.success", "Update san pham tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        UpdateAssetProductRequest request;
        AssetProductEntity product;
    }
}

