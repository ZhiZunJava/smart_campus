SET NAMES utf8mb4;

-- ----------------------------
-- 管理端菜单图标升级为 Remix Icon
-- 说明：
-- 1. 用于把 sys_menu.icon 中的旧 svg key 升级为 ri-* 图标类名
-- 2. 可重复执行；已经是 ri-* 或 # 的值不会受影响
-- 3. 建议在执行前先备份 sys_menu 表
-- ----------------------------

UPDATE `sys_menu`
SET `icon` = CASE `icon`
  WHEN 'system' THEN 'ri-settings-3-line'
  WHEN 'monitor' THEN 'ri-line-chart-line'
  WHEN 'user' THEN 'ri-user-3-line'
  WHEN 'peoples' THEN 'ri-team-line'
  WHEN 'people' THEN 'ri-parent-line'
  WHEN 'tree-table' THEN 'ri-node-tree'
  WHEN 'tree' THEN 'ri-git-branch-line'
  WHEN 'post' THEN 'ri-briefcase-4-line'
  WHEN 'dict' THEN 'ri-book-2-line'
  WHEN 'edit' THEN 'ri-edit-box-line'
  WHEN 'message' THEN 'ri-message-3-line'
  WHEN 'log' THEN 'ri-file-list-3-line'
  WHEN 'online' THEN 'ri-user-search-line'
  WHEN 'job' THEN 'ri-timer-line'
  WHEN 'druid' THEN 'ri-database-2-line'
  WHEN 'server' THEN 'ri-server-line'
  WHEN 'redis' THEN 'ri-stack-line'
  WHEN 'redis-list' THEN 'ri-file-list-3-line'
  WHEN 'form' THEN 'ri-file-text-line'
  WHEN 'logininfor' THEN 'ri-login-box-line'
  WHEN 'education' THEN 'ri-graduation-cap-line'
  WHEN 'dashboard' THEN 'ri-dashboard-3-line'
  WHEN 'build' THEN 'ri-ai-generate-2'
  WHEN 'chart' THEN 'ri-bar-chart-box-line'
  WHEN 'guide' THEN 'ri-road-map-line'
  WHEN 'documentation' THEN 'ri-article-line'
  WHEN 'nested' THEN 'ri-git-merge-line'
  WHEN 'date' THEN 'ri-calendar-2-line'
  WHEN 'warning' THEN 'ri-alert-line'
  WHEN 'clipboard' THEN 'ri-clipboard-line'
  WHEN 'comment' THEN 'ri-feedback-line'
  ELSE `icon`
END
WHERE `icon` IS NOT NULL
  AND `icon` <> ''
  AND `icon` <> '#'
  AND `icon` NOT LIKE 'ri-%';

-- 智慧校园扩展菜单按名称做一轮补齐，避免历史数据使用了不统一图标
UPDATE `sys_menu`
SET `icon` = CASE `menu_name`
  WHEN '智慧校园' THEN 'ri-graduation-cap-line'
  WHEN '智能问答' THEN 'ri-chat-3-line'
  WHEN 'AI管理' THEN 'ri-robot-2-line'
  WHEN '用户档案' THEN 'ri-account-box-line'
  WHEN '家长绑定' THEN 'ri-links-line'
  WHEN '登录风险' THEN 'ri-shield-user-line'
  WHEN '班级管理' THEN 'ri-organization-chart'
  WHEN '课程管理' THEN 'ri-book-open-line'
  WHEN '知识点管理' THEN 'ri-book-marked-line'
  WHEN '学习画像' THEN 'ri-brain-line'
  WHEN '问答会话' THEN 'ri-message-3-line'
  WHEN '问答消息' THEN 'ri-chat-3-line'
  WHEN '题库管理' THEN 'ri-book-open-line'
  WHEN '模型配置' THEN 'ri-robot-2-line'
  ELSE `icon`
END
WHERE `menu_id` BETWEEN 2000 AND 2299;
