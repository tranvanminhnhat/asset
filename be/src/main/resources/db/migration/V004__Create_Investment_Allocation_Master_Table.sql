-- Migration: Create investment_allocation_master table
CREATE TABLE IF NOT EXISTS `investment_allocation_master` (
    `code` VARCHAR(30) NOT NULL,
    `stock_percentage` DECIMAL(5,2) NOT NULL,
    `emergency_percentage` DECIMAL(5,2) NOT NULL,
    `gold_percentage` DECIMAL(5,2) NOT NULL,
    `etf_percentage` DECIMAL(5,2) NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NULL,
    PRIMARY KEY (`code`)
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- Insert default data
INSERT INTO `investment_allocation_master` (`code`, `stock_percentage`, `emergency_percentage`, `gold_percentage`, `etf_percentage`, `created_at`, `updated_at`) VALUES
('SAFE', 20, 40, 30, 10, NOW(), NOW()),
('BALANCED', 35, 25, 20, 20, NOW(), NOW()),
('GROWTH', 50, 10, 10, 30, NOW(), NOW());

