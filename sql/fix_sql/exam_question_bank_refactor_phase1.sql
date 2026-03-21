-- 题库与题目版本重构第一阶段
-- 目标：
-- 1. 独立题库实体，支持权限范围
-- 2. 引入题目主表与版本表，为后续版本追溯做准备
-- 3. 引入题库-题目关系表，为题目复用与多题库归属做准备
-- 执行前请先备份数据库

CREATE TABLE IF NOT EXISTS `sc_question_catalog` (
  `catalog_id` bigint NOT NULL AUTO_INCREMENT COMMENT '题库ID',
  `catalog_name` varchar(128) NOT NULL COMMENT '题库名称',
  `course_id` bigint DEFAULT NULL COMMENT '课程ID',
  `grade_id` bigint DEFAULT NULL COMMENT '年级ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '创建人用户ID',
  `visibility_type` varchar(32) NOT NULL DEFAULT 'PRIVATE' COMMENT '可见范围类型(PRIVATE/ROLE_SHARED/CLASS_SHARED/DEPT_SHARED/PUBLIC)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`catalog_id`),
  KEY `idx_question_catalog_course` (`course_id`),
  KEY `idx_question_catalog_grade` (`grade_id`),
  KEY `idx_question_catalog_owner` (`owner_user_id`),
  KEY `idx_question_catalog_visibility` (`visibility_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-题库主表';

CREATE TABLE IF NOT EXISTS `sc_question_catalog_scope` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `catalog_id` bigint NOT NULL COMMENT '题库ID',
  `scope_type` varchar(32) NOT NULL COMMENT '范围类型(ROLE/CLASS/DEPT/USER)',
  `scope_ref_id` bigint NOT NULL COMMENT '范围对象ID',
  `scope_ref_name` varchar(128) DEFAULT NULL COMMENT '范围对象名称快照',
  `permission_level` varchar(32) NOT NULL DEFAULT 'VIEW' COMMENT '权限级别(VIEW/MANAGE)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_catalog_scope_unique` (`catalog_id`, `scope_type`, `scope_ref_id`, `permission_level`),
  KEY `idx_catalog_scope_ref` (`scope_type`, `scope_ref_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-题库可见范围表';

CREATE TABLE IF NOT EXISTS `sc_question_item` (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目主键ID',
  `course_id` bigint DEFAULT NULL COMMENT '课程ID',
  `default_catalog_id` bigint DEFAULT NULL COMMENT '默认题库ID',
  `latest_version_id` bigint DEFAULT NULL COMMENT '最新版本ID',
  `current_version_no` int NOT NULL DEFAULT 1 COMMENT '当前版本号',
  `question_type` varchar(32) NOT NULL COMMENT '题型',
  `difficulty_level` int DEFAULT 3 COMMENT '难度级别',
  `source_type` varchar(32) DEFAULT 'MANUAL' COMMENT '来源类型(MANUAL/IMPORT/AI)',
  `source_ref_id` bigint DEFAULT NULL COMMENT '来源业务ID',
  `source_batch_no` varchar(64) DEFAULT NULL COMMENT '来源批次号',
  `creator_user_id` bigint DEFAULT NULL COMMENT '题目创建人用户ID',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`item_id`),
  KEY `idx_question_item_course` (`course_id`),
  KEY `idx_question_item_catalog` (`default_catalog_id`),
  KEY `idx_question_item_type` (`question_type`),
  KEY `idx_question_item_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-题目主表';

CREATE TABLE IF NOT EXISTS `sc_question_item_version` (
  `version_id` bigint NOT NULL AUTO_INCREMENT COMMENT '版本ID',
  `item_id` bigint NOT NULL COMMENT '题目ID',
  `version_no` int NOT NULL COMMENT '版本号',
  `chapter_id` bigint DEFAULT NULL COMMENT '章节ID',
  `knowledge_point_id` bigint DEFAULT NULL COMMENT '知识点ID',
  `stem` longtext COMMENT '题干',
  `answer` longtext COMMENT '答案',
  `analysis` longtext COMMENT '解析',
  `material_content` longtext COMMENT '材料内容',
  `question_tags` varchar(255) DEFAULT NULL COMMENT '题目标签',
  `quality_score` decimal(8,2) DEFAULT 0.00 COMMENT '质量评分',
  `source` varchar(255) DEFAULT NULL COMMENT '来源说明',
  `change_type` varchar(32) DEFAULT 'EDIT' COMMENT '变更类型(CREATE/EDIT/AI/IMPORT)',
  `is_current` char(1) NOT NULL DEFAULT '1' COMMENT '是否当前版本(0否 1是)',
  `ai_model_id` bigint DEFAULT NULL COMMENT 'AI模型ID',
  `ai_prompt_snapshot` longtext COMMENT 'AI提示词快照',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`version_id`),
  UNIQUE KEY `uk_question_item_version` (`item_id`, `version_no`),
  KEY `idx_question_item_version_item` (`item_id`),
  KEY `idx_question_item_version_current` (`item_id`, `is_current`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-题目版本表';

CREATE TABLE IF NOT EXISTS `sc_question_catalog_item_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `catalog_id` bigint NOT NULL COMMENT '题库ID',
  `item_id` bigint NOT NULL COMMENT '题目ID',
  `sort_no` int DEFAULT 0 COMMENT '排序号',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_question_catalog_item` (`catalog_id`, `item_id`),
  KEY `idx_question_catalog_item_item` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-题库题目关系表';
