<template>
  <div class="portal-page">
    <section
      class="relative min-h-[calc(100vh-92px)] overflow-hidden rounded-[28px] border border-white/25 bg-slate-900 shadow-[0_24px_60px_rgba(15,23,42,0.22)]"
      :style="heroBackgroundStyle"
    >
      <div class="absolute inset-0 bg-[linear-gradient(90deg,rgba(3,36,35,0.76)_0%,rgba(6,78,75,0.5)_34%,rgba(10,16,32,0.12)_100%)]"></div>
      <div class="absolute inset-0 bg-[radial-gradient(circle_at_18%_22%,rgba(45,212,191,0.22),transparent_28%),radial-gradient(circle_at_84%_18%,rgba(255,255,255,0.14),transparent_16%),radial-gradient(circle_at_72%_72%,rgba(15,23,42,0.34),transparent_34%)]"></div>

      <div class="relative z-10 flex min-h-[calc(100vh-92px)] flex-col px-5 py-6 sm:px-8 lg:px-12 lg:py-10">
        <div class="grid flex-1 gap-8 lg:grid-cols-[minmax(0,1.2fr)_minmax(360px,520px)] lg:items-center xl:gap-12">
          <div class="max-w-4xl self-center text-white">
            <div class="inline-flex items-center gap-2 rounded-full border border-white/25 bg-white/10 px-4 py-1.5 text-[11px] font-semibold uppercase tracking-[0.28em] text-white/80 backdrop-blur-md">
              <span class="h-2 w-2 rounded-full bg-emerald-300"></span>
              Smart Campus Portal
            </div>

            <div class="mt-6 max-w-3xl">
              <p class="text-sm font-medium tracking-[0.22em] text-cyan-100/80">{{ greetingLabel }}</p>
              <h1 class="mt-4 text-4xl font-light leading-tight text-white sm:text-5xl lg:text-6xl">
                {{ userName }}
                <span class="ml-0 block font-semibold text-white/95 sm:ml-3 sm:inline">{{ homeTitle }}</span>
              </h1>
              <div class="mt-6 h-px w-full max-w-2xl bg-white/25"></div>
              <p class="mt-6 text-lg leading-9 text-white/92 sm:text-2xl">
                {{ academicSummary }}
              </p>
              <div class="mt-4 flex flex-wrap items-center gap-x-8 gap-y-3 text-sm text-white/82 sm:text-base">
                <span>学习行为：{{ studyRecordText }}</span>
                <span>能力等级：{{ dashboard.profile?.abilityLevel || '--' }}</span>
                <span>掌握度：{{ dashboard.profile?.masteryScore || 0 }}</span>
              </div>
            </div>

            <div class="mt-8 flex flex-wrap items-center gap-4">
              <button
                type="button"
                class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-white/55 bg-white/10 px-5 py-2.5 text-sm font-medium text-white backdrop-blur-md transition-all duration-200 hover:bg-white/18 focus:outline-none focus:ring-2 focus:ring-white/70"
                @click="go('/student/recommendations')"
              >
                最近使用
                <i class="ri-arrow-right-s-line text-lg"></i>
              </button>
              <button
                type="button"
                class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-emerald-200/40 bg-emerald-300/15 px-5 py-2.5 text-sm font-medium text-emerald-50 backdrop-blur-md transition-all duration-200 hover:bg-emerald-300/25 focus:outline-none focus:ring-2 focus:ring-emerald-200/70"
                @click="scrollToSection('insights')"
              >
                学情洞察
                <i class="ri-bar-chart-box-line text-lg"></i>
              </button>
            </div>
          </div>

          <div class="flex justify-center lg:justify-end">
            <div class="w-full max-w-[520px]">
              <div class="mb-3 flex items-center justify-between px-1 text-white/90">
                <span class="text-base font-medium">快捷入口</span>
                <button
                  type="button"
                  class="inline-flex h-9 w-9 cursor-pointer items-center justify-center rounded-full border border-white/25 bg-white/10 text-white/85 backdrop-blur-md transition-all duration-200 hover:bg-white/18 focus:outline-none focus:ring-2 focus:ring-white/70"
                  @click="go('/student/dashboard')"
                  aria-label="刷新首页视图"
                >
                  <i class="ri-settings-3-line text-base"></i>
                </button>
              </div>

              <div class="grid grid-cols-2 gap-4 sm:grid-cols-3">
                <button
                  v-for="item in shortcutItems"
                  :key="item.path"
                  type="button"
                  class="group flex aspect-square cursor-pointer flex-col items-center justify-center gap-4 overflow-hidden rounded-[22px] border border-white/18 bg-white/16 px-4 py-5 text-white shadow-[0_10px_28px_rgba(15,23,42,0.16)] backdrop-blur-xl transition-all duration-200 hover:-translate-y-0.5 hover:bg-white/22 hover:shadow-[0_18px_36px_rgba(15,23,42,0.22)] focus:outline-none focus:ring-2 focus:ring-white/70"
                  @click="go(item.path)"
                >
                  <div
                    class="relative flex h-16 w-16 items-center justify-center rounded-full text-white shadow-[inset_0_1px_0_rgba(255,255,255,0.35)]"
                    :class="item.iconBgClass"
                  >
                    <i :class="[item.icon, 'text-[30px]']"></i>
                  </div>
                  <div class="space-y-1 text-center">
                    <div class="text-lg font-medium leading-none text-white">{{ item.title }}</div>
                    <p class="line-clamp-2 min-h-10 text-xs leading-5 text-white/72">{{ item.desc }}</p>
                  </div>
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="pointer-events-none absolute right-4 top-1/2 hidden -translate-y-1/2 xl:block">
          <div class="pointer-events-auto flex flex-col gap-5 rounded-[28px] border border-white/18 bg-black/18 px-4 py-5 backdrop-blur-xl">
            <button
              v-for="action in sideActions"
              :key="action.key"
              type="button"
              class="group flex w-20 cursor-pointer flex-col items-center gap-3 rounded-[20px] px-3 py-3 text-white transition-colors duration-200 hover:bg-white/10 focus:outline-none focus:ring-2 focus:ring-white/70"
              @click="handleSideAction(action)"
            >
              <span class="flex h-14 w-14 items-center justify-center rounded-full bg-white/92 text-slate-700 shadow-[0_10px_24px_rgba(15,23,42,0.18)]">
                <i :class="[action.icon, 'text-2xl']"></i>
              </span>
              <span class="text-center text-sm leading-5 text-white/88">{{ action.label }}</span>
            </button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getLearningDiagnosis, getLearningWorkbench, getStudentDashboard } from '@/api/portal'
import heroImage from '@/assets/img/login-background.jpg'
import usePortalUserStore from '@/store/user'

type ShortcutItem = {
  title: string
  desc: string
  path: string
  icon: string
  iconBgClass: string
}

type SideAction = {
  key: string
  label: string
  icon: string
  type: 'route' | 'scroll'
  target: string
}

const router = useRouter()
const userStore = usePortalUserStore()

const dashboard = ref<any>({})
const diagnosis = ref<any>(null)
const workbench = ref<any>({ activeRecommendations: [], recentStudyRecords: [], recentExamRecords: [], recentWarnings: [] })

const insightsSection = ref<HTMLElement | null>(null)
const examSection = ref<HTMLElement | null>(null)

const shortcutItems: ShortcutItem[] = [
  {
    title: '学习总览',
    desc: '查看首页诊断与学习态势',
    path: '/student/dashboard',
    icon: 'ri-home-5-line',
    iconBgClass: 'bg-[linear-gradient(180deg,#e7bf9a_0%,#cb8f68_100%)]',
  },
  {
    title: '我的课表',
    desc: '按周查看课程安排',
    path: '/student/schedule',
    icon: 'ri-calendar-schedule-line',
    iconBgClass: 'bg-[linear-gradient(180deg,#9fe7da_0%,#4fcab2_100%)]',
  },
  {
    title: '我的课程',
    desc: '进入学期课程与班级课程',
    path: '/student/courses',
    icon: 'ri-book-open-line',
    iconBgClass: 'bg-[linear-gradient(180deg,#d3ceff_0%,#9f94f5_100%)]',
  },
  {
    title: '资源中心',
    desc: '检索学习资源并跟踪行为',
    path: '/student/resources',
    icon: 'ri-folder-chart-line',
    iconBgClass: 'bg-[linear-gradient(180deg,#aedbfd_0%,#58b4fb_100%)]',
  },
  {
    title: '任务广场',
    desc: '查看挑战任务与通用题目',
    path: '/student/plaza',
    icon: 'ri-rocket-2-line',
    iconBgClass: 'bg-[linear-gradient(180deg,#f7b4bb_0%,#ff7575_100%)]',
  },
  {
    title: '智能问答',
    desc: '围绕课程问题快速求助',
    path: '/student/qa',
    icon: 'ri-message-3-line',
    iconBgClass: 'bg-[linear-gradient(180deg,#f7c99d_0%,#ffa540_100%)]',
  },
  {
    title: '个性推荐',
    desc: '查看推荐资源与个性化建议',
    path: '/student/recommendations',
    icon: 'ri-lightbulb-flash-line',
    iconBgClass: 'bg-[linear-gradient(180deg,#9fe7da_0%,#4fcab2_100%)]',
  },
  {
    title: '我的考试',
    desc: '进入考试记录与练习入口',
    path: '/student/exams',
    icon: 'ri-file-list-3-line',
    iconBgClass: 'bg-[linear-gradient(180deg,#e7bf9a_0%,#cb8f68_100%)]',
  },
  {
    title: '我的错题本',
    desc: '复盘错题并持续巩固',
    path: '/student/wrongbook',
    icon: 'ri-booklet-line',
    iconBgClass: 'bg-[linear-gradient(180deg,#f7b4bb_0%,#ff7575_100%)]',
  },
]

const sideActions: SideAction[] = [
  { key: 'todo', label: '我的推荐', icon: 'ri-task-line', type: 'route', target: '/student/recommendations' },
  { key: 'exam', label: '最近考试', icon: 'ri-message-2-line', type: 'scroll', target: 'exams' },
  { key: 'notice', label: '学情诊断', icon: 'ri-notification-3-line', type: 'scroll', target: 'insights' },
]

const userName = computed(() => userStore.user?.realName || userStore.user?.username || '同学')
const homeTitle = computed(() => '欢迎回来')
const studyRecordText = computed(() => {
  const value = dashboard.value.studyRecordCount
  return typeof value === 'number' ? `${value} 次` : '--'
})

const greetingLabel = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 12) return '早上好'
  if (hour < 14) return '中午好'
  if (hour < 19) return '下午好'
  return '晚上好'
})

const academicSummary = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = `${now.getMonth() + 1}`.padStart(2, '0')
  const day = `${now.getDate()}`.padStart(2, '0')
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return `${year}年${month}月${day}日 ${weekDays[now.getDay()]}，今天也适合把课程、资源与考试节奏安排好`
})

const summaryMetrics = computed(() => [
  {
    key: 'study',
    label: '学习行为',
    value: dashboard.value.studyRecordCount || 0,
    desc: '累计行为采集次数',
  },
  {
    key: 'exam',
    label: '考试记录',
    value: dashboard.value.examRecordCount || 0,
    desc: '阶段考试与练习记录',
  },
  {
    key: 'warning',
    label: '预警数量',
    value: dashboard.value.warningCount || 0,
    desc: '需要优先关注的学情风险',
  },
  {
    key: 'resource',
    label: '推荐资源',
    value: (workbench.value.activeRecommendations || []).length,
    desc: '当前推荐学习任务',
  },
])

const heroBackgroundStyle = computed(() => ({
  backgroundImage: `linear-gradient(rgba(8, 46, 53, 0.18), rgba(8, 46, 53, 0.18)), url(${heroImage})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
}))

function go(path: string) {
  router.push(path)
}

function scrollToSection(section: 'insights' | 'exams') {
  const target = section === 'insights' ? insightsSection.value : examSection.value
  target?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function handleSideAction(action: SideAction) {
  if (action.type === 'route') {
    go(action.target)
    return
  }
  if (action.target === 'exams' || action.target === 'insights') {
    scrollToSection(action.target)
  }
}

async function loadData() {
  const userId = userStore.user?.userId
  if (!userId) return

  const [dashboardRes, diagnosisRes, workbenchRes] = await Promise.all([
    getStudentDashboard({ userId, recommendLimit: 5 }),
    getLearningDiagnosis({ userId, recommendLimit: 5, autoGenerate: true }),
    getLearningWorkbench({ userId, limit: 5 }),
  ])

  dashboard.value = dashboardRes.data || {}
  diagnosis.value = diagnosisRes.data || null
  workbench.value = workbenchRes.data || {}
}

onMounted(loadData)
</script>
