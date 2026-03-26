<template>
  <div class="portal-page score-page">
    <div class="portal-section-title">
      <h3>我的成绩</h3>
      <p>按学期查看已发布课程成绩，表格化展示更适合核对、对比与打印。</p>
    </div>

    <section class="portal-card score-summary-card">
      <div class="score-summary-card__main">
        <div class="score-summary-card__intro">
          <span class="score-summary-card__badge">成绩中心</span>
          <h4>{{ currentTermLabel }}</h4>
          <p>这里集中展示当前学期已发布成绩。你可以先按学期或课程筛选，再查看课程总评、绩点、排名和各项组成。</p>
        </div>

        <div class="score-summary-card__metrics">
          <div class="score-summary-metric">
            <span>平均总评</span>
            <strong>{{ formatNumeric(overview.avgScore, '0') }}</strong>
            <small>当前筛选范围内课程均值</small>
          </div>
          <div class="score-summary-metric">
            <span>已通过</span>
            <strong>{{ overview.passCount || 0 }}</strong>
            <small>{{ passRateText }}</small>
          </div>
          <div class="score-summary-metric">
            <span>修读课程</span>
            <strong>{{ overview.courseCount || 0 }}</strong>
            <small>仅统计已发布成绩课程</small>
          </div>
          <div class="score-summary-metric">
            <span>获得学分</span>
            <strong>{{ formatNumeric(overview.creditEarned, '0') }}</strong>
            <small>通过课程累计学分</small>
          </div>
        </div>
      </div>

      <div class="score-summary-card__toolbar">
        <div class="score-toolbar-controls">
          <el-select
            v-model="queryParams.termId"
            filterable
            clearable
            placeholder="选择学期"
            style="width: 260px"
            @change="loadScores"
          >
            <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>

          <el-select
            v-model="queryParams.courseId"
            filterable
            clearable
            placeholder="筛选课程"
            style="width: 260px"
            @change="loadScores"
          >
            <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>

          <el-button @click="resetFilters">重置筛选</el-button>
        </div>

        <div class="score-toolbar-chips">
          <div class="score-toolbar-chip"><i class="ri-award-line"></i> 最佳成绩 {{ formatNumeric(bestScore, '0') }}</div>
          <div class="score-toolbar-chip"><i class="ri-arrow-down-circle-line"></i> 最低成绩 {{ formatNumeric(lowestScore, '0') }}</div>
        </div>
      </div>
    </section>

    <section class="score-insight-grid" v-if="hasScores">
      <div class="portal-card score-insight-card">
        <div class="score-insight-card__head">
          <strong>等级分布</strong>
          <span>当前学期成绩结构</span>
        </div>
        <div class="score-insight-list">
          <div v-for="item in gradeDistributionItems" :key="item.gradeLevel" class="score-insight-row">
            <span>{{ item.gradeLevel }}</span>
            <div class="score-insight-bar">
              <i :style="{ width: `${item.ratio}%` }"></i>
            </div>
            <strong>{{ item.count }}</strong>
          </div>
        </div>
      </div>

      <div class="portal-card score-insight-card">
        <div class="score-insight-card__head">
          <strong>分数区间</strong>
          <span>按课程总评分段统计</span>
        </div>
        <div class="score-insight-list">
          <div v-for="item in scoreBandItems" :key="item.label" class="score-insight-row">
            <span>{{ item.label }}</span>
            <div class="score-insight-bar score-insight-bar--soft">
              <i :style="{ width: `${item.ratio}%` }"></i>
            </div>
            <strong>{{ item.count }}</strong>
          </div>
        </div>
      </div>
    </section>

    <section class="portal-card score-table-card">
      <div class="score-table-card__head">
        <div>
          <strong>学期成绩单</strong>
          <span>当前仅显示已发布成绩，可点击查看课程明细</span>
        </div>
        <div class="score-table-card__meta">
          <span>{{ sortedScores.length }} 门课程</span>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="sortedScores"
        class="score-table"
        stripe
        @row-click="openDetail"
      >
        <template #empty>
          <el-empty description="当前条件下暂无已发布成绩" />
        </template>

        <el-table-column label="课程名称" min-width="280">
          <template #default="{ row }">
            <div class="score-course-cell">
              <strong>{{ row.courseName || '未命名课程' }}</strong>
              <span>{{ row.termName || '-' }} / {{ row.className || '-' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="学分" width="90" align="center">
          <template #default="{ row }">{{ formatNumeric(row.credits) }}</template>
        </el-table-column>

        <el-table-column label="绩点" width="90" align="center">
          <template #default="{ row }">{{ formatNumeric(row.gradePoint, '0') }}</template>
        </el-table-column>

        <el-table-column label="成绩" width="126" align="center">
          <template #default="{ row }">
            <div class="score-total-cell" :class="scoreToneClass(row.totalScore)">
              <strong>{{ formatNumeric(row.totalScore, '0') }}</strong>
              <span>{{ row.gradeLevel || '-' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="成绩明细" min-width="360">
          <template #default="{ row }">
            <div class="score-detail-cell">
              <span>平时 {{ formatNumeric(row.usualScore, '0') }}</span>
              <span>考勤 {{ formatNumeric(row.attendanceScore, '0') }}</span>
              <span>作业 {{ formatNumeric(row.homeworkScore, '0') }}</span>
              <span>实验 {{ formatNumeric(row.labScore, '0') }}</span>
              <span>考试 {{ formatNumeric(row.examScore, '0') }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="排名" width="92" align="center">
          <template #default="{ row }">
            <div class="score-rank-cell">
              <strong>{{ row.rankNo || '-' }}</strong>
              <span v-if="row.percentile != null">百分位 {{ formatNumeric(row.percentile, '0') }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.passFlag === '1' ? 'success' : 'danger'" effect="plain">
              {{ row.passFlag === '1' ? '已通过' : '待提升' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="发布时间" width="176" align="center">
          <template #default="{ row }">{{ formatDateTime(row.publishTime) || '-' }}</template>
        </el-table-column>

        <el-table-column label="操作" width="110" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click.stop="openDetail(row)">查看明细</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog
      v-model="detailOpen"
      :title="detailData.score?.courseName || '成绩详情'"
      width="1000px"
      top="5vh"
      append-to-body
      class="score-detail-dialog"
    >
      <div class="score-detail-shell" v-if="detailData.score">
        <section class="score-detail-banner">
          <div class="score-detail-banner__main">
            <span class="score-detail-banner__badge">{{ detailData.score.termName || '成绩详情' }}</span>
            <h4>{{ detailData.score.courseName || '-' }}</h4>
            <p>{{ detailData.course?.assessmentType || detailData.score.assessmentType || '课程总评' }}，这里可以查看总评组成、课程信息和考试来源。</p>
          </div>

          <div class="score-detail-banner__stats">
            <div>
              <span>总评</span>
              <strong>{{ formatNumeric(detailData.score.totalScore, '0') }}</strong>
            </div>
            <div>
              <span>等级</span>
              <strong>{{ detailData.score.gradeLevel || '-' }}</strong>
            </div>
            <div>
              <span>绩点</span>
              <strong>{{ formatNumeric(detailData.score.gradePoint, '0') }}</strong>
            </div>
            <div>
              <span>排名</span>
              <strong>{{ detailData.score.rankNo || '-' }}</strong>
            </div>
          </div>
        </section>

        <div class="score-detail-grid">
          <div class="portal-card score-detail-panel">
            <div class="score-detail-panel__title">成绩构成</div>
            <el-table :data="detailBreakdownRows" size="small" class="score-breakdown-table">
              <el-table-column prop="label" label="项目" min-width="120" />
              <el-table-column prop="score" label="分数" width="100" align="center" />
              <el-table-column prop="weight" label="权重" width="100" align="center" />
              <el-table-column prop="note" label="说明" min-width="140" show-overflow-tooltip />
            </el-table>

            <div class="score-comment" v-if="detailData.score.teacherComment">
              <div class="score-detail-panel__title">教师评语</div>
              <p>{{ detailData.score.teacherComment }}</p>
            </div>
          </div>

          <div class="portal-card score-detail-panel">
            <div class="score-detail-panel__title">课程信息</div>
            <el-descriptions :column="1" border size="small" class="score-course-descriptions">
              <el-descriptions-item label="学期">{{ detailData.score.termName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="班级">{{ detailData.score.className || '-' }}</el-descriptions-item>
              <el-descriptions-item label="学分">{{ formatNumeric(detailData.score.credits) }}</el-descriptions-item>
              <el-descriptions-item label="考核方式">{{ detailData.course?.assessmentType || detailData.score.assessmentType || '-' }}</el-descriptions-item>
              <el-descriptions-item label="发布时间">{{ formatDateTime(detailData.score.publishTime) || '-' }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="detailData.score.passFlag === '1' ? 'success' : 'danger'" effect="plain">
                  {{ detailData.score.passFlag === '1' ? '已通过' : '待提升' }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>

            <div class="score-exam-summary">
              <div class="score-exam-summary__item">
                <span>考试次数</span>
                <strong>{{ detailData.examSummary?.recordCount || 0 }}</strong>
              </div>
              <div class="score-exam-summary__item">
                <span>已提交</span>
                <strong>{{ detailData.examSummary?.submittedCount || 0 }}</strong>
              </div>
              <div class="score-exam-summary__item">
                <span>平均分</span>
                <strong>{{ formatNumeric(detailData.examSummary?.avgScore, '0') }}</strong>
              </div>
              <div class="score-exam-summary__item">
                <span>最高分</span>
                <strong>{{ formatNumeric(detailData.examSummary?.bestScore, '0') }}</strong>
              </div>
            </div>
          </div>
        </div>

        <div class="portal-card score-detail-panel">
          <div class="score-detail-panel__title">考试来源</div>
          <el-table :data="detailData.examRecords || []" max-height="360" class="score-exam-table">
            <template #empty>
              <el-empty description="当前课程暂无关联考试记录" />
            </template>
            <el-table-column prop="paperName" label="考试名称" min-width="220" show-overflow-tooltip />
            <el-table-column prop="score" label="得分" width="100" align="center" />
            <el-table-column prop="correctRate" label="正确率" width="100" align="center" />
            <el-table-column prop="examStatus" label="状态" width="110" align="center" />
            <el-table-column prop="submitTime" label="提交时间" min-width="180" />
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import usePortalUserStore from '@/store/user'
import { fetchPortalCourseOptions, getPortalScoreDetail, getPortalScoreOverview, listPortalScore, listPortalTermOptions } from '@/api/portal'

const userStore = usePortalUserStore()
const loading = ref(false)
const termOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const scoreList = ref<any[]>([])
const overview = ref<any>({})
const detailOpen = ref(false)
const detailData = ref<any>({})
const queryParams = ref<{ termId?: number; courseId?: number }>({})

const currentTermLabel = computed(() => {
  return termOptions.value.find((item: any) => item.value === queryParams.value.termId)?.label || '全部已发布成绩'
})

const sortedScores = computed(() => {
  return [...scoreList.value].sort((a: any, b: any) => Number(b.totalScore || 0) - Number(a.totalScore || 0))
})

const hasScores = computed(() => sortedScores.value.length > 0)

const bestScore = computed(() => {
  return sortedScores.value.length ? Number(sortedScores.value[0]?.totalScore || 0) : 0
})

const lowestScore = computed(() => {
  return sortedScores.value.length ? Number(sortedScores.value[sortedScores.value.length - 1]?.totalScore || 0) : 0
})

const passRateText = computed(() => {
  const total = Number(overview.value.courseCount || 0)
  const passed = Number(overview.value.passCount || 0)
  if (!total) return '暂无可统计课程'
  return `通过率 ${((passed / total) * 100).toFixed(0)}%`
})

const gradeDistributionItems = computed(() => {
  const items = (overview.value.gradeDistribution || []).filter((item: any) => Number(item.count || 0) > 0)
  const max = Math.max(...items.map((item: any) => Number(item.count || 0)), 1)
  return items.map((item: any) => ({
    ...item,
    ratio: (Number(item.count || 0) / max) * 100,
  }))
})

const scoreBandItems = computed(() => {
  const scores = sortedScores.value.map((item: any) => Number(item.totalScore || 0))
  const bands = [
    { label: '90-100', count: scores.filter((score) => score >= 90).length },
    { label: '80-89', count: scores.filter((score) => score >= 80 && score < 90).length },
    { label: '70-79', count: scores.filter((score) => score >= 70 && score < 80).length },
    { label: '60-69', count: scores.filter((score) => score >= 60 && score < 70).length },
    { label: '<60', count: scores.filter((score) => score < 60).length },
  ]
  const max = Math.max(...bands.map((item) => item.count), 1)
  return bands.map((item) => ({
    ...item,
    ratio: (item.count / max) * 100,
  }))
})

const detailBreakdownRows = computed(() => {
  const score = detailData.value.score || {}
  return [
    { label: '平时分', score: formatNumeric(score.usualScore, '0'), weight: `${formatNumeric(score.usualWeight, '0')}%`, note: '课堂表现 / 过程性评价' },
    { label: '考勤分', score: formatNumeric(score.attendanceScore, '0'), weight: `${formatNumeric(score.attendanceWeight, '0')}%`, note: '到课与参与情况' },
    { label: '作业分', score: formatNumeric(score.homeworkScore, '0'), weight: `${formatNumeric(score.homeworkWeight, '0')}%`, note: '作业与阶段练习' },
    { label: '实验分', score: formatNumeric(score.labScore, '0'), weight: `${formatNumeric(score.labWeight, '0')}%`, note: '实验 / 实训表现' },
    { label: '考试分', score: formatNumeric(score.examScore, '0'), weight: `${formatNumeric(score.examWeight, '0')}%`, note: `同步均分 ${formatNumeric(score.examAvgScore, '0')}` },
    { label: '附加项', score: `+${formatNumeric(score.bonusScore, '0')} / -${formatNumeric(score.penaltyScore, '0')}`, weight: '-', note: '教师手动加减分' },
  ]
})

function formatNumeric(value: any, fallback = '-') {
  if (value == null || value === '') return fallback
  const num = Number(value)
  if (Number.isNaN(num)) return String(value)
  return Number.isInteger(num) ? String(num) : num.toFixed(2)
}

function formatDateTime(value?: string) {
  return value || ''
}

function scoreToneClass(value: any) {
  const score = Number(value || 0)
  if (score >= 90) return 'is-excellent'
  if (score < 60) return 'is-risk'
  return 'is-normal'
}

async function loadScores() {
  const userId = userStore.user?.userId
  if (!userId) return
  loading.value = true
  try {
    const [scoreRes, overviewRes] = await Promise.all([
      listPortalScore({ userId, ...queryParams.value }),
      getPortalScoreOverview({ userId, termId: queryParams.value.termId }),
    ])
    scoreList.value = scoreRes.data || []
    overview.value = overviewRes.data || {}
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  queryParams.value.courseId = undefined
  loadScores()
}

async function openDetail(row: any) {
  const userId = userStore.user?.userId
  if (!userId) return
  const res = await getPortalScoreDetail({ userId, classCourseId: row.classCourseId })
  detailData.value = res.data || {}
  detailOpen.value = true
}

async function loadOptions() {
  const userId = userStore.user?.userId
  if (!userId) return
  const [termRes, courseRes] = await Promise.all([
    listPortalTermOptions(),
    fetchPortalCourseOptions(userId),
  ])
  termOptions.value = termRes.data || []
  courseOptions.value = courseRes || []
  const current = termOptions.value.find((item: any) => item.isCurrent === '1') || termOptions.value[0]
  if (current) {
    queryParams.value.termId = current.value
  }
}

onMounted(async () => {
  await loadOptions()
  await loadScores()
})
</script>

<style scoped>
.score-page {
  gap: 14px;
  padding-bottom: 20px;
}

.score-summary-card {
  padding: 18px;
}

.score-summary-card__main {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(420px, 0.95fr);
  gap: 16px;
  align-items: stretch;
}

.score-summary-card__intro {
  padding: 18px;
  border-radius: 12px;
  background: linear-gradient(180deg, #fbfdff 0%, #f4f8ff 100%);
  border: 1px solid var(--portal-border);
}

.score-summary-card__badge {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 12px;
  border-radius: 999px;
  background: var(--portal-brand-light);
  color: var(--portal-brand);
  font-size: 12px;
  font-weight: 700;
}

.score-summary-card__intro h4 {
  margin: 14px 0 10px;
  color: var(--portal-text);
  font-size: 20px;
  line-height: 1.35;
}

.score-summary-card__intro p {
  margin: 0;
  color: var(--portal-text-secondary);
  font-size: 13px;
  line-height: 1.85;
}

.score-summary-card__metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.score-summary-metric {
  padding: 16px 18px;
  border-radius: 12px;
  background: var(--portal-card-solid);
  border: 1px solid var(--portal-border);
}

.score-summary-metric span,
.score-summary-metric small {
  display: block;
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.score-summary-metric strong {
  display: block;
  margin-top: 8px;
  color: var(--portal-text);
  font-size: 26px;
  line-height: 1;
}

.score-summary-metric small {
  margin-top: 8px;
}

.score-summary-card__toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid var(--portal-border);
}

.score-toolbar-controls,
.score-toolbar-chips {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.score-toolbar-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  background: var(--portal-surface-bg);
  border: 1px solid var(--portal-border);
  color: var(--portal-text-secondary);
  font-size: 12px;
  font-weight: 600;
}

.score-insight-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.score-insight-card {
  padding: 16px 18px;
}

.score-insight-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.score-insight-card__head strong {
  color: var(--portal-text);
  font-size: 15px;
}

.score-insight-list {
  display: grid;
  gap: 10px;
}

.score-insight-row {
  display: grid;
  grid-template-columns: 60px minmax(0, 1fr) 28px;
  align-items: center;
  gap: 10px;
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.score-insight-row strong {
  color: var(--portal-text);
  text-align: right;
  font-size: 13px;
}

.score-insight-bar {
  height: 10px;
  border-radius: 999px;
  background: #edf3fb;
  overflow: hidden;
}

.score-insight-bar i {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, var(--portal-brand) 0%, var(--portal-brand-2) 100%);
}

.score-insight-bar--soft i {
  background: linear-gradient(90deg, #6f8fdb 0%, #93b5ff 100%);
}

.score-table-card {
  padding: 14px 16px 10px;
}

.score-table-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.score-table-card__head strong {
  display: block;
  color: var(--portal-text);
  font-size: 16px;
}

.score-table-card__head span,
.score-table-card__meta span {
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.score-course-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.score-course-cell strong {
  color: var(--portal-text);
  font-size: 14px;
  font-weight: 700;
}

.score-course-cell span {
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.score-total-cell {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  min-width: 78px;
  padding: 10px 8px;
  border-radius: 10px;
  border: 1px solid var(--portal-border);
  background: var(--portal-surface-bg);
}

.score-total-cell strong {
  color: var(--portal-text);
  font-size: 20px;
  line-height: 1;
}

.score-total-cell span {
  color: var(--portal-text-secondary);
  font-size: 11px;
  font-weight: 700;
}

.score-total-cell.is-excellent {
  background: #f0fbf5;
  border-color: #bde1ca;
}

.score-total-cell.is-excellent strong,
.score-total-cell.is-excellent span {
  color: #16794d;
}

.score-total-cell.is-risk {
  background: #fff5f3;
  border-color: #efc2ba;
}

.score-total-cell.is-risk strong,
.score-total-cell.is-risk span {
  color: #bf4b3b;
}

.score-detail-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.score-detail-cell span {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: var(--portal-surface-bg);
  color: var(--portal-text-secondary);
  font-size: 12px;
  font-weight: 600;
}

.score-rank-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.score-rank-cell strong {
  color: var(--portal-text);
  font-size: 16px;
}

.score-rank-cell span {
  color: var(--portal-text-secondary);
  font-size: 11px;
}

.score-table :deep(.el-table__row) {
  cursor: pointer;
}

.score-table :deep(.el-table__cell) {
  vertical-align: middle;
}

.score-detail-dialog :deep(.el-dialog__body) {
  padding-top: 12px;
}

.score-detail-shell {
  display: grid;
  gap: 14px;
}

.score-detail-banner {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(320px, 0.95fr);
  gap: 16px;
  padding: 18px;
  border-radius: 14px;
  background: linear-gradient(180deg, #fbfdff 0%, #f4f8ff 100%);
  border: 1px solid var(--portal-border);
}

.score-detail-banner__badge {
  display: inline-flex;
  align-items: center;
  height: 26px;
  padding: 0 10px;
  border-radius: 999px;
  background: var(--portal-brand-light);
  color: var(--portal-brand);
  font-size: 12px;
  font-weight: 700;
}

.score-detail-banner__main h4 {
  margin: 12px 0 8px;
  color: var(--portal-text);
  font-size: 22px;
}

.score-detail-banner__main p {
  margin: 0;
  color: var(--portal-text-secondary);
  font-size: 13px;
  line-height: 1.8;
}

.score-detail-banner__stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.score-detail-banner__stats div {
  padding: 14px 16px;
  border-radius: 12px;
  background: var(--portal-card-solid);
  border: 1px solid var(--portal-border);
}

.score-detail-banner__stats span {
  display: block;
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.score-detail-banner__stats strong {
  display: block;
  margin-top: 8px;
  color: var(--portal-text);
  font-size: 22px;
}

.score-detail-grid {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 14px;
}

.score-detail-panel {
  padding: 16px;
}

.score-detail-panel__title {
  margin-bottom: 12px;
  color: var(--portal-text);
  font-size: 15px;
  font-weight: 700;
}

.score-comment {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid var(--portal-border);
}

.score-comment p {
  margin: 0;
  color: var(--portal-text-secondary);
  font-size: 13px;
  line-height: 1.8;
}

.score-course-descriptions {
  margin-bottom: 14px;
}

.score-exam-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.score-exam-summary__item {
  padding: 14px 16px;
  border-radius: 12px;
  background: var(--portal-surface-bg);
  border: 1px solid var(--portal-border);
}

.score-exam-summary__item span {
  display: block;
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.score-exam-summary__item strong {
  display: block;
  margin-top: 8px;
  color: var(--portal-text);
  font-size: 20px;
}

@media (max-width: 1200px) {
  .score-summary-card__main,
  .score-insight-grid,
  .score-detail-banner,
  .score-detail-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .score-summary-card,
  .score-table-card,
  .score-detail-panel {
    padding: 14px;
  }

  .score-summary-card__toolbar,
  .score-toolbar-controls,
  .score-toolbar-chips {
    align-items: stretch;
  }

  .score-toolbar-controls :deep(.el-select),
  .score-toolbar-controls :deep(.el-button) {
    width: 100% !important;
  }

  .score-detail-banner__stats,
  .score-summary-card__metrics,
  .score-exam-summary {
    grid-template-columns: 1fr;
  }
}
</style>
