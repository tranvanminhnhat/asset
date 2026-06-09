package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.InvestmentSessionEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentSessionEventRepository extends JpaRepository<InvestmentSessionEventEntity, Long> {

    List<InvestmentSessionEventEntity> findBySessionId(Long sessionId);
}

