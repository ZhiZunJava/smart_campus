ALTER TABLE sys_notice
  ADD COLUMN IF NOT EXISTS biz_category VARCHAR(20) NOT NULL DEFAULT 'NOTICE' COMMENT '业务分类(TODO/MESSAGE/NOTICE)' AFTER status,
  ADD COLUMN IF NOT EXISTS notice_summary VARCHAR(255) DEFAULT NULL COMMENT '摘要' AFTER biz_category,
  ADD COLUMN IF NOT EXISTS receiver_scope VARCHAR(20) NOT NULL DEFAULT 'ALL' COMMENT '接收范围(ALL/STUDENT/TEACHER/PARENT/CUSTOM)' AFTER notice_summary,
  ADD COLUMN IF NOT EXISTS receiver_user_ids VARCHAR(1000) DEFAULT NULL COMMENT '指定接收用户ID，英文逗号分隔' AFTER receiver_scope,
  ADD COLUMN IF NOT EXISTS action_type VARCHAR(30) DEFAULT NULL COMMENT '动作类型' AFTER receiver_user_ids,
  ADD COLUMN IF NOT EXISTS action_path VARCHAR(255) DEFAULT NULL COMMENT '动作路径' AFTER action_type,
  ADD COLUMN IF NOT EXISTS action_target_id BIGINT DEFAULT NULL COMMENT '动作目标ID' AFTER action_path,
  ADD COLUMN IF NOT EXISTS pinned CHAR(1) NOT NULL DEFAULT '0' COMMENT '是否置顶(0否1是)' AFTER action_target_id,
  ADD COLUMN IF NOT EXISTS publish_time DATETIME DEFAULT NULL COMMENT '发布时间' AFTER pinned,
  ADD COLUMN IF NOT EXISTS expire_time DATETIME DEFAULT NULL COMMENT '失效时间' AFTER publish_time;

UPDATE sys_notice
SET biz_category = 'NOTICE',
    receiver_scope = 'ALL',
    pinned = '0',
    publish_time = IFNULL(publish_time, create_time),
    notice_summary = IFNULL(notice_summary, notice_title)
WHERE 1 = 1;

CREATE TABLE IF NOT EXISTS sys_notice_read (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  notice_id BIGINT NOT NULL COMMENT '通知ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  read_time DATETIME NOT NULL COMMENT '已读时间',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_notice_user (notice_id, user_id),
  KEY idx_notice_read_user (user_id, read_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告已读记录表';
