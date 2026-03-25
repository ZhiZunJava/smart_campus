import { reportPortalTaskFeedback } from '@/api/portal'

const STORAGE_KEY = 'portal-task-feedback:v1'
const FEEDBACK_WINDOW_MS = 1000 * 60 * 60 * 24 * 7
const RECENT_OPEN_COOLDOWN_MS = 1000 * 60 * 60 * 6

type FeedbackRecord = {
  openedAt: number
  sourceType: string
}

type FeedbackStore = {
  taskOpens: Record<string, FeedbackRecord>
  sourceOpens: Record<string, number>
}

function defaultStore(): FeedbackStore {
  return {
    taskOpens: {},
    sourceOpens: {},
  }
}

function readStore(): FeedbackStore {
  if (typeof window === 'undefined') return defaultStore()
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY)
    if (!raw) return defaultStore()
    const parsed = JSON.parse(raw)
    return {
      taskOpens: parsed?.taskOpens && typeof parsed.taskOpens === 'object' ? parsed.taskOpens : {},
      sourceOpens: parsed?.sourceOpens && typeof parsed.sourceOpens === 'object' ? parsed.sourceOpens : {},
    }
  } catch {
    return defaultStore()
  }
}

function writeStore(store: FeedbackStore) {
  if (typeof window === 'undefined') return
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(store))
}

function sourceTypeOf(task: any) {
  return String(task?.sourceType || task?.recommendationBucket || task?.action?.type || 'other').toLowerCase()
}

function keyOf(task: any) {
  return String(task?.key || '')
}

function cleanupStore(store: FeedbackStore) {
  const now = Date.now()
  const nextTaskOpens: Record<string, FeedbackRecord> = {}
  Object.entries(store.taskOpens || {}).forEach(([key, value]) => {
    if (!value?.openedAt || now - value.openedAt > FEEDBACK_WINDOW_MS) return
    nextTaskOpens[key] = value
  })
  const nextSourceOpens: Record<string, number> = {}
  Object.values(nextTaskOpens).forEach((value) => {
    const sourceType = String(value.sourceType || 'other')
    nextSourceOpens[sourceType] = (nextSourceOpens[sourceType] || 0) + 1
  })
  return {
    taskOpens: nextTaskOpens,
    sourceOpens: nextSourceOpens,
  }
}

export function recordTaskFeedback(task: any) {
  const key = keyOf(task)
  if (!key || typeof window === 'undefined') return
  const store = cleanupStore(readStore())
  const sourceType = sourceTypeOf(task)
  store.taskOpens[key] = {
    openedAt: Date.now(),
    sourceType,
  }
  store.sourceOpens[sourceType] = (store.sourceOpens[sourceType] || 0) + 1
  writeStore(cleanupStore(store))
}

export function syncTaskFeedback(task: any, page: string) {
  const action = task?.action || {}
  reportPortalTaskFeedback({
    feedbackType: 'OPEN',
    page,
    taskKey: keyOf(task),
    sourceType: sourceTypeOf(task),
    courseId: task?.action?.row?.courseId || task?.courseId || null,
    targetId: action?.targetId || action?.row?.paperId || action?.row?.wrongId || action?.row?.dispatchId || null,
  }).catch(() => {})
}

export function taskFeedbackScore(task: any) {
  const key = keyOf(task)
  if (!key || typeof window === 'undefined') return 0
  const store = cleanupStore(readStore())
  const sourceType = sourceTypeOf(task)
  const taskOpen = store.taskOpens[key]
  const sourceCount = Number(store.sourceOpens[sourceType] || 0)
  let score = Math.min(sourceCount * 3, 12)
  if (taskOpen?.openedAt) {
    const delta = Date.now() - taskOpen.openedAt
    if (delta <= RECENT_OPEN_COOLDOWN_MS) {
      score -= 6
    } else {
      score += 2
    }
  }
  return score
}

export function sortTasksWithFeedback(tasks: any[]) {
  return [...tasks].sort((left, right) => {
    const leftScore = Number(left?.score || 0) + taskFeedbackScore(left)
    const rightScore = Number(right?.score || 0) + taskFeedbackScore(right)
    if (leftScore !== rightScore) return rightScore - leftScore
    return String(left?.key || '').localeCompare(String(right?.key || ''))
  })
}
