<template>
  <div class="app-container">
    <section class="teaching-page-shell">
      <div class="teaching-page-shell__copy">
        <div class="teaching-page-shell__eyebrow">教学基础</div>
        <h2>选课关系</h2>
        <p>课程、学生、班级三方关系统一维护，AI 可帮助生成更合理的选课备注与配置建议。</p>
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
      <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width:240px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="学生"><el-select v-model="queryParams.studentUserId" filterable clearable style="width:240px"><el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="班级"><el-select v-model="queryParams.classId" filterable clearable style="width:220px"><el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
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
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addCourseStudent, checkCourseStudentDuplicate, delCourseStudent, listCourseStudent, updateCourseStudent } from '@/api/campus/courseStudent'
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
const queryParams=reactive<any>({ pageNum:1,pageSize:10,courseId:undefined,studentUserId:undefined,classId:undefined })
const form=reactive<any>({})
const duplicateState = reactive<any>({ duplicate:false })
const headerStats = computed(() => [
  { label: '当前页关系', value: total.value, tip: '课程与学生映射清晰管理' },
  { label: '已启用', value: dataList.value.filter((item: any) => item.status === '0').length, tip: '便于快速核对可用关系' },
  { label: '课程覆盖', value: new Set(dataList.value.map((item: any) => item.courseId).filter(Boolean)).size, tip: '支持 AI 优化配置' },
])

function getOptionLabel(options: any[], value: any, fallback: string) {
  return options.find((item) => item.value === value)?.label || `${fallback} ${value || '-'}`
}
function resetForm(){ Object.assign(form,{ id:undefined,courseId:undefined,studentUserId:undefined,classId:undefined,status:'0',remark:'' }) }
function applyAiDraft(draft: Record<string, any>) { Object.assign(form, draft); syncClassByStudent() }
async function getList(){ loading.value=true; const res=await listCourseStudent(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.courseId=undefined; queryParams.studentUserId=undefined; queryParams.classId=undefined; getList() }
function handleSelectionChange(selection:any[]){ selectedRows.value = selection; ids.value=selection.map(i=>i.id); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetForm(); Object.assign(duplicateState,{ duplicate:false }); title.value='新增选课关系'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.id===ids.value[0]); if(!item) return; resetForm(); Object.assign(duplicateState,{ duplicate:false }); Object.assign(form,item); title.value='修改选课关系'; open.value=true }
async function submitForm(){
  syncClassByStudent()
  await checkDuplicate()
  if(duplicateState.duplicate){ ElMessage.warning('当前学生已选过该课程，请直接修改现有记录'); return }
  if(form.id){ await updateCourseStudent(form); ElMessage.success('修改成功') } else { await addCourseStudent(form); ElMessage.success('新增成功') }
  open.value=false; getList()
}
async function handleDelete(row?:any){ const target=row?.id || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选选课关系吗？','提示',{type:'warning'}); await delCourseStudent(target); ElMessage.success('删除成功'); getList() }
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
  await autoArrangeCourseSchedule({ termId, classCourseIds, clearExistingSchedules:false, populationSize:60, generationCount:120, mutationRate:0.12 })
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
      classId: profile?.classId,
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
.teaching-page-shell{display:grid;grid-template-columns:minmax(0,1.15fr) minmax(320px,.85fr);gap:16px;margin-bottom:16px;padding:18px;border:1px solid #dbe3f0;border-radius:18px;background:linear-gradient(180deg,#fff 0%,#f7fbff 100%)}
.teaching-page-shell__eyebrow{color:#607188;font-size:12px;font-weight:700;letter-spacing:.08em}
.teaching-page-shell__copy h2{margin:8px 0 10px;color:#172033;font-size:24px;line-height:1.2}
.teaching-page-shell__copy p{margin:0;color:#526076;font-size:13px;line-height:1.8}
.teaching-page-shell__stats{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:10px}
.teaching-page-shell__stat{display:flex;flex-direction:column;gap:6px;padding:14px;border-radius:16px;background:#fff;border:1px solid rgba(219,227,240,.92)}
.teaching-page-shell__stat span,.teaching-page-shell__stat small{color:#667085;font-size:12px}
.teaching-page-shell__stat strong{color:#172033;font-size:22px;line-height:1.1}
.entity-cell{display:flex;flex-direction:column;gap:6px}
.entity-cell strong{color:#172033;font-size:13px}
.entity-cell span{color:#667085;font-size:12px}
@media (max-width: 1100px){.teaching-page-shell{grid-template-columns:1fr}.teaching-page-shell__stats{grid-template-columns:1fr}}
</style>
