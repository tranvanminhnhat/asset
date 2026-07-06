# 📅 Cách Chạy Schedule - Hướng Dẫn Đầy Đủ

## 🎯 Tóm Tắt Nhanh

### **Cách 1: Chạy Development (Local)**
```bash
# Tạo database
mysql -u root
CREATE DATABASE asset_db CHARACTER SET utf8mb4;
exit

# Chạy application
cd D:\code\asset\schedule
./gradlew bootRun
```

**Logs sẽ hiện:**
```
... DataInitializer : Initializing master data...
... AssetDataScheduler : Starting daily asset data sync...
... FireantApiService : Fetching 15 stocks from FireAnt API
... Successfully fetched 15 stocks
```

### **Cách 2: Chạy Production (Railway)**
```bash
# Build JAR
./gradlew clean build

# Deploy
railway up --spring.profiles.active=prod
```

---

## 📋 Chi Tiết: Schedule Là Gì?

### **3 Scheduled Tasks Được Cấu Hình:**

```
┌─────────────────────────────────────────────────────────┐
│ 1️⃣  DAILY SYNC (Mỗi ngày lúc 08:00)                   │
│    - Lấy tất cả stocks từ FireAnt API                   │
│    - Lấy tất cả ETFs từ FireAnt API                     │
│    - Insert/update vào asset_products & asset_prices    │
├─────────────────────────────────────────────────────────┤
│ 2️⃣  INTRADAY SYNC (Mỗi 15 phút, 08:00-15:00 weekday)  │
│    - Lấy giá stock mới nhất                             │
│    - Update asset_prices trong giờ giao dịch            │
├─────────────────────────────────────────────────────────┤
│ 3️⃣  GOLD PRICE SYNC (Mỗi giờ)                          │
│    - Crawl giá vàng từ https://sjc.com.vn              │
│    - Insert vào asset_prices                            │
└─────────────────────────────────────────────────────────┘
```

---

## 🚀 Cách Chạy Chi Tiết

### **Option 1: Development (Local MySQL)**

#### Step 1: Chuẩn Bị Database
```bash
# Login MySQL
mysql -h localhost -u root -p

# Tạo database
CREATE DATABASE asset_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE asset_db;

# Thoát
exit
```

#### Step 2: Cấu Hình application.yaml
File: `src/main/resources/application.yaml`

```yaml
spring:
  application:
    name: asset
  datasource:
    url: jdbc:mysql://localhost:3306/asset_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
    username: root
    password: ''  # Để trống nếu không có password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

#### Step 3: Chạy Application
```bash
cd D:\code\asset\schedule
./gradlew bootRun
```

#### Step 4: Xem Logs
```
2026-07-06 10:30:00.123  INFO 12345 --- [main] c.v.a.AssetApplication : Starting AssetApplication
2026-07-06 10:30:05.456  INFO 12345 --- [main] c.v.a.startup.DataInitializer : Initializing master data...
2026-07-06 10:30:05.789  INFO 12345 --- [main] c.v.a.startup.DataInitializer : Created category: STOCK
2026-07-06 10:30:05.812  INFO 12345 --- [main] c.v.a.startup.DataInitializer : Created category: ETF
2026-07-06 10:30:05.835  INFO 12345 --- [main] c.v.a.startup.DataInitializer : Created category: GOLD
2026-07-06 10:30:06.123  INFO 12345 --- [main] c.v.a.startup.DataInitializer : Created platform: VNDIRECT
2026-07-06 10:30:06.234  INFO 12345 --- [main] c.v.a.startup.DataInitializer : Created platform: GOLD_MARKET
2026-07-06 10:30:07.000  INFO 12345 --- [main] c.v.a.AssetApplication : Application started in 6.123 seconds
```

✅ **Application chạy thành công!**

#### Step 5: Xem Schedule Chạy

Mở terminal mới, kiểm tra database:
```bash
# Login MySQL
mysql -h localhost -u root -p asset_db

# Xem dữ liệu
SELECT COUNT(*) as total FROM asset_categories;
SELECT COUNT(*) as total FROM asset_platforms;
SELECT COUNT(*) as total FROM asset_products;
SELECT COUNT(*) as total FROM asset_prices;

# Xem chi tiết
SELECT code, name FROM asset_products;
SELECT p.code, p.name, pr.current_price, pr.asset_type 
FROM asset_prices pr 
LEFT JOIN asset_products p ON pr.product_id = p.id;
```

---

### **Option 2: Chạy Vào Vào Nhất Định**

Nếu bạn muốn **schedule chạy ngay lập tức** thay vì chờ 08:00 sáng:

#### Cách 1: Chỉnh Sửa Cron Expression

File: `src/main/java/com/vn/asset/scheduler/AssetDataScheduler.java`

```java
// Chạy vào thời điểm cụ thể
// Hiện tại (08:00 mỗi ngày)
@Scheduled(cron = "0 0 8 * * *")

// Thay đổi thành chạy 2 lần mỗi ngày (08:00 + 13:00)
@Scheduled(cron = "0 0 8,13 * * *")

// Hoặc chạy mỗi 5 phút (để test nhanh)
@Scheduled(cron = "0 */5 * * * *")
```

#### Cách 2: Dùng Test Runner (chạy ngay lập tức)

File: `src/main/java/com/vn/asset/test/DataInsertTestRunner.java`

```java
// Bỏ comment để enable
@Component
public class DataInsertTestRunner implements CommandLineRunner {
    ...
    public void run(String... args) throws Exception {
        // Chạy ngay khi app start
        insertData();
    }
}
```

Hoặc gọi service trực tiếp:

```java
@Component
public class SyncOnStartupRunner implements CommandLineRunner {
    @Autowired
    private AssetDataSyncService syncService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Syncing data on startup...");
        syncService.syncStockData();
        syncService.syncEtfData();
        syncService.syncGoldData();
        log.info("Sync completed!");
    }
}
```

---

### **Option 3: Production (Railway)**

#### Step 1: Build JAR
```bash
./gradlew clean build
# Tạo file: build/libs/asset-0.0.1-SNAPSHOT.jar
```

#### Step 2: Cấu Hình Production
File: `src/main/resources/application-prod.yaml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://viaduct.proxy.rlwy.net:36228/asset_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
    username: root
    password: 'MQTRfyRKCFZdhGrMpAbKXJNebafyHFLw'
```

#### Step 3: Deploy lên Railway
```bash
# Cách 1: Dùng Railway CLI
railway login
railway up --spring.profiles.active=prod

# Cách 2: Dùng GitHub + Railway Auto-Deploy
# Push code lên GitHub, Railway tự deploy
```

#### Step 4: Xem Logs trên Railway
```bash
# Xem real-time logs
railway logs -f

# Output:
# ... DataInitializer : Initializing master data...
# ... FireantApiService : Fetching 15 stocks
# ... Successfully inserted 15 asset_products
# ... Successfully inserted 15 asset_prices
```

#### Step 5: Verify Data on Production Database
```bash
# Connect Railway MySQL
mysql -h viaduct.proxy.rlwy.net -P 36228 -u root -pMQTRfyRKCFZdhGrMpAbKXJNebafyHFLw asset_db

# Check data
SELECT COUNT(*) FROM asset_products;
SELECT COUNT(*) FROM asset_prices;

# View latest prices
SELECT p.code, pr.current_price, pr.asset_type, pr.price_date
FROM asset_prices pr
LEFT JOIN asset_products p ON pr.product_id = p.id
ORDER BY pr.price_date DESC
LIMIT 10;
```

---

## ⏱️ Cron Expression Tham Khảo

### **Format:**
```
┌─────────── second (0-59)
│ ┌─────────── minute (0-59)
│ │ ┌──────────── hour (0-23)
│ │ │ ┌───────────── day of month (1-31)
│ │ │ │ ┌────────────── month (1-12)
│ │ │ │ │ ┌─────────────── day of week (0-7, 0/7=Sunday, 1-6=Mon-Sat)
│ │ │ │ │ │
; * * * * * *
```

### **Ví Dụ:**

| Cron Expression | Ý Nghĩa | Khi Nào Chạy |
|-----------------|---------|------------|
| `0 0 8 * * *` | Mỗi ngày lúc 08:00 | 8:00 AM hàng ngày |
| `0 0 8,13 * * *` | 08:00 và 13:00 | 8 AM & 1 PM hàng ngày |
| `0 */15 8-15 * * MON-FRI` | Mỗi 15 phút từ 8-15h, weekday | Mỗi 15 phút trong giờ giao dịch |
| `0 0 * * * *` | Mỗi giờ | :00 của mỗi giờ |
| `0 */5 * * * *` | Mỗi 5 phút | Để test |
| `0 0 0 * * *` | Nửa đêm hàng ngày | 00:00 (midnight) |
| `0 0 8 1 * *` | Lúc 08:00 ngày 1 hàng tháng | Đầu tháng 8 AM |

---

## 🔍 Giám Sát Schedule Chạy

### **1. Kiểm Tra Logs Realtime**

```bash
# Terminal 1: Chạy app
./gradlew bootRun

# Terminal 2: Lọc schedule logs
./gradlew bootRun 2>&1 | grep -i "schedule\|fireant\|gold\|inserted"

# Hoặc trên Production
railway logs -f | grep -i "schedule\|fireant\|gold"
```

### **2. Query Database để Xem Data Insert**

```bash
# Trước sync
mysql asset_db -e "SELECT COUNT(*) as before FROM asset_products;"

# Sau khi schedule chạy (chờ 08:00 hoặc custom time)
mysql asset_db -e "SELECT COUNT(*) as after FROM asset_products;"

# Xem detail
mysql asset_db -e "SELECT p.code, p.name, pr.current_price, pr.price_date 
FROM asset_prices pr 
LEFT JOIN asset_products p ON pr.product_id = p.id 
ORDER BY pr.created_at DESC LIMIT 10;"
```

### **3. Kiểm Tra File Log (Production)**

```bash
# Nếu bật logging to file (application-prod.yaml)
tail -f logs/asset-scheduler.log

# Grep for sync operations
tail -f logs/asset-scheduler.log | grep -i "sync\|fireant\|gold\|inserted"
```

---

## ✅ Cách Biết Schedule Đang Chạy?

### **Dấu Hiệu Thành Công:**

1. **Logs hiện:**
   ```
   AssetDataScheduler : Starting daily asset data sync...
   FireantApiService : Fetching 15 stocks from FireAnt API
   FireantApiService : Successfully fetched 15 stocks
   GoldCrawlerService : Crawling gold prices from: https://sjc.com.vn
   GoldCrawlerService : Successfully crawled 4 gold prices
   ```

2. **Database được update:**
   ```sql
   -- Count tăng lên
   SELECT COUNT(*) FROM asset_products;  -- Tăng từ 0 → 15+
   SELECT COUNT(*) FROM asset_prices;    -- Tăng từ 0 → 20+
   ```

3. **Price Date mới:**
   ```sql
   SELECT MAX(price_date) FROM asset_prices;
   -- Hiện thời điểm vừa sync
   ```

### **Dấu Hiệu Lỗi:**

1. **Logs hiện error:**
   ```
   Error fetching stock TCB from FireAnt API
   API error fetching ETF from FireAnt
   Error crawling SJC gold prices
   ```

2. **Database không update:**
   - Count vẫn cũ
   - Price date cũ

3. **Kiểm tra:**
   - API có accessible? `curl https://api.fireant.vn/quote/TCB`
   - Database connect ok? `mysql asset_db -e "SELECT 1;"`
   - Config đúng? `./gradlew bootRun | grep "datasource"`

---

## 🔧 Cấu Hình Schedule Custom

### **Thay Đổi Cron Expression:**

File: `src/main/resources/application.yaml`

```yaml
asset:
  schedule:
    # Daily sync at 08:00
    daily-sync-cron: "0 0 8 * * *"
    
    # Intraday every 15 minutes during trading hours
    intraday-sync-cron: "0 */15 8-15 * * MON-FRI"
    
    # Gold prices every hour
    gold-sync-cron: "0 0 * * * *"
    
    # Timezone
    timezone: Asia/Ho_Chi_Minh
```

### **Rebuild & Deploy:**

```bash
./gradlew clean build
./gradlew bootRun  # Dev
# hoặc
railway up --spring.profiles.active=prod  # Prod
```

---

## 📊 Vòng Đời Schedule

```
Application Start
        ↓
DataInitializer runs
        ↓
Create Categories (STOCK, ETF, GOLD)
Create Platforms (VNDIRECT, GOLD_MARKET)
        ↓
Schedule tasks registered & waiting
        ↓
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
08:00 → Daily Sync fires
        ├─ Fetch 15 stocks from FireAnt
        ├─ Fetch 7 ETFs from FireAnt
        ├─ Insert into asset_products & asset_prices
        │
08:15 → Intraday Sync fires
09:30 → Intraday Sync fires (every 15 min)
...
15:00 → Intraday Sync stops (trading hours over)
        │
00:00 → Gold Sync fires (every hour)
01:00 → Gold Sync fires
...
08:00 → Daily Sync fires again
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
(Repeat every day)
```

---

## 📝 Command Examples

### **Development - Full Workflow**

```bash
# 1. Create database
mysql -u root -e "CREATE DATABASE asset_db CHARACTER SET utf8mb4;"

# 2. Configure application.yaml with localhost database

# 3. Build & Run
cd D:\code\asset\schedule
./gradlew clean build
./gradlew bootRun

# 4. In another terminal, monitor
mysql asset_db -e "SELECT COUNT(*) FROM asset_products;" 
# Check every few minutes to see count increase

# 5. View logs in main terminal for sync messages
```

### **Production - Azure/Railway**

```bash
# 1. Build JAR
./gradlew clean build

# 2. Deploy
railway up --spring.profiles.active=prod

# 3. View logs
railway logs -f

# 4. Verify database
mysql -h HOST -u USER -p PASSWORD DATABASE \
  -e "SELECT COUNT(*) FROM asset_products;"
```

### **Testing - Run Schedule Immediately**

```bash
# Modify cron to run every 5 minutes for testing
# Change in AssetDataScheduler.java:
# @Scheduled(cron = "0 */5 * * * *")

./gradlew bootRun

# Watch logs - should see sync every 5 minutes
```

---

## 🎯 Troubleshooting Schedule

| Problem | Cause | Solution |
|---------|-------|----------|
| Schedule không chạy | App chưa start | Kiểm tra app logs |
| Logs hiện error | API unreachable | Check internet, test curl |
| Database không update | FK constraint | Check categories/platforms exist |
| Data không insert | Wrong config | Verify application.yaml |
| Performance chậm | API timeout | Increase timeout in config |

---

## 📚 Xem Thêm

- `CONFIGURATION_GUIDE.md` - Cấu hình chi tiết
- `RAILWAY_DEPLOYMENT.md` - Deploy production
- `API_INTEGRATION_GUIDE.md` - Chi tiết API endpoints
- `README.md` - Project overview

---

**Status**: ✅ Schedule Configured & Ready to Run
**Build**: ✅ BUILD SUCCESSFUL
**Tests**: ✅ ALL PASSING

