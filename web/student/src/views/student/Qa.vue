<template>
  <div class="qa-workbench">
    <aside class="qa-sidebar">
      <div class="qa-sidebar__brand">
        <div class="qa-sidebar__brand-top">
          <div class="qa-sidebar__logo"><i class="ri-sparkling-2-line"></i></div>
          <div class="qa-sidebar__badge">校园专属</div>
        </div>
        <div class="qa-sidebar__brand-text">
          <div class="qa-sidebar__title">校园智学助手</div>
          <div class="qa-sidebar__subtitle">课程问答、复盘答疑与图像理解</div>
        </div>
      </div>

      <button type="button" class="qa-sidebar__new" @click="createNewChat">
        <i class="ri-add-line"></i>
        <span>新对话</span>
      </button>

      <div class="qa-sidebar__search">
        <i class="ri-search-line"></i>
        <input v-model="sessionKeyword" placeholder="搜索历史对话" />
      </div>

      <div class="qa-sidebar__section">历史对话</div>
      <el-scrollbar class="qa-sidebar__history">
        <div class="qa-sidebar__history-list">
          <div
            v-for="item in filteredSessions"
            :key="item.sessionId"
            class="qa-session"
            :class="{ active: item.sessionId === activeSessionId }"
            @click="openSession(item)"
          >
            <div class="qa-session__main">
              <div class="qa-session__title-row">
                <div class="qa-session__title">{{ item.sessionTitle || `会话 ${item.sessionId}` }}</div>
                <i v-if="isPinned(item.sessionId)" class="ri-pushpin-2-fill qa-session__pin"></i>
              </div>
              <div class="qa-session__meta">{{ item.createTime || `ID: ${item.sessionId}` }}</div>
            </div>
            <el-dropdown trigger="click" @command="(command) => handleSessionCommand(command, item)">
              <button type="button" class="qa-session__more" @click.stop>
                <i class="ri-more-2-fill"></i>
              </button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="pin">
                    <i class="ri-pushpin-line"></i>
                    {{ isPinned(item.sessionId) ? '取消置顶' : '置顶会话' }}
                  </el-dropdown-item>
                  <el-dropdown-item command="rename"><i class="ri-edit-line"></i> 重命名</el-dropdown-item>
                  <el-dropdown-item command="delete"><i class="ri-delete-bin-6-line"></i> 删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <el-empty v-if="!filteredSessions.length" :image-size="64" description="暂无历史对话" />
        </div>
      </el-scrollbar>
    </aside>

    <section class="qa-main">
      <main class="qa-main__messages">
        <div class="qa-main__floating-header">
          <el-input
            v-model="editingSessionTitle"
            class="qa-main__title-input"
            :class="{ 'is-idle': !titleInputFocused }"
            maxlength="50"
            placeholder="给这次对话起个标题"
            @focus="titleInputFocused = true"
            @blur="handleTitleBlur"
            @keyup.enter="commitSessionTitle"
          />
          <div class="qa-main__actions">
            <span class="qa-main__pill"><i class="ri-robot-2-line"></i>{{ selectedModel?.modelName || '未选择模型' }}</span>
            <button v-if="streaming" type="button" class="qa-main__danger" @click="stopStreaming">
              <i class="ri-stop-circle-line"></i>
              <span>停止生成</span>
            </button>
          </div>
        </div>
        <el-scrollbar ref="messageScrollbarRef" class="qa-main__scroll">
          <div ref="messageContainerRef" class="qa-main__content" :class="{ 'is-empty': !chatMessages.length }">
            <div v-if="!chatMessages.length" class="qa-empty">
              <div class="qa-empty__title">有什么我能帮你的吗？</div>
              <div class="qa-empty__subtitle">围绕课程学习、作业分析、复盘答疑与图像理解，给你连续、清晰、可追问的校园学习协作体验。</div>
              <div class="qa-empty__suggestions">
                <button v-for="item in suggestionList" :key="item" type="button" class="qa-suggestion" @click="applySuggestion(item)">{{ item }}</button>
              </div>
            </div>

            <div
              v-for="(item, index) in chatMessages"
              :key="item.id"
              :ref="(el) => setMessageRef(item.id, el)"
              class="qa-message"
              :class="item.role"
            >
              <div class="qa-message__avatar">
                <i :class="item.role === 'user' ? 'ri-user-3-line' : 'ri-robot-2-line'"></i>
              </div>
              <div class="qa-message__bubble">
                <div class="qa-message__meta">
                  <strong>{{ item.role === 'user' ? '我' : 'AI 助手' }}</strong>
                  <span v-if="item.modelName">{{ item.modelName }}</span>
                </div>

                <div v-if="item.role === 'user'" class="qa-message__index-label">问题 {{ getUserQuestionOrder(index) }}</div>

                <div v-if="item.attachments?.length" class="qa-message__attachments">
                  <button
                    v-for="attachment in item.attachments"
                    :key="attachment.attachmentId || attachment.fileName || attachment.name"
                    type="button"
                    class="qa-message__attachment"
                    @click="openAttachment(attachment)"
                  >
                    <img
                      v-if="isImageAttachment(attachment)"
                      :src="attachment.imageUrl || attachment.fileUrl"
                      :alt="attachment.originalName || attachment.name || '附件图片'"
                    />
                    <i v-else class="ri-attachment-2"></i>
                    <span>{{ attachment.originalName || attachment.name || '附件' }}</span>
                  </button>
                </div>

                <details
                  v-if="item.role === 'assistant' && item.reasoningContent"
                  class="qa-reasoning"
                  :open="streaming && item.id === currentStreamingAssistantId"
                >
                  <summary class="qa-reasoning__summary">
                    <i class="ri-brain-line"></i>
                    <span>思考过程</span>
                  </summary>
                  <div
                    class="qa-reasoning__content markdown-body"
                    v-html="renderMarkdown(item.reasoningContent)"
                  ></div>
                </details>

                <div
                  v-if="item.role === 'assistant'"
                  class="qa-message__content markdown-body"
                  v-html="renderMarkdown(item.content || (streaming ? '思考中...' : ''))"
                ></div>
                <div v-else class="qa-message__content qa-message__content--plain">{{ item.content }}</div>

                <div class="qa-message__ops">
                  <button v-if="item.role === 'assistant' && item.content" type="button" @click="copyText(item.content)">
                    <i class="ri-file-copy-line"></i><span>复制内容</span>
                  </button>
                  <button
                    v-if="item.role === 'assistant' && item.content && item.messageId"
                    type="button"
                    :class="{ 'is-active': item.feedbackType === 'helpful' }"
                    @click="openFeedbackDialog(item, 'helpful')"
                  >
                    <i class="ri-thumb-up-line"></i><span>有帮助</span>
                  </button>
                  <button
                    v-if="item.role === 'assistant' && item.content && item.messageId"
                    type="button"
                    :class="{ 'is-active': item.feedbackType === 'unhelpful', 'is-danger': item.feedbackType === 'unhelpful' }"
                    @click="openFeedbackDialog(item, 'unhelpful')"
                  >
                    <i class="ri-thumb-down-line"></i><span>需改进</span>
                  </button>
                  <button v-if="item.role === 'assistant' && item.content" type="button" @click="continueFromAnswer(item.content)">
                    <i class="ri-chat-forward-line"></i><span>引用后继续问</span>
                  </button>
                  <button v-if="item.role === 'user'" type="button" @click="reAskMessage(item.content)">
                    <i class="ri-restart-line"></i><span>重新提问</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </el-scrollbar>
        <div v-if="userQuestionAnchors.length" class="qa-main__index">
          <button
            v-for="anchor in userQuestionAnchors"
            :key="anchor.id"
            type="button"
            class="qa-main__index-item"
            :class="{ 'is-active': anchor.id === activeAnchorId }"
            @click="scrollToMessage(anchor.id)"
          >
            <span class="qa-main__index-number">{{ anchor.order }}</span>
            <span class="qa-main__index-preview">{{ anchor.title }}</span>
            <span class="qa-main__index-line"></span>
          </button>
        </div>
      </main>

      <footer class="qa-composer">
        <div v-if="images.length" class="qa-composer__attachments">
          <div v-for="item in images" :key="item.name" class="qa-attachment">
            <i class="ri-image-line"></i>
            <span>{{ item.name }}</span>
            <button type="button" @click="removeImage(item.name)"><i class="ri-close-line"></i></button>
          </div>
        </div>

        <el-input
          v-model="inputText"
          type="textarea"
          :rows="4"
          resize="none"
          placeholder="输入你的问题，支持课程问答、知识点解释、解题思路分析"
          @keydown.enter.exact.prevent="handleSubmit"
          @paste="handleComposerPaste"
        />

        <div class="qa-composer__toolbar">
          <div class="qa-composer__left">
            <el-upload action="#" :auto-upload="false" :show-file-list="false" :on-change="handleImageChange" accept="image/*">
              <button type="button" class="qa-tool-btn">
                <i class="ri-image-add-line"></i>
                <span>上传图片</span>
              </button>
            </el-upload>

            <div class="qa-tool-select qa-tool-select--model">
              <div class="qa-tool-select__icon"><i class="ri-robot-2-line"></i></div>
              <div class="qa-tool-select__body">
                <el-select v-model="selectedModelId" placeholder="选择模型" filterable @change="handleModelChange">
                  <el-option v-for="item in modelOptions" :key="item.modelId" :label="`${item.modelName}（${item.provider}）`" :value="item.modelId" />
                </el-select>
              </div>
            </div>

            <label class="qa-tool-switch" :class="{ disabled: selectedModel?.supportStream !== '1' }">
              <i class="ri-signal-wifi-line"></i>
              <span>实时输出</span>
              <el-switch v-model="streamEnabled" :disabled="selectedModel?.supportStream !== '1'" />
            </label>

            <label class="qa-tool-switch" :class="{ disabled: selectedModel?.supportReasoning !== '1' }">
              <i class="ri-brain-line"></i>
              <span>深度思考</span>
              <el-switch v-model="deepThinking" :disabled="selectedModel?.supportReasoning !== '1'" />
            </label>
          </div>

          <div class="qa-composer__right">
            <span v-if="streaming" class="qa-composer__status">正在实时生成...</span>
            <button type="button" class="qa-send-btn" :disabled="streaming" @click="handleSubmit">
              <i class="ri-send-plane-2-fill"></i>
              <span>{{ streaming ? '生成中' : '发送' }}</span>
            </button>
          </div>
        </div>
      </footer>
    </section>

    <el-dialog v-model="feedbackDialogOpen" title="问答反馈" width="560px" class="qa-feedback-dialog" align-center>
      <el-form label-width="78px" class="qa-feedback-form">
        <el-form-item label="反馈类型">
          <el-radio-group v-model="feedbackForm.feedbackType" class="qa-feedback-form__type">
            <el-radio-button label="helpful">有帮助</el-radio-button>
            <el-radio-button label="unhelpful">需改进</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input
            v-model="feedbackForm.feedbackContent"
            type="textarea"
            rows="5"
            maxlength="200"
            show-word-limit
            placeholder="可以补充说明哪里回答得好，或哪里还需要改进"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="feedbackDialogOpen = false">取消</el-button>
        <el-button type="primary" @click="submitQaFeedback">提交反馈</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import 'highlight.js/styles/github-dark.css'
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import type { ElMessageBoxOptions } from 'element-plus'
import { ElMessage, ElMessageBox } from '@/utils/feedback'
import { getToken } from '@/utils/auth'
import { normalizeQaErrorMessage, parseAssistantReferenceSource, parseQaAttachments } from '@/utils/qaMessage'
import { renderQaMarkdown } from '@/utils/qaMarkdown'
import usePortalUserStore from '@/store/user'
import { addQaFeedback, addQaSession, deletePortalQaAttachment, deleteQaSession, listQaMessage, listQaModelOptions, listQaSession, updateQaSession, uploadPortalQaAttachment } from '@/api/portal'

const SESSION_PIN_KEY = 'qa-session-pins'
const SESSION_ACTIVE_KEY = 'qa-active-session-id'

const userStore = usePortalUserStore()

const inputText = ref('')
const streaming = ref(false)
const streamEnabled = ref(true)
const deepThinking = ref(false)
const selectedModelId = ref<number | undefined>()
const modelOptions = ref<any[]>([])
const images = ref<Array<{ attachmentId?: number; name: string; imageUrl: string; fileName?: string; originalName?: string; mimeType: string; size?: number }>>([])
const sessions = ref<any[]>([])
const sessionKeyword = ref('')
const activeSessionId = ref<number | undefined>()
const pinnedSessionIds = ref<number[]>([])
const chatMessages = ref<Array<{ id: string; role: 'user' | 'assistant'; content: string; modelName?: string; reasoningContent?: string; attachments?: any[]; messageId?: number; feedbackType?: string; feedbackContent?: string }>>([])
const messageContainerRef = ref<HTMLElement | null>(null)
const messageScrollbarRef = ref<any>(null)
const currentAbortController = ref<AbortController | null>(null)
const currentStreamingAssistantId = ref('')
const titleInputFocused = ref(false)
const renderMarkdown = renderQaMarkdown
const editingSessionTitle = ref('')
const messageRefs = ref<Record<string, HTMLElement | null>>({})
const activeAnchorId = ref('')
const feedbackDialogOpen = ref(false)
const feedbackTargetMessageId = ref<number | undefined>()
const feedbackForm = ref<{ feedbackType: 'helpful' | 'unhelpful'; feedbackContent: string }>({ feedbackType: 'helpful', feedbackContent: '' })

const suggestionList = [
  '帮我总结这一章的知识点',
  '这道题为什么这么做？',
  '请帮我生成复习计划',
  '请结合图片内容给我解释',
  '下一次考试我该怎么提高成绩？',
]

const selectedModel = computed(() => modelOptions.value.find((item) => item.modelId === selectedModelId.value))
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
const userQuestionAnchors = computed(() => chatMessages.value
  .filter((item) => item.role === 'user')
  .map((item, index) => ({ id: item.id, order: index + 1, title: item.content.slice(0, 18) })))

watch(currentSessionTitle, (value) => {
  editingSessionTitle.value = value
}, { immediate: true })

watch(chatMessages, async () => {
  await nextTick()
  if (messageContainerRef.value && messageScrollbarRef.value) {
    messageScrollbarRef.value.setScrollTop(messageContainerRef.value.scrollHeight)
  }
}, { deep: true })

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

async function loadSessions() {
  if (!userStore.user?.userId) return
  const res = await listQaSession({ pageNum: 1, pageSize: 100, userId: userStore.user.userId })
  sessions.value = res.rows || []
}

async function openSession(item: any) {
  activeSessionId.value = item.sessionId
  saveActiveSessionId(item.sessionId)
  const res = await listQaMessage({ pageNum: 1, pageSize: 200, sessionId: item.sessionId })
  chatMessages.value = (res.rows || []).map((message: any) => {
    const role = message.roleType === 'assistant' ? 'assistant' : 'user'
    const meta = role === 'assistant'
      ? parseAssistantReferenceSource(message.referenceSource)
      : { modelName: '', reasoningContent: '' }
    return {
      ...meta,
      id: `${message.roleType}_${message.messageId}`,
      messageId: message.messageId,
      role,
      content: message.content,
      attachments: parseQaAttachments(message.attachmentJson),
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
}

async function handleSessionCommand(command: string, item: any) {
  if (command === 'pin') {
    togglePin(item.sessionId)
    return
  }
  if (command === 'rename') {
    const title = await promptSessionTitle(item.sessionTitle || '新对话')
    if (!title) return
    await updateQaSession({ sessionId: item.sessionId, sessionTitle: title })
    ElMessage.success('重命名成功')
    await loadSessions()
    if (item.sessionId === activeSessionId.value) {
      const current = sessions.value.find((session) => session.sessionId === item.sessionId)
      if (current) await openSession(current)
    }
  }
  if (command === 'delete') {
    await ElMessageBox.confirm('确认删除该历史会话吗？删除后不可恢复。', '提示', { type: 'warning' } as ElMessageBoxOptions)
    await deleteQaSession(item.sessionId)
    ElMessage.success('删除成功')
    pinnedSessionIds.value = pinnedSessionIds.value.filter((id) => id !== item.sessionId)
    savePinnedSessions()
    if (item.sessionId === activeSessionId.value) await createNewChat()
    await loadSessions()
  }
}

function promptSessionTitle(defaultValue: string): Promise<string | null> {
  return ElMessageBox.prompt('请输入新的会话名称', '重命名会话', {
    inputValue: defaultValue,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^.{1,50}$/,
    inputErrorMessage: '会话名称长度需在 1 到 50 个字符之间',
  }).then(({ value }) => value).catch(() => null)
}

function syncModelCapabilities() {
  const current = selectedModel.value
  if (!current) return
  if (current.supportStream !== '1') streamEnabled.value = false
  if (current.supportReasoning !== '1') deepThinking.value = false
  if (current.supportVision !== '1' && images.value.length) {
    ElMessage.warning('当前模型不支持图片理解，建议切换支持视觉的模型')
  }
}

function handleModelChange() {
  syncModelCapabilities()
}

async function handleImageChange(file: any) {
  const raw = file?.raw || file
  if (!raw) return
  if (raw.size && raw.size > 5 * 1024 * 1024) {
    ElMessage.warning('单张图片大小不能超过 5MB')
    return
  }
  if (images.value.length >= 4) {
    ElMessage.warning('最多上传 4 张图片')
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
  ElMessage.success('已上传剪贴板图片')
}

function applySuggestion(text: string) {
  inputText.value = text
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
  streaming.value = false
  inputText.value = '请从刚才中断的位置继续回答。'
}

function setMessageRef(id: string, el: Element | null) {
  messageRefs.value[id] = el as HTMLElement | null
}

function getUserQuestionOrder(index: number) {
  return chatMessages.value.slice(0, index + 1).filter((item) => item.role === 'user').length
}

function scrollToMessage(messageId: string) {
  const target = messageRefs.value[messageId]
  const container = messageContainerRef.value
  if (!target || !container || !messageScrollbarRef.value) return
  const top = target.offsetTop - 16
  messageScrollbarRef.value.setScrollTop(top)
  activeAnchorId.value = messageId
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

async function handleTitleBlur() {
  titleInputFocused.value = false
  await commitSessionTitle()
}

async function ensureSession(question: string) {
  if (activeSessionId.value) return activeSessionId.value
  if (!userStore.user?.userId) return undefined
  const title = question.length > 18 ? `${question.slice(0, 18)}...` : question
  await addQaSession({ userId: userStore.user.userId, courseId: null, sessionTitle: title, sourceType: 'general', status: '0' })
  await loadSessions()
  const newSession = sessions.value.find((item) => item.sessionTitle === title) || sessions.value[0]
  activeSessionId.value = newSession?.sessionId
  saveActiveSessionId(activeSessionId.value)
  return activeSessionId.value
}

async function handleSubmit() {
  if (!inputText.value.trim()) {
    ElMessage.warning('请输入问题')
    return
  }
  if (!userStore.user?.userId) {
    ElMessage.warning('当前未获取到登录用户')
    return
  }
  if (images.value.length && selectedModel.value?.supportVision !== '1') {
    ElMessage.warning('当前模型不支持图片理解，请切换支持视觉的模型')
    return
  }

  const question = inputText.value.trim()
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
      question,
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
        if (done) break
        buffer += decoder.decode(value, { stream: true })
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

    await loadSessions()
    const currentSession = sessions.value.find((item) => item.sessionId === activeSessionId.value)
    if (currentSession) {
      await openSession(currentSession)
    }
  } catch (error: any) {
    const target = chatMessages.value.find((item) => item.id === assistantId)
    if (target) {
      target.content = error?.name === 'AbortError' ? '已停止生成，你可以继续追问。' : normalizeQaErrorMessage(error?.message || '问答请求失败')
    }
  } finally {
    streaming.value = false
    currentStreamingAssistantId.value = ''
    images.value = []
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
    target.reasoningContent = (target.reasoningContent || '') + (payload.content || '')
  }
  if (eventName === 'chunk') target.content += payload.content || ''
  if (eventName === 'done' && payload.aiResponse?.modelName) {
    target.modelName = payload.aiResponse.modelName
    target.reasoningContent = payload.aiResponse?.reasoningContent || target.reasoningContent || ''
  }
  if (eventName === 'error') target.content = payload.message || '生成失败'
}

async function copyText(text: string) {
  await navigator.clipboard.writeText(text)
  ElMessage.success('已复制')
}

function continueFromAnswer(text: string) {
  inputText.value = `请基于下面这段内容继续回答，并补充更具体的说明：\n${text}\n`
}

function reAskMessage(text: string) {
  inputText.value = text
}

function openFeedbackDialog(item: any, type: 'helpful' | 'unhelpful') {
  if (!item.messageId) {
    ElMessage.warning('当前消息还未完成入库，请稍后再反馈')
    return
  }
  feedbackTargetMessageId.value = item.messageId
  feedbackForm.value = {
    feedbackType: type,
    feedbackContent: item.feedbackContent || '',
  }
  feedbackDialogOpen.value = true
}

async function submitQaFeedback() {
  if (!feedbackTargetMessageId.value) {
    ElMessage.warning('未找到可反馈的消息')
    return
  }
  if (!userStore.user?.userId) {
    ElMessage.warning('当前未获取到登录用户')
    return
  }
  await addQaFeedback({
    messageId: feedbackTargetMessageId.value,
    userId: userStore.user.userId,
    feedbackType: feedbackForm.value.feedbackType,
    feedbackContent: feedbackForm.value.feedbackContent,
  })
  const target = chatMessages.value.find((item) => item.messageId === feedbackTargetMessageId.value)
  if (target) {
    target.feedbackType = feedbackForm.value.feedbackType
    target.feedbackContent = feedbackForm.value.feedbackContent
  }
  feedbackDialogOpen.value = false
  ElMessage.success('反馈已提交')
}

onMounted(async () => {
  loadPinnedSessions()
  loadActiveSessionId()
  await loadModels()
  await loadSessions()
  if (activeSessionId.value) {
    const current = sessions.value.find((item) => item.sessionId === activeSessionId.value)
    if (current) {
      await openSession(current)
      return
    }
    saveActiveSessionId()
  }
})

watch(
  () => messageScrollbarRef.value,
  async (scrollbar) => {
    await nextTick()
    const wrap = scrollbar?.wrapRef
    if (!wrap) return
    wrap.addEventListener('scroll', handleScrollSpy, { passive: true })
  },
  { flush: 'post' }
)

function handleScrollSpy() {
  const container = messageContainerRef.value
  const wrap = messageScrollbarRef.value?.wrapRef
  if (!container || !wrap) return
  const threshold = wrap.scrollTop + 120
  let currentId = userQuestionAnchors.value[0]?.id || ''
  for (const anchor of userQuestionAnchors.value) {
    const el = messageRefs.value[anchor.id]
    if (el && el.offsetTop <= threshold) {
      currentId = anchor.id
    }
  }
  activeAnchorId.value = currentId
}
</script>

<style scoped>
.qa-workbench{display:grid;grid-template-columns:220px minmax(0,1fr);width:100%;height:calc(100vh - 92px);min-height:680px;max-height:calc(100vh - 92px);background:var(--portal-card-solid);border:1px solid var(--portal-border);border-radius:6px;overflow:hidden;box-shadow:var(--portal-shadow-soft)}
.qa-sidebar{min-height:0;background:var(--portal-surface-bg);border-right:1px solid var(--portal-border);padding:12px 10px;display:flex;flex-direction:column}.qa-sidebar__brand{margin-bottom:12px;padding:12px;border-radius:6px;background:var(--portal-card-solid);border:1px solid var(--portal-border)}.qa-sidebar__brand-top{display:flex;align-items:center;justify-content:space-between;gap:10px}.qa-sidebar__brand-text{margin-top:8px}.qa-sidebar__logo{width:28px;height:28px;display:flex;align-items:center;justify-content:center;color:#315fca;font-size:20px;line-height:1}.qa-sidebar__badge{display:inline-flex;padding:2px 8px;border-radius:999px;background:var(--portal-brand-light);color:var(--portal-brand);font-size:11px;font-weight:700}.qa-sidebar__title{font-size:14px;font-weight:800;color:var(--portal-text);line-height:1.3}.qa-sidebar__subtitle{font-size:12px;color:var(--portal-text-secondary);margin-top:4px;line-height:1.6}
.qa-sidebar__new{display:flex;align-items:center;justify-content:center;gap:6px;height:34px;border:1px solid transparent;border-radius:4px;background:var(--portal-accent);color:#fff;font-size:13px;font-weight:600;cursor:pointer;box-shadow:0 4px 10px rgba(0,110,255,.16);margin-bottom:10px}.qa-sidebar__new i{font-size:15px}
.qa-sidebar__search{display:flex;align-items:center;gap:8px;height:34px;padding:0 10px;border-radius:4px;background:var(--portal-card-solid);border:1px solid var(--portal-border);margin-bottom:12px}.qa-sidebar__search i{color:var(--portal-text-secondary)}.qa-sidebar__search input{border:none;outline:none;background:transparent;flex:1;font-size:12px;color:var(--portal-text)}.qa-sidebar__section{font-size:12px;color:var(--portal-text-secondary);margin-bottom:8px}.qa-sidebar__history{flex:1;min-height:0}.qa-sidebar__history-list{display:flex;flex-direction:column;gap:4px;padding-right:4px}
.qa-session{display:flex;align-items:center;gap:8px;padding:8px;border-radius:4px;background:transparent;border:1px solid transparent;cursor:pointer;transition:all .2s ease}.qa-session:hover{border-color:var(--portal-border-strong);background:var(--portal-card-solid)}.qa-session.active{background:var(--portal-card-solid);border-color:var(--portal-border-strong)}.qa-session__main{flex:1;min-width:0}.qa-session__title-row{display:flex;align-items:center;gap:6px}.qa-session__title{font-size:12px;font-weight:600;color:var(--portal-text);white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.qa-session__pin{color:var(--portal-warning);font-size:12px}.qa-session__meta{margin-top:4px;font-size:11px;color:var(--portal-text-secondary)}.qa-session__more{width:24px;height:24px;border:none;border-radius:4px;background:var(--portal-surface-bg);color:var(--portal-text-secondary);cursor:pointer;display:flex;align-items:center;justify-content:center}
.qa-main{min-height:0;display:grid;grid-template-rows:minmax(0,1fr) auto;background:var(--portal-card-solid)}.qa-main__floating-header{position:absolute;top:8px;left:14px;right:14px;display:flex;align-items:center;justify-content:center;pointer-events:none;z-index:5}.qa-main__title-input{width:min(420px,42vw);pointer-events:auto;transition:all .2s ease}.qa-main__title-input :deep(.el-input__wrapper){border-radius:4px;box-shadow:none;border:1px solid var(--portal-border);padding:0 10px;height:32px;background:var(--portal-card-solid)}.qa-main__title-input :deep(.el-input__inner){text-align:center;font-size:14px;font-weight:700;color:var(--portal-text)}.qa-main__title-input :deep(.el-input__inner::placeholder){font-weight:500;color:var(--portal-text-secondary)}.qa-main__title-input.is-idle :deep(.el-input__wrapper){background:transparent;border-color:transparent}.qa-main__title-input.is-idle:hover :deep(.el-input__wrapper){background:var(--portal-card-solid);border-color:var(--portal-border)}.qa-main__title-input.is-idle :deep(.el-input__inner){cursor:text}.qa-main__actions{position:absolute;right:0;display:flex;align-items:center;gap:8px;pointer-events:auto}.qa-main__pill{display:inline-flex;align-items:center;gap:6px;padding:5px 10px;border-radius:999px;background:var(--portal-surface-bg);border:1px solid var(--portal-border);color:var(--portal-text-secondary);font-size:12px}.qa-main__danger{display:inline-flex;align-items:center;gap:6px;padding:6px 10px;border:none;border-radius:4px;background:rgba(220,91,73,.12);color:var(--portal-danger);cursor:pointer}
.qa-main__messages{position:relative;min-height:0;background:linear-gradient(180deg,var(--portal-card-solid) 0%,var(--portal-surface-bg) 100%)}.qa-main__scroll{height:100%}.qa-main__content{padding:52px 16px 14px}.qa-main__content.is-empty{min-height:100%;display:flex;align-items:center;justify-content:center;padding:72px 16px 14px}.qa-empty{display:flex;flex-direction:column;align-items:center;justify-content:center;text-align:center;max-width:980px}.qa-empty__title{font-size:24px;font-weight:800;color:var(--portal-text);text-align:center}.qa-empty__subtitle{margin-top:10px;max-width:760px;font-size:13px;line-height:1.85;color:var(--portal-text-secondary);text-align:center}.qa-empty__suggestions{margin-top:16px;display:flex;gap:8px;flex-wrap:wrap;justify-content:center;max-width:980px}.qa-suggestion{border:1px solid var(--portal-border);background:var(--portal-card-solid);padding:8px 12px;border-radius:999px;color:var(--portal-text);cursor:pointer;transition:.2s}.qa-suggestion:hover{background:var(--portal-brand-light);border-color:var(--portal-border-strong);color:var(--portal-brand)}
.qa-message{display:flex;gap:10px;width:100%;max-width:100%;margin:0 0 12px}.qa-message.assistant{justify-content:flex-start;padding-right:4%}.qa-message.user{justify-content:flex-start;flex-direction:row-reverse;padding-left:4%}.qa-message__avatar{width:30px;height:30px;border-radius:4px;background:var(--portal-surface-bg);color:var(--portal-brand);display:flex;align-items:center;justify-content:center;font-size:15px;flex-shrink:0}.qa-message.user .qa-message__avatar{background:var(--portal-brand-light);color:var(--portal-accent)}.qa-message__bubble{width:fit-content;max-width:min(1120px,calc(100% - 40px));padding:12px 14px;border-radius:6px;background:var(--portal-card-solid);border:1px solid var(--portal-border);box-shadow:none}.qa-message.user .qa-message__bubble{background:var(--portal-brand-light);border-color:var(--portal-border-strong)}.qa-message__meta{display:flex;align-items:center;justify-content:space-between;gap:10px;margin-bottom:8px;color:var(--portal-text-secondary);font-size:11px}.qa-message__meta strong{color:var(--portal-text);font-size:12px}.qa-message__index-label{margin-bottom:8px;color:var(--portal-brand);font-size:11px;font-weight:700}.qa-message__attachments{display:flex;flex-wrap:wrap;gap:8px;margin-bottom:10px}.qa-message__attachment{display:inline-flex;align-items:center;gap:8px;max-width:220px;padding:6px 8px;border:1px solid var(--portal-border);border-radius:6px;background:rgba(255,255,255,.72);color:var(--portal-text);cursor:pointer}.qa-message__attachment img{width:36px;height:36px;border-radius:4px;object-fit:cover;display:block;flex-shrink:0}.qa-message__attachment i{font-size:18px;color:var(--portal-brand);flex-shrink:0}.qa-message__attachment span{font-size:12px;line-height:1.4;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.qa-reasoning{margin:0 0 10px;border:1px solid var(--portal-border);border-radius:4px;background:var(--portal-surface-bg);overflow:hidden}.qa-reasoning__summary{display:flex;align-items:center;gap:8px;padding:8px 10px;cursor:pointer;color:var(--portal-brand);font-size:12px;font-weight:700;list-style:none}.qa-reasoning__summary::-webkit-details-marker{display:none}.qa-reasoning__content{padding:0 10px 10px;color:var(--portal-text-secondary)}.qa-message__content{line-height:1.75;color:var(--portal-text)}.qa-message__content--plain{white-space:pre-wrap;word-break:break-word}.qa-message__ops{margin-top:10px;display:flex;gap:8px;flex-wrap:wrap}.qa-message__ops button{display:inline-flex;align-items:center;justify-content:center;gap:6px;height:28px;padding:0 10px;border:1px solid var(--portal-border);border-radius:999px;background:var(--portal-card-solid);color:var(--portal-text-secondary);cursor:pointer;font-size:12px;font-weight:600;line-height:1;transition:all .2s ease}.qa-message__ops button i{display:inline-flex;align-items:center;justify-content:center;font-size:13px;line-height:1;flex-shrink:0}.qa-message__ops button span{display:inline-flex;align-items:center;line-height:1}.qa-message__ops button:hover{border-color:var(--portal-border-strong);background:var(--portal-brand-soft);color:var(--portal-brand)}.qa-message__ops button.is-active{border-color:#8cc8a2;background:#e9f7ef;color:#14804a}.qa-message__ops button.is-active.is-danger{border-color:#efb0a6;background:#fff1ee;color:#c2412d}.qa-main__index{position:absolute;right:8px;top:50%;transform:translateY(-50%);display:flex;flex-direction:column;gap:6px;padding:0;z-index:6;transition:all .22s ease}.qa-main__index-item{position:relative;display:flex;align-items:center;justify-content:flex-end;width:18px;height:16px;border:none;background:transparent;color:#a4b1bc;cursor:pointer;transition:all .2s ease}.qa-main__index-number{display:none;position:absolute;right:18px;font-size:11px;font-weight:700;color:inherit;white-space:nowrap}.qa-main__index-preview{display:none;position:absolute;right:34px;max-width:140px;padding:0;font-size:11px;font-weight:600;color:inherit;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.qa-main__index-line{display:block;width:12px;height:2px;border-radius:999px;background:currentColor;opacity:.85;transition:all .2s ease}.qa-main__index:hover{padding:10px;border-radius:4px;background:var(--portal-card-solid);border:1px solid var(--portal-border);box-shadow:var(--portal-shadow-soft)}.qa-main__index:hover .qa-main__index-item{width:176px}.qa-main__index:hover .qa-main__index-number,.qa-main__index:hover .qa-main__index-preview{display:block}.qa-main__index-item:hover,.qa-main__index-item.is-active{color:var(--portal-brand)}.qa-main__index-item.is-active .qa-main__index-number{display:block}.qa-main__index-item.is-active .qa-main__index-line{width:16px}
.qa-composer{position:sticky;bottom:0;z-index:3;padding:10px 14px 12px;border-top:1px solid var(--portal-border);background:var(--portal-card-solid);backdrop-filter:blur(8px)}.qa-composer__attachments{display:flex;gap:8px;flex-wrap:wrap;margin-bottom:8px}.qa-attachment{display:inline-flex;align-items:center;gap:8px;padding:6px 8px;border-radius:4px;background:var(--portal-surface-bg);border:1px solid var(--portal-border);font-size:12px;color:var(--portal-text-secondary)}.qa-attachment button{border:none;background:transparent;color:var(--portal-text-secondary);cursor:pointer}
.qa-composer :deep(.el-textarea__inner){border-radius:4px;min-height:92px;background:var(--portal-card-solid);color:var(--portal-text);border-color:var(--portal-border);padding:12px 14px}.qa-composer__toolbar{display:flex;align-items:center;justify-content:space-between;gap:10px;margin-top:10px;flex-wrap:nowrap}.qa-composer__left{display:flex;align-items:center;gap:8px;flex-wrap:nowrap;min-width:0;flex:1}.qa-composer__right{display:flex;align-items:center;gap:8px;flex-shrink:0}.qa-composer__status{font-size:12px;color:var(--portal-brand);white-space:nowrap}.qa-tool-btn{display:inline-flex;align-items:center;justify-content:center;gap:6px;height:36px;padding:0 12px;border:1px solid var(--portal-border);border-radius:4px;background:var(--portal-card-solid);color:var(--portal-text);cursor:pointer;font-size:12px;font-weight:600;line-height:1;white-space:nowrap;transition:all .2s ease;flex-shrink:0}.qa-tool-btn i{display:inline-flex;align-items:center;justify-content:center;font-size:14px;line-height:1}.qa-tool-btn:hover{border-color:var(--portal-border-strong);background:var(--portal-brand-soft);color:var(--portal-brand)}.qa-tool-select{display:flex;align-items:center;gap:10px;padding:0 12px;height:36px;border:1px solid var(--portal-border);border-radius:4px;background:var(--portal-card-solid);flex-shrink:0}.qa-tool-select--model{min-width:300px}.qa-tool-select__icon{width:20px;height:20px;border-radius:4px;background:var(--portal-brand-light);color:var(--portal-brand);display:flex;align-items:center;justify-content:center;flex-shrink:0;font-size:11px}.qa-tool-select__body{display:flex;align-items:center;min-width:0;flex:1}.qa-tool-select :deep(.el-select){width:100%}.qa-tool-select :deep(.el-select__wrapper){box-shadow:none;background:transparent;padding:0;min-height:auto;height:auto}.qa-tool-select :deep(.el-select__placeholder),.qa-tool-select :deep(.el-select__selected-item){font-size:13px;color:var(--portal-text);font-weight:700;line-height:1.2}.qa-tool-select :deep(.el-select__caret){color:var(--portal-text-secondary)}.qa-tool-switch{display:inline-flex;align-items:center;justify-content:center;gap:6px;height:36px;padding:0 10px;border:1px solid var(--portal-border);border-radius:4px;background:var(--portal-card-solid);font-size:12px;font-weight:600;color:var(--portal-text);line-height:1;white-space:nowrap;transition:all .2s ease;flex-shrink:0}.qa-tool-switch i{display:inline-flex;align-items:center;justify-content:center;font-size:14px;line-height:1;flex-shrink:0}.qa-tool-switch span{display:inline-flex;align-items:center;line-height:1}.qa-tool-switch:hover{border-color:var(--portal-border-strong);background:var(--portal-brand-soft)}.qa-tool-switch.disabled{opacity:.55}.qa-tool-switch :deep(.el-switch){margin-left:2px;--el-switch-height:18px;--el-switch-width:32px}.qa-send-btn{display:inline-flex;align-items:center;gap:7px;height:36px;padding:0 16px;border:none;border-radius:4px;background:var(--portal-accent);color:#fff;font-size:13px;font-weight:600;cursor:pointer;box-shadow:0 4px 10px rgba(0,110,255,.16);flex-shrink:0}.qa-send-btn i{font-size:14px}.qa-send-btn:disabled{opacity:.6;cursor:not-allowed}
.qa-sidebar__history :deep(.el-scrollbar__wrap),.qa-main__scroll :deep(.el-scrollbar__wrap){scroll-behavior:smooth}
.qa-sidebar__history :deep(.el-scrollbar__bar.is-vertical),.qa-main__scroll :deep(.el-scrollbar__bar.is-vertical){right:2px}
.qa-sidebar__history :deep(.el-scrollbar__thumb),.qa-main__scroll :deep(.el-scrollbar__thumb){background:#90a4b4}
.markdown-body :deep(p){margin:0 0 10px}.markdown-body :deep(pre){padding:0;border-radius:4px;overflow:auto}.markdown-body :deep(.hljs){margin:10px 0;background:#0f172a;color:#e5e7eb;padding:12px 14px;border-radius:4px}.markdown-body :deep(code){font-family:Consolas, Monaco, 'Courier New', monospace}.markdown-body :deep(:not(pre) > code){background:var(--portal-brand-light);color:var(--portal-brand);padding:2px 6px;border-radius:4px}.markdown-body :deep(ul),.markdown-body :deep(ol){padding-left:18px}.markdown-body :deep(blockquote){margin:10px 0;padding:8px 10px;border-left:4px solid var(--portal-border-strong);background:var(--portal-surface-bg);color:var(--portal-text-secondary)}.markdown-body :deep(.qa-table-wrap){width:100%;overflow-x:auto;margin:12px 0}.markdown-body :deep(table){display:table;max-width:100%;border-collapse:separate;border-spacing:0;width:100%;margin:0;border:1px solid var(--portal-border);border-radius:4px;background:var(--portal-card-solid)}.markdown-body :deep(thead tr){background:var(--portal-surface-bg)}.markdown-body :deep(th),.markdown-body :deep(td){border-right:1px solid var(--portal-border);border-bottom:1px solid var(--portal-border);padding:9px 10px;text-align:left;vertical-align:top;min-width:90px;line-height:1.7}.markdown-body :deep(th:last-child),.markdown-body :deep(td:last-child){border-right:none}.markdown-body :deep(tbody tr:last-child td){border-bottom:none}.markdown-body :deep(tbody tr:nth-child(even)){background:var(--portal-surface-bg)}.markdown-body :deep(a){color:var(--portal-accent);text-decoration:none}
.qa-feedback-dialog :deep(.el-dialog){border-radius:8px;overflow:hidden}.qa-feedback-dialog :deep(.el-dialog__header){padding:18px 20px 10px;margin-right:0}.qa-feedback-dialog :deep(.el-dialog__title){font-size:15px;font-weight:700;color:var(--portal-text)}.qa-feedback-dialog :deep(.el-dialog__body){padding:4px 20px 8px}.qa-feedback-dialog :deep(.el-dialog__footer){padding:10px 20px 18px}.qa-feedback-form :deep(.el-form-item){align-items:flex-start;margin-bottom:18px}.qa-feedback-form :deep(.el-form-item__label){padding-top:8px;font-size:13px;color:var(--portal-text)}.qa-feedback-form__type :deep(.el-radio-button__inner){min-width:92px;border-radius:6px;padding:10px 18px;font-weight:600;box-shadow:none}.qa-feedback-form :deep(.el-textarea__inner){min-height:150px;border-radius:6px;padding:12px 14px;font-size:13px;line-height:1.7}
@media (max-width: 1100px){.qa-workbench{grid-template-columns:1fr;height:calc(100vh - 96px);max-height:calc(100vh - 96px);min-height:unset}.qa-sidebar{display:none}.qa-main__floating-header{top:8px;left:10px;right:10px;padding:0}.qa-main__actions{position:static;margin-left:10px}.qa-main__title-input{width:100%}.qa-main__title-input :deep(.el-input__inner){font-size:14px}.qa-empty__title{font-size:22px}.qa-composer__toolbar{flex-direction:column;align-items:flex-start}.qa-composer__left,.qa-composer__right{width:100%}.qa-tool-select{width:100%}.qa-tool-select :deep(.el-select){width:100%}.qa-main__content{padding:50px 12px 12px}.qa-message.assistant,.qa-message.user{padding-left:0;padding-right:0}.qa-message__bubble{max-width:calc(100% - 40px)}.qa-main__index{display:none}}
</style>
