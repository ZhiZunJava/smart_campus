<template>
  <div class="exam-session-page" :class="{ 'is-result-mode': mode === 'result' }" v-loading="loading">
    <template v-if="mode === 'exam' && allQuestionEntries.length">
      <!-- Top Header -->
      <header class="exam-header">
        <div class="exam-header-left">
          <div class="exam-header-eyebrow"><i class="ri-edit-box-line mr-1"></i>在线考试</div>
          <h1 class="exam-title">{{ paper.paperName || '在线考试' }}</h1>
          <div class="exam-meta">
            <el-tag effect="plain" type="info" size="small">{{ courseLabel(paper.courseId) }}</el-tag>
            <span class="meta-item">总分 {{ paper.totalScore || 0 }}</span>
            <span class="meta-item" v-if="activeSessionPaper">当前阶段: {{ paperStatusLabel(activeSessionPaper.paperStatus) }}</span>
            <span class="meta-item" v-if="runtimeTimeSummary.startTime">开始于 {{ formatDateTime(runtimeTimeSummary.startTime) }}</span>
            <span class="meta-item" v-if="runtimeElapsedText !== '-'">已作答 {{ runtimeElapsedText }}</span>
          </div>
        </div>
        
        <div class="exam-header-center">
          <div class="exam-timer" :class="{ 'is-warning': remainingSeconds <= 300 }">
            <div class="timer-label">剩余时间</div>
            <div class="timer-value">{{ formatDuration(remainingSeconds) }}</div>
          </div>
        </div>

        <div class="exam-header-right">
          <div class="draft-status">
            <span class="draft-dot" :class="{ 'is-saving': draftSaving }"></span>
            <span class="draft-text">{{ draftHint }}</span>
          </div>
          <div class="header-actions">
            <el-button plain @click="goBackToHub">暂存并离开</el-button>
            <el-button type="primary" :loading="submitting" @click="confirmSubmit">立即交卷</el-button>
          </div>
        </div>
      </header>

      <el-alert
        v-if="showUrgentWarning"
        type="warning"
        :closable="false"
        class="exam-urgent-alert mt-4"
        title="距离考试结束不足 5 分钟，请尽快完成检查并提交试卷。"
        show-icon
      />

      <div class="exam-main-layout mt-4">
        <!-- Question Area (Left/Center) -->
        <main class="exam-content-area">
          <article v-if="currentQuestion" class="exam-question-sheet">
            <div class="sheet-header">
              <div class="sheet-header-left">
                <div class="question-index">第 {{ currentQuestionIndex + 1 }} 题</div>
                <div class="question-type">{{ questionTypeLabel(currentQuestion.question?.questionType) }}</div>
              </div>
              <div class="sheet-header-right">
                <div class="question-score">{{ currentQuestion.score || 0 }} 分</div>
              </div>
            </div>

            <div class="question-stem">
              {{ stripHtml(currentQuestion.question?.stem) }}
            </div>

            <div v-if="currentQuestion.question?.questionType === 'single'" class="question-body">
              <el-radio-group v-model="answerMap[currentQuestion.questionId]" class="exam-options-group">
                <el-radio
                  v-for="opt in currentQuestion.question?.options || []"
                  :key="opt.optionId"
                  :label="opt.optionKey"
                  class="exam-option-item"
                  :class="{ 'is-selected': answerMap[currentQuestion.questionId] === opt.optionKey }"
                >
                  <span class="option-key">{{ opt.optionKey }}.</span>
                  <span class="option-content">{{ opt.optionContent }}</span>
                </el-radio>
              </el-radio-group>
            </div>

            <div v-else-if="currentQuestion.question?.questionType === 'multiple'" class="question-body">
              <el-checkbox-group v-model="multiAnswerMap[currentQuestion.questionId]" class="exam-options-group">
                <el-checkbox
                  v-for="opt in currentQuestion.question?.options || []"
                  :key="opt.optionId"
                  :label="opt.optionKey"
                  class="exam-option-item"
                  :class="{ 'is-selected': (multiAnswerMap[currentQuestion.questionId] || []).includes(opt.optionKey) }"
                >
                  <span class="option-key">{{ opt.optionKey }}.</span>
                  <span class="option-content">{{ opt.optionContent }}</span>
                </el-checkbox>
              </el-checkbox-group>
            </div>

            <div v-else class="question-body">
              <el-input
                v-model="answerMap[currentQuestion.questionId]"
                type="textarea"
                :rows="6"
                placeholder="请输入你的答案..."
                class="exam-textarea"
              />
            </div>

            <div class="sheet-footer">
              <el-button size="large" :disabled="!canGoPrevious" @click="goToQuestion(currentQuestionIndex - 1)">上一题</el-button>
              
              <div class="sheet-footer-center">
                <el-button plain :type="isFlagged(currentQuestion.questionId) ? 'warning' : 'default'" @click="toggleFlagCurrentQuestion">
                  {{ isFlagged(currentQuestion.questionId) ? '取消标记' : '标记本题' }}
                </el-button>
                <el-button plain type="primary" :loading="questionSubmitting" @click="submitCurrentQuestion">提交本题</el-button>
              </div>

              <el-button size="large" type="primary" :disabled="!canGoNext" @click="goToQuestion(currentQuestionIndex + 1)">下一题</el-button>
            </div>
          </article>
        </main>

        <!-- Sidebar (Right) -->
        <aside class="exam-sidebar">
          <!-- Stats -->
          <div class="sidebar-card">
            <div class="sidebar-title">答题进度</div>
            <div class="progress-stats">
              <div class="stat-box">
                <div class="stat-val">{{ questionCount }}</div>
                <div class="stat-lbl">总题数</div>
              </div>
              <div class="stat-box text-success">
                <div class="stat-val">{{ answeredCount }}</div>
                <div class="stat-lbl">已作答</div>
              </div>
              <div class="stat-box text-warning">
                <div class="stat-val">{{ unansweredCount }}</div>
                <div class="stat-lbl">未作答</div>
              </div>
            </div>
          <div class="monitor-stats mt-3">
              <span>切屏: {{ focusLossCount }}次<span v-if="antiCheatEnabled"> / {{ maxFocusLossCount }}次</span></span>
              <el-divider direction="vertical" />
              <span>标记: {{ flaggedCount }}题</span>
            </div>
          </div>

          <!-- Subpapers -->
          <div v-if="sessionPapers.length > 1" class="sidebar-card mt-4">
            <div class="sidebar-title">试卷结构</div>
            <div class="subpaper-list">
              <div
                v-for="item in sessionPapers"
                :key="item.id"
                class="subpaper-item"
                :class="{
                  'is-active': activeSessionPaper?.id === item.id,
                  'is-done': item.paperStatus === 'SUBMITTED'
                }"
                @click="activateSessionPaper(item.id)"
              >
                <div class="subpaper-name">{{ paperNameMap[item.paperId] || `试卷 ${item.paperId}` }}</div>
                <div class="subpaper-status">
                  <el-tag size="small" :type="item.paperStatus === 'SUBMITTED' ? 'success' : 'info'">{{ paperStatusLabel(item.paperStatus) }}</el-tag>
                </div>
              </div>
            </div>
            <el-button
              v-if="activeSessionPaper?.paperLevel === 'SUB'"
              type="warning"
              plain
              class="w-full mt-3"
              :loading="subPaperSubmitting"
              @click="submitCurrentSubPaper"
            >
              提交当前子卷
            </el-button>
          </div>

          <!-- Palette -->
          <div class="sidebar-card mt-4 flex-1">
            <div class="sidebar-title">答题卡</div>
            <div class="palette-legend">
              <span class="legend-item"><i class="legend-dot is-answered"></i>已答</span>
              <span class="legend-item"><i class="legend-dot is-unanswered"></i>未答</span>
              <span class="legend-item"><i class="legend-dot is-flagged"></i>标记</span>
              <span class="legend-item"><i class="legend-dot is-current"></i>当前</span>
            </div>
            <div class="palette-container mt-3">
              <template v-for="(group, type) in groupedPaletteQuestions" :key="type">
                <div class="palette-group">
                  <div class="palette-group-title">{{ questionTypeLabel(String(type)) }}</div>
                  <div class="palette-grid">
                    <button
                      v-for="item in group"
                      :key="item.questionId"
                      type="button"
                      class="palette-btn"
                      :class="{
                        'is-current': currentQuestionIndex === item.index,
                        'is-answered': item.answered,
                        'is-flagged': item.flagged,
                        'is-disabled': !canNavigateToQuestion(item.index),
                      }"
                      :disabled="!canNavigateToQuestion(item.index)"
                      @click="goToQuestion(item.index)"
                    >
                      {{ item.order }}
                    </button>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </aside>
      </div>
    </template>

    <template v-else-if="mode === 'result'">
      <header class="exam-header">
        <div class="exam-header-left">
          <div class="exam-header-eyebrow"><i class="ri-bar-chart-box-line mr-1"></i>考试结果</div>
          <h1 class="exam-title">{{ resultPaper.paperName || '考试结果' }}</h1>
          <div class="exam-meta">
            <span class="meta-item">{{ canViewScoreSummary ? '本次考试已提交，你可以查看总成绩与作答结果。' : '本次考试已提交，当前试卷暂未开放总成绩查看。' }}</span>
            <span class="meta-item" v-if="resultTimeSummary.submitTime">交卷于 {{ formatDateTime(resultTimeSummary.submitTime) }}</span>
            <span class="meta-item" v-if="resultElapsedText !== '-'">总用时 {{ resultElapsedText }}</span>
          </div>
        </div>
        <div class="exam-header-right">
          <div class="header-actions">
            <el-button plain @click="goBackToHub">返回列表</el-button>
          </div>
        </div>
      </header>

      <div class="exam-main-layout mt-4">
        <!-- Main Area (Questions & Results) -->
        <main class="exam-content-area">
          <div v-if="canViewAnswerAnalysis" class="exam-result-list">
            <article v-for="(item, index) in filteredResultAnswers" :key="item.questionId" class="exam-question-sheet">
              <div class="sheet-header">
                <div class="sheet-header-left">
                  <div class="question-index">第 {{ index + 1 }} 题</div>
                  <div class="question-type">{{ questionTypeLabel(item.questionType) }}</div>
                </div>
                <div class="sheet-header-right">
                  <div class="question-score-badge" :class="{ 'is-wrong': item.isCorrect !== '1', 'is-correct': item.isCorrect === '1' }">
                    <i :class="item.isCorrect === '1' ? 'ri-check-line' : 'ri-close-line'"></i>
                    <span>得分: {{ item.score || 0 }}</span>
                  </div>
                </div>
              </div>

              <div class="question-stem">{{ stripHtml(item.stem) || '题干暂未维护' }}</div>

              <div class="exam-result-answers mt-4">
                <div class="answer-box">
                  <div class="answer-label">我的答案</div>
                  <div class="answer-content" :class="item.isCorrect === '1' ? 'text-success' : 'text-danger'">
                    {{ item.userAnswer || '未作答' }}
                  </div>
                </div>
                <div class="answer-box">
                  <div class="answer-label">标准答案</div>
                  <div class="answer-content text-primary">
                    {{ item.standardAnswer || '暂无标准答案' }}
                  </div>
                </div>
              </div>

              <div v-if="item.analysis" class="exam-result-analysis mt-4">
                <div class="analysis-label">解析</div>
                <div class="analysis-content">{{ item.analysis }}</div>
              </div>
            </article>
          </div>
          <el-empty v-else description="当前试卷未开放答案与解析查看" />
        </main>

        <!-- Sidebar -->
        <aside class="exam-sidebar">
          <div v-if="canViewScoreSummary" class="sidebar-card">
            <div class="sidebar-title">成绩概览</div>
            <div class="progress-stats">
              <div class="stat-box">
                <div class="stat-val text-primary">{{ resultRecord.record?.score || 0 }}</div>
                <div class="stat-lbl">总得分</div>
              </div>
              <div class="stat-box">
                <div class="stat-val text-success">{{ resultRecord.record?.correctRate || 0 }}%</div>
                <div class="stat-lbl">正确率</div>
              </div>
            </div>
            <div class="progress-stats mt-3">
              <div class="stat-box">
                <div class="stat-val">{{ resultQuestionCount }}</div>
                <div class="stat-lbl">总题数</div>
              </div>
              <div class="stat-box">
                <div class="stat-val text-danger">{{ resultWrongCount }}</div>
                <div class="stat-lbl">错题数</div>
              </div>
            </div>
          </div>

          <div v-if="canViewScoreSummary && resultRecord.subPaperStats?.length" class="sidebar-card mt-4">
            <div class="sidebar-title">子卷统计</div>
            <div class="subpaper-list">
              <div v-for="sub in resultRecord.subPaperStats" :key="sub.paperName" class="subpaper-item is-active">
                <div class="subpaper-name">{{ sub.paperName }}</div>
                <div class="subpaper-status">
                  <span class="text-primary font-bold">{{ sub.avgScore }}分</span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="canViewScoreSummary" class="sidebar-card mt-4">
            <div class="sidebar-title">时间与成长</div>
            <div class="result-meta-grid">
              <div class="result-meta-item">
                <span>开始时间</span>
                <strong>{{ formatDateTime(resultTimeSummary.startTime) }}</strong>
              </div>
              <div class="result-meta-item">
                <span>交卷时间</span>
                <strong>{{ formatDateTime(resultTimeSummary.submitTime) }}</strong>
              </div>
              <div class="result-meta-item">
                <span>交卷用时</span>
                <strong>{{ resultElapsedText }}</strong>
              </div>
              <div class="result-meta-item">
                <span>成长积分</span>
                <strong>+{{ resultGrowthPoints }}</strong>
              </div>
            </div>
            <div v-if="resultGrowthRewards.length" class="result-growth-rewards">
              <div v-for="item in resultGrowthRewards" :key="`${item.bizType}-${item.createTime || item.bizId}`" class="result-growth-reward">
                <div class="result-growth-reward__main">
                  <strong>{{ rewardTypeLabel(item.bizType) }}</strong>
                  <span>{{ item.remark || rewardTypeLabel(item.bizType) }}</span>
                </div>
                <div class="result-growth-reward__point">+{{ item.changePoints || 0 }}</div>
              </div>
            </div>
          </div>

          <div v-if="canViewScoreSummary && resultTypePerformance.length" class="sidebar-card mt-4">
            <div class="sidebar-title">题型表现</div>
            <div class="subpaper-list">
              <div v-for="item in resultTypePerformance" :key="item.type" class="subpaper-item">
                <div class="subpaper-name">
                  <div>{{ questionTypeLabel(item.type) }}</div>
                  <div class="type-perf-sub">{{ item.count }}题 / 正确{{ item.correct }}题</div>
                </div>
                <div class="subpaper-status">
                  <span :class="item.correctRate >= 60 ? 'text-success' : 'text-danger'" class="type-perf-rate">{{ item.correctRate }}%</span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="canViewScoreSummary" class="sidebar-card mt-4">
            <div class="sidebar-title">结果总结</div>
            <div class="result-summary-text">
              <p>{{ resultSummaryText }}</p>
              <p v-if="weakQuestionTypeText" class="result-summary-weak">薄弱题型：{{ weakQuestionTypeText }}</p>
              <p v-if="canViewAnswerAnalysis && resultWrongCount > 0" class="result-summary-tip">建议优先复盘错题与解析，再回到错题本做针对性回练。</p>
            </div>
          </div>
        </aside>
      </div>
    </template>

    <el-empty v-else description="考试数据加载失败或试卷内容为空" />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from '@/utils/feedback'
import {
  fetchPortalCourseOptions,
  getExamRecordDetail,
  getExamRuntime,
  getPaperDetail,
  listExamDrafts,
  saveExamDraft,
  submitExam,
  submitExamQuestion,
  submitExamSubPaper,
} from '@/api/portal'
import usePortalUserStore from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = usePortalUserStore()

const loading = ref(false)
const mode = ref<'exam' | 'result'>('exam')
const paper = ref<any>({})
const resultPaper = ref<any>({})
const resultRecord = ref<any>({})
const runtimeData = ref<any>({})
const sessionPapers = ref<any[]>([])
const courseOptions = ref<any[]>([])
const answerMap = ref<Record<string, any>>({})
const multiAnswerMap = ref<Record<string, string[]>>({})
const flaggedQuestionIds = ref<number[]>([])
const remainingSeconds = ref(0)
const currentQuestionIndex = ref(0)
const activeSessionPaperId = ref<number | null>(null)
const draftHint = ref('最近一次暂存：未开始')
const submitted = ref(false)
const submitting = ref(false)
const draftSaving = ref(false)
const questionSubmitting = ref(false)
const subPaperSubmitting = ref(false)
const focusLossCount = ref(0)
const timerId = ref<number | null>(null)
const draftTimerId = ref<number | null>(null)
const blurCheckTimerId = ref<number | null>(null)
const focusLossTracked = ref(false)
const clipboardWarningAt = ref(0)

const recordId = computed(() => Number(route.params.recordId || 0))
const paperId = computed(() => Number(route.query.paperId || 0))
const durationMinutes = computed(() => Number(paper.value.durationMinutes || 0))
const questionList = computed(() => paper.value.questions || [])
const allQuestionEntries = computed(() => {
  const seen = new Set<string>()
  const result: any[] = []
  const walk = (node: any) => {
    ;(node?.questions || []).forEach((item: any) => {
      const key = String(item?.questionId || '')
      if (!key || seen.has(key)) return
      seen.add(key)
      result.push(item)
    })
    ;(node?.subPapers || []).forEach((item: any) => walk(item))
  }
  walk(paper.value)
  return result
})
const questionCount = computed(() => currentSessionPaperQuestions.value.length)
const answeredCount = computed(() => currentSessionPaperQuestions.value.filter((item: any) => item.answered).length)
const unansweredCount = computed(() => Math.max(0, questionCount.value - answeredCount.value))
const currentQuestion = computed(() => currentSessionPaperQuestions.value[currentQuestionIndex.value] || null)
const showUrgentWarning = computed(() => mode.value === 'exam' && remainingSeconds.value > 0 && remainingSeconds.value <= 300)
const flaggedCount = computed(() => flaggedQuestionIds.value.length)
const canViewScoreSummary = computed(() => String(resultPaper.value?.allowViewScore ?? '1') !== '0')
const canViewAnswerAnalysis = computed(() => String(resultPaper.value?.allowReviewAnalysis ?? '1') !== '0')
const antiCheatEnabled = computed(() => String(paper.value.antiCheatEnabled ?? '1') !== '0')
const maxFocusLossCount = computed(() => Math.max(0, Number(paper.value.maxFocusLossCount ?? 5)))
const autoSubmitOnFocusLossLimit = computed(() => String(paper.value.autoSubmitOnFocusLossLimit ?? '0') === '1')
const allowCopyPaste = computed(() => String(paper.value.allowCopyPaste ?? '1') !== '0')
const questionNavigationMode = computed(() => String(paper.value.questionNavigationMode || 'free'))
const activeSessionPaper = computed(() =>
  sessionPapers.value.find((item: any) => Number(item.id) === Number(activeSessionPaperId.value)) || null,
)
const currentSessionPaperLabel = computed(() => {
  const item = activeSessionPaper.value
  if (!item) return '-'
  return paperNameMap.value[item.paperId] || `试卷 ${item.paperId}`
})
const currentSessionPaperQuestions = computed(() => {
  const item = activeSessionPaper.value
  const source = item ? resolvePaperQuestions(item.paperId) : questionList.value
  return source.map((entry: any, index: number) => ({
    ...entry,
    index,
    order: index + 1,
    answered: isAnswered(entry),
    flagged: isFlagged(entry.questionId),
  }))
})

const groupedPaletteQuestions = computed(() => {
  const groups: Record<string, any[]> = {}
  currentSessionPaperQuestions.value.forEach((item: any) => {
    const type = item.question?.questionType || 'unknown'
    if (!groups[type]) {
      groups[type] = []
    }
    groups[type].push(item)
  })
  return groups
})

const paperNameMap = computed(() => {
  const map: Record<string, string> = {}
  const walk = (node: any) => {
    if (!node) return
    if (node.paperId) {
      map[node.paperId] = node.paperName
    }
    ;(node.subPapers || []).forEach(walk)
  }
  walk(paper.value)
  return map
})
const runtimeTimeSummary = computed(() => runtimeData.value.timeSummary || {})
const resultTimeSummary = computed(() => resultRecord.value.timeSummary || {})
const runtimeElapsedText = computed(() => formatElapsedSeconds(runtimeTimeSummary.value.elapsedSeconds))
const resultElapsedText = computed(() => formatElapsedSeconds(resultTimeSummary.value.elapsedSeconds))
const resultGrowthRewards = computed(() => resultRecord.value.growthRewards || [])
const resultGrowthPoints = computed(() =>
  Number(resultRecord.value.growthPoints || resultGrowthRewards.value.reduce((total: number, item: any) => total + Number(item.changePoints || 0), 0)),
)
const filteredResultAnswers = computed(() => resultRecord.value.answers || [])
const resultQuestionCount = computed(() => filteredResultAnswers.value.length)
const resultCorrectCount = computed(() => filteredResultAnswers.value.filter((item: any) => item.isCorrect === '1').length)
const resultWrongCount = computed(() => Math.max(0, resultQuestionCount.value - resultCorrectCount.value))
const resultScoreRate = computed(() => {
  const totalScore = Number(resultPaper.value?.totalScore || 0)
  const score = Number(resultRecord.value?.record?.score || 0)
  if (!totalScore) return 0
  return Math.round((score / totalScore) * 100)
})
const resultTypePerformance = computed(() => {
  const groups = new Map<string, { type: string; count: number; correct: number; correctRate: number }>()
  filteredResultAnswers.value.forEach((item: any) => {
    const type = String(item.questionType || 'unknown')
    if (!groups.has(type)) {
      groups.set(type, { type, count: 0, correct: 0, correctRate: 0 })
    }
    const group = groups.get(type)!
    group.count += 1
    if (item.isCorrect === '1') {
      group.correct += 1
    }
    group.correctRate = Math.round((group.correct / group.count) * 100)
  })
  return Array.from(groups.values())
})
const weakQuestionTypeText = computed(() => {
  if (!resultTypePerformance.value.length) return ''
  const sorted = [...resultTypePerformance.value].sort((a, b) => a.correctRate - b.correctRate)
  const weakest = sorted[0]
  if (!weakest || weakest.correctRate >= 100) return ''
  return `${questionTypeLabel(weakest.type)}（正确率 ${weakest.correctRate}%）`
})
const resultSummaryText = computed(() => {
  if (!canViewScoreSummary.value) return '当前试卷未开放总成绩查看。'
  if (resultWrongCount.value === 0) return '这次答卷表现很稳定，全部题目都已答对。'
  return `本次共错 ${resultWrongCount.value} 题，整体得分率 ${resultScoreRate.value}%。`
})
const lockedQuestionIndex = computed(() => {
  if (questionNavigationMode.value !== 'lock_after_answer' && questionNavigationMode.value !== 'strict_sequence') return 0
  const firstUnanswered = currentSessionPaperQuestions.value.findIndex((item: any) => !item.answered)
  return firstUnanswered >= 0 ? firstUnanswered : Math.max(0, currentSessionPaperQuestions.value.length - 1)
})
const canGoPrevious = computed(() => currentQuestionIndex.value > 0 && canNavigateToQuestion(currentQuestionIndex.value - 1))
const currentQuestionSubmitted = computed(() => {
  const questionId = currentQuestion.value?.questionId
  return Boolean(questionId && runtimeData.value?.answerMap?.[questionId])
})
const canGoNext = computed(() => {
  if (currentQuestionIndex.value >= currentSessionPaperQuestions.value.length - 1) return false
  if (questionNavigationMode.value === 'strict_sequence') return currentQuestionSubmitted.value
  return canNavigateToQuestion(currentQuestionIndex.value + 1)
})

function stripHtml(value: string) {
  return String(value || '').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

function questionTypeLabel(type: string) {
  return ({ single: '单选题', multiple: '多选题', judge: '判断题', fill: '填空题', essay: '简答题', material: '材料题', case: '案例题' } as any)[type] || type || '-'
}

function paperStatusLabel(status?: string) {
  return ({ PENDING: '待作答', ONGOING: '进行中', SUBMITTED: '已提交', FINISHED: '已完成' } as Record<string, string>)[String(status || '').toUpperCase()] || status || '-'
}

function paperLevelLabel(level?: string) {
  return ({ MAIN: '主卷', SUB: '子卷' } as Record<string, string>)[String(level || '').toUpperCase()] || level || '-'
}

function answerModeLabel(modeValue?: string) {
  return ({ AUTO: '自动', MANUAL: '手动', ADAPTIVE: '自适应', REQUIRED: '必答', OPTIONAL: '选答' } as Record<string, string>)[String(modeValue || '').toUpperCase()] || modeValue || '-'
}

function courseLabel(courseId: number | string | undefined) {
  if (!courseId) return '通用试卷'
  const matched = courseOptions.value.find((item: any) => String(item.value) === String(courseId))
  return matched?.label || `课程 ${courseId}`
}

function formatDuration(totalSeconds: number) {
  const safe = Math.max(0, Number(totalSeconds || 0))
  const hours = Math.floor(safe / 3600)
  const minutes = Math.floor((safe % 3600) / 60)
  const seconds = safe % 60
  return [hours, minutes, seconds].map((item) => String(item).padStart(2, '0')).join(':')
}

function formatElapsedSeconds(totalSeconds: any) {
  const safe = Math.max(0, Number(totalSeconds || 0))
  if (!safe) return '-'
  const hours = Math.floor(safe / 3600)
  const minutes = Math.floor((safe % 3600) / 60)
  if (hours > 0) return `${hours}小时${minutes}分钟`
  return `${Math.max(1, minutes)} 分钟`
}

function formatDateTime(value?: string | number | Date | null) {
  if (!value) return '-'
  const date = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return date.toLocaleString()
}

function rewardTypeLabel(value?: string) {
  const map: Record<string, string> = {
    EXAM_SUBMIT: '完成交卷',
    EXAM_PASS: '通过考试',
    EXAM_EXCELLENT: '高分表现',
    EXAM_PERFECT: '满分答卷',
    EXAM_DISCIPLINE: '规范作答',
    EXAM_EFFICIENT: '高效完成',
    EXAM_PROGRESS: '进步跃迁',
  }
  return map[String(value || '').toUpperCase()] || value || '成长奖励'
}

function isAnswered(item: any) {
  if (item.question?.questionType === 'multiple') {
    return Boolean((multiAnswerMap.value[item.questionId] || []).length)
  }
  return String(answerMap.value[item.questionId] || '').trim().length > 0
}

function isFlagged(questionId?: number) {
  return questionId ? flaggedQuestionIds.value.includes(questionId) : false
}

function canNavigateToQuestion(index: number) {
  const safeIndex = Math.max(0, Math.min(index, Math.max(0, questionCount.value - 1)))
  if (questionNavigationMode.value === 'strict_sequence') return safeIndex === lockedQuestionIndex.value
  if (questionNavigationMode.value !== 'lock_after_answer') return true
  return safeIndex >= lockedQuestionIndex.value
}

function goToQuestion(index: number) {
  const safeIndex = Math.max(0, Math.min(index, Math.max(0, questionCount.value - 1)))
  if (!canNavigateToQuestion(safeIndex)) {
    ElMessage.warning(questionNavigationMode.value === 'strict_sequence' ? '当前试卷要求严格顺序作答，请先提交当前题目' : '当前试卷不允许返回已作答题目')
    return
  }
  currentQuestionIndex.value = safeIndex
}

function activateSessionPaper(sessionPaperId: number) {
  if (questionNavigationMode.value === 'strict_sequence' && activeSessionPaper.value?.id !== sessionPaperId) {
    ElMessage.warning('当前试卷要求严格顺序作答，请先完成当前子卷')
    return
  }
  activeSessionPaperId.value = sessionPaperId
  currentQuestionIndex.value = 0
}

function toggleFlagCurrentQuestion() {
  const questionId = currentQuestion.value?.questionId
  if (!questionId) return
  if (isFlagged(questionId)) {
    flaggedQuestionIds.value = flaggedQuestionIds.value.filter((item) => item !== questionId)
  } else {
    flaggedQuestionIds.value = [...flaggedQuestionIds.value, questionId]
  }
}

function resolvePaperQuestions(targetPaperId?: number) {
  const collect = (node: any): any[] => {
    if (!node) return []
    if (Number(node.paperId) === Number(targetPaperId)) {
      return node.questions || []
    }
    for (const sub of node.subPapers || []) {
      const match = collect(sub)
      if (match.length) return match
    }
    return []
  }
  return collect(paper.value)
}

function findQuestionTypeById(questionId: string | number) {
  const search = (items: any[]): string => {
    for (const item of items || []) {
      if (String(item.questionId) === String(questionId)) {
        return item.question?.questionType || ''
      }
    }
    return ''
  }
  const rootMatch = search(questionList.value)
  if (rootMatch) return rootMatch
  for (const sessionPaper of sessionPapers.value) {
    const matched = search(resolvePaperQuestions(sessionPaper.paperId))
    if (matched) return matched
  }
  return ''
}

function hydrateAnswers(answerMapData: Record<string, any>, draftMapData: Record<string, any>) {
  const plainAnswers: Record<string, any> = {}
  const multiAnswers: Record<string, string[]> = {}
  const applyValue = (questionId: string, rawValue: any, typeHint?: string) => {
    const value = String(rawValue || '')
    const type = typeHint || findQuestionTypeById(questionId)
    if (type === 'multiple') {
      multiAnswers[questionId] = value ? value.split(',').map((item) => item.trim()).filter(Boolean) : []
    } else {
      plainAnswers[questionId] = value
    }
  }
  Object.keys(draftMapData || {}).forEach((questionId) => {
    const draft = draftMapData[questionId]
    applyValue(questionId, draft?.userAnswer)
  })
  Object.keys(answerMapData || {}).forEach((questionId) => {
    const answer = answerMapData[questionId]
    applyValue(questionId, answer?.userAnswer)
  })
  answerMap.value = plainAnswers
  multiAnswerMap.value = multiAnswers
}

async function loadRuntime() {
  const res = await getExamRuntime(recordId.value)
  runtimeData.value = res.data || {}
  paper.value = runtimeData.value.paperDetail || {}
  sessionPapers.value = runtimeData.value.sessionPapers || []
  focusLossCount.value = Number(runtimeData.value.session?.focusLossCount || 0)
  hydrateAnswers(runtimeData.value.answerMap || {}, runtimeData.value.draftMap || {})
  const currentPaper = runtimeData.value.currentSessionPaper || sessionPapers.value[0]
  activeSessionPaperId.value = currentPaper?.id || null
  currentQuestionIndex.value = questionNavigationMode.value === 'lock_after_answer' || questionNavigationMode.value === 'strict_sequence'
    ? lockedQuestionIndex.value
    : 0
  focusLossTracked.value = false
  draftHint.value = runtimeData.value.drafts?.length ? '最近一次暂存：已从服务器恢复' : '最近一次暂存：未开始'
  startTimer(String(runtimeData.value.record?.startTime || route.query.startedAt || ''))
}

async function loadSubmittedResult() {
  const [recordRes, paperRes] = await Promise.all([
    getExamRecordDetail(recordId.value),
    paperId.value ? getPaperDetail(paperId.value) : Promise.resolve({ data: {} }),
  ])
  resultRecord.value = recordRes.data || {}
  resultPaper.value = paperRes.data || {}
}

async function bootstrap() {
  loading.value = true
  try {
    if (!courseOptions.value.length) {
      courseOptions.value = await fetchPortalCourseOptions(userStore.user?.userId)
    }
    if (route.query.mode === 'result') {
      mode.value = 'result'
      submitted.value = true
      stopTimer()
      stopDraftTimer()
      await loadSubmittedResult()
    } else {
      mode.value = 'exam'
      submitted.value = false
      await loadRuntime()
    }
  } finally {
    loading.value = false
  }
}

function stopTimer() {
  if (timerId.value) {
    window.clearInterval(timerId.value)
    timerId.value = null
  }
}

function stopDraftTimer() {
  if (draftTimerId.value) {
    window.clearTimeout(draftTimerId.value)
    draftTimerId.value = null
  }
}

function stopBlurCheckTimer() {
  if (blurCheckTimerId.value) {
    window.clearTimeout(blurCheckTimerId.value)
    blurCheckTimerId.value = null
  }
}

function startTimer(startTime?: string) {
  stopTimer()
  const beginAt = startTime ? new Date(startTime).getTime() : Date.now()
  const deadline = beginAt + durationMinutes.value * 60 * 1000
  const tick = async () => {
    remainingSeconds.value = Math.max(0, Math.floor((deadline - Date.now()) / 1000))
    if (remainingSeconds.value <= 0) {
      stopTimer()
      await submitCurrentExam(true)
    }
  }
  tick()
  timerId.value = window.setInterval(tick, 1000)
}

function currentAnswerPayload() {
  const question = currentQuestion.value
  if (!question) return null
  const userAnswer = question.question?.questionType === 'multiple'
    ? (multiAnswerMap.value[question.questionId] || []).join(',')
    : String(answerMap.value[question.questionId] || '')
  return {
    recordId: recordId.value,
    questionId: question.questionId,
    userAnswer,
    sessionPaperId: activeSessionPaperId.value,
  }
}

async function saveCurrentDraft() {
  const payload = currentAnswerPayload()
  if (!payload) return
  draftSaving.value = true
  try {
    await saveExamDraft(payload)
    draftHint.value = `最近一次暂存：${new Date().toLocaleTimeString()}`
  } finally {
    draftSaving.value = false
  }
}

async function submitCurrentQuestion() {
  const payload = currentAnswerPayload()
  if (!payload) return
  questionSubmitting.value = true
  try {
    await submitExamQuestion(payload)
    ElMessage.success('本题已提交')
    await refreshDraftsOnly()
    runtimeData.value.answerMap = {
      ...(runtimeData.value.answerMap || {}),
      [payload.questionId]: { userAnswer: payload.userAnswer },
    }
    if ((questionNavigationMode.value === 'lock_after_answer' || questionNavigationMode.value === 'strict_sequence')
      && currentQuestionIndex.value < currentSessionPaperQuestions.value.length - 1) {
      currentQuestionIndex.value = Math.max(currentQuestionIndex.value + 1, lockedQuestionIndex.value)
    }
  } finally {
    questionSubmitting.value = false
  }
}

async function submitCurrentSubPaper() {
  if (!activeSessionPaper.value) return
  subPaperSubmitting.value = true
  try {
    const answers = currentSessionPaperQuestions.value.map((item: any) => ({
      questionId: item.questionId,
      userAnswer: item.question?.questionType === 'multiple'
        ? (multiAnswerMap.value[item.questionId] || []).join(',')
        : String(answerMap.value[item.questionId] || ''),
    }))
    await submitExamSubPaper({
      recordId: recordId.value,
      sessionPaperId: activeSessionPaper.value.id,
      paperId: activeSessionPaper.value.paperId,
      answers,
    })
    ElMessage.success('当前子卷已提交')
    await loadRuntime()
  } finally {
    subPaperSubmitting.value = false
  }
}

async function refreshDraftsOnly() {
  const draftRes = await listExamDrafts(recordId.value)
  runtimeData.value.drafts = draftRes.data || []
}

async function confirmSubmit() {
  await ElMessageBox.confirm('确认提交整场考试吗？提交后将结束所有作答流程。', '提交考试', {
    type: 'warning',
    confirmButtonText: '确认交卷',
    cancelButtonText: '继续作答',
  })
  await submitCurrentExam(false)
}

async function submitCurrentExam(silent = false) {
  if (submitted.value || submitting.value) return
  submitting.value = true
  submitted.value = true
  stopTimer()
  const answersPayload = allQuestionEntries.value.map((item: any) => ({
    questionId: item.questionId,
    userAnswer: item.question?.questionType === 'multiple'
      ? (multiAnswerMap.value[item.questionId] || []).join(',')
      : String(answerMap.value[item.questionId] || ''),
  }))
  try {
    await submitExam({
      recordId: recordId.value,
      answers: answersPayload,
      focusLossCount: focusLossCount.value,
      flaggedCount: flaggedCount.value,
    })
    userStore.setOngoingExam(null)
    ElMessage[silent ? 'warning' : 'success'](silent ? '考试时间已结束，系统已自动交卷' : '考试提交成功')
    router.replace({
      path: `/student/exams/session/${recordId.value}`,
      query: { paperId: String(paperId.value), mode: 'result' },
    })
  } catch (error) {
    submitting.value = false
    submitted.value = false
    startTimer(String(runtimeData.value.record?.startTime || route.query.startedAt || ''))
    throw error
  }
}

function goBackToHub() {
  router.push(mode.value === 'result' ? '/student/exams?tab=records' : '/student/exams?tab=papers')
}

function increaseFocusLoss(sourceEvent = 'unknown') {
  if (mode.value !== 'exam' || submitted.value || submitting.value) return
  if (!antiCheatEnabled.value) return
  if (focusLossTracked.value) return
  focusLossTracked.value = true
  focusLossCount.value += 1
  const occurredAt = Date.now()
  saveExamDraft({
    recordId: recordId.value,
    focusLossCount: focusLossCount.value,
    focusLossSource: sourceEvent,
    focusLossOccurredAt: occurredAt,
  }).catch(() => undefined)
  if (maxFocusLossCount.value > 0 && focusLossCount.value >= maxFocusLossCount.value) {
    if (autoSubmitOnFocusLossLimit.value) {
      ElMessage.warning(`切屏次数已达到上限 ${maxFocusLossCount.value} 次，系统将自动交卷`)
      submitCurrentExam(true).catch(() => undefined)
      return
    }
    ElMessage.warning(`切屏次数已达到上限 ${maxFocusLossCount.value} 次，请不要再离开考试页面`)
  }
}

function resetFocusLossTracking() {
  focusLossTracked.value = false
}

function handleWindowBlur() {
  if (mode.value !== 'exam' || submitted.value || submitting.value) return
  stopBlurCheckTimer()
  blurCheckTimerId.value = window.setTimeout(() => {
    if (document.hidden || !document.hasFocus()) {
      increaseFocusLoss('window_blur')
    }
    blurCheckTimerId.value = null
  }, 80)
}

function handleWindowFocus() {
  stopBlurCheckTimer()
  resetFocusLossTracking()
}

function handleVisibilityChange() {
  if (document.hidden) {
    increaseFocusLoss('visibility_hidden')
    return
  }
  stopBlurCheckTimer()
  resetFocusLossTracking()
}

function handleClipboardBlocked(event: Event) {
  if (mode.value !== 'exam' || submitted.value || allowCopyPaste.value) return
  event.preventDefault()
  const now = Date.now()
  if (now - clipboardWarningAt.value < 1500) return
  clipboardWarningAt.value = now
  ElMessage.warning('当前考试已禁止复制、剪切和粘贴')
}

function handleBeforeUnload(event: BeforeUnloadEvent) {
  if (mode.value !== 'exam' || submitted.value || submitting.value) return
  event.preventDefault()
  event.returnValue = ''
}

watch([answerMap, multiAnswerMap], () => {
  if (mode.value !== 'exam' || submitted.value) return
  if (draftTimerId.value) {
    window.clearTimeout(draftTimerId.value)
  }
  draftTimerId.value = window.setTimeout(() => {
    saveCurrentDraft().catch(() => undefined)
    draftTimerId.value = null
  }, 1200)
}, { deep: true })

watch(() => [recordId.value, paperId.value, route.query.mode], async () => {
  await bootstrap()
}, { immediate: true })

onMounted(() => {
  window.addEventListener('beforeunload', handleBeforeUnload)
  window.addEventListener('blur', handleWindowBlur)
  window.addEventListener('focus', handleWindowFocus)
  document.addEventListener('visibilitychange', handleVisibilityChange)
  document.addEventListener('copy', handleClipboardBlocked)
  document.addEventListener('cut', handleClipboardBlocked)
  document.addEventListener('paste', handleClipboardBlocked)
})

onBeforeUnmount(() => {
  stopTimer()
  stopDraftTimer()
  stopBlurCheckTimer()
  window.removeEventListener('beforeunload', handleBeforeUnload)
  window.removeEventListener('blur', handleWindowBlur)
  window.removeEventListener('focus', handleWindowFocus)
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  document.removeEventListener('copy', handleClipboardBlocked)
  document.removeEventListener('cut', handleClipboardBlocked)
  document.removeEventListener('paste', handleClipboardBlocked)
})

onBeforeRouteLeave(async () => {
  if (mode.value !== 'exam' || submitted.value) return true
  try {
    await ElMessageBox.confirm('考试尚未提交，系统会保留当前草稿。确认离开吗？', '离开考试', {
      type: 'warning',
      confirmButtonText: '确认离开',
      cancelButtonText: '继续作答',
    })
    return true
  } catch {
    return false
  }
})
</script>

<style scoped>
.exam-session-page {
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

.exam-header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.exam-timer {
  text-align: center;
  padding: 16px 32px;
  border-radius: 6px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  min-width: 200px;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.02);
}

.exam-timer.is-warning {
  background: #fef2f2;
  border-color: #fecaca;
}
.exam-timer.is-warning .timer-value {
  color: #ef4444;
}

.exam-timer .timer-label {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 6px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.exam-timer .timer-value {
  font-size: 32px;
  font-family: monospace;
  font-weight: 800;
  color: #0f172a;
  line-height: 1;
}

.exam-header-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  gap: 16px;
}

.draft-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  background: #f8fafc;
  padding: 6px 12px;
  border-radius: 999px;
  border: 1px solid #f1f5f9;
}

.draft-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #10b981;
}
.draft-dot.is-saving {
  background: #f59e0b;
  animation: pulse 1.5s infinite;
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

.result-meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.result-meta-item {
  padding: 14px 16px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.result-meta-item span {
  font-size: 13px;
  color: #64748b;
  font-weight: 600;
}

.result-meta-item strong {
  font-size: 15px;
  color: #0f172a;
  line-height: 1.5;
  word-break: break-word;
}

.result-growth-rewards {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.result-growth-reward {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 6px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
}

.result-growth-reward__main {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.result-growth-reward__main strong {
  color: #0f172a;
  font-size: 15px;
}

.result-growth-reward__main span {
  color: #64748b;
  font-size: 13px;
  line-height: 1.5;
}

.result-growth-reward__point {
  color: #2563eb;
  font-size: 18px;
  font-weight: 800;
  white-space: nowrap;
}

.type-perf-sub {
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
}

.type-perf-rate {
  font-size: 16px;
  font-weight: 700;
}

.result-summary-text {
  font-size: 14px;
  color: #475569;
  line-height: 1.8;
}

.result-summary-text p {
  margin: 0;
}

.result-summary-weak {
  margin-top: 10px !important;
  color: #f59e0b;
  font-weight: 500;
}

.result-summary-tip {
  margin-top: 10px !important;
  color: #2563eb;
  font-weight: 500;
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
  transition: all 0.2s ease;
  height: auto;
  white-space: normal;
}

.exam-option-item :deep(.el-radio__label),
.exam-option-item :deep(.el-checkbox__label) {
  display: flex;
  align-items: flex-start;
  white-space: normal;
  word-break: break-word;
  line-height: 1.6;
  padding-left: 12px;
  flex: 1;
}

.exam-option-item :deep(.el-radio__input),
.exam-option-item :deep(.el-checkbox__input) {
  margin-top: 4px;
}

.exam-option-item:hover {
  border-color: #cbd5e1;
  background: #f1f5f9;
}

.exam-option-item.is-selected {
  background: #eff6ff;
  border-color: #3b82f6;
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

.sheet-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 48px;
  padding-top: 24px;
  border-top: 1px dashed #e2e8f0;
}

.sheet-footer-center {
  display: flex;
  gap: 16px;
}

.sheet-footer .el-button {
  padding: 12px 28px;
  font-size: 15px;
  border-radius: 6px;
  height: auto;
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
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  text-align: center;
}

.is-result-mode .progress-stats {
  grid-template-columns: repeat(2, 1fr);
}

.stat-box {
  background: #f8fafc;
  padding: 16px 8px;
  border-radius: 6px;
  border: 1px solid #f1f5f9;
}

.stat-val {
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 6px;
}

.stat-lbl {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.text-success .stat-val { color: #10b981; }
.text-warning .stat-val { color: #f59e0b; }

.monitor-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  text-align: center;
  font-size: 13px;
  color: #64748b;
  background: #f8fafc;
  padding: 12px;
  border-radius: 6px;
  border: 1px dashed #e2e8f0;
}

.subpaper-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.subpaper-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: #ffffff;
}

.subpaper-item:hover {
  border-color: #cbd5e1;
  background: #f8fafc;
}

.subpaper-item.is-active {
  border-color: #3b82f6;
  background: #eff6ff;
}

.subpaper-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.palette-legend {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #64748b;
  margin-bottom: 20px;
  font-weight: 500;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 3px;
  display: inline-block;
}

.legend-dot.is-answered { background: #3b82f6; }
.legend-dot.is-unanswered { background: #ffffff; border: 1px solid #cbd5e1; }
.legend-dot.is-flagged { background: #fef2f2; border: 1px solid #fecaca; }
.legend-dot.is-current { background: #ffffff; border: 2px solid #3b82f6; }

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
  cursor: pointer;
  transition: all 0.2s ease;
}

.palette-btn:hover {
  border-color: #94a3b8;
  color: #0f172a;
  background: #f8fafc;
}

.palette-btn.is-answered {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #ffffff;
}

.palette-btn.is-flagged {
  border-color: #fca5a5;
  background: #fef2f2;
  color: #ef4444;
}

.palette-btn.is-current {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px #eff6ff;
}
.palette-btn.is-current.is-answered {
  background: #3b82f6;
  color: #ffffff;
}
.palette-btn.is-disabled {
  opacity: 0.5;
  cursor: not-allowed;
  border-color: #f1f5f9;
  color: #cbd5e1;
  background: #f8fafc;
  box-shadow: none;
}

/* Result Mode Layout */
.is-result-mode {
  padding: 0;
}

.is-result-mode .exam-result-shell {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.exam-result-hero {
  padding: 32px;
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(320px, 0.9fr);
  gap: 32px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  box-shadow: 0 4px 20px -2px rgba(15, 23, 42, 0.05);
  position: relative;
  overflow: hidden;
}

.exam-result-hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3b82f6 0%, #60a5fa 100%);
}

.exam-result-hero__content {
  display: flex;
  flex-direction: column;
}

.exam-result-hero__top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exam-result-hero__eyebrow {
  display: inline-flex;
  padding: 6px 14px;
  border-radius: 999px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.exam-result-hero__title {
  margin: 16px 0 12px;
  color: #0f172a;
  font-size: 28px;
  line-height: 1.4;
  font-weight: 800;
}

.exam-result-hero__desc {
  margin: 0;
  color: #64748b;
  line-height: 1.6;
  font-size: 15px;
}

.exam-result-hero__stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  align-items: center;
}

.exam-result-metric {
  padding: 20px;
  text-align: center;
  border-radius: 6px;
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.exam-result-metric:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.04);
}

.exam-result-metric span {
  font-size: 13px;
  color: #64748b;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.exam-result-metric strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
  color: #3b82f6;
  font-weight: 800;
}

.exam-result-insights {
  margin-top: 24px;
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  border-radius: 6px;
  box-shadow: 0 4px 20px -2px rgba(15, 23, 42, 0.05);
  border: 1px solid #e2e8f0;
}

.exam-result-insights__card {
  padding: 20px;
  border-radius: 6px;
  border: 1px solid #f1f5f9;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 10px;
  text-align: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.exam-result-insights__card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.04);
}

.exam-result-insights__card span {
  font-size: 14px;
  color: #64748b;
  font-weight: 600;
}

.exam-result-insights__card strong {
  font-size: 28px;
  color: #0f172a;
  font-weight: 800;
}

.exam-result-insights__card.text-success strong { color: #10b981; }
.exam-result-insights__card.text-danger strong { color: #ef4444; }
.exam-result-insights__card.text-primary strong { color: #3b82f6; }

.exam-result-performance {
  margin-top: 24px;
  padding: 24px;
  border-radius: 6px;
  box-shadow: 0 4px 20px -2px rgba(15, 23, 42, 0.05);
  border: 1px solid #e2e8f0;
}

.exam-result-performance__header {
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #3b82f6;
}

.exam-result-performance__title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 800;
}

.exam-result-performance__subtitle {
  margin-top: 8px;
  color: #64748b;
  font-size: 14px;
  line-height: 1.6;
}

.exam-result-performance__grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.exam-result-performance__card {
  padding: 20px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.02);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.exam-result-performance__type {
  color: #0f172a;
  font-size: 16px;
  font-weight: 800;
}

.exam-result-performance__meta {
  margin-top: 10px;
  color: #64748b;
  font-size: 13px;
}

.exam-result-performance__value {
  margin-top: 12px;
  color: #3b82f6;
  font-size: 26px;
  font-weight: 800;
}

.exam-result-summary {
  margin-top: 24px;
  padding: 24px;
  border-radius: 6px;
  box-shadow: 0 4px 20px -2px rgba(15, 23, 42, 0.05);
  border: 1px solid #e2e8f0;
}

.exam-result-summary__title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 800;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #3b82f6;
}

.exam-result-summary__list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.exam-result-summary__item {
  padding: 16px 20px;
  border-radius: 6px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #334155;
  font-size: 15px;
  line-height: 1.6;
  font-weight: 500;
}

.exam-result-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.exam-result-answers {
  display: flex;
  gap: 20px;
}

.answer-box {
  flex: 1;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 16px;
}

.answer-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 600;
  margin-bottom: 8px;
}

.answer-content {
  font-size: 16px;
  font-weight: 700;
  word-break: break-all;
}

.exam-result-analysis {
  background: #f0fdfa;
  border: 1px solid #fef3c7;
  border-radius: 6px;
  padding: 16px;
}

.analysis-label {
  font-size: 13px;
  color: #d97706;
  font-weight: 600;
  margin-bottom: 8px;
}

.analysis-content {
  font-size: 15px;
  color: #92400e;
  line-height: 1.6;
}

.question-score-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 700;
  padding: 6px 16px;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  color: #64748b;
}

.question-score-badge i {
  font-size: 16px;
}

.question-score-badge.is-correct {
  color: #10b981;
  background: #ecfdf5;
  border-color: #a7f3d0;
}

.question-score-badge.is-wrong {
  color: #ef4444;
  background: #fef2f2;
  border-color: #fecaca;
}

.mt-3 { margin-top: 12px; }
.mt-4 { margin-top: 16px; }
.w-full { width: 100%; }
.flex-1 { flex: 1; }
.text-danger { color: #ef4444 !important; }
.text-success { color: #10b981 !important; }
.text-warning { color: #f59e0b !important; }

@media (max-width: 1200px) {
  .exam-main-layout {
    grid-template-columns: minmax(0, 1fr) 280px;
  }
  .exam-result-hero {
    grid-template-columns: 1fr;
  }
  .exam-result-insights {
    grid-template-columns: repeat(2, minmax(0, 1fr));
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
  .exam-header-center, .exam-header-right {
    width: 100%;
    align-items: flex-start;
    justify-content: flex-start;
  }
  .header-actions {
    margin-top: 12px;
  }
  .result-meta-grid {
    grid-template-columns: 1fr;
  }
}
</style>
