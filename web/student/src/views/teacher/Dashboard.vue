<template>
  <div class="portal-page portal-grid portal-grid-2">
    <el-card class="portal-card">
      <template #header><span>教学概览</span></template>
      <el-row :gutter="16">
        <el-col :span="12"><div class="portal-stat"><div class="label">课程数</div><div class="value">{{ data.courseCount || 0 }}</div></div></el-col>
        <el-col :span="12"><div class="portal-stat"><div class="label">班级数</div><div class="value">{{ data.classCount || 0 }}</div></div></el-col>
        <el-col :span="12" class="mt16"><div class="portal-stat"><div class="label">资源数</div><div class="value">{{ data.resourceCount || 0 }}</div></div></el-col>
        <el-col :span="12" class="mt16"><div class="portal-stat"><div class="label">预警数</div><div class="value">{{ data.warningCount || 0 }}</div></div></el-col>
      </el-row>
    </el-card>
    <el-card class="portal-card">
      <template #header><span>教学建议</span></template>
      <ul class="suggest-list">
        <li>优先关注高风险学生与班级的学习波动。</li>
        <li>定期更新课程资源，保持资源质量评分稳定。</li>
        <li>结合题库与试卷，组织阶段性测评与反馈。</li>
      </ul>
    </el-card>
    <el-card class="portal-card" style="grid-column: 1 / -1;">
      <template #header><span>预警概览</span></template>
      <el-table v-loading="loadingWarning" :data="warnings" border>
        <el-table-column prop="warningId" label="预警ID" width="90" />
        <el-table-column prop="userId" label="学生ID" width="90" />
        <el-table-column prop="warningType" label="预警类型" width="120" />
        <el-table-column label="等级" width="100"><template #default="scope"><el-tag :type="scope.row.warningLevel === 'HIGH' ? 'danger' : scope.row.warningLevel === 'MEDIUM' ? 'warning' : 'success'">{{ scope.row.warningLevel }}</el-tag></template></el-table-column>
        <el-table-column prop="warningContent" label="预警内容" min-width="240" show-overflow-tooltip />
        <el-table-column prop="suggestion" label="建议" min-width="240" show-overflow-tooltip />
      </el-table>
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
async function loadData(){ const teacherId = userStore.user?.userId; if(!teacherId) return; const res=await getTeacherDashboard({ teacherId }); data.value=res.data||{} }
async function loadWarnings(){ loadingWarning.value = true; const res = await listWarning({ pageNum: 1, pageSize: 8 }); warnings.value = res.rows || []; loadingWarning.value = false }
onMounted(async()=>{ await loadData(); await loadWarnings() })
</script>
<style scoped>.mt16{margin-top:16px}.suggest-list{margin:0;padding-left:18px;color:#5b6777;line-height:1.9}</style>
