package com.vn.asset.repository;

import com.vn.asset.entity.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Integer> {
    Optional<AssetCategory> findByCode(String code);
}

