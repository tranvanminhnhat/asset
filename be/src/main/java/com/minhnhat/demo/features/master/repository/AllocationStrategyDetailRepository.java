package com.minhnhat.demo.features.master.repository;

import com.minhnhat.demo.features.master.entity.AllocationStrategyDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllocationStrategyDetailRepository extends JpaRepository<AllocationStrategyDetailEntity, Long> {

    List<AllocationStrategyDetailEntity> findByStrategyId(Long strategyId);
}

