SET NAMES utf8mb4;

-- ----------------------------
-- 智慧校园菜单初始化脚本（增强版）
-- 说明：
-- 1. 依赖若依 base.sql 已初始化 sys_menu / sys_role_menu
-- 2. 默认授权给管理员角色 role_id = 1
-- 3. 如已执行过旧版脚本，建议先删除 2000~2199 范围菜单再执行本脚本
-- ----------------------------

DELETE FROM `sys_role_menu` WHERE `menu_id` BETWEEN 2000 AND 2199;
DELETE FROM `sys_menu` WHERE `menu_id` BETWEEN 2000 AND 2199;

INSERT INTO `sys_menu` VALUES
(2000, '智慧校园', '0', '10', 'campus', NULL, '', '', 1, 0, 'M', '0', '0', '', 'education', 'admin', NOW(), '', NULL, '智慧校园主目录'),

(2001, '首页概览', '2000', '1', 'overview', '', '', '', 1, 0, 'M', '0', '0', '', 'dashboard', 'admin', NOW(), '', NULL, '首页与多端概览'),
(2002, '用户与身份', '2000', '2', 'identity', '', '', '', 1, 0, 'M', '0', '0', '', 'user', 'admin', NOW(), '', NULL, '用户扩展与家长绑定'),
(2003, '教学基础', '2000', '3', 'teaching', '', '', '', 1, 0, 'M', '0', '0', '', 'tree-table', 'admin', NOW(), '', NULL, '年级班级课程基础数据'),
(2004, '学习中心', '2000', '4', 'learning', '', '', '', 1, 0, 'M', '0', '0', '', 'education', 'admin', NOW(), '', NULL, '资源行为画像推荐'),
(2005, '智能问答', '2000', '5', 'qa', '', '', '', 1, 0, 'M', '0', '0', '', 'message', 'admin', NOW(), '', NULL, '问答与反馈'),
(2006, '智能测评', '2000', '6', 'exam', '', '', '', 1, 0, 'M', '0', '0', '', 'build', 'admin', NOW(), '', NULL, '题库试卷考试错题'),
(2007, '学情分析', '2000', '7', 'analysis', '', '', '', 1, 0, 'M', '0', '0', '', 'chart', 'admin', NOW(), '', NULL, '预警与报告'),
(2008, 'AI管理', '2000', '8', 'ai', '', '', '', 1, 0, 'M', '0', '0', '', 'edit', 'admin', NOW(), '', NULL, '模型模板日志'),

(2010, '概览面板', '2001', '1', 'dashboard', 'campus/overview/index', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'dashboard', 'admin', NOW(), '', NULL, '统一概览页面'),
(2011, '学生概览', '2001', '2', 'studentDashboard', 'campus/overview/student', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'user', 'admin', NOW(), '', NULL, '学生端概览'),
(2012, '教师概览', '2001', '3', 'teacherDashboard', 'campus/overview/teacher', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'peoples', 'admin', NOW(), '', NULL, '教师端概览'),
(2013, '家长概览', '2001', '4', 'parentDashboard', 'campus/overview/parent', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'people', 'admin', NOW(), '', NULL, '家长端概览'),

(2020, '用户档案', '2002', '1', 'userProfile', 'campus/userProfile/index', '', '', 1, 0, 'C', '0', '0', 'campus:userProfile:list', 'user', 'admin', NOW(), '', NULL, '用户扩展档案'),
(2021, '家长绑定', '2002', '2', 'parentStudentRel', 'campus/parentStudentRel/index', '', '', 1, 0, 'C', '0', '0', 'campus:parentStudentRel:list', 'peoples', 'admin', NOW(), '', NULL, '家长学生绑定'),
(2022, '登录风险', '2002', '3', 'loginRiskEvent', 'campus/loginRiskEvent/index', '', '', 1, 0, 'C', '0', '0', 'campus:loginRiskEvent:list', 'log', 'admin', NOW(), '', NULL, '登录风险事件'),

(2030, '年级管理', '2003', '1', 'grade', 'campus/grade/index', '', '', 1, 0, 'C', '0', '0', 'campus:grade:list', 'tree', 'admin', NOW(), '', NULL, '年级管理'),
(2031, '班级管理', '2003', '2', 'class', 'campus/class/index', '', '', 1, 0, 'C', '0', '0', 'campus:class:list', 'tree', 'admin', NOW(), '', NULL, '班级管理'),
(2032, '课程管理', '2003', '3', 'course', 'campus/course/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'education', 'admin', NOW(), '', NULL, '课程管理'),
(2033, '课程章节', '2003', '4', 'courseChapter', 'campus/courseChapter/index', '', '', 1, 0, 'C', '0', '0', 'campus:courseChapter:list', 'nested', 'admin', NOW(), '', NULL, '课程章节'),
(2034, '选课关系', '2003', '5', 'courseStudent', 'campus/courseStudent/index', '', '', 1, 0, 'C', '0', '0', 'campus:courseStudent:list', 'post', 'admin', NOW(), '', NULL, '课程学生关系'),
(2035, '知识点管理', '2003', '6', 'knowledgePoint', 'campus/knowledgePoint/index', '', '', 1, 0, 'C', '0', '0', 'campus:knowledgePoint:list', 'dict', 'admin', NOW(), '', NULL, '知识点管理'),

(2040, '资源管理', '2004', '1', 'resource', 'campus/resource/index', '', '', 1, 0, 'C', '0', '0', 'campus:resource:list', 'documentation', 'admin', NOW(), '', NULL, '学习资源管理'),
(2041, '行为记录', '2004', '2', 'studyRecord', 'campus/studyRecord/index', '', '', 1, 0, 'C', '0', '0', 'campus:studyRecord:list', 'form', 'admin', NOW(), '', NULL, '学习行为记录'),
(2042, '学习画像', '2004', '3', 'learningProfile', 'campus/learningProfile/index', '', '', 1, 0, 'C', '0', '0', 'campus:learningProfile:list', 'chart', 'admin', NOW(), '', NULL, '学习画像'),
(2043, '个性推荐', '2004', '4', 'learningRecommendation', 'campus/learningRecommendation/index', '', '', 1, 0, 'C', '0', '0', 'campus:learningRecommendation:list', 'guide', 'admin', NOW(), '', NULL, '个性化推荐'),

(2050, '问答会话', '2005', '1', 'qaSession', 'campus/qa/session/index', '', '', 1, 0, 'C', '0', '0', 'campus:qa:session:list', 'message', 'admin', NOW(), '', NULL, '问答会话'),
(2051, '问答消息', '2005', '2', 'qaMessage', 'campus/qa/message/index', '', '', 1, 0, 'C', '0', '0', 'campus:qa:message:list', 'chat-line-round', 'admin', NOW(), '', NULL, '问答消息'),
(2052, '问答反馈', '2005', '3', 'qaFeedback', 'campus/qa/feedback/index', '', '', 1, 0, 'C', '0', '0', 'campus:qa:feedback:add', 'comment', 'admin', NOW(), '', NULL, '问答反馈'),

(2060, '题库管理', '2006', '1', 'questionBank', 'campus/exam/question/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:question:list', 'build', 'admin', NOW(), '', NULL, '题库管理'),
(2061, '题目选项', '2006', '2', 'questionOption', 'campus/exam/option/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:question:list', 'list', 'admin', NOW(), '', NULL, '题目选项'),
(2062, '试卷管理', '2006', '3', 'examPaper', 'campus/exam/paper/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:paper:list', 'nested', 'admin', NOW(), '', NULL, '试卷管理'),
(2063, '考试记录', '2006', '4', 'examRecord', 'campus/exam/record/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:record:list', 'date', 'admin', NOW(), '', NULL, '考试记录'),
(2064, '错题本', '2006', '5', 'wrongQuestionBook', 'campus/exam/wrong/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:record:list', 'edit', 'admin', NOW(), '', NULL, '错题本'),

(2070, '学情预警', '2007', '1', 'learningWarning', 'campus/analysis/warning/index', '', '', 1, 0, 'C', '0', '0', 'campus:analysis:warning:list', 'warning', 'admin', NOW(), '', NULL, '学情预警'),
(2071, '学情报告', '2007', '2', 'learningReport', 'campus/analysis/report/index', '', '', 1, 0, 'C', '0', '0', 'campus:analysis:report:list', 'clipboard', 'admin', NOW(), '', NULL, '学情报告'),

(2080, '模型配置', '2008', '1', 'aiModel', 'campus/ai/model/index', '', '', 1, 0, 'C', '0', '0', 'campus:ai:model:list', 'edit', 'admin', NOW(), '', NULL, 'AI模型配置'),
(2081, 'Prompt模板', '2008', '2', 'aiPrompt', 'campus/ai/prompt/index', '', '', 1, 0, 'C', '0', '0', 'campus:ai:prompt:list', 'form', 'admin', NOW(), '', NULL, 'Prompt模板'),
(2082, '任务日志', '2008', '3', 'aiTaskLog', 'campus/ai/task/index', '', '', 1, 0, 'C', '0', '0', 'campus:ai:task:list', 'log', 'admin', NOW(), '', NULL, 'AI任务日志');

INSERT INTO `sys_menu` VALUES
(2100, '档案查询', '2020', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:query', '#', 'admin', NOW(), '', NULL, ''),
(2101, '档案新增', '2020', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:add', '#', 'admin', NOW(), '', NULL, ''),
(2102, '档案修改', '2020', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:edit', '#', 'admin', NOW(), '', NULL, ''),
(2103, '档案删除', '2020', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:remove', '#', 'admin', NOW(), '', NULL, ''),

(2110, '资源查询', '2040', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:query', '#', 'admin', NOW(), '', NULL, ''),
(2111, '资源新增', '2040', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:add', '#', 'admin', NOW(), '', NULL, ''),
(2112, '资源修改', '2040', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:edit', '#', 'admin', NOW(), '', NULL, ''),
(2113, '资源删除', '2040', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:remove', '#', 'admin', NOW(), '', NULL, ''),

(2120, '行为查询', '2041', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:studyRecord:query', '#', 'admin', NOW(), '', NULL, ''),
(2121, '行为新增', '2041', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:studyRecord:add', '#', 'admin', NOW(), '', NULL, ''),

(2130, '画像查询', '2042', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningProfile:query', '#', 'admin', NOW(), '', NULL, ''),
(2131, '画像重建', '2042', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningProfile:edit', '#', 'admin', NOW(), '', NULL, ''),

(2140, '推荐查询', '2043', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningRecommendation:query', '#', 'admin', NOW(), '', NULL, ''),
(2141, '推荐生成', '2043', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:learningRecommendation:add', '#', 'admin', NOW(), '', NULL, ''),

(2150, '预警查询', '2070', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:analysis:warning:list', '#', 'admin', NOW(), '', NULL, ''),
(2151, '预警构建', '2070', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:analysis:warning:add', '#', 'admin', NOW(), '', NULL, ''),
(2152, '报告查询', '2071', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:analysis:report:list', '#', 'admin', NOW(), '', NULL, ''),
(2153, '报告生成', '2071', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:analysis:report:add', '#', 'admin', NOW(), '', NULL, ''),

(2160, '题库查询', '2060', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:question:list', '#', 'admin', NOW(), '', NULL, ''),
(2161, '题库新增', '2060', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:question:add', '#', 'admin', NOW(), '', NULL, ''),
(2162, '试卷查询', '2062', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:paper:list', '#', 'admin', NOW(), '', NULL, ''),
(2163, '试卷新增', '2062', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:paper:add', '#', 'admin', NOW(), '', NULL, ''),
(2164, '记录查询', '2063', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:record:list', '#', 'admin', NOW(), '', NULL, ''),

(2170, '模型查询', '2080', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:model:list', '#', 'admin', NOW(), '', NULL, ''),
(2171, '模型新增', '2080', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:model:add', '#', 'admin', NOW(), '', NULL, ''),
(2172, '模板查询', '2081', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:prompt:list', '#', 'admin', NOW(), '', NULL, ''),
(2173, '模板新增', '2081', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:prompt:add', '#', 'admin', NOW(), '', NULL, ''),
(2174, '任务日志查询', '2082', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:task:list', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `menu_id` FROM `sys_menu` WHERE `menu_id` BETWEEN 2000 AND 2174;
