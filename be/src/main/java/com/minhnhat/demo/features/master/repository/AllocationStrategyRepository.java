package com.minhnhat.demo.features.master.repository;

import com.minhnhat.demo.features.master.entity.AllocationStrategyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllocationStrategyRepository extends JpaRepository<AllocationStrategyEntity, Long> {

    Optional<AllocationStrategyEntity> findByCode(String code);

    @Query("SELECT s FROM AllocationStrategyEntity s " +
           "LEFT JOIN FETCH s.details " +
           "WHERE s.id = :id")
    Optional<AllocationStrategyEntity> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT s FROM AllocationStrategyEntity s " +
           "LEFT JOIN FETCH s.details " +
           "WHERE s.code = :code")
    Optional<AllocationStrategyEntity> findByCodeWithDetails(@Param("code") String code);
}

