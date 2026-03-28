<template>
  <div class="portal-page schedule-page">
    <div class="schedule-header-user">
      <h3>{{ currentChild?.studentName || '未选择孩子' }}({{ currentChild?.studentNo || '--' }})</h3>
    </div>

    <div class="schedule-content">
      <div class="schedule-filters">
        <el-select
          v-model="queryParams.studentUserId"
          filterable
          placeholder="选择孩子"
          class="schedule-select schedule-select--term"
          @change="loadSchedule"
        >
          <el-option
            v-for="item in childOptions"
            :key="`child-${item.value}`"
            :label="item.label"
            :value="item.value"
          />
        </el-select>

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
          <span class="schedule-term-date">学期起始日期: {{ formatDate(currentTermMeta.startDate) || '-' }}</span>
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
                      <div class="course-name">{{ getActivity(row.key, day.value).courseName || '未命名课程' }}</div>
                      <div>{{ formatWeeksAndUnits(getActivity(row.key, day.value)) }}</div>
                      <div>{{ formatLocation(getActivity(row.key, day.value)) }} {{ formatTeachers(getActivity(row.key, day.value)) }}</div>
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
                  <div
                    v-if="getActivity(row.key, day.value)"
                    class="tdHtml"
                    :style="getCardStyle(getActivity(row.key, day.value))"
                  >
                    <div class="course-name">{{ getActivity(row.key, day.value).courseName || '未命名课程' }}</div>
                    <div class="course-meta-list">
                      <div class="course-meta-item course-meta-item--wide">
                        <i class="ri-calendar-event-line"></i>
                        <span>
                          {{ getActivity(row.key, day.value).weeksStr || '全周' }}
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
                      <div class="lesson-name">{{ getActivity(row.key, day.value).lessonName || getActivity(row.key, day.value).className || '' }}</div>
                      <div class="course-population">
                        <i class="ri-group-line"></i>
                        <span>{{ getStudentCount(getActivity(row.key, day.value)) }}</span>
                      </div>
                    </div>
                  </div>
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
import { useRoute } from 'vue-router'
import { getPortalParentSchedule, listPortalParentChildren, listPortalTermOptions } from '@/api/portal'
import usePortalUserStore from '@/store/user'
import { resolveCurrentWeek } from '@/utils/termWeek'

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
const childOptions = ref<any[]>([])
const queryParams = reactive<any>({ termId: undefined, studentUserId: undefined })
const activities = ref<any[]>([])
const remarkLessons = ref<any[]>([])
const currentWeek = ref(1)
const selectedWeek = ref<WeekValue>('all')
const currentWeekFromApi = ref(1)

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
const currentChild = computed(() => childOptions.value.find((item: any) => item.value === queryParams.studentUserId) || null)

const printSchoolTitle = computed(() => {
  const user = userStore.user || {}
  return `${user.schoolName || user.orgName || user.deptName || '校园智学'}学生课表`
})
const printTermName = computed(() => currentTermMeta.value?.termName || currentTermMeta.value?.label || '-')
const printTableRows = computed(() => tableRows.value.map((item: any) => ({
  ...item,
  dayPartLabel: dayPartText(item.rowClass),
})))
const printStudentInfo = computed(() => {
  const sample = visibleActivities.value[0] || activities.value[0] || {}
  return {
    grade: currentChild.value?.admissionYear || '--',
    college: currentChild.value?.deptName || currentChild.value?.college || '--',
    major: currentChild.value?.major || sample.major || '--',
    className: currentChild.value?.className || sample.className || sample.lessonName || '--',
    studentNo: currentChild.value?.studentNo || '--',
    name: currentChild.value?.studentName || '孩子',
  }
})
const printTotalCredits = computed(() => currentTermMeta.value?.totalCredits || '')
const printDate = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}/${now.getMonth() + 1}/${now.getDate()}`
})

const visibleActivities = computed(() => {
  if (selectedWeek.value === 'all') return activities.value
  return activities.value.filter((item: any) => extractWeeks(item.weeksStr || item.weeksText).includes(Number(selectedWeek.value)))
})

const activityMap = computed(() => {
  const map = new Map<string, any>()
  visibleActivities.value.forEach((item: any) => {
    const startKey = String(item.startSection || '')
    if (!startKey) return
    map.set(`${startKey}-${item.weekDay}`, item)
  })
  return map
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

async function loadChildren() {
  const parentUserId = userStore.user?.userId
  if (!parentUserId) return
  const res = await listPortalParentChildren({ parentUserId })
  childOptions.value = res.data || []
  if (!queryParams.studentUserId && childOptions.value.length) {
    queryParams.studentUserId = childOptions.value[0].value
  }
}

async function loadSchedule() {
  const parentUserId = userStore.user?.userId
  if (!parentUserId) return
  loading.value = true
  try {
    const res = await getPortalParentSchedule({ parentUserId, studentUserId: queryParams.studentUserId, termId: queryParams.termId })
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
  return activityMap.value.get(`${row.unit}-${day}`) || null
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
  return item?.studentCount ?? item?.studentLimit ?? 0
}

function formatTeachers(item: any) {
  return item.teacherName || ''
}

function formatLocation(item: any) {
  return [item.campus, item.buildingName, item.classroom].filter(Boolean).join(' ')
}

function formatWeeksAndUnits(item: any) {
  return `${item.weeksStr || '全周'} · ${getSectionText(item)}`
}

function formatDate(value?: string) {
  if (!value) return ''
  return String(value).slice(0, 10).replace(/-/g, '.')
}

function printSchedule() {
  const printNode = document.querySelector('.schedule-print-sheet') as HTMLElement | null
  if (!printNode) return
  const html = `
    <!doctype html>
    <html lang="zh-CN">
      <head>
        <meta charset="UTF-8" />
        <title>${printSchoolTitle.value}</title>
        <style>${buildPrintStyles()}</style>
      </head>
      <body>
        <div class="print-page">${printNode.innerHTML}</div>
        <script>
          window.onload = function() {
            setTimeout(function() {
              window.print();
            }, 120);
          };
        <\/script>
      </body>
    </html>
  `
  const blob = new Blob([html], { type: 'text/html;charset=utf-8' })
  const printUrl = URL.createObjectURL(blob)
  const printWindow = window.open(printUrl, '_blank', 'toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable=yes')
  if (!printWindow) return
  printWindow.onbeforeunload = () => {
    URL.revokeObjectURL(printUrl)
  }
}

function buildPrintStyles() {
  return `
    html, body {
      margin: 0;
      padding: 0;
      background: #fff;
      color: #000;
      font-family: "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
    }
    .print-page {
      width: 1055px;
      margin: 0 auto;
      padding: 12px 0 20px;
      color: #000;
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
    .table-head-info > span:nth-child(1) { width: 10%; }
    .table-head-info > span:nth-child(2),
    .table-head-info > span:nth-child(3),
    .table-head-info > span:nth-child(4) { width: 20%; }
    .table-head-info > span:nth-child(5),
    .table-head-info > span:nth-child(6) { width: 15%; }
    .courseTable {
      width: 100%;
      margin-bottom: 0;
      font-size: 12px;
      word-break: break-all;
      white-space: normal;
      border: 1px solid #000;
      border-collapse: collapse;
      table-layout: fixed;
    }
    .courseTable > thead > tr > th {
      border: 1px solid #000;
      vertical-align: middle;
      padding: 0;
      text-align: center;
      height: 25px;
      background: #fff !important;
    }
    .courseTable > tbody > tr > td {
      padding: 0;
      max-height: 44px;
      border: 1px solid #000;
      text-align: left;
      vertical-align: top;
      background: #fff !important;
    }
    .dayPartUnit, .timeArea {
      width: 2%;
      text-align: center !important;
      vertical-align: middle !important;
      color: #000;
    }
    .td-content { height: auto; }
    .tdHtml {
      min-height: 30px;
      margin: 0;
      padding: 2px 3px;
      color: #000;
      box-shadow: none;
      border-left: none !important;
      background: transparent !important;
      line-height: 1.4;
    }
    .course-name {
      font-size: 12px;
      font-weight: 700;
      text-decoration: none;
      margin-bottom: 0;
      color: #000;
    }
    .tdHtml div { color: #000; }
    .tdHtml--empty { min-height: 30px; }
    .course-table-fontSize { font-size: 11px; }
    .course-table-remark {
      margin-top: 0;
      padding: 0;
      line-height: 1.5;
      color: #000;
      background: transparent;
    }
    .course-table-footer {
      display: flex;
      justify-content: space-between;
      gap: 12px;
      margin-top: 2px;
      color: #000;
    }
    .pull-right { margin-left: auto; }
  `
}

function getCardStyle(item: any) {
  const color = colorFromKey(item.courseCode || item.classCourseId || item.scheduleId)
  return {
    background: `${hexToRgba(color, 0.18)}`,
    borderLeft: `2px solid ${color}`,
  }
}

function colorFromKey(key: string | number) {
  const palette = ['#d2a1f2', '#38c8b4', '#f49060', '#7996ca', '#a9ce95', '#6fb0f3', '#dac4a5', '#7fc5a6']
  const value = String(key || '')
  let hash = 0
  for (let index = 0; index < value.length; index += 1) {
    hash = ((hash << 5) - hash) + value.charCodeAt(index)
    hash |= 0
  }
  return palette[Math.abs(hash) % palette.length]
}

function hexToRgba(hex: string, alpha: number) {
  const normalized = hex.replace('#', '')
  const bigint = Number.parseInt(normalized, 16)
  const r = (bigint >> 16) & 255
  const g = (bigint >> 8) & 255
  const b = bigint & 255
  return `rgba(${r}, ${g}, ${b}, ${alpha})`
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

onMounted(async () => {
  if (route.query.termId) {
    queryParams.termId = Number(route.query.termId)
  }
  if (route.query.studentUserId) {
    queryParams.studentUserId = Number(route.query.studentUserId)
  }
  await loadTerms()
  await loadChildren()
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

  .schedule-term-date {
    margin-left: 0;
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

  .schedule-header-user,
  .schedule-actions,
  .schedule-actions-right,
  .el-empty,
  .schedule-filters,
  .courseTable:not(.courseTable--print),
  .course-table-remark:not(.course-table-remark--print),
  .course-table-footer:not(.course-table-footer--print) {
    display: none !important;
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
