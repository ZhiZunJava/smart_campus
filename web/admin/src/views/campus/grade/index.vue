<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="年级名称"><el-input v-model="queryParams.gradeName" style="width:220px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="学年"><el-input v-model="queryParams.schoolYear" style="width:180px" /></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col><el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col><el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col><right-toolbar v-model:showSearch="showSearch" @queryTable="getList" /></el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange"><el-table-column type="selection" width="55" /><el-table-column label="ID" prop="gradeId" width="90" /><el-table-column label="年级名称" prop="gradeName" min-width="160" /><el-table-column label="学年" prop="schoolYear" width="140" /><el-table-column label="状态" prop="status" width="100" /><el-table-column label="操作" width="150" fixed="right"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="open" :title="title" width="520px"><el-form :model="form" label-width="90px"><el-form-item label="年级名称"><el-input v-model="form.gradeName" /></el-form-item><el-form-item label="学年"><el-input v-model="form.schoolYear" /></el-form-item><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item></el-form><template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addGrade, delGrade, listGrade, updateGrade } from '@/api/campus/teaching'
const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,gradeName:'',schoolYear:'' })
const form=reactive<any>({})
function resetFormData(){ Object.assign(form,{ gradeId:undefined,gradeName:'',schoolYear:'',status:'0',remark:''}) }
async function getList(){ loading.value=true; const res=await listGrade(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.gradeName=''; queryParams.schoolYear=''; getList() }
function handleSelectionChange(selection:any[]){ ids.value=selection.map(i=>i.gradeId); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetFormData(); title.value='新增年级'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.gradeId===ids.value[0]); if(!item) return; Object.assign(form,item); title.value='修改年级'; open.value=true }
async function submitForm(){ if(form.gradeId){ await updateGrade(form); ElMessage.success('修改成功') } else { await addGrade(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.gradeId || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选年级吗？','提示',{type:'warning'}); await delGrade(target); ElMessage.success('删除成功'); getList() }
resetFormData(); getList()
</script>
<style scoped>.mb16{margin-bottom:16px;}</style>
