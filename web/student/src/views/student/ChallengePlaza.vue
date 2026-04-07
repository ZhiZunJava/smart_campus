<template>
  <div class="portal-page plaza-page" v-loading="loading">
    <div class="portal-page-header">
      <div class="portal-page-title">
        <div class="portal-page-title-icon"></div>
        <span>任务广场</span>
      </div>
      <div class="portal-page-actions">
        <el-tag effect="light" type="primary" class="page-meta-tag">按优先级与截止时间处理任务</el-tag>
      </div>
    </div>

    <PlazaKpiBar
      :todo-count="todoTasks.length"
      :homework-count="homeworkCount"
      :exam-count="examCount"
      :challenge-count="generalPapers.length"
      @kpi-click="handleKpiClick"
    />

    <PlazaCategoryCards
      :buckets="categoryBuckets"
      :active-type="filters.taskType"
      @category-click="handleCategoryClick"
    />

    <PlazaFilterBar
      v-model:search="filters.search"
      v-model:task-type="filters.taskType"
      v-model:status="filters.status"
      :type-options="taskTypeOptions"
      :status-options="statusOptions"
    />

    <el-tabs v-model="activeTab" class="plaza-tabs">
      <el-tab-pane name="todo">
        <template #label>
          待办任务
          <span v-if="filteredTodoTasks.length" class="tab-badge">{{ filteredTodoTasks.length }}</span>
        </template>
        <div class="task-list">
          <TaskCard
            v-for="task in displayTodoTasks"
            :key="task.key"
            :task="task"
            variant="todo"
            @click="openTask(task)"
          />
        </div>
        <el-empty v-if="!filteredTodoTasks.length" description="没有匹配的待办任务" :image-size="80" />
      </el-tab-pane>

      <el-tab-pane name="recommended">
        <template #label>
          推荐任务
          <span v-if="filteredRecommendedTasks.length" class="tab-badge">{{ filteredRecommendedTasks.length }}</span>
        </template>
        <div v-if="recommendationGroups.length" class="recommendation-chips">
          <button
            v-for="group in recommendationGroups"
            :key="group.key"
            class="recommendation-chip"
            :class="{ 'is-active': filters.recommendationBucket === group.key }"
            @click="toggleRecommendationFilter(group.key)"
          >
            {{ group.label }}
            <span class="chip-count">{{ group.count }}</span>
          </button>
        </div>
        <div class="task-list">
          <TaskCard
            v-for="task in displayRecommendedTasks"
            :key="task.key"
            :task="task"
            variant="recommended"
            @click="openTask(task)"
          />
        </div>
        <el-empty v-if="!filteredRecommendedTasks.length" description="没有匹配的推荐任务" :image-size="80" />
      </el-tab-pane>

      <el-tab-pane name="history">
        <template #label>
          历史记录
          <span v-if="filteredHistoryTasks.length" class="tab-badge">{{ filteredHistoryTasks.length }}</span>
        </template>
        <div class="task-list">
          <TaskCard
            v-for="task in displayPaginatedHistory"
            :key="task.key"
            :task="task"
            variant="history"
            @click="openTask(task)"
          />
        </div>
        <el-empty v-if="!filteredHistoryTasks.length" description="没有匹配的历史任务" :image-size="80" />
        <el-pagination
          v-if="filteredHistoryTasks.length > historyPageSize"
          class="history-pagination"
          layout="prev, pager, next, total"
          :total="filteredHistoryTasks.length"
          :page-size="historyPageSize"
          v-model:current-page="historyPage"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  getPortalTaskCenter,
  listExamRecord,
  markPortalTaskRead,
} from '@/api/portal'
import usePortalUserStore from '@/store/user'
import { recordTaskFeedback, syncTaskFeedback } from '@/utils/taskFeedback'
import { usePlazaFilters, taskTypeOptions, statusOptions } from './composables/usePlazaFilters'
import PlazaKpiBar from './components/PlazaKpiBar.vue'
import PlazaCategoryCards from './components/PlazaCategoryCards.vue'
import PlazaFilterBar from './components/PlazaFilterBar.vue'
import TaskCard from './components/TaskCard.vue'

const router = useRouter()
const userStore = usePortalUserStore()
const loading = ref(false)
const taskCenter = ref<any>({})
const examRecords = ref<any[]>([])

const {
  filters,
  activeTab,
  historyPage,
  historyPageSize,
  todoTasks,
  recommendedTasks,
  historyTasks,
  recommendationGroups,
  filteredTodoTasks,
  filteredRecommendedTasks,
  filteredHistoryTasks,
  paginatedHistoryTasks,
  handleKpiClick,
  handleCategoryClick,
  toggleRecommendationFilter,
} = usePlazaFilters(taskCenter)

const examTasks = computed(() => taskCenter.value.examTasks || [])
const generalPapers = computed(() => examTasks.value.filter((item: any) => item?.action?.paperId && item?.desc?.includes('开放考试')))

const homeworkCount = computed(() => {
  const statsCount = Number(taskCenter.value.stats?.homeworkTaskCount || 0)
  if (statsCount > 0) return statsCount
  const source = [...todoTasks.value, ...recommendedTasks.value]
  return source.filter((item: any) => {
    const tag = String(item?.tag || '').toUpperCase()
    const type = String(item?.taskType || item?.action?.type || '').toUpperCase()
    const text = `${item?.title || ''} ${item?.desc || ''} ${item?.tag || ''}`.toUpperCase()
    return tag.includes('作业') || type === 'HOMEWORK' || text.includes('作业')
  }).length
})

const examCount = computed(() => {
  const statsCount = Number(taskCenter.value.stats?.examTaskCount || 0)
  if (statsCount > 0) return statsCount
  const source = [...todoTasks.value, ...recommendedTasks.value, ...examTasks.value]
  return source.filter((item: any) => {
    const tag = String(item?.tag || '').toUpperCase()
    const type = String(item?.taskType || item?.action?.type || '').toUpperCase()
    const text = `${item?.title || ''} ${item?.desc || ''} ${item?.tag || ''}`.toUpperCase()
    return type === 'EXAM' || type === 'RESUME' || tag.includes('考试') || text.includes('考试')
  }).length
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

const categoryBuckets = computed(() => [
  { type: 'EXAM', label: '考试任务', count: taskCenter.value.stats?.examTaskCount || 0, icon: 'ri-file-paper-2-line' },
  { type: 'WRONG', label: '错题任务', count: taskCenter.value.stats?.wrongTaskCount || 0, icon: 'ri-error-warning-line' },
  { type: 'COURSE', label: '课程任务', count: taskCenter.value.stats?.courseTaskCount || 0, icon: 'ri-book-open-line' },
  { type: 'history', label: '历史任务', count: historyTasks.value.length, icon: 'ri-history-line' },
])

const displayTodoTasks = computed(() => filteredTodoTasks.value.map((item: any) => decorateTask(item, '立即处理')))
const displayRecommendedTasks = computed(() => filteredRecommendedTasks.value.map((item: any) => decorateTask(item, '查看详情')))
const displayPaginatedHistory = computed(() => paginatedHistoryTasks.value.map((item: any) => decorateTask(item, '查看')))

const TAG_CN_MAP: Record<string, string> = {
  exam: '考试', homework: '作业', practice: '实验', reading: '阅读',
  course: '课程', wrongbook: '错题', record: '记录', resume: '继续',
  EXAM: '考试', HOMEWORK: '作业', PRACTICE: '实验', READING: '阅读',
  COURSE: '课程', WRONG: '错题',
}
const PRIORITY_CN_MAP: Record<string, string> = {
  LOW: '低', NORMAL: '普通', MEDIUM: '中', HIGH: '高', URGENT: '紧急',
  P0: '紧急', P1: '高', P2: '中', P3: '低', P4: '低',
  low: '低', normal: '普通', medium: '中', high: '高', urgent: '紧急',
}
const STATUS_CN_MAP: Record<string, string> = {
  PENDING: '待完成', IN_PROGRESS: '进行中', COMPLETED: '已完成',
  pending: '待完成', in_progress: '进行中', completed: '已完成',
}

function translateTag(raw?: string) {
  if (!raw) return ''
  const trimmed = raw.trim()
  return TAG_CN_MAP[trimmed] || PRIORITY_CN_MAP[trimmed] || STATUS_CN_MAP[trimmed] || trimmed
}

function decorateTask(task: any, fallbackActionLabel: string) {
  const actionType = String(task?.action?.type || '').toLowerCase()
  const paperId = String(task?.action?.targetId || task?.action?.paperId || task?.action?.row?.paperId || '')
  const attemptCount = paperId ? (examAttemptCountMap.value[paperId] || 0) : 0
  const latestRecord = paperId ? latestExamRecordMap.value[paperId] : null
  const maxAttemptCount = Number(task?.maxAttemptCount || 0)
  const isRetakeExam = actionType === 'exam' && attemptCount > 0
  const isMakeupExam = isRetakeExam && maxAttemptCount > 0
  const remainingCount = maxAttemptCount > 0 ? Math.max(0, maxAttemptCount - attemptCount) : null
  const rawTag = task?.tag
  const translatedTag = isMakeupExam ? '可补考' : isRetakeExam ? '可再次参加考试' : translateTag(rawTag)
  const rawStatus = task?.status
  const translatedStatus = STATUS_CN_MAP[rawStatus] || rawStatus
  return {
    ...task,
    raw: task,
    status: translatedStatus || task?.status,
    tag: translatedTag || rawTag,
    desc: isMakeupExam
      ? `你已参加 ${attemptCount}/${maxAttemptCount} 次，还可补考 ${remainingCount} 次`
      : isRetakeExam
        ? `你已参加 ${attemptCount} 次，可再次作答`
        : task?.desc,
    meta: [
      ...(Array.isArray(task?.meta) ? task.meta : []),
      ...(isMakeupExam && remainingCount !== null ? [`剩余补考次数：${remainingCount}`] : []),
      ...(isRetakeExam && latestRecord?.submitTime ? [`最近参加：${String(latestRecord.submitTime).slice(0, 16).replace('T', ' ')}`] : []),
    ],
    actionLabel: actionType === 'resume' ? '继续作答' : isMakeupExam ? '去补考' : isRetakeExam ? '再次考试' : fallbackActionLabel,
    recommendationReason: String(task?.recommendationReason || '').trim(),
  }
}

function openTask(task: any) {
  const rawTask = task?.raw || task
  recordTaskFeedback(rawTask)
  syncTaskFeedback(rawTask, 'plaza')
  const action = rawTask?.action || {}
  const dispatchId = Number(action?.row?.dispatchId || rawTask?.dispatchId || action?.dispatchId || 0)
  if (dispatchId) {
    markPortalTaskRead(dispatchId).catch(() => {})
    router.push(`/student/tasks/${dispatchId}?from=plaza`)
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
      path: '/student/courses',
      query: action.targetId ? { openCourseId: String(action.targetId) } : {},
    })
    return
  }
  router.push(action.path || '/student/exams')
}

async function loadData() {
  const userId = userStore.user?.userId
  if (!userId) return
  loading.value = true
  try {
    const [taskRes, recordRes] = await Promise.all([
      getPortalTaskCenter({ userId }),
      listExamRecord({ pageNum: 1, pageSize: 50 }),
    ])
    examRecords.value = recordRes.rows || []
    taskCenter.value = taskRes.data || {}
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.plaza-page {
  padding: 24px 24px 40px;
}

.portal-page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.portal-page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.portal-page-title-icon {
  width: 4px;
  height: 20px;
  background: var(--brand-primary, #266fcb);
  border-radius: 2px;
}

.plaza-tabs {
  background: #fff;
  border-radius: 6px;
  border: 1px solid #ebeef5;
  padding: 0 24px 24px;
}

.plaza-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.plaza-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
}

.tab-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  margin-left: 6px;
  background: var(--el-color-primary-light-9, #ecf5ff);
  color: var(--brand-primary, #266fcb);
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommendation-chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.recommendation-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 6px;
  border: 1px solid #e5edf8;
  background: linear-gradient(180deg, #f7fbff 0%, #ffffff 100%);
  cursor: pointer;
  font-size: 13px;
  color: #5f6c7b;
  transition: all 0.2s ease;
  outline: none;
}

.recommendation-chip:hover {
  border-color: #c6e2ff;
  color: #303133;
}

.recommendation-chip:focus-visible {
  box-shadow: 0 0 0 2px var(--brand-primary, #266fcb);
}

.recommendation-chip.is-active {
  border-color: var(--brand-primary, #266fcb);
  background: var(--el-color-primary-light-9, #ecf5ff);
  color: var(--brand-primary, #266fcb);
  font-weight: 500;
}

.chip-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: rgba(38, 111, 203, 0.1);
  color: var(--brand-primary, #266fcb);
  border-radius: 9px;
  font-size: 11px;
  font-weight: 600;
}

.is-active .chip-count {
  background: var(--brand-primary, #266fcb);
  color: #fff;
}

.history-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* ---- Responsive ---- */
@media (max-width: 768px) {
  .plaza-page { padding: 14px 12px 24px; }
  .portal-page-header { margin-bottom: 16px; }
  .portal-page-title { font-size: 17px; gap: 8px; }
  .plaza-tabs { padding: 0 14px 14px; }
  .plaza-tabs :deep(.el-tabs__item) { font-size: 14px; }
  .recommendation-chips { gap: 6px; margin-bottom: 12px; }
  .recommendation-chip { padding: 5px 10px; font-size: 12px; }
}

@media (max-width: 640px) {
  .plaza-page { padding: 10px 8px 20px; }
  .portal-page-title { font-size: 16px; }
  .plaza-tabs { padding: 0 10px 10px; }
  .recommendation-chip { padding: 4px 8px; font-size: 11px; }
}
</style>
