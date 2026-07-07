# Database Testing Guide

## Overview
This guide shows you 3 ways to test database insertion:

1. **Unit Tests (H2 In-Memory)** - Recommended for development
2. **Manual Test Runner** - Insert data on application startup
3. **SQL Script** - Direct database insertion

---

## Option 1: Unit Tests with H2 (Recommended for Development)

### What is H2?
H2 is an in-memory SQL database that runs inside the JVM. Perfect for testing without affecting your real database.

### Run Tests

```bash
# Run all tests
./gradlew test

# Run specific integration test
./gradlew test --tests "AssetDataInsertIntegrationTest"

# Run with verbose output to see print statements
./gradlew test -i

# Run single test method
./gradlew test --tests "AssetDataInsertIntegrationTest.testInsertCompleteData" -i
```

### What Tests Are Available

| Test | Purpose |
|------|---------|
| `testInsertAssetCategory()` | Insert single category, verify in DB |
| `testInsertAssetPlatform()` | Insert single platform, verify in DB |
| `testInsertAssetProduct()` | Insert product with foreign keys |
| `testInsertAssetPrice()` | Insert price linked to product |
| `testInsertCompleteData()` | Insert 3 categories, 2 platforms, 3 products, 3 prices |
| `testQueryInsertedData()` | Query inserted data by code |
| `testUpdateLatestPriceFlag()` | Test updating is_latest flag |

### Expected Output

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

========== SUMMARY ==========
✓ Categories inserted: 3
✓ Platforms inserted: 2
✓ Products inserted: 3
✓ Prices inserted: 3
✓ Total records: 11
```

### View Test Details
The tests use H2 in-memory database configured in:
```
src/test/resources/application-test.yaml
```

---

## Option 2: Manual Test Runner (Insert on Application Startup)

### Setup

1. **Enable the Test Runner**
   Edit `DataInsertTestRunner.java` and uncomment the `@Component` annotation:
   ```java
   @Component  // Uncomment to enable
   @Slf4j
   @RequiredArgsConstructor
   public class DataInsertTestRunner implements CommandLineRunner {
   ```

2. **Start the Application**
   ```bash
   ./gradlew bootRun
   ```

3. **Check Logs**
   Look for output like:
   ```
   ========== STARTING DATA INSERT TEST ==========
   --- Step 1: Inserting Asset Categories ---
   ✓ Inserted STOCK category (ID: 1)
   ✓ Inserted ETF category (ID: 2)
   ✓ Inserted GOLD category (ID: 3)
   
   --- Step 2: Inserting Asset Platforms ---
   ✓ Inserted VNDIRECT platform (ID: 1)
   ✓ Inserted GOLD_MARKET platform (ID: 2)
   
   --- Step 3: Inserting Asset Products ---
   ✓ Inserted TCB stock (ID: 1)
   ✓ Inserted FPT stock (ID: 2)
   ✓ Inserted E1VFVN30 ETF (ID: 3)
   ✓ Inserted SJC_1D gold (ID: 4)
   
   --- Step 4: Inserting Asset Prices ---
   ✓ Inserted TCB price: 27,000.50 VND (ID: 1)
   ✓ Inserted FPT price: 85,000.00 VND (ID: 2)
   ✓ Inserted E1VFVN30 price: 35,800.00 VND (ID: 3)
   ✓ Inserted SJC_1D price: 67,500,000 VND (ID: 4)
   
   ========== SUMMARY ==========
   Total Categories: 3
   Total Platforms: 2
   Total Products: 4
   Total Prices: 4
   ```

4. **Query Database to Verify**
   ```bash
   mysql -h localhost -u root -p asset_db
   
   # Check data
   SELECT COUNT(*) FROM asset_categories;      # Should show 3
   SELECT COUNT(*) FROM asset_platforms;       # Should show 2
   SELECT COUNT(*) FROM asset_products;        # Should show 4
   SELECT COUNT(*) FROM asset_prices;          # Should show 4
   
   # View results
   SELECT * FROM asset_products;
   SELECT p.code, p.name, pr.current_price FROM asset_products p 
          JOIN asset_prices pr ON p.id = pr.product_id;
   ```

### Important Notes
- The runner checks if data already exists before inserting
- If you run multiple times, it won't create duplicates
- To reset, delete the component annotation or manually clear tables

### To Disable After Testing
Comment out the `@Component` annotation again:
```java
// @Component  // Disabled after testing
@Slf4j
@RequiredArgsConstructor
public class DataInsertTestRunner implements CommandLineRunner {
```

---

## Option 3: SQL Script (Direct Database)

### Quickest Method for MySQL

```bash
# Run the test data script
mysql -h localhost -u root -p asset_db < test-data.sql

# Or if you have a password
mysql -h localhost -u root -pYOUR_PASSWORD asset_db < test-data.sql

# For Railway/production database
mysql -h viaduct.proxy.rlwy.net -P 36228 -u root -pMQTRfyRKCFZdhGrMpAbKXJNebafyHFLw asset_db < test-data.sql
```

### What the Script Does

1. **Inserts test categories**: STOCK, ETF, GOLD
2. **Inserts test platforms**: VNDIRECT, GOLD_MARKET
3. **Inserts test products**: TCB, FPT, VCB (stocks), E1VFVN30 (ETF), SJC_1D (gold)
4. **Inserts test prices**: Realistic prices for each product
5. **Verifies insertion** with sample queries

### Expected Output

```
Categories inserted
Platforms inserted
Products inserted
Prices inserted

--- Summary ---
+-------------------+-------+
| metric            | count |
+-------------------+-------+
| Categories count: | 3     |
| Platforms count:  | 2     |
| Products count:   | 5     |
| Prices count:     | 5     |
+-------------------+-------+

--- Sample Data ---
Categories:
+----+--------+------+-----------+
| id | code   | name | is_active |
+----+--------+------+-----------+
| 1  | STOCK  | Stock| 1         |
| 2  | ETF    | ETF  | 1         |
| 3  | GOLD   | Gold | 1         |
+----+--------+------+-----------+

Platforms:
+----+------------+-----------+-----------+
| id | code       | name      | is_active |
+----+------------+-----------+-----------+
| 1  | VNDIRECT   | VNDirect  | 1         |
| 2  | GOLD_MARKET| Gold Mark | 1         |
+----+------------+-----------+-----------+

Products:
(Shows all products with category and platform associations)

Prices:
(Shows all prices with product information)

✓ Test data insertion completed!
```

### Manually Verify

```bash
mysql -h localhost -u root -p asset_db

# View all categories
SELECT * FROM asset_categories\G

# View products with relationships
SELECT p.id, p.code, p.name, c.code as category, pl.code as platform 
FROM asset_products p
LEFT JOIN asset_categories c ON p.category_id = c.id
LEFT JOIN asset_platforms pl ON p.platform_id = pl.id\G

# View prices
SELECT pr.id, p.code, p.name, pr.current_price, pr.asset_type, pr.price_date
FROM asset_prices pr
LEFT JOIN asset_products p ON pr.product_id = p.id\G
```

---

## Comparison of Methods

| Method | Pros | Cons | Best For |
|--------|------|------|----------|
| **Unit Tests (H2)** | Fast, isolated, repeatable, no DB dependency | Limited to unit testing | Development & CI/CD |
| **Test Runner** | Tests real DB, data persists, easy to see | Manual @Component toggle | Quick verification |
| **SQL Script** | Simplest, direct control, works with any DB | Need MySQL access | Production/staging |

---

## Workflow Recommendation

### Development Phase
```bash
# 1. Write/update code
# 2. Run unit tests
./gradlew test -i

# 3. If tests pass, build JAR
./gradlew clean build

# 4. For manual testing, enable DataInsertTestRunner
# Edit: src/main/java/com/vn/asset/test/DataInsertTestRunner.java
# Uncomment: @Component

# 5. Run application
./gradlew bootRun

# 6. Verify in database
mysql asset_db
SELECT COUNT(*) FROM asset_products;

# 7. Disable test runner
# Comment: @Component
```

### Before Deployment
```bash
# 1. Run all tests
./gradlew test

# 2. Build production JAR
./gradlew clean build

# 3. Test SQL script (optional, for production data)
mysql -h production_host -u prod_user -p prod_db < test-data.sql

# 4. Deploy
railway up --spring.profiles.active=prod
```

---

## Troubleshooting

### Issue: Tests fail with "Cannot connect to database"
**Solution**: Make sure H2 is in classpath. Check `build.gradle` has:
```gradle
testRuntimeOnly 'com.h2database:h2'
```

### Issue: "Table doesn't exist" error
**Solution**: 
- Ensure `spring.jpa.hibernate.ddl-auto: update` is set in test config
- Tables are auto-created by Hibernate

### Issue: "Foreign key constraint fails"
**Solution**: 
- Make sure to insert categories and platforms BEFORE products
- Test runner handles this automatically

### Issue: Seeing duplicate data
**Solution**: 
- Test runner checks `count()` before inserting
- Manually clear tables:
  ```sql
  DELETE FROM asset_prices;
  DELETE FROM asset_products;
  DELETE FROM asset_platforms;
  DELETE FROM asset_categories;
  ```

### Issue: SQL script has wrong IDs
**Solution**: 
- Script assumes sequential IDs (1, 2, 3...)
- If your DB already has data, edit the script to use correct IDs
- Or use the values from:
  ```sql
  SELECT id, code FROM asset_categories;
  SELECT id, code FROM asset_platforms;
  ```

---

## Next Steps

1. **Choose your testing method** (typically Unit Tests for development)
2. **Run the tests**
3. **Verify data in your MySQL instance**
4. **Check the scheduler inserts data correctly** (scheduled tasks run at configured times)
5. **Monitor production deployment** for real sync data

---

## Example Query Results

After successful insertion, you should see:

```sql
-- Categories
mysql> SELECT * FROM asset_categories;
+----+--------+------+-----------+---------------------+---------------------+
| id | code   | name | is_active | created_at          | updated_at          |
+----+--------+------+-----------+---------------------+---------------------+
| 1  | STOCK  | Stock| 1         | 2026-07-06 10:00:00 | 2026-07-06 10:00:00 |
| 2  | ETF    | ETF  | 1         | 2026-07-06 10:00:01 | 2026-07-06 10:00:01 |
| 3  | GOLD   | Gold | 1         | 2026-07-06 10:00:02 | 2026-07-06 10:00:02 |
+----+--------+------+-----------+---------------------+---------------------+

-- Products with Joins
mysql> SELECT p.code, p.name, c.code as category, pl.code as platform FROM asset_products p
        LEFT JOIN asset_categories c ON p.category_id = c.id
        LEFT JOIN asset_platforms pl ON p.platform_id = pl.id;
+----------+------------------+----------+------------+
| code     | name             | category | platform   |
+----------+------------------+----------+------------+
| TCB      | Techcombank      | STOCK    | VNDIRECT   |
| FPT      | FPT Corporation  | STOCK    | VNDIRECT   |
| E1VFVN30 | E1VFVN30 ETF     | ETF      | VNDIRECT   |
| SJC_1D   | SJC 1 Chỉ        | GOLD     | GOLD_MARKET|
+----------+------------------+----------+------------+

-- Prices with Product Info
mysql> SELECT p.code, pr.current_price, pr.asset_type 
        FROM asset_prices pr
        LEFT JOIN asset_products p ON pr.product_id = p.id;
+----------+---------------+------------+
| code     | current_price | asset_type |
+----------+---------------+------------+
| TCB      | 27000.50      | STOCK      |
| FPT      | 85000.00      | STOCK      |
| E1VFVN30 | 35800.00      | ETF        |
| SJC_1D   | 67500000.00   | GOLD       |
+----------+---------------+------------+
```

Perfect! Your database is working correctly! ✓

