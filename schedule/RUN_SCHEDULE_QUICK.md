# ⚡ Quick Start - Chạy Schedule Ngay

## 🎯 Cách Chạy (Copy & Paste)

### **1️⃣ Development - Local**

#### Setup (First Time)
```bash
# 1. Create database
mysql -u root -e "CREATE DATABASE asset_db CHARACTER SET utf8mb4;"

# 2. Navigate to project
cd D:\code\asset\schedule

# 3. Check configuration
# File: src/main/resources/application.yaml
# Make sure datasource URL is: jdbc:mysql://localhost:3306/asset_db
```

#### Run Application
```bash
./gradlew bootRun
```

**Expected Output:**
```
2026-07-06 10:30:07  INFO ... AssetApplication : Started AssetApplication in 6.123 seconds
2026-07-06 10:30:08  INFO ... DataInitializer : Initializing master data...
2026-07-06 10:30:08  INFO ... DataInitializer : Created category: STOCK
2026-07-06 10:30:08  INFO ... DataInitializer : Created category: ETF
2026-07-06 10:30:08  INFO ... DataInitializer : Created category: GOLD
2026-07-06 10:30:09  INFO ... DataInitializer : Created platform: VNDIRECT
2026-07-06 10:30:09  INFO ... DataInitializer : Created platform: GOLD_MARKET
2026-07-06 10:30:09  INFO ... AssetApplication : Application started successfully
```

✅ **App running! Schedule will fire at configured times (08:00, 08:15, 00:00, etc)**

#### Monitor Database
```bash
# Open new terminal - check data every minute
mysql asset_db

# Count data
SELECT COUNT(*) FROM asset_products;
SELECT COUNT(*) FROM asset_prices;

# View latest
SELECT p.code, pr.current_price FROM asset_prices pr 
LEFT JOIN asset_products p ON pr.product_id = p.id 
ORDER BY pr.created_at DESC LIMIT 5;
```

---

### **2️⃣ Production - Railway**

#### Setup (First Time)
```bash
# 1. Setup Railway MySQL database (via Dashboard)
# Host: viaduct.proxy.rlwy.net
# Port: 36228
# User: root
# Pass: MQTRfyRKCFZdhGrMpAbKXJNebafyHFLw

# 2. Verify file: src/main/resources/application-prod.yaml
# Has Railway connection string (already configured)
```

#### Deploy
```bash
# 1. Build JAR
./gradlew clean build

# 2. Deploy to Railway
railway up --spring.profiles.active=prod

# 3. View logs (real-time)
railway logs -f
```

**Expected Output:**
```
2026-07-06 10:30:07 INFO DataInitializer : Initializing master data...
2026-07-06 10:30:09 INFO DataInitializer : Created category: STOCK
2026-07-06 10:30:10 INFO DataInitializer : Created platform: VNDIRECT
2026-07-06 10:30:11 AssetApplication : Application started successfully
```

✅ **Deployed! Schedule running on Railway**

#### Verify Data
```bash
# Connect to Railway MySQL
mysql -h viaduct.proxy.rlwy.net -P 36228 -u root -pMQTRfyRKCFZdhGrMpAbKXJNebafyHFLw asset_db

# Check
SELECT COUNT(*) FROM asset_products;
SELECT COUNT(*) FROM asset_prices;
```

---

## 📅 Schedule Chạy Khi Nào?

### **3 Scheduled Tasks:**

```
┌─────────────────────────────────────┐
│ 1. DAILY SYNC (08:00 Every Day)    │
│    → Fetch 15 stocks from FireAnt   │
│    → Fetch 7 ETFs from FireAnt      │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ 2. INTRADAY (Every 15 min, 8-15h)  │
│    → Update stock prices during day │
│    → Mon-Fri only (trading hours)   │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ 3. GOLD (Every Hour)                │
│    → Crawl gold prices from SJC     │
│    → 24/7, every :00 minute         │
└─────────────────────────────────────┘
```

---

## 🔄 Nếu Muốn Chạy Ngay Lập Tức (Test)

### **Cách 1: Chỉnh Cron Expression**

Edit: `src/main/java/com/vn/asset/scheduler/AssetDataScheduler.java`

```java
// Change from:
@Scheduled(cron = "0 0 8 * * *")  // 08:00 daily

// To (every 5 minutes - for testing):
@Scheduled(cron = "0 */5 * * * *")

// Rebuild & run
./gradlew clean build
./gradlew bootRun

// Now should see sync in logs every 5 minutes
```

### **Cách 2: Dùng Test Component**

Uncomment `@Component` in: `src/main/java/com/vn/asset/test/DataInsertTestRunner.java`

```java
@Component  // ← Uncomment this
public class DataInsertTestRunner implements CommandLineRunner {
    // ... runs on startup
}
```

Then:
```bash
./gradlew bootRun  # Will sync data immediately on startup
```

---

## ✅ Cách Biết Schedule Đang Chạy?

### **Kiểm Tra Logs**

```bash
# Look for these messages:

# 1. Startup initialization
"Initializing master data..."
"Created category: STOCK"
"Created platform: VNDIRECT"

# 2. Daily sync (at 08:00)
"Starting daily asset data sync..."
"Fetching 15 stocks from FireAnt API"
"Successfully fetched 15 stocks"
"Fetching 7 ETFs from FireAnt API"
"Successfully fetched 7 ETFs"
"Crawling gold prices"
"Successfully crawled 4 gold prices"

# 3. Data insert
"Synced 26 assets successfully"
```

### **Kiểm Tra Database**

```bash
# Count records
mysql asset_db -e "SELECT COUNT(*) FROM asset_products;"
# Should increase from 0 → 15+

mysql asset_db -e "SELECT COUNT(*) FROM asset_prices;"
# Should increase from 0 → 20+

# Check latest prices
mysql asset_db -e "
SELECT p.code, pr.current_price, pr.asset_type, pr.price_date
FROM asset_prices pr
LEFT JOIN asset_products p ON pr.product_id = p.id
ORDER BY pr.price_date DESC LIMIT 5
;"
```

---

## 🎯 Complete Workflow

### **Development - Start to Finish**

```bash
# 1. Setup
mysql -u root -e "CREATE DATABASE asset_db CHARACTER SET utf8mb4;"

# 2. Run
cd D:\code\asset\schedule
./gradlew bootRun

# 3. In another terminal - Monitor
mysql asset_db
SELECT COUNT(*) FROM asset_products;

# 4. Wait for 08:00 or modify cron for testing

# 5. Check logs in first terminal for sync messages

# 6. Verify data in second terminal - count should increase
SELECT COUNT(*) FROM asset_products;  # 0 → 15+
SELECT COUNT(*) FROM asset_prices;    # 0 → 20+
```

### **Production - Start to Finish**

```bash
# 1. Build
./gradlew clean build

# 2. Deploy
railway up --spring.profiles.active=prod

# 3. Monitor
railway logs -f

# 4. Verify
mysql -h viaduct.proxy.rlwy.net -P 36228 -u root -p asset_db
SELECT COUNT(*) FROM asset_products;
```

---

## 📊 Expected Data After Sync

### **Categories**
```
+----+--------+-------+
| id | code   | name  |
+----+--------+-------+
| 1  | STOCK  | Stock |
| 2  | ETF    | ETF   |
| 3  | GOLD   | Gold  |
+----+--------+-------+
```

### **Platforms**
```
+----+------------+---------+
| id | code       | name    |
+----+------------+---------+
| 1  | VNDIRECT   | VNDirect |
| 2  | GOLD_MARKET| Gold     |
+----+------------+---------+
```

### **Products (after sync)**
```
+----+-----+------------------+-----------+
| id | code| name             | category  |
+----+-----+------------------+-----------+
| 1  | TCB | Techcombank      | STOCK     |
| 2  | FPT | FPT Corporation  | STOCK     |
| 3  | VCB | VietcomBank      | STOCK     |
| 4  | E1V | E1VFVN30 ETF     | ETF       |
| 5  | SJC | SJC 1 Chỉ        | GOLD      |
+----+-----+------------------+-----------+
```

### **Prices (sample)**
```
+----+--------+-------+------------------+----------+
| id | code   | price | asset_type       | date     |
+----+--------+-------+------------------+----------+
| 1  | TCB    | 27000 | STOCK|2026-07-06 |
| 2  | FPT    | 85000 | STOCK|2026-07-06 |
| 3  | E1V    | 35800 | ETF  |2026-07-06 |
| 4  | SJC    | 67.5M | GOLD |2026-07-06 |
+----+--------+-------+------------------+----------+
```

---

## 🔍 Kiểm Tra Config

```bash
# Check what database is configured
cat src/main/resources/application.yaml | grep datasource -A 3

# Check schedule times
cat src/main/resources/application.yaml | grep schedule -A 5

# Check API endpoints
cat src/main/resources/application.yaml | grep "api:" -A 10
```

---

## 🛑 Nếu Có Lỗi

### **"Cannot connect to database"**
```bash
# Check MySQL is running
mysql -u root -e "SELECT 1;"

# Check database exists
mysql -u root -e "SHOW DATABASES;" | grep asset_db

# Check config URL
cat src/main/resources/application.yaml | grep "datasource:"
```

### **"Cannot fetch stocks from API"**
```bash
# Test API access
curl https://api.fireant.vn/quote/TCB

# Check internet connection
ping google.com
```

### **"Cannot remove old prices"**
```bash
# Check database permissions
mysql -u root asset_db -e "SHOW TABLES;"

# Check foreign key constraints
mysql -u root asset_db -e "SHOW CREATE TABLE asset_prices\G"
```

---

## 📚 Learn More

- **HOW_TO_RUN_SCHEDULE.md** - Full detailed guide
- **CONFIGURATION_GUIDE.md** - Custom config options
- **RAILWAY_DEPLOYMENT.md** - Production deployment details

---

**Status**: ✅ Ready to Run
**Time to Start**: ~5 minutes

