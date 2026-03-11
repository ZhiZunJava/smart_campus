<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="用户ID"><el-input v-model="queryParams.userId" style="width:180px" /></el-form-item><el-form-item label="试卷ID"><el-input v-model="queryParams.paperId" style="width:180px" /></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item></el-form>
    <el-table v-loading="loading" :data="recordList"><el-table-column label="记录ID" prop="recordId" width="100" /><el-table-column label="试卷ID" prop="paperId" width="90" /><el-table-column label="用户ID" prop="userId" width="90" /><el-table-column label="得分" prop="score" width="100" /><el-table-column label="正确率" prop="correctRate" width="100" /><el-table-column label="状态" prop="examStatus" width="120" /><el-table-column label="开始时间" prop="startTime" min-width="180" /><el-table-column label="提交时间" prop="submitTime" min-width="180" /><el-table-column label="操作" width="120"><template #default="scope"><el-button link type="primary" icon="View" @click="handleViewAnswer(scope.row)">查看答案</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="answerOpen" title="作答详情" width="860px">
      <el-table :data="answerList" border>
        <el-table-column label="题目ID" prop="questionId" width="90" />
        <el-table-column label="用户答案" prop="userAnswer" min-width="220" show-overflow-tooltip />
        <el-table-column label="是否正确" prop="isCorrect" width="100" />
        <el-table-column label="得分" prop="score" width="100" />
        <el-table-column label="知识点ID" prop="knowledgePointId" width="120" />
      </el-table>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { listAnswer, listRecord } from '@/api/campus/exam'
const loading=ref(false), total=ref(0), recordList=ref<any[]>([]), answerOpen=ref(false), answerList=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,userId:undefined,paperId:undefined })
async function getList(){ loading.value=true; const res=await listRecord(queryParams); recordList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
async function handleViewAnswer(row:any){ const res=await listAnswer({ recordId: row.recordId, pageNum:1, pageSize:100 }); answerList.value=res.rows||[]; answerOpen.value=true }
getList()
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
