<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="学生">
        <el-select v-model="queryParams.userId" filterable clearable style="width: 220px">
          <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" filterable clearable style="width: 220px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="掌握状态">
        <el-select v-model="queryParams.masteryStatus" clearable style="width: 160px">
          <el-option label="未掌握" value="0" />
          <el-option label="已掌握" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="reloadAll">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="mb16">
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">错题数</div><div class="metric-value">{{ overview.totalWrongCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">涉及用户</div><div class="metric-value">{{ overview.affectedUserCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">未掌握</div><div class="metric-value warning">{{ overview.unmasteredCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">已掌握</div><div class="metric-value success">{{ overview.masteredCount || 0 }}</div></el-card></el-col>
    </el-row>

    <el-row :gutter="16" class="mb16">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span>按题型统计</span></template>
          <el-table :data="overview.questionTypeStats || []" size="small" border>
            <el-table-column label="题型" min-width="120">
              <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
            </el-table-column>
            <el-table-column label="错题数" prop="wrongQuestionCount" width="100" />
            <el-table-column label="累计错误" prop="wrongTimes" width="100" />
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
            <el-table-column label="错题数" prop="wrongQuestionCount" width="100" />
            <el-table-column label="累计错误" prop="wrongTimes" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="mb16">
      <template #header><span>高频错题 Top5</span></template>
      <el-table :data="overview.topWrongQuestions || []" size="small" border>
        <el-table-column label="错题记录" width="120">
          <template #default="scope">#{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="题目" min-width="160">
          <template #default="scope">
            <div class="readable-cell">
              <strong>{{ scope.row.stem || `题目 ${scope.row.questionId}` }}</strong>
              <span>ID：{{ scope.row.questionId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="题型" min-width="120">
          <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
        </el-table-column>
        <el-table-column label="错误次数" prop="wrongCount" width="100" />
        <el-table-column label="题干" prop="stem" min-width="320" show-overflow-tooltip />
      </el-table>
    </el-card>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="MagicStick" :disabled="!selectedWrongIds.length" @click="handleRetry">错题回练</el-button></el-col>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="记录ID" prop="id" width="90" />
      <el-table-column label="学生" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          <div class="readable-cell">
            <strong>{{ userLabel(scope.row.userId) }}</strong>
            <span>ID：{{ scope.row.userId }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="课程" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          <div class="readable-cell">
            <strong>{{ courseLabel(scope.row.courseId) }}</strong>
            <span>ID：{{ scope.row.courseId }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="题目" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <div class="readable-cell">
            <strong>{{ scope.row.stem || `题目 ${scope.row.questionId}` }}</strong>
            <span>ID：{{ scope.row.questionId }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="题型" width="100">
        <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
      </el-table-column>
      <el-table-column label="错误次数" prop="wrongCount" width="100" />
      <el-table-column label="掌握状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.masteryStatus === '1' ? 'success' : 'warning'">{{ scope.row.masteryStatus === '1' ? '已掌握' : '未掌握' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最后做错时间" prop="lastWrongTime" min-width="180" />
      <el-table-column label="题干" prop="stem" min-width="280" show-overflow-tooltip />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleViewDetail(scope.row)">详情</el-button>
          <el-button link type="primary" icon="MagicStick" @click="handleRetry(scope.row)">回练</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="detailOpen" title="错题详情" width="980px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="题型">{{ questionTypeLabel(wrongDetail.wrong?.questionType) }}</el-descriptions-item>
        <el-descriptions-item label="错误次数">{{ wrongDetail.wrong?.wrongCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="题干" :span="2">{{ wrongDetail.question?.stem || wrongDetail.wrong?.stem || '-' }}</el-descriptions-item>
        <el-descriptions-item label="答案" :span="2">{{ wrongDetail.question?.answer || wrongDetail.wrong?.answer || '-' }}</el-descriptions-item>
        <el-descriptions-item label="解析" :span="2">{{ wrongDetail.question?.analysis || wrongDetail.wrong?.analysis || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="retryOpen" title="错题回练" width="860px">
      <el-form :model="retryForm" label-width="100px">
        <el-form-item label="筛题模式">
          <el-radio-group v-model="retryForm.pickMode">
            <el-radio-button value="selected">已选错题</el-radio-button>
            <el-radio-button value="smart">智能筛题</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="试卷名称"><el-input v-model="retryForm.paperName" /></el-form-item>
        <el-form-item label="课程ID"><el-input v-model="retryForm.courseId" /></el-form-item>
        <el-form-item v-if="retryForm.pickMode === 'smart'" label="知识点ID"><el-input v-model="retryForm.smartKnowledgePointId" placeholder="按知识点筛题，可留空" /></el-form-item>
        <el-form-item v-if="retryForm.pickMode === 'smart'" label="最少错次"><el-input-number v-model="retryForm.minWrongCount" :min="1" :max="100" /></el-form-item>
        <el-form-item label="时长"><el-input-number v-model="retryForm.durationMinutes" :min="5" :max="180" /></el-form-item>
        <el-form-item label="保存试卷"><el-switch v-model="retryForm.savePaper" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="retryOpen = false">取消</el-button>
        <el-button type="primary" :loading="retryLoading" @click="submitRetry">生成回练卷</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="retryResultOpen" title="回练卷预览" width="1080px">
      <div class="detail-header">
        <span>试卷名称：{{ retryResult.paperName || '-' }}</span>
        <span>总分：{{ retryResult.totalScore || 0 }}</span>
        <span>及格线：{{ retryResult.passScore || 0 }}</span>
        <span>时长：{{ retryResult.durationMinutes || 0 }} 分钟</span>
        <span>题数：{{ retryResult.questions?.length || 0 }} 题</span>
      </div>
      <el-table :data="retryResult.questions || []" border class="mt16">
        <el-table-column label="排序" prop="sortNo" width="90" />
        <el-table-column label="题目ID" prop="questionId" width="90" />
        <el-table-column label="题型" min-width="100">
          <template #default="scope">{{ questionTypeLabel(scope.row.question?.questionType || scope.row.questionType) }}</template>
        </el-table-column>
        <el-table-column label="分值" prop="score" width="90" />
        <el-table-column label="题干" min-width="360" show-overflow-tooltip>
          <template #default="scope">{{ scope.row.question?.stem || scope.row.stem || '-' }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createWrongRetryPaper, getWrongDetail, getWrongOverview, listWrong } from '@/api/campus/exam'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'

const loading = ref(false)
const total = ref(0)
const dataList = ref<any[]>([])
const overview = ref<any>({})
const detailOpen = ref(false)
const wrongDetail = ref<any>({})
const selectedWrongIds = ref<number[]>([])
const retryOpen = ref(false)
const retryLoading = ref(false)
const retryResultOpen = ref(false)
const retryResult = ref<any>({})
const userOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  userId: undefined,
  courseId: undefined,
  masteryStatus: '',
})

const retryForm = reactive<any>({
  wrongIds: [] as number[],
  courseId: undefined,
  paperName: '',
  durationMinutes: 45,
  savePaper: true,
  pickMode: 'selected',
  smartKnowledgePointId: undefined,
  minWrongCount: 2,
})

function questionTypeLabel(type: string) {
  return ({ single: '单选题', multiple: '多选题', judge: '判断题', fill: '填空题', essay: '简答题', material: '材料题', case: '案例题' } as any)[type] || type || '-'
}

function userLabel(userId: number | string | undefined) {
  if (!userId) return '-'
  const matched = userOptions.value.find((item: any) => String(item.value) === String(userId))
  return matched?.label || `用户 ${userId}`
}

function courseLabel(courseId: number | string | undefined) {
  if (!courseId) return '-'
  const matched = courseOptions.value.find((item: any) => String(item.value) === String(courseId))
  return matched?.label || `课程 ${courseId}`
}

function buildRetryPaperName(courseId?: number | string) {
  const matchedCourse = courseOptions.value.find((item: any) => String(item.value) === String(courseId || ''))
  const coursePrefix = matchedCourse?.label ? `${matchedCourse.label}-` : ''
  const now = new Date()
  const month = `${now.getMonth() + 1}`.padStart(2, '0')
  const day = `${now.getDate()}`.padStart(2, '0')
  return `${coursePrefix}错题回练卷-${now.getFullYear()}-${month}-${day}`
}

async function getList() {
  loading.value = true
  try {
    const res = await listWrong(queryParams)
    dataList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function loadOverview() {
  const res = await getWrongOverview({
    userId: queryParams.userId,
    courseId: queryParams.courseId,
    masteryStatus: queryParams.masteryStatus,
  })
  overview.value = res.data || {}
}

async function reloadAll() {
  await Promise.all([getList(), loadOverview()])
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.userId = undefined
  queryParams.courseId = undefined
  queryParams.masteryStatus = ''
  reloadAll()
}

function handleSelectionChange(selection: any[]) {
  selectedWrongIds.value = selection.map((item) => item.id)
}

async function handleViewDetail(row: any) {
  const res = await getWrongDetail(row.id)
  wrongDetail.value = res.data || {}
  detailOpen.value = true
}

function handleRetry(row?: any) {
  const wrongIds = row?.id ? [row.id] : selectedWrongIds.value
  if (!wrongIds.length) {
    ElMessage.warning('请先选择错题')
    return
  }
  retryForm.wrongIds = wrongIds
  retryForm.courseId = row?.courseId || queryParams.courseId
  retryForm.paperName = buildRetryPaperName(retryForm.courseId)
  retryForm.pickMode = 'selected'
  retryOpen.value = true
}

async function submitRetry() {
  retryLoading.value = true
  try {
    const payload = { ...retryForm }
    if (retryForm.pickMode === 'smart') {
      const matched = dataList.value.filter((item: any) => {
        const kpMatch = !retryForm.smartKnowledgePointId || String(item.knowledgePointId || '') === String(retryForm.smartKnowledgePointId)
        const countMatch = Number(item.wrongCount || 0) >= Number(retryForm.minWrongCount || 1)
        return kpMatch && countMatch
      })
      payload.wrongIds = matched.map((item: any) => item.id)
      if (!payload.wrongIds.length) {
        ElMessage.warning('当前筛选条件下没有可回练的错题')
        retryLoading.value = false
        return
      }
    }
    const res = await createWrongRetryPaper(payload)
    retryResult.value = res.data || {}
    retryOpen.value = false
    retryResultOpen.value = true
    ElMessage.success(retryForm.savePaper ? '回练卷已生成并保存' : '回练卷预览已生成')
  } finally {
    retryLoading.value = false
  }
}

onMounted(async () => {
  userOptions.value = await fetchUserOptions('student')
  courseOptions.value = await fetchCourseOptions()
  await reloadAll()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.mt16 { margin-top: 16px; }
.metric-label { color: var(--el-text-color-secondary); }
.metric-value { margin-top: 8px; font-size: 28px; font-weight: 700; color: var(--el-color-primary); }
.metric-value.warning { color: var(--el-color-warning); }
.metric-value.success { color: var(--el-color-success); }
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
.detail-header {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  font-weight: 600;
  color: var(--el-text-color-primary);
}
</style>
