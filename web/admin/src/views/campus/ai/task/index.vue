<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="查看视角">
        <el-radio-group v-model="viewMode" @change="handleViewModeChange">
          <el-radio-button value="business">业务任务</el-radio-button>
          <el-radio-button value="all">全部日志</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="业务类型">
        <el-select v-model="queryParams.bizType" clearable style="width:220px" placeholder="请选择业务类型">
          <el-option v-for="item in visibleBizTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="模型">
        <el-select v-model="queryParams.modelId" filterable clearable style="width:260px" placeholder="请选择模型">
          <el-option v-for="item in modelOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程">
        <el-select v-model="businessFilters.courseId" filterable clearable style="width:220px" placeholder="请选择课程">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="题型">
        <el-select v-model="businessFilters.questionType" clearable style="width:180px" placeholder="请选择题型">
          <el-option v-for="item in questionTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="任务状态">
        <el-select v-model="queryParams.taskStatus" clearable style="width:180px" placeholder="请选择状态">
          <el-option label="成功" value="SUCCESS" />
          <el-option label="失败" value="FAILED" />
          <el-option label="运行中" value="RUNNING" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetFilters">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="任务ID" prop="taskId" width="90" />
      <el-table-column label="业务类型" width="160">
        <template #default="scope">{{ bizTypeLabel(scope.row.bizType) }}</template>
      </el-table-column>
      <el-table-column label="业务对象" min-width="240" show-overflow-tooltip>
        <template #default="scope">{{ bizObjectLabel(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="任务摘要" min-width="240" show-overflow-tooltip>
        <template #default="scope">{{ taskSummary(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="模型名称" prop="modelName" min-width="180">
        <template #default="scope">{{ scope.row.modelName || `模型#${scope.row.modelId}` }}</template>
      </el-table-column>
      <el-table-column label="Token" prop="tokenUsed" width="90" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.taskStatus === 'SUCCESS' ? 'success' : scope.row.taskStatus === 'RUNNING' ? 'warning' : 'danger'">{{ taskStatusLabel(scope.row.taskStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="耗时(ms)" prop="durationMs" width="100" />
      <el-table-column label="错误信息" prop="errorMsg" min-width="220" show-overflow-tooltip />
      <el-table-column label="创建时间" prop="createTime" min-width="180" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button>
            <el-button
              v-if="scope.row.bizType === 'exam_question_generate_async'"
              link
              type="warning"
              :disabled="scope.row.taskStatus !== 'FAILED'"
              @click="retryExamAiTask(scope.row)"
            >
              重试
            </el-button>
          </template>
        </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="detailOpen" title="任务日志详情" width="980px">
      <el-descriptions :column="2" border class="detail-summary">
        <el-descriptions-item label="任务ID">{{ detail.taskId }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ taskStatusLabel(detail.taskStatus) }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ bizTypeLabel(detail.bizType) }}</el-descriptions-item>
        <el-descriptions-item label="业务对象" :span="2">{{ bizObjectLabel(detail) }}</el-descriptions-item>
        <el-descriptions-item label="模型名称">{{ detail.modelName || `模型#${detail.modelId || '-'}` }}</el-descriptions-item>
        <el-descriptions-item label="Token">{{ detail.tokenUsed }}</el-descriptions-item>
        <el-descriptions-item label="耗时(ms)">{{ detail.durationMs }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2">{{ detail.errorMsg || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div v-if="detailTraceRows.length" class="payload-section">
        <div class="payload-title">阶段耗时</div>
        <el-table :data="detailTraceRows" size="small" border>
          <el-table-column label="阶段" prop="phase" min-width="220" />
          <el-table-column label="耗时(ms)" prop="durationMs" width="120" />
        </el-table>
      </div>

      <div class="payload-section">
        <div class="payload-title">请求报文</div>
        <pre class="payload-code"><code>{{ formatPayload(detail.requestPayload) }}</code></pre>
      </div>

      <div class="payload-section payload-section--last">
        <div class="payload-title">响应报文</div>
        <pre class="payload-code"><code>{{ formatPayload(detail.responsePayload) }}</code></pre>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { fetchAiModelOptions, fetchCourseOptions } from '@/api/campus/options'
import { getAiTask, listAiTask } from '@/api/campus/ai'
import { submitAiGenerateQuestionTask } from '@/api/campus/exam'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const total = ref(0)
const dataList = ref<any[]>([])
const detailOpen = ref(false)
const detail = ref<any>({})
const modelOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const viewMode = ref<'business' | 'all'>('business')
const BUSINESS_BIZ_TYPES = ['exam_question_generate_async', 'qa', 'learning_profile_analysis', 'resource_analysis', 'teaching_admin']
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, bizType: '', taskStatus: '', modelId: undefined })
const businessFilters = reactive<any>({ courseId: undefined, questionType: '' })
const bizTypeOptions = [
  { label: 'AI异步出题任务', value: 'exam_question_generate_async' },
  { label: 'AI同步出题', value: 'exam_question_generate' },
  { label: '智能问答', value: 'qa' },
  { label: '学习画像AI分析', value: 'learning_profile_analysis' },
  { label: '资源元数据分析', value: 'resource_analysis' },
  { label: '教学配置助手', value: 'teaching_admin' },
  { label: '模型测试', value: 'model_test' },
]
const questionTypeOptions = [
  { label: '单选题', value: 'single' },
  { label: '多选题', value: 'multiple' },
  { label: '判断题', value: 'judge' },
  { label: '填空题', value: 'fill' },
  { label: '简答题', value: 'essay' },
  { label: '材料题', value: 'material' },
  { label: '案例题', value: 'case' },
]

const visibleBizTypeOptions = computed(() => {
  if (viewMode.value === 'business') {
    return bizTypeOptions.filter((item) => BUSINESS_BIZ_TYPES.includes(item.value))
  }
  return bizTypeOptions
})
const detailTraceRows = computed(() => {
  const payload = parsePayload(detail.value?.responsePayload)
  return Array.isArray(payload?.trace) ? payload.trace : []
})

function bizTypeLabel(value: string) {
  return bizTypeOptions.find((item) => item.value === value)?.label || value || '-'
}

function taskStatusLabel(value: string) {
  const map: Record<string, string> = {
    SUCCESS: '成功',
    FAILED: '失败',
    RUNNING: '运行中',
  }
  return map[value] || value || '-'
}

function parsePayload(payload?: string) {
  if (!payload) return null
  try {
    return JSON.parse(payload)
  } catch {
    return null
  }
}

function truncateText(value: string, limit = 36) {
  const text = String(value || '').replace(/\s+/g, ' ').trim()
  if (!text) return ''
  return text.length > limit ? `${text.slice(0, limit)}...` : text
}

function questionTypeLabel(value: string) {
  const map: Record<string, string> = {
    single: '单选题',
    multiple: '多选题',
    judge: '判断题',
    fill: '填空题',
    essay: '简答题',
    material: '材料题',
    case: '案例题',
  }
  return map[value] || value || '-'
}

function courseLabel(courseId: number | string | undefined) {
  if (!courseId) return '-'
  const matched = courseOptions.value.find((item: any) => String(item.value) === String(courseId))
  return matched?.label || `课程 ${courseId}`
}

function extractLastUserContent(payload: any) {
  const messages = Array.isArray(payload?.messages) ? payload.messages : []
  const reversed = [...messages].reverse()
  const userMessage = reversed.find((item: any) => item?.role === 'user')
  if (!userMessage) return ''
  const content = userMessage.content
  if (typeof content === 'string') return content
  if (Array.isArray(content)) {
    return content
      .map((item: any) => item?.text || '')
      .filter(Boolean)
      .join(' ')
  }
  return ''
}

function bizObjectLabel(row: any) {
  const payload = parsePayload(row?.requestPayload)
  if (row?.bizType === 'exam_question_generate_async') {
    return `异步出题${payload?.courseId ? ` · 课程#${payload.courseId}` : ''}${payload?.questionTypeLabel ? ` · ${payload.questionTypeLabel}` : ''}${payload?.count ? ` · ${payload.count}题` : ''}`
  }
  if (row?.bizType === 'exam_question_generate') {
    const userPayload = parsePayload(extractLastUserContent(payload))
    return `同步出题${userPayload?.courseId ? ` · 课程#${userPayload.courseId}` : ''}${userPayload?.questionType ? ` · ${questionTypeLabel(userPayload.questionType)}` : ''}${userPayload?.count ? ` · ${userPayload.count}题` : ''}`
  }
  if (row?.bizType === 'qa') {
    const question = extractLastUserContent(payload)
    return question ? `智能问答 · ${truncateText(question)}` : `问答会话#${row?.bizId || '-'}`
  }
  if (row?.bizType === 'learning_profile_analysis') {
    const courseId = payload?.courseId ?? payload?.diagnosis?.courseId ?? row?.bizId
    const abilityLevel = payload?.profile?.abilityLevel
    return `学习画像分析${courseId ? ` · 课程#${courseId}` : ''}${abilityLevel ? ` · ${abilityLevel}` : ''}`
  }
  if (row?.bizType === 'resource_analysis') {
    const resourceName = payload?.resourceName
    const resourceType = payload?.resourceType
    return resourceName ? `${resourceName}${resourceType ? ` · ${resourceType}` : ''}` : `资源分析#${row?.bizId || '-'}`
  }
  if (row?.bizType === 'teaching_admin') {
    const requestPayload = parsePayload(row?.requestPayload)
    const moduleLabel = requestPayload?.moduleLabel || requestPayload?.moduleKey
    const actionLabel = requestPayload?.actionLabel || requestPayload?.actionType
    return `${moduleLabel || '教学配置'}${actionLabel ? ` · ${actionLabel}` : ''}`
  }
  if (row?.bizType === 'model_test') {
    return row?.modelName ? `模型测试 · ${row.modelName}` : '模型测试'
  }
  return row?.bizId ? `业务#${row.bizId}` : '-'
}

function taskSummary(row: any) {
  const requestPayload = parsePayload(row?.requestPayload) || {}
  const responsePayload = parsePayload(row?.responsePayload) || {}
  if (row?.bizType === 'exam_question_generate_async') {
    const parts = []
    if (requestPayload?.instruction) {
      parts.push(`要求:${truncateText(requestPayload.instruction, 24)}`)
    }
    const result = responsePayload?.result
    if (result) {
      parts.push(`生成${result.generatedCount || 0}题`)
      parts.push(`入库${result.savedCount || 0}题`)
    } else if (responsePayload?.message) {
      parts.push(truncateText(responsePayload.message, 28))
    }
    return parts.join(' · ') || '-'
  }
  if (row?.bizType === 'qa') {
    const prompt = extractLastUserContent(requestPayload)
    return prompt ? `提问:${truncateText(prompt, 30)}` : '-'
  }
  if (row?.bizType === 'learning_profile_analysis' && requestPayload?.profile?.abilityLevel) {
    return `能力:${requestPayload.profile.abilityLevel}`
  }
  if (row?.bizType === 'resource_analysis' && requestPayload?.resourceType) {
    return `类型:${requestPayload.resourceType}`
  }
  return '-'
}

function resolveTaskCourseId(row: any) {
  const payload = parsePayload(row?.requestPayload) || {}
  if (row?.bizType === 'exam_question_generate_async') {
    return payload?.courseId || row?.bizId
  }
  if (row?.bizType === 'exam_question_generate') {
    const userPayload = parsePayload(extractLastUserContent(payload)) || {}
    return userPayload?.courseId || row?.bizId
  }
  if (row?.bizType === 'learning_profile_analysis') {
    return payload?.courseId ?? payload?.diagnosis?.courseId ?? row?.bizId
  }
  if (row?.bizType === 'resource_analysis') {
    return payload?.courseId ?? row?.bizId
  }
  if (row?.bizType === 'qa') {
    return payload?.courseId
  }
  return row?.bizId
}

function resolveTaskQuestionType(row: any) {
  const payload = parsePayload(row?.requestPayload) || {}
  if (row?.bizType === 'exam_question_generate_async') {
    return payload?.questionType || ''
  }
  if (row?.bizType === 'exam_question_generate') {
    const userPayload = parsePayload(extractLastUserContent(payload)) || {}
    return userPayload?.questionType || ''
  }
  return ''
}

function matchesBusinessFilters(row: any) {
  const courseId = businessFilters.courseId
  const questionType = businessFilters.questionType
  if (courseId && String(resolveTaskCourseId(row)) !== String(courseId)) {
    return false
  }
  if (questionType && resolveTaskQuestionType(row) !== questionType) {
    return false
  }
  return true
}

function handleViewModeChange() {
  queryParams.pageNum = 1
  if (viewMode.value === 'business' && queryParams.bizType && !BUSINESS_BIZ_TYPES.includes(queryParams.bizType)) {
    queryParams.bizType = ''
  }
  getList()
}

function resetFilters() {
  viewMode.value = 'business'
  queryParams.pageNum = 1
  queryParams.bizType = ''
  queryParams.taskStatus = ''
  queryParams.modelId = undefined
  businessFilters.courseId = undefined
  businessFilters.questionType = ''
  getList()
}

function buildListQuery() {
  const params = {
    ...queryParams,
    bizType: queryParams.bizType || undefined,
    bizTypes: undefined as string[] | undefined,
  }
  if (viewMode.value === 'all') {
    return params
  }
  if (params.bizType && BUSINESS_BIZ_TYPES.includes(params.bizType)) {
    return params
  }
  return {
    ...params,
    bizType: undefined,
    bizTypes: BUSINESS_BIZ_TYPES,
  }
}

function buildRetryPayload(row: any) {
  const payload = parsePayload(row?.requestPayload) || {}
  return {
    modelId: payload.modelId,
    courseId: payload.courseId,
    knowledgePointId: payload.knowledgePointId,
    questionType: payload.questionType,
    difficultyLevel: payload.difficultyLevel,
    count: payload.count,
    saveToBank: payload.saveToBank !== false,
    userInstruction: payload.instruction || '',
  }
}

async function getList() {
  loading.value = true
  try {
    const res = await listAiTask(buildListQuery())
    const rows = res.rows || []
    dataList.value = rows.filter((item: any) => matchesBusinessFilters(item))
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function retryExamAiTask(row: any) {
  const payload = buildRetryPayload(row)
  const res = await submitAiGenerateQuestionTask(payload)
  ElMessage.success(`已重新提交，新的任务ID：${res.data?.taskId || '-'}`)
  getList()
}

async function loadModelOptions() {
  modelOptions.value = await fetchAiModelOptions('chat')
}

async function loadCourseOptions() {
  courseOptions.value = await fetchCourseOptions()
}

async function handleView(row: any) {
  const res = await getAiTask(row.taskId)
  detail.value = res.data || {}
  detailOpen.value = true
}

function formatPayload(payload: string) {
  if (!payload) {
    return '-'
  }
  try {
    return JSON.stringify(JSON.parse(payload), null, 2)
  } catch {
    return payload
  }
}

onMounted(async () => {
  await loadCourseOptions()
  await loadModelOptions()
  await getList()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.detail-summary { margin-bottom: 18px; }
.payload-section { margin-top: 18px; }
.payload-section--last { margin-bottom: 4px; }
.payload-title {
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}
.payload-code {
  margin: 0;
  padding: 14px 16px;
  max-height: 260px;
  overflow: auto;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  background: #0f172a;
  color: #e5e7eb;
  font-size: 13px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, Monaco, 'Courier New', monospace;
}
</style>
