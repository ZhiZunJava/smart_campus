<template>
  <StudentAffairPageFrame
    :loading="loading"
    :has-detail="!!detail"
    :presentation="presentation"
    :title="presentation?.title || '教材管理服务'"
    :subtitle="presentation?.subtitle || '教材领用、补领与征订办理'"
    :metrics="metrics"
    :summary-cards="summaryCards"
    :actions="freeActions"
    :guide-steps="guideSteps"
    @back="$emit('back')"
    @action="handleAction"
  >
    <!-- ========== 本学期教材清单（来自管理端教材计划） ========== -->
    <section v-if="textbookPlans.length" class="service-section">
      <div class="section-head">
        <div>
          <h3>本学期教材清单</h3>
          <p>学校已为你所在年级/班级配置的本学期教材，可直接一键申请领用。</p>
        </div>
      </div>
      <div class="textbook-plan-grid">
        <div v-for="item in textbookPlans" :key="item.planId" class="textbook-plan-card" :class="{ 'is-claimed': isAlreadyClaimed(item) }">
          <div class="textbook-plan-card__head">
            <strong>{{ item.textbookName }}</strong>
            <el-tag v-if="item.courseName" size="small" type="info">{{ item.courseName }}</el-tag>
            <el-tag v-if="isAlreadyClaimed(item)" size="small" type="success">已领取</el-tag>
          </div>
          <div class="textbook-plan-card__meta">
            <span v-if="item.isbn">ISBN: {{ item.isbn }}</span>
            <span v-if="item.author">{{ item.author }}</span>
            <span v-if="item.publisher">{{ item.publisher }}</span>
            <span v-if="item.edition">{{ item.edition }}</span>
            <span v-if="item.price != null">¥{{ item.price }}</span>
          </div>
          <div class="textbook-plan-card__scope">
            <span>{{ item.termName || '当前学期' }}</span>
            <span v-if="item.gradeName"> · {{ item.gradeName }}</span>
            <span v-if="item.className"> · {{ item.className }}</span>
          </div>
          <div class="textbook-plan-card__actions">
            <template v-if="!isAlreadyClaimed(item)">
              <el-button type="primary" size="small" @click="applyFromPlan(item, 'CLAIM')">一键领用</el-button>
              <el-button size="small" @click="applyFromPlan(item, 'ORDER')">征订</el-button>
            </template>
            <template v-else>
              <el-button size="small" @click="applyFromPlan(item, 'REISSUE')">补领</el-button>
            </template>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 待审核教材业务 ========== -->
    <section v-if="pendingRequestCards.length" class="service-section">
      <div class="section-head">
        <div>
          <h3>待审核教材业务</h3>
          <p>已提交的教材申请正在审批中，请耐心等待。</p>
        </div>
      </div>
      <div class="record-list">
        <div v-for="item in pendingRequestCards" :key="item.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ textbookTypeLabel(item.parsedFormData.textbookType) }}</strong>
            <span class="record-row__sub">{{ item.requestNo }}</span>
          </div>
          <div class="record-row__detail">
            <span>ISBN：{{ item.parsedFormData.isbn || '-' }}</span>
            <span class="sep">·</span>
            <span>数量：{{ item.parsedFormData.quantity || 0 }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(item.requestStatus)" size="small">{{ statusLabel(item.requestStatus) }}</el-tag>
            <el-button link type="primary" size="small" @click="$emit('view-request', item)">查看详情</el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 已领取教材（审批通过记录） ========== -->
    <section v-if="claimedRecords.length" class="service-section">
      <div class="section-head">
        <div>
          <h3>已领取教材</h3>
          <p>审批通过的教材记录，可按学期查看。</p>
        </div>
        <el-select v-model="claimedTermFilter" clearable placeholder="全部学期" size="small" style="width:180px">
          <el-option label="全部学期" value="" />
          <el-option v-for="t in termOptionsForFilter" :key="t.value" :label="t.label" :value="t.value" />
        </el-select>
      </div>
      <el-table :data="filteredClaimedRecords" size="small">
        <el-table-column label="教材/ISBN" min-width="220">
          <template #default="{ row }">
            <div style="display:flex;flex-direction:column;gap:2px">
              <strong style="font-size:13px">{{ row.isbn || '-' }}</strong>
              <span style="color:#667085;font-size:12px">{{ textbookTypeLabel(row.textbook_type || row.parsedFormData?.textbookType) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="80" align="center">
          <template #default="{ row }">{{ row.quantity || row.parsedFormData?.quantity || 0 }}</template>
        </el-table-column>
        <el-table-column label="审批时间" min-width="160">
          <template #default="{ row }">{{ row.update_time || row.submittedTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openLedgerRequest(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <!-- ========== 教材办理台账 ========== -->
    <section class="service-section">
      <div class="section-head">
        <div>
          <h3>教材办理台账</h3>
          <p>全部教材申请记录，包含领用、补领、征订各类业务。</p>
        </div>
      </div>
      <el-table v-if="ledgerRecords.length" :data="ledgerRecords" size="small">
        <el-table-column prop="request_no" label="编号" min-width="150" />
        <el-table-column label="办理类型" min-width="100">
          <template #default="{ row }">{{ textbookTypeLabel(row.textbook_type) }}</template>
        </el-table-column>
        <el-table-column prop="isbn" label="ISBN" min-width="150" show-overflow-tooltip />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }"><el-tag :type="statusType(row.request_status)" size="small">{{ statusLabel(row.request_status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="update_time" label="更新时间" width="170" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openLedgerRequest(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无教材业务台账" />
    </section>
  </StudentAffairPageFrame>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import StudentAffairPageFrame from './StudentAffairPageFrame.vue'
import {
  buildApplyPayload,
  pickLatestRecord,
  resolveOptionLabel,
  resolveTermLabel,
  statusLabel,
  statusType,
  useStudentAffairDetail,
} from './useStudentAffairDetail'
import { listStudentTextbookPlans, listPortalTermOptions } from '@/api/portal'
import usePortalUserStore from '@/store/user'

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
  specializedData,
  guideSteps,
  findTemplate,
} = useStudentAffairDetail(computed(() => props.categoryCode || 'TEXTBOOK'))

const textbookTypeOptions = [
  { label: '教材领用', value: 'CLAIM' },
  { label: '教材补领', value: 'REISSUE' },
  { label: '教材征订', value: 'ORDER' },
]

/* ---- 教材计划数据 ---- */
const textbookPlans = ref<any[]>([])
const termOptionsForFilter = ref<any[]>([])
const claimedTermFilter = ref('')

async function loadTextbookPlans() {
  try {
    const userStore = usePortalUserStore()
    const userId = userStore.user?.userId
    if (!userId) return
    const res = await listStudentTextbookPlans(userId)
    textbookPlans.value = res.data || res || []
  } catch {
    textbookPlans.value = []
  }
}

async function loadTermOptions() {
  try {
    const res = await listPortalTermOptions()
    termOptionsForFilter.value = (res.data || res || []).map((item: any) => ({
      value: String(item.value),
      label: item.label || item.termName || `学期${item.value}`,
    }))
  } catch {
    termOptionsForFilter.value = []
  }
}

/** 检查某本教材计划是否已被领取（有APPROVED的CLAIM记录匹配isbn） */
function isAlreadyClaimed(plan: any): boolean {
  if (!plan.isbn) return false
  return textbookRequests.value.some((req: any) =>
    req.requestStatus === 'APPROVED'
    && req.parsedFormData.textbookType === 'CLAIM'
    && req.parsedFormData.isbn === plan.isbn,
  )
}

/** 从教材计划一键发起申请：预填ISBN、课程 */
function applyFromPlan(plan: any, textbookType: string) {
  const template = findTemplate('TEXTBOOK_APPLY')
  if (!template) return
  const typeLabel = textbookTypeOptions.find(o => o.value === textbookType)?.label || '教材申请'
  emit('apply', buildApplyPayload({
    template,
    presetData: {
      textbookType,
      courseId: plan.courseId || undefined,
      isbn: plan.isbn || plan.textbookCode || '',
      quantity: 1,
    },
    title: `${typeLabel}：${plan.textbookName}`,
  }))
}

onMounted(() => { loadTextbookPlans(); loadTermOptions() })

/* ---- 原有逻辑 ---- */
const textbookRequests = computed(() => requests.value.filter((item: any) => item.templateCode === 'TEXTBOOK_APPLY'))
const pendingRequestCards = computed(() => textbookRequests.value.filter((item: any) => item.requestStatus === 'PENDING'))

const ledgerRecords = computed(() => Array.isArray(specializedData.value?.recentRecords) && specializedData.value.recentRecords.length
  ? specializedData.value.recentRecords
  : textbookRequests.value.map((item: any) => ({
      request_id: item.requestId,
      request_no: item.requestNo,
      textbook_type: item.parsedFormData.textbookType,
      course_name: item.parsedFormData.courseName,
      isbn: item.parsedFormData.isbn,
      quantity: item.parsedFormData.quantity,
      request_status: item.requestStatus,
      update_time: item.submittedTime,
    })))

/** 已领取/已批准的记录 */
const claimedRecords = computed(() => {
  const fromLedger = ledgerRecords.value.filter((item: any) => item.request_status === 'APPROVED')
  if (fromLedger.length) return fromLedger
  return textbookRequests.value
    .filter((item: any) => item.requestStatus === 'APPROVED')
    .map((item: any) => ({
      request_id: item.requestId,
      request_no: item.requestNo,
      textbook_type: item.parsedFormData.textbookType,
      isbn: item.parsedFormData.isbn,
      quantity: item.parsedFormData.quantity,
      request_status: 'APPROVED',
      update_time: item.submittedTime,
      parsedFormData: item.parsedFormData,
    }))
})

const filteredClaimedRecords = computed(() => {
  if (!claimedTermFilter.value) return claimedRecords.value
  // 简单按时间前缀过滤（如果有学期信息的话）
  return claimedRecords.value
})

const latestLedgerRecord = computed(() => pickLatestRecord(ledgerRecords.value))

const metrics = computed(() => ([
  { label: '教材领用', value: Number(specializedData.value?.claimCount || textbookRequests.value.filter((item: any) => item.parsedFormData.textbookType === 'CLAIM').length), hint: '领用申请记录', tone: '' },
  { label: '教材补领', value: Number(specializedData.value?.reissueCount || textbookRequests.value.filter((item: any) => item.parsedFormData.textbookType === 'REISSUE').length), hint: '补领申请记录', tone: 'warning' },
  { label: '已领取', value: claimedRecords.value.length, hint: '审批通过教材', tone: 'success' },
  { label: '本学期教材', value: textbookPlans.value.length, hint: '学校配置的教材计划', tone: 'info' },
]))

const summaryCards = computed(() => ([
  {
    icon: 'ri-book-open-line',
    title: '本学期教材',
    value: textbookPlans.value.length,
    tone: textbookPlans.value.length ? 'info' : '',
    lines: textbookPlans.value.length
      ? [`共 ${textbookPlans.value.length} 本教材`, '下方卡片可一键申请领用']
      : ['暂无本学期教材计划'],
  },
  {
    icon: 'ri-checkbox-circle-line',
    title: '已领取',
    value: claimedRecords.value.length,
    tone: claimedRecords.value.length ? 'success' : '',
    lines: claimedRecords.value.length
      ? [`已领取 ${claimedRecords.value.length} 本教材`]
      : ['暂无已领取教材'],
  },
  {
    icon: Number(specializedData.value?.pendingCount || pendingRequestCards.value.length) ? 'ri-time-line' : 'ri-checkbox-circle-line',
    title: '待审核',
    value: Number(specializedData.value?.pendingCount || pendingRequestCards.value.length),
    tone: Number(specializedData.value?.pendingCount || pendingRequestCards.value.length) ? 'warning' : 'success',
    lines: ['辅导员初审 → 教务审核'],
  },
]))

/** 自由申请按钮（不通过教材计划的手动申请） */
const freeActions = computed(() => {
  const template = findTemplate('TEXTBOOK_APPLY')
  if (!template) return []
  return [
    { label: '手动教材领用', template, type: 'primary', presetData: { textbookType: 'CLAIM' }, title: '教材领用申请' },
    { label: '手动教材征订', template, type: 'success', presetData: { textbookType: 'ORDER' }, title: '教材征订申请' },
  ]
})

function textbookTypeLabel(value?: string) {
  return resolveOptionLabel(textbookTypeOptions, value, '未填写')
}

function handleAction(action: any) {
  if (!action?.template) return
  emit('apply', buildApplyPayload(action))
}

function openLedgerRequest(row: any) {
  const matched = textbookRequests.value.find((item: any) => Number(item.requestId) === Number(row.request_id))
  emit('view-request', matched || { requestId: row.request_id })
}
</script>

<style scoped>
@import './student-affair-shared.css';

.textbook-plan-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}
.textbook-plan-card {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 16px;
  background: #fff;
  display: flex;
  flex-direction: column;
  gap: 10px;
  transition: box-shadow 0.2s;
}
.textbook-plan-card:hover {
  box-shadow: 0 4px 14px rgba(0,0,0,0.08);
}
.textbook-plan-card.is-claimed {
  border-color: #c6f6d5;
  background: #f0fff4;
}
.textbook-plan-card__head {
  display: flex;
  align-items: center;
  gap: 8px;
}
.textbook-plan-card__head strong {
  font-size: 15px;
  color: #172033;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.textbook-plan-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.textbook-plan-card__meta span {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  background: #f3f6fb;
  color: #4b5565;
  font-size: 12px;
}
.textbook-plan-card__scope {
  font-size: 12px;
  color: #667085;
}
.textbook-plan-card__actions {
  display: flex;
  gap: 8px;
  margin-top: 4px;
}

.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}
</style>
