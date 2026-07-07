-- Add profile and auto-invest fields to user table
ALTER TABLE `user`
    ADD COLUMN IF NOT EXISTS `full_name` VARCHAR(255) NULL AFTER `email`,
    ADD COLUMN IF NOT EXISTS `avatar_url` VARCHAR(500) NULL AFTER `full_name`,
    ADD COLUMN IF NOT EXISTS `auto_invest_enabled` TINYINT(1) NOT NULL DEFAULT 0 AFTER `token`,
    ADD COLUMN IF NOT EXISTS `investment_style` VARCHAR(30) NULL AFTER `auto_invest_enabled`,
    ADD COLUMN IF NOT EXISTS `auto_invest_day_of_month` INT NULL AFTER `investment_style`;

