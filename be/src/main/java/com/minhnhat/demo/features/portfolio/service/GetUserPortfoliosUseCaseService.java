package com.minhnhat.demo.features.portfolio.service;

import com.minhnhat.demo.features.portfolio.entity.PortfolioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserPortfoliosUseCaseService {

    private final PortfolioService portfolioService;

    public List<PortfolioEntity> execute(String userId) {
        return portfolioService.getUserPortfolios(userId);
    }
}

