ALTER TABLE `sc_ai_model_config`
  ADD COLUMN `model_code` varchar(100) DEFAULT NULL COMMENT '实际调用模型编码' AFTER `biz_types`,
  ADD COLUMN `reasoning_model_code` varchar(100) DEFAULT NULL COMMENT '深度思考模型编码' AFTER `model_code`,
  ADD COLUMN `vision_model_code` varchar(100) DEFAULT NULL COMMENT '视觉模型编码' AFTER `reasoning_model_code`,
  ADD COLUMN `support_stream` char(1) DEFAULT '1' COMMENT '是否支持流式(0否 1是)' AFTER `vision_model_code`,
  ADD COLUMN `support_reasoning` char(1) DEFAULT '0' COMMENT '是否支持深度思考(0否 1是)' AFTER `support_stream`,
  ADD COLUMN `support_vision` char(1) DEFAULT '0' COMMENT '是否支持图片理解(0否 1是)' AFTER `support_reasoning`;

UPDATE `sc_ai_model_config`
SET `model_code` = COALESCE(`model_code`, `model_name`),
    `support_stream` = COALESCE(`support_stream`, '1'),
    `support_reasoning` = COALESCE(`support_reasoning`, '0'),
    `support_vision` = COALESCE(`support_vision`, '0');
