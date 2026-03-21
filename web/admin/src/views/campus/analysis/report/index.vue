<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="toolbar">
      <el-form-item label="用户">
        <el-select v-model="queryParams.userId" filterable clearable style="width: 240px" placeholder="请选择用户">
          <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="报告类型">
        <el-select v-model="reportType" style="width: 180px">
          <el-option label="周报" value="weekly" />
          <el-option label="月报" value="monthly" />
          <el-option label="诊断报告" value="diagnosis" />
          <el-option label="考试后报告" value="exam_after_submit" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button type="warning" plain icon="Document" @click="handleGenerate">生成报告</el-button>
        <el-button plain icon="Monitor" @click="loadWorkbench">工作台</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="summary-row" v-if="workbench">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="summary-label">能力等级</div>
          <div class="summary-value">{{ workbench.profile?.abilityLevel || '--' }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="summary-label">最近报告数</div>
          <div class="summary-value">{{ workbench.recentReports?.length || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="summary-label">当前推荐数</div>
          <div class="summary-value">{{ workbench.activeRecommendations?.length || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="summary-row" v-if="workbench?.behaviorStats">
      <el-col :span="6"><el-card shadow="never"><div class="summary-label">学习行为数</div><div class="summary-value small">{{ workbench.behaviorStats.totalBehaviorCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="summary-label">学习天数</div><div class="summary-value small">{{ workbench.behaviorStats.studyDayCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="summary-label">已学资源</div><div class="summary-value small">{{ workbench.behaviorStats.learnedResourceCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="summary-label">错题数</div><div class="summary-value small warning">{{ workbench.behaviorStats.wrongQuestionCount || 0 }}</div></el-card></el-col>
    </el-row>

    <el-row :gutter="16" class="summary-row" v-if="workbench?.focusResources?.length">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header><span>当前重点资源</span></template>
          <div class="focus-grid">
            <div v-for="item in workbench.focusResources" :key="item.resourceId" class="focus-card">
              <div class="focus-card__title">{{ item.resourceName }}</div>
              <div class="focus-card__meta">推荐分 {{ item.recommendationScore }} · 热度 {{ item.heatScore || '--' }} · {{ item.resourceType }}</div>
              <div class="focus-card__desc">{{ item.recommendationReason || item.summary || '暂无说明' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="reportList">
      <el-table-column label="报告ID" prop="reportId" width="100" />
      <el-table-column label="用户ID" prop="userId" width="90" />
      <el-table-column label="报告类型" prop="reportType" width="140" />
      <el-table-column label="报告内容" prop="reportContent" min-width="360" show-overflow-tooltip />
      <el-table-column label="生成时间" prop="generateTime" min-width="180" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="showReportJson(scope.row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-drawer v-model="reportVisible" title="报告详情" size="45%">
      <el-descriptions :column="1" border v-if="currentReport">
        <el-descriptions-item label="报告ID">{{ currentReport.reportId }}</el-descriptions-item>
        <el-descriptions-item label="报告类型">{{ currentReport.reportType }}</el-descriptions-item>
        <el-descriptions-item label="报告内容">{{ currentReport.reportContent }}</el-descriptions-item>
      </el-descriptions>
      <el-card shadow="never" class="section-card">
        <template #header>结构化数据</template>
        <pre class="json-block">{{ prettyReportJson }}</pre>
      </el-card>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { generateLearningReport, getLearningWorkbench, listLearningReport } from '@/api/campus/analysis'
import { fetchUserOptions } from '@/api/campus/options'

const loading = ref(false)
const total = ref(0)
const reportList = ref<any[]>([])
const reportType = ref('weekly')
const userOptions = ref<any[]>([])
const workbench = ref<any>(null)
const reportVisible = ref(false)
const currentReport = ref<any>(null)
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, userId: undefined })

const prettyReportJson = computed(() => {
  if (!currentReport.value?.reportJson) return '暂无结构化数据'
  try {
    return JSON.stringify(JSON.parse(currentReport.value.reportJson), null, 2)
  } catch {
    return currentReport.value.reportJson
  }
})

async function getList() {
  loading.value = true
  try {
    const res = await listLearningReport({ ...queryParams, reportType: reportType.value })
    reportList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function handleGenerate() {
  if (!queryParams.userId) {
    ElMessage.warning('请先选择用户')
    return
  }
  await generateLearningReport({ userId: queryParams.userId, reportType: reportType.value })
  ElMessage.success('报告生成成功')
  await Promise.all([getList(), loadWorkbench()])
}

async function loadWorkbench() {
  if (!queryParams.userId) {
    ElMessage.warning('请先选择用户')
    return
  }
  const res = await getLearningWorkbench({ userId: queryParams.userId, limit: 5 })
  workbench.value = res.data || null
}

function showReportJson(row: any) {
  currentReport.value = row
  reportVisible.value = true
}

async function loadOptions() {
  userOptions.value = await fetchUserOptions()
  if (!queryParams.userId && userOptions.value.length) {
    queryParams.userId = userOptions.value[0].value
  }
}

onMounted(async () => {
  await loadOptions()
  await Promise.all([getList(), loadWorkbench()])
})
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
.summary-row { margin-bottom: 16px; }
.summary-label { color: var(--el-text-color-secondary); }
.summary-value { margin-top: 8px; font-size: 28px; font-weight: 700; color: var(--el-color-primary); }
.summary-value.small { font-size: 24px; }
.summary-value.warning { color: var(--el-color-warning); }
.section-card { margin-top: 16px; }
.json-block { margin: 0; white-space: pre-wrap; word-break: break-word; font-size: 13px; line-height: 1.7; }
.focus-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
.focus-card { padding: 14px; border-radius: 12px; background: var(--el-fill-color-light); }
.focus-card__title { font-weight: 600; color: var(--el-text-color-primary); }
.focus-card__meta { margin-top: 6px; font-size: 12px; color: var(--el-color-primary); }
.focus-card__desc { margin-top: 8px; line-height: 1.7; color: var(--el-text-color-secondary); }
</style>
