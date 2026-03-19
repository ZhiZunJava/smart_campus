SET NAMES utf8mb4;

-- 排课表补齐教室快照字段，并把历史数据从教室表回填
-- 目标：
-- 1. classroom_id 继续作为主关联
-- 2. classroom_name / building_name / campus_name 作为快照字段兜底
-- 3. 教室改名后可同步刷新快照，教室被删除后历史排课仍可展示

SET @has_classroom_name := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sc_course_schedule'
      AND COLUMN_NAME = 'classroom_name'
);

SET @add_classroom_name_sql := IF(
    @has_classroom_name = 0,
    'ALTER TABLE sc_course_schedule ADD COLUMN classroom_name VARCHAR(128) NULL COMMENT ''教室名称快照'' AFTER classroom',
    'SELECT 1'
);

PREPARE stmt FROM @add_classroom_name_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_building_name := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sc_course_schedule'
      AND COLUMN_NAME = 'building_name'
);

SET @add_building_name_sql := IF(
    @has_building_name = 0,
    'ALTER TABLE sc_course_schedule ADD COLUMN building_name VARCHAR(64) NULL COMMENT ''楼栋名称快照'' AFTER classroom_name',
    'SELECT 1'
);

PREPARE stmt FROM @add_building_name_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_campus_name := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sc_course_schedule'
      AND COLUMN_NAME = 'campus_name'
);

SET @add_campus_name_sql := IF(
    @has_campus_name = 0,
    'ALTER TABLE sc_course_schedule ADD COLUMN campus_name VARCHAR(64) NULL COMMENT ''校区名称快照'' AFTER building_name',
    'SELECT 1'
);

PREPARE stmt FROM @add_campus_name_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE sc_course_schedule s
LEFT JOIN sc_classroom c ON c.classroom_id = s.classroom_id
SET s.classroom = COALESCE(c.classroom_name, s.classroom),
    s.classroom_name = COALESCE(c.classroom_name, s.classroom_name, s.classroom),
    s.building_name = COALESCE(c.building_name, s.building_name),
    s.campus_name = COALESCE(c.campus_name, s.campus_name)
WHERE s.classroom_id IS NOT NULL;
