package com.minhnhat.demo.common.client.service;

import com.minhnhat.demo.common.client.dto.response.SJCPriceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SCJClientImpl implements SJCClient {

    private final RestTemplate restTemplate;

    @Value("${app.api.sjc.price-url}")
    private String sjcPriceUrl;

    @Override
    public SJCPriceResponseDto getSJCPrice() {
        try {
            log.info("Calling SJC API to fetch gold prices from: {}", sjcPriceUrl);
            SJCPriceResponseDto response = restTemplate.getForObject(sjcPriceUrl, SJCPriceResponseDto.class);
            
            if (response != null && response.getSuccess()) {
                log.info("Successfully retrieved SJC price data. Latest date: {}", response.getLatestDate());
                log.debug("Number of items retrieved: {}", response.getData() != null ? response.getData().size() : 0);
                return response;
            } else {
                log.warn("SJC API returned unsuccessful response");
                return response;
            }
        } catch (RestClientException e) {
            log.error("Failed to call SJC API at {}. Error: {}", sjcPriceUrl, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch SJC gold prices: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching SJC prices: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error fetching SJC prices: " + e.getMessage(), e);
        }
    }
}
