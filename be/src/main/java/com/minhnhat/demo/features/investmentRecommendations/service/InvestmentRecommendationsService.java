package com.minhnhat.demo.features.investmentRecommendations.service;

import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.features.investmentRecommendations.constant.InvestmentRecommendationsConstant;
import com.minhnhat.demo.features.investmentRecommendations.dto.request.InvestmentRecommendationsRequest;
import com.minhnhat.demo.features.investmentRecommendations.dto.response.InvestmentRecommendationsResponse;
import com.minhnhat.demo.features.master.entity.InvestmentAllocationMasterEntity;
import com.minhnhat.demo.features.master.repository.InvestmentAllocationMasterRepository;
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

    private final InvestmentAllocationMasterRepository masterRepository;

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

        Optional<InvestmentAllocationMasterEntity> opt = masterRepository.findFirstByCode(type);
        InvestmentAllocationMasterEntity master = null;
        if (opt.isPresent()) {
            master = opt.get();
        } else {
            for (InvestmentAllocationMasterEntity m : masterRepository.findAll()) {
                if (m.getCode() != null && m.getCode().equalsIgnoreCase(type)) {
                    master = m;
                    break;
                }
            }
        }

        if (master == null) {
                log.error("Allocation validation failed: master config not found for type={}", type);
                    throw new LogicException(SERVICE_NAME, messageService.get(InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001), InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001);
        }

        ctx.master = master;
        return ctx;
    }

    private void executeBusinessLogic(Context ctx) {
        InvestmentAllocationMasterEntity master = ctx.master;

        BigDecimal pEmergency = nonNull(master.getEmergencyPercent());
        BigDecimal pGold = nonNull(master.getGoldPercent());
        BigDecimal pEtf = nonNull(master.getEtfPercent());
        BigDecimal pStock = nonNull(master.getStockPercent());

        BigDecimal sum = pEmergency.add(pGold).add(pEtf).add(pStock);
        if (sum.compareTo(BigDecimal.ZERO) == 0) {
            log.error("Master allocation percents sum to zero for type={}", ctx.code);
            throw new LogicException(SERVICE_NAME, messageService.get(InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001), InvestmentRecommendationsConstant.ALLOCATE_LOGIC_ERROR_001);
        }

        // normalize if sum != 1
        if (sum.compareTo(BigDecimal.ONE) != 0) {
            pEmergency = pEmergency.divide(sum, 8, RoundingMode.HALF_UP);
            pGold = pGold.divide(sum, 8, RoundingMode.HALF_UP);
            pEtf = pEtf.divide(sum, 8, RoundingMode.HALF_UP);
            pStock = pStock.divide(sum, 8, RoundingMode.HALF_UP);
            log.info("Normalized percents for type={} from sum={} to 1", ctx.code, sum);
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
    }

    private InvestmentRecommendationsResponse createResponse(Context ctx) {
        List<InvestmentRecommendationsResponse.Allocation> allocations = new ArrayList<>();
        // use 'type' field in response to map with DB keys for the allocation buckets
        allocations.add(InvestmentRecommendationsResponse.Allocation.builder().type("EMERGENCY").percent(strip(ctx.percentEmergency)).amount(strip(ctx.amountEmergency)).build());
        allocations.add(InvestmentRecommendationsResponse.Allocation.builder().type("GOLD").percent(strip(ctx.percentGold)).amount(strip(ctx.amountGold)).build());
        allocations.add(InvestmentRecommendationsResponse.Allocation.builder().type("ETF").percent(strip(ctx.percentEtf)).amount(strip(ctx.amountEtf)).build());
        allocations.add(InvestmentRecommendationsResponse.Allocation.builder().type("STOCK").percent(strip(ctx.percentStock)).amount(strip(ctx.amountStock)).build());

        log.info("Allocation computed from master type={} total={} => sumPercents={} sumAmounts={}", ctx.code, ctx.request.getAmount(), ctx.percentEmergency.add(ctx.percentGold).add(ctx.percentEtf).add(ctx.percentStock), ctx.amountEmergency.add(ctx.amountGold).add(ctx.amountEtf).add(ctx.amountStock));

        return InvestmentRecommendationsResponse.builder()
                .allocations(allocations)
                .build();
    }

    private static class Context {
        InvestmentRecommendationsRequest request;
        String code;
        InvestmentAllocationMasterEntity master;

        BigDecimal percentEmergency;
        BigDecimal percentGold;
        BigDecimal percentEtf;
        BigDecimal percentStock;

        BigDecimal amountEmergency;
        BigDecimal amountGold;
        BigDecimal amountEtf;
        BigDecimal amountStock;
    }

    private BigDecimal nonNull(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
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







