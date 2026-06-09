package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetProductEntity;
import com.minhnhat.demo.common.db.repository.AssetCategoryRepository;
import com.minhnhat.demo.common.db.repository.AssetProductRepository;
import com.minhnhat.demo.common.db.repository.AssetPlatformRepository;
import com.minhnhat.demo.features.admin.dto.request.CreateAssetProductRequest;
import com.minhnhat.demo.features.admin.dto.response.AssetProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateAssetProductService {
    private final AssetProductRepository productRepository;
    private final AssetCategoryRepository categoryRepository;
    private final AssetPlatformRepository platformRepository;
    private final MessageService messageService;

    public ApiResponse<AssetProductResponse> execute(CreateAssetProductRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        this.validateRequest(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
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

        if (productRepository.findByCode(ctx.request.getCode()).isPresent()) {
            throw new IllegalArgumentException("Product code already exists");
        }

        categoryRepository.findById(ctx.request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + ctx.request.getCategoryId()));

        platformRepository.findById(ctx.request.getPlatformId())
                .orElseThrow(() -> new IllegalArgumentException("Platform not found with id: " + ctx.request.getPlatformId()));
    }

    private void processBusiness(Context ctx) {
        AssetProductEntity product = AssetProductEntity.builder()
                .code(ctx.request.getCode())
                .name(ctx.request.getName())
                .categoryId(ctx.request.getCategoryId())
                .platformId(ctx.request.getPlatformId())
                .minimumQuantity(ctx.request.getMinimumQuantity())
                .unitName(ctx.request.getUnitName())
                .description(ctx.request.getDescription())
                .isActive(true)
                .build();
        ctx.savedProduct = productRepository.save(product);
        log.info("Asset product created: code={}, name={}", ctx.request.getCode(), ctx.request.getName());
    }

    private ApiResponse<AssetProductResponse> createResponse(Context ctx) {
        AssetProductResponse response = AssetProductResponse.builder()
                .id(ctx.savedProduct.getId())
                .code(ctx.savedProduct.getCode())
                .name(ctx.savedProduct.getName())
                .categoryId(ctx.savedProduct.getCategoryId())
                .platformId(ctx.savedProduct.getPlatformId())
                .minimumQuantity(ctx.savedProduct.getMinimumQuantity())
                .unitName(ctx.savedProduct.getUnitName())
                .description(ctx.savedProduct.getDescription())
                .isActive(ctx.savedProduct.getIsActive())
                .createdAt(ctx.savedProduct.getCreatedAt().toLocalDateTime())
                .updatedAt(ctx.savedProduct.getUpdatedAt().toLocalDateTime())
                .build();

        return ApiResponse.<AssetProductResponse>builder()
                .data(response)
                .message("admin.product.create.success", "Tao san pham tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        CreateAssetProductRequest request;
        AssetProductEntity savedProduct;
    }
}

