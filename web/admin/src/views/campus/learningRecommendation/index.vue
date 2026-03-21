<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="toolbar">
      <el-form-item label="用户">
        <el-select v-model="queryParams.userId" filterable clearable style="width: 240px" placeholder="请选择用户">
          <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="场景">
        <el-select v-model="sceneCode" style="width: 180px">
          <el-option v-for="scene in meta.supportedScenes || []" :key="scene" :label="sceneLabel(scene)" :value="scene" />
        </el-select>
      </el-form-item>
      <el-form-item label="模式">
        <el-radio-group v-model="viewMode">
          <el-radio-button label="active">当前推荐</el-radio-button>
          <el-radio-button label="history">历史记录</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button type="warning" plain icon="MagicStick" @click="handleGenerate">生成推荐</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="summary-row">
      <el-col :span="8"><el-card shadow="never"><div class="summary-label">推荐数量</div><div class="summary-value">{{ total }}</div></el-card></el-col>
      <el-col :span="8"><el-card shadow="never"><div class="summary-label">已点击</div><div class="summary-value">{{ clickedCount }}</div></el-card></el-col>
      <el-col :span="8"><el-card shadow="never"><div class="summary-label">已完成</div><div class="summary-value success">{{ doneCount }}</div></el-card></el-col>
    </el-row>

    <el-table v-loading="loading" :data="recommendationList">
      <el-table-column label="推荐ID" prop="recommendId" width="100" />
      <el-table-column label="用户ID" prop="userId" width="90" />
      <el-table-column label="业务类型" prop="bizType" width="100" />
      <el-table-column label="标题" prop="title" min-width="160" show-overflow-tooltip />
      <el-table-column label="推荐理由" prop="recommendReason" min-width="260" show-overflow-tooltip />
      <el-table-column label="推荐分" prop="recommendScore" width="100" />
      <el-table-column label="反馈状态" width="120">
        <template #default="scope">
          <el-tag>{{ labelOf(meta.recommendationFeedbackStatuses, scope.row.feedbackStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="场景" width="120">
        <template #default="scope">{{ sceneLabel(scope.row.sceneCode) }}</template>
      </el-table-column>
      <el-table-column label="过期时间" prop="expireTime" min-width="180" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="handleFeedback(scope.row, 'LIKE')">感兴趣</el-button>
          <el-button type="warning" link @click="handleFeedback(scope.row, 'DISLIKE')">不感兴趣</el-button>
          <el-button type="success" link @click="handleFeedback(scope.row, 'DONE')">已完成</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-if="viewMode === 'history'"
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAnalysisMeta } from '@/api/campus/analysis'
import { feedbackLearningRecommendation, generateLearningRecommendation, listActiveLearningRecommendation, listLearningRecommendation } from '@/api/campus/learning'
import { fetchUserOptions } from '@/api/campus/options'

const loading = ref(false)
const total = ref(0)
const recommendationList = ref<any[]>([])
const sceneCode = ref('home')
const viewMode = ref('active')
const userOptions = ref<any[]>([])
const meta = reactive<any>({ supportedScenes: ['home'], recommendationFeedbackStatuses: {} })
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, userId: undefined })

const clickedCount = computed(() => recommendationList.value.filter((item: any) => item.clickStatus === '1').length)
const doneCount = computed(() => recommendationList.value.filter((item: any) => item.feedbackStatus === 'DONE').length)

function labelOf(map: Record<string, string>, value: string) {
  return map?.[value] || value || '--'
}

function sceneLabel(value: string) {
  const labels: Record<string, string> = {
    home: '首页推荐',
    diagnosis: '学情诊断',
    workbench: '学习工作台',
    course: '课程学习',
    exam: '考试巩固',
  }
  return labels[value] || value || '--'
}

async function getList() {
  if (!queryParams.userId) {
    recommendationList.value = []
    total.value = 0
    return
  }
  loading.value = true
  try {
    if (viewMode.value === 'active') {
      const res = await listActiveLearningRecommendation({ userId: queryParams.userId, sceneCode: sceneCode.value, limit: 10 })
      recommendationList.value = res.data || []
      total.value = recommendationList.value.length
    } else {
      const res = await listLearningRecommendation({ ...queryParams, sceneCode: sceneCode.value })
      recommendationList.value = res.rows || []
      total.value = res.total || 0
    }
  } finally {
    loading.value = false
  }
}

async function handleGenerate() {
  if (!queryParams.userId) {
    ElMessage.warning('请先选择用户')
    return
  }
  await generateLearningRecommendation({ userId: queryParams.userId, sceneCode: sceneCode.value, limit: 5 })
  ElMessage.success('推荐生成成功')
  getList()
}

async function handleFeedback(row: any, feedbackStatus: string) {
  await feedbackLearningRecommendation({ recommendId: row.recommendId, feedbackStatus, clickStatus: '1', exposeStatus: '1' })
  ElMessage.success('反馈成功')
  getList()
}

async function loadOptions() {
  userOptions.value = await fetchUserOptions()
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
.summary-value.success { color: var(--el-color-success); }
</style>
