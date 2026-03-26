<template>
  <div class="app-container request-page">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="学期"><el-select v-model="queryParams.termId" filterable clearable style="width:220px"><el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.requestStatus" clearable style="width:160px">
          <el-option label="待审核" value="0" />
          <el-option label="已通过" value="1" />
          <el-option label="已驳回" value="2" />
          <el-option label="已撤回" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="学生"><el-select v-model="queryParams.studentUserId" filterable clearable style="width:220px"><el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="教学班"><el-select v-model="queryParams.targetClassCourseId" filterable clearable style="width:320px"><el-option v-for="item in classCourseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="success" plain icon="Select" :disabled="single || !canReviewSelected" @click="openReviewDialog('1')">通过</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="CloseBold" :disabled="single || !canReviewSelected" @click="openReviewDialog('2')">驳回</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="申请编号" prop="requestNo" min-width="180" />
      <el-table-column label="学生" min-width="220">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.studentName || '-' }}</strong>
            <span>{{ row.studentNo ? `学号：${row.studentNo}` : `用户ID：${row.studentUserId}` }}</span>
            <span>{{ row.className || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="目标教学班" min-width="320">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.targetCourseName || '-' }}</strong>
            <span>{{ row.targetClassName || '-' }}{{ row.targetTeachingClassCode ? ` · ${row.targetTeachingClassCode}` : '' }}</span>
            <span>{{ row.termName || '-' }}{{ row.targetTeacherName ? ` · ${row.targetTeacherName}` : '' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="申请理由" prop="requestReason" min-width="240" show-overflow-tooltip />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.requestStatus)">{{ statusLabel(row.requestStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" prop="createTime" min-width="170" />
      <el-table-column label="审核说明" prop="reviewRemark" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.requestStatus === '0'" link type="success" @click="openReviewDialog('1', row)">通过</el-button>
          <el-button v-if="row.requestStatus === '0'" link type="danger" @click="openReviewDialog('2', row)">驳回</el-button>
          <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="reviewDialogOpen" :title="reviewForm.requestStatus === '1' ? '通过申请' : '驳回申请'" width="560px">
      <el-form :model="reviewForm" label-width="90px">
        <el-form-item label="审核结果"><el-tag :type="reviewForm.requestStatus === '1' ? 'success' : 'danger'">{{ reviewForm.requestStatus === '1' ? '通过' : '驳回' }}</el-tag></el-form-item>
        <el-form-item label="审核说明"><el-input v-model="reviewForm.reviewRemark" type="textarea" :rows="5" maxlength="300" show-word-limit placeholder="请输入审核说明，便于学生查看处理结果" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogOpen = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="申请详情" width="760px">
      <div class="request-detail" v-if="detailRow">
        <div class="request-detail__grid">
          <div class="request-detail__item"><span>申请编号</span><strong>{{ detailRow.requestNo || '-' }}</strong></div>
          <div class="request-detail__item"><span>申请状态</span><strong>{{ statusLabel(detailRow.requestStatus) }}</strong></div>
          <div class="request-detail__item"><span>学生</span><strong>{{ detailRow.studentName || '-' }}</strong></div>
          <div class="request-detail__item"><span>学号</span><strong>{{ detailRow.studentNo || '-' }}</strong></div>
          <div class="request-detail__item"><span>班级</span><strong>{{ detailRow.className || '-' }}</strong></div>
          <div class="request-detail__item"><span>申请时间</span><strong>{{ detailRow.createTime || '-' }}</strong></div>
          <div class="request-detail__item request-detail__item--full"><span>目标教学班</span><strong>{{ detailRow.targetCourseName || '-' }} / {{ detailRow.targetClassName || '-' }}{{ detailRow.targetTeachingClassCode ? ` / ${detailRow.targetTeachingClassCode}` : '' }}</strong></div>
          <div class="request-detail__item request-detail__item--full"><span>申请理由</span><strong>{{ detailRow.requestReason || '-' }}</strong></div>
          <div class="request-detail__item request-detail__item--full"><span>审核说明</span><strong>{{ detailRow.reviewRemark || '-' }}</strong></div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listCourseSelectionRequest, reviewCourseSelectionRequest } from '@/api/campus/courseSelectionRequest'
import { fetchTermOptions, fetchUserOptions } from '@/api/campus/options'
import { listClassCourse } from '@/api/campus/teaching'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const dataList = ref<any[]>([])
const termOptions = ref<any[]>([])
const studentOptions = ref<any[]>([])
const classCourseOptions = ref<any[]>([])
const ids = ref<any[]>([])
const selectedRows = ref<any[]>([])
const single = ref(true)
const reviewDialogOpen = ref(false)
const detailOpen = ref(false)
const detailRow = ref<any>(null)
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, termId: undefined, requestStatus: undefined, studentUserId: undefined, targetClassCourseId: undefined })
const reviewForm = reactive<any>({ requestId: undefined, requestStatus: '1', reviewRemark: '' })

const canReviewSelected = computed(() => {
  const row = selectedRows.value[0]
  return row && row.requestStatus === '0'
})

function statusLabel(value?: string) {
  return value === '1' ? '已通过' : value === '2' ? '已驳回' : value === '3' ? '已撤回' : '待审核'
}

function statusTagType(value?: string) {
  return value === '1' ? 'success' : value === '2' ? 'danger' : value === '3' ? 'info' : 'warning'
}

async function loadOptions() {
  termOptions.value = await fetchTermOptions()
  studentOptions.value = await fetchUserOptions('student')
  const classCourseRes = await listClassCourse({ pageNum: 1, pageSize: 500, status: '0' })
  classCourseOptions.value = (classCourseRes.rows || []).map((item: any) => ({
    label: `${item.termName || '未配置学期'} / ${item.className || '未配置班级'} / ${item.courseName || '未命名课程'}${item.teachingClassCode ? ` / ${item.teachingClassCode}` : ''}`,
    value: item.id,
  }))
}

async function getList() {
  loading.value = true
  try {
    const res = await listCourseSelectionRequest(queryParams)
    dataList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(queryParams, { pageNum: 1, pageSize: 10, termId: undefined, requestStatus: undefined, studentUserId: undefined, targetClassCourseId: undefined })
  getList()
}

function handleSelectionChange(selection: any[]) {
  selectedRows.value = selection
  ids.value = selection.map((item: any) => item.requestId)
  single.value = selection.length !== 1
}

function openReviewDialog(status: '1' | '2', row?: any) {
  const target = row || selectedRows.value[0]
  if (!target) return
  reviewForm.requestId = target.requestId
  reviewForm.requestStatus = status
  reviewForm.reviewRemark = ''
  reviewDialogOpen.value = true
}

async function submitReview() {
  await reviewCourseSelectionRequest(reviewForm)
  ElMessage.success(reviewForm.requestStatus === '1' ? '申请已通过' : '申请已驳回')
  reviewDialogOpen.value = false
  await getList()
}

function viewDetail(row: any) {
  detailRow.value = row
  detailOpen.value = true
}

onMounted(async () => {
  await loadOptions()
  await getList()
})
</script>

<style scoped>
.request-page { display: flex; flex-direction: column; gap: 16px; }
.mb16 { margin-bottom: 16px; }
.entity-cell { display: flex; flex-direction: column; gap: 6px; }
.entity-cell strong { color: #172033; font-size: 13px; }
.entity-cell span { color: #667085; font-size: 12px; }
.request-actions { display: flex; align-items: center; justify-content: flex-end; gap: 8px; flex-wrap: wrap; }
.request-detail { padding: 4px 2px; }
.request-detail__grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px 16px; }
.request-detail__item { display: flex; flex-direction: column; gap: 6px; padding: 14px; border-radius: 12px; background: #f8fbff; border: 1px solid #dbe7f5; }
.request-detail__item span { color: #667085; font-size: 12px; }
.request-detail__item strong { color: #172033; font-size: 14px; line-height: 1.7; }
.request-detail__item--full { grid-column: 1 / -1; }
@media (max-width: 900px) { .request-detail__grid { grid-template-columns: 1fr; } }
</style>
