<template>
  <div class="portal-page portal-grid portal-grid-2">
    <el-card class="portal-card">
      <template #header><span>孩子学习概览</span></template>
      <el-row :gutter="16">
        <el-col :span="8"><div class="portal-stat"><div class="label">学习行为</div><div class="value">{{ data.studyRecordCount || 0 }}</div></div></el-col>
        <el-col :span="8"><div class="portal-stat"><div class="label">考试记录</div><div class="value">{{ data.examRecordCount || 0 }}</div></div></el-col>
        <el-col :span="8"><div class="portal-stat"><div class="label">预警数量</div><div class="value">{{ data.warningCount || 0 }}</div></div></el-col>
      </el-row>
      <div class="mt16" v-if="data.profile">能力等级：{{ data.profile.abilityLevel }}｜掌握度：{{ data.profile.masteryScore }}｜风险分：{{ data.profile.riskScore }}</div>
    </el-card>
    <el-card class="portal-card">
      <template #header><span>家长建议</span></template>
      <ul class="suggest-list">
        <li>关注预警较高的学习阶段，及时和教师沟通。</li>
        <li>鼓励孩子按推荐资源完成阶段学习任务。</li>
        <li>查看考试记录与报告，帮助孩子查缺补漏。</li>
      </ul>
    </el-card>
    <el-card class="portal-card" style="grid-column: 1 / -1;">
      <template #header><span>近期预警</span></template>
      <el-table :data="data.warnings || []" border>
        <el-table-column prop="warningType" label="预警类型" width="120" />
        <el-table-column label="等级" width="100"><template #default="scope"><el-tag :type="scope.row.warningLevel === 'HIGH' ? 'danger' : scope.row.warningLevel === 'MEDIUM' ? 'warning' : 'success'">{{ scope.row.warningLevel }}</el-tag></template></el-table-column>
        <el-table-column prop="warningContent" label="预警内容" min-width="240" show-overflow-tooltip />
        <el-table-column prop="suggestion" label="建议" min-width="240" show-overflow-tooltip />
      </el-table>
      <el-empty v-if="(data.warnings || []).length === 0" class="portal-empty" description="暂无预警提醒" />
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getParentDashboard } from '@/api/portal'
import usePortalUserStore from '@/store/user'
const userStore = usePortalUserStore()
const data=ref<any>({})
async function loadData(){ const parentUserId = userStore.user?.userId; if(!parentUserId) return; const res=await getParentDashboard({ parentUserId }); data.value=res.data||{} }
onMounted(loadData)
</script>
<style scoped>.mt16{margin-top:16px}.suggest-list{margin:0;padding-left:18px;color:#5b6777;line-height:1.9}</style>
