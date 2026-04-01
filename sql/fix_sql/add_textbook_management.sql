-- =============================================
-- 教材管理模块 DDL + 菜单权限
-- =============================================

SET FOREIGN_KEY_CHECKS = 0;

-- -------------------------------------------
-- 1. sc_textbook 教材目录表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `sc_textbook` (
  `textbook_id`   bigint       NOT NULL AUTO_INCREMENT COMMENT '教材ID',
  `textbook_code` varchar(64)  NOT NULL DEFAULT '' COMMENT '教材编码',
  `textbook_name` varchar(200) NOT NULL COMMENT '教材名称',
  `isbn`          varchar(32)  DEFAULT '' COMMENT 'ISBN',
  `author`        varchar(128) DEFAULT '' COMMENT '作者',
  `publisher`     varchar(128) DEFAULT '' COMMENT '出版社',
  `edition`       varchar(64)  DEFAULT '' COMMENT '版次（如第3版）',
  `publish_date`  varchar(32)  DEFAULT '' COMMENT '出版年月',
  `price`         decimal(10,2) DEFAULT NULL COMMENT '单价（元）',
  `cover_url`     varchar(500) DEFAULT '' COMMENT '封面图片URL',
  `category`      varchar(64)  DEFAULT '' COMMENT '教材类别（必修/选修/参考书）',
  `subject_type`  varchar(64)  DEFAULT '' COMMENT '学科分类',
  `status`        char(1)      DEFAULT '0' COMMENT '状态：0正常 1停用',
  `create_by`     varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
  `update_by`     varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`   datetime     DEFAULT NULL COMMENT '更新时间',
  `remark`        varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`textbook_id`),
  UNIQUE KEY `uk_textbook_code` (`textbook_code`),
  INDEX `idx_isbn` (`isbn`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教材目录表';

-- -------------------------------------------
-- 2. sc_textbook_plan 教材计划/分配表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `sc_textbook_plan` (
  `plan_id`              bigint      NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `term_id`              bigint      NOT NULL COMMENT '学期ID',
  `textbook_id`          bigint      NOT NULL COMMENT '教材ID',
  `course_id`            bigint      DEFAULT NULL COMMENT '课程ID（可选）',
  `grade_id`             bigint      DEFAULT NULL COMMENT '年级ID（可选）',
  `class_id`             bigint      DEFAULT NULL COMMENT '班级ID（可选）',
  `plan_quantity`        int         DEFAULT 0 COMMENT '计划数量',
  `distributed_quantity` int         DEFAULT 0 COMMENT '已发放数量',
  `plan_status`          varchar(16) DEFAULT 'DRAFT' COMMENT '状态：DRAFT/PUBLISHED/COMPLETED',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime    DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime    DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`plan_id`),
  INDEX `idx_term_textbook` (`term_id`, `textbook_id`),
  INDEX `idx_course` (`course_id`),
  INDEX `idx_grade_class` (`grade_id`, `class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教材计划表';

-- -------------------------------------------
-- 3. sys_menu 菜单权限（挂在学生事务 2300 下）
-- -------------------------------------------
-- 教材管理目录
INSERT INTO `sys_menu` (`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`)
VALUES
  (2340, '教材管理', 2300, 5, 'textbook', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-book-open-line', 'admin', NOW(), '', NULL, '教材管理目录'),
  (2341, '教材目录', 2340, 1, 'index', 'campus/affair/textbook/index', '', '', 1, 0, 'C', '0', '0', 'campus:textbook:list', 'ri-book-2-line', 'admin', NOW(), '', NULL, '教材目录管理'),
  (2342, '教材计划', 2340, 2, 'plan', 'campus/affair/textbook/plan', '', '', 1, 0, 'C', '0', '0', 'campus:textbookPlan:list', 'ri-calendar-todo-line', 'admin', NOW(), '', NULL, '教材计划管理'),
  -- 教材目录按钮权限
  (2343, '教材查询', 2341, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:textbook:query', '#', 'admin', NOW(), '', NULL, ''),
  (2344, '教材新增', 2341, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:textbook:add', '#', 'admin', NOW(), '', NULL, ''),
  (2345, '教材修改', 2341, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:textbook:edit', '#', 'admin', NOW(), '', NULL, ''),
  (2346, '教材删除', 2341, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:textbook:remove', '#', 'admin', NOW(), '', NULL, ''),
  -- 教材计划按钮权限
  (2347, '计划查询', 2342, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:textbookPlan:query', '#', 'admin', NOW(), '', NULL, ''),
  (2348, '计划新增', 2342, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:textbookPlan:add', '#', 'admin', NOW(), '', NULL, ''),
  (2349, '计划修改', 2342, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:textbookPlan:edit', '#', 'admin', NOW(), '', NULL, ''),
  (2350, '计划删除', 2342, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:textbookPlan:remove', '#', 'admin', NOW(), '', NULL, '');

-- -------------------------------------------
-- 4. 示例数据
-- -------------------------------------------
INSERT INTO `sc_textbook` (`textbook_code`, `textbook_name`, `isbn`, `author`, `publisher`, `edition`, `publish_date`, `price`, `category`, `subject_type`, `status`, `create_by`, `create_time`, `remark`)
VALUES
  ('TB20250001', '高等数学（第七版）上册', '978-7-04-039663-8', '同济大学数学系', '高等教育出版社', '第7版', '2014-07', 34.30, '必修', '数学', '0', 'admin', NOW(), ''),
  ('TB20250002', '大学英语综合教程1', '978-7-5446-3500-1', '郑树棠', '上海外语教育出版社', '第2版', '2017-09', 49.90, '必修', '外语', '0', 'admin', NOW(), ''),
  ('TB20250003', '计算机应用基础', '978-7-115-50234-5', '卢湘鸿', '人民邮电出版社', '第1版', '2020-01', 42.00, '必修', '计算机', '0', 'admin', NOW(), '');

SET FOREIGN_KEY_CHECKS = 1;
