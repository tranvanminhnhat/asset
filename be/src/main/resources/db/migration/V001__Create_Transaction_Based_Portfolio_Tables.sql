-- ============================================================
-- Migration: V001__Create_Transaction_Based_Portfolio_Tables
-- Description: Create new tables for transaction-based portfolio system
-- ============================================================

-- Create portfolio_transaction table (append-only)
CREATE TABLE IF NOT EXISTS PORTFOLIO_TRANSACTION (
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    PORTFOLIO_ID BIGINT NOT NULL,
    USER_ID VARCHAR(36) NOT NULL,
    TRANSACTION_TYPE VARCHAR(50) NOT NULL COMMENT 'BUY, SELL, DIVIDEND, ADJUSTMENT',
    ASSET_TYPE VARCHAR(100) NOT NULL,
    QUANTITY DECIMAL(18, 8) NOT NULL,
    UNIT_PRICE DECIMAL(18, 2) NOT NULL,
    TOTAL_AMOUNT DECIMAL(18, 2) NOT NULL,
    TRANSACTION_DATE TIMESTAMP NOT NULL,
    NOTES VARCHAR(500),
    REFERENCE_ID VARCHAR(50),
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Indexes for performance
    INDEX idx_portfolio_id (PORTFOLIO_ID),
    INDEX idx_user_id (USER_ID),
    INDEX idx_transaction_date (TRANSACTION_DATE),

    -- Foreign key constraint
    CONSTRAINT fk_portfolio_transaction_portfolio FOREIGN KEY (PORTFOLIO_ID)
        REFERENCES PORTFOLIO(ID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Append-only transaction log for portfolio audit trail';

-- Create asset_price table (historical prices)
CREATE TABLE IF NOT EXISTS ASSET_PRICE (
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ASSET_TYPE VARCHAR(100) NOT NULL,
    PRICE DECIMAL(18, 4) NOT NULL,
    PRICE_DATE TIMESTAMP NOT NULL,
    CURRENCY VARCHAR(10),
    SOURCE VARCHAR(100),
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Indexes for performance
    UNIQUE INDEX idx_asset_type_date (ASSET_TYPE, PRICE_DATE),
    INDEX idx_asset_type (ASSET_TYPE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Historical asset price snapshots for PnL calculation';

-- ============================================================
-- Create allocation strategy tables (Future Enhancement)
-- ============================================================

CREATE TABLE IF NOT EXISTS ALLOCATION_STRATEGY (
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    CODE VARCHAR(50) NOT NULL UNIQUE,
    NAME VARCHAR(100) NOT NULL,
    DESCRIPTION VARCHAR(500),
    STATUS VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_code (CODE),
    INDEX idx_status (STATUS)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Investment allocation strategy master';

CREATE TABLE IF NOT EXISTS ALLOCATION_STRATEGY_DETAIL (
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    STRATEGY_ID BIGINT NOT NULL,
    ASSET_TYPE VARCHAR(100) NOT NULL,
    ALLOCATION_PERCENT DECIMAL(5, 2) NOT NULL,
    DISPLAY_ORDER INT,
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_strategy_id (STRATEGY_ID),
    CONSTRAINT fk_allocation_detail_strategy FOREIGN KEY (STRATEGY_ID)
        REFERENCES ALLOCATION_STRATEGY(ID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Allocation percentages per asset type';

-- ============================================================
-- Add foreign key constraint to PORTFOLIO table
-- ============================================================

ALTER TABLE PORTFOLIO
    ADD CONSTRAINT fk_portfolio_user FOREIGN KEY (USER_ID)
        REFERENCES USER(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE;

-- ============================================================
-- Add foreign key constraint to investment_plan table
-- ============================================================

ALTER TABLE investment_plan
    ADD CONSTRAINT fk_investment_plan_user FOREIGN KEY (user_id)
        REFERENCES USER(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE;

