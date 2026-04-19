package com.minhnhat.demo.features.portfolio.service;

import com.minhnhat.demo.features.portfolio.entity.AssetPriceEntity;
import com.minhnhat.demo.features.portfolio.repository.AssetPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * PnL Service - Asset price utilities
 * Note: PnL calculations are no longer part of core system
 * Portfolio structure changed to transaction-based
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PnLService {

    private final AssetPriceRepository assetPriceRepository;

    /**
     * Get latest asset price
     */
    @Transactional(readOnly = true)
    public Optional<BigDecimal> getLatestAssetPrice(String assetType) {
        return assetPriceRepository.findById(assetType)
                .map(AssetPriceEntity::getPrice);
    }

    /**
     * Get current asset price by type
     */
    @Transactional(readOnly = true)
    public Optional<BigDecimal> getAssetPrice(String assetType) {
        return assetPriceRepository.findById(assetType)
                .map(AssetPriceEntity::getPrice);
    }
}



