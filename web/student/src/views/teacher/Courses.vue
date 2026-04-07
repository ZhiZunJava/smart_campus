<template>
  <div class="portal-page teacher-courses-page">
    <section class="course-overview">
      <el-card class="portal-card teacher-courses__hero">
        <div class="teacher-courses__hero-copy">
          <span class="teacher-courses__eyebrow">授课总览</span>
          <h4>{{ currentTermLabel }}</h4>
          <p>把当前学期授课课程、覆盖班级和学生规模集中展示，方便教师先总览再进入具体教学班。</p>
          <div class="teacher-courses__hero-tags">
            <span class="teacher-courses__tag">课程 {{ courseList.length }}</span>
            <span class="teacher-courses__tag">班级 {{ classCount }}</span>
            <span class="teacher-courses__tag">周学时 {{ totalHours }}</span>
          </div>
        </div>
        <div class="teacher-courses__hero-stats">
          <div class="teacher-courses__hero-stat">
            <span>授课课程</span>
            <strong>{{ courseList.length }}</strong>
          </div>
          <div class="teacher-courses__hero-stat">
            <span>覆盖班级</span>
            <strong>{{ classCount }}</strong>
          </div>
        </div>
      </el-card>
    </section>

    <div class="portal-card teacher-courses__toolbar">
      <div class="teacher-courses__toolbar-left">
        <div class="teacher-courses__summary-badge">
          <i class="ri-calendar-2-line"></i> {{ currentTermLabel }}
        </div>
      </div>
      <div class="teacher-courses__filters">
        <el-select v-model="queryParams.termId" filterable clearable placeholder="切换学期" class="teacher-courses__select" @change="loadCourses">
          <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="keyword" clearable placeholder="搜索课程名称、班级或课程编码..." class="teacher-courses__input">
          <template #prefix><i class="ri-search-line"></i></template>
        </el-input>
      </div>
    </div>

    <div class="portal-card teacher-courses__list-card">
      <el-skeleton :loading="loading" animated>
        <template #template>
          <div class="teacher-courses__grid">
            <div v-for="index in 4" :key="`teacher-skeleton-${index}`" class="teacher-course-card teacher-course-card--skeleton">
              <el-skeleton-item variant="text" class="teacher-course-card__skeleton-line teacher-course-card__skeleton-line--short" />
              <el-skeleton-item variant="text" class="teacher-course-card__skeleton-line teacher-course-card__skeleton-line--title" />
              <el-skeleton-item variant="text" class="teacher-course-card__skeleton-line" />
              <el-skeleton-item variant="rect" class="teacher-course-card__skeleton-block" />
            </div>
          </div>
        </template>
        <template #default>
          <div class="teacher-courses__grid" v-if="filteredCourses.length">
            <div v-for="row in filteredCourses" :key="`teacher-course-${row.id || row.courseId}`" class="teacher-course-card">
              <div class="teacher-course-card__header">
                <div class="teacher-course-card__type">{{ row.subjectType || row.courseCategory || '通用课程' }}</div>
                <div class="teacher-course-card__count">{{ row.selectedStudentCount ?? row.actualStudentCount ?? 0 }} / {{ row.studentLimit ?? '-' }} 人</div>
              </div>
              <div class="teacher-course-card__body">
                <h4>{{ row.courseName || '-' }}</h4>
                <div class="teacher-course-card__code">{{ row.courseCode || '-' }}<span v-if="row.className"> · {{ row.className }}</span></div>
                <div class="teacher-course-card__meta">
                  <span><i class="ri-time-line"></i> 周学时 {{ row.weeklyHours ?? '-' }}</span>
                  <span><i class="ri-calendar-schedule-line"></i> {{ row.termName || '-' }}</span>
                  <span><i class="ri-group-line"></i> 上限 {{ row.studentLimit ?? '-' }}</span>
                </div>
                <p class="teacher-course-card__intro">{{ row.intro || '暂无课程简介，可点击查看学生了解教学班情况。' }}</p>
              </div>
              <div class="teacher-course-card__footer">
                <div class="teacher-course-card__progress">
                  <div class="teacher-course-card__progress-track">
                    <div
                      class="teacher-course-card__progress-bar"
                      :style="{ width: `${Math.min(((row.selectedStudentCount ?? row.actualStudentCount ?? 0) / Math.max(Number(row.studentLimit || 1), 1)) * 100, 100)}%` }"
                    ></div>
                  </div>
                  <span>选课进度</span>
                </div>
                <el-button type="primary" plain @click="openStudentDialog(row)">查看学生</el-button>
              </div>
            </div>
          </div>
          <el-empty v-else description="当前学期暂无授课安排" />
        </template>
      </el-skeleton>
    </div>

    <el-dialog v-model="studentDialogOpen" :title="studentDialogTitle" width="900px" append-to-body>
      <div class="teacher-course-students">
        <div class="teacher-course-students__summary">
          <span>已选人数 {{ studentDialogSummary.selectedStudentCount || 0 }}</span>
          <span>人数上限 {{ studentDialogSummary.studentLimit || '-' }}</span>
          <span>{{ studentDialogSummary.termName || '-' }}</span>
        </div>
        <el-table v-loading="studentLoading" :data="studentList" max-height="480">
          <el-table-column label="学生" min-width="220">
            <template #default="{ row }">
              <div class="course-cell">
                <strong>{{ row.studentName || '-' }}</strong>
                <span>{{ row.studentNo ? `学号：${row.studentNo}` : `用户ID：${row.studentUserId}` }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="班级" prop="className" min-width="180" />
          <el-table-column label="加入时间" prop="joinTime" min-width="180" />
          <el-table-column label="备注" prop="remark" min-width="220" show-overflow-tooltip />
        </el-table>
        <el-empty v-if="!studentLoading && !studentList.length" description="当前教学班暂无选课学生" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { getPortalTeacherCourseStudents, listPortalTeacherCourses, listPortalTermOptions } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loading = ref(false)
const courseList = ref<any[]>([])
const termOptions = ref<any[]>([])
const keyword = ref('')
const queryParams = reactive<any>({ termId: undefined })
const studentDialogOpen = ref(false)
const studentLoading = ref(false)
const studentDialogSummary = ref<any>({})
const studentList = ref<any[]>([])

const filteredCourses = computed(() => {
  const value = keyword.value.trim().toLowerCase()
  if (!value) return courseList.value
  return courseList.value.filter((item: any) =>
    String(item.courseName || '').toLowerCase().includes(value)
    || String(item.courseCode || '').toLowerCase().includes(value)
    || String(item.className || '').toLowerCase().includes(value),
  )
})
const classCount = computed(() => new Set(courseList.value.map((item: any) => item.classId).filter(Boolean)).size)
const totalHours = computed(() => courseList.value.reduce((sum: number, item: any) => sum + Number(item.weeklyHours || 0), 0))
const currentTermLabel = computed(() => termOptions.value.find((item: any) => item.value === queryParams.termId)?.label || '当前学期')
const studentDialogTitle = computed(() => {
  return `${studentDialogSummary.value.courseName || '教学班'}${studentDialogSummary.value.className ? ` / ${studentDialogSummary.value.className}` : ''}`
})

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
  const current = termOptions.value.find((item: any) => item.isCurrent === '1')
  if (!queryParams.termId && current) queryParams.termId = current.value
}

async function loadCourses() {
  const teacherId = userStore.user?.userId
  if (!teacherId) return
  loading.value = true
  try {
    const res = await listPortalTeacherCourses({ teacherId, termId: queryParams.termId })
    courseList.value = res.data || []
  } finally {
    loading.value = false
  }
}

async function openStudentDialog(row: any) {
  const teacherId = userStore.user?.userId
  if (!teacherId || !row?.id) return
  studentDialogOpen.value = true
  studentLoading.value = true
  try {
    const res = await getPortalTeacherCourseStudents({ teacherId, classCourseId: row.id, termId: queryParams.termId })
    studentDialogSummary.value = res.data?.summary || {}
    studentList.value = res.data?.items || []
  } finally {
    studentLoading.value = false
  }
}

onMounted(async () => {
  await loadTerms()
  await loadCourses()
})
</script>

<style scoped>
.teacher-courses-page {
  padding: 24px 32px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.teacher-courses__hero {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(260px, 0.65fr);
  gap: 18px;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(24, 148, 106, 0.12) 0%, rgba(24, 148, 106, 0) 34%),
    linear-gradient(135deg, #ffffff 0%, #f2fbf8 100%);
}

.teacher-courses__eyebrow {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(24, 148, 106, 0.12);
  color: #12795a;
  font-size: 12px;
  font-weight: 700;
}

.teacher-courses__hero-copy h4 {
  margin: 12px 0 0;
  font-size: 28px;
  font-weight: 800;
  color: var(--portal-text);
}

.teacher-courses__hero-copy p {
  margin: 10px 0 0;
  color: var(--portal-text-secondary);
  font-size: 14px;
  line-height: 1.9;
}

.teacher-courses__hero-tags {
  margin-top: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.teacher-courses__tag {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(18, 121, 90, 0.14);
  color: #12795a;
  font-size: 13px;
  font-weight: 700;
}

.teacher-courses__hero-stats {
  display: grid;
  gap: 12px;
}

.teacher-courses__hero-stat {
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.84);
  border: 1px solid var(--portal-border);
}

.teacher-courses__hero-stat span {
  color: var(--portal-text-secondary);
  font-size: 13px;
}

.teacher-courses__hero-stat strong {
  display: block;
  margin-top: 8px;
  font-size: 28px;
  color: #12795a;
}

.teacher-courses__toolbar,
.teacher-courses__list-card {
  padding: 18px;
}

.teacher-courses__toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.teacher-courses__summary-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 999px;
  background: #f8fafc;
  color: #475569;
  font-size: 13px;
  font-weight: 700;
}

.teacher-courses__filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.teacher-courses__select {
  width: 220px;
}

.teacher-courses__input {
  width: 320px;
}

.teacher-courses__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.teacher-course-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  border: 1px solid var(--portal-border);
  border-radius: 16px;
  background: linear-gradient(180deg, #ffffff, #f7fcfa);
}

.teacher-course-card--skeleton {
  min-height: 220px;
}

.teacher-course-card__skeleton-line {
  height: 12px;
  border-radius: 999px;
}

.teacher-course-card__skeleton-line--short {
  width: 34%;
}

.teacher-course-card__skeleton-line--title {
  width: 72%;
  margin-top: 10px;
}

.teacher-course-card__skeleton-block {
  width: 100%;
  height: 96px;
  border-radius: 12px;
  margin-top: auto;
}

.teacher-course-card__header,
.teacher-course-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.teacher-course-card__type {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(18, 121, 90, 0.12);
  color: #12795a;
  font-size: 12px;
  font-weight: 700;
}

.teacher-course-card__count {
  color: #667085;
  font-size: 12px;
  font-weight: 600;
}

.teacher-course-card__body h4 {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  color: var(--portal-text);
}

.teacher-course-card__code {
  margin-top: 6px;
  color: var(--portal-text-secondary);
  font-size: 13px;
}

.teacher-course-card__meta {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: #667085;
  font-size: 12px;
}

.teacher-course-card__intro {
  margin: 10px 0 0;
  color: var(--portal-text-secondary);
  font-size: 13px;
  line-height: 1.8;
}

.teacher-course-card__progress {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 140px;
}

.teacher-course-card__progress-track {
  height: 4px;
  border-radius: 999px;
  background: #dcfce7;
  overflow: hidden;
}

.teacher-course-card__progress-bar {
  height: 100%;
  border-radius: 999px;
  background: #16a34a;
}

.teacher-course-card__progress span {
  color: #64748b;
  font-size: 12px;
}

.course-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.course-cell strong {
  color: #172033;
  font-size: 14px;
}

.course-cell span {
  color: #667085;
  font-size: 12px;
}

.teacher-course-students {
  display: grid;
  gap: 16px;
}

.teacher-course-students__summary {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  padding: 12px 14px;
  border-radius: 12px;
  background: #f6fbff;
  border: 1px solid #dbe8f8;
  color: #526076;
  font-size: 12px;
}

@media (max-width: 960px) {
  .teacher-courses__hero {
    grid-template-columns: 1fr;
  }

  .teacher-courses__grid {
    grid-template-columns: 1fr;
  }

  .teacher-courses-page {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .teacher-courses-page {
    padding: 16px;
    gap: 14px;
  }

  .teacher-courses__hero {
    padding: 18px;
  }

  .teacher-courses__hero-copy h4 {
    font-size: 22px;
  }

  .teacher-courses__hero-stat strong {
    font-size: 22px;
  }

  .teacher-courses__toolbar,
  .teacher-courses__list-card {
    padding: 14px;
  }

  .teacher-courses__select,
  .teacher-courses__input {
    width: 100%;
  }

  .teacher-course-card__body h4 {
    font-size: 16px;
  }
}

@media (max-width: 640px) {
  .teacher-courses-page {
    padding: 12px;
    gap: 12px;
  }

  .teacher-courses__hero {
    padding: 14px;
  }

  .teacher-courses__hero-copy h4 {
    font-size: 18px;
  }

  .teacher-courses__hero-copy p {
    font-size: 13px;
  }

  .teacher-courses__hero-stat {
    padding: 12px 14px;
  }

  .teacher-courses__hero-stat strong {
    font-size: 18px;
  }

  .teacher-course-card {
    padding: 12px;
  }

  .teacher-course-card__footer {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
}
</style>
