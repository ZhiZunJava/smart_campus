<template>
  <StudentAffairPageFrame
    :loading="loading"
    :has-detail="!!detail"
    :presentation="presentation"
    :title="presentation?.title || '助学贷款服务'"
    :subtitle="presentation?.subtitle || '助学贷款申请、续贷和还款进度查看'"
    :metrics="metrics"
    :summary-cards="summaryCards"
    :actions="actions"
    :guide-steps="guideSteps"
    @back="$emit('back')"
    @action="handleAction"
  >
    <section v-if="windowCards.length" class="service-section">
      <div class="section-head">
        <h3>申报时段</h3>
        <p>助学贷款按学期开放申报，申报范围和时段由老师或管理员配置。</p>
      </div>
      <div v-if="allWindowsClosed" class="no-data-hint">当前没有开放的申报时段，请关注后续通知</div>
      <div v-else class="window-list">
        <div v-for="item in windowCards" :key="item.templateId" class="window-row">
          <div class="window-row__info">
            <strong>{{ item.title }}</strong>
            <div class="window-row__meta">
              <span>{{ item.termLabel }}</span>
              <span class="sep">·</span>
              <span>{{ item.windowLabel }}</span>
            </div>
            <p v-if="item.notice" class="window-row__notice">{{ item.notice }}</p>
          </div>
          <div class="window-row__action">
            <el-tag :type="item.open ? 'success' : 'info'" size="small">{{ item.open ? '申报中' : '暂未开放' }}</el-tag>
            <el-button type="primary" size="small" :disabled="!item.open" @click="handleAction({ template: item.template, type: 'primary' })">立即申请</el-button>
          </div>
        </div>
      </div>
    </section>

    <section class="service-section">
      <div class="section-head">
        <h3>我的助学贷款记录</h3>
        <p>查看贷款类型、贷款金额、贷款年份和经办银行。</p>
      </div>
      <div v-if="loanRecords.length" class="record-list">
        <div v-for="item in loanRecords" :key="item.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ item.loanTypeLabel }}</strong>
            <span class="record-row__sub">{{ item.loanYear ? `${item.loanYear}年` : '助学贷款' }}</span>
          </div>
          <div class="record-row__detail">
            <span v-if="item.loanAmount">金额：{{ item.loanAmount }}元</span>
            <span v-if="item.bankName"><span class="sep">·</span>银行：{{ item.bankName }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(item.requestStatus)" size="small">{{ statusLabel(item.requestStatus) }}</el-tag>
            <span class="record-row__time">{{ item.submittedTime || '-' }}</span>
            <el-button link type="primary" size="small" @click="openRecordDetail(item)">查看详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无助学贷款记录" />
    </section>
  </StudentAffairPageFrame>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import StudentAffairPageFrame from './StudentAffairPageFrame.vue'
import { buildApplyPayload, buildWindowCards, resolveOptionLabel, statusLabel, statusType, useStudentAffairDetail } from './useStudentAffairDetail'

const props = defineProps<{ portalRole: 'student'; categoryCode: string }>()
const emit = defineEmits<{ back: []; apply: [payload: any]; 'view-request': [request: any] }>()

const { loading, detail, presentation, templates, requests, stats, guideSteps } = useStudentAffairDetail(computed(() => props.categoryCode || 'LOAN'))

const loanTypeOptions = [
  { label: '生源地贷款', value: 'ORIGIN' },
  { label: '校园地贷款', value: 'CAMPUS' },
]

const windowCards = computed(() => buildWindowCards(templates.value))
const allWindowsClosed = computed(() => windowCards.value.length > 0 && windowCards.value.every((item: any) => !item.open))

const loanRecords = computed(() => requests.value.filter((item: any) => item.categoryCode === 'LOAN').map((item: any) => ({
  requestId: item.requestId,
  loanTypeLabel: resolveOptionLabel(loanTypeOptions, item.parsedFormData.loanType, '助学贷款'),
  loanAmount: item.parsedFormData.loanAmount,
  loanYear: item.parsedFormData.loanYear,
  bankName: item.parsedFormData.bankName,
  requestStatus: item.requestStatus,
  submittedTime: item.submittedTime,
})))

const metrics = computed(() => ([
  { label: '累计申请', value: loanRecords.value.length, hint: '助学贷款申请记录', tone: '' },
  { label: '已通过', value: loanRecords.value.filter((item: any) => item.requestStatus === 'APPROVED').length, hint: '已通过审核', tone: 'success' },
  { label: '待审核', value: Number(stats.value.pending || 0), hint: '等待教师或辅导员审核', tone: 'warning' },
  { label: '开放时段', value: windowCards.value.filter((item: any) => item.open).length, hint: '当前可申报时段', tone: 'info' },
]))

const summaryCards = computed(() => ([
  {
    icon: templates.value.length ? 'ri-shield-check-line' : 'ri-close-circle-line',
    title: '申请资格',
    value: templates.value.length ? '符合条件' : '暂无资格',
    tone: templates.value.length ? 'success' : 'warning',
    lines: [templates.value.length ? '你在助学贷款申请范围内' : '不在申请范围，请联系辅导员'],
  },
]))

const windowTemplateIds = computed(() => new Set(windowCards.value.map((c: any) => c.templateId)))
const actions = computed(() => {
  return templates.value
    .filter((t: any) => !windowTemplateIds.value.has(t.templateId))
    .map((t: any) => ({ label: '发起助学贷款申请', template: t, type: 'primary' as const }))
})

function handleAction(action: any) {
  if (!action?.template) return
  emit('apply', buildApplyPayload(action))
}

function openRecordDetail(row: any) {
  const matched = requests.value.find((item: any) => Number(item.requestId) === Number(row.requestId))
  emit('view-request', matched || { requestId: row.requestId })
}
</script>

<style scoped>
@import './student-affair-shared.css';
</style>
