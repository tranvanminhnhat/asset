-- ============================================================================
-- COMPLETE DATABASE SCHEMA - Asset Investment Management System
-- This file contains ALL tables for the entire project
-- Drop all existing tables and run this to get a fresh start
-- ============================================================================

-- ============================================================================
-- STEP 0: DROP ALL EXISTING TABLES (if starting fresh)
-- ============================================================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `asset_prices`;
DROP TABLE IF EXISTS `asset_products`;
DROP TABLE IF EXISTS `asset_platforms`;
DROP TABLE IF EXISTS `asset_categories`;
DROP TABLE IF EXISTS `investment_asset_selections`;
DROP TABLE IF EXISTS `investment_session_events`;
DROP TABLE IF EXISTS `investment_session_allocations`;
DROP TABLE IF EXISTS `investment_sessions`;
DROP TABLE IF EXISTS `user_cash_balance`;
DROP TABLE IF EXISTS `portfolio_transactions`;
DROP TABLE IF EXISTS `portfolio`;
DROP TABLE IF EXISTS `investment_plan_allocations`;
DROP TABLE IF EXISTS `investment_plans`;
DROP TABLE IF EXISTS `mst_allocation_strategy_details`;
DROP TABLE IF EXISTS `mst_allocation_strategies`;
DROP TABLE IF EXISTS `investment_allocation_master`;
DROP TABLE IF EXISTS `allocation_strategy_details`;
DROP TABLE IF EXISTS `allocation_strategies`;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- TABLE: user
-- Description: User account information for authentication and authorization
-- ============================================================================
CREATE TABLE `user` (
  `user_id` VARCHAR(36) NOT NULL COMMENT 'UUID as primary key',
  `email` VARCHAR(255) NOT NULL COMMENT 'User email address',
  `password` VARCHAR(255) NOT NULL COMMENT 'Hashed password',
  `is_verified` TINYINT(1) NULL DEFAULT '0' COMMENT 'Email verification status',
  `status` VARCHAR(30) NOT NULL COMMENT 'ACTIVE, INACTIVE, SUSPENDED',
  `token` VARCHAR(2000) NULL COMMENT 'JWT refresh token (increased to 2000 for long tokens)',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `uk_email` (`email` ASC)
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'User authentication table';

-- ============================================================================
-- TABLE: mst_allocation_strategies
-- Description: Master data for investment allocation strategies (SAFE, BALANCED, GROWTH)
-- ============================================================================
CREATE TABLE `mst_allocation_strategies` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(30) NOT NULL UNIQUE COMMENT 'Strategy code (SAFE, BALANCED, GROWTH)',
  `name` VARCHAR(100) NULL COMMENT 'Strategy display name',
  `description` TEXT NULL COMMENT 'Strategy description',
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_code` (`code` ASC)
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Master allocation strategies for investment planning';

-- ============================================================================
-- TABLE: mst_allocation_strategy_details
-- Description: Allocation percentages per asset type for each strategy
-- ============================================================================
CREATE TABLE `mst_allocation_strategy_details` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `strategy_id` BIGINT NOT NULL,
  `asset_type` VARCHAR(50) NOT NULL COMMENT 'Asset type: EMERGENCY, GOLD, ETF, STOCK',
  `percentage` DECIMAL(5,2) NOT NULL COMMENT 'Allocation percentage for this asset',
  `strategy_code` VARCHAR(45) NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_strategy_asset` (`strategy_id` ASC, `asset_type` ASC),
  INDEX `idx_strategy` (`strategy_id` ASC),
  CONSTRAINT `fk_allocation_details_strategy`
    FOREIGN KEY (`strategy_id`)
    REFERENCES `mst_allocation_strategies` (`id`)
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Allocation percentages per asset type for each strategy';

-- ============================================================================
-- TABLE: portfolio
-- Description: User portfolio holdings summary by asset type
-- ============================================================================
CREATE TABLE `portfolio` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(36) NOT NULL COMMENT 'Reference to user',
  `asset_type` VARCHAR(50) NOT NULL COMMENT 'Asset type: EMERGENCY, GOLD, ETF, STOCK',
  `quantity` DECIMAL(19,6) NOT NULL COMMENT 'Total quantity held',
  `total_invested` DECIMAL(19,2) NOT NULL COMMENT 'Total amount invested',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_asset` (`user_id` ASC, `asset_type` ASC),
  CONSTRAINT `fk_portfolio_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Portfolio holdings summary by asset type';

-- ============================================================================
-- TABLE: portfolio_transactions
-- Description: Append-only audit trail of all portfolio transactions
-- ============================================================================
CREATE TABLE `portfolio_transactions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(36) NOT NULL COMMENT 'Reference to user',
  `asset_type` VARCHAR(50) NOT NULL COMMENT 'Asset type: EMERGENCY, GOLD, ETF, STOCK',
  `transaction_type` VARCHAR(20) NOT NULL COMMENT 'BUY, SELL, DIVIDEND, ADJUSTMENT',
  `quantity` DECIMAL(19,6) NULL COMMENT 'Transaction quantity',
  `price` DECIMAL(19,6) NULL COMMENT 'Unit price at transaction time',
  `total_amount` DECIMAL(19,2) NOT NULL COMMENT 'Total transaction amount',
  `fee` DECIMAL(19,2) NULL DEFAULT '0.00' COMMENT 'Transaction fee',
  `tax` DECIMAL(19,2) NULL DEFAULT '0.00' COMMENT 'Transaction tax',
  `reference_id` VARCHAR(100) NULL COMMENT 'Reference from external system',
  `note` VARCHAR(255) NULL COMMENT 'Transaction notes',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_reference` (`reference_id` ASC),
  INDEX `idx_user` (`user_id` ASC),
  INDEX `idx_asset` (`asset_type` ASC),
  CONSTRAINT `fk_portfolio_tx_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Append-only transaction log for portfolio audit trail';

-- ============================================================================
-- TABLE: investment_plans (Legacy - kept for backward compatibility)
-- Description: Investment plans created by users
-- ============================================================================
CREATE TABLE `investment_plans` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(36) NOT NULL,
  `strategy_id` BIGINT NOT NULL,
  `total_amount` DECIMAL(19,2) NOT NULL,
  `status` VARCHAR(30) NOT NULL,
  `recommendation_snapshot` JSON,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_user` (`user_id` ASC),
  INDEX `idx_strategy` (`strategy_id` ASC),
  CONSTRAINT `fk_investment_plan_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`user_id`),
  CONSTRAINT `fk_investment_plan_strategy`
    FOREIGN KEY (`strategy_id`)
    REFERENCES `mst_allocation_strategies` (`id`)
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Legacy investment plans';

-- ============================================================================
-- TABLE: investment_plan_allocations (Legacy - kept for backward compatibility)
-- Description: Allocations for legacy investment plans
-- ============================================================================
CREATE TABLE `investment_plan_allocations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `plan_id` BIGINT NOT NULL,
  `asset_type` VARCHAR(50) NOT NULL,
  `amount` DECIMAL(19,2) NOT NULL,
  `percentage` DECIMAL(5,2) NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_plan` (`plan_id` ASC),
  CONSTRAINT `fk_plan_allocation`
    FOREIGN KEY (`plan_id`)
    REFERENCES `investment_plans` (`id`)
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Allocations for legacy investment plans';

-- ============================================================================
-- TABLE: investment_sessions
-- Description: Investment sessions created by users for the new workflow
-- ============================================================================
CREATE TABLE `investment_sessions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(36) NOT NULL COMMENT 'Reference to user',
  `strategy_code` VARCHAR(30) NOT NULL COMMENT 'Strategy code (SAFE, BALANCED, GROWTH)',
  `total_amount` DECIMAL(19,2) NOT NULL COMMENT 'Total amount to invest',
  `status` VARCHAR(30) NOT NULL COMMENT 'DRAFT, IN_PROGRESS, COMPLETED, CANCELLED',
  `current_step` VARCHAR(30) NOT NULL COMMENT 'Current step in workflow',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_user` (`user_id` ASC),
  CONSTRAINT `fk_session_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`user_id`)
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Investment sessions for the new workflow';

-- ============================================================================
-- TABLE: investment_session_allocations
-- Description: Allocation targets for each asset type in an investment session
-- ============================================================================
CREATE TABLE `investment_session_allocations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT NOT NULL COMMENT 'Reference to investment session',
  `asset_type` VARCHAR(50) NOT NULL COMMENT 'Asset type: EMERGENCY, GOLD, ETF, STOCK',
  `recommended_amount` DECIMAL(19,2) NOT NULL COMMENT 'Recommended amount based on strategy',
  `percentage` DECIMAL(5,2) NOT NULL COMMENT 'Allocation percentage',
  `status` VARCHAR(30) NOT NULL COMMENT 'NOT_STARTED, IN_PROGRESS, COMPLETED, LOCKED',
  `invested_amount` DECIMAL(19,2) COMMENT 'Actual amount invested',
  `residual_amount` DECIMAL(19,2) COMMENT 'Remaining amount to invest',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_session` (`session_id` ASC),
  CONSTRAINT `fk_session_allocation`
    FOREIGN KEY (`session_id`)
    REFERENCES `investment_sessions` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Allocation targets for assets in investment session';

-- ============================================================================
-- TABLE: investment_session_events
-- Description: Audit trail of events during investment session workflow
-- ============================================================================
CREATE TABLE `investment_session_events` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT NOT NULL COMMENT 'Reference to investment session',
  `event_type` VARCHAR(50) NOT NULL COMMENT 'Event type: SESSION_CREATED, ASSET_SELECTED, SESSION_COMPLETED, etc.',
  `payload` JSON COMMENT 'Event payload in JSON format',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_session` (`session_id` ASC),
  CONSTRAINT `fk_session_event`
    FOREIGN KEY (`session_id`)
    REFERENCES `investment_sessions` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Audit trail of events during investment session';

-- ============================================================================
-- TABLE: user_cash_balance
-- Description: User cash/emergency fund balance
-- ============================================================================
CREATE TABLE `user_cash_balance` (
  `user_id` VARCHAR(36) NOT NULL COMMENT 'Reference to user',
  `cash_amount` DECIMAL(19,2) NOT NULL DEFAULT 0.00 COMMENT 'Current cash balance',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_cash_balance_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'User cash balance for emergency funds';

-- ============================================================================
-- TABLE: asset_categories
-- Description: Master data for asset categories (ETF, GOLD, STOCK, EMERGENCY)
-- ============================================================================
CREATE TABLE `asset_categories` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Category code (ETF, GOLD, STOCK, EMERGENCY)',
  `name` VARCHAR(100) NOT NULL COMMENT 'Category display name',
  `description` TEXT COMMENT 'Category description',
  `is_active` BOOLEAN DEFAULT TRUE COMMENT 'Active status',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_code` (`code`),
  INDEX `idx_active` (`is_active`)
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Master data for asset categories';

-- ============================================================================
-- TABLE: asset_platforms
-- Description: Master data for trading platforms (VNDIRECT, SSI, VIETCAP, GOLD_MARKET)
-- ============================================================================
CREATE TABLE `asset_platforms` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Platform code (VNDIRECT, SSI, VIETCAP, GOLD_MARKET)',
  `name` VARCHAR(255) NOT NULL COMMENT 'Platform display name',
  `description` TEXT COMMENT 'Platform description',
  `is_active` BOOLEAN DEFAULT TRUE COMMENT 'Active status',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_platform_code` (`code`),
  INDEX `idx_platform_active` (`is_active`)
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Master data for trading platforms';

-- ============================================================================
-- TABLE: asset_products
-- Description: Specific products with category and platform hierarchy
-- ============================================================================
CREATE TABLE `asset_products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Product code (VNI, SJC_1D, TCB, etc.)',
  `name` VARCHAR(255) NOT NULL COMMENT 'Product display name',
  `category_id` INT NOT NULL COMMENT 'Reference to asset_categories',
  `platform_id` BIGINT NOT NULL COMMENT 'Reference to asset_platforms',
  `description` TEXT COMMENT 'Product description',
  `minimum_quantity` DECIMAL(19,2) COMMENT 'Minimum quantity to purchase',
  `unit_name` VARCHAR(50) COMMENT 'Unit name (chi, don vi quy, co phieu, etc.)',
  `is_active` BOOLEAN DEFAULT TRUE COMMENT 'Active status',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_product_code` (`code`),
  INDEX `idx_product_category` (`category_id`),
  INDEX `idx_product_platform` (`platform_id`),
  INDEX `idx_product_active` (`is_active`),
  CONSTRAINT `fk_asset_products_category`
    FOREIGN KEY (`category_id`)
    REFERENCES `asset_categories`(`id`),
  CONSTRAINT `fk_asset_products_platform`
    FOREIGN KEY (`platform_id`)
    REFERENCES `asset_platforms`(`id`)
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Specific products with category and platform hierarchy';

-- ============================================================================
-- TABLE: asset_prices
-- Description: Real-time and historical prices for products with version tracking
-- ============================================================================
CREATE TABLE `asset_prices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL COMMENT 'Reference to asset_products',
  `buy_price` DECIMAL(19,6) NOT NULL COMMENT 'Buy price',
  `sell_price` DECIMAL(19,6) COMMENT 'Sell price',
  `price_date` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Price timestamp',
  `is_latest` BOOLEAN DEFAULT TRUE COMMENT 'Mark as latest price',
  `is_active` BOOLEAN DEFAULT TRUE COMMENT 'Active status',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_price_product` (`product_id`),
  INDEX `idx_price_latest` (`is_latest`),
  INDEX `idx_price_date` (`price_date`),
  CONSTRAINT `fk_asset_prices_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `asset_products`(`id`) ON DELETE CASCADE
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Real-time and historical prices for products';

-- ============================================================================
-- TABLE: investment_asset_selections
-- Description: Asset selections made during investment session workflow
-- ============================================================================
CREATE TABLE `investment_asset_selections` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT NOT NULL COMMENT 'Reference to investment session',
  `allocation_id` BIGINT NOT NULL COMMENT 'Reference to investment session allocation',
  `asset_code` VARCHAR(50) NOT NULL COMMENT 'Asset code for backward compatibility',
  `asset_name` VARCHAR(255) COMMENT 'Asset name',
  `quantity` DECIMAL(19,4) NOT NULL COMMENT 'Quantity selected',
  `buy_price` DECIMAL(19,6) NOT NULL COMMENT 'Locked buy price at selection time',
  `invested_amount` DECIMAL(19,2) NOT NULL COMMENT 'Total invested amount (quantity × price)',
  `product_id` BIGINT COMMENT 'Reference to asset_products (new hierarchy)',
  `platform_code` VARCHAR(50) COMMENT 'Platform code for audit trail',
  `product_code` VARCHAR(50) COMMENT 'Product code for audit trail',
  `product_name` VARCHAR(255) COMMENT 'Product name for display',
  `category_code` VARCHAR(50) COMMENT 'Category code for audit trail',
  `unit_name` VARCHAR(50) COMMENT 'Unit name of product',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_allocation` (`allocation_id` ASC),
  INDEX `idx_session` (`session_id` ASC),
  INDEX `idx_product` (`product_id` ASC),
  CONSTRAINT `fk_asset_selection_allocation`
    FOREIGN KEY (`allocation_id`)
    REFERENCES `investment_session_allocations` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_asset_selection_session`
    FOREIGN KEY (`session_id`)
    REFERENCES `investment_sessions` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Asset selections during investment session workflow';

-- ============================================================================
-- STEP 1: INSERT MASTER DATA FOR ALLOCATION STRATEGIES
-- ============================================================================
INSERT INTO `mst_allocation_strategies` (`code`, `name`, `created_at`)
VALUES
  ('SAFE', 'Safe Investment Plan', CURRENT_TIMESTAMP),
  ('BALANCED', 'Balanced Growth Strategy', CURRENT_TIMESTAMP),
  ('GROWTH', 'Aggressive Growth Strategy', CURRENT_TIMESTAMP);

-- ============================================================================
-- STEP 2: INSERT STRATEGY DETAILS
-- ============================================================================
-- SAFE Strategy (id=1): Emergency=40%, Gold=30%, ETF=10%, Stock=20%
INSERT INTO `mst_allocation_strategy_details` (`strategy_id`, `asset_type`, `percentage`, `strategy_code`)
VALUES
  (1, 'EMERGENCY', 40.00, 'SAFE'),
  (1, 'GOLD', 30.00, 'SAFE'),
  (1, 'ETF', 10.00, 'SAFE'),
  (1, 'STOCK', 20.00, 'SAFE');

-- BALANCED Strategy (id=2): Emergency=25%, Gold=20%, ETF=20%, Stock=35%
INSERT INTO `mst_allocation_strategy_details` (`strategy_id`, `asset_type`, `percentage`, `strategy_code`)
VALUES
  (2, 'EMERGENCY', 25.00, 'BALANCED'),
  (2, 'GOLD', 20.00, 'BALANCED'),
  (2, 'ETF', 20.00, 'BALANCED'),
  (2, 'STOCK', 35.00, 'BALANCED');

-- GROWTH Strategy (id=3): Emergency=10%, Gold=10%, ETF=30%, Stock=50%
INSERT INTO `mst_allocation_strategy_details` (`strategy_id`, `asset_type`, `percentage`, `strategy_code`)
VALUES
  (3, 'EMERGENCY', 10.00, 'GROWTH'),
  (3, 'GOLD', 10.00, 'GROWTH'),
  (3, 'ETF', 30.00, 'GROWTH'),
  (3, 'STOCK', 50.00, 'GROWTH');

-- ============================================================================
-- STEP 3: INSERT ASSET CATEGORIES
-- ============================================================================
INSERT IGNORE INTO `asset_categories` (`id`, `code`, `name`, `description`, `is_active`) VALUES
(1, 'ETF', 'Quy chi so', 'Quy giao dich tren san chung chi - Exchange Traded Funds', TRUE),
(2, 'GOLD', 'Vang', 'Vang thoi, vang miem - Precious metals', TRUE),
(3, 'STOCK', 'Co phieu', 'Co phieu cong ty dai chung - Stock market securities', TRUE),
(4, 'EMERGENCY', 'Tien mat', 'Quy khoan cap - Emergency cash funds', TRUE);

-- ============================================================================
-- STEP 4: INSERT ASSET PLATFORMS
-- ============================================================================
INSERT IGNORE INTO `asset_platforms` (`id`, `code`, `name`, `description`, `is_active`) VALUES
(1, 'VNDIRECT', 'VNDirect', 'VNDirect - Nha cung cap dich vu ty gia', TRUE),
(2, 'SSI', 'SSI', 'SSI - Sai Gon Securities', TRUE),
(3, 'VIETCAP', 'Vietcap', 'Vietcap - Nha cung cap dich vu ty gia', TRUE),
(4, 'GOLD_MARKET', 'San Vang', 'San Vang - Precious metals exchange', TRUE);

-- ============================================================================
-- STEP 5: INSERT ASSET PRODUCTS
-- ============================================================================
INSERT IGNORE INTO `asset_products` (`id`, `code`, `name`, `category_id`, `platform_id`, `unit_name`, `minimum_quantity`, `is_active`) VALUES
(1, 'VNI', 'Quy VN-Index ETF', 1, 1, 'don vi quy', 1.00, TRUE),
(2, 'VCI10', 'Quy VCI10 ETF', 1, 1, 'don vi quy', 1.00, TRUE),
(3, 'SJC_1D', 'Vang SJC 1 chi', 2, 4, 'chi', 1.00, TRUE),
(4, 'TCB', 'Techcombank Stock', 3, 1, 'co phieu', 1.00, TRUE);

-- ============================================================================
-- STEP 6: INSERT ASSET PRICES (Latest prices)
-- ============================================================================
INSERT IGNORE INTO `asset_prices` (`id`, `product_id`, `buy_price`, `sell_price`, `price_date`, `is_latest`, `is_active`) VALUES
(1, 1, 100.500000, 100.000000, CURRENT_TIMESTAMP, TRUE, TRUE),
(2, 2, 250.750000, 250.000000, CURRENT_TIMESTAMP, TRUE, TRUE),
(3, 3, 50000.000000, 49800.000000, CURRENT_TIMESTAMP, TRUE, TRUE),
(4, 4, 150.500000, 150.000000, CURRENT_TIMESTAMP, TRUE, TRUE);

-- ============================================================================
-- COMPLETION
-- ============================================================================
-- Schema creation and data insertion complete
-- All tables are ready for the backend application
-- Tables created:
--  1. user - User authentication
--  2. mst_allocation_strategies - Master strategies (SAFE, BALANCED, GROWTH)
--  3. mst_allocation_strategy_details - Strategy allocations
--  4. portfolio - User portfolio holdings
--  5. portfolio_transactions - Audit trail
--  6. investment_plans - Legacy plans
--  7. investment_plan_allocations - Legacy allocations
--  8. investment_sessions - Investment sessions
--  9. investment_session_allocations - Session allocations
-- 10. investment_session_events - Audit trail
-- 11. user_cash_balance - Cash balance
-- 12. asset_categories - Asset categories (ETF, GOLD, STOCK, EMERGENCY)
-- 13. asset_platforms - Trading platforms (VNDIRECT, SSI, VIETCAP, GOLD_MARKET)
-- 14. asset_products - Product hierarchy (VNI, VCI10, SJC_1D, TCB)
-- 15. asset_prices - Real-time prices with history
-- 16. investment_asset_selections - Selected assets during session
-- ============================================================================

