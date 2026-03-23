<template>
  <div class="portal-page service-home">
    <section class="service-home__hero portal-card service-home__hero--teacher">
      <div>
        <div class="service-home__eyebrow">Teacher Portal</div>
        <div class="service-home__title">教师服务大厅</div>
        <p class="service-home__desc">
          聚焦课程、课表和教学资源，把教师日常最常用的入口保留下来，页面更轻更直接。
        </p>
      </div>
      <div class="service-home__hero-stats">
        <div class="service-home__hero-metric">
          <span>课程数</span>
          <strong>{{ data.courseCount || 0 }}</strong>
        </div>
        <div class="service-home__hero-metric">
          <span>资源数</span>
          <strong>{{ data.resourceCount || 0 }}</strong>
        </div>
      </div>
    </section>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">课程数</div><div class="value">{{ data.courseCount || 0 }}</div><div class="sub">当前负责课程数量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">班级数</div><div class="value">{{ data.classCount || 0 }}</div><div class="sub">覆盖班级范围</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">资源数</div><div class="value">{{ data.resourceCount || 0 }}</div><div class="sub">教学资源累计数</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">题目数</div><div class="value">{{ data.questionCount || 0 }}</div><div class="sub">题库维护规模</div></el-card>
    </section>

    <div class="service-group-grid mt20">
      <section v-for="group in groups" :key="group.key" class="service-group-card portal-card">
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
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getTeacherDashboard } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const userStore = usePortalUserStore()
const data = ref<any>({})

const groups = [
  {
    key: 'general',
    label: '综合服务',
    desc: '总览教学指标和资源协同入口。',
    icon: 'ri-dashboard-line',
    items: [
      { title: '教学概览', path: '/teacher/dashboard', desc: '课程、班级与资源概况' },
      { title: '教学资源', path: '/teacher/resources', desc: '资源查看与更新' },
    ],
  },
  {
    key: 'course',
    label: '课程与课表',
    desc: '围绕授课课程和课表组织日常教学。',
    icon: 'ri-book-open-line',
    items: [
      { title: '我的课程', path: '/teacher/courses', desc: '查看本学期授课安排' },
      { title: '我的课表', path: '/teacher/schedule', desc: '按周查看教学安排' },
    ],
  },
]

function go(path: string) {
  router.push(path)
}

async function loadData() {
  const teacherId = userStore.user?.userId
  if (!teacherId) return
  const res = await getTeacherDashboard({ teacherId })
  data.value = res.data || {}
}

onMounted(loadData)
</script>

<style scoped>
.service-home__hero {
  display: grid;
  grid-template-columns: minmax(0, 1.3fr) minmax(280px, 0.7fr);
  gap: 18px;
  padding: 24px;
}
.service-home__hero--teacher {
  background:
    radial-gradient(circle at top left, rgba(24, 148, 106, 0.12) 0%, rgba(24, 148, 106, 0) 32%),
    linear-gradient(135deg, #ffffff 0%, #f2fbf8 100%);
}
.service-home__eyebrow {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(24, 148, 106, 0.12);
  color: #12795a;
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
  color: #12795a;
}
.service-group-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
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
  color: #12795a;
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
  background: linear-gradient(180deg, #ffffff 0%, #f7fcfa 100%);
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
@media (max-width: 960px) {
  .service-home__hero {
    grid-template-columns: 1fr;
  }
  .service-group-grid {
    grid-template-columns: 1fr;
  }
}
</style>
