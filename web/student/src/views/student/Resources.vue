<template>
  <div class="portal-page portal-grid portal-grid-2">
    <el-card class="portal-card">
      <template #header><span>资源中心</span></template>
      <el-form :inline="true" :model="queryParams" class="mb16">
        <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width:240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="资源名称"><el-input v-model="queryParams.resourceName" style="width:220px" @keyup.enter="getList" /></el-form-item>
        <el-form-item label="资源类型"><el-select v-model="queryParams.resourceType" clearable style="width:180px"><el-option label="视频" value="video" /><el-option label="文档" value="doc" /><el-option label="PPT" value="ppt" /><el-option label="PDF" value="pdf" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="getList">搜索</el-button></el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="list" border>
        <el-table-column prop="resourceName" label="资源名称" min-width="220" />
        <el-table-column prop="resourceType" label="类型" width="100" />
        <el-table-column prop="qualityScore" label="质量分" width="90" />
        <el-table-column label="操作" width="140"><template #default="scope"><el-button link type="primary" @click="viewDetail(scope.row.resourceId)">详情</el-button><el-button link type="success" @click="recordFavorite(scope.row.resourceId)">收藏</el-button></template></el-table-column>
      </el-table>
    </el-card>
    <el-card class="portal-card">
      <template #header><span>资源详情</span></template>
      <div v-if="detail.resourceId">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="资源名称">{{ detail.resourceName }}</el-descriptions-item>
          <el-descriptions-item label="资源类型">{{ detail.resourceType }}</el-descriptions-item>
          <el-descriptions-item label="摘要">{{ detail.summary || '-' }}</el-descriptions-item>
          <el-descriptions-item label="浏览量">{{ detail.viewCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="文件地址"><el-link v-if="detail.fileUrl" :href="detail.fileUrl" target="_blank" type="primary" @click="recordDownload(detail.resourceId)">打开资源</el-link></el-descriptions-item>
          <el-descriptions-item label="内容文本">{{ detail.contentText || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-empty v-else class="portal-empty" description="请选择一条资源查看详情" />
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchPortalCourseOptions, getResourceDetail, listResource, recordStudyBehavior } from '@/api/portal'
import usePortalUserStore from '@/store/user'
const userStore = usePortalUserStore()
const loading=ref(false), list=ref<any[]>([]), detail=ref<any>({}), courseOptions=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1, pageSize:20, auditStatus:'1', status:'0', resourceName:'', resourceType:'', courseId: undefined })
async function loadOptions(){ courseOptions.value = await fetchPortalCourseOptions() }
async function getList(){ loading.value=true; const res=await listResource(queryParams); list.value=res.rows||[]; loading.value=false }
async function recordBehavior(resourceId:number, behaviorType:string){ const userId=userStore.user?.userId; if(!userId) return; await recordStudyBehavior({ userId, courseId: detail.value.courseId || queryParams.courseId, resourceId, behaviorType, durationSeconds: 60, progressRate: 20, deviceType: 'web', sourcePage: 'student-resource' }) }
async function viewDetail(id:number){ const res=await getResourceDetail(id); detail.value=res.data||{}; await recordBehavior(id,'view') }
async function recordDownload(resourceId:number){ await recordBehavior(resourceId,'download'); ElMessage.success('已记录下载行为') }
async function recordFavorite(resourceId:number){ await recordBehavior(resourceId,'favorite'); ElMessage.success('已记录收藏行为') }
onMounted(async()=>{ await loadOptions(); await getList() })
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
