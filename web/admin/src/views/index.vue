<template>
  <div class="dashboard-page">
    <section class="ops-hero">
      <div class="ops-hero__main">
        <div class="ops-hero__eyebrow">首页概览</div>
        <h1>智慧校园运营首页</h1>
        <p>聚焦用户基础、教务基础和待处理事项，首页直接给出可执行信息，不再展示空泛介绍。</p>
        <div class="ops-hero__chips">
          <span class="ops-chip">当前学期：{{ currentTermLabel }}</span>
          <span class="ops-chip">启用学期：{{ metrics.enabledTermCount }}</span>
          <span class="ops-chip">待排课程：{{ metrics.pendingArrangeCount }}</span>
        </div>
      </div>
      <div class="ops-hero__side">
        <div class="hero-stat">
          <span>用户总量</span>
          <strong>{{ metrics.userTotal }}</strong>
          <small>学生、教师、家长、管理员统一管理</small>
        </div>
        <div class="hero-stat hero-stat--warn">
          <span>学情预警</span>
          <strong>{{ dashboard.warningCount || 0 }}</strong>
          <small>{{ warningHint }}</small>
        </div>
      </div>
    </section>

    <el-row :gutter="16" class="metric-row">
      <el-col v-for="item in topMetrics" :key="item.title" :xs="24" :sm="12" :xl="6">
        <el-card class="metric-card" shadow="never">
          <div class="metric-card__label">{{ item.title }}</div>
          <div class="metric-card__value">{{ item.value }}</div>
          <div class="metric-card__desc">{{ item.desc }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="content-row">
      <el-col :xs="24" :xl="14">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="section-card__header">
              <span>用户基础</span>
              <span class="section-card__sub">多类型用户与教务档案底座</span>
            </div>
          </template>
          <div class="info-grid info-grid--users">
            <div v-for="item in userMetrics" :key="item.title" class="info-tile">
              <div class="info-tile__title">{{ item.title }}</div>
              <div class="info-tile__value">{{ item.value }}</div>
              <div class="info-tile__desc">{{ item.desc }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :xl="10">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="section-card__header">
              <span>教务基础</span>
              <span class="section-card__sub">当前可用于排课与教学运行的数据规模</span>
            </div>
          </template>
          <div class="info-grid">
            <div v-for="item in teachingMetrics" :key="item.title" class="info-tile info-tile--compact">
              <div class="info-tile__title">{{ item.title }}</div>
              <div class="info-tile__value">{{ item.value }}</div>
              <div class="info-tile__desc">{{ item.desc }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="content-row">
      <el-col :xs="24" :lg="14">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="section-card__header">
              <span>待处理事项</span>
              <span class="section-card__sub">首页直接给出有动作价值的提醒</span>
            </div>
          </template>
          <div class="task-list">
            <div v-for="item in actionItems" :key="item.title" class="task-item" @click="navigateTo(item.path)">
              <div class="task-item__main">
                <div class="task-item__title">{{ item.title }}</div>
                <div class="task-item__desc">{{ item.desc }}</div>
              </div>
              <div class="task-item__value">{{ item.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="10">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="section-card__header">
              <span>快捷入口</span>
              <span class="section-card__sub">高频管理入口</span>
            </div>
          </template>
          <div class="quick-grid">
            <div v-for="item in quickLinks" :key="item.title" class="quick-item" @click="navigateTo(item.path)">
              <div class="quick-item__title">{{ item.title }}</div>
              <div class="quick-item__desc">{{ item.desc }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import useUserStore from '@/store/modules/user'
import { getCampusDashboard } from '@/api/campus/overview'
import { listGrade, listClass, listCourse, listClassCourse, listCourseSchedule, listSchoolTerm, listClassroom } from '@/api/campus/teaching'
import { listUser } from '@/api/system/user'

const router = useRouter()
const userStore = useUserStore()

const dashboard = reactive<any>({})
const metrics = reactive<any>({
  userTotal: 0,
  studentCount: 0,
  teacherCount: 0,
  parentCount: 0,
  adminCount: 0,
  gradeCount: 0,
  classCount: 0,
  courseCount: 0,
  classCourseCount: 0,
  scheduleCount: 0,
  classroomCount: 0,
  termCount: 0,
  enabledTermCount: 0,
  pendingArrangeCount: 0,
  arrangedClassCourseCount: 0,
})

const currentTermLabel = computed(() => {
  const label = dashboard.currentTermName || dashboard.currentTermLabel
  return label || '未识别当前学期'
})

const warningHint = computed(() => {
  const count = Number(dashboard.warningCount || 0)
  if (count <= 0) return '当前没有学情预警'
  if (count <= 5) return '建议今日内完成核查'
  return '建议优先进入学情预警页处理'
})

const topMetrics = computed(() => [
  { title: '教学班关系', value: metrics.classCourseCount, desc: '班级课程正式绑定数量' },
  { title: '已排课教学班', value: metrics.arrangedClassCourseCount, desc: '已有排课安排的教学班' },
  { title: '待排课教学班', value: metrics.pendingArrangeCount, desc: '总课时尚未排满，建议优先处理' },
  { title: '学习行为', value: dashboard.studyRecordCount || 0, desc: '当前账号关联的学习行为采集量' },
])

const userMetrics = computed(() => [
  { title: '学生', value: metrics.studentCount, desc: '学生账号规模' },
  { title: '教师', value: metrics.teacherCount, desc: '任课与班主任基础' },
  { title: '家长', value: metrics.parentCount, desc: '家校协同用户基础' },
  { title: '管理员', value: metrics.adminCount, desc: '后台管理与教务账号' },
  { title: '全部用户', value: metrics.userTotal, desc: '统一身份认证账号总量' },
  { title: '考试记录', value: dashboard.examRecordCount || 0, desc: '当前账号关联测评记录' },
])

const teachingMetrics = computed(() => [
  { title: '年级', value: metrics.gradeCount, desc: '年级基础数据' },
  { title: '班级', value: metrics.classCount, desc: '班级组织规模' },
  { title: '课程', value: metrics.courseCount, desc: '课程主数据规模' },
  { title: '排课表', value: metrics.scheduleCount, desc: '正式排课记录总数' },
  { title: '教室', value: metrics.classroomCount, desc: '可排课教室资源' },
  { title: '学期', value: metrics.enabledTermCount, desc: `启用 ${metrics.enabledTermCount} / 总计 ${metrics.termCount}` },
])

const actionItems = computed(() => [
  { title: '处理待排课教学班', value: metrics.pendingArrangeCount, desc: '优先进入班级课程或排课表，完成未排满课程的安排。', path: '/campus/classCourse' },
  { title: '查看学情预警', value: dashboard.warningCount || 0, desc: '核查当前学习风险并制定干预动作。', path: '/campus/analysis/warning' },
  { title: '检查班级课表', value: metrics.scheduleCount, desc: '进入班级课表核对教室与时间冲突。', path: '/campus/classSchedule' },
  { title: '维护教室资源', value: metrics.classroomCount, desc: '补齐教室、校区、类型与容量基础数据。', path: '/campus/classroom' },
])

const quickLinks = [
  { title: '用户与身份', desc: '管理学生、教师、家长与后台账号', path: '/system/user' },
  { title: '班级课程', desc: '维护教学班、教师、总课时与完成率', path: '/campus/classCourse' },
  { title: '排课表', desc: '进行手工排课与自动排课', path: '/campus/courseSchedule' },
  { title: '学情预警', desc: '查看风险学生与预警处理建议', path: '/campus/analysis/warning' },
]

function navigateTo(path: string) {
  router.push(path)
}

async function loadDashboard() {
  const userId = Number(userStore.id || 0)
  if (!userId) return
  const res = await getCampusDashboard({ userId, recommendLimit: 5 })
  Object.assign(dashboard, res.data || {})
}

async function loadUserMetrics() {
  const [allRes, studentRes, teacherRes, parentRes, adminRes] = await Promise.all([
    listUser({ pageNum: 1, pageSize: 1 }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'student' }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'teacher' }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'parent' }),
    listUser({ pageNum: 1, pageSize: 1, userType: 'admin' }),
  ])
  metrics.userTotal = Number(allRes.total || 0)
  metrics.studentCount = Number(studentRes.total || 0)
  metrics.teacherCount = Number(teacherRes.total || 0)
  metrics.parentCount = Number(parentRes.total || 0)
  metrics.adminCount = Number(adminRes.total || 0)
}

async function loadTeachingMetrics() {
  const [gradeRes, classRes, courseRes, classCourseRes, scheduleRes, termRes, classroomRes] = await Promise.all([
    listGrade({ pageNum: 1, pageSize: 1 }),
    listClass({ pageNum: 1, pageSize: 1 }),
    listCourse({ pageNum: 1, pageSize: 1 }),
    listClassCourse({ pageNum: 1, pageSize: 500 }),
    listCourseSchedule({ pageNum: 1, pageSize: 1 }),
    listSchoolTerm({ pageNum: 1, pageSize: 200 }),
    listClassroom({ pageNum: 1, pageSize: 1 }),
  ])

  const classCourseRows = classCourseRes.rows || []

  metrics.gradeCount = Number(gradeRes.total || 0)
  metrics.classCount = Number(classRes.total || 0)
  metrics.courseCount = Number(courseRes.total || 0)
  metrics.classCourseCount = Number(classCourseRes.total || classCourseRows.length || 0)
  metrics.scheduleCount = Number(scheduleRes.total || 0)
  metrics.classroomCount = Number(classroomRes.total || 0)
  metrics.termCount = Number(termRes.total || termRes.rows?.length || 0)
  metrics.enabledTermCount = (termRes.rows || []).filter((item: any) => item.status === '0').length
  metrics.pendingArrangeCount = classCourseRows.filter((item: any) => Number(item.totalHours || 0) > Number(item.arrangedHours || 0)).length
  metrics.arrangedClassCourseCount = classCourseRows.filter((item: any) => Number(item.arrangedHours || 0) > 0).length

  const currentTerm = (termRes.rows || []).find((item: any) => item.isCurrent === '1')
  if (currentTerm) {
    dashboard.currentTermName = currentTerm.termName
    dashboard.currentTermLabel = `${currentTerm.termName}（${currentTerm.schoolYear}）`
  }
}

onMounted(async () => {
  await Promise.all([loadDashboard(), loadUserMetrics(), loadTeachingMetrics()])
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
}

.ops-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.45fr) minmax(280px, 0.55fr);
  gap: 16px;
  padding: 22px 24px;
  border-radius: 6px;
  background: linear-gradient(135deg, #ffffff 0%, #f5f9ff 58%, #eef4ff 100%);
  border: 1px solid var(--el-border-color-lighter);
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.04);
}

.ops-hero__eyebrow {
  color: #4f6b92;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.ops-hero__main h1 {
  margin: 10px 0 10px;
  color: #15283f;
  font-size: 30px;
  line-height: 1.2;
}

.ops-hero__main p {
  margin: 0;
  max-width: 760px;
  color: var(--el-text-color-regular);
  font-size: 14px;
  line-height: 1.9;
}

.ops-hero__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.ops-chip {
  display: inline-flex;
  align-items: center;
  height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  background: #ffffff;
  border: 1px solid #d9e4f2;
  color: #365074;
  font-size: 12px;
  font-weight: 700;
}

.ops-hero__side {
  display: grid;
  gap: 12px;
}

.hero-stat {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 108px;
  padding: 18px;
  border-radius: 6px;
  background: #ffffff;
  border: 1px solid #dbe5f2;
}

.hero-stat span,
.hero-stat small {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.hero-stat strong {
  margin: 8px 0 6px;
  color: #153d7a;
  font-size: 28px;
  line-height: 1.05;
}

.hero-stat--warn strong {
  color: #c45500;
}

.metric-row,
.content-row {
  margin: 0 !important;
}

.metric-card,
.section-card {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
}

.metric-card :deep(.el-card__body) {
  padding: 16px 18px;
}

.metric-card__label {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.metric-card__value {
  margin: 10px 0 8px;
  color: #15283f;
  font-size: 28px;
  font-weight: 800;
}

.metric-card__desc {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.7;
}

.section-card :deep(.el-card__header) {
  padding: 16px 18px 0;
  border-bottom: none;
}

.section-card :deep(.el-card__body) {
  padding: 16px 18px 18px;
}

.section-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 16px;
  font-weight: 700;
}

.section-card__sub {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  font-weight: 500;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.info-grid--users {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.info-tile {
  padding: 16px;
  border-radius: 6px;
  background: var(--el-fill-color-extra-light);
  border: 1px solid var(--el-border-color-lighter);
}

.info-tile--compact {
  padding: 14px;
}

.info-tile__title {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.info-tile__value {
  margin: 8px 0 6px;
  color: #15283f;
  font-size: 24px;
  font-weight: 800;
}

.info-tile__desc {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.7;
}

.task-list,
.quick-grid {
  display: grid;
  gap: 12px;
}

.task-item,
.quick-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px;
  border-radius: 6px;
  background: var(--el-fill-color-extra-light);
  border: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  transition: border-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
}

.task-item:hover,
.quick-item:hover {
  border-color: rgba(0, 110, 255, 0.18);
  transform: translateY(-1px);
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.06);
}

.task-item__title,
.quick-item__title {
  color: #15283f;
  font-size: 14px;
  font-weight: 700;
}

.task-item__desc,
.quick-item__desc {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.7;
}

.task-item__value {
  color: #006eff;
  font-size: 28px;
  font-weight: 800;
  line-height: 1;
}

@media (max-width: 1200px) {
  .ops-hero {
    grid-template-columns: 1fr;
  }

  .info-grid--users {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: 16px;
  }

  .info-grid,
  .info-grid--users {
    grid-template-columns: 1fr;
  }
}
</style>
