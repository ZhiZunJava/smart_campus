SET NAMES utf8mb4;

-- ----------------------------
-- 选课功能升级脚本
-- 目标：
-- 1. 课程学生关系绑定到班级课程（class_course_id）
-- 2. 支持同一学生跨学期重复修读同一课程
-- 3. 为学生端真实选课、退课和教师查看选课学生提供底层支撑
-- ----------------------------

ALTER TABLE sc_course_student
  ADD COLUMN IF NOT EXISTS class_course_id BIGINT NULL COMMENT '班级课程ID' AFTER id;

UPDATE sc_course_student s
JOIN sc_class_course cc
  ON cc.course_id = s.course_id
 AND cc.class_id = s.class_id
SET s.class_course_id = cc.id
WHERE s.class_course_id IS NULL;

ALTER TABLE sc_course_student
  DROP INDEX uk_sc_course_student;

ALTER TABLE sc_course_student
  ADD UNIQUE INDEX uk_sc_course_student (class_course_id, student_user_id),
  ADD INDEX idx_sc_course_student_class_course_id (class_course_id);

-- 兜底检查：如果仍有未回填 class_course_id 的旧数据，需要人工确认匹配到哪一个教学班
SELECT id, course_id, class_id, student_user_id
FROM sc_course_student
WHERE class_course_id IS NULL;
