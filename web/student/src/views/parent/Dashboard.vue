<template>
  <div class="portal-page">
    <section class="portal-hero">
      <div>
        <div class="portal-hero__badge">Parent Overview</div>
        <div class="portal-hero__title">孩子学习概览</div>
        <p class="portal-hero__desc">你可以在这里快速看到孩子的学习活跃度、考试情况、预警提醒和阶段建议，便于及时陪伴与沟通。</p>
      </div>
      <div class="portal-hero__panel">
        <div class="portal-hero__metric"><span>能力等级</span><strong>{{ data.profile?.abilityLevel || '--' }}</strong></div>
        <div class="portal-hero__metric"><span>风险分</span><strong>{{ data.profile?.riskScore || 0 }}</strong></div>
      </div>
    </section>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">学习行为</div><div class="value">{{ data.studyRecordCount || 0 }}</div><div class="sub">近期学习行为采集</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">考试记录</div><div class="value">{{ data.examRecordCount || 0 }}</div><div class="sub">近期考试与练习情况</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">预警数量</div><div class="value">{{ data.warningCount || 0 }}</div><div class="sub">需要重点关注的提醒</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">掌握度</div><div class="value">{{ data.profile?.masteryScore || 0 }}</div><div class="sub">当前学习掌握情况</div></el-card>
    </section>

    <div class="portal-grid portal-grid-2 mt20">
      <el-card class="portal-card portal-soft-card">
        <template #header><span>家长建议</span></template>
        <ul class="portal-simple-list">
          <li>优先关注高风险预警与近期考试波动。</li>
          <li>鼓励孩子先完成高优先级推荐资源，再做错题复盘。</li>
          <li>结合学习报告与教师建议，形成固定的学习节奏。</li>
        </ul>
      </el-card>
      <el-card class="portal-card portal-soft-card">
        <template #header><span>画像摘要</span></template>
        <div v-if="data.profile" class="portal-grid portal-grid-2">
          <div class="portal-surface">活跃度：{{ data.profile.activeScore }}</div>
          <div class="portal-surface">掌握度：{{ data.profile.masteryScore }}</div>
          <div class="portal-surface">能力等级：{{ data.profile.abilityLevel }}</div>
          <div class="portal-surface">风险分：{{ data.profile.riskScore }}</div>
        </div>
        <el-empty v-else description="暂无画像数据" />
      </el-card>
    </div>

    <el-card class="portal-card mt20">
      <template #header><span>近期预警</span></template>
      <el-table :data="data.warnings || []">
        <el-table-column prop="warningType" label="预警类型" width="120" />
        <el-table-column label="等级" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.warningLevel === 'HIGH' ? 'danger' : scope.row.warningLevel === 'MEDIUM' ? 'warning' : 'success'">{{ scope.row.warningLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningContent" label="预警内容" min-width="240" show-overflow-tooltip />
        <el-table-column prop="suggestion" label="建议" min-width="240" show-overflow-tooltip />
      </el-table>
      <el-empty v-if="!(data.warnings || []).length" class="portal-empty" description="暂无预警提醒" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getParentDashboard } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const data = ref<any>({})

async function loadData() {
  const parentUserId = userStore.user?.userId
  if (!parentUserId) return
  const res = await getParentDashboard({ parentUserId })
  data.value = res.data || {}
}

onMounted(loadData)
</script>
