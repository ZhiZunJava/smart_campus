-- 智能测评菜单命名与排序优化
-- 目标：
-- 1. 将旧“题库管理”改名为更准确的“题目管理”
-- 2. 将新“题库目录”命名为“独立题库”
-- 3. 统一智能测评子菜单排序
-- 执行前请先备份数据库

-- 旧题库管理页面本质上是题目管理页
UPDATE sys_menu
SET menu_name = '题目管理',
    remark = '题目管理'
WHERE menu_id = 2060
   OR (parent_id = 2006 AND path = 'questionBank');

-- 新增的独立题库页采用更直观名称
UPDATE sys_menu
SET menu_name = '独立题库',
    remark = '独立题库管理'
WHERE menu_id = 2065
   OR (parent_id = 2006 AND path = 'questionCatalog');

-- 统一子菜单排序
UPDATE sys_menu SET order_num = '1' WHERE menu_id = 2065 OR (parent_id = 2006 AND path = 'questionCatalog');
UPDATE sys_menu SET order_num = '2' WHERE menu_id = 2060 OR (parent_id = 2006 AND path = 'questionBank');
UPDATE sys_menu SET order_num = '3' WHERE menu_id = 2061 OR (parent_id = 2006 AND path = 'questionOption');
UPDATE sys_menu SET order_num = '4' WHERE menu_id = 2062 OR (parent_id = 2006 AND path = 'examPaper');
UPDATE sys_menu SET order_num = '5' WHERE menu_id = 2066 OR (parent_id = 2006 AND path = 'examStrategy');
UPDATE sys_menu SET order_num = '6' WHERE menu_id = 2063 OR (parent_id = 2006 AND path = 'examRecord');
UPDATE sys_menu SET order_num = '7' WHERE menu_id = 2064 OR (parent_id = 2006 AND path = 'wrongQuestionBook');
