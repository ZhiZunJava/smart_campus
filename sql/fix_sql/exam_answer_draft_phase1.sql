-- 考试草稿第一阶段
-- 目标：
-- 1. 支持按题自动保存草稿
-- 2. 支持按考试记录读取当前草稿
-- 3. 为后续分题作答与子卷切换提供基础
-- 执行前请先备份数据库

CREATE TABLE IF NOT EXISTS `sc_exam_answer_draft` (
  `draft_id` bigint NOT NULL AUTO_INCREMENT COMMENT '草稿ID',
  `record_id` bigint NOT NULL COMMENT '考试记录ID',
  `session_id` bigint DEFAULT NULL COMMENT '考试会话ID',
  `session_paper_id` bigint DEFAULT NULL COMMENT '子试卷记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_id` bigint NOT NULL COMMENT '学生用户ID',
  `user_answer` longtext COMMENT '草稿答案',
  `save_count` int DEFAULT 1 COMMENT '保存次数',
  `last_save_time` datetime DEFAULT NULL COMMENT '最近保存时间',
  `status` char(1) DEFAULT '0' COMMENT '状态(0有效 1失效)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`draft_id`),
  UNIQUE KEY `uk_exam_answer_draft_unique` (`record_id`, `question_id`),
  KEY `idx_exam_answer_draft_session` (`session_id`),
  KEY `idx_exam_answer_draft_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-考试答案草稿表';
