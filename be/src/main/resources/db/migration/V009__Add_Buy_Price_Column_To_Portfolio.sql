-- Flyway Migration: V009__Add_Buy_Price_Column_To_Portfolio.sql
-- Add buy_price column to portfolio table to store original purchase price per unit
-- This is used for profit/loss calculation on dashboard

ALTER TABLE `portfolio`
ADD COLUMN `buy_price` DECIMAL(19,6) NULL DEFAULT NULL COMMENT 'Average buy price per unit - used for calculating original invested cost' AFTER `total_invested`;

