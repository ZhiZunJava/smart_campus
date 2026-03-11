<template>
  <div class="portal-page">
    <el-card class="portal-card">
      <template #header><span>学情预警</span></template>
      <el-table v-loading="loading" :data="warnings" border>
        <el-table-column prop="warningId" label="预警ID" width="90" />
        <el-table-column prop="userId" label="学生ID" width="90" />
        <el-table-column prop="warningType" label="预警类型" width="120" />
        <el-table-column label="预警等级" width="100"><template #default="scope"><el-tag :type="scope.row.warningLevel === 'HIGH' ? 'danger' : scope.row.warningLevel === 'MEDIUM' ? 'warning' : 'success'">{{ scope.row.warningLevel }}</el-tag></template></el-table-column>
        <el-table-column prop="warningContent" label="预警内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="suggestion" label="建议" min-width="260" show-overflow-tooltip />
      </el-table>
      <el-empty v-if="!loading && warnings.length === 0" class="portal-empty" description="暂无预警信息" />
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { listWarning } from '@/api/portal'
const loading=ref(false), warnings=ref<any[]>([])
async function getList(){ loading.value=true; const res=await listWarning({ pageNum:1, pageSize:50 }); warnings.value=res.rows||[]; loading.value=false }
getList()
</script>
