<template>
  <div class="portal-page advisor-score-page">
    <section class="advisor-score-hero portal-card">
      <div class="advisor-score-hero__copy">
        <span class="advisor-score-hero__eyebrow">成绩管理</span>
        <h3>{{ scoreTermLabel }}</h3>
        <p>以学生门户的分层信息方式重做筛选与结果区，方便快速定位需要优先跟进的班级和学生成绩。</p>
      </div>
      <div class="advisor-score-hero__stats">
        <div class="advisor-score-hero__stat">
          <span>成绩条数</span>
          <strong>{{ overview.scoreCount || 0 }}</strong>
        </div>
        <div class="advisor-score-hero__stat">
          <span>平均分</span>
          <strong>{{ formatScore(overview.avgScore) }}</strong>
        </div>
      </div>
    </section>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">成绩条数</div><div class="value">{{ overview.scoreCount || 0 }}</div><div class="sub">{{ scoreTermLabel }}</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">学生人数</div><div class="value">{{ overview.studentCount || 0 }}</div><div class="sub">成绩覆盖学生</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">班级数</div><div class="value">{{ overview.classCount || 0 }}</div><div class="sub">当前筛选范围</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">已发布</div><div class="value">{{ overview.publishedCount || 0 }}</div><div class="sub">未发布 {{ overview.unpublishedCount || 0 }}</div></el-card>
    </section>

    <section class="portal-card advisor-score-filter">
      <div class="portal-section-title">
        <h3>筛选条件</h3>
        <p>按学期、班级与关键词快速收敛成绩范围。</p>
      </div>
      <div class="portal-form-row advisor-score-filter__row">
        <el-select v-model="queryParams.termId" clearable filterable placeholder="全部学期" class="advisor-score-filter__select" @change="loadData">
          <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="queryParams.classId" clearable filterable placeholder="全部班级" class="advisor-score-filter__select" @change="loadData">
          <el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="queryParams.keyword" clearable placeholder="搜索学生姓名或课程名称" class="advisor-score-filter__input" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
    </section>

    <section class="portal-card advisor-score-table">
      <div class="portal-section-title">
        <h3>成绩明细</h3>
        <p>支持快速查看学生、课程、总评与发布状态。</p>
      </div>
      <el-skeleton :loading="loading" animated>
        <template #template>
          <div class="table-skeleton">
            <el-skeleton-item v-for="index in 6" :key="`score-skeleton-${index}`" variant="rect" class="table-skeleton__row" />
          </div>
        </template>
        <template #default>
          <el-table :data="scoreRows" row-key="scoreId">
            <el-table-column label="学生" min-width="220">
              <template #default="{ row }">
                <div class="advisor-rich-cell">
                  <strong>{{ row.studentName || '-' }}</strong>
                  <span>{{ row.studentNo ? `学号：${row.studentNo}` : `用户ID：${row.studentUserId}` }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="班级" min-width="160">
              <template #default="{ row }">
                <div class="advisor-soft-cell">
                  <strong>{{ row.className || '-' }}</strong>
                  <span>{{ scoreTermLabel }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="课程" min-width="240">
              <template #default="{ row }">
                <div class="advisor-rich-cell">
                  <strong>{{ row.courseName || '-' }}</strong>
                  <span>{{ row.teachingClassCode || '未配置教学班' }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="总评" width="110">
              <template #default="{ row }">
                <span class="advisor-score-pill">{{ formatScore(row.totalScore) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="绩点" width="90">
              <template #default="{ row }">{{ formatScore(row.gradePoint) }}</template>
            </el-table-column>
            <el-table-column label="等级" prop="gradeLevel" width="100" />
            <el-table-column label="排名" width="90">
              <template #default="{ row }">{{ row.rankNo || '-' }}</template>
            </el-table-column>
            <el-table-column label="发布状态" width="110">
              <template #default="{ row }">
                <el-tag :type="row.publishStatus === '1' ? 'success' : 'warning'">{{ row.publishStatus === '1' ? '已发布' : '未发布' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!scoreRows.length" description="当前筛选范围下没有成绩数据" />
        </template>
      </el-skeleton>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { listAdvisorScores, listPortalTermOptions } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loading = ref(false)
const classOptions = ref<any[]>([])
const termOptions = ref<any[]>([])
const scoreRows = ref<any[]>([])
const overview = ref<any>({})
const queryParams = reactive<{ termId?: number; classId?: number; keyword?: string }>({
  termId: undefined,
  classId: undefined,
  keyword: '',
})

const scoreTermLabel = computed(() => termOptions.value.find((item: any) => item.value === queryParams.termId)?.label || '当前学期')

function formatScore(value: any) {
  const num = Number(value)
  return Number.isFinite(num) ? num.toFixed(1) : '--'
}

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
  const current = termOptions.value.find((item: any) => item.isCurrent === '1')
  if (!queryParams.termId && current) {
    queryParams.termId = current.value
  }
}

async function loadData() {
  const advisorUserId = userStore.user?.userId
  if (!advisorUserId) return
  loading.value = true
  try {
    const res = await listAdvisorScores({
      advisorUserId,
      termId: queryParams.termId,
      classId: queryParams.classId,
      keyword: queryParams.keyword,
    })
    classOptions.value = res.data?.classOptions || []
    overview.value = res.data?.overview || {}
    scoreRows.value = res.data?.items || []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadTerms()
  await loadData()
})
</script>

<style scoped>
.advisor-score-page {
  padding: 24px 32px;
  gap: 20px;
}

.advisor-score-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(240px, 0.7fr);
  gap: 18px;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(37, 99, 235, 0.12) 0%, rgba(37, 99, 235, 0) 34%),
    linear-gradient(135deg, #ffffff 0%, #f4f8ff 100%);
}

.advisor-score-hero__eyebrow {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.12);
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
}

.advisor-score-hero__copy h3 {
  margin: 12px 0 0;
  font-size: 30px;
  font-weight: 800;
}

.advisor-score-hero__copy p {
  margin: 10px 0 0;
  color: var(--portal-text-secondary);
  font-size: 14px;
  line-height: 1.8;
}

.advisor-score-hero__stats {
  display: grid;
  gap: 12px;
}

.advisor-score-hero__stat {
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.84);
  border: 1px solid var(--portal-border);
}

.advisor-score-hero__stat span {
  color: var(--portal-text-secondary);
  font-size: 13px;
}

.advisor-score-hero__stat strong {
  display: block;
  margin-top: 8px;
  font-size: 28px;
  color: #2563eb;
}

.advisor-score-filter,
.advisor-score-table {
  padding: 20px;
}

.advisor-score-filter__row {
  margin-top: 10px;
}

.advisor-score-filter__select {
  width: 220px;
}

.advisor-score-filter__input {
  width: 320px;
}

.table-skeleton {
  display: grid;
  gap: 12px;
}

.table-skeleton__row {
  height: 52px;
  border-radius: 10px;
}

.advisor-rich-cell,
.advisor-soft-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.advisor-rich-cell strong,
.advisor-soft-cell strong {
  color: var(--portal-text);
  font-size: 14px;
}

.advisor-rich-cell span,
.advisor-soft-cell span {
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.advisor-score-pill {
  display: inline-flex;
  min-width: 56px;
  justify-content: center;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.1);
  color: #1d4ed8;
  font-weight: 700;
}

@media (max-width: 960px) {
  .advisor-score-page {
    padding: 20px;
  }

  .advisor-score-hero {
    grid-template-columns: 1fr;
  }

  .advisor-score-filter__select,
  .advisor-score-filter__input {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .advisor-score-page {
    padding: 16px;
    gap: 14px;
  }

  .advisor-score-hero {
    padding: 18px;
  }

  .advisor-score-hero__copy h3 {
    font-size: 22px;
  }

  .advisor-score-hero__stat strong {
    font-size: 22px;
  }

  .advisor-score-filter,
  .advisor-score-table {
    padding: 14px;
  }

  .advisor-score-pill {
    min-width: 48px;
    padding: 4px 8px;
    font-size: 13px;
  }
}

@media (max-width: 640px) {
  .advisor-score-page {
    padding: 12px;
    gap: 12px;
  }

  .advisor-score-hero {
    padding: 14px;
  }

  .advisor-score-hero__copy h3 {
    font-size: 18px;
  }

  .advisor-score-hero__copy p {
    font-size: 13px;
  }

  .advisor-score-hero__stat {
    padding: 12px 14px;
  }

  .advisor-score-hero__stat strong {
    font-size: 18px;
  }

  .advisor-score-filter,
  .advisor-score-table {
    padding: 12px;
  }
}
</style>
