<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="课程名称"><el-input v-model="queryParams.courseName" style="width:220px" @keyup.enter="getList" /></el-form-item><el-form-item label="教师"><el-select v-model="queryParams.teacherId" filterable clearable style="width:220px"><el-option v-for="item in teacherOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item></el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col><el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col><el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col><right-toolbar v-model:showSearch="showSearch" @queryTable="getList" /></el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange"><el-table-column type="selection" width="55" /><el-table-column label="ID" prop="courseId" width="90" /><el-table-column label="课程名称" prop="courseName" min-width="180" /><el-table-column label="课程编码" prop="courseCode" width="120" /><el-table-column label="学科类型" prop="subjectType" width="120" /><el-table-column label="教师ID" prop="teacherId" width="90" /><el-table-column label="年级ID" prop="gradeId" width="90" /><el-table-column label="状态" width="100"><template #default="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column><el-table-column label="操作" width="150" fixed="right"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="open" :title="title" width="640px"><el-form :model="form" label-width="90px"><el-row :gutter="16"><el-col :span="12"><el-form-item label="课程名称"><el-input v-model="form.courseName" /></el-form-item></el-col><el-col :span="12"><el-form-item label="课程编码"><el-input v-model="form.courseCode" /></el-form-item></el-col><el-col :span="12"><el-form-item label="学科类型"><el-input v-model="form.subjectType" /></el-form-item></el-col><el-col :span="12"><el-form-item label="教师"><el-select v-model="form.teacherId" filterable clearable><el-option v-for="item in teacherOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col><el-col :span="12"><el-form-item label="年级"><el-select v-model="form.gradeId" filterable clearable><el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col><el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-col><el-col :span="24"><el-form-item label="简介"><el-input v-model="form.intro" type="textarea" rows="3" /></el-form-item></el-col></el-row></el-form><template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addCourse, delCourse, listCourse, updateCourse } from '@/api/campus/teaching'
import { fetchGradeOptions, fetchUserOptions } from '@/api/campus/options'
const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const teacherOptions=ref<any[]>([])
const gradeOptions=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,courseName:'',teacherId:undefined })
const form=reactive<any>({})
function resetFormData(){ Object.assign(form,{ courseId:undefined,courseName:'',courseCode:'',subjectType:'',teacherId:undefined,gradeId:undefined,intro:'',status:'0'}) }
async function getList(){ loading.value=true; const res=await listCourse(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.courseName=''; queryParams.teacherId=undefined; getList() }
function handleSelectionChange(selection:any[]){ ids.value=selection.map(i=>i.courseId); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetFormData(); title.value='新增课程'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.courseId===ids.value[0]); if(!item) return; Object.assign(form,item); title.value='修改课程'; open.value=true }
async function submitForm(){ if(form.courseId){ await updateCourse(form); ElMessage.success('修改成功') } else { await addCourse(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.courseId || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选课程吗？','提示',{type:'warning'}); await delCourse(target); ElMessage.success('删除成功'); getList() }
async function loadOptions(){ teacherOptions.value = await fetchUserOptions('teacher'); gradeOptions.value = await fetchGradeOptions() }
onMounted(async()=>{ await loadOptions(); resetFormData(); getList() })
</script>
<style scoped>.mb16{margin-bottom:16px;}</style>
