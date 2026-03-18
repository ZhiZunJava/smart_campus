-- 教室与课表配置升级脚本
-- 说明：
-- 1. 课表布局使用压缩 JSON 保存在 campus.schedule.timeTableLayout
-- 2. 打印分组不再单独存储，前后端会根据节次布局自动推导

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
  UNIQUE KEY uk_classroom_name (classroom_name),
  KEY idx_building_name (building_name),
  KEY idx_campus_name (campus_name),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室表';

ALTER TABLE sc_course_schedule
  ADD COLUMN IF NOT EXISTS classroom_id BIGINT DEFAULT NULL COMMENT '教室ID（关联sc_classroom表）' AFTER class_course_id;

ALTER TABLE sc_course_schedule
  ADD INDEX idx_classroom_id (classroom_id);

INSERT INTO sys_config(config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '课表布局JSON', 'campus.schedule.timeTableLayout',
       '{"id":1,"n":"默认课表布局","e":"Default time table layout","u":[["1",1,900,940,"M"],["2",2,950,1030,"M"],["3",3,1040,1120,"M"],["4",4,1130,1210,"M"],["中午",5,1215,1240,"N"],["中午",6,1245,1315,"N"],["5",7,1320,1400,"A"],["6",8,1410,1450,"A"],["7",9,1500,1540,"A"],["8",10,1550,1630,"A"],["9",11,1830,1910,"E"],["10",12,1920,2000,"E"]]}',
       'Y', 'admin', NOW(), '校园课表默认布局'
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'campus.schedule.timeTableLayout');
