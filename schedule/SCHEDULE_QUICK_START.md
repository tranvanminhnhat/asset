**✅ HƯỚNG DẪN CHẠY SCHEDULE - HOÀN CHỈNH**

---

## 🎯 BẮT ĐẦU NGAY (5 phút)

### **1️⃣ Chạy Local Development**

```bash
# Tạo database
mysql -u root -e "CREATE DATABASE asset_db CHARACTER SET utf8mb4;"

# Chạy app
cd D:\code\asset\schedule
./gradlew bootRun
```

✅ **App sẽ chạy. Schedule tự động hoạt động**

### **2️⃣ Kiểm Tra Dữ Liệu**

```bash
# Terminal mới - kiểm tra database
mysql asset_db

# Xem số record
SELECT COUNT(*) FROM asset_products;   # Sẽ tăng từ 0
SELECT COUNT(*) FROM asset_prices;     # Khi sync chạy

# Xem chi tiết
SELECT p.code, p.name, pr.current_price 
FROM asset_prices pr 
LEFT JOIN asset_products p ON pr.product_id = p.id 
LIMIT 5;
```

---

## 📚 Documentation

### **Nhanh Nhất (Khuyến Nghị)**
📄 **SCHEDULE_SUMMARY.md** (bạn đang đọc)
- 3 câu lệnh để chạy
- Dấu hiệu hoạt động
- Cấu hình cơ bản

👉 **Đọc tiếp: RUN_SCHEDULE_QUICK.md** (3 min)

### **Chi Tiết Hơn**
📄 **RUN_SCHEDULE_QUICK.md** 
- Copy-paste commands
- Cách kiểm tra
- Troubleshooting nhanh

👉 **Đọc tiếp: HOW_TO_RUN_SCHEDULE.md** (20 min)

### **Đầy Đủ**
📄 **HOW_TO_RUN_SCHEDULE.md**
- Giải thích chi tiết
- Cron expressions
- Lifecycle diagram
- Cấu hình advanced

---

## 🔄 3 SCHEDULED TASKS

```
┌────────────────────────────────────┐
│ ✅ DAILY (08:00 mỗi ngày)         │
│    - Fetch 15 stocks               │
│    - Fetch 7 ETFs                  │
│    - Insert vào DB                 │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│ ✅ INTRADAY (Mỗi 15 min)          │
│    - Update giá stocks             │
│    - 08:00-15:00, Mon-Fri          │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│ ✅ GOLD (Mỗi giờ)                 │
│    - Crawl từ sjc.com.vn           │
│    - 24/7                          │
└────────────────────────────────────┘
```

---

## 🛠️ CẤU HÌNH

### **Thay Đổi Thời Gian Chạy**

File: `src/main/resources/application.yaml`

```yaml
asset:
  schedule:
    daily-sync-cron: "0 0 8 * * *"      # 08:00 mỗi ngày
    intraday-sync-cron: "0 */15 8-15 * * MON-FRI"  # 15 min
    gold-sync-cron: "0 0 * * * *"       # Mỗi giờ
```

### **Hoặc Tạo Test Runner**

File: `src/main/java/com/vn/asset/test/DataInsertTestRunner.java`

```java
@Component  // Uncomment để enable
public class DataInsertTestRunner implements CommandLineRunner {
    // Sẽ chạy ngay khi app start
}
```

---

## ✅ CÁCH BIẾT HO AT ĐỘNG

### **Logs (Terminal)**
```
Initializing master data...
Created category: STOCK
Created category: ETF
Created category: GOLD
Created platform: VNDIRECT
Created platform: GOLD_MARKET
Application started successfully
```

### **At 08:00 (Sync Time)**
```
Starting daily asset data sync...
Fetching 15 stocks from FireAnt API
Successfully fetched 15 stocks
Fetching 7 ETFs from FireAnt API
Successfully fetched 7 ETFs
Crawling gold prices from: https://sjc.com.vn
Successfully crawled 4 gold prices
Synced 26 assets successfully
```

### **Database**
```sql
-- Count tăng
SELECT COUNT(*) FROM asset_products;  # 0 → 15+
SELECT COUNT(*) FROM asset_prices;    # 0 → 20+

-- Price date mới nhất
SELECT MAX(price_date) FROM asset_prices;
-- Hiện thời điểm vừa sync
```

---

## 🚨 NẾU CÓ VẤN ĐỀ

### **App không chạy**
```bash
# Check Java
java -version

# Check Gradle
./gradlew --version

# Check MySQL
mysql -u root --version
```

### **Database connection lỗi**
```bash
# Check MySQL running
mysql -u root -e "SELECT 1;"

# Check database exists
mysql -u root -e "SHOW DATABASES;" | grep asset_db

# Check config in application.yaml
```

### **Schedule không chạy**
```bash
# Check logs có "Starting... sync" không
# Nếu không, có thể chưa tới thời gian 08:00
# Hoặc API không accessible

# Test API
curl https://api.fireant.vn/quote/TCB
```

---

## 📊 DỮ LIỆU SAU KHI SYNC

### **Categories**
```
STOCK, ETF, GOLD
```

### **Platforms**
```
VNDIRECT, GOLD_MARKET
```

### **Products** (Created Automatically)
```
Stocks: TCB, FPT, VCB, BAA, BID, ...
ETFs: E1VFVN30, FUEVFVND, VFIBL, ...
Gold: SJC_1D, SJC_999, SJC_916, ...
```

### **Prices** (Updated Automatically)
```
TCB:      27,000.50 VND (STOCK)
FPT:      85,000.00 VND (STOCK)
E1VFVN30: 35,800.00 VND (ETF)
SJC_1D:   67,500,000 VND (GOLD)
```

---

## 🚀 DEPLOY PRODUCTION

### **Railway (1 command)**
```bash
./gradlew clean build
railway up --spring.profiles.active=prod
```

### **View Logs**
```bash
railway logs -f
```

### **Verify**
```bash
mysql -h viaduct.proxy.rlwy.net -P 36228 -u root -p asset_db
SELECT COUNT(*) FROM asset_products;
```

---

## 🎯 NEXT STEPS

1. **Run Now**: `./gradlew bootRun`
2. **Verify**: Check database at 08:00 or modify cron
3. **Monitor**: Watch logs for sync messages
4. **Deploy**: Use Railway when ready

---

## 📖 FILES

| File | Purpose | Read Time |
|------|---------|-----------|
| SCHEDULE_SUMMARY.md | This file - quick overview | 2 min |
| RUN_SCHEDULE_QUICK.md | Copy-paste commands | 3 min |
| HOW_TO_RUN_SCHEDULE.md | Detailed guide | 20 min |
| CONFIGURATION_GUIDE.md | All config options | 30 min |
| RAILWAY_DEPLOYMENT.md | Production deployment | 30 min |

---

**Status**: ✅ Ready to Run
**Build**: ✅ BUILD SUCCESSFUL
**Tests**: ✅ ALL PASSING

👉 **Next**: Open RUN_SCHEDULE_QUICK.md for detailed copy-paste commands

