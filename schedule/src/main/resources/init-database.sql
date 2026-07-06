CREATE TABLE IF NOT EXISTS `asset_categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Category code (ETF, GOLD, STOCK, EMERGENCY)',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Category display name',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT 'Category description',
  `is_active` tinyint(1) DEFAULT '1' COMMENT 'Active status',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_code` (`code`),
  KEY `idx_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Master data for asset categories';

CREATE TABLE IF NOT EXISTS `asset_platforms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Platform code (VNDIRECT, SSI, VIETCAP, GOLD_MARKET)',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Platform display name',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT 'Platform description',
  `is_active` tinyint(1) DEFAULT '1' COMMENT 'Active status',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_platform_code` (`code`),
  KEY `idx_platform_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Master data for trading platforms';

CREATE TABLE IF NOT EXISTS `asset_products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Product code (VNI, SJC_1D, TCB, etc.)',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Product display name',
  `category_id` int NOT NULL COMMENT 'Reference to asset_categories',
  `platform_id` bigint NOT NULL COMMENT 'Reference to asset_platforms',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT 'Product description',
  `minimum_quantity` decimal(19,2) DEFAULT NULL COMMENT 'Minimum quantity to purchase',
  `unit_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Unit name (chi, don vi quy, co phieu, etc.)',
  `is_active` tinyint(1) DEFAULT '1' COMMENT 'Active status',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_product_code` (`code`),
  KEY `idx_product_category` (`category_id`),
  KEY `idx_product_platform` (`platform_id`),
  KEY `idx_product_active` (`is_active`),
  CONSTRAINT `fk_asset_products_category` FOREIGN KEY (`category_id`) REFERENCES `asset_categories` (`id`),
  CONSTRAINT `fk_asset_products_platform` FOREIGN KEY (`platform_id`) REFERENCES `asset_platforms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Specific products with category and platform hierarchy';

CREATE TABLE IF NOT EXISTS `asset_prices` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL COMMENT 'Reference to asset_products',
  `current_price` decimal(19,6) NOT NULL COMMENT 'Current price',
  `price_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Price timestamp',
  `is_latest` tinyint(1) DEFAULT '1' COMMENT 'Mark as latest price',
  `is_active` tinyint(1) DEFAULT '1' COMMENT 'Active status',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `asset_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_price_product` (`product_id`),
  KEY `idx_price_latest` (`is_latest`),
  KEY `idx_price_date` (`price_date`),
  CONSTRAINT `fk_asset_prices_product` FOREIGN KEY (`product_id`) REFERENCES `asset_products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Real-time and historical prices for products';

