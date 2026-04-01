<template>
  <StudentAffairPageFrame
    :loading="loading"
    :has-detail="!!detail"
    :presentation="presentation"
    :title="presentation?.title || '奖学金服务'"
    :subtitle="presentation?.subtitle || '在这里申请奖学金，随时查看审核进度'"
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
        <p>每学期会开放不同的奖学金申报，留意下方时段，别错过哦。</p>
      </div>
      <div v-if="allWindowsClosed" class="no-data-hint">目前还没有可以申请的奖学金，开放后会第一时间通知你</div>
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
        <h3>我的奖学金申请</h3>
        <p>你提交过的奖学金申请都在这里，可以随时查看进度。</p>
      </div>
      <div v-if="scholarshipRecords.length" class="record-list">
        <div v-for="item in scholarshipRecords" :key="item.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ item.scholarshipType }}</strong>
            <span class="record-row__sub">{{ item.termLabel }}</span>
          </div>
          <div class="record-row__detail">
            <span v-if="item.honorLevel">{{ item.honorLevel }}</span>
            <span v-if="item.mentorTeacherId"><span class="sep">·</span>指导教师：{{ item.mentorTeacherId }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(item.requestStatus)" size="small">{{ statusLabel(item.requestStatus) }}</el-tag>
            <span class="record-row__time">{{ item.submittedTime || '-' }}</span>
            <el-button link type="primary" size="small" @click="openRecordDetail(item)">查看详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="你还没有提交过奖学金申请，有合适的就大胆申报吧 ✨" />
    </section>
  </StudentAffairPageFrame>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import StudentAffairPageFrame from './StudentAffairPageFrame.vue'
import { buildApplyPayload, buildWindowCards, resolveOptionLabel, statusLabel, statusType, useStudentAffairDetail } from './useStudentAffairDetail'

const props = defineProps<{ portalRole: 'student'; categoryCode: string }>()
const emit = defineEmits<{ back: []; apply: [payload: any]; 'view-request': [request: any] }>()

const { loading, detail, presentation, templates, requests, stats, guideSteps } = useStudentAffairDetail(computed(() => props.categoryCode || 'SCHOLARSHIP'))

const scholarshipTypeOptions = [
  { label: '国家奖学金', value: 'NATIONAL' },
  { label: '校级奖学金', value: 'SCHOOL' },
  { label: '专项奖学金', value: 'SPECIAL' },
]

const windowCards = computed(() => buildWindowCards(templates.value))
const allWindowsClosed = computed(() => windowCards.value.length > 0 && windowCards.value.every((item: any) => !item.open))

const scholarshipRecords = computed(() => requests.value.filter((item: any) => item.categoryCode === 'SCHOLARSHIP').map((item: any) => ({
  requestId: item.requestId,
  scholarshipType: resolveOptionLabel(scholarshipTypeOptions, item.parsedFormData.scholarshipType, '奖学金申请'),
  termLabel: item.parsedFormData.termId ? `学期 ${item.parsedFormData.termId}` : item.parsedFormData.awardTerm || '未绑定学期',
  mentorTeacherId: item.parsedFormData.mentorTeacherId,
  honorLevel: item.parsedFormData.honorLevel,
  reason: item.parsedFormData.reason,
  requestStatus: item.requestStatus,
  submittedTime: item.submittedTime,
})))

const metrics = computed(() => ([
  { label: '我的申请', value: scholarshipRecords.value.length, hint: '你一共提交过的次数', tone: '' },
  { label: '已通过', value: scholarshipRecords.value.filter((item: any) => item.requestStatus === 'APPROVED').length, hint: '恭喜，这些已经通过啦', tone: 'success' },
  { label: '审核中', value: Number(stats.value.pending || 0), hint: '老师正在审核，耐心等一下', tone: 'warning' },
  { label: '可申请', value: windowCards.value.filter((item: any) => item.open).length, hint: '当前正在开放的奖学金', tone: 'info' },
]))

const summaryCards = computed(() => ([
  {
    icon: templates.value.length ? 'ri-shield-check-line' : 'ri-close-circle-line',
    title: '申请资格',
    value: templates.value.length ? '符合条件' : '暂无资格',
    tone: templates.value.length ? 'success' : 'warning',
    lines: [templates.value.length ? '你在奖学金申请范围内' : '不在申请范围，请联系辅导员'],
  },
]))

const windowTemplateIds = computed(() => new Set(windowCards.value.map((c: any) => c.templateId)))
const actions = computed(() => {
  // 只显示没有配置窗口期的模板在快捷办理；有窗口期的由下方「申报时段」卡片承载
  return templates.value
    .filter((t: any) => !windowTemplateIds.value.has(t.templateId))
    .map((t: any) => ({ label: '申请奖学金', template: t, type: 'primary' as const }))
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
