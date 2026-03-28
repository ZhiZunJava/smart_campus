<template>
  <div class="portal-page personalized-selection-page">
    <div class="portal-section-title">
      <h3>选课申请</h3>
    </div>

    <section
      v-loading="initialLoading"
      class="portal-card personalized-shell"
      element-loading-text="正在加载申请信息..."
      element-loading-background="rgba(255, 255, 255, 0.84)"
    >
      <template v-if="!initialLoading">
        <PlanUnavailableState
          v-if="!hasPlan"
          badge-text="未发布计划"
          title="当前选课申请未开启"
          description="教务尚未发布本学期个性化选课计划，请等待计划发布后再办理申请。"
          :tips="noPlanTips"
        >
          <template #actions>
            <el-button type="primary" @click="goMessages">消息中心</el-button>
            <el-button plain @click="goCourseSelection">标准选课</el-button>
          </template>
        </PlanUnavailableState>

        <template v-else>
          <div class="personalized-shell__header">
            <div class="personalized-shell__title">
              <span class="personalized-shell__eyebrow">审核申请模式</span>
              <h4>{{ currentTermLabel }}</h4>
              <p>用于处理跨班修读、容量协调和特殊退课等需要审核的场景。</p>
            </div>
            <div class="personalized-shell__stats">
              <div class="personalized-stat">
                <span>当前状态</span>
                <strong :class="`is-${planStageTone}`">{{ planStageLabel }}</strong>
              </div>
              <div class="personalized-stat">
                <span>待审核</span>
                <strong>{{ pendingCount }}</strong>
              </div>
              <div class="personalized-stat">
                <span>已通过</span>
                <strong>{{ approvedCount }}</strong>
              </div>
            </div>
          </div>
          <PlanUnavailableState
            v-if="showClosedState"
            badge-text="暂未开放"
            :title="unavailableTitle"
            :description="unavailableDescription"
            :tips="unavailableTips"
          >
            <template #actions>
              <el-button type="primary" @click="goMessages">消息中心</el-button>
              <el-button plain @click="goCourseSelection">标准选课</el-button>
            </template>
          </PlanUnavailableState>

          <template v-else>
          <div class="personalized-notice-bar">
            <div class="personalized-notice-bar__item">
              <span>计划名称</span>
              <strong>{{ planInfo?.planName || '当前申请计划' }}</strong>
            </div>
            <div class="personalized-notice-bar__item">
              <span>申请开放</span>
              <strong>{{ formatDateRange(planInfo?.requestStartTime, planInfo?.requestEndTime) }}</strong>
            </div>
            <div class="personalized-notice-bar__item">
              <span>标准选课</span>
              <strong>{{ formatDateRange(planInfo?.selectionStartTime, planInfo?.selectionEndTime) }}</strong>
            </div>
            <div class="personalized-notice-bar__item">
              <span>审核提醒</span>
              <strong>{{ reviewNoticeText }}</strong>
            </div>
          </div>

          <div class="personalized-toolbar">
            <div class="personalized-toolbar__left">
              <el-select
                v-model="queryParams.termId"
                filterable
                clearable
                placeholder="切换学期"
                style="width: 240px"
                @change="loadAll"
              >
                <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
              <el-input v-model="keyword" clearable placeholder="搜索课程、班级、教师或申请编号" style="width: 320px">
                <template #prefix><i class="ri-search-line"></i></template>
              </el-input>
            </div>
            <div class="personalized-toolbar__right">
              <el-button plain @click="goCourseSelection">标准选课</el-button>
              <el-button type="primary" plain @click="goMessages">消息中心</el-button>
            </div>
          </div>

          <el-tabs v-model="activeTab" class="personalized-tabs">
            <el-tab-pane :label="`申请选课 (${visibleAddCandidates.length})`" name="add">
              <el-table v-loading="loading" :data="visibleAddCandidates" max-height="520" stripe>
                <el-table-column label="目标教学班" min-width="300">
                  <template #default="{ row }">
                    <div class="request-cell">
                      <strong>{{ row.courseName || '-' }}</strong>
                      <span>{{ row.className || '-' }}{{ row.teachingClassCode ? ` / ${row.teachingClassCode}` : '' }}</span>
                      <span v-if="row.selectionGroupName || row.selectionGroupCode">
                        专项分组：{{ row.selectionGroupName || row.selectionGroupCode }} / 限选 {{ row.selectionGroupLimit || 1 }} 门
                      </span>
                      <span>{{ row.termName || '-' }}{{ row.teacherName ? ` / ${row.teacherName}` : '' }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="上课安排" min-width="260">
                  <template #default="{ row }">
                    <div class="request-cell request-cell--schedule">
                      <strong>{{ resolveScheduleText(row) }}</strong>
                      <span>{{ resolveScheduleRoom(row) }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="申请说明" min-width="220" show-overflow-tooltip>
                  <template #default="{ row }">
                    <div class="request-cell">
                      <strong>
                        {{ row.canRequest ? '可发起申请' : '暂不可申请' }}
                        <el-tag v-if="hasPendingRequest(row)" type="warning" size="small" effect="plain" style="margin-left: 4px;">已申请</el-tag>
                      </strong>
                      <span>{{ row.requestHint || '可发起个性化选课申请' }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="容量" min-width="150">
                  <template #default="{ row }">
                    <div class="request-cell">
                      <strong>{{ row.selectedStudentCount ?? row.actualStudentCount ?? 0 }} / {{ row.studentLimit || '-' }}</strong>
                      <span v-if="row.remainingSeats != null">剩余 {{ row.remainingSeats }}</span>
                      <span v-else>不限制人数</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="210" fixed="right">
                  <template #default="{ row }">
                    <div class="request-actions">
                      <el-button link type="primary" @click="previewClassCourse(row)">查看教学班</el-button>
                      <el-button type="primary" size="small" :disabled="!row.canRequest" @click="openCreateDialog(row, 'ADD')">申请选课</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loading && !visibleAddCandidates.length" description="当前学期暂无可申请的个性化选课教学班" />
            </el-tab-pane>

            <el-tab-pane :label="`申请退课 (${visibleDropCandidates.length})`" name="drop">
              <el-table v-loading="loading" :data="visibleDropCandidates" max-height="520" stripe>
                <el-table-column label="当前已选教学班" min-width="300">
                  <template #default="{ row }">
                    <div class="request-cell">
                      <strong>{{ row.courseName || '-' }}</strong>
                      <span>{{ row.className || '-' }}{{ row.teachingClassCode ? ` / ${row.teachingClassCode}` : '' }}</span>
                      <span v-if="row.selectionGroupName || row.selectionGroupCode">
                        专项分组：{{ row.selectionGroupName || row.selectionGroupCode }} / 限选 {{ row.selectionGroupLimit || 1 }} 门
                      </span>
                      <span>{{ row.termName || '-' }}{{ row.teacherName ? ` / ${row.teacherName}` : '' }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="上课安排" min-width="260">
                  <template #default="{ row }">
                    <div class="request-cell request-cell--schedule">
                      <strong>{{ resolveScheduleText(row) }}</strong>
                      <span>{{ resolveScheduleRoom(row) }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="申请说明" min-width="220" show-overflow-tooltip>
                  <template #default="{ row }">
                    <div class="request-cell">
                      <strong>
                        {{ row.canRequest ? '可发起申请' : '暂不可申请' }}
                        <el-tag v-if="hasPendingRequest(row)" type="warning" size="small" effect="plain" style="margin-left: 4px;">已申请</el-tag>
                      </strong>
                      <span>{{ row.requestHint || '可发起个性化退课申请' }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="210" fixed="right">
                  <template #default="{ row }">
                    <div class="request-actions">
                      <el-button link type="primary" @click="previewClassCourse(row)">查看教学班</el-button>
                      <el-button type="danger" size="small" plain :disabled="!row.canRequest" @click="openCreateDialog(row, 'DROP')">申请退课</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loading && !visibleDropCandidates.length" description="当前学期暂无需要个性化申请退课的教学班" />
            </el-tab-pane>

            <el-tab-pane :label="`我的申请 (${filteredRequests.length})`" name="mine">
              <div class="personalized-toolbar personalized-toolbar--inner">
                <div class="personalized-toolbar__left">
                  <el-radio-group v-model="statusFilter">
                    <el-radio-button label="all">全部 ({{ requestList.length }})</el-radio-button>
                    <el-radio-button label="0">待审核 ({{ statusCount('0') }})</el-radio-button>
                    <el-radio-button label="1">已通过 ({{ statusCount('1') }})</el-radio-button>
                    <el-radio-button label="2">已驳回 ({{ statusCount('2') }})</el-radio-button>
                    <el-radio-button label="3">已撤回 ({{ statusCount('3') }})</el-radio-button>
                  </el-radio-group>
                </div>
              </div>

              <el-table v-loading="loading" :data="filteredRequests" max-height="520" stripe>
                <el-table-column label="申请编号" prop="requestNo" width="180" />
                <el-table-column label="目标教学班" min-width="300">
                  <template #default="{ row }">
                    <div class="request-cell">
                      <strong>{{ row.targetCourseName || '-' }}</strong>
                      <span>{{ row.targetClassName || '-' }}{{ row.targetTeachingClassCode ? ` / ${row.targetTeachingClassCode}` : '' }}</span>
                      <span>{{ row.termName || '-' }}{{ row.targetTeacherName ? ` / ${row.targetTeacherName}` : '' }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="申请类型" width="120">
                  <template #default="{ row }">{{ requestTypeLabel(row.requestType) }}</template>
                </el-table-column>
                <el-table-column label="申请理由" prop="requestReason" min-width="220" show-overflow-tooltip />
                <el-table-column label="状态" width="160">
                  <template #default="{ row }">
                    <div style="display: flex; align-items: center; gap: 6px; flex-wrap: wrap;">
                      <el-tag :type="statusTagType(row.requestStatus)" size="small">{{ statusLabel(row.requestStatus) }}</el-tag>
                      <el-tag v-if="row.requestStatus === '0' && pendingDays(row) > 0" :type="pendingDays(row) >= 3 ? 'danger' : 'warning'" size="small" effect="plain">
                        等待 {{ pendingDays(row) }}天
                      </el-tag>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="结果说明" min-width="220" show-overflow-tooltip>
                  <template #default="{ row }">
                    {{ row.reviewRemark || (row.requestStatus === '0' ? '等待教务审核' : row.requestStatus === '3' ? '已由本人撤回' : '暂无审核说明') }}
                  </template>
                </el-table-column>
                <el-table-column label="申请时间" prop="createTime" min-width="170" />
                <el-table-column label="操作" width="180" fixed="right">
                  <template #default="{ row }">
                    <div class="request-actions">
                      <el-button link type="primary" @click="previewClassCourse(row)">查看教学班</el-button>
                      <el-button v-if="row.requestStatus === '0'" link type="danger" @click="cancelRequest(row)">撤回申请</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loading && !filteredRequests.length" description="当前学期暂无个性化选课申请" />
            </el-tab-pane>
          </el-tabs>
          </template>
        </template>
      </template>
    </section>

    <el-dialog v-model="createDialogOpen" :title="dialogTitle" width="760px" append-to-body>
      <div class="request-create-layout">
        <div class="request-target-card" v-if="selectedCandidate">
          <div class="request-target-card__header">
            <el-tag :type="requestForm.requestType === 'DROP' ? 'danger' : 'primary'" size="small" effect="dark">
              {{ requestForm.requestType === 'DROP' ? '退课' : '选课' }}
            </el-tag>
            <strong>{{ selectedCandidate.courseName || '-' }}</strong>
          </div>
          <span>{{ selectedCandidate.className || '-' }}{{ selectedCandidate.teachingClassCode ? ` / ${selectedCandidate.teachingClassCode}` : '' }}</span>
          <span v-if="selectedCandidate.selectionGroupName || selectedCandidate.selectionGroupCode">
            专项分组：{{ selectedCandidate.selectionGroupName || selectedCandidate.selectionGroupCode }} / 限选 {{ selectedCandidate.selectionGroupLimit || 1 }} 门
          </span>
          <span>{{ resolveScheduleText(selectedCandidate) }} / {{ resolveScheduleRoom(selectedCandidate) }}</span>
          <span v-if="selectedCandidate.studentLimit">
            容量：{{ selectedCandidate.selectedStudentCount ?? selectedCandidate.actualStudentCount ?? 0 }} / {{ selectedCandidate.studentLimit }}
            <template v-if="selectedCandidate.remainingSeats != null">（剩余 {{ selectedCandidate.remainingSeats }}）</template>
          </span>
          <p>{{ selectedCandidate.requestHint || '请结合实际情况填写申请理由。' }}</p>
        </div>

        <el-form :model="requestForm" label-width="88px">
          <el-form-item label="申请理由">
            <el-input
              v-model="requestForm.requestReason"
              type="textarea"
              :rows="7"
              maxlength="500"
              show-word-limit
              placeholder="请说明申请原因，例如跨班修读、调班需求、容量协调或特殊退课原因"
            />
          </el-form-item>
          <el-form-item label="补充备注">
            <el-input
              v-model="requestForm.remark"
              type="textarea"
              :rows="3"
              maxlength="300"
              show-word-limit
              placeholder="可选，补充与课程、时间安排或个人情况有关的说明"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="createDialogOpen = false">取消</el-button>
        <el-button type="primary" :disabled="!selectedCandidate" @click="submitRequest">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import {
  cancelPortalPersonalizedSelectionRequest,
  createPortalPersonalizedSelectionRequest,
  listPortalPersonalizedSelectionOptions,
  listPortalPersonalizedSelectionRequests,
  listPortalTermOptions,
} from '@/api/portal'
import usePortalUserStore from '@/store/user'
import PlanUnavailableState from '@/components/selection/PlanUnavailableState.vue'

defineOptions({ name: 'PersonalizedSelection' })

const route = useRoute()
const router = useRouter()
const userStore = usePortalUserStore()

const initialLoading = ref(true)
const initialized = ref(false)
const loading = ref(false)
const termOptions = ref<any[]>([])
const requestList = ref<any[]>([])
const addCandidateList = ref<any[]>([])
const dropCandidateList = ref<any[]>([])
const createDialogOpen = ref(false)
const keyword = ref('')
const activeTab = ref<'add' | 'drop' | 'mine'>('add')
const statusFilter = ref<'all' | '0' | '1' | '2' | '3'>('all')
const selectedCandidate = ref<any>(null)
const planInfo = ref<any>(null)
const requestOpen = ref(false)
const selectionOpen = ref(false)
const dropOpen = ref(false)
const queryParams = reactive<any>({ termId: undefined })
const requestForm = reactive<any>({ requestType: 'ADD', requestReason: '', remark: '' })

const currentTermLabel = computed(() => termOptions.value.find((item: any) => item.value === queryParams.termId)?.label || '当前学期')
const hasPlan = computed(() => Boolean(planInfo.value?.enabled))
const showClosedState = computed(() => hasPlan.value && !requestOpen.value && !requestList.value.length)

const filteredRequests = computed(() => {
  const keywordValue = keyword.value.trim().toLowerCase()
  return requestList.value.filter((item: any) => {
    if (statusFilter.value !== 'all' && String(item.requestStatus || '') !== statusFilter.value) return false
    if (!keywordValue) return true
    return String(item.requestNo || '').toLowerCase().includes(keywordValue)
      || String(item.targetCourseName || '').toLowerCase().includes(keywordValue)
      || String(item.targetClassName || '').toLowerCase().includes(keywordValue)
      || String(item.targetTeacherName || '').toLowerCase().includes(keywordValue)
      || String(item.targetTeachingClassCode || '').toLowerCase().includes(keywordValue)
  })
})

const filteredAddCandidates = computed(() => {
  const keywordValue = keyword.value.trim().toLowerCase()
  if (!keywordValue) return addCandidateList.value
  return addCandidateList.value.filter((item: any) =>
    String(item.courseName || '').toLowerCase().includes(keywordValue)
      || String(item.className || '').toLowerCase().includes(keywordValue)
      || String(item.teacherName || '').toLowerCase().includes(keywordValue)
      || String(item.teachingClassCode || '').toLowerCase().includes(keywordValue),
  )
})

const filteredDropCandidates = computed(() => {
  const keywordValue = keyword.value.trim().toLowerCase()
  if (!keywordValue) return dropCandidateList.value
  return dropCandidateList.value.filter((item: any) =>
    String(item.courseName || '').toLowerCase().includes(keywordValue)
      || String(item.className || '').toLowerCase().includes(keywordValue)
      || String(item.teacherName || '').toLowerCase().includes(keywordValue)
      || String(item.teachingClassCode || '').toLowerCase().includes(keywordValue),
  )
})

const visibleAddCandidates = computed(() => requestOpen.value ? filteredAddCandidates.value : [])
const visibleDropCandidates = computed(() => requestOpen.value ? filteredDropCandidates.value : [])

const pendingCount = computed(() => requestList.value.filter((item: any) => String(item.requestStatus) === '0').length)
const approvedCount = computed(() => requestList.value.filter((item: any) => String(item.requestStatus) === '1').length)

const planStageLabel = computed(() => {
  if (requestOpen.value) return '申请开放中'
  if (hasPlan.value) return '暂未开放'
  return '未发布计划'
})

const planStageTone = computed(() => {
  if (requestOpen.value) return 'success'
  if (hasPlan.value) return 'warning'
  return 'muted'
})

const unavailableTitle = computed(() => '当前还未开放选课申请')

const unavailableDescription = computed(() => {
  return '本学期申请计划已发布，但当前不在个性化申请开放时间段内，请等待开放后再提交。'
})

const reviewNoticeText = computed(() => pendingCount.value ? `待审核 ${pendingCount.value} 条` : '结果会同步推送到消息中心')

const planWindows = computed(() => [
  { key: 'request', label: '申请开放', range: formatDateRange(planInfo.value?.requestStartTime, planInfo.value?.requestEndTime) },
  { key: 'selection', label: '标准选课', range: formatDateRange(planInfo.value?.selectionStartTime, planInfo.value?.selectionEndTime) },
  { key: 'drop', label: '标准退课', range: formatDateRange(planInfo.value?.dropStartTime, planInfo.value?.dropEndTime) },
])

const unavailableTips = computed(() => {
  return planWindows.value.map((item) => ({ label: item.label, value: item.range }))
})

const noPlanTips = computed(() => [
  { label: '当前状态', value: '未发布个性化申请计划' },
  { label: '审核结果', value: '后续会同步推送到消息中心' },
])

const dialogTitle = computed(() => requestForm.requestType === 'DROP' ? '发起个性化退课申请' : '发起个性化选课申请')

function normalizeTab(tab: unknown): 'add' | 'drop' | 'mine' {
  if (tab === 'drop' || tab === 'mine') return tab
  return 'add'
}

function parseTermId(termId: unknown) {
  if (termId == null || termId === '') return undefined
  const parsed = Number(termId)
  return Number.isFinite(parsed) ? parsed : undefined
}

function statusLabel(value?: string) {
  return value === '1' ? '已通过' : value === '2' ? '已驳回' : value === '3' ? '已撤回' : '待审核'
}

function statusTagType(value?: string) {
  return value === '1' ? 'success' : value === '2' ? 'danger' : value === '3' ? 'info' : 'warning'
}

function requestTypeLabel(value?: string) {
  return value === 'DROP' ? '退课申请' : '选课申请'
}

function statusCount(status: string) {
  return requestList.value.filter((item: any) => String(item.requestStatus) === status).length
}

function pendingDays(row: any) {
  if (!row.createTime) return 0
  return Math.floor((Date.now() - new Date(row.createTime).getTime()) / 86400000)
}

function hasPendingRequest(row: any) {
  const targetId = row.id || row.targetClassCourseId
  if (!targetId) return false
  return requestList.value.some((req: any) =>
    String(req.requestStatus) === '0'
    && (req.targetClassCourseId === targetId || req.id === targetId),
  )
}

function formatDateValue(value?: string) {
  if (!value) return '--'
  const text = String(value).replace('T', ' ')
  return text.length >= 16 ? text.slice(0, 16) : text
}

function formatDateRange(startTime?: string, endTime?: string) {
  if (!startTime && !endTime) return '暂未配置'
  return `${formatDateValue(startTime)} ~ ${formatDateValue(endTime)}`
}

function resolveTextValue(value: any) {
  if (value == null) return ''
  if (typeof value === 'string') return value
  if (typeof value === 'object') return value.text || value.textZh || value.textEn || ''
  return String(value)
}

function resolveScheduleText(row: any) {
  return resolveTextValue(row.scheduleText || row.scheduleVm?.dateTimeText).replace(/;\s*\n/g, '\n').trim() || '待排课'
}

function resolveScheduleRoom(row: any) {
  return resolveTextValue(row.classroom || row.scheduleVm?.roomSeatText).replace(/;\s*\n/g, ' / ').trim() || '未安排教室'
}

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
  const current = termOptions.value.find((item: any) => item.isCurrent === '1')
  if (!queryParams.termId && current) queryParams.termId = current.value
}

async function loadRequests() {
  const userId = userStore.user?.userId
  if (!userId) return
  const res = await listPortalPersonalizedSelectionRequests({ userId, termId: queryParams.termId })
  requestList.value = res.data || []
}

async function loadCandidates() {
  const userId = userStore.user?.userId
  if (!userId) return
  const res = await listPortalPersonalizedSelectionOptions({ userId, termId: queryParams.termId })
  planInfo.value = res.data?.plan || null
  requestOpen.value = Boolean(res.data?.requestOpen)
  selectionOpen.value = Boolean(res.data?.selectionOpen)
  dropOpen.value = Boolean(res.data?.dropOpen)
  addCandidateList.value = res.data?.addOptions || []
  dropCandidateList.value = res.data?.dropOptions || []
}

async function loadAll() {
  loading.value = true
  try {
    await Promise.all([loadRequests(), loadCandidates()])
    if (!requestOpen.value && requestList.value.length && activeTab.value !== 'mine') {
      activeTab.value = 'mine'
    }
  } finally {
    loading.value = false
  }
}

function openCreateDialog(row?: any, requestType: 'ADD' | 'DROP' = 'ADD') {
  requestForm.requestReason = ''
  requestForm.remark = ''
  requestForm.requestType = requestType
  selectedCandidate.value = row || null
  createDialogOpen.value = true
}

async function submitRequest() {
  const userId = userStore.user?.userId
  if (!userId || !selectedCandidate.value?.id) return
  if (!String(requestForm.requestReason || '').trim()) {
    ElMessage.warning('请先填写申请理由')
    return
  }
  if (!requestOpen.value) {
    ElMessage.warning('当前不在个性化申请开放时间')
    return
  }
  await createPortalPersonalizedSelectionRequest({
    studentUserId: userId,
    targetClassCourseId: selectedCandidate.value.id,
    requestType: requestForm.requestType,
    requestReason: requestForm.requestReason,
    remark: requestForm.remark,
  })
  createDialogOpen.value = false
  activeTab.value = 'mine'
  await loadAll()
  ElMessage.success(requestForm.requestType === 'DROP' ? '个性化退课申请已提交' : '个性化选课申请已提交')
}

async function cancelRequest(row: any) {
  const userId = userStore.user?.userId
  if (!userId || !row?.requestId) return
  await ElMessageBox.confirm(`确认撤回申请「${row.requestNo || row.targetCourseName || '当前申请'}」吗？`, '提示', { type: 'warning' })
  await cancelPortalPersonalizedSelectionRequest(row.requestId, userId)
  await loadAll()
  ElMessage.success('申请已撤回')
}

function previewClassCourse(row: any) {
  router.push({
    path: '/student/class-courses',
    query: {
      openCourseId: String(row.targetClassCourseId || row.id),
      ...(queryParams.termId ? { termId: String(queryParams.termId) } : {}),
    },
  })
}

function goCourseSelection() {
  router.push({
    path: '/student/selection',
    query: queryParams.termId ? { termId: String(queryParams.termId), tab: 'add' } : { tab: 'add' },
  })
}

function goMessages() {
  router.push('/student/messages?tab=message')
}

function syncRouteTab(tab: unknown) {
  if (tab == null || tab === '') return
  const nextTab = normalizeTab(tab)
  if (activeTab.value !== nextTab) activeTab.value = nextTab
}

async function syncRouteTerm(termId: unknown) {
  const nextTermId = parseTermId(termId)
  if (nextTermId == null || parseTermId(queryParams.termId) === nextTermId) {
    return
  }
  queryParams.termId = nextTermId
  if (initialized.value) {
    await loadAll()
  }
}

watch(
  () => route.query.tab,
  (tab) => { syncRouteTab(tab) },
  { immediate: true },
)

watch(
  () => route.query.termId,
  (termId) => { void syncRouteTerm(termId) },
  { immediate: true },
)

onMounted(async () => {
  try {
    await loadTerms()
    await loadAll()
    initialized.value = true
  } finally {
    initialLoading.value = false
  }
})
</script>

<style scoped>
.personalized-selection-page {
  padding: 24px 32px;
}

.personalized-shell {
  min-height: 420px;
  overflow: hidden;
}

.personalized-shell__header {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(320px, 0.8fr);
  gap: 16px;
  padding: 20px 22px;
  border-bottom: 1px solid #eef2f7;
  background: linear-gradient(180deg, #ffffff 0%, #fff9f3 100%);
}

.personalized-shell__eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: #ffedd5;
  color: #c2410c;
  font-size: 12px;
  font-weight: 700;
}

.personalized-shell__title h4 {
  margin: 12px 0 6px;
  font-size: 28px;
  color: #0f172a;
}

.personalized-shell__title p {
  margin: 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.personalized-shell__stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.personalized-stat {
  padding: 14px;
  border-radius: 14px;
  background: #ffffff;
  border: 1px solid #f2ddcf;
}

.personalized-stat span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.personalized-stat strong {
  display: block;
  margin-top: 8px;
  color: #0f172a;
  font-size: 20px;
  line-height: 1.3;
}

.personalized-stat strong.is-success {
  color: #0f766e;
}

.personalized-stat strong.is-warning {
  color: #b45309;
}

.personalized-stat strong.is-muted {
  color: #64748b;
}

.personalized-empty-state {
  padding: 36px 24px 28px;
}

.personalized-empty-state__desc p {
  margin: 0 0 18px;
  color: #64748b;
  font-size: 14px;
}

.personalized-window-list {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.personalized-window-item {
  min-width: 220px;
  padding: 12px 14px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  text-align: left;
}

.personalized-window-item span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.personalized-window-item strong {
  display: block;
  margin-top: 6px;
  color: #0f172a;
  font-size: 14px;
  line-height: 1.6;
}

.personalized-empty-state__actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 12px;
}

.personalized-notice-bar,
.personalized-toolbar,
.personalized-tabs {
  padding-left: 22px;
  padding-right: 22px;
}

.personalized-notice-bar {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  padding-top: 18px;
}

.personalized-notice-bar__item {
  padding: 12px 14px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.personalized-notice-bar__item span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.personalized-notice-bar__item strong {
  display: block;
  margin-top: 6px;
  color: #0f172a;
  font-size: 14px;
  line-height: 1.6;
}

.personalized-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  padding-top: 18px;
}

.personalized-toolbar--inner {
  padding: 0 0 16px;
}

.personalized-toolbar__left,
.personalized-toolbar__right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.personalized-tabs {
  padding-bottom: 22px;
}

.request-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.request-cell strong {
  color: #0f172a;
  font-size: 14px;
  font-weight: 700;
}

.request-cell span {
  color: #64748b;
  font-size: 12px;
  line-height: 1.6;
}

.request-cell--schedule strong {
  white-space: pre-line;
  line-height: 1.7;
}

.request-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  flex-wrap: wrap;
}

.request-create-layout {
  display: grid;
  gap: 16px;
}

.request-target-card {
  display: grid;
  gap: 6px;
  padding: 16px;
  border-radius: 16px;
  border: 1px solid #f2ddcf;
  background: #fff7ed;
}

.request-target-card__header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.request-target-card strong {
  color: #0f172a;
  font-size: 16px;
}

.request-target-card span,
.request-target-card p {
  margin: 0;
  color: #64748b;
  font-size: 12px;
  line-height: 1.7;
}

@media (max-width: 960px) {
  .personalized-selection-page {
    padding: 20px;
  }

  .personalized-shell__header,
  .personalized-notice-bar {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .personalized-notice-bar,
  .personalized-toolbar,
  .personalized-tabs {
    padding-left: 16px;
    padding-right: 16px;
  }

  .personalized-shell__stats {
    grid-template-columns: 1fr;
  }
}
</style>
