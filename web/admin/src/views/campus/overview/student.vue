<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="学生">
        <el-select v-model="queryParams.userId" filterable clearable placeholder="请选择学生" style="width: 240px">
          <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" filterable clearable placeholder="可选课程" style="width: 240px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="loadData">加载概览</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16">
      <el-col :span="8"><el-card><div class="label">学习行为</div><div class="value">{{ data.studyRecordCount || 0 }}</div></el-card></el-col>
      <el-col :span="8"><el-card><div class="label">考试记录</div><div class="value">{{ data.examRecordCount || 0 }}</div></el-card></el-col>
      <el-col :span="8"><el-card><div class="label">预警数</div><div class="value">{{ data.warningCount || 0 }}</div></el-card></el-col>
    </el-row>
    <el-row :gutter="16" class="mt16">
      <el-col :span="10"><el-card shadow="never"><template #header><span>画像概览</span></template><div v-if="data.profile">能力等级：{{ data.profile.abilityLevel }}<br />活跃度：{{ data.profile.activeScore }}<br />掌握度：{{ data.profile.masteryScore }}<br />风险分：{{ data.profile.riskScore }}</div><el-empty v-else description="暂无画像数据" /></el-card></el-col>
      <el-col :span="14"><el-card shadow="never"><template #header><span>推荐资源</span></template><el-table :data="data.recommendations || []"><el-table-column label="标题" prop="title" min-width="180" /><el-table-column label="类型" prop="resourceType" width="100" /><el-table-column label="推荐理由" prop="recommendReason" min-width="220" show-overflow-tooltip /></el-table></el-card></el-col>
    </el-row>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getStudentDashboard } from '@/api/campus/overview'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'
const queryParams=reactive<any>({ userId: undefined, courseId: undefined, recommendLimit: 5 })
const data=ref<any>({})
const userOptions=ref<any[]>([])
const courseOptions=ref<any[]>([])
async function loadOptions(){ userOptions.value = await fetchUserOptions('student'); courseOptions.value = await fetchCourseOptions(); if(!queryParams.userId && userOptions.value.length){ queryParams.userId=userOptions.value[0].value } }
async function loadData(){ const res=await getStudentDashboard(queryParams); data.value=res.data||{} }
onMounted(async()=>{ await loadOptions(); await loadData() })
</script><style scoped>.mb16{margin-bottom:16px}.mt16{margin-top:16px}.label{color:var(--el-text-color-secondary)}.value{font-size:28px;font-weight:700;color:var(--el-color-primary)}</style>
