ALTER TABLE `sc_ai_model_config`
  ADD COLUMN `is_default` char(1) DEFAULT '0' COMMENT '是否默认模型(0否 1是)' AFTER `status`,
  ADD COLUMN `priority` int DEFAULT 0 COMMENT '优先级，越大越优先' AFTER `is_default`,
  ADD COLUMN `biz_types` varchar(255) DEFAULT NULL COMMENT '适用业务类型，逗号分隔，如 qa,report,recommend,common' AFTER `priority`;

UPDATE `sc_ai_model_config`
SET `is_default` = '0', `priority` = COALESCE(`priority`, 0)
WHERE `is_default` IS NULL OR `priority` IS NULL;
