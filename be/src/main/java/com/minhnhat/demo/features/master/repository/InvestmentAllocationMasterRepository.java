package com.minhnhat.demo.features.master.repository;

import com.minhnhat.demo.features.master.entity.InvestmentAllocationMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestmentAllocationMasterRepository extends JpaRepository<com.minhnhat.demo.features.master.entity.InvestmentAllocationMasterEntity, String> {
    Optional<InvestmentAllocationMasterEntity> findFirstByCode(String code);
}

