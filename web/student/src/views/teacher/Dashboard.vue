<template>
  <PortalDashboardShell
    v-model:schedule-mode="activeScheduleMode"
    role="teacher"
    theme-color="#12795a"
    :user-name="userName"
    :current-semester-label="currentSemesterLabel"
    :current-teaching-week="currentTeachingWeek"
    :secondary-info="secondaryInfo"
    :loading="loading"
    preview-module-title="教学日程预览"
    preview-link-text="查看课表"
    preview-link-path="/teacher/schedule"
    :schedule-modes="scheduleModes"
    :preview-summary-title="activeScheduleTitle"
    :preview-summary-text="activeScheduleSummary"
    :preview-cards="activeScheduleCards"
    :preview-empty-description="activeScheduleEmptyDescription"
    task-module-title="近期任务"
    :task-overview-badges="taskOverviewBadges"
    :task-cards="recentTasks"
    :drawer-todo-list="drawerTodoList"
    :drawer-message-list="drawerMessageList"
    :drawer-notice-list="drawerNoticeList"
    :drawer-loading="drawerLoading"
    @task-click="openTask"
    @open-drawer="openQuickDrawer"
    @drawer-task-click="openDrawerTask"
    @drawer-message-click="openDrawerMessage"
    @message-center="openMessageCenter"
  />
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import PortalDashboardShell from '@/components/dashboard/PortalDashboardShell.vue'
import {
  getPortalTeacherSchedule,
  getPortalUnreadMessages,
  getTeacherDashboard,
  listPortalNotice,
  listPortalTermOptions,
} from '@/api/portal'
import usePortalUserStore from '@/store/user'
import {
  buildStaticTaskCards,
  convertWeekDay,
  formatDateTime,
  formatMonthDay,
  formatWeekDayLabel,
  formatWeeksBadgeText,
} from '@/utils/portalDashboard'

const router = useRouter()
const userStore = usePortalUserStore()

const loading = ref(true)
const drawerLoading = ref(false)
const dashboard = ref<any>({})
const schedulePayload = ref<any>({})
const termOptions = ref<any[]>([])
const activeScheduleMode = ref('today')

const scheduleModes = [
  { key: 'today', label: '今日授课' },
  { key: 'tomorrow', label: '明日授课' },
]

const userName = computed(() => userStore.user?.realName || userStore.user?.userName || '老师')
const currentTerm = computed(() => {
  const currentTermId = schedulePayload.value?.termId
  return termOptions.value.find((item: any) => item.value === currentTermId)
    || termOptions.value.find((item: any) => item.isCurrent === '1')
    || termOptions.value[0]
    || null
})
const currentSemesterLabel = computed(() => {
  if (!currentTerm.value) return '当前学期'
  const schoolYear = currentTerm.value.schoolYear ? `${currentTerm.value.schoolYear}` : ''
  const termName = currentTerm.value.termName ? `${currentTerm.value.termName}` : ''
  return [schoolYear, termName].filter(Boolean).join(' ')
})
const currentTeachingWeek = computed(() => Number(schedulePayload.value?.currentWeek || 1))
const secondaryInfo = computed(() => [
  `授课课程：${dashboard.value.courseCount || 0} 门`,
  `覆盖班级：${dashboard.value.classCount || 0} 个`,
  `资源沉淀：${dashboard.value.resourceCount || 0} 份`,
  `题库规模：${dashboard.value.questionCount || 0} 题`,
  `上次登录时间：${formatDateTime(userStore.user?.loginDate) || '暂无记录'}`,
])

const todayMeta = computed(() => {
  const today = new Date()
  return {
    date: today,
    weekDay: convertWeekDay(today),
    targetWeek: Number(schedulePayload.value?.currentWeek || 1),
  }
})

const tomorrowMeta = computed(() => {
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  const todayWeekDay = convertWeekDay(new Date())
  const tomorrowWeekDay = convertWeekDay(tomorrow)
  const baseWeek = Number(schedulePayload.value?.currentWeek || 1)
  const targetWeek = tomorrowWeekDay < todayWeekDay ? baseWeek + 1 : baseWeek
  return {
    date: tomorrow,
    weekDay: tomorrowWeekDay,
    targetWeek,
  }
})

const todayScheduleCards = computed(() => buildScheduleCards(todayMeta.value, 'today'))
const tomorrowScheduleCards = computed(() => buildScheduleCards(tomorrowMeta.value, 'tomorrow'))
const activeScheduleCards = computed(() => activeScheduleMode.value === 'today' ? todayScheduleCards.value : tomorrowScheduleCards.value)
const activeScheduleTitle = computed(() => {
  const meta = activeScheduleMode.value === 'today' ? todayMeta.value : tomorrowMeta.value
  const prefix = activeScheduleMode.value === 'today' ? '今日授课' : '明日授课'
  return `${prefix} · ${formatMonthDay(meta.date)}`
})
const activeScheduleSummary = computed(() => {
  const count = activeScheduleCards.value.length
  if (!count) {
    return activeScheduleMode.value === 'today'
      ? '今天暂无授课安排，可集中处理备课、资源或班级沟通工作。'
      : '明天暂无授课安排，可以提前调整教学节奏和备课重心。'
  }
  return activeScheduleMode.value === 'today'
    ? `今天共有 ${count} 节教学安排，建议优先确认最近一节课的班级与教室。`
    : `明天共有 ${count} 节教学安排，可以提前查看班级分布与地点变更。`
})
const activeScheduleEmptyDescription = computed(() =>
  activeScheduleMode.value === 'today'
    ? '今天没有授课安排，适合整理教案与资源。'
    : '明天没有授课安排，适合预留教研时间。',
)

const taskSource = computed(() => buildStaticTaskCards([
  {
    key: 'teacher-course-manage',
    title: '查看授课课程',
    desc: '进入课程管理页查看教学班、学生和授课范围',
    tag: '课程管理',
    tagType: 'primary',
    meta: [`课程数：${dashboard.value.courseCount || 0}`],
    actionLabel: '查看课程',
    path: '/teacher/courses',
    iconType: 'practice',
  },
  {
    key: 'teacher-schedule-check',
    title: '查看本周课表',
    desc: `第 ${currentTeachingWeek.value} 教学周的授课安排已经同步，可按周切换查看`,
    tag: '教学课表',
    tagType: 'success',
    meta: [`排课条目：${Array.isArray(schedulePayload.value?.activities) ? schedulePayload.value.activities.length : 0}`],
    actionLabel: '查看课表',
    path: '/teacher/schedule',
    iconType: 'practice',
  },
  {
    key: 'teacher-resources',
    title: '整理教学资源',
    desc: '查看资源中心中的资料、浏览量和下载情况',
    tag: '资源中心',
    tagType: 'warning',
    meta: [`资源数：${dashboard.value.resourceCount || 0}`],
    actionLabel: '查看资源',
    path: '/teacher/resources',
    iconType: 'homework',
  },
  {
    key: 'teacher-course-offerings',
    title: '查看全校开课',
    desc: '快速查询全校课程开设信息，辅助排课和教学对照',
    tag: '开课查询',
    tagType: 'info',
    meta: [`覆盖班级：${dashboard.value.classCount || 0}`],
    actionLabel: '立即查看',
    path: '/teacher/course-offerings',
    iconType: 'exam',
  },
]))

const recentTasks = computed(() => taskSource.value)
const taskOverviewBadges = computed(() => [
  `课程 ${dashboard.value.courseCount || 0}`,
  `班级 ${dashboard.value.classCount || 0}`,
  `资源 ${dashboard.value.resourceCount || 0}`,
  `题目 ${dashboard.value.questionCount || 0}`,
])
const drawerTodoList = computed(() => recentTasks.value.map((item) => ({
  key: item.key,
  title: item.title,
  desc: item.statusText,
  action: item.raw?.action,
})))
const drawerMessageList = ref<any[]>([])
const drawerNoticeList = ref<any[]>([])

function buildScheduleCards(meta: { weekDay: number; targetWeek: number }, prefix: string) {
  const activities = Array.isArray(schedulePayload.value?.activities) ? schedulePayload.value.activities : []
  return activities
    .filter((item: any) => {
      if (Number(item.weekDay) !== meta.weekDay) return false
      const weekIndexes = Array.isArray(item.weekIndexes) ? item.weekIndexes.map((value: any) => Number(value)) : []
      return !weekIndexes.length || weekIndexes.includes(meta.targetWeek)
    })
    .sort((left: any, right: any) => Number(left.startSection || 0) - Number(right.startSection || 0))
    .map((item: any) => ({
      key: `${prefix}-${item.scheduleId || item.classCourseId}-${item.weekDay}-${item.startSection}`,
      title: item.courseName || '未命名课程',
      tag: item.className || formatWeekDayLabel(item.weekDay),
      badges: [
        `${item.startSection || '-'}-${item.endSection || item.startSection || '-'} 节`,
        formatWeeksBadgeText(item.weeksStr || item.weeksText || ''),
      ],
      facts: [
        { label: '时间', value: `${formatWeekDayLabel(item.weekDay)} ${item.startTime || '--:--'} - ${item.endTime || '--:--'}` },
        { label: '地点', value: [item.campus, item.buildingName, item.classroom].filter(Boolean).join(' / ') || '地点待定' },
        { label: '班级', value: item.className || '未配置教学班' },
      ],
      raw: item,
    }))
}

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
}

async function loadDashboardData() {
  const teacherId = userStore.user?.userId
  if (!teacherId) return
  const res = await getTeacherDashboard({ teacherId })
  dashboard.value = res.data || {}
}

async function loadScheduleData() {
  const teacherId = userStore.user?.userId
  if (!teacherId) return
  const current = termOptions.value.find((item: any) => item.isCurrent === '1') || termOptions.value[0]
  const res = await getPortalTeacherSchedule({
    teacherId,
    termId: current?.value,
  })
  schedulePayload.value = res.data || {}
}

async function loadData() {
  if (!userStore.user?.userId) return
  loading.value = true
  try {
    await loadTerms()
    await Promise.all([
      loadDashboardData(),
      loadScheduleData(),
    ])
    await preloadDrawerBadges()
  } finally {
    setTimeout(() => {
      loading.value = false
    }, 300)
  }
}

function openTask(task: any) {
  const path = task?.action?.path || '/teacher/dashboard'
  router.push(path)
}

function openDrawerTask(task: any) {
  openTask(task)
}

async function openQuickDrawer() {
  drawerLoading.value = true
  try {
    const [msgRes, noticeRes] = await Promise.all([
      getPortalUnreadMessages({ pageNum: 1, pageSize: 5 }),
      listPortalNotice({ pageNum: 1, pageSize: 5 }),
    ])
    drawerMessageList.value = msgRes.rows || msgRes.data || []
    drawerNoticeList.value = noticeRes.rows || noticeRes.data || []
  } finally {
    drawerLoading.value = false
  }
}

async function preloadDrawerBadges() {
  try {
    const [msgRes, noticeRes] = await Promise.all([
      getPortalUnreadMessages({ pageNum: 1, pageSize: 5 }),
      listPortalNotice({ pageNum: 1, pageSize: 5 }),
    ])
    drawerMessageList.value = msgRes.rows || msgRes.data || []
    drawerNoticeList.value = noticeRes.rows || noticeRes.data || []
  } catch (_) {
    // silent — badge counts are non-critical
  }
}

function openDrawerMessage(payload?: { tab: 'message' | 'notice'; item: any }) {
  router.push({
    path: '/teacher/messages',
    query: { tab: payload?.tab === 'notice' ? 'notice' : 'message' },
  })
}

function openMessageCenter(tab?: 'todo' | 'message' | 'notice') {
  router.push({
    path: '/teacher/messages',
    query: { tab: tab === 'notice' ? 'notice' : tab === 'todo' ? 'todo' : 'message' },
  })
}

onMounted(loadData)
</script>
