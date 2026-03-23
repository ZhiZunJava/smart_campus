INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2230, '学习任务', 2004, 5, 'learningTask', 'campus/learningTask/index', '', '', 1, 0, 'C', '0', '0', 'campus:learningTask:list', 'ri-task-line', 'admin', NOW(), '', NULL, '学习任务与任务派发管理'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2230);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2231, '任务查询', 2230, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:list', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2231);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2232, '任务详情', 2230, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:query', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2232);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2233, '任务新增', 2230, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:add', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2233);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2234, '任务修改', 2230, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:edit', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2234);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2235, '任务删除', 2230, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:remove', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2235);

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2236, '任务派发', 2230, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:dispatch', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2236);
