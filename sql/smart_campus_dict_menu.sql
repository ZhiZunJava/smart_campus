SET NAMES utf8mb4;

-- ----------------------------
-- 智慧校园字典、菜单、角色菜单初始化脚本
-- 说明：
-- 1. 依赖若依 base.sql 已初始化 sys_dict_type/sys_dict_data/sys_menu/sys_role_menu
-- 2. 默认把菜单授权给管理员角色(role_id=1)
-- 3. 菜单 ID 规划在 2000~2099 区间，避免与若依默认数据冲突
-- ----------------------------

-- ============================
-- 一、字典类型与字典数据
-- ============================

DELETE FROM `sys_dict_data` WHERE `dict_type` IN (
  'sc_user_type',
  'sc_resource_type',
  'sc_behavior_type',
  'sc_risk_level',
  'sc_warning_type',
  'sc_question_type',
  'sc_exam_status',
  'sc_recommend_scene'
);

DELETE FROM `sys_dict_type` WHERE `dict_type` IN (
  'sc_user_type',
  'sc_resource_type',
  'sc_behavior_type',
  'sc_risk_level',
  'sc_warning_type',
  'sc_question_type',
  'sc_exam_status',
  'sc_recommend_scene'
);

INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`) VALUES
('智慧校园-用户类型', 'sc_user_type', '0', 'admin', NOW(), '学生/教师/管理员/家长'),
('智慧校园-资源类型', 'sc_resource_type', '0', 'admin', NOW(), '学习资源类型'),
('智慧校园-学习行为', 'sc_behavior_type', '0', 'admin', NOW(), '学习行为类型'),
('智慧校园-风险等级', 'sc_risk_level', '0', 'admin', NOW(), '登录和学情风险等级'),
('智慧校园-预警类型', 'sc_warning_type', '0', 'admin', NOW(), '学情预警类型'),
('智慧校园-题目类型', 'sc_question_type', '0', 'admin', NOW(), '题库题型'),
('智慧校园-考试状态', 'sc_exam_status', '0', 'admin', NOW(), '考试记录状态'),
('智慧校园-推荐场景', 'sc_recommend_scene', '0', 'admin', NOW(), '推荐展示场景');

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`) VALUES
(1, '学生', 'student', 'sc_user_type', '', 'primary', 'Y', '0', 'admin', NOW(), '学生'),
(2, '教师', 'teacher', 'sc_user_type', '', 'success', 'N', '0', 'admin', NOW(), '教师'),
(3, '管理员', 'admin', 'sc_user_type', '', 'danger', 'N', '0', 'admin', NOW(), '管理员'),
(4, '家长', 'parent', 'sc_user_type', '', 'warning', 'N', '0', 'admin', NOW(), '家长'),

(1, '视频', 'video', 'sc_resource_type', '', 'primary', 'Y', '0', 'admin', NOW(), '视频资源'),
(2, '文档', 'doc', 'sc_resource_type', '', 'success', 'N', '0', 'admin', NOW(), '文档资源'),
(3, 'PPT', 'ppt', 'sc_resource_type', '', 'warning', 'N', '0', 'admin', NOW(), 'PPT资源'),
(4, 'PDF', 'pdf', 'sc_resource_type', '', 'danger', 'N', '0', 'admin', NOW(), 'PDF资源'),
(5, '题目', 'question', 'sc_resource_type', '', 'info', 'N', '0', 'admin', NOW(), '题目资源'),

(1, '浏览', 'view', 'sc_behavior_type', '', 'primary', 'Y', '0', 'admin', NOW(), '浏览行为'),
(2, '播放', 'play', 'sc_behavior_type', '', 'success', 'N', '0', 'admin', NOW(), '播放行为'),
(3, '下载', 'download', 'sc_behavior_type', '', 'warning', 'N', '0', 'admin', NOW(), '下载行为'),
(4, '收藏', 'favorite', 'sc_behavior_type', '', 'danger', 'N', '0', 'admin', NOW(), '收藏行为'),
(5, '搜索', 'search', 'sc_behavior_type', '', 'info', 'N', '0', 'admin', NOW(), '搜索行为'),
(6, '答题', 'answer', 'sc_behavior_type', '', 'primary', 'N', '0', 'admin', NOW(), '答题行为'),

(1, '低风险', 'LOW', 'sc_risk_level', '', 'success', 'Y', '0', 'admin', NOW(), '低风险'),
(2, '中风险', 'MEDIUM', 'sc_risk_level', '', 'warning', 'N', '0', 'admin', NOW(), '中风险'),
(3, '高风险', 'HIGH', 'sc_risk_level', '', 'danger', 'N', '0', 'admin', NOW(), '高风险'),

(1, '低活跃', 'low_active', 'sc_warning_type', '', 'warning', 'Y', '0', 'admin', NOW(), '低活跃'),
(2, '成绩下滑', 'score_down', 'sc_warning_type', '', 'danger', 'N', '0', 'admin', NOW(), '成绩下滑'),
(3, '高风险登录', 'high_risk', 'sc_warning_type', '', 'danger', 'N', '0', 'admin', NOW(), '高风险登录'),

(1, '单选题', 'single', 'sc_question_type', '', 'primary', 'Y', '0', 'admin', NOW(), '单选题'),
(2, '多选题', 'multiple', 'sc_question_type', '', 'success', 'N', '0', 'admin', NOW(), '多选题'),
(3, '判断题', 'judge', 'sc_question_type', '', 'warning', 'N', '0', 'admin', NOW(), '判断题'),
(4, '填空题', 'fill', 'sc_question_type', '', 'info', 'N', '0', 'admin', NOW(), '填空题'),
(5, '简答题', 'essay', 'sc_question_type', '', 'danger', 'N', '0', 'admin', NOW(), '简答题'),

(1, '初始化', 'INIT', 'sc_exam_status', '', 'info', 'Y', '0', 'admin', NOW(), '初始化'),
(2, '进行中', 'ONGOING', 'sc_exam_status', '', 'warning', 'N', '0', 'admin', NOW(), '进行中'),
(3, '已提交', 'SUBMITTED', 'sc_exam_status', '', 'success', 'N', '0', 'admin', NOW(), '已提交'),

(1, '首页推荐', 'home', 'sc_recommend_scene', '', 'primary', 'Y', '0', 'admin', NOW(), '首页推荐'),
(2, '考前冲刺', 'exam_before', 'sc_recommend_scene', '', 'warning', 'N', '0', 'admin', NOW(), '考前冲刺'),
(3, '每日计划', 'daily_plan', 'sc_recommend_scene', '', 'success', 'N', '0', 'admin', NOW(), '每日计划');

-- ============================
-- 二、菜单初始化
-- ============================

DELETE FROM `sys_role_menu` WHERE `menu_id` BETWEEN 2000 AND 2099;
DELETE FROM `sys_menu` WHERE `menu_id` BETWEEN 2000 AND 2099;

INSERT INTO `sys_menu` VALUES
(2000, '智慧校园', '0',   '10', 'campus',                NULL,                           '', '', 1, 0, 'M', '0', '0', '',                            'education', 'admin', NOW(), '', NULL, '智慧校园目录'),

(2001, '学习中心', '2000', '1', 'learning',             'campus/learning/index',        '', '', 1, 0, 'C', '0', '0', 'campus:learning:list',        'education', 'admin', NOW(), '', NULL, '学习中心菜单'),
(2002, '资源中心', '2000', '2', 'resource',             'campus/resource/index',        '', '', 1, 0, 'C', '0', '0', 'campus:resource:list',        'documentation', 'admin', NOW(), '', NULL, '资源中心菜单'),
(2003, '学情分析', '2000', '3', 'analysis',             'campus/analysis/index',        '', '', 1, 0, 'C', '0', '0', 'campus:analysis:list',        'chart', 'admin', NOW(), '', NULL, '学情分析菜单'),
(2004, '智能问答', '2000', '4', 'qa',                   'campus/qa/index',              '', '', 1, 0, 'C', '0', '0', 'campus:qa:list',              'message', 'admin', NOW(), '', NULL, '智能问答菜单'),
(2005, '智能测评', '2000', '5', 'exam',                 'campus/exam/index',            '', '', 1, 0, 'C', '0', '0', 'campus:exam:list',            'build', 'admin', NOW(), '', NULL, '智能测评菜单'),
(2006, 'AI配置',   '2000', '6', 'ai',                   'campus/ai/index',              '', '', 1, 0, 'C', '0', '0', 'campus:ai:list',              'edit', 'admin', NOW(), '', NULL, 'AI配置菜单'),

(2011, '学习计划新增', '2001', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learning:plan:add', '#', 'admin', NOW(), '', NULL, ''),

(2020, '资源查询', '2002', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:query', '#', 'admin', NOW(), '', NULL, ''),
(2021, '资源新增', '2002', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:add', '#', 'admin', NOW(), '', NULL, ''),
(2022, '资源修改', '2002', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:edit', '#', 'admin', NOW(), '', NULL, ''),
(2023, '资源删除', '2002', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:remove', '#', 'admin', NOW(), '', NULL, ''),
(2024, '资源审核', '2002', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:audit', '#', 'admin', NOW(), '', NULL, ''),


(2040, '问答会话查询', '2004', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:qa:session:list', '#', 'admin', NOW(), '', NULL, ''),
(2041, '发起问答',     '2004', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:qa:ask', '#', 'admin', NOW(), '', NULL, ''),
(2042, '问答反馈',     '2004', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:qa:feedback', '#', 'admin', NOW(), '', NULL, ''),

(2050, '题库查询', '2005', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:question:list', '#', 'admin', NOW(), '', NULL, ''),
(2051, '题库新增', '2005', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:question:add', '#', 'admin', NOW(), '', NULL, ''),
(2052, '试卷管理', '2005', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:paper:list', '#', 'admin', NOW(), '', NULL, ''),
(2053, '考试记录', '2005', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:record:list', '#', 'admin', NOW(), '', NULL, ''),

(2060, '模型配置查询', '2006', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:model:list', '#', 'admin', NOW(), '', NULL, ''),
(2061, '模型配置新增', '2006', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:model:add', '#', 'admin', NOW(), '', NULL, ''),
(2062, 'Prompt模板管理', '2006', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:prompt:list', '#', 'admin', NOW(), '', NULL, ''),
(2063, 'AI任务日志查询', '2006', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:task:list', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `menu_id` FROM `sys_menu` WHERE `menu_id` BETWEEN 2000 AND 2063;
