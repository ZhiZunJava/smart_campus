<template>
  <div class="portal-shell">
    <PortalTopBar
      :menu-items="menuItems"
      :active-path="activeMenuPath"
      :selected-role="activeRole"
      :menu-groups="menuGroups"
      :show-menu="true"
      :show-role-switch="true"
      :show-auth-action="false"
      :show-user-panel="true"
      @role-change="handleRoleChange"
      @logout="handleLogout"
    />

    <main
      class="portal-main"
      :class="{
        'portal-main--workspace': isWorkspaceRoute,
      }"
    >
      <el-alert
        v-if="showOngoingExamBanner"
        type="warning"
        :closable="true"
        class="portal-ongoing-banner"
        @close="dismissOngoingBanner"
      >
        <template #title>
          <div class="portal-ongoing-banner__content">
            <span>
              你有一场未完成考试：{{ userStore.ongoingExam?.paperName || `试卷 ${userStore.ongoingExam?.paperId}` }}，
              开始于 {{ formatDateTime(userStore.ongoingExam?.startTime) }}
            </span>
            <el-button type="warning" plain @click="continueOngoingExam">继续考试</el-button>
          </div>
        </template>
      </el-alert>

      <div v-if="!isWorkspaceRoute" class="portal-stage">
        <router-view v-slot="{ Component, route: childRoute }">
          <transition :name="resolveChildTransitionName(childRoute, 'portal-content-shift')" mode="out-in">
            <component :is="Component" :key="childRoute.fullPath" />
          </transition>
        </router-view>
      </div>
      <router-view v-else v-slot="{ Component, route: childRoute }">
        <transition :name="resolveChildTransitionName(childRoute, 'portal-workspace-fade')" mode="out-in">
          <component :is="Component" :key="childRoute.fullPath" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { RouteLocationNormalizedLoaded } from 'vue-router'
import { ElMessageBox } from '@/utils/feedback'
import PortalTopBar from '@/components/PortalTopBar.vue'
import usePortalUserStore from '@/store/user'

interface MenuItem {
  title: string
  path: string
  desc: string
}

interface MenuGroup {
  key: string
  label: string
  items: MenuItem[]
}

const route = useRoute()
const router = useRouter()
const userStore = usePortalUserStore()

const menus: Record<string, MenuItem[]> = {
  student: [
    { title: '学习首页', path: '/student/dashboard', desc: '诊断、工作台与关键指标' },
    { title: '我的课程', path: '/student/courses', desc: '查看学期课程与班级课程' },
    { title: '我的课表', path: '/student/schedule', desc: '按周查看课程安排' },
    { title: '资源中心', path: '/student/resources', desc: '筛选资源并记录学习行为' },
    { title: '个性推荐', path: '/student/recommendations', desc: '主动反馈推荐结果' },
    { title: '智能问答', path: '/student/qa', desc: '基于课程与学习问题问答' },
    { title: '任务广场', path: '/student/plaza', desc: '浏览通用试卷、开放挑战与通用题目' },
    { title: '我的考试', path: '/student/exams', desc: '考试、记录与错题闭环' },
    { title: '我的错题本', path: '/student/wrongbook', desc: '错题回顾与复盘' },
  ],
  teacher: [
    { title: '教学概览', path: '/teacher/dashboard', desc: '课程、预警与教学建议' },
    { title: '我的课程', path: '/teacher/courses', desc: '查看本学期授课安排' },
    { title: '我的课表', path: '/teacher/schedule', desc: '按周查看教学安排' },
    { title: '教学资源', path: '/teacher/resources', desc: '资源查看与更新' },
    { title: '学情预警', path: '/teacher/warnings', desc: '跟进学生预警与干预' },
  ],
  parent: [
    { title: '孩子概览', path: '/parent/dashboard', desc: '孩子学情、建议与提醒' },
    { title: '孩子课程', path: '/parent/courses', desc: '查看孩子当前学期课程' },
    { title: '孩子课表', path: '/parent/schedule', desc: '按周查看孩子课程安排' },
    { title: '预警提醒', path: '/parent/warnings', desc: '查看近期风险与干预建议' },
    { title: '学习报告', path: '/parent/reports', desc: '查看阶段学习报告' },
  ],
}

const groupedMenus: Record<string, MenuGroup[]> = {
  student: [
    {
      key: 'student-general',
      label: '综合服务',
      items: [
        menus.student[0],
        menus.student[3],
        menus.student[5],
      ],
    },
    {
      key: 'student-course',
      label: '课程与选课',
      items: [
        menus.student[1],
        menus.student[2],
        menus.student[4],
      ],
    },
    {
      key: 'student-plaza',
      label: '任务中心',
      items: [
        menus.student[6],
      ],
    },
    {
      key: 'student-growth',
      label: '考试与成长',
      items: [
        menus.student[7],
        menus.student[8],
      ],
    },
  ],
  teacher: [
    {
      key: 'teacher-general',
      label: '综合服务',
      items: [
        menus.teacher[0],
        menus.teacher[3],
      ],
    },
    {
      key: 'teacher-course',
      label: '课程与课表',
      items: [
        menus.teacher[1],
        menus.teacher[2],
      ],
    },
    {
      key: 'teacher-analysis',
      label: '学情与预警',
      items: [
        menus.teacher[4],
      ],
    },
  ],
  parent: [
    {
      key: 'parent-general',
      label: '综合服务',
      items: [
        menus.parent[0],
      ],
    },
    {
      key: 'parent-course',
      label: '课程与课表',
      items: [
        menus.parent[1],
        menus.parent[2],
      ],
    },
    {
      key: 'parent-growth',
      label: '成绩与成长',
      items: [
        menus.parent[3],
        menus.parent[4],
      ],
    },
  ],
}

const roleKeys = Object.keys(menus)

const activeRole = ref('student')
watch(
  () => route.path,
  (path) => {
    const firstSegment = path.split('/')[1]
    activeRole.value = roleKeys.includes(firstSegment) ? firstSegment : userStore.preferredPortalRole
  },
  { immediate: true }
)

const menuItems = computed(() => menus[activeRole.value] || menus.student)
const menuGroups = computed(() => groupedMenus[activeRole.value] || groupedMenus.student)
const isWorkspaceRoute = computed(() => Boolean(route.meta.workspace) || route.path === '/student/qa')
const showOngoingExamBanner = computed(() => {
  const ongoingExam = userStore.ongoingExam
  if (activeRole.value !== 'student' || !ongoingExam) return false
  if (route.path.startsWith('/student/exams/session/')) return false
  return !userStore.isOngoingExamDismissed(ongoingExam.recordId)
})
const activeMenuPath = computed(() => {
  if (route.path.startsWith('/student/exams/session/')) {
    return '/student/exams'
  }
  return route.path
})

function resolveChildTransitionName(childRoute: RouteLocationNormalizedLoaded, fallback: string) {
  return typeof childRoute.meta.transition === 'string' ? childRoute.meta.transition : fallback
}

function handleRoleChange(role: string) {
  userStore.setPreferredPortalRole(role)
  const first = menus[role]?.[0]
  if (first) {
    router.push(first.path)
  }
}

function formatDateTime(value?: string) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString()
}

function continueOngoingExam() {
  const ongoingExam = userStore.ongoingExam
  if (!ongoingExam?.recordId) return
  router.push({
    path: `/student/exams/session/${ongoingExam.recordId}`,
    query: {
      paperId: String(ongoingExam.paperId || ''),
      startedAt: String(ongoingExam.startTime || ''),
    },
  })
}

function dismissOngoingBanner() {
  userStore.dismissOngoingExam(userStore.ongoingExam?.recordId)
}

async function handleLogout() {
  await ElMessageBox.confirm('确认退出当前学习门户吗？', '提示', { type: 'warning' })
  await userStore.logoutAction()
  router.replace('/login')
}
</script>

<style scoped>
.portal-shell {
  min-height: 100vh;
  background:
    radial-gradient(circle at 18% 12%, rgba(47, 107, 255, 0.08) 0%, rgba(47, 107, 255, 0) 26%),
    radial-gradient(circle at 82% 16%, rgba(124, 208, 255, 0.12) 0%, rgba(124, 208, 255, 0) 22%),
    linear-gradient(180deg, #f6f9fd 0%, #f3f6fb 54%, #f7f9fc 100%);
}

.portal-main {
  max-width: 1680px;
  margin: 0 auto;
  padding: 10px 18px 18px;
}

.portal-ongoing-banner {
  margin: 0 0 12px;
}

.portal-ongoing-banner__content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.portal-stage {
  background: transparent;
  border: none;
  box-shadow: none;
  backdrop-filter: none;
  overflow: visible;
}

.portal-main--workspace {
  max-width: none;
  width: 100%;
  height: calc(100vh - 64px);
  overflow: hidden;
  padding: 10px 18px 18px;
}

@media (max-width: 1200px) {
  .portal-main {
    padding: 10px 12px 14px;
  }
}

@media (max-width: 900px) {
  .portal-main--workspace {
    height: calc(100vh - 60px);
    padding: 8px 10px 10px;
  }
}
</style>
