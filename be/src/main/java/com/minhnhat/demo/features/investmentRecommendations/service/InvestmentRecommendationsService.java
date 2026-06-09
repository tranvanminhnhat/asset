package com.minhnhat.demo.features.investmentRecommendations.service;

import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.features.investmentRecommendations.constant.InvestmentRecommendationsConstant;
import com.minhnhat.demo.features.investmentRecommendations.dto.request.InvestmentRecommendationsRequest;
import com.minhnhat.demo.features.investmentRecommendations.dto.response.InvestmentRecommendationsResponse;
import com.minhnhat.demo.common.db.entity.AllocationStrategyDetailEntity;
import com.minhnhat.demo.common.db.entity.AllocationStrategyEntity;
import com.minhnhat.demo.common.db.entity.AssetPriceEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyDetailRepository;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.common.db.repository.AssetPriceRepository;
import com.minhnhat.demo.common.db.repository.AssetResidualsRepository;
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

    private final AssetPriceRepository assetPriceRepository;

    private final AssetResidualsRepository assetResidualsRepository;
    
    private final SecurityUtils securityUtils;
    
    private final MessageService messageService;

    private static final String SERVICE_NAME = "InvestmentRecommendationsService";

    public InvestmentRecommendationsResponse allocate(InvestmentRecommendationsRequest request) {
        validateRequest(request);
        Context ctx = loadData(request);
        processBusiness(ctx);
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
        ctx.strategy = strategyOpt.orElse(null);
        if (ctx.strategy != null) {
            // Load strategy details by strategy_id from mst_allocation_strategy_details table
            ctx.details = detailRepository.findByStrategyId(ctx.strategy.getId());
        }
        return ctx;
    }

    private void processBusiness(Context ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(Context ctx) {
        if (ctx.strategy == null) {
            log.error("Allocation validation failed: strategy config not found for code={}", ctx.code);
            throw new LogicException(SERVICE_NAME, messageService.get(InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001), InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001);
        }

        if (ctx.details == null || ctx.details.isEmpty()) {
            log.error("Allocation validation failed: no details found for strategy id={}", ctx.strategy.getId());
            throw new LogicException(SERVICE_NAME, messageService.get(InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001), InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001);
        }
    }

    private void prepare(Context ctx) {
        // Extract percentages, minimumLotSizes, and asset prices from strategy details by asset_type
        BigDecimal pEmergency = BigDecimal.ZERO;
        BigDecimal pGold = BigDecimal.ZERO;
        BigDecimal pEtf = BigDecimal.ZERO;
        BigDecimal pStock = BigDecimal.ZERO;

        BigDecimal minLotEmergency = BigDecimal.ZERO;
        BigDecimal minLotGold = BigDecimal.ZERO;
        BigDecimal minLotEtf = BigDecimal.ZERO;
        BigDecimal minLotStock = BigDecimal.ZERO;

        for (AllocationStrategyDetailEntity detail : ctx.details) {
            String assetType = detail.getAssetType();
            BigDecimal percentage = nonNull(detail.getPercentage());
            BigDecimal minimumLotSize = nonNull(detail.getMinimumLotSizeAmount());

            if ("EMERGENCY".equalsIgnoreCase(assetType)) {
                pEmergency = percentage;
                minLotEmergency = minimumLotSize;
            } else if ("GOLD".equalsIgnoreCase(assetType)) {
                pGold = percentage;
                minLotGold = minimumLotSize;
            } else if ("ETF".equalsIgnoreCase(assetType)) {
                pEtf = percentage;
                minLotEtf = minimumLotSize;
            } else if ("STOCK".equalsIgnoreCase(assetType)) {
                pStock = percentage;
                minLotStock = minimumLotSize;
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

        ctx.minimumLotEmergency = minLotEmergency;
        ctx.minimumLotGold = minLotGold;
        ctx.minimumLotEtf = minLotEtf;
        ctx.minimumLotStock = minLotStock;
    }

    private void execute(Context ctx) {
        // Calculate investable and residual amounts
        ctx.carryOverEmergency = loadCarryOverAmount(ctx.userId, "EMERGENCY");
        ctx.carryOverGold = loadCarryOverAmount(ctx.userId, "GOLD");
        ctx.carryOverEtf = loadCarryOverAmount(ctx.userId, "ETF");
        ctx.carryOverStock = loadCarryOverAmount(ctx.userId, "STOCK");

        ctx.priceEmergency = loadAssetPrice("EMERGENCY");
        ctx.priceGold = loadAssetPrice("GOLD");
        ctx.priceEtf = loadAssetPrice("ETF");
        ctx.priceStock = loadAssetPrice("STOCK");

        calculateInvestableAndResidual(ctx, "EMERGENCY");
        calculateInvestableAndResidual(ctx, "GOLD");
        calculateInvestableAndResidual(ctx, "ETF");
        calculateInvestableAndResidual(ctx, "STOCK");
    }

    private void persist(Context ctx) {
        // Read-only flow; no persistence in recommendations.
    }

    private void afterProcess(Context ctx) {
        // No side effects for this flow.
    }

    private InvestmentRecommendationsResponse createResponse(Context ctx) {
        List<InvestmentRecommendationsResponse.Allocation> allocations = new ArrayList<>();
        // use 'type' field in response to map with DB keys for the allocation buckets
        allocations.add(InvestmentRecommendationsResponse.Allocation.builder()
                .type("EMERGENCY")
                .percent(strip(ctx.percentEmergency))
                .amount(strip(ctx.amountEmergency))
                .carryOverAmount(strip(ctx.carryOverEmergency))
                .investableAmount(strip(ctx.investableEmergency))
                .residualAmount(strip(ctx.residualEmergency))
                .build());
        allocations.add(InvestmentRecommendationsResponse.Allocation.builder()
                .type("GOLD")
                .percent(strip(ctx.percentGold))
                .amount(strip(ctx.amountGold))
                .carryOverAmount(strip(ctx.carryOverGold))
                .investableAmount(strip(ctx.investableGold))
                .residualAmount(strip(ctx.residualGold))
                .build());
        allocations.add(InvestmentRecommendationsResponse.Allocation.builder()
                .type("ETF")
                .percent(strip(ctx.percentEtf))
                .amount(strip(ctx.amountEtf))
                .carryOverAmount(strip(ctx.carryOverEtf))
                .investableAmount(strip(ctx.investableEtf))
                .residualAmount(strip(ctx.residualEtf))
                .build());
        allocations.add(InvestmentRecommendationsResponse.Allocation.builder()
                .type("STOCK")
                .percent(strip(ctx.percentStock))
                .amount(strip(ctx.amountStock))
                .carryOverAmount(strip(ctx.carryOverStock))
                .investableAmount(strip(ctx.investableStock))
                .residualAmount(strip(ctx.residualStock))
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

        // Minimum lot size amounts for each asset type
        BigDecimal minimumLotEmergency;
        BigDecimal minimumLotGold;
        BigDecimal minimumLotEtf;
        BigDecimal minimumLotStock;

        // Asset prices for each type
        BigDecimal priceEmergency;
        BigDecimal priceGold;
        BigDecimal priceEtf;
        BigDecimal priceStock;

        // Calculated investable and residual amounts
        BigDecimal investableEmergency;
        BigDecimal investableGold;
        BigDecimal investableEtf;
        BigDecimal investableStock;

        BigDecimal residualEmergency;
        BigDecimal residualGold;
        BigDecimal residualEtf;
        BigDecimal residualStock;
    }

    private BigDecimal nonNull(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    /**
     * Tính investableAmount và residualAmount dựa vào minimumLotSizeAmount và giá tài sản
     * Logic:
     * - totalAmount = amount + carryOverAmount
     * - minimumAmount = minimumLotSizeAmount * price
     * - Nếu totalAmount < minimumAmount: investable = 0, residual = totalAmount
     * - Nếu totalAmount >= minimumAmount: số lần đầu tư = floor(totalAmount / minimumAmount)
     *   investable = số lần * minimumAmount, residual = totalAmount - investable
     */
    private void calculateInvestableAndResidual(Context ctx, String assetType) {
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal carryOver = BigDecimal.ZERO;
        BigDecimal minimumLot = BigDecimal.ZERO;
        BigDecimal price = BigDecimal.ZERO;

        switch (assetType.toUpperCase()) {
            case "EMERGENCY":
                amount = ctx.amountEmergency;
                carryOver = ctx.carryOverEmergency;
                minimumLot = ctx.minimumLotEmergency;
                price = ctx.priceEmergency;
                break;
            case "GOLD":
                amount = ctx.amountGold;
                carryOver = ctx.carryOverGold;
                minimumLot = ctx.minimumLotGold;
                price = ctx.priceGold;
                break;
            case "ETF":
                amount = ctx.amountEtf;
                carryOver = ctx.carryOverEtf;
                minimumLot = ctx.minimumLotEtf;
                price = ctx.priceEtf;
                break;
            case "STOCK":
                amount = ctx.amountStock;
                carryOver = ctx.carryOverStock;
                minimumLot = ctx.minimumLotStock;
                price = ctx.priceStock;
                break;
        }

        // Kiểm tra null
        amount = nonNull(amount);
        carryOver = nonNull(carryOver);
        minimumLot = nonNull(minimumLot);
        price = nonNull(price);

        // Tổng tiền có thể đầu tư = amount hiện tại + số tiền dư từ kỳ trước
        BigDecimal totalAmount = amount.add(carryOver);

        // Tính tiền tối thiểu cần để đầu tư
        BigDecimal minimumAmount = minimumLot.multiply(price).setScale(2, RoundingMode.HALF_UP);

        BigDecimal investable = BigDecimal.ZERO;
        BigDecimal residual = totalAmount; // Mặc định: tất cả là dư nếu không đủ tối thiểu

        if (totalAmount.compareTo(minimumAmount) >= 0) {
            // Tính số lần có thể đầu tư (lấy phần nguyên)
            BigDecimal times = totalAmount.divide(minimumAmount, 0, RoundingMode.DOWN);
            investable = times.multiply(minimumAmount).setScale(2, RoundingMode.HALF_UP);
            residual = totalAmount.subtract(investable).setScale(2, RoundingMode.HALF_UP);

            log.info("Asset type {} calculated: totalAmount={}, minimumAmount={}, investable={}, residual={}", 
                assetType, totalAmount, minimumAmount, investable, residual);
        } else {
            log.warn("Asset type {} insufficient amount: totalAmount={} < minimumAmount={}, all to residual", 
                assetType, totalAmount, minimumAmount);
        }

        // Lưu kết quả vào Context
        switch (assetType.toUpperCase()) {
            case "EMERGENCY":
                ctx.investableEmergency = investable;
                ctx.residualEmergency = residual;
                break;
            case "GOLD":
                ctx.investableGold = investable;
                ctx.residualGold = residual;
                break;
            case "ETF":
                ctx.investableEtf = investable;
                ctx.residualEtf = residual;
                break;
            case "STOCK":
                ctx.investableStock = investable;
                ctx.residualStock = residual;
                break;
        }
    }

    /**
     * Lấy giá tài sản từ asset_prices table
     * Note: Updates to use latest prices instead of assetType lookup
     */
    private BigDecimal loadAssetPrice(String assetType) {
        try {
            // Return the latest prices for any active product
            return assetPriceRepository.findAllLatestPrices().stream()
                    .findFirst()
                    .map(AssetPriceEntity::getBuyPrice)
                    .orElse(BigDecimal.ZERO);
        } catch (Exception e) {
            log.warn("Error loading asset price for assetType={}: {}", assetType, e.getMessage());
            return BigDecimal.ZERO;
        }
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







