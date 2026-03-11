<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="业务类型"><el-input v-model="queryParams.bizType" style="width:180px" /></el-form-item><el-form-item label="任务状态"><el-input v-model="queryParams.taskStatus" style="width:180px" /></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item></el-form>
    <el-table v-loading="loading" :data="dataList"><el-table-column label="任务ID" prop="taskId" width="90" /><el-table-column label="业务类型" prop="bizType" width="120" /><el-table-column label="业务ID" prop="bizId" width="90" /><el-table-column label="模型ID" prop="modelId" width="90" /><el-table-column label="Token" prop="tokenUsed" width="90" /><el-table-column label="状态" width="100"><template #default="scope"><el-tag :type="scope.row.taskStatus === 'SUCCESS' ? 'success' : 'danger'">{{ scope.row.taskStatus }}</el-tag></template></el-table-column><el-table-column label="耗时(ms)" prop="durationMs" width="100" /><el-table-column label="错误信息" prop="errorMsg" min-width="220" show-overflow-tooltip /><el-table-column label="创建时间" prop="createTime" min-width="180" /><el-table-column label="操作" width="100"><template #default="scope"><el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="detailOpen" title="任务日志详情" width="900px"><el-descriptions :column="2" border><el-descriptions-item label="任务ID">{{ detail.taskId }}</el-descriptions-item><el-descriptions-item label="状态">{{ detail.taskStatus }}</el-descriptions-item><el-descriptions-item label="业务类型">{{ detail.bizType }}</el-descriptions-item><el-descriptions-item label="业务ID">{{ detail.bizId }}</el-descriptions-item><el-descriptions-item label="模型ID">{{ detail.modelId }}</el-descriptions-item><el-descriptions-item label="Token">{{ detail.tokenUsed }}</el-descriptions-item><el-descriptions-item label="耗时(ms)">{{ detail.durationMs }}</el-descriptions-item><el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item><el-descriptions-item label="错误信息" :span="2">{{ detail.errorMsg || '-' }}</el-descriptions-item><el-descriptions-item label="请求报文" :span="2">{{ detail.requestPayload || '-' }}</el-descriptions-item><el-descriptions-item label="响应报文" :span="2">{{ detail.responsePayload || '-' }}</el-descriptions-item></el-descriptions></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { getAiTask, listAiTask } from '@/api/campus/ai'
const loading=ref(false), total=ref(0), dataList=ref<any[]>([]), detailOpen=ref(false), detail=ref<any>({})
const queryParams=reactive<any>({ pageNum:1,pageSize:10,bizType:'',taskStatus:'' })
async function getList(){ loading.value=true; const res=await listAiTask(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
async function handleView(row:any){ const res=await getAiTask(row.taskId); detail.value=res.data||{}; detailOpen.value=true }
getList()
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
