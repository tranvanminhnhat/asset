# 📊 DATABASE MIGRATION SUMMARY: H2 → MySQL

## ✅ Changes Completed

### 1. Updated application.yaml
**File:** `src/main/resources/application.yaml`

**Before (H2 In-Memory):**
```yaml
datasource:
  url: jdbc:h2:mem:asset_db;MODE=MySQL;...
  driver-class-name: org.h2.Driver
jpa:
  hibernate:
    ddl-auto: create-drop  # Drops tables on restart
```

**After (MySQL - Like Production):**
```yaml
datasource:
  url: jdbc:mysql://localhost:3306/asset_db
  username: root
  password: root
  driver-class-name: com.mysql.cj.jdbc.Driver
jpa:
  hibernate:
    ddl-auto: update  # Updates schema safely
```

---

## 🎯 Key Differences

| Feature | H2 (Old) | MySQL (New) |
|---------|----------|-----------|
| **Storage** | In-Memory (RAM) | Persistent Disk |
| **Data Persistence** | Lost on restart ❌ | Persists ✅ |
| **Production-like** | No ❌ | Yes ✅ |
| **Schema Updates** | Drop & Recreate | Update Safely |
| **Startup Time** | Fast (empty DB) | ~1-2 sec |
| **SQL Access** | Via API only | Via MySQL CLI |

---

## 📋 Default Connection Settings

**Host:** localhost  
**Port:** 3306  
**Database:** asset_db  
**Username:** root  
**Password:** root  

**Change if needed:** Edit `src/main/resources/application.yaml` lines 8-11

---

## ✨ Benefits of MySQL

### 1. **Data Persistence**
- Data survives app restarts
- Data survives machine restarts
- Can backup database

### 2. **Production Parity**
- Same database type as production
- Same configuration as `application-prod.yaml`
- Easier to test production scenarios

### 3. **Better Development**
- Can access data via SQL tools (MySQL Workbench, DBeaver)
- Real-time debugging
- Database inspection

### 4. **Safe Schema Updates**
- ddl-auto: update preserves data
- Doesn't drop tables
- Backward compatible

---

## 🚀 How to Get Started

### Step 1: Install MySQL (if needed)
```bash
# Windows: Download from mysql.com
# macOS: brew install mysql
# Linux: apt-get install mysql-server
```

### Step 2: Create Database
```bash
mysql -u root -p
CREATE DATABASE asset_db;
EXIT;
```

### Step 3: Run Application
```bash
cd D:\code\asset\schedule
./gradlew bootRun
```

### Step 4: Verify Data Insertion
```bash
# Check via API:
curl http://localhost:8080/api/data/status

# Check via MySQL:
mysql -u root -p asset_db
mysql> SELECT * FROM asset_categories;
```

---

## 📊 Expected Data After Startup

```sql
-- Categories (3)
SELECT * FROM asset_categories;
-- STOCK, ETF, GOLD

-- Platforms (2)
SELECT * FROM asset_platforms;
-- VNDIRECT, GOLD_MARKET

-- Products (4)
SELECT * FROM asset_products;
-- TCB, FPT, E1VFVN30, SJC_1D

-- Prices (4)
SELECT * FROM asset_prices;
-- Prices for each product
```

---

## 🔧 Configuration Profiles

| Profile | Database | Usage |
|---------|----------|-------|
| **default** | MySQL (localhost) | Development |
| **test** | H2 In-Memory | Unit Tests |
| **prod** | MySQL (Railway) | Production |

### Running Different Profiles

```bash
# Development (default)
./gradlew bootRun

# Production
./gradlew bootRun --args='--spring.profiles.active=prod'

# Tests (automatic)
./gradlew test
```

---

## ⚡ Quick Decision Guide

### Use **MySQL Local** (Default)
```yaml
url: jdbc:mysql://localhost:3306/asset_db
username: root
password: root
```
✅ Best for: Development, Testing, Learning

### Use **Production Railway**
```yaml
url: jdbc:mysql://viaduct.proxy.rlwy.net:36228/asset_db
username: root
password: MQTRfyRKCFZdhGrMpAbKXJNebafyHFLw
```
⚠️ Be careful: Affects real production data!

---

## 📝 Important Notes

### 1. Database Creation
- Database must exist before app starts
- Script will auto-create tables
- But you must create database manually:
  ```bash
  CREATE DATABASE asset_db;
  ```

### 2. Credentials
- Default: root / root
- Change in application.yaml if different
- Don't commit passwords to Git (consider .env later)

### 3. ddl-auto: update Behavior
- **Creates** tables if missing ✅
- **Updates** columns if needed ✅
- **Doesn't drop** tables ✅
- **Doesn't delete** data ✅
- Safe for production ✅

### 4. Data Backup
- Regular MySQL backups recommended:
  ```bash
  mysqldump -u root -p asset_db > backup.sql
  ```

---

## 🧪 Testing

### Unit Tests (Still Use H2)
```bash
./gradlew test
# Tests automatically use H2 in-memory
# No MySQL needed for tests
```

### Integration Tests
```bash
./gradlew test --tests "AssetDataInsertIntegrationTest"
# Tests MySQL data insertion logic
```

### Full Build
```bash
./gradlew clean build
# Compiles + runs all tests
```

---

## 📈 Migration Status

| Task | Status | Details |
|------|--------|---------|
| Update application.yaml | ✅ Done | MySQL config applied |
| Build verification | ✅ Done | BUILD SUCCESSFUL |
| Test compatibility | ✅ OK | Tests still use H2 |
| Documentation | ✅ Done | Full guides provided |
| Data insertion | ✅ Works | Via DataInsertTestRunner |
| API endpoints | ✅ Works | /api/data/* ready |

---

## 📚 Documentation Files Created

1. **QUICK_MYSQL_SETUP.md** - 5-minute quick start
2. **MYSQL_SETUP_GUIDE.md** - Detailed comprehensive guide
3. **DATABASE_MIGRATION_SUMMARY.md** - This file

---

## 🎯 Next Steps

1. ✅ **Install MySQL** (if not already)
2. ✅ **Create database:** `CREATE DATABASE asset_db;`
3. ✅ **Update credentials** (if different from root/root)
4. ✅ **Run app:** `./gradlew bootRun`
5. ✅ **Verify:** `GET http://localhost:8080/api/data/status`
6. ✅ **Check DB:** `mysql -u root -p asset_db`

---

## ✅ Verification Checklist

Before running the app:

- [ ] MySQL installed and running
- [ ] Database `asset_db` created
- [ ] application.yaml has correct credentials
- [ ] Port 3306 is available
- [ ] No firewall blocking localhost:3306

After running the app:

- [ ] App starts without errors
- [ ] Logs show: "STARTING DATA INSERT TEST"
- [ ] API returns: `"total": 13` records
- [ ] MySQL shows tables created
- [ ] Data persists after restart

---

## 🆘 Troubleshooting

### "Access denied for user 'root'@'localhost'"
```bash
mysql -u root -p
# Enter password when prompted
```

### "Can't connect to MySQL server"
```bash
# Windows
net start MySQL80
# macOS
brew services start mysql
# Linux
sudo service mysql start
```

### "Unknown database 'asset_db'"
```bash
mysql -u root -p
CREATE DATABASE asset_db CHARACTER SET utf8mb4;
```

---

## 📞 Support

For detailed setup help, see:
- **QUICK_MYSQL_SETUP.md** (5 minutes)
- **MYSQL_SETUP_GUIDE.md** (Complete reference)

---

**Status:** ✅ COMPLETE - Ready for MySQL Development  
**Date:** 2026-07-06  
**Configuration:** Production-like Setup  
**Data Persistence:** ✅ Enabled

