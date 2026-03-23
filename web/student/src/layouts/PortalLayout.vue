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
        'portal-main--dashboard': isDashboardRoute
      }"
      :style="{ '--table-border-color': '#ebeef5', '--brand-primary': '#266fcb' }"
    >
      <div v-if="!isWorkspaceRoute" class="portal-tabs-wrapper">
        <el-tabs
          v-model="activeTab"
          class="portal-tabs toolbar-tabs"
          @tab-remove="handleTabRemove"
          @tab-click="handleTabClick"
        >
          <el-tab-pane
            v-for="tab in tabsStore.visitedTabs"
            :key="tab.path"
            :label="tab.title"
            :name="tab.path"
            :closable="tab.closable"
          />
        </el-tabs>
      </div>

      <div v-if="!isDashboardRoute && !isWorkspaceRoute" class="portal-breadcrumb-wrapper breadcrumb-wrapper">
        <span class="breadcrumb-label">您的当前位置：</span>
        <el-breadcrumb :separator-icon="ArrowRight">
          <el-breadcrumb-item>
            <button type="button" class="breadcrumb-link" @click="goToRoleHome">
              {{ roleLabel }}全部服务
            </button>
          </el-breadcrumb-item>
          <el-breadcrumb-item v-if="currentMenuGroup">
            <el-dropdown
              trigger="click"
              placement="bottom-start"
              popper-class="portal-breadcrumb-dropdown"
              @command="handleBreadcrumbGroupCommand"
            >
              <button type="button" class="breadcrumb-link breadcrumb-link--dropdown">
                <span>{{ currentMenuGroup }}</span>
              </button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-for="item in currentMenuGroupItems"
                    :key="item.path"
                    :command="item.path"
                    :disabled="item.path === route.path"
                  >
                    {{ item.title }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-breadcrumb-item>
          <el-breadcrumb-item>
            <span class="breadcrumb-current">{{ currentMenuTitle }}</span>
          </el-breadcrumb-item>
        </el-breadcrumb>
        <el-button size="small" type="primary" class="refresh-btn" @click="handleRefresh">刷新</el-button>
        <div style="flex: 1"></div>
      </div>

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

      <div class="portal-fixed-wrapper fixed-wrapper" :class="{ 'is-dashboard': isDashboardRoute, 'is-workspace': isWorkspaceRoute }">
        <el-scrollbar style="width: 100%; height: 100%;" view-style="display: flex; flex-direction: column; min-height: 100%;">
          <div class="portal-stage" :class="{ 'portal-stage--white': !isDashboardRoute && !isWorkspaceRoute }">
            <router-view v-slot="{ Component, route: childRoute }">
              <transition :name="resolveChildTransitionName(childRoute, 'portal-content-shift')" mode="out-in">
                <div :key="childRoute.path" class="portal-route-transition-shell">
                  <keep-alive :include="tabsStore.cachedViews">
                    <component :is="Component" v-if="!isRefreshing" />
                  </keep-alive>
                </div>
              </transition>
            </router-view>
          </div>
        </el-scrollbar>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { RouteLocationNormalizedLoaded } from 'vue-router'
import type { TabPaneName, TabsPaneContext } from 'element-plus'
import { ElMessageBox } from '@/utils/feedback'
import PortalTopBar from '@/components/PortalTopBar.vue'
import usePortalUserStore from '@/store/user'
import { useTabsStore } from '@/store/tabs'
import { ArrowRight } from '@element-plus/icons-vue'

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
const tabsStore = useTabsStore()

const activeTab = computed({
  get: () => tabsStore.activeTabPath,
  set: (val) => { tabsStore.setActiveTab(val) }
})

const isRefreshing = ref(false)

function handleRefresh() {
  isRefreshing.value = true
  nextTick(() => {
    isRefreshing.value = false
  })
}

function handleTabClick(pane: TabsPaneContext) {
  const path = pane.props.name
  if (typeof path === 'string' && path !== route.path) {
    router.push(path)
  }
}

function handleTabRemove(targetName: TabPaneName) {
  if (typeof targetName !== 'string') {
    return
  }
  tabsStore.removeTab(targetName)
  if (activeTab.value !== route.path) {
    if (activeTab.value) {
      router.push(activeTab.value)
    } else {
      router.push(`/${activeRole.value}/dashboard`)
    }
  }
}

watch(
  () => route.path,
  (path) => {
    if (!path.startsWith('/student/exams/session/')) {
      tabsStore.addTab({
        title: route.meta.title as string || '页面',
        path: route.path,
        name: route.name as string
      })
    }
  },
  { immediate: true }
)

const menus: Record<string, MenuItem[]> = {
  student: [
    { title: '学习首页', path: '/student/dashboard', desc: '诊断、工作台与关键指标' },
    { title: '我的课程', path: '/student/courses', desc: '查看学期课程与班级课程' },
    { title: '我的课表', path: '/student/schedule', desc: '按周查看课程安排' },
    { title: '资源中心', path: '/student/resources', desc: '查询学习资料与课程资源' },
    { title: '智能问答', path: '/student/qa', desc: '围绕课程内容进行智能问答' },
    { title: '任务广场', path: '/student/plaza', desc: '浏览通用试卷、开放挑战与通用题目' },
    { title: '我的考试', path: '/student/exams', desc: '查看考试安排、记录与成绩反馈' },
    { title: '我的错题本', path: '/student/wrongbook', desc: '错题回顾、练习与复盘' },
  ],
  teacher: [
    { title: '教学概览', path: '/teacher/dashboard', desc: '课程与教学资源概览' },
    { title: '我的课程', path: '/teacher/courses', desc: '查看本学期授课安排' },
    { title: '我的课表', path: '/teacher/schedule', desc: '按周查看教学安排' },
    { title: '教学资源', path: '/teacher/resources', desc: '资源查看与更新' },
  ],
  parent: [
    { title: '孩子概览', path: '/parent/dashboard', desc: '查看孩子课程与考试概况' },
    { title: '孩子课程', path: '/parent/courses', desc: '查看孩子当前学期课程' },
    { title: '孩子课表', path: '/parent/schedule', desc: '按周查看孩子课程安排' },
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
        menus.student[4],
      ],
    },
    {
      key: 'student-course',
      label: '课程与学习',
      items: [
        menus.student[1],
        menus.student[2],
      ],
    },
    {
      key: 'student-plaza',
      label: '任务与练习',
      items: [
        menus.student[5],
      ],
    },
    {
      key: 'student-growth',
      label: '考试与成长',
      items: [
        menus.student[6],
        menus.student[7],
      ],
    },
  ],
  teacher: [
    {
      key: 'teacher-general',
      label: '综合服务',
      items: [
        menus.teacher[0],
        menus.teacher[1],
      ],
    },
    {
      key: 'teacher-course',
      label: '课程与课表',
      items: [
        menus.teacher[2],
        menus.teacher[3],
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
const isWorkspaceRoute = computed(() => Boolean(route.meta.workspace))
const isDashboardRoute = computed(() => route.path === `/${activeRole.value}/dashboard` || route.path === '/')

const roleLabel = computed(() => {
  const map: Record<string, string> = { student: '学生', teacher: '教师', parent: '家长', admin: '管理员' }
  return map[activeRole.value] || '系统'
})

const currentMenuTitle = computed(() => {
  const item = menuItems.value.find(m => m.path === route.path)
  return item ? item.title : route.meta.title || '详情'
})

const currentMenuGroup = computed(() => {
  for (const group of menuGroups.value) {
    if (group.items.some(m => m.path === route.path)) {
      return group.label
    }
  }
  return ''
})
const currentMenuGroupItems = computed(() => {
  const group = menuGroups.value.find(item => item.items.some(menu => menu.path === route.path))
  return group?.items || []
})

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

function goToRoleHome() {
  const first = menus[activeRole.value]?.[0]
  if (first) {
    router.push(first.path)
  }
}

function handleBreadcrumbGroupCommand(path: string) {
  if (path && path !== route.path) {
    router.push(path)
  }
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
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-image: url('@/assets/img/login-background.jpg');
  background-size: cover;
  background-position: center;
}

.portal-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  background-color: transparent;
  --theme-bg: url('@/assets/img/login-background.jpg');
}

.portal-main--dashboard {
  position: relative;
}

.portal-tabs-wrapper {
  padding: 0 20px;
  z-index: 10;
  height: 4.5rem;
  flex: none;
  display: block;
  width: 100%;
  margin-top: 5px;
}
.toolbar-tabs {
  --el-font-size-base: 1.3rem;
  --el-tabs-header-height: 4.5rem;
  --el-text-color-primary: rgba(255, 255, 255, 0.7);
  --el-color-primary: #ffffff;
}
:deep(.toolbar-tabs .el-tabs__header) {
  margin-bottom: 0;
  border-bottom: none;
}
:deep(.toolbar-tabs .el-tabs__nav-wrap::after) {
  display: none;
}
:deep(.toolbar-tabs .el-tabs__nav) {
  border: none !important;
}
:deep(.toolbar-tabs .el-tabs__item) {
  color: var(--el-text-color-primary) !important;
  font-size: var(--el-font-size-base);
  height: 32px;
  line-height: 32px;
  padding: 0 16px !important;
  margin: calc((var(--el-tabs-header-height) - 32px) / 2) 4px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: none !important;
  border-radius: 16px; /* 胶囊圆角 */
}
:deep(.toolbar-tabs .el-tabs__item:hover) {
  color: var(--el-color-primary) !important;
  background-color: rgba(255, 255, 255, 0.15);
}
:deep(.toolbar-tabs .el-tabs__item.is-active) {
  color: var(--el-color-primary) !important;
  font-weight: 600;
  background-color: rgba(255, 255, 255, 0.25);
}
:deep(.toolbar-tabs .el-tabs__active-bar) {
  display: none !important; /* 隐藏底部的选中横线 */
}
:deep(.toolbar-tabs .el-tabs__item .is-icon-close) {
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.2s;
  width: 14px;
  height: 14px;
  margin-left: 6px;
  border-radius: 50%;
}
:deep(.toolbar-tabs .el-tabs__item:hover .is-icon-close) {
  color: rgba(255, 255, 255, 0.9);
}
:deep(.toolbar-tabs .el-tabs__item .is-icon-close:hover) {
  background-color: rgba(255, 255, 255, 0.3);
  color: #fff;
}

.portal-breadcrumb-wrapper {
  --el-font-size-base: 1.3rem;
  margin: 0 20px;
  padding-left: 2em;
  padding-right: 2em;
  height: 4rem;
  background-color: #fff;
  border-radius: 8px 8px 0 0;
  border-bottom: 1px dotted var(--table-border-color, #e6e7e8);
  display: flex;
  align-items: center;
  z-index: 10;
  position: absolute;
  top: calc(4.5rem + 10px);
  left: 0;
  right: 0;
  flex: none;
}
.breadcrumb-label {
  color: #606266;
  margin-right: 12px;
  font-size: 1.3rem;
}
.breadcrumb-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0;
  border: none;
  background: transparent;
  color: #606266;
  font-size: 1.3rem;
  cursor: pointer;
  transition: color 0.2s ease;
}
.breadcrumb-link:hover {
  color: #2563eb;
}
.breadcrumb-link--dropdown::after {
  content: '';
  width: 0;
  height: 0;
  margin-top: 2px;
  margin-left: 2px;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 5px solid currentColor;
}
.refresh-btn {
  margin-left: 16px;
  background-color: #3b82f6;
  border-color: #3b82f6;
  border-radius: 4px;
  padding: 8px 16px;
  font-weight: 500;
}
.refresh-btn:hover {
  background-color: #2563eb;
  border-color: #2563eb;
}

:deep(.el-breadcrumb__inner) {
  color: #303133 !important;
  font-weight: 400 !important;
}

:deep(.el-breadcrumb__separator) {
  color: #c0c4cc;
  margin: 0 8px;
}

.portal-fixed-wrapper {
  position: absolute;
  top: calc(7.5rem + 10px); /* 4.5rem tabs + 3rem breadcrumbs + margin */
  right: 20px;
  bottom: 20px;
  left: 20px;
  display: flex;
  flex-flow: column nowrap;
  gap: 0;
  overflow: hidden;
  border-radius: 0 0 6px 6px;
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}

:deep(.portal-fixed-wrapper > .el-scrollbar > .el-scrollbar__wrap > .el-scrollbar__view) {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.portal-fixed-wrapper > .el-scrollbar > .el-scrollbar__wrap) {
  display: flex;
  flex-direction: column;
}

.portal-fixed-wrapper.is-dashboard,
.portal-fixed-wrapper.is-workspace {
  top: 58px;
  right: 0;
  left: 0;
  bottom: 0;
  border-radius: 0;
  background: transparent;
  box-shadow: none;
}

.portal-fixed-wrapper.is-dashboard {
  top: calc(4.5rem + 58px);
}

.portal-stage {
  flex: 1;
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
}

.portal-stage--white {
  background: #fff;
  margin: 0;
  flex: 1;
  padding: 20px;
  border-radius: 0 0 6px 6px;
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.portal-route-transition-shell {
  display: flex;
  flex: 1;
  min-height: 100%;
  flex-direction: column;
}

.portal-ongoing-banner {
  margin: 12px 16px 0;
}

.portal-ongoing-banner__content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.breadcrumb-current {
  color: #303133;
  font-weight: 400;
  font-size: 1.3rem;
}

:global(.portal-breadcrumb-dropdown.el-popper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

:global(.portal-breadcrumb-dropdown .el-popper__arrow) {
  display: none;
}

:global(.portal-breadcrumb-dropdown .el-dropdown-menu) {
  padding: 8px;
}

:global(.portal-breadcrumb-dropdown .el-dropdown-menu__item) {
  min-width: 120px;
  min-height: 36px;
  border-radius: 6px;
  color: #475569;
  font-size: 13px;
  margin-bottom: 2px;
}

:global(.portal-breadcrumb-dropdown .el-dropdown-menu__item:last-child) {
  margin-bottom: 0;
}

:global(.portal-breadcrumb-dropdown .el-dropdown-menu__item:not(.is-disabled):hover) {
  background: #f8fafc;
  color: #2563eb;
}

:global(.el-overlay) {
  z-index: 5000 !important;
}

:global(.el-overlay-dialog) {
  z-index: 5001 !important;
}

:global(.el-dialog) {
  z-index: 5002 !important;
}

:global(.el-drawer) {
  z-index: 5002 !important;
}
</style>
