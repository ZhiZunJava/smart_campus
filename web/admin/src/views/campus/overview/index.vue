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
        <el-button type="primary" icon="Search" @click="loadAll">加载工作台</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="mb16">
      <el-col :xs="24" :sm="12" :lg="6"><el-card class="overview-card"><div class="card-label">资源总数</div><div class="card-value">{{ dashboard.resourceCount || 0 }}</div></el-card></el-col>
      <el-col :xs="24" :sm="12" :lg="6"><el-card class="overview-card"><div class="card-label">学习行为</div><div class="card-value">{{ dashboard.studyRecordCount || 0 }}</div></el-card></el-col>
      <el-col :xs="24" :sm="12" :lg="6"><el-card class="overview-card"><div class="card-label">预警数量</div><div class="card-value warning">{{ dashboard.warningCount || 0 }}</div></el-card></el-col>
      <el-col :xs="24" :sm="12" :lg="6"><el-card class="overview-card"><div class="card-label">风险等级</div><div class="card-value">{{ diagnosis.riskLevel || '--' }}</div></el-card></el-col>
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

        <el-card shadow="never" class="mt16">
          <template #header><span>诊断摘要</span></template>
          <template v-if="diagnosis.overallSummary">
            <el-alert :title="diagnosis.overallSummary" type="info" :closable="false" />
            <div class="section-subtitle mt16">风险标签</div>
            <div class="tag-list">
              <el-tag v-for="item in diagnosis.riskTags || []" :key="item" type="danger">{{ item }}</el-tag>
            </div>
            <div class="section-subtitle mt16">行动建议</div>
            <ul class="simple-list">
              <li v-for="item in diagnosis.actionSuggestions || []" :key="item">{{ item }}</li>
            </ul>
          </template>
          <el-empty v-else description="暂无诊断数据" />
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="14">
        <el-card shadow="never">
          <template #header><span>工作台</span></template>
          <el-tabs>
            <el-tab-pane label="推荐列表">
              <el-table :data="workbench.activeRecommendations || []" border>
                <el-table-column label="标题" prop="title" min-width="180" show-overflow-tooltip />
                <el-table-column label="资源类型" prop="resourceType" width="110" />
                <el-table-column label="推荐理由" prop="recommendReason" min-width="220" show-overflow-tooltip />
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="最近学习记录">
              <el-table :data="workbench.recentStudyRecords || []" border>
                <el-table-column label="行为类型" prop="behaviorType" width="120" />
                <el-table-column label="时长" prop="durationSeconds" width="90" />
                <el-table-column label="进度" prop="progressRate" width="90" />
                <el-table-column label="时间" prop="createTime" min-width="160" />
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="最近考试">
              <el-table :data="workbench.recentExamRecords || []" border>
                <el-table-column label="记录ID" prop="recordId" width="90" />
                <el-table-column label="得分" prop="score" width="90" />
                <el-table-column label="正确率" prop="correctRate" width="90" />
                <el-table-column label="状态" prop="examStatus" width="100" />
              </el-table>
            </el-tab-pane>
          </el-tabs>
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
import { getLearningDiagnosis, getLearningWorkbench } from '@/api/campus/analysis'
import { getCampusDashboard } from '@/api/campus/overview'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'

const queryParams = reactive<any>({ userId: undefined, courseId: undefined, recommendLimit: 5 })
const dashboard = ref<any>({})
const diagnosis = ref<any>({})
const workbench = ref<any>({ activeRecommendations: [], recentStudyRecords: [], recentExamRecords: [] })
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
  const [dashboardRes, diagnosisRes, workbenchRes] = await Promise.all([
    getCampusDashboard(queryParams),
    getLearningDiagnosis({ userId: queryParams.userId, courseId: queryParams.courseId, recommendLimit: queryParams.recommendLimit, autoGenerate: true }),
    getLearningWorkbench({ userId: queryParams.userId, courseId: queryParams.courseId, limit: queryParams.recommendLimit }),
  ])
  dashboard.value = dashboardRes.data || {}
  diagnosis.value = diagnosisRes.data || {}
  workbench.value = workbenchRes.data || {}
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
.mt16 { margin-top: 16px; }

.campus-overview-page :deep(.el-form) {
  padding: 18px 20px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid var(--el-border-color-lighter);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.04);
}

.overview-card .card-label { color: var(--el-text-color-secondary); margin-bottom: 10px; font-size: 13px; }
.overview-card .card-value { font-size: 28px; font-weight: 700; color: var(--el-color-primary); }
.overview-card .card-value.warning { color: var(--el-color-warning); }
.profile-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
.profile-grid > div { padding: 16px; border-radius: 14px; background: var(--el-fill-color-extra-light); border: 1px solid var(--el-border-color-lighter); }
.section-subtitle { font-weight: 600; color: var(--el-text-color-primary); }
.tag-list { display: flex; flex-wrap: wrap; gap: 8px; }
.simple-list { margin: 0; padding-left: 18px; line-height: 1.9; }
</style>
