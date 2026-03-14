<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>个性推荐</h3>
      <el-space>
        <el-tag type="primary">基于学情诊断与规则推荐</el-tag>
        <el-segmented v-model="sceneCode" :options="sceneOptions" @change="getList" />
      </el-space>
    </div>

    <section class="portal-kpis" style="margin-top: 0; margin-bottom: 18px;">
      <el-card class="portal-card portal-stat-card"><div class="label">当前推荐</div><div class="value">{{ list.length }}</div><div class="sub">当前场景下可执行推荐</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">感兴趣</div><div class="value">{{ likedCount }}</div><div class="sub">你标记为感兴趣的资源</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">已完成</div><div class="value">{{ doneCount }}</div><div class="sub">已完成推荐任务</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">工作建议</div><div class="sub">先完成高分推荐，再进入错题巩固或考试练习</div></el-card>
    </section>

    <div class="portal-grid portal-grid-3">
      <el-card v-for="item in list" :key="item.recommendId" class="portal-card portal-soft-card recommend-card" shadow="hover">
        <div class="recommend-head">
          <div class="recommend-type">{{ item.resourceType || item.bizType }}</div>
          <el-tag size="small">{{ item.recommendScore }}</el-tag>
        </div>
        <div class="recommend-title">{{ item.title || '未命名推荐项' }}</div>
        <div class="recommend-reason">{{ item.recommendReason }}</div>
        <div class="recommend-meta">场景：{{ item.sceneCode || sceneCode }}</div>
        <div class="recommend-actions">
          <el-button size="small" type="primary" @click="feedback(item, 'LIKE')">感兴趣</el-button>
          <el-button size="small" type="success" plain @click="feedback(item, 'DONE')">已完成</el-button>
          <el-button size="small" type="danger" plain @click="feedback(item, 'DISLIKE')">不感兴趣</el-button>
        </div>
      </el-card>
    </div>

    <el-empty v-if="!loading && list.length === 0" class="portal-empty" description="暂无推荐内容" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { feedbackRecommendation, getAnalysisMeta, listActiveRecommendation } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loading = ref(false)
const list = ref<any[]>([])
const sceneCode = ref('home')
const sceneOptions = ref<any[]>([{ label: '首页', value: 'home' }, { label: '工作台', value: 'workbench' }, { label: '诊断', value: 'diagnosis' }])

const likedCount = computed(() => list.value.filter((item: any) => item.feedbackStatus === 'LIKE').length)
const doneCount = computed(() => list.value.filter((item: any) => item.feedbackStatus === 'DONE').length)

async function getList() {
  const userId = userStore.user?.userId
  if (!userId) return
  loading.value = true
  try {
    const res = await listActiveRecommendation({ userId, sceneCode: sceneCode.value, limit: 12 })
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

async function feedback(item: any, status: string) {
  await feedbackRecommendation({ recommendId: item.recommendId, feedbackStatus: status, clickStatus: '1', exposeStatus: '1' })
  ElMessage.success('反馈已提交')
  getList()
}

async function loadMeta() {
  const res = await getAnalysisMeta()
  const scenes = res.data?.supportedScenes || []
  if (scenes.length) {
    sceneOptions.value = scenes.map((scene: string) => ({ label: scene, value: scene }))
    if (!scenes.includes(sceneCode.value)) {
      sceneCode.value = scenes[0]
    }
  }
}

onMounted(async () => {
  await loadMeta()
  await getList()
})
</script>

<style scoped>
.recommend-card { padding: 2px; }
.recommend-head { display: flex; align-items: center; justify-content: space-between; gap: 10px; }
.recommend-type { display: inline-flex; padding: 4px 10px; border-radius: 999px; background: var(--portal-brand-light); color: var(--portal-brand); font-size: 12px; }
.recommend-title { margin-top: 14px; font-size: 18px; font-weight: 700; }
.recommend-reason { margin-top: 10px; color: var(--portal-text-secondary); line-height: 1.8; min-height: 72px; }
.recommend-meta { margin-top: 10px; color: var(--portal-text-secondary); font-size: 12px; }
.recommend-actions { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 16px; }
</style>
