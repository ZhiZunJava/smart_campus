<template>
  <PortalDashboardShell
    v-model:schedule-mode="activeScheduleMode"
    role="advisor"
    theme-color="#2563eb"
    :user-name="userName"
    :current-semester-label="currentSemesterLabel"
    :current-teaching-week="currentTeachingWeek"
    :secondary-info="secondaryInfo"
    :loading="loading"
    preview-module-title="班级概览"
    preview-link-text="查看学生"
    preview-link-path="/advisor/students"
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
    :shortcut-items-override="shortcutItems"
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
  getAdvisorDashboard,
  getPortalUnreadMessages,
  listAdvisorScores,
  listAdvisorStudents,
  listPortalNotice,
  listPortalTermOptions,
} from '@/api/portal'
import usePortalUserStore from '@/store/user'
import { resolveCurrentWeek } from '@/utils/termWeek'
import { buildStaticTaskCards, formatDateTime } from '@/utils/portalDashboard'

const router = useRouter()
const userStore = usePortalUserStore()

const loading = ref(true)
const drawerLoading = ref(false)
const dashboard = ref<any>({})
const termOptions = ref<any[]>([])
const studentRows = ref<any[]>([])
const scoreRows = ref<any[]>([])
const activeScheduleMode = ref('today')
const shortcutItems = [
  {
    path: '/advisor/dashboard',
    title: '辅导员概览',
    icon: 'ri-home-5-line',
    bg: 'linear-gradient(135deg, #5aa9ff, #2f6ee5)',
  },
  {
    path: '/advisor/students?entry=classes',
    title: '负责班级',
    icon: 'ri-community-line',
    bg: 'linear-gradient(135deg, #7ecbff, #3b82f6)',
  },
  {
    path: '/advisor/scores',
    title: '成绩管理',
    icon: 'ri-bar-chart-box-line',
    bg: 'linear-gradient(135deg, #89d5ff, #3c9fec)',
  },
  {
    path: '/advisor/affairs',
    title: '事务审核',
    icon: 'ri-briefcase-4-line',
    bg: 'linear-gradient(135deg, #8f9fb8, #5e718d)',
  },
  {
    path: '/advisor/students',
    title: '学生管理',
    icon: 'ri-team-line',
    bg: 'linear-gradient(135deg, #7aa5ff, #4b68f3)',
  },
]

const scheduleModes = [
  { key: 'today', label: '今日重点' },
  { key: 'tomorrow', label: '明日跟进' },
]

const userName = computed(() => userStore.user?.realName || userStore.user?.userName || '辅导员')
const classOptions = computed(() => dashboard.value.classOptions || [])
const currentTerm = computed(() => {
  const current = termOptions.value.find((item: any) => item.value === dashboard.value.termId)
  return current || termOptions.value.find((item: any) => item.isCurrent === '1') || termOptions.value[0] || null
})
const currentSemesterLabel = computed(() => {
  if (!currentTerm.value) return dashboard.value.termLabel || '当前学期'
  const schoolYear = currentTerm.value.schoolYear ? `${currentTerm.value.schoolYear}` : ''
  const termName = currentTerm.value.termName ? `${currentTerm.value.termName}` : ''
  return [schoolYear, termName].filter(Boolean).join(' ') || dashboard.value.termLabel || '当前学期'
})
const currentTeachingWeek = computed(() => resolveCurrentWeek(currentTerm.value || {}))
const secondaryInfo = computed(() => [
  `负责班级：${dashboard.value.classCount || 0} 个`,
  `学生总数：${dashboard.value.studentCount || 0} 人`,
  `课程数：${dashboard.value.courseCount || 0} 门`,
  `平均分：${formatScore(dashboard.value.avgScore)}`,
  `上次登录时间：${formatDateTime(userStore.user?.loginDate) || '暂无记录'}`,
])

const classSummaryCards = computed(() => {
  const scoreSummaryMap = scoreRows.value.reduce((acc: Record<string, { scoreCount: number; scoreTotal: number }>, item: any) => {
    const key = String(item.classId || '')
    if (!key) return acc
    if (!acc[key]) {
      acc[key] = { scoreCount: 0, scoreTotal: 0 }
    }
    acc[key].scoreCount += 1
    acc[key].scoreTotal += Number(item.totalScore || 0)
    return acc
  }, {})
  const studentCountMap = studentRows.value.reduce((acc: Record<string, number>, item: any) => {
    const key = String(item.classId || '')
    if (!key) return acc
    acc[key] = (acc[key] || 0) + 1
    return acc
  }, {})

  return classOptions.value.map((item: any) => {
    const key = String(item.classId || item.value || '')
    const scoreSummary = scoreSummaryMap[key] || { scoreCount: 0, scoreTotal: 0 }
    return {
      classId: item.classId || item.value,
      className: item.className || item.label,
      deptName: item.deptName,
      studentCount: studentCountMap[key] || 0,
      scoreCount: scoreSummary.scoreCount,
      avgScore: scoreSummary.scoreCount ? (scoreSummary.scoreTotal / scoreSummary.scoreCount) : null,
    }
  })
})

const todayCards = computed(() =>
  [...classSummaryCards.value]
    .sort((left, right) => right.studentCount - left.studentCount)
    .slice(0, 3)
    .map((item) => ({
      key: `today-${item.classId || item.className}`,
      title: item.className || '未命名班级',
      tag: item.deptName || '负责班级',
      badges: [
        `${item.studentCount} 人`,
        item.scoreCount ? `${item.scoreCount} 条成绩` : '待汇总成绩',
      ],
      facts: [
        { label: '平均分', value: formatScore(item.avgScore) },
        { label: '重点', value: item.studentCount > 40 ? '人数较多，建议优先巡检' : '班级规模平稳' },
        { label: '去向', value: '可进入学生管理或成绩管理继续跟进' },
      ],
      raw: item,
    })),
)

const tomorrowCards = computed(() =>
  [...classSummaryCards.value]
    .sort((left, right) => {
      const leftScore = Number.isFinite(Number(left.avgScore)) ? Number(left.avgScore) : 999
      const rightScore = Number.isFinite(Number(right.avgScore)) ? Number(right.avgScore) : 999
      return leftScore - rightScore
    })
    .slice(0, 3)
    .map((item) => ({
      key: `tomorrow-${item.classId || item.className}`,
      title: item.className || '未命名班级',
      tag: item.avgScore != null && Number(item.avgScore) < 60 ? '成绩风险' : '跟进提醒',
      badges: [
        `均分 ${formatScore(item.avgScore)}`,
        item.scoreCount ? `${item.scoreCount} 条成绩` : '待发布成绩',
      ],
      facts: [
        { label: '院系', value: item.deptName || '未配置院系' },
        { label: '人数', value: `${item.studentCount} 人` },
        { label: '建议', value: item.avgScore != null && Number(item.avgScore) < 60 ? '建议优先复盘班级成绩' : '建议持续关注班级动态' },
      ],
      raw: item,
    })),
)

const activeScheduleCards = computed(() => activeScheduleMode.value === 'today' ? todayCards.value : tomorrowCards.value)
const activeScheduleTitle = computed(() => activeScheduleMode.value === 'today' ? '今日重点班级' : '明日跟进清单')
const activeScheduleSummary = computed(() => {
  const count = activeScheduleCards.value.length
  if (!count) {
    return activeScheduleMode.value === 'today'
      ? '当前暂无可展示的班级概览数据，可以先查看学生或成绩列表。'
      : '当前暂无待跟进班级数据，班级运行相对平稳。'
  }
  return activeScheduleMode.value === 'today'
    ? `已为你挑选 ${count} 个最值得优先关注的班级，适合先处理人数较多或成绩待观察的对象。`
    : `已为你整理 ${count} 个建议跟进的班级，方便提前安排谈话、成绩复盘或家校沟通。`
})
const activeScheduleEmptyDescription = computed(() =>
  activeScheduleMode.value === 'today'
    ? '暂无班级概览数据。'
    : '暂无待跟进班级数据。',
)

const taskSource = computed(() => buildStaticTaskCards([
  {
    key: 'advisor-students',
    title: '查看学生名单',
    desc: '进入学生管理页，快速浏览班级学生基础档案',
    tag: '学生管理',
    tagType: 'primary',
    meta: [`学生总数：${dashboard.value.studentCount || 0}`],
    actionLabel: '查看学生',
    path: '/advisor/students',
    iconType: 'practice',
  },
  {
    key: 'advisor-scores',
    title: '查看成绩概况',
    desc: '进入成绩管理页，继续按班级和课程筛选成绩',
    tag: '成绩管理',
    tagType: 'success',
    meta: [`平均分：${formatScore(dashboard.value.avgScore)}`],
    actionLabel: '查看成绩',
    path: '/advisor/scores',
    iconType: 'exam',
  },
  {
    key: 'advisor-class-focus',
    title: '重点班级跟进',
    desc: '优先关注人数较多或平均分偏低的班级情况',
    tag: '班级跟进',
    tagType: 'warning',
    meta: [`负责班级：${dashboard.value.classCount || 0} 个`],
    actionLabel: '继续查看',
    path: '/advisor/dashboard',
    iconType: 'homework',
  },
]))

const recentTasks = computed(() => taskSource.value)
const taskOverviewBadges = computed(() => [
  `班级 ${dashboard.value.classCount || 0}`,
  `学生 ${dashboard.value.studentCount || 0}`,
  `课程 ${dashboard.value.courseCount || 0}`,
  `均分 ${formatScore(dashboard.value.avgScore)}`,
])
const drawerTodoList = computed(() => recentTasks.value.map((item) => ({
  key: item.key,
  title: item.title,
  desc: item.statusText,
  action: item.raw?.action,
})))
const drawerMessageList = ref<any[]>([])
const drawerNoticeList = ref<any[]>([])

function formatScore(value: any) {
  const num = Number(value)
  return Number.isFinite(num) ? num.toFixed(1) : '--'
}

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
}

async function loadDashboardData() {
  const advisorUserId = userStore.user?.userId
  if (!advisorUserId) return
  const res = await getAdvisorDashboard({ advisorUserId, termId: currentTerm.value?.value })
  dashboard.value = res.data || {}
}

async function loadAdvisorAuxiliaryData() {
  const advisorUserId = userStore.user?.userId
  if (!advisorUserId) return
  const termId = termOptions.value.find((item: any) => item.isCurrent === '1')?.value || currentTerm.value?.value
  const [studentRes, scoreRes] = await Promise.all([
    listAdvisorStudents({ advisorUserId }),
    listAdvisorScores({ advisorUserId, termId }),
  ])
  studentRows.value = studentRes.data?.items || []
  scoreRows.value = scoreRes.data?.items || []
}

async function loadData() {
  if (!userStore.user?.userId) return
  loading.value = true
  try {
    await loadTerms()
    await Promise.all([
      loadDashboardData(),
      loadAdvisorAuxiliaryData(),
    ])
    preloadDrawerBadges()
  } finally {
    setTimeout(() => {
      loading.value = false
    }, 300)
  }
}

function openTask(task: any) {
  const path = task?.action?.path || '/advisor/dashboard'
  router.push(path)
}

function openDrawerTask(task: any) {
  openTask(task)
}

async function openQuickDrawer(tab?: string) {
  drawerLoading.value = true
  try {
    const userId = userStore.user?.userId
    if (!userId) return
    if (tab === 'message') {
      const res = await getPortalUnreadMessages({ userId, limit: 6 })
      drawerMessageList.value = (res.data || []).filter((item: any) => item.readFlag === '0')
    } else if (tab === 'notice') {
      const res = await listPortalNotice({ limit: 6 })
      drawerNoticeList.value = (res.data || []).filter((item: any) => item.readFlag === '0')
    }
  } finally {
    drawerLoading.value = false
  }
}

async function preloadDrawerBadges() {
  const userId = userStore.user?.userId
  if (!userId) return
  try {
    const [msgRes, noticeRes] = await Promise.all([
      getPortalUnreadMessages({ userId, limit: 20 }),
      listPortalNotice({ limit: 20 }),
    ])
    drawerMessageList.value = (msgRes.data || []).filter((item: any) => item.readFlag === '0')
    drawerNoticeList.value = (noticeRes.data || []).filter((item: any) => item.readFlag === '0')
  } catch { /* silent */ }
}

function openDrawerMessage(payload?: { tab: 'message' | 'notice'; item: any }) {
  router.push({
    path: '/advisor/messages',
    query: { tab: payload?.tab === 'notice' ? 'notice' : 'message' },
  })
}

function openMessageCenter(tab?: 'todo' | 'message' | 'notice') {
  router.push({
    path: '/advisor/messages',
    query: { tab: tab === 'notice' ? 'notice' : tab === 'todo' ? 'todo' : 'message' },
  })
}

onMounted(loadData)
</script>
