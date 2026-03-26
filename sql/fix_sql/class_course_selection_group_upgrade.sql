SET NAMES utf8mb4;

-- ----------------------------
-- 班级课程专项选课分组升级脚本
-- 目标：
-- 1. 支持同一班级、同一学期、同一母课程下配置多个专项选项，例如体育-篮球 / 体育-排球
-- 2. 为选课中心提供“组内限选”规则配置
-- ----------------------------

ALTER TABLE sc_class_course
  ADD COLUMN IF NOT EXISTS selection_option_name varchar(100) NOT NULL DEFAULT '' COMMENT '专项名称/子项目名称' AFTER required_flag,
  ADD COLUMN IF NOT EXISTS selection_group_code varchar(64) DEFAULT NULL COMMENT '专项选课分组编码' AFTER selection_option_name,
  ADD COLUMN IF NOT EXISTS selection_group_name varchar(100) DEFAULT NULL COMMENT '专项选课分组名称' AFTER selection_group_code,
  ADD COLUMN IF NOT EXISTS selection_group_limit int DEFAULT 1 COMMENT '组内限选门数' AFTER selection_group_name;

UPDATE sc_class_course
SET selection_option_name = COALESCE(selection_option_name, ''),
    selection_group_limit = CASE
      WHEN selection_group_limit IS NULL OR selection_group_limit <= 0 THEN 1
      ELSE selection_group_limit
    END
WHERE 1 = 1;

ALTER TABLE sc_class_course
  DROP INDEX uk_class_course_term,
  ADD UNIQUE INDEX uk_class_course_term_option (class_id, course_id, term_id, selection_option_name);
