import { computed, reactive, ref, watch, type Ref } from 'vue'
import { sortTasksWithFeedback } from '@/utils/taskFeedback'

export interface PlazaFilters {
  search: string
  taskType: string
  status: string
  recommendationBucket: string
}

const TASK_TYPE_LABEL_MAP: Record<string, string> = {
  HOMEWORK: '作业',
  EXAM: '考试',
  PRACTICE: '实验',
  READING: '阅读',
  COURSE: '课程',
  WRONG: '错题',
}

export const taskTypeOptions = [
  { label: '作业任务', value: 'HOMEWORK' },
  { label: '考试任务', value: 'EXAM' },
  { label: '实验任务', value: 'PRACTICE' },
  { label: '阅读任务', value: 'READING' },
  { label: '课程任务', value: 'COURSE' },
]

export const statusOptions = [
  { label: '待完成', value: 'PENDING' },
  { label: '进行中', value: 'IN_PROGRESS' },
  { label: '已完成', value: 'COMPLETED' },
]

export function usePlazaFilters(taskCenter: Ref<any>) {
  const filters = reactive<PlazaFilters>({
    search: '',
    taskType: '',
    status: '',
    recommendationBucket: '',
  })

  const activeTab = ref<'todo' | 'recommended' | 'history'>('todo')
  const historyPage = ref(1)
  const historyPageSize = 20

  watch(() => [filters.search, filters.taskType, filters.status], () => {
    historyPage.value = 1
  })

  const todoTasks = computed(() => taskCenter.value.todoTasks || [])
  const recommendedTasks = computed(() => taskCenter.value.recommendedTasks || [])
  const historyTasks = computed(() => taskCenter.value.historyTasks || [])
  const recommendationGroups = computed(() => taskCenter.value.recommendationGroups || [])

  function matchesFilters(task: any): boolean {
    if (filters.search) {
      const q = filters.search.toLowerCase()
      const haystack = `${task.title || ''} ${task.desc || ''} ${task.tag || ''}`.toLowerCase()
      if (!haystack.includes(q)) return false
    }
    if (filters.taskType) {
      const taskType = String(task.taskType || task.action?.type || '').toUpperCase()
      const tag = String(task.tag || '').toUpperCase()
      const label = TASK_TYPE_LABEL_MAP[filters.taskType] || '___'
      if (taskType !== filters.taskType && !tag.includes(label)) {
        return false
      }
    }
    if (filters.status) {
      const status = String(task.completionStatus || task.status || '').toUpperCase()
      if (status !== filters.status) return false
    }
    return true
  }

  const filteredTodoTasks = computed(() => todoTasks.value.filter(matchesFilters))
  const filteredRecommendedTasks = computed(() => {
    let list = recommendedTasks.value.filter(matchesFilters)
    if (filters.recommendationBucket) {
      list = list.filter((t: any) =>
        String(t.recommendationBucket || t.sourceType || '').toLowerCase() ===
        filters.recommendationBucket.toLowerCase(),
      )
    }
    return sortTasksWithFeedback(list)
  })
  const filteredHistoryTasks = computed(() => historyTasks.value.filter(matchesFilters))
  const paginatedHistoryTasks = computed(() => {
    const start = (historyPage.value - 1) * historyPageSize
    return filteredHistoryTasks.value.slice(start, start + historyPageSize)
  })

  function handleKpiClick(kpiType: string) {
    filters.search = ''
    filters.status = ''
    filters.recommendationBucket = ''
    switch (kpiType) {
      case 'todo':
        activeTab.value = 'todo'
        filters.taskType = ''
        break
      case 'homework':
        activeTab.value = 'todo'
        filters.taskType = 'HOMEWORK'
        break
      case 'exam':
        activeTab.value = 'todo'
        filters.taskType = 'EXAM'
        break
      case 'challenge':
        activeTab.value = 'recommended'
        filters.taskType = 'EXAM'
        break
    }
  }

  function handleCategoryClick(type: string) {
    filters.search = ''
    filters.status = ''
    filters.recommendationBucket = ''
    if (type === 'history') {
      activeTab.value = 'history'
      filters.taskType = ''
    } else {
      activeTab.value = 'todo'
      filters.taskType = filters.taskType === type ? '' : type
    }
  }

  function toggleRecommendationFilter(bucketKey: string) {
    filters.recommendationBucket =
      filters.recommendationBucket === bucketKey ? '' : bucketKey
  }

  return {
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
  }
}
