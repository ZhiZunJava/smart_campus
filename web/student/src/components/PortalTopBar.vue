<template>
  <header class="portal-topbar">
    <div class="portal-topbar__inner">
      <div class="portal-topbar__brand" @click="goHome">
        <div class="portal-topbar__logo">智</div>
        <div class="portal-topbar__brand-text">
          <strong>智慧校园</strong>
          <span>Smart Campus Learning Portal</span>
        </div>
      </div>

      <nav v-if="showMenu && menuItems.length" class="portal-topbar__nav">
        <button
          v-for="item in menuItems"
          :key="item.path"
          type="button"
          class="portal-topbar__nav-item"
          :class="{ 'is-active': item.path === activePath }"
          @click="handleNav(item.path)"
        >
          {{ item.title }}
        </button>
      </nav>

      <div class="portal-topbar__actions">
        <el-select
          v-if="showRoleSwitch"
          :model-value="selectedRole"
          class="portal-topbar__role"
          placeholder="切换角色"
          @change="handleRoleChange"
        >
          <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>

        <template v-if="userStore.token && showUserPanel">
          <div class="portal-topbar__user">
            <strong>{{ userStore.user?.nickName || userStore.user?.userName || '学习用户' }}</strong>
            <span>{{ currentRoleLabel }}</span>
          </div>
          <el-button type="primary" plain @click="emit('logout')">退出</el-button>
        </template>

        <template v-else-if="showAuthAction">
          <el-button type="primary" @click="router.push('/login')">登录</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import usePortalUserStore from '@/store/user'

interface MenuItem {
  title: string
  path: string
}

const props = withDefaults(
  defineProps<{
    menuItems?: MenuItem[]
    activePath?: string
    selectedRole?: string
    showMenu?: boolean
    showRoleSwitch?: boolean
    showAuthAction?: boolean
    showUserPanel?: boolean
    roleOptionsOverride?: Array<{ label: string; value: string }>
  }>(),
  {
    menuItems: () => [],
    activePath: '',
    selectedRole: 'student',
    showMenu: true,
    showRoleSwitch: true,
    showAuthAction: true,
    showUserPanel: true,
    roleOptionsOverride: () => [],
  }
)

const emit = defineEmits<{
  (e: 'role-change', role: string): void
  (e: 'logout'): void
}>()

const router = useRouter()
const userStore = usePortalUserStore()

const roleOptions = computed(() => {
  if (props.roleOptionsOverride.length) {
    return props.roleOptionsOverride
  }
  const roleMap: Record<string, string> = {
    student: '学生端',
    teacher: '教师端',
    parent: '家长端',
    admin: '管理员',
  }
  return userStore.availablePortalRoles.map((role) => ({ label: roleMap[role] || role, value: role }))
})

const currentRoleLabel = computed(() => roleOptions.value.find((item) => item.value === props.selectedRole)?.label || '学习门户')

function goHome() {
  if (props.selectedRole && props.selectedRole !== 'admin') {
    router.push(`/${props.selectedRole}/dashboard`)
    return
  }
  router.push('/login')
}

function handleNav(path: string) {
  router.push(path)
}

function handleRoleChange(value: string) {
  emit('role-change', value)
}
</script>

<style scoped>
.portal-topbar{position:sticky;top:0;z-index:50;background:rgba(255,255,255,.96);backdrop-filter:blur(10px);border-bottom:1px solid #e9eef5}
.portal-topbar__inner{height:78px;display:flex;align-items:center;justify-content:space-between;gap:24px;padding:0 28px;max-width:1600px;margin:0 auto}
.portal-topbar__brand{display:flex;align-items:center;gap:12px;min-width:260px;cursor:pointer}.portal-topbar__logo{width:48px;height:48px;border-radius:14px;background:linear-gradient(135deg,#2c86ff 0%,#5db0ff 100%);color:#fff;display:flex;align-items:center;justify-content:center;font-size:22px;font-weight:800;box-shadow:0 10px 25px rgba(44,134,255,.24)}
.portal-topbar__brand-text{display:flex;flex-direction:column}.portal-topbar__brand-text strong{font-size:28px;line-height:1;color:#1a2d4d}.portal-topbar__brand-text span{margin-top:5px;font-size:12px;color:#7a889d}
.portal-topbar__nav{flex:1;display:flex;align-items:center;gap:8px}.portal-topbar__nav-item{border:none;background:transparent;padding:12px 18px;border-radius:12px;color:#53657f;font-size:16px;cursor:pointer;transition:all .2s}.portal-topbar__nav-item:hover{background:#f2f7ff;color:#2c86ff}.portal-topbar__nav-item.is-active{background:#eaf3ff;color:#2c86ff;font-weight:700}
.portal-topbar__actions{display:flex;align-items:center;gap:12px}.portal-topbar__role{width:132px}.portal-topbar__user{display:flex;flex-direction:column;align-items:flex-end;min-width:88px}.portal-topbar__user strong{color:#1a2d4d;font-size:14px}.portal-topbar__user span{margin-top:3px;color:#7a889d;font-size:12px}
@media (max-width: 1200px){.portal-topbar__inner{padding:0 16px}.portal-topbar__brand-text strong{font-size:22px}.portal-topbar__nav{overflow:auto}.portal-topbar__nav-item{white-space:nowrap;padding:10px 14px}}
</style>
