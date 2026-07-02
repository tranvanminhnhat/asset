package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetPriceEntity;
import com.minhnhat.demo.common.db.repository.AssetPriceRepository;
import com.minhnhat.demo.common.db.repository.AssetProductRepository;
import com.minhnhat.demo.features.admin.dto.request.UpdateAssetPriceRequest;
import com.minhnhat.demo.features.admin.dto.response.AssetPriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateAssetPriceService {
    private final AssetPriceRepository priceRepository;
    private final AssetProductRepository productRepository;
    private final MessageService messageService;

    public ApiResponse<AssetPriceResponse> execute(UpdateAssetPriceRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        this.validateRequest(ctx);
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        if (ctx.request.getId() == null) {
            throw new IllegalArgumentException("Price id is required");
        }
        if (ctx.request.getProductId() == null) {
            throw new IllegalArgumentException("Product id is required");
        }
        if (ctx.request.getCurrentPrice() == null) {
            throw new IllegalArgumentException("Current price is required");
        }

        productRepository.findById(ctx.request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + ctx.request.getProductId()));
    }

    private void loadData(Context ctx) {
        ctx.price = priceRepository.findById(ctx.request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Price not found with id: " + ctx.request.getId()));
    }

    private void processBusiness(Context ctx) {
        ctx.price.setProductId(ctx.request.getProductId());
        ctx.price.setCurrentPrice(ctx.request.getCurrentPrice());
        if (ctx.request.getPriceDate() != null) {
            ctx.price.setPriceDate(ctx.request.getPriceDate());
        }
        ctx.price = priceRepository.save(ctx.price);
        log.info("Asset price updated: id={}, productId={}, currentPrice={}", ctx.request.getId(), ctx.request.getProductId(), ctx.request.getCurrentPrice());
    }

    private ApiResponse<AssetPriceResponse> createResponse(Context ctx) {
        AssetPriceResponse response = AssetPriceResponse.builder()
                .id(ctx.price.getId())
                .productId(ctx.price.getProductId())
                .currentPrice(ctx.price.getCurrentPrice())
                .priceDate(ctx.price.getPriceDate())
                .isLatest(ctx.price.getIsLatest())
                .isActive(ctx.price.getIsActive())
                .createdAt(ctx.price.getCreatedAt())
                .updatedAt(ctx.price.getUpdatedAt())
                .build();

        return ApiResponse.<AssetPriceResponse>builder()
                .data(response)
                .message("admin.price.update.success", "Update gia tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        UpdateAssetPriceRequest request;
        AssetPriceEntity price;
    }
}

