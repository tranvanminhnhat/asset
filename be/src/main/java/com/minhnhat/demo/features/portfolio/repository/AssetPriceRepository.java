package com.minhnhat.demo.features.portfolio.repository;

import com.minhnhat.demo.features.portfolio.entity.AssetPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Asset Price Repository
 * Note: assetType is String PK (not Long)
 */
@Repository
public interface AssetPriceRepository extends JpaRepository<AssetPriceEntity, String> {
    
    // findById is inherited from JpaRepository with String pk
    // Usage: assetPriceRepository.findById("STOCK")
}



