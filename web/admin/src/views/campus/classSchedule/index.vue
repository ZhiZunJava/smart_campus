<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="学期"><el-select v-model="queryParams.termId" filterable clearable style="width:220px"><el-option v-for="item in termOptions" :key="`term-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="部门">
        <el-tree-select
          v-model="queryParams.deptId"
          :data="deptOptions"
          :props="{ value: 'id', label: 'label', children: 'children' }"
          value-key="id"
          check-strictly
          clearable
          filterable
          style="width:220px"
        />
      </el-form-item>
      <el-form-item label="班级"><el-select v-model="queryParams.classId" filterable clearable style="width:220px"><el-option v-for="item in classOptions" :key="`class-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="周次"><el-select v-model="currentWeek" style="width:160px"><el-option v-for="item in weekNumberOptions" :key="`week-${item}`" :label="`第 ${item} 周`" :value="item" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="loadData">加载课表</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <div v-if="filterTags.length" class="filter-tag-bar">
      <span class="filter-tag-bar__label">当前查看范围</span>
      <el-tag v-for="tag in filterTags" :key="tag.label" size="small" round>{{ tag.label }}</el-tag>
    </div>

    <el-card class="schedule-board-card">
      <div class="schedule-toolbar">
        <div class="schedule-weekbar">
          <el-button type="primary" @click="setCurrentWeek">本周</el-button>
          <el-button @click="shiftWeek(-1)">上一周</el-button>
          <el-button type="primary" plain @click="shiftWeek(1)">下一周</el-button>
        </div>
        <div class="schedule-toolbar__hint">第 {{ currentWeek }} 周 · {{ visibleSchedules.length }} 个课块</div>
      </div>

      <div class="schedule-board">
        <table class="courseTable" cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <th width="4.8%" class="courseTable__section-head">节次</th>
              <th v-for="day in weekOptions" :key="day.value" width="13.7%">{{ day.label }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in tableRows" :key="row.key">
              <td class="dayPartUnit" :style="{ background: row.sideColor }">{{ row.label }}</td>
              <template v-for="day in weekOptions" :key="`${day.value}-${row.key}`">
                <td
                  v-if="shouldRenderCell(row.key, day.value)"
                  class="schedule-board__cell"
                  :rowspan="getRowSpan(row.key, day.value)"
                >
                  <el-tooltip v-if="getSchedule(row.key, day.value)" placement="right" :show-after="300">

                    <template #content>

                      <div class="schedule-tooltip">

                        <div><b>{{ getScheduleTitle(getSchedule(row.key, day.value)) }}</b></div>

                        <div v-if="getSchedule(row.key, day.value)?._parallelCount">专项课程: {{ (getSchedule(row.key, day.value)?._parallelOptionNames || []).join(', ') }}</div>

                        <div v-if="getSchedule(row.key, day.value)?._combinedCount">合班: {{ getSchedule(row.key, day.value)._combinedClassNames }}</div>

                        <div>周次: {{ getScheduleWeeks(getSchedule(row.key, day.value)) }}</div>

                        <div>节次: {{ getSectionText(getSchedule(row.key, day.value)) }}</div>

                        <div v-if="getScheduleRoom(getSchedule(row.key, day.value))">教室: {{ getScheduleRoom(getSchedule(row.key, day.value)) }}</div>

                        <div>教师: {{ getScheduleTeacher(getSchedule(row.key, day.value)) }}</div>

                        <div>班级: {{ getSchedule(row.key, day.value)?._combinedClassNames || getSchedule(row.key, day.value)?.className || currentClassLabel }}</div>

                        <div>人数: {{ getScheduleStudentCount(getSchedule(row.key, day.value)) }}</div>

                      </div>

                    </template>
<div
                    class="schedule-card"
                    :style="getCardStyle(getSchedule(row.key, day.value))"
                  >
                    <div class="course-name">
                        <span>{{ getScheduleTitle(getSchedule(row.key, day.value)) }}</span>
                        <span v-if="getSchedule(row.key, day.value)?._parallelCount" class="schedule-badge schedule-badge--parallel">{{ getSchedule(row.key, day.value)._parallelCount }}专项</span>
                        <span v-if="getSchedule(row.key, day.value)?._combinedCount" class="schedule-badge schedule-badge--combined">合班</span>
                      </div>
                    <div class="course-meta-list">
                      <div class="course-meta-item course-meta-item--wide">
                        <i class="ri-calendar-event-line"></i>
                        <span>{{ getScheduleWeeks(getSchedule(row.key, day.value)) }} · {{ getSectionText(getSchedule(row.key, day.value)) }}</span>
                      </div>
                      <div v-if="getScheduleRoom(getSchedule(row.key, day.value))" class="course-meta-item course-meta-item--wide">
                        <i class="ri-map-pin-2-line"></i>
                        <span>{{ getScheduleRoom(getSchedule(row.key, day.value)) }}</span>
                      </div>
                      <div class="course-meta-item">
                        <i class="ri-user-star-line"></i>
                        <span>{{ getScheduleTeacher(getSchedule(row.key, day.value)) }}</span>
                      </div>
                    </div>
                    <div class="course-footer">
                      <div class="lesson-name">{{ getSchedule(row.key, day.value)?._combinedClassNames || getSchedule(row.key, day.value)?.className || currentClassLabel }}</div>
                      <div class="course-population">
                        <i class="ri-group-line"></i>
                        <span>{{ getScheduleStudentCount(getSchedule(row.key, day.value)) }}</span>
                      </div>
                    </div>
                  </div>
                  </el-tooltip>
                </td>
              </template>
            </tr>
          </tbody>
        </table>
      </div>

      <el-empty v-if="!loading && !visibleSchedules.length" description="当前条件下暂无班级课表数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
// @ts-nocheck
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { fetchClassOptions, fetchTermOptions } from '@/api/campus/options'
import { getTimeTableLayout, listClassCourse, listCourseSchedule } from '@/api/campus/teaching'
import { deptTreeSelect } from '@/api/system/user'
import { resolveCurrentWeek } from '@/utils/termWeek'

type WeekScheduleValue = number

const loading = ref(false)
const termOptions = ref<any[]>([])
const deptOptions = ref<any[]>([])
const classOptions = ref<any[]>([])
const classCourseOptions = ref<any[]>([])
const schedules = ref<any[]>([])
const classCourseMap = ref<Map<number, any>>(new Map())
const currentWeek = ref<WeekScheduleValue>(1)
const queryParams = reactive<any>({ termId: undefined, deptId: undefined, classId: undefined })
const weekOptions = [{ label:'星期一', value:1 },{ label:'星期二', value:2 },{ label:'星期三', value:3 },{ label:'星期四', value:4 },{ label:'星期五', value:5 },{ label:'星期六', value:6 },{ label:'星期日', value:7 }]
const defaultTableRows = [
  { key:'1', unit:1, label:'1', sideColor:'#dcecff' },
  { key:'2', unit:2, label:'2', sideColor:'#dcecff' },
  { key:'3', unit:3, label:'3', sideColor:'#dcecff' },
  { key:'4', unit:4, label:'4', sideColor:'#dcecff' },
  { key:'noon-1', unit:null, label:'中午', sideColor:'#ffe7a3' },
  { key:'noon-2', unit:null, label:'中午', sideColor:'#ffe7a3' },
  { key:'5', unit:5, label:'5', sideColor:'#c9f0ea' },
  { key:'6', unit:6, label:'6', sideColor:'#c9f0ea' },
  { key:'7', unit:7, label:'7', sideColor:'#c9f0ea' },
  { key:'8', unit:8, label:'8', sideColor:'#c9f0ea' },
  { key:'9', unit:9, label:'9', sideColor:'#d7eef8' },
  { key:'10', unit:10, label:'10', sideColor:'#d7eef8' },
]
const tableRows = ref<any[]>([...defaultTableRows])
const currentTermMeta = computed(() => termOptions.value.find((item:any)=>item.value===queryParams.termId) || {})
const currentTermTotalWeeks = computed(() => {
  const total = Number(currentTermMeta.value?.totalWeeks || 20)
  return total > 0 ? total : 20
})

const visibleSchedules = computed(() => schedules.value.filter((item: any) => matchesWeek(item.weeksText, currentWeek.value)))
const weekNumberOptions = computed(() => {
  return Array.from({ length: currentTermTotalWeeks.value }).map((_, index) => index + 1)
})
const activityMap = computed(() => {
  const map = new Map<string, any[]>()
  visibleSchedules.value.forEach((item: any) => {
    const startUnit = Number(item.startSection || 0)
    const weekDay = Number(item.weekDay || 0)
    if (!startUnit || !weekDay) return
    const key = `${startUnit}-${weekDay}`
    if (!map.has(key)) map.set(key, [])
    map.get(key)!.push(item)
  })
  return map
})
// Merged view: group parallel selection-group and combined-class schedules
const mergedActivityMap = computed(() => {
  const result = new Map<string, any>()
  for (const [key, items] of activityMap.value) {
    const enriched = items.map((i: any) => getMergedSchedule(i))
    if (enriched.length === 1) { result.set(key, enriched[0]); continue }
    const primary = { ...enriched[0] }
    const selGroupItems = enriched.filter((e: any) => e.selectionGroupCode && e.selectionGroupCode === primary.selectionGroupCode && e.classId === primary.classId)
    if (selGroupItems.length > 1) {
      primary._parallelOptionNames = selGroupItems.map((e: any) => e.selectionOptionName || e.teacherName || '').filter(Boolean)
      primary._parallelTeachers = [...new Set(selGroupItems.map((e: any) => e.teacherName).filter(Boolean))]
      primary._parallelCount = selGroupItems.length
    }
    const combinedItems = enriched.filter((e: any) => e.combinedClassCode && e.combinedClassCode === primary.combinedClassCode)
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
  visibleSchedules.value.forEach((item: any) => {
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
const currentClassLabel = computed(() => simplifyClassLabel(classOptions.value.find((item:any)=>item.value===queryParams.classId)?.label) || '全部班级')
const currentTermLabel = computed(() => termOptions.value.find((item:any)=>item.value===queryParams.termId)?.label || '全部学期')
const filterTags = computed(() => {
  const tags: Array<{ label: string }> = []
  const termLabel = currentTermLabel.value
  const classLabel = currentClassLabel.value
  const deptLabel = findDeptLabel(deptOptions.value, queryParams.deptId)
  if (termLabel && termLabel !== '全部学期') tags.push({ label: `学期：${termLabel}` })
  if (deptLabel) tags.push({ label: `部门：${deptLabel}` })
  if (classLabel && classLabel !== '全部班级') tags.push({ label: `班级：${classLabel}` })
  return tags
})

function getOptionLabel(options:any[], value:any, fallback:string){ return options.find((item)=>item.value===value)?.label || `${fallback} ${value || '-'}` }
function simplifyClassLabel(label?: string) {
  return String(label || '').split(' · ')[0]
}
function findDeptLabel(options: any[], value: any): string {
  if (value == null) return ''
  for (const item of options || []) {
    if (item?.id === value) return item.label || ''
    const child = findDeptLabel(item?.children || [], value)
    if (child) return child
  }
  return ''
}
function dayPartColor(dayPart?: string) {
  if (dayPart === 'NOON') return '#ffe7a3'
  if (dayPart === 'AFTERNOON') return '#c9f0ea'
  if (dayPart === 'EVENING') return '#d7eef8'
  return '#dcecff'
}
function buildTableRowsFromLayout(layout: any) {
  const units = Array.isArray(layout?.courseUnitList) ? layout.courseUnitList : []
  if (!units.length) {
    tableRows.value = [...defaultTableRows]
    return
  }
  tableRows.value = units
    .slice()
    .sort((left:any, right:any) => Number(left.indexNo || 0) - Number(right.indexNo || 0))
    .map((item:any, index:number) => ({
      key: String(item.indexNo || index + 1),
      unit: Number(item.indexNo || index + 1),
      label: item.nameZh || String(item.indexNo || index + 1),
      sideColor: dayPartColor(item.dayPart),
    }))
}
function buildClassCourseLabel(item:any){
  const className = item.className || `班级${item.classId || '-'}`
  const courseName = item.courseName || `课程${item.courseId || '-'}`
  return `${className} - ${courseName}（${item.id}）`
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
function matchesWeek(text: string | undefined, week: number) {
  const weeks = extractWeeks(text)
  return weeks.length ? weeks.includes(Number(week)) : true
}
function getMergedSchedule(item: any) {
  return { ...classCourseMap.value.get(Number(item.classCourseId)), ...item }
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
  return occupiedMap.value.get(`${row.unit}-${day}`)?.span || 1
}
function getSchedule(rowKey: string, day: number) {
  const row = tableRows.value.find((item) => item.key === rowKey)
  if (!row?.unit) return null
  return mergedActivityMap.value.get(`${row.unit}-${day}`) || null
}
function displayClassCourseName(item: any) {
  if (!item) return '-'
  const displayName = String(item?.courseName || '').trim()
  const baseCourseName = String(item?.baseCourseName || '').trim()
  const selectionOptionName = String(item?.selectionOptionName || '').trim()
  if (baseCourseName && selectionOptionName && baseCourseName !== selectionOptionName) return `${baseCourseName} / ${selectionOptionName}`
  return displayName || baseCourseName || selectionOptionName || '-'
}
function getScheduleTitle(item: any) {
  return displayClassCourseName(item)
}
function getScheduleWeeks(item: any) {
  return item?.weeksText || '全周'
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
function getUnitLabel(unit: number) {
  const row = tableRows.value.find((item) => Number(item.unit) === Number(unit))
  return String(row?.label || unit)
}
function getScheduleRoom(item: any) {
  const values = [item?.campusName || item?.campus, item?.buildingName, item?.classroomName || item?.classroom || '待定教室']
  const result: string[] = []
  values.forEach((value) => {
    const text = String(value || '').trim()
    if (!text) return
    if (result.some((item) => item === text || item.includes(text) || text.includes(item))) return
    result.push(text)
  })
  return result.join(' / ')
}
function getScheduleTeacher(item: any) {
  if (item?._parallelTeachers?.length > 1) return item._parallelTeachers.join(', ')
  return item?.teacherName || '未配置'
}
function getScheduleStudentCount(item: any) {
  const actual = item?.actualStudentCount
  const limit = item?.studentLimit
  if (actual == null && limit == null) return '0'
  if (limit == null || limit === 0) return `${actual ?? 0}`
  return `${actual ?? 0}/${limit}`
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
function getCardStyle(item: any) {
  const color = colorFromKey(item?.courseCode || item?.classCourseId || item?.scheduleId)
  return {
    background: `${hexToRgba(color, 0.18)}`,
    borderLeft: `2px solid ${color}`,
  }
}
function shiftWeek(offset:number){
  const options = weekNumberOptions.value
  const currentIndex = options.findIndex((item)=>item===currentWeek.value)
  const targetIndex = Math.min(Math.max(currentIndex + offset, 0), options.length - 1)
  currentWeek.value = options[targetIndex] || currentWeek.value
}
function setCurrentWeek(){
  const current = resolveCurrentWeek(currentTermMeta.value || {})
  currentWeek.value = weekNumberOptions.value.includes(current) ? current : (weekNumberOptions.value[0] || 1)
}
async function loadOptions(){
  termOptions.value = await fetchTermOptions()
  const deptRes = await deptTreeSelect()
  deptOptions.value = deptRes.data || []
  if (!queryParams.termId) {
    const currentTerm = termOptions.value.find((item: any) => item.isCurrent === '1')
    queryParams.termId = currentTerm?.value ?? termOptions.value[0]?.value
  }
  classOptions.value = await fetchClassOptions(undefined, queryParams.deptId)
  try {
    const layoutRes = await getTimeTableLayout()
    buildTableRowsFromLayout(layoutRes.data)
  } catch {
    tableRows.value = [...defaultTableRows]
  }
  if (!queryParams.classId && classOptions.value.length) queryParams.classId = classOptions.value[0].value
}
async function loadData(){
  loading.value = true
  try {
    if (!queryParams.termId) {
      const currentTerm = termOptions.value.find((item: any) => item.isCurrent === '1')
      queryParams.termId = currentTerm?.value ?? termOptions.value[0]?.value
    }
    if (!queryParams.classId) {
      schedules.value = []
      classCourseOptions.value = []
      classCourseMap.value = new Map()
      return
    }
    const classCourseRes = await listClassCourse({ pageNum:1, pageSize:200, classId: queryParams.classId, termId: queryParams.termId, status:'0' })
    const classCourseRows = classCourseRes.rows || []
    classCourseMap.value = new Map(classCourseRows.map((item:any) => [Number(item.id), item]))
    classCourseOptions.value = classCourseRows.map((item:any)=>({ label:buildClassCourseLabel(item), value:item.id }))
    const ids = classCourseOptions.value.map((item:any)=>item.value)
    const scheduleRes = await listCourseSchedule({ pageNum:1, pageSize:500, termId: queryParams.termId, status:'0' })
    schedules.value = (scheduleRes.rows || []).filter((item:any)=> ids.includes(item.classCourseId))
    setCurrentWeek()
  } finally {
    loading.value = false
  }
}
function resetQuery(){
  const currentTerm = termOptions.value.find((item: any) => item.isCurrent === '1')
  queryParams.termId = currentTerm?.value ?? termOptions.value[0]?.value
  queryParams.deptId = undefined
  queryParams.classId = undefined
  schedules.value = []
  classCourseOptions.value = []
  classCourseMap.value = new Map()
}
watch(() => queryParams.deptId, async (value) => {
  classOptions.value = await fetchClassOptions(undefined, value)
  if (!classOptions.value.some((item:any) => item.value === queryParams.classId)) queryParams.classId = undefined
  schedules.value = []
  classCourseOptions.value = []
  classCourseMap.value = new Map()
})
onMounted(async()=>{ await loadOptions(); await loadData() })
</script>

<style scoped>
.mb16{margin-bottom:16px}
.filter-tag-bar{display:flex;align-items:center;gap:8px;flex-wrap:wrap;margin:-2px 0 16px;padding:10px 12px;border-radius:14px;background:linear-gradient(180deg,#f8fbff 0%,#f3f8ff 100%);border:1px solid #dbe6f5}
.filter-tag-bar__label{color:#526076;font-size:12px;font-weight:600}
.schedule-board-card{
  margin-bottom:16px;
  border:none;
  border-radius:22px;
  background:linear-gradient(180deg,#f8fbff 0%,#eef5ff 100%);
  box-shadow:0 18px 42px rgba(15, 23, 42, 0.08);
}
.schedule-toolbar{display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:18px;flex-wrap:wrap}
.schedule-toolbar__hint{
  color:#526076;
  font-size:13px;
  padding:9px 14px;
  border-radius:999px;
  background:rgba(255,255,255,0.92);
  border:1px solid rgba(210,223,238,0.95);
}
.schedule-weekbar{display:flex;align-items:center;gap:8px;flex-wrap:wrap}
.schedule-board{
  overflow:auto;
  background:transparent;
}
.courseTable{
  width:100%;
  margin-bottom:0;
  font-size:12px;
  word-break:break-word;
  white-space:normal;
  border:1px solid #c8d8eb;
  border-collapse:collapse;
  table-layout:fixed;
}
.courseTable th{
  border:1px solid #d4e0ef;
  padding:0 8px;
  text-align:center;
  vertical-align:middle;
  height:40px;
  color:#172033;
  background:linear-gradient(180deg,#f6faff 0%,#edf4fb 100%);
  font-size:12px;
  font-weight:700;
  letter-spacing:0.02em;
}
.courseTable__section-head{
  text-align:center !important;
}
.courseTable td{
  padding:0;
  border:1px solid #d4e0ef;
  vertical-align:top;
  background:rgba(255,255,255,0.82);
}
.dayPartUnit{
  width:4.8%;
  text-align:center !important;
  vertical-align:middle !important;
  color:#172033;
  font-size:12px;
  font-weight:700;
  letter-spacing:0.04em;
  box-shadow: inset 0 0 0 1px rgba(255,255,255,0.28);
}
.schedule-board__cell{
  position:relative;
  height:52px;
  background:linear-gradient(180deg,rgba(255,255,255,0.88) 0%,rgba(247,250,253,0.96) 100%);
  box-shadow: inset 0 0 0 1px rgba(255,255,255,0.14);
}
.schedule-card{
  display:flex;
  flex-direction:column;
  gap:4px;
  padding:5px 8px 6px;
  min-height:46px;
  height:calc(100% - 4px);
  box-sizing:border-box;
  margin:2px;
  box-shadow:0 6px 16px rgba(15,23,42,0.05);
}
.course-name{
  color:#122033;
  font-size:15px;
  font-weight:800;
  line-height:1.18;
  margin-bottom:3px;
  text-decoration:underline;
  text-decoration-thickness:1px;
  text-underline-offset:2px;
  text-decoration-color:rgba(18,32,51,0.24);
}
.course-meta-list{display:flex;flex-direction:column;gap:2px;min-width:0}
.course-meta-item{
  display:inline-flex;
  align-items:center;
  gap:4px;
  min-width:0;
  color:#405068;
  font-size:12px;
  line-height:1.3;
}
.course-meta-item i,.course-population i{flex:0 0 auto;font-size:13px;color:#245a97}
.course-meta-item span,.lesson-name,.course-population span{min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}
.course-meta-item--wide{color:#33455d}
.course-footer{display:flex;align-items:center;justify-content:space-between;gap:8px;margin-top:auto}
.lesson-name{color:#1d4f91;font-weight:600;flex:1;font-size:12px;line-height:1.25}
.course-population{
  display:inline-flex;
  align-items:center;
  gap:3px;
  flex:0 0 auto;
  padding:1px 6px;
  border-radius:999px;
  background:rgba(36,90,151,0.08);
  color:#23476d;
  font-size:12px;
  font-weight:600;
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
</style>
