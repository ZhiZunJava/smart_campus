<template>
  <el-container class="portal-shell">
    <el-aside width="248px" class="portal-aside">
      <div class="portal-logo">智慧校园学习门户</div>
      <div class="portal-subtitle">AI 赋能自主学习生态</div>
      <el-menu router :default-active="route.path" class="portal-menu">
        <template v-for="item in menuItems" :key="item.path">
          <el-menu-item :index="item.path">{{ item.title }}</el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="portal-header">
        <div class="portal-header__left">
          <div class="title">{{ currentRoleTitle }}</div>
          <div class="subtitle">围绕资源、行为、画像、问答与考试构建学习闭环</div>
        </div>
        <div class="portal-header__right">
          <div class="user-info">{{ userStore.user?.nickName || userStore.user?.userName || '学习用户' }}</div>
          <el-segmented v-model="activeRole" :options="roleOptions" @change="handleRoleChange" />
          <el-button text type="danger" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="portal-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import usePortalUserStore from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = usePortalUserStore()

const roleOptions = computed(() => {
  const roleMap: Record<string, string> = { student: '学生端', teacher: '教师端', parent: '家长端' }
  return userStore.availablePortalRoles.map((role) => ({ label: roleMap[role] || role, value: role }))
})

const menus: Record<string, Array<{ title: string; path: string }>> = {
  student: [
    { title: '学习首页', path: '/student/dashboard' },
    { title: '资源中心', path: '/student/resources' },
    { title: '个性推荐', path: '/student/recommendations' },
    { title: '智能问答', path: '/student/qa' },
    { title: '我的考试', path: '/student/exams' },
    { title: '我的错题本', path: '/student/wrongbook' },
  ],
  teacher: [
    { title: '教学概览', path: '/teacher/dashboard' },
    { title: '课程管理', path: '/teacher/courses' },
    { title: '教学资源', path: '/teacher/resources' },
    { title: '学情预警', path: '/teacher/warnings' },
  ],
  parent: [
    { title: '孩子概览', path: '/parent/dashboard' },
    { title: '预警提醒', path: '/parent/warnings' },
    { title: '学习报告', path: '/parent/reports' },
  ],
}

const activeRole = ref(route.path.split('/')[1] || 'student')
watch(() => route.path, (path) => {
  activeRole.value = path.split('/')[1] || userStore.preferredPortalRole
}, { immediate: true })

const menuItems = computed(() => menus[activeRole.value] || menus.student)
const currentRoleTitle = computed(() => roleOptions.value.find((item) => item.value === activeRole.value)?.label || '学习门户')

function handleRoleChange(value: string | number) {
  const role = String(value)
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
.portal-shell { height: 100vh; }
.portal-aside { background: linear-gradient(180deg, #11284c 0%, #0d1d36 100%); color: #fff; padding: 0 12px; }
.portal-logo { height: 64px; display: flex; align-items: center; justify-content: center; font-size: 22px; font-weight: 700; border-bottom: 1px solid rgba(255,255,255,.08); }
.portal-subtitle { padding: 14px 12px 6px; font-size: 12px; color: rgba(255,255,255,.62); }
.portal-menu { border-right: none; background: transparent; }
.portal-menu :deep(.el-menu-item) { color: rgba(255,255,255,.85); border-radius: 12px; margin-bottom: 8px; }
.portal-menu :deep(.el-menu-item:hover) { background: rgba(255,255,255,.06); }
.portal-menu :deep(.el-menu-item.is-active) { background: linear-gradient(90deg, #006eff 0%, #2a86ff 100%); color: #fff; }
.portal-header { display: flex; align-items: center; justify-content: space-between; background: rgba(255,255,255,.86); border-bottom: 1px solid #e5edf7; backdrop-filter: blur(10px); }
.portal-header__left .title { font-size: 22px; font-weight: 700; color: #1f2d3d; }
.portal-header__left .subtitle { margin-top: 4px; font-size: 13px; color: #6b7280; }
.portal-header__right{display:flex;align-items:center;gap:12px}
.user-info{color:#4b5563;font-weight:600}
.portal-main { background: linear-gradient(180deg, #eef3f9 0%, #f7f9fc 100%); }
</style>
