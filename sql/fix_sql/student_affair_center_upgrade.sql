SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `sc_affair_category` (
  `category_id` bigint NOT NULL AUTO_INCREMENT COMMENT '事务分类ID',
  `category_code` varchar(64) NOT NULL COMMENT '事务分类编码',
  `category_name` varchar(100) NOT NULL COMMENT '事务分类名称',
  `service_group` varchar(64) DEFAULT '' COMMENT '服务分组',
  `icon` varchar(100) DEFAULT '#' COMMENT '图标',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `target_role_scope` varchar(100) DEFAULT 'student' COMMENT '适用角色范围',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uk_sc_affair_category_code` (`category_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务分类表';

CREATE TABLE IF NOT EXISTS `sc_affair_template` (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `category_id` bigint NOT NULL COMMENT '事务分类ID',
  `template_code` varchar(64) NOT NULL COMMENT '模板编码',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `business_code` varchar(64) DEFAULT '' COMMENT '业务编码',
  `business_name` varchar(100) DEFAULT '' COMMENT '业务名称',
  `audience_roles` varchar(100) DEFAULT 'student' COMMENT '可发起角色',
  `form_schema_json` json DEFAULT NULL COMMENT '表单Schema',
  `workflow_schema_json` json DEFAULT NULL COMMENT '流程Schema',
  `allow_attachment` char(1) DEFAULT '0' COMMENT '是否允许附件（0否 1是）',
  `allow_revoke` char(1) DEFAULT '1' COMMENT '是否允许撤回（0否 1是）',
  `target_status_code` varchar(64) DEFAULT '' COMMENT '目标学籍状态编码',
  `target_status_name` varchar(100) DEFAULT '' COMMENT '目标学籍状态名称',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `uk_sc_affair_template_code` (`template_code`),
  KEY `idx_sc_affair_template_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务模板表';

CREATE TABLE IF NOT EXISTS `sc_affair_request` (
  `request_id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `request_no` varchar(64) NOT NULL COMMENT '申请编号',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `category_code` varchar(64) DEFAULT '' COMMENT '分类编码',
  `category_name` varchar(100) DEFAULT '' COMMENT '分类名称',
  `template_id` bigint DEFAULT NULL COMMENT '模板ID',
  `template_code` varchar(64) DEFAULT '' COMMENT '模板编码',
  `template_name` varchar(100) DEFAULT '' COMMENT '模板名称',
  `business_code` varchar(64) DEFAULT '' COMMENT '业务编码',
  `business_name` varchar(100) DEFAULT '' COMMENT '业务名称',
  `applicant_user_id` bigint DEFAULT NULL COMMENT '发起人用户ID',
  `applicant_name` varchar(100) DEFAULT '' COMMENT '发起人姓名',
  `applicant_no` varchar(64) DEFAULT '' COMMENT '发起人工号/学号',
  `applicant_user_type` varchar(32) DEFAULT '' COMMENT '发起人类型',
  `applicant_dept_id` bigint DEFAULT NULL COMMENT '发起人部门ID',
  `applicant_dept_name` varchar(100) DEFAULT '' COMMENT '发起人部门名称',
  `grade_id` bigint DEFAULT NULL COMMENT '年级ID',
  `grade_name` varchar(100) DEFAULT '' COMMENT '年级名称',
  `class_id` bigint DEFAULT NULL COMMENT '班级ID',
  `class_name` varchar(100) DEFAULT '' COMMENT '班级名称',
  `major` varchar(100) DEFAULT '' COMMENT '专业',
  `current_student_status_code` varchar(64) DEFAULT '' COMMENT '当前学籍状态编码',
  `current_student_status_name` varchar(100) DEFAULT '' COMMENT '当前学籍状态名称',
  `target_student_status_code` varchar(64) DEFAULT '' COMMENT '目标学籍状态编码',
  `target_student_status_name` varchar(100) DEFAULT '' COMMENT '目标学籍状态名称',
  `title` varchar(200) DEFAULT '' COMMENT '申请标题',
  `summary_text` varchar(500) DEFAULT '' COMMENT '摘要',
  `request_status` varchar(32) DEFAULT 'PENDING' COMMENT '申请状态',
  `current_step_index` int DEFAULT 0 COMMENT '当前步骤序号',
  `current_step_code` varchar(64) DEFAULT '' COMMENT '当前步骤编码',
  `current_step_name` varchar(100) DEFAULT '' COMMENT '当前步骤名称',
  `current_assignment_type` varchar(32) DEFAULT '' COMMENT '当前分派方式',
  `current_reviewer_role` varchar(64) DEFAULT '' COMMENT '当前审核角色',
  `current_reviewer_user_id` bigint DEFAULT NULL COMMENT '当前审核人ID',
  `current_reviewer_name` varchar(100) DEFAULT '' COMMENT '当前审核人',
  `submitted_time` datetime DEFAULT NULL COMMENT '提交时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `last_action` varchar(32) DEFAULT '' COMMENT '最后动作',
  `form_data_json` json DEFAULT NULL COMMENT '表单数据',
  `attachment_json` json DEFAULT NULL COMMENT '附件数据',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`request_id`),
  UNIQUE KEY `uk_sc_affair_request_no` (`request_no`),
  KEY `idx_sc_affair_request_template_id` (`template_id`),
  KEY `idx_sc_affair_request_applicant_user_id` (`applicant_user_id`),
  KEY `idx_sc_affair_request_status` (`request_status`),
  KEY `idx_sc_affair_request_reviewer` (`current_reviewer_role`,`current_reviewer_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务申请表';

CREATE TABLE IF NOT EXISTS `sc_affair_action` (
  `action_id` bigint NOT NULL AUTO_INCREMENT COMMENT '动作ID',
  `request_id` bigint NOT NULL COMMENT '申请ID',
  `request_no` varchar(64) DEFAULT '' COMMENT '申请编号',
  `step_index` int DEFAULT 0 COMMENT '步骤序号',
  `step_code` varchar(64) DEFAULT '' COMMENT '步骤编码',
  `step_name` varchar(100) DEFAULT '' COMMENT '步骤名称',
  `action_type` varchar(32) DEFAULT '' COMMENT '动作类型',
  `action_result` varchar(32) DEFAULT '' COMMENT '动作结果',
  `reviewer_user_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `reviewer_name` varchar(100) DEFAULT '' COMMENT '操作人名称',
  `reviewer_role` varchar(64) DEFAULT '' COMMENT '操作人角色',
  `assignment_type` varchar(32) DEFAULT '' COMMENT '分派方式',
  `comment_text` varchar(500) DEFAULT '' COMMENT '意见',
  `data_snapshot_json` json DEFAULT NULL COMMENT '数据快照',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`action_id`),
  KEY `idx_sc_affair_action_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务审批动作表';

CREATE TABLE IF NOT EXISTS `sc_student_status_log` (
  `status_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '学籍台账ID',
  `request_id` bigint DEFAULT NULL COMMENT '关联申请ID',
  `student_user_id` bigint DEFAULT NULL COMMENT '学生用户ID',
  `student_name` varchar(100) DEFAULT '' COMMENT '学生姓名',
  `student_no` varchar(64) DEFAULT '' COMMENT '学号',
  `before_status_code` varchar(64) DEFAULT '' COMMENT '变更前状态编码',
  `before_status_name` varchar(100) DEFAULT '' COMMENT '变更前状态名称',
  `after_status_code` varchar(64) DEFAULT '' COMMENT '变更后状态编码',
  `after_status_name` varchar(100) DEFAULT '' COMMENT '变更后状态名称',
  `business_code` varchar(64) DEFAULT '' COMMENT '业务编码',
  `business_name` varchar(100) DEFAULT '' COMMENT '业务名称',
  `effective_date` date DEFAULT NULL COMMENT '生效日期',
  `detail_json` json DEFAULT NULL COMMENT '业务明细',
  `operator_user_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) DEFAULT '' COMMENT '操作人名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`status_log_id`),
  KEY `idx_sc_student_status_log_student_user_id` (`student_user_id`),
  KEY `idx_sc_student_status_log_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生学籍状态变更台账表';

ALTER TABLE `sc_user_profile`
  ADD COLUMN IF NOT EXISTS `student_status_code` varchar(64) DEFAULT 'IN_SCHOOL' COMMENT '当前学籍状态编码' AFTER `status`,
  ADD COLUMN IF NOT EXISTS `student_status_name` varchar(100) DEFAULT '在籍在校' COMMENT '当前学籍状态名称' AFTER `student_status_code`,
  ADD COLUMN IF NOT EXISTS `student_status_update_time` datetime DEFAULT NULL COMMENT '学籍状态更新时间' AFTER `student_status_name`;

INSERT INTO `sc_affair_category`
  (`category_code`, `category_name`, `service_group`, `icon`, `sort_order`, `target_role_scope`, `status`, `create_by`, `create_time`, `remark`)
VALUES
  ('ASK_LEAVE', '请销假', 'ATTENDANCE', 'ri-calendar-check-line', 1, 'student', '0', 'admin', NOW(), '学生请销假办理'),
  ('LEAVE_RETURN_SCHOOL', '离返校', 'ATTENDANCE', 'ri-map-pin-user-line', 2, 'student', '0', 'admin', NOW(), '学生离校返校办理'),
  ('SCHOLARSHIP', '奖学金', 'FUNDING', 'ri-award-line', 3, 'student', '0', 'admin', NOW(), '奖学金申报与审核'),
  ('GRANT', '助学金', 'FUNDING', 'ri-hand-coin-line', 4, 'student', '0', 'admin', NOW(), '助学金申报与审核'),
  ('LOAN', '助学贷款', 'FUNDING', 'ri-bank-card-line', 5, 'student', '0', 'admin', NOW(), '助学贷款申报与审核'),
  ('WORK_STUDY', '勤工助学', 'FUNDING', 'ri-briefcase-4-line', 6, 'student', '0', 'admin', NOW(), '勤工助学岗位申请'),
  ('ACTIVITY', '活动申请', 'GROWTH', 'ri-flag-2-line', 7, 'student,teacher', '0', 'admin', NOW(), '活动立项与申请'),
  ('SUBJECT_COMPETITION', '学科竞赛管理', 'GROWTH', 'ri-trophy-line', 8, 'student,teacher', '0', 'admin', NOW(), '竞赛报名与立项'),
  ('TEXTBOOK', '教材管理', 'ACADEMIC', 'ri-book-open-line', 9, 'student,teacher', '0', 'admin', NOW(), '教材征订与补领'),
  ('INNOVATION_ENTREPRENEURSHIP', '大学生创新创业管理', 'GROWTH', 'ri-lightbulb-flash-line', 10, 'student,teacher', '0', 'admin', NOW(), '创新创业项目申请'),
  ('ACADEMIC_STATUS', '学籍异动', 'ACADEMIC', 'ri-exchange-box-line', 11, 'student', '0', 'admin', NOW(), '学籍异动管理'),
  ('COMPREHENSIVE_EVALUATION', '综测测评', 'GROWTH', 'ri-bar-chart-grouped-line', 12, 'student', '0', 'admin', NOW(), '综测测评申请与佐证'),
  ('POVERTY_IDENTIFICATION', '贫困生鉴别', 'FUNDING', 'ri-heart-pulse-line', 13, 'student', '0', 'admin', NOW(), '家庭经济困难认定')
ON DUPLICATE KEY UPDATE
  `category_name` = VALUES(`category_name`),
  `service_group` = VALUES(`service_group`),
  `icon` = VALUES(`icon`),
  `sort_order` = VALUES(`sort_order`),
  `target_role_scope` = VALUES(`target_role_scope`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'ASK_LEAVE_STANDARD',
  '学生请销假申请',
  'ASK_LEAVE_STANDARD',
  '学生请销假申请',
  'student',
  '[{"field":"leaveType","label":"请假类型","component":"select","required":true,"options":[{"label":"事假","value":"PERSONAL"},{"label":"病假","value":"SICK"},{"label":"公假","value":"OFFICIAL"}]},{"field":"startDate","label":"开始时间","component":"date","required":true},{"field":"endDate","label":"结束时间","component":"date","required":true},{"field":"destination","label":"去向地点","component":"input","required":true},{"field":"emergencyContact","label":"紧急联系人","component":"input","required":true},{"field":"reason","label":"申请说明","component":"textarea","required":true}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"}]',
  '1',
  '1',
  '',
  '',
  1,
  '0',
  'admin',
  NOW(),
  '学生请销假默认模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'ASK_LEAVE'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'LEAVE_RETURN_STANDARD',
  '离返校申请',
  'LEAVE_RETURN_STANDARD',
  '离返校申请',
  'student',
  '[{"field":"leaveSchoolDate","label":"离校日期","component":"date","required":true},{"field":"expectedReturnDate","label":"预计返校日期","component":"date","required":true},{"field":"location","label":"当前去向","component":"input","required":true},{"field":"healthStatus","label":"健康状态","component":"select","required":true,"options":[{"label":"良好","value":"GOOD"},{"label":"需关注","value":"WATCH"},{"label":"特殊情况","value":"SPECIAL"}]},{"field":"reason","label":"申请说明","component":"textarea","required":true}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"},{"stepCode":"admin_review","stepName":"学工审核","assignmentType":"ROLE","reviewerRole":"admin"}]',
  '1',
  '1',
  '',
  '',
  2,
  '0',
  'admin',
  NOW(),
  '离返校办理模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'LEAVE_RETURN_SCHOOL'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'SCHOLARSHIP_APPLY',
  '奖学金申请',
  'SCHOLARSHIP_APPLY',
  '奖学金申请',
  'student',
  '[{"field":"scholarshipType","label":"奖项类型","component":"select","required":true,"options":[{"label":"国家奖学金","value":"NATIONAL"},{"label":"校级奖学金","value":"SCHOOL"},{"label":"专项奖学金","value":"SPECIAL"}]},{"field":"awardTerm","label":"申报学期","component":"input","required":true},{"field":"honorLevel","label":"成果/荣誉","component":"input","required":true},{"field":"mentorTeacherId","label":"指导教师","component":"user-select","required":true,"optionSource":"teacher"},{"field":"reason","label":"申请陈述","component":"textarea","required":true}]',
  '[{"stepCode":"teacher_review","stepName":"指导教师审核","assignmentType":"FORM_FIELD","reviewerField":"mentorTeacherId"},{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"}]',
  '1',
  '1',
  '',
  '',
  3,
  '0',
  'admin',
  NOW(),
  '奖学金申报模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'SCHOLARSHIP'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'GRANT_APPLY',
  '助学金申请',
  'GRANT_APPLY',
  '助学金申请',
  'student',
  '[{"field":"grantType","label":"助学金类型","component":"select","required":true,"options":[{"label":"国家助学金","value":"NATIONAL"},{"label":"校级助学金","value":"SCHOOL"}]},{"field":"familyIncome","label":"家庭年收入（元）","component":"number","required":true},{"field":"hardshipLevel","label":"困难等级","component":"select","required":true,"options":[{"label":"特别困难","value":"LEVEL_A"},{"label":"困难","value":"LEVEL_B"},{"label":"一般困难","value":"LEVEL_C"}]},{"field":"reason","label":"申请说明","component":"textarea","required":true}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"},{"stepCode":"admin_review","stepName":"资助中心审核","assignmentType":"ROLE","reviewerRole":"admin"}]',
  '1',
  '1',
  '',
  '',
  4,
  '0',
  'admin',
  NOW(),
  '助学金模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'GRANT'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'LOAN_APPLY',
  '助学贷款申请',
  'LOAN_APPLY',
  '助学贷款申请',
  'student',
  '[{"field":"loanType","label":"贷款类型","component":"select","required":true,"options":[{"label":"国家助学贷款","value":"NATIONAL"},{"label":"生源地贷款","value":"LOCAL"}]},{"field":"loanAmount","label":"申请金额（元）","component":"number","required":true},{"field":"loanTerm","label":"贷款期限（年）","component":"number","required":true},{"field":"bankName","label":"经办银行","component":"input","required":false},{"field":"reason","label":"申请说明","component":"textarea","required":true}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"},{"stepCode":"admin_review","stepName":"资助中心审核","assignmentType":"ROLE","reviewerRole":"admin"}]',
  '1',
  '1',
  '',
  '',
  5,
  '0',
  'admin',
  NOW(),
  '助学贷款模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'LOAN'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'WORK_STUDY_APPLY',
  '勤工助学申请',
  'WORK_STUDY_APPLY',
  '勤工助学申请',
  'student',
  '[{"field":"jobPreference","label":"岗位意向","component":"input","required":true},{"field":"availableHours","label":"每周可投入时长","component":"number","required":true},{"field":"skillTags","label":"技能特长","component":"input","required":false},{"field":"reason","label":"申请说明","component":"textarea","required":true}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"}]',
  '1',
  '1',
  '',
  '',
  6,
  '0',
  'admin',
  NOW(),
  '勤工助学模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'WORK_STUDY'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'ACTIVITY_APPLY',
  '活动申请',
  'ACTIVITY_APPLY',
  '活动申请',
  'student,teacher',
  '[{"field":"activityName","label":"活动名称","component":"input","required":true},{"field":"activityDate","label":"活动日期","component":"date","required":true},{"field":"venue","label":"活动地点","component":"input","required":true},{"field":"participantCount","label":"参与人数","component":"number","required":true},{"field":"mentorTeacherId","label":"指导教师","component":"user-select","required":true,"optionSource":"teacher"},{"field":"reason","label":"活动说明","component":"textarea","required":true}]',
  '[{"stepCode":"teacher_review","stepName":"指导教师审核","assignmentType":"FORM_FIELD","reviewerField":"mentorTeacherId"},{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"}]',
  '1',
  '1',
  '',
  '',
  7,
  '0',
  'admin',
  NOW(),
  '活动申请模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'ACTIVITY'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'SUBJECT_COMPETITION_APPLY',
  '学科竞赛申请',
  'SUBJECT_COMPETITION_APPLY',
  '学科竞赛申请',
  'student,teacher',
  '[{"field":"competitionName","label":"竞赛名称","component":"input","required":true},{"field":"competitionLevel","label":"竞赛级别","component":"select","required":true,"options":[{"label":"校级","value":"SCHOOL"},{"label":"市级","value":"CITY"},{"label":"省级","value":"PROVINCE"},{"label":"国家级","value":"NATIONAL"}]},{"field":"competitionDate","label":"竞赛时间","component":"date","required":true},{"field":"mentorTeacherId","label":"指导教师","component":"user-select","required":true,"optionSource":"teacher"},{"field":"teamInfo","label":"团队成员","component":"textarea","required":false},{"field":"reason","label":"申请说明","component":"textarea","required":true}]',
  '[{"stepCode":"teacher_review","stepName":"指导教师审核","assignmentType":"FORM_FIELD","reviewerField":"mentorTeacherId"},{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"}]',
  '1',
  '1',
  '',
  '',
  8,
  '0',
  'admin',
  NOW(),
  '学科竞赛模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'SUBJECT_COMPETITION'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'TEXTBOOK_APPLY',
  '教材申请',
  'TEXTBOOK_APPLY',
  '教材申请',
  'student,teacher',
  '[{"field":"textbookType","label":"办理类型","component":"select","required":true,"options":[{"label":"教材领用","value":"CLAIM"},{"label":"教材补领","value":"REISSUE"},{"label":"教材征订","value":"ORDER"}]},{"field":"courseName","label":"对应课程","component":"input","required":true},{"field":"isbn","label":"ISBN/教材编号","component":"input","required":false},{"field":"quantity","label":"数量","component":"number","required":true},{"field":"reason","label":"申请说明","component":"textarea","required":true}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"},{"stepCode":"admin_review","stepName":"教务审核","assignmentType":"ROLE","reviewerRole":"admin"}]',
  '1',
  '1',
  '',
  '',
  9,
  '0',
  'admin',
  NOW(),
  '教材模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'TEXTBOOK'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'INNOVATION_PROJECT_APPLY',
  '创新创业项目申请',
  'INNOVATION_PROJECT_APPLY',
  '创新创业项目申请',
  'student,teacher',
  '[{"field":"projectName","label":"项目名称","component":"input","required":true},{"field":"projectType","label":"项目类型","component":"select","required":true,"options":[{"label":"创新训练","value":"INNOVATION"},{"label":"创业实践","value":"ENTREPRENEURSHIP"},{"label":"重点孵化","value":"INCUBATION"}]},{"field":"mentorTeacherId","label":"指导教师","component":"user-select","required":true,"optionSource":"teacher"},{"field":"fundingNeed","label":"经费需求（元）","component":"number","required":false},{"field":"reason","label":"项目说明","component":"textarea","required":true}]',
  '[{"stepCode":"teacher_review","stepName":"指导教师审核","assignmentType":"FORM_FIELD","reviewerField":"mentorTeacherId"},{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"},{"stepCode":"admin_review","stepName":"创新创业中心审核","assignmentType":"ROLE","reviewerRole":"admin"}]',
  '1',
  '1',
  '',
  '',
  10,
  '0',
  'admin',
  NOW(),
  '创新创业模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'INNOVATION_ENTREPRENEURSHIP'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'ACADEMIC_STATUS_SUSPEND',
  '学籍异动-休学',
  'ACADEMIC_STATUS_SUSPEND',
  '休学申请',
  'student',
  '[{"field":"effectiveDate","label":"生效日期","component":"date","required":true},{"field":"expectedResumeDate","label":"预计复学日期","component":"date","required":false},{"field":"guardianPhone","label":"家长联系电话","component":"input","required":true},{"field":"reason","label":"异动原因","component":"textarea","required":true}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"},{"stepCode":"admin_review","stepName":"教务审核","assignmentType":"ROLE","reviewerRole":"admin"}]',
  '1',
  '1',
  'SUSPENDED',
  '休学',
  11,
  '0',
  'admin',
  NOW(),
  '学籍异动休学模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'ACADEMIC_STATUS'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'ACADEMIC_STATUS_RESUME',
  '学籍异动-复学',
  'ACADEMIC_STATUS_RESUME',
  '复学申请',
  'student',
  '[{"field":"effectiveDate","label":"生效日期","component":"date","required":true},{"field":"recoveryProof","label":"复学依据","component":"input","required":false},{"field":"reason","label":"异动原因","component":"textarea","required":true}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"},{"stepCode":"admin_review","stepName":"教务审核","assignmentType":"ROLE","reviewerRole":"admin"}]',
  '1',
  '1',
  'IN_SCHOOL',
  '在籍在校',
  12,
  '0',
  'admin',
  NOW(),
  '学籍异动复学模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'ACADEMIC_STATUS'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'ACADEMIC_STATUS_TRANSFER_MAJOR',
  '学籍异动-转专业',
  'ACADEMIC_STATUS_TRANSFER_MAJOR',
  '转专业申请',
  'student',
  '[{"field":"effectiveDate","label":"生效日期","component":"date","required":true},{"field":"targetMajor","label":"目标专业","component":"input","required":true},{"field":"reason","label":"异动原因","component":"textarea","required":true}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"},{"stepCode":"admin_review","stepName":"教务审核","assignmentType":"ROLE","reviewerRole":"admin"}]',
  '1',
  '1',
  'TRANSFERRED_MAJOR',
  '转专业',
  13,
  '0',
  'admin',
  NOW(),
  '学籍异动转专业模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'ACADEMIC_STATUS'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'COMPREHENSIVE_EVALUATION_APPLY',
  '综测测评申请',
  'COMPREHENSIVE_EVALUATION_APPLY',
  '综测测评申请',
  'student',
  '[{"field":"evaluationTerm","label":"评价学期","component":"input","required":true},{"field":"achievementSummary","label":"德育/竞赛/志愿成果","component":"textarea","required":true},{"field":"volunteerHours","label":"志愿时长（小时）","component":"number","required":false},{"field":"reason","label":"补充说明","component":"textarea","required":false}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"}]',
  '1',
  '1',
  '',
  '',
  14,
  '0',
  'admin',
  NOW(),
  '综测测评模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'COMPREHENSIVE_EVALUATION'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'POVERTY_IDENTIFICATION_APPLY',
  '贫困生认定申请',
  'POVERTY_IDENTIFICATION_APPLY',
  '贫困生认定申请',
  'student',
  '[{"field":"familyIncome","label":"家庭年收入（元）","component":"number","required":true},{"field":"familyMembers","label":"家庭人口","component":"number","required":true},{"field":"hardshipLevel","label":"困难等级","component":"select","required":true,"options":[{"label":"特别困难","value":"LEVEL_A"},{"label":"困难","value":"LEVEL_B"},{"label":"一般困难","value":"LEVEL_C"}]},{"field":"reason","label":"认定说明","component":"textarea","required":true}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员审核","assignmentType":"ROLE","reviewerRole":"advisor"},{"stepCode":"admin_review","stepName":"资助中心审核","assignmentType":"ROLE","reviewerRole":"admin"}]',
  '1',
  '1',
  '',
  '',
  15,
  '0',
  'admin',
  NOW(),
  '贫困生认定模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'POVERTY_IDENTIFICATION'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `audience_roles` = VALUES(`audience_roles`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `target_status_code` = VALUES(`target_status_code`),
  `target_status_name` = VALUES(`target_status_name`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

DELETE FROM `sys_menu` WHERE `menu_id` BETWEEN 2300 AND 2335;

INSERT INTO `sys_menu`
  (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
  (2300, '学生事务', 2000, 9, 'affair', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-service-line', 'admin', NOW(), '', NULL, '学生事务中心目录'),
  (2301, '事务分类', 2300, 1, 'category', 'campus/affair/category/index', '', '', 1, 0, 'C', '0', '0', 'campus:affairCategory:list', 'ri-shapes-line', 'admin', NOW(), '', NULL, '事务分类管理'),
  (2302, '事务模板', 2300, 2, 'template', 'campus/affair/template/index', '', '', 1, 0, 'C', '0', '0', 'campus:affairTemplate:list', 'ri-file-settings-line', 'admin', NOW(), '', NULL, '事务模板管理'),
  (2303, '事务申请', 2300, 3, 'request', 'campus/affair/request/index', '', '', 1, 0, 'C', '0', '0', 'campus:affairRequest:list', 'ri-file-list-3-line', 'admin', NOW(), '', NULL, '事务申请管理'),
  (2304, '学籍台账', 2300, 4, 'studentStatus', 'campus/affair/status/index', '', '', 1, 0, 'C', '0', '0', 'campus:studentStatus:list', 'ri-folders-line', 'admin', NOW(), '', NULL, '学籍状态变更台账'),
  (2310, '分类查询', 2301, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:affairCategory:query', '#', 'admin', NOW(), '', NULL, ''),
  (2311, '分类新增', 2301, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:affairCategory:add', '#', 'admin', NOW(), '', NULL, ''),
  (2312, '分类修改', 2301, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:affairCategory:edit', '#', 'admin', NOW(), '', NULL, ''),
  (2313, '分类删除', 2301, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:affairCategory:remove', '#', 'admin', NOW(), '', NULL, ''),
  (2320, '模板查询', 2302, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:affairTemplate:query', '#', 'admin', NOW(), '', NULL, ''),
  (2321, '模板新增', 2302, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:affairTemplate:add', '#', 'admin', NOW(), '', NULL, ''),
  (2322, '模板修改', 2302, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:affairTemplate:edit', '#', 'admin', NOW(), '', NULL, ''),
  (2323, '模板删除', 2302, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:affairTemplate:remove', '#', 'admin', NOW(), '', NULL, ''),
  (2330, '申请查询', 2303, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:affairRequest:query', '#', 'admin', NOW(), '', NULL, ''),
  (2331, '申请审核', 2303, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:affairRequest:review', '#', 'admin', NOW(), '', NULL, ''),
  (2332, '台账查询', 2304, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:studentStatus:query', '#', 'admin', NOW(), '', NULL, '');

SET FOREIGN_KEY_CHECKS = 1;
