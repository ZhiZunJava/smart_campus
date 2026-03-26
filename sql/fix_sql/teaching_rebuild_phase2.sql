-- 学期表：存储学校学年学期信息
CREATE TABLE IF NOT EXISTS sc_school_term (
  term_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学期ID',
  term_name VARCHAR(64) NOT NULL COMMENT '学期名称（如：2025-2026学年第一学期）',
  school_year VARCHAR(32) NOT NULL COMMENT '学年（如：2025-2026）',
  term_code VARCHAR(64) DEFAULT NULL COMMENT '学期编码（自定义规则）',
  sort_order INT DEFAULT 0 COMMENT '排序序号',
  start_date DATE DEFAULT NULL COMMENT '学期开始日期',
  end_date DATE DEFAULT NULL COMMENT '学期结束日期',
  total_weeks INT DEFAULT 20 COMMENT '总周数',
  is_current CHAR(1) DEFAULT '0' COMMENT '是否当前学期：0-否 1-是',
  status CHAR(1) DEFAULT '0' COMMENT '状态：0-禁用 1-启用',
  create_by VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  -- 新增索引：优化学年+学期查询、当前学期查询
  INDEX idx_school_year (school_year),
  INDEX idx_is_current (is_current),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校学期表';

-- 班级课程关联表：存储班级-课程-教师的关联关系
CREATE TABLE IF NOT EXISTS sc_class_course (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  class_id BIGINT NOT NULL COMMENT '班级ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  teacher_id BIGINT DEFAULT NULL COMMENT '授课教师ID',
  term_id BIGINT DEFAULT NULL COMMENT '学期ID',
  weekly_hours INT DEFAULT 0 COMMENT '每周课时数',
  selection_option_name VARCHAR(100) NOT NULL DEFAULT '' COMMENT '专项名称/子项目名称',
  selection_group_code VARCHAR(64) DEFAULT NULL COMMENT '专项选课分组编码',
  selection_group_name VARCHAR(100) DEFAULT NULL COMMENT '专项选课分组名称',
  selection_group_limit INT DEFAULT 1 COMMENT '组内限选门数',
  student_limit INT DEFAULT 0 COMMENT '学生人数上限',
  status CHAR(1) DEFAULT '0' COMMENT '状态：0-禁用 1-启用',
  create_by VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  -- 新增索引：优化班级/课程/学期/教师的查询
  INDEX idx_class_id (class_id),
  INDEX idx_course_id (course_id),
  INDEX idx_term_id (term_id),
  INDEX idx_teacher_id (teacher_id),
  -- 新增联合唯一索引：避免同一班级同一学期重复添加同一课程
  UNIQUE INDEX uk_class_course_term_option (class_id, course_id, term_id, selection_option_name),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级课程关联表';

-- 教室表：统一维护校区、楼栋、教室和容量等信息
CREATE TABLE IF NOT EXISTS sc_classroom (
  classroom_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教室ID',
  classroom_name VARCHAR(128) NOT NULL COMMENT '教室名称',
  building_name VARCHAR(64) DEFAULT NULL COMMENT '楼栋名称',
  campus_name VARCHAR(64) DEFAULT NULL COMMENT '校区名称',
  capacity INT DEFAULT 0 COMMENT '容量',
  room_type VARCHAR(64) DEFAULT NULL COMMENT '教室类型',
  sort_order INT DEFAULT 0 COMMENT '排序',
  status CHAR(1) DEFAULT '0' COMMENT '状态：0-正常 1-停用',
  create_by VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  UNIQUE INDEX uk_classroom_name (classroom_name),
  INDEX idx_building_name (building_name),
  INDEX idx_campus_name (campus_name),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室表';

-- 课程排课表：存储具体的上课时间、教室等排课信息
CREATE TABLE IF NOT EXISTS sc_course_schedule (
  schedule_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '排课ID',
  term_id BIGINT NOT NULL COMMENT '学期ID',
  class_course_id BIGINT NOT NULL COMMENT '班级课程关联ID（关联sc_class_course表）',
  classroom_id BIGINT DEFAULT NULL COMMENT '教室ID（关联sc_classroom表）',
  week_day INT NOT NULL COMMENT '星期几：1-周一 2-周二 ... 7-周日',
  start_section INT NOT NULL COMMENT '开始节次（如：1）',
  end_section INT NOT NULL COMMENT '结束节次（如：2）',
  classroom VARCHAR(128) DEFAULT NULL COMMENT '教室名称',
  weeks_text VARCHAR(128) DEFAULT NULL COMMENT '上课周次（如：1-18周）',
  status CHAR(1) DEFAULT '0' COMMENT '状态：0-禁用 1-启用',
  create_by VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  -- 新增索引：优化排课查询
  INDEX idx_term_id (term_id),
  INDEX idx_class_course_id (class_course_id),
  INDEX idx_classroom_id (classroom_id),
  INDEX idx_week_day (week_day),
  -- 新增联合索引：避免同一教室同一时间重复排课（可选，根据业务需求）
  INDEX idx_classroom_time (classroom, week_day, start_section, end_section),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程排课表';

-- 可选：添加外键约束（保证数据完整性，根据业务场景选择是否添加）
ALTER TABLE sc_class_course 
ADD CONSTRAINT fk_class_course_term FOREIGN KEY (term_id) REFERENCES sc_school_term(term_id) ON DELETE SET NULL;

ALTER TABLE sc_course_schedule 
ADD CONSTRAINT fk_schedule_term FOREIGN KEY (term_id) REFERENCES sc_school_term(term_id) ON DELETE CASCADE,
ADD CONSTRAINT fk_schedule_class_course FOREIGN KEY (class_course_id) REFERENCES sc_class_course(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_schedule_classroom FOREIGN KEY (classroom_id) REFERENCES sc_classroom(classroom_id) ON DELETE SET NULL;
