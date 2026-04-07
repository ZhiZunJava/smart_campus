<template>
  <div class="dashboard-container" :style="themeVars">
    <div class="dashboard-main-content">
      <div class="hello-wrapper">
        <div class="greetings">
          <span>{{ greetingLabel }}！</span><span>{{ userName }}</span>
        </div>
        <hr class="hello-hr" />
        <div class="hello-other-info">
          <span class="currentSemester">{{ currentSemesterLabel }}</span>，
          <span>{{ currentDate }}</span>，
          <span style="margin: 0 10px">第<span class="font-size-11 m-2">{{ currentTeachingWeek }}</span>教学周</span>
        </div>
        <div v-if="secondaryInfo.length" class="hello-other-info login-info">
          <span v-for="item in secondaryInfo" :key="item">{{ item }}</span>
        </div>

        <slot name="greeting-extra" />

        <div v-if="recentTabs.length > 0" class="recent-tabs-container">
          <div class="recent-tabs-title">最近访问：</div>
          <div class="recent-tabs-list">
            <div
              v-for="tab in recentTabs"
              :key="tab.path"
              class="recent-tab-item"
              @click="go(tab.path)"
            >
              {{ tab.title }}
            </div>
          </div>
        </div>
      </div>

      <div class="dashboard-modules-wrapper">
        <div class="dashboard-module module-progress">
          <div class="module-header">
            <div class="module-header__content">
              <h3>{{ previewModuleTitle }}</h3>
              <div class="schedule-switcher">
                <button
                  v-for="item in scheduleModes"
                  :key="item.key"
                  type="button"
                  class="schedule-switcher__btn"
                  :class="{ active: activeScheduleMode === item.key }"
                  @click="activeScheduleMode = item.key"
                >
                  {{ item.label }}
                </button>
              </div>
            </div>
            <el-button v-if="previewLinkPath" link type="primary" @click="go(previewLinkPath)">{{ previewLinkText }}</el-button>
          </div>
          <div class="module-content">
            <el-skeleton :loading="loading" animated>
              <template #template>
                <div class="skeleton-column">
                  <el-skeleton-item variant="rect" class="skeleton-card" />
                  <el-skeleton-item variant="rect" class="skeleton-card" />
                </div>
              </template>
              <template #default>
                <div class="schedule-preview-summary">
                  <div class="schedule-preview-summary__title">{{ previewSummaryTitle }}</div>
                  <div class="schedule-preview-summary__desc">{{ previewSummaryText }}</div>
                </div>
                <template v-if="previewCards.length > 0">
                  <el-carousel
                    v-if="previewCards.length > 1"
                    class="schedule-carousel"
                    :autoplay="false"
                    arrow="always"
                    indicator-position="outside"
                    height="214px"
                  >
                    <el-carousel-item v-for="card in previewCards" :key="card.key">
                      <div class="modern-progress-card modern-progress-card--featured">
                        <div class="modern-progress-top">
                          <div class="modern-progress-head">
                            <h4>{{ card.title }}</h4>
                            <el-tag v-if="card.tag" size="small" effect="plain">{{ card.tag }}</el-tag>
                          </div>
                          <div v-if="card.badges?.length" class="modern-progress-badges">
                            <span
                              v-for="(badge, index) in card.badges"
                              :key="`${card.key}-badge-${badge}`"
                              class="modern-progress-pill"
                              :class="{ 'modern-progress-pill--primary': index === 0 }"
                            >
                              {{ badge }}
                            </span>
                          </div>
                        </div>
                        <div v-if="card.facts?.length" class="modern-progress-facts">
                          <div v-for="fact in card.facts" :key="`${card.key}-${fact.label}`" class="modern-progress-fact">
                            <span class="modern-progress-fact__label">{{ fact.label }}</span>
                            <strong class="modern-progress-fact__value">{{ fact.value }}</strong>
                          </div>
                        </div>
                      </div>
                    </el-carousel-item>
                  </el-carousel>
                  <div v-else class="schedule-single">
                    <div v-for="card in previewCards" :key="card.key" class="modern-progress-card modern-progress-card--featured">
                      <div class="modern-progress-top">
                        <div class="modern-progress-head">
                          <h4>{{ card.title }}</h4>
                          <el-tag v-if="card.tag" size="small" effect="plain">{{ card.tag }}</el-tag>
                        </div>
                        <div v-if="card.badges?.length" class="modern-progress-badges">
                          <span
                            v-for="(badge, index) in card.badges"
                            :key="`${card.key}-single-badge-${badge}`"
                            class="modern-progress-pill"
                            :class="{ 'modern-progress-pill--primary': index === 0 }"
                          >
                            {{ badge }}
                          </span>
                        </div>
                      </div>
                      <div v-if="card.facts?.length" class="modern-progress-facts">
                        <div v-for="fact in card.facts" :key="`${card.key}-single-${fact.label}`" class="modern-progress-fact">
                          <span class="modern-progress-fact__label">{{ fact.label }}</span>
                          <strong class="modern-progress-fact__value">{{ fact.value }}</strong>
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
                <template v-else>
                  <el-empty :description="previewEmptyDescription" :image-size="100" />
                </template>
              </template>
            </el-skeleton>
          </div>
        </div>

        <div class="dashboard-module module-tasks">
          <div class="module-header">
            <h3>{{ taskModuleTitle }}</h3>
            <el-button v-if="taskLinkPath" link type="primary" @click="go(taskLinkPath)">{{ taskLinkText }}</el-button>
          </div>
          <div class="module-content task-list">
            <el-skeleton :loading="loading" animated>
              <template #template>
                <div class="skeleton-column">
                  <el-skeleton-item variant="rect" class="skeleton-card" />
                  <el-skeleton-item variant="rect" class="skeleton-card" />
                </div>
              </template>
              <template #default>
                <div v-if="taskOverviewBadges.length" class="task-overview-badges">
                  <span v-for="badge in taskOverviewBadges" :key="badge" class="task-overview-badge">{{ badge }}</span>
                </div>
                <template v-if="taskCards.length">
                  <el-scrollbar max-height="392px" class="task-scrollbar">
                    <div class="task-scroll-content">
                      <div
                        v-for="task in taskCards"
                        :key="task.key"
                        class="modern-task-card"
                      >
                        <div class="modern-task-icon" :class="task.iconClass">
                          <i :class="task.icon"></i>
                        </div>
                        <div class="modern-task-details">
                          <div class="modern-task-title">
                            <h4>{{ task.title }}</h4>
                            <el-tag size="small" :type="task.tagType" effect="plain">{{ task.tag }}</el-tag>
                          </div>
                          <span class="modern-task-status" :class="task.metaClass">{{ task.statusText }}</span>
                          <div v-if="task.meta?.length" class="modern-task-meta">
                            <span v-for="meta in task.meta.slice(0, 2)" :key="meta">{{ meta }}</span>
                          </div>
                          <div v-if="task.recommendationReason" class="modern-task-reason">{{ task.recommendationReason }}</div>
                        </div>
                        <div class="modern-task-actions">
                          <el-button plain type="primary" size="small" @click="emit('task-click', task.raw)">
                            {{ task.actionLabel }}
                          </el-button>
                        </div>
                      </div>
                    </div>
                  </el-scrollbar>
                </template>
                <el-empty v-else description="当前没有待处理任务" :image-size="100" />
              </template>
            </el-skeleton>
          </div>
        </div>
      </div>

      <div v-if="$slots['module-extra']" class="dashboard-extra-wrapper">
        <slot name="module-extra" />
      </div>
    </div>

    <div class="short-wrapper">
      <div class="short-header">
        <span>快捷入口</span>
        <el-icon class="cursor-pointer" @click="openShortcutSettings"><Setting /></el-icon>
      </div>
      <div class="shortcut-panel">
        <div
          v-for="item in displayShortcutItems"
          :key="item.path"
          class="shortcut-item"
          @click="go(item.path)"
        >
          <div class="shortcut-item__icon" :style="{ backgroundImage: item.bg }">
            <i :class="item.icon"></i>
          </div>
          <div class="shortcut-item__title">{{ item.title }}</div>
        </div>
      </div>
      <el-empty
        v-if="!displayShortcutItems.length"
        description="当前端暂无可配置快捷入口"
        :image-size="90"
      />
    </div>

    <div class="suspension-wrapper">
      <div class="suspension-list">
        <div class="suspension-item todo" @click="openQuickDrawer('todo')">
          <div class="item">
            <div class="icon-wrapper">
              <el-icon><i class="ri-mac-line"></i></el-icon>
              <span v-if="drawerTodoList.length" class="icon-badge">{{ drawerTodoList.length > 99 ? '99+' : drawerTodoList.length }}</span>
            </div>
            <div class="icon-text">我的待办</div>
          </div>
        </div>
        <div class="suspension-item notification" @click="openQuickDrawer('message')">
          <div class="item">
            <div class="icon-wrapper">
              <el-icon><i class="ri-message-3-line"></i></el-icon>
              <span v-if="drawerMessageList.length" class="icon-badge">{{ drawerMessageList.length > 99 ? '99+' : drawerMessageList.length }}</span>
            </div>
            <div class="icon-text">未读消息</div>
          </div>
        </div>
        <div class="suspension-item notice" @click="openQuickDrawer('notice')">
          <div class="item">
            <div class="icon-wrapper">
              <el-icon><i class="ri-notification-3-line"></i></el-icon>
              <span v-if="drawerNoticeList.length" class="icon-badge">{{ drawerNoticeList.length > 99 ? '99+' : drawerNoticeList.length }}</span>
            </div>
            <div class="icon-text">通知公告</div>
          </div>
        </div>
      </div>
    </div>

    <el-drawer v-model="quickDrawerVisible" direction="rtl" :size="windowWidth <= 640 ? '100%' : '460px'" class="dashboard-quick-drawer" append-to-body>
      <template #header>
        <div class="dashboard-quick-drawer__header">
          <div class="dashboard-quick-drawer__title">{{ drawerTitle }}（{{ drawerCount }}）</div>
          <el-button type="primary" link @click="emit('message-center', activeDrawerTab)">查看全部</el-button>
        </div>
      </template>
      <div class="dashboard-quick-drawer__body" v-loading="drawerLoading">
        <template v-if="activeDrawerTab === 'todo'">
          <div v-if="drawerTodoList.length" class="dashboard-quick-list">
            <article v-for="item in drawerTodoList" :key="item.key || item.title" class="dashboard-quick-item" @click="emit('drawer-task-click', item)">
              <div class="dashboard-quick-item__icon drawer-icon--todo">
                <el-icon><i class="ri-mac-line"></i></el-icon>
              </div>
              <div class="dashboard-quick-item__main">
                <div class="dashboard-quick-item__head">
                  <h4 class="dashboard-quick-item__title" :title="item.title">{{ item.title }}</h4>
                  <span v-if="item.createTime" class="dashboard-quick-item__time">{{ String(item.createTime).substring(0, 10) }}</span>
                </div>
                <p class="dashboard-quick-item__desc" :title="item.desc || item.status || '待处理事项'">{{ item.desc || item.status || '待处理事项' }}</p>
              </div>
              <div class="dashboard-quick-item__action">
                <el-button type="primary" round plain size="small">处理</el-button>
              </div>
            </article>
          </div>
          <el-empty v-else description="暂无数据" :image-size="100" />
        </template>
        <template v-else-if="activeDrawerTab === 'message'">
          <div v-if="drawerMessageList.length" class="dashboard-quick-list">
            <article v-for="item in drawerMessageList" :key="item.messageId || item.noticeId" class="dashboard-quick-item" @click="emit('drawer-message-click', { tab: 'message', item })">
              <div class="dashboard-quick-item__icon drawer-icon--message">
                <el-icon><i class="ri-message-3-line"></i></el-icon>
              </div>
              <div class="dashboard-quick-item__main">
                <div class="dashboard-quick-item__head">
                  <h4 class="dashboard-quick-item__title" :title="item.messageTitle">{{ item.messageTitle }}</h4>
                  <span v-if="item.createTime" class="dashboard-quick-item__time">{{ String(item.createTime).substring(0, 10) }}</span>
                </div>
                <p class="dashboard-quick-item__desc" :title="item.messageSummary || item.messageContent">{{ item.messageSummary || item.messageContent || '暂无内容摘要' }}</p>
              </div>
              <div class="dashboard-quick-item__action">
                <el-button type="primary" round plain size="small">查看</el-button>
              </div>
            </article>
          </div>
          <el-empty v-else description="暂无数据" :image-size="100" />
        </template>
        <template v-else>
          <div v-if="drawerNoticeList.length" class="dashboard-quick-list">
            <article v-for="item in drawerNoticeList" :key="item.messageId || item.noticeId" class="dashboard-quick-item" @click="emit('drawer-message-click', { tab: 'notice', item })">
              <div class="dashboard-quick-item__icon drawer-icon--notice">
                <el-icon><i class="ri-notification-3-line"></i></el-icon>
              </div>
              <div class="dashboard-quick-item__main">
                <div class="dashboard-quick-item__head">
                  <h4 class="dashboard-quick-item__title" :title="item.messageTitle">{{ item.messageTitle }}</h4>
                  <span v-if="item.createTime" class="dashboard-quick-item__time">{{ String(item.createTime).substring(0, 10) }}</span>
                </div>
                <p class="dashboard-quick-item__desc" :title="item.messageSummary || item.messageContent">{{ item.messageSummary || item.messageContent || '暂无内容摘要' }}</p>
              </div>
              <div class="dashboard-quick-item__action">
                <el-button type="primary" round plain size="small">查看</el-button>
              </div>
            </article>
          </div>
          <el-empty v-else description="暂无数据" :image-size="100" />
        </template>
      </div>
    </el-drawer>

    <el-dialog
      v-model="shortcutDialogVisible"
      title="快捷入口设置"
      width="min(760px, 92vw)"
      top="6vh"
      class="shortcut-config"
      destroy-on-close
      append-to-body
      modal-append-to-body
      modal-class="shortcut-config-modal"
    >
      <div class="shortcut-config__tip">
        当前端可访问页面共 {{ availableShortcutItems.length }} 个，最多可设置 9 个首页快捷入口。
      </div>
      <div class="shortcut-config__selected-panel">
        <div class="shortcut-config__panel-head">
          <strong>已选快捷入口</strong>
          <span>{{ editingShortcutPaths.length }} / 9</span>
        </div>
        <div v-if="selectedShortcutItems.length" class="shortcut-config__drag-hint">拖拽可调整顺序，点击 × 移除</div>
        <div v-if="selectedShortcutItems.length" class="shortcut-config__selected-list">
          <button
            v-for="(item, idx) in selectedShortcutItems"
            :key="`selected-${item.path}`"
            type="button"
            class="shortcut-config__selected-chip"
            :class="{
              'is-dragging': chipDragIndex === idx,
              'is-drag-over': chipDragOverIndex === idx
            }"
            draggable="true"
            @dragstart="onChipDragStart(idx)"
            @dragover="onChipDragOver($event, idx)"
            @drop="onChipDrop(idx)"
            @dragend="onChipDragEnd"
          >
            <span class="shortcut-config__selected-order">{{ idx + 1 }}</span>
            <span class="shortcut-config__selected-label">{{ item.title }}</span>
            <span class="shortcut-config__selected-arrows">
              <i class="ri-arrow-left-s-line" :class="{ disabled: idx === 0 }" @click.stop="moveShortcutUp(idx)"></i>
              <i class="ri-arrow-right-s-line" :class="{ disabled: idx === selectedShortcutItems.length - 1 }" @click.stop="moveShortcutDown(idx)"></i>
            </span>
            <span class="shortcut-config__selected-close" @click.stop="removeShortcutSelection(item.path)">×</span>
          </button>
        </div>
        <div v-else class="shortcut-config__empty">
          还没有选择首页快捷入口，点击下方卡片即可加入。
        </div>
      </div>

      <el-scrollbar class="shortcut-config__scroll" max-height="520px" always>
        <div class="shortcut-config__scroll-view">
          <div class="shortcut-config__grid">
            <button
              v-for="item in shortcutDialogItems"
              :key="`config-${item.path}`"
              type="button"
              class="shortcut-config__card"
              :class="{ 'is-checked': isShortcutSelected(item.path) }"
              @click="toggleShortcutSelection(item.path)"
            >
              <div class="shortcut-config__card-main">
                <div class="shortcut-item__icon shortcut-item__icon--small" :style="{ backgroundImage: item.bg }">
                  <i :class="item.icon"></i>
                </div>
                <div class="shortcut-config__text">
                  <strong>{{ item.title }}</strong>
                  <span>{{ isShortcutSelected(item.path) ? '已加入首页快捷入口' : '点击加入首页快捷入口' }}</span>
                </div>
              </div>
              <div class="shortcut-config__card-status">
                <el-icon v-if="isShortcutSelected(item.path)" class="shortcut-config__card-check"><i class="ri-checkbox-circle-fill"></i></el-icon>
                <el-icon v-else class="shortcut-config__card-add"><i class="ri-add-circle-line"></i></el-icon>
              </div>
            </button>
          </div>
        </div>
      </el-scrollbar>
      <template #footer>
        <div class="shortcut-config__footer">
          <div class="shortcut-config__count">
            已选择 <span>{{ editingShortcutPaths.length }}</span> / 9
          </div>
          <div class="shortcut-config__actions">
            <el-button @click="shortcutDialogVisible = false">取消</el-button>
            <el-button @click="restoreDefaultShortcuts">恢复默认</el-button>
            <el-button type="primary" @click="saveShortcutSettings">保存设置</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useTabsStore } from '@/store/tabs'
import { resolveDefaultShortcutPaths, resolveShortcutVisual } from '@/utils/portalDashboard'
import type { DashboardDrawerTab, DashboardPreviewCard, DashboardTaskCard } from '@/utils/portalDashboard'

interface ScheduleModeItem {
  key: string
  label: string
}

interface ShortcutVisualItem {
  path: string
  title: string
  icon: string
  bg: string
}

interface ShortcutItemOverride {
  path: string
  title: string
  icon?: string
  bg?: string
}

const props = withDefaults(defineProps<{
  role: string
  themeColor: string
  userName: string
  currentSemesterLabel: string
  currentTeachingWeek: string | number
  secondaryInfo?: string[]
  loading?: boolean
  previewModuleTitle?: string
  previewLinkText?: string
  previewLinkPath?: string
  scheduleModes: ScheduleModeItem[]
  scheduleMode: string
  previewSummaryTitle: string
  previewSummaryText: string
  previewCards: DashboardPreviewCard[]
  previewEmptyDescription: string
  taskModuleTitle?: string
  taskLinkText?: string
  taskLinkPath?: string
  taskOverviewBadges?: string[]
  taskCards?: DashboardTaskCard[]
  drawerTodoList?: any[]
  drawerMessageList?: any[]
  drawerNoticeList?: any[]
  drawerLoading?: boolean
  shortcutItemsOverride?: ShortcutItemOverride[]
}>(), {
  secondaryInfo: () => [],
  loading: false,
  previewModuleTitle: '课程预览',
  previewLinkText: '查看详情',
  previewLinkPath: '',
  taskModuleTitle: '近期任务',
  taskLinkText: '任务中心',
  taskLinkPath: '',
  taskOverviewBadges: () => [],
  taskCards: () => [],
  drawerTodoList: () => [],
  drawerMessageList: () => [],
  drawerNoticeList: () => [],
  drawerLoading: false,
  shortcutItemsOverride: () => [],
})

const emit = defineEmits<{
  (e: 'update:scheduleMode', value: string): void
  (e: 'task-click', task: any): void
  (e: 'open-drawer', tab: DashboardDrawerTab): void
  (e: 'drawer-task-click', item: any): void
  (e: 'drawer-message-click', payload: { tab: 'message' | 'notice'; item: any }): void
  (e: 'message-center', tab: DashboardDrawerTab): void
}>()

const router = useRouter()
const tabsStore = useTabsStore()

const shortcutDialogVisible = ref(false)
const storedShortcutPaths = ref<string[]>([])
const editingShortcutPaths = ref<string[]>([])
const quickDrawerVisible = ref(false)
const activeDrawerTab = ref<DashboardDrawerTab>('todo')
const windowWidth = ref(typeof window !== 'undefined' ? window.innerWidth : 1024)

function handleResize() { windowWidth.value = window.innerWidth }
onMounted(() => window.addEventListener('resize', handleResize))
onBeforeUnmount(() => window.removeEventListener('resize', handleResize))

const activeScheduleMode = computed({
  get: () => props.scheduleMode,
  set: (value: string) => emit('update:scheduleMode', value),
})

const greetingLabel = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 12) return '早上好'
  if (hour < 14) return '中午好'
  if (hour < 19) return '下午好'
  return '晚上好'
})

const currentDate = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = `${now.getMonth() + 1}`.padStart(2, '0')
  const day = `${now.getDate()}`.padStart(2, '0')
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return `${year}年${month}月${day}日 ${weekDays[now.getDay()]}`
})

const recentTabs = computed(() => {
  const dashboardPath = `/${props.role}/dashboard`
  return tabsStore.visitedTabs.filter((tab) => tab.path !== dashboardPath).slice(-5)
})

const drawerTitle = computed(() => {
  if (activeDrawerTab.value === 'message') return '未读消息'
  if (activeDrawerTab.value === 'notice') return '通知公告'
  return '我的待办'
})

const drawerCount = computed(() => {
  if (activeDrawerTab.value === 'message') return props.drawerMessageList.length
  if (activeDrawerTab.value === 'notice') return props.drawerNoticeList.length
  return props.drawerTodoList.length
})

const availableShortcutItems = computed(() => {
  if (props.shortcutItemsOverride.length) {
    return props.shortcutItemsOverride.map((item) => ({
      path: item.path,
      title: item.title,
      icon: item.icon || resolveShortcutVisual(item.path, item.title).icon,
      bg: item.bg || resolveShortcutVisual(item.path, item.title).bg,
    }))
  }
  const prefix = `/${props.role}/`
  return router.getRoutes()
    .filter((item) => {
      if (!item.path.startsWith(prefix)) return false
      if (item.path.includes('/:')) return false
      if (item.meta?.workspace) return false
      if (item.meta?.hideShortcut) return false
      const segment = item.path.slice(prefix.length)
      if (!segment || segment.includes('/')) return false
      return Boolean(item.meta?.title)
    })
    .sort((left, right) => {
      if (left.path.endsWith('/dashboard')) return -1
      if (right.path.endsWith('/dashboard')) return 1
      return left.path.localeCompare(right.path)
    })
    .map((item) => ({
      path: item.path,
      title: String(item.meta?.title || item.name || item.path),
      ...resolveShortcutVisual(item.path, String(item.meta?.title || '')),
    }))
})

const defaultShortcutPaths = computed(() => resolveDefaultShortcutPaths(props.role, availableShortcutItems.value))

const displayShortcutItems = computed(() => {
  const selected = storedShortcutPaths.value.length
    ? storedShortcutPaths.value
    : defaultShortcutPaths.value
  const itemMap = new Map(availableShortcutItems.value.map((item) => [item.path, item]))
  return selected
    .map((path) => itemMap.get(path))
    .filter(Boolean)
    .slice(0, 9) as ShortcutVisualItem[]
})

const selectedShortcutItems = computed(() => {
  const itemMap = new Map(availableShortcutItems.value.map((item) => [item.path, item]))
  return editingShortcutPaths.value
    .map((path) => itemMap.get(path))
    .filter(Boolean) as ShortcutVisualItem[]
})

const shortcutDialogItems = computed(() => {
  const selectedSet = new Set(editingShortcutPaths.value)
  return [...availableShortcutItems.value].sort((left, right) => {
    const leftSelected = selectedSet.has(left.path)
    const rightSelected = selectedSet.has(right.path)
    if (leftSelected !== rightSelected) return leftSelected ? -1 : 1
    return left.title.localeCompare(right.title, 'zh-Hans-CN')
  })
})

const themeVars = computed(() => ({
  '--dashboard-primary': props.themeColor,
  '--dashboard-primary-soft': hexToRgba(props.themeColor, 0.12),
  '--dashboard-primary-soft-strong': hexToRgba(props.themeColor, 0.2),
  '--dashboard-primary-border': hexToRgba(props.themeColor, 0.24),
  '--dashboard-primary-shadow': hexToRgba(props.themeColor, 0.14),
  '--dashboard-primary-glow': hexToRgba(props.themeColor, 0.08),
  '--dashboard-primary-text': props.themeColor,
}))

watch(
  availableShortcutItems,
  () => {
    restoreShortcutSelection()
  },
  { immediate: true },
)

function go(path: string) {
  router.push(path)
}

function openQuickDrawer(tab: DashboardDrawerTab) {
  activeDrawerTab.value = tab
  quickDrawerVisible.value = true
  emit('open-drawer', tab)
}

function shortcutStorageKey() {
  return `portal-shortcuts:${props.role}${props.shortcutItemsOverride.length ? ':custom' : ''}`
}

function restoreShortcutSelection() {
  const availablePaths = new Set(availableShortcutItems.value.map((item) => item.path))
  const raw = localStorage.getItem(shortcutStorageKey())
  if (!raw) {
    storedShortcutPaths.value = [...defaultShortcutPaths.value]
    return
  }
  try {
    const parsed = JSON.parse(raw)
    if (!Array.isArray(parsed)) {
      throw new Error('invalid shortcuts')
    }
    const safePaths = parsed.filter((path) => availablePaths.has(path)).slice(0, 9)
    storedShortcutPaths.value = safePaths.length ? safePaths : [...defaultShortcutPaths.value]
  } catch {
    storedShortcutPaths.value = [...defaultShortcutPaths.value]
  }
}

function openShortcutSettings() {
  editingShortcutPaths.value = [...displayShortcutItems.value.map((item) => item.path)]
  shortcutDialogVisible.value = true
}

function isShortcutSelected(path: string) {
  return editingShortcutPaths.value.includes(path)
}

function toggleShortcutSelection(path: string) {
  if (isShortcutSelected(path)) {
    editingShortcutPaths.value = editingShortcutPaths.value.filter((item) => item !== path)
    return
  }
  if (editingShortcutPaths.value.length >= 9) {
    ElMessage.warning('首页快捷入口最多选择 9 个')
    return
  }
  editingShortcutPaths.value = [...editingShortcutPaths.value, path]
}

function removeShortcutSelection(path: string) {
  editingShortcutPaths.value = editingShortcutPaths.value.filter((item) => item !== path)
}

const chipDragIndex = ref<number | null>(null)
const chipDragOverIndex = ref<number | null>(null)

function onChipDragStart(index: number) {
  chipDragIndex.value = index
}

function onChipDragOver(event: DragEvent, index: number) {
  event.preventDefault()
  chipDragOverIndex.value = index
}

function onChipDrop(index: number) {
  if (chipDragIndex.value === null || chipDragIndex.value === index) {
    chipDragIndex.value = null
    chipDragOverIndex.value = null
    return
  }
  const paths = [...editingShortcutPaths.value]
  const [moved] = paths.splice(chipDragIndex.value, 1)
  paths.splice(index, 0, moved)
  editingShortcutPaths.value = paths
  chipDragIndex.value = null
  chipDragOverIndex.value = null
}

function onChipDragEnd() {
  chipDragIndex.value = null
  chipDragOverIndex.value = null
}

function moveShortcutUp(index: number) {
  if (index <= 0) return
  const paths = [...editingShortcutPaths.value]
  ;[paths[index - 1], paths[index]] = [paths[index], paths[index - 1]]
  editingShortcutPaths.value = paths
}

function moveShortcutDown(index: number) {
  if (index >= editingShortcutPaths.value.length - 1) return
  const paths = [...editingShortcutPaths.value]
  ;[paths[index], paths[index + 1]] = [paths[index + 1], paths[index]]
  editingShortcutPaths.value = paths
}

function restoreDefaultShortcuts() {
  editingShortcutPaths.value = [...defaultShortcutPaths.value]
}

function saveShortcutSettings() {
  const safePaths = editingShortcutPaths.value.slice(0, 9)
  storedShortcutPaths.value = safePaths
  localStorage.setItem(shortcutStorageKey(), JSON.stringify(safePaths))
  shortcutDialogVisible.value = false
  ElMessage.success('快捷入口已更新')
}

function hexToRgba(hex: string, alpha: number) {
  const normalized = hex.replace('#', '')
  const bigint = Number.parseInt(normalized.length === 3 ? normalized.split('').map((item) => item + item).join('') : normalized, 16)
  const r = (bigint >> 16) & 255
  const g = (bigint >> 8) & 255
  const b = bigint & 255
  return `rgba(${r}, ${g}, ${b}, ${alpha})`
}
</script>

<style scoped>
.dashboard-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  flex: 1;
  padding: 0 4%;
  box-sizing: border-box;
  gap: 28px;
  min-height: 100%;
}

.dashboard-main-content {
  flex: 0 1 58%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.hello-wrapper {
  margin-left: 1%;
  margin-bottom: 40px;
  display: flex;
  flex-flow: column nowrap;
  color: #333333;
  text-shadow: none;
  box-sizing: content-box;
}

.greetings {
  margin-top: 20px;
  font-size: 36px;
  font-weight: 300;
  letter-spacing: 2px;
}

.hello-hr {
  width: 100%;
  margin-top: 15px;
  margin-bottom: 15px;
  border: 0;
  border-top: 1px solid rgba(48, 49, 51, 0.15);
  height: 0;
}

.hello-other-info {
  font-size: 18px;
  margin: 0 0 8px;
  line-height: 28px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  color: #333333;
  font-weight: 400;
}

.login-info {
  font-size: 12px;
  gap: 20px;
  margin-top: 10px;
  color: #666666;
  flex-wrap: wrap;
}

.currentSemester {
  color: var(--dashboard-primary-text);
  font-weight: 700;
}

.m-2 {
  margin: 0 5px;
}

.font-size-11 {
  font-size: 20px;
  font-weight: 600;
  color: var(--dashboard-primary-text);
}

.recent-tabs-container {
  margin-top: 30px;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.recent-tabs-title {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.recent-tabs-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.recent-tab-item {
  padding: 6px 16px;
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(48, 49, 51, 0.15);
  color: #303133;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.02);
}

.recent-tab-item:hover {
  background-color: #ffffff;
  border-color: var(--dashboard-primary-border);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px var(--dashboard-primary-glow);
}

.dashboard-modules-wrapper {
  display: flex;
  gap: 20px;
  margin-left: 1%;
}

.dashboard-extra-wrapper {
  margin-top: 20px;
  margin-left: 1%;
}

.dashboard-module {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 280px;
  border-radius: 12px;
  background-color: rgba(255, 255, 255, 0.9);
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.module-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
}

.module-header__content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.module-header h3 {
  margin: 0;
  font-size: 15px;
  color: #333333;
  font-weight: 600;
  text-shadow: none;
}

.module-content {
  flex: 1;
}

.skeleton-column {
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: 100%;
}

.skeleton-card {
  height: 80px;
  border-radius: 8px;
}

.schedule-switcher {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.schedule-switcher__btn {
  border: 1px solid rgba(203, 213, 225, 0.9);
  background: #f8fbff;
  color: #5c6b7f;
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 11.5px;
  line-height: 1;
  cursor: pointer;
  transition: all 0.2s ease;
}

.schedule-switcher__btn:hover {
  border-color: var(--dashboard-primary-border);
  color: var(--dashboard-primary-text);
}

.schedule-switcher__btn.active {
  background: linear-gradient(135deg, var(--dashboard-primary-soft), rgba(255, 255, 255, 0.92));
  border-color: var(--dashboard-primary-border);
  color: var(--dashboard-primary-text);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.7);
}

.schedule-preview-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
  padding: 12px 14px;
  border-radius: 10px;
  background: linear-gradient(135deg, rgba(248, 251, 255, 0.94), var(--dashboard-primary-soft));
  border: 1px solid rgba(237, 242, 255, 0.9);
}

.schedule-preview-summary__title {
  font-size: 13px;
  font-weight: 600;
  color: #2f3e52;
}

.schedule-preview-summary__desc {
  font-size: 11.5px;
  color: #6b7b8f;
  text-align: right;
}

.schedule-carousel {
  padding: 0 14px 6px;
  overflow: visible !important;
}

.schedule-carousel :deep(.el-carousel__container) {
  overflow: hidden;
  padding: 8px 0 12px;
  box-sizing: border-box;
}

.schedule-carousel :deep(.el-carousel__item) {
  display: flex;
  align-items: stretch;
  padding: 0 8px;
  box-sizing: border-box;
  overflow: visible;
}

.schedule-carousel :deep(.el-carousel__indicators--outside) {
  margin-top: 8px;
}

.schedule-carousel :deep(.el-carousel__arrow) {
  width: 30px;
  height: 30px;
  background: rgba(255, 255, 255, 0.96);
  color: var(--dashboard-primary-text);
  box-shadow: 0 6px 14px var(--dashboard-primary-shadow);
  top: calc(50% - 12px);
}

.schedule-carousel :deep(.el-carousel__arrow--left) {
  left: -14px;
}

.schedule-carousel :deep(.el-carousel__arrow--right) {
  right: -14px;
}

.schedule-carousel :deep(.el-carousel__arrow:hover) {
  background: #ffffff;
}

.schedule-carousel :deep(.el-carousel__button) {
  width: 18px;
  height: 4px;
  border-radius: 999px;
  background: #cbd5e1;
}

.schedule-carousel :deep(.is-active .el-carousel__button) {
  background: var(--dashboard-primary-text);
}

.schedule-single {
  padding: 0 4px;
}

.modern-progress-card {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 14px;
  padding: 16px 18px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  margin-bottom: 12px;
  border: 1px solid #f0f2f5;
  transition: all 0.2s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.02);
}

.modern-progress-card:hover {
  border-color: var(--dashboard-primary-border);
  box-shadow: 0 4px 16px var(--dashboard-primary-glow);
  transform: translateY(-1px);
}

.modern-progress-card:last-child {
  margin-bottom: 0;
}

.modern-progress-card--featured {
  width: 100%;
  min-height: 156px;
  height: 100%;
  margin-bottom: 0;
}

.modern-progress-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.modern-progress-head {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  min-width: 0;
}

.modern-progress-head h4 {
  margin: 0;
  font-size: 14px;
  color: #333333;
  font-weight: 600;
}

.modern-progress-badges {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 8px;
}

.modern-progress-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  background: #f8fafc;
  color: #64748b;
  font-size: 11.5px;
  line-height: 1;
  font-weight: 700;
  box-sizing: border-box;
}

.modern-progress-pill--primary {
  background: linear-gradient(135deg, var(--dashboard-primary-soft), rgba(255, 255, 255, 0.95));
  color: var(--dashboard-primary-text);
  font-size: 11.5px;
  font-weight: 800;
}

.modern-progress-facts {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.modern-progress-fact {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 10px;
  background: #f8fafc;
}

.modern-progress-fact__label {
  flex: 0 0 auto;
  font-size: 11px;
  color: #94a3b8;
  font-weight: 700;
}

.modern-progress-fact__value {
  min-width: 0;
  font-size: 12px;
  color: #475569;
  line-height: 1.55;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-height: 0;
}

.task-overview-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.task-overview-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: var(--dashboard-primary-soft);
  color: var(--dashboard-primary-text);
  font-size: 11px;
  line-height: 1.4;
}

.task-scrollbar {
  width: 100%;
}

.task-scroll-content {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding-right: 6px;
}

.modern-task-card {
  display: flex;
  align-items: flex-start;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  border: 1px solid #f0f2f5;
  transition: all 0.2s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.02);
}

.modern-task-card:hover {
  border-color: var(--dashboard-primary-border);
  box-shadow: 0 4px 16px var(--dashboard-primary-glow);
  transform: translateY(-1px);
}

.modern-task-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 16px;
  color: #fff;
  flex-shrink: 0;
}

.task-icon--exam {
  background: linear-gradient(135deg, #f6aeb4, #fe7271);
}

.task-icon--homework {
  background: linear-gradient(135deg, #a5d6fd, #54b0fb);
}

.task-icon--practice {
  background: linear-gradient(135deg, #97e4d6, #4dc7ae);
}

.task-icon--default {
  background: linear-gradient(135deg, #c4ccd9, #8b98ac);
}

.modern-task-details {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.modern-task-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.modern-task-title h4 {
  margin: 0;
  font-size: 13px;
  color: #303133;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.modern-task-status {
  font-size: 12px;
  font-weight: 500;
}

.modern-task-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.modern-task-reason {
  font-size: 11.5px;
  line-height: 1.6;
  color: #5c6b7f;
}

.modern-task-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-left: 16px;
  flex-shrink: 0;
  align-self: center;
}

.modern-task-actions .el-button {
  margin-left: 0;
  width: 80px;
}

.text-danger {
  color: #f56c6c;
}

.text-warning {
  color: #e6a23c;
}

.text-success {
  color: #67c23a;
}

.text-muted {
  color: #909399;
}

.short-wrapper {
  margin-right: 20px;
  flex: 0 0 380px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  border: none;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  padding: 24px;
}

.short-header {
  display: flex;
  justify-content: space-between;
  color: #303133;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: 600;
  text-shadow: none;
}

.shortcut-panel {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px 20px;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: transparent;
  border: none;
  border-radius: 16px;
  width: 100%;
  padding: 16px 8px;
  text-decoration: none;
  transition: all 0.2s ease-out;
  cursor: pointer;
  box-shadow: none;
}

.shortcut-item:hover {
  background-color: rgba(248, 250, 252, 0.9);
  transform: translateY(-2px);
}

.shortcut-item__icon {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 12px;
  font-size: 24px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.2s ease;
}

.shortcut-item:hover .shortcut-item__icon {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.shortcut-item__icon--small {
  width: 40px;
  height: 40px;
  margin-bottom: 0;
  font-size: 20px;
}

.shortcut-item__title {
  color: #606266;
  font-size: 13px;
  text-align: center;
}

.suspension-wrapper {
  position: fixed;
  right: 0.8vw;
  top: 50%;
  transform: translateY(-50%);
  z-index: 100;
}

.suspension-list {
  display: flex;
  flex-direction: column;
  padding: 16px 8px;
  border-radius: 40px;
  background: #ffffff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  border: 1px solid #e4e7ed;
  gap: 16px;
}

.suspension-item {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: transform 0.2s;
}

.suspension-item:hover {
  transform: scale(1.05);
}

.suspension-item .item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.icon-wrapper {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 22px;
  color: var(--dashboard-primary-text);
  transition: all 0.3s;
  position: relative;
}

.suspension-item:hover .icon-wrapper {
  background: var(--dashboard-primary-soft);
  color: var(--dashboard-primary-text);
}

.icon-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 999px;
  background: #ef4444;
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  border: 2px solid #fff;
  box-shadow: 0 4px 10px rgba(239, 68, 68, 0.25);
}

:deep(.dashboard-quick-drawer .el-drawer__header) {
  margin-bottom: 0;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--portal-border);
}

:deep(.dashboard-quick-drawer .el-drawer__body) {
  padding-top: 12px;
}

.dashboard-quick-drawer__header {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.dashboard-quick-drawer__title {
  font-size: 18px;
  font-weight: 600;
  color: var(--portal-text);
}

.dashboard-quick-drawer__body {
  min-height: 320px;
}

.dashboard-quick-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 4px;
}

.dashboard-quick-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  border-radius: 12px;
  background-color: #ffffff;
  border: 1px solid #ebeef5;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
  position: relative;
  overflow: hidden;
}

.dashboard-quick-item:hover {
  border-color: var(--dashboard-primary-border);
  box-shadow: 0 8px 24px var(--dashboard-primary-glow);
  transform: translateY(-2px);
  background-color: #ffffff;
}

.dashboard-quick-item__main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
}

.dashboard-quick-item__icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
  transition: transform 0.3s;
}

.drawer-icon--todo {
  background: linear-gradient(135deg, #ecf5ff, #d9ecff);
  color: #409eff;
}

.drawer-icon--message {
  background: linear-gradient(135deg, #fdf6ec, #faecd8);
  color: #e6a23c;
}

.drawer-icon--notice {
  background: linear-gradient(135deg, #f0f9eb, #e1f3d8);
  color: #67c23a;
}

.dashboard-quick-item:hover .dashboard-quick-item__icon {
  transform: scale(1.05);
}

.dashboard-quick-item__head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}

.dashboard-quick-item__title {
  margin: 0;
  color: #303133;
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
  transition: max-width 0.3s;
}

.dashboard-quick-item__time {
  font-size: 13px;
  color: #909399;
  flex-shrink: 0;
  margin-left: 12px;
  white-space: nowrap;
  transition: opacity 0.3s;
}

.dashboard-quick-item:hover .dashboard-quick-item__time {
  opacity: 0;
}

.dashboard-quick-item__desc {
  margin: 0;
  color: #606266;
  line-height: 1.5;
  font-size: 13px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  padding-right: 20px;
  transition: padding-right 0.3s;
}

.dashboard-quick-item:hover .dashboard-quick-item__title {
  max-width: calc(100% - 64px);
}

.dashboard-quick-item:hover .dashboard-quick-item__desc {
  padding-right: 64px;
}

.dashboard-quick-item__action {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%) translateX(20px);
  flex-shrink: 0;
  opacity: 0;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.dashboard-quick-item:hover .dashboard-quick-item__action {
  opacity: 1;
  transform: translateY(-50%) translateX(0);
}

.icon-text {
  color: #606266;
  font-size: 11px;
  text-shadow: none;
}

.shortcut-config__tip {
  margin-bottom: 16px;
  font-size: 14px;
  color: #606266;
  background-color: #f4f4f5;
  padding: 10px 16px;
  border-radius: 6px;
}

.shortcut-config__selected-panel {
  margin-bottom: 16px;
  padding: 12px 14px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fbff, #eef5ff);
  border: 1px solid #dbe7ff;
}

.shortcut-config__panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
  color: #303133;
  font-size: 14px;
}

.shortcut-config__panel-head span {
  color: #409eff;
  font-weight: 700;
}

.shortcut-config__selected-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.shortcut-config__selected-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  max-width: 100%;
  border: 1px solid #bfd7ff;
  background: #ffffff;
  border-radius: 999px;
  padding: 6px 12px 6px 8px;
  color: #2f3e52;
  cursor: pointer;
  transition: all 0.2s ease;
}

.shortcut-config__selected-chip:hover {
  border-color: #8cb9ff;
  box-shadow: 0 6px 14px rgba(64, 158, 255, 0.12);
}

.shortcut-config__selected-order {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #409eff;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  flex: 0 0 auto;
}

.shortcut-config__selected-label {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  font-weight: 600;
}

.shortcut-config__selected-close {
  color: #94a3b8;
  font-size: 16px;
  line-height: 1;
  flex: 0 0 auto;
}

.shortcut-config__drag-hint {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.shortcut-config__selected-chip {
  user-select: none;
}

.shortcut-config__selected-chip.is-dragging {
  opacity: 0.4;
  transform: scale(0.95);
}

.shortcut-config__selected-chip.is-drag-over {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.shortcut-config__selected-arrows {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  margin-left: auto;
}

.shortcut-config__selected-arrows i {
  font-size: 16px;
  color: #909399;
  cursor: pointer;
  border-radius: 4px;
  padding: 2px;
  transition: all 0.15s;
}

.shortcut-config__selected-arrows i:hover:not(.disabled) {
  color: #409eff;
  background: rgba(64, 158, 255, 0.1);
}

.shortcut-config__selected-arrows i.disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.shortcut-config__card-status {
  flex-shrink: 0;
  font-size: 22px;
  display: flex;
  align-items: center;
}

.shortcut-config__card-check {
  color: #409eff;
}

.shortcut-config__card-add {
  color: #c0c4cc;
  transition: color 0.2s;
}

.shortcut-config__card:hover .shortcut-config__card-add {
  color: #909399;
}

.shortcut-config__empty {
  color: #6b7b8f;
  font-size: 13px;
}

.shortcut-config__scroll {
  padding-right: 4px;
}

.shortcut-config__scroll-view {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.shortcut-config__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  padding-right: 6px;
}

.shortcut-config__card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid #ebeef5;
  background: #ffffff;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.shortcut-config__card:hover {
  border-color: #c6e2ff;
  background: #f8fbff;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.06);
}

.shortcut-config__card.is-checked {
  border-color: #409eff;
  background-color: #f0f7ff;
}

.shortcut-config__card-main {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
  flex: 1;
}

.shortcut-config__text {
  min-width: 0;
  flex: 1;
}

.shortcut-config__text strong {
  display: block;
  font-size: 15px;
  color: #303133;
  white-space: normal;
  word-break: break-all;
  font-weight: 500;
  line-height: 1.4;
}

.shortcut-config__text span {
  display: block;
  margin-top: 4px;
  color: #94a3b8;
  font-size: 12px;
  line-height: 1.4;
}

.shortcut-config__checkbox-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}

.shortcut-config__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.shortcut-config__count {
  font-size: 14px;
  color: #606266;
}

.shortcut-config__count span {
  color: #409eff;
  font-weight: bold;
  font-size: 16px;
}

.shortcut-config__actions {
  display: flex;
  gap: 10px;
}

:deep(.shortcut-config .el-dialog__body) {
  padding-top: 10px;
  padding-bottom: 20px;
  overflow: hidden;
}

:deep(.shortcut-config .el-scrollbar__bar.is-vertical) {
  right: 2px;
}

:global(.shortcut-config-modal.el-overlay) {
  z-index: 5200 !important;
}

:global(.shortcut-config.el-dialog) {
  z-index: 5202 !important;
}

/* ---------- 1200px — stack to single-column ---------- */
@media (max-width: 1200px) {
  .dashboard-container {
    flex-direction: column;
    align-items: flex-start;
    padding: 20px;
    height: auto;
    gap: 20px;
  }

  .dashboard-main-content {
    flex: none;
    width: 100%;
  }

  .short-wrapper {
    flex: none;
    width: 100%;
    margin-right: 0;
    margin-bottom: 0;
  }

  .dashboard-modules-wrapper {
    flex-direction: column;
  }

  .dashboard-extra-wrapper {
    margin-top: 16px;
  }

  .shortcut-panel {
    gap: 10px 16px;
  }

  .suspension-wrapper {
    right: 20px;
    bottom: 20px;
    top: auto;
    transform: none;
  }

  .suspension-list {
    border-radius: 22px;
  }
}

/* ---------- 1024px — tablet landscape / narrow desktop ---------- */
@media (max-width: 1024px) {
  .dashboard-container {
    padding: 16px;
    gap: 18px;
  }

  .hello-wrapper {
    margin-left: 0;
    margin-bottom: 28px;
  }

  .greetings {
    font-size: 32px;
    letter-spacing: 1px;
  }

  .hello-other-info {
    font-size: 16px;
  }

  .login-info {
    gap: 14px;
  }

  .dashboard-module {
    padding: 16px;
    min-height: 240px;
  }

  .modern-task-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
    margin-right: 12px;
  }

  .dashboard-modules-wrapper {
    margin-left: 0;
  }
}

@media (max-width: 768px) {
  .greetings {
    font-size: 32px;
    letter-spacing: 0.8px;
  }

  .hello-other-info {
    font-size: 15px;
    white-space: normal;
    flex-wrap: wrap;
  }

  .shortcut-panel,
  .shortcut-config__grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .module-header {
    align-items: flex-start;
    gap: 12px;
    flex-wrap: wrap;
  }

  .schedule-preview-summary {
    flex-direction: column;
    align-items: flex-start;
  }

  .schedule-preview-summary__desc {
    text-align: left;
  }

  .modern-progress-top {
    flex-direction: column;
  }

  .modern-progress-badges {
    justify-content: flex-start;
  }

  .modern-progress-fact {
    align-items: flex-start;
    flex-direction: column;
    gap: 6px;
  }

  .shortcut-config__selected-list {
    gap: 8px;
  }

  .shortcut-config__selected-chip {
    width: 100%;
  }

  .suspension-list {
    flex-direction: row;
    border-radius: 22px;
    padding: 8px 14px;
  }

  .dashboard-quick-item {
    padding: 14px 16px;
    gap: 12px;
  }

  .dashboard-quick-item__icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }

  .hello-wrapper {
    margin-bottom: 24px;
  }

  .login-info {
    gap: 10px 16px;
  }
}

/* ---------- 640px — small mobile ---------- */
@media (max-width: 640px) {
  :deep(.dashboard-quick-drawer.el-drawer) {
    width: 100% !important;
  }

  .dashboard-container {
    padding: 12px;
    gap: 14px;
  }

  .hello-wrapper {
    margin-left: 0;
    margin-bottom: 18px;
  }

  .greetings {
    font-size: 24px;
    letter-spacing: 0.5px;
    margin-top: 10px;
  }

  .hello-other-info {
    font-size: 13px;
    line-height: 22px;
  }

  .login-info {
    gap: 8px 12px;
    font-size: 11px;
  }

  .recent-tabs-container {
    margin-top: 16px;
    gap: 8px;
  }

  .recent-tab-item {
    padding: 5px 12px;
    font-size: 12px;
  }

  .dashboard-modules-wrapper {
    margin-left: 0;
    gap: 14px;
  }

  .dashboard-module {
    padding: 14px;
    min-height: 200px;
    border-radius: 10px;
  }

  .module-header {
    margin-bottom: 12px;
    gap: 8px;
  }

  .module-header h3 {
    font-size: 14px;
  }

  .schedule-preview-summary {
    padding: 10px 12px;
    gap: 8px;
  }

  .schedule-preview-summary__title {
    font-size: 12px;
  }

  .schedule-preview-summary__desc {
    font-size: 11px;
  }

  .schedule-carousel {
    padding: 0 6px 4px;
  }

  .schedule-carousel :deep(.el-carousel__arrow) {
    width: 26px;
    height: 26px;
  }

  .schedule-carousel :deep(.el-carousel__arrow--left) {
    left: -8px;
  }

  .schedule-carousel :deep(.el-carousel__arrow--right) {
    right: -8px;
  }

  .modern-progress-card {
    padding: 12px 14px;
    gap: 10px;
  }

  .modern-progress-pill {
    min-height: 26px;
    padding: 0 10px;
    font-size: 10.5px;
  }

  .modern-progress-fact {
    padding: 8px 10px;
  }

  .modern-task-card {
    padding: 12px;
    flex-wrap: wrap;
  }

  .modern-task-icon {
    width: 36px;
    height: 36px;
    font-size: 18px;
    margin-right: 10px;
    border-radius: 10px;
  }

  .modern-task-title h4 {
    font-size: 12.5px;
  }

  .modern-task-actions {
    margin-left: 0;
    margin-top: 8px;
    width: 100%;
    flex-direction: row;
    justify-content: flex-end;
  }

  .modern-task-actions .el-button {
    width: auto;
  }

  .short-wrapper {
    padding: 16px;
    border-radius: 12px;
  }

  .short-header {
    font-size: 15px;
    margin-bottom: 14px;
  }

  .shortcut-panel {
    gap: 8px 12px;
  }

  .shortcut-item {
    padding: 12px 6px;
  }

  .shortcut-item__icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
    margin-bottom: 8px;
  }

  .shortcut-item__title {
    font-size: 12px;
  }

  .suspension-wrapper {
    right: 10px;
    bottom: 10px;
  }

  .suspension-list {
    padding: 10px 6px;
    gap: 10px;
    border-radius: 18px;
  }

  .icon-wrapper {
    width: 38px;
    height: 38px;
    font-size: 18px;
  }

  .icon-text {
    font-size: 10px;
  }

  .task-overview-badges {
    gap: 6px;
  }

  .task-overview-badge {
    font-size: 10px;
    padding: 3px 8px;
  }
}

/* ---------- 560px — narrow mobile ---------- */
@media (max-width: 560px) {
  .shortcut-panel,
  .shortcut-config__grid {
    grid-template-columns: 1fr;
  }
}
</style>
