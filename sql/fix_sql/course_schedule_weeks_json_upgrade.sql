SET NAMES utf8mb4;

-- 为排课表增加结构化周次存储
-- 说明：
-- 1. weeks_text 保留，兼容老数据与旧界面
-- 2. weeks_json 用于结构化存储周次数组，例如 [1,2,3,4,5]

SET @has_weeks_json := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sc_course_schedule'
      AND COLUMN_NAME = 'weeks_json'
);

SET @add_weeks_json_sql := IF(
    @has_weeks_json = 0,
    'ALTER TABLE sc_course_schedule ADD COLUMN weeks_json VARCHAR(1024) NULL COMMENT ''结构化周次JSON'' AFTER weeks_text',
    'SELECT 1'
);

PREPARE stmt FROM @add_weeks_json_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
