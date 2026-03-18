-- 班级课程教学任务详情升级脚本
-- 用途：
-- 1. 扩展 sc_class_course，支持“我的课程”详情弹窗所需字段
-- 2. 供后台班级课程维护、学生端课程详情展示使用

ALTER TABLE sc_class_course
  ADD COLUMN IF NOT EXISTS business_type varchar(30) DEFAULT NULL COMMENT '所属业务类型' AFTER term_id,
  ADD COLUMN IF NOT EXISTS teaching_class_code varchar(64) DEFAULT NULL COMMENT '教学班代码' AFTER business_type,
  ADD COLUMN IF NOT EXISTS credits decimal(8,2) DEFAULT NULL COMMENT '学分' AFTER teaching_class_code,
  ADD COLUMN IF NOT EXISTS course_category varchar(50) DEFAULT NULL COMMENT '课程类别' AFTER credits,
  ADD COLUMN IF NOT EXISTS open_dept_id bigint DEFAULT NULL COMMENT '开课部门ID' AFTER course_category,
  ADD COLUMN IF NOT EXISTS major varchar(100) DEFAULT NULL COMMENT '专业' AFTER open_dept_id,
  ADD COLUMN IF NOT EXISTS campus_name varchar(50) DEFAULT NULL COMMENT '授课校区' AFTER major,
  ADD COLUMN IF NOT EXISTS assessment_type varchar(50) DEFAULT NULL COMMENT '考核方式' AFTER campus_name,
  ADD COLUMN IF NOT EXISTS teaching_language varchar(50) DEFAULT NULL COMMENT '授课语言' AFTER assessment_type,
  ADD COLUMN IF NOT EXISTS prerequisite_course varchar(200) DEFAULT NULL COMMENT '先修课程' AFTER teaching_language,
  ADD COLUMN IF NOT EXISTS task_type varchar(30) DEFAULT NULL COMMENT '任务类型' AFTER prerequisite_course,
  ADD COLUMN IF NOT EXISTS required_flag char(1) DEFAULT NULL COMMENT '是否必修(Y/N)' AFTER task_type,
  ADD COLUMN IF NOT EXISTS course_level_requirement varchar(100) DEFAULT NULL COMMENT '课程等级要求' AFTER required_flag,
  ADD COLUMN IF NOT EXISTS total_hours int DEFAULT NULL COMMENT '要求总课时' AFTER course_level_requirement,
  ADD COLUMN IF NOT EXISTS required_weeks int DEFAULT NULL COMMENT '要求周数' AFTER total_hours,
  ADD COLUMN IF NOT EXISTS arranged_hours int DEFAULT NULL COMMENT '已安排总课时' AFTER weekly_hours;

-- 回填默认值，避免旧数据详情弹窗大量空白
UPDATE sc_class_course
SET business_type = COALESCE(NULLIF(business_type, ''), '专科'),
    teaching_class_code = COALESCE(NULLIF(teaching_class_code, ''), CONCAT((SELECT course_code FROM sc_course WHERE sc_course.course_id = sc_class_course.course_id LIMIT 1), '-', LPAD(id, 3, '0'))),
    credits = COALESCE(credits, GREATEST(1, weekly_hours / 2)),
    course_category = COALESCE(NULLIF(course_category, ''), '理论'),
    major = COALESCE(NULLIF(major, ''), '计算机应用技术'),
    campus_name = COALESCE(NULLIF(campus_name, ''), '本部'),
    assessment_type = COALESCE(NULLIF(assessment_type, ''), '考查'),
    teaching_language = COALESCE(NULLIF(teaching_language, ''), '中文授课'),
    prerequisite_course = COALESCE(NULLIF(prerequisite_course, ''), '无'),
    task_type = COALESCE(NULLIF(task_type, ''), '正常'),
    required_flag = COALESCE(NULLIF(required_flag, ''), 'N'),
    course_level_requirement = COALESCE(NULLIF(course_level_requirement, ''), '无'),
    total_hours = COALESCE(total_hours, weekly_hours * 15),
    required_weeks = COALESCE(required_weeks, 15),
    arranged_hours = COALESCE(arranged_hours, weekly_hours * 15)
WHERE 1 = 1;
