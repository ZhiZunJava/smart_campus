SET NAMES utf8mb4;

-- ----------------------------
-- 问答体验增强升级脚本
-- 说明：
-- 1. 为问答会话补充摘要/计数/最近活跃字段
-- 2. 为问答消息补充内容类型、附件、模型名、思考过程等字段
-- 3. 为问答反馈补充评分与问题标签字段
-- ----------------------------

ALTER TABLE `sc_qa_session`
  ADD COLUMN `message_count` int DEFAULT 0 COMMENT '消息数量' AFTER `status`,
  ADD COLUMN `last_message_preview` varchar(255) DEFAULT NULL COMMENT '最后一条消息摘要' AFTER `message_count`,
  ADD COLUMN `last_message_time` datetime DEFAULT NULL COMMENT '最后消息时间' AFTER `last_message_preview`,
  ADD COLUMN `context_summary` varchar(1000) DEFAULT NULL COMMENT '会话上下文摘要' AFTER `last_message_time`;

ALTER TABLE `sc_qa_session`
  ADD KEY `idx_sc_qa_session_last_message_time` (`last_message_time`);

ALTER TABLE `sc_qa_message`
  ADD COLUMN `content_type` varchar(20) DEFAULT 'text' COMMENT '内容类型(text/markdown/image/mixed)' AFTER `role_type`,
  ADD COLUMN `attachment_json` longtext COMMENT '附件元数据JSON' AFTER `reference_source`,
  ADD COLUMN `reply_to_message_id` bigint DEFAULT NULL COMMENT '回复的消息ID' AFTER `attachment_json`,
  ADD COLUMN `model_name` varchar(100) DEFAULT NULL COMMENT '回答使用模型名' AFTER `reply_to_message_id`,
  ADD COLUMN `reasoning_content` longtext COMMENT '思考过程内容' AFTER `model_name`;

ALTER TABLE `sc_qa_message`
  ADD KEY `idx_sc_qa_message_reply_to` (`reply_to_message_id`);

ALTER TABLE `sc_qa_feedback`
  ADD COLUMN `feedback_score` tinyint DEFAULT NULL COMMENT '评分(1-5)' AFTER `feedback_type`,
  ADD COLUMN `issue_tags` varchar(255) DEFAULT NULL COMMENT '问题标签' AFTER `feedback_score`;

CREATE TABLE IF NOT EXISTS `sc_qa_attachment` (
  `attachment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `session_id` bigint DEFAULT NULL COMMENT '会话ID',
  `message_id` bigint DEFAULT NULL COMMENT '消息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `file_name` varchar(255) NOT NULL COMMENT '系统文件名',
  `original_name` varchar(255) DEFAULT NULL COMMENT '原始文件名',
  `file_url` varchar(500) NOT NULL COMMENT '文件访问地址',
  `mime_type` varchar(100) DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint DEFAULT 0 COMMENT '文件大小',
  `storage_type` varchar(20) DEFAULT 'local' COMMENT '存储类型(local/oss)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1删除)',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`attachment_id`),
  KEY `idx_sc_qa_attachment_session_id` (`session_id`),
  KEY `idx_sc_qa_attachment_message_id` (`message_id`),
  KEY `idx_sc_qa_attachment_user_id` (`user_id`),
  KEY `idx_sc_qa_attachment_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-问答附件表';
