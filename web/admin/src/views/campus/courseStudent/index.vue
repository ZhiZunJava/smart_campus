<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width:240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="学生"><el-select v-model="queryParams.studentUserId" filterable clearable style="width:240px"><el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="班级"><el-select v-model="queryParams.classId" filterable clearable style="width:220px"><el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
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
      <el-table-column label="选课对象" min-width="280">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.studentName || getOptionLabel(studentOptions, row.studentUserId, '学生') }}</strong>
            <span>{{ row.studentNo ? `学号：${row.studentNo}` : `学生ID：${row.studentUserId}` }}</span>
            <span>{{ row.courseName || getOptionLabel(courseOptions, row.courseId, '课程') }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="班级" min-width="180">
        <template #default="{ row }">{{ row.className || getOptionLabel(classOptions, row.classId, '班级') }}</template>
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
        :available-options="{ courseOptions, studentOptions, classOptions }"
        @apply="applyAiDraft"
      />
      <el-alert
        v-if="duplicateState.duplicate"
        class="mb16"
        type="warning"
        :closable="false"
        show-icon
        :title="`重复选课：${duplicateState.studentName || '当前学生'} 已关联 ${duplicateState.courseName || '当前课程'}`"
        :description="duplicateState.className ? `当前记录班级：${duplicateState.className}` : '请直接修改现有选课关系，避免重复新增。'"
      />
      <el-form :model="form" label-width="90px">
        <el-form-item label="课程"><el-select v-model="form.courseId" filterable clearable style="width:100%"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="学生"><el-select v-model="form.studentUserId" filterable clearable style="width:100%"><el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="班级"><el-select v-model="form.classId" filterable clearable style="width:100%"><el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>

    <el-dialog v-model="batchOpen" title="批量添加选课学生" width="900px">
      <el-form :model="batchForm" label-width="100px" class="mb16">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="课程"><el-select v-model="batchForm.courseId" filterable clearable style="width:100%"><el-option v-for="item in courseOptions" :key="`batch-course-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="班级"><el-select v-model="batchForm.classId" filterable clearable style="width:100%"><el-option v-for="item in classOptions" :key="`batch-class-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
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
import { fetchClassOptions, fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'
import { listUserProfile } from '@/api/campus/userProfile'
import { autoArrangeCourseSchedule, listClassCourse } from '@/api/campus/teaching'
import TeachingAiAssist from '@/components/Teaching/TeachingAiAssist.vue'

const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([])
const courseOptions=ref<any[]>([])
const studentOptions=ref<any[]>([])
const classOptions=ref<any[]>([])
const studentProfileMap = ref<Map<number, any>>(new Map())
const selectedRows=ref<any[]>([])
const batchOpen = ref(false)
const batchKeyword = ref('')
const batchSelectedStudentIds = ref<number[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,courseId:undefined,studentUserId:undefined,classId:undefined })
const form=reactive<any>({})
const batchForm = reactive<any>({ courseId: undefined, classId: undefined, status:'0', remark:'' })
const duplicateState = reactive<any>({ duplicate:false })
function getOptionLabel(options: any[], value: any, fallback: string) {
  return options.find((item) => item.value === value)?.label || `${fallback} ${value || '-'}`
}
function resetForm(){ Object.assign(form,{ id:undefined,courseId:undefined,studentUserId:undefined,classId:undefined,status:'0',remark:'' }) }
function resetBatchForm(){ Object.assign(batchForm,{ courseId:undefined,classId:undefined,status:'0',remark:'' }); batchKeyword.value=''; batchSelectedStudentIds.value=[] }
function applyAiDraft(draft: Record<string, any>) { Object.assign(form, draft); syncClassByStudent() }
async function getList(){ loading.value=true; const res=await listCourseStudent(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.courseId=undefined; queryParams.studentUserId=undefined; queryParams.classId=undefined; getList() }
function handleSelectionChange(selection:any[]){ selectedRows.value = selection; ids.value=selection.map(i=>i.id); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetForm(); Object.assign(duplicateState,{ duplicate:false }); title.value='新增选课关系'; open.value=true }
function handleBatchAdd(){ resetBatchForm(); batchOpen.value = true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.id===ids.value[0]); if(!item) return; resetForm(); Object.assign(duplicateState,{ duplicate:false }); Object.assign(form,item); title.value='修改选课关系'; open.value=true }
async function submitForm(){
  syncClassByStudent()
  await checkDuplicate()
  if(duplicateState.duplicate){ ElMessage.warning('当前学生已选过该课程，请直接修改现有记录'); return }
  if(form.id){ await updateCourseStudent(form); ElMessage.success('修改成功') } else { await addCourseStudent(form); ElMessage.success('新增成功') }
  open.value=false; getList()
}
async function handleDelete(row?:any){ const target=row?.id || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选选课关系吗？','提示',{type:'warning'}); await delCourseStudent(target); ElMessage.success('删除成功'); getList() }
const batchCandidateStudents = computed(() => {
  let rows = studentOptions.value
  if (batchForm.classId) rows = rows.filter((item:any) => item.classId === batchForm.classId)
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
  if(!batchForm.courseId){ ElMessage.warning('请先选择课程'); return }
  if(!batchSelectedStudentIds.value.length){ ElMessage.warning('请至少勾选一个学生'); return }
  const res = await batchAddCourseStudent({
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
function syncClassByStudent(){
  if(form.classId) return
  const profile = studentProfileMap.value.get(Number(form.studentUserId))
  if(profile?.classId){ form.classId = profile.classId }
}
async function checkDuplicate(){
  if(!open.value || !form.courseId || !form.studentUserId){ Object.assign(duplicateState,{ duplicate:false }); return }
  const res = await checkCourseStudentDuplicate(form)
  Object.assign(duplicateState,{ duplicate:false },res.data || {})
}
async function handleAutoArrange(){
  const classIds = Array.from(new Set(dataList.value.map((item:any)=>item.classId).filter(Boolean)))
  if(!classIds.length){ ElMessage.warning('当前选课关系中没有可用班级，无法自动排课'); return }
  const classCourseRes = await listClassCourse({ pageNum:1, pageSize:500, status:'0' })
  const rows = classCourseRes.rows || []
  const classCourseIds = rows.filter((item:any)=> classIds.includes(item.classId)).map((item:any)=>item.id)
  if(!classCourseIds.length){ ElMessage.warning('当前没有匹配到班级课程，无法自动排课'); return }
  const termId = rows.find((item:any)=> classCourseIds.includes(item.id))?.termId
  if(!termId){ ElMessage.warning('未找到班级课程所属学期，无法自动排课'); return }
  await autoArrangeCourseSchedule({ termId, classCourseIds, clearExistingSchedules:false, populationSize:60, generationCount:120, mutationRate:0.12, excludedWeekDays:[6,7], excludedDayParts:['NOON', 'EVENING'] })
  ElMessage.success('已触发遗传算法自动排课')
}
async function loadOptions(){
  courseOptions.value = await fetchCourseOptions()
  classOptions.value = await fetchClassOptions()
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
watch(() => [open.value, form.courseId, form.studentUserId], () => { checkDuplicate() })
watch(() => form.studentUserId, () => { if(open.value){ form.classId = undefined; syncClassByStudent() } })
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
