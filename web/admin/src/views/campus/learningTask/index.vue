<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="任务标题">
        <el-input v-model="queryParams.taskTitle" placeholder="请输入任务标题" clearable style="width: 220px" />
      </el-form-item>
      <el-form-item label="任务类型">
        <el-select v-model="queryParams.taskType" clearable style="width: 180px">
          <el-option v-for="item in taskTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable style="width: 160px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="mb16">
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">任务总数</div><div class="metric-value">{{ total }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">待派发任务</div><div class="metric-value warning">{{ pendingDispatchCount }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">考试类任务</div><div class="metric-value danger">{{ examTaskCount }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">作业/实验任务</div><div class="metric-value success">{{ practiceTaskCount }}</div></el-card></el-col>
    </el-row>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="warning" plain icon="Promotion" :disabled="single" @click="handleDispatch()">派发</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <right-toolbar @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="taskList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="任务ID" prop="taskId" width="90" />
      <el-table-column label="任务标题" prop="taskTitle" min-width="220" show-overflow-tooltip />
      <el-table-column label="任务类型" width="120">
        <template #default="scope">
          <el-tag :type="taskTypeTag(scope.row.taskType)" effect="plain">{{ taskTypeLabel(scope.row.taskType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="课程" min-width="180">
        <template #default="scope">{{ courseLabel(scope.row.courseId) }}</template>
      </el-table-column>
      <el-table-column label="优先级" width="100">
        <template #default="scope">{{ priorityLabel(scope.row.priorityLevel) }}</template>
      </el-table-column>
      <el-table-column label="开始时间" prop="startTime" min-width="160" />
      <el-table-column label="截止时间" prop="dueTime" min-width="160" />
      <el-table-column label="状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" effect="plain">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="warning" icon="Promotion" @click="handleDispatch(scope.row)">派发</el-button>
          <el-button link type="info" icon="View" @click="handleViewDispatch(scope.row)">派发记录</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="920px">
      <el-form :model="form" label-width="100px">
        <div class="task-step-strip">
          <button
            v-for="step in createStepOptions"
            :key="step.value"
            type="button"
            class="task-step-chip"
            :class="{ 'is-active': currentFormStep === step.value }"
            @click="currentFormStep = step.value"
          >
            <span>{{ step.index }}</span>
            <strong>{{ step.label }}</strong>
          </button>
        </div>
        <div v-if="currentFormStep === 0" class="task-template-strip">
          <button
            v-for="preset in taskPresets"
            :key="preset.value"
            type="button"
            class="task-template-chip"
            :class="{ 'is-active': form.taskType === preset.value }"
            @click="applyTaskPreset(preset.value)"
          >
            <strong>{{ preset.label }}</strong>
            <span>{{ preset.desc }}</span>
          </button>
        </div>
        <div v-if="currentFormStep === 0" class="task-form-section-title">基础信息</div>
        <el-row v-if="currentFormStep === 0" :gutter="16">
          <el-col :span="12"><el-form-item label="任务标题"><el-input v-model="form.taskTitle" maxlength="120" placeholder="例如：课后作业：Python数据分析第一章" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="任务类型">
              <div class="task-type-display">
                <el-tag :type="taskTypeTag(form.taskType)" effect="plain" size="large">{{ taskTypeLabel(form.taskType) }}</el-tag>
                <span>通过上方任务模板切换类型</span>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="课程"><el-select v-model="form.courseId" filterable clearable @change="handleCourseChange"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学期"><el-select v-model="form.termId" filterable clearable @change="loadClassCourseOptions"><el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="教学班"><el-select v-model="selectedClassCourseId" filterable clearable style="width: 100%" placeholder="选择教学班后自动回填业务ID、班级课程ID等信息" @change="handleClassCourseSelect"><el-option v-for="item in classCourseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
        </el-row>
        <div v-if="currentFormStep === 1" class="task-form-section-title">执行规则</div>
        <el-row v-if="currentFormStep === 1" :gutter="16">
          <el-col :span="12"><el-form-item label="优先级"><el-select v-model="form.priorityLevel"><el-option label="低" value="LOW" /><el-option label="普通" value="NORMAL" /><el-option label="高" value="HIGH" /><el-option label="紧急" value="URGENT" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="任务去向"><el-select v-model="form.actionType" @change="handleActionTypeChange"><el-option v-for="item in actionTypeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="跳转页面"><el-input v-model="form.actionPath" :readonly="form.actionType !== 'custom'" :placeholder="form.actionType === 'custom' ? '/student/plaza' : '系统自动生成'" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="截止时间"><el-date-picker v-model="form.dueTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="任务描述"><el-input v-model="form.taskDesc" type="textarea" :rows="4" maxlength="1000" show-word-limit /></el-form-item></el-col>
        </el-row>
        <div v-if="currentFormStep === 2" class="task-form-section-title">确认信息</div>
        <div v-if="currentFormStep === 2" class="task-preview-card">
          <div class="task-preview-card__title">{{ form.taskTitle || '未命名任务' }}</div>
          <div class="task-preview-card__meta">
            <span>{{ taskTypeLabel(form.taskType) }}</span>
            <span>{{ courseLabel(form.courseId) }}</span>
            <span>{{ form.dueTime ? `截止 ${form.dueTime}` : '未设置截止时间' }}</span>
          </div>
          <p>{{ form.taskDesc || '暂无任务描述，保存后可继续编辑。' }}</p>
        </div>
        <div v-if="currentFormStep === 2" class="task-generated-panel">
          <div class="task-generated-item">
            <span>业务类型</span>
            <strong>{{ form.bizType || '--' }}</strong>
          </div>
          <div class="task-generated-item">
            <span>业务ID</span>
            <strong>{{ bizDisplayValue }}</strong>
          </div>
          <div class="task-generated-item">
            <span>班级课程ID</span>
            <strong>{{ classCourseDisplayValue }}</strong>
          </div>
          <div class="task-generated-item">
            <span>动作目标</span>
            <strong>{{ actionTargetDisplayValue }}</strong>
          </div>
          <div class="task-generated-item task-generated-item--full">
            <span>当前说明</span>
            <strong>{{ generatedHint }}</strong>
          </div>
        </div>
        <el-checkbox v-if="currentFormStep === 2" v-model="saveAndDispatch" class="task-save-option">
          保存后立即进入派发流程
        </el-checkbox>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button v-if="currentFormStep > 0" @click="currentFormStep -= 1">上一步</el-button>
        <el-button v-if="currentFormStep < 2" type="primary" @click="goNextStep">下一步</el-button>
        <el-button v-else type="primary" @click="submitForm">{{ saveAndDispatch ? '保存并派发' : '保存任务' }}</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dispatchOpen" title="任务派发" width="680px">
      <el-form label-width="100px">
        <el-form-item label="当前任务">
          <div class="dispatch-title">{{ currentTask?.taskTitle || '--' }}</div>
        </el-form-item>
        <el-form-item label="派发学生">
          <el-segmented v-model="dispatchMode" :options="dispatchModeOptions" class="mb16" />
        </el-form-item>
        <el-form-item v-if="dispatchMode === 'users'" label="派发学生">
          <el-select v-model="dispatchUserIds" multiple filterable clearable style="width: 100%" placeholder="请选择学生">
            <el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dispatchMode === 'class'" label="班级">
          <el-select v-model="dispatchClassId" filterable clearable style="width: 100%" placeholder="请选择班级">
            <el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dispatchMode === 'course'" label="课程">
          <el-select v-model="dispatchCourseId" filterable clearable style="width: 100%" placeholder="请选择课程">
            <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dispatchOpen = false">取消</el-button>
        <el-button type="primary" @click="submitDispatch">确认派发</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dispatchRecordOpen" title="任务执行情况" width="1180px">
      <el-table v-loading="dispatchLoading" :data="dispatchList">
        <el-table-column label="派发ID" prop="dispatchId" width="90" />
        <el-table-column label="学生" min-width="180">
          <template #default="scope">{{ userLabel(scope.row.userId) }}</template>
        </el-table-column>
        <el-table-column label="派发状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.dispatchStatus === '0' ? 'success' : 'info'" effect="plain">{{ scope.row.dispatchStatus === '0' ? '有效' : '取消' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="完成状态" width="120">
          <template #default="scope">
            <el-select v-model="scope.row.completionStatus" style="width: 100%" @change="updateDispatchStatus(scope.row)">
              <el-option label="待完成" value="PENDING" />
              <el-option label="进行中" value="IN_PROGRESS" />
              <el-option label="已完成" value="COMPLETED" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="完成时间" prop="completedTime" min-width="160" />
        <el-table-column label="阅读时间" prop="readTime" min-width="160" />
        <el-table-column label="完成说明" prop="completionRemark" min-width="180" show-overflow-tooltip />
        <el-table-column label="提交时间" prop="submitTime" min-width="160" />
        <el-table-column label="提交内容" min-width="260" show-overflow-tooltip>
          <template #default="scope">{{ scope.row.submitContent || '--' }}</template>
        </el-table-column>
        <el-table-column label="附件" min-width="220">
          <template #default="scope">
            <div v-if="parseAttachmentList(scope.row.attachmentUrls).length" class="submission-attachments">
              <a
                v-for="(item, index) in parseAttachmentList(scope.row.attachmentUrls)"
                :key="`${item.url}-${index}`"
                :href="item.url"
                target="_blank"
                rel="noreferrer"
              >
                {{ item.name }}
              </a>
            </div>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column label="批阅状态" width="120">
          <template #default="scope">
            <el-select v-model="scope.row.reviewStatus" style="width: 100%">
              <el-option label="待批阅" value="PENDING" />
              <el-option label="已批阅" value="REVIEWED" />
              <el-option label="需修改" value="RETURNED" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.score" :min="0" :max="100" :precision="2" style="width: 100%" />
          </template>
        </el-table-column>
        <el-table-column label="教师反馈" min-width="240">
          <template #default="scope">
            <el-input v-model="scope.row.teacherFeedback" type="textarea" :rows="2" maxlength="300" show-word-limit />
          </template>
        </el-table-column>
        <el-table-column label="保存" width="90" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="saveSubmissionReview(scope.row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addLearningTask, delLearningTask, dispatchLearningTask, dispatchLearningTaskToClass, dispatchLearningTaskToCourse, getLearningTask, listLearningTask, listLearningTaskDispatch, listLearningTaskSubmission, updateLearningTask, updateLearningTaskDispatch, updateLearningTaskSubmission } from '@/api/campus/learningTask'
import { fetchClassOptions, fetchCourseOptions, fetchTermOptions, fetchUserOptions } from '@/api/campus/options'
import { listClassCourse } from '@/api/campus/teaching'

const loading = ref(false)
const dispatchLoading = ref(false)
const open = ref(false)
const dispatchOpen = ref(false)
const dispatchRecordOpen = ref(false)
const title = ref('')
const total = ref(0)
const single = ref(true)
const multiple = ref(true)
const ids = ref<any[]>([])
const taskList = ref<any[]>([])
const dispatchList = ref<any[]>([])
const courseOptions = ref<any[]>([])
const studentOptions = ref<any[]>([])
const currentTask = ref<any>(null)
const dispatchUserIds = ref<number[]>([])
const dispatchClassId = ref<number | undefined>(undefined)
const dispatchCourseId = ref<number | undefined>(undefined)
const dispatchMode = ref<'users' | 'class' | 'course'>('users')
const termOptions = ref<any[]>([])
const classCourseOptions = ref<any[]>([])
const selectedClassCourseId = ref<number | undefined>(undefined)
const currentFormStep = ref(0)
const saveAndDispatch = ref(true)

const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, taskTitle: undefined, taskType: undefined, status: undefined })
const form = reactive<any>({})

const taskTypeOptions = [
  { label: '作业任务', value: 'HOMEWORK' },
  { label: '考试任务', value: 'EXAM' },
  { label: '实验任务', value: 'PRACTICE' },
  { label: '阅读任务', value: 'READING' },
  { label: '课程任务', value: 'COURSE' },
]
const taskPresets = [
  { value: 'HOMEWORK', label: '作业任务', desc: '课后作业、章节练习、报告提交' },
  { value: 'EXAM', label: '考试任务', desc: '阶段测验、期中期末、统一考试' },
  { value: 'PRACTICE', label: '实验任务', desc: '实验、实训、上机和实践项目' },
  { value: 'READING', label: '阅读任务', desc: '阅读、预习、资料查阅与打卡' },
]
const createStepOptions = [
  { value: 0, index: '01', label: '选任务' },
  { value: 1, index: '02', label: '配规则' },
  { value: 2, index: '03', label: '确认并保存' },
]
const dispatchModeOptions = [
  { label: '指定学生', value: 'users' },
  { label: '按班级派发', value: 'class' },
  { label: '按课程派发', value: 'course' },
]
const actionTypeOptions = [
  { label: '课程页', value: 'course' },
  { label: '考试页', value: 'exam' },
  { label: '错题页', value: 'wrongbook' },
  { label: '自定义页面', value: 'custom' }
]
const classOptions = ref<any[]>([])

const pendingDispatchCount = computed(() => taskList.value.filter((item: any) => !item.actionPath).length)
const examTaskCount = computed(() => taskList.value.filter((item: any) => item.taskType === 'EXAM').length)
const practiceTaskCount = computed(() => taskList.value.filter((item: any) => ['HOMEWORK', 'PRACTICE'].includes(item.taskType)).length)
const bizDisplayValue = computed(() => form.bizId ? `${form.bizId}` : '自动生成')
const classCourseDisplayValue = computed(() => form.classCourseId ? `${form.classCourseId}` : '未选择')
const actionTargetDisplayValue = computed(() => {
  if (!form.actionTargetId) return '自动回填'
  if (form.actionType === 'course') {
    const match = classCourseOptions.value.find((item: any) => String(item.value) === String(form.actionTargetId))
    return match?.label || `${form.actionTargetId}`
  }
  if (form.actionType === 'exam') {
    return courseLabel(form.actionTargetId)
  }
  return `${form.actionTargetId}`
})
const generatedHint = computed(() => {
  const courseText = courseLabel(form.courseId)
  const pathText = form.actionPath || '--'
  return `当前任务将按“${taskTypeLabel(form.taskType)}”创建，默认引导学生进入 ${pathText} 处理，关联课程为 ${courseText}。`
})

function taskTypeLabel(value: string) {
  return taskTypeOptions.find((item) => item.value === value)?.label || value || '--'
}

function taskTypeTag(value: string) {
  if (value === 'EXAM') return 'danger'
  if (value === 'HOMEWORK') return 'warning'
  if (value === 'PRACTICE') return 'success'
  if (value === 'READING') return 'info'
  return 'primary'
}

function priorityLabel(value: string) {
  const map: Record<string, string> = { LOW: '低', NORMAL: '普通', HIGH: '高', URGENT: '紧急' }
  return map[value] || value || '--'
}

function userLabel(userId: number | string) {
  return studentOptions.value.find((item: any) => item.value === userId)?.label || `学生 ${userId}`
}

function courseLabel(courseId: number | string) {
  return courseOptions.value.find((item: any) => item.value === courseId)?.label || (courseId ? `课程 ${courseId}` : '--')
}

function parseAttachmentList(value?: string) {
  if (!value) return []
  try {
    const parsed = JSON.parse(value)
    if (!Array.isArray(parsed)) return []
    return parsed.map((item: any) => {
      const url = String(item || '')
      const parts = url.split('/')
      return {
        url,
        name: decodeURIComponent(parts[parts.length - 1] || url)
      }
    }).filter((item: any) => item.url)
  } catch {
    return []
  }
}

function resetFormData() {
  Object.assign(form, {
    taskId: undefined,
    taskTitle: '',
    taskType: 'HOMEWORK',
    bizType: '',
    bizId: undefined,
    courseId: undefined,
    classCourseId: undefined,
    termId: currentDefaultTermId(),
    taskDesc: '',
    priorityLevel: 'NORMAL',
    actionType: 'custom',
    actionPath: '/student/plaza',
    actionTargetId: undefined,
    startTime: undefined,
    dueTime: undefined,
    status: '0'
  })
  selectedClassCourseId.value = undefined
  classCourseOptions.value = []
  currentFormStep.value = 0
  saveAndDispatch.value = true
  applyTaskPreset('HOMEWORK')
}

async function getList() {
  loading.value = true
  try {
    const res = await listLearningTask(queryParams)
    taskList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.taskTitle = undefined
  queryParams.taskType = undefined
  queryParams.status = undefined
  getList()
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.taskId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  resetFormData()
  title.value = '新增学习任务'
  open.value = true
}

async function handleUpdate(row?: any) {
  resetFormData()
  const taskId = row?.taskId || ids.value[0]
  const res = await getLearningTask(taskId)
  Object.assign(form, res.data || {})
  selectedClassCourseId.value = res.data?.classCourseId
  if (form.courseId || form.termId) {
    await loadClassCourseOptions()
  }
  title.value = '修改学习任务'
  open.value = true
}

async function submitForm() {
  const payload = { ...form }
  if (!payload.taskTitle?.trim()) {
    ElMessage.warning('请填写任务标题')
    return
  }
  if (!payload.taskType) {
    ElMessage.warning('请选择任务类型')
    return
  }
  if (!payload.courseId) {
    ElMessage.warning('请选择课程')
    return
  }
  if (payload.taskId) {
    const res = await updateLearningTask(payload)
    ElMessage.success('修改成功')
    if (saveAndDispatch.value) {
      currentTask.value = { ...payload, taskId: res.taskId || payload.taskId }
      dispatchCourseId.value = currentTask.value?.courseId
      dispatchMode.value = currentTask.value?.courseId ? 'course' : 'users'
      dispatchOpen.value = true
    }
  } else {
    const res = await addLearningTask(payload)
    ElMessage.success('新增成功')
    if (saveAndDispatch.value) {
      currentTask.value = { ...payload, taskId: res.taskId }
      dispatchCourseId.value = currentTask.value?.courseId
      dispatchMode.value = currentTask.value?.courseId ? 'course' : 'users'
      dispatchOpen.value = true
    }
  }
  open.value = false
  getList()
}

function goNextStep() {
  if (currentFormStep.value === 0) {
    if (!form.taskTitle?.trim()) {
      ElMessage.warning('请先填写任务标题')
      return
    }
    if (!form.courseId) {
      ElMessage.warning('请先选择课程')
      return
    }
  }
  if (currentFormStep.value === 1) {
    if (!form.actionType) {
      ElMessage.warning('请先选择任务去向')
      return
    }
  }
  currentFormStep.value += 1
}

function applyTaskPreset(taskType: string) {
  form.taskType = taskType
  if (taskType === 'HOMEWORK') {
    form.bizType = 'HOMEWORK'
    form.actionType = 'course'
    form.actionPath = '/student/courses'
    form.priorityLevel = 'NORMAL'
  } else if (taskType === 'EXAM') {
    form.bizType = 'EXAM'
    form.actionType = 'exam'
    form.actionPath = '/student/exams'
    form.priorityLevel = 'HIGH'
  } else if (taskType === 'PRACTICE') {
    form.bizType = 'PRACTICE'
    form.actionType = 'course'
    form.actionPath = '/student/courses'
    form.priorityLevel = 'NORMAL'
  } else if (taskType === 'READING') {
    form.bizType = 'READING'
    form.actionType = 'custom'
    form.actionPath = '/student/resources'
    form.priorityLevel = 'LOW'
  }
}

function handleTaskTypeChange(value: string) {
  applyTaskPreset(value)
}

function handleActionTypeChange(value: string) {
  if (value === 'course') {
    form.actionPath = '/student/courses'
    form.actionTargetId = form.classCourseId || form.courseId
    return
  }
  if (value === 'exam') {
    form.actionPath = '/student/exams'
    form.actionTargetId = form.courseId
    return
  }
  if (value === 'wrongbook') {
    form.actionPath = '/student/wrongbook'
    return
  }
}

function currentDefaultTermId() {
  return termOptions.value.find((item: any) => item.isCurrent === '1')?.value
    || termOptions.value[0]?.value
    || undefined
}

function handleCourseChange() {
  selectedClassCourseId.value = undefined
  form.classCourseId = undefined
  form.bizId = form.courseId
  if (['course', 'exam'].includes(form.actionType)) {
    form.actionTargetId = form.courseId
  }
  loadClassCourseOptions()
}

async function loadClassCourseOptions() {
  if (!form.courseId && !form.termId) {
    classCourseOptions.value = []
    return
  }
  const res = await listClassCourse({
    pageNum: 1,
    pageSize: 300,
    courseId: form.courseId,
    termId: form.termId,
    status: '0'
  })
  classCourseOptions.value = (res.rows || []).map((item: any) => ({
    label: `${item.courseName || '未命名课程'} / ${item.className || '未命名教学班'}（${item.id}）${item.termName ? ' · ' + item.termName : ''}`,
    value: item.id,
    raw: item
  }))
}

function handleClassCourseSelect(value?: number) {
  const target = classCourseOptions.value.find((item: any) => item.value === value)?.raw
  if (!target) {
    form.classCourseId = undefined
    return
  }
  form.classCourseId = target.id
  form.courseId = target.courseId
  form.termId = target.termId
  form.bizId = target.id
  if (form.actionType === 'course') {
    form.actionTargetId = target.id
  } else if (form.actionType === 'exam') {
    form.actionTargetId = target.courseId
  }
  if (!form.taskTitle?.trim()) {
    form.taskTitle = buildTaskTitle(target)
  }
}

function buildTaskTitle(target: any) {
  const prefixMap: Record<string, string> = {
    HOMEWORK: '课后作业',
    EXAM: '课程考试',
    PRACTICE: '实验任务',
    READING: '阅读任务',
    COURSE: '课程任务'
  }
  return `${prefixMap[form.taskType] || '学习任务'}：${target.courseName || '未命名课程'}`
}

async function handleDelete(row?: any) {
  const taskIds = row?.taskId || ids.value
  if (!taskIds || (Array.isArray(taskIds) && !taskIds.length)) return
  await ElMessageBox.confirm('确认删除选中的学习任务吗？', '提示', { type: 'warning' })
  await delLearningTask(taskIds)
  ElMessage.success('删除成功')
  getList()
}

function handleDispatch(row?: any) {
  currentTask.value = row || taskList.value.find((item: any) => item.taskId === ids.value[0]) || null
  dispatchUserIds.value = []
  dispatchClassId.value = undefined
  dispatchCourseId.value = currentTask.value?.courseId
  dispatchMode.value = currentTask.value?.courseId ? 'course' : 'users'
  dispatchOpen.value = true
}

async function submitDispatch() {
  if (!currentTask.value?.taskId) {
    ElMessage.warning('当前任务无效')
    return
  }
  if (dispatchMode.value === 'users') {
    if (!dispatchUserIds.value.length) {
      ElMessage.warning('请先选择要派发的学生')
      return
    }
    await dispatchLearningTask(currentTask.value.taskId, dispatchUserIds.value)
  } else if (dispatchMode.value === 'class') {
    if (!dispatchClassId.value) {
      ElMessage.warning('请先选择班级')
      return
    }
    await dispatchLearningTaskToClass(currentTask.value.taskId, dispatchClassId.value)
  } else {
    if (!dispatchCourseId.value) {
      ElMessage.warning('请先选择课程')
      return
    }
    await dispatchLearningTaskToCourse(currentTask.value.taskId, dispatchCourseId.value)
  }
  ElMessage.success('任务派发成功')
  dispatchOpen.value = false
}

async function handleViewDispatch(row: any) {
  currentTask.value = row
  dispatchRecordOpen.value = true
  dispatchLoading.value = true
  try {
    const [dispatchRes, submissionRes] = await Promise.all([
      listLearningTaskDispatch({ pageNum: 1, pageSize: 200, taskId: row.taskId }),
      listLearningTaskSubmission({ pageNum: 1, pageSize: 200, taskId: row.taskId })
    ])
    const submissions = submissionRes.rows || []
    const submissionMap = new Map(submissions.map((item: any) => [item.dispatchId, item]))
    dispatchList.value = (dispatchRes.rows || []).map((item: any) => {
      const submission = submissionMap.get(item.dispatchId) as Record<string, any> | undefined
      return {
        ...item,
        ...(submission || {})
      }
    })
  } finally {
    dispatchLoading.value = false
  }
}

async function updateDispatchStatus(row: any) {
  const payload = { ...row }
  if (payload.completionStatus === 'COMPLETED' && !payload.completedTime) {
    const now = new Date()
    const year = now.getFullYear()
    const month = `${now.getMonth() + 1}`.padStart(2, '0')
    const day = `${now.getDate()}`.padStart(2, '0')
    const hour = `${now.getHours()}`.padStart(2, '0')
    const minute = `${now.getMinutes()}`.padStart(2, '0')
    const second = `${now.getSeconds()}`.padStart(2, '0')
    payload.completedTime = `${year}-${month}-${day} ${hour}:${minute}:${second}`
  }
  await updateLearningTaskDispatch(payload)
  ElMessage.success('派发状态已更新')
}

async function saveSubmissionReview(row: any) {
  if (!row?.submissionId) {
    ElMessage.warning('该学生尚未提交任务，暂无法批阅')
    return
  }
  await updateLearningTaskSubmission({
    submissionId: row.submissionId,
    dispatchId: row.dispatchId,
    taskId: row.taskId,
    userId: row.userId,
    reviewStatus: row.reviewStatus,
    score: row.score,
    teacherFeedback: row.teacherFeedback
  })
  ElMessage.success('批阅结果已保存')
}

async function loadOptions() {
  courseOptions.value = await fetchCourseOptions()
  studentOptions.value = await fetchUserOptions('student')
  classOptions.value = await fetchClassOptions()
  termOptions.value = await fetchTermOptions()
}

onMounted(async () => {
  await loadOptions()
  resetFormData()
  getList()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.metric-label { color: var(--el-text-color-secondary); }
.metric-value { margin-top: 8px; font-size: 28px; font-weight: 700; color: var(--el-color-primary); }
.metric-value.success { color: var(--el-color-success); }
.metric-value.warning { color: var(--el-color-warning); }
.metric-value.danger { color: var(--el-color-danger); }
.dispatch-title { font-weight: 600; color: var(--el-text-color-primary); }
.submission-attachments { display: flex; flex-direction: column; gap: 6px; }
.submission-attachments a { color: var(--el-color-primary); text-decoration: none; word-break: break-all; }
.task-step-strip { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 12px; margin-bottom: 18px; }
.task-step-chip { display: flex; align-items: center; gap: 10px; border: 1px solid var(--el-border-color); background: var(--el-bg-color); border-radius: 10px; padding: 12px 14px; text-align: left; cursor: pointer; transition: all .2s ease; }
.task-step-chip span { width: 34px; height: 34px; border-radius: 999px; display: inline-flex; align-items: center; justify-content: center; background: var(--el-fill-color-light); color: var(--el-text-color-secondary); font-size: 12px; font-weight: 700; flex-shrink: 0; }
.task-step-chip strong { font-size: 14px; color: var(--el-text-color-primary); }
.task-step-chip.is-active { border-color: var(--el-color-primary); background: var(--el-color-primary-light-9); }
.task-step-chip.is-active span { background: var(--el-color-primary); color: #fff; }
.task-form-section-title { margin: 4px 0 14px; font-size: 13px; font-weight: 700; color: var(--el-text-color-secondary); letter-spacing: .02em; }
.task-type-display { display: flex; align-items: center; gap: 10px; min-height: 32px; }
.task-type-display span { font-size: 12px; color: var(--el-text-color-secondary); }
.task-generated-panel { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; margin-top: 4px; }
.task-generated-item { padding: 12px 14px; border-radius: 10px; background: var(--el-fill-color-light); border: 1px solid var(--el-border-color-lighter); }
.task-generated-item span { display: block; font-size: 12px; color: var(--el-text-color-secondary); }
.task-generated-item strong { display: block; margin-top: 6px; font-size: 13px; color: var(--el-text-color-primary); line-height: 1.6; word-break: break-word; }
.task-generated-item--full { grid-column: 1 / -1; }
.task-preview-card { margin-bottom: 14px; padding: 16px 18px; border-radius: 12px; background: linear-gradient(180deg, var(--el-color-primary-light-9), #fff); border: 1px solid var(--el-color-primary-light-7); }
.task-preview-card__title { font-size: 18px; font-weight: 700; color: var(--el-text-color-primary); }
.task-preview-card__meta { margin-top: 8px; display: flex; flex-wrap: wrap; gap: 10px; font-size: 12px; color: var(--el-text-color-secondary); }
.task-preview-card p { margin: 12px 0 0; line-height: 1.8; color: var(--el-text-color-regular); }
.task-save-option { margin-top: 16px; }
.task-template-strip { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; margin-bottom: 18px; }
.task-template-chip { border: 1px solid var(--el-border-color); background: var(--el-bg-color); border-radius: 10px; padding: 12px 14px; text-align: left; cursor: pointer; transition: all .2s ease; }
.task-template-chip strong { display: block; font-size: 14px; color: var(--el-text-color-primary); }
.task-template-chip span { display: block; margin-top: 6px; font-size: 12px; line-height: 1.5; color: var(--el-text-color-secondary); }
.task-template-chip.is-active { border-color: var(--el-color-primary); background: var(--el-color-primary-light-9); box-shadow: 0 0 0 1px rgba(64, 158, 255, .12) inset; }
@media (max-width: 1200px) { .task-template-strip,.task-generated-panel,.task-step-strip { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
</style>
