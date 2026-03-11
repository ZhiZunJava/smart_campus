# 智慧校园学习门户

这是基于管理端相同技术栈扩展的学习门户前端，覆盖：
- 学生端
- 教师端
- 家长端

## 技术栈
- Vue 3
- Vite
- Element Plus
- Axios
- Vue Router

## 已实现页面
### 登录
- `/login` 门户登录页
- 登录后根据当前角色自动跳转学生端、教师端或家长端

### 学生端
- `/student/dashboard` 学习首页
- `/student/resources` 资源中心
- `/student/recommendations` 个性推荐
- `/student/qa` 智能问答
- `/student/exams` 我的考试

### 教师端
- `/teacher/dashboard` 教学概览
- `/teacher/courses` 课程管理
- `/teacher/warnings` 学情预警

### 家长端
- `/parent/dashboard` 孩子概览
- `/parent/reports` 学习报告

## 运行方式
```bash
cd web/student
npm install
npm run dev
```

默认启动端口：`3000`

## 接口说明
门户端复用了管理端后端接口，包括：
- `/campus/portal/student/dashboard`
- `/campus/portal/teacher/dashboard`
- `/campus/portal/parent/dashboard`
- `/campus/resource/list`
- `/campus/detail/resource/{id}`
- `/campus/learningRecommendation/list`
- `/campus/qa/*`
- `/campus/exam/record/list`
- `/campus/exam/answer/list`
- `/campus/course/list`
- `/campus/analysis/warning/list`
- `/campus/analysis/report/list`

## 复用说明
已复用管理端的设计思路：
- Token 读取逻辑
- 登录接口与获取用户信息接口
- 路由守卫与角色识别逻辑
- Axios 请求拦截器思路
- 统一错误处理方式
- 统一蓝色主题风格
