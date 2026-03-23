<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>孩子课程</h3>
      <el-tag type="success">家长查看</el-tag>
    </div>

    <section class="portal-kpis" style="margin-top: 0; margin-bottom: 18px;">
      <el-card class="portal-card portal-stat-card"><div class="label">课程数</div><div class="value">{{ courseList.length }}</div><div class="sub">孩子当前学期课程</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">周学时</div><div class="value">{{ totalHours }}</div><div class="sub">按班级课程累计</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">班级数</div><div class="value">{{ classCount }}</div><div class="sub">当前班级课程归属</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">学期</div><div class="value">{{ currentTermLabel }}</div><div class="sub">支持按学期切换</div></el-card>
    </section>

    <div class="portal-card">
      <div class="course-filter">
        <el-select v-model="queryParams.termId" filterable clearable placeholder="选择学期" style="width: 240px" @change="loadCourses">
          <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="keyword" clearable placeholder="搜索课程名称或课程编码" style="width: 280px" />
      </div>

      <el-table v-loading="loading" :data="filteredCourses">
        <el-table-column label="课程信息" min-width="260">
          <template #default="{ row }">
            <div class="course-cell">
              <strong>{{ row.courseName }}</strong>
              <span>{{ row.courseCode || '未配置课程编码' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="班级" prop="className" min-width="140" />
        <el-table-column label="学科类型" prop="subjectType" width="120" />
        <el-table-column label="周学时" prop="weeklyHours" width="90" />
        <el-table-column label="人数上限" prop="studentLimit" width="100" />
        <el-table-column label="学期" min-width="180">
          <template #default="{ row }">{{ row.termName || '-' }} {{ row.schoolYear ? `· ${row.schoolYear}` : '' }}</template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && !filteredCourses.length" description="当前学期暂无课程数据" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { getParentDashboard, listPortalParentCourses, listPortalTermOptions } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loading = ref(false)
const courseList = ref<any[]>([])
const termOptions = ref<any[]>([])
const keyword = ref('')
const queryParams = reactive<any>({ termId: undefined, studentUserId: undefined })

const filteredCourses = computed(() => {
  const value = keyword.value.trim().toLowerCase()
  if (!value) return courseList.value
  return courseList.value.filter((item: any) =>
    String(item.courseName || '').toLowerCase().includes(value)
    || String(item.courseCode || '').toLowerCase().includes(value),
  )
})
const totalHours = computed(() => courseList.value.reduce((sum: number, item: any) => sum + Number(item.weeklyHours || 0), 0))
const classCount = computed(() => new Set(courseList.value.map((item: any) => item.classId).filter(Boolean)).size)
const currentTermLabel = computed(() => termOptions.value.find((item: any) => item.value === queryParams.termId)?.label || '全部')

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
  const current = termOptions.value.find((item: any) => item.isCurrent === '1')
  if (!queryParams.termId && current) queryParams.termId = current.value
}

async function loadStudent() {
  const parentUserId = userStore.user?.userId
  if (!parentUserId) return
  const res = await getParentDashboard({ parentUserId })
  queryParams.studentUserId = res.data?.studentUserId
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
  await loadStudent()
  await loadCourses()
})
</script>

<style scoped>
.course-filter { display:flex; gap:12px; align-items:center; margin-bottom:16px; flex-wrap:wrap }
.course-cell { display:flex; flex-direction:column; gap:6px }
.course-cell strong { color:#172033; font-size:14px }
.course-cell span { color:#667085; font-size:12px }
</style>
