<template>
  <div class="dashboard-page">
    <section class="hero-card">
      <div class="hero-content">
        <div class="hero-copy">
          <el-tag effect="dark" class="hero-tag">智慧校园管理驾驶舱</el-tag>
          <h1>AI 赋能智慧校园自主学习生态系统</h1>
          <p>
            面向学生、教师、管理员与家长，统一承载学习资源、学习画像、学情分析、智能答疑与测评考试，打造数据驱动、过程可追踪、结果可干预的校园学习平台。
          </p>
          <div class="hero-actions">
            <el-button type="primary" @click="navigateTo('/system/user')">进入用户管理</el-button>
            <el-button plain @click="navigateTo('/system/menu')">配置系统菜单</el-button>
          </div>
        </div>
        <div class="hero-panel">
          <div class="panel-title">平台建设重点</div>
          <div class="panel-list">
            <div v-for="item in priorities" :key="item.title" class="panel-item">
              <div class="panel-item__title">{{ item.title }}</div>
              <div class="panel-item__desc">{{ item.desc }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <el-row :gutter="16" class="overview-row">
      <el-col v-for="item in overviewCards" :key="item.title" :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="overview-card">
          <div class="overview-card__icon" :class="item.type">
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
          <div class="overview-card__main">
            <div class="overview-card__label">{{ item.title }}</div>
            <div class="overview-card__value">{{ item.value }}</div>
            <div class="overview-card__desc">{{ item.desc }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="content-row">
      <el-col :xs="24" :lg="14">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="section-card__header">
              <span>核心业务模块</span>
              <span class="section-card__sub">围绕自主学习闭环建设</span>
            </div>
          </template>
          <div class="feature-grid">
            <div v-for="item in featureModules" :key="item.title" class="feature-item">
              <div class="feature-item__top">
                <el-icon class="feature-item__icon"><component :is="item.icon" /></el-icon>
                <span class="feature-item__title">{{ item.title }}</span>
              </div>
              <div class="feature-item__desc">{{ item.desc }}</div>
              <div class="feature-item__meta">{{ item.meta }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="10">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="section-card__header">
              <span>建设建议</span>
              <span class="section-card__sub">建议按阶段推进</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="item in roadmap"
              :key="item.title"
              :timestamp="item.stage"
              placement="top"
              :type="item.type"
            >
              <div class="timeline-title">{{ item.title }}</div>
              <div class="timeline-desc">{{ item.desc }}</div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="content-row bottom-row">
      <el-col :xs="24" :lg="12">
        <el-card class="section-card quick-card" shadow="never">
          <template #header>
            <div class="section-card__header">
              <span>快捷入口</span>
              <span class="section-card__sub">常用后台配置入口</span>
            </div>
          </template>
          <div class="quick-links">
            <div v-for="item in quickLinks" :key="item.title" class="quick-link" @click="navigateTo(item.path)">
              <div class="quick-link__title">{{ item.title }}</div>
              <div class="quick-link__desc">{{ item.desc }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="section-card quick-card" shadow="never">
          <template #header>
            <div class="section-card__header">
              <span>平台说明</span>
              <span class="section-card__sub">可用于项目汇报与演示</span>
            </div>
          </template>
          <ul class="tips-list">
            <li>支持学生、教师、管理员、家长四类用户统一认证与权限管理。</li>
            <li>支持学习资源管理、智能检索、学习过程追踪与 AI 学情分析。</li>
            <li>支持智能答疑、自适应考试、错题沉淀与个性化学习推荐。</li>
            <li>支持后续扩展向量检索、模型配置、风控策略与推荐排序能力。</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import pkg from '../../package.json'
import {
  DataAnalysis,
  Reading,
  ChatDotRound,
  Finished,
  Monitor,
  User,
  Files,
  Notebook,
  TrendCharts,
  SetUp,
} from '@element-plus/icons-vue'

const router = useRouter()

const overviewCards = [
  { title: '当前版本', value: `v${pkg.version}`, desc: '管理端前端版本号', icon: DataAnalysis, type: 'brand' },
  { title: '核心角色', value: '4 类', desc: '学生 / 教师 / 管理员 / 家长', icon: User, type: 'success' },
  { title: '核心能力', value: '6 大', desc: '画像、推荐、答疑、资源、分析、测评', icon: Monitor, type: 'warning' },
  { title: '一期方向', value: '可落地', desc: '适合作为课程设计与毕设演示', icon: Finished, type: 'danger' },
]

const priorities = [
  { title: '统一身份认证', desc: '多角色账号统一接入，结合登录异常识别提升安全性。' },
  { title: '学习数据中台', desc: '沉淀课程、资源、行为、测评、画像等核心学习数据。' },
  { title: 'AI 场景闭环', desc: '从推荐、问答、分析到考试形成教学与学习闭环。' },
]

const featureModules = [
  { title: '个性化学习中心', desc: '学习画像、学习计划、资源推荐、成长追踪。', meta: '画像 + 推荐 + 学习目标', icon: Reading },
  { title: '学习资源中心', desc: '支持视频、文档、PPT、试题等资源统一管理与检索。', meta: '资源管理 + 智能检索', icon: Files },
  { title: '智能答疑助手', desc: '结合课程资料与知识点，提供可追溯的问答服务。', meta: '知识库问答 + 来源引用', icon: ChatDotRound },
  { title: '学情分析预警', desc: '跟踪活跃度、完成率、成绩波动并生成干预建议。', meta: '过程追踪 + 风险预警', icon: TrendCharts },
  { title: '智能测评考试', desc: '支持题库、试卷、在线考试、错题本与自适应测评。', meta: '考试记录 + 自适应出题', icon: Notebook },
  { title: 'AI 运维配置', desc: '统一管理模型配置、Prompt 模板与 AI 调用日志。', meta: '模型配置 + 可运维化', icon: SetUp },
]

const roadmap = [
  { stage: '阶段一', title: '平台底座搭建', desc: '完成用户、课程、班级、资源、菜单与权限基础能力建设。', type: 'primary' },
  { stage: '阶段二', title: '学习闭环打通', desc: '打通资源学习、学习行为、画像、推荐和智能答疑。', type: 'success' },
  { stage: '阶段三', title: '测评分析完善', desc: '补齐学情预警、考试记录、错题本和阶段性学情报告。', type: 'warning' },
  { stage: '阶段四', title: 'AI 增强升级', desc: '接入更强的检索、风控、推荐排序与预测分析能力。', type: 'danger' },
]

const quickLinks = [
  { title: '用户管理', desc: '维护平台账户与角色权限', path: '/system/user' },
  { title: '角色管理', desc: '配置学生/教师/管理员/家长权限', path: '/system/role' },
  { title: '菜单管理', desc: '维护前端导航与功能权限点', path: '/system/menu' },
  { title: '参数设置', desc: '配置模型、策略与系统参数', path: '/system/config' },
]

function navigateTo(path: string) {
  router.push(path)
}
</script>

<style lang="scss" scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
}

.hero-card {
  border-radius: 20px;
  padding: 28px;
  background: linear-gradient(135deg, rgba(0, 110, 255, 0.96), rgba(0, 88, 223, 0.92) 45%, rgba(19, 167, 80, 0.78));
  box-shadow: 0 18px 40px rgba(0, 78, 170, 0.18);
  color: #fff;
}

.hero-content {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  gap: 24px;
  align-items: stretch;
}

.hero-tag {
  margin-bottom: 16px;
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.24);
}

.hero-copy h1 {
  margin: 0 0 14px;
  font-size: 32px;
  line-height: 1.25;
}

.hero-copy p {
  margin: 0;
  max-width: 720px;
  font-size: 15px;
  line-height: 1.9;
  color: rgba(255, 255, 255, 0.88);
}

.hero-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.hero-panel {
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 18px;
  padding: 20px;
  backdrop-filter: blur(10px);
}

.panel-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 16px;
}

.panel-list {
  display: grid;
  gap: 14px;
}

.panel-item__title {
  margin-bottom: 6px;
  font-weight: 600;
}

.panel-item__desc {
  color: rgba(255, 255, 255, 0.82);
  line-height: 1.7;
  font-size: 13px;
}

.overview-card {
  border: none;
  border-radius: 18px;
}

.overview-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
}

.overview-card__icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.overview-card__icon.brand { background: rgba(0, 110, 255, 0.12); color: #006eff; }
.overview-card__icon.success { background: rgba(19, 167, 80, 0.12); color: #13a750; }
.overview-card__icon.warning { background: rgba(240, 105, 0, 0.12); color: #c45500; }
.overview-card__icon.danger { background: rgba(252, 98, 94, 0.12); color: #dc4242; }

.overview-card__label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.overview-card__value {
  margin: 6px 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.overview-card__desc {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.section-card {
  border: none;
  border-radius: 18px;
}

.section-card :deep(.el-card__header) {
  padding: 18px 22px 0;
  border-bottom: none;
}

.section-card :deep(.el-card__body) {
  padding: 18px 22px 22px;
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
  font-size: 12px;
  font-weight: 500;
  color: var(--el-text-color-secondary);
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.feature-item {
  padding: 18px;
  border-radius: 16px;
  background: var(--el-fill-color-extra-light);
  border: 1px solid var(--el-border-color-lighter);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.feature-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.08);
  border-color: rgba(0, 110, 255, 0.2);
}

.feature-item__top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.feature-item__icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #006eff;
  background: rgba(0, 110, 255, 0.1);
}

.feature-item__title {
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.feature-item__desc,
.timeline-desc,
.tips-list li {
  font-size: 13px;
  line-height: 1.8;
  color: var(--el-text-color-secondary);
}

.feature-item__meta {
  margin-top: 10px;
  font-size: 12px;
  color: #006eff;
}

.timeline-title {
  margin-bottom: 6px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.quick-link {
  padding: 18px;
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(0, 110, 255, 0.06), rgba(255, 255, 255, 0));
  border: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-link:hover {
  border-color: rgba(0, 110, 255, 0.26);
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.08);
}

.quick-link__title {
  margin-bottom: 8px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.quick-link__desc {
  font-size: 13px;
  line-height: 1.7;
  color: var(--el-text-color-secondary);
}

.tips-list {
  margin: 0;
  padding-left: 18px;
}

.tips-list li + li {
  margin-top: 10px;
}

@media (max-width: 1200px) {
  .hero-content,
  .feature-grid,
  .quick-links {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: 12px;
  }

  .hero-card {
    padding: 20px;
  }

  .hero-copy h1 {
    font-size: 24px;
  }
}
</style>
