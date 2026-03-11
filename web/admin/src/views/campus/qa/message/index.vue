<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="会话ID"><el-input v-model="queryParams.sessionId" style="width:180px" /></el-form-item><el-form-item label="角色"><el-select v-model="queryParams.roleType" clearable style="width:180px"><el-option label="用户" value="user" /><el-option label="助手" value="assistant" /><el-option label="系统" value="system" /></el-select></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item></el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增消息</el-button></el-col></el-row>
    <el-table v-loading="loading" :data="dataList"><el-table-column label="消息ID" prop="messageId" width="90" /><el-table-column label="会话ID" prop="sessionId" width="90" /><el-table-column label="角色" prop="roleType" width="100" /><el-table-column label="内容" prop="content" min-width="280" show-overflow-tooltip /><el-table-column label="来源" prop="referenceSource" min-width="220" show-overflow-tooltip /><el-table-column label="Token" prop="tokenCount" width="80" /><el-table-column label="耗时" prop="latencyMs" width="80" /></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="open" title="新增消息" width="760px"><el-form :model="form" label-width="90px"><el-form-item label="会话ID"><el-input v-model="form.sessionId" /></el-form-item><el-form-item label="角色"><el-select v-model="form.roleType"><el-option label="用户" value="user" /><el-option label="助手" value="assistant" /><el-option label="系统" value="system" /></el-select></el-form-item><el-form-item label="内容"><el-input v-model="form.content" type="textarea" rows="6" /></el-form-item><el-form-item label="引用来源"><el-input v-model="form.referenceSource" type="textarea" rows="3" /></el-form-item></el-form><template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addQaMessage, listQaMessage } from '@/api/campus/qa'
const loading=ref(false), total=ref(0), open=ref(false), dataList=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,sessionId:undefined,roleType:'' })
const form=reactive<any>({ sessionId:undefined,roleType:'assistant',content:'',referenceSource:'',tokenCount:0,latencyMs:0,sensitiveFlag:'0' })
async function getList(){ loading.value=true; const res=await listQaMessage(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function handleAdd(){ open.value=true }
async function submitForm(){ await addQaMessage(form); ElMessage.success('新增成功'); open.value=false; getList() }
getList()
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
