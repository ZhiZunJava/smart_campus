<template>
  <div class="portal-page service-home">
    <section class="service-home__hero portal-card service-home__hero--parent">
      <div>
        <div class="service-home__eyebrow">Parent Portal</div>
        <div class="service-home__title">家长服务大厅</div>
        <p class="service-home__desc">
          现在只保留课程、课表和考试相关的常用服务，让家长端聚焦在真正需要查看的信息上。
        </p>
      </div>
      <div class="service-home__hero-stats">
        <div class="service-home__hero-metric">
          <span>考试记录</span>
          <strong>{{ data.examRecordCount || 0 }}</strong>
        </div>
        <div class="service-home__hero-metric">
          <span>绑定学生</span>
          <strong>{{ data.studentUserId || '--' }}</strong>
        </div>
      </div>
    </section>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">考试记录</div><div class="value">{{ data.examRecordCount || 0 }}</div><div class="sub">近期考试与练习情况</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">绑定学生</div><div class="value">{{ data.studentUserId || '--' }}</div><div class="sub">当前查看的学生账号</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">家长账号</div><div class="value">{{ data.parentUserId || '--' }}</div><div class="sub">当前登录家长身份</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">服务状态</div><div class="value">精简</div><div class="sub">已移除画像、预警与报告</div></el-card>
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
import { getParentDashboard } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const userStore = usePortalUserStore()
const data = ref<any>({})

const groups = [
  {
    key: 'general',
    label: '综合服务',
    desc: '保留家长最常用的课程与概览入口。',
    icon: 'ri-home-heart-line',
    items: [
      { title: '孩子概览', path: '/parent/dashboard', desc: '查看孩子课程与考试概况' },
    ],
  },
  {
    key: 'course',
    label: '课程与课表',
    desc: '查看孩子当前课程安排与每周课表。',
    icon: 'ri-calendar-schedule-line',
    items: [
      { title: '孩子课程', path: '/parent/courses', desc: '查看孩子当前学期课程' },
      { title: '孩子课表', path: '/parent/schedule', desc: '按周查看孩子课程安排' },
    ],
  },
]

function go(path: string) {
  router.push(path)
}

async function loadData() {
  const parentUserId = userStore.user?.userId
  if (!parentUserId) return
  const res = await getParentDashboard({ parentUserId })
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
.service-home__hero--parent {
  background:
    radial-gradient(circle at top left, rgba(220, 91, 73, 0.12) 0%, rgba(220, 91, 73, 0) 32%),
    linear-gradient(135deg, #ffffff 0%, #fff6f4 100%);
}
.service-home__eyebrow {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(220, 91, 73, 0.12);
  color: #c2412d;
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
  color: #c2412d;
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
  color: #c2412d;
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
  background: linear-gradient(180deg, #ffffff 0%, #fff8f6 100%);
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
