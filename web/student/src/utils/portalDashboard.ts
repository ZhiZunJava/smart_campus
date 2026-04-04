import { sortTasksWithFeedback } from '@/utils/taskFeedback'

export interface DashboardFactItem {
  label: string
  value: string
}

export interface DashboardPreviewCard {
  key: string
  title: string
  tag?: string
  badges?: string[]
  facts?: DashboardFactItem[]
  raw?: any
}

export interface DashboardTaskCard {
  raw: any
  key: string
  title: string
  tag: string
  tagType: string
  meta: string[]
  icon: string
  iconClass: string
  statusText: string
  metaClass: string
  actionLabel: string
  recommendationReason: string
}

export interface StaticDashboardTaskInput {
  key: string
  title: string
  desc?: string
  tag?: string
  tagType?: string
  statusText?: string
  meta?: string[]
  actionLabel?: string
  recommendationReason?: string
  path: string
  iconType?: 'exam' | 'homework' | 'practice' | 'default'
}

export type DashboardDrawerTab = 'todo' | 'message' | 'notice'

export interface ShortcutVisualConfig {
  icon: string
  bg: string
}

const shortcutGradientPool = [
  'linear-gradient(135deg, #5aa9ff, #2f6ee5)',
  'linear-gradient(135deg, #70dbc9, #2fae95)',
  'linear-gradient(135deg, #9ea2ff, #7168f1)',
  'linear-gradient(135deg, #ffb86a, #ff8f3c)',
  'linear-gradient(135deg, #f7a9c4, #ef668f)',
  'linear-gradient(135deg, #77d3ef, #2f8de4)',
  'linear-gradient(135deg, #8bd8af, #2ca675)',
  'linear-gradient(135deg, #ffc46a, #ee8b1f)',
  'linear-gradient(135deg, #7aa5ff, #4b68f3)',
  'linear-gradient(135deg, #8f9fb8, #5e718d)',
  'linear-gradient(135deg, #9dd6ff, #4f9aff)',
  'linear-gradient(135deg, #85e0d1, #3db39a)',
  'linear-gradient(135deg, #ffb0a8, #f06c5f)',
  'linear-gradient(135deg, #c7a7ff, #8a68f1)',
  'linear-gradient(135deg, #ffd36e, #f1a702)',
  'linear-gradient(135deg, #92d0ff, #3f86d9)',
] as const

const shortcutIconPool = [
  'ri-compass-3-line',
  'ri-dashboard-line',
  'ri-apps-2-line',
  'ri-book-open-line',
  'ri-calendar-check-line',
  'ri-file-list-3-line',
  'ri-chat-quote-line',
  'ri-search-eye-line',
  'ri-medal-line',
  'ri-book-2-line',
  'ri-team-line',
  'ri-graduation-cap-line',
  'ri-archive-drawer-line',
  'ri-article-line',
  'ri-slideshow-3-line',
  'ri-briefcase-4-line',
] as const

const shortcutVisualByPath: Record<string, ShortcutVisualConfig> = {
  '/student/dashboard': { icon: 'ri-home-5-line', bg: 'linear-gradient(135deg, #5aa9ff, #2f6ee5)' },
  '/student/selection': { icon: 'ri-list-check-3', bg: 'linear-gradient(135deg, #6aa8ff, #326cf3)' },
  '/student/personalized-selection': { icon: 'ri-git-branch-line', bg: 'linear-gradient(135deg, #ffb680, #eb7e45)' },
  '/student/schedule': { icon: 'ri-calendar-schedule-line', bg: 'linear-gradient(135deg, #70dbc9, #2fae95)' },
  '/student/class-schedule': { icon: 'ri-calendar-2-line', bg: 'linear-gradient(135deg, #79e0d4, #36b39f)' },
  '/student/scores': { icon: 'ri-bar-chart-box-line', bg: 'linear-gradient(135deg, #8dc8ff, #468ce8)' },
  '/student/academic-profile': { icon: 'ri-id-card-line', bg: 'linear-gradient(135deg, #8fa7ff, #5c71ef)' },
  '/student/qa': { icon: 'ri-chat-quote-line', bg: 'linear-gradient(135deg, #79d8f3, #2f8de4)' },
  '/student/plaza': { icon: 'ri-task-line', bg: 'linear-gradient(135deg, #ff9898, #f15f5f)' },
  '/student/course-offerings': { icon: 'ri-search-eye-line', bg: 'linear-gradient(135deg, #ffbf75, #f08c33)' },
  '/student/exams': { icon: 'ri-file-paper-2-line', bg: 'linear-gradient(135deg, #9ea2ff, #7168f1)' },
  '/student/courses': { icon: 'ri-book-open-line', bg: 'linear-gradient(135deg, #edb580, #c97b4e)' },
  '/student/class-courses': { icon: 'ri-book-2-line', bg: 'linear-gradient(135deg, #eab37a, #c97d46)' },
  '/student/resources': { icon: 'ri-folder-chart-line', bg: 'linear-gradient(135deg, #ffb86a, #ff8f3c)' },
  '/student/favorites': { icon: 'ri-bookmark-3-line', bg: 'linear-gradient(135deg, #f7a9c4, #ef668f)' },
  '/student/wrongbook': { icon: 'ri-book-marked-line', bg: 'linear-gradient(135deg, #ffb0cf, #ef6d98)' },
  '/student/verification': { icon: 'ri-shield-check-line', bg: 'linear-gradient(135deg, #7fd4bf, #2da987)' },
  '/teacher/dashboard': { icon: 'ri-home-5-line', bg: 'linear-gradient(135deg, #5aa9ff, #2f6ee5)' },
  '/teacher/courses': { icon: 'ri-book-open-line', bg: 'linear-gradient(135deg, #edb580, #c97b4e)' },
  '/teacher/schedule': { icon: 'ri-calendar-schedule-line', bg: 'linear-gradient(135deg, #70dbc9, #2fae95)' },
  '/teacher/affairs': { icon: 'ri-briefcase-4-line', bg: 'linear-gradient(135deg, #8f9fb8, #5e718d)' },
  '/teacher/resources': { icon: 'ri-folder-chart-line', bg: 'linear-gradient(135deg, #ffb86a, #ff8f3c)' },
  '/teacher/course-offerings': { icon: 'ri-search-eye-line', bg: 'linear-gradient(135deg, #ffbf75, #f08c33)' },
  '/advisor/dashboard': { icon: 'ri-home-5-line', bg: 'linear-gradient(135deg, #5aa9ff, #2f6ee5)' },
  '/advisor/students': { icon: 'ri-team-line', bg: 'linear-gradient(135deg, #7aa5ff, #4b68f3)' },
  '/advisor/scores': { icon: 'ri-bar-chart-box-line', bg: 'linear-gradient(135deg, #8dc8ff, #468ce8)' },
  '/advisor/affairs': { icon: 'ri-briefcase-4-line', bg: 'linear-gradient(135deg, #8f9fb8, #5e718d)' },
  '/parent/dashboard': { icon: 'ri-home-5-line', bg: 'linear-gradient(135deg, #5aa9ff, #2f6ee5)' },
  '/parent/courses': { icon: 'ri-book-open-line', bg: 'linear-gradient(135deg, #edb580, #c97b4e)' },
  '/parent/schedule': { icon: 'ri-calendar-schedule-line', bg: 'linear-gradient(135deg, #70dbc9, #2fae95)' },
  '/parent/child-bind': { icon: 'ri-links-line', bg: 'linear-gradient(135deg, #f7a9c4, #ef668f)' },
  '/student/parent-requests': { icon: 'ri-parent-line', bg: 'linear-gradient(135deg, #a78bfa, #7c3aed)' },
}

const studentDefaultShortcutPaths = [
  '/student/selection',
  '/student/schedule',
  '/student/scores',
  '/student/academic-profile',
  '/student/qa',
  '/student/plaza',
  '/student/class-schedule',
  '/student/course-offerings',
  '/student/exams',
] as const

function shortcutHash(input: string) {
  let hash = 0
  for (let index = 0; index < input.length; index += 1) {
    hash = (hash * 31 + input.charCodeAt(index)) >>> 0
  }
  return hash
}

export function resolveShortcutVisual(path: string, title: string): ShortcutVisualConfig {
  const directMatch = shortcutVisualByPath[path]
  if (directMatch) {
    return directMatch
  }

  const text = `${path} ${title}`.toLowerCase()
  if (text.includes('dashboard') || text.includes('概览') || text.includes('首页')) {
    return { icon: 'ri-home-5-line', bg: 'linear-gradient(135deg, #5aa9ff, #2f6ee5)' }
  }
  if (text.includes('selection') || text.includes('选课')) {
    return { icon: 'ri-list-check-3', bg: 'linear-gradient(135deg, #6aa8ff, #326cf3)' }
  }
  if (text.includes('schedule') || text.includes('课表')) {
    return { icon: 'ri-calendar-schedule-line', bg: 'linear-gradient(135deg, #70dbc9, #2fae95)' }
  }
  if (text.includes('score') || text.includes('成绩')) {
    return { icon: 'ri-bar-chart-box-line', bg: 'linear-gradient(135deg, #8dc8ff, #468ce8)' }
  }
  if (text.includes('exam') || text.includes('考试')) {
    return { icon: 'ri-file-paper-2-line', bg: 'linear-gradient(135deg, #9ea2ff, #7168f1)' }
  }
  if (text.includes('qa') || text.includes('问答')) {
    return { icon: 'ri-chat-quote-line', bg: 'linear-gradient(135deg, #79d8f3, #2f8de4)' }
  }
  if (text.includes('resource') || text.includes('资源')) {
    return { icon: 'ri-folder-chart-line', bg: 'linear-gradient(135deg, #ffb86a, #ff8f3c)' }
  }
  if (text.includes('task') || text.includes('plaza') || text.includes('任务')) {
    return { icon: 'ri-task-line', bg: 'linear-gradient(135deg, #ff9898, #f15f5f)' }
  }
  if (text.includes('profile') || text.includes('verification') || text.includes('学籍')) {
    return { icon: 'ri-id-card-line', bg: 'linear-gradient(135deg, #8fa7ff, #5c71ef)' }
  }
  if (text.includes('favorite') || text.includes('收藏')) {
    return { icon: 'ri-bookmark-3-line', bg: 'linear-gradient(135deg, #f7a9c4, #ef668f)' }
  }
  if (text.includes('message') || text.includes('消息') || text.includes('notice') || text.includes('通知')) {
    return { icon: 'ri-message-3-line', bg: 'linear-gradient(135deg, #ffbf75, #f08c33)' }
  }
  if (text.includes('student') || text.includes('班级')) {
    return { icon: 'ri-team-line', bg: 'linear-gradient(135deg, #7aa5ff, #4b68f3)' }
  }
  if (text.includes('course') || text.includes('课程')) {
    return { icon: 'ri-book-open-line', bg: 'linear-gradient(135deg, #edb580, #c97b4e)' }
  }

  const hash = shortcutHash(`${path}::${title}`)
  return {
    icon: shortcutIconPool[hash % shortcutIconPool.length],
    bg: shortcutGradientPool[hash % shortcutGradientPool.length],
  }
}

export function resolveDefaultShortcutPaths(
  role: string,
  items: Array<{ path: string }>,
  limit = 9,
) {
  const availablePaths = items.map((item) => item.path)
  const preferredPaths = role === 'student' ? Array.from(studentDefaultShortcutPaths) : []
  const result: string[] = []
  const seen = new Set<string>()

  preferredPaths.forEach((path) => {
    if (availablePaths.includes(path) && !seen.has(path)) {
      seen.add(path)
      result.push(path)
    }
  })

  availablePaths.forEach((path) => {
    if (!seen.has(path)) {
      seen.add(path)
      result.push(path)
    }
  })

  return result.slice(0, limit)
}

export function formatDateTime(value?: string | number | Date | null) {
  if (!value) return ''
  const date = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  const hour = `${date.getHours()}`.padStart(2, '0')
  const minute = `${date.getMinutes()}`.padStart(2, '0')
  const second = `${date.getSeconds()}`.padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

export function convertWeekDay(date: Date) {
  const day = date.getDay()
  return day === 0 ? 7 : day
}

export function formatWeekDayLabel(weekDay: number) {
  const labels: Record<number, string> = {
    1: '星期一',
    2: '星期二',
    3: '星期三',
    4: '星期四',
    5: '星期五',
    6: '星期六',
    7: '星期日',
  }
  return labels[Number(weekDay)] || '未知星期'
}

export function formatMonthDay(date: Date) {
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${month}.${day}`
}

export function formatWeeksBadgeText(value?: string) {
  const text = String(value || '').trim()
  if (!text) return '本学期授课'
  if (text.includes('周')) return text
  return `${text}周`
}

function priorityLabel(value?: string) {
  const normalized = String(value || '').trim().toUpperCase()
  const map: Record<string, string> = {
    LOW: '低',
    NORMAL: '普通',
    MEDIUM: '中',
    HIGH: '高',
    URGENT: '紧急',
    P0: '紧急',
    P1: '高',
    P2: '中',
    P3: '普通',
    P4: '低',
  }
  return map[normalized] || String(value || '').trim()
}

function normalizeTaskMetaText(value: unknown) {
  const text = String(value || '').trim()
  if (!text) return ''

  const priorityMatch = text.match(/^(优先级)\s*[:：]?\s*(.+)$/i) || text.match(/^(priority)\s*[:：]?\s*(.+)$/i)
  if (priorityMatch) {
    const rawValue = priorityMatch[2].trim()
    const label = priorityLabel(rawValue)
    return `处理优先级：${label}`
  }

  return text
}

function resolveExamRetakeInfo(
  task: any,
  examAttemptCountMap: Record<string, number>,
  latestExamRecordMap: Record<string, any>,
) {
  const actionType = String(task?.action?.type || '').toLowerCase()
  if (actionType !== 'exam') return null
  const paperId = String(task?.action?.targetId || task?.action?.paperId || task?.action?.row?.paperId || '')
  if (!paperId) return null
  const attemptCount = examAttemptCountMap[paperId] || 0
  if (attemptCount <= 0) return null
  const maxAttemptCount = Number(task?.maxAttemptCount || 0)
  return {
    attemptCount,
    maxAttemptCount,
    remainingCount: maxAttemptCount > 0 ? Math.max(0, maxAttemptCount - attemptCount) : null,
    isMakeup: maxAttemptCount > 0,
    latestRecord: latestExamRecordMap[paperId] || null,
  }
}

export function isWrongTask(task: any) {
  const actionType = String(task?.action?.type || '').toLowerCase()
  if (actionType === 'wrongbook') return true

  const tag = String(task?.tag || '')
  const title = String(task?.title || '')
  const desc = String(task?.desc || '')
  const path = String(task?.action?.path || '')
  const metaText = Array.isArray(task?.meta) ? task.meta.join(' ') : ''
  const combinedText = `${tag} ${title} ${desc} ${path} ${metaText}`.toLowerCase()

  return ['wrongbook', '错题', '回练', '薄弱项'].some((keyword) => combinedText.includes(keyword.toLowerCase()))
}

function resolveTaskTag(
  task: any,
  examAttemptCountMap: Record<string, number>,
  latestExamRecordMap: Record<string, any>,
) {
  const actionType = String(task?.action?.type || '').toLowerCase()
  const tag = String(task?.tag || '').trim()

  if (actionType === 'resume') return '进行中的考试'
  if (actionType === 'exam') {
    const retakeInfo = resolveExamRetakeInfo(task, examAttemptCountMap, latestExamRecordMap)
    if (retakeInfo?.isMakeup) return '可补考'
    return retakeInfo ? '可再次参加考试' : '待参加考试'
  }
  if (actionType === 'wrongbook') return '错题复习'
  if (actionType === 'course') return '课程任务'

  if (tag === '待办考试') return '待参加考试'
  if (tag === '考试任务') return '待参加考试'
  return tag || '任务'
}

function resolveTaskStatusText(
  task: any,
  examAttemptCountMap: Record<string, number>,
  latestExamRecordMap: Record<string, any>,
) {
  const actionType = String(task?.action?.type || '').toLowerCase()
  const status = String(task?.status || '').trim()
  const desc = String(task?.desc || '').trim()

  if (actionType === 'resume') {
    return '你已开始答题，可继续完成'
  }
  if (actionType === 'exam') {
    const retakeInfo = resolveExamRetakeInfo(task, examAttemptCountMap, latestExamRecordMap)
    if (retakeInfo) {
      if (retakeInfo.isMakeup) {
        return `你已参加 ${retakeInfo.attemptCount}/${retakeInfo.maxAttemptCount} 次，还可补考 ${retakeInfo.remainingCount} 次`
      }
      return `你已参加 ${retakeInfo.attemptCount} 次，可再次作答`
    }
    return '考试尚未开始作答'
  }
  if (status === '进行中') return '正在处理中'
  if (status === '待处理') return desc || '等待你处理'
  return status || desc || '待处理'
}

function resolveTaskMetaClass(tagType?: string) {
  if (tagType === 'danger') return 'text-danger'
  if (tagType === 'warning') return 'text-warning'
  if (tagType === 'success') return 'text-success'
  return 'text-muted'
}

function resolveTaskActionLabel(
  task: any,
  examAttemptCountMap: Record<string, number>,
  latestExamRecordMap: Record<string, any>,
) {
  const type = task?.action?.type
  const dispatchId = Number(task?.action?.row?.dispatchId || 0)
  if (dispatchId > 0) return '去处理'
  if (type === 'resume') return '继续作答'
  if (type === 'exam') {
    const retakeInfo = resolveExamRetakeInfo(task, examAttemptCountMap, latestExamRecordMap)
    if (retakeInfo?.isMakeup) return '去补考'
    return retakeInfo ? '再次考试' : '去考试'
  }
  if (type === 'course') return '查看课程'
  if (type === 'wrongbook') return '去复习'
  if (type === 'record') return '查看记录'
  return '查看详情'
}

function buildTaskCard(
  task: any,
  examAttemptCountMap: Record<string, number>,
  latestExamRecordMap: Record<string, any>,
): DashboardTaskCard {
  const actionType = task?.action?.type || ''
  let icon = 'ri-task-line'
  let iconClass = 'task-icon--default'
  if (actionType === 'exam' || actionType === 'resume' || actionType === 'record') {
    icon = 'ri-file-paper-2-line'
    iconClass = 'task-icon--exam'
  } else if (actionType === 'wrongbook') {
    icon = 'ri-book-read-line'
    iconClass = 'task-icon--homework'
  } else if (actionType === 'course') {
    icon = 'ri-calendar-check-line'
    iconClass = 'task-icon--practice'
  }

  const retakeInfo = resolveExamRetakeInfo(task, examAttemptCountMap, latestExamRecordMap)
  return {
    raw: task,
    key: String(task?.key || Math.random()),
    title: task?.title || '未命名任务',
    tag: resolveTaskTag(task, examAttemptCountMap, latestExamRecordMap),
    tagType: task?.tagType || 'info',
    meta: [
      ...(Array.isArray(task?.meta) ? task.meta.map((item: unknown) => normalizeTaskMetaText(item)).filter(Boolean) : []),
      ...(retakeInfo?.isMakeup && retakeInfo.remainingCount !== null ? [`剩余补考次数：${retakeInfo.remainingCount}`] : []),
      ...(retakeInfo?.latestRecord?.submitTime ? [`最近参加：${String(retakeInfo.latestRecord.submitTime).slice(0, 16).replace('T', ' ')}`] : []),
    ],
    icon,
    iconClass,
    statusText: resolveTaskStatusText(task, examAttemptCountMap, latestExamRecordMap),
    metaClass: resolveTaskMetaClass(task?.tagType),
    actionLabel: resolveTaskActionLabel(task, examAttemptCountMap, latestExamRecordMap),
    recommendationReason: String(task?.recommendationReason || '').trim(),
  }
}

export function buildTaskCards(taskCenter: any, examRecords: any[] = [], ongoingPaperId?: string | number | null) {
  const source = Array.isArray(taskCenter?.homepageTasks) && taskCenter.homepageTasks.length
    ? taskCenter.homepageTasks
    : [
      ...(taskCenter?.todoTasks || []),
      ...(taskCenter?.recommendedTasks || []),
    ]

  const examAttemptCountMap = examRecords.reduce((acc: Record<string, number>, item: any) => {
    const key = String(item.paperId || '')
    if (!key) return acc
    acc[key] = (acc[key] || 0) + 1
    return acc
  }, {})

  const latestExamRecordMap = examRecords.reduce((acc: Record<string, any>, item: any) => {
    const key = String(item.paperId || '')
    if (!key || acc[key]) return acc
    acc[key] = item
    return acc
  }, {})

  const seen = new Set<string>()
  const activeOngoingPaperId = String(ongoingPaperId || '')
  const dedupedTasks = source.filter((item: any) => {
    const actionType = String(item?.action?.type || '').toLowerCase()
    const taskPaperId = String(item?.action?.targetId || item?.action?.paperId || item?.action?.row?.paperId || '')

    if (activeOngoingPaperId && actionType === 'exam' && taskPaperId && taskPaperId === activeOngoingPaperId) {
      return false
    }

    const key = String(item?.key || '')
    if (!key || seen.has(key)) return false
    seen.add(key)
    return true
  })

  const regularTasks = dedupedTasks.filter((item: any) => !isWrongTask(item))
  const wrongReviewTasks = dedupedTasks.filter((item: any) => isWrongTask(item))
  const selectedTasks = regularTasks.length
    ? sortTasksWithFeedback(regularTasks)
    : wrongReviewTasks.slice(0, 1)

  return selectedTasks.map((item: any) => buildTaskCard(item, examAttemptCountMap, latestExamRecordMap))
}

export function buildTaskOverviewBadges(taskCenter: any) {
  const summary = taskCenter?.recommendationSummary || {}
  const badges: string[] = []
  const urgentCount = Number(summary.urgentCount || 0)
  const examCount = Number(summary.examCount || 0)
  const wrongbookCount = Number(summary.wrongbookCount || 0)
  if (urgentCount > 0) badges.push(`优先处理 ${urgentCount}`)
  if (examCount > 0) badges.push(`考试相关 ${examCount}`)
  if (wrongbookCount > 0) badges.push(`薄弱巩固 ${wrongbookCount}`)
  const reasons = Array.isArray(summary.topReasons) ? summary.topReasons : []
  reasons.slice(0, 2).forEach((reason: string) => {
    if (reason) badges.push(reason)
  })
  return badges.slice(0, 4)
}

export function buildStaticTaskCards(items: StaticDashboardTaskInput[]): DashboardTaskCard[] {
  return items.map((item) => {
    const iconType = item.iconType || 'default'
    let icon = 'ri-task-line'
    let iconClass = 'task-icon--default'
    if (iconType === 'exam') {
      icon = 'ri-file-paper-2-line'
      iconClass = 'task-icon--exam'
    } else if (iconType === 'homework') {
      icon = 'ri-book-read-line'
      iconClass = 'task-icon--homework'
    } else if (iconType === 'practice') {
      icon = 'ri-calendar-check-line'
      iconClass = 'task-icon--practice'
    }

    return {
      raw: {
        key: item.key,
        title: item.title,
        desc: item.desc,
        action: {
          type: 'route',
          path: item.path,
        },
      },
      key: item.key,
      title: item.title,
      tag: item.tag || '任务',
      tagType: item.tagType || 'info',
      meta: item.meta || [],
      icon,
      iconClass,
      statusText: item.statusText || item.desc || '待处理',
      metaClass: resolveTaskMetaClass(item.tagType || 'info'),
      actionLabel: item.actionLabel || '查看详情',
      recommendationReason: item.recommendationReason || '',
    }
  })
}
