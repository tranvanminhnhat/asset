# 🎯 Cách Chạy Schedule - Tóm Tắt Nhanh

## ⚡ 3 Câu Lệnh Để Chạy

### **1. Development (Local)**
```bash
cd D:\code\asset\schedule
./gradlew bootRun
```

### **2. Production (Railway)**
```bash
./gradlew clean build
railway up --spring.profiles.active=prod
```

### **3. Test Ngay Lập Tức**
```bash
# Sửa cron sau đó run:
./gradlew bootRun
```

---

## 📅 Schedule Chạy Tự Động

### **3 Tasks:**
```
✅ 08:00 mỗi ngày  → Lấy stocks + ETFs từ FireAnt
✅ Mỗi 15 phút      → Update giá stock (giờ giao dịch)
✅ Mỗi giờ          → Crawl giá vàng từ SJC.com.vn
```

### **Flow:**
```
Application Start
    ↓
Create master data (categories, platforms)
    ↓
Schedule tasks registered
    ↓
Automatic sync at configured times:
  • Fetch real data from APIs
  • Insert into database
  • Mark old prices as not latest
```

---

## 🔍 Cách Kiểm Tra

### **Logs (Real-time)**
```bash
# In application - look for:
"Starting daily asset data sync..."
"Fetching 15 stocks from FireAnt API"
"Successfully fetched"
"Created asset_products"
"Inserted asset_prices"
```

### **Database**
```bash
# New terminal:
mysql asset_db

# Count data (should increase):
SELECT COUNT(*) FROM asset_products;      # 0 → 15+
SELECT COUNT(*) FROM asset_prices;        # 0 → 20+

# View latest prices:
SELECT p.code, pr.current_price, pr.asset_type
FROM asset_prices pr
LEFT JOIN asset_products p ON pr.product_id = p.id
ORDER BY pr.created_at DESC LIMIT 5;
```

---

## 🛠️ Cấu Hình

### **Điểm Chạy (Cron Expression)**

File: `src/main/resources/application.yaml`

```yaml
asset:
  schedule:
    daily-sync-cron: "0 0 8 * * *"           # 08:00 mỗi ngày
    intraday-sync-cron: "0 */15 8-15 * * MON-FRI"  # 15 phút/lần
    gold-sync-cron: "0 0 * * * *"            # Mỗi giờ
```

Hoặc chỉnh ASsetDataScheduler.java:
```java
@Scheduled(cron = "0 */5 * * * *")  // Test: mỗi 5 phút
```

---

## 📊 Dữ Liệu Được Insert

### **Sau Khi Schedule Chạy:**

**Categories:**
- STOCK (Chứng chỉ)
- ETF (Quỹ)
- GOLD (Vàng)

**Platforms:**
- VNDIRECT (Giao dịch chứng chỉ)
- GOLD_MARKET (Thị trường vàng)

**Products (được tạo tự động):**
- TCB, FPT, VCB (Stocks)
- E1VFVN30, FUEVFVND (ETFs)
- SJC_1D, SJC_999 (Gold)

**Prices (được update tự động):**
- Giá mua/bán, ngày update, loại asset

---

## ✅ Dấu Hiệu Hoạt Động

### **Good ✅**
- Logs hiện "Starting... sync..."
- Database count tăng
- Price date mới nhất
- Không có errors

### **Bad ❌**
- Logs không hiện sync message
- Database count không tăng
- Error trong logs
- API unreachable

---

## 🎓 Hướng Dẫn Chi Tiết

| File | Nội Dung | Độ Dài |
|------|----------|--------|
| **RUN_SCHEDULE_QUICK.md** | Copy-paste commands | 3 min |
| **HOW_TO_RUN_SCHEDULE.md** | Đầy đủ chi tiết | 20 min |
| **CONFIGURATION_GUIDE.md** | Cấu hình tùy chỉnh | 30 min |
| **RAILWAY_DEPLOYMENT.md** | Deploy production | 30 min |

👉 **Đọc RUN_SCHEDULE_QUICK.md trước!**

---

## 🚀 Bắt Đầu Ngay

```bash
# 1. Setup database
mysql -u root -e "CREATE DATABASE asset_db CHARACTER SET utf8mb4;"

# 2. Run app
cd D:\code\asset\schedule
./gradlew bootRun

# 3. Monitor (in another terminal)
mysql asset_db -e "SELECT COUNT(*) FROM asset_products;"

# 4. Check logs for sync messages
# Look for "Starting daily asset data sync..." at 08:00
```

✅ **Xong! Schedule sẽ chạy tự động**

---

**Next Steps:**
- Read `RUN_SCHEDULE_QUICK.md` for detailed copy-paste commands
- Read `HOW_TO_RUN_SCHEDULE.md` for full understanding
- Deploy to production when ready (see `RAILWAY_DEPLOYMENT.md`)

**Build Status**: ✅ BUILD SUCCESSFUL
**Tests**: ✅ ALL PASSING
**Ready**: ✅ TO RUN

