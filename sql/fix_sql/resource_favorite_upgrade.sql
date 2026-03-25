SET NAMES utf8mb4;

DROP TABLE IF EXISTS `sc_resource_favorite`;
CREATE TABLE `sc_resource_favorite` (
  `favorite_id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `resource_id` bigint NOT NULL COMMENT '文章/课时ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`favorite_id`),
  UNIQUE KEY `uk_sc_resource_favorite_resource_user` (`resource_id`, `user_id`),
  KEY `idx_sc_resource_favorite_user` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章收藏表';
