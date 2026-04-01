<template>
  <div class="ov-page">
    <!-- ===== Hero ===== -->
    <section class="ov-hero ov-hero--rose">
      <div class="ov-hero__bg"></div>
      <div class="ov-hero__content">
        <div class="ov-hero__top">
          <div class="ov-hero__badge"><i class="ri-parent-line" /> 家长概览</div>
          <div class="ov-hero__selects">
            <el-select
              v-model="selectedParentId"
              filterable
              clearable
              placeholder="选择家长"
              style="width: 240px;"
              @change="onParentChange"
            >
              <el-option v-for="p in parentOptions" :key="p.value" :label="p.shortLabel || p.label" :value="p.value" />
            </el-select>
            <el-select
              v-model="selectedStudentId"
              filterable
              clearable
              placeholder="选择关联学生"
              style="width: 240px;"
              @change="onStudentChange"
            >
              <el-option v-for="s in studentOptions" :key="s.value" :label="s.shortLabel || s.label" :value="s.value" />
            </el-select>
          </div>
        </div>

        <div class="ov-hero__intro">
          <h1>家校协同总览</h1>
          <p>查看家长用户规模与家校联系覆盖率，选择具体家长和学生可查看关联的学业数据。</p>
        </div>

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

    <!-- ===== Individual Detail Panel ===== -->
    <section v-if="showDetail" class="ov-card ov-card--highlight">
      <div class="ov-card__header">
        <div>
          <h2>
            <i class="ri-parent-line" />
            {{ parentInfo.nickName || '家长' }}
            <template v-if="studentInfo.nickName"> / {{ studentInfo.nickName }}</template>
          </h2>
          <p>
            <template v-if="selectedParentId">家长ID: {{ selectedParentId }}</template>
            <template v-if="selectedParentId && selectedStudentId"> &nbsp;|&nbsp; </template>
            <template v-if="selectedStudentId">学生ID: {{ selectedStudentId }}</template>
          </p>
        </div>
        <el-button size="small" @click="loadDetail" :loading="detailLoading"><i class="ri-refresh-line" /> 刷新</el-button>
      </div>

      <div class="ov-metric-grid ov-metric-grid--4">
        <div v-for="item in detailMetrics" :key="item.title" class="ov-stat-box" :class="item.cls">
          <div class="ov-stat-box__label">{{ item.title }}</div>
          <div class="ov-stat-box__value">{{ item.value }}</div>
          <p class="ov-stat-box__desc">{{ item.desc }}</p>
        </div>
      </div>

      <!-- Student Scores for Parent -->
      <div v-if="studentScores.length" class="ov-section-sub">
        <h3>学生近期成绩</h3>
        <el-table :data="studentScores" stripe size="small" max-height="280">
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

      <!-- Student Exam Records -->
      <div v-if="studentExams.length" class="ov-section-sub">
        <h3>学生近期考试</h3>
        <el-table :data="studentExams" stripe size="small" max-height="280">
          <el-table-column prop="paperName" label="试卷" min-width="180" show-overflow-tooltip />
          <el-table-column prop="score" label="得分" width="80" align="center" />
          <el-table-column prop="correctRate" label="正确率" width="90" align="center">
            <template #default="{ row }">
              {{ row.correctRate != null ? (Number(row.correctRate) * 100).toFixed(0) + '%' : '--' }}
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="考试时间" width="160" />
        </el-table>
      </div>

      <div v-if="!studentScores.length && !studentExams.length && !detailLoading" class="ov-empty">
        <i class="ri-inbox-2-line" />
        <p>暂无学业数据，请确认家长与学生的关联关系</p>
      </div>
    </section>

    <!-- ===== Global View ===== -->
    <section v-else class="ov-body">
      <div class="ov-body__main">
        <section class="ov-card">
          <div class="ov-card__header">
            <div>
              <h2>家校覆盖</h2>
              <p>家长与学生账号的关联概况</p>
            </div>
            <span class="ov-card__tag">Coverage</span>
          </div>
          <div class="ov-progress-list">
            <div class="ov-progress-item">
              <div class="ov-progress-item__head">
                <div>
                  <div class="ov-progress-item__title">家长覆盖率</div>
                  <p class="ov-progress-item__desc">家长账号数占学生数比例（理想值 ≥ 100%）</p>
                </div>
                <div class="ov-progress-item__right">
                  <div class="ov-progress-item__value">{{ coveragePct }}%</div>
                  <div class="ov-progress-item__caption">{{ gm.parentCount }}/{{ gm.studentCount }}</div>
                </div>
              </div>
              <div class="ov-progress-bar">
                <div class="ov-progress-bar__fill" :class="coveragePct >= 80 ? 'is-green' : 'is-amber'" :style="{ width: Math.min(coveragePct, 100) + '%' }" />
              </div>
            </div>
          </div>
          <div class="ov-metric-grid ov-metric-grid--3" style="margin-top: 16px;">
            <div v-for="item in coverageMetrics" :key="item.title" class="ov-stat-box">
              <div class="ov-stat-box__label">{{ item.title }}</div>
              <div class="ov-stat-box__value">{{ item.value }}</div>
              <p class="ov-stat-box__desc">{{ item.desc }}</p>
            </div>
          </div>
        </section>

        <section class="ov-card">
          <div class="ov-card__header">
            <div>
              <h2>学生学业概况</h2>
              <p>与家长关联的学生群体的考试与成绩数据</p>
            </div>
            <span class="ov-card__tag">Academic</span>
          </div>
          <div class="ov-metric-grid ov-metric-grid--3">
            <div v-for="item in academicMetrics" :key="item.title" class="ov-stat-box">
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
            <div><h2>快捷入口</h2><p>家长相关管理模块</p></div>
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
            <i class="ri-lightbulb-line ov-card__icon is-rose" />
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
import { getParentDashboard } from '@/api/campus/overview'
import { listUser } from '@/api/system/user'
import { listStudentScore } from '@/api/campus/score'
import { listRecord } from '@/api/campus/exam'
import { fetchUserOptions } from '@/api/campus/options'

const router = useRouter()

const parentOptions = ref<any[]>([])
const studentOptions = ref<any[]>([])
const selectedParentId = ref<number | undefined>(undefined)
const selectedStudentId = ref<number | undefined>(undefined)
const detailLoading = ref(false)

const parentInfo = reactive<any>({})
const studentInfo = reactive<any>({})
const studentScores = ref<any[]>([])
const studentExams = ref<any[]>([])

const gm = reactive({
  parentCount: 0,
  studentCount: 0,
  scoreCount: 0,
  examRecordCount: 0,
})

const dm = reactive({
  scoreCount: 0,
  examCount: 0,
  avgScore: '--' as string | number,
  highestScore: '--' as string | number,
})

const showDetail = computed(() => !!(selectedParentId.value || selectedStudentId.value))
const coveragePct = computed(() => gm.studentCount ? Math.min(200, Math.round((gm.parentCount / gm.studentCount) * 100)) : 0)

const globalMetrics = computed(() => [
  { title: '家长总数', value: gm.parentCount, desc: '平台家长账号', icon: 'ri-parent-line' },
  { title: '学生总数', value: gm.studentCount, desc: '可关联学生', icon: 'ri-user-3-line' },
  { title: '成绩记录', value: gm.scoreCount, desc: '全部成绩条目', icon: 'ri-bar-chart-2-line' },
  { title: '考试记录', value: gm.examRecordCount, desc: '测评参与数据', icon: 'ri-file-chart-line' },
])

const detailMetrics = computed(() => [
  { title: '成绩记录', value: dm.scoreCount, desc: '学生成绩条目', cls: '' },
  { title: '考试记录', value: dm.examCount, desc: '学生参与测评', cls: '' },
  { title: '平均成绩', value: dm.avgScore, desc: '综合均分', cls: 'is-accent' },
  { title: '最高分', value: dm.highestScore, desc: '历史最佳', cls: 'is-accent' },
])

const coverageMetrics = computed(() => [
  { title: '家长', value: gm.parentCount, desc: '家校协同家长数' },
  { title: '学生', value: gm.studentCount, desc: '在册学生总数' },
  { title: '覆盖率', value: coveragePct.value + '%', desc: '家长/学生比值' },
])

const academicMetrics = computed(() => [
  { title: '成绩记录', value: gm.scoreCount, desc: '全校成绩总量' },
  { title: '考试记录', value: gm.examRecordCount, desc: '全部考试记录' },
])

const quickLinks = [
  { title: '家长管理', desc: '查看家长账号信息', path: '/system/user', icon: 'ri-parent-line' },
  { title: '学生管理', desc: '学生账号与档案', path: '/system/user', icon: 'ri-user-3-line' },
  { title: '成绩查询', desc: '学生成绩查看', path: '/campus/score', icon: 'ri-bar-chart-2-line' },
  { title: '考试记录', desc: '测评数据查看', path: '/campus/exam/record', icon: 'ri-file-chart-line' },
]

const tips = computed(() => [
  `当前共有 ${gm.parentCount} 名家长和 ${gm.studentCount} 名学生。`,
  coveragePct.value >= 100
    ? '家长覆盖率良好，家校联系渠道畅通。'
    : `家长覆盖率 ${coveragePct.value}%，建议鼓励更多家长注册。`,
  `全校已有 ${gm.scoreCount} 条成绩记录和 ${gm.examRecordCount} 条考试记录。`,
])

function navigateTo(path: string) { router.push(path) }

async function loadGlobal() {
  const [parRes, stuRes, scoreRes, examRes] = await Promise.all([
    listUser({ pageNum: 1, pageSize: 1, userType: 'parent' }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'student' }),
    listStudentScore({ pageNum: 1, pageSize: 1 }),
    listRecord({ pageNum: 1, pageSize: 1 }),
  ])
  gm.parentCount = Number(parRes.total || 0)
  gm.studentCount = Number(stuRes.total || 0)
  gm.scoreCount = Number(scoreRes.total || 0)
  gm.examRecordCount = Number(examRes.total || 0)
}

async function loadDetail() {
  detailLoading.value = true
  try {
    // Set parent info
    if (selectedParentId.value) {
      const opt = parentOptions.value.find(o => o.value === selectedParentId.value)
      if (opt) parentInfo.nickName = opt.nickName || opt.shortLabel
    }
    // Set student info
    if (selectedStudentId.value) {
      const opt = studentOptions.value.find(o => o.value === selectedStudentId.value)
      if (opt) studentInfo.nickName = opt.nickName || opt.shortLabel
    }

    // Try parent dashboard API
    if (selectedParentId.value) {
      try {
        const res = await getParentDashboard({
          parentUserId: selectedParentId.value,
          studentUserId: selectedStudentId.value,
        })
        const data = res.data || {}
        Object.assign(parentInfo, data)
      } catch { /* ignore */ }
    }

    // Load student academic data
    const targetUserId = selectedStudentId.value || undefined
    if (targetUserId) {
      try {
        const scoreRes = await listStudentScore({ pageNum: 1, pageSize: 10, studentUserId: targetUserId })
        const rows = scoreRes.rows || []
        studentScores.value = rows
        dm.scoreCount = Number(scoreRes.total || 0)
        if (rows.length) {
          const vals = rows.map((r: any) => Number(r.totalScore || 0)).filter((v: number) => v > 0)
          dm.avgScore = vals.length ? (vals.reduce((a: number, b: number) => a + b, 0) / vals.length).toFixed(1) : '--'
          dm.highestScore = vals.length ? Math.max(...vals) : '--'
        } else {
          dm.avgScore = '--'
          dm.highestScore = '--'
        }
      } catch { dm.scoreCount = 0; studentScores.value = [] }

      try {
        const examRes = await listRecord({ pageNum: 1, pageSize: 10, userId: targetUserId })
        studentExams.value = examRes.rows || []
        dm.examCount = Number(examRes.total || 0)
      } catch { dm.examCount = 0; studentExams.value = [] }
    } else {
      dm.scoreCount = 0
      dm.examCount = 0
      dm.avgScore = '--'
      dm.highestScore = '--'
      studentScores.value = []
      studentExams.value = []
    }
  } finally {
    detailLoading.value = false
  }
}

function onParentChange() { if (selectedParentId.value) loadDetail() }
function onStudentChange() { if (selectedStudentId.value) loadDetail() }

onMounted(async () => {
  parentOptions.value = await fetchUserOptions('parent')
  studentOptions.value = await fetchUserOptions('student')
  await loadGlobal()
})
</script>

<style scoped>
.ov-page { padding: 28px 16px 16px; display: flex; flex-direction: column; gap: 16px; min-height: 100%; background: var(--el-bg-color-page); }

/* ===== Hero ===== */
.ov-hero { position: relative; overflow: hidden; border-radius: 12px; border: 1px solid #e2e8f0; }
.ov-hero--rose { background: linear-gradient(135deg, #fff1f2 0%, #fff 40%, #fdf2f8 100%); }
.ov-hero__bg { position: absolute; inset: 0; background: radial-gradient(circle at top right, rgba(244,63,94,.08), transparent 60%); pointer-events: none; }
.ov-hero__content { position: relative; padding: 20px 24px; }
.ov-hero__top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; flex-wrap: wrap; gap: 12px; }
.ov-hero__badge { display: inline-flex; align-items: center; gap: 6px; padding: 4px 12px; border-radius: 4px; border: 1px solid #fecdd3; background: rgba(255,255,255,.7); font-size: 11px; font-weight: 600; letter-spacing: .18em; text-transform: uppercase; color: #9f1239; }
.ov-hero__selects { display: flex; gap: 10px; flex-wrap: wrap; }
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
.ov-metric-card__icon { font-size: 18px; color: #e11d48; }
.ov-metric-card__value { margin-top: 12px; font-size: 28px; font-weight: 800; color: #0f172a; letter-spacing: -.02em; }
.ov-metric-card__desc { margin: 6px 0 0; font-size: 12px; color: #64748b; line-height: 1.5; }

/* ===== Body Layout ===== */
.ov-body { display: grid; gap: 16px; grid-template-columns: 1fr 340px; align-items: start; }
.ov-body__main { display: flex; flex-direction: column; gap: 16px; }
.ov-body__side { display: flex; flex-direction: column; gap: 16px; }

/* ===== Card ===== */
.ov-card { background: #fff; border-radius: 12px; border: 1px solid #e5eef8; padding: 18px 20px; }
.ov-card--highlight { border-color: #fecdd3; background: linear-gradient(180deg, #fff5f6 0%, #fff 100%); }
.ov-card__header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #f1f5f9; }
.ov-card__header h2 { margin: 0; font-size: 16px; font-weight: 700; color: #0f172a; display: flex; align-items: center; gap: 8px; }
.ov-card__header h2 i { color: #e11d48; }
.ov-card__header p { margin: 4px 0 0; font-size: 13px; color: #94a3b8; }
.ov-card__tag { font-size: 11px; font-weight: 600; letter-spacing: .16em; text-transform: uppercase; color: #94a3b8; white-space: nowrap; }
.ov-card__icon { font-size: 18px; color: #94a3b8; }
.ov-card__icon.is-rose { color: #e11d48; }

/* ===== Stat Box ===== */
.ov-stat-box { padding: 12px 14px; border-radius: 8px; border: 1px solid #e5eef8; background: #f8fafc; }
.ov-stat-box.is-accent { background: #fff1f2; border-color: #fecdd3; }
.ov-stat-box__label { font-size: 11px; font-weight: 600; letter-spacing: .14em; text-transform: uppercase; color: #64748b; }
.ov-stat-box__value { margin-top: 8px; font-size: 24px; font-weight: 800; color: #0f172a; }
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

/* ===== Section Sub ===== */
.ov-section-sub { margin-top: 18px; }
.ov-section-sub h3 { margin: 0 0 10px; font-size: 14px; font-weight: 600; color: #334155; }
.is-good { color: #059669; font-weight: 700; }
.is-warn { color: #dc2626; font-weight: 700; }

/* ===== Empty ===== */
.ov-empty { text-align: center; padding: 40px 0; color: #94a3b8; }
.ov-empty i { font-size: 36px; display: block; margin-bottom: 8px; color: #cbd5e1; }
.ov-empty p { font-size: 14px; margin: 0; }

/* ===== Quick Links ===== */
.ov-links { display: flex; flex-direction: column; gap: 8px; }
.ov-link { display: flex; align-items: flex-start; gap: 12px; width: 100%; padding: 10px 12px; border-radius: 8px; border: 1px solid #e5eef8; background: #f8fafc; cursor: pointer; transition: all .2s; text-align: left; }
.ov-link:hover { border-color: #fecdd3; background: #fff1f2; transform: translateY(-1px); }
.ov-link__icon { width: 36px; height: 36px; border-radius: 8px; background: #fff; display: flex; align-items: center; justify-content: center; flex-shrink: 0; box-shadow: 0 1px 3px rgba(0,0,0,.06); }
.ov-link__icon i { font-size: 18px; color: #e11d48; }
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
  .ov-hero__selects { flex-direction: column; }
}
@media (max-width: 480px) {
  .ov-page { padding: 10px; }
  .ov-metric-grid--4, .ov-metric-grid--3 { grid-template-columns: 1fr; }
}
</style>
