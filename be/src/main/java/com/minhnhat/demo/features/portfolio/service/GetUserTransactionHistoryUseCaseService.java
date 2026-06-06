package com.minhnhat.demo.features.portfolio.service;

import com.minhnhat.demo.features.portfolio.entity.PortfolioTransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserTransactionHistoryUseCaseService {

    private final PortfolioService portfolioService;

    public List<PortfolioTransactionEntity> execute(String userId) {
        return portfolioService.getUserTransactionHistory(userId);
    }
}

