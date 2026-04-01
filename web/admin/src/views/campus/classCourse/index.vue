<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="班级"><el-select v-model="queryParams.classId" filterable clearable style="width:220px"><el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="课程"><el-select v-model="queryParams.courseId" filterable clearable style="width:220px"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="学期"><el-select v-model="queryParams.termId" filterable clearable style="width:220px"><el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="开课部门">
        <el-tree-select
          v-model="queryParams.openDeptId"
          :data="deptOptions"
          :props="{ value: 'id', label: 'label', children: 'children' }"
          value-key="id"
          check-strictly
          clearable
          filterable
          style="width:220px"
        />
      </el-form-item>
      <el-form-item label="专业"><el-select v-model="queryParams.major" filterable clearable style="width:220px"><el-option v-for="dict in campus_major_type" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></el-form-item>
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
      <el-table-column label="班级课程" min-width="300">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.selectionOptionName || row.courseName || getOptionLabel(courseOptions, row.courseId, '课程') }}</strong>
            <span>{{ row.className || getOptionLabel(classOptions, row.classId, '班级') }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="任课教师" min-width="180"><template #default="{ row }">{{ row.teacherName || getOptionLabel(teacherOptions, row.teacherId, '教师') }}</template></el-table-column>
      <el-table-column label="学期" min-width="180"><template #default="{ row }">{{ row.termName || getOptionLabel(termOptions, row.termId, '学期') }}</template></el-table-column>
      <el-table-column label="开课部门" min-width="180"><template #default="{ row }">{{ row.openDeptName || '-' }}</template></el-table-column>
      <el-table-column label="专业" prop="major" min-width="160" />
      <el-table-column label="教学班代码" prop="teachingClassCode" min-width="150" />
      <el-table-column label="合班编码" min-width="130">
        <template #default="{ row }">
          <el-tag v-if="row.combinedClassCode" size="small" type="warning">{ { row.combinedClassCode } }</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="专项分组" min-width="180">
        <template #default="{ row }">{{ row.selectionGroupName || row.selectionGroupCode || '-' }}</template>
      </el-table-column>
      <el-table-column label="组内限选" width="100">
        <template #default="{ row }">{{ row.selectionGroupCode ? (row.selectionGroupLimit || 1) : '-' }}</template>
      </el-table-column>
      <el-table-column label="周学时" prop="weeklyHours" width="90" />
      <el-table-column label="总课时" prop="totalHours" width="90" />
      <el-table-column label="完成率" width="140">
        <template #default="{ row }">
          <div class="progress-cell">
            <el-progress :percentage="getCompletionPercent(row)" :stroke-width="10" :show-text="false" />
            <span>{{ getCompletionText(row) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="学分" prop="credits" width="90" />
      <el-table-column label="实有人数" width="100"><template #default="{ row }">{{ row.actualStudentCount ?? 0 }}</template></el-table-column>
      <el-table-column label="人数上限" prop="studentLimit" width="100" />
      <el-table-column label="容量使用" width="120"><template #default="{ row }">{{ formatCapacity(row) }}</template></el-table-column>
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="210" fixed="right"><template #default="{ row }"><el-button v-if="canCopyOption(row)" link type="primary" icon="DocumentCopy" @click="handleCopyOption(row)">复制专项课</el-button><el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button></template></el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="960px">
      <TeachingAiAssist module-key="classCourse" module-label="班级课程" :form-data="form" :selected-rows="selectedRows" :available-options="{ classOptions, courseOptions, teacherOptions, termOptions }" @apply="applyAiDraft" />
      <div class="dialog-dual-grid">
        <div class="dialog-panel">
          <div class="dialog-panel__title">基础信息</div>
          <el-form :model="form" label-width="92px">
            <el-form-item label="班级"><el-select v-model="form.classId" filterable clearable style="width:100%"><el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
            <el-form-item label="课程"><el-select v-model="form.courseId" filterable clearable style="width:100%"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
            <el-form-item label="教师"><el-select v-model="form.teacherId" filterable clearable style="width:100%"><el-option v-for="item in teacherOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
            <el-form-item label="学期"><el-select v-model="form.termId" filterable clearable style="width:100%"><el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
            <el-form-item label="业务类型"><el-input v-model="form.businessType" placeholder="例如 专科" /></el-form-item>
            <el-form-item label="教学班代码">
              <div class="code-field">
                <el-input v-model="form.teachingClassCode" placeholder="请输入教学班代码" />
                <el-button plain @click="handleGenerateTeachingCode">自动生成</el-button>
              </div>
            </el-form-item>
            <el-form-item label="学分"><el-input-number v-model="form.credits" :min="0" :max="20" :precision="1" style="width:100%" /></el-form-item>
            <el-form-item label="课程类别"><el-input v-model="form.courseCategory" placeholder="例如 理论 / 实训" /></el-form-item>
            <el-form-item label="专项名称"><el-input v-model="form.selectionOptionName" placeholder="例如 篮球 / 排球，可为空" /></el-form-item>
            <el-form-item label="开课部门">
              <el-tree-select
                v-model="form.openDeptId"
                :data="deptOptions"
                :props="{ value: 'id', label: 'label', children: 'children' }"
                value-key="id"
                check-strictly
                clearable
                filterable
                style="width:100%"
              />
            </el-form-item>
            <el-form-item label="专业"><el-select v-model="form.major" filterable clearable allow-create default-first-option style="width:100%"><el-option v-for="dict in campus_major_type" :key="`major-${dict.value}`" :label="dict.label" :value="dict.value" /></el-select></el-form-item>
          </el-form>
        </div>

        <div class="dialog-panel">
          <div class="dialog-panel__title">教学属性</div>
          <el-form :model="form" label-width="92px">
            <el-form-item label="授课校区"><el-input v-model="form.campusName" placeholder="例如 本部" /></el-form-item>
            <el-form-item label="考核方式"><el-input v-model="form.assessmentType" placeholder="例如 考查 / 考试" /></el-form-item>
            <el-form-item label="授课语言"><el-input v-model="form.teachingLanguage" placeholder="例如 中文授课" /></el-form-item>
            <el-form-item label="先修课程"><el-input v-model="form.prerequisiteCourse" placeholder="例如 无" /></el-form-item>
            <el-form-item label="任务类型"><el-input v-model="form.taskType" placeholder="例如 正常" /></el-form-item>
            <el-form-item label="是否必修"><el-select v-model="form.requiredFlag" clearable style="width:100%"><el-option label="是" value="Y" /><el-option label="否" value="N" /></el-select></el-form-item>
            <div class="selection-group-panel" :class="{ 'is-active': isSelectionGroupScenario }">
              <div class="selection-group-panel__head">
                <div class="selection-group-panel__title">
                  <strong>专项选课配置</strong>
                  <span>{{ isSelectionGroupScenario ? '当前课程看起来像专项/体育课，建议在这里配置专项名称和分组选课规则。' : '普通课程可保持为空；体育专项、方向分流等场景再展开配置。' }}</span>
                </div>
                <el-button link type="primary" @click="selectionGroupPanelOpen = !selectionGroupPanelOpen">
                  {{ selectionGroupPanelExpanded ? '收起' : '展开' }}
                </el-button>
              </div>
              <div v-if="selectionGroupPanelExpanded" class="selection-group-panel__body">
                <el-form-item label="分组编码">
                  <div class="code-field">
                    <el-input v-model="form.selectionGroupCode" placeholder="例如 SG0001" />
                    <el-button plain @click="handleGenerateSelectionGroupCode">自动生成</el-button>
                  </div>
                </el-form-item>
                <el-form-item label="分组名称"><el-input v-model="form.selectionGroupName" placeholder="例如 体育专项选课" /></el-form-item>
                <el-form-item label="组内限选"><el-input-number v-model="form.selectionGroupLimit" :min="1" :max="10" style="width:100%" /></el-form-item>
              </div>
            </div>
            <el-form-item label="合班编码"><el-input v-model="form.combinedClassCode" placeholder="相同编码的班级课程合班上课" clearable /></el-form-item>
            <el-form-item label="等级要求"><el-input v-model="form.courseLevelRequirement" placeholder="例如 无" /></el-form-item>
            <el-form-item label="总课时"><el-input-number v-model="form.totalHours" :min="0" :max="500" style="width:100%" @change="syncTeachingLoad('total')" /></el-form-item>
            <el-form-item label="要求周数"><el-input-number v-model="form.requiredWeeks" :min="0" :max="40" style="width:100%" @change="syncTeachingLoad('weeks')" /></el-form-item>
            <el-form-item label="周学时"><el-input-number v-model="form.weeklyHours" :min="0" :max="500" style="width:100%" @change="syncTeachingLoad('weekly')" /></el-form-item>
            <el-form-item label="已排课时"><el-input-number v-model="form.arrangedHours" :min="0" :max="500" style="width:100%" disabled /></el-form-item>
            <div class="teaching-load-tip">
              <strong>教学负荷推算</strong>
              <span>{{ teachingLoadSummary }}</span>
            </div>
            <el-form-item label="人数上限"><el-input-number v-model="form.studentLimit" :min="0" :max="200" style="width:100%" /></el-form-item>
            <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
            <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="4" /></el-form-item>
          </el-form>
        </div>
      </div>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
// @ts-nocheck
import { computed, getCurrentInstance, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchClassOptions, fetchCourseOptions, fetchTermOptions, fetchTeachingUserOptions } from '@/api/campus/options'
import { addClassCourse, delClassCourse, generateSelectionGroupCode, generateTeachingClassCode, listClassCourse, updateClassCourse } from '@/api/campus/teaching'
import { deptTreeSelect } from '@/api/system/user'
import TeachingAiAssist from '@/components/Teaching/TeachingAiAssist.vue'

const { proxy } = getCurrentInstance() as any
const { campus_major_type } = proxy.useDict('campus_major_type')
const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([]), selectedRows=ref<any[]>([])
const classOptions=ref<any[]>([])
const courseOptions=ref<any[]>([])
const teacherOptions=ref<any[]>([])
const termOptions=ref<any[]>([])
const deptOptions=ref<any[]>([])
const queryParams=reactive<any>({ pageNum:1,pageSize:10,classId:undefined,courseId:undefined,termId:undefined,openDeptId:undefined,major:undefined })
const form=reactive<any>({})
const selectionGroupPanelOpen = ref(false)
const teachingLoadSummary = computed(() => {
  const total = Number(form.totalHours || 0)
  const weeks = Number(form.requiredWeeks || 0)
  const weekly = Number(form.weeklyHours || 0)
  const arranged = Number(form.arrangedHours || 0)
  const remain = total > 0 ? Math.max(total - arranged, 0) : 0
  if (!total && !weeks && !weekly) return '建议至少填写其中两项，系统会自动推算另一项。已排课时由排课表自动累计。'
  return `当前按 总课时 ${total || 0} / 要求周数 ${weeks || 0} / 周学时 ${weekly || 0} 推算，剩余待排 ${remain} 学时。`
})
function getOptionLabel(options:any[], value:any, fallback:string){ return options.find((item)=>item.value===value)?.label || `${fallback} ${value || '-'}` }
function formatCapacity(row:any){
  const actual = Number(row?.actualStudentCount ?? 0)
  const limit = Number(row?.studentLimit ?? 0)
  if (!limit) return actual ? `${actual} / -` : '-'
  return `${actual} / ${limit}`
}
function getCompletionPercent(row:any){
  const total = Number(row?.totalHours || 0)
  const arranged = Number(row?.arrangedHours || 0)
  if(total <= 0) return 0
  return Math.min(100, Math.max(0, Math.round(arranged / total * 100)))
}
function getCompletionText(row:any){
  const arranged = Number(row?.arrangedHours || 0)
  const total = Number(row?.totalHours || 0)
  if(total <= 0) return `${arranged} / -`
  return `${arranged} / ${total}`
}
const currentCourseLabel = computed(() => {
  return courseOptions.value.find((item:any) => item.value === form.courseId)?.label || String(form.courseName || '')
})
const isSportsLikeCourse = computed(() => {
  const courseText = `${currentCourseLabel.value || ''} ${form.courseCategory || ''}`.trim()
  return courseText.includes('体育')
})
const hasSelectionGroupConfig = computed(() => {
  return Boolean(String(form.selectionOptionName || '').trim()
    || String(form.selectionGroupCode || '').trim()
    || String(form.selectionGroupName || '').trim())
})
const isSelectionGroupScenario = computed(() => isSportsLikeCourse.value || hasSelectionGroupConfig.value)
const selectionGroupPanelExpanded = computed(() => selectionGroupPanelOpen.value || isSelectionGroupScenario.value)
function resetForm(){ selectionGroupPanelOpen.value = false; Object.assign(form,{ id:undefined,classId:undefined,courseId:undefined,teacherId:undefined,termId:undefined,businessType:'专科',teachingClassCode:'',credits:0,courseCategory:'理论',openDeptId:undefined,major:undefined,campusName:'本部',assessmentType:'考查',teachingLanguage:'中文授课',prerequisiteCourse:'无',taskType:'正常',requiredFlag:'N',selectionOptionName:'',selectionGroupCode:'',selectionGroupName:'',selectionGroupLimit:1,combinedClassCode:'',courseLevelRequirement:'无',totalHours:0,requiredWeeks:15,weeklyHours:0,arrangedHours:0,studentLimit:0,status:'0',remark:'' }) }
function applyAiDraft(draft: Record<string, any>) { Object.assign(form, draft) }
async function getList(){ loading.value=true; const res=await listClassCourse(queryParams); dataList.value=res.rows||[]; total.value=res.total||0; loading.value=false }
function resetQuery(){ queryParams.pageNum=1; queryParams.classId=undefined; queryParams.courseId=undefined; queryParams.termId=undefined; queryParams.openDeptId=undefined; queryParams.major=undefined; getList() }
function handleSelectionChange(selection:any[]){ selectedRows.value=selection; ids.value=selection.map((i:any)=>i.id); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetForm(); title.value='新增班级课程'; open.value=true }
function handleUpdate(row?:any){ const item=row || dataList.value.find((i:any)=>i.id===ids.value[0]); if(!item) return; resetForm(); Object.assign(form,item); selectionGroupPanelOpen.value = false; title.value='修改班级课程'; open.value=true }
function handleCopyOption(row:any){
  if(!row) return
  resetForm()
  Object.assign(form, row, {
    id: undefined,
    teachingClassCode: '',
    selectionOptionName: '',
    arrangedHours: 0,
    status: '0',
  })
  selectionGroupPanelOpen.value = true
  title.value = '复制新增专项课'
  open.value = true
}
function canCopyOption(row:any){
  if(!row) return false
  if(String(row.selectionOptionName || '').trim()) return true
  if(String(row.selectionGroupCode || '').trim()) return true
  const courseName = String(row.courseName || '').trim()
  const courseCategory = String(row.courseCategory || '').trim()
  return courseName.includes('体育') || courseCategory.includes('体育')
}
async function submitForm(){ if(form.id){ await updateClassCourse(form); ElMessage.success('修改成功') } else { await addClassCourse(form); ElMessage.success('新增成功') } open.value=false; getList() }
async function handleDelete(row?:any){ const target=row?.id || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选班级课程吗？','提示',{type:'warning'}); await delClassCourse(target); ElMessage.success('删除成功'); getList() }
async function handleGenerateTeachingCode(){
  const res = await generateTeachingClassCode(form)
  if(res.data?.teachingClassCode){ form.teachingClassCode = res.data.teachingClassCode; ElMessage.success(`已生成教学班代码：${res.data.teachingClassCode}`) }
}
async function handleGenerateSelectionGroupCode(){
  const res = await generateSelectionGroupCode(form)
  if(res.data?.selectionGroupCode){
    form.selectionGroupCode = res.data.selectionGroupCode
    if(!form.selectionGroupName) form.selectionGroupName = '专项选课分组'
    ElMessage.success(`已生成分组编码：${res.data.selectionGroupCode}`)
  }
}
function syncTeachingLoad(source: 'total' | 'weeks' | 'weekly'){
  const total = Number(form.totalHours || 0)
  const weeks = Number(form.requiredWeeks || 0)
  const weekly = Number(form.weeklyHours || 0)
  if(source === 'total'){
    if(total > 0 && weekly > 0){
      form.requiredWeeks = Math.max(1, Math.ceil(total / weekly))
    } else if(total > 0 && weeks > 0){
      form.weeklyHours = Math.max(1, Math.ceil(total / weeks))
    }
    return
  }
  if(source === 'weeks'){
    if(weeks > 0 && weekly > 0){
      form.totalHours = weeks * weekly
    } else if(weeks > 0 && total > 0){
      form.weeklyHours = Math.max(1, Math.ceil(total / weeks))
    }
    return
  }
  if(weekly > 0 && weeks > 0){
    form.totalHours = weekly * weeks
  } else if(weekly > 0 && total > 0){
    form.requiredWeeks = Math.max(1, Math.ceil(total / weekly))
  }
}
async function loadOptions(){ classOptions.value=await fetchClassOptions(); courseOptions.value=await fetchCourseOptions(); teacherOptions.value=await fetchTeachingUserOptions('course_teacher'); termOptions.value=await fetchTermOptions(); const deptRes = await deptTreeSelect(); deptOptions.value = deptRes.data || [] }
watch(() => queryParams.openDeptId, async (value) => {
  classOptions.value = await fetchClassOptions(undefined, value)
  if (!classOptions.value.some((item:any) => item.value === queryParams.classId)) queryParams.classId = undefined
})
watch(() => form.classId, () => {
  const target = classOptions.value.find((item:any) => item.value === form.classId)
  if (target?.deptId && !form.openDeptId) {
    form.openDeptId = target.deptId
  }
})
onMounted(async()=>{ await loadOptions(); resetForm(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px}
.entity-cell{display:flex;flex-direction:column;gap:6px}
.entity-cell strong{color:#172033;font-size:13px}
.entity-cell span{color:#667085;font-size:12px}
.progress-cell{display:flex;align-items:center;gap:10px}
.progress-cell span{flex:0 0 auto;color:#526076;font-size:12px;font-weight:600}
.progress-cell :deep(.el-progress){flex:1}
.code-field{display:flex;align-items:center;gap:8px;width:100%}
.code-field :deep(.el-input){flex:1}
.dialog-dual-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:16px}
.dialog-panel{padding:14px 14px 6px;border:1px solid #e4ebf4;border-radius:16px;background:linear-gradient(180deg,#fff 0%,#fafcff 100%)}
.dialog-panel__title{margin-bottom:14px;color:#172033;font-size:14px;font-weight:700}
.selection-group-panel{margin-bottom:18px;padding:14px 14px 2px;border:1px dashed #d8e2f0;border-radius:14px;background:#fbfdff}
.selection-group-panel.is-active{border-style:solid;border-color:#f2c59a;background:linear-gradient(180deg,#fffaf5 0%,#fff 100%);box-shadow:0 8px 20px rgba(249,115,22,0.08)}
.selection-group-panel__head{display:flex;align-items:flex-start;justify-content:space-between;gap:12px;margin-bottom:8px}
.selection-group-panel__title strong{display:block;color:#172033;font-size:13px;margin-bottom:4px}
.selection-group-panel__title span{display:block;color:#667085;font-size:12px;line-height:1.7}
.selection-group-panel__body{padding-top:4px}
.teaching-load-tip{margin-top:-4px;margin-bottom:4px;padding:10px 12px;border-radius:12px;background:#f6f9fc;color:#526076;font-size:12px;line-height:1.7}
.teaching-load-tip strong{display:block;color:#172033;font-size:12px;margin-bottom:4px}
@media (max-width: 900px){.dialog-dual-grid{grid-template-columns:1fr}.selection-group-panel__head{flex-direction:column;align-items:stretch}}
</style>
