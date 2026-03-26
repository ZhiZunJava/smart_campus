<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="学期"><el-select v-model="queryParams.termId" filterable clearable style="width:220px"><el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="教学班"><el-select v-model="queryParams.classCourseId" filterable clearable style="width:320px"><el-option v-for="item in queryClassCourseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="学生"><el-select v-model="queryParams.studentUserId" filterable clearable style="width:240px"><el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.8"><el-button type="primary" icon="UserFilled" @click="handleBatchAdd">批量加学生</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <el-col :span="2"><el-button type="warning" plain icon="MagicStick" @click="handleAutoArrange">自动排课</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="选课对象" min-width="220">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.studentName || getOptionLabel(studentOptions, row.studentUserId, '学生') }}</strong>
            <span>{{ row.studentNo ? `学号：${row.studentNo}` : `学生ID：${row.studentUserId}` }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="教学班" min-width="320">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.courseName || '-' }}</strong>
            <span>{{ row.className || '-' }}{{ row.teachingClassCode ? ` · ${row.teachingClassCode}` : '' }}</span>
            <span>{{ row.termName || '-' }}{{ row.teacherName ? ` · ${row.teacherName}` : '' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="加入时间" prop="joinTime" min-width="180" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="660px">
      <TeachingAiAssist
        module-key="courseStudent"
        module-label="选课关系"
        :form-data="form"
        :selected-rows="selectedRows"
        :available-options="{ classCourseOptions: formClassCourseOptions, studentOptions }"
        @apply="applyAiDraft"
      />
      <el-alert
        v-if="duplicateState.duplicate"
        class="mb16"
        type="warning"
        :closable="false"
        show-icon
        :title="`重复选课：${duplicateState.studentName || '当前学生'} 已关联 ${duplicateState.courseName || '当前课程'}`"
        :description="duplicateState.teachingClassCode ? `当前教学班：${duplicateState.className || '-'} / ${duplicateState.teachingClassCode}` : (duplicateState.className ? `当前记录班级：${duplicateState.className}` : '请直接修改现有选课关系，避免重复新增。')"
      />
      <el-form :model="form" label-width="90px">
        <el-form-item label="教学班"><el-select v-model="form.classCourseId" filterable clearable style="width:100%" @change="handleClassCourseChange"><el-option v-for="item in formClassCourseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="学生"><el-select v-model="form.studentUserId" filterable clearable style="width:100%"><el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="班级"><el-input :model-value="selectedFormClassCourse?.className || form.className || '-'" disabled /></el-form-item>
        <el-form-item label="课程"><el-input :model-value="selectedFormClassCourse?.courseName || form.courseName || '-'" disabled /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>

    <el-dialog v-model="batchOpen" title="批量添加选课学生" width="900px">
      <el-form :model="batchForm" label-width="100px" class="mb16">
        <el-row :gutter="16">
          <el-col :span="24"><el-form-item label="教学班"><el-select v-model="batchForm.classCourseId" filterable clearable style="width:100%" @change="handleBatchClassCourseChange"><el-option v-for="item in formClassCourseOptions" :key="`batch-class-course-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="batchForm.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="备注"><el-input v-model="batchForm.remark" placeholder="可统一补充本次选课说明" /></el-form-item></el-col>
        </el-row>
      </el-form>

      <div class="batch-toolbar">
        <div class="batch-toolbar__summary">当前可选学生 {{ batchCandidateStudents.length }} 人，已勾选 {{ batchSelectedStudentIds.length }} 人</div>
        <el-input v-model="batchKeyword" clearable placeholder="筛选姓名/学号" style="width:240px" />
      </div>

      <el-table :data="filteredBatchStudents" @selection-change="handleBatchSelectionChange" row-key="value" max-height="420">
        <el-table-column type="selection" width="55" />
        <el-table-column label="学生" min-width="220">
          <template #default="{ row }">
            <div class="entity-cell">
              <strong>{{ row.realName || row.label }}</strong>
              <span>{{ row.studentNo || `用户ID ${row.value}` }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="班级" min-width="180"><template #default="{ row }">{{ row.className || '-' }}</template></el-table-column>
        <el-table-column label="专业" min-width="160" prop="major" />
      </el-table>

      <template #footer>
        <el-button @click="batchOpen=false">取消</el-button>
        <el-button type="primary" @click="submitBatchAdd">批量加入课程</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addCourseStudent, batchAddCourseStudent, checkCourseStudentDuplicate, delCourseStudent, listCourseStudent, updateCourseStudent } from '@/api/campus/courseStudent'
import { fetchClassOptions, fetchTermOptions, fetchUserOptions } from '@/api/campus/options'
import { listUserProfile } from '@/api/campus/userProfile'
import { autoArrangeCourseSchedule, listClassCourse } from '@/api/campus/teaching'
import TeachingAiAssist from '@/components/Teaching/TeachingAiAssist.vue'

const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const studentOptions=ref<any[]>([])
const classOptions=ref<any[]>([])
const termOptions=ref<any[]>([])
const classCourseRows = ref<any[]>([])
const studentProfileMap = ref<Map<number, any>>(new Map())
const selectedRows=ref<any[]>([])
const batchOpen = ref(false)
const batchKeyword = ref('')
const batchSelectedStudentIds = ref<number[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,termId:undefined,classCourseId:undefined,studentUserId:undefined })
const form=reactive<any>({})
const batchForm = reactive<any>({ classCourseId: undefined, courseId: undefined, classId: undefined, status:'0', remark:'' })
const duplicateState = reactive<any>({ duplicate:false })
const queryClassCourseOptions = computed(() => {
  return classCourseRows.value
    .filter((item:any) => !queryParams.termId || String(item.termId) === String(queryParams.termId))
    .map((item:any) => ({
      label: `${item.termName || '未配置学期'} / ${item.className || '未配置班级'} / ${item.courseName || '未命名课程'}${item.teachingClassCode ? ` / ${item.teachingClassCode}` : ''}`,
      value: item.id,
      raw: item,
    }))
})
const formClassCourseOptions = computed(() => {
  return classCourseRows.value.map((item:any) => ({
    label: `${item.termName || '未配置学期'} / ${item.className || '未配置班级'} / ${item.courseName || '未命名课程'}${item.teachingClassCode ? ` / ${item.teachingClassCode}` : ''}`,
    value: item.id,
    raw: item,
  }))
})
const selectedFormClassCourse = computed(() => classCourseRows.value.find((item:any) => String(item.id) === String(form.classCourseId)))
const selectedBatchClassCourse = computed(() => classCourseRows.value.find((item:any) => String(item.id) === String(batchForm.classCourseId)))
function getOptionLabel(options: any[], value: any, fallback: string) {
  return options.find((item) => item.value === value)?.label || `${fallback} ${value || '-'}`
}
function resetForm(){ Object.assign(form,{ id:undefined,classCourseId:undefined,courseId:undefined,courseName:'',studentUserId:undefined,classId:undefined,className:'',status:'0',remark:'' }) }
function resetBatchForm(){ Object.assign(batchForm,{ classCourseId:undefined,courseId:undefined,classId:undefined,status:'0',remark:'' }); batchKeyword.value=''; batchSelectedStudentIds.value=[] }
function applyAiDraft(draft: Record<string, any>) { Object.assign(form, draft); syncSnapshotsByClassCourse(form) }
async function getList(){ loading.value=true; const res=await listCourseStudent(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.termId=undefined; queryParams.classCourseId=undefined; queryParams.studentUserId=undefined; getList() }
function handleSelectionChange(selection:any[]){ selectedRows.value = selection; ids.value=selection.map(i=>i.id); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetForm(); Object.assign(duplicateState,{ duplicate:false }); title.value='新增选课关系'; open.value=true }
function handleBatchAdd(){ resetBatchForm(); batchOpen.value = true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.id===ids.value[0]); if(!item) return; resetForm(); Object.assign(duplicateState,{ duplicate:false }); Object.assign(form,item); syncSnapshotsByClassCourse(form); title.value='修改选课关系'; open.value=true }
async function submitForm(){
  syncSnapshotsByClassCourse(form)
  await checkDuplicate()
  if(duplicateState.duplicate){ ElMessage.warning('当前学生已选过该课程，请直接修改现有记录'); return }
  if(form.id){ await updateCourseStudent(form); ElMessage.success('修改成功') } else { await addCourseStudent(form); ElMessage.success('新增成功') }
  open.value=false; getList()
}
async function handleDelete(row?:any){ const target=row?.id || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选选课关系吗？','提示',{type:'warning'}); await delCourseStudent(target); ElMessage.success('删除成功'); getList() }
const batchCandidateStudents = computed(() => {
  let rows = studentOptions.value
  if (selectedBatchClassCourse.value?.classId) rows = rows.filter((item:any) => item.classId === selectedBatchClassCourse.value.classId)
  return rows
})
const filteredBatchStudents = computed(() => {
  const keyword = String(batchKeyword.value || '').trim()
  if (!keyword) return batchCandidateStudents.value
  return batchCandidateStudents.value.filter((item:any) => String(item.realName || item.label || '').includes(keyword) || String(item.studentNo || '').includes(keyword))
})
function handleBatchSelectionChange(selection:any[]){
  batchSelectedStudentIds.value = selection.map((item:any)=>Number(item.value))
}
async function submitBatchAdd(){
  if(!batchForm.classCourseId){ ElMessage.warning('请先选择教学班'); return }
  if(!batchSelectedStudentIds.value.length){ ElMessage.warning('请至少勾选一个学生'); return }
  syncSnapshotsByClassCourse(batchForm)
  const res = await batchAddCourseStudent({
    classCourseId: batchForm.classCourseId,
    courseId: batchForm.courseId,
    classId: batchForm.classId,
    studentUserIds: batchSelectedStudentIds.value,
    status: batchForm.status,
    remark: batchForm.remark,
  })
  const data = res.data || {}
  ElMessage.success(`批量加入完成：成功 ${data.successCount || 0} 人${data.skippedCount ? `，跳过重复 ${data.skippedCount} 人` : ''}`)
  batchOpen.value = false
  getList()
}
function syncSnapshotsByClassCourse(target:any){
  const classCourse = classCourseRows.value.find((item:any) => String(item.id) === String(target.classCourseId))
  if (!classCourse) return
  target.classCourseId = classCourse.id
  target.courseId = classCourse.courseId
  target.courseName = classCourse.courseName
  target.classId = classCourse.classId
  target.className = classCourse.className
  target.termId = classCourse.termId
  target.termName = classCourse.termName
}
function handleClassCourseChange(){
  syncSnapshotsByClassCourse(form)
}
function handleBatchClassCourseChange(){
  syncSnapshotsByClassCourse(batchForm)
  batchSelectedStudentIds.value = []
}
async function checkDuplicate(){
  if(!open.value || !form.classCourseId || !form.studentUserId){ Object.assign(duplicateState,{ duplicate:false }); return }
  syncSnapshotsByClassCourse(form)
  const res = await checkCourseStudentDuplicate(form)
  Object.assign(duplicateState,{ duplicate:false },res.data || {})
}
async function handleAutoArrange(){
  const classCourseIds = Array.from(new Set(dataList.value.map((item:any)=>item.classCourseId).filter(Boolean)))
  if(!classCourseIds.length){ ElMessage.warning('当前没有匹配到班级课程，无法自动排课'); return }
  const termIds = Array.from(new Set(dataList.value.filter((item:any)=> classCourseIds.includes(item.classCourseId)).map((item:any)=>item.termId).filter(Boolean)))
  if(termIds.length > 1){ ElMessage.warning('当前列表包含多个学期，请先按学期筛选后再自动排课'); return }
  const termId = termIds[0]
  if(!termId){ ElMessage.warning('未找到班级课程所属学期，无法自动排课'); return }
  await autoArrangeCourseSchedule({ termId, classCourseIds, clearExistingSchedules:false, populationSize:60, generationCount:120, mutationRate:0.12, excludedWeekDays:[6,7], excludedDayParts:['NOON', 'EVENING'] })
  ElMessage.success('已触发遗传算法自动排课')
}
async function loadOptions(){
  classOptions.value = await fetchClassOptions()
  termOptions.value = await fetchTermOptions()
  const classCourseRes = await listClassCourse({ pageNum:1, pageSize:500, status:'0' })
  classCourseRows.value = classCourseRes.rows || []
  const [users, profilesRes] = await Promise.all([
    fetchUserOptions('student'),
    listUserProfile({ pageNum:1, pageSize:500, userType:'student', status:'0' })
  ])
  const profiles = profilesRes.rows || []
  studentProfileMap.value = new Map(profiles.map((item:any)=>[Number(item.userId), item]))
  studentOptions.value = users.map((item:any) => {
    const profile = studentProfileMap.value.get(Number(item.value))
    return {
      ...item,
      label: `${profile?.realName || item.label}${profile?.studentNo ? ' · ' + profile.studentNo : ''}`,
      realName: profile?.realName,
      classId: profile?.classId,
      className: classOptions.value.find((classItem:any)=>classItem.value===profile?.classId)?.label,
      major: profile?.major,
      studentNo: profile?.studentNo,
    }
  })
}
watch(() => [open.value, form.classCourseId, form.studentUserId], () => { checkDuplicate() })
onMounted(async()=>{ await loadOptions(); resetForm(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px}
.entity-cell{display:flex;flex-direction:column;gap:6px}
.entity-cell strong{color:#172033;font-size:13px}
.entity-cell span{color:#667085;font-size:12px}
.batch-toolbar{display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:12px}
.batch-toolbar__summary{color:#526076;font-size:12px}
</style>
