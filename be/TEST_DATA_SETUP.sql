-- ============================================================
-- Investment Workflow - Test Data Setup Script
-- ============================================================
-- This script prepares test data for the complete investment workflow

-- ============================================================
-- 1. TEST USER SETUP
-- ============================================================

-- Insert test user (if not exists)
INSERT IGNORE INTO user (user_id, email, password, is_verified, status, created_at, updated_at)
VALUES ('test-user-001', 'testuser@example.com', 'hashed_password_here', TRUE, 'ACTIVE', NOW(), NOW());

-- Create cash balance record for test user
INSERT IGNORE INTO user_cash_balance (user_id, cash_amount, created_at, updated_at)
VALUES ('test-user-001', 10000000, NOW(), NOW());

-- ============================================================
-- 2. ALLOCATION STRATEGIES SETUP
-- ============================================================

-- Insert "Safe" strategy
INSERT IGNORE INTO mst_allocation_strategies (code, name, created_at, updated_at)
VALUES ('safe', 'Chiến lược An toàn', NOW(), NOW());

-- Get the strategy ID for details
SET @strategy_id = (SELECT id FROM mst_allocation_strategies WHERE code = 'safe' LIMIT 1);

-- Insert strategy details for "Safe" strategy
INSERT IGNORE INTO mst_allocation_strategy_details
(strategy_id, strategy_code, asset_type, percentage, minimum_lot_size_amount, created_at, updated_at)
VALUES
  (@strategy_id, 'safe', 'EMERGENCY', 10.00, 0, NOW(), NOW()),
  (@strategy_id, 'safe', 'GOLD', 20.00, 100, NOW(), NOW()),
  (@strategy_id, 'safe', 'ETF', 50.00, 100000, NOW(), NOW()),
  (@strategy_id, 'safe', 'STOCK', 20.00, 500000, NOW(), NOW());

-- ============================================================
-- 3. ASSET PRICES SETUP
-- ============================================================

-- Insert ETF assets
INSERT IGNORE INTO asset_prices
(asset_code, asset_type, asset_name, buy_price, sell_price, is_active, created_at, updated_at)
VALUES
  ('ETF001', 'ETF', 'Quỹ ETF Vàng', 100.50, 100.00, TRUE, NOW(), NOW()),
  ('ETF002', 'ETF', 'Quỹ ETF Chứng chỉ', 250.75, 250.00, TRUE, NOW(), NOW()),
  ('ETF003', 'ETF', 'Quỹ ETF Cổ phiếu', 150.25, 150.00, TRUE, NOW(), NOW());

-- Insert GOLD assets
INSERT IGNORE INTO asset_prices
(asset_code, asset_type, asset_name, buy_price, sell_price, is_active, created_at, updated_at)
VALUES
  ('GOLD001', 'GOLD', 'Vàng 999', 50000.00, 49000.00, TRUE, NOW(), NOW()),
  ('GOLD002', 'GOLD', 'Vàng 750', 35000.00, 34000.00, TRUE, NOW(), NOW());

-- Insert STOCK assets
INSERT IGNORE INTO asset_prices
(asset_code, asset_type, asset_name, buy_price, sell_price, is_active, created_at, updated_at)
VALUES
  ('STOCK001', 'STOCK', 'Cổ phiếu VNM', 150.50, 150.00, TRUE, NOW(), NOW()),
  ('STOCK002', 'STOCK', 'Cổ phiếu VCB', 200.75, 200.00, TRUE, NOW(), NOW()),
  ('STOCK003', 'STOCK', 'Cổ phiếu HPG', 50.25, 50.00, TRUE, NOW(), NOW());

-- ============================================================
-- 4. VERIFY SETUP
-- ============================================================

SELECT '=== TEST USER ===' as section;
SELECT user_id, email, is_verified, status FROM user WHERE user_id = 'test-user-001';

SELECT '=== USER CASH BALANCE ===' as section;
SELECT user_id, cash_amount FROM user_cash_balance WHERE user_id = 'test-user-001';

SELECT '=== ALLOCATION STRATEGIES ===' as section;
SELECT id, code, name FROM mst_allocation_strategies WHERE code = 'safe';

SELECT '=== STRATEGY DETAILS ===' as section;
SELECT strategy_code, asset_type, percentage, minimum_lot_size_amount
FROM mst_allocation_strategy_details WHERE strategy_code = 'safe'
ORDER BY asset_type;

SELECT '=== ASSET PRICES ===' as section;
SELECT asset_code, asset_type, asset_name, buy_price, is_active
FROM asset_prices
WHERE is_active = TRUE
ORDER BY asset_type, asset_code;

-- ============================================================
-- Notes for Testing:
-- ============================================================
-- 1. Use JWT token with userId = 'test-user-001'
-- 2. Test investment with totalAmount = 10000000 (10 million)
-- 3. Expected allocations:
--    - EMERGENCY: 1,000,000 (10%) - LOCKED
--    - GOLD: 2,000,000 (20%) - CAN PROCESS
--    - ETF: 5,000,000 (50%) - CAN PROCESS
--    - STOCK: 2,000,000 (20%) - CAN PROCESS
-- 4. Final residual sum: 7,998,995 will be added to cash balance
-- 5. After finalize, cash balance should be: 10000000 + 7998995 = 17,998,995

