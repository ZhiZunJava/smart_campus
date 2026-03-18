SET NAMES utf8mb4;

-- 教务基础菜单与课程表结构瘦身脚本
-- 目标：
-- 1. 删除“课程章节”菜单及其按钮权限
-- 2. 调整教学基础菜单顺序，避免中间空档
-- 3. 移除 sc_course.teacher_id，课程主数据不再维护教师

START TRANSACTION;

DELETE FROM sys_role_menu
WHERE menu_id IN (2033, 2192, 2193, 2194, 2195);

DELETE FROM sys_menu
WHERE menu_id IN (2033, 2192, 2193, 2194, 2195);

UPDATE sys_menu SET order_num = '4' WHERE menu_id = 2034;
UPDATE sys_menu SET order_num = '5' WHERE menu_id = 2035;
UPDATE sys_menu SET order_num = '6' WHERE menu_id = 2036;
UPDATE sys_menu SET order_num = '7' WHERE menu_id = 2037;
UPDATE sys_menu SET order_num = '8' WHERE menu_id = 2038;
UPDATE sys_menu SET order_num = '9' WHERE menu_id = 2039;
UPDATE sys_menu SET order_num = '10' WHERE menu_id = 2045;
UPDATE sys_menu SET order_num = '11' WHERE menu_id = 2044;
UPDATE sys_menu SET order_num = '12' WHERE menu_id = 2046;

SET @has_teacher_col := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sc_course'
      AND COLUMN_NAME = 'teacher_id'
);

SET @drop_teacher_sql := IF(
    @has_teacher_col > 0,
    'ALTER TABLE sc_course DROP COLUMN teacher_id',
    'SELECT 1'
);

PREPARE stmt FROM @drop_teacher_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

COMMIT;
