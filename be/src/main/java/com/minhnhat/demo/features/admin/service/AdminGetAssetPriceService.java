package com.minhnhat.demo.features.admin.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetPriceEntity;
import com.minhnhat.demo.common.db.repository.AssetPriceRepository;
import com.minhnhat.demo.features.admin.dto.response.AssetPriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminGetAssetPriceService {
    private final AssetPriceRepository priceRepository;
    private final MessageService messageService;

    public ApiResponse<AssetPriceResponse> execute(Long id) {
        Context ctx = new Context();
        ctx.id = id;
        this.loadData(ctx);
        return this.createResponse(ctx);
    }

    private void loadData(Context ctx) {
        ctx.price = priceRepository.findById(ctx.id)
                .orElseThrow(() -> new IllegalArgumentException("Price not found with id: " + ctx.id));
    }

    private ApiResponse<AssetPriceResponse> createResponse(Context ctx) {
        AssetPriceResponse response = AssetPriceResponse.builder()
                .id(ctx.price.getId())
                .productId(ctx.price.getProductId())
                .buyPrice(ctx.price.getBuyPrice())
                .sellPrice(ctx.price.getSellPrice())
                .priceDate(ctx.price.getPriceDate())
                .isLatest(ctx.price.getIsLatest())
                .isActive(ctx.price.getIsActive())
                .createdAt(ctx.price.getCreatedAt())
                .updatedAt(ctx.price.getUpdatedAt())
                .build();

        return ApiResponse.<AssetPriceResponse>builder()
                .data(response)
                .message("admin.price.get.success", "Get gia tai san thanh cong", "AdminService")
                .build();
    }

    private static class Context {
        Long id;
        AssetPriceEntity price;
    }
}


