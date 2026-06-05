-- Migration: Update token column length to support JWT refresh tokens
-- Refresh tokens are long (1000+ characters), so we need to increase the column length

ALTER TABLE `user` MODIFY COLUMN `token` VARCHAR(2000) NULL DEFAULT NULL;

