package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.AssetPlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetPlatformRepository extends JpaRepository<AssetPlatformEntity, Long> {
    Optional<AssetPlatformEntity> findByCode(String code);
    List<AssetPlatformEntity> findAllByIsActiveTrueOrderByName();
    List<AssetPlatformEntity> findAllByIsActive(Boolean isActive);
}

