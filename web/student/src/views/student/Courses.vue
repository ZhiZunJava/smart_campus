<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>我的课程</h3>
    </div>

    <section class="portal-kpis course-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">课程数</div><div class="value">{{ courseList.length }}</div><div class="sub">当前学期课程数量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">总周学时</div><div class="value">{{ totalHours }}</div><div class="sub">按班级课程累计</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">学科数</div><div class="value">{{ subjectCount }}</div><div class="sub">覆盖学科类型</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">学期</div><div class="value">{{ currentTermLabel }}</div><div class="sub">支持切换学年学期</div></el-card>
    </section>

    <el-card class="portal-card course-list-card">
      <div class="course-filter">
        <el-select v-model="queryParams.termId" filterable clearable placeholder="选择学期" style="width: 240px" @change="loadCourses">
          <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="keyword" clearable placeholder="搜索课程名称或课程编码" style="width: 280px" />
      </div>

      <div class="course-summary-bar">
        <div class="course-summary-bar__term">{{ summaryTermText }}</div>
        <div class="course-summary-bar__credit">总学分：{{ totalCredits }}</div>
      </div>

      <div class="course-sheet">
        <div class="course-sheet__head">
          <div>课程信息</div>
          <div>教学班</div>
          <div>时间地点人员</div>
          <div>人数</div>
          <div>教学材料</div>
        </div>

        <div v-for="row in filteredCourses" :key="`course-${row.id || row.courseId}`" class="course-sheet__row">
          <div class="course-sheet__cell course-sheet__cell--course">
            <button type="button" class="course-title-link" @click="openCourseDetail(row)">{{ row.courseName }}</button>
            <div class="course-meta-line">
              <span><i class="ri-barcode-box-line"></i>{{ row.courseCode || '-' }}</span>
              <span><i class="ri-building-4-line"></i>{{ row.openDeptName || '-' }}</span>
              <span><i class="ri-book-shelf-line"></i>{{ row.courseCategory || row.subjectType || '-' }}</span>
            </div>
            <div class="course-meta-line">
              <span><i class="ri-award-line"></i>{{ row.credits ?? '-' }}学分</span>
              <span><i class="ri-time-line"></i>{{ row.totalHours ?? '-' }}学时</span>
              <span><i class="ri-graduation-cap-line"></i>{{ row.major || '-' }}</span>
              <span><i class="ri-translate-2"></i>{{ row.teachingLanguage || '-' }}</span>
              <span><i class="ri-file-list-3-line"></i>{{ row.assessmentType || '-' }}</span>
            </div>
            <div v-if="row.remark" class="course-remark">备注：{{ row.remark }}</div>
          </div>

          <div class="course-sheet__cell">
            <div class="course-stack-line"><i class="ri-hashtag"></i>{{ row.teachingClassCode || '-' }}</div>
            <div class="course-stack-line muted"><i class="ri-team-line"></i>{{ row.className || '-' }}</div>
          </div>

          <div class="course-sheet__cell course-sheet__cell--schedule">
            <template v-if="(row.scheduleDetails || []).length">
              <div v-for="(item, index) in summarizeSchedules(row.scheduleDetails)" :key="`schedule-${index}`" class="schedule-line">
                <i class="ri-calendar-schedule-line"></i>
                <span>{{ item }}</span>
              </div>
            </template>
            <span v-else class="muted">无排课数据</span>
          </div>

          <div class="course-sheet__cell">
            <div class="course-count-list">
              <div class="course-count-item"><i class="ri-user-line"></i><span>实际：{{ row.actualStudentCount ?? '-' }}</span></div>
              <div class="course-count-item"><i class="ri-group-line"></i><span>上限：{{ row.studentLimit ?? '-' }}</span></div>
            </div>
          </div>

          <div class="course-sheet__cell course-sheet__cell--material">
            <span class="material-pill"><i class="ri-file-paper-2-line"></i>教学大纲</span>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && !filteredCourses.length" description="当前学期暂无课程" />
    </el-card>

    <el-dialog v-model="detailOpen" :title="currentCourse?.courseName || '任务详情'" width="1280px" top="4vh" class="course-detail-dialog">
      <div class="course-detail-header">
        <div class="course-detail-header__meta">
          <span><i class="ri-barcode-box-line"></i>{{ currentCourse?.courseCode || '-' }}</span>
          <span><i class="ri-hashtag"></i>{{ currentCourse?.teachingClassCode || '-' }}</span>
          <span><i class="ri-book-shelf-line"></i>{{ currentCourse?.courseCategory || '-' }}</span>
        </div>
      </div>
      <el-tabs v-model="detailTab">
        <el-tab-pane label="基本信息" name="basic">
          <div class="detail-grid">
            <div class="detail-row"><label>所属业务类型</label><span>{{ currentCourse?.businessType || '-' }}</span><label>所属学期</label><span>{{ currentCourse?.termName || '-' }}</span></div>
            <div class="detail-row"><label>课程代码</label><span>{{ currentCourse?.courseCode || '-' }}</span><label>课程名称</label><span>{{ currentCourse?.courseName || '-' }}</span></div>
            <div class="detail-row"><label>教学班代码</label><span>{{ currentCourse?.teachingClassCode || '-' }}</span><label>学分</label><span>{{ currentCourse?.credits ?? '-' }}</span></div>
            <div class="detail-row detail-row--full"><label>教学班名称</label><span>{{ currentCourse?.className || '-' }}</span></div>
            <div class="detail-row"><label>课程类别</label><span>{{ currentCourse?.courseCategory || '-' }}</span><label>开课部门</label><span>{{ currentCourse?.openDeptName || '-' }}</span></div>
            <div class="detail-row"><label>考核方式</label><span>{{ currentCourse?.assessmentType || '-' }}</span><label>授课校区</label><span>{{ currentCourse?.campusName || '-' }}</span></div>
            <div class="detail-row"><label>授课语言</label><span>{{ currentCourse?.teachingLanguage || '-' }}</span><label>先修课程</label><span>{{ currentCourse?.prerequisiteCourse || '-' }}</span></div>
            <div class="detail-row"><label>任务类型</label><span>{{ currentCourse?.taskType || '-' }}</span><label>是否必修</label><span>{{ requiredLabel(currentCourse?.requiredFlag) }}</span></div>
            <div class="detail-row"><label>课程等级要求</label><span>{{ currentCourse?.courseLevelRequirement || '-' }}</span><label>要求总课时</label><span>{{ currentCourse?.totalHours ?? '-' }}</span></div>
            <div class="detail-row"><label>要求周数</label><span>{{ currentCourse?.requiredWeeks ?? '-' }}</span><label>要求周课时</label><span>{{ currentCourse?.weeklyHours ?? '-' }}</span></div>
            <div class="detail-row"><label>已安排总课时</label><span>{{ currentCourse?.arrangedHours ?? '-' }}</span><label>选课上限</label><span>{{ currentCourse?.studentLimit ?? '-' }}</span></div>
            <div class="detail-row"><label>已选学生数</label><span>{{ currentCourse?.actualStudentCount ?? '-' }}</span><label>备注</label><span>{{ currentCourse?.remark || '无' }}</span></div>
          </div>
          <div class="course-detail-intro">
            <div class="course-detail-intro__label">课程简介</div>
            <div class="course-detail-intro__content">{{ currentCourse?.intro || '暂无课程简介' }}</div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="排课信息" name="schedule">
          <el-table :data="currentCourse?.scheduleDetails || []" max-height="520">
            <el-table-column label="日期" prop="date" width="120" />
            <el-table-column label="教室" prop="classroom" min-width="260" show-overflow-tooltip />
            <el-table-column label="老师" prop="teacherName" min-width="120" />
            <el-table-column label="星期几" prop="weekLabel" width="100" />
            <el-table-column label="开始时间" prop="startTime" width="110" />
            <el-table-column label="结束时间" prop="endTime" width="110" />
            <el-table-column label="学时信息" prop="hours" width="90" />
          </el-table>
          <el-empty v-if="!(currentCourse?.scheduleDetails || []).length" description="当前课程暂无排课信息" />
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { listPortalMyCourses, listPortalTermOptions } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loading = ref(false)
const courseList = ref<any[]>([])
const termOptions = ref<any[]>([])
const keyword = ref('')
const queryParams = reactive<any>({ termId: undefined })
const detailOpen = ref(false)
const detailTab = ref('basic')
const currentCourse = ref<any>(null)

const filteredCourses = computed(() => {
  const value = keyword.value.trim().toLowerCase()
  if (!value) return courseList.value
  return courseList.value.filter((item: any) =>
    String(item.courseName || '').toLowerCase().includes(value)
    || String(item.courseCode || '').toLowerCase().includes(value),
  )
})
const totalHours = computed(() => courseList.value.reduce((sum: number, item: any) => sum + Number(item.weeklyHours || 0), 0))
const subjectCount = computed(() => new Set(courseList.value.map((item: any) => item.subjectType).filter(Boolean)).size)
const currentTermLabel = computed(() => termOptions.value.find((item: any) => item.value === queryParams.termId)?.label || '全部')
const summaryTermText = computed(() => {
  const current = termOptions.value.find((item: any) => item.value === queryParams.termId)
  return current?.termName || current?.label || currentTermLabel.value
})
const totalCredits = computed(() => {
  return courseList.value.reduce((sum: number, item: any) => sum + Number(item.credits || 0), 0)
})

function requiredLabel(value?: string) {
  if (value === 'Y') return '是'
  if (value === 'N') return '否'
  return value || '-'
}

function openCourseDetail(row: any) {
  currentCourse.value = row
  detailTab.value = 'basic'
  detailOpen.value = true
}

function summarizeSchedules(items: any[]) {
  const seen = new Set<string>()
  return items.reduce((result: string[], item: any) => {
    const weeksText = item.weeksText || '-'
    const sectionText = `${item.startSection || '-'}~${item.endSection || '-'}节`
    const text = `${weeksText} ${item.weekLabel || ''} ${sectionText} ${item.classroom || ''} ${item.teacherName || ''}`.trim()
    if (!text || seen.has(text)) return result
    seen.add(text)
    result.push(text)
    return result
  }, [])
}

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
  const current = termOptions.value.find((item: any) => item.isCurrent === '1')
  if (!queryParams.termId && current) {
    queryParams.termId = current.value
  }
}

async function loadCourses() {
  const userId = userStore.user?.userId
  if (!userId) return
  loading.value = true
  try {
    const res = await listPortalMyCourses({ userId, termId: queryParams.termId })
    courseList.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadTerms()
  await loadCourses()
})
</script>

<style scoped>
.course-kpis {
  margin-top: 0;
  margin-bottom: 18px;
}

.course-list-card {
  padding-bottom: 6px;
}

.course-summary-bar {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 28px;
  margin-bottom: 12px;
  padding: 2px 0 10px;
}

.course-summary-bar__term {
  display: inline-flex;
  align-items: center;
  position: relative;
  padding-left: 10px;
  color: #172033;
  font-size: 14px;
  font-weight: 700;
  min-height: 20px;
}

.course-summary-bar__term::before {
  content: '';
  position: absolute;
  left: 0;
  top: 2px;
  width: 4px;
  height: 18px;
  border-radius: 999px;
  background: linear-gradient(180deg, #2ac3b6 0%, #179b92 100%);
}

.course-summary-bar__credit {
  display: inline-flex;
  align-items: center;
  color: #172033;
  font-size: 14px;
  font-weight: 700;
  min-height: 20px;
}

.course-filter {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.course-sheet {
  border-top: 1px solid #e7edf5;
}

.course-sheet__head,
.course-sheet__row {
  display: grid;
  grid-template-columns: 2.7fr 1.4fr 3fr 1fr 1fr;
  gap: 20px;
  align-items: start;
}

.course-sheet__head {
  padding: 14px 8px 12px;
  color: #8a94a6;
  font-size: 13px;
  font-weight: 600;
}

.course-sheet__row {
  padding: 14px 8px;
  border-top: 1px solid #edf2f8;
  color: #4d5b72;
  font-size: 14px;
  line-height: 1.8;
}

.course-sheet__cell {
  min-width: 0;
}

.course-sheet__cell--course {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.course-sheet__cell--schedule {
  color: #344256;
}

.course-sheet__cell--material {
  display: flex;
  align-items: center;
}

.course-title-link {
  padding: 0;
  border: none;
  background: transparent;
  color: #0f9f9f;
  font-size: 15px;
  font-weight: 700;
  text-align: left;
  cursor: pointer;
}

.course-title-link:hover {
  color: #0b7b7b;
}

.course-meta-line {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 14px;
  color: #667085;
  font-size: 13px;
}

.course-meta-line span,
.course-stack-line,
.schedule-line {
  display: inline-flex;
  align-items: flex-start;
  gap: 6px;
}

.course-meta-line i,
.course-stack-line i,
.schedule-line i,
.course-detail-header__meta i {
  color: #86a0bd;
  font-size: 14px;
  line-height: 1.5;
  flex: 0 0 auto;
}

.course-remark {
  color: #8893a6;
  font-size: 13px;
}

.course-stack-line {
  color: #5b677a;
  font-size: 14px;
  margin-bottom: 6px;
}

.course-stack-line:last-child {
  margin-bottom: 0;
}

.schedule-line {
  color: #3f4d63;
}

.schedule-line span {
  flex: 1;
}

.material-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 999px;
  color: #7e8a9f;
  background: #f4f7fb;
  font-size: 13px;
}

.course-count-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-top: 2px;
}

.course-count-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #5b677a;
  font-size: 14px;
  line-height: 1.4;
}

.course-count-item i {
  color: #86a0bd;
  font-size: 14px;
  flex: 0 0 auto;
}

.muted {
  color: #98a2b3;
}

.course-detail-header {
  margin-bottom: 6px;
}

.course-detail-header__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 18px;
  color: #667085;
  font-size: 13px;
}

.detail-grid {
  border: 1px solid #e5ebf3;
  border-bottom: none;
}

.detail-row {
  display: grid;
  grid-template-columns: 180px 1fr 180px 1fr;
}

.detail-row--full {
  grid-template-columns: 180px 1fr;
}

.detail-row label,
.detail-row span {
  min-height: 46px;
  display: flex;
  align-items: center;
  padding: 0 14px;
  border-bottom: 1px solid #e5ebf3;
}

.detail-row label {
  justify-content: flex-end;
  color: #9aa4b2;
  background: #fafcff;
}

.detail-row span {
  color: #374357;
}

.course-detail-intro {
  margin-top: 16px;
  padding: 16px 18px;
  border: 1px solid #e6edf5;
  border-radius: 16px;
  background: #f8fbff;
}

.course-detail-intro__label {
  color: #172033;
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 8px;
}

.course-detail-intro__content {
  color: #526076;
  line-height: 1.8;
  white-space: pre-wrap;
}

@media (max-width: 1200px) {
  .course-sheet__head,
  .course-sheet__row {
    grid-template-columns: 1.9fr 1.1fr 2fr 0.9fr 0.9fr;
    gap: 14px;
  }
}
</style>
