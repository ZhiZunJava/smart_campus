<template>
  <StudentAffairPageFrame
    :loading="loading || povertyLoading"
    :has-detail="!!detail"
    :presentation="presentation"
    :title="presentation?.title || '助学金服务'"
    :subtitle="presentation?.subtitle || '助学金申请、困难等级联动与资助进度查看'"
    :metrics="metrics"
    :summary-cards="summaryCards"
    :actions="actions"
    :guide-steps="guideSteps"
    @back="$emit('back')"
    @action="handleAction"
  >
    <section class="service-section">
      <div class="section-head">
        <h3>前置条件</h3>
        <p>助学金申请默认依赖贫困认定结果，系统会提示当前学期的认定状态。</p>
      </div>
      <article class="prerequisite-card" :class="{ 'is-ready': povertyApproved }">
        <strong>{{ povertyApproved ? '已完成贫困认定，可申请助学金' : '尚未完成贫困认定' }}</strong>
        <p>当前认定结果：{{ povertyLevelLabel }}</p>
        <p>最近认定时间：{{ povertyApprovedRecord?.submittedTime || '--' }}</p>
      </article>
    </section>

    <section class="service-section">
      <div class="section-head">
        <h3>申报时段</h3>
        <p>以下为当前助学金模板的开放申报时段，仅在开放期内可发起申请。</p>
      </div>
      <div v-if="windowCards.length" class="window-list">
        <div v-for="card in windowCards" :key="card.templateId" class="window-row">
          <div class="window-row__info">
            <strong>{{ card.title }}</strong>
            <div class="window-row__meta">
              <span>{{ card.termLabel }}</span>
              <span class="sep">·</span>
              <span>{{ card.windowLabel }}</span>
            </div>
            <p class="window-row__notice">{{ card.notice }}</p>
          </div>
          <div class="window-row__action">
            <el-tag :type="card.open ? 'success' : 'info'" size="small">{{ card.open ? '开放中' : '未开放' }}</el-tag>
          </div>
        </div>
      </div>
      <div v-else class="no-data-hint">暂无申报时段信息</div>
    </section>

    <section class="service-section">
      <div class="section-head">
        <h3>助学金申请记录</h3>
        <p>查看学期、资助类型、困难等级和审核结果。</p>
      </div>
      <div v-if="grantRecords.length" class="record-list">
        <div v-for="item in grantRecords" :key="item.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ item.grantType }}</strong>
            <span class="record-row__sub">{{ item.termLabel }}</span>
          </div>
          <div class="record-row__detail">
            <span>困难等级：{{ item.hardshipLevel }}</span>
            <span class="sep">·</span>
            <span>家庭收入：{{ item.familyIncome }}</span>
            <span v-if="item.reason" class="sep">·</span>
            <span v-if="item.reason">{{ item.reason }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(item.requestStatus)" size="small">{{ statusLabel(item.requestStatus) }}</el-tag>
            <span class="record-row__time">{{ item.submittedTime || '-' }}</span>
            <el-button link type="primary" size="small" @click="openRecordDetail(item)">查看详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无助学金申请记录" />
    </section>
  </StudentAffairPageFrame>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getPortalAffairCategoryDetail } from '@/api/portal'
import StudentAffairPageFrame from './StudentAffairPageFrame.vue'
import { buildApplyPayload, buildWindowCards, resolveOptionLabel, statusLabel, statusType, useStudentAffairDetail } from './useStudentAffairDetail'

const props = defineProps<{ portalRole: 'student'; categoryCode: string }>()
const emit = defineEmits<{ back: []; apply: [payload: any]; 'view-request': [request: any] }>()

const { loading, detail, presentation, templates, requests, stats, guideSteps } = useStudentAffairDetail(computed(() => props.categoryCode || 'GRANT'))
const povertyDetail = ref<any>(null)
const povertyLoading = ref(false)

const grantTypeOptions = [
  { label: '国家助学金', value: 'NATIONAL' },
  { label: '校级助学金', value: 'SCHOOL' },
]
const hardshipLevelOptions = [
  { label: '特别困难', value: 'LEVEL_A' },
  { label: '困难', value: 'LEVEL_B' },
  { label: '一般困难', value: 'LEVEL_C' },
]

const windowCards = computed(() => buildWindowCards(templates.value))
const hasOpenWindow = computed(() => windowCards.value.some((card: any) => card.open))

const grantRecords = computed(() => requests.value.filter((item: any) => item.categoryCode === 'GRANT').map((item: any) => ({
  requestId: item.requestId,
  grantType: resolveOptionLabel(grantTypeOptions, item.parsedFormData.grantType, '助学金申请'),
  termLabel: item.parsedFormData.termId ? `学期 ${item.parsedFormData.termId}` : '未绑定学期',
  hardshipLevel: resolveOptionLabel(hardshipLevelOptions, item.parsedFormData.hardshipLevel, '未填写'),
  familyIncome: item.parsedFormData.familyIncome ? `${item.parsedFormData.familyIncome} 元` : '--',
  reason: item.parsedFormData.reason,
  requestStatus: item.requestStatus,
  submittedTime: item.submittedTime,
})))

const povertyApprovedRecord = computed(() => {
  const list = povertyDetail.value?.myRequests || []
  return list.filter((item: any) => item.requestStatus === 'APPROVED')
    .map((item: any) => ({
      hardshipLevel: item.parsedFormData?.hardshipLevel || item.formData?.hardshipLevel,
      submittedTime: item.submittedTime,
    }))[0] || null
})
const povertyApproved = computed(() => !!povertyApprovedRecord.value)
const povertyLevelLabel = computed(() => resolveOptionLabel(hardshipLevelOptions, povertyApprovedRecord.value?.hardshipLevel, '未认定'))

const metrics = computed(() => ([
  { label: '累计申请', value: grantRecords.value.length, hint: '助学金申请记录', tone: '' },
  { label: '已通过', value: grantRecords.value.filter((item: any) => item.requestStatus === 'APPROVED').length, hint: '资助审核已通过', tone: 'success' },
  { label: '待审核', value: Number(stats.value.pending || 0), hint: '等待资助中心审核', tone: 'warning' },
  { label: '认定状态', value: povertyApproved.value ? '已完成' : '未完成', hint: '贫困认定前置条件', tone: povertyApproved.value ? 'info' : 'danger' },
]))

const summaryCards = computed(() => ([
  {
    icon: povertyApproved.value ? 'ri-user-heart-line' : 'ri-error-warning-line',
    title: '贫困认定',
    value: povertyApproved.value ? '已通过' : '未完成',
    tone: povertyApproved.value ? 'success' : 'warning',
    lines: [`困难等级：${povertyLevelLabel.value}`],
  },
]))

const windowTemplateIds = computed(() => new Set(windowCards.value.map((c: any) => c.templateId)))
const actions = computed(() => {
  // 有窗口期的模板由「申报时段」卡片承载，快捷办理只显示无窗口期的模板
  const nonWindowTemplates = templates.value.filter((t: any) => !windowTemplateIds.value.has(t.templateId))
  if (!nonWindowTemplates.length) return []
  const template = nonWindowTemplates[0]
  const canApply = povertyApproved.value
  const label = !povertyApproved.value ? '需先完成贫困认定' : '发起助学金申请'
  return [{
    label,
    template,
    type: 'primary',
    disabled: !canApply,
  }]
})

async function loadPovertyDetail() {
  povertyLoading.value = true
  try {
    const res = await getPortalAffairCategoryDetail('student', 'POVERTY_IDENTIFICATION')
    const payload = res.data || res
    povertyDetail.value = {
      ...payload,
      myRequests: (payload?.myRequests || []).map((item: any) => ({
        ...item,
        parsedFormData: (() => {
          try { return JSON.parse(item.formDataJson || '{}') } catch { return {} }
        })(),
      })),
    }
  } finally {
    povertyLoading.value = false
  }
}

function handleAction(action: any) {
  if (!action?.template || action.disabled) return
  emit('apply', buildApplyPayload(action))
}

function openRecordDetail(row: any) {
  const matched = requests.value.find((item: any) => Number(item.requestId) === Number(row.requestId))
  emit('view-request', matched || { requestId: row.requestId })
}

onMounted(loadPovertyDetail)
</script>

<style scoped>
@import './student-affair-shared.css';

.prerequisite-card { padding: 20px; border-radius: 18px; border: 1px solid #fed7aa; background: #fff7ed; }
.prerequisite-card.is-ready { border-color: #bbf7d0; background: #f0fdf4; }
.prerequisite-card strong { color: #0f172a; font-size: 18px; }
.prerequisite-card p { margin: 10px 0 0; color: #667085; }

@media (max-width: 768px) {
  .prerequisite-card { padding: 16px; border-radius: 14px; }
}

@media (max-width: 640px) {
  .prerequisite-card { padding: 14px; border-radius: 10px; }
  .prerequisite-card strong { font-size: 16px; }
  .prerequisite-card p { font-size: 13px; }
}
</style>
