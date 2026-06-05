package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.AssetPriceEntity;
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




