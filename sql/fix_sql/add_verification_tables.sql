SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------------
-- 1. 学籍核对批次表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sc_verification_batch` (
  `batch_id`        bigint       NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `batch_name`      varchar(128) NOT NULL COMMENT '批次名称',
  `batch_no`        varchar(64)  NOT NULL COMMENT '批次编号',
  `description`     varchar(512) DEFAULT '' COMMENT '批次说明',
  `editable_fields` varchar(1024) NOT NULL COMMENT '可编辑字段JSON数组',
  `scope_type`      varchar(16)  NOT NULL DEFAULT 'ALL' COMMENT '范围类型（ALL/GRADE/CLASS）',
  `scope_grade_id`  bigint       DEFAULT NULL COMMENT '范围年级ID',
  `scope_class_id`  bigint       DEFAULT NULL COMMENT '范围班级ID',
  `batch_status`    varchar(16)  NOT NULL DEFAULT 'DRAFT' COMMENT '批次状态（DRAFT/ACTIVE/CLOSED）',
  `start_time`      datetime     DEFAULT NULL COMMENT '开始时间',
  `end_time`        datetime     DEFAULT NULL COMMENT '截止时间',
  `create_by`       varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`     datetime     DEFAULT NULL COMMENT '创建时间',
  `update_by`       varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`     datetime     DEFAULT NULL COMMENT '更新时间',
  `remark`          varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`batch_id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`),
  KEY `idx_batch_status` (`batch_status`),
  KEY `idx_scope` (`scope_type`, `scope_grade_id`, `scope_class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学籍核对批次表';

-- -----------------------------------------------------------
-- 2. 学生核对记录表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sc_verification_record` (
  `record_id`        bigint       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `batch_id`         bigint       NOT NULL COMMENT '批次ID',
  `student_user_id`  bigint       NOT NULL COMMENT '学生用户ID',
  `student_no`       varchar(32)  DEFAULT '' COMMENT '学号',
  `student_name`     varchar(32)  DEFAULT '' COMMENT '学生姓名',
  `record_status`    varchar(16)  NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/CONFIRMED/MODIFIED/UNDER_REVIEW/APPROVED/REJECTED）',
  `snapshot_json`    text         DEFAULT NULL COMMENT '提交时档案快照JSON',
  `submit_time`      datetime     DEFAULT NULL COMMENT '提交时间',
  `review_user_id`   bigint       DEFAULT NULL COMMENT '审核人ID',
  `review_time`      datetime     DEFAULT NULL COMMENT '审核时间',
  `review_comment`   varchar(512) DEFAULT '' COMMENT '审核备注',
  `create_by`        varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`      datetime     DEFAULT NULL COMMENT '创建时间',
  `update_by`        varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`      datetime     DEFAULT NULL COMMENT '更新时间',
  `remark`           varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_batch_student` (`batch_id`, `student_user_id`),
  KEY `idx_record_status` (`record_status`),
  KEY `idx_student_user_id` (`student_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学籍核对记录表';

-- -----------------------------------------------------------
-- 3. 学籍核对字段变更明细表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sc_verification_change` (
  `change_id`        bigint       NOT NULL AUTO_INCREMENT COMMENT '变更ID',
  `record_id`        bigint       NOT NULL COMMENT '记录ID',
  `batch_id`         bigint       NOT NULL COMMENT '批次ID',
  `student_user_id`  bigint       NOT NULL COMMENT '学生用户ID',
  `field_name`       varchar(64)  NOT NULL COMMENT '字段名',
  `field_label`      varchar(64)  DEFAULT '' COMMENT '字段中文名',
  `old_value`        varchar(512) DEFAULT '' COMMENT '原值',
  `new_value`        varchar(512) DEFAULT '' COMMENT '新值',
  `change_status`    varchar(16)  NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING/APPROVED/REJECTED）',
  `review_comment`   varchar(256) DEFAULT '' COMMENT '审核备注',
  `create_time`      datetime     DEFAULT NULL COMMENT '创建时间',
  `update_time`      datetime     DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`change_id`),
  KEY `idx_change_record` (`record_id`),
  KEY `idx_change_batch_student` (`batch_id`, `student_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学籍核对字段变更明细表';

-- -----------------------------------------------------------
-- 4. 菜单权限数据
-- -----------------------------------------------------------

-- 4.1 查找"校园管理"父菜单ID（假设已存在），动态获取
-- 为安全起见，使用固定的大ID范围避免冲突
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3080, '学籍核对', menu_id, 20, 'verification', NULL, '', '', 1, 0, 'M', '0', '0', '', 'document-checked', 'admin', sysdate(), '', NULL, '学籍核对管理'
FROM sys_menu WHERE menu_name = '智慧校园' AND menu_type = 'M' LIMIT 1;

-- 批次管理页
INSERT INTO `sys_menu` VALUES (3081, '核对批次管理', 3080, 1, 'batch', 'campus/verification/index', '', '', 1, 0, 'C', '0', '0', 'campus:verification:list', 'list', 'admin', sysdate(), '', NULL, '学籍核对批次管理');

-- 批次记录页
INSERT INTO `sys_menu` VALUES (3082, '核对记录查看', 3080, 2, 'records/:batchId', 'campus/verification/records', '', '', 1, 0, 'C', '1', '0', 'campus:verification:list', '#', 'admin', sysdate(), '', NULL, '学籍核对记录查看');

-- 功能权限按钮
INSERT INTO `sys_menu` VALUES (3083, '核对批次查询', 3081, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:verification:query',   '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (3084, '核对批次新增', 3081, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:verification:add',     '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (3085, '核对批次修改', 3081, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:verification:edit',    '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (3086, '核对批次删除', 3081, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:verification:remove',  '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (3087, '核对记录审核', 3081, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:verification:review',  '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (3088, '核对结果导出', 3081, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:verification:export',  '#', 'admin', sysdate(), '', NULL, '');

SET FOREIGN_KEY_CHECKS = 1;
