<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="学期">
        <el-select v-model="queryParams.termId" clearable filterable style="width:220px" @change="getList">
          <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="教材名称"><el-input v-model="queryParams.textbookName" clearable style="width:180px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="年级">
        <el-select v-model="queryParams.gradeId" clearable filterable style="width:160px" @change="onGradeChange">
          <el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="班级">
        <el-select v-model="queryParams.classId" clearable filterable style="width:160px">
          <el-option v-for="item in filteredClassOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.planStatus" clearable style="width:120px">
          <el-option label="草稿" value="DRAFT" /><el-option label="已发布" value="PUBLISHED" /><el-option label="已完成" value="COMPLETED" />
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
      <el-table-column label="学期" prop="termName" min-width="180" show-overflow-tooltip />
      <el-table-column label="教材" min-width="220">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.textbookName || '-' }}</strong>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="课程" prop="courseName" min-width="160" show-overflow-tooltip>
        <template #default="{ row }">{{ row.courseName || '未指定' }}</template>
      </el-table-column>
      <el-table-column label="年级 / 班级" min-width="180">
        <template #default="{ row }">
          <span>{{ row.gradeName || '全部年级' }}</span>
          <span v-if="row.className"> / {{ row.className }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发放进度" min-width="180">
        <template #default="{ row }">
          <div class="distribution-cell">
            <el-progress
              :percentage="calcPercentage(row)"
              :color="progressColor(row)"
              :stroke-width="14"
              :text-inside="true"
              style="flex:1"
            />
            <span class="distribution-text">{{ row.distributedQuantity || 0 }} / {{ row.planQuantity || 0 }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.planStatus)">{{ statusLabel(row.planStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="维护信息" min-width="200">
        <template #default="{ row }">
          <div class="maintain-cell">
            <span>创建：{{ row.createBy || '-' }}</span>
            <span>{{ formatDateTime(row.createTime) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="View" @click="handleViewClaims(row)">领取记录</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="680px">
      <el-form :model="form" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学期">
              <el-select v-model="form.termId" filterable clearable style="width:100%">
                <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教材">
              <el-select v-model="form.textbookId" filterable clearable style="width:100%">
                <el-option v-for="item in textbookOptions" :key="item.textbookId" :label="`${item.textbookName}（${item.textbookCode}）`" :value="item.textbookId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程">
              <el-select v-model="form.courseId" filterable clearable style="width:100%">
                <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级">
              <el-select v-model="form.gradeId" filterable clearable style="width:100%" @change="onFormGradeChange">
                <el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级">
              <el-select v-model="form.classId" filterable clearable style="width:100%">
                <el-option v-for="item in formClassOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="计划数量"><el-input-number v-model="form.planQuantity" :min="0" controls-position="right" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.planStatus" style="width:100%">
                <el-option label="草稿" value="DRAFT" /><el-option label="已发布" value="PUBLISHED" /><el-option label="已完成" value="COMPLETED" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>

    <!-- 领取记录弹窗 -->
    <el-dialog v-model="claimDialogOpen" title="教材领取记录" width="780px">
      <div v-if="claimData" class="claim-summary">
        <div class="claim-summary__item">
          <span class="claim-summary__label">教材：</span>
          <strong>{{ claimData.textbookName }}</strong>
        </div>
        <div class="claim-summary__item">
          <span class="claim-summary__label">ISBN：</span>
          <span>{{ claimData.isbn }}</span>
        </div>
        <div class="claim-summary__item">
          <span class="claim-summary__label">计划数量：</span>
          <span>{{ claimData.planQuantity }}</span>
        </div>
        <div class="claim-summary__item">
          <span class="claim-summary__label">已发放：</span>
          <el-tag :type="claimData.distributedQuantity >= claimData.planQuantity ? 'danger' : 'success'" size="small">
            {{ claimData.distributedQuantity }} / {{ claimData.planQuantity }}
          </el-tag>
        </div>
      </div>
      <el-table v-if="claimRecords.length" :data="claimRecords" size="small" style="margin-top:16px">
        <el-table-column label="学生" min-width="120">
          <template #default="{ row }">{{ row.applicant_nick_name || row.applicant_user_id || '-' }}</template>
        </el-table-column>
        <el-table-column label="申请编号" prop="request_no" min-width="160" show-overflow-tooltip />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.textbook_type === 'CLAIM' ? '' : 'warning'" size="small">
              {{ row.textbook_type === 'CLAIM' ? '领用' : '补领' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="数量" prop="quantity" width="80" align="center" />
        <el-table-column label="领取时间" min-width="170">
          <template #default="{ row }">{{ formatDateTime(row.update_time) }}</template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无领取记录" />
      <template #footer><el-button @click="claimDialogOpen=false">关闭</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listTextbookPlan, addTextbookPlan, updateTextbookPlan, removeTextbookPlan, listTextbookOptions, getTextbookPlanClaimRecords } from '@/api/campus/affair'
import { fetchTermOptions, fetchGradeOptions, fetchClassOptions, fetchCourseOptions } from '@/api/campus/options'

const loading = ref(false), showSearch = ref(true), total = ref(0), open = ref(false), title = ref('')
const ids = ref<any[]>([]), single = ref(true), multiple = ref(true), dataList = ref<any[]>([])
const termOptions = ref<any[]>([]), gradeOptions = ref<any[]>([]), classOptions = ref<any[]>([]), courseOptions = ref<any[]>([])
const textbookOptions = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, termId: undefined, textbookName: '', gradeId: undefined, classId: undefined, planStatus: undefined })
const form = reactive<any>({})

// 领取记录弹窗
const claimDialogOpen = ref(false)
const claimData = ref<any>(null)
const claimRecords = ref<any[]>([])

const STATUS_MAP: Record<string, { label: string; type: string }> = {
  DRAFT: { label: '草稿', type: 'info' },
  PUBLISHED: { label: '已发布', type: 'success' },
  COMPLETED: { label: '已完成', type: '' },
}
function statusLabel(s: string) { return STATUS_MAP[s]?.label || s }
function statusTagType(s: string) { return STATUS_MAP[s]?.type || 'info' }

function formatDateTime(value: string) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

function calcPercentage(row: any): number {
  const plan = row.planQuantity || 0
  const dist = row.distributedQuantity || 0
  if (plan <= 0) return 0
  return Math.min(Math.round((dist / plan) * 100), 100)
}

function progressColor(row: any): string {
  const pct = calcPercentage(row)
  if (pct >= 100) return '#e6a23c'
  if (pct >= 70) return '#409eff'
  return '#67c23a'
}

const filteredClassOptions = computed(() => {
  if (!queryParams.gradeId) return classOptions.value
  return classOptions.value.filter((c: any) => c.gradeId === queryParams.gradeId)
})
const formClassOptions = computed(() => {
  if (!form.gradeId) return classOptions.value
  return classOptions.value.filter((c: any) => c.gradeId === form.gradeId)
})

function onGradeChange() { queryParams.classId = undefined }
function onFormGradeChange() { form.classId = undefined }

function resetFormData() {
  Object.assign(form, {
    planId: undefined, termId: undefined, textbookId: undefined, courseId: undefined,
    gradeId: undefined, classId: undefined, planQuantity: 0, distributedQuantity: 0,
    planStatus: 'DRAFT', remark: '',
  })
}

async function getList() {
  loading.value = true
  const res = await listTextbookPlan(queryParams)
  dataList.value = res.rows || []; total.value = res.total || 0
  loading.value = false
}

function resetQuery() {
  queryParams.pageNum = 1; queryParams.termId = undefined; queryParams.textbookName = ''
  queryParams.gradeId = undefined; queryParams.classId = undefined; queryParams.planStatus = undefined
  getList()
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map(i => i.planId); single.value = selection.length !== 1; multiple.value = !selection.length
}

function handleAdd() { resetFormData(); title.value = '新增教材计划'; open.value = true }

function handleUpdate(row?: any) {
  const item = row || dataList.value.find((i: any) => i.planId === ids.value[0])
  if (!item) return
  resetFormData(); Object.assign(form, item); title.value = '修改教材计划'; open.value = true
}

async function submitForm() {
  if (form.planId) { await updateTextbookPlan(form); ElMessage.success('修改成功') }
  else { await addTextbookPlan(form); ElMessage.success('新增成功') }
  open.value = false; getList()
}

async function handleDelete(row?: any) {
  const target = row?.planId || ids.value
  if (!target || (Array.isArray(target) && !target.length)) return
  await ElMessageBox.confirm('确认删除所选教材计划吗？', '提示', { type: 'warning' })
  await removeTextbookPlan(target); ElMessage.success('删除成功'); getList()
}

async function handleViewClaims(row: any) {
  try {
    const res = await getTextbookPlanClaimRecords(row.planId)
    const data = res.data || res || {}
    claimData.value = data
    claimRecords.value = data.claimRecords || []
    claimDialogOpen.value = true
  } catch {
    ElMessage.warning('获取领取记录失败')
  }
}

async function loadOptions() {
  const [t, g, c, co] = await Promise.all([
    fetchTermOptions(), fetchGradeOptions(), fetchClassOptions(), fetchCourseOptions(),
  ])
  termOptions.value = t; gradeOptions.value = g; classOptions.value = c; courseOptions.value = co
  const tbRes = await listTextbookOptions()
  textbookOptions.value = tbRes.data || tbRes || []
}

onMounted(async () => { await loadOptions(); resetFormData(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px;}
.entity-cell{display:flex;flex-direction:column;gap:6px}
.entity-cell strong{color:#172033;font-size:14px}
.maintain-cell{display:flex;flex-direction:column;gap:4px;color:#667085;font-size:12px;line-height:1.5}

.distribution-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
.distribution-text {
  font-size: 12px;
  color: #4b5565;
  white-space: nowrap;
  min-width: 60px;
}

.claim-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 16px 32px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
}
.claim-summary__item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}
.claim-summary__label {
  color: #667085;
}
</style>
