package com.vn.asset.repository;

import com.vn.asset.entity.AssetProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetProductRepository extends JpaRepository<AssetProduct, Long> {
    Optional<AssetProduct> findByCode(String code);
}

