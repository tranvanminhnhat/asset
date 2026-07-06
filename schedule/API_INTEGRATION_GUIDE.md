# Asset Scheduler - API Integration Guide

## Overview
This document describes the APIs and data sources used by the Asset Scheduler to fetch stock, ETF, and gold price data.

## 1. FireAnt API (Stocks & ETFs)

### API Endpoint
- **Base URL**: `https://api.fireant.vn`
- **Endpoint**: `GET /quote/{symbol}`
- **Example**: `https://api.fireant.vn/quote/TCB`

### Response Format
The API returns a JSON object with the following fields:
```json
{
  "code": "TCB",
  "name": "Techcombank",
  "price": 27000,
  "change": 100,
  "percentChange": 0.37,
  "volume": 5000000,
  "pe": 15.5,
  "eps": 1734,
  "exchange": "HOSE",
  "lastPrice": 27000,
  "closePrice": 26900,
  ...
}
```

### Key Fields
- `code`: Stock/ETF symbol
- `name`: Company or fund name
- `price`: Current/latest price
- `change`: Price change in absolute value
- `percentChange`: Price change in percentage
- `volume`: Trading volume
- `exchange`: Trading exchange (HOSE, HNX)

### Configuration
Edit `src/main/resources/application.yaml`:
```yaml
asset:
  fireant:
    stock-codes: TCB,FPT,VCB,BAA,BID,VNI,VIC
    etf-codes: E1VFVN30,FUEVFVND,VFIBL,VFVNDPX
```

### Implementation Notes
- The service iterates through configured codes and calls the API for each
- Response mapping is flexible to handle different field name variations
- Errors on individual symbols don't stop the entire sync process
- Logging at DEBUG level shows individual symbol processing
- Stock and ETF data use the same endpoint; differentiation is based on configuration

### Testing the API
```bash
# Test with curl
curl -s "https://api.fireant.vn/quote/TCB" | jq .

# Test with PowerShell
Invoke-WebRequest -Uri "https://api.fireant.vn/quote/TCB" -UseBasicParsing | ConvertFrom-Json
```

---

## 2. SJC Gold Prices (Web Scraping)

### Data Source
- **Website**: `https://sjc.com.vn`
- **Method**: HTML scraping using Jsoup
- **Update Frequency**: Real-time (on demand)

### Gold Products Available
The SJC website typically displays prices for:
- **SJC_1D** (1 chỉ): Bar gold, 1 dong (3.75g)
- **SJC_999**: 24-karat gold
- **SJC_916**: 22-karat gold  
- **SJC_810**: 18-karat gold

### Price Table Structure
The website contains a price table with rows for each gold type:
```
| Type/Code | Product Name | Buy Price | Sell Price |
|-----------|--------------|-----------|------------|
| SJC 1 Chỉ | Bar Gold    | 67,500,000| 67,600,000|
| SJC 999   | 24K Gold    | 67,200,000| 67,300,000|
| ...       | ...         | ...       | ...       |
```

### Implementation Details

#### Web Scraping Approach
1. **User-Agent & Headers**: Mimics Chrome browser to bypass basic detection
2. **Timeout**: 15 seconds to handle slow connections
3. **Fallbacks**: Tries multiple CSS selectors to adapt to website changes
4. **Error Handling**: Non-fatal errors on specific rows don't stop overall scraping

#### Price Parsing
- Handles Vietnamese number formats (`,` and `.` as separators)
- Extracts numeric values from cells with special characters
- Normalizes gold codes from various formats (e.g., "SJC 1 Chỉ" → "SJC_1D")

#### Known Limitations
1. **403 Forbidden**: SJC website may occasionally block requests
   - Solution: Configure rotating User-Agents or proxies if needed
   - Alternative: Use VietcomBank gold API if available

2. **Website Structure Changes**: If SJC restructures their HTML, CSS selectors may need updating
   - Current selectors: `table tbody tr`, fallback to `table tr`
   - Test by visiting the website and inspecting the price table

3. **Rate Limiting**: SJC may rate-limit aggressive scraping
   - Current approach: One request per scheduled sync (hourly)
   - If needed, add delays between requests

### Configuration
Currently hardcoded in `GoldCrawlerService`:
```java
private static final String SJC_GOLD_URL = "https://sjc.com.vn";
private static final int CONNECTION_TIMEOUT = 15000;
```

### Scheduler Configuration (application.yaml)
The gold sync runs hourly:
```yaml
# In AssetDataScheduler.java
@Scheduled(cron = "0 0 * * * *")  // Every hour
public void syncGoldPrices() { ... }
```

### Testing the Scraper
```bash
# Test with curl and redirect User-Agent
curl -A "Mozilla/5.0 (Windows NT 10.0; Win64; x64)" https://sjc.com.vn \
  | grep -i "sjc\|gold\|price"

# Test with PowerShell
$headers = @{"User-Agent"="Mozilla/5.0 (Windows NT 10.0; Win64; x64)"}
Invoke-WebRequest -Uri "https://sjc.com.vn" -UseBasicParsing -Headers $headers
```

---

## 3. Sync Schedule

### Daily Sync (08:00 AM)
```
AssetDataScheduler.syncDailyData()
├── fireantApiService.getStockQuotes()    // Fetch all stocks
├── fireantApiService.getEtfQuotes()      // Fetch all ETFs
└── Save to asset_products & asset_prices
```

### Intraday Sync (Every 15 minutes, 08:00-15:00, Weekdays)
```
AssetDataScheduler.syncIntraday()
├── fireantApiService.getStockQuotes()
└── Save to asset_products & asset_prices (update only)
```

### Gold Price Sync (Every hour)
```
AssetDataScheduler.syncGoldPrices()
├── goldCrawlerService.getGoldPrices()
└── Save to asset_products & asset_prices
```

---

## 4. Database Schema

### asset_categories
Stores asset type classifications:
- STOCK: Individual stocks
- ETF: Exchange-traded funds
- GOLD: Gold products

### asset_platforms
Stores trading/selling platform info:
- VNDIRECT: Vietnamese direct trading
- GOLD_MARKET: SJC gold market

### asset_products
Individual products (stocks, ETFs, gold):
- code: Unique identifier (TCB, E1VFVN30, SJC_1D, etc)
- category_id: Type of asset
- platform_id: Where it's traded
- name: Display name

### asset_prices
Historical price records:
- product_id: Reference to asset_products
- current_price: The price value
- price_date: When the price was recorded
- is_latest: Mark the most recent price
- asset_type: STOCK, ETF, or GOLD

---

## 5. Troubleshooting

### FireAnt API Issues
**Problem**: 404 errors or "No data found"
- Verify the symbol code is correct (e.g., TCB, not tcb)
- Check if the stock trades on HOSE or HNX exchange
- Try the endpoint directly in a browser/curl

**Problem**: Rate limiting
- Current implementation has 1-2 second delays between requests
- Reduce `stockCodes` and `etfCodes` lists if needed

### SJC Gold Scraping Issues
**Problem**: 403 Forbidden
- Website is blocking the user-agent
- Try updating the User-Agent string to match latest Chrome version
- Consider implementing a proxy rotation solution

**Problem**: No prices found in response
- Website HTML structure may have changed
- Inspect the current SJC website and update CSS selectors in `GoldCrawlerService`
- Look for table with prices and update `doc.select()` patterns

**Problem**: Incorrect prices parsed
- Check if Vietnamese number format parsing is working correctly
- Test with `parsePriceString()` method directly
- Add logs to debug which cells are being parsed

### General Issues
**Empty results for stocks/ETFs**: 
- Check if the stock codes are valid
- Verify the API endpoint is accessible (not blocked by network)
- Check logs for specific error messages

**DB insertion errors**:
- Verify database connection in `application.yaml`
- Check if tables exist with correct schema
- Review transaction logs for constraint violations

---

## 6. Future Enhancements

1. **Alternative Gold Data Source**: VietcomBank API
   - More reliable than web scraping
   - Might require API key registration

2. **Caching Layer**: Store recent prices in-memory
   - Reduces API calls during testing
   - Improves response time for price queries

3. **WebSocket Updates**: Real-time price streaming
   - Some exchanges offer WebSocket APIs
   - Would replace periodic polling

4. **Multi-Exchange Support**: Add more platforms
   - SSI, VIETCAP (brokerage platforms)
   - Upcom (alternative exchange)

5. **Price Alerts**: Notify on significant price changes
   - Requires threshold configuration
   - Email/SMS integration

---

## 7. Configuration Reference

### application.yaml
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/asset_db
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update

asset:
  fireant:
    stock-codes: TCB,FPT,VCB,BAA,BID,VNI,VIC
    etf-codes: E1VFVN30,FUEVFVND,VFIBL,VFVNDPX

logging:
  level:
    root: INFO
    com.vn.asset: DEBUG
```

### Cron Expression Format
Used in `@Scheduled` annotations:
```
┌───────────── second (0-59)
│ ┌───────────── minute (0-59)
│ │ ┌───────────── hour (0-23)
│ │ │ ┌───────────── day of month (1-31)
│ │ │ │ ┌───────────── month (1-12)
│ │ │ │ │ ┌───────────── day of week (0-6) (0 = Sunday)
│ │ │ │ │ │
; * * * * * *
```

Examples:
- `0 0 8 * * *` = Every day at 08:00
- `0 */15 8-15 * * MON-FRI` = Every 15 minutes between 08:00 and 15:00, weekdays only
- `0 0 * * * *` = Every hour at minute 0

---

## Contact & Support

For API documentation:
- FireAnt: https://api.fireant.vn/docs
- SJC: Visit https://sjc.com.vn and contact their support

For code issues, check logs in your Spring Boot console with:
```
logging:
  level:
    com.vn.asset: DEBUG
```

