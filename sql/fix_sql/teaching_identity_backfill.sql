SET NAMES utf8mb4;

-- ----------------------------
-- 存量用户教务身份回填脚本
-- 说明：
-- 1. 依赖 teaching_identity_init.sql 先执行，确保角色和岗位已存在
-- 2. 根据 sys_user.user_type / 扩展档案中的 user_type 回填角色和岗位
-- 3. 不会重复插入已存在的 user-role / user-post 关系
-- ----------------------------

-- 学生 -> student 角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM sys_user u
JOIN sys_role r ON r.role_key = 'student' AND r.del_flag = '0'
LEFT JOIN sys_user_role ur ON ur.user_id = u.user_id AND ur.role_id = r.role_id
WHERE ur.user_id IS NULL
  AND LOWER(COALESCE(u.user_type, 'student')) = 'student';

-- 教师 -> teacher 角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM sys_user u
JOIN sys_role r ON r.role_key = 'teacher' AND r.del_flag = '0'
LEFT JOIN sys_user_role ur ON ur.user_id = u.user_id AND ur.role_id = r.role_id
WHERE ur.user_id IS NULL
  AND LOWER(COALESCE(u.user_type, 'student')) = 'teacher';

-- 家长 -> parent 角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM sys_user u
JOIN sys_role r ON r.role_key = 'parent' AND r.del_flag = '0'
LEFT JOIN sys_user_role ur ON ur.user_id = u.user_id AND ur.role_id = r.role_id
WHERE ur.user_id IS NULL
  AND LOWER(COALESCE(u.user_type, 'student')) = 'parent';

-- 教师 -> 任课教师岗位
INSERT INTO sys_user_post (user_id, post_id)
SELECT u.user_id, p.post_id
FROM sys_user u
JOIN sys_post p ON p.post_code = 'course_teacher'
LEFT JOIN sys_user_post up ON up.user_id = u.user_id AND up.post_id = p.post_id
WHERE up.user_id IS NULL
  AND LOWER(COALESCE(u.user_type, 'student')) = 'teacher';

-- 学生 -> 学生身份岗位
INSERT INTO sys_user_post (user_id, post_id)
SELECT u.user_id, p.post_id
FROM sys_user u
JOIN sys_post p ON p.post_code = 'student_identity'
LEFT JOIN sys_user_post up ON up.user_id = u.user_id AND up.post_id = p.post_id
WHERE up.user_id IS NULL
  AND LOWER(COALESCE(u.user_type, 'student')) = 'student';

-- 家长 -> 家长监护岗位
INSERT INTO sys_user_post (user_id, post_id)
SELECT u.user_id, p.post_id
FROM sys_user u
JOIN sys_post p ON p.post_code = 'parent_guardian'
LEFT JOIN sys_user_post up ON up.user_id = u.user_id AND up.post_id = p.post_id
WHERE up.user_id IS NULL
  AND LOWER(COALESCE(u.user_type, 'student')) = 'parent';

-- 班主任用户自动挂班主任角色与岗位：如果当前是教师，且在班级表中被引用为 head_teacher_id
INSERT INTO sys_user_role (user_id, role_id)
SELECT DISTINCT c.head_teacher_id, r.role_id
FROM sc_class c
JOIN sys_role r ON r.role_key = 'campus_head_teacher' AND r.del_flag = '0'
LEFT JOIN sys_user_role ur ON ur.user_id = c.head_teacher_id AND ur.role_id = r.role_id
WHERE c.head_teacher_id IS NOT NULL
  AND ur.user_id IS NULL;

INSERT INTO sys_user_post (user_id, post_id)
SELECT DISTINCT c.head_teacher_id, p.post_id
FROM sc_class c
JOIN sys_post p ON p.post_code = 'head_teacher'
LEFT JOIN sys_user_post up ON up.user_id = c.head_teacher_id AND up.post_id = p.post_id
WHERE c.head_teacher_id IS NOT NULL
  AND up.user_id IS NULL;
