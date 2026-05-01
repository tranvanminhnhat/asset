package com.minhnhat.demo.features.investmentRecommendations.service;

import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investmentRecommendations.constant.InvestmentRecommendationsConstant;
import com.minhnhat.demo.features.investmentRecommendations.dto.request.InvestmentRecommendationsRequest;
import com.minhnhat.demo.features.investmentRecommendations.dto.response.InvestmentRecommendationsResponse;
import com.minhnhat.demo.features.investmentRecommendations.repository.AssetResidualsRepository;
import com.minhnhat.demo.features.master.entity.AllocationStrategyEntity;
import com.minhnhat.demo.features.master.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.features.master.repository.AllocationStrategyRepository;
import com.minhnhat.demo.features.master.repository.AllocationStrategyDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentRecommendationsService {

    private final AllocationStrategyRepository strategyRepository;
    
    private final AllocationStrategyDetailRepository detailRepository;

    private final AssetResidualsRepository assetResidualsRepository;
    
    private final SecurityUtils securityUtils;
    
    private final MessageService messageService;

    private static final String SERVICE_NAME = "InvestmentRecommendationsService";

    public InvestmentRecommendationsResponse allocate(InvestmentRecommendationsRequest request) {
        validateRequest(request);
        Context ctx = loadData(request);
        executeBusinessLogic(ctx);
        return createResponse(ctx);
    }

    private void validateRequest(InvestmentRecommendationsRequest request) {
        if (request == null) {
            log.error("Allocation validation failed: request is null");
            throw new LogicException(SERVICE_NAME, messageService.get(InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001), InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001);
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Allocation validation failed: invalid amount={}", request.getAmount());
            throw new LogicException(SERVICE_NAME, messageService.get(InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001), InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001);
        }

        if (request.getType() == null || request.getType().trim().isEmpty()) {
            log.error("Allocation validation failed: type is empty");
            throw new LogicException(SERVICE_NAME, messageService.get(InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001), InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001);
        }
    }

    private Context loadData(InvestmentRecommendationsRequest request) {
        String type = request.getType().trim();
        Context ctx = new Context();
        ctx.request = request;
        ctx.code = type;

        // Get current user ID
        ctx.userId = securityUtils.getCurrentUser().getUserId();

        // Load strategy by code from mst_allocation_strategies table
        Optional<AllocationStrategyEntity> strategyOpt = strategyRepository.findByCode(type);
        if (strategyOpt.isEmpty()) {
            log.error("Allocation validation failed: strategy config not found for code={}", type);
            throw new LogicException(SERVICE_NAME, messageService.get(InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001), InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001);
        }

        AllocationStrategyEntity strategy = strategyOpt.get();
        
        // Load strategy details by strategy_id from mst_allocation_strategy_details table
        List<AllocationStrategyDetailEntity> details = detailRepository.findByStrategyId(strategy.getId());
        if (details == null || details.isEmpty()) {
            log.error("Allocation validation failed: no details found for strategy id={}", strategy.getId());
            throw new LogicException(SERVICE_NAME, messageService.get(InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001), InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001);
        }


        ctx.strategy = strategy;
        ctx.details = details;
        return ctx;
    }

    private void executeBusinessLogic(Context ctx) {
        // Extract percentages from strategy details by asset_type
        BigDecimal pEmergency = BigDecimal.ZERO;
        BigDecimal pGold = BigDecimal.ZERO;
        BigDecimal pEtf = BigDecimal.ZERO;
        BigDecimal pStock = BigDecimal.ZERO;

        for (AllocationStrategyDetailEntity detail : ctx.details) {
            String assetType = detail.getAssetType();
            BigDecimal percentage = nonNull(detail.getPercentage());
            
            if ("EMERGENCY".equalsIgnoreCase(assetType)) {
                pEmergency = percentage;
            } else if ("GOLD".equalsIgnoreCase(assetType)) {
                pGold = percentage;
            } else if ("ETF".equalsIgnoreCase(assetType)) {
                pEtf = percentage;
            } else if ("STOCK".equalsIgnoreCase(assetType)) {
                pStock = percentage;
            }
        }

        BigDecimal sum = pEmergency.add(pGold).add(pEtf).add(pStock);
        if (sum.compareTo(BigDecimal.ZERO) == 0) {
            log.error("Master allocation percents sum to zero for type={}", ctx.code);
            throw new LogicException(SERVICE_NAME, messageService.get(InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001), InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001);
        }

        // normalize if sum != 100 (or 1 if stored as decimal)
        if (sum.compareTo(new BigDecimal("100")) != 0 && sum.compareTo(BigDecimal.ONE) != 0) {
            pEmergency = pEmergency.divide(sum, 8, RoundingMode.HALF_UP);
            pGold = pGold.divide(sum, 8, RoundingMode.HALF_UP);
            pEtf = pEtf.divide(sum, 8, RoundingMode.HALF_UP);
            pStock = pStock.divide(sum, 8, RoundingMode.HALF_UP);
            log.info("Normalized percents for type={} from sum={} to 1", ctx.code, sum);
        }

        // If percentages are stored as 0-100, convert to 0-1
        if (sum.compareTo(new BigDecimal("100")) == 0) {
            pEmergency = pEmergency.divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP);
            pGold = pGold.divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP);
            pEtf = pEtf.divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP);
            pStock = pStock.divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP);
        }

        BigDecimal total = ctx.request.getAmount();
        // compute amounts with 2 decimal places, adjust last item to ensure total equality
        BigDecimal amtEmergency = total.multiply(pEmergency).setScale(2, RoundingMode.HALF_UP);
        BigDecimal amtGold = total.multiply(pGold).setScale(2, RoundingMode.HALF_UP);
        BigDecimal amtEtf = total.multiply(pEtf).setScale(2, RoundingMode.HALF_UP);
        BigDecimal sumFirstThree = amtEmergency.add(amtGold).add(amtEtf);
        BigDecimal amtStock = total.subtract(sumFirstThree).setScale(2, RoundingMode.HALF_UP);

        // in rare case rounding makes negative, fix by setting stock to zero and adjust
        if (amtStock.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Rounding adjustment produced negative stock amount; setting to zero and recalculating last item. type={}", ctx.code);
            amtStock = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            // adjust etf to absorb difference
            BigDecimal diff = total.subtract(amtEmergency).subtract(amtGold).subtract(amtStock);
            amtEtf = diff.setScale(2, RoundingMode.HALF_UP);
        }

        ctx.percentEmergency = pEmergency;
        ctx.percentGold = pGold;
        ctx.percentEtf = pEtf;
        ctx.percentStock = pStock;

        ctx.amountEmergency = amtEmergency;
        ctx.amountGold = amtGold;
        ctx.amountEtf = amtEtf;
        ctx.amountStock = amtStock;

        // Load carry over amounts from AssetResidualsRepository based on user_id and asset_type
        ctx.carryOverEmergency = loadCarryOverAmount(ctx.userId, "EMERGENCY");
        ctx.carryOverGold = loadCarryOverAmount(ctx.userId, "GOLD");
        ctx.carryOverEtf = loadCarryOverAmount(ctx.userId, "ETF");
        ctx.carryOverStock = loadCarryOverAmount(ctx.userId, "STOCK");
    }

    private InvestmentRecommendationsResponse createResponse(Context ctx) {
        List<InvestmentRecommendationsResponse.Allocation> allocations = new ArrayList<>();
        // use 'type' field in response to map with DB keys for the allocation buckets
        allocations.add(InvestmentRecommendationsResponse.Allocation.builder()
                .type("EMERGENCY")
                .percent(strip(ctx.percentEmergency))
                .amount(strip(ctx.amountEmergency))
                .carryOverAmount(strip(ctx.carryOverEmergency))
                .build());
        allocations.add(InvestmentRecommendationsResponse.Allocation.builder()
                .type("GOLD")
                .percent(strip(ctx.percentGold))
                .amount(strip(ctx.amountGold))
                .carryOverAmount(strip(ctx.carryOverGold))
                .build());
        allocations.add(InvestmentRecommendationsResponse.Allocation.builder()
                .type("ETF")
                .percent(strip(ctx.percentEtf))
                .amount(strip(ctx.amountEtf))
                .carryOverAmount(strip(ctx.carryOverEtf))
                .build());
        allocations.add(InvestmentRecommendationsResponse.Allocation.builder()
                .type("STOCK")
                .percent(strip(ctx.percentStock))
                .amount(strip(ctx.amountStock))
                .carryOverAmount(strip(ctx.carryOverStock))
                .build());

        log.info("Allocation computed from master type={} total={} => sumPercents={} sumAmounts={}", ctx.code, ctx.request.getAmount(), ctx.percentEmergency.add(ctx.percentGold).add(ctx.percentEtf).add(ctx.percentStock), ctx.amountEmergency.add(ctx.amountGold).add(ctx.amountEtf).add(ctx.amountStock));

        return InvestmentRecommendationsResponse.builder()
                .allocations(allocations)
                .build();
    }

    private static class Context {
        InvestmentRecommendationsRequest request;
        String code;
        String userId;
        AllocationStrategyEntity strategy;
        List<AllocationStrategyDetailEntity> details;

        BigDecimal percentEmergency;
        BigDecimal percentGold;
        BigDecimal percentEtf;
        BigDecimal percentStock;

        BigDecimal amountEmergency;
        BigDecimal amountGold;
        BigDecimal amountEtf;
        BigDecimal amountStock;

        BigDecimal carryOverEmergency;
        BigDecimal carryOverGold;
        BigDecimal carryOverEtf;
        BigDecimal carryOverStock;
    }

    private BigDecimal nonNull(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private BigDecimal loadCarryOverAmount(String userId, String assetType) {
        try {
            return assetResidualsRepository.findByUserIdAndAssetType(userId, assetType)
                    .map(residual -> residual.getResidualAmount())
                    .orElse(BigDecimal.ZERO);
        } catch (Exception e) {
            log.warn("Error loading carry over amount for userId={} assetType={}: {}", userId, assetType, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal strip(BigDecimal v) {
        if (v == null) return null;
        try {
            BigDecimal s = v.stripTrailingZeros();
            // if value is an integer (scale <= 0) return without decimal places
            return new BigDecimal(s.toPlainString());
        } catch (Exception ex) {
            return v;
        }
    }
}







