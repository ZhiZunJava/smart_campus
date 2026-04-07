<template>
  <div class="portal-page teacher-resources-page">
    <section class="teacher-resources__hero portal-card">
      <div class="teacher-resources__hero-copy">
        <span class="teacher-resources__eyebrow">教学资源</span>
        <h3>教学资源中心</h3>
        <p>把自己上传的教学资源、浏览下载情况和资源详情放到同一页里，方便教师边筛选边查看。</p>
      </div>
      <div class="teacher-resources__hero-stats">
        <div class="teacher-resources__hero-stat"><span>资源数</span><strong>{{ resources.length }}</strong></div>
        <div class="teacher-resources__hero-stat"><span>总浏览</span><strong>{{ totalViews }}</strong></div>
      </div>
    </section>

    <div class="portal-card teacher-resources__toolbar">
      <div class="portal-section-title">
        <h3>资源筛选</h3>
        <p>按教师、资源名和类型组合过滤，缩短查找资源的路径。</p>
      </div>
      <div class="teacher-resources__filters">
        <el-select v-model="queryParams.uploaderId" filterable clearable placeholder="选择教师" class="teacher-resources__select">
          <el-option v-for="item in teacherOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="queryParams.resourceName" placeholder="资源名称" class="teacher-resources__input" @keyup.enter="getList" />
        <el-select v-model="queryParams.resourceType" clearable placeholder="资源类型" class="teacher-resources__select--small">
          <el-option label="视频" value="video" />
          <el-option label="文档" value="doc" />
          <el-option label="PPT" value="ppt" />
          <el-option label="PDF" value="pdf" />
        </el-select>
        <el-button type="primary" @click="getList">搜索</el-button>
      </div>
    </div>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">资源数</div><div class="value">{{ resources.length }}</div><div class="sub">当前筛选范围</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">总浏览</div><div class="value">{{ totalViews }}</div><div class="sub">资源浏览总量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">总下载</div><div class="value">{{ totalDownloads }}</div><div class="sub">资源下载总量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">总收藏</div><div class="value">{{ totalFavorites }}</div><div class="sub">资源收藏总量</div></el-card>
    </section>

    <div class="teacher-resources__grid">
      <div class="portal-card teacher-resources__panel">
        <div class="teacher-resources__panel-head">
          <h4>资源列表</h4>
        </div>
        <el-skeleton :loading="loading" animated>
          <template #template>
            <div class="teacher-resources__skeleton">
              <el-skeleton-item v-for="index in 6" :key="`resource-row-${index}`" variant="rect" class="teacher-resources__skeleton-row" />
            </div>
          </template>
          <template #default>
            <el-table :data="resources" @row-click="selectResource">
              <el-table-column prop="resourceId" label="资源ID" width="90" />
              <el-table-column label="资源名称" min-width="240">
                <template #default="{ row }">
                  <div class="teacher-resources__resource-cell">
                    <strong>{{ row.resourceName || '-' }}</strong>
                    <span>{{ row.resourceType || '未分类' }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="resourceType" label="类型" width="100" />
              <el-table-column prop="qualityScore" label="质量分" width="90" />
              <el-table-column prop="viewCount" label="浏览" width="90" />
              <el-table-column prop="downloadCount" label="下载" width="90" />
              <el-table-column prop="favoriteCount" label="收藏" width="90" />
            </el-table>
            <el-empty v-if="resources.length === 0" description="暂无资源数据" />
          </template>
        </el-skeleton>
      </div>

      <div class="portal-card teacher-resources__panel">
        <div class="teacher-resources__panel-head">
          <h4>资源详情</h4>
        </div>
        <el-skeleton :loading="loading && !detail.resourceId" animated>
          <template #template>
            <div class="teacher-resources__detail-skeleton">
              <el-skeleton-item variant="rect" class="teacher-resources__detail-skeleton-title" />
              <el-skeleton-item variant="rect" class="teacher-resources__detail-skeleton-block" />
              <el-skeleton-item variant="rect" class="teacher-resources__detail-skeleton-block" />
            </div>
          </template>
          <template #default>
            <template v-if="detail.resourceId">
              <div class="teacher-resources__detail-title">{{ detail.resourceName }}</div>
              <div class="teacher-resources__detail-grid">
                <div class="teacher-resources__detail-item"><span>资源类型</span><strong>{{ detail.resourceType || '-' }}</strong></div>
                <div class="teacher-resources__detail-item"><span>质量分</span><strong>{{ detail.qualityScore || 0 }}</strong></div>
                <div class="teacher-resources__detail-item"><span>浏览量</span><strong>{{ detail.viewCount || 0 }}</strong></div>
                <div class="teacher-resources__detail-item"><span>下载量</span><strong>{{ detail.downloadCount || 0 }}</strong></div>
              </div>
              <div class="teacher-resources__detail-note">
                <strong>摘要</strong>
                <p>{{ detail.summary || '-' }}</p>
              </div>
              <div class="teacher-resources__detail-note">
                <strong>内容文本</strong>
                <p>{{ detail.contentText || '-' }}</p>
              </div>
            </template>
            <el-empty v-else class="portal-empty" description="请选择一条资源查看详情" />
          </template>
        </el-skeleton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { fetchPortalUserOptions, listResource } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loading = ref(false)
const resources = ref<any[]>([])
const teacherOptions = ref<any[]>([])
const detail = ref<any>({})
const queryParams = reactive<any>({ pageNum: 1, pageSize: 20, uploaderId: undefined, resourceName: '', resourceType: '', status: '0' })

const totalViews = computed(() => resources.value.reduce((sum: number, item: any) => sum + Number(item.viewCount || 0), 0))
const totalDownloads = computed(() => resources.value.reduce((sum: number, item: any) => sum + Number(item.downloadCount || 0), 0))
const totalFavorites = computed(() => resources.value.reduce((sum: number, item: any) => sum + Number(item.favoriteCount || 0), 0))

async function loadOptions() {
  teacherOptions.value = await fetchPortalUserOptions('teacher')
  queryParams.uploaderId = userStore.user?.userId || teacherOptions.value[0]?.value
}

async function getList() {
  loading.value = true
  try {
    const res = await listResource(queryParams)
    resources.value = res.rows || []
    if (!detail.value.resourceId && resources.value.length) {
      detail.value = resources.value[0]
    }
  } finally {
    loading.value = false
  }
}

function selectResource(row: any) {
  detail.value = row
}

onMounted(async () => {
  await loadOptions()
  await getList()
})
</script>

<style scoped>
.teacher-resources-page {
  padding: 24px 32px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.teacher-resources__hero {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(260px, 0.65fr);
  gap: 18px;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(24, 148, 106, 0.12) 0%, rgba(24, 148, 106, 0) 34%),
    linear-gradient(135deg, #ffffff 0%, #f2fbf8 100%);
}

.teacher-resources__eyebrow {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(24, 148, 106, 0.12);
  color: #12795a;
  font-size: 12px;
  font-weight: 700;
}

.teacher-resources__hero-copy h3 {
  margin: 12px 0 0;
  font-size: 30px;
  font-weight: 800;
  color: var(--portal-text);
}

.teacher-resources__hero-copy p {
  margin: 10px 0 0;
  font-size: 14px;
  line-height: 1.9;
  color: var(--portal-text-secondary);
}

.teacher-resources__hero-stats {
  display: grid;
  gap: 12px;
}

.teacher-resources__hero-stat {
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.84);
  border: 1px solid var(--portal-border);
}

.teacher-resources__hero-stat span {
  color: var(--portal-text-secondary);
  font-size: 13px;
}

.teacher-resources__hero-stat strong {
  display: block;
  margin-top: 8px;
  font-size: 28px;
  color: #12795a;
}

.teacher-resources__toolbar {
  padding: 18px;
}

.teacher-resources__filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.teacher-resources__select {
  width: 220px;
}

.teacher-resources__select--small {
  width: 180px;
}

.teacher-resources__input {
  width: 240px;
}

.teacher-resources__grid {
  display: grid;
  grid-template-columns: minmax(0, 1.3fr) minmax(320px, 0.7fr);
  gap: 16px;
}

.teacher-resources__panel {
  padding: 18px;
}

.teacher-resources__skeleton,
.teacher-resources__detail-skeleton {
  display: grid;
  gap: 12px;
}

.teacher-resources__skeleton-row {
  height: 52px;
  border-radius: 10px;
}

.teacher-resources__detail-skeleton-title {
  width: 100%;
  height: 56px;
  border-radius: 14px;
}

.teacher-resources__detail-skeleton-block {
  width: 100%;
  height: 120px;
  border-radius: 14px;
}

.teacher-resources__panel-head {
  margin-bottom: 14px;
}

.teacher-resources__panel-head h4 {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  color: var(--portal-text);
}

.teacher-resources__resource-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.teacher-resources__resource-cell strong {
  color: var(--portal-text);
  font-size: 14px;
}

.teacher-resources__resource-cell span {
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.teacher-resources__detail-title {
  padding: 14px;
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff, #f7fcfa);
  border: 1px solid #dbe8f8;
  font-size: 16px;
  font-weight: 700;
  color: var(--portal-text);
}

.teacher-resources__detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.teacher-resources__detail-item,
.teacher-resources__detail-note {
  padding: 14px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1px solid var(--portal-border);
}

.teacher-resources__detail-item span {
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.teacher-resources__detail-item strong {
  display: block;
  margin-top: 6px;
  color: var(--portal-text);
  font-size: 16px;
}

.teacher-resources__detail-note {
  margin-top: 12px;
}

.teacher-resources__detail-note strong {
  display: block;
  color: var(--portal-text);
  font-size: 14px;
}

.teacher-resources__detail-note p {
  margin: 8px 0 0;
  color: var(--portal-text-secondary);
  font-size: 13px;
  line-height: 1.8;
}

@media (max-width: 960px) {
  .teacher-resources-page {
    padding: 20px;
  }

  .teacher-resources__hero,
  .teacher-resources__grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .teacher-resources-page {
    padding: 16px;
    gap: 14px;
  }

  .teacher-resources__hero {
    padding: 18px;
  }

  .teacher-resources__hero-copy h3 {
    font-size: 22px;
  }

  .teacher-resources__hero-stat strong {
    font-size: 22px;
  }

  .teacher-resources__toolbar {
    padding: 14px;
  }

  .teacher-resources__select,
  .teacher-resources__select--small,
  .teacher-resources__input {
    width: 100%;
  }

  .teacher-resources__panel {
    padding: 14px;
  }

  .teacher-resources__detail-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .teacher-resources-page {
    padding: 12px;
    gap: 12px;
  }

  .teacher-resources__hero {
    padding: 14px;
  }

  .teacher-resources__hero-copy h3 {
    font-size: 18px;
  }

  .teacher-resources__hero-copy p {
    font-size: 13px;
  }

  .teacher-resources__hero-stat {
    padding: 12px 14px;
  }

  .teacher-resources__hero-stat strong {
    font-size: 18px;
  }

  .teacher-resources__panel {
    padding: 12px;
  }

  .teacher-resources__panel-head h4 {
    font-size: 16px;
  }
}
</style>
