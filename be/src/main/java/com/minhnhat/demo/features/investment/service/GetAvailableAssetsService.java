package com.minhnhat.demo.features.investment.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetCategoryEntity;
import com.minhnhat.demo.common.db.entity.AssetPriceEntity;
import com.minhnhat.demo.common.db.entity.AssetProductEntity;
import com.minhnhat.demo.common.db.repository.AssetCategoryRepository;
import com.minhnhat.demo.common.db.repository.AssetPriceRepository;
import com.minhnhat.demo.common.db.repository.AssetProductRepository;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.response.AssetPriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAvailableAssetsService {

    private final AssetPriceRepository assetPriceRepository;
    private final AssetProductRepository assetProductRepository;
    private final AssetCategoryRepository assetCategoryRepository;
    private final MessageService messageService;

    public ApiResponse<List<AssetPriceResponse>> execute(String assetType) {
        Context ctx = new Context();
        ctx.assetType = assetType;
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void loadData(Context ctx) {
        log.debug("Loading asset data: assetType={}", ctx.assetType);

        // Load latest and active prices
        ctx.assets = assetPriceRepository.findAllByIsLatestTrueAndIsActiveTrue();

        // Load all active products for mapping
        ctx.productMap = assetProductRepository.findByIsActiveTrueOrderByName().stream()
                .collect(Collectors.toMap(AssetProductEntity::getId, p -> p));

        // Load all active categories for mapping
        ctx.categoryMap = assetCategoryRepository.findAllByIsActive(true).stream()
                .collect(Collectors.toMap(AssetCategoryEntity::getId, c -> c));

        log.debug("Loaded {} assets, {} products, {} categories",
                ctx.assets.size(), ctx.productMap.size(), ctx.categoryMap.size());
    }

    private void processBusiness(Context ctx) {
        ctx.responses = ctx.assets.stream()
                .filter(asset -> {
                    // Filter by assetType if provided
                    if (ctx.assetType == null || ctx.assetType.isBlank()) {
                        return true;
                    }

                    // Find the product for this asset
                    AssetProductEntity product = ctx.productMap.get(asset.getProductId());
                    if (product == null) {
                        return false;
                    }

                    // Find the category for this product
                    AssetCategoryEntity category = ctx.categoryMap.get(product.getCategoryId());
                    if (category == null) {
                        return false;
                    }

                    // Check if category code matches requested assetType
                    return category.getCode().equals(ctx.assetType);
                })
                .map(asset -> {
                    // Get product details
                    AssetProductEntity product = ctx.productMap.get(asset.getProductId());
                    String assetCode = product != null ? product.getCode() : null;
                    String assetName = product != null ? product.getName() : null;

                    // Get category (assetType) details
                    String assetType = null;
                    if (product != null) {
                        AssetCategoryEntity category = ctx.categoryMap.get(product.getCategoryId());
                        if (category != null) {
                            assetType = category.getCode();
                        }
                    }

                    return AssetPriceResponse.builder()
                            .id(asset.getId())
                            .productId(asset.getProductId())
                            .buyPrice(asset.getCurrentPrice())
                            .priceDate(asset.getPriceDate())
                            .isLatest(asset.getIsLatest())
                            .isActive(asset.getIsActive())
                            // Populate legacy fields for backward compatibility
                            .assetCode(assetCode)
                            .assetType(assetType)
                            .assetName(assetName)
                            .build();
                })
                .collect(Collectors.toList());

        log.debug("Processed {} assets for response", ctx.responses.size());
    }

    private ApiResponse<List<AssetPriceResponse>> createResponse(Context ctx) {
        return ApiResponse.<List<AssetPriceResponse>>builder()
                .data(ctx.responses)
                .message(InvestmentWorkflowConstant.GET_ASSETS_INFO,
                        messageService.get(InvestmentWorkflowConstant.GET_ASSETS_INFO),
                        InvestmentWorkflowConstant.SERVICE_NAME)
                .build();
    }

    private static class Context {
        String assetType;
        List<AssetPriceEntity> assets;
        Map<Long, AssetProductEntity> productMap;
        Map<Integer, AssetCategoryEntity> categoryMap;
        List<AssetPriceResponse> responses;
    }
}




