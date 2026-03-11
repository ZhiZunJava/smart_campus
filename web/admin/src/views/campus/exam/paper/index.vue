<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16"><el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width:240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item label="试卷名称"><el-input v-model="queryParams.paperName" style="width:220px" @keyup.enter="getList" /></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item></el-form>
    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增试卷</el-button></el-col></el-row>
    <el-table v-loading="loading" :data="dataList"><el-table-column label="试卷ID" prop="paperId" width="90" /><el-table-column label="试卷名称" prop="paperName" min-width="200" /><el-table-column label="课程ID" prop="courseId" width="90" /><el-table-column label="类型" prop="paperType" width="100" /><el-table-column label="总分" prop="totalScore" width="90" /><el-table-column label="时长" prop="durationMinutes" width="90" /><el-table-column label="状态" width="90"><template #default="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column><el-table-column label="操作" width="220"><template #default="scope"><el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button><el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button></template></el-table-column></el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog v-model="open" :title="title" width="640px"><el-form :model="form" label-width="90px"><el-form-item label="试卷名称"><el-input v-model="form.paperName" /></el-form-item><el-form-item label="课程"><el-select v-model="form.courseId" filterable clearable><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item label="类型"><el-select v-model="form.paperType"><el-option label="固定" value="fixed" /><el-option label="随机" value="random" /><el-option label="自适应" value="adaptive" /></el-select></el-form-item><el-form-item label="总分"><el-input-number v-model="form.totalScore" :min="0" :max="1000" /></el-form-item><el-form-item label="时长"><el-input-number v-model="form.durationMinutes" :min="1" :max="300" /></el-form-item><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-form><template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template></el-dialog>
    <el-dialog v-model="detailOpen" title="试卷详情" width="900px"><div class="detail-header"><span>试卷名称：{{ detail.paperName }}</span><span>总分：{{ detail.totalScore }}</span><span>时长：{{ detail.durationMinutes }} 分钟</span></div><el-table :data="detail.questions || []" class="mt16"><el-table-column label="排序" prop="sortNo" width="80" /><el-table-column label="题目ID" prop="questionId" width="90" /><el-table-column label="分值" prop="score" width="90" /><el-table-column label="题型" min-width="100"><template #default="scope">{{ scope.row.question?.questionType }}</template></el-table-column><el-table-column label="题干" min-width="300" show-overflow-tooltip><template #default="scope">{{ scope.row.question?.stem }}</template></el-table-column></el-table></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addPaper, delPaper, getPaperDetail, listPaper, updatePaper } from '@/api/campus/exam'
import { fetchCourseOptions } from '@/api/campus/options'
const loading=ref(false), total=ref(0), open=ref(false), detailOpen=ref(false), title=ref(''), dataList=ref<any[]>([]), detail=ref<any>({})
const courseOptions=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,courseId:undefined,paperName:'' })
const form=reactive<any>({})
function resetForm(){ Object.assign(form,{ paperId:undefined,paperName:'',courseId:undefined,paperType:'fixed',totalScore:100,durationMinutes:60,status:'0'}) }
async function getList(){ loading.value=true; const res=await listPaper(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function handleAdd(){ resetForm(); title.value='新增试卷'; open.value=true }
function handleEdit(row:any){ Object.assign(form,row); title.value='编辑试卷'; open.value=true }
async function submitForm(){ if(form.paperId){ await updatePaper(form); ElMessage.success('修改成功') } else { await addPaper(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleView(row:any){ const res=await getPaperDetail(row.paperId); detail.value=res.data||{}; detailOpen.value=true }
async function handleDelete(row:any){ await ElMessageBox.confirm('确认删除该试卷吗？','提示',{type:'warning'}); await delPaper(row.paperId); ElMessage.success('删除成功'); getList() }
async function loadOptions(){ courseOptions.value = await fetchCourseOptions() }
onMounted(async()=>{ await loadOptions(); resetForm(); getList() })
</script>
<style scoped>.mb16{margin-bottom:16px}.mt16{margin-top:16px}.detail-header{display:flex;gap:24px;color:var(--el-text-color-primary);font-weight:600}</style>
