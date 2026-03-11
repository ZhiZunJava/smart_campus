<template>
  <div class="portal-page">
    <div class="portal-section-title"><h3>个性推荐</h3><el-tag type="primary">基于学习画像与行为规则</el-tag></div>
    <div class="portal-grid portal-grid-3">
      <el-card v-for="item in list" :key="item.recommendId" class="portal-card recommend-card" shadow="hover">
        <div class="recommend-type">{{ item.resourceType || item.bizType }}</div>
        <div class="recommend-title">{{ item.title || '未命名推荐项' }}</div>
        <div class="recommend-score">推荐分：{{ item.recommendScore }}</div>
        <div class="recommend-reason">{{ item.recommendReason }}</div>
        <div class="recommend-actions">
          <el-button size="small" type="primary" plain @click="feedback(item, '1')">感兴趣</el-button>
          <el-button size="small" type="danger" plain @click="feedback(item, '2')">不感兴趣</el-button>
        </div>
      </el-card>
    </div>
    <el-empty v-if="!loading && list.length === 0" class="portal-empty" description="暂无推荐内容" />
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { feedbackRecommendation, getStudentDashboard } from '@/api/portal'
import usePortalUserStore from '@/store/user'
const userStore = usePortalUserStore()
const loading=ref(false), list=ref<any[]>([])
async function getList(){ const userId = userStore.user?.userId; if(!userId) return; loading.value=true; const res=await getStudentDashboard({ userId, recommendLimit: 20 }); list.value=res.data?.recommendations || []; loading.value=false }
async function feedback(item:any, status:string){ await feedbackRecommendation({ recommendId: item.recommendId, feedbackStatus: status, clickStatus: '1', exposeStatus: '1' }); ElMessage.success(status === '1' ? '已标记感兴趣' : '已标记不感兴趣'); getList() }
getList()
</script>
<style scoped>
.recommend-card{padding:4px}
.recommend-type{display:inline-flex;padding:4px 10px;border-radius:999px;background:#d5e7ff;color:#006eff;font-size:12px;margin-bottom:12px}
.recommend-title{font-size:18px;font-weight:700;margin-bottom:10px}
.recommend-score{color:#006eff;font-weight:600;margin-bottom:10px}
.recommend-reason{color:#5b6777;line-height:1.8}
.recommend-actions{display:flex;gap:8px;margin-top:14px}
</style>
