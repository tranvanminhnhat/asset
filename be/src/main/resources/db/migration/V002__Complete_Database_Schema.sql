-- Flyway Migration: V002__Complete_Database_Schema.sql
-- This migration creates all tables matching the new application structure

-- Drop existing tables to recreate them properly (only for development)
-- Uncomment these lines if starting fresh:
-- DROP TABLE IF EXISTS investment_plan_allocations;
-- DROP TABLE IF EXISTS investment_plans;
-- DROP TABLE IF EXISTS portfolio_transactions;
-- DROP TABLE IF EXISTS portfolio;
-- DROP TABLE IF EXISTS allocation_strategy_details;
-- DROP TABLE IF EXISTS allocation_strategies;
-- DROP TABLE IF EXISTS asset_prices;
-- DROP TABLE IF EXISTS user;

-- ==================== USER TABLE ====================
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` VARCHAR(36) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `is_verified` TINYINT(1) NULL DEFAULT '0',
  `status` VARCHAR(30) NOT NULL,
  `token` VARCHAR(45) NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `email` (`email` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- ==================== ALLOCATION STRATEGIES ====================
CREATE TABLE IF NOT EXISTS `allocation_strategies` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(30) NULL,
  `name` VARCHAR(100) NULL,
  `description` TEXT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `code` (`code` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- ==================== ALLOCATION STRATEGY DETAILS ====================
CREATE TABLE IF NOT EXISTS `allocation_strategy_details` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `strategy_id` BIGINT NOT NULL,
  `asset_type` VARCHAR(50) NOT NULL,
  `percentage` DECIMAL(5,2) NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_strategy` (`strategy_id` ASC),
  CONSTRAINT `fk_strategy`
    FOREIGN KEY (`strategy_id`)
    REFERENCES `allocation_strategies` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- ==================== PORTFOLIO ====================
CREATE TABLE IF NOT EXISTS `portfolio` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(36) NOT NULL,
  `asset_type` VARCHAR(50) NOT NULL,
  `quantity` DECIMAL(19,6) NOT NULL,
  `total_invested` DECIMAL(19,2) NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_asset` (`user_id` ASC, `asset_type` ASC),
  CONSTRAINT `fk_portfolio_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`user_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- ==================== PORTFOLIO TRANSACTIONS ====================
CREATE TABLE IF NOT EXISTS `portfolio_transactions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(36) NOT NULL,
  `asset_type` VARCHAR(50) NOT NULL,
  `transaction_type` VARCHAR(20) NOT NULL,
  `quantity` DECIMAL(19,6) NULL,
  `price` DECIMAL(19,6) NULL,
  `total_amount` DECIMAL(19,2) NOT NULL,
  `fee` DECIMAL(19,2) NULL DEFAULT '0.00',
  `tax` DECIMAL(19,2) NULL DEFAULT '0.00',
  `reference_id` VARCHAR(100) NULL,
  `note` VARCHAR(255) NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `reference_id` (`reference_id` ASC),
  INDEX `idx_tx_user` (`user_id` ASC),
  INDEX `idx_tx_asset` (`asset_type` ASC),
  CONSTRAINT `fk_tx_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`user_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- ==================== ASSET PRICES ====================
CREATE TABLE IF NOT EXISTS `asset_prices` (
  `asset_type` VARCHAR(50) NOT NULL,
  `price` DECIMAL(19,6) NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`asset_type`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- ==================== INVESTMENT PLANS ====================
CREATE TABLE IF NOT EXISTS `investment_plans` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(36) NOT NULL,
  `strategy_id` BIGINT NOT NULL,
  `total_amount` DECIMAL(19,2) NOT NULL,
  `status` VARCHAR(30) NOT NULL,
  `recommendation_snapshot` JSON,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_plan_user` (`user_id` ASC),
  INDEX `fk_plan_strategy` (`strategy_id` ASC),
  CONSTRAINT `fk_plan_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`user_id`),
  CONSTRAINT `fk_plan_strategy`
    FOREIGN KEY (`strategy_id`)
    REFERENCES `allocation_strategies` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- ==================== INVESTMENT PLAN ALLOCATIONS ====================
CREATE TABLE IF NOT EXISTS `investment_plan_allocations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `plan_id` BIGINT NOT NULL,
  `asset_type` VARCHAR(50) NOT NULL,
  `amount` DECIMAL(19,2) NOT NULL,
  `percentage` DECIMAL(5,2) NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_plan_alloc` (`plan_id` ASC),
  CONSTRAINT `fk_plan_alloc`
    FOREIGN KEY (`plan_id`)
    REFERENCES `investment_plans` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- ==================== INSERT SAMPLE DATA ====================
-- Insert allocation strategies
INSERT INTO `allocation_strategies` (`code`, `name`, `description`) VALUES
('SAFE', 'Safe Strategy', 'Conservative investment strategy with focus on safety'),
('BALANCED', 'Balanced Strategy', 'Balanced investment strategy with moderate risk'),
('GROWTH', 'Growth Strategy', 'Aggressive growth strategy with higher risk');

-- Insert allocation strategy details for SAFE strategy
INSERT INTO `allocation_strategy_details` (`strategy_id`, `asset_type`, `percentage`)
SELECT id, 'EMERGENCY_FUND', 40 FROM `allocation_strategies` WHERE `code` = 'SAFE'
UNION ALL
SELECT id, 'GOLD', 30 FROM `allocation_strategies` WHERE `code` = 'SAFE'
UNION ALL
SELECT id, 'ETF', 10 FROM `allocation_strategies` WHERE `code` = 'SAFE'
UNION ALL
SELECT id, 'STOCK', 20 FROM `allocation_strategies` WHERE `code` = 'SAFE';

-- Insert allocation strategy details for BALANCED strategy
INSERT INTO `allocation_strategy_details` (`strategy_id`, `asset_type`, `percentage`)
SELECT id, 'EMERGENCY_FUND', 25 FROM `allocation_strategies` WHERE `code` = 'BALANCED'
UNION ALL
SELECT id, 'GOLD', 20 FROM `allocation_strategies` WHERE `code` = 'BALANCED'
UNION ALL
SELECT id, 'ETF', 20 FROM `allocation_strategies` WHERE `code` = 'BALANCED'
UNION ALL
SELECT id, 'STOCK', 35 FROM `allocation_strategies` WHERE `code` = 'BALANCED';

-- Insert allocation strategy details for GROWTH strategy
INSERT INTO `allocation_strategy_details` (`strategy_id`, `asset_type`, `percentage`)
SELECT id, 'EMERGENCY_FUND', 10 FROM `allocation_strategies` WHERE `code` = 'GROWTH'
UNION ALL
SELECT id, 'GOLD', 10 FROM `allocation_strategies` WHERE `code` = 'GROWTH'
UNION ALL
SELECT id, 'ETF', 30 FROM `allocation_strategies` WHERE `code` = 'GROWTH'
UNION ALL
SELECT id, 'STOCK', 50 FROM `allocation_strategies` WHERE `code` = 'GROWTH';

-- Insert sample asset prices
INSERT INTO `asset_prices` (`asset_type`, `price`) VALUES
('EMERGENCY_FUND', 1.00),
('GOLD', 60.00),
('ETF', 150.00),
('STOCK', 100.00)
ON DUPLICATE KEY UPDATE `price` = VALUES(`price`);

