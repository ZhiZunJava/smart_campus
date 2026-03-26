SET NAMES utf8mb4;

-- ----------------------------
-- 选课计划功能升级脚本
-- ----------------------------

CREATE TABLE IF NOT EXISTS `sc_course_selection_plan` (
  `plan_id`                bigint NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `plan_name`              varchar(100) DEFAULT NULL COMMENT '计划名称',
  `term_id`                bigint NOT NULL COMMENT '学期ID',
  `plan_type`              varchar(32) DEFAULT 'GENERAL' COMMENT '计划类型',
  `selection_start_time`   datetime DEFAULT NULL COMMENT '选课开始时间',
  `selection_end_time`     datetime DEFAULT NULL COMMENT '选课结束时间',
  `drop_start_time`        datetime DEFAULT NULL COMMENT '退课开始时间',
  `drop_end_time`          datetime DEFAULT NULL COMMENT '退课结束时间',
  `request_start_time`     datetime DEFAULT NULL COMMENT '申请开始时间',
  `request_end_time`       datetime DEFAULT NULL COMMENT '申请结束时间',
  `notice_content`         varchar(1000) DEFAULT NULL COMMENT '公告内容',
  `rule_content`           varchar(1500) DEFAULT NULL COMMENT '规则说明',
  `status`                 char(1) DEFAULT '0' COMMENT '状态(0启用 1停用)',
  `create_by`              varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`            datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`              varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`            datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`                 varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`plan_id`),
  KEY `idx_course_selection_plan_term` (`term_id`),
  KEY `idx_course_selection_plan_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课计划表';
