<template>
  <div class="portal-page plaza-page" style="padding-bottom: 40px;">
    <div class="portal-page-header">
      <div class="portal-page-title">
        <div class="portal-page-title-icon"></div>
        <span>任务广场</span>
      </div>
      <div class="portal-page-actions">
        <el-tag effect="light" type="primary" class="page-meta-tag">按优先级与截止时间处理任务</el-tag>
      </div>
    </div>

    <section class="plaza-kpis">
      <div class="plaza-kpi-card">
        <div class="kpi-content">
          <div class="kpi-label">今日待办</div>
          <div class="kpi-value text-primary">{{ todoTasks.length }}</div>
          <div class="kpi-sub">紧急与即将截止</div>
        </div>
      </div>
      <div class="plaza-kpi-card">
        <div class="kpi-content">
          <div class="kpi-label">本周作业</div>
          <div class="kpi-value">{{ homeworkCount }}</div>
          <div class="kpi-sub">需本周内提交</div>
        </div>
      </div>
      <div class="plaza-kpi-card">
        <div class="kpi-content">
          <div class="kpi-label">即将考试</div>
          <div class="kpi-value">{{ examCount }}</div>
          <div class="kpi-sub">近3天内考试</div>
        </div>
      </div>
      <div class="plaza-kpi-card">
        <div class="kpi-content">
          <div class="kpi-label">开放挑战</div>
          <div class="kpi-value">{{ generalPapers.length }}</div>
          <div class="kpi-sub">不依赖课程即可参与</div>
        </div>
      </div>
    </section>

    <div class="portal-grid portal-grid-2 mt20">
      <div class="plaza-section-card">
        <div class="section-header">
          <div class="section-title">今日待办</div>
        </div>
        <div class="task-list">
          <article v-for="task in displayTodoTasks" :key="task.key" class="task-item task-item--todo">
            <div class="task-item-main">
              <div class="task-item-head">
                <div class="task-item-title">{{ task.title }}</div>
                <el-tag :type="task.tagType" effect="plain" size="small">{{ task.tag }}</el-tag>
              </div>
              <div class="task-item-desc">{{ task.desc }}</div>
              <div class="task-item-meta">
                <span v-for="meta in task.meta" :key="meta">{{ meta }}</span>
              </div>
            </div>
            <div class="task-item-actions">
              <el-button type="primary" @click="openTask(task)">{{ task.actionLabel }}</el-button>
            </div>
          </article>
        </div>
        <el-empty v-if="!todoTasks.length" description="今天没有待办任务" :image-size="80" />
      </div>

      <div class="plaza-section-card plaza-section-card--soft">
        <div class="section-header">
          <div class="section-title">推荐任务</div>
        </div>
        <div v-if="recommendationGroups.length" class="recommendation-groups">
          <article v-for="group in recommendationGroups" :key="group.key" class="recommendation-group-card">
            <div class="recommendation-group-head">
              <span class="recommendation-group-title">{{ group.label }}</span>
              <span class="count-badge" :class="{ 'is-zero': Number(group.count || 0) === 0 }">{{ group.count }}</span>
            </div>
            <div class="recommendation-group-desc">{{ group.desc }}</div>
            <div v-if="group.topReason" class="recommendation-group-reason">{{ group.topReason }}</div>
          </article>
        </div>
        <div class="task-list">
          <article v-for="task in displayRecommendedTasks" :key="task.key" class="task-item">
            <div class="task-item-main">
              <div class="task-item-head">
                <div class="task-item-title">{{ task.title }}</div>
                <el-tag :type="task.tagType" effect="plain" size="small">{{ task.tag }}</el-tag>
              </div>
              <div class="task-item-desc">{{ task.desc }}</div>
              <div class="task-item-meta">
                <span v-for="meta in task.meta" :key="meta">{{ meta }}</span>
              </div>
              <div v-if="task.recommendationReason" class="task-item-reason">{{ task.recommendationReason }}</div>
            </div>
            <div class="task-item-actions">
              <el-button type="primary" plain @click="openTask(task)">{{ task.actionLabel }}</el-button>
            </div>
          </article>
        </div>
        <el-empty v-if="!recommendedTasks.length" description="暂无推荐任务" :image-size="80" />
      </div>
    </div>

    <div class="portal-grid portal-grid-2 mt20">
      <div class="plaza-section-card">
        <div class="section-header">
          <div class="section-title">任务分类</div>
        </div>
        <el-table :data="taskBuckets" style="width: 100%" class="custom-table" :header-cell-style="{ background: '#fafafa', color: '#606266', fontWeight: 500 }">
          <el-table-column prop="label" label="任务类型" min-width="120" />
          <el-table-column prop="count" label="数量" min-width="100">
            <template #default="scope">
              <span class="count-badge" :class="{'is-zero': scope.row.count === 0}">{{ scope.row.count }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="desc" label="说明" min-width="200" show-overflow-tooltip />
        </el-table>
      </div>

      <div class="plaza-section-card">
        <div class="section-header">
          <div class="section-title">历史任务</div>
        </div>
        <el-table :data="historyTasks" style="width: 100%" class="custom-table" :header-cell-style="{ background: '#fafafa', color: '#606266', fontWeight: 500 }">
          <el-table-column prop="title" label="任务标题" min-width="180" show-overflow-tooltip />
          <el-table-column prop="tag" label="类型" min-width="90" />
          <el-table-column prop="status" label="状态" min-width="100">
            <template #default="scope">
              <el-tag size="small" type="info" effect="plain">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="90" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="openTask(scope.row)">查看</el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="暂无历史任务" :image-size="60" />
          </template>
        </el-table>
      </div>
    </div>
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
import { recordTaskFeedback, sortTasksWithFeedback, syncTaskFeedback } from '@/utils/taskFeedback'

const router = useRouter()
const userStore = usePortalUserStore()
const loading = ref(false)
const taskCenter = ref<any>({})
const examRecords = ref<any[]>([])

const todoTasks = computed(() => taskCenter.value.todoTasks || [])
const recommendedTasks = computed(() => taskCenter.value.recommendedTasks || [])
const historyTasks = computed(() => taskCenter.value.historyTasks || [])
const examTasks = computed(() => taskCenter.value.examTasks || [])
const wrongTasks = computed(() => taskCenter.value.wrongTasks || [])
const courseTasks = computed(() => taskCenter.value.courseTasks || [])
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
const displayTodoTasks = computed(() => todoTasks.value.map((item: any) => decorateTask(item, '立即处理')))
const displayRecommendedTasks = computed(() => sortTasksWithFeedback(recommendedTasks.value).map((item: any) => decorateTask(item, '查看详情')))
const recommendationGroups = computed(() => taskCenter.value.recommendationGroups || [])
const taskBuckets = computed(() => [
  { label: '考试任务', count: taskCenter.value.stats?.examTaskCount || 0, desc: '来自课程考试与开放考试' },
  { label: '错题任务', count: taskCenter.value.stats?.wrongTaskCount || 0, desc: '来自错题回练与薄弱项复盘' },
  { label: '课程任务', count: taskCenter.value.stats?.courseTaskCount || 0, desc: '来自当前学期课程详情' },
  { label: '历史任务', count: historyTasks.value.length, desc: '已经完成或已提交的任务记录' },
])

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

function decorateTask(task: any, fallbackActionLabel: string) {
  const actionType = String(task?.action?.type || '').toLowerCase()
  const paperId = String(task?.action?.targetId || task?.action?.paperId || task?.action?.row?.paperId || '')
  const attemptCount = paperId ? (examAttemptCountMap.value[paperId] || 0) : 0
  const latestRecord = paperId ? latestExamRecordMap.value[paperId] : null
  const maxAttemptCount = Number(task?.maxAttemptCount || 0)
  const isRetakeExam = actionType === 'exam' && attemptCount > 0
  const isMakeupExam = isRetakeExam && maxAttemptCount > 0
  const remainingCount = maxAttemptCount > 0 ? Math.max(0, maxAttemptCount - attemptCount) : null
  return {
    ...task,
    raw: task,
    tag: isMakeupExam ? '可补考' : isRetakeExam ? '可再次参加考试' : task?.tag,
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

.plaza-kpis {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.plaza-kpi-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  padding: 24px;
  display: flex;
  align-items: flex-start;
  transition: all 0.3s ease;
}

.plaza-kpi-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.kpi-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
  height: 100%;
  width: 100%;
}

.kpi-label {
  font-size: 14px;
  color: #606266;
}

.kpi-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.kpi-value.text-primary {
  color: var(--brand-primary, #266fcb);
}

.kpi-sub {
  font-size: 12px;
  color: #909399;
}

.plaza-section-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  padding: 24px;
  height: 100%;
}

.plaza-page > .portal-grid:last-of-type {
  margin-bottom: 16px;
}

.plaza-section-card--soft {
  background: #fafafa;
  border: 1px solid #e4e7ed;
}

.section-header {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.recommendation-groups {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 18px;
}

.recommendation-group-card {
  border-radius: 12px;
  border: 1px solid #e5edf8;
  background: linear-gradient(180deg, #f7fbff 0%, #ffffff 100%);
  padding: 14px 16px;
}

.recommendation-group-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 6px;
}

.recommendation-group-title {
  font-size: 14px;
  font-weight: 600;
  color: #243b53;
}

.recommendation-group-desc {
  font-size: 12px;
  line-height: 1.6;
  color: #5f6c7b;
}

.recommendation-group-reason {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.6;
  color: var(--brand-primary, #266fcb);
}

.task-item {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
}

.task-item:hover {
  box-shadow: 0 8px 24px rgba(38, 111, 203, 0.08);
  transform: translateY(-2px);
  border-color: #c6e2ff;
}

.task-item--todo {
  border-left: 4px solid var(--el-color-warning);
  background: linear-gradient(90deg, var(--el-color-warning-light-9) 0%, #fff 10%);
}

.task-item-main {
  flex: 1;
  min-width: 0;
  margin-right: 24px;
}

.task-item-head {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.task-item-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-item-desc {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
  line-height: 1.5;
}

.task-item-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.task-item-reason {
  margin-top: 10px;
  font-size: 12px;
  line-height: 1.6;
  color: #266fcb;
}

.task-item-actions {
  flex-shrink: 0;
}

.custom-table {
  --el-table-border-color: #ebeef5;
  --el-table-header-bg-color: #fafafa;
}

.count-badge {
  display: inline-block;
  padding: 2px 8px;
  background: var(--el-color-primary-light-9);
  color: var(--brand-primary, #266fcb);
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
}

.count-badge.is-zero {
  background: #f4f4f5;
  color: #909399;
}

@media (max-width: 1200px) {
  .plaza-kpis {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .plaza-kpis {
    grid-template-columns: 1fr;
  }
  .recommendation-groups {
    grid-template-columns: 1fr;
  }
  .task-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  .task-item-main {
    margin-right: 0;
    width: 100%;
  }
}
</style>
