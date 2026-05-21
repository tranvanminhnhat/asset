package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.AssetResidualsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetResidualsRepository extends JpaRepository<AssetResidualsEntity, Long> {

    Optional<AssetResidualsEntity> findByUserIdAndAssetType(String userId, String assetType);
}

