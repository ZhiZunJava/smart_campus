/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : smart_campus

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 28/03/2026 15:03:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由名称',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2272 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '基础设置', 0, 1, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'ri-settings-3-line', 'admin', '2026-03-10 19:39:27', 'admin', '2026-03-12 13:13:37', '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', '', 1, 0, 'M', '0', '0', '', 'ri-line-chart-line', 'admin', '2026-03-10 19:39:27', '', NULL, '系统监控目录');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'ri-user-3-line', 'admin', '2026-03-10 19:39:27', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '', 1, 0, 'C', '0', '0', 'system:role:list', 'ri-team-line', 'admin', '2026-03-10 19:39:27', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'ri-node-tree', 'admin', '2026-03-10 19:39:27', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'ri-git-branch-line', 'admin', '2026-03-10 19:39:27', '', NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', '', 1, 0, 'C', '0', '0', 'system:post:list', 'ri-briefcase-4-line', 'admin', '2026-03-10 19:39:27', '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'ri-book-2-line', 'admin', '2026-03-10 19:39:27', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', '', 1, 0, 'C', '0', '0', 'system:config:list', 'ri-edit-box-line', 'admin', '2026-03-10 19:39:27', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'ri-message-3-line', 'admin', '2026-03-10 19:39:27', '', NULL, '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-file-list-3-line', 'admin', '2026-03-10 19:39:27', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'ri-user-search-line', 'admin', '2026-03-10 19:39:27', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'ri-timer-line', 'admin', '2026-03-10 19:39:27', '', NULL, '定时任务菜单');
INSERT INTO `sys_menu` VALUES (111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'ri-database-2-line', 'admin', '2026-03-10 19:39:27', '', NULL, '数据监控菜单');
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'ri-server-line', 'admin', '2026-03-10 19:39:27', '', NULL, '服务监控菜单');
INSERT INTO `sys_menu` VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'ri-stack-line', 'admin', '2026-03-10 19:39:27', '', NULL, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES (114, '缓存列表', 2, 6, 'cacheList', 'monitor/cache/list', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'ri-file-list-3-line', 'admin', '2026-03-10 19:39:27', '', NULL, '缓存列表菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'ri-file-text-line', 'admin', '2026-03-10 19:39:27', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'ri-login-box-line', 'admin', '2026-03-10 19:39:27', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1000, '用户查询', 100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1001, '用户新增', 100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户修改', 100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户删除', 100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户导出', 100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导入', 100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '重置密码', 100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '角色查询', 101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色新增', 101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色修改', 101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色删除', 101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色导出', 101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '菜单查询', 102, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单新增', 102, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单修改', 102, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单删除', 102, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '部门查询', 103, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, '部门新增', 103, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, '部门修改', 103, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, '部门删除', 103, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, '岗位查询', 104, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, '岗位新增', 104, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, '岗位修改', 104, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '岗位删除', 104, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '岗位导出', 104, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '字典查询', 105, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典新增', 105, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典修改', 105, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典删除', 105, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典导出', 105, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '参数查询', 106, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数新增', 106, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数修改', 106, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数删除', 106, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数导出', 106, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '公告查询', 107, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告新增', 107, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告修改', 107, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告删除', 107, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '操作查询', 500, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作删除', 500, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '日志导出', 500, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '登录查询', 501, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录删除', 501, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '日志导出', 501, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '账户解锁', 501, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '批量强退', 109, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1049, '任务查询', 110, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1050, '任务新增', 110, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1051, '任务修改', 110, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1052, '任务删除', 110, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1053, '状态修改', 110, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1054, '任务导出', 110, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2026-03-10 19:39:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2000, '智慧校园', 0, 10, 'campus', NULL, '', '', 1, 0, 'M', '0', '0', '', 'ri-graduation-cap-line', 'admin', '2026-03-17 18:32:10', '', NULL, '智慧校园主目录');
INSERT INTO `sys_menu` VALUES (2001, '首页概览', 2000, 1, 'overview', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-dashboard-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '首页与多端概览');
INSERT INTO `sys_menu` VALUES (2002, '用户与身份', 2000, 2, 'identity', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-user-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '用户扩展与家长绑定');
INSERT INTO `sys_menu` VALUES (2003, '教学基础', 2000, 3, 'teaching', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-node-tree', 'admin', '2026-03-17 18:32:10', '', NULL, '年级班级课程基础数据');
INSERT INTO `sys_menu` VALUES (2004, '学习中心', 2000, 4, 'learning', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-graduation-cap-line', 'admin', '2026-03-17 18:32:10', '', NULL, '资源行为画像推荐');
INSERT INTO `sys_menu` VALUES (2005, '智能问答', 2000, 5, 'qa', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-chat-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '问答与反馈');
INSERT INTO `sys_menu` VALUES (2006, '智能测评', 2000, 6, 'exam', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-ai-generate-2', 'admin', '2026-03-17 18:32:10', '', NULL, '题库试卷考试错题');
INSERT INTO `sys_menu` VALUES (2008, 'AI管理', 2000, 8, 'ai', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-robot-2-line', 'admin', '2026-03-17 18:32:10', '', NULL, '模型模板日志');
INSERT INTO `sys_menu` VALUES (2010, '概览面板', 2001, 1, 'dashboard', 'campus/overview/index', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'ri-dashboard-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '统一概览页面');
INSERT INTO `sys_menu` VALUES (2011, '学生概览', 2001, 2, 'studentDashboard', 'campus/overview/student', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'ri-user-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '学生端概览');
INSERT INTO `sys_menu` VALUES (2012, '教师概览', 2001, 3, 'teacherDashboard', 'campus/overview/teacher', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'ri-team-line', 'admin', '2026-03-17 18:32:10', '', NULL, '教师端概览');
INSERT INTO `sys_menu` VALUES (2013, '家长概览', 2001, 4, 'parentDashboard', 'campus/overview/parent', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'ri-parent-line', 'admin', '2026-03-17 18:32:10', '', NULL, '家长端概览');
INSERT INTO `sys_menu` VALUES (2020, '用户档案', 2002, 1, 'userProfile', 'campus/userProfile/index', '', '', 1, 0, 'C', '0', '0', 'campus:userProfile:list', 'ri-account-box-line', 'admin', '2026-03-17 18:32:10', '', NULL, '用户扩展档案');
INSERT INTO `sys_menu` VALUES (2021, '家长绑定', 2002, 2, 'parentStudentRel', 'campus/parentStudentRel/index', '', '', 1, 0, 'C', '0', '0', 'campus:parentStudentRel:list', 'ri-links-line', 'admin', '2026-03-17 18:32:10', '', NULL, '家长学生绑定');
INSERT INTO `sys_menu` VALUES (2022, '登录风险', 2002, 3, 'loginRiskEvent', 'campus/loginRiskEvent/index', '', '', 1, 0, 'C', '0', '0', 'campus:loginRiskEvent:list', 'ri-shield-user-line', 'admin', '2026-03-17 18:32:10', '', NULL, '登录风险事件');
INSERT INTO `sys_menu` VALUES (2030, '年级管理', 2003, 1, 'grade', 'campus/grade/index', '', '', 1, 0, 'C', '0', '0', 'campus:grade:list', 'ri-git-branch-line', 'admin', '2026-03-17 18:32:10', '', NULL, '年级管理');
INSERT INTO `sys_menu` VALUES (2031, '班级管理', 2003, 2, 'class', 'campus/class/index', '', '', 1, 0, 'C', '0', '0', 'campus:class:list', 'ri-organization-chart', 'admin', '2026-03-17 18:32:10', '', NULL, '班级管理');
INSERT INTO `sys_menu` VALUES (2032, '课程管理', 2003, 3, 'course', 'campus/course/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'ri-book-open-line', 'admin', '2026-03-17 18:32:10', '', NULL, '课程管理');
INSERT INTO `sys_menu` VALUES (2034, '选课关系', 2003, 4, 'courseStudent', 'campus/courseStudent/index', '', '', 1, 0, 'C', '0', '0', 'campus:courseStudent:list', 'ri-links-line', 'admin', '2026-03-17 18:32:10', '', NULL, '课程学生关系');
INSERT INTO `sys_menu` VALUES (2035, '知识点管理', 2003, 5, 'knowledgePoint', 'campus/knowledgePoint/index', '', '', 1, 0, 'C', '0', '0', 'campus:knowledgePoint:list', 'ri-book-marked-line', 'admin', '2026-03-17 18:32:10', '', NULL, '知识点管理');
INSERT INTO `sys_menu` VALUES (2036, '班级学生', 2003, 6, 'classStudent', 'campus/classStudent/index', '', '', 1, 0, 'C', '0', '0', 'campus:userProfile:list', 'ri-group-line', 'admin', '2026-03-17 18:32:10', '', NULL, '班级学生关系');
INSERT INTO `sys_menu` VALUES (2037, '学年学期', 2003, 7, 'schoolTerm', 'campus/schoolTerm/index', '', '', 1, 0, 'C', '0', '0', 'campus:grade:list', 'ri-calendar-schedule-line', 'admin', '2026-03-17 18:32:10', '', NULL, '学年学期管理');
INSERT INTO `sys_menu` VALUES (2038, '班级课程', 2003, 8, 'classCourse', 'campus/classCourse/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'ri-book-2-line', 'admin', '2026-03-17 18:32:10', '', NULL, '班级课程关系');
INSERT INTO `sys_menu` VALUES (2039, '排课表', 2003, 9, 'courseSchedule', 'campus/courseSchedule/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'ri-calendar-view', 'admin', '2026-03-17 18:32:10', '', NULL, '排课表管理');
INSERT INTO `sys_menu` VALUES (2040, '教室管理', 2003, 11, 'classroom', 'campus/classroom/index', '', '', 1, 0, 'C', '0', '0', 'campus:classroom:list', 'ri-building-line', 'admin', '2026-03-17 18:32:10', '', NULL, '统一维护教室、楼栋与校区');
INSERT INTO `sys_menu` VALUES (2044, '班级课表', 2003, 11, 'classSchedule', 'campus/classSchedule/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'ri-table-view', 'admin', '2026-03-17 18:32:10', '', NULL, '班级课表视图');
INSERT INTO `sys_menu` VALUES (2045, '资源管理', 2004, 10, 'resource', 'campus/resource/index', '', '', 1, 0, 'C', '0', '0', 'campus:resource:list', 'ri-article-line', 'admin', '2026-03-17 18:32:10', '', NULL, '学习资源管理');
INSERT INTO `sys_menu` VALUES (2046, '节次布局', 2003, 12, 'timeTableLayout', 'campus/timeTableLayout/index', '', '', 1, 0, 'C', '0', '0', 'campus:timetable:list', 'ri-layout-grid-line', 'admin', '2026-03-17 18:44:01', '', NULL, '课表节次与时间布局配置');
INSERT INTO `sys_menu` VALUES (2047, '个性化选课', 2003, 14, 'courseSelectionRequest', 'campus/courseSelectionRequest/index', '', '', 1, 0, 'C', '0', '0', 'campus:courseSelectionRequest:list', 'ri-file-edit-line', 'admin', '2026-03-26 20:56:25', '', NULL, '个性化选课申请审核');
INSERT INTO `sys_menu` VALUES (2048, '选课计划', 2003, 15, 'courseSelectionPlan', 'campus/courseSelectionPlan/index', '', '', 1, 0, 'C', '0', '0', 'campus:courseSelectionPlan:list', 'ri-timer-line', 'admin', '2026-03-26 21:11:20', '', NULL, '选课开放时间与规则配置');
INSERT INTO `sys_menu` VALUES (2050, '问答会话', 2005, 1, 'qaSession', 'campus/qa/session/index', '', '', 1, 0, 'C', '0', '0', 'campus:qa:session:list', 'ri-message-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '问答会话');
INSERT INTO `sys_menu` VALUES (2051, '问答消息', 2005, 2, 'qaMessage', 'campus/qa/message/index', '', '', 1, 0, 'C', '0', '0', 'campus:qa:message:list', 'ri-chat-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '问答消息');
INSERT INTO `sys_menu` VALUES (2052, '问答反馈', 2005, 3, 'qaFeedback', 'campus/qa/feedback/index', '', '', 1, 0, 'C', '0', '0', 'campus:qa:feedback:add', 'ri-feedback-line', 'admin', '2026-03-17 18:32:10', '', NULL, '问答反馈');
INSERT INTO `sys_menu` VALUES (2060, '题目管理', 2006, 2, 'questionBank', 'campus/exam/question/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:question:list', 'ri-book-open-line', 'admin', '2026-03-17 18:32:10', '', NULL, '题目管理');
INSERT INTO `sys_menu` VALUES (2061, '题目选项', 2006, 3, 'questionOption', 'campus/exam/option/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:question:list', 'ri-file-list-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, '题目选项');
INSERT INTO `sys_menu` VALUES (2062, '试卷管理', 2006, 4, 'examPaper', 'campus/exam/paper/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:paper:list', 'ri-git-merge-line', 'admin', '2026-03-17 18:32:10', '', NULL, '试卷管理');
INSERT INTO `sys_menu` VALUES (2063, '考试记录', 2006, 6, 'examRecord', 'campus/exam/record/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:record:list', 'ri-calendar-2-line', 'admin', '2026-03-17 18:32:10', '', NULL, '考试记录');
INSERT INTO `sys_menu` VALUES (2064, '错题本', 2006, 7, 'wrongQuestionBook', 'campus/exam/wrong/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:record:list', 'ri-edit-box-line', 'admin', '2026-03-17 18:32:10', '', NULL, '错题本');
INSERT INTO `sys_menu` VALUES (2065, '独立题库', 2006, 1, 'questionCatalog', 'campus/exam/catalog/index', '', '', 1, 0, 'C', '0', '0', 'campus:questionCatalog:list', 'ri-folders-line', 'admin', '2026-03-21 18:02:04', '', NULL, '独立题库管理');
INSERT INTO `sys_menu` VALUES (2066, '组卷策略', 2006, 5, 'examStrategy', 'campus/exam/strategy/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:strategy:list', 'ri-flow-chart', 'admin', '2026-03-21 18:02:05', '', NULL, '组卷策略模板管理');
INSERT INTO `sys_menu` VALUES (2067, '课程成绩', 2006, 6, 'studentScore', 'campus/score/index', '', '', 1, 0, 'C', '0', '0', 'campus:score:list', 'ri-award-line', 'admin', '2026-03-26 13:06:27', '', NULL, '课程成绩管理');
INSERT INTO `sys_menu` VALUES (2080, '模型配置', 2008, 1, 'aiModel', 'campus/ai/model/index', '', '', 1, 0, 'C', '0', '0', 'campus:ai:model:list', 'ri-robot-2-line', 'admin', '2026-03-17 18:32:10', '', NULL, 'AI模型配置');
INSERT INTO `sys_menu` VALUES (2081, 'Prompt模板', 2008, 2, 'aiPrompt', 'campus/ai/prompt/index', '', '', 1, 0, 'C', '0', '0', 'campus:ai:prompt:list', 'ri-file-text-line', 'admin', '2026-03-17 18:32:10', '', NULL, 'Prompt模板');
INSERT INTO `sys_menu` VALUES (2082, '任务日志', 2008, 3, 'aiTaskLog', 'campus/ai/task/index', '', '', 1, 0, 'C', '0', '0', 'campus:ai:task:list', 'ri-file-list-3-line', 'admin', '2026-03-17 18:32:10', '', NULL, 'AI任务日志');
INSERT INTO `sys_menu` VALUES (2100, '档案查询', 2020, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2101, '档案新增', 2020, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2102, '档案修改', 2020, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2103, '档案删除', 2020, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2110, '资源查询', 2045, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2111, '资源新增', 2045, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2112, '资源修改', 2045, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2113, '资源删除', 2045, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2160, '题库查询', 2060, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:question:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2161, '题库新增', 2060, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:question:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2162, '试卷查询', 2062, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:paper:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2163, '试卷新增', 2062, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:paper:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2164, '记录查询', 2063, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:record:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2165, '题库查询', 2065, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:questionCatalog:list', '#', 'admin', '2026-03-21 18:02:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2166, '题库新增', 2065, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:questionCatalog:add', '#', 'admin', '2026-03-21 18:02:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2167, '题库编辑', 2065, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:questionCatalog:edit', '#', 'admin', '2026-03-21 18:02:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2168, '题库删除', 2065, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:questionCatalog:remove', '#', 'admin', '2026-03-21 18:02:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2169, '策略查询', 2066, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:strategy:list', '#', 'admin', '2026-03-21 18:02:05', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2170, '模型查询', 2080, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:model:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2171, '模型新增', 2080, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:model:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2172, '模板查询', 2081, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:prompt:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2173, '模板新增', 2081, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:prompt:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2174, '任务日志查询', 2082, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:task:list', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2180, '年级查询', 2030, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2181, '年级新增', 2030, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2182, '年级修改', 2030, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2183, '年级删除', 2030, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2184, '班级查询', 2031, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:class:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2185, '班级新增', 2031, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:class:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2186, '班级修改', 2031, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:class:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2187, '班级删除', 2031, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:class:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2188, '课程查询', 2032, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2189, '课程新增', 2032, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2190, '课程修改', 2032, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2191, '课程删除', 2032, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2196, '选课查询', 2034, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseStudent:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2197, '选课新增', 2034, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseStudent:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2198, '选课修改', 2034, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseStudent:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2199, '选课删除', 2034, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseStudent:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2200, '知识点查询', 2035, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:knowledgePoint:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2201, '知识点新增', 2035, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:knowledgePoint:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2202, '知识点修改', 2035, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:knowledgePoint:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2203, '知识点删除', 2035, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:knowledgePoint:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2204, '班级学生配置', 2036, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2205, '学期查询', 2037, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2206, '学期新增', 2037, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2207, '学期修改', 2037, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2208, '学期删除', 2037, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2209, '班级课程查询', 2038, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2210, '班级课程新增', 2038, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2211, '班级课程修改', 2038, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2212, '班级课程删除', 2038, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2213, '排课查询', 2039, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:query', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2214, '排课新增', 2039, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:add', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2215, '排课修改', 2039, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:edit', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2216, '排课删除', 2039, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:remove', '#', 'admin', '2026-03-17 18:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2221, '教室查询', 2040, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:query', '#', 'admin', '2026-03-17 18:32:10', 'admin', '2026-03-17 18:44:01', '');
INSERT INTO `sys_menu` VALUES (2222, '教室新增', 2040, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:add', '#', 'admin', '2026-03-17 18:32:10', 'admin', '2026-03-17 18:44:01', '');
INSERT INTO `sys_menu` VALUES (2223, '教室修改', 2040, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:edit', '#', 'admin', '2026-03-17 18:32:10', 'admin', '2026-03-17 18:44:01', '');
INSERT INTO `sys_menu` VALUES (2224, '教室删除', 2040, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:remove', '#', 'admin', '2026-03-17 18:32:10', 'admin', '2026-03-17 18:44:01', '');
INSERT INTO `sys_menu` VALUES (2225, '布局查询', 2046, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:timetable:list', '#', 'admin', '2026-03-17 18:32:10', 'admin', '2026-03-17 18:44:01', '');
INSERT INTO `sys_menu` VALUES (2226, '布局修改', 2046, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:timetable:edit', '#', 'admin', '2026-03-17 18:32:10', 'admin', '2026-03-17 18:44:01', '');
INSERT INTO `sys_menu` VALUES (2230, '学习任务', 2004, 5, 'learningTask', 'campus/learningTask/index', '', '', 1, 0, 'C', '0', '0', 'campus:learningTask:list', 'ri-task-line', 'admin', '2026-03-22 16:43:39', '', NULL, '学习任务与任务派发管理');
INSERT INTO `sys_menu` VALUES (2231, '任务查询', 2230, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:list', '#', 'admin', '2026-03-22 16:43:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2232, '任务详情', 2230, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:query', '#', 'admin', '2026-03-22 16:43:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2233, '任务新增', 2230, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:add', '#', 'admin', '2026-03-22 16:43:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2234, '任务修改', 2230, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:edit', '#', 'admin', '2026-03-22 16:43:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2235, '任务删除', 2230, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:remove', '#', 'admin', '2026-03-22 16:43:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2236, '任务派发', 2230, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningTask:dispatch', '#', 'admin', '2026-03-22 16:43:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2237, '申请查询', 2047, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseSelectionRequest:query', '#', 'admin', '2026-03-26 20:56:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2238, '申请审核', 2047, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseSelectionRequest:review', '#', 'admin', '2026-03-26 20:56:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2239, '计划查询', 2048, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseSelectionPlan:query', '#', 'admin', '2026-03-26 21:11:49', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2240, '计划新增', 2048, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseSelectionPlan:add', '#', 'admin', '2026-03-26 21:11:49', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2241, '计划修改', 2048, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseSelectionPlan:edit', '#', 'admin', '2026-03-26 21:11:49', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2242, '计划删除', 2048, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseSelectionPlan:remove', '#', 'admin', '2026-03-26 21:11:49', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2265, '成绩查询', 2067, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:score:query', '#', 'admin', '2026-03-26 13:06:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2267, '成绩修改', 2067, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:score:edit', '#', 'admin', '2026-03-26 13:06:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2268, '成绩删除', 2067, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:score:remove', '#', 'admin', '2026-03-26 13:06:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2269, '成绩发布', 2067, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'campus:score:edit', '#', 'admin', '2026-03-26 13:06:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2270, '教室导出', 2040, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'campus:classroom:export', '#', 'admin', '2026-03-26 19:19:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2271, '教室导入', 2040, 6, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'campus:classroom:import', '#', 'admin', '2026-03-26 19:19:52', '', NULL, '');

SET FOREIGN_KEY_CHECKS = 1;
