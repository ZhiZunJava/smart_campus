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
      <el-col :span="6"><el-card><div class="label">学习行为</div><div class="value">{{ dashboard.studyRecordCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card><div class="label">考试记录</div><div class="value">{{ dashboard.examRecordCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card><div class="label">预警数</div><div class="value warning">{{ dashboard.warningCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card><div class="label">风险等级</div><div class="value">{{ diagnosis?.riskLevel || '--' }}</div></el-card></el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="10">
        <el-card shadow="never">
          <template #header><span>画像概览</span></template>
          <div v-if="dashboard.profile" class="profile-grid">
            <div>能力等级：{{ dashboard.profile.abilityLevel }}</div>
            <div>活跃度：{{ dashboard.profile.activeScore }}</div>
            <div>专注度：{{ dashboard.profile.concentrationScore }}</div>
            <div>掌握度：{{ dashboard.profile.masteryScore }}</div>
            <div>兴趣度：{{ dashboard.profile.interestScore }}</div>
            <div>风险分：{{ dashboard.profile.riskScore }}</div>
          </div>
          <el-empty v-else description="暂无画像数据" />
        </el-card>

        <el-card shadow="never" class="mt16">
          <template #header><span>诊断结论</span></template>
          <template v-if="diagnosis">
            <el-alert :title="diagnosis.overallSummary" type="info" :closable="false" />
            <div class="mt16">风险标签：</div>
            <el-space wrap class="mt8">
              <el-tag v-for="item in diagnosis.riskTags || []" :key="item" type="danger">{{ item }}</el-tag>
            </el-space>
            <div class="mt16">建议动作：</div>
            <ul class="suggestion-list">
              <li v-for="item in diagnosis.actionSuggestions || []" :key="item">{{ item }}</li>
            </ul>
          </template>
          <el-empty v-else description="暂无诊断数据" />
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card shadow="never">
          <template #header><span>工作台推荐</span></template>
          <el-table :data="workbench.activeRecommendations || []">
            <el-table-column label="标题" prop="title" min-width="180" />
            <el-table-column label="类型" prop="resourceType" width="100" />
            <el-table-column label="推荐理由" prop="recommendReason" min-width="220" show-overflow-tooltip />
          </el-table>
        </el-card>

        <el-card shadow="never" class="mt16">
          <template #header><span>最近动态</span></template>
          <el-tabs>
            <el-tab-pane label="学习记录">
              <el-table :data="workbench.recentStudyRecords || []">
                <el-table-column label="行为类型" prop="behaviorType" width="120" />
                <el-table-column label="时长(秒)" prop="durationSeconds" width="100" />
                <el-table-column label="进度" prop="progressRate" width="100" />
                <el-table-column label="时间" prop="createTime" min-width="160" />
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="考试记录">
              <el-table :data="workbench.recentExamRecords || []">
                <el-table-column label="记录ID" prop="recordId" width="100" />
                <el-table-column label="得分" prop="score" width="100" />
                <el-table-column label="正确率" prop="correctRate" width="100" />
                <el-table-column label="状态" prop="examStatus" width="100" />
                <el-table-column label="交卷时间" prop="submitTime" min-width="160" />
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="预警">
              <el-table :data="workbench.recentWarnings || []">
                <el-table-column label="等级" prop="warningLevel" width="100" />
                <el-table-column label="内容" prop="warningContent" min-width="220" show-overflow-tooltip />
                <el-table-column label="状态" prop="processStatus" width="100" />
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="报告">
              <el-table :data="workbench.recentReports || []">
                <el-table-column label="类型" prop="reportType" width="140" />
                <el-table-column label="内容" prop="reportContent" min-width="240" show-overflow-tooltip />
                <el-table-column label="时间" prop="generateTime" min-width="160" />
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getLearningDiagnosis, getLearningWorkbench } from '@/api/campus/analysis'
import { getStudentDashboard } from '@/api/campus/overview'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'

const queryParams = reactive<any>({ userId: undefined, courseId: undefined, recommendLimit: 5 })
const dashboard = ref<any>({})
const diagnosis = ref<any>(null)
const workbench = ref<any>({ activeRecommendations: [], recentStudyRecords: [], recentExamRecords: [], recentWarnings: [], recentReports: [] })
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
  if (!queryParams.userId) {
    return
  }
  const [dashboardRes, diagnosisRes, workbenchRes] = await Promise.all([
    getStudentDashboard(queryParams),
    getLearningDiagnosis({ userId: queryParams.userId, courseId: queryParams.courseId, recommendLimit: 5, autoGenerate: true }),
    getLearningWorkbench({ userId: queryParams.userId, courseId: queryParams.courseId, limit: 5 }),
  ])
  dashboard.value = dashboardRes.data || {}
  diagnosis.value = diagnosisRes.data || null
  workbench.value = workbenchRes.data || {}
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
.value.warning { color: var(--el-color-warning); }
.mt16 { margin-top: 16px; }
.mt8 { margin-top: 8px; }
.profile-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; line-height: 1.9; }
.suggestion-list { margin: 8px 0 0; padding-left: 18px; line-height: 1.9; }
</style>
