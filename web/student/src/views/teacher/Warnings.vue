<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>学情预警</h3>
      <el-tag type="danger">教师应优先跟进高风险学生</el-tag>
    </div>

    <section class="portal-kpis" style="margin-top: 0; margin-bottom: 18px;">
      <el-card class="portal-card portal-stat-card"><div class="label">预警总数</div><div class="value">{{ warnings.length }}</div><div class="sub">当前可见预警数量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">高风险</div><div class="value">{{ highCount }}</div><div class="sub">优先安排跟进</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">中风险</div><div class="value">{{ mediumCount }}</div><div class="sub">建议补充练习</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">处理建议</div><div class="sub">资源补充 + 阶段测验 + 家校沟通</div></el-card>
    </section>

    <el-card class="portal-card">
      <template #header><span>预警列表</span></template>
      <el-table v-loading="loading" :data="warnings">
        <el-table-column prop="warningId" label="预警ID" width="90" />
        <el-table-column prop="userId" label="学生ID" width="90" />
        <el-table-column prop="warningType" label="预警类型" width="120" />
        <el-table-column label="预警等级" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.warningLevel === 'HIGH' ? 'danger' : scope.row.warningLevel === 'MEDIUM' ? 'warning' : 'success'">{{ scope.row.warningLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningContent" label="预警内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="suggestion" label="建议" min-width="260" show-overflow-tooltip />
      </el-table>
      <el-empty v-if="!loading && warnings.length === 0" class="portal-empty" description="暂无预警信息" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { listWarning } from '@/api/portal'

const loading = ref(false)
const warnings = ref<any[]>([])

const highCount = computed(() => warnings.value.filter((item: any) => item.warningLevel === 'HIGH').length)
const mediumCount = computed(() => warnings.value.filter((item: any) => item.warningLevel === 'MEDIUM').length)

async function getList() {
  loading.value = true
  try {
    const res = await listWarning({ pageNum: 1, pageSize: 50 })
    warnings.value = res.rows || []
  } finally {
    loading.value = false
  }
}

onMounted(getList)
</script>
