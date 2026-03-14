<template>
  <div class="portal-page">
    <section class="portal-hero">
      <div>
        <div class="portal-hero__badge">Teacher Overview</div>
        <div class="portal-hero__title">教学工作台</div>
        <p class="portal-hero__desc">聚合课程、班级、资源和预警信息，帮助你快速识别需要干预的学生和班级。</p>
      </div>
      <div class="portal-hero__panel">
        <div class="portal-hero__metric"><span>课程数</span><strong>{{ data.courseCount || 0 }}</strong></div>
        <div class="portal-hero__metric"><span>预警数</span><strong>{{ data.warningCount || 0 }}</strong></div>
      </div>
    </section>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">课程数</div><div class="value">{{ data.courseCount || 0 }}</div><div class="sub">当前负责课程数量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">班级数</div><div class="value">{{ data.classCount || 0 }}</div><div class="sub">覆盖班级范围</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">资源数</div><div class="value">{{ data.resourceCount || 0 }}</div><div class="sub">教学资源累计数</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">预警数</div><div class="value">{{ data.warningCount || 0 }}</div><div class="sub">需要跟进的学情风险</div></el-card>
    </section>

    <div class="portal-grid portal-grid-2 mt20">
      <el-card class="portal-card portal-soft-card">
        <template #header><span>教学建议</span></template>
        <ul class="portal-simple-list">
          <li>优先关注高风险学生与波动明显的课程。</li>
          <li>围绕预警学生推送针对性资源和阶段练习。</li>
          <li>将高频错题转化为下一轮教学讲解重点。</li>
        </ul>
      </el-card>
      <el-card class="portal-card portal-soft-card">
        <template #header><span>当前重点</span></template>
        <div class="portal-grid portal-grid-2">
          <div class="portal-surface">题库总量：{{ data.questionCount || 0 }}</div>
          <div class="portal-surface">待跟进预警：{{ warnings.length }}</div>
          <div class="portal-surface">建议优先处理：高风险学生</div>
          <div class="portal-surface">建议动作：资源补充 + 测验复查</div>
        </div>
      </el-card>
    </div>

    <el-card class="portal-card mt20">
      <template #header><span>预警概览</span></template>
      <el-table v-loading="loadingWarning" :data="warnings">
        <el-table-column prop="warningId" label="预警ID" width="90" />
        <el-table-column prop="userId" label="学生ID" width="90" />
        <el-table-column prop="warningType" label="预警类型" width="120" />
        <el-table-column label="等级" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.warningLevel === 'HIGH' ? 'danger' : scope.row.warningLevel === 'MEDIUM' ? 'warning' : 'success'">{{ scope.row.warningLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningContent" label="预警内容" min-width="240" show-overflow-tooltip />
        <el-table-column prop="suggestion" label="建议" min-width="240" show-overflow-tooltip />
      </el-table>
      <el-empty v-if="!loadingWarning && warnings.length === 0" class="portal-empty" description="暂无预警信息" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getTeacherDashboard, listWarning } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const data = ref<any>({})
const warnings = ref<any[]>([])
const loadingWarning = ref(false)

async function loadData() {
  const teacherId = userStore.user?.userId
  if (!teacherId) return
  const res = await getTeacherDashboard({ teacherId })
  data.value = res.data || {}
}

async function loadWarnings() {
  loadingWarning.value = true
  try {
    const res = await listWarning({ pageNum: 1, pageSize: 8 })
    warnings.value = res.rows || []
  } finally {
    loadingWarning.value = false
  }
}

onMounted(async () => {
  await loadData()
  await loadWarnings()
})
</script>
