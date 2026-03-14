<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>我的课程</h3>
      <el-tag type="primary">查看课程分布与课程详情</el-tag>
    </div>

    <section class="portal-kpis" style="margin-top: 0; margin-bottom: 18px;">
      <el-card class="portal-card portal-stat-card"><div class="label">课程数</div><div class="value">{{ courses.length }}</div><div class="sub">当前负责课程数量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">已选课程</div><div class="value">{{ detail.courseId ? 1 : 0 }}</div><div class="sub">右侧查看详情</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">学科类型</div><div class="value">{{ subjectCount }}</div><div class="sub">覆盖学科类型数量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">教学建议</div><div class="sub">建议按课程建设资源与测评闭环</div></el-card>
    </section>

    <div class="portal-grid portal-grid-2">
      <el-card class="portal-card">
        <template #header><span>课程列表</span></template>
        <el-table v-loading="loading" :data="courses" @row-click="selectCourse">
          <el-table-column prop="courseId" label="课程ID" width="90" />
          <el-table-column prop="courseName" label="课程名称" min-width="180" />
          <el-table-column prop="courseCode" label="课程编码" width="120" />
          <el-table-column prop="subjectType" label="学科类型" width="120" />
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button link type="primary" @click.stop="selectCourse(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loading && courses.length === 0" description="暂无课程数据" />
      </el-card>

      <el-card class="portal-card portal-soft-card">
        <template #header><span>课程详情</span></template>
        <template v-if="detail.courseId">
          <div class="portal-surface">课程名称：{{ detail.courseName }}</div>
          <div class="portal-grid portal-grid-2 mt16">
            <div class="portal-surface">课程编码：{{ detail.courseCode || '-' }}</div>
            <div class="portal-surface">学科类型：{{ detail.subjectType || '-' }}</div>
          </div>
          <div class="portal-surface mt16">课程简介：{{ detail.intro || '-' }}</div>
          <div class="mt16">
            <div class="portal-section-title"><h3>建议动作</h3></div>
            <ul class="portal-simple-list">
              <li>为该课程补充高质量资源，提升资源覆盖率。</li>
              <li>定期检查课程相关预警学生情况，及时跟进。</li>
              <li>围绕课程知识点组织阶段性测评和复盘。</li>
            </ul>
          </div>
        </template>
        <el-empty v-else class="portal-empty" description="请选择一门课程查看详情" />
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { listCourse } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loading = ref(false)
const courses = ref<any[]>([])
const detail = ref<any>({})

const subjectCount = computed(() => {
  const values = new Set(courses.value.map((item: any) => item.subjectType).filter(Boolean))
  return values.size
})

async function getList() {
  const teacherId = userStore.user?.userId
  if (!teacherId) return
  loading.value = true
  try {
    const res = await listCourse({ pageNum: 1, pageSize: 50, teacherId })
    courses.value = res.rows || []
    if (!detail.value.courseId && courses.value.length) {
      detail.value = courses.value[0]
    }
  } finally {
    loading.value = false
  }
}

function selectCourse(row: any) {
  detail.value = row
}

onMounted(getList)
</script>
