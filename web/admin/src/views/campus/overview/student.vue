<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="toolbar">
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

    <el-row :gutter="16" class="summary-row">
      <el-col :span="8"><el-card><div class="label">学生ID</div><div class="value">{{ dashboard.userId || '--' }}</div></el-card></el-col>
      <el-col :span="8"><el-card><div class="label">课程ID</div><div class="value">{{ dashboard.courseId || '--' }}</div></el-card></el-col>
      <el-col :span="8"><el-card><div class="label">考试记录</div><div class="value">{{ dashboard.examRecordCount || 0 }}</div></el-card></el-col>
    </el-row>

    <el-card shadow="never">
      <template #header><span>说明</span></template>
      <div class="info-text">学生概览已移除行为记录、画像、推荐、预警和报告，只保留基础考试数据。</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getStudentDashboard } from '@/api/campus/overview'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'

const queryParams = reactive<any>({ userId: undefined, courseId: undefined })
const dashboard = ref<any>({})
const userOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])

async function loadOptions() {
  userOptions.value = await fetchUserOptions('student')
  courseOptions.value = await fetchCourseOptions()
  if (!queryParams.userId && userOptions.value.length) {
    queryParams.userId = userOptions.value[0].value
  }
}

async function loadData() {
  if (!queryParams.userId) return
  const res = await getStudentDashboard(queryParams)
  dashboard.value = res.data || {}
}

onMounted(async () => {
  await loadOptions()
  await loadData()
})
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
.summary-row { margin-bottom: 16px; }
.label { color: var(--el-text-color-secondary); }
.value { margin-top: 8px; font-size: 28px; font-weight: 700; color: var(--el-color-primary); }
.info-text { line-height: 1.8; color: var(--el-text-color-regular); }
</style>
