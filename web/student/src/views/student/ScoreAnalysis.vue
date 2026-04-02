<template>
  <div class="portal-page score-analysis-page">
    <header class="score-analysis-topbar">
      <div class="score-analysis-topbar__left">
        <el-button class="back-btn" text bg size="small" @click="goBack">
          <i class="ri-arrow-left-line" />
        </el-button>
        <div class="score-analysis-topbar__title">
          <h1>成绩分析</h1>
          <p>把图表趋势、结构分布和 AI 洞察单独放到这个页面，方便连续复盘，不挤占成绩单主视图。</p>
        </div>
      </div>
      <div class="score-analysis-topbar__tag">{{ analysisScopeLabel }}</div>
    </header>

    <section class="portal-card score-analysis-summary">
      <div class="score-analysis-summary__intro">
        <span class="score-analysis-summary__badge">图表分析</span>
        <h4>{{ currentTermLabel }}</h4>
        <p>按学期和课程查看趋势、构成与等级结构，适合做阶段性复盘和后续提升规划。</p>
      </div>

      <div class="score-analysis-summary__metrics">
        <div class="score-metric-card">
          <span>平均总评</span>
          <strong>{{ formatNumeric(currentOverview.avgScore, '0') }}</strong>
          <small>当前分析范围内课程均值</small>
        </div>
        <div class="score-metric-card">
          <span>已通过</span>
          <strong>{{ currentOverview.passCount || 0 }}</strong>
          <small>{{ passRateText }}</small>
        </div>
        <div class="score-metric-card">
          <span>修读课程</span>
          <strong>{{ currentOverview.courseCount || 0 }}</strong>
          <small>仅统计已发布成绩课程</small>
        </div>
        <div class="score-metric-card">
          <span>获得学分</span>
          <strong>{{ formatNumeric(currentOverview.creditEarned, '0') }}</strong>
          <small>通过课程累计学分</small>
        </div>
      </div>

      <div class="score-analysis-toolbar">
        <div class="score-analysis-toolbar__controls">
          <el-select v-model="queryParams.termId" filterable clearable placeholder="选择学期" style="width: 240px" @change="loadAnalysis">
            <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-select v-model="queryParams.courseId" filterable clearable placeholder="筛选课程" style="width: 240px" @change="loadAnalysis">
            <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-button @click="resetFilters">重置筛选</el-button>
          <el-button type="primary" plain @click="goBack">返回成绩单</el-button>
        </div>
        <div class="score-analysis-toolbar__chips">
          <div class="score-toolbar-chip"><i class="ri-award-line"></i> 最佳成绩 {{ formatNumeric(bestScore, '0') }}</div>
          <div class="score-toolbar-chip"><i class="ri-arrow-down-circle-line"></i> 最低成绩 {{ formatNumeric(lowestScore, '0') }}</div>
        </div>
      </div>
    </section>

    <div class="score-section-header" v-if="hasAnalytics">
      <div class="score-section-header__title">
        <span>图表看板</span>
        <strong>多维数据复盘</strong>
      </div>
      <p>悬停图表可查看更细的数据说明、比较关系和关键节点。</p>
    </div>

    <section class="score-chart-grid" v-if="hasAnalytics">
      <div class="portal-card score-chart-card score-chart-card--span-2">
        <div class="score-chart-card__head">
          <div class="score-chart-card__title-block">
            <div class="icon-wrapper"><i class="ri-line-chart-line"></i></div>
            <div>
              <strong>成绩趋势</strong>
              <span>{{ termTrendCaption }}</span>
            </div>
          </div>
          <div class="score-chart-card__meta">{{ analysisScopeLabel }}</div>
        </div>
        <ScoreAnalysisChart :option="termTrendOption" :loading="loading" height="320" empty-text="暂无跨学期趋势数据" />
      </div>

      <div class="portal-card score-chart-card">
        <div class="score-chart-card__head">
          <div class="score-chart-card__title-block">
            <div class="icon-wrapper"><i class="ri-bar-chart-box-line"></i></div>
            <div>
              <strong>课程成绩对比</strong>
              <span>当前筛选范围内课程总评横向比较</span>
            </div>
          </div>
          <div class="score-chart-card__meta">Top {{ Math.min((scoreAnalysis.courseSeries || []).length, 10) }}</div>
        </div>
        <ScoreAnalysisChart :option="courseComparisonOption" :loading="loading" height="320" empty-text="暂无课程成绩数据" />
      </div>

      <div class="portal-card score-chart-card">
        <div class="score-chart-card__head">
          <div class="score-chart-card__title-block">
            <div class="icon-wrapper"><i class="ri-radar-line"></i></div>
            <div>
              <strong>成绩构成雷达</strong>
              <span>基于当前筛选成绩的各项均分表现</span>
            </div>
          </div>
          <div class="score-chart-card__meta">过程 + 考试</div>
        </div>
        <ScoreAnalysisChart :option="componentRadarOption" :loading="loading" height="320" empty-text="暂无成绩构成数据" />
        <div class="score-component-legend" v-if="componentLegendItems.length">
          <div v-for="item in componentLegendItems" :key="item.label" class="score-component-chip">
            <span>{{ item.label }}</span>
            <strong>{{ item.avgScoreText }}</strong>
            <small>权重均值 {{ item.avgWeightText }}%</small>
          </div>
        </div>
      </div>

      <div class="portal-card score-chart-card score-chart-card--span-2 score-chart-card--structure">
        <div class="score-chart-card__head">
          <div class="score-chart-card__title-block">
            <div class="icon-wrapper"><i class="ri-donut-chart-line"></i></div>
            <div>
              <strong>成绩结构</strong>
              <span>查看当前成绩在分数段与等级上的分布</span>
            </div>
          </div>
          <div class="score-chart-card__meta">结构分析</div>
        </div>
        <div class="score-structure-grid">
          <div class="score-structure-panel">
            <strong>分数段分布</strong>
            <ScoreAnalysisChart :option="scoreBandChartOption" :loading="loading" height="220" empty-text="暂无分数段数据" />
          </div>
          <div class="score-structure-panel">
            <strong>等级分布</strong>
            <ScoreAnalysisChart :option="gradeDistributionChartOption" :loading="loading" height="220" empty-text="暂无等级分布数据" />
          </div>
        </div>
      </div>
    </section>

    <section v-else class="portal-card score-analysis-empty">
      <el-empty description="当前筛选范围暂无可分析成绩" />
    </section>

    <div class="score-section-header" v-if="analysisInsights.length">
      <div class="score-section-header__title">
        <span>分析建议</span>
        <strong>系统洞察与 AI 追问</strong>
      </div>
      <p>先看摘要，再决定是否把当前上下文继续交给 AI 深挖原因与计划。</p>
    </div>

    <section class="portal-card score-ai-card" v-if="analysisInsights.length">
      <div class="score-ai-card__main">
        <div class="score-ai-card__head">
          <strong>结构化洞察</strong>
          <span>基于当前筛选范围生成的关键数据特征提取。</span>
        </div>
        <div class="score-ai-card__insights">
          <article v-for="(item, index) in analysisInsights" :key="`${item.title}-${index}`" class="score-ai-item" :class="`score-ai-item--${item.tone || 'info'}`">
            <div class="ai-item-indicator"></div>
            <div class="ai-item-content">
              <strong>{{ item.title }}</strong>
              <p>{{ item.content }}</p>
            </div>
          </article>
        </div>
      </div>
      <div class="score-ai-card__actions">
        <div class="score-ai-card__badge">AI 联动</div>
        <h4>深入成绩诊断</h4>
        <p>自动预置当前筛选范围的分析背景，向智能助手追问原因、策略和复习安排。</p>
        <el-button type="primary" size="large" @click="handleAskAiAnalysis">去 AI 深入分析</el-button>
      </div>
    </section>

    <div class="score-section-header" v-if="hasScores">
      <div class="score-section-header__title">
        <span>结构概览</span>
        <strong>分数段密度</strong>
      </div>
      <p>整体分布判断，和图表看板形成有效补充。</p>
    </div>

    <section class="score-insight-grid" v-if="hasScores">
      <div class="portal-card score-insight-card">
        <div class="score-insight-card__head">
          <strong>等级分布概览</strong>
          <span>当前学期成绩结构</span>
        </div>
        <div class="score-insight-list">
          <div v-for="item in gradeDistributionItems" :key="item.gradeLevel" class="score-insight-row">
            <span class="row-label">{{ item.gradeLevel }}</span>
            <div class="score-insight-bar"><i :style="{ width: `${item.ratio}%` }"></i></div>
            <strong class="row-value">{{ item.count }}</strong>
          </div>
        </div>
      </div>
      <div class="portal-card score-insight-card">
        <div class="score-insight-card__head">
          <strong>分数区间分布</strong>
          <span>按课程总评分段统计</span>
        </div>
        <div class="score-insight-list">
          <div v-for="item in scoreBandItems" :key="item.label" class="score-insight-row">
            <span class="row-label">{{ item.label }}</span>
            <div class="score-insight-bar score-insight-bar--soft"><i :style="{ width: `${item.ratio}%` }"></i></div>
            <strong class="row-value">{{ item.count }}</strong>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
// Script 部分保持不变，此处省略以聚焦样式调整...
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import ScoreAnalysisChart from '@/components/score/ScoreAnalysisChart.vue'
import usePortalUserStore from '@/store/user'
import { fetchPortalCourseOptions, getPortalScoreAnalysis, listPortalScore, listPortalTermOptions } from '@/api/portal'
import { saveScoreAnalysisQaPreset } from '@/utils/qaPreset'
import {
  buildComponentRadarOption,
  buildCourseComparisonOption,
  buildGradeDistributionChartOption,
  buildScoreBandChartOption,
  buildTermTrendOption,
  formatScoreNumber,
} from '@/utils/scoreAnalysis'

const route = useRoute()
const router = useRouter()
const userStore = usePortalUserStore()
const loading = ref(false)
const termOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const scoreList = ref<any[]>([])
const scoreAnalysis = ref<any>({})
const queryParams = ref<{ termId?: number; courseId?: number }>({})
const defaultTermId = ref<number | undefined>()

const currentTermLabel = computed(() => termOptions.value.find((item: any) => item.value === queryParams.value.termId)?.label || '全部已发布成绩')
const sortedScores = computed(() => [...scoreList.value].sort((a: any, b: any) => Number(b.totalScore || 0) - Number(a.totalScore || 0)))
const hasScores = computed(() => sortedScores.value.length > 0)
const analysisInsights = computed(() => scoreAnalysis.value.insights || [])
const componentSeries = computed(() => scoreAnalysis.value.componentSeries || [])
const analysisScopeLabel = computed(() => scoreAnalysis.value.scope?.scopeLabel || currentTermLabel.value)
const hasAnalytics = computed(() => {
  const data = scoreAnalysis.value || {}
  return analysisInsights.value.length > 0
    || (data.termTrend || []).length > 0
    || (data.courseSeries || []).length > 0
    || (data.componentSeries || []).length > 0
    || (data.scoreBands || []).length > 0
    || (data.gradeDistribution || []).length > 0
})
const currentOverview = computed(() => {
  const courseCount = sortedScores.value.length
  const passCount = sortedScores.value.filter((item: any) => item.passFlag === '1').length
  const avgScore = courseCount ? sortedScores.value.reduce((total: number, item: any) => total + Number(item.totalScore || 0), 0) / courseCount : 0
  const creditEarned = sortedScores.value.filter((item: any) => item.passFlag === '1').reduce((total: number, item: any) => total + Number(item.credits || 0), 0)
  return { avgScore, passCount, courseCount, creditEarned }
})
const bestScore = computed(() => sortedScores.value.length ? Number(sortedScores.value[0]?.totalScore || 0) : 0)
const lowestScore = computed(() => sortedScores.value.length ? Number(sortedScores.value[sortedScores.value.length - 1]?.totalScore || 0) : 0)
const passRateText = computed(() => {
  const total = Number(currentOverview.value.courseCount || 0)
  const passed = Number(currentOverview.value.passCount || 0)
  if (!total) return '暂无可统计课程'
  return `通过率 ${((passed / total) * 100).toFixed(0)}%`
})
const gradeDistributionItems = computed(() => {
  const items = (scoreAnalysis.value.gradeDistribution || []).filter((item: any) => Number(item.count || 0) > 0)
  const max = Math.max(...items.map((item: any) => Number(item.count || 0)), 1)
  return items.map((item: any) => ({ ...item, ratio: (Number(item.count || 0) / max) * 100 }))
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
  return bands.map((item) => ({ ...item, ratio: (item.count / max) * 100 }))
})
const termTrendCaption = computed(() => queryParams.value.courseId ? '当前课程跨学期平均分变化' : '近学期平均分变化')
const componentLegendItems = computed(() => componentSeries.value
  .filter((item: any) => Number(item.avgWeight || 0) > 0)
  .map((item: any) => ({
    ...item,
    avgScoreText: formatNumeric(item.avgScore, '0'),
    avgWeightText: formatNumeric(item.avgWeight, '0'),
  })))
const termTrendOption = computed(() => buildTermTrendOption(scoreAnalysis.value.termTrend || []))
const courseComparisonOption = computed(() => buildCourseComparisonOption(scoreAnalysis.value.courseSeries || [], !queryParams.value.termId))
const componentRadarOption = computed(() => buildComponentRadarOption(componentSeries.value))
const scoreBandChartOption = computed(() => buildScoreBandChartOption(scoreAnalysis.value.scoreBands || []))
const gradeDistributionChartOption = computed(() => buildGradeDistributionChartOption(scoreAnalysis.value.gradeDistribution || []))

function formatNumeric(value: any, fallback = '-') {
  return formatScoreNumber(value, fallback)
}

function buildRouteQuery() {
  return {
    ...(queryParams.value.termId ? { termId: String(queryParams.value.termId) } : {}),
    ...(queryParams.value.courseId ? { courseId: String(queryParams.value.courseId) } : {}),
  }
}

function syncRouteQuery() {
  router.replace({ path: '/student/scores/analysis', query: buildRouteQuery() })
}

function applyRouteQuery() {
  const queryTermId = Number(route.query.termId || 0)
  const queryCourseId = Number(route.query.courseId || 0)
  if (queryTermId) queryParams.value.termId = queryTermId
  if (queryCourseId) queryParams.value.courseId = queryCourseId
}

async function loadAnalysis() {
  const userId = userStore.user?.userId
  if (!userId) return
  syncRouteQuery()
  loading.value = true
  try {
    const [scoreRes, analysisRes] = await Promise.all([
      listPortalScore({ userId, ...queryParams.value }),
      getPortalScoreAnalysis({ userId, ...queryParams.value }),
    ])
    scoreList.value = scoreRes.data || []
    scoreAnalysis.value = analysisRes.data || {}
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  queryParams.value.termId = defaultTermId.value
  queryParams.value.courseId = undefined
  loadAnalysis()
}

function goBack() {
  router.push({ path: '/student/scores', query: buildRouteQuery() })
}

function handleAskAiAnalysis() {
  if (!scoreAnalysis.value.aiContextPrompt) {
    ElMessage.warning('当前筛选范围暂无可用于 AI 分析的成绩数据')
    return
  }
  saveScoreAnalysisQaPreset({
    courseId: queryParams.value.courseId,
    prompt: '请结合当前成绩分析上下文，做一次成绩诊断，指出优势、风险、可能原因，并给出接下来两周的提升计划。',
    contextLabel: `成绩分析：${analysisScopeLabel.value}`,
    contextPrompt: scoreAnalysis.value.aiContextPrompt,
  })
  router.push('/student/qa')
}

async function loadOptions() {
  const userId = userStore.user?.userId
  if (!userId) return
  const [termRes, courseRes] = await Promise.all([listPortalTermOptions(), fetchPortalCourseOptions(userId)])
  termOptions.value = termRes.data || []
  courseOptions.value = courseRes || []
  const current = termOptions.value.find((item: any) => item.isCurrent === '1') || termOptions.value[0]
  if (current) {
    defaultTermId.value = current.value
    queryParams.value.termId = current.value
  }
  applyRouteQuery()
}

onMounted(async () => {
  await loadOptions()
  await loadAnalysis()
})
</script>

<style scoped>
/* ================= Base Layout & Variables ================= */
.score-analysis-page {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-bottom: 40px;
  color: var(--portal-text);
}

/* 柔和的环境光晕底图 */
.score-analysis-page::before {
  content: '';
  position: absolute;
  top: -40px;
  right: -50px;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(var(--portal-brand-rgb, 47, 107, 255), 0.08) 0%, transparent 70%);
  pointer-events: none;
  z-index: -1;
}

/* ================= Common Components ================= */
.portal-card {
  border-radius: 20px;
  border: 1px solid rgba(220, 228, 238, 0.6);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.03);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.portal-card:hover {
  box-shadow: 0 8px 30px rgba(15, 23, 42, 0.06);
  border-color: rgba(220, 228, 238, 0.9);
}

.score-section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-top: 12px;
  padding: 0 8px;
}

.score-section-header__title {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.score-section-header span {
  display: inline-flex;
  align-items: center;
  height: 26px;
  padding: 0 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(var(--portal-brand-rgb, 47, 107, 255), 0.08);
  color: var(--portal-brand);
  width: fit-content;
}

.score-section-header strong {
  font-size: 20px;
  font-weight: 700;
  line-height: 1.2;
}

.score-section-header p {
  margin: 0;
  font-size: 13px;
  color: var(--portal-text-secondary);
}

/* ================= Topbar ================= */
.score-analysis-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 28px;
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 255, 0.85) 100%);
  border: 1px solid rgba(220, 228, 238, 0.8);
  box-shadow: 0 4px 24px rgba(15, 23, 42, 0.04);
}

.score-analysis-topbar__left {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.back-btn {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid rgba(220, 228, 238, 0.8);
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
}

.back-btn:hover {
  background: var(--portal-brand);
  color: #fff;
  border-color: var(--portal-brand);
}

.score-analysis-topbar__title h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.score-analysis-topbar__title p {
  margin: 6px 0 0;
  color: var(--portal-text-secondary);
  font-size: 13px;
}

.score-analysis-topbar__tag {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  background: rgba(var(--portal-brand-rgb, 47, 107, 255), 0.1);
  color: var(--portal-brand);
}

/* ================= Summary Section ================= */
.score-analysis-summary {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.score-analysis-summary__intro h4 {
  margin: 12px 0 8px;
  font-size: 26px;
  font-weight: 800;
}

.score-analysis-summary__intro p {
  margin: 0;
  font-size: 14px;
  color: var(--portal-text-secondary);
}

.score-analysis-summary__badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(var(--portal-brand-rgb, 47, 107, 255), 0.08);
  color: var(--portal-brand);
}

/* Metrics Cards */
.score-analysis-summary__metrics {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.score-metric-card {
  position: relative;
  padding: 20px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid rgba(220, 228, 238, 0.6);
  box-shadow: 0 2px 12px rgba(15, 23, 42, 0.02);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.score-metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  border-color: rgba(var(--portal-brand-rgb, 47, 107, 255), 0.3);
}

.score-metric-card span {
  font-size: 14px;
  color: var(--portal-text-secondary);
  font-weight: 500;
}

.score-metric-card strong {
  display: block;
  margin: 12px 0 8px;
  font-size: 32px;
  font-weight: 800;
  line-height: 1;
  color: var(--portal-text);
}

.score-metric-card small {
  font-size: 12px;
  color: #8c9bb0;
}

/* Toolbar */
.score-analysis-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  padding-top: 20px;
  border-top: 1px dashed rgba(220, 228, 238, 0.8);
}

.score-analysis-toolbar__controls {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.score-analysis-toolbar__chips {
  display: flex;
  gap: 12px;
}

.score-toolbar-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  background: #f8fafc;
  border: 1px solid rgba(220, 228, 238, 0.8);
  color: var(--portal-text-secondary);
}

/* ================= Chart Grids ================= */
.score-chart-grid,
.score-insight-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.score-chart-card--span-2 {
  grid-column: span 2;
}

.score-chart-card,
.score-insight-card {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.score-chart-card__head,
.score-insight-card__head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.score-chart-card__title-block {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(var(--portal-brand-rgb, 47, 107, 255), 0.08);
  color: var(--portal-brand);
  font-size: 18px;
}

.score-chart-card__title-block strong,
.score-insight-card__head strong {
  display: block;
  font-size: 16px;
  font-weight: 700;
}

.score-chart-card__title-block span,
.score-insight-card__head span {
  font-size: 12px;
  color: var(--portal-text-secondary);
  margin-top: 4px;
  display: block;
}

.score-chart-card__meta {
  font-size: 12px;
  padding: 4px 10px;
  background: #f1f5f9;
  border-radius: 12px;
  color: #64748b;
}

/* Component Legend (Radar) */
.score-component-legend {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 12px;
  margin-top: 8px;
}

.score-component-chip {
  padding: 12px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid rgba(220, 228, 238, 0.6);
  text-align: center;
}

.score-component-chip span {
  display: block;
  font-size: 12px;
  color: var(--portal-text-secondary);
}

.score-component-chip strong {
  display: block;
  font-size: 18px;
  font-weight: 700;
  margin: 6px 0;
  color: var(--portal-text);
}

.score-component-chip small {
  font-size: 11px;
  color: #94a3b8;
}

/* Structure Panels */
.score-structure-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(280px, 1fr));
  gap: 18px;
}

.score-structure-panel {
  min-width: 0;
  display: grid;
  gap: 12px;
  padding: 16px;
  border-radius: 16px;
  background: #f8fafc;
  border: 1px solid rgba(220, 228, 238, 0.6);
}

.score-structure-panel strong {
  display: block;
  font-size: 14px;
  margin-bottom: 0;
  color: var(--portal-text);
}

/* ================= AI Insights Card ================= */
.score-ai-card {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  padding: 24px;
}

.score-ai-card__head {
  margin-bottom: 16px;
}

.score-ai-card__head strong {
  font-size: 18px;
  font-weight: 700;
  display: block;
}

.score-ai-card__head span {
  font-size: 13px;
  color: var(--portal-text-secondary);
  margin-top: 6px;
  display: block;
}

.score-ai-card__insights {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.score-ai-item {
  position: relative;
  padding: 16px 20px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid rgba(220, 228, 238, 0.8);
  display: flex;
  gap: 12px;
  overflow: hidden;
}

.ai-item-indicator {
  width: 4px;
  border-radius: 4px;
  flex-shrink: 0;
}

.score-ai-item--success .ai-item-indicator { background: #10b981; }
.score-ai-item--warning .ai-item-indicator { background: #f59e0b; }
.score-ai-item--info .ai-item-indicator { background: var(--portal-brand); }

.ai-item-content strong {
  display: block;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 6px;
}

.ai-item-content p {
  margin: 0;
  font-size: 13px;
  color: var(--portal-text-secondary);
  line-height: 1.6;
}

.score-ai-card__actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 24px;
  border-radius: 16px;
  background: linear-gradient(145deg, #f8fafc 0%, #f1f5f9 100%);
  border: 1px solid rgba(220, 228, 238, 0.6);
}

.score-ai-card__badge {
  align-self: flex-start;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  background: var(--portal-brand);
  color: #fff;
  margin-bottom: 16px;
}

.score-ai-card__actions h4 {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 700;
}

.score-ai-card__actions p {
  margin: 0 0 20px;
  font-size: 13px;
  color: var(--portal-text-secondary);
  line-height: 1.6;
}

.score-ai-card__actions .el-button {
  width: 100%;
  border-radius: 10px;
}

/* ================= Insight Lists (Bottom) ================= */
.score-insight-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 8px;
}

.score-insight-row {
  display: grid;
  grid-template-columns: 60px 1fr 40px;
  align-items: center;
  gap: 16px;
}

.row-label {
  font-size: 13px;
  color: var(--portal-text-secondary);
  font-weight: 500;
}

.row-value {
  font-size: 14px;
  font-weight: 700;
  text-align: right;
  color: var(--portal-text);
}

.score-insight-bar {
  height: 8px;
  border-radius: 4px;
  background: #f1f5f9;
  overflow: hidden;
}

.score-insight-bar i {
  display: block;
  height: 100%;
  border-radius: 4px;
  background: linear-gradient(90deg, rgba(var(--portal-brand-rgb, 47, 107, 255), 0.7) 0%, var(--portal-brand) 100%);
  transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.score-insight-bar--soft i {
  background: linear-gradient(90deg, #94a3b8 0%, #64748b 100%);
}

.score-analysis-empty {
  padding: 40px 20px;
  border-radius: 20px;
}

/* ================= Responsive ================= */
@media (max-width: 1024px) {
  .score-chart-grid,
  .score-insight-grid,
  .score-ai-card,
  .score-structure-grid {
    grid-template-columns: 1fr;
  }
  
  .score-chart-card--span-2 {
    grid-column: auto;
  }

  .score-ai-card {
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .score-analysis-page {
    gap: 16px;
    padding-bottom: 24px;
  }

  .score-analysis-topbar {
    flex-direction: column;
    align-items: flex-start;
    padding: 20px;
    gap: 16px;
  }

  .score-analysis-topbar__left {
    flex-direction: column;
  }

  .score-analysis-summary,
  .score-chart-card,
  .score-insight-card,
  .score-ai-card {
    padding: 16px;
  }

  .score-analysis-toolbar__controls {
    flex-direction: column;
    width: 100%;
  }

  .score-analysis-toolbar__controls .el-select,
  .score-analysis-toolbar__controls .el-button {
    width: 100% !important;
  }

  .score-section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
