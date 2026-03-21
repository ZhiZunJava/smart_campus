<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="用户">
        <el-select v-model="queryParams.userId" filterable clearable style="width: 240px">
          <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" filterable clearable style="width: 240px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="行为类型">
        <el-select v-model="queryParams.behaviorType" clearable style="width: 180px">
          <el-option v-for="item in behaviorTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="来源页面">
        <el-select v-model="queryParams.sourcePage" clearable style="width: 180px">
          <el-option v-for="item in sourcePageOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="mb16">
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">行为总数</div><div class="metric-value">{{ total }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">总时长(分钟)</div><div class="metric-value">{{ totalMinutes }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">平均进度</div><div class="metric-value">{{ avgProgress }}</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="never"><div class="metric-label">完成/提交</div><div class="metric-value success">{{ completedCount }}</div></el-card></el-col>
    </el-row>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <right-toolbar @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="记录ID" prop="recordId" width="90" />
      <el-table-column label="学生" min-width="180">
        <template #default="scope">{{ userLabel(scope.row.userId) }}</template>
      </el-table-column>
      <el-table-column label="课程" min-width="180">
        <template #default="scope">
          <span v-if="scope.row.courseId">{{ courseLabel(scope.row.courseId) }}</span>
          <el-tag v-else type="warning" effect="plain">未关联课程</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="知识点" min-width="180">
        <template #default="scope">{{ knowledgeLabel(scope.row.chapterId) }}</template>
      </el-table-column>
      <el-table-column label="资源ID" prop="resourceId" width="100" />
      <el-table-column label="行为类型" width="120">
        <template #default="scope">{{ behaviorTypeLabel(scope.row.behaviorType) }}</template>
      </el-table-column>
      <el-table-column label="学习时长(秒)" prop="durationSeconds" width="120" />
      <el-table-column label="进度(%)" prop="progressRate" width="100" />
      <el-table-column label="设备" prop="deviceType" width="100" />
      <el-table-column label="来源页面" prop="sourcePage" min-width="140" />
      <el-table-column label="记录时间" prop="createTime" min-width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="760px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="用户"><el-select v-model="form.userId" filterable clearable><el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="课程"><el-select v-model="form.courseId" filterable clearable><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="知识点"><el-select v-model="form.chapterId" filterable clearable><el-option v-for="item in filteredKnowledgeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资源ID"><el-input v-model="form.resourceId" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="行为类型"><el-select v-model="form.behaviorType"><el-option v-for="item in behaviorTypeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="设备"><el-select v-model="form.deviceType"><el-option label="Web" value="web" /><el-option label="App" value="app" /><el-option label="Pad" value="pad" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学习时长"><el-input-number v-model="form.durationSeconds" :min="0" :step="60" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学习进度"><el-slider v-model="form.progressRate" :min="0" :max="100" show-input /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="来源页面"><el-select v-model="form.sourcePage" filterable allow-create default-first-option><el-option v-for="item in sourcePageOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="自定义来源"><el-input v-model="form.customSourcePage" placeholder="仅在需要补充时填写" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addStudyRecord, delStudyRecord, getStudyRecord, listStudyRecord, updateStudyRecord } from '@/api/campus/studyRecord'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'
import { listKnowledgePoint } from '@/api/campus/teaching'

const loading = ref(false)
const open = ref(false)
const title = ref('')
const total = ref(0)
const single = ref(true)
const multiple = ref(true)
const ids = ref<any[]>([])
const recordList = ref<any[]>([])
const userOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const knowledgeOptions = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, userId: undefined, courseId: undefined, behaviorType: undefined, sourcePage: undefined })
const form = reactive<any>({})

const behaviorTypeOptions = [
  { label: '浏览', value: 'view' },
  { label: '播放', value: 'play' },
  { label: '下载', value: 'download' },
  { label: '收藏', value: 'favorite' },
  { label: '完成', value: 'complete' },
  { label: '提交', value: 'submit' },
]
const sourcePageOptions = [
  { label: '学习中心', value: 'learning_center' },
  { label: '资源详情', value: 'resource_detail' },
  { label: '课程学习', value: 'course_learning' },
  { label: '考试复盘', value: 'exam_review' },
  { label: '推荐位', value: 'recommendation_panel' },
]

const totalMinutes = computed(() => Math.round(recordList.value.reduce((sum: number, item: any) => sum + Number(item.durationSeconds || 0), 0) / 60))
const avgProgress = computed(() => {
  if (!recordList.value.length) return '0%'
  const sum = recordList.value.reduce((totalValue: number, item: any) => totalValue + Number(item.progressRate || 0), 0)
  return `${(sum / recordList.value.length).toFixed(1)}%`
})
const completedCount = computed(() => recordList.value.filter((item: any) => ['complete', 'submit'].includes(item.behaviorType)).length)
const filteredKnowledgeOptions = computed(() => knowledgeOptions.value.filter((item: any) => !form.courseId || item.courseId === form.courseId))

function behaviorTypeLabel(value: string) {
  return behaviorTypeOptions.find((item) => item.value === value)?.label || value || '--'
}
function userLabel(userId: number | string) { return userOptions.value.find((item: any) => item.value === userId)?.label || `用户 ${userId}` }
function courseLabel(courseId: number | string) { return courseOptions.value.find((item: any) => item.value === courseId)?.label || `课程 ${courseId}` }
function knowledgeLabel(knowledgeId: number | string) { return knowledgeOptions.value.find((item: any) => item.value === knowledgeId)?.label || (knowledgeId ? `知识点 ${knowledgeId}` : '--') }

function resetFormData() {
  Object.assign(form, { recordId: undefined, userId: undefined, courseId: undefined, chapterId: undefined, resourceId: undefined, behaviorType: 'view', durationSeconds: 0, progressRate: 0, deviceType: 'web', sourcePage: 'learning_center', customSourcePage: '' })
}

async function getList() {
  loading.value = true
  try {
    const res = await listStudyRecord(queryParams)
    recordList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.userId = undefined
  queryParams.courseId = undefined
  queryParams.behaviorType = undefined
  queryParams.sourcePage = undefined
  getList()
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.recordId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  resetFormData()
  title.value = '新增行为记录'
  open.value = true
}

async function handleUpdate(row?: any) {
  resetFormData()
  const recordId = row?.recordId || ids.value[0]
  const res = await getStudyRecord(recordId)
  const data = res.data || {}
  Object.assign(form, { ...data, progressRate: Number(data.progressRate || 0), customSourcePage: '' })
  title.value = '修改行为记录'
  open.value = true
}

async function submitForm() {
  const payload = { ...form, sourcePage: form.customSourcePage || form.sourcePage, progressRate: Number(form.progressRate || 0) }
  if (payload.recordId) {
    await updateStudyRecord(payload)
    ElMessage.success('修改成功')
  } else {
    await addStudyRecord(payload)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

async function handleDelete(row?: any) {
  const recordIds = row?.recordId || ids.value
  if (!recordIds || (Array.isArray(recordIds) && !recordIds.length)) return
  await ElMessageBox.confirm('确认删除选中的行为记录吗？', '提示', { type: 'warning' })
  await delStudyRecord(recordIds)
  ElMessage.success('删除成功')
  getList()
}

async function loadOptions() {
  userOptions.value = await fetchUserOptions('student')
  courseOptions.value = await fetchCourseOptions()
  const res = await listKnowledgePoint({ pageNum: 1, pageSize: 500, status: '0' })
  knowledgeOptions.value = (res.rows || []).map((item: any) => ({ label: item.knowledgeName, value: item.knowledgePointId, courseId: item.courseId }))
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
</style>
