<template>
  <header ref="topbarRef" class="portal-topbar" :class="{ 'is-menu-open': isMenuOpen }">
    <div class="portal-topbar__inner">
      <div class="portal-topbar__left">
        <button v-if="showMenu && menuItems.length" type="button" class="portal-topbar__menu-trigger" @click="toggleMenu">
          <el-icon><Menu /></el-icon>
          <span>{{ isMenuOpen ? '收起菜单' : '菜单' }}</span>
        </button>

        <div class="portal-topbar__brand" @click="goHome">
          <div class="portal-topbar__logo">
            <i class="portal-topbar__logo-main ri-graduation-cap-line"></i>
          </div>
          <div class="portal-topbar__brand-text">
            <strong>校园智学</strong>
            <span>Campus Learning</span>
          </div>
        </div>
      </div>

      <div class="portal-topbar__right">
        <div v-if="showMenu && menuItems.length" class="portal-topbar__search">
          <el-input
            v-model="searchKeyword"
            clearable
            placeholder="菜单搜索"
            @click.stop
            @input="openSearchPanel"
            @clear="closeSearchPanel"
            @focus="searchFocused = true"
            @blur="handleSearchBlur"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>

          <div
            v-if="showSearchDropdown"
            class="portal-topbar__search-panel"
            @mousedown.stop
            @mouseenter="searchPanelHover = true"
            @mouseleave="searchPanelHover = false"
          >
            <button
              v-for="item in filteredMenuItems"
              :key="item.path"
              type="button"
              class="portal-topbar__search-item"
              @click="handleSearchSelect(item.path)"
            >
              <strong>{{ item.title }}</strong>
              <span>{{ item.desc || '点击进入功能页面' }}</span>
            </button>
            <div v-if="!filteredMenuItems.length" class="portal-topbar__search-empty">未匹配到相关菜单</div>
          </div>
        </div>

        <div v-if="showUserPanel" class="portal-topbar__icon-group">
          <button type="button" class="portal-topbar__icon-btn" title="返回首页" @click="goHome">
            <el-icon><House /></el-icon>
          </button>
          <button type="button" class="portal-topbar__icon-btn" title="帮助中心" @click="openHelpCenter">
            <el-icon><QuestionFilled /></el-icon>
          </button>
          <button type="button" class="portal-topbar__icon-btn" title="消息提醒" @click="openNoticeCenter">
            <el-icon><Bell /></el-icon>
          </button>
        </div>

        <el-dropdown
          v-if="showRoleSwitch && roleOptions.length > 1"
          trigger="hover"
          placement="bottom-end"
          popper-class="portal-topbar-role-dropdown"
          @command="handleRoleChange"
        >
          <button type="button" class="portal-topbar__role-pill portal-topbar__role-pill--switchable">
            <span>{{ currentRoleLabel }}</span>
            <el-icon class="portal-topbar__role-icon"><Grid /></el-icon>
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="item in roleOptions"
                :key="item.value"
                :command="item.value"
                :disabled="item.value === selectedRole"
              >
                {{ item.label }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <div v-else-if="showRoleSwitch" class="portal-topbar__role-pill">{{ currentRoleLabel }}</div>

        <el-dropdown v-if="showUserPanel" trigger="click" placement="bottom-end" popper-class="portal-topbar-user-dropdown">
          <button type="button" class="portal-topbar__avatar">
            <span>{{ avatarText }}</span>
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <div class="portal-topbar__dropdown-header">
                <strong>{{ userDisplayName }}</strong>
                <span>{{ currentRoleLabel }}</span>
              </div>

              <div v-if="themeStore.themes.length > 1" class="portal-topbar__theme-panel">
                <div class="portal-topbar__theme-title">主题色</div>
                <button
                  v-for="theme in themeStore.themes"
                  :key="theme.name"
                  type="button"
                  class="portal-topbar__theme-chip"
                  :class="{ 'is-active': theme.name === themeStore.activeTheme }"
                  @click.stop="themeStore.setTheme(theme.name)"
                >
                  <span class="portal-topbar__theme-dot" :style="{ background: theme.color }"></span>
                  <span>{{ theme.label }}</span>
                </button>
              </div>

              <el-dropdown-item divided @click="goProfilePage">
                <el-icon><Files /></el-icon>
                <span>我的档案</span>
              </el-dropdown-item>
              <el-dropdown-item @click="goAccountSettingsPage">
                <el-icon><Setting /></el-icon>
                <span>账号设置</span>
              </el-dropdown-item>
              <el-dropdown-item divided @click="emit('logout')">
                <el-icon><SwitchButton /></el-icon>
                <span>退出</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <div v-else-if="showAuthAction" class="portal-topbar__guest-actions">
          <button
            type="button"
            class="portal-topbar__guest-btn"
            :class="isRegisterRoute ? 'portal-topbar__guest-btn--primary is-active' : 'portal-topbar__guest-btn--ghost'"
            @click="router.push('/register')"
          >
            注册
          </button>
          <button
            type="button"
            class="portal-topbar__guest-btn"
            :class="isLoginRoute ? 'portal-topbar__guest-btn--primary is-active' : 'portal-topbar__guest-btn--ghost'"
            @click="router.push('/login')"
          >
            登录
          </button>
        </div>
      </div>
    </div>

    <transition name="portal-menu-overlay">
      <div v-if="showMenu && menuItems.length && isMenuOpen" class="portal-topbar__menu-overlay" @click.self="closeMenu">
        <section class="portal-topbar__menu-surface" @click.stop>
          <aside class="portal-topbar__menu-aside">
            <div class="portal-topbar__menu-aside-title">{{ currentRoleMenuTitle }}</div>
          </aside>

          <div class="portal-topbar__menu-content">
            <section
              v-for="group in normalizedGroups"
              :key="group.key"
              class="portal-topbar__menu-group"
            >
              <div class="portal-topbar__menu-card">
                <h3>{{ group.label }}</h3>
                <button
                  v-for="item in group.items"
                  :key="item.path"
                  type="button"
                  class="portal-topbar__menu-link"
                  :class="{ 'is-active': item.path === activePath }"
                  @click="handleNav(item.path)"
                >
                  <strong>{{ item.title }}</strong>
                  <span>{{ item.desc || '进入对应功能模块' }}</span>
                </button>
              </div>
            </section>
          </div>
        </section>
      </div>
    </transition>

    <el-drawer
      v-model="helpCenterVisible"
      title="帮助中心"
      size="480px"
      append-to-body
      :with-header="true"
      class="portal-topbar-drawer"
    >
      <div v-loading="helpLoading" class="portal-topbar__drawer-body">
        <section class="portal-topbar__drawer-hero">
          <div>
            <span>Support</span>
            <h3>帮助中心</h3>
            <p>查看常见问题、使用说明与当前门户操作指引。</p>
          </div>
        </section>
        <div
          v-for="section in helpSections"
          :key="section.title"
          class="portal-topbar__help-section"
        >
          <h3>{{ section.title }}</h3>
          <p>{{ section.description }}</p>
          <div
            v-for="item in section.items"
            :key="item.title"
            class="portal-topbar__help-item"
          >
            <strong>{{ item.title }}</strong>
            <span>{{ item.content }}</span>
          </div>
        </div>
        <div v-if="helpContact" class="portal-topbar__help-contact">{{ helpContact }}</div>
      </div>
    </el-drawer>

    <el-drawer
      v-model="noticeVisible"
      title="消息提醒"
      size="520px"
      append-to-body
      :with-header="true"
      class="portal-topbar-drawer"
    >
      <div v-loading="noticeLoading" class="portal-topbar__drawer-body">
        <section class="portal-topbar__drawer-hero">
          <div>
            <span>Notice</span>
            <h3>消息提醒</h3>
            <p>集中查看近期通知公告，并快速浏览详细内容。</p>
          </div>
        </section>
        <div class="portal-topbar__notice-layout">
          <div class="portal-topbar__notice-list">
            <button
              v-for="message in messages"
              :key="message.messageId"
              type="button"
              class="portal-topbar__notice-item"
              @click="selectMessage(message)"
            >
              <div class="portal-topbar__notice-head">
                <strong>{{ message.messageTitle }}</strong>
                <span>{{ messageTypeLabel(message.messageType) }}</span>
              </div>
              <p>{{ message.messageContent || '暂无内容摘要' }}</p>
            </button>
            <el-empty v-if="!noticeLoading && !messages.length" description="暂无新的消息提醒" />
          </div>
          <div v-if="activeMessage" class="portal-topbar__notice-detail">
            <h3>{{ activeMessage.messageTitle }}</h3>
            <div class="portal-topbar__notice-meta">
              <span>{{ messageTypeLabel(activeMessage.messageType) }}</span>
              <span>{{ activeMessage.createTime || '最近更新' }}</span>
            </div>
            <div class="portal-topbar__notice-content">{{ activeMessage.messageContent || '暂无详细内容' }}</div>
            <div class="portal-topbar__form-footer" style="margin-top: 12px;" v-if="activeMessage.actionType === 'resumeExam'">
              <el-button type="warning" plain @click="goMessageAction(activeMessage)">继续作答</el-button>
            </div>
            <div class="portal-topbar__form-footer" style="margin-top: 12px;" v-else-if="activeMessage.actionType === 'growth'">
              <el-button type="primary" plain @click="goProfilePage">查看成长账户</el-button>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>

  </header>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Bell,
  Files,
  Grid,
  House,
  Menu,
  QuestionFilled,
  Search,
  Setting,
  SwitchButton,
} from '@element-plus/icons-vue'
import usePortalUserStore from '@/store/user'
import usePortalThemeStore from '@/store/theme'
import { getPortalHelpCenter, getPortalMessageCenter } from '@/api/portal'

interface MenuItem {
  title: string
  path: string
  desc?: string
}

interface MenuGroup {
  key: string
  label: string
  items: MenuItem[]
}

const props = withDefaults(
  defineProps<{
    menuItems?: MenuItem[]
    menuGroups?: MenuGroup[]
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
    menuGroups: () => [],
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
const themeStore = usePortalThemeStore()
const topbarRef = ref<HTMLElement | null>(null)
const isMenuOpen = ref(false)
const searchKeyword = ref('')
const searchFocused = ref(false)
const searchPanelHover = ref(false)
const helpCenterVisible = ref(false)
const noticeVisible = ref(false)
const helpLoading = ref(false)
const noticeLoading = ref(false)
const helpSections = ref<Array<{ title: string; description: string; items: Array<{ title: string; content: string }> }>>([])
const helpContact = ref('')
const messages = ref<any[]>([])
const activeMessage = ref<any>(null)

const roleOptions = computed(() => {
  if (props.roleOptionsOverride.length) {
    return props.roleOptionsOverride
  }
  const roleMap: Record<string, string> = {
    student: '学生',
    teacher: '教师',
    parent: '家长',
    admin: '管理员',
  }
  return userStore.availablePortalRoles.map((role) => ({ label: roleMap[role] || role, value: role }))
})

const currentRoleLabel = computed(() => {
  return roleOptions.value.find((item) => item.value === props.selectedRole)?.label || '学习门户'
})

const currentRoleMenuTitle = computed(() => `${currentRoleLabel.value}全部服务`)
const normalizedGroups = computed(() => props.menuGroups.length ? props.menuGroups : [{ key: 'default', label: currentRoleMenuTitle.value, items: props.menuItems }])
const userDisplayName = computed(() => userStore.user?.nickName || userStore.user?.userName || '学习用户')
const avatarText = computed(() => String(userDisplayName.value).slice(0, 1).toUpperCase())
const isLoginRoute = computed(() => router.currentRoute.value.path === '/login')
const isRegisterRoute = computed(() => router.currentRoute.value.path === '/register')

const filteredMenuItems = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (!keyword) {
    return props.menuItems
  }
  return props.menuItems.filter((item) => `${item.title} ${item.desc || ''}`.toLowerCase().includes(keyword))
})

const showSearchDropdown = computed(() => (searchFocused.value || searchPanelHover.value) && !!props.showMenu && props.menuItems.length > 0)

watch(
  () => props.activePath,
  () => {
    isMenuOpen.value = false
    searchFocused.value = false
    searchKeyword.value = ''
  }
)

watch(
  () => props.showMenu,
  (visible) => {
    if (!visible) {
      isMenuOpen.value = false
      closeSearchPanel()
    }
  }
)

function goHome() {
  if (props.selectedRole && props.selectedRole !== 'admin') {
    const target = props.menuItems[0]?.path || `/${props.selectedRole}/dashboard`
    router.push(target)
    return
  }
  router.push('/login')
}

function handleNav(path: string) {
  isMenuOpen.value = false
  router.push(path)
}

function handleSearchSelect(path: string) {
  closeSearchPanel()
  handleNav(path)
}

function toggleMenu() {
  isMenuOpen.value = !isMenuOpen.value
  closeSearchPanel()
}

function closeMenu() {
  isMenuOpen.value = false
}

function openSearchPanel() {
  searchFocused.value = true
}

function closeSearchPanel() {
  searchFocused.value = false
  searchPanelHover.value = false
  searchKeyword.value = ''
}

function handleSearchBlur() {
  window.setTimeout(() => {
    if (!searchPanelHover.value) {
      searchFocused.value = false
    }
  }, 120)
}

function handleRoleChange(value: string) {
  emit('role-change', value)
}

function showComingSoon(label: string) {
  ElMessage.info(`${label}功能正在完善中`)
}

function messageTypeLabel(type: string) {
  if (type === 'EXAM_REMINDER') return '考试提醒'
  if (type === 'ACHIEVEMENT') return '成就提醒'
  return '系统通知'
}

async function openHelpCenter() {
  helpCenterVisible.value = true
  if (helpSections.value.length) return
  helpLoading.value = true
  try {
    const res = await getPortalHelpCenter()
    helpSections.value = res.data?.sections || []
    helpContact.value = res.data?.contact || ''
  } finally {
    helpLoading.value = false
  }
}

async function openNoticeCenter() {
  noticeVisible.value = true
  if (!messages.value.length) {
    noticeLoading.value = true
    try {
      const res = await getPortalMessageCenter({ userId: userStore.user?.userId })
      messages.value = res.data || []
    } finally {
      noticeLoading.value = false
    }
  }
  if (messages.value.length && !activeMessage.value) {
    activeMessage.value = messages.value[0]
  }
}

function selectMessage(message: any) {
  activeMessage.value = message || null
  if (!message) return
  if (message.actionType === 'resumeExam' && message.actionTarget) {
    // keep detail open, actual jump left to user click
    return
  }
}

function goMessageAction(message: any) {
  if (!message) return
  if (message.actionType === 'resumeExam' && message.actionTarget) {
    router.push({
      path: `/student/exams/session/${message.actionTarget}`,
      query: {
        paperId: String(message.paperId || ''),
      },
    })
    noticeVisible.value = false
  }
}

function goProfilePage() {
  router.push('/account/profile')
}

function goAccountSettingsPage() {
  router.push('/account/settings')
}

function handleClickOutside(event: MouseEvent) {
  if (!topbarRef.value) return
  const target = event.target as Node
  if (!topbarRef.value.contains(target)) {
    isMenuOpen.value = false
    closeSearchPanel()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.portal-topbar {
  --topbar-height: 58px;
  --auth-topbar-text: #24486f;
  --auth-topbar-muted: rgba(58, 90, 126, 0.72);
  --auth-topbar-line: rgba(126, 166, 214, 0.26);
  --auth-topbar-surface: rgba(255, 255, 255, 0.52);
  position: sticky;
  top: 0;
  z-index: 100;
  color: var(--auth-topbar-text);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.42) 0%, rgba(255, 255, 255, 0.22) 100%);
  box-shadow: 0 10px 26px rgba(63, 97, 145, 0.08);
  backdrop-filter: blur(14px);
  overflow: visible;
}

.portal-topbar::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 1px;
  background: rgba(143, 176, 219, 0.34);
}

.portal-topbar::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 1px;
  background: rgba(255, 255, 255, 0.8);
  pointer-events: none;
}

.portal-topbar__inner {
  position: relative;
  z-index: 2;
  min-height: var(--topbar-height);
  padding: 0 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.portal-topbar__left,
.portal-topbar__right {
  display: flex;
  align-items: center;
}

.portal-topbar__left {
  gap: 12px;
  min-width: 0;
}

.portal-topbar__right {
  gap: 12px;
  margin-left: auto;
}

.portal-topbar__menu-trigger {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 34px;
  padding: 0 14px;
  border: none;
  border-radius: 4px;
  color: inherit;
  background: rgba(255, 255, 255, 0.68);
  border: 1px solid var(--auth-topbar-line);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.7);
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: background-color .2s ease, border-color .2s ease, transform .2s ease;
}

.portal-topbar__menu-trigger:hover,
.portal-topbar.is-menu-open .portal-topbar__menu-trigger {
  background: rgba(255, 255, 255, 0.82);
  border-color: rgba(118, 160, 213, 0.34);
  transform: translateY(-1px);
}

.portal-topbar__brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 2px 4px 0;
  min-width: 0;
  cursor: pointer;
}

.portal-topbar__logo {
  width: 32px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.portal-topbar__logo-main {
  font-size: 22px;
  color: #315fca;
  line-height: 1;
}

.portal-topbar__brand-text {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.portal-topbar__brand-text strong {
  font-size: 16px;
  line-height: 1.1;
  letter-spacing: 0;
  color: var(--auth-topbar-text);
  white-space: nowrap;
  font-weight: 700;
}

.portal-topbar__brand-text span {
  margin-top: 2px;
  font-size: 10px;
  letter-spacing: 0;
  line-height: 1.4;
  color: var(--auth-topbar-muted);
  white-space: nowrap;
}

.portal-topbar__search {
  position: relative;
  width: min(260px, 20vw);
  z-index: 120;
}

.portal-topbar__search :deep(.el-input__wrapper) {
  height: 32px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: none;
  border: 1px solid rgba(148, 180, 220, 0.24);
  box-shadow: 0 6px 16px rgba(86, 119, 168, 0.06);
  transition: border-color .2s ease, background-color .2s ease, box-shadow .2s ease;
}

.portal-topbar__search :deep(.el-input__inner),
.portal-topbar__search :deep(.el-input__prefix) {
  color: var(--auth-topbar-text);
}

.portal-topbar__search :deep(.el-input__inner::placeholder) {
  color: color-mix(in srgb, var(--auth-topbar-text) 58%, transparent);
}

.portal-topbar__search :deep(.el-input__wrapper.is-focus),
.portal-topbar__search :deep(.el-input__wrapper:hover) {
  background: #fff;
  border-color: rgba(118, 160, 213, 0.34);
  box-shadow:
    0 0 0 3px rgba(130, 175, 230, 0.12),
    0 8px 18px rgba(86, 119, 168, 0.08);
}

.portal-topbar__search-panel {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 100%;
  min-width: 320px;
  padding: 10px;
  border-radius: 6px;
  background:
    linear-gradient(180deg, rgba(248, 251, 255, 0.96) 0%, rgba(239, 245, 253, 0.96) 100%);
  border: 1px solid rgba(148, 180, 220, 0.24);
  box-shadow: 0 14px 30px rgba(63, 97, 145, 0.12);
  backdrop-filter: blur(14px);
  z-index: 160;
}

.portal-topbar__search-item {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 10px 12px;
  text-align: left;
  border: 1px solid transparent;
  border-radius: 4px;
  color: var(--auth-topbar-text);
  background: transparent;
  cursor: pointer;
  transition: background-color .18s ease, border-color .18s ease, transform .18s ease;
}

.portal-topbar__search-item:hover {
  background: rgba(255, 255, 255, 0.78);
  border-color: rgba(148, 180, 220, 0.18);
  transform: translateY(-1px);
}

.portal-topbar__search-item strong {
  font-size: 14px;
  line-height: 1.35;
}

.portal-topbar__search-item span {
  color: var(--auth-topbar-muted);
  line-height: 1.45;
}

.portal-topbar__search-empty {
  padding: 10px 12px;
  font-size: 12px;
  color: var(--auth-topbar-muted);
}

.portal-topbar__icon-group {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 3px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.52);
  border: 1px solid rgba(138, 172, 220, 0.2);
}

.portal-topbar__icon-btn,
.portal-topbar__avatar {
  width: 30px;
  height: 30px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  color: var(--auth-topbar-text);
  background: transparent;
  cursor: pointer;
  transition: background-color .2s ease;
}

.portal-topbar__icon-btn:hover,
.portal-topbar__avatar:hover {
  background: rgba(255, 255, 255, 0.72);
}

.portal-topbar__role-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-width: 96px;
  height: 34px;
  padding: 0 14px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid var(--auth-topbar-line);
  color: var(--auth-topbar-text);
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;
  justify-content: center;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.76);
  transition: background-color .2s ease, border-color .2s ease, transform .2s ease;
  outline: none;
}

.portal-topbar__role-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  line-height: 1;
}

.portal-topbar__role-pill--switchable {
  cursor: pointer;
}

.portal-topbar__role-pill:hover,
.portal-topbar__role-pill--switchable:focus-visible {
  background: rgba(255, 255, 255, 0.84);
  border-color: rgba(118, 160, 213, 0.34);
  transform: translateY(-1px);
}

.portal-topbar__role-pill:focus,
.portal-topbar__role-pill:focus-visible,
.portal-topbar__guest-btn:focus,
.portal-topbar__guest-btn:focus-visible,
.portal-topbar__menu-trigger:focus,
.portal-topbar__menu-trigger:focus-visible,
.portal-topbar__icon-btn:focus,
.portal-topbar__icon-btn:focus-visible,
.portal-topbar__avatar:focus,
.portal-topbar__avatar:focus-visible {
  outline: none;
}

.portal-topbar__avatar {
  background: rgba(255, 255, 255, 0.76);
  font-weight: 700;
}

.portal-topbar__guest-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 3px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(138, 172, 220, 0.2);
}

.portal-topbar__guest-btn {
  min-width: 84px;
  height: 32px;
  padding: 0 16px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease, border-color 0.2s ease, color 0.2s ease;
}

.portal-topbar__guest-btn:hover {
  transform: translateY(-1px);
}

.portal-topbar__guest-btn--ghost {
  color: var(--auth-topbar-text);
  border: 1px solid transparent;
  background: transparent;
  box-shadow: none;
}

.portal-topbar__guest-btn--ghost.is-active {
  border-color: rgba(118, 160, 213, 0.24);
  background: rgba(255, 255, 255, 0.62);
}

.portal-topbar__guest-btn--primary {
  color: #234b74;
  border: 1px solid rgba(118, 160, 213, 0.22);
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 6px 16px rgba(93, 130, 177, 0.08);
}

.portal-topbar__guest-btn--primary.is-active {
  border-color: rgba(118, 160, 213, 0.24);
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 6px 16px rgba(93, 130, 177, 0.08);
}

.portal-topbar__menu-overlay {
  position: fixed;
  left: 0;
  right: 0;
  top: var(--topbar-height);
  bottom: 0;
  background: rgba(56, 82, 122, 0.1);
  backdrop-filter: blur(6px);
}

.portal-topbar__menu-surface {
  display: grid;
  grid-template-columns: 180px minmax(0, 1fr);
  min-height: 420px;
  background:
    linear-gradient(180deg, rgba(248, 251, 255, 0.97) 0%, rgba(239, 245, 253, 0.96) 100%);
  color: var(--portal-text);
  border-top: 1px solid rgba(148, 180, 220, 0.22);
  box-shadow: 0 22px 42px rgba(63, 97, 145, 0.12);
}

.portal-topbar__menu-aside {
  padding: 16px 0;
  background: rgba(231, 239, 250, 0.92);
  border-right: 1px solid rgba(148, 180, 220, 0.2);
}

.portal-topbar__menu-aside-title {
  display: flex;
  align-items: center;
  height: 50px;
  padding: 0 20px;
  font-size: 15px;
  font-weight: 700;
  color: var(--portal-text);
  background: rgba(255, 255, 255, 0.72);
  border-left: 3px solid rgba(47, 107, 255, 0.4);
}

.portal-topbar__menu-content {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
  align-content: start;
  padding: 24px 24px 28px;
}

.portal-topbar__menu-group {
  min-width: 0;
}

.portal-topbar__menu-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 18px 18px 14px;
  border: 1px solid rgba(148, 180, 220, 0.2);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.68);
  box-shadow: 0 10px 24px rgba(63, 97, 145, 0.06);
}

.portal-topbar__menu-card h3 {
  margin: 0 0 10px;
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 0.2px;
  color: var(--portal-text);
}

.portal-topbar__menu-link {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 3px;
  margin-bottom: 6px;
  padding: 10px 12px;
  text-align: left;
  border: 1px solid transparent;
  border-radius: 6px;
  background: transparent;
  color: var(--portal-text);
  cursor: pointer;
  transition: transform .18s ease, background-color .18s ease, border-color .18s ease, color .18s ease;
}

.portal-topbar__menu-link strong {
  font-size: 15px;
  font-weight: 600;
  line-height: 1.4;
  letter-spacing: 0.2px;
}

.portal-topbar__menu-link span {
  font-size: 12px;
  line-height: 1.45;
  color: var(--portal-text-secondary);
}

.portal-topbar__menu-link:hover,
.portal-topbar__menu-link.is-active {
  transform: translateY(-1px);
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(148, 180, 220, 0.24);
  box-shadow: 0 10px 20px rgba(63, 97, 145, 0.08);
}

.portal-topbar__menu-link:hover strong,
.portal-topbar__menu-link.is-active strong {
  color: var(--portal-brand);
}

.portal-topbar__menu-link:hover span,
.portal-topbar__menu-link.is-active span {
  color: var(--portal-text-secondary);
}

.portal-topbar__theme-panel {
  padding: 12px 14px 6px;
}

.portal-topbar__theme-title {
  margin-bottom: 8px;
  font-size: 12px;
  font-weight: 700;
  color: #576b7f;
}

.portal-topbar__theme-chip {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 8px;
  height: 32px;
  margin-bottom: 6px;
  padding: 0 10px;
  border: 1px solid transparent;
  border-radius: 6px;
  background: transparent;
  color: #3f5368;
  cursor: pointer;
}

.portal-topbar__theme-chip:hover,
.portal-topbar__theme-chip.is-active {
  border-color: #d8e4f2;
  background: #f5f9ff;
}

.portal-topbar__theme-dot {
  width: 12px;
  height: 12px;
  border-radius: 999px;
  flex-shrink: 0;
}

:global(.portal-topbar-user-dropdown.el-popper),
:global(.portal-topbar-role-dropdown.el-popper) {
  border-radius: 6px;
  border: 1px solid #d9e1ec;
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.14);
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(14px);
  overflow: hidden;
}

:global(.portal-topbar-user-dropdown .el-popper__arrow),
:global(.portal-topbar-role-dropdown .el-popper__arrow) {
  display: none;
}

:global(.portal-topbar-role-dropdown .el-dropdown-menu) {
  padding: 8px;
  background: transparent;
}

:global(.portal-topbar-role-dropdown .el-dropdown-menu__item) {
  min-width: 110px;
  min-height: 36px;
  justify-content: center;
  line-height: 1;
  font-size: 13px;
  font-weight: 600;
  border-radius: 4px;
  color: #36506f;
}

:global(.portal-topbar-role-dropdown .el-dropdown-menu__item:not(.is-disabled):hover) {
  background: #f3f7ff;
  color: var(--portal-brand);
}

:global(.portal-topbar-role-dropdown .el-dropdown-menu__item.is-disabled) {
  color: #9aaabc;
  background: #f7f9fc;
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu) {
  padding: 0;
  min-width: 176px;
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item) {
  min-height: 42px;
  padding: 0 14px;
  gap: 10px;
  color: #2f4458;
  font-size: 14px;
  font-weight: 600;
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item i),
:global(.portal-topbar-user-dropdown .el-dropdown-menu__item .el-icon) {
  color: #6f84a0;
  font-size: 15px;
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item:not(.is-disabled):hover) {
  background: #f3f7ff;
  color: var(--portal-brand);
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item:not(.is-disabled):hover i),
:global(.portal-topbar-user-dropdown .el-dropdown-menu__item:not(.is-disabled):hover .el-icon) {
  color: var(--portal-brand);
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item--divided) {
  margin-top: 0;
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item--divided::before) {
  left: 14px;
  right: 14px;
  top: 0;
  background-color: #edf2f7;
}

.portal-topbar__dropdown-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px 14px 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f7faff 100%);
  border-bottom: 1px solid #edf2f7;
}

.portal-topbar__dropdown-header strong {
  font-size: 15px;
  color: #233a53;
  line-height: 1.3;
}

.portal-topbar__dropdown-header span {
  font-size: 12px;
  color: #708398;
  line-height: 1.4;
}

.portal-topbar__drawer-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.portal-topbar__drawer-hero {
  padding: 16px;
  border-radius: 8px;
  border: 1px solid var(--portal-border);
  background: linear-gradient(180deg, #ffffff 0%, #f5f8fd 100%);
}

.portal-topbar__drawer-hero span {
  display: inline-flex;
  color: var(--portal-brand);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.portal-topbar__drawer-hero h3 {
  margin: 8px 0 0;
  font-size: 20px;
  color: var(--portal-text);
}

.portal-topbar__drawer-hero p {
  margin: 8px 0 0;
  color: var(--portal-text-secondary);
  font-size: 13px;
  line-height: 1.7;
}

.portal-topbar__help-section {
  padding: 14px;
  border-radius: 8px;
  border: 1px solid var(--portal-border);
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  box-shadow: 0 8px 18px rgba(63, 97, 145, 0.05);
}

.portal-topbar__help-section h3,
.portal-topbar__notice-detail h3,
.portal-topbar__password-block h3 {
  margin: 0;
  font-size: 16px;
  color: var(--portal-text);
}

.portal-topbar__help-section p {
  margin: 6px 0 0;
  font-size: 12px;
  color: var(--portal-text-secondary);
}

.portal-topbar__help-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid var(--portal-border);
}

.portal-topbar__help-item strong {
  font-size: 14px;
  color: var(--portal-text);
}

.portal-topbar__help-item span,
.portal-topbar__help-contact,
.portal-topbar__notice-item p,
.portal-topbar__notice-content,
.portal-topbar__notice-meta {
  font-size: 12px;
  line-height: 1.6;
  color: var(--portal-text-secondary);
}

.portal-topbar__notice-item {
  width: 100%;
  text-align: left;
  padding: 12px 14px;
  border: 1px solid var(--portal-border);
  border-radius: 8px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  cursor: pointer;
  transition: border-color .18s ease, transform .18s ease, box-shadow .18s ease;
}

.portal-topbar__notice-item:hover {
  border-color: var(--portal-border-strong);
  transform: translateY(-1px);
  box-shadow: var(--portal-shadow-soft);
}

.portal-topbar__notice-layout {
  display: grid;
  grid-template-columns: minmax(0, 0.95fr) minmax(0, 1.05fr);
  gap: 14px;
}

.portal-topbar__notice-list {
  display: grid;
  gap: 10px;
  align-content: start;
}

.portal-topbar__notice-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.portal-topbar__notice-head strong {
  font-size: 14px;
  color: var(--portal-text);
}

.portal-topbar__notice-head span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 44px;
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  background: var(--portal-brand-light);
  color: var(--portal-brand);
  font-size: 11px;
  font-weight: 700;
}

.portal-topbar__notice-item p {
  margin: 8px 0 0;
}

.portal-topbar__notice-detail {
  padding: 14px;
  border-radius: 8px;
  border: 1px solid var(--portal-border);
  background: linear-gradient(180deg, #ffffff 0%, #f7faff 100%);
}

.portal-topbar__notice-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
}

.portal-topbar__notice-content {
  margin-top: 10px;
}

.portal-topbar__profile-panel,
.portal-topbar__password-block {
  padding: 14px;
  border-radius: 8px;
  border: 1px solid var(--portal-border);
  background: var(--portal-card-solid);
}

.portal-topbar__profile-head {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--portal-border);
}

.portal-topbar__profile-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--portal-brand-light);
  color: var(--portal-brand);
  font-size: 18px;
  font-weight: 700;
}

.portal-topbar__profile-head strong {
  display: block;
  font-size: 16px;
  color: var(--portal-text);
}

.portal-topbar__profile-head p {
  margin: 4px 0 0;
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.portal-topbar__profile-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-top: 12px;
}

.portal-topbar__profile-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 10px 12px;
  border-radius: 8px;
  background: var(--portal-surface-bg);
}

.portal-topbar__profile-item span {
  font-size: 12px;
  color: var(--portal-text-secondary);
}

.portal-topbar__profile-item strong {
  font-size: 14px;
  color: var(--portal-text);
  line-height: 1.5;
}

.portal-topbar__profile-item--full {
  grid-column: 1 / -1;
}

.portal-topbar__form {
  margin-top: 4px;
}

.portal-topbar__form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.portal-topbar__password-block {
  margin-top: 2px;
}

:global(.portal-topbar-drawer .el-drawer__header) {
  margin-bottom: 0;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--portal-border);
}

:global(.portal-topbar-drawer .el-drawer__body) {
  padding: 14px;
  background: var(--portal-surface-bg);
}

.portal-menu-overlay-enter-active,
.portal-menu-overlay-leave-active {
  transition: opacity .18s ease;
}

.portal-menu-overlay-enter-from,
.portal-menu-overlay-leave-to {
  opacity: 0;
}

@media (max-width: 1200px) {
  .portal-topbar__search {
    width: 220px;
  }

  .portal-topbar__menu-content {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 14px;
  }

  .portal-topbar__notice-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .portal-topbar {
    --topbar-height: 56px;
  }

  .portal-topbar__brand-text span,
  .portal-topbar__icon-group {
    display: none;
  }

  .portal-topbar__search {
    width: 180px;
  }

  .portal-topbar__guest-actions {
    gap: 6px;
  }

  .portal-topbar__guest-btn {
    min-width: 64px;
    padding: 0 12px;
  }

  .portal-topbar__menu-surface {
    grid-template-columns: 1fr;
  }

  .portal-topbar__menu-aside {
    padding: 0;
  }

  .portal-topbar__menu-content {
    grid-template-columns: 1fr;
    padding: 16px 16px 20px;
  }
}
</style>
