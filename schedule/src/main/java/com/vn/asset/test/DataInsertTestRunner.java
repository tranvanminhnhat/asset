package com.vn.asset.test;

import com.vn.asset.entity.*;
import com.vn.asset.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Manual Data Insert Test Runner
 *
 * This class inserts sample data into the database on application startup.
 *
 * To use:
 * 1. Uncomment @Component annotation below
 * 2. Run: ./gradlew bootRun
 * 3. Check database tables for inserted data
 *
 * Or keep it disabled and use AssetDataInsertIntegrationTest for unit testing
 */
//@Component  // Enabled to insert test data
@Slf4j
@RequiredArgsConstructor
public class DataInsertTestRunner implements CommandLineRunner {

    private final AssetCategoryRepository categoryRepository;
    private final AssetPlatformRepository platformRepository;
    private final AssetProductRepository productRepository;
    private final AssetPriceRepository priceRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("\n========== STARTING DATA INSERT TEST ==========\n");

        try {
            // Step 1: Insert Categories
            log.info("--- Step 1: Inserting Asset Categories ---");
            insertCategories();

            // Step 2: Insert Platforms
            log.info("\n--- Step 2: Inserting Asset Platforms ---");
            insertPlatforms();

            // Step 3: Insert Products
            log.info("\n--- Step 3: Inserting Asset Products ---");
            insertProducts();

            // Step 4: Insert Prices
            log.info("\n--- Step 4: Inserting Asset Prices ---");
            insertPrices();

            // Step 5: Summary
            logSummary();

            log.info("\n========== DATA INSERT TEST COMPLETED SUCCESSFULLY ==========\n");
        } catch (Exception e) {
            log.error("ERROR during data insertion test", e);
        }
    }

    private void insertCategories() {
        long existingCount = categoryRepository.count();
        log.info("Current categories in DB: {}", existingCount);

        if (existingCount == 0) {
            // Insert STOCK
            AssetCategory stock = categoryRepository.save(
                    AssetCategory.builder()
                            .code("STOCK")
                            .name("Stock")
                            .description("Stock assets")
                            .isActive(true)
                            .build()
            );
            log.info("✓ Inserted STOCK category (ID: {})", stock.getId());

            // Insert ETF
            AssetCategory etf = categoryRepository.save(
                    AssetCategory.builder()
                            .code("ETF")
                            .name("ETF")
                            .description("Exchange Traded Funds")
                            .isActive(true)
                            .build()
            );
            log.info("✓ Inserted ETF category (ID: {})", etf.getId());

            // Insert GOLD
            AssetCategory gold = categoryRepository.save(
                    AssetCategory.builder()
                            .code("GOLD")
                            .name("Gold")
                            .description("Gold assets")
                            .isActive(true)
                            .build()
            );
            log.info("✓ Inserted GOLD category (ID: {})", gold.getId());

            long newCount = categoryRepository.count();
            log.info("Total categories now: {}", newCount);
        } else {
            log.info("! Categories already exist, skipping insertion");
        }
    }

    private void insertPlatforms() {
        long existingCount = platformRepository.count();
        log.info("Current platforms in DB: {}", existingCount);

        if (existingCount == 0) {
            // Insert VNDIRECT
            AssetPlatform vndirect = platformRepository.save(
                    AssetPlatform.builder()
                            .code("VNDIRECT")
                            .name("VNDirect")
                            .description("VNDirect trading platform")
                            .isActive(true)
                            .build()
            );
            log.info("✓ Inserted VNDIRECT platform (ID: {})", vndirect.getId());

            // Insert GOLD_MARKET
            AssetPlatform goldMarket = platformRepository.save(
                    AssetPlatform.builder()
                            .code("GOLD_MARKET")
                            .name("Gold Market")
                            .description("SJC Gold Market")
                            .isActive(true)
                            .build()
            );
            log.info("✓ Inserted GOLD_MARKET platform (ID: {})", goldMarket.getId());

            long newCount = platformRepository.count();
            log.info("Total platforms now: {}", newCount);
        } else {
            log.info("! Platforms already exist, skipping insertion");
        }
    }

    private void insertProducts() {
        long existingCount = productRepository.count();
        log.info("Current products in DB: {}", existingCount);

        if (existingCount == 0) {
            // Get category and platform IDs
            var stockCategory = categoryRepository.findByCode("STOCK");
            var etfCategory = categoryRepository.findByCode("ETF");
            var goldCategory = categoryRepository.findByCode("GOLD");

            var stockPlatform = platformRepository.findByCode("VNDIRECT");
            var goldPlatform = platformRepository.findByCode("GOLD_MARKET");

            if (stockCategory.isEmpty() || stockPlatform.isEmpty()) {
                log.warn("! Categories or Platforms not found, skipping product insertion");
                return;
            }

            // Insert Stocks
            AssetProduct tcb = productRepository.save(
                    AssetProduct.builder()
                            .code("TCB")
                            .name("Techcombank")
                            .categoryId(stockCategory.get().getId())
                            .platformId(stockPlatform.get().getId())
                            .unitName("cổ phiếu")
                            .description("Techcombank stock")
                            .isActive(true)
                            .build()
            );
            log.info("✓ Inserted TCB stock (ID: {})", tcb.getId());

            AssetProduct fpt = productRepository.save(
                    AssetProduct.builder()
                            .code("FPT")
                            .name("FPT Corporation")
                            .categoryId(stockCategory.get().getId())
                            .platformId(stockPlatform.get().getId())
                            .unitName("cổ phiếu")
                            .isActive(true)
                            .build()
            );
            log.info("✓ Inserted FPT stock (ID: {})", fpt.getId());

            // Insert ETF
            if (etfCategory.isPresent()) {
                AssetProduct etf = productRepository.save(
                        AssetProduct.builder()
                                .code("E1VFVN30")
                                .name("E1VFVN30 ETF")
                                .categoryId(etfCategory.get().getId())
                                .platformId(stockPlatform.get().getId())
                                .unitName("đơn vị quỹ")
                                .isActive(true)
                                .build()
                );
                log.info("✓ Inserted E1VFVN30 ETF (ID: {})", etf.getId());
            }

            // Insert Gold
            if (goldCategory.isPresent() && goldPlatform.isPresent()) {
                AssetProduct gold = productRepository.save(
                        AssetProduct.builder()
                                .code("SJC_1D")
                                .name("SJC 1 Chỉ")
                                .categoryId(goldCategory.get().getId())
                                .platformId(goldPlatform.get().getId())
                                .unitName("chỉ")
                                .isActive(true)
                                .build()
                );
                log.info("✓ Inserted SJC_1D gold (ID: {})", gold.getId());
            }

            long newCount = productRepository.count();
            log.info("Total products now: {}", newCount);
        } else {
            log.info("! Products already exist, skipping insertion");
        }
    }

    private void insertPrices() {
        long existingCount = priceRepository.count();
        log.info("Current prices in DB: {}", existingCount);

        if (existingCount == 0) {
            // Get products
            var tcb = productRepository.findByCode("TCB");
            var fpt = productRepository.findByCode("FPT");
            var etf = productRepository.findByCode("E1VFVN30");
            var gold = productRepository.findByCode("SJC_1D");

            // Insert prices
            if (tcb.isPresent()) {
                AssetPrice tcbPrice = priceRepository.save(
                        AssetPrice.builder()
                                .productId(tcb.get().getId())
                                .currentPrice(new BigDecimal("27000.50"))
                                .assetType("STOCK")
                                .isLatest(true)
                                .isActive(true)
                                .build()
                );
                log.info("✓ Inserted TCB price: 27,000.50 VND (ID: {})", tcbPrice.getId());
            }

            if (fpt.isPresent()) {
                AssetPrice fptPrice = priceRepository.save(
                        AssetPrice.builder()
                                .productId(fpt.get().getId())
                                .currentPrice(new BigDecimal("85000.00"))
                                .assetType("STOCK")
                                .isLatest(true)
                                .isActive(true)
                                .build()
                );
                log.info("✓ Inserted FPT price: 85,000.00 VND (ID: {})", fptPrice.getId());
            }

            if (etf.isPresent()) {
                AssetPrice etfPrice = priceRepository.save(
                        AssetPrice.builder()
                                .productId(etf.get().getId())
                                .currentPrice(new BigDecimal("35800.00"))
                                .assetType("ETF")
                                .isLatest(true)
                                .isActive(true)
                                .build()
                );
                log.info("✓ Inserted E1VFVN30 price: 35,800.00 VND (ID: {})", etfPrice.getId());
            }

            if (gold.isPresent()) {
                AssetPrice goldPrice = priceRepository.save(
                        AssetPrice.builder()
                                .productId(gold.get().getId())
                                .currentPrice(new BigDecimal("67500000.00"))
                                .assetType("GOLD")
                                .isLatest(true)
                                .isActive(true)
                                .build()
                );
                log.info("✓ Inserted SJC_1D price: 67,500,000 VND (ID: {})", goldPrice.getId());
            }

            long newCount = priceRepository.count();
            log.info("Total prices now: {}", newCount);
        } else {
            log.info("! Prices already exist, skipping insertion");
        }
    }

    private void logSummary() {
        log.info("\n========== SUMMARY ==========");
        log.info("Total Categories: {}", categoryRepository.count());
        log.info("Total Platforms: {}", platformRepository.count());
        log.info("Total Products: {}", productRepository.count());
        log.info("Total Prices: {}", priceRepository.count());
        log.info("=============================\n");
    }
}

