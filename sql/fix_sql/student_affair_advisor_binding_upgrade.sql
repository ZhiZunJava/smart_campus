SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE `sc_user_profile`
  ADD COLUMN IF NOT EXISTS `advisor_user_id` bigint DEFAULT NULL COMMENT '绑定辅导员用户ID' AFTER `class_id`;

SET FOREIGN_KEY_CHECKS = 1;
