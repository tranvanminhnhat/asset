package com.minhnhat.demo.features.investment.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.AssetPriceEntity;
import com.minhnhat.demo.common.db.repository.AssetPriceRepository;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.response.AssetPriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAvailableAssetsService {

    private final AssetPriceRepository assetPriceRepository;
    private final MessageService messageService;

    public ApiResponse<List<AssetPriceResponse>> execute(String assetType) {
        Context ctx = new Context();
        ctx.assetType = assetType;
        this.loadData(ctx);
        this.processBusiness(ctx);
        return this.createResponse(ctx);
    }

    private void loadData(Context ctx) {

        // Load latest prices for all active products
        ctx.assets = assetPriceRepository.findAllByIsLatestTrueAndIsActiveTrue();
    }

    private void processBusiness(Context ctx) {
        ctx.responses = ctx.assets.stream()
                .map(asset -> AssetPriceResponse.builder()
                        .id(asset.getId())
                        .productId(asset.getProductId())
                        .buyPrice(asset.getBuyPrice())
                        .sellPrice(asset.getSellPrice())
                        .priceDate(asset.getPriceDate())
                        .isLatest(asset.getIsLatest())
                        .isActive(asset.getIsActive())
                        .build())
                .collect(java.util.stream.Collectors.toList());
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
        List<AssetPriceResponse> responses;
    }
}




