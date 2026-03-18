<template>
  <div class="portal-page schedule-page">
    <div class="portal-section-title">
      <h3>我的课表</h3>
      <el-tag type="primary">教师教务视图</el-tag>
    </div>

    <el-card class="portal-card schedule-shell">
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
          <el-button type="success" @click="setCurrentWeek">本周</el-button>
          <el-button type="success" plain @click="shiftWeek(-1)">上一周</el-button>
          <el-button type="success" plain @click="shiftWeek(1)">下一周</el-button>
        </div>

        <div class="schedule-meta">
          学期起始日期: {{ formatDate(currentTermMeta.startDate) || '-' }}，班级课程数: {{ classCourseCount }}
        </div>
      </div>

      <div class="table-container">
        <table class="courseTable" cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <th width="4%"></th>
              <th v-for="day in weekDays" :key="`head-${day.value}`" width="13.7%">{{ day.label }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in tableRows" :key="`row-${row.key}`">
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
                    <div>
                      ({{ getActivity(row.key, day.value).weeksStr || '全周' }})
                      ({{ getActivity(row.key, day.value).startSection }}-{{ getActivity(row.key, day.value).endSection }}节)
                      {{ getActivity(row.key, day.value).className || '未配置班级' }}
                    </div>
                    <div class="lesson-name">{{ getActivity(row.key, day.value).classroom || '待定教室' }}</div>
                    <div>教师:{{ getActivity(row.key, day.value).teacherName || '-' }}</div>
                  </div>
                  <div v-else class="tdHtml tdHtml--empty"></div>
                </td>
              </template>
            </tr>
          </tbody>
        </table>
      </div>

      <el-empty v-if="!loading && !activities.length" description="当前条件下暂无课表数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { getPortalTeacherSchedule, listPortalTermOptions } from '@/api/portal'
import usePortalUserStore from '@/store/user'
import { resolveCurrentWeek } from '@/utils/termWeek'

type WeekValue = number | 'all'

const DEFAULT_ROW_CONFIG = [
  { key: '1', unit: 1, label: '1', sideColor: '#3779b2' },
  { key: '2', unit: 2, label: '2', sideColor: '#3779b2' },
  { key: '3', unit: 3, label: '3', sideColor: '#3779b2' },
  { key: '4', unit: 4, label: '4', sideColor: '#3779b2' },
  { key: 'noon-1', unit: null, label: '中午', sideColor: '#ffc53d' },
  { key: 'noon-2', unit: null, label: '中午', sideColor: '#ffc53d' },
  { key: '5', unit: 5, label: '5', sideColor: '#46b7b1' },
  { key: '6', unit: 6, label: '6', sideColor: '#46b7b1' },
  { key: '7', unit: 7, label: '7', sideColor: '#46b7b1' },
  { key: '8', unit: 8, label: '8', sideColor: '#46b7b1' },
  { key: '9', unit: 9, label: '9', sideColor: '#62bdd8' },
  { key: '10', unit: 10, label: '10', sideColor: '#62bdd8' },
] as const

const userStore = usePortalUserStore()
const loading = ref(false)
const termOptions = ref<any[]>([])
const queryParams = reactive<any>({ termId: undefined })
const activities = ref<any[]>([])
const currentWeek = ref(1)
const currentWeekFromApi = ref(1)
const selectedWeek = ref<WeekValue>('all')
const classCourseCount = ref(0)

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

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
  const current = termOptions.value.find((item: any) => item.isCurrent === '1')
  if (!queryParams.termId && current) queryParams.termId = current.value
  currentWeek.value = resolveCurrentWeek(current || termOptions.value[0] || {})
  currentWeekFromApi.value = currentWeek.value
}

async function loadSchedule() {
  const teacherId = userStore.user?.userId
  if (!teacherId) return
  loading.value = true
  try {
    const res = await getPortalTeacherSchedule({ teacherId, termId: queryParams.termId })
    activities.value = res.data?.activities || []
    applyTimeTableLayout(res.data?.timeTableLayout)
    classCourseCount.value = Number(res.data?.classCourseCount || 0)
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
    }))
}

function dayPartColor(dayPart?: string) {
  if (dayPart === 'NOON') return '#ffc53d'
  if (dayPart === 'AFTERNOON') return '#46b7b1'
  if (dayPart === 'EVENING') return '#62bdd8'
  return '#3779b2'
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

function formatDate(value?: string) {
  if (!value) return ''
  return String(value).slice(0, 10)
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

onMounted(async () => {
  await loadTerms()
  await loadSchedule()
})
</script>

<style scoped>
.schedule-shell {
  overflow: hidden;
  border: none;
  border-radius: 0;
  background:
    radial-gradient(circle at top left, rgba(56, 200, 180, 0.14), transparent 28%),
    radial-gradient(circle at top right, rgba(121, 150, 202, 0.12), transparent 24%),
    linear-gradient(180deg, #fbfdff 0%, #f4f8fc 100%);
  box-shadow: 0 20px 48px rgba(15, 23, 42, 0.08);
  padding: 4px 4px 10px;
}
.table-container { margin-top: 14px; }
.schedule-filters { display:flex; align-items:center; gap:12px; flex-wrap:wrap; }
.schedule-select { width:148px; }
.schedule-select--term { width:168px; }
.schedule-actions { display:flex; align-items:center; gap:8px; }
.schedule-meta {
  color:#526076;
  font-size:13px;
  padding:8px 12px;
  border-radius:999px;
  background:rgba(255,255,255,0.8);
  border:1px solid rgba(210,223,238,0.9);
}
.courseTable { width:100%; margin-bottom:0; font-size:12px; word-break:break-word; white-space:normal; border:1px solid #dde8f3; border-collapse:collapse; table-layout:fixed; border-radius:0; overflow:hidden; }
.courseTable > thead > tr > th { border:1px solid #e3ebf5; padding:0 8px; text-align:center; height:46px; color:#172033; background:linear-gradient(180deg,#f6faff 0%,#edf4fb 100%); font-size:13px; font-weight:700; letter-spacing:0.02em; }
.courseTable > tbody > tr > td { padding:0; min-height:20px; border:1px solid #e3ebf5; text-align:left; vertical-align:top; background:rgba(255,255,255,0.82); }
.dayPartUnit { width:4%; text-align:center !important; vertical-align:middle !important; color:#102132; font-size:13px; font-weight:700; letter-spacing:0.04em; }
.td-content { position:relative; background:linear-gradient(180deg,rgba(255,255,255,0.88) 0%,rgba(247,250,253,0.96) 100%); height:88px; }
.tdHtml { margin:4px; padding:10px 10px 11px; color:#4c5d72; height:calc(100% - 8px); min-height:76px; overflow:hidden; border-radius:0; box-shadow:0 10px 24px rgba(15, 23, 42, 0.06); }
.tdHtml--empty { background:transparent; border-left:none; box-shadow:none; }
.course-name { color:#122033; font-size:14px; font-weight:700; line-height:1.35; margin-bottom:4px; }
.lesson-name { color:#1d4f91; font-weight:600; }
@media (max-width: 900px) {
  .schedule-filters { align-items:stretch; }
  .schedule-select, .schedule-select--term { width:100%; }
  .schedule-actions { width:100%; flex-wrap:wrap; }
  .schedule-meta { width:100%; }
  .courseTable { min-width:980px; }
}
</style>
