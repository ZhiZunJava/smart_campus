<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="用户ID"><el-input v-model="queryParams.userId" style="width:180px" /></el-form-item><el-form-item label="课程ID"><el-input v-model="queryParams.courseId" style="width:180px" /></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item></el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增会话</el-button></el-col></el-row>
    <el-table v-loading="loading" :data="dataList"><el-table-column label="会话ID" prop="sessionId" width="90" /><el-table-column label="用户ID" prop="userId" width="90" /><el-table-column label="课程ID" prop="courseId" width="90" /><el-table-column label="会话标题" prop="sessionTitle" min-width="200" /><el-table-column label="场景类型" prop="sourceType" width="120" /><el-table-column label="状态" prop="status" width="100" /><el-table-column label="操作" width="160"><template #default="scope"><el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="open" :title="title" width="560px"><el-form :model="form" label-width="90px"><el-form-item label="用户ID"><el-input v-model="form.userId" /></el-form-item><el-form-item label="课程ID"><el-input v-model="form.courseId" /></el-form-item><el-form-item label="会话标题"><el-input v-model="form.sessionTitle" /></el-form-item><el-form-item label="场景类型"><el-select v-model="form.sourceType"><el-option label="通用" value="general" /><el-option label="课程" value="course" /><el-option label="错题" value="wrong_question" /></el-select></el-form-item><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="结束" value="1" /></el-select></el-form-item></el-form><template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template></el-dialog>
    <el-dialog v-model="detailOpen" title="会话详情" width="860px"><el-descriptions :column="2" border><el-descriptions-item label="会话标题">{{ detail.sessionTitle }}</el-descriptions-item><el-descriptions-item label="场景类型">{{ detail.sourceType }}</el-descriptions-item><el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item><el-descriptions-item label="课程ID">{{ detail.courseId }}</el-descriptions-item></el-descriptions><el-table :data="detail.messages || []" class="mt16"><el-table-column label="消息ID" prop="messageId" width="90" /><el-table-column label="角色" prop="roleType" width="100" /><el-table-column label="内容" prop="content" min-width="280" show-overflow-tooltip /><el-table-column label="来源" prop="referenceSource" min-width="200" show-overflow-tooltip /></el-table></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addQaSession, getQaSessionDetail, listQaSession } from '@/api/campus/qa'
const loading=ref(false), total=ref(0), open=ref(false), detailOpen=ref(false), title=ref(''), dataList=ref<any[]>([]), detail=ref<any>({})
const queryParams=reactive<any>({ pageNum:1,pageSize:10,userId:undefined,courseId:undefined })
const form=reactive<any>({ userId:undefined,courseId:undefined,sessionTitle:'',sourceType:'course',status:'0' })
async function getList(){ loading.value=true; const res=await listQaSession(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function handleAdd(){ title.value='新增会话'; open.value=true }
async function submitForm(){ await addQaSession(form); ElMessage.success('新增成功'); open.value=false; getList() }
async function handleView(row:any){ const res=await getQaSessionDetail(row.sessionId); detail.value=res.data||{}; detailOpen.value=true }
getList()
</script>
<style scoped>.mb16{margin-bottom:16px}.mt16{margin-top:16px}</style>
