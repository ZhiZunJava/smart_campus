SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `sc_affair_activity_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `request_id` bigint NOT NULL COMMENT '事务申请ID',
  `request_no` varchar(64) DEFAULT '' COMMENT '申请编号',
  `applicant_user_id` bigint DEFAULT NULL COMMENT '申请人ID',
  `template_code` varchar(64) DEFAULT '' COMMENT '模板编码',
  `activity_name` varchar(200) DEFAULT '' COMMENT '活动名称',
  `activity_date` date DEFAULT NULL COMMENT '活动日期',
  `venue` varchar(200) DEFAULT '' COMMENT '活动地点',
  `participant_count` int DEFAULT 0 COMMENT '参与人数',
  `mentor_teacher_id` bigint DEFAULT NULL COMMENT '指导教师ID',
  `mentor_teacher_name` varchar(100) DEFAULT '' COMMENT '指导教师',
  `reason` text COMMENT '申请说明',
  `request_status` varchar(32) DEFAULT 'PENDING' COMMENT '申请状态',
  `business_snapshot_json` json DEFAULT NULL COMMENT '业务快照',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_sc_affair_activity_record_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务-活动申请记录';

CREATE TABLE IF NOT EXISTS `sc_affair_competition_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `request_id` bigint NOT NULL COMMENT '事务申请ID',
  `request_no` varchar(64) DEFAULT '' COMMENT '申请编号',
  `applicant_user_id` bigint DEFAULT NULL COMMENT '申请人ID',
  `template_code` varchar(64) DEFAULT '' COMMENT '模板编码',
  `competition_name` varchar(200) DEFAULT '' COMMENT '竞赛名称',
  `competition_level` varchar(64) DEFAULT '' COMMENT '竞赛级别',
  `competition_date` date DEFAULT NULL COMMENT '竞赛时间',
  `mentor_teacher_id` bigint DEFAULT NULL COMMENT '指导教师ID',
  `mentor_teacher_name` varchar(100) DEFAULT '' COMMENT '指导教师',
  `team_info` text COMMENT '团队信息',
  `reason` text COMMENT '申请说明',
  `request_status` varchar(32) DEFAULT 'PENDING' COMMENT '申请状态',
  `business_snapshot_json` json DEFAULT NULL COMMENT '业务快照',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_sc_affair_competition_record_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务-学科竞赛记录';

CREATE TABLE IF NOT EXISTS `sc_affair_innovation_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `request_id` bigint NOT NULL COMMENT '事务申请ID',
  `request_no` varchar(64) DEFAULT '' COMMENT '申请编号',
  `applicant_user_id` bigint DEFAULT NULL COMMENT '申请人ID',
  `template_code` varchar(64) DEFAULT '' COMMENT '模板编码',
  `project_name` varchar(200) DEFAULT '' COMMENT '项目名称',
  `project_type` varchar(64) DEFAULT '' COMMENT '项目类型',
  `mentor_teacher_id` bigint DEFAULT NULL COMMENT '指导教师ID',
  `mentor_teacher_name` varchar(100) DEFAULT '' COMMENT '指导教师',
  `funding_need` decimal(10,2) DEFAULT NULL COMMENT '经费需求',
  `reason` text COMMENT '项目说明',
  `request_status` varchar(32) DEFAULT 'PENDING' COMMENT '申请状态',
  `business_snapshot_json` json DEFAULT NULL COMMENT '业务快照',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_sc_affair_innovation_record_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务-创新创业项目记录';

CREATE TABLE IF NOT EXISTS `sc_affair_evaluation_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `request_id` bigint NOT NULL COMMENT '事务申请ID',
  `request_no` varchar(64) DEFAULT '' COMMENT '申请编号',
  `applicant_user_id` bigint DEFAULT NULL COMMENT '申请人ID',
  `template_code` varchar(64) DEFAULT '' COMMENT '模板编码',
  `evaluation_term` varchar(100) DEFAULT '' COMMENT '评价学期',
  `achievement_summary` text COMMENT '成果摘要',
  `volunteer_hours` decimal(10,2) DEFAULT NULL COMMENT '志愿时长',
  `reason` text COMMENT '补充说明',
  `request_status` varchar(32) DEFAULT 'PENDING' COMMENT '申请状态',
  `business_snapshot_json` json DEFAULT NULL COMMENT '业务快照',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_sc_affair_evaluation_record_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务-综测测评记录';

CREATE TABLE IF NOT EXISTS `sc_affair_ask_leave_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `request_id` bigint NOT NULL COMMENT '事务申请ID',
  `request_no` varchar(64) DEFAULT '' COMMENT '申请编号',
  `applicant_user_id` bigint DEFAULT NULL COMMENT '申请人ID',
  `template_code` varchar(64) DEFAULT '' COMMENT '模板编码',
  `business_type` varchar(32) DEFAULT '' COMMENT '业务类型',
  `leave_type` varchar(64) DEFAULT '' COMMENT '请假类型',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `destination` varchar(200) DEFAULT '' COMMENT '去向地点',
  `emergency_contact` varchar(100) DEFAULT '' COMMENT '紧急联系人',
  `leave_request_no` varchar(64) DEFAULT '' COMMENT '关联请假编号',
  `actual_return_date` date DEFAULT NULL COMMENT '实际返校日期',
  `cancel_remark` text COMMENT '销假说明',
  `reason` text COMMENT '申请说明',
  `request_status` varchar(32) DEFAULT 'PENDING' COMMENT '申请状态',
  `business_snapshot_json` json DEFAULT NULL COMMENT '业务快照',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_sc_affair_ask_leave_record_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务-请销假记录';

CREATE TABLE IF NOT EXISTS `sc_affair_leave_return_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `request_id` bigint NOT NULL COMMENT '事务申请ID',
  `request_no` varchar(64) DEFAULT '' COMMENT '申请编号',
  `applicant_user_id` bigint DEFAULT NULL COMMENT '申请人ID',
  `template_code` varchar(64) DEFAULT '' COMMENT '模板编码',
  `business_type` varchar(32) DEFAULT '' COMMENT '业务类型',
  `leave_school_date` date DEFAULT NULL COMMENT '离校日期',
  `expected_return_date` date DEFAULT NULL COMMENT '预计返校日期',
  `location` varchar(200) DEFAULT '' COMMENT '当前去向',
  `health_status` varchar(64) DEFAULT '' COMMENT '健康状态',
  `leave_request_no` varchar(64) DEFAULT '' COMMENT '关联离校编号',
  `return_date` date DEFAULT NULL COMMENT '返校日期',
  `travel_summary` text COMMENT '行程说明',
  `reason` text COMMENT '申请说明',
  `request_status` varchar(32) DEFAULT 'PENDING' COMMENT '申请状态',
  `business_snapshot_json` json DEFAULT NULL COMMENT '业务快照',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_sc_affair_leave_return_record_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务-离返校记录';

CREATE TABLE IF NOT EXISTS `sc_affair_textbook_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `request_id` bigint NOT NULL COMMENT '事务申请ID',
  `request_no` varchar(64) DEFAULT '' COMMENT '申请编号',
  `applicant_user_id` bigint DEFAULT NULL COMMENT '申请人ID',
  `template_code` varchar(64) DEFAULT '' COMMENT '模板编码',
  `textbook_type` varchar(64) DEFAULT '' COMMENT '办理类型',
  `course_name` varchar(200) DEFAULT '' COMMENT '课程名称',
  `isbn` varchar(100) DEFAULT '' COMMENT 'ISBN',
  `quantity` int DEFAULT 0 COMMENT '数量',
  `reason` text COMMENT '申请说明',
  `request_status` varchar(32) DEFAULT 'PENDING' COMMENT '申请状态',
  `business_snapshot_json` json DEFAULT NULL COMMENT '业务快照',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_sc_affair_textbook_record_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务-教材管理记录';

CREATE TABLE IF NOT EXISTS `sc_affair_academic_status_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `request_id` bigint NOT NULL COMMENT '事务申请ID',
  `request_no` varchar(64) DEFAULT '' COMMENT '申请编号',
  `applicant_user_id` bigint DEFAULT NULL COMMENT '申请人ID',
  `template_code` varchar(64) DEFAULT '' COMMENT '模板编码',
  `effective_date` date DEFAULT NULL COMMENT '生效日期',
  `expected_resume_date` date DEFAULT NULL COMMENT '预计复学日期',
  `target_major` varchar(100) DEFAULT '' COMMENT '目标专业',
  `recovery_proof` varchar(200) DEFAULT '' COMMENT '复学依据',
  `guardian_phone` varchar(32) DEFAULT '' COMMENT '家长联系电话',
  `before_status_code` varchar(64) DEFAULT '' COMMENT '变更前状态编码',
  `before_status_name` varchar(100) DEFAULT '' COMMENT '变更前状态名称',
  `after_status_code` varchar(64) DEFAULT '' COMMENT '变更后状态编码',
  `after_status_name` varchar(100) DEFAULT '' COMMENT '变更后状态名称',
  `reason` text COMMENT '异动原因',
  `request_status` varchar(32) DEFAULT 'PENDING' COMMENT '申请状态',
  `business_snapshot_json` json DEFAULT NULL COMMENT '业务快照',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_sc_affair_academic_status_record_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生事务-学籍异动记录';

SET FOREIGN_KEY_CHECKS = 1;
