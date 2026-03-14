<template>
  <div class="portal-page">
    <section class="portal-hero">
      <div>
        <div class="portal-hero__badge">Student Workbench</div>
        <div class="portal-hero__title">欢迎回来，{{ userName }}</div>
        <p class="portal-hero__desc">
          这里会集中展示你的学情诊断、学习建议、最近考试与推荐资源，帮助你更快找到当前阶段最值得做的事情。
        </p>
      </div>
      <div class="portal-hero__panel">
        <div class="portal-hero__metric">
          <span>风险等级</span>
          <strong>{{ diagnosis?.riskLevel || '--' }}</strong>
        </div>
        <div class="portal-hero__metric">
          <span>能力等级</span>
          <strong>{{ dashboard.profile?.abilityLevel || '--' }}</strong>
        </div>
      </div>
    </section>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">学习行为</div><div class="value">{{ dashboard.studyRecordCount || 0 }}</div><div class="sub">累计行为采集次数</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">考试记录</div><div class="value">{{ dashboard.examRecordCount || 0 }}</div><div class="sub">阶段考试与练习记录</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">预警数量</div><div class="value">{{ dashboard.warningCount || 0 }}</div><div class="sub">待关注学情风险</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">掌握度</div><div class="value">{{ dashboard.profile?.masteryScore || 0 }}</div><div class="sub">当前课程掌握度</div></el-card>
    </section>

    <div class="portal-grid portal-grid-2 mt20">
      <el-card class="portal-card portal-soft-card">
        <template #header><span>学习画像</span></template>
        <div v-if="dashboard.profile" class="portal-grid portal-grid-2">
          <div class="portal-surface">能力等级：{{ dashboard.profile.abilityLevel }}</div>
          <div class="portal-surface">活跃度：{{ dashboard.profile.activeScore }}</div>
          <div class="portal-surface">专注度：{{ dashboard.profile.concentrationScore }}</div>
          <div class="portal-surface">掌握度：{{ dashboard.profile.masteryScore }}</div>
          <div class="portal-surface">兴趣度：{{ dashboard.profile.interestScore }}</div>
          <div class="portal-surface">风险分：{{ dashboard.profile.riskScore }}</div>
        </div>
        <el-empty v-else description="暂无画像数据" />
      </el-card>

      <el-card class="portal-card portal-soft-card">
        <template #header><span>诊断结论</span></template>
        <template v-if="diagnosis">
          <el-alert :title="diagnosis.overallSummary" type="info" :closable="false" />
          <div class="mt16">
            <div class="portal-section-title"><h3>风险标签</h3></div>
            <div class="portal-chip-list">
              <el-tag v-for="item in diagnosis.riskTags || []" :key="item" type="danger">{{ item }}</el-tag>
              <el-tag v-if="!(diagnosis.riskTags || []).length" type="info">当前暂无明显风险标签</el-tag>
            </div>
          </div>
          <div class="mt16">
            <div class="portal-section-title"><h3>行动建议</h3></div>
            <ul class="portal-simple-list">
              <li v-for="item in diagnosis.actionSuggestions || []" :key="item">{{ item }}</li>
            </ul>
          </div>
        </template>
        <el-empty v-else description="暂无诊断数据" />
      </el-card>
    </div>

    <div class="portal-grid portal-grid-2 mt20">
      <el-card class="portal-card">
        <template #header><span>当前推荐</span></template>
        <el-table :data="workbench.activeRecommendations || []">
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="resourceType" label="类型" width="100" />
          <el-table-column prop="recommendReason" label="推荐理由" min-width="220" show-overflow-tooltip />
        </el-table>
        <el-empty v-if="!(workbench.activeRecommendations || []).length" description="暂无推荐资源" />
      </el-card>

      <el-card class="portal-card">
        <template #header><span>最近考试</span></template>
        <el-table :data="workbench.recentExamRecords || []">
          <el-table-column prop="recordId" label="记录ID" width="90" />
          <el-table-column prop="score" label="得分" width="100" />
          <el-table-column prop="correctRate" label="正确率" width="100" />
          <el-table-column prop="examStatus" label="状态" width="100" />
          <el-table-column prop="submitTime" label="交卷时间" min-width="160" />
        </el-table>
        <el-empty v-if="!(workbench.recentExamRecords || []).length" description="暂无考试记录" />
      </el-card>
    </div>

    <el-card class="portal-card mt20">
      <template #header><span>最近预警与学习动态</span></template>
      <el-tabs>
        <el-tab-pane label="预警提醒">
          <el-table :data="workbench.recentWarnings || []">
            <el-table-column prop="warningType" label="预警类型" width="120" />
            <el-table-column prop="warningLevel" label="等级" width="100" />
            <el-table-column prop="warningContent" label="预警内容" min-width="220" show-overflow-tooltip />
            <el-table-column prop="suggestion" label="建议" min-width="220" show-overflow-tooltip />
          </el-table>
          <el-empty v-if="!(workbench.recentWarnings || []).length" description="暂无预警信息" />
        </el-tab-pane>
        <el-tab-pane label="学习记录">
          <el-table :data="workbench.recentStudyRecords || []">
            <el-table-column prop="behaviorType" label="行为类型" width="120" />
            <el-table-column prop="durationSeconds" label="时长(秒)" width="100" />
            <el-table-column prop="progressRate" label="进度" width="100" />
            <el-table-column prop="createTime" label="时间" min-width="160" />
          </el-table>
          <el-empty v-if="!(workbench.recentStudyRecords || []).length" description="暂无学习记录" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getLearningDiagnosis, getLearningWorkbench, getStudentDashboard } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const dashboard = ref<any>({})
const diagnosis = ref<any>(null)
const workbench = ref<any>({ activeRecommendations: [], recentStudyRecords: [], recentExamRecords: [], recentWarnings: [] })

const userName = computed(() => userStore.user?.nickName || userStore.user?.userName || '同学')

async function loadData() {
  const userId = userStore.user?.userId
  if (!userId) return
  const [dashboardRes, diagnosisRes, workbenchRes] = await Promise.all([
    getStudentDashboard({ userId, recommendLimit: 5 }),
    getLearningDiagnosis({ userId, recommendLimit: 5, autoGenerate: true }),
    getLearningWorkbench({ userId, limit: 5 }),
  ])
  dashboard.value = dashboardRes.data || {}
  diagnosis.value = diagnosisRes.data || null
  workbench.value = workbenchRes.data || {}
}

onMounted(loadData)
</script>
