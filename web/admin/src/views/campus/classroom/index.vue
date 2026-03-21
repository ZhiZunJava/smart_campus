<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="教室"><el-input v-model="queryParams.classroomName" placeholder="请输入教室名称" clearable style="width:220px" /></el-form-item>
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
      <el-form-item label="教学楼">
        <el-select v-model="queryParams.buildingName" filterable clearable placeholder="请选择教学楼" style="width:220px">
          <el-option v-for="item in buildingOptions" :key="`query-building-${item.value}`" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="校区">
        <el-select v-model="queryParams.campusName" filterable clearable placeholder="请选择校区" style="width:220px">
          <el-option v-for="dict in campus_area_type" :key="`query-campus-${dict.value}`" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="queryParams.roomType" filterable clearable placeholder="请选择教室类型" style="width:220px">
          <el-option v-for="dict in classroom_room_type" :key="`query-room-type-${dict.value}`" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <div v-if="currentFilterTags.length" class="filter-tag-bar">
      <span class="filter-tag-bar__label">当前资源范围</span>
      <el-tag v-for="tag in currentFilterTags" :key="tag.label" size="small" round>{{ tag.label }}</el-tag>
    </div>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="教室名称" prop="classroomName" min-width="160" />
      <el-table-column label="所属部门" min-width="180"><template #default="{ row }">{{ row.deptName || '-' }}</template></el-table-column>
      <el-table-column label="位置" min-width="240">
        <template #default="{ row }">
          <div class="position-cell">
            <strong>{{ formatLocationLine(row) }}</strong>
            <span>{{ formatClassroomPreview(row) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="容量" prop="capacity" width="100" />
      <el-table-column label="类型" prop="roomType" min-width="120" />
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="150" fixed="right"><template #default="{ row }"><el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button></template></el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="680px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="教室名称"><el-input v-model="form.classroomName" placeholder="例如 512 / 机房A / 报告厅" /></el-form-item>
        <el-form-item label="所属部门">
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
        <el-form-item label="教学楼">
          <el-select
            v-model="form.buildingName"
            filterable
            clearable
            placeholder="请选择教学楼"
            style="width:100%"
          >
            <el-option v-for="item in filteredBuildingOptions" :key="`form-building-${item.value}`" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="校区">
          <el-select
            v-model="form.campusName"
            filterable
            clearable
            placeholder="请选择校区"
            style="width:100%"
          >
            <el-option v-for="dict in campus_area_type" :key="`form-campus-${dict.value}`" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="容量"><el-input-number v-model="form.capacity" :min="0" :max="500" style="width:100%" /></el-form-item>
        <el-form-item label="类型">
          <el-select
            v-model="form.roomType"
            filterable
            clearable
            placeholder="请选择教室类型"
            style="width:100%"
          >
            <el-option v-for="dict in classroom_room_type" :key="`form-room-type-${dict.value}`" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" :max="999" style="width:100%" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item>
        <div class="classroom-form-preview">
          预览位置：{{ formatLocationLine(form) || '--' }}
        </div>
        <div class="classroom-form-preview classroom-form-preview--muted">
          展示摘要：{{ formatClassroomPreview(form) }}
        </div>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
// @ts-nocheck
import { computed, getCurrentInstance, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addClassroom, delClassroom, listClassroom, updateClassroom } from '@/api/campus/teaching'
import { deptTreeSelect } from '@/api/system/user'

const { proxy } = getCurrentInstance() as any
const { campus_area_type, classroom_room_type, campus_building_type } = proxy.useDict('campus_area_type', 'classroom_room_type', 'campus_building_type')
const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const allClassrooms = ref<any[]>([])
const deptOptions=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,classroomName:undefined,deptId:undefined,buildingName:undefined,campusName:undefined,roomType:undefined })
const form=reactive<any>({})
const buildingOptions = computed(() => {
  const dictOptions = (campus_building_type.value || []).map((item:any) => ({ label: item.label, value: item.value }))
  if (dictOptions.length) return dictOptions
  return uniqueOptions(allClassrooms.value.map((item:any) => item.buildingName)).map((item:any) => ({ label: item, value: item }))
})
const filteredBuildingOptions = computed(() => {
  return buildingOptions.value
})
const currentFilterTags = computed(() => {
  const tags: Array<{ label: string }> = []
  const deptLabel = findDeptLabel(deptOptions.value, queryParams.deptId)
  const campusLabel = campus_area_type.value.find((item:any) => item.value === queryParams.campusName)?.label
  const roomTypeLabel = classroom_room_type.value.find((item:any) => item.value === queryParams.roomType)?.label
  if (deptLabel) tags.push({ label: `部门：${deptLabel}` })
  if (campusLabel) tags.push({ label: `校区：${campusLabel}` })
  if (roomTypeLabel) tags.push({ label: `类型：${roomTypeLabel}` })
  if (queryParams.buildingName) tags.push({ label: `教学楼：${queryParams.buildingName}` })
  return tags
})

function resetForm(){ Object.assign(form,{ classroomId:undefined,classroomName:'',buildingName:'',deptId:undefined,campusName:'',capacity:0,roomType:'',sortOrder:0,status:'0',remark:'' }) }
async function getList(){ loading.value=true; const res=await listClassroom(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
async function loadDictionaryOptions() {
  const res = await listClassroom({ pageNum:1, pageSize:500 })
  allClassrooms.value = res.rows || []
  const deptRes = await deptTreeSelect()
  deptOptions.value = deptRes.data || []
}
function findDeptLabel(options: any[], value: any): string {
  if (value == null) return ''
  for (const item of options || []) {
    if (item?.id === value) return item.label || ''
    const child = findDeptLabel(item?.children || [], value)
    if (child) return child
  }
  return ''
}
function resetQuery(){ queryParams.pageNum=1; queryParams.classroomName=undefined; queryParams.deptId=undefined; queryParams.buildingName=undefined; queryParams.campusName=undefined; queryParams.roomType=undefined; getList() }
function handleSelectionChange(selection:any[]){ ids.value=selection.map((i:any)=>i.classroomId); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetForm(); title.value='新增教室'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.classroomId===ids.value[0]); if(!item) return; resetForm(); Object.assign(form,item); title.value='修改教室'; open.value=true }
function uniqueOptions(values: any[]) {
  return Array.from(new Set(values.filter((item) => item && String(item).trim()))).sort((a:any, b:any) => String(a).localeCompare(String(b), 'zh-CN'))
}
function joinUniqueParts(parts: any[]) {
  const result: string[] = []
  parts.forEach((part) => {
    const text = String(part || '').trim()
    if (!text) return
    if (result.some((item) => item === text || item.includes(text) || text.includes(item))) return
    result.push(text)
  })
  return result.join(' · ')
}
function formatLocationLine(item: any) {
  return joinUniqueParts([item?.campusName, item?.buildingName, item?.classroomName]) || '--'
}
function formatClassroomPreview(item: any) {
  return joinUniqueParts([item?.deptName, item?.campusName, item?.buildingName, item?.classroomName, item?.roomType]) || '--'
}
async function submitForm(){
  form.classroomName = String(form.classroomName || '').trim()
  form.buildingName = String(form.buildingName || '').trim()
  if(form.classroomId){ await updateClassroom(form); ElMessage.success('修改成功') } else { await addClassroom(form); ElMessage.success('新增成功') }
  open.value=false
  await loadDictionaryOptions()
  getList()
}
async function handleDelete(row?:any){ const target=row?.classroomId || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选教室吗？','提示',{type:'warning'}); await delClassroom(target); ElMessage.success('删除成功'); getList() }
onMounted(async()=>{ resetForm(); await loadDictionaryOptions(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px}
.filter-tag-bar{display:flex;align-items:center;gap:8px;flex-wrap:wrap;margin:-2px 0 16px;padding:10px 12px;border-radius:14px;background:linear-gradient(180deg,#f8fbff 0%,#f3f8ff 100%);border:1px solid #dbe6f5}
.filter-tag-bar__label{color:#526076;font-size:12px;font-weight:600}
.classroom-form-preview{
  margin-top:4px;
  padding-left:90px;
  color:#667085;
  font-size:12px;
  line-height:1.6;
}
.classroom-form-preview--muted{color:#98a2b3}
.position-cell{display:flex;flex-direction:column;gap:4px}
.position-cell strong{color:#172033;font-size:13px}
.position-cell span{color:#667085;font-size:12px}
</style>
