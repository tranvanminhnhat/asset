package com.minhnhat.demo.features.portfolio.service;

import com.minhnhat.demo.common.db.entity.PortfolioEntity;
import com.minhnhat.demo.common.db.entity.PortfolioTransactionEntity;
import com.minhnhat.demo.common.db.repository.PortfolioRepository;
import com.minhnhat.demo.common.db.repository.PortfolioTransactionRepository;
import com.minhnhat.demo.features.portfolio.dto.response.PortfolioTransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddPortfolioTransactionService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioTransactionRepository transactionRepository;

    @Transactional(rollbackFor = Exception.class)
    public PortfolioTransactionResponse execute(
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
        Context ctx = new Context(
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

    private void validateRequest(Context ctx) {
        log.info("Recording transaction - User: {}, Type: {}, Asset: {}, Qty: {}, Price: {}",
                ctx.userId, ctx.transactionType, ctx.assetType, ctx.quantity, ctx.price);
    }

    private void loadData(Context ctx) {
        ctx.portfolio = portfolioRepository.findByUserIdAndAssetType(ctx.userId, ctx.assetType)
                .orElseGet(() -> {
                    log.info("Creating new portfolio for user: {}, assetType: {}", ctx.userId, ctx.assetType);
                    PortfolioEntity portfolio = PortfolioEntity.builder()
                            .userId(ctx.userId)
                            .assetType(ctx.assetType)
                            .quantity(BigDecimal.ZERO)
                            .totalInvested(BigDecimal.ZERO)
                            .build();
                    return portfolioRepository.save(portfolio);
                });
    }

    private void processBusiness(Context ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(Context ctx) {
        // Keep behavior unchanged.
    }

    private void prepare(Context ctx) {
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

    private void execute(Context ctx) {
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

    private void persist(Context ctx) {
        ctx.savedTransaction = transactionRepository.save(ctx.transaction);
        portfolioRepository.save(ctx.portfolio);
    }

    private void afterProcess(Context ctx) {
        log.info("Transaction recorded with ID: {}", ctx.savedTransaction.getId());
        log.info("Portfolio {} updated", ctx.portfolio.getId());
    }

    private PortfolioTransactionResponse createResponse(Context ctx) {
        return PortfolioTransactionResponse.builder()
                .transactionId(ctx.savedTransaction.getId())
                .transactionType(ctx.savedTransaction.getTransactionType())
                .assetType(ctx.savedTransaction.getAssetType())
                .quantity(ctx.savedTransaction.getQuantity())
                .price(ctx.savedTransaction.getPrice())
                .totalAmount(ctx.savedTransaction.getTotalAmount())
                .fee(ctx.savedTransaction.getFee())
                .tax(ctx.savedTransaction.getTax())
                .referenceId(ctx.savedTransaction.getReferenceId())
                .note(ctx.savedTransaction.getNote())
                .createdAt(ctx.savedTransaction.getCreatedAt())
                .build();
    }

    private static class Context {
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

        Context(String userId, String transactionType, String assetType, BigDecimal quantity, BigDecimal price,
                BigDecimal totalAmount, BigDecimal fee, BigDecimal tax, String referenceId, String note) {
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

