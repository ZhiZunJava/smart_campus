<template>
  <div class="exam-preview-page" v-loading="loading">
    <template v-if="paper.paperId">
      <header class="exam-header">
        <div class="exam-header-left">
          <div class="exam-header-eyebrow"><i class="ri-eye-line mr-1"></i>试卷预览</div>
          <h1 class="exam-title">{{ paper.paperName || '未命名试卷' }}</h1>
          <div class="exam-meta">
            <el-tag effect="plain" type="info" size="small">{{ courseLabel(paper.courseId) }}</el-tag>
            <span class="meta-item">时长 {{ paper.durationMinutes || 0 }} 分钟</span>
            <span class="meta-item">总分 {{ paper.totalScore || 0 }}</span>
            <span class="meta-item">及格线 {{ paper.passScore || 60 }}</span>
            <el-tag :type="paper.publishStatus === 'published' ? 'success' : 'warning'" effect="plain">{{ publishStatusLabel(paper.publishStatus) }}</el-tag>
            <el-tag v-if="isWrongRetryPaper(paper)" type="danger" effect="plain">错题回练</el-tag>
          </div>
        </div>
        <div class="exam-header-right">
          <div class="header-actions">
            <el-button plain @click="goExamHub">前往我的考试</el-button>
            <el-button
              type="primary"
              :loading="starting"
              :disabled="starting || (!ongoingRecord && isAttemptLimitReached)"
              @click="beginOrResume"
            >
              {{ primaryActionLabel }}
            </el-button>
          </div>
          <div v-if="Number(paper.maxAttemptCount || 0) > 0" class="exam-attempt-info mt-2 text-sm text-gray-500">
            次数：{{ attemptSummary }}
          </div>
        </div>
      </header>

      <el-alert
        v-if="statusMessage"
        :title="statusMessage"
        :type="statusType"
        :closable="false"
        class="mt-4 mb-4"
        show-icon
      />

      <div class="exam-main-layout mt-4">
        <!-- Main Area (Questions) -->
        <main class="exam-content-area">
          <div class="exam-preview-list">
            <div v-for="group in questionGroups" :key="group.type" class="exam-preview-group">
              <div class="exam-preview-group__header">
                <div class="exam-preview-group__title">{{ questionTypeLabel(group.type) }}</div>
                <div class="exam-preview-group__meta">{{ group.questions.length }} 题 / {{ group.totalScore }} 分</div>
              </div>
              <div class="exam-preview-question-list">
                <article v-for="item in group.questions" :key="item.questionId" class="exam-question-sheet">
                  <div class="sheet-header">
                    <div class="sheet-header-left">
                      <div class="question-index">第 {{ item.sortNo || item.order }} 题</div>
                      <div class="question-type">{{ questionTypeLabel(group.type) }}</div>
                    </div>
                    <div class="sheet-header-right">
                      <div class="question-score">{{ item.score || 0 }} 分</div>
                    </div>
                  </div>

                  <div class="question-stem">{{ stripHtml(item.question?.stem) || '题干暂未维护' }}</div>

                  <div v-if="(item.question?.options || []).length" class="question-body">
                    <div class="exam-options-group">
                      <div v-for="opt in item.question?.options || []" :key="opt.optionId || opt.optionKey" class="exam-option-item">
                        <span class="option-key">{{ opt.optionKey }}.</span>
                        <span class="option-content">{{ opt.optionContent }}</span>
                      </div>
                    </div>
                  </div>
                  <div v-else class="question-body">
                    <el-input
                      type="textarea"
                      :rows="4"
                      placeholder="非选择题，无需在此作答..."
                      class="exam-textarea"
                      disabled
                    />
                  </div>
                </article>
              </div>
            </div>
          </div>
        </main>

        <!-- Sidebar -->
        <aside class="exam-sidebar">
          <div class="sidebar-card">
            <div class="sidebar-title">试卷大纲</div>
            <div class="progress-stats">
              <div class="stat-box">
                <div class="stat-val">{{ questionCount }}</div>
                <div class="stat-lbl">总题数</div>
              </div>
              <div class="stat-box">
                <div class="stat-val text-primary">{{ paper.totalScore || 0 }}</div>
                <div class="stat-lbl">总分</div>
              </div>
            </div>
          </div>

          <div class="sidebar-card mt-4 flex-1">
            <div class="sidebar-title">题型分布</div>
            <div class="palette-container">
              <template v-for="group in questionGroups" :key="group.type">
                <div class="palette-group">
                  <div class="palette-group-title">{{ questionTypeLabel(group.type) }} ({{ group.questions.length }}题 / {{ group.totalScore }}分)</div>
                  <div class="palette-grid">
                    <button
                      v-for="item in group.questions"
                      :key="item.questionId"
                      type="button"
                      class="palette-btn"
                      disabled
                    >
                      {{ item.sortNo || item.order }}
                    </button>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </aside>
      </div>
    </template>

    <el-empty v-else description="试卷不存在或暂不可预览" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from '@/utils/feedback'
import { fetchPortalCourseOptions, getPaperDetail, listExamRecord, startExam } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = usePortalUserStore()

const loading = ref(false)
const starting = ref(false)
const paper = ref<any>({})
const records = ref<any[]>([])
const courseOptions = ref<any[]>([])

const paperId = computed(() => Number(route.params.paperId || route.query.paperId || 0))
const ongoingRecord = computed(() => records.value.find((item: any) => Number(item.paperId || 0) === paperId.value && item.examStatus === 'ONGOING') || null)
const attemptCount = computed(() => records.value.filter((item: any) => Number(item.paperId || 0) === paperId.value).length)
const attemptSummary = computed(() => {
  const maxAttemptCount = Number(paper.value?.maxAttemptCount || 0)
  if (maxAttemptCount <= 0) return '不限'
  return `${attemptCount.value}/${maxAttemptCount}`
})
const isAttemptLimitReached = computed(() => {
  const maxAttemptCount = Number(paper.value?.maxAttemptCount || 0)
  return maxAttemptCount > 0 && attemptCount.value >= maxAttemptCount
})
const isMakeupPaper = computed(() => {
  const maxAttemptCount = Number(paper.value?.maxAttemptCount || 0)
  return maxAttemptCount > 0 && attemptCount.value > 0 && attemptCount.value < maxAttemptCount
})
const statusMessage = computed(() => {
  if (ongoingRecord.value) return `你有一场未完成考试，开始于 ${formatDateTime(ongoingRecord.value.startTime)}，可直接继续作答。`
  if (isAttemptLimitReached.value) return '当前试卷可参加次数已用完。'
  if (isMakeupPaper.value) return `你已参加 ${attemptCount.value}/${paper.value.maxAttemptCount} 次，还可补考 ${Number(paper.value.maxAttemptCount || 0) - attemptCount.value} 次。`
  if (attemptCount.value > 0) return `你已参加 ${attemptCount.value} 次，可再次参加考试。`
  return ''
})
const statusType = computed(() => {
  if (ongoingRecord.value) return 'warning'
  if (isAttemptLimitReached.value) return 'error'
  return 'info'
})
const primaryActionLabel = computed(() => {
  if (ongoingRecord.value) return '继续考试'
  if (isAttemptLimitReached.value) return '次数已用完'
  if (isMakeupPaper.value) return '去补考'
  if (attemptCount.value > 0) return '再次考试'
  return '开始考试'
})
const questionCount = computed(() => (paper.value.questions || []).length)
const questionGroups = computed(() => {
  const groups = new Map<string, { type: string; totalScore: number; questions: any[] }>()
  ;(paper.value.questions || []).forEach((item: any, index: number) => {
    const type = String(item?.question?.questionType || 'unknown')
    if (!groups.has(type)) {
      groups.set(type, { type, totalScore: 0, questions: [] })
    }
    const group = groups.get(type)!
    group.questions.push({
      ...item,
      order: index + 1,
    })
    group.totalScore += Number(item?.score || 0)
  })
  return Array.from(groups.values())
})

function stripHtml(value: string) {
  return String(value || '').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

function questionTypeLabel(type?: string) {
  return ({ single: '单选题', multiple: '多选题', judge: '判断题', fill: '填空题', essay: '简答题', material: '材料题', case: '案例题' } as any)[type || ''] || type || '-'
}

function publishStatusLabel(status?: string) {
  return ({ draft: '草稿', published: '已发布', archived: '已归档' } as any)[status || ''] || status || '-'
}

function courseLabel(courseId: number | string | undefined) {
  if (!courseId) return '通用试卷'
  const matched = courseOptions.value.find((item: any) => String(item.value) === String(courseId))
  return matched?.label || `课程 ${courseId}`
}

function isWrongRetryPaper(targetPaper: any) {
  return String(targetPaper?.paperName || '').includes('错题回练')
}

function formatDateTime(value?: string) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString()
}

async function loadData() {
  if (!paperId.value) return
  loading.value = true
  try {
    const userId = userStore.user?.userId
    const [paperRes, recordRes, courseRes] = await Promise.all([
      getPaperDetail(paperId.value),
      listExamRecord({ pageNum: 1, pageSize: 50 }),
      fetchPortalCourseOptions(userId),
    ])
    paper.value = paperRes.data || {}
    records.value = recordRes.rows || []
    courseOptions.value = courseRes || []
  } finally {
    loading.value = false
  }
}

async function beginOrResume() {
  if (ongoingRecord.value) {
    router.push({
      path: `/student/exams/session/${ongoingRecord.value.recordId}`,
      query: {
        paperId: String(paperId.value),
        startedAt: String(ongoingRecord.value.startTime || ''),
      },
    })
    return
  }
  if (isAttemptLimitReached.value || starting.value) return
  starting.value = true
  try {
    const res = await startExam({ paperId: paperId.value })
    const record = res.data || {}
    if (!record.recordId) {
      ElMessage.error('考试启动失败，请稍后重试')
      return
    }
    router.push({
      path: `/student/exams/session/${record.recordId}`,
      query: {
        paperId: String(paperId.value),
        startedAt: String(record.startTime || ''),
      },
    })
  } finally {
    starting.value = false
  }
}

function goExamHub() {
  router.push('/student/exams')
}

function goBack() {
  router.back()
}

onMounted(loadData)
</script>

<style scoped>
.exam-preview-page {
  min-height: 100%;
  padding: 0;
}

/* Header */
.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  margin-bottom: 24px;
  background: #ffffff;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 20px -2px rgba(15, 23, 42, 0.05);
  position: relative;
  overflow: hidden;
}

.exam-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3b82f6 0%, #60a5fa 100%);
}

.exam-header-left {
  flex: 1;
}

.exam-header-eyebrow {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 999px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin-bottom: 12px;
}

.exam-title {
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 12px;
  line-height: 1.4;
}

.exam-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.meta-item {
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  background: #f8fafc;
  padding: 4px 10px;
  border-radius: 6px;
  border: 1px solid #f1f5f9;
}

.exam-header-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.header-actions .el-button {
  padding: 10px 24px;
  font-size: 14px;
  border-radius: 6px;
}

/* Main Layout */
.exam-main-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 24px;
  align-items: start;
}

/* Question Area */
.exam-question-sheet {
  padding: 32px;
  background: #ffffff;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 20px -2px rgba(15, 23, 42, 0.05);
  margin-bottom: 24px;
}

.sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 20px;
  border-bottom: 1px dashed #e2e8f0;
  margin-bottom: 24px;
}

.sheet-header-left {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-index {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
}

.question-type {
  font-size: 13px;
  color: #64748b;
  background: #f1f5f9;
  padding: 4px 12px;
  border-radius: 6px;
  display: inline-block;
  width: fit-content;
  font-weight: 600;
}

.question-score {
  font-size: 14px;
  font-weight: 700;
  color: #3b82f6;
  background: #eff6ff;
  padding: 6px 16px;
  border-radius: 999px;
  border: 1px solid #bfdbfe;
}

.question-stem {
  font-size: 16px;
  line-height: 1.8;
  color: #0f172a;
  margin-bottom: 32px;
  font-weight: 500;
}

.exam-options-group {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 16px;
  width: 100%;
}

.exam-option-item {
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  margin: 0 !important;
  padding: 16px 20px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: #f8fafc;
  height: auto;
  white-space: normal;
}

.option-key {
  color: #3b82f6;
  font-weight: 800;
  font-size: 15px;
  flex-shrink: 0;
  width: 28px;
}

.option-content {
  color: #334155;
  font-size: 15px;
  line-height: 1.6;
  flex: 1;
}

.exam-textarea {
  font-size: 15px;
  line-height: 1.6;
}
.exam-textarea :deep(.el-textarea__inner) {
  padding: 16px;
  border-radius: 6px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  box-shadow: none;
}

/* Sidebar */
.exam-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-card {
  padding: 24px;
  background: #ffffff;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 20px -2px rgba(15, 23, 42, 0.05);
}

.sidebar-title {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #3b82f6;
}

.progress-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  text-align: center;
}

.stat-box {
  background: #f8fafc;
  padding: 16px 8px;
  border-radius: 6px;
  border: 1px solid #f1f5f9;
}

.stat-val {
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 6px;
}

.stat-lbl {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.text-primary {
  color: #3b82f6 !important;
}

.palette-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.palette-group-title {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
  margin-bottom: 14px;
  padding-left: 10px;
  border-left: 3px solid #3b82f6;
  line-height: 1.2;
}

.palette-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
}

.palette-btn {
  height: 40px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  color: #475569;
  font-size: 15px;
  font-weight: 600;
}

.palette-btn:disabled {
  opacity: 0.8;
  cursor: default;
  background: #f8fafc;
}

.exam-preview-group__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 24px;
  background: #f8fafc;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
}

.exam-preview-group__title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 800;
}

.exam-preview-group__meta {
  color: #64748b;
  font-size: 14px;
  font-weight: 600;
}

@media (max-width: 1200px) {
  .exam-main-layout {
    grid-template-columns: minmax(0, 1fr) 280px;
  }
}

@media (max-width: 1024px) {
  .exam-main-layout {
    grid-template-columns: 1fr;
  }
  .exam-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  .exam-header-right {
    width: 100%;
    align-items: flex-start;
  }
}
</style>
