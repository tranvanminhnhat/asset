package com.minhnhat.demo.features.investmentComplete.repository;

import com.minhnhat.demo.features.investmentComplete.entity.InvestmentPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentPlanRepository extends JpaRepository<InvestmentPlanEntity, Long> {

}

