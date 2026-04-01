<template>
  <div class="portal-page resource-center-page">
    <section class="resource-catalog-shell">
      <aside class="resource-catalog-sidebar portal-card">
        <el-scrollbar>
          <div class="sidebar-inner">
            <div class="catalog-course-crumb">
              <i class="ri-map-pin-2-line"></i>
              <span>{{ currentCourseBreadcrumb }}</span>
            </div>

            <div class="catalog-course-card">
              <div v-if="courseLoading" class="catalog-course-card__skeleton">
                <div class="catalog-course-card__skeleton-line is-badge"></div>
                <div class="catalog-course-card__skeleton-line is-title"></div>
                <div class="catalog-course-card__skeleton-line"></div>
                <div class="catalog-course-card__skeleton-stats">
                  <span></span>
                  <span></span>
                </div>
              </div>
              <div v-else class="catalog-course-card__meta">
                <div class="catalog-course-card__status">
                  <span class="course-status-badge">
                    <i class="ri-stack-line"></i>
                    {{ currentAssetTypeLabel }}
                  </span>
                </div>
                <h2>{{ currentCourseLabel || '请选择课程' }}</h2>
                <p>{{ currentCourseSubtitle }}</p>
                <div class="catalog-course-card__stats">
                  <span><i class="ri-node-tree"></i> {{ treeFlatCount }} 个目录</span>
                  <span><i class="ri-file-list-3-line"></i> {{ resultCount }} 条资源</span>
                </div>
              </div>
            </div>

            <div class="catalog-course-toolbar">
              <div class="catalog-course-toolbar__label">
                <i class="ri-book-marked-line"></i>
                切换课程
              </div>
              <el-select v-model="courseId" filterable clearable placeholder="请选择课程" style="width: 100%" @change="handleCourseChange">
                <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </div>

            <div class="catalog-tree-panel">
              <div class="catalog-tree-panel__title">
                <div class="catalog-tree-panel__title-main">
                  <strong>课程目录</strong>
                  <p>按课程目录逐层浏览，右侧会自动聚合当前节点下的资源。</p>
                </div>
                <span>{{ treeFlatCount }} 项</span>
              </div>
              <div v-if="treeLoading" class="catalog-tree-skeleton">
                <div v-for="index in 6" :key="`tree-skeleton-${index}`" class="catalog-tree-skeleton__row"></div>
              </div>
              <el-empty v-if="!courseId" description="请选择课程" :image-size="72" />
              <el-empty v-else-if="!treeLoading && !treeData.length" description="当前课程还没有目录内容" :image-size="72" />
              <el-tree
                v-else-if="!treeLoading"
                node-key="nodeId"
                :data="treeData"
                :props="{ label: 'nodeName', children: 'children' }"
                default-expand-all
                highlight-current
                @node-click="handleNodeSelect"
              >
                <template #default="{ data, node }">
                  <div class="catalog-tree-node">
                    <span class="catalog-tree-node__main">
                      <button
                        v-if="data.children && data.children.length"
                        type="button"
                        class="catalog-tree-node__toggle"
                        @click.stop="toggleTreeNode(node)"
                      >
                        <i class="ri-arrow-right-s-line" :class="{ 'is-expanded': node.expanded }"></i>
                      </button>
                      <i :class="(data.children && data.children.length) ? 'ri-folder-5-line' : 'ri-file-text-line'" class="catalog-tree-node__icon"></i>
                      <span class="catalog-tree-node__label">{{ data.nodeName }}</span>
                    </span>
                    <span class="catalog-tree-node__count">{{ data.articleCount || 0 }}</span>
                  </div>
                </template>
              </el-tree>
            </div>
          </div>
        </el-scrollbar>
      </aside>

      <section class="resource-catalog-main portal-card">
        <el-scrollbar>
          <div class="main-inner">
            <div class="catalog-main-toolbar">
              <div class="catalog-main-toolbar__head">
                <div class="catalog-main-toolbar__title">
                  <span class="catalog-main-toolbar__eyebrow">资源浏览</span>
                  <h2>{{ selectedNode ? selectedNodeLabel : (currentCourseLabel || '资源中心') }}</h2>
                  <p>{{ selectedNode ? '已按当前目录聚合课时资源，可继续按类型、制作单位和关键词细化。' : '先从左侧选择课程和目录节点，再查看具体资源内容。' }}</p>
                </div>
                <div class="catalog-main-toolbar__stats">
                  <div class="catalog-main-toolbar__stat">
                    <span>当前课程</span>
                    <strong>{{ currentCourseLabel || '未选择' }}</strong>
                  </div>
                  <div class="catalog-main-toolbar__stat">
                    <span>目录节点</span>
                    <strong>{{ treeFlatCount }}</strong>
                  </div>
                  <div class="catalog-main-toolbar__stat">
                    <span>筛选结果</span>
                    <strong>{{ resultCount }}</strong>
                  </div>
                </div>
              </div>

              <div class="catalog-main-tabs">
                <button
                  v-for="item in assetTypeOptions"
                  :key="item.value"
                  type="button"
                  class="catalog-main-tab"
                  :class="{ 'is-active': catalogAssetType === item.value }"
                  @click="catalogAssetType = item.value; filterArticles()"
                >
                  <i :class="getAssetTypeIcon(item.value)"></i>
                  <span>{{ item.label }}</span>
                  <em>{{ assetTypeCounts[item.value] || 0 }}</em>
                </button>
              </div>

              <div class="catalog-main-filters">
                <el-input v-model="keyword" class="catalog-filter-control" placeholder="搜索资源标题、摘要" clearable style="width: 240px" @input="filterArticles">
                  <template #prefix><i class="ri-search-line"></i></template>
                </el-input>
                <el-select v-model="publisherFilter" class="catalog-filter-control" clearable placeholder="全部制作单位" style="width: 180px" @change="filterArticles">
                  <el-option v-for="item in publisherOptions" :key="item" :label="item" :value="item" />
                </el-select>
                <el-select v-model="sortMode" class="catalog-filter-control" style="width: 152px" @change="filterArticles">
                  <el-option v-for="item in sortOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
                <button v-if="hasActiveFilters" type="button" class="catalog-reset-btn" @click="resetFilters">
                  <i class="ri-refresh-line"></i>
                  重置筛选
                </button>
              </div>
            </div>

            <div v-if="activeFilterTags.length" class="catalog-active-filters">
              <span class="catalog-active-filters__label">
                <i class="ri-equalizer-line"></i>
                当前筛选
              </span>
              <span v-for="item in activeFilterTags" :key="item.label" class="catalog-active-filters__chip">
                {{ item.label }}
              </span>
            </div>

            <div v-if="selectedNode" class="catalog-results-bar">
              <div class="catalog-results-bar__info">
                <i class="ri-node-tree"></i>
                <span>当前目录：{{ selectedNodeLabel }}</span>
                <strong>{{ resultCount }} 条资源</strong>
              </div>
              <div class="catalog-results-bar__hint">
                <i class="ri-cursor-line"></i>
                点击资源卡可进入详情页查看课时文件与评论
              </div>
            </div>

            <div class="catalog-main-list">
              <div v-if="articlesLoading" class="catalog-main-skeleton">
                <article v-for="index in 4" :key="`article-skeleton-${index}`" class="catalog-main-skeleton__card">
                  <div class="catalog-main-skeleton__cover"></div>
                  <div class="catalog-main-skeleton__content">
                    <div class="catalog-main-skeleton__line is-short"></div>
                    <div class="catalog-main-skeleton__line is-title"></div>
                    <div class="catalog-main-skeleton__line"></div>
                    <div class="catalog-main-skeleton__line"></div>
                    <div class="catalog-main-skeleton__chips">
                      <span></span>
                      <span></span>
                    </div>
                  </div>
                </article>
              </div>
              <el-empty v-else-if="!selectedNode" description="请先从左侧选择目录节点" />
              <template v-else>
                <article
                  v-for="item in filteredArticles"
                  :key="item.resourceId"
                  class="catalog-lesson-card"
                  role="button"
                  tabindex="0"
                  @click="openDetail(item)"
                  @keyup.enter="openDetail(item)"
                  @keyup.space.prevent="openDetail(item)"
                >
                  <div class="catalog-lesson-card__cover">
                    <img v-if="item.coverUrl" :src="resolveResourceUrl(item.coverUrl)" :alt="item.resourceName">
                    <div v-else class="catalog-lesson-card__cover-fallback">
                      <i :class="getAssetTypeIcon(getArticleAssetType(item))"></i>
                      <span>{{ getAssetTypeLabel(getArticleAssetType(item)) }}</span>
                    </div>
                    <div class="catalog-lesson-card__cover-meta">
                      <span>{{ displayDate(item.createTime) }}</span>
                      <span>{{ item._assetTypes?.length || 0 }} 类附件</span>
                    </div>
                  </div>

                  <div class="catalog-lesson-card__content">
                    <div class="catalog-lesson-card__top">
                      <div class="catalog-lesson-card__title-block">
                        <div class="catalog-lesson-card__title-row">
                          <span class="catalog-lesson-card__tag">
                            <i :class="getAssetTypeIcon(getArticleAssetType(item))"></i>
                            {{ getAssetTypeLabel(getArticleAssetType(item)) }}
                          </span>
                          <div class="catalog-lesson-card__rating">
                            <i class="ri-star-fill"></i>
                            <span>{{ formatRating(item.avgRating) }} 分</span>
                          </div>
                        </div>
                        <h3 :title="item.resourceName">{{ item.resourceName }}</h3>
                        <p class="catalog-lesson-card__summary">{{ getArticleSummary(item) }}</p>
                      </div>
                      <div class="catalog-lesson-card__cta">
                        <span>查看详情</span>
                        <i class="ri-arrow-right-up-line"></i>
                      </div>
                    </div>

                    <div class="catalog-lesson-card__meta-row">
                      <span class="catalog-lesson-card__meta-pill">
                        <i class="ri-user-line"></i>
                        {{ item.authorName || '未填写作者' }}
                      </span>
                      <span class="catalog-lesson-card__meta-pill">
                        <i class="ri-building-line"></i>
                        {{ item.publisherName || '未填写制作单位' }}
                      </span>
                    </div>

                    <div class="catalog-lesson-card__stats">
                      <span><i class="ri-time-line"></i> {{ displayDate(item.createTime) }}</span>
                      <span><i class="ri-eye-line"></i> {{ displayCount(item.viewCount) }} 浏览</span>
                      <span><i class="ri-heart-3-line"></i> {{ displayCount(item.favoriteCount) }} 收藏</span>
                      <span><i class="ri-file-list-3-line"></i> {{ item._assetTypes?.length || 0 }} 类附件</span>
                    </div>
                  </div>
                </article>
                <el-empty v-if="selectedNode && !filteredArticles.length" description="当前目录下暂无符合条件的文章" />
              </template>
            </div>
          </div>
        </el-scrollbar>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchPortalCourseOptions, getResourceTree, listResource, listResourceAsset } from '@/api/portal'
import usePortalUserStore from '@/store/user'
import { resolveResourceUrl } from '@/utils/resource'

const userStore = usePortalUserStore()
const route = useRoute()
const router = useRouter()
const stateStorageKey = 'student-resource-center-state'

const courseOptions = ref<any[]>([])
const courseId = ref<number | undefined>()
const treeData = ref<any[]>([])
const selectedNode = ref<any>(null)
const articleList = ref<any[]>([])
const filteredArticles = ref<any[]>([])
const courseLoading = ref(false)
const treeLoading = ref(false)
const articlesLoading = ref(false)
const keyword = ref('')
const publisherFilter = ref('')
const sortMode = ref('smart')
const catalogAssetType = ref('video_course')
const restoringState = ref(false)

const sortOptions = [
  { label: '智能排序', value: 'smart' },
  { label: '评分优先', value: 'rating' },
  { label: '最新发布', value: 'latest' },
]

const assetTypeOptions = [
  { label: '课程包', value: 'video_course' },
  { label: '套卷', value: 'after_class_exercise' },
  { label: '课件', value: 'courseware' },
  { label: '教学设计', value: 'teaching_design' },
  { label: '任务单', value: 'task_sheet' },
]

const currentCourse = computed(() => courseOptions.value.find((item: any) => String(item.value) === String(courseId.value)))
const currentCourseLabel = computed(() => currentCourse.value?.label || '')
const currentCourseSubtitle = computed(() => '课程资源目录与课时内容')
const currentCourseBreadcrumb = computed(() => currentCourseLabel.value || '课程资源')
const publisherOptions = computed(() => Array.from(new Set(articleList.value.map((item: any) => item.publisherName).filter(Boolean))))
const currentAssetTypeLabel = computed(() => assetTypeOptions.find((item) => item.value === catalogAssetType.value)?.label || '资源')
const selectedNodeLabel = computed(() => selectedNode.value?.nodeName || '请选择左侧目录节点')
const resultCount = computed(() => filteredArticles.value.length)
const hasActiveFilters = computed(() => Boolean(keyword.value.trim() || publisherFilter.value || sortMode.value !== 'smart'))
const activeFilterTags = computed(() => {
  const tags: Array<{ label: string }> = []
  if (keyword.value.trim()) tags.push({ label: `关键词：${keyword.value.trim()}` })
  if (publisherFilter.value) tags.push({ label: `制作单位：${publisherFilter.value}` })
  if (sortMode.value !== 'smart') {
    const label = sortOptions.find((item) => item.value === sortMode.value)?.label || sortMode.value
    tags.push({ label: `排序：${label}` })
  }
  if (catalogAssetType.value) {
    tags.push({ label: `资源类型：${currentAssetTypeLabel.value}` })
  }
  return tags
})
const treeFlatCount = computed(() => {
  const walk = (items: any[]): number => items.reduce((sum, item) => sum + 1 + walk(item.children || []), 0)
  return walk(treeData.value)
})
const assetTypeCounts = computed(() => {
  const counts = assetTypeOptions.reduce((map, item) => {
    map[item.value] = 0
    return map
  }, {} as Record<string, number>)

  getFilteredArticlesData(true).forEach((item: any) => {
    item._assetTypes.forEach((type: string) => {
      if (type in counts) {
        counts[type] += 1
      }
    })
  })

  return counts
})

function displayDate(value?: string) {
  if (!value) return '--'
  return value.slice(0, 10)
}

function displayCount(value?: number) {
  const count = Number(value || 0)
  if (count >= 10000) return `${(count / 10000).toFixed(1)}万`
  return String(count)
}

function formatRating(value?: number) {
  return Number(value || 0).toFixed(1)
}

function getAssetTypeIcon(type?: string) {
  const map: Record<string, string> = {
    video_course: 'ri-play-circle-line',
    after_class_exercise: 'ri-file-paper-2-line',
    courseware: 'ri-slideshow-3-line',
    teaching_design: 'ri-quill-pen-line',
    task_sheet: 'ri-task-line',
  }
  return map[String(type || '')] || 'ri-file-3-line'
}

function getAssetTypeLabel(type?: string) {
  return assetTypeOptions.find((item) => item.value === type)?.label || '资源'
}

function toggleTreeNode(node: any) {
  node.expanded = !node.expanded
}

async function loadCourses() {
  restoringState.value = true
  courseLoading.value = true
  try {
    const storedState = readStoredState()
    courseOptions.value = await fetchPortalCourseOptions(userStore.user?.userId)
    const queryCourseId = Number(storedState.courseId || route.query.courseId || 0)
    if (queryCourseId) {
      courseId.value = queryCourseId
    } else if (!courseId.value && courseOptions.value.length) {
      courseId.value = courseOptions.value[0].value
    }
    keyword.value = String(storedState.keyword || route.query.keyword || '')
    publisherFilter.value = String(storedState.publisher || route.query.publisher || '')
    sortMode.value = String(storedState.sort || route.query.sort || 'smart')
    catalogAssetType.value = String(storedState.assetType || route.query.assetType || 'video_course')
    if (courseId.value) {
      await handleCourseChange()
    }
  } finally {
    courseLoading.value = false
    restoringState.value = false
  }
}

async function loadTree() {
  if (!courseId.value) {
    treeData.value = []
    return
  }
  treeLoading.value = true
  try {
    const res = await getResourceTree(courseId.value)
    treeData.value = res.data || []
  } finally {
    treeLoading.value = false
  }
}

async function handleCourseChange() {
  selectedNode.value = null
  await loadTree()
  const storedState = readStoredState()
  const preferredNodeId = Number(storedState.nodeId || route.query.nodeId || 0)
  const preferredNode = preferredNodeId ? findNodeById(treeData.value, preferredNodeId) : null
  const firstNode = preferredNode || findFirstNode(treeData.value)
  if (firstNode) {
    handleNodeSelect(firstNode)
    return
  }
  if (preferredNodeId) {
    saveState(undefined)
  }
}

function findFirstNode(nodes: any[]): any | null {
  for (const node of nodes) {
    if ((node.articleCount || 0) > 0) return node
    const child = findFirstNode(node.children || [])
    if (child) return child
  }
  return nodes[0] || null
}

function findNodeById(nodes: any[], nodeId: number): any | null {
  for (const node of nodes) {
    if (Number(node.nodeId) === nodeId) return node
    const child = findNodeById(node.children || [], nodeId)
    if (child) return child
  }
  return null
}

function handleNodeSelect(node: any) {
  selectedNode.value = node
  saveState(node)
  void loadArticles()
}

async function loadArticles() {
  if (!courseId.value || !selectedNode.value?.nodeId) {
    articleList.value = []
    filteredArticles.value = []
    return
  }
  articlesLoading.value = true
  try {
    const res = await listResource({
      pageNum: 1,
      pageSize: 200,
      courseId: courseId.value,
      nodeId: selectedNode.value.nodeId,
      auditStatus: '1',
      status: '0',
      targetUserType: 'student',
    })
    articleList.value = (res.rows || []).map((item: any) => ({ ...item, _assetTypes: [] }))
    await enrichArticleAssets()
    filterArticles()
  } finally {
    articlesLoading.value = false
  }
}

async function enrichArticleAssets() {
  await Promise.all(
    articleList.value.map(async (item: any) => {
      const res = await listResourceAsset({ resourceId: item.resourceId, status: '0' })
      const assets = res.data || []
      item._assetTypes = Array.from(new Set(assets.map((asset: any) => asset.assetType)))
    }),
  )
}

function getFilteredArticlesData(ignoreAssetType = false) {
  let data = [...articleList.value]
  const text = keyword.value.trim()
  if (text) {
    data = data.filter((item: any) =>
      String(item.resourceName || '').includes(text) || String(item.summary || '').includes(text),
    )
  }
  if (publisherFilter.value) {
    data = data.filter((item: any) => item.publisherName === publisherFilter.value)
  }

  if (!ignoreAssetType) {
    data = data.filter((item: any) => !catalogAssetType.value || item._assetTypes.includes(catalogAssetType.value))
  }

  return data
}

function sortArticles(data: any[]) {
  if (sortMode.value === 'rating') {
    data.sort((a: any, b: any) => Number(b.avgRating || 0) - Number(a.avgRating || 0))
  } else if (sortMode.value === 'latest') {
    data.sort((a: any, b: any) => String(b.createTime || '').localeCompare(String(a.createTime || '')))
  } else {
    data.sort((a: any, b: any) =>
      Number(b.avgRating || 0) - Number(a.avgRating || 0) || Number(b.favoriteCount || 0) - Number(a.favoriteCount || 0),
    )
  }
  return data
}

function filterArticles() {
  const data = sortArticles(getFilteredArticlesData())
  filteredArticles.value = data
  saveState()
}

function resetFilters() {
  keyword.value = ''
  publisherFilter.value = ''
  sortMode.value = 'smart'
  filterArticles()
}

function getArticleAssetType(item: any) {
  if (catalogAssetType.value && item?._assetTypes?.includes(catalogAssetType.value)) {
    return catalogAssetType.value
  }
  return item?._assetTypes?.[0] || 'video_course'
}

function getArticleSummary(item: any) {
  return String(item.summary || item.contentText || '该资源暂未填写导学摘要，可进入详情页查看课时文件与评论内容。').trim()
}

async function openDetail(article: any) {
  saveState(selectedNode.value)
  await router.push({
    path: `/student/resources/${article.resourceId}`,
    query: {
      courseId: String(article.courseId || ''),
      nodeId: String(article.nodeId || ''),
      keyword: keyword.value || '',
      publisher: publisherFilter.value || '',
      sort: sortMode.value || 'smart',
      assetType: catalogAssetType.value || 'video_course',
    },
  })
}

function saveState(node: any = selectedNode.value) {
  if (restoringState.value) return
  const state = {
    courseId: courseId.value ? String(courseId.value) : '',
    nodeId: node?.nodeId ? String(node.nodeId) : '',
    keyword: keyword.value || '',
    publisher: publisherFilter.value || '',
    sort: sortMode.value || 'smart',
    assetType: catalogAssetType.value || 'video_course',
  }
  sessionStorage.setItem(stateStorageKey, JSON.stringify(state))
}

function readStoredState(): Record<string, string> {
  try {
    const raw = sessionStorage.getItem(stateStorageKey)
    if (!raw) return {}
    const parsed = JSON.parse(raw)
    return typeof parsed === 'object' && parsed ? parsed : {}
  } catch {
    return {}
  }
}

onMounted(loadCourses)
</script>

<style scoped>
.resource-center-page {
  --rc-primary: #0891b2;
  --rc-primary-deep: #0f766e;
  --rc-ink: #0f172a;
  --rc-muted: #64748b;
  --rc-border: rgba(148, 163, 184, 0.2);
  --rc-soft: #f8fcff;
  display: flex;
  flex-direction: column;
  gap: 18px;
  height: calc(100vh - 100px);
  padding: 10px 0 4px;
}

.resource-catalog-shell {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 18px;
  height: 100%;
  overflow: hidden;
}

.resource-catalog-sidebar {
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--rc-border);
  border-radius: 16px;
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.06);
  overflow: hidden;
}

.sidebar-inner {
  padding: 20px;
  display: grid;
  gap: 18px;
}

.catalog-course-crumb {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 12px;
  background: #f8fafc;
  color: var(--rc-primary-deep);
  font-size: 14px;
  line-height: 1.5;
  font-weight: 600;
}

.catalog-course-card {
  display: block;
  padding: 16px;
  border-radius: 14px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.04);
}

.catalog-course-card__meta {
  min-width: 0;
}

.catalog-course-card__skeleton {
  display: grid;
  gap: 10px;
}

.catalog-course-card__skeleton-line,
.catalog-course-card__skeleton-stats span,
.catalog-tree-skeleton__row,
.catalog-main-skeleton__cover,
.catalog-main-skeleton__line,
.catalog-main-skeleton__chips span {
  position: relative;
  overflow: hidden;
  background: linear-gradient(90deg, #eef4f8 0%, #f8fbfd 50%, #eef4f8 100%);
  background-size: 200% 100%;
  animation: resource-skeleton 1.4s ease-in-out infinite;
}

.catalog-course-card__skeleton-line {
  height: 14px;
  border-radius: 999px;
}

.catalog-course-card__skeleton-line.is-badge {
  width: 92px;
  height: 28px;
}

.catalog-course-card__skeleton-line.is-title {
  width: 68%;
  height: 20px;
}

.catalog-course-card__skeleton-stats {
  display: flex;
  gap: 12px;
  margin-top: 6px;
}

.catalog-course-card__skeleton-stats span {
  width: 88px;
  height: 12px;
  border-radius: 999px;
}

.catalog-course-card__status {
  margin-bottom: 8px;
}

.course-status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #ecfeff;
  color: var(--rc-primary-deep);
  font-size: 12px;
  font-weight: 700;
}

.catalog-course-card__meta h2 {
  margin: 0 0 6px;
  font-size: 17px;
  line-height: 1.4;
  font-weight: 700;
  color: var(--rc-ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.catalog-course-card__meta p {
  margin: 0;
  font-size: 13px;
  color: var(--rc-muted);
  line-height: 1.6;
}

.catalog-course-card__stats {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 12px;
}

.catalog-course-card__stats span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
}

.catalog-course-toolbar {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.catalog-course-toolbar__label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--rc-ink);
  font-size: 13px;
  font-weight: 700;
}

.catalog-tree-panel__title {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 14px;
}

.catalog-tree-panel__title-main p {
  margin: 6px 0 0;
  color: var(--rc-muted);
  font-size: 12px;
  line-height: 1.5;
}

.catalog-tree-panel__title strong {
  font-size: 16px;
  font-weight: 700;
  color: var(--rc-ink);
}

.catalog-tree-panel__title span {
  font-size: 12px;
  color: var(--rc-muted);
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #f8fafc;
  display: inline-flex;
  align-items: center;
  font-weight: 600;
}

.catalog-tree-skeleton {
  display: grid;
  gap: 8px;
}

.catalog-tree-skeleton__row {
  height: 42px;
  border-radius: 12px;
}

:deep(.el-tree) {
  background: transparent;
}

:deep(.el-tree-node__expand-icon) {
  display: none;
}

:deep(.el-tree-node__content) {
  height: auto;
  padding: 0 !important;
  margin-bottom: 6px;
}

:deep(.el-tree-node__content:hover) {
  background-color: transparent;
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: transparent;
}

.catalog-tree-node {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 12px;
  transition: background-color 0.2s, border-color 0.2s, transform 0.2s;
  background: #fff;
  border: 1px solid transparent;
}

.catalog-tree-node__main {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.catalog-tree-node__toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  padding: 0;
  border: none;
  background: transparent;
  color: #94a3b8;
  cursor: pointer;
  flex-shrink: 0;
}

.catalog-tree-node__toggle i {
  font-size: 18px;
  transition: transform 0.2s ease, color 0.2s ease;
}

.catalog-tree-node__toggle i.is-expanded {
  transform: rotate(90deg);
  color: var(--rc-primary-deep);
}

.catalog-tree-node__icon {
  color: var(--rc-primary);
  font-size: 15px;
  flex-shrink: 0;
}

.catalog-tree-node:hover {
  background: #f8fcff;
  border-color: rgba(8, 145, 178, 0.14);
  transform: translateX(2px);
}

:deep(.el-tree-node.is-current > .el-tree-node__content .catalog-tree-node) {
  background: #ecfeff;
  border-color: rgba(8, 145, 178, 0.18);
}

:deep(.el-tree-node.is-current > .el-tree-node__content .catalog-tree-node__label) {
  color: var(--rc-primary-deep);
  font-weight: 700;
}

.catalog-tree-node__label {
  color: #334155;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.catalog-tree-node__count {
  min-width: 26px;
  text-align: center;
  color: #94a3b8;
  font-size: 12px;
}

.resource-catalog-main {
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--rc-border);
  border-radius: 16px;
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.06);
  overflow: hidden;
}

.main-inner {
  padding: 22px 24px 24px;
}

.catalog-main-toolbar {
  display: grid;
  gap: 16px;
  padding: 0 0 18px;
  border-bottom: 1px solid #f1f5f9;
}

.catalog-main-toolbar__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  flex-wrap: wrap;
}

.catalog-main-toolbar__title {
  min-width: 0;
  flex: 1;
}

.catalog-main-toolbar__eyebrow {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #effbff;
  color: var(--rc-primary-deep);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.catalog-main-toolbar__title h2 {
  margin: 10px 0 6px;
  color: var(--rc-ink);
  font-size: 26px;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.catalog-main-toolbar__title p {
  margin: 0;
  color: var(--rc-muted);
  font-size: 14px;
  line-height: 1.7;
  max-width: 58ch;
}

.catalog-main-toolbar__stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(120px, 1fr));
  gap: 10px;
}

.catalog-main-toolbar__stat {
  min-height: 72px;
  padding: 12px 14px;
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fcff 100%);
  border: 1px solid #e2edf5;
  box-shadow: 0 10px 22px rgba(15, 23, 42, 0.04);
}

.catalog-main-toolbar__stat span {
  display: block;
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 8px;
}

.catalog-main-toolbar__stat strong {
  display: block;
  color: var(--rc-ink);
  font-size: 17px;
  line-height: 1.3;
  font-weight: 700;
}

.catalog-main-tabs {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.catalog-main-tab {
  border: 1px solid transparent;
  background: #f8fafc;
  color: var(--rc-muted);
  font-size: 14px;
  font-weight: 600;
  min-height: 42px;
  padding: 8px 14px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: background-color 0.2s, border-color 0.2s, color 0.2s, transform 0.2s;
}

.catalog-main-tab:hover {
  color: var(--rc-primary-deep);
  background: #f0fdff;
  border-color: rgba(8, 145, 178, 0.12);
  transform: translateY(-1px);
}

.catalog-main-tab.is-active {
  color: var(--rc-primary-deep);
  font-weight: 700;
  background: #ecfeff;
  border-color: rgba(8, 145, 178, 0.18);
}

.catalog-main-tab i {
  font-size: 16px;
}

.catalog-main-tab em {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 24px;
  padding: 0 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.9);
  color: inherit;
  font-size: 12px;
  font-style: normal;
  font-weight: 700;
}

.catalog-main-filters {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.catalog-reset-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 42px;
  padding: 0 14px;
  border: 1px solid #dbe5f0;
  border-radius: 12px;
  background: #fff;
  color: var(--rc-muted);
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: background-color 0.2s, border-color 0.2s, color 0.2s;
}

.catalog-reset-btn:hover {
  background: #f8fafc;
  border-color: #bfdbfe;
  color: var(--rc-primary-deep);
}

.catalog-active-filters {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-top: 16px;
}

.catalog-active-filters__label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #475569;
  font-size: 13px;
  font-weight: 700;
}

.catalog-active-filters__chip {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #475569;
  font-size: 12px;
  font-weight: 600;
}

.catalog-results-bar {
  margin-top: 18px;
  padding: 14px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  background: #fff;
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.catalog-results-bar__info,
.catalog-results-bar__hint {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.catalog-results-bar__info {
  color: var(--rc-muted);
}

.catalog-results-bar__info strong {
  color: var(--rc-ink);
  font-weight: 700;
}

.catalog-results-bar__hint {
  color: #94a3b8;
}

.catalog-main-list {
  padding-top: 18px;
  display: grid;
  gap: 14px;
}

.catalog-main-skeleton {
  display: grid;
  gap: 14px;
}

.catalog-main-skeleton__card {
  display: grid;
  grid-template-columns: 216px minmax(0, 1fr);
  gap: 18px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  background: #fff;
}

.catalog-main-skeleton__cover {
  height: 132px;
  border-radius: 14px;
}

.catalog-main-skeleton__content {
  display: grid;
  gap: 12px;
}

.catalog-main-skeleton__line {
  height: 13px;
  border-radius: 999px;
}

.catalog-main-skeleton__line.is-short {
  width: 112px;
}

.catalog-main-skeleton__line.is-title {
  width: 56%;
  height: 18px;
}

.catalog-main-skeleton__chips {
  display: flex;
  gap: 10px;
  margin-top: 6px;
}

.catalog-main-skeleton__chips span {
  width: 100px;
  height: 34px;
  border-radius: 999px;
}

.catalog-lesson-card {
  display: grid;
  grid-template-columns: 216px minmax(0, 1fr);
  gap: 18px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  background: #fff;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s, transform 0.2s;
  outline: none;
}

.catalog-lesson-card:hover {
  border-color: rgba(8, 145, 178, 0.18);
  box-shadow: 0 18px 34px rgba(15, 23, 42, 0.08);
  transform: translateY(-2px);
}

.catalog-lesson-card:focus-visible {
  border-color: rgba(8, 145, 178, 0.42);
  box-shadow: 0 0 0 3px rgba(8, 145, 178, 0.14);
}

.catalog-lesson-card__cover {
  flex-shrink: 0;
  width: 100%;
  height: 132px;
  border-radius: 14px;
  overflow: hidden;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  position: relative;
}

.catalog-lesson-card__cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.24s ease;
}

.catalog-lesson-card:hover .catalog-lesson-card__cover img {
  transform: scale(1.03);
}

.catalog-lesson-card__cover-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
}

.catalog-lesson-card__cover-fallback i {
  font-size: 32px;
  color: var(--rc-primary);
}

.catalog-lesson-card__cover-fallback span {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.08em;
}

.catalog-lesson-card__cover-meta {
  position: absolute;
  left: 10px;
  right: 10px;
  bottom: 10px;
  display: flex;
  justify-content: space-between;
  gap: 8px;
  flex-wrap: wrap;
}

.catalog-lesson-card__cover-meta span {
  display: inline-flex;
  align-items: center;
  min-height: 24px;
  padding: 0 8px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.64);
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  backdrop-filter: blur(6px);
}

.catalog-lesson-card__content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 0;
}

.catalog-lesson-card__top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.catalog-lesson-card__title-block {
  flex: 1;
  min-width: 0;
}

.catalog-lesson-card__title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.catalog-lesson-card__tag {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  font-size: 12px;
  color: var(--rc-primary-deep);
  border: 1px solid rgba(8, 145, 178, 0.16);
  border-radius: 999px;
  background: #ecfeff;
  font-weight: 700;
}

.catalog-lesson-card__rating {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #b45309;
  font-size: 13px;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #fff7ed;
  font-weight: 700;
}

.catalog-lesson-card__rating i {
  color: #f59e0b;
  font-size: 14px;
}

.catalog-lesson-card__title-block h3 {
  margin: 0;
  font-size: 20px;
  color: var(--rc-ink);
  font-weight: 700;
  line-height: 1.35;
}

.catalog-lesson-card__summary {
  margin: 10px 0 0;
  color: var(--rc-muted);
  font-size: 14px;
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.catalog-lesson-card__cta {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--rc-primary-deep);
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
  padding-top: 2px;
  transition: transform 0.2s ease, color 0.2s ease;
}

.catalog-lesson-card:hover .catalog-lesson-card__cta {
  transform: translateX(2px);
}

.catalog-lesson-card__meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.catalog-lesson-card__meta-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  background: #f8fafc;
  color: #475569;
  font-size: 13px;
  font-weight: 600;
}

.catalog-lesson-card__stats {
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
  color: #94a3b8;
  font-size: 13px;
}

.catalog-lesson-card__stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

:deep(.el-empty) {
  padding: 30px 0;
}

.catalog-course-toolbar :deep(.el-select__wrapper),
.catalog-filter-control :deep(.el-input__wrapper),
.catalog-filter-control :deep(.el-select__wrapper) {
  min-height: 42px;
  border-radius: 12px;
  box-shadow: 0 0 0 1px #dbe5f0 inset !important;
}

.catalog-course-toolbar :deep(.el-select__wrapper:hover),
.catalog-filter-control :deep(.el-input__wrapper:hover),
.catalog-filter-control :deep(.el-select__wrapper:hover) {
  box-shadow: 0 0 0 1px #bfdbfe inset !important;
}

.catalog-course-toolbar :deep(.el-select__wrapper.is-focused),
.catalog-filter-control :deep(.el-input__wrapper.is-focus),
.catalog-filter-control :deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px rgba(8, 145, 178, 0.4) inset !important;
}

@media (max-width: 1400px) {
  .resource-catalog-shell {
    grid-template-columns: 1fr;
  }

  .catalog-main-toolbar__stats {
    width: 100%;
  }
}

@media (max-width: 960px) {
  .catalog-main-toolbar__head,
  .catalog-main-filters {
    flex-direction: column;
    align-items: stretch;
  }

  .catalog-main-toolbar__stats,
  .catalog-main-skeleton__card {
    grid-template-columns: 1fr;
  }

  .catalog-main-tabs {
    gap: 10px;
  }

  .catalog-lesson-card {
    grid-template-columns: 1fr;
  }

  .catalog-lesson-card__cover {
    width: 100%;
  }

  .main-inner,
  .sidebar-inner {
    padding: 18px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .catalog-course-card,
  .catalog-tree-node,
  .catalog-main-tab,
  .catalog-reset-btn,
  .catalog-lesson-card,
  .catalog-lesson-card__cover img,
  .catalog-lesson-card__cta {
    transition: none !important;
    transform: none !important;
  }
}

@keyframes resource-skeleton {
  0% {
    background-position: 200% 0;
  }

  100% {
    background-position: -200% 0;
  }
}
</style>
