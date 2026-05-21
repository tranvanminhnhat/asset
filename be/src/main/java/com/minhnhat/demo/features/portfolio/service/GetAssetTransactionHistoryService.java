package com.minhnhat.demo.features.portfolio.service;

import com.minhnhat.demo.common.db.entity.PortfolioTransactionEntity;
import com.minhnhat.demo.common.db.repository.PortfolioTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAssetTransactionHistoryService {

    private final PortfolioTransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<PortfolioTransactionEntity> execute(String userId, String assetType) {
        log.debug("Fetching transaction history for user: {}, asset: {}", userId, assetType);
        return transactionRepository.findByUserIdAndAssetTypeOrderByDateDesc(userId, assetType);
    }
}

