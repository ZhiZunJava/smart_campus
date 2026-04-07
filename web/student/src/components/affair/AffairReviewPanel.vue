<template>
  <div class="review-panel">
    <div class="back-nav">
      <el-button text bg size="small" @click="$emit('back')"><i class="ri-arrow-left-line" /> 返回</el-button>
      <span class="panel-title">待审事务</span>
    </div>

    <div v-if="allowBatch" class="batch-bar">
      <el-checkbox v-model="selectAll" :indeterminate="indeterminate" @change="toggleSelectAll">全选</el-checkbox>
      <template v-if="selectedIds.length">
        <el-button type="success" plain size="small" @click="batchAction('APPROVE')">批量通过 ({{ selectedIds.length }})</el-button>
        <el-button type="danger" plain size="small" @click="batchAction('REJECT')">批量驳回 ({{ selectedIds.length }})</el-button>
      </template>
    </div>

    <el-table v-loading="loading" :data="reviewList" @selection-change="handleSelectionChange">
      <el-table-column v-if="allowBatch" type="selection" width="50" />
      <el-table-column prop="requestNo" label="申请编号" min-width="170" />
      <el-table-column label="申请人" min-width="160">
        <template #default="{ row }">
          <div class="applicant-cell">
            <strong>{{ row.applicantName }}</strong>
            <span>{{ row.applicantNo || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="templateName" label="服务名称" min-width="190" />
      <el-table-column prop="summaryText" label="摘要" min-width="240" show-overflow-tooltip />
      <el-table-column prop="submittedTime" label="提交时间" width="165" />
      <el-table-column label="操作" width="170" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="$emit('view-request', row)">详情</el-button>
          <el-button link type="success" @click="openReviewDialog(row)">审核</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && !reviewList.length" description="暂无待审核事务" />

    <el-dialog v-model="reviewOpen" title="审核事务申请" width="620px">
      <div v-if="reviewTarget" class="review-box">
        <div class="review-box__summary">
          <strong>{{ reviewTarget.templateName }}</strong>
          <p>{{ reviewTarget.applicantName }} · {{ reviewTarget.summaryText || '暂无摘要' }}</p>
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
        <el-button type="primary" :loading="submitting" @click="submitReview">提交审核</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="batchOpen" title="批量审核" width="520px">
      <p>将对 <strong>{{ selectedIds.length }}</strong> 条申请执行 <el-tag :type="batchForm.actionType === 'APPROVE' ? 'success' : 'danger'">{{ batchForm.actionType === 'APPROVE' ? '通过' : '驳回' }}</el-tag> 操作</p>
      <el-input v-model="batchForm.commentText" type="textarea" :rows="3" maxlength="300" show-word-limit placeholder="批量审核意见（可选）" />
      <template #footer>
        <el-button @click="batchOpen = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitBatchReview">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage } from '@/utils/feedback'
import { batchReviewPortalAffairRequests, reviewPortalAffairRequest } from '@/api/portal'

const props = defineProps<{
  portalRole: 'teacher' | 'advisor'
  reviewList: any[]
  loading: boolean
  allowBatch?: boolean
}>()

const emit = defineEmits<{
  back: []
  'view-request': [request: any]
  refresh: []
}>()

const submitting = ref(false)
const reviewOpen = ref(false)
const batchOpen = ref(false)
const reviewTarget = ref<any>(null)
const selectedIds = ref<number[]>([])
const selectAll = ref(false)
const indeterminate = computed(() => selectedIds.value.length > 0 && selectedIds.value.length < props.reviewList.length)

const reviewForm = reactive({ requestId: undefined as number | undefined, actionType: 'APPROVE', commentText: '' })
const batchForm = reactive({ actionType: 'APPROVE', commentText: '' })

function handleSelectionChange(selection: any[]) {
  selectedIds.value = selection.map(s => s.requestId)
  selectAll.value = selectedIds.value.length === props.reviewList.length
}

function toggleSelectAll(val: boolean) {
  selectedIds.value = val ? props.reviewList.map(r => r.requestId) : []
}

function openReviewDialog(row: any) {
  reviewTarget.value = row
  reviewForm.requestId = row.requestId
  reviewForm.actionType = 'APPROVE'
  reviewForm.commentText = ''
  reviewOpen.value = true
}

function batchAction(actionType: string) {
  batchForm.actionType = actionType
  batchForm.commentText = ''
  batchOpen.value = true
}

async function submitReview() {
  if (!reviewForm.requestId) return
  submitting.value = true
  try {
    await reviewPortalAffairRequest(props.portalRole, reviewForm)
    ElMessage.success(reviewForm.actionType === 'APPROVE' ? '审核通过' : '已驳回申请')
    reviewOpen.value = false
    emit('refresh')
  } finally {
    submitting.value = false
  }
}

async function submitBatchReview() {
  if (!selectedIds.value.length) return
  submitting.value = true
  try {
    const res = await batchReviewPortalAffairRequests(props.portalRole, {
      requestIds: selectedIds.value,
      actionType: batchForm.actionType,
      commentText: batchForm.commentText,
    })
    const data = res.data || res
    ElMessage.success(data.message || '批量审核完成')
    batchOpen.value = false
    selectedIds.value = []
    emit('refresh')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.review-panel { display: grid; gap: 16px; }
.back-nav { display: flex; align-items: center; gap: 12px; margin-bottom: -4px; }
.panel-title { font-size: 18px; font-weight: 700; color: #0f172a; }
.batch-bar { display: flex; align-items: center; gap: 12px; padding: 10px 16px; border-radius: 12px; background: #f8fafc; border: 1px solid #e2e8f0; }
.applicant-cell { display: flex; flex-direction: column; gap: 2px; }
.applicant-cell span { color: #667085; font-size: 12px; }
.review-box { display: grid; gap: 14px; }
.review-box__summary { padding: 14px; border-radius: 12px; background: #f8fafc; border: 1px solid #e2e8f0; transition: transform .15s ease, box-shadow .15s ease; }
.review-box__summary:hover { transform: scale(1.02); box-shadow: 0 4px 16px rgba(15,23,42,.06); }
.review-box__summary p { margin: 6px 0 0; color: #667085; }

@media (max-width: 768px) {
  :deep(.el-table) { overflow-x: auto; }
  .batch-bar { flex-wrap: wrap; }
}
@media (max-width: 640px) {
  .batch-bar { padding: 8px 12px; flex-direction: column; align-items: flex-start; }
  .review-panel { gap: 12px; }
  .panel-title { font-size: 16px; }
  .review-box__summary { padding: 12px; }
  .review-box__summary p { font-size: 13px; }
}

@media (prefers-reduced-motion: reduce) {
  * { transition: none !important; }
}
</style>
