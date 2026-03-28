<template>
  <div class="portal-page message-center-page !p-0 !bg-transparent">
    <div class="message-center-wrapper">
      <div class="message-center-container">
        <aside class="message-center-sidebar">
          <div class="sidebar-menu">
            <div
              v-for="item in tabOptions"
              :key="item.value"
              class="sidebar-item"
              :class="{ 'is-active': activeTab === item.value }"
              @click="switchTab(item.value)"
            >
              <div class="item-icon-wrapper">
                <el-icon v-if="item.value === 'todo'"><Monitor /></el-icon>
                <el-icon v-else-if="item.value === 'message'"><ChatDotRound /></el-icon>
                <el-icon v-else><Bell /></el-icon>
              </div>
              <span class="item-label">{{ item.label }}</span>
            </div>
          </div>
        </aside>

        <section class="message-center-content">
          <div class="content-header">
            <div class="header-title">
              <h3>{{ currentTabLabel }} ({{ currentTabCount }})</h3>
            </div>
          </div>

          <div v-loading="loading" class="content-body">
            <template v-if="activeTab === 'todo'">
              <div class="message-filter-bar">
                <div class="filter-group-pill">
                  <div class="filter-pill" :class="{ 'is-active': todoFilter === 'all' }" @click="todoFilter = 'all'">
                    全部
                  </div>
                  <div class="filter-pill" :class="{ 'is-active': todoFilter === 'pending' }" @click="todoFilter = 'pending'">
                    待处理
                  </div>
                </div>
                <div class="header-actions">
                  <el-input
                    v-model="keyword"
                    clearable
                    :placeholder="searchPlaceholder"
                    class="search-input"
                  >
                    <template #prefix>
                      <el-icon><Search /></el-icon>
                    </template>
                  </el-input>
                </div>
              </div>
              <div v-if="pagedTodoRows.length" class="message-list-simple">
                <div v-for="item in pagedTodoRows" :key="item.key || item.title" class="message-item-simple" @click="openTask(item)">
                  <div class="message-item-title">{{ item.title || '未命名待办' }}</div>
                  <div class="message-item-time">{{ item.desc || item.status || '待处理' }}</div>
                </div>
              </div>
              <div class="message-list-pagination" v-if="filteredTodoRows.length">
                <el-pagination
                  background
                  layout="prev, pager, next, jumper"
                  :total="filteredTodoRows.length"
                  :page-size="pageSize"
                  v-model:current-page="todoPage"
                />
              </div>
              <el-empty v-else description="暂无待办事项" />
            </template>

            <template v-else-if="activeTab === 'message'">
              <div class="message-filter-bar">
                <div class="filter-bar-stack">
                  <div class="filter-group-pill">
                    <div class="filter-pill" :class="{ 'is-active': messageFilter === 'unread' }" @click="messageFilter = 'unread'">
                      未读({{ unreadMessageCount }})
                    </div>
                    <div class="filter-pill" :class="{ 'is-active': messageFilter === 'read' }" @click="messageFilter = 'read'">
                      已读({{ readMessageCount }})
                    </div>
                  </div>
                  <div class="message-type-filter">
                    <div class="message-type-chip" :class="{ 'is-active': messageTypeFilter === 'all' }" @click="messageTypeFilter = 'all'">全部</div>
                    <div class="message-type-chip" :class="{ 'is-active': messageTypeFilter === 'EXAM_REMINDER' }" @click="messageTypeFilter = 'EXAM_REMINDER'">考试提醒</div>
                    <div class="message-type-chip" :class="{ 'is-active': messageTypeFilter === 'ACHIEVEMENT' }" @click="messageTypeFilter = 'ACHIEVEMENT'">成长提醒</div>
                    <div class="message-type-chip" :class="{ 'is-active': messageTypeFilter === 'COURSE_SELECTION' }" @click="messageTypeFilter = 'COURSE_SELECTION'">选课服务</div>
                    <div class="message-type-chip" :class="{ 'is-active': messageTypeFilter === 'SYSTEM' }" @click="messageTypeFilter = 'SYSTEM'">系统消息</div>
                  </div>
                </div>
                <div class="header-actions">
                  <el-input
                    v-model="keyword"
                    clearable
                    :placeholder="searchPlaceholder"
                    class="search-input"
                  >
                    <template #prefix>
                      <el-icon><Search /></el-icon>
                    </template>
                  </el-input>
                </div>
              </div>
              <div v-if="pagedMessageRows.length" class="message-list-rich">
                <article v-for="item in pagedMessageRows" :key="item.messageId" class="message-card" @click="openMessageAction(item)">
                  <div class="message-card__icon" :class="`is-${messageIconTone(item)}`">
                    <el-icon v-if="item.messageType === 'EXAM_REMINDER'"><Bell /></el-icon>
                    <el-icon v-else-if="item.messageType === 'ACHIEVEMENT'"><ChatDotRound /></el-icon>
                    <el-icon v-else><Monitor /></el-icon>
                  </div>
                  <div class="message-card__main">
                    <div class="message-card__head">
                      <div class="message-card__title" :class="{ 'is-unread-text': !isMessageRead(item) }">{{ item.messageTitle || '未命名消息' }}</div>
                      <div class="message-card__time">{{ formatDateTime(item.createTime) || '--' }}</div>
                    </div>
                    <div class="message-card__meta">
                      <el-tag size="small" effect="plain" :type="messageTagType(item)">{{ resolveMessageTypeLabel(item) }}</el-tag>
                      <el-tag v-if="!isMessageRead(item)" size="small" effect="light" type="primary">未读</el-tag>
                    </div>
                    <div class="message-card__summary">{{ item.messageSummary || item.messageContent || item.desc || '暂无内容摘要' }}</div>
                  </div>
                </article>
              </div>
              <div class="message-list-pagination" v-if="filteredMessageRows.length">
                <el-pagination
                  background
                  layout="prev, pager, next, jumper"
                  :total="filteredMessageRows.length"
                  :page-size="pageSize"
                  v-model:current-page="messagePage"
                />
              </div>
              <el-empty v-else :description="messageFilter === 'unread' ? '暂无未读消息' : '暂无已读消息'" />
            </template>

            <template v-else>
              <div class="message-filter-bar">
                <div class="filter-group-pill">
                  <div class="filter-pill" :class="{ 'is-active': noticeFilter === 'unread' }" @click="noticeFilter = 'unread'">
                    未读({{ unreadNoticeCount }})
                  </div>
                  <div class="filter-pill" :class="{ 'is-active': noticeFilter === 'read' }" @click="noticeFilter = 'read'">
                    已读({{ readNoticeCount }})
                  </div>
                </div>
                <div class="header-actions">
                  <el-input
                    v-model="keyword"
                    clearable
                    :placeholder="searchPlaceholder"
                    class="search-input"
                  >
                    <template #prefix>
                      <el-icon><Search /></el-icon>
                    </template>
                  </el-input>
                </div>
              </div>
              <div v-if="pagedNoticeRows.length" class="message-list-rich">
                <article v-for="item in pagedNoticeRows" :key="item.messageId" class="message-card" @click="openMessageAction(item)">
                  <div class="message-card__main">
                    <div class="message-card__head">
                      <div class="message-card__title" :class="{ 'is-unread-text': String(item.readFlag || '0') === '0' }">{{ item.messageTitle || '未命名公告' }}</div>
                      <div class="message-card__time">{{ formatDateTime(item.createTime) || '--' }}</div>
                    </div>
                    <div class="message-card__meta">
                      <el-tag size="small" effect="plain" type="warning">通知公告</el-tag>
                      <el-tag v-if="String(item.readFlag || '0') === '0'" size="small" effect="light" type="primary">未读</el-tag>
                    </div>
                    <div class="message-card__summary">{{ item.messageSummary || item.messageContent || '暂无内容摘要' }}</div>
                  </div>
                </article>
              </div>
              <div class="message-list-pagination" v-if="filteredNoticeRows.length">
                <el-pagination
                  background
                  layout="prev, pager, next, jumper"
                  :total="filteredNoticeRows.length"
                  :page-size="pageSize"
                  v-model:current-page="noticePage"
                />
              </div>
              <el-empty v-else :description="noticeFilter === 'unread' ? '暂无未读公告' : '暂无已读公告'" />
            </template>
          </div>
        </section>
      </div>
    </div>

    <!-- 消息详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="activeMessage?.noticeType === '1' ? '通知' : (activeMessage?.noticeType === '2' ? '公告' : '消息详情')"
      width="800px"
      class="message-detail-dialog-simple"
      destroy-on-close
      align-center
      append-to-body
      modal-class="message-detail-dialog-modal"
    >
      <div v-if="activeMessage" class="detail-container-simple">
        <div class="detail-header-simple">
          <h2 class="detail-title-simple">
            {{ activeMessage.messageTitle || activeMessage.title || '未命名消息' }}
          </h2>
          <div class="detail-meta-simple">
            发送时间: {{ formatDateTime(activeMessage.createTime || activeMessage.createAt) || '最近更新' }}
          </div>
        </div>
        
        <el-divider border-style="dashed" />
        
        <div class="detail-content-simple">
          <div v-if="activeMessage.messageContent" class="html-content" v-html="activeMessage.messageContent"></div>
          <div v-else class="text-content">{{ activeMessage.messageSummary || activeMessage.desc || '暂无详细内容' }}</div>
        </div>

        <div class="detail-actions-simple">
          <el-button v-if="activeMessage.actionType === 'resumeExam' || activeMessage.actionPath" type="primary" size="large" @click="handleAction(activeMessage)">
            {{ activeMessage.actionType === 'resumeExam' ? '继续考试' : '前往处理' }}
          </el-button>
          <el-button v-else type="primary" size="large" @click="detailVisible = false">我已阅读</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, ChatDotRound, Monitor, Search } from '@element-plus/icons-vue'
import {
  buildStaticTaskCards,
  getPortalMessageCenter,
  getPortalMessageOverview,
  getPortalTaskCenter,
  markPortalMessageRead,
  markPortalTaskRead,
} from '@/api/portal'
import usePortalUserStore from '@/store/user'
import { recordTaskFeedback, syncTaskFeedback } from '@/utils/taskFeedback'

type MessageTab = 'todo' | 'message' | 'notice'

interface TaskActionRow {
  dispatchId?: number | string
  paperId?: number | string
  startTime?: string
}

interface TaskAction {
  type?: string
  path?: string
  targetId?: number | string
  recordId?: number | string
  row?: TaskActionRow
}

interface TodoItem {
  key?: string | number
  title?: string
  desc?: string
  status?: string
  action?: TaskAction
}

interface MessageItem {
  messageId?: number | string
  noticeId?: number | string
  messageTitle?: string
  messageSummary?: string
  messageContent?: string
  title?: string
  desc?: string
  createTime?: string
  createAt?: string
  readFlag?: string | number
  noticeType?: string
  messageType?: string
  actionType?: string
  actionPath?: string
  actionTarget?: string | number
  paperId?: string | number
  isRead?: boolean
  messageKind?: 'EXAM_REMINDER' | 'ACHIEVEMENT' | 'COURSE_SELECTION' | 'SYSTEM' | 'NOTICE'
  sourceKind?: 'notice' | 'synthetic'
  canMarkRead?: boolean
}

interface TabOption {
  value: MessageTab
  label: string
  count: number
}

const route = useRoute()
const router = useRouter()
const userStore = usePortalUserStore()

const loading = ref(false)
const keyword = ref('')
const activeTab = ref<MessageTab>('todo')
const taskCenter = ref<any>({})
const overview = ref<any>({})
const portalMessages = ref<MessageItem[]>([])
const localReadMessageIds = ref<string[]>([])
const pageSize = 10
const todoPage = ref(1)
const messagePage = ref(1)
const noticePage = ref(1)

const todoFilter = ref('all')
const messageFilter = ref('unread')
const messageTypeFilter = ref<'all' | 'EXAM_REMINDER' | 'ACHIEVEMENT' | 'COURSE_SELECTION' | 'SYSTEM'>('all')
const noticeFilter = ref('unread')

const detailVisible = ref(false)
const activeMessage = ref<any>(null)

const activeRole = computed(() => {
  const firstSegment = route.path.split('/')[1]
  if (userStore.availablePortalRoles.includes(firstSegment)) {
    return firstSegment
  }
  return userStore.preferredPortalRole || 'student'
})

const messageBasePath = computed(() => `/${activeRole.value}/messages`)

const todoRows = computed<TodoItem[]>(() => (taskCenter.value?.todoTasks || []) as TodoItem[])
const messageRows = computed<MessageItem[]>(() => portalMessages.value)
const noticeRows = computed<MessageItem[]>(() => (overview.value?.notices || []) as MessageItem[])

const normalizedKeyword = computed(() => keyword.value.trim().toLowerCase())

function includesKeyword(item: any) {
  const search = normalizedKeyword.value
  if (!search) return true
  const text = [
    item?.title,
    item?.desc,
    item?.status,
    item?.messageTitle,
    item?.messageSummary,
    item?.messageContent,
  ]
    .filter(Boolean)
    .join(' ')
    .toLowerCase()
  return text.includes(search)
}

function messageReadKey(item: any) {
  return String(item?.messageId || item?.noticeId || item?.actionTarget || '')
}

function normalizeMessageItem(item: any, index: number): MessageItem {
  const key = String(item?.messageId || item?.noticeId || item?.actionTarget || `message-${index}`)
  const hasServerReadState = item?.readFlag !== undefined
  const isSynthetic = !item?.noticeId && !hasServerReadState
  const isRead = hasServerReadState
    ? String(item?.readFlag || '0') === '1'
    : localReadMessageIds.value.includes(key)
  const messageKind = item?.noticeType === '2'
    ? 'NOTICE'
    : item?.messageType === 'COURSE_SELECTION'
      ? 'COURSE_SELECTION'
    : item?.messageType === 'EXAM_REMINDER'
      ? 'EXAM_REMINDER'
      : item?.messageType === 'ACHIEVEMENT'
        ? 'ACHIEVEMENT'
        : 'SYSTEM'
  return {
    ...item,
    messageId: key,
    isRead,
    messageKind,
    sourceKind: isSynthetic ? 'synthetic' : 'notice',
    canMarkRead: !isRead && (Boolean(item?.noticeId) || isSynthetic),
  }
}

function isMessageRead(item: any) {
  return Boolean(item?.isRead)
}

function messageTagType(item: any) {
  if (item?.messageKind === 'ACHIEVEMENT') return 'success'
  if (item?.messageKind === 'EXAM_REMINDER') return 'warning'
  if (item?.messageKind === 'COURSE_SELECTION') return 'primary'
  return 'info'
}

function messageIconTone(item: any) {
  if (item?.messageKind === 'ACHIEVEMENT') return 'success'
  if (item?.messageKind === 'EXAM_REMINDER') return 'warning'
  if (item?.messageKind === 'COURSE_SELECTION') return 'primary'
  return 'info'
}

const filteredTodoRows = computed(() => {
  let list = todoRows.value.filter(includesKeyword)
  if (todoFilter.value === 'pending') {
    list = list.filter((item: any) => item.status !== '已处理' && item.status !== '已完成')
  }
  return list
})
const filteredMessageRows = computed(() => {
  let list = messageRows.value.filter(includesKeyword)
  if (messageTypeFilter.value === 'EXAM_REMINDER' || messageTypeFilter.value === 'ACHIEVEMENT' || messageTypeFilter.value === 'COURSE_SELECTION') {
    list = list.filter((item: any) => item.messageKind === messageTypeFilter.value)
  } else if (messageTypeFilter.value === 'SYSTEM') {
    list = list.filter((item: any) => item.messageKind === 'SYSTEM')
  }
  if (messageFilter.value === 'unread') {
    list = list.filter((item: any) => !isMessageRead(item))
  } else if (messageFilter.value === 'read') {
    list = list.filter((item: any) => isMessageRead(item))
  }
  return list
})
const filteredNoticeRows = computed(() => {
  let list = noticeRows.value.filter(includesKeyword)
  if (noticeFilter.value === 'unread') {
    list = list.filter((item: any) => String(item.readFlag || '0') === '0')
  } else if (noticeFilter.value === 'read') {
    list = list.filter((item: any) => String(item.readFlag || '0') === '1')
  }
  return list
})

const tabOptions = computed<TabOption[]>(() => [
  { value: 'todo' as MessageTab, label: '我的待办', count: Number(taskCenter.value?.stats?.todoCount || todoRows.value.length || 0) },
  { value: 'message' as MessageTab, label: '消息提醒', count: Number(overview.value?.stats?.unreadMessageCount || messageRows.value.length || 0) },
  { value: 'notice' as MessageTab, label: '通知公告', count: Number(overview.value?.stats?.noticeCount || noticeRows.value.length || 0) },
])

const readMessageCount = computed(() => {
  return messageRows.value.filter((item: any) => isMessageRead(item)).length
})
const unreadMessageCount = computed(() => {
  return messageRows.value.filter((item: any) => !isMessageRead(item)).length
})

const unreadNoticeCount = computed(() => {
  return noticeRows.value.filter((item: any) => String(item.readFlag || '0') === '0').length
})

const readNoticeCount = computed(() => {
  return noticeRows.value.filter((item: any) => String(item.readFlag || '0') === '1').length
})

const currentTabLabel = computed(() => {
  return tabOptions.value.find((item) => item.value === activeTab.value)?.label || '消息中心'
})

const currentTabCount = computed(() => {
  return tabOptions.value.find((item) => item.value === activeTab.value)?.count || 0
})

const searchPlaceholder = computed(() => {
  if (activeTab.value === 'todo') return '请输入待办标题或描述'
  if (activeTab.value === 'message') return '请输入消息标题或内容'
  return '请输入通知公告标题或内容'
})

function paginate<T>(list: T[], page: number) {
  const start = (Math.max(page, 1) - 1) * pageSize
  return list.slice(start, start + pageSize)
}

const pagedTodoRows = computed<TodoItem[]>(() => paginate(filteredTodoRows.value, todoPage.value))
const pagedMessageRows = computed<MessageItem[]>(() => paginate(filteredMessageRows.value, messagePage.value))
const pagedNoticeRows = computed<MessageItem[]>(() => paginate(filteredNoticeRows.value, noticePage.value))

function formatDateTime(value?: string | number | Date | null) {
  if (!value) return ''
  const date = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  const hour = `${date.getHours()}`.padStart(2, '0')
  const minute = `${date.getMinutes()}`.padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

function resolveMessageTypeLabel(item: any) {
  if (item?.messageKind === 'EXAM_REMINDER') return '考试提醒'
  if (item?.messageKind === 'ACHIEVEMENT') return '成长提醒'
  if (item?.messageKind === 'COURSE_SELECTION') return '选课服务'
  if (item?.messageKind === 'NOTICE') return '通知公告'
  return '系统消息'
}

function normalizeTab(value?: unknown): MessageTab {
  if (value === 'message' || value === 'notice') return value
  return 'todo'
}

function buildRoleTodoRows(role: string): TodoItem[] {
  if (role === 'teacher') {
    return buildStaticTaskCards([
      { key: 'teacher-message-course', title: '查看授课课程', desc: '查看教学班、学生和授课范围', tag: '课程管理', path: '/teacher/courses', iconType: 'practice' },
      { key: 'teacher-message-schedule', title: '查看教学课表', desc: '按周查看授课安排', tag: '教学课表', path: '/teacher/schedule', iconType: 'practice' },
      { key: 'teacher-message-resource', title: '查看教学资源', desc: '进入资源中心查看资料和详情', tag: '资源中心', path: '/teacher/resources', iconType: 'homework' },
    ]).map((item) => ({ key: item.key, title: item.title, desc: item.statusText, action: item.raw?.action }))
  }
  if (role === 'advisor') {
    return buildStaticTaskCards([
      { key: 'advisor-message-students', title: '查看学生管理', desc: '浏览学生名册和班级档案', tag: '学生管理', path: '/advisor/students', iconType: 'practice' },
      { key: 'advisor-message-scores', title: '查看成绩管理', desc: '查看班级成绩和发布状态', tag: '成绩管理', path: '/advisor/scores', iconType: 'exam' },
      { key: 'advisor-message-dashboard', title: '查看重点班级', desc: '回到辅导员概览关注重点班级', tag: '班级跟进', path: '/advisor/dashboard', iconType: 'homework' },
    ]).map((item) => ({ key: item.key, title: item.title, desc: item.statusText, action: item.raw?.action }))
  }
  if (role === 'parent') {
    return buildStaticTaskCards([
      { key: 'parent-message-courses', title: '查看孩子课程', desc: '查看当前学期课程和教师信息', tag: '课程概览', path: '/parent/courses', iconType: 'practice' },
      { key: 'parent-message-schedule', title: '查看孩子课表', desc: '按周查看孩子课程安排', tag: '课表跟进', path: '/parent/schedule', iconType: 'practice' },
      { key: 'parent-message-dashboard', title: '切换孩子视角', desc: '返回首页切换不同孩子查看', tag: '家庭关系', path: '/parent/dashboard', iconType: 'homework' },
    ]).map((item) => ({ key: item.key, title: item.title, desc: item.statusText, action: item.raw?.action }))
  }
  return []
}

function syncTabFromRoute() {
  activeTab.value = normalizeTab(route.query.tab)
}

function switchTab(tab: MessageTab) {
  activeTab.value = tab
  router.replace({ path: messageBasePath.value, query: { ...route.query, tab } })
}

async function loadData() {
  const userId = userStore.user?.userId
  if (!userId) return
  loading.value = true
  try {
    if (activeRole.value === 'student') {
      const [taskRes, overviewRes, messageRes] = await Promise.all([
        getPortalTaskCenter({ userId }),
        getPortalMessageOverview({ userId }),
        getPortalMessageCenter({ userId, limit: 100, includeRead: true }),
      ])
      taskCenter.value = taskRes.data || {}
      overview.value = overviewRes.data || {}
      portalMessages.value = ((messageRes.data || []) as MessageItem[]).map((item, index) => normalizeMessageItem(item, index))
      return
    }

    const [overviewRes, messageRes] = await Promise.all([
      getPortalMessageOverview({ userId }),
      getPortalMessageCenter({ userId, limit: 100, includeRead: true }),
    ])
    taskCenter.value = { todoTasks: buildRoleTodoRows(activeRole.value) }
    overview.value = overviewRes.data || {}
    portalMessages.value = ((messageRes.data || []) as MessageItem[]).map((item, index) => normalizeMessageItem(item, index))
  } finally {
    loading.value = false
  }
}

async function markMessageRead(item: any, showMessage = true) {
  if (item?.noticeId) {
    await markPortalMessageRead(item.noticeId)
  } else {
    const key = messageReadKey(item)
    if (key && !localReadMessageIds.value.includes(key)) {
      localReadMessageIds.value = [...localReadMessageIds.value, key]
      sessionStorage.setItem('portal-local-read-messages', JSON.stringify(localReadMessageIds.value))
    }
  }
  if (showMessage) {
    ElMessage.success('已标记为已读')
  }
  await loadData()
}

function openMessageAction(item: any) {
  if (!item) return
  
  if (!isMessageRead(item)) {
    markMessageRead(item, false).catch(() => {})
  }
  
  activeMessage.value = item
  detailVisible.value = true
}

function handleAction(item: any) {
  if (!item) return
  if (item.actionType === 'resumeExam' && item.actionTarget) {
    router.push({ path: `/student/exams/session/${item.actionTarget}`, query: { paperId: String(item.paperId || '') } })
    detailVisible.value = false
    return
  }
  if (item.actionType === 'growth') {
    router.push('/account/profile')
    detailVisible.value = false
    return
  }
  if (item.actionPath) {
    router.push(item.actionPath)
    detailVisible.value = false
    return
  }
}

function openTask(task: any) {
  const action = task?.action || {}
  if (action.type === 'route' && action.path) {
    router.push(action.path)
    return
  }
  recordTaskFeedback(task)
  syncTaskFeedback(task, 'messages')
  const dispatchId = Number(action?.row?.dispatchId || 0)
  if (dispatchId) {
    markPortalTaskRead(dispatchId).catch(() => {})
  }
  if (dispatchId) {
    router.push(`/student/tasks/${dispatchId}?from=messages`)
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
        startedAt: String(action.row?.startTime || ''),
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
      path: activeRole.value === 'student' ? '/student/courses' : `/${activeRole.value}/dashboard`,
      query: action.targetId ? { openCourseId: String(action.targetId) } : {},
    })
    return
  }
  router.push(action.path || (activeRole.value === 'student' ? '/student/plaza' : messageBasePath.value))
}

watch(() => route.query.tab, syncTabFromRoute, { immediate: true })
watch(
  () => route.path,
  () => {
    syncTabFromRoute()
    loadData()
  },
)
watch(keyword, () => {
  todoPage.value = 1
  messagePage.value = 1
  noticePage.value = 1
})
watch(todoFilter, () => {
  todoPage.value = 1
})
watch(messageFilter, () => {
  messagePage.value = 1
})
watch(noticeFilter, () => {
  noticePage.value = 1
})
watch(activeTab, () => {
  if (activeTab.value === 'todo') {
    todoPage.value = 1
  } else if (activeTab.value === 'message') {
    messagePage.value = 1
  } else {
    noticePage.value = 1
  }
})
onMounted(loadData)
onMounted(() => {
  try {
    const raw = sessionStorage.getItem('portal-local-read-messages')
    localReadMessageIds.value = raw ? JSON.parse(raw) : []
  } catch {
    localReadMessageIds.value = []
  }
})
</script>

<style scoped>
.message-center-page {
  /* padding and bg handled by utility classes now */
  min-height: 100vh;
}

.message-center-wrapper {
  max-width: 100%;
  margin: 0;
  padding: 0;
}

.message-center-container {
  display: flex;
  align-items: flex-start;
  min-height: calc(100vh - 60px); /* adjusted for portal header */
}

.message-center-sidebar {
  width: 240px;
  background: #fcfcfc;
  flex-shrink: 0;
  min-height: calc(100vh - 60px);
  padding: 0;
  border-right: 1px solid #ebeef5;
}

.sidebar-menu {
  display: flex;
  flex-direction: column;
  padding: 0;
}

.sidebar-item {
  display: flex;
  align-items: center;
  padding: 24px;
  cursor: pointer;
  color: var(--portal-text-secondary);
  font-size: 15px;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.sidebar-item:hover {
  background: #f5f7fa;
}

.sidebar-item.is-active {
  background: #eef2fa;
  color: var(--portal-brand);
  border-left-color: var(--portal-brand);
}

.item-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  margin-right: 12px;
  font-size: 18px;
  background: #e6e8eb;
  border-radius: 8px;
  color: #909399;
}

.sidebar-item.is-active .item-icon-wrapper {
  background: var(--portal-brand);
  color: #ffffff;
}

.item-label {
  flex: 1;
  font-weight: 500;
}

.message-center-content {
  flex: 1;
  padding: 32px;
  background: #ffffff;
  min-height: calc(100vh - 60px);
  position: relative;
  display: flex;
  flex-direction: column;
}

.content-header {
  margin-bottom: 24px;
}

.header-title h3 {
  margin: 0;
  font-size: 18px;
  color: var(--portal-text);
  font-weight: 600;
}

.content-body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.message-filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 16px;
}

.filter-bar-stack {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.header-actions {
  display: flex;
  align-items: center;
}

.search-input {
  width: 280px;
}

.filter-group-pill {
  display: inline-flex;
  background-color: #f4f4f5;
  border-radius: 20px;
  padding: 4px;
}

.filter-pill {
  padding: 6px 24px;
  border-radius: 16px;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-pill.is-active {
  background-color: var(--portal-brand);
  color: #fff;
  font-weight: 500;
}

.message-type-filter {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.message-type-chip {
  padding: 5px 12px;
  border-radius: 999px;
  border: 1px solid #dcdfe6;
  background: #fff;
  color: #606266;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.message-type-chip.is-active {
  border-color: var(--portal-brand);
  color: var(--portal-brand);
  background: #eff6ff;
}

.message-list-simple {
  display: flex;
  flex-direction: column;
}

.message-list-rich {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item-simple {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px dashed #ebeef5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.message-card {
  padding: 18px 20px;
  border: 1px solid #ebeef5;
  border-radius: 14px;
  background: #ffffff;
  cursor: pointer;
  transition: all 0.25s ease;
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.message-card:hover {
  border-color: #c6e2ff;
  box-shadow: 0 10px 24px rgba(38, 111, 203, 0.08);
  transform: translateY(-2px);
}

.message-card__main {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.message-card__icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
  border: 1px solid #e5e7eb;
}

.message-card__icon.is-warning {
  background: #fff7ed;
  color: #f59e0b;
}

.message-card__icon.is-success {
  background: #f0fdf4;
  color: #22c55e;
}

.message-card__icon.is-primary {
  background: #eff6ff;
  color: #2563eb;
}

.message-card__icon.is-info {
  background: #eff6ff;
  color: #3b82f6;
}

.message-card__head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.message-card__title {
  font-size: 16px;
  color: var(--portal-text);
  font-weight: 600;
  line-height: 1.5;
}

.message-card__time {
  font-size: 13px;
  color: var(--portal-text-secondary);
  white-space: nowrap;
}

.message-card__meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.message-card__summary {
  color: #606266;
  font-size: 14px;
  line-height: 1.8;
}

.message-item-simple:hover {
  background-color: #fafafa;
}

.message-item-title {
  font-size: 14px;
  color: var(--portal-text);
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding-right: 24px;
}

.message-item-title.is-unread-text {
  font-weight: 600;
  color: #303133;
}

.message-item-time {
  font-size: 13px;
  color: var(--portal-text-secondary);
  white-space: nowrap;
  flex-shrink: 0;
}

.message-list-pagination {
  margin-top: auto;
  display: flex;
  justify-content: center;
  padding-top: 32px;
  padding-bottom: 24px;
}

@media (max-width: 960px) {
  .message-center-container {
    flex-direction: column;
  }

  .message-center-sidebar {
    width: 100%;
    position: static;
    min-height: auto;
  }

  .sidebar-menu {
    flex-direction: row;
    overflow-x: auto;
    padding: 0;
  }

  .sidebar-item {
    white-space: nowrap;
    padding: 12px 20px;
  }

  .message-center-content {
    margin-left: 0;
    margin-top: 16px;
    padding: 24px;
  }

  .message-filter-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
  }

  .search-input {
    width: 100%;
  }
}

/* 详情弹窗样式 */
.message-detail-dialog-simple :deep(.el-dialog__body) {
  padding: 0 32px 32px;
}

:global(.message-detail-dialog-simple.el-overlay-dialog) {
  z-index: 3000;
}

:global(.message-detail-dialog-modal) {
  z-index: 2999 !important;
}

.detail-container-simple {
  display: flex;
  flex-direction: column;
}

.detail-header-simple {
  margin-bottom: 24px;
  text-align: center;
  position: relative;
}

.detail-title-simple {
  margin: 0 0 16px 0;
  font-size: 22px;
  color: #303133;
  font-weight: 600;
  line-height: 1.4;
}

.detail-meta-simple {
  color: #909399;
  font-size: 13px;
  text-align: right;
}

.detail-content-simple {
  padding: 24px 0;
  min-height: 120px;
  max-height: 400px;
  overflow-y: auto;
}

.text-content {
  font-size: 15px;
  line-height: 1.8;
  color: var(--portal-text);
  white-space: pre-wrap;
}

.html-content {
  font-size: 15px;
  line-height: 1.8;
  color: var(--portal-text);
}

.html-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 8px 0;
}

.html-content :deep(p) {
  margin: 0 0 1em 0;
}

.detail-actions-simple {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}
.detail-actions-simple .el-button {
  width: 160px;
}
</style>
