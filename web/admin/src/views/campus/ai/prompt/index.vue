<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="模板名称"><el-input v-model="queryParams.templateName" style="width:220px" @keyup.enter="getList" /></el-form-item><el-form-item label="业务类型"><el-input v-model="queryParams.bizType" style="width:180px" /></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item></el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增模板</el-button></el-col></el-row>
    <el-table v-loading="loading" :data="dataList"><el-table-column label="模板ID" prop="templateId" width="90" /><el-table-column label="模板编码" prop="templateCode" width="140" /><el-table-column label="模板名称" prop="templateName" min-width="180" /><el-table-column label="业务类型" prop="bizType" width="120" /><el-table-column label="版本" prop="version" width="80" /><el-table-column label="状态" width="100"><template #default="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column><el-table-column label="操作" width="220"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button><el-button link type="primary" icon="Connection" @click="handleTest(scope.row)">测试</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="open" :title="form.templateId ? '编辑模板' : '新增模板'" width="760px"><el-form :model="form" label-width="90px"><el-form-item label="模板编码"><el-input v-model="form.templateCode" /></el-form-item><el-form-item label="模板名称"><el-input v-model="form.templateName" /></el-form-item><el-form-item label="业务类型"><el-input v-model="form.bizType" placeholder="如 qa / report / recommend" /></el-form-item><el-form-item label="版本"><el-input-number v-model="form.version" :min="1" :max="20" /></el-form-item><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item><el-form-item label="模板内容"><el-input v-model="form.promptContent" type="textarea" rows="8" placeholder="这里填写系统提示词模板，测试时会作为 system prompt 使用" /></el-form-item></el-form><template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template></el-dialog>
    <el-dialog v-model="testOpen" title="Prompt 测试" width="760px"><el-form label-width="90px"><el-form-item label="模型ID"><el-input v-model="testForm.modelId" placeholder="请输入模型ID" /></el-form-item><el-form-item label="测试问题"><el-input v-model="testForm.userPrompt" type="textarea" rows="3" placeholder="输入一段测试问题" /></el-form-item><el-form-item><el-button type="primary" :loading="testLoading" @click="submitTest">开始测试</el-button></el-form-item><el-form-item label="返回结果"><el-input :model-value="testResult" type="textarea" rows="10" readonly /></el-form-item></el-form></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addAiPrompt, delAiPrompt, listAiPrompt, testAiPrompt, updateAiPrompt } from '@/api/campus/ai'
const loading=ref(false), total=ref(0), open=ref(false), testOpen=ref(false), testLoading=ref(false), dataList=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,templateName:'',bizType:'' })
const form=reactive<any>({ templateId: undefined, templateCode:'',templateName:'',bizType:'',promptContent:'',version:1,status:'0'})
const testForm=reactive<any>({ templateId: undefined, modelId: undefined, userPrompt: '请介绍一下这个提示词是否工作正常。' })
const testResult=ref('')
async function getList(){ loading.value=true; const res=await listAiPrompt(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetForm(){ Object.assign(form,{ templateId: undefined, templateCode:'',templateName:'',bizType:'',promptContent:'',version:1,status:'0'}) }
function handleAdd(){ resetForm(); open.value=true }
function handleEdit(row:any){ Object.assign(form,row); open.value=true }
async function handleDelete(row:any){ await ElMessageBox.confirm('确认删除该模板吗？','提示',{type:'warning'}); await delAiPrompt(row.templateId); ElMessage.success('删除成功'); getList() }
function handleTest(row:any){ testForm.templateId = row.templateId; testResult.value=''; testOpen.value=true }
async function submitTest(){ testLoading.value=true; try{ const res=await testAiPrompt(testForm); testResult.value = res.data?.content || '无返回内容' } finally { testLoading.value=false } }
async function submitForm(){ if(form.templateId){ await updateAiPrompt(form); ElMessage.success('修改成功') } else { await addAiPrompt(form); ElMessage.success('新增成功') } open.value=false; getList() }
getList()
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
