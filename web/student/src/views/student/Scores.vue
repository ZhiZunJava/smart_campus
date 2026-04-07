<template>
  <div class="portal-page score-page">
    <div class="portal-section-title">
      <h3>我的成绩</h3>
      <p>按学期查看已发布课程成绩，表格化展示更适合核对、对比与打印。</p>
    </div>

    <section class="portal-card score-summary-card">
      <div class="score-summary-card__main">
        <div class="score-summary-card__intro">
          <span class="score-summary-card__badge"><i class="ri-bar-chart-box-line"></i> 成绩中心</span>
          <h4>{{ currentTermLabel }}</h4>
          <p>这里集中展示当前学期已发布成绩。你可以先按学期或课程筛选，再查看课程总评、绩点、排名和各项组成。</p>
        </div>

        <div class="score-summary-card__metrics">
          <div class="score-summary-metric">
            <div class="metric-head">
              <span>平均总评</span>
              <div class="metric-icon"><i class="ri-line-chart-line"></i></div>
            </div>
            <strong>{{ formatNumeric(currentOverview.avgScore, '0') }}</strong>
            <small>当前筛选范围内课程均值</small>
          </div>
          <div class="score-summary-metric">
            <div class="metric-head">
              <span>已通过</span>
              <div class="metric-icon" style="color: #10b981; background: #ecfdf5;"><i
                  class="ri-checkbox-circle-line"></i></div>
            </div>
            <strong>{{ currentOverview.passCount || 0 }}</strong>
            <small>{{ passRateText }}</small>
          </div>
          <div class="score-summary-metric">
            <div class="metric-head">
              <span>修读课程</span>
              <div class="metric-icon" style="color: #8b5cf6; background: #f5f3ff;"><i class="ri-book-read-line"></i>
              </div>
            </div>
            <strong>{{ currentOverview.courseCount || 0 }}</strong>
            <small>仅统计已发布成绩课程</small>
          </div>
          <div class="score-summary-metric">
            <div class="metric-head">
              <span>获得学分</span>
              <div class="metric-icon" style="color: #f59e0b; background: #fffbeb;"><i class="ri-medal-line"></i></div>
            </div>
            <strong>{{ formatNumeric(currentOverview.creditEarned, '0') }}</strong>
            <small>通过课程累计学分</small>
          </div>
        </div>
      </div>

      <div class="score-summary-card__toolbar">
        <div class="score-toolbar-controls">
          <el-select v-model="queryParams.termId" filterable clearable placeholder="选择学期" style="width: 260px"
            @change="loadScores">
            <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>

          <el-select v-model="queryParams.courseId" filterable clearable placeholder="筛选课程" style="width: 260px"
            @change="loadScores">
            <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>

          <el-button @click="resetFilters" class="btn-soft">重置筛选</el-button>
          <el-button type="primary" class="btn-shadow" @click="openAnalysisPage">
            <i class="ri-magic-line" style="margin-right: 4px;"></i> 查看深度分析
          </el-button>
        </div>

        <div class="score-toolbar-chips">
          <div class="score-toolbar-chip score-toolbar-chip--best"><i class="ri-award-fill"></i> 最佳成绩 {{
            formatNumeric(bestScore, '0') }}</div>
          <div class="score-toolbar-chip"><i class="ri-arrow-down-circle-fill"></i> 最低成绩 {{ formatNumeric(lowestScore,
            '0') }}</div>
        </div>
      </div>
    </section>

    <section class="portal-card score-table-card">
      <div class="score-table-card__head">
        <div class="head-title-wrap">
          <strong>学期成绩单</strong>
          <span>当前仅显示已发布成绩，点击行可查看组成明细</span>
        </div>
        <div class="score-table-card__meta">
          <span>{{ sortedScores.length }} 门课程</span>
        </div>
      </div>

      <el-table v-loading="loading" :data="sortedScores" class="score-table custom-el-table" stripe
        :header-cell-style="{ background: '#f8fafc', color: '#475569', fontWeight: '600', height: '50px', borderBottom: '1px solid #e2e8f0' }"
        @row-click="openDetail">
        <template #empty>
          <el-empty description="当前条件下暂无已发布成绩" />
        </template>

        <el-table-column label="课程名称" min-width="280">
          <template #default="{ row }">
            <div class="score-course-cell">
              <strong class="course-name">{{ row.courseName || '未命名课程' }}</strong>
              <span class="course-meta">{{ row.termName || '-' }} <i class="divider"></i> {{ row.className || '-'
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="学分" width="90" align="center">
          <template #default="{ row }"><span class="number-font">{{ formatNumeric(row.credits) }}</span></template>
        </el-table-column>

        <el-table-column label="绩点" width="90" align="center">
          <template #default="{ row }"><strong class="number-font text-brand">{{ formatNumeric(row.gradePoint, '0')
              }}</strong></template>
        </el-table-column>

        <el-table-column label="成绩" width="130" align="center">
          <template #default="{ row }">
            <div class="score-total-cell" :class="scoreToneClass(row.totalScore)">
              <strong class="number-font">{{ formatNumeric(row.totalScore, '0') }}</strong>
              <span>{{ row.gradeLevel || '-' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="成绩明细" min-width="360">
          <template #default="{ row }">
            <div class="score-detail-cell">
              <span v-for="item in buildScoreDetailItems(row)" :key="item.label">
                {{ item.label }} <b class="number-font">{{ item.value }}</b>
              </span>
              <span v-if="!buildScoreDetailItems(row).length" class="score-detail-cell__empty">暂无明细</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="排名" width="100" align="center">
          <template #default="{ row }">
            <div class="score-rank-cell">
              <strong class="number-font">{{ row.rankNo || '-' }}</strong>
              <span v-if="row.percentile != null">百分位 {{ formatNumeric(row.percentile, '0') }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.passFlag === '1' ? 'success' : 'danger'"
              :effect="row.passFlag === '1' ? 'light' : 'plain'" round>
              {{ row.passFlag === '1' ? '已通过' : '待提升' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="发布时间" width="160" align="center">
          <template #default="{ row }"><span class="time-font">{{ formatDateTime(row.publishTime) || '-'
              }}</span></template>
        </el-table-column>

        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" class="action-btn" @click.stop="openDetail(row)">查看明细</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="detailOpen" title="课程成绩详情" width="960px" top="6vh" append-to-body
      class="score-detail-dialog custom-dialog" destroy-on-close>
      <div class="score-detail-shell" v-if="detailData.score">
        <section class="score-detail-banner">
          <div class="score-detail-banner__main">
            <span class="score-detail-banner__badge">{{ detailData.score.termName || '成绩详情' }}</span>
            <h4>{{ detailData.score.courseName || '-' }}</h4>
            <p>{{ detailData.course?.assessmentType || detailData.score.assessmentType || '课程总评' }} · 这里可以查看各项成绩权重及考试来源。
            </p>
          </div>

          <div class="score-detail-banner__stats">
            <div class="stat-box primary">
              <span>最终总评</span>
              <strong class="number-font">{{ formatNumeric(detailData.score.totalScore, '0') }}</strong>
            </div>
            <div class="stat-box">
              <span>评级等级</span>
              <strong class="number-font">{{ detailData.score.gradeLevel || '-' }}</strong>
            </div>
            <div class="stat-box">
              <span>获得绩点</span>
              <strong class="number-font">{{ formatNumeric(detailData.score.gradePoint, '0') }}</strong>
            </div>
            <div class="stat-box">
              <span>班级排名</span>
              <strong class="number-font">{{ detailData.score.rankNo || '-' }}</strong>
            </div>
          </div>
        </section>

        <div class="score-detail-grid">
          <div class="portal-card score-detail-panel score-detail-panel--breakdown">
            <div class="score-detail-panel__title">成绩构成图谱</div>
            <el-table :data="detailBreakdownRows" size="small" class="score-breakdown-table custom-el-table"
              :header-cell-style="{ background: 'transparent', color: '#64748b' }">
              <el-table-column prop="label" label="考核项目" min-width="120">
                <template #default="{ row }"><span class="font-medium">{{ row.label }}</span></template>
              </el-table-column>
              <el-table-column prop="score" label="得分" width="100" align="center">
                <template #default="{ row }"><strong class="number-font">{{ row.score }}</strong></template>
              </el-table-column>
              <el-table-column prop="weight" label="权重比例" width="100" align="center">
                <template #default="{ row }"><el-tag size="small" type="info" effect="plain">{{ row.weight
                    }}</el-tag></template>
              </el-table-column>
              <el-table-column prop="note" label="补充说明" min-width="140" show-overflow-tooltip>
                <template #default="{ row }"><span class="text-muted">{{ row.note }}</span></template>
              </el-table-column>
            </el-table>

            <div class="score-comment" v-if="detailData.score.teacherComment">
              <div class="score-detail-panel__title"><i class="ri-message-3-line"></i> 教师评语</div>
              <div class="comment-box">
                <p>{{ detailData.score.teacherComment }}</p>
              </div>
            </div>
          </div>

          <div class="portal-card score-detail-panel score-detail-panel--course">
            <div class="score-detail-panel__title">档案信息</div>
            <div class="score-course-info">
              <div class="info-row"><span class="label">开课学期</span><span class="value">{{ detailData.score.termName ||
                  '-'
                  }}</span></div>
              <div class="info-row"><span class="label">所属班级</span><span class="value">{{ detailData.score.className ||
                  '-'
                  }}</span></div>
              <div class="info-row"><span class="label">课程学分</span><span class="value number-font">{{
                formatNumeric(detailData.score.credits) }}</span></div>
              <div class="info-row"><span class="label">考核方式</span><span class="value">{{
                detailData.course?.assessmentType
                || detailData.score.assessmentType || '-' }}</span></div>
              <div class="info-row"><span class="label">发布时间</span><span class="value time-font">{{
                formatDateTime(detailData.score.publishTime) || '-' }}</span></div>
              <div class="info-row">
                <span class="label">结课状态</span>
                <span class="value">
                  <span class="status-dot"
                    :class="detailData.score.passFlag === '1' ? 'dot-success' : 'dot-danger'"></span>
                  {{ detailData.score.passFlag === '1' ? '已通过' : '待提升' }}
                </span>
              </div>
            </div>

            <div class="score-exam-summary">
              <div v-for="item in detailExamSummaryItems" :key="item.label" class="summary-item">
                <span class="summary-label">{{ item.label }}</span>
                <strong class="summary-value number-font">{{ item.value }}</strong>
              </div>
            </div>
          </div>
        </div>

        <div class="portal-card score-detail-panel" v-if="detailData.examRecords && detailData.examRecords.length > 0">
          <div class="score-detail-panel__title">在线考试来源记录</div>
          <el-table :data="detailData.examRecords" max-height="300" class="score-exam-table custom-el-table">
            <el-table-column prop="paperName" label="考试/测验名称" min-width="240" show-overflow-tooltip />
            <el-table-column prop="score" label="试卷得分" width="100" align="center">
              <template #default="{ row }"><strong class="number-font text-brand">{{ row.score }}</strong></template>
            </el-table-column>
            <el-table-column prop="correctRate" label="正确率" width="100" align="center" />
            <el-table-column prop="examStatus" label="状态" width="110" align="center">
              <template #default="{ row }"><el-tag size="small">{{ row.examStatus }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="submitTime" label="提交交卷时间" min-width="180" align="center" />
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import type { EChartsCoreOption } from 'echarts'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import ScoreAnalysisChart from '@/components/score/ScoreAnalysisChart.vue'
import usePortalUserStore from '@/store/user'
import { saveScoreAnalysisQaPreset } from '@/utils/qaPreset'
import { fetchPortalCourseOptions, getPortalScoreDetail, listPortalScore, listPortalTermOptions } from '@/api/portal'

const route = useRoute()
const router = useRouter()
const userStore = usePortalUserStore()
const loading = ref(false)
const termOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const scoreList = ref<any[]>([])
const scoreAnalysis = ref<any>({})
const detailOpen = ref(false)
const detailData = ref<any>({})
const queryParams = ref<{ termId?: number; courseId?: number }>({})
const defaultTermId = ref<number | undefined>()

const currentTermLabel = computed(() => {
  return termOptions.value.find((item: any) => item.value === queryParams.value.termId)?.label || '全部已发布成绩'
})

const sortedScores = computed(() => {
  return [...scoreList.value].sort((a: any, b: any) => Number(b.totalScore || 0) - Number(a.totalScore || 0))
})

const hasScores = computed(() => sortedScores.value.length > 0)
const analysisInsights = computed(() => scoreAnalysis.value.insights || [])
const componentSeries = computed(() => scoreAnalysis.value.componentSeries || [])
const analysisScopeLabel = computed(() => scoreAnalysis.value.scope?.scopeLabel || currentTermLabel.value)
const currentOverview = computed(() => {
  const courseCount = sortedScores.value.length
  const passCount = sortedScores.value.filter((item: any) => item.passFlag === '1').length
  const avgScore = courseCount
    ? sortedScores.value.reduce((total: number, item: any) => total + Number(item.totalScore || 0), 0) / courseCount
    : 0
  const creditEarned = sortedScores.value
    .filter((item: any) => item.passFlag === '1')
    .reduce((total: number, item: any) => total + Number(item.credits || 0), 0)
  return {
    avgScore,
    passCount,
    courseCount,
    creditEarned,
  }
})
const hasAnalytics = computed(() => {
  const data = scoreAnalysis.value || {}
  return analysisInsights.value.length > 0
    || (data.termTrend || []).length > 0
    || (data.courseSeries || []).length > 0
    || (data.componentSeries || []).length > 0
    || (data.scoreBands || []).length > 0
    || (data.gradeDistribution || []).length > 0
})

const bestScore = computed(() => {
  return sortedScores.value.length ? Number(sortedScores.value[0]?.totalScore || 0) : 0
})

const lowestScore = computed(() => {
  return sortedScores.value.length ? Number(sortedScores.value[sortedScores.value.length - 1]?.totalScore || 0) : 0
})

const passRateText = computed(() => {
  const total = Number(currentOverview.value.courseCount || 0)
  const passed = Number(currentOverview.value.passCount || 0)
  if (!total) return '暂无可统计课程'
  return `通过率 ${((passed / total) * 100).toFixed(0)}%`
})

const gradeDistributionItems = computed(() => {
  const items = (scoreAnalysis.value.gradeDistribution || [])
    .filter((item: any) => Number(item.count || 0) > 0)
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

const termTrendCaption = computed(() => {
  return queryParams.value.courseId ? '当前课程跨学期平均分变化' : '近学期平均分变化'
})

const componentLegendItems = computed(() => {
  return componentSeries.value
    .filter((item: any) => Number(item.avgWeight || 0) > 0)
    .map((item: any) => ({
      ...item,
      avgScoreText: formatNumeric(item.avgScore, '0'),
      avgWeightText: formatNumeric(item.avgWeight, '0'),
    }))
})

const termTrendOption = computed<EChartsCoreOption | undefined>(() => {
  const trend = scoreAnalysis.value.termTrend || []
  if (!trend.length) return undefined
  return {
    animationDuration: 480,
    grid: { left: 44, right: 18, top: 26, bottom: 26 },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const current = trend[params?.[0]?.dataIndex || 0] || {}
        return [
          current.termName || '未配置学期',
          `平均分：${formatNumeric(current.avgScore, '0')}`,
          `课程数：${current.courseCount || 0}`,
          `平均绩点：${formatNumeric(current.avgGradePoint, '0')}`,
        ].join('<br/>')
      },
    },
    xAxis: {
      type: 'category',
      data: trend.map((item: any) => item.termName || '未配置学期'),
      axisTick: { show: false },
      axisLine: { lineStyle: { color: '#d7e3f5' } },
      axisLabel: { color: '#64748b' },
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      splitNumber: 5,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#94a3b8' },
      splitLine: { lineStyle: { color: '#eef3fa' } },
    },
    series: [
      {
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 10,
        data: trend.map((item: any) => Number(item.avgScore || 0)),
        lineStyle: { width: 3, color: '#2563eb' },
        itemStyle: { color: '#1d4ed8' },
        areaStyle: { color: 'rgba(59, 130, 246, 0.14)' },
        label: {
          show: trend.length <= 6,
          position: 'top',
          color: '#475569',
          fontSize: 12,
          formatter: (params: any) => formatNumeric(params.value, '0'),
        },
      },
    ],
  }
})

const courseComparisonOption = computed<EChartsCoreOption | undefined>(() => {
  const raw = (scoreAnalysis.value.courseSeries || []).slice(0, 10)
  if (!raw.length) return undefined
  const data = [...raw].reverse()
  return {
    animationDuration: 420,
    grid: { left: 12, right: 24, top: 10, bottom: 10, containLabel: true },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params: any) => {
        const current = data[params?.[0]?.dataIndex || 0] || {}
        return [
          buildCourseChartLabel(current),
          `总评：${formatNumeric(current.totalScore, '0')}`,
          `绩点：${formatNumeric(current.gradePoint, '0')}`,
          `排名：${current.rankNo || '-'}`,
        ].join('<br/>')
      },
    },
    xAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#94a3b8' },
      splitLine: { lineStyle: { color: '#eef3fa' } },
    },
    yAxis: {
      type: 'category',
      data: data.map((item: any) => buildCourseChartLabel(item)),
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: {
        color: '#64748b',
        width: 128,
        overflow: 'truncate',
      },
    },
    series: [
      {
        type: 'bar',
        barWidth: 18,
        label: {
          show: true,
          position: 'right',
          color: '#334155',
          formatter: (params: any) => formatNumeric(params.value, '0'),
        },
        data: data.map((item: any) => ({
          value: Number(item.totalScore || 0),
          itemStyle: {
            color: resolveScoreColor(Number(item.totalScore || 0)),
            borderRadius: [0, 8, 8, 0],
          },
        })),
      },
    ],
  }
})

const componentRadarOption = computed<EChartsCoreOption | undefined>(() => {
  const data = componentSeries.value
  if (!data.length) return undefined
  return {
    animationDuration: 420,
    tooltip: {
      trigger: 'item',
      formatter: () => data.map((item: any) => `${item.label}：${formatNumeric(item.avgScore, '0')}`).join('<br/>'),
    },
    radar: {
      center: ['50%', '54%'],
      radius: '68%',
      splitNumber: 5,
      indicator: data.map((item: any) => ({ name: item.label, max: 100 })),
      axisName: { color: '#475569', fontSize: 12 },
      splitArea: {
        areaStyle: {
          color: ['rgba(248, 250, 252, 0.96)', 'rgba(238, 246, 255, 0.7)'],
        },
      },
      splitLine: { lineStyle: { color: '#d7e3f5' } },
      axisLine: { lineStyle: { color: '#d7e3f5' } },
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: data.map((item: any) => Number(item.avgScore || 0)),
            name: '成绩构成均分',
            itemStyle: { color: '#2563eb' },
            lineStyle: { color: '#2563eb', width: 2 },
            areaStyle: { color: 'rgba(59, 130, 246, 0.18)' },
          },
        ],
      },
    ],
  }
})

const scoreBandChartOption = computed<EChartsCoreOption | undefined>(() => {
  const raw = (scoreAnalysis.value.scoreBands || []).filter((item: any) => Number(item.count || 0) > 0)
  if (!raw.length) return undefined
  const colors = ['#1d4ed8', '#3b82f6', '#60a5fa', '#93c5fd', '#f97316']
  return {
    animationDuration: 360,
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => `${params.name}：${params.value} 门`,
    },
    series: [
      {
        type: 'pie',
        radius: ['42%', '72%'],
        center: ['50%', '50%'],
        minAngle: 10,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: {
          color: '#475569',
          fontSize: 12,
          formatter: (params: any) => `${params.name}\n${params.value}`,
        },
        data: raw.map((item: any, index: number) => ({
          name: item.label,
          value: Number(item.count || 0),
          itemStyle: { color: colors[index % colors.length] },
        })),
      },
    ],
  }
})

const gradeDistributionChartOption = computed<EChartsCoreOption | undefined>(() => {
  const raw = (scoreAnalysis.value.gradeDistribution || []).filter((item: any) => Number(item.count || 0) > 0)
  if (!raw.length) return undefined
  return {
    animationDuration: 360,
    grid: { left: 42, right: 10, top: 12, bottom: 8, containLabel: true },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params: any) => {
        const current = raw[params?.[0]?.dataIndex || 0] || {}
        return `${current.gradeLevel || '未评级'}：${current.count || 0} 门`
      },
    },
    xAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#94a3b8' },
      splitLine: { lineStyle: { color: '#eef3fa' } },
    },
    yAxis: {
      type: 'category',
      data: raw.map((item: any) => item.gradeLevel || '未评级'),
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: { color: '#64748b' },
    },
    series: [
      {
        type: 'bar',
        barWidth: 16,
        label: {
          show: true,
          position: 'right',
          color: '#334155',
        },
        data: raw.map((item: any) => ({
          value: Number(item.count || 0),
          itemStyle: {
            color: resolveGradeColor(String(item.gradeLevel || '')),
            borderRadius: [0, 8, 8, 0],
          },
        })),
      },
    ],
  }
})

const detailBreakdownRows = computed(() => {
  const score = detailData.value.score || {}
  const weightedRows = buildWeightedBreakdownItems(score)
    .filter((item) => item.weightValue > 0)
    .map((item) => ({
      label: item.label,
      score: item.score,
      weight: `${formatNumeric(item.weightValue, '0')}%`,
      note: item.note,
    }))

  return [
    ...weightedRows,
    { label: '附加项', score: `+${formatNumeric(score.bonusScore, '0')} / -${formatNumeric(score.penaltyScore, '0')}`, weight: '-', note: '教师手动加减分' },
  ]
})

const detailExamSummaryItems = computed(() => {
  const summary = detailData.value.examSummary || {}
  return [
    { label: '考试次数', value: String(summary.recordCount || 0) },
    { label: '已提交', value: String(summary.submittedCount || 0) },
    { label: '平均分', value: formatNumeric(summary.avgScore, '0') },
    { label: '最高分', value: formatNumeric(summary.bestScore, '0') },
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

function buildWeightedBreakdownItems(score: any) {
  return [
    { label: '平时分', shortLabel: '平时', score: formatNumeric(score?.usualScore, '0'), weightValue: Number(score?.usualWeight || 0), note: '课堂表现 / 过程性评价' },
    { label: '考勤分', shortLabel: '考勤', score: formatNumeric(score?.attendanceScore, '0'), weightValue: Number(score?.attendanceWeight || 0), note: '到课与参与情况' },
    { label: '作业分', shortLabel: '作业', score: formatNumeric(score?.homeworkScore, '0'), weightValue: Number(score?.homeworkWeight || 0), note: '作业与阶段练习' },
    { label: '实验分', shortLabel: '实验', score: formatNumeric(score?.labScore, '0'), weightValue: Number(score?.labWeight || 0), note: '实验 / 实训表现' },
    { label: '考试分', shortLabel: '考试', score: formatNumeric(score?.examScore, '0'), weightValue: Number(score?.examWeight || 0), note: `同步均分 ${formatNumeric(score?.examAvgScore, '0')}` },
  ]
}

function buildScoreDetailItems(score: any) {
  return buildWeightedBreakdownItems(score)
    .filter((item) => item.weightValue > 0)
    .map((item) => ({
      label: item.shortLabel,
      value: item.score,
    }))
}

function resolveScoreColor(value: number) {
  if (value >= 90) return '#2563eb'
  if (value >= 80) return '#3b82f6'
  if (value >= 60) return '#94a3b8'
  return '#f97316'
}

function resolveGradeColor(level: string) {
  if (level.startsWith('A')) return '#2563eb'
  if (level.startsWith('B')) return '#3b82f6'
  if (level.startsWith('C')) return '#60a5fa'
  if (level === 'D') return '#f59e0b'
  return '#f97316'
}

function buildCourseChartLabel(item: any) {
  const courseName = item?.courseName || '未命名课程'
  if (!queryParams.value.termId && item?.termName) {
    return `${courseName} · ${item.termName}`
  }
  return courseName
}

function scoreToneClass(value: any) {
  const score = Number(value || 0)
  if (score >= 90) return 'is-excellent'
  if (score < 60) return 'is-risk'
  return 'is-normal'
}

function buildRouteQuery() {
  return {
    ...(queryParams.value.termId ? { termId: String(queryParams.value.termId) } : {}),
    ...(queryParams.value.courseId ? { courseId: String(queryParams.value.courseId) } : {}),
  }
}

function syncRouteQuery() {
  router.replace({
    path: '/student/scores',
    query: buildRouteQuery(),
  })
}

function applyRouteQuery() {
  const queryTermId = Number(route.query.termId || 0)
  const queryCourseId = Number(route.query.courseId || 0)
  if (queryTermId) {
    queryParams.value.termId = queryTermId
  }
  if (queryCourseId) {
    queryParams.value.courseId = queryCourseId
  }
}

async function loadScores() {
  const userId = userStore.user?.userId
  if (!userId) return
  syncRouteQuery()
  loading.value = true
  try {
    const scoreRes = await listPortalScore({ userId, ...queryParams.value })
    scoreList.value = scoreRes.data || []
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  queryParams.value.termId = defaultTermId.value
  queryParams.value.courseId = undefined
  loadScores()
}

function openAnalysisPage() {
  router.push({
    path: '/student/scores/analysis',
    query: buildRouteQuery(),
  })
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
    defaultTermId.value = current.value
    queryParams.value.termId = current.value
  }
  applyRouteQuery()
}

onMounted(async () => {
  await loadOptions()
  await loadScores()
})
</script>

<style scoped>
.number-font {
  font-family: 'Din', 'Roboto', 'Helvetica Neue', sans-serif;
  letter-spacing: 0.5px;
}

.time-font {
  color: #64748b;
  font-size: 13px;
}

.text-brand {
  color: var(--portal-brand, #2563eb);
}

.text-muted {
  color: #94a3b8;
}

.font-medium {
  font-weight: 500;
  color: #334155;
}

/* 基础页面布局 */
.score-page {
  gap: 20px;
  padding-bottom: 24px;
}

.portal-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.02);
  border: 1px solid rgba(226, 232, 240, 0.8);
  overflow: hidden;
}

/* 概览卡片区 */
.score-summary-card {
  padding: 24px;
}

.score-summary-card__main {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(460px, 1.2fr);
  gap: 20px;
  align-items: stretch;
}

/* Intro 左侧横幅 */
.score-summary-card__intro {
  padding: 24px;
  border-radius: 14px;
  background: linear-gradient(135deg, #f4f8ff 0%, #ffffff 100%);
  border: 1px solid #e0e7ff;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.score-summary-card__badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  align-self: flex-start;
  height: 28px;
  padding: 0 12px;
  border-radius: 8px;
  background: #eef2ff;
  color: #4338ca;
  font-size: 12px;
  font-weight: 700;
}

.score-summary-card__intro h4 {
  margin: 16px 0 10px;
  color: #1e293b;
  font-size: 22px;
  font-weight: 700;
  line-height: 1.3;
}

.score-summary-card__intro p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
  line-height: 1.6;
}

/* Metrics 右侧四宫格 */
.score-summary-card__metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.score-summary-metric {
  padding: 18px 20px;
  border-radius: 14px;
  background: #ffffff;
  border: 1px solid #f1f5f9;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.02);
  transition: all 0.25s ease;
  display: flex;
  flex-direction: column;
}

.score-summary-metric:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 16px rgba(15, 23, 42, 0.06);
  border-color: #e2e8f0;
}

.metric-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.metric-head span {
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
}

.metric-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  background: #eff6ff;
  color: #3b82f6;
}

.score-summary-metric strong {
  margin-top: 12px;
  color: #0f172a;
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
  font-family: 'Din', sans-serif;
}

.score-summary-metric small {
  margin-top: 8px;
  color: #94a3b8;
  font-size: 12px;
}

/* Toolbar 工具栏 */
.score-summary-card__toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px dashed #e2e8f0;
}

.score-toolbar-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.btn-soft {
  background: #f8fafc;
  border-color: #e2e8f0;
  color: #475569;
}

.btn-soft:hover {
  background: #f1f5f9;
  color: #0f172a;
}

.btn-shadow {
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

.score-toolbar-chips {
  display: flex;
  align-items: center;
  gap: 10px;
}

.score-toolbar-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 32px;
  padding: 0 14px;
  border-radius: 100px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #475569;
  font-size: 13px;
  font-weight: 600;
}

.score-toolbar-chip--best {
  background: #fffbeb;
  border-color: #fde68a;
  color: #b45309;
}

.score-toolbar-chip--best i {
  color: #f59e0b;
}

/* 表格卡片区 */
.score-table-card {
  padding: 20px 24px;
}

.score-table-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.head-title-wrap strong {
  display: block;
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.head-title-wrap span {
  display: block;
  margin-top: 4px;
  color: #64748b;
  font-size: 13px;
}

.score-table-card__meta span {
  display: inline-block;
  padding: 6px 12px;
  background: #f1f5f9;
  border-radius: 6px;
  color: #475569;
  font-size: 13px;
  font-weight: 600;
}

/* 覆盖 el-table 默认样式，增加拟物轻盈感 */
.custom-el-table {
  --el-table-border-color: #f1f5f9;
  --el-table-header-text-color: #475569;
}

.custom-el-table :deep(.el-table__row) {
  cursor: pointer;
  transition: background-color 0.2s;
}

.custom-el-table :deep(.el-table__row:hover > td) {
  background-color: #f8fafc !important;
}

.score-course-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.course-name {
  color: #1e293b;
  font-size: 14px;
}

.course-meta {
  color: #64748b;
  font-size: 12px;
}

.course-meta .divider {
  display: inline-block;
  width: 1px;
  height: 10px;
  background: #cbd5e1;
  margin: 0 6px;
  vertical-align: middle;
}

/* 成绩胶囊样式 (保留你之前的横排居中完美方案) */
.score-total-cell {
  display: inline-flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 100px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.score-total-cell strong {
  color: #0f172a;
  font-size: 16px;
  font-weight: 700;
  line-height: 1;
}

.score-total-cell span {
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
}

.score-total-cell.is-excellent {
  background: #ecfdf5;
  border-color: #a7f3d0;
}

.score-total-cell.is-excellent strong,
.score-total-cell.is-excellent span {
  color: #059669;
}

.score-total-cell.is-risk {
  background: #fef2f2;
  border-color: #fecaca;
}

.score-total-cell.is-risk strong,
.score-total-cell.is-risk span {
  color: #dc2626;
}

/* 成绩明细小标 */
.score-detail-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.score-detail-cell span {
  display: inline-flex;
  align-items: center;
  height: 24px;
  padding: 0 8px;
  border-radius: 6px;
  background: #f1f5f9;
  color: #64748b;
  font-size: 12px;
}

.score-detail-cell__empty {
  background: rgba(244, 247, 252, 0.8);
  border: 1px dashed rgba(203, 213, 225, 0.95);
}

.score-detail-cell b {
  color: #334155;
  margin-left: 4px;
}

/* 排名 */
.score-rank-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.score-rank-cell strong {
  color: #0f172a;
  font-size: 15px;
}

.score-rank-cell span {
  color: #94a3b8;
  font-size: 11px;
}

/* 详情弹窗定制化 */
.custom-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px 24px 16px;
  border-bottom: 1px solid #f1f5f9;
}

.custom-dialog :deep(.el-dialog__title) {
  font-weight: 700;
  color: #0f172a;
}

.custom-dialog :deep(.el-dialog__body) {
  padding: 20px 24px 24px;
  background: #f8fafc;
  /* 给弹窗加一点微弱底色，突出里面的纯白卡片 */
}

.score-detail-shell {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 弹窗 Banner */
.score-detail-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 24px;
  border-radius: 16px;
  background: linear-gradient(135deg, #eef2ff 0%, #ffffff 100%);
  border: 1px solid #e0e7ff;
  box-shadow: 0 4px 12px rgba(67, 56, 202, 0.03);
}

.score-detail-banner__badge {
  display: inline-flex;
  align-items: center;
  height: 26px;
  padding: 0 10px;
  border-radius: 6px;
  background: #4338ca;
  color: #ffffff;
  font-size: 12px;
  font-weight: 600;
}

.score-detail-banner__main h4 {
  margin: 12px 0 8px;
  color: #0f172a;
  font-size: 24px;
  font-weight: 700;
}

.score-detail-banner__main p {
  margin: 0;
  color: #64748b;
  font-size: 13px;
}

.score-detail-banner__stats {
  display: flex;
  gap: 12px;
}

.stat-box {
  min-width: 90px;
  padding: 16px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  text-align: center;
}

.stat-box.primary {
  background: #4338ca;
  border-color: #4338ca;
}

.stat-box.primary span,
.stat-box.primary strong {
  color: #ffffff;
}

.stat-box span {
  display: block;
  color: #64748b;
  font-size: 12px;
  margin-bottom: 8px;
}

.stat-box strong {
  display: block;
  color: #0f172a;
  font-size: 22px;
  font-weight: 700;
}

/* 弹窗双列 Grid */
.score-detail-grid {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 16px;
}

.score-detail-panel {
  padding: 20px;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
}

.score-detail-panel__title {
  margin-bottom: 16px;
  color: #0f172a;
  font-size: 16px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 教师评语框 */
.score-comment {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px dashed #e2e8f0;
}

.comment-box {
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
  border-left: 3px solid #3b82f6;
}

.comment-box p {
  margin: 0;
  color: #475569;
  font-size: 13px;
  line-height: 1.6;
}

/* 课程信息（苹果设置页风格排版） */
.score-course-info {
  background: #f8fafc;
  border-radius: 12px;
  padding: 8px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-bottom: 1px solid #f1f5f9;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row .label {
  color: #64748b;
  font-size: 13px;
}

.info-row .value {
  color: #1e293b;
  font-size: 13px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.dot-success {
  background: #10b981;
}

.dot-danger {
  background: #ef4444;
}

/* 考试汇总小块 */
.score-exam-summary {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-top: 16px;
}

.summary-item {
  background: #f8fafc;
  padding: 12px 16px;
  border-radius: 10px;
  border: 1px solid #f1f5f9;
}

.summary-label {
  display: block;
  color: #64748b;
  font-size: 12px;
  margin-bottom: 4px;
}

.summary-value {
  display: block;
  color: #0f172a;
  font-size: 20px;
  font-weight: 700;
}

/* 响应式调整 */
@media (max-width: 1024px) {
  .score-summary-card__main {
    grid-template-columns: 1fr;
  }

  .score-detail-banner {
    flex-direction: column;
    align-items: stretch;
  }

  .score-detail-banner__stats {
    flex-wrap: wrap;
  }

  .stat-box {
    flex: 1;
  }

  .score-detail-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .score-summary-card {
    padding: 16px;
  }

  .score-summary-card__intro {
    padding: 16px;
  }

  .score-summary-card__intro h4 {
    font-size: 18px;
  }

  .score-table-card {
    padding: 14px 16px;
    overflow-x: auto;
  }

  .head-title-wrap strong {
    font-size: 16px;
  }

  .score-detail-banner__main h4 {
    font-size: 20px;
  }

  .score-summary-metric strong {
    font-size: 22px;
  }

  .custom-dialog :deep(.el-dialog) {
    --el-dialog-width: 100% !important;
    margin: 0 !important;
    max-width: 100%;
    border-radius: 12px 12px 0 0;
  }
}

@media (max-width: 640px) {
  .score-summary-card__metrics {
    grid-template-columns: 1fr;
  }

  .score-toolbar-controls {
    flex-direction: column;
    width: 100%;
  }

  .score-toolbar-controls .el-select,
  .score-toolbar-controls .el-button {
    width: 100% !important;
  }

  .score-toolbar-chips {
    flex-direction: column;
    gap: 6px;
    width: 100%;
  }

  .score-toolbar-chip {
    width: 100%;
    justify-content: center;
  }

  .score-summary-card__badge {
    font-size: 11px;
    padding: 0 8px;
    height: 24px;
  }

  .score-summary-card__intro h4 {
    font-size: 16px;
    margin: 10px 0 8px;
  }

  .score-summary-card__intro p {
    font-size: 13px;
  }

  .score-summary-metric {
    padding: 14px 16px;
  }

  .score-summary-metric strong {
    font-size: 20px;
    margin-top: 8px;
  }

  .score-table-card {
    padding: 12px;
  }

  .score-table-card__head {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .head-title-wrap strong {
    font-size: 15px;
  }

  /* Table overflow on mobile */
  .custom-el-table {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }

  .custom-el-table :deep(.el-table) {
    min-width: 600px;
  }

  /* Touch targets */
  .score-total-cell {
    padding: 5px 10px;
    min-height: 36px;
  }

  /* Dialog banner */
  .score-detail-banner {
    padding: 16px;
    gap: 16px;
  }

  .score-detail-banner__main h4 {
    font-size: 18px;
  }

  .stat-box {
    min-width: 70px;
    padding: 12px;
  }

  .stat-box strong {
    font-size: 18px;
  }

  .score-detail-panel {
    padding: 14px;
  }

  .score-exam-summary {
    grid-template-columns: 1fr;
  }

  .score-comment .comment-box {
    padding: 10px 12px;
  }
}
</style>
