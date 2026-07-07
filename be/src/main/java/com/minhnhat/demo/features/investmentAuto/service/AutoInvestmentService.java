package com.minhnhat.demo.features.investmentAuto.service;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.db.entity.*;
import com.minhnhat.demo.common.db.repository.*;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.investment.constant.InvestmentWorkflowConstant;
import com.minhnhat.demo.features.investment.dto.request.CreateInvestmentSessionRequest;
import com.minhnhat.demo.features.investment.dto.request.FinalizeAllocationItem;
import com.minhnhat.demo.features.investment.dto.request.FinalizeSessionRequest;
import com.minhnhat.demo.features.investment.dto.request.SelectAssetRequest;
import com.minhnhat.demo.features.investment.dto.response.CreateInvestmentSessionResponse;
import com.minhnhat.demo.features.investment.dto.response.FinalizeSessionResponse;
import com.minhnhat.demo.features.investment.service.CreateInvestmentSessionService;
import com.minhnhat.demo.features.investment.service.FinalizeInvestmentSessionService;
import com.minhnhat.demo.features.investment.service.ReviewInvestmentSessionService;
import com.minhnhat.demo.features.investment.service.SelectAssetForAllocationService;
import com.minhnhat.demo.features.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoInvestmentService {

    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;
    private final AssetCategoryRepository assetCategoryRepository;
    private final AssetProductRepository assetProductRepository;
    private final AssetPriceRepository assetPriceRepository;
    private final InvestmentSessionRepository sessionRepository;
    private final InvestmentSessionAllocationRepository allocationRepository;
    private final CreateInvestmentSessionService createInvestmentSessionService;
    private final SelectAssetForAllocationService selectAssetForAllocationService;
    private final ReviewInvestmentSessionService reviewInvestmentSessionService;
    private final FinalizeInvestmentSessionService finalizeInvestmentSessionService;
    private final NotificationService notificationService;

    @Transactional(rollbackFor = Exception.class)
    public FinalizeSessionResponse execute(BigDecimal amount, Long notificationId) {
        CurrentUserInfo currentUser = securityUtils.getCurrentUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new IllegalArgumentException("User must be authenticated");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        UserEntity user = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!Boolean.TRUE.equals(user.getAutoInvestEnabled())) {
            throw new IllegalArgumentException("Auto invest is disabled for this user");
        }
        if (user.getInvestmentStyle() == null || user.getInvestmentStyle().isBlank()) {
            throw new IllegalArgumentException("Investment style is not configured");
        }

        String strategyCode = user.getInvestmentStyle().trim().toUpperCase(Locale.ROOT);
        ApiResponse<CreateInvestmentSessionResponse> created = createInvestmentSessionService.execute(
                CreateInvestmentSessionRequest.builder()
                        .strategyCode(strategyCode)
                        .totalAmount(amount)
                        .build()
        );
        Long sessionId = created.getData().getSessionId();

        InvestmentSessionEntity session = sessionRepository.findByIdWithAllocations(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Investment session not found after creation"));

        for (InvestmentSessionAllocationEntity allocation : session.getAllocations()) {
            if (InvestmentWorkflowConstant.ASSET_TYPE_EMERGENCY.equals(allocation.getAssetType())) {
                allocation.setInvestedAmount(allocation.getRecommendedAmount());
                allocation.setResidualAmount(BigDecimal.ZERO);
                allocation.setStatus(InvestmentWorkflowConstant.ALLOCATION_COMPLETED);
                allocationRepository.save(allocation);
                continue;
            }

            AssetProductEntity selectedProduct = pickRandomProduct(allocation.getAssetType(), allocation.getRecommendedAmount());
            AssetPriceEntity latestPrice = assetPriceRepository.findByProductIdAndIsLatestTrue(selectedProduct.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Latest price not found for product: " + selectedProduct.getCode()));

            BigDecimal quantity = calculateQuantity(allocation.getRecommendedAmount(), latestPrice.getCurrentPrice());
            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount is too small for asset type: " + allocation.getAssetType());
            }

            SelectAssetRequest selectRequest = SelectAssetRequest.builder()
                    .productId(selectedProduct.getId())
                    .assetType(allocation.getAssetType())
                    .assetCode(selectedProduct.getCode())
                    .quantity(quantity)
                    .build();

            selectAssetForAllocationService.execute(sessionId, allocation.getId(), selectRequest);
        }

        reviewInvestmentSessionService.execute(sessionId);

        InvestmentSessionEntity reviewedSession = sessionRepository.findByIdWithAllocations(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Investment session not found after review"));

        List<FinalizeAllocationItem> finalizeItems = reviewedSession.getAllocations().stream()
                .map(allocation -> FinalizeAllocationItem.builder()
                        .assetType(allocation.getAssetType())
                        .finalAmount(allocation.getInvestedAmount() != null ? allocation.getInvestedAmount() : BigDecimal.ZERO)
                        .build())
                .toList();

        ApiResponse<FinalizeSessionResponse> finalized = finalizeInvestmentSessionService.execute(
                sessionId,
                FinalizeSessionRequest.builder().allocations(finalizeItems).build()
        );

        FinalizeSessionResponse response = finalized.getData();
        if (notificationId != null) {
            notificationService.markDone(notificationId);
        }
        return response;
    }

    private AssetProductEntity pickRandomProduct(String assetType, BigDecimal amount) {
        AssetCategoryEntity category = assetCategoryRepository.findByCode(assetType)
                .orElseThrow(() -> new IllegalArgumentException("Asset category not found for type: " + assetType));

        List<AssetProductEntity> activeProducts = assetProductRepository.findByCategoryIdAndIsActiveTrue(category.getId());
        if (activeProducts == null || activeProducts.isEmpty()) {
            throw new IllegalArgumentException("No active products available for asset type: " + assetType);
        }

        Map<Long, AssetPriceEntity> priceMap = assetPriceRepository.findAllLatestPrices().stream()
                .filter(price -> price.getCurrentPrice() != null)
                .collect(Collectors.toMap(AssetPriceEntity::getProductId, price -> price, (left, right) -> left));

        List<AssetProductEntity> pricedProducts = activeProducts.stream()
                .filter(product -> priceMap.containsKey(product.getId()))
                .collect(Collectors.toList());
        if (pricedProducts.isEmpty()) {
            throw new IllegalArgumentException("No priced products available for asset type: " + assetType);
        }

        List<AssetProductEntity> affordableProducts = pricedProducts.stream()
                .filter(product -> priceMap.get(product.getId()).getCurrentPrice().compareTo(BigDecimal.ZERO) > 0)
                .filter(product -> amount == null || amount.compareTo(priceMap.get(product.getId()).getCurrentPrice()) >= 0)
                .collect(Collectors.toList());

        if (affordableProducts.isEmpty()) {
            throw new IllegalArgumentException("Amount is too small for any available product in asset type: " + assetType);
        }

        return affordableProducts.get(ThreadLocalRandom.current().nextInt(affordableProducts.size()));
    }

    private BigDecimal calculateQuantity(BigDecimal amount, BigDecimal currentPrice) {
        if (amount == null || currentPrice == null || currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return amount.divide(currentPrice, 4, RoundingMode.DOWN);
    }
}




