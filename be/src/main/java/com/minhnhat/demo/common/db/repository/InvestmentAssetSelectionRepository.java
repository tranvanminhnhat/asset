package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.InvestmentAssetSelectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentAssetSelectionRepository extends JpaRepository<InvestmentAssetSelectionEntity, Long> {

    List<InvestmentAssetSelectionEntity> findByAllocationId(Long allocationId);

    List<InvestmentAssetSelectionEntity> findBySessionId(Long sessionId);
}

