<template>
  <StudentAffairPageFrame
    :loading="loading"
    :has-detail="!!detail"
    :presentation="presentation"
    :title="presentation?.title || '活动申请服务'"
    :subtitle="presentation?.subtitle || '校园活动报名申请、审核进度查看'"
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
        <p>活动按学期开放申报，申报范围和时段由老师或管理员配置。</p>
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
        <h3>我的活动申请</h3>
        <p>查看活动名称、活动类型、举办时间和审核状态。</p>
      </div>
      <div v-if="activityRecords.length" class="record-list">
        <div v-for="item in activityRecords" :key="item.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ item.activityName }}</strong>
            <span class="record-row__sub">{{ item.activityTypeLabel }}</span>
          </div>
          <div class="record-row__detail">
            <span v-if="item.activityDate">{{ item.activityDate }}</span>
            <span v-if="item.organizer"><span class="sep">·</span>主办方：{{ item.organizer }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(item.requestStatus)" size="small">{{ statusLabel(item.requestStatus) }}</el-tag>
            <span class="record-row__time">{{ item.submittedTime || '-' }}</span>
            <el-button link type="primary" size="small" @click="openRecordDetail(item)">查看详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无活动申请记录" />
    </section>
  </StudentAffairPageFrame>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import StudentAffairPageFrame from './StudentAffairPageFrame.vue'
import { buildApplyPayload, buildWindowCards, resolveOptionLabel, statusLabel, statusType, useStudentAffairDetail } from './useStudentAffairDetail'

const props = defineProps<{ portalRole: 'student'; categoryCode: string }>()
const emit = defineEmits<{ back: []; apply: [payload: any]; 'view-request': [request: any] }>()

const { loading, detail, presentation, templates, requests, stats, guideSteps } = useStudentAffairDetail(computed(() => props.categoryCode || 'ACTIVITY'))

const activityTypeOptions = [
  { label: '文体活动', value: 'SPORTS_CULTURE' },
  { label: '学术活动', value: 'ACADEMIC' },
  { label: '志愿服务', value: 'VOLUNTEER' },
  { label: '社团活动', value: 'CLUB' },
]

const windowCards = computed(() => buildWindowCards(templates.value))
const allWindowsClosed = computed(() => windowCards.value.length > 0 && windowCards.value.every((item: any) => !item.open))

const activityRecords = computed(() => requests.value.filter((item: any) => item.categoryCode === 'ACTIVITY').map((item: any) => ({
  requestId: item.requestId,
  activityName: item.parsedFormData.activityName || '活动申请',
  activityTypeLabel: resolveOptionLabel(activityTypeOptions, item.parsedFormData.activityType, '活动申请'),
  activityDate: item.parsedFormData.activityDate,
  organizer: item.parsedFormData.organizer,
  requestStatus: item.requestStatus,
  submittedTime: item.submittedTime,
})))

const metrics = computed(() => ([
  { label: '累计申请', value: activityRecords.value.length, hint: '活动申请记录', tone: '' },
  { label: '已通过', value: activityRecords.value.filter((item: any) => item.requestStatus === 'APPROVED').length, hint: '已通过审核', tone: 'success' },
  { label: '待审核', value: Number(stats.value.pending || 0), hint: '等待教师或辅导员审核', tone: 'warning' },
  { label: '开放时段', value: windowCards.value.filter((item: any) => item.open).length, hint: '当前可申报时段', tone: 'info' },
]))

const summaryCards = computed(() => ([
  {
    icon: templates.value.length ? 'ri-shield-check-line' : 'ri-close-circle-line',
    title: '申请资格',
    value: templates.value.length ? '符合条件' : '暂无资格',
    tone: templates.value.length ? 'success' : 'warning',
    lines: [templates.value.length ? '你在活动申请范围内' : '不在申请范围，请联系辅导员'],
  },
]))

const windowTemplateIds = computed(() => new Set(windowCards.value.map((c: any) => c.templateId)))
const actions = computed(() => {
  return templates.value
    .filter((t: any) => !windowTemplateIds.value.has(t.templateId))
    .map((t: any) => ({ label: '发起活动申请', template: t, type: 'primary' as const }))
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
