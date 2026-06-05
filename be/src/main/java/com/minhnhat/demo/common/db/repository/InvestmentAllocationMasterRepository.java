package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.InvestmentAllocationMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestmentAllocationMasterRepository extends JpaRepository<InvestmentAllocationMasterEntity, String> {
    Optional<InvestmentAllocationMasterEntity> findFirstByCode(String code);
}


