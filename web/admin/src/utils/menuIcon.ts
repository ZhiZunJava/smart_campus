export interface MenuIconOption {
  label: string
  value: string
  group: string
  legacy?: string[]
  keywords?: string[]
}

export const legacyMenuIconMap: Record<string, string> = {
  system: 'ri-settings-3-line',
  monitor: 'ri-line-chart-line',
  user: 'ri-user-3-line',
  peoples: 'ri-team-line',
  people: 'ri-parent-line',
  'tree-table': 'ri-node-tree',
  tree: 'ri-git-branch-line',
  post: 'ri-briefcase-4-line',
  dict: 'ri-book-2-line',
  edit: 'ri-edit-box-line',
  message: 'ri-message-3-line',
  log: 'ri-file-list-3-line',
  online: 'ri-user-search-line',
  job: 'ri-timer-line',
  druid: 'ri-database-2-line',
  server: 'ri-server-line',
  redis: 'ri-stack-line',
  'redis-list': 'ri-file-list-3-line',
  form: 'ri-file-text-line',
  logininfor: 'ri-login-box-line',
  education: 'ri-graduation-cap-line',
  dashboard: 'ri-dashboard-line',
  build: 'ri-ai-generate-2',
  chart: 'ri-bar-chart-box-line',
  guide: 'ri-road-map-line',
  documentation: 'ri-article-line',
  nested: 'ri-git-merge-line',
  date: 'ri-calendar-2-line',
  warning: 'ri-alert-line',
  clipboard: 'ri-clipboard-line',
  comment: 'ri-feedback-line',
  campus: 'ri-school-line',
  code: 'ri-code-s-slash-line',
  swagger: 'ri-survey-line',
  tool: 'ri-apps-2-line',
  example: 'ri-layout-grid-line',
  component: 'ri-puzzle-line',
  skill: 'ri-sparkling-2-line',
  bug: 'ri-bug-line',
}

export const menuIconOptions: MenuIconOption[] = [
  { label: '系统设置', value: 'ri-settings-3-line', group: '系统', legacy: ['system'], keywords: ['基础设置', '设置', 'system'] },
  { label: '系统监控', value: 'ri-line-chart-line', group: '系统', legacy: ['monitor'], keywords: ['监控', 'monitor'] },
  { label: '用户', value: 'ri-user-3-line', group: '用户', legacy: ['user'], keywords: ['用户', 'user'] },
  { label: '角色/群组', value: 'ri-team-line', group: '用户', legacy: ['peoples'], keywords: ['角色', '教师', '家长绑定', 'team'] },
  { label: '家长', value: 'ri-parent-line', group: '用户', legacy: ['people'], keywords: ['家长', 'parent'] },
  { label: '用户档案', value: 'ri-account-box-line', group: '用户', keywords: ['档案', 'profile'] },
  { label: '用户安全', value: 'ri-shield-user-line', group: '用户', keywords: ['安全', '登录风险'] },
  { label: '菜单结构', value: 'ri-node-tree', group: '结构', legacy: ['tree-table'], keywords: ['菜单管理', '树表', 'tree-table'] },
  { label: '树结构', value: 'ri-git-branch-line', group: '结构', legacy: ['tree'], keywords: ['部门', '年级', '班级', 'tree'] },
  { label: '层级关系', value: 'ri-git-merge-line', group: '结构', legacy: ['nested'], keywords: ['章节', '试卷', 'nested'] },
  { label: '岗位/关系', value: 'ri-briefcase-4-line', group: '结构', legacy: ['post'], keywords: ['岗位', '选课关系', 'post'] },
  { label: '字典/知识', value: 'ri-book-2-line', group: '内容', legacy: ['dict'], keywords: ['字典', '知识点', 'dict'] },
  { label: '文档', value: 'ri-article-line', group: '内容', legacy: ['documentation'], keywords: ['资源', '文档', 'documentation'] },
  { label: '表单', value: 'ri-file-text-line', group: '内容', legacy: ['form'], keywords: ['表单', '行为记录', 'prompt'] },
  { label: '列表日志', value: 'ri-file-list-3-line', group: '内容', legacy: ['log', 'redis-list'], keywords: ['日志', '列表', 'log'] },
  { label: '消息公告', value: 'ri-message-3-line', group: '沟通', legacy: ['message'], keywords: ['通知', '公告', '问答会话'] },
  { label: '反馈评论', value: 'ri-feedback-line', group: '沟通', legacy: ['comment'], keywords: ['反馈', '评论'] },
  { label: '聊天', value: 'ri-chat-3-line', group: '沟通', keywords: ['消息', 'chat'] },
  { label: '教育', value: 'ri-graduation-cap-line', group: '校园', legacy: ['education'], keywords: ['课程', '教学', '学习中心'] },
  { label: '概览仪表盘', value: 'ri-dashboard-line', group: '校园', legacy: ['dashboard'], keywords: ['首页', '概览', 'dashboard'] },
  { label: '路线导引', value: 'ri-road-map-line', group: '校园', legacy: ['guide'], keywords: ['推荐', 'guide'] },
  { label: '日历记录', value: 'ri-calendar-2-line', group: '校园', legacy: ['date'], keywords: ['考试记录', 'date'] },
  { label: '预警提醒', value: 'ri-alert-line', group: '校园', legacy: ['warning'], keywords: ['预警', 'warning'] },
  { label: '报告剪贴板', value: 'ri-clipboard-line', group: '校园', legacy: ['clipboard'], keywords: ['报告', 'clipboard'] },
  { label: '数据分析', value: 'ri-bar-chart-box-line', group: '数据', legacy: ['chart'], keywords: ['分析', '画像', 'chart'] },
  { label: '数据库', value: 'ri-database-2-line', group: '数据', legacy: ['druid'], keywords: ['数据监控', 'druid'] },
  { label: '服务器', value: 'ri-server-line', group: '数据', legacy: ['server'], keywords: ['服务监控', 'server'] },
  { label: '缓存堆栈', value: 'ri-stack-line', group: '数据', legacy: ['redis'], keywords: ['缓存', 'redis'] },
  { label: '搜索用户', value: 'ri-user-search-line', group: '数据', legacy: ['online'], keywords: ['在线用户', '查询', 'online'] },
  { label: '编辑', value: 'ri-edit-box-line', group: '操作', legacy: ['edit'], keywords: ['参数设置', '错题本', '模型配置'] },
  { label: '定时器', value: 'ri-timer-line', group: '操作', legacy: ['job'], keywords: ['定时任务', 'job'] },
  { label: '登录', value: 'ri-login-box-line', group: '操作', legacy: ['logininfor'], keywords: ['登录日志', 'login'] },
  { label: 'AI 生成', value: 'ri-ai-generate-2', group: 'AI', legacy: ['build'], keywords: ['智能测评', '构建', 'build'] },
  { label: '校园学习', value: 'ri-school-line', group: 'AI', legacy: ['campus'], keywords: ['智慧校园', '校园'] },
  { label: '智能问答', value: 'ri-chat-3-line', group: 'AI', keywords: ['智能问答', 'qa'] },
  { label: 'AI 搜索', value: 'ri-search-ai-line', group: 'AI', keywords: ['检索', '搜索'] },
  { label: '学习画像', value: 'ri-brain-line', group: 'AI', keywords: ['学习画像', '分析', 'brain'] },
  { label: '模型/助手', value: 'ri-robot-2-line', group: 'AI', keywords: ['AI管理', '模型配置', 'agent'] },
  { label: 'AI 火花', value: 'ri-sparkling-2-line', group: 'AI', keywords: ['智能', 'spark'] },
]

export function resolveMenuIcon(icon?: string): string {
  if (!icon || icon === '#') return ''
  if (icon.startsWith('ri-')) return icon
  return legacyMenuIconMap[icon] || icon
}

export function isRemixIcon(icon?: string): boolean {
  return resolveMenuIcon(icon).startsWith('ri-')
}
