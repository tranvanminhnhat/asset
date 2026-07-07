package com.vn.asset.service;

import com.vn.asset.dto.EtfQuoteDto;
import com.vn.asset.dto.StockQuoteDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FireantApiService {

    private final RestTemplate restTemplate;

    @Value("${app.api.fireant.base-url:https://api.fireant.vn}")
    private String baseUrl;

    @Value("${app.api.fireant.auth.anonymous-login-endpoint:/authentication/anonymous-login}")
    private String anonymousLoginEndpoint;

    @Value("${app.api.fireant.symbol-endpoint:/symbols/{symbol}}")
    private String symbolEndpoint;

    @Value("${app.api.fireant.historical-quotes-endpoint:/symbols/{symbol}/historical-quotes}")
    private String historicalQuotesEndpoint;

    @Value("${app.api.fireant.quote-lookback-days:7}")
    private int quoteLookbackDays;

    private final List<String> stockCodes;
    private final List<String> etfCodes;

    private volatile String accessToken;

    public FireantApiService(
            RestTemplate restTemplate,
            @Value("${asset.fireant.stock-codes:TCB,FPT,VCB,BAA,BID,VNI,VIC}") List<String> stockCodes,
            @Value("${asset.fireant.etf-codes:E1VFVN30,FUEVFVND,VFIBL,VFVNDPX}") List<String> etfCodes
    ) {
        this.restTemplate = restTemplate;
        this.stockCodes = stockCodes;
        this.etfCodes = etfCodes;
    }

    /**
     * Fetch all stock quotes from FireAnt API.
     * APIs used:
     * - POST /authentication/anonymous-login
     * - GET /symbols/{symbol}
     * - GET /symbols/{symbol}/historical-quotes
     */
    public List<StockQuoteDto> getStockQuotes() {
        List<StockQuoteDto> stocks = new ArrayList<>();
        log.info("Fetching {} stocks from FireAnt API", stockCodes.size());

        for (String code : stockCodes) {
            try {
                StockQuoteDto quote = getStockQuote(code.trim());
                if (quote != null && quote.getPrice() != null) {
                    stocks.add(quote);
                    log.debug("Successfully fetched stock: {}", code);
                }
            } catch (Exception e) {
                log.warn("Failed to fetch stock {}", code, e);
            }
        }

        log.info("Successfully fetched {} stocks from FireAnt API", stocks.size());
        return stocks;
    }

    /**
     * Fetch all ETF quotes from FireAnt API.
     */
    public List<EtfQuoteDto> getEtfQuotes() {
        List<EtfQuoteDto> etfs = new ArrayList<>();
        log.info("Fetching {} ETFs from FireAnt API", etfCodes.size());

        for (String code : etfCodes) {
            try {
                EtfQuoteDto quote = getEtfQuote(code.trim());
                if (quote != null && quote.getPrice() != null) {
                    etfs.add(quote);
                    log.debug("Successfully fetched ETF: {}", code);
                }
            } catch (Exception e) {
                log.warn("Failed to fetch ETF {}", code, e);
            }
        }

        log.info("Successfully fetched {} ETFs from FireAnt API", etfs.size());
        return etfs;
    }

    public StockQuoteDto getStockQuote(String code) {
        try {
            Map<String, Object> symbolInfo = getSymbolInfo(code);
            Map<String, Object> latestQuote = getLatestQuote(code);

            if (symbolInfo == null || latestQuote == null) {
                log.warn("Missing symbol or quote data for stock: {}", code);
                return null;
            }

            return toStockDto(code, symbolInfo, latestQuote);
        } catch (RestClientException e) {
            log.warn("API error fetching stock {} from FireAnt", code, e);
            return null;
        } catch (Exception e) {
            log.error("Unexpected error fetching stock {}", code, e);
            return null;
        }
    }

    private EtfQuoteDto getEtfQuote(String code) {
        try {
            Map<String, Object> symbolInfo = getSymbolInfo(code);
            Map<String, Object> latestQuote = getLatestQuote(code);

            if (symbolInfo == null || latestQuote == null) {
                log.warn("Missing symbol or quote data for ETF: {}", code);
                return null;
            }

            return toEtfDto(code, symbolInfo, latestQuote);
        } catch (RestClientException e) {
            log.warn("API error fetching ETF {} from FireAnt", code, e);
            return null;
        } catch (Exception e) {
            log.error("Unexpected error fetching ETF {}", code, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getSymbolInfo(String symbol) {
        String url = baseUrl + symbolEndpoint.replace("{symbol}", symbol);
        ResponseEntity<Map> response = exchangeGetWithAuthRetry(url, Map.class);
        return response.getBody();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getLatestQuote(String symbol) {
        String url = buildHistoricalUrl(symbol);
        ResponseEntity<List> response = exchangeGetWithAuthRetry(url, List.class);
        List<?> rows = response.getBody();

        if (rows == null || rows.isEmpty()) {
            return null;
        }

        return rows.stream()
                .filter(Map.class::isInstance)
                .map(item -> (Map<String, Object>) item)
                .max(Comparator.comparing(this::readDateTimeSafe, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
    }

    private String buildHistoricalUrl(String symbol) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(Math.max(quoteLookbackDays, 1));

        String endpoint = historicalQuotesEndpoint.replace("{symbol}", symbol);
        String start = startDate + "T00:00:00";
        String end = endDate + "T23:59:59";

        return UriComponentsBuilder
                .fromUriString(baseUrl + endpoint)
                .queryParam("startDate", start)
                .queryParam("endDate", end)
                .queryParam("offset", 0)
                .queryParam("limit", 30)
                .build(true)
                .toUriString();
    }

    private synchronized void refreshAccessToken() {
        String url = baseUrl + anonymousLoginEndpoint;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                Map.class
        );

        Map<?, ?> body = response.getBody();
        Object token = body != null ? body.get("accessToken") : null;

        if (token == null || String.valueOf(token).isBlank()) {
            throw new RestClientException("FireAnt anonymous-login did not return accessToken");
        }

        this.accessToken = String.valueOf(token);
        log.info("Refreshed FireAnt anonymous access token");
    }

    private HttpHeaders authHeaders() {
        if (accessToken == null || accessToken.isBlank()) {
            refreshAccessToken();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }

    private <T> ResponseEntity<T> exchangeGetWithAuthRetry(String url, Class<T> responseType) {
        try {
            return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authHeaders()), responseType);
        } catch (HttpClientErrorException.Unauthorized unauthorized) {
            log.warn("FireAnt token expired/invalid, refreshing and retrying: {}", url);
            refreshAccessToken();
            return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authHeaders()), responseType);
        }
    }

    private StockQuoteDto toStockDto(String code, Map<String, Object> symbolInfo, Map<String, Object> quoteInfo) {
        try {
            StockQuoteDto dto = new StockQuoteDto();
            dto.setCode(code);
            dto.setCompanyName(readString(symbolInfo, "name", "symbol"));
            dto.setExchange(readString(symbolInfo, "exchange", "HNX"));

            BigDecimal close = readDecimal(quoteInfo, "priceClose", "closePrice", "close", "price");
            BigDecimal basic = readDecimal(quoteInfo, "priceBasic", "referencePrice", "basePrice");

            dto.setPrice(close);
            dto.setPriceChange(calcChange(close, basic));
            dto.setPercentChange(calcPercentChange(close, basic));
            dto.setVolume(readLong(quoteInfo, "totalVolume", "dealVolume", "volume", "tradingVolume"));

            return dto;
        } catch (Exception e) {
            log.error("Error mapping stock data for code: {}", code, e);
            return null;
        }
    }

    private EtfQuoteDto toEtfDto(String code, Map<String, Object> symbolInfo, Map<String, Object> quoteInfo) {
        try {
            EtfQuoteDto dto = new EtfQuoteDto();
            dto.setCode(code);
            dto.setName(readString(symbolInfo, "name", "symbol", code));
            dto.setExchange(readString(symbolInfo, "exchange", "HNX"));

            BigDecimal close = readDecimal(quoteInfo, "priceClose", "closePrice", "close", "price");
            BigDecimal basic = readDecimal(quoteInfo, "priceBasic", "referencePrice", "basePrice");

            dto.setPrice(close);
            dto.setNav(null);
            dto.setPriceChange(calcChange(close, basic));
            dto.setPercentChange(calcPercentChange(close, basic));
            dto.setVolume(readLong(quoteInfo, "totalVolume", "dealVolume", "volume", "tradingVolume"));

            return dto;
        } catch (Exception e) {
            log.error("Error mapping ETF data for code: {}", code, e);
            return null;
        }
    }

    private BigDecimal calcChange(BigDecimal close, BigDecimal basic) {
        if (close == null || basic == null) {
            return null;
        }
        return close.subtract(basic);
    }

    private BigDecimal calcPercentChange(BigDecimal close, BigDecimal basic) {
        if (close == null || basic == null || BigDecimal.ZERO.compareTo(basic) == 0) {
            return null;
        }

        return close.subtract(basic)
                .multiply(BigDecimal.valueOf(100))
                .divide(basic, 4, RoundingMode.HALF_UP);
    }

    private LocalDateTime readDateTimeSafe(Map<String, Object> map) {
        Object value = map.get("date");
        if (value == null) {
            return null;
        }

        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String readString(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null && !String.valueOf(value).trim().isEmpty()) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    private BigDecimal readDecimal(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                try {
                    String strValue = String.valueOf(value).trim();
                    if (!strValue.isEmpty()) {
                        return new BigDecimal(strValue);
                    }
                } catch (NumberFormatException ignored) {
                    // Try next key
                }
            }
        }
        return null;
    }

    private Long readLong(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                try {
                    String strValue = String.valueOf(value).trim();
                    if (!strValue.isEmpty()) {
                        return Long.valueOf(strValue.replace(",", "").split("\\.")[0]);
                    }
                } catch (NumberFormatException ignored) {
                    // Try next key
                }
            }
        }
        return null;
    }
}
