<template>
  <div class="portal-page advisor-students-page">
    <section class="advisor-students-hero portal-card">
      <div class="advisor-students-hero__copy">
        <span class="advisor-students-hero__eyebrow">学生管理</span>
        <h3>负责学生总览</h3>
        <p>保持现有名单能力不变，同时把筛选、统计和表格层次拉回到学生门户的统一表达方式。</p>
      </div>
      <div class="advisor-students-hero__stats">
        <div class="advisor-students-hero__stat">
          <span>学生人数</span>
          <strong>{{ studentRows.length }}</strong>
        </div>
        <div class="advisor-students-hero__stat">
          <span>班级数</span>
          <strong>{{ visibleClassCount }}</strong>
        </div>
      </div>
    </section>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">学生人数</div><div class="value">{{ studentRows.length }}</div><div class="sub">当前筛选范围</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">班级数</div><div class="value">{{ visibleClassCount }}</div><div class="sub">负责班级覆盖</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">专业数</div><div class="value">{{ majorCount }}</div><div class="sub">涉及专业方向</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">在籍状态</div><div class="value">{{ activeCount }}</div><div class="sub">正常学生档案</div></el-card>
    </section>

    <section class="portal-card advisor-students-filter">
      <div class="portal-section-title">
        <h3>筛选条件</h3>
        <p>按班级、姓名、学号或专业快速过滤学生名单。</p>
      </div>
      <div class="portal-form-row advisor-students-filter__row">
        <el-select v-model="queryParams.classId" clearable filterable placeholder="全部班级" class="advisor-students-filter__select" @change="loadData">
          <el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="queryParams.keyword" clearable placeholder="搜索姓名、学号、专业或班级" class="advisor-students-filter__input" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
    </section>

    <section class="portal-card advisor-students-table">
      <div class="portal-section-title">
        <h3>学生名单</h3>
        <p>保留现有字段，同时增强主副信息和状态的辨识度。</p>
      </div>
      <el-skeleton :loading="loading" animated>
        <template #template>
          <div class="table-skeleton">
            <el-skeleton-item v-for="index in 6" :key="`student-skeleton-${index}`" variant="rect" class="table-skeleton__row" />
          </div>
        </template>
        <template #default>
          <el-table :data="studentRows" row-key="studentUserId">
            <el-table-column label="学生" min-width="240">
              <template #default="{ row }">
                <div class="advisor-rich-cell">
                  <strong>{{ row.studentName || '-' }}</strong>
                  <span>{{ row.studentNo ? `学号：${row.studentNo}` : `用户ID：${row.studentUserId}` }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="班级" min-width="180">
              <template #default="{ row }">
                <div class="advisor-soft-cell">
                  <strong>{{ row.className || '-' }}</strong>
                  <span>{{ row.deptName || '未配置院系' }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="专业" prop="major" min-width="160" />
            <el-table-column label="性别" prop="gender" width="100" />
            <el-table-column label="入学年份" prop="admissionYear" width="120" />
            <el-table-column label="院系" prop="deptName" min-width="160" />
            <el-table-column label="状态" width="110">
              <template #default="{ row }">
                <el-tag :type="row.status === '0' ? 'success' : 'info'">{{ row.status === '0' ? '正常' : '其他' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!studentRows.length" description="当前筛选范围下没有学生数据" />
        </template>
      </el-skeleton>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { listAdvisorStudents } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loading = ref(false)
const classOptions = ref<any[]>([])
const studentRows = ref<any[]>([])
const queryParams = reactive<{ classId?: number; keyword?: string }>({
  classId: undefined,
  keyword: '',
})

const visibleClassCount = computed(() => new Set(studentRows.value.map((item: any) => item.classId).filter(Boolean)).size)
const majorCount = computed(() => new Set(studentRows.value.map((item: any) => item.major).filter(Boolean)).size)
const activeCount = computed(() => studentRows.value.filter((item: any) => String(item.status || '0') === '0').length)

async function loadData() {
  const advisorUserId = userStore.user?.userId
  if (!advisorUserId) return
  loading.value = true
  try {
    const res = await listAdvisorStudents({
      advisorUserId,
      classId: queryParams.classId,
      keyword: queryParams.keyword,
    })
    classOptions.value = res.data?.classOptions || []
    studentRows.value = res.data?.items || []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.advisor-students-page {
  padding: 2.4rem 3.2rem;
  gap: 2rem;
}

.advisor-students-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(24rem, 0.7fr);
  gap: 1.8rem;
  padding: 2.4rem;
  background:
    radial-gradient(circle at top left, rgba(37, 99, 235, 0.12) 0%, rgba(37, 99, 235, 0) 34%),
    linear-gradient(135deg, #ffffff 0%, #f4f8ff 100%);
}

.advisor-students-hero__eyebrow {
  display: inline-flex;
  padding: 0.4rem 1rem;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.12);
  color: #2563eb;
  font-size: 1.2rem;
  font-weight: 700;
}

.advisor-students-hero__copy h3 {
  margin: 1.2rem 0 0;
  font-size: 3rem;
  font-weight: 800;
}

.advisor-students-hero__copy p {
  margin: 1rem 0 0;
  color: var(--portal-text-secondary);
  font-size: 1.4rem;
  line-height: 1.8;
}

.advisor-students-hero__stats {
  display: grid;
  gap: 1.2rem;
}

.advisor-students-hero__stat {
  padding: 1.6rem 1.8rem;
  border-radius: 1.8rem;
  background: rgba(255, 255, 255, 0.84);
  border: 1px solid var(--portal-border);
}

.advisor-students-hero__stat span {
  color: var(--portal-text-secondary);
  font-size: 1.3rem;
}

.advisor-students-hero__stat strong {
  display: block;
  margin-top: 0.8rem;
  font-size: 2.8rem;
  color: #2563eb;
}

.advisor-students-filter,
.advisor-students-table {
  padding: 2rem;
}

.advisor-students-filter__row {
  margin-top: 1rem;
}

.advisor-students-filter__select {
  width: 22rem;
}

.advisor-students-filter__input {
  width: 32rem;
}

.table-skeleton {
  display: grid;
  gap: 1.2rem;
}

.table-skeleton__row {
  height: 5.2rem;
  border-radius: 1rem;
}

.advisor-rich-cell,
.advisor-soft-cell {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}

.advisor-rich-cell strong,
.advisor-soft-cell strong {
  color: var(--portal-text);
  font-size: 1.4rem;
}

.advisor-rich-cell span,
.advisor-soft-cell span {
  color: var(--portal-text-secondary);
  font-size: 1.2rem;
}

@media (max-width: 960px) {
  .advisor-students-page {
    padding: 2rem;
  }

  .advisor-students-hero {
    grid-template-columns: 1fr;
  }

  .advisor-students-filter__select,
  .advisor-students-filter__input {
    width: 100%;
  }
}
</style>
