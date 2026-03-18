<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="课程名称"><el-input v-model="queryParams.courseName" clearable style="width:220px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="课程编码"><el-input v-model="queryParams.courseCode" clearable style="width:180px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="学科类型"><el-input v-model="queryParams.subjectType" clearable style="width:180px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="状态"><el-select v-model="queryParams.status" clearable style="width:140px"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="课程" min-width="260">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.courseName }}</strong>
            <span>{{ row.courseCode || '未配置课程编码' }}</span>
            <div class="entity-cell__meta">
              <span>{{ row.subjectType || '未分类' }}</span>
              <span>{{ getOptionLabel(gradeOptions, row.gradeId, '年级') }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="年级" min-width="160">
        <template #default="{ row }">{{ getOptionLabel(gradeOptions, row.gradeId, '年级') }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="课程简介" prop="intro" min-width="260" show-overflow-tooltip />
      <el-table-column label="备注" prop="remark" min-width="220" show-overflow-tooltip />
      <el-table-column label="维护信息" min-width="220">
        <template #default="{ row }">
          <div class="maintain-cell">
            <span>创建：{{ row.createBy || '-' }}</span>
            <span>创建时间：{{ formatDateTime(row.createTime) }}</span>
            <span>更新：{{ row.updateBy || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="720px">
      <TeachingAiAssist
        module-key="course"
        module-label="课程管理"
        :form-data="form"
        :selected-rows="selectedRows"
        :available-options="{ gradeOptions }"
        @apply="applyAiDraft"
      />
      <el-form :model="form" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="课程名称"><el-input v-model="form.courseName" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="课程编码">
              <div class="code-field">
                <el-input v-model="form.courseCode" />
                <el-button plain @click="handleGenerateCourseCode">自动生成</el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="学科类型"><el-input v-model="form.subjectType" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="年级"><el-select v-model="form.gradeId" filterable clearable style="width:100%"><el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="简介"><el-input v-model="form.intro" type="textarea" rows="4" placeholder="请输入课程简介、学习目标、适用对象等" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" placeholder="可补充教材、先修建议、实践说明等" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addCourse, delCourse, generateCourseCode, listCourse, updateCourse } from '@/api/campus/teaching'
import { fetchGradeOptions } from '@/api/campus/options'
import TeachingAiAssist from '@/components/Teaching/TeachingAiAssist.vue'

const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const gradeOptions=ref<any[]>([])
const selectedRows=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,courseName:'',courseCode:'',subjectType:'',status:undefined })
const form=reactive<any>({})

function getOptionLabel(options: any[], value: any, fallback: string) {
  return options.find((item) => item.value === value)?.label || `${fallback} ${value || '-'}`
}
function formatDateTime(value: string) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}
function resetFormData(){ Object.assign(form,{ courseId:undefined,courseName:'',courseCode:'',subjectType:'',gradeId:undefined,intro:'',remark:'',status:'0'}) }
function applyAiDraft(draft: Record<string, any>) { Object.assign(form, draft) }
async function getList(){ loading.value=true; const res=await listCourse(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
 function resetQuery(){ queryParams.pageNum=1; queryParams.courseName=''; queryParams.courseCode=''; queryParams.subjectType=''; queryParams.status=undefined; getList() }
function handleSelectionChange(selection:any[]){ selectedRows.value = selection; ids.value=selection.map(i=>i.courseId); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetFormData(); title.value='新增课程'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.courseId===ids.value[0]); if(!item) return; resetFormData(); Object.assign(form,item); title.value='修改课程'; open.value=true }
async function submitForm(){ if(form.courseId){ await updateCourse(form); ElMessage.success('修改成功') } else { await addCourse(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.courseId || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选课程吗？','提示',{type:'warning'}); await delCourse(target); ElMessage.success('删除成功'); getList() }
async function handleGenerateCourseCode(){
  const res = await generateCourseCode(form)
  if(res.data?.courseCode){ form.courseCode = res.data.courseCode; ElMessage.success(`已生成课程编码：${res.data.courseCode}`) }
}
async function loadOptions(){
  gradeOptions.value = await fetchGradeOptions()
}
onMounted(async()=>{ await loadOptions(); resetFormData(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px;}
.entity-cell{display:flex;flex-direction:column;gap:6px}
.entity-cell strong{color:#172033;font-size:14px}
.entity-cell span{color:#667085;font-size:12px}
.entity-cell__meta{display:flex;flex-wrap:wrap;gap:6px}
.entity-cell__meta span{
  display:inline-flex;
  align-items:center;
  padding:2px 8px;
  border-radius:999px;
  background:#f3f6fb;
  color:#4b5565;
  font-size:12px;
}
.maintain-cell{
  display:flex;
  flex-direction:column;
  gap:4px;
  color:#667085;
  font-size:12px;
  line-height:1.5;
}
.code-field{display:flex;align-items:center;gap:8px;width:100%}
.code-field :deep(.el-input){flex:1}
</style>
