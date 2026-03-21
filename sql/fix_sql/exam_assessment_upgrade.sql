-- 智能评测增强升级脚本
-- 执行前请先备份数据库

ALTER TABLE `sc_question_bank`
  ADD COLUMN `question_tags` varchar(255) DEFAULT NULL COMMENT '题目标签，逗号分隔' AFTER `source`,
  ADD COLUMN `material_content` longtext COMMENT '材料题/案例题材料内容' AFTER `question_tags`,
  ADD COLUMN `source_batch_no` varchar(64) DEFAULT NULL COMMENT '来源批次号' AFTER `material_content`;

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

DROP TABLE IF EXISTS `sc_exam_strategy_template`;
CREATE TABLE `sc_exam_strategy_template` (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `course_id` bigint DEFAULT NULL COMMENT '课程ID',
  `template_type` varchar(20) DEFAULT 'paper_rule' COMMENT '模板类型',
  `rule_json` longtext COMMENT '组卷规则JSON',
  `publish_status` varchar(20) DEFAULT 'draft' COMMENT '状态(draft/published/archived)',
  `status` char(1) DEFAULT '0' COMMENT '系统状态(0正常 1停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`template_id`),
  KEY `idx_exam_strategy_template_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-组卷策略模板表';

CREATE INDEX `idx_sc_question_source_batch_no` ON `sc_question_bank` (`source_batch_no`);
CREATE INDEX `idx_sc_exam_paper_publish_status` ON `sc_exam_paper` (`publish_status`);
