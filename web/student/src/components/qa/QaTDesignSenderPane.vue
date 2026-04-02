<template>
  <div class="qa-td-sender-pane">
    <t-chat-sender
      v-model="innerValue"
      class="qa-td-sender-pane__sender"
      :loading="streaming"
      :textarea-props="{ placeholder: '输入你的问题，支持课程问答、知识点解释、解题思路分析', autosize: { minRows: 3, maxRows: 7 } }"
      @send="$emit('submit')"
      @stop="$emit('stop')"
      @file-select="handleFileSelect"
    >
      <template #header>
        <t-attachments
          v-if="senderAttachments.length"
          class="qa-td-sender-pane__attachments"
          :items="senderAttachments"
          overflow="scrollX"
          :removable="true"
          :image-viewer="false"
          @remove="handleAttachmentRemove"
          @file-click="handleAttachmentClick"
        />
      </template>

      <template #suffix="{ renderPresets }">
        <component :is="renderPresets(senderSuffixActions)" />
      </template>

      <template #inner-header>
        <div v-if="contextSummary" class="qa-td-sender-pane__inner-header">
          <div class="qa-td-sender-pane__inner-copy">
            <i class="ri-links-line qa-td-sender-pane__inner-icon"></i>
            <p>{{ contextSummary }}</p>
          </div>
          <button
            v-if="showContextClear"
            type="button"
            class="qa-td-sender-pane__inner-clear"
            @click="$emit('clear-context')"
          >
            <CloseIcon />
          </button>
        </div>
      </template>

      <template #footer-prefix>
        <div class="qa-td-sender-pane__controls">
          <div class="qa-td-sender-pane__context-row">
            <t-select
              v-model="selectedCourseIdModel"
              class="qa-td-sender-pane__context-select qa-td-sender-pane__context-select--course"
              :options="courseOptions"
              clearable
              filterable
              placeholder="关联课程"
              @change="handleCourseChange"
            />

            <t-select
              v-model="selectedExamRecordIdModel"
              class="qa-td-sender-pane__context-select qa-td-sender-pane__context-select--exam"
              :options="examOptions"
              clearable
              filterable
              placeholder="选择考试记录"
              :disabled="!examOptions.length"
            />

            <t-button class="qa-td-sender-pane__quick-btn" variant="outline" @click="$emit('apply-course-plan')">
              <i class="ri-road-map-line"></i>
              <span>生成规划</span>
            </t-button>

            <t-button
              class="qa-td-sender-pane__quick-btn"
              variant="outline"
              :disabled="!selectedExamRecordIdModel"
              @click="$emit('apply-exam-analysis')"
            >
              <i class="ri-bar-chart-box-line"></i>
              <span>考试分析</span>
            </t-button>

            <t-button
              class="qa-td-sender-pane__quick-btn"
              variant="outline"
              :disabled="!selectedCourseIdModel"
              @click="$emit('apply-wrongbook-plan')"
            >
              <i class="ri-booklet-line"></i>
              <span>错题规划</span>
            </t-button>
          </div>

          <div class="qa-td-sender-pane__model-row">
            <t-tooltip content="切换模型" trigger="hover">
              <t-select
                v-model="selectedModelIdModel"
                class="qa-td-sender-pane__select"
                :options="modelSelectOptions"
                placeholder="选择模型"
                filterable
              />
            </t-tooltip>

            <t-tag
              v-if="visionSupported"
              class="qa-td-sender-pane__vision-tag"
              shape="round"
              variant="light"
              theme="primary"
            >
              多模态
            </t-tag>

            <t-button
              class="qa-td-sender-pane__thinking-btn"
              :class="{ 'is-active': deepThinkingModel, 'is-disabled': !reasoningSupported }"
              variant="outline"
              @click="toggleThinking"
            >
              <SystemSumIcon />
              <span>深度思考</span>
            </t-button>
          </div>
        </div>
      </template>
    </t-chat-sender>

    <t-image-viewer
      :key="previewViewerKey"
      :visible="previewVisible"
      attach="body"
      mode="modal"
      :close-on-overlay="true"
      :close-on-esc-keydown="true"
      :z-index="5600"
      :default-index="previewDefaultIndex"
      :images="previewImages"
      :on-close="handlePreviewClose"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { Attachments as TAttachments, ChatSender as TChatSender } from '@tdesign-vue-next/chat'
import { ImageViewer as TImageViewer, Select as TSelect, Tooltip as TTooltip, Button as TButton, Tag as TTag } from 'tdesign-vue-next'
import { SystemSumIcon, CloseIcon } from 'tdesign-icons-vue-next'
import type { TdAttachmentItem } from 'tdesign-web-components/lib/filecard/type'

type QaImageAttachment = {
  attachmentId?: number
  name: string
  imageUrl: string
  fileName?: string
  originalName?: string
  mimeType: string
  size?: number
}

type QaModelOption = {
  modelId: number
  modelName: string
  provider: string
  supportVision?: string
}

type QaSelectOption = {
  label: string
  value: number
}

type QaSenderAttachmentItem = TdAttachmentItem & {
  sourceName: string
}

const innerValue = defineModel<string>({ default: '' })
const selectedModelIdModel = defineModel<number | undefined>('selectedModelId')
const deepThinkingModel = defineModel<boolean>('deepThinking', { default: false })
const selectedCourseIdModel = defineModel<number | undefined>('selectedCourseId')
const selectedExamRecordIdModel = defineModel<number | undefined>('selectedExamRecordId')
const previewVisible = ref(false)
const previewDefaultIndex = ref(0)
const previewViewerKey = ref(0)

const props = defineProps<{
  streaming: boolean
  images: QaImageAttachment[]
  modelOptions: QaModelOption[]
  courseOptions: QaSelectOption[]
  examOptions: QaSelectOption[]
  reasoningSupported: boolean
  visionSupported: boolean
  contextSummary?: string
  showContextClear?: boolean
}>()

const emit = defineEmits<{
  (e: 'submit'): void
  (e: 'stop'): void
  (e: 'upload-images', files: File[]): void
  (e: 'remove-image', name: string): void
  (e: 'open-attachment', attachment: any): void
  (e: 'clear-context'): void
  (e: 'course-change'): void
  (e: 'apply-course-plan'): void
  (e: 'apply-exam-analysis'): void
  (e: 'apply-wrongbook-plan'): void
}>()

const modelSelectOptions = computed(() => props.modelOptions.map((item) => ({
  label: `${item.modelName}（${item.provider}）${item.supportVision === '1' ? ' · 多模态' : ''}`,
  value: item.modelId,
})))

const senderSuffixActions = computed(() => {
  const actions: Array<{ name: 'uploadImage' | 'uploadAttachment' }> = []
  if (props.visionSupported) {
    actions.push({ name: 'uploadImage' })
  }
  return actions
})

const senderAttachments = computed<QaSenderAttachmentItem[]>(() => props.images.map((item) => ({
  key: item.name,
  sourceName: item.name,
  name: item.originalName || item.name,
  url: item.imageUrl,
  type: item.mimeType || 'image/png',
  status: 'success',
  fileType: 'image' as const,
  extension: String(item.mimeType || '').split('/')[1] || 'png',
  size: item.size,
})))

const previewImages = computed(() => props.images
  .map(item => item.imageUrl)
  .filter((url): url is string => Boolean(url)))

function toggleThinking() {
  if (!props.reasoningSupported) return
  deepThinkingModel.value = !deepThinkingModel.value
}

function handleCourseChange() {
  emit('course-change')
}

function handleFileSelect(context: { name?: string; files?: File[] }) {
  if (context?.name !== 'uploadImage') return
  const files = Array.isArray(context?.files) ? context.files : []
  emit('upload-images', files)
}

function handleAttachmentRemove(event: CustomEvent<TdAttachmentItem>) {
  const payload = event.detail as QaSenderAttachmentItem
  const name = payload?.sourceName || payload?.key || payload?.name
  if (!name) return
  emit('remove-image', name)
}

function handleAttachmentClick(event: CustomEvent<TdAttachmentItem>) {
  const payload = event.detail as QaSenderAttachmentItem
  const targetKey = payload?.key || payload?.sourceName || payload?.name
  const targetIndex = senderAttachments.value.findIndex(item =>
    [item.key, item.sourceName, item.name].includes(targetKey),
  )

  const resolvedIndex = targetIndex >= 0 ? targetIndex : (senderAttachments.value.length === 1 ? 0 : -1)
  if (resolvedIndex < 0 || resolvedIndex >= previewImages.value.length) return

  previewDefaultIndex.value = resolvedIndex
  previewViewerKey.value += 1
  previewVisible.value = true
}

function handlePreviewClose() {
  previewVisible.value = false
}
</script>

<style scoped>
.qa-td-sender-pane__sender {
  border: 1px solid var(--portal-border);
  border-radius: 10px;
  background: var(--portal-card-solid);
  overflow: hidden;
}

.qa-td-sender-pane__sender :deep(.t-chat-sender) {
  background: transparent;
}

.qa-td-sender-pane__sender :deep(.t-chat-sender__textarea) {
  border: none;
  border-radius: 0;
  box-shadow: none;
  background: transparent;
}

.qa-td-sender-pane__attachments {
  display: block;
  padding: 0 12px 10px;
}

.qa-td-sender-pane__sender :deep(.t-textarea__inner) {
  background: transparent;
  color: var(--portal-text);
}

.qa-td-sender-pane__sender :deep(.t-chat-sender__inner-header) {
  padding: 0 12px;
}

.qa-td-sender-pane__sender :deep(.t-chat-sender__footer) {
  padding: 0 12px 12px;
  gap: 12px;
  align-items: flex-end;
}

.qa-td-sender-pane__sender :deep(.t-chat-sender__mode) {
  flex: 1;
  min-width: 0;
}

.qa-td-sender-pane__sender :deep(.t-chat-sender__button__sendbtn) {
  display: flex;
  align-items: center;
  gap: 0;
}

.qa-td-sender-pane__sender :deep(.t-chat-sender__button__default) {
  margin-left: -8px;
}

.qa-td-sender-pane__sender :deep(.t-button) {
  border-radius: 999px;
}

.qa-td-sender-pane__inner-header {
  display: flex;
  width: 100%;
  margin-bottom: 8px;
  padding-bottom: 8px;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid var(--td-component-stroke);
}

.qa-td-sender-pane__inner-copy {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
}

.qa-td-sender-pane__inner-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  font-size: 16px;
  color: var(--td-text-color-disabled);
  margin-left: 2px;
  padding: 0;
  flex-shrink: 0;
}

.qa-td-sender-pane__inner-copy p {
  margin: 0 0 0 4px;
  color: var(--td-text-color-placeholder);
  font-size: 13px;
  line-height: 1.6;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.qa-td-sender-pane__inner-clear {
  border: none;
  background: transparent;
  color: var(--td-text-color-disabled);
  padding: 6px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
}

.qa-td-sender-pane__inner-clear:hover {
  color: var(--td-brand-color-hover);
}

.qa-td-sender-pane__controls {
  display: grid;
  gap: 10px;
  width: 100%;
  min-width: 0;
}

.qa-td-sender-pane__context-row,
.qa-td-sender-pane__model-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.qa-td-sender-pane__context-select {
  min-width: 0;
  height: var(--td-comp-size-m);
}

.qa-td-sender-pane__context-select--course {
  width: 192px;
}

.qa-td-sender-pane__context-select--exam {
  width: 268px;
}

.qa-td-sender-pane__quick-btn {
  height: var(--td-comp-size-m);
  flex: 0 0 auto;
}

.qa-td-sender-pane__quick-btn :deep(.t-button__text) {
  display: inline-flex;
  align-items: center;
}

.qa-td-sender-pane__quick-btn :deep(.t-button__text span) {
  margin-left: var(--td-comp-margin-s);
}

.qa-td-sender-pane__vision-tag {
  flex: 0 0 auto;
}

.qa-td-sender-pane__select {
  width: 148px;
  height: var(--td-comp-size-m);
}

.qa-td-sender-pane__sender :deep(.qa-td-sender-pane__select .t-input) {
  border-radius: 999px;
  padding: 0 15px;
}

.qa-td-sender-pane__sender :deep(.qa-td-sender-pane__select .t-input.t-is-focused) {
  box-shadow: none;
}

.qa-td-sender-pane__sender :deep(.qa-td-sender-pane__context-select .t-input) {
  border-radius: 999px;
  padding: 0 15px;
}

.qa-td-sender-pane__sender :deep(.qa-td-sender-pane__context-select .t-input.t-is-focused) {
  box-shadow: none;
}

.qa-td-sender-pane__thinking-btn {
  width: 116px;
  height: var(--td-comp-size-m);
  border-radius: 999px;
  box-sizing: border-box;
  flex: 0 0 auto;
}

.qa-td-sender-pane__thinking-btn :deep(.t-button__text) {
  display: flex;
  align-items: center;
  justify-content: center;
}

.qa-td-sender-pane__thinking-btn :deep(.t-button__text span) {
  margin-left: var(--td-comp-margin-s);
}

.qa-td-sender-pane__thinking-btn.is-active {
  border-color: var(--td-brand-color-focus);
  background: var(--td-brand-color-light);
  color: var(--td-text-color-brand);
}

.qa-td-sender-pane__thinking-btn.is-disabled {
  opacity: 0.55;
}

@media (max-width: 1100px) {
  .qa-td-sender-pane__context-select,
  .qa-td-sender-pane__select,
  .qa-td-sender-pane__thinking-btn,
  .qa-td-sender-pane__quick-btn {
    width: 100%;
  }
}
</style>
