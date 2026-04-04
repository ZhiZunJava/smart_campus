<template>
  <div class="min-h-full bg-[var(--el-bg-color-page)] p-4 lg:p-6">
    <!-- Header -->
    <section class="mb-6 flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">智慧校园管理总览</h1>
        <p class="mt-1 text-sm text-slate-500">
          {{ currentTermName }} &middot; {{ todayLabel }}
        </p>
      </div>
      <span
        class="inline-flex items-center gap-1.5 self-start rounded-md border px-3 py-1.5 text-xs font-semibold"
        :class="riskClass"
      >
        <i :class="riskIcon" class="text-sm"></i>
        {{ riskLabel }}
      </span>
    </section>

    <!-- KPI Cards -->
    <section class="mb-6 grid grid-cols-2 gap-4 sm:grid-cols-3 xl:grid-cols-6">
      <div
        v-for="card in kpiCards"
        :key="card.title"
        class="group relative overflow-hidden rounded-lg border border-slate-200 bg-white p-4 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
      >
        <div class="flex items-center justify-between">
          <span class="text-xs font-semibold uppercase tracking-wider text-slate-400">{{ card.title }}</span>
          <div class="flex h-8 w-8 items-center justify-center rounded-md bg-slate-50 text-slate-500">
            <i :class="card.icon" class="text-base"></i>
          </div>
        </div>
        <div class="mt-3 text-3xl font-black tracking-tight text-slate-900">{{ card.value }}</div>
        <p class="mt-1 text-xs text-slate-500">{{ card.desc }}</p>
      </div>
    </section>

    <!-- Charts Row 1 -->
    <section class="mb-6 grid gap-6 xl:grid-cols-2">
      <!-- User Distribution Pie -->
      <div class="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
        <div class="mb-4 flex items-center justify-between">
          <div>
            <h2 class="text-base font-bold text-slate-900">用户分布</h2>
            <p class="mt-0.5 text-xs text-slate-500">按角色统计平台用户构成</p>
          </div>
          <span class="text-xs font-semibold text-slate-400">共 {{ data.userTotal || 0 }} 人</span>
        </div>
        <div ref="userChartRef" class="h-[320px] w-full"></div>
      </div>

      <!-- Scheduling Bar Chart -->
      <div class="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
        <div class="mb-4 flex items-center justify-between">
          <div>
            <h2 class="text-base font-bold text-slate-900">排课概况</h2>
            <p class="mt-0.5 text-xs text-slate-500">教学班排课完成情况对比</p>
          </div>
          <span class="text-xs font-semibold text-slate-400">覆盖率 {{ scheduleCoverage }}%</span>
        </div>
        <div ref="scheduleChartRef" class="h-[320px] w-full"></div>
      </div>
    </section>

    <!-- Charts Row 2 -->
    <section class="mb-6 grid gap-6 xl:grid-cols-2">
      <!-- Exam Trend Line -->
      <div class="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
        <div class="mb-4">
          <h2 class="text-base font-bold text-slate-900">考试记录趋势</h2>
          <p class="mt-0.5 text-xs text-slate-500">近 7 天新增考试记录数量</p>
        </div>
        <div ref="examChartRef" class="h-[280px] w-full"></div>
      </div>

      <!-- Login Trend Line -->
      <div class="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
        <div class="mb-4">
          <h2 class="text-base font-bold text-slate-900">登录活跃度</h2>
          <p class="mt-0.5 text-xs text-slate-500">近 7 天系统登录次数</p>
        </div>
        <div ref="loginChartRef" class="h-[280px] w-full"></div>
      </div>
    </section>

    <!-- Bottom Row -->
    <section class="grid gap-6 xl:grid-cols-[1fr_360px]">
      <!-- Teaching Readiness -->
      <div class="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
        <div class="mb-4 flex items-center justify-between">
          <div>
            <h2 class="text-base font-bold text-slate-900">教学资源就绪度</h2>
            <p class="mt-0.5 text-xs text-slate-500">本学期教学组织准备情况一览</p>
          </div>
          <i class="ri-bar-chart-grouped-line text-lg text-slate-400"></i>
        </div>

        <!-- Progress Bars -->
        <div class="space-y-4">
          <div v-for="bar in progressBars" :key="bar.label">
            <div class="mb-1.5 flex items-center justify-between">
              <span class="text-sm font-medium text-slate-700">{{ bar.label }}</span>
              <span class="text-sm font-bold text-slate-900">{{ bar.text }}</span>
            </div>
            <div class="h-2.5 rounded-full bg-slate-100">
              <div
                class="h-2.5 rounded-full transition-all duration-500"
                :class="bar.color"
                :style="{ width: `${bar.percent}%` }"
              ></div>
            </div>
          </div>
        </div>

        <!-- Readiness Radar Chart -->
        <div ref="radarChartRef" class="mt-4 h-[280px] w-full"></div>

        <!-- Teaching Metrics Grid -->
        <div class="mt-4 grid grid-cols-2 gap-3 border-t border-slate-100 pt-5 sm:grid-cols-3 lg:grid-cols-6">
          <div v-for="m in teachingMetrics" :key="m.label" class="rounded-md border border-slate-100 bg-slate-50 p-3">
            <div class="text-[11px] font-semibold uppercase tracking-wider text-slate-400">{{ m.label }}</div>
            <div class="mt-1 text-xl font-black text-slate-900">{{ m.value }}</div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="space-y-4">
        <div class="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <h2 class="mb-3 text-base font-bold text-slate-900">快捷入口</h2>
          <div class="grid gap-2">
            <button
              v-for="link in quickLinks"
              :key="link.title"
              type="button"
              class="flex w-full cursor-pointer items-center gap-3 rounded-md border border-slate-200 bg-slate-50/80 px-3 py-2.5 text-left transition hover:-translate-y-0.5 hover:border-cyan-200 hover:bg-cyan-50"
              @click="navigateTo(link.path)"
            >
              <div class="flex h-9 w-9 shrink-0 items-center justify-center rounded-md bg-white text-cyan-600 shadow-sm">
                <i :class="link.icon" class="text-lg"></i>
              </div>
              <div class="min-w-0">
                <div class="text-sm font-semibold text-slate-900">{{ link.title }}</div>
                <p class="truncate text-xs text-slate-500">{{ link.desc }}</p>
              </div>
            </button>
          </div>
        </div>

        <div class="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <h2 class="mb-3 text-base font-bold text-slate-900">待处理事项</h2>
          <div class="space-y-2">
            <div
              v-for="item in actionItems"
              :key="item.label"
              class="flex cursor-pointer items-center justify-between rounded-md border px-3 py-2.5 transition hover:-translate-y-0.5"
              :class="item.cls"
              @click="navigateTo(item.path)"
            >
              <div class="flex items-center gap-2">
                <i :class="item.icon" class="text-base"></i>
                <span class="text-sm font-medium">{{ item.label }}</span>
              </div>
              <span class="text-lg font-black">{{ item.value }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getAdminDashboard } from '@/api/campus/overview'

const router = useRouter()

// ===== Data =====
const data = ref<any>({})
const loading = ref(true)

// ===== Chart Refs =====
const userChartRef = ref<HTMLElement>()
const scheduleChartRef = ref<HTMLElement>()
const examChartRef = ref<HTMLElement>()
const loginChartRef = ref<HTMLElement>()
const radarChartRef = ref<HTMLElement>()

let userChart: echarts.ECharts | null = null
let scheduleChart: echarts.ECharts | null = null
let examChart: echarts.ECharts | null = null
let loginChart: echarts.ECharts | null = null
let radarChart: echarts.ECharts | null = null

// ===== Computed =====
const todayLabel = computed(() => {
  const d = new Date()
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${weekDays[d.getDay()]}`
})

const currentTermName = computed(() => data.value.currentTermName || '未设置学期')

const scheduleCoverage = computed(() => {
  const total = data.value.classCourseCount || 0
  const arranged = data.value.arrangedClassCourseCount || 0
  if (!total) return 0
  return Math.round((arranged / total) * 100)
})

const riskClass = computed(() => {
  const pending = data.value.pendingArrangeCount || 0
  if (pending <= 0) return 'border-emerald-200 bg-emerald-50 text-emerald-700'
  if (pending <= 5) return 'border-amber-200 bg-amber-50 text-amber-700'
  return 'border-rose-200 bg-rose-50 text-rose-700'
})

const riskIcon = computed(() => {
  const pending = data.value.pendingArrangeCount || 0
  if (pending <= 0) return 'ri-shield-check-line'
  if (pending <= 5) return 'ri-alert-line'
  return 'ri-alarm-warning-line'
})

const riskLabel = computed(() => {
  const pending = data.value.pendingArrangeCount || 0
  if (pending <= 0) return '运行平稳'
  if (pending <= 5) return `${pending} 项待处理`
  return `${pending} 项需优先处理`
})

const kpiCards = computed(() => [
  { title: '用户总量', value: data.value.userTotal || 0, desc: '平台全部账号', icon: 'ri-team-line' },
  { title: '班级', value: data.value.classCount || 0, desc: '参与教学组织', icon: 'ri-community-line' },
  { title: '课程', value: data.value.courseCount || 0, desc: '系统维护课程', icon: 'ri-book-2-line' },
  { title: '排课记录', value: data.value.scheduleCount || 0, desc: '正式排课条数', icon: 'ri-calendar-schedule-line' },
  { title: '考试记录', value: data.value.examRecordCount || 0, desc: '测评数据沉淀', icon: 'ri-file-chart-line' },
  { title: '教学资源', value: data.value.resourceCount || 0, desc: '已发布资源', icon: 'ri-folder-open-line' },
])

const progressBars = computed(() => {
  const total = data.value.classCourseCount || 0
  const arranged = data.value.arrangedClassCourseCount || 0
  const termTotal = data.value.termCount || 0
  const termEnabled = data.value.enabledTermCount || 0
  return [
    {
      label: '排课覆盖率',
      text: `${scheduleCoverage.value}% (${arranged}/${total})`,
      percent: scheduleCoverage.value,
      color: scheduleCoverage.value >= 80 ? 'bg-gradient-to-r from-emerald-500 to-cyan-500' : 'bg-gradient-to-r from-amber-400 to-orange-500',
    },
    {
      label: '启用学期占比',
      text: `${termTotal ? Math.round((termEnabled / termTotal) * 100) : 0}% (${termEnabled}/${termTotal})`,
      percent: termTotal ? Math.round((termEnabled / termTotal) * 100) : 0,
      color: 'bg-gradient-to-r from-cyan-500 to-sky-600',
    },
  ]
})

const teachingMetrics = computed(() => [
  { label: '年级', value: data.value.gradeCount || 0 },
  { label: '班级', value: data.value.classCount || 0 },
  { label: '课程', value: data.value.courseCount || 0 },
  { label: '教室', value: data.value.classroomCount || 0 },
  { label: '题库', value: data.value.questionBankCount || 0 },
  { label: '学习任务', value: data.value.learningTaskCount || 0 },
])

const quickLinks = [
  { title: '用户管理', desc: '学生、教师、家长和管理员账号', path: '/system/user', icon: 'ri-user-settings-line' },
  { title: '班级课程', desc: '教学班、任课教师和课时', path: '/campus/classCourse', icon: 'ri-book-open-line' },
  { title: '排课管理', desc: '手工排课、自动排课和调整', path: '/campus/courseSchedule', icon: 'ri-calendar-2-line' },
  { title: '考试记录', desc: '测评数据与成绩分析', path: '/campus/exam/record', icon: 'ri-file-chart-line' },
  { title: '教室资源', desc: '教室容量、校区和类型', path: '/campus/classroom', icon: 'ri-building-4-line' },
]

const actionItems = computed(() => {
  const pending = data.value.pendingArrangeCount || 0
  const exams = data.value.examRecordCount || 0
  return [
    {
      label: '待排课教学班',
      value: pending,
      path: '/campus/courseSchedule',
      icon: 'ri-calendar-todo-line',
      cls: pending > 0
        ? 'border-amber-200 bg-amber-50 text-amber-900 hover:shadow-md'
        : 'border-emerald-200 bg-emerald-50 text-emerald-900 hover:shadow-md',
    },
    {
      label: '考试记录',
      value: exams,
      path: '/campus/exam/record',
      icon: 'ri-file-chart-line',
      cls: 'border-cyan-200 bg-cyan-50 text-cyan-900 hover:shadow-md',
    },
  ]
})

// ===== Navigation =====
function navigateTo(path: string) {
  router.push(path)
}

// ===== Chart Initialization =====
function initUserChart() {
  if (!userChartRef.value) return
  userChart = echarts.init(userChartRef.value, 'macarons')
  const d = data.value
  userChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
    },
    legend: {
      orient: 'vertical',
      right: 16,
      top: 'center',
      textStyle: { fontSize: 13 },
    },
    series: [
      {
        name: '用户分布',
        type: 'pie',
        radius: ['42%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' },
        },
        data: [
          { value: d.studentCount || 0, name: '学生', itemStyle: { color: '#06b6d4' } },
          { value: d.teacherCount || 0, name: '教师', itemStyle: { color: '#8b5cf6' } },
          { value: d.parentCount || 0, name: '家长', itemStyle: { color: '#f59e0b' } },
          { value: d.adminCount || 0, name: '管理员', itemStyle: { color: '#10b981' } },
        ],
      },
    ],
    animationDuration: 800,
    animationEasing: 'cubicInOut',
  })
}

function initScheduleChart() {
  if (!scheduleChartRef.value) return
  scheduleChart = echarts.init(scheduleChartRef.value, 'macarons')
  const d = data.value
  scheduleChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
    },
    legend: {
      data: ['已排课', '待排课'],
      top: 0,
    },
    grid: {
      left: 50,
      right: 30,
      bottom: 30,
      top: 40,
    },
    xAxis: {
      type: 'category',
      data: ['教学班', '排课记录', '教室', '课程'],
      axisLabel: { fontSize: 12 },
    },
    yAxis: {
      type: 'value',
      axisLabel: { fontSize: 11 },
    },
    series: [
      {
        name: '已排课',
        type: 'bar',
        barWidth: 28,
        itemStyle: { color: '#06b6d4', borderRadius: [4, 4, 0, 0] },
        data: [
          d.arrangedClassCourseCount || 0,
          d.scheduleCount || 0,
          d.classroomCount || 0,
          d.courseCount || 0,
        ],
      },
      {
        name: '待排课',
        type: 'bar',
        barWidth: 28,
        itemStyle: { color: '#f59e0b', borderRadius: [4, 4, 0, 0] },
        data: [
          d.pendingArrangeCount || 0,
          0,
          0,
          0,
        ],
      },
    ],
    animationDuration: 800,
    animationEasing: 'cubicInOut',
  })
}

function initExamChart() {
  if (!examChartRef.value) return
  examChart = echarts.init(examChartRef.value, 'macarons')
  const trend = data.value.examTrend || []
  const dates = trend.map((t: any) => t.date?.substring(5) || '')
  const counts = trend.map((t: any) => t.count || 0)
  examChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const p = params[0]
        return `${p.axisValue}<br/>${p.marker} 考试记录: <b>${p.value}</b>`
      },
    },
    grid: {
      left: 45,
      right: 20,
      bottom: 30,
      top: 20,
    },
    xAxis: {
      type: 'category',
      data: dates,
      boundaryGap: false,
      axisLabel: { fontSize: 11 },
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: { fontSize: 11 },
    },
    series: [
      {
        name: '考试记录',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { width: 3, color: '#8b5cf6' },
        itemStyle: { color: '#8b5cf6' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(139,92,246,0.25)' },
            { offset: 1, color: 'rgba(139,92,246,0.02)' },
          ]),
        },
        data: counts,
      },
    ],
    animationDuration: 800,
    animationEasing: 'cubicInOut',
  })
}

function initLoginChart() {
  if (!loginChartRef.value) return
  loginChart = echarts.init(loginChartRef.value, 'macarons')
  const trend = data.value.loginTrend || []
  const dates = trend.map((t: any) => t.date?.substring(5) || '')
  const counts = trend.map((t: any) => t.count || 0)
  loginChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const p = params[0]
        return `${p.axisValue}<br/>${p.marker} 登录次数: <b>${p.value}</b>`
      },
    },
    grid: {
      left: 45,
      right: 20,
      bottom: 30,
      top: 20,
    },
    xAxis: {
      type: 'category',
      data: dates,
      boundaryGap: false,
      axisLabel: { fontSize: 11 },
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: { fontSize: 11 },
    },
    series: [
      {
        name: '登录次数',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { width: 3, color: '#06b6d4' },
        itemStyle: { color: '#06b6d4' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(6,182,212,0.25)' },
            { offset: 1, color: 'rgba(6,182,212,0.02)' },
          ]),
        },
        data: counts,
      },
    ],
    animationDuration: 800,
    animationEasing: 'cubicInOut',
  })
}

function initRadarChart() {
  if (!radarChartRef.value) return
  radarChart = echarts.init(radarChartRef.value, 'macarons')
  const d = data.value
  const maxUser = Math.max(d.userTotal || 1, 1)
  const maxCourse = Math.max(d.courseCount || 1, d.classCount || 1, d.gradeCount || 1, 1)
  const maxSchedule = Math.max(d.classCourseCount || 1, d.scheduleCount || 1, 1)
  const maxResource = Math.max(d.resourceCount || 1, d.questionBankCount || 1, d.learningTaskCount || 1, 1)

  radarChart.setOption({
    tooltip: {},
    radar: {
      indicator: [
        { name: '用户规模', max: maxUser },
        { name: '课程体系', max: maxCourse },
        { name: '班级覆盖', max: maxCourse },
        { name: '排课完成', max: maxSchedule },
        { name: '教学资源', max: maxResource },
        { name: '题库建设', max: maxResource },
      ],
      radius: '65%',
      axisName: { fontSize: 12, color: '#64748b' },
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: [
              d.userTotal || 0,
              d.courseCount || 0,
              d.classCount || 0,
              d.arrangedClassCourseCount || 0,
              d.resourceCount || 0,
              d.questionBankCount || 0,
            ],
            name: '当前就绪度',
            areaStyle: { color: 'rgba(6,182,212,0.15)' },
            lineStyle: { color: '#06b6d4', width: 2 },
            itemStyle: { color: '#06b6d4' },
          },
        ],
      },
    ],
    animationDuration: 800,
    animationEasing: 'cubicInOut',
  })
}

function handleResize() {
  userChart?.resize()
  scheduleChart?.resize()
  examChart?.resize()
  loginChart?.resize()
  radarChart?.resize()
}

// ===== Lifecycle =====
onMounted(async () => {
  try {
    const res = await getAdminDashboard()
    data.value = res.data || {}
  } catch (e) {
    console.error('Failed to load admin dashboard:', e)
  } finally {
    loading.value = false
  }

  await nextTick()
  initUserChart()
  initScheduleChart()
  initExamChart()
  initLoginChart()
  initRadarChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  userChart?.dispose()
  scheduleChart?.dispose()
  examChart?.dispose()
  loginChart?.dispose()
  radarChart?.dispose()
})
</script>
