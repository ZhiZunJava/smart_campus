<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="业务类型">
        <el-input v-model="queryParams.bizType" style="width:180px" />
      </el-form-item>
      <el-form-item label="模型">
        <el-select v-model="queryParams.modelId" filterable clearable style="width:260px" placeholder="请选择模型">
          <el-option v-for="item in modelOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="任务状态">
        <el-input v-model="queryParams.taskStatus" style="width:180px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="任务ID" prop="taskId" width="90" />
      <el-table-column label="业务类型" prop="bizType" width="120" />
      <el-table-column label="业务ID" prop="bizId" width="90" />
      <el-table-column label="模型名称" prop="modelName" min-width="180">
        <template #default="scope">{{ scope.row.modelName || `模型#${scope.row.modelId}` }}</template>
      </el-table-column>
      <el-table-column label="Token" prop="tokenUsed" width="90" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.taskStatus === 'SUCCESS' ? 'success' : 'danger'">{{ scope.row.taskStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="耗时(ms)" prop="durationMs" width="100" />
      <el-table-column label="错误信息" prop="errorMsg" min-width="220" show-overflow-tooltip />
      <el-table-column label="创建时间" prop="createTime" min-width="180" />
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="detailOpen" title="任务日志详情" width="980px">
      <el-descriptions :column="2" border class="detail-summary">
        <el-descriptions-item label="任务ID">{{ detail.taskId }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.taskStatus }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ detail.bizType }}</el-descriptions-item>
        <el-descriptions-item label="业务ID">{{ detail.bizId }}</el-descriptions-item>
        <el-descriptions-item label="模型名称">{{ detail.modelName || `模型#${detail.modelId || '-'}` }}</el-descriptions-item>
        <el-descriptions-item label="Token">{{ detail.tokenUsed }}</el-descriptions-item>
        <el-descriptions-item label="耗时(ms)">{{ detail.durationMs }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2">{{ detail.errorMsg || '-' }}</el-descriptions-item>
      </el-descriptions>

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
import { onMounted, reactive, ref } from 'vue'
import { fetchAiModelOptions } from '@/api/campus/options'
import { getAiTask, listAiTask } from '@/api/campus/ai'

const loading = ref(false)
const total = ref(0)
const dataList = ref<any[]>([])
const detailOpen = ref(false)
const detail = ref<any>({})
const modelOptions = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, bizType: '', taskStatus: '', modelId: undefined })

async function getList() {
  loading.value = true
  const res = await listAiTask(queryParams)
  dataList.value = res.rows || []
  total.value = res.total || 0
  loading.value = false
}

async function loadModelOptions() {
  modelOptions.value = await fetchAiModelOptions('chat')
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
