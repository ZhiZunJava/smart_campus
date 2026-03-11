<template>
  <div class="portal-page portal-grid portal-grid-2">
    <el-card class="portal-card">
      <template #header><span>我的课程</span></template>
      <el-table v-loading="loading" :data="courses" border>
        <el-table-column prop="courseId" label="课程ID" width="90" />
        <el-table-column prop="courseName" label="课程名称" min-width="180" />
        <el-table-column prop="courseCode" label="课程编码" width="120" />
        <el-table-column prop="subjectType" label="学科类型" width="120" />
        <el-table-column label="操作" width="100"><template #default="scope"><el-button link type="primary" @click="selectCourse(scope.row)">详情</el-button></template></el-table-column>
      </el-table>
    </el-card>
    <el-card class="portal-card">
      <template #header><span>课程详情</span></template>
      <div v-if="detail.courseId">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="课程名称">{{ detail.courseName }}</el-descriptions-item>
          <el-descriptions-item label="课程编码">{{ detail.courseCode }}</el-descriptions-item>
          <el-descriptions-item label="学科类型">{{ detail.subjectType }}</el-descriptions-item>
          <el-descriptions-item label="课程简介">{{ detail.intro || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-empty v-else class="portal-empty" description="请选择一门课程查看详情" />
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { listCourse } from '@/api/portal'
import usePortalUserStore from '@/store/user'
const userStore = usePortalUserStore()
const loading=ref(false), courses=ref<any[]>([]), detail=ref<any>({})
async function getList(){ const teacherId = userStore.user?.userId; if(!teacherId) return; loading.value=true; const res=await listCourse({ pageNum:1, pageSize:50, teacherId }); courses.value=res.rows||[]; loading.value=false }
function selectCourse(row:any){ detail.value = row }
getList()
</script>
