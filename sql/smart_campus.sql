/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : smart_campus

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 24/03/2026 12:41:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob NULL COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Blob类型的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '日历信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Cron类型的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '已触发的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务组名',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob NULL COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '存储的悲观锁信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '暂停的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '调度器状态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '简单触发器的信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int NULL DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int NULL DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint NULL DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint NULL DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13, 4) NULL DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13, 4) NULL DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '同步机制的行锁表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint NULL DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint NULL DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int NULL DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint NULL DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint NULL DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob NULL COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  INDEX `sched_name`(`sched_name` ASC, `job_name` ASC, `job_group` ASC) USING BTREE,
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '触发器详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for sc_adaptive_rule
-- ----------------------------
DROP TABLE IF EXISTS `sc_adaptive_rule`;
CREATE TABLE `sc_adaptive_rule`  (
  `rule_id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `ability_range` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '能力范围',
  `difficulty_strategy` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '难度策略',
  `question_count` int NULL DEFAULT 10 COMMENT '出题数量',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`rule_id`) USING BTREE,
  INDEX `idx_sc_adaptive_rule_course_id`(`course_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-自适应规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_adaptive_rule
-- ----------------------------

-- ----------------------------
-- Table structure for sc_ai_model_config
-- ----------------------------
DROP TABLE IF EXISTS `sc_ai_model_config`;
CREATE TABLE `sc_ai_model_config`  (
  `model_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模型ID',
  `model_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模型名称',
  `provider` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务提供商',
  `base_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接口地址',
  `api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密钥(建议加密存储)',
  `model_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'chat' COMMENT '模型类型(chat/embedding/rerank)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '是否默认模型(0否 1是)',
  `priority` int NULL DEFAULT 0 COMMENT '优先级，越大越优先',
  `biz_types` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用业务类型，逗号分隔，如 qa,report,recommend,common',
  `model_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '实际调用模型编码',
  `reasoning_model_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '深度思考模型编码',
  `vision_model_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视觉模型编码',
  `support_stream` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否支持流式(0否 1是)',
  `support_reasoning` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '是否支持深度思考(0否 1是)',
  `support_vision` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '是否支持图片理解(0否 1是)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`model_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-AI模型配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_ai_model_config
-- ----------------------------
INSERT INTO `sc_ai_model_config` VALUES (1, 'DeepSeek', 'deepseek', 'https://api.deepseek.com', 'sk-ea608da4de674be3a85f209dee2d2638', 'chat', '0', '1', 0, '', 'deepseek-chat', 'deepseek-reasoner', NULL, '1', '1', '0', 'admin', '2026-03-11 23:56:33', 'admin', '2026-03-17 11:03:00', NULL);
INSERT INTO `sc_ai_model_config` VALUES (4, '阿里百炼 Qwen', 'qwen', 'https://dashscope.aliyuncs.com/compatible-mode/v1', 'sk-f6327c2e2d2b484b88f0a87e626d87ea', 'chat', '0', '0', 0, '', 'qwen3.5-plus', '', '', '1', '1', '1', 'admin', '2026-03-17 11:02:52', 'admin', '2026-03-17 11:03:19', NULL);

-- ----------------------------
-- Table structure for sc_ai_prompt_template
-- ----------------------------
DROP TABLE IF EXISTS `sc_ai_prompt_template`;
CREATE TABLE `sc_ai_prompt_template`  (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板编码',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
  `biz_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型',
  `prompt_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '模板内容',
  `version` int NULL DEFAULT 1 COMMENT '版本号',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`template_id`) USING BTREE,
  UNIQUE INDEX `uk_sc_ai_prompt_template_code`(`template_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-Prompt模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_ai_prompt_template
-- ----------------------------

-- ----------------------------
-- Table structure for sc_ai_task_log
-- ----------------------------
DROP TABLE IF EXISTS `sc_ai_task_log`;
CREATE TABLE `sc_ai_task_log`  (
  `task_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `biz_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型',
  `biz_id` bigint NULL DEFAULT NULL COMMENT '业务ID',
  `model_id` bigint NULL DEFAULT NULL COMMENT '模型ID',
  `request_payload` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求报文',
  `response_payload` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '响应报文',
  `token_used` int NULL DEFAULT 0 COMMENT '使用token数',
  `task_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'SUCCESS' COMMENT '任务状态',
  `error_msg` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误信息',
  `duration_ms` int NULL DEFAULT 0 COMMENT '耗时毫秒',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`task_id`) USING BTREE,
  INDEX `idx_sc_ai_task_log_biz`(`biz_type` ASC, `biz_id` ASC) USING BTREE,
  INDEX `idx_sc_ai_task_log_model_id`(`model_id` ASC) USING BTREE,
  INDEX `idx_sc_ai_task_log_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 97 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-AI任务日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_ai_task_log
-- ----------------------------
INSERT INTO `sc_ai_task_log` VALUES (53, 'qa', 4, 4, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":true,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园自主学习助手，请围绕课程学习、资源推荐、知识点理解与考试答疑做出简洁、专业的回答。\"},{\"role\":\"user\",\"content\":\"https://chipreal.com/wp-content/uploads/2025/12/企业微信截图_17660393125819.png我想得到图片里面的信息\"}],\"model\":\"qwen3.5-plus\",\"stream_options\":{\"include_usage\":true}}', 'data: {\"choices\":[{\"delta\":{\"content\":\"您好\",\"role\":\"assistant\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"！作为智慧校园\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"自主学习助手，我\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"**\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"无法直接访问或\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"解析外部链接**\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"（包括您提供的\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" chip\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"real.com 图片\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"链接），这是\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"为了确保您的数据安全以及\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"遵守网络访问\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"限制。\\n\\n为了\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"帮您获取图片中的\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"关键信息并转化为\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"学习资源\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，建议您采取以下\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"操作\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"：\\n\\n1.\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"  **截图\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"上传**：直接将\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"图片截图\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"并粘贴到我们的\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"对话窗口\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"中。\\n2\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\".  **文字\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"描述**：简要\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"告诉我图片中包含\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"的内容（例如：\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"是一道\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"具体的数学题、\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"一段\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"代码、一个化学\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"方程式\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，还是课程通知\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"？）。\\n\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"一旦您提供内容\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，我将立即为您提供\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"：\\n*  \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" 📚 **知识点解析\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"**：拆解\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"核心概念。\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"*  \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" 💡 **解题\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"思路/\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"资源推荐**：\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"提供\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"相关学习资料或解题\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"步骤。\\n*\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"   ❓ **\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"考试\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"答疑**：针对\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"该内容的\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"常见考点进行预测\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"和\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"解答。\\n\\n请\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"随时\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"发送图片或文字描述\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"我们立刻开始学习\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"！\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[{\"finish_reason\":\"stop\",\"delta\":{\"content\":\"\"},\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: {\"choices\":[],\"object\":\"chat.completion.chunk\",\"usage\":{\"prompt_tokens\":83,\"completion_tokens\":189,\"total_tokens\":272,\"completion_tokens_details\":{\"text_tokens\":189},\"prompt_tokens_details\":{\"text_tokens\":83}},\"created\":1773717798,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-7d58fb75-f76e-95a4-b66e-fe8707952df8\"}\n\ndata: [DONE]\n', 272, 'SUCCESS', NULL, 4542, '2026-03-17 11:23:22');
INSERT INTO `sc_ai_task_log` VALUES (54, 'qa', 4, 4, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":true,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园自主学习助手，请围绕课程学习、资源推荐、知识点理解与考试答疑做出简洁、专业的回答。\"},{\"role\":\"user\",\"content\":\"你是千问吗\"}],\"model\":\"qwen3.5-plus\",\"stream_options\":{\"include_usage\":true}}', 'data: {\"choices\":[{\"delta\":{\"content\":\"您好\",\"role\":\"assistant\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"我是**智慧校园\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"自主学习助手**。\\n\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"虽然我的底层\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"技术由通义\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"千问\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"（Qwen）\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"提供支持\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，但在当前场景\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"下，我\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"专注于为您提供以下教育\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"服务：\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"*   **课程\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"学习**：规划\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"学习路径，解析\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"课程大纲\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"。\\n*  \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" **资源推荐**\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"：匹配教材、\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"论文\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"及多媒体学习资料。\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"\\n*  \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" **知识点理解**\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"：拆解\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"复杂概念，提供\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"通俗解释\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"与案例。\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"*   **考试\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"答疑**：梳理\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"考点\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，解答习题，\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"辅助\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"备考复习。\\n\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"请问您\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"今天需要哪方面的\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"帮助\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"？\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[{\"finish_reason\":\"stop\",\"delta\":{\"content\":\"\"},\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: {\"choices\":[],\"object\":\"chat.completion.chunk\",\"usage\":{\"prompt_tokens\":46,\"completion_tokens\":114,\"total_tokens\":160,\"completion_tokens_details\":{\"text_tokens\":114},\"prompt_tokens_details\":{\"text_tokens\":46}},\"created\":1773720421,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-66cb5bc9-f337-94d1-81fd-d617bea6b01d\"}\n\ndata: [DONE]\n', 160, 'SUCCESS', NULL, 2694, '2026-03-17 12:07:04');
INSERT INTO `sc_ai_task_log` VALUES (55, 'qa', 4, 4, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":true,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园自主学习助手，请围绕课程学习、资源推荐、知识点理解与考试答疑做出简洁、专业的回答。\"},{\"role\":\"user\",\"content\":\"请帮我生成复习计划\"}],\"model\":\"qwen3.5-plus\",\"stream_options\":{\"include_usage\":true}}', 'data: {\"choices\":[{\"delta\":{\"content\":\"你好\",\"role\":\"assistant\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"！作为\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"智慧校园自主学习助手\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，我很\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"乐意为你制定一份\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"高效的复习计划。\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"\\n\\n由于我暂时\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"不了解你的具体\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"**课程名称**\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"、**考试\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"日期**以及**\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"当前\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"掌握程度**，\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"为了给你\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"提供最精准的方案，\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"请你\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"补充以下信息：\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"1. **复习\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"科目**（例如\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"：高等数学、\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"大学英语\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"、Java 程序设计\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"等\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"）\\n2.\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" **距离\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"考试的时间**（\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"例如\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"：3 天\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"、2\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" 周）\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"3. **每日\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"可用复习时长**\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"4. **目前的\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"薄弱环节**（可选\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，例如：公式\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"记不住、代码\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"逻辑不清）\\n\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"---\\n\\n###\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" 📅 \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"通用高效\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"复习框架（参考\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"模板）\\n\\n在你\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"提供详细信息前，\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"你可以\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"先参考这套基于\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"**\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"艾宾浩斯\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"遗忘曲线**和\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"**主动回忆法\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"**设计的\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"通用三阶段复习\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"策略：\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"\\n\\n#### 第一阶段\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"：知识重构\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"与梳理（占总\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"时间 \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"40%）\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"\\n*   **\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"目标**：建立\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"知识\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"体系，扫清\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"盲点。\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"*   **行动\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"**：\\n   \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" *   **绘制\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"思维导图**：不\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"看书，凭记忆\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"画出\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"章节脉络，再\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"对照教材\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"查漏补缺。\\n   \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" *   **核心\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"概念复盘**：\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"针对\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"定义、定理、\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"公式进行“\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"费曼技巧”\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"自\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"测（尝试用\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"大白\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"话讲给自己听\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"）。\\n   \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" *   **资源\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"推荐**：利用\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"校园图书馆数据库或\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" MO\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"OC 平台观看\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"该\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"课程的“期末串\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"讲”视频\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"。\\n\\n#### \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"第二阶段：真题\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"实战与专项突破\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"（\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"占总时间 4\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"0%）\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"*   **目标\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"**：熟悉\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"题型，提升解题\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"速度与\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"准确率。\\n*\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"   **行动\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"**：\\n   \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" *   **限时\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"模拟**：选取\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"近 3-\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"5 年真题\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，完全\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"按照考试时间进行模拟\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"。\\n   \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" *   **错题\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"归因**\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"：建立错题本\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，分析\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"是“知识点遗忘\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"”、“\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"审题错误”还是\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"“逻辑\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"漏洞”，并针对性\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"回\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"看教材对应章节\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"。\\n   \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" *   **高频\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"考点聚焦\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"**：统计真题\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"中出现频率最高的知识点\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，进行深度挖掘\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"。\\n\\n####\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" 第三阶段：\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"全真模拟与\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"心态调整（占总\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"时间\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" 20%\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"）\\n*  \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" **目标**：\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"保持手感，调整\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"生物钟。\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"*  \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" **行动**：\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"\\n    *  \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" **回归基础**\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"：不再做\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"难题，快速浏览\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"笔记\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"、公式表和错题\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"本。\\n   \"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" *   **全\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"真演练\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"**：在考试\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"相同\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"的时间段进行一次最后\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"的热\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"身模拟。\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"    *   **\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"答疑准备**：\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"整理最后\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"遗留的 3\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"-\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"5 个疑难\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"问题，向\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"老师或同学请教\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"。\\n\\n---\\n\\n\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"**👉\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\" 请告诉我你的\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"具体科目和时间安排\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"，我将立即\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"为你生成一份包含\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"每日\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"任务清单、推荐\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"资源链接\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"及重点难点解析\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"的定制化\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"复习计划。**\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[{\"finish_reason\":\"stop\",\"delta\":{\"content\":\"\"},\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: {\"choices\":[],\"object\":\"chat.completion.chunk\",\"usage\":{\"prompt_tokens\":47,\"completion_tokens\":564,\"total_tokens\":611,\"completion_tokens_details\":{\"text_tokens\":564},\"prompt_tokens_details\":{\"text_tokens\":47}},\"created\":1773726082,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-89999ce1-b0be-9421-8d3e-50f95f038df4\"}\n\ndata: [DONE]\n', 611, 'SUCCESS', NULL, 10923, '2026-03-17 13:41:32');
INSERT INTO `sc_ai_task_log` VALUES (56, 'teaching_admin', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园后台的教学基础配置助手，负责帮助管理员生成和优化年级管理的表单配置。你必须只返回 JSON，不允许返回 markdown，不允许解释。JSON 结构固定为：{\\\"assistantTitle\\\":\\\"...\\\",\\\"formDraft\\\":{},\\\"highlights\\\":[\\\"...\\\"],\\\"warnings\\\":[\\\"...\\\"],\\\"tips\\\":[\\\"...\\\"]}。formDraft 只允许包含这些字段：gradeName,schoolYear,status,remark。如果某个字段没有把握，可以不返回该字段。状态字段 status 默认使用 0 表示正常、1 表示停用。回答要适合中国校园教务场景，配置清晰、易维护、能直接落地。\"},{\"role\":\"user\",\"content\":\"{\\\"moduleKey\\\":\\\"grade\\\",\\\"moduleLabel\\\":\\\"年级管理\\\",\\\"actionType\\\":\\\"generate\\\",\\\"actionLabel\\\":\\\"生成新配置\\\",\\\"userInstruction\\\":\\\"\\\",\\\"fieldDescriptions\\\":[\\\"gradeName: 年级名称，如高一、高二\\\",\\\"schoolYear: 学年，如2025-2026\\\",\\\"status: 0正常 1停用\\\",\\\"remark: 补充说明\\\"],\\\"currentForm\\\":{\\\"gradeName\\\":\\\"\\\",\\\"schoolYear\\\":\\\"\\\",\\\"status\\\":\\\"0\\\",\\\"remark\\\":\\\"\\\"},\\\"selectedRows\\\":[],\\\"availableOptions\\\":{},\\\"goal\\\":\\\"请返回适合直接回填表单的 formDraft，并补充 highlights、warnings、tips。\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"11bf1df0-c3c5-4518-a0a8-6b1421fb826a\",\"object\":\"chat.completion\",\"created\":1773728651,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n    \\\"assistantTitle\\\": \\\"年级管理配置助手\\\",\\n    \\\"formDraft\\\": {\\n        \\\"gradeName\\\": \\\"\\\",\\n        \\\"schoolYear\\\": \\\"\\\",\\n        \\\"status\\\": \\\"0\\\",\\n        \\\"remark\\\": \\\"\\\"\\n    },\\n    \\\"highlights\\\": [\\\"年级名称建议使用标准学段+序号，如\'高一\'、\'高二\'，便于统一管理。\\\", \\\"学年格式推荐\'起始年份-结束年份\'，如\'2025-2026\'，与教务系统学年设置保持一致。\\\", \\\"状态默认设为\'正常(0)\'，新建年级通常需要立即启用。\\\"],\\n    \\\"warnings\\\": [\\\"请确保年级名称在全校范围内唯一，避免与已有年级重复。\\\", \\\"学年信息一旦保存，后续关联排课、分班等操作将依赖此数据，请谨慎填写。\\\", \\\"若将状态设为\'停用(1)\'，该年级将无法用于后续的新生分班、课程安排等操作。\\\"],\\n    \\\"tips\\\": [\\\"年级名称可结合学校特色，如\'高一年级\'、\'国际部十年级\'。\\\", \\\"学年通常指秋季开学到次年暑假结束的周期。\\\", \\\"备注可用于记录年级负责人、特殊教学要求等信息。\\\", \\\"提交前，请核对年级名称和学年，确保与学校实际教学规划一致。\\\"]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":271,\"completion_tokens\":277,\"total_tokens\":548,\"prompt_tokens_details\":{\"cached_tokens\":0},\"prompt_cache_hit_tokens\":0,\"prompt_cache_miss_tokens\":271},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache\"}', 548, 'SUCCESS', NULL, 8416, '2026-03-17 14:24:21');
INSERT INTO `sc_ai_task_log` VALUES (57, 'teaching_admin', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园后台的教学基础配置助手，负责帮助管理员生成和优化学年学期的表单配置。你必须只返回 JSON，不允许返回 markdown，不允许解释。JSON 结构固定为：{\\\"assistantTitle\\\":\\\"...\\\",\\\"formDraft\\\":{},\\\"highlights\\\":[\\\"...\\\"],\\\"warnings\\\":[\\\"...\\\"],\\\"tips\\\":[\\\"...\\\"]}。formDraft 只允许包含这些字段：termName,schoolYear,termCode,sortOrder,isCurrent,status,remark。如果某个字段没有把握，可以不返回该字段。状态字段 status 默认使用 0 表示正常、1 表示停用。回答要适合中国校园教务场景，配置清晰、易维护、能直接落地。\"},{\"role\":\"user\",\"content\":\"{\\\"moduleKey\\\":\\\"schoolTerm\\\",\\\"moduleLabel\\\":\\\"学年学期\\\",\\\"actionType\\\":\\\"generate\\\",\\\"actionLabel\\\":\\\"生成新配置\\\",\\\"userInstruction\\\":\\\"\\\",\\\"fieldDescriptions\\\":[\\\"termName: 学期名称\\\",\\\"schoolYear: 学年，如2025-2026\\\",\\\"termCode: 学期编码，如2025-2026-1\\\",\\\"sortOrder: 排序\\\",\\\"isCurrent: 1当前学期 0非当前\\\",\\\"status: 0正常 1停用\\\",\\\"remark: 备注\\\"],\\\"currentForm\\\":{\\\"termName\\\":\\\"\\\",\\\"schoolYear\\\":\\\"\\\",\\\"termCode\\\":\\\"\\\",\\\"sortOrder\\\":0,\\\"isCurrent\\\":\\\"0\\\",\\\"status\\\":\\\"0\\\",\\\"remark\\\":\\\"\\\"},\\\"selectedRows\\\":[],\\\"availableOptions\\\":{},\\\"goal\\\":\\\"请返回适合直接回填表单的 formDraft，并补充 highlights、warnings、tips。\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"e45fb02b-447c-4f94-bcb3-4a2d275cb1cc\",\"object\":\"chat.completion\",\"created\":1773732106,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n    \\\"assistantTitle\\\": \\\"新学年学期配置助手\\\",\\n    \\\"formDraft\\\": {\\n        \\\"termName\\\": \\\"2025-2026学年第一学期\\\",\\n        \\\"schoolYear\\\": \\\"2025-2026\\\",\\n        \\\"termCode\\\": \\\"2025-2026-1\\\",\\n        \\\"sortOrder\\\": 1,\\n        \\\"isCurrent\\\": \\\"0\\\",\\n        \\\"status\\\": \\\"0\\\",\\n        \\\"remark\\\": \\\"新学年第一学期，请根据实际开学日期调整相关教学安排。\\\"\\n    },\\n    \\\"highlights\\\": [\\\"已根据当前年份自动生成2025-2026学年第一学期配置\\\", \\\"学期编码遵循\'学年-学期序号\'标准格式，便于系统识别和排序\\\", \\\"排序值设为1，通常作为新学年的起始学期\\\"],\\n    \\\"warnings\\\": [\\\"请务必确认\'2025-2026\'是否为正确的目标学年\\\", \\\"当前学期标记(isCurrent)默认为\'0\'，启用后需确保系统中只有一个当前学期\\\", \\\"学期名称和编码一旦创建，修改可能影响已关联的课程、成绩等数据\\\"],\\n    \\\"tips\\\": [\\\"建议在学期开始前完成配置，以便提前安排课表、选课等教务活动\\\", \\\"若需配置多个学期，可按顺序递增sortOrder（如：1, 2, 3...）\\\", \\\"备注栏可记录学期特殊事项，如\'国庆调课\'、\'寒假安排\'等\\\"]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":324,\"completion_tokens\":303,\"total_tokens\":627,\"prompt_tokens_details\":{\"cached_tokens\":0},\"prompt_cache_hit_tokens\":0,\"prompt_cache_miss_tokens\":324},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache\"}', 627, 'SUCCESS', NULL, 8728, '2026-03-17 15:21:56');
INSERT INTO `sc_ai_task_log` VALUES (58, 'teaching_admin', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园后台的教学基础配置助手，负责帮助管理员生成和优化排课表的表单配置。你必须只返回 JSON，不允许返回 markdown，不允许解释。JSON 结构固定为：{\\\"assistantTitle\\\":\\\"...\\\",\\\"formDraft\\\":{},\\\"highlights\\\":[\\\"...\\\"],\\\"warnings\\\":[\\\"...\\\"],\\\"tips\\\":[\\\"...\\\"]}。formDraft 只允许包含这些字段：termId,classCourseId,weekDay,startSection,endSection,classroom,weeksText,status,remark。如果某个字段没有把握，可以不返回该字段。状态字段 status 默认使用 0 表示正常、1 表示停用。回答要适合中国校园教务场景，配置清晰、易维护、能直接落地。\"},{\"role\":\"user\",\"content\":\"{\\\"moduleKey\\\":\\\"courseSchedule\\\",\\\"moduleLabel\\\":\\\"排课表\\\",\\\"actionType\\\":\\\"generate\\\",\\\"actionLabel\\\":\\\"生成新配置\\\",\\\"userInstruction\\\":\\\"\\\",\\\"fieldDescriptions\\\":[\\\"termId: 学期ID\\\",\\\"classCourseId: 班级课程ID\\\",\\\"weekDay: 星期1-7\\\",\\\"startSection: 开始节次\\\",\\\"endSection: 结束节次\\\",\\\"classroom: 教室\\\",\\\"weeksText: 周次文本，如1-16周\\\",\\\"status: 0正常 1停用\\\",\\\"remark: 备注\\\"],\\\"currentForm\\\":{\\\"weekDay\\\":1,\\\"startSection\\\":1,\\\"endSection\\\":2,\\\"classroom\\\":\\\"\\\",\\\"weeksText\\\":\\\"1-16周\\\",\\\"status\\\":\\\"0\\\",\\\"remark\\\":\\\"\\\"},\\\"selectedRows\\\":[],\\\"availableOptions\\\":{\\\"termOptions\\\":[],\\\"classCourseOptions\\\":[]},\\\"goal\\\":\\\"请返回适合直接回填表单的 formDraft，并补充 highlights、warnings、tips。\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"9ef4feaf-0461-4da1-8b87-7a5758f6632f\",\"object\":\"chat.completion\",\"created\":1773732827,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n    \\\"assistantTitle\\\": \\\"排课表基础配置助手\\\",\\n    \\\"formDraft\\\": {\\n        \\\"termId\\\": \\\"\\\",\\n        \\\"classCourseId\\\": \\\"\\\",\\n        \\\"weekDay\\\": 1,\\n        \\\"startSection\\\": 1,\\n        \\\"endSection\\\": 2,\\n        \\\"classroom\\\": \\\"\\\",\\n        \\\"weeksText\\\": \\\"1-16周\\\",\\n        \\\"status\\\": 0,\\n        \\\"remark\\\": \\\"\\\"\\n    },\\n    \\\"highlights\\\": [\\\"已预设常用周次范围（1-16周），覆盖大部分学期长度\\\", \\\"状态默认设为正常（0），便于快速启用新课程\\\", \\\"节次范围预设为1-2节，符合上午第一节常见安排\\\"],\\n    \\\"warnings\\\": [\\\"学期ID和班级课程ID为必填项，请务必从下拉选项中选择\\\", \\\"周次文本格式需严格遵循\'X-X周\'或\'X,X,X周\'等规范，否则可能影响排课计算\\\", \\\"开始节次不能大于结束节次，且通常为1-12之间的整数\\\"],\\n    \\\"tips\\\": [\\\"教室字段可填写具体教室编号（如\'逸夫楼201\'）或类型（如\'多媒体教室\'），建议统一命名规范\\\", \\\"备注可用于记录特殊安排（如\'单周上课\'、\'调至xxx教室\'），便于后续查询\\\", \\\"若为连续多节课程（如3-4节），只需设置开始节次为3，结束节次为4即可\\\"]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":356,\"completion_tokens\":312,\"total_tokens\":668,\"prompt_tokens_details\":{\"cached_tokens\":0},\"prompt_cache_hit_tokens\":0,\"prompt_cache_miss_tokens\":356},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache\"}', 668, 'SUCCESS', NULL, 8473, '2026-03-17 15:33:57');
INSERT INTO `sc_ai_task_log` VALUES (59, 'teaching_admin', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园后台的教学基础配置助手，负责帮助管理员生成和优化学年学期的表单配置。你必须只返回 JSON，不允许返回 markdown，不允许解释。JSON 结构固定为：{\\\"assistantTitle\\\":\\\"...\\\",\\\"formDraft\\\":{},\\\"highlights\\\":[\\\"...\\\"],\\\"warnings\\\":[\\\"...\\\"],\\\"tips\\\":[\\\"...\\\"]}。formDraft 只允许包含这些字段：termName,schoolYear,termCode,sortOrder,isCurrent,status,remark。如果某个字段没有把握，可以不返回该字段。状态字段 status 默认使用 0 表示正常、1 表示停用。回答要适合中国校园教务场景，配置清晰、易维护、能直接落地。\"},{\"role\":\"user\",\"content\":\"{\\\"moduleKey\\\":\\\"schoolTerm\\\",\\\"moduleLabel\\\":\\\"学年学期\\\",\\\"actionType\\\":\\\"generate\\\",\\\"actionLabel\\\":\\\"生成新配置\\\",\\\"userInstruction\\\":\\\"\\\",\\\"fieldDescriptions\\\":[\\\"termName: 学期名称\\\",\\\"schoolYear: 学年，如2025-2026\\\",\\\"termCode: 学期编码，如2025-2026-1\\\",\\\"sortOrder: 排序\\\",\\\"isCurrent: 1当前学期 0非当前\\\",\\\"status: 0正常 1停用\\\",\\\"remark: 备注\\\"],\\\"currentForm\\\":{\\\"termName\\\":\\\"\\\",\\\"schoolYear\\\":\\\"\\\",\\\"termCode\\\":\\\"\\\",\\\"sortOrder\\\":0,\\\"startDate\\\":\\\"\\\",\\\"endDate\\\":\\\"\\\",\\\"totalWeeks\\\":20,\\\"isCurrent\\\":\\\"0\\\",\\\"status\\\":\\\"0\\\",\\\"remark\\\":\\\"\\\"},\\\"selectedRows\\\":[],\\\"availableOptions\\\":{},\\\"goal\\\":\\\"请返回适合直接回填表单的 formDraft，并补充 highlights、warnings、tips。\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"0ad510fa-3193-426f-b0c3-d66056cacf39\",\"object\":\"chat.completion\",\"created\":1773736146,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"{\\n  \\\"assistantTitle\\\": \\\"新学年学期配置助手\\\",\\n  \\\"formDraft\\\": {\\n    \\\"termName\\\": \\\"2025-2026学年第一学期\\\",\\n    \\\"schoolYear\\\": \\\"2025-2026\\\",\\n    \\\"termCode\\\": \\\"2025-2026-1\\\",\\n    \\\"sortOrder\\\": 1,\\n    \\\"isCurrent\\\": 1,\\n    \\\"status\\\": 0,\\n    \\\"remark\\\": \\\"新学期初始配置，请根据实际教学日历调整起止日期和总周数。\\\"\\n  },\\n  \\\"highlights\\\": [\\n    \\\"已生成标准格式的学期名称、学年和编码，符合教务系统规范。\\\",\\n    \\\"将当前学期标记为“是”，便于系统自动识别进行中的教学活动。\\\",\\n    \\\"设置了默认排序为1，确保在列表顶部显示。\\\"\\n  ],\\n  \\\"warnings\\\": [\\n    \\\"当前配置未包含起止日期和总周数，需在表单中手动补充，否则排课等功能可能受限。\\\",\\n    \\\"若本学年已有其他学期，请检查并调整sortOrder值，避免排序冲突。\\\",\\n    \\\"确认当前学期唯一性：启用新当前学期前，请确保原当前学期已标记为非当前。\\\"\\n  ],\\n  \\\"tips\\\": [\\n    \\\"学期编码建议统一为“学年-序号”格式，如2025-2026-1、2025-2026-2，便于数据筛选。\\\",\\n    \\\"备注字段可记录关键信息，如“高考备考学期”、“实习实践周安排”等。\\\",\\n    \\\"若为毕业年级学期，可在termName中注明，如“2025-2026学年第一学期（高三年级）”。\\\"\\n  ]\\n}\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":338,\"completion_tokens\":354,\"total_tokens\":692,\"prompt_tokens_details\":{\"cached_tokens\":256},\"prompt_cache_hit_tokens\":256,\"prompt_cache_miss_tokens\":82},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache\"}', 692, 'SUCCESS', NULL, 9851, '2026-03-17 16:29:17');
INSERT INTO `sc_ai_task_log` VALUES (60, 'teaching_admin', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园后台的教学基础配置助手，负责帮助管理员生成和优化排课表的表单配置。你必须只返回 JSON，不允许返回 markdown，不允许解释。JSON 结构固定为：{\\\"assistantTitle\\\":\\\"...\\\",\\\"formDraft\\\":{},\\\"highlights\\\":[\\\"...\\\"],\\\"warnings\\\":[\\\"...\\\"],\\\"tips\\\":[\\\"...\\\"]}。formDraft 只允许包含这些字段：termId,classCourseId,weekDay,startSection,endSection,classroom,weeksText,status,remark。如果某个字段没有把握，可以不返回该字段。状态字段 status 默认使用 0 表示正常、1 表示停用。回答要适合中国校园教务场景，配置清晰、易维护、能直接落地。\"},{\"role\":\"user\",\"content\":\"{\\\"moduleKey\\\":\\\"courseSchedule\\\",\\\"moduleLabel\\\":\\\"排课表\\\",\\\"actionType\\\":\\\"generate\\\",\\\"actionLabel\\\":\\\"生成新配置\\\",\\\"userInstruction\\\":\\\"\\\",\\\"fieldDescriptions\\\":[\\\"termId: 学期ID\\\",\\\"classCourseId: 班级课程ID\\\",\\\"weekDay: 星期1-7\\\",\\\"startSection: 开始节次\\\",\\\"endSection: 结束节次\\\",\\\"classroom: 教室\\\",\\\"weeksText: 周次文本，由周次多选自动生成，如1-4,6,8-16周\\\",\\\"status: 0正常 1停用\\\",\\\"remark: 备注\\\"],\\\"currentForm\\\":{\\\"termId\\\":1,\\\"classroom\\\":\\\"\\\",\\\"weekDay\\\":1,\\\"startSection\\\":1,\\\"endSection\\\":2,\\\"selectedWeeks\\\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19],\\\"weeksText\\\":\\\"1-19周\\\",\\\"status\\\":\\\"0\\\",\\\"remark\\\":\\\"\\\",\\\"createBy\\\":\\\"admin\\\",\\\"createTime\\\":\\\"2026-03-17 16:47:17\\\",\\\"updateBy\\\":\\\"admin\\\",\\\"updateTime\\\":\\\"2026-03-18 00:26:38\\\",\\\"classroomName\\\":\\\"躬行楼512\\\",\\\"buildingName\\\":\\\"B栋\\\",\\\"campusName\\\":\\\"本部\\\"},\\\"selectedRows\\\":[],\\\"availableOptions\\\":{\\\"termOptions\\\":[{\\\"label\\\":\\\"2025-2026学年第二学期（2025-2026）\\\",\\\"value\\\":1,\\\"startDate\\\":\\\"2026-03-05\\\",\\\"endDate\\\":\\\"2026-07-12\\\",\\\"totalWeeks\\\":19,\\\"isCurrent\\\":\\\"1\\\"}],\\\"classCourseOptions\\\":[{\\\"label\\\":\\\"计算机应用技术1班 - Python数据分析（1）\\\",\\\"value\\\":1}],\\\"classroomOptions\\\":[{\\\"label\\\":\\\"躬行楼512 · B栋 · 本部\\\",\\\"value\\\":1,\\\"classroomName\\\":\\\"躬行楼512\\\",\\\"buildingName\\\":\\\"B栋\\\",\\\"campusName\\\":\\\"本部\\\",\\\"capacity\\\":60}]},\\\"goal\\\":\\\"请返回适合直接回填表单的 formDraft，并补充 highlights、warnings、tips。\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"bdab5f8b-42a6-4349-8bcb-3774480c7453\",\"object\":\"chat.completion\",\"created\":1773806864,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n    \\\"assistantTitle\\\": \\\"排课表配置助手\\\",\\n    \\\"formDraft\\\": {\\n        \\\"termId\\\": 1,\\n        \\\"classCourseId\\\": 1,\\n        \\\"weekDay\\\": 1,\\n        \\\"startSection\\\": 1,\\n        \\\"endSection\\\": 2,\\n        \\\"classroom\\\": 1,\\n        \\\"weeksText\\\": \\\"1-19周\\\",\\n        \\\"status\\\": 0,\\n        \\\"remark\\\": \\\"\\\"\\n    },\\n    \\\"highlights\\\": [\\\"已根据当前表单预填了学期、星期、节次、周次和教室信息，配置完整可直接提交。\\\", \\\"周次已自动生成为“1-19周”，覆盖了整个学期的所有教学周。\\\", \\\"教室“躬行楼512”容量60人，适合班级课程教学需求。\\\"],\\n    \\\"warnings\\\": [\\\"请务必确认“班级课程ID”是否正确，这是排课的核心关联字段。\\\", \\\"连续19周排课，请确认课程教学计划是否需要覆盖整个学期。\\\", \\\"当前配置为星期一第1-2节，请留意是否与教师、其他班级课程时间冲突。\\\"],\\n    \\\"tips\\\": [\\\"“周次文本”字段已由系统根据所选周次自动生成，无需手动修改。\\\", \\\"如需调整上课周次，请在周次选择器中重新勾选，本字段会自动更新。\\\", \\\"“状态”字段默认为0（正常），若课程暂不开设或需停用，可设置为1。\\\", \\\"备注可用于记录调课、合班上课等特殊情况。\\\"]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":598,\"completion_tokens\":327,\"total_tokens\":925,\"prompt_tokens_details\":{\"cached_tokens\":192},\"prompt_cache_hit_tokens\":192,\"prompt_cache_miss_tokens\":406},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache\"}', 925, 'SUCCESS', NULL, 10508, '2026-03-18 12:07:55');
INSERT INTO `sc_ai_task_log` VALUES (61, 'teaching_admin', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园后台的教学基础配置助手，负责帮助管理员生成和优化课程管理的表单配置。你必须只返回 JSON，不允许返回 markdown，不允许解释。JSON 结构固定为：{\\\"assistantTitle\\\":\\\"...\\\",\\\"formDraft\\\":{},\\\"highlights\\\":[\\\"...\\\"],\\\"warnings\\\":[\\\"...\\\"],\\\"tips\\\":[\\\"...\\\"]}。formDraft 只允许包含这些字段：courseName,courseCode,subjectType,gradeId,intro,status。如果某个字段没有把握，可以不返回该字段。状态字段 status 默认使用 0 表示正常、1 表示停用。回答要适合中国校园教务场景，配置清晰、易维护、能直接落地。\"},{\"role\":\"user\",\"content\":\"{\\\"moduleKey\\\":\\\"course\\\",\\\"moduleLabel\\\":\\\"课程管理\\\",\\\"actionType\\\":\\\"optimize\\\",\\\"actionLabel\\\":\\\"优化已有配置\\\",\\\"userInstruction\\\":\\\"命名风格：标准规范；优先目标：优先可用\\\",\\\"fieldDescriptions\\\":[\\\"courseName: 课程名称\\\",\\\"courseCode: 课程编码\\\",\\\"subjectType: 学科类型\\\",\\\"gradeId: 年级ID\\\",\\\"intro: 课程简介\\\",\\\"status: 0正常 1停用\\\"],\\\"currentForm\\\":{\\\"courseId\\\":2,\\\"courseName\\\":\\\"Python数据分析\\\",\\\"courseCode\\\":\\\"KC02GENX001\\\",\\\"subjectType\\\":\\\"理论\\\",\\\"gradeId\\\":2,\\\"intro\\\":\\\"\\\",\\\"remark\\\":null,\\\"status\\\":\\\"0\\\",\\\"createBy\\\":\\\"admin\\\",\\\"createTime\\\":\\\"2026-03-17 16:45:19\\\",\\\"updateBy\\\":\\\"admin\\\",\\\"updateTime\\\":\\\"2026-03-18 13:14:04\\\",\\\"coverUrl\\\":null},\\\"selectedRows\\\":[],\\\"availableOptions\\\":{\\\"gradeOptions\\\":[{\\\"label\\\":\\\"2024级（2）\\\",\\\"value\\\":2,\\\"schoolYear\\\":\\\"2025-2026\\\"}]},\\\"goal\\\":\\\"请返回适合直接回填表单的 formDraft，并补充 highlights、warnings、tips。\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"c856b1a6-d75f-469e-86e7-015614938866\",\"object\":\"chat.completion\",\"created\":1773834687,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n    \\\"assistantTitle\\\": \\\"课程管理配置优化助手\\\",\\n    \\\"formDraft\\\": {\\n        \\\"courseName\\\": \\\"Python数据分析\\\",\\n        \\\"courseCode\\\": \\\"KC02GENX001\\\",\\n        \\\"subjectType\\\": \\\"理论\\\",\\n        \\\"gradeId\\\": 2,\\n        \\\"intro\\\": \\\"\\\",\\n        \\\"status\\\": 0\\n    },\\n    \\\"highlights\\\": [\\n        \\\"课程名称与编码已采用标准命名，符合规范要求\\\",\\n        \\\"年级ID与可用选项匹配，配置可直接回填\\\",\\n        \\\"状态字段已转换为数字格式，便于系统处理\\\"\\n    ],\\n    \\\"warnings\\\": [\\n        \\\"课程简介字段为空，建议补充课程目标、大纲等基本信息\\\",\\n        \\\"学科类型为\'理论\'，若涉及实践环节，建议调整为\'理论与实践\'或补充说明\\\",\\n        \\\"当前年级选项仅有一项，若需跨年级开课，需扩展年级配置\\\"\\n    ],\\n    \\\"tips\\\": [\\n        \\\"课程编码建议遵循校内统一规则，如\'KC\'+年级+类型+序号\\\",\\n        \\\"学科类型可细分为：理论、实践、理论与实践、选修等，便于分类统计\\\",\\n        \\\"课程简介可支持富文本，建议包含教学目标、课时安排、考核方式等\\\",\\n        \\\"状态字段使用0/1，在表单中通常对应\'正常\'/\'停用\'的单选或开关组件\\\"\\n    ]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":391,\"completion_tokens\":290,\"total_tokens\":681,\"prompt_tokens_details\":{\"cached_tokens\":0},\"prompt_cache_hit_tokens\":0,\"prompt_cache_miss_tokens\":391},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache\"}', 681, 'SUCCESS', NULL, 8913, '2026-03-18 19:51:37');
INSERT INTO `sc_ai_task_log` VALUES (62, 'teaching_admin', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园后台的教学基础配置助手，负责帮助管理员生成和优化课程管理的表单配置。你必须只返回 JSON，不允许返回 markdown，不允许解释。JSON 结构固定为：{\\\"assistantTitle\\\":\\\"...\\\",\\\"formDraft\\\":{},\\\"highlights\\\":[\\\"...\\\"],\\\"warnings\\\":[\\\"...\\\"],\\\"tips\\\":[\\\"...\\\"]}。formDraft 只允许包含这些字段：courseName,courseCode,subjectType,gradeId,intro,status。如果某个字段没有把握，可以不返回该字段。状态字段 status 默认使用 0 表示正常、1 表示停用。回答要适合中国校园教务场景，配置清晰、易维护、能直接落地。\"},{\"role\":\"user\",\"content\":\"{\\\"moduleKey\\\":\\\"course\\\",\\\"moduleLabel\\\":\\\"课程管理\\\",\\\"actionType\\\":\\\"generate\\\",\\\"actionLabel\\\":\\\"生成新配置\\\",\\\"userInstruction\\\":\\\"命名风格：标准规范；优先目标：优先可用\\\",\\\"fieldDescriptions\\\":[\\\"courseName: 课程名称\\\",\\\"courseCode: 课程编码\\\",\\\"subjectType: 学科类型\\\",\\\"gradeId: 年级ID\\\",\\\"intro: 课程简介\\\",\\\"status: 0正常 1停用\\\"],\\\"currentForm\\\":{\\\"courseId\\\":2,\\\"courseName\\\":\\\"Python数据分析\\\",\\\"courseCode\\\":\\\"KC02GENX001\\\",\\\"subjectType\\\":\\\"理论\\\",\\\"gradeId\\\":2,\\\"intro\\\":\\\"\\\",\\\"remark\\\":null,\\\"status\\\":0,\\\"createBy\\\":\\\"admin\\\",\\\"createTime\\\":\\\"2026-03-17 16:45:19\\\",\\\"updateBy\\\":\\\"admin\\\",\\\"updateTime\\\":\\\"2026-03-18 13:14:04\\\",\\\"coverUrl\\\":null},\\\"selectedRows\\\":[],\\\"availableOptions\\\":{\\\"gradeOptions\\\":[{\\\"label\\\":\\\"2024级（2）\\\",\\\"value\\\":2,\\\"schoolYear\\\":\\\"2025-2026\\\"}]},\\\"goal\\\":\\\"请返回适合直接回填表单的 formDraft，并补充 highlights、warnings、tips。\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"aba104a4-620a-4673-a55c-862ddcc81cc7\",\"object\":\"chat.completion\",\"created\":1773834726,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n    \\\"assistantTitle\\\": \\\"课程配置助手\\\",\\n    \\\"formDraft\\\": {\\n        \\\"courseName\\\": \\\"Python数据分析\\\",\\n        \\\"courseCode\\\": \\\"KC02GENX001\\\",\\n        \\\"subjectType\\\": \\\"理论\\\",\\n        \\\"gradeId\\\": 2,\\n        \\\"intro\\\": \\\"\\\",\\n        \\\"status\\\": 0\\n    },\\n    \\\"highlights\\\": [\\\"表单已根据现有课程数据回填，可直接用于编辑或作为新配置模板。\\\", \\\"课程编码KC02GENX001符合标准规范，便于系统识别和管理。\\\", \\\"年级ID与可用选项中的2024级（2）匹配，确保数据关联正确。\\\"],\\n    \\\"warnings\\\": [\\\"课程简介（intro）字段当前为空，建议补充课程目标、主要内容等信息。\\\", \\\"学科类型（subjectType）为“理论”，若课程包含实践环节，请考虑调整类型或补充说明。\\\", \\\"当前配置基于ID为2的课程生成，创建新课程时需确保课程编码唯一性。\\\"],\\n    \\\"tips\\\": [\\\"课程名称建议保持简洁，如“Python数据分析基础”或“Python数据分析应用”。\\\", \\\"课程编码可沿用KC+年级ID+学科缩写+序列号规则，例如KC02SJFX002。\\\", \\\"年级ID可从availableOptions中选取，确保与年级数据绑定。\\\", \\\"状态字段默认0（正常），新课程通常无需修改；若为历史或暂停课程，可设为1（停用）。\\\"]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":390,\"completion_tokens\":307,\"total_tokens\":697,\"prompt_tokens_details\":{\"cached_tokens\":128},\"prompt_cache_hit_tokens\":128,\"prompt_cache_miss_tokens\":262},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache\"}', 697, 'SUCCESS', NULL, 9300, '2026-03-18 19:52:16');
INSERT INTO `sc_ai_task_log` VALUES (63, 'teaching_admin', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园后台的教学基础配置助手，负责帮助管理员生成和优化课程管理的表单配置。你必须只返回 JSON，不允许返回 markdown，不允许解释。JSON 结构固定为：{\\\"assistantTitle\\\":\\\"...\\\",\\\"formDraft\\\":{},\\\"highlights\\\":[\\\"...\\\"],\\\"warnings\\\":[\\\"...\\\"],\\\"tips\\\":[\\\"...\\\"]}。formDraft 只允许包含这些字段：courseName,courseCode,subjectType,gradeId,intro,status。如果某个字段没有把握，可以不返回该字段。状态字段 status 默认使用 0 表示正常、1 表示停用。回答要适合中国校园教务场景，配置清晰、易维护、能直接落地。\"},{\"role\":\"user\",\"content\":\"{\\\"moduleKey\\\":\\\"course\\\",\\\"moduleLabel\\\":\\\"课程管理\\\",\\\"actionType\\\":\\\"generate\\\",\\\"actionLabel\\\":\\\"生成新配置\\\",\\\"userInstruction\\\":\\\"命名风格：标准规范；优先目标：优先可用\\\",\\\"fieldDescriptions\\\":[\\\"courseName: 课程名称\\\",\\\"courseCode: 课程编码\\\",\\\"subjectType: 学科类型\\\",\\\"gradeId: 年级ID\\\",\\\"intro: 课程简介\\\",\\\"status: 0正常 1停用\\\"],\\\"currentForm\\\":{\\\"courseName\\\":\\\"\\\",\\\"courseCode\\\":\\\"\\\",\\\"subjectType\\\":\\\"\\\",\\\"intro\\\":\\\"\\\",\\\"remark\\\":\\\"\\\",\\\"status\\\":\\\"0\\\",\\\"createBy\\\":\\\"admin\\\",\\\"createTime\\\":\\\"2026-03-17 16:45:19\\\",\\\"updateBy\\\":\\\"admin\\\",\\\"updateTime\\\":\\\"2026-03-18 13:14:04\\\",\\\"coverUrl\\\":null},\\\"selectedRows\\\":[],\\\"availableOptions\\\":{\\\"gradeOptions\\\":[{\\\"label\\\":\\\"2024级（2）\\\",\\\"value\\\":2,\\\"schoolYear\\\":\\\"2025-2026\\\"}]},\\\"goal\\\":\\\"请返回适合直接回填表单的 formDraft，并补充 highlights、warnings、tips。\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"249fce19-145b-48f1-8025-c46cb91b68d7\",\"object\":\"chat.completion\",\"created\":1773834744,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n    \\\"assistantTitle\\\": \\\"课程管理配置助手\\\",\\n    \\\"formDraft\\\": {\\n        \\\"courseName\\\": \\\"\\\",\\n        \\\"courseCode\\\": \\\"\\\",\\n        \\\"subjectType\\\": \\\"\\\",\\n        \\\"gradeId\\\": 2,\\n        \\\"intro\\\": \\\"\\\",\\n        \\\"status\\\": 0\\n    },\\n    \\\"highlights\\\": [\\n        \\\"gradeId 已根据可用选项自动填充为 2（2024级），符合教务排课需求\\\",\\n        \\\"status 默认值设为 0（正常），确保新课程创建后立即可用\\\",\\n        \\\"表单结构精简，仅保留核心配置字段，便于管理员快速操作\\\"\\n    ],\\n    \\\"warnings\\\": [\\n        \\\"courseCode 需遵循校内编码规范（如：MATH-G10-S1），避免与现有课程冲突\\\",\\n        \\\"subjectType 需与学校学科分类体系一致（如：语文、数学、英语等）\\\",\\n        \\\"gradeId 当前仅绑定 2024级，若需跨年级开课，需后续手动调整\\\"\\n    ],\\n    \\\"tips\\\": [\\n        \\\"courseName 建议使用“年级+学科+模块”格式（如：高一数学必修一）\\\",\\n        \\\"intro 可简要说明课程目标、适用学生群体及考核方式\\\",\\n        \\\"课程创建后，需在“教学班管理”中关联教师与学生\\\"\\n    ]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":371,\"completion_tokens\":284,\"total_tokens\":655,\"prompt_tokens_details\":{\"cached_tokens\":192},\"prompt_cache_hit_tokens\":192,\"prompt_cache_miss_tokens\":179},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache\"}', 655, 'SUCCESS', NULL, 8864, '2026-03-18 19:52:33');
INSERT INTO `sc_ai_task_log` VALUES (64, 'teaching_admin', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园后台的教学基础配置助手，负责帮助管理员生成和优化班级课程的表单配置。你必须只返回 JSON，不允许返回 markdown，不允许解释。JSON 结构固定为：{\\\"assistantTitle\\\":\\\"...\\\",\\\"formDraft\\\":{},\\\"highlights\\\":[\\\"...\\\"],\\\"warnings\\\":[\\\"...\\\"],\\\"tips\\\":[\\\"...\\\"]}。formDraft 只允许包含这些字段：classId,courseId,teacherId,termId,weeklyHours,studentLimit,status,remark。如果某个字段没有把握，可以不返回该字段。状态字段 status 默认使用 0 表示正常、1 表示停用。回答要适合中国校园教务场景，配置清晰、易维护、能直接落地。\"},{\"role\":\"user\",\"content\":\"{\\\"moduleKey\\\":\\\"classCourse\\\",\\\"moduleLabel\\\":\\\"班级课程\\\",\\\"actionType\\\":\\\"generate\\\",\\\"actionLabel\\\":\\\"生成新配置\\\",\\\"userInstruction\\\":\\\"命名风格：标准规范；优先目标：优先可用\\\",\\\"fieldDescriptions\\\":[\\\"classId: 班级ID\\\",\\\"courseId: 课程ID\\\",\\\"teacherId: 任课教师ID\\\",\\\"termId: 学期ID\\\",\\\"weeklyHours: 周学时\\\",\\\"studentLimit: 人数上限，建议不低于班级实有人数\\\",\\\"status: 0正常 1停用\\\",\\\"remark: 备注\\\"],\\\"currentForm\\\":{\\\"businessType\\\":\\\"专科\\\",\\\"teachingClassCode\\\":\\\"\\\",\\\"credits\\\":0,\\\"courseCategory\\\":\\\"理论\\\",\\\"campusName\\\":\\\"本部\\\",\\\"assessmentType\\\":\\\"考查\\\",\\\"teachingLanguage\\\":\\\"中文授课\\\",\\\"prerequisiteCourse\\\":\\\"无\\\",\\\"taskType\\\":\\\"正常\\\",\\\"requiredFlag\\\":\\\"N\\\",\\\"courseLevelRequirement\\\":\\\"无\\\",\\\"totalHours\\\":0,\\\"requiredWeeks\\\":15,\\\"weeklyHours\\\":0,\\\"arrangedHours\\\":0,\\\"studentLimit\\\":0,\\\"status\\\":\\\"0\\\",\\\"remark\\\":\\\"\\\",\\\"createBy\\\":\\\"admin\\\",\\\"createTime\\\":\\\"2026-03-17 16:46:04\\\",\\\"updateBy\\\":\\\"admin\\\",\\\"updateTime\\\":\\\"2026-03-18 00:10:47\\\",\\\"className\\\":\\\"计算机应用技术1班\\\",\\\"courseName\\\":\\\"Python数据分析\\\",\\\"teacherName\\\":\\\"张老师\\\",\\\"termName\\\":\\\"2025-2026学年第二学期\\\",\\\"openDeptName\\\":\\\"电子信息工程学院\\\",\\\"actualStudentCount\\\":1},\\\"selectedRows\\\":[],\\\"availableOptions\\\":{\\\"classOptions\\\":[{\\\"label\\\":\\\"计算机应用技术1班（2） · 电子信息工程学院\\\",\\\"value\\\":2,\\\"gradeId\\\":2,\\\"deptId\\\":321,\\\"deptName\\\":\\\"电子信息工程学院\\\"}],\\\"courseOptions\\\":[{\\\"label\\\":\\\"Python数据分析（2）\\\",\\\"value\\\":2,\\\"gradeId\\\":2}],\\\"teacherOptions\\\":[{\\\"label\\\":\\\"张老师（101） · 任课教师\\\",\\\"value\\\":101,\\\"roleKeys\\\":[\\\"teacher\\\",\\\"campus_head_teacher\\\"],\\\"postCodes\\\":[\\\"course_teacher\\\"],\\\"roleLabels\\\":[\\\"教师\\\",\\\"班主任\\\"],\\\"postLabels\\\":[\\\"任课教师\\\"]}],\\\"termOptions\\\":[{\\\"label\\\":\\\"2025-2026学年第二学期（2025-2026）\\\",\\\"value\\\":1,\\\"startDate\\\":\\\"2026-03-05\\\",\\\"endDate\\\":\\\"2026-07-12\\\",\\\"totalWeeks\\\":19,\\\"isCurrent\\\":\\\"1\\\"}]},\\\"goal\\\":\\\"请返回适合直接回填表单的 formDraft，并补充 highlights、warnings、tips。\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"a774fd32-7502-480f-a8ec-46555fef4364\",\"object\":\"chat.completion\",\"created\":1773967646,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n    \\\"assistantTitle\\\": \\\"班级课程配置助手\\\",\\n    \\\"formDraft\\\": {\\n        \\\"classId\\\": 2,\\n        \\\"courseId\\\": 2,\\n        \\\"teacherId\\\": 101,\\n        \\\"termId\\\": 1,\\n        \\\"weeklyHours\\\": 4,\\n        \\\"studentLimit\\\": 40,\\n        \\\"status\\\": 0,\\n        \\\"remark\\\": \\\"计算机应用技术1班 Python数据分析 2025-2026学年第二学期\\\"\\n    },\\n    \\\"highlights\\\": [\\\"已根据可用选项自动匹配班级、课程、教师和学期ID\\\", \\\"周学时设置为4，符合常规理论课程安排\\\", \\\"人数上限设置为40，为班级预留充足发展空间\\\", \\\"状态默认为0（正常），开课即生效\\\"],\\n    \\\"warnings\\\": [\\\"请确认周学时4与总学时、教学周数是否匹配\\\", \\\"人数上限40需大于班级当前实有人数（1人）\\\", \\\"学期2025-2026学年第二学期为当前学期，请确认开课时间\\\"],\\n    \\\"tips\\\": [\\\"备注已自动生成课程基本信息，便于后续查询识别\\\", \\\"若为考查课，可考虑在备注中标注考核方式\\\", \\\"如需停用课程，将status改为1即可\\\"]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":671,\"completion_tokens\":264,\"total_tokens\":935,\"prompt_tokens_details\":{\"cached_tokens\":0},\"prompt_cache_hit_tokens\":0,\"prompt_cache_miss_tokens\":671},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\"}', 935, 'SUCCESS', NULL, 9264, '2026-03-20 08:47:35');
INSERT INTO `sc_ai_task_log` VALUES (65, 'learning_profile_analysis', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园学习画像分析助手。请基于提供的画像、学习行为、考试表现、错题情况，输出严格 JSON：{\\\"portraitTitle\\\":\\\"\\\",\\\"aiSummary\\\":\\\"\\\",\\\"learningPattern\\\":\\\"\\\",\\\"aiConfidence\\\":0,\\\"strengths\\\":[],\\\"risks\\\":[],\\\"aiSuggestions\\\":[]}。必须使用中文，strengths/risks/aiSuggestions 各返回 2-4 条简洁可执行内容。\"},{\"role\":\"user\",\"content\":\"{\\\"profile\\\":{\\\"abilityLevel\\\":\\\"LOW\\\",\\\"activeScore\\\":0.00,\\\"concentrationScore\\\":0.00,\\\"masteryScore\\\":0.00,\\\"interestScore\\\":0.00,\\\"riskScore\\\":100.00},\\\"diagnosis\\\":{\\\"riskLevel\\\":\\\"HIGH\\\",\\\"overallSummary\\\":\\\"当前学情风险等级为HIGH，累计学习行为0次，平均进度0.00，平均正确率0.00，待处理预警1条，推荐资源0项。\\\",\\\"riskTags\\\":[\\\"高风险学情\\\",\\\"学习活跃度偏低\\\",\\\"课程进度偏慢\\\"],\\\"actionSuggestions\\\":[\\\"本阶段累计学习时长偏少，建议先保证每天稳定学习时段\\\",\\\"优先推进当前课程进度，先完成未学完章节再做拓展练习\\\"]},\\\"studyBehaviorCount\\\":0,\\\"studyDurationSeconds\\\":0,\\\"avgProgressRate\\\":0,\\\"avgExamScore\\\":0,\\\"avgCorrectRate\\\":0,\\\"wrongQuestionCount\\\":0}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"9cc52ed7-72fa-43e5-a623-9c0315c35b48\",\"object\":\"chat.completion\",\"created\":1773974341,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n    \\\"portraitTitle\\\": \\\"高风险学情：学习行为缺失型\\\",\\n    \\\"aiSummary\\\": \\\"该生当前学情风险等级为高，学习行为数据完全缺失（学习次数、时长、进度、正确率均为0），处于学习启动的停滞阶段，需立即干预以建立基本学习习惯。\\\",\\n    \\\"learningPattern\\\": \\\"目前未观察到有效的学习行为模式，属于典型的“未启动”或“完全中断”状态。系统监测到高风险预警，表明学生尚未开始或已长时间脱离学习轨道。\\\",\\n    \\\"aiConfidence\\\": 0.95,\\n    \\\"strengths\\\": [\\n        \\\"暂无数据支持的优势项，但意味着有从零开始规划、建立全新学习习惯的潜力。\\\",\\n        \\\"错题记录为0，表明尚未因错误概念形成知识漏洞，基础可塑性强。\\\"\\n    ],\\n    \\\"risks\\\": [\\n        \\\"学习行为完全缺失：累计学习次数、时长、进度均为零，学习处于停滞状态。\\\",\\n        \\\"高风险预警：系统判定为“高风险学情”，存在脱离学习进程的严重风险。\\\",\\n        \\\"无学习轨迹：无法分析其专注度、掌握度及兴趣点，缺乏个性化指导的数据基础。\\\"\\n    ],\\n    \\\"aiSuggestions\\\": [\\n        \\\"立即启动最低学习时长：建议从每天15-20分钟的固定学习时段开始，优先完成当前课程最基础的1-2个未学章节，以打破零记录。\\\",\\n        \\\"建立学习打卡机制：鼓励学生通过完成微小任务（如观看一个短视频课程、完成5道基础题）来获取正向反馈，培养初始学习惯性。\\\",\\n        \\\"寻求外部监督与支持：建议老师或家长进行一对一沟通，了解学习停滞的具体原因（如动力不足、认知困难等），并提供启动辅助。\\\",\\n        \\\"优先完成核心进度：严格遵循“先完成，再拓展”的原则，在未达到基本课程进度前，暂不引入额外练习资源，避免畏难情绪。\\\"\\n    ]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":289,\"completion_tokens\":423,\"total_tokens\":712,\"prompt_tokens_details\":{\"cached_tokens\":0},\"prompt_cache_hit_tokens\":0,\"prompt_cache_miss_tokens\":289},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\"}', 712, 'SUCCESS', NULL, 16869, '2026-03-20 10:39:19');
INSERT INTO `sc_ai_task_log` VALUES (66, 'learning_profile_analysis', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园学习画像分析助手。请基于提供的画像、学习行为、考试表现、错题情况，输出严格 JSON：{\\\"portraitTitle\\\":\\\"\\\",\\\"aiSummary\\\":\\\"\\\",\\\"learningPattern\\\":\\\"\\\",\\\"aiConfidence\\\":0,\\\"strengths\\\":[],\\\"risks\\\":[],\\\"aiSuggestions\\\":[]}。必须使用中文，strengths/risks/aiSuggestions 各返回 2-4 条简洁可执行内容。\"},{\"role\":\"user\",\"content\":\"{\\\"profile\\\":{\\\"abilityLevel\\\":\\\"LOW\\\",\\\"activeScore\\\":0.00,\\\"concentrationScore\\\":0.00,\\\"masteryScore\\\":0.00,\\\"interestScore\\\":0.00,\\\"riskScore\\\":100.00},\\\"diagnosis\\\":{\\\"riskLevel\\\":\\\"HIGH\\\",\\\"overallSummary\\\":\\\"当前学情风险等级为HIGH，累计学习行为0次，平均进度0.00，平均正确率0.00，待处理预警1条，推荐资源0项。\\\",\\\"riskTags\\\":[\\\"高风险学情\\\",\\\"学习活跃度偏低\\\",\\\"课程进度偏慢\\\"],\\\"actionSuggestions\\\":[\\\"本阶段累计学习时长偏少，建议先保证每天稳定学习时段\\\",\\\"优先推进当前课程进度，先完成未学完章节再做拓展练习\\\"]},\\\"studyBehaviorCount\\\":0,\\\"studyDurationSeconds\\\":0,\\\"avgProgressRate\\\":0,\\\"avgExamScore\\\":0,\\\"avgCorrectRate\\\":0,\\\"wrongQuestionCount\\\":0}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"b8fafd7b-b90e-4e2a-aab6-38ae8ed7eb4a\",\"object\":\"chat.completion\",\"created\":1773974383,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n  \\\"portraitTitle\\\": \\\"高风险学情：学习行为缺失型\\\",\\n  \\\"aiSummary\\\": \\\"该生当前学情风险等级为“高风险”，核心表现为学习行为完全缺失。系统记录的学习次数、时长、进度、正确率等关键指标均为零，表明学生尚未开始或未有效参与线上学习活动，存在严重的学业脱节风险。\\\",\\n  \\\"learningPattern\\\": \\\"目前未观察到有效的学习行为模式。数据表明学生可能处于学习启动困难、缺乏学习计划或学习动力严重不足的状态，尚未形成任何规律性的学习习惯。\\\",\\n  \\\"aiConfidence\\\": 0.95,\\n  \\\"strengths\\\": [\\n    \\\"暂无数据支持的优势项。当前首要任务是启动学习，建立基础学习记录。\\\",\\n    \\\"可塑性高。由于尚未形成固定模式，更容易在引导下建立新的、良好的学习习惯。\\\"\\n  ],\\n  \\\"risks\\\": [\\n    \\\"学业脱节风险极高。零学习行为意味着课程进度完全停滞，知识断层会随时间扩大。\\\",\\n    \\\"学习动力与习惯双重缺失。长期缺乏参与可能导致学习兴趣进一步丧失，形成恶性循环。\\\"\\n  ],\\n  \\\"aiSuggestions\\\": [\\n    \\\"立即启动最低限度学习。建议从今天起，每天设定一个固定的、短暂的（如15-20分钟）学习时段，专注于完成当前课程中最基础的1-2个小节，以建立学习记录和节奏感。\\\",\\n    \\\"寻求外部监督与支持。建议学生主动联系导师或家长，说明当前困境，共同制定一个简单的、可执行的“学习启动计划”，并定期汇报进展，借助外力打破初始惰性。\\\"\\n  ]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":289,\"completion_tokens\":348,\"total_tokens\":637,\"prompt_tokens_details\":{\"cached_tokens\":256},\"prompt_cache_hit_tokens\":256,\"prompt_cache_miss_tokens\":33},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\"}', 637, 'SUCCESS', NULL, 13087, '2026-03-20 10:39:56');
INSERT INTO `sc_ai_task_log` VALUES (67, 'learning_profile_analysis', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园学习画像分析助手。请基于提供的画像、学习行为、考试表现、错题情况，输出严格 JSON：{\\\"portraitTitle\\\":\\\"\\\",\\\"aiSummary\\\":\\\"\\\",\\\"learningPattern\\\":\\\"\\\",\\\"aiConfidence\\\":0,\\\"strengths\\\":[],\\\"risks\\\":[],\\\"aiSuggestions\\\":[]}。必须使用中文，strengths/risks/aiSuggestions 各返回 2-4 条简洁可执行内容。\"},{\"role\":\"user\",\"content\":\"{\\\"profile\\\":{\\\"abilityLevel\\\":\\\"LOW\\\",\\\"activeScore\\\":0.00,\\\"concentrationScore\\\":0.00,\\\"masteryScore\\\":0.00,\\\"interestScore\\\":0.00,\\\"riskScore\\\":100.00},\\\"diagnosis\\\":{\\\"riskLevel\\\":\\\"HIGH\\\",\\\"overallSummary\\\":\\\"当前学情风险等级为HIGH，累计学习行为0次，平均进度0.00，平均正确率0.00，待处理预警1条，推荐资源0项。\\\",\\\"riskTags\\\":[\\\"高风险学情\\\",\\\"学习活跃度偏低\\\",\\\"课程进度偏慢\\\"],\\\"actionSuggestions\\\":[\\\"本阶段累计学习时长偏少，建议先保证每天稳定学习时段\\\",\\\"优先推进当前课程进度，先完成未学完章节再做拓展练习\\\"]},\\\"studyBehaviorCount\\\":0,\\\"studyDurationSeconds\\\":0,\\\"avgProgressRate\\\":0,\\\"avgExamScore\\\":0,\\\"avgCorrectRate\\\":0,\\\"wrongQuestionCount\\\":0}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"e6a82241-b2c7-49a6-8a3d-2b89b5c90851\",\"object\":\"chat.completion\",\"created\":1773974423,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n    \\\"portraitTitle\\\": \\\"高风险学情画像：学习行为缺失型\\\",\\n    \\\"aiSummary\\\": \\\"该生当前学情风险等级为“高风险”，核心表现为学习行为完全缺失。系统记录的学习次数、时长、进度、正确率均为零，风险评分已达100分。这表明学生尚未开始或未有效参与线上学习活动，处于学习进程的初始或停滞状态，亟需启动并建立基本的学习习惯。\\\",\\n    \\\"learningPattern\\\": \\\"当前未检测到有效的线上学习行为模式。学生可能尚未登录学习平台，或登录后未进行任何课程学习、练习或考试活动。这是一种典型的“行为空白”模式，缺乏任何可分析的学习轨迹、专注度或兴趣倾向数据。\\\",\\n    \\\"aiConfidence\\\": 0.95,\\n    \\\"strengths\\\": [\\n        \\\"暂无学习负担：当前无未完成的练习或考试积压，可以从零开始规划学习路径。\\\",\\n        \\\"可塑性强：尚未形成固定的学习习惯或不良模式，便于引导建立科学的学习节奏。\\\"\\n    ],\\n    \\\"risks\\\": [\\n        \\\"学习行为完全缺失：累计学习次数和时长均为零，课程进度停滞，存在严重的脱节风险。\\\",\\n        \\\"基础数据空白：缺乏正确率、专注度等关键学情指标，无法评估现有知识掌握情况，教学干预缺乏依据。\\\"\\n    ],\\n    \\\"aiSuggestions\\\": [\\n        \\\"立即启动学习，建立最小学习单元：建议从今天起，每天设定一个固定的、短时段（如15-20分钟）登录平台，完成任一课程章节的学习，以打破“零记录”状态。\\\",\\n        \\\"优先完成入门引导：关注平台或老师发布的入门课程或学习指南，首先理解平台基本操作和学习要求，确保技术层面无障碍。\\\",\\n        \\\"寻求外部监督与激励：建议家长或老师介入，协助设定最初的学习目标（如“本周完成第一章学习”），并通过简单反馈（如打卡记录）提供即时正向激励。\\\"\\n    ]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":289,\"completion_tokens\":421,\"total_tokens\":710,\"prompt_tokens_details\":{\"cached_tokens\":256},\"prompt_cache_hit_tokens\":256,\"prompt_cache_miss_tokens\":33},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\"}', 710, 'SUCCESS', NULL, 16887, '2026-03-20 10:40:41');
INSERT INTO `sc_ai_task_log` VALUES (68, 'learning_profile_analysis', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":512,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园学习画像分析助手。请基于提供的画像、学习行为、考试表现、错题情况，输出严格 JSON：{\\\"portraitTitle\\\":\\\"\\\",\\\"aiSummary\\\":\\\"\\\",\\\"learningPattern\\\":\\\"\\\",\\\"aiConfidence\\\":0,\\\"strengths\\\":[],\\\"risks\\\":[],\\\"aiSuggestions\\\":[]}。必须使用中文，strengths/risks/aiSuggestions 各返回 2-4 条简洁可执行内容。如果数据不足，要给出保守结论，不允许输出解释性 markdown。\"},{\"role\":\"user\",\"content\":\"{\\\"profile\\\":{\\\"abilityLevel\\\":\\\"LOW\\\",\\\"activeScore\\\":0.00,\\\"concentrationScore\\\":0.00,\\\"masteryScore\\\":0.00,\\\"interestScore\\\":0.00,\\\"riskScore\\\":100.00},\\\"diagnosis\\\":{\\\"riskLevel\\\":\\\"HIGH\\\",\\\"overallSummary\\\":\\\"当前学情风险等级为HIGH，累计学习行为0次，平均进度0.00，平均正确率0.00，待处理预警1条，推荐资源0项。\\\",\\\"riskTags\\\":[\\\"高风险学情\\\",\\\"学习活跃度偏低\\\",\\\"课程进度偏慢\\\"],\\\"actionSuggestions\\\":[\\\"本阶段累计学习时长偏少，建议先保证每天稳定学习时段\\\",\\\"优先推进当前课程进度，先完成未学完章节再做拓展练习\\\"]},\\\"studyBehaviorCount\\\":0,\\\"studyDurationSeconds\\\":0,\\\"avgProgressRate\\\":0,\\\"avgExamScore\\\":0,\\\"avgCorrectRate\\\":0,\\\"wrongQuestionCount\\\":0}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"599b7146-c34c-49cb-982d-7d8f990850a0\",\"object\":\"chat.completion\",\"created\":1773974762,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n  \\\"portraitTitle\\\": \\\"高风险学情：学习行为缺失型\\\",\\n  \\\"aiSummary\\\": \\\"该生当前学习行为数据完全缺失，系统评估为高风险学情。活跃度、专注度、掌握度、兴趣度均为零，风险评分达100。无任何学习时长、进度、考试及错题记录，处于学习活动停滞状态。\\\",\\n  \\\"learningPattern\\\": \\\"当前无有效学习行为记录，无法识别具体学习模式。\\\",\\n  \\\"aiConfidence\\\": 0.7,\\n  \\\"strengths\\\": [\\\"暂无明确优势项，需从启动学习行为开始建立\\\"],\\n  \\\"risks\\\": [\\\"学习行为完全缺失，学业可能处于停滞或放弃状态\\\", \\\"各项能力指标均为零，存在严重的学业脱节风险\\\"],\\n  \\\"aiSuggestions\\\": [\\\"立即启动最低限度的学习行为，例如每天登录平台学习15分钟\\\", \\\"联系老师或家长，明确当前学习状态与困难，制定重启学习计划\\\"]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":305,\"completion_tokens\":208,\"total_tokens\":513,\"prompt_tokens_details\":{\"cached_tokens\":64},\"prompt_cache_hit_tokens\":64,\"prompt_cache_miss_tokens\":241},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\"}', 513, 'SUCCESS', NULL, 8226, '2026-03-20 10:46:11');
INSERT INTO `sc_ai_task_log` VALUES (69, 'learning_profile_analysis', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":512,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园学习画像分析助手。请基于提供的画像、学习行为、考试表现、错题情况，输出严格 JSON：{\\\"portraitTitle\\\":\\\"\\\",\\\"aiSummary\\\":\\\"\\\",\\\"learningPattern\\\":\\\"\\\",\\\"aiConfidence\\\":0,\\\"strengths\\\":[],\\\"risks\\\":[],\\\"aiSuggestions\\\":[]}。必须使用中文，strengths/risks/aiSuggestions 各返回 2-4 条简洁可执行内容。如果数据不足，要给出保守结论，不允许输出解释性 markdown。\"},{\"role\":\"user\",\"content\":\"{\\\"profile\\\":{\\\"abilityLevel\\\":\\\"LOW\\\",\\\"activeScore\\\":0.00,\\\"concentrationScore\\\":0.00,\\\"masteryScore\\\":0.00,\\\"interestScore\\\":0.00,\\\"riskScore\\\":100.00},\\\"diagnosis\\\":{\\\"riskLevel\\\":\\\"HIGH\\\",\\\"overallSummary\\\":\\\"当前学情风险等级为HIGH，累计学习行为0次，平均进度0.00，平均正确率0.00，待处理预警1条，推荐资源0项。\\\",\\\"riskTags\\\":[\\\"高风险学情\\\",\\\"学习活跃度偏低\\\",\\\"课程进度偏慢\\\"],\\\"actionSuggestions\\\":[\\\"本阶段累计学习时长偏少，建议先保证每天稳定学习时段\\\",\\\"优先推进当前课程进度，先完成未学完章节再做拓展练习\\\"]},\\\"studyBehaviorCount\\\":0,\\\"studyDurationSeconds\\\":0,\\\"avgProgressRate\\\":0,\\\"avgExamScore\\\":0,\\\"avgCorrectRate\\\":0,\\\"wrongQuestionCount\\\":0}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"cc2fa10d-54be-42b5-a93b-2fe99ab46cac\",\"object\":\"chat.completion\",\"created\":1773975063,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"```json\\n{\\n  \\\"portraitTitle\\\": \\\"高风险学情：学习行为缺失型\\\",\\n  \\\"aiSummary\\\": \\\"该生当前学习行为数据完全缺失，系统评估为高风险学情。活跃度、专注度、掌握度及兴趣度得分均为零，风险评分已达最高值。未记录到任何学习时长、考试或错题数据，表明近期未参与系统学习活动。\\\",\\n  \\\"learningPattern\\\": \\\"基于现有数据，无法识别具体学习模式。学习行为记录为零，表明学生可能尚未开始使用学习平台，或处于完全未参与状态。\\\",\\n  \\\"aiConfidence\\\": 0.5,\\n  \\\"strengths\\\": [\\\"暂无明确优势项可识别\\\", \\\"可视为从零开始规划学习的空白状态\\\"],\\n  \\\"risks\\\": [\\\"学习行为完全缺失，存在脱离学习进程的风险\\\", \\\"各项能力指标均为初始值，学习基础与习惯亟待建立\\\"],\\n  \\\"aiSuggestions\\\": [\\\"立即启动学习：从平台推荐或当前课程的最基础章节开始，完成首次登录与学习记录。\\\", \\\"建立学习节奏：设定每日最低学习时长目标（如15分钟），优先保证学习行为的持续性。\\\", \\\"联系教师或导师：说明当前情况，获取入门指导与学习路径规划，明确短期学习目标。\\\"]\\n}\\n```\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":305,\"completion_tokens\":270,\"total_tokens\":575,\"prompt_tokens_details\":{\"cached_tokens\":256},\"prompt_cache_hit_tokens\":256,\"prompt_cache_miss_tokens\":49},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\"}', 575, 'SUCCESS', NULL, 10473, '2026-03-20 10:51:14');
INSERT INTO `sc_ai_task_log` VALUES (73, 'exam_question_generate', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园智能题库助手。你必须只返回 JSON 数组，不允许返回 markdown，不允许解释。数组每一项格式固定为：{\\\"questionType\\\":\\\"single\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"...\\\",\\\"answer\\\":\\\"...\\\",\\\"analysis\\\":\\\"...\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"...\\\",\\\"isRight\\\":\\\"0\\\"}]}。questionType 仅允许：single,multiple,judge,fill,essay,material,case。客观题必须提供 options；主观题 options 返回空数组。答案必须简洁、可落库、适合中国校园考试场景。\"},{\"role\":\"user\",\"content\":\"{\\\"courseId\\\":2,\\\"knowledgePointId\\\":null,\\\"questionType\\\":\\\"single\\\",\\\"difficultyLevel\\\":2,\\\"count\\\":1,\\\"instruction\\\":\\\"\\\",\\\"goal\\\":\\\"请生成可直接导入题库的试题数据\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"3e444521-9b29-44de-afc9-f42954388733\",\"object\":\"chat.completion\",\"created\":1773986701,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"[\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 2,\\n    \\\"stem\\\": \\\"下列哪项是计算机硬件系统中最核心的部件？\\\",\\n    \\\"answer\\\": \\\"A\\\",\\n    \\\"analysis\\\": \\\"中央处理器（CPU）是计算机硬件系统的核心，负责解释和执行程序指令，控制其他硬件设备的工作。\\\",\\n    \\\"options\\\": [\\n      {\\n        \\\"optionKey\\\": \\\"A\\\",\\n        \\\"optionContent\\\": \\\"中央处理器（CPU）\\\",\\n        \\\"isRight\\\": \\\"1\\\"\\n      },\\n      {\\n        \\\"optionKey\\\": \\\"B\\\",\\n        \\\"optionContent\\\": \\\"内存（RAM）\\\",\\n        \\\"isRight\\\": \\\"0\\\"\\n      },\\n      {\\n        \\\"optionKey\\\": \\\"C\\\",\\n        \\\"optionContent\\\": \\\"硬盘（HDD）\\\",\\n        \\\"isRight\\\": \\\"0\\\"\\n      },\\n      {\\n        \\\"optionKey\\\": \\\"D\\\",\\n        \\\"optionContent\\\": \\\"显卡（GPU）\\\",\\n        \\\"isRight\\\": \\\"0\\\"\\n      }\\n    ]\\n  }\\n]\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":168,\"completion_tokens\":210,\"total_tokens\":378,\"prompt_tokens_details\":{\"cached_tokens\":128},\"prompt_cache_hit_tokens\":128,\"prompt_cache_miss_tokens\":40},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\"}', 378, 'SUCCESS', NULL, 5529, '2026-03-20 14:05:07');
INSERT INTO `sc_ai_task_log` VALUES (75, 'exam_question_generate_async', 2, 1, '{\"taskLabel\":\"AI异步出题\",\"courseId\":2,\"knowledgePointId\":null,\"questionType\":\"single\",\"questionTypeLabel\":\"单选题\",\"difficultyLevel\":3,\"count\":4,\"saveToBank\":true,\"instruction\":\"\",\"operator\":\"admin\"}', '{\"stage\":\"completed\",\"message\":\"AI 出题完成，共生成4题，已入库4题\",\"result\":{\"generatedCount\":4,\"savedCount\":4,\"rawContent\":\"[\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在Python中，以下哪个关键字用于定义函数？\\\",\\n    \\\"answer\\\": \\\"def\\\",\\n    \\\"analysis\\\": \\\"在Python中，使用def关键字来定义函数，这是Python语言的基本语法规则。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"function\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"def\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"define\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"func\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"下列哪个选项是Python中的可变数据类型？\\\",\\n    \\\"answer\\\": \\\"list\\\",\\n    \\\"analysis\\\": \\\"在Python中，list（列表）是可变数据类型，可以修改其内容；而字符串、元组和整数是不可变的。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"str\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"tuple\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"list\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"int\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在Python中，用于从列表中删除最后一个元素并返回该元素的方法是？\\\",\\n    \\\"answer\\\": \\\"pop()\\\",\\n    \\\"analysis\\\": \\\"pop()方法用于移除列表中的一个元素（默认最后一个），并返回该元素的值。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"remove()\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"pop()\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"delete()\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"clear()\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"Python中，以下哪个运算符用于计算幂运算？\\\",\\n    \\\"answer\\\": \\\"**\\\",\\n    \\\"analysis\\\": \\\"在Python中，**运算符用于幂运算，例如2**3表示2的3次方，结果为8。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"^\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"**\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"//\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"%\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  }\\n]\",\"questions\":[{\"questionId\":5,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"在Python中，以下哪个关键字用于定义函数？\",\"answer\":\"B\",\"analysis\":\"在Python中，使用def关键字来定义函数，这是Python语言的基本语法规则。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1773993534386\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":17,\"optionKey\":\"A\",\"optionContent\":\"function\",\"isRight\":\"0\"},{\"optionId\":18,\"optionKey\":\"B\",\"optionContent\":\"def\",\"isRight\":\"1\"},{\"optionId\":19,\"optionKey\":\"C\",\"optionContent\":\"define\",\"isRight\":\"0\"},{\"optionId\":20,\"optionKey\":\"D\",\"optionContent\":\"func\",\"isRight\":\"0\"}]},{\"questionId\":6,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"下列哪个选项是Python中的可变数据类型？\",\"answer\":\"C\",\"analysis\":\"在Python中，list（列表）是可变数据类型，可以修改其内容；而字符串、元组和整数是不可变的。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1773993534389\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":21,\"optionKey\":\"A\",\"optionContent\":\"str\",\"isRight\":\"0\"},{\"optionId\":22,\"optionKey\":\"B\",\"optionContent\":\"tuple\",\"isRight\":\"0\"},{\"optionId\":23,\"optionKey\":\"C\",\"optionContent\":\"list\",\"isRight\":\"1\"},{\"optionId\":24,\"optionKey\":\"D\",\"optionContent\":\"int\",\"isRight\":\"0\"}]},{\"questionId\":7,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"在Python中，用于从列表中删除最后一个元素并返回该元素的方法是？\",\"answer\":\"B\",\"analysis\":\"pop()方法用于移除列表中的一个元素（默认最后一个），并返回该元素的值。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1773993534389\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":25,\"optionKey\":\"A\",\"optionContent\":\"remove()\",\"isRight\":\"0\"},{\"optionId\":26,\"optionKey\":\"B\",\"optionContent\":\"pop()\",\"isRight\":\"1\"},{\"optionId\":27,\"optionKey\":\"C\",\"optionContent\":\"delete()\",\"isRight\":\"0\"},{\"optionId\":28,\"optionKey\":\"D\",\"optionContent\":\"clear()\",\"isRight\":\"0\"}]},{\"questionId\":8,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"Python中，以下哪个运算符用于计算幂运算？\",\"answer\":\"B\",\"analysis\":\"在Python中，**运算符用于幂运算，例如2**3表示2的3次方，结果为8。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1773993534389\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":29,\"optionKey\":\"A\",\"optionContent\":\"^\",\"isRight\":\"0\"},{\"optionId\":30,\"optionKey\":\"B\",\"optionContent\":\"**\",\"isRight\":\"1\"},{\"optionId\":31,\"optionKey\":\"C\",\"optionContent\":\"//\",\"isRight\":\"0\"},{\"optionId\":32,\"optionKey\":\"D\",\"optionContent\":\"%\",\"isRight\":\"0\"}]}]}}', NULL, 'SUCCESS', NULL, 16863, '2026-03-20 15:58:37');
INSERT INTO `sc_ai_task_log` VALUES (76, 'exam_question_generate', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园智能题库助手。你必须只返回 JSON 数组，不允许返回 markdown，不允许解释。数组每一项格式固定为：{\\\"questionType\\\":\\\"single\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"...\\\",\\\"answer\\\":\\\"...\\\",\\\"analysis\\\":\\\"...\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"...\\\",\\\"isRight\\\":\\\"0\\\"}]}。questionType 仅允许：single,multiple,judge,fill,essay,material,case。客观题必须提供 options；主观题 options 返回空数组。答案必须简洁、可落库、适合中国校园考试场景。\"},{\"role\":\"user\",\"content\":\"{\\\"courseId\\\":2,\\\"knowledgePointId\\\":null,\\\"questionType\\\":\\\"single\\\",\\\"difficultyLevel\\\":3,\\\"count\\\":4,\\\"instruction\\\":\\\"\\\",\\\"goal\\\":\\\"请生成可直接导入题库的试题数据\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"0f84dd52-1929-4dd5-9b4d-ea765a131dc1\",\"object\":\"chat.completion\",\"created\":1773993517,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"[\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在Python中，以下哪个关键字用于定义函数？\\\",\\n    \\\"answer\\\": \\\"def\\\",\\n    \\\"analysis\\\": \\\"在Python中，使用def关键字来定义函数，这是Python语言的基本语法规则。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"function\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"def\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"define\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"func\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"下列哪个选项是Python中的可变数据类型？\\\",\\n    \\\"answer\\\": \\\"list\\\",\\n    \\\"analysis\\\": \\\"在Python中，list（列表）是可变数据类型，可以修改其内容；而字符串、元组和整数是不可变的。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"str\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"tuple\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"list\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"int\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在Python中，用于从列表中删除最后一个元素并返回该元素的方法是？\\\",\\n    \\\"answer\\\": \\\"pop()\\\",\\n    \\\"analysis\\\": \\\"pop()方法用于移除列表中的一个元素（默认最后一个），并返回该元素的值。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"remove()\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"pop()\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"delete()\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"clear()\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"Python中，以下哪个运算符用于计算幂运算？\\\",\\n    \\\"answer\\\": \\\"**\\\",\\n    \\\"analysis\\\": \\\"在Python中，**运算符用于幂运算，例如2**3表示2的3次方，结果为8。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"^\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"**\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"//\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"%\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  }\\n]\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":168,\"completion_tokens\":679,\"total_tokens\":847,\"prompt_tokens_details\":{\"cached_tokens\":128},\"prompt_cache_hit_tokens\":128,\"prompt_cache_miss_tokens\":40},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\"}', 847, 'SUCCESS', NULL, 16651, '2026-03-20 15:58:54');
INSERT INTO `sc_ai_task_log` VALUES (77, 'exam_question_generate_async', 2, 4, '{\"taskLabel\":\"AI异步出题\",\"courseId\":2,\"knowledgePointId\":null,\"questionType\":\"multiple\",\"questionTypeLabel\":\"多选题\",\"difficultyLevel\":3,\"count\":5,\"saveToBank\":true,\"instruction\":\"\",\"operator\":\"admin\"}', '{\"stage\":\"completed\",\"message\":\"AI 出题完成，共生成5题，已入库5题\",\"result\":{\"generatedCount\":5,\"savedCount\":5,\"rawContent\":\"[{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"下列属于智慧校园建设核心目标的有（ ）。\\\",\\\"answer\\\":\\\"ABCD\\\",\\\"analysis\\\":\\\"智慧校园旨在通过信息技术实现教育教学、管理服务、生活环境的全面智能化，涵盖资源优化、管理效率提升、个性化学习支持及数据驱动决策等多个维度。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"实现教育资源的优化配置与共享\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"提升学校管理与服务的效率\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"构建个性化的学习与成长环境\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"基于大数据的科学决策支持\\\",\\\"isRight\\\":\\\"1\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"在智慧校园物联网架构中，感知层主要涉及的技术或设备包括（ ）。\\\",\\\"answer\\\":\\\"ABC\\\",\\\"analysis\\\":\\\"感知层负责信息采集，主要包括 RFID、传感器、摄像头等终端设备；而 4G/5G 和云平台分别属于网络层和应用/平台层。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"RFID 射频识别技术\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"各类环境传感器\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"智能监控摄像头\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"云计算数据中心\\\",\\\"isRight\\\":\\\"0\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"关于智慧教室的功能特征，以下描述正确的有（ ）。\\\",\\\"answer\\\":\\\"ABD\\\",\\\"analysis\\\":\\\"智慧教室强调互动性、数据采集和环境自适应。虽然支持远程互动，但完全取代传统面授并非其建设初衷或必然特征，而是作为补充和增强。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"支持多屏互动与小组协作学习\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"自动采集课堂教学行为数据\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"完全取代教师进行授课\\\",\\\"isRight\\\":\\\"0\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"根据环境参数自动调节灯光与温度\\\",\\\"isRight\\\":\\\"1\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"智慧校园大数据平台的主要应用场景包括（ ）。\\\",\\\"answer\\\":\\\"ABCD\\\",\\\"analysis\\\":\\\"大数据平台广泛应用于学生画像分析、教学质量评估、校园安全预警以及后勤资源调度等场景，以数据驱动教育治理现代化。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"学生综合素质评价与画像构建\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"教学质量监测与诊断改进\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"校园安全隐患预测与预警\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"能源消耗分析与绿色校园管理\\\",\\\"isRight\\\":\\\"1\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"保障智慧校园信息安全的关键措施有（ ）。\\\",\\\"answer\\\":\\\"ABC\\\",\\\"analysis\\\":\\\"信息安全需从身份认证、数据加密、权限管理及定期审计等多方面入手。将所有系统开放给公网且无验证是严重的安全漏洞。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"建立统一身份认证与单点登录机制\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"实施敏感数据加密存储与传输\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"定期进行系统漏洞扫描与安全审计\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"所有应用系统无需验证直接对外开放\\\",\\\"isRight\\\":\\\"0\\\"}]}]\",\"questions\":[{\"questionId\":9,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"multiple\",\"difficultyLevel\":3,\"stem\":\"下列属于智慧校园建设核心目标的有（ ）。\",\"answer\":\"A,B,C,D\",\"analysis\":\"智慧校园旨在通过信息技术实现教育教学、管理服务、生活环境的全面智能化，涵盖资源优化、管理效率提升、个性化学习支持及数据驱动决策等多个维度。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1773993600850\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":33,\"optionKey\":\"A\",\"optionContent\":\"实现教育资源的优化配置与共享\",\"isRight\":\"1\"},{\"optionId\":34,\"optionKey\":\"B\",\"optionContent\":\"提升学校管理与服务的效率\",\"isRight\":\"1\"},{\"optionId\":35,\"optionKey\":\"C\",\"optionContent\":\"构建个性化的学习与成长环境\",\"isRight\":\"1\"},{\"optionId\":36,\"optionKey\":\"D\",\"optionContent\":\"基于大数据的科学决策支持\",\"isRight\":\"1\"}]},{\"questionId\":10,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"multiple\",\"difficultyLevel\":3,\"stem\":\"在智慧校园物联网架构中，感知层主要涉及的技术或设备包括（ ）。\",\"answer\":\"A,B,C\",\"analysis\":\"感知层负责信息采集，主要包括 RFID、传感器、摄像头等终端设备；而 4G/5G 和云平台分别属于网络层和应用/平台层。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1773993600850\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":37,\"optionKey\":\"A\",\"optionContent\":\"RFID 射频识别技术\",\"isRight\":\"1\"},{\"optionId\":38,\"optionKey\":\"B\",\"optionContent\":\"各类环境传感器\",\"isRight\":\"1\"},{\"optionId\":39,\"optionKey\":\"C\",\"optionContent\":\"智能监控摄像头\",\"isRight\":\"1\"},{\"optionId\":40,\"optionKey\":\"D\",\"optionContent\":\"云计算数据中心\",\"isRight\":\"0\"}]},{\"questionId\":11,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"multiple\",\"difficultyLevel\":3,\"stem\":\"关于智慧教室的功能特征，以下描述正确的有（ ）。\",\"answer\":\"A,B,D\",\"analysis\":\"智慧教室强调互动性、数据采集和环境自适应。虽然支持远程互动，但完全取代传统面授并非其建设初衷或必然特征，而是作为补充和增强。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1773993600850\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":41,\"optionKey\":\"A\",\"optionContent\":\"支持多屏互动与小组协作学习\",\"isRight\":\"1\"},{\"optionId\":42,\"optionKey\":\"B\",\"optionContent\":\"自动采集课堂教学行为数据\",\"isRight\":\"1\"},{\"optionId\":43,\"optionKey\":\"C\",\"optionContent\":\"完全取代教师进行授课\",\"isRight\":\"0\"},{\"optionId\":44,\"optionKey\":\"D\",\"optionContent\":\"根据环境参数自动调节灯光与温度\",\"isRight\":\"1\"}]},{\"questionId\":12,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"multiple\",\"difficultyLevel\":3,\"stem\":\"智慧校园大数据平台的主要应用场景包括（ ）。\",\"answer\":\"A,B,C,D\",\"analysis\":\"大数据平台广泛应用于学生画像分析、教学质量评估、校园安全预警以及后勤资源调度等场景，以数据驱动教育治理现代化。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1773993600850\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":45,\"optionKey\":\"A\",\"optionContent\":\"学生综合素质评价与画像构建\",\"isRight\":\"1\"},{\"optionId\":46,\"optionKey\":\"B\",\"optionContent\":\"教学质量监测与诊断改进\",\"isRight\":\"1\"},{\"optionId\":47,\"optionKey\":\"C\",\"optionContent\":\"校园安全隐患预测与预警\",\"isRight\":\"1\"},{\"optionId\":48,\"optionKey\":\"D\",\"optionContent\":\"能源消耗分析与绿色校园管理\",\"isRight\":\"1\"}]},{\"questionId\":13,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"multiple\",\"difficultyLevel\":3,\"stem\":\"保障智慧校园信息安全的关键措施有（ ）。\",\"answer\":\"A,B,C\",\"analysis\":\"信息安全需从身份认证、数据加密、权限管理及定期审计等多方面入手。将所有系统开放给公网且无验证是严重的安全漏洞。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1773993600850\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":49,\"optionKey\":\"A\",\"optionContent\":\"建立统一身份认证与单点登录机制\",\"isRight\":\"1\"},{\"optionId\":50,\"optionKey\":\"B\",\"optionContent\":\"实施敏感数据加密存储与传输\",\"isRight\":\"1\"},{\"optionId\":51,\"optionKey\":\"C\",\"optionContent\":\"定期进行系统漏洞扫描与安全审计\",\"isRight\":\"1\"},{\"optionId\":52,\"optionKey\":\"D\",\"optionContent\":\"所有应用系统无需验证直接对外开放\",\"isRight\":\"0\"}]}]}}', NULL, 'SUCCESS', NULL, 15293, '2026-03-20 15:59:45');
INSERT INTO `sc_ai_task_log` VALUES (78, 'exam_question_generate', 4, 4, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园智能题库助手。你必须只返回 JSON 数组，不允许返回 markdown，不允许解释。数组每一项格式固定为：{\\\"questionType\\\":\\\"single\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"...\\\",\\\"answer\\\":\\\"...\\\",\\\"analysis\\\":\\\"...\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"...\\\",\\\"isRight\\\":\\\"0\\\"}]}。questionType 仅允许：single,multiple,judge,fill,essay,material,case。客观题必须提供 options；主观题 options 返回空数组。答案必须简洁、可落库、适合中国校园考试场景。\"},{\"role\":\"user\",\"content\":\"{\\\"courseId\\\":2,\\\"knowledgePointId\\\":null,\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"count\\\":5,\\\"instruction\\\":\\\"\\\",\\\"goal\\\":\\\"请生成可直接导入题库的试题数据\\\"}\"}],\"model\":\"qwen3.5-plus\"}', '{\"choices\":[{\"message\":{\"content\":\"[{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"下列属于智慧校园建设核心目标的有（ ）。\\\",\\\"answer\\\":\\\"ABCD\\\",\\\"analysis\\\":\\\"智慧校园旨在通过信息技术实现教育教学、管理服务、生活环境的全面智能化，涵盖资源优化、管理效率提升、个性化学习支持及数据驱动决策等多个维度。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"实现教育资源的优化配置与共享\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"提升学校管理与服务的效率\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"构建个性化的学习与成长环境\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"基于大数据的科学决策支持\\\",\\\"isRight\\\":\\\"1\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"在智慧校园物联网架构中，感知层主要涉及的技术或设备包括（ ）。\\\",\\\"answer\\\":\\\"ABC\\\",\\\"analysis\\\":\\\"感知层负责信息采集，主要包括 RFID、传感器、摄像头等终端设备；而 4G/5G 和云平台分别属于网络层和应用/平台层。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"RFID 射频识别技术\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"各类环境传感器\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"智能监控摄像头\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"云计算数据中心\\\",\\\"isRight\\\":\\\"0\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"关于智慧教室的功能特征，以下描述正确的有（ ）。\\\",\\\"answer\\\":\\\"ABD\\\",\\\"analysis\\\":\\\"智慧教室强调互动性、数据采集和环境自适应。虽然支持远程互动，但完全取代传统面授并非其建设初衷或必然特征，而是作为补充和增强。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"支持多屏互动与小组协作学习\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"自动采集课堂教学行为数据\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"完全取代教师进行授课\\\",\\\"isRight\\\":\\\"0\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"根据环境参数自动调节灯光与温度\\\",\\\"isRight\\\":\\\"1\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"智慧校园大数据平台的主要应用场景包括（ ）。\\\",\\\"answer\\\":\\\"ABCD\\\",\\\"analysis\\\":\\\"大数据平台广泛应用于学生画像分析、教学质量评估、校园安全预警以及后勤资源调度等场景，以数据驱动教育治理现代化。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"学生综合素质评价与画像构建\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"教学质量监测与诊断改进\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"校园安全隐患预测与预警\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"能源消耗分析与绿色校园管理\\\",\\\"isRight\\\":\\\"1\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"保障智慧校园信息安全的关键措施有（ ）。\\\",\\\"answer\\\":\\\"ABC\\\",\\\"analysis\\\":\\\"信息安全需从身份认证、数据加密、权限管理及定期审计等多方面入手。将所有系统开放给公网且无验证是严重的安全漏洞。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"建立统一身份认证与单点登录机制\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"实施敏感数据加密存储与传输\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"定期进行系统漏洞扫描与安全审计\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"所有应用系统无需验证直接对外开放\\\",\\\"isRight\\\":\\\"0\\\"}]}]\",\"role\":\"assistant\"},\"finish_reason\":\"stop\",\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion\",\"usage\":{\"prompt_tokens\":173,\"completion_tokens\":737,\"total_tokens\":910,\"completion_tokens_details\":{\"text_tokens\":737},\"prompt_tokens_details\":{\"text_tokens\":173}},\"created\":1773993600,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-081c57b5-eabb-9c7f-8507-7c31ed8ae392\"}', 910, 'SUCCESS', NULL, 15089, '2026-03-20 16:00:00');
INSERT INTO `sc_ai_task_log` VALUES (79, 'learning_profile_analysis', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":512,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园学习画像分析助手。请基于提供的画像、学习行为、考试表现、错题情况，输出严格 JSON：{\\\"portraitTitle\\\":\\\"\\\",\\\"aiSummary\\\":\\\"\\\",\\\"learningPattern\\\":\\\"\\\",\\\"aiConfidence\\\":0,\\\"strengths\\\":[],\\\"risks\\\":[],\\\"aiSuggestions\\\":[]}。必须使用中文，strengths/risks/aiSuggestions 各返回 2-4 条简洁可执行内容。如果数据不足，要给出保守结论，不允许输出解释性 markdown。\"},{\"role\":\"user\",\"content\":\"{\\\"profile\\\":{\\\"abilityLevel\\\":\\\"LOW\\\",\\\"activeScore\\\":0.00,\\\"concentrationScore\\\":0.00,\\\"masteryScore\\\":13.13,\\\"interestScore\\\":0.00,\\\"riskScore\\\":100},\\\"diagnosis\\\":{\\\"riskLevel\\\":\\\"HIGH\\\",\\\"overallSummary\\\":\\\"当前学情风险等级为HIGH，累计学习行为0次，平均进度0.00，平均正确率37.50，待处理预警1条，推荐资源0项。\\\",\\\"riskTags\\\":[\\\"高风险学情\\\",\\\"学习活跃度偏低\\\",\\\"课程进度偏慢\\\",\\\"测验正确率偏低\\\"],\\\"actionSuggestions\\\":[\\\"本阶段累计学习时长偏少，建议先保证每天稳定学习时段\\\",\\\"优先推进当前课程进度，先完成未学完章节再做拓展练习\\\",\\\"安排一次基础巩固测验，先修复核心知识点漏洞\\\",\\\"围绕错题本进行专项复习，优先消化高频错题\\\"]},\\\"studyBehaviorCount\\\":0,\\\"studyDurationSeconds\\\":0,\\\"avgProgressRate\\\":0,\\\"avgExamScore\\\":37.50,\\\"avgCorrectRate\\\":37.50,\\\"wrongQuestionCount\\\":4}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"ddb086aa-2229-4909-a673-00e3520c4dee\",\"object\":\"chat.completion\",\"created\":1774008636,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"{\\n  \\\"portraitTitle\\\": \\\"高风险低活跃学习者\\\",\\n  \\\"aiSummary\\\": \\\"该生当前学情风险等级为高，学习活跃度极低，课程进度停滞，测验正确率偏低，存在4道错题待处理。\\\",\\n  \\\"learningPattern\\\": \\\"学习行为数据缺失，表现为零活跃、零时长、零进度，但存在考试记录和错题，表明学习过程可能不连续或缺乏系统性。\\\",\\n  \\\"aiConfidence\\\": 0.6,\\n  \\\"strengths\\\": [\\\"暂无明确优势项，需从建立基础学习习惯开始\\\"],\\n  \\\"risks\\\": [\\\"学习活跃度极低，可能导致知识脱节\\\", \\\"课程进度停滞，学习任务积压风险高\\\", \\\"测验正确率偏低，基础知识掌握不牢\\\"],\\n  \\\"aiSuggestions\\\": [\\\"立即设定并执行每日最低学习时长目标，如15分钟，以激活学习状态\\\", \\\"优先集中处理现有4道错题，理解并订正，作为知识复习的起点\\\", \\\"从当前课程最基础章节开始推进，确保完成核心内容学习\\\"]\\n}\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":338,\"completion_tokens\":228,\"total_tokens\":566,\"prompt_tokens_details\":{\"cached_tokens\":0},\"prompt_cache_hit_tokens\":0,\"prompt_cache_miss_tokens\":338},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\"}', 566, 'SUCCESS', NULL, 8402, '2026-03-20 20:10:45');
INSERT INTO `sc_ai_task_log` VALUES (80, 'test', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"请随机生成一道高中数学题目。\"},{\"role\":\"user\",\"content\":\"请介绍一下这个提示词是否工作正常。\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"90e3ea42-a44d-4845-837f-c850654a97cd\",\"object\":\"chat.completion\",\"created\":1774078656,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"好的，我理解您的需求。您希望我生成一道随机的高中数学题目，而不是直接回答关于提示词本身的问题。\\n\\n这说明当前的提示词工作正常，它成功地引导我执行了“生成题目”这个核心任务，而不是去分析或讨论提示词。\\n\\n为了证明这一点，并完成您的指令，我现在就为您随机生成一道高中数学题目：\\n\\n---\\n\\n**题目：**\\n\\n已知函数 \\\\( f(x) = 2\\\\sin(x) \\\\cos(x) - \\\\sqrt{3} \\\\cos(2x) \\\\)。\\n\\n1.  将函数 \\\\( f(x) \\\\) 化简为 \\\\( A\\\\sin(\\\\omega x + \\\\varphi) + B \\\\) 的形式（其中 \\\\( A > 0, \\\\omega > 0 \\\\)）。\\n2.  求函数 \\\\( f(x) \\\\) 在区间 \\\\( [0, \\\\frac{\\\\pi}{2}] \\\\) 上的最大值和最小值，并指出取得最值时对应的 \\\\( x \\\\) 值。\\n\\n---\\n\\n**这道题考察了以下知识点：**\\n*   三角恒等变换（二倍角公式、辅助角公式）。\\n*   三角函数在给定区间上的最值问题。\\n\\n如果您需要这道题的解答，或者希望我生成其他类型（如代数、几何、概率统计）的题目，请随时提出。\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":20,\"completion_tokens\":260,\"total_tokens\":280,\"prompt_tokens_details\":{\"cached_tokens\":0},\"prompt_cache_hit_tokens\":0,\"prompt_cache_miss_tokens\":20},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\"}', 280, 'SUCCESS', NULL, 7973, '2026-03-21 15:37:43');
INSERT INTO `sc_ai_task_log` VALUES (81, 'model_test', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":true,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"You are a concise assistant.\"},{\"role\":\"user\",\"content\":\"请简单介绍一下你当前模型连接是否正常。\"}],\"model\":\"deepseek-chat\",\"stream_options\":{\"include_usage\":true}}', 'data: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"role\":\"assistant\",\"content\":\"\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"当前\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"模型\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"连接\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"正常\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"，\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"可以\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"流畅\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"响应\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"您的\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"请求\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"。\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"如有\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"任何\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"问题\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"或\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"需要\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"帮助\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"，\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"请\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"随时\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"告诉我\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"！\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\" 😊\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"559704bf-7b19-4128-8074-18860afbfed3\",\"object\":\"chat.completion.chunk\",\"created\":1774078690,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":20,\"completion_tokens\":24,\"total_tokens\":44,\"prompt_tokens_details\":{\"cached_tokens\":0},\"prompt_cache_hit_tokens\":0,\"prompt_cache_miss_tokens\":20}}\n\ndata: [DONE]\n', 44, 'SUCCESS', NULL, 1954, '2026-03-21 15:38:12');
INSERT INTO `sc_ai_task_log` VALUES (82, 'model_test', 4, 4, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":true,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"You are a concise assistant.\"},{\"role\":\"user\",\"content\":\"请简单介绍一下你当前模型连接是否正常。\"}],\"model\":\"qwen3.5-plus\",\"stream_options\":{\"include_usage\":true}}', 'data: {\"choices\":[{\"delta\":{\"content\":\"我的\",\"role\":\"assistant\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1774078701,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-884796ca-730c-9a70-b51d-1b5c471b41ca\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"连接状态正常，\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1774078701,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-884796ca-730c-9a70-b51d-1b5c471b41ca\"}\n\ndata: {\"choices\":[{\"delta\":{\"content\":\"可以为您提供帮助。\"},\"finish_reason\":null,\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1774078701,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-884796ca-730c-9a70-b51d-1b5c471b41ca\"}\n\ndata: {\"choices\":[{\"finish_reason\":\"stop\",\"delta\":{\"content\":\"\"},\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion.chunk\",\"usage\":null,\"created\":1774078701,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-884796ca-730c-9a70-b51d-1b5c471b41ca\"}\n\ndata: {\"choices\":[],\"object\":\"chat.completion.chunk\",\"usage\":{\"prompt_tokens\":32,\"completion_tokens\":9,\"total_tokens\":41,\"completion_tokens_details\":{\"text_tokens\":9},\"prompt_tokens_details\":{\"text_tokens\":32}},\"created\":1774078701,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-884796ca-730c-9a70-b51d-1b5c471b41ca\"}\n\ndata: [DONE]\n', 41, 'SUCCESS', NULL, 1267, '2026-03-21 15:38:21');
INSERT INTO `sc_ai_task_log` VALUES (88, 'exam_question_generate_async', NULL, 1, '{\"taskLabel\":\"AI异步出题\",\"courseId\":null,\"knowledgePointId\":null,\"modelId\":null,\"questionType\":\"single\",\"questionTypeLabel\":\"单选题\",\"difficultyLevel\":3,\"count\":5,\"saveToBank\":true,\"instruction\":\"\",\"operator\":\"admin\"}', '{\"stage\":\"completed\",\"message\":\"AI 出题完成，共生成5题，已入库5题\",\"result\":{\"generatedCount\":5,\"savedCount\":5,\"tokenUsed\":1301,\"rawContent\":\"[\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在面向对象编程中，用于描述对象所具有的行为或操作的术语是？\\\",\\n    \\\"answer\\\": \\\"B\\\",\\n    \\\"analysis\\\": \\\"在面向对象编程中，对象的行为或操作通常通过方法来定义和实现。属性描述对象的状态，类是对对象的抽象定义，而实例是类的具体化。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"属性\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"方法\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"类\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"实例\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"下列数据结构中，按照“先进后出”原则工作的是？\\\",\\n    \\\"answer\\\": \\\"C\\\",\\n    \\\"analysis\\\": \\\"栈是一种线性数据结构，其操作遵循“先进后出”的原则。队列是“先进先出”，链表和树没有固定的全局存取顺序。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"队列\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"链表\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"栈\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"二叉树\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在关系型数据库中，用于唯一标识表中每一行记录的字段或字段组合称为？\\\",\\n    \\\"answer\\\": \\\"A\\\",\\n    \\\"analysis\\\": \\\"主键是关系型数据库表中的一个或多个字段，其值能唯一地标识表中的每一行记录。外键用于建立表与表之间的关联，索引用于提高查询速度，约束用于保证数据的完整性。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"主键\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"外键\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"索引\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"约束\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在Python中，用于创建一个新的类定义的关键字是？\\\",\\n    \\\"answer\\\": \\\"B\\\",\\n    \\\"analysis\\\": \\\"在Python中，使用关键字`class`来定义一个新的类。`def`用于定义函数，`new`不是用于定义类的关键字，`object`是所有类的基类。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"def\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"class\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"new\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"object\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在Java语言中，下列哪个关键字用于声明一个常量，即其值在初始化后不能被修改？\\\",\\n    \\\"answer\\\": \\\"D\\\",\\n    \\\"analysis\\\": \\\"在Java中，`final`关键字用于声明常量，表示该变量的值一旦被赋值就不能再被更改。`static`表示静态成员，`const`是Java的保留字但并未使用，`volatile`用于确保变量的可见性。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"static\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"const\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"volatile\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"final\\\", \\\"isRight\\\": \\\"1\\\"}\\n    ]\\n  }\\n]\",\"questions\":[{\"questionId\":14,\"courseId\":null,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"在面向对象编程中，用于描述对象所具有的行为或操作的术语是？\",\"answer\":\"B\",\"analysis\":\"在面向对象编程中，对象的行为或操作通常通过方法来定义和实现。属性描述对象的状态，类是对对象的抽象定义，而实例是类的具体化。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774081217791\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":53,\"optionKey\":\"A\",\"optionContent\":\"属性\",\"isRight\":\"0\"},{\"optionId\":54,\"optionKey\":\"B\",\"optionContent\":\"方法\",\"isRight\":\"1\"},{\"optionId\":55,\"optionKey\":\"C\",\"optionContent\":\"类\",\"isRight\":\"0\"},{\"optionId\":56,\"optionKey\":\"D\",\"optionContent\":\"实例\",\"isRight\":\"0\"}]},{\"questionId\":15,\"courseId\":null,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"下列数据结构中，按照“先进后出”原则工作的是？\",\"answer\":\"C\",\"analysis\":\"栈是一种线性数据结构，其操作遵循“先进后出”的原则。队列是“先进先出”，链表和树没有固定的全局存取顺序。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774081217792\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":57,\"optionKey\":\"A\",\"optionContent\":\"队列\",\"isRight\":\"0\"},{\"optionId\":58,\"optionKey\":\"B\",\"optionContent\":\"链表\",\"isRight\":\"0\"},{\"optionId\":59,\"optionKey\":\"C\",\"optionContent\":\"栈\",\"isRight\":\"1\"},{\"optionId\":60,\"optionKey\":\"D\",\"optionContent\":\"二叉树\",\"isRight\":\"0\"}]},{\"questionId\":16,\"courseId\":null,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"在关系型数据库中，用于唯一标识表中每一行记录的字段或字段组合称为？\",\"answer\":\"A\",\"analysis\":\"主键是关系型数据库表中的一个或多个字段，其值能唯一地标识表中的每一行记录。外键用于建立表与表之间的关联，索引用于提高查询速度，约束用于保证数据的完整性。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774081217792\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":61,\"optionKey\":\"A\",\"optionContent\":\"主键\",\"isRight\":\"1\"},{\"optionId\":62,\"optionKey\":\"B\",\"optionContent\":\"外键\",\"isRight\":\"0\"},{\"optionId\":63,\"optionKey\":\"C\",\"optionContent\":\"索引\",\"isRight\":\"0\"},{\"optionId\":64,\"optionKey\":\"D\",\"optionContent\":\"约束\",\"isRight\":\"0\"}]},{\"questionId\":17,\"courseId\":null,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"在Python中，用于创建一个新的类定义的关键字是？\",\"answer\":\"B\",\"analysis\":\"在Python中，使用关键字`class`来定义一个新的类。`def`用于定义函数，`new`不是用于定义类的关键字，`object`是所有类的基类。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774081217792\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":65,\"optionKey\":\"A\",\"optionContent\":\"def\",\"isRight\":\"0\"},{\"optionId\":66,\"optionKey\":\"B\",\"optionContent\":\"class\",\"isRight\":\"1\"},{\"optionId\":67,\"optionKey\":\"C\",\"optionContent\":\"new\",\"isRight\":\"0\"},{\"optionId\":68,\"optionKey\":\"D\",\"optionContent\":\"object\",\"isRight\":\"0\"}]},{\"questionId\":18,\"courseId\":null,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"在Java语言中，下列哪个关键字用于声明一个常量，即其值在初始化后不能被修改？\",\"answer\":\"D\",\"analysis\":\"在Java中，`final`关键字用于声明常量，表示该变量的值一旦被赋值就不能再被更改。`static`表示静态成员，`const`是Java的保留字但并未使用，`volatile`用于确保变量的可见性。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774081217792\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":69,\"optionKey\":\"A\",\"optionContent\":\"static\",\"isRight\":\"0\"},{\"optionId\":70,\"optionKey\":\"B\",\"optionContent\":\"const\",\"isRight\":\"0\"},{\"optionId\":71,\"optionKey\":\"C\",\"optionContent\":\"volatile\",\"isRight\":\"0\"},{\"optionId\":72,\"optionKey\":\"D\",\"optionContent\":\"final\",\"isRight\":\"1\"}]}]}}', 1301, 'SUCCESS', NULL, 27427, '2026-03-21 16:19:50');
INSERT INTO `sc_ai_task_log` VALUES (89, 'exam_question_generate', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":3200,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是高校与中学校园题库生成助手。你必须严格围绕当前课程与知识点出题，绝不能输出与课程无关的题目。如果课程是 Python、Java、数据库等学科课程，题目必须体现该课程本身的专业知识，而不是智慧校园平台、校园管理、教务流程等泛化主题。你需要优先参考课程名称、课程简介、章节目录和知识点信息，确保题目贴合课程教学内容。你必须只返回 JSON 数组，不允许返回 markdown，不允许解释，不允许额外说明。数组每一项格式固定为：{\\\"questionType\\\":\\\"single\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"...\\\",\\\"answer\\\":\\\"...\\\",\\\"analysis\\\":\\\"...\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"...\\\",\\\"isRight\\\":\\\"0\\\"}]}。questionType 仅允许：single,multiple,judge,fill,essay,material,case。客观题必须提供 options；主观题 options 返回空数组。题干、答案、解析必须语言自然、专业准确、适合中国校园教学与考试场景，可直接入库。当课程上下文与用户补充要求冲突时，以课程和知识点上下文为最高优先级。\"},{\"role\":\"user\",\"content\":\"{\\\"courseId\\\":null,\\\"courseName\\\":\\\"\\\",\\\"courseCode\\\":\\\"\\\",\\\"subjectType\\\":\\\"\\\",\\\"courseIntro\\\":\\\"\\\",\\\"chapterOutline\\\":\\\"\\\",\\\"knowledgePointId\\\":null,\\\"knowledgeName\\\":\\\"\\\",\\\"knowledgeDescription\\\":\\\"\\\",\\\"questionType\\\":\\\"single\\\",\\\"questionTypeLabel\\\":\\\"单选题\\\",\\\"difficultyLevel\\\":3,\\\"count\\\":5,\\\"difficultyHint\\\":\\\"中等难度，兼顾概念理解与基础应用\\\",\\\"instruction\\\":\\\"\\\",\\\"goal\\\":\\\"请基于上述课程上下文，生成可直接导入题库的试题数据，题目必须与课程强相关\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"8c2b617c-db10-405e-8d85-2a29b2d64d09\",\"object\":\"chat.completion\",\"created\":1774081190,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"[\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在面向对象编程中，用于描述对象所具有的行为或操作的术语是？\\\",\\n    \\\"answer\\\": \\\"B\\\",\\n    \\\"analysis\\\": \\\"在面向对象编程中，对象的行为或操作通常通过方法来定义和实现。属性描述对象的状态，类是对对象的抽象定义，而实例是类的具体化。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"属性\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"方法\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"类\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"实例\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"下列数据结构中，按照“先进后出”原则工作的是？\\\",\\n    \\\"answer\\\": \\\"C\\\",\\n    \\\"analysis\\\": \\\"栈是一种线性数据结构，其操作遵循“先进后出”的原则。队列是“先进先出”，链表和树没有固定的全局存取顺序。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"队列\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"链表\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"栈\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"二叉树\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在关系型数据库中，用于唯一标识表中每一行记录的字段或字段组合称为？\\\",\\n    \\\"answer\\\": \\\"A\\\",\\n    \\\"analysis\\\": \\\"主键是关系型数据库表中的一个或多个字段，其值能唯一地标识表中的每一行记录。外键用于建立表与表之间的关联，索引用于提高查询速度，约束用于保证数据的完整性。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"主键\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"外键\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"索引\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"约束\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在Python中，用于创建一个新的类定义的关键字是？\\\",\\n    \\\"answer\\\": \\\"B\\\",\\n    \\\"analysis\\\": \\\"在Python中，使用关键字`class`来定义一个新的类。`def`用于定义函数，`new`不是用于定义类的关键字，`object`是所有类的基类。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"def\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"class\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"new\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"object\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在Java语言中，下列哪个关键字用于声明一个常量，即其值在初始化后不能被修改？\\\",\\n    \\\"answer\\\": \\\"D\\\",\\n    \\\"analysis\\\": \\\"在Java中，`final`关键字用于声明常量，表示该变量的值一旦被赋值就不能再被更改。`static`表示静态成员，`const`是Java的保留字但并未使用，`volatile`用于确保变量的可见性。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"static\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"const\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"volatile\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"final\\\", \\\"isRight\\\": \\\"1\\\"}\\n    ]\\n  }\\n]\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":337,\"completion_tokens\":964,\"total_tokens\":1301,\"prompt_tokens_details\":{\"cached_tokens\":0},\"prompt_cache_hit_tokens\":0,\"prompt_cache_miss_tokens\":337},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\"}', 1301, 'SUCCESS', NULL, 27157, '2026-03-21 16:20:17');
INSERT INTO `sc_ai_task_log` VALUES (92, 'exam_question_generate_async', 2, 4, '{\"taskLabel\":\"AI异步出题\",\"courseId\":2,\"knowledgePointId\":null,\"modelId\":4,\"questionType\":\"multiple\",\"questionTypeLabel\":\"多选题\",\"difficultyLevel\":3,\"count\":5,\"saveToBank\":true,\"instruction\":\"\",\"operator\":\"admin\"}', '{\"stage\":\"completed\",\"message\":\"AI 出题完成，共生成5题，已入库5题\",\"result\":{\"generatedCount\":5,\"savedCount\":5,\"tokenUsed\":1583,\"rawContent\":\"[{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"在 Python 数据分析中，关于 Pandas 库的 DataFrame 数据结构，以下描述正确的有（ ）。\\\",\\\"answer\\\":\\\"A,B,D\\\",\\\"analysis\\\":\\\"DataFrame 是二维的表格型数据结构，具有行索引和列索引（A 正确）；它支持不同数据类型的列，如某一列为整数，另一列为字符串（B 正确）；DataFrame 是可变的数据结构，可以动态添加或删除行列（C 错误）；可以通过字典、列表嵌套等多种方式创建 DataFrame（D 正确）。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"是一种带有行索引和列索引的二维表格型数据结构\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"每一列可以是不同的数据类型\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"一旦创建，其大小和形状不可改变\\\",\\\"isRight\\\":\\\"0\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"可以通过字典列表或 NumPy 数组等多种方式创建\\\",\\\"isRight\\\":\\\"1\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"在使用 Matplotlib 进行数据可视化时，下列关于 pyplot 模块常用函数的说法，正确的有（ ）。\\\",\\\"answer\\\":\\\"A,C,D\\\",\\\"analysis\\\":\\\"plt.plot() 用于绘制折线图（A 正确）；plt.scatter() 用于绘制散点图，而非柱状图，柱状图应使用 plt.bar()（B 错误）；plt.xlabel() 和 plt.ylabel() 分别用于设置 x 轴和 y 轴的标签（C 正确）；plt.show() 用于显示当前生成的所有图形窗口（D 正确）。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"plot() 函数主要用于绘制折线图\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"scatter() 函数用于绘制柱状图\\\",\\\"isRight\\\":\\\"0\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"xlabel() 和 ylabel() 用于设置坐标轴标签\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"show() 函数用于将图形渲染并显示出来\\\",\\\"isRight\\\":\\\"1\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"关于 NumPy 数组（ndarray）的特性与操作，下列选项正确的有（ ）。\\\",\\\"answer\\\":\\\"A,B,C\\\",\\\"analysis\\\":\\\"NumPy 数组要求所有元素必须是相同的数据类型，以保证内存连续和计算效率（A 正确）；ndarray 支持广播机制（Broadcasting），允许不同形状的数组进行算术运算（B 正确）；通过 shape 属性可以查看或修改数组的维度形状（C 正确）；NumPy 数组通常比同等大小的 Python 原生列表占用更少的内存且运算速度更快（D 错误）。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"数组中的所有元素必须具有相同的数据类型\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"支持广播机制，可进行不同形状数组间的运算\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"可以通过 shape 属性获取或修改数组的维度\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"相比 Python 原生列表，占用内存更大但灵活性更高\\\",\\\"isRight\\\":\\\"0\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"在利用 Pandas 进行数据清洗时，处理缺失值（NaN）的常用方法包括（ ）。\\\",\\\"answer\\\":\\\"A,B,C,D\\\",\\\"analysis\\\":\\\"dropna() 可以直接删除包含缺失值的行或列（A 正确）；fillna() 可以使用指定值（如 0、均值等）填充缺失值（B 正确）；interpolate() 可以根据前后数据进行插值填充（C 正确）；isnull() 或 isna() 常用于检测缺失值，是处理流程中的关键步骤，虽不直接“处理”但属于处理策略的一部分，且在多选语境下常作为识别手段被纳入考量，若严格指“填补/删除”动作，前三者最核心，但在广义数据处理流程中，识别也是处理的一环。此处题目问“处理方法”，通常涵盖识别后的处置策略，故全选符合数据分析教学常规认知。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"使用 dropna() 方法直接删除含有缺失值的记录\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"使用 fillna() 方法用特定值或统计量填充缺失值\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"使用 interpolate() 方法进行插值填充\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"使用 isnull() 检测后结合逻辑判断进行自定义处理\\\",\\\"isRight\\\":\\\"1\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"下列关于 Python 数据分析生态系统中各库主要功能的描述，正确的有（ ）。\\\",\\\"answer\\\":\\\"A,B,D\\\",\\\"analysis\\\":\\\"NumPy 提供高性能的多维数组对象及数学函数，是数据分析的基础（A 正确）；Pandas 基于 NumPy 构建，提供 DataFrame 和 Series 结构，擅长数据操纵和分析（B 正确）；Scikit-learn 是机器学习库，虽然依赖数据分析结果，但其核心功能不是数据可视化，可视化主要由 Matplotlib 和 Seaborn 负责（C 错误）；Matplotlib 是基础的绘图库，可用于生成各种静态、动态和交互式可视化图表（D 正确）。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"NumPy 主要用于高效的数值计算和多维数组操作\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"Pandas 提供了强大的数据结构和数据分析工具\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"Scikit-learn 是专门用于高级数据可视化的核心库\\\",\\\"isRight\\\":\\\"0\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"Matplotlib 是 Python 中最基础且广泛使用的绘图库\\\",\\\"isRight\\\":\\\"1\\\"}]}]\",\"questions\":[{\"questionId\":19,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"multiple\",\"difficultyLevel\":3,\"stem\":\"在 Python 数据分析中，关于 Pandas 库的 DataFrame 数据结构，以下描述正确的有（ ）。\",\"answer\":\"A,B,D\",\"analysis\":\"DataFrame 是二维的表格型数据结构，具有行索引和列索引（A 正确）；它支持不同数据类型的列，如某一列为整数，另一列为字符串（B 正确）；DataFrame 是可变的数据结构，可以动态添加或删除行列（C 错误）；可以通过字典、列表嵌套等多种方式创建 DataFrame（D 正确）。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774081794756\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":73,\"optionKey\":\"A\",\"optionContent\":\"是一种带有行索引和列索引的二维表格型数据结构\",\"isRight\":\"1\"},{\"optionId\":74,\"optionKey\":\"B\",\"optionContent\":\"每一列可以是不同的数据类型\",\"isRight\":\"1\"},{\"optionId\":75,\"optionKey\":\"C\",\"optionContent\":\"一旦创建，其大小和形状不可改变\",\"isRight\":\"0\"},{\"optionId\":76,\"optionKey\":\"D\",\"optionContent\":\"可以通过字典列表或 NumPy 数组等多种方式创建\",\"isRight\":\"1\"}]},{\"questionId\":20,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"multiple\",\"difficultyLevel\":3,\"stem\":\"在使用 Matplotlib 进行数据可视化时，下列关于 pyplot 模块常用函数的说法，正确的有（ ）。\",\"answer\":\"A,C,D\",\"analysis\":\"plt.plot() 用于绘制折线图（A 正确）；plt.scatter() 用于绘制散点图，而非柱状图，柱状图应使用 plt.bar()（B 错误）；plt.xlabel() 和 plt.ylabel() 分别用于设置 x 轴和 y 轴的标签（C 正确）；plt.show() 用于显示当前生成的所有图形窗口（D 正确）。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774081794757\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":77,\"optionKey\":\"A\",\"optionContent\":\"plot() 函数主要用于绘制折线图\",\"isRight\":\"1\"},{\"optionId\":78,\"optionKey\":\"B\",\"optionContent\":\"scatter() 函数用于绘制柱状图\",\"isRight\":\"0\"},{\"optionId\":79,\"optionKey\":\"C\",\"optionContent\":\"xlabel() 和 ylabel() 用于设置坐标轴标签\",\"isRight\":\"1\"},{\"optionId\":80,\"optionKey\":\"D\",\"optionContent\":\"show() 函数用于将图形渲染并显示出来\",\"isRight\":\"1\"}]},{\"questionId\":21,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"multiple\",\"difficultyLevel\":3,\"stem\":\"关于 NumPy 数组（ndarray）的特性与操作，下列选项正确的有（ ）。\",\"answer\":\"A,B,C\",\"analysis\":\"NumPy 数组要求所有元素必须是相同的数据类型，以保证内存连续和计算效率（A 正确）；ndarray 支持广播机制（Broadcasting），允许不同形状的数组进行算术运算（B 正确）；通过 shape 属性可以查看或修改数组的维度形状（C 正确）；NumPy 数组通常比同等大小的 Python 原生列表占用更少的内存且运算速度更快（D 错误）。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774081794757\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":81,\"optionKey\":\"A\",\"optionContent\":\"数组中的所有元素必须具有相同的数据类型\",\"isRight\":\"1\"},{\"optionId\":82,\"optionKey\":\"B\",\"optionContent\":\"支持广播机制，可进行不同形状数组间的运算\",\"isRight\":\"1\"},{\"optionId\":83,\"optionKey\":\"C\",\"optionContent\":\"可以通过 shape 属性获取或修改数组的维度\",\"isRight\":\"1\"},{\"optionId\":84,\"optionKey\":\"D\",\"optionContent\":\"相比 Python 原生列表，占用内存更大但灵活性更高\",\"isRight\":\"0\"}]},{\"questionId\":22,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"multiple\",\"difficultyLevel\":3,\"stem\":\"在利用 Pandas 进行数据清洗时，处理缺失值（NaN）的常用方法包括（ ）。\",\"answer\":\"A,B,C,D\",\"analysis\":\"dropna() 可以直接删除包含缺失值的行或列（A 正确）；fillna() 可以使用指定值（如 0、均值等）填充缺失值（B 正确）；interpolate() 可以根据前后数据进行插值填充（C 正确）；isnull() 或 isna() 常用于检测缺失值，是处理流程中的关键步骤，虽不直接“处理”但属于处理策略的一部分，且在多选语境下常作为识别手段被纳入考量，若严格指“填补/删除”动作，前三者最核心，但在广义数据处理流程中，识别也是处理的一环。此处题目问“处理方法”，通常涵盖识别后的处置策略，故全选符合数据分析教学常规认知。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774081794757\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":85,\"optionKey\":\"A\",\"optionContent\":\"使用 dropna() 方法直接删除含有缺失值的记录\",\"isRight\":\"1\"},{\"optionId\":86,\"optionKey\":\"B\",\"optionContent\":\"使用 fillna() 方法用特定值或统计量填充缺失值\",\"isRight\":\"1\"},{\"optionId\":87,\"optionKey\":\"C\",\"optionContent\":\"使用 interpolate() 方法进行插值填充\",\"isRight\":\"1\"},{\"optionId\":88,\"optionKey\":\"D\",\"optionContent\":\"使用 isnull() 检测后结合逻辑判断进行自定义处理\",\"isRight\":\"1\"}]},{\"questionId\":23,\"courseId\":2,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"multiple\",\"difficultyLevel\":3,\"stem\":\"下列关于 Python 数据分析生态系统中各库主要功能的描述，正确的有（ ）。\",\"answer\":\"A,B,D\",\"analysis\":\"NumPy 提供高性能的多维数组对象及数学函数，是数据分析的基础（A 正确）；Pandas 基于 NumPy 构建，提供 DataFrame 和 Series 结构，擅长数据操纵和分析（B 正确）；Scikit-learn 是机器学习库，虽然依赖数据分析结果，但其核心功能不是数据可视化，可视化主要由 Matplotlib 和 Seaborn 负责（C 错误）；Matplotlib 是基础的绘图库，可用于生成各种静态、动态和交互式可视化图表（D 正确）。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774081794757\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":89,\"optionKey\":\"A\",\"optionContent\":\"NumPy 主要用于高效的数值计算和多维数组操作\",\"isRight\":\"1\"},{\"optionId\":90,\"optionKey\":\"B\",\"optionContent\":\"Pandas 提供了强大的数据结构和数据分析工具\",\"isRight\":\"1\"},{\"optionId\":91,\"optionKey\":\"C\",\"optionContent\":\"Scikit-learn 是专门用于高级数据可视化的核心库\",\"isRight\":\"0\"},{\"optionId\":92,\"optionKey\":\"D\",\"optionContent\":\"Matplotlib 是 Python 中最基础且广泛使用的绘图库\",\"isRight\":\"1\"}]}]}}', 1583, 'SUCCESS', NULL, 22735, '2026-03-21 16:29:32');
INSERT INTO `sc_ai_task_log` VALUES (93, 'exam_question_generate', 4, 4, '{\"enable_thinking\":false,\"max_tokens\":6400,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是高校与中学校园题库生成助手。你必须严格围绕当前课程与知识点出题，绝不能输出与课程无关的题目。如果课程是 Python、Java、数据库等学科课程，题目必须体现该课程本身的专业知识，而不是智慧校园平台、校园管理、教务流程等泛化主题。你需要优先参考课程名称、课程简介、章节目录和知识点信息，确保题目贴合课程教学内容。你必须只返回 JSON 数组，不允许返回 markdown，不允许解释，不允许额外说明。数组每一项格式固定为：{\\\"questionType\\\":\\\"single\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"...\\\",\\\"answer\\\":\\\"...\\\",\\\"analysis\\\":\\\"...\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"...\\\",\\\"isRight\\\":\\\"0\\\"}]}。questionType 仅允许：single,multiple,judge,fill,essay,material,case。客观题必须提供 options；主观题 options 返回空数组。题干、答案、解析必须语言自然、专业准确、适合中国校园教学与考试场景，可直接入库。当课程上下文与用户补充要求冲突时，以课程和知识点上下文为最高优先级。\"},{\"role\":\"user\",\"content\":\"{\\\"courseId\\\":2,\\\"courseName\\\":\\\"Python数据分析\\\",\\\"courseCode\\\":\\\"0503B205\\\",\\\"subjectType\\\":\\\"理论\\\",\\\"courseIntro\\\":\\\"\\\",\\\"chapterOutline\\\":\\\"\\\",\\\"knowledgePointId\\\":null,\\\"knowledgeName\\\":\\\"\\\",\\\"knowledgeDescription\\\":\\\"\\\",\\\"questionType\\\":\\\"multiple\\\",\\\"questionTypeLabel\\\":\\\"多选题\\\",\\\"difficultyLevel\\\":3,\\\"count\\\":5,\\\"difficultyHint\\\":\\\"中等难度，兼顾概念理解与基础应用\\\",\\\"instruction\\\":\\\"\\\",\\\"goal\\\":\\\"请基于上述课程上下文，生成可直接导入题库的试题数据，题目必须与课程强相关\\\"}\"}],\"model\":\"qwen3.5-plus\"}', '{\"choices\":[{\"message\":{\"content\":\"[{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"在 Python 数据分析中，关于 Pandas 库的 DataFrame 数据结构，以下描述正确的有（ ）。\\\",\\\"answer\\\":\\\"A,B,D\\\",\\\"analysis\\\":\\\"DataFrame 是二维的表格型数据结构，具有行索引和列索引（A 正确）；它支持不同数据类型的列，如某一列为整数，另一列为字符串（B 正确）；DataFrame 是可变的数据结构，可以动态添加或删除行列（C 错误）；可以通过字典、列表嵌套等多种方式创建 DataFrame（D 正确）。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"是一种带有行索引和列索引的二维表格型数据结构\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"每一列可以是不同的数据类型\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"一旦创建，其大小和形状不可改变\\\",\\\"isRight\\\":\\\"0\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"可以通过字典列表或 NumPy 数组等多种方式创建\\\",\\\"isRight\\\":\\\"1\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"在使用 Matplotlib 进行数据可视化时，下列关于 pyplot 模块常用函数的说法，正确的有（ ）。\\\",\\\"answer\\\":\\\"A,C,D\\\",\\\"analysis\\\":\\\"plt.plot() 用于绘制折线图（A 正确）；plt.scatter() 用于绘制散点图，而非柱状图，柱状图应使用 plt.bar()（B 错误）；plt.xlabel() 和 plt.ylabel() 分别用于设置 x 轴和 y 轴的标签（C 正确）；plt.show() 用于显示当前生成的所有图形窗口（D 正确）。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"plot() 函数主要用于绘制折线图\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"scatter() 函数用于绘制柱状图\\\",\\\"isRight\\\":\\\"0\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"xlabel() 和 ylabel() 用于设置坐标轴标签\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"show() 函数用于将图形渲染并显示出来\\\",\\\"isRight\\\":\\\"1\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"关于 NumPy 数组（ndarray）的特性与操作，下列选项正确的有（ ）。\\\",\\\"answer\\\":\\\"A,B,C\\\",\\\"analysis\\\":\\\"NumPy 数组要求所有元素必须是相同的数据类型，以保证内存连续和计算效率（A 正确）；ndarray 支持广播机制（Broadcasting），允许不同形状的数组进行算术运算（B 正确）；通过 shape 属性可以查看或修改数组的维度形状（C 正确）；NumPy 数组通常比同等大小的 Python 原生列表占用更少的内存且运算速度更快（D 错误）。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"数组中的所有元素必须具有相同的数据类型\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"支持广播机制，可进行不同形状数组间的运算\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"可以通过 shape 属性获取或修改数组的维度\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"相比 Python 原生列表，占用内存更大但灵活性更高\\\",\\\"isRight\\\":\\\"0\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"在利用 Pandas 进行数据清洗时，处理缺失值（NaN）的常用方法包括（ ）。\\\",\\\"answer\\\":\\\"A,B,C,D\\\",\\\"analysis\\\":\\\"dropna() 可以直接删除包含缺失值的行或列（A 正确）；fillna() 可以使用指定值（如 0、均值等）填充缺失值（B 正确）；interpolate() 可以根据前后数据进行插值填充（C 正确）；isnull() 或 isna() 常用于检测缺失值，是处理流程中的关键步骤，虽不直接“处理”但属于处理策略的一部分，且在多选语境下常作为识别手段被纳入考量，若严格指“填补/删除”动作，前三者最核心，但在广义数据处理流程中，识别也是处理的一环。此处题目问“处理方法”，通常涵盖识别后的处置策略，故全选符合数据分析教学常规认知。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"使用 dropna() 方法直接删除含有缺失值的记录\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"使用 fillna() 方法用特定值或统计量填充缺失值\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"使用 interpolate() 方法进行插值填充\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"使用 isnull() 检测后结合逻辑判断进行自定义处理\\\",\\\"isRight\\\":\\\"1\\\"}]},{\\\"questionType\\\":\\\"multiple\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"下列关于 Python 数据分析生态系统中各库主要功能的描述，正确的有（ ）。\\\",\\\"answer\\\":\\\"A,B,D\\\",\\\"analysis\\\":\\\"NumPy 提供高性能的多维数组对象及数学函数，是数据分析的基础（A 正确）；Pandas 基于 NumPy 构建，提供 DataFrame 和 Series 结构，擅长数据操纵和分析（B 正确）；Scikit-learn 是机器学习库，虽然依赖数据分析结果，但其核心功能不是数据可视化，可视化主要由 Matplotlib 和 Seaborn 负责（C 错误）；Matplotlib 是基础的绘图库，可用于生成各种静态、动态和交互式可视化图表（D 正确）。\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"NumPy 主要用于高效的数值计算和多维数组操作\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"B\\\",\\\"optionContent\\\":\\\"Pandas 提供了强大的数据结构和数据分析工具\\\",\\\"isRight\\\":\\\"1\\\"},{\\\"optionKey\\\":\\\"C\\\",\\\"optionContent\\\":\\\"Scikit-learn 是专门用于高级数据可视化的核心库\\\",\\\"isRight\\\":\\\"0\\\"},{\\\"optionKey\\\":\\\"D\\\",\\\"optionContent\\\":\\\"Matplotlib 是 Python 中最基础且广泛使用的绘图库\\\",\\\"isRight\\\":\\\"1\\\"}]}]\",\"role\":\"assistant\"},\"finish_reason\":\"stop\",\"index\":0,\"logprobs\":null}],\"object\":\"chat.completion\",\"usage\":{\"prompt_tokens\":345,\"completion_tokens\":1238,\"total_tokens\":1583,\"completion_tokens_details\":{\"text_tokens\":1238},\"prompt_tokens_details\":{\"text_tokens\":345}},\"created\":1774081795,\"system_fingerprint\":null,\"model\":\"qwen3.5-plus\",\"id\":\"chatcmpl-afa7d4e6-dcfc-9a81-a9d9-6452afeab32a\"}', 1583, 'SUCCESS', NULL, 22418, '2026-03-21 16:29:54');
INSERT INTO `sc_ai_task_log` VALUES (94, 'qa', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":1024,\"stream\":true,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是智慧校园自主学习助手，请围绕课程学习、资源推荐、知识点理解与考试答疑做出简洁、专业的回答。\"},{\"role\":\"user\",\"content\":\"hello\"}],\"model\":\"deepseek-chat\",\"stream_options\":{\"include_usage\":true}}', 'data: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"role\":\"assistant\",\"content\":\"\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"你好\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"！\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"我是\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"你的\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"智慧\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"校园\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"学习\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"助手\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"，\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"可以\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"为你\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"提供\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"以下\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"帮助\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"：\\n\\n\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"1\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\".\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\" **\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"课程\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"学习\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"**\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"：\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"解答\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"课程\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"疑问\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"、\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"梳理\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"知识\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"框架\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"、\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"制定\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"学习\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"计划\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"。\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"  \\n\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"2\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\".\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\" **\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"资源\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"推荐\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"**\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"：\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"根据\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"你的\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"需求\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"推荐\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"教材\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"、\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"论文\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"、\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"在线\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"课程\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"或\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"工具\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"。\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"  \\n\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"3\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\".\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\" **\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"知识点\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"解析\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"**\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"：\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"针对\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"复杂\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"概念\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"提供\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"通俗\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"易懂\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"的解释\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"或\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"示例\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"。\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"  \\n\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"4\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\".\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\" **\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"考试\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"答疑\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"**\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"：\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"协助\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"复习\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"重点\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"、\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"解析\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"题目\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"、\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"分享\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"备考\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"技巧\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"。\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"  \\n\\n\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"请\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"告诉我\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"你的\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"具体\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"需求\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"，\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"我会\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"为你\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"提供\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"简洁\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"专业的\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"支持\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"！\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\" 📚\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"✨\"},\"logprobs\":null,\"finish_reason\":null}],\"usage\":null}\n\ndata: {\"id\":\"3242115a-2075-46be-a063-0ec673b1ba69\",\"object\":\"chat.completion.chunk\",\"created\":1774247956,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":30,\"completion_tokens\":109,\"total_tokens\":139,\"prompt_tokens_details\":{\"cached_tokens\":0},\"prompt_cache_hit_tokens\":0,\"prompt_cache_miss_tokens\":30}}\n\ndata: [DONE]\n', 139, 'SUCCESS', NULL, 4818, '2026-03-23 14:39:21');
INSERT INTO `sc_ai_task_log` VALUES (95, 'exam_question_generate_async', NULL, 1, '{\"taskLabel\":\"AI异步出题\",\"courseId\":null,\"knowledgePointId\":null,\"modelId\":1,\"questionType\":\"single\",\"questionTypeLabel\":\"单选题\",\"difficultyLevel\":3,\"count\":5,\"saveToBank\":true,\"instruction\":\"出适合c语言都题目，指针相关。\",\"operator\":\"admin\"}', '{\"stage\":\"completed\",\"message\":\"AI 出题完成，共生成5题，已入库5题\",\"result\":{\"generatedCount\":5,\"savedCount\":5,\"tokenUsed\":1606,\"rawContent\":\"[\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在C语言中，若有声明 `int a = 10, *p = &a;`，则表达式 `*p + 1` 的值是多少？\\\",\\n    \\\"answer\\\": \\\"11\\\",\\n    \\\"analysis\\\": \\\"指针p指向整型变量a，a的值为10。`*p`表示取p所指向地址的内容，即a的值10。因此`*p + 1`的值为10 + 1 = 11。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"10\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"11\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"变量a的地址加1\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"指针p的值加1\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"以下关于C语言指针的叙述，正确的是？\\\",\\n    \\\"answer\\\": \\\"指针变量存储的是内存地址\\\",\\n    \\\"analysis\\\": \\\"指针的本质是一个变量，其值为另一个变量的内存地址。选项A错误，因为指针变量本身也占用内存空间；选项C错误，指针可以指向任何数据类型，包括void类型；选项D错误，指针可以进行加减运算，但其运算单位取决于所指向数据类型的大小。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"指针变量不占用内存空间\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"指针变量存储的是内存地址\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"指针只能指向基本数据类型（如int, char）\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"指针不能进行加减运算\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"若有代码片段：`int arr[3] = {1, 2, 3}; int *ptr = arr;`，则 `*(ptr + 2)` 的值是？\\\",\\n    \\\"answer\\\": \\\"3\\\",\\n    \\\"analysis\\\": \\\"数组名arr在大多数表达式中代表数组首元素的地址，即&arr[0]。`ptr = arr`使得ptr指向arr[0]。`ptr + 2`根据指针算术，指向arr[0]向后偏移2个int大小的位置，即arr[2]的地址。`*(ptr + 2)`解引用得到arr[2]的值，即3。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"1\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"2\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"3\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"编译错误\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在C语言中，以下哪种方式可以正确地声明一个指向整型常量的指针（即指针指向的值不可修改，但指针本身可以指向别的地址）？\\\",\\n    \\\"answer\\\": \\\"const int *p;\\\",\\n    \\\"analysis\\\": \\\"`const int *p;` 或 `int const *p;` 声明了一个指向常量整数的指针。指针p可以修改（指向不同的地址），但不能通过*p来修改它所指向的整数值。选项B声明了一个指向整数的常量指针（指针本身不可修改）；选项C声明了一个指向常量整数的常量指针；选项D语法错误。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"const int *p;\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"int * const p;\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"const int * const p;\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"int const p*;\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"对于函数 `void swap(int *x, int *y) { int t = *x; *x = *y; *y = t; }`，以下调用方式中，能正确交换变量a和b值的是？（假设a, b为已定义的int变量）\\\",\\n    \\\"answer\\\": \\\"swap(&a, &b);\\\",\\n    \\\"analysis\\\": \\\"函数swap的参数是指向int的指针，目的是交换两个指针所指向的变量的值。因此，调用时需要传递变量的地址（即&a和&b）。选项A传递的是变量值，选项B和C的语法或逻辑错误。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"swap(a, b);\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"swap(*a, *b);\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"swap(int *a, int *b);\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"swap(&a, &b);\\\", \\\"isRight\\\": \\\"1\\\"}\\n    ]\\n  }\\n]\",\"questions\":[{\"questionId\":24,\"courseId\":null,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"在C语言中，若有声明 `int a = 10, *p = &a;`，则表达式 `*p + 1` 的值是多少？\",\"answer\":\"B\",\"analysis\":\"指针p指向整型变量a，a的值为10。`*p`表示取p所指向地址的内容，即a的值10。因此`*p + 1`的值为10 + 1 = 11。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774252388152\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":97,\"optionKey\":\"A\",\"optionContent\":\"10\",\"isRight\":\"0\"},{\"optionId\":98,\"optionKey\":\"B\",\"optionContent\":\"11\",\"isRight\":\"1\"},{\"optionId\":99,\"optionKey\":\"C\",\"optionContent\":\"变量a的地址加1\",\"isRight\":\"0\"},{\"optionId\":100,\"optionKey\":\"D\",\"optionContent\":\"指针p的值加1\",\"isRight\":\"0\"}]},{\"questionId\":25,\"courseId\":null,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"以下关于C语言指针的叙述，正确的是？\",\"answer\":\"B\",\"analysis\":\"指针的本质是一个变量，其值为另一个变量的内存地址。选项A错误，因为指针变量本身也占用内存空间；选项C错误，指针可以指向任何数据类型，包括void类型；选项D错误，指针可以进行加减运算，但其运算单位取决于所指向数据类型的大小。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774252388160\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":101,\"optionKey\":\"A\",\"optionContent\":\"指针变量不占用内存空间\",\"isRight\":\"0\"},{\"optionId\":102,\"optionKey\":\"B\",\"optionContent\":\"指针变量存储的是内存地址\",\"isRight\":\"1\"},{\"optionId\":103,\"optionKey\":\"C\",\"optionContent\":\"指针只能指向基本数据类型（如int, char）\",\"isRight\":\"0\"},{\"optionId\":104,\"optionKey\":\"D\",\"optionContent\":\"指针不能进行加减运算\",\"isRight\":\"0\"}]},{\"questionId\":26,\"courseId\":null,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"若有代码片段：`int arr[3] = {1, 2, 3}; int *ptr = arr;`，则 `*(ptr + 2)` 的值是？\",\"answer\":\"C\",\"analysis\":\"数组名arr在大多数表达式中代表数组首元素的地址，即&arr[0]。`ptr = arr`使得ptr指向arr[0]。`ptr + 2`根据指针算术，指向arr[0]向后偏移2个int大小的位置，即arr[2]的地址。`*(ptr + 2)`解引用得到arr[2]的值，即3。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774252388160\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":105,\"optionKey\":\"A\",\"optionContent\":\"1\",\"isRight\":\"0\"},{\"optionId\":106,\"optionKey\":\"B\",\"optionContent\":\"2\",\"isRight\":\"0\"},{\"optionId\":107,\"optionKey\":\"C\",\"optionContent\":\"3\",\"isRight\":\"1\"},{\"optionId\":108,\"optionKey\":\"D\",\"optionContent\":\"编译错误\",\"isRight\":\"0\"}]},{\"questionId\":27,\"courseId\":null,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"在C语言中，以下哪种方式可以正确地声明一个指向整型常量的指针（即指针指向的值不可修改，但指针本身可以指向别的地址）？\",\"answer\":\"A\",\"analysis\":\"`const int *p;` 或 `int const *p;` 声明了一个指向常量整数的指针。指针p可以修改（指向不同的地址），但不能通过*p来修改它所指向的整数值。选项B声明了一个指向整数的常量指针（指针本身不可修改）；选项C声明了一个指向常量整数的常量指针；选项D语法错误。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774252388160\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":109,\"optionKey\":\"A\",\"optionContent\":\"const int *p;\",\"isRight\":\"1\"},{\"optionId\":110,\"optionKey\":\"B\",\"optionContent\":\"int * const p;\",\"isRight\":\"0\"},{\"optionId\":111,\"optionKey\":\"C\",\"optionContent\":\"const int * const p;\",\"isRight\":\"0\"},{\"optionId\":112,\"optionKey\":\"D\",\"optionContent\":\"int const p*;\",\"isRight\":\"0\"}]},{\"questionId\":28,\"courseId\":null,\"chapterId\":null,\"knowledgePointId\":null,\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"对于函数 `void swap(int *x, int *y) { int t = *x; *x = *y; *y = t; }`，以下调用方式中，能正确交换变量a和b值的是？（假设a, b为已定义的int变量）\",\"answer\":\"D\",\"analysis\":\"函数swap的参数是指向int的指针，目的是交换两个指针所指向的变量的值。因此，调用时需要传递变量的地址（即&a和&b）。选项A传递的是变量值，选项B和C的语法或逻辑错误。\",\"source\":\"AI智能出题\",\"questionTags\":\"\",\"materialContent\":\"\",\"sourceBatchNo\":\"AI-1774252388160\",\"qualityScore\":0.00,\"status\":\"0\",\"options\":[{\"optionId\":113,\"optionKey\":\"A\",\"optionContent\":\"swap(a, b);\",\"isRight\":\"0\"},{\"optionId\":114,\"optionKey\":\"B\",\"optionContent\":\"swap(*a, *b);\",\"isRight\":\"0\"},{\"optionId\":115,\"optionKey\":\"C\",\"optionContent\":\"swap(int *a, int *b);\",\"isRight\":\"0\"},{\"optionId\":116,\"optionKey\":\"D\",\"optionContent\":\"swap(&a, &b);\",\"isRight\":\"1\"}]}]}}', 1606, 'SUCCESS', NULL, 59523, '2026-03-23 15:52:08');
INSERT INTO `sc_ai_task_log` VALUES (96, 'exam_question_generate', 1, 1, '{\"enable_thinking\":false,\"max_tokens\":6400,\"stream\":false,\"temperature\":0.3,\"messages\":[{\"role\":\"system\",\"content\":\"你是高校与中学校园题库生成助手。你必须严格围绕当前课程与知识点出题，绝不能输出与课程无关的题目。如果课程是 Python、Java、数据库等学科课程，题目必须体现该课程本身的专业知识，而不是智慧校园平台、校园管理、教务流程等泛化主题。你需要优先参考课程名称、课程简介、章节目录和知识点信息，确保题目贴合课程教学内容。你必须只返回 JSON 数组，不允许返回 markdown，不允许解释，不允许额外说明。数组每一项格式固定为：{\\\"questionType\\\":\\\"single\\\",\\\"difficultyLevel\\\":3,\\\"stem\\\":\\\"...\\\",\\\"answer\\\":\\\"...\\\",\\\"analysis\\\":\\\"...\\\",\\\"options\\\":[{\\\"optionKey\\\":\\\"A\\\",\\\"optionContent\\\":\\\"...\\\",\\\"isRight\\\":\\\"0\\\"}]}。questionType 仅允许：single,multiple,judge,fill,essay,material,case。客观题必须提供 options；主观题 options 返回空数组。题干、答案、解析必须语言自然、专业准确、适合中国校园教学与考试场景，可直接入库。当课程上下文与用户补充要求冲突时，以课程和知识点上下文为最高优先级。\"},{\"role\":\"user\",\"content\":\"{\\\"courseId\\\":null,\\\"courseName\\\":\\\"\\\",\\\"courseCode\\\":\\\"\\\",\\\"subjectType\\\":\\\"\\\",\\\"courseIntro\\\":\\\"\\\",\\\"chapterOutline\\\":\\\"\\\",\\\"knowledgePointId\\\":null,\\\"knowledgeName\\\":\\\"\\\",\\\"knowledgeDescription\\\":\\\"\\\",\\\"questionType\\\":\\\"single\\\",\\\"questionTypeLabel\\\":\\\"单选题\\\",\\\"difficultyLevel\\\":3,\\\"count\\\":5,\\\"difficultyHint\\\":\\\"中等难度，兼顾概念理解与基础应用\\\",\\\"instruction\\\":\\\"出适合c语言都题目，指针相关。\\\",\\\"goal\\\":\\\"请基于上述课程上下文，生成可直接导入题库的试题数据，题目必须与课程强相关\\\"}\"}],\"model\":\"deepseek-chat\"}', '{\"id\":\"c4dc3ee7-2a5e-4a21-a211-8e8c978844f4\",\"object\":\"chat.completion\",\"created\":1774252348,\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"[\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在C语言中，若有声明 `int a = 10, *p = &a;`，则表达式 `*p + 1` 的值是多少？\\\",\\n    \\\"answer\\\": \\\"11\\\",\\n    \\\"analysis\\\": \\\"指针p指向整型变量a，a的值为10。`*p`表示取p所指向地址的内容，即a的值10。因此`*p + 1`的值为10 + 1 = 11。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"10\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"11\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"变量a的地址加1\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"指针p的值加1\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"以下关于C语言指针的叙述，正确的是？\\\",\\n    \\\"answer\\\": \\\"指针变量存储的是内存地址\\\",\\n    \\\"analysis\\\": \\\"指针的本质是一个变量，其值为另一个变量的内存地址。选项A错误，因为指针变量本身也占用内存空间；选项C错误，指针可以指向任何数据类型，包括void类型；选项D错误，指针可以进行加减运算，但其运算单位取决于所指向数据类型的大小。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"指针变量不占用内存空间\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"指针变量存储的是内存地址\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"指针只能指向基本数据类型（如int, char）\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"指针不能进行加减运算\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"若有代码片段：`int arr[3] = {1, 2, 3}; int *ptr = arr;`，则 `*(ptr + 2)` 的值是？\\\",\\n    \\\"answer\\\": \\\"3\\\",\\n    \\\"analysis\\\": \\\"数组名arr在大多数表达式中代表数组首元素的地址，即&arr[0]。`ptr = arr`使得ptr指向arr[0]。`ptr + 2`根据指针算术，指向arr[0]向后偏移2个int大小的位置，即arr[2]的地址。`*(ptr + 2)`解引用得到arr[2]的值，即3。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"1\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"2\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"3\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"编译错误\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"在C语言中，以下哪种方式可以正确地声明一个指向整型常量的指针（即指针指向的值不可修改，但指针本身可以指向别的地址）？\\\",\\n    \\\"answer\\\": \\\"const int *p;\\\",\\n    \\\"analysis\\\": \\\"`const int *p;` 或 `int const *p;` 声明了一个指向常量整数的指针。指针p可以修改（指向不同的地址），但不能通过*p来修改它所指向的整数值。选项B声明了一个指向整数的常量指针（指针本身不可修改）；选项C声明了一个指向常量整数的常量指针；选项D语法错误。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"const int *p;\\\", \\\"isRight\\\": \\\"1\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"int * const p;\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"const int * const p;\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"int const p*;\\\", \\\"isRight\\\": \\\"0\\\"}\\n    ]\\n  },\\n  {\\n    \\\"questionType\\\": \\\"single\\\",\\n    \\\"difficultyLevel\\\": 3,\\n    \\\"stem\\\": \\\"对于函数 `void swap(int *x, int *y) { int t = *x; *x = *y; *y = t; }`，以下调用方式中，能正确交换变量a和b值的是？（假设a, b为已定义的int变量）\\\",\\n    \\\"answer\\\": \\\"swap(&a, &b);\\\",\\n    \\\"analysis\\\": \\\"函数swap的参数是指向int的指针，目的是交换两个指针所指向的变量的值。因此，调用时需要传递变量的地址（即&a和&b）。选项A传递的是变量值，选项B和C的语法或逻辑错误。\\\",\\n    \\\"options\\\": [\\n      {\\\"optionKey\\\": \\\"A\\\", \\\"optionContent\\\": \\\"swap(a, b);\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"B\\\", \\\"optionContent\\\": \\\"swap(*a, *b);\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"C\\\", \\\"optionContent\\\": \\\"swap(int *a, int *b);\\\", \\\"isRight\\\": \\\"0\\\"},\\n      {\\\"optionKey\\\": \\\"D\\\", \\\"optionContent\\\": \\\"swap(&a, &b);\\\", \\\"isRight\\\": \\\"1\\\"}\\n    ]\\n  }\\n]\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":347,\"completion_tokens\":1259,\"total_tokens\":1606,\"prompt_tokens_details\":{\"cached_tokens\":0},\"prompt_cache_hit_tokens\":0,\"prompt_cache_miss_tokens\":347},\"system_fingerprint\":\"fp_eaab8d114b_prod0820_fp8_kvcache_new_kvcache\"}', 1606, 'SUCCESS', NULL, 59205, '2026-03-23 15:53:08');

-- ----------------------------
-- Table structure for sc_class
-- ----------------------------
DROP TABLE IF EXISTS `sc_class`;
CREATE TABLE `sc_class`  (
  `class_id` bigint NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `grade_id` bigint NOT NULL COMMENT '年级ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级名称',
  `head_teacher_id` bigint NULL DEFAULT NULL COMMENT '班主任用户ID',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`class_id`) USING BTREE,
  INDEX `idx_sc_class_grade_id`(`grade_id` ASC) USING BTREE,
  INDEX `idx_sc_class_head_teacher_id`(`head_teacher_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-班级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_class
-- ----------------------------
INSERT INTO `sc_class` VALUES (2, 2, 321, '计算机应用技术1班', 101, '0', 'admin', '2026-03-17 16:41:12', 'admin', '2026-03-18 20:40:40', '');

-- ----------------------------
-- Table structure for sc_class_course
-- ----------------------------
DROP TABLE IF EXISTS `sc_class_course`;
CREATE TABLE `sc_class_course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NULL DEFAULT NULL COMMENT '授课教师ID',
  `term_id` bigint NULL DEFAULT NULL COMMENT '学期ID',
  `business_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属业务类型',
  `teaching_class_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教学班代码',
  `credits` decimal(8, 2) NULL DEFAULT NULL COMMENT '学分',
  `course_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程类别',
  `open_dept_id` bigint NULL DEFAULT NULL COMMENT '开课部门ID',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `campus_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '授课校区',
  `assessment_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '考核方式',
  `teaching_language` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '授课语言',
  `prerequisite_course` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '先修课程',
  `task_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务类型',
  `required_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否必修(Y/N)',
  `course_level_requirement` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程等级要求',
  `total_hours` int NULL DEFAULT NULL COMMENT '要求总课时',
  `required_weeks` int NULL DEFAULT NULL COMMENT '要求周数',
  `arranged_hours` int NULL DEFAULT NULL COMMENT '已安排总课时',
  `weekly_hours` int NULL DEFAULT 0 COMMENT '每周课时数',
  `student_limit` int NULL DEFAULT 0 COMMENT '学生人数上限',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态：0-禁用 1-启用',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_class_course_term`(`class_id` ASC, `course_id` ASC, `term_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_term_id`(`term_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_class_course_term` FOREIGN KEY (`term_id`) REFERENCES `sc_school_term` (`term_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级课程关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_class_course
-- ----------------------------
INSERT INTO `sc_class_course` VALUES (1, 2, 2, 101, 1, '专科', '0503B205-004', 3.00, '理论', 321, '计算机应用技术', '本部', '考查', '中文授课', '无', '正常', 'N', '无', 90, 15, 90, 6, 50, '0', 'admin', '2026-03-17 16:46:04', 'admin', '2026-03-18 00:10:47', '');

-- ----------------------------
-- Table structure for sc_classroom
-- ----------------------------
DROP TABLE IF EXISTS `sc_classroom`;
CREATE TABLE `sc_classroom`  (
  `classroom_id` bigint NOT NULL AUTO_INCREMENT COMMENT '教室ID',
  `classroom_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教室名称',
  `building_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '楼栋名称',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID',
  `campus_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '校区名称',
  `capacity` int NULL DEFAULT 0 COMMENT '容量',
  `room_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教室类型',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态：0-正常 1-停用',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`classroom_id`) USING BTREE,
  UNIQUE INDEX `uk_classroom_name`(`classroom_name` ASC) USING BTREE,
  INDEX `idx_building_name`(`building_name` ASC) USING BTREE,
  INDEX `idx_campus_name`(`campus_name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教室表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_classroom
-- ----------------------------
INSERT INTO `sc_classroom` VALUES (1, '交换机路由实训室', '躬行楼', 321, '本部', 60, '机房', 0, '0', 'admin', '2026-03-17 18:42:43', 'admin', '2026-03-21 18:51:32', '');

-- ----------------------------
-- Table structure for sc_course
-- ----------------------------
DROP TABLE IF EXISTS `sc_course`;
CREATE TABLE `sc_course`  (
  `course_id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `course_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程编码',
  `subject_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学科类型',
  `grade_id` bigint NULL DEFAULT NULL COMMENT '所属年级ID',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面地址',
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '课程简介',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`course_id`) USING BTREE,
  INDEX `idx_sc_course_grade_id`(`grade_id` ASC) USING BTREE,
  INDEX `idx_sc_course_code`(`course_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-课程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_course
-- ----------------------------
INSERT INTO `sc_course` VALUES (2, 'Python数据分析', '0503B205', '理论', 2, NULL, '', '0', 'admin', '2026-03-17 16:45:19', 'admin', '2026-03-18 13:14:04', NULL);

-- ----------------------------
-- Table structure for sc_course_chapter
-- ----------------------------
DROP TABLE IF EXISTS `sc_course_chapter`;
CREATE TABLE `sc_course_chapter`  (
  `chapter_id` bigint NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父章节ID',
  `chapter_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '章节名称',
  `chapter_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`chapter_id`) USING BTREE,
  INDEX `idx_sc_course_chapter_course_parent`(`course_id` ASC, `parent_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-课程章节表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_course_chapter
-- ----------------------------

-- ----------------------------
-- Table structure for sc_course_schedule
-- ----------------------------
DROP TABLE IF EXISTS `sc_course_schedule`;
CREATE TABLE `sc_course_schedule`  (
  `schedule_id` bigint NOT NULL AUTO_INCREMENT COMMENT '排课ID',
  `term_id` bigint NOT NULL COMMENT '学期ID',
  `class_course_id` bigint NOT NULL COMMENT '班级课程关联ID（关联sc_class_course表）',
  `classroom_id` bigint NULL DEFAULT NULL COMMENT '教室ID（关联sc_classroom表）',
  `week_day` int NOT NULL COMMENT '星期几：1-周一 2-周二 ... 7-周日',
  `start_section` int NOT NULL COMMENT '开始节次（如：1）',
  `end_section` int NOT NULL COMMENT '结束节次（如：2）',
  `classroom` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教室名称',
  `classroom_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教室名称快照',
  `building_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '楼栋名称快照',
  `campus_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '校区名称快照',
  `weeks_text` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上课周次（如：1-18周）',
  `weeks_json` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '结构化周次JSON',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态：0-禁用 1-启用',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`schedule_id`) USING BTREE,
  INDEX `idx_term_id`(`term_id` ASC) USING BTREE,
  INDEX `idx_class_course_id`(`class_course_id` ASC) USING BTREE,
  INDEX `idx_week_day`(`week_day` ASC) USING BTREE,
  INDEX `idx_classroom_time`(`classroom` ASC, `week_day` ASC, `start_section` ASC, `end_section` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_classroom_id`(`classroom_id` ASC) USING BTREE,
  CONSTRAINT `fk_schedule_class_course` FOREIGN KEY (`class_course_id`) REFERENCES `sc_class_course` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_schedule_term` FOREIGN KEY (`term_id`) REFERENCES `sc_school_term` (`term_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程排课表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_course_schedule
-- ----------------------------
INSERT INTO `sc_course_schedule` VALUES (13, 1, 1, 1, 4, 3, 4, '交换机路由实训室', '交换机路由实训室', '躬行楼', '本部', '1-15周', '[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]', '0', 'admin', '2026-03-19 18:46:38', 'admin', '2026-03-21 18:51:32', '遗传算法自动排课生成');
INSERT INTO `sc_course_schedule` VALUES (14, 1, 1, 1, 1, 1, 4, '交换机路由实训室', '交换机路由实训室', '躬行楼', '本部', '1-15周', '[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]', '0', 'admin', '2026-03-20 11:17:36', 'admin', '2026-03-22 17:36:07', '遗传算法自动排课生成');

-- ----------------------------
-- Table structure for sc_course_student
-- ----------------------------
DROP TABLE IF EXISTS `sc_course_student`;
CREATE TABLE `sc_course_student`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `student_user_id` bigint NOT NULL COMMENT '学生用户ID',
  `class_id` bigint NULL DEFAULT NULL COMMENT '班级ID',
  `join_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sc_course_student`(`course_id` ASC, `student_user_id` ASC) USING BTREE,
  INDEX `idx_sc_course_student_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_sc_course_student_student`(`student_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-课程学生关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_course_student
-- ----------------------------
INSERT INTO `sc_course_student` VALUES (1, 2, 102, 2, '2026-03-17 18:20:01', '0', '');

-- ----------------------------
-- Table structure for sc_exam_answer
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_answer`;
CREATE TABLE `sc_exam_answer`  (
  `answer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '作答ID',
  `session_id` bigint NULL DEFAULT NULL COMMENT '考试会话ID',
  `session_paper_id` bigint NULL DEFAULT NULL COMMENT '子试卷记录ID',
  `record_id` bigint NOT NULL COMMENT '考试记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_answer` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户答案',
  `is_correct` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '是否正确(0否 1是)',
  `score` decimal(6, 2) NULL DEFAULT 0.00 COMMENT '得分',
  `knowledge_point_id` bigint NULL DEFAULT NULL COMMENT '知识点ID',
  PRIMARY KEY (`answer_id`) USING BTREE,
  INDEX `idx_sc_exam_answer_record_id`(`record_id` ASC) USING BTREE,
  INDEX `idx_sc_exam_answer_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_sc_exam_answer_session`(`session_id` ASC) USING BTREE,
  INDEX `idx_sc_exam_answer_session_paper`(`session_paper_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-作答明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_exam_answer
-- ----------------------------
INSERT INTO `sc_exam_answer` VALUES (1, NULL, NULL, 7, 8, 'D', '0', 0.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (2, NULL, NULL, 7, 7, 'B', '1', 25.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (3, NULL, NULL, 7, 6, 'C', '1', 25.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (4, NULL, NULL, 7, 5, 'B', '1', 25.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (5, NULL, NULL, 8, 8, '', '0', 0.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (6, NULL, NULL, 8, 7, '', '0', 0.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (7, NULL, NULL, 8, 6, '', '0', 0.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (8, NULL, NULL, 8, 5, '', '0', 0.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (9, 1, 1, 9, 8, 'B', '1', 20.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (10, 1, 1, 9, 14, 'B', '1', 20.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (11, 1, 1, 9, 7, 'B', '1', 20.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (12, 1, 1, 9, 6, 'C', '1', 20.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (13, 1, 1, 9, 5, 'B', '1', 20.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (16, 2, 2, 10, 8, 'B', '1', 20.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (17, 2, 2, 10, 14, 'C', '0', 0.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (18, 2, 2, 10, 7, '', '0', 0.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (19, 2, 2, 10, 6, '', '0', 0.00, NULL);
INSERT INTO `sc_exam_answer` VALUES (20, 2, 2, 10, 5, '', '0', 0.00, NULL);

-- ----------------------------
-- Table structure for sc_exam_answer_draft
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_answer_draft`;
CREATE TABLE `sc_exam_answer_draft`  (
  `draft_id` bigint NOT NULL AUTO_INCREMENT COMMENT '草稿ID',
  `record_id` bigint NOT NULL COMMENT '考试记录ID',
  `session_id` bigint NULL DEFAULT NULL COMMENT '考试会话ID',
  `session_paper_id` bigint NULL DEFAULT NULL COMMENT '子试卷记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_id` bigint NOT NULL COMMENT '学生用户ID',
  `user_answer` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '草稿答案',
  `save_count` int NULL DEFAULT 1 COMMENT '保存次数',
  `last_save_time` datetime NULL DEFAULT NULL COMMENT '最近保存时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0有效 1失效)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`draft_id`) USING BTREE,
  UNIQUE INDEX `uk_exam_answer_draft_unique`(`record_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_exam_answer_draft_session`(`session_id` ASC) USING BTREE,
  INDEX `idx_exam_answer_draft_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-考试答案草稿表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_exam_answer_draft
-- ----------------------------

-- ----------------------------
-- Table structure for sc_exam_behavior_log
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_behavior_log`;
CREATE TABLE `sc_exam_behavior_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `session_id` bigint NOT NULL COMMENT '考试会话ID',
  `record_id` bigint NULL DEFAULT NULL COMMENT '旧考试记录ID',
  `behavior_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '行为类型(FOCUS_LOSS/FLAGGED/MANUAL_SUBMIT/AUTO_SAVE)',
  `behavior_count` int NULL DEFAULT 1 COMMENT '次数',
  `behavior_time` datetime NULL DEFAULT NULL COMMENT '行为时间',
  `behavior_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '行为明细JSON',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_behavior_session`(`session_id` ASC) USING BTREE,
  INDEX `idx_exam_behavior_type`(`behavior_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 61 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-考试行为日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_exam_behavior_log
-- ----------------------------
INSERT INTO `sc_exam_behavior_log` VALUES (1, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:35:16', '{\"questionId\":8}', NULL, '2026-03-23 15:35:15', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (2, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:36:18', '{\"questionId\":8}', NULL, '2026-03-23 15:36:17', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (3, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:42:51', '{\"questionId\":8}', NULL, '2026-03-23 15:42:50', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (4, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:42:56', '{\"questionId\":8}', NULL, '2026-03-23 15:42:56', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (5, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:44:15', '{\"questionId\":8}', NULL, '2026-03-23 15:44:15', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (6, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:44:48', '{\"questionId\":8}', NULL, '2026-03-23 15:44:48', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (7, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:45:04', '{\"questionId\":8}', NULL, '2026-03-23 15:45:04', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (8, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:45:10', '{\"questionId\":8}', NULL, '2026-03-23 15:45:09', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (9, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:45:58', '{\"questionId\":8}', NULL, '2026-03-23 15:45:58', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (10, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:47:02', '{\"questionId\":8}', NULL, '2026-03-23 15:47:02', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (11, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:47:52', '{\"questionId\":8}', NULL, '2026-03-23 15:47:51', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (12, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:51:16', '{\"questionId\":8}', NULL, '2026-03-23 15:51:16', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (13, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:55:26', '{\"questionId\":8}', NULL, '2026-03-23 15:55:25', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (14, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:57:28', '{\"questionId\":8}', NULL, '2026-03-23 15:57:28', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (15, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 15:58:10', '{\"questionId\":8}', NULL, '2026-03-23 15:58:10', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (16, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:00:22', '{\"questionId\":8}', NULL, '2026-03-23 16:00:22', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (17, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:00:31', '{\"questionId\":8}', NULL, '2026-03-23 16:00:31', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (18, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:08:07', '{\"questionId\":8}', NULL, '2026-03-23 16:08:07', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (19, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:09:17', '{\"questionId\":8}', NULL, '2026-03-23 16:09:17', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (20, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:25:24', '{\"questionId\":8}', NULL, '2026-03-23 16:25:24', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (21, 1, 9, 'FOCUS_LOSS', 1, '2026-03-23 16:25:26', '{\"focusLossCount\":1,\"sourceEvent\":\"unknown\",\"occurredAt\":1774254326066}', NULL, '2026-03-23 16:25:26', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (22, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:25:30', '{\"questionId\":8}', NULL, '2026-03-23 16:25:29', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (23, 1, 9, 'FOCUS_LOSS', 1, '2026-03-23 16:25:41', '{\"focusLossCount\":2,\"sourceEvent\":\"unknown\",\"occurredAt\":1774254340630}', NULL, '2026-03-23 16:25:40', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (24, 1, 9, 'FOCUS_LOSS', 1, '2026-03-23 16:26:19', '{\"focusLossCount\":3,\"sourceEvent\":\"unknown\",\"occurredAt\":1774254378690}', NULL, '2026-03-23 16:26:18', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (25, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:26:35', '{\"questionId\":8}', NULL, '2026-03-23 16:26:34', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (26, 1, 9, 'FOCUS_LOSS', 1, '2026-03-23 16:26:49', '{\"focusLossCount\":4,\"sourceEvent\":\"unknown\",\"occurredAt\":1774254409017}', NULL, '2026-03-23 16:26:49', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (27, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:27:28', '{\"questionId\":8}', NULL, '2026-03-23 16:27:28', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (28, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:28:15', '{\"questionId\":8}', NULL, '2026-03-23 16:28:15', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (29, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:31:48', '{\"questionId\":8}', NULL, '2026-03-23 16:31:48', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (30, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:32:14', '{\"questionId\":7}', NULL, '2026-03-23 16:32:14', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (31, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:32:16', '{\"questionId\":14}', NULL, '2026-03-23 16:32:15', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (32, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:32:19', '{\"questionId\":14}', NULL, '2026-03-23 16:32:18', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (33, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:32:20', '{\"questionId\":6}', NULL, '2026-03-23 16:32:19', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (34, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:32:23', '{\"questionId\":5}', NULL, '2026-03-23 16:32:22', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (35, 1, 9, 'AUTO_SAVE', 1, '2026-03-23 16:32:26', '{\"questionId\":5}', NULL, '2026-03-23 16:32:26', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (36, 1, 9, 'FOCUS_LOSS', 1, '2026-03-23 16:32:43', '{\"focusLossCount\":5,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774254763446}', NULL, '2026-03-23 16:32:43', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (37, 1, 9, 'MANUAL_SUBMIT', 1, '2026-03-23 16:32:56', '{\"total\":5,\"correct\":5,\"score\":100.00,\"focusLossCount\":5,\"flaggedCount\":0,\"warningLevel\":\"HIGH\"}', NULL, '2026-03-23 16:32:56', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (38, 2, 10, 'AUTO_SAVE', 1, '2026-03-23 23:02:47', '{\"questionId\":8}', NULL, '2026-03-23 23:02:46', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (39, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:02:50', '{\"focusLossCount\":1,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774278169974}', NULL, '2026-03-23 23:02:49', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (40, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:02:52', '{\"focusLossCount\":2,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774278171781}', NULL, '2026-03-23 23:02:51', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (41, 2, 10, 'AUTO_SAVE', 1, '2026-03-23 23:14:48', '{\"questionId\":8}', NULL, '2026-03-23 23:14:48', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (42, 2, 10, 'AUTO_SAVE', 1, '2026-03-23 23:16:45', '{\"questionId\":8}', NULL, '2026-03-23 23:16:44', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (43, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:16:50', '{\"focusLossCount\":3,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774279010239}', NULL, '2026-03-23 23:16:50', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (44, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:17:04', '{\"focusLossCount\":4,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774279024158}', NULL, '2026-03-23 23:17:04', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (45, 2, 10, 'AUTO_SAVE', 1, '2026-03-23 23:17:11', '{\"questionId\":8}', NULL, '2026-03-23 23:17:11', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (46, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:17:20', '{\"focusLossCount\":5,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774279039780}', NULL, '2026-03-23 23:17:19', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (47, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:17:37', '{\"focusLossCount\":6,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774279056997}', NULL, '2026-03-23 23:17:37', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (48, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:17:37', '{\"focusLossCount\":7,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774279056997}', NULL, '2026-03-23 23:17:37', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (49, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:17:39', '{\"focusLossCount\":8,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774279058689}', NULL, '2026-03-23 23:17:38', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (50, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:17:45', '{\"focusLossCount\":9,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774279064569}', NULL, '2026-03-23 23:17:44', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (51, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:17:45', '{\"focusLossCount\":10,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774279065255}', NULL, '2026-03-23 23:17:45', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (52, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:17:52', '{\"focusLossCount\":11,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774279071974}', NULL, '2026-03-23 23:17:51', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (53, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:17:55', '{\"focusLossCount\":12,\"sourceEvent\":\"visibility_hidden\",\"occurredAt\":1774279074922}', NULL, '2026-03-23 23:17:54', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (54, 2, 10, 'AUTO_SAVE', 1, '2026-03-23 23:19:02', '{\"questionId\":8}', NULL, '2026-03-23 23:19:02', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (55, 2, 10, 'QUESTION_SUBMIT', 1, '2026-03-23 23:19:18', '{\"questionId\":14}', NULL, '2026-03-23 23:19:17', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (56, 2, 10, 'QUESTION_SUBMIT', 1, '2026-03-23 23:19:21', '{\"questionId\":14}', NULL, '2026-03-23 23:19:20', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (57, 2, 10, 'AUTO_SAVE', 1, '2026-03-23 23:19:21', '{\"questionId\":14}', NULL, '2026-03-23 23:19:21', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (58, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:19:23', '{\"focusLossCount\":13,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774279163275}', NULL, '2026-03-23 23:19:23', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (59, 2, 10, 'FOCUS_LOSS', 1, '2026-03-23 23:19:26', '{\"focusLossCount\":14,\"sourceEvent\":\"window_blur\",\"occurredAt\":1774279166388}', NULL, '2026-03-23 23:19:26', NULL);
INSERT INTO `sc_exam_behavior_log` VALUES (60, 2, 10, 'MANUAL_SUBMIT', 1, '2026-03-23 23:19:30', '{\"total\":5,\"correct\":1,\"score\":20.00,\"focusLossCount\":14,\"flaggedCount\":0,\"warningLevel\":\"HIGH\"}', NULL, '2026-03-23 23:19:30', NULL);

-- ----------------------------
-- Table structure for sc_exam_paper
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_paper`;
CREATE TABLE `sc_exam_paper`  (
  `paper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `parent_paper_id` bigint NULL DEFAULT NULL COMMENT '父试卷ID，主试卷为空',
  `paper_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '试卷名称',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `paper_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'fixed' COMMENT '试卷类型(fixed/random/adaptive)',
  `paper_level` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'MAIN' COMMENT '试卷层级(MAIN/SUB)',
  `answer_mode` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'REQUIRED' COMMENT '作答模式(REQUIRED/OPTIONAL)',
  `paper_weight` decimal(8, 2) NULL DEFAULT 100.00 COMMENT '子试卷权重',
  `sort_no` int NULL DEFAULT 0 COMMENT '同层排序号',
  `total_score` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '总分',
  `duration_minutes` int NULL DEFAULT 60 COMMENT '考试时长(分钟)',
  `publish_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'draft' COMMENT '发布状态(draft/published/archived)',
  `publish_start_time` datetime NULL DEFAULT NULL COMMENT '发布时间起',
  `publish_end_time` datetime NULL DEFAULT NULL COMMENT '发布时间止',
  `strategy_template_id` bigint NULL DEFAULT NULL COMMENT '组卷策略模板ID',
  `pass_score` decimal(8, 2) NULL DEFAULT 60.00 COMMENT '及格线',
  `marking_mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'auto_first' COMMENT '阅卷方式',
  `question_order_mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'manual' COMMENT '题目顺序模式',
  `question_navigation_mode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'free' COMMENT '切题方式(free自由切题 lock_after_answer已答不可回退)',
  `allow_review_analysis` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否允许回看解析(0否 1是)',
  `anti_cheat_enabled` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否启用防作弊(0否 1是)',
  `max_focus_loss_count` int NULL DEFAULT 5 COMMENT '允许切屏次数',
  `auto_submit_on_focus_loss_limit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '切屏超限是否自动交卷(0否 1是)',
  `allow_copy_paste` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否允许复制粘贴(0否 1是)',
  `max_attempt_count` int NULL DEFAULT 0 COMMENT '最多可考次数(0表示不限制)',
  `allow_view_score` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '考后是否允许学生查看成绩(0否 1是)',
  `section_config_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '题型区块配置JSON',
  `adaptive_rule_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '自适应规则JSON',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`paper_id`) USING BTREE,
  INDEX `idx_sc_exam_paper_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_sc_exam_paper_publish_status`(`publish_status` ASC) USING BTREE,
  INDEX `idx_sc_exam_paper_parent`(`parent_paper_id` ASC) USING BTREE,
  INDEX `idx_sc_exam_paper_level`(`paper_level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-试卷表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_exam_paper
-- ----------------------------
INSERT INTO `sc_exam_paper` VALUES (2, NULL, '测试试卷', NULL, 'fixed', 'MAIN', 'REQUIRED', 100.00, 0, 100.00, 60, 'published', '2026-03-23 06:59:38', '2026-03-31 00:00:00', NULL, 60.00, 'auto_first', 'manual', 'free', '1', '1', 5, '0', '1', 0, '1', '[{\"title\":\"单选题\",\"questionType\":\"single\",\"defaultScore\":20,\"targetScore\":100,\"instructions\":\"每题 20 分，共 5 题\",\"questionIds\":[8,14,7,6,5]}]', NULL, '0', 'admin', '2026-03-23 14:59:29', 'admin', '2026-03-23 14:59:38', '');

-- ----------------------------
-- Table structure for sc_exam_paper_question
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_paper_question`;
CREATE TABLE `sc_exam_paper_question`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `paper_id` bigint NOT NULL COMMENT '试卷ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `score` decimal(6, 2) NULL DEFAULT 0.00 COMMENT '分值',
  `sort_no` int NULL DEFAULT 0 COMMENT '排序号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sc_exam_paper_question`(`paper_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_sc_exam_paper_question_question_id`(`question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-试卷题目关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_exam_paper_question
-- ----------------------------
INSERT INTO `sc_exam_paper_question` VALUES (22, 2, 8, 20.00, 1);
INSERT INTO `sc_exam_paper_question` VALUES (23, 2, 14, 20.00, 2);
INSERT INTO `sc_exam_paper_question` VALUES (24, 2, 7, 20.00, 3);
INSERT INTO `sc_exam_paper_question` VALUES (25, 2, 6, 20.00, 4);
INSERT INTO `sc_exam_paper_question` VALUES (26, 2, 5, 20.00, 5);

-- ----------------------------
-- Table structure for sc_exam_record
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_record`;
CREATE TABLE `sc_exam_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '考试记录ID',
  `session_id` bigint NULL DEFAULT NULL COMMENT '考试会话ID',
  `paper_id` bigint NOT NULL COMMENT '试卷ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `score` decimal(6, 2) NULL DEFAULT 0.00 COMMENT '得分',
  `correct_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '正确率',
  `exam_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'INIT' COMMENT '考试状态(INIT/ONGOING/SUBMITTED)',
  `cheat_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '作弊标记(0否 1是)',
  `analysis_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '考试分析JSON',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `idx_sc_exam_record_user_paper`(`user_id` ASC, `paper_id` ASC) USING BTREE,
  INDEX `idx_sc_exam_record_submit_time`(`submit_time` ASC) USING BTREE,
  INDEX `idx_sc_exam_record_session`(`session_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-考试记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_exam_record
-- ----------------------------
INSERT INTO `sc_exam_record` VALUES (9, 1, 2, 102, '2026-03-23 15:35:14', '2026-03-23 16:32:56', 100.00, 100.00, 'SUBMITTED', '0', '{\"total\":5,\"correct\":5,\"score\":100.00,\"focusLossCount\":5,\"flaggedCount\":0,\"warningLevel\":\"HIGH\"}');
INSERT INTO `sc_exam_record` VALUES (10, 2, 2, 102, '2026-03-23 23:02:45', '2026-03-23 23:19:30', 20.00, 20.00, 'SUBMITTED', '0', '{\"total\":5,\"correct\":1,\"score\":20.00,\"focusLossCount\":14,\"flaggedCount\":0,\"warningLevel\":\"HIGH\"}');

-- ----------------------------
-- Table structure for sc_exam_session
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_session`;
CREATE TABLE `sc_exam_session`  (
  `session_id` bigint NOT NULL AUTO_INCREMENT COMMENT '考试会话ID',
  `root_paper_id` bigint NOT NULL COMMENT '主试卷ID',
  `user_id` bigint NOT NULL COMMENT '学生用户ID',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `session_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ONGOING' COMMENT '会话状态(ONGOING/SUBMITTED/EXPIRED)',
  `total_score` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '总得分',
  `correct_rate` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '总正确率',
  `focus_loss_count` int NULL DEFAULT 0 COMMENT '切屏次数',
  `flagged_count` int NULL DEFAULT 0 COMMENT '标记异常次数',
  `analysis_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '统计分析JSON',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`session_id`) USING BTREE,
  INDEX `idx_exam_session_root_user`(`root_paper_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_exam_session_status`(`session_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-考试会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_exam_session
-- ----------------------------
INSERT INTO `sc_exam_session` VALUES (1, 2, 102, '2026-03-23 15:35:14', '2026-03-23 16:32:56', 'SUBMITTED', 100.00, 100.00, 5, 0, '{\"total\":5,\"correct\":5,\"score\":100.00,\"focusLossCount\":5,\"flaggedCount\":0,\"warningLevel\":\"HIGH\"}', NULL, '2026-03-23 15:35:13', 'zhangsan', '2026-03-23 16:32:56', NULL);
INSERT INTO `sc_exam_session` VALUES (2, 2, 102, '2026-03-23 23:02:45', '2026-03-23 23:19:30', 'SUBMITTED', 20.00, 20.00, 14, 0, '{\"total\":5,\"correct\":1,\"score\":20.00,\"focusLossCount\":14,\"flaggedCount\":0,\"warningLevel\":\"HIGH\"}', NULL, '2026-03-23 23:02:45', 'zhangsan', '2026-03-23 23:19:30', NULL);

-- ----------------------------
-- Table structure for sc_exam_session_paper
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_session_paper`;
CREATE TABLE `sc_exam_session_paper`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `session_id` bigint NOT NULL COMMENT '考试会话ID',
  `paper_id` bigint NOT NULL COMMENT '试卷ID',
  `parent_paper_id` bigint NULL DEFAULT NULL COMMENT '父试卷ID',
  `paper_level` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'MAIN' COMMENT '试卷层级(MAIN/SUB)',
  `answer_mode` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'REQUIRED' COMMENT '作答模式(REQUIRED/OPTIONAL)',
  `paper_weight` decimal(8, 2) NULL DEFAULT 100.00 COMMENT '子试卷权重',
  `sort_no` int NULL DEFAULT 0 COMMENT '排序号',
  `paper_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '子试卷状态(PENDING/ONGOING/SUBMITTED/SKIPPED)',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `score` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '得分',
  `correct_rate` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '正确率',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_exam_session_paper`(`session_id` ASC, `paper_id` ASC) USING BTREE,
  INDEX `idx_exam_session_paper_session`(`session_id` ASC) USING BTREE,
  INDEX `idx_exam_session_paper_parent`(`parent_paper_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-考试会话子试卷表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_exam_session_paper
-- ----------------------------
INSERT INTO `sc_exam_session_paper` VALUES (1, 1, 2, NULL, 'MAIN', 'REQUIRED', 100.00, 0, 'SUBMITTED', '2026-03-23 15:35:14', '2026-03-23 16:32:56', 100.00, 100.00, NULL, '2026-03-23 15:35:13', '', '2026-03-23 16:32:56', NULL);
INSERT INTO `sc_exam_session_paper` VALUES (2, 2, 2, NULL, 'MAIN', 'REQUIRED', 100.00, 0, 'SUBMITTED', '2026-03-23 23:02:45', '2026-03-23 23:19:30', 20.00, 20.00, NULL, '2026-03-23 23:02:45', 'zhangsan', '2026-03-23 23:19:30', NULL);

-- ----------------------------
-- Table structure for sc_exam_strategy_template
-- ----------------------------
DROP TABLE IF EXISTS `sc_exam_strategy_template`;
CREATE TABLE `sc_exam_strategy_template`  (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `template_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'paper_rule' COMMENT '模板类型',
  `rule_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '组卷规则JSON',
  `publish_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'draft' COMMENT '状态(draft/published/archived)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '系统状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`template_id`) USING BTREE,
  INDEX `idx_exam_strategy_template_course`(`course_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-组卷策略模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_exam_strategy_template
-- ----------------------------

-- ----------------------------
-- Table structure for sc_grade
-- ----------------------------
DROP TABLE IF EXISTS `sc_grade`;
CREATE TABLE `sc_grade`  (
  `grade_id` bigint NOT NULL AUTO_INCREMENT COMMENT '年级ID',
  `grade_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '年级名称',
  `school_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学年',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`grade_id`) USING BTREE,
  INDEX `idx_sc_grade_name`(`grade_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-年级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_grade
-- ----------------------------
INSERT INTO `sc_grade` VALUES (2, '2024级', '2025-2026', '0', 'admin', '2026-03-17 16:39:09', '', NULL, '');

-- ----------------------------
-- Table structure for sc_knowledge_point
-- ----------------------------
DROP TABLE IF EXISTS `sc_knowledge_point`;
CREATE TABLE `sc_knowledge_point`  (
  `knowledge_point_id` bigint NOT NULL AUTO_INCREMENT COMMENT '知识点ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父知识点ID',
  `knowledge_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '知识点名称',
  `difficulty_level` int NULL DEFAULT 1 COMMENT '难度等级(1-5)',
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关键词',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`knowledge_point_id`) USING BTREE,
  INDEX `idx_sc_knowledge_course_parent`(`course_id` ASC, `parent_id` ASC) USING BTREE,
  INDEX `idx_sc_knowledge_name`(`knowledge_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-知识点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_knowledge_point
-- ----------------------------

-- ----------------------------
-- Table structure for sc_learning_plan
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_plan`;
CREATE TABLE `sc_learning_plan`  (
  `plan_id` bigint NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '计划名称',
  `goal_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标描述',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `plan_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '计划状态(0进行中 1完成 2终止)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`plan_id`) USING BTREE,
  INDEX `idx_sc_learning_plan_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-学习计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_learning_plan
-- ----------------------------

-- ----------------------------
-- Table structure for sc_learning_plan_item
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_plan_item`;
CREATE TABLE `sc_learning_plan_item`  (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `plan_id` bigint NOT NULL COMMENT '计划ID',
  `biz_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型',
  `biz_id` bigint NOT NULL COMMENT '业务ID',
  `target_duration` int NULL DEFAULT 0 COMMENT '目标时长(分钟)',
  `target_count` int NULL DEFAULT 0 COMMENT '目标次数',
  `complete_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '完成状态(0未完成 1已完成)',
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `idx_sc_learning_plan_item_plan_id`(`plan_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-学习计划明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_learning_plan_item
-- ----------------------------

-- ----------------------------
-- Table structure for sc_learning_profile
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_profile`;
CREATE TABLE `sc_learning_profile`  (
  `profile_id` bigint NOT NULL AUTO_INCREMENT COMMENT '画像ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `ability_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '能力等级',
  `concentration_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '专注度得分',
  `active_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '活跃度得分',
  `mastery_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '掌握度得分',
  `interest_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '兴趣度得分',
  `risk_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '风险得分',
  `profile_version` int NULL DEFAULT 1 COMMENT '画像版本',
  `feature_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '特征快照JSON',
  `last_calculated_time` datetime NULL DEFAULT NULL COMMENT '最后计算时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`profile_id`) USING BTREE,
  UNIQUE INDEX `uk_sc_learning_profile_user_course`(`user_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_sc_learning_profile_last_time`(`last_calculated_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-学习画像主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_learning_profile
-- ----------------------------
INSERT INTO `sc_learning_profile` VALUES (5, 102, 2, 'LOW', 0.13, 22.00, 0.00, 16.00, 100.00, 11, '{\"behaviorCount\":2,\"durationSeconds\":2,\"resourceCount\":1,\"studyDayCount\":1,\"progressScore\":0.00,\"avgExamScore\":0.00,\"avgCorrectRate\":0.00,\"wrongQuestionCount\":3,\"completedBehaviorCount\":0,\"behaviorTypes\":\"{TASK_OPEN=2}\",\"deviceTypes\":\"{web=2}\"}', '2026-03-24 12:30:37', '2026-03-20 10:26:28', '2026-03-24 12:30:36');

-- ----------------------------
-- Table structure for sc_learning_profile_tag
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_profile_tag`;
CREATE TABLE `sc_learning_profile_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `profile_id` bigint NOT NULL COMMENT '画像ID',
  `tag_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签编码',
  `tag_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  `tag_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签值',
  `tag_weight` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '标签权重',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sc_learning_profile_tag_profile_id`(`profile_id` ASC) USING BTREE,
  INDEX `idx_sc_learning_profile_tag_code`(`tag_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-学习画像标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_learning_profile_tag
-- ----------------------------

-- ----------------------------
-- Table structure for sc_learning_recommendation
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_recommendation`;
CREATE TABLE `sc_learning_recommendation`  (
  `recommend_id` bigint NOT NULL AUTO_INCREMENT COMMENT '推荐ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `biz_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型(course/resource/question/task/post)',
  `biz_id` bigint NOT NULL COMMENT '业务ID',
  `recommend_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推荐理由',
  `recommend_score` decimal(8, 4) NULL DEFAULT 0.0000 COMMENT '推荐分',
  `source_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源类型(rule/cf/weakness/ai)',
  `scene_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推荐场景(home/exam_before/daily_plan)',
  `expose_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '曝光状态(0未曝光 1已曝光)',
  `click_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '点击状态(0未点击 1已点击)',
  `feedback_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '反馈状态(0无反馈 1喜欢 2不喜欢)',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`recommend_id`) USING BTREE,
  INDEX `idx_sc_learning_recommend_user_scene`(`user_id` ASC, `scene_code` ASC) USING BTREE,
  INDEX `idx_sc_learning_recommend_user_biz`(`user_id` ASC, `biz_type` ASC, `biz_id` ASC) USING BTREE,
  INDEX `idx_sc_learning_recommend_expire_time`(`expire_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-个性化推荐表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_learning_recommendation
-- ----------------------------

-- ----------------------------
-- Table structure for sc_learning_report
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_report`;
CREATE TABLE `sc_learning_report`  (
  `report_id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `report_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'weekly' COMMENT '报告类型(day/week/month/exam)',
  `report_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '报告内容',
  `report_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '结构化报告JSON',
  `generate_time` datetime NULL DEFAULT NULL COMMENT '生成时间',
  PRIMARY KEY (`report_id`) USING BTREE,
  INDEX `idx_sc_learning_report_user_type`(`user_id` ASC, `report_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 149 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-学情报告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_learning_report
-- ----------------------------
INSERT INTO `sc_learning_report` VALUES (1, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-12 10:14:50');
INSERT INTO `sc_learning_report` VALUES (2, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-12 10:14:52');
INSERT INTO `sc_learning_report` VALUES (3, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-12 10:15:09');
INSERT INTO `sc_learning_report` VALUES (4, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-12 12:12:03');
INSERT INTO `sc_learning_report` VALUES (5, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-12 12:12:14');
INSERT INTO `sc_learning_report` VALUES (6, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-12 12:12:20');
INSERT INTO `sc_learning_report` VALUES (7, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-12 13:15:08');
INSERT INTO `sc_learning_report` VALUES (8, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-12 13:15:13');
INSERT INTO `sc_learning_report` VALUES (9, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-12 16:06:59');
INSERT INTO `sc_learning_report` VALUES (10, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-13 16:22:54');
INSERT INTO `sc_learning_report` VALUES (11, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-14 19:46:16');
INSERT INTO `sc_learning_report` VALUES (12, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-14 20:16:25');
INSERT INTO `sc_learning_report` VALUES (13, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-14 20:31:21');
INSERT INTO `sc_learning_report` VALUES (14, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-14 20:50:12');
INSERT INTO `sc_learning_report` VALUES (15, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 12:58:48');
INSERT INTO `sc_learning_report` VALUES (16, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 12:59:04');
INSERT INTO `sc_learning_report` VALUES (17, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 13:24:56');
INSERT INTO `sc_learning_report` VALUES (18, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 13:25:08');
INSERT INTO `sc_learning_report` VALUES (19, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 13:39:18');
INSERT INTO `sc_learning_report` VALUES (20, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 13:39:36');
INSERT INTO `sc_learning_report` VALUES (21, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 13:45:29');
INSERT INTO `sc_learning_report` VALUES (22, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 13:47:48');
INSERT INTO `sc_learning_report` VALUES (23, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 13:48:10');
INSERT INTO `sc_learning_report` VALUES (24, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 13:48:50');
INSERT INTO `sc_learning_report` VALUES (25, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 13:57:02');
INSERT INTO `sc_learning_report` VALUES (26, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 13:57:33');
INSERT INTO `sc_learning_report` VALUES (27, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 13:58:56');
INSERT INTO `sc_learning_report` VALUES (28, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 14:04:04');
INSERT INTO `sc_learning_report` VALUES (29, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 14:04:42');
INSERT INTO `sc_learning_report` VALUES (30, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 14:10:57');
INSERT INTO `sc_learning_report` VALUES (31, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 14:15:03');
INSERT INTO `sc_learning_report` VALUES (32, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-15 19:48:15');
INSERT INTO `sc_learning_report` VALUES (33, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 00:31:58');
INSERT INTO `sc_learning_report` VALUES (34, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 16:28:07');
INSERT INTO `sc_learning_report` VALUES (35, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 16:28:15');
INSERT INTO `sc_learning_report` VALUES (36, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 16:28:17');
INSERT INTO `sc_learning_report` VALUES (37, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 16:41:43');
INSERT INTO `sc_learning_report` VALUES (38, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 16:44:21');
INSERT INTO `sc_learning_report` VALUES (39, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 16:46:59');
INSERT INTO `sc_learning_report` VALUES (40, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 16:49:40');
INSERT INTO `sc_learning_report` VALUES (41, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 16:51:55');
INSERT INTO `sc_learning_report` VALUES (42, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 16:51:57');
INSERT INTO `sc_learning_report` VALUES (43, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 17:41:34');
INSERT INTO `sc_learning_report` VALUES (44, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 18:44:11');
INSERT INTO `sc_learning_report` VALUES (45, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-16 18:44:11');
INSERT INTO `sc_learning_report` VALUES (46, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 09:05:37');
INSERT INTO `sc_learning_report` VALUES (47, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 09:13:02');
INSERT INTO `sc_learning_report` VALUES (48, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 09:28:27');
INSERT INTO `sc_learning_report` VALUES (49, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 10:13:45');
INSERT INTO `sc_learning_report` VALUES (50, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 13:22:19');
INSERT INTO `sc_learning_report` VALUES (51, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 14:26:44');
INSERT INTO `sc_learning_report` VALUES (52, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 14:26:45');
INSERT INTO `sc_learning_report` VALUES (53, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 14:27:02');
INSERT INTO `sc_learning_report` VALUES (54, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 14:34:44');
INSERT INTO `sc_learning_report` VALUES (55, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 14:35:01');
INSERT INTO `sc_learning_report` VALUES (56, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 15:59:51');
INSERT INTO `sc_learning_report` VALUES (57, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 16:13:23');
INSERT INTO `sc_learning_report` VALUES (58, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 22:19:37');
INSERT INTO `sc_learning_report` VALUES (59, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 22:21:43');
INSERT INTO `sc_learning_report` VALUES (60, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 23:10:27');
INSERT INTO `sc_learning_report` VALUES (61, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 23:10:30');
INSERT INTO `sc_learning_report` VALUES (62, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 23:10:56');
INSERT INTO `sc_learning_report` VALUES (63, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 23:12:34');
INSERT INTO `sc_learning_report` VALUES (64, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-17 23:40:33');
INSERT INTO `sc_learning_report` VALUES (65, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-18 00:11:52');
INSERT INTO `sc_learning_report` VALUES (66, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-18 00:27:50');
INSERT INTO `sc_learning_report` VALUES (67, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-18 12:05:46');
INSERT INTO `sc_learning_report` VALUES (68, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-18 17:33:15');
INSERT INTO `sc_learning_report` VALUES (69, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 12:55:45');
INSERT INTO `sc_learning_report` VALUES (70, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 12:55:52');
INSERT INTO `sc_learning_report` VALUES (71, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 13:16:20');
INSERT INTO `sc_learning_report` VALUES (72, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 13:24:14');
INSERT INTO `sc_learning_report` VALUES (73, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 13:33:26');
INSERT INTO `sc_learning_report` VALUES (74, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 13:38:30');
INSERT INTO `sc_learning_report` VALUES (75, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 13:39:22');
INSERT INTO `sc_learning_report` VALUES (76, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 13:39:29');
INSERT INTO `sc_learning_report` VALUES (77, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 13:41:36');
INSERT INTO `sc_learning_report` VALUES (78, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 18:29:52');
INSERT INTO `sc_learning_report` VALUES (79, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 18:35:40');
INSERT INTO `sc_learning_report` VALUES (80, 1, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 23:03:15');
INSERT INTO `sc_learning_report` VALUES (81, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-19 23:03:28');
INSERT INTO `sc_learning_report` VALUES (82, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 00:09:37');
INSERT INTO `sc_learning_report` VALUES (83, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 10:39:02');
INSERT INTO `sc_learning_report` VALUES (84, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 10:39:43');
INSERT INTO `sc_learning_report` VALUES (85, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 10:40:24');
INSERT INTO `sc_learning_report` VALUES (86, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 10:46:03');
INSERT INTO `sc_learning_report` VALUES (87, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 10:51:03');
INSERT INTO `sc_learning_report` VALUES (88, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 12:46:15');
INSERT INTO `sc_learning_report` VALUES (89, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 12:47:43');
INSERT INTO `sc_learning_report` VALUES (90, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 15:09:12');
INSERT INTO `sc_learning_report` VALUES (91, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 15:09:13');
INSERT INTO `sc_learning_report` VALUES (92, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 15:09:35');
INSERT INTO `sc_learning_report` VALUES (93, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 15:32:10');
INSERT INTO `sc_learning_report` VALUES (94, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 16:57:52');
INSERT INTO `sc_learning_report` VALUES (95, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 16:58:22');
INSERT INTO `sc_learning_report` VALUES (96, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度0.00，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":0.00,\"riskScore\":100.00}', '2026-03-20 17:09:15');
INSERT INTO `sc_learning_report` VALUES (97, 102, 'exam_after_submit', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 17:12:11');
INSERT INTO `sc_learning_report` VALUES (98, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 17:12:16');
INSERT INTO `sc_learning_report` VALUES (99, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:08:31');
INSERT INTO `sc_learning_report` VALUES (100, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:13:50');
INSERT INTO `sc_learning_report` VALUES (101, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:16:08');
INSERT INTO `sc_learning_report` VALUES (102, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:16:09');
INSERT INTO `sc_learning_report` VALUES (103, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:16:29');
INSERT INTO `sc_learning_report` VALUES (104, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:16:29');
INSERT INTO `sc_learning_report` VALUES (105, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:33:00');
INSERT INTO `sc_learning_report` VALUES (106, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:33:05');
INSERT INTO `sc_learning_report` VALUES (107, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:36:53');
INSERT INTO `sc_learning_report` VALUES (108, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:36:53');
INSERT INTO `sc_learning_report` VALUES (109, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:42:25');
INSERT INTO `sc_learning_report` VALUES (110, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:49:00');
INSERT INTO `sc_learning_report` VALUES (111, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:49:02');
INSERT INTO `sc_learning_report` VALUES (112, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:49:41');
INSERT INTO `sc_learning_report` VALUES (113, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度26.25，平均进度0，风险分91.50。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":26.25,\"riskScore\":91.50}', '2026-03-20 19:49:44');
INSERT INTO `sc_learning_report` VALUES (114, 102, 'exam_after_submit', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-20 19:55:29');
INSERT INTO `sc_learning_report` VALUES (115, 1, 'diagnosis', '当前暂无画像数据，建议先进行学习行为采集与画像计算。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":false,\"abilityLevel\":\"\",\"activeScore\":null,\"masteryScore\":null,\"riskScore\":null}', '2026-03-20 20:09:59');
INSERT INTO `sc_learning_report` VALUES (116, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-20 20:10:03');
INSERT INTO `sc_learning_report` VALUES (117, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-20 20:10:26');
INSERT INTO `sc_learning_report` VALUES (118, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-20 20:10:37');
INSERT INTO `sc_learning_report` VALUES (119, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-20 20:12:38');
INSERT INTO `sc_learning_report` VALUES (120, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 12:33:19');
INSERT INTO `sc_learning_report` VALUES (121, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 12:37:35');
INSERT INTO `sc_learning_report` VALUES (122, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 15:30:22');
INSERT INTO `sc_learning_report` VALUES (123, 1, 'diagnosis', '当前暂无画像数据，建议先进行学习行为采集与画像计算。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":false,\"abilityLevel\":\"\",\"activeScore\":null,\"masteryScore\":null,\"riskScore\":null}', '2026-03-21 15:37:13');
INSERT INTO `sc_learning_report` VALUES (124, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 20:52:59');
INSERT INTO `sc_learning_report` VALUES (125, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 20:53:00');
INSERT INTO `sc_learning_report` VALUES (126, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 20:54:12');
INSERT INTO `sc_learning_report` VALUES (127, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 20:55:09');
INSERT INTO `sc_learning_report` VALUES (128, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 20:58:57');
INSERT INTO `sc_learning_report` VALUES (129, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 20:59:24');
INSERT INTO `sc_learning_report` VALUES (130, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 20:59:36');
INSERT INTO `sc_learning_report` VALUES (131, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 21:06:51');
INSERT INTO `sc_learning_report` VALUES (132, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 21:07:07');
INSERT INTO `sc_learning_report` VALUES (133, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 21:07:37');
INSERT INTO `sc_learning_report` VALUES (134, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 21:07:47');
INSERT INTO `sc_learning_report` VALUES (135, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 21:08:03');
INSERT INTO `sc_learning_report` VALUES (136, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 21:10:42');
INSERT INTO `sc_learning_report` VALUES (137, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 21:10:47');
INSERT INTO `sc_learning_report` VALUES (138, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 21:10:50');
INSERT INTO `sc_learning_report` VALUES (139, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 21:10:52');
INSERT INTO `sc_learning_report` VALUES (140, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 21:45:17');
INSERT INTO `sc_learning_report` VALUES (141, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 22:01:09');
INSERT INTO `sc_learning_report` VALUES (142, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 22:01:19');
INSERT INTO `sc_learning_report` VALUES (143, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 22:01:20');
INSERT INTO `sc_learning_report` VALUES (144, 102, 'diagnosis', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-21 22:03:05');
INSERT INTO `sc_learning_report` VALUES (145, 102, 'weekly', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-23 15:59:34');
INSERT INTO `sc_learning_report` VALUES (146, 102, 'weekly', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-23 15:59:39');
INSERT INTO `sc_learning_report` VALUES (147, 102, 'exam_after_submit', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-23 16:32:56');
INSERT INTO `sc_learning_report` VALUES (148, 102, 'exam_after_submit', '本阶段共记录学习行为0次，累计学习时长0秒，当前能力等级为LOW，活跃度0.00，掌握度13.13，平均进度0，风险分100.00。', '{\"behaviorCount\":0,\"totalDurationSeconds\":0,\"avgProgress\":0.00,\"hasProfile\":true,\"abilityLevel\":\"LOW\",\"activeScore\":0.00,\"masteryScore\":13.13,\"riskScore\":100.00}', '2026-03-23 23:19:30');

-- ----------------------------
-- Table structure for sc_learning_task
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_task`;
CREATE TABLE `sc_learning_task`  (
  `task_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `task_title` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务标题',
  `task_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务类型(HOMEWORK/EXAM/PRACTICE/READING/COURSE)',
  `biz_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型',
  `biz_id` bigint NULL DEFAULT NULL COMMENT '业务ID',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `class_course_id` bigint NULL DEFAULT NULL COMMENT '班级课程ID',
  `term_id` bigint NULL DEFAULT NULL COMMENT '学期ID',
  `task_desc` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务描述',
  `priority_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NORMAL' COMMENT '优先级',
  `action_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '动作类型',
  `action_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '动作路径',
  `action_target_id` bigint NULL DEFAULT NULL COMMENT '动作目标ID',
  `publish_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `due_time` datetime NULL DEFAULT NULL COMMENT '截止时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `target_scope` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'USER' COMMENT '派发范围',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习任务主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_learning_task
-- ----------------------------
INSERT INTO `sc_learning_task` VALUES (1, '课后作业：Python数据分析', 'HOMEWORK', 'HOMEWORK', 1, 2, 1, 1, '自主学习。', 'NORMAL', 'course', '/student/courses', 1, '2026-03-22 17:23:22', '2026-03-22 00:00:00', '2026-03-30 00:00:00', '0', NULL, NULL, 'admin', '2026-03-22 17:23:21', '', NULL);

-- ----------------------------
-- Table structure for sc_learning_task_dispatch
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_task_dispatch`;
CREATE TABLE `sc_learning_task_dispatch`  (
  `dispatch_id` bigint NOT NULL AUTO_INCREMENT COMMENT '派发ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `dispatch_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '派发状态(0有效 1取消)',
  `completion_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '完成状态',
  `completed_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `completion_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '完成说明',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dispatch_id`) USING BTREE,
  UNIQUE INDEX `uk_task_user`(`task_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_dispatch_user`(`user_id` ASC, `dispatch_status` ASC, `completion_status` ASC) USING BTREE,
  CONSTRAINT `fk_learning_task_dispatch_task` FOREIGN KEY (`task_id`) REFERENCES `sc_learning_task` (`task_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习任务派发表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_learning_task_dispatch
-- ----------------------------
INSERT INTO `sc_learning_task_dispatch` VALUES (1, 1, 102, '0', 'IN_PROGRESS', '2026-03-22 17:26:29', '2026-03-22 17:26:13', '学生在首页标记完成', 'admin', '2026-03-22 17:23:32', 'zhangsan', '2026-03-24 12:30:36');

-- ----------------------------
-- Table structure for sc_learning_task_submission
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_task_submission`;
CREATE TABLE `sc_learning_task_submission`  (
  `submission_id` bigint NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `dispatch_id` bigint NOT NULL COMMENT '派发ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `submit_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '提交内容',
  `attachment_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '附件地址(JSON数组字符串)',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `review_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '批阅状态',
  `score` decimal(6, 2) NULL DEFAULT NULL COMMENT '得分',
  `teacher_feedback` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教师反馈',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`submission_id`) USING BTREE,
  UNIQUE INDEX `uk_dispatch_submission`(`dispatch_id` ASC) USING BTREE,
  INDEX `idx_submission_task_user`(`task_id` ASC, `user_id` ASC) USING BTREE,
  CONSTRAINT `fk_learning_task_submission_dispatch` FOREIGN KEY (`dispatch_id`) REFERENCES `sc_learning_task_dispatch` (`dispatch_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习任务提交回执表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_learning_task_submission
-- ----------------------------

-- ----------------------------
-- Table structure for sc_learning_warning
-- ----------------------------
DROP TABLE IF EXISTS `sc_learning_warning`;
CREATE TABLE `sc_learning_warning`  (
  `warning_id` bigint NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `warning_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预警类型(low_active/score_down/high_risk)',
  `warning_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'LOW' COMMENT '预警等级',
  `warning_content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预警内容',
  `suggestion` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '干预建议',
  `process_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '处理状态',
  `source_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'rule' COMMENT '来源类型(rule/ai)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `process_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`warning_id`) USING BTREE,
  INDEX `idx_sc_learning_warning_user_type`(`user_id` ASC, `warning_type` ASC) USING BTREE,
  INDEX `idx_sc_learning_warning_level_status`(`warning_level` ASC, `process_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-学情预警表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_learning_warning
-- ----------------------------
INSERT INTO `sc_learning_warning` VALUES (1, 1, NULL, 'high_risk', 'HIGH', '近期学习活跃度与掌握度偏低，存在较高学习风险', '建议教师或家长及时干预，补充重点资源并安排阶段性测验', 'PENDING', 'rule', '2026-03-12 10:14:50', NULL);
INSERT INTO `sc_learning_warning` VALUES (2, 102, NULL, 'high_risk', 'HIGH', '近期学习活跃度与掌握度偏低，存在较高学习风险', '建议教师或家长及时干预，补充重点资源并安排阶段性测验', 'PENDING', 'rule', '2026-03-17 15:59:51', NULL);
INSERT INTO `sc_learning_warning` VALUES (3, 102, 2, 'high_risk', 'HIGH', '近期学习活跃度与掌握度偏低，存在较高学习风险', '建议教师或家长及时干预，补充重点资源并安排阶段性测验', 'PENDING', 'rule', '2026-03-20 10:39:02', NULL);

-- ----------------------------
-- Table structure for sc_login_risk_event
-- ----------------------------
DROP TABLE IF EXISTS `sc_login_risk_event`;
CREATE TABLE `sc_login_risk_event`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '事件ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `login_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录IP',
  `login_location` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录地点',
  `device_fingerprint` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备指纹',
  `device_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类型',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作系统',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  `risk_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '风险类型',
  `risk_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '风险得分',
  `risk_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'LOW' COMMENT '风险等级(LOW/MEDIUM/HIGH)',
  `dispose_action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处置动作',
  `dispose_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '处置状态',
  `evidence_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '证据JSON',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sc_login_risk_user_time`(`user_id` ASC, `login_time` ASC) USING BTREE,
  INDEX `idx_sc_login_risk_level`(`risk_level` ASC) USING BTREE,
  INDEX `idx_sc_login_risk_type`(`risk_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-登录风险事件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_login_risk_event
-- ----------------------------

-- ----------------------------
-- Table structure for sc_parent_student_rel
-- ----------------------------
DROP TABLE IF EXISTS `sc_parent_student_rel`;
CREATE TABLE `sc_parent_student_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_user_id` bigint NOT NULL COMMENT '家长用户ID',
  `student_user_id` bigint NOT NULL COMMENT '学生用户ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'guardian' COMMENT '关系类型(father/mother/guardian)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sc_parent_student_rel`(`parent_user_id` ASC, `student_user_id` ASC) USING BTREE,
  INDEX `idx_sc_parent_student_student`(`student_user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-家长学生关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_parent_student_rel
-- ----------------------------

-- ----------------------------
-- Table structure for sc_qa_attachment
-- ----------------------------
DROP TABLE IF EXISTS `sc_qa_attachment`;
CREATE TABLE `sc_qa_attachment`  (
  `attachment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `session_id` bigint NULL DEFAULT NULL COMMENT '会话ID',
  `message_id` bigint NULL DEFAULT NULL COMMENT '消息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '系统文件名',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原始文件名',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件访问地址',
  `mime_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint NULL DEFAULT 0 COMMENT '文件大小',
  `storage_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'local' COMMENT '存储类型(local/oss)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1删除)',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`attachment_id`) USING BTREE,
  INDEX `idx_sc_qa_attachment_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_sc_qa_attachment_message_id`(`message_id` ASC) USING BTREE,
  INDEX `idx_sc_qa_attachment_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_sc_qa_attachment_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-问答附件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_qa_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for sc_qa_feedback
-- ----------------------------
DROP TABLE IF EXISTS `sc_qa_feedback`;
CREATE TABLE `sc_qa_feedback`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `message_id` bigint NOT NULL COMMENT '消息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `feedback_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '反馈类型(helpful/unhelpful)',
  `feedback_score` tinyint NULL DEFAULT NULL COMMENT '评分(1-5)',
  `issue_tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '问题标签',
  `feedback_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '反馈内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sc_qa_feedback_message_id`(`message_id` ASC) USING BTREE,
  INDEX `idx_sc_qa_feedback_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-问答反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_qa_feedback
-- ----------------------------

-- ----------------------------
-- Table structure for sc_qa_message
-- ----------------------------
DROP TABLE IF EXISTS `sc_qa_message`;
CREATE TABLE `sc_qa_message`  (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` bigint NOT NULL COMMENT '会话ID',
  `role_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色(user/assistant/system)',
  `content_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'text' COMMENT '内容类型(text/markdown/image/mixed)',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
  `reference_source` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '引用来源',
  `attachment_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '附件元数据JSON',
  `reply_to_message_id` bigint NULL DEFAULT NULL COMMENT '回复的消息ID',
  `model_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回答使用模型名',
  `reasoning_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '思考过程内容',
  `token_count` int NULL DEFAULT 0 COMMENT 'token使用量',
  `latency_ms` int NULL DEFAULT 0 COMMENT '响应耗时',
  `sensitive_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '敏感标记(0否 1是)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_sc_qa_message_session_time`(`session_id` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_sc_qa_message_reply_to`(`reply_to_message_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 75 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-问答消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_qa_message
-- ----------------------------
INSERT INTO `sc_qa_message` VALUES (71, 26, 'user', 'text', '请帮我生成复习计划', '', '', NULL, NULL, NULL, 0, 0, '0', '2026-03-17 13:41:21');
INSERT INTO `sc_qa_message` VALUES (72, 26, 'assistant', 'markdown', '你好！作为智慧校园自主学习助手，我很乐意为你制定一份高效的复习计划。\n\n由于我暂时不了解你的具体**课程名称**、**考试日期**以及**当前掌握程度**，为了给你提供最精准的方案，请你补充以下信息：1. **复习科目**（例如：高等数学、大学英语、Java 程序设计等）\n2. **距离考试的时间**（例如：3 天、2 周）\n3. **每日可用复习时长**4. **目前的薄弱环节**（可选，例如：公式记不住、代码逻辑不清）\n\n---\n\n### 📅 通用高效复习框架（参考模板）\n\n在你提供详细信息前，你可以先参考这套基于**艾宾浩斯遗忘曲线**和**主动回忆法**设计的通用三阶段复习策略：\n\n#### 第一阶段：知识重构与梳理（占总时间 40%）\n*   **目标**：建立知识体系，扫清盲点。\n*   **行动**：\n    *   **绘制思维导图**：不看书，凭记忆画出章节脉络，再对照教材查漏补缺。\n    *   **核心概念复盘**：针对定义、定理、公式进行“费曼技巧”自测（尝试用大白话讲给自己听）。\n    *   **资源推荐**：利用校园图书馆数据库或 MOOC 平台观看该课程的“期末串讲”视频。\n\n#### 第二阶段：真题实战与专项突破（占总时间 40%）\n*   **目标**：熟悉题型，提升解题速度与准确率。\n*   **行动**：\n    *   **限时模拟**：选取近 3-5 年真题，完全按照考试时间进行模拟。\n    *   **错题归因**：建立错题本，分析是“知识点遗忘”、“审题错误”还是“逻辑漏洞”，并针对性回看教材对应章节。\n    *   **高频考点聚焦**：统计真题中出现频率最高的知识点，进行深度挖掘。\n\n#### 第三阶段：全真模拟与心态调整（占总时间 20%）\n*   **目标**：保持手感，调整生物钟。\n*   **行动**：\n    *   **回归基础**：不再做难题，快速浏览笔记、公式表和错题本。\n    *   **全真演练**：在考试相同的时间段进行一次最后的热身模拟。\n    *   **答疑准备**：整理最后遗留的 3-5 个疑难问题，向老师或同学请教。\n\n---\n\n**👉 请告诉我你的具体科目和时间安排，我将立即为你生成一份包含每日任务清单、推荐资源链接及重点难点解析的定制化复习计划。**', '{\"modelName\":\"阿里百炼 Qwen\",\"reasoningContent\":\"\"}', NULL, NULL, '阿里百炼 Qwen', '', 611, 10923, '0', '2026-03-17 13:41:32');
INSERT INTO `sc_qa_message` VALUES (73, 27, 'user', 'text', 'hello', '', '', NULL, NULL, NULL, 0, 0, '0', '2026-03-23 14:39:16');
INSERT INTO `sc_qa_message` VALUES (74, 27, 'assistant', 'markdown', '你好！我是你的智慧校园学习助手，可以为你提供以下帮助：\n\n1. **课程学习**：解答课程疑问、梳理知识框架、制定学习计划。2. **资源推荐**：根据你的需求推荐教材、论文、在线课程或工具。3. **知识点解析**：针对复杂概念提供通俗易懂的解释或示例。4. **考试答疑**：协助复习重点、解析题目、分享备考技巧。请告诉我你的具体需求，我会为你提供简洁专业的支持！ 📚✨', '{\"modelName\":\"DeepSeek\",\"reasoningContent\":\"\"}', NULL, NULL, 'DeepSeek', '', 139, 4818, '0', '2026-03-23 14:39:21');

-- ----------------------------
-- Table structure for sc_qa_session
-- ----------------------------
DROP TABLE IF EXISTS `sc_qa_session`;
CREATE TABLE `sc_qa_session`  (
  `session_id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `session_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话标题',
  `source_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'course' COMMENT '场景类型(general/course/wrong_question)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1结束)',
  `message_count` int NULL DEFAULT 0 COMMENT '消息数量',
  `last_message_preview` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后一条消息摘要',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
  `context_summary` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话上下文摘要',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`session_id`) USING BTREE,
  INDEX `idx_sc_qa_session_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_sc_qa_session_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_sc_qa_session_last_message_time`(`last_message_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-问答会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_qa_session
-- ----------------------------
INSERT INTO `sc_qa_session` VALUES (26, 1, NULL, '请帮我生成复习计划', 'general', '0', 2, '你好！作为智慧校园自主学习助手，我很乐意为你制定一份高效的复习计划。 由于我暂时不了解你的具体**课程名称**、**考试日期**以及**当前掌握程度**，为了给你提供最精准的方案，请你补充以下信息：1. **复习科目**（例如：高等数学、大', '2026-03-17 13:41:33', NULL, '2026-03-17 13:41:21', '2026-03-17 13:41:32');
INSERT INTO `sc_qa_session` VALUES (27, 102, NULL, 'hello', 'general', '0', 2, '你好！我是你的智慧校园学习助手，可以为你提供以下帮助： 1. **课程学习**：解答课程疑问、梳理知识框架、制定学习计划。2. **资源推荐**：根据你的需求推荐教材、论文、在线课程或工具。3. **知识点解析**：针对复杂概念提供通俗易懂', '2026-03-23 14:39:21', NULL, '2026-03-23 14:39:16', '2026-03-23 14:39:21');

-- ----------------------------
-- Table structure for sc_question_bank
-- ----------------------------
DROP TABLE IF EXISTS `sc_question_bank`;
CREATE TABLE `sc_question_bank`  (
  `question_id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `chapter_id` bigint NULL DEFAULT NULL COMMENT '章节ID',
  `knowledge_point_id` bigint NULL DEFAULT NULL COMMENT '知识点ID',
  `question_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题型(single/multiple/judge/fill/essay)',
  `difficulty_level` int NULL DEFAULT 1 COMMENT '难度等级(1-5)',
  `stem` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '题干',
  `answer` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '答案',
  `analysis` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '解析',
  `source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源',
  `question_tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '题目标签，逗号分隔',
  `material_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '材料题/案例题材料内容',
  `source_batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源批次号',
  `quality_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '质量评分',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`question_id`) USING BTREE,
  INDEX `idx_sc_question_course_kp`(`course_id` ASC, `knowledge_point_id` ASC) USING BTREE,
  INDEX `idx_sc_question_type_difficulty`(`question_type` ASC, `difficulty_level` ASC) USING BTREE,
  INDEX `idx_sc_question_source_batch_no`(`source_batch_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-题库表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_question_bank
-- ----------------------------
INSERT INTO `sc_question_bank` VALUES (4, 2, NULL, NULL, 'single', 2, '下列哪项是计算机硬件系统中最核心的部件？', 'A', '中央处理器（CPU）是计算机硬件系统的核心，负责解释和执行程序指令，控制其他硬件设备的工作。', 'AI智能出题', '', '', 'AI-1773986707760', 0.00, '0', 'admin', '2026-03-20 14:05:07', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (5, 2, NULL, NULL, 'single', 3, '在Python中，以下哪个关键字用于定义函数？', 'B', '在Python中，使用def关键字来定义函数，这是Python语言的基本语法规则。', 'AI智能出题', '', '', 'AI-1773993534386', 0.00, '0', 'admin', '2026-03-20 15:58:54', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (6, 2, NULL, NULL, 'single', 3, '下列哪个选项是Python中的可变数据类型？', 'C', '在Python中，list（列表）是可变数据类型，可以修改其内容；而字符串、元组和整数是不可变的。', 'AI智能出题', '', '', 'AI-1773993534389', 0.00, '0', 'admin', '2026-03-20 15:58:54', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (7, 2, NULL, NULL, 'single', 3, '在Python中，用于从列表中删除最后一个元素并返回该元素的方法是？', 'B', 'pop()方法用于移除列表中的一个元素（默认最后一个），并返回该元素的值。', 'AI智能出题', '', '', 'AI-1773993534389', 0.00, '0', 'admin', '2026-03-20 15:58:54', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (8, 2, NULL, NULL, 'single', 3, 'Python中，以下哪个运算符用于计算幂运算？', 'B', '在Python中，**运算符用于幂运算，例如2**3表示2的3次方，结果为8。', 'AI智能出题', '', '', 'AI-1773993534389', 0.00, '0', 'admin', '2026-03-20 15:58:54', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (14, NULL, NULL, NULL, 'single', 3, '在面向对象编程中，用于描述对象所具有的行为或操作的术语是？', 'B', '在面向对象编程中，对象的行为或操作通常通过方法来定义和实现。属性描述对象的状态，类是对对象的抽象定义，而实例是类的具体化。', 'AI智能出题', '', '', 'AI-1774081217791', 0.00, '0', 'admin', '2026-03-21 16:20:17', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (15, NULL, NULL, NULL, 'single', 3, '下列数据结构中，按照“先进后出”原则工作的是？', 'C', '栈是一种线性数据结构，其操作遵循“先进后出”的原则。队列是“先进先出”，链表和树没有固定的全局存取顺序。', 'AI智能出题', '', '', 'AI-1774081217792', 0.00, '0', 'admin', '2026-03-21 16:20:17', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (16, NULL, NULL, NULL, 'single', 3, '在关系型数据库中，用于唯一标识表中每一行记录的字段或字段组合称为？', 'A', '主键是关系型数据库表中的一个或多个字段，其值能唯一地标识表中的每一行记录。外键用于建立表与表之间的关联，索引用于提高查询速度，约束用于保证数据的完整性。', 'AI智能出题', '', '', 'AI-1774081217792', 0.00, '0', 'admin', '2026-03-21 16:20:17', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (17, NULL, NULL, NULL, 'single', 3, '在Python中，用于创建一个新的类定义的关键字是？', 'B', '在Python中，使用关键字`class`来定义一个新的类。`def`用于定义函数，`new`不是用于定义类的关键字，`object`是所有类的基类。', 'AI智能出题', '', '', 'AI-1774081217792', 0.00, '0', 'admin', '2026-03-21 16:20:17', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (18, NULL, NULL, NULL, 'single', 3, '在Java语言中，下列哪个关键字用于声明一个常量，即其值在初始化后不能被修改？', 'D', '在Java中，`final`关键字用于声明常量，表示该变量的值一旦被赋值就不能再被更改。`static`表示静态成员，`const`是Java的保留字但并未使用，`volatile`用于确保变量的可见性。', 'AI智能出题', '', '', 'AI-1774081217792', 0.00, '0', 'admin', '2026-03-21 16:20:17', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (19, 2, NULL, NULL, 'multiple', 3, '在 Python 数据分析中，关于 Pandas 库的 DataFrame 数据结构，以下描述正确的有（ ）。', 'A,B,D', 'DataFrame 是二维的表格型数据结构，具有行索引和列索引（A 正确）；它支持不同数据类型的列，如某一列为整数，另一列为字符串（B 正确）；DataFrame 是可变的数据结构，可以动态添加或删除行列（C 错误）；可以通过字典、列表嵌套等多种方式创建 DataFrame（D 正确）。', 'AI智能出题', '', '', 'AI-1774081794756', 0.00, '0', 'admin', '2026-03-21 16:29:54', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (20, 2, NULL, NULL, 'multiple', 3, '在使用 Matplotlib 进行数据可视化时，下列关于 pyplot 模块常用函数的说法，正确的有（ ）。', 'A,C,D', 'plt.plot() 用于绘制折线图（A 正确）；plt.scatter() 用于绘制散点图，而非柱状图，柱状图应使用 plt.bar()（B 错误）；plt.xlabel() 和 plt.ylabel() 分别用于设置 x 轴和 y 轴的标签（C 正确）；plt.show() 用于显示当前生成的所有图形窗口（D 正确）。', 'AI智能出题', '', '', 'AI-1774081794757', 0.00, '0', 'admin', '2026-03-21 16:29:54', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (21, 2, NULL, NULL, 'multiple', 3, '关于 NumPy 数组（ndarray）的特性与操作，下列选项正确的有（ ）。', 'A,B,C', 'NumPy 数组要求所有元素必须是相同的数据类型，以保证内存连续和计算效率（A 正确）；ndarray 支持广播机制（Broadcasting），允许不同形状的数组进行算术运算（B 正确）；通过 shape 属性可以查看或修改数组的维度形状（C 正确）；NumPy 数组通常比同等大小的 Python 原生列表占用更少的内存且运算速度更快（D 错误）。', 'AI智能出题', '', '', 'AI-1774081794757', 0.00, '0', 'admin', '2026-03-21 16:29:54', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (22, 2, NULL, NULL, 'multiple', 3, '在利用 Pandas 进行数据清洗时，处理缺失值（NaN）的常用方法包括（ ）。', 'A,B,C,D', 'dropna() 可以直接删除包含缺失值的行或列（A 正确）；fillna() 可以使用指定值（如 0、均值等）填充缺失值（B 正确）；interpolate() 可以根据前后数据进行插值填充（C 正确）；isnull() 或 isna() 常用于检测缺失值，是处理流程中的关键步骤，虽不直接“处理”但属于处理策略的一部分，且在多选语境下常作为识别手段被纳入考量，若严格指“填补/删除”动作，前三者最核心，但在广义数据处理流程中，识别也是处理的一环。此处题目问“处理方法”，通常涵盖识别后的处置策略，故全选符合数据分析教学常规认知。', 'AI智能出题', '', '', 'AI-1774081794757', 0.00, '0', 'admin', '2026-03-21 16:29:54', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (23, 2, NULL, NULL, 'multiple', 3, '<p>下列关于 Python 数据分析生态系统中各库主要功能的描述，正确的有（ ）。</p>', 'A,B,D', 'NumPy 提供高性能的多维数组对象及数学函数，是数据分析的基础（A 正确）；Pandas 基于 NumPy 构建，提供 DataFrame 和 Series 结构，擅长数据操纵和分析（B 正确）；Scikit-learn 是机器学习库，虽然依赖数据分析结果，但其核心功能不是数据可视化，可视化主要由 Matplotlib 和 Seaborn 负责（C 错误）；Matplotlib 是基础的绘图库，可用于生成各种静态、动态和交互式可视化图表（D 正确）。', 'AI智能出题', '', '', 'AI-1774081794757', 0.00, '0', 'admin', '2026-03-21 16:29:54', 'admin', '2026-03-21 18:53:56', '');
INSERT INTO `sc_question_bank` VALUES (24, NULL, NULL, NULL, 'single', 3, '在C语言中，若有声明 `int a = 10, *p = &a;`，则表达式 `*p + 1` 的值是多少？', 'B', '指针p指向整型变量a，a的值为10。`*p`表示取p所指向地址的内容，即a的值10。因此`*p + 1`的值为10 + 1 = 11。', 'AI智能出题', '', '', 'AI-1774252388152', 0.00, '0', 'admin', '2026-03-23 15:53:08', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (25, NULL, NULL, NULL, 'single', 3, '以下关于C语言指针的叙述，正确的是？', 'B', '指针的本质是一个变量，其值为另一个变量的内存地址。选项A错误，因为指针变量本身也占用内存空间；选项C错误，指针可以指向任何数据类型，包括void类型；选项D错误，指针可以进行加减运算，但其运算单位取决于所指向数据类型的大小。', 'AI智能出题', '', '', 'AI-1774252388160', 0.00, '0', 'admin', '2026-03-23 15:53:08', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (26, NULL, NULL, NULL, 'single', 3, '若有代码片段：`int arr[3] = {1, 2, 3}; int *ptr = arr;`，则 `*(ptr + 2)` 的值是？', 'C', '数组名arr在大多数表达式中代表数组首元素的地址，即&arr[0]。`ptr = arr`使得ptr指向arr[0]。`ptr + 2`根据指针算术，指向arr[0]向后偏移2个int大小的位置，即arr[2]的地址。`*(ptr + 2)`解引用得到arr[2]的值，即3。', 'AI智能出题', '', '', 'AI-1774252388160', 0.00, '0', 'admin', '2026-03-23 15:53:08', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (27, NULL, NULL, NULL, 'single', 3, '在C语言中，以下哪种方式可以正确地声明一个指向整型常量的指针（即指针指向的值不可修改，但指针本身可以指向别的地址）？', 'A', '`const int *p;` 或 `int const *p;` 声明了一个指向常量整数的指针。指针p可以修改（指向不同的地址），但不能通过*p来修改它所指向的整数值。选项B声明了一个指向整数的常量指针（指针本身不可修改）；选项C声明了一个指向常量整数的常量指针；选项D语法错误。', 'AI智能出题', '', '', 'AI-1774252388160', 0.00, '0', 'admin', '2026-03-23 15:53:08', '', NULL, '');
INSERT INTO `sc_question_bank` VALUES (28, NULL, NULL, NULL, 'single', 3, '对于函数 `void swap(int *x, int *y) { int t = *x; *x = *y; *y = t; }`，以下调用方式中，能正确交换变量a和b值的是？（假设a, b为已定义的int变量）', 'D', '函数swap的参数是指向int的指针，目的是交换两个指针所指向的变量的值。因此，调用时需要传递变量的地址（即&a和&b）。选项A传递的是变量值，选项B和C的语法或逻辑错误。', 'AI智能出题', '', '', 'AI-1774252388160', 0.00, '0', 'admin', '2026-03-23 15:53:08', '', NULL, '');

-- ----------------------------
-- Table structure for sc_question_catalog
-- ----------------------------
DROP TABLE IF EXISTS `sc_question_catalog`;
CREATE TABLE `sc_question_catalog`  (
  `catalog_id` bigint NOT NULL AUTO_INCREMENT COMMENT '题库ID',
  `catalog_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题库名称',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `grade_id` bigint NULL DEFAULT NULL COMMENT '年级ID',
  `owner_user_id` bigint NULL DEFAULT NULL COMMENT '创建人用户ID',
  `visibility_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PRIVATE' COMMENT '可见范围类型(PRIVATE/ROLE_SHARED/CLASS_SHARED/DEPT_SHARED/PUBLIC)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`catalog_id`) USING BTREE,
  INDEX `idx_question_catalog_course`(`course_id` ASC) USING BTREE,
  INDEX `idx_question_catalog_grade`(`grade_id` ASC) USING BTREE,
  INDEX `idx_question_catalog_owner`(`owner_user_id` ASC) USING BTREE,
  INDEX `idx_question_catalog_visibility`(`visibility_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-题库主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_question_catalog
-- ----------------------------

-- ----------------------------
-- Table structure for sc_question_catalog_item_rel
-- ----------------------------
DROP TABLE IF EXISTS `sc_question_catalog_item_rel`;
CREATE TABLE `sc_question_catalog_item_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `catalog_id` bigint NOT NULL COMMENT '题库ID',
  `item_id` bigint NOT NULL COMMENT '题目ID',
  `sort_no` int NULL DEFAULT 0 COMMENT '排序号',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_question_catalog_item`(`catalog_id` ASC, `item_id` ASC) USING BTREE,
  INDEX `idx_question_catalog_item_item`(`item_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-题库题目关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_question_catalog_item_rel
-- ----------------------------

-- ----------------------------
-- Table structure for sc_question_catalog_scope
-- ----------------------------
DROP TABLE IF EXISTS `sc_question_catalog_scope`;
CREATE TABLE `sc_question_catalog_scope`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `catalog_id` bigint NOT NULL COMMENT '题库ID',
  `scope_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '范围类型(ROLE/CLASS/DEPT/USER)',
  `scope_ref_id` bigint NOT NULL COMMENT '范围对象ID',
  `scope_ref_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '范围对象名称快照',
  `permission_level` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'VIEW' COMMENT '权限级别(VIEW/MANAGE)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_catalog_scope_unique`(`catalog_id` ASC, `scope_type` ASC, `scope_ref_id` ASC, `permission_level` ASC) USING BTREE,
  INDEX `idx_catalog_scope_ref`(`scope_type` ASC, `scope_ref_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-题库可见范围表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_question_catalog_scope
-- ----------------------------

-- ----------------------------
-- Table structure for sc_question_item
-- ----------------------------
DROP TABLE IF EXISTS `sc_question_item`;
CREATE TABLE `sc_question_item`  (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目主键ID',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `default_catalog_id` bigint NULL DEFAULT NULL COMMENT '默认题库ID',
  `latest_version_id` bigint NULL DEFAULT NULL COMMENT '最新版本ID',
  `current_version_no` int NOT NULL DEFAULT 1 COMMENT '当前版本号',
  `question_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题型',
  `difficulty_level` int NULL DEFAULT 3 COMMENT '难度级别',
  `source_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'MANUAL' COMMENT '来源类型(MANUAL/IMPORT/AI)',
  `source_ref_id` bigint NULL DEFAULT NULL COMMENT '来源业务ID',
  `source_batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源批次号',
  `creator_user_id` bigint NULL DEFAULT NULL COMMENT '题目创建人用户ID',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `idx_question_item_course`(`course_id` ASC) USING BTREE,
  INDEX `idx_question_item_catalog`(`default_catalog_id` ASC) USING BTREE,
  INDEX `idx_question_item_type`(`question_type` ASC) USING BTREE,
  INDEX `idx_question_item_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-题目主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_question_item
-- ----------------------------
INSERT INTO `sc_question_item` VALUES (1, 2, NULL, 1, 1, 'multiple', 3, 'AI', 23, 'AI-1774081794757', NULL, '0', 'admin', '2026-03-21 18:53:56', 'admin', '2026-03-21 18:53:56', '');
INSERT INTO `sc_question_item` VALUES (2, NULL, NULL, 2, 1, 'single', 3, 'AI', 24, 'AI-1774252388152', NULL, '0', 'admin', '2026-03-23 15:53:08', 'admin', '2026-03-23 15:53:08', NULL);
INSERT INTO `sc_question_item` VALUES (3, NULL, NULL, 3, 1, 'single', 3, 'AI', 25, 'AI-1774252388160', NULL, '0', 'admin', '2026-03-23 15:53:08', 'admin', '2026-03-23 15:53:08', NULL);
INSERT INTO `sc_question_item` VALUES (4, NULL, NULL, 4, 1, 'single', 3, 'AI', 26, 'AI-1774252388160', NULL, '0', 'admin', '2026-03-23 15:53:08', 'admin', '2026-03-23 15:53:08', NULL);
INSERT INTO `sc_question_item` VALUES (5, NULL, NULL, 5, 1, 'single', 3, 'AI', 27, 'AI-1774252388160', NULL, '0', 'admin', '2026-03-23 15:53:08', 'admin', '2026-03-23 15:53:08', NULL);
INSERT INTO `sc_question_item` VALUES (6, NULL, NULL, 6, 1, 'single', 3, 'AI', 28, 'AI-1774252388160', NULL, '0', 'admin', '2026-03-23 15:53:08', 'admin', '2026-03-23 15:53:08', NULL);

-- ----------------------------
-- Table structure for sc_question_item_version
-- ----------------------------
DROP TABLE IF EXISTS `sc_question_item_version`;
CREATE TABLE `sc_question_item_version`  (
  `version_id` bigint NOT NULL AUTO_INCREMENT COMMENT '版本ID',
  `item_id` bigint NOT NULL COMMENT '题目ID',
  `version_no` int NOT NULL COMMENT '版本号',
  `chapter_id` bigint NULL DEFAULT NULL COMMENT '章节ID',
  `knowledge_point_id` bigint NULL DEFAULT NULL COMMENT '知识点ID',
  `stem` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '题干',
  `answer` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '答案',
  `analysis` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '解析',
  `material_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '材料内容',
  `question_tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '题目标签',
  `quality_score` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '质量评分',
  `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源说明',
  `change_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'EDIT' COMMENT '变更类型(CREATE/EDIT/AI/IMPORT)',
  `is_current` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1' COMMENT '是否当前版本(0否 1是)',
  `ai_model_id` bigint NULL DEFAULT NULL COMMENT 'AI模型ID',
  `ai_prompt_snapshot` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI提示词快照',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`version_id`) USING BTREE,
  UNIQUE INDEX `uk_question_item_version`(`item_id` ASC, `version_no` ASC) USING BTREE,
  INDEX `idx_question_item_version_item`(`item_id` ASC) USING BTREE,
  INDEX `idx_question_item_version_current`(`item_id` ASC, `is_current` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-题目版本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_question_item_version
-- ----------------------------
INSERT INTO `sc_question_item_version` VALUES (1, 1, 1, NULL, NULL, '<p>下列关于 Python 数据分析生态系统中各库主要功能的描述，正确的有（ ）。</p>', 'A,B,D', 'NumPy 提供高性能的多维数组对象及数学函数，是数据分析的基础（A 正确）；Pandas 基于 NumPy 构建，提供 DataFrame 和 Series 结构，擅长数据操纵和分析（B 正确）；Scikit-learn 是机器学习库，虽然依赖数据分析结果，但其核心功能不是数据可视化，可视化主要由 Matplotlib 和 Seaborn 负责（C 错误）；Matplotlib 是基础的绘图库，可用于生成各种静态、动态和交互式可视化图表（D 正确）。', '', '', 0.00, 'AI智能出题', 'AI', '1', NULL, NULL, 'admin', '2026-03-21 18:53:56', '', NULL, '');
INSERT INTO `sc_question_item_version` VALUES (2, 2, 1, NULL, NULL, '在C语言中，若有声明 `int a = 10, *p = &a;`，则表达式 `*p + 1` 的值是多少？', 'B', '指针p指向整型变量a，a的值为10。`*p`表示取p所指向地址的内容，即a的值10。因此`*p + 1`的值为10 + 1 = 11。', NULL, NULL, 0.00, 'AI智能出题', 'AI', '1', NULL, NULL, 'admin', '2026-03-23 15:53:08', '', NULL, NULL);
INSERT INTO `sc_question_item_version` VALUES (3, 3, 1, NULL, NULL, '以下关于C语言指针的叙述，正确的是？', 'B', '指针的本质是一个变量，其值为另一个变量的内存地址。选项A错误，因为指针变量本身也占用内存空间；选项C错误，指针可以指向任何数据类型，包括void类型；选项D错误，指针可以进行加减运算，但其运算单位取决于所指向数据类型的大小。', NULL, NULL, 0.00, 'AI智能出题', 'AI', '1', NULL, NULL, 'admin', '2026-03-23 15:53:08', '', NULL, NULL);
INSERT INTO `sc_question_item_version` VALUES (4, 4, 1, NULL, NULL, '若有代码片段：`int arr[3] = {1, 2, 3}; int *ptr = arr;`，则 `*(ptr + 2)` 的值是？', 'C', '数组名arr在大多数表达式中代表数组首元素的地址，即&arr[0]。`ptr = arr`使得ptr指向arr[0]。`ptr + 2`根据指针算术，指向arr[0]向后偏移2个int大小的位置，即arr[2]的地址。`*(ptr + 2)`解引用得到arr[2]的值，即3。', NULL, NULL, 0.00, 'AI智能出题', 'AI', '1', NULL, NULL, 'admin', '2026-03-23 15:53:08', '', NULL, NULL);
INSERT INTO `sc_question_item_version` VALUES (5, 5, 1, NULL, NULL, '在C语言中，以下哪种方式可以正确地声明一个指向整型常量的指针（即指针指向的值不可修改，但指针本身可以指向别的地址）？', 'A', '`const int *p;` 或 `int const *p;` 声明了一个指向常量整数的指针。指针p可以修改（指向不同的地址），但不能通过*p来修改它所指向的整数值。选项B声明了一个指向整数的常量指针（指针本身不可修改）；选项C声明了一个指向常量整数的常量指针；选项D语法错误。', NULL, NULL, 0.00, 'AI智能出题', 'AI', '1', NULL, NULL, 'admin', '2026-03-23 15:53:08', '', NULL, NULL);
INSERT INTO `sc_question_item_version` VALUES (6, 6, 1, NULL, NULL, '对于函数 `void swap(int *x, int *y) { int t = *x; *x = *y; *y = t; }`，以下调用方式中，能正确交换变量a和b值的是？（假设a, b为已定义的int变量）', 'D', '函数swap的参数是指向int的指针，目的是交换两个指针所指向的变量的值。因此，调用时需要传递变量的地址（即&a和&b）。选项A传递的是变量值，选项B和C的语法或逻辑错误。', NULL, NULL, 0.00, 'AI智能出题', 'AI', '1', NULL, NULL, 'admin', '2026-03-23 15:53:08', '', NULL, NULL);

-- ----------------------------
-- Table structure for sc_question_option
-- ----------------------------
DROP TABLE IF EXISTS `sc_question_option`;
CREATE TABLE `sc_question_option`  (
  `option_id` bigint NOT NULL AUTO_INCREMENT COMMENT '选项ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `option_key` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项标识',
  `option_content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '选项内容',
  `is_right` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '是否正确(0否 1是)',
  PRIMARY KEY (`option_id`) USING BTREE,
  INDEX `idx_sc_question_option_question_id`(`question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 117 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-题目选项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_question_option
-- ----------------------------
INSERT INTO `sc_question_option` VALUES (13, 4, 'A', '中央处理器（CPU）', '1');
INSERT INTO `sc_question_option` VALUES (14, 4, 'B', '内存（RAM）', '0');
INSERT INTO `sc_question_option` VALUES (15, 4, 'C', '硬盘（HDD）', '0');
INSERT INTO `sc_question_option` VALUES (16, 4, 'D', '显卡（GPU）', '0');
INSERT INTO `sc_question_option` VALUES (17, 5, 'A', 'function', '0');
INSERT INTO `sc_question_option` VALUES (18, 5, 'B', 'def', '1');
INSERT INTO `sc_question_option` VALUES (19, 5, 'C', 'define', '0');
INSERT INTO `sc_question_option` VALUES (20, 5, 'D', 'func', '0');
INSERT INTO `sc_question_option` VALUES (21, 6, 'A', 'str', '0');
INSERT INTO `sc_question_option` VALUES (22, 6, 'B', 'tuple', '0');
INSERT INTO `sc_question_option` VALUES (23, 6, 'C', 'list', '1');
INSERT INTO `sc_question_option` VALUES (24, 6, 'D', 'int', '0');
INSERT INTO `sc_question_option` VALUES (25, 7, 'A', 'remove()', '0');
INSERT INTO `sc_question_option` VALUES (26, 7, 'B', 'pop()', '1');
INSERT INTO `sc_question_option` VALUES (27, 7, 'C', 'delete()', '0');
INSERT INTO `sc_question_option` VALUES (28, 7, 'D', 'clear()', '0');
INSERT INTO `sc_question_option` VALUES (29, 8, 'A', '^', '0');
INSERT INTO `sc_question_option` VALUES (30, 8, 'B', '**', '1');
INSERT INTO `sc_question_option` VALUES (31, 8, 'C', '//', '0');
INSERT INTO `sc_question_option` VALUES (32, 8, 'D', '%', '0');
INSERT INTO `sc_question_option` VALUES (53, 14, 'A', '属性', '0');
INSERT INTO `sc_question_option` VALUES (54, 14, 'B', '方法', '1');
INSERT INTO `sc_question_option` VALUES (55, 14, 'C', '类', '0');
INSERT INTO `sc_question_option` VALUES (56, 14, 'D', '实例', '0');
INSERT INTO `sc_question_option` VALUES (57, 15, 'A', '队列', '0');
INSERT INTO `sc_question_option` VALUES (58, 15, 'B', '链表', '0');
INSERT INTO `sc_question_option` VALUES (59, 15, 'C', '栈', '1');
INSERT INTO `sc_question_option` VALUES (60, 15, 'D', '二叉树', '0');
INSERT INTO `sc_question_option` VALUES (61, 16, 'A', '主键', '1');
INSERT INTO `sc_question_option` VALUES (62, 16, 'B', '外键', '0');
INSERT INTO `sc_question_option` VALUES (63, 16, 'C', '索引', '0');
INSERT INTO `sc_question_option` VALUES (64, 16, 'D', '约束', '0');
INSERT INTO `sc_question_option` VALUES (65, 17, 'A', 'def', '0');
INSERT INTO `sc_question_option` VALUES (66, 17, 'B', 'class', '1');
INSERT INTO `sc_question_option` VALUES (67, 17, 'C', 'new', '0');
INSERT INTO `sc_question_option` VALUES (68, 17, 'D', 'object', '0');
INSERT INTO `sc_question_option` VALUES (69, 18, 'A', 'static', '0');
INSERT INTO `sc_question_option` VALUES (70, 18, 'B', 'const', '0');
INSERT INTO `sc_question_option` VALUES (71, 18, 'C', 'volatile', '0');
INSERT INTO `sc_question_option` VALUES (72, 18, 'D', 'final', '1');
INSERT INTO `sc_question_option` VALUES (73, 19, 'A', '是一种带有行索引和列索引的二维表格型数据结构', '1');
INSERT INTO `sc_question_option` VALUES (74, 19, 'B', '每一列可以是不同的数据类型', '1');
INSERT INTO `sc_question_option` VALUES (75, 19, 'C', '一旦创建，其大小和形状不可改变', '0');
INSERT INTO `sc_question_option` VALUES (76, 19, 'D', '可以通过字典列表或 NumPy 数组等多种方式创建', '1');
INSERT INTO `sc_question_option` VALUES (77, 20, 'A', 'plot() 函数主要用于绘制折线图', '1');
INSERT INTO `sc_question_option` VALUES (78, 20, 'B', 'scatter() 函数用于绘制柱状图', '0');
INSERT INTO `sc_question_option` VALUES (79, 20, 'C', 'xlabel() 和 ylabel() 用于设置坐标轴标签', '1');
INSERT INTO `sc_question_option` VALUES (80, 20, 'D', 'show() 函数用于将图形渲染并显示出来', '1');
INSERT INTO `sc_question_option` VALUES (81, 21, 'A', '数组中的所有元素必须具有相同的数据类型', '1');
INSERT INTO `sc_question_option` VALUES (82, 21, 'B', '支持广播机制，可进行不同形状数组间的运算', '1');
INSERT INTO `sc_question_option` VALUES (83, 21, 'C', '可以通过 shape 属性获取或修改数组的维度', '1');
INSERT INTO `sc_question_option` VALUES (84, 21, 'D', '相比 Python 原生列表，占用内存更大但灵活性更高', '0');
INSERT INTO `sc_question_option` VALUES (85, 22, 'A', '使用 dropna() 方法直接删除含有缺失值的记录', '1');
INSERT INTO `sc_question_option` VALUES (86, 22, 'B', '使用 fillna() 方法用特定值或统计量填充缺失值', '1');
INSERT INTO `sc_question_option` VALUES (87, 22, 'C', '使用 interpolate() 方法进行插值填充', '1');
INSERT INTO `sc_question_option` VALUES (88, 22, 'D', '使用 isnull() 检测后结合逻辑判断进行自定义处理', '1');
INSERT INTO `sc_question_option` VALUES (93, 23, 'A', 'NumPy 主要用于高效的数值计算和多维数组操作', '1');
INSERT INTO `sc_question_option` VALUES (94, 23, 'B', 'Pandas 提供了强大的数据结构和数据分析工具', '1');
INSERT INTO `sc_question_option` VALUES (95, 23, 'C', 'Scikit-learn 是专门用于高级数据可视化的核心库', '0');
INSERT INTO `sc_question_option` VALUES (96, 23, 'D', 'Matplotlib 是 Python 中最基础且广泛使用的绘图库', '1');
INSERT INTO `sc_question_option` VALUES (97, 24, 'A', '10', '0');
INSERT INTO `sc_question_option` VALUES (98, 24, 'B', '11', '1');
INSERT INTO `sc_question_option` VALUES (99, 24, 'C', '变量a的地址加1', '0');
INSERT INTO `sc_question_option` VALUES (100, 24, 'D', '指针p的值加1', '0');
INSERT INTO `sc_question_option` VALUES (101, 25, 'A', '指针变量不占用内存空间', '0');
INSERT INTO `sc_question_option` VALUES (102, 25, 'B', '指针变量存储的是内存地址', '1');
INSERT INTO `sc_question_option` VALUES (103, 25, 'C', '指针只能指向基本数据类型（如int, char）', '0');
INSERT INTO `sc_question_option` VALUES (104, 25, 'D', '指针不能进行加减运算', '0');
INSERT INTO `sc_question_option` VALUES (105, 26, 'A', '1', '0');
INSERT INTO `sc_question_option` VALUES (106, 26, 'B', '2', '0');
INSERT INTO `sc_question_option` VALUES (107, 26, 'C', '3', '1');
INSERT INTO `sc_question_option` VALUES (108, 26, 'D', '编译错误', '0');
INSERT INTO `sc_question_option` VALUES (109, 27, 'A', 'const int *p;', '1');
INSERT INTO `sc_question_option` VALUES (110, 27, 'B', 'int * const p;', '0');
INSERT INTO `sc_question_option` VALUES (111, 27, 'C', 'const int * const p;', '0');
INSERT INTO `sc_question_option` VALUES (112, 27, 'D', 'int const p*;', '0');
INSERT INTO `sc_question_option` VALUES (113, 28, 'A', 'swap(a, b);', '0');
INSERT INTO `sc_question_option` VALUES (114, 28, 'B', 'swap(*a, *b);', '0');
INSERT INTO `sc_question_option` VALUES (115, 28, 'C', 'swap(int *a, int *b);', '0');
INSERT INTO `sc_question_option` VALUES (116, 28, 'D', 'swap(&a, &b);', '1');

-- ----------------------------
-- Table structure for sc_resource
-- ----------------------------
DROP TABLE IF EXISTS `sc_resource`;
CREATE TABLE `sc_resource`  (
  `resource_id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `resource_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源名称',
  `resource_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源类型(video/pdf/ppt/doc/question)',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `chapter_id` bigint NULL DEFAULT NULL COMMENT '章节ID',
  `uploader_id` bigint NULL DEFAULT NULL COMMENT '上传者ID',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件地址',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面地址',
  `content_text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '抽取文本内容',
  `summary` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '摘要',
  `audit_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '审核状态(0待审 1通过 2驳回)',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `favorite_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `quality_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '质量评分',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`resource_id`) USING BTREE,
  INDEX `idx_sc_resource_course_type`(`course_id` ASC, `resource_type` ASC) USING BTREE,
  INDEX `idx_sc_resource_uploader_id`(`uploader_id` ASC) USING BTREE,
  INDEX `idx_sc_resource_audit_status`(`audit_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-学习资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_resource
-- ----------------------------
INSERT INTO `sc_resource` VALUES (1, '测试资源', 'pdf', 2, NULL, 1, '/profile/upload/2026/03/24/2025-2026年度安徽省职业院校技能大赛（高职组）拟获奖名单（学生赛）_20260324123351A001.pdf', '', '阅读难度：basic\n可打印：是', '阅读难度：basic\n可打印：是', '1', 0, 0, 0, 80.00, '0', 'admin', '2026-03-24 12:33:55', '', NULL, '{\"knowledgePointIds\":[],\"keywordsText\":\"\",\"learningAdvice\":\"\",\"aiInsights\":\"\",\"videoDuration\":\"\",\"videoResolution\":\"hd\",\"posterTime\":\"\",\"captionLanguage\":\"zh-CN\",\"pageCount\":\"\",\"readingLevel\":\"basic\",\"readingMinutes\":\"\",\"printable\":true,\"keyPoints\":\"\",\"questionType\":\"single\",\"difficultyLevel\":1,\"questionStem\":\"\",\"questionAnswer\":\"\",\"questionAnalysis\":\"\"}');

-- ----------------------------
-- Table structure for sc_resource_index
-- ----------------------------
DROP TABLE IF EXISTS `sc_resource_index`;
CREATE TABLE `sc_resource_index`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id` bigint NOT NULL COMMENT '资源ID',
  `keyword_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '关键词文本',
  `embedding_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '向量索引ID',
  `index_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '索引状态(0待建 1完成 2失败)',
  `last_index_time` datetime NULL DEFAULT NULL COMMENT '最后索引时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sc_resource_index_resource_id`(`resource_id` ASC) USING BTREE,
  INDEX `idx_sc_resource_index_status`(`index_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-资源检索索引表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_resource_index
-- ----------------------------

-- ----------------------------
-- Table structure for sc_resource_tag
-- ----------------------------
DROP TABLE IF EXISTS `sc_resource_tag`;
CREATE TABLE `sc_resource_tag`  (
  `tag_id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  `tag_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'common' COMMENT '标签类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE INDEX `uk_sc_resource_tag_name`(`tag_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-资源标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_resource_tag
-- ----------------------------

-- ----------------------------
-- Table structure for sc_resource_tag_rel
-- ----------------------------
DROP TABLE IF EXISTS `sc_resource_tag_rel`;
CREATE TABLE `sc_resource_tag_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id` bigint NOT NULL COMMENT '资源ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sc_resource_tag_rel`(`resource_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_sc_resource_tag_rel_tag_id`(`tag_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-资源标签关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_resource_tag_rel
-- ----------------------------

-- ----------------------------
-- Table structure for sc_school_term
-- ----------------------------
DROP TABLE IF EXISTS `sc_school_term`;
CREATE TABLE `sc_school_term`  (
  `term_id` bigint NOT NULL AUTO_INCREMENT COMMENT '学期ID',
  `term_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期名称（如：2025-2026学年第一学期）',
  `school_year` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学年（如：2025-2026）',
  `term_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学期编码（自定义规则）',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序序号',
  `is_current` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '是否当前学期：0-否 1-是',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态：0-禁用 1-启用',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `start_date` date NULL DEFAULT NULL COMMENT '学期开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '学期结束日期',
  `total_weeks` int NULL DEFAULT 20 COMMENT '总周数',
  PRIMARY KEY (`term_id`) USING BTREE,
  INDEX `idx_school_year`(`school_year` ASC) USING BTREE,
  INDEX `idx_is_current`(`is_current` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学校学期表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_school_term
-- ----------------------------
INSERT INTO `sc_school_term` VALUES (1, '2025-2026学年第二学期', '2025-2026', '2025-2026-1', 1, '1', '0', 'admin', '2026-03-17 16:31:09', 'admin', '2026-03-17 18:58:09', '', '2026-03-05', '2026-07-12', 19);

-- ----------------------------
-- Table structure for sc_search_log
-- ----------------------------
DROP TABLE IF EXISTS `sc_search_log`;
CREATE TABLE `sc_search_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '搜索关键词',
  `search_mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'keyword' COMMENT '检索模式(keyword/semantic)',
  `result_count` int NULL DEFAULT 0 COMMENT '结果数量',
  `click_resource_id` bigint NULL DEFAULT NULL COMMENT '点击资源ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sc_search_log_user_time`(`user_id` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_sc_search_log_keyword`(`keyword` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-搜索日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_search_log
-- ----------------------------

-- ----------------------------
-- Table structure for sc_study_record
-- ----------------------------
DROP TABLE IF EXISTS `sc_study_record`;
CREATE TABLE `sc_study_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `chapter_id` bigint NULL DEFAULT NULL COMMENT '章节ID',
  `resource_id` bigint NULL DEFAULT NULL COMMENT '资源ID',
  `behavior_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '行为类型(view/play/download/favorite/search/answer)',
  `duration_seconds` int NULL DEFAULT 0 COMMENT '时长(秒)',
  `progress_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '进度百分比',
  `device_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类型',
  `source_page` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源页面',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `idx_sc_study_record_user_time`(`user_id` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_sc_study_record_course_behavior`(`course_id` ASC, `behavior_type` ASC) USING BTREE,
  INDEX `idx_sc_study_record_resource_id`(`resource_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-学习行为记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_study_record
-- ----------------------------
INSERT INTO `sc_study_record` VALUES (1, 102, 2, NULL, 1, 'TASK_OPEN', 1, 0.00, 'web', 'portal_task|messages|manual|manual-1-1', '2026-03-24 12:30:33');
INSERT INTO `sc_study_record` VALUES (2, 102, 2, NULL, 1, 'TASK_OPEN', 1, 0.00, 'web', 'portal_task|messages|manual|manual-1-1', '2026-03-24 12:30:36');

-- ----------------------------
-- Table structure for sc_user_achievement
-- ----------------------------
DROP TABLE IF EXISTS `sc_user_achievement`;
CREATE TABLE `sc_user_achievement`  (
  `achievement_id` bigint NOT NULL AUTO_INCREMENT COMMENT '成就ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `achievement_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '成就编码',
  `achievement_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '成就标题',
  `achievement_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '成就描述',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '状态(1已解锁 0未解锁)',
  `progress_value` int NULL DEFAULT 0 COMMENT '进度值',
  `earned_time` datetime NULL DEFAULT NULL COMMENT '达成时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`achievement_id`) USING BTREE,
  UNIQUE INDEX `uk_user_achievement_code`(`user_id` ASC, `achievement_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-用户成就表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_user_achievement
-- ----------------------------
INSERT INTO `sc_user_achievement` VALUES (1, 102, 'EXAM_FIRST', '考试达人·初启', '完成首次考试提交', '1', 1, '2026-03-23 16:32:56', 'system', '2026-03-23 16:32:56', 'system', '2026-03-23 23:19:30', NULL);

-- ----------------------------
-- Table structure for sc_user_point_account
-- ----------------------------
DROP TABLE IF EXISTS `sc_user_point_account`;
CREATE TABLE `sc_user_point_account`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_points` int NULL DEFAULT 0 COMMENT '累计积分',
  `available_points` int NULL DEFAULT 0 COMMENT '可用积分',
  `used_points` int NULL DEFAULT 0 COMMENT '已使用积分',
  `level_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'L1' COMMENT '成长等级编码',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_point_account_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-用户积分账户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_user_point_account
-- ----------------------------
INSERT INTO `sc_user_point_account` VALUES (1, 102, 20, 20, 0, 'L1', 'system', '2026-03-23 16:32:56', 'system', '2026-03-23 23:19:30', NULL);

-- ----------------------------
-- Table structure for sc_user_point_ledger
-- ----------------------------
DROP TABLE IF EXISTS `sc_user_point_ledger`;
CREATE TABLE `sc_user_point_ledger`  (
  `ledger_id` bigint NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `change_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '变更类型(EARN/USE/REFUND)',
  `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型',
  `biz_id` bigint NULL DEFAULT NULL COMMENT '业务ID',
  `change_points` int NOT NULL COMMENT '积分变更值',
  `balance_after` int NULL DEFAULT 0 COMMENT '变更后余额',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`ledger_id`) USING BTREE,
  UNIQUE INDEX `uk_user_point_ledger_biz`(`user_id` ASC, `change_type` ASC, `biz_type` ASC, `biz_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-用户积分流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_user_point_ledger
-- ----------------------------
INSERT INTO `sc_user_point_ledger` VALUES (1, 102, 'EARN', 'EXAM_SUBMIT', 9, 10, 10, 'system', '2026-03-23 16:32:56', '考试提交奖励积分');
INSERT INTO `sc_user_point_ledger` VALUES (2, 102, 'EARN', 'EXAM_SUBMIT', 10, 10, 20, 'system', '2026-03-23 23:19:30', '考试提交奖励积分');

-- ----------------------------
-- Table structure for sc_user_profile
-- ----------------------------
DROP TABLE IF EXISTS `sc_user_profile`;
CREATE TABLE `sc_user_profile`  (
  `profile_id` bigint NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户类型(student/teacher/admin/parent)',
  `student_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `teacher_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工号',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '性别(0未知 1男 2女)',
  `grade_id` bigint NULL DEFAULT NULL COMMENT '年级ID',
  `class_id` bigint NULL DEFAULT NULL COMMENT '班级ID',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `admission_year` int NULL DEFAULT NULL COMMENT '入学年份',
  `learning_goal` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学习目标',
  `interest_tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '兴趣标签',
  `learning_style` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学习风格',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`profile_id`) USING BTREE,
  UNIQUE INDEX `uk_sc_user_profile_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_sc_user_profile_student_no`(`student_no` ASC) USING BTREE,
  INDEX `idx_sc_user_profile_teacher_no`(`teacher_no` ASC) USING BTREE,
  INDEX `idx_sc_user_profile_grade_class`(`grade_id` ASC, `class_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-用户扩展档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_user_profile
-- ----------------------------
INSERT INTO `sc_user_profile` VALUES (1, 1, 'admin', '2401036049', '', '程安宁', '0', NULL, NULL, '', NULL, '', '', '', '/profile/avatar/2026/03/17/50b92bae76c04d358ac421ae7bca7a63.png', '0', 'admin', '2026-03-11 23:24:28', 'admin', '2026-03-17 16:18:57', '管理员');
INSERT INTO `sc_user_profile` VALUES (3, 101, 'teacher', NULL, '000001', '张老师', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', '0', 'admin', '2026-03-17 14:32:47', 'admin', '2026-03-17 16:39:43', NULL);
INSERT INTO `sc_user_profile` VALUES (4, 102, 'student', '2401036049', '', '张三', '0', 2, 2, '计算机应用技术', 2024, '', '', '', '', '0', '', '2026-03-17 15:08:02', 'zhangsan', '2026-03-23 13:00:47', '');

-- ----------------------------
-- Table structure for sc_wrong_question_book
-- ----------------------------
DROP TABLE IF EXISTS `sc_wrong_question_book`;
CREATE TABLE `sc_wrong_question_book`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `wrong_count` int NULL DEFAULT 1 COMMENT '错误次数',
  `last_wrong_time` datetime NULL DEFAULT NULL COMMENT '最后做错时间',
  `mastery_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '掌握状态(0未掌握 1已掌握)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sc_wrong_question_user_question`(`user_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_sc_wrong_question_course_id`(`course_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智慧校园-错题本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sc_wrong_question_book
-- ----------------------------
INSERT INTO `sc_wrong_question_book` VALUES (5, 102, 14, NULL, 1, '2026-03-23 23:19:30', '0');
INSERT INTO `sc_wrong_question_book` VALUES (6, 102, 7, 2, 1, '2026-03-23 23:19:30', '0');
INSERT INTO `sc_wrong_question_book` VALUES (7, 102, 6, 2, 1, '2026-03-23 23:19:30', '0');
INSERT INTO `sc_wrong_question_book` VALUES (8, 102, 5, 2, 1, '2026-03-23 23:19:30', '0');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 102 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2026-03-10 19:39:28', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2026-03-10 19:39:28', '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2026-03-10 19:39:28', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', '2026-03-10 19:39:28', '', NULL, '是否开启验证码功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'true', 'Y', 'admin', '2026-03-10 19:39:28', 'admin', '2026-03-17 15:07:55', '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (6, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2026-03-10 19:39:28', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
INSERT INTO `sys_config` VALUES (7, '用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 'Y', 'admin', '2026-03-10 19:39:28', '', NULL, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES (8, '用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 'Y', 'admin', '2026-03-10 19:39:28', '', NULL, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES (100, '课表打印分组JSON', 'campus.schedule.printConfigs', '[{\"nameZh\":\"上午\",\"nameEn\":\"MORNING\",\"unitGroup\":[[1,2],[3,4]]},{\"nameZh\":\"下午\",\"nameEn\":\"AFTERNOON\",\"unitGroup\":[[5,6],[7,8]]},{\"nameZh\":\"晚上\",\"nameEn\":\"EVENING\",\"unitGroup\":[[9,10]]}]', 'Y', 'admin', '2026-03-17 18:22:27', '', NULL, '校园课表打印分组');
INSERT INTO `sys_config` VALUES (101, '课表布局JSON', 'campus.schedule.timeTableLayout', '{\"id\":1,\"n\":\"默认课表布局\",\"e\":\"Default time table layout\",\"u\":[[\"1\",1,820,900,\"M\"],[\"2\",2,910,950,\"M\"],[\"3\",3,1005,1045,\"M\"],[\"4\",4,1045,1125,\"M\"],[\"中午\",5,1215,1240,\"N\"],[\"中午\",6,1245,1315,\"N\"],[\"5\",7,1400,1440,\"A\"],[\"6\",8,1450,1530,\"A\"],[\"7\",9,1545,1625,\"A\"],[\"8\",10,1635,1715,\"A\"],[\"9\",11,1900,1940,\"E\"],[\"10\",12,1950,2030,\"E\"]]}', 'Y', 'admin', '2026-03-17 18:23:25', 'admin', '2026-03-20 08:50:43', '校园课表布局配置');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 343 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (300, 0, '0', '智慧校园', 0, '信息化平台主管', '15800000000', 'campus@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (301, 300, '0,300', '校务中心', 1, '校务主任', '15800000001', 'office@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (302, 300, '0,300', '教务处', 2, '教务主任', '15800000002', 'academic@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (303, 300, '0,300', '学生发展中心', 3, '学工主任', '15800000003', 'student@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (304, 300, '0,300', '家校协同中心', 4, '家校主任', '15800000004', 'parent@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (310, 302, '0,300,302', '教学管理科', 1, '课务主管', '15800000010', 'course@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (311, 302, '0,300,302', '考试与评价科', 2, '考试主管', '15800000011', 'exam@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (312, 302, '0,300,302', '资源建设科', 3, '资源主管', '15800000012', 'resource@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (313, 302, '0,300,302', '学情分析科', 4, '分析主管', '15800000013', 'analysis@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (320, 310, '0,300,302,310', '人工智能学院', 1, '学院负责人', '15800000020', 'ai-college@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (321, 310, '0,300,302,310', '电子信息工程学院', 2, '学院负责人', '15800000021', 'ee-college@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (322, 310, '0,300,302,310', '马克思主义学院', 3, '学院负责人', '15800000022', 'marx-college@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (330, 320, '0,300,302,310,320', '软件工程教研室', 1, '教研室主任', '15800000030', 'se-lab@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (331, 320, '0,300,302,310,320', '数据科学教研室', 2, '教研室主任', '15800000031', 'data-lab@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (332, 321, '0,300,302,310,321', '网络工程教研室', 1, '教研室主任', '15800000032', 'net-lab@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (333, 321, '0,300,302,310,321', '物联网教研室', 2, '教研室主任', '15800000033', 'iot-lab@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (340, 303, '0,300,303', '辅导员中心', 1, '辅导员负责人', '15800000040', 'advisor@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (341, 303, '0,300,303', '班主任中心', 2, '班主任负责人', '15800000041', 'headteacher@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);
INSERT INTO `sys_dept` VALUES (342, 304, '0,300,304', '家长服务组', 1, '家校负责人', '15800000042', 'family@school.edu.cn', '0', '0', 'admin', '2026-03-17 16:23:05', '', NULL);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 153 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (100, 1, '学生', 'student', 'sc_user_type', '', 'primary', 'Y', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '学生');
INSERT INTO `sys_dict_data` VALUES (101, 2, '教师', 'teacher', 'sc_user_type', '', 'success', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '教师');
INSERT INTO `sys_dict_data` VALUES (102, 3, '管理员', 'admin', 'sc_user_type', '', 'danger', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '管理员');
INSERT INTO `sys_dict_data` VALUES (103, 4, '家长', 'parent', 'sc_user_type', '', 'warning', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '家长');
INSERT INTO `sys_dict_data` VALUES (104, 1, '视频', 'video', 'sc_resource_type', '', 'primary', 'Y', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '视频资源');
INSERT INTO `sys_dict_data` VALUES (105, 2, '文档', 'doc', 'sc_resource_type', '', 'success', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '文档资源');
INSERT INTO `sys_dict_data` VALUES (106, 3, 'PPT', 'ppt', 'sc_resource_type', '', 'warning', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, 'PPT资源');
INSERT INTO `sys_dict_data` VALUES (107, 4, 'PDF', 'pdf', 'sc_resource_type', '', 'danger', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, 'PDF资源');
INSERT INTO `sys_dict_data` VALUES (108, 5, '题目', 'question', 'sc_resource_type', '', 'info', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '题目资源');
INSERT INTO `sys_dict_data` VALUES (109, 1, '浏览', 'view', 'sc_behavior_type', '', 'primary', 'Y', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '浏览行为');
INSERT INTO `sys_dict_data` VALUES (110, 2, '播放', 'play', 'sc_behavior_type', '', 'success', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '播放行为');
INSERT INTO `sys_dict_data` VALUES (111, 3, '下载', 'download', 'sc_behavior_type', '', 'warning', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '下载行为');
INSERT INTO `sys_dict_data` VALUES (112, 4, '收藏', 'favorite', 'sc_behavior_type', '', 'danger', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '收藏行为');
INSERT INTO `sys_dict_data` VALUES (113, 5, '搜索', 'search', 'sc_behavior_type', '', 'info', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '搜索行为');
INSERT INTO `sys_dict_data` VALUES (114, 6, '答题', 'answer', 'sc_behavior_type', '', 'primary', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '答题行为');
INSERT INTO `sys_dict_data` VALUES (115, 1, '低风险', 'LOW', 'sc_risk_level', '', 'success', 'Y', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '低风险');
INSERT INTO `sys_dict_data` VALUES (116, 2, '中风险', 'MEDIUM', 'sc_risk_level', '', 'warning', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '中风险');
INSERT INTO `sys_dict_data` VALUES (117, 3, '高风险', 'HIGH', 'sc_risk_level', '', 'danger', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '高风险');
INSERT INTO `sys_dict_data` VALUES (118, 1, '低活跃', 'low_active', 'sc_warning_type', '', 'warning', 'Y', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '低活跃');
INSERT INTO `sys_dict_data` VALUES (119, 2, '成绩下滑', 'score_down', 'sc_warning_type', '', 'danger', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '成绩下滑');
INSERT INTO `sys_dict_data` VALUES (120, 3, '高风险登录', 'high_risk', 'sc_warning_type', '', 'danger', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '高风险登录');
INSERT INTO `sys_dict_data` VALUES (121, 1, '单选题', 'single', 'sc_question_type', '', 'primary', 'Y', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '单选题');
INSERT INTO `sys_dict_data` VALUES (122, 2, '多选题', 'multiple', 'sc_question_type', '', 'success', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '多选题');
INSERT INTO `sys_dict_data` VALUES (123, 3, '判断题', 'judge', 'sc_question_type', '', 'warning', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '判断题');
INSERT INTO `sys_dict_data` VALUES (124, 4, '填空题', 'fill', 'sc_question_type', '', 'info', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '填空题');
INSERT INTO `sys_dict_data` VALUES (125, 5, '简答题', 'essay', 'sc_question_type', '', 'danger', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '简答题');
INSERT INTO `sys_dict_data` VALUES (126, 1, '初始化', 'INIT', 'sc_exam_status', '', 'info', 'Y', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '初始化');
INSERT INTO `sys_dict_data` VALUES (127, 2, '进行中', 'ONGOING', 'sc_exam_status', '', 'warning', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '进行中');
INSERT INTO `sys_dict_data` VALUES (128, 3, '已提交', 'SUBMITTED', 'sc_exam_status', '', 'success', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '已提交');
INSERT INTO `sys_dict_data` VALUES (129, 1, '首页推荐', 'home', 'sc_recommend_scene', '', 'primary', 'Y', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '首页推荐');
INSERT INTO `sys_dict_data` VALUES (130, 2, '考前冲刺', 'exam_before', 'sc_recommend_scene', '', 'warning', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '考前冲刺');
INSERT INTO `sys_dict_data` VALUES (131, 3, '每日计划', 'daily_plan', 'sc_recommend_scene', '', 'success', 'N', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '每日计划');
INSERT INTO `sys_dict_data` VALUES (132, 1, '计算机应用技术', '计算机应用技术', 'campus_major_type', '', 'primary', 'Y', '0', 'admin', '2026-03-17 23:35:50', '', NULL, '默认专业');
INSERT INTO `sys_dict_data` VALUES (133, 2, '软件技术', '软件技术', 'campus_major_type', '', 'success', 'N', '0', 'admin', '2026-03-17 23:35:50', '', NULL, '专业字典');
INSERT INTO `sys_dict_data` VALUES (134, 3, '大数据技术', '大数据技术', 'campus_major_type', '', 'warning', 'N', '0', 'admin', '2026-03-17 23:35:50', '', NULL, '专业字典');
INSERT INTO `sys_dict_data` VALUES (135, 4, '人工智能技术应用', '人工智能技术应用', 'campus_major_type', '', 'danger', 'N', '0', 'admin', '2026-03-17 23:35:50', '', NULL, '专业字典');
INSERT INTO `sys_dict_data` VALUES (136, 5, '计算机网络技术', '计算机网络技术', 'campus_major_type', '', 'info', 'N', '0', 'admin', '2026-03-17 23:35:50', '', NULL, '专业字典');
INSERT INTO `sys_dict_data` VALUES (137, 6, '信息安全技术应用', '信息安全技术应用', 'campus_major_type', '', 'primary', 'N', '0', 'admin', '2026-03-17 23:35:50', '', NULL, '专业字典');
INSERT INTO `sys_dict_data` VALUES (138, 7, '物联网应用技术', '物联网应用技术', 'campus_major_type', '', 'success', 'N', '0', 'admin', '2026-03-17 23:35:50', '', NULL, '专业字典');
INSERT INTO `sys_dict_data` VALUES (139, 8, '数字媒体技术', '数字媒体技术', 'campus_major_type', '', 'warning', 'N', '0', 'admin', '2026-03-17 23:35:50', '', NULL, '专业字典');
INSERT INTO `sys_dict_data` VALUES (140, 9, '电子信息工程技术', '电子信息工程技术', 'campus_major_type', '', 'danger', 'N', '0', 'admin', '2026-03-17 23:35:50', '', NULL, '专业字典');
INSERT INTO `sys_dict_data` VALUES (141, 10, '现代通信技术', '现代通信技术', 'campus_major_type', '', 'info', 'N', '0', 'admin', '2026-03-17 23:35:50', '', NULL, '专业字典');
INSERT INTO `sys_dict_data` VALUES (142, 1, '本部', '本部', 'campus_area_type', '', 'primary', 'Y', '0', 'admin', '2026-03-18 20:16:24', '', NULL, '默认校区');
INSERT INTO `sys_dict_data` VALUES (143, 2, '东校区', '东校区', 'campus_area_type', '', 'success', 'N', '0', 'admin', '2026-03-18 20:16:24', '', NULL, '校区字典');
INSERT INTO `sys_dict_data` VALUES (144, 3, '西校区', '西校区', 'campus_area_type', '', 'warning', 'N', '0', 'admin', '2026-03-18 20:16:24', '', NULL, '校区字典');
INSERT INTO `sys_dict_data` VALUES (145, 1, '普通教室', '普通教室', 'classroom_room_type', '', 'primary', 'Y', '0', 'admin', '2026-03-18 20:16:24', '', NULL, '默认教室类型');
INSERT INTO `sys_dict_data` VALUES (146, 2, '机房', '机房', 'classroom_room_type', '', 'success', 'N', '0', 'admin', '2026-03-18 20:16:24', '', NULL, '教室类型字典');
INSERT INTO `sys_dict_data` VALUES (147, 3, '实验室', '实验室', 'classroom_room_type', '', 'warning', 'N', '0', 'admin', '2026-03-18 20:16:24', '', NULL, '教室类型字典');
INSERT INTO `sys_dict_data` VALUES (148, 4, '报告厅', '报告厅', 'classroom_room_type', '', 'danger', 'N', '0', 'admin', '2026-03-18 20:16:24', '', NULL, '教室类型字典');
INSERT INTO `sys_dict_data` VALUES (149, 1, '知行楼', '知行楼', 'campus_building_type', '', 'default', 'Y', '0', 'admin', '2026-03-20 09:33:09', 'admin', '2026-03-20 09:41:40', '默认教学楼');
INSERT INTO `sys_dict_data` VALUES (150, 2, '善学楼', '善学楼', 'campus_building_type', '', 'default', 'N', '0', 'admin', '2026-03-20 09:33:09', 'admin', '2026-03-20 09:41:50', '教学楼字典');
INSERT INTO `sys_dict_data` VALUES (151, 3, '笃行楼', '笃行楼', 'campus_building_type', '', 'default', 'N', '0', 'admin', '2026-03-20 09:33:09', 'admin', '2026-03-20 09:42:15', '教学楼字典');
INSERT INTO `sys_dict_data` VALUES (152, 4, '躬行楼', '躬行楼', 'campus_building_type', '', 'default', 'N', '0', 'admin', '2026-03-20 09:33:10', 'admin', '2026-03-20 09:41:24', '教学楼字典');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 112 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2026-03-10 19:39:28', '', NULL, '登录状态列表');
INSERT INTO `sys_dict_type` VALUES (100, '智慧校园-用户类型', 'sc_user_type', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '学生/教师/管理员/家长');
INSERT INTO `sys_dict_type` VALUES (101, '智慧校园-资源类型', 'sc_resource_type', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '学习资源类型');
INSERT INTO `sys_dict_type` VALUES (102, '智慧校园-学习行为', 'sc_behavior_type', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '学习行为类型');
INSERT INTO `sys_dict_type` VALUES (103, '智慧校园-风险等级', 'sc_risk_level', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '登录和学情风险等级');
INSERT INTO `sys_dict_type` VALUES (104, '智慧校园-预警类型', 'sc_warning_type', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '学情预警类型');
INSERT INTO `sys_dict_type` VALUES (105, '智慧校园-题目类型', 'sc_question_type', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '题库题型');
INSERT INTO `sys_dict_type` VALUES (106, '智慧校园-考试状态', 'sc_exam_status', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '考试记录状态');
INSERT INTO `sys_dict_type` VALUES (107, '智慧校园-推荐场景', 'sc_recommend_scene', '0', 'admin', '2026-03-11 13:21:49', '', NULL, '推荐展示场景');
INSERT INTO `sys_dict_type` VALUES (108, '校园专业', 'campus_major_type', '0', 'admin', '2026-03-17 23:35:50', '', NULL, '智慧校园学生/教师专业与方向字典');
INSERT INTO `sys_dict_type` VALUES (109, '校园校区', 'campus_area_type', '0', 'admin', '2026-03-18 20:16:24', '', NULL, '教室和教学任务使用的校区字典');
INSERT INTO `sys_dict_type` VALUES (110, '教室类型', 'classroom_room_type', '0', 'admin', '2026-03-18 20:16:24', '', NULL, '教室管理使用的类型字典');
INSERT INTO `sys_dict_type` VALUES (111, '教学楼', 'campus_building_type', '0', 'admin', '2026-03-20 09:33:09', '', NULL, '教室管理与排课位置使用的教学楼字典');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '定时任务调度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2026-03-10 19:39:28', '', NULL, '');
INSERT INTO `sys_job` VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2026-03-10 19:39:28', '', NULL, '');
INSERT INTO `sys_job` VALUES (3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2026-03-10 19:39:28', '', NULL, '');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '异常信息',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE,
  INDEX `idx_sys_logininfor_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_logininfor_lt`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 217 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (100, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-10 19:41:28');
INSERT INTO `sys_logininfor` VALUES (101, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-11 13:23:32');
INSERT INTO `sys_logininfor` VALUES (102, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-11 13:45:01');
INSERT INTO `sys_logininfor` VALUES (103, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-11 13:52:22');
INSERT INTO `sys_logininfor` VALUES (104, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-11 18:28:42');
INSERT INTO `sys_logininfor` VALUES (105, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-11 23:20:11');
INSERT INTO `sys_logininfor` VALUES (106, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-11 23:20:47');
INSERT INTO `sys_logininfor` VALUES (107, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-11 23:21:05');
INSERT INTO `sys_logininfor` VALUES (108, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-12 10:07:26');
INSERT INTO `sys_logininfor` VALUES (109, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '1', '验证码已失效', '2026-03-12 10:25:00');
INSERT INTO `sys_logininfor` VALUES (110, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-12 12:12:03');
INSERT INTO `sys_logininfor` VALUES (111, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-12 13:16:12');
INSERT INTO `sys_logininfor` VALUES (112, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '1', '验证码已失效', '2026-03-12 13:32:11');
INSERT INTO `sys_logininfor` VALUES (113, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '1', '验证码错误', '2026-03-12 13:32:13');
INSERT INTO `sys_logininfor` VALUES (114, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-12 13:32:15');
INSERT INTO `sys_logininfor` VALUES (115, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-13 16:22:54');
INSERT INTO `sys_logininfor` VALUES (116, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-14 19:46:16');
INSERT INTO `sys_logininfor` VALUES (117, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-15 12:58:48');
INSERT INTO `sys_logininfor` VALUES (118, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '1', '验证码错误', '2026-03-15 19:48:14');
INSERT INTO `sys_logininfor` VALUES (119, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-15 19:48:15');
INSERT INTO `sys_logininfor` VALUES (120, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-16 00:31:58');
INSERT INTO `sys_logininfor` VALUES (121, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-16 00:32:38');
INSERT INTO `sys_logininfor` VALUES (122, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-16 16:04:09');
INSERT INTO `sys_logininfor` VALUES (123, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-16 16:04:22');
INSERT INTO `sys_logininfor` VALUES (124, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-16 16:28:06');
INSERT INTO `sys_logininfor` VALUES (125, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-16 16:28:49');
INSERT INTO `sys_logininfor` VALUES (126, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-16 16:41:43');
INSERT INTO `sys_logininfor` VALUES (127, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-16 16:43:15');
INSERT INTO `sys_logininfor` VALUES (128, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-16 16:44:20');
INSERT INTO `sys_logininfor` VALUES (129, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-16 18:44:30');
INSERT INTO `sys_logininfor` VALUES (130, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-17 09:05:36');
INSERT INTO `sys_logininfor` VALUES (131, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-17 13:22:18');
INSERT INTO `sys_logininfor` VALUES (132, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '注册成功', '2026-03-17 15:08:03');
INSERT INTO `sys_logininfor` VALUES (133, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '1', '用户不存在/密码错误', '2026-03-17 15:08:13');
INSERT INTO `sys_logininfor` VALUES (134, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-17 15:08:22');
INSERT INTO `sys_logininfor` VALUES (135, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-17 15:59:44');
INSERT INTO `sys_logininfor` VALUES (136, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-17 15:59:50');
INSERT INTO `sys_logininfor` VALUES (137, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-17 16:13:18');
INSERT INTO `sys_logininfor` VALUES (138, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-17 16:13:22');
INSERT INTO `sys_logininfor` VALUES (139, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-17 22:19:36');
INSERT INTO `sys_logininfor` VALUES (140, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-17 22:19:49');
INSERT INTO `sys_logininfor` VALUES (141, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-17 22:21:34');
INSERT INTO `sys_logininfor` VALUES (142, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-17 22:21:43');
INSERT INTO `sys_logininfor` VALUES (143, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-17 23:10:52');
INSERT INTO `sys_logininfor` VALUES (144, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-17 23:10:56');
INSERT INTO `sys_logininfor` VALUES (145, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-17 23:40:28');
INSERT INTO `sys_logininfor` VALUES (146, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-17 23:40:32');
INSERT INTO `sys_logininfor` VALUES (147, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-18 00:11:46');
INSERT INTO `sys_logininfor` VALUES (148, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-18 00:11:51');
INSERT INTO `sys_logininfor` VALUES (149, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-18 12:05:36');
INSERT INTO `sys_logininfor` VALUES (150, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '1', '验证码错误', '2026-03-18 12:05:41');
INSERT INTO `sys_logininfor` VALUES (151, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-18 12:05:46');
INSERT INTO `sys_logininfor` VALUES (152, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-18 17:33:00');
INSERT INTO `sys_logininfor` VALUES (153, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '1', '验证码错误', '2026-03-18 17:33:06');
INSERT INTO `sys_logininfor` VALUES (154, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '1', '验证码已失效', '2026-03-18 17:33:08');
INSERT INTO `sys_logininfor` VALUES (155, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '1', '验证码错误', '2026-03-18 17:33:10');
INSERT INTO `sys_logininfor` VALUES (156, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-18 17:33:14');
INSERT INTO `sys_logininfor` VALUES (157, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-19 08:50:21');
INSERT INTO `sys_logininfor` VALUES (158, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-19 12:55:44');
INSERT INTO `sys_logininfor` VALUES (159, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-19 12:55:47');
INSERT INTO `sys_logininfor` VALUES (160, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-19 12:55:52');
INSERT INTO `sys_logininfor` VALUES (161, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-19 16:39:25');
INSERT INTO `sys_logininfor` VALUES (162, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-19 18:29:52');
INSERT INTO `sys_logininfor` VALUES (163, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-19 18:35:36');
INSERT INTO `sys_logininfor` VALUES (164, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-19 18:35:40');
INSERT INTO `sys_logininfor` VALUES (165, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-19 23:03:14');
INSERT INTO `sys_logininfor` VALUES (166, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '退出成功', '2026-03-19 23:03:24');
INSERT INTO `sys_logininfor` VALUES (167, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-19 23:03:27');
INSERT INTO `sys_logininfor` VALUES (168, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-20 00:09:36');
INSERT INTO `sys_logininfor` VALUES (169, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-20 08:44:54');
INSERT INTO `sys_logininfor` VALUES (170, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '1', '验证码错误', '2026-03-20 12:46:07');
INSERT INTO `sys_logininfor` VALUES (171, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '1', '验证码已失效', '2026-03-20 12:46:08');
INSERT INTO `sys_logininfor` VALUES (172, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '1', '验证码错误', '2026-03-20 12:46:12');
INSERT INTO `sys_logininfor` VALUES (173, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-20 12:46:15');
INSERT INTO `sys_logininfor` VALUES (174, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-21 12:33:19');
INSERT INTO `sys_logininfor` VALUES (175, 'admin', '127.0.0.1', '内网IP', 'Edge 146', 'Windows >=10', '0', '登录成功', '2026-03-21 12:33:32');
INSERT INTO `sys_logininfor` VALUES (176, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-21 15:24:52');
INSERT INTO `sys_logininfor` VALUES (177, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码已失效', '2026-03-21 15:30:15');
INSERT INTO `sys_logininfor` VALUES (178, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-21 15:30:21');
INSERT INTO `sys_logininfor` VALUES (179, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:38:51');
INSERT INTO `sys_logininfor` VALUES (180, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码已失效', '2026-03-21 20:38:54');
INSERT INTO `sys_logininfor` VALUES (181, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:38:58');
INSERT INTO `sys_logininfor` VALUES (182, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:39:55');
INSERT INTO `sys_logininfor` VALUES (183, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:40:01');
INSERT INTO `sys_logininfor` VALUES (184, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:41:17');
INSERT INTO `sys_logininfor` VALUES (185, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:41:19');
INSERT INTO `sys_logininfor` VALUES (186, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:45:59');
INSERT INTO `sys_logininfor` VALUES (187, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:46:18');
INSERT INTO `sys_logininfor` VALUES (188, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:46:26');
INSERT INTO `sys_logininfor` VALUES (189, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:49:22');
INSERT INTO `sys_logininfor` VALUES (190, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:49:24');
INSERT INTO `sys_logininfor` VALUES (191, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:51:02');
INSERT INTO `sys_logininfor` VALUES (192, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:51:05');
INSERT INTO `sys_logininfor` VALUES (193, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:51:18');
INSERT INTO `sys_logininfor` VALUES (194, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-21 20:52:53');
INSERT INTO `sys_logininfor` VALUES (195, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-21 20:52:58');
INSERT INTO `sys_logininfor` VALUES (196, 'student', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '用户不存在/密码错误', '2026-03-21 23:31:27');
INSERT INTO `sys_logininfor` VALUES (197, 'student', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '用户不存在/密码错误', '2026-03-21 23:31:29');
INSERT INTO `sys_logininfor` VALUES (198, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-21 23:31:43');
INSERT INTO `sys_logininfor` VALUES (199, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-22 14:22:20');
INSERT INTO `sys_logininfor` VALUES (200, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码已失效', '2026-03-22 16:44:17');
INSERT INTO `sys_logininfor` VALUES (201, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-22 16:44:19');
INSERT INTO `sys_logininfor` VALUES (202, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-22 16:44:24');
INSERT INTO `sys_logininfor` VALUES (203, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-22 16:44:32');
INSERT INTO `sys_logininfor` VALUES (204, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-22 21:24:19');
INSERT INTO `sys_logininfor` VALUES (205, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码错误', '2026-03-22 22:51:27');
INSERT INTO `sys_logininfor` VALUES (206, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-22 22:51:29');
INSERT INTO `sys_logininfor` VALUES (207, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-23 12:38:09');
INSERT INTO `sys_logininfor` VALUES (208, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-23 13:09:46');
INSERT INTO `sys_logininfor` VALUES (209, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '退出成功', '2026-03-23 13:10:05');
INSERT INTO `sys_logininfor` VALUES (210, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-23 13:10:06');
INSERT INTO `sys_logininfor` VALUES (211, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-23 21:51:34');
INSERT INTO `sys_logininfor` VALUES (212, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '1', '验证码已失效', '2026-03-23 21:51:34');
INSERT INTO `sys_logininfor` VALUES (213, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-23 21:51:35');
INSERT INTO `sys_logininfor` VALUES (214, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-23 21:51:55');
INSERT INTO `sys_logininfor` VALUES (215, 'admin', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-24 12:03:43');
INSERT INTO `sys_logininfor` VALUES (216, 'zhangsan', '127.0.0.1', '内网IP', 'Edge 147', 'Windows >=10', '0', '登录成功', '2026-03-24 12:03:57');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由名称',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2237 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '基础设置', 0, 1, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'ri-settings-3-line', 'admin', '2026-03-10 19:39:27', 'admin', '2026-03-12 13:13:37', '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', '', 1, 0, 'M', '0', '0', '', 'ri-line-chart-line', 'admin', '2026-03-10 19:39:27', '', NULL, '系统监控目录');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'ri-user-3-line', 'admin', '2026-03-10 19:39:27', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '', 1, 0, 'C', '0', '0', 'system:role:list', 'ri-team-line', 'admin', '2026-03-10 19:39:27', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'ri-node-tree', 'admin', '2026-03-10 19:39:27', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'ri-git-branch-line', 'admin', '2026-03-10 19:39:27', '', NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', '', 1, 0, 'C', '0', '0', 'system:post:list', 'ri-briefcase-4-line', 'admin', '2026-03-10 19:39:27', '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'ri-book-2-line', 'admin', '2026-03-10 19:39:27', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', '', 1, 0, 'C', '0', '0', 'system:config:list', 'ri-edit-box-line', 'admin', '2026-03-10 19:39:27', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'ri-message-3-line', 'admin', '2026-03-10 19:39:27', '', NULL, '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-file-list-3-line', 'admin', '2026-03-10 19:39:27', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'ri-user-search-line', 'admin', '2026-03-10 19:39:27', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'ri-timer-line', 'admin', '2026-03-10 19:39:27', '', NULL, '定时任务菜单');
INSERT INTO `sys_menu` VALUES (111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'ri-database-2-line', 'admin', '2026-03-10 19:39:27', '', NULL, '数据监控菜单');
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'ri-server-line', 'admin', '2026-03-10 19:39:27', '', NULL, '服务监控菜单');
INSERT INTO `sys_menu` VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'ri-stack-line', 'admin', '2026-03-10 19:39:27', '', NULL, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES (114, '缓存列表', 2, 6, 'cacheList', 'monitor/cache/list', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'ri-file-list-3-line', 'admin', '2026-03-10 19:39:27', '', NULL, '缓存列表菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'ri-file-text-line', 'admin', '2026-03-10 19:39:27', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'ri-login-box-line', 'admin', '2026-03-10 19:39:27', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1000, '用户查询', 100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1001, '用户新增', 100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户修改', 100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户删除', 100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户导出', 100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导入', 100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '重置密码', 100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '角色查询', 101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色新增', 101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色修改', 101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色删除', 101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色导出', 101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '菜单查询', 102, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单新增', 102, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单修改', 102, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单删除', 102, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '部门查询', 103, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, '部门新增', 103, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, '部门修改', 103, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, '部门删除', 103, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, '岗位查询', 104, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, '岗位新增', 104, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, '岗位修改', 104, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '岗位删除', 104, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '岗位导出', 104, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '字典查询', 105, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典新增', 105, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典修改', 105, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典删除', 105, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典导出', 105, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '参数查询', 106, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数新增', 106, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数修改', 106, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数删除', 106, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数导出', 106, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '公告查询', 107, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告新增', 107, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告修改', 107, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告删除', 107, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '操作查询', 500, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作删除', 500, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '日志导出', 500, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '登录查询', 501, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录删除', 501, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '日志导出', 501, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '账户解锁', 501, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '批量强退', 109, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1049, '任务查询', 110, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1050, '任务新增', 110, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1051, '任务修改', 110, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1052, '任务删除', 110, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1053, '状态修改', 110, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1054, '任务导出', 110, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2000, '智慧校园', 0, 10, 'campus', NULL, '', '', 1, 0, 'M', '0', '0', '', 'ri-graduation-cap-line', 'admin', '2026-03-17 18:32:10', '', NULL, '智慧校园主目录');
INSERT INTO `sys_menu` VALUES (2001, '首页概览', 2000, 1, 'overview', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-dashboard-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '首页与多端概览');
INSERT INTO `sys_menu` VALUES (2002, '用户与身份', 2000, 2, 'identity', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-user-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '用户扩展与家长绑定');
INSERT INTO `sys_menu` VALUES (2003, '教学基础', 2000, 3, 'teaching', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-node-tree', 'admin', '2026-03-17 18:32:10', '', NULL, '年级班级课程基础数据');
INSERT INTO `sys_menu` VALUES (2004, '学习中心', 2000, 4, 'learning', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-graduation-cap-line', 'admin', '2026-03-17 18:32:10', '', NULL, '资源行为画像推荐');
INSERT INTO `sys_menu` VALUES (2005, '智能问答', 2000, 5, 'qa', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-chat-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '问答与反馈');
INSERT INTO `sys_menu` VALUES (2006, '智能测评', 2000, 6, 'exam', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-ai-generate-2', 'admin', '2026-03-17 18:32:10', '', NULL, '题库试卷考试错题');
INSERT INTO `sys_menu` VALUES (2007, '学情分析', 2000, 7, 'analysis', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-bar-chart-box-line', 'admin', '2026-03-17 18:32:10', '', NULL, '预警与报告');
INSERT INTO `sys_menu` VALUES (2008, 'AI管理', 2000, 8, 'ai', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-robot-2-line', 'admin', '2026-03-17 18:32:10', '', NULL, '模型模板日志');
INSERT INTO `sys_menu` VALUES (2010, '概览面板', 2001, 1, 'dashboard', 'campus/overview/index', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'ri-dashboard-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '统一概览页面');
INSERT INTO `sys_menu` VALUES (2011, '学生概览', 2001, 2, 'studentDashboard', 'campus/overview/student', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'ri-user-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '学生端概览');
INSERT INTO `sys_menu` VALUES (2012, '教师概览', 2001, 3, 'teacherDashboard', 'campus/overview/teacher', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'ri-team-line', 'admin', '2026-03-17 18:32:10', '', NULL, '教师端概览');
INSERT INTO `sys_menu` VALUES (2013, '家长概览', 2001, 4, 'parentDashboard', 'campus/overview/parent', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'ri-parent-line', 'admin', '2026-03-17 18:32:10', '', NULL, '家长端概览');
INSERT INTO `sys_menu` VALUES (2020, '用户档案', 2002, 1, 'userProfile', 'campus/userProfile/index', '', '', 1, 0, 'C', '0', '0', 'campus:userProfile:list', 'ri-account-box-line', 'admin', '2026-03-17 18:32:10', '', NULL, '用户扩展档案');
INSERT INTO `sys_menu` VALUES (2021, '家长绑定', 2002, 2, 'parentStudentRel', 'campus/parentStudentRel/index', '', '', 1, 0, 'C', '0', '0', 'campus:parentStudentRel:list', 'ri-links-line', 'admin', '2026-03-17 18:32:10', '', NULL, '家长学生绑定');
INSERT INTO `sys_menu` VALUES (2022, '登录风险', 2002, 3, 'loginRiskEvent', 'campus/loginRiskEvent/index', '', '', 1, 0, 'C', '0', '0', 'campus:loginRiskEvent:list', 'ri-shield-user-line', 'admin', '2026-03-17 18:32:10', '', NULL, '登录风险事件');
INSERT INTO `sys_menu` VALUES (2030, '年级管理', 2003, 1, 'grade', 'campus/grade/index', '', '', 1, 0, 'C', '0', '0', 'campus:grade:list', 'ri-git-branch-line', 'admin', '2026-03-17 18:32:10', '', NULL, '年级管理');
INSERT INTO `sys_menu` VALUES (2031, '班级管理', 2003, 2, 'class', 'campus/class/index', '', '', 1, 0, 'C', '0', '0', 'campus:class:list', 'ri-organization-chart', 'admin', '2026-03-17 18:32:10', '', NULL, '班级管理');
INSERT INTO `sys_menu` VALUES (2032, '课程管理', 2003, 3, 'course', 'campus/course/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'ri-book-open-line', 'admin', '2026-03-17 18:32:10', '', NULL, '课程管理');
INSERT INTO `sys_menu` VALUES (2034, '选课关系', 2003, 4, 'courseStudent', 'campus/courseStudent/index', '', '', 1, 0, 'C', '0', '0', 'campus:courseStudent:list', 'ri-links-line', 'admin', '2026-03-17 18:32:10', '', NULL, '课程学生关系');
INSERT INTO `sys_menu` VALUES (2035, '知识点管理', 2003, 5, 'knowledgePoint', 'campus/knowledgePoint/index', '', '', 1, 0, 'C', '0', '0', 'campus:knowledgePoint:list', 'ri-book-marked-line', 'admin', '2026-03-17 18:32:10', '', NULL, '知识点管理');
INSERT INTO `sys_menu` VALUES (2036, '班级学生', 2003, 6, 'classStudent', 'campus/classStudent/index', '', '', 1, 0, 'C', '0', '0', 'campus:userProfile:list', 'ri-group-line', 'admin', '2026-03-17 18:32:10', '', NULL, '班级学生关系');
INSERT INTO `sys_menu` VALUES (2037, '学年学期', 2003, 7, 'schoolTerm', 'campus/schoolTerm/index', '', '', 1, 0, 'C', '0', '0', 'campus:grade:list', 'ri-calendar-schedule-line', 'admin', '2026-03-17 18:32:10', '', NULL, '学年学期管理');
INSERT INTO `sys_menu` VALUES (2038, '班级课程', 2003, 8, 'classCourse', 'campus/classCourse/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'ri-book-2-line', 'admin', '2026-03-17 18:32:10', '', NULL, '班级课程关系');
INSERT INTO `sys_menu` VALUES (2039, '排课表', 2003, 9, 'courseSchedule', 'campus/courseSchedule/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'ri-calendar-view', 'admin', '2026-03-17 18:32:10', '', NULL, '排课表管理');
INSERT INTO `sys_menu` VALUES (2040, '教室管理', 2003, 11, 'classroom', 'campus/classroom/index', '', '', 1, 0, 'C', '0', '0', 'campus:classroom:list', 'ri-building-line', 'admin', '2026-03-17 18:32:10', '', NULL, '统一维护教室、楼栋与校区');
INSERT INTO `sys_menu` VALUES (2041, '行为记录', 2004, 2, 'studyRecord', 'campus/studyRecord/index', '', '', 1, 0, 'C', '0', '0', 'campus:studyRecord:list', 'ri-file-text-line', 'admin', '2026-03-17 18:32:10', '', NULL, '学习行为记录');
INSERT INTO `sys_menu` VALUES (2042, '学习画像', 2004, 3, 'learningProfile', 'campus/learningProfile/index', '', '', 1, 0, 'C', '0', '0', 'campus:learningProfile:list', 'ri-brain-line', 'admin', '2026-03-17 18:32:10', '', NULL, '学习画像');
INSERT INTO `sys_menu` VALUES (2043, '个性推荐', 2004, 4, 'learningRecommendation', 'campus/learningRecommendation/index', '', '', 1, 0, 'C', '0', '0', 'campus:learningRecommendation:list', 'ri-road-map-line', 'admin', '2026-03-17 18:32:10', '', NULL, '个性化推荐');
INSERT INTO `sys_menu` VALUES (2044, '班级课表', 2003, 11, 'classSchedule', 'campus/classSchedule/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'ri-table-view', 'admin', '2026-03-17 18:32:10', '', NULL, '班级课表视图');
INSERT INTO `sys_menu` VALUES (2045, '资源管理', 2004, 10, 'resource', 'campus/resource/index', '', '', 1, 0, 'C', '0', '0', 'campus:resource:list', 'ri-article-line', 'admin', '2026-03-17 18:32:10', '', NULL, '学习资源管理');
INSERT INTO `sys_menu` VALUES (2046, '节次布局', 2003, 12, 'timeTableLayout', 'campus/timeTableLayout/index', '', '', 1, 0, 'C', '0', '0', 'campus:timetable:list', 'ri-layout-grid-line', 'admin', '2026-03-17 18:44:01', '', NULL, '课表节次与时间布局配置');
INSERT INTO `sys_menu` VALUES (2050, '问答会话', 2005, 1, 'qaSession', 'campus/qa/session/index', '', '', 1, 0, 'C', '0', '0', 'campus:qa:session:list', 'ri-message-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '问答会话');
INSERT INTO `sys_menu` VALUES (2051, '问答消息', 2005, 2, 'qaMessage', 'campus/qa/message/index', '', '', 1, 0, 'C', '0', '0', 'campus:qa:message:list', 'ri-chat-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '问答消息');
INSERT INTO `sys_menu` VALUES (2052, '问答反馈', 2005, 3, 'qaFeedback', 'campus/qa/feedback/index', '', '', 1, 0, 'C', '0', '0', 'campus:qa:feedback:add', 'ri-feedback-line', 'admin', '2026-03-17 18:32:10', '', NULL, '问答反馈');
INSERT INTO `sys_menu` VALUES (2060, '题目管理', 2006, 2, 'questionBank', 'campus/exam/question/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:question:list', 'ri-book-open-line', 'admin', '2026-03-17 18:32:10', '', NULL, '题目管理');
INSERT INTO `sys_menu` VALUES (2061, '题目选项', 2006, 3, 'questionOption', 'campus/exam/option/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:question:list', 'ri-file-list-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '题目选项');
INSERT INTO `sys_menu` VALUES (2062, '试卷管理', 2006, 4, 'examPaper', 'campus/exam/paper/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:paper:list', 'ri-git-merge-line', 'admin', '2026-03-17 18:32:10', '', NULL, '试卷管理');
INSERT INTO `sys_menu` VALUES (2063, '考试记录', 2006, 6, 'examRecord', 'campus/exam/record/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:record:list', 'ri-calendar-2-line', 'admin', '2026-03-17 18:32:10', '', NULL, '考试记录');
INSERT INTO `sys_menu` VALUES (2064, '错题本', 2006, 7, 'wrongQuestionBook', 'campus/exam/wrong/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:record:list', 'ri-edit-box-line', 'admin', '2026-03-17 18:32:10', '', NULL, '错题本');
INSERT INTO `sys_menu` VALUES (2065, '独立题库', 2006, 1, 'questionCatalog', 'campus/exam/catalog/index', '', '', 1, 0, 'C', '0', '0', 'campus:questionCatalog:list', 'ri-folders-line', 'admin', '2026-03-21 18:02:04', '', NULL, '独立题库管理');
INSERT INTO `sys_menu` VALUES (2066, '组卷策略', 2006, 5, 'examStrategy', 'campus/exam/strategy/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:strategy:list', 'ri-flow-chart', 'admin', '2026-03-21 18:02:05', '', NULL, '组卷策略模板管理');
INSERT INTO `sys_menu` VALUES (2070, '学情预警', 2007, 1, 'learningWarning', 'campus/analysis/warning/index', '', '', 1, 0, 'C', '0', '0', 'campus:analysis:warning:list', 'ri-alert-line', 'admin', '2026-03-17 18:32:10', '', NULL, '学情预警');
INSERT INTO `sys_menu` VALUES (2071, '学情报告', 2007, 2, 'learningReport', 'campus/analysis/report/index', '', '', 1, 0, 'C', '0', '0', 'campus:analysis:report:list', 'ri-clipboard-line', 'admin', '2026-03-17 18:32:10', '', NULL, '学情报告');
INSERT INTO `sys_menu` VALUES (2080, '模型配置', 2008, 1, 'aiModel', 'campus/ai/model/index', '', '', 1, 0, 'C', '0', '0', 'campus:ai:model:list', 'ri-robot-2-line', 'admin', '2026-03-17 18:32:10', '', NULL, 'AI模型配置');
INSERT INTO `sys_menu` VALUES (2081, 'Prompt模板', 2008, 2, 'aiPrompt', 'campus/ai/prompt/index', '', '', 1, 0, 'C', '0', '0', 'campus:ai:prompt:list', 'ri-file-text-line', 'admin', '2026-03-17 18:32:10', '', NULL, 'Prompt模板');
INSERT INTO `sys_menu` VALUES (2082, '任务日志', 2008, 3, 'aiTaskLog', 'campus/ai/task/index', '', '', 1, 0, 'C', '0', '0', 'campus:ai:task:list', 'ri-file-list-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, 'AI任务日志');
INSERT INTO `sys_menu` VALUES (2100, '档案查询', 2020, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2101, '档案新增', 2020, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2102, '档案修改', 2020, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2103, '档案删除', 2020, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2110, '资源查询', 2045, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2111, '资源新增', 2045, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2112, '资源修改', 2045, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2113, '资源删除', 2045, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2120, '行为查询', 2041, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:studyRecord:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2121, '行为新增', 2041, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:studyRecord:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2130, '画像查询', 2042, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningProfile:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2131, '画像重建', 2042, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningProfile:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2140, '推荐查询', 2043, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningRecommendation:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2141, '推荐生成', 2043, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningRecommendation:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2150, '预警查询', 2070, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:analysis:warning:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2151, '预警构建', 2070, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:analysis:warning:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2152, '报告查询', 2071, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:analysis:report:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2153, '报告生成', 2071, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:analysis:report:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2160, '题库查询', 2060, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:question:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2161, '题库新增', 2060, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:question:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2162, '试卷查询', 2062, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:paper:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2163, '试卷新增', 2062, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:paper:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2164, '记录查询', 2063, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:record:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2165, '题库查询', 2065, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:questionCatalog:list', '#', 'admin', '2026-03-21 18:02:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2166, '题库新增', 2065, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:questionCatalog:add', '#', 'admin', '2026-03-21 18:02:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2167, '题库编辑', 2065, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:questionCatalog:edit', '#', 'admin', '2026-03-21 18:02:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2168, '题库删除', 2065, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:questionCatalog:remove', '#', 'admin', '2026-03-21 18:02:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2169, '策略查询', 2066, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:strategy:list', '#', 'admin', '2026-03-21 18:02:05', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2170, '模型查询', 2080, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:model:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2171, '模型新增', 2080, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:model:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2172, '模板查询', 2081, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:prompt:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2173, '模板新增', 2081, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:prompt:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2174, '任务日志查询', 2082, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:task:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2180, '年级查询', 2030, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2181, '年级新增', 2030, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2182, '年级修改', 2030, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2183, '年级删除', 2030, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2184, '班级查询', 2031, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:class:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2185, '班级新增', 2031, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:class:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2186, '班级修改', 2031, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:class:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2187, '班级删除', 2031, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:class:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2188, '课程查询', 2032, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2189, '课程新增', 2032, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2190, '课程修改', 2032, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2191, '课程删除', 2032, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2196, '选课查询', 2034, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseStudent:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2197, '选课新增', 2034, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseStudent:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2198, '选课修改', 2034, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseStudent:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2199, '选课删除', 2034, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseStudent:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2200, '知识点查询', 2035, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:knowledgePoint:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2201, '知识点新增', 2035, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:knowledgePoint:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2202, '知识点修改', 2035, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:knowledgePoint:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2203, '知识点删除', 2035, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:knowledgePoint:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2204, '班级学生配置', 2036, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2205, '学期查询', 2037, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2206, '学期新增', 2037, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2207, '学期修改', 2037, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2208, '学期删除', 2037, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2209, '班级课程查询', 2038, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2210, '班级课程新增', 2038, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2211, '班级课程修改', 2038, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2212, '班级课程删除', 2038, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2213, '排课查询', 2039, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2214, '排课新增', 2039, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2215, '排课修改', 2039, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2216, '排课删除', 2039, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2221, '教室查询', 2045, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:query', '#', 'admin', '2026-03-17 18:32:10', 'admin', '2026-03-17 18:44:01', '');
INSERT INTO `sys_menu` VALUES (2222, '教室新增', 2045, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:add', '#', 'admin', '2026-03-17 18:32:10', 'admin', '2026-03-17 18:44:01', '');
INSERT INTO `sys_menu` VALUES (2223, '教室修改', 2045, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:edit', '#', 'admin', '2026-03-17 18:32:10', 'admin', '2026-03-17 18:44:01', '');
INSERT INTO `sys_menu` VALUES (2224, '教室删除', 2045, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:remove', '#', 'admin', '2026-03-17 18:32:10', 'admin', '2026-03-17 18:44:01', '');
INSERT INTO `sys_menu` VALUES (2225, '布局查询', 2046, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:timetable:list', '#', 'admin', '2026-03-17 18:32:10', 'admin', '2026-03-17 18:44:01', '');
INSERT INTO `sys_menu` VALUES (2226, '布局修改', 2046, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:timetable:edit', '#', 'admin', '2026-03-17 18:32:10', 'admin', '2026-03-17 18:44:01', '');
INSERT INTO `sys_menu` VALUES (2230, '学习任务', 2004, 5, 'learningTask', 'campus/learningTask/index', '', '', 1, 0, 'C', '0', '0', 'campus:learningTask:list', 'ri-task-line', 'admin', '2026-03-22 16:43:39', '', NULL, '学习任务与任务派发管理');
INSERT INTO `sys_menu` VALUES (2231, '任务查询', 2230, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:list', '#', 'admin', '2026-03-22 16:43:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2232, '任务详情', 2230, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:query', '#', 'admin', '2026-03-22 16:43:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2233, '任务新增', 2230, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:add', '#', 'admin', '2026-03-22 16:43:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2234, '任务修改', 2230, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:edit', '#', 'admin', '2026-03-22 16:43:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2235, '任务删除', 2230, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:remove', '#', 'admin', '2026-03-22 16:43:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2236, '任务派发', 2230, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:dispatch', '#', 'admin', '2026-03-22 16:43:39', '', NULL, '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `biz_category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NOTICE' COMMENT '业务分类(TODO/MESSAGE/NOTICE)',
  `notice_summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '摘要',
  `receiver_scope` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ALL' COMMENT '接收范围(ALL/STUDENT/TEACHER/PARENT/CUSTOM)',
  `receiver_user_ids` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '指定接收用户ID，英文逗号分隔',
  `action_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '动作类型',
  `action_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '动作路径',
  `action_target_id` bigint NULL DEFAULT NULL COMMENT '动作目标ID',
  `pinned` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '是否置顶(0否1是)',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '失效时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (10, '测试', '1', 0x3C703E313C2F703E, '0', 'NOTICE', '测试', 'ALL', NULL, NULL, NULL, NULL, '0', '2026-03-15 13:58:53', NULL, 'admin', '2026-03-15 13:58:53', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_notice_read
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice_read`;
CREATE TABLE `sys_notice_read`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `notice_id` bigint NOT NULL COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `read_time` datetime NOT NULL COMMENT '已读时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_notice_user`(`notice_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_notice_read_user`(`user_id` ASC, `read_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知公告已读记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice_read
-- ----------------------------
INSERT INTO `sys_notice_read` VALUES (1, 10, 102, '2026-03-24 12:31:19', 'zhangsan', '2026-03-23 14:01:21', 'zhangsan', '2026-03-24 12:31:19');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  INDEX `idx_sys_oper_log_bt`(`business_type` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_ot`(`oper_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 266 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (100, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/4', '127.0.0.1', '内网IP', '4 ', '{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}', 0, NULL, '2026-03-11 13:24:26', 14);
INSERT INTO `sys_oper_log` VALUES (101, '角色管理', 2, 'com.smart.web.controller.system.SysRoleController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2026-03-10 19:39:27\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,114,3,115,116,1055,1056,1057,1058,1059,1060,117],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-11 13:24:36', 40);
INSERT INTO `sys_oper_log` VALUES (102, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/4', '127.0.0.1', '内网IP', '4 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-11 13:24:42', 19);
INSERT INTO `sys_oper_log` VALUES (103, '用户扩展档案', 1, 'com.smart.web.controller.campus.ScUserProfileController.add()', 'POST', 1, 'admin', '研发部门', '/campus/userProfile', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"learningGoal\":\"\",\"params\":{},\"profileId\":1,\"realName\":\"程安宁\",\"status\":\"0\",\"studentNo\":\"2401036049\",\"teacherNo\":\"\",\"userId\":1,\"userType\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-11 23:24:28', 24);
INSERT INTO `sys_oper_log` VALUES (104, 'AI模型配置', 1, 'com.smart.web.controller.campus.ScAiController.addModel()', 'POST', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"sk-ea608da4de674be3a85f209dee2d2638\",\"baseUrl\":\"https://api.deepseek.com\",\"createBy\":\"admin\",\"modelId\":1,\"modelName\":\"deepseek-chat\",\"modelType\":\"chat\",\"params\":{},\"provider\":\"deepseek\",\"status\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-11 23:56:33', 184);
INSERT INTO `sys_oper_log` VALUES (105, 'Prompt模板', 1, 'com.smart.web.controller.campus.ScAiController.addPrompt()', 'POST', 1, 'admin', '研发部门', '/campus/ai/prompt', '127.0.0.1', '内网IP', '{\"bizType\":\"test\",\"createBy\":\"admin\",\"params\":{},\"promptContent\":\"请随机生成一道高中数学题目。\",\"status\":\"0\",\"templateCode\":\"test\",\"templateId\":1,\"templateName\":\"test\",\"version\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-11 23:57:18', 11);
INSERT INTO `sys_oper_log` VALUES (106, 'AI模型配置', 1, 'com.smart.web.controller.campus.ScAiController.addModel()', 'POST', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"12345678\",\"baseUrl\":\"root\",\"createBy\":\"admin\",\"modelId\":2,\"modelName\":\"\",\"modelType\":\"chat\",\"params\":{},\"provider\":\"deepseek\",\"status\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 00:13:10', 37);
INSERT INTO `sys_oper_log` VALUES (107, 'AI模型配置', 3, 'com.smart.web.controller.campus.ScAiController.removeModel()', 'DELETE', 1, 'admin', '研发部门', '/campus/ai/model/2', '127.0.0.1', '内网IP', '[2] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 00:13:13', 17);
INSERT INTO `sys_oper_log` VALUES (108, '用户管理', 3, 'com.smart.web.controller.system.SysUserController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/user/2', '127.0.0.1', '内网IP', '[2] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 10:16:48', 25);
INSERT INTO `sys_oper_log` VALUES (109, '角色管理', 3, 'com.smart.web.controller.system.SysRoleController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/role/2', '127.0.0.1', '内网IP', '[2] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 10:17:14', 19);
INSERT INTO `sys_oper_log` VALUES (110, 'AI模型配置', 2, 'com.smart.web.controller.campus.ScAiController.editModel()', 'PUT', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"sk-ea608da4de674be3a85f209dee2d2638\",\"baseUrl\":\"https://api.deepseek.com\",\"bizTypes\":\"\",\"isDefault\":\"1\",\"modelId\":1,\"modelName\":\"deepseek-chat\",\"modelType\":\"chat\",\"params\":{},\"priority\":0,\"provider\":\"deepseek\",\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 12:48:49', 34);
INSERT INTO `sys_oper_log` VALUES (111, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/117', '127.0.0.1', '内网IP', '117 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 13:12:48', 113);
INSERT INTO `sys_oper_log` VALUES (112, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/116', '127.0.0.1', '内网IP', '116 ', '{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}', 0, NULL, '2026-03-12 13:12:49', 5);
INSERT INTO `sys_oper_log` VALUES (113, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1060', '127.0.0.1', '内网IP', '1060 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 13:12:53', 10);
INSERT INTO `sys_oper_log` VALUES (114, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1059', '127.0.0.1', '内网IP', '1059 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 13:12:55', 12);
INSERT INTO `sys_oper_log` VALUES (115, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1058', '127.0.0.1', '内网IP', '1058 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 13:12:56', 9);
INSERT INTO `sys_oper_log` VALUES (116, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1057', '127.0.0.1', '内网IP', '1057 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 13:12:57', 12);
INSERT INTO `sys_oper_log` VALUES (117, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1056', '127.0.0.1', '内网IP', '1056 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 13:12:59', 11);
INSERT INTO `sys_oper_log` VALUES (118, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1055', '127.0.0.1', '内网IP', '1055 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 13:13:00', 10);
INSERT INTO `sys_oper_log` VALUES (119, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/116', '127.0.0.1', '内网IP', '116 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 13:13:02', 9);
INSERT INTO `sys_oper_log` VALUES (120, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/115', '127.0.0.1', '内网IP', '115 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 13:13:03', 24);
INSERT INTO `sys_oper_log` VALUES (121, '菜单管理', 3, 'com.smart.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/3', '127.0.0.1', '内网IP', '3 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 13:13:05', 10);
INSERT INTO `sys_oper_log` VALUES (122, '菜单管理', 2, 'com.smart.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2026-03-10 19:39:27\",\"icon\":\"system\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1,\"menuName\":\"基础设置\",\"menuType\":\"M\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"system\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 13:13:37', 47);
INSERT INTO `sys_oper_log` VALUES (123, '菜单管理', 2, 'com.smart.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2026-03-11 18:32:31\",\"icon\":\"education\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2000,\"menuName\":\"系统设置\",\"menuType\":\"M\",\"orderNum\":10,\"params\":{},\"parentId\":0,\"path\":\"campus\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 13:13:47', 10);
INSERT INTO `sys_oper_log` VALUES (124, 'AI模型配置', 2, 'com.smart.web.controller.campus.ScAiController.editModel()', 'PUT', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"sk-ea608da4de674be3a85f209dee2d2638\",\"baseUrl\":\"https://api.deepseek.com\",\"bizTypes\":\"\",\"isDefault\":\"1\",\"modelCode\":\"deepseek-chat\",\"modelId\":1,\"modelName\":\"deepseek-chat\",\"modelType\":\"chat\",\"params\":{},\"priority\":0,\"provider\":\"deepseek\",\"reasoningModelCode\":\"deepseek-reasoner\",\"status\":\"0\",\"supportReasoning\":\"1\",\"supportStream\":\"1\",\"supportVision\":\"1\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 16:18:16', 25);
INSERT INTO `sys_oper_log` VALUES (125, 'AI模型配置', 2, 'com.smart.web.controller.campus.ScAiController.editModel()', 'PUT', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"sk-ea608da4de674be3a85f209dee2d2638\",\"baseUrl\":\"https://api.deepseek.com\",\"bizTypes\":\"\",\"isDefault\":\"1\",\"modelCode\":\"deepseek-chat\",\"modelId\":1,\"modelName\":\"DeepSeek\",\"modelType\":\"chat\",\"params\":{},\"priority\":0,\"provider\":\"deepseek\",\"reasoningModelCode\":\"deepseek-reasoner\",\"status\":\"0\",\"supportReasoning\":\"1\",\"supportStream\":\"1\",\"supportVision\":\"1\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-12 16:19:06', 28);
INSERT INTO `sys_oper_log` VALUES (126, '通知公告', 3, 'com.smart.web.controller.system.SysNoticeController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/notice/1,2', '127.0.0.1', '内网IP', '[1,2] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-15 13:58:45', 16);
INSERT INTO `sys_oper_log` VALUES (127, '通知公告', 1, 'com.smart.web.controller.system.SysNoticeController.add()', 'POST', 1, 'admin', '研发部门', '/system/notice', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"noticeContent\":\"<p>1</p>\",\"noticeTitle\":\"测试\",\"noticeType\":\"1\",\"params\":{},\"status\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-15 13:58:53', 12);
INSERT INTO `sys_oper_log` VALUES (128, '菜单管理', 2, 'com.smart.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"\",\"createTime\":\"2026-03-11 18:32:31\",\"icon\":\"ri-ai-generate-2\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2006,\"menuName\":\"智能测评\",\"menuType\":\"M\",\"orderNum\":6,\"params\":{},\"parentId\":2000,\"path\":\"exam\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 10:06:14', 27);
INSERT INTO `sys_oper_log` VALUES (129, 'AI模型配置', 1, 'com.smart.web.controller.campus.ScAiController.addModel()', 'POST', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"sk-LmShZNiiAZCjh4zEJJzUcaUuakE1qDf8kVyfeh23BBKCnITu\",\"baseUrl\":\"https://agentrouter.org/\",\"bizTypes\":\"\",\"createBy\":\"admin\",\"isDefault\":\"0\",\"modelCode\":\"glm-4-plus\",\"modelId\":3,\"modelName\":\"glm-4.6\",\"modelType\":\"chat\",\"params\":{},\"priority\":0,\"provider\":\"zhipu\",\"reasoningModelCode\":\"\",\"status\":\"0\",\"supportReasoning\":\"0\",\"supportStream\":\"1\",\"supportVision\":\"1\",\"visionModelCode\":\"\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 10:23:18', 15);
INSERT INTO `sys_oper_log` VALUES (130, 'AI模型配置', 2, 'com.smart.web.controller.campus.ScAiController.editModel()', 'PUT', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"sk-LmShZNiiAZCjh4zEJJzUcaUuakE1qDf8kVyfeh23BBKCnITu\",\"baseUrl\":\"https://agentrouter.org/\",\"bizTypes\":\"\",\"isDefault\":\"0\",\"modelCode\":\"glm-4.6\",\"modelId\":3,\"modelName\":\"glm-4.6\",\"modelType\":\"chat\",\"params\":{},\"priority\":0,\"provider\":\"custom\",\"reasoningModelCode\":\"\",\"status\":\"0\",\"supportReasoning\":\"0\",\"supportStream\":\"1\",\"supportVision\":\"1\",\"updateBy\":\"admin\",\"visionModelCode\":\"\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 10:23:58', 10);
INSERT INTO `sys_oper_log` VALUES (131, 'AI模型配置', 2, 'com.smart.web.controller.campus.ScAiController.editModel()', 'PUT', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"sk-LmShZNiiAZCjh4zEJJzUcaUuakE1qDf8kVyfeh23BBKCnITu\",\"baseUrl\":\"https://agentrouter.org/v1\",\"bizTypes\":\"\",\"isDefault\":\"0\",\"modelCode\":\"glm-4.6\",\"modelId\":3,\"modelName\":\"glm-4.6\",\"modelType\":\"chat\",\"params\":{},\"priority\":0,\"provider\":\"custom\",\"reasoningModelCode\":\"\",\"status\":\"0\",\"supportReasoning\":\"0\",\"supportStream\":\"1\",\"supportVision\":\"1\",\"updateBy\":\"admin\",\"visionModelCode\":\"\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 10:24:12', 8);
INSERT INTO `sys_oper_log` VALUES (132, 'AI模型配置', 2, 'com.smart.web.controller.campus.ScAiController.editModel()', 'PUT', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"sk-LmShZNiiAZCjh4zEJJzUcaUuakE1qDf8kVyfeh23BBKCnITu\",\"baseUrl\":\"https://agentrouter.org\",\"bizTypes\":\"\",\"isDefault\":\"0\",\"modelCode\":\"glm-4.6\",\"modelId\":3,\"modelName\":\"glm-4.6\",\"modelType\":\"chat\",\"params\":{},\"priority\":0,\"provider\":\"custom\",\"reasoningModelCode\":\"\",\"status\":\"0\",\"supportReasoning\":\"0\",\"supportStream\":\"1\",\"supportVision\":\"1\",\"updateBy\":\"admin\",\"visionModelCode\":\"\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 10:24:18', 8);
INSERT INTO `sys_oper_log` VALUES (133, 'AI模型配置', 2, 'com.smart.web.controller.campus.ScAiController.editModel()', 'PUT', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"sk-LmShZNiiAZCjh4zEJJzUcaUuakE1qDf8kVyfeh23BBKCnITu\",\"baseUrl\":\"https://agentrouter.org/v1\",\"bizTypes\":\"\",\"isDefault\":\"0\",\"modelCode\":\"glm-4.6\",\"modelId\":3,\"modelName\":\"glm-4.6\",\"modelType\":\"chat\",\"params\":{},\"priority\":0,\"provider\":\"custom\",\"reasoningModelCode\":\"\",\"status\":\"0\",\"supportReasoning\":\"0\",\"supportStream\":\"1\",\"supportVision\":\"1\",\"updateBy\":\"admin\",\"visionModelCode\":\"\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 10:25:02', 9);
INSERT INTO `sys_oper_log` VALUES (134, 'AI模型配置', 2, 'com.smart.web.controller.campus.ScAiController.editModel()', 'PUT', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"sk-f6327c2e2d2b484b88f0a87e626d87ea\",\"baseUrl\":\"https://dashscope.aliyuncs.com/compatible-mode/v1\",\"bizTypes\":\"\",\"isDefault\":\"0\",\"modelCode\":\"glm-5\",\"modelId\":3,\"modelName\":\"glm-5\",\"modelType\":\"chat\",\"params\":{},\"priority\":0,\"provider\":\"custom\",\"reasoningModelCode\":\"\",\"status\":\"0\",\"supportReasoning\":\"0\",\"supportStream\":\"1\",\"supportVision\":\"1\",\"updateBy\":\"admin\",\"visionModelCode\":\"\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 10:28:28', 9);
INSERT INTO `sys_oper_log` VALUES (135, 'AI模型配置', 3, 'com.smart.web.controller.campus.ScAiController.removeModel()', 'DELETE', 1, 'admin', '研发部门', '/campus/ai/model/3', '127.0.0.1', '内网IP', '[3] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 10:46:48', 15);
INSERT INTO `sys_oper_log` VALUES (136, 'AI模型配置', 1, 'com.smart.web.controller.campus.ScAiController.addModel()', 'POST', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"https://bailian.console.aliyun.com/cn-beijing/?spm=5176.29597918.J_SEsSjsNv72yRuRFS2VknO.2.5809133ccmEiAr&tab=doc&accounttraceid=d1118cd8558d453ebed505dbb9ff13canivd#/doc\",\"baseUrl\":\"https://dashscope.aliyuncs.com/compatible-mode/v1\",\"bizTypes\":\"\",\"createBy\":\"admin\",\"isDefault\":\"0\",\"modelCode\":\"qwen3.5-plus\",\"modelId\":4,\"modelName\":\"阿里百炼 Qwen\",\"modelType\":\"chat\",\"params\":{},\"priority\":0,\"provider\":\"qwen\",\"reasoningModelCode\":\"\",\"status\":\"0\",\"supportReasoning\":\"1\",\"supportStream\":\"1\",\"supportVision\":\"1\",\"visionModelCode\":\"\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 11:02:52', 16);
INSERT INTO `sys_oper_log` VALUES (137, 'AI模型配置', 2, 'com.smart.web.controller.campus.ScAiController.editModel()', 'PUT', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"sk-ea608da4de674be3a85f209dee2d2638\",\"baseUrl\":\"https://api.deepseek.com\",\"bizTypes\":\"\",\"isDefault\":\"1\",\"modelCode\":\"deepseek-chat\",\"modelId\":1,\"modelName\":\"DeepSeek\",\"modelType\":\"chat\",\"params\":{},\"priority\":0,\"provider\":\"deepseek\",\"reasoningModelCode\":\"deepseek-reasoner\",\"status\":\"0\",\"supportReasoning\":\"1\",\"supportStream\":\"1\",\"supportVision\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 11:03:00', 13);
INSERT INTO `sys_oper_log` VALUES (138, 'AI模型配置', 2, 'com.smart.web.controller.campus.ScAiController.editModel()', 'PUT', 1, 'admin', '研发部门', '/campus/ai/model', '127.0.0.1', '内网IP', '{\"apiKey\":\"sk-f6327c2e2d2b484b88f0a87e626d87ea\",\"baseUrl\":\"https://dashscope.aliyuncs.com/compatible-mode/v1\",\"bizTypes\":\"\",\"isDefault\":\"0\",\"modelCode\":\"qwen3.5-plus\",\"modelId\":4,\"modelName\":\"阿里百炼 Qwen\",\"modelType\":\"chat\",\"params\":{},\"priority\":0,\"provider\":\"qwen\",\"reasoningModelCode\":\"\",\"status\":\"0\",\"supportReasoning\":\"1\",\"supportStream\":\"1\",\"supportVision\":\"1\",\"updateBy\":\"admin\",\"visionModelCode\":\"\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 11:03:19', 8);
INSERT INTO `sys_oper_log` VALUES (139, '年级管理', 1, 'com.smart.web.controller.campus.ScGradeController.add()', 'POST', 1, 'admin', '研发部门', '/campus/grade', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"gradeId\":1,\"gradeName\":\"2024级\",\"params\":{},\"remark\":\"\",\"schoolYear\":\"2025-2026\",\"status\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:25:45', 17);
INSERT INTO `sys_oper_log` VALUES (140, '用户管理', 1, 'com.smart.web.controller.system.SysUserController.add()', 'POST', 1, 'admin', '研发部门', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"createBy\":\"admin\",\"major\":\"\",\"nickName\":\"zhangteacher\",\"params\":{},\"postIds\":[],\"profileId\":2,\"realName\":\"张老师\",\"roleIds\":[],\"sex\":\"0\",\"status\":\"0\",\"teacherNo\":\"00001\",\"userId\":100,\"userName\":\"张老师\",\"userType\":\"teacher\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:29:55', 93);
INSERT INTO `sys_oper_log` VALUES (141, '部门管理', 3, 'com.smart.web.controller.system.SysDeptController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/dept/101', '127.0.0.1', '内网IP', '101 ', '{\"msg\":\"存在下级部门,不允许删除\",\"code\":601}', 0, NULL, '2026-03-17 14:30:01', 3);
INSERT INTO `sys_oper_log` VALUES (142, '部门管理', 3, 'com.smart.web.controller.system.SysDeptController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/dept/109', '127.0.0.1', '内网IP', '109 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:30:03', 12);
INSERT INTO `sys_oper_log` VALUES (143, '部门管理', 3, 'com.smart.web.controller.system.SysDeptController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/dept/108', '127.0.0.1', '内网IP', '108 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:30:05', 9);
INSERT INTO `sys_oper_log` VALUES (144, '部门管理', 3, 'com.smart.web.controller.system.SysDeptController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/dept/102', '127.0.0.1', '内网IP', '102 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:30:08', 11);
INSERT INTO `sys_oper_log` VALUES (145, '部门管理', 3, 'com.smart.web.controller.system.SysDeptController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/dept/107', '127.0.0.1', '内网IP', '107 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:30:09', 8);
INSERT INTO `sys_oper_log` VALUES (146, '部门管理', 3, 'com.smart.web.controller.system.SysDeptController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/dept/106', '127.0.0.1', '内网IP', '106 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:30:11', 10);
INSERT INTO `sys_oper_log` VALUES (147, '部门管理', 3, 'com.smart.web.controller.system.SysDeptController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/dept/105', '127.0.0.1', '内网IP', '105 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:30:12', 10);
INSERT INTO `sys_oper_log` VALUES (148, '部门管理', 3, 'com.smart.web.controller.system.SysDeptController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/dept/104', '127.0.0.1', '内网IP', '104 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:30:14', 9);
INSERT INTO `sys_oper_log` VALUES (149, '部门管理', 3, 'com.smart.web.controller.system.SysDeptController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/dept/103', '127.0.0.1', '内网IP', '103 ', '{\"msg\":\"部门存在用户,不允许删除\",\"code\":601}', 0, NULL, '2026-03-17 14:30:15', 3);
INSERT INTO `sys_oper_log` VALUES (150, '用户管理', 2, 'com.smart.web.controller.system.SysUserController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":true,\"avatar\":\"\",\"createBy\":\"admin\",\"createTime\":\"2026-03-10 19:39:27\",\"delFlag\":\"0\",\"dept\":{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":103,\"deptName\":\"研发部门\",\"leader\":\"若依\",\"orderNum\":1,\"params\":{},\"parentId\":101,\"status\":\"0\"},\"email\":\"\",\"interestTags\":\"\",\"learningStyle\":\"\",\"loginDate\":\"2026-03-17 13:22:17\",\"loginIp\":\"127.0.0.1\",\"nickName\":\"程安宁\",\"params\":{},\"phonenumber\":\"18955952774\",\"postIds\":[],\"profileId\":1,\"pwdUpdateDate\":\"2026-03-10 19:39:27\",\"realName\":\"程安宁\",\"remark\":\"管理员\",\"roleIds\":[1],\"roles\":[{\"admin\":true,\"dataScope\":\"1\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"params\":{},\"roleId\":1,\"roleKey\":\"admin\",\"roleName\":\"超级管理员\",\"roleSort\":1,\"status\":\"0\"}],\"sex\":\"0\",\"status\":\"0\",\"userId\":1,\"userName\":\"admin\",\"userType\":\"admin\"} ', NULL, 1, '不允许操作超级管理员用户', '2026-03-17 14:30:38', 7);
INSERT INTO `sys_oper_log` VALUES (151, '部门管理', 2, 'com.smart.web.controller.system.SysDeptController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/dept', '127.0.0.1', '内网IP', '{\"ancestors\":\"0\",\"children\":[],\"deptId\":100,\"deptName\":\"校园智学\",\"email\":\"\",\"leader\":\"\",\"orderNum\":0,\"params\":{},\"parentId\":0,\"phone\":\"\",\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:30:59', 13);
INSERT INTO `sys_oper_log` VALUES (152, '部门管理', 2, 'com.smart.web.controller.system.SysDeptController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/dept', '127.0.0.1', '内网IP', '{\"ancestors\":\"0,100\",\"children\":[],\"deptId\":101,\"deptName\":\"智学总公司\",\"email\":\"\",\"leader\":\"\",\"orderNum\":1,\"params\":{},\"parentId\":100,\"parentName\":\"校园智学\",\"phone\":\"\",\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:31:26', 21);
INSERT INTO `sys_oper_log` VALUES (153, '部门管理', 2, 'com.smart.web.controller.system.SysDeptController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/dept', '127.0.0.1', '内网IP', '{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":103,\"deptName\":\"总部门\",\"email\":\"\",\"leader\":\"\",\"orderNum\":1,\"params\":{},\"parentId\":101,\"parentName\":\"智学总公司\",\"phone\":\"\",\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:31:40', 12);
INSERT INTO `sys_oper_log` VALUES (154, '岗位管理', 3, 'com.smart.web.controller.system.SysPostController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/post/4', '127.0.0.1', '内网IP', '[4] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:31:45', 10);
INSERT INTO `sys_oper_log` VALUES (155, '岗位管理', 3, 'com.smart.web.controller.system.SysPostController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/post/3', '127.0.0.1', '内网IP', '[3] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:31:46', 11);
INSERT INTO `sys_oper_log` VALUES (156, '岗位管理', 3, 'com.smart.web.controller.system.SysPostController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/post/2', '127.0.0.1', '内网IP', '[2] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:31:48', 11);
INSERT INTO `sys_oper_log` VALUES (157, '岗位管理', 2, 'com.smart.web.controller.system.SysPostController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/post', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2026-03-10 19:39:27\",\"flag\":false,\"params\":{},\"postCode\":\"manager\",\"postId\":1,\"postName\":\"管理员\",\"postSort\":1,\"remark\":\"\",\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:31:58', 15);
INSERT INTO `sys_oper_log` VALUES (158, '用户管理', 3, 'com.smart.web.controller.system.SysUserController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/user/100', '127.0.0.1', '内网IP', '[100] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:32:26', 8);
INSERT INTO `sys_oper_log` VALUES (159, '用户管理', 1, 'com.smart.web.controller.system.SysUserController.add()', 'POST', 1, 'admin', '研发部门', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"createBy\":\"admin\",\"nickName\":\"张老师\",\"params\":{},\"postIds\":[],\"profileId\":3,\"realName\":\"张老师\",\"roleIds\":[],\"sex\":\"0\",\"status\":\"0\",\"teacherNo\":\"000001\",\"userId\":101,\"userName\":\"zhangteacher\",\"userType\":\"teacher\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:32:47', 72);
INSERT INTO `sys_oper_log` VALUES (160, '班级管理', 1, 'com.smart.web.controller.campus.ScClassController.add()', 'POST', 1, 'admin', '研发部门', '/campus/class', '127.0.0.1', '内网IP', '{\"classId\":1,\"className\":\"计算机应用技术班\",\"createBy\":\"admin\",\"gradeId\":1,\"headTeacherId\":101,\"params\":{},\"remark\":\"\",\"status\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:33:03', 13);
INSERT INTO `sys_oper_log` VALUES (161, '课程管理', 1, 'com.smart.web.controller.campus.ScCourseController.add()', 'POST', 1, 'admin', '研发部门', '/campus/course', '127.0.0.1', '内网IP', '{\"courseCode\":\"0503B204\",\"courseId\":1,\"courseName\":\"Java EE企业级开发\",\"createBy\":\"admin\",\"gradeId\":1,\"intro\":\"\",\"params\":{},\"status\":\"0\",\"subjectType\":\"理论\",\"teacherId\":101} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:34:38', 13);
INSERT INTO `sys_oper_log` VALUES (162, '年级管理', 3, 'com.smart.web.controller.campus.ScGradeController.remove()', 'DELETE', 1, 'admin', '研发部门', '/campus/grade/1', '127.0.0.1', '内网IP', '[1] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:47:46', 8);
INSERT INTO `sys_oper_log` VALUES (163, '班级管理', 3, 'com.smart.web.controller.campus.ScClassController.remove()', 'DELETE', 1, 'admin', '研发部门', '/campus/class/1', '127.0.0.1', '内网IP', '[1] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:47:50', 5);
INSERT INTO `sys_oper_log` VALUES (164, '课程管理', 3, 'com.smart.web.controller.campus.ScCourseController.remove()', 'DELETE', 1, 'admin', '研发部门', '/campus/course/1', '127.0.0.1', '内网IP', '[1] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 14:47:54', 8);
INSERT INTO `sys_oper_log` VALUES (165, '参数管理', 2, 'com.smart.web.controller.system.SysConfigController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/config', '127.0.0.1', '内网IP', '{\"configId\":5,\"configKey\":\"sys.account.registerUser\",\"configName\":\"账号自助-是否开启用户注册功能\",\"configType\":\"Y\",\"configValue\":\"true\",\"createBy\":\"admin\",\"createTime\":\"2026-03-10 19:39:28\",\"params\":{},\"remark\":\"是否开启注册用户功能（true开启，false关闭）\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 15:07:55', 15);
INSERT INTO `sys_oper_log` VALUES (166, '用户管理', 2, 'com.smart.web.controller.system.SysUserController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"admissionYear\":2024,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-17 15:08:02\",\"delFlag\":\"0\",\"email\":\"\",\"learningGoal\":\"\",\"loginDate\":\"2026-03-17 15:59:51\",\"loginIp\":\"127.0.0.1\",\"major\":\"\",\"nickName\":\"张三\",\"params\":{},\"phonenumber\":\"18654141553\",\"postIds\":[],\"profileId\":4,\"pwdUpdateDate\":\"2026-03-17 15:08:03\",\"realName\":\"张三\",\"roleIds\":[100],\"roles\":[],\"sex\":\"0\",\"status\":\"0\",\"studentNo\":\"2401036049\",\"teacherNo\":\"\",\"updateBy\":\"admin\",\"userId\":102,\"userName\":\"zhangsan\",\"userType\":\"student\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:08:08', 40);
INSERT INTO `sys_oper_log` VALUES (167, '用户管理', 2, 'com.smart.web.controller.system.SysUserController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"avatar\":\"\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 14:32:47\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"张老师\",\"params\":{},\"phonenumber\":\"\",\"postIds\":[],\"profileId\":3,\"realName\":\"张老师\",\"roleIds\":[101,103],\"roles\":[],\"sex\":\"0\",\"status\":\"0\",\"teacherNo\":\"000001\",\"updateBy\":\"admin\",\"userId\":101,\"userName\":\"zhangteacher\",\"userType\":\"teacher\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:08:16', 31);
INSERT INTO `sys_oper_log` VALUES (168, '用户管理', 2, 'com.smart.web.controller.system.SysUserController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"admissionYear\":2024,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-17 15:08:02\",\"delFlag\":\"0\",\"email\":\"\",\"learningGoal\":\"\",\"loginDate\":\"2026-03-17 15:59:51\",\"loginIp\":\"127.0.0.1\",\"major\":\"\",\"nickName\":\"张三\",\"params\":{},\"phonenumber\":\"18654141553\",\"postIds\":[8],\"profileId\":4,\"pwdUpdateDate\":\"2026-03-17 15:08:03\",\"realName\":\"张三\",\"roleIds\":[100],\"roles\":[{\"admin\":false,\"dataScope\":\"5\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"params\":{},\"roleId\":100,\"roleKey\":\"student\",\"roleName\":\"学生\",\"roleSort\":100,\"status\":\"0\"}],\"sex\":\"0\",\"status\":\"0\",\"studentNo\":\"2401036049\",\"teacherNo\":\"\",\"updateBy\":\"admin\",\"userId\":102,\"userName\":\"zhangsan\",\"userType\":\"student\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:09:24', 24);
INSERT INTO `sys_oper_log` VALUES (169, '用户管理', 2, 'com.smart.web.controller.system.SysUserController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"avatar\":\"\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 14:32:47\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"张老师\",\"params\":{},\"phonenumber\":\"\",\"postIds\":[5],\"profileId\":3,\"realName\":\"张老师\",\"roleIds\":[101],\"roles\":[{\"admin\":false,\"dataScope\":\"5\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"params\":{},\"roleId\":101,\"roleKey\":\"teacher\",\"roleName\":\"教师\",\"roleSort\":110,\"status\":\"0\"},{\"admin\":false,\"dataScope\":\"4\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"params\":{},\"roleId\":103,\"roleKey\":\"campus_head_teacher\",\"roleName\":\"班主任\",\"roleSort\":130,\"status\":\"0\"}],\"sex\":\"0\",\"status\":\"0\",\"teacherNo\":\"000001\",\"updateBy\":\"admin\",\"userId\":101,\"userName\":\"zhangteacher\",\"userType\":\"teacher\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:09:28', 16);
INSERT INTO `sys_oper_log` VALUES (170, '用户头像', 2, 'com.smart.web.controller.system.SysProfileController.avatar()', 'POST', 1, 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', '{\"msg\":\"操作成功\",\"imgUrl\":\"/profile/avatar/2026/03/17/50b92bae76c04d358ac421ae7bca7a63.png\",\"code\":200}', 0, NULL, '2026-03-17 16:18:45', 91);
INSERT INTO `sys_oper_log` VALUES (171, '个人信息', 2, 'com.smart.web.controller.system.SysProfileController.updateProfile()', 'PUT', 1, 'admin', '研发部门', '/system/user/profile', '127.0.0.1', '内网IP', '{\"admin\":false,\"email\":\"can20060303@gmail.com\",\"nickName\":\"程安宁\",\"params\":{},\"phonenumber\":\"18955952774\",\"sex\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:18:57', 28);
INSERT INTO `sys_oper_log` VALUES (172, '用户管理', 2, 'com.smart.web.controller.system.SysUserController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":true,\"avatar\":\"/profile/avatar/2026/03/17/50b92bae76c04d358ac421ae7bca7a63.png\",\"createBy\":\"admin\",\"createTime\":\"2026-03-10 19:39:27\",\"delFlag\":\"0\",\"dept\":{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":103,\"deptName\":\"总部门\",\"leader\":\"\",\"orderNum\":1,\"params\":{},\"parentId\":101,\"status\":\"0\"},\"deptId\":301,\"email\":\"can20060303@gmail.com\",\"interestTags\":\"\",\"learningStyle\":\"\",\"loginDate\":\"2026-03-17 09:05:35\",\"loginIp\":\"127.0.0.1\",\"nickName\":\"程安宁\",\"params\":{},\"phonenumber\":\"18955952774\",\"postIds\":[1],\"profileId\":1,\"pwdUpdateDate\":\"2026-03-10 19:39:27\",\"realName\":\"程安宁\",\"remark\":\"管理员\",\"roleIds\":[1],\"roles\":[{\"admin\":true,\"dataScope\":\"1\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"params\":{},\"roleId\":1,\"roleKey\":\"admin\",\"roleName\":\"超级管理员\",\"roleSort\":1,\"status\":\"0\"}],\"sex\":\"0\",\"status\":\"0\",\"userId\":1,\"userName\":\"admin\",\"userType\":\"admin\"} ', NULL, 1, '不允许操作超级管理员用户', '2026-03-17 16:23:35', 2);
INSERT INTO `sys_oper_log` VALUES (173, '部门管理', 3, 'com.smart.web.controller.system.SysDeptController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/dept/103', '127.0.0.1', '内网IP', '103 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:24:31', 10);
INSERT INTO `sys_oper_log` VALUES (174, '部门管理', 3, 'com.smart.web.controller.system.SysDeptController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/dept/101', '127.0.0.1', '内网IP', '101 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:24:34', 10);
INSERT INTO `sys_oper_log` VALUES (175, '学年学期', 1, 'com.smart.web.controller.campus.ScSchoolTermController.add()', 'POST', 1, 'admin', '研发部门', '/campus/schoolTerm', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"endDate\":\"2026-07-12\",\"isCurrent\":\"1\",\"params\":{},\"remark\":\"新学期初始配置，请根据实际教学日历调整起止日期和总周数。\",\"schoolYear\":\"2025-2026\",\"sortOrder\":1,\"startDate\":\"2026-03-05\",\"status\":\"0\",\"termCode\":\"2025-2026-1\",\"termId\":1,\"termName\":\"2025-2026学年第一学期\",\"totalWeeks\":19} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:31:09', 14);
INSERT INTO `sys_oper_log` VALUES (176, '年级管理', 1, 'com.smart.web.controller.campus.ScGradeController.add()', 'POST', 1, 'admin', '研发部门', '/campus/grade', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"gradeId\":2,\"gradeName\":\"2024级\",\"params\":{},\"remark\":\"\",\"schoolYear\":\"2025-2026\",\"status\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:39:09', 12);
INSERT INTO `sys_oper_log` VALUES (177, '用户管理', 2, 'com.smart.web.controller.system.SysUserController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"avatar\":\"\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 14:32:47\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"张老师\",\"params\":{},\"phonenumber\":\"\",\"postIds\":[5],\"profileId\":3,\"realName\":\"张老师\",\"roleIds\":[101,103],\"roles\":[{\"admin\":false,\"dataScope\":\"5\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"params\":{},\"roleId\":101,\"roleKey\":\"teacher\",\"roleName\":\"教师\",\"roleSort\":110,\"status\":\"0\"}],\"sex\":\"0\",\"status\":\"0\",\"teacherNo\":\"000001\",\"updateBy\":\"admin\",\"userId\":101,\"userName\":\"zhangteacher\",\"userType\":\"teacher\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:39:43', 36);
INSERT INTO `sys_oper_log` VALUES (178, '班级管理', 1, 'com.smart.web.controller.campus.ScClassController.add()', 'POST', 1, 'admin', '研发部门', '/campus/class', '127.0.0.1', '内网IP', '{\"classId\":2,\"className\":\"计算机应用技术1班\",\"createBy\":\"admin\",\"gradeId\":2,\"headTeacherId\":101,\"params\":{},\"remark\":\"\",\"status\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:41:13', 10);
INSERT INTO `sys_oper_log` VALUES (179, '班级学生关系', 2, 'com.smart.web.controller.campus.ScClassStudentController.assign()', 'PUT', 1, 'admin', '研发部门', '/campus/classStudent', '127.0.0.1', '内网IP', '{\"classId\":2,\"gradeId\":2,\"params\":{},\"remark\":\"\",\"status\":\"0\",\"userId\":102} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:41:20', 18);
INSERT INTO `sys_oper_log` VALUES (180, '课程管理', 1, 'com.smart.web.controller.campus.ScCourseController.add()', 'POST', 1, 'admin', '研发部门', '/campus/course', '127.0.0.1', '内网IP', '{\"courseCode\":\"05030001\",\"courseId\":2,\"courseName\":\"Python数据分析\",\"createBy\":\"admin\",\"gradeId\":2,\"intro\":\"\",\"params\":{},\"status\":\"0\",\"subjectType\":\"理论\",\"teacherId\":101} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:45:19', 12);
INSERT INTO `sys_oper_log` VALUES (181, '班级课程', 1, 'com.smart.web.controller.campus.ScClassCourseController.add()', 'POST', 1, 'admin', '研发部门', '/campus/classCourse', '127.0.0.1', '内网IP', '{\"classId\":2,\"courseId\":2,\"createBy\":\"admin\",\"id\":1,\"params\":{},\"remark\":\"\",\"status\":\"0\",\"studentLimit\":50,\"teacherId\":101,\"termId\":1,\"weeklyHours\":6} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:46:04', 10);
INSERT INTO `sys_oper_log` VALUES (182, '排课表', 1, 'com.smart.web.controller.campus.ScCourseScheduleController.add()', 'POST', 1, 'admin', '研发部门', '/campus/courseSchedule', '127.0.0.1', '内网IP', '{\"classCourseId\":1,\"classroom\":\"躬行楼512\",\"createBy\":\"admin\",\"endSection\":4,\"params\":{},\"remark\":\"\",\"scheduleId\":1,\"startSection\":1,\"status\":\"0\",\"termId\":1,\"weekDay\":1,\"weeksText\":\"1-16周\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 16:47:17', 114);
INSERT INTO `sys_oper_log` VALUES (183, '课程管理', 2, 'com.smart.web.controller.campus.ScCourseController.edit()', 'PUT', 1, 'admin', '研发部门', '/campus/course', '127.0.0.1', '内网IP', '{\"courseCode\":\"0503B205-004\",\"courseId\":2,\"courseName\":\"Python数据分析\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:45:19\",\"gradeId\":2,\"intro\":\"\",\"params\":{},\"status\":\"0\",\"subjectType\":\"理论\",\"teacherId\":101,\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 17:11:56', 34);
INSERT INTO `sys_oper_log` VALUES (184, '学年学期', 2, 'com.smart.web.controller.campus.ScSchoolTermController.edit()', 'PUT', 1, 'admin', '研发部门', '/campus/schoolTerm', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:31:09\",\"endDate\":\"2026-07-12\",\"isCurrent\":\"1\",\"params\":{},\"remark\":\"新学期初始配置，请根据实际教学日历调整起止日期和总周数。\",\"schoolYear\":\"2025-2026\",\"sortOrder\":1,\"startDate\":\"2026-03-05\",\"status\":\"0\",\"termCode\":\"2025-2026-1\",\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalWeeks\":19,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-17 16:31:09\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 18:16:42', 21);
INSERT INTO `sys_oper_log` VALUES (185, '学年学期', 2, 'com.smart.web.controller.campus.ScSchoolTermController.edit()', 'PUT', 1, 'admin', '研发部门', '/campus/schoolTerm', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:31:09\",\"endDate\":\"2026-07-12\",\"isCurrent\":\"1\",\"params\":{},\"remark\":\"\",\"schoolYear\":\"2025-2026\",\"sortOrder\":1,\"startDate\":\"2026-03-05\",\"status\":\"0\",\"termCode\":\"2025-2026-1\",\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalWeeks\":19,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-17 18:16:42\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 18:18:31', 6);
INSERT INTO `sys_oper_log` VALUES (186, '课程学生关系', 1, 'com.smart.web.controller.campus.ScCourseStudentController.add()', 'POST', 1, 'admin', '研发部门', '/campus/courseStudent', '127.0.0.1', '内网IP', '{\"classId\":2,\"courseId\":2,\"createBy\":\"admin\",\"id\":1,\"joinTime\":\"2026-03-17 18:20:00\",\"params\":{},\"remark\":\"\",\"status\":\"0\",\"studentUserId\":102} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 18:20:00', 18);
INSERT INTO `sys_oper_log` VALUES (187, '参数管理', 3, 'com.smart.web.controller.system.SysConfigController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/config/100', '127.0.0.1', '内网IP', '[100] ', NULL, 1, '内置参数【campus.schedule.printConfigs】不能删除 ', '2026-03-17 18:23:05', 4);
INSERT INTO `sys_oper_log` VALUES (188, '教室管理', 1, 'com.smart.web.controller.campus.ScClassroomController.add()', 'POST', 1, 'admin', '研发部门', '/campus/classroom', '127.0.0.1', '内网IP', '{\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"capacity\":60,\"classroomId\":1,\"classroomName\":\"躬行楼\",\"createBy\":\"admin\",\"params\":{},\"remark\":\"\",\"roomType\":\"机房\",\"sortOrder\":0,\"status\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 18:42:43', 469);
INSERT INTO `sys_oper_log` VALUES (189, '排课表', 2, 'com.smart.web.controller.campus.ScCourseScheduleController.edit()', 'PUT', 1, 'admin', '研发部门', '/campus/courseSchedule', '127.0.0.1', '内网IP', '{\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"classCourseId\":1,\"classroom\":\"躬行楼\",\"classroomId\":1,\"classroomName\":\"躬行楼\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:47:17\",\"endSection\":4,\"params\":{},\"remark\":\"\",\"scheduleId\":1,\"startSection\":1,\"status\":\"0\",\"termId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-17 16:47:17\",\"weekDay\":1,\"weeksText\":\"1-16周\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 18:42:52', 20);
INSERT INTO `sys_oper_log` VALUES (190, '教室管理', 2, 'com.smart.web.controller.campus.ScClassroomController.edit()', 'PUT', 1, 'admin', '研发部门', '/campus/classroom', '127.0.0.1', '内网IP', '{\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"capacity\":60,\"classroomId\":1,\"classroomName\":\"躬行楼512\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 18:42:43\",\"params\":{},\"remark\":\"\",\"roomType\":\"机房\",\"sortOrder\":0,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2026-03-17 18:42:43\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 18:43:06', 12);
INSERT INTO `sys_oper_log` VALUES (191, '学年学期', 2, 'com.smart.web.controller.campus.ScSchoolTermController.edit()', 'PUT', 1, 'admin', '研发部门', '/campus/schoolTerm', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:31:09\",\"endDate\":\"2026-07-12\",\"isCurrent\":\"1\",\"params\":{},\"remark\":\"\",\"schoolYear\":\"2025-2026\",\"sortOrder\":1,\"startDate\":\"2026-03-05\",\"status\":\"0\",\"termCode\":\"2025-2026-1\",\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalWeeks\":19,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-17 18:18:31\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 18:58:09', 17);
INSERT INTO `sys_oper_log` VALUES (192, '排课表', 2, 'com.smart.web.controller.campus.ScCourseScheduleController.edit()', 'PUT', 1, 'admin', '研发部门', '/campus/courseSchedule', '127.0.0.1', '内网IP', '{\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"classCourseId\":1,\"classroom\":\"躬行楼512\",\"classroomId\":1,\"classroomName\":\"躬行楼512\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:47:17\",\"endSection\":4,\"params\":{},\"remark\":\"\",\"scheduleId\":1,\"startSection\":1,\"status\":\"0\",\"termId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-17 18:42:52\",\"weekDay\":1,\"weeksText\":\"1-16周\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 19:19:41', 33);
INSERT INTO `sys_oper_log` VALUES (193, '用户管理', 2, 'com.smart.web.controller.system.SysUserController.edit()', 'PUT', 1, 'admin', '校务中心', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"admissionYear\":2024,\"avatar\":\"\",\"classId\":2,\"createBy\":\"\",\"createTime\":\"2026-03-17 15:08:02\",\"delFlag\":\"0\",\"deptId\":321,\"email\":\"\",\"gradeId\":2,\"learningGoal\":\"\",\"loginDate\":\"2026-03-17 22:21:44\",\"loginIp\":\"127.0.0.1\",\"major\":\"\",\"nickName\":\"张三\",\"params\":{},\"phonenumber\":\"18654141553\",\"postIds\":[8],\"profileId\":4,\"pwdUpdateDate\":\"2026-03-17 15:08:03\",\"realName\":\"张三\",\"remark\":\"\",\"roleIds\":[100],\"roles\":[{\"admin\":false,\"dataScope\":\"5\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"params\":{},\"roleId\":100,\"roleKey\":\"student\",\"roleName\":\"学生\",\"roleSort\":100,\"status\":\"0\"}],\"sex\":\"0\",\"status\":\"0\",\"studentNo\":\"2401036049\",\"teacherNo\":\"\",\"updateBy\":\"admin\",\"userId\":102,\"userName\":\"zhangsan\",\"userType\":\"student\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 23:09:46', 58);
INSERT INTO `sys_oper_log` VALUES (194, '用户管理', 2, 'com.smart.web.controller.system.SysUserController.edit()', 'PUT', 1, 'admin', '校务中心', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"admissionYear\":2024,\"avatar\":\"\",\"classId\":2,\"createBy\":\"\",\"createTime\":\"2026-03-17 15:08:02\",\"delFlag\":\"0\",\"dept\":{\"ancestors\":\"0,300,302,310\",\"children\":[],\"deptId\":321,\"deptName\":\"电子信息工程学院\",\"leader\":\"学院负责人\",\"orderNum\":2,\"params\":{},\"parentId\":310,\"status\":\"0\"},\"deptId\":321,\"email\":\"\",\"gradeId\":2,\"learningGoal\":\"\",\"loginDate\":\"2026-03-17 23:10:56\",\"loginIp\":\"127.0.0.1\",\"major\":\"\",\"nickName\":\"张三\",\"params\":{},\"phonenumber\":\"18654141553\",\"postIds\":[8],\"profileId\":4,\"pwdUpdateDate\":\"2026-03-17 15:08:03\",\"realName\":\"张三\",\"remark\":\"\",\"roleIds\":[100],\"roles\":[{\"admin\":false,\"dataScope\":\"5\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"params\":{},\"roleId\":100,\"roleKey\":\"student\",\"roleName\":\"学生\",\"roleSort\":100,\"status\":\"0\"}],\"sex\":\"0\",\"status\":\"0\",\"studentNo\":\"2401036049\",\"teacherNo\":\"\",\"updateBy\":\"admin\",\"userId\":102,\"userName\":\"zhangsan\",\"userType\":\"student\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 23:27:30', 31);
INSERT INTO `sys_oper_log` VALUES (195, '用户管理', 2, 'com.smart.web.controller.system.SysUserController.edit()', 'PUT', 1, 'admin', '校务中心', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"admissionYear\":2024,\"avatar\":\"\",\"classId\":2,\"createBy\":\"\",\"createTime\":\"2026-03-17 15:08:02\",\"delFlag\":\"0\",\"dept\":{\"ancestors\":\"0,300,302,310\",\"children\":[],\"deptId\":321,\"deptName\":\"电子信息工程学院\",\"leader\":\"学院负责人\",\"orderNum\":2,\"params\":{},\"parentId\":310,\"status\":\"0\"},\"deptId\":321,\"email\":\"\",\"gradeId\":2,\"learningGoal\":\"\",\"loginDate\":\"2026-03-17 23:10:56\",\"loginIp\":\"127.0.0.1\",\"major\":\"\",\"nickName\":\"张三\",\"params\":{},\"phonenumber\":\"18654141553\",\"postIds\":[8],\"profileId\":4,\"pwdUpdateDate\":\"2026-03-17 15:08:03\",\"realName\":\"张三\",\"remark\":\"\",\"roleIds\":[100],\"roles\":[{\"admin\":false,\"dataScope\":\"5\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"params\":{},\"roleId\":100,\"roleKey\":\"student\",\"roleName\":\"学生\",\"roleSort\":100,\"status\":\"0\"}],\"sex\":\"0\",\"status\":\"0\",\"studentNo\":\"2401036049\",\"teacherNo\":\"\",\"updateBy\":\"admin\",\"userId\":102,\"userName\":\"zhangsan\",\"userType\":\"student\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 23:27:56', 16);
INSERT INTO `sys_oper_log` VALUES (196, '用户管理', 2, 'com.smart.web.controller.system.SysUserController.edit()', 'PUT', 1, 'admin', '校务中心', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"admissionYear\":2024,\"avatar\":\"\",\"classId\":2,\"createBy\":\"\",\"createTime\":\"2026-03-17 15:08:02\",\"delFlag\":\"0\",\"dept\":{\"ancestors\":\"0,300,302,310\",\"children\":[],\"deptId\":321,\"deptName\":\"电子信息工程学院\",\"leader\":\"学院负责人\",\"orderNum\":2,\"params\":{},\"parentId\":310,\"status\":\"0\"},\"deptId\":321,\"email\":\"\",\"gradeId\":2,\"learningGoal\":\"\",\"loginDate\":\"2026-03-17 23:10:56\",\"loginIp\":\"127.0.0.1\",\"major\":\"计算机应用技术\",\"nickName\":\"张三\",\"params\":{},\"phonenumber\":\"18654141553\",\"postIds\":[8],\"profileId\":4,\"pwdUpdateDate\":\"2026-03-17 15:08:03\",\"realName\":\"张三\",\"remark\":\"\",\"roleIds\":[100],\"roles\":[{\"admin\":false,\"dataScope\":\"5\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"params\":{},\"roleId\":100,\"roleKey\":\"student\",\"roleName\":\"学生\",\"roleSort\":100,\"status\":\"0\"}],\"sex\":\"0\",\"status\":\"0\",\"studentNo\":\"2401036049\",\"teacherNo\":\"\",\"updateBy\":\"admin\",\"userId\":102,\"userName\":\"zhangsan\",\"userType\":\"student\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-17 23:36:26', 16);
INSERT INTO `sys_oper_log` VALUES (197, '班级课程', 2, 'com.smart.web.controller.campus.ScClassCourseController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/classCourse', '127.0.0.1', '内网IP', '{\"actualStudentCount\":1,\"arrangedHours\":90,\"assessmentType\":\"考查\",\"businessType\":\"专科\",\"campusName\":\"本部\",\"classId\":2,\"className\":\"计算机应用技术1班\",\"courseCategory\":\"理论\",\"courseId\":2,\"courseLevelRequirement\":\"无\",\"courseName\":\"Python数据分析\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:46:04\",\"credits\":3,\"id\":1,\"major\":\"计算机应用技术\",\"params\":{},\"prerequisiteCourse\":\"无\",\"remark\":\"\",\"requiredFlag\":\"N\",\"requiredWeeks\":15,\"status\":\"0\",\"studentLimit\":50,\"taskType\":\"正常\",\"teacherId\":101,\"teacherName\":\"张老师\",\"teachingClassCode\":\"0503B205-004\",\"teachingLanguage\":\"中文授课\",\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalHours\":90,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-17 23:53:28\",\"weeklyHours\":6} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 00:02:40', 35);
INSERT INTO `sys_oper_log` VALUES (198, '班级课程', 2, 'com.smart.web.controller.campus.ScClassCourseController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/classCourse', '127.0.0.1', '内网IP', '{\"actualStudentCount\":1,\"arrangedHours\":90,\"assessmentType\":\"考查\",\"businessType\":\"专科\",\"campusName\":\"本部\",\"classId\":2,\"className\":\"计算机应用技术1班\",\"courseCategory\":\"理论\",\"courseId\":2,\"courseLevelRequirement\":\"无\",\"courseName\":\"Python数据分析\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:46:04\",\"credits\":3,\"id\":1,\"major\":\"计算机应用技术\",\"openDeptId\":321,\"params\":{},\"prerequisiteCourse\":\"无\",\"remark\":\"\",\"requiredFlag\":\"N\",\"requiredWeeks\":15,\"status\":\"0\",\"studentLimit\":50,\"taskType\":\"正常\",\"teacherId\":101,\"teacherName\":\"张老师\",\"teachingClassCode\":\"0503B205-004\",\"teachingLanguage\":\"中文授课\",\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalHours\":90,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-18 00:02:40\",\"weeklyHours\":6} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 00:08:17', 10);
INSERT INTO `sys_oper_log` VALUES (199, '班级课程', 2, 'com.smart.web.controller.campus.ScClassCourseController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/classCourse', '127.0.0.1', '内网IP', '{\"actualStudentCount\":1,\"arrangedHours\":90,\"assessmentType\":\"考查\",\"businessType\":\"专科\",\"campusName\":\"本部\",\"classId\":2,\"className\":\"计算机应用技术1班\",\"courseCategory\":\"理论\",\"courseId\":2,\"courseLevelRequirement\":\"无\",\"courseName\":\"Python数据分析\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:46:04\",\"credits\":3,\"id\":1,\"major\":\"计算机应用技术\",\"openDeptId\":321,\"openDeptName\":\"电子信息工程学院\",\"params\":{},\"prerequisiteCourse\":\"无\",\"remark\":\"\",\"requiredFlag\":\"N\",\"requiredWeeks\":15,\"status\":\"0\",\"studentLimit\":50,\"taskType\":\"正常\",\"teacherId\":101,\"teacherName\":\"张老师\",\"teachingClassCode\":\"0503B405-004\",\"teachingLanguage\":\"中文授课\",\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalHours\":90,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-18 00:08:17\",\"weeklyHours\":6} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 00:10:06', 12);
INSERT INTO `sys_oper_log` VALUES (200, '课程管理', 2, 'com.smart.web.controller.campus.ScCourseController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/course', '127.0.0.1', '内网IP', '{\"courseCode\":\"0503B205\",\"courseId\":2,\"courseName\":\"Python数据分析\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:45:19\",\"gradeId\":2,\"intro\":\"\",\"params\":{},\"status\":\"0\",\"subjectType\":\"理论\",\"teacherId\":101,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-17 17:11:56\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 00:10:31', 12);
INSERT INTO `sys_oper_log` VALUES (201, '班级课程', 2, 'com.smart.web.controller.campus.ScClassCourseController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/classCourse', '127.0.0.1', '内网IP', '{\"actualStudentCount\":1,\"arrangedHours\":90,\"assessmentType\":\"考查\",\"businessType\":\"专科\",\"campusName\":\"本部\",\"classId\":2,\"className\":\"计算机应用技术1班\",\"courseCategory\":\"理论\",\"courseId\":2,\"courseLevelRequirement\":\"无\",\"courseName\":\"Python数据分析\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:46:04\",\"credits\":3,\"id\":1,\"major\":\"计算机应用技术\",\"openDeptId\":321,\"openDeptName\":\"电子信息工程学院\",\"params\":{},\"prerequisiteCourse\":\"无\",\"remark\":\"\",\"requiredFlag\":\"N\",\"requiredWeeks\":15,\"status\":\"0\",\"studentLimit\":50,\"taskType\":\"正常\",\"teacherId\":101,\"teacherName\":\"张老师\",\"teachingClassCode\":\"0503B205-004\",\"teachingLanguage\":\"中文授课\",\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalHours\":90,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-18 00:10:06\",\"weeklyHours\":6} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 00:10:47', 10);
INSERT INTO `sys_oper_log` VALUES (202, '排课表', 2, 'com.smart.web.controller.campus.ScCourseScheduleController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/courseSchedule', '127.0.0.1', '内网IP', '{\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"classCourseId\":1,\"classroom\":\"躬行楼512\",\"classroomId\":1,\"classroomName\":\"躬行楼512\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:47:17\",\"endSection\":8,\"params\":{},\"remark\":\"\",\"scheduleId\":1,\"startSection\":5,\"status\":\"0\",\"termId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-17 19:19:41\",\"weekDay\":5,\"weeksText\":\"1-15周\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 00:25:28', 22);
INSERT INTO `sys_oper_log` VALUES (203, '排课表', 2, 'com.smart.web.controller.campus.ScCourseScheduleController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/courseSchedule', '127.0.0.1', '内网IP', '{\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"classCourseId\":1,\"classroom\":\"躬行楼512\",\"classroomId\":1,\"classroomName\":\"躬行楼512\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:47:17\",\"endSection\":10,\"params\":{},\"remark\":\"\",\"scheduleId\":1,\"startSection\":7,\"status\":\"0\",\"termId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-18 00:25:28\",\"weekDay\":5,\"weeksText\":\"1-15周\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 00:26:38', 15);
INSERT INTO `sys_oper_log` VALUES (204, '课程管理', 2, 'com.smart.web.controller.campus.ScCourseController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/course', '127.0.0.1', '内网IP', '{\"courseCode\":\"0503B205\",\"courseId\":2,\"courseName\":\"Python数据分析\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:45:19\",\"gradeId\":2,\"intro\":\"\",\"params\":{},\"status\":\"0\",\"subjectType\":\"理论\",\"updateBy\":\"admin\",\"updateTime\":\"2026-03-18 00:10:31\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 13:14:04', 17);
INSERT INTO `sys_oper_log` VALUES (205, '班级课程', 1, 'com.smart.web.controller.campus.ScClassCourseController.add()', 'POST', 1, 'admin', '校务中心', '/campus/classCourse', '127.0.0.1', '内网IP', '{\"arrangedHours\":0,\"assessmentType\":\"考查\",\"businessType\":\"专科\",\"campusName\":\"本部\",\"classId\":2,\"courseCategory\":\"理论\",\"courseId\":2,\"courseLevelRequirement\":\"无\",\"createBy\":\"admin\",\"credits\":0,\"params\":{},\"prerequisiteCourse\":\"无\",\"remark\":\"\",\"requiredFlag\":\"N\",\"requiredWeeks\":15,\"status\":\"0\",\"studentLimit\":0,\"taskType\":\"正常\",\"teacherId\":101,\"teachingClassCode\":\"\",\"teachingLanguage\":\"中文授课\",\"termId\":1,\"totalHours\":0,\"weeklyHours\":0} ', NULL, 1, '班级课程重复：同一班级、课程、学期已存在绑定关系', '2026-03-18 17:34:36', 16);
INSERT INTO `sys_oper_log` VALUES (206, '课程学生关系', 1, 'com.smart.web.controller.campus.ScCourseStudentController.add()', 'POST', 1, 'admin', '校务中心', '/campus/courseStudent', '127.0.0.1', '内网IP', '{\"classId\":2,\"courseId\":2,\"createBy\":\"admin\",\"joinTime\":\"2026-03-18 17:35:16\",\"params\":{},\"remark\":\"\",\"status\":\"0\",\"studentUserId\":102} ', NULL, 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-102\' for key \'sc_course_student.uk_sc_course_student\'\r\n### The error may exist in file [E:\\smart_campus\\smart_campus-system\\target\\classes\\mapper\\system\\ScCourseStudentMapper.xml]\r\n### The error may involve com.smart.system.mapper.ScCourseStudentMapper.insertScCourseStudent-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into sc_course_student(course_id, student_user_id, class_id, join_time, status, remark) values(?, ?, ?, ?, ?, ?)\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-102\' for key \'sc_course_student.uk_sc_course_student\'\n; Duplicate entry \'2-102\' for key \'sc_course_student.uk_sc_course_student\'', '2026-03-18 17:35:16', 95);
INSERT INTO `sys_oper_log` VALUES (207, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"generationCount\":120,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":1,\"arrangedLessons\":[{\"taskId\":\"1-1\",\"termId\":1,\"weekDay\":3,\"startSection\":11,\"endSection\":12,\"classroomId\":1}],\"bestFitnessScore\":132.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":1,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":1,\"unscheduledLessons\":[],\"warnings\":[\"本次自动排课保留了学期内已有排课，仅为剩余课块补排。\",\"自动排课执行人：admin\"]}}', 0, NULL, '2026-03-18 18:25:21', 189);
INSERT INTO `sys_oper_log` VALUES (208, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"generationCount\":120,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":0,\"arrangedLessons\":[],\"bestFitnessScore\":0.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":0,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":0,\"unscheduledLessons\":[],\"warnings\":[\"当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。\"]}}', 0, NULL, '2026-03-18 18:25:31', 10);
INSERT INTO `sys_oper_log` VALUES (209, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"generationCount\":120,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":0,\"arrangedLessons\":[],\"bestFitnessScore\":0.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":0,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":0,\"unscheduledLessons\":[],\"warnings\":[\"当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。\"]}}', 0, NULL, '2026-03-18 18:25:33', 10);
INSERT INTO `sys_oper_log` VALUES (210, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"generationCount\":120,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":0,\"arrangedLessons\":[],\"bestFitnessScore\":0.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":0,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":0,\"unscheduledLessons\":[],\"warnings\":[\"当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。\"]}}', 0, NULL, '2026-03-18 18:25:51', 8);
INSERT INTO `sys_oper_log` VALUES (211, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"generationCount\":120,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":0,\"arrangedLessons\":[],\"bestFitnessScore\":0.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":0,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":0,\"unscheduledLessons\":[],\"warnings\":[\"当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。\"]}}', 0, NULL, '2026-03-18 18:26:24', 9);
INSERT INTO `sys_oper_log` VALUES (212, '排课表', 3, 'com.smart.web.controller.campus.ScCourseScheduleController.remove()', 'DELETE', 1, 'admin', '校务中心', '/campus/courseSchedule/2,1', '127.0.0.1', '内网IP', '[2,1] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 18:31:45', 13);
INSERT INTO `sys_oper_log` VALUES (213, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"generationCount\":120,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":3,\"arrangedLessons\":[{\"taskId\":\"1-1\",\"termId\":1,\"weekDay\":6,\"startSection\":2,\"endSection\":3,\"classroomId\":1},{\"taskId\":\"1-2\",\"termId\":1,\"weekDay\":1,\"startSection\":1,\"endSection\":2,\"classroomId\":1},{\"taskId\":\"1-3\",\"termId\":1,\"weekDay\":2,\"startSection\":9,\"endSection\":10,\"classroomId\":1}],\"bestFitnessScore\":396.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":3,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":3,\"unscheduledLessons\":[],\"warnings\":[\"自动排课执行人：admin\"]}}', 0, NULL, '2026-03-18 18:31:48', 52);
INSERT INTO `sys_oper_log` VALUES (214, '排课表', 3, 'com.smart.web.controller.campus.ScCourseScheduleController.remove()', 'DELETE', 1, 'admin', '校务中心', '/campus/courseSchedule/4,5,3', '127.0.0.1', '内网IP', '[4,5,3] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 18:33:39', 4);
INSERT INTO `sys_oper_log` VALUES (215, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"generationCount\":120,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":3,\"arrangedLessons\":[{\"taskId\":\"1-1\",\"termId\":1,\"weekDay\":5,\"startSection\":3,\"endSection\":4,\"classroomId\":1},{\"taskId\":\"1-2\",\"termId\":1,\"weekDay\":2,\"startSection\":8,\"endSection\":9,\"classroomId\":1},{\"taskId\":\"1-3\",\"termId\":1,\"weekDay\":6,\"startSection\":5,\"endSection\":6,\"classroomId\":1}],\"bestFitnessScore\":396.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":3,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":3,\"unscheduledLessons\":[],\"warnings\":[\"自动排课执行人：admin\"]}}', 0, NULL, '2026-03-18 18:33:40', 45);
INSERT INTO `sys_oper_log` VALUES (216, '参数管理', 9, 'com.smart.web.controller.system.SysConfigController.refreshCache()', 'DELETE', 1, 'admin', '校务中心', '/system/config/refreshCache', '127.0.0.1', '内网IP', '', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 18:37:08', 7);
INSERT INTO `sys_oper_log` VALUES (217, '排课表', 3, 'com.smart.web.controller.campus.ScCourseScheduleController.remove()', 'DELETE', 1, 'admin', '校务中心', '/campus/courseSchedule/7,6,8', '127.0.0.1', '内网IP', '[7,6,8] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 18:37:43', 10);
INSERT INTO `sys_oper_log` VALUES (218, '排课表', 1, 'com.smart.web.controller.campus.ScCourseScheduleController.add()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule', '127.0.0.1', '内网IP', '{\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"classCourseId\":1,\"classroom\":\"躬行楼512\",\"classroomId\":1,\"classroomName\":\"躬行楼512\",\"createBy\":\"admin\",\"endSection\":4,\"params\":{},\"remark\":\"\",\"scheduleId\":9,\"startSection\":1,\"status\":\"0\",\"termId\":1,\"weekDay\":1,\"weeksText\":\"1-15周\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 18:38:05', 17);
INSERT INTO `sys_oper_log` VALUES (219, '排课表', 3, 'com.smart.web.controller.campus.ScCourseScheduleController.remove()', 'DELETE', 1, 'admin', '校务中心', '/campus/courseSchedule/9', '127.0.0.1', '内网IP', '[9] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 19:34:16', 13);
INSERT INTO `sys_oper_log` VALUES (220, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"generationCount\":120,\"items\":[{\"classCourseId\":1,\"maxWeeklySections\":6,\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"weeksText\":\"1-15周\"}],\"mutationRate\":0.12,\"populationSize\":60,\"preferredSessionDurations\":[4,2,1],\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":2,\"arrangedLessons\":[{\"taskId\":\"1-1\",\"termId\":1,\"weekDay\":4,\"startSection\":1,\"endSection\":4,\"classroomId\":1},{\"taskId\":\"1-2\",\"termId\":1,\"weekDay\":5,\"startSection\":9,\"endSection\":10,\"classroomId\":1}],\"bestFitnessScore\":264.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":2,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":2,\"unscheduledLessons\":[],\"warnings\":[\"自动排课执行人：admin\"]}}', 0, NULL, '2026-03-18 19:34:18', 62);
INSERT INTO `sys_oper_log` VALUES (221, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"generationCount\":120,\"items\":[{\"classCourseId\":1,\"maxWeeklySections\":6,\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"weeksText\":\"1-15周\"}],\"mutationRate\":0.12,\"populationSize\":60,\"preferredSessionDurations\":[4,2,1],\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":0,\"arrangedLessons\":[],\"bestFitnessScore\":0.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":0,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":0,\"unscheduledLessons\":[],\"warnings\":[\"当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。\"]}}', 0, NULL, '2026-03-18 19:42:03', 6);
INSERT INTO `sys_oper_log` VALUES (222, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"generationCount\":120,\"items\":[{\"classCourseId\":1,\"maxWeeklySections\":6,\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"weeksText\":\"1-15周\"}],\"mutationRate\":0.12,\"populationSize\":60,\"preferredSessionDurations\":[4,2,1],\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":0,\"arrangedLessons\":[],\"bestFitnessScore\":0.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":0,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":0,\"unscheduledLessons\":[],\"warnings\":[\"当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。\"]}}', 0, NULL, '2026-03-18 19:42:08', 9);
INSERT INTO `sys_oper_log` VALUES (223, '教室管理', 2, 'com.smart.web.controller.campus.ScClassroomController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/classroom', '127.0.0.1', '内网IP', '{\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"capacity\":60,\"classroomId\":1,\"classroomName\":\"躬行楼512\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 18:42:43\",\"params\":{},\"remark\":\"\",\"roomType\":\"机房\",\"sortOrder\":0,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2026-03-17 18:43:06\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 20:20:50', 20);
INSERT INTO `sys_oper_log` VALUES (224, '教室管理', 2, 'com.smart.web.controller.campus.ScClassroomController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/classroom', '127.0.0.1', '内网IP', '{\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"capacity\":60,\"classroomId\":1,\"classroomName\":\"躬行楼512\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 18:42:43\",\"deptId\":321,\"params\":{},\"remark\":\"\",\"roomType\":\"机房\",\"sortOrder\":0,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2026-03-18 20:20:50\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 20:20:55', 9);
INSERT INTO `sys_oper_log` VALUES (225, '班级管理', 2, 'com.smart.web.controller.campus.ScClassController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/class', '127.0.0.1', '内网IP', '{\"classId\":2,\"className\":\"计算机应用技术1班\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 16:41:12\",\"deptId\":321,\"gradeId\":2,\"headTeacherId\":101,\"params\":{},\"remark\":\"\",\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-18 20:40:40', 28);
INSERT INTO `sys_oper_log` VALUES (226, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"generationCount\":120,\"items\":[{\"classCourseId\":1,\"maxWeeklySections\":6,\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"weeksText\":\"1-15周\"}],\"mutationRate\":0.12,\"populationSize\":60,\"preferredSessionDurations\":[4,2,1],\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":0,\"arrangedLessons\":[],\"bestFitnessScore\":0.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":0,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":0,\"unscheduledLessons\":[],\"warnings\":[\"当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。\"]}}', 0, NULL, '2026-03-19 09:19:18', 38);
INSERT INTO `sys_oper_log` VALUES (227, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"generationCount\":120,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":0,\"arrangedLessons\":[],\"bestFitnessScore\":0.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":0,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":0,\"unscheduledLessons\":[],\"warnings\":[\"当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。\"]}}', 0, NULL, '2026-03-19 11:17:25', 33);
INSERT INTO `sys_oper_log` VALUES (228, '班级课程', 1, 'com.smart.web.controller.campus.ScClassCourseController.add()', 'POST', 1, 'admin', '校务中心', '/campus/classCourse', '127.0.0.1', '内网IP', '{\"arrangedHours\":0,\"assessmentType\":\"考查\",\"businessType\":\"专科\",\"campusName\":\"本部\",\"classId\":2,\"courseCategory\":\"理论\",\"courseId\":2,\"courseLevelRequirement\":\"无\",\"createBy\":\"admin\",\"credits\":0,\"openDeptId\":321,\"params\":{},\"prerequisiteCourse\":\"无\",\"remark\":\"\",\"requiredFlag\":\"N\",\"requiredWeeks\":15,\"status\":\"0\",\"studentLimit\":0,\"taskType\":\"正常\",\"teacherId\":101,\"teachingClassCode\":\"TB20261321ZY00200201\",\"teachingLanguage\":\"中文授课\",\"termId\":1,\"totalHours\":0,\"weeklyHours\":0} ', NULL, 1, '班级课程重复：同一班级、课程、学期已存在绑定关系', '2026-03-19 11:20:04', 11);
INSERT INTO `sys_oper_log` VALUES (229, '课程学生关系', 1, 'com.smart.web.controller.campus.ScCourseStudentController.batchAdd()', 'POST', 1, 'admin', '校务中心', '/campus/courseStudent/batch-add', '127.0.0.1', '内网IP', '{\"courseId\":2,\"remark\":\"\",\"status\":\"0\",\"studentUserIds\":[102]} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"successCount\":0,\"skippedCount\":1,\"skippedStudents\":[{\"studentUserId\":102,\"studentName\":\"张三\",\"studentNo\":\"2401036049\",\"courseName\":\"Python数据分析\",\"className\":\"计算机应用技术1班\"}]}}', 0, NULL, '2026-03-19 12:45:06', 15);
INSERT INTO `sys_oper_log` VALUES (230, '教室管理', 2, 'com.smart.web.controller.campus.ScClassroomController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/classroom', '127.0.0.1', '内网IP', '{\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"capacity\":60,\"classroomId\":1,\"classroomName\":\"交换机路由实训\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 18:42:43\",\"deptId\":321,\"deptName\":\"电子信息工程学院\",\"params\":{},\"remark\":\"\",\"roomType\":\"机房\",\"sortOrder\":0,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2026-03-18 20:20:55\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-19 18:35:21', 49);
INSERT INTO `sys_oper_log` VALUES (231, '排课表', 3, 'com.smart.web.controller.campus.ScCourseScheduleController.remove()', 'DELETE', 1, 'admin', '校务中心', '/campus/courseSchedule/10,11', '127.0.0.1', '内网IP', '[10,11] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-19 18:38:11', 27);
INSERT INTO `sys_oper_log` VALUES (232, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"generationCount\":120,\"items\":[{\"classCourseId\":1,\"classroomId\":1,\"maxWeeklySections\":6,\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"weeksText\":\"1-15周\"}],\"mutationRate\":0.12,\"populationSize\":60,\"preferredSessionDurations\":[4,2,1],\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":2,\"arrangedLessons\":[{\"taskId\":\"1-1\",\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"weekDay\":6,\"startSection\":7,\"endSection\":10,\"classroomId\":1,\"classCourseId\":1,\"className\":\"计算机应用技术1班\",\"courseName\":\"Python数据分析\",\"teacherName\":\"张老师\",\"weeksText\":\"1-15周\",\"studentCount\":1,\"courseCategory\":\"理论\",\"classroomName\":\"交换机路由实训\",\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"roomType\":\"机房\",\"capacity\":60,\"reasons\":[\"同部门优先\",\"同校区优先\",\"容量匹配\"]},{\"taskId\":\"1-2\",\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"weekDay\":4,\"startSection\":2,\"endSection\":3,\"classroomId\":1,\"classCourseId\":1,\"className\":\"计算机应用技术1班\",\"courseName\":\"Python数据分析\",\"teacherName\":\"张老师\",\"weeksText\":\"1-15周\",\"studentCount\":1,\"courseCategory\":\"理论\",\"classroomName\":\"交换机路由实训\",\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"roomType\":\"机房\",\"capacity\":60,\"reasons\":[\"同部门优先\",\"同校区优先\",\"容量匹配\"]}],\"bestFitnessScore\":292.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":2,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":2,\"unscheduledLessons\":[],\"warnings\":[\"自动排课执行人：admin\"]}}', 0, NULL, '2026-03-19 18:46:38', 93);
INSERT INTO `sys_oper_log` VALUES (233, '排课表', 2, 'com.smart.web.controller.campus.ScCourseScheduleController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/courseSchedule', '127.0.0.1', '内网IP', '{\"buildingName\":\"B栋\",\"campusName\":\"本部\",\"classCourseId\":1,\"classroom\":\"交换机路由实训\",\"classroomId\":1,\"classroomName\":\"交换机路由实训\",\"createBy\":\"admin\",\"createTime\":\"2026-03-19 18:46:38\",\"endSection\":4,\"params\":{},\"remark\":\"遗传算法自动排课生成\",\"scheduleId\":13,\"startSection\":3,\"status\":\"0\",\"termId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-19 18:46:38\",\"weekDay\":4,\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"weeksText\":\"1-15周\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-19 18:48:58', 22);
INSERT INTO `sys_oper_log` VALUES (234, '课程学生关系', 1, 'com.smart.web.controller.campus.ScCourseStudentController.batchAdd()', 'POST', 1, 'admin', '校务中心', '/campus/courseStudent/batch-add', '127.0.0.1', '内网IP', '{\"classId\":2,\"courseId\":2,\"remark\":\"\",\"status\":\"0\",\"studentUserIds\":[102]} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"successCount\":0,\"skippedCount\":1,\"skippedStudents\":[{\"studentUserId\":102,\"studentName\":\"张三\",\"studentNo\":\"2401036049\",\"courseName\":\"Python数据分析\",\"className\":\"计算机应用技术1班\"}]}}', 0, NULL, '2026-03-20 08:46:17', 10);
INSERT INTO `sys_oper_log` VALUES (235, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"generationCount\":120,\"items\":[{\"classCourseId\":1,\"maxWeeklySections\":6,\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"weeksText\":\"1-15周\"}],\"mutationRate\":0.12,\"populationSize\":60,\"preferredSessionDurations\":[4,2,1],\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":0,\"arrangedLessons\":[],\"bestFitnessScore\":0.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":0,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":0,\"unscheduledLessons\":[],\"warnings\":[\"当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。\"]}}', 0, NULL, '2026-03-20 08:48:15', 31);
INSERT INTO `sys_oper_log` VALUES (236, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"generationCount\":120,\"items\":[{\"classCourseId\":1,\"maxWeeklySections\":6,\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"weeksText\":\"1-15周\"}],\"mutationRate\":0.12,\"populationSize\":60,\"preferredSessionDurations\":[4,2,1],\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":0,\"arrangedLessons\":[],\"bestFitnessScore\":0.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":0,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":0,\"unscheduledLessons\":[],\"warnings\":[\"当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。\"]}}', 0, NULL, '2026-03-20 09:01:10', 56);
INSERT INTO `sys_oper_log` VALUES (237, '字典数据', 2, 'com.smart.web.controller.system.SysDictDataController.edit()', 'PUT', 1, 'admin', '校务中心', '/system/dict/data', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2026-03-20 09:33:10\",\"cssClass\":\"\",\"default\":false,\"dictCode\":152,\"dictLabel\":\"躬行楼\",\"dictSort\":4,\"dictType\":\"campus_building_type\",\"dictValue\":\"躬行楼\",\"isDefault\":\"N\",\"listClass\":\"default\",\"params\":{},\"remark\":\"教学楼字典\",\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-20 09:41:24', 23);
INSERT INTO `sys_oper_log` VALUES (238, '字典数据', 2, 'com.smart.web.controller.system.SysDictDataController.edit()', 'PUT', 1, 'admin', '校务中心', '/system/dict/data', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2026-03-20 09:33:09\",\"cssClass\":\"\",\"default\":true,\"dictCode\":149,\"dictLabel\":\"知行楼\",\"dictSort\":1,\"dictType\":\"campus_building_type\",\"dictValue\":\"知行楼\",\"isDefault\":\"Y\",\"listClass\":\"default\",\"params\":{},\"remark\":\"默认教学楼\",\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-20 09:41:40', 10);
INSERT INTO `sys_oper_log` VALUES (239, '字典数据', 2, 'com.smart.web.controller.system.SysDictDataController.edit()', 'PUT', 1, 'admin', '校务中心', '/system/dict/data', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2026-03-20 09:33:09\",\"cssClass\":\"\",\"default\":false,\"dictCode\":150,\"dictLabel\":\"善学楼\",\"dictSort\":2,\"dictType\":\"campus_building_type\",\"dictValue\":\"善学楼\",\"isDefault\":\"N\",\"listClass\":\"default\",\"params\":{},\"remark\":\"教学楼字典\",\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-20 09:41:50', 12);
INSERT INTO `sys_oper_log` VALUES (240, '字典数据', 2, 'com.smart.web.controller.system.SysDictDataController.edit()', 'PUT', 1, 'admin', '校务中心', '/system/dict/data', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2026-03-20 09:33:09\",\"cssClass\":\"\",\"default\":false,\"dictCode\":151,\"dictLabel\":\"笃行楼\",\"dictSort\":3,\"dictType\":\"campus_building_type\",\"dictValue\":\"笃行楼\",\"isDefault\":\"N\",\"listClass\":\"warning\",\"params\":{},\"remark\":\"教学楼字典\",\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-20 09:42:10', 12);
INSERT INTO `sys_oper_log` VALUES (241, '字典数据', 2, 'com.smart.web.controller.system.SysDictDataController.edit()', 'PUT', 1, 'admin', '校务中心', '/system/dict/data', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2026-03-20 09:33:09\",\"cssClass\":\"\",\"default\":false,\"dictCode\":151,\"dictLabel\":\"笃行楼\",\"dictSort\":3,\"dictType\":\"campus_building_type\",\"dictValue\":\"笃行楼\",\"isDefault\":\"N\",\"listClass\":\"default\",\"params\":{},\"remark\":\"教学楼字典\",\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-20 09:42:15', 10);
INSERT INTO `sys_oper_log` VALUES (242, '教室管理', 2, 'com.smart.web.controller.campus.ScClassroomController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/classroom', '127.0.0.1', '内网IP', '{\"buildingName\":\"躬行楼\",\"campusName\":\"本部\",\"capacity\":60,\"classroomId\":1,\"classroomName\":\"交换机路由实训\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 18:42:43\",\"deptId\":321,\"deptName\":\"电子信息工程学院\",\"params\":{},\"remark\":\"\",\"roomType\":\"机房\",\"sortOrder\":0,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2026-03-19 18:35:21\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-20 09:42:28', 22);
INSERT INTO `sys_oper_log` VALUES (243, '排课表', 3, 'com.smart.web.controller.campus.ScCourseScheduleController.remove()', 'DELETE', 1, 'admin', '校务中心', '/campus/courseSchedule/12', '127.0.0.1', '内网IP', '[12] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-20 11:17:34', 19);
INSERT INTO `sys_oper_log` VALUES (244, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"excludedWeekDays\":[6,7],\"generationCount\":120,\"items\":[{\"classCourseId\":1,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"excludedWeekDays\":[6,7],\"maxWeeklySections\":6,\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"weeksText\":\"1-15周\"}],\"mutationRate\":0.12,\"populationSize\":60,\"preferredSessionDurations\":[4,2,1],\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":1,\"arrangedLessons\":[{\"taskId\":\"1-1\",\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"weekDay\":2,\"startSection\":1,\"endSection\":4,\"classroomId\":1,\"classCourseId\":1,\"className\":\"计算机应用技术1班\",\"courseName\":\"Python数据分析\",\"teacherName\":\"张老师\",\"weeksText\":\"1-15周\",\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"studentCount\":1,\"courseCategory\":\"理论\",\"excludedWeekDays\":[6,7],\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"classroomName\":\"交换机路由实训\",\"buildingName\":\"躬行楼\",\"campusName\":\"本部\",\"roomType\":\"机房\",\"capacity\":60,\"reasons\":[\"同部门优先\",\"同校区优先\",\"容量匹配\"]}],\"bestFitnessScore\":146.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":1,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":1,\"unscheduledLessons\":[],\"warnings\":[\"本次自动排课保留了学期内已有排课，仅为剩余课块补排。\",\"自动排课执行人：admin\"]}}', 0, NULL, '2026-03-20 11:17:36', 68);
INSERT INTO `sys_oper_log` VALUES (245, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"excludedWeekDays\":[6,7],\"generationCount\":120,\"items\":[{\"classCourseId\":1,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"excludedWeekDays\":[6,7],\"maxWeeklySections\":6,\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"weeksText\":\"1-15周\"}],\"mutationRate\":0.12,\"populationSize\":60,\"preferredSessionDurations\":[4,2,1],\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":0,\"arrangedLessons\":[],\"bestFitnessScore\":0.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":0,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":0,\"unscheduledLessons\":[],\"warnings\":[\"当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。\"]}}', 0, NULL, '2026-03-20 11:20:36', 11);
INSERT INTO `sys_oper_log` VALUES (246, '试卷管理', 1, 'com.smart.web.controller.campus.ScExamController.addPaperDetail()', 'POST', 1, 'admin', '校务中心', '/campus/exam/paper/detail', '127.0.0.1', '内网IP', '{\"allowReviewAnalysis\":\"1\",\"courseId\":2,\"durationMinutes\":60,\"markingMode\":\"auto_first\",\"paperName\":\"测试试卷\",\"paperType\":\"fixed\",\"passScore\":60,\"publishEndTime\":\"2026-03-21 00:00:00\",\"publishStartTime\":\"2026-03-20 00:00:00\",\"publishStatus\":\"draft\",\"questionOrderMode\":\"all_random\",\"questions\":[{\"questionId\":8,\"score\":25,\"sortNo\":1},{\"questionId\":7,\"score\":25,\"sortNo\":2},{\"questionId\":6,\"score\":25,\"sortNo\":3},{\"questionId\":5,\"score\":25,\"sortNo\":4}],\"remark\":\"\",\"sectionConfigJson\":\"[{\\\"title\\\":\\\"单选题\\\",\\\"questionType\\\":\\\"single\\\",\\\"defaultScore\\\":25,\\\"targetScore\\\":100,\\\"instructions\\\":\\\"每题 5 分，共 4 题\\\",\\\"questionIds\\\":[8,7,6,5]}]\",\"status\":\"0\",\"totalScore\":100} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-20 16:31:42', 114);
INSERT INTO `sys_oper_log` VALUES (247, '试卷管理', 2, 'com.smart.web.controller.campus.ScExamController.editPaperDetail()', 'PUT', 1, 'admin', '校务中心', '/campus/exam/paper/detail', '127.0.0.1', '内网IP', '{\"allowReviewAnalysis\":\"1\",\"courseId\":2,\"durationMinutes\":60,\"markingMode\":\"auto_first\",\"paperId\":1,\"paperName\":\"测试试卷\",\"paperType\":\"fixed\",\"passScore\":60,\"publishEndTime\":\"2026-03-21 00:00:00\",\"publishStartTime\":\"2026-03-20 08:33:53\",\"publishStatus\":\"published\",\"questionOrderMode\":\"all_random\",\"questions\":[{\"id\":1,\"questionId\":8,\"score\":25,\"sortNo\":1},{\"id\":2,\"questionId\":7,\"score\":25,\"sortNo\":2},{\"id\":3,\"questionId\":6,\"score\":25,\"sortNo\":3},{\"id\":4,\"questionId\":5,\"score\":25,\"sortNo\":4}],\"sectionConfigJson\":\"[{\\\"title\\\":\\\"单选题\\\",\\\"questionType\\\":\\\"single\\\",\\\"defaultScore\\\":25,\\\"targetScore\\\":100,\\\"instructions\\\":\\\"每题 5 分，共 4 题\\\",\\\"questionIds\\\":[8,7,6,5]}]\",\"status\":\"0\",\"totalScore\":100} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-20 16:33:53', 44);
INSERT INTO `sys_oper_log` VALUES (248, '试卷管理', 2, 'com.smart.web.controller.campus.ScExamController.editPaperDetail()', 'PUT', 1, 'admin', '校务中心', '/campus/exam/paper/detail', '127.0.0.1', '内网IP', '{\"allowReviewAnalysis\":\"1\",\"courseId\":2,\"durationMinutes\":60,\"markingMode\":\"auto_first\",\"paperId\":1,\"paperName\":\"测试试卷\",\"paperType\":\"fixed\",\"passScore\":60,\"publishEndTime\":\"2026-03-21 00:00:00\",\"publishStartTime\":\"2026-03-20 08:36:05\",\"publishStatus\":\"published\",\"questionOrderMode\":\"all_random\",\"questions\":[{\"id\":5,\"questionId\":8,\"score\":25,\"sortNo\":1},{\"id\":6,\"questionId\":7,\"score\":25,\"sortNo\":2},{\"id\":7,\"questionId\":6,\"score\":25,\"sortNo\":3},{\"id\":8,\"questionId\":5,\"score\":25,\"sortNo\":4}],\"sectionConfigJson\":\"[{\\\"title\\\":\\\"单选题\\\",\\\"questionType\\\":\\\"single\\\",\\\"defaultScore\\\":25,\\\"targetScore\\\":100,\\\"instructions\\\":\\\"每题 5 分，共 4 题\\\",\\\"questionIds\\\":[8,7,6,5]}]\",\"status\":\"0\",\"totalScore\":100} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-20 16:36:05', 26);
INSERT INTO `sys_oper_log` VALUES (249, '试卷管理', 2, 'com.smart.web.controller.campus.ScExamController.editPaperDetail()', 'PUT', 1, 'admin', '校务中心', '/campus/exam/paper/detail', '127.0.0.1', '内网IP', '{\"allowReviewAnalysis\":\"1\",\"courseId\":2,\"durationMinutes\":60,\"markingMode\":\"auto_first\",\"paperId\":1,\"paperName\":\"测试试卷\",\"paperType\":\"fixed\",\"passScore\":60,\"publishEndTime\":\"2026-03-22 00:00:00\",\"publishStartTime\":\"2026-03-20 08:36:05\",\"publishStatus\":\"published\",\"questionOrderMode\":\"all_random\",\"questions\":[{\"id\":9,\"questionId\":8,\"score\":25,\"sortNo\":1},{\"id\":10,\"questionId\":7,\"score\":25,\"sortNo\":2},{\"id\":11,\"questionId\":6,\"score\":25,\"sortNo\":3},{\"id\":12,\"questionId\":5,\"score\":25,\"sortNo\":4}],\"remark\":\"\",\"sectionConfigJson\":\"[{\\\"title\\\":\\\"单选题\\\",\\\"questionType\\\":\\\"single\\\",\\\"defaultScore\\\":25,\\\"targetScore\\\":100,\\\"instructions\\\":\\\"每题 5 分，共 4 题\\\",\\\"questionIds\\\":[8,7,6,5]}]\",\"status\":\"0\",\"totalScore\":100} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-21 12:42:25', 64);
INSERT INTO `sys_oper_log` VALUES (250, '试卷管理', 2, 'com.smart.web.controller.campus.ScExamController.editPaperDetail()', 'PUT', 1, 'admin', '校务中心', '/campus/exam/paper/detail', '127.0.0.1', '内网IP', '{\"allowReviewAnalysis\":\"1\",\"courseId\":2,\"durationMinutes\":60,\"markingMode\":\"auto_first\",\"paperId\":1,\"paperName\":\"测试试卷\",\"paperType\":\"fixed\",\"passScore\":60,\"publishEndTime\":\"2026-03-22 00:00:00\",\"publishStartTime\":\"2026-03-20 08:36:05\",\"publishStatus\":\"published\",\"questionOrderMode\":\"all_random\",\"questions\":[{\"id\":13,\"questionId\":8,\"score\":25,\"sortNo\":1},{\"id\":14,\"questionId\":7,\"score\":25,\"sortNo\":2},{\"id\":15,\"questionId\":6,\"score\":25,\"sortNo\":3},{\"id\":16,\"questionId\":5,\"score\":25,\"sortNo\":4}],\"remark\":\"{\\\"metaVersion\\\":\\\"paper-workbench-v2\\\",\\\"purposeType\\\":\\\"formal_exam\\\",\\\"attemptLimit\\\":1,\\\"allowPostExamReview\\\":true,\\\"noteText\\\":\\\"\\\",\\\"audience\\\":{\\\"gradeIds\\\":[],\\\"classIds\\\":[]},\\\"schedule\\\":{\\\"forceSubmitOnTimeout\\\":true,\\\"questionTimeLimitSeconds\\\":0,\\\"limitEntry\\\":false,\\\"sessionName\\\":\\\"\\\"},\\\"antiCheat\\\":{\\\"enableMonitor\\\":false,\\\"snapshotEnabled\\\":false,\\\"screenSwitchDetect\\\":false,\\\"lockExamClient\\\":false,\\\"screenSwitchLimit\\\":3,\\\"snapshotIntervalSeconds\\\":30},\\\"advanced\\\":{\\\"allowRedo\\\":false,\\\"redoLimit\\\":0,\\\"showScoreAfterSubmit\\\":true,\\\"reviewRemark\\\":\\\"\\\"},\\\"managementTeacherIds\\\":[],\\\"smartAssemble\\\":{\\\"enabled\\\":false,\\\"sourceScope\\\":\\\"all\\\",\\\"sourceBatchNo\\\":\\\"\\\",\\\"versionCount\\\":3,\\\"repeatRate\\\":25,\\\"autoAssignOnPublish\\\":true,\\\"rules\\\":[{\\\"questionType\\\":\\\"single\\\",\\\"count\\\":10,\\\"difficultyLevel\\\":3,\\\"score\\\":5}],\\\"generatedVariants\\\":[]}}\",\"sectionConfigJson\":\"[{\\\"title\\\":\\\"单选题\\\",\\\"questionType\\\":\\\"single\\\",\\\"defaultScore\\\":25,\\\"targetScore\\\":100,\\\"instructions\\\":\\\"每题 5 分，共 4 题\\\",\\\"questionIds\\\":[8,7,6,5]}]\",\"status\":\"0\",\"totalScore\":100} ', NULL, 1, '\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'remark\' at row 1\r\n### The error may exist in file [E:\\smart_campus\\smart_campus-system\\target\\classes\\mapper\\system\\ScExamPaperMapper.xml]\r\n### The error may involve com.smart.system.mapper.ScExamPaperMapper.updateScExamPaper-Inline\r\n### The error occurred while setting parameters\r\n### SQL: update sc_exam_paper  SET paper_name = ?,course_id     = ?,paper_type = ?,total_score = ?,duration_minutes = ?,publish_status = ?,publish_start_time = ?,publish_end_time = ?,pass_score = ?,marking_mode = ?,question_order_mode = ?,allow_review_analysis = ?,section_config_json = ?,status = ?,update_by =     ?,remark = ?,update_time = sysdate()      where paper_id = ?\r\n### Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'remark\' at row 1\n; Data truncation: Data too long for column \'remark\' at row 1', '2026-03-21 13:15:24', 165);
INSERT INTO `sys_oper_log` VALUES (251, '试卷管理', 2, 'com.smart.web.controller.campus.ScExamController.editPaperDetail()', 'PUT', 1, 'admin', '校务中心', '/campus/exam/paper/detail', '127.0.0.1', '内网IP', '{\"allowReviewAnalysis\":\"1\",\"courseId\":2,\"durationMinutes\":60,\"markingMode\":\"auto_first\",\"paperId\":1,\"paperName\":\"测试试卷\",\"paperType\":\"fixed\",\"passScore\":60,\"publishEndTime\":\"2026-03-22 00:00:00\",\"publishStartTime\":\"2026-03-20 08:36:05\",\"publishStatus\":\"published\",\"questionOrderMode\":\"all_random\",\"questions\":[{\"id\":13,\"questionId\":8,\"score\":25,\"sortNo\":1},{\"id\":14,\"questionId\":7,\"score\":25,\"sortNo\":2},{\"id\":15,\"questionId\":6,\"score\":25,\"sortNo\":3},{\"id\":16,\"questionId\":5,\"score\":25,\"sortNo\":4}],\"remark\":\"{\\\"metaVersion\\\":\\\"paper-workbench-v2\\\",\\\"purposeType\\\":\\\"formal_exam\\\",\\\"attemptLimit\\\":1,\\\"allowPostExamReview\\\":true,\\\"noteText\\\":\\\"\\\",\\\"audience\\\":{\\\"gradeIds\\\":[],\\\"classIds\\\":[]},\\\"schedule\\\":{\\\"forceSubmitOnTimeout\\\":true,\\\"questionTimeLimitSeconds\\\":0,\\\"limitEntry\\\":false,\\\"sessionName\\\":\\\"\\\"},\\\"antiCheat\\\":{\\\"enableMonitor\\\":false,\\\"snapshotEnabled\\\":false,\\\"screenSwitchDetect\\\":false,\\\"lockExamClient\\\":false,\\\"screenSwitchLimit\\\":3,\\\"snapshotIntervalSeconds\\\":30},\\\"advanced\\\":{\\\"allowRedo\\\":false,\\\"redoLimit\\\":0,\\\"showScoreAfterSubmit\\\":true,\\\"reviewRemark\\\":\\\"\\\"},\\\"managementTeacherIds\\\":[],\\\"smartAssemble\\\":{\\\"enabled\\\":false,\\\"sourceScope\\\":\\\"all\\\",\\\"sourceBatchNo\\\":\\\"\\\",\\\"versionCount\\\":3,\\\"repeatRate\\\":25,\\\"autoAssignOnPublish\\\":true,\\\"rules\\\":[{\\\"questionType\\\":\\\"single\\\",\\\"count\\\":10,\\\"difficultyLevel\\\":3,\\\"score\\\":5}],\\\"generatedVariants\\\":[]}}\",\"sectionConfigJson\":\"[{\\\"title\\\":\\\"单选题\\\",\\\"questionType\\\":\\\"single\\\",\\\"defaultScore\\\":25,\\\"targetScore\\\":100,\\\"instructions\\\":\\\"每题 5 分，共 4 题\\\",\\\"questionIds\\\":[8,7,6,5]}]\",\"status\":\"0\",\"totalScore\":100} ', NULL, 1, '\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'remark\' at row 1\r\n### The error may exist in file [E:\\smart_campus\\smart_campus-system\\target\\classes\\mapper\\system\\ScExamPaperMapper.xml]\r\n### The error may involve com.smart.system.mapper.ScExamPaperMapper.updateScExamPaper-Inline\r\n### The error occurred while setting parameters\r\n### SQL: update sc_exam_paper  SET paper_name = ?,course_id     = ?,paper_type = ?,total_score = ?,duration_minutes = ?,publish_status = ?,publish_start_time = ?,publish_end_time = ?,pass_score = ?,marking_mode = ?,question_order_mode = ?,allow_review_analysis = ?,section_config_json = ?,status = ?,update_by =     ?,remark = ?,update_time = sysdate()      where paper_id = ?\r\n### Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'remark\' at row 1\n; Data truncation: Data too long for column \'remark\' at row 1', '2026-03-21 13:16:32', 12);
INSERT INTO `sys_oper_log` VALUES (252, '排课表', 0, 'com.smart.web.controller.campus.ScCourseScheduleController.autoArrange()', 'POST', 1, 'admin', '校务中心', '/campus/courseSchedule/auto-arrange', '127.0.0.1', '内网IP', '{\"classCourseIds\":[1],\"clearExistingSchedules\":false,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"excludedWeekDays\":[6,7],\"generationCount\":120,\"items\":[{\"classCourseId\":1,\"excludedDayParts\":[\"NOON\",\"EVENING\"],\"excludedWeekDays\":[6,7],\"maxWeeklySections\":6,\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"weeksText\":\"1-15周\"}],\"mutationRate\":0.12,\"populationSize\":60,\"preferredSessionDurations\":[4,2,1],\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"arrangedLessonTasks\":0,\"arrangedLessons\":[],\"bestFitnessScore\":0.0,\"clearedSchedules\":0,\"generationCount\":120,\"insertedSchedules\":0,\"mutationRate\":0.12,\"populationSize\":60,\"termId\":1,\"termName\":\"2025-2026学年第二学期\",\"totalLessonTasks\":0,\"unscheduledLessons\":[],\"warnings\":[\"当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。\"]}}', 0, NULL, '2026-03-21 13:45:07', 104);
INSERT INTO `sys_oper_log` VALUES (253, 'Prompt模板', 3, 'com.smart.web.controller.campus.ScAiController.removePrompt()', 'DELETE', 1, 'admin', '校务中心', '/campus/ai/prompt/1', '127.0.0.1', '内网IP', '[1] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-21 16:02:18', 29);
INSERT INTO `sys_oper_log` VALUES (254, '题库管理', 3, 'com.smart.web.controller.campus.ScExamController.removeQuestion()', 'DELETE', 1, 'admin', '校务中心', '/campus/exam/question/12,13,11,10,9', '127.0.0.1', '内网IP', '[12,13,11,10,9] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-21 16:22:38', 41);
INSERT INTO `sys_oper_log` VALUES (255, '试卷管理', 3, 'com.smart.web.controller.campus.ScExamController.removePaperDetail()', 'DELETE', 1, 'admin', '校务中心', '/campus/exam/paper/detail/1', '127.0.0.1', '内网IP', '[1] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-21 16:30:45', 39);
INSERT INTO `sys_oper_log` VALUES (256, '教室管理', 2, 'com.smart.web.controller.campus.ScClassroomController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/classroom', '127.0.0.1', '内网IP', '{\"buildingName\":\"躬行楼\",\"campusName\":\"本部\",\"capacity\":60,\"classroomId\":1,\"classroomName\":\"交换机路由实训室\",\"createBy\":\"admin\",\"createTime\":\"2026-03-17 18:42:43\",\"deptId\":321,\"deptName\":\"电子信息工程学院\",\"params\":{},\"remark\":\"\",\"roomType\":\"机房\",\"sortOrder\":0,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2026-03-20 09:42:28\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-21 18:51:32', 66);
INSERT INTO `sys_oper_log` VALUES (257, '题库管理', 2, 'com.smart.web.controller.campus.ScExamController.editQuestionDetail()', 'PUT', 1, 'admin', '校务中心', '/campus/exam/question/detail', '127.0.0.1', '内网IP', '{\"analysis\":\"NumPy 提供高性能的多维数组对象及数学函数，是数据分析的基础（A 正确）；Pandas 基于 NumPy 构建，提供 DataFrame 和 Series 结构，擅长数据操纵和分析（B 正确）；Scikit-learn 是机器学习库，虽然依赖数据分析结果，但其核心功能不是数据可视化，可视化主要由 Matplotlib 和 Seaborn 负责（C 错误）；Matplotlib 是基础的绘图库，可用于生成各种静态、动态和交互式可视化图表（D 正确）。\",\"answer\":\"A,B,D\",\"courseId\":2,\"difficultyLevel\":3,\"materialContent\":\"\",\"options\":[{\"isRight\":\"1\",\"optionContent\":\"NumPy 主要用于高效的数值计算和多维数组操作\",\"optionId\":89,\"optionKey\":\"A\"},{\"isRight\":\"1\",\"optionContent\":\"Pandas 提供了强大的数据结构和数据分析工具\",\"optionId\":90,\"optionKey\":\"B\"},{\"isRight\":\"0\",\"optionContent\":\"Scikit-learn 是专门用于高级数据可视化的核心库\",\"optionId\":91,\"optionKey\":\"C\"},{\"isRight\":\"1\",\"optionContent\":\"Matplotlib 是 Python 中最基础且广泛使用的绘图库\",\"optionId\":92,\"optionKey\":\"D\"}],\"qualityScore\":0,\"questionId\":23,\"questionTags\":\"\",\"questionType\":\"multiple\",\"remark\":\"\",\"source\":\"AI智能出题\",\"sourceBatchNo\":\"AI-1774081794757\",\"status\":\"0\",\"stem\":\"<p>下列关于 Python 数据分析生态系统中各库主要功能的描述，正确的有（ ）。</p>\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-21 18:53:56', 96);
INSERT INTO `sys_oper_log` VALUES (258, '学习任务', 1, 'com.smart.web.controller.campus.ScLearningTaskController.add()', 'POST', 1, 'admin', '校务中心', '/campus/learningTask', '127.0.0.1', '内网IP', '{\"actionPath\":\"/student/courses\",\"actionTargetId\":1,\"actionType\":\"course\",\"bizId\":1,\"bizType\":\"HOMEWORK\",\"classCourseId\":1,\"courseId\":2,\"createBy\":\"admin\",\"dueTime\":\"2026-03-30 00:00:00\",\"params\":{},\"priorityLevel\":\"NORMAL\",\"publishTime\":\"2026-03-22 17:23:21\",\"startTime\":\"2026-03-22 00:00:00\",\"status\":\"0\",\"taskDesc\":\"自主学习。\",\"taskId\":1,\"taskTitle\":\"课后作业：Python数据分析\",\"taskType\":\"HOMEWORK\",\"termId\":1} ', '{\"msg\":\"操作成功\",\"code\":200,\"taskId\":1}', 0, NULL, '2026-03-22 17:23:22', 56);
INSERT INTO `sys_oper_log` VALUES (259, '学习任务派发', 0, 'com.smart.web.controller.campus.ScLearningTaskController.dispatch()', 'POST', 1, 'admin', '校务中心', '/campus/learningTask/1/dispatch', '127.0.0.1', '内网IP', '1 [102] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-22 17:23:32', 15);
INSERT INTO `sys_oper_log` VALUES (260, '学习任务派发', 2, 'com.smart.web.controller.campus.ScLearningTaskDispatchController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/learningTaskDispatch', '127.0.0.1', '内网IP', '{\"completedTime\":\"2026-03-22 17:26:29\",\"completionRemark\":\"学生在首页标记完成\",\"completionStatus\":\"PENDING\",\"createBy\":\"admin\",\"createTime\":\"2026-03-22 17:23:32\",\"dispatchId\":1,\"dispatchStatus\":\"0\",\"params\":{},\"readTime\":\"2026-03-22 17:26:13\",\"taskId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-22 17:26:28\",\"userId\":102} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-22 17:26:36', 15);
INSERT INTO `sys_oper_log` VALUES (261, '排课表', 2, 'com.smart.web.controller.campus.ScCourseScheduleController.edit()', 'PUT', 1, 'admin', '校务中心', '/campus/courseSchedule', '127.0.0.1', '内网IP', '{\"buildingName\":\"躬行楼\",\"campusName\":\"本部\",\"classCourseId\":1,\"classroom\":\"交换机路由实训室\",\"classroomId\":1,\"classroomName\":\"交换机路由实训室\",\"createBy\":\"admin\",\"createTime\":\"2026-03-20 11:17:36\",\"endSection\":4,\"params\":{},\"remark\":\"遗传算法自动排课生成\",\"scheduleId\":14,\"startSection\":1,\"status\":\"0\",\"termId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2026-03-21 18:51:32\",\"weekDay\":1,\"weeksJson\":\"[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]\",\"weeksText\":\"1-15周\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-22 17:36:07', 58);
INSERT INTO `sys_oper_log` VALUES (262, '试卷管理', 1, 'com.smart.web.controller.campus.ScExamController.addPaperDetail()', 'POST', 1, 'admin', '校务中心', '/campus/exam/paper/detail', '127.0.0.1', '内网IP', '{\"allowReviewAnalysis\":\"1\",\"answerMode\":\"REQUIRED\",\"durationMinutes\":60,\"markingMode\":\"auto_first\",\"paperLevel\":\"MAIN\",\"paperName\":\"测试试卷\",\"paperType\":\"fixed\",\"paperWeight\":100,\"passScore\":60,\"publishEndTime\":\"2026-03-31 00:00:00\",\"publishStartTime\":\"2026-03-23 14:58:31\",\"publishStatus\":\"draft\",\"questionOrderMode\":\"manual\",\"questions\":[{\"questionId\":8,\"score\":20,\"sortNo\":1},{\"questionId\":14,\"score\":20,\"sortNo\":2},{\"questionId\":7,\"score\":20,\"sortNo\":3},{\"questionId\":6,\"score\":20,\"sortNo\":4},{\"questionId\":5,\"score\":20,\"sortNo\":5}],\"remark\":\"\",\"sectionConfigJson\":\"[{\\\"title\\\":\\\"单选题\\\",\\\"questionType\\\":\\\"single\\\",\\\"defaultScore\\\":20,\\\"targetScore\\\":100,\\\"instructions\\\":\\\"每题 20 分，共 5 题\\\",\\\"questionIds\\\":[8,14,7,6,5]}]\",\"status\":\"0\",\"subPapers\":[],\"totalScore\":100} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-23 14:59:29', 152);
INSERT INTO `sys_oper_log` VALUES (263, '试卷管理', 2, 'com.smart.web.controller.campus.ScExamController.editPaperDetail()', 'PUT', 1, 'admin', '校务中心', '/campus/exam/paper/detail', '127.0.0.1', '内网IP', '{\"allowReviewAnalysis\":\"1\",\"answerMode\":\"REQUIRED\",\"durationMinutes\":60,\"markingMode\":\"auto_first\",\"paperId\":2,\"paperLevel\":\"MAIN\",\"paperName\":\"测试试卷\",\"paperType\":\"fixed\",\"paperWeight\":100,\"passScore\":60,\"publishEndTime\":\"2026-03-31 00:00:00\",\"publishStartTime\":\"2026-03-23 06:59:38\",\"publishStatus\":\"published\",\"questionOrderMode\":\"manual\",\"questions\":[{\"id\":17,\"questionId\":8,\"score\":20,\"sortNo\":1},{\"id\":18,\"questionId\":14,\"score\":20,\"sortNo\":2},{\"id\":19,\"questionId\":7,\"score\":20,\"sortNo\":3},{\"id\":20,\"questionId\":6,\"score\":20,\"sortNo\":4},{\"id\":21,\"questionId\":5,\"score\":20,\"sortNo\":5}],\"sectionConfigJson\":\"[{\\\"title\\\":\\\"单选题\\\",\\\"questionType\\\":\\\"single\\\",\\\"defaultScore\\\":20,\\\"targetScore\\\":100,\\\"instructions\\\":\\\"每题 20 分，共 5 题\\\",\\\"questionIds\\\":[8,14,7,6,5]}]\",\"status\":\"0\",\"subPapers\":[],\"totalScore\":100} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-23 14:59:38', 44);
INSERT INTO `sys_oper_log` VALUES (264, '角色管理', 2, 'com.smart.web.controller.system.SysRoleController.edit()', 'PUT', 1, 'admin', '校务中心', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2026-03-17 16:07:33\",\"dataScope\":\"4\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"remark\":\"教务班主任角色\",\"roleId\":103,\"roleKey\":\"campus_head_teacher\",\"roleName\":\"辅导员\",\"roleSort\":130,\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-23 23:05:02', 45);
INSERT INTO `sys_oper_log` VALUES (265, '学习资源', 1, 'com.smart.web.controller.campus.ScResourceController.add()', 'POST', 1, 'admin', '校务中心', '/campus/resource', '127.0.0.1', '内网IP', '{\"auditStatus\":\"1\",\"contentText\":\"阅读难度：basic\\n可打印：是\",\"courseId\":2,\"coverUrl\":\"\",\"createBy\":\"admin\",\"downloadCount\":0,\"favoriteCount\":0,\"fileUrl\":\"/profile/upload/2026/03/24/2025-2026年度安徽省职业院校技能大赛（高职组）拟获奖名单（学生赛）_20260324123351A001.pdf\",\"params\":{},\"qualityScore\":80,\"remark\":\"{\\\"knowledgePointIds\\\":[],\\\"keywordsText\\\":\\\"\\\",\\\"learningAdvice\\\":\\\"\\\",\\\"aiInsights\\\":\\\"\\\",\\\"videoDuration\\\":\\\"\\\",\\\"videoResolution\\\":\\\"hd\\\",\\\"posterTime\\\":\\\"\\\",\\\"captionLanguage\\\":\\\"zh-CN\\\",\\\"pageCount\\\":\\\"\\\",\\\"readingLevel\\\":\\\"basic\\\",\\\"readingMinutes\\\":\\\"\\\",\\\"printable\\\":true,\\\"keyPoints\\\":\\\"\\\",\\\"questionType\\\":\\\"single\\\",\\\"difficultyLevel\\\":1,\\\"questionStem\\\":\\\"\\\",\\\"questionAnswer\\\":\\\"\\\",\\\"questionAnalysis\\\":\\\"\\\"}\",\"resourceId\":1,\"resourceName\":\"测试资源\",\"resourceType\":\"pdf\",\"status\":\"0\",\"summary\":\"阅读难度：basic\\n可打印：是\",\"uploaderId\":1,\"viewCount\":0} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-03-24 12:33:56', 26);

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'manager', '管理员', 1, '0', 'admin', '2026-03-10 19:39:27', 'admin', '2026-03-17 14:31:58', '');
INSERT INTO `sys_post` VALUES (5, 'course_teacher', '任课教师', 10, '0', 'admin', '2026-03-17 16:07:33', '', NULL, '教务身份初始化');
INSERT INTO `sys_post` VALUES (6, 'head_teacher', '班主任', 20, '0', 'admin', '2026-03-17 16:07:33', '', NULL, '教务身份初始化');
INSERT INTO `sys_post` VALUES (7, 'academic_admin', '教务员', 30, '0', 'admin', '2026-03-17 16:07:33', '', NULL, '教务身份初始化');
INSERT INTO `sys_post` VALUES (8, 'student_identity', '学生身份', 40, '0', 'admin', '2026-03-17 16:07:33', '', NULL, '教务身份初始化');
INSERT INTO `sys_post` VALUES (9, 'parent_guardian', '家长监护', 50, '0', 'admin', '2026-03-17 16:07:33', '', NULL, '教务身份初始化');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 105 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2026-03-10 19:39:27', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '2', 'admin', '2026-03-10 19:39:27', 'admin', '2026-03-11 13:24:36', '普通角色');
INSERT INTO `sys_role` VALUES (100, '学生', 'student', 100, '5', 1, 1, '0', '0', 'admin', '2026-03-17 16:07:33', '', NULL, '门户学生角色');
INSERT INTO `sys_role` VALUES (101, '教师', 'teacher', 110, '5', 1, 1, '0', '0', 'admin', '2026-03-17 16:07:33', '', NULL, '门户教师角色');
INSERT INTO `sys_role` VALUES (102, '家长', 'parent', 120, '5', 1, 1, '0', '0', 'admin', '2026-03-17 16:07:33', '', NULL, '门户家长角色');
INSERT INTO `sys_role` VALUES (103, '辅导员', 'campus_head_teacher', 130, '4', 1, 1, '0', '0', 'admin', '2026-03-17 16:07:33', 'admin', '2026-03-23 23:05:02', '教务班主任角色');
INSERT INTO `sys_role` VALUES (104, '教务员', 'campus_academic_admin', 140, '1', 1, 1, '0', '0', 'admin', '2026-03-17 16:07:33', '', NULL, '教务管理员角色');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 2000);
INSERT INTO `sys_role_menu` VALUES (1, 2001);
INSERT INTO `sys_role_menu` VALUES (1, 2002);
INSERT INTO `sys_role_menu` VALUES (1, 2003);
INSERT INTO `sys_role_menu` VALUES (1, 2004);
INSERT INTO `sys_role_menu` VALUES (1, 2005);
INSERT INTO `sys_role_menu` VALUES (1, 2006);
INSERT INTO `sys_role_menu` VALUES (1, 2007);
INSERT INTO `sys_role_menu` VALUES (1, 2008);
INSERT INTO `sys_role_menu` VALUES (1, 2010);
INSERT INTO `sys_role_menu` VALUES (1, 2011);
INSERT INTO `sys_role_menu` VALUES (1, 2012);
INSERT INTO `sys_role_menu` VALUES (1, 2013);
INSERT INTO `sys_role_menu` VALUES (1, 2020);
INSERT INTO `sys_role_menu` VALUES (1, 2021);
INSERT INTO `sys_role_menu` VALUES (1, 2022);
INSERT INTO `sys_role_menu` VALUES (1, 2030);
INSERT INTO `sys_role_menu` VALUES (1, 2031);
INSERT INTO `sys_role_menu` VALUES (1, 2032);
INSERT INTO `sys_role_menu` VALUES (1, 2034);
INSERT INTO `sys_role_menu` VALUES (1, 2035);
INSERT INTO `sys_role_menu` VALUES (1, 2036);
INSERT INTO `sys_role_menu` VALUES (1, 2037);
INSERT INTO `sys_role_menu` VALUES (1, 2038);
INSERT INTO `sys_role_menu` VALUES (1, 2039);
INSERT INTO `sys_role_menu` VALUES (1, 2040);
INSERT INTO `sys_role_menu` VALUES (1, 2041);
INSERT INTO `sys_role_menu` VALUES (1, 2042);
INSERT INTO `sys_role_menu` VALUES (1, 2043);
INSERT INTO `sys_role_menu` VALUES (1, 2044);
INSERT INTO `sys_role_menu` VALUES (1, 2045);
INSERT INTO `sys_role_menu` VALUES (1, 2046);
INSERT INTO `sys_role_menu` VALUES (1, 2050);
INSERT INTO `sys_role_menu` VALUES (1, 2051);
INSERT INTO `sys_role_menu` VALUES (1, 2052);
INSERT INTO `sys_role_menu` VALUES (1, 2060);
INSERT INTO `sys_role_menu` VALUES (1, 2061);
INSERT INTO `sys_role_menu` VALUES (1, 2062);
INSERT INTO `sys_role_menu` VALUES (1, 2063);
INSERT INTO `sys_role_menu` VALUES (1, 2064);
INSERT INTO `sys_role_menu` VALUES (1, 2070);
INSERT INTO `sys_role_menu` VALUES (1, 2071);
INSERT INTO `sys_role_menu` VALUES (1, 2080);
INSERT INTO `sys_role_menu` VALUES (1, 2081);
INSERT INTO `sys_role_menu` VALUES (1, 2082);
INSERT INTO `sys_role_menu` VALUES (1, 2100);
INSERT INTO `sys_role_menu` VALUES (1, 2101);
INSERT INTO `sys_role_menu` VALUES (1, 2102);
INSERT INTO `sys_role_menu` VALUES (1, 2103);
INSERT INTO `sys_role_menu` VALUES (1, 2110);
INSERT INTO `sys_role_menu` VALUES (1, 2111);
INSERT INTO `sys_role_menu` VALUES (1, 2112);
INSERT INTO `sys_role_menu` VALUES (1, 2113);
INSERT INTO `sys_role_menu` VALUES (1, 2120);
INSERT INTO `sys_role_menu` VALUES (1, 2121);
INSERT INTO `sys_role_menu` VALUES (1, 2130);
INSERT INTO `sys_role_menu` VALUES (1, 2131);
INSERT INTO `sys_role_menu` VALUES (1, 2140);
INSERT INTO `sys_role_menu` VALUES (1, 2141);
INSERT INTO `sys_role_menu` VALUES (1, 2150);
INSERT INTO `sys_role_menu` VALUES (1, 2151);
INSERT INTO `sys_role_menu` VALUES (1, 2152);
INSERT INTO `sys_role_menu` VALUES (1, 2153);
INSERT INTO `sys_role_menu` VALUES (1, 2160);
INSERT INTO `sys_role_menu` VALUES (1, 2161);
INSERT INTO `sys_role_menu` VALUES (1, 2162);
INSERT INTO `sys_role_menu` VALUES (1, 2163);
INSERT INTO `sys_role_menu` VALUES (1, 2164);
INSERT INTO `sys_role_menu` VALUES (1, 2170);
INSERT INTO `sys_role_menu` VALUES (1, 2171);
INSERT INTO `sys_role_menu` VALUES (1, 2172);
INSERT INTO `sys_role_menu` VALUES (1, 2173);
INSERT INTO `sys_role_menu` VALUES (1, 2174);
INSERT INTO `sys_role_menu` VALUES (1, 2180);
INSERT INTO `sys_role_menu` VALUES (1, 2181);
INSERT INTO `sys_role_menu` VALUES (1, 2182);
INSERT INTO `sys_role_menu` VALUES (1, 2183);
INSERT INTO `sys_role_menu` VALUES (1, 2184);
INSERT INTO `sys_role_menu` VALUES (1, 2185);
INSERT INTO `sys_role_menu` VALUES (1, 2186);
INSERT INTO `sys_role_menu` VALUES (1, 2187);
INSERT INTO `sys_role_menu` VALUES (1, 2188);
INSERT INTO `sys_role_menu` VALUES (1, 2189);
INSERT INTO `sys_role_menu` VALUES (1, 2190);
INSERT INTO `sys_role_menu` VALUES (1, 2191);
INSERT INTO `sys_role_menu` VALUES (1, 2196);
INSERT INTO `sys_role_menu` VALUES (1, 2197);
INSERT INTO `sys_role_menu` VALUES (1, 2198);
INSERT INTO `sys_role_menu` VALUES (1, 2199);
INSERT INTO `sys_role_menu` VALUES (1, 2200);
INSERT INTO `sys_role_menu` VALUES (1, 2201);
INSERT INTO `sys_role_menu` VALUES (1, 2202);
INSERT INTO `sys_role_menu` VALUES (1, 2203);
INSERT INTO `sys_role_menu` VALUES (1, 2204);
INSERT INTO `sys_role_menu` VALUES (1, 2205);
INSERT INTO `sys_role_menu` VALUES (1, 2206);
INSERT INTO `sys_role_menu` VALUES (1, 2207);
INSERT INTO `sys_role_menu` VALUES (1, 2208);
INSERT INTO `sys_role_menu` VALUES (1, 2209);
INSERT INTO `sys_role_menu` VALUES (1, 2210);
INSERT INTO `sys_role_menu` VALUES (1, 2211);
INSERT INTO `sys_role_menu` VALUES (1, 2212);
INSERT INTO `sys_role_menu` VALUES (1, 2213);
INSERT INTO `sys_role_menu` VALUES (1, 2214);
INSERT INTO `sys_role_menu` VALUES (1, 2215);
INSERT INTO `sys_role_menu` VALUES (1, 2216);
INSERT INTO `sys_role_menu` VALUES (1, 2221);
INSERT INTO `sys_role_menu` VALUES (1, 2222);
INSERT INTO `sys_role_menu` VALUES (1, 2223);
INSERT INTO `sys_role_menu` VALUES (1, 2224);
INSERT INTO `sys_role_menu` VALUES (1, 2225);
INSERT INTO `sys_role_menu` VALUES (1, 2226);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime NULL DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 301, 'admin', '程安宁', '00', 'can20060303@gmail.com', '18955952774', '0', '/profile/avatar/2026/03/17/50b92bae76c04d358ac421ae7bca7a63.png', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2026-03-24 12:03:41', '2026-03-10 19:39:27', 'admin', '2026-03-10 19:39:27', 'admin', '2026-03-17 16:18:57', '管理员');
INSERT INTO `sys_user` VALUES (2, 105, 'ry', '若依', '00', 'ry@qq.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '2', '127.0.0.1', '2026-03-10 19:39:27', '2026-03-10 19:39:27', 'admin', '2026-03-10 19:39:27', '', NULL, '测试员');
INSERT INTO `sys_user` VALUES (100, NULL, '张老师', 'zhangteacher', '00', '', '', '0', '', '$2a$10$Y8259XmbMCIs4SWdtr.i4u4Z/hIEeEKHg/ssu1wMEdY3PgpKR29ue', '0', '2', '', NULL, NULL, 'admin', '2026-03-17 14:29:55', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (101, NULL, 'zhangteacher', '张老师', '00', '', '', '0', '', '$2a$10$5fe25.x/gKwK7DIsuiWOCOhZU9SdFtLHXDNddlyLR9IlBOKk0YZh6', '0', '0', '', NULL, NULL, 'admin', '2026-03-17 14:32:47', 'admin', '2026-03-17 16:39:43', NULL);
INSERT INTO `sys_user` VALUES (102, 321, 'zhangsan', '张三', '00', '', '18654141553', '0', '', '$2a$10$9Ce3Tij5HH/OyM8P/7q9hOYTXboRhKn4jvVM49klGhZeyN3K01cJe', '0', '0', '127.0.0.1', '2026-03-24 12:03:57', '2026-03-17 15:08:03', '', '2026-03-17 15:08:02', 'zhangsan', '2026-03-23 13:00:47', '');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (1, 1);
INSERT INTO `sys_user_post` VALUES (101, 5);
INSERT INTO `sys_user_post` VALUES (102, 8);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (101, 101);
INSERT INTO `sys_user_role` VALUES (101, 103);
INSERT INTO `sys_user_role` VALUES (102, 100);

SET FOREIGN_KEY_CHECKS = 1;
