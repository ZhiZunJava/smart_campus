<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>学习报告</h3>
      <el-tag type="primary">结合学习行为与画像自动生成</el-tag>
    </div>

    <div class="portal-grid portal-grid-2">
      <el-card class="portal-card">
        <template #header><span>报告列表</span></template>
        <el-table v-loading="loading" :data="reports">
          <el-table-column prop="reportId" label="报告ID" width="90" />
          <el-table-column label="报告类型" width="140">
            <template #default="scope">
              <el-tag>{{ reportTypeLabel(scope.row.reportType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="generateTime" label="生成时间" min-width="180" />
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button link type="primary" @click="selectReport(scope.row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loading && reports.length === 0" class="portal-empty" description="暂无学习报告" />
      </el-card>

      <el-card class="portal-card portal-soft-card">
        <template #header><span>报告详情</span></template>
        <template v-if="detail.reportId">
          <div class="portal-surface">{{ reportTypeLabel(detail.reportType) }}</div>
          <div class="portal-surface mt16">生成时间：{{ detail.generateTime }}</div>
          <div class="portal-surface mt16">{{ detail.reportContent }}</div>
          <div class="mt16">
            <div class="portal-section-title"><h3>结构化数据</h3></div>
            <pre class="portal-json">{{ prettyJson }}</pre>
          </div>
        </template>
        <el-empty v-else class="portal-empty" description="请选择一份报告查看详情" />
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getParentDashboard, listReport } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loading = ref(false)
const reports = ref<any[]>([])
const detail = ref<any>({})

function reportTypeLabel(type: string) {
  return ({ weekly: '周报', monthly: '月报', diagnosis: '诊断报告', exam_after_submit: '考试后报告', exam: '考前报告' } as any)[type] || type || '-'
}

const prettyJson = computed(() => {
  if (!detail.value?.reportJson) return '暂无结构化数据'
  try {
    return JSON.stringify(JSON.parse(detail.value.reportJson), null, 2)
  } catch {
    return detail.value.reportJson
  }
})

async function getList() {
  const parentUserId = userStore.user?.userId
  if (!parentUserId) return
  loading.value = true
  try {
    const dash = await getParentDashboard({ parentUserId })
    const studentUserId = dash.data?.studentUserId
    const res = await listReport({ pageNum: 1, pageSize: 50, userId: studentUserId })
    reports.value = res.rows || []
    if (reports.value.length) {
      detail.value = reports.value[0]
    }
  } finally {
    loading.value = false
  }
}

function selectReport(row: any) {
  detail.value = row
}

onMounted(getList)
</script>
