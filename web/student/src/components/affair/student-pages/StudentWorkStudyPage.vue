<template>
  <StudentAffairPageFrame
    :loading="loading || jobLoading"
    :has-detail="!!detail"
    :presentation="presentation"
    :title="presentation?.title || '勤工助学服务'"
    :subtitle="presentation?.subtitle || '岗位发布、岗位申请与审核进度联动管理'"
    :metrics="metrics"
    :summary-cards="summaryCards"
    :actions="actions"
    :guide-steps="guideSteps"
    @back="$emit('back')"
    @action="handleAction"
  >
    <section class="service-section">
      <div class="section-head">
        <h3>开放岗位</h3>
        <p>只有后台已发布且处于开放期的岗位才会出现在这里，学生可直接选择岗位申请。</p>
      </div>
      <div v-if="jobList.length" class="job-grid">
        <article v-for="job in jobList" :key="job.jobId" class="job-card">
          <div class="job-card__head">
            <strong>{{ job.jobName }}</strong>
            <el-tag type="success" size="small">{{ job.salaryAmount || 0 }} / {{ salaryUnitLabel(job.salaryUnit) }}</el-tag>
          </div>
          <div class="job-card__meta">
            <span><span class="label">用工部门</span>{{ job.deptName || '--' }}</span>
            <span><span class="label">所属学期</span>{{ job.termName || '--' }}</span>
            <span><span class="label">工作地点</span>{{ job.workPlace || '--' }}</span>
            <span><span class="label">每周工时</span>{{ job.weeklyHours || 0 }} 小时</span>
            <span><span class="label">招聘人数</span>{{ job.headcount || 0 }}</span>
            <span><span class="label">申报时段</span>{{ job.openStartTime || '--' }} ~ {{ job.openEndTime || '--' }}</span>
          </div>
          <div class="job-card__footer">
            <el-button type="primary" size="small" :disabled="!workStudyTemplate || isJobExpired(job)" @click="applyForJob(job)">{{ isJobExpired(job) ? '已截止' : '申请岗位' }}</el-button>
          </div>
        </article>
      </div>
      <el-empty v-else description="当前暂无开放的勤工助学岗位" />
    </section>

    <section class="service-section">
      <div class="section-head">
        <h3>我的岗位申请</h3>
        <p>查看岗位编号、岗位意向、每周工时和审核状态。</p>
      </div>
      <div v-if="workStudyRecords.length" class="record-list">
        <div v-for="item in workStudyRecords" :key="item.requestId" class="record-row">
          <div class="record-row__main">
            <strong>{{ item.jobPreference || '岗位申请' }}</strong>
            <span class="record-row__sub">{{ item.termLabel }}</span>
          </div>
          <div class="record-row__detail">
            <span>{{ item.availableHours || '--' }}</span>
            <span class="sep">·</span>
            <span>{{ item.skillTags || '暂无技能填写' }}</span>
          </div>
          <div class="record-row__trail">
            <el-tag :type="statusType(item.requestStatus)" size="small">{{ statusLabel(item.requestStatus) }}</el-tag>
            <span class="record-row__time">{{ item.submittedTime || '-' }}</span>
            <el-button link type="primary" size="small" @click="openRecordDetail(item)">查看详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无勤工助学申请记录" />
    </section>
  </StudentAffairPageFrame>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { listPortalWorkStudyJobs } from '@/api/portal'
import StudentAffairPageFrame from './StudentAffairPageFrame.vue'
import { buildApplyPayload, statusLabel, statusType, useStudentAffairDetail } from './useStudentAffairDetail'

const props = defineProps<{ portalRole: 'student'; categoryCode: string }>()
const emit = defineEmits<{ back: []; apply: [payload: any]; 'view-request': [request: any] }>()

const { loading, detail, presentation, templates, requests, stats, guideSteps } = useStudentAffairDetail(computed(() => props.categoryCode || 'WORK_STUDY'))
const jobList = ref<any[]>([])
const jobLoading = ref(false)

const workStudyTemplate = computed(() => templates.value.find((item: any) => item.templateCode === 'WORK_STUDY_APPLY') || templates.value[0] || null)
const workStudyRecords = computed(() => requests.value.filter((item: any) => item.categoryCode === 'WORK_STUDY').map((item: any) => ({
  requestId: item.requestId,
  jobId: item.parsedFormData.jobId,
  jobPreference: item.parsedFormData.jobPreference,
  termLabel: item.parsedFormData.termId ? `学期 ${item.parsedFormData.termId}` : '未绑定学期',
  availableHours: item.parsedFormData.availableHours ? `${item.parsedFormData.availableHours} 小时/周` : '--',
  skillTags: item.parsedFormData.skillTags,
  requestStatus: item.requestStatus,
  submittedTime: item.submittedTime,
})))

const metrics = computed(() => ([
  { label: '开放岗位', value: jobList.value.length, hint: '当前可申请岗位数', tone: '' },
  { label: '累计申请', value: workStudyRecords.value.length, hint: '岗位申请记录', tone: 'info' },
  { label: '待审核', value: Number(stats.value.pending || 0), hint: '待辅导员审核', tone: 'warning' },
  { label: '已通过', value: workStudyRecords.value.filter((item: any) => item.requestStatus === 'APPROVED').length, hint: '已通过岗位申请', tone: 'success' },
]))

const summaryCards = computed(() => ([
  {
    icon: workStudyTemplate.value ? 'ri-briefcase-line' : 'ri-error-warning-line',
    title: '岗位申请',
    value: workStudyTemplate.value ? '按已发布岗位申请' : '暂未开放',
    tone: workStudyTemplate.value ? 'info' : 'warning',
    lines: ['系统校验岗位开放期，未发布岗位不可申请'],
  },
]))

const actions = computed(() => [])

function isJobExpired(job: any): boolean {
  if (!job.openEndTime) return false
  return new Date(job.openEndTime).getTime() < Date.now()
}

function salaryUnitLabel(value?: string) {
  if (value === 'HOUR') return '元/小时'
  if (value === 'ONCE') return '元/次'
  return '元/月'
}

async function loadJobs() {
  jobLoading.value = true
  try {
    const res = await listPortalWorkStudyJobs()
    jobList.value = res.data || res || []
  } finally {
    jobLoading.value = false
  }
}

function handleAction(action: any) {
  if (!action?.template || action.disabled) return
  emit('apply', buildApplyPayload(action))
}

function applyForJob(job: any) {
  if (!workStudyTemplate.value) return
  emit('apply', buildApplyPayload({
    template: workStudyTemplate.value,
    presetData: {
      termId: job.termId,
      jobId: job.jobId,
      jobPreference: job.jobName,
    },
    title: `勤工助学申请 - ${job.jobName}`,
  }))
}

function openRecordDetail(row: any) {
  const matched = requests.value.find((item: any) => Number(item.requestId) === Number(row.requestId))
  emit('view-request', matched || { requestId: row.requestId })
}

onMounted(loadJobs)
</script>

<style scoped>
@import './student-affair-shared.css';

/* Compact job cards */
.job-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
}
.job-card {
  display: flex;
  flex-direction: column;
  padding: 14px 16px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: #fff;
  transition: all 0.2s;
}
.job-card:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 12px -4px rgba(0,0,0,0.08);
}
.job-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
.job-card__head strong { font-size: 14px; color: #1e293b; }
.job-card__meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
  color: #667085;
  flex: 1;
}
.job-card__meta span { display: flex; justify-content: space-between; }
.job-card__meta .label { color: #94a3b8; min-width: 60px; }
.job-card__footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid #f1f5f9;
}
</style>
