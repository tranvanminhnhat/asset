package com.minhnhat.demo.features.portfolio.repository;

import com.minhnhat.demo.features.portfolio.entity.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<PortfolioEntity, String> {

    List<PortfolioEntity> findByUserId(String nnn);
}
