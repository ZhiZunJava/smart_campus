<template>
  <div class="request-detail">
    <div class="back-nav">
      <el-button text bg size="small" @click="$emit('back')"><i class="ri-arrow-left-line" /> 返回</el-button>
    </div>

    <div v-if="loading" class="loading-center"><el-icon class="is-loading" style="font-size:28px;color:#94a3b8"><i class="ri-loader-4-line" /></el-icon></div>

    <template v-if="bundle && !loading">
      <section class="detail-hero">
        <div class="detail-hero__text">
          <h2>{{ bundle.request?.title || bundle.request?.templateName }}</h2>
          <p>{{ bundle.request?.requestNo }} · {{ bundle.request?.categoryName }}</p>
        </div>
        <el-tag :type="statusType(bundle.request?.requestStatus)" size="large">{{ statusLabel(bundle.request?.requestStatus) }}</el-tag>
      </section>

      <section class="workflow-progress">
        <h3>审批进度</h3>
        <el-steps :active="activeStepIndex" align-center finish-status="success" :process-status="processStatus">
          <el-step title="提交申请" />
          <el-step v-for="(step, i) in workflowSteps" :key="i" :title="step.stepName" />
          <el-step title="办结" />
        </el-steps>
      </section>

      <div class="detail-grid">
        <section class="detail-card">
          <h4>申请概况</h4>
          <div class="info-rows">
            <div class="info-row"><span>分类</span><strong>{{ bundle.request?.categoryName || '-' }}</strong></div>
            <div class="info-row"><span>服务</span><strong>{{ bundle.request?.templateName || '-' }}</strong></div>
            <div class="info-row"><span>申请人</span><strong>{{ bundle.request?.applicantName }}（{{ bundle.request?.applicantNo || '-' }}）</strong></div>
            <div class="info-row"><span>班级</span><strong>{{ bundle.request?.className || bundle.request?.applicantDeptName || '-' }}</strong></div>
            <div class="info-row"><span>提交时间</span><strong>{{ bundle.request?.submittedTime || '-' }}</strong></div>
            <div class="info-row"><span>当前节点</span><strong>{{ bundle.request?.currentStepName || '-' }}</strong></div>
          </div>
        </section>

        <section class="detail-card">
          <h4>表单数据</h4>
          <div class="info-rows">
            <div v-for="item in formEntries" :key="item.label" class="info-row">
              <span>{{ item.label }}</span>
              <strong>{{ item.value || '-' }}</strong>
            </div>
          </div>
          <el-empty v-if="!formEntries.length" description="无表单数据" :image-size="40" />
        </section>
      </div>

      <section v-if="attachments.length" class="detail-card detail-card--full">
        <h4>附件</h4>
        <div class="attachment-grid">
          <a v-for="item in attachments" :key="item.url" :href="item.url" target="_blank" rel="noreferrer" class="attachment-chip">
            <i class="ri-attachment-2" />
            {{ item.name || '附件' }}
          </a>
        </div>
      </section>

      <section v-if="specializedEntries.length" class="detail-card detail-card--full">
        <h4>业务信息</h4>
        <div class="detail-grid">
          <div v-for="item in specializedEntries" :key="item.label" class="detail-card detail-card--inner">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </section>

      <section class="detail-card detail-card--full">
        <h4>审批轨迹</h4>
        <AffairTimeline :actions="bundle.actions || []" />
      </section>

      <div v-if="bundle.canCancel || bundle.canReview" class="detail-actions">
        <el-button v-if="bundle.canCancel" type="warning" @click="$emit('cancel')">撤回申请</el-button>
        <el-button v-if="bundle.canReview" type="success" @click="$emit('open-review')">审核此申请</el-button>
      </div>
    </template>

    <el-empty v-if="!loading && !bundle" description="加载失败或申请不存在" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import AffairTimeline from './AffairTimeline.vue'
import { getPortalAffairRequestDetail } from '@/api/portal'
import { statusLabel, statusType } from './student-pages/useStudentAffairDetail'

const props = defineProps<{
  portalRole: 'student' | 'teacher' | 'advisor'
  requestId: number
}>()

defineEmits<{
  back: []
  cancel: []
  'open-review': []
}>()

const loading = ref(false)
const bundle = ref<any>(null)

const workflowSteps = computed(() => bundle.value?.workflowSteps || [])
const activeStepIndex = computed(() => {
  const req = bundle.value?.request
  if (!req) return 0
  if (req.requestStatus === 'APPROVED') return workflowSteps.value.length + 1
  if (req.requestStatus === 'REJECTED' || req.requestStatus === 'CANCELLED') return (req.currentStepIndex || 0) + 1
  return (req.currentStepIndex || 0) + 1
})
const processStatus = computed(() => {
  const status = bundle.value?.request?.requestStatus
  if (status === 'REJECTED') return 'error'
  if (status === 'CANCELLED') return 'wait'
  return 'process'
})

const formEntries = computed(() => {
  const fields = bundle.value?.formFields || []
  const data = bundle.value?.formData || {}
  return fields
    .map((f: any) => {
      let value = data[f.field]
      if (value === null || value === undefined || value === '') return null
      if (f.component === 'select' && Array.isArray(f.options)) {
        const opt = f.options.find((o: any) => String(o.value) === String(value))
        if (opt) value = opt.label
      }
      return { label: f.label, value }
    })
    .filter(Boolean)
})

const attachments = computed(() =>
  (bundle.value?.attachments || []).map((a: any) => ({
    name: a.name || a.originalFilename || a.newFileName || '附件',
    url: a.url || a.value || a,
  }))
)
const specializedEntries = computed(() => {
  const specialized = bundle.value?.specializedRecord || {}
  const request = bundle.value?.request || {}
  const categoryCode = request.categoryCode
  const labelMap: Record<string, Record<string, string>> = {
    ASK_LEAVE: {
      business_type: '业务类型',
      leave_type: '请假类型',
      start_date: '开始日期',
      end_date: '结束日期',
      destination: '去向地点',
      emergency_contact: '紧急联系人',
      leave_request_no: '关联请假编号',
      actual_return_date: '实际返校日期',
      cancel_remark: '销假说明',
      reason: '申请说明',
    },
    LEAVE_RETURN_SCHOOL: {
      business_type: '业务类型',
      leave_school_date: '离校日期',
      expected_return_date: '预计返校日期',
      location: '当前去向',
      health_status: '健康状态',
      leave_request_no: '关联离校编号',
      return_date: '返校日期',
      travel_summary: '行程说明',
      reason: '申请说明',
    },
    ACTIVITY: {
      activity_name: '活动名称',
      activity_date: '活动日期',
      venue: '活动地点',
      participant_count: '参与人数',
      mentor_teacher_name: '指导教师',
      reason: '活动说明',
    },
    SUBJECT_COMPETITION: {
      competition_name: '竞赛名称',
      competition_level: '竞赛级别',
      competition_date: '竞赛时间',
      mentor_teacher_name: '指导教师',
      team_info: '团队信息',
      reason: '申请说明',
    },
    INNOVATION_ENTREPRENEURSHIP: {
      project_name: '项目名称',
      project_type: '项目类型',
      mentor_teacher_name: '指导教师',
      funding_need: '经费需求',
      reason: '项目说明',
    },
    COMPREHENSIVE_EVALUATION: {
      evaluation_term: '评价学期',
      achievement_summary: '成果摘要',
      volunteer_hours: '志愿时长',
      reason: '补充说明',
    },
    TEXTBOOK: {
      textbook_type: '办理类型',
      course_name: '课程名称',
      isbn: 'ISBN',
      quantity: '数量',
      reason: '申请说明',
    },
    ACADEMIC_STATUS: {
      effective_date: '生效日期',
      expected_resume_date: '预计复学日期',
      target_major: '目标专业',
      recovery_proof: '复学依据',
      guardian_phone: '家长电话',
      before_status_name: '变更前状态',
      after_status_name: '变更后状态',
      reason: '异动原因',
    }
  }
  const currentLabels = labelMap[categoryCode] || {}
  return Object.entries(currentLabels)
    .map(([key, label]) => ({ label, value: formatSpecializedValue(categoryCode, key, specialized[key]) }))
    .filter((item) => item.value !== undefined && item.value !== null && item.value !== '')
})

async function loadDetail() {
  loading.value = true
  try {
    const res = await getPortalAffairRequestDetail(props.portalRole, props.requestId)
    bundle.value = res.data || res
  } finally {
    loading.value = false
  }
}

function formatSpecializedValue(categoryCode: string, key: string, value: any) {
  if (value === undefined || value === null || value === '') return value
  if (categoryCode === 'ASK_LEAVE' && key === 'business_type') {
    return value === 'LEAVE' ? '请假申请' : value === 'CANCEL' ? '销假登记' : value
  }
  if (categoryCode === 'ASK_LEAVE' && key === 'leave_type') {
    if (value === 'PERSONAL') return '事假'
    if (value === 'SICK') return '病假'
    if (value === 'OFFICIAL') return '公假'
  }
  if (categoryCode === 'LEAVE_RETURN_SCHOOL' && key === 'business_type') {
    return value === 'LEAVE' ? '离校申请' : value === 'RETURN' ? '返校登记' : value
  }
  if (categoryCode === 'LEAVE_RETURN_SCHOOL' && key === 'health_status') {
    if (value === 'GOOD') return '良好'
    if (value === 'WATCH') return '需关注'
    if (value === 'SPECIAL') return '特殊情况'
  }
  return value
}

watch(() => props.requestId, () => { if (props.requestId) loadDetail() })
onMounted(() => { if (props.requestId) loadDetail() })
</script>

<style scoped>
.request-detail { display: grid; gap: 20px; }
.back-nav { margin-bottom: -4px; }
.loading-center { display: flex; justify-content: center; padding: 40px; }

.detail-hero { display: flex; justify-content: space-between; align-items: center; padding: 24px; border-radius: 20px; background: #f8fafc; border: 1px solid #dbeafe; }
.detail-hero__text h2 { margin: 0 0 6px; font-size: 22px; color: #0f172a; }
.detail-hero__text p { margin: 0; color: #64748b; }

.workflow-progress { padding: 20px 0; }
.workflow-progress h3 { margin: 0 0 16px; font-size: 16px; color: #334155; }

.detail-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.detail-card { border: 1px solid #e5e7eb; border-radius: 16px; padding: 20px; background: #fff; transition: transform .15s ease, box-shadow .15s ease; }
.detail-card:hover { transform: scale(1.02); box-shadow: 0 4px 16px rgba(15,23,42,.06); }
.detail-card h4 { margin: 0 0 14px; font-size: 15px; color: #0f172a; }
.detail-card--full { grid-column: 1 / -1; }
.detail-card--inner { padding: 14px; background: #f8fafc; }
.info-rows { display: grid; gap: 10px; }
.info-row { display: flex; gap: 10px; line-height: 1.7; }
.info-row span { color: #64748b; font-size: 13px; min-width: 70px; flex-shrink: 0; }
.info-row strong { color: #111827; font-weight: 500; }

.attachment-grid { display: flex; gap: 10px; flex-wrap: wrap; }
.attachment-chip { display: inline-flex; align-items: center; gap: 6px; padding: 8px 16px; border-radius: 10px; background: #eff6ff; color: #266fcb; font-size: 13px; text-decoration: none; transition: background .15s; }
.attachment-chip:hover { background: #dbeafe; }

.detail-actions { display: flex; gap: 12px; justify-content: center; padding-top: 8px; }

@media (max-width: 768px) {
  .detail-grid { grid-template-columns: 1fr; }
  .detail-hero { flex-direction: column; align-items: flex-start; gap: 12px; }
  :deep(.el-steps) { overflow-x: auto; }
}
@media (max-width: 640px) {
  .detail-hero { padding: 16px; }
  .detail-card { padding: 16px; }
  .detail-actions { flex-direction: column; }
}

@media (prefers-reduced-motion: reduce) {
  * { transition: none !important; }
}
</style>
