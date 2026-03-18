<template>
  <div class="app-container">
    <div class="page-title-row">
      <div>
        <h2 class="page-title-row__title">排课表</h2>
        <p class="page-title-row__desc">用学期、班级课程、节次和教室维护正式课表，为后续学生端“我的课表”做准备。</p>
      </div>
    </div>

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
      <el-form-item label="班级课程"><el-select v-model="queryParams.classCourseId" filterable clearable style="width:280px"><el-option v-for="item in classCourseOptions" :key="`class-course-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="教室"><el-select v-model="queryParams.classroomId" filterable clearable style="width:260px"><el-option v-for="item in classroomOptions" :key="`classroom-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="星期"><el-select v-model="queryParams.weekDay" clearable style="width:160px"><el-option v-for="item in weekOptions" :key="`week-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="周次"><el-select v-model="queryParams.weekNo" clearable style="width:160px"><el-option v-for="item in weekNumberOptions" :key="`week-${item}`" :label="`第 ${item} 周`" :value="item" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <el-col :span="1.8"><el-button type="warning" plain icon="MagicStick" :loading="autoArrangeLoading" @click="openAutoArrangeDialog">自动排课</el-button></el-col>
      <el-col :span="4">
        <el-radio-group v-model="viewMode" size="default">
          <el-radio-button label="table">列表视图</el-radio-button>
          <el-radio-button label="board">周课表</el-radio-button>
        </el-radio-group>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-if="viewMode === 'table'" v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="班级课程" min-width="300"><template #default="{ row }">{{ getOptionLabel(classCourseOptions, row.classCourseId, '班级课程') }}</template></el-table-column>
      <el-table-column label="学期" min-width="160"><template #default="{ row }">{{ getOptionLabel(termOptions, row.termId, '学期') }}</template></el-table-column>
      <el-table-column label="星期" width="90"><template #default="{ row }">{{ weekLabel(row.weekDay) }}</template></el-table-column>
      <el-table-column label="节次" width="120"><template #default="{ row }">{{ getSectionText(row) }}</template></el-table-column>
      <el-table-column label="教室" min-width="220"><template #default="{ row }">{{ row.classroomName || row.classroom || '待定教室' }}{{ row.buildingName ? ` · ${row.buildingName}` : '' }}</template></el-table-column>
      <el-table-column label="周次" prop="weeksText" min-width="140" />
      <el-table-column label="人数" width="100"><template #default="{ row }">{{ getTableStudentCount(row) }}</template></el-table-column>
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="150" fixed="right"><template #default="{ row }"><el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button></template></el-table-column>
    </el-table>

    <el-card v-else class="schedule-board-card">
      <div class="schedule-toolbar">
        <div class="schedule-weekbar">
          <el-button type="primary" @click="setCurrentWeek">本周</el-button>
          <el-button @click="shiftWeek(-1)">上一周</el-button>
          <el-button type="primary" plain @click="shiftWeek(1)">下一周</el-button>
        </div>
        <div class="schedule-toolbar__hint">当前筛选：{{ getOptionLabel(termOptions, queryParams.termId, '学期') }} / {{ getOptionLabel(classCourseOptions, queryParams.classCourseId, '班级课程') }} / {{ queryParams.weekNo ? `第 ${queryParams.weekNo} 周` : '全部周次' }}</div>
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
                  <div
                    v-if="getSchedule(row.key, day.value)"
                    class="schedule-card"
                    :style="getCardStyle(getSchedule(row.key, day.value))"
                  >
                    <div class="course-name">{{ getScheduleTitle(getSchedule(row.key, day.value)) }}</div>
                    <div class="course-meta-list">
                      <div class="course-meta-item course-meta-item--wide">
                        <i class="ri-calendar-event-line"></i>
                        <span>{{ getScheduleWeeks(getSchedule(row.key, day.value)) }} · {{ getSectionText(getSchedule(row.key, day.value)) }}</span>
                      </div>
                      <div class="course-meta-item course-meta-item--wide">
                        <i class="ri-map-pin-2-line"></i>
                        <span>{{ getScheduleRoom(getSchedule(row.key, day.value)) }}</span>
                      </div>
                      <div class="course-meta-item">
                        <i class="ri-user-star-line"></i>
                        <span>{{ getScheduleTeacher(getSchedule(row.key, day.value)) }}</span>
                      </div>
                    </div>
                    <div class="course-footer">
                      <div class="lesson-name">{{ getSchedule(row.key, day.value)?.className || '-' }}</div>
                      <div class="course-population">
                        <i class="ri-group-line"></i>
                        <span>{{ getScheduleStudentCount(getSchedule(row.key, day.value)) }}</span>
                      </div>
                    </div>
                  </div>
                </td>
              </template>
            </tr>
          </tbody>
        </table>
      </div>
      <el-empty v-if="!loading && !visibleSchedules.length" description="当前条件下暂无排课数据" />
    </el-card>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="980px">
      <TeachingAiAssist module-key="courseSchedule" module-label="排课表" :form-data="form" :selected-rows="selectedRows" :available-options="{ termOptions, classCourseOptions, classroomOptions }" @apply="applyAiDraft" />
      <div class="dialog-dual-grid">
        <div class="dialog-panel">
          <div class="dialog-panel__title">排课条件</div>
          <el-form :model="form" label-width="92px">
            <el-form-item label="学期"><el-select v-model="form.termId" filterable clearable style="width:100%"><el-option v-for="item in termOptions" :key="`dialog-term-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
            <el-form-item label="班级课程"><el-select v-model="form.classCourseId" filterable clearable style="width:100%"><el-option v-for="item in classCourseOptions" :key="`dialog-class-course-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
            <el-form-item label="星期"><el-select v-model="form.weekDay" style="width:100%"><el-option v-for="item in weekOptions" :key="`dialog-week-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
            <el-form-item label="教室"><el-select v-model="form.classroomId" filterable clearable style="width:100%" @change="handleClassroomChange"><el-option v-for="item in classroomOptions" :key="`dialog-classroom-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item>
            <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
            <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="4" /></el-form-item>
          </el-form>
        </div>

        <div class="dialog-panel">
          <div class="dialog-panel__title">节次与周次</div>
          <el-form :model="form" label-width="92px">
            <el-form-item label="开始节次"><el-input-number v-model="form.startSection" :min="1" :max="12" style="width:100%" /></el-form-item>
            <el-form-item label="结束节次"><el-input-number v-model="form.endSection" :min="1" :max="12" style="width:100%" /></el-form-item>
            <div class="section-preview">
              <strong>当前节次显示</strong>
              <span>{{ getSectionText(form) || '未选择节次' }}</span>
            </div>
            <el-form-item label="周次">
              <div class="weeks-field">
                <div class="weeks-field__toolbar">
                  <span class="weeks-field__meta">共 {{ currentFormTotalWeeks }} 周</span>
                  <div class="weeks-field__actions">
                    <el-button link type="primary" @click="selectAllWeeks">全选</el-button>
                    <el-button link @click="clearWeeks">清空</el-button>
                  </div>
                </div>
                <el-select
                  v-model="form.selectedWeeks"
                  multiple
                  filterable
                  collapse-tags
                  collapse-tags-tooltip
                  placeholder="请选择上课周次"
                  style="width:100%"
                >
                  <el-option
                    v-for="item in currentWeekOptions"
                    :key="`dialog-week-no-${item}`"
                    :label="`第${item}周`"
                    :value="item"
                  />
                </el-select>
                <div class="weeks-field__preview">已生成: {{ form.weeksText || '未选择周次' }}</div>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <el-alert
        v-if="conflictState.hasConflict"
        class="mt12"
        type="warning"
        :closable="false"
        show-icon
        :title="buildConflictTitle()"
        :description="buildConflictDescription()"
      />
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>

    <el-dialog v-model="autoArrangeOpen" title="自动排课配置" width="920px">
      <el-form :model="autoArrangeForm" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="学期"><el-select v-model="autoArrangeForm.termId" style="width:100%"><el-option v-for="item in termOptions" :key="`auto-term-${item.value}`" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="保留已有排课"><el-switch v-model="autoArrangeForm.keepExisting" inline-prompt active-text="是" inactive-text="否" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="课块优先长度"><el-select v-model="autoArrangeForm.preferredSessionDurations" multiple collapse-tags collapse-tags-tooltip style="width:100%"><el-option label="4节连排" :value="4" /><el-option label="3节连排" :value="3" /><el-option label="2节连排" :value="2" /><el-option label="1节拆分" :value="1" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="禁排时段"><el-select v-model="autoArrangeForm.excludedDayParts" multiple collapse-tags collapse-tags-tooltip style="width:100%"><el-option label="中午" value="NOON" /><el-option label="晚上" value="EVENING" /><el-option label="下午" value="AFTERNOON" /><el-option label="上午" value="MORNING" /></el-select></el-form-item></el-col>
        </el-row>
      </el-form>

        <el-table :data="autoArrangeItems" max-height="420">
        <el-table-column label="参与" width="80">
          <template #default="{ row }"><el-switch v-model="row.selected" /></template>
        </el-table-column>
        <el-table-column label="班级课程" min-width="240">
          <template #default="{ row }">{{ buildClassCourseLabel(row) }}</template>
        </el-table-column>
        <el-table-column label="默认周次" min-width="130">
          <template #default="{ row }">{{ row.requiredWeeks ? `1-${row.requiredWeeks}周` : '全学期' }}</template>
        </el-table-column>
        <el-table-column label="本次排课周次" min-width="240">
          <template #default="{ row }">
            <el-select v-model="row.selectedWeeks" multiple collapse-tags collapse-tags-tooltip style="width:100%">
              <el-option v-for="item in autoArrangeWeekOptions" :key="`auto-week-${row.id}-${item}`" :label="`第${item}周`" :value="item" />
            </el-select>
            <div class="auto-arrange-week-preview">{{ row.weeksText || '未选择周次' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="每周最多节次" width="140">
          <template #default="{ row }"><el-input-number v-model="row.maxWeeklySections" :min="1" :max="12" style="width:100%" /></template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="autoArrangeOpen=false">取消</el-button>
        <el-button type="primary" :loading="autoArrangeLoading" @click="handleAutoArrange">开始自动排课</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addCourseSchedule, autoArrangeCourseSchedule, checkCourseScheduleConflict, delCourseSchedule, getTimeTableLayout, listClassCourse, listCourseSchedule, updateCourseSchedule } from '@/api/campus/teaching'
import { fetchClassroomOptions, fetchTermOptions } from '@/api/campus/options'
import { deptTreeSelect } from '@/api/system/user'
import TeachingAiAssist from '@/components/Teaching/TeachingAiAssist.vue'
import { resolveCurrentWeek } from '@/utils/termWeek'

const loading=ref(false), showSearch=ref(true), total=ref(0), open=ref(false), title=ref(''), ids=ref<any[]>([]), single=ref(true), multiple=ref(true), dataList=ref<any[]>([]), selectedRows=ref<any[]>([])
const autoArrangeLoading = ref(false)
const autoArrangeOpen = ref(false)
const termOptions=ref<any[]>([])
const deptOptions=ref<any[]>([])
const classCourseOptions=ref<any[]>([])
const classroomOptions=ref<any[]>([])
const classCourseMap = ref<Map<number, any>>(new Map())
const autoArrangeItems = ref<any[]>([])
const viewMode = ref('table')
const currentWeek = ref(1)
const queryParams=reactive<any>({ pageNum:1,pageSize:10,termId:undefined,deptId:undefined,classCourseId:undefined,classroomId:undefined,weekDay:undefined,weekNo:undefined })
const form=reactive<any>({})
const autoArrangeForm = reactive<any>({
  termId: undefined,
  keepExisting: true,
  preferredSessionDurations: [4, 2, 1],
  excludedDayParts: ['NOON', 'EVENING'],
})
const conflictState = reactive<any>({ hasConflict:false })
const weekOptions = [{ label:'星期一', value:1 },{ label:'星期二', value:2 },{ label:'星期三', value:3 },{ label:'星期四', value:4 },{ label:'星期五', value:5 },{ label:'星期六', value:6 },{ label:'星期日', value:7 }]
const defaultTableRows = [
  { key:'1', unit:1, label:'1', sideColor:'#dcecff' },
  { key:'2', unit:2, label:'2', sideColor:'#dcecff' },
  { key:'3', unit:3, label:'3', sideColor:'#dcecff' },
  { key:'4', unit:4, label:'4', sideColor:'#dcecff' },
  { key:'noon-1', unit:null, label:'中午1', sideColor:'#ffe7a3' },
  { key:'noon-2', unit:null, label:'中午', sideColor:'#ffe7a3' },
  { key:'5', unit:5, label:'5', sideColor:'#c9f0ea' },
  { key:'6', unit:6, label:'6', sideColor:'#c9f0ea' },
  { key:'7', unit:7, label:'7', sideColor:'#c9f0ea' },
  { key:'8', unit:8, label:'8', sideColor:'#c9f0ea' },
  { key:'9', unit:9, label:'9', sideColor:'#d7eef8' },
  { key:'10', unit:10, label:'10', sideColor:'#d7eef8' },
]
const tableRows = ref<any[]>([...defaultTableRows])
const currentFormTerm = computed(() => termOptions.value.find((item:any)=>item.value===form.termId))
const currentTermMeta = computed(() => termOptions.value.find((item:any)=>item.value===queryParams.termId) || {})
const currentFormTotalWeeks = computed(() => {
  const total = Number(currentFormTerm.value?.totalWeeks || 20)
  return total > 0 ? total : 20
})
const currentTermTotalWeeks = computed(() => {
  const total = Number(currentTermMeta.value?.totalWeeks || 20)
  return total > 0 ? total : 20
})
const currentWeekOptions = computed(() => Array.from({ length: currentFormTotalWeeks.value }).map((_, index) => index + 1))
const weekNumberOptions = computed(() => Array.from({ length: currentTermTotalWeeks.value }).map((_, index) => index + 1))
const autoArrangeWeekOptions = computed(() => {
  const term = termOptions.value.find((item:any)=>item.value===autoArrangeForm.termId) || currentTermMeta.value || {}
  const total = Number(term?.totalWeeks || 20)
  return Array.from({ length: total > 0 ? total : 20 }).map((_, index) => index + 1)
})
const visibleSchedules = computed(() => dataList.value.filter((item:any) => matchesWeek(item.weeksText, currentWeek.value)))
const activityMap = computed(() => {
  const map = new Map<string, any>()
  visibleSchedules.value.forEach((item: any) => {
    const startUnit = Number(item.startSection || 0)
    const weekDay = Number(item.weekDay || 0)
    if (!startUnit || !weekDay) return
    map.set(`${startUnit}-${weekDay}`, item)
  })
  return map
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
const headerStats = computed(() => [
  { label: '当前页排课', value: total.value, tip: '支持按学期和班级课程筛选' },
  { label: '已启用', value: dataList.value.filter((item:any)=>item.status==='0').length, tip: '将直接进入课表视图' },
  { label: '上课日覆盖', value: new Set(visibleSchedules.value.map((item:any)=>item.weekDay).filter(Boolean)).size, tip: '为周课表做准备' },
])
function weekLabel(value:any){ return weekOptions.find(item=>item.value===Number(value))?.label || `星期${value || '-'}` }
function getOptionLabel(options:any[], value:any, fallback:string){ return options.find((item)=>item.value===value)?.label || `${fallback} ${value || '-'}` }
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
function parseWeeksText(text?: string){
  if(!text) return []
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
    if(!part) return
    const oddOnly = part.includes('单')
    const evenOnly = part.includes('双')
    const cleaned = part.replace(/\(单\)|\(双\)|单周|双周|单|双|\(|\)/g, '')
    if(!cleaned) return
    if(cleaned.includes('-')){
      let [start, end] = cleaned.split('-').map((item)=>Number(item))
      if(Number.isFinite(start) && Number.isFinite(end)){
        if(start > end) [start, end] = [end, start]
        let current = start
        const step = oddOnly || evenOnly ? 2 : 1
        if(oddOnly && current % 2 === 0) current += 1
        if(evenOnly && current % 2 !== 0) current += 1
        for(let week = current; week <= end; week += step) values.add(week)
      }
      return
    }
    const single = Number(cleaned)
    if(!Number.isFinite(single)) return
    if((!oddOnly && !evenOnly) || (oddOnly && single % 2 !== 0) || (evenOnly && single % 2 === 0)){
      values.add(single)
    }
  })
  return Array.from(values).sort((a,b)=>a-b)
}
function buildWeeksText(weeks: number[]){
  const sorted = Array.from(new Set((weeks || []).map((item)=>Number(item)).filter((item)=>Number.isFinite(item) && item > 0))).sort((a,b)=>a-b)
  if(!sorted.length) return ''
  const segments: string[] = []
  let start = sorted[0]
  let prev = sorted[0]
  for(let index = 1; index <= sorted.length; index += 1){
    const current = sorted[index]
    if(current === prev + 1){
      prev = current
      continue
    }
    segments.push(start === prev ? `${start}` : `${start}-${prev}`)
    start = current
    prev = current
  }
  return `${segments.join(',')}周`
}
function matchesWeek(text: string | undefined, week: number) {
  const weeks = parseWeeksText(text)
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
  const item = activityMap.value.get(`${row.unit}-${day}`)
  return item ? getMergedSchedule(item) : null
}
function getScheduleTitle(item: any) {
  return item?.courseName || getOptionLabel(classCourseOptions.value, item?.classCourseId, '班级课程')
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
  return result.join(' ')
}
function getScheduleTeacher(item: any) {
  return item?.teacherName || '未配置'
}
function getScheduleStudentCount(item: any) {
  const actual = item?.actualStudentCount
  const limit = item?.studentLimit
  if (actual == null && limit == null) return '0'
  if (limit == null || limit === 0) return `${actual ?? 0}`
  return `${actual ?? 0}/${limit}`
}
function getTableStudentCount(item: any) {
  const merged = getMergedSchedule(item)
  return getScheduleStudentCount(merged)
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
function buildConflictTitle() {
  if (conflictState.conflictType === 'teacher') {
    return `检测到教师时间冲突：${conflictState.teacherName || '当前教师'} 在 ${weekLabel(conflictState.weekDay)} ${conflictState.startSection}-${conflictState.endSection} 节已有授课安排`
  }
  return `检测到教室时间冲突：${conflictState.classroomName || '当前教室'} 在 ${weekLabel(conflictState.weekDay)} ${conflictState.startSection}-${conflictState.endSection} 节已被占用`
}
function buildConflictDescription() {
  const location = `${conflictState.buildingName ? '，楼栋：' + conflictState.buildingName : ''}${conflictState.campusName ? '，校区：' + conflictState.campusName : ''}`
  if (conflictState.conflictType === 'teacher') {
    const classCourse = `${conflictState.className ? '，班级：' + conflictState.className : ''}${conflictState.classCourseName ? '，课程：' + conflictState.classCourseName : ''}`
    return `冲突周次：${conflictState.weeksText || '-'}${classCourse}${conflictState.classroomName ? '，教室：' + conflictState.classroomName : ''}${location}`
  }
  return `冲突周次：${conflictState.weeksText || '-'}${location}`
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
function resetForm(){
  const fallbackTermId = queryParams.termId ?? termOptions.value.find((item:any)=>item.isCurrent === '1')?.value ?? termOptions.value[0]?.value
  const fallbackTotalWeeks = Number(termOptions.value.find((item:any)=>item.value===fallbackTermId)?.totalWeeks || 20)
  Object.assign(form,{
    scheduleId:undefined,
    termId:fallbackTermId,
    classCourseId:undefined,
    classroomId:undefined,
    classroom:'',
    weekDay:1,
    startSection:1,
    endSection:2,
    selectedWeeks:Array.from({ length: fallbackTotalWeeks > 0 ? fallbackTotalWeeks : 20 }).map((_, index) => index + 1),
    weeksText:'',
    status:'0',
    remark:''
  })
  form.weeksText = buildWeeksText(form.selectedWeeks)
  Object.assign(conflictState,{ hasConflict:false })
}
function applyAiDraft(draft: Record<string, any>) {
  Object.assign(form, draft)
  form.selectedWeeks = Array.isArray(draft.selectedWeeks) && draft.selectedWeeks.length
    ? draft.selectedWeeks.map((item:any)=>Number(item)).filter((item:number)=>Number.isFinite(item))
    : parseWeeksText(draft.weeksText)
  form.weeksText = buildWeeksText(form.selectedWeeks)
}
async function getList(){
  loading.value=true
  const useClientWeekFilter = viewMode.value === 'board' || queryParams.weekNo != null
  const params = useClientWeekFilter
    ? { ...queryParams, pageNum:1, pageSize:500 }
    : queryParams
  const res=await listCourseSchedule(params)
  let rows = res.rows || []
  if (queryParams.weekNo != null) {
    rows = rows.filter((item:any) => matchesWeek(item.weeksText, Number(queryParams.weekNo)))
  }
  if (viewMode.value === 'table' && queryParams.weekNo != null) {
    total.value = rows.length
    const start = (Number(queryParams.pageNum || 1) - 1) * Number(queryParams.pageSize || 10)
    const end = start + Number(queryParams.pageSize || 10)
    dataList.value = rows.slice(start, end)
  } else {
    dataList.value=rows
    total.value=queryParams.weekNo != null ? rows.length : (res.total||0)
  }
  if (viewMode.value === 'board') {
    currentWeek.value = Number(queryParams.weekNo || 0) || currentWeek.value
    if (!queryParams.weekNo) setCurrentWeek()
  }
  loading.value=false
}
function resetQuery(){ queryParams.pageNum=1; queryParams.termId=undefined; queryParams.deptId=undefined; queryParams.classCourseId=undefined; queryParams.classroomId=undefined; queryParams.weekDay=undefined; queryParams.weekNo=undefined; getList() }
function handleSelectionChange(selection:any[]){ selectedRows.value=selection; ids.value=selection.map((i:any)=>i.scheduleId); single.value=selection.length!==1; multiple.value=!selection.length }
function handleAdd(){ resetForm(); title.value='新增排课'; open.value=true }
function handleUpdate(row?:any){
  const item=row || dataList.value.find((i:any)=>i.scheduleId===ids.value[0])
  if(!item) return
  resetForm()
  Object.assign(form,item)
  form.weekDay = Number(form.weekDay || 1)
  form.selectedWeeks = parseWeeksText(form.weeksText)
  form.weeksText = buildWeeksText(form.selectedWeeks)
  title.value='修改排课'
  open.value=true
}
function handleClassroomChange(value:any){ const option = classroomOptions.value.find((item:any)=>item.value===value); form.classroom = option?.classroomName || '' }
function selectAllWeeks(){ form.selectedWeeks = [...currentWeekOptions.value] }
function clearWeeks(){ form.selectedWeeks = [] }
async function checkConflict(){
  if(!open.value) return
  if(!form.classroomId || !form.termId || !form.weekDay || !form.startSection || !form.endSection) { Object.assign(conflictState,{ hasConflict:false }); return }
  const res=await checkCourseScheduleConflict(form)
  Object.assign(conflictState,{ hasConflict:false },res.data || {})
}
async function submitForm(){
  form.weeksText = buildWeeksText(form.selectedWeeks || [])
  form.weeksJson = JSON.stringify((form.selectedWeeks || []).map((item:any)=>Number(item)).filter((item:number)=>Number.isFinite(item)))
  if(!form.weeksText){ ElMessage.warning('请选择至少一个上课周次'); return }
  await checkConflict()
  if(conflictState.hasConflict){ ElMessage.warning(conflictState.conflictType === 'teacher' ? '当前排课存在教师时间冲突，请调整后再保存' : '当前排课存在教室时间冲突，请调整后再保存'); return }
  if(form.scheduleId){ await updateCourseSchedule(form); ElMessage.success('修改成功') } else { await addCourseSchedule(form); ElMessage.success('新增成功') }
  open.value=false; getList()
}
async function handleDelete(row?:any){ const target=row?.scheduleId || ids.value; if(!target || (Array.isArray(target)&&!target.length)) return; await ElMessageBox.confirm('确认删除所选排课吗？','提示',{type:'warning'}); await delCourseSchedule(target); ElMessage.success('删除成功'); getList() }
function openAutoArrangeDialog(){
  autoArrangeForm.termId = queryParams.termId ?? termOptions.value.find((item:any)=>item.isCurrent === '1')?.value ?? termOptions.value[0]?.value
  autoArrangeItems.value = Array.from(classCourseMap.value.values())
    .filter((item:any)=> !queryParams.classCourseId || item.id === queryParams.classCourseId)
    .map((item:any)=>({
      ...item,
      weeksText: item.requiredWeeks ? `1-${item.requiredWeeks}周` : '',
      selectedWeeks: item.requiredWeeks ? Array.from({ length: item.requiredWeeks }).map((_, index) => index + 1) : [...autoArrangeWeekOptions.value],
      maxWeeklySections: item.weeklyHours || 2,
      selected: true,
    }))
  if(!autoArrangeItems.value.length){
    ElMessage.warning('当前条件下没有可配置的班级课程')
    return
  }
  autoArrangeOpen.value = true
}
async function handleAutoArrange(){
  const targetTermId = autoArrangeForm.termId ?? queryParams.termId ?? termOptions.value.find((item:any)=>item.isCurrent === '1')?.value ?? termOptions.value[0]?.value
  if(!targetTermId){ ElMessage.warning('请先选择学期后再自动排课'); return }
  const selectedItems = autoArrangeItems.value.filter((item:any)=>item.selected !== false)
  if(!selectedItems.length){ ElMessage.warning('请至少保留一个班级课程参与自动排课'); return }
  autoArrangeLoading.value = true
  try{
    const res = await autoArrangeCourseSchedule({
      termId: targetTermId,
      classCourseIds: selectedItems.map((item:any)=>item.id),
      items: selectedItems.map((item:any)=>({
        classCourseId: item.id,
        weeksText: item.weeksText,
        weeksJson: JSON.stringify(item.selectedWeeks || []),
        maxWeeklySections: item.maxWeeklySections,
      })),
      clearExistingSchedules: !autoArrangeForm.keepExisting,
      populationSize: 60,
      generationCount: 120,
      mutationRate: 0.12,
      preferredSessionDurations: autoArrangeForm.preferredSessionDurations,
      excludedDayParts: autoArrangeForm.excludedDayParts,
    })
    const data = res.data || {}
    const arranged = Number(data.arrangedLessonTasks || 0)
    const totalTask = Number(data.totalLessonTasks || 0)
    const unscheduled = Array.isArray(data.unscheduledLessons) ? data.unscheduledLessons.length : 0
    ElMessage.success(`自动排课完成，已排 ${arranged}/${totalTask} 个课块${unscheduled ? `，未排 ${unscheduled} 个` : ''}`)
    autoArrangeOpen.value = false
    getList()
  } finally {
    autoArrangeLoading.value = false
  }
}
async function loadOptions(){
  termOptions.value=await fetchTermOptions()
  const deptRes = await deptTreeSelect()
  deptOptions.value = deptRes.data || []
  classroomOptions.value=await fetchClassroomOptions()
  try {
    const layoutRes = await getTimeTableLayout()
    buildTableRowsFromLayout(layoutRes.data)
  } catch {
    tableRows.value = [...defaultTableRows]
  }
  const res=await listClassCourse({ pageNum:1,pageSize:200,status:'0', termId: queryParams.termId, openDeptId: queryParams.deptId })
  const rows = res.rows || []
  classCourseMap.value = new Map(rows.map((item:any) => [Number(item.id), item]))
  classCourseOptions.value=rows.map((item:any)=>({ label:buildClassCourseLabel(item), value:item.id }))
}
watch(() => form.selectedWeeks, (value) => {
  form.weeksText = buildWeeksText(value || [])
}, { deep:true })
watch(() => autoArrangeItems.value.map((item:any)=>item.selectedWeeks), (value) => {
  autoArrangeItems.value.forEach((item:any, index:number) => {
    item.weeksText = buildWeeksText(value[index] || [])
  })
}, { deep:true })
watch(() => form.termId, () => {
  const limit = currentFormTotalWeeks.value
  const current = Array.isArray(form.selectedWeeks) ? form.selectedWeeks.map((item:any)=>Number(item)).filter((item:number)=>Number.isFinite(item) && item >= 1 && item <= limit) : []
  form.selectedWeeks = current.length ? current : [...currentWeekOptions.value]
})
watch(() => autoArrangeForm.termId, () => {
  const limit = autoArrangeWeekOptions.value
  autoArrangeItems.value.forEach((item:any) => {
    const current = Array.isArray(item.selectedWeeks) ? item.selectedWeeks.map((value:any)=>Number(value)).filter((value:number)=>limit.includes(value)) : []
    item.selectedWeeks = current.length ? current : [...limit]
    item.weeksText = buildWeeksText(item.selectedWeeks)
  })
})
watch(() => [queryParams.termId, queryParams.deptId], async () => {
  const res=await listClassCourse({ pageNum:1,pageSize:200,status:'0', termId: queryParams.termId, openDeptId: queryParams.deptId })
  const rows = res.rows || []
  classCourseMap.value = new Map(rows.map((item:any) => [Number(item.id), item]))
  classCourseOptions.value=rows.map((item:any)=>({ label:buildClassCourseLabel(item), value:item.id }))
  if (!classCourseOptions.value.some((item:any)=>item.value===queryParams.classCourseId)) queryParams.classCourseId = undefined
})
watch(viewMode, () => { getList() })
watch(() => queryParams.weekNo, (value) => {
  queryParams.pageNum = 1
  if (viewMode.value === 'board' && value != null) {
    currentWeek.value = Number(value)
  }
})
watch(() => [open.value, form.classroomId, form.termId, form.weekDay, form.startSection, form.endSection, form.weeksText, form.status], () => { checkConflict() })
onMounted(async()=>{ await loadOptions(); resetForm(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px}
.page-title-row{display:flex;align-items:flex-end;justify-content:space-between;gap:16px;margin-bottom:16px}
.page-title-row__title{margin:0;color:#172033;font-size:24px;line-height:1.2}
.page-title-row__desc{margin:6px 0 0;color:#526076;font-size:13px;line-height:1.8}
.dialog-dual-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:16px}
.dialog-panel{padding:14px 14px 6px;border:1px solid #e4ebf4;border-radius:16px;background:linear-gradient(180deg,#fff 0%,#fafcff 100%)}
.dialog-panel__title{margin-bottom:14px;color:#172033;font-size:14px;font-weight:700}
.section-preview{margin:-4px 0 10px;padding:10px 12px;border-radius:12px;background:#f6f9fc;color:#526076;font-size:12px;line-height:1.7}
.section-preview strong{display:block;color:#172033;font-size:12px;margin-bottom:4px}
.mt12{margin-top:12px}
.weeks-field{width:100%}
.weeks-field__toolbar{display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:8px}
.weeks-field__meta{color:#667085;font-size:12px}
.weeks-field__actions{display:flex;align-items:center;gap:8px}
.weeks-field__preview{margin-top:8px;color:#526076;font-size:12px;line-height:1.5}
.schedule-board-card{
  margin-bottom:16px;
  border:none;
  border-radius:22px;
  background:linear-gradient(180deg,#f8fbff 0%,#eef7f2 100%);
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
.el-dialog :deep(.el-dialog__body){padding-top:12px}
.el-dialog :deep(.el-dialog__footer){padding-top:8px}
.el-dialog :deep(.el-form-item:last-child){margin-bottom:10px}
.auto-arrange-week-preview{margin-top:6px;color:#667085;font-size:12px;line-height:1.5}
@media (max-width: 900px){.dialog-dual-grid{grid-template-columns:1fr}}
</style>
