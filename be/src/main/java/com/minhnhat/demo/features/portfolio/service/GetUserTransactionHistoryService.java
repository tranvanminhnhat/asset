package com.minhnhat.demo.features.portfolio.service;

import com.minhnhat.demo.common.db.entity.PortfolioTransactionEntity;
import com.minhnhat.demo.common.db.repository.PortfolioTransactionRepository;
import com.minhnhat.demo.features.portfolio.dto.response.PortfolioTransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserTransactionHistoryService {

    private final PortfolioTransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<PortfolioTransactionResponse> execute(String userId) {
        log.debug("Fetching transaction history for user: {}", userId);
        return transactionRepository.findByUserIdOrderByDateDesc(userId).stream()
                .map(this::createResponse)
                .toList();
    }

    private PortfolioTransactionResponse createResponse(PortfolioTransactionEntity entity) {
        return PortfolioTransactionResponse.builder()
                .transactionId(entity.getId())
                .transactionType(entity.getTransactionType())
                .assetType(entity.getAssetType())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .totalAmount(entity.getTotalAmount())
                .fee(entity.getFee())
                .tax(entity.getTax())
                .referenceId(entity.getReferenceId())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

