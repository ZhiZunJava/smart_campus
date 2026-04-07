<template>
  <StudentAffairPageFrame
    :loading="loading"
    :has-detail="!!detail"
    :presentation="presentation"
    :title="presentation?.title || '请销假服务'"
    :subtitle="presentation?.subtitle || '请假申请、审批结果、销假登记一体化办理'"
    :metrics="metrics"
    :summary-cards="summaryCards"
    :actions="actions"
    :guide-steps="guideSteps"
    @back="$emit('back')"
    @action="handleAction"
  >
    <!-- 当前请假中 -->
    <section v-if="activeLeaveCard" class="service-section">
      <div class="section-head">
        <div>
          <h3>请假中</h3>
          <p>当前正在生效的请假记录</p>
        </div>
      </div>
      <article class="ongoing-card">
        <div class="ongoing-card__info">
          <strong>当前请假中</strong>
          <p>{{ leaveTypeLabel(activeLeaveCard.leaveType) }} · {{ formatDateRange(activeLeaveCard.startDate, activeLeaveCard.endDate) }}</p>
          <p v-if="activeLeaveCard.reason">{{ activeLeaveCard.reason }}</p>
        </div>
        <div class="ongoing-card__trail">
          <el-tag type="success" size="small">进行中</el-tag>
        </div>
      </article>
    </section>

    <!-- 待销假事项 -->
    <section v-if="pendingLeaveCards.length" class="service-section">
      <div class="section-head">
        <div>
          <h3>待销假事项</h3>
          <p>已审批通过但尚未销假的记录，返校后请及时完成销假登记</p>
        </div>
      </div>
      <div class="record-list">
        <div v-for="item in pendingLeaveCards" :key="item.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ leaveTypeLabel(item.leaveType) }}</strong>
            <span class="record-row__sub">{{ item.requestNo }}</span>
          </div>
          <div class="record-row__detail">
            <span>{{ formatDateRange(item.startDate, item.endDate) }}</span>
            <span class="sep">·</span>
            <span>{{ item.destination || '-' }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(item.requestStatus)" size="small">{{ statusLabel(item.requestStatus) }}</el-tag>
            <el-button type="warning" size="small" @click="openCancelFor(item)">去销假</el-button>
            <el-button link type="primary" size="small" @click="openRecordDetail(item)">查看详情</el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 请假记录 -->
    <section class="service-section">
      <div class="section-head">
        <div>
          <h3>请假记录</h3>
          <p>历次请假申请及审批结果</p>
        </div>
      </div>
      <div v-if="leaveDisplayRecords.length" class="record-list">
        <div v-for="row in leaveDisplayRecords" :key="row.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ leaveTypeLabel(row.leaveType) }}</strong>
            <span class="record-row__sub">{{ row.requestNo }}</span>
          </div>
          <div class="record-row__detail">
            <span>{{ formatDateRange(row.startDate, row.endDate) }}</span>
            <span class="sep">·</span>
            <span>{{ row.destination || '-' }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(row.requestStatus)" size="small">{{ statusLabel(row.requestStatus) }}</el-tag>
            <span class="record-row__time">{{ row.submittedTime || '-' }}</span>
            <el-button link type="primary" size="small" @click="openRecordDetail(row)">查看详情</el-button>
          </div>
        </div>
      </div>
      <div v-else class="no-data-hint">暂无请假记录</div>
    </section>

    <!-- 销假记录 -->
    <section class="service-section">
      <div class="section-head">
        <div>
          <h3>销假记录</h3>
          <p>返校后的销假登记记录</p>
        </div>
      </div>
      <div v-if="cancelDisplayRecords.length" class="record-list">
        <div v-for="row in cancelDisplayRecords" :key="row.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ row.requestNo }}</strong>
            <span class="record-row__sub">{{ row.leaveRequestNo || '-' }}</span>
          </div>
          <div class="record-row__detail">
            <span>实际返校 {{ row.actualReturnDate || '-' }}</span>
            <span class="sep">·</span>
            <span>{{ row.cancelRemark || '暂无说明' }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(row.requestStatus)" size="small">{{ statusLabel(row.requestStatus) }}</el-tag>
            <span class="record-row__time">{{ row.submittedTime || '-' }}</span>
            <el-button link type="primary" size="small" @click="openRecordDetail(row)">查看详情</el-button>
          </div>
        </div>
      </div>
      <div v-else class="no-data-hint">暂无销假记录</div>
    </section>
  </StudentAffairPageFrame>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import StudentAffairPageFrame from './StudentAffairPageFrame.vue'
import {
  buildApplyPayload,
  formatDateRange,
  pickLatestRecord,
  resolveOptionLabel,
  statusLabel,
  statusType,
  useStudentAffairDetail,
} from './useStudentAffairDetail'

const props = defineProps<{
  portalRole: 'student'
  categoryCode: string
}>()

const emit = defineEmits<{
  back: []
  apply: [payload: any]
  'view-request': [request: any]
}>()

const {
  loading,
  detail,
  presentation,
  requests,
  stats,
  specializedData,
  advisorInfo,
  guideSteps,
  findTemplate,
} = useStudentAffairDetail(computed(() => props.categoryCode || 'ASK_LEAVE'))

const leaveTypeOptions = [
  { label: '事假', value: 'PERSONAL' },
  { label: '病假', value: 'SICK' },
  { label: '公假', value: 'OFFICIAL' },
]

const leaveRequests = computed(() => requests.value.filter((item: any) => item.templateCode === 'ASK_LEAVE_STANDARD'))
const cancelRecords = computed(() => requests.value.filter((item: any) => item.templateCode === 'ASK_LEAVE_CANCEL'))
const canceledNos = computed(() => new Set(cancelRecords.value.map((item: any) => item.parsedFormData.leaveRequestNo).filter(Boolean)))
const pendingLeaves = computed(() => leaveRequests.value
  .filter((item: any) => item.requestStatus === 'APPROVED' && !canceledNos.value.has(item.requestNo))
  .sort((a: any, b: any) => String(b.submittedTime || '').localeCompare(String(a.submittedTime || ''))))
const latestPendingLeave = computed(() => pickLatestRecord(pendingLeaveCards.value))
const activeLeaveCard = computed(() => {
  const today = new Date()
  return pendingLeaveCards.value.find((item: any) => {
    const start = item.startDate ? new Date(item.startDate) : null
    const end = item.endDate ? new Date(item.endDate) : null
    if (!start || Number.isNaN(start.getTime()) || !end || Number.isNaN(end.getTime())) return false
    const endWithDay = new Date(end)
    endWithDay.setHours(23, 59, 59, 999)
    return today >= start && today <= endWithDay
  }) || null
})
const leaveDisplayRecords = computed(() => {
  if (Array.isArray(specializedData.value?.leaveRecords) && specializedData.value.leaveRecords.length) {
    return specializedData.value.leaveRecords.map((item: any) => ({
      requestId: item.request_id,
      requestNo: item.request_no,
      leaveType: item.leave_type,
      startDate: item.start_date,
      endDate: item.end_date,
      destination: item.destination,
      emergencyContact: item.emergency_contact,
      reason: item.reason,
      requestStatus: item.request_status,
      submittedTime: item.update_time || item.create_time,
    }))
  }
  return leaveRequests.value.map((item: any) => ({
    requestId: item.requestId,
    requestNo: item.requestNo,
    leaveType: item.parsedFormData.leaveType,
    startDate: item.parsedFormData.startDate,
    endDate: item.parsedFormData.endDate,
    destination: item.parsedFormData.destination,
    emergencyContact: item.parsedFormData.emergencyContact,
    reason: item.parsedFormData.reason,
    requestStatus: item.requestStatus,
    submittedTime: item.submittedTime,
  }))
})
const cancelDisplayRecords = computed(() => {
  if (Array.isArray(specializedData.value?.cancelRecords) && specializedData.value.cancelRecords.length) {
    return specializedData.value.cancelRecords.map((item: any) => ({
      requestId: item.request_id,
      requestNo: item.request_no,
      leaveRequestNo: item.leave_request_no,
      actualReturnDate: item.actual_return_date,
      cancelRemark: item.cancel_remark,
      requestStatus: item.request_status,
      submittedTime: item.update_time || item.create_time,
    }))
  }
  return cancelRecords.value.map((item: any) => ({
    requestId: item.requestId,
    requestNo: item.requestNo,
    leaveRequestNo: item.parsedFormData.leaveRequestNo,
    actualReturnDate: item.parsedFormData.actualReturnDate,
    cancelRemark: item.parsedFormData.cancelRemark,
    requestStatus: item.requestStatus,
    submittedTime: item.submittedTime,
  }))
})
const pendingLeaveCards = computed(() => {
  if (Array.isArray(specializedData.value?.pendingCancelRecords) && specializedData.value.pendingCancelRecords.length) {
    return specializedData.value.pendingCancelRecords.map((item: any) => ({
      requestId: item.request_id,
      requestNo: item.request_no,
      leaveType: item.leave_type,
      startDate: item.start_date,
      endDate: item.end_date,
      destination: item.destination,
      emergencyContact: item.emergency_contact,
      reason: item.reason,
      requestStatus: item.request_status,
    }))
  }
  return pendingLeaves.value.map((item: any) => ({
    requestId: item.requestId,
    requestNo: item.requestNo,
    leaveType: item.parsedFormData.leaveType,
    startDate: item.parsedFormData.startDate,
    endDate: item.parsedFormData.endDate,
    destination: item.parsedFormData.destination,
    emergencyContact: item.parsedFormData.emergencyContact,
    reason: item.parsedFormData.reason,
    requestStatus: item.requestStatus,
  }))
})

const metrics = computed(() => ([
  { label: '请假申请', value: Number(specializedData.value?.leaveCount || leaveRequests.value.length), hint: '累计请假申请', tone: '' },
  { label: '销假登记', value: Number(specializedData.value?.cancelCount || cancelRecords.value.length), hint: '已完成返校登记', tone: 'success' },
  { label: '待销假', value: Number(specializedData.value?.pendingCancelCount || pendingLeaves.value.length), hint: '返校后待补齐闭环', tone: 'warning' },
  { label: '待审核', value: Number(stats.value.pending || 0), hint: '仍在审批流中的申请', tone: 'danger' },
]))

const summaryCards = computed(() => {
  const cards: any[] = []
  if (advisorInfo.value) {
    cards.push({
      icon: 'ri-user-star-line',
      title: '辅导员',
      value: advisorInfo.value.advisorName,
      badge: advisorInfo.value.bindingMode === 'DIRECT' ? '显式绑定' : '班级回退',
      lines: [
        advisorInfo.value.phonenumber ? `电话：${advisorInfo.value.phonenumber}` : '已自动绑定审核辅导员',
      ],
    })
  }
  cards.push({
    icon: Number(specializedData.value?.pendingCancelCount || pendingLeaves.value.length) ? 'ri-alarm-warning-line' : 'ri-checkbox-circle-line',
    title: '待销假',
    value: Number(specializedData.value?.pendingCancelCount || pendingLeaves.value.length),
    tone: Number(specializedData.value?.pendingCancelCount || pendingLeaves.value.length) ? 'warning' : 'success',
    lines: latestPendingLeave.value
      ? [`单号：${latestPendingLeave.value.requestNo}`, `时段：${formatDateRange(latestPendingLeave.value.startDate, latestPendingLeave.value.endDate)}`]
      : ['考勤闭环完整'],
  })
  cards.push({
    icon: Number(stats.value.pending || 0) ? 'ri-time-line' : 'ri-checkbox-circle-line',
    title: '审批进度',
    value: Number(stats.value.pending || 0),
    tone: Number(stats.value.pending || 0) ? 'info' : 'success',
    lines: [`通过 ${Number(stats.value.approved || 0)} · 驳回 ${Number(stats.value.rejected || 0)}`],
  })
  return cards
})

const actions = computed(() => {
  const leaveTemplate = findTemplate('ASK_LEAVE_STANDARD')
  const cancelTemplate = findTemplate('ASK_LEAVE_CANCEL')
  const result: any[] = []
  if (leaveTemplate) {
    result.push({ label: '发起请假', template: leaveTemplate, type: 'primary' })
  }
  if (cancelTemplate) {
    result.push({
      label: latestPendingLeave.value ? '去销假登记' : '填写销假登记',
      template: cancelTemplate,
      type: 'warning',
      presetData: latestPendingLeave.value ? { leaveRequestNo: latestPendingLeave.value.requestNo } : null,
      title: latestPendingLeave.value ? `销假登记 - ${latestPendingLeave.value.requestNo}` : '销假登记',
    })
  }
  return result
})

function leaveTypeLabel(value?: string) {
  return resolveOptionLabel(leaveTypeOptions, value, '未填写')
}

function handleAction(action: any) {
  if (!action?.template) return
  emit('apply', buildApplyPayload(action))
}

function openCancelFor(record: any) {
  const cancelTemplate = findTemplate('ASK_LEAVE_CANCEL')
  if (!cancelTemplate) return
  emit('apply', buildApplyPayload({
    template: cancelTemplate,
    presetData: { leaveRequestNo: record.requestNo },
    title: `销假登记 - ${record.requestNo}`,
  }))
}

function openRecordDetail(row: any) {
  const matched = leaveRequests.value.find((item: any) => Number(item.requestId) === Number(row.requestId))
    || cancelRecords.value.find((item: any) => Number(item.requestId) === Number(row.requestId))
  emit('view-request', matched || { requestId: row.requestId })
}
</script>

<style scoped>
@import './student-affair-shared.css';

.ongoing-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-radius: 10px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-left: 4px solid #22c55e;
}
.ongoing-card__info strong { font-size: 15px; color: #166534; }
.ongoing-card__info p { margin: 4px 0 0; color: #667085; font-size: 13px; }
.ongoing-card__trail { flex-shrink: 0; }

@media (max-width: 768px) {
  .ongoing-card { flex-direction: column; align-items: flex-start; gap: 10px; padding: 14px 16px; }
  .ongoing-card__trail { align-self: flex-end; }
}

@media (max-width: 640px) {
  .ongoing-card { padding: 12px; border-radius: 8px; }
  .ongoing-card__info strong { font-size: 14px; }
  .ongoing-card__info p { font-size: 12px; }
}
</style>
