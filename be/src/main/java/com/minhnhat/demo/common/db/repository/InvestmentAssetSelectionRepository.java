package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.InvestmentAssetSelectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentAssetSelectionRepository extends JpaRepository<InvestmentAssetSelectionEntity, Long> {

    List<InvestmentAssetSelectionEntity> findByAllocationId(Long allocationId);

    List<InvestmentAssetSelectionEntity> findBySessionId(Long sessionId);

    /**
     * Find all product selections by assetType/categoryCode
     * Used for dashboard to get current prices of selected products
     */
    @Query("SELECT s FROM InvestmentAssetSelectionEntity s WHERE s.categoryCode = :assetType")
    List<InvestmentAssetSelectionEntity> findByAssetType(@Param("assetType") String assetType);
}

