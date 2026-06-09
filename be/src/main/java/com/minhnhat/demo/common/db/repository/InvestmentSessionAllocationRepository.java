package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.InvestmentSessionAllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentSessionAllocationRepository extends JpaRepository<InvestmentSessionAllocationEntity, Long> {

    List<InvestmentSessionAllocationEntity> findBySessionId(Long sessionId);

    Optional<InvestmentSessionAllocationEntity> findByIdAndSessionId(Long id, Long sessionId);
}

