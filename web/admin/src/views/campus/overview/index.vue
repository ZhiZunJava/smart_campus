<template>
  <div class="ov-page">
    <!-- ===== Hero Banner ===== -->
    <section class="ov-hero">
      <div class="ov-hero__bg"></div>
      <div class="ov-hero__content">
        <div class="ov-hero__top">
          <div class="ov-hero__badge"><i class="ri-dashboard-line" /> 校园综合概览</div>
          <span class="ov-hero__term" v-if="currentTermLabel">{{ currentTermLabel }}</span>
        </div>

        <div class="ov-hero__intro">
          <h1>智慧校园运营中心</h1>
          <p>这里汇总了校园的用户规模、教学组织、考试数据与事务审批等核心运营指标，帮助管理者快速掌握全局。</p>
        </div>

        <!-- Headline Metrics -->
        <div class="ov-metric-grid ov-metric-grid--4">
          <div v-for="item in headlineMetrics" :key="item.title" class="ov-metric-card ov-metric-card--glass">
            <div class="ov-metric-card__head">
              <span class="ov-metric-card__label">{{ item.title }}</span>
              <i :class="item.icon" class="ov-metric-card__icon" />
            </div>
            <div class="ov-metric-card__value">{{ item.value }}</div>
            <p class="ov-metric-card__desc">{{ item.desc }}</p>
          </div>
        </div>

        <!-- Status Chips -->
        <div class="ov-chips">
          <span v-for="chip in statusChips" :key="chip.label" class="ov-chip" :class="chip.cls">
            <i :class="chip.icon" /> {{ chip.label }}
          </span>
        </div>
      </div>
    </section>

    <!-- ===== Main Content ===== -->
    <div class="ov-body">
      <div class="ov-body__main">
        <!-- User Distribution -->
        <section class="ov-card">
          <div class="ov-card__header">
            <div>
              <h2>用户基础</h2>
              <p>当前平台各类用户规模概况</p>
            </div>
            <span class="ov-card__tag">Identity Base</span>
          </div>
          <div class="ov-metric-grid ov-metric-grid--3">
            <div v-for="item in userMetrics" :key="item.title" class="ov-stat-box">
              <div class="ov-stat-box__label">{{ item.title }}</div>
              <div class="ov-stat-box__value">{{ item.value }}</div>
              <p class="ov-stat-box__desc">{{ item.desc }}</p>
            </div>
          </div>
        </section>

        <!-- Teaching Readiness -->
        <section class="ov-card">
          <div class="ov-card__header">
            <div>
              <h2>教学组织就绪度</h2>
              <p>查看排课覆盖与学期配置情况</p>
            </div>
            <span class="ov-card__tag">Teaching</span>
          </div>
          <div class="ov-progress-list">
            <div v-for="item in readinessMetrics" :key="item.title" class="ov-progress-item">
              <div class="ov-progress-item__head">
                <div>
                  <div class="ov-progress-item__title">{{ item.title }}</div>
                  <p class="ov-progress-item__desc">{{ item.desc }}</p>
                </div>
                <div class="ov-progress-item__right">
                  <div class="ov-progress-item__value">{{ item.value }}</div>
                  <div class="ov-progress-item__caption">{{ item.caption }}</div>
                </div>
              </div>
              <div class="ov-progress-bar">
                <div class="ov-progress-bar__fill" :class="item.barCls" :style="{ width: item.percent + '%' }" />
              </div>
            </div>
          </div>
          <div class="ov-metric-grid ov-metric-grid--3" style="margin-top: 16px;">
            <div v-for="item in teachingMetrics" :key="item.title" class="ov-stat-box">
              <div class="ov-stat-box__label">{{ item.title }}</div>
              <div class="ov-stat-box__value">{{ item.value }}</div>
              <p class="ov-stat-box__desc">{{ item.desc }}</p>
            </div>
          </div>
        </section>

        <!-- Exam & Resource -->
        <section class="ov-card">
          <div class="ov-card__header">
            <div>
              <h2>考试与教学资源</h2>
              <p>题库、试卷和教学资源的沉淀情况</p>
            </div>
            <span class="ov-card__tag">Exam & Resource</span>
          </div>
          <div class="ov-metric-grid ov-metric-grid--4">
            <div v-for="item in examResourceMetrics" :key="item.title" class="ov-stat-box">
              <div class="ov-stat-box__label">{{ item.title }}</div>
              <div class="ov-stat-box__value" :class="item.cls">{{ item.value }}</div>
              <p class="ov-stat-box__desc">{{ item.desc }}</p>
            </div>
          </div>
        </section>
      </div>

      <!-- Sidebar -->
      <aside class="ov-body__side">
        <!-- Affairs -->
        <section class="ov-card">
          <div class="ov-card__header">
            <div>
              <h2>事务审批</h2>
              <p>学生事务与审批状态概况</p>
            </div>
          </div>
          <div class="ov-affair-stats">
            <div v-for="item in affairItems" :key="item.label" class="ov-affair-item" :class="item.cls">
              <i :class="item.icon" />
              <div class="ov-affair-item__info">
                <span class="ov-affair-item__label">{{ item.label }}</span>
                <strong class="ov-affair-item__value">{{ item.value }}</strong>
              </div>
            </div>
          </div>
        </section>

        <!-- Quick Links -->
        <section class="ov-card">
          <div class="ov-card__header">
            <div>
              <h2>快捷入口</h2>
              <p>高频管理模块</p>
            </div>
            <i class="ri-links-line ov-card__icon" />
          </div>
          <div class="ov-links">
            <button v-for="link in quickLinks" :key="link.title" class="ov-link" @click="navigateTo(link.path)">
              <div class="ov-link__icon"><i :class="link.icon" /></div>
              <div class="ov-link__text">
                <div class="ov-link__title">{{ link.title }}</div>
                <p class="ov-link__desc">{{ link.desc }}</p>
              </div>
            </button>
          </div>
        </section>

        <!-- Today Tips -->
        <section class="ov-card">
          <div class="ov-card__header">
            <div><h2>今日建议</h2></div>
            <i class="ri-flashlight-line ov-card__icon is-green" />
          </div>
          <ul class="ov-tips">
            <li v-for="(tip, idx) in focusTips" :key="idx">
              <span class="ov-tips__dot"><i class="ri-check-line" /></span>
              <span>{{ tip }}</span>
            </li>
          </ul>
        </section>
      </aside>
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
import { listResource } from '@/api/campus/resource'
import { listQuestion, listPaper, listRecord } from '@/api/campus/exam'
import { listAffairRequest, getAffairRequestStatistics } from '@/api/campus/affair'
import { listVerificationBatch } from '@/api/campus/verification'

const router = useRouter()
const userStore = useUserStore()

const dashboard = reactive<any>({})
const m = reactive({
  userTotal: 0, studentCount: 0, teacherCount: 0, parentCount: 0, adminCount: 0,
  gradeCount: 0, classCount: 0, courseCount: 0, classCourseCount: 0, scheduleCount: 0,
  classroomCount: 0, termCount: 0, enabledTermCount: 0,
  pendingArrangeCount: 0, arrangedClassCourseCount: 0,
  resourceCount: 0, questionCount: 0, paperCount: 0, examRecordCount: 0,
  affairTotal: 0, affairPending: 0, affairApproved: 0, affairRejected: 0,
  verificationBatchCount: 0,
})

const currentTermLabel = computed(() => dashboard.currentTermLabel || dashboard.currentTermName || '')

const scheduleCoveragePct = computed(() => m.classCourseCount ? Math.min(100, Math.round((m.arrangedClassCourseCount / m.classCourseCount) * 100)) : 0)
const enabledTermPct = computed(() => m.termCount ? Math.min(100, Math.round((m.enabledTermCount / m.termCount) * 100)) : 0)

const headlineMetrics = computed(() => [
  { title: '用户总量', value: m.userTotal, desc: '平台已纳管账号总数', icon: 'ri-team-line' },
  { title: '教学班', value: m.classCourseCount, desc: '班级与课程关联总数', icon: 'ri-node-tree' },
  { title: '排课记录', value: m.scheduleCount, desc: '已生成的正式排课', icon: 'ri-calendar-schedule-line' },
  { title: '考试记录', value: m.examRecordCount, desc: '已沉淀的测评数据', icon: 'ri-file-chart-line' },
])

const statusChips = computed(() => [
  { label: `启用学期 ${m.enabledTermCount}/${m.termCount}`, icon: 'ri-calendar-check-line', cls: 'ov-chip--cyan' },
  { label: `待排课程 ${m.pendingArrangeCount}`, icon: 'ri-time-line', cls: m.pendingArrangeCount > 0 ? 'ov-chip--amber' : 'ov-chip--green' },
  { label: `教学资源 ${m.resourceCount}`, icon: 'ri-folder-line', cls: 'ov-chip--slate' },
  { label: `事务申请 ${m.affairTotal}`, icon: 'ri-file-list-3-line', cls: 'ov-chip--slate' },
])

const userMetrics = computed(() => [
  { title: '学生', value: m.studentCount, desc: '当前学生账号' },
  { title: '教师', value: m.teacherCount, desc: '任课与班主任' },
  { title: '家长', value: m.parentCount, desc: '家校协同账号' },
  { title: '管理员', value: m.adminCount, desc: '后台管理账号' },
  { title: '全部用户', value: m.userTotal, desc: '统一身份账号总量' },
])

const readinessMetrics = computed(() => [
  { title: '排课覆盖率', value: scheduleCoveragePct.value + '%', caption: `${m.arrangedClassCourseCount}/${m.classCourseCount}`, percent: scheduleCoveragePct.value, desc: '教学班已排课覆盖情况', barCls: scheduleCoveragePct.value >= 80 ? 'is-green' : 'is-amber' },
  { title: '启用学期', value: enabledTermPct.value + '%', caption: `${m.enabledTermCount}/${m.termCount}`, percent: enabledTermPct.value, desc: '学期配置是否支撑正常教学', barCls: 'is-cyan' },
])

const teachingMetrics = computed(() => [
  { title: '年级', value: m.gradeCount, desc: '已配置年级数' },
  { title: '班级', value: m.classCount, desc: '参与教学的班级' },
  { title: '课程', value: m.courseCount, desc: '系统维护课程数' },
  { title: '排课表', value: m.scheduleCount, desc: '已生成排课记录' },
  { title: '教室', value: m.classroomCount, desc: '可排课教室' },
  { title: '学期', value: `${m.enabledTermCount}/${m.termCount}`, desc: '启用/总学期' },
])

const examResourceMetrics = computed(() => [
  { title: '题目总数', value: m.questionCount, desc: '题库中的题目数量', cls: '' },
  { title: '试卷总数', value: m.paperCount, desc: '已组装的试卷数', cls: '' },
  { title: '考试记录', value: m.examRecordCount, desc: '测评记录沉淀', cls: 'is-primary' },
  { title: '教学资源', value: m.resourceCount, desc: '课程相关资源', cls: '' },
])

const affairItems = computed(() => [
  { label: '全部申请', value: m.affairTotal, icon: 'ri-file-list-3-line', cls: '' },
  { label: '待审核', value: m.affairPending, icon: 'ri-time-line', cls: 'is-warning' },
  { label: '已通过', value: m.affairApproved, icon: 'ri-checkbox-circle-line', cls: 'is-success' },
  { label: '已驳回', value: m.affairRejected, icon: 'ri-close-circle-line', cls: 'is-danger' },
])

const quickLinks = [
  { title: '用户管理', desc: '学生、教师、家长账号维护', path: '/system/user', icon: 'ri-user-settings-line' },
  { title: '班级课程', desc: '教学班与任课关联', path: '/campus/classCourse', icon: 'ri-book-open-line' },
  { title: '排课管理', desc: '课表排课与调整', path: '/campus/courseSchedule', icon: 'ri-calendar-2-line' },
  { title: '考试记录', desc: '测评数据查看', path: '/campus/exam/record', icon: 'ri-radar-line' },
  { title: '事务审批', desc: '学生事务申请处理', path: '/campus/affair/request', icon: 'ri-file-list-3-line' },
  { title: '学籍核对', desc: '批次核对与审核', path: '/campus/verification', icon: 'ri-shield-check-line' },
]

const focusTips = computed(() => [
  m.pendingArrangeCount > 0
    ? `还有 ${m.pendingArrangeCount} 个教学班的课时未排满，建议优先处理。`
    : '教学班排课整体平稳，可关注课程优化和教室调整。',
  m.affairPending > 0
    ? `当前有 ${m.affairPending} 条事务申请待审核，请及时处理。`
    : '事务审批已清零，暂无待处理事项。',
  `平台共 ${m.userTotal} 个账号、${m.classroomCount} 间教室、${m.resourceCount} 个教学资源。`,
])

function navigateTo(path: string) { router.push(path) }

async function loadAll() {
  await Promise.all([loadDashboard(), loadUsers(), loadTeaching(), loadExamResource(), loadAffairs(), loadVerification()])
}

async function loadDashboard() {
  try {
    const userId = Number(userStore.id || 0)
    if (!userId) return
    const res = await getCampusDashboard({ userId, recommendLimit: 5 })
    Object.assign(dashboard, res.data || {})
  } catch { /* ignore */ }
}

async function loadUsers() {
  const [all, stu, tea, par, adm] = await Promise.all([
    listUser({ pageNum: 1, pageSize: 1 }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'student' }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'teacher' }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'parent' }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'admin' }),
  ])
  m.userTotal = Number(all.total || 0)
  m.studentCount = Number(stu.total || 0)
  m.teacherCount = Number(tea.total || 0)
  m.parentCount = Number(par.total || 0)
  m.adminCount = Number(adm.total || 0)
}

async function loadTeaching() {
  const [gradeRes, classRes, courseRes, ccRes, schedRes, termRes, crRes] = await Promise.all([
    listGrade({ pageNum: 1, pageSize: 1 }),
    listClass({ pageNum: 1, pageSize: 1 }),
    listCourse({ pageNum: 1, pageSize: 1 }),
    listClassCourse({ pageNum: 1, pageSize: 500 }),
    listCourseSchedule({ pageNum: 1, pageSize: 1 }),
    listSchoolTerm({ pageNum: 1, pageSize: 200 }),
    listClassroom({ pageNum: 1, pageSize: 1 }),
  ])
  const ccRows = ccRes.rows || []
  m.gradeCount = Number(gradeRes.total || 0)
  m.classCount = Number(classRes.total || 0)
  m.courseCount = Number(courseRes.total || 0)
  m.classCourseCount = Number(ccRes.total || ccRows.length || 0)
  m.scheduleCount = Number(schedRes.total || 0)
  m.classroomCount = Number(crRes.total || 0)
  m.termCount = Number(termRes.total || termRes.rows?.length || 0)
  m.enabledTermCount = (termRes.rows || []).filter((i: any) => i.status === '0').length
  m.pendingArrangeCount = ccRows.filter((i: any) => Number(i.totalHours || 0) > Number(i.arrangedHours || 0)).length
  m.arrangedClassCourseCount = ccRows.filter((i: any) => Number(i.arrangedHours || 0) > 0).length
  const ct = (termRes.rows || []).find((i: any) => i.isCurrent === '1')
  if (ct) { dashboard.currentTermName = ct.termName; dashboard.currentTermLabel = `${ct.termName}（${ct.schoolYear}）` }
}

async function loadExamResource() {
  const [qRes, pRes, rRes, resRes] = await Promise.all([
    listQuestion({ pageNum: 1, pageSize: 1 }),
    listPaper({ pageNum: 1, pageSize: 1 }),
    listRecord({ pageNum: 1, pageSize: 1 }),
    listResource({ pageNum: 1, pageSize: 1 }),
  ])
  m.questionCount = Number(qRes.total || 0)
  m.paperCount = Number(pRes.total || 0)
  m.examRecordCount = Number(rRes.total || 0)
  m.resourceCount = Number(resRes.total || 0)
}

async function loadAffairs() {
  try {
    const [totalRes, statsRes] = await Promise.all([
      listAffairRequest({ pageNum: 1, pageSize: 1 }),
      getAffairRequestStatistics(),
    ])
    m.affairTotal = Number(totalRes.total || 0)
    const stats = statsRes.data || statsRes || {}
    m.affairPending = Number(stats.pendingCount || stats.pending || 0)
    m.affairApproved = Number(stats.approvedCount || stats.approved || 0)
    m.affairRejected = Number(stats.rejectedCount || stats.rejected || 0)
  } catch { /* ignore */ }
}

async function loadVerification() {
  try {
    const res = await listVerificationBatch({ pageNum: 1, pageSize: 1 })
    m.verificationBatchCount = Number(res.total || 0)
  } catch { /* ignore */ }
}

onMounted(loadAll)
</script>

<style scoped>
.ov-page { padding: 28px 16px 16px; display: flex; flex-direction: column; gap: 16px; min-height: 100%; background: var(--el-bg-color-page); }

/* ===== Hero ===== */
.ov-hero { position: relative; overflow: hidden; border-radius: 12px; border: 1px solid #e2e8f0; background: linear-gradient(135deg, #ecfeff 0%, #fff 40%, #f0f9ff 100%); }
.ov-hero__bg { position: absolute; inset: 0; background: radial-gradient(circle at top right, rgba(34,211,238,.12), transparent 60%); pointer-events: none; }
.ov-hero__content { position: relative; padding: 20px 24px; }
.ov-hero__top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.ov-hero__badge { display: inline-flex; align-items: center; gap: 6px; padding: 4px 12px; border-radius: 4px; border: 1px solid #a5f3fc; background: rgba(255,255,255,.7); font-size: 11px; font-weight: 600; letter-spacing: .18em; text-transform: uppercase; color: #155e75; }
.ov-hero__term { padding: 4px 12px; border-radius: 4px; border: 1px solid #a7f3d0; background: #ecfdf5; font-size: 12px; font-weight: 600; color: #065f46; }
.ov-hero__intro h1 { margin: 0 0 6px; font-size: 26px; font-weight: 800; color: #0f172a; }
.ov-hero__intro p { margin: 0 0 18px; font-size: 14px; color: #475569; line-height: 1.7; max-width: 680px; }

/* ===== Metric Grid ===== */
.ov-metric-grid { display: grid; gap: 12px; }
.ov-metric-grid--4 { grid-template-columns: repeat(4, 1fr); }
.ov-metric-grid--3 { grid-template-columns: repeat(3, 1fr); }

.ov-metric-card { padding: 14px; border-radius: 8px; border: 1px solid rgba(255,255,255,.7); }
.ov-metric-card--glass { background: rgba(255,255,255,.78); backdrop-filter: blur(6px); }
.ov-metric-card__head { display: flex; align-items: center; justify-content: space-between; }
.ov-metric-card__label { font-size: 11px; font-weight: 600; letter-spacing: .14em; text-transform: uppercase; color: #64748b; }
.ov-metric-card__icon { font-size: 18px; color: #0891b2; }
.ov-metric-card__value { margin-top: 12px; font-size: 28px; font-weight: 800; color: #0f172a; letter-spacing: -.02em; }
.ov-metric-card__desc { margin: 6px 0 0; font-size: 12px; color: #64748b; line-height: 1.5; }

/* ===== Chips ===== */
.ov-chips { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 16px; }
.ov-chip { display: inline-flex; align-items: center; gap: 6px; padding: 5px 12px; border-radius: 4px; border: 1px solid #e2e8f0; font-size: 12px; font-weight: 500; }
.ov-chip i { font-size: 14px; }
.ov-chip--cyan { border-color: #a5f3fc; background: rgba(255,255,255,.8); color: #155e75; }
.ov-chip--amber { border-color: #fde68a; background: #fffbeb; color: #92400e; }
.ov-chip--green { border-color: #a7f3d0; background: #ecfdf5; color: #065f46; }
.ov-chip--slate { border-color: #e2e8f0; background: #f8fafc; color: #475569; }

/* ===== Body Layout ===== */
.ov-body { display: grid; gap: 16px; grid-template-columns: 1fr 360px; align-items: start; }
.ov-body__main { display: flex; flex-direction: column; gap: 16px; }
.ov-body__side { display: flex; flex-direction: column; gap: 16px; }

/* ===== Card ===== */
.ov-card { background: #fff; border-radius: 12px; border: 1px solid #e5eef8; padding: 18px 20px; }
.ov-card__header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #f1f5f9; }
.ov-card__header h2 { margin: 0; font-size: 16px; font-weight: 700; color: #0f172a; }
.ov-card__header p { margin: 4px 0 0; font-size: 13px; color: #94a3b8; }
.ov-card__tag { font-size: 11px; font-weight: 600; letter-spacing: .16em; text-transform: uppercase; color: #94a3b8; white-space: nowrap; }
.ov-card__icon { font-size: 18px; color: #94a3b8; }
.ov-card__icon.is-green { color: #059669; }

/* ===== Stat Box ===== */
.ov-stat-box { padding: 12px 14px; border-radius: 8px; border: 1px solid #e5eef8; background: #f8fafc; }
.ov-stat-box__label { font-size: 11px; font-weight: 600; letter-spacing: .14em; text-transform: uppercase; color: #64748b; }
.ov-stat-box__value { margin-top: 8px; font-size: 24px; font-weight: 800; color: #0f172a; }
.ov-stat-box__value.is-primary { color: #2563eb; }
.ov-stat-box__desc { margin: 4px 0 0; font-size: 12px; color: #94a3b8; }

/* ===== Progress ===== */
.ov-progress-list { display: flex; flex-direction: column; gap: 14px; }
.ov-progress-item { padding: 14px; border-radius: 8px; border: 1px solid #e5eef8; background: #fff; }
.ov-progress-item__head { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.ov-progress-item__title { font-size: 14px; font-weight: 600; color: #1e293b; }
.ov-progress-item__desc { margin: 2px 0 0; font-size: 12px; color: #94a3b8; }
.ov-progress-item__right { text-align: right; }
.ov-progress-item__value { font-size: 24px; font-weight: 800; color: #0f172a; }
.ov-progress-item__caption { font-size: 11px; font-weight: 600; color: #94a3b8; }
.ov-progress-bar { margin-top: 10px; height: 6px; border-radius: 3px; background: #f1f5f9; overflow: hidden; }
.ov-progress-bar__fill { height: 100%; border-radius: 3px; transition: width .3s; }
.ov-progress-bar__fill.is-green { background: linear-gradient(to right, #10b981, #06b6d4); }
.ov-progress-bar__fill.is-amber { background: linear-gradient(to right, #f59e0b, #f97316); }
.ov-progress-bar__fill.is-cyan { background: linear-gradient(to right, #06b6d4, #0284c7); }

/* ===== Affair Stats ===== */
.ov-affair-stats { display: flex; flex-direction: column; gap: 8px; }
.ov-affair-item { display: flex; align-items: center; gap: 12px; padding: 10px 14px; border-radius: 8px; border: 1px solid #e5eef8; background: #f8fafc; }
.ov-affair-item i { font-size: 18px; color: #64748b; }
.ov-affair-item.is-warning { background: #fffbeb; border-color: #fde68a; }
.ov-affair-item.is-warning i { color: #d97706; }
.ov-affair-item.is-success { background: #ecfdf5; border-color: #a7f3d0; }
.ov-affair-item.is-success i { color: #059669; }
.ov-affair-item.is-danger { background: #fef2f2; border-color: #fecaca; }
.ov-affair-item.is-danger i { color: #dc2626; }
.ov-affair-item__info { flex: 1; display: flex; align-items: center; justify-content: space-between; }
.ov-affair-item__label { font-size: 13px; color: #475569; }
.ov-affair-item__value { font-size: 20px; font-weight: 800; color: #0f172a; }

/* ===== Quick Links ===== */
.ov-links { display: flex; flex-direction: column; gap: 8px; }
.ov-link { display: flex; align-items: flex-start; gap: 12px; width: 100%; padding: 10px 12px; border-radius: 8px; border: 1px solid #e5eef8; background: #f8fafc; cursor: pointer; transition: all .2s; text-align: left; }
.ov-link:hover { border-color: #a5f3fc; background: #ecfeff; transform: translateY(-1px); }
.ov-link__icon { width: 36px; height: 36px; border-radius: 8px; background: #fff; display: flex; align-items: center; justify-content: center; flex-shrink: 0; box-shadow: 0 1px 3px rgba(0,0,0,.06); }
.ov-link__icon i { font-size: 18px; color: #0891b2; }
.ov-link__text { min-width: 0; }
.ov-link__title { font-size: 14px; font-weight: 600; color: #0f172a; }
.ov-link__desc { margin: 2px 0 0; font-size: 12px; color: #94a3b8; }

/* ===== Tips ===== */
.ov-tips { list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 10px; }
.ov-tips li { display: flex; align-items: flex-start; gap: 10px; font-size: 13px; color: #475569; line-height: 1.6; }
.ov-tips__dot { width: 20px; height: 20px; border-radius: 4px; background: #ecfdf5; display: flex; align-items: center; justify-content: center; flex-shrink: 0; margin-top: 2px; }
.ov-tips__dot i { font-size: 12px; color: #059669; }

/* ===== Responsive ===== */
@media (max-width: 1200px) {
  .ov-body { grid-template-columns: 1fr; }
  .ov-body__side { flex-direction: row; flex-wrap: wrap; }
  .ov-body__side > .ov-card { flex: 1; min-width: 280px; }
}
@media (max-width: 768px) {
  .ov-metric-grid--4 { grid-template-columns: repeat(2, 1fr); }
  .ov-metric-grid--3 { grid-template-columns: repeat(2, 1fr); }
  .ov-hero__intro h1 { font-size: 20px; }
}
@media (max-width: 480px) {
  .ov-page { padding: 10px; }
  .ov-metric-grid--4, .ov-metric-grid--3 { grid-template-columns: 1fr; }
}
</style>
