package com.minhnhat.demo.features.investmentComplete.repository;

import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanAllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}

