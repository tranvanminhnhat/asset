# ⚡ Quick MySQL Setup (5 Minutes)

## 🎯 What Changed
✅ **H2 In-Memory → MySQL** (same as production)  
✅ Data now **persists** across app restarts  
✅ Can access database via SQL clients

---

## 🚀 Quick Setup (Choose One)

### Option A: Use Existing MySQL

If you already have MySQL running:

1. **Create database:**
   ```bash
   mysql -u root -p
   CREATE DATABASE asset_db;
   EXIT;
   ```

2. **Update credentials in `application.yaml` if needed:**
   ```yaml
   datasource:
     url: jdbc:mysql://localhost:3306/asset_db
     username: your_username
     password: your_password
   ```

3. **Run app:**
   ```bash
   ./gradlew bootRun
   ```

---

### Option B: Install MySQL Fresh (Windows)

1. **Download MySQL:** https://dev.mysql.com/downloads/mysql/
2. **Install with default settings**
3. **Start service** (MySQL should auto-start)
4. **Create database:**
   ```bash
   mysql -u root -p
   CREATE DATABASE asset_db;
   EXIT;
   ```
5. **Run app:**
   ```bash
   ./gradlew bootRun
   ```

---

### Option C: Use Production Database (Railway)

To connect to actual production database:

Edit `src/main/resources/application.yaml`:

```yaml
datasource:
  url: jdbc:mysql://viaduct.proxy.rlwy.net:36228/asset_db
  username: root
  password: MQTRfyRKCFZdhGrMpAbKXJNebafyHFLw
  driver-class-name: com.mysql.cj.jdbc.Driver
```

⚠️ **Warning:** This connects to PRODUCTION data

---

## ✅ Verify It Works

### Test 1: Via API
```bash
curl http://localhost:8080/api/data/status
```

Should show: `"categories": 3, "platforms": 2, "products": 4, "prices": 4`

### Test 2: Via SQL
```bash
mysql -u root -p asset_db
mysql> SELECT COUNT(*) FROM asset_categories;
# Should return: 3

mysql> SELECT COUNT(*) FROM asset_products;
# Should return: 4

mysql> EXIT;
```

---

## 🔧 Connection Defaults

```yaml
Host:     localhost
Port:     3306
Database: asset_db
Username: root
Password: root
Driver:   com.mysql.cj.jdbc.Driver
```

---

## 📁 Files Updated

- ✅ `src/main/resources/application.yaml` - MySQL config
- ✅ Tests still use H2 (unchanged)
- ✅ Production config unchanged in `application-prod.yaml`

---

## ⚠️ Common Issues

| Issue | Fix |
|-------|-----|
| Can't connect | Start MySQL service |
| Database not found | Run: `CREATE DATABASE asset_db;` |
| Wrong password | Update in `application.yaml` |
| Port 3306 in use | Change port in connection URL |

---

## 📚 Full Documentation

For detailed info, see: `MYSQL_SETUP_GUIDE.md`

---

**That's it! You're ready to go.** 🎉

Run: `./gradlew bootRun`

