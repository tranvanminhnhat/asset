package com.vn.asset.controller;

import com.vn.asset.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller untuk cek status data di database
 * Endpoint: GET http://localhost:8080/api/data/status
 */
@Slf4j
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataCheckController {

    private final AssetCategoryRepository categoryRepository;
    private final AssetPlatformRepository platformRepository;
    private final AssetProductRepository productRepository;
    private final AssetPriceRepository priceRepository;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDataStatus() {
        long categoriesCount = categoryRepository.count();
        long platformsCount = platformRepository.count();
        long productsCount = productRepository.count();
        long pricesCount = priceRepository.count();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("timestamp", System.currentTimeMillis());
        response.put("data", Map.of(
                "categories", categoriesCount,
                "platforms", platformsCount,
                "products", productsCount,
                "prices", pricesCount,
                "total", categoriesCount + platformsCount + productsCount + pricesCount
        ));
        response.put("message", buildMessage(categoriesCount, platformsCount, productsCount, pricesCount));

        log.info("Data Status Check - Categories: {}, Platforms: {}, Products: {}, Prices: {}",
                categoriesCount, platformsCount, productsCount, pricesCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        var categories = categoryRepository.findAll();
        return ResponseEntity.ok(Map.of(
                "count", categories.size(),
                "data", categories
        ));
    }

    @GetMapping("/platforms")
    public ResponseEntity<?> getPlatforms() {
        var platforms = platformRepository.findAll();
        return ResponseEntity.ok(Map.of(
                "count", platforms.size(),
                "data", platforms
        ));
    }

    @GetMapping("/products")
    public ResponseEntity<?> getProducts() {
        var products = productRepository.findAll();
        return ResponseEntity.ok(Map.of(
                "count", products.size(),
                "data", products
        ));
    }

    @GetMapping("/prices")
    public ResponseEntity<?> getPrices() {
        var prices = priceRepository.findAll();
        return ResponseEntity.ok(Map.of(
                "count", prices.size(),
                "data", prices
        ));
    }

    private String buildMessage(long categories, long platforms, long products, long prices) {
        if (categories == 0 && platforms == 0 && products == 0 && prices == 0) {
            return "❌ Không có dữ liệu được insert. Hãy chọi (restart) ứng dụng.";
        }
        if (categories > 0 && platforms > 0 && products > 0 && prices > 0) {
            return "✅ Dữ liệu đã được insert thành công!";
        }
        return "⚠️  Dữ liệu chưa đầy đủ. Hãy kiểm tra application logs.";
    }
}

