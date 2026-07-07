# Railway Deployment Guide

## Prerequisites
- Railway account (https://railway.app)
- MySQL database on Railway
- JAR file built from this project

---

## Step 1: Prepare Database on Railway

1. Create a MySQL plugin on Railway
2. Get connection details:
   - Host: `viaduct.proxy.rlwy.net`
   - Port: `36228` (or your assigned port)
   - Database: `asset_db`
   - Username: `root`
   - Password: `MQTRfyRKCFZdhGrMpAbKXJNebafyHFLw`

3. Create the database and tables:
   ```bash
   mysql -h viaduct.proxy.rlwy.net -P 36228 -u root -p asset_db < src/main/resources/init-database.sql
   ```

---

## Step 2: Set Environment Variables on Railway

In Railway dashboard, set these environment variables:

### Database
```
SPRING_DATASOURCE_URL: jdbc:mysql://viaduct.proxy.rlwy.net:36228/asset_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
SPRING_DATASOURCE_USERNAME: root
SPRING_DATASOURCE_PASSWORD: MQTRfyRKCFZdhGrMpAbKXJNebafyHFLw
```

### Spring Profile
```
SPRING_PROFILES_ACTIVE: prod
```

### Email Configuration (Optional)
```
SPRING_MAIL_USERNAME: tranvanminhnhat99@gmail.com
SPRING_MAIL_PASSWORD: xsxw nqab vesg hzfp
```

### Logging
```
LOGGING_LEVEL_ROOT: WARN
LOGGING_LEVEL_COM_VN_ASSET: INFO
```

### Application Configuration
```
APP_FRONTEND_LOGIN_URL: http://your-frontend-url/login
APP_API_FIREANT_BASE_URL: https://api.fireant.vn
APP_API_SJC_BASE_URL: https://sjc.com.vn
ASSET_FIREANT_STOCK_CODES: TCB,FPT,VCB,BAA,BID,VNI,VIC
ASSET_FIREANT_ETF_CODES: E1VFVN30,FUEVFVND,VFIBL,VFVNDPX
```

---

## Step 3: Deploy on Railway

### Option A: GitHub Integration (Recommended)
1. Push code to GitHub
2. Connect Railway to your GitHub repo
3. Railway auto-deploys on every push
4. Environment variables already set in Railway dashboard

### Option B: Local Deployment
```bash
# Build the JAR
./gradlew clean build

# Login to Railway
railway login

# Create project on Railway
railway init

# Set environment variables
railway env SPRING_PROFILES_ACTIVE prod
railway env SPRING_DATASOURCE_URL "jdbc:mysql://..."
# ... set all other variables

# Deploy
railway up

# View logs
railway logs
```

### Option C: Docker Deployment
```bash
# Create Dockerfile
cat > Dockerfile << 'EOF'
FROM eclipse-temurin:21-jre-alpine
COPY build/libs/asset-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

# Build and deploy
docker build -t asset-scheduler .
railway up
```

---

## Step 4: Verify Deployment

### Check Health
```bash
# View logs on Railway
railway logs

# Should see:
# 2026-07-06 ... INFO DataInitializer : Initializing master data...
# 2026-07-06 ... INFO AssetDataScheduler : Starting daily asset data sync...
```

### Test Database Connection
```bash
# Connect to Railway MySQL
mysql -h viaduct.proxy.rlwy.net -P 36228 -u root -p asset_db

# Query tables
SELECT COUNT(*) FROM asset_categories;
SELECT COUNT(*) FROM asset_prices;
```

### Test API Access
```bash
# Check FireAnt API accessible
curl https://api.fireant.vn/quote/TCB | jq .

# Check SJC website accessible
curl -A "Mozilla/5.0" https://sjc.com.vn | grep -i "sjc\|gold"
```

---

## Step 5: Monitor Running Services

### First Sync (Should happen at 08:00 or first schedule run)
```
2026-07-06 08:00:00 INFO AssetDataScheduler : Starting daily asset data sync...
2026-07-06 08:00:01 INFO FireantApiService : Fetching 15 stocks from FireAnt API
2026-07-06 08:00:02 INFO FireantApiService : Successfully fetched 15 stocks
2026-07-06 08:00:03 INFO FireantApiService : Fetching 7 ETFs from FireAnt API
2026-07-06 08:00:04 INFO FireantApiService : Successfully fetched 7 ETFs
2026-07-06 08:00:05 INFO GoldCrawlerService : Crawling gold prices from: https://sjc.com.vn
2026-07-06 08:00:10 INFO GoldCrawlerService : Successfully crawled 4 gold prices
2026-07-06 08:00:11 INFO AssetDataSyncService : Synced 26 items to database
```

### View Real-time Logs
```bash
railway logs -f    # -f for follow/real-time
```

### Monitor Database Growth
```bash
mysql -h viaduct.proxy.rlwy.net -P 36228 -u root -p asset_db << EOF
SELECT 'asset_prices' as table_name, COUNT(*) as count FROM asset_prices
UNION ALL
SELECT 'asset_products' as table_name, COUNT(*) as count FROM asset_products
UNION ALL
SELECT 'asset_categories' as table_name, COUNT(*) as count FROM asset_categories;
EOF
```

---

## Common Railway-Specific Issues

### Issue: Cannot connect to database
```
Error: Communications link failure
```
**Solution**: 
- Verify Railway MySQL service is running
- Check SPRING_DATASOURCE_URL format is correct
- Ensure IP allowlist includes Railway app

### Issue: Environment variables not loading
```
Error: Cannot resolve placeholder 'app.api.fireant.base-url'
```
**Solution**:
- Use underscore naming: `APP_API_FIREANT_BASE_URL`
- Make sure Railway env variables are set after creating service
- Redeploy after changing variables: `railway up`

### Issue: Scheduled tasks not running at expected time
**Solution**:
- Check timezone is correct: `Asia/Ho_Chi_Minh`
- Verify Railway instance time synchronization
- Check cron expression syntax with: https://crontab.guru

### Issue: Out of memory
```
java.lang.OutOfMemoryError: Java heap space
```
**Solution**:
- Railway provides 512MB by default
- Add max heap flag in railway.json:
  ```json
  {
    "runtime": {
      "args": ["-Xmx256m"]
    }
  }
  ```

---

## Security Best Practices

### 1. Never Commit Secrets
```bash
# .gitignore
*.properties
*.yaml
.env
secrets/

# Use environment variables instead
export SPRING_DATASOURCE_PASSWORD=your_password
```

### 2. Rotate Credentials Regularly
- Change database password every 3 months
- Use different passwords for dev/staging/prod

### 3. Restrict Database Access
- Only allow connections from Railway IP
- Use VPC/Private networking if available

### 4. Enable Database Backups
- Configure automatic backups on Railway
- Test restore procedure periodically

### 5. Monitor API Rate Limiting
- FireAnt may have rate limits
- Add exponential backoff for retries
- Log failed requests for investigation

---

## Scaling Considerations

### If traffic increases:
1. Increase task scheduling pool size:
   ```yaml
   spring.task.scheduling.pool.size: 20
   ```

2. Add caching layer:
   ```java
   @Cacheable("prices")
   public List<AssetPrice> getCurrentPrices() { ... }
   ```

3. Optimize database queries:
   - Add indexes on frequently queried columns
   - Use pagination for large result sets

4. Upgrade Railway instance:
   - Railway allows easy scaling
   - Monitor CPU/memory usage in dashboard

---

## Troubleshooting Commands

```bash
# Check if app is running
railway logs | tail -20

# View all environment variables
railway env

# Update environment variable
railway env DATABASE_URL "new_value"

# Redeploy with new settings
railway up

# Connect to app shell
railway shell

# View app metrics
railway metrics

# Check connected services
railway list services
```

---

## Costs on Railway

- **Pricing**: Pay-as-you-go, ~$5/month for MySQL + app
- **Included**: 5GB bandwidth/month free
- **Database**: $5/month for MySQL plugin
- **Storage**: $1/GB/month

---

## Support Links

- Railway Documentation: https://docs.railway.app
- MySQL Plugin Guide: https://docs.railway.app/guides/mysql
- Environment Variables: https://docs.railway.app/reference/variables
- Troubleshooting: https://docs.railway.app/troubleshooting

---

## Next Steps

After successful deployment:
1. Set up monitoring alerts (CloudWatch, New Relic, etc)
2. Configure log aggregation (Papertrail, Datadog)
3. Set up database monitoring
4. Create runbooks for common issues
5. Document your deployment process

