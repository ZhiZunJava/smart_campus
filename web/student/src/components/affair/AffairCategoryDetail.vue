<template>
  <div class="category-detail" :class="presentation?.accentClass || ''">
    <div class="back-nav">
      <el-button text bg size="small" @click="$emit('back')"><i class="ri-arrow-left-line" /> 返回服务大厅</el-button>
    </div>

    <section v-if="detail" class="category-hero">
      <div class="category-hero__icon"><i :class="detail.category?.icon || 'ri-service-line'" /></div>
      <div class="category-hero__text">
        <p class="category-hero__eyebrow">{{ presentation?.highlightLabel || '学生服务' }}</p>
        <h2>{{ presentation?.title || detail.category?.categoryName }}</h2>
        <p>{{ presentation?.subtitle || detail.category?.remark || '在线办理，全流程可追踪。' }}</p>
      </div>
      <!-- Stats consolidated into metric-grid below -->
    </section>

    <section v-if="detail" class="overview-panels">
      <div class="overview-card">
        <h3>服务说明</h3>
        <p>{{ presentation?.intro || detail.category?.remark || '当前分类支持线上办理和进度跟踪。' }}</p>
      </div>
      <div v-if="detail.advisorInfo" class="overview-card overview-card--advisor">
        <h3>我的辅导员</h3>
        <div class="advisor-line">
          <strong>{{ detail.advisorInfo.advisorName }}</strong>
          <el-tag size="small" effect="plain">{{ detail.advisorInfo.bindingMode === 'DIRECT' ? '显式绑定' : '班级回退' }}</el-tag>
        </div>
        <p v-if="detail.advisorInfo.teacherNo">工号：{{ detail.advisorInfo.teacherNo }}</p>
        <p v-if="detail.advisorInfo.phonenumber">联系方式：{{ detail.advisorInfo.phonenumber }}</p>
      </div>
      <div v-if="detail.studentStatus" class="overview-card overview-card--status">
        <h3>当前学籍状态</h3>
        <div class="advisor-line">
          <strong>{{ detail.studentStatus.currentStatusName }}</strong>
          <el-tag type="success" effect="light">{{ detail.studentStatus.currentStatusCode }}</el-tag>
        </div>
        <p v-if="detail.studentStatus.updateTime">最近更新：{{ detail.studentStatus.updateTime }}</p>
      </div>
    </section>

    <section v-if="serviceMetrics.length" class="portal-section">
      <div class="section-header">
        <div>
          <h3>业务工作台</h3>
          <p>关键数据一目了然，快速掌握办理进度。</p>
        </div>
      </div>
      <div class="metric-grid">
        <article v-for="item in serviceMetrics" :key="item.label" class="metric-card">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <p>{{ item.hint }}</p>
        </article>
      </div>
      <div v-if="quickActions.length" class="quick-actions">
        <el-button
          v-for="action in quickActions"
          :key="action.label"
          :type="action.type || 'primary'"
          plain
          @click="handleQuickAction(action)"
        >
          {{ action.label }}
        </el-button>
      </div>
    </section>

    <el-tabs v-if="detail" v-model="activeTab" class="category-tabs">
      <el-tab-pane label="快捷办理" name="services">
        <section class="portal-section">
          <div class="section-header">
            <div>
              <h3>可办理服务</h3>
              <p>点击即可发起办理，流程自动流转。</p>
            </div>
          </div>
          <div class="template-grid">
            <article
              v-for="tpl in templates"
              :key="tpl.templateId"
              class="template-card"
              @click="emitApply(tpl)"
            >
              <div class="template-card__title">
                <strong>{{ tpl.businessName || tpl.templateName }}</strong>
                <el-tag size="small" effect="plain">{{ tpl.allowAttachment === '1' ? '支持附件' : '纯表单' }}</el-tag>
              </div>
              <p>{{ tpl.remark || '模板驱动，按流程在线办理。' }}</p>
              <div class="template-card__stats">
                <span>历史办理 {{ findTemplateStats(tpl.templateId)?.total || 0 }} 次</span>
                <span>待处理 {{ findTemplateStats(tpl.templateId)?.pending || 0 }} 次</span>
              </div>
              <footer>
                <span>{{ tpl.audienceRoles }}</span>
                <strong>{{ primaryActionLabel(tpl) }} →</strong>
              </footer>
            </article>
          </div>
          <el-empty v-if="!templates.length" :description="presentation?.emptyHint || '当前分类暂无可办理模板'" />
        </section>
      </el-tab-pane>

      <el-tab-pane label="我的记录" name="records">
        <section class="portal-section">
          <div class="record-toolbar">
            <div class="filter-pills">
              <button class="pill" :class="{ 'is-active': statusFilter === '' }" @click="statusFilter = ''">全部</button>
              <button class="pill" :class="{ 'is-active': statusFilter === 'PENDING' }" @click="statusFilter = 'PENDING'">待处理</button>
              <button class="pill" :class="{ 'is-active': statusFilter === 'APPROVED' }" @click="statusFilter = 'APPROVED'">已通过</button>
              <button class="pill" :class="{ 'is-active': statusFilter === 'REJECTED' }" @click="statusFilter = 'REJECTED'">已驳回</button>
            </div>
            <div class="record-summary">
              <span>当前共 {{ filteredRequests.length }} 条记录</span>
            </div>
          </div>
          <el-table :data="filteredRequests" size="small">
            <el-table-column prop="requestNo" label="编号" min-width="160" />
            <el-table-column prop="templateName" label="服务" min-width="180" />
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
          <el-empty v-if="!filteredRequests.length" :description="presentation?.emptyHint || '暂无记录'" />
        </section>
      </el-tab-pane>

      <el-tab-pane label="办理指南" name="guide">
        <section class="portal-section">
          <div class="guide-grid">
            <article v-for="(step, index) in guideSteps" :key="step.title" class="guide-card">
              <span class="guide-card__index">0{{ index + 1 }}</span>
              <strong>{{ step.title }}</strong>
              <p>{{ step.desc }}</p>
            </article>
          </div>
        </section>
      </el-tab-pane>
    </el-tabs>

    <el-empty v-if="!loading && !detail" description="分类不存在或加载失败" />
    <div v-if="loading" class="loading-center"><el-icon class="is-loading"><i class="ri-loader-4-line" /></el-icon></div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { getPortalAffairCategoryDetail } from '@/api/portal'
import { resolveAffairPresentation } from '@/utils/affairPresentation'
import { statusLabel, statusType } from './student-pages/useStudentAffairDetail'

const props = defineProps<{
  portalRole: 'student' | 'teacher' | 'advisor'
  categoryCode: string
}>()

const emit = defineEmits<{
  back: []
  apply: [payload: any]
  'view-request': [request: any]
}>()

const loading = ref(false)
const detail = ref<any>(null)
const activeTab = ref('services')
const statusFilter = ref('')
const presentation = computed(() => resolveAffairPresentation(props.categoryCode))
const templates = computed(() => detail.value?.templates || [])
const templateStats = computed(() => detail.value?.templateStats || [])
const myRequests = computed(() => detail.value?.myRequests || [])
const normalizedRequests = computed(() => myRequests.value.map((item: any) => ({
  ...item,
  parsedFormData: parseJson(item.formDataJson),
})))
const filteredRequests = computed(() => {
  if (!statusFilter.value) return myRequests.value
  return myRequests.value.filter((item: any) => item.requestStatus === statusFilter.value)
})
const guideSteps = computed(() => presentation.value?.guideSteps || [])
const serviceMetrics = computed(() => buildServiceMetrics())
const quickActions = computed(() => buildQuickActions())

async function loadDetail() {
  loading.value = true
  try {
    const res = await getPortalAffairCategoryDetail(props.portalRole, props.categoryCode)
    detail.value = res.data || res
  } finally {
    loading.value = false
  }
}

function findTemplateStats(templateId: number) {
  return templateStats.value.find((item: any) => Number(item.templateId) === Number(templateId))
}

function findTemplateByCode(templateCode: string) {
  return templates.value.find((item: any) => item.templateCode === templateCode)
}

function parseJson(value?: string) {
  try {
    return JSON.parse(value || '{}')
  } catch {
    return {}
  }
}

function buildServiceMetrics() {
  const pendingCount = myRequests.value.filter((item: any) => item.requestStatus === 'PENDING').length
  if (props.categoryCode === 'ASK_LEAVE') {
    const leaveRequests = normalizedRequests.value.filter((item: any) => item.templateCode === 'ASK_LEAVE_STANDARD')
    const cancelRequests = normalizedRequests.value.filter((item: any) => item.templateCode === 'ASK_LEAVE_CANCEL')
    const canceledRequestNos = new Set(cancelRequests.map((item: any) => item.parsedFormData.leaveRequestNo).filter(Boolean))
    const unclosedLeaves = leaveRequests.filter((item: any) => item.requestStatus === 'APPROVED' && !canceledRequestNos.has(item.requestNo))
    return [
      { label: '请假申请', value: leaveRequests.length, hint: '累计提交请假单' },
      { label: '销假登记', value: cancelRequests.length, hint: '已完成销假登记' },
      { label: '待销假', value: unclosedLeaves.length, hint: '已通过但未销假' },
      { label: '待审核', value: pendingCount, hint: '处理中申请' },
    ]
  }
  if (props.categoryCode === 'LEAVE_RETURN_SCHOOL') {
    const leaveRequests = normalizedRequests.value.filter((item: any) => item.templateCode === 'LEAVE_RETURN_STANDARD')
    const returnRequests = normalizedRequests.value.filter((item: any) => item.templateCode === 'LEAVE_RETURN_CONFIRM')
    const returnedNos = new Set(returnRequests.map((item: any) => item.parsedFormData.leaveRequestNo).filter(Boolean))
    const waitingReturn = leaveRequests.filter((item: any) => item.requestStatus === 'APPROVED' && !returnedNos.has(item.requestNo))
    return [
      { label: '离校申请', value: leaveRequests.length, hint: '累计离校申请' },
      { label: '返校登记', value: returnRequests.length, hint: '累计返校登记' },
      { label: '待返校', value: waitingReturn.length, hint: '已通过离校未登记返校' },
      { label: '待审核', value: pendingCount, hint: '处理中申请' },
    ]
  }
  if (props.categoryCode === 'TEXTBOOK') {
    const claimCount = normalizedRequests.value.filter((item: any) => item.parsedFormData.textbookType === 'CLAIM').length
    const reissueCount = normalizedRequests.value.filter((item: any) => item.parsedFormData.textbookType === 'REISSUE').length
    const orderCount = normalizedRequests.value.filter((item: any) => item.parsedFormData.textbookType === 'ORDER').length
    return [
      { label: '教材领用', value: claimCount, hint: '领用申请记录' },
      { label: '教材补领', value: reissueCount, hint: '补领申请记录' },
      { label: '教材征订', value: orderCount, hint: '征订申请记录' },
      { label: '待审核', value: pendingCount, hint: '处理中申请' },
    ]
  }
  if (props.categoryCode === 'ACADEMIC_STATUS') {
    const suspendCount = normalizedRequests.value.filter((item: any) => (item.templateCode || '').includes('SUSPEND')).length
    const resumeCount = normalizedRequests.value.filter((item: any) => (item.templateCode || '').includes('REINSTATE') || (item.templateCode || '').includes('RESUME')).length
    const dropCount = normalizedRequests.value.filter((item: any) => (item.templateCode || '').includes('DROP')).length
    const keepCount = normalizedRequests.value.filter((item: any) => (item.templateCode || '').includes('KEEP')).length
    return [
      { label: '休学申请', value: suspendCount, hint: '休学业务记录' },
      { label: '复学申请', value: resumeCount, hint: '复学业务记录' },
      { label: '退学申请', value: dropCount, hint: '退学业务记录' },
      { label: '保留学籍', value: keepCount, hint: '保留学籍业务记录' },
    ]
  }
  return [
    { label: '累计申请', value: myRequests.value.length, hint: '当前分类全部申请' },
    { label: '待审核', value: pendingCount, hint: '处理中申请' },
    { label: '已通过', value: myRequests.value.filter((item: any) => item.requestStatus === 'APPROVED').length, hint: '已完成审批' },
    { label: '已驳回', value: myRequests.value.filter((item: any) => item.requestStatus === 'REJECTED').length, hint: '需要重新提交' },
  ]
}

function buildQuickActions() {
  if (props.categoryCode === 'ASK_LEAVE') {
    const leaveTemplate = findTemplateByCode('ASK_LEAVE_STANDARD')
    const cancelTemplate = findTemplateByCode('ASK_LEAVE_CANCEL')
    const latestApprovedLeave = normalizedRequests.value
      .filter((item: any) => item.templateCode === 'ASK_LEAVE_STANDARD' && item.requestStatus === 'APPROVED')
      .sort((a: any, b: any) => String(b.submittedTime || '').localeCompare(String(a.submittedTime || '')))[0]
    const actions: any[] = []
    if (leaveTemplate) actions.push({ label: '发起请假', template: leaveTemplate, type: 'primary' })
    if (cancelTemplate) {
      actions.push({
        label: latestApprovedLeave ? '去销假登记' : '填写销假登记',
        template: cancelTemplate,
        type: 'warning',
        presetData: latestApprovedLeave ? { leaveRequestNo: latestApprovedLeave.requestNo } : {},
      })
    }
    return actions
  }
  if (props.categoryCode === 'LEAVE_RETURN_SCHOOL') {
    const leaveTemplate = findTemplateByCode('LEAVE_RETURN_STANDARD')
    const returnTemplate = findTemplateByCode('LEAVE_RETURN_CONFIRM')
    const latestApprovedLeave = normalizedRequests.value
      .filter((item: any) => item.templateCode === 'LEAVE_RETURN_STANDARD' && item.requestStatus === 'APPROVED')
      .sort((a: any, b: any) => String(b.submittedTime || '').localeCompare(String(a.submittedTime || '')))[0]
    const actions: any[] = []
    if (leaveTemplate) actions.push({ label: '发起离校申请', template: leaveTemplate, type: 'primary' })
    if (returnTemplate) {
      actions.push({
        label: latestApprovedLeave ? '登记返校' : '填写返校登记',
        template: returnTemplate,
        type: 'success',
        presetData: latestApprovedLeave ? { leaveRequestNo: latestApprovedLeave.requestNo } : {},
      })
    }
    return actions
  }
  const primaryTemplate = templates.value[0]
  return primaryTemplate ? [{ label: presentation.value?.primaryActionLabel || '立即办理', template: primaryTemplate, type: 'primary' }] : []
}

function handleQuickAction(action: any) {
  if (!action?.template) return
  emit('apply', {
    template: action.template,
    presetData: action.presetData
      ? { formData: action.presetData, title: action.title || '' }
      : null,
  })
}

function emitApply(template: any) {
  emit('apply', { template, presetData: null })
}

function primaryActionLabel(tpl: any) {
  const businessName = String(tpl?.businessName || tpl?.templateName || '')
  if (businessName.includes('销假')) return '去登记'
  if (businessName.includes('返校')) return '去登记'
  return presentation.value?.primaryActionLabel || '立即办理'
}

watch(() => props.categoryCode, () => {
  activeTab.value = 'services'
  statusFilter.value = ''
  if (props.categoryCode) loadDetail()
})

onMounted(() => {
  if (props.categoryCode) loadDetail()
})
</script>

<style scoped>
.category-detail { display: grid; gap: 20px; }
.back-nav { margin-bottom: -8px; }

.category-hero { display: flex; align-items: center; gap: 20px; padding: 28px; border-radius: 22px; background: #f8fafc; border: 1px solid #dbeafe; flex-wrap: wrap; }
.category-hero__icon { width: 56px; height: 56px; display: flex; align-items: center; justify-content: center; border-radius: 18px; background: #eff6ff; color: #266fcb; font-size: 28px; }
.category-hero__text { flex: 1; min-width: 200px; }
.category-hero__eyebrow { margin: 0 0 8px; color: #266fcb; font-size: 12px; letter-spacing: 0.12em; text-transform: uppercase; }
.category-hero__text h2 { margin: 0 0 6px; font-size: 24px; color: #0f172a; }
.category-hero__text p { margin: 0; color: #506173; line-height: 1.6; }
.category-hero__stats { display: flex; gap: 10px; flex-wrap: wrap; }
.mini-stat { padding: 12px 18px; border-radius: 14px; background: rgba(255,255,255,.85); border: 1px solid rgba(191,219,254,.6); text-align: center; }
.mini-stat span { display: block; color: #64748b; font-size: 11px; }
.mini-stat strong { display: block; margin-top: 4px; font-size: 20px; color: #0f172a; }
.mini-stat.is-warning strong { color: #d97706; }
.mini-stat.is-success strong { color: #059669; }
.mini-stat.is-danger strong { color: #dc2626; }

.overview-panels { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 14px; }
.overview-card { padding: 18px; border-radius: 18px; background: #fff; border: 1px solid #e5eef8; }
.overview-card h3 { margin: 0 0 10px; font-size: 16px; color: #0f172a; }
.overview-card p { margin: 0; color: #667085; line-height: 1.8; }
.advisor-line { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.overview-card--advisor { background: #f8fbff; }
.overview-card--status { background: #fffdf5; }

.metric-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 12px; }
.metric-card { padding: 18px; border-radius: 18px; background: #fff; border: 1px solid #e5eef8; }
.metric-card span { display: block; color: #64748b; font-size: 12px; }
.metric-card strong { display: block; margin-top: 8px; color: #0f172a; font-size: 28px; }
.metric-card p { margin: 8px 0 0; color: #94a3b8; font-size: 12px; }
.quick-actions { display: flex; gap: 10px; flex-wrap: wrap; }

.category-tabs :deep(.el-tabs__header) { margin-bottom: 12px; }
.portal-section { display: grid; gap: 14px; }
.section-header { display: flex; justify-content: space-between; align-items: end; gap: 12px; }
.section-header h3 { margin: 0; font-size: 18px; color: #0f172a; }
.section-header p { margin: 6px 0 0; color: #667085; }

.template-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 14px; }
.template-card { padding: 20px; border-radius: 18px; background: #fff; border: 1px solid #dbe7f5; cursor: pointer; transition: transform .15s, box-shadow .15s; }
.template-card:hover { transform: scale(1.02); box-shadow: 0 10px 28px rgba(15,23,42,.07); }
.template-card__title { display: flex; justify-content: space-between; gap: 10px; }
.template-card__title strong { color: #172033; font-size: 15px; }
.template-card p { min-height: 40px; color: #667085; line-height: 1.65; margin: 8px 0; }
.template-card__stats { display: flex; gap: 10px; flex-wrap: wrap; color: #94a3b8; font-size: 12px; margin-bottom: 8px; }
.template-card footer { display: flex; justify-content: space-between; align-items: center; color: #64748b; font-size: 12px; }
.template-card footer strong { color: #266fcb; }

.record-toolbar { display: flex; justify-content: space-between; align-items: center; gap: 12px; flex-wrap: wrap; }
.filter-pills { display: flex; gap: 8px; flex-wrap: wrap; }
.pill { border: 1px solid #dbe7f5; background: #fff; color: #475467; padding: 8px 14px; border-radius: 999px; cursor: pointer; transition: all .15s ease; }
.pill.is-active { background: #eff6ff; color: #2563eb; border-color: #93c5fd; }
.record-summary { color: #94a3b8; font-size: 12px; }

.guide-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 14px; }
.guide-card { padding: 18px; border-radius: 18px; border: 1px solid #e5eef8; background: #fff; }
.guide-card__index { display: inline-flex; padding: 4px 10px; border-radius: 999px; background: #eff6ff; color: #2563eb; font-size: 12px; margin-bottom: 10px; }
.guide-card strong { display: block; color: #0f172a; font-size: 16px; margin-bottom: 8px; }
.guide-card p { margin: 0; color: #667085; line-height: 1.75; }

.loading-center { display: flex; justify-content: center; padding: 40px; font-size: 28px; color: #94a3b8; }

.category-detail.is-attendance .category-hero { background: #f0fdf4; border-left: 4px solid #059669; border-color: #bbf7d0; }
.category-detail.is-funding .category-hero { background: #fff7ed; border-left: 4px solid #d97706; border-color: #fed7aa; }
.category-detail.is-growth .category-hero { background: #fdf4ff; border-left: 4px solid #7c3aed; border-color: #e9d5ff; }
.category-detail.is-academic .category-hero { background: #eff6ff; border-left: 4px solid #2563eb; border-color: #bfdbfe; }

@media (max-width: 800px) {
  .category-hero { flex-direction: column; align-items: flex-start; }
}

@media (max-width: 768px) {
  .category-hero { padding: 22px; }
  .category-hero__icon { width: 48px; height: 48px; font-size: 24px; border-radius: 14px; }
  .category-hero__text h2 { font-size: 20px; }
  .template-grid { grid-template-columns: 1fr; }
  .template-card { padding: 16px; }
  .overview-panels { grid-template-columns: 1fr; }
  .section-header { flex-direction: column; align-items: flex-start; gap: 6px; }
  .mini-stat { padding: 10px 14px; }
  .mini-stat strong { font-size: 18px; }
  :deep(.el-dialog) { width: 96vw !important; margin: 2vh auto !important; }
  :deep(.el-drawer) { width: 100% !important; }
}

@media (max-width: 640px) {
  .category-hero { padding: 16px; gap: 14px; }
  .category-hero__stats { flex-direction: column; }
  .overview-panels { grid-template-columns: 1fr; }
  .template-grid { grid-template-columns: 1fr; }
  .guide-grid { grid-template-columns: 1fr; }
  .metric-grid { grid-template-columns: 1fr 1fr; }
  .record-toolbar { flex-direction: column; align-items: flex-start; }
  .category-hero__text h2 { font-size: 18px; }
  .category-hero__eyebrow { font-size: 11px; }
  .category-hero__text p { font-size: 13px; }
  .section-header h3 { font-size: 16px; }
  .template-card__title strong { font-size: 14px; }
  .template-card p { font-size: 13px; }
  .overview-card h3 { font-size: 14px; }
  .pill { padding: 6px 12px; font-size: 13px; }
  .guide-card strong { font-size: 14px; }
}

@media (prefers-reduced-motion: reduce) {
  .template-card,
  .pill {
    transition: none !important;
  }
  .template-card:hover {
    transform: none;
  }
}
</style>
