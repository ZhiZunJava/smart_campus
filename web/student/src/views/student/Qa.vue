<template>
  <div class="qa-workbench">
    <aside class="qa-sidebar">

      <t-button theme="primary" class="qa-sidebar__new" @click="createNewChat">
        <i class="ri-add-line"></i>
        <span>新对话</span>
      </t-button>

      <t-input v-model="sessionKeyword" class="qa-sidebar__search" placeholder="搜索历史对话" clearable>
        <template #prefix-icon><i class="ri-search-line"></i></template>
      </t-input>

      <div class="qa-sidebar__section">历史对话</div>
      <div class="qa-sidebar__history">
        <div class="qa-sidebar__history-list">
          <div v-for="item in filteredSessions" :key="item.sessionId" class="qa-session"
            :class="{ active: item.sessionId === activeSessionId }" @click="openSession(item)">
            <div class="qa-session__main">
              <div class="qa-session__title-row">
                <div class="qa-session__title">{{ item.sessionTitle || `会话 ${item.sessionId}` }}</div>
                <i v-if="isPinned(item.sessionId)" class="ri-pushpin-2-fill qa-session__pin"></i>
              </div>
              <div class="qa-session__meta">{{ item.createTime || `ID: ${item.sessionId}` }}</div>
            </div>
            <t-dropdown
              trigger="click"
              :options="buildSessionActionOptions(item)"
              @click="(data: any) => handleSessionCommand(String(data.value), item)"
            >
              <t-button theme="default" variant="text" shape="square" class="qa-session__more" @click.stop>
                <i class="ri-more-2-fill"></i>
              </t-button>
            </t-dropdown>
          </div>
          <t-empty v-if="!filteredSessions.length" :image-size="64" description="暂无历史对话" />
        </div>
      </div>
    </aside>

    <section class="qa-main">
      <main class="qa-main__messages">
        <div class="qa-main__floating-header">
          <div class="qa-main__title-anchor">
            <t-input
              v-if="titleInputFocused"
              ref="titleInputRef"
              v-model="editingSessionTitle"
              class="qa-main__title-input"
              maxlength="50"
              placeholder="给这次对话起个标题"
              @blur="handleTitleBlur"
              @enter="handleTitleEnter"
            />
            <button
              v-else
              type="button"
              class="qa-main__title-display"
              :class="{ 'is-placeholder': !editingSessionTitle?.trim() }"
              @click="activateTitleInput"
            >
              {{ editingSessionTitle?.trim() || '给这次对话起个标题' }}
            </button>
          </div>
          <div class="qa-main__actions">
            <t-tag class="qa-main__pill" theme="primary" variant="light">
              <i class="ri-robot-2-line"></i>{{ selectedModel?.modelName || '未选择模型' }}
            </t-tag>
            <t-button v-if="streaming" theme="danger" variant="outline" size="small" class="qa-main__danger" @click="stopStreaming">
              <i class="ri-stop-circle-line"></i>
              <span>停止生成</span>
            </t-button>
          </div>
        </div>
        <QaTDesignMessagePane
          :messages="chatMessages"
          :streaming="streaming"
          :current-streaming-assistant-id="currentStreamingAssistantId"
          :suggestion-list="suggestionList"
          @apply-suggestion="applySuggestion"
          @open-attachment="openAttachment"
          @copy="copyText"
          @continue="continueFromAnswer"
          @reask="reAskMessage"
          @feedback="handlePocFeedback"
        />
      </main>

      <footer class="qa-composer">
        <QaTDesignSenderPane
          v-model="inputText"
          v-model:selected-model-id="selectedModelId"
          v-model:selected-course-id="selectedCourseId"
          v-model:selected-exam-record-id="selectedExamRecordId"
          v-model:deep-thinking="deepThinking"
          :streaming="streaming"
          :images="images"
          :model-options="modelOptions"
          :course-options="courseSelectOptions"
          :exam-options="examSelectOptions"
          :reasoning-supported="selectedModel?.supportReasoning === '1'"
          :vision-supported="selectedModel?.supportVision === '1'"
          :context-summary="currentSenderContextSummary"
          :show-context-clear="showSenderContextClear"
          @submit="handleSubmit"
          @stop="stopStreaming"
          @upload-images="handleTdesignFileSelect"
          @remove-image="removeImage"
          @open-attachment="openAttachment"
          @clear-context="clearSenderContext"
          @course-change="handleCourseContextChange"
          @apply-course-plan="applyCoursePlanPrompt"
          @apply-exam-analysis="applyExamAnalysisPrompt"
          @apply-wrongbook-plan="applyWrongbookPlanPrompt"
        />
      </footer>
    </section>

    <t-dialog
      v-model:visible="renameDialogOpen"
      attach="body"
      placement="center"
      :z-index="5600"
      header="重命名会话"
      width="480px"
      class="qa-rename-dialog"
      :confirm-btn="{ content: '确定', theme: 'primary' }"
      :cancel-btn="{ content: '取消', theme: 'default', variant: 'outline' }"
      @confirm="confirmRenameDialog"
      @close="closeRenameDialog"
    >
      <t-input
        v-model="renameSessionTitle"
        autofocus
        maxlength="50"
        placeholder="请输入新的会话名称"
      />
    </t-dialog>

  </div>
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent, nextTick, onMounted, ref, watch } from 'vue'
import {
  Button as TButton,
  Dialog as TDialog,
  DialogPlugin,
  Dropdown as TDropdown,
  Empty as TEmpty,
  Input as TInput,
  MessagePlugin,
  Tag as TTag,
} from 'tdesign-vue-next'
import { getToken } from '@/utils/auth'
import { normalizeQaErrorMessage, parseAssistantReferenceSource, parseQaAttachments } from '@/utils/qaMessage'
import usePortalUserStore from '@/store/user'
import { consumeScoreAnalysisQaPreset } from '@/utils/qaPreset'
import { addQaFeedback, addQaSession, deletePortalQaAttachment, deleteQaSession, fetchPortalCourseOptions, listExamRecord, listQaMessage, listQaModelOptions, listQaSession, updateQaSession, uploadPortalQaAttachment } from '@/api/portal'

const SESSION_PIN_KEY = 'qa-session-pins'
const SESSION_ACTIVE_KEY = 'qa-active-session-id'
const QaTDesignMessagePane = defineAsyncComponent(() => import('@/components/qa/QaTDesignMessagePane.vue'))
const QaTDesignSenderPane = defineAsyncComponent(() => import('@/components/qa/QaTDesignSenderPane.vue'))

const userStore = usePortalUserStore()

const inputText = ref('')
const streaming = ref(false)
const deepThinking = ref(false)
const selectedModelId = ref<number | undefined>()
const modelOptions = ref<any[]>([])
const courseOptions = ref<Array<{ label: string; value: number }>>([])
const selectedCourseId = ref<number | undefined>()
const examRecordOptions = ref<Array<{ label: string; value: number; raw: any }>>([])
const selectedExamRecordId = ref<number | undefined>()
const images = ref<Array<{ attachmentId?: number; name: string; imageUrl: string; fileName?: string; originalName?: string; mimeType: string; size?: number }>>([])
const sessions = ref<any[]>([])
const sessionKeyword = ref('')
const activeSessionId = ref<number | undefined>()
const pinnedSessionIds = ref<number[]>([])
const chatMessages = ref<Array<{ id: string; role: 'user' | 'assistant'; content: string; modelName?: string; reasoningContent?: string; attachments?: any[]; messageId?: number; feedbackType?: string; feedbackContent?: string }>>([])
const currentAbortController = ref<AbortController | null>(null)
const currentStreamingAssistantId = ref('')
const streamChunkBuffers = ref<Record<string, { content: string; reasoning: string }>>({})
const streamFlushTimers = ref<Record<string, number>>({})
const titleInputFocused = ref(false)
const editingSessionTitle = ref('')
const titleInputRef = ref<any>(null)
const renameDialogOpen = ref(false)
const renamingSession = ref<any>(null)
const renameSessionTitle = ref('')
const feedbackSubmittingMessageIds = ref<number[]>([])
const externalContextLabel = ref('')
const externalContextPrompt = ref('')

const suggestionList = [
  '帮我总结这一章的知识点',
  '这道题为什么这么做？',
  '请帮我生成复习计划',
  '请结合图片内容给我解释',
  '下一次考试我该怎么提高成绩？',
]

const selectedModel = computed(() => modelOptions.value.find((item) => item.modelId === selectedModelId.value))
const streamEnabled = computed(() => selectedModel.value?.supportStream === '1')
const currentSenderContextSummary = computed(() => {
  const parts: string[] = []
  if (selectedExamRecord.value) {
    const paperName = selectedExamRecord.value.paperName || `考试 ${selectedExamRecord.value.recordId}`
    parts.push(`考试记录：${paperName}｜课程：${courseContextLabel(selectedExamRecord.value.courseId)}｜得分 ${selectedExamRecord.value.score ?? 0}`)
  } else if (selectedCourse.value) {
    parts.push(`课程上下文：${selectedCourse.value.label}`)
  }
  if (externalContextLabel.value) {
    parts.push(externalContextLabel.value)
  }
  return parts.join(' ｜ ')
})
const showSenderContextClear = computed(() => Boolean(selectedExamRecord.value || selectedCourse.value || externalContextPrompt.value))
const courseSelectOptions = computed(() => courseOptions.value.map((item) => ({ label: item.label, value: item.value })))
const examSelectOptions = computed(() => examRecordOptions.value.map((item) => ({ label: item.label, value: item.value })))
const filteredSessions = computed(() => {
  const keyword = sessionKeyword.value.trim().toLowerCase()
  let list = sessions.value
  if (keyword) {
    list = list.filter((item) => String(item.sessionTitle || '').toLowerCase().includes(keyword))
  }
  return [...list].sort((left, right) => {
    const leftPinned = isPinned(left.sessionId)
    const rightPinned = isPinned(right.sessionId)
    if (leftPinned && !rightPinned) return -1
    if (!leftPinned && rightPinned) return 1
    return 0
  })
})
const currentSessionTitle = computed(() => {
  const session = sessions.value.find((item) => item.sessionId === activeSessionId.value)
  return session?.sessionTitle || '新对话'
})
const selectedCourse = computed(() => courseOptions.value.find((item) => item.value === selectedCourseId.value))
const selectedExamRecord = computed(() => examRecordOptions.value.find((item) => item.value === selectedExamRecordId.value)?.raw)

watch(currentSessionTitle, (value) => {
  editingSessionTitle.value = value
}, { immediate: true })

watch(selectedModelId, () => {
  syncModelCapabilities()
})

function loadPinnedSessions() {
  try {
    pinnedSessionIds.value = JSON.parse(localStorage.getItem(SESSION_PIN_KEY) || '[]')
  } catch {
    pinnedSessionIds.value = []
  }
}

function savePinnedSessions() {
  localStorage.setItem(SESSION_PIN_KEY, JSON.stringify(pinnedSessionIds.value))
}

function loadActiveSessionId() {
  const value = localStorage.getItem(SESSION_ACTIVE_KEY)
  activeSessionId.value = value ? Number(value) || undefined : undefined
}

function saveActiveSessionId(sessionId?: number) {
  if (!sessionId) {
    localStorage.removeItem(SESSION_ACTIVE_KEY)
    return
  }
  localStorage.setItem(SESSION_ACTIVE_KEY, String(sessionId))
}

function buildSessionActionOptions(item: any) {
  return [
    { content: isPinned(item.sessionId) ? '取消置顶' : '置顶会话', value: 'pin', icon: 'ri-pushpin-line' },
    { content: '重命名', value: 'rename', icon: 'ri-edit-line' },
    { content: '删除', value: 'delete', icon: 'ri-delete-bin-6-line', theme: 'error' as const },
  ]
}

function isPinned(sessionId: number) {
  return pinnedSessionIds.value.includes(sessionId)
}

function togglePin(sessionId: number) {
  if (isPinned(sessionId)) {
    pinnedSessionIds.value = pinnedSessionIds.value.filter((item) => item !== sessionId)
  } else {
    pinnedSessionIds.value.unshift(sessionId)
  }
  savePinnedSessions()
}

async function loadModels() {
  const res = await listQaModelOptions()
  modelOptions.value = res.data || []
  if (!selectedModelId.value && modelOptions.value.length) {
    selectedModelId.value = modelOptions.value[0].modelId
    syncModelCapabilities()
  }
}

async function loadCourseOptions() {
  courseOptions.value = await fetchPortalCourseOptions(userStore.user?.userId)
}

async function loadExamRecordOptions() {
  if (!userStore.user?.userId) {
    examRecordOptions.value = []
    return
  }
  const res = await listExamRecord({
    pageNum: 1,
    pageSize: 30,
    userId: userStore.user.userId,
    courseId: selectedCourseId.value,
  })
  examRecordOptions.value = (res.rows || []).map((item: any) => ({
    value: item.recordId,
    raw: item,
    label: `${item.paperName || `考试 ${item.recordId}`}｜${courseContextLabel(item.courseId)}｜得分 ${item.score ?? 0}`,
  }))
  if (selectedExamRecordId.value && !examRecordOptions.value.some((item) => item.value === selectedExamRecordId.value)) {
    selectedExamRecordId.value = undefined
  }
}

async function loadSessions() {
  if (!userStore.user?.userId) return
  const res = await listQaSession({ pageNum: 1, pageSize: 100, userId: userStore.user.userId })
  sessions.value = res.rows || []
}

async function openSession(item: any) {
  externalContextLabel.value = ''
  externalContextPrompt.value = ''
  activeSessionId.value = item.sessionId
  saveActiveSessionId(item.sessionId)
  selectedCourseId.value = Number(item.courseId || 0) || undefined
  await loadExamRecordOptions()
  const res = await listQaMessage({ pageNum: 1, pageSize: 200, sessionId: item.sessionId })
  chatMessages.value = (res.rows || []).map((message: any) => {
    const role = message.roleType === 'assistant' ? 'assistant' : 'user'
    const meta = role === 'assistant'
      ? parseAssistantReferenceSource(message.referenceSource)
      : { modelName: '', reasoningContent: '' }
    return {
      id: `${message.roleType}_${message.messageId}`,
      messageId: message.messageId,
      role,
      content: message.content,
      modelName: message.modelName || meta.modelName || '',
      reasoningContent: message.reasoningContent || meta.reasoningContent || '',
      attachments: parseQaAttachments(message.attachmentJson),
      feedbackType: message.feedbackType,
      feedbackContent: message.feedbackContent,
    }
  })
}

async function createNewChat() {
  activeSessionId.value = undefined
  saveActiveSessionId()
  chatMessages.value = []
  inputText.value = ''
  images.value = []
  editingSessionTitle.value = '新对话'
  selectedExamRecordId.value = undefined
  selectedCourseId.value = undefined
  externalContextLabel.value = ''
  externalContextPrompt.value = ''
}

async function handleSessionCommand(command: string, item: any) {
  if (command === 'pin') {
    togglePin(item.sessionId)
    return
  }
  if (command === 'rename') {
    openRenameDialog(item)
    return
  }
  if (command === 'delete') {
    const confirmed = await confirmWithTDesign('确认删除该历史会话吗？删除后不可恢复。')
    if (!confirmed) return
    await deleteQaSession(item.sessionId)
    MessagePlugin.success('删除成功')
    pinnedSessionIds.value = pinnedSessionIds.value.filter((id) => id !== item.sessionId)
    savePinnedSessions()
    if (item.sessionId === activeSessionId.value) await createNewChat()
    await loadSessions()
  }
}

function openRenameDialog(session: any) {
  renamingSession.value = session
  renameSessionTitle.value = session?.sessionTitle || '新对话'
  renameDialogOpen.value = true
}

async function confirmRenameDialog() {
  const title = renameSessionTitle.value.trim()
  if (!title || title.length > 50) {
    MessagePlugin.warning('会话名称长度需在 1 到 50 个字符之间')
    return
  }
  const session = renamingSession.value
  if (!session?.sessionId) return
  await updateQaSession({ sessionId: session.sessionId, sessionTitle: title })
  renameDialogOpen.value = false
  MessagePlugin.success('重命名成功')
  await loadSessions()
  if (session.sessionId === activeSessionId.value) {
    const current = sessions.value.find((item) => item.sessionId === session.sessionId)
    if (current) await openSession(current)
  }
}

function closeRenameDialog() {
  renameDialogOpen.value = false
}

function confirmWithTDesign(body: string, header = '提示') {
  return new Promise<boolean>((resolve) => {
    let settled = false
    const done = (value: boolean) => {
      if (settled) return
      settled = true
      resolve(value)
    }
    let instance: any
    instance = DialogPlugin.confirm({
      header,
      body,
      confirmBtn: '确定',
      cancelBtn: '取消',
      onConfirm: () => {
        done(true)
        instance?.hide?.()
      },
      onCancel: () => done(false),
      onClose: () => done(false),
    })
  })
}

function syncModelCapabilities() {
  const current = selectedModel.value
  if (!current) return
  if (current.supportReasoning !== '1') deepThinking.value = false
  if (current.supportVision !== '1' && images.value.length) {
    MessagePlugin.warning('当前模型不支持图片理解，建议切换支持视觉的模型')
  }
}

async function handleCourseContextChange() {
  selectedExamRecordId.value = undefined
  await loadExamRecordOptions()
  if (activeSessionId.value) {
    await updateQaSession({ sessionId: activeSessionId.value, courseId: selectedCourseId.value || null })
    await loadSessions()
  }
}

async function clearSenderContext() {
  if (selectedExamRecordId.value) {
    selectedExamRecordId.value = undefined
    return
  }
  if (externalContextPrompt.value) {
    externalContextLabel.value = ''
    externalContextPrompt.value = ''
    return
  }
  if (selectedCourseId.value) {
    selectedCourseId.value = undefined
    await handleCourseContextChange()
  }
}

async function handleImageChange(file: any) {
  const raw = file?.raw || file
  if (!raw) return
  if (raw.size && raw.size > 5 * 1024 * 1024) {
    MessagePlugin.warning('单张图片大小不能超过 5MB')
    return
  }
  if (images.value.length >= 4) {
    MessagePlugin.warning('最多上传 4 张图片')
    return
  }
  if (!images.value.some((item) => item.name === raw.name)) {
    const res = await uploadPortalQaAttachment(raw, activeSessionId.value)
    images.value.push({
      attachmentId: res.attachmentId,
      name: raw.name,
      imageUrl: res.url,
      fileName: res.fileName,
      originalName: res.originalFilename || raw.name,
      mimeType: raw.type || 'image/png',
      size: raw.size
    })
  }
}

async function removeImage(name: string) {
  const target = images.value.find((item) => item.name === name)
  if (target?.attachmentId) {
    await deletePortalQaAttachment(target.attachmentId)
  }
  images.value = images.value.filter((item) => item.name !== name)
}

async function handleComposerPaste(event: ClipboardEvent) {
  const items = Array.from(event.clipboardData?.items || [])
  const imageItem = items.find((item) => item.type.startsWith('image/'))
  if (!imageItem) return
  const file = imageItem.getAsFile()
  if (!file) return
  event.preventDefault()
  await handleImageChange({
    raw: new File([file], `粘贴图片-${Date.now()}.${(file.type.split('/')[1] || 'png').replace('jpeg', 'jpg')}`, { type: file.type }),
  })
  MessagePlugin.success('已上传剪贴板图片')
}

function courseContextLabel(courseId?: number) {
  if (!courseId) return '通用'
  return courseOptions.value.find((item) => item.value === courseId)?.label || `课程 ${courseId}`
}

function buildExamRecordContext(record: any) {
  if (!record) return ''
  const correctRateText = record.correctRate == null || record.correctRate === ''
    ? '-'
    : `${record.correctRate}${String(record.correctRate).includes('%') ? '' : '%'}`
  return [
    `考试名称：${record.paperName || `考试 ${record.recordId}`}`,
    `课程：${courseContextLabel(record.courseId)}`,
    `得分：${record.score ?? 0}`,
    `正确率：${correctRateText}`,
    `状态：${record.examStatus || '-'}`,
    `开始时间：${record.startTime || '-'}`,
    `提交时间：${record.submitTime || '-'}`,
  ].join('\n')
}

function buildContextPrompt() {
  const contexts: string[] = []
  if (selectedCourse.value) {
    contexts.push(`当前课程上下文：${selectedCourse.value.label}`)
  }
  if (selectedExamRecord.value) {
    contexts.push(`考试记录摘要：\n${buildExamRecordContext(selectedExamRecord.value)}`)
  }
  if (externalContextPrompt.value) {
    contexts.push(`补充分析上下文：\n${externalContextPrompt.value}`)
  }
  return contexts.join('\n\n')
}

function applyCoursePlanPrompt() {
  const courseText = selectedCourse.value ? `请围绕课程「${selectedCourse.value.label}」` : '请结合我当前学习情况'
  inputText.value = `${courseText}生成一份可执行的学习规划，包含阶段目标、每周安排、重点模块、复习节奏和注意事项。`
}

function applyExamAnalysisPrompt() {
  if (!selectedExamRecord.value) {
    MessagePlugin.warning('请先选择一条考试记录')
    return
  }
  inputText.value = '请结合这次考试记录做一次成绩与能力分析，指出优势、薄弱点、可能原因，并给出后续两周的改进建议。'
}

function applyWrongbookPlanPrompt() {
  if (!selectedCourseId.value) {
    MessagePlugin.warning('请先选择课程')
    return
  }
  inputText.value = `请结合当前课程的错题情况，生成一份查漏补缺复习计划，包含优先级、专题安排、每日训练建议和复盘节奏。`
}

function formatCorrectRate(value: any) {
  if (value == null || value === '') return '-'
  return `${value}${String(value).includes('%') ? '' : '%'}`
}

function applySuggestion(text: string) {
  inputText.value = text
}

async function handleTdesignFileSelect(context: any) {
  const files = Array.isArray(context) ? context : []
  for (const file of files) {
    await handleImageChange({ raw: file })
  }
}

function isImageAttachment(attachment: any) {
  return String(attachment?.mimeType || '').startsWith('image/')
}

function openAttachment(attachment: any) {
  const url = attachment?.imageUrl || attachment?.fileUrl
  if (!url) return
  window.open(url, '_blank', 'noopener,noreferrer')
}

function stopStreaming() {
  currentAbortController.value?.abort()
  currentAbortController.value = null
  flushStreamBuffers(currentStreamingAssistantId.value)
  clearStreamBufferState(currentStreamingAssistantId.value)
  streaming.value = false
  inputText.value = '请从刚才中断的位置继续回答。'
}

async function commitSessionTitle() {
  const title = editingSessionTitle.value.trim() || '新对话'
  editingSessionTitle.value = title
  if (!activeSessionId.value) return
  const current = sessions.value.find((item) => item.sessionId === activeSessionId.value)
  if (current?.sessionTitle === title) return
  await updateQaSession({ sessionId: activeSessionId.value, sessionTitle: title })
  await loadSessions()
}

async function activateTitleInput() {
  titleInputFocused.value = true
  await nextTick()
  titleInputRef.value?.focus?.()
}

async function handleTitleEnter() {
  titleInputFocused.value = false
  await commitSessionTitle()
}

async function handleTitleBlur() {
  titleInputFocused.value = false
  await commitSessionTitle()
}

async function ensureSession(question: string) {
  if (activeSessionId.value) return activeSessionId.value
  if (!userStore.user?.userId) return undefined
  const title = question.length > 18 ? `${question.slice(0, 18)}...` : question
  await addQaSession({
    userId: userStore.user.userId,
    courseId: selectedCourseId.value || null,
    sessionTitle: title,
    sourceType: selectedCourseId.value ? 'course' : 'general',
    status: '0'
  })
  await loadSessions()
  const newSession = sessions.value.find((item) => item.sessionTitle === title) || sessions.value[0]
  activeSessionId.value = newSession?.sessionId
  saveActiveSessionId(activeSessionId.value)
  return activeSessionId.value
}

async function handleSubmit() {
  if (!inputText.value.trim()) {
    MessagePlugin.warning('请输入问题')
    return
  }
  if (!userStore.user?.userId) {
    MessagePlugin.warning('当前未获取到登录用户')
    return
  }
  if (images.value.length && selectedModel.value?.supportVision !== '1') {
    MessagePlugin.warning('当前模型不支持图片理解，请切换支持视觉的模型')
    return
  }

  const question = inputText.value.trim()
  const contextPrompt = buildContextPrompt()
  const ensuredSessionId = await ensureSession(question)
  const assistantId = `assistant_${Date.now()}`
  currentStreamingAssistantId.value = assistantId
  const pendingAttachments = images.value.map((item) => ({ ...item }))
  chatMessages.value.push({ id: `user_${Date.now()}`, role: 'user', content: question, attachments: pendingAttachments })
  chatMessages.value.push({ id: assistantId, role: 'assistant', content: '', modelName: selectedModel.value?.modelName || '', reasoningContent: '' })
  inputText.value = ''
  streaming.value = true

  try {
    const payload = {
      sessionId: ensuredSessionId,
      userId: userStore.user.userId,
      courseId: selectedCourseId.value,
      question,
      contextPrompt,
      modelId: selectedModelId.value,
      deepThinking: deepThinking.value,
      stream: streamEnabled.value,
      images: images.value,
      sessionTitle: currentSessionTitle.value,
    }

    if (streamEnabled.value) {
      const controller = new AbortController()
      currentAbortController.value = controller
      const response = await fetch(`${import.meta.env.VITE_APP_BASE_API}/campus/qa/ask/stream`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${getToken() || ''}`,
        },
        body: JSON.stringify(payload),
        signal: controller.signal,
      })
      if (!response.ok || !response.body) throw new Error('流式问答请求失败')
      const reader = response.body.getReader()
      const decoder = new TextDecoder('utf-8')
      let buffer = ''
      while (true) {
        const { done, value } = await reader.read()
        if (done) {
          flushStreamBuffers(assistantId)
          if (buffer.trim()) {
            parseSseBlock(buffer.trim(), assistantId)
          }
          break
        }
        buffer += decoder.decode(value, { stream: true }).replace(/\r\n/g, '\n')
        const parts = buffer.split('\n\n')
        buffer = parts.pop() || ''
        for (const part of parts) parseSseBlock(part, assistantId)
      }
      currentAbortController.value = null
    } else {
      const response = await fetch(`${import.meta.env.VITE_APP_BASE_API}/campus/qa/ask`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${getToken() || ''}`,
        },
        body: JSON.stringify(payload),
      })
      const result = await response.json()
      if (result.code !== 200) throw new Error(result.msg || '问答请求失败')
      const target = chatMessages.value.find((item) => item.id === assistantId)
      if (target) {
        target.content = result.aiResponse?.content || ''
        target.modelName = result.aiResponse?.modelName || target.modelName
        target.reasoningContent = result.aiResponse?.reasoningContent || ''
      }
    }

    flushStreamBuffers(assistantId)
    await loadSessions()
    const currentSession = sessions.value.find((item) => item.sessionId === activeSessionId.value)
    if (currentSession) {
      await openSession(currentSession)
    }
  } catch (error: any) {
    flushStreamBuffers(assistantId)
    const target = chatMessages.value.find((item) => item.id === assistantId)
    if (target) {
      target.content = error?.name === 'AbortError' ? '已停止生成，你可以继续追问。' : normalizeQaErrorMessage(error?.message || '问答请求失败')
    }
  } finally {
    clearStreamBufferState(assistantId)
    streaming.value = false
    currentStreamingAssistantId.value = ''
    images.value = []
  }
}

function ensureStreamBufferState(assistantId: string) {
  if (!assistantId) return
  if (!streamChunkBuffers.value[assistantId]) {
    streamChunkBuffers.value[assistantId] = { content: '', reasoning: '' }
  }
}

function scheduleStreamFlush(assistantId: string) {
  if (!assistantId || streamFlushTimers.value[assistantId]) return
  streamFlushTimers.value[assistantId] = window.setTimeout(() => {
    flushStreamBuffers(assistantId)
  }, 80)
}

function flushStreamBuffers(assistantId: string) {
  if (!assistantId) return
  const timer = streamFlushTimers.value[assistantId]
  if (timer) {
    window.clearTimeout(timer)
    delete streamFlushTimers.value[assistantId]
  }
  const buffer = streamChunkBuffers.value[assistantId]
  if (!buffer) return
  const target = chatMessages.value.find((item) => item.id === assistantId)
  if (!target) return
  if (buffer.reasoning) {
    target.reasoningContent = (target.reasoningContent || '') + buffer.reasoning
    buffer.reasoning = ''
  }
  if (buffer.content) {
    target.content = (target.content || '') + buffer.content
    buffer.content = ''
  }
}

function clearStreamBufferState(assistantId: string) {
  if (!assistantId) return
  const timer = streamFlushTimers.value[assistantId]
  if (timer) {
    window.clearTimeout(timer)
    delete streamFlushTimers.value[assistantId]
  }
  if (streamChunkBuffers.value[assistantId]) {
    delete streamChunkBuffers.value[assistantId]
  }
}

function parseSseBlock(block: string, assistantId: string) {
  const eventMatch = block.match(/event:\s*(.+)/)
  const dataMatch = block.match(/data:\s*(.+)/s)
  const eventName = eventMatch?.[1]?.trim()
  const rawData = dataMatch?.[1]?.trim()
  if (!eventName || !rawData) return
  let payload: any = {}
  try {
    payload = JSON.parse(rawData)
  } catch {
    payload = { content: rawData }
  }
  const target = chatMessages.value.find((item) => item.id === assistantId)
  if (!target) return
  if (eventName === 'session' && payload.sessionId) {
    activeSessionId.value = payload.sessionId
    saveActiveSessionId(payload.sessionId)
  }
  if (eventName === 'reasoning') {
    ensureStreamBufferState(assistantId)
    streamChunkBuffers.value[assistantId].reasoning += payload.content || ''
    scheduleStreamFlush(assistantId)
  }
  if (eventName === 'chunk') {
    ensureStreamBufferState(assistantId)
    streamChunkBuffers.value[assistantId].content += payload.content || ''
    scheduleStreamFlush(assistantId)
  }
  if (eventName === 'done') {
    flushStreamBuffers(assistantId)
    if (payload.aiResponse?.modelName) {
      target.modelName = payload.aiResponse.modelName
    }
    target.content = payload.aiResponse?.content || target.content || ''
    target.reasoningContent = payload.aiResponse?.reasoningContent || target.reasoningContent || ''
  }
  if (eventName === 'error') {
    flushStreamBuffers(assistantId)
    target.content = payload.message || '生成失败'
  }
}

async function copyText(text: string) {
  await navigator.clipboard.writeText(text)
  MessagePlugin.success('已复制')
}

function continueFromAnswer(text: string) {
  inputText.value = `请基于下面这段内容继续回答，并补充更具体的说明：\n${text}\n`
}

function reAskMessage(text: string) {
  inputText.value = text
}

async function handlePocFeedback(payload: { item: any; type: 'helpful' | 'unhelpful' }) {
  const { item, type } = payload || {}
  if (!item?.messageId) {
    MessagePlugin.warning('当前消息还未完成入库，请稍后再反馈')
    return
  }
  if (!userStore.user?.userId) {
    MessagePlugin.warning('当前未获取到登录用户')
    return
  }
  const messageId = Number(item.messageId)
  if (!messageId) {
    MessagePlugin.warning('未找到可反馈的消息')
    return
  }
  if (feedbackSubmittingMessageIds.value.includes(messageId)) {
    return
  }
  if (item.feedbackType === type) {
    MessagePlugin.success(type === 'helpful' ? '这条回答已点赞' : '这条回答已点踩')
    return
  }
  feedbackSubmittingMessageIds.value = [...feedbackSubmittingMessageIds.value, messageId]
  try {
    await addQaFeedback({
      messageId,
      feedbackType: type,
      feedbackContent: '',
    })
    const target = chatMessages.value.find((chatItem) => chatItem.messageId === messageId)
    if (target) {
      target.feedbackType = type
      target.feedbackContent = ''
    }
    MessagePlugin.success(type === 'helpful' ? '已点赞并反馈到后台' : '已点踩并反馈到后台')
  } finally {
    feedbackSubmittingMessageIds.value = feedbackSubmittingMessageIds.value.filter((id) => id !== messageId)
  }
}

onMounted(async () => {
  loadPinnedSessions()
  loadActiveSessionId()
  await loadModels()
  await loadCourseOptions()
  const scoreAnalysisPreset = consumeScoreAnalysisQaPreset()
  if (scoreAnalysisPreset) {
    await createNewChat()
    selectedCourseId.value = Number(scoreAnalysisPreset.courseId || 0) || undefined
    externalContextLabel.value = scoreAnalysisPreset.contextLabel || '成绩分析上下文'
    externalContextPrompt.value = scoreAnalysisPreset.contextPrompt || ''
    inputText.value = scoreAnalysisPreset.prompt || inputText.value
  }
  await loadExamRecordOptions()
  await loadSessions()
  if (scoreAnalysisPreset) {
    return
  }
  if (activeSessionId.value) {
    const current = sessions.value.find((item) => item.sessionId === activeSessionId.value)
    if (current) {
      await openSession(current)
      return
    }
    saveActiveSessionId()
  }
})
</script>

<style scoped>
.qa-workbench {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  width: 100%;
  height: calc(100vh - 92px);
  min-height: 680px;
  max-height: calc(100vh - 92px);
  background: var(--portal-card-solid);
  border: 1px solid var(--portal-border);
  border-radius: 6px;
  overflow: hidden;
  box-shadow: var(--portal-shadow-soft)
}

.qa-sidebar {
  min-height: 0;
  background: var(--portal-surface-bg);
  border-right: 1px solid var(--portal-border);
  padding: 12px 10px;
  display: flex;
  flex-direction: column
}

.qa-sidebar__new {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 34px;
  border: 1px solid transparent;
  border-radius: 4px;
  background: var(--portal-accent);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(0, 110, 255, .16);
  margin-bottom: 10px
}

.qa-sidebar__new i {
  font-size: 15px
}

.qa-sidebar__search {
  margin-bottom: 12px
}

.qa-sidebar__search :deep(.t-input__wrap) {
  border-radius: 10px;
  background: var(--portal-card-solid);
  border-color: var(--portal-border);
  box-shadow: none;
  min-height: 36px;
}

.qa-sidebar__search :deep(.t-input__inner) {
  font-size: 12px;
  color: var(--portal-text);
}

.qa-sidebar__search :deep(.t-input__prefix) {
  color: var(--portal-text-secondary);
}

.qa-sidebar__section {
  font-size: 12px;
  color: var(--portal-text-secondary);
  margin-bottom: 8px
}

.qa-sidebar__history {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding-right: 4px;
  scrollbar-width: thin;
}

.qa-sidebar__history-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-right: 4px
}

.qa-session {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  border-radius: 4px;
  background: transparent;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all .2s ease
}

.qa-session:hover {
  border-color: var(--portal-border-strong);
  background: var(--portal-card-solid)
}

.qa-session.active {
  background: var(--portal-card-solid);
  border-color: var(--portal-border-strong)
}

.qa-session__main {
  flex: 1;
  min-width: 0
}

.qa-session__title-row {
  display: flex;
  align-items: center;
  gap: 6px
}

.qa-session__title {
  font-size: 12px;
  font-weight: 600;
  color: var(--portal-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis
}

.qa-session__pin {
  color: var(--portal-warning);
  font-size: 12px
}

.qa-session__meta {
  margin-top: 4px;
  font-size: 11px;
  color: var(--portal-text-secondary)
}

.qa-session__more {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  color: var(--portal-text-secondary);
}

.qa-main {
  min-height: 0;
  display: grid;
  grid-template-rows: minmax(0, 1fr) auto;
  background: var(--portal-card-solid)
}

.qa-main__floating-header {
  position: absolute;
  top: 8px;
  left: 14px;
  right: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
  z-index: 5
}

.qa-main__title-anchor {
  width: min(420px, 42vw);
  pointer-events: auto;
  display: flex;
  justify-content: center;
}

.qa-main__title-input {
  width: 100%;
  transition: all .2s ease
}

.qa-main__title-input :deep(.t-input__wrap) {
  border-radius: 999px;
  box-shadow: none;
  border-color: var(--portal-border);
  padding: 0 12px;
  min-height: 34px;
  background: var(--portal-card-solid);
}

.qa-main__title-input :deep(.t-input__inner) {
  text-align: center;
  font-size: 14px;
  font-weight: 700;
  color: var(--portal-text)
}

.qa-main__title-input :deep(.t-input__inner::placeholder) {
  font-weight: 500;
  color: var(--portal-text-secondary)
}

.qa-main__title-display {
  width: 100%;
  min-height: 34px;
  padding: 0 14px;
  border: 1px solid transparent;
  border-radius: 999px;
  background: transparent;
  color: var(--portal-text);
  font-size: 14px;
  font-weight: 700;
  line-height: 34px;
  text-align: center;
  cursor: pointer;
  transition: all .2s ease;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis
}

.qa-main__title-display:hover {
  background: var(--portal-card-solid);
  border-color: var(--portal-border)
}

.qa-main__title-display.is-placeholder {
  color: var(--portal-text-secondary);
  font-weight: 500
}

.qa-main__actions {
  position: absolute;
  right: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  pointer-events: auto
}

.qa-main__pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 10px;
  border-radius: 999px;
  background: var(--portal-surface-bg);
  border: 1px solid var(--portal-border);
  color: var(--portal-text-secondary);
  font-size: 12px
}

.qa-main__danger {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border: none;
  border-radius: 4px;
  background: rgba(220, 91, 73, .12);
  color: var(--portal-danger);
  cursor: pointer
}

.qa-main__messages {
  position: relative;
  min-height: 0;
  background: linear-gradient(180deg, var(--portal-card-solid) 0%, var(--portal-surface-bg) 100%)
}

.qa-composer {
  position: sticky;
  bottom: 0;
  z-index: 3;
  padding: 10px 14px 12px;
  border-top: 1px solid var(--portal-border);
  background: var(--portal-card-solid);
  backdrop-filter: blur(8px)
}

:global(.t-image-viewer-preview-image),
:global(.t-image-viewer__dialog.t-dialog__ctx--fixed),
:global(.t-image-viewer-preview-image .t-image-viewer__modal-mask),
:global(.t-image-viewer-preview-image .t-image-viewer__modal-pic),
:global(.t-image-viewer-preview-image .t-image-viewer__modal-icon.t-image-viewer__modal-close-bt),
:global(.t-image-viewer-preview-image .t-image-viewer__modal-icon.t-image-viewer__modal-prev-bt),
:global(.t-image-viewer-preview-image .t-image-viewer__modal-icon.t-image-viewer__modal-next-bt),
:global(.t-image-viewer__utils) {
  z-index: 5600 !important;
}

@media (max-width: 1100px) {
  .qa-workbench {
    grid-template-columns: 1fr;
    height: calc(100vh - 96px);
    max-height: calc(100vh - 96px);
    min-height: unset
  }

  .qa-sidebar {
    display: none
  }

  .qa-main__floating-header {
    top: 8px;
    left: 10px;
    right: 10px;
    padding: 0
  }

  .qa-main__actions {
    position: static;
    margin-left: 10px
  }

  .qa-main__title-input {
    width: 100%
  }

  .qa-main__title-input :deep(.t-input__inner) {
    font-size: 14px
  }

  .qa-main__title-anchor {
    width: 100%
  }

}

@media (max-width: 768px) {
  .qa-workbench {
    height: calc(100vh - 80px);
    max-height: calc(100vh - 80px);
    border-radius: 0;
  }

  .qa-composer {
    padding: 8px 10px 10px;
  }

  .qa-main__floating-header {
    top: 6px;
    left: 8px;
    right: 8px;
  }

  .qa-main__pill {
    padding: 4px 8px;
    font-size: 11px;
  }

  .qa-main__danger {
    padding: 4px 8px;
    font-size: 12px;
  }
}

@media (max-width: 640px) {
  .qa-workbench {
    height: calc(100vh - 64px);
    max-height: calc(100vh - 64px);
    border: none;
    border-radius: 0;
  }

  .qa-main__title-input :deep(.t-input__inner) {
    font-size: 13px;
  }

  .qa-main__actions {
    gap: 4px;
  }
}
</style>
