-- 智能评测-试卷扩展字段补丁
-- 适用于数据库还未补齐 sc_exam_paper 扩展字段时执行
-- 执行前请先备份数据库

ALTER TABLE `sc_exam_paper`
  ADD COLUMN `publish_status` varchar(20) DEFAULT 'draft' COMMENT '发布状态(draft/published/archived)' AFTER `duration_minutes`,
  ADD COLUMN `publish_start_time` datetime DEFAULT NULL COMMENT '发布时间起' AFTER `publish_status`,
  ADD COLUMN `publish_end_time` datetime DEFAULT NULL COMMENT '发布时间止' AFTER `publish_start_time`,
  ADD COLUMN `strategy_template_id` bigint DEFAULT NULL COMMENT '组卷策略模板ID' AFTER `publish_end_time`,
  ADD COLUMN `pass_score` decimal(8,2) DEFAULT 60.00 COMMENT '及格线' AFTER `strategy_template_id`,
  ADD COLUMN `marking_mode` varchar(20) DEFAULT 'auto_first' COMMENT '阅卷方式' AFTER `pass_score`,
  ADD COLUMN `question_order_mode` varchar(20) DEFAULT 'manual' COMMENT '题目顺序模式' AFTER `marking_mode`,
  ADD COLUMN `allow_review_analysis` char(1) DEFAULT '1' COMMENT '是否允许回看解析(0否 1是)' AFTER `question_order_mode`,
  ADD COLUMN `section_config_json` longtext COMMENT '题型区块配置JSON' AFTER `allow_review_analysis`;
