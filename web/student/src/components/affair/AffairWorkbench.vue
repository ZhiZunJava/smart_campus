<template>
  <div class="affair-workbench !pb-5">
    <!-- 服务门户首页 -->
    <AffairServicePortal
      v-if="currentView === 'portal'"
      :portal-role="portalRole"
      :allow-apply="allowApply"
      :allow-review="allowReview"
      :hero-title="heroTitle"
      :hero-subtitle="heroSubtitle"
      :dashboard="dashboard"
      :service-catalog="serviceCatalog"
      :frequent-templates="frequentTemplates"
      :recent-activity="recentActivity"
      @navigate="handleNavigate"
    />

    <!-- 分类详情 -->
    <AffairCategoryDetail
      v-if="currentView === 'category' && !showStudentSpecialPage"
      :portal-role="portalRole"
      :category-code="currentCategoryCode"
      @back="goPortal"
      @apply="openApply"
      @view-request="openRequestDetail"
    />

    <component
      :is="studentSpecialComponent"
      v-if="currentView === 'category' && showStudentSpecialPage && studentSpecialComponent"
      :portal-role="'student'"
      :category-code="currentCategoryCode"
      @back="goPortal"
      @apply="openApply"
      @view-request="openRequestDetail"
    />

    <!-- 分步申请向导 -->
    <AffairApplyWizard
      v-if="currentView === 'apply' && portalRole !== 'advisor'"
      :portal-role="portalRole as 'student' | 'teacher'"
      :template="currentTemplate"
      :initial-form-data="currentApplyPreset?.formData"
      :initial-title="currentApplyPreset?.title"
      @back="goBackFromApply"
      @view-result="(r) => openRequestDetailById(r.requestId)"
    />

    <!-- 我的申请列表 -->
    <AffairRequestList
      v-if="currentView === 'my-requests'"
      :requests="myRequests"
      :loading="loading"
      :allow-cancel="true"
      :initial-status-filter="requestStatusFilter"
      @back="goPortal"
      @view-request="openRequestDetail"
      @cancel-request="cancelRequest"
    />

    <!-- 申请详情 -->
    <AffairRequestDetail
      v-if="currentView === 'request-detail'"
      :portal-role="portalRole"
      :request-id="currentRequestId"
      @back="goBackFromDetail"
      @cancel="cancelCurrentRequest"
      @open-review="openReviewForCurrent"
    />

    <!-- 审核面板 -->
    <AffairReviewPanel
      v-if="currentView === 'reviews'"
      :portal-role="portalRole as 'teacher' | 'advisor'"
      :review-list="reviewList"
      :loading="loading"
      :allow-batch="true"
      @back="goPortal"
      @view-request="openRequestDetail"
      @refresh="loadAll"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from '@/utils/feedback'
import AffairServicePortal from './AffairServicePortal.vue'
import AffairCategoryDetail from './AffairCategoryDetail.vue'
import StudentAffairServicePage from './StudentAffairServicePage.vue'
import StudentAskLeavePage from './student-pages/StudentAskLeavePage.vue'
import StudentLeaveReturnPage from './student-pages/StudentLeaveReturnPage.vue'
import StudentTextbookPage from './student-pages/StudentTextbookPage.vue'
import StudentAcademicStatusPage from './student-pages/StudentAcademicStatusPage.vue'
import StudentPovertyIdentificationPage from './student-pages/StudentPovertyIdentificationPage.vue'
import StudentScholarshipPage from './student-pages/StudentScholarshipPage.vue'
import StudentGrantPage from './student-pages/StudentGrantPage.vue'
import StudentWorkStudyPage from './student-pages/StudentWorkStudyPage.vue'
import StudentActivityPage from './student-pages/StudentActivityPage.vue'
import StudentSubjectCompetitionPage from './student-pages/StudentSubjectCompetitionPage.vue'
import StudentInnovationEntrepreneurshipPage from './student-pages/StudentInnovationEntrepreneurshipPage.vue'
import StudentComprehensiveEvaluationPage from './student-pages/StudentComprehensiveEvaluationPage.vue'
import StudentLoanPage from './student-pages/StudentLoanPage.vue'
import AffairApplyWizard from './AffairApplyWizard.vue'
import AffairRequestList from './AffairRequestList.vue'
import AffairRequestDetail from './AffairRequestDetail.vue'
import AffairReviewPanel from './AffairReviewPanel.vue'
import {
  cancelPortalAffairRequest,
  getPortalAffairDashboard,
  getPortalAffairRecentActivity,
  getPortalAffairServices,
  listPortalAffairRequests,
  listPortalAffairReviewList,
} from '@/api/portal'
import { buildStudentAffairPathByCode } from '@/utils/affairCatalog'
import { isWindowOpen, parseJsonObject } from './student-pages/useStudentAffairDetail'

const props = withDefaults(defineProps<{
  portalRole: 'student' | 'teacher' | 'advisor'
  allowApply?: boolean
  allowReview?: boolean
  initialCategoryCode?: string
  heroTitle: string
  heroSubtitle: string
}>(), {
  allowApply: false,
  allowReview: false,
  initialCategoryCode: '',
})

const router = useRouter()
const loading = ref(false)
const currentView = ref<'portal' | 'category' | 'apply' | 'my-requests' | 'request-detail' | 'reviews'>('portal')
const currentCategoryCode = ref('')
const currentTemplate = ref<any>(null)
const currentApplyPreset = ref<any>(null)
const currentRequestId = ref(0)
const requestStatusFilter = ref('')
const previousView = ref<string>('portal')

const dashboard = ref<any>({})
const serviceCatalog = ref<any[]>([])
const frequentTemplates = ref<any[]>([])
const recentActivity = ref<any[]>([])
const myRequests = ref<any[]>([])
const reviewList = ref<any[]>([])
const specializedStudentCategories = ['ASK_LEAVE', 'LEAVE_RETURN_SCHOOL', 'TEXTBOOK', 'ACADEMIC_STATUS', 'POVERTY_IDENTIFICATION', 'SCHOLARSHIP', 'GRANT', 'WORK_STUDY', 'ACTIVITY', 'SUBJECT_COMPETITION', 'INNOVATION_ENTREPRENEURSHIP', 'COMPREHENSIVE_EVALUATION', 'LOAN']
const showStudentSpecialPage = computed(() => props.portalRole === 'student' && specializedStudentCategories.includes(currentCategoryCode.value))
const studentSpecialComponent = computed(() => {
  if (currentCategoryCode.value === 'ASK_LEAVE') return StudentAskLeavePage
  if (currentCategoryCode.value === 'LEAVE_RETURN_SCHOOL') return StudentLeaveReturnPage
  if (currentCategoryCode.value === 'TEXTBOOK') return StudentTextbookPage
  if (currentCategoryCode.value === 'ACADEMIC_STATUS') return StudentAcademicStatusPage
  if (currentCategoryCode.value === 'POVERTY_IDENTIFICATION') return StudentPovertyIdentificationPage
  if (currentCategoryCode.value === 'SCHOLARSHIP') return StudentScholarshipPage
  if (currentCategoryCode.value === 'GRANT') return StudentGrantPage
  if (currentCategoryCode.value === 'WORK_STUDY') return StudentWorkStudyPage
  if (currentCategoryCode.value === 'ACTIVITY') return StudentActivityPage
  if (currentCategoryCode.value === 'SUBJECT_COMPETITION') return StudentSubjectCompetitionPage
  if (currentCategoryCode.value === 'INNOVATION_ENTREPRENEURSHIP') return StudentInnovationEntrepreneurshipPage
  if (currentCategoryCode.value === 'COMPREHENSIVE_EVALUATION') return StudentComprehensiveEvaluationPage
  if (currentCategoryCode.value === 'LOAN') return StudentLoanPage
  return StudentAffairServicePage
})

async function loadAll() {
  loading.value = true
  try {
    const dashboardRes = await getPortalAffairDashboard(props.portalRole)
    dashboard.value = dashboardRes.data || dashboardRes
    frequentTemplates.value = dashboard.value.frequentTemplates || []

    if (props.allowApply && props.portalRole !== 'advisor') {
      const [serviceRes, requestRes, activityRes] = await Promise.all([
        getPortalAffairServices(props.portalRole as 'student' | 'teacher'),
        listPortalAffairRequests(props.portalRole as 'student' | 'teacher'),
        getPortalAffairRecentActivity(props.portalRole),
      ])
      serviceCatalog.value = serviceRes.data || serviceRes
      myRequests.value = requestRes.data || requestRes
      recentActivity.value = activityRes.data || activityRes
    }

    if (props.allowReview) {
      const reviewRes = await listPortalAffairReviewList(props.portalRole as 'teacher' | 'advisor')
      reviewList.value = reviewRes.data || reviewRes
    }
  } finally {
    loading.value = false
  }
}

function goPortal() {
  if (props.initialCategoryCode) {
    router.push(`/${props.portalRole}/affairs`)
    return
  }
  currentView.value = 'portal'
}

function goBackFromApply() {
  currentView.value = currentCategoryCode.value ? 'category' : 'portal'
}

function goBackFromDetail() {
  currentView.value = previousView.value as any || 'portal'
}

function handleNavigate(target: string, payload?: any) {
  if (target === 'category') {
    const categoryCode = payload?.categoryCode || ''
    if (categoryCode) {
      if (props.portalRole === 'student') {
        router.push(buildStudentAffairPathByCode(categoryCode))
      } else {
        router.push(`/${props.portalRole}/affairs/${categoryCode}`)
      }
      return
    }
  } else if (target === 'apply') {
    openApply(payload)
    return
  } else if (target === 'my-requests') {
    requestStatusFilter.value = typeof payload === 'string' ? payload : ''
    currentView.value = 'my-requests'
  } else if (target === 'reviews') {
    currentView.value = 'reviews'
  } else if (target === 'request-detail') {
    openRequestDetail(payload)
  }
}

function openApply(payload: any) {
  const tpl = payload?.template || payload
  // ── Student-side window period interception ──
  if (tpl && props.portalRole === 'student') {
    const rules = parseJsonObject(tpl.businessRulesJson)
    if (rules.openWindowEnabled && !isWindowOpen(rules)) {
      const label = rules.openStartTime && rules.openEndTime
        ? `申报时段为 ${rules.openStartTime} 至 ${rules.openEndTime}，当前不在开放期内`
        : '当前申报时段尚未开放'
      ElMessageBox.alert(label, '暂不可申请', {
        confirmButtonText: '知道了',
        type: 'warning',
      })
      return
    }
  }
  if (payload?.template) {
    currentTemplate.value = payload.template
    currentApplyPreset.value = payload.presetData || null
  } else {
    currentTemplate.value = payload
    currentApplyPreset.value = null
  }
  currentView.value = 'apply'
}

function openRequestDetail(row: any) {
  previousView.value = currentView.value
  currentRequestId.value = row.requestId
  currentView.value = 'request-detail'
}

function openRequestDetailById(requestId: number) {
  previousView.value = currentCategoryCode.value ? 'category' : 'portal'
  currentRequestId.value = requestId
  currentView.value = 'request-detail'
}

async function cancelRequest(row: any) {
  await ElMessageBox.confirm(`确定撤回申请"${row.templateName}"吗？`, '提示', { type: 'warning' })
  await cancelPortalAffairRequest(props.portalRole as 'student' | 'teacher', row.requestId)
  ElMessage.success('已撤回申请')
  await loadAll()
}

async function cancelCurrentRequest() {
  const request = { requestId: currentRequestId.value, templateName: '当前申请' }
  await cancelRequest(request)
  goBackFromDetail()
}

function openReviewForCurrent() {
  previousView.value = 'reviews'
  currentView.value = 'reviews'
}

watch(() => props.initialCategoryCode, (value) => {
  currentCategoryCode.value = value || ''
  if (value) {
    currentView.value = 'category'
    return
  }
  if (currentView.value === 'category') {
    currentView.value = 'portal'
  }
}, { immediate: true })

onMounted(loadAll)
</script>

<style scoped>
.affair-workbench {
  display: grid;
  gap: 18px;
}

@media (max-width: 768px) {
  .affair-workbench { gap: 12px; }
}
</style>
