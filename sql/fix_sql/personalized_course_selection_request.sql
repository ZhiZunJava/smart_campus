SET NAMES utf8mb4;

-- ----------------------------
-- 个性化选课申请功能升级脚本
-- ----------------------------

CREATE TABLE IF NOT EXISTS `sc_course_selection_request` (
  `request_id`              bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `request_no`              varchar(64) DEFAULT NULL COMMENT '申请编号',
  `student_user_id`         bigint NOT NULL COMMENT '学生用户ID',
  `target_class_course_id`  bigint NOT NULL COMMENT '目标教学班ID',
  `request_type`            varchar(32) DEFAULT 'PERSONALIZED' COMMENT '申请类型',
  `request_reason`          varchar(1000) DEFAULT NULL COMMENT '申请理由',
  `request_status`          char(1) DEFAULT '0' COMMENT '状态(0待审 1通过 2驳回 3撤回)',
  `review_remark`           varchar(500) DEFAULT NULL COMMENT '审核说明',
  `review_user_id`          bigint DEFAULT NULL COMMENT '审核人ID',
  `review_time`             datetime DEFAULT NULL COMMENT '审核时间',
  `cancel_time`             datetime DEFAULT NULL COMMENT '撤回时间',
  `create_by`               varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`             datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`               varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`             datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`                  varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`request_id`),
  UNIQUE KEY `uk_course_selection_request_no` (`request_no`),
  KEY `idx_course_selection_request_student` (`student_user_id`),
  KEY `idx_course_selection_request_class_course` (`target_class_course_id`),
  KEY `idx_course_selection_request_status` (`request_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个性化选课申请表';
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache,
  menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT 2047, '个性化选课', '2003', '14', 'courseSelectionRequest', 'campus/courseSelectionRequest/index', '', '', 1, 0,
       'C', '0', '0', 'campus:courseSelectionRequest:list', 'ri-file-edit-line', 'admin', NOW(), '', NULL, '个性化选课申请审核'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2047);

-- 3. 补齐教室管理与节次布局按钮权限
INSERT INTO sys_menu VALUES
(2237, '申请查询', '2047', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseSelectionRequest:query', '#', 'admin', NOW(), '', NULL, ''),
(2238, '申请审核', '2047', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseSelectionRequest:review', '#', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE
parent_id = VALUES(parent_id),
order_num = VALUES(order_num),
perms = VALUES(perms),
menu_name = VALUES(menu_name),
update_by = 'admin',
update_time = NOW(),
remark = VALUES(remark);