<template>
  <div class="selection-schedule-board">
    <div class="selection-schedule-board__hero">
      <div class="selection-schedule-board__intro">
        <span class="selection-schedule-board__eyebrow">课表选课</span>
        <h4>把选课放到课表里看</h4>
        <p>按星期与节次查看已选课程、待选教学班和时段冲突，点任意课程格即可展开该时段候选课。</p>
      </div>
      <div class="selection-schedule-board__stats">
        <div class="selection-schedule-board__stat">
          <span>已占用时段</span>
          <strong>{{ boardSummary.selectedSlotCount }}</strong>
        </div>
        <div class="selection-schedule-board__stat">
          <span>待选时段</span>
          <strong>{{ boardSummary.availableSlotCount }}</strong>
        </div>
        <div class="selection-schedule-board__stat">
          <span>时间重叠</span>
          <strong>{{ boardSummary.conflictSlotCount }}</strong>
        </div>
        <div class="selection-schedule-board__stat">
          <span>待排课程</span>
          <strong>{{ unscheduledCourses.length }}</strong>
        </div>
      </div>
    </div>

    <div class="selection-schedule-board__legend">
      <span class="selection-schedule-board__legend-chip is-selected">已选课程</span>
      <span class="selection-schedule-board__legend-chip is-available">可选课程</span>
      <span class="selection-schedule-board__legend-chip is-mixed">当前时段还有备选</span>
      <span class="selection-schedule-board__legend-chip is-conflict">含时间重叠</span>
    </div>

    <div class="selection-schedule-board__table-wrap table-container">
      <table class="selection-schedule-board__table courseTable" cellspacing="0" cellpadding="0">
        <thead>
          <tr>
            <th width="4.8%" class="selection-schedule-board__section-head courseTable__section-head">节次</th>
            <th v-for="day in weekDays" :key="`schedule-head-${day.value}`" width="13.6%">
              {{ day.label }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in tableRows" :key="`schedule-row-${row.key}`" :class="row.rowClass">
            <td class="selection-schedule-board__section-unit dayPartUnit" :style="{ background: row.sideColor }">
              <div class="selection-schedule-board__section-content">
                <span v-if="isDayPartStart(row) && !isBreakRow(row)" class="selection-schedule-board__section-part">{{ resolveDayPartLabel(row.dayPart) }}</span>
                <strong>{{ row.label }}</strong>
                <small v-if="formatRowTime(row)">{{ formatRowTime(row) }}</small>
              </div>
            </td>
            <template v-for="day in weekDays" :key="`${row.key}-${day.value}`">
              <td
                v-if="shouldRenderCell(row, day.value)"
                class="selection-schedule-board__cell td-content"
                :class="{ 'is-break': isBreakRow(row), 'is-empty': !isBreakRow(row) && !getSlotGroup(row, day.value) }"
                :rowspan="getRowSpan(row, day.value)"
              >
                <template v-if="isBreakRow(row)">
                  <div class="selection-schedule-board__break-cell"></div>
                </template>
                <button
                  v-else-if="getSlotGroup(row, day.value)"
                  type="button"
                  class="selection-schedule-board__slot-card tdHtml"
                  :class="slotCardClass(getSlotGroup(row, day.value))"
                  @click="openSlotDialog(getSlotGroup(row, day.value)!)"
                >
                  <div class="selection-schedule-board__slot-head">
                    <span>{{ slotStateLabel(getSlotGroup(row, day.value)!) }}</span>
                    <div class="selection-schedule-board__head-right">
                      <strong v-if="getSlotGroup(row, day.value)!.selectedCount > 1">{{ getSlotGroup(row, day.value)!.selectedCount }}门</strong>
                      <strong v-else-if="getSlotGroup(row, day.value)!.availableCount > 1">{{ getSlotGroup(row, day.value)!.availableCount }}门</strong>
                      <span
                        v-if="getSlotGroup(row, day.value)!.selectableCount > 0"
                        class="selection-schedule-board__action-label"
                      >
                        选课
                      </span>
                    </div>
                  </div>
                  <div class="selection-schedule-board__slot-title course-name">{{ slotPrimaryTitle(getSlotGroup(row, day.value)!) }}</div>
                  <div class="course-meta-list">
                    <div class="course-meta-item course-meta-item--wide">
                      <i class="ri-calendar-event-line"></i>
                      <span>{{ getSlotGroup(row, day.value)!.sectionText }} · {{ getSlotGroup(row, day.value)!.weekdayLabel }}</span>
                    </div>
                    <div class="course-meta-item course-meta-item--wide">
                      <i class="ri-map-pin-2-line"></i>
                      <span>{{ slotSummaryText(getSlotGroup(row, day.value)!) }}</span>
                    </div>
                  </div>
                  <div class="selection-schedule-board__slot-tags course-footer">
                    <span v-if="getSlotGroup(row, day.value)!.hasRequiredSelected" class="selection-schedule-board__slot-tag">
                      本班必修
                    </span>
                    <span v-if="getSlotGroup(row, day.value)!.selectedCount && getSlotGroup(row, day.value)!.availableCount" class="selection-schedule-board__slot-tag">
                      +{{ getSlotGroup(row, day.value)!.availableCount }} 备选
                    </span>
                    <span v-if="getSlotGroup(row, day.value)!.conflictCount" class="selection-schedule-board__slot-tag is-conflict">
                      {{ getSlotGroup(row, day.value)!.conflictCount }} 门重叠
                    </span>
                  </div>
                </button>
                <div v-else class="selection-schedule-board__empty-cell"></div>
              </td>
            </template>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="unscheduledCourses.length" class="selection-schedule-board__unscheduled">
      <div class="selection-schedule-board__unscheduled-head">
        <div>
          <strong>以下课程暂未排入课表</strong>
          <span>没有固定上课时段的课程仍然可以通过列表选课查看并办理。</span>
        </div>
        <el-button plain @click="$emit('switch-list')">切换到列表选课</el-button>
      </div>
      <div class="selection-schedule-board__unscheduled-list">
        <span
          v-for="item in unscheduledCourses.slice(0, 8)"
          :key="`unscheduled-${item.id}`"
          class="selection-schedule-board__unscheduled-chip"
        >
          {{ item.courseName || '-' }}
        </span>
        <span v-if="unscheduledCourses.length > 8" class="selection-schedule-board__unscheduled-chip is-muted">
          另有 {{ unscheduledCourses.length - 8 }} 门
        </span>
      </div>
    </div>

    <el-drawer
      v-model="slotDialogOpen"
      direction="rtl"
      size="86%"
      append-to-body
      class="selection-schedule-board__drawer"
    >
      <template #header>
        <div class="selection-schedule-board__drawer-header">
          <div>
            <span class="selection-schedule-board__drawer-eyebrow">时段选课</span>
            <strong>{{ activeSlotTitle }}</strong>
            <p v-if="activeSlotGroup">点击“选课”即可直接办理，已选课程和重叠课程会一并展示。</p>
          </div>
        </div>
      </template>
      <div v-if="activeSlotGroup" class="selection-schedule-board__dialog-shell selection-schedule-board__drawer-body">
        <div class="selection-schedule-board__dialog-top">
          <div class="selection-schedule-board__dialog-summary">
            <div class="selection-schedule-board__dialog-stat">
              <span>相关课程</span>
              <strong>{{ activeSlotGroup.entries.length }}</strong>
            </div>
            <div class="selection-schedule-board__dialog-stat">
              <span>已选课程</span>
              <strong>{{ activeSlotGroup.selectedCount }}</strong>
            </div>
            <div class="selection-schedule-board__dialog-stat">
              <span>可直接选课</span>
              <strong>{{ activeSlotGroup.selectableCount }}</strong>
            </div>
            <div class="selection-schedule-board__dialog-stat">
              <span>时间重叠</span>
              <strong>{{ activeSlotGroup.conflictCount }}</strong>
            </div>
          </div>
          <div class="selection-schedule-board__dialog-filters">
            <el-input
              v-model="drawerFilterForm.keyword"
              clearable
              placeholder="搜索课程、教学班、教师"
              class="selection-schedule-board__filter-field selection-schedule-board__filter-field--keyword"
              @keyup.enter="applyDrawerFilters"
            >
              <template #prefix><i class="ri-search-line"></i></template>
            </el-input>
            <el-select
              v-model="drawerFilterForm.status"
              class="selection-schedule-board__filter-field"
              popper-class="selection-schedule-board__drawer-popper"
              teleported
            >
              <el-option label="全部课程" value="all" />
              <el-option label="已选课程" value="selected" />
              <el-option label="可直接选课" value="selectable" />
              <el-option label="时间重叠" value="conflict" />
              <el-option label="人数已满" value="full" />
            </el-select>
            <el-select
              v-model="drawerFilterForm.teacher"
              class="selection-schedule-board__filter-field"
              popper-class="selection-schedule-board__drawer-popper"
              teleported
            >
              <el-option label="全部教师" value="all" />
              <el-option v-for="item in drawerTeacherOptions" :key="item" :label="item" :value="item" />
            </el-select>
            <el-select
              v-model="drawerFilterForm.courseKind"
              class="selection-schedule-board__filter-field"
              popper-class="selection-schedule-board__drawer-popper"
              teleported
            >
              <el-option label="全部类型" value="all" />
              <el-option label="本班必修" value="required" />
              <el-option label="专项课程" value="grouped" />
              <el-option label="合班课程" value="combined" />
              <el-option label="普通课程" value="normal" />
            </el-select>
            <label class="selection-schedule-board__checkbox">
              <el-checkbox v-model="drawerFilterForm.onlySeats">仅看有余量</el-checkbox>
            </label>
            <div class="selection-schedule-board__filter-actions">
              <el-button type="primary" :loading="drawerQuerying" @click="handleDrawerQuery">查询</el-button>
              <el-button :disabled="drawerQuerying" @click="handleDrawerReset">重置</el-button>
            </div>
          </div>
        </div>
        <el-table :data="filteredActiveSlotEntries" border max-height="560">
          <el-table-column label="操作" width="110" fixed="left">
            <template #default="{ row }">
              <el-button
                v-if="row.course.selected && row.course.canDrop"
                type="danger"
                plain
                size="small"
                :loading="row.course.__dropping"
                @click.stop="$emit('drop-course', row.course)"
              >
                退课
              </el-button>
              <el-button
                v-else-if="!row.course.selected && row.course.canSelect"
                type="primary"
                size="small"
                :loading="row.course.__selecting"
                @click.stop="$emit('select-course', row.course)"
              >
                选课
              </el-button>
              <el-button v-else size="small" disabled>
                {{ row.course.selected ? '已保留' : '不可选' }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="选课状态" width="150">
            <template #default="{ row }">
              <div class="selection-schedule-board__status">
                <div class="selection-schedule-board__status-tags">
                  <el-tag size="small" effect="plain" :type="slotEntryStatusType(row)">
                    {{ slotEntryStatusLabel(row) }}
                  </el-tag>
                  <el-tag
                    v-for="reason in slotEntryExtraReasons(row)"
                    :key="reason"
                    size="small"
                    effect="plain"
                    type="warning"
                  >
                    {{ reason }}
                  </el-tag>
                </div>
                <span>{{ slotEntryHint(row) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="课程信息" min-width="280" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="selection-schedule-board__course-cell">
                <strong>{{ row.course.courseName || '-' }}</strong>
                <span>{{ row.course.teachingClassCode || '-' }} / {{ row.course.className || '-' }}</span>
                <span>{{ row.course.credits ?? '-' }} 学分 / {{ row.course.totalHours ?? '-' }} 总学时 / {{ row.course.assessmentType || '未定考核' }}</span>
                <span v-if="row.course.selectionGroupName || row.course.selectionGroupCode">
                  专项分组：{{ row.course.selectionGroupName || row.course.selectionGroupCode }} / 限选 {{ row.course.selectionGroupLimit || 1 }} 门
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="授课教师" min-width="150">
            <template #default="{ row }">{{ row.course.teacherName || '-' }}</template>
          </el-table-column>
          <el-table-column label="时间地点" min-width="300">
            <template #default="{ row }">
              <div class="selection-schedule-board__course-cell">
                <strong>{{ row.primarySession.weeksText || '待定周次' }}</strong>
                <span v-for="item in row.sessions" :key="item.sessionKey">
                  {{ item.weekLabel }} {{ item.sectionText }}{{ item.classroom ? ` / ${item.classroom}` : '' }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="已选人数/上限" min-width="190">
            <template #default="{ row }">
              <div class="selection-schedule-board__capacity">
                <strong>{{ row.course.selectedStudentCount ?? row.course.actualStudentCount ?? 0 }} / {{ row.course.studentLimit || '-' }}</strong>
                <el-progress
                  v-if="row.course.studentLimit"
                  :percentage="capacityPercent(row.course)"
                  :color="capacityColor(row.course)"
                  :stroke-width="6"
                  :show-text="false"
                />
                <span v-else>不限制人数</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="详情" width="120" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click.stop="$emit('preview-course', row.course)">查看教学班</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!filteredActiveSlotEntries.length" description="当前筛选条件下没有匹配课程" :image-size="72" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, reactive, ref, watch } from 'vue'

type CourseRow = Record<string, any>

type BoardRow = {
  key: string
  unit: number | null
  label: string
  sideColor: string
  rowClass: string
  dayPart: string
  startTime?: number | null
  endTime?: number | null
  isBreak?: boolean
  breakLabel?: string
}

type SlotSession = {
  sessionKey: string
  weekDay: number
  weekLabel: string
  startSection: number
  endSection: number
  sectionText: string
  weeksText: string
  classroom: string
  teacherName: string
}

type SlotEntry = {
  course: CourseRow
  sessions: SlotSession[]
  primarySession: SlotSession
  conflictNames: string[]
}

type SlotGroup = {
  slotKey: string
  weekDay: number
  weekdayLabel: string
  startSection: number
  endSection: number
  sectionText: string
  rowSpan: number
  entries: SlotEntry[]
  selectedCount: number
  selectableCount: number
  availableCount: number
  conflictCount: number
  hasRequiredSelected: boolean
}

const BOARD_ROWS = [
  { key: '1', unit: 1, label: '1', sideColor: '#2f6fa7', rowClass: 'is-morning', dayPart: 'MORNING', startTime: 900, endTime: 940, isBreak: false, breakLabel: '' },
  { key: '2', unit: 2, label: '2', sideColor: '#2f6fa7', rowClass: 'is-morning', dayPart: 'MORNING', startTime: 950, endTime: 1030, isBreak: false, breakLabel: '' },
  { key: '3', unit: 3, label: '3', sideColor: '#2f6fa7', rowClass: 'is-morning', dayPart: 'MORNING', startTime: 1040, endTime: 1120, isBreak: false, breakLabel: '' },
  { key: '4', unit: 4, label: '4', sideColor: '#2f6fa7', rowClass: 'is-morning', dayPart: 'MORNING', startTime: 1130, endTime: 1210, isBreak: false, breakLabel: '' },
  { key: 'noon-1', unit: null, label: '中午', sideColor: '#ffbd2f', rowClass: 'is-break', dayPart: 'NOON', startTime: null, endTime: null, isBreak: true, breakLabel: '午间时段' },
  { key: 'noon-2', unit: null, label: '中午', sideColor: '#ffbd2f', rowClass: 'is-break', dayPart: 'NOON', startTime: null, endTime: null, isBreak: true, breakLabel: '午间时段' },
  { key: '5', unit: 5, label: '5', sideColor: '#2bb7a8', rowClass: 'is-afternoon', dayPart: 'AFTERNOON', startTime: 1320, endTime: 1400, isBreak: false, breakLabel: '' },
  { key: '6', unit: 6, label: '6', sideColor: '#2bb7a8', rowClass: 'is-afternoon', dayPart: 'AFTERNOON', startTime: 1410, endTime: 1450, isBreak: false, breakLabel: '' },
  { key: '7', unit: 7, label: '7', sideColor: '#2bb7a8', rowClass: 'is-afternoon', dayPart: 'AFTERNOON', startTime: 1500, endTime: 1540, isBreak: false, breakLabel: '' },
  { key: '8', unit: 8, label: '8', sideColor: '#2bb7a8', rowClass: 'is-afternoon', dayPart: 'AFTERNOON', startTime: 1550, endTime: 1630, isBreak: false, breakLabel: '' },
  { key: '9', unit: 9, label: '9', sideColor: '#65bcd9', rowClass: 'is-evening', dayPart: 'EVENING', startTime: 1830, endTime: 1910, isBreak: false, breakLabel: '' },
  { key: '10', unit: 10, label: '10', sideColor: '#65bcd9', rowClass: 'is-evening', dayPart: 'EVENING', startTime: 1920, endTime: 2000, isBreak: false, breakLabel: '' },
] as const satisfies ReadonlyArray<BoardRow>

const weekDays = [
  { label: '星期一', value: 1 },
  { label: '星期二', value: 2 },
  { label: '星期三', value: 3 },
  { label: '星期四', value: 4 },
  { label: '星期五', value: 5 },
  { label: '星期六', value: 6 },
  { label: '星期日', value: 7 },
]

const props = withDefaults(defineProps<{
  courses?: CourseRow[]
  selectedCourses?: CourseRow[]
  timeTableLayout?: any
  refreshCourses?: (() => Promise<void> | void) | null
  pollCourseCounts?: ((lessonIds?: Array<number | string>) => Promise<any> | any) | null
  queryCourses?: ((filters: Record<string, any>) => Promise<any> | any) | null
}>(), {
  courses: () => [],
  selectedCourses: () => [],
  timeTableLayout: () => null,
  refreshCourses: null,
  pollCourseCounts: null,
  queryCourses: null,
})

defineEmits<{
  (e: 'select-course', row: CourseRow): void
  (e: 'drop-course', row: CourseRow): void
  (e: 'preview-course', row: CourseRow): void
  (e: 'switch-list'): void
}>()

const slotDialogOpen = ref(false)
const activeSlotKey = ref('')
const drawerQuerying = ref(false)
const drawerServerCourseIds = ref<Set<string> | null>(null)
const DRAWER_COUNT_POLLING_INTERVAL = 5000
let drawerCountPollingTimer: ReturnType<typeof setInterval> | null = null
let drawerCountPollingBusy = false

function createDrawerFilterState() {
  return {
    keyword: '',
    status: 'all' as 'all' | 'selected' | 'selectable' | 'conflict' | 'full',
    teacher: 'all',
    courseKind: 'all' as 'all' | 'required' | 'grouped' | 'combined' | 'normal',
    onlySeats: false,
  }
}

const drawerFilterForm = reactive(createDrawerFilterState())
const appliedDrawerFilters = ref(createDrawerFilterState())

const normalizedCourses = computed(() => props.courses || [])
const tableRows = computed(() => resolveTableRows(props.timeTableLayout))
const unscheduledCourses = computed(() =>
  normalizedCourses.value.filter((item) => normalizeCourseSessions(item).length === 0),
)

const selectedOccupancyMap = computed(() => {
  const map = new Map<string, Array<{ id: number | string; name: string }>>()
  ;(props.selectedCourses || []).forEach((course) => {
    normalizeCourseSessions(course).forEach((session) => {
      for (let unit = session.startSection; unit <= session.endSection; unit += 1) {
        const key = `${session.weekDay}-${unit}`
        if (!map.has(key)) {
          map.set(key, [])
        }
        const bucket = map.get(key)!
        if (!bucket.some((item) => String(item.id) === String(course.id))) {
          bucket.push({ id: course.id, name: resolveCourseDisplayName(course) })
        }
      }
    })
  })
  return map
})

const slotGroupMap = computed(() => {
  const groups = new Map<string, {
    slotKey: string
    weekDay: number
    weekdayLabel: string
    startSection: number
    endSection: number
    sectionText: string
    entryMap: Map<string, SlotEntry>
  }>()

  normalizedCourses.value.forEach((course) => {
    const sessions = normalizeCourseSessions(course)
    const conflictNames = resolveConflictNames(course, sessions)
    sessions.forEach((session) => {
      const slotKey = `${session.weekDay}-${session.startSection}-${session.endSection}`
      if (!groups.has(slotKey)) {
        groups.set(slotKey, {
          slotKey,
          weekDay: session.weekDay,
          weekdayLabel: session.weekLabel || weekDayLabel(session.weekDay),
          startSection: session.startSection,
          endSection: session.endSection,
          sectionText: session.sectionText || `${session.startSection}-${session.endSection}节`,
          entryMap: new Map<string, SlotEntry>(),
        })
      }
      const group = groups.get(slotKey)!
      const entryKey = String(course.id || `${course.courseName || ''}-${course.teachingClassCode || ''}`)
      if (!group.entryMap.has(entryKey)) {
        group.entryMap.set(entryKey, {
          course,
          sessions: [],
          primarySession: session,
          conflictNames,
        })
      }
      const entry = group.entryMap.get(entryKey)!
      if (!entry.sessions.some((item) => item.sessionKey === session.sessionKey)) {
        entry.sessions.push(session)
        entry.sessions.sort((left, right) => left.startSection - right.startSection)
        entry.primarySession = entry.sessions[0]
      }
    })
  })

  const result = new Map<string, SlotGroup>()
  groups.forEach((group) => {
    const entries = Array.from(group.entryMap.values()).sort(compareSlotEntry)
    result.set(group.slotKey, {
      slotKey: group.slotKey,
      weekDay: group.weekDay,
      weekdayLabel: group.weekdayLabel,
      startSection: group.startSection,
      endSection: group.endSection,
      sectionText: group.sectionText,
      rowSpan: Math.max(1, group.endSection - group.startSection + 1),
      entries,
      selectedCount: entries.filter((item) => Boolean(item.course.selected)).length,
      selectableCount: entries.filter((item) => !item.course.selected && Boolean(item.course.canSelect)).length,
      availableCount: entries.filter((item) => !item.course.selected).length,
      conflictCount: entries.filter((item) => !item.course.selected && item.conflictNames.length > 0).length,
      hasRequiredSelected: entries.some((item) => Boolean(item.course.selected) && Boolean(item.course.requiredSelection)),
    })
  })
  return result
})

const occupiedMap = computed(() => {
  const map = new Map<string, { startSection: number; slotKey: string }>()
  slotGroupMap.value.forEach((group) => {
    for (let unit = group.startSection; unit <= group.endSection; unit += 1) {
      map.set(`${group.weekDay}-${unit}`, { startSection: group.startSection, slotKey: group.slotKey })
    }
  })
  return map
})

const boardSummary = computed(() => {
  const groups = Array.from(slotGroupMap.value.values())
  return {
    selectedSlotCount: groups.filter((item) => item.selectedCount > 0).length,
    availableSlotCount: groups.filter((item) => item.availableCount > 0).length,
    conflictSlotCount: groups.filter((item) => item.conflictCount > 0).length,
  }
})

const activeSlotGroup = computed(() => slotGroupMap.value.get(activeSlotKey.value) || null)
const activeSlotCourseIds = computed(() => {
  const group = activeSlotGroup.value
  if (!group) return []
  const ids = new Set<number>()
  group.entries.forEach((entry) => {
    const parsed = Number(entry?.course?.id)
    if (Number.isFinite(parsed) && parsed > 0) {
      ids.add(parsed)
    }
  })
  return Array.from(ids)
})
const activeSlotTitle = computed(() => {
  const group = activeSlotGroup.value
  if (!group) return '时段相关课程'
  return `${group.weekdayLabel} · ${group.sectionText} 相关课程`
})

const drawerTeacherOptions = computed(() => {
  const group = activeSlotGroup.value
  if (!group) return []
  return Array.from(new Set(
    group.entries
      .map((entry) => String(entry.course.teacherName || '').trim())
      .filter(Boolean),
  )).sort((left, right) => left.localeCompare(right))
})

const filteredActiveSlotEntries = computed(() => {
  const group = activeSlotGroup.value
  if (!group) return []
  const filters = appliedDrawerFilters.value
  const keyword = filters.keyword.trim().toLowerCase()
  return group.entries.filter((entry) => {
    const course = entry.course
    const courseIdKey = String(course.id || '')
    if (drawerServerCourseIds.value && courseIdKey && !drawerServerCourseIds.value.has(courseIdKey)) {
      return false
    }
    if (filters.onlySeats && Number(course.studentLimit || 0) > 0 && Number(course.remainingSeats || 0) <= 0 && !course.selected) {
      return false
    }
    if (filters.status === 'selected' && !course.selected) return false
    if (filters.status === 'selectable' && (course.selected || !course.canSelect)) return false
    if (filters.status === 'conflict' && entry.conflictNames.length === 0) return false
    if (filters.status === 'full' && !isCapacityFull(course)) return false
    if (filters.teacher !== 'all' && String(course.teacherName || '') !== filters.teacher) return false
    if (filters.courseKind === 'required' && !course.requiredSelection) return false
    if (filters.courseKind === 'grouped' && !course.selectionGroupCode) return false
    if (filters.courseKind === 'combined' && !course.combinedClassCode) return false
    if (filters.courseKind === 'normal' && (course.requiredSelection || course.selectionGroupCode || course.combinedClassCode)) return false
    if (!keyword) return true
    return [
      course.courseName,
      course.courseCode,
      course.teachingClassCode,
      course.className,
      course.teacherName,
      course.selectionOptionName,
      course.selectionGroupName,
    ].some((item) => String(item || '').toLowerCase().includes(keyword))
  })
})

watch(slotGroupMap, (value) => {
  if (activeSlotKey.value && !value.has(activeSlotKey.value)) {
    slotDialogOpen.value = false
    activeSlotKey.value = ''
  }
})

watch(
  () => [slotDialogOpen.value, activeSlotKey.value, activeSlotCourseIds.value.join(',')],
  ([open]) => {
    if (open && activeSlotCourseIds.value.length && props.pollCourseCounts) {
      void pollDrawerCourseCounts()
      startDrawerCountPolling()
      return
    }
    stopDrawerCountPolling()
  },
  { immediate: true },
)

function applyDrawerFilters() {
  appliedDrawerFilters.value = { ...drawerFilterForm }
}

function resetDrawerFilters() {
  Object.assign(drawerFilterForm, createDrawerFilterState())
  appliedDrawerFilters.value = createDrawerFilterState()
}

async function pollDrawerCourseCounts() {
  if (!slotDialogOpen.value || !props.pollCourseCounts || !activeSlotCourseIds.value.length || drawerCountPollingBusy) {
    return
  }
  drawerCountPollingBusy = true
  try {
    await props.pollCourseCounts(activeSlotCourseIds.value)
  } finally {
    drawerCountPollingBusy = false
  }
}

function stopDrawerCountPolling() {
  if (drawerCountPollingTimer) {
    clearInterval(drawerCountPollingTimer)
    drawerCountPollingTimer = null
  }
}

function startDrawerCountPolling() {
  if (drawerCountPollingTimer || !props.pollCourseCounts) return
  drawerCountPollingTimer = setInterval(() => {
    void pollDrawerCourseCounts()
  }, DRAWER_COUNT_POLLING_INTERVAL)
}

async function handleDrawerQuery() {
  drawerQuerying.value = true
  try {
    applyDrawerFilters()
    if (props.queryCourses) {
      const data = await props.queryCourses({
        keyword: appliedDrawerFilters.value.keyword || undefined,
        teacherName: appliedDrawerFilters.value.teacher === 'all' ? undefined : appliedDrawerFilters.value.teacher,
        courseKind: appliedDrawerFilters.value.courseKind === 'all' ? undefined : appliedDrawerFilters.value.courseKind,
        onlySeats: appliedDrawerFilters.value.onlySeats ? '1' : undefined,
      })
      applyDrawerServerResult(data)
    } else if (props.refreshCourses) {
      await props.refreshCourses()
      drawerServerCourseIds.value = null
    }
  } finally {
    drawerQuerying.value = false
  }
}

async function handleDrawerReset() {
  drawerQuerying.value = true
  try {
    resetDrawerFilters()
    if (props.queryCourses) {
      await props.queryCourses({})
    } else if (props.refreshCourses) {
      await props.refreshCourses()
    }
    drawerServerCourseIds.value = null
  } finally {
    drawerQuerying.value = false
  }
}

function applyDrawerServerResult(data: any) {
  const ids = new Set<string>()
  ;[...(data?.availableCourses || []), ...(data?.selectedCourses || [])].forEach((item: any) => {
    if (item?.id != null) ids.add(String(item.id))
  })
  drawerServerCourseIds.value = ids.size ? ids : new Set<string>()
}

function createDrawerPreset(group: SlotGroup) {
  const preset = createDrawerFilterState()

  const teacherSet = new Set(
    group.entries
      .map((entry) => String(entry.course.teacherName || '').trim())
      .filter(Boolean),
  )
  if (teacherSet.size === 1) {
    preset.teacher = Array.from(teacherSet)[0]
  }

  const kindSet = new Set(group.entries.map((entry) => resolveCourseKind(entry.course)))
  if (kindSet.size === 1) {
    const kind = Array.from(kindSet)[0]
    if (kind !== 'all') {
      preset.courseKind = kind
    }
  }

  const statusSet = new Set(group.entries.map((entry) => resolveEntryStatusValue(entry)))
  if (statusSet.size === 1) {
    const status = Array.from(statusSet)[0]
    if (status !== 'all') {
      preset.status = status
    }
  }

  return preset
}

function resolveCourseKind(course: CourseRow) {
  if (course.requiredSelection) return 'required'
  if (course.selectionGroupCode) return 'grouped'
  if (course.combinedClassCode) return 'combined'
  return 'normal'
}

function resolveEntryStatusValue(entry: SlotEntry) {
  const course = entry.course
  if (course.selected) return 'selected'
  if (entry.conflictNames.length > 0) return 'conflict'
  if (course.canSelect) return 'selectable'
  if (isCapacityFull(course)) return 'full'
  return 'all'
}

function normalizeCourseSessions(course: CourseRow): SlotSession[] {
  const sessionMap = new Map<string, SlotSession>()
  ;(course?.scheduleDetails || []).forEach((detail: any) => {
    const weekDay = resolveWeekDay(detail)
    const startSection = Number(detail?.startSection || 0)
    const endSection = Number(detail?.endSection || startSection || 0)
    if (!weekDay || !startSection || !endSection) {
      return
    }
    const weekLabel = String(detail?.weekLabel || weekDayLabel(weekDay))
    const sectionText = String(detail?.sectionText || `${startSection}-${endSection}节`)
    const weeksText = String(detail?.weeksText || '').trim()
    const classroom = String(detail?.classroom || '').trim()
    const teacherName = String(detail?.teacherName || course?.teacherName || '').trim()
    const sessionKey = [weekDay, startSection, endSection, weeksText, classroom, teacherName].join('|')
    if (!sessionMap.has(sessionKey)) {
      sessionMap.set(sessionKey, {
        sessionKey,
        weekDay,
        weekLabel,
        startSection,
        endSection,
        sectionText,
        weeksText,
        classroom,
        teacherName,
      })
    }
  })
  return Array.from(sessionMap.values()).sort((left, right) => {
    if (left.weekDay !== right.weekDay) return left.weekDay - right.weekDay
    if (left.startSection !== right.startSection) return left.startSection - right.startSection
    return left.endSection - right.endSection
  })
}

function resolveWeekDay(detail: any) {
  const numeric = Number(detail?.weekDay || 0)
  if (numeric >= 1 && numeric <= 7) return numeric
  const label = String(detail?.weekLabel || '')
  const weekDayMap: Record<string, number> = {
    星期一: 1,
    星期二: 2,
    星期三: 3,
    星期四: 4,
    星期五: 5,
    星期六: 6,
    星期日: 7,
  }
  if (weekDayMap[label]) return weekDayMap[label]
  const dateText = String(detail?.date || '')
  if (!dateText || dateText === '-') return 0
  const date = new Date(dateText)
  if (Number.isNaN(date.getTime())) return 0
  const day = date.getDay()
  return day === 0 ? 7 : day
}

function resolveConflictNames(course: CourseRow, sessions: SlotSession[]) {
  const names = new Set<string>()
  sessions.forEach((session) => {
    for (let unit = session.startSection; unit <= session.endSection; unit += 1) {
      const key = `${session.weekDay}-${unit}`
      const occupied = selectedOccupancyMap.value.get(key) || []
      occupied.forEach((item) => {
        if (String(item.id) !== String(course.id) && item.name) {
          names.add(item.name)
        }
      })
    }
  })
  return Array.from(names)
}

function compareSlotEntry(left: SlotEntry, right: SlotEntry) {
  if (left.course.selected && !right.course.selected) return -1
  if (!left.course.selected && right.course.selected) return 1
  if (left.course.canSelect && !right.course.canSelect) return -1
  if (!left.course.canSelect && right.course.canSelect) return 1
  const leftSeats = Number(left.course.remainingSeats ?? left.course.studentLimit ?? 9999)
  const rightSeats = Number(right.course.remainingSeats ?? right.course.studentLimit ?? 9999)
  if (leftSeats !== rightSeats) return rightSeats - leftSeats
  return String(left.course.courseName || '').localeCompare(String(right.course.courseName || ''))
}

function resolveTableRows(layout?: any) {
  const units = Array.isArray(layout?.courseUnitList) ? layout.courseUnitList : []
  if (!units.length) {
    return BOARD_ROWS.map((item) => ({ ...item }))
  }
  const normalizedRows = units
    .slice()
    .sort((left: any, right: any) => Number(left.indexNo || 0) - Number(right.indexNo || 0))
    .map((item: any, index: number) => ({
      key: String(item.indexNo || index + 1),
      unit: Number(item.indexNo || index + 1),
      label: item.nameZh || String(item.indexNo || index + 1),
      sideColor: dayPartColor(item.dayPart),
      rowClass: rowClassByDayPart(item.dayPart),
      dayPart: String(item.dayPart || 'MORNING').toUpperCase(),
      startTime: item.startTime ?? null,
      endTime: item.endTime ?? null,
      isBreak: String(item.dayPart || '').toUpperCase() === 'NOON',
      breakLabel: String(item.dayPart || '').toUpperCase() === 'NOON' ? '午间时段' : '',
    }))
  return injectBreakRows(normalizedRows)
}

function injectBreakRows(rows: BoardRow[]) {
  if (!rows.length) return []
  const result: BoardRow[] = []
  rows.forEach((row, index) => {
    const previous = rows[index - 1]
    if (previous && previous.dayPart !== row.dayPart) {
      if (previous.dayPart === 'MORNING' && row.dayPart === 'AFTERNOON') {
        result.push(buildBreakRow('noon-gap-1'), buildBreakRow('noon-gap-2'))
      }
    }
    result.push(row)
  })
  return result
}

function buildBreakRow(key: string, label = '中午'): BoardRow {
  return {
    key,
    unit: null,
    label,
    sideColor: '#ffbd2f',
    rowClass: 'is-break',
    dayPart: 'NOON',
    startTime: null,
    endTime: null,
    isBreak: true,
    breakLabel: '',
  }
}

function dayPartColor(dayPart?: string) {
  if (dayPart === 'NOON') return '#ffe7a3'
  if (dayPart === 'AFTERNOON') return '#2bb7a8'
  if (dayPart === 'EVENING') return '#65bcd9'
  return '#2f6fa7'
}

function rowClassByDayPart(dayPart?: string) {
  if (dayPart === 'NOON') return 'is-break'
  if (dayPart === 'AFTERNOON') return 'is-afternoon'
  if (dayPart === 'EVENING') return 'is-evening'
  return 'is-morning'
}

function isBreakRow(row: any) {
  return Boolean(row?.isBreak)
}

function isDayPartStart(row: BoardRow) {
  const currentIndex = tableRows.value.findIndex((item) => item.key === row.key)
  if (currentIndex <= 0) return true
  return tableRows.value[currentIndex - 1]?.dayPart !== row.dayPart
}

function resolveDayPartLabel(dayPart?: string) {
  if (dayPart === 'AFTERNOON') return '下午'
  if (dayPart === 'EVENING') return '晚上'
  if (dayPart === 'NOON') return '中午'
  return '上午'
}

function formatClockText(rawTime?: number | null) {
  if (rawTime == null) return ''
  const value = Number(rawTime)
  if (!Number.isFinite(value) || value <= 0) return ''
  const hour = Math.floor(value / 100)
  const minute = value % 100
  return `${hour}:${String(minute).padStart(2, '0')}`
}

function formatRowTime(row: BoardRow) {
  const start = formatClockText(row.startTime)
  const end = formatClockText(row.endTime)
  if (!start || !end) return ''
  return `${start}-${end}`
}

function shouldRenderCell(row: any, weekDay: number) {
  if (isBreakRow(row)) return true
  const marker = occupiedMap.value.get(`${weekDay}-${row.unit}`)
  if (!marker) return true
  return marker.startSection === row.unit
}

function getSlotGroup(row: any, weekDay: number) {
  if (isBreakRow(row)) return null
  const marker = occupiedMap.value.get(`${weekDay}-${row.unit}`)
  if (!marker || marker.startSection !== row.unit) return null
  return slotGroupMap.value.get(marker.slotKey) || null
}

function getRowSpan(row: any, weekDay: number) {
  const group = getSlotGroup(row, weekDay)
  return group ? group.rowSpan : 1
}

function slotCardClass(group: SlotGroup | null) {
  return {
    'is-selected': Boolean(group && group.selectedCount > 0 && group.availableCount === 0),
    'is-available': Boolean(group && group.selectedCount === 0),
    'is-mixed': Boolean(group && group.selectedCount > 0 && group.availableCount > 0),
    'is-conflict': Boolean(group && group.conflictCount > 0),
  }
}

function slotPrimaryTitle(group: SlotGroup) {
  if (group.selectedCount > 1) return `${group.selectedCount} 门已排课程`
  if (group.selectedCount === 1) {
    return resolveCourseDisplayName(group.entries.find((item) => item.course.selected)?.course)
  }
  if (group.availableCount === 1) {
    return resolveCourseDisplayName(group.entries[0]?.course)
  }
  return `${group.availableCount} 门候选课程`
}

function slotStateLabel(group: SlotGroup) {
  if (group.selectedCount > 0) {
    return group.hasRequiredSelected ? '已选 / 必修' : '已选课程'
  }
  if (group.selectableCount > 0) {
    return '可直接选课'
  }
  return '查看候选课'
}

function slotSummaryText(group: SlotGroup) {
  const selectedEntry = group.entries.find((item) => item.course.selected)
  if (selectedEntry) {
    const extra = group.availableCount > 0 ? `，另有 ${group.availableCount} 门备选` : ''
    return `${selectedEntry.primarySession.classroom || '待定教室'}${extra}`
  }
  const first = group.entries[0]
  const conflictText = group.conflictCount > 0 ? ` / ${group.conflictCount} 门重叠` : ''
  return `${first?.primarySession.classroom || '待定教室'}${conflictText}`
}

function openSlotDialog(group: SlotGroup) {
  activeSlotKey.value = group.slotKey
  const preset = createDrawerPreset(group)
  Object.assign(drawerFilterForm, preset)
  appliedDrawerFilters.value = { ...preset }
  drawerServerCourseIds.value = null
  slotDialogOpen.value = true
}

function resolveCourseDisplayName(course: CourseRow | undefined) {
  if (!course) return '未命名课程'
  return String(course.courseName || course.selectionOptionName || course.baseCourseName || '未命名课程')
}

function slotEntryStatusLabel(entry: SlotEntry) {
  const course = entry.course
  if (course.selected && course.requiredSelection) return '本班必修'
  if (course.selected) return '已选课程'
  if (entry.conflictNames.length) return '时间重叠'
  if (course.actionDisabledReason && String(course.actionDisabledReason).includes('专项组')) return '专项限选'
  if (course.actionDisabledReason && String(course.actionDisabledReason).includes('合班课程')) return '合班已选'
  if (course.canSelect) return '可直接选课'
  if (isCapacityFull(course)) return '人数已满'
  return course.actionDisabledReason || '暂不可选'
}

function slotEntryStatusType(entry: SlotEntry) {
  const course = entry.course
  if (course.selected && course.requiredSelection) return 'warning'
  if (course.selected) return 'success'
  if (entry.conflictNames.length) return 'danger'
  if (course.canSelect) return 'primary'
  if (isCapacityFull(course)) return 'danger'
  return 'info'
}

function slotEntryHint(entry: SlotEntry) {
  const course = entry.course
  if (course.selected && course.requiredSelection) return '本班必修课默认保留'
  if (course.selected) return '当前已在你的课表中'
  const reasons: string[] = []
  if (entry.conflictNames.length) {
    reasons.push(`与《${entry.conflictNames.join('》《')}》时间重叠`)
  }
  const disabledReason = String(course.actionDisabledReason || '').trim()
  if (disabledReason) {
    reasons.push(disabledReason)
  }
  return reasons.length ? reasons.join('；') : '满足条件即可直接办理'
}

function slotEntryExtraReasons(entry: SlotEntry) {
  const course = entry.course
  const reasons: string[] = []
  const disabledReason = String(course.actionDisabledReason || '').trim()
  if (!disabledReason) {
    return reasons
  }
  if (disabledReason.includes('专项组')) {
    reasons.push('专项限选')
  } else if (disabledReason.includes('合班课程')) {
    reasons.push('合班已选')
  } else if (disabledReason.includes('人数已满') && slotEntryStatusLabel(entry) !== '人数已满') {
    reasons.push('人数已满')
  }
  return reasons.filter((reason, index, array) => array.indexOf(reason) === index && reason !== slotEntryStatusLabel(entry))
}

function weekDayLabel(weekDay: number) {
  return weekDays.find((item) => item.value === weekDay)?.label || `星期${weekDay}`
}

function capacityPercent(row: CourseRow) {
  const current = Number(row.selectedStudentCount ?? row.actualStudentCount ?? 0)
  const limit = Number(row.studentLimit || 0)
  if (limit <= 0) return 0
  return Math.min(100, Math.round((current / limit) * 100))
}

function capacityColor(row: CourseRow) {
  const pct = capacityPercent(row)
  if (pct >= 90) return '#ef4444'
  if (pct >= 70) return '#f59e0b'
  return '#10b981'
}

function isCapacityFull(row: CourseRow) {
  return Number(row.studentLimit || 0) > 0 && Number(row.remainingSeats || 0) <= 0
}

onBeforeUnmount(() => {
  stopDrawerCountPolling()
})
</script>

<style scoped>
.selection-schedule-board {
  padding: 0 22px 22px;
}

.selection-schedule-board__hero {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(340px, 0.8fr);
  gap: 18px;
  padding: 18px 0 12px;
}

.selection-schedule-board__intro {
  padding: 18px 20px;
  border-radius: 4px;
  background: linear-gradient(135deg, #f8fbff 0%, #ffffff 100%);
  border: 1px solid #dbeafe;
}

.selection-schedule-board__eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 4px;
  background: #e0ecff;
  color: #245fca;
  font-size: 12px;
  font-weight: 700;
}

.selection-schedule-board__intro h4 {
  margin: 12px 0 8px;
  color: #0f172a;
  font-size: 24px;
}

.selection-schedule-board__intro p {
  margin: 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.8;
}

.selection-schedule-board__stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.selection-schedule-board__stat {
  padding: 16px 18px;
  border-radius: 4px;
  background: #ffffff;
  border: 1px solid #dbe7f5;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.05);
}

.selection-schedule-board__stat span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.selection-schedule-board__stat strong {
  display: block;
  margin-top: 8px;
  color: #0f172a;
  font-size: 24px;
}

.selection-schedule-board__legend {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 4px 0 14px;
}

.selection-schedule-board__legend-chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  border: 1px solid #dbe7f5;
  color: #475569;
  background: #ffffff;
}

.selection-schedule-board__legend-chip.is-selected {
  border-color: #8fb5ff;
  background: #eef4ff;
  color: #245fca;
}

.selection-schedule-board__legend-chip.is-available {
  border-color: #9fded5;
  background: #effcf8;
  color: #0f766e;
}

.selection-schedule-board__legend-chip.is-mixed {
  border-color: #bfd6ff;
  background: linear-gradient(135deg, #eef4ff 0%, #f5fbfa 100%);
  color: #1d4ed8;
}

.selection-schedule-board__legend-chip.is-conflict {
  border-color: #fecaca;
  background: #fff1f2;
  color: #dc2626;
}

.selection-schedule-board__table-wrap {
  margin-top: 6px;
}

.selection-schedule-board__table {
  margin-bottom: 0;
}

.courseTable {
  width: 100%;
  margin-bottom: 0;
  font-size: 12px;
  word-break: break-word;
  white-space: normal;
  border: none;
  border-collapse: separate;
  border-spacing: 0;
  table-layout: fixed;
  background: #fff;
  outline: 1px solid #c8d8eb;
  outline-offset: -1px;
}

.courseTable > thead > tr > th {
  border: none;
  padding: 0 8px;
  text-align: center;
  vertical-align: middle;
  height: 40px;
  color: #172033;
  background: linear-gradient(180deg, #f6faff 0%, #edf4fb 100%);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.02em;
  box-shadow: inset -1px 0 0 #d4e0ef, inset 0 -1px 0 #d4e0ef;
}

.selection-schedule-board__section-head {
  text-align: center !important;
}

.selection-schedule-board__section-unit {
  width: 4.8%;
  color: #172033;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-shadow: none;
  box-shadow: inset -1px 0 0 #d4e0ef, inset 0 -1px 0 #d4e0ef, inset 0 0 0 1px rgba(255,255,255,0.22);
}

.selection-schedule-board__section-content {
  height: 100%;
  min-height: 52px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  padding: 6px 4px;
}

.selection-schedule-board__section-content strong {
  color: #172033;
  font-size: 14px;
  line-height: 1;
}

.selection-schedule-board__section-content strong:only-child {
  font-size: 13px;
}

.selection-schedule-board__section-part {
  color: rgba(23, 32, 51, 0.82);
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.selection-schedule-board__section-content small {
  color: rgba(23, 32, 51, 0.68);
  font-size: 10px;
  line-height: 1.2;
}

.courseTable > tbody > tr > td {
  padding: 0;
  min-height: 20px;
  border: none;
  text-align: left;
  vertical-align: top;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: inset -1px 0 0 #d4e0ef, inset 0 -1px 0 #d4e0ef;
}

.selection-schedule-board__cell {
  position: relative;
  background: linear-gradient(180deg, rgba(255,255,255,0.88) 0%, rgba(247,250,253,0.96) 100%);
  height: 52px;
  box-shadow: inset -1px 0 0 #d4e0ef, inset 0 -1px 0 #d4e0ef, inset 0 0 0 0.5px rgba(255,255,255,0.14);
  background-clip: padding-box;
}

.selection-schedule-board__cell.is-break {
  background: rgba(255, 255, 255, 0.82);
  box-shadow: inset -1px 0 0 #d4e0ef, inset 0 -1px 0 #d4e0ef;
}

.selection-schedule-board__break-cell {
  height: 100%;
  min-height: 52px;
  background: transparent;
}

.selection-schedule-board__empty-cell {
  min-height: 46px;
  background:
    linear-gradient(90deg, rgba(148, 163, 184, 0.08) 1px, transparent 1px) 0 0 / 24px 24px,
    linear-gradient(rgba(148, 163, 184, 0.06) 1px, transparent 1px) 0 0 / 24px 24px,
    #ffffff;
}

.selection-schedule-board__slot-card {
  margin: 2px;
  width: calc(100% - 4px);
  height: calc(100% - 4px);
  border: none;
  padding: 5px 8px 6px;
  text-align: left;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 4px;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
  background: linear-gradient(135deg, #eef4ff 0%, #ffffff 100%);
  box-shadow: inset 4px 0 0 #60a5fa;
  color: #4c5d72;
  min-height: 46px;
  overflow: hidden;
  border-radius: 0;
}

.selection-schedule-board__slot-card:hover {
  transform: translateY(-1px);
  box-shadow: inset 4px 0 0 #3b82f6, 0 14px 24px rgba(59, 130, 246, 0.12);
}

.selection-schedule-board__slot-card.is-selected {
  background: linear-gradient(135deg, #eaf2ff 0%, #ffffff 100%);
  box-shadow: inset 4px 0 0 #3b82f6;
}

.selection-schedule-board__slot-card.is-available {
  background: linear-gradient(135deg, #effcf8 0%, #ffffff 100%);
  box-shadow: inset 4px 0 0 #2bb7a8;
}

.selection-schedule-board__slot-card.is-mixed {
  background: linear-gradient(135deg, #eef4ff 0%, #f5fbfa 100%);
  box-shadow: inset 4px 0 0 #38bdf8;
}

.selection-schedule-board__slot-card.is-conflict {
  box-shadow: inset 4px 0 0 #ef4444;
}

.selection-schedule-board__slot-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.selection-schedule-board__slot-head span {
  color: #475569;
  font-size: 12px;
  font-weight: 700;
}

.selection-schedule-board__head-right {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.selection-schedule-board__slot-head strong {
  color: #0f172a;
  font-size: 13px;
}

.selection-schedule-board__action-label {
  display: inline-flex;
  align-items: center;
  color: #ef4444;
  font-size: 12px;
  font-weight: 700;
}

.selection-schedule-board__slot-title {
  margin-bottom: 3px;
}

.course-name {
  color: #122033;
  font-size: 15px;
  font-weight: 800;
  line-height: 1.18;
  text-decoration: underline;
  text-decoration-thickness: 1px;
  text-underline-offset: 2px;
  text-decoration-color: rgba(18, 32, 51, 0.24);
}

.course-meta-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.course-meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
  color: #405068;
  font-size: 12px;
  line-height: 1.3;
}

.course-meta-item i {
  flex: 0 0 auto;
  font-size: 13px;
  color: #245a97;
}

.course-meta-item span {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-meta-item--wide {
  color: #33455d;
}

.course-footer {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
  margin-top: auto;
}

.selection-schedule-board__slot-tags {
  justify-content: flex-start;
}

.selection-schedule-board__slot-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 8px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.7);
  color: #245fca;
  font-size: 11px;
  font-weight: 700;
}

.selection-schedule-board__slot-tag.is-conflict {
  color: #dc2626;
  background: rgba(254, 226, 226, 0.9);
}

.selection-schedule-board__unscheduled {
  margin-top: 16px;
  padding: 16px 18px;
  border-radius: 4px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.selection-schedule-board__unscheduled-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.selection-schedule-board__unscheduled-head strong {
  display: block;
  color: #0f172a;
  font-size: 15px;
}

.selection-schedule-board__unscheduled-head span {
  display: block;
  margin-top: 4px;
  color: #64748b;
  font-size: 12px;
}

.selection-schedule-board__unscheduled-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 14px;
}

.selection-schedule-board__unscheduled-chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 10px;
  border-radius: 4px;
  background: #ffffff;
  border: 1px solid #dbe7f5;
  color: #334155;
  font-size: 12px;
}

.selection-schedule-board__unscheduled-chip.is-muted {
  color: #64748b;
  background: #f1f5f9;
}

.selection-schedule-board__dialog-shell {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.selection-schedule-board__drawer-header {
  width: 100%;
}

.selection-schedule-board__drawer-eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 4px;
  background: #eef4ff;
  color: #245fca;
  font-size: 12px;
  font-weight: 700;
}

.selection-schedule-board__drawer-header strong {
  display: block;
  margin-top: 10px;
  color: #0f172a;
  font-size: 20px;
}

.selection-schedule-board__drawer-header p {
  margin: 8px 0 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.selection-schedule-board__drawer-body {
  min-height: 320px;
}

.selection-schedule-board__dialog-top {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  flex-wrap: wrap;
}

.selection-schedule-board__dialog-summary {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  flex: 1;
  min-width: min(720px, 100%);
}

.selection-schedule-board__dialog-stat {
  padding: 14px 16px;
  border-radius: 4px;
  background: #f8fbff;
  border: 1px solid #dbeafe;
}

.selection-schedule-board__dialog-stat span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.selection-schedule-board__dialog-stat strong {
  display: block;
  margin-top: 6px;
  color: #0f172a;
  font-size: 22px;
}

.selection-schedule-board__dialog-filters {
  display: grid;
  grid-template-columns: minmax(240px, 1.2fr) repeat(3, minmax(160px, 0.9fr)) auto auto;
  gap: 12px;
  align-items: center;
}

.selection-schedule-board__status,
.selection-schedule-board__course-cell,
.selection-schedule-board__capacity {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.selection-schedule-board__status-tags {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.selection-schedule-board__status span,
.selection-schedule-board__course-cell span,
.selection-schedule-board__capacity span {
  color: #64748b;
  font-size: 12px;
  line-height: 1.6;
}

.selection-schedule-board__course-cell strong,
.selection-schedule-board__capacity strong {
  color: #0f172a;
  font-size: 14px;
}

.selection-schedule-board__filter-field {
  width: 100%;
}

.selection-schedule-board__filter-field--keyword {
  min-width: 260px;
}

.selection-schedule-board__checkbox {
  display: inline-flex;
  align-items: center;
  min-height: 32px;
}

.selection-schedule-board__filter-actions {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-end;
}

:deep(.selection-schedule-board__drawer .el-drawer__header) {
  margin-bottom: 0;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
}

:deep(.selection-schedule-board__drawer .el-drawer__body) {
  padding-top: 12px;
}

:global(.selection-schedule-board__drawer-popper.el-select__popper),
:global(.selection-schedule-board__drawer-popper.el-popper) {
  z-index: 5602 !important;
}

@media (max-width: 1200px) {
  .selection-schedule-board__hero {
    grid-template-columns: 1fr;
  }

  .selection-schedule-board__dialog-summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .selection-schedule-board__dialog-filters {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 960px) {
  .selection-schedule-board {
    padding-left: 14px;
    padding-right: 14px;
  }

  .selection-schedule-board__stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .selection-schedule-board__table-wrap {
    overflow-x: auto;
  }

  .selection-schedule-board__table {
    min-width: 980px;
  }

  .selection-schedule-board__dialog-filters {
    grid-template-columns: 1fr;
  }

  .selection-schedule-board__filter-actions {
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .selection-schedule-board {
    padding-left: 12px;
    padding-right: 12px;
    padding-bottom: 14px;
  }

  .selection-schedule-board__intro h4 {
    font-size: 20px;
  }

  .selection-schedule-board__intro p {
    font-size: 12px;
  }

  .selection-schedule-board__stat strong {
    font-size: 20px;
  }

  .selection-schedule-board__legend {
    gap: 6px;
  }

  .selection-schedule-board__legend-chip {
    padding: 4px 10px;
    font-size: 11px;
  }

  .selection-schedule-board__unscheduled {
    padding: 12px 14px;
  }

  .selection-schedule-board__unscheduled-head strong {
    font-size: 14px;
  }

  .selection-schedule-board__drawer-header strong {
    font-size: 18px;
  }

  :deep(.selection-schedule-board__drawer.el-drawer) {
    width: 100% !important;
  }
}

@media (max-width: 640px) {
  .selection-schedule-board__stats,
  .selection-schedule-board__dialog-summary {
    grid-template-columns: 1fr;
  }

  .selection-schedule-board__intro {
    padding: 14px;
  }

  .selection-schedule-board__intro h4 {
    font-size: 18px;
  }

  .selection-schedule-board__stat {
    padding: 12px 14px;
  }

  .selection-schedule-board__stat strong {
    font-size: 18px;
  }
}
</style>
