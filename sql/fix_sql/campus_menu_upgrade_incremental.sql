SET NAMES utf8mb4;

-- ----------------------------
-- 智慧校园菜单增量升级脚本
-- 说明：
-- 1. 用于已执行过旧版 campus_menu_init.sql 的环境
-- 2. 不做 2000~2299 全量删除，只修复冲突并补齐新增菜单/按钮
-- 3. 默认授权给管理员角色 role_id = 1
-- ----------------------------

-- 1. 修复旧脚本中 2040/2217/2218 的冲突数据
UPDATE sys_menu
SET menu_id = 2045,
    parent_id = 2003,
    order_num = '11',
    menu_name = '教室管理',
    path = 'classroom',
    component = 'campus/classroom/index',
    perms = 'campus:classroom:list',
    icon = 'ri-building-line',
    remark = '统一维护教室、楼栋与校区'
WHERE menu_id = 2040
  AND parent_id = 2003;

UPDATE sys_menu
SET order_num = '12'
WHERE menu_id = 2044
  AND parent_id = 2003;

UPDATE sys_menu
SET menu_id = 2221,
    parent_id = 2045,
    perms = 'campus:classroom:query',
    menu_name = '教室查询'
WHERE menu_id = 2217
  AND parent_id = 2040
  AND menu_type = 'F';

UPDATE sys_menu
SET menu_id = 2222,
    parent_id = 2045,
    perms = 'campus:classroom:add',
    menu_name = '教室新增'
WHERE menu_id = 2218
  AND parent_id = 2040
  AND menu_type = 'F';

UPDATE sys_menu
SET parent_id = 2045
WHERE menu_id = 2219
  AND parent_id = 2040
  AND menu_type = 'F';

UPDATE sys_menu
SET parent_id = 2045
WHERE menu_id = 2220
  AND parent_id = 2040
  AND menu_type = 'F';

-- 2. 补齐教室管理菜单（若不存在）
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache,
  menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT 2045, '教室管理', '2003', '11', 'classroom', 'campus/classroom/index', '', '', 1, 0,
       'C', '0', '0', 'campus:classroom:list', 'ri-building-line', 'admin', NOW(), '', NULL, '统一维护教室、楼栋与校区'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2045);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache,
  menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT 2046, '节次布局', '2003', '13', 'timeTableLayout', 'campus/timeTableLayout/index', '', '', 1, 0,
       'C', '0', '0', 'campus:timetable:list', 'ri-layout-grid-line', 'admin', NOW(), '', NULL, '课表节次与时间布局配置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2046);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache,
  menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT 2047, '个性化选课', '2003', '14', 'courseSelectionRequest', 'campus/courseSelectionRequest/index', '', '', 1, 0,
       'C', '0', '0', 'campus:courseSelectionRequest:list', 'ri-file-edit-line', 'admin', NOW(), '', NULL, '个性化选课申请审核'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2047);

-- 3. 补齐教室管理与节次布局按钮权限
INSERT INTO sys_menu VALUES
(2221, '教室查询', '2045', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:query', '#', 'admin', NOW(), '', NULL, ''),
(2222, '教室新增', '2045', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:add', '#', 'admin', NOW(), '', NULL, ''),
(2223, '教室修改', '2045', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:edit', '#', 'admin', NOW(), '', NULL, ''),
(2224, '教室删除', '2045', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:remove', '#', 'admin', NOW(), '', NULL, ''),
(2227, '教室导出', '2045', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:export', '#', 'admin', NOW(), '', NULL, ''),
(2228, '教室导入', '2045', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:import', '#', 'admin', NOW(), '', NULL, ''),
(2237, '申请查询', '2047', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseSelectionRequest:query', '#', 'admin', NOW(), '', NULL, ''),
(2238, '申请审核', '2047', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseSelectionRequest:review', '#', 'admin', NOW(), '', NULL, ''),
(2225, '布局查询', '2046', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:timetable:list', '#', 'admin', NOW(), '', NULL, ''),
(2226, '布局修改', '2046', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:timetable:edit', '#', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE
parent_id = VALUES(parent_id),
order_num = VALUES(order_num),
perms = VALUES(perms),
menu_name = VALUES(menu_name),
update_by = 'admin',
update_time = NOW(),
remark = VALUES(remark);

-- 4. 管理员授权补齐
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE m.menu_id IN (2045, 2046, 2047, 2221, 2222, 2223, 2224, 2225, 2226, 2227, 2228, 2237, 2238)
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm
    WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id
  );
