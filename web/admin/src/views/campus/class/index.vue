<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="班级名称"><el-input v-model="queryParams.className" clearable style="width:220px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="部门">
        <el-tree-select
          v-model="queryParams.deptId"
          :data="deptOptions"
          :props="{ value: 'id', label: 'label', children: 'children' }"
          value-key="id"
          check-strictly
          clearable
          filterable
          style="width:220px"
        />
      </el-form-item>
      <el-form-item label="学期"><el-select v-model="queryParams.termId" filterable clearable style="width:220px"><el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="年级"><el-select v-model="queryParams.gradeId" filterable clearable style="width:220px"><el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
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
      <el-table-column label="班级信息" min-width="260">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.className }}</strong>
            <span>{{ getOptionLabel(gradeOptions, row.gradeId, '年级') }}{{ row.deptName ? ` · ${row.deptName}` : '' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="所属部门" min-width="180"><template #default="{ row }">{{ row.deptName || '-' }}</template></el-table-column>
      <el-table-column label="班主任" min-width="180">
        <template #default="{ row }">{{ getOptionLabel(teacherOptions, row.headTeacherId, '教师') }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="640px">
      <TeachingAiAssist
        module-key="class"
        module-label="班级管理"
        :form-data="form"
        :selected-rows="selectedRows"
        :available-options="{ gradeOptions, teacherOptions, deptOptions }"
        @apply="applyAiDraft"
      />
      <el-form :model="form" label-width="90px">
        <el-form-item label="年级"><el-select v-model="form.gradeId" filterable clearable style="width:100%"><el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="form.deptId"
            :data="deptOptions"
            :props="{ value: 'id', label: 'label', children: 'children' }"
            value-key="id"
            check-strictly
            clearable
            filterable
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="班级名称"><el-input v-model="form.className" /></el-form-item>
        <el-form-item label="班主任"><el-select v-model="form.headTeacherId" filterable clearable style="width:100%"><el-option v-for="item in teacherOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
// @ts-nocheck
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addClass, delClass, listClass, updateClass } from '@/api/campus/teaching'
import { fetchGradeOptions, fetchTeachingUserOptions, fetchTermOptions } from '@/api/campus/options'
import { deptTreeSelect } from '@/api/system/user'
import TeachingAiAssist from '@/components/Teaching/TeachingAiAssist.vue'

const loading = ref(false), showSearch = ref(true), total = ref(0), open = ref(false), title = ref(''), ids = ref<any[]>([]), single = ref(true), multiple = ref(true), dataList = ref<any[]>([])
const gradeOptions = ref<any[]>([])
const teacherOptions = ref<any[]>([])
const termOptions = ref<any[]>([])
const deptOptions = ref<any[]>([])
const selectedRows = ref<any[]>([])
const queryParams = reactive<any>({ pageNum:1,pageSize:10,className:'',gradeId:undefined,deptId:undefined,termId:undefined })
const form = reactive<any>({})

function getOptionLabel(options: any[], value: any, fallback: string) {
  return options.find((item) => item.value === value)?.label || `${fallback} ${value || '-'}`
}
function resetFormData(){ Object.assign(form,{ classId:undefined,gradeId:undefined,deptId:undefined,className:'',headTeacherId:undefined,status:'0',remark:''}) }
function applyAiDraft(draft: Record<string, any>) { Object.assign(form, draft) }
async function getList(){ loading.value=true; const res=await listClass(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.className=''; queryParams.gradeId=undefined; queryParams.deptId=undefined; getList() }
function handleSelectionChange(selection:any[]){ selectedRows.value = selection; ids.value=selection.map(i=>i.classId); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetFormData(); title.value='新增班级'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.classId===ids.value[0]); if(!item) return; resetFormData(); Object.assign(form,item); title.value='修改班级'; open.value=true }
async function submitForm(){ if(form.classId){ await updateClass(form); ElMessage.success('修改成功') } else { await addClass(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.classId || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选班级吗？','提示',{type:'warning'}); await delClass(target); ElMessage.success('删除成功'); getList() }
watch(() => queryParams.termId, async (value) => {
  const term = termOptions.value.find((item: any) => item.value === value)
  gradeOptions.value = await fetchGradeOptions(term?.label?.match(/（(.+)）/)?.[1])
  if (!gradeOptions.value.some((item: any) => item.value === queryParams.gradeId)) queryParams.gradeId = undefined
})
async function loadOptions(){
  termOptions.value = await fetchTermOptions()
  const current = termOptions.value.find((item: any) => item.isCurrent === '1')
  if (current) queryParams.termId = current.value
  gradeOptions.value = await fetchGradeOptions(current?.label?.match(/（(.+)）/)?.[1])
  teacherOptions.value = await fetchTeachingUserOptions('head_teacher')
  const deptRes = await deptTreeSelect()
  deptOptions.value = deptRes.data || []
}
onMounted(async()=>{ await loadOptions(); resetFormData(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px;}
.entity-cell{display:flex;flex-direction:column;gap:6px}
.entity-cell strong{color:#172033;font-size:13px}
.entity-cell span{color:#667085;font-size:12px}
</style>
