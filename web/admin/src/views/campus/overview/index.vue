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
      <el-form-item label="推荐数">
        <el-input-number v-model="queryParams.recommendLimit" :min="1" :max="20" style="width: 140px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="loadDashboard">加载概览</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="mb16">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="overview-card"><div class="card-label">资源总数</div><div class="card-value">{{ dashboard.resourceCount || 0 }}</div></el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="overview-card"><div class="card-label">学习行为</div><div class="card-value">{{ dashboard.studyRecordCount || 0 }}</div></el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="overview-card"><div class="card-label">预警数量</div><div class="card-value">{{ dashboard.warningCount || 0 }}</div></el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="overview-card"><div class="card-label">考试记录</div><div class="card-value">{{ dashboard.examRecordCount || 0 }}</div></el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="10">
        <el-card shadow="never">
          <template #header><span>画像概览</span></template>
          <div v-if="dashboard.profile" class="profile-grid">
            <div>能力等级：{{ dashboard.profile.abilityLevel || '-' }}</div>
            <div>活跃度：{{ dashboard.profile.activeScore || 0 }}</div>
            <div>专注度：{{ dashboard.profile.concentrationScore || 0 }}</div>
            <div>掌握度：{{ dashboard.profile.masteryScore || 0 }}</div>
            <div>兴趣度：{{ dashboard.profile.interestScore || 0 }}</div>
            <div>风险分：{{ dashboard.profile.riskScore || 0 }}</div>
          </div>
          <el-empty v-else description="暂无画像数据" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="14">
        <el-card shadow="never">
          <template #header><span>推荐列表</span></template>
          <el-table :data="dashboard.recommendations || []" border>
            <el-table-column label="推荐ID" prop="recommendId" width="90" />
            <el-table-column label="标题" prop="title" min-width="180" show-overflow-tooltip />
            <el-table-column label="资源类型" prop="resourceType" width="110" />
            <el-table-column label="推荐理由" prop="recommendReason" min-width="220" show-overflow-tooltip />
            <el-table-column label="推荐分" prop="recommendScore" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="mt16">
      <template #header><span>预警列表</span></template>
      <el-table :data="dashboard.warnings || []" border>
        <el-table-column label="预警类型" prop="warningType" width="140" />
        <el-table-column label="预警等级" prop="warningLevel" width="120" />
        <el-table-column label="预警内容" prop="warningContent" min-width="260" show-overflow-tooltip />
        <el-table-column label="干预建议" prop="suggestion" min-width="260" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCampusDashboard } from '@/api/campus/overview'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'

const queryParams = reactive<any>({ userId: undefined, courseId: undefined, recommendLimit: 5 })
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

async function loadDashboard() {
  if (!queryParams.userId) {
    ElMessage.warning('请先选择用户')
    return
  }
  const res = await getCampusDashboard(queryParams)
  dashboard.value = res.data || {}
}

onMounted(async () => {
  await loadOptions()
  await loadDashboard()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.mt16 { margin-top: 16px; }
.overview-card .card-label { color: var(--el-text-color-secondary); margin-bottom: 10px; }
.overview-card .card-value { font-size: 28px; font-weight: 700; color: var(--el-color-primary); }
.profile-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
</style>
