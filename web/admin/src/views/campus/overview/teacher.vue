<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="教师">
        <el-select v-model="queryParams.teacherId" filterable clearable placeholder="请选择教师" style="width: 240px">
          <el-option v-for="item in teacherOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="loadData">加载概览</el-button></el-form-item>
    </el-form>
    <el-row :gutter="16"><el-col :span="6"><el-card><div class="label">课程数</div><div class="value">{{ data.courseCount || 0 }}</div></el-card></el-col><el-col :span="6"><el-card><div class="label">班级数</div><div class="value">{{ data.classCount || 0 }}</div></el-card></el-col><el-col :span="6"><el-card><div class="label">资源数</div><div class="value">{{ data.resourceCount || 0 }}</div></el-card></el-col><el-col :span="6"><el-card><div class="label">预警数</div><div class="value">{{ data.warningCount || 0 }}</div></el-card></el-col></el-row>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getTeacherDashboard } from '@/api/campus/overview'
import { fetchUserOptions } from '@/api/campus/options'
const queryParams=reactive<any>({ teacherId: undefined })
const data=ref<any>({})
const teacherOptions=ref<any[]>([])
async function loadOptions(){ teacherOptions.value = await fetchUserOptions('teacher'); if(!queryParams.teacherId && teacherOptions.value.length){ queryParams.teacherId = teacherOptions.value[0].value } }
async function loadData(){ const res=await getTeacherDashboard(queryParams); data.value=res.data||{} }
onMounted(async()=>{ await loadOptions(); await loadData() })
</script><style scoped>.mb16{margin-bottom:16px}.label{color:var(--el-text-color-secondary)}.value{font-size:28px;font-weight:700;color:var(--el-color-primary)}</style>
