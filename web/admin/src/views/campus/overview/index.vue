<template>
  <div class="app-container campus-overview-page">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="用户">
        <el-select v-model="queryParams.userId" filterable clearable placeholder="请选择用户" style="width: 240px">
          <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" filterable clearable placeholder="可选课程" style="width: 240px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="loadAll">加载概览</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="mb16">
      <el-col :xs="24" :sm="12" :lg="6"><el-card class="overview-card"><div class="card-label">资源总数</div><div class="card-value">{{ dashboard.resourceCount || 0 }}</div></el-card></el-col>
      <el-col :xs="24" :sm="12" :lg="6"><el-card class="overview-card"><div class="card-label">考试记录</div><div class="card-value">{{ dashboard.examRecordCount || 0 }}</div></el-card></el-col>
      <el-col :xs="24" :sm="12" :lg="6"><el-card class="overview-card"><div class="card-label">用户ID</div><div class="card-value">{{ dashboard.userId || '--' }}</div></el-card></el-col>
      <el-col :xs="24" :sm="12" :lg="6"><el-card class="overview-card"><div class="card-label">当前状态</div><div class="card-value">已精简</div></el-card></el-col>
    </el-row>

    <el-card shadow="never">
      <template #header><span>说明</span></template>
      <div class="info-grid">
        <div>当前概览已移除行为记录、学习画像、个性推荐、学情预警和学情报告相关内容。</div>
        <div>这里仅保留资源与考试类基础统计，便于后台继续查看核心教学运行数据。</div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCampusDashboard } from '@/api/campus/overview'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'

const queryParams = reactive<any>({ userId: undefined, courseId: undefined })
const dashboard = ref<any>({})
const userOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])

async function loadOptions() {
  userOptions.value = await fetchUserOptions()
  courseOptions.value = await fetchCourseOptions()
  if (!queryParams.userId && userOptions.value.length) {
    queryParams.userId = userOptions.value[0].value
  }
}

async function loadAll() {
  if (!queryParams.userId) {
    ElMessage.warning('请先选择用户')
    return
  }
  const res = await getCampusDashboard(queryParams)
  dashboard.value = res.data || {}
}

onMounted(async () => {
  await loadOptions()
  await loadAll()
})
</script>

<style scoped>
.campus-overview-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.mb16 { margin-bottom: 0; }
.overview-card .card-label { color: var(--el-text-color-secondary); margin-bottom: 10px; font-size: 13px; }
.overview-card .card-value { font-size: 28px; font-weight: 700; color: var(--el-color-primary); }
.info-grid {
  display: grid;
  gap: 12px;
  line-height: 1.8;
  color: var(--el-text-color-regular);
}
</style>
