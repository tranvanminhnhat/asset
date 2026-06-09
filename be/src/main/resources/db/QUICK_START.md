## 🎯 QUICK START - Database Reset Instructions

### **File Location:**
`D:\code\asset\be\src\main\resources\db\COMPLETE_SCHEMA_RESET.sql`

---

## ⚡ 3 Ways to Run This

### **Method 1: MySQL Command Line**
```bash
mysql -h localhost -u root -p asset_db < D:\code\asset\be\src\main\resources\db\COMPLETE_SCHEMA_RESET.sql
```

### **Method 2: MySQL Workbench**
1. Open MySQL Workbench
2. File → Open SQL Script → Select `COMPLETE_SCHEMA_RESET.sql`
3. Click ⚡ Execute

### **Method 3: Inside MySQL Console**
```sql
-- Connect to MySQL first
mysql -u root -p

-- Inside MySQL:
USE asset_db;
SOURCE D:/code/asset/be/src/main/resources/db/COMPLETE_SCHEMA_RESET.sql;
```

---

## ✅ What Gets Created

**15 Tables Total:**

| Category | Tables |
|----------|--------|
| **User** | user |
| **Strategy Master** | mst_allocation_strategies, mst_allocation_strategy_details |
| **Portfolio** | portfolio, portfolio_transactions |
| **Legacy** | investment_plans, investment_plan_allocations |
| **Investment Sessions** | investment_sessions, investment_session_allocations, investment_session_events, investment_asset_selections |
| **User Finance** | user_cash_balance |
| **Asset Master** | asset_categories, asset_platforms, asset_products, asset_prices |

---

## 📊 Test Data Included

✅ **3 Investment Strategies:** SAFE, BALANCED, GROWTH
✅ **4 Asset Categories:** ETF, GOLD, STOCK, EMERGENCY
✅ **4 Trading Platforms:** VNDIRECT, SSI, VIETCAP, GOLD_MARKET
✅ **4 Sample Products:** VNI, VCI10, SJC_1D, TCB
✅ **4 Price Records:** Latest prices for each product

---

## 🔄 Full Reset Procedure

```sql
-- Step 1: Run the SQL file
SOURCE COMPLETE_SCHEMA_RESET.sql;

-- Step 2: Verify tables created (should show 15 tables)
SHOW TABLES;

-- Step 3: Verify test data
SELECT COUNT(*) FROM asset_categories;      -- Should return 4
SELECT COUNT(*) FROM asset_platforms;       -- Should return 4
SELECT COUNT(*) FROM asset_products;        -- Should return 4
SELECT COUNT(*) FROM asset_prices;          -- Should return 4
SELECT COUNT(*) FROM mst_allocation_strategies;  -- Should return 3
```

---

## 🛠️ For Backend Development

### **All 16 New Admin Endpoints Ready:**
- 4 endpoints for Category CRUD
- 4 endpoints for Platform CRUD
- 4 endpoints for Product CRUD
- 4 endpoints for Price CRUD

**All API implementations included in BE source code!**

---

## 📈 Key Features

✨ **3-Level Hierarchical Asset Structure**
- Category → Platform → Product → Price

✨ **Complete Audit Trail**
- All tables have created_at/updated_at
- investment_session_events for workflow tracking
- portfolio_transactions for all transactions

✨ **Real-time Price Management**
- is_latest flag for quick lookups
- Full price history maintained
- Locked prices at selection time

✨ **Backward Compatible**
- Legacy tables kept
- Old API still works
- Smooth migration path

---

## 🚀 Next Steps

1. **Run the SQL file** to create/reset database
2. **Start your BE application** - Flyway will recognize the schema
3. **Test admin APIs** with test data
4. **Create users** and test investment workflow
5. **Update prices** using admin endpoints

---

## ⚠️ Important

- The file includes `DROP TABLE IF EXISTS` statements
- **All existing tables will be deleted**
- **All existing data will be lost**
- **Use only for development/testing**

---

## 📋 File Contents Summary

**Total Lines:** ~600
**Tables:** 15
**Indexes:** 40+
**Foreign Keys:** 15+
**Test Records:** 20+

Each table includes:
- ✅ Full column definitions with comments
- ✅ Primary keys
- ✅ Appropriate indexes
- ✅ Foreign key constraints
- ✅ Timestamps (created_at, updated_at)
- ✅ UTF-8MB4 character set

---

**Created:** 2026-06-08  
**Version:** Complete Schema v1.0  
**Status:** Ready for Production

