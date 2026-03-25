SET NAMES utf8mb4;

DROP TABLE IF EXISTS `sc_resource_comment_like`;
CREATE TABLE `sc_resource_comment_like` (
  `like_id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `uk_sc_resource_comment_like_comment_user` (`comment_id`, `user_id`),
  KEY `idx_sc_resource_comment_like_user` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';
