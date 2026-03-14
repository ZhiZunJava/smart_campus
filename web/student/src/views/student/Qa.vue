<template>
  <div class="qa-workbench">
    <aside class="qa-sidebar">
      <div class="qa-sidebar__brand">
        <div class="qa-sidebar__brand-main">
          <div class="qa-sidebar__logo"><i class="ri-sparkling-2-line"></i></div>
          <div class="qa-sidebar__brand-text">
            <div class="qa-sidebar__badge">校园专属</div>
            <div class="qa-sidebar__title">智慧校园 AI 助手</div>
            <div class="qa-sidebar__subtitle">支持深度思考、原生流式、Markdown 渲染与图片理解</div>
          </div>
        </div>
        <div class="qa-sidebar__brand-tags">
          <span>课程问答</span>
          <span>学习规划</span>
          <span>图片理解</span>
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
      <div class="qa-sidebar__history">
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
    </aside>

    <section class="qa-main">
      <header class="qa-main__header">
        <div>
          <div class="qa-main__title">{{ currentSessionTitle }}</div>
          <div class="qa-main__desc">{{ headerDesc }}</div>
        </div>
        <div class="qa-main__actions">
          <span class="qa-main__pill"><i class="ri-robot-2-line"></i>{{ selectedModel?.modelName || '未选择模型' }}</span>
          <button v-if="streaming" type="button" class="qa-main__danger" @click="stopStreaming">
            <i class="ri-stop-circle-line"></i>
            <span>停止生成</span>
          </button>
        </div>
      </header>

      <main ref="messageContainerRef" class="qa-main__messages">
        <div v-if="!chatMessages.length" class="qa-empty">
          <div class="qa-empty__title">有什么我能帮你的吗？</div>
          <div class="qa-empty__subtitle">像豆包 / DeepSeek 一样的校园智能问答工作台，支持多轮上下文与图片理解。</div>
          <div class="qa-empty__suggestions">
            <button v-for="item in suggestionList" :key="item" type="button" class="qa-suggestion" @click="applySuggestion(item)">{{ item }}</button>
          </div>
        </div>

        <div v-for="item in chatMessages" :key="item.id" class="qa-message" :class="item.role">
          <div class="qa-message__avatar">
            <i :class="item.role === 'user' ? 'ri-user-3-line' : 'ri-robot-2-line'"></i>
          </div>
          <div class="qa-message__bubble">
            <div class="qa-message__meta">
              <strong>{{ item.role === 'user' ? '我' : 'AI 助手' }}</strong>
              <span v-if="item.modelName">{{ item.modelName }}</span>
            </div>

            <div
              v-if="item.role === 'assistant'"
              class="qa-message__content markdown-body"
              v-html="renderMarkdown(item.content || (streaming ? '思考中...' : ''))"
            ></div>
            <div v-else class="qa-message__content qa-message__content--plain">{{ item.content }}</div>

            <div class="qa-message__ops">
              <button v-if="item.role === 'assistant' && item.content" type="button" @click="copyText(item.content)">
                <i class="ri-file-copy-line"></i><span>复制</span>
              </button>
              <button v-if="item.role === 'assistant' && item.content" type="button" @click="reuseAnswer(item.content)">
                <i class="ri-chat-forward-line"></i><span>追问延展</span>
              </button>
              <button v-if="item.role === 'assistant' && item.content" type="button" @click="continueFromAnswer(item.content)">
                <i class="ri-arrow-go-forward-line"></i><span>继续追问</span>
              </button>
              <button v-if="item.role === 'user'" type="button" @click="reAskMessage(item.content)">
                <i class="ri-restart-line"></i><span>重新回答</span>
              </button>
            </div>
          </div>
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
                <span class="qa-tool-select__label">当前模型</span>
                <el-select v-model="selectedModelId" placeholder="选择模型" filterable @change="handleModelChange">
                  <el-option v-for="item in modelOptions" :key="item.modelId" :label="`${item.modelName}（${item.provider}）`" :value="item.modelId" />
                </el-select>
              </div>
            </div>

            <label class="qa-tool-switch" :class="{ disabled: selectedModel?.supportStream !== '1' }">
              <i class="ri-signal-wifi-line"></i>
              <span>Stream</span>
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
  </div>
</template>

<script setup lang="ts">
import MarkdownIt from 'markdown-it'
import DOMPurify from 'dompurify'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox, type ElMessageBoxOptions } from 'element-plus'
import { getToken } from '@/utils/auth'
import usePortalUserStore from '@/store/user'
import { addQaSession, deleteQaSession, listQaMessage, listQaModelOptions, listQaSession, updateQaSession } from '@/api/portal'

const SESSION_PIN_KEY = 'qa-session-pins'

const userStore = usePortalUserStore()
const md = new MarkdownIt({
  breaks: true,
  linkify: true,
  html: false,
  highlight(code, lang) {
    try {
      if (lang && hljs.getLanguage(lang)) {
        return `<pre class="hljs"><code>${hljs.highlight(code, { language: lang }).value}</code></pre>`
      }
      return `<pre class="hljs"><code>${hljs.highlightAuto(code).value}</code></pre>`
    } catch {
      return `<pre class="hljs"><code>${md.utils.escapeHtml(code)}</code></pre>`
    }
  },
})

const inputText = ref('')
const streaming = ref(false)
const streamEnabled = ref(true)
const deepThinking = ref(false)
const selectedModelId = ref<number | undefined>()
const modelOptions = ref<any[]>([])
const images = ref<Array<{ name: string; dataUrl: string; mimeType: string }>>([])
const sessions = ref<any[]>([])
const sessionKeyword = ref('')
const activeSessionId = ref<number | undefined>()
const pinnedSessionIds = ref<number[]>([])
const chatMessages = ref<Array<{ id: string; role: 'user' | 'assistant'; content: string; modelName?: string }>>([])
const messageContainerRef = ref<HTMLElement | null>(null)
const currentAbortController = ref<AbortController | null>(null)

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
const headerDesc = computed(() => {
  if (streaming.value) return '模型正在实时生成答案'
  if (selectedModel.value?.supportVision === '1') return '当前模型支持图片理解、Markdown 渲染与高级问答'
  return '支持历史对话、模型切换、深度思考与实时输出'
})

watch(chatMessages, async () => {
  await nextTick()
  if (messageContainerRef.value) {
    messageContainerRef.value.scrollTop = messageContainerRef.value.scrollHeight
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
  const res = await listQaMessage({ pageNum: 1, pageSize: 200, sessionId: item.sessionId })
  chatMessages.value = (res.rows || []).map((message: any) => ({
    id: `${message.roleType}_${message.messageId}`,
    role: message.roleType === 'assistant' ? 'assistant' : 'user',
    content: message.content,
    modelName: message.referenceSource?.replace('AI模型：', '') || '',
  }))
}

async function createNewChat() {
  activeSessionId.value = undefined
  chatMessages.value = []
  inputText.value = ''
  images.value = []
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
  const dataUrl = await readFileAsDataUrl(raw)
  if (!images.value.some((item) => item.name === raw.name)) {
    images.value.push({ name: raw.name, dataUrl, mimeType: raw.type || 'image/png' })
  }
}

function readFileAsDataUrl(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(String(reader.result || ''))
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}

function removeImage(name: string) {
  images.value = images.value.filter((item) => item.name !== name)
}

function applySuggestion(text: string) {
  inputText.value = text
}

function stopStreaming() {
  currentAbortController.value?.abort()
  currentAbortController.value = null
  streaming.value = false
  inputText.value = '请从刚才中断的位置继续回答。'
}

async function ensureSession(question: string) {
  if (activeSessionId.value) return activeSessionId.value
  if (!userStore.user?.userId) return undefined
  const title = question.length > 18 ? `${question.slice(0, 18)}...` : question
  await addQaSession({ userId: userStore.user.userId, courseId: null, sessionTitle: title, sourceType: 'course', status: '0' })
  await loadSessions()
  const newSession = sessions.value.find((item) => item.sessionTitle === title) || sessions.value[0]
  activeSessionId.value = newSession?.sessionId
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
  chatMessages.value.push({ id: `user_${Date.now()}`, role: 'user', content: question })
  chatMessages.value.push({ id: assistantId, role: 'assistant', content: '', modelName: selectedModel.value?.modelName || '' })
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
      }
    }

    await loadSessions()
  } catch (error: any) {
    const target = chatMessages.value.find((item) => item.id === assistantId)
    if (target) {
      target.content = error?.name === 'AbortError' ? '已停止生成，你可以继续追问。' : (error?.message || '问答请求失败')
    }
  } finally {
    streaming.value = false
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
  if (eventName === 'session' && payload.sessionId) activeSessionId.value = payload.sessionId
  if (eventName === 'chunk') target.content += payload.content || ''
  if (eventName === 'done' && payload.aiResponse?.modelName) target.modelName = payload.aiResponse.modelName
  if (eventName === 'error') target.content = payload.message || '生成失败'
}

function renderMarkdown(content: string) {
  return DOMPurify.sanitize(md.render(content || ''))
}

async function copyText(text: string) {
  await navigator.clipboard.writeText(text)
  ElMessage.success('已复制')
}

function reuseAnswer(text: string) {
  inputText.value = `请基于下面这段回答继续展开：\n${text}`
}

function continueFromAnswer(text: string) {
  inputText.value = `请从这里继续：\n${text}`
}

function reAskMessage(text: string) {
  inputText.value = text
}

onMounted(async () => {
  loadPinnedSessions()
  await loadModels()
  await loadSessions()
})
</script>

<style scoped>
.qa-workbench{display:grid;grid-template-columns:300px 1fr;height:calc(100vh - 140px);min-height:780px;max-height:calc(100vh - 140px);background:#f6f8fc;border:1px solid #e6ebf3;border-radius:28px;overflow:hidden;box-shadow:0 18px 50px rgba(15,23,42,.05)}
.qa-sidebar{min-height:0;background:#f3f5fa;border-right:1px solid #e6eaf2;padding:18px 16px;display:flex;flex-direction:column}.qa-sidebar__brand{margin-bottom:18px;padding:14px;border-radius:20px;background:linear-gradient(180deg,#ffffff 0%,#f7faff 100%);border:1px solid #e6ecf7;box-shadow:0 10px 28px rgba(15,23,42,.04)}.qa-sidebar__brand-main{display:flex;align-items:center;gap:12px}.qa-sidebar__brand-text{flex:1}.qa-sidebar__logo{width:52px;height:52px;border-radius:18px;background:linear-gradient(135deg,#5a8bff 0%,#7e6bff 100%);display:flex;align-items:center;justify-content:center;color:#fff;font-size:24px;box-shadow:0 10px 20px rgba(90,139,255,.22)}.qa-sidebar__badge{display:inline-flex;padding:4px 10px;border-radius:999px;background:#eef4ff;color:#4f46e5;font-size:12px;font-weight:700;margin-bottom:8px}.qa-sidebar__title{font-size:18px;font-weight:800;color:#1f2937}.qa-sidebar__subtitle{font-size:12px;color:#8a94a6;margin-top:4px;line-height:1.6}.qa-sidebar__brand-tags{display:flex;gap:8px;flex-wrap:wrap;margin-top:14px}.qa-sidebar__brand-tags span{padding:6px 10px;border-radius:999px;background:#f8fafc;border:1px solid #e7edf6;color:#64748b;font-size:12px}
.qa-sidebar__new{display:flex;align-items:center;justify-content:center;gap:8px;height:46px;border:none;border-radius:16px;background:linear-gradient(135deg,#4b8dff 0%,#5da8ff 100%);color:#fff;font-size:15px;font-weight:600;cursor:pointer;box-shadow:0 12px 22px rgba(75,141,255,.24);margin-bottom:16px}.qa-sidebar__new i{font-size:18px}
.qa-sidebar__search{display:flex;align-items:center;gap:10px;height:42px;padding:0 14px;border-radius:14px;background:#fff;border:1px solid #e7edf6;margin-bottom:18px}.qa-sidebar__search i{color:#94a3b8}.qa-sidebar__search input{border:none;outline:none;background:transparent;flex:1;font-size:14px;color:#334155}.qa-sidebar__section{font-size:13px;color:#8a94a6;margin-bottom:10px}.qa-sidebar__history{flex:1;min-height:0;overflow:auto;display:flex;flex-direction:column;gap:8px;padding-right:4px}
.qa-session{display:flex;align-items:center;gap:10px;padding:12px 14px;border-radius:16px;background:#fff;border:1px solid transparent;cursor:pointer;transition:.2s}.qa-session:hover{border-color:#d8e0f0;transform:translateY(-1px)}.qa-session.active{background:#eef4ff;border-color:#bfd3ff}.qa-session__main{flex:1;min-width:0}.qa-session__title-row{display:flex;align-items:center;gap:6px}.qa-session__title{font-size:14px;font-weight:600;color:#1f2937;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.qa-session__pin{color:#f59e0b;font-size:14px}.qa-session__meta{margin-top:6px;font-size:12px;color:#8a94a6}.qa-session__more{width:28px;height:28px;border:none;border-radius:10px;background:#f8fafc;color:#64748b;cursor:pointer;display:flex;align-items:center;justify-content:center}
.qa-main{min-height:0;display:grid;grid-template-rows:auto minmax(0,1fr) auto;background:#fff}.qa-main__header{display:flex;align-items:center;justify-content:space-between;padding:16px 22px;border-bottom:1px solid #edf1f7;background:rgba(255,255,255,.92);backdrop-filter:blur(8px)}.qa-main__title{font-size:22px;font-weight:700;color:#111827}.qa-main__desc{margin-top:4px;font-size:13px;color:#8a94a6}.qa-main__actions{display:flex;align-items:center;gap:10px}.qa-main__pill{display:inline-flex;align-items:center;gap:8px;padding:8px 12px;border-radius:999px;background:#f6f8fc;border:1px solid #e6ebf3;color:#475569;font-size:13px}.qa-main__danger{display:inline-flex;align-items:center;gap:6px;padding:9px 12px;border:none;border-radius:12px;background:#fff1f2;color:#e11d48;cursor:pointer}
.qa-main__messages{min-height:0;overflow:auto;padding:28px 30px 24px;background:linear-gradient(180deg,#fbfcfe 0%,#f8f9fc 100%);scroll-behavior:smooth}.qa-empty{min-height:100%;display:flex;flex-direction:column;align-items:center;justify-content:center}.qa-empty__title{font-size:42px;font-weight:800;color:#111827}.qa-empty__subtitle{margin-top:14px;font-size:15px;color:#8a94a6}.qa-empty__suggestions{margin-top:28px;display:flex;gap:12px;flex-wrap:wrap;justify-content:center;max-width:960px}.qa-suggestion{border:none;background:#f2f4f8;padding:12px 18px;border-radius:999px;color:#374151;cursor:pointer;transition:.2s}.qa-suggestion:hover{background:#e8eefb;color:#2563eb}
.qa-message{display:flex;gap:14px;max-width:100%;margin:0 0 18px}.qa-message.assistant{justify-content:flex-start;padding-right:14%}.qa-message.user{justify-content:flex-end;flex-direction:row-reverse;padding-left:14%}.qa-message__avatar{width:40px;height:40px;border-radius:50%;background:#eef2ff;color:#4f46e5;display:flex;align-items:center;justify-content:center;font-size:18px;flex-shrink:0;box-shadow:0 6px 16px rgba(79,70,229,.08)}.qa-message.user .qa-message__avatar{background:#dbeafe;color:#2563eb}.qa-message__bubble{width:fit-content;max-width:min(820px,calc(100% - 54px));padding:16px 18px;border-radius:20px;background:#fff;border:1px solid #e6ebf3;box-shadow:0 6px 20px rgba(15,23,42,.03)}.qa-message.user .qa-message__bubble{background:#eef4ff;border-color:#d9e5ff}.qa-message__meta{display:flex;align-items:center;justify-content:space-between;gap:10px;margin-bottom:10px;color:#6b7280;font-size:12px}.qa-message__meta strong{color:#111827;font-size:14px}.qa-message__content{line-height:1.9;color:#374151}.qa-message__content--plain{white-space:pre-wrap;word-break:break-word}.qa-message__ops{margin-top:12px;display:flex;gap:14px;flex-wrap:wrap}.qa-message__ops button{display:inline-flex;align-items:center;gap:6px;border:none;background:transparent;color:#4f46e5;cursor:pointer;font-size:13px}
.qa-composer{position:sticky;bottom:0;z-index:3;padding:16px 22px 20px;border-top:1px solid #edf1f7;background:rgba(255,255,255,.98);backdrop-filter:blur(10px);box-shadow:0 -8px 24px rgba(15,23,42,.04)}.qa-composer__attachments{display:flex;gap:8px;flex-wrap:wrap;margin-bottom:10px}.qa-attachment{display:inline-flex;align-items:center;gap:8px;padding:8px 10px;border-radius:12px;background:#f8fafc;border:1px solid #e5e7eb;font-size:13px;color:#475569}.qa-attachment button{border:none;background:transparent;color:#94a3b8;cursor:pointer}
.qa-composer__toolbar{display:flex;align-items:center;justify-content:space-between;gap:12px;margin-top:12px}.qa-composer__left{display:flex;align-items:center;gap:10px;flex-wrap:wrap}.qa-composer__right{display:flex;align-items:center;gap:10px}.qa-composer__status{font-size:13px;color:#7c3aed}.qa-tool-btn{display:inline-flex;align-items:center;gap:8px;height:42px;padding:0 14px;border:1px solid #e5e7eb;border-radius:14px;background:#fff;color:#334155;cursor:pointer}.qa-tool-select{display:flex;align-items:center;gap:10px;padding:0 12px;height:56px;border:1px solid #dce5f3;border-radius:16px;background:linear-gradient(180deg,#ffffff 0%,#f8fbff 100%);box-shadow:0 6px 20px rgba(15,23,42,.04)}.qa-tool-select--model{min-width:250px}.qa-tool-select__icon{width:32px;height:32px;border-radius:10px;background:#eef4ff;color:#4f46e5;display:flex;align-items:center;justify-content:center;flex-shrink:0}.qa-tool-select__body{display:flex;flex-direction:column;justify-content:center;min-width:0}.qa-tool-select__label{font-size:12px;color:#94a3b8;margin-bottom:2px}.qa-tool-select :deep(.el-select){width:220px}.qa-tool-select :deep(.el-select__wrapper){box-shadow:none;background:transparent;padding:0;min-height:auto}.qa-tool-select :deep(.el-select__placeholder),.qa-tool-select :deep(.el-select__selected-item){font-size:14px;color:#334155;font-weight:600}.qa-tool-switch{display:flex;align-items:center;gap:8px;padding:0 10px;height:42px;border:1px solid #e5e7eb;border-radius:14px;background:#fff;font-size:13px;color:#334155}.qa-tool-switch.disabled{opacity:.55}.qa-send-btn{display:inline-flex;align-items:center;gap:8px;height:42px;padding:0 18px;border:none;border-radius:14px;background:linear-gradient(135deg,#4b8dff 0%,#5da8ff 100%);color:#fff;font-size:15px;font-weight:600;cursor:pointer;box-shadow:0 10px 20px rgba(75,141,255,.2)}.qa-send-btn:disabled{opacity:.6;cursor:not-allowed}
.markdown-body :deep(p){margin:0 0 12px}.markdown-body :deep(pre){padding:0;border-radius:12px;overflow:auto}.markdown-body :deep(.hljs){margin:12px 0;background:#0f172a;color:#e5e7eb;padding:14px 16px;border-radius:12px}.markdown-body :deep(code){font-family:Consolas, Monaco, 'Courier New', monospace}.markdown-body :deep(:not(pre) > code){background:#eef2ff;color:#4f46e5;padding:2px 6px;border-radius:6px}.markdown-body :deep(ul),.markdown-body :deep(ol){padding-left:20px}.markdown-body :deep(blockquote){margin:12px 0;padding:8px 12px;border-left:4px solid #cbd5e1;background:#f8fafc;color:#475569}.markdown-body :deep(table){border-collapse:collapse;width:100%;margin:12px 0}.markdown-body :deep(th),.markdown-body :deep(td){border:1px solid #e5e7eb;padding:8px 10px;text-align:left}.markdown-body :deep(a){color:#2563eb;text-decoration:none}
@media (max-width: 1100px){.qa-workbench{grid-template-columns:1fr;height:calc(100vh - 110px);max-height:calc(100vh - 110px);min-height:unset}.qa-sidebar{display:none}.qa-empty__title{font-size:30px}.qa-composer__toolbar{flex-direction:column;align-items:flex-start}.qa-composer__left,.qa-composer__right{width:100%}.qa-tool-select{width:100%}.qa-tool-select :deep(.el-select){width:100%}}
</style>
