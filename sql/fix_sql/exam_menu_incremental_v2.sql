-- 智能测评菜单补充
-- 目标：
-- 1. 挂载独立题库管理页面
-- 2. 挂载组卷策略模板页面
-- 3. 补充对应按钮权限
-- 执行前请先备份数据库

-- 题库目录
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
  2065, '题库目录', 2006, '1.5', 'questionCatalog', 'campus/exam/catalog/index', '', '',
  1, 0, 'C', '0', '0', 'campus:questionCatalog:list', 'ri-folders-line', 'admin', NOW(), '', NULL, '独立题库管理'
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE menu_id = 2065 OR (parent_id = 2006 AND path = 'questionCatalog')
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
  2165, '题库查询', 2065, '1', '', '', '', '',
  1, 0, 'F', '0', '0', 'campus:questionCatalog:list', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE menu_id = 2165
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
  2166, '题库新增', 2065, '2', '', '', '', '',
  1, 0, 'F', '0', '0', 'campus:questionCatalog:add', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE menu_id = 2166
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
  2167, '题库编辑', 2065, '3', '', '', '', '',
  1, 0, 'F', '0', '0', 'campus:questionCatalog:edit', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE menu_id = 2167
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
  2168, '题库删除', 2065, '4', '', '', '', '',
  1, 0, 'F', '0', '0', 'campus:questionCatalog:remove', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE menu_id = 2168
);

-- 组卷策略
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
  2066, '组卷策略', 2006, '3.5', 'examStrategy', 'campus/exam/strategy/index', '', '',
  1, 0, 'C', '0', '0', 'campus:exam:strategy:list', 'ri-flow-chart', 'admin', NOW(), '', NULL, '组卷策略模板管理'
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE menu_id = 2066 OR (parent_id = 2006 AND path = 'examStrategy')
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
  2169, '策略查询', 2066, '1', '', '', '', '',
  1, 0, 'F', '0', '0', 'campus:exam:strategy:list', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE menu_id = 2169
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
  2170, '策略新增', 2066, '2', '', '', '', '',
  1, 0, 'F', '0', '0', 'campus:exam:strategy:add', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE menu_id = 2170
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
  2171, '策略编辑', 2066, '3', '', '', '', '',
  1, 0, 'F', '0', '0', 'campus:exam:strategy:edit', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE menu_id = 2171
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark
)
SELECT
  2172, '策略删除', 2066, '4', '', '', '', '',
  1, 0, 'F', '0', '0', 'campus:exam:strategy:remove', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE menu_id = 2172
);
