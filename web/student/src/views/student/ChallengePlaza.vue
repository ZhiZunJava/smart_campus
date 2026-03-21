<template>
  <div class="portal-page plaza-page">
    <section class="plaza-hero portal-card">
      <div>
        <div class="plaza-hero__eyebrow">Task Center</div>
        <div class="plaza-hero__title">任务中心</div>
        <p class="plaza-hero__desc">
          统一聚合考试任务、课程任务、错题回练和开放挑战，让每天该做什么一目了然。
        </p>
      </div>
      <div class="plaza-hero__stats">
        <div class="plaza-hero__metric">
          <span>今日待办</span>
          <strong>{{ todoTasks.length }}</strong>
        </div>
        <div class="plaza-hero__metric">
          <span>推荐任务</span>
          <strong>{{ recommendedTasks.length }}</strong>
        </div>
      </div>
    </section>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">考试任务</div><div class="value">{{ examTasks.length }}</div><div class="sub">来自课程考试与开放考试</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">错题任务</div><div class="value">{{ wrongTasks.length }}</div><div class="sub">来自错题回练与薄弱项</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">课程任务</div><div class="value">{{ courseTasks.length }}</div><div class="sub">来自当前学期课程与课程详情</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">开放挑战</div><div class="value">{{ generalPapers.length }}</div><div class="sub">不依赖课程即可参与</div></el-card>
    </section>

    <div class="portal-grid portal-grid-2 mt20">
      <el-card class="portal-card">
        <template #header><span>今日待办</span></template>
        <div class="task-list">
          <article v-for="task in todoTasks" :key="task.key" class="task-card task-card--todo">
            <div class="task-card__head">
              <div class="task-card__title">{{ task.title }}</div>
              <el-tag :type="task.tagType" effect="plain">{{ task.tag }}</el-tag>
            </div>
            <div class="task-card__desc">{{ task.desc }}</div>
            <div class="task-card__meta">
              <span v-for="meta in task.meta" :key="meta">{{ meta }}</span>
            </div>
            <div class="task-card__actions">
              <el-button type="primary" size="small" @click="openTask(task)">立即处理</el-button>
            </div>
          </article>
        </div>
        <el-empty v-if="!todoTasks.length" description="今天没有待办任务" />
      </el-card>

      <el-card class="portal-card portal-soft-card">
        <template #header><span>推荐任务</span></template>
        <div class="task-list">
          <article v-for="task in recommendedTasks" :key="task.key" class="task-card">
            <div class="task-card__head">
              <div class="task-card__title">{{ task.title }}</div>
              <el-tag :type="task.tagType" effect="plain">{{ task.tag }}</el-tag>
            </div>
            <div class="task-card__desc">{{ task.desc }}</div>
            <div class="task-card__meta">
              <span v-for="meta in task.meta" :key="meta">{{ meta }}</span>
            </div>
            <div class="task-card__actions">
              <el-button plain size="small" @click="openTask(task)">查看</el-button>
            </div>
          </article>
        </div>
      </el-card>
    </div>

    <div class="portal-grid portal-grid-2 mt20">
      <el-card class="portal-card">
        <template #header><span>任务分类</span></template>
        <el-table :data="taskBuckets" size="small" border>
          <el-table-column prop="label" label="任务类型" min-width="140" />
          <el-table-column prop="count" label="数量" width="90" />
          <el-table-column prop="desc" label="说明" min-width="240" show-overflow-tooltip />
        </el-table>
      </el-card>

      <el-card class="portal-card">
        <template #header><span>历史任务</span></template>
        <el-table :data="historyTasks" size="small" border>
          <el-table-column prop="title" label="任务标题" min-width="220" show-overflow-tooltip />
          <el-table-column prop="tag" label="类型" width="100" />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button link type="primary" @click="openTask(scope.row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  getPortalTaskCenter,
} from '@/api/portal'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const userStore = usePortalUserStore()
const loading = ref(false)
const taskCenter = ref<any>({})

const todoTasks = computed(() => taskCenter.value.todoTasks || [])
const recommendedTasks = computed(() => taskCenter.value.recommendedTasks || [])
const historyTasks = computed(() => taskCenter.value.historyTasks || [])
const examTasks = computed(() => taskCenter.value.examTasks || [])
const wrongTasks = computed(() => taskCenter.value.wrongTasks || [])
const courseTasks = computed(() => taskCenter.value.courseTasks || [])
const generalPapers = computed(() => examTasks.value.filter((item: any) => item?.action?.paperId && item?.desc?.includes('开放考试')))
const taskBuckets = computed(() => [
  { label: '考试任务', count: taskCenter.value.stats?.examTaskCount || 0, desc: '来自课程考试与开放考试' },
  { label: '错题任务', count: taskCenter.value.stats?.wrongTaskCount || 0, desc: '来自错题回练与薄弱项复盘' },
  { label: '课程任务', count: taskCenter.value.stats?.courseTaskCount || 0, desc: '来自当前学期课程详情' },
  { label: '历史任务', count: historyTasks.value.length, desc: '已经完成或已提交的任务记录' },
])

function openTask(task: any) {
  const action = task?.action || {}
  if (action.type === 'exam') {
    router.push('/student/exams')
    return
  }
  if (action.type === 'resume' && action.row?.recordId) {
    router.push({
      path: `/student/exams/session/${action.row.recordId}`,
      query: {
        paperId: String(action.row.paperId || ''),
        startedAt: String(action.row.startTime || ''),
      },
    })
    return
  }
  if (action.type === 'record') {
    router.push('/student/exams?tab=records')
    return
  }
  if (action.type === 'wrongbook') {
    router.push('/student/wrongbook')
    return
  }
  if (action.type === 'course') {
    router.push({
      path: '/student/courses',
      query: action.paperId ? { openCourseId: String(action.paperId) } : {},
    })
    return
  }
  router.push('/student/exams')
}

async function loadData() {
  const userId = userStore.user?.userId
  if (!userId) return
  loading.value = true
  try {
    const res = await getPortalTaskCenter({ userId })
    taskCenter.value = res.data || {}
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.plaza-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(280px, 0.8fr);
  gap: 18px;
  padding: 22px;
  background:
    radial-gradient(circle at top left, rgba(24, 148, 106, 0.16) 0%, rgba(24, 148, 106, 0) 34%),
    linear-gradient(135deg, #ffffff 0%, #f2fbf8 100%);
}
.plaza-hero__eyebrow {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(24, 148, 106, 0.12);
  color: #12795a;
  font-size: 12px;
  font-weight: 700;
}
.plaza-hero__title {
  margin-top: 12px;
  font-size: 30px;
  font-weight: 800;
  color: var(--portal-text);
}
.plaza-hero__desc {
  margin-top: 10px;
  max-width: 760px;
  font-size: 14px;
  line-height: 1.9;
  color: var(--portal-text-secondary);
}
.plaza-hero__stats {
  display: grid;
  gap: 14px;
}
.plaza-hero__metric {
  padding: 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid var(--portal-border);
}
.plaza-hero__metric span {
  color: var(--portal-text-secondary);
  font-size: 13px;
}
.plaza-hero__metric strong {
  display: block;
  margin-top: 8px;
  font-size: 30px;
  color: #12795a;
}
.task-list {
  display: grid;
  gap: 14px;
}
.task-card {
  padding: 16px;
  border-radius: 18px;
  background: linear-gradient(180deg, #ffffff 0%, #f7fcfa 100%);
  border: 1px solid var(--portal-border);
}
.task-card--todo {
  border-color: #f0c27b;
  background: linear-gradient(180deg, #fffdf8 0%, #fff7eb 100%);
}
.task-card__head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}
.task-card__title {
  font-size: 16px;
  font-weight: 800;
  color: var(--portal-text);
  line-height: 1.6;
}
.task-card__desc {
  margin-top: 8px;
  color: var(--portal-text-secondary);
  line-height: 1.8;
  font-size: 14px;
}
.task-card__meta {
  margin-top: 10px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  color: var(--portal-text-secondary);
  font-size: 12px;
}
.task-card__actions {
  margin-top: 12px;
}
@media (max-width: 960px) {
  .plaza-hero {
    grid-template-columns: 1fr;
  }
}
</style>
