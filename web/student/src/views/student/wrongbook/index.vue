<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>我的错题本</h3>
      <el-tag type="warning">聚焦高频错题与未掌握题目</el-tag>
    </div>

    <section class="portal-kpis" style="margin-top: 0; margin-bottom: 18px;">
      <el-card class="portal-card portal-stat-card"><div class="label">错题总数</div><div class="value">{{ wrongs.length }}</div><div class="sub">当前累计错题条目</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">未掌握</div><div class="value">{{ unmasteredCount }}</div><div class="sub">建议优先复习</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">高频错题</div><div class="value">{{ highWrongCount }}</div><div class="sub">错误次数 ≥ 3</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">复习建议</div><div class="sub">先复习高频错题，再做阶段练习验证</div></el-card>
    </section>

    <div class="portal-grid portal-grid-2">
      <el-card class="portal-card">
        <template #header><span>错题列表</span></template>
        <el-table v-loading="loading" :data="wrongs" @row-click="selectWrong">
          <el-table-column prop="questionId" label="题目ID" width="90" />
          <el-table-column prop="courseId" label="课程ID" width="90" />
          <el-table-column prop="wrongCount" label="错误次数" width="100" />
          <el-table-column prop="lastWrongTime" label="最后做错时间" min-width="160" />
          <el-table-column label="掌握状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.masteryStatus === '1' ? 'success' : 'warning'">{{ scope.row.masteryStatus === '1' ? '已掌握' : '未掌握' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loading && wrongs.length === 0" description="暂无错题数据" />
      </el-card>

      <el-card class="portal-card portal-soft-card">
        <template #header><span>复习建议</span></template>
        <template v-if="selectedWrong">
          <div class="portal-surface">题目ID：{{ selectedWrong.questionId }}</div>
          <div class="portal-surface mt16">课程ID：{{ selectedWrong.courseId }}</div>
          <div class="portal-surface mt16">错误次数：{{ selectedWrong.wrongCount }}</div>
          <div class="portal-surface mt16">最近做错时间：{{ selectedWrong.lastWrongTime }}</div>
          <div class="mt16">
            <div class="portal-section-title"><h3>建议动作</h3></div>
            <ul class="portal-simple-list">
              <li>先回顾相关章节知识点和课堂笔记。</li>
              <li>重新做一次同类型题目，验证是否已掌握。</li>
              <li>如果连续错误，建议去智能问答中提问具体解题思路。</li>
            </ul>
          </div>
        </template>
        <el-empty v-else description="点击左侧错题查看复习建议" />
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { listWrongBook } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loading = ref(false)
const wrongs = ref<any[]>([])
const selectedWrong = ref<any>(null)

const unmasteredCount = computed(() => wrongs.value.filter((item: any) => item.masteryStatus !== '1').length)
const highWrongCount = computed(() => wrongs.value.filter((item: any) => Number(item.wrongCount || 0) >= 3).length)

async function getList() {
  const userId = userStore.user?.userId
  if (!userId) return
  loading.value = true
  try {
    const res = await listWrongBook({ pageNum: 1, pageSize: 50, userId })
    wrongs.value = res.rows || []
    selectedWrong.value = wrongs.value[0] || null
  } finally {
    loading.value = false
  }
}

function selectWrong(row: any) {
  selectedWrong.value = row
}

onMounted(getList)
</script>
