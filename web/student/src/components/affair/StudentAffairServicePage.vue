<template>
  <div class="special-page" :class="presentation?.accentClass || ''">
    <div class="back-nav">
      <el-button text bg size="small" @click="$emit('back')"><i class="ri-arrow-left-line" /> 返回事务总览</el-button>
    </div>

    <section v-if="detail" class="special-hero">
      <div class="special-hero__copy">
        <p class="special-hero__eyebrow">{{ presentation?.highlightLabel || '学生事务' }}</p>
        <h2>{{ moduleTitle }}</h2>
        <p>{{ moduleSubtitle }}</p>
      </div>
      <div class="special-hero__stats">
        <div v-for="item in moduleBoard.metrics" :key="item.label" class="stat-card" :class="item.className">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <small>{{ item.hint }}</small>
        </div>
      </div>
    </section>

    <section v-if="detail" class="summary-panels">
      <article class="summary-card">
        <h3>办理说明</h3>
        <p>{{ presentation?.intro || detail.category?.remark || '当前业务支持线上办理。' }}</p>
      </article>
      <article v-if="detail.advisorInfo" class="summary-card">
        <h3>当前辅导员</h3>
        <p><strong>{{ detail.advisorInfo.advisorName }}</strong></p>
        <p v-if="detail.advisorInfo.phonenumber">联系方式：{{ detail.advisorInfo.phonenumber }}</p>
        <p>绑定方式：{{ detail.advisorInfo.bindingMode === 'DIRECT' ? '显式绑定' : '班级回退' }}</p>
      </article>
      <article v-if="detail.studentStatus" class="summary-card">
        <h3>学籍状态</h3>
        <p><strong>{{ detail.studentStatus.currentStatusName }}</strong></p>
        <p>状态编码：{{ detail.studentStatus.currentStatusCode }}</p>
        <p v-if="detail.studentStatus.updateTime">更新时间：{{ detail.studentStatus.updateTime }}</p>
      </article>
    </section>

    <section v-if="moduleBoard.actions.length" class="special-section">
      <div class="section-head">
        <div>
          <h3>快捷办理</h3>
          <p>选择下方按钮快速发起办理。</p>
        </div>
      </div>
      <div class="action-row">
        <el-button
          v-for="action in moduleBoard.actions"
          :key="action.label"
          :type="action.type || 'primary'"
          plain
          @click="handleAction(action)"
        >
          {{ action.label }}
        </el-button>
      </div>
    </section>

    <section v-if="moduleBoard.pendingRecords.length" class="special-section">
      <div class="section-head">
        <div>
          <h3>{{ moduleBoard.pendingTitle }}</h3>
          <p>{{ moduleBoard.pendingDesc }}</p>
        </div>
      </div>
      <div class="pending-grid">
        <article v-for="record in moduleBoard.pendingRecords" :key="record.requestId" class="pending-card">
          <div class="pending-card__head">
            <strong>{{ record.templateName || record.title }}</strong>
            <el-tag :type="statusType(record.requestStatus)" size="small">{{ statusLabel(record.requestStatus) }}</el-tag>
          </div>
          <p>{{ record.summaryText || '暂无摘要' }}</p>
          <div class="pending-card__footer">
            <span>{{ record.requestNo }}</span>
            <el-button size="small" type="primary" @click="handlePendingAction(record)">{{ moduleBoard.pendingActionLabel }}</el-button>
          </div>
        </article>
      </div>
    </section>

    <section v-for="section in moduleBoard.sections" :key="section.title" class="special-section">
      <div class="section-head">
        <div>
          <h3>{{ section.title }}</h3>
          <p>{{ section.desc }}</p>
        </div>
      </div>
      <el-table v-if="section.records.length" :data="section.records" size="small">
        <el-table-column prop="requestNo" label="编号" min-width="150" />
        <el-table-column prop="templateName" label="服务" min-width="160" />
        <el-table-column prop="summaryText" label="摘要" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag :type="statusType(row.requestStatus)" size="small">{{ statusLabel(row.requestStatus) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="submittedTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$emit('view-request', row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else :description="section.emptyText" />
    </section>

    <section v-if="guideSteps.length" class="special-section">
      <div class="section-head">
        <div>
          <h3>办理指南</h3>
          <p>了解办理步骤，轻松完成申请。</p>
        </div>
      </div>
      <div class="guide-grid">
        <article v-for="(step, index) in guideSteps" :key="step.title" class="guide-card">
          <span class="guide-card__index">{{ String(index + 1).padStart(2, '0') }}</span>
          <strong>{{ step.title }}</strong>
          <p>{{ step.desc }}</p>
        </article>
      </div>
    </section>

    <el-empty v-if="!loading && !detail" description="加载失败或分类不存在" />
    <div v-if="loading" class="loading-center"><el-icon class="is-loading"><i class="ri-loader-4-line" /></el-icon></div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { getPortalAffairCategoryDetail } from '@/api/portal'
import { resolveAffairPresentation } from '@/utils/affairPresentation'
import { statusLabel, statusType } from './student-pages/useStudentAffairDetail'

const props = defineProps<{
  portalRole: 'student'
  categoryCode: string
}>()

const emit = defineEmits<{
  back: []
  apply: [payload: any]
  'view-request': [request: any]
}>()

const loading = ref(false)
const detail = ref<any>(null)
const presentation = computed(() => resolveAffairPresentation(props.categoryCode))
const templates = computed(() => detail.value?.templates || [])
const requests = computed(() => (detail.value?.myRequests || []).map((item: any) => ({
  ...item,
  parsedFormData: parseJson(item.formDataJson),
})))
const guideSteps = computed(() => presentation.value?.guideSteps || [])

const moduleTitle = computed(() => presentation.value?.title || detail.value?.category?.categoryName || '学生事务服务')
const moduleSubtitle = computed(() => presentation.value?.subtitle || detail.value?.category?.remark || '在线办理与进度跟踪')

const moduleBoard = computed(() => {
  switch (props.categoryCode) {
    case 'ASK_LEAVE':
      return buildAskLeaveBoard()
    case 'LEAVE_RETURN_SCHOOL':
      return buildLeaveReturnBoard()
    case 'TEXTBOOK':
      return buildTextbookBoard()
    case 'ACADEMIC_STATUS':
      return buildAcademicStatusBoard()
    default:
      return buildGenericBoard()
  }
})

async function loadDetail() {
  loading.value = true
  try {
    const res = await getPortalAffairCategoryDetail(props.portalRole, props.categoryCode)
    detail.value = res.data || res
  } finally {
    loading.value = false
  }
}

function parseJson(value?: string) {
  try {
    return JSON.parse(value || '{}')
  } catch {
    return {}
  }
}

function findTemplate(templateCode: string) {
  return templates.value.find((item: any) => item.templateCode === templateCode)
}

function filterRequestsByTemplate(...codes: string[]) {
  const codeSet = new Set(codes)
  return requests.value.filter((item: any) => codeSet.has(item.templateCode))
}

function makeAction(label: string, template: any, type = 'primary', presetData?: Record<string, any>, title?: string) {
  return { label, template, type, presetData, title }
}

function handleAction(action: any) {
  if (!action?.template) return
  emit('apply', {
    template: action.template,
    presetData: action.presetData || null,
    title: action.title || '',
  })
}

function handlePendingAction(record: any) {
  if (props.categoryCode === 'ASK_LEAVE') {
    const cancelTemplate = findTemplate('ASK_LEAVE_CANCEL')
    if (!cancelTemplate) return
    handleAction(makeAction('销假登记', cancelTemplate, 'warning', { leaveRequestNo: record.requestNo }, `销假登记 - ${record.requestNo}`))
    return
  }
  if (props.categoryCode === 'LEAVE_RETURN_SCHOOL') {
    const returnTemplate = findTemplate('LEAVE_RETURN_CONFIRM')
    if (!returnTemplate) return
    handleAction(makeAction('返校登记', returnTemplate, 'success', { leaveRequestNo: record.requestNo }, `返校登记 - ${record.requestNo}`))
  }
}

function buildAskLeaveBoard() {
  const leaveRecords = filterRequestsByTemplate('ASK_LEAVE_STANDARD')
  const cancelRecords = filterRequestsByTemplate('ASK_LEAVE_CANCEL')
  const canceledRequestNos = new Set(cancelRecords.map((item: any) => item.parsedFormData.leaveRequestNo).filter(Boolean))
  const pendingCancel = leaveRecords.filter((item: any) => item.requestStatus === 'APPROVED' && !canceledRequestNos.has(item.requestNo))
  return {
    metrics: [
      { label: '请假申请', value: leaveRecords.length, hint: '累计请假申请', className: '' },
      { label: '销假登记', value: cancelRecords.length, hint: '累计销假登记', className: 'is-success' },
      { label: '待销假', value: pendingCancel.length, hint: '已通过未销假', className: 'is-warning' },
      { label: '待审核', value: requests.value.filter((item: any) => item.requestStatus === 'PENDING').length, hint: '流程处理中', className: 'is-danger' },
    ],
    actions: [
      makeAction('发起请假', findTemplate('ASK_LEAVE_STANDARD'), 'primary'),
      makeAction('销假登记', findTemplate('ASK_LEAVE_CANCEL'), 'warning'),
    ].filter((item) => item.template),
    pendingTitle: '待销假事项',
    pendingDesc: '已通过的请假单建议返校后及时销假，保持考勤记录完整。',
    pendingActionLabel: '去销假',
    pendingRecords: pendingCancel,
    sections: [
      { title: '请假记录', desc: '所有请假申请记录', records: leaveRecords, emptyText: '暂无请假记录' },
      { title: '销假记录', desc: '已完成销假登记的记录', records: cancelRecords, emptyText: '暂无销假记录' },
    ],
  }
}

function buildLeaveReturnBoard() {
  const leaveRecords = filterRequestsByTemplate('LEAVE_RETURN_STANDARD')
  const returnRecords = filterRequestsByTemplate('LEAVE_RETURN_CONFIRM')
  const returnedRequestNos = new Set(returnRecords.map((item: any) => item.parsedFormData.leaveRequestNo).filter(Boolean))
  const waitingReturn = leaveRecords.filter((item: any) => item.requestStatus === 'APPROVED' && !returnedRequestNos.has(item.requestNo))
  return {
    metrics: [
      { label: '离校申请', value: leaveRecords.length, hint: '累计离校申请', className: '' },
      { label: '返校登记', value: returnRecords.length, hint: '累计返校登记', className: 'is-success' },
      { label: '待返校', value: waitingReturn.length, hint: '已通过离校未登记返校', className: 'is-warning' },
      { label: '待审核', value: requests.value.filter((item: any) => item.requestStatus === 'PENDING').length, hint: '流程处理中', className: 'is-danger' },
    ],
    actions: [
      makeAction('发起离校申请', findTemplate('LEAVE_RETURN_STANDARD'), 'primary'),
      makeAction('返校登记', findTemplate('LEAVE_RETURN_CONFIRM'), 'success'),
    ].filter((item) => item.template),
    pendingTitle: '待返校登记',
    pendingDesc: '已通过离校申请但还未登记返校的记录会显示在这里。',
    pendingActionLabel: '去登记',
    pendingRecords: waitingReturn,
    sections: [
      { title: '离校记录', desc: '所有离校申请记录', records: leaveRecords, emptyText: '暂无离校记录' },
      { title: '返校记录', desc: '返校登记记录', records: returnRecords, emptyText: '暂无返校登记记录' },
    ],
  }
}

function buildTextbookBoard() {
  const allRecords = filterRequestsByTemplate('TEXTBOOK_APPLY')
  const claimRecords = allRecords.filter((item: any) => item.parsedFormData.textbookType === 'CLAIM')
  const reissueRecords = allRecords.filter((item: any) => item.parsedFormData.textbookType === 'REISSUE')
  const orderRecords = allRecords.filter((item: any) => item.parsedFormData.textbookType === 'ORDER')
  const template = findTemplate('TEXTBOOK_APPLY')
  return {
    metrics: [
      { label: '教材领用', value: claimRecords.length, hint: '领用申请记录', className: '' },
      { label: '教材补领', value: reissueRecords.length, hint: '补领申请记录', className: 'is-warning' },
      { label: '教材征订', value: orderRecords.length, hint: '征订申请记录', className: 'is-info' },
      { label: '待审核', value: requests.value.filter((item: any) => item.requestStatus === 'PENDING').length, hint: '流程处理中', className: 'is-danger' },
    ],
    actions: template ? [
      makeAction('教材领用', template, 'primary', { textbookType: 'CLAIM' }, '教材领用申请'),
      makeAction('教材补领', template, 'warning', { textbookType: 'REISSUE' }, '教材补领申请'),
      makeAction('教材征订', template, 'success', { textbookType: 'ORDER' }, '教材征订申请'),
    ] : [],
    pendingTitle: '',
    pendingDesc: '',
    pendingActionLabel: '',
    pendingRecords: [],
    sections: [
      { title: '教材领用记录', desc: '教材首次领用申请记录', records: claimRecords, emptyText: '暂无教材领用记录' },
      { title: '教材补领记录', desc: '教材补领与缺书处理记录', records: reissueRecords, emptyText: '暂无教材补领记录' },
      { title: '教材征订记录', desc: '教材征订申请记录', records: orderRecords, emptyText: '暂无教材征订记录' },
    ],
  }
}

function buildAcademicStatusBoard() {
  const suspendRecords = filterRequestsByTemplate('ACADEMIC_STATUS_SUSPEND')
  const resumeRecords = filterRequestsByTemplate('ACADEMIC_STATUS_RESUME')
  const transferRecords = filterRequestsByTemplate('ACADEMIC_STATUS_TRANSFER_MAJOR')
  return {
    metrics: [
      { label: '休学申请', value: suspendRecords.length, hint: '休学业务记录', className: '' },
      { label: '复学申请', value: resumeRecords.length, hint: '复学业务记录', className: 'is-success' },
      { label: '转专业申请', value: transferRecords.length, hint: '转专业业务记录', className: 'is-warning' },
      { label: '待审核', value: requests.value.filter((item: any) => item.requestStatus === 'PENDING').length, hint: '流程处理中', className: 'is-danger' },
    ],
    actions: [
      makeAction('发起休学', findTemplate('ACADEMIC_STATUS_SUSPEND'), 'primary'),
      makeAction('发起复学', findTemplate('ACADEMIC_STATUS_RESUME'), 'success'),
      makeAction('发起转专业', findTemplate('ACADEMIC_STATUS_TRANSFER_MAJOR'), 'warning'),
    ].filter((item) => item.template),
    pendingTitle: '',
    pendingDesc: '',
    pendingActionLabel: '',
    pendingRecords: [],
    sections: [
      { title: '休学记录', desc: '休学相关申请记录', records: suspendRecords, emptyText: '暂无休学记录' },
      { title: '复学记录', desc: '复学相关申请记录', records: resumeRecords, emptyText: '暂无复学记录' },
      { title: '转专业记录', desc: '转专业申请记录', records: transferRecords, emptyText: '暂无转专业记录' },
    ],
  }
}

function buildGenericBoard() {
  return {
    metrics: [
      { label: '累计申请', value: requests.value.length, hint: '当前分类全部申请', className: '' },
      { label: '待审核', value: requests.value.filter((item: any) => item.requestStatus === 'PENDING').length, hint: '流程处理中', className: 'is-warning' },
      { label: '已通过', value: requests.value.filter((item: any) => item.requestStatus === 'APPROVED').length, hint: '已完成审批', className: 'is-success' },
      { label: '已驳回', value: requests.value.filter((item: any) => item.requestStatus === 'REJECTED').length, hint: '需要重新提交', className: 'is-danger' },
    ],
    actions: templates.value[0] ? [makeAction(presentation.value?.primaryActionLabel || '立即办理', templates.value[0], 'primary')] : [],
    pendingTitle: '',
    pendingDesc: '',
    pendingActionLabel: '',
    pendingRecords: [],
    sections: [
      { title: '申请记录', desc: '当前分类下的全部申请记录', records: requests.value, emptyText: presentation.value?.emptyHint || '暂无记录' },
    ],
  }
}

watch(() => props.categoryCode, () => {
  if (props.categoryCode) loadDetail()
}, { immediate: true })
</script>

<style scoped>
.special-page { display: grid; gap: 20px; }
.back-nav { margin-bottom: -8px; }

.special-hero { display: grid; grid-template-columns: minmax(0, 1.3fr) minmax(340px, 0.9fr); gap: 18px; padding: 28px; border-radius: 24px; background: #f8fafc; border: 1px solid #dbeafe; }
.special-hero__eyebrow { margin: 0 0 8px; color: #266fcb; font-size: 12px; letter-spacing: 0.14em; text-transform: uppercase; }
.special-hero__copy h2 { margin: 0 0 8px; font-size: 30px; color: #0f172a; }
.special-hero__copy p { margin: 0; color: #506173; line-height: 1.75; }
.special-hero__stats { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }

.summary-panels { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 14px; }
.summary-card,
.metric-card,
.pending-card,
.guide-card { padding: 18px; border-radius: 18px; border: 1px solid #e5eef8; background: #fff; }
.summary-card h3 { margin: 0 0 10px; font-size: 16px; color: #0f172a; }
.summary-card p { margin: 0; color: #667085; line-height: 1.8; }

.special-section { display: grid; gap: 14px; }
.section-head { display: flex; justify-content: space-between; align-items: end; gap: 12px; }
.section-head h3 { margin: 0; font-size: 18px; color: #0f172a; }
.section-head p { margin: 6px 0 0; color: #667085; }

.metric-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(170px, 1fr)); gap: 12px; }
.metric-card span,
.stat-card span { display: block; color: #64748b; font-size: 12px; }
.metric-card strong,
.stat-card strong { display: block; margin-top: 8px; font-size: 28px; color: #0f172a; }
.metric-card p,
.stat-card small { margin: 8px 0 0; color: #94a3b8; font-size: 12px; }
.metric-card.is-success strong,
.stat-card.is-success strong { color: #059669; }
.metric-card.is-warning strong,
.stat-card.is-warning strong { color: #d97706; }
.metric-card.is-danger strong,
.stat-card.is-danger strong { color: #dc2626; }
.metric-card.is-info strong,
.stat-card.is-info strong { color: #2563eb; }

.action-row { display: flex; gap: 10px; flex-wrap: wrap; }

.pending-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 14px; }
.pending-card__head { display: flex; justify-content: space-between; gap: 12px; align-items: flex-start; }
.pending-card p { margin: 10px 0; color: #667085; line-height: 1.7; min-height: 46px; }
.pending-card__footer { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.pending-card__footer span { color: #94a3b8; font-size: 12px; }

.guide-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 14px; }
.guide-card__index { display: inline-flex; padding: 4px 10px; border-radius: 999px; background: #eff6ff; color: #2563eb; font-size: 12px; margin-bottom: 10px; }
.guide-card strong { display: block; color: #0f172a; font-size: 16px; margin-bottom: 8px; }
.guide-card p { margin: 0; color: #667085; line-height: 1.75; }

.special-page.is-attendance .special-hero { background: #f0fdf4; border-left: 4px solid #059669; border-color: #bbf7d0; }
.special-page.is-funding .special-hero { background: #fff7ed; border-left: 4px solid #d97706; border-color: #fed7aa; }
.special-page.is-academic .special-hero { background: #eff6ff; border-left: 4px solid #2563eb; border-color: #bfdbfe; }
.special-page.is-growth .special-hero { background: #fdf4ff; border-left: 4px solid #7c3aed; border-color: #e9d5ff; }

.loading-center { display: flex; justify-content: center; padding: 40px; font-size: 28px; color: #94a3b8; }

@media (max-width: 960px) {
  .special-hero { grid-template-columns: 1fr; }
  .special-hero__stats { grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); }
}

@media (max-width: 640px) {
  .special-hero { padding: 20px; }
  .special-hero__stats { grid-template-columns: 1fr 1fr; }
  .summary-panels { grid-template-columns: 1fr; }
  .pending-grid { grid-template-columns: 1fr; }
  .guide-grid { grid-template-columns: 1fr; }
  .action-row { flex-direction: column; }
  .action-row .el-button { width: 100%; }
}

@media (prefers-reduced-motion: reduce) {
  .pending-card,
  .stat-card,
  .summary-card {
    transition: none !important;
  }
}
</style>
