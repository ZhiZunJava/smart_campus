<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch" class="mb16">
      <el-form-item label="岗位名称">
        <el-input v-model="queryParams.jobName" placeholder="请输入岗位名称" clearable @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="用工部门">
        <el-select v-model="queryParams.deptId" clearable filterable placeholder="请选择部门" style="width: 220px">
          <el-option v-for="item in deptOptions" :key="item.deptId || item.value" :label="item.deptName || item.label" :value="item.deptId || item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="学期">
        <el-select v-model="queryParams.termId" clearable filterable placeholder="请选择学期" style="width: 220px">
          <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.publishStatus" clearable placeholder="请选择状态" style="width: 140px">
          <el-option label="开放" value="0" />
          <el-option label="关闭" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5">
        <el-button v-hasPermi="['campus:affairWorkStudyJob:add']" type="primary" plain icon="Plus" @click="handleAdd">新增岗位</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <div class="overview-grid mb16">
      <div class="overview-card">
        <span>岗位总数</span>
        <strong>{{ total }}</strong>
      </div>
      <div class="overview-card is-success">
        <span>开放岗位</span>
        <strong>{{ openCount }}</strong>
      </div>
      <div class="overview-card is-info">
        <span>覆盖学期</span>
        <strong>{{ coveredTermCount }}</strong>
      </div>
      <div class="overview-card is-warning">
        <span>累计招聘人数</span>
        <strong>{{ totalHeadcount }}</strong>
      </div>
    </div>

    <el-table v-loading="loading" :data="jobList">
      <el-table-column prop="jobCode" label="岗位编码" min-width="140" />
      <el-table-column prop="jobName" label="岗位名称" min-width="180" />
      <el-table-column prop="deptName" label="用工部门" min-width="160" />
      <el-table-column prop="termName" label="学期" min-width="160" />
      <el-table-column label="月薪/补贴" min-width="120">
        <template #default="{ row }">{{ row.salaryAmount || 0 }} / {{ salaryUnitLabel(row.salaryUnit) }}</template>
      </el-table-column>
      <el-table-column prop="headcount" label="招聘人数" width="100" />
      <el-table-column prop="weeklyHours" label="每周工时" width="100" />
      <el-table-column label="开放时间" min-width="220">
        <template #default="{ row }">{{ row.openStartTime || '-' }} ~ {{ row.openEndTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.publishStatus === '0' ? 'success' : 'info'">{{ row.publishStatus === '0' ? '开放' : '关闭' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog v-model="open" :title="title" width="860px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="岗位编码" prop="jobCode">
              <el-input v-model="form.jobCode" placeholder="自动生成岗位编码，也可手动调整" @input="handleCodeInput">
                <template #append>
                  <el-button @click="refreshGeneratedCode">重置生成</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位名称" prop="jobName">
              <el-input v-model="form.jobName" placeholder="请输入岗位名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用工部门" prop="deptId">
              <el-select v-model="form.deptId" filterable style="width: 100%" placeholder="请选择部门" @change="syncDeptName">
                <el-option v-for="item in deptOptions" :key="item.deptId || item.value" :label="item.deptName || item.label" :value="item.deptId || item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属学期" prop="termId">
              <el-select v-model="form.termId" filterable style="width: 100%" placeholder="请选择学期" @change="syncTermName">
                <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="月薪/补贴" prop="salaryAmount">
              <el-input-number v-model="form.salaryAmount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资单位">
              <el-select v-model="form.salaryUnit" style="width: 100%">
                <el-option label="元/月" value="MONTH" />
                <el-option label="元/次" value="ONCE" />
                <el-option label="元/小时" value="HOUR" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作地点">
              <el-input v-model="form.workPlace" placeholder="请输入工作地点" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="招聘人数">
              <el-input-number v-model="form.headcount" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每周工时">
              <el-input-number v-model="form.weeklyHours" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.publishStatus">
                <el-radio value="0">开放</el-radio>
                <el-radio value="1">关闭</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开放开始">
              <el-date-picker v-model="form.openStartTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开放结束">
              <el-date-picker v-model="form.openEndTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="岗位说明">
              <el-input v-model="form.jobDesc" type="textarea" :rows="3" placeholder="请输入岗位说明" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="岗位要求">
              <el-input v-model="form.jobRequirements" type="textarea" :rows="3" placeholder="请输入岗位要求" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addAffairWorkStudyJob, getAffairWorkStudyJob, listAffairWorkStudyJob, removeAffairWorkStudyJob, updateAffairWorkStudyJob } from '@/api/campus/affair'
import { fetchTermOptions } from '@/api/campus/options'
import { listDept } from '@/api/system/dept'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const open = ref(false)
const title = ref('')
const jobList = ref<any[]>([])
const deptOptions = ref<any[]>([])
const termOptions = ref<any[]>([])
const formRef = ref<any>()
const manualCodeOverride = ref(false)

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  jobName: '',
  deptId: undefined,
  termId: undefined,
  publishStatus: '',
})

const form = reactive<any>({
  jobId: undefined,
  jobCode: '',
  jobName: '',
  deptId: undefined,
  deptName: '',
  termId: undefined,
  termName: '',
  salaryAmount: 0,
  salaryUnit: 'MONTH',
  workPlace: '',
  weeklyHours: 0,
  headcount: 1,
  appliedCount: 0,
  contactPerson: '',
  contactPhone: '',
  openStartTime: '',
  openEndTime: '',
  publishStatus: '0',
  jobDesc: '',
  jobRequirements: '',
  remark: '',
})

const rules = {
  jobCode: [{ required: true, message: '请输入岗位编码', trigger: 'blur' }],
  jobName: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择用工部门', trigger: 'change' }],
  termId: [{ required: true, message: '请选择所属学期', trigger: 'change' }],
  salaryAmount: [{ required: true, message: '请输入月薪/补贴', trigger: 'blur' }],
}

const openCount = computed(() => jobList.value.filter((item: any) => item.publishStatus === '0').length)
const coveredTermCount = computed(() => new Set(jobList.value.map((item: any) => item.termId).filter(Boolean)).size)
const totalHeadcount = computed(() => jobList.value.reduce((sum: number, item: any) => sum + Number(item.headcount || 0), 0))

function salaryUnitLabel(value?: string) {
  if (value === 'HOUR') return '元/小时'
  if (value === 'ONCE') return '元/次'
  return '元/月'
}

function resetForm() {
  Object.assign(form, {
    jobId: undefined,
    jobCode: '',
    jobName: '',
    deptId: undefined,
    deptName: '',
    termId: undefined,
    termName: '',
    salaryAmount: 0,
    salaryUnit: 'MONTH',
    workPlace: '',
    weeklyHours: 0,
    headcount: 1,
    appliedCount: 0,
    contactPerson: '',
    contactPhone: '',
    openStartTime: '',
    openEndTime: '',
    publishStatus: '0',
    jobDesc: '',
    jobRequirements: '',
    remark: '',
  })
  manualCodeOverride.value = false
  formRef.value?.clearValidate()
}

function syncDeptName() {
  const current = deptOptions.value.find((item: any) => String(item.deptId || item.value) === String(form.deptId))
  form.deptName = current?.deptName || current?.label || ''
}

function syncTermName() {
  const current = termOptions.value.find((item: any) => String(item.value) === String(form.termId))
  form.termName = current?.label || ''
}

function sanitizeToken(value: any, fallback: string, maxLength: number) {
  const source = String(value || '')
    .toUpperCase()
    .replace(/[^A-Z0-9]/g, '')
  return (source || fallback).slice(0, maxLength)
}

function buildTimeToken() {
  const now = new Date()
  const year = String(now.getFullYear()).slice(-2)
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hour = String(now.getHours()).padStart(2, '0')
  const minute = String(now.getMinutes()).padStart(2, '0')
  return `${year}${month}${day}${hour}${minute}`
}

function generateJobCode() {
  const currentTerm = termOptions.value.find((item: any) => String(item.value) === String(form.termId))
  const termToken = sanitizeToken(currentTerm?.termCode || currentTerm?.termName || currentTerm?.value, 'TERM', 8)
  const deptToken = sanitizeToken(form.deptId, 'DEPT', 6)
  const nameToken = sanitizeToken(form.jobName, 'JOB', 8)
  form.jobCode = `WS-${termToken}-${deptToken}-${nameToken}-${buildTimeToken()}`.slice(0, 64)
}

function handleCodeInput() {
  manualCodeOverride.value = true
}

function refreshGeneratedCode() {
  manualCodeOverride.value = false
  generateJobCode()
}

watch(() => [form.jobName, form.termId, form.deptId], () => {
  if (form.jobId || manualCodeOverride.value) return
  generateJobCode()
}, { deep: true })

async function loadOptions() {
  const deptRes = await listDept()
  deptOptions.value = Array.isArray(deptRes.data) ? deptRes.data : []
  termOptions.value = await fetchTermOptions()
}

async function getList() {
  loading.value = true
  try {
    const res = await listAffairWorkStudyJob(queryParams)
    jobList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    jobName: '',
    deptId: undefined,
    termId: undefined,
    publishStatus: '',
  })
  getList()
}

function handleAdd() {
  resetForm()
  title.value = '新增勤工助学岗位'
  generateJobCode()
  open.value = true
}

async function handleEdit(row: any) {
  resetForm()
  const res = await getAffairWorkStudyJob(row.jobId)
  Object.assign(form, res.data || res)
  manualCodeOverride.value = true
  title.value = '编辑勤工助学岗位'
  open.value = true
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确定删除岗位“${row.jobName}”吗？`, '提示', { type: 'warning' })
  await removeAffairWorkStudyJob(row.jobId)
  ElMessage.success('删除成功')
  getList()
}

async function submitForm() {
  await formRef.value?.validate()
  syncDeptName()
  syncTermName()
  const payload = { ...form }
  if (payload.jobId) {
    await updateAffairWorkStudyJob(payload)
    ElMessage.success('岗位已更新')
  } else {
    await addAffairWorkStudyJob(payload)
    ElMessage.success('岗位已新增')
  }
  open.value = false
  getList()
}

onMounted(async () => {
  await loadOptions()
  await getList()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.overview-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; }
.overview-card { padding: 16px; border-radius: 12px; background: #f8fafc; border: 1px solid #e2e8f0; }
.overview-card span { display: block; color: #64748b; font-size: 12px; }
.overview-card strong { display: block; margin-top: 8px; font-size: 24px; color: #0f172a; }
.overview-card.is-success strong { color: #059669; }
.overview-card.is-info strong { color: #2563eb; }
.overview-card.is-warning strong { color: #d97706; }
</style>
