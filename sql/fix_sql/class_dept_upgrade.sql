SET NAMES utf8mb4;

-- 班级增加所属部门，便于教学基础页面按部门筛选班级

SET @has_dept_id := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sc_class'
      AND COLUMN_NAME = 'dept_id'
);

SET @add_dept_sql := IF(
    @has_dept_id = 0,
    'ALTER TABLE sc_class ADD COLUMN dept_id BIGINT NULL COMMENT ''所属部门ID'' AFTER grade_id',
    'SELECT 1'
);

PREPARE stmt FROM @add_dept_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
