package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationStrategyRepository extends JpaRepository<AllocationStrategyEntity, Long> {

    @Query("SELECT s FROM AllocationStrategyEntity s LEFT JOIN FETCH s.details")
    List<AllocationStrategyEntity> findAllWithDetails();

    Optional<AllocationStrategyEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    @Query("SELECT s FROM AllocationStrategyEntity s " +
           "LEFT JOIN FETCH s.details " +
           "WHERE s.id = :id")
    Optional<AllocationStrategyEntity> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT s FROM AllocationStrategyEntity s " +
           "LEFT JOIN FETCH s.details " +
           "WHERE s.code = :code")
    Optional<AllocationStrategyEntity> findByCodeWithDetails(@Param("code") String code);
}
