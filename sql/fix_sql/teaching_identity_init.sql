SET NAMES utf8mb4;

-- ----------------------------
-- 教务身份岗位 / 角色初始化
-- 说明：
-- 1. 可重复执行
-- 2. 优先补齐门户端依赖的 student / teacher / parent 角色
-- 3. 再补齐班主任、教务员等教务扩展身份
-- ----------------------------

INSERT INTO sys_post (post_code, post_name, post_sort, status, create_by, create_time, remark)
SELECT 'course_teacher', '任课教师', 10, '0', 'admin', NOW(), '教务身份初始化'
WHERE NOT EXISTS (SELECT 1 FROM sys_post WHERE post_code = 'course_teacher');

INSERT INTO sys_post (post_code, post_name, post_sort, status, create_by, create_time, remark)
SELECT 'head_teacher', '班主任', 20, '0', 'admin', NOW(), '教务身份初始化'
WHERE NOT EXISTS (SELECT 1 FROM sys_post WHERE post_code = 'head_teacher');

INSERT INTO sys_post (post_code, post_name, post_sort, status, create_by, create_time, remark)
SELECT 'academic_admin', '教务员', 30, '0', 'admin', NOW(), '教务身份初始化'
WHERE NOT EXISTS (SELECT 1 FROM sys_post WHERE post_code = 'academic_admin');

INSERT INTO sys_post (post_code, post_name, post_sort, status, create_by, create_time, remark)
SELECT 'student_identity', '学生身份', 40, '0', 'admin', NOW(), '教务身份初始化'
WHERE NOT EXISTS (SELECT 1 FROM sys_post WHERE post_code = 'student_identity');

INSERT INTO sys_post (post_code, post_name, post_sort, status, create_by, create_time, remark)
SELECT 'parent_guardian', '家长监护', 50, '0', 'admin', NOW(), '教务身份初始化'
WHERE NOT EXISTS (SELECT 1 FROM sys_post WHERE post_code = 'parent_guardian');

INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time, remark)
SELECT '学生', 'student', 100, '5', '0', '0', 'admin', NOW(), '门户学生角色'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'student' AND del_flag = '0');

INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time, remark)
SELECT '教师', 'teacher', 110, '5', '0', '0', 'admin', NOW(), '门户教师角色'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'teacher' AND del_flag = '0');

INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time, remark)
SELECT '家长', 'parent', 120, '5', '0', '0', 'admin', NOW(), '门户家长角色'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'parent' AND del_flag = '0');

INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time, remark)
SELECT '班主任', 'campus_head_teacher', 130, '4', '0', '0', 'admin', NOW(), '教务班主任角色'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'campus_head_teacher' AND del_flag = '0');

INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time, remark)
SELECT '教务员', 'campus_academic_admin', 140, '1', '0', '0', 'admin', NOW(), '教务管理员角色'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'campus_academic_admin' AND del_flag = '0');
