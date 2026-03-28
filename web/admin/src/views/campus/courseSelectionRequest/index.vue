<template>
  <div class="app-container request-page">
    <!-- Overview Stats -->
    <div class="request-overview">
      <div class="request-overview__card" :class="{ 'is-active': !queryParams.requestStatus }" @click="filterByStatus(undefined)">
        <span>全部申请</span>
        <strong>{{ total }}</strong>
      </div>
      <div class="request-overview__card is-warning" :class="{ 'is-active': queryParams.requestStatus === '0' }" @click="filterByStatus('0')">
        <span>待审核</span>
        <strong>{{ statusCounts.pending }}</strong>
      </div>
      <div class="request-overview__card is-success" :class="{ 'is-active': queryParams.requestStatus === '1' }" @click="filterByStatus('1')">
        <span>已通过</span>
        <strong>{{ statusCounts.approved }}</strong>
      </div>
      <div class="request-overview__card is-danger" :class="{ 'is-active': queryParams.requestStatus === '2' }" @click="filterByStatus('2')">
        <span>已驳回</span>
        <strong>{{ statusCounts.rejected }}</strong>
      </div>
      <div class="request-overview__card is-info" :class="{ 'is-active': queryParams.requestStatus === '3' }" @click="filterByStatus('3')">
        <span>已撤回</span>
        <strong>{{ statusCounts.withdrawn }}</strong>
      </div>
    </div>

    <el-form :inline="true" :model="queryParams" v-show="showSearch" class="mb16">
      <el-form-item label="学期"><el-select v-model="queryParams.termId" filterable clearable style="width:220px"><el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="学生"><el-select v-model="queryParams.studentUserId" filterable clearable style="width:220px"><el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="教学班"><el-select v-model="queryParams.targetClassCourseId" filterable clearable style="width:320px"><el-option v-for="item in classCourseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <!-- Quick Filters -->
    <div class="quick-filters mb16">
      <el-check-tag :checked="quickFilter === 'pending'" @change="toggleQuickFilter('pending')">仅待审核</el-check-tag>
      <el-check-tag :checked="quickFilter === 'overdue'" @change="toggleQuickFilter('overdue')">超3天未审核</el-check-tag>
      <el-check-tag :checked="quickFilter === 'add'" @change="toggleQuickFilter('add')">选课申请</el-check-tag>
      <el-check-tag :checked="quickFilter === 'drop'" @change="toggleQuickFilter('drop')">退课申请</el-check-tag>
    </div>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="success" plain icon="Select" :disabled="!canBatchReview" @click="openBatchReviewDialog('1')">批量通过</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="CloseBold" :disabled="!canBatchReview" @click="openBatchReviewDialog('2')">批量驳回</el-button></el-col>
      <el-col :span="1.5" v-if="selectedRows.length > 0"><span class="selected-hint">已选 {{ selectedRows.length }} 条（{{ pendingSelectedCount }} 条待审核）</span></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="displayList" @selection-change="handleSelectionChange" stripe>
      <el-table-column type="selection" width="55" :selectable="(row: any) => row.requestStatus === '0'" />
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
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.requestType === 'DROP' ? 'danger' : 'primary'" size="small" effect="plain">{{ row.requestType === 'DROP' ? '退课' : '选课' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请理由" prop="requestReason" min-width="240" show-overflow-tooltip />
      <el-table-column label="状态" width="150">
        <template #default="{ row }">
          <div style="display: flex; align-items: center; gap: 6px; flex-wrap: wrap;">
            <el-tag :type="statusTagType(row.requestStatus)">{{ statusLabel(row.requestStatus) }}</el-tag>
            <el-tag v-if="row.requestStatus === '0' && pendingDays(row) > 0" :type="pendingDays(row) >= 3 ? 'danger' : 'warning'" size="small" effect="plain">
              {{ pendingDays(row) }}天
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" prop="createTime" min-width="170" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.requestStatus === '0'" link type="success" @click="openReviewDialog('1', row)">通过</el-button>
          <el-button v-if="row.requestStatus === '0'" link type="danger" @click="openReviewDialog('2', row)">驳回</el-button>
          <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- Review Dialog (single + batch) -->
    <el-dialog v-model="reviewDialogOpen" :title="reviewDialogTitle" width="620px">
      <!-- Context Panel for single review -->
      <div v-if="reviewTarget && !isBatchReview" class="review-context">
        <div class="review-context__section">
          <h5>学生信息</h5>
          <div class="review-context__grid">
            <div><span>姓名</span><strong>{{ reviewTarget.studentName || '-' }}</strong></div>
            <div><span>学号</span><strong>{{ reviewTarget.studentNo || '-' }}</strong></div>
            <div><span>班级</span><strong>{{ reviewTarget.className || '-' }}</strong></div>
          </div>
        </div>
        <div class="review-context__section">
          <h5>目标教学班</h5>
          <div class="review-context__grid">
            <div><span>课程</span><strong>{{ reviewTarget.targetCourseName || '-' }}</strong></div>
            <div><span>教学班</span><strong>{{ reviewTarget.targetClassName || '-' }}{{ reviewTarget.targetTeachingClassCode ? ` / ${reviewTarget.targetTeachingClassCode}` : '' }}</strong></div>
            <div><span>教师</span><strong>{{ reviewTarget.targetTeacherName || '-' }}</strong></div>
          </div>
        </div>
      </div>
      <!-- Batch hint -->
      <div v-if="isBatchReview" class="review-batch-hint">
        <el-icon :size="18"><Warning /></el-icon>
        <span>即将批量审核 <strong>{{ batchReviewIds.length }}</strong> 条待审核申请</span>
      </div>

      <el-form :model="reviewForm" label-width="90px" style="margin-top: 16px;">
        <el-form-item label="审核结果"><el-tag :type="reviewForm.requestStatus === '1' ? 'success' : 'danger'" size="large">{{ reviewForm.requestStatus === '1' ? '通过' : '驳回' }}</el-tag></el-form-item>
        <el-form-item label="审核说明"><el-input v-model="reviewForm.reviewRemark" type="textarea" :rows="4" maxlength="300" show-word-limit placeholder="请输入审核说明，便于学生查看处理结果" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogOpen = false">取消</el-button>
        <el-button type="primary" :loading="reviewSubmitting" @click="submitReview">提交</el-button>
      </template>
    </el-dialog>

    <!-- Detail Dialog (sectioned) -->
    <el-dialog v-model="detailOpen" title="申请详情" width="760px">
      <div class="request-detail" v-if="detailRow">
        <div class="request-detail__section">
          <h5>学生信息</h5>
          <div class="request-detail__grid">
            <div class="request-detail__item"><span>姓名</span><strong>{{ detailRow.studentName || '-' }}</strong></div>
            <div class="request-detail__item"><span>学号</span><strong>{{ detailRow.studentNo || '-' }}</strong></div>
            <div class="request-detail__item"><span>班级</span><strong>{{ detailRow.className || '-' }}</strong></div>
          </div>
        </div>
        <div class="request-detail__section">
          <h5>目标教学班</h5>
          <div class="request-detail__grid">
            <div class="request-detail__item request-detail__item--full"><span>教学班</span><strong>{{ detailRow.targetCourseName || '-' }} / {{ detailRow.targetClassName || '-' }}{{ detailRow.targetTeachingClassCode ? ` / ${detailRow.targetTeachingClassCode}` : '' }}</strong></div>
            <div class="request-detail__item"><span>教师</span><strong>{{ detailRow.targetTeacherName || '-' }}</strong></div>
            <div class="request-detail__item"><span>学期</span><strong>{{ detailRow.termName || '-' }}</strong></div>
          </div>
        </div>
        <div class="request-detail__section">
          <h5>申请信息</h5>
          <div class="request-detail__grid">
            <div class="request-detail__item"><span>申请编号</span><strong>{{ detailRow.requestNo || '-' }}</strong></div>
            <div class="request-detail__item"><span>申请类型</span><strong>{{ detailRow.requestType === 'DROP' ? '退课申请' : '选课申请' }}</strong></div>
            <div class="request-detail__item"><span>申请时间</span><strong>{{ detailRow.createTime || '-' }}</strong></div>
            <div class="request-detail__item request-detail__item--full"><span>申请理由</span><strong>{{ detailRow.requestReason || '-' }}</strong></div>
          </div>
        </div>
        <div class="request-detail__section">
          <h5>审核信息</h5>
          <div class="request-detail__grid">
            <div class="request-detail__item"><span>审核状态</span><strong><el-tag :type="statusTagType(detailRow.requestStatus)">{{ statusLabel(detailRow.requestStatus) }}</el-tag></strong></div>
            <div class="request-detail__item"><span>审核时间</span><strong>{{ detailRow.reviewTime || '-' }}</strong></div>
            <div class="request-detail__item request-detail__item--full"><span>审核说明</span><strong>{{ detailRow.reviewRemark || '-' }}</strong></div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Warning } from '@element-plus/icons-vue'
import { listCourseSelectionRequest, reviewCourseSelectionRequest, batchReviewCourseSelectionRequest } from '@/api/campus/courseSelectionRequest'
import { fetchTermOptions, fetchUserOptions } from '@/api/campus/options'
import { listClassCourse } from '@/api/campus/teaching'

const loading = ref(false)
const reviewSubmitting = ref(false)
const showSearch = ref(true)
const total = ref(0)
const dataList = ref<any[]>([])
const termOptions = ref<any[]>([])
const studentOptions = ref<any[]>([])
const classCourseOptions = ref<any[]>([])
const selectedRows = ref<any[]>([])
const reviewDialogOpen = ref(false)
const detailOpen = ref(false)
const detailRow = ref<any>(null)
const reviewTarget = ref<any>(null)
const isBatchReview = ref(false)
const batchReviewIds = ref<Long[]>([])
const quickFilter = ref<string>('')
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, termId: undefined, requestStatus: undefined, studentUserId: undefined, targetClassCourseId: undefined, requestType: undefined })
const reviewForm = reactive<any>({ requestId: undefined, requestStatus: '1', reviewRemark: '' })

const statusCounts = computed(() => {
  const list = dataList.value
  return {
    pending: list.filter((r: any) => r.requestStatus === '0').length,
    approved: list.filter((r: any) => r.requestStatus === '1').length,
    rejected: list.filter((r: any) => r.requestStatus === '2').length,
    withdrawn: list.filter((r: any) => r.requestStatus === '3').length,
  }
})

const pendingSelectedCount = computed(() => selectedRows.value.filter((r: any) => r.requestStatus === '0').length)
const canBatchReview = computed(() => pendingSelectedCount.value > 0)

const displayList = computed(() => {
  if (quickFilter.value === 'overdue') {
    return dataList.value.filter((r: any) => r.requestStatus === '0' && pendingDays(r) >= 3)
  }
  return dataList.value
})

const reviewDialogTitle = computed(() => {
  if (isBatchReview.value) {
    return reviewForm.requestStatus === '1' ? `批量通过 ${batchReviewIds.value.length} 条申请` : `批量驳回 ${batchReviewIds.value.length} 条申请`
  }
  return reviewForm.requestStatus === '1' ? '通过申请' : '驳回申请'
})

function pendingDays(row: any) {
  if (!row.createTime) return 0
  return Math.floor((Date.now() - new Date(row.createTime).getTime()) / 86400000)
}

function statusLabel(value?: string) {
  return value === '1' ? '已通过' : value === '2' ? '已驳回' : value === '3' ? '已撤回' : '待审核'
}

function statusTagType(value?: string) {
  return value === '1' ? 'success' : value === '2' ? 'danger' : value === '3' ? 'info' : 'warning'
}

function filterByStatus(status: string | undefined) {
  queryParams.requestStatus = status
  queryParams.pageNum = 1
  quickFilter.value = ''
  getList()
}

function toggleQuickFilter(filter: string) {
  if (quickFilter.value === filter) {
    quickFilter.value = ''
    queryParams.requestStatus = undefined
    queryParams.requestType = undefined
  } else {
    quickFilter.value = filter
    if (filter === 'pending') {
      queryParams.requestStatus = '0'
      queryParams.requestType = undefined
    } else if (filter === 'overdue') {
      queryParams.requestStatus = '0'
      queryParams.requestType = undefined
    } else if (filter === 'add') {
      queryParams.requestType = 'ADD'
      queryParams.requestStatus = undefined
    } else if (filter === 'drop') {
      queryParams.requestType = 'DROP'
      queryParams.requestStatus = undefined
    }
  }
  queryParams.pageNum = 1
  getList()
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
  Object.assign(queryParams, { pageNum: 1, pageSize: 10, termId: undefined, requestStatus: undefined, studentUserId: undefined, targetClassCourseId: undefined, requestType: undefined })
  quickFilter.value = ''
  getList()
}

function handleSelectionChange(selection: any[]) {
  selectedRows.value = selection
}

function openReviewDialog(status: '1' | '2', row?: any) {
  isBatchReview.value = false
  batchReviewIds.value = []
  reviewTarget.value = row || null
  reviewForm.requestId = row?.requestId
  reviewForm.requestStatus = status
  reviewForm.reviewRemark = ''
  reviewDialogOpen.value = true
}

function openBatchReviewDialog(status: '1' | '2') {
  const pendingRows = selectedRows.value.filter((r: any) => r.requestStatus === '0')
  if (!pendingRows.length) return
  isBatchReview.value = true
  batchReviewIds.value = pendingRows.map((r: any) => r.requestId)
  reviewTarget.value = null
  reviewForm.requestId = undefined
  reviewForm.requestStatus = status
  reviewForm.reviewRemark = ''
  reviewDialogOpen.value = true
}

async function submitReview() {
  reviewSubmitting.value = true
  try {
    if (isBatchReview.value) {
      const dtos = batchReviewIds.value.map((id: any) => ({
        requestId: id,
        requestStatus: reviewForm.requestStatus,
        reviewRemark: reviewForm.reviewRemark,
      }))
      await batchReviewCourseSelectionRequest(dtos)
      ElMessage.success(`已批量${reviewForm.requestStatus === '1' ? '通过' : '驳回'} ${dtos.length} 条申请`)
    } else {
      await reviewCourseSelectionRequest(reviewForm)
      ElMessage.success(reviewForm.requestStatus === '1' ? '申请已通过' : '申请已驳回')
    }
    reviewDialogOpen.value = false
    await getList()
  } finally {
    reviewSubmitting.value = false
  }
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
.request-page { display: flex; flex-direction: column; gap: 0; }
.mb16 { margin-bottom: 16px; }

/* Overview Cards */
.request-overview {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 20px;
}
.request-overview__card {
  padding: 16px;
  border-radius: 12px;
  background: #f8fafc;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.2s;
}
.request-overview__card:hover { background: #f1f5f9; }
.request-overview__card.is-active { border-color: #3b82f6; background: #eff6ff; }
.request-overview__card span { display: block; color: #64748b; font-size: 12px; font-weight: 500; }
.request-overview__card strong { display: block; margin-top: 8px; font-size: 24px; color: #0f172a; }
.request-overview__card.is-warning strong { color: #d97706; }
.request-overview__card.is-success strong { color: #059669; }
.request-overview__card.is-danger strong { color: #dc2626; }
.request-overview__card.is-info strong { color: #6b7280; }

/* Quick Filters */
.quick-filters { display: flex; gap: 8px; flex-wrap: wrap; }

.selected-hint { color: #6b7280; font-size: 13px; line-height: 32px; }

/* Table cells */
.entity-cell { display: flex; flex-direction: column; gap: 6px; }
.entity-cell strong { color: #172033; font-size: 13px; }
.entity-cell span { color: #667085; font-size: 12px; }

/* Review Context */
.review-context { display: flex; flex-direction: column; gap: 16px; }
.review-context__section h5 { margin: 0 0 10px; font-size: 13px; color: #64748b; font-weight: 600; }
.review-context__grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; }
.review-context__grid > div { padding: 10px 12px; border-radius: 10px; background: #f8fafc; border: 1px solid #e2e8f0; display: flex; flex-direction: column; gap: 4px; }
.review-context__grid > div span { color: #94a3b8; font-size: 11px; }
.review-context__grid > div strong { color: #0f172a; font-size: 13px; }

.review-batch-hint {
  display: flex; align-items: center; gap: 8px;
  padding: 14px 16px; border-radius: 10px;
  background: #fffbeb; border: 1px solid #fde68a; color: #92400e; font-size: 14px;
}

/* Detail Dialog */
.request-detail { padding: 4px 2px; display: flex; flex-direction: column; gap: 20px; }
.request-detail__section h5 { margin: 0 0 10px; font-size: 13px; color: #64748b; font-weight: 600; border-bottom: 1px solid #f1f5f9; padding-bottom: 8px; }
.request-detail__grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px 16px; }
.request-detail__item { display: flex; flex-direction: column; gap: 6px; padding: 14px; border-radius: 12px; background: #f8fbff; border: 1px solid #dbe7f5; }
.request-detail__item span { color: #667085; font-size: 12px; }
.request-detail__item strong { color: #172033; font-size: 14px; line-height: 1.7; }
.request-detail__item--full { grid-column: 1 / -1; }

@media (max-width: 900px) {
  .request-overview { grid-template-columns: repeat(3, 1fr); }
  .request-detail__grid { grid-template-columns: 1fr; }
  .review-context__grid { grid-template-columns: 1fr; }
}
</style>
