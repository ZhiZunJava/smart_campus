<template>
  <div class="portal-page">
    <el-card class="portal-card">
      <template #header><span>教学资源</span></template>
      <el-form :inline="true" :model="queryParams" class="mb16">
        <el-form-item label="教师"><el-select v-model="queryParams.uploaderId" filterable clearable style="width:240px"><el-option v-for="item in teacherOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="资源名称"><el-input v-model="queryParams.resourceName" style="width:220px" @keyup.enter="getList" /></el-form-item>
        <el-form-item><el-button type="primary" @click="getList">搜索</el-button></el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="resources" border>
        <el-table-column prop="resourceId" label="资源ID" width="90" />
        <el-table-column prop="resourceName" label="资源名称" min-width="220" />
        <el-table-column prop="resourceType" label="类型" width="100" />
        <el-table-column prop="viewCount" label="浏览" width="90" />
        <el-table-column prop="downloadCount" label="下载" width="90" />
        <el-table-column prop="favoriteCount" label="收藏" width="90" />
      </el-table>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { fetchPortalUserOptions, listResource } from '@/api/portal'
import usePortalUserStore from '@/store/user'
const userStore = usePortalUserStore()
const loading=ref(false), resources=ref<any[]>([]), teacherOptions=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1, pageSize:20, uploaderId: undefined, resourceName:'', status:'0' })
async function loadOptions(){ teacherOptions.value = await fetchPortalUserOptions('teacher'); queryParams.uploaderId = userStore.user?.userId || teacherOptions.value[0]?.value }
async function getList(){ loading.value=true; const res=await listResource(queryParams); resources.value=res.rows||[]; loading.value=false }
onMounted(async()=>{ await loadOptions(); await getList() })
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
