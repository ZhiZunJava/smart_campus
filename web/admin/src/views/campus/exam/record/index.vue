<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="学生">
        <el-select v-model="queryParams.userId" filterable clearable style="width: 220px">
          <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="试卷ID"><el-input v-model="queryParams.paperId" style="width: 180px" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.examStatus" clearable style="width: 160px">
          <el-option label="进行中" value="ONGOING" />
          <el-option label="已提交" value="SUBMITTED" />
        </el-select>
      </el-form-item>
      <el-form-item label="行为等级">
        <el-select v-model="queryParams.warningLevel" clearable style="width: 160px">
          <el-option label="低风险" value="LOW" />
          <el-option label="中风险" value="MEDIUM" />
          <el-option label="高风险" value="HIGH" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="reloadAll">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="mb16">
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">考试记录数</div><div class="metric-value">{{ overview.totalRecordCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">已提交</div><div class="metric-value success">{{ overview.submittedCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">平均得分</div><div class="metric-value">{{ overview.avgScore || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">平均正确率</div><div class="metric-value warning">{{ overview.avgCorrectRate || 0 }}%</div></el-card></el-col>
    </el-row>

    <el-row :gutter="16" class="mb16">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span>按题型统计</span></template>
          <el-table :data="overview.questionTypeStats || []" size="small" border>
            <el-table-column label="题型" min-width="120">
              <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
            </el-table-column>
            <el-table-column label="总题数" prop="totalCount" width="100" />
            <el-table-column label="答对数" prop="correctCount" width="100" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span>按知识点统计</span></template>
          <el-table :data="overview.knowledgePointStats || []" size="small" border>
            <el-table-column label="知识点" min-width="160">
              <template #default="scope">{{ scope.row.knowledgePointName || `知识点 ${scope.row.knowledgePointId}` }}</template>
            </el-table-column>
            <el-table-column label="总题数" prop="totalCount" width="100" />
            <el-table-column label="答对数" prop="correctCount" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="mb16">
      <template #header><span>高频失分题</span></template>
      <el-table :data="overview.topWrongQuestions || []" size="small" border>
        <el-table-column label="题目ID" prop="questionId" width="90" />
        <el-table-column label="题型" min-width="120">
          <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
        </el-table-column>
        <el-table-column label="失分次数" prop="wrongCount" width="100" />
        <el-table-column label="题干" prop="stem" min-width="320" show-overflow-tooltip />
      </el-table>
    </el-card>

      <el-table v-loading="loading" :data="filteredRecordList" :row-class-name="recordRowClassName">
        <el-table-column label="记录ID" prop="recordId" width="100" />
        <el-table-column label="试卷" min-width="220" show-overflow-tooltip>
          <template #default="scope">
            <div class="readable-cell">
              <strong>{{ scope.row.paperName || `试卷 ${scope.row.paperId}` }}</strong>
              <span>ID：{{ scope.row.paperId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="学生" min-width="180" show-overflow-tooltip>
          <template #default="scope">
            <div class="readable-cell">
              <strong>{{ userLabel(scope.row.userId) }}</strong>
              <span>ID：{{ scope.row.userId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="得分" prop="score" width="100" />
        <el-table-column label="正确率" prop="correctRate" width="100" />
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.examStatus === 'SUBMITTED' ? 'success' : 'warning'">
              {{ examStatusLabel(scope.row.examStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="切屏次数" width="100">
          <template #default="scope">{{ analysisSummary(scope.row).focusLossCount }}</template>
        </el-table-column>
        <el-table-column label="待检查题" width="100">
          <template #default="scope">{{ analysisSummary(scope.row).flaggedCount }}</template>
        </el-table-column>
        <el-table-column label="行为等级" width="100">
          <template #default="scope">
            <el-tag :type="analysisLevelTag(analysisSummary(scope.row).warningLevel)">
              {{ warningLevelLabel(analysisSummary(scope.row).warningLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="开始时间" prop="startTime" min-width="180" />
        <el-table-column label="提交时间" prop="submitTime" min-width="180" />
        <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleViewDetail(scope.row)">答题详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="detailOpen" title="考试记录详情" width="1100px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="试卷">{{ recordDetail.record?.paperName || recordDetail.record?.paperId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="学生">{{ userLabel(recordDetail.record?.userId) }}</el-descriptions-item>
        <el-descriptions-item label="得分">{{ recordDetail.record?.score || 0 }}</el-descriptions-item>
        <el-descriptions-item label="正确率">{{ recordDetail.record?.correctRate || 0 }}%</el-descriptions-item>
      </el-descriptions>

      <el-row :gutter="16" class="mt16">
        <el-col :span="12">
          <el-card shadow="never">
            <template #header><span>本次按题型统计</span></template>
            <el-table :data="recordDetail.questionTypeStats || []" size="small" border>
              <el-table-column label="题型">
                <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
              </el-table-column>
              <el-table-column label="总题数" prop="totalCount" width="100" />
              <el-table-column label="答对数" prop="correctCount" width="100" />
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never">
            <template #header><span>本次按知识点统计</span></template>
            <el-table :data="recordDetail.knowledgePointStats || []" size="small" border>
              <el-table-column label="知识点" min-width="160">
                <template #default="scope">{{ scope.row.knowledgePointName || `知识点 ${scope.row.knowledgePointId}` }}</template>
              </el-table-column>
              <el-table-column label="总题数" prop="totalCount" width="100" />
              <el-table-column label="答对数" prop="correctCount" width="100" />
            </el-table>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="mt16">
        <el-col :span="24">
          <el-card shadow="never">
            <template #header><span>考试行为分析</span></template>
            <el-descriptions :column="4" border>
              <el-descriptions-item label="切屏次数">{{ detailAnalysis.focusLossCount }}</el-descriptions-item>
              <el-descriptions-item label="待检查题">{{ detailAnalysis.flaggedCount }}</el-descriptions-item>
              <el-descriptions-item label="行为等级">
                <el-tag :type="analysisLevelTag(detailAnalysis.warningLevel)">{{ warningLevelLabel(detailAnalysis.warningLevel) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="答题概览">
                总题 {{ detailAnalysis.total }} / 正确 {{ detailAnalysis.correct }}
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="mt16">
        <el-col :span="24">
          <el-card shadow="never">
            <template #header><span>行为日志</span></template>
            <el-table :data="parsedBehaviorLogs" size="small" border>
              <el-table-column label="时间" min-width="180">
                <template #default="scope">{{ scope.row.behaviorTime || '-' }}</template>
              </el-table-column>
              <el-table-column label="事件" width="120">
                <template #default="scope">{{ behaviorTypeLabel(scope.row.behaviorType) }}</template>
              </el-table-column>
              <el-table-column label="次数" width="90" prop="behaviorCount" />
              <el-table-column label="来源事件" width="140">
                <template #default="scope">{{ scope.row.parsedData.sourceEvent || '-' }}</template>
              </el-table-column>
              <el-table-column label="累计切屏" width="110">
                <template #default="scope">{{ scope.row.parsedData.focusLossCount ?? '-' }}</template>
              </el-table-column>
              <el-table-column label="发生时间戳" min-width="180">
                <template #default="scope">{{ scope.row.parsedData.occurredAt || '-' }}</template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>

      <el-table :data="recordDetail.answers || []" border class="mt16">
        <el-table-column label="题目ID" prop="questionId" width="90" />
        <el-table-column label="题型" width="100">
          <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
        </el-table-column>
        <el-table-column label="题干" prop="stem" min-width="280" show-overflow-tooltip />
        <el-table-column label="用户答案" prop="userAnswer" min-width="180" show-overflow-tooltip />
        <el-table-column label="标准答案" prop="standardAnswer" min-width="180" show-overflow-tooltip />
        <el-table-column label="是否正确" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isCorrect === '1' ? 'success' : 'danger'">{{ scope.row.isCorrect === '1' ? '正确' : '错误' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="得分" prop="score" width="90" />
        <el-table-column label="知识点ID" prop="knowledgePointId" width="120" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { getRecordDetail, getRecordOverview, listRecord } from '@/api/campus/exam'
import { fetchUserOptions } from '@/api/campus/options'

const loading = ref(false)
const total = ref(0)
const recordList = ref<any[]>([])
const detailOpen = ref(false)
const overview = ref<any>({})
const recordDetail = ref<any>({})
const userOptions = ref<any[]>([])

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  userId: undefined,
  paperId: undefined,
  examStatus: '',
  warningLevel: '',
})
const parsedBehaviorLogs = computed(() =>
  (recordDetail.value.behaviorLogs || []).map((item: any) => ({
    ...item,
    parsedData: parseBehaviorData(item?.behaviorData),
  })),
)

function questionTypeLabel(type: string) {
  return ({ single: '单选题', multiple: '多选题', judge: '判断题', fill: '填空题', essay: '简答题', material: '材料题', case: '案例题' } as any)[type] || type || '-'
}

function examStatusLabel(status: string) {
  return ({ ONGOING: '进行中', SUBMITTED: '已提交' } as any)[status] || status || '-'
}

function parseAnalysisJson(value?: string) {
  if (!value) {
    return { total: 0, correct: 0, score: 0, focusLossCount: 0, flaggedCount: 0, warningLevel: 'LOW' }
  }
  try {
    return {
      total: 0,
      correct: 0,
      score: 0,
      focusLossCount: 0,
      flaggedCount: 0,
      warningLevel: 'LOW',
      ...JSON.parse(value),
    }
  } catch {
    return { total: 0, correct: 0, score: 0, focusLossCount: 0, flaggedCount: 0, warningLevel: 'LOW' }
  }
}

function analysisSummary(row: any) {
  return parseAnalysisJson(row?.analysisJson)
}

function analysisLevelTag(level: string) {
  if (level === 'HIGH') return 'danger'
  if (level === 'MEDIUM') return 'warning'
  return 'success'
}

function warningLevelLabel(level: string) {
  return ({ LOW: '低风险', MEDIUM: '中风险', HIGH: '高风险' } as any)[level] || level || '-'
}

function behaviorTypeLabel(type: string) {
  return ({ FOCUS_LOSS: '切屏', FLAGGED: '标记', MANUAL_SUBMIT: '交卷', AUTO_SAVE: '暂存' } as any)[type] || type || '-'
}

function parseBehaviorData(value?: string) {
  if (!value) return {}
  try {
    return JSON.parse(value)
  } catch {
    return {}
  }
}

function userLabel(userId: number | string | undefined) {
  if (!userId) return '-'
  const matched = userOptions.value.find((item: any) => String(item.value) === String(userId))
  return matched?.label || `用户 ${userId}`
}

function recordRowClassName({ row }: any) {
  const level = analysisSummary(row).warningLevel
  if (level === 'HIGH') return 'record-row-high'
  if (level === 'MEDIUM') return 'record-row-medium'
  return ''
}

const detailAnalysis = computed(() => parseAnalysisJson(recordDetail.value?.record?.analysisJson))
const filteredRecordList = computed(() =>
  (recordList.value || []).filter((item: any) => {
    if (!queryParams.warningLevel) return true
    return analysisSummary(item).warningLevel === queryParams.warningLevel
  }),
)

async function getList() {
  loading.value = true
  try {
    const res = await listRecord(queryParams)
    recordList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function loadOverview() {
  const res = await getRecordOverview({
    userId: queryParams.userId,
    paperId: queryParams.paperId,
    examStatus: queryParams.examStatus,
  })
  overview.value = res.data || {}
}

async function reloadAll() {
  await Promise.all([getList(), loadOverview()])
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.userId = undefined
  queryParams.paperId = undefined
  queryParams.examStatus = ''
  queryParams.warningLevel = ''
  reloadAll()
}

async function handleViewDetail(row: any) {
  const res = await getRecordDetail(row.recordId)
  recordDetail.value = res.data || {}
  detailOpen.value = true
}

onMounted(async () => {
  userOptions.value = await fetchUserOptions('student')
  await reloadAll()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.mt16 { margin-top: 16px; }
.metric-label { color: var(--el-text-color-secondary); }
.metric-value { margin-top: 8px; font-size: 28px; font-weight: 700; color: var(--el-color-primary); }
.metric-value.success { color: var(--el-color-success); }
.metric-value.warning { color: var(--el-color-warning); }
.readable-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.readable-cell strong {
  color: var(--el-text-color-primary);
  font-weight: 600;
}
.readable-cell span {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
:deep(.record-row-high) {
  --el-table-tr-bg-color: #fff1f2;
}
:deep(.record-row-medium) {
  --el-table-tr-bg-color: #fffbeb;
}
</style>
