-- 学生成绩系统升级脚本
-- 1. 新增课程成绩台账表
-- 2. 新增后台菜单：课程成绩

CREATE TABLE IF NOT EXISTS `sc_student_score` (
  `score_id` bigint NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `class_course_id` bigint NOT NULL COMMENT '班级课程ID',
  `term_id` bigint DEFAULT NULL COMMENT '学期ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `class_id` bigint DEFAULT NULL COMMENT '班级ID',
  `student_user_id` bigint NOT NULL COMMENT '学生用户ID',
  `usual_score` decimal(6,2) DEFAULT 0.00 COMMENT '平时分',
  `attendance_score` decimal(6,2) DEFAULT 0.00 COMMENT '考勤分',
  `homework_score` decimal(6,2) DEFAULT 0.00 COMMENT '作业分',
  `lab_score` decimal(6,2) DEFAULT 0.00 COMMENT '实验分',
  `exam_score` decimal(6,2) DEFAULT 0.00 COMMENT '考试分',
  `exam_avg_score` decimal(6,2) DEFAULT 0.00 COMMENT '考试同步均分',
  `bonus_score` decimal(6,2) DEFAULT 0.00 COMMENT '加分',
  `penalty_score` decimal(6,2) DEFAULT 0.00 COMMENT '扣分',
  `usual_weight` decimal(5,2) DEFAULT 20.00 COMMENT '平时权重',
  `attendance_weight` decimal(5,2) DEFAULT 10.00 COMMENT '考勤权重',
  `homework_weight` decimal(5,2) DEFAULT 20.00 COMMENT '作业权重',
  `lab_weight` decimal(5,2) DEFAULT 10.00 COMMENT '实验权重',
  `exam_weight` decimal(5,2) DEFAULT 40.00 COMMENT '考试权重',
  `total_score` decimal(6,2) DEFAULT 0.00 COMMENT '总评成绩',
  `grade_point` decimal(4,2) DEFAULT 0.00 COMMENT '绩点',
  `grade_level` varchar(20) DEFAULT NULL COMMENT '等级',
  `pass_flag` char(1) DEFAULT '0' COMMENT '是否通过(0否 1是)',
  `rank_no` int DEFAULT NULL COMMENT '班级课程排名',
  `percentile` decimal(5,2) DEFAULT NULL COMMENT '百分位',
  `exam_record_count` int DEFAULT 0 COMMENT '考试记录数',
  `sync_time` datetime DEFAULT NULL COMMENT '最近同步时间',
  `publish_status` char(1) DEFAULT '0' COMMENT '发布状态(0草稿 1已发布)',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `publish_by` varchar(64) DEFAULT '' COMMENT '发布人',
  `teacher_comment` varchar(1000) DEFAULT NULL COMMENT '教师评语',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`score_id`),
  UNIQUE KEY `uk_sc_student_score_class_course_student` (`class_course_id`, `student_user_id`),
  KEY `idx_sc_student_score_term_course` (`term_id`, `course_id`),
  KEY `idx_sc_student_score_student` (`student_user_id`),
  KEY `idx_sc_student_score_publish` (`publish_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-学生课程成绩台账';

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
  `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`,
  `update_time`, `remark`)
SELECT 2067, '课程成绩', '2006', '6', 'studentScore', 'campus/score/index', '', '', 1, 0, 'C', '0', '0',
  'campus:score:list', 'ri-award-line', 'admin', NOW(), '', NULL, '课程成绩管理'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 2067);

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
  `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`,
  `update_time`, `remark`)
SELECT 2265, '成绩查询', '2067', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:score:query', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 2265);

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
  `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`,
  `update_time`, `remark`)
SELECT 2267, '成绩修改', '2067', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:score:edit', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 2267);

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
  `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`,
  `update_time`, `remark`)
SELECT 2268, '成绩删除', '2067', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:score:remove', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 2268);

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
  `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`,
  `update_time`, `remark`)
SELECT 2269, '成绩发布', '2067', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:score:edit', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 2269);
