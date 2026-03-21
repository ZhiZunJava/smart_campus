<template>
  <div class="portal-page wrongbook-page">
    <section class="wrongbook-hero portal-card">
      <div>
        <div class="wrongbook-hero__eyebrow">Wrongbook Studio</div>
        <div class="wrongbook-hero__title">我的错题本</div>
        <p class="wrongbook-hero__desc">
          这里会集中保留考试与练习过程中产生的错题，帮助你按错误频率、知识点与掌握状态进行回顾，并快速发起错题回练。
        </p>
      </div>
      <div class="wrongbook-hero__stats">
        <div class="wrongbook-hero__metric">
          <span>总错题</span>
          <strong>{{ wrongs.length }}</strong>
        </div>
        <div class="wrongbook-hero__metric">
          <span>未掌握</span>
          <strong>{{ overview.unmasteredCount || 0 }}</strong>
        </div>
        <div class="wrongbook-hero__metric">
          <span>高频错题</span>
          <strong>{{ highWrongCount }}</strong>
        </div>
      </div>
    </section>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">错题总数</div><div class="value">{{ overview.totalWrongCount || 0 }}</div><div class="sub">系统累计错题条目</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">涉及题库</div><div class="value">{{ (overview.catalogStats || []).length }}</div><div class="sub">错题来源题库维度</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">涉及知识点</div><div class="value">{{ (overview.knowledgePointStats || []).length }}</div><div class="sub">薄弱点分布数量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">高频错题</div><div class="value">{{ highWrongCount }}</div><div class="sub">错误次数 ≥ 3</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">建议动作</div><div class="sub">先回练高频错题，再补题库与知识点专项练习</div></el-card>
    </section>

    <div class="portal-grid portal-grid-2 mt20">
      <el-card class="portal-card">
        <template #header>
          <div class="wrongbook-header">
            <span>错题列表</span>
            <el-button type="primary" plain @click="openRetryDialog('smart')">智能回练</el-button>
          </div>
        </template>

        <el-form :inline="true" :model="filters" class="mb16">
          <el-form-item label="课程">
            <el-select v-model="filters.courseId" clearable filterable style="width: 180px" @change="getList">
              <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="题库">
            <el-select v-model="filters.catalogId" clearable filterable style="width: 180px" @change="getList">
              <el-option v-for="item in catalogOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="题型">
            <el-select v-model="filters.questionType" clearable style="width: 160px" @change="getList">
              <el-option v-for="item in questionTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="知识点">
            <el-input v-model="filters.knowledgePointId" style="width: 160px" @keyup.enter="getList" />
          </el-form-item>
          <el-form-item label="掌握状态">
            <el-select v-model="filters.masteryStatus" clearable style="width: 140px" @change="getList">
              <el-option label="未掌握" value="0" />
              <el-option label="已掌握" value="1" />
            </el-select>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="wrongs" @row-click="selectWrong">
          <el-table-column prop="questionId" label="题目ID" width="90" />
          <el-table-column label="题库" min-width="140">
            <template #default="scope">{{ catalogLabel(scope.row.catalogId) }}</template>
          </el-table-column>
          <el-table-column label="课程" min-width="160">
            <template #default="scope">{{ courseLabel(scope.row.courseId) }}</template>
          </el-table-column>
          <el-table-column label="知识点" min-width="160">
            <template #default="scope">{{ knowledgePointLabel(scope.row.knowledgePointId) }}</template>
          </el-table-column>
          <el-table-column prop="wrongCount" label="错误次数" width="100" />
          <el-table-column label="掌握状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.masteryStatus === '1' ? 'success' : 'warning'">{{ scope.row.masteryStatus === '1' ? '已掌握' : '未掌握' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loading && wrongs.length === 0" description="暂无错题数据" />
      </el-card>

      <el-card class="portal-card portal-soft-card">
        <template #header><span>错题详情与建议</span></template>
        <template v-if="selectedWrong">
          <div class="wrongbook-detail-block">
            <div class="wrongbook-detail-block__label">题型</div>
            <div class="wrongbook-detail-block__value">{{ questionTypeLabel(selectedWrong.questionType) }}</div>
          </div>
          <div class="wrongbook-detail-block">
            <div class="wrongbook-detail-block__label">题干</div>
            <div class="wrongbook-detail-block__value">{{ selectedWrong.stem || '-' }}</div>
          </div>
          <div class="wrongbook-detail-grid">
            <div class="portal-surface">题库：{{ catalogLabel(selectedWrong.catalogId) }}</div>
            <div class="portal-surface">课程：{{ courseLabel(selectedWrong.courseId) }}</div>
            <div class="portal-surface">知识点：{{ knowledgePointLabel(selectedWrong.knowledgePointId) }}</div>
            <div class="portal-surface">错误次数：{{ selectedWrong.wrongCount }}</div>
            <div class="portal-surface">掌握状态：{{ selectedWrong.masteryStatus === '1' ? '已掌握' : '未掌握' }}</div>
          </div>
          <div class="wrongbook-detail-block">
            <div class="wrongbook-detail-block__label">标准答案</div>
            <div class="wrongbook-detail-block__value">{{ selectedWrong.answer || '-' }}</div>
          </div>
          <div class="wrongbook-detail-block">
            <div class="wrongbook-detail-block__label">解析</div>
            <div class="wrongbook-detail-block__value">{{ selectedWrong.analysis || '-' }}</div>
          </div>
          <div class="wrongbook-actions">
            <el-button type="primary" @click="openRetryDialog('selected')">针对这题回练</el-button>
            <el-button plain @click="goExamHub">返回考试中心</el-button>
          </div>
        </template>
        <el-empty v-else description="点击左侧错题查看详情与建议" />
      </el-card>
    </div>

    <div class="portal-grid portal-grid-2 mt20">
      <el-card class="portal-card">
        <template #header><span>按题库统计</span></template>
        <el-table :data="overview.catalogStats || []" size="small" border>
          <el-table-column label="题库" min-width="160">
            <template #default="scope">{{ catalogLabel(scope.row.catalogId) }}</template>
          </el-table-column>
          <el-table-column prop="wrongQuestionCount" label="错题数" width="100" />
          <el-table-column prop="wrongTimes" label="累计错误" width="100" />
        </el-table>
      </el-card>

      <el-card class="portal-card">
        <template #header><span>按题型统计</span></template>
        <el-table :data="overview.questionTypeStats || []" size="small" border>
          <el-table-column label="题型">
            <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
          </el-table-column>
          <el-table-column prop="wrongQuestionCount" label="错题数" width="100" />
          <el-table-column prop="wrongTimes" label="累计错误" width="100" />
        </el-table>
      </el-card>

      <el-card class="portal-card">
        <template #header><span>按知识点统计</span></template>
        <el-table :data="overview.knowledgePointStats || []" size="small" border>
          <el-table-column label="知识点" min-width="160">
            <template #default="scope">{{ scope.row.knowledgePointName || knowledgePointLabel(scope.row.knowledgePointId) }}</template>
          </el-table-column>
          <el-table-column prop="wrongQuestionCount" label="错题数" width="100" />
          <el-table-column prop="wrongTimes" label="累计错误" width="100" />
        </el-table>
      </el-card>
    </div>

    <el-dialog v-model="retryOpen" title="错题回练" width="860px">
      <el-form :model="retryForm" label-width="100px">
        <el-form-item label="筛题模式">
          <el-radio-group v-model="retryForm.pickMode">
            <el-radio-button label="selected">已选错题</el-radio-button>
            <el-radio-button label="smart">智能筛题</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="回练名称"><el-input v-model="retryForm.paperName" /></el-form-item>
        <el-form-item label="课程ID"><el-input v-model="retryForm.courseId" /></el-form-item>
        <el-form-item v-if="retryForm.pickMode === 'smart'" label="知识点ID"><el-input v-model="retryForm.smartKnowledgePointId" placeholder="按知识点筛题，可留空" /></el-form-item>
        <el-form-item v-if="retryForm.pickMode === 'smart'" label="最少错次"><el-input-number v-model="retryForm.minWrongCount" :min="1" :max="100" /></el-form-item>
        <el-form-item label="时长"><el-input-number v-model="retryForm.durationMinutes" :min="5" :max="180" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="retryOpen = false">取消</el-button>
        <el-button type="primary" :loading="retryLoading" @click="submitRetry">生成回练卷</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="retryResultOpen" title="回练卷预览" width="1080px">
      <div class="preview-head">
        <span>试卷名称：{{ retryResult.paperName || '-' }}</span>
        <span>总分：{{ retryResult.totalScore || 0 }}</span>
        <span>时长：{{ retryResult.durationMinutes || 0 }} 分钟</span>
      </div>
      <el-table :data="retryResult.questions || []" border class="mt20">
        <el-table-column prop="sortNo" label="排序" width="90" />
        <el-table-column prop="questionId" label="题目ID" width="90" />
        <el-table-column label="题型" width="100">
          <template #default="scope">{{ questionTypeLabel(scope.row.question?.questionType || scope.row.questionType) }}</template>
        </el-table-column>
        <el-table-column prop="score" label="分值" width="90" />
        <el-table-column label="题干" min-width="360" show-overflow-tooltip>
          <template #default="scope">{{ scope.row.question?.stem || scope.row.stem || '-' }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from '@/utils/feedback'
import { createWrongRetryPaper, fetchPortalCourseOptions, getWrongBookOverview, listWrongBook } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const userStore = usePortalUserStore()
const loading = ref(false)
const wrongs = ref<any[]>([])
const selectedWrong = ref<any>(null)
const overview = ref<any>({})
const courseOptions = ref<any[]>([])
const retryOpen = ref(false)
const retryLoading = ref(false)
const retryResultOpen = ref(false)
const retryResult = ref<any>({})

const retryForm = reactive<any>({
  wrongIds: [] as number[],
  courseId: undefined,
  paperName: '',
  durationMinutes: 45,
  savePaper: false,
  pickMode: 'selected',
  smartKnowledgePointId: undefined,
  minWrongCount: 2,
})

const filters = reactive<any>({
  courseId: undefined,
  catalogId: undefined,
  questionType: '',
  knowledgePointId: '',
  masteryStatus: '',
})

const highWrongCount = computed(() =>
  wrongs.value.filter((item: any) => Number(item.wrongCount || 0) >= 3).length,
)

const catalogOptions = computed(() => {
  const stats = overview.value.catalogStats || []
  const rows = wrongs.value || []
  const ids = new Set<string>()
  const result: Array<{ label: string; value: number | string }> = []
  stats.forEach((item: any) => {
    if (!item?.catalogId) return
    const key = String(item.catalogId)
    if (ids.has(key)) return
    ids.add(key)
    result.push({ label: `题库 ${item.catalogId}`, value: item.catalogId })
  })
  rows.forEach((item: any) => {
    if (!item?.catalogId) return
    const key = String(item.catalogId)
    if (ids.has(key)) return
    ids.add(key)
    result.push({ label: `题库 ${item.catalogId}`, value: item.catalogId })
  })
  return result
})

const questionTypeOptions = [
  { label: '单选题', value: 'single' },
  { label: '多选题', value: 'multiple' },
  { label: '判断题', value: 'judge' },
  { label: '填空题', value: 'fill' },
  { label: '简答题', value: 'essay' },
  { label: '材料题', value: 'material' },
  { label: '案例题', value: 'case' },
]

function questionTypeLabel(type: string) {
  return ({ single: '单选题', multiple: '多选题', judge: '判断题', fill: '填空题', essay: '简答题', material: '材料题', case: '案例题' } as any)[type] || type || '-'
}

function courseLabel(courseId: number | string | undefined) {
  if (!courseId) return '通用题目'
  const matched = courseOptions.value.find((item: any) => String(item.value) === String(courseId))
  return matched?.label || `课程 ${courseId}`
}

function catalogLabel(catalogId: number | string | undefined) {
  if (!catalogId) return '未归类题库'
  const matched = catalogOptions.value.find((item: any) => String(item.value) === String(catalogId))
  return matched?.label || `题库 ${catalogId}`
}

function knowledgePointLabel(knowledgePointId: number | string | undefined) {
  return knowledgePointId ? `知识点 ${knowledgePointId}` : '-'
}

async function getList() {
  const userId = userStore.user?.userId
  if (!userId) return
  loading.value = true
  try {
    const [wrongRes, overviewRes] = await Promise.all([
      listWrongBook({
        pageNum: 1,
        pageSize: 100,
        userId,
        courseId: filters.courseId,
        catalogId: filters.catalogId,
        questionType: filters.questionType || undefined,
        knowledgePointId: filters.knowledgePointId || undefined,
        masteryStatus: filters.masteryStatus || undefined,
      }),
      getWrongBookOverview({
        userId,
        courseId: filters.courseId,
        catalogId: filters.catalogId,
        questionType: filters.questionType || undefined,
        knowledgePointId: filters.knowledgePointId || undefined,
        masteryStatus: filters.masteryStatus || undefined,
      }),
    ])
    wrongs.value = wrongRes.rows || []
    selectedWrong.value = wrongs.value[0] || null
    overview.value = overviewRes.data || {}
  } finally {
    loading.value = false
  }
}

function selectWrong(row: any) {
  selectedWrong.value = row
}

function openRetryDialog(mode: 'selected' | 'smart') {
  retryForm.pickMode = mode
  retryForm.paperName = `错题回练-${new Date().toLocaleDateString()}`
  retryForm.courseId = selectedWrong.value?.courseId
  retryForm.wrongIds = mode === 'selected' && selectedWrong.value ? [selectedWrong.value.id] : []
  retryOpen.value = true
}

async function submitRetry() {
  retryLoading.value = true
  try {
    const payload = { ...retryForm }
    if (retryForm.pickMode === 'smart') {
      const matched = wrongs.value.filter((item: any) => {
        const kpMatch = !retryForm.smartKnowledgePointId || String(item.knowledgePointId || '') === String(retryForm.smartKnowledgePointId)
        const countMatch = Number(item.wrongCount || 0) >= Number(retryForm.minWrongCount || 1)
        return kpMatch && countMatch
      })
      payload.wrongIds = matched.map((item: any) => item.id)
      if (!payload.wrongIds.length) {
        ElMessage.warning('当前条件下没有可回练的错题')
        retryLoading.value = false
        return
      }
    }
    const res = await createWrongRetryPaper(payload)
    retryResult.value = res.data || {}
    retryOpen.value = false
    retryResultOpen.value = true
    ElMessage.success('已生成回练卷预览')
  } finally {
    retryLoading.value = false
  }
}

function goExamHub() {
  router.push('/student/exams')
}

onMounted(async () => {
  const userId = userStore.user?.userId
  courseOptions.value = await fetchPortalCourseOptions(userId)
  await getList()
})
</script>

<style scoped>
.wrongbook-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(280px, 0.8fr);
  gap: 18px;
  padding: 22px;
  background:
    radial-gradient(circle at top left, rgba(255, 171, 0, 0.12) 0%, rgba(255, 171, 0, 0) 34%),
    linear-gradient(135deg, #ffffff 0%, #fffaf2 100%);
}
.wrongbook-hero__eyebrow {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255, 171, 0, 0.12);
  color: #c67a00;
  font-size: 12px;
  font-weight: 700;
}
.wrongbook-hero__title {
  margin-top: 12px;
  font-size: 30px;
  font-weight: 800;
  color: var(--portal-text);
}
.wrongbook-hero__desc {
  margin-top: 10px;
  max-width: 760px;
  font-size: 14px;
  line-height: 1.9;
  color: var(--portal-text-secondary);
}
.wrongbook-hero__stats {
  display: grid;
  gap: 14px;
}
.wrongbook-hero__metric {
  padding: 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid var(--portal-border);
}
.wrongbook-hero__metric span {
  color: var(--portal-text-secondary);
  font-size: 13px;
}
.wrongbook-hero__metric strong {
  display: block;
  margin-top: 8px;
  font-size: 30px;
  color: var(--portal-warning);
}
.wrongbook-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}
.wrongbook-detail-block + .wrongbook-detail-block {
  margin-top: 14px;
}
.wrongbook-detail-block__label {
  font-size: 13px;
  color: var(--portal-text-secondary);
}
.wrongbook-detail-block__value {
  margin-top: 6px;
  font-size: 15px;
  line-height: 1.8;
  color: var(--portal-text);
}
.wrongbook-detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 14px;
}
.wrongbook-actions {
  margin-top: 18px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.preview-head {
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
  font-weight: 600;
}
@media (max-width: 960px) {
  .wrongbook-hero {
    grid-template-columns: 1fr;
  }
  .wrongbook-detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
