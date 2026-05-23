package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllocationStrategyDetailRepository extends JpaRepository<AllocationStrategyDetailEntity, Long> {

    List<AllocationStrategyDetailEntity> findByStrategyId(Long strategyId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM AllocationStrategyDetailEntity d WHERE d.strategyId = :strategyId")
    void deleteByStrategyId(@Param("strategyId") Long strategyId);
}


