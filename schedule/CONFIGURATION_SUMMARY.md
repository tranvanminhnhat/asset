# Configuration Summary

## 📋 What's Been Configured

### 1. **Application Configuration Files**
- ✅ `application.yaml` - Development configuration (default)
- ✅ `application-prod.yaml` - Production configuration for Railway

### 2. **API Endpoints** (Fully Configurable)
- ✅ **FireAnt API** - Stock & ETF quotes
  - Base URL: `https://api.fireant.vn`
  - Endpoint: `GET /quote/{symbol}`
  - Examples: TCB, FPT, VCB, BAA, BID, VNI, VIC

- ✅ **SJC Website** - Gold prices scraping
  - Website: `https://sjc.com.vn`
  - Timeout: 15 seconds
  - User-Agent: Modern Chrome browser
  - Products: SJC_1D, SJC_999, SJC_916, SJC_810

### 3. **Database Configuration**
- ✅ **Development**: Local MySQL (`localhost:3306`)
- ✅ **Production**: Railway MySQL (`viaduct.proxy.rlwy.net:36228`)
- ✅ Both configurations in YAML files
- ✅ UTF-8 character encoding enabled
- ✅ Timezone set to UTC

### 4. **Email Configuration** (Gmail SMTP)
- ✅ SMTP host: `smtp.gmail.com`
- ✅ Port: 587 with TLS/STARTTLS
- ✅ Ready for sending notifications
- ✅ Configuration templates provided

### 5. **Scheduled Tasks**
- ✅ Daily sync at 08:00 (stocks + ETFs)
- ✅ Intraday sync every 15 minutes (08:00-15:00, weekdays)
- ✅ Gold price sync every hour
- ✅ Timezone: Asia/Ho_Chi_Minh
- ✅ All cron expressions configurable

### 6. **Logging**
- ✅ Development: DEBUG level for `com.vn.asset`
- ✅ Production: INFO/WARN level with file output
- ✅ Log files: `logs/asset-scheduler.log` (prod)
- ✅ Log rotation: 10MB per file, 30-day retention

### 7. **Database Initialization**
- ✅ JPA auto-schema creation enabled (`ddl-auto: update`)
- ✅ Master data auto-initialization on startup
- ✅ SQL initialization script: `init-database.sql`

---

## 🎯 Configuration Locations

### Main YAML Files
```
src/main/resources/
├── application.yaml        # Development (default)
├── application-prod.yaml   # Production
└── init-database.sql       # Database schema
```

### Service Classes (Updated to Use Configuration)
```
src/main/java/com/vn/asset/service/
├── FireantApiService.java      # Reads: app.api.fireant.*, asset.fireant.*
└── GoldCrawlerService.java     # Reads: app.api.sjc.*
```

### Scheduler
```
src/main/java/com/vn/asset/scheduler/
└── AssetDataScheduler.java     # Uses cron from ServiceClasses or hardcoded
```

---

## 🔄 Configuration Properties Reference

### Database
```yaml
# Development (Default)
spring.datasource.url: jdbc:mysql://localhost:3306/asset_db

# Production (Railway)
spring.datasource.url: jdbc:mysql://viaduct.proxy.rlwy.net:36228/asset_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username: root
spring.datasource.password: MQTRfyRKCFZdhGrMpAbKXJNebafyHFLw
```

### FireAnt API
```yaml
app.api.fireant.base-url: https://api.fireant.vn
app.api.fireant.quote-endpoint: /quote/{symbol}
app.api.fireant.timeout-ms: 10000
asset.fireant.stock-codes: TCB,FPT,VCB,BAA,BID,VNI,VIC,HDB,ACB,MBB,CTG,VIB,STB,CEO,BCS
asset.fireant.etf-codes: E1VFVN30,FUEVFVND,VFIBL,VFVNDPX,VFEVFVND,VVNF,VESAF
```

### SJC Gold
```yaml
app.api.sjc.base-url: https://sjc.com.vn
app.api.sjc.price-url: https://sjc.com.vn/GoldPrice/Services/PriceService.ashx
app.api.sjc.timeout-ms: 15000
app.api.sjc.user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36
```

### Email (Gmail)
```yaml
spring.mail.host: smtp.gmail.com
spring.mail.port: 587
spring.mail.username: tranvanminhnhat99@gmail.com
spring.mail.password: xsxw nqab vesg hzfp
```

### Scheduling
```yaml
asset.schedule.daily-sync-cron: "0 0 8 * * *"
asset.schedule.intraday-sync-cron: "0 */15 8-15 * * MON-FRI"
asset.schedule.gold-sync-cron: "0 0 * * * *"
asset.schedule.timezone: Asia/Ho_Chi_Minh
```

---

## 🚀 How to Use

### Development (Local)
```bash
# Default uses application.yaml
./gradlew bootRun

# Logs will show:
# 2026-07-06 08:00:00 INFO DataInitializer : Initializing master data...
# 2026-07-06 08:00:01 INFO FireantApiService : Fetching 15 stocks
# 2026-07-06 08:00:05 INFO GoldCrawlerService : Crawling gold prices
```

### Production (Railway)
```bash
# Build JAR
./gradlew clean build

# Deploy with prod profile
railway up --spring.profiles.active=prod

# Or set env var on Railway and deploy
railway env SPRING_PROFILES_ACTIVE prod
railway up
```

### Customize Configuration
Edit `application.yaml` to change:
- Stock symbols: `asset.fireant.stock-codes`
- ETF symbols: `asset.fireant.etf-codes`
- Schedule times: `asset.schedule.*-cron`
- Database connection: `spring.datasource.*`
- Email settings: `spring.mail.*`

---

## ✅ Verification Checklist

- [x] All API endpoints configured in YAML
- [x] Database configurations for both dev and prod
- [x] Email configuration (Gmail SMTP)
- [x] Schedule configurations (cron expressions)
- [x] FireantApiService reads API config from @Value
- [x] GoldCrawlerService reads SJC config from @Value
- [x] production profile (application-prod.yaml) created
- [x] Project compiles successfully
- [x] Tests pass
- [x] Documentation complete

---

## 📚 Documentation Files Created

1. **CONFIGURATION_GUIDE.md**
   - Detailed explanation of all configuration options
   - Examples for different scenarios
   - Troubleshooting guide
   - Environment variables alternative

2. **RAILWAY_DEPLOYMENT.md**
   - Step-by-step Railway deployment instructions
   - Environment variable setup
   - Monitoring and verification
   - Common issues and solutions
   - Security best practices

3. **API_INTEGRATION_GUIDE.md**
   - API endpoint documentation
   - Testing methods
   - Response format examples
   - Troubleshooting individual APIs

4. **IMPLEMENTATION_SUMMARY.md**
   - Overview of what was implemented
   - Build status verification
   - Quick start instructions

5. **README.md** (Updated)
   - Project overview
   - Setup instructions
   - Build and run commands
   - API integration overview

---

## 🔧 Quick Configuration Examples

### Example 1: Add More Stock Symbols
```yaml
# In application.yaml
asset:
  fireant:
    stock-codes: TCB,FPT,VCB,BAA,BID,VNI,VIC,HDB,ACB,MBB,CTG,VIB,STB,CEO,BCS,VPB,MSN,ASM
```

### Example 2: Change Sync Schedule (More Frequent)
```yaml
# In application.yaml
asset:
  schedule:
    daily-sync-cron: "0 0 8,13 * * *"       # 8 AM and 1 PM
    intraday-sync-cron: "0 */5 8-17 * * MON-FRI"  # Every 5 min
    gold-sync-cron: "0 */30 * * * *"        # Every 30 min
```

### Example 3: Use Different Email
```yaml
# In application.yaml
spring:
  mail:
    username: your-email@gmail.com
    password: your-app-password  # 16-char app password, not regular password
```

### Example 4: Connect to Different Database
```yaml
# In application.yaml
spring:
  datasource:
    url: jdbc:mysql://your-host:3306/your_database?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
    username: your_user
    password: your_password
```

---

## 🔐 Security Notes

1. **Never commit sensitive data to Git**
   - Keep passwords in environment variables
   - Use application-prod.yaml locally never commit it
   - Or use .gitignore for sensitive files

2. **Email Credentials**
   - Use App Password (16 chars), NOT regular Gmail password
   - Generate at: https://myaccount.google.com/apppasswords

3. **Database Credentials**
   - Change Railway password if exposed
   - Use different passwords for dev/prod
   - Enable backups for production

4. **API Keys (Future)**
   - If adding API keys, store in environment variables only
   - Never commit keys to repository

---

## 🎯 Next Steps

1. **Database Setup**
   ```sql
   CREATE DATABASE asset_db CHARACTER SET utf8mb4;
   SOURCE src/main/resources/init-database.sql;
   ```

2. **Test Locally**
   ```bash
   ./gradlew bootRun
   # Check logs for successful sync messages
   ```

3. **Deploy to Production**
   - See RAILWAY_DEPLOYMENT.md for detailed steps
   - Set environment variables on Railway
   - Deploy JAR or push to GitHub for auto-deploy

4. **Monitor**
   - Set up log monitoring
   - Configure alerts for sync failures
   - Regular database backups

---

## 📞 Support Resources

- **Configuration Questions**: See CONFIGURATION_GUIDE.md
- **Railway Deployment**: See RAILWAY_DEPLOYMENT.md
- **API Issues**: See API_INTEGRATION_GUIDE.md
- **Code Questions**: Check service classes for @Value annotations
- **Database Schema**: See init-database.sql

---

**Status**: ✅ All configurations ready for development and production deployment
**Last Updated**: July 6, 2026
**Build Status**: ✅ BUILD SUCCESSFUL

