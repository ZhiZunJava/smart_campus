<template>
  <div class="portal-page">
    <el-card class="portal-card">
      <template #header><span>我的错题本</span></template>
      <el-table v-loading="loading" :data="wrongs" border>
        <el-table-column prop="questionId" label="题目ID" width="90" />
        <el-table-column prop="courseId" label="课程ID" width="90" />
        <el-table-column prop="wrongCount" label="错误次数" width="100" />
        <el-table-column prop="lastWrongTime" label="最后做错时间" min-width="180" />
        <el-table-column label="掌握状态" width="100"><template #default="scope"><el-tag :type="scope.row.masteryStatus === '1' ? 'success' : 'warning'">{{ scope.row.masteryStatus === '1' ? '已掌握' : '未掌握' }}</el-tag></template></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { listWrongBook } from '@/api/portal'
import usePortalUserStore from '@/store/user'
const userStore = usePortalUserStore()
const loading=ref(false), wrongs=ref<any[]>([])
async function getList(){ const userId = userStore.user?.userId; if(!userId) return; loading.value=true; const res=await listWrongBook({ pageNum:1, pageSize:50, userId }); wrongs.value=res.rows||[]; loading.value=false }
getList()
</script>
