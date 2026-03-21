<template>
  <div class="portal-page service-home">
    <section class="service-home__hero portal-card">
      <div>
        <div class="service-home__eyebrow">Student Portal</div>
        <div class="service-home__title">学生服务大厅</div>
        <p class="service-home__desc">
          从课程、资源、考试、错题到任务挑战，所有学习服务集中在这里。你可以像使用完整校园门户一样，快速进入对应模块。
        </p>
      </div>
      <div class="service-home__hero-stats">
        <div class="service-home__hero-metric">
          <span>能力等级</span>
          <strong>{{ dashboard.profile?.abilityLevel || '--' }}</strong>
        </div>
        <div class="service-home__hero-metric">
          <span>掌握度</span>
          <strong>{{ dashboard.profile?.masteryScore || 0 }}</strong>
        </div>
      </div>
    </section>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">学习行为</div><div class="value">{{ dashboard.studyRecordCount || 0 }}</div><div class="sub">累计行为采集次数</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">考试记录</div><div class="value">{{ dashboard.examRecordCount || 0 }}</div><div class="sub">阶段考试与练习记录</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">预警数量</div><div class="value">{{ dashboard.warningCount || 0 }}</div><div class="sub">待关注学情风险</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">推荐资源</div><div class="value">{{ (workbench.activeRecommendations || []).length }}</div><div class="sub">当前推荐学习任务</div></el-card>
    </section>

    <div class="service-group-grid mt20">
      <section v-for="group in displayGroups" :key="group.key" class="service-group-card portal-card">
        <div class="service-group-card__header">
          <div>
            <h3>{{ group.label }}</h3>
            <p>{{ group.desc }}</p>
          </div>
          <i :class="group.icon"></i>
        </div>
        <div class="service-group-card__list">
          <button v-for="item in group.items" :key="item.path" type="button" class="service-link" @click="go(item.path)">
            <strong>{{ item.title }}</strong>
            <span>{{ item.desc }}</span>
          </button>
        </div>
      </section>
    </div>

    <div class="portal-grid portal-grid-2 mt20">
      <el-card class="portal-card portal-soft-card">
        <template #header><span>诊断与建议</span></template>
        <template v-if="diagnosis">
          <el-alert :title="diagnosis.overallSummary" type="info" :closable="false" />
          <div class="mt16">
            <div class="service-subtitle">风险标签</div>
            <div class="portal-chip-list">
              <el-tag v-for="item in diagnosis.riskTags || []" :key="item" type="danger">{{ item }}</el-tag>
              <el-tag v-if="!(diagnosis.riskTags || []).length" type="info">暂无明显风险标签</el-tag>
            </div>
          </div>
        </template>
        <el-empty v-else description="暂无诊断数据" />
      </el-card>

      <el-card class="portal-card portal-soft-card">
        <template #header><span>最近考试</span></template>
        <el-table :data="workbench.recentExamRecords || []" size="small">
          <el-table-column prop="recordId" label="记录ID" width="90" />
          <el-table-column prop="score" label="得分" width="100" />
          <el-table-column prop="correctRate" label="正确率" width="100" />
          <el-table-column prop="submitTime" label="交卷时间" min-width="160" />
        </el-table>
        <el-empty v-if="!(workbench.recentExamRecords || []).length" description="暂无考试记录" />
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getLearningDiagnosis, getLearningWorkbench, getStudentDashboard } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const userStore = usePortalUserStore()
const dashboard = ref<any>({})
const diagnosis = ref<any>(null)
const workbench = ref<any>({ activeRecommendations: [], recentStudyRecords: [], recentExamRecords: [], recentWarnings: [] })
const latestOngoingRecord = computed(() => userStore.ongoingExam)

const groups = [
  {
    key: 'general',
    label: '综合服务',
    desc: '学习入口、资源与智能辅助统一收口。',
    icon: 'ri-service-line',
    items: [
      { title: '学习首页', path: '/student/dashboard', desc: '诊断、工作台与关键指标' },
      { title: '资源中心', path: '/student/resources', desc: '筛选资源并记录学习行为' },
      { title: '智能问答', path: '/student/qa', desc: '基于课程与学习问题问答' },
    ],
  },
  {
    key: 'course',
    label: '课程与选课',
    desc: '围绕课程、课表与学习路径组织学习任务。',
    icon: 'ri-book-open-line',
    items: [
      { title: '我的课程', path: '/student/courses', desc: '查看学期课程与班级课程' },
      { title: '我的课表', path: '/student/schedule', desc: '按周查看课程安排' },
      { title: '个性推荐', path: '/student/recommendations', desc: '主动反馈推荐结果' },
    ],
  },
  {
    key: 'plaza',
    label: '任务与挑战',
    desc: '进入开放挑战和通用任务广场，扩展综合能力。',
    icon: 'ri-rocket-2-line',
    items: [
      { title: '任务广场', path: '/student/plaza', desc: '浏览通用试卷、开放挑战与通用题目' },
    ],
  },
  {
    key: 'growth',
    label: '考试与成长',
    desc: '沉淀考试记录、错题闭环与持续成长轨迹。',
    icon: 'ri-medal-line',
    items: [
      { title: '继续考试', path: '/student/exams?resume=1&tab=papers', desc: '快速回到最近一场未完成考试' },
      { title: '我的考试', path: '/student/exams', desc: '考试、记录与错题闭环' },
      { title: '我的错题本', path: '/student/wrongbook', desc: '错题回顾与复盘' },
    ],
  },
]

const displayGroups = computed(() =>
  groups.map((group) => {
    if (group.key !== 'growth' || latestOngoingRecord.value) {
      return group
    }
    return {
      ...group,
      items: group.items.filter((item) => item.title !== '继续考试'),
    }
  }),
)

function go(path: string) {
  router.push(path)
}

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

<style scoped>
.service-home__hero {
  display: grid;
  grid-template-columns: minmax(0, 1.3fr) minmax(280px, 0.7fr);
  gap: 18px;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(47, 107, 255, 0.12) 0%, rgba(47, 107, 255, 0) 32%),
    linear-gradient(135deg, #ffffff 0%, #f5f9ff 100%);
}
.service-home__eyebrow {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(49, 95, 202, 0.1);
  color: #315fca;
  font-size: 12px;
  font-weight: 700;
}
.service-home__title {
  margin-top: 12px;
  font-size: 32px;
  font-weight: 800;
  color: var(--portal-text);
}
.service-home__desc {
  margin-top: 10px;
  max-width: 760px;
  font-size: 14px;
  line-height: 1.9;
  color: var(--portal-text-secondary);
}
.service-home__hero-stats {
  display: grid;
  gap: 14px;
}
.service-home__hero-metric {
  padding: 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid var(--portal-border);
}
.service-home__hero-metric span {
  color: var(--portal-text-secondary);
  font-size: 13px;
}
.service-home__hero-metric strong {
  display: block;
  margin-top: 8px;
  font-size: 30px;
  color: var(--portal-brand);
}
.service-group-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}
.service-group-card {
  padding: 18px;
}
.service-group-card__header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 16px;
}
.service-group-card__header h3 {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  color: var(--portal-text);
}
.service-group-card__header p {
  margin: 8px 0 0;
  font-size: 13px;
  line-height: 1.8;
  color: var(--portal-text-secondary);
}
.service-group-card__header i {
  font-size: 26px;
  color: var(--portal-brand);
}
.service-group-card__list {
  display: grid;
  gap: 12px;
}
.service-link {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 12px 14px;
  text-align: left;
  border: 1px solid var(--portal-border);
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  cursor: pointer;
  transition: all .2s ease;
}
.service-link:hover {
  transform: translateY(-1px);
  border-color: var(--portal-border-strong);
  box-shadow: var(--portal-shadow-soft);
}
.service-link strong {
  font-size: 16px;
  color: var(--portal-text);
}
.service-link span {
  font-size: 12px;
  line-height: 1.7;
  color: var(--portal-text-secondary);
}
.service-subtitle {
  font-size: 13px;
  font-weight: 700;
  color: var(--portal-text);
}
@media (max-width: 1200px) {
  .service-group-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: 960px) {
  .service-home__hero {
    grid-template-columns: 1fr;
  }
  .service-group-grid {
    grid-template-columns: 1fr;
  }
}
</style>
