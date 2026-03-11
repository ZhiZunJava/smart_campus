<template>
  <div class="portal-page">
    <el-card class="portal-card">
      <template #header><span>孩子预警</span></template>
      <el-form :inline="true" :model="queryParams" class="mb16">
        <el-form-item label="学生"><el-select v-model="queryParams.userId" filterable clearable style="width:240px"><el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="getList">搜索</el-button></el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="warnings" border>
        <el-table-column prop="warningId" label="预警ID" width="90" />
        <el-table-column prop="warningType" label="预警类型" width="120" />
        <el-table-column prop="warningLevel" label="预警等级" width="100" />
        <el-table-column prop="warningContent" label="预警内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="suggestion" label="干预建议" min-width="260" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getParentDashboard, fetchPortalUserOptions, listWarning } from '@/api/portal'
import usePortalUserStore from '@/store/user'
const userStore = usePortalUserStore()
const loading=ref(false), warnings=ref<any[]>([]), studentOptions=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1, pageSize:50, userId: undefined })
async function loadOptions(){ studentOptions.value = await fetchPortalUserOptions('student'); const parentUserId = userStore.user?.userId; if(parentUserId){ try{ const res = await getParentDashboard({ parentUserId }); queryParams.userId = res.data?.studentUserId || studentOptions.value[0]?.value }catch{ queryParams.userId = studentOptions.value[0]?.value } } else { queryParams.userId = studentOptions.value[0]?.value } }
async function getList(){ if(!queryParams.userId) return; loading.value=true; const res=await listWarning(queryParams); warnings.value=res.rows||[]; loading.value=false }
onMounted(async()=>{ await loadOptions(); await getList() })
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
