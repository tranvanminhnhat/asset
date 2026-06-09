-- V008__Create_Asset_Master_And_Admin_Management.sql
-- Creates all Asset Management tables for admin CRUD operations
-- ============================================================
-- ASSET CATEGORIES (ETF, GOLD, STOCK)
-- ============================================================
CREATE TABLE IF NOT EXISTS `asset_categories` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(50) NOT NULL UNIQUE,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_code` (`code`),
  INDEX `idx_active` (`is_active`)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4;

-- ============================================================
-- ASSET PLATFORMS (VNDIRECT, SSI, VIETCAP, GOLD_MARKET)
-- ============================================================
CREATE TABLE IF NOT EXISTS `asset_platforms` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(100) NOT NULL UNIQUE,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_platform_code` (`code`),
  INDEX `idx_platform_active` (`is_active`)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4;

-- ============================================================
-- ASSET PRODUCTS (VNI, VCI10, SJC_1D, TCB)
-- ============================================================
CREATE TABLE IF NOT EXISTS `asset_products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(100) NOT NULL UNIQUE,
  `name` VARCHAR(255) NOT NULL,
  `category_id` INT NOT NULL,
  `platform_id` BIGINT NOT NULL,
  `description` TEXT,
  `minimum_quantity` DECIMAL(19,2),
  `unit_name` VARCHAR(50),
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_product_code` (`code`),
  INDEX `idx_product_category` (`category_id`),
  INDEX `idx_product_platform` (`platform_id`),
  INDEX `idx_product_active` (`is_active`),
  CONSTRAINT `fk_asset_products_category` FOREIGN KEY (`category_id`) REFERENCES `asset_categories`(`id`),
  CONSTRAINT `fk_asset_products_platform` FOREIGN KEY (`platform_id`) REFERENCES `asset_platforms`(`id`)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4;

-- ============================================================
-- ASSET PRICES (Real-time prices with history tracking)
-- ============================================================
CREATE TABLE IF NOT EXISTS `asset_prices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `buy_price` DECIMAL(19,6) NOT NULL,
  `sell_price` DECIMAL(19,6),
  `price_date` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `is_latest` BOOLEAN DEFAULT TRUE,
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_price_product` (`product_id`),
  INDEX `idx_price_latest` (`is_latest`),
  INDEX `idx_price_date` (`price_date`),
  CONSTRAINT `fk_asset_prices_product` FOREIGN KEY (`product_id`) REFERENCES `asset_products`(`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4;

-- ============================================================
-- UPDATE investment_asset_selections to use new hierarchy
-- ============================================================
ALTER TABLE `investment_asset_selections`
ADD COLUMN IF NOT EXISTS `product_id` BIGINT COMMENT 'Foreign key to asset_products',
ADD COLUMN IF NOT EXISTS `platform_code` VARCHAR(50) COMMENT 'Platform code for audit trail',
ADD COLUMN IF NOT EXISTS `product_code` VARCHAR(50) COMMENT 'Product code for audit trail',
ADD COLUMN IF NOT EXISTS `product_name` VARCHAR(255) COMMENT 'Product name for display',
ADD COLUMN IF NOT EXISTS `category_code` VARCHAR(50) COMMENT 'Category code for audit trail',
ADD COLUMN IF NOT EXISTS `unit_name` VARCHAR(50) COMMENT 'Unit name of product';

-- ============================================================
-- INSERT TEST DATA
-- ============================================================
INSERT IGNORE INTO `asset_categories` (`id`, `code`, `name`, `description`, `is_active`) VALUES
(1, 'ETF', 'Quy chi so', 'Quy giao dich tren san chung chi - Exchange Traded Funds', TRUE),
(2, 'GOLD', 'Vang', 'Vang thoi, vang miem - Precious metals', TRUE),
(3, 'STOCK', 'Co phieu', 'Co phieu cong ty dai chung - Stock market securities', TRUE),
(4, 'EMERGENCY', 'Tien mat', 'Quy khoan cap - Emergency cash funds', TRUE);

INSERT IGNORE INTO `asset_platforms` (`id`, `code`, `name`, `description`, `is_active`) VALUES
(1, 'VNDIRECT', 'VNDirect', 'VNDirect - Nha cung cap dich vu ty gia', TRUE),
(2, 'SSI', 'SSI', 'SSI - Sai Gon Securities', TRUE),
(3, 'VIETCAP', 'Vietcap', 'Vietcap - Nha cung cap dich vu ty gia', TRUE),
(4, 'GOLD_MARKET', 'San Vang', 'San Vang - Precious metals exchange', TRUE);

INSERT IGNORE INTO `asset_products` (`id`, `code`, `name`, `category_id`, `platform_id`, `unit_name`, `minimum_quantity`, `is_active`) VALUES
(1, 'VNI', 'Quy VN-Index ETF', 1, 1, 'don vi quy', 1.00, TRUE),
(2, 'VCI10', 'Quy VCI10 ETF', 1, 1, 'don vi quy', 1.00, TRUE),
(3, 'SJC_1D', 'Vang SJC 1 chi', 2, 4, 'chi', 1.00, TRUE),
(4, 'TCB', 'Techcombank Stock', 3, 1, 'co phieu', 1.00, TRUE);

INSERT IGNORE INTO `asset_prices` (`id`, `product_id`, `buy_price`, `sell_price`, `price_date`, `is_latest`, `is_active`) VALUES
(1, 1, 100.500000, 100.000000, CURRENT_TIMESTAMP, TRUE, TRUE),
(2, 2, 250.750000, 250.000000, CURRENT_TIMESTAMP, TRUE, TRUE),
(3, 3, 50000.000000, 49800.000000, CURRENT_TIMESTAMP, TRUE, TRUE),
(4, 4, 150.500000, 150.000000, CURRENT_TIMESTAMP, TRUE, TRUE);

