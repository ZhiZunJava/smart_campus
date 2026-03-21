-- 分层考试执行第一阶段
-- 目标：
-- 1. 引入考试会话主表，作为主试卷作答入口
-- 2. 引入子试卷作答记录表，记录主卷下各子卷执行情况
-- 3. 扩展作答明细与旧考试记录，打通新旧模型兼容
-- 执行前请先备份数据库

CREATE TABLE IF NOT EXISTS `sc_exam_session` (
  `session_id` bigint NOT NULL AUTO_INCREMENT COMMENT '考试会话ID',
  `root_paper_id` bigint NOT NULL COMMENT '主试卷ID',
  `user_id` bigint NOT NULL COMMENT '学生用户ID',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `session_status` varchar(20) DEFAULT 'ONGOING' COMMENT '会话状态(ONGOING/SUBMITTED/EXPIRED)',
  `total_score` decimal(8,2) DEFAULT 0.00 COMMENT '总得分',
  `correct_rate` decimal(8,2) DEFAULT 0.00 COMMENT '总正确率',
  `focus_loss_count` int DEFAULT 0 COMMENT '切屏次数',
  `flagged_count` int DEFAULT 0 COMMENT '标记异常次数',
  `analysis_json` longtext COMMENT '统计分析JSON',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`session_id`),
  KEY `idx_exam_session_root_user` (`root_paper_id`, `user_id`),
  KEY `idx_exam_session_status` (`session_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-考试会话表';

CREATE TABLE IF NOT EXISTS `sc_exam_session_paper` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `session_id` bigint NOT NULL COMMENT '考试会话ID',
  `paper_id` bigint NOT NULL COMMENT '试卷ID',
  `parent_paper_id` bigint DEFAULT NULL COMMENT '父试卷ID',
  `paper_level` varchar(16) DEFAULT 'MAIN' COMMENT '试卷层级(MAIN/SUB)',
  `answer_mode` varchar(16) DEFAULT 'REQUIRED' COMMENT '作答模式(REQUIRED/OPTIONAL)',
  `paper_weight` decimal(8,2) DEFAULT 100.00 COMMENT '子试卷权重',
  `sort_no` int DEFAULT 0 COMMENT '排序号',
  `paper_status` varchar(20) DEFAULT 'PENDING' COMMENT '子试卷状态(PENDING/ONGOING/SUBMITTED/SKIPPED)',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `score` decimal(8,2) DEFAULT 0.00 COMMENT '得分',
  `correct_rate` decimal(8,2) DEFAULT 0.00 COMMENT '正确率',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_session_paper` (`session_id`, `paper_id`),
  KEY `idx_exam_session_paper_session` (`session_id`),
  KEY `idx_exam_session_paper_parent` (`parent_paper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-考试会话子试卷表';

CREATE TABLE IF NOT EXISTS `sc_exam_behavior_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `session_id` bigint NOT NULL COMMENT '考试会话ID',
  `record_id` bigint DEFAULT NULL COMMENT '旧考试记录ID',
  `behavior_type` varchar(32) NOT NULL COMMENT '行为类型(FOCUS_LOSS/FLAGGED/MANUAL_SUBMIT/AUTO_SAVE)',
  `behavior_count` int DEFAULT 1 COMMENT '次数',
  `behavior_time` datetime DEFAULT NULL COMMENT '行为时间',
  `behavior_data` longtext COMMENT '行为明细JSON',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_exam_behavior_session` (`session_id`),
  KEY `idx_exam_behavior_type` (`behavior_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-考试行为日志表';

ALTER TABLE `sc_exam_record`
  ADD COLUMN `session_id` bigint DEFAULT NULL COMMENT '考试会话ID' AFTER `record_id`;

ALTER TABLE `sc_exam_answer`
  ADD COLUMN `session_id` bigint DEFAULT NULL COMMENT '考试会话ID' AFTER `answer_id`,
  ADD COLUMN `session_paper_id` bigint DEFAULT NULL COMMENT '子试卷记录ID' AFTER `session_id`;

CREATE INDEX `idx_sc_exam_record_session` ON `sc_exam_record` (`session_id`);
CREATE INDEX `idx_sc_exam_answer_session` ON `sc_exam_answer` (`session_id`);
CREATE INDEX `idx_sc_exam_answer_session_paper` ON `sc_exam_answer` (`session_paper_id`);
