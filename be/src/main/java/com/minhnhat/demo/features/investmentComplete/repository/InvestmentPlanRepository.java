package com.minhnhat.demo.features.investmentComplete.repository;

import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentPlanRepository extends JpaRepository<InvestmentPlanEntity, Long> {

    @Query("SELECT p FROM InvestmentPlanEntity p " +
           "LEFT JOIN FETCH p.user " +
           "LEFT JOIN FETCH p.allocations " +
           "WHERE p.userId = :userId " +
           "ORDER BY p.createdAt DESC")
    List<InvestmentPlanEntity> findByUserIdWithDetails(@Param("userId") String userId);

    @Query("SELECT p FROM InvestmentPlanEntity p " +
           "LEFT JOIN FETCH p.user " +
           "LEFT JOIN FETCH p.allocations " +
           "WHERE p.id = :id")
    Optional<InvestmentPlanEntity> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT p FROM InvestmentPlanEntity p " +
           "WHERE p.userId = :userId " +
           "AND p.status = :status")
    List<InvestmentPlanEntity> findByUserIdAndStatus(
            @Param("userId") String userId,
            @Param("status") String status);
}

