<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="用户"><el-select v-model="queryParams.userId" filterable clearable style="width:240px"><el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="场景"><el-select v-model="sceneCode" style="width:180px"><el-option label="首页推荐" value="home" /><el-option label="每日计划" value="daily_plan" /><el-option label="考前冲刺" value="exam_before" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item>
      <el-form-item><el-button type="warning" plain icon="MagicStick" @click="handleGenerate">生成推荐</el-button></el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="recommendationList">
      <el-table-column label="推荐ID" prop="recommendId" width="100" />
      <el-table-column label="用户ID" prop="userId" width="90" />
      <el-table-column label="业务类型" prop="bizType" width="110" />
      <el-table-column label="业务ID" prop="bizId" width="90" />
      <el-table-column label="推荐理由" prop="recommendReason" min-width="220" show-overflow-tooltip />
      <el-table-column label="推荐分" prop="recommendScore" width="100" />
      <el-table-column label="场景" prop="sceneCode" width="120" />
      <el-table-column label="过期时间" prop="expireTime" min-width="180" />
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { generateLearningRecommendation, listLearningRecommendation } from '@/api/campus/learning'
import { fetchUserOptions } from '@/api/campus/options'
const loading = ref(false)
const total = ref(0)
const recommendationList = ref<any[]>([])
const sceneCode = ref('home')
const userOptions = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, userId: undefined })
async function getList(){ loading.value = true; const res = await listLearningRecommendation({ ...queryParams, sceneCode: sceneCode.value }); recommendationList.value = res.rows || []; total.value = res.total || 0; loading.value = false }
async function handleGenerate(){ if(!queryParams.userId){ ElMessage.warning('请先输入用户ID'); return } await generateLearningRecommendation({ userId: queryParams.userId, sceneCode: sceneCode.value, limit: 5 }); ElMessage.success('推荐生成成功'); getList() }
async function loadOptions(){ userOptions.value = await fetchUserOptions() }
onMounted(async()=>{ await loadOptions(); getList() })
</script>

<style scoped>.mb16{margin-bottom:16px;}</style>
