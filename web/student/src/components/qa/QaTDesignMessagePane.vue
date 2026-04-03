<template>
  <div class="qa-td-poc">
    <div v-if="!messages.length" class="qa-td-poc__empty">
      <div class="qa-td-poc__eyebrow">智慧问答</div>
      <div class="qa-td-poc__title">有什么我能帮你的吗？</div>
      <div class="qa-td-poc__subtitle">
        围绕课程学习、作业分析、复盘答疑与图像理解，给你连续、清晰、可追问的校园学习协作体验。
      </div>
      <div class="qa-td-poc__suggestions">
        <button
          v-for="item in suggestionList"
          :key="item"
          type="button"
          class="qa-td-poc__suggestion"
          @click="$emit('apply-suggestion', item)"
        >
          {{ item }}
        </button>
      </div>
    </div>

    <el-scrollbar v-else ref="scrollbarRef" class="qa-td-poc__scroll">
      <div ref="contentRef" class="qa-td-poc__list">
        <t-chat-message
          v-for="entry in resolvedMessages"
          :key="entry.item.id"
          :role="entry.item.role"
          :status="entry.status"
          :placement="entry.item.role === 'user' ? 'right' : 'left'"
          :variant="entry.item.role === 'user' ? 'base' : 'outline'"
          :content="entry.content"
          :chat-content-props="entry.chatContentProps"
        >
          <template #avatar>
            <div class="qa-td-poc__avatar" :class="`is-${entry.item.role}`">
              <i :class="entry.item.role === 'user' ? 'ri-user-3-line' : 'ri-robot-2-line'"></i>
            </div>
          </template>

          <template #name>
            <div class="qa-td-poc__name">
              <strong>{{ entry.item.role === 'user' ? '我' : 'AI 助手' }}</strong>
              <span v-if="entry.questionOrder" class="qa-td-poc__name-tag">问题 {{ entry.questionOrder }}</span>
              <span v-if="entry.item.modelName" class="qa-td-poc__name-tag">{{ entry.item.modelName }}</span>
            </div>
          </template>

          <template v-if="entry.item.role === 'assistant'" #content>
            <div class="qa-td-poc__assistant-shell">
              <div class="qa-td-poc__assistant-content">
                <template v-for="(block, blockIndex) in entry.content" :key="`${entry.item.id}_${block.type}_${blockIndex}`">
                  <t-chat-reasoning
                    v-if="block.type === 'thinking'"
                    :key="`${entry.item.id}_thinking_${block.status || 'complete'}`"
                    class="qa-td-poc__assistant-block"
                    :default-collapsed="block.ext?.defaultCollapsed"
                    :layout="block.ext?.layout"
                    :collapse-panel-props="{ destroyOnCollapse: false }"
                  >
                    <template #header>
                      <div class="qa-td-poc__thinking-header">
                        <span>{{ block.data.title }}</span>
                        <span
                          v-if="block.status === 'pending'"
                          class="qa-td-poc__thinking-pending"
                        >
                          生成中
                        </span>
                      </div>
                    </template>

                    <div
                      class="qa-td-poc__thinking-body"
                      :style="{ maxHeight: `${block.ext?.maxHeight || 180}px` }"
                    >
                      <div
                        v-if="block.status === 'pending'"
                        class="qa-td-poc__thinking-stream"
                      >
                        {{ block.data.text }}
                      </div>
                      <t-chat-markdown
                        v-else
                        class="qa-td-poc__assistant-markdown"
                        :content="block.data.text"
                        :options="MARKDOWN_OPTIONS"
                      />
                    </div>
                  </t-chat-reasoning>

                  <t-chat-markdown
                    v-else-if="block.type === 'markdown'"
                    class="qa-td-poc__assistant-block qa-td-poc__assistant-markdown"
                    :content="block.data"
                    :options="MARKDOWN_OPTIONS"
                  />

                  <QaEchartsBlock
                    v-else-if="block.type === 'chart'"
                    class="qa-td-poc__assistant-block qa-td-poc__chart"
                    :source="block.data"
                    :embedded="true"
                  />
                </template>
              </div>
            </div>
          </template>

          <template #actionbar>
            <div
              v-if="entry.item.role === 'assistant' && entry.showAssistantActions"
              class="qa-td-poc__ops is-assistant"
            >
              <div class="qa-td-poc__action-cluster">
                <t-tooltip content="重新生成" placement="top" show-arrow>
                  <t-button
                    theme="default"
                    variant="text"
                    shape="circle"
                    size="small"
                    class="qa-td-poc__action-btn is-icon"
                    @click="$emit('reask', entry.replayPrompt)"
                  >
                    <template #icon>
                      <i class="ri-refresh-line"></i>
                    </template>
                  </t-button>
                </t-tooltip>

                <t-tooltip content="复制" placement="top" show-arrow>
                  <t-button
                    theme="default"
                    variant="text"
                    shape="circle"
                    size="small"
                    class="qa-td-poc__action-btn is-icon"
                    @click="$emit('copy', entry.copyContent)"
                  >
                    <template #icon>
                      <i class="ri-file-copy-line"></i>
                    </template>
                  </t-button>
                </t-tooltip>

                <t-tooltip content="点赞" placement="top" show-arrow>
                  <t-button
                    theme="default"
                    variant="text"
                    shape="circle"
                    size="small"
                    class="qa-td-poc__action-btn is-icon"
                    :class="{ 'is-active': entry.item.feedbackType === 'helpful' }"
                    @click="emit('feedback', { item: entry.item, type: 'helpful' })"
                  >
                    <template #icon>
                      <i :class="entry.item.feedbackType === 'helpful' ? 'ri-thumb-up-fill' : 'ri-thumb-up-line'"></i>
                    </template>
                  </t-button>
                </t-tooltip>

                <t-tooltip content="点踩" placement="top" show-arrow>
                  <t-button
                    theme="default"
                    variant="text"
                    shape="circle"
                    size="small"
                    class="qa-td-poc__action-btn is-icon"
                    :class="{ 'is-active is-danger': entry.item.feedbackType === 'unhelpful' }"
                    @click="emit('feedback', { item: entry.item, type: 'unhelpful' })"
                  >
                    <template #icon>
                      <i :class="entry.item.feedbackType === 'unhelpful' ? 'ri-thumb-down-fill' : 'ri-thumb-down-line'"></i>
                    </template>
                  </t-button>
                </t-tooltip>

                <span class="qa-td-poc__action-divider"></span>

                <t-tooltip v-if="entry.showContinueAction" content="继续追问" placement="top" show-arrow>
                  <t-button
                    theme="default"
                    variant="text"
                    shape="circle"
                    size="small"
                    class="qa-td-poc__action-btn is-icon"
                    @click="$emit('continue', entry.item.content)"
                  >
                    <template #icon>
                      <i class="ri-chat-forward-line"></i>
                    </template>
                  </t-button>
                </t-tooltip>
              </div>
            </div>

            <div v-else-if="entry.item.role === 'user' && entry.showReaskAction" class="qa-td-poc__ops is-user">
              <div class="qa-td-poc__action-cluster">
                <t-tooltip content="重新提问" placement="top" show-arrow>
                  <t-button
                    theme="default"
                    variant="text"
                    shape="circle"
                    size="small"
                    class="qa-td-poc__action-btn is-icon"
                    @click="$emit('reask', entry.replayPrompt)"
                  >
                    <template #icon>
                      <i class="ri-restart-line"></i>
                    </template>
                  </t-button>
                </t-tooltip>
              </div>
            </div>
          </template>
        </t-chat-message>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'
import {
  ChatMarkdown as TChatMarkdown,
  ChatMessage as TChatMessage,
  ChatReasoning as TChatReasoning,
} from '@tdesign-vue-next/chat'
import { Button as TButton, Tooltip as TTooltip } from 'tdesign-vue-next'
import QaEchartsBlock from '@/components/qa/QaEchartsBlock.vue'
import { normalizeQaMarkdownDocument, normalizeQaMarkdownSource } from '@/utils/qaMarkdown'

type QaMessage = {
  id: string
  role: 'user' | 'assistant'
  content: string
  modelName?: string
  reasoningContent?: string
  attachments?: any[]
  messageId?: number
  feedbackType?: string
}

type QaMessageContentItem = {
  type: string
  data: any
  status?: string
  ext?: Record<string, any>
  slotName?: string
}

const MARKDOWN_OPTIONS = {
  themeSettings: {
    codeBlockTheme: 'one-light',
  },
} as const

const props = defineProps<{
  messages: QaMessage[]
  streaming: boolean
  currentStreamingAssistantId: string
  suggestionList: string[]
}>()

const emit = defineEmits<{
  (e: 'apply-suggestion', value: string): void
  (e: 'open-attachment', attachment: any): void
  (e: 'copy', content: string): void
  (e: 'continue', content: string): void
  (e: 'reask', content: string): void
  (e: 'feedback', payload: { item: QaMessage; type: 'helpful' | 'unhelpful' }): void
}>()

const scrollbarRef = ref<{ wrapRef?: HTMLElement | null } | null>(null)
const contentRef = ref<HTMLElement | null>(null)

const resolvedMessages = computed(() => {
  let questionOrder = 0

  return props.messages.map((item, index) => {
    if (item.role === 'user') {
      questionOrder += 1
    }

    const renderModel = buildMessageRenderModel(item)
    const replayPrompt = resolveReplayPrompt(item, index)

    return {
      item,
      questionOrder: item.role === 'user' ? questionOrder : undefined,
      replayPrompt,
      status: resolveMessageStatus(item),
      content: renderModel.content,
      copyContent: renderModel.copyContent || String(item.content || ''),
      showAssistantActions: item.role === 'assistant' && Boolean(String(item.content || '').trim()),
      showContinueAction: item.role === 'assistant' && Boolean(String(item.content || '').trim()),
      showReaskAction: item.role === 'user' && Boolean(String(replayPrompt || '').trim()),
      chatContentProps: {
        markdown: {
          options: MARKDOWN_OPTIONS,
        },
        attachments: {
          onFileClick: (event: any) => handleAttachmentClick(item, event),
        },
        thinking: {
          layout: 'border',
          maxHeight: 180,
        },
      },
    }
  })
})

watch(
  () => props.messages,
  async () => {
    await nextTick()
    const wrap = scrollbarRef.value?.wrapRef
    const content = contentRef.value
    if (!wrap || !content) return
    wrap.scrollTop = content.scrollHeight
  },
  { deep: true, immediate: true },
)

function isStreamingAssistantMessage(item: QaMessage) {
  return item.role === 'assistant' && props.streaming && item.id === props.currentStreamingAssistantId
}

function resolveMessageStatus(item: QaMessage) {
  return isStreamingAssistantMessage(item) && !String(item.content || '').trim() ? 'streaming' : ''
}

function resolveReplayPrompt(item: QaMessage, index: number) {
  if (item.role === 'user') {
    return item.content
  }

  for (let cursor = index - 1; cursor >= 0; cursor -= 1) {
    const candidate = props.messages[cursor]
    if (candidate?.role === 'user' && String(candidate.content || '').trim()) {
      return candidate.content
    }
  }

  return item.content
}

function buildMessageRenderModel(item: QaMessage) {
  if (item.role === 'user') {
    return {
      content: buildUserMessageContent(item),
      copyContent: String(item.content || ''),
    }
  }

  return buildAssistantMessageContent(item)
}

function buildUserMessageContent(item: QaMessage): QaMessageContentItem[] {
  const content: QaMessageContentItem[] = []

  if (String(item.content || '').trim()) {
    content.push({
      type: 'text',
      data: item.content,
    })
  }

  if (item.attachments?.length) {
    content.push({
      type: 'attachment',
      data: item.attachments.map((attachment) => mapAttachmentToTDesignItem(attachment)),
    })
  }

  return content
}

function buildAssistantMessageContent(item: QaMessage) {
  const content: QaMessageContentItem[] = []
  const streamingMessage = isStreamingAssistantMessage(item)
  const normalizedCopyContent = normalizeQaMarkdownDocument(item.content || '')

  if (String(item.reasoningContent || '').trim()) {
    content.push({
      type: 'thinking',
      status: streamingMessage ? 'pending' : 'complete',
      data: {
        title: streamingMessage ? '思考中...' : '思考过程',
        text: normalizeQaMarkdownSource(item.reasoningContent || ''),
      },
      ext: {
        defaultCollapsed: !streamingMessage,
        layout: 'border',
        maxHeight: streamingMessage ? 220 : 180,
      },
    })
  }

  if (!String(item.content || '').trim()) {
    return { content, copyContent: normalizedCopyContent }
  }

  if (streamingMessage) {
    content.push({
      type: 'markdown',
      data: buildStreamingAssistantMarkdown(item.content || ''),
    })
    return { content, copyContent: normalizedCopyContent }
  }

  buildMarkdownAndChartSegments(normalizedCopyContent).forEach((segment) => {
    content.push({
      type: segment.type,
      data: segment.data,
    })
  })

  return { content, copyContent: normalizedCopyContent }
}

function buildMarkdownAndChartSegments(content: string) {
  const normalized = String(content || '')
  const segments: Array<{ type: 'markdown' | 'chart'; data: string }> = []
  const chartFenceRegex = /```qa-echarts\s*\n([\s\S]*?)\n```/g

  let lastIndex = 0

  for (const match of normalized.matchAll(chartFenceRegex)) {
    const matchIndex = match.index ?? 0
    const markdownChunk = String(normalized.slice(lastIndex, matchIndex) || '').trim()
    const chartSource = String(match[1] || '').trim()

    if (markdownChunk) {
      segments.push({
        type: 'markdown',
        data: markdownChunk,
      })
    }

    if (chartSource) {
      segments.push({
        type: 'chart',
        data: chartSource,
      })
    }

    lastIndex = matchIndex + match[0].length
  }

  const tailChunk = String(normalized.slice(lastIndex) || '').trim()
  if (tailChunk) {
    segments.push({
      type: 'markdown',
      data: tailChunk,
    })
  }

  if (!segments.length && normalized.trim()) {
    segments.push({
      type: 'markdown',
      data: normalized.trim(),
    })
  }

  return segments
}

function buildStreamingAssistantMarkdown(content: string) {
  const raw = String(content || '').replace(/\r\n?/g, '\n')
  if (!raw.trim()) {
    return '正在生成回答...'
  }

  let preview = raw
  const chartStartIndex = preview.search(
    /:::\s*echarts|```(?:json|yaml|js|javascript|qa-echart|qa-echarts|echarts?)|(?:^|[{\s,])["']?(?:series|dataset|xAxis|yAxis|legend|tooltip|grid|title)["']?\s*:\s*|setOption\s*\(/,
  )

  if (chartStartIndex >= 0) {
    const intro = preview.slice(0, chartStartIndex).trim()
    preview = intro ? `${intro}\n\n> 正在生成图表...` : '正在生成图表...'
  }

  return normalizeQaMarkdownSource(preview)
}

function mapAttachmentToTDesignItem(attachment: any) {
  const name = attachment?.originalName || attachment?.fileName || attachment?.name || '附件'
  const url = attachment?.fileUrl || attachment?.imageUrl || ''

  return {
    fileType: resolveAttachmentFileType(attachment, name),
    name,
    url,
    size: attachment?.size,
  }
}

function resolveAttachmentFileType(attachment: any, name: string) {
  const mimeType = String(attachment?.mimeType || '').toLowerCase()
  if (mimeType.startsWith('image/')) {
    return 'image'
  }

  const extension = String(name).split('.').pop()?.toLowerCase() || ''
  if (!extension) {
    return 'file'
  }

  if (['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp', 'svg'].includes(extension)) {
    return 'image'
  }

  if (['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'zip', 'rar', 'txt', 'md'].includes(extension)) {
    return extension
  }

  return 'file'
}

function handleAttachmentClick(item: QaMessage, event: any) {
  const detail = event?.detail || event
  const target = item.attachments?.find((attachment) => {
    const name = attachment?.originalName || attachment?.fileName || attachment?.name || '附件'
    const url = attachment?.fileUrl || attachment?.imageUrl || ''

    if (detail?.url && url) {
      return detail.url === url
    }

    return detail?.name === name
  })

  if (target && resolveAttachmentFileType(target, target?.originalName || target?.fileName || target?.name || '') === 'image') {
    return
  }

  event?.preventDefault?.()
  event?.stopPropagation?.()

  if (target) {
    emit('open-attachment', target)
    return
  }

  emit('open-attachment', {
    originalName: detail?.name || '附件',
    fileUrl: detail?.url,
    imageUrl: detail?.url,
    mimeType: detail?.fileType,
    size: detail?.size,
  })
}

</script>

<style scoped>
.qa-td-poc {
  height: 100%;
  min-height: 0;
}

.qa-td-poc__empty {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 88px 24px 24px;
}

.qa-td-poc__eyebrow {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 12px;
  border-radius: 999px;
  background: rgba(8, 145, 178, 0.12);
  color: #0f5e70;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.qa-td-poc__title {
  margin-top: 14px;
  color: var(--portal-text);
  font-size: 28px;
  font-weight: 800;
}

.qa-td-poc__subtitle {
  margin-top: 10px;
  max-width: 720px;
  color: var(--portal-text-secondary);
  font-size: 14px;
  line-height: 1.8;
}

.qa-td-poc__suggestions {
  margin-top: 18px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.qa-td-poc__suggestion {
  border: 1px solid rgba(8, 145, 178, 0.16);
  background: #fff;
  padding: 10px 14px;
  border-radius: 999px;
  color: #0f5e70;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease, border-color 0.2s ease, transform 0.2s ease;
}

.qa-td-poc__suggestion:hover {
  background: #ecfeff;
  border-color: rgba(8, 145, 178, 0.26);
  transform: translateY(-1px);
}

.qa-td-poc__scroll {
  height: 100%;
}

.qa-td-poc__scroll :deep(.el-scrollbar__wrap) {
  height: 100%;
}

.qa-td-poc__scroll :deep(.el-scrollbar__view) {
  min-height: 100%;
}

.qa-td-poc__list {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 72px 24px 24px;
}

.qa-td-poc__list :deep(.t-chat__item__inner) {
  width: 100%;
  margin-bottom: 0;
}

.qa-td-poc__list :deep(.t-chat__item__inner.left) {
  padding-right: 14%;
}

.qa-td-poc__list :deep(.t-chat__item__inner.right) {
  padding-left: 26%;
}

.qa-td-poc__list :deep(.t-chat__item__inner.left .t-chat__content) {
  justify-content: flex-start;
}

.qa-td-poc__list :deep(.t-chat__item__inner.right .t-chat__content) {
  justify-content: flex-end;
}

.qa-td-poc__list :deep(.t-chat__item__inner.left .t-chat__item__main) {
  width: min(100%, 940px);
}

.qa-td-poc__list :deep(.t-chat__item__inner.right .t-chat__item__main) {
  width: auto;
  max-width: min(100%, 560px);
  align-items: flex-end;
}

.qa-td-poc__list :deep(.t-chat__item__main) {
  gap: 4px;
}

.qa-td-poc__list :deep(.t-chat__item-chat-loading) {
  margin-left: 8px;
}

.qa-td-poc__list :deep(.t-chat__avatar) {
  margin: 0 8px 0 0;
  padding-top: 2px;
  align-self: flex-start;
}

.qa-td-poc__list :deep(.t-chat__avatar__box) {
  padding-top: 0;
}

.qa-td-poc__list :deep(.t-chat__item__inner.right .t-chat__avatar) {
  margin: 0 0 0 8px;
}

.qa-td-poc__list :deep(.t-chat__item__header) {
  padding: 0 4px 2px;
  min-height: 22px;
}

.qa-td-poc__list :deep(.t-chat__item__role--assistant .t-chat__item__header) {
  justify-content: flex-start;
}

.qa-td-poc__list :deep(.t-chat__item__role--user .t-chat__item__header) {
  justify-content: flex-end;
}

.qa-td-poc__list :deep(.t-chat__item__content) {
  width: 100%;
  border-radius: 20px;
}

.qa-td-poc__list :deep(.t-chat__item__role--assistant .t-chat__item__content) {
  background: transparent;
  border: none;
  box-shadow: none;
  padding: 0;
}

.qa-td-poc__list :deep(.t-chat__item__role--assistant .t-chat__detail) {
  width: 100%;
  max-width: none;
  padding: 0;
}

.qa-td-poc__assistant-shell {
  width: min(100%, 920px);
  margin-right: auto;
  padding: 14px 16px 16px;
  border: 1px solid rgba(180, 212, 228, 0.92);
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.05);
}

.qa-td-poc__assistant-content {
  display: grid;
  gap: 12px;
}

.qa-td-poc__thinking-header {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.qa-td-poc__thinking-pending {
  color: #0f5e70;
  font-size: 12px;
  font-weight: 500;
}

.qa-td-poc__thinking-body {
  overflow: auto;
  padding-right: 4px;
}

.qa-td-poc__thinking-stream {
  white-space: pre-wrap;
  word-break: break-word;
  color: var(--portal-text-secondary);
  font-size: 13px;
  line-height: 1.7;
}

.qa-td-poc__assistant-block {
  min-width: 0;
}

.qa-td-poc__assistant-markdown {
  display: block;
}

.qa-td-poc__assistant-markdown :deep(.cherry) {
  background: transparent;
}

.qa-td-poc__assistant-markdown :deep(.cherry-previewer) {
  padding: 0;
  background: transparent;
}

.qa-td-poc__assistant-markdown :deep(.cherry-previewer p:first-child) {
  margin-top: 0;
}

.qa-td-poc__list :deep(.t-chat__item__role--assistant .t-chat__reasoning) {
  border-radius: 14px;
  overflow: hidden;
  background: #f8fbfd;
  border: 1px solid rgba(191, 219, 233, 0.9);
  margin-top: 0;
}

.qa-td-poc__list :deep(.t-chat__item__role--user .t-chat__item__content) {
  background: transparent;
  padding: 0;
  box-shadow: none;
  border: none;
}

.qa-td-poc__list :deep(.t-chat__item__role--user .t-chat__text--user) {
  display: inline-block;
  background: linear-gradient(180deg, #e9f7fb 0%, #dff3ff 100%);
  border: 1px solid rgba(125, 211, 252, 0.7);
  border-radius: 18px;
  padding: 12px 16px;
  box-shadow: 0 10px 22px rgba(8, 145, 178, 0.08);
  color: #0f172a;
  max-width: 100%;
  white-space: pre-wrap;
  word-break: break-word;
}

.qa-td-poc__list :deep(.t-chat__item__role--user t-attachments) {
  display: block;
  margin-top: 10px;
}

.qa-td-poc__list :deep(.t-chat__item__role--assistant .t-chat__actions),
.qa-td-poc__list :deep(.t-chat__item__role--user .t-chat__actions) {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(191, 219, 233, 0.86);
  border-radius: 999px;
  padding: 2px 4px;
}

.qa-td-poc__list :deep(.t-chat-actions__item__wrapper) {
  border-radius: 999px;
}

.qa-td-poc__avatar {
  width: 34px;
  height: 34px;
  border-radius: 11px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  color: #0f5e70;
  background: rgba(8, 145, 178, 0.12);
}

.qa-td-poc__avatar.is-user {
  color: #0f766e;
  background: rgba(20, 184, 166, 0.14);
}

.qa-td-poc__name {
  display: inline-flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  min-height: 22px;
  margin-left: 6px;
  margin-bottom: 5px;
  color: var(--portal-text-secondary);
  font-size: 12px;
  line-height: 1.2;
}

.qa-td-poc__name strong {
  color: var(--portal-text);
  font-size: 13px;
}

.qa-td-poc__name-tag {
  display: inline-flex;
  align-items: center;
  min-height: 20px;
  padding: 0 7px;
  border-radius: 999px;
  background: rgba(8, 145, 178, 0.08);
  color: #0f5e70;
  font-size: 11px;
  font-weight: 700;
}

.qa-td-poc__chart {
  width: min(100%, 820px);
  margin: 2px auto 0;
}

.qa-td-poc__ops {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 8px;
}

.qa-td-poc__ops.is-assistant,
.qa-td-poc__ops.is-user {
  justify-content: flex-start;
}

.qa-td-poc__action-cluster {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  min-height: 32px;
  padding: 3px;
  border: 1px solid rgba(203, 213, 225, 0.9);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 6px 14px rgba(15, 23, 42, 0.04);
  backdrop-filter: blur(10px);
}

.qa-td-poc__actionbar {
  display: inline-flex;
}

.qa-td-poc__actionbar :deep(.t-chat__actions) {
  background: transparent;
  border: none;
  padding: 0;
  gap: 0;
}

.qa-td-poc__actionbar :deep(.t-chat-actions__item__wrapper) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 9px;
  background: transparent;
  color: var(--portal-text-secondary);
  transition: background-color 0.18s ease, color 0.18s ease;
}

.qa-td-poc__actionbar :deep(.t-chat-actions__item__wrapper:hover) {
  background: rgba(8, 145, 178, 0.08);
  color: #0f5e70;
}

.qa-td-poc__actionbar :deep(.t-chat-actions__item__wrapper .t-icon) {
  font-size: 14px;
  line-height: 1;
}

.qa-td-poc__action-divider {
  width: 1px;
  height: 14px;
  margin: 0 1px;
  background: rgba(203, 213, 225, 0.95);
  flex-shrink: 0;
}

.qa-td-poc__action-btn {
  width: 26px;
  height: 26px;
  border-radius: 9px;
  color: var(--portal-text-secondary);
  transition: background-color 0.18s ease, color 0.18s ease;
}

.qa-td-poc__action-btn.is-active {
  color: #0f5e70;
  background: rgba(8, 145, 178, 0.12);
}

.qa-td-poc__action-btn.is-active.is-danger {
  color: #b42318;
  background: rgba(220, 38, 38, 0.10);
}

.qa-td-poc__action-btn:hover {
  color: #0f5e70;
  background: rgba(8, 145, 178, 0.08);
}

.qa-td-poc__action-btn :deep(.t-icon),
.qa-td-poc__action-btn i {
  font-size: 14px;
  line-height: 1;
}

@media (max-width: 1100px) {
  .qa-td-poc__list {
    padding: 68px 12px 16px;
  }

  .qa-td-poc__list :deep(.t-chat__item__inner.left),
  .qa-td-poc__list :deep(.t-chat__item__inner.right) {
    padding-left: 0;
    padding-right: 0;
  }

  .qa-td-poc__list :deep(.t-chat__item__inner.left .t-chat__item__main),
  .qa-td-poc__list :deep(.t-chat__item__inner.right .t-chat__item__main) {
    width: 100%;
    max-width: 100%;
  }
}
</style>
