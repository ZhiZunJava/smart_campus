<template>
  <div class="min-h-full bg-[var(--el-bg-color-page)] text-[var(--el-text-color-primary)]">
    <div class="m-3 mt-4 flex w-auto flex-col gap-3 md:m-4 md:mt-4 xl:m-4 xl:mt-4">
      <section
        class="relative overflow-hidden rounded-md border border-slate-200/70 bg-gradient-to-br from-cyan-50 via-white to-sky-100 shadow-[0_14px_34px_rgba(8,145,178,0.10)]"
      >
        <div class="absolute inset-y-0 right-0 hidden w-1/2 bg-[radial-gradient(circle_at_top_right,rgba(34,211,238,0.18),transparent_58%)] lg:block"></div>
        <div class="absolute -right-16 top-8 h-40 w-40 bg-cyan-200/35 blur-3xl"></div>
        <div class="absolute bottom-0 left-1/3 h-24 w-24 bg-emerald-200/40 blur-2xl"></div>

        <div class="relative px-3 py-3 lg:px-4 lg:py-4">
          <div class="space-y-4">
            <div class="inline-flex items-center gap-2 rounded-sm border border-cyan-200 bg-white/70 px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.22em] text-cyan-800">
              <i class="ri-dashboard-line text-sm"></i>
              Campus Operations Hub
            </div>

            <div class="max-w-4xl space-y-2">
              <div class="flex flex-wrap items-center gap-2">
                <h1 class="text-3xl font-black tracking-tight text-slate-900 md:text-4xl">
                  智慧校园运营首页
                </h1>
                <span
                  class="inline-flex items-center rounded-sm border border-emerald-200 bg-emerald-50 px-3 py-1 text-xs font-semibold text-emerald-700"
                >
                  {{ currentTermLabel }}
                </span>
              </div>
              <p class="max-w-3xl text-sm leading-7 text-slate-600 md:text-[15px]">
                这里汇总了本学期最值得优先关注的校园运营信息。你可以快速查看排课进度、学情风险、用户规模和教学资源状态，并从首页直接进入对应模块处理事项。
              </p>
            </div>

            <div class="grid gap-2.5 sm:grid-cols-2 xl:grid-cols-4">
              <div
                v-for="item in headlineMetrics"
                :key="item.title"
                class="rounded-md border border-white/70 bg-white/78 p-3.5 backdrop-blur"
              >
                <div class="flex items-center justify-between gap-3">
                  <span class="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">
                    {{ item.title }}
                  </span>
                  <i :class="item.icon" class="text-lg text-cyan-600"></i>
                </div>
                <div class="mt-4 text-3xl font-black tracking-tight text-slate-900">
                  {{ item.value }}
                </div>
                <p class="mt-2 text-xs leading-6 text-slate-500">
                  {{ item.desc }}
                </p>
              </div>
            </div>

            <div class="flex flex-wrap gap-2">
              <span
                v-for="item in statusChips"
                :key="item.label"
                class="inline-flex items-center gap-2 rounded-sm border px-3 py-1.5 text-xs font-medium"
                :class="item.className"
              >
                <i :class="item.icon" class="text-sm"></i>
                {{ item.label }}
              </span>
            </div>

            <div class="grid gap-2.5 border-t border-cyan-100/70 pt-3 xl:grid-cols-[minmax(0,1.55fr)_minmax(0,1fr)]">
              <section class="rounded-md border border-slate-200/70 bg-white/90 p-3 shadow-[0_10px_24px_rgba(15,23,42,0.06)]">
                <div class="flex items-center justify-between">
                  <div>
                    <div class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-500">运营风险概览</div>
                    <div class="mt-1 text-sm font-semibold text-slate-900">关注当前需要优先处理的排课与学情状态</div>
                  </div>
                  <span class="rounded-sm px-2.5 py-1 text-xs font-semibold" :class="riskBadgeClass">
                    {{ riskLabel }}
                  </span>
                </div>

                <div class="mt-3 grid gap-2.5 md:grid-cols-3">
                  <div class="rounded-sm border border-slate-200 bg-slate-950 p-3 text-white">
                    <div class="flex items-start justify-between gap-3">
                      <div>
                        <div class="text-xs uppercase tracking-[0.18em] text-slate-400">学情预警</div>
                        <strong class="mt-2 block text-4xl font-black leading-none">{{ dashboard.warningCount || 0 }}</strong>
                      </div>
                      <i class="ri-alarm-warning-line text-lg text-amber-300"></i>
                    </div>
                    <p class="mt-3 text-xs leading-5 text-slate-300">{{ warningHint }}</p>
                  </div>

                  <div class="rounded-sm border border-amber-200 bg-amber-50 p-3">
                    <div class="flex items-start justify-between gap-3">
                      <div>
                        <div class="text-xs font-semibold uppercase tracking-[0.16em] text-amber-700">待排课教学班</div>
                        <div class="mt-2 text-3xl font-black text-amber-900">{{ metrics.pendingArrangeCount }}</div>
                      </div>
                      <i class="ri-calendar-todo-line text-lg text-amber-600"></i>
                    </div>
                    <p class="mt-2 text-xs leading-5 text-amber-800">建议先处理总课时尚未排满的班级课程。</p>
                  </div>

                  <div class="rounded-sm border border-cyan-200 bg-cyan-50 p-3">
                    <div class="flex items-center justify-between gap-3">
                      <div>
                        <div class="text-xs font-semibold uppercase tracking-[0.16em] text-cyan-700">排课完成度</div>
                        <p class="mt-1 text-xs leading-5 text-cyan-800">已进入执行阶段的教学班覆盖率。</p>
                      </div>
                      <i class="ri-line-chart-line text-lg text-cyan-700"></i>
                    </div>
                    <div class="mt-3 flex items-end justify-between gap-3">
                      <strong class="text-3xl font-black text-cyan-950">{{ scheduleCoverageLabel }}</strong>
                      <span class="text-xs font-semibold text-cyan-700">{{ metrics.arrangedClassCourseCount }}/{{ metrics.classCourseCount }}</span>
                    </div>
                    <div class="mt-3 h-2 rounded-sm bg-cyan-100">
                      <div
                        class="h-2 rounded-sm bg-gradient-to-r from-cyan-500 to-sky-600 transition-all duration-300"
                        :style="{ width: `${scheduleCoveragePercent}%` }"
                      ></div>
                    </div>
                  </div>
                </div>
              </section>

              <section class="rounded-md border border-slate-200/70 bg-white/90 p-3 shadow-[0_10px_20px_rgba(15,23,42,0.05)]">
                <div class="flex items-center justify-between">
                  <div class="text-sm font-semibold text-slate-900">今日建议</div>
                  <i class="ri-flashlight-line text-lg text-emerald-600"></i>
                </div>
                <ul class="mt-3 grid gap-2 text-sm text-slate-600">
                  <li v-for="tip in focusTips" :key="tip" class="flex items-start gap-2.5">
                    <span class="mt-1 inline-flex h-5 w-5 items-center justify-center rounded-sm bg-emerald-100 text-emerald-700">
                      <i class="ri-check-line text-xs"></i>
                    </span>
                    <span class="leading-6">{{ tip }}</span>
                  </li>
                </ul>
              </section>
            </div>
          </div>
        </div>
      </section>

      <section class="grid gap-2.5 xl:grid-cols-[minmax(0,1.55fr)_340px]">
        <div class="grid gap-3">
          <div class="grid gap-3 lg:grid-cols-2 2xl:grid-cols-4">
            <div
              v-for="item in topMetrics"
              :key="item.title"
              class="rounded-md border border-slate-200 bg-white p-4 shadow-[0_8px_18px_rgba(15,23,42,0.05)] transition duration-200 hover:-translate-y-0.5 hover:shadow-[0_12px_24px_rgba(15,23,42,0.07)]"
            >
              <div class="flex items-center justify-between gap-3">
                <span class="text-sm font-semibold text-slate-700">{{ item.title }}</span>
                <div class="flex h-9 w-9 items-center justify-center rounded-sm bg-slate-100 text-slate-600">
                  <i :class="item.icon" class="text-lg"></i>
                </div>
              </div>
              <div class="mt-4 text-4xl font-black tracking-tight text-slate-900">
                {{ item.value }}
              </div>
              <p class="mt-2 text-xs leading-6 text-slate-500">
                {{ item.desc }}
              </p>
            </div>
          </div>

          <div class="grid gap-3 2xl:grid-cols-[minmax(0,1.1fr)_minmax(0,0.9fr)]">
            <section class="rounded-md border border-slate-200 bg-white p-4 shadow-[0_10px_22px_rgba(15,23,42,0.05)]">
              <div class="flex flex-col gap-2 border-b border-slate-100 pb-3 md:flex-row md:items-end md:justify-between">
                <div>
                  <div class="text-lg font-bold text-slate-900">用户基础</div>
                  <p class="mt-1 text-sm text-slate-500">快速了解当前平台服务的用户规模，方便判断运营覆盖范围和活跃基础。</p>
                </div>
                <span class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">Identity Base</span>
              </div>

              <div class="mt-4 grid gap-2.5 sm:grid-cols-2 xl:grid-cols-3">
                <div
                  v-for="item in userMetrics"
                  :key="item.title"
                  class="rounded-sm border border-slate-200 bg-slate-50/80 p-3.5"
                >
                  <div class="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">{{ item.title }}</div>
                  <div class="mt-3 text-3xl font-black tracking-tight text-slate-900">{{ item.value }}</div>
                  <p class="mt-2 text-xs leading-6 text-slate-500">{{ item.desc }}</p>
                </div>
              </div>
            </section>

            <section class="rounded-md border border-slate-200 bg-white p-4 shadow-[0_10px_22px_rgba(15,23,42,0.05)]">
              <div class="flex flex-col gap-2 border-b border-slate-100 pb-3 md:flex-row md:items-end md:justify-between">
                <div>
                  <div class="text-lg font-bold text-slate-900">教学资源就绪度</div>
                  <p class="mt-1 text-sm text-slate-500">查看本学期教学组织是否准备充分，及时发现排课、教室和课程资源上的缺口。</p>
                </div>
                <span class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">Teaching Base</span>
              </div>

              <div class="mt-4 space-y-3">
                <div
                  v-for="item in readinessMetrics"
                  :key="item.title"
                  class="rounded-sm border border-slate-200 bg-white p-3.5"
                >
                  <div class="flex items-center justify-between gap-3">
                    <div>
                      <div class="text-sm font-semibold text-slate-800">{{ item.title }}</div>
                      <p class="mt-1 text-xs text-slate-500">{{ item.desc }}</p>
                    </div>
                    <div class="text-right">
                      <div class="text-2xl font-black tracking-tight text-slate-900">{{ item.value }}</div>
                      <div class="text-[11px] font-semibold text-slate-400">{{ item.caption }}</div>
                    </div>
                  </div>
                  <div class="mt-3 h-2 rounded-sm bg-slate-100">
                    <div
                      class="h-2 rounded-sm transition-all duration-300"
                      :class="item.barClass"
                      :style="{ width: `${item.percent}%` }"
                    ></div>
                  </div>
                </div>
              </div>

              <div class="mt-4 grid gap-2.5 sm:grid-cols-2">
                <div
                  v-for="item in teachingMetrics"
                  :key="item.title"
                  class="rounded-sm border border-slate-200 bg-slate-50/80 p-3.5"
                >
                  <div class="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">{{ item.title }}</div>
                  <div class="mt-2 text-2xl font-black tracking-tight text-slate-900">{{ item.value }}</div>
                  <p class="mt-1 text-xs leading-6 text-slate-500">{{ item.desc }}</p>
                </div>
              </div>
            </section>
          </div>
        </div>

        <aside class="content-start">
          <section class="rounded-md border border-slate-200 bg-white p-3.5 shadow-[0_10px_22px_rgba(15,23,42,0.05)]">
            <div class="flex items-center justify-between border-b border-slate-100 pb-3">
              <div>
                <div class="text-lg font-bold text-slate-900">待处理事项</div>
                <p class="mt-1 text-sm text-slate-500">建议优先从这些事项开始处理，能更快稳定本学期教学运行。</p>
              </div>
              <span class="inline-flex h-11 min-w-[52px] shrink-0 flex-col items-center justify-center rounded-md bg-slate-100 px-2 text-xs font-semibold leading-tight text-slate-600">
                <span>{{ actionItems.length }}</span>
                <span>项</span>
              </span>
            </div>

            <div class="mt-3 grid gap-2">
              <button
                v-for="item in actionItems"
                :key="item.title"
                type="button"
                class="group grid w-full cursor-pointer grid-cols-[44px_minmax(0,1fr)_68px] items-start gap-3 rounded-md border px-3 py-3 text-left transition duration-200 hover:-translate-y-0.5"
                :class="item.className"
                @click="navigateTo(item.path)"
              >
                <div class="flex h-11 w-11 shrink-0 items-center justify-center rounded-md bg-white/80 shadow-sm">
                  <i :class="item.icon" class="text-lg"></i>
                </div>
                <div class="min-w-0 pt-0.5">
                  <div class="truncate text-[15px] font-semibold leading-6">{{ item.title }}</div>
                  <p class="mt-4 pr-1 text-xs leading-6 opacity-80">
                    {{ item.desc }}
                  </p>
                </div>
                <div class="pt-0.5 text-right">
                  <div class="text-[42px] font-black leading-none">{{ item.value }}</div>
                  <div class="mt-4 text-[12px] font-semibold uppercase tracking-[0.2em] opacity-70">{{ item.level }}</div>
                </div>
              </button>
            </div>
          </section>

          <section class="mt-4 rounded-md border border-slate-200 bg-white p-3.5 shadow-[0_10px_22px_rgba(15,23,42,0.05)]">
            <div class="flex items-center justify-between border-b border-slate-100 pb-3">
              <div>
                <div class="text-lg font-bold text-slate-900">快捷入口</div>
                <p class="mt-1 text-sm text-slate-500">常用管理入口集中在这里，便于快速切换到日常高频工作。</p>
              </div>
              <i class="ri-links-line text-lg text-slate-400"></i>
            </div>

            <div class="mt-3 grid grid-cols-1 gap-2">
              <button
                v-for="item in quickLinks"
                :key="item.title"
                type="button"
                class="group grid w-full cursor-pointer grid-cols-[44px_minmax(0,1fr)] items-start gap-3 rounded-md border border-slate-200 bg-slate-50/80 px-3 py-3 text-left transition duration-200 hover:-translate-y-0.5 hover:border-cyan-200 hover:bg-cyan-50"
                @click="navigateTo(item.path)"
              >
                <div class="flex h-11 w-11 shrink-0 items-center justify-center rounded-md bg-white text-cyan-700 shadow-sm">
                  <i :class="item.icon" class="text-xl"></i>
                </div>
                <div class="min-w-0 pt-0.5">
                  <div class="text-[15px] font-semibold leading-6 text-slate-900">{{ item.title }}</div>
                  <p class="mt-2 text-xs leading-6 text-slate-500">{{ item.desc }}</p>
                </div>
              </button>
            </div>
          </section>
        </aside>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import useUserStore from '@/store/modules/user'
import { getCampusDashboard } from '@/api/campus/overview'
import { listGrade, listClass, listCourse, listClassCourse, listCourseSchedule, listSchoolTerm, listClassroom } from '@/api/campus/teaching'
import { listUser } from '@/api/system/user'

const router = useRouter()
const userStore = useUserStore()

const dashboard = reactive<any>({})
const metrics = reactive<any>({
  userTotal: 0,
  studentCount: 0,
  teacherCount: 0,
  parentCount: 0,
  adminCount: 0,
  gradeCount: 0,
  classCount: 0,
  courseCount: 0,
  classCourseCount: 0,
  scheduleCount: 0,
  classroomCount: 0,
  termCount: 0,
  enabledTermCount: 0,
  pendingArrangeCount: 0,
  arrangedClassCourseCount: 0,
})

const currentTermLabel = computed(() => {
  const label = dashboard.currentTermName || dashboard.currentTermLabel
  return label || '未识别当前学期'
})

const warningHint = computed(() => {
  const count = Number(dashboard.warningCount || 0)
  if (count <= 0) return '当前没有学情预警'
  if (count <= 5) return '建议今日内完成核查'
  return '建议优先进入学情预警页处理'
})

const scheduleCoveragePercent = computed(() => {
  if (!metrics.classCourseCount) return 0
  return Math.min(100, Math.round((metrics.arrangedClassCourseCount / metrics.classCourseCount) * 100))
})

const scheduleCoverageLabel = computed(() => `${scheduleCoveragePercent.value}%`)

const enabledTermPercent = computed(() => {
  if (!metrics.termCount) return 0
  return Math.min(100, Math.round((metrics.enabledTermCount / metrics.termCount) * 100))
})

const warningRiskLevel = computed(() => {
  const count = Number(dashboard.warningCount || 0)
  if (count <= 0) return 'stable'
  if (count <= 5) return 'attention'
  return 'urgent'
})

const riskLabel = computed(() => {
  if (warningRiskLevel.value === 'stable') return '平稳'
  if (warningRiskLevel.value === 'attention') return '关注'
  return '优先处理'
})

const riskBadgeClass = computed(() => {
  if (warningRiskLevel.value === 'stable') return 'bg-emerald-100 text-emerald-700'
  if (warningRiskLevel.value === 'attention') return 'bg-amber-100 text-amber-700'
  return 'bg-rose-100 text-rose-700'
})

const headlineMetrics = computed(() => [
  { title: '用户总量', value: metrics.userTotal, desc: '当前平台已纳管的全部账号数量', icon: 'ri-team-line' },
  { title: '教学班关系', value: metrics.classCourseCount, desc: '已完成课程、班级与教师关联的教学班数量', icon: 'ri-node-tree' },
  { title: '排课记录', value: metrics.scheduleCount, desc: '本系统当前生成的正式排课记录总数', icon: 'ri-calendar-schedule-line' },
  { title: '学习行为', value: dashboard.studyRecordCount || 0, desc: '已采集的学习过程数据，可用于学情分析', icon: 'ri-line-chart-line' },
])

const statusChips = computed(() => [
  {
    label: `启用学期 ${metrics.enabledTermCount}/${metrics.termCount || 0}`,
    icon: 'ri-calendar-check-line',
    className: 'border-cyan-200 bg-white/80 text-cyan-800',
  },
  {
    label: `待排课程 ${metrics.pendingArrangeCount}`,
    icon: 'ri-time-line',
    className: metrics.pendingArrangeCount > 0 ? 'border-amber-200 bg-amber-50 text-amber-800' : 'border-emerald-200 bg-emerald-50 text-emerald-800',
  },
  {
    label: `考试记录 ${dashboard.examRecordCount || 0}`,
    icon: 'ri-file-chart-line',
    className: 'border-slate-200 bg-slate-50 text-slate-700',
  },
])

const topMetrics = computed(() => [
  { title: '已排课教学班', value: metrics.arrangedClassCourseCount, desc: '已经进入排课执行阶段的教学班数量。', icon: 'ri-checkbox-circle-line' },
  { title: '待排课教学班', value: metrics.pendingArrangeCount, desc: '仍有课时未排满，建议尽快补齐安排。', icon: 'ri-alarm-warning-line' },
  { title: '学情预警', value: dashboard.warningCount || 0, desc: '需要及时跟进的学习风险提醒。', icon: 'ri-notification-3-line' },
  { title: '教室资源', value: metrics.classroomCount, desc: '当前可投入教学使用的教室资源数量。', icon: 'ri-building-4-line' },
])

const userMetrics = computed(() => [
  { title: '学生', value: metrics.studentCount, desc: '当前纳入平台管理的学生账号数量' },
  { title: '教师', value: metrics.teacherCount, desc: '任课教师与班主任账号数量' },
  { title: '家长', value: metrics.parentCount, desc: '参与家校协同的家长账号数量' },
  { title: '管理员', value: metrics.adminCount, desc: '负责后台运营与教务管理的账号数量' },
  { title: '全部用户', value: metrics.userTotal, desc: '平台统一身份体系下的账号总量' },
  { title: '考试记录', value: dashboard.examRecordCount || 0, desc: '当前已沉淀的考试与测评记录数量' },
])

const teachingMetrics = computed(() => [
  { title: '年级', value: metrics.gradeCount, desc: '当前已配置的年级数量' },
  { title: '班级', value: metrics.classCount, desc: '当前参与教学组织的班级数量' },
  { title: '课程', value: metrics.courseCount, desc: '系统中维护的课程数量' },
  { title: '排课表', value: metrics.scheduleCount, desc: '已经生成的正式排课记录数量' },
  { title: '教室', value: metrics.classroomCount, desc: '可用于排课的教室资源数量' },
  { title: '学期', value: metrics.enabledTermCount, desc: `当前启用 ${metrics.enabledTermCount} 个学期，共维护 ${metrics.termCount} 个学期` },
])

const readinessMetrics = computed(() => [
  {
    title: '排课覆盖率',
    value: `${scheduleCoveragePercent.value}%`,
    caption: `${metrics.arrangedClassCourseCount}/${metrics.classCourseCount || 0}`,
    percent: scheduleCoveragePercent.value,
    desc: '教学班中已有明确排课安排的覆盖情况。',
    barClass: scheduleCoveragePercent.value >= 80 ? 'bg-gradient-to-r from-emerald-500 to-cyan-500' : 'bg-gradient-to-r from-amber-400 to-orange-500',
  },
  {
    title: '启用学期占比',
    value: `${enabledTermPercent.value}%`,
    caption: `${metrics.enabledTermCount}/${metrics.termCount || 0}`,
    percent: enabledTermPercent.value,
    desc: '当前学期配置是否清晰、是否可支撑正常教学安排。',
    barClass: 'bg-gradient-to-r from-cyan-500 to-sky-600',
  },
])

const actionItems = computed(() => [
  {
    title: '处理待排课教学班',
    value: metrics.pendingArrangeCount,
    desc: '优先补齐尚未排满课时的班级课程安排。',
    path: 'courseSchedule',
    level: metrics.pendingArrangeCount > 0 ? 'High' : 'Normal',
    icon: 'ri-calendar-todo-line',
    className: metrics.pendingArrangeCount > 0
      ? 'border-amber-200 bg-amber-50 text-amber-900 hover:border-amber-300 hover:shadow-[0_14px_30px_rgba(245,158,11,0.12)]'
      : 'border-emerald-200 bg-emerald-50 text-emerald-900 hover:border-emerald-300 hover:shadow-[0_14px_30px_rgba(16,185,129,0.10)]',
  },
  {
    title: '查看学情预警',
    value: dashboard.warningCount || 0,
    desc: '查看风险学生情况，并尽快安排预警处理。',
    path: 'warning',
    level: Number(dashboard.warningCount || 0) > 5 ? 'High' : 'Watch',
    icon: 'ri-alert-line',
    className: Number(dashboard.warningCount || 0) > 5
      ? 'border-rose-200 bg-rose-50 text-rose-900 hover:border-rose-300 hover:shadow-[0_14px_30px_rgba(244,63,94,0.12)]'
      : 'border-cyan-200 bg-cyan-50 text-cyan-900 hover:border-cyan-300 hover:shadow-[0_14px_30px_rgba(8,145,178,0.10)]',
  },
  {
    title: '检查班级课表',
    value: metrics.scheduleCount,
    desc: '核对课表安排、教室占用和时间冲突。',
    path: 'classSchedule',
    level: 'Check',
    icon: 'ri-table-line',
    className: 'border-slate-200 bg-slate-50 text-slate-900 hover:border-slate-300 hover:shadow-[0_14px_30px_rgba(15,23,42,0.10)]',
  },
  {
    title: '维护教室资源',
    value: metrics.classroomCount,
    desc: '完善教室容量、校区和类型信息，减少排课阻塞。',
    path: 'classroom',
    level: 'Base',
    icon: 'ri-building-line',
    className: 'border-sky-200 bg-sky-50 text-sky-900 hover:border-sky-300 hover:shadow-[0_14px_30px_rgba(14,165,233,0.10)]',
  },
])

const quickLinks = [
  { title: '用户与身份', desc: '查看并维护学生、教师、家长和后台账号', path: 'user', icon: 'ri-user-settings-line' },
  { title: '班级课程', desc: '管理教学班、任课教师和课时完成情况', path: 'classCourse', icon: 'ri-book-open-line' },
  { title: '排课表', desc: '处理手工排课、自动排课和课表调整', path: 'courseSchedule', icon: 'ri-calendar-2-line' },
  { title: '学情预警', desc: '查看风险提醒并跟进预警处理进度', path: 'warning', icon: 'ri-radar-line' },
]

const focusTips = computed(() => [
  metrics.pendingArrangeCount > 0
    ? `还有 ${metrics.pendingArrangeCount} 个教学班的课时安排未完成，建议优先处理排课任务。`
    : '当前教学班排课整体平稳，可以把精力转向课程优化和教室资源调整。',
  Number(dashboard.warningCount || 0) > 0
    ? `当前有 ${dashboard.warningCount || 0} 条学情预警，建议结合学习行为和考试记录尽快跟进。`
    : '当前没有新增学情预警，可以把注意力放在教学资源和排课质量检查上。',
  `目前平台共有 ${metrics.userTotal} 个账号、${metrics.classroomCount} 间教室和 ${metrics.scheduleCount} 条排课记录，整体运行情况一目了然。`,
])

const routePathMap: Record<string, string> = {
  user: '/system/user',
  classCourse: '/campus/classCourse',
  courseSchedule: '/campus/courseSchedule',
  classSchedule: '/campus/classSchedule',
  classroom: '/campus/classroom',
  warning: '/campus/analysis/warning',
}

function navigateTo(path: string) {
  router.push(routePathMap[path] || path)
}

async function loadDashboard() {
  const userId = Number(userStore.id || 0)
  if (!userId) return
  const res = await getCampusDashboard({ userId, recommendLimit: 5 })
  Object.assign(dashboard, res.data || {})
}

async function loadUserMetrics() {
  const [allRes, studentRes, teacherRes, parentRes, adminRes] = await Promise.all([
    listUser({ pageNum: 1, pageSize: 1 }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'student' }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'teacher' }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'parent' }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'admin' }),
  ])
  metrics.userTotal = Number(allRes.total || 0)
  metrics.studentCount = Number(studentRes.total || 0)
  metrics.teacherCount = Number(teacherRes.total || 0)
  metrics.parentCount = Number(parentRes.total || 0)
  metrics.adminCount = Number(adminRes.total || 0)
}

async function loadTeachingMetrics() {
  const [gradeRes, classRes, courseRes, classCourseRes, scheduleRes, termRes, classroomRes] = await Promise.all([
    listGrade({ pageNum: 1, pageSize: 1 }),
    listClass({ pageNum: 1, pageSize: 1 }),
    listCourse({ pageNum: 1, pageSize: 1 }),
    listClassCourse({ pageNum: 1, pageSize: 500 }),
    listCourseSchedule({ pageNum: 1, pageSize: 1 }),
    listSchoolTerm({ pageNum: 1, pageSize: 200 }),
    listClassroom({ pageNum: 1, pageSize: 1 }),
  ])

  const classCourseRows = classCourseRes.rows || []

  metrics.gradeCount = Number(gradeRes.total || 0)
  metrics.classCount = Number(classRes.total || 0)
  metrics.courseCount = Number(courseRes.total || 0)
  metrics.classCourseCount = Number(classCourseRes.total || classCourseRows.length || 0)
  metrics.scheduleCount = Number(scheduleRes.total || 0)
  metrics.classroomCount = Number(classroomRes.total || 0)
  metrics.termCount = Number(termRes.total || termRes.rows?.length || 0)
  metrics.enabledTermCount = (termRes.rows || []).filter((item: any) => item.status === '0').length
  metrics.pendingArrangeCount = classCourseRows.filter((item: any) => Number(item.totalHours || 0) > Number(item.arrangedHours || 0)).length
  metrics.arrangedClassCourseCount = classCourseRows.filter((item: any) => Number(item.arrangedHours || 0) > 0).length

  const currentTerm = (termRes.rows || []).find((item: any) => item.isCurrent === '1')
  if (currentTerm) {
    dashboard.currentTermName = currentTerm.termName
    dashboard.currentTermLabel = `${currentTerm.termName}（${currentTerm.schoolYear}）`
  }
}

onMounted(async () => {
  await Promise.all([loadDashboard(), loadUserMetrics(), loadTeachingMetrics()])
})
</script>
