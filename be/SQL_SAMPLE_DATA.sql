-- Sample data for allocation strategies
-- Insert this after running migrations

-- Insert Strategies
INSERT INTO mst_allocation_strategies (code, name, created_at) VALUES
('SAFE', 'Chiến lược An Toàn', NOW()),
('BALANCED', 'Chiến lược Cân Bằng', NOW()),
('GROWTH', 'Chiến lược Tăng Trưởng', NOW());

-- Get the inserted IDs (replace with actual IDs from your database)
-- SELECT id, code FROM mst_allocation_strategies;

-- Insert Strategy Details for SAFE (id = 1)
INSERT INTO mst_allocation_strategy_details (strategy_id, asset_type, percentage, strategy_code, created_at) VALUES
(1, 'EMERGENCY', 40, 'SAFE', NOW()),
(1, 'GOLD', 30, 'SAFE', NOW()),
(1, 'ETF', 10, 'SAFE', NOW()),
(1, 'STOCK', 20, 'SAFE', NOW());

-- Insert Strategy Details for BALANCED (id = 2)
INSERT INTO mst_allocation_strategy_details (strategy_id, asset_type, percentage, strategy_code, created_at) VALUES
(2, 'EMERGENCY', 25, 'BALANCED', NOW()),
(2, 'GOLD', 20, 'BALANCED', NOW()),
(2, 'ETF', 20, 'BALANCED', NOW()),
(2, 'STOCK', 35, 'BALANCED', NOW());

-- Insert Strategy Details for GROWTH (id = 3)
INSERT INTO mst_allocation_strategy_details (strategy_id, asset_type, percentage, strategy_code, created_at) VALUES
(3, 'EMERGENCY', 10, 'GROWTH', NOW()),
(3, 'GOLD', 10, 'GROWTH', NOW()),
(3, 'ETF', 30, 'GROWTH', NOW()),
(3, 'STOCK', 50, 'GROWTH', NOW());

-- Verify data
SELECT
  s.id,
  s.code,
  s.name,
  sd.asset_type,
  sd.percentage
FROM mst_allocation_strategies s
LEFT JOIN mst_allocation_strategy_details sd ON s.id = sd.strategy_id
ORDER BY s.code, sd.asset_type;

