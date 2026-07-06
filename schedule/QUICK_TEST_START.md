# Quick Start Guide: Database Testing

## 🎯 Run Tests Now!

### Option 1: Run Unit Tests (Recommended)
```bash
# Run all tests
./gradlew test -i

# Run specific database test
./gradlew test --tests "AssetDataInsertIntegrationTest" -i

# Run single test method
./gradlew test --tests "AssetDataInsertIntegrationTest.testInsertCompleteData" -i
```

**Output Example:**
```
========== TEST 5: Insert Complete Data Set ==========

--- Step 1: Insert Categories ---
✓ Inserted: STOCK
✓ Inserted: ETF
✓ Inserted: GOLD
→ Total categories: 3

--- Step 2: Insert Platforms ---
✓ Inserted: VNDIRECT
✓ Inserted: GOLD_MARKET
→ Total platforms: 2

--- Step 3: Insert Products ---
✓ Inserted: TCB
✓ Inserted: E1VFVN30
✓ Inserted: SJC_1D
→ Total products: 3

--- Step 4: Insert Prices ---
✓ Inserted TCB price: 27000.50
✓ Inserted ETF price: 35800.00
✓ Inserted Gold price: 67500000.00
→ Total prices: 3
```

---

### Option 2: Insert Data on Application Startup

**Step 1: Enable Test Runner**

Edit: `src/main/java/com/vn/asset/test/DataInsertTestRunner.java`

Find this line (around line 15):
```java
// @Component  // Uncomment to enable
```

Change to:
```java
@Component  // Enable for testing
```

**Step 2: Start Application**
```bash
./gradlew bootRun
```

**Step 3: Check Logs**
Look for:
```
========== STARTING DATA INSERT TEST ==========
--- Step 1: Inserting Asset Categories ---
✓ Inserted STOCK category (ID: 1)
✓ Inserted ETF category (ID: 2)
✓ Inserted GOLD category (ID: 3)

[... more lines ...]

========== SUMMARY ==========
Total Categories: 3
Total Platforms: 2
Total Products: 4
Total Prices: 4
```

**Step 4: Verify in Database**
```bash
mysql -h localhost -u root -p asset_db

# Check data
SELECT COUNT(*) FROM asset_categories;      # Should show 3
SELECT COUNT(*) FROM asset_platforms;       # Should show 2
SELECT COUNT(*) FROM asset_products;        # Should show 4  
SELECT COUNT(*) FROM asset_prices;          # Should show 4

# View detailed data
SELECT * FROM asset_products;
SELECT p.code, p.name, pr.current_price FROM asset_products p 
       JOIN asset_prices pr ON p.id = pr.product_id;
```

**Step 5: Disable Test Runner** (when done)
```java
// @Component  // Disabled
```

---

### Option 3: SQL Script Insert

**Fastest way - Single Command:**
```bash
mysql -h localhost -u root -p asset_db < test-data.sql
```

**Or with password:**
```bash
mysql -h localhost -u root -pYOUR_PASSWORD asset_db < test-data.sql
```

**For Railway Production:**
```bash
mysql -h viaduct.proxy.rlwy.net -P 36228 -u root -pMQTRfyRKCFZdhGrMpAbKXJNebafyHFLw asset_db < test-data.sql
```

---

## ✅ Available Tests

| Test | What it tests |
|------|--------------|
| `testInsertAssetCategory()` | Single category insertion |
| `testInsertAssetPlatform()` | Single platform insertion |
| `testInsertAssetProduct()` | Product with foreign keys |
| `testInsertAssetPrice()` | Price linked to product |
| `testInsertCompleteData()` | **Full dataset (3+2+3+3 records)** |
| `testQueryInsertedData()` | Query by code functionality |
| `testInsertMultiplePricesPerProduct()` | Multiple prices per product |

---

## 🔍 Verify Your Database

### After Running Tests/Script, Run These Queries:

```sql
-- 1. Count each table
SELECT 'Categories' AS table_name, COUNT(*) AS count FROM asset_categories
UNION ALL
SELECT 'Platforms', COUNT(*) FROM asset_platforms
UNION ALL
SELECT 'Products', COUNT(*) FROM asset_products
UNION ALL
SELECT 'Prices', COUNT(*) FROM asset_prices;

-- 2. View products with category/platform
SELECT 
    p.code,
    p.name,
    c.code AS category,
    pl.code AS platform,
    p.is_active
FROM asset_products p
LEFT JOIN asset_categories c ON p.category_id = c.id
LEFT JOIN asset_platforms pl ON p.platform_id = pl.id;

-- 3. View prices with product info
SELECT 
    p.code,
    p.name,
    pr.current_price,
    pr.asset_type,
    pr.is_latest,
    pr.price_date
FROM asset_prices pr
LEFT JOIN asset_products p ON pr.product_id = p.id
ORDER BY pr.id DESC;
```

### Expected Output:

```
+-----------+-------+
| table_name| count |
+-----------+-------+
| Categories| 3     |
| Platforms | 2     |
| Products  | 3     |
| Prices    | 3     |
+-----------+-------+

Products with Relationships:
+----------+------------------+----------+----------+-----------+
| code     | name             | category | platform | is_active |
+----------+------------------+----------+----------+-----------+
| TCB      | Techcombank      | STOCK    | VNDIRECT | 1         |
| FPT      | FPT Corporation  | STOCK    | VNDIRECT | 1         |
| E1VFVN30 | E1VFVN30 ETF     | ETF      | VNDIRECT | 1         |
| SJC_1D   | SJC 1 Chỉ        | GOLD     | GOLD_MARKET | 1     |
+----------+------------------+----------+----------+-----------+

Prices:
+-----+------------------+----------------+----------+----------+---------------------+
| code| name             | current_price  | type     | latest   | date                |
+-----+------------------+----------------+----------+----------+---------------------+
| TCB | Techcombank      | 27000.50       | STOCK    | 1        | 2026-07-06 10:00:01 |
| FPT | FPT Corporation  | 85000.00       | STOCK    | 1        | 2026-07-06 10:00:02 |
| E1VFVN30| E1VFVN30 ETF  | 35800.00       | ETF      | 1        | 2026-07-06 10:00:03 |
| SJC_1D| SJC 1 Chỉ      | 67500000.00    | GOLD     | 1        | 2026-07-06 10:00:04 |
+-----+------------------+----------------+----------+----------+---------------------+
```

---

## 🛠️ Integration Test Details

### Test Database Configuration
- **Type**: H2 In-Memory (no MySQL needed)
- **Config**: `src/test/resources/application-test.yaml`
- **Persistence**: Data cleared between tests
- **Speed**: Fast (< 1 second per test)

### Test Data Inserted
- **3 Categories**: STOCK, ETF, GOLD
- **2 Platforms**: VNDIRECT, GOLD_MARKET
- **3-5 Products**: TCB, FPT, E1VFVN30, SJC_1D (depending on test)
- **3-5 Prices**: Realistic prices for each product

---

## 📊 Complete Workflow

```bash
# 1. Build project
./gradlew clean build

# 2. Run all integration tests
./gradlew test --tests "AssetDataInsertIntegrationTest" -i

# 3. If tests pass, data is correctly configured

# 4. For manual testing, enable DataInsertTestRunner
# Edit: src/main/java/com/vn/asset/test/DataInsertTestRunner.java
# Uncomment: @Component

# 5. Start application
./gradlew bootRun

# 6. Verify in database
mysql asset_db
SELECT * FROM asset_products;
exit

# 7. Disable test runner (comment @Component)

# 8. Rebuild and deploy
./gradlew clean build
# ... deploy to production
```

---

## ⚠️ Troubleshooting

### Tests fail with "Cannot find table"
**Solution**: This is normal - H2 creates tables from entities automatically

### "Cannot save, foreign key constraint error"
**Solution**: Tests insert categories/platforms BEFORE products. Verify order.

### Multiple runs create duplicates
**Solution**: 
- Unit tests are isolated (data cleared between tests)
- Test runner checks `count()` before inserting
- SQL script needs manual delete: `DELETE FROM asset_prices; DELETE FROM asset_products;`

### Wrong number of records
**Solution**: 
- Each test method starts fresh (cleared by @BeforeEach)
- `testInsertCompleteData()` inserts 11 total records (3+2+3+3)
- `testInsertMultiplePricesPerProduct()` inserts 3 prices

---

## 📚 Full Documentation

For more details, see:
- **DATABASE_TESTING_GUIDE.md** - Comprehensive testing guide
- **README.md** - Project overview
- **CONFIGURATION_GUIDE.md** - Database configuration options

---

## ✨ What's Next

✅ **Tests pass?** Good! Your database schema is correct.

Next:
1. Run the actual scheduler (it will sync real data from APIs)
2. Monitor logs for: `FireantApiService : Fetching X stocks`
3. Check database fills with real market data at scheduled times

---

**Status**: ✅ Ready to Test
**Last Updated**: July 6, 2026

