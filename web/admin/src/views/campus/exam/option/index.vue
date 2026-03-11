<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="题目ID"><el-input v-model="queryParams.questionId" style="width:180px" /></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item></el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增选项</el-button></el-col><el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col><el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col><right-toolbar v-model:showSearch="showSearch" @queryTable="getList" /></el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange"><el-table-column type="selection" width="55" /><el-table-column label="选项ID" prop="optionId" width="90" /><el-table-column label="题目ID" prop="questionId" width="90" /><el-table-column label="选项标识" prop="optionKey" width="100" /><el-table-column label="选项内容" prop="optionContent" min-width="260" show-overflow-tooltip /><el-table-column label="正确" width="90"><template #default="scope"><el-tag :type="scope.row.isRight === '1' ? 'success' : 'info'">{{ scope.row.isRight === '1' ? '是' : '否' }}</el-tag></template></el-table-column><el-table-column label="操作" width="150"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="open" :title="title" width="560px"><el-form :model="form" label-width="90px"><el-form-item label="题目ID"><el-input v-model="form.questionId" /></el-form-item><el-form-item label="选项标识"><el-input v-model="form.optionKey" /></el-form-item><el-form-item label="选项内容"><el-input v-model="form.optionContent" type="textarea" rows="4" /></el-form-item><el-form-item label="是否正确"><el-select v-model="form.isRight"><el-option label="否" value="0" /><el-option label="是" value="1" /></el-select></el-form-item></el-form><template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addOption, listOption, updateOption, delOption } from '@/api/campus/exam'
const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,questionId:undefined })
const form=reactive<any>({})
function resetForm(){ Object.assign(form,{ optionId:undefined,questionId:undefined,optionKey:'',optionContent:'',isRight:'0'}) }
async function getList(){ loading.value=true; const res=await listOption(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function handleSelectionChange(selection:any[]){ ids.value=selection.map(i=>i.optionId); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetForm(); title.value='新增选项'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.optionId===ids.value[0]); if(!item) return; Object.assign(form,item); title.value='修改选项'; open.value=true }
async function submitForm(){ if(form.optionId){ await updateOption(form); ElMessage.success('修改成功') } else { await addOption(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.optionId || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选选项吗？','提示',{type:'warning'}); await delOption(target); ElMessage.success('删除成功'); getList() }
resetForm(); getList()
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
