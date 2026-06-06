package com.minhnhat.demo.features.portfolio.service;

import com.minhnhat.demo.features.portfolio.entity.PortfolioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetPortfolioByIdUseCaseService {

    private final PortfolioService portfolioService;

    public Optional<PortfolioEntity> execute(Long portfolioId) {
        return portfolioService.getPortfolioById(portfolioId);
    }
}

