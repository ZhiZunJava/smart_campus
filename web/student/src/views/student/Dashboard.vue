<template>
  <div class="portal-page">
    <el-card class="portal-card hero-card">
      <div class="hero-left">
        <div class="hero-tag">学习首页</div>
        <h2>欢迎回来，{{ userStore.user?.nickName || userStore.user?.userName || '同学' }}</h2>
        <p>围绕资源、行为、画像、推荐、问答与考试，持续构建你的个性化学习闭环。</p>
      </div>
      <div class="hero-right">
        <div class="hero-item"><span>能力等级</span><strong>{{ data.profile?.abilityLevel || '-' }}</strong></div>
        <div class="hero-item"><span>风险分</span><strong>{{ data.profile?.riskScore || 0 }}</strong></div>
      </div>
    </el-card>

    <el-row :gutter="16" class="mt16">
      <el-col :span="6"><el-card class="portal-card portal-stat"><div class="label">学习行为</div><div class="value">{{ data.studyRecordCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card class="portal-card portal-stat"><div class="label">考试记录</div><div class="value">{{ data.examRecordCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card class="portal-card portal-stat"><div class="label">预警数量</div><div class="value">{{ data.warningCount || 0 }}</div></el-card></el-col>
      <el-col :span="6"><el-card class="portal-card portal-stat"><div class="label">掌握度</div><div class="value">{{ data.profile?.masteryScore || 0 }}</div></el-card></el-col>
    </el-row>

    <el-row :gutter="16" class="mt16">
      <el-col :span="10"><el-card class="portal-card"><template #header><span>学习画像</span></template><div v-if="data.profile">活跃度：{{ data.profile.activeScore }}<br />专注度：{{ data.profile.concentrationScore }}<br />掌握度：{{ data.profile.masteryScore }}<br />兴趣度：{{ data.profile.interestScore }}<br />风险分：{{ data.profile.riskScore }}</div><el-empty v-else description="暂无画像数据" /></el-card></el-col>
      <el-col :span="14"><el-card class="portal-card"><template #header><span>推荐资源</span></template><el-table :data="data.recommendations || []"><el-table-column prop="title" label="标题" min-width="180" /><el-table-column prop="resourceType" label="类型" width="100" /><el-table-column prop="recommendReason" label="推荐理由" min-width="220" show-overflow-tooltip /></el-table></el-card></el-col>
    </el-row>

    <el-card class="portal-card mt16"><template #header><span>预警提醒</span></template><el-table :data="data.warnings || []"><el-table-column prop="warningType" label="预警类型" width="120" /><el-table-column prop="warningLevel" label="等级" width="100" /><el-table-column prop="warningContent" label="预警内容" min-width="240" show-overflow-tooltip /><el-table-column prop="suggestion" label="建议" min-width="240" show-overflow-tooltip /></el-table><el-empty v-if="(data.warnings || []).length === 0" class="portal-empty" description="暂无预警信息" /></el-card>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getStudentDashboard } from '@/api/portal'
import usePortalUserStore from '@/store/user'
const userStore = usePortalUserStore()
const data = ref<any>({})
async function loadData(){ const userId = userStore.user?.userId; if(!userId) return; const res = await getStudentDashboard({ userId, recommendLimit: 5 }); data.value = res.data || {} }
onMounted(loadData)
</script>
<style scoped>
.mt16{margin-top:16px}
.hero-card{display:flex;align-items:center;justify-content:space-between;gap:24px;background:linear-gradient(135deg,#0f4fb5 0%,#26a1ff 100%);color:#fff}
.hero-left h2{margin:8px 0 12px;font-size:30px}.hero-left p{margin:0;max-width:560px;line-height:1.8;color:rgba(255,255,255,.9)}
.hero-tag{display:inline-flex;padding:4px 10px;border-radius:999px;background:rgba(255,255,255,.18);font-size:12px}
.hero-right{display:grid;gap:12px;min-width:180px}.hero-item{padding:12px 16px;border-radius:14px;background:rgba(255,255,255,.14)}.hero-item span{display:block;font-size:12px;color:rgba(255,255,255,.78)}.hero-item strong{display:block;margin-top:6px;font-size:24px}
</style>
