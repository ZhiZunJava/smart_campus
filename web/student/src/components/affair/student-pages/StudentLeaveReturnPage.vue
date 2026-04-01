<template>
  <StudentAffairPageFrame
    :loading="loading"
    :has-detail="!!detail"
    :presentation="presentation"
    :title="presentation?.title || '离返校服务'"
    :subtitle="presentation?.subtitle || '离校申请、返校登记和行程信息同步管理'"
    :metrics="metrics"
    :summary-cards="summaryCards"
    :actions="actions"
    :guide-steps="guideSteps"
    @back="$emit('back')"
    @action="handleAction"
  >
    <!-- 待返校登记 — record-list pattern -->
    <section v-if="outstandingLeaveCards.length" class="service-section">
      <div class="section-head">
        <div>
          <h3>待返校登记</h3>
          <p>以下离校申请已完成审批，但还未登记返校，建议返校后及时补充行程信息。</p>
        </div>
      </div>
      <div class="record-list">
        <div v-for="item in outstandingLeaveCards" :key="item.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ item.requestNo }}</strong>
            <span class="record-row__sub">{{ healthStatusLabel(item.healthStatus) }}</span>
          </div>
          <div class="record-row__detail">
            <span>离校 {{ item.leaveSchoolDate || '-' }}</span>
            <span class="sep">·</span>
            <span>预计返校 {{ item.expectedReturnDate || '-' }}</span>
            <span class="sep">·</span>
            <span>{{ item.location || '-' }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(item.requestStatus)" size="small">{{ statusLabel(item.requestStatus) }}</el-tag>
            <el-button type="success" size="small" @click="openReturnFor(item)">返校登记</el-button>
            <el-button link type="primary" size="small" @click="openRecordDetail(item)">详情</el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 离返校记录（合并） -->
    <section class="service-section">
      <div class="section-head">
        <div>
          <h3>离返校记录</h3>
          <p>离校申请与返校登记的合并时间线，按提交时间倒序排列。</p>
        </div>
      </div>
      <div v-if="mergedRecords.length" class="record-list">
        <div v-for="item in mergedRecords" :key="item.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ item.requestNo }}</strong>
            <span class="record-row__sub">
              <el-tag :type="item._kind === 'leave' ? '' : 'success'" size="small" effect="plain">
                {{ item._kind === 'leave' ? '离校' : '返校' }}
              </el-tag>
            </span>
          </div>
          <div class="record-row__detail">
            <template v-if="item._kind === 'leave'">
              <span>{{ formatDateRange(item.leaveSchoolDate, item.expectedReturnDate) }}</span>
              <span class="sep">·</span>
              <span>{{ item.location || '-' }}</span>
              <span class="sep">·</span>
              <span>{{ healthStatusLabel(item.healthStatus) }}</span>
            </template>
            <template v-else>
              <span>返校 {{ item.returnDate || '-' }}</span>
              <span class="sep">·</span>
              <span>对应离校 {{ item.leaveRequestNo || '-' }}</span>
              <span v-if="item.travelSummary" class="sep">·</span>
              <span v-if="item.travelSummary">{{ item.travelSummary }}</span>
            </template>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(item.requestStatus)" size="small">{{ statusLabel(item.requestStatus) }}</el-tag>
            <span class="record-row__time">{{ item.submittedTime || '-' }}</span>
            <el-button link type="primary" size="small" @click="openRecordDetail(item)">详情</el-button>
          </div>
        </div>
      </div>
      <div v-else class="no-data-hint">暂无离返校记录</div>
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
} = useStudentAffairDetail(computed(() => props.categoryCode || 'LEAVE_RETURN_SCHOOL'))

const healthStatusOptions = [
  { label: '良好', value: 'GOOD' },
  { label: '需关注', value: 'WATCH' },
  { label: '特殊情况', value: 'SPECIAL' },
]

const leaveRequests = computed(() => requests.value.filter((item: any) => item.templateCode === 'LEAVE_RETURN_STANDARD'))
const returnRecords = computed(() => requests.value.filter((item: any) => item.templateCode === 'LEAVE_RETURN_CONFIRM'))
const returnedNos = computed(() => new Set(returnRecords.value.map((item: any) => item.parsedFormData.leaveRequestNo).filter(Boolean)))
const outstandingLeaves = computed(() => leaveRequests.value
  .filter((item: any) => item.requestStatus === 'APPROVED' && !returnedNos.value.has(item.requestNo))
  .sort((a: any, b: any) => String(b.submittedTime || '').localeCompare(String(a.submittedTime || ''))))

const leaveDisplayRecords = computed(() => {
  if (Array.isArray(specializedData.value?.leaveRecords) && specializedData.value.leaveRecords.length) {
    return specializedData.value.leaveRecords.map((item: any) => ({
      requestId: item.request_id,
      requestNo: item.request_no,
      leaveSchoolDate: item.leave_school_date,
      expectedReturnDate: item.expected_return_date,
      location: item.location,
      healthStatus: item.health_status,
      requestStatus: item.request_status,
      submittedTime: item.update_time || item.create_time,
    }))
  }
  return leaveRequests.value.map((item: any) => ({
    requestId: item.requestId,
    requestNo: item.requestNo,
    leaveSchoolDate: item.parsedFormData.leaveSchoolDate,
    expectedReturnDate: item.parsedFormData.expectedReturnDate,
    location: item.parsedFormData.location,
    healthStatus: item.parsedFormData.healthStatus,
    requestStatus: item.requestStatus,
    submittedTime: item.submittedTime,
  }))
})

const returnDisplayRecords = computed(() => {
  if (Array.isArray(specializedData.value?.returnRecords) && specializedData.value.returnRecords.length) {
    return specializedData.value.returnRecords.map((item: any) => ({
      requestId: item.request_id,
      requestNo: item.request_no,
      leaveRequestNo: item.leave_request_no,
      returnDate: item.return_date,
      travelSummary: item.travel_summary,
      requestStatus: item.request_status,
      submittedTime: item.update_time || item.create_time,
    }))
  }
  return returnRecords.value.map((item: any) => ({
    requestId: item.requestId,
    requestNo: item.requestNo,
    leaveRequestNo: item.parsedFormData.leaveRequestNo,
    returnDate: item.parsedFormData.returnDate,
    travelSummary: item.parsedFormData.travelSummary,
    requestStatus: item.requestStatus,
    submittedTime: item.submittedTime,
  }))
})

/** Merged timeline: leave + return records sorted by submittedTime desc */
const mergedRecords = computed(() => {
  const leaves = leaveDisplayRecords.value.map((r: any) => ({ ...r, _kind: 'leave' as const }))
  const returns = returnDisplayRecords.value.map((r: any) => ({ ...r, _kind: 'return' as const }))
  return [...leaves, ...returns].sort(
    (a, b) => String(b.submittedTime || '').localeCompare(String(a.submittedTime || '')),
  )
})

const outstandingLeaveCards = computed(() => {
  if (Array.isArray(specializedData.value?.pendingReturnRecords) && specializedData.value.pendingReturnRecords.length) {
    return specializedData.value.pendingReturnRecords.map((item: any) => ({
      requestId: item.request_id,
      requestNo: item.request_no,
      leaveSchoolDate: item.leave_school_date,
      expectedReturnDate: item.expected_return_date,
      location: item.location,
      healthStatus: item.health_status,
      requestStatus: item.request_status,
    }))
  }
  return outstandingLeaves.value.map((item: any) => ({
    requestId: item.requestId,
    requestNo: item.requestNo,
    leaveSchoolDate: item.parsedFormData.leaveSchoolDate,
    expectedReturnDate: item.parsedFormData.expectedReturnDate,
    location: item.parsedFormData.location,
    healthStatus: item.parsedFormData.healthStatus,
    requestStatus: item.requestStatus,
  }))
})
const latestOutstandingLeave = computed(() => pickLatestRecord(outstandingLeaveCards.value))

const metrics = computed(() => ([
  { label: '离校申请', value: Number(specializedData.value?.leaveCount || leaveRequests.value.length), hint: '累计离校申请', tone: '' },
  { label: '返校登记', value: Number(specializedData.value?.returnCount || returnRecords.value.length), hint: '累计返校登记', tone: 'success' },
  { label: '待返校', value: Number(specializedData.value?.pendingReturnCount || outstandingLeaves.value.length), hint: '已离校未登记返校', tone: 'warning' },
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
      lines: [advisorInfo.value.phonenumber ? `电话：${advisorInfo.value.phonenumber}` : '已绑定审核辅导员'],
    })
  }
  cards.push({
    icon: Number(specializedData.value?.pendingReturnCount || outstandingLeaves.value.length) ? 'ri-alarm-warning-line' : 'ri-checkbox-circle-line',
    title: '待返校',
    value: Number(specializedData.value?.pendingReturnCount || outstandingLeaves.value.length),
    tone: Number(specializedData.value?.pendingReturnCount || outstandingLeaves.value.length) ? 'warning' : 'success',
    lines: latestOutstandingLeave.value
      ? [`离校：${latestOutstandingLeave.value.leaveSchoolDate || '-'}`, `预返：${latestOutstandingLeave.value.expectedReturnDate || '-'}`]
      : ['无待返校记录'],
  })
  cards.push({
    icon: Number(stats.value.pending || 0) ? 'ri-time-line' : 'ri-checkbox-circle-line',
    title: '申请进度',
    value: Number(stats.value.pending || 0),
    tone: Number(stats.value.pending || 0) ? 'info' : 'success',
    lines: [`通过 ${Number(stats.value.approved || 0)} · 驳回 ${Number(stats.value.rejected || 0)}`],
  })
  return cards
})

const actions = computed(() => {
  const leaveTemplate = findTemplate('LEAVE_RETURN_STANDARD')
  const returnTemplate = findTemplate('LEAVE_RETURN_CONFIRM')
  const result: any[] = []
  if (leaveTemplate) {
    result.push({ label: '发起离校申请', template: leaveTemplate, type: 'primary' })
  }
  if (returnTemplate) {
    result.push({
      label: latestOutstandingLeave.value ? '返校登记' : '填写返校登记',
      template: returnTemplate,
      type: 'success',
      presetData: latestOutstandingLeave.value ? { leaveRequestNo: latestOutstandingLeave.value.requestNo } : null,
      title: latestOutstandingLeave.value ? `返校登记 - ${latestOutstandingLeave.value.requestNo}` : '返校登记',
    })
  }
  return result
})

function healthStatusLabel(value?: string) {
  return resolveOptionLabel(healthStatusOptions, value, '未填写')
}

function handleAction(action: any) {
  if (!action?.template) return
  emit('apply', buildApplyPayload(action))
}

function openReturnFor(record: any) {
  const returnTemplate = findTemplate('LEAVE_RETURN_CONFIRM')
  if (!returnTemplate) return
  emit('apply', buildApplyPayload({
    template: returnTemplate,
    presetData: { leaveRequestNo: record.requestNo },
    title: `返校登记 - ${record.requestNo}`,
  }))
}

function openRecordDetail(row: any) {
  const matched = leaveRequests.value.find((item: any) => Number(item.requestId) === Number(row.requestId))
    || returnRecords.value.find((item: any) => Number(item.requestId) === Number(row.requestId))
  emit('view-request', matched || { requestId: row.requestId })
}
</script>

<style scoped>
@import './student-affair-shared.css';

.record-row__sub .el-tag { vertical-align: text-top; }
</style>
