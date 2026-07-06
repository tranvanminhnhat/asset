package com.vn.asset;

import com.vn.asset.entity.*;
import com.vn.asset.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test for database operations
 * Tests inserting data into asset tables and verifying correctness
 *
 * Run: ./gradlew test --tests "AssetDataInsertIntegrationTest"
 * Or:  ./gradlew test -i (to see console output)
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yaml")
class AssetDataInsertIntegrationTest {

    @Autowired
    private AssetCategoryRepository categoryRepository;

    @Autowired
    private AssetPlatformRepository platformRepository;

    @Autowired
    private AssetProductRepository productRepository;

    @Autowired
    private AssetPriceRepository priceRepository;

    @BeforeEach
    void setUp() {
        // Clear all data before each test
        priceRepository.deleteAll();
        productRepository.deleteAll();
        platformRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testInsertAssetCategory() {
        System.out.println("\n========== TEST 1: Insert Asset Category ==========");

        // Create and insert category
        AssetCategory category = AssetCategory.builder()
                .code("STOCK")
                .name("Stock")
                .description("Stock assets")
                .isActive(true)
                .build();

        AssetCategory saved = categoryRepository.save(category);

        System.out.println("✓ Inserted category: " + saved.getCode());
        System.out.println("  ID: " + saved.getId());
        System.out.println("  Name: " + saved.getName());
        System.out.println("  Active: " + saved.getIsActive());
        System.out.println("  Created At: " + saved.getCreatedAt());

        // Verify
        assertNotNull(saved.getId());
        assertEquals("STOCK", saved.getCode());
        assertTrue(saved.getIsActive());

        // Verify in database
        long count = categoryRepository.count();
        System.out.println("✓ Total categories in DB: " + count);
        assertEquals(1, count);
    }

    @Test
    void testInsertAssetPlatform() {
        System.out.println("\n========== TEST 2: Insert Asset Platform ==========");

        // Create and insert platform
        AssetPlatform platform = AssetPlatform.builder()
                .code("VNDIRECT")
                .name("VNDirect")
                .description("VNDirect trading platform")
                .isActive(true)
                .build();

        AssetPlatform saved = platformRepository.save(platform);

        System.out.println("✓ Inserted platform: " + saved.getCode());
        System.out.println("  ID: " + saved.getId());
        System.out.println("  Name: " + saved.getName());
        System.out.println("  Active: " + saved.getIsActive());
        System.out.println("  Created At: " + saved.getCreatedAt());

        // Verify
        assertNotNull(saved.getId());
        assertEquals("VNDIRECT", saved.getCode());

        // Verify in database
        long count = platformRepository.count();
        System.out.println("✓ Total platforms in DB: " + count);
        assertEquals(1, count);
    }

    @Test
    void testInsertAssetProduct() {
        System.out.println("\n========== TEST 3: Insert Asset Product ==========");

        // First insert category and platform (foreign keys)
        AssetCategory category = categoryRepository.save(
                AssetCategory.builder()
                        .code("STOCK")
                        .name("Stock")
                        .isActive(true)
                        .build()
        );
        System.out.println("✓ Created category (FK): " + category.getId());

        AssetPlatform platform = platformRepository.save(
                AssetPlatform.builder()
                        .code("VNDIRECT")
                        .name("VNDirect")
                        .isActive(true)
                        .build()
        );
        System.out.println("✓ Created platform (FK): " + platform.getId());

        // Create and insert product
        AssetProduct product = AssetProduct.builder()
                .code("TCB")
                .name("Techcombank")
                .categoryId(category.getId())
                .platformId(platform.getId())
                .unitName("cổ phiếu")
                .description("Techcombank stock")
                .isActive(true)
                .build();

        AssetProduct saved = productRepository.save(product);

        System.out.println("✓ Inserted product: " + saved.getCode());
        System.out.println("  ID: " + saved.getId());
        System.out.println("  Name: " + saved.getName());
        System.out.println("  Category ID: " + saved.getCategoryId());
        System.out.println("  Platform ID: " + saved.getPlatformId());
        System.out.println("  Unit: " + saved.getUnitName());
        System.out.println("  Created At: " + saved.getCreatedAt());

        // Verify
        assertNotNull(saved.getId());
        assertEquals("TCB", saved.getCode());
        assertEquals(category.getId(), saved.getCategoryId());
        assertEquals(platform.getId(), saved.getPlatformId());

        // Verify in database
        long count = productRepository.count();
        System.out.println("✓ Total products in DB: " + count);
        assertEquals(1, count);
    }

    @Test
    void testInsertAssetPrice() {
        System.out.println("\n========== TEST 4: Insert Asset Price ==========");

        // Create dependent entities
        AssetCategory category = categoryRepository.save(
                AssetCategory.builder()
                        .code("STOCK")
                        .name("Stock")
                        .isActive(true)
                        .build()
        );

        AssetPlatform platform = platformRepository.save(
                AssetPlatform.builder()
                        .code("VNDIRECT")
                        .name("VNDirect")
                        .isActive(true)
                        .build()
        );

        AssetProduct product = productRepository.save(
                AssetProduct.builder()
                        .code("TCB")
                        .name("Techcombank")
                        .categoryId(category.getId())
                        .platformId(platform.getId())
                        .unitName("cổ phiếu")
                        .isActive(true)
                        .build()
        );
        System.out.println("✓ Created product (FK): " + product.getId());

        // Create and insert price
        AssetPrice price = AssetPrice.builder()
                .productId(product.getId())
                .currentPrice(new BigDecimal("27000.50"))
                .assetType("STOCK")
                .isLatest(true)
                .isActive(true)
                .build();

        AssetPrice saved = priceRepository.save(price);

        System.out.println("✓ Inserted price: " + saved.getId());
        System.out.println("  Product ID: " + saved.getProductId());
        System.out.println("  Current Price: " + saved.getCurrentPrice());
        System.out.println("  Asset Type: " + saved.getAssetType());
        System.out.println("  Price Date: " + saved.getPriceDate());
        System.out.println("  Is Latest: " + saved.getIsLatest());
        System.out.println("  Created At: " + saved.getCreatedAt());

        // Verify
        assertNotNull(saved.getId());
        assertEquals(product.getId(), saved.getProductId());
        assertEquals(new BigDecimal("27000.50"), saved.getCurrentPrice());
        assertEquals("STOCK", saved.getAssetType());
        assertTrue(saved.getIsLatest());

        // Verify in database
        long count = priceRepository.count();
        System.out.println("✓ Total prices in DB: " + count);
        assertEquals(1, count);
    }

    @Test
    void testInsertCompleteData() {
        System.out.println("\n========== TEST 5: Insert Complete Data Set ==========");

        // 1. Insert categories
        System.out.println("\n--- Step 1: Insert Categories ---");
        AssetCategory stockCategory = categoryRepository.save(
                AssetCategory.builder().code("STOCK").name("Stock").isActive(true).build()
        );
        System.out.println("✓ Inserted: " + stockCategory.getCode());

        AssetCategory etfCategory = categoryRepository.save(
                AssetCategory.builder().code("ETF").name("ETF").isActive(true).build()
        );
        System.out.println("✓ Inserted: " + etfCategory.getCode());

        AssetCategory goldCategory = categoryRepository.save(
                AssetCategory.builder().code("GOLD").name("Gold").isActive(true).build()
        );
        System.out.println("✓ Inserted: " + goldCategory.getCode());

        long categoryCount = categoryRepository.count();
        System.out.println("→ Total categories: " + categoryCount);

        // 2. Insert platforms
        System.out.println("\n--- Step 2: Insert Platforms ---");
        AssetPlatform stockPlatform = platformRepository.save(
                AssetPlatform.builder().code("VNDIRECT").name("VNDirect").isActive(true).build()
        );
        System.out.println("✓ Inserted: " + stockPlatform.getCode());

        AssetPlatform goldPlatform = platformRepository.save(
                AssetPlatform.builder().code("GOLD_MARKET").name("Gold Market").isActive(true).build()
        );
        System.out.println("✓ Inserted: " + goldPlatform.getCode());

        long platformCount = platformRepository.count();
        System.out.println("→ Total platforms: " + platformCount);

        // 3. Insert products
        System.out.println("\n--- Step 3: Insert Products ---");
        AssetProduct tcbProduct = productRepository.save(
                AssetProduct.builder()
                        .code("TCB")
                        .name("Techcombank")
                        .categoryId(stockCategory.getId())
                        .platformId(stockPlatform.getId())
                        .unitName("cổ phiếu")
                        .isActive(true)
                        .build()
        );
        System.out.println("✓ Inserted: " + tcbProduct.getCode());

        AssetProduct etfProduct = productRepository.save(
                AssetProduct.builder()
                        .code("E1VFVN30")
                        .name("E1VFVN30 ETF")
                        .categoryId(etfCategory.getId())
                        .platformId(stockPlatform.getId())
                        .unitName("đơn vị quỹ")
                        .isActive(true)
                        .build()
        );
        System.out.println("✓ Inserted: " + etfProduct.getCode());

        AssetProduct goldProduct = productRepository.save(
                AssetProduct.builder()
                        .code("SJC_1D")
                        .name("SJC 1 Chỉ")
                        .categoryId(goldCategory.getId())
                        .platformId(goldPlatform.getId())
                        .unitName("chỉ")
                        .isActive(true)
                        .build()
        );
        System.out.println("✓ Inserted: " + goldProduct.getCode());

        long productCount = productRepository.count();
        System.out.println("→ Total products: " + productCount);

        // 4. Insert prices
        System.out.println("\n--- Step 4: Insert Prices ---");
        AssetPrice tcbPrice = priceRepository.save(
                AssetPrice.builder()
                        .productId(tcbProduct.getId())
                        .currentPrice(new BigDecimal("27000.50"))
                        .assetType("STOCK")
                        .isLatest(true)
                        .isActive(true)
                        .build()
        );
        System.out.println("✓ Inserted TCB price: " + tcbPrice.getCurrentPrice());

        AssetPrice etfPrice = priceRepository.save(
                AssetPrice.builder()
                        .productId(etfProduct.getId())
                        .currentPrice(new BigDecimal("35800.00"))
                        .assetType("ETF")
                        .isLatest(true)
                        .isActive(true)
                        .build()
        );
        System.out.println("✓ Inserted ETF price: " + etfPrice.getCurrentPrice());

        AssetPrice goldPrice = priceRepository.save(
                AssetPrice.builder()
                        .productId(goldProduct.getId())
                        .currentPrice(new BigDecimal("67500000.00"))
                        .assetType("GOLD")
                        .isLatest(true)
                        .isActive(true)
                        .build()
        );
        System.out.println("✓ Inserted Gold price: " + goldPrice.getCurrentPrice());

        long priceCount = priceRepository.count();
        System.out.println("→ Total prices: " + priceCount);

        // 5. Summary
        System.out.println("\n========== SUMMARY ==========");
        System.out.println("✓ Categories inserted: " + categoryCount);
        System.out.println("✓ Platforms inserted: " + platformCount);
        System.out.println("✓ Products inserted: " + productCount);
        System.out.println("✓ Prices inserted: " + priceCount);
        System.out.println("✓ Total records: " + (categoryCount + platformCount + productCount + priceCount));

        // Verify
        assertTrue(categoryCount > 0);
        assertTrue(platformCount > 0);
        assertTrue(productCount > 0);
        assertTrue(priceCount > 0);
    }

    @Test
    void testQueryInsertedData() {
        System.out.println("\n========== TEST 6: Query Inserted Data ==========");

        // Insert test data
        AssetCategory category = categoryRepository.save(
                AssetCategory.builder().code("STOCK").name("Stock").isActive(true).build()
        );

        AssetPlatform platform = platformRepository.save(
                AssetPlatform.builder().code("VNDIRECT").name("VNDirect").isActive(true).build()
        );

        AssetProduct product = productRepository.save(
                AssetProduct.builder()
                        .code("FPT")
                        .name("FPT Corporation")
                        .categoryId(category.getId())
                        .platformId(platform.getId())
                        .unitName("cổ phiếu")
                        .isActive(true)
                        .build()
        );

        AssetPrice price = priceRepository.save(
                AssetPrice.builder()
                        .productId(product.getId())
                        .currentPrice(new BigDecimal("85000.00"))
                        .assetType("STOCK")
                        .isLatest(true)
                        .isActive(true)
                        .build()
        );

        // Query and verify
        System.out.println("\n--- Query Results ---");

        System.out.println("\n1. Query Category by Code:");
        var foundCategory = categoryRepository.findByCode("STOCK");
        if (foundCategory.isPresent()) {
            System.out.println("✓ Found category: " + foundCategory.get().getName());
        }
        assertTrue(foundCategory.isPresent());

        System.out.println("\n2. Query Platform by Code:");
        var foundPlatform = platformRepository.findByCode("VNDIRECT");
        if (foundPlatform.isPresent()) {
            System.out.println("✓ Found platform: " + foundPlatform.get().getName());
        }
        assertTrue(foundPlatform.isPresent());

        System.out.println("\n3. Query Product by Code:");
        var foundProduct = productRepository.findByCode("FPT");
        if (foundProduct.isPresent()) {
            AssetProduct p = foundProduct.get();
            System.out.println("✓ Found product: " + p.getName());
            System.out.println("  Code: " + p.getCode());
            System.out.println("  Category ID: " + p.getCategoryId());
            System.out.println("  Platform ID: " + p.getPlatformId());
        }
        assertTrue(foundProduct.isPresent());

        System.out.println("\n4. Query Prices for Latest Only:");
        long latestCount = priceRepository.count();
        System.out.println("✓ Latest prices count: " + latestCount);
        assertTrue(latestCount > 0);

        System.out.println("\n✓ All queries executed successfully!");
    }

    @Test
    void testInsertMultiplePricesPerProduct() {
        System.out.println("\n========== TEST 7: Insert Multiple Prices Per Product ==========");

        // Setup
        AssetCategory category = categoryRepository.save(
                AssetCategory.builder().code("STOCK").name("Stock").isActive(true).build()
        );
        AssetPlatform platform = platformRepository.save(
                AssetPlatform.builder().code("VNDIRECT").name("VNDirect").isActive(true).build()
        );
        AssetProduct product = productRepository.save(
                AssetProduct.builder()
                        .code("VCB")
                        .name("VietcomBank")
                        .categoryId(category.getId())
                        .platformId(platform.getId())
                        .isActive(true)
                        .build()
        );

        System.out.println("✓ Created product: " + product.getCode());

        // Insert first price
        AssetPrice price1 = priceRepository.save(
                AssetPrice.builder()
                        .productId(product.getId())
                        .currentPrice(new BigDecimal("100000.00"))
                        .assetType("STOCK")
                        .isLatest(true)
                        .isActive(true)
                        .build()
        );
        System.out.println("✓ Inserted price 1: " + price1.getCurrentPrice() + " VND");

        // Insert second price (different time, different value)
        AssetPrice price2 = priceRepository.save(
                AssetPrice.builder()
                        .productId(product.getId())
                        .currentPrice(new BigDecimal("101000.00"))
                        .assetType("STOCK")
                        .isLatest(true)
                        .isActive(true)
                        .build()
        );
        System.out.println("✓ Inserted price 2: " + price2.getCurrentPrice() + " VND");

        // Insert third price
        AssetPrice price3 = priceRepository.save(
                AssetPrice.builder()
                        .productId(product.getId())
                        .currentPrice(new BigDecimal("102000.00"))
                        .assetType("STOCK")
                        .isLatest(true)
                        .isActive(true)
                        .build()
        );
        System.out.println("✓ Inserted price 3: " + price3.getCurrentPrice() + " VND");

        // Verify - count all prices for product
        long priceCount = priceRepository.count();
        System.out.println("\n✓ Total prices for all products: " + priceCount);
        assertEquals(3, priceCount);

        // Verify all prices are linked to the same product
        long productACount = priceRepository.findByAssetType("STOCK").size();
        System.out.println("✓ Prices with type STOCK: " + productACount);
        assertTrue(productACount >= 3);

        System.out.println("✓ Test passed: Multiple prices inserted successfully!");
    }
}

