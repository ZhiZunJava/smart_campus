<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="家长ID"><el-input v-model="queryParams.parentUserId" style="width:180px" /></el-form-item>
      <el-form-item label="学生">
        <el-select v-model="queryParams.studentUserId" filterable clearable placeholder="可选学生" style="width: 240px">
          <el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" filterable clearable placeholder="可选课程" style="width: 240px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="loadData">加载概览</el-button></el-form-item>
    </el-form>
    <el-row :gutter="16"><el-col :span="8"><el-card><div class="label">学习行为</div><div class="value">{{ data.studyRecordCount || 0 }}</div></el-card></el-col><el-col :span="8"><el-card><div class="label">考试记录</div><div class="value">{{ data.examRecordCount || 0 }}</div></el-card></el-col><el-col :span="8"><el-card><div class="label">预警数</div><div class="value">{{ data.warningCount || 0 }}</div></el-card></el-col></el-row>
    <el-card class="mt16" shadow="never"><template #header><span>孩子画像</span></template><div v-if="data.profile">能力等级：{{ data.profile.abilityLevel }}，掌握度：{{ data.profile.masteryScore }}，风险分：{{ data.profile.riskScore }}</div><el-empty v-else description="暂无画像数据" /></el-card>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getParentDashboard } from '@/api/campus/overview'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'
const queryParams=reactive<any>({ parentUserId: 1, studentUserId: undefined, courseId: undefined })
const data=ref<any>({})
const studentOptions=ref<any[]>([])
const courseOptions=ref<any[]>([])
async function loadOptions(){ studentOptions.value = await fetchUserOptions('student'); courseOptions.value = await fetchCourseOptions(); if(!queryParams.studentUserId && studentOptions.value.length){ queryParams.studentUserId = studentOptions.value[0].value } }
async function loadData(){ try{ const res=await getParentDashboard(queryParams); data.value=res.data||{} }catch(e){ ElMessage.error('暂无家长绑定学生信息') } }
onMounted(async()=>{ await loadOptions(); await loadData() })
</script><style scoped>.mb16{margin-bottom:16px}.mt16{margin-top:16px}.label{color:var(--el-text-color-secondary)}.value{font-size:28px;font-weight:700;color:var(--el-color-primary)}</style>
