package com.minhnhat.demo.features.portfolio.service;

import com.minhnhat.demo.common.db.entity.AssetPriceEntity;
import com.minhnhat.demo.common.db.repository.AssetPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetLatestAssetPriceService {

    private final AssetPriceRepository assetPriceRepository;

    @Transactional(readOnly = true)
    public Optional<BigDecimal> execute(String assetType) {
        return assetPriceRepository.findById(assetType)
                .map(AssetPriceEntity::getBuyPrice);
    }
}

