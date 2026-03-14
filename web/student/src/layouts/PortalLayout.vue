<template>
  <div class="portal-shell">
    <PortalTopBar
      :menu-items="menuItems"
      :active-path="route.path"
      :selected-role="activeRole"
      :show-menu="true"
      :show-role-switch="true"
      :show-auth-action="false"
      :show-user-panel="true"
      @role-change="handleRoleChange"
      @logout="handleLogout"
    />

    <section v-if="showBanner" class="portal-banner">
      <div class="portal-banner__inner">
        <div class="portal-banner__content">
          <div class="portal-banner__tag">{{ currentRoleTitle }}</div>
          <h1>{{ currentRouteTitle }}</h1>
          <p>{{ currentRouteDesc }}</p>
        </div>
      </div>
    </section>

    <main class="portal-main" :class="{ 'portal-main--compact': !showBanner }">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import PortalTopBar from '@/components/PortalTopBar.vue'
import usePortalUserStore from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = usePortalUserStore()

const menus: Record<string, Array<{ title: string; path: string; desc: string }>> = {
  student: [
    { title: '学习首页', path: '/student/dashboard', desc: '诊断、工作台与关键指标' },
    { title: '资源中心', path: '/student/resources', desc: '筛选资源并记录学习行为' },
    { title: '个性推荐', path: '/student/recommendations', desc: '主动反馈推荐结果' },
    { title: '智能问答', path: '/student/qa', desc: '基于课程与学习问题问答' },
    { title: '我的考试', path: '/student/exams', desc: '考试、记录与错题闭环' },
    { title: '我的错题本', path: '/student/wrongbook', desc: '错题回顾与复盘' },
  ],
  teacher: [
    { title: '教学概览', path: '/teacher/dashboard', desc: '课程、预警与教学建议' },
    { title: '课程管理', path: '/teacher/courses', desc: '查看课程与学生分布' },
    { title: '教学资源', path: '/teacher/resources', desc: '资源查看与更新' },
    { title: '学情预警', path: '/teacher/warnings', desc: '跟进学生预警与干预' },
  ],
  parent: [
    { title: '孩子概览', path: '/parent/dashboard', desc: '孩子学情、建议与提醒' },
    { title: '预警提醒', path: '/parent/warnings', desc: '查看近期风险与干预建议' },
    { title: '学习报告', path: '/parent/reports', desc: '查看阶段学习报告' },
  ],
}

const roleTitleMap: Record<string, string> = {
  student: '学生端',
  teacher: '教师端',
  parent: '家长端',
}

const activeRole = ref(route.path.split('/')[1] || 'student')
watch(
  () => route.path,
  (path) => {
    activeRole.value = path.split('/')[1] || userStore.preferredPortalRole
  },
  { immediate: true }
)

const menuItems = computed(() => menus[activeRole.value] || menus.student)
const currentRoleTitle = computed(() => roleTitleMap[activeRole.value] || '学习门户')
const currentMenu = computed(() => menuItems.value.find((item) => item.path === route.path) || menuItems.value[0])
const currentRouteTitle = computed(() => currentMenu.value?.title || currentRoleTitle.value)
const currentRouteDesc = computed(() => currentMenu.value?.desc || '围绕资源、行为、画像、问答与考试构建学习闭环')
const showBanner = computed(() => route.meta.hideBanner !== true)

function handleRoleChange(role: string) {
  userStore.setPreferredPortalRole(role)
  const first = menus[role]?.[0]
  if (first) {
    router.push(first.path)
  }
}

async function handleLogout() {
  await ElMessageBox.confirm('确认退出当前学习门户吗？', '提示', { type: 'warning' })
  await userStore.logoutAction()
  router.replace('/login')
}
</script>

<style scoped>
.portal-shell{min-height:100vh;background:var(--portal-bg)}
.portal-banner{padding:22px 24px 0}.portal-banner__inner{max-width:1600px;margin:0 auto;border-radius:24px;overflow:hidden;background:linear-gradient(100deg,#f3f7fc 0%,#eef4fb 36%,#e8f0fb 60%,#f5f9ff 100%);border:1px solid #e7eef7;position:relative}.portal-banner__inner::after{content:'';position:absolute;right:0;top:0;bottom:0;width:34%;background:linear-gradient(135deg,rgba(44,134,255,.07) 0%,rgba(93,176,255,.17) 100%)}
.portal-banner__content{position:relative;z-index:1;padding:34px 40px;max-width:760px}.portal-banner__tag{display:inline-flex;padding:6px 12px;border-radius:999px;background:#e8f2ff;color:#2c86ff;font-size:12px;font-weight:700}.portal-banner h1{margin:14px 0 10px;font-size:36px;color:#152642}.portal-banner p{margin:0;color:#6e7d92;line-height:1.9;font-size:14px}
.portal-main{max-width:1600px;margin:0 auto;padding-bottom:28px}.portal-main--compact{padding-top:18px}
@media (max-width: 1200px){.portal-banner{padding:16px 16px 0}.portal-banner__content{padding:26px 22px}.portal-banner h1{font-size:28px}}
</style>
