## 📋 Complete Database Schema Documentation

**File:** `COMPLETE_SCHEMA_RESET.sql`

**Purpose:** Single comprehensive SQL file containing ALL tables needed for the Asset Investment Management System. Drop all existing tables and run this file to get a clean, working database.

---

## 🏗️ Database Schema Overview

### **15 Core Tables:**

#### 1️⃣ **Authentication & Users**
- **user** - User account information with JWT token support

#### 2️⃣ **Strategy & Allocation Master Data**
- **mst_allocation_strategies** - Master strategies (SAFE, BALANCED, GROWTH)
- **mst_allocation_strategy_details** - Allocation percentages per asset type

#### 3️⃣ **Portfolio Management**
- **portfolio** - User portfolio holdings summary
- **portfolio_transactions** - Append-only transaction audit trail

#### 4️⃣ **Investment Plans (Legacy - Backward Compatible)**
- **investment_plans** - Legacy investment plans
- **investment_plan_allocations** - Legacy allocations

#### 5️⃣ **Investment Sessions (New Workflow)**
- **investment_sessions** - Investment sessions with workflow state
- **investment_session_allocations** - Allocation targets for each asset
- **investment_session_events** - Audit trail of session events
- **investment_asset_selections** - Selected assets with locked prices

#### 6️⃣ **User Financial Data**
- **user_cash_balance** - Cash/emergency fund balance

#### 7️⃣ **Asset Management (Hierarchical Structure)**
- **asset_categories** - Asset types (ETF, GOLD, STOCK, EMERGENCY)
- **asset_platforms** - Trading platforms (VNDIRECT, SSI, VIETCAP, GOLD_MARKET)
- **asset_products** - Specific products with category & platform hierarchy
- **asset_prices** - Real-time & historical prices with is_latest tracking

---

## 📊 Data Architecture

### **3-Level Asset Hierarchy:**
```
Asset Category (ETF, GOLD, STOCK, EMERGENCY)
    ↓
Asset Platform (VNDIRECT, SSI, VIETCAP, GOLD_MARKET)
    ↓
Asset Product (VNI, VCI10, SJC_1D, TCB)
    ↓
Asset Prices (Real-time with history tracking)
```

### **Investment Workflow:**
```
User → Select Strategy → Create Session
         ↓
    Get Recommendations → For Each Asset Type:
         ↓
    1. Select Platform
    2. Select Product
    3. Enter Quantity
    4. Lock Price
    5. Save Selection
         ↓
    Review → Complete → Summary
```

---

## 🔑 Key Features

### ✅ **Fully Denormalized for Performance**
- Audit trails with category_code, platform_code, product_code, unit_name
- Locked prices at selection time (immutable)
- Price history with is_latest flag for easy lookups

### ✅ **Backward Compatibility**
- Legacy investment_plans tables kept
- Old portfolio & transaction tables works with new asset hierarchy
- service layer handles both old and new structures

### ✅ **Comprehensive Audit Trail**
- investment_session_events for workflow tracking
- portfolio_transactions for all investment transactions
- All tables have created_at/updated_at timestamps

### ✅ **Real-time Price Management**
- asset_prices with product_id foreign key
- is_latest flag for quick lookups
- Price history maintained for reporting
- Strategy: Scheduler updates every 1-10 minutes OR webhook push OR WebSocket stream

### ✅ **Foreign Key Constraints**
- Proper cascading deletes (DELETE CASCADE for transaction tables)
- Referential integrity maintained
- All relationships clearly documented

---

## 🚀 Usage Instructions

### **Option 1: Fresh Database Setup**
```sql
-- Connect to MySQL and drop existing database
DROP DATABASE asset_db;
CREATE DATABASE asset_db CHARACTER SET utf8mb4;
USE asset_db;

-- Run the complete schema file
SOURCE /path/to/COMPLETE_SCHEMA_RESET.sql;

-- Verify tables created
SHOW TABLES;

-- Verify test data
SELECT * FROM asset_categories;
SELECT * FROM asset_platforms;
SELECT * FROM asset_products;
SELECT * FROM asset_prices;
```

### **Option 2: Start Fresh on Existing Database**
```sql
-- The file includes DROP IF EXISTS statements
-- Simply run it directly - all tables will be recreated
SOURCE /path/to/COMPLETE_SCHEMA_RESET.sql;
```

---

## 📝 Test Data Included

### **Strategies (3 predefined):**
| Code | Name | Emergency | Gold | ETF | Stock |
|------|------|-----------|------|-----|-------|
| SAFE | Safe Investment Plan | 40% | 30% | 10% | 20% |
| BALANCED | Balanced Growth Strategy | 25% | 20% | 20% | 35% |
| GROWTH | Aggressive Growth Strategy | 10% | 10% | 30% | 50% |

### **Asset Categories (4):**
- ETF - Exchange Traded Funds
- GOLD - Precious metals
- STOCK - Stock market securities
- EMERGENCY - Emergency cash funds

### **Asset Platforms (4):**
- VNDIRECT
- SSI (Sai Gon Securities)
- VIETCAP
- GOLD_MARKET

### **Asset Products (4):**
| Code | Name | Category | Platform | Unit |
|------|------|----------|----------|------|
| VNI | Quy VN-Index ETF | ETF | VNDIRECT | don vi quy |
| VCI10 | Quy VCI10 ETF | ETF | VNDIRECT | don vi quy |
| SJC_1D | Vang SJC 1 chi | GOLD | GOLD_MARKET | chi |
| TCB | Techcombank Stock | STOCK | VNDIRECT | co phieu |

### **Asset Prices (4 sample):**
| Product | Buy Price | Sell Price |
|---------|-----------|------------|
| VNI | 100.50 | 100.00 |
| VCI10 | 250.75 | 250.00 |
| SJC_1D | 50,000.00 | 49,800.00 |
| TCB | 150.50 | 150.00 |

---

## 🔧 Backend Integration Points

### **Admin CRUD APIs:**
```
POST   /api/v1/admin/category          - Create category
GET    /api/v1/admin/category/{id}     - Get category
PUT    /api/v1/admin/category          - Update category
DELETE /api/v1/admin/category/{id}     - Delete category

POST   /api/v1/admin/platform          - Create platform
GET    /api/v1/admin/platform/{id}     - Get platform
PUT    /api/v1/admin/platform          - Update platform
DELETE /api/v1/admin/platform/{id}     - Delete platform

POST   /api/v1/admin/product           - Create product
GET    /api/v1/admin/product/{id}      - Get product
PUT    /api/v1/admin/product           - Update product
DELETE /api/v1/admin/product/{id}      - Delete product

POST   /api/v1/admin/price             - Create price
GET    /api/v1/admin/price/{id}        - Get price
PUT    /api/v1/admin/price             - Update price
DELETE /api/v1/admin/price/{id}        - Delete price
```

### **Investment Session APIs:**
```
POST   /api/v1/investment/session                      - Create session
GET    /api/v1/investment/recommendations/{sessionId}  - Get recommendations
POST   /api/v1/investment/select/{sessionId}/{allocationId}  - Select asset
GET    /api/v1/investment/review/{sessionId}           - Review session
POST   /api/v1/investment/complete/{sessionId}         - Complete session
```

---

## 📋 Table Statistics

| Table Name | Type | Records | Purpose |
|------------|------|---------|---------|
| user | Master | Variable | User accounts |
| mst_allocation_strategies | Master | 3 | Investment strategies |
| mst_allocation_strategy_details | Master | 12 | Strategy allocations |
| portfolio | Transactional | Variable | Portfolio holdings |
| portfolio_transactions | Transactional | Variable | Transaction audit |
| asset_categories | Master | 4 | Asset types |
| asset_platforms | Master | 4 | Trading platforms |
| asset_products | Master | 4+ | Products |
| asset_prices | Transactional | Variable | Price history |
| investment_sessions | Transactional | Variable | User sessions |
| investment_session_allocations | Transactional | Variable | Session targets |
| investment_session_events | Audit | Variable | Event trail |
| investment_asset_selections | Transactional | Variable | Selected assets |

---

## ⚠️ Important Notes

1. **Data Integrity:** All foreign key constraints are enforced. Cannot delete parent records with child records.

2. **Cascading Deletes:** 
   - Deleting a user cascades to portfolio, transactions, sessions, allocations
   - Deleting a session cascades to all related allocations, events, selections
   - Deleting a product cascades to all related prices

3. **Price Locking:** 
   - When user selects an asset, current price (is_latest=TRUE) is locked
   - This price cannot be changed - ensures immutable audit trail

4. **is_latest Flag:** 
   - Used to quickly find current prices
   - Old prices retained for history
   - Update strategy: Create new price record with is_latest=TRUE, set old is_latest=FALSE

5. **Character Set:** UTF-8MB4 (full Unicode support including Vietnamese characters)

6. **Auto-Increment Safety:** IDs use BIGINT (supports up to 9 quintillion records)

---

## 🔍 Query Examples

### **Get recommended allocation for strategy:**
```sql
SELECT asset_type, percentage 
FROM mst_allocation_strategy_details 
WHERE strategy_code = 'BALANCED';
```

### **Get latest prices for all products:**
```sql
SELECT 
    p.name, 
    ap.buy_price, 
    ap.sell_price, 
    ac.code as category, 
    apt.code as platform
FROM asset_prices ap
JOIN asset_products p ON ap.product_id = p.id
JOIN asset_categories ac ON p.category_id = ac.id
JOIN asset_platforms apt ON p.platform_id = apt.id
WHERE ap.is_latest = TRUE AND ap.is_active = TRUE;
```

### **Get user investment sessions:**
```sql
SELECT * FROM investment_sessions 
WHERE user_id = 'user-uuid' 
ORDER BY created_at DESC;
```

### **Get investment session summary:**
```sql
SELECT 
    asset_type,
    recommended_amount,
    invested_amount,
    residual_amount,
    status
FROM investment_session_allocations
WHERE session_id = 123;
```

---

## 📞 Support

For issues or questions about the schema:
1. Check table definitions with `DESCRIBE table_name;`
2. Verify foreign keys: `SHOW CREATE TABLE table_name;`
3. Check test data: `SELECT * FROM table_name LIMIT 5;`

---

**Last Updated:** 2026-06-08  
**Schema Version:** Complete V1  
**Encoding:** UTF-8MB4  
**Collation:** utf8mb4_unicode_ci

