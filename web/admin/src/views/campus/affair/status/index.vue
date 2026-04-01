<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch" class="mb16">
      <el-form-item label="学生姓名">
        <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="学号">
        <el-input v-model="queryParams.studentNo" placeholder="请输入学号" clearable @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="业务类型">
        <el-input v-model="queryParams.businessName" placeholder="请输入业务名称" clearable @keyup.enter="getList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />

    <el-table v-loading="loading" :data="logList">
      <el-table-column prop="studentName" label="学生姓名" min-width="140" />
      <el-table-column prop="studentNo" label="学号" min-width="140" />
      <el-table-column prop="businessName" label="异动业务" min-width="180" />
      <el-table-column label="状态变化" min-width="220">
        <template #default="{ row }">
          <div class="status-flow">
            <el-tag>{{ row.beforeStatusName || '-' }}</el-tag>
            <span>→</span>
            <el-tag type="success">{{ row.afterStatusName || '-' }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="effectiveDate" label="生效日期" width="140" />
      <el-table-column prop="operatorName" label="操作人" width="140" />
      <el-table-column prop="createTime" label="记录时间" width="170" />
      <el-table-column label="明细" min-width="260">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewDetail(row)">查看详情</el-button>
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

    <el-dialog v-model="detailOpen" title="学籍异动台账详情" width="760px">
      <div v-if="detailRow" class="detail-box">
        <div class="detail-item"><span>学生姓名</span><strong>{{ detailRow.studentName }}</strong></div>
        <div class="detail-item"><span>学号</span><strong>{{ detailRow.studentNo }}</strong></div>
        <div class="detail-item"><span>业务名称</span><strong>{{ detailRow.businessName }}</strong></div>
        <div class="detail-item"><span>生效日期</span><strong>{{ detailRow.effectiveDate || '-' }}</strong></div>
        <div class="detail-item"><span>变更前状态</span><strong>{{ detailRow.beforeStatusName || '-' }}</strong></div>
        <div class="detail-item"><span>变更后状态</span><strong>{{ detailRow.afterStatusName || '-' }}</strong></div>
        <div class="detail-item detail-item--full"><span>业务明细</span><pre>{{ prettyJson(detailRow.detailJson) }}</pre></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getStudentStatusLog, listStudentStatusLog } from '@/api/campus/affair'

const loading = ref(false)
const showSearch = ref(true)
const detailOpen = ref(false)
const total = ref(0)
const logList = ref<any[]>([])
const detailRow = ref<any>(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  studentName: '',
  studentNo: '',
  businessName: '',
})

function prettyJson(value: string) {
  try {
    return JSON.stringify(JSON.parse(value || '{}'), null, 2)
  } catch {
    return value || '{}'
  }
}

async function getList() {
  loading.value = true
  try {
    const res = await listStudentStatusLog(queryParams)
    logList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(queryParams, { pageNum: 1, pageSize: 10, studentName: '', studentNo: '', businessName: '' })
  getList()
}

async function viewDetail(row: any) {
  const res = await getStudentStatusLog(row.statusLogId)
  detailRow.value = res.data || res
  detailOpen.value = true
}

onMounted(getList)
</script>

<style scoped>
.mb16 {
  margin-bottom: 16px;
}

.status-flow {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-box {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.detail-item {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 14px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item span {
  color: #64748b;
  font-size: 12px;
}

.detail-item strong {
  color: #111827;
}

.detail-item--full {
  grid-column: 1 / -1;
}

.detail-item pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 12px;
}
</style>
