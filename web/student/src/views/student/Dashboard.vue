<template>
  <div class="dashboard-container">
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
        <div class="hello-other-info login-info">
          <span>考试记录：{{ dashboard.examRecordCount || 0 }} 条</span>
          <span>上次登录时间：{{ lastLoginTime }}</span>
        </div>

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
            <h3>明天课程表</h3>
            <el-button link type="primary" @click="go(`/${activeRole}/schedule`)">查看课表</el-button>
          </div>
          <div class="module-content">
          <el-skeleton :loading="loading" animated>
            <template #template>
              <div style="display: flex; flex-direction: column; gap: 12px; height: 100%;">
                <el-skeleton-item variant="rect" style="height: 80px; border-radius: 8px;" />
                <el-skeleton-item variant="rect" style="height: 80px; border-radius: 8px;" />
              </div>
            </template>
            <template #default>
                <template v-if="tomorrowCourses.length > 0">
                  <div v-for="course in tomorrowCourses" :key="course.key" class="modern-progress-card">
                    <div class="modern-progress-info">
                      <div class="modern-progress-head">
                        <h4>{{ course.courseName }}</h4>
                        <el-tag size="small" effect="plain">{{ course.weekDayLabel }}</el-tag>
                      </div>
                      <p>{{ course.timeText }}</p>
                      <p v-if="course.locationText">{{ course.locationText }}</p>
                      <p v-if="course.teacherName">任课教师：{{ course.teacherName }}</p>
                    </div>
                    <div class="modern-progress-side">
                      <div class="modern-progress-section">{{ course.sectionText }}</div>
                      <div class="modern-progress-meta">{{ course.weeksText || '本学期授课' }}</div>
                    </div>
                  </div>
                </template>
                <template v-else>
                  <el-empty description="明天没有课程安排，好好休息吧！" :image-size="100" />
                </template>
              </template>
            </el-skeleton>
          </div>
        </div>

        <div class="dashboard-module module-tasks">
          <div class="module-header">
            <h3>近期任务</h3>
            <el-button link type="primary" @click="go('/student/plaza')">任务中心</el-button>
          </div>
          <div class="module-content task-list">
          <el-skeleton :loading="loading" animated>
            <template #template>
              <div style="display: flex; flex-direction: column; gap: 12px; height: 100%;">
                <el-skeleton-item variant="rect" style="height: 80px; border-radius: 8px;" />
                <el-skeleton-item variant="rect" style="height: 80px; border-radius: 8px;" />
              </div>
            </template>
            <template #default>
                <template v-if="recentTasks.length">
                  <div
                    v-for="task in recentTasks"
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
                    </div>
                    <div class="modern-task-actions">
                      <el-button plain type="primary" size="small" @click="openTask(task.raw)">
                        {{ task.actionLabel }}
                      </el-button>
                    </div>
                  </div>
                </template>
                <el-empty v-else description="当前没有待处理任务" :image-size="100" />
              </template>
            </el-skeleton>
          </div>
        </div>
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

    <el-drawer v-model="quickDrawerVisible" direction="rtl" size="460px" class="dashboard-quick-drawer" append-to-body>
      <template #header>
        <div class="dashboard-quick-drawer__header">
          <div class="dashboard-quick-drawer__title">{{ drawerTitle }}（{{ drawerCount }}）</div>
          <el-button type="primary" link @click="openMessageCenter">查看全部</el-button>
        </div>
      </template>
      <div class="dashboard-quick-drawer__body" v-loading="drawerLoading">
        <template v-if="activeDrawerTab === 'todo'">
          <div v-if="drawerTodoList.length" class="dashboard-quick-list">
            <article v-for="item in drawerTodoList" :key="item.key" class="dashboard-quick-item" @click="openDrawerTask(item)">
              <div class="dashboard-quick-item__icon" style="background: linear-gradient(135deg, #ecf5ff, #d9ecff); color: #409eff;">
                <el-icon><i class="ri-mac-line"></i></el-icon>
              </div>
              <div class="dashboard-quick-item__main">
                <div class="dashboard-quick-item__head">
                  <h4 class="dashboard-quick-item__title" :title="item.title">{{ item.title }}</h4>
                  <span v-if="item.createTime" class="dashboard-quick-item__time">{{ item.createTime.substring(0, 10) }}</span>
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
            <article v-for="item in drawerMessageList" :key="item.messageId" class="dashboard-quick-item" @click="openDrawerMessage(item)">
              <div class="dashboard-quick-item__icon" style="background: linear-gradient(135deg, #fdf6ec, #faecd8); color: #e6a23c;">
                <el-icon><i class="ri-message-3-line"></i></el-icon>
              </div>
              <div class="dashboard-quick-item__main">
                <div class="dashboard-quick-item__head">
                  <h4 class="dashboard-quick-item__title" :title="item.messageTitle">{{ item.messageTitle }}</h4>
                  <span v-if="item.createTime" class="dashboard-quick-item__time">{{ item.createTime.substring(0, 10) }}</span>
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
            <article v-for="item in drawerNoticeList" :key="item.messageId" class="dashboard-quick-item" @click="openDrawerMessage(item)">
              <div class="dashboard-quick-item__icon" style="background: linear-gradient(135deg, #f0f9eb, #e1f3d8); color: #67c23a;">
                <el-icon><i class="ri-notification-3-line"></i></el-icon>
              </div>
              <div class="dashboard-quick-item__main">
                <div class="dashboard-quick-item__head">
                  <h4 class="dashboard-quick-item__title" :title="item.messageTitle">{{ item.messageTitle }}</h4>
                  <span v-if="item.createTime" class="dashboard-quick-item__time">{{ item.createTime.substring(0, 10) }}</span>
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
      width="680px"
      class="shortcut-config"
      destroy-on-close
    >
      <div class="shortcut-config__tip">
        当前端可访问页面共 {{ availableShortcutItems.length }} 个，最多可设置 9 个首页快捷入口。
      </div>
      <el-checkbox-group v-model="editingShortcutPaths" @change="handleShortcutSelectionChange">
        <div class="shortcut-config__grid">
          <label
            v-for="item in availableShortcutItems"
            :key="`config-${item.path}`"
            class="shortcut-config__card"
            :class="{ 'is-checked': editingShortcutPaths.includes(item.path) }"
          >
            <div class="shortcut-config__card-main">
              <div class="shortcut-item__icon shortcut-item__icon--small" :style="{ backgroundImage: item.bg }">
                <i :class="item.icon"></i>
              </div>
              <div class="shortcut-config__text">
                <strong>{{ item.title }}</strong>
              </div>
            </div>
            <div class="shortcut-config__checkbox-wrapper">
              <el-checkbox :value="item.path" @click.stop />
            </div>
          </label>
        </div>
      </el-checkbox-group>
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
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { CheckboxValueType } from 'element-plus'
import { Setting } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getPortalMySchedule,
  getPortalTaskCenter,
  getStudentDashboard,
  getPortalUnreadMessages,
  listExamRecord,
  listPortalTermOptions,
  listPortalNotice,
  markPortalTaskRead,
} from '@/api/portal'
import usePortalUserStore from '@/store/user'
import { useTabsStore } from '@/store/tabs'

const router = useRouter()
const route = useRoute()
const userStore = usePortalUserStore()
const tabsStore = useTabsStore()

const dashboard = ref<any>({})
const taskCenter = ref<any>({})
const examRecords = ref<any[]>([])
const schedulePayload = ref<any>({})
const termOptions = ref<any[]>([])
const shortcutDialogVisible = ref(false)
const storedShortcutPaths = ref<string[]>([])
const editingShortcutPaths = ref<string[]>([])
const quickDrawerVisible = ref(false)
const drawerLoading = ref(false)
const activeDrawerTab = ref<'todo' | 'message' | 'notice'>('todo')
const drawerMessageList = ref<any[]>([])
const drawerNoticeList = ref<any[]>([])

const activeRole = computed(() => {
  const firstSegment = route.path.split('/')[1]
  if (userStore.availablePortalRoles.includes(firstSegment)) {
    return firstSegment
  }
  return userStore.preferredPortalRole || 'student'
})

const recentTabs = computed(() => {
  const dashboardPath = `/${activeRole.value}/dashboard`
  return tabsStore.visitedTabs.filter((tab) => tab.path !== dashboardPath).slice(-5)
})

const userName = computed(() => userStore.user?.realName || userStore.user?.userName || '同学')

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

const currentTerm = computed(() => {
  const currentTermId = schedulePayload.value?.termId
  return termOptions.value.find((item: any) => item.value === currentTermId)
    || termOptions.value.find((item: any) => item.isCurrent === '1')
    || termOptions.value[0]
    || null
})

const currentSemesterLabel = computed(() => {
  if (!currentTerm.value) return '当前学期'
  const schoolYear = currentTerm.value.schoolYear ? `${currentTerm.value.schoolYear}` : ''
  const termName = currentTerm.value.termName ? `${currentTerm.value.termName}` : ''
  return [schoolYear, termName].filter(Boolean).join(' ')
})

const currentTeachingWeek = computed(() => Number(schedulePayload.value?.currentWeek || 1))

const lastLoginTime = computed(() => formatDateTime(userStore.user?.loginDate) || '暂无记录')

const tomorrowMeta = computed(() => {
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  const todayWeekDay = convertWeekDay(new Date())
  const tomorrowWeekDay = convertWeekDay(tomorrow)
  const baseWeek = Number(schedulePayload.value?.currentWeek || 1)
  const targetWeek = tomorrowWeekDay < todayWeekDay ? baseWeek + 1 : baseWeek
  return {
    date: tomorrow,
    weekDay: tomorrowWeekDay,
    targetWeek,
  }
})

const tomorrowCourses = computed(() => {
  const activities = Array.isArray(schedulePayload.value?.activities) ? schedulePayload.value.activities : []
  return activities
    .filter((item: any) => {
      const sameDay = Number(item.weekDay) === tomorrowMeta.value.weekDay
      if (!sameDay) return false
      const weekIndexes = Array.isArray(item.weekIndexes) ? item.weekIndexes.map((value: any) => Number(value)) : []
      if (!weekIndexes.length) return true
      return weekIndexes.includes(tomorrowMeta.value.targetWeek)
    })
    .sort((left: any, right: any) => Number(left.startSection || 0) - Number(right.startSection || 0))
    .map((item: any) => ({
      key: `${item.scheduleId || item.classCourseId}-${item.weekDay}-${item.startSection}`,
      courseName: item.courseName || '未命名课程',
      teacherName: item.teacherName || '',
      timeText: `${formatWeekDayLabel(item.weekDay)} ${item.startTime || '--:--'} - ${item.endTime || '--:--'}`,
      locationText: [item.campus, item.buildingName, item.classroom].filter(Boolean).join(' / '),
      sectionText: `${item.startSection || '-'}-${item.endSection || item.startSection || '-'} 节`,
      weekDayLabel: formatWeekDayLabel(item.weekDay),
      weeksText: item.weeksStr || item.weeksText || '',
    }))
})

const recentTasks = computed(() => {
  const source = [
    ...(taskCenter.value.todoTasks || []),
    ...(taskCenter.value.recommendedTasks || []),
  ]
  const seen = new Set<string>()
  const ongoingPaperId = String(userStore.ongoingExam?.paperId || '')
  return source
    .filter((item: any) => {
      const actionType = String(item?.action?.type || '').toLowerCase()
      const taskPaperId = String(item?.action?.targetId || item?.action?.paperId || item?.action?.row?.paperId || '')

      // If the user is already inside this exam, hide the duplicate "start exam" task
      // and keep only the "resume exam" card visible on the homepage.
      if (ongoingPaperId && actionType === 'exam' && taskPaperId && taskPaperId === ongoingPaperId) {
        return false
      }

      const key = String(item?.key || '')
      if (!key || seen.has(key)) return false
      seen.add(key)
      return true
    })
    .slice(0, 4)
    .map((item: any) => buildTaskCard(item))
})
const examAttemptCountMap = computed(() =>
  examRecords.value.reduce((acc: Record<string, number>, item: any) => {
    const key = String(item.paperId || '')
    if (!key) return acc
    acc[key] = (acc[key] || 0) + 1
    return acc
  }, {}),
)
const latestExamRecordMap = computed(() =>
  examRecords.value.reduce((acc: Record<string, any>, item: any) => {
    const key = String(item.paperId || '')
    if (!key || acc[key]) return acc
    acc[key] = item
    return acc
  }, {}),
)

const drawerTodoList = computed(() => (taskCenter.value?.todoTasks || []).slice(0, 6))
const drawerTitle = computed(() => {
  if (activeDrawerTab.value === 'message') return '未读消息'
  if (activeDrawerTab.value === 'notice') return '通知公告'
  return '我的待办'
})
const drawerCount = computed(() => {
  if (activeDrawerTab.value === 'message') return drawerMessageList.value.length
  if (activeDrawerTab.value === 'notice') return drawerNoticeList.value.length
  return drawerTodoList.value.length
})

const availableShortcutItems = computed(() => {
  const prefix = `/${activeRole.value}/`
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

const displayShortcutItems = computed(() => {
  const selected = storedShortcutPaths.value.length
    ? storedShortcutPaths.value
    : availableShortcutItems.value.slice(0, 9).map((item) => item.path)
  const itemMap = new Map(availableShortcutItems.value.map((item) => [item.path, item]))
  return selected
    .map((path) => itemMap.get(path))
    .filter(Boolean)
    .slice(0, 9) as Array<{ path: string; title: string; icon: string; bg: string }>
})

function isUnreadMessage(item: any) {
  return String(item?.readFlag || '0') === '0'
}

function isUnreadNotice(item: any) {
  return String(item?.readFlag || '0') === '0'
}

watch(
  [activeRole, availableShortcutItems],
  () => {
    restoreShortcutSelection()
  },
  { immediate: true },
)

function go(path: string) {
  router.push(path)
}

function convertWeekDay(date: Date) {
  const day = date.getDay()
  return day === 0 ? 7 : day
}

function formatWeekDayLabel(weekDay: number) {
  const labels: Record<number, string> = {
    1: '星期一',
    2: '星期二',
    3: '星期三',
    4: '星期四',
    5: '星期五',
    6: '星期六',
    7: '星期日',
  }
  return labels[Number(weekDay)] || '未知星期'
}

function formatDateTime(value?: string | number | Date | null) {
  if (!value) return ''
  const date = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  const hour = `${date.getHours()}`.padStart(2, '0')
  const minute = `${date.getMinutes()}`.padStart(2, '0')
  const second = `${date.getSeconds()}`.padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

function resolveShortcutVisual(path: string, title: string) {
  const text = `${path} ${title}`
  if (text.includes('dashboard') || text.includes('概览') || text.includes('首页')) {
    return { icon: 'ri-home-5-line', bg: 'linear-gradient(135deg, #5aa9ff, #2f6ee5)' }
  }
  if (text.includes('course') || title.includes('课程')) {
    return { icon: 'ri-book-open-line', bg: 'linear-gradient(135deg, #edb580, #c97b4e)' }
  }
  if (text.includes('schedule') || title.includes('课表')) {
    return { icon: 'ri-calendar-schedule-line', bg: 'linear-gradient(135deg, #70dbc9, #2fae95)' }
  }
  if (text.includes('resource') || title.includes('资源')) {
    return { icon: 'ri-folder-chart-line', bg: 'linear-gradient(135deg, #ffb86a, #ff8f3c)' }
  }
  if (text.includes('recommend') || title.includes('推荐')) {
    return { icon: 'ri-lightbulb-flash-line', bg: 'linear-gradient(135deg, #ffd36e, #f1a702)' }
  }
  if (text.includes('qa') || title.includes('问答')) {
    return { icon: 'ri-chat-3-line', bg: 'linear-gradient(135deg, #77d3ef, #2f8de4)' }
  }
  if (text.includes('plaza') || title.includes('任务')) {
    return { icon: 'ri-task-line', bg: 'linear-gradient(135deg, #ff9595, #f15f5f)' }
  }
  if (text.includes('exam') || title.includes('考试')) {
    return { icon: 'ri-file-paper-2-line', bg: 'linear-gradient(135deg, #9ea2ff, #7168f1)' }
  }
  if (text.includes('wrongbook') || title.includes('错题')) {
    return { icon: 'ri-bookmark-3-line', bg: 'linear-gradient(135deg, #f7a9c4, #ef668f)' }
  }
  if (text.includes('warning') || title.includes('预警')) {
    return { icon: 'ri-alarm-warning-line', bg: 'linear-gradient(135deg, #ffc46a, #ee8b1f)' }
  }
  if (text.includes('report') || title.includes('报告')) {
    return { icon: 'ri-bar-chart-box-line', bg: 'linear-gradient(135deg, #8bd8af, #2ca675)' }
  }
  return { icon: 'ri-grid-line', bg: 'linear-gradient(135deg, #a6b4c8, #6f8097)' }
}

function priorityLabel(value?: string) {
  const normalized = String(value || '').trim().toUpperCase()
  const map: Record<string, string> = {
    LOW: '低',
    NORMAL: '普通',
    MEDIUM: '中',
    HIGH: '高',
    URGENT: '紧急',
    P0: '紧急',
    P1: '高',
    P2: '中',
    P3: '普通',
    P4: '低',
  }
  return map[normalized] || String(value || '').trim()
}

function normalizeTaskMetaText(value: unknown) {
  const text = String(value || '').trim()
  if (!text) return ''

  const priorityMatch = text.match(/^(优先级)\s*[:：]?\s*(.+)$/i) || text.match(/^(priority)\s*[:：]?\s*(.+)$/i)
  if (priorityMatch) {
    const rawValue = priorityMatch[2].trim()
    const label = priorityLabel(rawValue)
    return `处理优先级：${label}`
  }

  return text
}

function resolveExamRetakeInfo(task: any) {
  const actionType = String(task?.action?.type || '').toLowerCase()
  if (actionType !== 'exam') return null
  const paperId = String(task?.action?.targetId || task?.action?.paperId || task?.action?.row?.paperId || '')
  if (!paperId) return null
  const attemptCount = examAttemptCountMap.value[paperId] || 0
  if (attemptCount <= 0) return null
  const maxAttemptCount = Number(task?.maxAttemptCount || 0)
  return {
    attemptCount,
    maxAttemptCount,
    remainingCount: maxAttemptCount > 0 ? Math.max(0, maxAttemptCount - attemptCount) : null,
    isMakeup: maxAttemptCount > 0,
    latestRecord: latestExamRecordMap.value[paperId] || null,
  }
}

function resolveTaskTag(task: any) {
  const actionType = String(task?.action?.type || '').toLowerCase()
  const tag = String(task?.tag || '').trim()

  if (actionType === 'resume') return '进行中的考试'
  if (actionType === 'exam') {
    const retakeInfo = resolveExamRetakeInfo(task)
    if (retakeInfo?.isMakeup) return '可补考'
    return retakeInfo ? '可再次参加考试' : '待参加考试'
  }
  if (actionType === 'wrongbook') return '错题复习'
  if (actionType === 'course') return '课程任务'

  if (tag === '待办考试') return '待参加考试'
  if (tag === '考试任务') return '待参加考试'
  return tag || '任务'
}

function resolveTaskStatusText(task: any) {
  const actionType = String(task?.action?.type || '').toLowerCase()
  const status = String(task?.status || '').trim()
  const desc = String(task?.desc || '').trim()

  if (actionType === 'resume') {
    return '你已开始答题，可继续完成'
  }
  if (actionType === 'exam') {
    const retakeInfo = resolveExamRetakeInfo(task)
    if (retakeInfo) {
      if (retakeInfo.isMakeup) {
        return `你已参加 ${retakeInfo.attemptCount}/${retakeInfo.maxAttemptCount} 次，还可补考 ${retakeInfo.remainingCount} 次`
      }
      return `你已参加 ${retakeInfo.attemptCount} 次，可再次作答`
    }
    return '考试尚未开始作答'
  }
  if (status === '进行中') return '正在处理中'
  if (status === '待处理') return desc || '等待你处理'
  return status || desc || '待处理'
}

function buildTaskCard(task: any) {
  const actionType = task?.action?.type || ''
  let icon = 'ri-task-line'
  let iconClass = 'task-icon--default'
  if (actionType === 'exam' || actionType === 'resume' || actionType === 'record') {
    icon = 'ri-file-paper-2-line'
    iconClass = 'task-icon--exam'
  } else if (actionType === 'wrongbook') {
    icon = 'ri-book-read-line'
    iconClass = 'task-icon--homework'
  } else if (actionType === 'course') {
    icon = 'ri-calendar-check-line'
    iconClass = 'task-icon--practice'
  }

  const retakeInfo = resolveExamRetakeInfo(task)
  const tag = resolveTaskTag(task)
  return {
    raw: task,
    key: task?.key,
    title: task?.title || '未命名任务',
    tag,
    tagType: task?.tagType || 'info',
    meta: [
      ...(Array.isArray(task?.meta) ? task.meta.map((item: unknown) => normalizeTaskMetaText(item)).filter(Boolean) : []),
      ...(retakeInfo?.isMakeup && retakeInfo.remainingCount !== null ? [`剩余补考次数：${retakeInfo.remainingCount}`] : []),
      ...(retakeInfo?.latestRecord?.submitTime ? [`最近参加：${String(retakeInfo.latestRecord.submitTime).slice(0, 16).replace('T', ' ')}`] : []),
    ],
    icon,
    iconClass,
    statusText: resolveTaskStatusText(task),
    metaClass: resolveTaskMetaClass(task?.tagType),
    actionLabel: resolveTaskActionLabel(task),
  }
}

function resolveTaskMetaClass(tagType?: string) {
  if (tagType === 'danger') return 'text-danger'
  if (tagType === 'warning') return 'text-warning'
  if (tagType === 'success') return 'text-success'
  return 'text-muted'
}

function resolveTaskActionLabel(task: any) {
  const type = task?.action?.type
  const dispatchId = Number(task?.action?.row?.dispatchId || 0)
  if (dispatchId > 0) return '去处理'
  if (type === 'resume') return '继续作答'
  if (type === 'exam') {
    const retakeInfo = resolveExamRetakeInfo(task)
    if (retakeInfo?.isMakeup) return '去补考'
    return retakeInfo ? '再次考试' : '去考试'
  }
  if (type === 'course') return '查看课程'
  if (type === 'wrongbook') return '去复习'
  if (type === 'record') return '查看记录'
  return '查看详情'
}

function openTask(task: any) {
  const action = task?.action || {}
  const dispatchId = Number(action?.row?.dispatchId || 0)
  if (dispatchId) {
    markPortalTaskRead(dispatchId).catch(() => {})
  }
  if (dispatchId) {
    router.push(`/student/tasks/${dispatchId}?from=dashboard`)
    return
  }
  if (action.type === 'exam') {
    router.push('/student/exams')
    return
  }
  if (action.type === 'resume' && action.recordId) {
    router.push({
      path: `/student/exams/session/${action.recordId}`,
      query: {
        paperId: String(action.targetId || action.row?.paperId || ''),
        startedAt: String(action.row.startTime || ''),
      },
    })
    return
  }
  if (action.type === 'record') {
    router.push('/student/exams?tab=records')
    return
  }
  if (action.type === 'wrongbook') {
    router.push('/student/wrongbook')
    return
  }
  if (action.type === 'course') {
    router.push({
      path: '/student/courses',
      query: action.targetId ? { openCourseId: String(action.targetId) } : {},
    })
    return
  }
  router.push(action.path || '/student/plaza')
}

async function openQuickDrawer(tab: 'todo' | 'message' | 'notice') {
  activeDrawerTab.value = tab
  quickDrawerVisible.value = true
  await refreshQuickDrawer(tab)
}

async function refreshQuickDrawer(tab = activeDrawerTab.value) {
  const userId = userStore.user?.userId
  if (!userId) return
  drawerLoading.value = true
  try {
    if (tab === 'todo') {
      if (!taskCenter.value?.todoTasks) {
        await loadTaskCenter()
      }
      return
    }
    if (tab === 'message') {
      const res = await getPortalUnreadMessages({ userId, limit: 6 })
      drawerMessageList.value = (res.data || []).filter((item: any) => isUnreadMessage(item))
      return
    }
    const res = await listPortalNotice({ limit: 6 })
    drawerNoticeList.value = (res.data || []).filter((item: any) => isUnreadNotice(item))
  } finally {
    drawerLoading.value = false
  }
}

async function preloadQuickBadges() {
  const userId = userStore.user?.userId
  if (!userId) return
  try {
    const [messageRes, noticeRes] = await Promise.all([
      getPortalUnreadMessages({ userId, limit: 20 }),
      listPortalNotice({ limit: 20 }),
    ])
    drawerMessageList.value = (messageRes.data || []).filter((item: any) => isUnreadMessage(item))
    drawerNoticeList.value = (noticeRes.data || []).filter((item: any) => isUnreadNotice(item))
  } catch {
    // Keep badge preload non-blocking for the dashboard
  }
}

function openMessageCenter() {
  quickDrawerVisible.value = false
  router.push(`/student/messages?tab=${activeDrawerTab.value}`)
}

function openDrawerTask(task: any) {
  quickDrawerVisible.value = false
  openTask(task)
}

function openDrawerMessage(item: any) {
  quickDrawerVisible.value = false
  router.push(`/student/messages?tab=${activeDrawerTab.value}`)
}

function shortcutStorageKey() {
  return `portal-shortcuts:${activeRole.value}`
}

function restoreShortcutSelection() {
  const availablePaths = new Set(availableShortcutItems.value.map((item) => item.path))
  const raw = localStorage.getItem(shortcutStorageKey())
  if (!raw) {
    storedShortcutPaths.value = availableShortcutItems.value.slice(0, 9).map((item) => item.path)
    return
  }
  try {
    const parsed = JSON.parse(raw)
    if (!Array.isArray(parsed)) {
      throw new Error('invalid shortcuts')
    }
    storedShortcutPaths.value = parsed.filter((path) => availablePaths.has(path)).slice(0, 9)
  } catch {
    storedShortcutPaths.value = availableShortcutItems.value.slice(0, 9).map((item) => item.path)
  }
}

function openShortcutSettings() {
  editingShortcutPaths.value = [...displayShortcutItems.value.map((item) => item.path)]
  shortcutDialogVisible.value = true
}

function handleShortcutSelectionChange(value: CheckboxValueType[]) {
  const stringValues = value.map((item) => String(item))
  if (stringValues.length <= 9) {
    editingShortcutPaths.value = stringValues
    return
  }
  editingShortcutPaths.value = stringValues.slice(0, 9)
  ElMessage.warning('首页快捷入口最多选择 9 个')
}

function restoreDefaultShortcuts() {
  editingShortcutPaths.value = availableShortcutItems.value.slice(0, 9).map((item) => item.path)
}

function saveShortcutSettings() {
  const safePaths = editingShortcutPaths.value.slice(0, 9)
  storedShortcutPaths.value = safePaths
  localStorage.setItem(shortcutStorageKey(), JSON.stringify(safePaths))
  shortcutDialogVisible.value = false
  ElMessage.success('快捷入口已更新')
}

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
}

async function loadDashboardData() {
  const userId = userStore.user?.userId
  if (!userId) return
  const res = await getStudentDashboard({ userId, recommendLimit: 5 })
  dashboard.value = res.data || {}
}

async function loadTaskCenter() {
  const userId = userStore.user?.userId
  if (!userId) return
  const res = await getPortalTaskCenter({ userId })
  taskCenter.value = res.data || {}
}

async function loadExamRecords() {
  const res = await listExamRecord({ pageNum: 1, pageSize: 50 })
  examRecords.value = res.rows || []
}

async function loadScheduleData() {
  const userId = userStore.user?.userId
  if (!userId) return
  const current = termOptions.value.find((item: any) => item.isCurrent === '1') || termOptions.value[0]
  const res = await getPortalMySchedule({
    userId,
    termId: current?.value,
  })
  schedulePayload.value = res.data || {}
}

const loading = ref(true)

async function loadData() {
  if (!userStore.user?.userId) return
  loading.value = true
  try {
    await loadTerms()
    await Promise.all([
      loadDashboardData(),
      loadTaskCenter(),
      loadScheduleData(),
      loadExamRecords(),
      preloadQuickBadges(),
    ])
  } finally {
    // 增加一点延迟，防止接口过快返回导致的闪烁
    setTimeout(() => {
      loading.value = false
    }, 300)
  }
}

onMounted(loadData)
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
}

.dashboard-main-content {
  flex: 0 1 58%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.hello-wrapper {
  margin-left: 1%;
  margin-bottom: 4rem;
  display: flex;
  flex-flow: column nowrap;
  color: #333333;
  text-shadow: none;
  box-sizing: content-box;
}

.greetings {
  margin-top: 2rem;
  font-size: 3.6rem;
  font-weight: 300;
  letter-spacing: 0.2rem;
}

.hello-hr {
  width: 100%;
  margin-top: 1.5rem;
  margin-bottom: 1.5rem;
  border: 0;
  border-top: 1px solid rgba(48, 49, 51, 0.15);
  height: 0;
}

.hello-other-info {
  font-size: 1.8rem;
  margin: 0 0 8px;
  line-height: 2.8rem;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  color: #333333;
  font-weight: 400;
}

.login-info {
  font-size: 1.2rem;
  gap: 20px;
  margin-top: 10px;
  color: #666666;
  flex-wrap: wrap;
}

.m-2 {
  margin: 0 0.5rem;
}

.font-size-11 {
  font-size: 2rem;
  font-weight: 600;
}

.recent-tabs-container {
  margin-top: 30px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.recent-tabs-title {
  font-size: 1.4rem;
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
  font-size: 1.3rem;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.02);
}

.recent-tab-item:hover {
  background-color: #ffffff;
  border-color: rgba(48, 49, 51, 0.3);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.dashboard-modules-wrapper {
  display: flex;
  gap: 20px;
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
}

.module-header h3 {
  margin: 0;
  font-size: 1.5rem;
  color: #333333;
  font-weight: 600;
  text-shadow: none;
}

.module-content {
  flex: 1;
}

.modern-progress-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  margin-bottom: 12px;
  border: 1px solid #f0f2f5;
  transition: all 0.2s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.02);
}

.modern-progress-card:hover {
  border-color: #e4e7ed;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}

.modern-progress-card:last-child {
  margin-bottom: 0;
}

.modern-progress-info {
  flex: 1;
  min-width: 0;
}

.modern-progress-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.modern-progress-head h4 {
  margin: 0;
  font-size: 1.4rem;
  color: #333333;
  font-weight: 500;
}

.modern-progress-info p {
  margin: 0 0 4px;
  font-size: 1.2rem;
  color: #999999;
}

.modern-progress-info p:last-child {
  margin-bottom: 0;
}

.modern-progress-side {
  min-width: 96px;
  text-align: right;
  padding: 10px 12px;
  border-radius: 10px;
  background: linear-gradient(180deg, #f4f9ff 0%, #edf4ff 100%);
}

.modern-progress-section {
  font-size: 1.6rem;
  font-weight: 700;
  color: #2563eb;
}

.modern-progress-meta {
  margin-top: 6px;
  font-size: 1.1rem;
  color: #7b8794;
  line-height: 1.5;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.modern-task-card {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  border: 1px solid #f0f2f5;
  transition: all 0.2s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.02);
}

.modern-task-card:hover {
  border-color: #e4e7ed;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
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
  font-size: 1.3rem;
  color: #303133;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.modern-task-status {
  font-size: 1.2rem;
  font-weight: 500;
}

.modern-task-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 1.2rem;
  color: #909399;
}

.modern-task-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-left: 16px;
  flex-shrink: 0;
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
  margin-bottom: 5rem;
  margin-right: 2rem;
  flex: 0 0 38rem;
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
  margin-bottom: 2rem;
  font-size: 1.6rem;
  font-weight: 600;
  text-shadow: none;
}

.shortcut-panel {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px 24px;
  min-height: 280px;
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
  aspect-ratio: 1;
  text-decoration: none;
  transition: all 0.2s ease-out;
  cursor: pointer;
  box-shadow: none;
}

.shortcut-item:hover {
  background-color: #f5f7fa;
  box-shadow: none;
  transform: translateY(-2px);
  border-color: transparent;
}

.shortcut-item__icon {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 1.2rem;
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
  font-size: 1.3rem;
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
  color: #409eff;
  transition: all 0.3s;
  position: relative;
}

.suspension-item:hover .icon-wrapper {
  background: #ecf5ff;
  color: #2563eb;
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
  border-color: #c6e2ff;
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.08);
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
  font-size: 1.1rem;
  text-shadow: none;
}

.shortcut-config__tip {
  margin-bottom: 20px;
  font-size: 14px;
  color: #606266;
  background-color: #f4f4f5;
  padding: 10px 16px;
  border-radius: 6px;
}

.shortcut-config__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
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
}

html {
  font-size: 10px;
}

@media (max-width: 1200px) {
  .dashboard-container {
    flex-direction: column;
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

  .shortcut-panel {
    min-height: auto;
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

@media (max-width: 768px) {
  .greetings {
    font-size: 3.2rem;
    letter-spacing: 0.08rem;
  }

  .hello-other-info {
    font-size: 1.5rem;
    white-space: normal;
    flex-wrap: wrap;
  }

  .shortcut-panel,
  .shortcut-config__grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
