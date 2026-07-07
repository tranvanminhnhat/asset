package com.vn.asset.repository;

import com.vn.asset.entity.AssetPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetPlatformRepository extends JpaRepository<AssetPlatform, Long> {
    Optional<AssetPlatform> findByCode(String code);
}

