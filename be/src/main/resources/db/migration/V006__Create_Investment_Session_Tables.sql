-- Flyway Migration: V006__Create_Investment_Session_Tables.sql
-- This migration creates tables for investment session workflow

-- ==================== INVESTMENT SESSIONS ====================
CREATE TABLE IF NOT EXISTS `investment_sessions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(36) NOT NULL,
  `strategy_code` VARCHAR(30) NOT NULL,
  `total_amount` DECIMAL(19,2) NOT NULL,
  `status` VARCHAR(30) NOT NULL,
  `current_step` VARCHAR(30) NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_user` (`user_id` ASC),
  CONSTRAINT `investment_sessions_ibfk_1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`user_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- ==================== INVESTMENT SESSION ALLOCATIONS ====================
CREATE TABLE IF NOT EXISTS `investment_session_allocations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT NOT NULL,
  `asset_type` VARCHAR(50) NOT NULL,
  `recommended_amount` DECIMAL(19,2) NOT NULL,
  `percentage` DECIMAL(5,2) NOT NULL,
  `status` VARCHAR(30) NOT NULL,
  `invested_amount` DECIMAL(19,2),
  `residual_amount` DECIMAL(19,2),
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_session` (`session_id` ASC),
  CONSTRAINT `investment_session_allocations_ibfk_1`
    FOREIGN KEY (`session_id`)
    REFERENCES `investment_sessions` (`id`) ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- ==================== INVESTMENT SESSION EVENTS ====================
CREATE TABLE IF NOT EXISTS `investment_session_events` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT NOT NULL,
  `event_type` VARCHAR(50) NOT NULL,
  `payload` JSON,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_session` (`session_id` ASC),
  CONSTRAINT `investment_session_events_ibfk_1`
    FOREIGN KEY (`session_id`)
    REFERENCES `investment_sessions` (`id`) ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- ==================== INVESTMENT ASSET SELECTIONS ====================
CREATE TABLE IF NOT EXISTS `investment_asset_selections` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT NOT NULL,
  `allocation_id` BIGINT NOT NULL,
  `asset_code` VARCHAR(50) NOT NULL,
  `asset_name` VARCHAR(255),
  `quantity` DECIMAL(19,4) NOT NULL,
  `buy_price` DECIMAL(19,6) NOT NULL,
  `invested_amount` DECIMAL(19,2) NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_allocation` (`allocation_id` ASC),
  INDEX `idx_session` (`session_id` ASC),
  CONSTRAINT `investment_asset_selections_ibfk_1`
    FOREIGN KEY (`allocation_id`)
    REFERENCES `investment_session_allocations` (`id`) ON DELETE CASCADE,
  CONSTRAINT `investment_asset_selections_ibfk_2`
    FOREIGN KEY (`session_id`)
    REFERENCES `investment_sessions` (`id`) ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- ==================== USER CASH BALANCE ====================
CREATE TABLE IF NOT EXISTS `user_cash_balance` (
  `user_id` VARCHAR(36) NOT NULL,
  `cash_amount` DECIMAL(19,2) NOT NULL DEFAULT 0.00,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `user_cash_balance_ibfk_1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`user_id`) ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


