<template>
  <div class="portal-page wrongbook-page" style="padding-bottom: 40px;">
    <div class="portal-page-header">
      <div class="portal-page-title">
        <div class="portal-page-title-icon"></div>
        <span>我的错题本</span>
      </div>
      <div class="portal-page-actions">
        <el-button type="primary" plain @click="getList">刷新</el-button>
      </div>
    </div>

    <section class="wrongbook-hero">
      <div class="hero-content">
        <div class="hero-eyebrow">Wrongbook Studio</div>
        <div class="hero-title">我的错题本</div>
        <p class="hero-desc">
          这里会集中保留考试与练习过程中产生的错题，帮助你按错误频率、知识点与掌握状态进行回顾，并快速发起错题回练。
        </p>
      </div>
      <div class="hero-stats">
        <div class="hero-metric">
          <span>总错题</span>
          <strong>{{ wrongs.length }}</strong>
        </div>
        <div class="hero-metric">
          <span>未掌握</span>
          <strong>{{ overview.unmasteredCount || 0 }}</strong>
        </div>
        <div class="hero-metric">
          <span>高频错题</span>
          <strong>{{ highWrongCount }}</strong>
        </div>
      </div>
    </section>

    <section class="wrongbook-kpis">
      <div class="wrongbook-kpi-card">
        <div class="kpi-content">
          <div class="kpi-label">错题总数</div>
          <div class="kpi-value text-primary">{{ overview.totalWrongCount || 0 }}</div>
          <div class="kpi-sub">系统累计错题条目</div>
        </div>
      </div>
      <div class="wrongbook-kpi-card">
        <div class="kpi-content">
          <div class="kpi-label">涉及题库</div>
          <div class="kpi-value">{{ (overview.catalogStats || []).length }}</div>
          <div class="kpi-sub">错题来源题库维度</div>
        </div>
      </div>
      <div class="wrongbook-kpi-card">
        <div class="kpi-content">
          <div class="kpi-label">涉及知识点</div>
          <div class="kpi-value">{{ (overview.knowledgePointStats || []).length }}</div>
          <div class="kpi-sub">薄弱点分布数量</div>
        </div>
      </div>
      <div class="wrongbook-kpi-card">
        <div class="kpi-content">
          <div class="kpi-label">高频错题</div>
          <div class="kpi-value text-warning">{{ highWrongCount }}</div>
          <div class="kpi-sub">错误次数 ≥ 3</div>
        </div>
      </div>
      <div class="wrongbook-kpi-card kpi-card-wide">
        <div class="kpi-content">
          <div class="kpi-label">建议动作</div>
          <div class="kpi-sub mt-auto">先回练高频错题，再补缺题库与知识点专项练习</div>
        </div>
      </div>
    </section>

    <div class="wrongbook-workbench mt20">
      <div class="wrongbook-section-card">
        <div class="section-header">
          <div class="section-title">错题列表</div>
          <el-button type="primary" plain @click="openRetryDialog('smart')">智能回练</el-button>
        </div>

        <el-form :inline="true" :model="filters" class="wrongbook-filters">
          <el-form-item label="课程">
            <el-select v-model="filters.courseId" clearable filterable style="width: 140px" @change="getList">
              <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="题库">
            <el-select v-model="filters.catalogId" clearable filterable style="width: 140px" @change="getList">
              <el-option v-for="item in catalogOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="题型">
            <el-select v-model="filters.questionType" clearable style="width: 120px" @change="getList">
              <el-option v-for="item in questionTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="知识点">
            <el-input v-model="filters.knowledgePointId" style="width: 120px" @keyup.enter="getList" />
          </el-form-item>
          <el-form-item label="掌握状态">
            <el-select v-model="filters.masteryStatus" clearable style="width: 110px" @change="getList">
              <el-option label="未掌握" value="0" />
              <el-option label="已掌握" value="1" />
            </el-select>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="wrongs" @row-click="selectWrong" class="custom-table" highlight-current-row>
          <el-table-column prop="questionId" label="题目ID" min-width="90" />
          <el-table-column label="题库" min-width="120" show-overflow-tooltip>
            <template #default="scope">{{ catalogLabel(scope.row.catalogId) }}</template>
          </el-table-column>
          <el-table-column label="课程" min-width="140" show-overflow-tooltip>
            <template #default="scope">{{ courseLabel(scope.row.courseId) }}</template>
          </el-table-column>
          <el-table-column label="知识点" min-width="120" show-overflow-tooltip>
            <template #default="scope">{{ knowledgePointLabel(scope.row.knowledgePointId) }}</template>
          </el-table-column>
          <el-table-column prop="wrongCount" label="错误次数" min-width="100" align="center" />
          <el-table-column label="掌握状态" min-width="110" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.masteryStatus === '1' ? 'success' : 'warning'" effect="light" size="small">
                {{ scope.row.masteryStatus === '1' ? '已掌握' : '未掌握' }}
              </el-tag>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="暂无错题数据" :image-size="80" />
          </template>
        </el-table>
      </div>

      <div class="wrongbook-section-card wrongbook-section-card--soft">
        <div class="section-header">
          <div class="section-title">错题详情与建议</div>
        </div>
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
            <div class="detail-grid-item"><span>题库</span><strong>{{ catalogLabel(selectedWrong.catalogId) }}</strong></div>
            <div class="detail-grid-item"><span>课程</span><strong>{{ courseLabel(selectedWrong.courseId) }}</strong></div>
            <div class="detail-grid-item"><span>知识点</span><strong>{{ knowledgePointLabel(selectedWrong.knowledgePointId) }}</strong></div>
            <div class="detail-grid-item"><span>错误次数</span><strong class="text-danger">{{ selectedWrong.wrongCount }}</strong></div>
            <div class="detail-grid-item"><span>掌握状态</span>
              <strong :class="selectedWrong.masteryStatus === '1' ? 'text-success' : 'text-warning'">
                {{ selectedWrong.masteryStatus === '1' ? '已掌握' : '未掌握' }}
              </strong>
            </div>
          </div>
          <div class="wrongbook-detail-block">
            <div class="wrongbook-detail-block__label">标准答案</div>
            <div class="wrongbook-detail-block__value text-success">{{ selectedWrong.answer || '-' }}</div>
          </div>
          <div class="wrongbook-detail-block">
            <div class="wrongbook-detail-block__label">解析</div>
            <div class="wrongbook-detail-block__value analysis-box">{{ selectedWrong.analysis || '-' }}</div>
          </div>
          <div class="wrongbook-actions">
            <el-button type="primary" @click="openRetryDialog('selected')">针对这题回练</el-button>
            <el-button plain @click="goExamHub">返回考试中心</el-button>
          </div>
        </template>
        <el-empty v-else description="点击左侧错题查看详情与建议" :image-size="80" />
      </div>
    </div>

    <div class="portal-grid portal-grid-3 mt20">
      <div class="wrongbook-section-card">
        <div class="section-header">
          <div class="section-title">按题库统计</div>
        </div>
        <el-table :data="overview.catalogStats || []" class="custom-table" style="width: 100%" :header-cell-style="{ background: '#fafafa', color: '#606266', fontWeight: 500 }">
          <el-table-column label="题库" min-width="120" show-overflow-tooltip>
            <template #default="scope">{{ catalogLabel(scope.row.catalogId) }}</template>
          </el-table-column>
          <el-table-column prop="wrongQuestionCount" label="错题数" min-width="80" align="center" />
          <el-table-column prop="wrongTimes" label="累计错误" min-width="90" align="center" />
        </el-table>
      </div>

      <div class="wrongbook-section-card">
        <div class="section-header">
          <div class="section-title">按题型统计</div>
        </div>
        <el-table :data="overview.questionTypeStats || []" class="custom-table" style="width: 100%" :header-cell-style="{ background: '#fafafa', color: '#606266', fontWeight: 500 }">
          <el-table-column label="题型" min-width="100" show-overflow-tooltip>
            <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
          </el-table-column>
          <el-table-column prop="wrongQuestionCount" label="错题数" min-width="80" align="center" />
          <el-table-column prop="wrongTimes" label="累计错误" min-width="90" align="center" />
        </el-table>
      </div>

      <div class="wrongbook-section-card">
        <div class="section-header">
          <div class="section-title">按知识点统计</div>
        </div>
        <el-table :data="overview.knowledgePointStats || []" class="custom-table" style="width: 100%" :header-cell-style="{ background: '#fafafa', color: '#606266', fontWeight: 500 }">
          <el-table-column label="知识点" min-width="120" show-overflow-tooltip>
            <template #default="scope">{{ scope.row.knowledgePointName || knowledgePointLabel(scope.row.knowledgePointId) }}</template>
          </el-table-column>
          <el-table-column prop="wrongQuestionCount" label="错题数" min-width="80" align="center" />
          <el-table-column prop="wrongTimes" label="累计错误" min-width="90" align="center" />
        </el-table>
      </div>
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
        <el-form-item label="所属课程">
          <el-select v-model="retryForm.courseId" clearable filterable placeholder="可不选，生成通用回练卷" style="width: 100%">
            <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="retryForm.pickMode === 'smart'" label="知识点">
          <el-select v-model="retryForm.smartKnowledgePointId" clearable filterable placeholder="按知识点筛题，可留空" style="width: 100%">
            <el-option v-for="item in filteredKnowledgePointOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="retryForm.pickMode === 'smart'" label="最少错次"><el-input-number v-model="retryForm.minWrongCount" :min="1" :max="100" /></el-form-item>
        <el-form-item v-if="retryForm.pickMode === 'smart'" label="排序方式">
          <el-select v-model="retryForm.sortMode" style="width: 100%">
            <el-option label="按未掌握优先" value="unmastered_first" />
            <el-option label="按最近错题优先" value="recent_wrong_first" />
          </el-select>
        </el-form-item>
        <el-form-item label="时长"><el-input-number v-model="retryForm.durationMinutes" :min="5" :max="180" /></el-form-item>
        <el-form-item label="生成方式">
          <el-switch v-model="retryForm.savePaper" active-text="保存为专属回练卷" inactive-text="仅预览不保存" />
        </el-form-item>
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
      <div class="retry-result-actions mt20">
        <el-button v-if="retryResult.paperId" type="primary" @click="startRetryExam">开始回练</el-button>
        <el-button v-if="retryResult.paperId" plain @click="goRetryExamHub">前往我的考试</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from '@/utils/feedback'
import { createWrongRetryPaper, fetchPortalCourseOptions, fetchPortalStudentKnowledgePointOptions, getWrongBookOverview, listWrongBook, startExam } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const userStore = usePortalUserStore()
const loading = ref(false)
const wrongs = ref<any[]>([])
const selectedWrong = ref<any>(null)
const overview = ref<any>({})
const courseOptions = ref<any[]>([])
const knowledgePointOptions = ref<any[]>([])
const retryOpen = ref(false)
const retryLoading = ref(false)
const retryResultOpen = ref(false)
const retryResult = ref<any>({})

const retryForm = reactive<any>({
  wrongIds: [] as number[],
  courseId: undefined,
  paperName: '',
  durationMinutes: 45,
  savePaper: true,
  pickMode: 'selected',
  smartKnowledgePointId: undefined,
  minWrongCount: 2,
  sortMode: 'unmastered_first',
})
const retryStarting = ref(false)

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
const filteredKnowledgePointOptions = computed(() =>
  knowledgePointOptions.value.filter((item: any) => !retryForm.courseId || String(item.courseId || '') === String(retryForm.courseId)),
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
  if (!knowledgePointId) return '-'
  const matched = knowledgePointOptions.value.find((item: any) => String(item.value) === String(knowledgePointId))
  return matched?.label || `知识点 ${knowledgePointId}`
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
  retryForm.courseId = selectedWrong.value?.courseId || filters.courseId
  retryForm.wrongIds = mode === 'selected' && selectedWrong.value ? [selectedWrong.value.id] : []
  retryForm.smartKnowledgePointId = selectedWrong.value?.knowledgePointId
  retryForm.savePaper = true
  retryForm.sortMode = 'unmastered_first'
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
      const sorted = [...matched].sort((left: any, right: any) => {
        if (retryForm.sortMode === 'recent_wrong_first') {
          const rightTime = new Date(right?.lastWrongTime || 0).getTime()
          const leftTime = new Date(left?.lastWrongTime || 0).getTime()
          if (rightTime !== leftTime) return rightTime - leftTime
          return Number(right?.wrongCount || 0) - Number(left?.wrongCount || 0)
        }
        const leftMastery = String(left?.masteryStatus || '0') === '0' ? 0 : 1
        const rightMastery = String(right?.masteryStatus || '0') === '0' ? 0 : 1
        if (leftMastery !== rightMastery) return leftMastery - rightMastery
        const wrongDiff = Number(right?.wrongCount || 0) - Number(left?.wrongCount || 0)
        if (wrongDiff !== 0) return wrongDiff
        const rightTime = new Date(right?.lastWrongTime || 0).getTime()
        const leftTime = new Date(left?.lastWrongTime || 0).getTime()
        return rightTime - leftTime
      })
      payload.wrongIds = sorted.map((item: any) => item.id)
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
    ElMessage.success(payload.savePaper ? '已生成专属回练卷' : '已生成回练卷预览')
  } finally {
    retryLoading.value = false
  }
}

function goExamHub() {
  router.push('/student/exams')
}

function goRetryExamHub() {
  if (!retryResult.value?.paperId) {
    goExamHub()
    return
  }
  retryResultOpen.value = false
  router.push(`/student/exams?paperId=${retryResult.value.paperId}`)
}

async function startRetryExam() {
  if (!retryResult.value?.paperId || retryStarting.value) return
  retryStarting.value = true
  try {
    const res = await startExam({ paperId: retryResult.value.paperId })
    const record = res.data || {}
    if (!record.recordId) {
      ElMessage.error('回练启动失败，请稍后重试')
      return
    }
    retryResultOpen.value = false
    router.push({
      path: `/student/exams/session/${record.recordId}`,
      query: {
        paperId: String(retryResult.value.paperId),
        startedAt: String(record.startTime || ''),
      },
    })
  } finally {
    retryStarting.value = false
  }
}

onMounted(async () => {
  const userId = userStore.user?.userId
  courseOptions.value = await fetchPortalCourseOptions(userId)
  knowledgePointOptions.value = await fetchPortalStudentKnowledgePointOptions(userId)
  await getList()
})
</script>

<style scoped>
.wrongbook-page {
  padding: 24px 24px 40px;
}

.portal-page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.portal-page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.portal-page-title-icon {
  width: 4px;
  height: 20px;
  background: var(--brand-primary, #266fcb);
  border-radius: 2px;
}

.wrongbook-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(280px, 0.8fr);
  gap: 20px;
  padding: 32px;
  border-radius: 12px;
  background: linear-gradient(135deg, #fff7ed 0%, #ffffff 100%);
  border: 1px solid #feedd6;
  margin-bottom: 24px;
}

.hero-content {
  display: flex;
  flex-direction: column;
}

.hero-eyebrow {
  display: inline-flex;
  align-self: flex-start;
  padding: 4px 12px;
  border-radius: 4px;
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 12px;
}

.hero-title {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 16px;
  line-height: 1.3;
}

.hero-desc {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
  max-width: 800px;
  margin: 0;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.hero-metric {
  background: #ffffff;
  padding: 24px 20px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
  transition: transform 0.3s ease;
}

.hero-metric:hover {
  transform: translateY(-2px);
}

.hero-metric span {
  color: #909399;
  font-size: 14px;
  margin-bottom: 12px;
}

.hero-metric strong {
  font-size: 32px;
  color: #e6a23c;
  font-weight: 600;
  line-height: 1;
}

.wrongbook-kpis {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.wrongbook-kpi-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  padding: 20px;
  display: flex;
  align-items: flex-start;
  transition: all 0.3s ease;
}

.wrongbook-kpi-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.kpi-card-wide {
  grid-column: span 1;
  background: #fafafa;
}

.kpi-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
  height: 100%;
  width: 100%;
}

.kpi-label {
  font-size: 14px;
  color: #606266;
}

.kpi-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.kpi-value.text-primary {
  color: var(--brand-primary, #266fcb);
}

.kpi-value.text-warning {
  color: #e6a23c;
}

.kpi-sub {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.mt-auto {
  margin-top: auto;
}

.wrongbook-workbench {
  display: grid;
  grid-template-columns: minmax(0, 1.7fr) minmax(320px, 0.9fr);
  gap: 20px;
}

.wrongbook-section-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  padding: 24px;
  height: 100%;
}

.wrongbook-section-card--soft {
  background: #fafafa;
  border: 1px solid #e4e7ed;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.wrongbook-filters {
  background: #fafafa;
  padding: 16px 16px 0;
  border-radius: 6px;
  margin-bottom: 20px;
}

.custom-table {
  --el-table-border-color: #ebeef5;
  --el-table-header-bg-color: #fafafa;
}

.wrongbook-detail-block + .wrongbook-detail-block {
  margin-top: 20px;
}

.wrongbook-detail-block__label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.wrongbook-detail-block__value {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
}

.analysis-box {
  background: #fff;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.text-success {
  color: #67c23a;
}

.text-warning {
  color: #e6a23c;
}

.text-danger {
  color: #f56c6c;
}

.wrongbook-detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin: 20px 0;
  padding: 20px;
  background: #fff;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.detail-grid-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-grid-item span {
  font-size: 13px;
  color: #909399;
}

.detail-grid-item strong {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}

.wrongbook-actions {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 16px;
}

.preview-head {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  font-weight: 600;
  background: #fafafa;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
  color: #303133;
  font-size: 15px;
}

.retry-result-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.portal-grid-3 {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

@media (max-width: 1400px) {
  .wrongbook-kpis {
    grid-template-columns: repeat(3, 1fr);
  }
  .kpi-card-wide {
    grid-column: span 2;
  }
}

@media (max-width: 1200px) {
  .wrongbook-workbench {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 960px) {
  .wrongbook-hero {
    grid-template-columns: 1fr;
  }
  .hero-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  .portal-grid-3 {
    grid-template-columns: 1fr;
  }
}
</style>
