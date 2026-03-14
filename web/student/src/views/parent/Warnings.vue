<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>孩子预警</h3>
      <el-tag type="warning">优先处理高风险与待处理预警</el-tag>
    </div>

    <el-card class="portal-card portal-soft-card">
      <div class="portal-form-row">
        <el-select v-model="queryParams.userId" filterable clearable placeholder="请选择学生" style="width: 240px">
          <el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button type="primary" @click="getList">搜索</el-button>
      </div>
    </el-card>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">预警数</div><div class="value">{{ warnings.length }}</div><div class="sub">当前学生预警数量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">高风险</div><div class="value">{{ highCount }}</div><div class="sub">建议优先联系教师</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">待处理</div><div class="value">{{ pendingCount }}</div><div class="sub">建议尽快关注</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">建议</div><div class="sub">先看高风险，再结合学习报告复盘</div></el-card>
    </section>

    <el-card class="portal-card mt20">
      <template #header><span>预警列表</span></template>
      <el-table v-loading="loading" :data="warnings">
        <el-table-column prop="warningId" label="预警ID" width="90" />
        <el-table-column prop="warningType" label="预警类型" width="120" />
        <el-table-column label="预警等级" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.warningLevel === 'HIGH' ? 'danger' : scope.row.warningLevel === 'MEDIUM' ? 'warning' : 'success'">{{ scope.row.warningLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningContent" label="预警内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="suggestion" label="干预建议" min-width="260" show-overflow-tooltip />
      </el-table>
      <el-empty v-if="!loading && warnings.length === 0" class="portal-empty" description="暂无预警提醒" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { fetchPortalUserOptions, getParentDashboard, listWarning } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loading = ref(false)
const warnings = ref<any[]>([])
const studentOptions = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 50, userId: undefined })

const highCount = computed(() => warnings.value.filter((item: any) => item.warningLevel === 'HIGH').length)
const pendingCount = computed(() => warnings.value.filter((item: any) => item.processStatus === 'PENDING').length)

async function loadOptions() {
  studentOptions.value = await fetchPortalUserOptions('student')
  const parentUserId = userStore.user?.userId
  if (parentUserId) {
    try {
      const res = await getParentDashboard({ parentUserId })
      queryParams.userId = res.data?.studentUserId || studentOptions.value[0]?.value
    } catch {
      queryParams.userId = studentOptions.value[0]?.value
    }
  } else {
    queryParams.userId = studentOptions.value[0]?.value
  }
}

async function getList() {
  if (!queryParams.userId) return
  loading.value = true
  try {
    const res = await listWarning(queryParams)
    warnings.value = res.rows || []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadOptions()
  await getList()
})
</script>
