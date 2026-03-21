-- 分层试卷第一阶段
-- 目标：
-- 1. 让 sc_exam_paper 支持主试卷 / 子试卷层级结构
-- 2. 支持子试卷权重、顺序、必答/选答、自适应规则挂载
-- 执行前请先备份数据库

ALTER TABLE `sc_exam_paper`
  ADD COLUMN `parent_paper_id` bigint DEFAULT NULL COMMENT '父试卷ID，主试卷为空' AFTER `paper_id`,
  ADD COLUMN `paper_level` varchar(16) DEFAULT 'MAIN' COMMENT '试卷层级(MAIN/SUB)' AFTER `paper_type`,
  ADD COLUMN `answer_mode` varchar(16) DEFAULT 'REQUIRED' COMMENT '作答模式(REQUIRED/OPTIONAL)' AFTER `paper_level`,
  ADD COLUMN `paper_weight` decimal(8,2) DEFAULT 100.00 COMMENT '子试卷权重' AFTER `answer_mode`,
  ADD COLUMN `sort_no` int DEFAULT 0 COMMENT '同层排序号' AFTER `paper_weight`,
  ADD COLUMN `adaptive_rule_json` longtext COMMENT '自适应规则JSON' AFTER `section_config_json`;

CREATE INDEX `idx_sc_exam_paper_parent` ON `sc_exam_paper` (`parent_paper_id`);
CREATE INDEX `idx_sc_exam_paper_level` ON `sc_exam_paper` (`paper_level`);
