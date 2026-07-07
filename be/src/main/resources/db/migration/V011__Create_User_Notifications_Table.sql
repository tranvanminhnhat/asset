CREATE TABLE IF NOT EXISTS `user_notifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(36) NOT NULL,
  `type` VARCHAR(50) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `message` VARCHAR(1000) NOT NULL,
  `mode` VARCHAR(20) NOT NULL,
  `target_flow` VARCHAR(50) NOT NULL,
  `payload` JSON NULL,
  `status` VARCHAR(20) NOT NULL,
  `due_date` TIMESTAMP NULL,
  `read_at` TIMESTAMP NULL,
  `actioned_at` TIMESTAMP NULL,
  `done_at` TIMESTAMP NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_notification_user` (`user_id`),
  INDEX `idx_notification_status` (`status`),
  INDEX `idx_notification_due_date` (`due_date`),
  CONSTRAINT `fk_user_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

