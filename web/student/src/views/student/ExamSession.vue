<template>
  <div class="exam-session-page">
    <section class="exam-session-shell" :class="{ 'is-result-mode': mode === 'result' }" v-loading="loading">
      <template v-if="mode === 'exam' && questionList.length">
        <aside class="exam-session-sidebar portal-card">
          <div class="exam-session-sidebar__header">
            <div class="exam-session-sidebar__eyebrow">Runtime Exam</div>
            <h1 class="exam-session-sidebar__title">{{ paper.paperName || '在线考试' }}</h1>
            <p class="exam-session-sidebar__desc">
              系统已接入自动保存、分题提交和子卷提交。建议完成当前子卷后再进入下一阶段。
            </p>
          </div>

          <div class="exam-session-timer">
            <div class="exam-session-timer__label">剩余时间</div>
            <div class="exam-session-timer__value" :class="{ 'is-warning': remainingSeconds <= 300 }">
              {{ formatDuration(remainingSeconds) }}
            </div>
          </div>

          <div class="exam-session-stats">
            <div class="exam-session-stat"><span>总题数</span><strong>{{ questionCount }}</strong></div>
            <div class="exam-session-stat"><span>已作答</span><strong>{{ answeredCount }}</strong></div>
            <div class="exam-session-stat"><span>未作答</span><strong>{{ unansweredCount }}</strong></div>
            <div class="exam-session-stat"><span>当前子卷</span><strong>{{ currentSessionPaperLabel }}</strong></div>
            <div class="exam-session-stat"><span>切屏次数</span><strong>{{ focusLossCount }}</strong></div>
            <div class="exam-session-stat"><span>异常标记</span><strong>{{ flaggedCount }}</strong></div>
          </div>

          <div class="exam-session-actions">
            <el-button plain @click="goBackToHub">返回评测中心</el-button>
            <el-button
              v-if="activeSessionPaper?.paperLevel === 'SUB'"
              type="warning"
              plain
              :loading="subPaperSubmitting"
              @click="submitCurrentSubPaper"
            >
              提交当前子卷
            </el-button>
            <el-button type="primary" :loading="submitting" @click="confirmSubmit">立即交卷</el-button>
          </div>

          <div class="exam-session-card">
            <div class="exam-session-card__title">子卷状态</div>
            <div class="exam-subpaper-list">
              <button
                v-for="item in sessionPapers"
                :key="item.id"
                type="button"
                class="exam-subpaper-item"
                :class="{
                  'is-current': activeSessionPaper?.id === item.id,
                  'is-submitted': item.paperStatus === 'SUBMITTED',
                  'is-pending': item.paperStatus === 'PENDING',
                }"
                @click="activateSessionPaper(item.id)"
              >
                <div class="exam-subpaper-item__name">{{ paperNameMap[item.paperId] || `试卷 ${item.paperId}` }}</div>
                <div class="exam-subpaper-item__meta">
                  <span>{{ item.paperLevel }}</span>
                  <span>{{ item.answerMode }}</span>
                  <span>{{ item.paperStatus }}</span>
                </div>
              </button>
            </div>
          </div>

          <div class="exam-session-card">
            <div class="exam-session-card__title">答题卡</div>
            <div class="exam-session-palette">
              <button
                v-for="item in currentSessionPaperQuestions"
                :key="item.questionId"
                type="button"
                class="exam-session-palette__item"
                :class="{
                  'is-current': currentQuestionIndex === item.index,
                  'is-answered': item.answered,
                  'is-flagged': item.flagged,
                }"
                @click="goToQuestion(item.index)"
              >
                {{ item.order }}
              </button>
            </div>
          </div>
        </aside>

        <section class="exam-session-main">
          <el-alert
            v-if="showUrgentWarning"
            type="warning"
            :closable="false"
            class="exam-session-warning"
            title="距离考试结束不足 5 分钟，请尽快完成检查并提交试卷。"
          />

          <div class="exam-session-topbar portal-card">
            <div class="exam-session-topbar__meta">
              <span>{{ courseLabel(paper.courseId) }}</span>
              <span>{{ paper.durationMinutes || 0 }} 分钟</span>
              <span>总分 {{ paper.totalScore || 0 }}</span>
              <span v-if="activeSessionPaper">子卷状态 {{ activeSessionPaper.paperStatus }}</span>
            </div>
            <div class="exam-session-topbar__draft">
              <el-tag effect="plain" type="info">已接入后端草稿</el-tag>
              <span>{{ draftHint }}</span>
            </div>
          </div>

          <article v-if="currentQuestion" class="exam-question-sheet portal-card">
            <div class="exam-question-sheet__progress">
              <span>当前子卷</span>
              <strong>{{ currentSessionPaperLabel }}</strong>
            </div>

            <div class="exam-question-sheet__header">
              <div>
                <div class="exam-question-sheet__index">第 {{ currentQuestionIndex + 1 }} 题</div>
                <div class="exam-question-sheet__type">{{ questionTypeLabel(currentQuestion.question?.questionType) }}</div>
              </div>
              <div class="exam-question-sheet__score">{{ currentQuestion.score || 0 }} 分</div>
            </div>

            <div class="exam-question-sheet__stem">
              {{ stripHtml(currentQuestion.question?.stem) }}
            </div>

            <div v-if="currentQuestion.question?.questionType === 'single'" class="exam-question-sheet__body">
              <el-radio-group v-model="answerMap[currentQuestion.questionId]" class="exam-options">
                <el-radio
                  v-for="opt in currentQuestion.question?.options || []"
                  :key="opt.optionId"
                  :label="opt.optionKey"
                  class="exam-option"
                >
                  {{ opt.optionKey }}. {{ opt.optionContent }}
                </el-radio>
              </el-radio-group>
            </div>

            <div v-else-if="currentQuestion.question?.questionType === 'multiple'" class="exam-question-sheet__body">
              <el-checkbox-group v-model="multiAnswerMap[currentQuestion.questionId]" class="exam-options">
                <el-checkbox
                  v-for="opt in currentQuestion.question?.options || []"
                  :key="opt.optionId"
                  :label="opt.optionKey"
                  class="exam-option"
                >
                  {{ opt.optionKey }}. {{ opt.optionContent }}
                </el-checkbox>
              </el-checkbox-group>
            </div>

            <div v-else class="exam-question-sheet__body">
              <el-input
                v-model="answerMap[currentQuestion.questionId]"
                type="textarea"
                :rows="4"
                placeholder="请输入你的答案"
              />
            </div>

            <div class="exam-question-sheet__footer">
              <el-button :disabled="currentQuestionIndex <= 0" @click="goToQuestion(currentQuestionIndex - 1)">上一题</el-button>
              <div class="exam-question-sheet__footer-meta">
                <el-button link type="info" :loading="draftSaving" @click="saveCurrentDraft">保存草稿</el-button>
                <el-button link type="primary" :loading="questionSubmitting" @click="submitCurrentQuestion">提交本题</el-button>
                <el-tag :type="isAnswered(currentQuestion) ? 'success' : 'warning'" effect="plain">
                  {{ isAnswered(currentQuestion) ? '已作答' : '未作答' }}
                </el-tag>
                <el-button link type="primary" @click="toggleFlagCurrentQuestion">
                  {{ isFlagged(currentQuestion.questionId) ? '取消标记' : '标记稍后检查' }}
                </el-button>
              </div>
              <el-button type="primary" :disabled="currentQuestionIndex >= currentSessionPaperQuestions.length - 1" @click="goToQuestion(currentQuestionIndex + 1)">下一题</el-button>
            </div>
          </article>
        </section>
      </template>

      <template v-else-if="mode === 'result'">
        <section class="exam-result-shell">
          <div class="exam-result-hero portal-card">
            <div>
              <div class="exam-result-hero__eyebrow">Submitted</div>
              <h1 class="exam-result-hero__title">{{ resultPaper.paperName || '考试结果' }}</h1>
              <p class="exam-result-hero__desc">本次考试已提交。你可以查看整卷结果、分层子卷统计和逐题回看信息。</p>
            </div>
            <div class="exam-result-hero__stats">
              <div class="exam-result-metric"><span>总得分</span><strong>{{ resultRecord.record?.score || 0 }}</strong></div>
              <div class="exam-result-metric"><span>正确率</span><strong>{{ resultRecord.record?.correctRate || 0 }}%</strong></div>
              <div class="exam-result-metric"><span>子卷数</span><strong>{{ resultRecord.subPaperStats?.length || 0 }}</strong></div>
            </div>
          </div>

          <el-table :data="resultRecord.subPaperStats || []" border class="portal-card">
            <el-table-column prop="paperName" label="子试卷" min-width="180" />
            <el-table-column prop="paperLevel" label="层级" width="100" />
            <el-table-column prop="answerMode" label="模式" width="110" />
            <el-table-column prop="submittedCount" label="已提交" width="100" />
            <el-table-column prop="skippedCount" label="跳过" width="100" />
            <el-table-column prop="avgScore" label="平均得分" width="120" />
            <el-table-column prop="avgCorrectRate" label="平均正确率" width="120" />
          </el-table>

          <el-table :data="filteredResultAnswers" border class="portal-card exam-result-table">
            <el-table-column prop="questionId" label="题目ID" width="90" />
            <el-table-column label="题型" width="110">
              <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
            </el-table-column>
            <el-table-column label="题干" min-width="260" show-overflow-tooltip>
              <template #default="scope">{{ stripHtml(scope.row.stem) }}</template>
            </el-table-column>
            <el-table-column prop="userAnswer" label="我的答案" min-width="160" show-overflow-tooltip />
            <el-table-column prop="standardAnswer" label="标准答案" min-width="160" show-overflow-tooltip />
            <el-table-column prop="analysis" label="解析" min-width="200" show-overflow-tooltip />
            <el-table-column label="结果" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isCorrect === '1' ? 'success' : 'danger'">
                  {{ scope.row.isCorrect === '1' ? '正确' : '错误' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="score" label="得分" width="90" />
          </el-table>
        </section>
      </template>

      <el-empty v-else description="考试数据加载失败或试卷内容为空" />
    </section>
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

const recordId = computed(() => Number(route.params.recordId || 0))
const paperId = computed(() => Number(route.query.paperId || 0))
const durationMinutes = computed(() => Number(paper.value.durationMinutes || 0))
const questionList = computed(() => paper.value.questions || [])
const questionCount = computed(() => currentSessionPaperQuestions.value.length)
const answeredCount = computed(() => currentSessionPaperQuestions.value.filter((item: any) => item.answered).length)
const unansweredCount = computed(() => Math.max(0, questionCount.value - answeredCount.value))
const currentQuestion = computed(() => currentSessionPaperQuestions.value[currentQuestionIndex.value] || null)
const showUrgentWarning = computed(() => mode.value === 'exam' && remainingSeconds.value > 0 && remainingSeconds.value <= 300)
const flaggedCount = computed(() => flaggedQuestionIds.value.length)
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
const filteredResultAnswers = computed(() => resultRecord.value.answers || [])

function stripHtml(value: string) {
  return String(value || '').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

function questionTypeLabel(type: string) {
  return ({ single: '单选题', multiple: '多选题', judge: '判断题', fill: '填空题', essay: '简答题', material: '材料题', case: '案例题' } as any)[type] || type || '-'
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

function isAnswered(item: any) {
  if (item.question?.questionType === 'multiple') {
    return Boolean((multiAnswerMap.value[item.questionId] || []).length)
  }
  return String(answerMap.value[item.questionId] || '').trim().length > 0
}

function isFlagged(questionId?: number) {
  return questionId ? flaggedQuestionIds.value.includes(questionId) : false
}

function goToQuestion(index: number) {
  const safeIndex = Math.max(0, Math.min(index, Math.max(0, questionCount.value - 1)))
  currentQuestionIndex.value = safeIndex
}

function activateSessionPaper(sessionPaperId: number) {
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
  currentQuestionIndex.value = 0
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
  const answersPayload = questionList.value.map((item: any) => ({
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

function increaseFocusLoss() {
  if (mode.value !== 'exam' || submitted.value || submitting.value) return
  focusLossCount.value += 1
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
  window.addEventListener('blur', increaseFocusLoss)
  document.addEventListener('visibilitychange', () => {
    if (document.hidden) increaseFocusLoss()
  })
})

onBeforeUnmount(() => {
  stopTimer()
  stopDraftTimer()
  window.removeEventListener('beforeunload', handleBeforeUnload)
  window.removeEventListener('blur', increaseFocusLoss)
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
}
.exam-session-shell {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 16px;
}
.exam-session-shell.is-result-mode {
  grid-template-columns: 1fr;
}
.exam-session-sidebar,
.exam-session-topbar,
.exam-question-sheet,
.exam-result-hero,
.exam-result-table {
  border-radius: 6px;
  border: 1px solid #d7dee9;
  background: #fff;
}
.exam-session-sidebar {
  position: sticky;
  top: 16px;
  align-self: start;
  padding: 16px;
}
.exam-session-sidebar__eyebrow,
.exam-result-hero__eyebrow {
  display: inline-flex;
  padding: 2px 8px;
  border-radius: 4px;
  background: #eef4ff;
  color: #315fca;
  font-size: 12px;
  font-weight: 700;
}
.exam-session-sidebar__title,
.exam-result-hero__title {
  margin-top: 10px;
  font-size: 24px;
  line-height: 1.35;
  font-weight: 800;
  color: var(--portal-text);
}
.exam-session-sidebar__desc,
.exam-result-hero__desc {
  margin-top: 10px;
  color: var(--portal-text-secondary);
  line-height: 1.8;
  font-size: 14px;
}
.exam-session-timer,
.exam-session-card,
.exam-session-stat,
.exam-result-metric {
  border-radius: 4px;
  background: #fafbfd;
  border: 1px solid #d7dee9;
}
.exam-session-timer {
  margin-top: 16px;
  padding: 14px 16px;
}
.exam-session-timer__label {
  color: var(--portal-text-secondary);
  font-size: 12px;
}
.exam-session-timer__value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 800;
}
.exam-session-timer__value.is-warning {
  color: #d97706;
}
.exam-session-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  margin-top: 16px;
}
.exam-session-stat,
.exam-result-metric {
  padding: 12px;
}
.exam-session-stat span,
.exam-result-metric span {
  font-size: 12px;
  color: var(--portal-text-secondary);
}
.exam-session-stat strong,
.exam-result-metric strong {
  display: block;
  margin-top: 8px;
  font-size: 22px;
  color: var(--portal-brand);
}
.exam-session-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 14px;
}
.exam-session-card {
  margin-top: 16px;
  padding: 12px;
}
.exam-session-card__title {
  font-size: 14px;
  font-weight: 700;
  color: var(--portal-text);
}
.exam-subpaper-list {
  display: grid;
  gap: 10px;
  margin-top: 10px;
}
.exam-subpaper-item {
  text-align: left;
  border: 1px solid #d7dee9;
  background: #fff;
  border-radius: 4px;
  padding: 10px;
  cursor: pointer;
}
.exam-subpaper-item.is-current {
  border-color: #315fca;
  background: #eef4ff;
}
.exam-subpaper-item.is-submitted {
  border-color: #16a34a;
}
.exam-subpaper-item.is-pending {
  border-color: #d97706;
}
.exam-subpaper-item__name {
  font-weight: 700;
  color: var(--portal-text);
}
.exam-subpaper-item__meta {
  margin-top: 6px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  color: var(--portal-text-secondary);
  font-size: 12px;
}
.exam-session-palette {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 8px;
  margin-top: 10px;
}
.exam-session-palette__item {
  height: 36px;
  border-radius: 4px;
  border: 1px solid #d7dee9;
  background: #fff;
  color: var(--portal-text);
  cursor: pointer;
}
.exam-session-palette__item.is-current {
  border-color: #315fca;
  background: #eef4ff;
}
.exam-session-palette__item.is-answered {
  background: #315fca;
  border-color: #315fca;
  color: #fff;
}
.exam-session-palette__item.is-flagged:not(.is-current) {
  border-color: #d97706;
  background: #fff7ed;
  color: #b45309;
}
.exam-session-topbar {
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.exam-session-topbar__meta,
.exam-session-topbar__draft {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
  color: var(--portal-text-secondary);
  font-size: 13px;
}
.exam-question-sheet {
  margin-top: 14px;
  padding: 16px;
}
.exam-question-sheet__progress {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5eaf1;
  color: var(--portal-text-secondary);
  font-size: 12px;
}
.exam-question-sheet__header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}
.exam-question-sheet__index {
  font-size: 16px;
  font-weight: 800;
  color: var(--portal-text);
}
.exam-question-sheet__type {
  margin-top: 4px;
  color: var(--portal-text-secondary);
  font-size: 12px;
}
.exam-question-sheet__score {
  padding: 4px 10px;
  border-radius: 4px;
  background: #f5f8fd;
  border: 1px solid #d7dee9;
  color: #315fca;
  font-weight: 700;
}
.exam-question-sheet__stem {
  margin-top: 14px;
  line-height: 1.8;
  color: var(--portal-text);
  font-size: 14px;
}
.exam-question-sheet__body {
  margin-top: 14px;
}
.exam-options {
  display: grid;
  gap: 10px;
}
.exam-option {
  margin-right: 0;
  padding: 10px 12px;
  border-radius: 4px;
  border: 1px solid #d7dee9;
  background: #fafbfd;
}
.exam-question-sheet__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
  padding-top: 14px;
  border-top: 1px solid #e5eaf1;
}
.exam-question-sheet__footer-meta {
  flex: 1;
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}
.exam-result-shell {
  display: grid;
  gap: 14px;
}
.exam-result-hero {
  padding: 16px;
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(280px, 0.9fr);
  gap: 16px;
}
.exam-result-hero__stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
}
@media (max-width: 1080px) {
  .exam-session-shell {
    grid-template-columns: 1fr;
  }
  .exam-session-sidebar {
    position: static;
  }
  .exam-result-hero {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 768px) {
  .exam-session-stats,
  .exam-result-hero__stats {
    grid-template-columns: 1fr;
  }
  .exam-session-topbar,
  .exam-question-sheet__footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
