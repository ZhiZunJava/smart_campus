<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="用户ID"><el-input v-model="queryParams.userId" style="width:180px" /></el-form-item><el-form-item label="课程ID"><el-input v-model="queryParams.courseId" style="width:180px" /></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item></el-form>
    <el-table v-loading="loading" :data="dataList"><el-table-column label="ID" prop="id" width="90" /><el-table-column label="用户ID" prop="userId" width="90" /><el-table-column label="题目ID" prop="questionId" width="90" /><el-table-column label="课程ID" prop="courseId" width="90" /><el-table-column label="错误次数" prop="wrongCount" width="100" /><el-table-column label="最后做错时间" prop="lastWrongTime" min-width="180" /><el-table-column label="掌握状态" prop="masteryStatus" width="100" /></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { listWrong } from '@/api/campus/exam'
const loading=ref(false), total=ref(0), dataList=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,userId:undefined,courseId:undefined })
async function getList(){ loading.value=true; const res=await listWrong(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
getList()
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
