<template>
  <div class="app-container">
    <section class="teaching-page-shell">
      <div class="teaching-page-shell__copy">
        <div class="teaching-page-shell__eyebrow">教学基础</div>
        <h2>教室管理</h2>
        <p>统一维护校区、楼栋和教室容量，排课时直接选择，课程表自动回填。</p>
      </div>
      <div class="teaching-page-shell__stats">
        <div v-for="item in headerStats" :key="item.label" class="teaching-page-shell__stat">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <small>{{ item.tip }}</small>
        </div>
      </div>
    </section>

    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="教室"><el-input v-model="queryParams.classroomName" placeholder="请输入教室名称" clearable style="width:220px" /></el-form-item>
      <el-form-item label="部门">
        <el-tree-select
          v-model="queryParams.deptId"
          :data="deptOptions"
          :props="{ value: 'value', label: 'label', children: 'children' }"
          value-key="value"
          check-strictly
          clearable
          filterable
          style="width:220px"
        />
      </el-form-item>
      <el-form-item label="楼栋">
        <el-select v-model="queryParams.buildingName" filterable clearable placeholder="请选择楼栋" style="width:220px">
          <el-option v-for="item in buildingOptions" :key="`query-building-${item}`" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="校区">
        <el-select v-model="queryParams.campusName" filterable clearable placeholder="请选择校区" style="width:220px">
          <el-option v-for="dict in campus_area_type" :key="`query-campus-${dict.value}`" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
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
      <el-table-column label="教室名称" prop="classroomName" min-width="180" />
      <el-table-column label="所属部门" min-width="180"><template #default="{ row }">{{ row.deptName || '-' }}</template></el-table-column>
      <el-table-column label="楼栋" prop="buildingName" min-width="160" />
      <el-table-column label="校区" prop="campusName" min-width="120" />
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
            :props="{ value: 'value', label: 'label', children: 'children' }"
            value-key="value"
            check-strictly
            clearable
            filterable
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select
            v-model="form.buildingName"
            filterable
            allow-create
            default-first-option
            clearable
            placeholder="请选择或输入楼栋"
            style="width:100%"
          >
            <el-option v-for="item in filteredBuildingOptions" :key="`form-building-${item}`" :label="item" :value="item" />
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
          预览位置：{{ formatClassroomPreview(form) }}
        </div>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addClassroom, delClassroom, listClassroom, updateClassroom } from '@/api/campus/teaching'
import { listDept } from '@/api/system/dept'

const { proxy } = getCurrentInstance() as any
const { campus_area_type, classroom_room_type } = proxy.useDict('campus_area_type', 'classroom_room_type')
const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const allClassrooms = ref<any[]>([])
const deptOptions=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,classroomName:undefined,deptId:undefined,buildingName:undefined,campusName:undefined })
const form=reactive<any>({})
const buildingOptions = computed(() => uniqueOptions(allClassrooms.value.map((item:any) => item.buildingName)))
const filteredBuildingOptions = computed(() => {
  if (!form.campusName) return buildingOptions.value
  return uniqueOptions(
    allClassrooms.value
      .filter((item:any) => item.campusName === form.campusName)
      .map((item:any) => item.buildingName)
  )
})
const headerStats = computed(() => [
  { label: '当前页教室', value: total.value, tip: '可供排课直接选择' },
  { label: '已启用', value: dataList.value.filter((item:any)=>item.status==='0').length, tip: '课表会自动读取' },
  { label: '楼栋覆盖', value: new Set(dataList.value.map((item:any)=>item.buildingName).filter(Boolean)).size, tip: '统一楼栋命名' },
])

function resetForm(){ Object.assign(form,{ classroomId:undefined,classroomName:'',buildingName:'',deptId:undefined,campusName:'',capacity:0,roomType:'',sortOrder:0,status:'0',remark:'' }) }
async function getList(){ loading.value=true; const res=await listClassroom(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
async function loadDictionaryOptions() {
  const res = await listClassroom({ pageNum:1, pageSize:500 })
  allClassrooms.value = res.rows || []
  const deptRes = await listDept()
  deptOptions.value = buildDeptTree(deptRes.data || [])
}
function resetQuery(){ queryParams.pageNum=1; queryParams.classroomName=undefined; queryParams.deptId=undefined; queryParams.buildingName=undefined; queryParams.campusName=undefined; getList() }
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
function formatClassroomPreview(item: any) {
  return joinUniqueParts([item?.deptName, item?.campusName, item?.buildingName, item?.classroomName]) || '--'
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
function buildDeptTree(items:any[] = []): any[] {
  return items.map((item:any) => ({
    label: item.deptName,
    value: item.deptId,
    children: buildDeptTree(item.children || []),
  }))
}

onMounted(async()=>{ resetForm(); await loadDictionaryOptions(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px}
.teaching-page-shell{display:grid;grid-template-columns:minmax(0,1.15fr) minmax(320px,.85fr);gap:16px;margin-bottom:16px;padding:18px;border:1px solid #dbe3f0;border-radius:18px;background:linear-gradient(180deg,#fff 0%,#f7fbff 100%)}
.teaching-page-shell__eyebrow{color:#607188;font-size:12px;font-weight:700;letter-spacing:.08em}
.teaching-page-shell__copy h2{margin:8px 0 10px;color:#172033;font-size:24px;line-height:1.2}
.teaching-page-shell__copy p{margin:0;color:#526076;font-size:13px;line-height:1.8}
.teaching-page-shell__stats{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:10px}
.teaching-page-shell__stat{display:flex;flex-direction:column;gap:6px;padding:14px;border-radius:16px;background:#fff;border:1px solid rgba(219,227,240,.92)}
.teaching-page-shell__stat span,.teaching-page-shell__stat small{color:#667085;font-size:12px}
.teaching-page-shell__stat strong{color:#172033;font-size:22px;line-height:1.1}
.classroom-form-preview{
  margin-top:4px;
  padding-left:90px;
  color:#667085;
  font-size:12px;
  line-height:1.6;
}
@media (max-width: 1100px){.teaching-page-shell{grid-template-columns:1fr}.teaching-page-shell__stats{grid-template-columns:1fr}}
</style>
