<template>
  <div class="request-list">
    <div class="back-nav">
      <el-button text bg size="small" @click="$emit('back')"><i class="ri-arrow-left-line" /> 返回服务大厅</el-button>
    </div>

    <div class="list-header">
      <h2>我的办理记录</h2>
      <div class="list-filters">
        <el-select v-model="statusFilter" clearable placeholder="全部状态" style="width: 140px" @change="filterList">
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已驳回" value="REJECTED" />
          <el-option label="已撤回" value="CANCELLED" />
        </el-select>
      </div>
    </div>

    <el-table v-loading="loading" :data="filteredList" @row-click="(row: any) => $emit('view-request', row)">
      <el-table-column prop="requestNo" label="申请编号" min-width="170" />
      <el-table-column prop="templateName" label="服务名称" min-width="190" />
      <el-table-column prop="categoryName" label="分类" width="140" />
      <el-table-column prop="summaryText" label="摘要" min-width="240" show-overflow-tooltip />
      <el-table-column prop="currentStepName" label="当前节点" width="130" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.requestStatus)" size="small">{{ statusLabel(row.requestStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submittedTime" label="提交时间" width="165" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click.stop="$emit('view-request', row)">详情</el-button>
          <el-button v-if="row.requestStatus === 'PENDING' && allowCancel" link type="warning" @click.stop="$emit('cancel-request', row)">撤回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && !filteredList.length" description="暂无申请记录" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { statusLabel, statusType } from './student-pages/useStudentAffairDetail'

const props = defineProps<{
  requests: any[]
  loading: boolean
  allowCancel?: boolean
  initialStatusFilter?: string
}>()

defineEmits<{
  back: []
  'view-request': [request: any]
  'cancel-request': [request: any]
}>()

const statusFilter = ref(props.initialStatusFilter || '')

const filteredList = computed(() => {
  if (!statusFilter.value) return props.requests
  return props.requests.filter(r => r.requestStatus === statusFilter.value)
})

function filterList() {}
</script>

<style scoped>
.request-list { display: grid; gap: 16px; }
.back-nav { margin-bottom: -4px; }
.list-header { display: flex; align-items: center; justify-content: space-between; }
.list-header h2 { margin: 0; font-size: 20px; color: #0f172a; }
.list-filters { display: flex; gap: 10px; }

@media (max-width: 768px) {
  .list-header { flex-direction: column; align-items: flex-start; gap: 10px; }
  :deep(.el-table) { overflow-x: auto; }
}
@media (max-width: 640px) {
  .request-list { gap: 12px; }
}

@media (prefers-reduced-motion: reduce) {
  * { transition: none !important; }
}
</style>
