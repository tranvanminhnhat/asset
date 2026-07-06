-- Test Data Insert Script for Asset Database
-- Run this script to insert test data into MySQL database
-- Usage: mysql -h localhost -u root -p asset_db < test-data.sql

-- ========== INSERT TEST DATA ==========

-- 1. Insert into asset_categories
INSERT INTO `asset_categories` (`code`, `name`, `description`, `is_active`) VALUES
('STOCK', 'Stock', 'Stock assets', 1),
('ETF', 'ETF', 'Exchange Traded Funds', 1),
('GOLD', 'Gold', 'Gold assets', 1);

SELECT 'Categories inserted' AS status;

-- 2. Insert into asset_platforms
INSERT INTO `asset_platforms` (`code`, `name`, `description`, `is_active`) VALUES
('VNDIRECT', 'VNDirect', 'VNDirect trading platform', 1),
('GOLD_MARKET', 'Gold Market', 'SJC Gold Market', 1);

SELECT 'Platforms inserted' AS status;

-- 3. Insert into asset_products
-- Get category and platform IDs (assuming they are 1, 2, 3 and 1, 2)
INSERT INTO `asset_products` (`code`, `name`, `category_id`, `platform_id`, `unit_name`, `description`, `is_active`) VALUES
('TCB', 'Techcombank', 1, 1, 'cổ phiếu', 'Techcombank stock', 1),
('FPT', 'FPT Corporation', 1, 1, 'cổ phiếu', 'FPT Corporation stock', 1),
('VCB', 'VietcomBank', 1, 1, 'cổ phiếu', 'VietcomBank stock', 1),
('E1VFVN30', 'E1VFVN30 ETF', 2, 1, 'đơn vị quỹ', 'E1VFVN30 ETF', 1),
('SJC_1D', 'SJC 1 Chỉ', 3, 2, 'chỉ', 'SJC bar gold 1 chỉ', 1);

SELECT 'Products inserted' AS status;

-- 4. Insert into asset_prices
-- Get product IDs (assuming they are 1-5 based on insertion order above)
INSERT INTO `asset_prices` (`product_id`, `current_price`, `asset_type`, `is_latest`, `is_active`) VALUES
(1, 27000.50, 'STOCK', 1, 1),
(2, 85000.00, 'STOCK', 1, 1),
(3, 100000.00, 'STOCK', 1, 1),
(4, 35800.00, 'ETF', 1, 1),
(5, 67500000.00, 'GOLD', 1, 1);

SELECT 'Prices inserted' AS status;

-- ========== VERIFY DATA ==========

SELECT '--- Summary ---' AS '';
SELECT 'Categories count:' AS metric, COUNT(*) AS count FROM `asset_categories`;
SELECT 'Platforms count:' AS metric, COUNT(*) AS count FROM `asset_platforms`;
SELECT 'Products count:' AS metric, COUNT(*) AS count FROM `asset_products`;
SELECT 'Prices count:' AS metric, COUNT(*) AS count FROM `asset_prices`;

-- ========== SAMPLE QUERIES ==========

SELECT '' AS '';
SELECT '--- Sample Data ---' AS '';

-- All categories
SELECT 'Categories:' AS '';
SELECT `id`, `code`, `name`, `is_active` FROM `asset_categories`;

-- All platforms
SELECT '' AS '';
SELECT 'Platforms:' AS '';
SELECT `id`, `code`, `name`, `is_active` FROM `asset_platforms`;

-- All products with category and platform names
SELECT '' AS '';
SELECT 'Products:' AS '';
SELECT
    p.`id`,
    p.`code`,
    p.`name`,
    c.`code` AS category,
    pl.`code` AS platform,
    p.`is_active`
FROM `asset_products` p
LEFT JOIN `asset_categories` c ON p.`category_id` = c.`id`
LEFT JOIN `asset_platforms` pl ON p.`platform_id` = pl.`id`;

-- All prices with product names
SELECT '' AS '';
SELECT 'Prices:' AS '';
SELECT
    pr.`id`,
    p.`code` AS product_code,
    p.`name` AS product_name,
    pr.`current_price`,
    pr.`asset_type`,
    pr.`is_latest`,
    pr.`price_date`
FROM `asset_prices` pr
LEFT JOIN `asset_products` p ON pr.`product_id` = p.`id`;

SELECT '' AS '';
SELECT '✓ Test data insertion completed!' AS status;

