<template>
  <div class="portal-page resource-detail-page">
    <div class="resource-detail-shell">
      <div class="detail-inner">
        <header class="resource-detail-header">
            <div class="resource-detail-header__main">
              <button type="button" class="resource-detail-back" @click="backToCatalog">
                <i class="ri-arrow-left-s-line"></i>
                返回目录
              </button>
              <div class="resource-detail-header__eyebrow">
                <span class="header-kicker">
                  <i class="ri-book-open-line"></i>
                  课时资源详情
                </span>
                <span class="header-kicker is-accent">
                  <i class="ri-stack-line"></i>
                  {{ currentAssetTypeLabel }}
                </span>
              </div>
              <h1>{{ selectedArticle?.resourceName }}</h1>
              <div class="resource-detail-header__subtitle">{{ currentCourseSubtitle }}</div>
              <div class="resource-detail-header__meta">
                <span class="meta-pill">
                  <i class="ri-user-star-line"></i>
                  授课教师：{{ selectedArticle?.authorName || '未填写作者' }}
                </span>
                <span class="meta-pill">
                  <i class="ri-building-line"></i>
                  制作单位：{{ selectedArticle?.publisherName || '未填写制作单位' }}
                </span>
                <span class="meta-pill">
                  <i class="ri-folders-line"></i>
                  {{ availableDetailTypes.length || 0 }} 类课时资源
                </span>
                <span class="meta-pill detail-rating">
                  <i class="ri-star-smile-line"></i>
                  综合评分 {{ formatRating(selectedArticle?.avgRating) }} 分
                </span>
              </div>
            </div>

            <div class="resource-detail-header__aside">
              <div class="resource-summary-grid">
                <div class="resource-summary-card">
                  <div class="resource-summary-card__label">
                    <i class="ri-eye-line"></i>
                    浏览次数
                  </div>
                  <strong>{{ displayCount(selectedArticle?.viewCount) }}</strong>
                </div>
                <div class="resource-summary-card">
                  <div class="resource-summary-card__label">
                    <i class="ri-heart-3-line"></i>
                    收藏人数
                  </div>
                  <strong>{{ displayCount(selectedArticle?.favoriteCount) }}</strong>
                </div>
                <div class="resource-summary-card">
                  <div class="resource-summary-card__label">
                    <i class="ri-chat-1-line"></i>
                    学习评论
                  </div>
                  <strong>{{ commentTotalCount }}</strong>
                </div>
                <div class="resource-summary-card">
                  <div class="resource-summary-card__label">
                    <i class="ri-star-line"></i>
                    资源评分
                  </div>
                  <strong>{{ formatRating(selectedArticle?.avgRating) }}</strong>
                </div>
              </div>

              <div class="resource-detail-header__actions">
              <button
                type="button"
                class="header-action"
                :class="{ 'is-favorited': isFavorited }"
                :disabled="favoriteLoading"
                @click="submitFavorite"
              >
                <i :class="isFavorited ? 'ri-heart-3-fill' : 'ri-heart-3-line'"></i>
                {{ isFavorited ? '已收藏' : '收藏' }}
              </button>
              <button
                type="button"
                class="header-action is-share"
                :disabled="shareLoading"
                @click="submitShare"
              >
                <i :class="shareLoading ? 'ri-loader-4-line preview-spin' : 'ri-share-forward-line'"></i>
                {{ shareActionLabel }}
              </button>
            </div>
          </div>
          </header>

          <div class="resource-detail-body">
            <div class="resource-preview-panel">
              <div class="resource-preview-panel__surface">
                <template v-if="activeAsset">
                  <div class="preview-stage">
                    <template v-if="isVideoAsset(activeAsset)">
                      <ResourceVideoPlayer :src="activeAssetUrl" />
                    </template>
                    <template v-else-if="isPdfAsset(activeAsset)">
                      <ResourcePdfPreview :key="activeAssetUrl" :src="activeAssetUrl" />
                    </template>
                    <template v-else-if="isOfficeAsset(activeAsset) && officePreviewLoading">
                      <div class="preview-empty">
                        <i class="ri-loader-4-line preview-spin"></i>
                        <p>正在生成文档预览，请稍候...</p>
                      </div>
                    </template>
                    <template v-else-if="isOfficeAsset(activeAsset) && officePreviewPdfUrl">
                      <ResourcePdfPreview :key="officePreviewPdfUrl" :src="officePreviewPdfUrl" />
                    </template>
                    <template v-else-if="isOfficeAsset(activeAsset)">
                      <div class="preview-empty">
                        <i class="ri-file-warning-line"></i>
                        <p>{{ officePreviewError || '文档预览暂不可用，请点击“打开资源”查看原文件。' }}</p>
                      </div>
                    </template>
                    <template v-else-if="activeAsset.fileUrl">
                      <iframe class="preview-iframe" :src="activeAssetUrl" title="资源预览"></iframe>
                    </template>
                    <template v-else>
                      <div class="preview-empty">
                        <i class="ri-file-list-3-line"></i>
                        <p>当前资源没有可预览文件，请使用下方打开按钮查看。</p>
                      </div>
                    </template>
                  </div>
                </template>
                <el-empty v-else description="当前文章还没有可展示的资源" />
              </div>

              <div class="resource-preview-panel__footer">
                <div class="resource-preview-panel__copyright">
                  感谢所有为资源建设提供资料的单位和个人，未经允许不得转载或引用。
                </div>
                <div class="resource-preview-panel__actions">
                  <el-button type="primary" size="small" @click="openAsset" :disabled="!activeAsset?.fileUrl">
                    <i class="ri-external-link-line"></i>
                    打开资源
                  </el-button>
                </div>
              </div>
            </div>

            <aside class="resource-asset-sidebar">
              <div class="resource-asset-sidebar__title">
                <i class="ri-folder-open-line"></i> 课时资源
              </div>

              <div class="resource-asset-sidebar__list">
                <button
                  v-for="type in availableDetailTypes"
                  :key="type.value"
                  type="button"
                  class="resource-asset-type"
                  :class="{ 'is-active': activeAssetType === type.value }"
                  @click="switchAssetType(type.value)"
                >
                  <div class="resource-asset-type__left">
                    <i :class="getAssetIcon(type.value)"></i>
                    <span class="resource-asset-type__label">{{ type.label }}</span>
                  </div>
                  <div class="resource-asset-type__right">
                    <i v-if="activeAssetType === type.value" class="ri-bar-chart-2-fill active-icon"></i>
                    <span v-else class="resource-asset-type__count"><i class="ri-checkbox-blank-circle-line"></i></span>
                  </div>
                </button>
              </div>
            </aside>
          </div>

          <div class="resource-detail-extra">
            <el-tabs v-model="activeTab" class="detail-tabs">
              <el-tab-pane label="课时导学" name="article">
                <div class="detail-article-text">{{ selectedArticle?.contentText || selectedArticle?.summary || '暂无导学内容' }}</div>
              </el-tab-pane>
              <el-tab-pane label="学习评论" name="comments">
                <section class="detail-comment-card">
                  <div class="comment-input-area">
                    <div v-if="replyTarget" class="replying-banner">
                      <div class="replying-banner__text">
                        <i class="ri-chat-quote-line"></i>
                        正在回复 <strong>{{ replyTarget.userName || `用户${replyTarget.userId}` }}</strong>
                      </div>
                      <button type="button" class="replying-banner__cancel" @click="clearReplyTarget">取消回复</button>
                    </div>
                    <div class="comment-editor-toolbar">
                      <el-popover
                        v-model:visible="emojiPickerVisible"
                        placement="bottom-start"
                        :width="360"
                        trigger="click"
                        popper-class="resource-emoji-popper"
                        @show="handleEmojiPickerShow"
                      >
                        <template #reference>
                          <button type="button" class="emoji-picker-trigger">
                            <i class="ri-emotion-happy-line"></i>
                            选择表情
                            <i class="ri-arrow-down-s-line"></i>
                          </button>
                        </template>

                        <div class="emoji-picker">
                          <div class="emoji-picker__tabs">
                            <button
                              v-for="group in emojiGroups"
                              :key="group.key"
                              type="button"
                              class="emoji-picker__tab"
                              :class="{ 'is-active': activeEmojiGroup === group.key }"
                              @click="activeEmojiGroup = group.key"
                            >
                              <i :class="group.icon"></i>
                              {{ group.label }}
                            </button>
                          </div>

                          <div v-if="visibleEmojis.length" class="emoji-picker__grid">
                            <button
                              v-for="emoji in visibleEmojis"
                              :key="`${activeEmojiGroup}-${emoji}`"
                              type="button"
                              class="emoji-picker__item"
                              @click="selectEmoji(emoji)"
                            >
                              {{ emoji }}
                            </button>
                          </div>

                          <div v-else class="emoji-picker__empty">
                            <i class="ri-history-line"></i>
                            最近使用的表情会显示在这里
                          </div>
                        </div>
                      </el-popover>

                      <span class="comment-editor-toolbar__tip">
                        <i class="ri-chat-smile-2-line"></i>
                        选择表情后会自动插入到评论末尾
                      </span>
                    </div>
                    <el-input 
                      ref="commentInputRef"
                      v-model="commentText" 
                      type="textarea" 
                      :rows="3" 
                      placeholder="记录你的学习感受、问题或补充理解..." 
                      class="comment-textarea"
                    />
                    <div class="detail-comment-actions">
                      <div class="comment-actions-left">
                        <el-rate v-model="ratingScore" />
                      </div>
                      <el-button type="primary" class="submit-comment-btn" @click="submitComment">
                        {{ replyTarget ? '发布回复' : '发布评论' }}
                      </el-button>
                    </div>
                  </div>
                  
                  <div class="detail-comment-list">
                    <div class="comment-list-header">
                      <h3>全部评论 ({{ commentTotalCount }})</h3>
                    </div>
                    <ResourceCommentThread
                      v-if="comments.length"
                      :comments="comments"
                      @reply="beginReply"
                      @like="toggleLike"
                    />
                    <el-empty v-if="!comments.length" description="还没有评论，来写第一条吧" :image-size="72" />
                  </div>
                </section>
              </el-tab-pane>
            </el-tabs>
          </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ResourceCommentThread from '@/components/resource/ResourceCommentThread.vue'
import ResourcePdfPreview from '@/components/resource/ResourcePdfPreview.vue'
import ResourceVideoPlayer from '@/components/resource/ResourceVideoPlayer.vue'
import { ElMessage } from '@/utils/feedback'
import {
  addResourceComment,
  favoriteResource,
  getFavoriteResourceStatus,
  getOfficePreview,
  getOfficePreviewStatus,
  getResourceDetail,
  likeResourceComment,
  listResourceAsset,
  listResourceComment,
  shareResource,
  unfavoriteResource,
} from '@/api/portal'
import { warmPdfDocument } from '@/utils/pdfPreview'
import { isOfficeFile, isPdfFile, resolveResourceUrl } from '@/utils/resource'

const route = useRoute()
const router = useRouter()

type ResourceCommentItem = {
  commentId: number
  parentId?: number
  rootId?: number
  userId?: number | string
  userName?: string
  liked?: boolean
  likeCount?: number
  replies?: ResourceCommentItem[]
}

type EmojiGroup = {
  key: string
  label: string
  icon: string
  emojis: string[]
}

const selectedArticle = ref<any>(null)
const assetList = ref<any[]>([])
const activeAsset = ref<any>(null)
const activeAssetType = ref('video_course')
const activeTab = ref('article')
const comments = ref<ResourceCommentItem[]>([])
const commentText = ref('')
const commentInputRef = ref<any | null>(null)
const ratingScore = ref(5)
const favoriteLoading = ref(false)
const isFavorited = ref(false)
const shareLoading = ref(false)
const emojiPickerVisible = ref(false)
const activeEmojiGroup = ref('recent')
const recentEmojis = ref<string[]>([])
const replyTarget = ref<ResourceCommentItem | null>(null)
const officePreviewPdfUrl = ref('')
const officePreviewLoading = ref(false)
const officePreviewError = ref('')
let officePreviewPollTimer: ReturnType<typeof setTimeout> | null = null
let officePreviewRequestId = 0
let assetWarmupTimer: ReturnType<typeof setTimeout> | null = null
const officePreviewCache = new Map<string, string>()
const officePreviewTaskCache = new Map<string, Promise<string>>()
const emojiGroups: EmojiGroup[] = [
  { key: 'recent', label: '最近', icon: 'ri-history-line', emojis: [] },
  { key: 'study', label: '学习', icon: 'ri-book-open-line', emojis: ['📘', '📚', '📝', '✏️', '💡', '🧠', '🤔', '✅', '📌', '🎯'] },
  { key: 'mood', label: '互动', icon: 'ri-chat-smile-2-line', emojis: ['😀', '😁', '😊', '🥳', '👏', '👍', '🙌', '🤝', '🙏', '❤️'] },
  { key: 'energy', label: '氛围', icon: 'ri-fire-line', emojis: ['🎉', '🔥', '💯', '🚀', '🌟', '🎊', '🏆', '⚡', '🍀', '🎈'] },
]

const assetTypeOptions = [
  { label: '课程包', value: 'video_course' },
  { label: '套卷', value: 'after_class_exercise' },
  { label: '课件', value: 'courseware' },
  { label: '教学设计', value: 'teaching_design' },
  { label: '任务单', value: 'task_sheet' },
]

function getAssetIcon(type: string) {
  const map: Record<string, string> = {
    video_course: 'ri-play-circle-line',
    after_class_exercise: 'ri-file-paper-2-line',
    courseware: 'ri-slideshow-3-line',
    teaching_design: 'ri-quill-pen-line',
    task_sheet: 'ri-file-list-3-line',
  }
  return map[type] || 'ri-file-3-line'
}

const currentCourseSubtitle = computed(() => '课程资源目录与课时内容')
const activeAssetUrl = computed(() => resolveResourceUrl(activeAsset.value?.fileUrl))
const availableDetailTypes = computed(() => assetTypeOptions.filter((item) => assetList.value.some((asset: any) => asset.assetType === item.value)))
const currentAssetTypeLabel = computed(() => assetTypeOptions.find((item) => item.value === activeAssetType.value)?.label || '资源预览')
const commentTotalCount = computed(() => countComments(comments.value))
const visibleEmojis = computed(() => {
  if (activeEmojiGroup.value === 'recent') {
    return recentEmojis.value
  }
  return emojiGroups.find((group) => group.key === activeEmojiGroup.value)?.emojis || []
})
const shareActionLabel = computed(() => {
  if (typeof navigator !== 'undefined' && typeof navigator.share === 'function') {
    return '分享资源'
  }
  return '复制链接'
})

function displayCount(value?: number) {
  const count = Number(value || 0)
  if (count >= 10000) return `${(count / 10000).toFixed(1)}万`
  return String(count)
}

function formatRating(value?: number) {
  return Number(value || 0).toFixed(1)
}

function isVideoAsset(asset: any) {
  return asset?.assetType === 'video_course'
}

function isPdfAsset(asset: any) {
  return isPdfFile(asset?.fileUrl)
}

function isOfficeAsset(asset: any) {
  return isOfficeFile(asset?.fileUrl)
}

function queueAssetPreviewWarmup() {
  if (assetWarmupTimer) {
    clearTimeout(assetWarmupTimer)
    assetWarmupTimer = null
  }

  assetWarmupTimer = setTimeout(() => {
    const pdfUrls = Array.from(new Set(
      assetList.value
        .filter((item: any) => isPdfAsset(item) && item.fileUrl)
        .map((item: any) => resolveResourceUrl(item.fileUrl))
        .filter(Boolean),
    ))

    pdfUrls.forEach((url) => warmPdfDocument(url))
    assetWarmupTimer = null
  }, 180)
}

async function resolveOfficePreviewPdf(fileUrl: string) {
  const cached = officePreviewCache.get(fileUrl)
  if (cached) return cached

  const existingTask = officePreviewTaskCache.get(fileUrl)
  if (existingTask) return existingTask

  const previewTask = (async () => {
    const res = await getOfficePreview(fileUrl)
    const status = res.status || res.data?.status
    const taskId = res.taskId || res.data?.taskId
    const fileName = res.fileName || res.data?.fileName

    if (status === 'SUCCESS' && fileName) {
      const pdfUrl = resolveResourceUrl(fileName)
      officePreviewCache.set(fileUrl, pdfUrl)
      warmPdfDocument(pdfUrl)
      return pdfUrl
    }

    if (!taskId) {
      throw new Error(res.message || res.data?.message || '预览任务提交失败')
    }

    const generatedFileName = await pollOfficePreview(taskId)
    const pdfUrl = resolveResourceUrl(generatedFileName)
    officePreviewCache.set(fileUrl, pdfUrl)
    warmPdfDocument(pdfUrl)
    return pdfUrl
  })()

  officePreviewTaskCache.set(fileUrl, previewTask)

  try {
    return await previewTask
  } finally {
    officePreviewTaskCache.delete(fileUrl)
  }
}

async function loadOfficePreview() {
  officePreviewRequestId += 1
  const requestId = officePreviewRequestId
  const fileUrl = String(activeAsset.value?.fileUrl || '')
  officePreviewError.value = ''
  if (officePreviewPollTimer) {
    clearTimeout(officePreviewPollTimer)
    officePreviewPollTimer = null
  }

  if (!fileUrl || !isOfficeAsset(activeAsset.value)) {
    officePreviewPdfUrl.value = ''
    officePreviewLoading.value = false
    return
  }

  const cachedPreviewUrl = officePreviewCache.get(fileUrl)
  if (cachedPreviewUrl) {
    officePreviewPdfUrl.value = cachedPreviewUrl
    officePreviewLoading.value = false
    warmPdfDocument(cachedPreviewUrl)
    return
  }

  officePreviewPdfUrl.value = ''
  officePreviewLoading.value = true

  try {
    const previewPdfUrl = await resolveOfficePreviewPdf(fileUrl)
    if (requestId !== officePreviewRequestId) return
    officePreviewPdfUrl.value = previewPdfUrl
  } catch (error: any) {
    if (requestId !== officePreviewRequestId) return
    officePreviewError.value = error?.message || '文档预览生成失败'
  } finally {
    if (requestId === officePreviewRequestId) {
      officePreviewLoading.value = false
    }
  }
}

async function pollOfficePreview(taskId: string) {
  return new Promise<string>((resolve, reject) => {
    let attempts = 0
    const maxAttempts = 60
    const check = async () => {
      attempts += 1
      try {
        const res = await getOfficePreviewStatus(taskId)
        const status = res.status || res.data?.status
        const fileName = res.fileName || res.data?.fileName
        const message = res.message || res.data?.message || ''
        if (status === 'SUCCESS' && fileName) {
          if (officePreviewPollTimer) {
            clearTimeout(officePreviewPollTimer)
            officePreviewPollTimer = null
          }
          resolve(fileName)
          return
        }
        if (status === 'FAILED') {
          if (officePreviewPollTimer) {
            clearTimeout(officePreviewPollTimer)
            officePreviewPollTimer = null
          }
          reject(new Error(message || '文档预览生成失败'))
          return
        }
        if (attempts >= maxAttempts) {
          if (officePreviewPollTimer) {
            clearTimeout(officePreviewPollTimer)
            officePreviewPollTimer = null
          }
          reject(new Error('文档预览生成超时，请稍后重试'))
          return
        }
        officePreviewPollTimer = setTimeout(() => { void check() }, 1500)
      } catch (error: any) {
        if (officePreviewPollTimer) {
          clearTimeout(officePreviewPollTimer)
          officePreviewPollTimer = null
        }
        reject(error)
      }
    }
    void check()
  })
}

async function loadDetail() {
  const resourceId = Number(route.params.resourceId || 0)
  if (!resourceId) return
  replyTarget.value = null
  commentText.value = ''
  const res = await getResourceDetail(resourceId)
  selectedArticle.value = res.data || {}
  const favoriteRes = await getFavoriteResourceStatus(resourceId)
  isFavorited.value = Boolean(favoriteRes.data === true || favoriteRes.favorited === true)
  ratingScore.value = Math.max(1, Math.round(Number(selectedArticle.value?.avgRating || 5)))
  await Promise.all([loadAssets(), loadComments()])
}

async function loadAssets() {
  if (!selectedArticle.value?.resourceId) {
    assetList.value = []
    activeAsset.value = null
    return
  }
  const res = await listResourceAsset({ resourceId: selectedArticle.value.resourceId, status: '0' })
  assetList.value = res.data || []
  queueAssetPreviewWarmup()
  const availableTypes = Array.from(new Set(assetList.value.map((item: any) => item.assetType)))
  const preferredType = String(route.query.assetType || '')
  if (preferredType && availableTypes.includes(preferredType)) {
    activeAssetType.value = preferredType
  } else {
    activeAssetType.value = availableTypes.includes(activeAssetType.value) ? activeAssetType.value : (availableTypes[0] || 'video_course')
  }
  switchAssetType(activeAssetType.value)
}

function switchAssetType(type: string) {
  activeAssetType.value = type
  activeAsset.value = assetList.value.find((item: any) => item.assetType === type) || null
}

async function loadComments() {
  if (!selectedArticle.value?.resourceId) {
    comments.value = []
    return
  }
  const res = await listResourceComment(selectedArticle.value.resourceId)
  comments.value = res.data || []
  if (replyTarget.value && !containsComment(comments.value, replyTarget.value.commentId)) {
    replyTarget.value = null
  }
}

function countComments(items: ResourceCommentItem[] = []): number {
  return items.reduce((total, item) => total + 1 + countComments(item.replies ?? []), 0)
}

function containsComment(items: ResourceCommentItem[] = [], targetId?: number): boolean {
  if (targetId == null) return false
  return items.some((item) => item.commentId === targetId || containsComment(item.replies ?? [], targetId))
}

function backToCatalog() {
  void router.push({
    path: '/student/resources',
    query: {
      courseId: String(route.query.courseId || ''),
      nodeId: String(route.query.nodeId || ''),
    },
  })
}

function openAsset() {
  if (!activeAsset.value?.fileUrl) {
    ElMessage.warning('当前资源还没有配置文件地址')
    return
  }
  window.open(resolveResourceUrl(activeAsset.value.fileUrl), '_blank')
}

function handleEmojiPickerShow() {
  if (!recentEmojis.value.length && activeEmojiGroup.value === 'recent') {
    activeEmojiGroup.value = 'study'
  }
}

async function submitComment() {
  if (!selectedArticle.value?.resourceId) return
  if (!commentText.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  await addResourceComment({
    resourceId: selectedArticle.value.resourceId,
    courseId: selectedArticle.value.courseId,
    nodeId: selectedArticle.value.nodeId,
    content: commentText.value.trim(),
    parentId: replyTarget.value?.commentId || 0,
    rootId: replyTarget.value?.rootId || replyTarget.value?.commentId || 0,
  })
  commentText.value = ''
  replyTarget.value = null
  ElMessage.success('评论已发布')
  await loadComments()
}

function appendEmoji(emoji: string) {
  commentText.value = `${commentText.value}${emoji}`
}

function rememberEmoji(emoji: string) {
  recentEmojis.value = [emoji, ...recentEmojis.value.filter((item) => item !== emoji)].slice(0, 8)
}

function selectEmoji(emoji: string) {
  appendEmoji(emoji)
  rememberEmoji(emoji)
  emojiPickerVisible.value = false
  void nextTick(() => {
    commentInputRef.value?.focus?.()
  })
}

async function submitFavorite() {
  if (!selectedArticle.value?.resourceId) return
  favoriteLoading.value = true
  try {
    if (isFavorited.value) {
      await unfavoriteResource(selectedArticle.value.resourceId)
      selectedArticle.value.favoriteCount = Math.max(0, Number(selectedArticle.value.favoriteCount || 0) - 1)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
      return
    }
    const res = await favoriteResource(selectedArticle.value.resourceId)
    if (res.favorited === true) {
      isFavorited.value = true
      ElMessage.warning('该资源已在你的收藏列表中')
      return
    }
    selectedArticle.value.favoriteCount = Number(selectedArticle.value.favoriteCount || 0) + 1
    isFavorited.value = true
    ElMessage.success('收藏成功')
  } finally {
    favoriteLoading.value = false
  }
}

function beginReply(item: ResourceCommentItem) {
  replyTarget.value = item
  activeTab.value = 'comments'
}

function clearReplyTarget() {
  replyTarget.value = null
}

async function toggleLike(item: ResourceCommentItem) {
  await likeResourceComment(item.commentId)
  item.liked = !item.liked
  item.likeCount = Math.max(0, Number(item.likeCount || 0) + (item.liked ? 1 : -1))
}

async function copyShareLink(text: string) {
  if (typeof navigator !== 'undefined' && navigator.clipboard?.writeText) {
    await navigator.clipboard.writeText(text)
    return
  }

  const textarea = document.createElement('textarea')
  textarea.value = text
  textarea.setAttribute('readonly', 'readonly')
  textarea.style.position = 'fixed'
  textarea.style.left = '-9999px'
  document.body.appendChild(textarea)
  textarea.select()
  document.execCommand('copy')
  document.body.removeChild(textarea)
}

async function submitShare() {
  if (!selectedArticle.value?.resourceId) return
  shareLoading.value = true

  const shareTitle = selectedArticle.value?.resourceName || '资源分享'
  const shareText = `${shareTitle} · ${currentCourseSubtitle.value}`
  const shareUrl = typeof window !== 'undefined' ? window.location.href : ''

  try {
    let completed = false
    let copied = false

    if (typeof navigator !== 'undefined' && typeof navigator.share === 'function') {
      try {
        await navigator.share({
          title: shareTitle,
          text: shareText,
          url: shareUrl,
        })
        completed = true
      } catch (error: any) {
        if (error?.name === 'AbortError') return
      }
    }

    if (!completed) {
      await copyShareLink(shareUrl)
      completed = true
      copied = true
    }

    if (!completed) return

    try {
      await shareResource(selectedArticle.value.resourceId)
    } catch (error) {
      // Sharing itself is already complete, so share count failure should not block feedback.
    }

    ElMessage.success(copied ? '资源链接已复制，快去分享吧' : '分享操作已完成')
  } catch (error: any) {
    ElMessage.error(error?.message || '分享失败，请稍后重试')
  } finally {
    shareLoading.value = false
  }
}

watch(
  () => activeAsset.value?.fileUrl,
  () => {
    void loadOfficePreview()
  },
  { immediate: true },
)

watch(
  () => route.params.resourceId,
  () => {
    void loadDetail()
  },
  { immediate: true },
)

onMounted(loadDetail)
</script>

<style scoped>
.resource-detail-page {
  --resource-primary: #0ea5e9;
  --resource-primary-deep: #0369a1;
  --resource-accent: #f97316;
  --resource-ink: #0f172a;
  --resource-muted: #64748b;
  --resource-border: rgba(186, 230, 253, 0.9);
  --resource-surface: rgba(255, 255, 255, 0.92);
  padding: 0;
  background: transparent;
}

.resource-detail-shell {
  display: flex;
  flex-direction: column;
}

.detail-inner {
  padding: 24px 0;
  width: 100%;
}

.resource-detail-header {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.45fr) minmax(320px, 0.85fr);
  gap: 28px;
  align-items: stretch;
  padding: 30px 32px;
  background: #fff;
  border: 1px solid var(--resource-border);
  border-radius: 16px;
  box-shadow: 0 18px 42px rgba(2, 132, 199, 0.08);
  margin-bottom: 24px;
  overflow: hidden;
}

.resource-detail-header::before,
.resource-detail-header::after {
  display: none;
}

.resource-detail-back {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: none;
  background: rgba(240, 249, 255, 0.9);
  color: var(--resource-primary-deep);
  padding: 9px 16px;
  border-radius: 10px;
  cursor: pointer;
  margin-bottom: 16px;
  font-size: 13px;
  font-weight: 600;
  transition: background-color 0.2s, color 0.2s, transform 0.2s;
}

.resource-detail-back:hover {
  background: rgba(224, 242, 254, 1);
  color: var(--resource-ink);
  transform: translateY(-1px);
}

.resource-detail-header__main,
.resource-detail-header__aside {
  position: relative;
  z-index: 1;
}

.resource-detail-header__eyebrow {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 14px;
}

.header-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 14px;
  border-radius: 10px;
  background: rgba(236, 254, 255, 0.9);
  border: 1px solid rgba(165, 243, 252, 0.9);
  color: var(--resource-primary-deep);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.header-kicker i {
  font-size: 15px;
}

.header-kicker.is-accent {
  background: rgba(255, 247, 237, 0.94);
  border-color: rgba(254, 215, 170, 0.92);
  color: #c2410c;
}

.resource-detail-header__main h1 {
  margin: 0;
  font-size: 34px;
  color: var(--resource-ink);
  font-weight: 700;
  line-height: 1.2;
  letter-spacing: -0.02em;
  max-width: 16ch;
}

.resource-detail-header__subtitle {
  margin-top: 10px;
  color: var(--resource-muted);
  font-size: 15px;
  line-height: 1.7;
  max-width: 54ch;
}

.resource-detail-header__meta {
  margin-top: 22px;
  display: flex;
  align-items: stretch;
  gap: 12px;
  font-size: 13px;
  flex-wrap: wrap;
}

.meta-pill {
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 44px;
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(226, 232, 240, 0.95);
  border-radius: 10px;
  color: var(--resource-muted);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.75);
}

.meta-pill i {
  color: var(--resource-primary);
  font-size: 16px;
}

.detail-rating {
  color: #c2410c;
}

.resource-detail-header__aside {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.resource-summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.resource-summary-card {
  min-height: 102px;
  padding: 16px 18px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid rgba(191, 219, 254, 0.9);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.92),
    0 10px 22px rgba(14, 165, 233, 0.06);
}

.resource-summary-card__label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--resource-muted);
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 10px;
}

.resource-summary-card__label i {
  color: var(--resource-primary);
  font-size: 16px;
}

.resource-summary-card strong {
  display: block;
  color: var(--resource-ink);
  font-size: 28px;
  line-height: 1;
  letter-spacing: -0.03em;
}

.resource-detail-header__actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
  margin-top: auto;
}

.header-action {
  min-height: 48px;
  border: 1px solid rgba(191, 219, 254, 0.96);
  background: rgba(255, 255, 255, 0.9);
  color: var(--resource-primary-deep);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 10px 22px rgba(14, 165, 233, 0.06);
  transition: background-color 0.2s, border-color 0.2s, color 0.2s, transform 0.2s, box-shadow 0.2s;
}

.header-action i {
  font-size: 18px;
}

.header-action:hover {
  background: rgba(240, 249, 255, 1);
  border-color: rgba(125, 211, 252, 1);
  color: var(--resource-ink);
  transform: translateY(-1px);
}

.header-action:disabled {
  opacity: 0.72;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.header-action.is-favorited {
  border-color: rgba(249, 168, 212, 0.92);
  background: #fff1f7;
  color: #be185d;
  box-shadow: 0 14px 28px rgba(190, 24, 93, 0.16);
}

.header-action.is-favorited:hover {
  border-color: #f472b6;
  background: #ffe8f2;
  color: #9d174d;
}

.header-action.is-share {
  border-color: rgba(191, 219, 254, 0.96);
  color: var(--resource-primary-deep);
}

.resource-detail-body {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 24px;
  margin-bottom: 24px;
}

.resource-preview-panel {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 16px;
  border: 1px solid rgba(186, 230, 253, 0.86);
  box-shadow: 0 18px 40px rgba(14, 116, 144, 0.08);
  overflow: hidden;
}

.resource-preview-panel__surface {
  height: 65vh;
  min-height: 480px;
  max-height: 800px;
  background: #0f172a;
}

.preview-stage {
  width: 100%;
  height: 100%;
  background: #0f172a;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
  display: block;
  background: #fff;
}

.preview-empty {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  color: #94a3b8;
  background: #1e293b;
}

.preview-empty i {
  font-size: 48px;
  color: #475569;
}

.preview-spin {
  animation: preview-spin 1s linear infinite;
}

.resource-preview-panel__footer {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  border-top: 1px solid rgba(226, 232, 240, 0.9);
}

.resource-preview-panel__copyright {
  color: #94a3b8;
  line-height: 1.6;
  font-size: 13px;
}

.resource-preview-panel__actions :deep(.el-button) {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border-radius: 10px;
  padding-inline: 16px;
}

.resource-asset-sidebar {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  border: 1px solid rgba(186, 230, 253, 0.86);
  box-shadow: 0 18px 38px rgba(14, 116, 144, 0.08);
  height: fit-content;
}

.resource-asset-sidebar__title {
  font-size: 18px;
  font-weight: 700;
  color: var(--resource-ink);
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 14px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.9);
}

.resource-asset-sidebar__list {
  display: grid;
  gap: 12px;
}

.resource-asset-type {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid transparent;
  background: rgba(255, 255, 255, 0.78);
  padding: 12px 16px;
  cursor: pointer;
  color: var(--resource-muted);
  border-radius: 10px;
  font-size: 15px;
  transition: background-color 0.2s, border-color 0.2s, color 0.2s, transform 0.2s, box-shadow 0.2s;
}

.resource-asset-type:hover {
  background: rgba(240, 249, 255, 0.96);
  border-color: rgba(186, 230, 253, 0.9);
  transform: translateY(-1px);
}

.resource-asset-type.is-active {
  background: #eff6ff;
  color: #0369a1;
  border-color: rgba(125, 211, 252, 0.96);
  font-weight: 700;
  box-shadow: 0 12px 24px rgba(14, 165, 233, 0.08);
}

.resource-asset-type__left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.active-icon {
  color: #2563eb;
  font-size: 16px;
}

.resource-asset-type__count {
  min-width: 26px;
  text-align: center;
  color: #94a3b8;
}

.resource-detail-extra {
  background: #fff;
  border-radius: 16px;
  padding: 0 32px;
  border: 1px solid rgba(186, 230, 253, 0.82);
  box-shadow: 0 18px 38px rgba(14, 116, 144, 0.08);
  margin-bottom: 40px;
}

.detail-tabs {
  margin-top: 0;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #f1f5f9;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  color: #64748b;
  padding: 0 24px !important;
  height: 60px;
  line-height: 60px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-tabs__item:hover) {
  color: #2563eb;
}

:deep(.el-tabs__item.is-active) {
  color: #0f172a;
  font-weight: 600;
  font-size: 17px;
}

:deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px 3px 0 0;
  background-color: #2563eb;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.detail-article-text {
  white-space: pre-wrap;
  line-height: 1.8;
  color: #334155;
  font-size: 15px;
  padding: 24px 0;
}

.detail-comment-card {
  padding: 24px 0;
}

.comment-input-area {
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 32px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s;
}

.comment-input-area:focus-within {
  background: #fff;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.replying-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  margin-bottom: 12px;
  border-radius: 10px;
  background: #f8fbff;
  border: 1px solid #bfdbfe;
}

.replying-banner__text {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #1d4ed8;
  font-size: 14px;
}

.replying-banner__cancel {
  border: none;
  background: transparent;
  color: #64748b;
  cursor: pointer;
  padding: 0;
  font-size: 13px;
}

.replying-banner__cancel:hover {
  color: #0f172a;
}

.comment-editor-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.emoji-picker-trigger {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 38px;
  padding: 8px 14px;
  border: 1px solid #dbe5f0;
  background: #fff;
  border-radius: 10px;
  cursor: pointer;
  color: #334155;
  font-size: 14px;
  font-weight: 600;
  transition: background-color 0.2s ease, border-color 0.2s ease, color 0.2s ease;
}

.emoji-picker-trigger:hover {
  background: #eef4ff;
  border-color: #bfdbfe;
}

.emoji-picker-trigger i:first-child {
  color: #2563eb;
  font-size: 18px;
}

.comment-editor-toolbar__tip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 13px;
}

.comment-editor-toolbar__tip i {
  color: #94a3b8;
}

:deep(.resource-emoji-popper) {
  padding: 12px !important;
  border-radius: 12px !important;
  border: 1px solid #dbe5f0 !important;
  box-shadow: 0 14px 28px rgba(15, 23, 42, 0.12) !important;
}

.emoji-picker {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.emoji-picker__tabs {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px;
}

.emoji-picker__tab {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  min-height: 36px;
  border: 1px solid #e2e8f0;
  background: #fff;
  border-radius: 10px;
  color: #64748b;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: background-color 0.2s ease, border-color 0.2s ease, color 0.2s ease;
}

.emoji-picker__tab:hover,
.emoji-picker__tab.is-active {
  background: #eff6ff;
  border-color: #bfdbfe;
  color: #1d4ed8;
}

.emoji-picker__grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 8px;
  max-height: 216px;
  overflow-y: auto;
  padding-right: 2px;
}

.emoji-picker__item {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 42px;
  border: 1px solid #e2e8f0;
  background: #fff;
  border-radius: 10px;
  cursor: pointer;
  font-size: 22px;
  line-height: 1;
  transition: background-color 0.2s ease, border-color 0.2s ease, transform 0.2s ease;
}

.emoji-picker__item:hover {
  background: #fff7ed;
  border-color: #fdba74;
  transform: translateY(-1px);
}

.emoji-picker__empty {
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #94a3b8;
  font-size: 13px;
}

.emoji-picker__empty i {
  font-size: 20px;
}

:deep(.comment-textarea .el-textarea__inner) {
  background: transparent;
  border: none;
  box-shadow: none;
  padding: 0;
  font-size: 15px;
  color: #334155;
  resize: none;
}

:deep(.comment-textarea .el-textarea__inner:focus) {
  box-shadow: none;
}

:deep(.comment-textarea .el-textarea__inner::placeholder) {
  color: #94a3b8;
}

.detail-comment-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #e2e8f0;
}

.comment-actions-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.submit-comment-btn {
  padding: 8px 24px;
  border-radius: 10px;
  font-weight: 500;
}

.comment-list-header {
  margin-bottom: 24px;
}

.comment-list-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.detail-comment-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

:deep(.comment-thread) {
  display: flex;
  flex-direction: column;
  gap: 0;
}

:deep(.comment-thread__item) {
  display: flex;
  gap: 16px;
  padding: 24px 0;
  border-bottom: 1px solid #f1f5f9;
}

:deep(.comment-thread__item.is-reply) {
  padding: 16px 0 0;
  border-bottom: none;
}

:deep(.comment-thread__avatar) {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background: #e2e8f0;
  flex-shrink: 0;
}

:deep(.comment-thread__item.is-reply .comment-thread__avatar) {
  width: 32px;
  height: 32px;
}

:deep(.comment-thread__avatar img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

:deep(.avatar-fallback) {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: #2563eb;
  font-weight: 700;
  font-size: 16px;
}

:deep(.comment-thread__item.is-reply .avatar-fallback) {
  font-size: 13px;
}

:deep(.comment-thread__content) {
  flex: 1;
  min-width: 0;
}

:deep(.comment-thread__head) {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 8px;
}

:deep(.comment-thread__user) {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

:deep(.comment-thread__user strong) {
  color: #1e293b;
  font-size: 15px;
  font-weight: 600;
}

:deep(.comment-time) {
  color: #94a3b8;
  font-size: 13px;
}

:deep(.reply-target) {
  font-size: 13px;
  color: #64748b;
}

:deep(.reply-target span) {
  color: #2563eb;
  font-weight: 600;
}

:deep(.comment-actions) {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

:deep(.comment-thread__item:hover .comment-actions) {
  opacity: 1;
}

:deep(.comment-action-btn) {
  color: #64748b;
  padding: 4px 8px;
}

:deep(.comment-action-btn:hover) {
  color: #2563eb;
  background: #eff6ff;
}

:deep(.comment-action-btn.is-active) {
  color: #2563eb;
  background: #eff6ff;
}

:deep(.comment-text) {
  margin: 0;
  line-height: 1.7;
  color: #334155;
  font-size: 15px;
  white-space: pre-wrap;
  word-break: break-word;
}

:deep(.comment-replies) {
  margin-top: 14px;
  padding: 16px 18px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
}

@keyframes preview-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@media (max-width: 1400px) {
  .resource-detail-body {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1180px) {
  .resource-detail-header {
    grid-template-columns: 1fr;
  }

  .resource-detail-header__actions {
    justify-content: flex-start;
  }
}

@media (max-width: 960px) {
  .resource-preview-panel__footer,
  .detail-comment-actions {
    flex-direction: column;
    align-items: flex-start;
  }

  .resource-detail-header {
    padding: 24px 20px;
    gap: 22px;
  }

  .resource-detail-header__main h1 {
    font-size: 28px;
  }

  .resource-summary-grid {
    grid-template-columns: 1fr;
  }

  .resource-detail-extra {
    padding: 0 20px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .resource-detail-back,
  .header-action,
  .resource-asset-type,
  .preview-spin {
    animation: none !important;
    transition: none !important;
  }
}
</style>
