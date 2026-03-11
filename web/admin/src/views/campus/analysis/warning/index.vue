<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="用户"><el-select v-model="queryParams.userId" filterable clearable style="width:240px"><el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width:240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item>
      <el-form-item><el-button type="warning" plain icon="Bell" @click="handleBuild">构建预警</el-button></el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="warningList">
      <el-table-column label="预警ID" prop="warningId" width="100" />
      <el-table-column label="用户ID" prop="userId" width="90" />
      <el-table-column label="课程ID" prop="courseId" width="90" />
      <el-table-column label="预警类型" prop="warningType" width="120" />
      <el-table-column label="预警等级" prop="warningLevel" width="100" />
      <el-table-column label="预警内容" prop="warningContent" min-width="260" show-overflow-tooltip />
      <el-table-column label="干预建议" prop="suggestion" min-width="260" show-overflow-tooltip />
      <el-table-column label="处理状态" prop="processStatus" width="110" />
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { buildLearningWarning, listLearningWarning } from '@/api/campus/analysis'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'
const loading = ref(false)
const total = ref(0)
const warningList = ref<any[]>([])
const userOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, userId: undefined, courseId: undefined })
async function getList(){ loading.value = true; const res = await listLearningWarning(queryParams); warningList.value = res.rows || []; total.value = res.total || 0; loading.value = false }
async function handleBuild(){ if(!queryParams.userId || !queryParams.courseId){ ElMessage.warning('请先输入用户ID和课程ID'); return } await buildLearningWarning({ userId: queryParams.userId, courseId: queryParams.courseId }); ElMessage.success('预警构建成功'); getList() }
async function loadOptions(){ userOptions.value = await fetchUserOptions(); courseOptions.value = await fetchCourseOptions() }
onMounted(async()=>{ await loadOptions(); getList() })
</script>

<style scoped>.mb16{margin-bottom:16px;}</style>
