SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 在线学习平台资源中心升级脚本
-- 正确模型：
-- 1. 课程 -> 树形目录节点
-- 2. 节点 -> 文章/课时（例如：我是中国人、我爱我们的祖国）
-- 3. 文章/课时 -> 多个资源资产（视频课程、课件、教学设计、学习任务单、课后练习）
-- 4. 评论/评分/分享挂在文章/课时层
-- ----------------------------

ALTER TABLE `sc_resource`
  ADD COLUMN `node_id` bigint DEFAULT NULL COMMENT '所属课程资源目录节点ID' AFTER `chapter_id`,
  ADD COLUMN `sort_no` int DEFAULT 0 COMMENT '同节点下文章排序号' AFTER `node_id`,
  ADD COLUMN `author_name` varchar(100) DEFAULT NULL COMMENT '作者/主讲人' AFTER `uploader_id`,
  ADD COLUMN `publisher_name` varchar(100) DEFAULT NULL COMMENT '发布单位/出版社' AFTER `author_name`,
  ADD COLUMN `target_user_type` varchar(20) DEFAULT 'student' COMMENT '适用对象(student/teacher/parent/all)' AFTER `publisher_name`,
  ADD COLUMN `allow_comment` char(1) DEFAULT '1' COMMENT '是否允许评论(1是 0否)' AFTER `favorite_count`,
  ADD COLUMN `allow_rating` char(1) DEFAULT '1' COMMENT '是否允许评分(1是 0否)' AFTER `allow_comment`,
  ADD COLUMN `allow_share` char(1) DEFAULT '1' COMMENT '是否允许分享(1是 0否)' AFTER `allow_rating`,
  ADD COLUMN `avg_rating` decimal(4,2) DEFAULT 0.00 COMMENT '平均评分' AFTER `quality_score`,
  ADD COLUMN `rating_count` int DEFAULT 0 COMMENT '评分次数' AFTER `avg_rating`,
  ADD COLUMN `comment_count` int DEFAULT 0 COMMENT '评论数' AFTER `rating_count`,
  ADD COLUMN `share_count` int DEFAULT 0 COMMENT '分享次数' AFTER `comment_count`;

ALTER TABLE `sc_resource`
  ADD KEY `idx_sc_resource_course_node` (`course_id`, `node_id`, `sort_no`),
  ADD KEY `idx_sc_resource_status` (`status`, `audit_status`);

DROP TABLE IF EXISTS `sc_resource_node`;
CREATE TABLE `sc_resource_node` (
  `node_id` bigint NOT NULL AUTO_INCREMENT COMMENT '目录节点ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `parent_id` bigint DEFAULT 0 COMMENT '父节点ID',
  `node_name` varchar(100) NOT NULL COMMENT '节点名称',
  `sort_no` int DEFAULT 0 COMMENT '排序号',
  `expand_default` char(1) DEFAULT '1' COMMENT '默认展开(1是 0否)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`node_id`),
  KEY `idx_sc_resource_node_course_parent` (`course_id`, `parent_id`),
  KEY `idx_sc_resource_node_sort` (`course_id`, `sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资源树节点表';

DROP TABLE IF EXISTS `sc_resource_asset`;
CREATE TABLE `sc_resource_asset` (
  `asset_id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源资产ID',
  `resource_id` bigint NOT NULL COMMENT '所属文章/课时ID',
  `asset_type` varchar(30) NOT NULL COMMENT '资产类型(video_course/courseware/teaching_design/task_sheet/after_class_exercise)',
  `asset_name` varchar(200) NOT NULL COMMENT '资产名称',
  `file_url` varchar(255) DEFAULT NULL COMMENT '文件/视频地址',
  `cover_url` varchar(255) DEFAULT NULL COMMENT '封面地址',
  `content_text` longtext COMMENT '正文/内容补充',
  `summary` varchar(1000) DEFAULT NULL COMMENT '资产摘要',
  `biz_type` varchar(30) DEFAULT NULL COMMENT '关联业务类型(task/paper/question/external)',
  `biz_id` bigint DEFAULT NULL COMMENT '关联业务ID',
  `sort_no` int DEFAULT 0 COMMENT '同文章内排序',
  `allow_download` char(1) DEFAULT '1' COMMENT '是否允许下载(1是 0否)',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `download_count` int DEFAULT 0 COMMENT '下载次数',
  `favorite_count` int DEFAULT 0 COMMENT '收藏次数',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`asset_id`),
  KEY `idx_sc_resource_asset_resource` (`resource_id`, `asset_type`, `sort_no`),
  KEY `idx_sc_resource_asset_biz` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章资源资产表';

DROP TABLE IF EXISTS `sc_resource_comment`;
CREATE TABLE `sc_resource_comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `resource_id` bigint NOT NULL COMMENT '文章/课时ID',
  `course_id` bigint DEFAULT NULL COMMENT '课程ID',
  `node_id` bigint DEFAULT NULL COMMENT '目录节点ID',
  `user_id` bigint NOT NULL COMMENT '评论人ID',
  `user_name` varchar(100) DEFAULT NULL COMMENT '评论人名称',
  `parent_id` bigint DEFAULT 0 COMMENT '父评论ID',
  `root_id` bigint DEFAULT 0 COMMENT '根评论ID',
  `content` varchar(1000) NOT NULL COMMENT '评论内容',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1隐藏)',
  `like_count` int DEFAULT 0 COMMENT '点赞数',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`comment_id`),
  KEY `idx_sc_resource_comment_resource` (`resource_id`, `create_time`),
  KEY `idx_sc_resource_comment_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章评论表';

DROP TABLE IF EXISTS `sc_resource_rating`;
CREATE TABLE `sc_resource_rating` (
  `rating_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评分ID',
  `resource_id` bigint NOT NULL COMMENT '文章/课时ID',
  `user_id` bigint NOT NULL COMMENT '评分人ID',
  `rating_score` int NOT NULL COMMENT '评分(1-5)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`rating_id`),
  UNIQUE KEY `uk_sc_resource_rating_resource_user` (`resource_id`, `user_id`),
  KEY `idx_sc_resource_rating_resource` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章评分表';

-- 为现有课程创建一个默认根目录，便于平滑迁移旧资源
INSERT INTO `sc_resource_node`
(`course_id`, `parent_id`, `node_name`, `sort_no`, `expand_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT c.course_id, 0, '课程资源', 1, '1', '0', 'admin', NOW(), '系统初始化默认课程资源根目录'
FROM `sc_course` c
WHERE NOT EXISTS (
  SELECT 1 FROM `sc_resource_node` n
  WHERE n.course_id = c.course_id
    AND n.parent_id = 0
    AND n.node_name = '课程资源'
);

-- 旧资源挂接到默认根目录，作为文章/课时主体
UPDATE `sc_resource` r
JOIN `sc_resource_node` n
  ON n.course_id = r.course_id
 AND n.parent_id = 0
 AND n.node_name = '课程资源'
SET r.node_id = n.node_id,
    r.sort_no = IFNULL(r.sort_no, 0)
WHERE r.course_id IS NOT NULL
  AND (r.node_id IS NULL OR r.node_id = 0);

-- 按旧资源类型回填为第一批资源资产
INSERT INTO `sc_resource_asset`
(`resource_id`, `asset_type`, `asset_name`, `file_url`, `cover_url`, `content_text`, `summary`, `sort_no`, `allow_download`, `view_count`, `download_count`, `favorite_count`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  r.resource_id,
  CASE
    WHEN r.resource_type = 'video' THEN 'video_course'
    WHEN r.resource_type IN ('doc', 'pdf', 'ppt') THEN 'courseware'
    WHEN r.resource_type = 'question' THEN 'after_class_exercise'
    ELSE 'courseware'
  END AS asset_type,
  CONCAT(r.resource_name, ' - 主资源'),
  r.file_url,
  r.cover_url,
  r.content_text,
  r.summary,
  1,
  '1',
  IFNULL(r.view_count, 0),
  IFNULL(r.download_count, 0),
  IFNULL(r.favorite_count, 0),
  IFNULL(r.status, '0'),
  IFNULL(r.create_by, 'admin'),
  IFNULL(r.create_time, NOW()),
  '由旧资源表自动迁移生成'
FROM `sc_resource` r
WHERE NOT EXISTS (
  SELECT 1 FROM `sc_resource_asset` a WHERE a.resource_id = r.resource_id
);

SET FOREIGN_KEY_CHECKS = 1;
