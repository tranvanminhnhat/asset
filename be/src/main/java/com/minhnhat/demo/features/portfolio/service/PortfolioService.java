package com.minhnhat.demo.features.portfolio.service;

import com.minhnhat.demo.common.db.entity.PortfolioEntity;
import com.minhnhat.demo.common.db.entity.PortfolioTransactionEntity;
import com.minhnhat.demo.common.db.repository.PortfolioRepository;
import com.minhnhat.demo.common.db.repository.PortfolioTransactionRepository;
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
        AddTransactionContext ctx = new AddTransactionContext(
                userId,
                transactionType,
                assetType,
                quantity,
                price,
                totalAmount,
                fee,
                tax,
                referenceId,
                note
        );

        validateRequest(ctx);
        loadData(ctx);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    private void validateRequest(AddTransactionContext ctx) {
        log.info("Recording transaction - User: {}, Type: {}, Asset: {}, Qty: {}, Price: {}",
                ctx.userId, ctx.transactionType, ctx.assetType, ctx.quantity, ctx.price);
    }

    private void loadData(AddTransactionContext ctx) {
        ctx.portfolio = getOrCreatePortfolio(ctx.userId, ctx.assetType);
    }

    private void processBusiness(AddTransactionContext ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(AddTransactionContext ctx) {
        // Keep behavior unchanged: no additional validation in current flow.
    }

    private void prepare(AddTransactionContext ctx) {
        ctx.transaction = PortfolioTransactionEntity.builder()
                .userId(ctx.userId)
                .transactionType(ctx.transactionType)
                .assetType(ctx.assetType)
                .quantity(ctx.quantity)
                .price(ctx.price)
                .totalAmount(ctx.totalAmount)
                .fee(ctx.fee != null ? ctx.fee : BigDecimal.ZERO)
                .tax(ctx.tax != null ? ctx.tax : BigDecimal.ZERO)
                .referenceId(ctx.referenceId)
                .note(ctx.note)
                .build();
    }

    private void execute(AddTransactionContext ctx) {
        if ("BUY".equals(ctx.transactionType)) {
            ctx.portfolio.setQuantity(ctx.portfolio.getQuantity().add(ctx.quantity));
            ctx.portfolio.setTotalInvested(ctx.portfolio.getTotalInvested().add(ctx.totalAmount));
        } else if ("SELL".equals(ctx.transactionType)) {
            ctx.portfolio.setQuantity(ctx.portfolio.getQuantity().subtract(ctx.quantity));
            if (ctx.portfolio.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal proportionalInvested = ctx.portfolio.getTotalInvested()
                        .multiply(ctx.quantity)
                        .divide(ctx.portfolio.getQuantity().add(ctx.quantity), 2, RoundingMode.HALF_UP);
                ctx.portfolio.setTotalInvested(ctx.portfolio.getTotalInvested().subtract(proportionalInvested));
            }
        }
    }

    private void persist(AddTransactionContext ctx) {
        ctx.savedTransaction = transactionRepository.save(ctx.transaction);
        portfolioRepository.save(ctx.portfolio);
    }

    private void afterProcess(AddTransactionContext ctx) {
        log.info("Transaction recorded with ID: {}", ctx.savedTransaction.getId());
        log.info("Portfolio {} updated", ctx.portfolio.getId());
    }

    private PortfolioTransactionEntity createResponse(AddTransactionContext ctx) {
        return ctx.savedTransaction;
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

    private static class AddTransactionContext {
        final String userId;
        final String transactionType;
        final String assetType;
        final BigDecimal quantity;
        final BigDecimal price;
        final BigDecimal totalAmount;
        final BigDecimal fee;
        final BigDecimal tax;
        final String referenceId;
        final String note;
        PortfolioEntity portfolio;
        PortfolioTransactionEntity transaction;
        PortfolioTransactionEntity savedTransaction;

        AddTransactionContext(
                String userId,
                String transactionType,
                String assetType,
                BigDecimal quantity,
                BigDecimal price,
                BigDecimal totalAmount,
                BigDecimal fee,
                BigDecimal tax,
                String referenceId,
                String note
        ) {
            this.userId = userId;
            this.transactionType = transactionType;
            this.assetType = assetType;
            this.quantity = quantity;
            this.price = price;
            this.totalAmount = totalAmount;
            this.fee = fee;
            this.tax = tax;
            this.referenceId = referenceId;
            this.note = note;
        }
    }
}

