package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetProductEntity;
import com.minhnhat.demo.common.db.repository.AssetProductRepository;
import com.minhnhat.demo.features.admin.dto.response.AssetProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAssetProductService {
    private final AssetProductRepository productRepository;
    private final MessageService messageService;

    public ApiResponse<AssetProductResponse> execute(Long id) {
        Context ctx = new Context();
        ctx.id = id;
        this.loadData(ctx);
        return this.createResponse(ctx);
    }

    private void loadData(Context ctx) {
        ctx.product = productRepository.findById(ctx.id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + ctx.id));
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
                .message("admin.product.get.success", "Get san pham tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        Long id;
        AssetProductEntity product;
    }
}

