package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.AssetProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetProductRepository extends JpaRepository<AssetProductEntity, Long> {
    Optional<AssetProductEntity> findByCode(String code);
    List<AssetProductEntity> findByCategoryIdAndPlatformId(Integer categoryId, Long platformId);
    List<AssetProductEntity> findByIsActiveTrueOrderByName();
    List<AssetProductEntity> findAllByIsActive(Boolean isActive);
    Optional<AssetProductEntity> findByIdAndIsActiveTrue(Long id);
}

