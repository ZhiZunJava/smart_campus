<template>
  <div class="ov-page">
    <!-- ===== Hero ===== -->
    <section class="ov-hero ov-hero--indigo">
      <div class="ov-hero__bg"></div>
      <div class="ov-hero__content">
        <div class="ov-hero__top">
          <div class="ov-hero__badge"><i class="ri-graduation-cap-line" /> 学生概览</div>
          <el-select
            v-model="selectedStudentId"
            filterable
            clearable
            placeholder="选择学生查看个人数据"
            style="width: 280px;"
            @change="onStudentChange"
          >
            <el-option v-for="s in studentOptions" :key="s.value" :label="s.shortLabel || s.label" :value="s.value" />
          </el-select>
        </div>

        <div class="ov-hero__intro">
          <h1>学生数据总览</h1>
          <p>汇总全校学生的注册规模、成绩表现、考试参与和课程选修情况。选择具体学生可查看个人学业概况。</p>
        </div>

        <!-- Global Stats -->
        <div class="ov-metric-grid ov-metric-grid--4">
          <div v-for="item in globalMetrics" :key="item.title" class="ov-metric-card ov-metric-card--glass">
            <div class="ov-metric-card__head">
              <span class="ov-metric-card__label">{{ item.title }}</span>
              <i :class="item.icon" class="ov-metric-card__icon" />
            </div>
            <div class="ov-metric-card__value">{{ item.value }}</div>
            <p class="ov-metric-card__desc">{{ item.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== Individual Student Panel ===== -->
    <section v-if="selectedStudentId" class="ov-card ov-card--highlight">
      <div v-if="studentLoading" class="ov-loading">
        <i class="ri-loader-4-line ov-spin" />
        <span>加载学生数据中…</span>
      </div>
      <template v-else>
        <div class="ov-card__header">
          <div>
            <h2><i class="ri-user-3-line" /> {{ studentInfo.realName || studentInfo.nickName || studentInfo.userName || '学生' }} 的学业概况</h2>
            <p>学号: {{ studentInfo.studentNo || '--' }} &nbsp;|&nbsp; 班级: {{ studentInfo.className || studentInfo.major || '--' }}</p>
          </div>
          <el-button size="small" @click="loadStudentDetail" :loading="studentLoading"><i class="ri-refresh-line" /> 刷新</el-button>
        </div>

        <div class="ov-metric-grid ov-metric-grid--4">
          <div v-for="item in studentDetailMetrics" :key="item.title" class="ov-stat-box" :class="item.cls">
            <div class="ov-stat-box__label">{{ item.title }}</div>
            <div class="ov-stat-box__value">{{ item.value }}</div>
            <p class="ov-stat-box__desc">{{ item.desc }}</p>
          </div>
        </div>

        <!-- Recent Scores -->
        <div v-if="recentScores.length" class="ov-section-sub">
          <h3>近期成绩记录</h3>
          <el-table :data="recentScores" stripe size="small" max-height="280">
            <el-table-column prop="courseName" label="课程" min-width="140" show-overflow-tooltip />
            <el-table-column prop="className" label="班级" min-width="120" show-overflow-tooltip />
            <el-table-column label="总评" width="80" align="center">
              <template #default="{ row }">
                <span :class="{ 'is-good': Number(row.totalScore) >= 90, 'is-warn': Number(row.totalScore) < 60 }">
                  {{ row.totalScore ?? '--' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="gradeLevel" label="等级" width="70" align="center" />
            <el-table-column prop="termName" label="学期" width="140" show-overflow-tooltip />
          </el-table>
        </div>

        <!-- Recent Exam Records -->
        <div v-if="recentExams.length" class="ov-section-sub">
          <h3>近期考试记录</h3>
          <el-table :data="recentExams" stripe size="small" max-height="280">
            <el-table-column prop="paperName" label="试卷" min-width="180" show-overflow-tooltip />
            <el-table-column prop="score" label="得分" width="80" align="center" />
            <el-table-column label="正确率" width="90" align="center">
              <template #default="{ row }">
                {{ row.correctRate != null ? (Number(row.correctRate) * 100).toFixed(0) + '%' : '--' }}
              </template>
            </el-table-column>
            <el-table-column prop="submitTime" label="交卷时间" width="160" />
          </el-table>
        </div>

        <div v-if="!recentScores.length && !recentExams.length && !studentLoading" class="ov-empty">
          <i class="ri-inbox-2-line" />
          <p>该学生暂无成绩和考试记录</p>
        </div>
      </template>
    </section>

    <!-- ===== No student selected ===== -->
    <section v-else class="ov-body">
      <div class="ov-body__main">
        <!-- Score Overview -->
        <section class="ov-card">
          <div class="ov-card__header">
            <div>
              <h2>成绩与考试概况</h2>
              <p>全校学生的成绩和考试数据聚合</p>
            </div>
            <span class="ov-card__tag">Score & Exam</span>
          </div>
          <div class="ov-metric-grid ov-metric-grid--3">
            <div v-for="item in scoreExamMetrics" :key="item.title" class="ov-stat-box">
              <div class="ov-stat-box__label">{{ item.title }}</div>
              <div class="ov-stat-box__value">{{ item.value }}</div>
              <p class="ov-stat-box__desc">{{ item.desc }}</p>
            </div>
          </div>
        </section>

        <!-- Course Selection -->
        <section class="ov-card">
          <div class="ov-card__header">
            <div>
              <h2>选课与资源</h2>
              <p>课程选修与教学资源覆盖</p>
            </div>
            <span class="ov-card__tag">Enrollment</span>
          </div>
          <div class="ov-metric-grid ov-metric-grid--3">
            <div v-for="item in enrollmentMetrics" :key="item.title" class="ov-stat-box">
              <div class="ov-stat-box__label">{{ item.title }}</div>
              <div class="ov-stat-box__value">{{ item.value }}</div>
              <p class="ov-stat-box__desc">{{ item.desc }}</p>
            </div>
          </div>
        </section>
      </div>

      <aside class="ov-body__side">
        <section class="ov-card">
          <div class="ov-card__header">
            <div><h2>快捷入口</h2><p>学生相关管理模块</p></div>
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

        <section class="ov-card">
          <div class="ov-card__header">
            <div><h2>数据提示</h2></div>
            <i class="ri-lightbulb-line ov-card__icon is-amber" />
          </div>
          <ul class="ov-tips">
            <li v-for="(tip, idx) in tips" :key="idx">
              <span class="ov-tips__dot"><i class="ri-check-line" /></span>
              <span>{{ tip }}</span>
            </li>
          </ul>
        </section>
      </aside>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { getStudentDashboard } from '@/api/campus/overview'
import { listUser } from '@/api/system/user'
import { listStudentScore } from '@/api/campus/score'
import { listRecord, listQuestion, listPaper } from '@/api/campus/exam'
import { listResource } from '@/api/campus/resource'
import { listCourse } from '@/api/campus/teaching'
import { listUserProfile } from '@/api/campus/userProfile'
import { fetchUserOptions } from '@/api/campus/options'

const router = useRouter()

const studentOptions = ref<any[]>([])
const selectedStudentId = ref<number | undefined>(undefined)
const studentLoading = ref(false)
const studentInfo = reactive<any>({})

const g = reactive({
  studentCount: 0,
  scoreCount: 0,
  examRecordCount: 0,
  questionCount: 0,
  paperCount: 0,
  resourceCount: 0,
  courseCount: 0,
})

const sd = reactive({
  scoreCount: 0,
  examCount: 0,
  avgScore: '--' as string | number,
  highestScore: '--' as string | number,
})

const recentScores = ref<any[]>([])
const recentExams = ref<any[]>([])

const globalMetrics = computed(() => [
  { title: '学生总数', value: g.studentCount, desc: '平台在册学生', icon: 'ri-user-3-line' },
  { title: '成绩记录', value: g.scoreCount, desc: '全部成绩条目', icon: 'ri-bar-chart-2-line' },
  { title: '考试记录', value: g.examRecordCount, desc: '测评参与数据', icon: 'ri-file-chart-line' },
  { title: '教学资源', value: g.resourceCount, desc: '可用学习资源', icon: 'ri-folder-open-line' },
])

const studentDetailMetrics = computed(() => [
  { title: '成绩记录', value: sd.scoreCount, desc: '个人成绩条目', cls: '' },
  { title: '考试记录', value: sd.examCount, desc: '参与的测评', cls: '' },
  { title: '平均成绩', value: sd.avgScore, desc: '综合均分', cls: 'is-accent' },
  { title: '最高分', value: sd.highestScore, desc: '历史最佳', cls: 'is-accent' },
])

const scoreExamMetrics = computed(() => [
  { title: '成绩记录', value: g.scoreCount, desc: '全校学生成绩总量' },
  { title: '考试记录', value: g.examRecordCount, desc: '全部考试参与记录' },
  { title: '题目总数', value: g.questionCount, desc: '题库中题目量' },
  { title: '试卷总数', value: g.paperCount, desc: '已组装试卷数' },
  { title: '课程总数', value: g.courseCount, desc: '系统维护课程' },
])

const enrollmentMetrics = computed(() => [
  { title: '课程数', value: g.courseCount, desc: '全校课程总量' },
  { title: '教学资源', value: g.resourceCount, desc: '课程关联资源' },
  { title: '学生数', value: g.studentCount, desc: '可参与选课的学生' },
])

const quickLinks = [
  { title: '学生管理', desc: '查看学生账号与档案', path: '/system/user', icon: 'ri-user-3-line' },
  { title: '成绩管理', desc: '学生成绩录入与查询', path: '/campus/score', icon: 'ri-bar-chart-2-line' },
  { title: '考试记录', desc: '测评数据查看', path: '/campus/exam/record', icon: 'ri-file-chart-line' },
  { title: '用户档案', desc: '学籍信息维护', path: '/campus/userProfile', icon: 'ri-profile-line' },
]

const tips = computed(() => [
  `当前共有 ${g.studentCount} 名学生，${g.scoreCount} 条成绩记录。`,
  g.examRecordCount > 0
    ? `已沉淀 ${g.examRecordCount} 条考试记录，可用于学情分析。`
    : '暂无考试记录，建议推进测评任务。',
  `题库包含 ${g.questionCount} 道题目，${g.paperCount} 份试卷。`,
])

function navigateTo(path: string) { router.push(path) }

async function loadGlobal() {
  const [stuRes, scoreRes, examRes, qRes, pRes, resRes, courseRes] = await Promise.all([
    listUser({ pageNum: 1, pageSize: 1, userType: 'student' }),
    listStudentScore({ pageNum: 1, pageSize: 1 }),
    listRecord({ pageNum: 1, pageSize: 1 }),
    listQuestion({ pageNum: 1, pageSize: 1 }),
    listPaper({ pageNum: 1, pageSize: 1 }),
    listResource({ pageNum: 1, pageSize: 1 }),
    listCourse({ pageNum: 1, pageSize: 1 }),
  ])
  g.studentCount = Number(stuRes.total || 0)
  g.scoreCount = Number(scoreRes.total || 0)
  g.examRecordCount = Number(examRes.total || 0)
  g.questionCount = Number(qRes.total || 0)
  g.paperCount = Number(pRes.total || 0)
  g.resourceCount = Number(resRes.total || 0)
  g.courseCount = Number(courseRes.total || 0)
}

async function loadStudentDetail() {
  if (!selectedStudentId.value) return
  studentLoading.value = true

  // Set basic info from options first (always available)
  const opt = studentOptions.value.find(o => o.value === selectedStudentId.value)
  studentInfo.userId = selectedStudentId.value
  if (opt) {
    studentInfo.nickName = opt.nickName || opt.shortLabel || opt.label || ''
  }

  try {
    // Load user profile (admin-accessible, reliable)
    try {
      const profileRes = await listUserProfile({ userId: selectedStudentId.value, pageNum: 1, pageSize: 1 })
      const profiles = profileRes.rows || []
      if (profiles.length) {
        const p = profiles[0]
        studentInfo.realName = p.realName || ''
        studentInfo.studentNo = p.studentNo || ''
        studentInfo.gender = p.gender
        studentInfo.major = p.major || ''
        studentInfo.nation = p.nation || ''
        if (!studentInfo.nickName && p.realName) studentInfo.nickName = p.realName
      }
    } catch { /* ignore */ }

    // Try dashboard API (may fail for admin context — that's OK)
    try {
      const res = await getStudentDashboard({ userId: selectedStudentId.value })
      const data = res.data || {}
      Object.assign(studentInfo, data)
    } catch { /* portal API may not work for admin — ignore */ }

    // Load scores (admin-accessible list API) — use studentUserId field
    try {
      const scoreRes = await listStudentScore({ pageNum: 1, pageSize: 10, studentUserId: selectedStudentId.value })
      const rows = scoreRes.rows || []
      recentScores.value = rows
      sd.scoreCount = Number(scoreRes.total || 0)
      // Get className from the first score record (scores carry className from classCourse)
      if (rows.length && rows[0].className && !studentInfo.className) {
        studentInfo.className = rows[0].className
      }
      if (rows.length) {
        const vals = rows.map((r: any) => Number(r.totalScore || 0)).filter((v: number) => v > 0)
        sd.avgScore = vals.length ? (vals.reduce((a: number, b: number) => a + b, 0) / vals.length).toFixed(1) : '--'
        sd.highestScore = vals.length ? Math.max(...vals) : '--'
      } else {
        sd.avgScore = '--'
        sd.highestScore = '--'
      }
    } catch { sd.scoreCount = 0; recentScores.value = [] }

    // Load exam records (admin-accessible list API)
    try {
      const examRes = await listRecord({ pageNum: 1, pageSize: 10, userId: selectedStudentId.value })
      recentExams.value = examRes.rows || []
      sd.examCount = Number(examRes.total || 0)
    } catch { sd.examCount = 0; recentExams.value = [] }
  } finally {
    studentLoading.value = false
  }
}

function onStudentChange(val: number | undefined) {
  if (val) {
    loadStudentDetail()
  } else {
    Object.keys(studentInfo).forEach(k => delete studentInfo[k])
    recentScores.value = []
    recentExams.value = []
  }
}

onMounted(async () => {
  studentOptions.value = await fetchUserOptions('student')
  await loadGlobal()
})
</script>

<style scoped>
.ov-page { padding: 28px 16px 16px; display: flex; flex-direction: column; gap: 16px; min-height: 100%; background: var(--el-bg-color-page); }

/* ===== Hero ===== */
.ov-hero { position: relative; overflow: hidden; border-radius: 12px; border: 1px solid #e2e8f0; }
.ov-hero--indigo { background: linear-gradient(135deg, #eef2ff 0%, #fff 40%, #f5f3ff 100%); }
.ov-hero__bg { position: absolute; inset: 0; background: radial-gradient(circle at top right, rgba(99,102,241,.1), transparent 60%); pointer-events: none; }
.ov-hero__content { position: relative; padding: 20px 24px; }
.ov-hero__top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; flex-wrap: wrap; gap: 12px; }
.ov-hero__badge { display: inline-flex; align-items: center; gap: 6px; padding: 4px 12px; border-radius: 4px; border: 1px solid #c7d2fe; background: rgba(255,255,255,.7); font-size: 11px; font-weight: 600; letter-spacing: .18em; text-transform: uppercase; color: #3730a3; }
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
.ov-metric-card__icon { font-size: 18px; color: #6366f1; }
.ov-metric-card__value { margin-top: 12px; font-size: 28px; font-weight: 800; color: #0f172a; letter-spacing: -.02em; }
.ov-metric-card__desc { margin: 6px 0 0; font-size: 12px; color: #64748b; line-height: 1.5; }

/* ===== Body Layout ===== */
.ov-body { display: grid; gap: 16px; grid-template-columns: 1fr 340px; align-items: start; }
.ov-body__main { display: flex; flex-direction: column; gap: 16px; }
.ov-body__side { display: flex; flex-direction: column; gap: 16px; }

/* ===== Card ===== */
.ov-card { background: #fff; border-radius: 12px; border: 1px solid #e5eef8; padding: 18px 20px; }
.ov-card--highlight { border-color: #c7d2fe; background: linear-gradient(180deg, #fafbff 0%, #fff 100%); }
.ov-card__header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #f1f5f9; }
.ov-card__header h2 { margin: 0; font-size: 16px; font-weight: 700; color: #0f172a; display: flex; align-items: center; gap: 8px; }
.ov-card__header h2 i { color: #6366f1; }
.ov-card__header p { margin: 4px 0 0; font-size: 13px; color: #94a3b8; }
.ov-card__tag { font-size: 11px; font-weight: 600; letter-spacing: .16em; text-transform: uppercase; color: #94a3b8; white-space: nowrap; }
.ov-card__icon { font-size: 18px; color: #94a3b8; }
.ov-card__icon.is-amber { color: #d97706; }

/* ===== Stat Box ===== */
.ov-stat-box { padding: 12px 14px; border-radius: 8px; border: 1px solid #e5eef8; background: #f8fafc; }
.ov-stat-box.is-accent { background: #eef2ff; border-color: #c7d2fe; }
.ov-stat-box__label { font-size: 11px; font-weight: 600; letter-spacing: .14em; text-transform: uppercase; color: #64748b; }
.ov-stat-box__value { margin-top: 8px; font-size: 24px; font-weight: 800; color: #0f172a; }
.ov-stat-box__desc { margin: 4px 0 0; font-size: 12px; color: #94a3b8; }

/* ===== Loading ===== */
.ov-loading { display: flex; align-items: center; justify-content: center; gap: 10px; padding: 40px 0; color: #94a3b8; font-size: 14px; }
.ov-spin { animation: ov-spin 1s linear infinite; }
@keyframes ov-spin { to { transform: rotate(360deg); } }

/* ===== Empty ===== */
.ov-empty { text-align: center; padding: 40px 0; color: #94a3b8; }
.ov-empty i { font-size: 36px; display: block; margin-bottom: 8px; color: #cbd5e1; }
.ov-empty p { font-size: 14px; margin: 0; }

/* ===== Section Sub ===== */
.ov-section-sub { margin-top: 18px; }
.ov-section-sub h3 { margin: 0 0 10px; font-size: 14px; font-weight: 600; color: #334155; }
.is-good { color: #059669; font-weight: 700; }
.is-warn { color: #dc2626; font-weight: 700; }

/* ===== Quick Links ===== */
.ov-links { display: flex; flex-direction: column; gap: 8px; }
.ov-link { display: flex; align-items: flex-start; gap: 12px; width: 100%; padding: 10px 12px; border-radius: 8px; border: 1px solid #e5eef8; background: #f8fafc; cursor: pointer; transition: all .2s; text-align: left; }
.ov-link:hover { border-color: #c7d2fe; background: #eef2ff; transform: translateY(-1px); }
.ov-link__icon { width: 36px; height: 36px; border-radius: 8px; background: #fff; display: flex; align-items: center; justify-content: center; flex-shrink: 0; box-shadow: 0 1px 3px rgba(0,0,0,.06); }
.ov-link__icon i { font-size: 18px; color: #6366f1; }
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
