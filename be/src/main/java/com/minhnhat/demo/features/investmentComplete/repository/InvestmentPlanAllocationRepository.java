package com.minhnhat.demo.features.investmentComplete.repository;

import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanAllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentPlanAllocationRepository extends JpaRepository<InvestmentPlanAllocationEntity, Long> {

}

