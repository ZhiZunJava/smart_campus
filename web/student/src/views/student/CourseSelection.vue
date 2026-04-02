<template>
  <div class="portal-page course-selection-page">
    <div class="portal-section-title">
      <h3>选课中心</h3>
    </div>

    <section
      v-loading="initialLoading"
      class="portal-card selection-shell"
      element-loading-text="正在加载选课信息..."
      element-loading-background="rgba(255, 255, 255, 0.84)"
    >
      <template v-if="!initialLoading">
        <PlanUnavailableState
          v-if="!hasPlan"
          badge-text="未发布计划"
          title="当前选课中心未开启"
          description="教务尚未发布本学期标准选课计划，请等待计划发布后再进入选课中心办理。"
          :tips="noPlanTips"
        >
          <template #actions>
            <el-button type="primary" @click="goClassCourses">我的班级课程</el-button>
            <el-button plain @click="goPersonalizedSelection">选课申请</el-button>
          </template>
        </PlanUnavailableState>

        <template v-else>
          <div class="selection-shell__header">
            <div class="selection-shell__title">
              <span class="selection-shell__eyebrow">标准选课</span>
              <h4>{{ currentTermLabel }}</h4>
              <p>用于办理无需审核的直接选课与直接退课。</p>
            </div>
            <div class="selection-shell__stats">
              <div class="selection-stat">
                <span>当前状态</span>
                <strong :class="`is-${planStageTone}`">{{ planStageLabel }}</strong>
              </div>
              <div class="selection-stat">
                <span>已选课程</span>
                <strong>{{ selectionStats.selectedCount || 0 }}</strong>
              </div>
              <div class="selection-stat">
                <span>本班必修</span>
                <strong>{{ selectionStats.requiredCount || 0 }}</strong>
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
              <el-button type="primary" @click="goClassCourses">我的班级课程</el-button>
              <el-button plain @click="goPersonalizedSelection">选课申请</el-button>
            </template>
          </PlanUnavailableState>

          <template v-else>
          <div class="selection-notice-bar">
            <div class="selection-notice-bar__item" :class="{ 'is-active': selectionOpen }">
              <span>
                <i v-if="selectionOpen" class="notice-pulse"></i>
                计划名称
              </span>
              <strong>{{ planInfo?.planName || '当前选课计划' }}</strong>
            </div>
            <div class="selection-notice-bar__item" :class="{ 'is-active': selectionOpen }">
              <span>
                <i v-if="selectionOpen" class="notice-pulse"></i>
                直接选课
              </span>
              <strong>{{ formatDateRange(planInfo?.selectionStartTime, planInfo?.selectionEndTime) }}</strong>
            </div>
            <div class="selection-notice-bar__item" :class="{ 'is-active': dropOpen }">
              <span>
                <i v-if="dropOpen" class="notice-pulse"></i>
                直接退课
              </span>
              <strong>{{ formatDateRange(planInfo?.dropStartTime, planInfo?.dropEndTime) }}</strong>
            </div>
            <div class="selection-notice-bar__item">
              <span>选课申请</span>
              <strong>{{ formatDateRange(planInfo?.requestStartTime, planInfo?.requestEndTime) }}</strong>
            </div>
          </div>

          <div class="selection-toolbar">
            <div class="selection-toolbar__left">
              <el-select
                v-model="queryParams.termId"
                filterable
                clearable
                placeholder="切换学期"
                style="width: 240px"
                @change="loadSelectionOptions"
              >
                <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
              <el-input v-model="searchInput" clearable placeholder="搜索课程、班级、教师或教学班代码" style="width: 340px" @input="onSearchInput">
                <template #prefix><i class="ri-search-line"></i></template>
              </el-input>
            </div>
            <div class="selection-toolbar__right">
              <el-button-group class="selection-view-switch">
                <el-button
                  :type="viewMode === 'schedule' ? 'primary' : 'default'"
                  :plain="viewMode !== 'schedule'"
                  @click="viewMode = 'schedule'"
                >
                  课表选课
                </el-button>
                <el-button
                  :type="viewMode === 'list' ? 'primary' : 'default'"
                  :plain="viewMode !== 'list'"
                  @click="viewMode = 'list'"
                >
                  列表选课
                </el-button>
              </el-button-group>
              <el-button :icon="Refresh" circle @click="loadSelectionOptions" title="刷新数据" />
              <el-button plain @click="goClassCourses">我的班级课程</el-button>
              <el-button type="primary" plain @click="goPersonalizedSelection">选课申请</el-button>
            </div>
          </div>

          <CourseSelectionScheduleBoard
            v-if="viewMode === 'schedule'"
            :courses="scheduleBoardCourses"
            :selected-courses="selectedCourses"
            :time-table-layout="timeTableLayout"
            :refresh-courses="loadSelectionOptions"
            :poll-course-counts="refreshCourseSelectionStudentCounts"
            :query-courses="queryCourseSelectionOptions"
            @select-course="handleSelectCourse"
            @drop-course="handleDropCourse"
            @preview-course="previewClassCourse"
            @switch-list="viewMode = 'list'"
          />

          <el-tabs v-else v-model="activeTab" class="selection-tabs">
            <el-tab-pane :label="`直接选课 (${filteredAddCourses.length})`" name="add">
              <el-table v-loading="loading" :data="filteredAddCourses" max-height="620" stripe>
                <el-table-column label="课程 / 教学班" min-width="300">
                  <template #default="{ row }">
                    <div class="selection-cell">
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
                    <div class="selection-cell selection-cell--schedule">
                      <strong>{{ resolveScheduleText(row) }}</strong>
                      <span>{{ resolveScheduleRoom(row) }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="课程信息" min-width="220">
                  <template #default="{ row }">
                    <div class="selection-cell">
                      <strong>{{ row.courseCategory || row.subjectType || '通用课程' }}</strong>
                      <span>{{ row.weeklyHours ?? '-' }} 周学时 / {{ row.totalHours ?? '-' }} 总学时</span>
                      <span>{{ row.assessmentType || '未定考核' }}{{ row.teachingLanguage ? ` / ${row.teachingLanguage}` : '' }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="状态" min-width="190">
                  <template #default="{ row }">
                    <div class="selection-cell">
                      <strong>{{ row.requiredSelection ? '本班必修' : '可直接选课' }}</strong>
                      <span>{{ row.actionDisabledReason || '满足条件即可直接选课' }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="容量" min-width="170">
                  <template #default="{ row }">
                    <div class="selection-cell">
                      <strong>{{ row.selectedStudentCount ?? row.actualStudentCount ?? 0 }} / {{ row.studentLimit || '-' }}</strong>
                      <el-progress
                        v-if="row.studentLimit"
                        :percentage="capacityPercent(row)"
                        :color="capacityColor(row)"
                        :stroke-width="6"
                        :show-text="false"
                        style="margin-top: 4px"
                      />
                      <span v-if="row.studentLimit && capacityPercent(row) >= 100" style="color: #ef4444; font-weight: 600;">已满</span>
                      <span v-else-if="row.remainingSeats != null">剩余 {{ row.remainingSeats }}</span>
                      <span v-else>不限制人数</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="220" fixed="right">
                  <template #default="{ row }">
                    <div class="selection-actions">
                      <el-button link type="primary" @click.stop="previewClassCourse(row)">查看教学班</el-button>
                      <el-button type="primary" size="small" :disabled="!row.canSelect" :loading="row.__selecting" @click.stop="handleSelectCourse(row)">立即选课</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loading && !filteredAddCourses.length" description="当前学期暂无可直接选课的教学班" />
            </el-tab-pane>

            <el-tab-pane :label="`直接退课 (${filteredDropCourses.length})`" name="drop">
              <el-table v-loading="loading" :data="filteredDropCourses" max-height="620" stripe>
                <el-table-column label="已选教学班" min-width="300">
                  <template #default="{ row }">
                    <div class="selection-cell">
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
                    <div class="selection-cell selection-cell--schedule">
                      <strong>{{ resolveScheduleText(row) }}</strong>
                      <span>{{ resolveScheduleRoom(row) }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="退课提示" min-width="220">
                  <template #default="{ row }">
                    <div class="selection-cell">
                      <strong>{{ row.requiredSelection ? '本班必修' : '可直接退课' }}</strong>
                      <span>{{ row.actionDisabledReason || '满足条件即可直接退课' }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="220" fixed="right">
                  <template #default="{ row }">
                    <div class="selection-actions">
                      <el-button link type="primary" @click.stop="previewClassCourse(row)">查看教学班</el-button>
                      <el-button type="danger" size="small" plain :disabled="!row.canDrop" :loading="row.__dropping" @click.stop="handleDropCourse(row)">立即退课</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!loading && !filteredDropCourses.length" description="当前学期暂无可直接退课的教学班" />
            </el-tab-pane>
          </el-tabs>
          </template>
        </template>
      </template>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import {
  dropPortalCourse,
  getPortalCourseSelectionOptions,
  getPortalCourseSelectionStudentCounts,
  listPortalTermOptions,
  selectPortalCourse,
} from '@/api/portal'
import usePortalUserStore from '@/store/user'
import PlanUnavailableState from '@/components/selection/PlanUnavailableState.vue'
import CourseSelectionScheduleBoard from '@/components/selection/CourseSelectionScheduleBoard.vue'

defineOptions({ name: 'CourseSelection' })

const route = useRoute()
const router = useRouter()
const userStore = usePortalUserStore()

const initialLoading = ref(true)
const initialized = ref(false)
const loading = ref(false)
const termOptions = ref<any[]>([])
const allCourses = ref<any[]>([])
const selectedCourses = ref<any[]>([])
const selectionStats = ref<any>({})
const timeTableLayout = ref<any>(null)
const keyword = ref('')
const searchInput = ref('')
let searchTimer: ReturnType<typeof setTimeout> | null = null
function onSearchInput(val: string) {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { keyword.value = val }, 300)
}
const activeTab = ref<'add' | 'drop'>('add')
const viewMode = ref<'list' | 'schedule'>('list')
const planInfo = ref<any>(null)
const selectionOpen = ref(false)
const dropOpen = ref(false)
const queryParams = reactive<any>({ termId: undefined })
const LIST_COUNT_POLLING_INTERVAL = 5000
const COUNT_QUERY_BATCH_SIZE = 200
let listCountPollingTimer: ReturnType<typeof setInterval> | null = null
let listCountPollingBusy = false

const currentTermLabel = computed(() => termOptions.value.find((item: any) => item.value === queryParams.termId)?.label || '当前学期')
const hasPlan = computed(() => Boolean(planInfo.value?.enabled))
const showClosedState = computed(() => hasPlan.value && !selectionOpen.value && !dropOpen.value)
const shouldPollListCounts = computed(() => !initialLoading.value && viewMode.value === 'list' && hasPlan.value && allCourses.value.length > 0)

const filteredAddCourses = computed(() => {
  const keywordValue = keyword.value.trim().toLowerCase()
  return allCourses.value.filter((item: any) => {
    if (item.selected) return false
    if (!keywordValue) return true
    return matchesCourseKeyword(item, keywordValue)
  })
})

const filteredDropCourses = computed(() => {
  const keywordValue = keyword.value.trim().toLowerCase()
  return selectedCourses.value.filter((item: any) => {
    if (!keywordValue) return true
    return matchesCourseKeyword(item, keywordValue)
  })
})

const scheduleBoardCourses = computed(() => {
  const keywordValue = keyword.value.trim().toLowerCase()
  return allCourses.value.filter((item: any) => {
    if (!keywordValue) return true
    return Boolean(item.selected) || matchesCourseKeyword(item, keywordValue)
  })
})

const planStageLabel = computed(() => {
  if (selectionOpen.value) return '选课开放中'
  if (dropOpen.value) return '退课开放中'
  if (hasPlan.value) return '暂未开放'
  return '未发布计划'
})

const planStageTone = computed(() => {
  if (selectionOpen.value) return 'success'
  if (dropOpen.value) return 'danger'
  if (hasPlan.value) return 'warning'
  return 'muted'
})

const unavailableTitle = computed(() => '当前还未开放选课')

const unavailableDescription = computed(() => {
  return '本学期选课计划已发布，但当前不在直接选课或直接退课时间段内，请关注开放时间。'
})

const planWindows = computed(() => [
  { key: 'selection', label: '直接选课', range: formatDateRange(planInfo.value?.selectionStartTime, planInfo.value?.selectionEndTime) },
  { key: 'drop', label: '直接退课', range: formatDateRange(planInfo.value?.dropStartTime, planInfo.value?.dropEndTime) },
  { key: 'request', label: '选课申请', range: formatDateRange(planInfo.value?.requestStartTime, planInfo.value?.requestEndTime) },
])

const unavailableTips = computed(() => {
  return planWindows.value.map((item) => ({ label: item.label, value: item.range }))
})

const noPlanTips = computed(() => [
  { label: '当前状态', value: '未发布标准选课计划' },
  { label: '建议操作', value: '可先查看班级课程或前往选课申请' },
])

function normalizeTab(tab: unknown): 'add' | 'drop' {
  return tab === 'drop' ? 'drop' : 'add'
}

function parseTermId(termId: unknown) {
  if (termId == null || termId === '') return undefined
  const parsed = Number(termId)
  return Number.isFinite(parsed) ? parsed : undefined
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

function capacityPercent(row: any) {
  const current = row.selectedStudentCount ?? row.actualStudentCount ?? 0
  const limit = row.studentLimit || 0
  if (limit <= 0) return 0
  return Math.min(100, Math.round((current / limit) * 100))
}

function capacityColor(row: any) {
  const pct = capacityPercent(row)
  if (pct >= 90) return '#ef4444'
  if (pct >= 70) return '#f59e0b'
  return '#10b981'
}

function resolveTextValue(value: any) {
  if (value == null) return ''
  if (typeof value === 'string') return value
  if (typeof value === 'object') return value.text || value.textZh || value.textEn || ''
  return String(value)
}

function isVirtualVenueText(value: any) {
  const text = String(value || '').trim()
  if (!text) return true
  return ['空场地', '空教室', '不占用教室', '待定教室', '未安排教室'].some((keyword) => text.includes(keyword))
}

function matchesCourseKeyword(item: any, keywordValue: string) {
  return String(item.courseName || '').toLowerCase().includes(keywordValue)
    || String(item.courseCode || '').toLowerCase().includes(keywordValue)
    || String(item.className || '').toLowerCase().includes(keywordValue)
    || String(item.teacherName || '').toLowerCase().includes(keywordValue)
    || String(item.teachingClassCode || '').toLowerCase().includes(keywordValue)
    || String(item.selectionOptionName || '').toLowerCase().includes(keywordValue)
}

function resolveScheduleText(row: any) {
  return resolveTextValue(row.scheduleText || row.scheduleVm?.dateTimeText).replace(/;\s*\n/g, '\n').trim() || '待排课'
}

function resolveScheduleRoom(row: any) {
  const text = resolveTextValue(row.classroom || row.scheduleVm?.roomSeatText).replace(/;\s*\n/g, ' / ').trim()
  return isVirtualVenueText(text) ? '' : text
}

function normalizeLessonIds(rawIds?: Array<number | string | null | undefined>) {
  const seen = new Set<number>()
  const result: number[] = []
  ;(rawIds || []).forEach((item) => {
    const parsed = Number(item)
    if (!Number.isFinite(parsed) || parsed <= 0 || seen.has(parsed)) return
    seen.add(parsed)
    result.push(parsed)
  })
  return result
}

function resolveCourseLessonIds(rows: any[]) {
  return normalizeLessonIds((rows || []).map((item: any) => item?.id))
}

function updateCourseCountState(row: any, snapshot: any) {
  if (!row || !snapshot) return
  const selectedStudentCount = Number(snapshot.selectedStudentCount ?? snapshot.actualStudentCount ?? 0)
  const studentLimit = snapshot.studentLimit != null ? Number(snapshot.studentLimit) : Number(row.studentLimit || 0)
  const remainingSeats = snapshot.remainingSeats != null
    ? Number(snapshot.remainingSeats)
    : (studentLimit > 0 ? Math.max(studentLimit - selectedStudentCount, 0) : null)
  const isFull = studentLimit > 0 && remainingSeats != null && remainingSeats <= 0
  const currentReason = String(row.actionDisabledReason || '').trim()
  const reasonIsCapacity = currentReason === '人数已满'

  row.selectedStudentCount = selectedStudentCount
  row.actualStudentCount = selectedStudentCount
  row.studentLimit = snapshot.studentLimit ?? row.studentLimit
  row.remainingSeats = remainingSeats
  row.capacityFull = isFull
  row.selectedCount = selectedStudentCount
  row.studentCountText = studentLimit > 0 ? `${selectedStudentCount}/${studentLimit}` : String(selectedStudentCount)

  if (!row.selected) {
    if (isFull) {
      if (row.canSelect || reasonIsCapacity || !currentReason) {
        row.canSelect = false
        row.actionDisabledReason = '人数已满'
      }
    } else if (reasonIsCapacity) {
      row.canSelect = true
      row.actionDisabledReason = ''
    }
  }
}

function applyCourseCountSnapshots(items: any[]) {
  const snapshotMap = new Map<string, any>()
  ;(items || []).forEach((item: any) => {
    const id = item?.classCourseId ?? item?.lessonId
    if (id != null) {
      snapshotMap.set(String(id), item)
    }
  })
  ;[allCourses.value, selectedCourses.value].forEach((rows) => {
    ;(rows || []).forEach((row: any) => {
      const snapshot = snapshotMap.get(String(row?.id))
      if (snapshot) {
        updateCourseCountState(row, snapshot)
      }
    })
  })
}

async function refreshCourseSelectionStudentCounts(lessonIds?: Array<number | string>) {
  const userId = userStore.user?.userId
  if (!userId) return []
  const ids = normalizeLessonIds(lessonIds?.length ? lessonIds : resolveCourseLessonIds(allCourses.value))
  if (!ids.length) return []
  const requests: Promise<any>[] = []
  for (let index = 0; index < ids.length; index += COUNT_QUERY_BATCH_SIZE) {
    requests.push(getPortalCourseSelectionStudentCounts({
      userId,
      lessonIds: ids.slice(index, index + COUNT_QUERY_BATCH_SIZE),
    }))
  }
  const responses = await Promise.all(requests)
  const items = responses.flatMap((res: any) => res.data?.items || [])
  applyCourseCountSnapshots(items)
  return items
}

async function pollListCourseCounts() {
  if (listCountPollingBusy) return
  listCountPollingBusy = true
  try {
    await refreshCourseSelectionStudentCounts()
  } finally {
    listCountPollingBusy = false
  }
}

function stopListCountPolling() {
  if (listCountPollingTimer) {
    clearInterval(listCountPollingTimer)
    listCountPollingTimer = null
  }
}

function startListCountPolling() {
  if (listCountPollingTimer) return
  listCountPollingTimer = setInterval(() => {
    void pollListCourseCounts()
  }, LIST_COUNT_POLLING_INTERVAL)
}

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
  const current = termOptions.value.find((item: any) => item.isCurrent === '1')
  if (!queryParams.termId && current) {
    queryParams.termId = current.value
  }
}

async function loadSelectionOptions() {
  const data = await queryCourseSelectionOptions()
  planInfo.value = data?.plan || null
  selectionOpen.value = Boolean(data?.selectionOpen)
  dropOpen.value = Boolean(data?.dropOpen)
  allCourses.value = data?.availableCourses || []
  selectedCourses.value = data?.selectedCourses || []
  selectionStats.value = data?.stats || {}
  timeTableLayout.value = data?.timeTableLayout || null
}

async function queryCourseSelectionOptions(extraParams: Record<string, any> = {}) {
  const userId = userStore.user?.userId
  if (!userId) return null
  const shouldShowLoading = !extraParams || !Object.keys(extraParams).length
  if (shouldShowLoading) {
    loading.value = true
  }
  try {
    const res = await getPortalCourseSelectionOptions({ userId, termId: queryParams.termId, ...extraParams })
    return res.data || {}
  } finally {
    if (shouldShowLoading) {
      loading.value = false
    }
  }
}

async function handleSelectCourse(row: any) {
  const userId = userStore.user?.userId
  if (!userId || !row?.id) return
  row.__selecting = true
  try {
    await selectPortalCourse({ userId, classCourseId: row.id })
    await loadSelectionOptions()
    ElMessage.success(`已加入《${row.courseName || '当前课程'}》`)
  } finally {
    row.__selecting = false
  }
}

async function handleDropCourse(row: any) {
  const userId = userStore.user?.userId
  if (!userId || !row?.id) return
  await ElMessageBox.confirm(`确认退选《${row.courseName || '当前课程'}》吗？`, '提示', { type: 'warning' })
  row.__dropping = true
  try {
    await dropPortalCourse({ userId, classCourseId: row.id })
    await loadSelectionOptions()
    ElMessage.success(`已退选《${row.courseName || '当前课程'}》`)
  } finally {
    row.__dropping = false
  }
}

function previewClassCourse(row: any) {
  router.push({
    path: '/student/class-courses',
    query: {
      openCourseId: String(row.id),
      ...(queryParams.termId ? { termId: String(queryParams.termId) } : {}),
    },
  })
}

function goClassCourses() {
  router.push({
    path: '/student/class-courses',
    query: queryParams.termId ? { termId: String(queryParams.termId) } : {},
  })
}

function goPersonalizedSelection() {
  router.push({
    path: '/student/personalized-selection',
    query: {
      ...(queryParams.termId ? { termId: String(queryParams.termId) } : {}),
      tab: 'add',
    },
  })
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
    await loadSelectionOptions()
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

watch(
  shouldPollListCounts,
  (enabled) => {
    if (enabled) {
      void pollListCourseCounts()
      startListCountPolling()
      return
    }
    stopListCountPolling()
  },
  { immediate: true },
)

onMounted(async () => {
  try {
    await loadTerms()
    await loadSelectionOptions()
    initialized.value = true
  } finally {
    initialLoading.value = false
  }
})

onBeforeUnmount(() => {
  stopListCountPolling()
  if (searchTimer) {
    clearTimeout(searchTimer)
    searchTimer = null
  }
})
</script>

<style scoped>
.course-selection-page {
  padding: 24px 32px;
}

.selection-shell {
  min-height: 420px;
  overflow: hidden;
}

.selection-shell__header {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(320px, 0.8fr);
  gap: 16px;
  padding: 20px 22px;
  border-bottom: 1px solid #eef2f7;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
}

.selection-shell__eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 4px;
  background: #eaf2ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
}

.selection-shell__title h4 {
  margin: 12px 0 6px;
  font-size: 28px;
  color: #0f172a;
}

.selection-shell__title p {
  margin: 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.selection-shell__stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.selection-stat {
  padding: 14px;
  border-radius: 4px;
  background: #ffffff;
  border: 1px solid #dbe7f5;
}

.selection-stat span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.selection-stat strong {
  display: block;
  margin-top: 8px;
  color: #0f172a;
  font-size: 20px;
  line-height: 1.3;
}

.selection-stat strong.is-success {
  color: #0f766e;
}

.selection-stat strong.is-danger {
  color: #dc2626;
}

.selection-stat strong.is-warning {
  color: #b45309;
}

.selection-stat strong.is-muted {
  color: #64748b;
}

.selection-empty-state {
  padding: 36px 24px 28px;
}

.selection-empty-state__desc p {
  margin: 0 0 18px;
  color: #64748b;
  font-size: 14px;
}

.selection-window-list {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.selection-window-item {
  min-width: 220px;
  padding: 12px 14px;
  border-radius: 4px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  text-align: left;
}

.selection-window-item span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.selection-window-item strong {
  display: block;
  margin-top: 6px;
  color: #0f172a;
  font-size: 14px;
  line-height: 1.6;
}

.selection-empty-state__actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 12px;
}

.selection-notice-bar,
.selection-toolbar,
.selection-tabs {
  padding-left: 22px;
  padding-right: 22px;
}

.selection-notice-bar {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  padding-top: 18px;
}

.selection-notice-bar__item {
  padding: 12px 14px;
  border-radius: 4px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  transition: border-color 0.3s, background 0.3s;
}

.selection-notice-bar__item.is-active {
  border-color: #93c5fd;
  background: #eff6ff;
}

.notice-pulse {
  display: inline-block;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #3b82f6;
  margin-right: 4px;
  vertical-align: middle;
  animation: notice-pulse-anim 1.5s ease-in-out infinite;
}

@keyframes notice-pulse-anim {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.4; transform: scale(0.8); }
}

.selection-notice-bar__item span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.selection-notice-bar__item strong {
  display: block;
  margin-top: 6px;
  color: #0f172a;
  font-size: 14px;
  line-height: 1.6;
}

.selection-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  padding-top: 18px;
}

.selection-toolbar__left,
.selection-toolbar__right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.selection-view-switch {
  box-shadow: none;
}

.selection-view-switch :deep(.el-button) {
  border-radius: 4px;
  padding-left: 18px;
  padding-right: 18px;
}

.selection-view-switch :deep(.el-button + .el-button) {
  margin-left: -1px;
}

.selection-view-switch :deep(.el-button:first-child) {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.selection-view-switch :deep(.el-button:last-child) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

.selection-view-switch :deep(.el-button:not(:first-child):not(:last-child)) {
  border-radius: 0;
}

.selection-tabs {
  padding-bottom: 22px;
}

.selection-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.selection-cell strong {
  color: #0f172a;
  font-size: 14px;
  font-weight: 700;
}

.selection-cell span {
  color: #64748b;
  font-size: 12px;
  line-height: 1.6;
}

.selection-cell--schedule strong {
  white-space: pre-line;
  line-height: 1.7;
}

.selection-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  flex-wrap: wrap;
}

@media (max-width: 960px) {
  .course-selection-page {
    padding: 20px;
  }

  .selection-shell__header,
  .selection-notice-bar {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .selection-notice-bar,
  .selection-toolbar,
  .selection-tabs {
    padding-left: 16px;
    padding-right: 16px;
  }

  .selection-shell__stats {
    grid-template-columns: 1fr;
  }
}
</style>
