SET NAMES utf8mb4;

-- ----------------------------
-- 废弃功能菜单清理脚本
-- 说明：
-- 1. 用于已经执行过旧版智慧校园菜单脚本的环境
-- 2. 清理以下已废弃功能对应的后台菜单与按钮权限：
--    行为记录、学习画像、个性推荐、学情预警、学情报告
-- 3. 默认只删除菜单与角色菜单关系，不删除业务表结构
-- ----------------------------

START TRANSACTION;

-- 1. 删除按钮权限与角色授权
DELETE FROM sys_role_menu
WHERE menu_id IN (
  2041, 2042, 2043, 2070, 2071,
  2120, 2121, 2130, 2131, 2140, 2141, 2150, 2151, 2152, 2153
);

DELETE FROM sys_menu
WHERE menu_id IN (
  2120, 2121, 2130, 2131, 2140, 2141, 2150, 2151, 2152, 2153
);

-- 2. 删除页面级菜单
DELETE FROM sys_menu
WHERE menu_id IN (2041, 2042, 2043, 2070, 2071);

-- 3. 兜底按权限标识删除，兼容部分环境中菜单 ID 不一致的情况
DELETE FROM sys_role_menu
WHERE menu_id IN (
  SELECT menu_id
  FROM sys_menu
  WHERE perms IN (
    'campus:studyRecord:list',
    'campus:studyRecord:query',
    'campus:studyRecord:add',
    'campus:studyRecord:edit',
    'campus:studyRecord:remove',
    'campus:learningProfile:list',
    'campus:learningProfile:query',
    'campus:learningProfile:add',
    'campus:learningProfile:edit',
    'campus:learningProfile:remove',
    'campus:learningRecommendation:list',
    'campus:learningRecommendation:query',
    'campus:learningRecommendation:add',
    'campus:learningRecommendation:edit',
    'campus:learningRecommendation:remove',
    'campus:analysis:warning:list',
    'campus:analysis:warning:add',
    'campus:analysis:warning:edit',
    'campus:analysis:report:list',
    'campus:analysis:report:add',
    'campus:analysis:report:view'
  )
);

DELETE FROM sys_menu
WHERE perms IN (
  'campus:studyRecord:list',
  'campus:studyRecord:query',
  'campus:studyRecord:add',
  'campus:studyRecord:edit',
  'campus:studyRecord:remove',
  'campus:learningProfile:list',
  'campus:learningProfile:query',
  'campus:learningProfile:add',
  'campus:learningProfile:edit',
  'campus:learningProfile:remove',
  'campus:learningRecommendation:list',
  'campus:learningRecommendation:query',
  'campus:learningRecommendation:add',
  'campus:learningRecommendation:edit',
  'campus:learningRecommendation:remove',
  'campus:analysis:warning:list',
  'campus:analysis:warning:add',
  'campus:analysis:warning:edit',
  'campus:analysis:report:list',
  'campus:analysis:report:add',
  'campus:analysis:report:view'
);

-- 4. 删除旧路径残留，兼容部分手工改过 menu_id 但未改 component 的情况
DELETE FROM sys_role_menu
WHERE menu_id IN (
  SELECT menu_id
  FROM sys_menu
  WHERE component IN (
    'campus/studyRecord/index',
    'campus/learningProfile/index',
    'campus/learningRecommendation/index',
    'campus/analysis/warning/index',
    'campus/analysis/report/index'
  )
);

DELETE FROM sys_menu
WHERE component IN (
  'campus/studyRecord/index',
  'campus/learningProfile/index',
  'campus/learningRecommendation/index',
  'campus/analysis/warning/index',
  'campus/analysis/report/index'
);

-- 5. 如果“学情分析”目录下已经没有任何子菜单，则一并删除空目录
DELETE FROM sys_role_menu
WHERE menu_id = 2007
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE parent_id = 2007
  );

DELETE FROM sys_menu
WHERE menu_id = 2007
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE parent_id = 2007
  );

COMMIT;
