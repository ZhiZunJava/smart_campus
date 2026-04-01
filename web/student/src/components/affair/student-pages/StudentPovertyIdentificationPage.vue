<template>
  <StudentAffairPageFrame
    :loading="loading"
    :has-detail="!!detail"
    :presentation="presentation"
    :title="presentation?.title || '贫困认定服务'"
    :subtitle="presentation?.subtitle || '按学期开放申请，认定通过后形成困难等级档案'"
    :metrics="metrics"
    :summary-cards="summaryCards"
    :actions="actions"
    :guide-steps="guideSteps"
    @back="$emit('back')"
    @action="handleAction"
  >
    <!-- Level result: segmented bar instead of 3 separate cards -->
    <section class="service-section">
      <div class="section-head">
        <h3>困难等级结果</h3>
        <p>认定通过后会在这里显示当前学期的困难等级，便于后续助学金等资助业务联动。</p>
      </div>
      <div class="level-segment">
        <div
          v-for="level in levelCards"
          :key="level.value"
          class="level-segment__item"
          :class="{ 'is-active': currentApprovedLevel === level.value }"
        >
          <strong>{{ level.label }}</strong>
          <span class="level-segment__desc">{{ level.desc }}</span>
          <span v-if="currentApprovedLevel === level.value" class="level-segment__badge">当前认定等级</span>
        </div>
      </div>
      <p v-if="!currentApprovedLevel" class="level-empty-hint">暂未获得认定结果，请在下方申报时段中发起认定申请。</p>
    </section>

    <!-- Window list: compact rows instead of cards -->
    <section v-if="windowCards.length" class="service-section">
      <div class="section-head">
        <h3>申报时段</h3>
        <p>按学期开放申报，认定时段由辅导员或管理员配置。</p>
      </div>
      <div class="window-list">
        <div v-for="item in windowCards" :key="item.templateId" class="window-row">
          <div class="window-row__info">
            <strong>{{ item.title }}</strong>
            <div class="window-row__meta">
              <span>{{ item.termLabel }}</span>
              <span class="sep">·</span>
              <span>{{ item.windowLabel }}</span>
            </div>
            <p v-if="item.notice !== '暂无公告'" class="window-row__notice">{{ item.notice }}</p>
          </div>
          <div class="window-row__action">
            <el-tag :type="item.open ? 'success' : 'info'" size="small" effect="light" round>
              {{ item.open ? '申报中' : '暂未开放' }}
            </el-tag>
            <el-button type="primary" size="small" :disabled="!item.open" @click="handleAction({ template: item.template, type: 'primary' })">
              立即申请
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- Records: compact list rows instead of card grid -->
    <section class="service-section">
      <div class="section-head">
        <h3>认定记录</h3>
        <p>保留历次认定申请记录，按学期回看申请说明、困难等级和审核结果。</p>
      </div>
      <div v-if="povertyRecords.length" class="record-list">
        <div v-for="item in povertyRecords" :key="item.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ item.termLabel }}</strong>
            <span class="record-row__level">{{ hardshipLevelLabel(item.hardshipLevel) }}</span>
          </div>
          <div class="record-row__detail">
            <span>家庭收入 {{ item.familyIncome || '--' }}</span>
            <span class="sep">·</span>
            <span>家庭人口 {{ item.familyMembers || '--' }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(item.requestStatus)" size="small" effect="light">{{ statusLabel(item.requestStatus) }}</el-tag>
            <span class="record-row__time">{{ item.submittedTime || '-' }}</span>
            <el-button link type="primary" size="small" @click="openRecordDetail(item)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无贫困认定记录" />
    </section>
  </StudentAffairPageFrame>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import StudentAffairPageFrame from './StudentAffairPageFrame.vue'
import { buildApplyPayload, buildWindowCards, pickLatestRecord, resolveOptionLabel, statusLabel, statusType, useStudentAffairDetail } from './useStudentAffairDetail'

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
  templates,
  requests,
  stats,
  guideSteps,
} = useStudentAffairDetail(computed(() => props.categoryCode || 'POVERTY_IDENTIFICATION'))

const hardshipLevelOptions = [
  { label: '特别困难', value: 'LEVEL_A' },
  { label: '困难', value: 'LEVEL_B' },
  { label: '一般困难', value: 'LEVEL_C' },
]

const levelCards = [
  { value: 'LEVEL_A', label: '特别困难', desc: '家庭经济极度困难，资助优先级最高。' },
  { value: 'LEVEL_B', label: '困难', desc: '家庭经济存在明显困难，需持续跟踪资助。' },
  { value: 'LEVEL_C', label: '一般困难', desc: '家庭经济存在阶段性困难，可参与基础资助。' },
]

const povertyRequests = computed(() => requests.value.filter((item: any) => item.categoryCode === 'POVERTY_IDENTIFICATION'))
const povertyRecords = computed(() => povertyRequests.value.map((item: any) => ({
  requestId: item.requestId,
  requestNo: item.requestNo,
  requestStatus: item.requestStatus,
  termId: item.parsedFormData.termId,
  termLabel: item.parsedFormData.termId ? `学期 ${item.parsedFormData.termId}` : '未绑定学期',
  hardshipLevel: item.parsedFormData.hardshipLevel,
  familyIncome: item.parsedFormData.familyIncome ? `${item.parsedFormData.familyIncome} 元` : '--',
  familyMembers: item.parsedFormData.familyMembers,
  reason: item.parsedFormData.reason,
  submittedTime: item.submittedTime,
})))
const latestApprovedRecord = computed(() => pickLatestRecord(povertyRecords.value.filter((item: any) => item.requestStatus === 'APPROVED')))
const currentApprovedLevel = computed(() => latestApprovedRecord.value?.hardshipLevel || '')

const metrics = computed(() => ([
  { label: '累计申请', value: povertyRequests.value.length, hint: '历次认定申请', tone: '' },
  { label: '已通过', value: povertyRequests.value.filter((item: any) => item.requestStatus === 'APPROVED').length, hint: '形成认定结果', tone: 'success' },
  { label: '待审核', value: Number(stats.value.pending || 0), hint: '等待辅导员或资助中心审核', tone: 'warning' },
  { label: '当前档次', value: hardshipLevelLabel(currentApprovedLevel.value), hint: '最近一次通过的认定结果', tone: currentApprovedLevel.value ? 'info' : '' },
]))

const summaryCards = computed(() => ([
  {
    icon: currentApprovedLevel.value ? 'ri-shield-check-line' : 'ri-file-unknow-line',
    title: '认定结果',
    value: hardshipLevelLabel(currentApprovedLevel.value),
    tone: currentApprovedLevel.value ? 'success' : '',
    lines: latestApprovedRecord.value
      ? [`申请时间：${latestApprovedRecord.value.submittedTime || '--'}`]
      : ['暂无通过的认定记录'],
  },
  {
    icon: currentApprovedLevel.value ? 'ri-links-line' : 'ri-error-warning-line',
    title: '资助联动',
    value: currentApprovedLevel.value ? '可作为资助依据' : '需先完成认定',
    tone: currentApprovedLevel.value ? 'info' : 'warning',
    lines: ['助学金等可将认定结果作为前置条件'],
  },
]))

const windowCards = computed(() => buildWindowCards(templates.value))

const windowTemplateIds = computed(() => new Set(windowCards.value.map((c: any) => c.templateId)))
const actions = computed(() => {
  return templates.value
    .filter((t: any) => !windowTemplateIds.value.has(t.templateId))
    .map((t: any) => ({ label: '发起贫困认定', template: t, type: 'primary' as const }))
})

function hardshipLevelLabel(value?: string) {
  return resolveOptionLabel(hardshipLevelOptions, value, '未认定')
}

function handleAction(action: any) {
  if (!action?.template) return
  emit('apply', buildApplyPayload(action))
}

function openRecordDetail(row: any) {
  const matched = povertyRequests.value.find((item: any) => Number(item.requestId) === Number(row.requestId))
  emit('view-request', matched || { requestId: row.requestId })
}
</script>

<style scoped>
@import './student-affair-shared.css';

/* ── Level segment bar (page-specific) ── */
.level-segment {
  display: flex;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
  background: #f8fafc;
}

.level-segment__item {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px 18px;
  border-right: 1px solid #e2e8f0;
  transition: all 0.25s;
}
.level-segment__item:last-child { border-right: none; }

.level-segment__item strong {
  font-size: 15px;
  color: #334155;
}

.level-segment__desc {
  margin-top: 4px;
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.5;
}

.level-segment__badge {
  display: inline-flex;
  align-self: flex-start;
  margin-top: 10px;
  padding: 2px 10px;
  border-radius: 10px;
  background: var(--el-color-success);
  color: #fff;
  font-size: 11px;
  font-weight: 500;
}

.level-segment__item.is-active {
  background: #f8fafc;
  border-color: var(--el-color-success-light-5);
  border-left: 4px solid var(--el-color-success);
}

.level-segment__item.is-active strong {
  color: var(--el-color-success);
}

.level-empty-hint {
  margin: 0;
  color: #94a3b8;
  font-size: 13px;
}

@media (max-width: 768px) {
  .level-segment { flex-direction: column; }
  .level-segment__item { border-right: none; border-bottom: 1px solid #e2e8f0; }
  .level-segment__item:last-child { border-bottom: none; }
}
</style>
