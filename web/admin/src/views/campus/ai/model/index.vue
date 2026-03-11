<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="模型名称"><el-input v-model="queryParams.modelName" style="width:220px" @keyup.enter="getList" /></el-form-item><el-form-item label="提供商"><el-input v-model="queryParams.provider" style="width:180px" /></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item></el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增模型</el-button></el-col></el-row>
    <el-table v-loading="loading" :data="dataList"><el-table-column label="模型ID" prop="modelId" width="90" /><el-table-column label="模型名称" prop="modelName" min-width="180" /><el-table-column label="提供商" prop="provider" width="120" /><el-table-column label="类型" prop="modelType" width="100" /><el-table-column label="状态" width="100"><template #default="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column><el-table-column label="操作" width="220"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button><el-button link type="primary" icon="Connection" @click="handleTest(scope.row)">测试</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="open" :title="form.modelId ? '编辑模型' : '新增模型'" width="620px"><el-form :model="form" label-width="90px"><el-form-item label="模型名称"><el-input v-model="form.modelName" placeholder="如 deepseek-chat / gpt-4o-mini" /></el-form-item><el-form-item label="提供商"><el-select v-model="form.provider" @change="handleProviderChange"><el-option label="DeepSeek（OpenAI兼容）" value="deepseek" /><el-option label="OpenAI" value="openai" /><el-option label="自定义兼容协议" value="custom" /></el-select></el-form-item><el-form-item label="地址"><el-input v-model="form.baseUrl" placeholder="如 https://api.deepseek.com 或 https://api.openai.com/v1" /></el-form-item><el-form-item label="Key"><el-input v-model="form.apiKey" show-password /></el-form-item><el-form-item label="类型"><el-select v-model="form.modelType"><el-option label="Chat" value="chat" /><el-option label="Embedding" value="embedding" /><el-option label="Rerank" value="rerank" /></el-select></el-form-item><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-form><template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template></el-dialog>
    <el-dialog v-model="testOpen" title="模型测试结果" width="720px"><el-form label-width="90px"><el-form-item label="提示词"><el-input v-model="testPrompt" type="textarea" rows="3" /></el-form-item><el-form-item><el-button type="primary" :loading="testLoading" @click="submitTest">发送测试</el-button></el-form-item><el-form-item label="返回结果"><el-input :model-value="testResult" type="textarea" rows="10" readonly /></el-form-item></el-form></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addAiModel, delAiModel, listAiModel, testAiModel, updateAiModel } from '@/api/campus/ai'
const loading=ref(false), total=ref(0), open=ref(false), testOpen=ref(false), testLoading=ref(false), dataList=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,modelName:'',provider:'' })
const form=reactive<any>({ modelId: undefined, modelName:'',provider:'',baseUrl:'',apiKey:'',modelType:'chat',status:'0'})
const currentTestModelId = ref<number | undefined>()
const testPrompt = ref('请简单介绍一下你当前模型连接是否正常。')
const testResult = ref('')
async function getList(){ loading.value=true; const res=await listAiModel(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetForm(){ Object.assign(form,{ modelId: undefined, modelName:'',provider:'',baseUrl:'',apiKey:'',modelType:'chat',status:'0'}) }
function handleProviderChange(value:string){ if(value === 'deepseek' && !form.baseUrl){ form.baseUrl = 'https://api.deepseek.com'; if(!form.modelName){ form.modelName = 'deepseek-chat' } } if(value === 'openai' && !form.baseUrl){ form.baseUrl = 'https://api.openai.com/v1'; if(!form.modelName){ form.modelName = 'gpt-4o-mini' } } }
function handleAdd(){ resetForm(); open.value=true }
function handleEdit(row:any){ Object.assign(form,row); open.value=true }
async function handleDelete(row:any){ await ElMessageBox.confirm('确认删除该模型吗？','提示',{type:'warning'}); await delAiModel(row.modelId); ElMessage.success('删除成功'); getList() }
function handleTest(row:any){ currentTestModelId.value = row.modelId; testResult.value=''; testOpen.value=true }
async function submitTest(){ if(!currentTestModelId.value) return; testLoading.value=true; try{ const res = await testAiModel({ modelId: currentTestModelId.value, prompt: testPrompt.value }); testResult.value = res.data?.content || '无返回内容' } finally { testLoading.value=false } }
async function submitForm(){ if(form.modelId){ await updateAiModel(form); ElMessage.success('修改成功') } else { await addAiModel(form); ElMessage.success('新增成功') } open.value=false; getList() }
getList()
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
