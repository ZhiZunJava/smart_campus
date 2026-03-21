<template>
  <div class="app-container">
    <el-alert
      type="info"
      :closable="false"
      class="mb16"
      title="题目选项建议在“题库管理”中统一维护；此页面仅用于只读查看和排查。"
    />

    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="题目ID">
        <el-input v-model="queryParams.questionId" style="width: 180px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList" border>
      <el-table-column label="选项ID" prop="optionId" width="90" />
      <el-table-column label="题目信息" min-width="420">
        <template #default="scope">
          <div class="question-cell">
            <div class="question-cell__title">
              {{ scope.row.questionTitle || `题目 ${scope.row.questionId}` }}
            </div>
            <div class="question-cell__meta">
              <span>题目ID：{{ scope.row.questionId }}</span>
              <span>题型：{{ questionTypeLabel(scope.row.questionType) }}</span>
              <span>答案：{{ scope.row.questionAnswer || '-' }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="选项标识" prop="optionKey" width="100" />
      <el-table-column label="选项内容" prop="optionContent" min-width="320" show-overflow-tooltip />
      <el-table-column label="正确项" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isRight === '1' ? 'success' : 'info'">{{ scope.row.isRight === '1' ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button link type="primary" icon="View" @click="handleViewQuestion(scope.row)">题目详情</el-button>
            <el-button link type="success" icon="Position" @click="jumpToQuestion(scope.row)">前往题库</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="detailOpen" title="题目详情" width="920px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="题型">{{ questionTypeLabel(questionDetail.questionType) }}</el-descriptions-item>
        <el-descriptions-item label="答案">{{ questionDetail.answer || '-' }}</el-descriptions-item>
        <el-descriptions-item label="题干" :span="2">{{ questionDetail.questionTitle || '-' }}</el-descriptions-item>
        <el-descriptions-item label="解析" :span="2">{{ questionDetail.analysis || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="questionDetail.options || []" border class="mt16">
        <el-table-column label="标识" prop="optionKey" width="90" />
        <el-table-column label="内容" prop="optionContent" min-width="260" />
        <el-table-column label="正确项" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isRight === '1' ? 'success' : 'info'">{{ scope.row.isRight === '1' ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listOption, getQuestionDetail } from '@/api/campus/exam'

const loading = ref(false)
const total = ref(0)
const dataList = ref<any[]>([])
const detailOpen = ref(false)
const questionDetail = ref<any>({})
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, questionId: undefined })
const router = useRouter()

function questionTypeLabel(type: string) {
  return ({ single: '单选题', multiple: '多选题', judge: '判断题', fill: '填空题', essay: '简答题', material: '材料题', case: '案例题' } as any)[type] || type || '-'
}

function stripHtml(value: string) {
  return String(value || '').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

async function getList() {
  loading.value = true
  try {
    const res = await listOption(queryParams)
    const rows = res.rows || []
    const questionIds = Array.from(new Set(rows.map((item: any) => item.questionId).filter(Boolean))) as number[]
    const detailList = await Promise.all(questionIds.map((questionId: number) => getQuestionDetail(questionId).catch(() => ({ data: null }))))
    const detailMap = new Map<number, any>()
    questionIds.forEach((questionId, index) => {
      detailMap.set(questionId, detailList[index]?.data || null)
    })
    dataList.value = rows.map((item: any) => {
      const detail = detailMap.get(item.questionId)
      return {
        ...item,
        questionType: detail?.questionType,
        questionAnswer: detail?.answer,
        questionTitle: stripHtml(detail?.stem),
      }
    })
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.questionId = undefined
  getList()
}

async function handleViewQuestion(row: any) {
  const res = await getQuestionDetail(row.questionId)
  const data = res.data || {}
  questionDetail.value = {
    ...data,
    questionTitle: stripHtml(data.stem),
  }
  detailOpen.value = true
}

function jumpToQuestion(row: any) {
  router.push({
    path: '/campus/exam/questionBank',
    query: { openQuestionId: String(row.questionId) },
  })
}

getList()
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.mt16 { margin-top: 16px; }
.question-cell__title {
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.6;
}
.question-cell__meta {
  margin-top: 4px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.table-actions {
  display: flex;
  flex-wrap: nowrap;
  gap: 8px;
  white-space: nowrap;
}
</style>
