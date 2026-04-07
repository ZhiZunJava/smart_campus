<template>
  <header ref="topbarRef" class="portal-topbar" :class="{ 'is-menu-open': isMenuOpen }">
    <div class="portal-topbar__inner">
      <div class="portal-topbar__left">
        <div 
          class="portal-topbar__menu-trigger-wrapper" 
          @mouseenter="openMenu" 
        >
          <button
            v-if="showMenu && menuItems.length"
            type="button"
            class="portal-topbar__menu-trigger"
            @mouseenter="openMenu"
            @click.stop="toggleMenu"
          >
            <i class="ri-apps-line" />
            <span>服务</span>
          </button>
        </div>

        <button type="button" class="portal-topbar__hamburger portal-hide-desktop" @click="mobileMenuVisible = true">
          <i class="ri-menu-line" />
        </button>
        <div class="portal-topbar__brand" @click="goHome">
          <div class="portal-topbar__logo">
            <img class="portal-topbar__logo-image portal-topbar__logo-image--full" :src="portalLogo" alt="教务管理信息系统">
            <img class="portal-topbar__logo-image portal-topbar__logo-image--compact" :src="portalSmallLogo" alt="教务管理信息系统">
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
            <el-scrollbar max-height="400px">
              <button
                v-for="item in filteredMenuItems"
                :key="item.path"
                type="button"
                class="portal-topbar__search-item"
                @click="handleSearchSelect(item.path)"
              >
                <strong>
                  <span v-if="searchKeyword" class="search-highlight-wrapper">
                    <template v-for="(part, index) in highlightText(item.title, searchKeyword)" :key="index">
                      <span v-if="part.isHighlight" class="search-highlight">{{ part.text }}</span>
                      <template v-else>{{ part.text }}</template>
                    </template>
                  </span>
                  <template v-else>{{ item.title }}</template>
                </strong>
              </button>
              <div v-if="!filteredMenuItems.length" class="portal-topbar__search-empty">未匹配到相关菜单</div>
            </el-scrollbar>
          </div>
        </div>

        <el-dropdown
          v-if="showRoleSwitch && roleOptions.length > 1"
          trigger="hover"
          placement="bottom-end"
          popper-class="portal-topbar-role-dropdown"
          @command="handleRoleChange"
        >
          <button type="button" class="portal-topbar__role-pill portal-topbar__role-pill--switchable">
            <i class="ri-swap-line portal-topbar__role-icon" />
            <span>{{ currentRoleLabel }}</span>
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
            <i class="ri-user-3-line" />
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <div class="portal-topbar__dropdown-header">
                <div class="portal-topbar__dropdown-avatar"><i class="ri-user-3-line" /></div>
                <div class="portal-topbar__dropdown-info">
                  <strong>{{ userDisplayName }}</strong>
                  <span>{{ currentRoleLabel }}</span>
                </div>
              </div>

              <el-dropdown-item @click="goProfilePage">
                <i class="ri-id-card-line" />
                <span>我的档案</span>
              </el-dropdown-item>
              <el-dropdown-item v-if="userStore.availablePortalRoles.includes('student')" @click="goFavoritesPage">
                <i class="ri-star-line" />
                <span>我的收藏</span>
              </el-dropdown-item>
              <el-dropdown-item @click="goAccountSettingsPage">
                <i class="ri-settings-3-line" />
                <span>账号设置</span>
              </el-dropdown-item>
              <el-dropdown-item divided @click="emit('logout')">
                <i class="ri-logout-box-r-line" />
                <span>退出登录</span>
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
        <section class="portal-topbar__menu-surface" @click.stop @mouseenter="cancelCloseTimer" @mouseleave="closeMenuTimer">
          <div class="portal-topbar__menu-content" @mouseenter="cancelCloseTimer">
            <div class="portal-topbar__menu-filter"></div>
            <el-scrollbar class="menu-side-scrollbar">
              <aside class="menu-side-wrapper">
                <div class="menu-side-item active">
                  <el-icon><Grid /></el-icon>
                  <span>{{ currentRoleLabel }}全部服务</span>
                </div>
              </aside>
            </el-scrollbar>
            <el-scrollbar class="second-menu-scrollbar">
              <div class="second-menu-wrapper">
                <section
                  v-for="group in normalizedGroups"
                  :key="group.key"
                  class="portal-topbar__menu-group"
                >
                  <h3>{{ group.label }}</h3>
                  <div class="portal-topbar__menu-links">
                    <button
                      v-for="item in group.items"
                      :key="item.path"
                      type="button"
                      class="portal-topbar__menu-link"
                      :class="{ 'is-active': item.path === activePath }"
                      @click="handleNav(item.path)"
                    >
                      <strong>{{ item.title }}</strong>
                      <span>{{ item.desc || '点击进入对应功能页面' }}</span>
                    </button>
                  </div>
                </section>
              </div>
            </el-scrollbar>
          </div>
        </section>
      </div>
    </transition>

    <el-drawer
      v-model="helpCenterVisible"
      title="帮助中心"
      :size="helpDrawerSize"
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
      :size="noticeDrawerSize"
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
        <div class="portal-topbar__notice-filter">
          <button
            type="button"
            class="portal-topbar__notice-filter-btn"
            :class="{ 'is-active': noticeFilter === 'unread' }"
            @click="noticeFilter = 'unread'"
          >
            未读({{ unreadMessages.length }})
          </button>
          <button
            type="button"
            class="portal-topbar__notice-filter-btn"
            :class="{ 'is-active': noticeFilter === 'read' }"
            @click="noticeFilter = 'read'"
          >
            已读({{ readMessages.length }})
          </button>
        </div>
        <div class="portal-topbar__notice-layout">
          <div class="portal-topbar__notice-list">
            <button
              v-for="message in filteredMessages"
              :key="message.messageId"
              type="button"
              class="portal-topbar__notice-item"
              @click="selectMessage(message)"
            >
              <div class="portal-topbar__notice-icon" :class="`is-${messageIconTone(message)}`">
                <el-icon v-if="message.messageType === 'EXAM_REMINDER'"><Bell /></el-icon>
                <el-icon v-else-if="message.messageType === 'ACHIEVEMENT'"><Grid /></el-icon>
                <el-icon v-else><Files /></el-icon>
              </div>
              <div class="portal-topbar__notice-head">
                <strong>{{ message.messageTitle }}</strong>
                <span>{{ messageTypeLabel(message.messageType) }}</span>
              </div>
              <p>{{ message.messageContent || '暂无内容摘要' }}</p>
            </button>
            <el-empty v-if="!noticeLoading && !filteredMessages.length" :description="noticeFilter === 'unread' ? '暂无未读消息提醒' : '暂无已读消息提醒'" />
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

    <el-drawer
      v-model="mobileMenuVisible"
      direction="ltr"
      size="280px"
      :with-header="false"
      append-to-body
      class="portal-topbar-mobile-drawer"
    >
      <div class="mobile-drawer-header">
        <img :src="portalLogo" alt="Logo" class="mobile-drawer-logo" />
        <button type="button" class="mobile-drawer-close" @click="mobileMenuVisible = false">
          <i class="ri-close-line" />
        </button>
      </div>
      <div v-if="showRoleSwitch && roleOptions.length > 1" class="mobile-drawer-role">
        <button
          v-for="item in roleOptions"
          :key="item.value"
          type="button"
          class="mobile-drawer-role-btn"
          :class="{ 'is-active': item.value === selectedRole }"
          @click="handleRoleChange(item.value); mobileMenuVisible = false"
        >
          {{ item.label }}
        </button>
      </div>
      <el-scrollbar class="mobile-drawer-scroll">
        <div v-for="group in normalizedGroups" :key="group.key" class="mobile-drawer-group">
          <h4>{{ group.label }}</h4>
          <button
            v-for="item in group.items"
            :key="item.path"
            type="button"
            class="mobile-drawer-link"
            :class="{ 'is-active': item.path === activePath }"
            @click="handleNav(item.path); mobileMenuVisible = false"
          >
            {{ item.title }}
          </button>
        </div>
      </el-scrollbar>
    </el-drawer>

  </header>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from '@/utils/feedback'
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
import portalLogo from '@/assets/img/logo.png'
import portalSmallLogo from '@/assets/img/small-logo.png'

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
let menuCloseTimer: number | null = null
const helpSections = ref<Array<{ title: string; description: string; items: Array<{ title: string; content: string }> }>>([])
const helpContact = ref('')
const mobileMenuVisible = ref(false)

// Responsive drawer sizing
const windowWidth = ref(typeof window !== 'undefined' ? window.innerWidth : 1200)
function onWinResize() { windowWidth.value = window.innerWidth }
const isMobile = computed(() => windowWidth.value <= 768)
const helpDrawerSize = computed(() => isMobile.value ? '100%' : '480px')
const noticeDrawerSize = computed(() => isMobile.value ? '100%' : '520px')
const messages = ref<any[]>([])
const activeMessage = ref<any>(null)
const noticeFilter = ref<'unread' | 'read'>('unread')
const localReadMessageIds = ref<string[]>([])
const unreadMessages = computed(() => messages.value.filter((item: any) => !isMessageRead(item)))
const readMessages = computed(() => messages.value.filter((item: any) => isMessageRead(item)))
const filteredMessages = computed(() => noticeFilter.value === 'unread' ? unreadMessages.value : readMessages.value)

const roleOptions = computed(() => {
  if (props.roleOptionsOverride.length) {
    return props.roleOptionsOverride
  }
  const roleMap: Record<string, string> = {
    student: '学生',
    teacher: '教师',
    advisor: '辅导员',
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
const sideQuickItems = computed(() => props.menuItems.slice(0, 4))

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

function highlightText(text: string, keyword: string) {
  if (!keyword) return [{ text, isHighlight: false }]
  const lowerText = text.toLowerCase()
  const lowerKeyword = keyword.toLowerCase()
  const result = []
  let startIndex = 0
  let index = lowerText.indexOf(lowerKeyword)
  
  while (index !== -1) {
    if (index > startIndex) {
      result.push({ text: text.substring(startIndex, index), isHighlight: false })
    }
    result.push({ text: text.substring(index, index + keyword.length), isHighlight: true })
    startIndex = index + keyword.length
    index = lowerText.indexOf(lowerKeyword, startIndex)
  }
  
  if (startIndex < text.length) {
    result.push({ text: text.substring(startIndex), isHighlight: false })
  }
  
  return result
}

function toggleMenu() {
  isMenuOpen.value = !isMenuOpen.value
  closeSearchPanel()
}

function openMenu() {
  if (props.showMenu && props.menuItems.length) {
    cancelCloseTimer()
    isMenuOpen.value = true
    closeSearchPanel()
  }
}

function closeMenuTimer() {
  cancelCloseTimer()
  menuCloseTimer = window.setTimeout(() => {
    isMenuOpen.value = false
  }, 250)
}

function cancelCloseTimer() {
  if (menuCloseTimer) {
    window.clearTimeout(menuCloseTimer)
    menuCloseTimer = null
  }
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
  if (type === 'NOTICE') return '通知公告'
  return '系统通知'
}

function messageReadKey(message: any) {
  return String(message?.messageId || message?.noticeId || message?.actionTarget || '')
}

function normalizeTopbarMessage(message: any, index: number) {
  const key = String(message?.messageId || message?.noticeId || message?.actionTarget || `message-${index}`)
  const hasServerReadState = message?.readFlag !== undefined
  const isRead = hasServerReadState
    ? String(message?.readFlag || '0') === '1'
    : localReadMessageIds.value.includes(key)
  return {
    ...message,
    messageId: key,
    messageKind: message?.noticeType === '2'
      ? 'NOTICE'
      : message?.messageType === 'COURSE_SELECTION'
        ? 'COURSE_SELECTION'
      : message?.messageType === 'EXAM_REMINDER'
        ? 'EXAM_REMINDER'
        : message?.messageType === 'ACHIEVEMENT'
          ? 'ACHIEVEMENT'
          : 'SYSTEM',
    isRead,
  }
}

function isMessageRead(message: any) {
  return Boolean(message?.isRead)
}

function markMessageRead(message: any) {
  if (!message || message.noticeId || message.readFlag !== undefined) return
  const key = messageReadKey(message)
  if (!key || localReadMessageIds.value.includes(key)) return
  localReadMessageIds.value = [...localReadMessageIds.value, key]
  sessionStorage.setItem('portal-local-read-messages', JSON.stringify(localReadMessageIds.value))
}

function messageIconTone(message: any) {
  if (message?.messageKind === 'ACHIEVEMENT') return 'success'
  if (message?.messageKind === 'EXAM_REMINDER') return 'warning'
  if (message?.messageKind === 'COURSE_SELECTION') return 'primary'
  return 'info'
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
      const res = await getPortalMessageCenter({ userId: userStore.user?.userId, limit: 12 })
      messages.value = (res.data || []).map((item: any, index: number) => normalizeTopbarMessage(item, index))
    } finally {
      noticeLoading.value = false
    }
  }
  if (filteredMessages.value.length) {
    activeMessage.value = filteredMessages.value[0]
  } else {
    activeMessage.value = null
  }
}

function selectMessage(message: any) {
  activeMessage.value = message || null
  markMessageRead(message)
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
    return
  }
  if (message.actionPath) {
    router.push(message.actionPath)
    noticeVisible.value = false
  }
}

function goProfilePage() {
  router.push('/account/profile')
}

function goFavoritesPage() {
  if (userStore.availablePortalRoles.includes('student')) {
    router.push('/student/favorites')
    return
  }
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
  window.addEventListener('resize', onWinResize)
  try {
    const raw = sessionStorage.getItem('portal-local-read-messages')
    localReadMessageIds.value = raw ? JSON.parse(raw) : []
  } catch {
    localReadMessageIds.value = []
  }
})

watch(filteredMessages, (list) => {
  if (!list.length) {
    activeMessage.value = null
    return
  }
  if (!activeMessage.value || !list.some((item: any) => item.messageId === activeMessage.value?.messageId)) {
    activeMessage.value = list[0]
  }
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
  window.removeEventListener('resize', onWinResize)
})
</script>

<style scoped>
.portal-topbar {
  --topbar-height: 58px;
  --auth-topbar-text: #173c69;
  --auth-topbar-muted: rgba(23, 60, 105, 0.7);
  --auth-topbar-line: rgba(23, 60, 105, 0.14);
  --auth-topbar-surface: rgba(255, 255, 255, 0.34);
  --topbar-search-text: #173c69;
  --topbar-search-muted: rgba(23, 60, 105, 0.5);
  --topbar-search-border: rgba(150, 183, 219, 0.58);
  --topbar-search-surface: rgba(255, 255, 255, 0.34);
  --topbar-search-surface-strong: rgba(255, 255, 255, 0.56);
  position: sticky;
  top: 0;
  z-index: 100;
  color: var(--auth-topbar-text);
  background: rgba(232, 243, 253, 0.82);
  backdrop-filter: blur(12px) saturate(1.08);
  -webkit-backdrop-filter: blur(12px) saturate(1.08);
  box-shadow: 0 8px 20px rgba(76, 120, 168, 0.08);
  overflow: visible;
}

.portal-topbar::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 1px;
  background: linear-gradient(90deg, rgba(119, 160, 206, 0.12), rgba(119, 160, 206, 0.34), rgba(119, 160, 206, 0.12));
}

.portal-topbar::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    linear-gradient(135deg, rgba(235, 244, 252, 0.96) 0%, rgba(214, 229, 246, 0.92) 36%, rgba(201, 231, 246, 0.88) 100%),
    radial-gradient(circle at 16% 18%, rgba(255, 255, 255, 0.54), transparent 30%),
    radial-gradient(circle at 82% 14%, rgba(178, 234, 239, 0.32), transparent 24%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.24), rgba(255, 255, 255, 0.04));
  pointer-events: none;
  z-index: 1;
}

.portal-topbar__inner {
  position: relative;
  z-index: 2;
  min-height: var(--topbar-height);
  padding: 0 24px;
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
  gap: 6px;
  height: 34px;
  padding: 0 14px;
  border: 1px solid rgba(150, 183, 219, 0.42);
  border-radius: 8px;
  color: inherit;
  background: rgba(255, 255, 255, 0.40);
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: background-color .2s ease, border-color .2s ease, box-shadow .2s ease;
}

.portal-topbar__menu-trigger i {
  font-size: 16px;
}

.portal-topbar__menu-trigger:hover,
.portal-topbar.is-menu-open .portal-topbar__menu-trigger {
  background: rgba(255, 255, 255, 0.72);
  border-color: rgba(125, 163, 205, 0.76);
  box-shadow: 0 1px 4px rgba(37, 99, 235, 0.08);
}

.portal-topbar__brand {
  display: flex;
  align-items: center;
  padding: 4px 4px 4px 0;
  min-width: 0;
  cursor: pointer;
  border-radius: 6px;
  transition: opacity .15s ease;
}

.portal-topbar__brand:hover {
  opacity: 0.82;
}

.portal-topbar__logo {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 0;
  padding: 2px 0;
}

.portal-topbar__logo-image {
  display: block;
  object-fit: contain;
  filter: drop-shadow(0 2px 6px rgba(17, 49, 93, 0.06));
  transition: filter .2s ease;
}

.portal-topbar__brand:hover .portal-topbar__logo-image {
  filter: drop-shadow(0 2px 8px rgba(17, 49, 93, 0.12));
}

.portal-topbar__logo-image--full {
  width: 160px;
  height: 36px;
}

.portal-topbar__logo-image--compact {
  display: none;
  width: 32px;
  height: 32px;
}

.portal-topbar__search {
  position: relative;
  width: 240px;
  z-index: 120;
}

.portal-topbar__search :deep(.el-input__wrapper) {
  height: 34px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.40);
  box-shadow: none;
  border: 1px solid rgba(150, 183, 219, 0.42);
  transition: border-color .2s ease, background-color .2s ease, box-shadow .2s ease;
}

.portal-topbar__search :deep(.el-input__inner),
.portal-topbar__search :deep(.el-input__prefix) {
  color: var(--topbar-search-text);
  font-size: 13px;
}

.portal-topbar__search :deep(.el-input__inner::placeholder) {
  color: var(--topbar-search-muted);
}

.portal-topbar__search :deep(.el-input__prefix) {
  color: var(--topbar-search-muted);
}

.portal-topbar__search :deep(.el-input__wrapper.is-focus),
.portal-topbar__search :deep(.el-input__wrapper:hover) {
  background: rgba(255, 255, 255, 0.78);
  border-color: rgba(125, 163, 205, 0.68);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.06);
}

.portal-topbar__search-panel {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 100%;
  min-width: 320px;
  padding: 6px;
  border-radius: 10px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.10), 0 2px 6px rgba(0, 0, 0, 0.04);
  z-index: 160;
}

.portal-topbar__search-item {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 0;
  padding: 10px 14px;
  text-align: left;
  border: none;
  border-radius: 6px;
  color: #334155;
  background: transparent;
  cursor: pointer;
  transition: background-color .15s ease;
}

.portal-topbar__search-item:hover {
  background: #f1f5f9;
  transform: none;
}

.portal-topbar__search-item strong {
  font-size: 14px;
  line-height: 1.35;
  color: #334155;
  font-weight: 500;
}

.search-highlight {
  color: #2563eb;
}

.portal-topbar__search-empty {
  padding: 16px;
  font-size: 13px;
  color: #64748b;
  text-align: center;
}

.portal-topbar__icon-group {
  display: none;
}

.portal-topbar__icon-btn {
  display: none;
}

.portal-topbar__role-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-width: 72px;
  height: 34px;
  padding: 0 14px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.40);
  border: 1px solid rgba(150, 183, 219, 0.42);
  color: var(--auth-topbar-text);
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
  justify-content: center;
  transition: background-color .2s ease, border-color .2s ease, box-shadow .2s ease;
  outline: none;
}

.portal-topbar__role-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  line-height: 1;
  opacity: 0.7;
}

.portal-topbar__role-pill--switchable {
  cursor: pointer;
}

.portal-topbar__role-pill:hover,
.portal-topbar__role-pill--switchable:focus-visible {
  background: rgba(255, 255, 255, 0.72);
  border-color: rgba(125, 163, 205, 0.68);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.06);
}

.portal-topbar__role-pill:focus,
.portal-topbar__role-pill:focus-visible,
.portal-topbar__guest-btn:focus,
.portal-topbar__guest-btn:focus-visible,
.portal-topbar__menu-trigger:focus,
.portal-topbar__menu-trigger:focus-visible,
.portal-topbar__avatar:focus,
.portal-topbar__avatar:focus-visible {
  outline: none;
}

.portal-topbar__avatar {
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(150, 183, 219, 0.32);
  border-radius: 50%;
  color: #64748b;
  background: #e2e8f0;
  cursor: pointer;
  font-size: 17px;
  line-height: 1;
  transition: background-color .2s ease, border-color .2s ease, color .2s ease;
}

.portal-topbar__avatar:hover {
  background: #cbd5e1;
  border-color: rgba(125, 163, 205, 0.56);
  color: #334155;
}

.portal-topbar__guest-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 3px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.42);
  border: 1px solid rgba(138, 172, 220, 0.22);
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
  color: #fff;
  border: 1px solid transparent;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.22);
}

.portal-topbar__guest-btn--primary.is-active {
  border-color: transparent;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.28);
}

.portal-topbar__menu-overlay {
  position: absolute;
  left: 0;
  right: 0;
  top: 100%;
  height: calc(100vh - var(--topbar-height));
  height: calc(100dvh - var(--topbar-height));
  background: rgba(15, 23, 42, 0.32);
  backdrop-filter: blur(2px);
  -webkit-backdrop-filter: blur(2px);
  z-index: 5400;
}

.portal-topbar__menu-surface {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 70vw;
  max-width: 900px;
  background: transparent;
  color: #333;
  padding: 0;
  z-index: 5401;
}

.portal-topbar__menu-content {
  position: relative;
  z-index: 5402;
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  width: 100%;
  height: 100%;
  margin: 0;
  border-radius: 0;
  overflow: hidden;
  isolation: isolate;
  box-shadow: 14px 0 48px rgba(15, 23, 42, 0.18);
}

.portal-topbar__menu-filter {
  position: absolute;
  inset: 0;
  background: #ffffff;
  z-index: 0;
}

.menu-side-scrollbar,
.second-menu-scrollbar {
  position: relative;
  z-index: 1;
  height: 100%;
  min-height: 0;
}

.menu-side-scrollbar {
  background: #f8fafc;
  border-right: 1px solid #e2e8f0;
}

.menu-side-scrollbar :deep(.el-scrollbar__wrap),
.second-menu-scrollbar :deep(.el-scrollbar__wrap) {
  height: 100%;
}

.menu-side-scrollbar :deep(.el-scrollbar__view),
.second-menu-scrollbar :deep(.el-scrollbar__view) {
  min-height: 100%;
}

.second-menu-wrapper {
  padding: 32px 36px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px 32px;
  align-content: flex-start;
  min-width: 0;
  box-sizing: border-box;
}

.portal-topbar__menu-group {
  flex: 0 1 calc(33.333% - 22px);
  min-width: 160px;
  padding: 0 0 10px;
}

.portal-topbar__menu-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.portal-topbar__menu-group h3 {
  margin: 0 0 10px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 700;
  color: #94a3b8;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.portal-topbar__menu-link {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  width: 100%;
  padding: 9px 12px;
  margin-bottom: 1px;
  border-radius: 8px;
  text-align: left;
  border: none;
  background: transparent;
  color: #475569;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.15s ease, color 0.15s ease;
}

.portal-topbar__menu-link strong {
  font-size: 13.5px;
  line-height: 1.4;
  color: #334155;
  font-weight: 500;
  transition: color 0.15s ease;
}

.portal-topbar__menu-link span {
  display: none;
}

.portal-topbar__menu-link:hover,
.portal-topbar__menu-link.is-active {
  color: #2563eb;
  background: #eef4ff;
  border-color: transparent;
  box-shadow: none;
  transform: none;
}

.portal-topbar__menu-link:hover strong,
.portal-topbar__menu-link.is-active strong {
  color: #2563eb;
  font-weight: 600;
}

.menu-side-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  gap: 16px;
  padding: 0;
  min-height: 100%;
}

.menu-side-item {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  line-height: 2.5em;
  color: #475569;
  font-size: 14px;
  position: relative;
  text-decoration: none;
  font-weight: 600;
  min-height: 64px;
  padding: 0 22px;
  word-break: break-all;
  border-bottom: 1px solid #e2e8f0;
}

.menu-side-item.active,
.menu-side-item:hover {
  background-color: #f1f5f9;
  color: #0f172a;
}

.menu-side-item.active::after,
.menu-side-item:hover::after {
  content: '';
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background-color: #2563eb;
}

.menu-side-item .el-icon {
  font-size: 16px;
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
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.10), 0 2px 6px rgba(0, 0, 0, 0.04);
  background: #ffffff;
  overflow: hidden;
}

:global(.portal-topbar-user-dropdown .el-popper__arrow),
:global(.portal-topbar-role-dropdown .el-popper__arrow) {
  display: none;
}

:global(.portal-topbar-role-dropdown .el-dropdown-menu) {
  padding: 4px 0;
  background: transparent;
}

:global(.portal-topbar-role-dropdown .el-dropdown-menu__item) {
  min-width: 100px;
  min-height: 36px;
  justify-content: center;
  line-height: 1;
  font-size: 13px;
  font-weight: 500;
  color: #334155;
}

:global(.portal-topbar-role-dropdown .el-dropdown-menu__item:not(.is-disabled):hover) {
  background: #f1f5f9;
  color: #0f172a;
}

:global(.portal-topbar-role-dropdown .el-dropdown-menu__item.is-disabled) {
  color: #94a3b8;
  background: #f8fafc;
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu) {
  padding: 4px 0;
  min-width: 170px;
  background: #ffffff;
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item) {
  min-height: 38px;
  padding: 0 16px;
  gap: 10px;
  color: #334155;
  font-size: 13px;
  font-weight: 500;
  border-radius: 0;
  transition: background-color .15s ease;
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item i) {
  color: #64748b;
  font-size: 16px;
  width: 18px;
  text-align: center;
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item .el-icon) {
  color: #64748b;
  font-size: 14px;
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item:not(.is-disabled):hover) {
  background: #f1f5f9;
  color: #0f172a;
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item:not(.is-disabled):hover i),
:global(.portal-topbar-user-dropdown .el-dropdown-menu__item:not(.is-disabled):hover .el-icon) {
  color: #0f172a;
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item--divided) {
  margin-top: 4px;
  border-top: 1px solid #f1f5f9;
}

:global(.portal-topbar-user-dropdown .el-dropdown-menu__item--divided::before) {
  display: none;
}

.portal-topbar__dropdown-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  background: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  margin-bottom: 4px;
}

.portal-topbar__dropdown-avatar {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #e2e8f0;
  color: #64748b;
  font-size: 18px;
  line-height: 1;
}

.portal-topbar__dropdown-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.portal-topbar__dropdown-info strong {
  font-size: 14px;
  color: #0f172a;
  font-weight: 600;
  line-height: 1.2;
}

.portal-topbar__dropdown-info span {
  font-size: 12px;
  color: #64748b;
  line-height: 1.2;
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

.portal-topbar__notice-filter {
  display: inline-flex;
  gap: 8px;
  padding: 4px;
  border-radius: 999px;
  background: #f3f4f6;
  margin-bottom: 16px;
}

.portal-topbar__notice-filter-btn {
  border: none;
  background: transparent;
  color: #606266;
  border-radius: 999px;
  padding: 8px 14px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.portal-topbar__notice-filter-btn.is-active {
  background: var(--el-color-primary);
  color: #fff;
}

.portal-topbar__notice-item {
  width: 100%;
  text-align: left;
  padding: 12px 14px;
  border: 1px solid var(--portal-border);
  border-radius: 8px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  display: grid;
  grid-template-columns: 44px minmax(0, 1fr);
  gap: 12px;
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

.portal-topbar__notice-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  border: 1px solid #e5e7eb;
}

.portal-topbar__notice-icon.is-warning {
  background: #fff7ed;
  color: #f59e0b;
}

.portal-topbar__notice-icon.is-success {
  background: #f0fdf4;
  color: #22c55e;
}

.portal-topbar__notice-icon.is-primary {
  background: #eff6ff;
  color: #2563eb;
}

.portal-topbar__notice-icon.is-info {
  background: #eff6ff;
  color: #3b82f6;
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

@media (max-width: 768px) {
  :global(.portal-topbar-drawer) {
    width: 100% !important;
    max-width: 100vw !important;
  }
  :global(.portal-topbar-drawer .el-drawer__header) { padding: 12px 14px; margin-bottom: 0; padding-bottom: 10px; }
  :global(.portal-topbar-drawer .el-drawer__body) { padding: 12px 14px; overflow-y: auto; }

  /* Hide hero on mobile — saves vertical space */
  .portal-topbar__drawer-hero { display: none; }

  /* Reduce gap in body */
  .portal-topbar__drawer-body { gap: 10px; }

  /* Help section compact */
  .portal-topbar__help-section { padding: 12px; }
  .portal-topbar__help-section h3 { font-size: 14px; }
  .portal-topbar__help-item { margin-top: 8px; padding-top: 8px; }
  .portal-topbar__help-item strong { font-size: 13px; }
  .portal-topbar__help-item span { font-size: 12px; }
  .portal-topbar__help-contact { font-size: 12px; }

  /* Notice filter — full width, centered */
  .portal-topbar__notice-filter { display: flex; width: 100%; margin-bottom: 8px; }
  .portal-topbar__notice-filter-btn { flex: 1; text-align: center; padding: 8px 10px; font-size: 13px; }

  /* Notice layout: single column stack */
  .portal-topbar__notice-layout {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  /* Notice list: scrollable area */
  .portal-topbar__notice-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    max-height: none;
  }

  /* Notice items: more compact with better touch targets */
  .portal-topbar__notice-item {
    grid-template-columns: 40px minmax(0, 1fr);
    gap: 10px;
    padding: 12px;
    border-radius: 10px;
  }
  .portal-topbar__notice-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    font-size: 16px;
  }
  .portal-topbar__notice-head {
    gap: 6px;
  }
  .portal-topbar__notice-head strong { font-size: 13px; }
  .portal-topbar__notice-head span {
    min-width: auto;
    height: 20px;
    padding: 0 6px;
    font-size: 10px;
  }
  .portal-topbar__notice-item p {
    font-size: 12px;
    margin-top: 4px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  /* Detail panel: card style with clear separation */
  .portal-topbar__notice-detail {
    border-left: none;
    border-top: none;
    padding: 14px;
    border-radius: 10px;
    background: linear-gradient(180deg, #ffffff 0%, #f7faff 100%);
    border: 1px solid var(--portal-border);
  }
  .portal-topbar__notice-detail h3 { font-size: 15px; }
  .portal-topbar__notice-meta {
    font-size: 11px;
    display: flex;
    gap: 10px;
    margin-top: 6px;
    color: var(--portal-text-secondary);
  }
  .portal-topbar__notice-content {
    font-size: 13px;
    line-height: 1.7;
    margin-top: 10px;
  }
  .portal-topbar__form-footer { margin-top: 10px !important; }
  .portal-topbar__form-footer :deep(.el-button) { width: 100%; }
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
    grid-template-columns: 164px minmax(0, 1fr);
    width: calc(100vw - 10px);
  }

  .portal-topbar__notice-layout {
    grid-template-columns: 1fr;
  }

  .portal-topbar__menu-group {
    flex-basis: calc(33.333% - 16px);
  }
}

@media (max-width: 900px) {
  .portal-topbar {
    --topbar-height: 56px;
  }

  .portal-topbar__icon-group {
    display: none;
  }

  .portal-topbar__logo {
    padding: 6px 10px;
  }

  .portal-topbar__logo-image--full {
    display: none;
  }

  .portal-topbar__logo-image--compact {
    display: block;
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
    padding: 0 0 16px;
  }

  .portal-topbar__menu-content {
    grid-template-columns: 1fr;
    width: 100%;
    min-height: calc(100vh - var(--topbar-height));
    border-radius: 0;
  }

  .portal-topbar__menu-filter {
    background:
      linear-gradient(180deg, rgba(68, 92, 122, 0.78) 0%, rgba(88, 118, 151, 0.72) 22%, rgba(241, 246, 252, 0.95) 22%, rgba(241, 246, 252, 0.93) 100%);
  }

  .menu-side-wrapper {
    gap: 10px;
    padding: 16px 0 14px;
    border-right: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.16);
  }

  .second-menu-wrapper {
    padding: 18px 16px 20px;
  }

  .portal-topbar__menu-group {
    flex-basis: calc(50% - 12px);
    min-width: 150px;
  }
}

@media (max-width: 640px) {
  .portal-topbar__menu-group {
    flex-basis: 100%;
  }
}

/* ---- Mobile Responsive ---- */
.portal-topbar__hamburger {
  display: none;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  color: var(--portal-topbar-text, #24486f);
  font-size: 22px;
  cursor: pointer;
  border-radius: 6px;
  transition: background 0.15s;
  flex-shrink: 0;
}
.portal-topbar__hamburger:hover { background: rgba(0,0,0,0.06); }

@media (max-width: 768px) {
  .portal-topbar__hamburger { display: flex; }
  .portal-topbar__menu-trigger-wrapper { display: none; }
  .portal-topbar__search { display: none !important; }
  .portal-topbar__logo-image--full { display: none; }
  .portal-topbar__logo-image--compact { display: block !important; }
  .portal-topbar__role-pill span { display: none; }
  .portal-topbar__role-pill { padding: 0 8px; min-width: 36px; justify-content: center; }
  .portal-topbar__guest-actions { gap: 6px; }
  .portal-topbar__guest-btn { padding: 6px 12px; font-size: 12px; }
  .portal-topbar__inner { padding: 0 12px; }
}

/* ---- Mobile Drawer ---- */
:global(.portal-topbar-mobile-drawer) {
  z-index: 6000 !important;
}
:global(.portal-topbar-mobile-drawer .el-drawer__body) {
  padding: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.mobile-drawer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid #e8ecf1;
}
.mobile-drawer-logo { height: 28px; width: auto; }
.mobile-drawer-close {
  width: 32px; height: 32px;
  border: none; background: transparent;
  border-radius: 6px; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  font-size: 18px; color: #667b93;
}
.mobile-drawer-close:hover { background: #f1f5f9; }

.mobile-drawer-role {
  display: flex; gap: 6px;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
}
.mobile-drawer-role-btn {
  flex: 1;
  height: 34px;
  border: 1px solid #dce4ee;
  border-radius: 6px;
  background: #fff;
  color: #6a7b90;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
}
.mobile-drawer-role-btn.is-active {
  background: var(--portal-brand, #2f6bff);
  border-color: var(--portal-brand, #2f6bff);
  color: #fff;
  font-weight: 600;
}

.mobile-drawer-scroll { flex: 1; overflow: hidden; }
.mobile-drawer-group { padding: 12px 16px 4px; }
.mobile-drawer-group h4 {
  margin: 0 0 8px;
  font-size: 11px;
  font-weight: 700;
  color: #9aacbf;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}
.mobile-drawer-link {
  display: block;
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 2px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: #475569;
  font-size: 14px;
  text-align: left;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
}
.mobile-drawer-link:hover { background: #f5f8ff; color: var(--portal-brand, #2f6bff); }
.mobile-drawer-link.is-active {
  background: #ebf1ff;
  color: var(--portal-brand, #2f6bff);
  font-weight: 600;
}
</style>
