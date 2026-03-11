<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="消息ID"><el-input v-model="queryParams.messageId" style="width:180px" /></el-form-item>
      <el-form-item label="用户ID"><el-input v-model="queryParams.userId" style="width:180px" /></el-form-item>
      <el-form-item label="反馈类型"><el-select v-model="queryParams.feedbackType" clearable style="width:180px"><el-option label="有帮助" value="helpful" /><el-option label="无帮助" value="unhelpful" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增反馈</el-button></el-col></el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="反馈ID" prop="id" width="90" />
      <el-table-column label="消息ID" prop="messageId" width="90" />
      <el-table-column label="用户ID" prop="userId" width="90" />
      <el-table-column label="反馈类型" prop="feedbackType" width="120" />
      <el-table-column label="反馈内容" prop="feedbackContent" min-width="260" show-overflow-tooltip />
      <el-table-column label="创建时间" prop="createTime" min-width="180" />
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" title="新增反馈" width="620px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="消息ID"><el-input v-model="form.messageId" /></el-form-item>
        <el-form-item label="用户ID"><el-input v-model="form.userId" /></el-form-item>
        <el-form-item label="反馈类型"><el-select v-model="form.feedbackType"><el-option label="有帮助" value="helpful" /><el-option label="无帮助" value="unhelpful" /></el-select></el-form-item>
        <el-form-item label="反馈内容"><el-input v-model="form.feedbackContent" type="textarea" rows="5" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addQaFeedback, listQaFeedback } from '@/api/campus/qa'
const loading=ref(false), total=ref(0), open=ref(false), dataList=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,messageId:undefined,userId:undefined,feedbackType:'' })
const form=reactive<any>({ messageId:undefined,userId:undefined,feedbackType:'helpful',feedbackContent:'' })
async function getList(){ loading.value=true; const res=await listQaFeedback(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.messageId=undefined; queryParams.userId=undefined; queryParams.feedbackType=''; getList() }
function handleAdd(){ open.value=true }
async function submitForm(){ await addQaFeedback(form); ElMessage.success('提交成功'); open.value=false; form.feedbackContent=''; getList() }
getList()
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
