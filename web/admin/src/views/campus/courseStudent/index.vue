<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width:240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="学生"><el-select v-model="queryParams.studentUserId" filterable clearable style="width:240px"><el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col><el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col><el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col><right-toolbar v-model:showSearch="showSearch" @queryTable="getList" /></el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange"><el-table-column type="selection" width="55" /><el-table-column label="ID" prop="id" width="90" /><el-table-column label="课程ID" prop="courseId" width="90" /><el-table-column label="学生ID" prop="studentUserId" width="90" /><el-table-column label="班级ID" prop="classId" width="90" /><el-table-column label="加入时间" prop="joinTime" min-width="180" /><el-table-column label="状态" prop="status" width="100" /><el-table-column label="操作" width="150"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="open" :title="title" width="560px"><el-form :model="form" label-width="90px"><el-form-item label="课程"><el-select v-model="form.courseId" filterable clearable><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item label="学生"><el-select v-model="form.studentUserId" filterable clearable><el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item label="班级"><el-select v-model="form.classId" filterable clearable><el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item></el-form><template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addCourseStudent, delCourseStudent, listCourseStudent, updateCourseStudent } from '@/api/campus/courseStudent'
import { fetchClassOptions, fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'
const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const courseOptions=ref<any[]>([])
const studentOptions=ref<any[]>([])
const classOptions=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,courseId:undefined,studentUserId:undefined })
const form=reactive<any>({})
function resetForm(){ Object.assign(form,{ id:undefined,courseId:undefined,studentUserId:undefined,classId:undefined,status:'0',remark:'' }) }
async function getList(){ loading.value=true; const res=await listCourseStudent(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.courseId=undefined; queryParams.studentUserId=undefined; getList() }
function handleSelectionChange(selection:any[]){ ids.value=selection.map(i=>i.id); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetForm(); title.value='新增选课关系'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.id===ids.value[0]); if(!item) return; Object.assign(form,item); title.value='修改选课关系'; open.value=true }
async function submitForm(){ if(form.id){ await updateCourseStudent(form); ElMessage.success('修改成功') } else { await addCourseStudent(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.id || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选选课关系吗？','提示',{type:'warning'}); await delCourseStudent(target); ElMessage.success('删除成功'); getList() }
async function loadOptions(){ courseOptions.value = await fetchCourseOptions(); studentOptions.value = await fetchUserOptions('student'); classOptions.value = await fetchClassOptions() }
onMounted(async()=>{ await loadOptions(); resetForm(); getList() })
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
