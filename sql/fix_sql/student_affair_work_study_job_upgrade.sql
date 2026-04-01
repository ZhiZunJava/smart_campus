SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `sc_affair_work_study_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `job_code` varchar(64) DEFAULT '' COMMENT '岗位编码',
  `job_name` varchar(200) DEFAULT '' COMMENT '岗位名称',
  `dept_id` bigint DEFAULT NULL COMMENT '用工部门ID',
  `dept_name` varchar(200) DEFAULT '' COMMENT '用工部门',
  `term_id` bigint DEFAULT NULL COMMENT '学期ID',
  `term_name` varchar(120) DEFAULT '' COMMENT '学期名称',
  `salary_amount` decimal(10,2) DEFAULT NULL COMMENT '月薪/补贴',
  `salary_unit` varchar(32) DEFAULT 'MONTH' COMMENT '薪资单位',
  `work_place` varchar(200) DEFAULT '' COMMENT '工作地点',
  `weekly_hours` int DEFAULT 0 COMMENT '每周工时',
  `headcount` int DEFAULT 1 COMMENT '招聘人数',
  `applied_count` int DEFAULT 0 COMMENT '申请人数',
  `contact_person` varchar(100) DEFAULT '' COMMENT '联系人',
  `contact_phone` varchar(32) DEFAULT '' COMMENT '联系电话',
  `open_start_time` datetime DEFAULT NULL COMMENT '开放开始时间',
  `open_end_time` datetime DEFAULT NULL COMMENT '开放结束时间',
  `publish_status` char(1) DEFAULT '0' COMMENT '状态（0开放 1关闭）',
  `job_desc` text COMMENT '岗位说明',
  `job_requirements` text COMMENT '岗位要求',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`job_id`),
  UNIQUE KEY `uk_sc_affair_work_study_job_code` (`job_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务-勤工助学岗位';

INSERT INTO `sys_menu`
  (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT 2336, '勤工岗位', 2300, 5, 'workStudyJob', 'campus/affair/workStudyJob/index', '', '', 1, 0, 'C', '0', '0', 'campus:affairWorkStudyJob:list', 'ri-briefcase-4-line', 'admin', NOW(), '勤工助学岗位发布管理'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 2336);

INSERT INTO `sys_menu`
  (`menu_id`, `menu_name`, `parent_id`, `order_num`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT 2337, '岗位查询', 2336, 1, 'F', '0', '0', 'campus:affairWorkStudyJob:query', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 2337);

INSERT INTO `sys_menu`
  (`menu_id`, `menu_name`, `parent_id`, `order_num`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT 2338, '岗位新增', 2336, 2, 'F', '0', '0', 'campus:affairWorkStudyJob:add', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 2338);

INSERT INTO `sys_menu`
  (`menu_id`, `menu_name`, `parent_id`, `order_num`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT 2339, '岗位修改', 2336, 3, 'F', '0', '0', 'campus:affairWorkStudyJob:edit', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 2339);

INSERT INTO `sys_menu`
  (`menu_id`, `menu_name`, `parent_id`, `order_num`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT 2340, '岗位删除', 2336, 4, 'F', '0', '0', 'campus:affairWorkStudyJob:remove', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 2340);

SET FOREIGN_KEY_CHECKS = 1;
