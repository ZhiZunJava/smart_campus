<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width:240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item label="知识点"><el-input v-model="queryParams.knowledgeName" style="width:220px" @keyup.enter="getList" /></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item></el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col><el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col><el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col><right-toolbar v-model:showSearch="showSearch" @queryTable="getList" /></el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange"><el-table-column type="selection" width="55" /><el-table-column label="ID" prop="knowledgePointId" width="90" /><el-table-column label="课程ID" prop="courseId" width="90" /><el-table-column label="父级ID" prop="parentId" width="90" /><el-table-column label="知识点名称" prop="knowledgeName" min-width="180" /><el-table-column label="难度" prop="difficultyLevel" width="80" /><el-table-column label="关键词" prop="keyword" min-width="140" /><el-table-column label="状态" width="100"><template #default="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column><el-table-column label="操作" width="150" fixed="right"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="open" :title="title" width="600px"><el-form :model="form" label-width="100px"><el-form-item label="课程"><el-select v-model="form.courseId" filterable clearable><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item label="父级ID"><el-input v-model="form.parentId" /></el-form-item><el-form-item label="知识点名称"><el-input v-model="form.knowledgeName" /></el-form-item><el-form-item label="难度"><el-input-number v-model="form.difficultyLevel" :min="1" :max="5" /></el-form-item><el-form-item label="关键词"><el-input v-model="form.keyword" /></el-form-item><el-form-item label="描述"><el-input v-model="form.description" type="textarea" rows="3" /></el-form-item><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-form><template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addKnowledgePoint, delKnowledgePoint, listKnowledgePoint, updateKnowledgePoint } from '@/api/campus/teaching'
import { fetchCourseOptions } from '@/api/campus/options'
const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const courseOptions=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,courseId:undefined,knowledgeName:'' })
const form=reactive<any>({})
function resetFormData(){ Object.assign(form,{ knowledgePointId:undefined,courseId:undefined,parentId:0,knowledgeName:'',difficultyLevel:1,keyword:'',description:'',status:'0'}) }
async function getList(){ loading.value=true; const res=await listKnowledgePoint(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.courseId=undefined; queryParams.knowledgeName=''; getList() }
function handleSelectionChange(selection:any[]){ ids.value=selection.map(i=>i.knowledgePointId); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetFormData(); title.value='新增知识点'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.knowledgePointId===ids.value[0]); if(!item) return; Object.assign(form,item); title.value='修改知识点'; open.value=true }
async function submitForm(){ if(form.knowledgePointId){ await updateKnowledgePoint(form); ElMessage.success('修改成功') } else { await addKnowledgePoint(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.knowledgePointId || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选知识点吗？','提示',{type:'warning'}); await delKnowledgePoint(target); ElMessage.success('删除成功'); getList() }
async function loadOptions(){ courseOptions.value = await fetchCourseOptions() }
onMounted(async()=>{ await loadOptions(); resetFormData(); getList() })
</script>
<style scoped>.mb16{margin-bottom:16px;}</style>
