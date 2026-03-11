<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="用户"><el-select v-model="queryParams.userId" filterable clearable style="width:240px"><el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="报告类型"><el-select v-model="reportType" style="width:180px"><el-option label="周报" value="weekly" /><el-option label="月报" value="monthly" /><el-option label="考前报告" value="exam" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item>
      <el-form-item><el-button type="warning" plain icon="Document" @click="handleGenerate">生成报告</el-button></el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="reportList">
      <el-table-column label="报告ID" prop="reportId" width="100" />
      <el-table-column label="用户ID" prop="userId" width="90" />
      <el-table-column label="报告类型" prop="reportType" width="120" />
      <el-table-column label="报告内容" prop="reportContent" min-width="360" show-overflow-tooltip />
      <el-table-column label="生成时间" prop="generateTime" min-width="180" />
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { generateLearningReport, listLearningReport } from '@/api/campus/analysis'
import { fetchUserOptions } from '@/api/campus/options'
const loading = ref(false)
const total = ref(0)
const reportList = ref<any[]>([])
const reportType = ref('weekly')
const userOptions = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, userId: undefined })
async function getList(){ loading.value = true; const res = await listLearningReport({ ...queryParams, reportType: reportType.value }); reportList.value = res.rows || []; total.value = res.total || 0; loading.value = false }
async function handleGenerate(){ if(!queryParams.userId){ ElMessage.warning('请先输入用户ID'); return } await generateLearningReport({ userId: queryParams.userId, reportType: reportType.value }); ElMessage.success('报告生成成功'); getList() }
async function loadOptions(){ userOptions.value = await fetchUserOptions() }
onMounted(async()=>{ await loadOptions(); getList() })
</script>

<style scoped>.mb16{margin-bottom:16px;}</style>
