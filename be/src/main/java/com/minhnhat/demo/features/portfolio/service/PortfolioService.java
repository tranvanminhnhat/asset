package com.minhnhat.demo.features.portfolio.service;

import com.minhnhat.demo.features.portfolio.entity.PortfolioEntity;
import com.minhnhat.demo.features.portfolio.entity.PortfolioTransactionEntity;
import com.minhnhat.demo.features.portfolio.repository.PortfolioRepository;
import com.minhnhat.demo.features.portfolio.repository.PortfolioTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Portfolio Service - Business logic for portfolio management
 * Handles:
 * - Portfolio CRUD operations
 * - Portfolio summaries
 * - Transaction recording (append-only)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioTransactionRepository transactionRepository;

    /**
     * Get portfolio by ID
     */
    @Transactional(readOnly = true)
    public Optional<PortfolioEntity> getPortfolioById(Long portfolioId) {
        log.debug("Fetching portfolio with ID: {}", portfolioId);
        return portfolioRepository.findById(portfolioId);
    }

    /**
     * Get all portfolios for a user
     */
    @Transactional(readOnly = true)
    public List<PortfolioEntity> getUserPortfolios(String userId) {
        log.debug("Fetching portfolios for user: {}", userId);
        return portfolioRepository.findByUserId(userId);
    }

    /**
     * Get or create portfolio for user and asset type
     */
    @Transactional
    public PortfolioEntity getOrCreatePortfolio(String userId, String assetType) {
        return portfolioRepository.findByUserIdAndAssetType(userId, assetType)
                .orElseGet(() -> {
                    log.info("Creating new portfolio for user: {}, assetType: {}", userId, assetType);
                    PortfolioEntity portfolio = PortfolioEntity.builder()
                            .userId(userId)
                            .assetType(assetType)
                            .quantity(BigDecimal.ZERO)
                            .totalInvested(BigDecimal.ZERO)
                            .build();
                    return portfolioRepository.save(portfolio);
                });
    }

    /**
     * Add transaction to portfolio (append-only)
     * Updates portfolio quantity and totalInvested after recording transaction
     */
    @Transactional(rollbackFor = Exception.class)
    public PortfolioTransactionEntity addTransaction(
            String userId,
            String transactionType,
            String assetType,
            BigDecimal quantity,
            BigDecimal price,
            BigDecimal totalAmount,
            BigDecimal fee,
            BigDecimal tax,
            String referenceId,
            String note) {

        log.info("Recording transaction - User: {}, Type: {}, Asset: {}, Qty: {}, Price: {}",
                userId, transactionType, assetType, quantity, price);

        // Get or create portfolio
        PortfolioEntity portfolio = getOrCreatePortfolio(userId, assetType);

        // Create transaction record (immutable)
        PortfolioTransactionEntity transaction = PortfolioTransactionEntity.builder()
                .userId(userId)
                .transactionType(transactionType)
                .assetType(assetType)
                .quantity(quantity)
                .price(price)
                .totalAmount(totalAmount)
                .fee(fee != null ? fee : BigDecimal.ZERO)
                .tax(tax != null ? tax : BigDecimal.ZERO)
                .referenceId(referenceId)
                .note(note)
                .build();

        // Persist transaction
        PortfolioTransactionEntity saved = transactionRepository.save(transaction);
        log.info("Transaction recorded with ID: {}", saved.getId());

        // Update portfolio based on transaction type
        if ("BUY".equals(transactionType)) {
            portfolio.setQuantity(portfolio.getQuantity().add(quantity));
            portfolio.setTotalInvested(portfolio.getTotalInvested().add(totalAmount));
        } else if ("SELL".equals(transactionType)) {
            portfolio.setQuantity(portfolio.getQuantity().subtract(quantity));
            // For selling, we reduce the proportional invested amount
            if (portfolio.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal proportionalInvested = portfolio.getTotalInvested()
                        .multiply(quantity)
                        .divide(portfolio.getQuantity().add(quantity), 2, RoundingMode.HALF_UP);
                portfolio.setTotalInvested(portfolio.getTotalInvested().subtract(proportionalInvested));
            }
        }
        
        portfolioRepository.save(portfolio);
        log.info("Portfolio {} updated", portfolio.getId());

        return saved;
    }

    /**
     * Get transaction history for user
     */
    @Transactional(readOnly = true)
    public List<PortfolioTransactionEntity> getUserTransactionHistory(String userId) {
        log.debug("Fetching transaction history for user: {}", userId);
        return transactionRepository.findByUserIdOrderByDateDesc(userId);
    }

    /**
     * Get transaction history for specific asset
     */
    @Transactional(readOnly = true)
    public List<PortfolioTransactionEntity> getAssetTransactionHistory(String userId, String assetType) {
        log.debug("Fetching transaction history for user: {}, asset: {}", userId, assetType);
        return transactionRepository.findByUserIdAndAssetTypeOrderByDateDesc(userId, assetType);
    }
}

