package com.vn.asset.service;
import com.vn.asset.entity.*;
import com.vn.asset.repository.*;
import com.vn.asset.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AssetDataSyncService {
    private final AssetCategoryRepository categoryRepository;
    private final AssetPlatformRepository platformRepository;
    private final AssetProductRepository productRepository;
    private final AssetPriceRepository priceRepository;
    private final FireantApiService fireantApiService;
    private final GoldCrawlerService goldCrawlerService;
    public void initializeMasterData() {
        initializeCategories();
        initializePlatforms();
    }
    private void initializeCategories() {
        String[] categories = {"STOCK", "ETF", "GOLD"};
        for (String code : categories) {
            if (categoryRepository.findByCode(code).isEmpty()) {
                AssetCategory cat = AssetCategory.builder()
                        .code(code)
                        .name(code)
                        .isActive(true)
                        .build();
                categoryRepository.save(cat);
                log.info("Created category: {}", code);
            }
        }
    }
    private void initializePlatforms() {
        Object[][] platforms = {
                {"VNDIRECT", "VNDirect"},
                {"GOLD_MARKET", "Gold Market"}
        };
        for (Object[] p : platforms) {
            if (platformRepository.findByCode((String) p[0]).isEmpty()) {
                AssetPlatform plat = AssetPlatform.builder()
                        .code((String) p[0])
                        .name((String) p[1])
                        .isActive(true)
                        .build();
                platformRepository.save(plat);
                log.info("Created platform: {}", p[0]);
            }
        }
    }
    public void syncStockData() {
        log.info("Syncing stocks...");
        try {
            List<StockQuoteDto> stocks = fireantApiService.getStockQuotes();
            for (StockQuoteDto stock : stocks) {
                saveStockPrice(stock);
            }
        } catch (Exception e) {
            log.error("Error syncing stocks", e);
        }
    }
    public void syncEtfData() {
        log.info("Syncing ETFs...");
        try {
            List<EtfQuoteDto> etfs = fireantApiService.getEtfQuotes();
            for (EtfQuoteDto etf : etfs) {
                saveEtfPrice(etf);
            }
        } catch (Exception e) {
            log.error("Error syncing ETFs", e);
        }
    }
    public void syncGoldData() {
        log.info("Syncing gold data...");
        try {
            List<GoldPriceDto> goldPrices = goldCrawlerService.getGoldPrices();
            for (GoldPriceDto gold : goldPrices) {
                saveGoldPrice(gold);
            }
        } catch (Exception e) {
            log.error("Error syncing gold data", e);
        }
    }
    private void saveStockPrice(StockQuoteDto stock) {
        try {
            Integer catId = categoryRepository.findByCode("STOCK")
                    .map(AssetCategory::getId)
                    .orElse(null);
            Long platId = platformRepository.findByCode("VNDIRECT")
                    .map(AssetPlatform::getId)
                    .orElse(null);
            if (catId == null || platId == null) return;
            Optional<AssetProduct> product = productRepository.findByCode(stock.getCode());
            AssetProduct prod = product.orElseGet(() ->
                    productRepository.save(AssetProduct.builder()
                            .code(stock.getCode())
                            .name(stock.getCompanyName())
                            .categoryId(catId)
                            .platformId(platId)
                            .unitName("cổ phiếu")
                            .isActive(true)
                            .build())
            );
            priceRepository.findByProductIdAndIsLatestTrue(prod.getId())
                    .forEach(p -> {
                        p.setIsLatest(false);
                        priceRepository.save(p);
                    });
            AssetPrice price = AssetPrice.builder()
                    .productId(prod.getId())
                    .currentPrice(stock.getPrice())
                    .assetType("STOCK")
                    .isLatest(true)
                    .isActive(true)
                    .build();
            priceRepository.save(price);
        } catch (Exception e) {
            log.error("Error saving stock price: {}", stock.getCode(), e);
        }
    }
    private void saveEtfPrice(EtfQuoteDto etf) {
        try {
            Integer catId = categoryRepository.findByCode("ETF")
                    .map(AssetCategory::getId)
                    .orElse(null);
            Long platId = platformRepository.findByCode("VNDIRECT")
                    .map(AssetPlatform::getId)
                    .orElse(null);
            if (catId == null || platId == null) return;
            Optional<AssetProduct> product = productRepository.findByCode(etf.getCode());
            AssetProduct prod = product.orElseGet(() ->
                    productRepository.save(AssetProduct.builder()
                            .code(etf.getCode())
                            .name(etf.getName())
                            .categoryId(catId)
                            .platformId(platId)
                            .unitName("đơn vị quỹ")
                            .isActive(true)
                            .build())
            );
            priceRepository.findByProductIdAndIsLatestTrue(prod.getId())
                    .forEach(p -> {
                        p.setIsLatest(false);
                        priceRepository.save(p);
                    });
            AssetPrice price = AssetPrice.builder()
                    .productId(prod.getId())
                    .currentPrice(etf.getPrice())
                    .assetType("ETF")
                    .isLatest(true)
                    .isActive(true)
                    .build();
            priceRepository.save(price);
        } catch (Exception e) {
            log.error("Error saving ETF price: {}", etf.getCode(), e);
        }
    }
    private void saveGoldPrice(GoldPriceDto gold) {
        try {
            Integer catId = categoryRepository.findByCode("GOLD")
                    .map(AssetCategory::getId)
                    .orElse(null);
            Long platId = platformRepository.findByCode("GOLD_MARKET")
                    .map(AssetPlatform::getId)
                    .orElse(null);
            if (catId == null || platId == null) return;
            Optional<AssetProduct> product = productRepository.findByCode(gold.getCode());
            AssetProduct prod = product.orElseGet(() ->
                    productRepository.save(AssetProduct.builder()
                            .code(gold.getCode())
                            .name(gold.getName())
                            .categoryId(catId)
                            .platformId(platId)
                            .unitName("chỉ")
                            .isActive(true)
                            .build())
            );
            priceRepository.findByProductIdAndIsLatestTrue(prod.getId())
                    .forEach(p -> {
                        p.setIsLatest(false);
                        priceRepository.save(p);
                    });
            AssetPrice price = AssetPrice.builder()
                    .productId(prod.getId())
                    .currentPrice(gold.getBuyPrice())
                    .assetType("GOLD")
                    .isLatest(true)
                    .isActive(true)
                    .build();
            priceRepository.save(price);
        } catch (Exception e) {
            log.error("Error saving gold price: {}", gold.getCode(), e);
        }
    }
}