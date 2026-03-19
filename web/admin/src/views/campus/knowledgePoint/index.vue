<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width:240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="知识点"><el-input v-model="queryParams.knowledgeName" clearable style="width:220px" @keyup.enter="getList" /></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <div class="knowledge-summary">
      <div class="knowledge-summary__title">知识点更适合按课程树维护</div>
      <div class="knowledge-summary__desc">当前页面已改为显示父级名称和课程内层级关系，后续题库、学情分析、错题归因都可以直接挂接到知识点。</div>
    </div>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="displayList" row-key="knowledgePointId" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="知识点" min-width="260">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ buildKnowledgePointTitle(row) }}</strong>
            <span>{{ getOptionLabel(courseOptions, row.courseId, '课程') }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="父级知识点" min-width="180">
        <template #default="{ row }">{{ getParentKnowledgeName(row) }}</template>
      </el-table-column>
      <el-table-column label="层级" width="80">
        <template #default="{ row }"><el-tag size="small" effect="plain">L{{ row.level || 1 }}</el-tag></template>
      </el-table-column>
      <el-table-column label="难度" prop="difficultyLevel" width="80" />
      <el-table-column label="关键词" prop="keyword" min-width="160" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="描述" prop="description" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="700px">
      <TeachingAiAssist
        module-key="knowledgePoint"
        module-label="知识点管理"
        :form-data="form"
        :selected-rows="selectedRows"
        :available-options="{ courseOptions }"
        @apply="applyAiDraft"
      />
      <el-form :model="form" label-width="100px">
        <el-form-item label="课程"><el-select v-model="form.courseId" filterable clearable style="width:100%"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="父级知识点">
          <el-select v-model="form.parentId" filterable clearable style="width:100%" placeholder="不选则为顶级知识点">
            <el-option label="顶级知识点" :value="0" />
            <el-option v-for="item in parentKnowledgePointOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="知识点名称"><el-input v-model="form.knowledgeName" /></el-form-item>
        <el-form-item label="难度"><el-input-number v-model="form.difficultyLevel" :min="1" :max="5" style="width:100%" /></el-form-item>
        <el-form-item label="关键词"><el-input v-model="form.keyword" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" rows="3" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addKnowledgePoint, delKnowledgePoint, listKnowledgePoint, updateKnowledgePoint } from '@/api/campus/teaching'
import { fetchCourseOptions } from '@/api/campus/options'
import TeachingAiAssist from '@/components/Teaching/TeachingAiAssist.vue'

const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const courseOptions=ref<any[]>([])
const selectedRows=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,courseId:undefined,knowledgeName:'' })
const form=reactive<any>({})
const knowledgePointMap = computed(() => new Map((dataList.value || []).map((item:any) => [Number(item.knowledgePointId), item])))
const displayList = computed(() => {
  return (dataList.value || []).map((item:any) => ({
    ...item,
    level: buildKnowledgePointLevel(item, knowledgePointMap.value),
  }))
})
const parentKnowledgePointOptions = computed(() => {
  const currentCourseId = form.courseId
  return (dataList.value || [])
    .filter((item:any) => item.courseId === currentCourseId && item.knowledgePointId !== form.knowledgePointId)
    .map((item:any) => ({
      label: `${'— '.repeat(Math.max(0, buildKnowledgePointLevel(item, knowledgePointMap.value) - 1))}${item.knowledgeName}`,
      value: item.knowledgePointId,
    }))
})

function getOptionLabel(options: any[], value: any, fallback: string) {
  return options.find((item) => item.value === value)?.label || `${fallback} ${value || '-'}`
}
function resetFormData(){ Object.assign(form,{ knowledgePointId:undefined,courseId:undefined,parentId:0,knowledgeName:'',difficultyLevel:1,keyword:'',description:'',status:'0',remark:''}) }
function applyAiDraft(draft: Record<string, any>) { Object.assign(form, draft) }
async function getList(){ loading.value=true; const res=await listKnowledgePoint(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.courseId=undefined; queryParams.knowledgeName=''; getList() }
function handleSelectionChange(selection:any[]){ selectedRows.value = selection; ids.value=selection.map(i=>i.knowledgePointId); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetFormData(); title.value='新增知识点'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.knowledgePointId===ids.value[0]); if(!item) return; resetFormData(); Object.assign(form,item); title.value='修改知识点'; open.value=true }
async function submitForm(){ if(form.knowledgePointId){ await updateKnowledgePoint(form); ElMessage.success('修改成功') } else { await addKnowledgePoint(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.knowledgePointId || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选知识点吗？','提示',{type:'warning'}); await delKnowledgePoint(target); ElMessage.success('删除成功'); getList() }
function getParentKnowledgeName(row:any){
  if (!row?.parentId || Number(row.parentId) === 0) return '顶级知识点'
  return knowledgePointMap.value.get(Number(row.parentId))?.knowledgeName || `ID ${row.parentId}`
}
function buildKnowledgePointLevel(row:any, map: Map<number, any>){
  let level = 1
  let current = row
  const guard = new Set<number>()
  while (current?.parentId && Number(current.parentId) !== 0) {
    const parentId = Number(current.parentId)
    if (guard.has(parentId)) break
    guard.add(parentId)
    current = map.get(parentId)
    if (!current) break
    level += 1
  }
  return level
}
function buildKnowledgePointTitle(row:any){
  return `${'— '.repeat(Math.max(0, Number(row?.level || 1) - 1))}${row?.knowledgeName || '-'}`
}
async function loadOptions(){ courseOptions.value = await fetchCourseOptions() }
watch(() => form.courseId, () => {
  if (!parentKnowledgePointOptions.value.some((item:any) => item.value === form.parentId)) {
    form.parentId = 0
  }
})
onMounted(async()=>{ await loadOptions(); resetFormData(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px;}
.knowledge-summary{margin-bottom:16px;padding:12px 14px;border:1px solid #dbe6f5;border-radius:6px;background:linear-gradient(180deg,#f8fbff 0%,#f3f8ff 100%)}
.knowledge-summary__title{color:#1b3556;font-size:14px;font-weight:700}
.knowledge-summary__desc{margin-top:4px;color:#5a6c82;font-size:12px;line-height:1.7}
.entity-cell{display:flex;flex-direction:column;gap:6px}
.entity-cell strong{color:#172033;font-size:13px}
.entity-cell span{color:#667085;font-size:12px}
</style>
