package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.AssetPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetPriceRepository extends JpaRepository<AssetPriceEntity, Long> {

    Optional<AssetPriceEntity> findByProductIdAndIsLatestTrue(Long productId);

    List<AssetPriceEntity> findByProductIdOrderByPriceDateDesc(Long productId);

    List<AssetPriceEntity> findByProductIdAndIsActiveTrue(Long productId);

    List<AssetPriceEntity> findAllByIsLatestTrueAndIsActiveTrue();

    @Query("SELECT p FROM AssetPriceEntity p WHERE p.isLatest = true ORDER BY p.priceDate DESC")
    List<AssetPriceEntity> findAllLatestPrices();

    // Backward compatibility methods for old assetCode/assetType based system
    @Deprecated
    default Optional<AssetPriceEntity> findByAssetCode(String assetCode) {
        return Optional.empty();
    }

    @Deprecated
    default List<AssetPriceEntity> findByAssetType(String assetType) {
        return List.of();
    }

    @Deprecated
    default List<AssetPriceEntity> findByAssetTypeAndIsActiveTrue(String assetType) {
        return List.of();
    }
}




