package com.vn.asset.repository;

import com.vn.asset.entity.AssetPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetPriceRepository extends JpaRepository<AssetPrice, Long> {
    List<AssetPrice> findByProductIdAndIsLatestTrue(Long productId);
    List<AssetPrice> findByAssetType(String assetType);
}

