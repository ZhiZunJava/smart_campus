<template>
  <div class="portal-page schedule-page">
    <div class="schedule-header-user">
      <h3>{{ scheduleHeaderTitle }}</h3>
    </div>

    <div class="schedule-content">
      <div class="schedule-filters">
        <el-select
          v-model="queryParams.termId"
          filterable
          clearable
          placeholder="选择学期"
          class="schedule-select schedule-select--term"
          @change="loadSchedule"
        >
          <el-option
            v-for="item in termOptions"
            :key="`term-${item.value}`"
            :label="item.termName || item.label"
            :value="item.value"
          />
        </el-select>

        <el-select v-model="selectedWeek" class="schedule-select schedule-select--week">
          <el-option label="全部周次" value="all" />
          <el-option
            v-for="item in weekNumberOptions"
            :key="`week-${item}`"
            :label="`第${item}周`"
            :value="item"
          />
        </el-select>

        <div class="schedule-actions">
          <el-button type="primary" @click="setCurrentWeek">本周</el-button>
          <el-button type="primary" plain @click="shiftWeek(-1)">上一周</el-button>
          <el-button type="primary" plain @click="shiftWeek(1)">下一周</el-button>
          <span class="schedule-term-date">学期起始日期: {{ formatDate(currentTermMeta.startDate) || '2026.03.05' }}</span>
        </div>

        <div class="schedule-actions-right">
          <el-button type="primary" class="schedule-print-btn" @click="printSchedule">
            打印
          </el-button>
        </div>
      </div>

      <div class="table-container !pb-5">
        <div class="schedule-print-sheet">
          <div class="tableTopName">
            {{ printSchoolTitle }}
            <span class="semester-container">（{{ printTermName }}）</span>
          </div>
          <div class="table-head-info">
            <span><label>年级：</label><span>{{ printStudentInfo.grade }}</span></span>
            <span><label>学院：</label><span>{{ printStudentInfo.college }}</span></span>
            <span><label>专业：</label><span>{{ printStudentInfo.major }}</span></span>
            <span><label>班级：</label><span>{{ printStudentInfo.className }}</span></span>
            <span><label>学号：</label><span>{{ printStudentInfo.studentNo }}</span></span>
            <span><label>姓名：</label><span>{{ printStudentInfo.name }}</span></span>
          </div>
          <table class="courseTable courseTable--print" cellspacing="0" cellpadding="0">
            <thead>
              <tr>
                <th width="2%" colspan="2"></th>
                <th v-for="day in weekDays" :key="`print-head-${day.value}`" width="14%">{{ day.label }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in printTableRows" :key="`print-row-${row.key}`" :class="row.rowClass">
                <td
                  v-if="shouldRenderTimeArea(row.key)"
                  class="timeArea"
                  :rowspan="getTimeAreaSpan(row.key)"
                >
                  {{ row.dayPartLabel }}
                </td>
                <td class="dayPartUnit">{{ row.label }}</td>
                <template v-for="day in weekDays" :key="`print-${row.key}-${day.value}`">
                  <td
                    v-if="shouldRenderCell(row.key, day.value)"
                    class="td-content"
                    :rowspan="getRowSpan(row.key, day.value)"
                  >
                    <div
                      v-if="getActivity(row.key, day.value)"
                      class="tdHtml tdHtml--print"
                    >
                      <div class="course-name">{{ formatPrintCourseName(getActivity(row.key, day.value)) }}</div>
                      <div>{{ formatWeeksAndUnits(getActivity(row.key, day.value)) }}</div>
                      <div>{{ formatPrintLocationTeachers(getActivity(row.key, day.value)) }}</div>
                      <div>{{ getActivity(row.key, day.value).lessonName || getActivity(row.key, day.value).className || '' }}</div>
                      <div>人数:{{ getStudentCount(getActivity(row.key, day.value)) }}</div>
                    </div>
                    <div v-else class="tdHtml tdHtml--empty"></div>
                  </td>
                </template>
              </tr>
            </tbody>
          </table>
          <div v-if="remarkItems.length" class="course-table-fontSize course-table-remark course-table-remark--print">
            备注:
            <span v-for="(item, index) in remarkItems" :key="`print-remark-${item.id || index}`">
              {{ index + 1 }}.{{ item.courseName || item.course?.nameZh || '未排课程' }}
              {{ item.code || item.course?.code || '' }}
              {{ item.teacherAssignmentString || '' }}
              <template v-if="item.remark">
                任务备注:({{ item.remark }})
              </template>
            </span>
          </div>
          <div class="course-table-fontSize course-table-footer course-table-footer--print">
            <span v-if="printTotalCredits">总学分：{{ printTotalCredits }}</span>
            <span class="pull-right">打印日期:{{ printDate }}</span>
          </div>
        </div>
        <table class="courseTable" cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <th width="4.8%" class="courseTable__section-head">节次</th>
              <th v-for="day in weekDays" :key="`head-${day.value}`" width="13.7%">{{ day.label }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in tableRows" :key="`row-${row.key}`" :class="row.rowClass">
              <td class="dayPartUnit" :style="{ background: row.sideColor }">{{ row.label }}</td>
              <template v-for="day in weekDays" :key="`${row.key}-${day.value}`">
                <td
                  v-if="shouldRenderCell(row.key, day.value)"
                  class="td-content"
                  :rowspan="getRowSpan(row.key, day.value)"
                >
                  <el-tooltip v-if="getActivity(row.key, day.value)" placement="right" :show-after="300" :popper-options="{ modifiers: [{ name: 'offset', options: { offset: [0, 8] } }] }">
                    <template #content>
                      <div class="schedule-tooltip">
                        <div><b>{{ displayClassCourseName(getActivity(row.key, day.value)) }}</b></div>
                        <div v-if="getActivity(row.key, day.value)?._parallelCount">专项课程: {{ (getActivity(row.key, day.value)?._parallelOptionNames || []).join(', ') }}</div>
                        <div v-if="getActivity(row.key, day.value)?._combinedCount">合班: {{ getActivity(row.key, day.value)._combinedClassNames }}</div>
                        <div>周次: {{ formatWeeksLabel(getActivity(row.key, day.value)) }}</div>
                        <div>节次: {{ getSectionText(getActivity(row.key, day.value)) }}</div>
                        <div v-if="formatLocation(getActivity(row.key, day.value))">教室: {{ formatLocation(getActivity(row.key, day.value)) }}</div>
                        <div>教师: {{ formatTeachers(getActivity(row.key, day.value)) || '未配置' }}</div>
                        <div>班级: {{ getActivity(row.key, day.value)?._combinedClassNames || getActivity(row.key, day.value)?.className || '-' }}</div>
                        <div>人数: {{ getStudentCount(getActivity(row.key, day.value)) }}</div>
                      </div>
                    </template>
<div
                    class="tdHtml"
                    :style="getCardStyle(getActivity(row.key, day.value))"
                  >
                    <div class="course-name">
                      <span>{{ displayClassCourseName(getActivity(row.key, day.value)) }}</span>
                      <span v-if="getActivity(row.key, day.value)?._parallelCount" class="schedule-badge schedule-badge--parallel">{{ getActivity(row.key, day.value)._parallelCount }}专项</span>
                      <span v-if="getActivity(row.key, day.value)?._combinedCount" class="schedule-badge schedule-badge--combined">合班</span>
                    </div>
                    <div class="course-meta-list">
                      <div class="course-meta-item course-meta-item--wide">
                        <i class="ri-calendar-event-line"></i>
                        <span>
                          {{ formatWeeksLabel(getActivity(row.key, day.value)) }}
                          ·
                          {{ getSectionText(getActivity(row.key, day.value)) }}
                        </span>
                      </div>
                      <div
                        v-if="formatLocation(getActivity(row.key, day.value))"
                        class="course-meta-item course-meta-item--wide"
                      >
                        <i class="ri-map-pin-2-line"></i>
                        <span>{{ formatLocation(getActivity(row.key, day.value)) }}</span>
                      </div>
                      <div v-if="formatTeachers(getActivity(row.key, day.value))" class="course-meta-item">
                        <i class="ri-user-star-line"></i>
                        <span>{{ formatTeachers(getActivity(row.key, day.value)) }}</span>
                      </div>
                    </div>
                    <div class="course-footer">
                      <div class="lesson-name">{{ getActivity(row.key, day.value)?._combinedClassNames || getActivity(row.key, day.value)?.lessonName || getActivity(row.key, day.value)?.className || '' }}</div>
                      <div class="course-population">
                        <i class="ri-group-line"></i>
                        <span>{{ getStudentCount(getActivity(row.key, day.value)) }}</span>
                      </div>
                    </div>
                  </div>
                    </el-tooltip>
                  <div v-else class="tdHtml tdHtml--empty"></div>
                </td>
              </template>
            </tr>
          </tbody>
        </table>

        <div v-if="remarkItems.length" class="course-table-fontSize course-table-remark">
          备注:
          <span v-for="(item, index) in remarkItems" :key="`remark-${item.id || index}`">
            {{ index + 1 }}.{{ item.courseName || item.course?.nameZh || '未排课程' }}
            {{ item.code || item.course?.code || '' }}
            {{ item.teacherAssignmentString || '' }}
            <template v-if="item.remark">
              任务备注:({{ item.remark }})
            </template>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { getPortalMyClassSchedule, getPortalMySchedule, listPortalTermOptions } from '@/api/portal'
import { useRoute } from 'vue-router'
import usePortalUserStore from '@/store/user'
import { resolveCurrentWeek } from '@/utils/termWeek'

const props = withDefaults(defineProps<{ mode?: 'selected' | 'class' }>(), {
  mode: 'selected',
})

type WeekValue = number | 'all'

const route = useRoute()

const DEFAULT_ROW_CONFIG = [
  { key: '1', unit: 1, label: '1', sideColor: '#dcecff', rowClass: 'row-morning' },
  { key: '2', unit: 2, label: '2', sideColor: '#dcecff', rowClass: 'row-morning' },
  { key: '3', unit: 3, label: '3', sideColor: '#dcecff', rowClass: 'row-morning' },
  { key: '4', unit: 4, label: '4', sideColor: '#dcecff', rowClass: 'row-morning' },
  { key: 'noon-1', unit: null, label: '中午', sideColor: '#ffe7a3', rowClass: 'row-noon' },
  { key: 'noon-2', unit: null, label: '中午', sideColor: '#ffe7a3', rowClass: 'row-noon' },
  { key: '5', unit: 5, label: '5', sideColor: '#c9f0ea', rowClass: 'row-afternoon' },
  { key: '6', unit: 6, label: '6', sideColor: '#c9f0ea', rowClass: 'row-afternoon' },
  { key: '7', unit: 7, label: '7', sideColor: '#c9f0ea', rowClass: 'row-afternoon' },
  { key: '8', unit: 8, label: '8', sideColor: '#c9f0ea', rowClass: 'row-afternoon' },
  { key: '9', unit: 9, label: '9', sideColor: '#d7eef8', rowClass: 'row-evening' },
  { key: '10', unit: 10, label: '10', sideColor: '#d7eef8', rowClass: 'row-evening' },
] as const

const userStore = usePortalUserStore()
const loading = ref(false)
const termOptions = ref<any[]>([])
const queryParams = reactive<any>({ termId: undefined })
const activities = ref<any[]>([])
const remarkLessons = ref<any[]>([])
const currentWeek = ref(1)
const selectedWeek = ref<WeekValue>('all')
const currentWeekFromApi = ref(1)
const isClassMode = computed(() => props.mode === 'class')

const weekDays = [
  { label: '星期一', value: 1 },
  { label: '星期二', value: 2 },
  { label: '星期三', value: 3 },
  { label: '星期四', value: 4 },
  { label: '星期五', value: 5 },
  { label: '星期六', value: 6 },
  { label: '星期日', value: 7 },
]

const tableRows = ref<any[]>(DEFAULT_ROW_CONFIG.map((item) => ({ ...item })))
const currentTermMeta = computed(() => termOptions.value.find((item: any) => item.value === queryParams.termId) || {})
const currentTermTotalWeeks = computed(() => {
  const total = Number(currentTermMeta.value?.totalWeeks || 20)
  return total > 0 ? total : 20
})
const weekNumberOptions = computed(() => {
  return Array.from({ length: currentTermTotalWeeks.value }).map((_, index) => index + 1)
})

const scheduleHeaderTitle = computed(() => {
  const user = userStore.user || {}
  const sample = activities.value[0] || {}
  if (isClassMode.value) {
    return sample.className || user.className || '我的班级课表'
  }
  const name = user.realName || user.username || '程安宁'
  const studentNo = user.studentNo || '2401036049'
  return `${name}(${studentNo})`
})

const printSchoolTitle = computed(() => {
  const user = userStore.user || {}
  return `${user.schoolName || user.orgName || user.deptName || '校园智学'}${isClassMode.value ? '班级课表' : '学生课表'}`
})
const printTermName = computed(() => currentTermMeta.value?.termName || currentTermMeta.value?.label || '-')
const printTableRows = computed(() => tableRows.value.map((item: any) => ({
  ...item,
  dayPartLabel: dayPartText(item.rowClass),
})))
const printStudentInfo = computed(() => {
  const user = userStore.user || {}
  const sample = visibleActivities.value[0] || activities.value[0] || {}
  return {
    grade: user.admissionYear || '--',
    college: user.deptName || user.dept?.deptName || user.college || user.faculty || user.academy || '--',
    major: user.major || user.subject || sample.major || '--',
    className: sample.className || sample.lessonName || user.className || '--',
    studentNo: user.studentNo || '--',
    name: user.realName || user.nickName || user.userName || '同学',
  }
})
const printTotalCredits = computed(() => currentTermMeta.value?.totalCredits || '')
const printDate = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1
  const day = now.getDate()
  return `${year}/${month}/${day}`
})

const visibleActivities = computed(() => {
  if (selectedWeek.value === 'all') return activities.value
  return activities.value.filter((item: any) => extractWeeks(item.weeksStr || item.weeksText).includes(Number(selectedWeek.value)))
})

const activityMap = computed(() => {
  const map = new Map<string, any[]>()
  visibleActivities.value.forEach((item: any) => {
    const startKey = String(item.startSection || '')
    if (!startKey) return
    const key = `${startKey}-${item.weekDay}`
    if (!map.has(key)) map.set(key, [])
    map.get(key)!.push(item)
  })
  return map
})
// Merged view: group parallel selection-group and combined-class schedules
const mergedActivityMap = computed(() => {
  const result = new Map<string, any>()
  for (const [key, items] of activityMap.value) {
    if (items.length === 1) { result.set(key, items[0]); continue }
    const primary = { ...items[0] }
    const selGroupItems = items.filter((e: any) => e.selectionGroupCode && e.selectionGroupCode === primary.selectionGroupCode && e.classId === primary.classId)
    if (selGroupItems.length > 1) {
      primary._parallelOptionNames = selGroupItems.map((e: any) => e.selectionOptionName || e.teacherName || '').filter(Boolean)
      primary._parallelTeachers = [...new Set(selGroupItems.map((e: any) => e.teacherName).filter(Boolean))]
      primary._parallelCount = selGroupItems.length
    }
    const combinedItems = items.filter((e: any) => e.combinedClassCode && e.combinedClassCode === primary.combinedClassCode)
    if (combinedItems.length > 1) {
      primary._combinedClassNames = [...new Set(combinedItems.map((e: any) => e.className).filter(Boolean))].join('+')
      primary._combinedCount = combinedItems.length
    }
    result.set(key, primary)
  }
  return result
})

const occupiedMap = computed(() => {
  const map = new Map<string, { startKey: string; span: number }>()
  visibleActivities.value.forEach((item: any) => {
    const startUnit = Number(item.startSection || 0)
    const endUnit = Number(item.endSection || startUnit)
    const weekDay = Number(item.weekDay || 0)
    if (!startUnit || !weekDay) return
    const span = Math.max(1, endUnit - startUnit + 1)
    for (let unit = startUnit; unit <= endUnit; unit += 1) {
      map.set(`${unit}-${weekDay}`, { startKey: String(startUnit), span })
    }
  })
  return map
})

const activityColorMap = computed(() => {
  const uniqueKeys: string[] = []
  const seen = new Set<string>()
  activities.value.forEach((item: any) => {
    const key = resolveActivityColorKey(item)
    if (!key || seen.has(key)) return
    seen.add(key)
    uniqueKeys.push(key)
  })
  uniqueKeys.sort()
  return new Map(uniqueKeys.map((key, index) => [key, buildActivityColor(index)]))
})

const remarkItems = computed(() => remarkLessons.value)

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
  const current = termOptions.value.find((item: any) => item.isCurrent === '1')
  if (!queryParams.termId && current) {
    queryParams.termId = current.value
  }
  currentWeek.value = resolveCurrentWeek(current || termOptions.value[0] || {})
  currentWeekFromApi.value = currentWeek.value
}

async function loadSchedule() {
  const userId = userStore.user?.userId
  if (!userId) return
  loading.value = true
  try {
    const res = isClassMode.value
      ? await getPortalMyClassSchedule({ userId, termId: queryParams.termId })
      : await getPortalMySchedule({ userId, termId: queryParams.termId })
    activities.value = res.data?.activities || []
    remarkLessons.value = res.data?.remarkLessons || []
    applyTimeTableLayout(res.data?.timeTableLayout)
    currentWeekFromApi.value = Number(res.data?.currentWeek || currentWeek.value || 1)
    currentWeek.value = currentWeekFromApi.value
    selectedWeek.value = 'all'
  } finally {
    loading.value = false
  }
}

function setCurrentWeek() {
  currentWeek.value = currentWeekFromApi.value || 1
  selectedWeek.value = currentWeek.value
}

function shiftWeek(offset: number) {
  const options = weekNumberOptions.value
  const current = selectedWeek.value === 'all' ? currentWeek.value : Number(selectedWeek.value)
  const currentIndex = options.findIndex((item) => item === current)
  const safeIndex = currentIndex === -1 ? 0 : currentIndex
  const targetIndex = Math.min(Math.max(safeIndex + offset, 0), options.length - 1)
  selectedWeek.value = options[targetIndex] || options[0] || 'all'
}

function shouldRenderCell(rowKey: string, day: number) {
  const row = tableRows.value.find((item) => item.key === rowKey)
  if (!row?.unit) return true
  const occupied = occupiedMap.value.get(`${row.unit}-${day}`)
  if (!occupied) return true
  return occupied.startKey === String(row.unit)
}

function applyTimeTableLayout(layout?: any) {
  const units = Array.isArray(layout?.courseUnitList) ? layout.courseUnitList : []
  if (!units.length) {
    tableRows.value = DEFAULT_ROW_CONFIG.map((item) => ({ ...item }))
    return
  }
  tableRows.value = units
    .slice()
    .sort((left: any, right: any) => Number(left.indexNo || 0) - Number(right.indexNo || 0))
    .map((item: any, index: number) => ({
      key: String(item.indexNo || index + 1),
      unit: Number(item.indexNo || index + 1),
      label: item.nameZh || String(item.indexNo || index + 1),
      sideColor: dayPartColor(item.dayPart),
      rowClass: rowClassByDayPart(item.dayPart),
    }))
}

function dayPartColor(dayPart?: string) {
  if (dayPart === 'NOON') return '#ffe7a3'
  if (dayPart === 'AFTERNOON') return '#c9f0ea'
  if (dayPart === 'EVENING') return '#d7eef8'
  return '#dcecff'
}

function rowClassByDayPart(dayPart?: string) {
  if (dayPart === 'NOON') return 'row-noon'
  if (dayPart === 'AFTERNOON') return 'row-afternoon'
  if (dayPart === 'EVENING') return 'row-evening'
  return 'row-morning'
}

function dayPartText(rowClass?: string) {
  if (rowClass === 'row-noon') return '中午'
  if (rowClass === 'row-afternoon') return '下午'
  if (rowClass === 'row-evening') return '晚上'
  return '上午'
}

function getRowSpan(rowKey: string, day: number) {
  const row = tableRows.value.find((item) => item.key === rowKey)
  if (!row?.unit) return 1
  const occupied = occupiedMap.value.get(`${row.unit}-${day}`)
  return occupied?.span || 1
}

function getActivity(rowKey: string, day: number) {
  const row = tableRows.value.find((item) => item.key === rowKey)
  if (!row?.unit) return null
  return mergedActivityMap.value.get(`${row.unit}-${day}`) || null
}

function shouldRenderTimeArea(rowKey: string) {
  const rows = printTableRows.value
  const index = rows.findIndex((item: any) => item.key === rowKey)
  if (index === -1) return true
  return index === 0 || rows[index - 1].dayPartLabel !== rows[index].dayPartLabel
}

function getTimeAreaSpan(rowKey: string) {
  const rows = printTableRows.value
  const index = rows.findIndex((item: any) => item.key === rowKey)
  if (index === -1) return 1
  const currentLabel = rows[index].dayPartLabel
  let span = 1
  for (let cursor = index + 1; cursor < rows.length; cursor += 1) {
    if (rows[cursor].dayPartLabel !== currentLabel) break
    span += 1
  }
  return span
}

function extractWeeks(text?: string) {
  if (!text) return []
  const values = new Set<number>()
  const normalized = String(text)
    .replace(/第/g, '')
    .replace(/周次/g, '')
    .replace(/周/g, '')
    .replace(/[，、；;]/g, ',')
    .replace(/[～~至到]/g, '-')
    .replace(/[（]/g, '(')
    .replace(/[）]/g, ')')
    .replace(/\s+/g, '')
  normalized.split(',').forEach((segment) => {
    const part = segment.trim()
    if (!part) return
    const oddOnly = part.includes('单')
    const evenOnly = part.includes('双')
    const cleaned = part
      .replace(/\(单\)|\(双\)|单周|双周|单|双|\(|\)/g, '')
    if (!cleaned) return
    if (cleaned.includes('-')) {
      let [start, end] = cleaned.split('-').map((item) => Number(item))
      if (Number.isFinite(start) && Number.isFinite(end)) {
        if (start > end) [start, end] = [end, start]
        let current = start
        const step = oddOnly || evenOnly ? 2 : 1
        if (oddOnly && current % 2 === 0) current += 1
        if (evenOnly && current % 2 !== 0) current += 1
        for (let week = current; week <= end; week += step) values.add(week)
      }
      return
    }
    const single = Number(cleaned)
    if (!Number.isFinite(single)) return
    if ((!oddOnly && !evenOnly) || (oddOnly && single % 2 !== 0) || (evenOnly && single % 2 === 0)) {
      values.add(single)
    }
  })
  return Array.from(values)
}

function getStudentCount(item: any) {
  const actual = Number(item?.studentCount ?? item?.actualStudentCount)
  const limit = Number(item?.studentLimit)
  if (!Number.isFinite(actual) && !Number.isFinite(limit)) return '0'
  if (!Number.isFinite(limit) || limit <= 0) return `${Number.isFinite(actual) ? actual : 0}`
  return `${Number.isFinite(actual) ? actual : 0}/${limit}`
}

function formatTeachers(item: any) {
  if (item?._parallelTeachers?.length > 1) return item._parallelTeachers.join(', ')
  return item?.teacherName || ''
}

function displayClassCourseName(item: any) {
  if (!item) return '-'
  const displayName = String(item?.courseName || '').trim()
  const baseCourseName = String(item?.baseCourseName || '').trim()
  const selectionOptionName = String(item?.selectionOptionName || '').trim()
  if (isClassMode.value && Number(item?._parallelCount || 0) > 1 && baseCourseName) {
    return baseCourseName
  }
  if (baseCourseName && selectionOptionName && baseCourseName !== selectionOptionName) {
    const compactSelectionOptionName = isClassMode.value
      ? getClassScheduleSelectionOptionName(selectionOptionName)
      : selectionOptionName
    if (isClassMode.value && compactSelectionOptionName && compactSelectionOptionName !== selectionOptionName) {
      return `${baseCourseName} ${compactSelectionOptionName}`.trim()
    }
    return `${baseCourseName} / ${selectionOptionName}`
  }
  return displayName || baseCourseName || selectionOptionName || '-'
}

function getClassScheduleSelectionOptionName(selectionOptionName: string) {
  const text = String(selectionOptionName || '').trim()
  if (!text) return ''
  const match = text.match(/([0-9一二三四五六七八九十百]+\s*专项)$/)
  return match ? match[1].replace(/\s+/g, '') : text
}

function isVirtualClassroomText(value: any) {
  const text = String(value || '').trim()
  if (!text) return false
  return ['空教室', '空场地', '不占用教室'].some((keyword) => text.includes(keyword))
}

function formatLocation(item: any) {
  if (!item || isVirtualClassroomText(item?.classroom)) return ''
  const values = [item?.campus, item?.buildingName, item?.classroom]
  const result: string[] = []
  values.forEach((value) => {
    const text = String(value || '').trim()
    if (!text) return
    if (result.some((item) => item === text || item.includes(text) || text.includes(item))) return
    result.push(text)
  })
  return result.join(' ')
}

function formatWeeksAndUnits(item: any) {
  return `(${formatWeeksLabel(item)}) (${getSectionText(item)})`
}

function formatWeeksLabel(item: any) {
  const text = String(item?.weeksStr || item?.weeksText || '').trim()
  if (!text) return '全周'
  if (text === '全周' || text.includes('周')) return text
  return `${text}周`
}

function formatPrintCourseName(item: any) {
  const title = displayClassCourseName(item)
  const flags: string[] = []
  const parallelOptions = formatPrintParallelOptions(item)
  if (parallelOptions) flags.push(parallelOptions)
  else if (Number(item?._parallelCount || 0) > 1) flags.push(`${item._parallelCount}个专项`)
  if (Number(item?._combinedCount || 0) > 1) flags.push('合班')
  return `${title}${flags.length ? `（${flags.join('，')}）` : ''}`.trim()
}

function formatPrintParallelOptions(item: any) {
  const names = Array.isArray(item?._parallelOptionNames)
    ? item._parallelOptionNames.map((name: any) => String(name || '').trim()).filter(Boolean)
    : []
  if (!names.length) return ''
  return Array.from(new Set(names)).join('、')
}

function formatPrintLocationTeachers(item: any) {
  return [formatLocation(item), formatTeachers(item)].filter(Boolean).join(' ')
}

function getUnitLabel(unit: number) {
  const row = tableRows.value.find((item: any) => Number(item.unit) === Number(unit))
  return String(row?.label || unit)
}

function getSectionText(item: any) {
  const start = Number(item?.startSection || 0)
  const end = Number(item?.endSection || start)
  if (!start) return '-'
  const labels: string[] = []
  for (let unit = start; unit <= end; unit += 1) {
    labels.push(getUnitLabel(unit))
  }
  if (!labels.length) return `${item?.startSection || '-'}-${item?.endSection || '-'}节`
  return `${labels[0]}-${labels[labels.length - 1]}节`
}

function escapeHtml(value: any) {
  return String(value ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

function buildPrintStyles() {
  return `
    @page { size: A4 landscape; margin: 12mm; }
    * { box-sizing: border-box; }
    html, body { width: 100%; height: 100%; }
    body { margin: 0; font-family: "Microsoft YaHei", "PingFang SC", sans-serif; color: #000; background: #fff; }
    .print-page { width: 100%; max-width: 100%; padding: 6mm 8mm 4mm; }
    .print-toolbar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 12px;
      margin-bottom: 10px;
      padding-bottom: 8px;
      border-bottom: 1px dashed #cbd5e1;
      font-size: 12px;
      color: #475569;
    }
    .print-toolbar__actions {
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .print-toolbar button {
      border: 1px solid #94a3b8;
      background: #fff;
      color: #0f172a;
      padding: 5px 14px;
      border-radius: 999px;
      cursor: pointer;
      font-size: 12px;
    }
    .print-toolbar button.primary {
      border-color: #2563eb;
      background: #2563eb;
      color: #fff;
    }
    .tableTopName {
      font-size: 19px;
      text-align: center;
      letter-spacing: 0.32em;
      margin-bottom: 6px;
      line-height: 1.25;
      font-weight: 500;
    }
    .semester-container { letter-spacing: normal; font-weight: 600; margin-left: 6px; }
    .table-head-info {
      display: grid;
      grid-template-columns: repeat(6, minmax(0, 1fr));
      gap: 4px 12px;
      font-size: 12px;
      line-height: 1.5;
      margin-bottom: 8px;
      padding: 0 2px;
    }
    .table-head-info span { display: flex; align-items: center; gap: 2px; min-width: 0; }
    .table-head-info label { color: #333; white-space: nowrap; }
    .table-head-info span > span:last-child {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-weight: 500;
    }
    .courseTable {
      width: 100%;
      border-collapse: collapse;
      table-layout: fixed;
      font-size: 12px;
      word-break: break-all;
      border: 1px solid #111;
    }
    .courseTable th, .courseTable td { border: 1px solid #111; padding: 0; vertical-align: top; }
    .courseTable thead th {
      height: 28px;
      text-align: center;
      vertical-align: middle;
      font-weight: 700;
      background: #fff;
      font-size: 12px;
    }
    .courseTable thead th:first-child { width: 5%; }
    .timeArea, .dayPartUnit { text-align: center; vertical-align: middle !important; }
    .dayPartUnit { width: 5%; font-weight: 500; }
    .td-content { height: auto; min-height: 36px; }
    .tdHtml {
      min-height: 34px;
      padding: 4px 5px;
      line-height: 1.42;
      font-size: 11px;
    }
    .tdHtml strong {
      display: block;
      font-size: 12px;
      line-height: 1.3;
      margin-bottom: 1px;
    }
    .course-table-remark {
      margin-top: 6px;
      font-size: 11px;
      line-height: 1.55;
      min-height: 20px;
    }
    .course-table-footer {
      margin-top: 4px;
      font-size: 11px;
      display: flex;
      justify-content: space-between;
      gap: 12px;
      align-items: center;
    }
    @media print {
      .print-toolbar { display: none !important; }
      .print-page { padding: 0; }
    }
  `
}

function buildPrintRowsHtml() {
  return printTableRows.value.map((row: any) => {
    const dayCells = weekDays.map((day) => {
      if (!shouldRenderCell(row.key, day.value)) return ''
      const activity = getActivity(row.key, day.value)
      const content = activity ? `
        <div class="tdHtml">
          <strong>${escapeHtml(formatPrintCourseName(activity) || '未命名课程')}</strong>
          <div>${escapeHtml(formatWeeksAndUnits(activity))}</div>
          <div>${escapeHtml(formatPrintLocationTeachers(activity))}</div>
          <div>${escapeHtml(activity.lessonName || activity.className || '')}</div>
          <div>${escapeHtml(`人数:${getStudentCount(activity)}`)}</div>
        </div>
      ` : '<div class="tdHtml"></div>'
      return `<td class="td-content" rowspan="${getRowSpan(row.key, day.value)}">${content}</td>`
    }).join('')
    return `<tr><td class="dayPartUnit">${escapeHtml(row.label)}</td>${dayCells}</tr>`
  }).join('')
}

function buildPrintRemarksHtml() {
  if (!remarkItems.value.length) return ''
  const remarks = remarkItems.value.map((item: any, index: number) => {
    const text = `${index + 1}.${item.courseName || item.course?.nameZh || '未排课程'} ${item.code || item.course?.code || ''} ${item.teacherAssignmentString || ''}${item.remark ? ` 任务备注:(${item.remark})` : ''}`
    return escapeHtml(text)
  }).join('&nbsp;&nbsp;&nbsp;')
  return `<div class="course-table-remark">备注: ${remarks}</div>`
}

function buildPrintHtml() {
  const info = printStudentInfo.value
  const totalCredits = printTotalCredits.value ? `<span>总学分：${escapeHtml(printTotalCredits.value)}</span>` : '<span></span>'
  return `
    <!doctype html>
    <html lang="zh-CN">
      <head>
        <meta charset="UTF-8" />
        <title>${escapeHtml(printSchoolTitle.value)}</title>
        <style>${buildPrintStyles()}
.schedule-badge {
  display: inline-block;
  font-size: 10px;
  font-weight: 700;
  line-height: 1;
  padding: 2px 5px;
  border-radius: 3px;
  margin-left: 4px;
  vertical-align: middle;
}
.schedule-badge--parallel {
  background: rgba(230, 162, 60, 0.18);
  color: #b88230;
}
.schedule-badge--combined {
  background: rgba(64, 158, 255, 0.16);
  color: #2b7fd4;
}
.schedule-tooltip {
  font-size: 12px;
  line-height: 1.6;
  max-width: 280px;
}
.schedule-tooltip b {
  font-size: 13px;
}
</style>
      </head>
      <body>
        <div class="print-page">
          <div class="print-toolbar">
            <div>打印预览已准备完成，请确认无误后再打印。</div>
            <div class="print-toolbar__actions">
              <button class="primary" onclick="window.print()">打印</button>
              <button onclick="window.close()">关闭</button>
            </div>
          </div>
          <div class="tableTopName">${escapeHtml(printSchoolTitle.value)} <span class="semester-container">(${escapeHtml(printTermName.value)})</span></div>
          <div class="table-head-info">
            <span><label>年级：</label><span>${escapeHtml(info.grade)}</span></span>
            <span><label>学院：</label><span>${escapeHtml(info.college)}</span></span>
            <span><label>专业：</label><span>${escapeHtml(info.major)}</span></span>
            <span><label>班级：</label><span>${escapeHtml(info.className)}</span></span>
            <span><label>学号：</label><span>${escapeHtml(info.studentNo)}</span></span>
            <span><label>姓名：</label><span>${escapeHtml(info.name)}</span></span>
          </div>
          <table class="courseTable">
            <thead>
              <tr>
                <th></th>
                ${weekDays.map((day) => `<th>${escapeHtml(day.label)}</th>`).join('')}
              </tr>
            </thead>
            <tbody>${buildPrintRowsHtml()}</tbody>
          </table>
          ${buildPrintRemarksHtml()}
          <div class="course-table-footer">${totalCredits}<span>打印日期:${escapeHtml(printDate.value)}</span></div>
        </div>
      </body>
    </html>
  `
}

function formatDate(value?: string) {
  if (!value) return ''
  return String(value).slice(0, 10).replace(/-/g, '.')
}

function printSchedule() {
  const html = buildPrintHtml()
  const blob = new Blob([html], { type: 'text/html;charset=utf-8' })
  const printUrl = URL.createObjectURL(blob)
  const screenWidth = window.screen?.availWidth || 1440
  const screenHeight = window.screen?.availHeight || 900
  const features = [
    'toolbar=no',
    'location=no',
    'status=no',
    'menubar=no',
    'scrollbars=yes',
    'resizable=yes',
    `width=${screenWidth}`,
    `height=${screenHeight}`,
    'left=0',
    'top=0'
  ].join(',')
  const printWindow = window.open(printUrl, '_blank', features)
  if (!printWindow) return

  const prepareWindow = () => {
    try {
      printWindow.moveTo(0, 0)
      printWindow.resizeTo(screenWidth, screenHeight)
    } catch {}
    printWindow.focus()
  }
  printWindow.onload = () => setTimeout(prepareWindow, 150)
  printWindow.onbeforeunload = () => {
    URL.revokeObjectURL(printUrl)
  }
}

function getCardStyle(item: any) {
  const color = activityColorMap.value.get(resolveActivityColorKey(item)) || buildActivityColor(0)
  return {
    background: color.background,
    borderLeft: `2px solid ${color.border}`,
  }
}

function resolveActivityColorKey(item: any) {
  return String(item?.classCourseId || item?.courseCode || item?.scheduleId || '').trim()
}

function buildActivityColor(index: number) {
  const hue = Math.round((index * 137.508 + 18) % 360)
  const saturationCycle = [84, 74, 88, 78]
  const lightnessCycle = [48, 44, 52, 46]
  const backgroundCycle = [95, 93, 96, 94]
  const saturation = saturationCycle[index % saturationCycle.length]
  const lightness = lightnessCycle[Math.floor(index / saturationCycle.length) % lightnessCycle.length]
  const backgroundLightness = backgroundCycle[index % backgroundCycle.length]
  return {
    border: `hsl(${hue}, ${saturation}%, ${lightness}%)`,
    background: `hsl(${hue}, ${Math.max(52, saturation - 22)}%, ${backgroundLightness}%)`,
  }
}

onMounted(async () => {
  if (route.query.termId) {
    queryParams.termId = Number(route.query.termId)
  }
  await loadTerms()
  await loadSchedule()
})
</script>

<style scoped>
.schedule-page {
  padding: 20px;
  height: 100%;
  background: #fff;
  border-radius: 4px;
}

.schedule-header-user {
  margin-bottom: 16px;
  padding: 0;
}

.schedule-header-user h3 {
  font-size: 16px;
  color: #333;
  margin: 0;
  font-weight: bold;
}

.schedule-content {
  margin-top: 0;
  padding: 0;
}

.schedule-print-sheet {
  display: none;
}

.table-container {
  margin-top: 14px;
}

.schedule-filters {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.schedule-select {
  width: 148px;
}

.schedule-select--term {
  width: 196px;
}

.schedule-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.schedule-term-date {
  margin-left: 12px;
  font-size: 13px;
  color: #606266;
}

.schedule-actions-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}

.lab-checkbox {
  margin-right: 8px;
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
  border-radius: 0;
  overflow: hidden;
  background: #fff;
  outline: 1px solid #c8d8eb;
  outline-offset: -1px;
}

.courseTable--print {
  table-layout: fixed;
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

.courseTable__section-head {
  text-align: center !important;
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

.dayPartUnit {
  width: 4.8%;
  text-align: center !important;
  vertical-align: middle !important;
  color: #172033;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-shadow: none;
  box-shadow: inset -1px 0 0 #d4e0ef, inset 0 -1px 0 #d4e0ef, inset 0 0 0 1px rgba(255,255,255,0.22);
}

.timeArea {
  width: 2%;
  text-align: center !important;
  vertical-align: middle !important;
  color: #172033;
}

.td-content {
  position: relative;
  background: linear-gradient(180deg, rgba(255,255,255,0.88) 0%, rgba(247,250,253,0.96) 100%);
  height: 52px;
  box-shadow: inset -1px 0 0 #d4e0ef, inset 0 -1px 0 #d4e0ef, inset 0 0 0 0.5px rgba(255,255,255,0.14);
  background-clip: padding-box;
}

.tdHtml {
  margin: 2px;
  padding: 5px 8px 6px;
  color: #4c5d72;
  height: calc(100% - 4px);
  min-height: 46px;
  overflow: hidden;
  border-radius: 0;
  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.05);
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.tdHtml--empty {
  background: transparent;
  border-left: none;
  box-shadow: none;
}

.tdHtml--print {
  display: block;
  min-height: 30px;
  margin: 0;
  padding: 2px 3px;
  color: #111827;
  box-shadow: none;
  border-left: none !important;
  background: transparent !important;
  line-height: 1.4;
}

.course-name {
  color: #122033;
  font-size: 15px;
  font-weight: 800;
  line-height: 1.18;
  margin-bottom: 3px;
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

.course-meta-item i,
.course-population i {
  flex: 0 0 auto;
  font-size: 13px;
  color: #245a97;
}

.course-meta-item span,
.lesson-name,
.course-population span {
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
  justify-content: space-between;
  gap: 8px;
  margin-top: auto;
}

.lesson-name {
  color: #1d4f91;
  font-weight: 600;
  margin-top: 1px;
  flex: 1;
  font-size: 12px;
  line-height: 1.25;
}

.course-population {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  flex: 0 0 auto;
  padding: 1px 6px;
  border-radius: 999px;
  background: rgba(36, 90, 151, 0.08);
  color: #23476d;
  font-size: 12px;
  font-weight: 600;
}

.schedule-badge {
  display: inline-block;
  font-size: 10px;
  font-weight: 700;
  line-height: 1;
  padding: 2px 5px;
  border-radius: 3px;
  margin-left: 4px;
  vertical-align: middle;
}

.schedule-badge--parallel {
  background: rgba(230, 162, 60, 0.18);
  color: #b88230;
}

.schedule-badge--combined {
  background: rgba(64, 158, 255, 0.16);
  color: #2b7fd4;
}

.schedule-tooltip {
  font-size: 12px;
  line-height: 1.6;
  max-width: 280px;
}

.schedule-tooltip b {
  font-size: 13px;
}

.course-table-fontSize {
  font-size: 11px;
}

.course-table-remark {
  margin-top: 12px;
  color: #445169;
  line-height: 1.8;
  padding: 10px 12px;
  border-radius: 0;
  background: rgba(255, 255, 255, 0.72);
}

.course-table-remark span {
  margin-right: 16px;
}

.course-table-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 8px;
}

.pull-right {
  margin-left: auto;
}

@media (max-width: 900px) {
  .schedule-filters {
    align-items: stretch;
  }

  .schedule-select,
  .schedule-select--term {
    width: 100%;
  }

  .schedule-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .schedule-meta {
    width: 100%;
  }

  .courseTable {
    min-width: 980px;
  }
}

@media print {
  .schedule-page {
    padding: 0;
    background: #fff;
  }

  .portal-section-title,
  .schedule-actions,
  .el-empty,
  .schedule-filters,
  .courseTable:not(.courseTable--print),
  .course-table-remark:not(.course-table-remark--print),
  .course-table-footer:not(.course-table-footer--print) {
    display: none !important;
  }

  .schedule-shell {
    box-shadow: none;
    background: #fff;
    border: none;
    padding: 0;
  }

  .table-container {
    width: 1055px;
    margin: 0 auto;
  }

  .schedule-print-sheet {
    display: block;
    padding-bottom: 30px;
    color: #000;
    page-break-inside: avoid;
  }

  .tableTopName {
    margin-bottom: 0;
    font-size: 20px;
    letter-spacing: 0.45em;
    line-height: 1.4;
    text-align: center;
  }

  .semester-container {
    font-size: 20px;
    letter-spacing: normal;
  }

  .table-head-info {
    margin: 2px 0 2px;
    font-size: 12px;
    line-height: 1.4;
  }

  .table-head-info span {
    display: inline-block;
    margin: 0 auto;
    vertical-align: top;
  }

  .table-head-info > span:nth-child(1) {
    width: 10%;
  }

  .table-head-info > span:nth-child(2),
  .table-head-info > span:nth-child(3),
  .table-head-info > span:nth-child(4) {
    width: 20%;
  }

  .table-head-info > span:nth-child(5),
  .table-head-info > span:nth-child(6) {
    width: 15%;
  }

  .table-head-info label {
    margin-bottom: 0;
  }

  .courseTable--print {
    width: 100%;
    margin-bottom: 0;
    font-size: 12px;
    word-break: break-all;
    white-space: normal;
    border: 1px solid #000;
  }

  .courseTable--print > thead > tr > th {
    border: 1px solid #000;
    vertical-align: middle;
    padding: 0;
    text-align: center;
    height: 25px;
    background: #fff !important;
  }

  .courseTable--print > tbody > tr > td {
    padding: 0;
    max-height: 44px;
    border: 1px solid #000;
    text-align: left;
    vertical-align: top;
    background: #fff !important;
  }

  .courseTable--print .dayPartUnit,
  .courseTable--print .timeArea {
    width: 2%;
    text-align: center !important;
    vertical-align: middle !important;
    color: #000;
  }

  .courseTable--print .td-content {
    height: auto;
  }

  .courseTable--print .tdHtml {
    min-height: 30px;
  }

  .courseTable--print .course-name {
    font-size: 12px;
    font-weight: 700;
    text-decoration: none;
    margin-bottom: 0;
  }

  .courseTable--print .course-name,
  .courseTable--print .tdHtml div {
    color: #000;
  }

  .courseTable--print .tdHtml--empty {
    min-height: 30px;
  }

  .course-table-remark--print {
    margin-top: 0;
    padding: 0;
    line-height: 1.5;
    color: #000;
    background: transparent;
  }

  .course-table-footer--print {
    margin-top: 2px;
    color: #000;
  }
}
</style>
