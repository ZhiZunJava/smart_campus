import { createRouter, createWebHistory } from 'vue-router'
import PortalLayout from '@/layouts/PortalLayout.vue'
import usePortalUserStore from '@/store/user'

function resolveHome(role: string) {
  return `/${role}/dashboard`
}

const routes = [
  { path: '/', redirect: '/student/dashboard' },
  { path: '/login', component: () => import('@/views/auth/Login.vue'), meta: { public: true, title: '门户登录', transition: 'auth-screen' } },
  { path: '/register', component: () => import('@/views/auth/Register.vue'), meta: { public: true, title: '门户注册', transition: 'auth-screen' } },
  { path: '/scan-login/confirm', component: () => import('@/views/auth/ScanConfirm.vue'), meta: { public: true, allowAuthenticated: true, title: '扫码确认', transition: 'auth-screen' } },
  {
    path: '/account',
    component: PortalLayout,
    meta: { title: '账号中心' },
    children: [
      { path: 'profile', component: () => import('@/views/account/Profile.vue'), meta: { title: '我的档案' } },
      { path: 'settings', component: () => import('@/views/account/Settings.vue'), meta: { title: '账号设置' } },
    ]
  },
  {
    path: '/student',
    component: PortalLayout,
    meta: { title: '学生端', role: 'student' },
    children: [
      { path: 'dashboard', component: () => import('@/views/student/Dashboard.vue'), meta: { title: '学习首页' } },
      { path: 'courses', component: () => import('@/views/student/Courses.vue'), meta: { title: '我的课程' } },
      { path: 'schedule', component: () => import('@/views/student/Schedule.vue'), meta: { title: '我的课表' } },
      { path: 'resources', component: () => import('@/views/student/Resources.vue'), meta: { title: '资源中心' } },
      { path: 'resources/:resourceId', component: () => import('@/views/student/ResourceDetail.vue'), meta: { title: '资源详情', hideShortcut: true } },
      { path: 'favorites', component: () => import('@/views/student/Favorites.vue'), meta: { title: '我的收藏' } },
      { path: 'qa', component: () => import('@/views/student/Qa.vue'), meta: { title: '智能问答', hideBanner: true } },
      { path: 'plaza', component: () => import('@/views/student/ChallengePlaza.vue'), meta: { title: '任务中心' } },
      { path: 'messages', component: () => import('@/views/student/Messages.vue'), meta: { title: '消息中心', hideShortcut: true } },
      { path: 'notices', component: () => import('@/views/student/Notices.vue'), meta: { title: '消息中心', hideShortcut: true } },
      { path: 'tasks/:dispatchId', component: () => import('@/views/student/TaskDetail.vue'), meta: { title: '任务详情', hideShortcut: true } },
      { path: 'exams', component: () => import('@/views/student/Exams.vue'), meta: { title: '我的考试' } },
      { path: 'exams/preview/:paperId', component: () => import('@/views/student/ExamPreviewPage.vue'), meta: { title: '试卷预览' } },
      { path: 'exams/session/:recordId', component: () => import('@/views/student/ExamSession.vue'), meta: { title: '在线考试' } },
      { path: 'wrongbook', component: () => import('@/views/student/wrongbook/index.vue'), meta: { title: '我的错题本' } }
    ]
  },
  {
    path: '/teacher',
    component: PortalLayout,
    meta: { title: '教师端', role: 'teacher' },
    children: [
      { path: 'dashboard', component: () => import('@/views/teacher/Dashboard.vue'), meta: { title: '教学概览' } },
      { path: 'courses', component: () => import('@/views/teacher/Courses.vue'), meta: { title: '课程管理' } },
      { path: 'schedule', component: () => import('@/views/teacher/Schedule.vue'), meta: { title: '我的课表' } },
      { path: 'resources', component: () => import('@/views/teacher/Resources.vue'), meta: { title: '教学资源' } }
    ]
  },
  {
    path: '/parent',
    component: PortalLayout,
    meta: { title: '家长端', role: 'parent' },
    children: [
      { path: 'dashboard', component: () => import('@/views/parent/Dashboard.vue'), meta: { title: '孩子概览' } },
      { path: 'courses', component: () => import('@/views/parent/Courses.vue'), meta: { title: '孩子课程' } },
      { path: 'schedule', component: () => import('@/views/parent/Schedule.vue'), meta: { title: '孩子课表' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach(async (to, _from) => {
  const userStore = usePortalUserStore()
  const isPublic = to.meta.public

  if (to.path === '/') {
    if (!userStore.token) {
      return '/login'
    }
    if (!userStore.user) {
      try {
        await userStore.loadUserInfo()
      } catch (error) {
        await userStore.logoutAction()
        return '/login'
      }
    }
    return resolveHome(userStore.preferredPortalRole)
  }

  if (isPublic) {
    if (userStore.token && !to.meta.allowAuthenticated) {
      if (!userStore.user) {
        try {
          await userStore.loadUserInfo()
        } catch (error) {
          return true
        }
      }
      return resolveHome(userStore.preferredPortalRole)
    }
    return true
  }

  if (!userStore.token) {
    return '/login'
  }

  if (!userStore.user) {
    try {
      await userStore.loadUserInfo()
    } catch (error) {
      await userStore.logoutAction()
      return '/login'
    }
  }

  const routeRole = to.meta.role as string | undefined
  if (routeRole && !userStore.availablePortalRoles.includes(routeRole)) {
    return resolveHome(userStore.preferredPortalRole)
  }

  if (routeRole) {
    userStore.setPreferredPortalRole(routeRole)
  }

  return true
})

export default router
