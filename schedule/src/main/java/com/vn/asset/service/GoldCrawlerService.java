package com.vn.asset.service;

import com.vn.asset.dto.GoldPriceDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GoldCrawlerService {

    @Value("${app.api.sjc.base-url:https://sjc.com.vn}")
    private String sjcBaseUrl;
    
    @Value("${app.api.sjc.price-url:https://sjc.com.vn/GoldPrice/Services/PriceService.ashx}")
    private String sjcPriceUrl;
    
    @Value("${app.api.sjc.timeout-ms:15000}")
    private long timeoutMs;
    
    @Value("${app.api.sjc.user-agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36}")
    private String userAgent;

    private final RestTemplate restTemplate;

    public GoldCrawlerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Get gold prices from SJC website
     * Crawls the main page and extracts price data from HTML table
     * Typical response contains: SJC_1D (1 chỉ bar gold), SJC_999 (24K), SJC_916 (22K), SJC_810 (18K)
     */
    public List<GoldPriceDto> getGoldPrices() {
        List<GoldPriceDto> goldPrices = new ArrayList<>();
        try {
            log.info("Crawling gold prices from: {}", sjcBaseUrl);
            
            // Connect with proper User-Agent and headers
            Document doc = Jsoup.connect(sjcBaseUrl)
                    .userAgent(userAgent)
                    .timeout((int) timeoutMs)
                    .referrer("https://www.google.com")
                    .get();

            // Find price table - SJC typically uses table with class "tbl-price" or similar
            // Try multiple selector patterns as website structure may vary
            Elements priceRows = doc.select("table tbody tr");
            if (priceRows.isEmpty()) {
                priceRows = doc.select("table tr");
            }
            if (priceRows.isEmpty()) {
                log.warn("No price table found in SJC website");
                return goldPrices;
            }

            log.debug("Found {} rows in price table", priceRows.size());

            for (Element row : priceRows) {
                try {
                    GoldPriceDto goldPrice = parseGoldRow(row);
                    if (goldPrice != null) {
                        goldPrices.add(goldPrice);
                        log.debug("Parsed gold price: {} - Buy: {} - Sell: {}", 
                            goldPrice.getCode(), goldPrice.getBuyPrice(), goldPrice.getSellPrice());
                    }
                } catch (Exception e) {
                    log.debug("Error parsing gold price row", e);
                }
            }

            log.info("Successfully crawled {} gold prices from SJC", goldPrices.size());
        } catch (IOException e) {
            log.error("IO Error while crawling SJC gold prices", e);
        } catch (Exception e) {
            log.error("Unexpected error crawling SJC gold prices", e);
        }

        return goldPrices;
    }

    /**
     * Parse a single gold price row from HTML table
     * Expected structure:
     * - Column 0: Gold type (SJC_1D, SJC_999, etc)
     * - Column 1: Product name (1 chỉ, 24K, etc)
     * - Column 2-3: Buy/Sell prices (may vary)
     */
    private GoldPriceDto parseGoldRow(Element row) {
        try {
            Elements cells = row.select("td");
            if (cells.size() < 3) {
                return null;
            }

            // Parse gold type/code (e.g., "SJC_1D", "SJC 1 Chỉ")
            String code = cells.get(0).text().trim();
            if (code.isEmpty() || !code.contains("SJC")) {
                return null; // Skip non-SJC rows or headers
            }

            // Normalize code format
            code = normalizeGoldCode(code);

            // Parse product name
            String name = cells.size() > 1 ? cells.get(1).text().trim() : code;

            // Extract prices (usually in different formats like "1,234,567" or "1234567")
            BigDecimal buyPrice = null;
            BigDecimal sellPrice = null;

            // Try to find prices in remaining cells
            for (int i = 2; i < cells.size(); i++) {
                String cellText = cells.get(i).text().replaceAll("[^0-9.,]", "").trim();
                if (!cellText.isEmpty()) {
                    try {
                        BigDecimal price = parsePriceString(cellText);
                        if (price != null) {
                            if (buyPrice == null) {
                                buyPrice = price;
                            } else {
                                sellPrice = price;
                                break; // Got both buy and sell prices
                            }
                        }
                    } catch (Exception ignored) {
                        // Continue to next cell
                    }
                }
            }

            // Validate we have at least a buy price
            if (buyPrice == null) {
                return null;
            }

            // If only one price found, use it as both buy and sell
            if (sellPrice == null) {
                sellPrice = buyPrice;
            }

            return GoldPriceDto.builder()
                    .code(code)
                    .name(name)
                    .buyPrice(buyPrice)
                    .sellPrice(sellPrice)
                    .time(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                    .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .build();
        } catch (Exception e) {
            log.trace("Error parsing gold price row: {}", row.text(), e);
            return null;
        }
    }

    /**
     * Normalize gold code from various formats to standard format
     * Examples: "SJC 1 Chỉ" -> "SJC_1D", "SJC_999" stays "SJC_999"
     */
    private String normalizeGoldCode(String input) {
        // If already in standard format, return as is
        if (input.contains("_")) {
            return input.trim().toUpperCase();
        }
        
        // Convert "SJC 1 Chỉ" format
        if (input.contains("1") && input.contains("Chỉ")) {
            return "SJC_1D";
        }
        if (input.contains("999")) {
            return "SJC_999";
        }
        if (input.contains("916")) {
            return "SJC_916";
        }
        if (input.contains("810")) {
            return "SJC_810";
        }
        
        return input.trim().toUpperCase();
    }

    /**
     * Parse price string handling Vietnamese number format
     * Examples: "1,234,567" or "1.234.567" or "1234567"
     */
    private BigDecimal parsePriceString(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) {
            return null;
        }

        // Remove all spaces
        priceStr = priceStr.replaceAll("\\s+", "").trim();

        // Handle Vietnamese format (using . or , as thousand separator)
        // Replace the last separator with decimal point if there are 2 digits after it
        if ((priceStr.contains(".") && priceStr.lastIndexOf('.') > priceStr.indexOf('.')) ||
            (priceStr.contains(",") && priceStr.lastIndexOf(',') > priceStr.indexOf(','))) {
            // Remove all separators except last one, then replace last with nothing
            String cleaned = priceStr.replaceAll("[,.]", "");
            try {
                return new BigDecimal(cleaned);
            } catch (Exception ignored) {
                // Fall through
            }
        }

        // Remove all separators and parse
        String cleaned = priceStr.replaceAll("[,.]", "").trim();
        if (cleaned.isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse price: {}", priceStr);
            return null;
        }
    }

    /**
     * Get gold price for a specific gold type
     */
    public GoldPriceDto getGoldPrice(String goldType) {
        List<GoldPriceDto> prices = getGoldPrices();
        String normalizedType = normalizeGoldCode(goldType);
        
        return prices.stream()
                .filter(p -> p.getCode().equalsIgnoreCase(normalizedType))
                .findFirst()
                .orElse(null);
    }
}