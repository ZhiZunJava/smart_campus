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
  >
    <template #greeting-extra>
      <div class="parent-greeting-panel">
        <div class="parent-greeting-panel__switcher">
          <span class="parent-greeting-panel__label">孩子视角</span>
          <el-select
            v-model="selectedChildId"
            filterable
            placeholder="选择孩子"
            class="parent-greeting-panel__select"
            @change="handleChildChange"
          >
            <el-option v-for="item in childOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <div class="parent-greeting-panel__summary">
          <div class="parent-greeting-panel__metric">
            <span>当前孩子</span>
            <strong>{{ currentChild?.studentName || '未绑定孩子' }}</strong>
          </div>
          <div class="parent-greeting-panel__metric">
            <span>班级 / 专业</span>
            <strong>{{ currentChild?.className || '待完善' }}</strong>
            <p>{{ currentChild?.major || '未配置专业' }}</p>
          </div>
          <div class="parent-greeting-panel__metric">
            <span>家庭关系</span>
            <strong>{{ currentChild?.relationType || '未标注' }}</strong>
            <p>已绑定 {{ childOptions.length }} 位孩子</p>
          </div>
        </div>
      </div>
    </template>
  </PortalDashboardShell>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import PortalDashboardShell from '@/components/dashboard/PortalDashboardShell.vue'
import {
  getParentDashboard,
  getPortalParentSchedule,
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
const secondaryInfo = computed(() => [
  `孩子账号：${currentChild.value?.studentNo || '--'}`,
  `考试记录：${dashboard.value.examRecordCount || 0} 条`,
  `学习记录：${dashboard.value.studyRecordCount || 0} 条`,
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
  const prefix = activeScheduleMode.value === 'today' ? '今日课程' : '明日课程'
  return `${prefix} · ${formatMonthDay(meta.date)}`
})
const activeScheduleSummary = computed(() => {
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
  const childName = currentChild.value?.studentName || '孩子'
  return [
    {
      key: 'parent-course-list',
      title: '查看孩子课程',
      desc: `进入 ${childName} 的课程列表，继续查看课程和教师信息`,
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
    {
      key: 'parent-child-switch',
      title: '切换孩子视角',
      desc: childOptions.value.length > 1 ? '当前家庭绑定了多位孩子，可随时切换查看' : '当前仅绑定 1 位孩子，可持续关注学习安排',
      tag: '家庭关系',
      tagType: 'warning',
      meta: [`已绑定：${childOptions.value.length} 位`],
      actionLabel: '继续查看',
      path: '/parent/dashboard',
      iconType: 'homework' as const,
    },
  ]
})

const recentTasks = computed(() => buildStaticTaskCards(baseTaskItems.value))
const taskOverviewBadges = computed(() => [
  `已绑定孩子 ${childOptions.value.length}`,
  `考试记录 ${dashboard.value.examRecordCount || 0}`,
  `当前查看 ${currentChild.value?.studentName || '未选择孩子'}`,
].filter(Boolean))
const drawerTodoList = computed(() => recentTasks.value.map((item) => ({
  key: item.key,
  title: item.title,
  desc: item.statusText,
  action: item.raw?.action,
})))
const drawerMessageList = computed(() => [
  {
    messageId: 'parent-child-focus',
    messageTitle: `当前查看：${currentChild.value?.studentName || '未选择孩子'}`,
    messageSummary: `${currentChild.value?.className || '待完善班级'} · ${currentChild.value?.major || '未完善专业'}`,
    createTime: formatDateTime(new Date()),
    readFlag: '0',
  },
  {
    messageId: 'parent-week-focus',
    messageTitle: `第 ${currentTeachingWeek.value} 教学周提醒`,
    messageSummary: `本周共 ${Array.isArray(schedulePayload.value?.activities) ? schedulePayload.value.activities.length : 0} 条排课安排`,
    createTime: formatDateTime(new Date()),
    readFlag: '0',
  },
])
const drawerNoticeList = computed(() => [
  {
    messageId: 'parent-exam-summary',
    messageTitle: '考试记录概览',
    messageSummary: `孩子当前共有 ${dashboard.value.examRecordCount || 0} 条考试记录`,
    createTime: formatDateTime(new Date()),
    readFlag: '0',
  },
  {
    messageId: 'parent-course-summary',
    messageTitle: '课程同步完成',
    messageSummary: `当前学期已同步 ${activeScheduleCards.value.length} 条当日预览课程`,
    createTime: formatDateTime(new Date()),
    readFlag: '0',
  },
])

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

async function loadData() {
  if (!userStore.user?.userId) return
  loading.value = true
  try {
    await loadTerms()
    await loadChildren()
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

function openQuickDrawer() {
  drawerLoading.value = false
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
.parent-greeting-panel {
  margin-top: 2.2rem;
  padding: 1.8rem 2rem;
  border-radius: 1.8rem;
  background: linear-gradient(135deg, rgba(255, 244, 240, 0.92), rgba(255, 255, 255, 0.96));
  border: 1px solid rgba(194, 65, 45, 0.16);
  box-shadow: 0 10px 24px rgba(194, 65, 45, 0.08);
}

.parent-greeting-panel__switcher {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  flex-wrap: wrap;
}

.parent-greeting-panel__label {
  font-size: 1.3rem;
  color: #7c2d12;
  font-weight: 700;
}

.parent-greeting-panel__select {
  width: 30rem;
}

.parent-greeting-panel__summary {
  margin-top: 1.6rem;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1.2rem;
}

.parent-greeting-panel__metric {
  padding: 1.4rem 1.6rem;
  border-radius: 1.4rem;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(194, 65, 45, 0.12);
}

.parent-greeting-panel__metric span {
  display: block;
  font-size: 1.2rem;
  color: #9a3412;
}

.parent-greeting-panel__metric strong {
  display: block;
  margin-top: 0.8rem;
  font-size: 1.7rem;
  color: #7c2d12;
}

.parent-greeting-panel__metric p {
  margin: 0.6rem 0 0;
  font-size: 1.2rem;
  color: #7c6f6a;
  line-height: 1.6;
}

@media (max-width: 900px) {
  .parent-greeting-panel__summary {
    grid-template-columns: 1fr;
  }

  .parent-greeting-panel__select {
    width: 100%;
  }
}
</style>
