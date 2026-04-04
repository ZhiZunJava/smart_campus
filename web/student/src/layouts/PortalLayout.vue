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
        <div class="portal-tab-bar">
          <div
            v-for="tab in tabsStore.visitedTabs"
            :key="tab.path"
            class="portal-tab-item"
            :class="{ 'is-active': activeTab === tab.path }"
            @click="handleTabNav(tab.path)"
            @contextmenu.prevent="openTabContextMenu($event, tab)"
          >
            <span class="portal-tab-item__label">{{ tab.title }}</span>
            <button v-if="tab.closable" class="portal-tab-item__close" @click.stop="handleTabRemove(tab.path)">
              <i class="ri-close-line" />
            </button>
          </div>
        </div>

        <!-- Tab Context Menu -->
        <teleport to="body">
          <transition name="ctx-fade">
            <div
              v-if="ctxMenu.visible"
              class="tab-context-menu"
              :style="{ left: ctxMenu.x + 'px', top: ctxMenu.y + 'px' }"
              @contextmenu.prevent
            >
              <div class="ctx-item" @click="ctxRefreshTab"><i class="ri-refresh-line" /> 刷新当前页</div>
              <div class="ctx-divider" />
              <div class="ctx-item" @click="ctxCloseCurrent"><i class="ri-close-line" /> 关闭当前</div>
              <div class="ctx-item" :class="{ 'is-disabled': !hasOtherTabs }" @click="ctxCloseOthers"><i class="ri-subtract-line" /> 关闭其他</div>
              <div class="ctx-item" :class="{ 'is-disabled': !hasRightTabs }" @click="ctxCloseRight"><i class="ri-skip-right-line" /> 关闭右侧</div>
              <div class="ctx-item" :class="{ 'is-disabled': !hasLeftTabs }" @click="ctxCloseLeft"><i class="ri-skip-left-line" /> 关闭左侧</div>
              <div class="ctx-divider" />
              <div class="ctx-item ctx-item--danger" @click="ctxCloseAll"><i class="ri-delete-bin-line" /> 关闭全部</div>
            </div>
          </transition>
        </teleport>
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
              trigger="hover"
              placement="bottom-start"
              :show-timeout="120"
              :hide-timeout="200"
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
          <div class="portal-stage" :class="{ 'portal-stage--white': !isDashboardRoute && !isWorkspaceRoute, 'portal-stage--compact': isQaRoute }">
            <router-view v-slot="{ Component, route: childRoute }">
              <transition :name="resolveChildTransitionName(childRoute, 'portal-content-shift')" mode="out-in">
                <div :key="childRoute.path" class="portal-route-transition-shell">
                  <keep-alive :include="tabsStore.cachedViews">
                    <component :is="Component" :key="resolveRouteViewKey(childRoute)" v-if="!isRefreshing" />
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
import { computed, ref, reactive, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { RouteLocationNormalizedLoaded } from 'vue-router'
import { ElMessageBox } from '@/utils/feedback'
import type { TabItem } from '@/store/tabs'
import PortalTopBar from '@/components/PortalTopBar.vue'
import usePortalUserStore from '@/store/user'
import { useTabsStore } from '@/store/tabs'
import { ArrowRight } from '@element-plus/icons-vue'
import { buildStudentAffairPathByCode, studentAffairPageConfigs } from '@/utils/affairCatalog'

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

const studentAffairOverviewMenu: MenuItem = {
  title: '事务总览',
  path: '/student/affairs',
  desc: '查看事务总览、最近申请和常用服务'
}

const studentAffairMenuItems: MenuItem[] = studentAffairPageConfigs.map((item) => ({
  title: item.title,
  path: buildStudentAffairPathByCode(item.categoryCode),
  desc: item.desc,
}))

const studentAffairGroupedMenus: MenuGroup[] = ['attendance', 'funding', 'growth', 'academic'].map((groupKey) => ({
  key: `student-affair-${groupKey}`,
  label: studentAffairPageConfigs.find((item) => item.groupKey === groupKey)?.groupLabel || groupKey,
  items: studentAffairPageConfigs
    .filter((item) => item.groupKey === groupKey)
    .map((item) => ({
      title: item.title,
      path: buildStudentAffairPathByCode(item.categoryCode),
      desc: item.desc,
    })),
}))

const route = useRoute()
const router = useRouter()
const userStore = usePortalUserStore()
const tabsStore = useTabsStore()

const activeTab = computed({
  get: () => tabsStore.activeTabPath,
  set: (val) => { tabsStore.setActiveTab(val) }
})

const isRefreshing = ref(false)
const refreshKeyMap = ref<Record<string, number>>({})

function resolveRouteRefreshId(targetRoute: RouteLocationNormalizedLoaded) {
  return String(targetRoute.name || targetRoute.path)
}

function resolveRouteViewKey(targetRoute: RouteLocationNormalizedLoaded) {
  const refreshId = resolveRouteRefreshId(targetRoute)
  return `${refreshId}:${refreshKeyMap.value[refreshId] || 0}`
}

async function handleRefresh() {
  const currentViewName = typeof route.name === 'string' ? route.name : ''
  const refreshId = resolveRouteRefreshId(route)
  if (currentViewName) {
    tabsStore.excludeCacheView(currentViewName)
    await nextTick()
  }
  isRefreshing.value = true
  await nextTick()
  refreshKeyMap.value = {
    ...refreshKeyMap.value,
    [refreshId]: (refreshKeyMap.value[refreshId] || 0) + 1,
  }
  isRefreshing.value = false
  await nextTick()
  if (currentViewName) {
    tabsStore.restoreCacheView(currentViewName)
  }
}

function handleTabNav(path: string) {
  if (path !== route.path) router.push(path)
}

function handleTabRemove(targetPath: string) {
  tabsStore.removeTab(targetPath)
  if (activeTab.value !== route.path) {
    router.push(activeTab.value || `/${activeRole.value}/dashboard`)
  }
}

// ---- Tab Context Menu ----
const ctxMenu = reactive({ visible: false, x: 0, y: 0, tab: null as TabItem | null })

function openTabContextMenu(e: MouseEvent, tab: TabItem) {
  ctxMenu.x = e.clientX
  ctxMenu.y = e.clientY
  ctxMenu.tab = tab
  ctxMenu.visible = true
}

function closeCtxMenu() { ctxMenu.visible = false }

const ctxTabIndex = computed(() => {
  if (!ctxMenu.tab) return -1
  return tabsStore.visitedTabs.findIndex(t => t.path === ctxMenu.tab!.path)
})
const hasOtherTabs = computed(() => tabsStore.visitedTabs.filter(t => t.closable).length > 1)
const hasRightTabs = computed(() => {
  const idx = ctxTabIndex.value
  return idx >= 0 && tabsStore.visitedTabs.slice(idx + 1).some(t => t.closable)
})
const hasLeftTabs = computed(() => {
  const idx = ctxTabIndex.value
  return idx > 0 && tabsStore.visitedTabs.slice(0, idx).some(t => t.closable)
})

function navigateAfterCtx() {
  if (!tabsStore.visitedTabs.find(t => t.path === route.path)) {
    router.push(activeTab.value || `/${activeRole.value}/dashboard`)
  }
}

function ctxRefreshTab() {
  closeCtxMenu()
  if (ctxMenu.tab) {
    if (ctxMenu.tab.path !== route.path) router.push(ctxMenu.tab.path)
    nextTick(() => handleRefresh())
  }
}
function ctxCloseCurrent() {
  closeCtxMenu()
  if (ctxMenu.tab?.closable) { tabsStore.removeTab(ctxMenu.tab.path); navigateAfterCtx() }
}
function ctxCloseOthers() {
  closeCtxMenu()
  if (ctxMenu.tab) { tabsStore.closeOthers(ctxMenu.tab.path); navigateAfterCtx() }
}
function ctxCloseRight() {
  closeCtxMenu()
  if (ctxMenu.tab) { tabsStore.closeRight(ctxMenu.tab.path); navigateAfterCtx() }
}
function ctxCloseLeft() {
  closeCtxMenu()
  if (ctxMenu.tab) { tabsStore.closeLeft(ctxMenu.tab.path); navigateAfterCtx() }
}
function ctxCloseAll() {
  closeCtxMenu()
  tabsStore.closeAll()
  router.push(`/${activeRole.value}/dashboard`)
}

function onDocClick() { ctxMenu.visible = false }
onMounted(() => document.addEventListener('click', onDocClick))
onUnmounted(() => document.removeEventListener('click', onDocClick))

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
    studentAffairOverviewMenu,
    { title: '学籍信息', path: '/student/academic-profile', desc: '查看个人学籍档案与当前学籍状态' },
    ...studentAffairMenuItems,
    { title: '我的课程', path: '/student/courses', desc: '查看当前已选教学班课程' },
    { title: '我的班级课程', path: '/student/class-courses', desc: '查看班级默认开设课程' },
    { title: '选课中心', path: '/student/selection', desc: '办理标准选课与退课' },
    { title: '选课申请', path: '/student/personalized-selection', desc: '提交特殊场景下的个性化选课申请' },
    { title: '我的课表', path: '/student/schedule', desc: '按周查看已选课程安排' },
    { title: '我的班级课表', path: '/student/class-schedule', desc: '按周查看班级默认课表' },
    { title: '资源中心', path: '/student/resources', desc: '查询学习资料与课程资源' },
    { title: '我的收藏', path: '/student/favorites', desc: '查看已收藏的课时和资源' },
    { title: '智能问答', path: '/student/qa', desc: '围绕课程内容进行智能问答' },
    { title: '任务广场', path: '/student/plaza', desc: '浏览通用试卷、开放挑战与通用题目' },
    { title: '我的考试', path: '/student/exams', desc: '查看考试安排、记录与成绩反馈' },
    { title: '我的成绩', path: '/student/scores', desc: '查看课程总评、构成与排名' },
    { title: '我的错题本', path: '/student/wrongbook', desc: '错题回顾、练习与复盘' },
    { title: '全校开课查询', path: '/student/course-offerings', desc: '查询全校各学期课程开设情况' },
    { title: '学籍核对', path: '/student/verification', desc: '核对并确认个人学籍信息' },
    { title: '家长绑定请求', path: '/student/parent-requests', desc: '查看并处理家长发来的绑定请求' },
  ],
  teacher: [
    { title: '教学概览', path: '/teacher/dashboard', desc: '课程与教学资源概览' },
    { title: '事务协同', path: '/teacher/affairs', desc: '处理教师发起事项与指导审核事务' },
    { title: '我的课程', path: '/teacher/courses', desc: '查看本学期授课安排' },
    { title: '我的课表', path: '/teacher/schedule', desc: '按周查看教学安排' },
    { title: '教学资源', path: '/teacher/resources', desc: '资源查看与更新' },
    { title: '消息中心', path: '/teacher/messages', desc: '查看系统消息与通知公告' },
    { title: '全校开课查询', path: '/teacher/course-offerings', desc: '查询全校各学期开课与教学班信息' },
  ],
  advisor: [
    { title: '辅导员概览', path: '/advisor/dashboard', desc: '查看负责班级、学生与成绩概况' },
    { title: '事务审核', path: '/advisor/affairs', desc: '集中处理学生事务审核与学籍跟踪' },
    { title: '学生管理', path: '/advisor/students', desc: '查看负责班级的学生名册与基本信息' },
    { title: '成绩管理', path: '/advisor/scores', desc: '查看行政班成绩、发布状态与分布概况' },
    { title: '消息中心', path: '/advisor/messages', desc: '查看系统消息与通知公告' },
  ],
  parent: [
    { title: '孩子概览', path: '/parent/dashboard', desc: '查看孩子课程与考试概况' },
    { title: '孩子课程', path: '/parent/courses', desc: '查看孩子当前学期课程' },
    { title: '孩子课表', path: '/parent/schedule', desc: '按周查看孩子课程安排' },
    { title: '消息中心', path: '/parent/messages', desc: '查看系统消息与通知公告' },
    { title: '孩子绑定', path: '/parent/child-bind', desc: '管理与孩子账号的绑定关系' },
  ],
}

const groupedMenus: Record<string, MenuGroup[]> = {
  student: [
    {
      key: 'student-general',
      label: '综合服务',
      items: [
        menus.student[0],
        menus.student[1],
        menus.student[2],
        menus.student[30],
        menus.student[31],
        menus.student[22],
        menus.student[23],
      ],
    },
    ...studentAffairGroupedMenus,
    {
      key: 'student-course',
      label: '课程与学习',
      items: [
        menus.student[16],
        menus.student[17],
        menus.student[20],
        menus.student[21],
        menus.student[24],
      ],
    },
    {
      key: 'student-selection',
      label: '选课服务',
      items: [
        menus.student[18],
        menus.student[19],
        menus.student[29],
      ],
    },
    {
      key: 'student-plaza',
      label: '任务与练习',
      items: [
        menus.student[25],
      ],
    },
    {
      key: 'student-growth',
      label: '考试与成长',
      items: [
        menus.student[26],
        menus.student[27],
        menus.student[28],
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
        menus.teacher[5],
        menus.teacher[6],
      ],
    },
    {
      key: 'teacher-course',
      label: '课程与课表',
      items: [
        menus.teacher[2],
        menus.teacher[3],
        menus.teacher[4],
      ],
    },
  ],
  advisor: [
    {
      key: 'advisor-general',
      label: '综合服务',
      items: [
        menus.advisor[0],
        menus.advisor[1],
        menus.advisor[4],
      ],
    },
    {
      key: 'advisor-student',
      label: '学生与成绩',
      items: [
        menus.advisor[2],
        menus.advisor[3],
      ],
    },
  ],
  parent: [
    {
      key: 'parent-general',
      label: '综合服务',
      items: [
        menus.parent[0],
        menus.parent[3],
        menus.parent[4],
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
const isQaRoute = computed(() => /\/qa$/.test(route.path))

const roleLabel = computed(() => {
  const map: Record<string, string> = { student: '学生', teacher: '教师', advisor: '辅导员', parent: '家长', admin: '管理员' }
  return map[activeRole.value] || '系统'
})

const resolvedMenuPath = computed(() => {
  const menuPath = typeof route.meta.menuPath === 'string' ? route.meta.menuPath : ''
  if (menuPath) {
    return menuPath
  }
  if (route.path.startsWith('/student/exams/session/')) {
    return '/student/exams'
  }
  return route.path
})

const currentMenuTitle = computed(() => {
  const item = menuItems.value.find(m => m.path === resolvedMenuPath.value)
  if (typeof route.meta.menuPath === 'string' && route.meta.title) {
    return route.meta.title as string
  }
  return item ? item.title : route.meta.title || '详情'
})

const currentMenuGroup = computed(() => {
  for (const group of menuGroups.value) {
    if (group.items.some(m => m.path === resolvedMenuPath.value)) {
      return group.label
    }
  }
  return ''
})
const currentMenuGroupItems = computed(() => {
  const group = menuGroups.value.find(item => item.items.some(menu => menu.path === resolvedMenuPath.value))
  return group?.items || []
})

const showOngoingExamBanner = computed(() => {
  const ongoingExam = userStore.ongoingExam
  if (activeRole.value !== 'student' || !ongoingExam) return false
  if (route.path.startsWith('/student/exams/session/')) return false
  return !userStore.isOngoingExamDismissed(ongoingExam.recordId)
})
const activeMenuPath = computed(() => {
  return resolvedMenuPath.value
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
  height: 45px;
  flex: none;
  display: block;
  width: 100%;
  margin-top: 5px;
}
/* ---- Custom Tab Bar ---- */
.portal-tab-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 100%;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding: 0 4px;
}
.portal-tab-bar::-webkit-scrollbar { display: none; }

.portal-tab-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: 34px;
  padding: 0 14px;
  border-radius: 8px;
  font-size: 13px;
  color: #475569;
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
  transition: background 0.2s, color 0.2s, border-color 0.2s, box-shadow 0.2s;
  user-select: none;
}
.portal-tab-item:hover {
  background: rgba(255, 255, 255, 0.85);
  color: #1e40af;
  border-color: rgba(59, 130, 246, 0.25);
}
.portal-tab-item.is-active {
  background: rgba(255, 255, 255, 0.92);
  color: #2563eb;
  font-weight: 600;
  border-color: rgba(37, 99, 235, 0.3);
  box-shadow: 0 1px 6px rgba(37, 99, 235, 0.12);
}
.portal-tab-item__label {
  line-height: 1;
}
.portal-tab-item__close {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border: none;
  background: transparent;
  border-radius: 50%;
  color: #94a3b8;
  font-size: 12px;
  cursor: pointer;
  padding: 0;
  margin-left: 2px;
  transition: background 0.15s, color 0.15s;
}
.portal-tab-item__close:hover {
  background: rgba(239, 68, 68, 0.12);
  color: #ef4444;
}
.portal-tab-item__close i { font-size: 12px; line-height: 1; }

/* ---- Tab Context Menu (teleported to body) ---- */
:global(.tab-context-menu) {
  position: fixed;
  z-index: 9999;
  min-width: 160px;
  padding: 6px;
  background: #fff;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}
:global(.ctx-item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  font-size: 13px;
  color: #334155;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
  user-select: none;
}
:global(.ctx-item i) { font-size: 15px; opacity: 0.7; }
:global(.ctx-item:hover) { background: #f1f5f9; color: #2563eb; }
:global(.ctx-item.is-disabled) { color: #cbd5e1; cursor: not-allowed; pointer-events: none; }
:global(.ctx-item--danger:hover) { background: #fef2f2; color: #dc2626; }
:global(.ctx-divider) { height: 1px; background: #f1f5f9; margin: 4px 8px; }

/* ctx-fade transition */
:global(.ctx-fade-enter-active) { transition: opacity 0.15s ease, transform 0.15s ease; }
:global(.ctx-fade-leave-active) { transition: opacity 0.1s ease, transform 0.1s ease; }
:global(.ctx-fade-enter-from) { opacity: 0; transform: scale(0.95) translateY(-4px); }
:global(.ctx-fade-leave-to) { opacity: 0; transform: scale(0.95) translateY(-4px); }

.portal-breadcrumb-wrapper {
  --el-font-size-base: 13px;
  margin: 0 20px;
  padding-left: 2em;
  padding-right: 2em;
  height: 40px;
  background-color: #fff;
  border-radius: 8px 8px 0 0;
  border-bottom: 1px dotted var(--table-border-color, #e6e7e8);
  display: flex;
  align-items: center;
  z-index: 10;
  position: absolute;
  top: calc(45px + 10px);
  left: 0;
  right: 0;
  flex: none;
}
.breadcrumb-label {
  color: #606266;
  margin-right: 12px;
  font-size: 13px;
}
.breadcrumb-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0;
  border: none;
  background: transparent;
  color: #606266;
  font-size: 13px;
  cursor: pointer;
  transition: color 0.2s ease;
  outline: none;
}
.breadcrumb-link:hover {
  color: #2563eb;
}
.breadcrumb-link--dropdown::after {
  content: '';
  width: 0;
  height: 0;
  margin-top: 2px;
  margin-left: 4px;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 5px solid currentColor;
  transition: transform 0.2s ease;
}
.breadcrumb-link--dropdown:hover::after {
  transform: rotate(180deg);
}
.breadcrumb-link--dropdown:hover {
  color: #2563eb;
  text-decoration: underline;
  text-decoration-style: dashed;
  text-underline-offset: 4px;
  text-decoration-thickness: 1px;
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
  top: calc(75px + 10px); /* 45px tabs + 30px breadcrumbs + margin */
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
  top: calc(45px + 58px);
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
  padding: 20px 20px 28px;
  border-radius: 0 0 6px 6px;
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.portal-stage--white.portal-stage--compact {
  padding-top: 8px;
}

.portal-route-transition-shell {
  display: flex;
  flex: 1 1 auto;
  min-height: 0;
  flex-direction: column;
}

:deep(.portal-stage--white .portal-page) {
  min-height: auto;
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
  font-size: 13px;
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
