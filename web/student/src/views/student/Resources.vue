<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>资源中心</h3>
      <el-tag type="primary">支持筛选、查看详情与学习行为记录</el-tag>
    </div>

    <el-card class="portal-card portal-soft-card">
      <div class="portal-form-row">
        <el-select v-model="queryParams.courseId" filterable clearable placeholder="选择课程" style="width: 220px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="queryParams.resourceName" placeholder="资源名称" style="width: 220px" @keyup.enter="getList" />
        <el-select v-model="queryParams.resourceType" clearable placeholder="资源类型" style="width: 160px">
          <el-option label="视频" value="video" />
          <el-option label="文档" value="doc" />
          <el-option label="PPT" value="ppt" />
          <el-option label="PDF" value="pdf" />
        </el-select>
        <el-button type="primary" @click="getList">搜索</el-button>
      </div>
    </el-card>

    <div class="portal-grid portal-grid-2 mt20">
      <el-card class="portal-card">
        <template #header><span>资源列表</span></template>
        <el-table v-loading="loading" :data="list">
          <el-table-column prop="resourceName" label="资源名称" min-width="200" />
          <el-table-column prop="resourceType" label="类型" width="100" />
          <el-table-column prop="qualityScore" label="质量分" width="90" />
          <el-table-column label="操作" width="160">
            <template #default="scope">
              <el-button link type="primary" @click="viewDetail(scope.row.resourceId)">详情</el-button>
              <el-button link type="success" @click="recordFavorite(scope.row.resourceId)">收藏</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="portal-card">
        <template #header><span>资源详情</span></template>
        <template v-if="detail.resourceId">
          <div class="portal-surface">资源名称：{{ detail.resourceName }}</div>
          <div class="portal-surface mt16">资源类型：{{ detail.resourceType }}</div>
          <div class="portal-surface mt16">摘要：{{ detail.summary || '-' }}</div>
          <div class="portal-grid portal-grid-2 mt16">
            <div class="portal-surface">浏览量：{{ detail.viewCount || 0 }}</div>
            <div class="portal-surface">下载量：{{ detail.downloadCount || 0 }}</div>
          </div>
          <div class="portal-surface mt16">内容文本：{{ detail.contentText || '-' }}</div>
          <div class="mt16">
            <el-link v-if="detail.fileUrl" :href="detail.fileUrl" target="_blank" type="primary" @click="recordDownload(detail.resourceId)">打开资源并记录下载</el-link>
          </div>
        </template>
        <el-empty v-else class="portal-empty" description="请选择一条资源查看详情" />
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchPortalCourseOptions, getResourceDetail, listResource, recordStudyBehavior } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loading = ref(false)
const list = ref<any[]>([])
const detail = ref<any>({})
const courseOptions = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 20, auditStatus: '1', status: '0', resourceName: '', resourceType: '', courseId: undefined })

async function loadOptions() {
  courseOptions.value = await fetchPortalCourseOptions()
}

async function getList() {
  loading.value = true
  try {
    const res = await listResource(queryParams)
    list.value = res.rows || []
  } finally {
    loading.value = false
  }
}

async function recordBehavior(resourceId: number, behaviorType: string) {
  const userId = userStore.user?.userId
  if (!userId) return
  await recordStudyBehavior({
    userId,
    courseId: detail.value.courseId || queryParams.courseId,
    resourceId,
    behaviorType,
    durationSeconds: 120,
    progressRate: 20,
    deviceType: 'web',
    sourcePage: 'student-resource',
  })
}

async function viewDetail(id: number) {
  const res = await getResourceDetail(id)
  detail.value = res.data || {}
  await recordBehavior(id, 'view')
}

async function recordDownload(resourceId: number) {
  await recordBehavior(resourceId, 'download')
  ElMessage.success('已记录下载行为')
}

async function recordFavorite(resourceId: number) {
  await recordBehavior(resourceId, 'favorite')
  ElMessage.success('已记录收藏行为')
}

onMounted(async () => {
  await loadOptions()
  await getList()
})
</script>
