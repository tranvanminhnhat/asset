-- Rename table allocation_strategies to mst_allocation_strategies
ALTER TABLE allocation_strategies RENAME TO mst_allocation_strategies;

-- Rename table allocation_strategy_details to mst_allocation_strategy_details
ALTER TABLE allocation_strategy_details RENAME TO mst_allocation_strategy_details;

-- Update index name if needed (MySQL handles this automatically)
-- No additional changes needed for the data

