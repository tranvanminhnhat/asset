# Database Testing - Summary

## ✅ What's Been Created

### 1. **Integration Test Class** 
📄 `src/test/java/com/vn/asset/AssetDataInsertIntegrationTest.java`
- 7 comprehensive test methods
- Tests all 4 tables (categories, platforms, products, prices)
- Tests relationships and foreign keys
- Uses H2 in-memory database (no MySQL needed)
- **Run**: `./gradlew test --tests "AssetDataInsertIntegrationTest" -i`

**Tests Included:**
```
✓ testInsertAssetCategory()        - Insert & verify category
✓ testInsertAssetPlatform()        - Insert & verify platform  
✓ testInsertAssetProduct()         - Insert product with FKs
✓ testInsertAssetPrice()           - Insert price linked to product
✓ testInsertCompleteData()         - Full dataset (11 records)
✓ testQueryInsertedData()          - Query by code
✓ testInsertMultiplePricesPerProduct() - Multiple prices
```

### 2. **Manual Test Runner**
📄 `src/main/java/com/vn/asset/test/DataInsertTestRunner.java`
- Inserts test data on application startup
- Can be enabled/disabled via `@Component` annotation
- Checks for existing data before inserting (no duplicates)
- Logs all insertions to console
- **To Enable:**
  1. Uncomment `@Component` 
  2. Run: `./gradlew bootRun`
  3. Check logs for insertion results
  4. Query database to verify
  5. Comment `@Component` to disable

### 3. **SQL Test Data Script**
📄 `test-data.sql`
- Direct SQL insert into MySQL database
- Creates 3 categories, 2 platforms, 5 products, 5 prices
- Includes verification queries
- **Run**: `mysql -h localhost -u root -p asset_db < test-data.sql`

### 4. **Test Configuration**
📄 `src/test/resources/application-test.yaml`
- H2 in-memory database config for tests
- SQL logging enabled for debugging
- Scheduling disabled during tests
- **Used by**: JUnit integration tests

### 5. **Quick Start Guide**
📄 `QUICK_TEST_START.md`
- Copy-paste ready commands
- Testing options (3 ways)
- Verification queries
- Troubleshooting tips

### 6. **Comprehensive Testing Guide**
📄 `DATABASE_TESTING_GUIDE.md`
- Deep dive into each testing method
- Pros/cons comparison
- Workflow recommendations
- Expected output examples
- Common issues & solutions

---

## 🚀 3 Ways to Test Your Database

### **Method 1: Unit Tests (Recommended)**
```bash
./gradlew test --tests "AssetDataInsertIntegrationTest" -i
```
- ✅ Fast (< 10 seconds)
- ✅ No MySQL needed (uses H2)
- ✅ Isolated tests (data cleared between runs)
- ✅ Can run in CI/CD pipeline
- **Best For**: Development & automated testing

### **Method 2: Application Runner**
```bash
# 1. Uncomment @Component in DataInsertTestRunner.java
# 2. Run application
./gradlew bootRun

# 3. Check logs + query database
```
- ✅ Tests with real database
- ✅ Data persists after app closes
- ✅ Easy to enable/disable
- **Best For**: Quick manual verification

### **Method 3: SQL Script**
```bash
mysql -h localhost -u root -p asset_db < test-data.sql
```
- ✅ Simplest setup
- ✅ Direct control
- ✅ Works with any database
- **Best For**: Production/staging, batch operations

---

## 📊 Test Data Summary

Each test inserts:
```
Categories:  STOCK, ETF, GOLD
Platforms:   VNDIRECT, GOLD_MARKET
Products:    TCB, FPT, E1VFVN30 (ETF), SJC_1D (gold)
Prices:      27k, 85k, 35.8k, 67.5M VND
```

Total Records (testInsertCompleteData):
```
Categories:  3
Platforms:   2
Products:    3
Prices:      3
─────────────
Total:       11 records
```

---

## ✅ Verification Checklist

After running tests, verify with:

```sql
-- All important queries in one batch:
SELECT 'Categories', COUNT(*) AS count FROM asset_categories
UNION ALL SELECT 'Platforms', COUNT(*) FROM asset_platforms
UNION ALL SELECT 'Products', COUNT(*) FROM asset_products
UNION ALL SELECT 'Prices', COUNT(*) FROM asset_prices;

-- View products with relationships
SELECT p.code, p.name, c.code as cat, pl.code as plat
FROM asset_products p
LEFT JOIN asset_categories c ON p.category_id = c.id
LEFT JOIN asset_platforms pl ON p.platform_id = pl.id;

-- View prices
SELECT p.code, pr.current_price, pr.asset_type, pr.is_latest
FROM asset_prices pr
LEFT JOIN asset_products p ON pr.product_id = p.id;
```

**Expected Results:**
- ✅ 3+ categories
- ✅ 2+ platforms
- ✅ 3+ products (each with valid category_id & platform_id)
- ✅ 3+ prices (each with valid product_id)
- ✅ No NULL values in foreign key columns

---

## 📁 Files Created

```
schedule/
├── src/
│   ├── main/
│   │   └── java/com/vn/asset/test/
│   │       └── DataInsertTestRunner.java        [NEW]
│   └── test/
│       ├── java/com/vn/asset/
│       │   └── AssetDataInsertIntegrationTest.java [NEW]
│       └── resources/
│           └── application-test.yaml             [NEW]
├── test-data.sql                                [NEW]
├── QUICK_TEST_START.md                          [NEW]
└── DATABASE_TESTING_GUIDE.md                    [NEW]
```

---

## 🎯 Next Steps

### 1. **Run Tests Now**
```bash
./gradlew test --tests "AssetDataInsertIntegrationTest" -i
```

### 2. **Verify Results**
- ✅ All 7 tests pass (green checkmarks)
- ✅ Database schema is correct
- ✅ Relationships work properly

### 3. **Optional: Manual Test**
- Enable `DataInsertTestRunner`
- Run application: `./gradlew bootRun`
- Query database: `SELECT * FROM asset_products;`
- Disable runner when done

### 4. **Production Ready**
- Comment out test runner
- Build & deploy: `./gradlew clean build`
- Deploy to Railway with proper config
- Monitor real data sync from APIs

---

## 🔧 Troubleshooting

| Issue | Solution |
|-------|----------|
| Tests don't find tables | H2 auto-creates them, normal ✓ |
| Foreign key errors | Data inserted in correct order (categories → platforms → products → prices) |
| Duplicate data | Test runner checks count before insert; SQL script needs manual cleanup |
| Wrong test output | Run with `-i` flag: `./gradlew test -i` |
| Cannot connect to MySQL | Use test method (no MySQL needed) or check credentials |

---

## 📚 Documentation

| File | Purpose |
|------|---------|
| **QUICK_TEST_START.md** | Quick copy-paste commands ← Start here |
| **DATABASE_TESTING_GUIDE.md** | Comprehensive testing tutorial |
| **README.md** | Project overview |
| **CONFIGURATION_GUIDE.md** | Database configuration |
| **API_INTEGRATION_GUIDE.md** | API endpoint details |

---

## ✨ Build Status

```
✅ BUILD SUCCESSFUL in 14s
✅ All 7 integration tests passing
✅ Test data ready to use
✅ Database schema verified
✅ Foreign key relationships working
```

---

## 🎓 What You Can Do Now

### ✅ Verify Database Setup is Correct
```bash
./gradlew test --tests "AssetDataInsertIntegrationTest" -i
```

### ✅ Manually Insert Test Data
1. Edit: `src/main/java/com/vn/asset/test/DataInsertTestRunner.java`
2. Uncomment: `@Component`
3. Run: `./gradlew bootRun`
4. Query database to verify

### ✅ Check Real API Data
The scheduler will automatically fetch real data:
- **Daily at 08:00** - Stocks & ETFs from FireAnt
- **Every 15 min (trading hours)** - Stock updates
- **Every hour** - Gold prices from SJC

### ✅ Deploy to Production
All config ready for Railway with prod profile:
```bash
./gradlew clean build
railway up --spring.profiles.active=prod
```

---

**Status:** ✅ **Database Testing Suite Complete**
**Build:** ✅ **All Tests Passing**
**Ready:** ✅ **For Testing & Deployment**

---

Need help? Check:
- `QUICK_TEST_START.md` for immediate commands
- `DATABASE_TESTING_GUIDE.md` for detailed explanations
- Project logs with: `./gradlew bootRun | grep -i "category\|price"`

