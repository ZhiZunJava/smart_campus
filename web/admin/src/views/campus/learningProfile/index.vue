<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="用户"><el-select v-model="queryParams.userId" filterable clearable style="width:240px"><el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width:240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="warning" plain icon="Refresh" @click="handleRebuild">重建画像</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>
    <el-table v-loading="loading" :data="profileList">
      <el-table-column label="画像ID" prop="profileId" width="90" />
      <el-table-column label="用户ID" prop="userId" width="90" />
      <el-table-column label="课程ID" prop="courseId" width="90" />
      <el-table-column label="能力等级" prop="abilityLevel" width="110" />
      <el-table-column label="活跃度" prop="activeScore" width="100" />
      <el-table-column label="专注度" prop="concentrationScore" width="100" />
      <el-table-column label="掌握度" prop="masteryScore" width="100" />
      <el-table-column label="兴趣度" prop="interestScore" width="100" />
      <el-table-column label="风险分" prop="riskScore" width="100" />
      <el-table-column label="更新时间" prop="lastCalculatedTime" min-width="180" />
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listLearningProfile, rebuildLearningProfile } from '@/api/campus/learning'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const profileList = ref<any[]>([])
const userOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, userId: undefined, courseId: undefined })
async function getList(){ loading.value = true; const res = await listLearningProfile(queryParams); profileList.value = res.rows || []; total.value = res.total || 0; loading.value = false }
async function handleRebuild(){ if(!queryParams.userId || !queryParams.courseId){ ElMessage.warning('请先输入用户ID和课程ID'); return } await rebuildLearningProfile({ userId: queryParams.userId, courseId: queryParams.courseId }); ElMessage.success('画像重建成功'); getList() }
async function loadOptions(){ userOptions.value = await fetchUserOptions(); courseOptions.value = await fetchCourseOptions() }
onMounted(async()=>{ await loadOptions(); getList() })
</script>

<style scoped>.mb16{margin-bottom:16px;}</style>
