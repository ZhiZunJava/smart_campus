<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch" class="mb16">
      <el-form-item label="申请编号">
        <el-input v-model="queryParams.requestNo" placeholder="请输入申请编号" clearable @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="申请人">
        <el-input v-model="queryParams.applicantName" placeholder="请输入申请人姓名" clearable @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="queryParams.categoryCode" clearable filterable placeholder="请选择分类" style="width: 220px">
          <el-option v-for="item in categoryOptions" :key="item.categoryId" :label="item.categoryName" :value="item.categoryCode" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.requestStatus" clearable placeholder="请选择状态" style="width: 180px">
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已驳回" value="REJECTED" />
          <el-option label="已撤回" value="CANCELLED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="overview-grid mb16">
      <div class="overview-card">
        <span>全部申请</span>
        <strong>{{ overviewTotalCount }}</strong>
      </div>
      <div class="overview-card is-warning">
        <span>待审核</span>
        <strong>{{ countByStatus('PENDING') }}</strong>
      </div>
      <div class="overview-card is-success">
        <span>已通过</span>
        <strong>{{ countByStatus('APPROVED') }}</strong>
      </div>
      <div class="overview-card is-danger">
        <span>已驳回</span>
        <strong>{{ countByStatus('REJECTED') }}</strong>
      </div>
    </div>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5">
        <el-button v-hasPermi="['campus:affairRequest:review']" type="success" plain :disabled="!selectedIds.length" @click="openBatchReview('APPROVE')">
          批量通过 ({{ selectedIds.length }})
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['campus:affairRequest:review']" type="danger" plain :disabled="!selectedIds.length" @click="openBatchReview('REJECT')">
          批量驳回 ({{ selectedIds.length }})
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['campus:affairRequest:list']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="requestList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="requestNo" label="申请编号" min-width="180" />
      <el-table-column label="业务信息" min-width="260">
        <template #default="{ row }">
          <div class="summary-cell">
            <strong>{{ row.templateName || row.businessName }}</strong>
            <span>{{ row.categoryName || '-' }}</span>
            <span>{{ row.summaryText || '暂无摘要' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="申请人" min-width="180">
        <template #default="{ row }">
          <div class="summary-cell">
            <strong>{{ row.applicantName || '-' }}</strong>
            <span>{{ row.applicantNo || '-' }}</span>
            <span>{{ row.className || row.applicantDeptName || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="currentStepName" label="当前节点" min-width="140" />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.requestStatus)">{{ statusLabel(row.requestStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submittedTime" label="提交时间" width="170" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
          <el-button
            v-if="row.requestStatus === 'PENDING'"
            v-hasPermi="['campus:affairRequest:review']"
            link
            type="success"
            @click="openReviewDialog(row)"
          >
            审核
          </el-button>
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

    <el-dialog v-model="detailOpen" title="事务申请详情" width="920px">
      <div v-if="detailBundle" class="detail-layout">
        <div class="detail-hero">
          <div>
            <h3>{{ detailBundle.request?.title || detailBundle.request?.templateName }}</h3>
            <p>{{ detailBundle.request?.requestNo }} · {{ statusLabel(detailBundle.request?.requestStatus) }}</p>
          </div>
          <el-tag :type="statusTagType(detailBundle.request?.requestStatus)" size="large">{{ statusLabel(detailBundle.request?.requestStatus) }}</el-tag>
        </div>

        <div class="detail-grid">
          <div class="detail-card">
            <h4>申请概况</h4>
            <p>分类：{{ detailBundle.request?.categoryName || '-' }}</p>
            <p>模板：{{ detailBundle.request?.templateName || '-' }}</p>
            <p>申请人：{{ detailBundle.request?.applicantName || '-' }}（{{ detailBundle.request?.applicantNo || '-' }}）</p>
            <p>提交时间：{{ detailBundle.request?.submittedTime || '-' }}</p>
            <p>当前节点：{{ detailBundle.request?.currentStepName || '-' }}</p>
          </div>
          <div class="detail-card">
            <h4>表单数据</h4>
            <p v-for="item in formDataEntries" :key="item.label">{{ item.label }}：{{ item.value }}</p>
          </div>
        </div>

        <div class="detail-card" v-if="attachmentEntries.length">
          <h4>附件</h4>
          <div class="attachment-list">
            <a v-for="item in attachmentEntries" :key="item.url" :href="item.url" target="_blank" rel="noreferrer">{{ item.name || item.url }}</a>
          </div>
        </div>

        <div class="detail-card">
          <h4>审批轨迹</h4>
          <el-timeline>
            <el-timeline-item
              v-for="item in detailBundle.actions || []"
              :key="item.actionId"
              :timestamp="item.createTime"
              :type="item.actionResult === 'REJECT' ? 'danger' : 'primary'"
            >
              <div class="timeline-content">
                <strong>{{ item.reviewerName || '系统' }} · {{ actionResultLabel(item.actionResult || item.actionType) }}</strong>
                <span>{{ item.stepName || '-' }}</span>
                <p>{{ item.commentText || '无备注' }}</p>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="batchOpen" title="批量审核" width="520px">
      <p>将对 <strong>{{ selectedIds.length }}</strong> 条待审核申请执行操作</p>
      <el-form label-width="90px">
        <el-form-item label="审核结论">
          <el-radio-group v-model="batchForm.actionType">
            <el-radio value="APPROVE">通过</el-radio>
            <el-radio value="REJECT">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="batchForm.commentText" type="textarea" :rows="3" maxlength="300" show-word-limit placeholder="批量审核意见（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchOpen = false">取消</el-button>
        <el-button type="primary" :loading="reviewSubmitting" @click="submitBatchReview">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="reviewOpen" title="事务审核" width="620px">
      <div v-if="currentRow" class="review-panel">
        <div class="review-panel__summary">
          <strong>{{ currentRow.templateName }}</strong>
          <span>{{ currentRow.applicantName }} · {{ currentRow.requestNo }}</span>
          <p>{{ currentRow.summaryText || '暂无摘要' }}</p>
        </div>
        <el-form label-width="90px">
          <el-form-item label="审核结论">
            <el-radio-group v-model="reviewForm.actionType">
              <el-radio value="APPROVE">通过</el-radio>
              <el-radio value="REJECT">驳回</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核意见">
            <el-input v-model="reviewForm.commentText" type="textarea" :rows="4" maxlength="300" show-word-limit placeholder="请输入审核意见" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="reviewOpen = false">取消</el-button>
        <el-button type="primary" :loading="reviewSubmitting" @click="submitReview">提交审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { batchReviewAffairRequest, exportAffairRequest, getAffairRequest, getAffairRequestStatistics, listAffairCategory, listAffairRequest, reviewAffairRequest } from '@/api/campus/affair'

const loading = ref(false)
const showSearch = ref(true)
const reviewSubmitting = ref(false)
const detailOpen = ref(false)
const reviewOpen = ref(false)
const total = ref(0)
const requestList = ref<any[]>([])
const categoryOptions = ref<any[]>([])
const detailBundle = ref<any>(null)
const currentRow = ref<any>(null)
const statisticsMap = ref<Record<string, number>>({})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  requestNo: '',
  applicantName: '',
  categoryCode: '',
  requestStatus: '',
})

const reviewForm = reactive({
  requestId: undefined as number | undefined,
  actionType: 'APPROVE',
  commentText: '',
})

const selectedIds = ref<number[]>([])
const batchOpen = ref(false)
const batchForm = reactive({ actionType: 'APPROVE', commentText: '' })
const overviewTotalCount = computed(() => Object.values(statisticsMap.value).reduce((sum, count) => sum + Number(count || 0), 0) || total.value)

const formDataEntries = computed(() => {
  const fields = detailBundle.value?.formFields || []
  const formData = detailBundle.value?.formData || {}
  return fields
    .map((item: any) => ({ label: item.label, value: resolveFieldDisplay(item, formData[item.field]) }))
    .filter((item: any) => item.value !== '')
})

const attachmentEntries = computed(() => {
  const list = detailBundle.value?.attachments || []
  return list.map((item: any) => ({ name: item.name || item.originalFilename || item.newFileName, url: item.url || item.value || item }))
})

function resolveFieldDisplay(field: any, value: any) {
  if (value === null || value === undefined || value === '') return ''
  if (field.component === 'select' && Array.isArray(field.options)) {
    const option = field.options.find((item: any) => String(item.value) === String(value))
    return option?.label || value
  }
  return value
}

function countByStatus(status: string) {
  return statisticsMap.value[status] ?? requestList.value.filter((item) => item.requestStatus === status).length
}

function statusLabel(status?: string) {
  return status === 'APPROVED' ? '已通过' : status === 'REJECTED' ? '已驳回' : status === 'CANCELLED' ? '已撤回' : '待审核'
}

function actionResultLabel(action?: string) {
  const map: Record<string, string> = { APPROVE: '通过', REJECT: '驳回', CANCEL: '撤回', SUBMIT: '提交', RESUBMIT: '重新提交', APPROVED: '已通过', REJECTED: '已驳回', CANCELLED: '已撤回', PENDING: '待审核' }
  return map[action || ''] || action || '-'
}

function statusTagType(status?: string) {
  return status === 'APPROVED' ? 'success' : status === 'REJECTED' ? 'danger' : status === 'CANCELLED' ? 'info' : 'warning'
}

async function loadOptions() {
  const res = await listAffairCategory({ pageNum: 1, pageSize: 200 })
  categoryOptions.value = res.rows || []
}

async function loadStatistics() {
  const res = await getAffairRequestStatistics()
  const data = res.data || res
  const rows = data.byStatus || []
  statisticsMap.value = Object.fromEntries(rows.map((item: any) => [item.status, Number(item.count || 0)]))
}

async function getList() {
  loading.value = true
  try {
    const [res] = await Promise.all([
      listAffairRequest(queryParams),
      loadStatistics(),
    ])
    requestList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(queryParams, { pageNum: 1, pageSize: 10, requestNo: '', applicantName: '', categoryCode: '', requestStatus: '' })
  getList()
}

async function viewDetail(row: any) {
  const res = await getAffairRequest(row.requestId)
  detailBundle.value = res.data || res
  detailOpen.value = true
}

function openReviewDialog(row: any) {
  currentRow.value = row
  reviewForm.requestId = row.requestId
  reviewForm.actionType = 'APPROVE'
  reviewForm.commentText = ''
  reviewOpen.value = true
}

async function submitReview() {
  if (!reviewForm.requestId) return
  reviewSubmitting.value = true
  try {
    await reviewAffairRequest(reviewForm)
    ElMessage.success(reviewForm.actionType === 'APPROVE' ? '审核通过' : '审核已驳回')
    reviewOpen.value = false
    await getList()
  } finally {
    reviewSubmitting.value = false
  }
}

function handleSelectionChange(selection: any[]) {
  selectedIds.value = selection.filter(r => r.requestStatus === 'PENDING').map(r => r.requestId)
}

function openBatchReview(actionType: string) {
  batchForm.actionType = actionType
  batchForm.commentText = ''
  batchOpen.value = true
}

async function submitBatchReview() {
  if (!selectedIds.value.length) return
  reviewSubmitting.value = true
  try {
    const res = await batchReviewAffairRequest({
      requestIds: selectedIds.value,
      actionType: batchForm.actionType,
      commentText: batchForm.commentText,
    })
    ElMessage.success(res.data?.message || res.message || '批量审核完成')
    if ((res.data?.errors || []).length) {
      await ElMessageBox.alert((res.data.errors || []).join('<br/>'), '部分申请未处理', {
        dangerouslyUseHTMLString: true,
        type: 'warning',
      })
    }
    batchOpen.value = false
    selectedIds.value = []
    await getList()
  } finally {
    reviewSubmitting.value = false
  }
}

async function handleExport() {
  const blob = await exportAffairRequest(queryParams)
  const file = new Blob([blob], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  const url = window.URL.createObjectURL(file)
  const link = document.createElement('a')
  link.href = url
  link.download = `学生事务申请-${new Date().toISOString().slice(0, 10)}.xlsx`
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(url)
}

onMounted(async () => {
  await loadOptions()
  await getList()
})
</script>

<style scoped>
.mb16 {
  margin-bottom: 16px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.overview-card {
  padding: 16px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.overview-card span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.overview-card strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
  color: #0f172a;
}

.overview-card.is-warning strong { color: #d97706; }
.overview-card.is-success strong { color: #059669; }
.overview-card.is-danger strong { color: #dc2626; }

.summary-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.summary-cell strong {
  color: #172033;
}

.summary-cell span {
  color: #667085;
  font-size: 12px;
}

.detail-layout {
  display: grid;
  gap: 16px;
}

.detail-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px;
  border-radius: 14px;
  background: linear-gradient(135deg, #eff6ff, #f8fafc);
}

.detail-hero h3 {
  margin: 0 0 6px;
}

.detail-hero p {
  margin: 0;
  color: #64748b;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.detail-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
  background: #fff;
}

.detail-card h4 {
  margin: 0 0 12px;
}

.detail-card p {
  margin: 0 0 8px;
  line-height: 1.7;
}

.attachment-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.attachment-list a {
  color: var(--el-color-primary);
}

.timeline-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.timeline-content span,
.timeline-content p {
  color: #667085;
  margin: 0;
}

.review-panel__summary {
  margin-bottom: 16px;
  padding: 14px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.review-panel__summary span,
.review-panel__summary p {
  color: #667085;
  margin: 0;
}
</style>
