# Configuration Guide

## Overview
This document explains how to configure the Asset Scheduler with correct database connections, API endpoints, and email settings.

## Quick Start

### Development Environment (Local)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/asset_db
    username: root
    password: ''
```

### Production Environment (Railway)
```yaml
spring:
  datasource:
    url: jdbc:mysql://viaduct.proxy.rlwy.net:36228/asset_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
    username: root
    password: 'MQTRfyRKCFZdhGrMpAbKXJNebafyHFLw'
```

---

## Configuration Files

### 1. `application.yaml` (Development - Default)
- **Location**: `src/main/resources/application.yaml`
- **Used when**: Running without specific Spring profile
- **Purpose**: Local development with localhost database
- **Database**: Local MySQL
- **Log Level**: DEBUG for `com.vn.asset`

### 2. `application-prod.yaml` (Production)
- **Location**: `src/main/resources/application-prod.yaml`
- **Used when**: Running with `--spring.profiles.active=prod`
- **Purpose**: Production deployment on Railway
- **Database**: Railway MySQL cluster
- **Log Level**: WARN (less verbose)
- **Logging**: File output to `logs/asset-scheduler.log`

---

## Running with Different Profiles

### Development
```bash
# Default profile (application.yaml)
./gradlew bootRun

# Or explicitly
./gradlew bootRun --args='--spring.profiles.active=default'
```

### Production
```bash
# Using production profile
./gradlew bootRun --args='--spring.profiles.active=prod'

# Or with JAR
java -jar build/libs/asset-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## API Configuration

### FireAnt API (Stock & ETF Quotes)
```yaml
app:
  api:
    fireant:
      base-url: https://api.fireant.vn          # Base URL
      quote-endpoint: /quote/{symbol}            # Endpoint path
      timeout-ms: 10000                          # Request timeout
      retry-count: 3                             # Retry attempts
```

**Configured Stock Codes** (can customize):
```yaml
asset:
  fireant:
    stock-codes: TCB,FPT,VCB,BAA,BID,VNI,VIC,HDB,ACB,MBB,CTG,VIB,STB,CEO,BCS
    etf-codes: E1VFVN30,FUEVFVND,VFIBL,VFVNDPX,VFEVFVND,VVNF,VESAF
```

**Example API Request**:
```
GET https://api.fireant.vn/quote/TCB

Response:
{
  "code": "TCB",
  "name": "Techcombank",
  "price": 27000,
  "change": 100,
  "percentChange": 0.37,
  "volume": 5000000
}
```

### SJC Gold Prices (Web Scraping)
```yaml
app:
  api:
    sjc:
      base-url: https://sjc.com.vn                                              # Website
      price-url: https://sjc.com.vn/GoldPrice/Services/PriceService.ashx       # API endpoint
      timeout-ms: 15000                                                         # Scraping timeout
      user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 # Browser UA
```

**Tracked Gold Products**:
- SJC_1D: Bar gold (1 chỉ = 3.75g)
- SJC_999: 24-karat gold
- SJC_916: 22-karat gold
- SJC_810: 18-karat gold

---

## Email Configuration

### Gmail SMTP
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: tranvanminhnhat99@gmail.com    # Your Gmail
    password: 'xsxw nqab vesg hzfp'         # Your App Password (NOT regular password)
    protocol: smtp
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### How to Get Gmail App Password
1. Go to https://myaccount.google.com/
2. Enable 2-Factor Authentication
3. Go to Security → App passwords
4. Generate an App Password for "Mail" and "Windows Computer"
5. Copy the 16-character password (without spaces)
6. Paste in YAML (without spaces as shown)

---

## Schedule Configuration

### Cron Expressions
```yaml
asset:
  schedule:
    # Daily sync at 08:00 AM
    daily-sync-cron: "0 0 8 * * *"
    
    # Every 15 minutes during trading hours (8:00-15:00, Monday-Friday)
    intraday-sync-cron: "0 */15 8-15 * * MON-FRI"
    
    # Every hour for gold prices
    gold-sync-cron: "0 0 * * * *"
    
    # Timezone for schedule execution
    timezone: Asia/Ho_Chi_Minh
```

### Cron Format
```
┌─────────── second (0-59)
│ ┌─────────── minute (0-59)
│ │ ┌─────────── hour (0-23)
│ │ │ ┌─────────── day of month (1-31)
│ │ │ │ ┌─────────── month (1-12)
│ │ │ │ │ ┌─────────── day of week (0-6, 0=Sunday)
│ │ │ │ │ │
; * * * * * *
```

### Examples
```
0 0 8 * * *        = Every day at 08:00 AM
0 */15 8-15 * * *  = Every 15 minutes from 08:00 to 15:00
0 0 * * * *        = Every hour at minute 0
0 0 0 * * *        = Every day at 00:00 (midnight)
0 30 14 * * MON-FRI = Every weekday at 14:30
```

---

## Database Configuration

### Connection String Format
```
jdbc:mysql://[host]:[port]/[database]?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
```

### Parameters
- `useSSL=false`: Disable SSL (usually for local/internal networks)
- `allowPublicKeyRetrieval=true`: Allow RSA public key for authentication
- `serverTimezone=UTC`: Set timezone to UTC
- `characterEncoding=UTF-8`: Use UTF-8 encoding for data

### Local MySQL
```yaml
datasource:
  url: jdbc:mysql://localhost:3306/asset_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
  username: root
  password: ''          # Empty for local development
  driver-class-name: com.mysql.cj.jdbc.Driver
```

### Railway MySQL
```yaml
datasource:
  url: jdbc:mysql://viaduct.proxy.rlwy.net:36228/asset_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
  username: root
  password: 'MQTRfyRKCFZdhGrMpAbKXJNebafyHFLw'
  driver-class-name: com.mysql.cj.jdbc.Driver
```

---

## JPA/Hibernate Configuration

### DDL Auto Options
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update    # Options: validate | update | create | create-drop | none
```

| Option | Behavior |
|--------|----------|
| `validate` | Validate schema, throw error if not match |
| `update` | Update schema if needed (recommended for production) |
| `create` | Drop and recreate schema (use carefully!) |
| `create-drop` | Create schema on startup, drop on shutdown |
| `none` | Don't touch schema, manual only |

### Other Settings
```yaml
spring:
  jpa:
    show-sql: true                    # Log SQL statements
    properties:
      hibernate:
        format_sql: true              # Pretty-print SQL
        dialect: org.hibernate.dialect.MySQLDialect
    database-platform: org.hibernate.dialect.MySQLDialect
    open-in-view: false               # Disable OpenSessionInView (best practice)
```

---

## Frontend Configuration

```yaml
app:
  frontend:
    login-url: http://localhost:3000/login     # Login page
    home-url: http://localhost:3000/home       # Home page
```

These URLs are used for redirects or linking from the backend.

---

## Logging Configuration

### Development
```yaml
logging:
  level:
    root: INFO
    com.vn.asset: DEBUG                  # Detailed logs for our app
    org.springframework.web: INFO         # Spring MVC logs
    org.hibernate.SQL: DEBUG              # SQL queries
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE  # SQL parameters
```

### Production
```yaml
logging:
  level:
    root: WARN                           # Only warnings and errors
    com.vn.asset: INFO                   # Application logs
  file:
    name: logs/asset-scheduler.log       # Log file location
    max-size: 10MB                       # Rotate when 10MB
    max-history: 30                      # Keep 30 days of logs
```

### View Live Logs
```bash
# Development
./gradlew bootRun --args='--logging.level.com.vn.asset=DEBUG'

# Production
tail -f logs/asset-scheduler.log
```

---

## Task Scheduling Configuration

```yaml
spring:
  task:
    scheduling:
      pool:
        size: 5                          # Thread pool size for scheduled tasks
      thread-name-prefix: asset-scheduler-  # Thread name prefix for debugging
```

---

## Configuration Customization Examples

### Example 1: Change Stock Symbols
```yaml
asset:
  fireant:
    stock-codes: ACB,MBB,VIC,VNI,CTG    # Your selected stocks
    etf-codes: E1VFVN30,FUEVFVND         # Your selected ETFs
```

### Example 2: Change Sync Schedule (More Frequent)
```yaml
asset:
  schedule:
    daily-sync-cron: "0 0 8,13 * * *"     # At 08:00 and 13:00
    intraday-sync-cron: "0 */5 8-17 * * MON-FRI"  # Every 5 minutes
    gold-sync-cron: "0 */30 * * * *"       # Every 30 minutes
```

### Example 3: Use Different Email
```yaml
spring:
  mail:
    username: your-email@gmail.com
    password: 'your-app-password'  # 16-character app password
```

---

## Environment Variables (Alternative to YAML)

You can also use environment variables instead of YAML files:

```bash
# Database
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/asset_db
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=

# Email
export SPRING_MAIL_USERNAME=your-email@gmail.com
export SPRING_MAIL_PASSWORD=your-app-password

# API
export APP_API_FIREANT_BASE_URL=https://api.fireant.vn
export APP_API_SJC_BASE_URL=https://sjc.com.vn

# Asset codes
export ASSET_FIREANT_STOCK_CODES=TCB,FPT,VCB
export ASSET_FIREANT_ETF_CODES=E1VFVN30,FUEVFVND

# Run with env vars
./gradlew bootRun
```

---

## Debugging Configuration

### Check Loaded Configuration
Add to your application to print all loaded properties:
```java
@Component
public class ConfigPrinter {
    @Autowired
    private Environment env;
    
    @PostConstruct
    public void printConfig() {
        log.info("Database URL: {}", env.getProperty("spring.datasource.url"));
        log.info("FireAnt Base URL: {}", env.getProperty("app.api.fireant.base-url"));
        log.info("Stock Codes: {}", env.getProperty("asset.fireant.stock-codes"));
    }
}
```

### Enable SQL Logging
To debug database operations, set in YAML or environment:
```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

---

## Common Issues & Solutions

### Issue: "Unknown database 'asset_db'"
- **Cause**: Database doesn't exist
- **Solution**: Create the database first
  ```sql
  CREATE DATABASE asset_db CHARACTER SET utf8mb4;
  ```

### Issue: "Access denied for user 'root'@'localhost'"
- **Cause**: Wrong password in `datasource.password`
- **Solution**: Verify MySQL password matches configuration

### Issue: "Communications link failure"
- **Cause**: Can't connect to database server
- **Solution**: Verify the host and port are correct

### Issue: Gmail not sending emails
- **Cause**: Using regular Gmail password instead of App Password
- **Solution**: Use 16-character App Password (see Email Configuration section)

### Issue: API endpoints returning 404
- **Cause**: Incorrect base URL or endpoint path
- **Solution**: Verify the URL pattern in configuration matches actual API

---

## Deployment Checklist

- [ ] Database created and accessible
- [ ] Database credentials correct
- [ ] API endpoints verified and accessible
- [ ] Email credentials configured (if needed)
- [ ] Log level set appropriately (WARN for production)
- [ ] Schedule timezones correct for your region
- [ ] Stock/ETF codes updated as needed
- [ ] All sensitive data in environment variables, not committed to git
- [ ] Database backups configured
- [ ] Monitoring/alerting set up for sync failures

---

## See Also
- `README.md` - Project overview
- `API_INTEGRATION_GUIDE.md` - API details
- `IMPLEMENTATION_SUMMARY.md` - Implementation notes

