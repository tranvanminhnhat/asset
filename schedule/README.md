# Asset Data Scheduler

## Overview
This Spring Boot application automatically fetches and updates asset data (stocks, ETFs, and gold prices) from external sources and stores them in a MySQL database.

## Features
- **Scheduled Data Synchronization**: Automatically fetches data at configured intervals
- **Stock & ETF Data**: Gets quotes from Fireant API
  - **Endpoint**: `GET https://api.fireant.vn/quote/{symbol}`
  - Supports: Stocks (TCB, FPT, VCB, etc) and ETFs (E1VFVN30, FUEVFVND, etc)
- **Gold Prices**: Crawls gold prices from SJC website (https://sjc.com.vn)
  - Tracks: SJC_1D, SJC_999, SJC_916, SJC_810
- **Data Persistence**: Stores all data in MySQL with proper relationships
- **Master Data**: Manages categories (STOCK, ETF, GOLD) and platforms (VNDIRECT, GOLD_MARKET)

## Architecture

### Tables
- **asset_categories**: Asset category master data
- **asset_platforms**: Trading platform master data
- **asset_products**: Individual products (stocks, ETFs, gold)
- **asset_prices**: Historical and current prices

### Services
- **FireantApiService**: Fetches stock and ETF data from Fireant API
- **GoldCrawlerService**: Crawls gold prices from SJC website
- **AssetDataSyncService**: Orchestrates data synchronization and database updates

### Schedulers
- **Daily Sync (8:00 AM)**: Syncs all stock and ETF data
- **Intraday Sync (Every 15 minutes, 8:00-15:00 on weekdays)**: Updates stock prices during trading hours
- **Gold Sync (Every hour)**: Updates gold prices

## Setup Instructions

### 1. Database Setup
```sql
-- Create the database
CREATE DATABASE asset_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Run init-database.sql to create tables
source src/main/resources/init-database.sql;
```

### 2. Application Configuration
Update `application.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/asset_db
    username: root
    password: your_password
```

### 3. Build and Run
```bash
./gradlew clean build
./gradlew bootRun
```

## Data Flow

### Application Startup
1. Spring Boot initializes all beans
2. `DataInitializer` runs automatically
3. Master data (categories, platforms) are created if not exist

### Scheduled Synchronization

#### Daily Sync (08:00)
- Fetches all stocks from Fireant API
- Fetches all ETFs from Fireant API
- Updates asset_products and asset_prices tables

#### Intraday Sync (Every 15 min, weekdays 08:00-15:00)
- Fetches latest stock prices
- Updates asset_prices with is_latest=true

#### Gold Sync (Every hour)
- Crawls gold prices from SJC website
- Updates asset_prices for gold products

## API Integration

### Fireant API
**Endpoint**: `GET https://api.fireant.vn/quote/{symbol}`

Example:
```bash
curl https://api.fireant.vn/quote/TCB
```

**Configured Symbols** (edit `application.yaml`):
- Stocks: TCB, FPT, VCB, BAA, BID, VNI, VIC
- ETFs: E1VFVN30, FUEVFVND, VFIBL, VFVNDPX

Response includes: `code`, `name`, `price`, `change`, `percentChange`, `volume`, etc.

### SJC Gold Website
Uses Jsoup to scrape gold prices from `https://sjc.com.vn/`

**Gold Products**:
- SJC_1D: Bar gold (1 chỉ)
- SJC_999: 24-karat gold
- SJC_916: 22-karat gold
- SJC_810: 18-karat gold

**See** `API_INTEGRATION_GUIDE.md` for detailed documentation and troubleshooting.

## Important Notes

1. **API Endpoints**: Updated to correct FireAnt endpoint `GET /quote/{symbol}`
   - Flexible field mapping to handle various response formats
   - Individual symbol errors don't stop the entire sync

2. **Web Scraping**: SJC website uses proper User-Agent headers
   - May occasionally return 403 Forbidden
   - CSS selectors fallback to handle website structure changes
   - Consider using VietcomBank gold API if SJC becomes unavailable

3. **Data Cleanup**: Old prices are marked as `is_latest=false` when new prices are added, maintaining full historical data.

4. **Error Handling**: All sync operations include try-catch blocks with detailed logging. Errors don't stop the scheduler.

5. **Transaction Management**: All database operations are wrapped in transactions for data consistency.

6. **Configuration**: Stock/ETF codes are configured in `application.yaml` under `asset.fireant.stock-codes` and `asset.fireant.etf-codes`

## Logging
Logs are written to console with the following configuration:
- Root logger: INFO
- com.vn.asset: DEBUG

## Customization

### Change Schedule Intervals
Edit cron expressions in `AssetDataScheduler`:
```java
@Scheduled(cron = "0 0 8 * * *")  // Format: second minute hour day month weekday
```

### Add New Products
Products are automatically created when prices are fetched. To manually add:
```sql
INSERT INTO asset_products (code, name, category_id, platform_id, unit_name, is_active)
VALUES ('VNI', 'VN Index', 1, 1, 'điểm', 1);
```

## Troubleshooting

### Database Connection Issues
- Verify MySQL is running
- Check credentials in application.yaml
- Ensure database exists

### No Data Being Fetched
- Check application logs for Fireant API errors
- Verify API endpoints are correct
- Check network connectivity

### Gold Prices Not Updating
- Verify SJC website is accessible
- Check if website HTML structure has changed
- Update CSS selectors in GoldCrawlerService if needed

## Dependencies
- Spring Boot 4.1.0
- Spring Data JPA
- MySQL Connector
- Jsoup (HTML parsing)
- Lombok (Code generation)
- Jackson (JSON processing)

## Future Enhancements
- Add REST APIs to query prices
- Implement WebSocket for real-time updates
- Add alternative data sources (VietcomBank gold API, broker APIs)
- Implement price change notifications and alerts
- Add caching layer for frequently accessed data

## Documentation
For detailed API information, troubleshooting, and integration examples, see `API_INTEGRATION_GUIDE.md`

