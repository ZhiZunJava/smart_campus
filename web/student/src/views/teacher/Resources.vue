<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>教学资源</h3>
      <el-tag type="primary">查看自己上传的资源与使用情况</el-tag>
    </div>

    <el-card class="portal-card portal-soft-card">
      <div class="portal-form-row">
        <el-select v-model="queryParams.uploaderId" filterable clearable placeholder="选择教师" style="width: 220px">
          <el-option v-for="item in teacherOptions" :key="item.value" :label="item.label" :value="item.value" />
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

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">资源数</div><div class="value">{{ resources.length }}</div><div class="sub">当前筛选下资源数量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">总浏览</div><div class="value">{{ totalViews }}</div><div class="sub">资源浏览总量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">总下载</div><div class="value">{{ totalDownloads }}</div><div class="sub">资源下载总量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">总收藏</div><div class="value">{{ totalFavorites }}</div><div class="sub">资源收藏总量</div></el-card>
    </section>

    <div class="portal-grid portal-grid-2 mt20">
      <el-card class="portal-card">
        <template #header><span>资源列表</span></template>
        <el-table v-loading="loading" :data="resources" @row-click="selectResource">
          <el-table-column prop="resourceId" label="资源ID" width="90" />
          <el-table-column prop="resourceName" label="资源名称" min-width="220" />
          <el-table-column prop="resourceType" label="类型" width="100" />
          <el-table-column prop="qualityScore" label="质量分" width="90" />
          <el-table-column prop="viewCount" label="浏览" width="90" />
          <el-table-column prop="downloadCount" label="下载" width="90" />
          <el-table-column prop="favoriteCount" label="收藏" width="90" />
        </el-table>
        <el-empty v-if="!loading && resources.length === 0" description="暂无资源数据" />
      </el-card>

      <el-card class="portal-card portal-soft-card">
        <template #header><span>资源详情</span></template>
        <template v-if="detail.resourceId">
          <div class="portal-surface">资源名称：{{ detail.resourceName }}</div>
          <div class="portal-grid portal-grid-2 mt16">
            <div class="portal-surface">资源类型：{{ detail.resourceType || '-' }}</div>
            <div class="portal-surface">质量分：{{ detail.qualityScore || 0 }}</div>
            <div class="portal-surface">浏览量：{{ detail.viewCount || 0 }}</div>
            <div class="portal-surface">下载量：{{ detail.downloadCount || 0 }}</div>
          </div>
          <div class="portal-surface mt16">摘要：{{ detail.summary || '-' }}</div>
          <div class="portal-surface mt16">内容文本：{{ detail.contentText || '-' }}</div>
        </template>
        <el-empty v-else class="portal-empty" description="请选择一条资源查看详情" />
      </el-card>
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
