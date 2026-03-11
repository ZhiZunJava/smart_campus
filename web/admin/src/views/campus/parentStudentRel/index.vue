<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="家长ID"><el-input v-model="queryParams.parentUserId" style="width:180px" /></el-form-item><el-form-item label="学生ID"><el-input v-model="queryParams.studentUserId" style="width:180px" /></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item></el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增绑定</el-button></el-col><el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col><el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col><right-toolbar v-model:showSearch="showSearch" @queryTable="getList" /></el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange"><el-table-column type="selection" width="55" /><el-table-column label="ID" prop="id" width="90" /><el-table-column label="家长ID" prop="parentUserId" width="100" /><el-table-column label="学生ID" prop="studentUserId" width="100" /><el-table-column label="关系" prop="relationType" width="120" /><el-table-column label="状态" prop="status" width="100" /><el-table-column label="操作" width="150"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="open" :title="title" width="520px"><el-form :model="form" label-width="90px"><el-form-item label="家长ID"><el-input v-model="form.parentUserId" /></el-form-item><el-form-item label="学生ID"><el-input v-model="form.studentUserId" /></el-form-item><el-form-item label="关系"><el-select v-model="form.relationType"><el-option label="父亲" value="father" /><el-option label="母亲" value="mother" /><el-option label="监护人" value="guardian" /></el-select></el-form-item><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-form><template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addParentStudentRel, delParentStudentRel, getParentStudentRel, listParentStudentRel, updateParentStudentRel } from '@/api/campus/identity'
const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,parentUserId:undefined,studentUserId:undefined })
const form=reactive<any>({})
function resetForm(){ Object.assign(form,{ id:undefined,parentUserId:undefined,studentUserId:undefined,relationType:'guardian',status:'0'}) }
async function getList(){ loading.value=true; const res=await listParentStudentRel(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.parentUserId=undefined; queryParams.studentUserId=undefined; getList() }
function handleSelectionChange(selection:any[]){ ids.value=selection.map(i=>i.id); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetForm(); title.value='新增绑定'; open.value=true }
async function handleUpdate(row?:any){ resetForm(); const id=row?.id || ids.value[0]; if(!id) return; const res=await getParentStudentRel(id); Object.assign(form,res.data||{}); title.value='修改绑定'; open.value=true }
async function submitForm(){ if(form.id){ await updateParentStudentRel(form); ElMessage.success('修改成功') } else { await addParentStudentRel(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.id || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选绑定关系吗？','提示',{type:'warning'}); await delParentStudentRel(target); ElMessage.success('删除成功'); getList() }
resetForm(); getList()
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
