# MySQL Database Setup Guide

## 📝 Configuration Changed
Updated `src/main/resources/application.yaml` to use **MySQL instead of H2 in-memory database**.

### Before (H2 In-Memory)
```yaml
datasource:
  url: jdbc:h2:mem:asset_db
  driver-class-name: org.h2.Driver
jpa:
  hibernate:
    ddl-auto: create-drop
```

### After (MySQL)
```yaml
datasource:
  url: jdbc:mysql://localhost:3306/asset_db
  username: root
  password: root
  driver-class-name: com.mysql.cj.jdbc.Driver
jpa:
  hibernate:
    ddl-auto: update  # Safe for MySQL - updates schema without dropping
```

---

## 🛠️ MySQL Setup on Your Machine

### Option 1: Install MySQL Server (Recommended)

#### Step 1: Download & Install MySQL
- **Windows:** Download from [mysql.com](https://dev.mysql.com/downloads/mysql/)
- **macOS:** `brew install mysql`
- **Linux:** `apt-get install mysql-server`

#### Step 2: Start MySQL Service
```bash
# Windows (in PowerShell as Admin)
net start MySQL80

# macOS
brew services start mysql

# Linux
sudo service mysql start
```

#### Step 3: Create Database & User
```bash
# Connect to MySQL
mysql -u root -p

# Create database
CREATE DATABASE asset_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Create user (optional)
CREATE USER 'root'@'localhost' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON asset_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

# Exit
EXIT;
```

#### Step 4: Verify Connection
```bash
mysql -u root -p -D asset_db
```

---

## ⚙️ Update Connection Details

### If You Use Different Credentials:

Edit `src/main/resources/application.yaml`:

```yaml
datasource:
  url: jdbc:mysql://localhost:3306/asset_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
  username: your_username    # Change this
  password: your_password    # Change this
  driver-class-name: com.mysql.cj.jdbc.Driver
```

### Common Connection Strings:

**Local MySQL (Default):**
```yaml
url: jdbc:mysql://localhost:3306/asset_db
username: root
password: root
```

**With Different Port:**
```yaml
url: jdbc:mysql://localhost:3307/asset_db  # Port 3307
```

**Remote MySQL:**
```yaml
url: jdbc:mysql://192.168.1.100:3306/asset_db
```

**Production (Railway):**
```yaml
url: jdbc:mysql://viaduct.proxy.rlwy.net:36228/asset_db
username: root
password: MQTRfyRKCFZdhGrMpAbKXJNebafyHFLw
```

---

## 🚀 Running the Application

### Step 1: Build
```bash
cd D:\code\asset\schedule
./gradlew clean build
```

### Step 2: Run
```bash
./gradlew bootRun
```

### Step 3: Check Logs
Look for:
```
---- Creating/Updating Database Tables ----
---- DataInsertTestRunner Starting ----
✓ Inserted STOCK category
✓ Inserted ETF category
✓ Inserted GOLD category
...
```

---

## 📊 Database Behavior

### ddl-auto: update (Current)
- ✅ Keeps existing data when restarting app
- ✅ Creates tables if they don't exist
- ✅ Updates schema if needed
- ⚠️ Doesn't delete old columns (safe)
- **Best for:** Development & Production

### Alternative: ddl-auto: validate (More Safe)
```yaml
ddl-auto: validate  # Only checks schema, doesn't create/update
```

### Alternative: ddl-auto: create-drop (For Testing)
```yaml
ddl-auto: create-drop  # Drops and recreates tables (data lost on restart)
```

---

## 🧪 Verify MySQL Connection

### Using SQL Client:

```bash
mysql -u root -p asset_db

# Check tables
SHOW TABLES;

# Check categories
SELECT * FROM asset_categories;

# Check products
SELECT * FROM asset_products;

# Check prices
SELECT * FROM asset_prices;

# Exit
EXIT;
```

### Using REST API:

```bash
# While app is running:
curl http://localhost:8080/api/data/status

# Response:
{
  "status": "success",
  "message": "✅ Dữ liệu đã được insert thành công!",
  "data": {
    "categories": 3,
    "platforms": 2,
    "products": 4,
    "prices": 4
  }
}
```

---

## ❌ Troubleshooting

### Problem: "Access denied for user 'root'@'localhost'"
**Solution:**
1. Check MySQL is running: `mysql -u root -p`
2. Update password in application.yaml
3. Reset password if needed

### Problem: "Can't connect to MySQL server"
**Solution:**
1. Start MySQL service
2. Check port (default 3306)
3. Check firewall settings

### Problem: "Database asset_db doesn't exist"
**Solution:**
```bash
mysql -u root -p
CREATE DATABASE asset_db CHARACTER SET utf8mb4;
```

### Problem: "Java Driver not found"
**Solution:**
- Ensure mysql-connector-java is in dependencies (it is in build.gradle)
- Rebuild: `./gradlew clean build`

### Problem: Data not inserting
**Solution:**
1. Check logs for `STARTING DATA INSERT TEST`
2. Ensure MySQL is running
3. Check database connectivity
4. Verify ddl-auto settings

---

## 📝 Configuration Summary

| Setting | Value | Purpose |
|---------|-------|---------|
| Database Type | MySQL | Production-like |
| Host | localhost | Local machine |
| Port | 3306 | Default MySQL |
| Database | asset_db | Database name |
| Username | root | Default user |
| Password | root | Default password |
| DDL Auto | update | Safe schema updates |
| Show SQL | true | Debug queries |
| Timezone | UTC | Consistent time |

---

## 💾 Data Persistence

**With MySQL:**
- ✅ Data persists when app restarts
- ✅ Data persists after shutdown
- ✅ Production-ready configuration
- ✅ Can access via SQL clients

**Unlike H2 In-Memory:**
- ❌ Previously: Data lost on restart
- ❌ Previously: Can only access via app API

---

## 🔗 Related Files

- `src/main/resources/application.yaml` - Updated to MySQL
- `src/main/resources/application-prod.yaml` - Production config (Railway)
- `src/test/resources/application.yaml` - Tests still use H2
- `src/test/resources/application-test.yaml` - Test profile uses H2

---

## ✅ Next Steps

1. ✅ Install MySQL (if not already installed)
2. ✅ Create database: `CREATE DATABASE asset_db;`
3. ✅ Update credentials if needed (in application.yaml)
4. ✅ Run: `./gradlew bootRun`
5. ✅ Verify: `GET http://localhost:8080/api/data/status`
6. ✅ Access DB: `mysql -u root -p asset_db`

---

**Status:** ✅ Configuration Updated to MySQL
**Date:** 2026-07-06
**Compatibility:** Development & Production Ready

