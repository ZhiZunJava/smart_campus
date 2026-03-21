-- 学生成长体系第一阶段
-- 目标：
-- 1. 建立积分账户与积分流水
-- 2. 建立成就记录表
-- 3. 先打通考试提交后的积分发放与基础成就解锁
-- 执行前请先备份数据库

CREATE TABLE IF NOT EXISTS `sc_user_point_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_points` int DEFAULT 0 COMMENT '累计积分',
  `available_points` int DEFAULT 0 COMMENT '可用积分',
  `used_points` int DEFAULT 0 COMMENT '已使用积分',
  `level_code` varchar(32) DEFAULT 'L1' COMMENT '成长等级编码',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_point_account_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-用户积分账户表';

CREATE TABLE IF NOT EXISTS `sc_user_point_ledger` (
  `ledger_id` bigint NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `change_type` varchar(32) NOT NULL COMMENT '变更类型(EARN/USE/REFUND)',
  `biz_type` varchar(64) NOT NULL COMMENT '业务类型',
  `biz_id` bigint DEFAULT NULL COMMENT '业务ID',
  `change_points` int NOT NULL COMMENT '积分变更值',
  `balance_after` int DEFAULT 0 COMMENT '变更后余额',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`ledger_id`),
  UNIQUE KEY `uk_user_point_ledger_biz` (`user_id`, `change_type`, `biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-用户积分流水表';

CREATE TABLE IF NOT EXISTS `sc_user_achievement` (
  `achievement_id` bigint NOT NULL AUTO_INCREMENT COMMENT '成就ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `achievement_code` varchar(64) NOT NULL COMMENT '成就编码',
  `achievement_title` varchar(128) NOT NULL COMMENT '成就标题',
  `achievement_desc` varchar(255) DEFAULT NULL COMMENT '成就描述',
  `status` char(1) DEFAULT '1' COMMENT '状态(1已解锁 0未解锁)',
  `progress_value` int DEFAULT 0 COMMENT '进度值',
  `earned_time` datetime DEFAULT NULL COMMENT '达成时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`achievement_id`),
  UNIQUE KEY `uk_user_achievement_code` (`user_id`, `achievement_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧校园-用户成就表';
