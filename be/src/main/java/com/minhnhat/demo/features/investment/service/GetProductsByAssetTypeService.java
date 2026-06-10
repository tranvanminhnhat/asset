package com.minhnhat.demo.features.investment.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.*;
import com.minhnhat.demo.common.db.repository.*;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.response.ProductWithPriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetProductsByAssetTypeService {

    private final AssetProductRepository assetProductRepository;
    private final AssetCategoryRepository assetCategoryRepository;
    private final AssetPlatformRepository assetPlatformRepository;
    private final AssetPriceRepository assetPriceRepository;
    private final MessageService messageService;

    public ApiResponse<List<ProductWithPriceResponse>> execute(String assetType) {
        Context ctx = new Context();
        ctx.assetType = assetType;
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void loadData(Context ctx) {
        log.debug("Loading products by assetType: {}", ctx.assetType);

        // Load category for validation
        ctx.category = assetCategoryRepository.findByCode(ctx.assetType)
                .orElseThrow(() -> {
                    log.error("Category not found for code: {}", ctx.assetType);
                    return new IllegalArgumentException("Asset type not found: " + ctx.assetType);
                });

        // Load all active products for this category
        ctx.products = assetProductRepository.findByIsActiveTrueOrderByName();

        // Load all active platforms
        ctx.platforms = assetPlatformRepository.findAllByIsActiveTrueOrderByName();

        // Load all active prices
        ctx.prices = assetPriceRepository.findAllByIsLatestTrueAndIsActiveTrue();

        log.debug("Loaded {} products, {} platforms, {} prices",
                ctx.products.size(), ctx.platforms.size(), ctx.prices.size());
    }

    private void processBusiness(Context ctx) {
        log.debug("Processing products for assetType: {}", ctx.assetType);

        // Build maps for efficient lookup
        Map<Long, AssetPriceEntity> priceByProductId = ctx.prices.stream()
                .collect(Collectors.toMap(AssetPriceEntity::getProductId, p -> p));

        Map<Long, AssetPlatformEntity> platformMap = ctx.platforms.stream()
                .collect(Collectors.toMap(AssetPlatformEntity::getId, p -> p));

        // Filter products by category and map to response
        ctx.responses = ctx.products.stream()
                .filter(product -> product.getCategoryId().equals(ctx.category.getId()))
                .map(product -> {
                    AssetPriceEntity price = priceByProductId.get(product.getId());
                    AssetPlatformEntity platform = platformMap.get(product.getPlatformId());

                    return ProductWithPriceResponse.builder()
                            .productId(product.getId())
                            .assetCode(product.getCode())
                            .assetType(ctx.assetType)
                            .assetName(product.getName())
                            .platformName(platform != null ? platform.getName() : "")
                            .buyPrice(price != null ? price.getBuyPrice() : null)
                            .sellPrice(price != null ? price.getSellPrice() : null)
                            .build();
                })
                .collect(Collectors.toList());

        log.debug("Processed {} products for response", ctx.responses.size());
    }

    private ApiResponse<List<ProductWithPriceResponse>> createResponse(Context ctx) {
        return ApiResponse.<List<ProductWithPriceResponse>>builder()
                .data(ctx.responses)
                .message(InvestmentWorkflowConstant.GET_ASSETS_INFO,
                        messageService.get(InvestmentWorkflowConstant.GET_ASSETS_INFO),
                        InvestmentWorkflowConstant.SERVICE_NAME)
                .build();
    }

    private static class Context {
        String assetType;
        AssetCategoryEntity category;
        List<AssetProductEntity> products;
        List<AssetPlatformEntity> platforms;
        List<AssetPriceEntity> prices;
        List<ProductWithPriceResponse> responses;
    }
}


