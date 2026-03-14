<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="toolbar">
      <el-form-item label="用户">
        <el-select v-model="queryParams.userId" filterable clearable placeholder="请选择用户" style="width: 240px">
          <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" filterable clearable placeholder="请选择课程" style="width: 240px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button type="warning" plain icon="Bell" @click="handleBuild">构建预警</el-button>
        <el-button plain icon="DataAnalysis" @click="openDiagnosis">查看诊断</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="summary-row">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="summary-label">预警总数</div>
          <div class="summary-value">{{ total }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="summary-label">待处理</div>
          <div class="summary-value warning">{{ pendingCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="summary-label">已处理</div>
          <div class="summary-value success">{{ processedCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="warningList">
      <el-table-column label="预警ID" prop="warningId" width="90" />
      <el-table-column label="用户ID" prop="userId" width="90" />
      <el-table-column label="课程ID" prop="courseId" width="90" />
      <el-table-column label="预警类型" prop="warningType" width="140" />
      <el-table-column label="预警等级" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.warningLevel === 'HIGH' ? 'danger' : 'warning'">
            {{ labelOf(meta.warningLevels, scope.row.warningLevel) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="预警内容" prop="warningContent" min-width="260" show-overflow-tooltip />
      <el-table-column label="干预建议" prop="suggestion" min-width="260" show-overflow-tooltip />
      <el-table-column label="处理状态" width="110">
        <template #default="scope">
          <el-tag :type="scope.row.processStatus === 'PROCESSED' ? 'success' : 'info'">
            {{ labelOf(meta.warningProcessStatuses, scope.row.processStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" min-width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button v-if="scope.row.processStatus !== 'PROCESSED'" type="success" link @click="handleProcess(scope.row, 'PROCESSED')">标记已处理</el-button>
          <el-button v-if="scope.row.processStatus !== 'IGNORED'" type="warning" link @click="handleProcess(scope.row, 'IGNORED')">忽略</el-button>
          <el-button type="primary" link @click="openDiagnosis(scope.row)">诊断</el-button>
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

    <el-drawer v-model="diagnosisVisible" title="学情诊断" size="52%">
      <div v-loading="diagnosisLoading" class="diagnosis-wrapper">
        <template v-if="diagnosis">
          <el-alert :title="diagnosis.overallSummary" type="info" :closable="false" />
          <el-row :gutter="12" class="summary-row">
            <el-col :span="8"><el-card shadow="never"><div class="summary-label">风险等级</div><div class="summary-value">{{ labelOf(meta.riskLevels, diagnosis.riskLevel) }}</div></el-card></el-col>
            <el-col :span="8"><el-card shadow="never"><div class="summary-label">平均进度</div><div class="summary-value">{{ diagnosis.avgProgressRate ?? 0 }}</div></el-card></el-col>
            <el-col :span="8"><el-card shadow="never"><div class="summary-label">平均正确率</div><div class="summary-value">{{ diagnosis.avgCorrectRate ?? 0 }}</div></el-card></el-col>
          </el-row>
          <el-card shadow="never" class="section-card">
            <template #header>风险标签</template>
            <el-space wrap>
              <el-tag v-for="item in diagnosis.riskTags || []" :key="item" type="danger">{{ item }}</el-tag>
              <el-empty v-if="!(diagnosis.riskTags || []).length" description="暂无风险标签" />
            </el-space>
          </el-card>
          <el-card shadow="never" class="section-card">
            <template #header>建议动作</template>
            <ul class="simple-list">
              <li v-for="item in diagnosis.actionSuggestions || []" :key="item">{{ item }}</li>
            </ul>
          </el-card>
          <el-card shadow="never" class="section-card">
            <template #header>最新预警</template>
            <div v-if="diagnosis.latestWarning">
              <div>等级：{{ labelOf(meta.warningLevels, diagnosis.latestWarning.warningLevel) }}</div>
              <div class="mt8">内容：{{ diagnosis.latestWarning.warningContent }}</div>
              <div class="mt8">建议：{{ diagnosis.latestWarning.suggestion }}</div>
            </div>
            <el-empty v-else description="暂无预警" />
          </el-card>
        </template>
        <el-empty v-else description="请选择用户和课程后查看诊断" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { buildLearningWarning, getAnalysisMeta, getLearningDiagnosis, listLearningWarning, processLearningWarning } from '@/api/campus/analysis'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'

const loading = ref(false)
const total = ref(0)
const warningList = ref<any[]>([])
const userOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const diagnosisVisible = ref(false)
const diagnosisLoading = ref(false)
const diagnosis = ref<any>(null)
const meta = reactive<any>({ riskLevels: {}, warningProcessStatuses: {}, warningLevels: {} })
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, userId: undefined, courseId: undefined })

const pendingCount = computed(() => warningList.value.filter((item: any) => item.processStatus === 'PENDING').length)
const processedCount = computed(() => warningList.value.filter((item: any) => item.processStatus === 'PROCESSED').length)

function labelOf(map: Record<string, string>, value: string) {
  return map?.[value] || value || '--'
}

async function getList() {
  loading.value = true
  try {
    const res = await listLearningWarning(queryParams)
    warningList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function handleBuild() {
  if (!queryParams.userId || !queryParams.courseId) {
    ElMessage.warning('请先选择用户和课程')
    return
  }
  await buildLearningWarning({ userId: queryParams.userId, courseId: queryParams.courseId })
  ElMessage.success('预警构建成功')
  getList()
}

async function handleProcess(row: any, processStatus: string) {
  await ElMessageBox.confirm(`确认将该预警标记为${labelOf(meta.warningProcessStatuses, processStatus)}吗？`, '提示', { type: 'warning' })
  await processLearningWarning(row.warningId, processStatus)
  ElMessage.success('处理成功')
  getList()
}

async function openDiagnosis(row?: any) {
  const userId = row?.userId || queryParams.userId
  const courseId = row?.courseId || queryParams.courseId
  if (!userId || !courseId) {
    ElMessage.warning('请先选择用户和课程')
    return
  }
  diagnosisVisible.value = true
  diagnosisLoading.value = true
  try {
    const res = await getLearningDiagnosis({ userId, courseId, recommendLimit: 5, autoGenerate: true })
    diagnosis.value = res.data || null
  } finally {
    diagnosisLoading.value = false
  }
}

async function loadOptions() {
  userOptions.value = await fetchUserOptions()
  courseOptions.value = await fetchCourseOptions()
}

async function loadMeta() {
  const res = await getAnalysisMeta()
  Object.assign(meta, res.data || {})
}

onMounted(async () => {
  await Promise.all([loadOptions(), loadMeta()])
  getList()
})
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
.summary-row { margin-bottom: 16px; }
.summary-label { color: var(--el-text-color-secondary); }
.summary-value { margin-top: 8px; font-size: 28px; font-weight: 700; color: var(--el-color-primary); }
.summary-value.warning { color: var(--el-color-warning); }
.summary-value.success { color: var(--el-color-success); }
.section-card { margin-top: 16px; }
.simple-list { margin: 0; padding-left: 18px; line-height: 1.9; }
.mt8 { margin-top: 8px; }
</style>
