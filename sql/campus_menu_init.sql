SET NAMES utf8mb4;

-- ----------------------------
-- 智慧校园菜单初始化脚本（增强版）
-- 说明：
-- 1. 依赖若依 base.sql 已初始化 sys_menu / sys_role_menu
-- 2. 默认授权给管理员角色 role_id = 1
-- 3. 如已执行过旧版脚本，建议先删除 2000~2299 范围菜单再执行本脚本
-- ----------------------------

DELETE FROM `sys_role_menu` WHERE `menu_id` BETWEEN 2000 AND 2299;
DELETE FROM `sys_menu` WHERE `menu_id` BETWEEN 2000 AND 2299;

INSERT INTO `sys_menu` VALUES
(2000, '智慧校园', '0', '10', 'campus', NULL, '', '', 1, 0, 'M', '0', '0', '', 'ri-graduation-cap-line', 'admin', NOW(), '', NULL, '智慧校园主目录'),

(2001, '首页概览', '2000', '1', 'overview', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-dashboard-3-line', 'admin', NOW(), '', NULL, '首页与多端概览'),
(2002, '用户与身份', '2000', '2', 'identity', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-user-3-line', 'admin', NOW(), '', NULL, '用户扩展与家长绑定'),
(2003, '教学基础', '2000', '3', 'teaching', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-node-tree', 'admin', NOW(), '', NULL, '年级班级课程基础数据'),
(2004, '学习中心', '2000', '4', 'learning', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-graduation-cap-line', 'admin', NOW(), '', NULL, '资源行为画像推荐'),
(2005, '智能问答', '2000', '5', 'qa', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-chat-3-line', 'admin', NOW(), '', NULL, '问答与反馈'),
(2006, '智能测评', '2000', '6', 'exam', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-ai-generate-2', 'admin', NOW(), '', NULL, '题库试卷考试错题'),
(2007, '学情分析', '2000', '7', 'analysis', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-bar-chart-box-line', 'admin', NOW(), '', NULL, '预警与报告'),
(2008, 'AI管理', '2000', '8', 'ai', '', '', '', 1, 0, 'M', '0', '0', '', 'ri-robot-2-line', 'admin', NOW(), '', NULL, '模型模板日志'),

(2010, '概览面板', '2001', '1', 'dashboard', 'campus/overview/index', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'ri-dashboard-3-line', 'admin', NOW(), '', NULL, '统一概览页面'),
(2011, '学生概览', '2001', '2', 'studentDashboard', 'campus/overview/student', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'ri-user-3-line', 'admin', NOW(), '', NULL, '学生端概览'),
(2012, '教师概览', '2001', '3', 'teacherDashboard', 'campus/overview/teacher', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'ri-team-line', 'admin', NOW(), '', NULL, '教师端概览'),
(2013, '家长概览', '2001', '4', 'parentDashboard', 'campus/overview/parent', '', '', 1, 0, 'C', '0', '0', 'campus:overview:query', 'ri-parent-line', 'admin', NOW(), '', NULL, '家长端概览'),

(2020, '用户档案', '2002', '1', 'userProfile', 'campus/userProfile/index', '', '', 1, 0, 'C', '0', '0', 'campus:userProfile:list', 'ri-account-box-line', 'admin', NOW(), '', NULL, '用户扩展档案'),
(2021, '家长绑定', '2002', '2', 'parentStudentRel', 'campus/parentStudentRel/index', '', '', 1, 0, 'C', '0', '0', 'campus:parentStudentRel:list', 'ri-links-line', 'admin', NOW(), '', NULL, '家长学生绑定'),
(2022, '登录风险', '2002', '3', 'loginRiskEvent', 'campus/loginRiskEvent/index', '', '', 1, 0, 'C', '0', '0', 'campus:loginRiskEvent:list', 'ri-shield-user-line', 'admin', NOW(), '', NULL, '登录风险事件'),

(2030, '年级管理', '2003', '1', 'grade', 'campus/grade/index', '', '', 1, 0, 'C', '0', '0', 'campus:grade:list', 'ri-git-branch-line', 'admin', NOW(), '', NULL, '年级管理'),
(2031, '班级管理', '2003', '2', 'class', 'campus/class/index', '', '', 1, 0, 'C', '0', '0', 'campus:class:list', 'ri-organization-chart', 'admin', NOW(), '', NULL, '班级管理'),
(2032, '课程管理', '2003', '3', 'course', 'campus/course/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'ri-book-open-line', 'admin', NOW(), '', NULL, '课程管理'),
(2033, '课程章节', '2003', '4', 'courseChapter', 'campus/courseChapter/index', '', '', 1, 0, 'C', '0', '0', 'campus:courseChapter:list', 'ri-git-merge-line', 'admin', NOW(), '', NULL, '课程章节'),
(2034, '选课关系', '2003', '5', 'courseStudent', 'campus/courseStudent/index', '', '', 1, 0, 'C', '0', '0', 'campus:courseStudent:list', 'ri-links-line', 'admin', NOW(), '', NULL, '课程学生关系'),
(2035, '知识点管理', '2003', '6', 'knowledgePoint', 'campus/knowledgePoint/index', '', '', 1, 0, 'C', '0', '0', 'campus:knowledgePoint:list', 'ri-book-marked-line', 'admin', NOW(), '', NULL, '知识点管理'),
(2036, '班级学生', '2003', '7', 'classStudent', 'campus/classStudent/index', '', '', 1, 0, 'C', '0', '0', 'campus:userProfile:list', 'ri-group-line', 'admin', NOW(), '', NULL, '班级学生关系'),
(2037, '学年学期', '2003', '8', 'schoolTerm', 'campus/schoolTerm/index', '', '', 1, 0, 'C', '0', '0', 'campus:grade:list', 'ri-calendar-schedule-line', 'admin', NOW(), '', NULL, '学年学期管理'),
(2038, '班级课程', '2003', '9', 'classCourse', 'campus/classCourse/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'ri-book-2-line', 'admin', NOW(), '', NULL, '班级课程关系'),
(2039, '排课表', '2003', '10', 'courseSchedule', 'campus/courseSchedule/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'ri-calendar-view', 'admin', NOW(), '', NULL, '排课表管理'),
(2045, '教室管理', '2003', '11', 'classroom', 'campus/classroom/index', '', '', 1, 0, 'C', '0', '0', 'campus:classroom:list', 'ri-building-line', 'admin', NOW(), '', NULL, '统一维护教室、楼栋与校区'),
(2044, '班级课表', '2003', '12', 'classSchedule', 'campus/classSchedule/index', '', '', 1, 0, 'C', '0', '0', 'campus:course:list', 'ri-table-view', 'admin', NOW(), '', NULL, '班级课表视图'),
(2046, '节次布局', '2003', '13', 'timeTableLayout', 'campus/timeTableLayout/index', '', '', 1, 0, 'C', '0', '0', 'campus:timetable:list', 'ri-layout-grid-line', 'admin', NOW(), '', NULL, '课表节次与时间布局配置'),

(2040, '资源管理', '2004', '1', 'resource', 'campus/resource/index', '', '', 1, 0, 'C', '0', '0', 'campus:resource:list', 'ri-article-line', 'admin', NOW(), '', NULL, '学习资源管理'),

(2050, '问答会话', '2005', '1', 'qaSession', 'campus/qa/session/index', '', '', 1, 0, 'C', '0', '0', 'campus:qa:session:list', 'ri-message-3-line', 'admin', NOW(), '', NULL, '问答会话'),
(2051, '问答消息', '2005', '2', 'qaMessage', 'campus/qa/message/index', '', '', 1, 0, 'C', '0', '0', 'campus:qa:message:list', 'ri-chat-3-line', 'admin', NOW(), '', NULL, '问答消息'),
(2052, '问答反馈', '2005', '3', 'qaFeedback', 'campus/qa/feedback/index', '', '', 1, 0, 'C', '0', '0', 'campus:qa:feedback:add', 'ri-feedback-line', 'admin', NOW(), '', NULL, '问答反馈'),

(2060, '题库管理', '2006', '1', 'questionBank', 'campus/exam/question/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:question:list', 'ri-book-open-line', 'admin', NOW(), '', NULL, '题库管理'),
(2061, '题目选项', '2006', '2', 'questionOption', 'campus/exam/option/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:question:list', 'ri-file-list-3-line', 'admin', NOW(), '', NULL, '题目选项'),
(2062, '试卷管理', '2006', '3', 'examPaper', 'campus/exam/paper/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:paper:list', 'ri-git-merge-line', 'admin', NOW(), '', NULL, '试卷管理'),
(2063, '考试记录', '2006', '4', 'examRecord', 'campus/exam/record/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:record:list', 'ri-calendar-2-line', 'admin', NOW(), '', NULL, '考试记录'),
(2064, '错题本', '2006', '5', 'wrongQuestionBook', 'campus/exam/wrong/index', '', '', 1, 0, 'C', '0', '0', 'campus:exam:record:list', 'ri-edit-box-line', 'admin', NOW(), '', NULL, '错题本'),


(2080, '模型配置', '2008', '1', 'aiModel', 'campus/ai/model/index', '', '', 1, 0, 'C', '0', '0', 'campus:ai:model:list', 'ri-robot-2-line', 'admin', NOW(), '', NULL, 'AI模型配置'),
(2081, 'Prompt模板', '2008', '2', 'aiPrompt', 'campus/ai/prompt/index', '', '', 1, 0, 'C', '0', '0', 'campus:ai:prompt:list', 'ri-file-text-line', 'admin', NOW(), '', NULL, 'Prompt模板'),
(2082, '任务日志', '2008', '3', 'aiTaskLog', 'campus/ai/task/index', '', '', 1, 0, 'C', '0', '0', 'campus:ai:task:list', 'ri-file-list-3-line', 'admin', NOW(), '', NULL, 'AI任务日志');

INSERT INTO `sys_menu` VALUES
(2100, '档案查询', '2020', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:query', '#', 'admin', NOW(), '', NULL, ''),
(2101, '档案新增', '2020', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:add', '#', 'admin', NOW(), '', NULL, ''),
(2102, '档案修改', '2020', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:edit', '#', 'admin', NOW(), '', NULL, ''),
(2103, '档案删除', '2020', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:remove', '#', 'admin', NOW(), '', NULL, ''),

(2110, '资源查询', '2040', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:query', '#', 'admin', NOW(), '', NULL, ''),
(2111, '资源新增', '2040', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:add', '#', 'admin', NOW(), '', NULL, ''),
(2112, '资源修改', '2040', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:edit', '#', 'admin', NOW(), '', NULL, ''),
(2113, '资源删除', '2040', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:resource:remove', '#', 'admin', NOW(), '', NULL, ''),


(2160, '题库查询', '2060', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:question:list', '#', 'admin', NOW(), '', NULL, ''),
(2161, '题库新增', '2060', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:question:add', '#', 'admin', NOW(), '', NULL, ''),
(2162, '试卷查询', '2062', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:paper:list', '#', 'admin', NOW(), '', NULL, ''),
(2163, '试卷新增', '2062', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:paper:add', '#', 'admin', NOW(), '', NULL, ''),
(2164, '记录查询', '2063', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:exam:record:list', '#', 'admin', NOW(), '', NULL, ''),

(2170, '模型查询', '2080', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:model:list', '#', 'admin', NOW(), '', NULL, ''),
(2171, '模型新增', '2080', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:model:add', '#', 'admin', NOW(), '', NULL, ''),
(2172, '模板查询', '2081', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:prompt:list', '#', 'admin', NOW(), '', NULL, ''),
(2173, '模板新增', '2081', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:prompt:add', '#', 'admin', NOW(), '', NULL, ''),
(2174, '任务日志查询', '2082', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:ai:task:list', '#', 'admin', NOW(), '', NULL, ''),

(2180, '年级查询', '2030', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:query', '#', 'admin', NOW(), '', NULL, ''),
(2181, '年级新增', '2030', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:add', '#', 'admin', NOW(), '', NULL, ''),
(2182, '年级修改', '2030', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:edit', '#', 'admin', NOW(), '', NULL, ''),
(2183, '年级删除', '2030', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:remove', '#', 'admin', NOW(), '', NULL, ''),

(2184, '班级查询', '2031', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:class:query', '#', 'admin', NOW(), '', NULL, ''),
(2185, '班级新增', '2031', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:class:add', '#', 'admin', NOW(), '', NULL, ''),
(2186, '班级修改', '2031', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:class:edit', '#', 'admin', NOW(), '', NULL, ''),
(2187, '班级删除', '2031', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:class:remove', '#', 'admin', NOW(), '', NULL, ''),

(2188, '课程查询', '2032', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:query', '#', 'admin', NOW(), '', NULL, ''),
(2189, '课程新增', '2032', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:add', '#', 'admin', NOW(), '', NULL, ''),
(2190, '课程修改', '2032', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:edit', '#', 'admin', NOW(), '', NULL, ''),
(2191, '课程删除', '2032', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:remove', '#', 'admin', NOW(), '', NULL, ''),

(2192, '章节查询', '2033', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseChapter:query', '#', 'admin', NOW(), '', NULL, ''),
(2193, '章节新增', '2033', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseChapter:add', '#', 'admin', NOW(), '', NULL, ''),
(2194, '章节修改', '2033', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseChapter:edit', '#', 'admin', NOW(), '', NULL, ''),
(2195, '章节删除', '2033', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseChapter:remove', '#', 'admin', NOW(), '', NULL, ''),

(2196, '选课查询', '2034', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseStudent:query', '#', 'admin', NOW(), '', NULL, ''),
(2197, '选课新增', '2034', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseStudent:add', '#', 'admin', NOW(), '', NULL, ''),
(2198, '选课修改', '2034', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseStudent:edit', '#', 'admin', NOW(), '', NULL, ''),
(2199, '选课删除', '2034', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:courseStudent:remove', '#', 'admin', NOW(), '', NULL, ''),

(2200, '知识点查询', '2035', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:knowledgePoint:query', '#', 'admin', NOW(), '', NULL, ''),
(2201, '知识点新增', '2035', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:knowledgePoint:add', '#', 'admin', NOW(), '', NULL, ''),
(2202, '知识点修改', '2035', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:knowledgePoint:edit', '#', 'admin', NOW(), '', NULL, ''),
(2203, '知识点删除', '2035', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:knowledgePoint:remove', '#', 'admin', NOW(), '', NULL, ''),

(2204, '班级学生配置', '2036', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:userProfile:edit', '#', 'admin', NOW(), '', NULL, ''),
(2205, '学期查询', '2037', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:query', '#', 'admin', NOW(), '', NULL, ''),
(2206, '学期新增', '2037', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:add', '#', 'admin', NOW(), '', NULL, ''),
(2207, '学期修改', '2037', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:edit', '#', 'admin', NOW(), '', NULL, ''),
(2208, '学期删除', '2037', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:remove', '#', 'admin', NOW(), '', NULL, ''),

(2209, '班级课程查询', '2038', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:query', '#', 'admin', NOW(), '', NULL, ''),
(2210, '班级课程新增', '2038', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:add', '#', 'admin', NOW(), '', NULL, ''),
(2211, '班级课程修改', '2038', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:edit', '#', 'admin', NOW(), '', NULL, ''),
(2212, '班级课程删除', '2038', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:remove', '#', 'admin', NOW(), '', NULL, ''),

(2213, '排课查询', '2039', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:query', '#', 'admin', NOW(), '', NULL, ''),
(2214, '排课新增', '2039', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:add', '#', 'admin', NOW(), '', NULL, ''),
(2215, '排课修改', '2039', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:edit', '#', 'admin', NOW(), '', NULL, ''),
(2216, '排课删除', '2039', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:remove', '#', 'admin', NOW(), '', NULL, ''),
(2221, '教室查询', '2045', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:query', '#', 'admin', NOW(), '', NULL, ''),
(2222, '教室新增', '2045', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:add', '#', 'admin', NOW(), '', NULL, ''),
(2223, '教室修改', '2045', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:edit', '#', 'admin', NOW(), '', NULL, ''),
(2224, '教室删除', '2045', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:classroom:remove', '#', 'admin', NOW(), '', NULL, ''),

(2217, '教学AI配置', '2003', '12', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:grade:edit', '#', 'admin', NOW(), '', NULL, '教学基础 AI 配置能力'),
(2218, '班级课表查询', '2044', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:course:query', '#', 'admin', NOW(), '', NULL, ''),
(2225, '布局查询', '2046', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:timetable:list', '#', 'admin', NOW(), '', NULL, ''),
(2226, '布局修改', '2046', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'campus:timetable:edit', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `menu_id` FROM `sys_menu` WHERE `menu_id` BETWEEN 2000 AND 2226;
