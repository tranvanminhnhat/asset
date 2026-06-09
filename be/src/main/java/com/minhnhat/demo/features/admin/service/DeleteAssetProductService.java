package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetProductEntity;
import com.minhnhat.demo.common.db.repository.AssetProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeleteAssetProductService {
    private final AssetProductRepository productRepository;
    private final MessageService messageService;

    public ApiResponse<Void> execute(Long id) {
        Context ctx = new Context();
        ctx.id = id;
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse();
    }

    private void loadData(Context ctx) {
        ctx.product = productRepository.findById(ctx.id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + ctx.id));
    }

    private void processBusiness(Context ctx) {
        ctx.product.setIsActive(false);
        productRepository.save(ctx.product);
        log.info("Asset product deleted: id={}, code={}", ctx.id, ctx.product.getCode());
    }

    private ApiResponse<Void> createResponse() {
        return ApiResponse.<Void>builder()
                .data(null)
                .message("admin.product.delete.success", "Xoa san pham tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        Long id;
        AssetProductEntity product;
    }
}

