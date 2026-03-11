<template>
  <div class="portal-page portal-grid portal-grid-2">
    <el-card class="portal-card">
      <template #header><span>学习报告</span></template>
      <el-table v-loading="loading" :data="reports" border>
        <el-table-column prop="reportId" label="报告ID" width="90" />
        <el-table-column label="报告类型" width="120"><template #default="scope"><el-tag>{{ reportTypeLabel(scope.row.reportType) }}</el-tag></template></el-table-column>
        <el-table-column prop="generateTime" label="生成时间" min-width="180" />
        <el-table-column label="操作" width="100"><template #default="scope"><el-button link type="primary" @click="selectReport(scope.row)">查看</el-button></template></el-table-column>
      </el-table>
      <el-empty v-if="!loading && reports.length === 0" class="portal-empty" description="暂无学习报告" />
    </el-card>
    <el-card class="portal-card">
      <template #header><span>报告详情</span></template>
      <div v-if="detail.reportId">
        <div class="report-title">{{ reportTypeLabel(detail.reportType) }}</div>
        <div class="report-time">生成时间：{{ detail.generateTime }}</div>
        <div class="report-content">{{ detail.reportContent }}</div>
      </div>
      <el-empty v-else class="portal-empty" description="请选择一份报告查看详情" />
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getParentDashboard, listReport } from '@/api/portal'
import usePortalUserStore from '@/store/user'
const userStore = usePortalUserStore()
const loading=ref(false), reports=ref<any[]>([]), detail=ref<any>({})
function reportTypeLabel(type:string){ return ({ weekly:'周报', monthly:'月报', exam:'考前报告' } as any)[type] || type || '-' }
async function getList(){ const parentUserId = userStore.user?.userId; if(!parentUserId) return; loading.value=true; const dash = await getParentDashboard({ parentUserId }); const studentUserId = dash.data?.studentUserId; const res=await listReport({ pageNum:1, pageSize:50, userId: studentUserId }); reports.value=res.rows||[]; if(reports.value.length){ detail.value = reports.value[0] } loading.value=false }
function selectReport(row:any){ detail.value = row }
onMounted(getList)
</script>
<style scoped>.report-title{font-size:20px;font-weight:700;margin-bottom:8px}.report-time{color:#6b7280;margin-bottom:16px}.report-content{line-height:1.9;color:#344054;white-space:pre-wrap}</style>
