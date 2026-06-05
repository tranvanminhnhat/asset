package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.InvestmentPlanAllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface InvestmentPlanAllocationRepository extends JpaRepository<InvestmentPlanAllocationEntity, Long> {

    /**
     * Find all allocations for a plan
     */
    List<InvestmentPlanAllocationEntity> findByPlanId(Long planId);

    /**
     * Find allocations with plan details (LEFT JOIN FETCH to avoid N+1)
     */
    @Query("SELECT a FROM InvestmentPlanAllocationEntity a " +
           "LEFT JOIN FETCH a.plan " +
           "WHERE a.planId = :planId")
    List<InvestmentPlanAllocationEntity> findByPlanIdWithDetails(@Param("planId") Long planId);

    /**
     * Optimized query for Dashboard - aggregates allocation data per asset type
     * Joins with asset prices to get current market price
     *
     * Returns: assetType, totalQuantity, totalAmount, currentPrice
     * GROUP BY assetType to aggregate across all plans
     */
    @Query(value = "SELECT " +
            "ipa.asset_type AS assetType, " +
            "SUM(ipa.quantity) AS totalQuantity, " +
            "SUM(ipa.amount) AS totalAmount, " +
            "MAX(ap.buy_price) AS currentPrice " +
            "FROM investment_plan_allocations ipa " +
            "JOIN investment_plans ip ON ipa.plan_id = ip.id " +
            "LEFT JOIN asset_prices ap ON ipa.asset_type = ap.asset_type " +
            "WHERE ip.user_id = :userId " +
            "GROUP BY ipa.asset_type",
            nativeQuery = true)
List<Map<String, Object>> findDashboardDataByUserId(@Param("userId") String userId);
}


