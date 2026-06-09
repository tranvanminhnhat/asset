package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.InvestmentSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentSessionRepository extends JpaRepository<InvestmentSessionEntity, Long> {

    List<InvestmentSessionEntity> findByUserId(String userId);

    Optional<InvestmentSessionEntity> findByIdAndUserId(Long id, String userId);

    @Query("SELECT s FROM InvestmentSessionEntity s LEFT JOIN FETCH s.allocations WHERE s.id = :id")
    Optional<InvestmentSessionEntity> findByIdWithAllocations(@Param("id") Long id);
}

