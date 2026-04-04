<template>
  <div class="portal-page parent-courses-page">
    <section class="course-overview">
      <el-card class="portal-card parent-courses__hero">
        <div class="parent-courses__hero-copy">
          <span class="parent-courses__eyebrow">孩子课程总览</span>
          <h4>{{ currentTermLabel }}</h4>
          <p>把孩子当前学期的课程、班级归属和周学时集中展示，方便先总览再进入课表查看细节。</p>
          <div class="parent-courses__hero-tags">
            <span class="parent-courses__tag">{{ currentChild?.studentName || '未选择孩子' }}</span>
            <span class="parent-courses__tag">课程 {{ courseList.length }}</span>
            <span class="parent-courses__tag">周学时 {{ totalHours }}</span>
          </div>
        </div>
        <div class="parent-courses__hero-stats">
          <div class="parent-courses__hero-stat">
            <span>排课课程</span>
            <strong>{{ scheduledCourseCount }}</strong>
          </div>
          <div class="parent-courses__hero-stat">
            <span>覆盖班级</span>
            <strong>{{ classCount }}</strong>
          </div>
        </div>
      </el-card>

      <div class="course-overview__metrics">
        <div class="course-metric-card course-metric-card--courses">
          <div class="course-metric-card__body">
            <div class="course-metric-card__icon"><i class="ri-book-open-line"></i></div>
            <div class="course-metric-card__content">
              <div class="course-metric-card__label">课程数</div>
              <div class="course-metric-card__value">{{ courseList.length }}</div>
              <div class="course-metric-card__sub">当前孩子的学期课程</div>
            </div>
          </div>
        </div>
        <div class="course-metric-card course-metric-card--hours">
          <div class="course-metric-card__body">
            <div class="course-metric-card__icon"><i class="ri-time-line"></i></div>
            <div class="course-metric-card__content">
              <div class="course-metric-card__label">总周学时</div>
              <div class="course-metric-card__value">{{ totalHours }}</div>
              <div class="course-metric-card__sub">按当前课表累计</div>
            </div>
          </div>
        </div>
        <div class="course-metric-card course-metric-card--classes">
          <div class="course-metric-card__body">
            <div class="course-metric-card__icon"><i class="ri-group-line"></i></div>
            <div class="course-metric-card__content">
              <div class="course-metric-card__label">班级数</div>
              <div class="course-metric-card__value">{{ classCount }}</div>
              <div class="course-metric-card__sub">涉及的教学班范围</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <div class="portal-card parent-courses__toolbar">
      <div class="parent-courses__toolbar-left">
        <div class="course-summary-badge">
          <i class="ri-calendar-2-line"></i> {{ currentTermLabel }}
        </div>
        <div class="course-summary-badge course-summary-badge--highlight">
          <i class="ri-user-smile-line"></i> {{ currentChild?.relationType || '家庭关系未标注' }}
        </div>
      </div>
      <div class="parent-courses__filters">
        <el-select v-model="queryParams.studentUserId" filterable placeholder="选择孩子" class="filter-select" @change="loadCourses">
          <el-option v-for="item in childOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="queryParams.termId" filterable clearable placeholder="选择学期" class="filter-select" @change="loadCourses">
          <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="keyword" clearable placeholder="搜索课程名称或课程编码..." class="filter-input">
          <template #prefix><i class="ri-search-line"></i></template>
        </el-input>
      </div>
    </div>

    <div class="portal-card parent-courses__list-card">
      <el-skeleton :loading="loading" animated>
        <template #template>
          <div class="parent-courses__grid">
            <div v-for="index in 4" :key="`skeleton-${index}`" class="parent-course-card parent-course-card--skeleton">
              <el-skeleton-item variant="text" class="parent-course-card__skeleton-line parent-course-card__skeleton-line--short" />
              <el-skeleton-item variant="text" class="parent-course-card__skeleton-line parent-course-card__skeleton-line--title" />
              <el-skeleton-item variant="text" class="parent-course-card__skeleton-line" />
              <el-skeleton-item variant="rect" class="parent-course-card__skeleton-block" />
            </div>
          </div>
        </template>
        <template #default>
          <div class="parent-courses__grid" v-if="filteredCourses.length">
            <article v-for="row in filteredCourses" :key="`parent-course-${row.id || row.courseId}`" class="parent-course-card">
              <div class="parent-course-card__header">
                <div class="parent-course-card__type">{{ row.subjectType || row.courseCategory || '通用课程' }}</div>
                <div class="parent-course-card__credits">周学时 {{ row.weeklyHours ?? row.totalHours ?? '-' }}</div>
              </div>
              <div class="parent-course-card__body">
                <h4>{{ row.courseName || '-' }}</h4>
                <div class="parent-course-card__code">
                  <i class="ri-barcode-box-line"></i>
                  {{ row.courseCode || '未配置课程编码' }}
                  <span v-if="row.className" class="parent-course-card__divider">|</span>
                  {{ row.className || '' }}
                </div>
                <div class="parent-course-card__meta">
                  <span><i class="ri-user-star-line"></i> {{ row.teacherName || '待分配教师' }}</span>
                  <span><i class="ri-group-line"></i> {{ row.className || '未配置班级' }}</span>
                  <span><i class="ri-calendar-schedule-line"></i> {{ row.termName || currentTermLabel }}</span>
                </div>
                <div class="parent-course-card__schedule">
                  <template v-if="summarizeSchedules(row.scheduleDetails).length">
                    <div
                      v-for="(item, index) in summarizeSchedules(row.scheduleDetails).slice(0, 2)"
                      :key="`schedule-${row.id || row.courseId}-${index}`"
                      class="parent-course-card__schedule-item"
                    >
                      <i class="ri-map-pin-time-line"></i>
                      <span>{{ item }}</span>
                    </div>
                    <div v-if="summarizeSchedules(row.scheduleDetails).length > 2" class="parent-course-card__schedule-more">
                      另有 {{ summarizeSchedules(row.scheduleDetails).length - 2 }} 条排课安排
                    </div>
                  </template>
                  <div v-else class="parent-course-card__schedule-empty">
                    <i class="ri-calendar-close-line"></i> 暂无排课安排
                  </div>
                </div>
              </div>
              <div class="parent-course-card__footer">
                <div class="parent-course-card__limit">
                  <i class="ri-group-line"></i>
                  <span>人数上限 {{ row.studentLimit ?? '-' }}</span>
                </div>
                <el-button type="primary" plain @click="goSchedule">查看课表</el-button>
              </div>
            </article>
          </div>
          <el-empty v-else description="当前学期暂无课程数据" class="course-empty" />
        </template>
      </el-skeleton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listPortalParentChildren, listPortalParentCourses, listPortalTermOptions } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const userStore = usePortalUserStore()
const loading = ref(false)
const courseList = ref<any[]>([])
const termOptions = ref<any[]>([])
const childOptions = ref<any[]>([])
const keyword = ref('')
const queryParams = reactive<any>({ termId: undefined, studentUserId: undefined })

const filteredCourses = computed(() => {
  const value = keyword.value.trim().toLowerCase()
  if (!value) return courseList.value
  return courseList.value.filter((item: any) =>
    String(item.courseName || '').toLowerCase().includes(value)
    || String(item.courseCode || '').toLowerCase().includes(value)
    || String(item.className || '').toLowerCase().includes(value),
  )
})
const totalHours = computed(() => courseList.value.reduce((sum: number, item: any) => sum + Number(item.weeklyHours || 0), 0))
const classCount = computed(() => new Set(courseList.value.map((item: any) => item.classId || item.className).filter(Boolean)).size)
const scheduledCourseCount = computed(() => courseList.value.filter((item: any) => (item.scheduleDetails || []).length).length)
const currentTermLabel = computed(() => termOptions.value.find((item: any) => item.value === queryParams.termId)?.label || '当前学期')
const currentChild = computed(() => childOptions.value.find((item: any) => item.value === queryParams.studentUserId) || null)

function summarizeSchedules(list: any[] = []) {
  return list
    .map((item: any) => {
      const weekLabel = item.weekLabel || item.weekDayLabel || ''
      const timeText = item.startTime && item.endTime ? `${item.startTime}-${item.endTime}` : ''
      const location = [item.campusName, item.buildingName, item.classroomName || item.classroom].filter(Boolean).join(' / ')
      return [weekLabel, timeText, location].filter(Boolean).join(' · ')
    })
    .filter(Boolean)
}

function goSchedule() {
  router.push('/parent/schedule')
}

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
  const current = termOptions.value.find((item: any) => item.isCurrent === '1')
  if (!queryParams.termId && current) queryParams.termId = current.value
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

async function loadCourses() {
  const parentUserId = userStore.user?.userId
  if (!parentUserId) return
  loading.value = true
  try {
    const res = await listPortalParentCourses({ parentUserId, studentUserId: queryParams.studentUserId, termId: queryParams.termId })
    courseList.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadTerms()
  await loadChildren()
  await loadCourses()
})
</script>

<style scoped>
.parent-courses-page {
  padding: 24px 32px;
  gap: 24px;
  background: transparent;
}

.course-overview {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(280px, 1fr);
  gap: 16px;
}

.parent-courses__hero {
  padding: 0;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid rgba(194, 65, 45, 0.12);
  box-shadow: none;
}

.parent-courses__hero :deep(.el-card__body) {
  padding: 24px;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.parent-courses__eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(194, 65, 45, 0.12);
  color: #c2412d;
  font-size: 12px;
  font-weight: 700;
  margin-bottom: 16px;
}

.parent-courses__hero-copy h4 {
  margin: 0 0 8px;
  color: #0f172a;
  font-size: 24px;
  line-height: 1.3;
  font-weight: 700;
}

.parent-courses__hero-copy p {
  margin: 0 0 16px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.6;
}

.parent-courses__hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.parent-courses__tag {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 12px;
  border-radius: 8px;
  background: #ffffff;
  border: 1px solid rgba(194, 65, 45, 0.16);
  color: #7c2d12;
  font-size: 12px;
  font-weight: 500;
}

.parent-courses__hero-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px dashed #e2e8f0;
}

.parent-courses__hero-stat span {
  color: #64748b;
  font-size: 12px;
  font-weight: 500;
}

.parent-courses__hero-stat strong {
  display: block;
  margin-top: 4px;
  color: #7c2d12;
  font-size: 24px;
  line-height: 1;
  font-weight: 700;
}

.course-overview__metrics {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.course-metric-card {
  padding: 0;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: none;
  background: #ffffff;
  flex: 1;
}

.course-metric-card__body {
  display: flex;
  align-items: center;
  padding: 16px 20px;
}

.course-metric-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 16px;
  flex-shrink: 0;
}

.course-metric-card__label {
  color: #64748b;
  font-size: 12px;
}

.course-metric-card__value {
  color: #0f172a;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.1;
}

.course-metric-card__sub {
  color: #94a3b8;
  font-size: 12px;
  margin-top: 4px;
}

.course-metric-card--courses .course-metric-card__icon {
  background: rgba(194, 65, 45, 0.12);
  color: #c2412d;
}

.course-metric-card--hours .course-metric-card__icon {
  background: #ecfdf5;
  color: #10b981;
}

.course-metric-card--classes .course-metric-card__icon {
  background: #eff6ff;
  color: #2563eb;
}

.parent-courses__toolbar,
.parent-courses__list-card {
  padding: 18px 20px;
}

.parent-courses__toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.parent-courses__toolbar-left,
.parent-courses__filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.course-summary-badge {
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

.course-summary-badge--highlight {
  background: rgba(194, 65, 45, 0.08);
  color: #9a3412;
}

.filter-select {
  width: 220px;
}

.filter-input {
  width: 320px;
}

.parent-courses__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.parent-course-card {
  display: flex;
  flex-direction: column;
  border-radius: 18px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  background: linear-gradient(180deg, #ffffff, #fff7f4);
  overflow: hidden;
  min-height: 280px;
}

.parent-course-card--skeleton {
  padding: 18px;
  min-height: 220px;
}

.parent-course-card__skeleton-line {
  height: 12px;
  border-radius: 999px;
  margin-bottom: 12px;
}

.parent-course-card__skeleton-line--short {
  width: 34%;
}

.parent-course-card__skeleton-line--title {
  width: 72%;
}

.parent-course-card__skeleton-block {
  width: 100%;
  height: 100px;
  border-radius: 12px;
}

.parent-course-card__header,
.parent-course-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
}

.parent-course-card__header {
  padding-bottom: 0;
}

.parent-course-card__body {
  padding: 14px 20px 18px;
  flex: 1;
}

.parent-course-card__type {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(194, 65, 45, 0.12);
  color: #c2412d;
  font-size: 12px;
  font-weight: 700;
}

.parent-course-card__credits {
  color: #7c2d12;
  font-size: 12px;
  font-weight: 700;
}

.parent-course-card__body h4 {
  margin: 0;
  font-size: 20px;
  color: #0f172a;
  line-height: 1.35;
}

.parent-course-card__code {
  margin-top: 8px;
  color: #64748b;
  font-size: 13px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
}

.parent-course-card__divider {
  color: #cbd5e1;
}

.parent-course-card__meta {
  margin-top: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.parent-course-card__meta span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 7px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(226, 232, 240, 0.9);
  color: #475569;
  font-size: 12px;
}

.parent-course-card__schedule {
  margin-top: 16px;
  display: grid;
  gap: 8px;
}

.parent-course-card__schedule-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 12.5px;
  color: #334155;
  line-height: 1.6;
}

.parent-course-card__schedule-item i {
  color: #c2412d;
  margin-top: 2px;
}

.parent-course-card__schedule-more {
  padding-left: 20px;
  color: #64748b;
  font-size: 12px;
}

.parent-course-card__schedule-empty {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #94a3b8;
  font-size: 13px;
}

.parent-course-card__footer {
  border-top: 1px dashed #e2e8f0;
  background: rgba(255, 255, 255, 0.72);
}

.parent-course-card__limit {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 12.5px;
}

.course-empty {
  padding: 60px 0;
}

@media (max-width: 1100px) {
  .course-overview {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .parent-courses-page {
    padding: 20px;
  }

  .parent-courses__grid {
    grid-template-columns: 1fr;
  }

  .filter-select,
  .filter-input {
    width: 100%;
  }
}
</style>
