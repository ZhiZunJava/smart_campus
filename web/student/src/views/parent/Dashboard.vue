<template>
  <PortalDashboardShell
    v-model:schedule-mode="activeScheduleMode"
    role="parent"
    theme-color="#c2412d"
    :user-name="userName"
    :current-semester-label="currentSemesterLabel"
    :current-teaching-week="currentTeachingWeek"
    :secondary-info="secondaryInfo"
    :loading="loading"
    preview-module-title="孩子课程预览"
    preview-link-text="查看课表"
    preview-link-path="/parent/schedule"
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
  getParentDashboard,
  getPortalParentSchedule,
  getPortalUnreadMessages,
  listPortalNotice,
  listPortalParentChildren,
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
const childOptions = ref<any[]>([])
const selectedChildId = ref<number | undefined>()
const activeScheduleMode = ref('today')

const scheduleModes = [
  { key: 'today', label: '今日课程' },
  { key: 'tomorrow', label: '明日课程' },
]

const userName = computed(() => userStore.user?.realName || userStore.user?.userName || '家长')
const currentChild = computed(() => childOptions.value.find((item: any) => item.value === selectedChildId.value) || null)
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
const secondaryInfo = computed(() => {
  if (!childOptions.value.length) {
    return [
      '孩子账号：暂未绑定',
      '绑定状态：待绑定',
      `上次登录时间：${formatDateTime(userStore.user?.loginDate) || '暂无记录'}`,
    ]
  }
  return [
    `孩子账号：${currentChild.value?.studentNo || '--'}`,
    `考试记录：${dashboard.value.examRecordCount || 0} 条`,
    `学习记录：${dashboard.value.studyRecordCount || 0} 条`,
    `上次登录时间：${formatDateTime(userStore.user?.loginDate) || '暂无记录'}`,
  ]
})

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
  const prefix = activeScheduleMode.value === 'today' ? '今日课程' : '明日课程'
  return `${prefix} · ${formatMonthDay(meta.date)}`
})
const activeScheduleSummary = computed(() => {
  if (!childOptions.value.length) {
    return '暂未绑定孩子，绑定后可查看孩子的课程安排。'
  }
  const childName = currentChild.value?.studentName || '孩子'
  const count = activeScheduleCards.value.length
  if (!count) {
    return activeScheduleMode.value === 'today'
      ? `${childName} 今天暂无排课，可以先查看课程与课表信息。`
      : `${childName} 明天暂无排课，适合提前安排作息与学习节奏。`
  }
  return activeScheduleMode.value === 'today'
    ? `${childName} 今天共有 ${count} 门课程，建议优先确认最近一节课的时间和教室。`
    : `${childName} 明天共有 ${count} 门课程，可以提前帮孩子留意节次与授课地点。`
})
const activeScheduleEmptyDescription = computed(() =>
  activeScheduleMode.value === 'today'
    ? '今天没有课程安排，适合整理学习计划。'
    : '明天没有课程安排，做好充分休息吧。',
)

const baseTaskItems = computed(() => {
  const hasBoundChild = childOptions.value.length > 0
  const childName = currentChild.value?.studentName || '孩子'

  if (!hasBoundChild) {
    return [
      {
        key: 'parent-no-child',
        title: '尚未绑定孩子',
        desc: '当前账号暂未绑定孩子信息，请联系学校管理员或班主任完成亲子关系绑定后再使用家长端功能。',
        tag: '待绑定',
        tagType: 'danger',
        meta: ['请联系管理员完成绑定'],
        actionLabel: '了解详情',
        path: '/parent/dashboard',
        iconType: 'homework' as const,
      },
    ]
  }

  return [
    {
      key: 'parent-course-list',
      title: '查看孩子课程',
      desc: `进入 ${childName} 的课程列表，查看课程和教师信息`,
      tag: '课程概览',
      tagType: 'primary',
      meta: [`当前学期：${currentSemesterLabel.value}`],
      actionLabel: '查看课程',
      path: '/parent/courses',
      iconType: 'practice' as const,
    },
    {
      key: 'parent-schedule',
      title: '查看孩子课表',
      desc: `第 ${currentTeachingWeek.value} 教学周的课表已经同步，可按周切换查看`,
      tag: '课表跟进',
      tagType: 'success',
      meta: [`排课条目：${Array.isArray(schedulePayload.value?.activities) ? schedulePayload.value.activities.length : 0}`],
      actionLabel: '查看课表',
      path: '/parent/schedule',
      iconType: 'practice' as const,
    },
  ]
})

const recentTasks = computed(() => buildStaticTaskCards(baseTaskItems.value))
const taskOverviewBadges = computed(() => {
  if (!childOptions.value.length) {
    return ['暂未绑定孩子', '请联系管理员完成绑定']
  }
  return [
    `已绑定孩子 ${childOptions.value.length}`,
    `考试记录 ${dashboard.value.examRecordCount || 0}`,
    `当前查看 ${currentChild.value?.studentName || '未选择孩子'}`,
  ].filter(Boolean)
})
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
      tag: formatWeekDayLabel(item.weekDay),
      badges: [
        `${item.startSection || '-'}-${item.endSection || item.startSection || '-'} 节`,
        formatWeeksBadgeText(item.weeksStr || item.weeksText || ''),
      ],
      facts: [
        { label: '时间', value: `${formatWeekDayLabel(item.weekDay)} ${item.startTime || '--:--'} - ${item.endTime || '--:--'}` },
        { label: '地点', value: [item.campus, item.buildingName, item.classroom].filter(Boolean).join(' / ') || '地点待定' },
        { label: '教师', value: item.teacherName || '待分配教师' },
      ],
      raw: item,
    }))
}

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
}

async function loadChildren() {
  const parentUserId = userStore.user?.userId
  if (!parentUserId) return
  const res = await listPortalParentChildren({ parentUserId })
  childOptions.value = res.data || []
  if (!selectedChildId.value && childOptions.value.length) {
    selectedChildId.value = childOptions.value[0].value
  }
}

async function loadDashboardData() {
  const parentUserId = userStore.user?.userId
  if (!parentUserId || !selectedChildId.value) return
  const res = await getParentDashboard({ parentUserId, studentUserId: selectedChildId.value })
  dashboard.value = res.data || {}
}

async function loadScheduleData() {
  const parentUserId = userStore.user?.userId
  if (!parentUserId || !selectedChildId.value) return
  const current = termOptions.value.find((item: any) => item.isCurrent === '1') || termOptions.value[0]
  const res = await getPortalParentSchedule({
    parentUserId,
    studentUserId: selectedChildId.value,
    termId: current?.value,
  })
  schedulePayload.value = res.data || {}
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

async function loadData() {
  if (!userStore.user?.userId) return
  loading.value = true
  try {
    await loadTerms()
    await loadChildren()
    preloadDrawerBadges()
    if (!selectedChildId.value) return
    await Promise.all([
      loadDashboardData(),
      loadScheduleData(),
    ])
  } finally {
    setTimeout(() => {
      loading.value = false
    }, 300)
  }
}

async function handleChildChange() {
  if (!selectedChildId.value) return
  await loadData()
}

function openTask(task: any) {
  const path = task?.action?.path || '/parent/dashboard'
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

function openDrawerMessage(payload?: { tab: 'message' | 'notice'; item: any }) {
  router.push({
    path: '/parent/messages',
    query: { tab: payload?.tab === 'notice' ? 'notice' : 'message' },
  })
}

function openMessageCenter(tab?: 'todo' | 'message' | 'notice') {
  router.push({
    path: '/parent/messages',
    query: { tab: tab === 'notice' ? 'notice' : tab === 'todo' ? 'todo' : 'message' },
  })
}

onMounted(loadData)
</script>

<style scoped>
</style>
