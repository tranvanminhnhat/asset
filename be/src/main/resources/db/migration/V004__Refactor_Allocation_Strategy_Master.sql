-- ============================================================================
-- Migration: V004__Refactor_Allocation_Strategy_Master.sql
-- Purpose: Refactor investment allocation master from single table to
--          normalized strategy master + details tables
-- Date: 2025-05-01
-- ============================================================================

-- Step 1: Create new strategy master table
CREATE TABLE IF NOT EXISTS `asset_db`.`mst_allocation_strategies` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(30) NOT NULL,
  `name` VARCHAR(100) NULL DEFAULT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `code` (`code` ASC)
) ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- Step 2: Create new strategy details table
CREATE TABLE IF NOT EXISTS `asset_db`.`mst_allocation_strategy_details` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `strategy_id` BIGINT NOT NULL,
  `asset_type` VARCHAR(50) NOT NULL,
  `percentage` DECIMAL(5,2) NOT NULL,
  `strategy_code` VARCHAR(45) NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `unique_strategy_asset` (`strategy_id` ASC, `asset_type` ASC) VISIBLE,
  INDEX `fk_strategy` (`strategy_id` ASC) VISIBLE,
  CONSTRAINT `fk_strategy`
    FOREIGN KEY (`strategy_id`)
    REFERENCES `asset_db`.`mst_allocation_strategies` (`id`)
) ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- Step 3: Insert strategy records
INSERT INTO `asset_db`.`mst_allocation_strategies`
  (`code`, `name`, `created_at`)
VALUES
  ('SAFE', 'Safe Investment Plan', CURRENT_TIMESTAMP),
  ('BALANCED', 'Balanced Growth Strategy', CURRENT_TIMESTAMP),
  ('GROWTH', 'Aggressive Growth Strategy', CURRENT_TIMESTAMP);

-- Step 4: Insert strategy detail records for SAFE strategy (id=1)
-- Allocations: Emergency=40%, Gold=30%, ETF=10%, Stock=20%
INSERT INTO `asset_db`.`mst_allocation_strategy_details`
  (`strategy_id`, `asset_type`, `percentage`, `strategy_code`)
VALUES
  (1, 'EMERGENCY', 40.00, 'SAFE'),
  (1, 'GOLD', 30.00, 'SAFE'),
  (1, 'ETF', 10.00, 'SAFE'),
  (1, 'STOCK', 20.00, 'SAFE');

-- Step 5: Insert strategy detail records for BALANCED strategy (id=2)
-- Allocations: Emergency=25%, Gold=20%, ETF=20%, Stock=35%
INSERT INTO `asset_db`.`mst_allocation_strategy_details`
  (`strategy_id`, `asset_type`, `percentage`, `strategy_code`)
VALUES
  (2, 'EMERGENCY', 25.00, 'BALANCED'),
  (2, 'GOLD', 20.00, 'BALANCED'),
  (2, 'ETF', 20.00, 'BALANCED'),
  (2, 'STOCK', 35.00, 'BALANCED');

-- Step 6: Insert strategy detail records for GROWTH strategy (id=3)
-- Allocations: Emergency=10%, Gold=10%, ETF=30%, Stock=50%
INSERT INTO `asset_db`.`mst_allocation_strategy_details`
  (`strategy_id`, `asset_type`, `percentage`, `strategy_code`)
VALUES
  (3, 'EMERGENCY', 10.00, 'GROWTH'),
  (3, 'GOLD', 10.00, 'GROWTH'),
  (3, 'ETF', 30.00, 'GROWTH'),
  (3, 'STOCK', 50.00, 'GROWTH');

-- Step 7: Verify data integrity
-- Verify all strategies created
-- SELECT * FROM mst_allocation_strategies;

-- Verify all details created (should be 12 rows: 3 strategies × 4 assets)
-- SELECT COUNT(*) as detail_count FROM mst_allocation_strategy_details;

-- Verify percentages sum to 100 for each strategy
-- SELECT
--   strategy_id,
--   ROUND(SUM(percentage), 2) as total_percentage
-- FROM mst_allocation_strategy_details
-- GROUP BY strategy_id
-- HAVING total_percentage != 100;

-- Step 8: Optional - Archive old table (comment out if you want to keep for reference)
-- RENAME TABLE investment_allocation_master TO investment_allocation_master_archive;

COMMIT;

