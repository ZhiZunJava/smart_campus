<template>
  <div class="portal-page favorites-page">
    <div class="page-header">
      <div class="header-titles">
        <h3>我的收藏</h3>
        <p>统一查看你收藏过的课时内容，方便二次学习和复盘。</p>
      </div>
      <div class="header-actions">
        <el-input
          v-model="keyword"
          placeholder="搜索已收藏内容..."
          clearable
          style="width: 240px"
          @input="loadData"
        >
          <template #prefix>
            <i class="ri-search-line"></i>
          </template>
        </el-input>
        <el-select
          v-model="selectedCourseId"
          clearable
          filterable
          placeholder="所有课程"
          style="width: 200px"
          @change="loadData"
        >
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>
    </div>

    <div class="favorites-list">
      <template v-if="filteredList.length">
        <article
          v-for="item in filteredList"
          :key="item.resourceId"
          class="favorite-card"
          @click="openDetail(item)"
        >
          <div class="card-cover">
            <img v-if="item.coverUrl" :src="resolveResourceUrl(item.coverUrl)" :alt="item.resourceName">
            <div v-else class="card-cover-fallback">
              <i class="ri-image-line"></i>
              <span>{{ item.resourceName?.slice(0, 4) || '资源' }}</span>
            </div>
          </div>

          <div class="card-content">
            <div class="card-main">
              <div class="card-title-row">
                <h4 class="card-title" :title="item.resourceName">{{ item.resourceName }}</h4>
                <el-tooltip content="取消收藏" placement="top">
                  <el-button
                    class="unfavorite-btn"
                    circle
                    text
                    @click.stop="removeFavorite(item)"
                  >
                    <i class="ri-heart-3-fill"></i>
                  </el-button>
                </el-tooltip>
              </div>

              <div class="card-tags">
                <el-tag size="small" class="custom-tag course-tag" disable-transitions>
                  <i class="ri-book-2-line"></i> {{ courseLabel(item.courseId) }}
                </el-tag>
                <el-tag size="small" type="info" effect="plain" class="custom-tag" disable-transitions>
                  {{ item.resourceType || 'article' }}
                </el-tag>
              </div>

              <p class="card-summary">{{ item.summary || item.contentText || '该资源暂无详细描述...' }}</p>
            </div>

            <div class="card-footer">
              <div class="meta-info">
                <span class="meta-item"><i class="ri-user-smile-line"></i> {{ item.authorName || '未填写' }}</span>
                <span class="meta-divider"></span>
                <span class="meta-item"><i class="ri-building-line"></i> {{ item.publisherName || '未知来源' }}</span>
                <span class="meta-divider"></span>
                <span class="meta-item rating"><i class="ri-star-smile-fill"></i> {{ Number(item.avgRating || 0).toFixed(1) }} 分</span>
              </div>
            </div>
          </div>
        </article>
      </template>

      <div v-else class="empty-state">
        <el-empty description="当前筛选条件下还没有收藏资源">
          <el-button v-if="keyword || selectedCourseId" type="primary" plain @click="clearFilters">
            清除筛选条件
          </el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listCourse, listFavoriteResources, unfavoriteResource } from '@/api/portal'
import { resolveResourceUrl } from '@/utils/resource'
import { ElMessage } from 'element-plus'

const router = useRouter()
const keyword = ref('')
const list = ref<any[]>([])
const selectedCourseId = ref<number | undefined>()
const courseMap = ref<Record<string, string>>({})

const courseOptions = computed(() =>
  Array.from(new Set(list.value.map((item) => String(item.courseId || '')).filter(Boolean))).map((courseId) => ({
    label: courseMap.value[courseId] || `课程 ${courseId}`,
    value: Number(courseId),
  })),
)

const filteredList = computed(() =>
  list.value.filter((item) => !selectedCourseId.value || String(item.courseId || '') === String(selectedCourseId.value)),
)

async function loadCourseMap() {
  try {
    const res = await listCourse({ pageNum: 1, pageSize: 500 })
    const rows = res.rows || []
    courseMap.value = rows.reduce((map: Record<string, string>, item: any) => {
      map[String(item.courseId)] = item.courseName || `课程 ${item.courseId}`
      return map
    }, {})
  } catch {
    courseMap.value = {}
  }
}

async function loadData() {
  try {
    const res = await listFavoriteResources({
      pageNum: 1,
      pageSize: 100,
      resourceName: keyword.value,
    })
    list.value = res.rows || []
  } catch (error) {
    console.error('加载收藏失败', error)
  }
}

async function removeFavorite(item: any) {
  try {
    await unfavoriteResource(item.resourceId)
    ElMessage.success('已取消收藏')
    await loadData()
  } catch (error) {
    ElMessage.error('取消收藏失败')
  }
}

function openDetail(item: any) {
  router.push({
    path: `/student/resources/${item.resourceId}`,
    query: {
      courseId: String(item.courseId || ''),
      nodeId: String(item.nodeId || ''),
    },
  })
}

function courseLabel(courseId?: number | string) {
  if (!courseId) return '未分配课程'
  return courseMap.value[String(courseId)] || `课程 ${courseId}`
}

function clearFilters() {
  keyword.value = ''
  selectedCourseId.value = undefined
  loadData()
}

// 初始化
void loadCourseMap()
void loadData()
</script>

<style scoped>
.favorites-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
  /* 假设你的 portal-page 没有内边距，这里加上以避免贴边 */
  padding: 20px 0; 
}

/* 头部样式 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding: 24px;
  background: var(--el-bg-color, #ffffff);
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--el-border-color-light, #e4e7ed);
}

.header-titles h3 {
  margin: 0 0 8px 0;
  font-size: 22px;
  color: var(--el-text-color-primary, #303133);
  font-weight: 600;
}

.header-titles p {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-regular, #606266);
}

.header-actions {
  display: flex;
  gap: 16px;
}

/* 列表容器 */
.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 卡片样式 */
.favorite-card {
  display: flex;
  gap: 24px;
  padding: 20px;
  background: var(--el-bg-color, #ffffff);
  border-radius: 12px;
  cursor: pointer;
  border: 1px solid var(--el-border-color-light, #e4e7ed);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
}

.favorite-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  transform: translateY(-2px);
  border-color: var(--el-color-primary-light-5, #a0cfff);
}

/* 封面区 */
.card-cover {
  width: 240px;
  height: 135px; /* 16:9 标准比例 */
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
  background: var(--el-fill-color-light, #f5f7fa);
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.favorite-card:hover .card-cover img {
  transform: scale(1.05);
}

.card-cover-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-placeholder, #a8abb2);
  background: linear-gradient(135deg, var(--el-fill-color-light) 0%, var(--el-fill-color) 100%);
  font-size: 14px;
  font-weight: 600;
}

.card-cover-fallback i {
  font-size: 36px;
  margin-bottom: 8px;
  color: var(--el-color-primary-light-5);
}
/* 内容区 */
.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0; /* 防止文本溢出导致 flex 撑破 */
}

.card-title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.card-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary, #303133);
  line-height: 1.4;
  transition: color 0.2s;
  /* 多行截断 */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.favorite-card:hover .card-title {
  color: var(--el-color-primary, #409eff);
}

/* 收藏按钮 */
.unfavorite-btn {
  color: var(--el-color-danger, #f56c6c);
  font-size: 20px;
  margin-top: -4px;
  margin-right: -8px;
  opacity: 0.5;
  transition: all 0.2s;
}

.unfavorite-btn:hover {
  opacity: 1;
  transform: scale(1.1);
  color: var(--el-color-danger);
  background: var(--el-color-danger-light-9) !important;
}

/* 标签与摘要 */
.card-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.custom-tag {
  border: none;
  font-weight: 500;
}

.course-tag {
  background: var(--el-color-primary-light-9, #ecf5ff);
  color: var(--el-color-primary, #409eff);
}

.card-summary {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: var(--el-text-color-regular, #606266);
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 最多显示两行 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 底部元数据 */
.card-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed var(--el-border-color-lighter, #ebeef5);
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: var(--el-text-color-secondary, #909399);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-item i {
  font-size: 15px;
}

.meta-item.rating {
  color: #e6a23c;
  font-weight: 600;
}

.meta-divider {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background-color: var(--el-border-color-darker, #c0c4cc);
}

/* 空状态 */
.empty-state {
  background: var(--el-bg-color, #ffffff);
  border-radius: 12px;
  padding: 60px 0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--el-border-color-light, #e4e7ed);
}

/* 移动端响应式 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  .favorite-card {
    flex-direction: column;
    gap: 16px;
  }
  .card-cover {
    width: 100%;
    height: 180px;
  }

  .header-actions {
    flex-direction: column;
    gap: 10px;
  }

  .header-actions .el-input,
  .header-actions .el-select {
    width: 100% !important;
  }
}

@media (max-width: 640px) {
  .favorites-page {
    padding: 12px 0;
  }

  .page-header {
    padding: 16px;
    border-radius: 8px;
  }

  .header-titles h3 {
    font-size: 18px;
  }

  .favorite-card {
    padding: 14px;
    gap: 12px;
  }

  .card-cover {
    height: 150px;
  }

  .card-title {
    font-size: 16px;
  }

  .card-summary {
    font-size: 13px;
  }

  .meta-info {
    font-size: 12px;
    gap: 8px;
  }

  .card-footer {
    margin-top: 12px;
    padding-top: 12px;
  }
}
</style>