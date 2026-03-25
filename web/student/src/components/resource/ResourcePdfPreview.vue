<template>
  <div class="resource-pdf-preview">
    <div v-if="error" class="resource-pdf-preview__error">
      <i class="ri-file-warning-line"></i>
      <p>{{ error }}</p>
      <el-button type="primary" plain @click="openFallback">在新标签页打开原文件</el-button>
    </div>
    <div v-else class="resource-pdf-preview__container" ref="containerRef">
      <div class="resource-pdf-preview__toolbar" v-if="pdfSource">
        <div class="toolbar-group">
          <el-button :disabled="currentPage <= 1" @click="currentPage = Math.max(1, currentPage - 1)" circle>
            <i class="ri-arrow-left-s-line"></i>
          </el-button>
          <span class="page-indicator">{{ currentPage }} / {{ pageCount }}</span>
          <el-button :disabled="currentPage >= pageCount" @click="currentPage = Math.min(pageCount, currentPage + 1)" circle>
            <i class="ri-arrow-right-s-line"></i>
          </el-button>
        </div>

        <div class="toolbar-divider"></div>

        <div class="toolbar-group">
          <el-input-number
            v-model="pageJump"
            :min="1"
            :max="pageCount"
            :step="1"
            controls-position="right"
            size="small"
            class="page-jump"
            @keyup.enter="jumpToPage"
          />
          <el-button size="small" @click="jumpToPage">跳转</el-button>
        </div>

        <div class="toolbar-divider"></div>

        <div class="toolbar-group">
          <el-button size="small" :type="fitMode === 'fit-width' ? 'primary' : 'default'" @click="fitMode = 'fit-width'">
            适应宽度
          </el-button>
          <el-button size="small" :type="fitMode === 'original' ? 'primary' : 'default'" @click="setOriginalSize">
            原始大小
          </el-button>
        </div>

        <div class="toolbar-group">
          <el-button @click="handleZoomOut" circle size="small">
            <i class="ri-subtract-line"></i>
          </el-button>
          <span class="page-indicator zoom-indicator">{{ Math.round(scale * 100) }}%</span>
          <el-button @click="handleZoomIn" circle size="small">
            <i class="ri-add-line"></i>
          </el-button>
        </div>

        <div class="toolbar-divider"></div>

        <div class="toolbar-group">
          <el-tooltip content="下载 PDF" placement="top">
            <el-button circle @click="downloadPdf">
              <i class="ri-download-2-line"></i>
            </el-button>
          </el-tooltip>
          <el-tooltip :content="isFullscreen ? '退出全屏' : '全屏阅读'" placement="top">
            <el-button circle @click="toggleFullscreen">
              <i :class="isFullscreen ? 'ri-fullscreen-exit-line' : 'ri-fullscreen-line'"></i>
            </el-button>
          </el-tooltip>
        </div>
      </div>
      <el-scrollbar class="resource-pdf-preview__pages-wrap">
        <div ref="pagesRef" class="resource-pdf-preview__pages" :class="[`is-${fitMode}`]">
          <VuePdfEmbed 
            v-if="pdfSource" 
            :key="viewerKey"
            :source="pdfSource" 
            :page="currentPage" 
            :scale="renderScale"
            @loaded="handlePdfLoaded"
            @rendered="handlePdfRendered"
            @loading-failed="handlePdfLoadingFailed"
            @rendering-failed="handlePdfRenderingFailed"
          />
        </div>
      </el-scrollbar>
      <div v-if="loading" class="resource-pdf-preview__loading-mask">
        <i class="ri-loader-4-line preview-spin"></i>
        <p>{{ pdfSource ? '正在切换 PDF 预览...' : 'PDF 正在加载，请稍候...' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, shallowRef, watch } from 'vue'
import type { PDFDocumentProxy } from 'pdfjs-dist/types/src/display/api'
import VuePdfEmbed from 'vue-pdf-embed'
import { ElMessage } from '@/utils/feedback'
import { getCachedPdfDocument, getOrLoadPdfDocument } from '@/utils/pdfPreview'

const props = defineProps<{
  src: string
}>()

const loading = ref(false)
const error = ref('')
const pdfSource = shallowRef<PDFDocumentProxy | null>(null)
const currentPage = ref(1)
const pageCount = ref(1)
const scale = ref(1)
const fitMode = ref<'fit-width' | 'original'>('fit-width')
const pageJump = ref(1)
const pagesRef = ref<HTMLElement | null>(null)
const renderScale = computed(() => (fitMode.value === 'fit-width' ? 1 : scale.value))
const viewerKey = ref('')
let renderRequestId = 0

function setOriginalSize() {
  fitMode.value = 'original'
  scale.value = 1
}

function handleZoomOut() {
  fitMode.value = 'original'
  scale.value = Math.max(0.6, Number((scale.value - 0.1).toFixed(1)))
}

function handleZoomIn() {
  fitMode.value = 'original'
  scale.value = Math.min(2, Number((scale.value + 0.1).toFixed(1)))
}

const containerRef = ref<HTMLElement | null>(null)
const isFullscreen = ref(false)

function resetPreviewState() {
  currentPage.value = 1
  pageCount.value = 1
  scale.value = 1
  fitMode.value = 'fit-width'
  pageJump.value = 1
}

function applyPdfDocument(src: string, pdf: PDFDocumentProxy, requestId: number) {
  if (requestId !== renderRequestId) return
  pdfSource.value = pdf
  viewerKey.value = `${src}::${requestId}`
}

function handlePdfLoaded(pdf: PDFDocumentProxy) {
  if (pdf?.numPages) {
    pageCount.value = pdf.numPages
    currentPage.value = Math.min(currentPage.value, pdf.numPages)
    pageJump.value = currentPage.value
  }
}

function handlePdfRendered() {
  loading.value = false
}

function handlePdfLoadingFailed(err: any) {
  error.value = err?.message || 'PDF 预览失败，请使用“打开资源”查看'
  loading.value = false
  pdfSource.value = null
}

function handlePdfRenderingFailed(err: any) {
  error.value = err?.message || 'PDF 渲染失败，请稍后重试'
  loading.value = false
}

async function renderPdf() {
  const src = String(props.src || '').trim()
  renderRequestId += 1
  const requestId = renderRequestId
  error.value = ''
  resetPreviewState()

  if (!src) {
    loading.value = false
    pdfSource.value = null
    viewerKey.value = ''
    return
  }

  loading.value = true

  const cached = getCachedPdfDocument(src)
  if (cached) {
    applyPdfDocument(src, cached, requestId)
    return
  }

  try {
    const pdf = await getOrLoadPdfDocument(src)
    applyPdfDocument(src, pdf, requestId)
  } catch (err: any) {
    if (requestId !== renderRequestId) return
    pdfSource.value = null
    viewerKey.value = ''
    error.value = err?.message || 'PDF 预览失败，请使用“打开资源”查看'
    loading.value = false
  } finally {
    if (requestId !== renderRequestId) return
    if (!pdfSource.value) {
      loading.value = false
    }
  }
}

function openFallback() {
  if (!props.src) {
    ElMessage.warning('当前没有可打开的 PDF 地址')
    return
  }
  window.open(props.src, '_blank')
}

function jumpToPage() {
  currentPage.value = Math.min(pageCount.value, Math.max(1, Number(pageJump.value || 1)))
}

function downloadPdf() {
  openFallback()
}

async function toggleFullscreen() {
  const el = containerRef.value
  if (!el) return
  if (document.fullscreenElement) {
    await document.exitFullscreen()
  } else {
    await el.requestFullscreen()
  }
}

function handleFullscreenChange() {
  isFullscreen.value = !!document.fullscreenElement
}

onMounted(() => {
  document.addEventListener('fullscreenchange', handleFullscreenChange)
})

onUnmounted(() => {
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})

watch(() => props.src, () => {
  void renderPdf()
}, { immediate: true })

watch(currentPage, (value) => {
  pageJump.value = value
})
</script>

<style scoped>
.resource-pdf-preview {
  height: 100%;
  background: #f8fafc;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.resource-pdf-preview__loading,
.resource-pdf-preview__error {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #64748b;
  gap: 12px;
}

.resource-pdf-preview__error i {
  font-size: 28px;
}

.resource-pdf-preview__container {
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
  background: #e2e8f0;
}

.resource-pdf-preview__loading-mask {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #475569;
  background: rgba(248, 250, 252, 0.76);
  backdrop-filter: blur(3px);
  z-index: 20;
}

.resource-pdf-preview__toolbar {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
  z-index: 10;
}

.toolbar-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-indicator {
  font-size: 14px;
  font-weight: 500;
  color: #475569;
  min-width: 60px;
  text-align: center;
}

.zoom-indicator {
  min-width: 48px;
}

.page-jump {
  width: 100px;
}

.toolbar-divider {
  width: 1px;
  height: 24px;
  background: #cbd5e1;
  margin: 0 8px;
}

.resource-pdf-preview__pages-wrap {
  flex: 1;
  overflow: hidden;
}

.resource-pdf-preview__pages {
  padding: 24px 0;
}

.resource-pdf-preview__pages :deep(.vue-pdf-embed) {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.resource-pdf-preview__pages :deep(.vue-pdf-embed > div) {
  width: 100%;
  max-width: 920px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  overflow: hidden;
}

.resource-pdf-preview__pages.is-fit-width :deep(canvas) {
  width: 100% !important;
  height: auto !important;
  display: block;
}

.resource-pdf-preview__pages.is-original :deep(.vue-pdf-embed > div) {
  width: auto;
  max-width: none;
}

.resource-pdf-preview__pages.is-original :deep(canvas) {
  width: auto !important;
  max-width: none !important;
  height: auto !important;
  display: block;
}

.preview-spin {
  font-size: 32px;
  color: #3b82f6;
  animation: preview-spin 1s linear infinite;
}

@keyframes preview-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
