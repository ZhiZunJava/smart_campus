-- 专业字典与用户档案专业字段升级脚本
-- 用途：
-- 1. 确保 sc_user_profile 存在 major 字段
-- 2. 初始化“专业”字典 campus_major_type
-- 3. 供后台系统用户页面按专业选择与检索使用

-- 1) 确保专业字段存在
SET @major_column_exists := (
  SELECT COUNT(1)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sc_user_profile'
    AND COLUMN_NAME = 'major'
);

SET @major_column_sql := IF(
  @major_column_exists = 0,
  'ALTER TABLE sc_user_profile ADD COLUMN major varchar(100) DEFAULT NULL COMMENT ''专业'' AFTER class_id',
  'SELECT ''sc_user_profile.major already exists'' AS message'
);

PREPARE stmt_major_column FROM @major_column_sql;
EXECUTE stmt_major_column;
DEALLOCATE PREPARE stmt_major_column;

-- 2) 初始化字典类型
INSERT INTO sys_dict_type (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`)
SELECT '校园专业', 'campus_major_type', '0', 'admin', NOW(), '智慧校园学生/教师专业与方向字典'
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_type WHERE dict_type = 'campus_major_type'
);

-- 3) 初始化字典数据
INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 1, '计算机应用技术', '计算机应用技术', 'campus_major_type', '', 'primary', 'Y', '0', 'admin', NOW(), '默认专业'
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'campus_major_type' AND dict_value = '计算机应用技术'
);

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 2, '软件技术', '软件技术', 'campus_major_type', '', 'success', 'N', '0', 'admin', NOW(), '专业字典'
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'campus_major_type' AND dict_value = '软件技术'
);

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 3, '大数据技术', '大数据技术', 'campus_major_type', '', 'warning', 'N', '0', 'admin', NOW(), '专业字典'
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'campus_major_type' AND dict_value = '大数据技术'
);

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 4, '人工智能技术应用', '人工智能技术应用', 'campus_major_type', '', 'danger', 'N', '0', 'admin', NOW(), '专业字典'
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'campus_major_type' AND dict_value = '人工智能技术应用'
);

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 5, '计算机网络技术', '计算机网络技术', 'campus_major_type', '', 'info', 'N', '0', 'admin', NOW(), '专业字典'
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'campus_major_type' AND dict_value = '计算机网络技术'
);

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 6, '信息安全技术应用', '信息安全技术应用', 'campus_major_type', '', 'primary', 'N', '0', 'admin', NOW(), '专业字典'
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'campus_major_type' AND dict_value = '信息安全技术应用'
);

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 7, '物联网应用技术', '物联网应用技术', 'campus_major_type', '', 'success', 'N', '0', 'admin', NOW(), '专业字典'
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'campus_major_type' AND dict_value = '物联网应用技术'
);

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 8, '数字媒体技术', '数字媒体技术', 'campus_major_type', '', 'warning', 'N', '0', 'admin', NOW(), '专业字典'
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'campus_major_type' AND dict_value = '数字媒体技术'
);

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 9, '电子信息工程技术', '电子信息工程技术', 'campus_major_type', '', 'danger', 'N', '0', 'admin', NOW(), '专业字典'
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'campus_major_type' AND dict_value = '电子信息工程技术'
);

INSERT INTO sys_dict_data (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT 10, '现代通信技术', '现代通信技术', 'campus_major_type', '', 'info', 'N', '0', 'admin', NOW(), '专业字典'
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'campus_major_type' AND dict_value = '现代通信技术'
);

-- 4) 可选：把当前为空的学生专业先回填成默认专业
UPDATE sc_user_profile
SET major = '计算机应用技术'
WHERE (major IS NULL OR TRIM(major) = '')
  AND user_type = 'student';
