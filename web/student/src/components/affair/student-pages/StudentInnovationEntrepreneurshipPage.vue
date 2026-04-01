<template>
  <StudentAffairPageFrame
    :loading="loading"
    :has-detail="!!detail"
    :presentation="presentation"
    :title="presentation?.title || '创新创业服务'"
    :subtitle="presentation?.subtitle || '创新创业项目申报、团队和进展管理'"
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
        <p>创新创业项目按学期开放申报，申报范围和时段由老师或管理员配置。</p>
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
        <h3>我的创新创业记录</h3>
        <p>查看项目名称、项目类型、团队成员和进展阶段。</p>
      </div>
      <div v-if="innovationRecords.length" class="record-list">
        <div v-for="item in innovationRecords" :key="item.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ item.projectName }}</strong>
            <span class="record-row__sub">{{ item.projectTypeLabel }}</span>
          </div>
          <div class="record-row__detail">
            <span v-if="item.teamMembers">团队成员：{{ item.teamMembers }}</span>
            <span v-if="item.stage"><span class="sep">·</span>阶段：{{ item.stage }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(item.requestStatus)" size="small">{{ statusLabel(item.requestStatus) }}</el-tag>
            <span class="record-row__time">{{ item.submittedTime || '-' }}</span>
            <el-button link type="primary" size="small" @click="openRecordDetail(item)">查看详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无创新创业记录" />
    </section>
  </StudentAffairPageFrame>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import StudentAffairPageFrame from './StudentAffairPageFrame.vue'
import { buildApplyPayload, buildWindowCards, resolveOptionLabel, statusLabel, statusType, useStudentAffairDetail } from './useStudentAffairDetail'

const props = defineProps<{ portalRole: 'student'; categoryCode: string }>()
const emit = defineEmits<{ back: []; apply: [payload: any]; 'view-request': [request: any] }>()

const { loading, detail, presentation, templates, requests, stats, guideSteps } = useStudentAffairDetail(computed(() => props.categoryCode || 'INNOVATION_ENTREPRENEURSHIP'))

const projectTypeOptions = [
  { label: '创新训练', value: 'INNOVATION' },
  { label: '创业训练', value: 'ENTREPRENEURSHIP_TRAINING' },
  { label: '创业实践', value: 'ENTREPRENEURSHIP_PRACTICE' },
]

const windowCards = computed(() => buildWindowCards(templates.value))
const allWindowsClosed = computed(() => windowCards.value.length > 0 && windowCards.value.every((item: any) => !item.open))

const innovationRecords = computed(() => requests.value.filter((item: any) => item.categoryCode === 'INNOVATION_ENTREPRENEURSHIP').map((item: any) => ({
  requestId: item.requestId,
  projectName: item.parsedFormData.projectName || '创新创业项目',
  projectTypeLabel: resolveOptionLabel(projectTypeOptions, item.parsedFormData.projectType, '创新创业项目'),
  teamMembers: item.parsedFormData.teamMembers,
  stage: item.parsedFormData.stage,
  requestStatus: item.requestStatus,
  submittedTime: item.submittedTime,
})))

const metrics = computed(() => ([
  { label: '累计申报', value: innovationRecords.value.length, hint: '创新创业申报记录', tone: '' },
  { label: '已通过', value: innovationRecords.value.filter((item: any) => item.requestStatus === 'APPROVED').length, hint: '已通过审核', tone: 'success' },
  { label: '待审核', value: Number(stats.value.pending || 0), hint: '等待教师或辅导员审核', tone: 'warning' },
  { label: '开放时段', value: windowCards.value.filter((item: any) => item.open).length, hint: '当前可申报时段', tone: 'info' },
]))

const summaryCards = computed(() => ([
  {
    icon: templates.value.length ? 'ri-shield-check-line' : 'ri-close-circle-line',
    title: '申请资格',
    value: templates.value.length ? '符合条件' : '暂无资格',
    tone: templates.value.length ? 'success' : 'warning',
    lines: [templates.value.length ? '你在创新创业申报范围内' : '不在申请范围，请联系辅导员'],
  },
]))

const windowTemplateIds = computed(() => new Set(windowCards.value.map((c: any) => c.templateId)))
const actions = computed(() => {
  return templates.value
    .filter((t: any) => !windowTemplateIds.value.has(t.templateId))
    .map((t: any) => ({ label: '发起创新创业申报', template: t, type: 'primary' as const }))
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
