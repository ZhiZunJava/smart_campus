SET NAMES utf8mb4;

-- 教学楼字典，供教室管理与排课位置展示统一使用

INSERT INTO sys_dict_type(dict_name, dict_type, status, create_by, create_time, remark)
SELECT '教学楼', 'campus_building_type', '0', 'admin', NOW(), '教室管理与排课位置使用的教学楼字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'campus_building_type');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 1, '教学楼A', '教学楼A', 'campus_building_type', '', 'primary', 'Y', '0', 'admin', NOW(), '默认教学楼'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='campus_building_type' AND dict_value='教学楼A');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 2, '教学楼B', '教学楼B', 'campus_building_type', '', 'success', 'N', '0', 'admin', NOW(), '教学楼字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='campus_building_type' AND dict_value='教学楼B');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 3, '实验楼', '实验楼', 'campus_building_type', '', 'warning', 'N', '0', 'admin', NOW(), '教学楼字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='campus_building_type' AND dict_value='实验楼');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 4, '信息楼', '信息楼', 'campus_building_type', '', 'danger', 'N', '0', 'admin', NOW(), '教学楼字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='campus_building_type' AND dict_value='信息楼');
