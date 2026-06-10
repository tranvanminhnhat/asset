package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {

    List<PortfolioEntity> findByUserId(String userId);

    Optional<PortfolioEntity> findByUserIdAndAssetType(String userId, String assetType);

    List<PortfolioEntity> findByAssetType(String assetType);


}

