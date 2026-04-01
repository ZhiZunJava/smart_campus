<template>
  <StudentAffairPageFrame
    :loading="loading"
    :has-detail="!!detail"
    :presentation="presentation"
    :title="presentation?.title || '学籍异动服务'"
    :subtitle="presentation?.subtitle || '休学、复学、转专业等学籍业务线上办理'"
    :metrics="metrics"
    :summary-cards="summaryCards"
    :actions="actions"
    :guide-steps="guideSteps"
    @back="$emit('back')"
    @action="handleAction"
  >
    <section v-if="allTemplateCards.length" class="service-section">
      <div class="section-head">
        <div>
          <h3>可申请事项</h3>
          <p>当前可申请的学籍异动类型与申报时段，更贴近高校实际办理场景。</p>
        </div>
      </div>
      <div class="window-list">
        <div v-for="item in allTemplateCards" :key="item.templateId" class="window-row" :class="item.tone">
          <div class="window-row__info">
            <strong>{{ item.businessName }}</strong>
            <div class="window-row__meta">
              <span>{{ item.termLabel }}</span><span class="sep">·</span><span>{{ item.windowLabel }}</span>
            </div>
            <div v-if="item.notice" class="window-row__notice">{{ item.notice }}</div>
          </div>
          <div class="window-row__action">
            <el-button type="primary" size="small" @click="handleAction({ template: item.rawTemplate, type: 'primary' })">立即申请</el-button>
          </div>
        </div>
      </div>
    </section>

    <section v-if="pendingLedgerRecords.length" class="service-section">
      <div class="section-head">
        <div>
          <h3>处理中异动事项</h3>
          <p>当前仍在流程中的学籍异动会显示在这里，便于你跟踪审批和生效时间。</p>
        </div>
      </div>
      <div class="record-list">
        <div v-for="item in pendingLedgerRecords" :key="item.request_id" class="record-row">
          <div class="record-row__main">
            <strong>{{ item.after_status_name || '学籍异动' }}</strong>
            <span class="record-row__sub">{{ item.request_no }}</span>
          </div>
          <div class="record-row__detail">
            <span>变更前：{{ item.before_status_name || '-' }}</span>
            <span class="sep">·</span>
            <span>变更后：{{ item.after_status_name || '-' }}</span>
            <span class="sep">·</span>
            <span>生效：{{ item.effective_date || '-' }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(item.request_status)" size="small">{{ statusLabel(item.request_status) }}</el-tag>
            <el-button link type="primary" size="small" @click="openLedgerRequest(item)">查看详情</el-button>
          </div>
        </div>
      </div>
    </section>

    <section class="service-section">
      <div class="section-head">
        <div>
          <h3>学籍异动台账</h3>
          <p>来自专用业务表的学籍异动记录，支持按前后状态、原因和生效日期进行回看。</p>
        </div>
      </div>
      <el-table v-if="ledgerRecords.length" :data="ledgerRecords" size="small">
        <el-table-column prop="request_no" label="编号" min-width="150" />
        <el-table-column prop="before_status_name" label="变更前状态" min-width="140" />
        <el-table-column prop="after_status_name" label="变更后状态" min-width="140" />
        <el-table-column prop="effective_date" label="生效日期" min-width="130" />
        <el-table-column prop="reason" label="异动原因" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag :type="statusType(row.request_status)" size="small">{{ statusLabel(row.request_status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="update_time" label="更新时间" width="170" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openLedgerRequest(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无学籍异动台账" />
    </section>

    <section class="service-section">
      <div class="section-head">
        <div>
          <h3>我的异动申请</h3>
          <p>按事务申请维度查看休学、复学、转专业等办理记录。</p>
        </div>
      </div>
      <el-table v-if="academicRequests.length" :data="academicRequests" size="small">
        <el-table-column prop="requestNo" label="编号" min-width="150" />
        <el-table-column prop="templateName" label="业务" min-width="150" />
        <el-table-column label="目标状态" min-width="120">
          <template #default="{ row }">{{ row.targetStudentStatusName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="parsedFormData.effectiveDate" label="生效日期" min-width="130" />
        <el-table-column prop="parsedFormData.reason" label="异动原因" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag :type="statusType(row.requestStatus)" size="small">{{ statusLabel(row.requestStatus) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="submittedTime" label="提交时间" width="170" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$emit('view-request', row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无学籍异动申请记录" />
    </section>
  </StudentAffairPageFrame>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import StudentAffairPageFrame from './StudentAffairPageFrame.vue'
import {
  buildApplyPayload,
  resolveTermLabel,
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

const router = useRouter()

const {
  loading,
  detail,
  presentation,
  templates,
  requests,
  stats,
  specializedData,
  advisorInfo,
  studentStatus,
  guideSteps,
  findTemplate,
} = useStudentAffairDetail(computed(() => props.categoryCode || 'ACADEMIC_STATUS'))

const academicRequests = computed(() => requests.value.filter((item: any) => String(item.categoryCode) === 'ACADEMIC_STATUS'))
const ledgerRecords = computed(() => Array.isArray(specializedData.value?.records) && specializedData.value.records.length
  ? specializedData.value.records
  : academicRequests.value.map((item: any) => ({
      request_id: item.requestId,
      request_no: item.requestNo,
      before_status_name: item.currentStudentStatusName,
      after_status_name: item.targetStudentStatusName,
      effective_date: item.parsedFormData.effectiveDate,
      reason: item.parsedFormData.reason,
      request_status: item.requestStatus,
      update_time: item.submittedTime,
    })))
const pendingLedgerRecords = computed(() => Array.isArray(specializedData.value?.pendingRecords) && specializedData.value.pendingRecords.length
  ? specializedData.value.pendingRecords
  : ledgerRecords.value.filter((item: any) => item.request_status === 'PENDING'))
const latestApprovedRecord = computed(() => specializedData.value?.latestApprovedRecord || ledgerRecords.value.find((item: any) => item.request_status === 'APPROVED') || null)
const allTemplateCards = computed(() => templates.value.map((item: any, index: number) => {
  const rules = parseJsonObject(item.businessRulesJson)
  const tones = ['is-cyan', 'is-amber', 'is-rose', 'is-sky', 'is-violet', 'is-emerald', 'is-orange', 'is-indigo']
  const termLabel = rules.termId ? resolveTermLabel(rules.termId) : '未限定学期'
  const windowLabel = rules.openWindowEnabled
    ? `${rules.openStartTime || '未设置'} ~ ${rules.openEndTime || '未设置'}`
    : '长期开放'
  return {
    templateId: item.templateId,
    businessName: item.businessName || item.templateName,
    notice: rules.notice || item.remark || '暂无公告',
    termLabel,
    windowLabel,
    tone: tones[index % tones.length],
    rawTemplate: item,
  }
}))

const metrics = computed(() => ([
  { label: '休学申请', value: Number(specializedData.value?.suspendCount || academicRequests.value.filter((item: any) => item.templateCode === 'ACADEMIC_STATUS_SUSPEND').length), hint: '休学业务记录', tone: '' },
  { label: '复学申请', value: Number(specializedData.value?.resumeCount || academicRequests.value.filter((item: any) => item.templateCode === 'ACADEMIC_STATUS_RESUME').length), hint: '复学业务记录', tone: 'success' },
  { label: '转专业申请', value: Number(specializedData.value?.transferCount || academicRequests.value.filter((item: any) => item.templateCode === 'ACADEMIC_STATUS_TRANSFER_MAJOR').length), hint: '转专业业务记录', tone: 'warning' },
  { label: '待审核', value: Number(specializedData.value?.pendingCount || stats.value.pending || 0), hint: '审批流程处理中', tone: 'danger' },
]))

const statusCodeMap: Record<string, string> = {
  IN_SCHOOL: '在籍在校', ENROLLED: '在读', GRADUATED: '已毕业', SUSPENDED: '休学',
  DROPPED: '退学', WITHDRAWN: '退学', DEFERRED: '延期', RETAINED: '保留学籍',
  TRANSFERRED_MAJOR: '转专业', inschool: '在籍在校',
}

const summaryCards = computed(() => {
  const rawCode = studentStatus.value?.currentStatusCode || 'IN_SCHOOL'
  const statusBadge = statusCodeMap[rawCode] || rawCode
  const cards: any[] = [
    {
      icon: 'ri-profile-line',
      title: '学籍状态',
      value: studentStatus.value?.currentStatusName || '在籍在校',
      badge: statusBadge,
      lines: [
        studentStatus.value?.updateTime ? `更新：${studentStatus.value.updateTime}` : '审批通过后自动更新',
      ],
    },
    {
      icon: latestApprovedRecord.value ? 'ri-exchange-line' : 'ri-file-unknow-line',
      title: '最近异动',
      value: latestApprovedRecord.value?.after_status_name || '暂无记录',
      tone: latestApprovedRecord.value ? 'info' : '',
      lines: latestApprovedRecord.value
        ? [`${latestApprovedRecord.value.before_status_name || '-'} → ${latestApprovedRecord.value.after_status_name || '-'}`]
        : ['暂无已完成异动'],
    },
    {
      icon: Number(specializedData.value?.pendingCount || stats.value.pending || 0) ? 'ri-time-line' : 'ri-checkbox-circle-line',
      title: '处理中',
      value: Number(specializedData.value?.pendingCount || stats.value.pending || 0),
      tone: Number(specializedData.value?.pendingCount || stats.value.pending || 0) ? 'warning' : 'success',
      lines: advisorInfo.value
        ? [`辅导员：${advisorInfo.value.advisorName}`]
        : ['辅导员审核 → 教务审核'],
    },
  ]
  return cards.map((item) => ({
    ...item,
    lines: (item.lines || []).filter(Boolean),
  }))
})

const actions = computed(() => ([] as any[]))

function handleAction(action: any) {
  if (!action?.template) return
  emit('apply', buildApplyPayload(action))
}

function openLedgerRequest(row: any) {
  const matched = academicRequests.value.find((item: any) => Number(item.requestId) === Number(row.request_id))
  emit('view-request', matched || { requestId: row.request_id })
}

function parseJsonObject(value?: string) {
  try {
    return JSON.parse(value || '{}')
  } catch {
    return {}
  }
}
</script>

<style scoped>
@import './student-affair-shared.css';

/* page-specific: left color border for window rows */
.window-row.is-cyan    { border-left: 3px solid #0ea5e9; }
.window-row.is-amber   { border-left: 3px solid #f59e0b; }
.window-row.is-rose    { border-left: 3px solid #f43f5e; }
.window-row.is-sky     { border-left: 3px solid #0284c7; }
.window-row.is-violet  { border-left: 3px solid #8b5cf6; }
.window-row.is-emerald { border-left: 3px solid #10b981; }
.window-row.is-orange  { border-left: 3px solid #f97316; }
.window-row.is-indigo  { border-left: 3px solid #6366f1; }
</style>
