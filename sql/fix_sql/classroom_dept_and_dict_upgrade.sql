SET NAMES utf8mb4;

-- 教室增加所属部门，并补齐校区/教室类型字典

SET @has_dept_id := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sc_classroom'
      AND COLUMN_NAME = 'dept_id'
);

SET @add_dept_sql := IF(
    @has_dept_id = 0,
    'ALTER TABLE sc_classroom ADD COLUMN dept_id BIGINT NULL COMMENT ''所属部门ID'' AFTER building_name',
    'SELECT 1'
);

PREPARE stmt FROM @add_dept_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

INSERT INTO sys_dict_type(dict_name, dict_type, status, create_by, create_time, remark)
SELECT '校园校区', 'campus_area_type', '0', 'admin', NOW(), '教室和教学任务使用的校区字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'campus_area_type');

INSERT INTO sys_dict_type(dict_name, dict_type, status, create_by, create_time, remark)
SELECT '教室类型', 'classroom_room_type', '0', 'admin', NOW(), '教室管理使用的类型字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'classroom_room_type');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 1, '本部', '本部', 'campus_area_type', '', 'primary', 'Y', '0', 'admin', NOW(), '默认校区'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='campus_area_type' AND dict_value='本部');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 2, '东校区', '东校区', 'campus_area_type', '', 'success', 'N', '0', 'admin', NOW(), '校区字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='campus_area_type' AND dict_value='东校区');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 3, '西校区', '西校区', 'campus_area_type', '', 'warning', 'N', '0', 'admin', NOW(), '校区字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='campus_area_type' AND dict_value='西校区');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 1, '普通教室', '普通教室', 'classroom_room_type', '', 'primary', 'Y', '0', 'admin', NOW(), '默认教室类型'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='classroom_room_type' AND dict_value='普通教室');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 2, '机房', '机房', 'classroom_room_type', '', 'success', 'N', '0', 'admin', NOW(), '教室类型字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='classroom_room_type' AND dict_value='机房');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 3, '实验室', '实验室', 'classroom_room_type', '', 'warning', 'N', '0', 'admin', NOW(), '教室类型字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='classroom_room_type' AND dict_value='实验室');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 4, '报告厅', '报告厅', 'classroom_room_type', '', 'danger', 'N', '0', 'admin', NOW(), '教室类型字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='classroom_room_type' AND dict_value='报告厅');
