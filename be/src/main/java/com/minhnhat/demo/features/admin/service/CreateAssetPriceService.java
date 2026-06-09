package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetPriceEntity;
import com.minhnhat.demo.common.db.repository.AssetPriceRepository;
import com.minhnhat.demo.common.db.repository.AssetProductRepository;
import com.minhnhat.demo.features.admin.dto.request.CreateAssetPriceRequest;
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
public class CreateAssetPriceService {
    private final AssetPriceRepository priceRepository;
    private final AssetProductRepository productRepository;
    private final MessageService messageService;

    public ApiResponse<AssetPriceResponse> execute(CreateAssetPriceRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        this.validateRequest(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void validateRequest(Context ctx) {
        if (ctx.request.getProductId() == null) {
            throw new IllegalArgumentException("Product id is required");
        }
        if (ctx.request.getBuyPrice() == null) {
            throw new IllegalArgumentException("Buy price is required");
        }

        productRepository.findById(ctx.request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + ctx.request.getProductId()));
    }

    private void processBusiness(Context ctx) {
        // Mark previous latest price as not latest
        priceRepository.findByProductIdAndIsLatestTrue(ctx.request.getProductId())
                .ifPresent(previousPrice -> {
                    previousPrice.setIsLatest(false);
                    priceRepository.save(previousPrice);
                });

        // Create new price
        LocalDateTime priceDate = ctx.request.getPriceDate() != null ? ctx.request.getPriceDate() : LocalDateTime.now();
        AssetPriceEntity price = AssetPriceEntity.builder()
                .productId(ctx.request.getProductId())
                .buyPrice(ctx.request.getBuyPrice())
                .sellPrice(ctx.request.getSellPrice())
                .priceDate(priceDate)
                .isLatest(true)
                .isActive(true)
                .build();
        ctx.savedPrice = priceRepository.save(price);
        log.info("Asset price created: productId={}, buyPrice={}", ctx.request.getProductId(), ctx.request.getBuyPrice());
    }

    private ApiResponse<AssetPriceResponse> createResponse(Context ctx) {
        AssetPriceResponse response = AssetPriceResponse.builder()
                .id(ctx.savedPrice.getId())
                .productId(ctx.savedPrice.getProductId())
                .buyPrice(ctx.savedPrice.getBuyPrice())
                .sellPrice(ctx.savedPrice.getSellPrice())
                .priceDate(ctx.savedPrice.getPriceDate())
                .isLatest(ctx.savedPrice.getIsLatest())
                .isActive(ctx.savedPrice.getIsActive())
                .createdAt(ctx.savedPrice.getCreatedAt())
                .updatedAt(ctx.savedPrice.getUpdatedAt())
                .build();

        return ApiResponse.<AssetPriceResponse>builder()
                .data(response)
                .message("admin.price.create.success", "Tao gia tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        CreateAssetPriceRequest request;
        AssetPriceEntity savedPrice;
    }
}

