<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="班级名称"><el-input v-model="queryParams.className" style="width:220px" @keyup.enter="getList" /></el-form-item><el-form-item label="年级"><el-select v-model="queryParams.gradeId" filterable clearable style="width:220px"><el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item></el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col><el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col><el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col><right-toolbar v-model:showSearch="showSearch" @queryTable="getList" /></el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange"><el-table-column type="selection" width="55" /><el-table-column label="ID" prop="classId" width="90" /><el-table-column label="年级ID" prop="gradeId" width="90" /><el-table-column label="班级名称" prop="className" min-width="160" /><el-table-column label="班主任ID" prop="headTeacherId" width="100" /><el-table-column label="状态" width="100"><template #default="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column><el-table-column label="操作" width="150" fixed="right"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="open" :title="title" width="560px"><el-form :model="form" label-width="90px"><el-form-item label="年级"><el-select v-model="form.gradeId" filterable clearable><el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item label="班级名称"><el-input v-model="form.className" /></el-form-item><el-form-item label="班主任"><el-select v-model="form.headTeacherId" filterable clearable><el-option v-for="item in teacherOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item></el-form><template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addClass, delClass, listClass, updateClass } from '@/api/campus/teaching'
import { fetchGradeOptions, fetchUserOptions } from '@/api/campus/options'
const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const gradeOptions=ref<any[]>([])
const teacherOptions=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,className:'',gradeId:undefined })
const form=reactive<any>({})
function resetFormData(){ Object.assign(form,{ classId:undefined,gradeId:undefined,className:'',headTeacherId:undefined,status:'0',remark:''}) }
async function getList(){ loading.value=true; const res=await listClass(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.className=''; queryParams.gradeId=undefined; getList() }
function handleSelectionChange(selection:any[]){ ids.value=selection.map(i=>i.classId); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetFormData(); title.value='新增班级'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.classId===ids.value[0]); if(!item) return; Object.assign(form,item); title.value='修改班级'; open.value=true }
async function submitForm(){ if(form.classId){ await updateClass(form); ElMessage.success('修改成功') } else { await addClass(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.classId || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选班级吗？','提示',{type:'warning'}); await delClass(target); ElMessage.success('删除成功'); getList() }
async function loadOptions(){ gradeOptions.value = await fetchGradeOptions(); teacherOptions.value = await fetchUserOptions('teacher') }
onMounted(async()=>{ await loadOptions(); resetFormData(); getList() })
</script>
<style scoped>.mb16{margin-bottom:16px;}</style>
