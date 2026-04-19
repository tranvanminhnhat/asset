package com.minhnhat.demo.features.portfolio.repository;

import com.minhnhat.demo.features.portfolio.entity.PortfolioTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioTransactionRepository extends JpaRepository<PortfolioTransactionEntity, Long> {

    List<PortfolioTransactionEntity> findByUserId(String userId);
    
    List<PortfolioTransactionEntity> findByUserIdAndAssetType(String userId, String assetType);
    
    List<PortfolioTransactionEntity> findByAssetType(String assetType);
    
    Optional<PortfolioTransactionEntity> findByReferenceId(String referenceId);

    @Query("SELECT t FROM PortfolioTransactionEntity t " +
           "WHERE t.userId = :userId " +
           "ORDER BY t.createdAt DESC")
    List<PortfolioTransactionEntity> findByUserIdOrderByDateDesc(@Param("userId") String userId);

    @Query("SELECT t FROM PortfolioTransactionEntity t " +
           "WHERE t.userId = :userId AND t.assetType = :assetType " +
           "ORDER BY t.createdAt DESC")
    List<PortfolioTransactionEntity> findByUserIdAndAssetTypeOrderByDateDesc(
            @Param("userId") String userId,
            @Param("assetType") String assetType);
}



