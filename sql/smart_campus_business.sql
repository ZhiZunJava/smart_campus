SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 智慧校园一期业务库表
-- 说明：
-- 1. 依赖若依基础库表，如 sys_user/sys_role/sys_menu/sys_dict_* 等
-- 2. 先执行 sql/base.sql、sql/quartz.sql，再执行本脚本
-- 3. 采用逻辑关联为主，便于与若依现有结构平滑集成
-- ----------------------------

-- ----------------------------
-- 1. 用户扩展档案表
-- ----------------------------
DROP TABLE IF EXISTS `sc_user_profile`;
CREATE TABLE `sc_user_profile` (
  `profile_id`           bigint NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `user_type`            varchar(20) NOT NULL COMMENT '用户类型(student/teacher/admin/parent)',
  `student_no`           varchar(32) DEFAULT NULL COMMENT '学号',
  `teacher_no`           varchar(32) DEFAULT NULL COMMENT '工号',
  `real_name`            varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `gender`               char(1) DEFAULT '0' COMMENT '性别(0未知 1男 2女)',
  `grade_id`             bigint DEFAULT NULL COMMENT '年级ID',
  `class_id`             bigint DEFAULT NULL COMMENT '班级ID',
  `major`                varchar(100) DEFAULT NULL COMMENT '专业',
  `admission_year`       int DEFAULT NULL COMMENT '入学年份',
  `learning_goal`        varchar(500) DEFAULT NULL COMMENT '学习目标',
  `interest_tags`        varchar(500) DEFAULT NULL COMMENT '兴趣标签',
  `learning_style`       varchar(50) DEFAULT NULL COMMENT '学习风格',
  `avatar_url`           varchar(255) DEFAULT NULL COMMENT '头像地址',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`profile_id`),
  UNIQUE KEY `uk_sc_user_profile_user_id` (`user_id`),
  KEY `idx_sc_user_profile_student_no` (`student_no`),
  KEY `idx_sc_user_profile_teacher_no` (`teacher_no`),
  KEY `idx_sc_user_profile_grade_class` (`grade_id`, `class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-用户扩展档案表';

-- ----------------------------
-- 2. 家长学生关系表
-- ----------------------------
DROP TABLE IF EXISTS `sc_parent_student_rel`;
CREATE TABLE `sc_parent_student_rel` (
  `id`                   bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_user_id`       bigint NOT NULL COMMENT '家长用户ID',
  `student_user_id`      bigint NOT NULL COMMENT '学生用户ID',
  `relation_type`        varchar(20) DEFAULT 'guardian' COMMENT '关系类型(father/mother/guardian)',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_parent_student_rel` (`parent_user_id`, `student_user_id`),
  KEY `idx_sc_parent_student_student` (`student_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-家长学生关系表';

-- ----------------------------
-- 3. 登录风险事件表
-- ----------------------------
DROP TABLE IF EXISTS `sc_login_risk_event`;
CREATE TABLE `sc_login_risk_event` (
  `id`                   bigint NOT NULL AUTO_INCREMENT COMMENT '事件ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `login_ip`             varchar(64) DEFAULT NULL COMMENT '登录IP',
  `login_location`       varchar(128) DEFAULT NULL COMMENT '登录地点',
  `device_fingerprint`   varchar(128) DEFAULT NULL COMMENT '设备指纹',
  `device_type`          varchar(32) DEFAULT NULL COMMENT '设备类型',
  `browser`              varchar(64) DEFAULT NULL COMMENT '浏览器',
  `os`                   varchar(64) DEFAULT NULL COMMENT '操作系统',
  `login_time`           datetime NOT NULL COMMENT '登录时间',
  `risk_type`            varchar(50) NOT NULL COMMENT '风险类型',
  `risk_score`           decimal(5,2) DEFAULT 0.00 COMMENT '风险得分',
  `risk_level`           varchar(20) DEFAULT 'LOW' COMMENT '风险等级(LOW/MEDIUM/HIGH)',
  `dispose_action`       varchar(50) DEFAULT NULL COMMENT '处置动作',
  `dispose_status`       varchar(20) DEFAULT 'PENDING' COMMENT '处置状态',
  `evidence_json`        text COMMENT '证据JSON',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_sc_login_risk_user_time` (`user_id`, `login_time`),
  KEY `idx_sc_login_risk_level` (`risk_level`),
  KEY `idx_sc_login_risk_type` (`risk_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-登录风险事件表';

-- ----------------------------
-- 4. 年级表
-- ----------------------------
DROP TABLE IF EXISTS `sc_grade`;
CREATE TABLE `sc_grade` (
  `grade_id`             bigint NOT NULL AUTO_INCREMENT COMMENT '年级ID',
  `grade_name`           varchar(50) NOT NULL COMMENT '年级名称',
  `school_year`          varchar(20) DEFAULT NULL COMMENT '学年',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`grade_id`),
  KEY `idx_sc_grade_name` (`grade_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-年级表';

-- ----------------------------
-- 5. 班级表
-- ----------------------------
DROP TABLE IF EXISTS `sc_class`;
CREATE TABLE `sc_class` (
  `class_id`             bigint NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `grade_id`             bigint NOT NULL COMMENT '年级ID',
  `class_name`           varchar(100) NOT NULL COMMENT '班级名称',
  `head_teacher_id`      bigint DEFAULT NULL COMMENT '班主任用户ID',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`class_id`),
  KEY `idx_sc_class_grade_id` (`grade_id`),
  KEY `idx_sc_class_head_teacher_id` (`head_teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-班级表';

-- ----------------------------
-- 6. 课程表
-- ----------------------------
DROP TABLE IF EXISTS `sc_course`;
CREATE TABLE `sc_course` (
  `course_id`            bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name`          varchar(100) NOT NULL COMMENT '课程名称',
  `course_code`          varchar(50) DEFAULT NULL COMMENT '课程编码',
  `subject_type`         varchar(50) DEFAULT NULL COMMENT '学科类型',
  `teacher_id`           bigint DEFAULT NULL COMMENT '授课教师ID',
  `grade_id`             bigint DEFAULT NULL COMMENT '所属年级ID',
  `cover_url`            varchar(255) DEFAULT NULL COMMENT '封面地址',
  `intro`                text COMMENT '课程简介',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`course_id`),
  KEY `idx_sc_course_teacher_id` (`teacher_id`),
  KEY `idx_sc_course_grade_id` (`grade_id`),
  KEY `idx_sc_course_code` (`course_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-课程表';

-- ----------------------------
-- 7. 课程章节表
-- ----------------------------
DROP TABLE IF EXISTS `sc_course_chapter`;
CREATE TABLE `sc_course_chapter` (
  `chapter_id`           bigint NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id`            bigint NOT NULL COMMENT '课程ID',
  `parent_id`            bigint DEFAULT 0 COMMENT '父章节ID',
  `chapter_name`         varchar(100) NOT NULL COMMENT '章节名称',
  `chapter_order`        int DEFAULT 0 COMMENT '排序号',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`chapter_id`),
  KEY `idx_sc_course_chapter_course_parent` (`course_id`, `parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-课程章节表';

-- ----------------------------
-- 8. 课程学生关系表
-- ----------------------------
DROP TABLE IF EXISTS `sc_course_student`;
CREATE TABLE `sc_course_student` (
  `id`                   bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_course_id`      bigint DEFAULT NULL COMMENT '班级课程ID',
  `course_id`            bigint NOT NULL COMMENT '课程ID',
  `student_user_id`      bigint NOT NULL COMMENT '学生用户ID',
  `class_id`             bigint DEFAULT NULL COMMENT '班级ID',
  `join_time`            datetime DEFAULT NULL COMMENT '加入时间',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_course_student` (`class_course_id`, `student_user_id`),
  KEY `idx_sc_course_student_class_course_id` (`class_course_id`),
  KEY `idx_sc_course_student_class_id` (`class_id`),
  KEY `idx_sc_course_student_student` (`student_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-课程学生关系表';

-- ----------------------------
-- 9. 选课计划表
-- ----------------------------
DROP TABLE IF EXISTS `sc_course_selection_plan`;
CREATE TABLE `sc_course_selection_plan` (
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

-- ----------------------------
-- 10. 个性化选课申请表
-- ----------------------------
DROP TABLE IF EXISTS `sc_course_selection_request`;
CREATE TABLE `sc_course_selection_request` (
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

-- ----------------------------
-- 11. 知识点表
-- ----------------------------
DROP TABLE IF EXISTS `sc_knowledge_point`;
CREATE TABLE `sc_knowledge_point` (
  `knowledge_point_id`   bigint NOT NULL AUTO_INCREMENT COMMENT '知识点ID',
  `course_id`            bigint NOT NULL COMMENT '课程ID',
  `parent_id`            bigint DEFAULT 0 COMMENT '父知识点ID',
  `knowledge_name`       varchar(100) NOT NULL COMMENT '知识点名称',
  `difficulty_level`     int DEFAULT 1 COMMENT '难度等级(1-5)',
  `keyword`              varchar(255) DEFAULT NULL COMMENT '关键词',
  `description`          varchar(500) DEFAULT NULL COMMENT '描述',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`knowledge_point_id`),
  KEY `idx_sc_knowledge_course_parent` (`course_id`, `parent_id`),
  KEY `idx_sc_knowledge_name` (`knowledge_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-知识点表';

-- ----------------------------
-- 10. 学习资源表
-- ----------------------------
DROP TABLE IF EXISTS `sc_resource`;
CREATE TABLE `sc_resource` (
  `resource_id`          bigint NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `resource_name`        varchar(200) NOT NULL COMMENT '资源名称',
  `resource_type`        varchar(30) NOT NULL COMMENT '资源类型(video/pdf/ppt/doc/question)',
  `course_id`            bigint DEFAULT NULL COMMENT '课程ID',
  `chapter_id`           bigint DEFAULT NULL COMMENT '章节ID',
  `uploader_id`          bigint DEFAULT NULL COMMENT '上传者ID',
  `file_url`             varchar(255) DEFAULT NULL COMMENT '文件地址',
  `cover_url`            varchar(255) DEFAULT NULL COMMENT '封面地址',
  `content_text`         longtext COMMENT '抽取文本内容',
  `summary`              varchar(1000) DEFAULT NULL COMMENT '摘要',
  `audit_status`         char(1) DEFAULT '0' COMMENT '审核状态(0待审 1通过 2驳回)',
  `view_count`           int DEFAULT 0 COMMENT '浏览次数',
  `download_count`       int DEFAULT 0 COMMENT '下载次数',
  `favorite_count`       int DEFAULT 0 COMMENT '收藏次数',
  `quality_score`        decimal(5,2) DEFAULT 0.00 COMMENT '质量评分',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`resource_id`),
  KEY `idx_sc_resource_course_type` (`course_id`, `resource_type`),
  KEY `idx_sc_resource_uploader_id` (`uploader_id`),
  KEY `idx_sc_resource_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-学习资源表';

-- ----------------------------
-- 11. 资源标签表
-- ----------------------------
DROP TABLE IF EXISTS `sc_resource_tag`;
CREATE TABLE `sc_resource_tag` (
  `tag_id`               bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name`             varchar(50) NOT NULL COMMENT '标签名称',
  `tag_type`             varchar(30) DEFAULT 'common' COMMENT '标签类型',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `uk_sc_resource_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-资源标签表';

-- ----------------------------
-- 12. 资源标签关系表
-- ----------------------------
DROP TABLE IF EXISTS `sc_resource_tag_rel`;
CREATE TABLE `sc_resource_tag_rel` (
  `id`                   bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id`          bigint NOT NULL COMMENT '资源ID',
  `tag_id`               bigint NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_resource_tag_rel` (`resource_id`, `tag_id`),
  KEY `idx_sc_resource_tag_rel_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-资源标签关系表';

-- ----------------------------
-- 13. 资源检索索引表
-- ----------------------------
DROP TABLE IF EXISTS `sc_resource_index`;
CREATE TABLE `sc_resource_index` (
  `id`                   bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id`          bigint NOT NULL COMMENT '资源ID',
  `keyword_text`         text COMMENT '关键词文本',
  `embedding_id`         varchar(128) DEFAULT NULL COMMENT '向量索引ID',
  `index_status`         char(1) DEFAULT '0' COMMENT '索引状态(0待建 1完成 2失败)',
  `last_index_time`      datetime DEFAULT NULL COMMENT '最后索引时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_resource_index_resource_id` (`resource_id`),
  KEY `idx_sc_resource_index_status` (`index_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-资源检索索引表';

-- ----------------------------
-- 14. 搜索日志表
-- ----------------------------
DROP TABLE IF EXISTS `sc_search_log`;
CREATE TABLE `sc_search_log` (
  `id`                   bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id`              bigint DEFAULT NULL COMMENT '用户ID',
  `keyword`              varchar(255) NOT NULL COMMENT '搜索关键词',
  `search_mode`          varchar(20) DEFAULT 'keyword' COMMENT '检索模式(keyword/semantic)',
  `result_count`         int DEFAULT 0 COMMENT '结果数量',
  `click_resource_id`    bigint DEFAULT NULL COMMENT '点击资源ID',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_sc_search_log_user_time` (`user_id`, `create_time`),
  KEY `idx_sc_search_log_keyword` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-搜索日志表';

-- ----------------------------
-- 15. 学习行为记录表
-- ----------------------------
DROP TABLE IF EXISTS `sc_study_record`;
CREATE TABLE `sc_study_record` (
  `record_id`            bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `course_id`            bigint DEFAULT NULL COMMENT '课程ID',
  `chapter_id`           bigint DEFAULT NULL COMMENT '章节ID',
  `resource_id`          bigint DEFAULT NULL COMMENT '资源ID',
  `behavior_type`        varchar(30) NOT NULL COMMENT '行为类型(view/play/download/favorite/search/answer)',
  `duration_seconds`     int DEFAULT 0 COMMENT '时长(秒)',
  `progress_rate`        decimal(5,2) DEFAULT 0.00 COMMENT '进度百分比',
  `device_type`          varchar(20) DEFAULT NULL COMMENT '设备类型',
  `source_page`          varchar(50) DEFAULT NULL COMMENT '来源页面',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_sc_study_record_user_time` (`user_id`, `create_time`),
  KEY `idx_sc_study_record_course_behavior` (`course_id`, `behavior_type`),
  KEY `idx_sc_study_record_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-学习行为记录表';

-- ----------------------------
-- 16. 学习画像主表
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_profile`;
CREATE TABLE `sc_learning_profile` (
  `profile_id`           bigint NOT NULL AUTO_INCREMENT COMMENT '画像ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `course_id`            bigint DEFAULT NULL COMMENT '课程ID',
  `ability_level`        varchar(20) DEFAULT NULL COMMENT '能力等级',
  `concentration_score`  decimal(5,2) DEFAULT 0.00 COMMENT '专注度得分',
  `active_score`         decimal(5,2) DEFAULT 0.00 COMMENT '活跃度得分',
  `mastery_score`        decimal(5,2) DEFAULT 0.00 COMMENT '掌握度得分',
  `interest_score`       decimal(5,2) DEFAULT 0.00 COMMENT '兴趣度得分',
  `risk_score`           decimal(5,2) DEFAULT 0.00 COMMENT '风险得分',
  `profile_version`      int DEFAULT 1 COMMENT '画像版本',
  `feature_json`         text COMMENT '特征快照JSON',
  `last_calculated_time` datetime DEFAULT NULL COMMENT '最后计算时间',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`profile_id`),
  UNIQUE KEY `uk_sc_learning_profile_user_course` (`user_id`, `course_id`),
  KEY `idx_sc_learning_profile_last_time` (`last_calculated_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-学习画像主表';

-- ----------------------------
-- 17. 学习画像标签表
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_profile_tag`;
CREATE TABLE `sc_learning_profile_tag` (
  `id`                   bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `profile_id`           bigint NOT NULL COMMENT '画像ID',
  `tag_code`             varchar(50) NOT NULL COMMENT '标签编码',
  `tag_name`             varchar(100) NOT NULL COMMENT '标签名称',
  `tag_value`            varchar(255) DEFAULT NULL COMMENT '标签值',
  `tag_weight`           decimal(5,2) DEFAULT 0.00 COMMENT '标签权重',
  PRIMARY KEY (`id`),
  KEY `idx_sc_learning_profile_tag_profile_id` (`profile_id`),
  KEY `idx_sc_learning_profile_tag_code` (`tag_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-学习画像标签表';

-- ----------------------------
-- 18. 个性化推荐表
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_recommendation`;
CREATE TABLE `sc_learning_recommendation` (
  `recommend_id`         bigint NOT NULL AUTO_INCREMENT COMMENT '推荐ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `biz_type`             varchar(30) NOT NULL COMMENT '业务类型(course/resource/question/task/post)',
  `biz_id`               bigint NOT NULL COMMENT '业务ID',
  `recommend_reason`     varchar(500) DEFAULT NULL COMMENT '推荐理由',
  `recommend_score`      decimal(8,4) DEFAULT 0.0000 COMMENT '推荐分',
  `source_type`          varchar(30) DEFAULT NULL COMMENT '来源类型(rule/cf/weakness/ai)',
  `scene_code`           varchar(30) DEFAULT NULL COMMENT '推荐场景(home/exam_before/daily_plan)',
  `expose_status`        char(1) DEFAULT '0' COMMENT '曝光状态(0未曝光 1已曝光)',
  `click_status`         char(1) DEFAULT '0' COMMENT '点击状态(0未点击 1已点击)',
  `feedback_status`      char(1) DEFAULT '0' COMMENT '反馈状态(0无反馈 1喜欢 2不喜欢)',
  `expire_time`          datetime DEFAULT NULL COMMENT '过期时间',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`recommend_id`),
  KEY `idx_sc_learning_recommend_user_scene` (`user_id`, `scene_code`),
  KEY `idx_sc_learning_recommend_user_biz` (`user_id`, `biz_type`, `biz_id`),
  KEY `idx_sc_learning_recommend_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-个性化推荐表';

-- ----------------------------
-- 19. 学习计划表
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_plan`;
CREATE TABLE `sc_learning_plan` (
  `plan_id`              bigint NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `plan_name`            varchar(100) NOT NULL COMMENT '计划名称',
  `goal_desc`            varchar(500) DEFAULT NULL COMMENT '目标描述',
  `start_date`           date DEFAULT NULL COMMENT '开始日期',
  `end_date`             date DEFAULT NULL COMMENT '结束日期',
  `plan_status`          char(1) DEFAULT '0' COMMENT '计划状态(0进行中 1完成 2终止)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`plan_id`),
  KEY `idx_sc_learning_plan_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-学习计划表';

-- ----------------------------
-- 20. 学习计划明细表
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_plan_item`;
CREATE TABLE `sc_learning_plan_item` (
  `item_id`              bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `plan_id`              bigint NOT NULL COMMENT '计划ID',
  `biz_type`             varchar(30) NOT NULL COMMENT '业务类型',
  `biz_id`               bigint NOT NULL COMMENT '业务ID',
  `target_duration`      int DEFAULT 0 COMMENT '目标时长(分钟)',
  `target_count`         int DEFAULT 0 COMMENT '目标次数',
  `complete_status`      char(1) DEFAULT '0' COMMENT '完成状态(0未完成 1已完成)',
  PRIMARY KEY (`item_id`),
  KEY `idx_sc_learning_plan_item_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-学习计划明细表';

-- ----------------------------
-- 21. 学情预警表
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_warning`;
CREATE TABLE `sc_learning_warning` (
  `warning_id`           bigint NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `course_id`            bigint DEFAULT NULL COMMENT '课程ID',
  `warning_type`         varchar(30) DEFAULT NULL COMMENT '预警类型(low_active/score_down/high_risk)',
  `warning_level`        varchar(20) DEFAULT 'LOW' COMMENT '预警等级',
  `warning_content`      varchar(1000) DEFAULT NULL COMMENT '预警内容',
  `suggestion`           varchar(1000) DEFAULT NULL COMMENT '干预建议',
  `process_status`       varchar(20) DEFAULT 'PENDING' COMMENT '处理状态',
  `source_type`          varchar(20) DEFAULT 'rule' COMMENT '来源类型(rule/ai)',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `process_time`         datetime DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`warning_id`),
  KEY `idx_sc_learning_warning_user_type` (`user_id`, `warning_type`),
  KEY `idx_sc_learning_warning_level_status` (`warning_level`, `process_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-学情预警表';

-- ----------------------------
-- 22. 学情报告表
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_report`;
CREATE TABLE `sc_learning_report` (
  `report_id`            bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `report_type`          varchar(20) DEFAULT 'weekly' COMMENT '报告类型(day/week/month/exam)',
  `report_content`       longtext COMMENT '报告内容',
  `report_json`          longtext COMMENT '结构化报告JSON',
  `generate_time`        datetime DEFAULT NULL COMMENT '生成时间',
  PRIMARY KEY (`report_id`),
  KEY `idx_sc_learning_report_user_type` (`user_id`, `report_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-学情报告表';

-- ----------------------------
-- 23. 问答会话表
-- ----------------------------
DROP TABLE IF EXISTS `sc_qa_session`;
CREATE TABLE `sc_qa_session` (
  `session_id`           bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `course_id`            bigint DEFAULT NULL COMMENT '课程ID',
  `session_title`        varchar(200) DEFAULT NULL COMMENT '会话标题',
  `source_type`          varchar(30) DEFAULT 'course' COMMENT '场景类型(general/course/wrong_question)',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1结束)',
  `message_count`        int DEFAULT 0 COMMENT '消息数量',
  `last_message_preview` varchar(255) DEFAULT NULL COMMENT '最后一条消息摘要',
  `last_message_time`    datetime DEFAULT NULL COMMENT '最后消息时间',
  `context_summary`      varchar(1000) DEFAULT NULL COMMENT '会话上下文摘要',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`session_id`),
  KEY `idx_sc_qa_session_user_id` (`user_id`),
  KEY `idx_sc_qa_session_course_id` (`course_id`),
  KEY `idx_sc_qa_session_last_message_time` (`last_message_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-问答会话表';

-- ----------------------------
-- 24. 问答消息表
-- ----------------------------
DROP TABLE IF EXISTS `sc_qa_message`;
CREATE TABLE `sc_qa_message` (
  `message_id`           bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id`           bigint NOT NULL COMMENT '会话ID',
  `role_type`            varchar(20) NOT NULL COMMENT '角色(user/assistant/system)',
  `content_type`         varchar(20) DEFAULT 'text' COMMENT '内容类型(text/markdown/image/mixed)',
  `content`              longtext COMMENT '消息内容',
  `reference_source`     varchar(1000) DEFAULT NULL COMMENT '引用来源',
  `attachment_json`      longtext COMMENT '附件元数据JSON',
  `reply_to_message_id`  bigint DEFAULT NULL COMMENT '回复的消息ID',
  `model_name`           varchar(100) DEFAULT NULL COMMENT '回答使用模型名',
  `reasoning_content`    longtext COMMENT '思考过程内容',
  `token_count`          int DEFAULT 0 COMMENT 'token使用量',
  `latency_ms`           int DEFAULT 0 COMMENT '响应耗时',
  `sensitive_flag`       char(1) DEFAULT '0' COMMENT '敏感标记(0否 1是)',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`message_id`),
  KEY `idx_sc_qa_message_session_time` (`session_id`, `create_time`),
  KEY `idx_sc_qa_message_reply_to` (`reply_to_message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-问答消息表';

-- ----------------------------
-- 25. 问答反馈表
-- ----------------------------
DROP TABLE IF EXISTS `sc_qa_feedback`;
CREATE TABLE `sc_qa_feedback` (
  `id`                   bigint NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `message_id`           bigint NOT NULL COMMENT '消息ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `feedback_type`        varchar(20) NOT NULL COMMENT '反馈类型(helpful/unhelpful)',
  `feedback_score`       tinyint DEFAULT NULL COMMENT '评分(1-5)',
  `issue_tags`           varchar(255) DEFAULT NULL COMMENT '问题标签',
  `feedback_content`     varchar(500) DEFAULT NULL COMMENT '反馈内容',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_sc_qa_feedback_message_id` (`message_id`),
  KEY `idx_sc_qa_feedback_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-问答反馈表';

-- ----------------------------
-- 26. 问答附件表
-- ----------------------------
DROP TABLE IF EXISTS `sc_qa_attachment`;
CREATE TABLE `sc_qa_attachment` (
  `attachment_id`        bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `session_id`           bigint DEFAULT NULL COMMENT '会话ID',
  `message_id`           bigint DEFAULT NULL COMMENT '消息ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `file_name`            varchar(255) NOT NULL COMMENT '系统文件名',
  `original_name`        varchar(255) DEFAULT NULL COMMENT '原始文件名',
  `file_url`             varchar(500) NOT NULL COMMENT '文件访问地址',
  `mime_type`            varchar(100) DEFAULT NULL COMMENT '文件类型',
  `file_size`            bigint DEFAULT 0 COMMENT '文件大小',
  `storage_type`         varchar(20) DEFAULT 'local' COMMENT '存储类型(local/oss)',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1删除)',
  `delete_time`          datetime DEFAULT NULL COMMENT '删除时间',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`attachment_id`),
  KEY `idx_sc_qa_attachment_session_id` (`session_id`),
  KEY `idx_sc_qa_attachment_message_id` (`message_id`),
  KEY `idx_sc_qa_attachment_user_id` (`user_id`),
  KEY `idx_sc_qa_attachment_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-问答附件表';

-- ----------------------------
-- 27. 题库表
-- ----------------------------
DROP TABLE IF EXISTS `sc_question_bank`;
CREATE TABLE `sc_question_bank` (
  `question_id`          bigint NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `course_id`            bigint DEFAULT NULL COMMENT '课程ID',
  `chapter_id`           bigint DEFAULT NULL COMMENT '章节ID',
  `knowledge_point_id`   bigint DEFAULT NULL COMMENT '知识点ID',
  `question_type`        varchar(20) NOT NULL COMMENT '题型(single/multiple/judge/fill/essay)',
  `difficulty_level`     int DEFAULT 1 COMMENT '难度等级(1-5)',
  `stem`                 longtext COMMENT '题干',
  `answer`               longtext COMMENT '答案',
  `analysis`             longtext COMMENT '解析',
  `source`               varchar(100) DEFAULT NULL COMMENT '来源',
  `quality_score`        decimal(5,2) DEFAULT 0.00 COMMENT '质量评分',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`question_id`),
  KEY `idx_sc_question_course_kp` (`course_id`, `knowledge_point_id`),
  KEY `idx_sc_question_type_difficulty` (`question_type`, `difficulty_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-题库表';

-- ----------------------------
-- 28. 题目选项表
-- ----------------------------
DROP TABLE IF EXISTS `sc_question_option`;
CREATE TABLE `sc_question_option` (
  `option_id`            bigint NOT NULL AUTO_INCREMENT COMMENT '选项ID',
  `question_id`          bigint NOT NULL COMMENT '题目ID',
  `option_key`           varchar(10) NOT NULL COMMENT '选项标识',
  `option_content`       varchar(1000) DEFAULT NULL COMMENT '选项内容',
  `is_right`             char(1) DEFAULT '0' COMMENT '是否正确(0否 1是)',
  PRIMARY KEY (`option_id`),
  KEY `idx_sc_question_option_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-题目选项表';

-- ----------------------------
-- 29. 试卷表
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_paper`;
CREATE TABLE `sc_exam_paper` (
  `paper_id`             bigint NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `paper_name`           varchar(100) NOT NULL COMMENT '试卷名称',
  `course_id`            bigint DEFAULT NULL COMMENT '课程ID',
  `paper_type`           varchar(20) DEFAULT 'fixed' COMMENT '试卷类型(fixed/random/adaptive)',
  `total_score`          decimal(8,2) DEFAULT 0.00 COMMENT '总分',
  `duration_minutes`     int DEFAULT 60 COMMENT '考试时长(分钟)',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`paper_id`),
  KEY `idx_sc_exam_paper_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-试卷表';

-- ----------------------------
-- 30. 试卷题目关系表
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_paper_question`;
CREATE TABLE `sc_exam_paper_question` (
  `id`                   bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `paper_id`             bigint NOT NULL COMMENT '试卷ID',
  `question_id`          bigint NOT NULL COMMENT '题目ID',
  `score`                decimal(6,2) DEFAULT 0.00 COMMENT '分值',
  `sort_no`              int DEFAULT 0 COMMENT '排序号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_exam_paper_question` (`paper_id`, `question_id`),
  KEY `idx_sc_exam_paper_question_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-试卷题目关系表';

-- ----------------------------
-- 31. 考试记录表
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_record`;
CREATE TABLE `sc_exam_record` (
  `record_id`            bigint NOT NULL AUTO_INCREMENT COMMENT '考试记录ID',
  `paper_id`             bigint NOT NULL COMMENT '试卷ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `start_time`           datetime DEFAULT NULL COMMENT '开始时间',
  `submit_time`          datetime DEFAULT NULL COMMENT '提交时间',
  `score`                decimal(6,2) DEFAULT 0.00 COMMENT '得分',
  `correct_rate`         decimal(5,2) DEFAULT 0.00 COMMENT '正确率',
  `exam_status`          varchar(20) DEFAULT 'INIT' COMMENT '考试状态(INIT/ONGOING/SUBMITTED)',
  `cheat_flag`           char(1) DEFAULT '0' COMMENT '作弊标记(0否 1是)',
  `analysis_json`        text COMMENT '考试分析JSON',
  PRIMARY KEY (`record_id`),
  KEY `idx_sc_exam_record_user_paper` (`user_id`, `paper_id`),
  KEY `idx_sc_exam_record_submit_time` (`submit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-考试记录表';

-- ----------------------------
-- 32. 作答明细表
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_answer`;
CREATE TABLE `sc_exam_answer` (
  `answer_id`            bigint NOT NULL AUTO_INCREMENT COMMENT '作答ID',
  `record_id`            bigint NOT NULL COMMENT '考试记录ID',
  `question_id`          bigint NOT NULL COMMENT '题目ID',
  `user_answer`          longtext COMMENT '用户答案',
  `is_correct`           char(1) DEFAULT '0' COMMENT '是否正确(0否 1是)',
  `score`                decimal(6,2) DEFAULT 0.00 COMMENT '得分',
  `knowledge_point_id`   bigint DEFAULT NULL COMMENT '知识点ID',
  PRIMARY KEY (`answer_id`),
  KEY `idx_sc_exam_answer_record_id` (`record_id`),
  KEY `idx_sc_exam_answer_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-作答明细表';

-- ----------------------------
-- 33. 错题本表
-- ----------------------------
DROP TABLE IF EXISTS `sc_wrong_question_book`;
CREATE TABLE `sc_wrong_question_book` (
  `id`                   bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`              bigint NOT NULL COMMENT '用户ID',
  `question_id`          bigint NOT NULL COMMENT '题目ID',
  `course_id`            bigint DEFAULT NULL COMMENT '课程ID',
  `wrong_count`          int DEFAULT 1 COMMENT '错误次数',
  `last_wrong_time`      datetime DEFAULT NULL COMMENT '最后做错时间',
  `mastery_status`       char(1) DEFAULT '0' COMMENT '掌握状态(0未掌握 1已掌握)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_wrong_question_user_question` (`user_id`, `question_id`),
  KEY `idx_sc_wrong_question_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-错题本表';

-- ----------------------------
-- 34. 自适应规则表
-- ----------------------------
DROP TABLE IF EXISTS `sc_adaptive_rule`;
CREATE TABLE `sc_adaptive_rule` (
  `rule_id`              bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `course_id`            bigint NOT NULL COMMENT '课程ID',
  `ability_range`        varchar(50) DEFAULT NULL COMMENT '能力范围',
  `difficulty_strategy`  varchar(100) DEFAULT NULL COMMENT '难度策略',
  `question_count`       int DEFAULT 10 COMMENT '出题数量',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`rule_id`),
  KEY `idx_sc_adaptive_rule_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-自适应规则表';

-- ----------------------------
-- 35. AI模型配置表
-- ----------------------------
DROP TABLE IF EXISTS `sc_ai_model_config`;
CREATE TABLE `sc_ai_model_config` (
  `model_id`             bigint NOT NULL AUTO_INCREMENT COMMENT '模型ID',
  `model_name`           varchar(100) NOT NULL COMMENT '模型名称',
  `provider`             varchar(50) NOT NULL COMMENT '服务提供商',
  `base_url`             varchar(255) DEFAULT NULL COMMENT '接口地址',
  `api_key`              varchar(255) DEFAULT NULL COMMENT '密钥(建议加密存储)',
  `model_type`           varchar(20) DEFAULT 'chat' COMMENT '模型类型(chat/embedding/rerank)',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-AI模型配置表';

-- ----------------------------
-- 36. Prompt模板表
-- ----------------------------
DROP TABLE IF EXISTS `sc_ai_prompt_template`;
CREATE TABLE `sc_ai_prompt_template` (
  `template_id`          bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_code`        varchar(50) NOT NULL COMMENT '模板编码',
  `template_name`        varchar(100) NOT NULL COMMENT '模板名称',
  `biz_type`             varchar(30) DEFAULT NULL COMMENT '业务类型',
  `prompt_content`       longtext COMMENT '模板内容',
  `version`              int DEFAULT 1 COMMENT '版本号',
  `status`               char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by`            varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  `update_by`            varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time`          datetime DEFAULT NULL COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `uk_sc_ai_prompt_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-Prompt模板表';

-- ----------------------------
-- 37. AI任务日志表
-- ----------------------------
DROP TABLE IF EXISTS `sc_ai_task_log`;
CREATE TABLE `sc_ai_task_log` (
  `task_id`              bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `biz_type`             varchar(30) DEFAULT NULL COMMENT '业务类型',
  `biz_id`               bigint DEFAULT NULL COMMENT '业务ID',
  `model_id`             bigint DEFAULT NULL COMMENT '模型ID',
  `request_payload`      longtext COMMENT '请求报文',
  `response_payload`     longtext COMMENT '响应报文',
  `token_used`           int DEFAULT 0 COMMENT '使用token数',
  `task_status`          varchar(20) DEFAULT 'SUCCESS' COMMENT '任务状态',
  `error_msg`            varchar(1000) DEFAULT NULL COMMENT '错误信息',
  `duration_ms`          int DEFAULT 0 COMMENT '耗时毫秒',
  `create_time`          datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`task_id`),
  KEY `idx_sc_ai_task_log_biz` (`biz_type`, `biz_id`),
  KEY `idx_sc_ai_task_log_model_id` (`model_id`),
  KEY `idx_sc_ai_task_log_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-AI任务日志表';

SET FOREIGN_KEY_CHECKS = 1;
