<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>我的课程</h3>
      <el-tag type="success">班级课程视图</el-tag>
    </div>

    <section class="portal-kpis" style="margin-top: 0; margin-bottom: 18px;">
      <el-card class="portal-card portal-stat-card"><div class="label">课程数</div><div class="value">{{ courseList.length }}</div><div class="sub">当前学期授课数量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">班级数</div><div class="value">{{ classCount }}</div><div class="sub">覆盖教学班级</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">周学时</div><div class="value">{{ totalHours }}</div><div class="sub">授课工作量参考</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">学期</div><div class="value">{{ currentTermLabel }}</div><div class="sub">支持学期切换</div></el-card>
    </section>

    <div class="portal-card">
      <div class="course-filter">
        <el-select v-model="queryParams.termId" filterable clearable placeholder="选择学期" style="width: 240px" @change="loadCourses">
          <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="keyword" clearable placeholder="搜索课程名称、班级或课程编码" style="width: 320px" />
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
        <el-table-column label="已选人数" width="100">
          <template #default="{ row }">{{ row.selectedStudentCount ?? row.actualStudentCount ?? 0 }}</template>
        </el-table-column>
        <el-table-column label="人数上限" prop="studentLimit" width="100" />
        <el-table-column label="学期" min-width="180">
          <template #default="{ row }">{{ row.termName || '-' }} {{ row.schoolYear ? `· ${row.schoolYear}` : '' }}</template>
        </el-table-column>
        <el-table-column label="课程简介" prop="intro" min-width="260" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openStudentDialog(row)">查看学生</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && !filteredCourses.length" description="当前学期暂无授课安排" />
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
const currentTermLabel = computed(() => termOptions.value.find((item: any) => item.value === queryParams.termId)?.label || '全部')
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
.course-filter {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
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
</style>
