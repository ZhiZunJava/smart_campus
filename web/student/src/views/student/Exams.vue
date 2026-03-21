<template>
  <div class="portal-page exam-hub">
    <section class="exam-hero portal-card">
      <div>
        <div class="exam-hero__eyebrow">Assessment Hub</div>
        <div class="exam-hero__title">智能评测中心</div>
        <p class="exam-hero__desc">
          在这里统一查看可参加考试、历史成绩、错题回流和答题反馈。每次测评都会沉淀成你后续练习和诊断的依据。
        </p>
        <div class="exam-hero__actions">
          <el-button type="primary" @click="activeTab = 'papers'">开始新测评</el-button>
          <el-button plain @click="activeTab = 'records'">查看历史记录</el-button>
          <el-button plain @click="goWrongbook">进入错题本</el-button>
        </div>
      </div>
      <div class="exam-hero__stats">
        <div class="exam-hero__metric">
          <span>最佳成绩</span>
          <strong>{{ bestScore }}</strong>
        </div>
        <div class="exam-hero__metric">
          <span>平均得分</span>
          <strong>{{ recordOverview.avgScore || 0 }}</strong>
        </div>
        <div class="exam-hero__metric">
          <span>错题积累</span>
          <strong>{{ wrongs.length }}</strong>
        </div>
      </div>
    </section>

    <section class="portal-kpis">
      <el-card class="portal-card portal-stat-card"><div class="label">可参加考试</div><div class="value">{{ availablePapers.length }}</div><div class="sub">当前开放试卷数量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">考试记录</div><div class="value">{{ records.length }}</div><div class="sub">最近考试与练习记录</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">平均正确率</div><div class="value">{{ recordOverview.avgCorrectRate || 0 }}%</div><div class="sub">自动统计历史提交结果</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">未掌握错题</div><div class="value">{{ unmasteredWrongCount }}</div><div class="sub">建议优先回练与复盘</div></el-card>
    </section>

    <el-card class="portal-card mt20">
      <template #header>
        <div class="exam-tabs-header">
          <span>智能评测工作台</span>
          <el-segmented v-model="activeTab" :options="tabOptions" />
        </div>
      </template>

      <el-alert
        v-if="activeTab === 'papers' && latestOngoingRecord"
        type="warning"
        :closable="false"
        class="exam-ongoing-alert"
      >
        <template #title>
          <div class="exam-ongoing-alert__content">
            <span>
              你有一场未完成考试：{{ latestOngoingRecord.paperName || `试卷 ${latestOngoingRecord.paperId}` }}，
              开始于 {{ formatDateTime(latestOngoingRecord.startTime) }}
            </span>
            <el-button type="warning" plain @click="resumeExam(latestOngoingRecord)">继续作答</el-button>
          </div>
        </template>
      </el-alert>

      <div v-if="activeTab === 'papers'" class="exam-workbench exam-workbench--papers">
        <section class="exam-panel exam-panel--primary">
          <div class="exam-panel__header">
            <div>
              <div class="exam-panel__title">可参加考试</div>
              <div class="exam-panel__subtitle">优先显示已发布试卷，也支持通用试卷。</div>
            </div>
          </div>

          <div class="exam-paper-grid">
            <article v-for="paper in availablePapers" :key="paper.paperId" class="exam-paper-card">
              <div class="exam-paper-card__head">
                <div class="exam-paper-card__title">{{ paper.paperName }}</div>
                <div class="exam-paper-card__badges">
                  <el-tag v-if="ongoingRecordMap[paper.paperId]" type="warning" effect="plain">进行中</el-tag>
                  <el-tag :type="paper.publishStatus === 'published' ? 'success' : 'warning'" effect="plain">
                    {{ publishStatusLabel(paper.publishStatus) }}
                  </el-tag>
                </div>
              </div>
              <div class="exam-paper-card__meta">
                <span>{{ courseLabel(paper.courseId) }}</span>
                <span>{{ paperTypeLabel(paper.paperType) }}</span>
                <span>{{ paper.durationMinutes || 0 }} 分钟</span>
                <span>{{ paper.paperLevel || 'MAIN' }}</span>
              </div>
              <div class="exam-paper-card__stats">
                <span>总分 {{ paper.totalScore || 0 }}</span>
                <span>及格线 {{ paper.passScore || 60 }}</span>
                <span v-if="paper.subPapers?.length">子卷 {{ paper.subPapers.length }}</span>
              </div>
              <div v-if="ongoingRecordMap[paper.paperId]" class="exam-paper-card__resume">
                已有未交卷记录，开始时间 {{ formatDateTime(ongoingRecordMap[paper.paperId]?.startTime) }}
              </div>
              <div class="exam-paper-card__actions">
                <el-button
                  type="primary"
                  :loading="startingPaperId === paper.paperId"
                  :disabled="startingPaperId === paper.paperId"
                  @click="beginExam(paper)"
                >
                  {{ ongoingRecordMap[paper.paperId] ? '继续考试' : '开始考试' }}
                </el-button>
                <el-button
                  link
                  type="primary"
                  :disabled="Boolean(ongoingRecordMap[paper.paperId])"
                  @click="previewPaper(paper)"
                >
                  预览
                </el-button>
              </div>
            </article>
          </div>
          <el-empty v-if="!loadingPaper && !availablePapers.length" description="暂无可参加考试" />
        </section>

        <section class="exam-panel exam-panel--soft exam-panel--side">
          <div class="exam-panel__header">
            <div>
              <div class="exam-panel__title">进行中考试</div>
              <div class="exam-panel__subtitle">继续作答前可快速确认当前子卷状态与进度。</div>
            </div>
          </div>
          <div v-if="ongoingRecords.length" class="exam-runtime-list">
            <article v-for="item in ongoingRecords" :key="item.recordId" class="exam-runtime-card">
              <div class="exam-runtime-card__title">{{ item.paperName || `试卷 ${item.paperId}` }}</div>
              <div class="exam-runtime-card__meta">
                <span>开始于 {{ formatDateTime(item.startTime) }}</span>
                <span>状态 {{ examStatusLabel(item.examStatus) }}</span>
              </div>
              <div class="exam-runtime-card__stats">
                <span>切屏 {{ item.session?.focusLossCount || 0 }}</span>
                <span>异常 {{ item.session?.flaggedCount || 0 }}</span>
                <span>子卷 {{ item.subPaperStats?.length || 0 }}</span>
              </div>
              <div class="exam-runtime-card__actions">
                <el-button type="warning" plain size="small" @click="resumeExam(item)">继续作答</el-button>
              </div>
            </article>
          </div>
          <el-empty v-else description="当前没有进行中的考试" />
        </section>
      </div>

      <div v-else-if="activeTab === 'records'" class="exam-workbench exam-workbench--records">
        <section class="exam-panel exam-panel--primary">
          <div class="exam-panel__header">
            <div>
              <div class="exam-panel__title">考试记录</div>
              <div class="exam-panel__subtitle">点击一条记录，可查看详细作答和标准答案。</div>
            </div>
          </div>
          <el-table v-loading="loadingRecord" :data="records" @row-click="viewRecordDetail">
            <el-table-column prop="paperName" label="试卷名称" min-width="180" show-overflow-tooltip />
            <el-table-column label="课程" min-width="160" show-overflow-tooltip>
              <template #default="scope">{{ courseLabel(scope.row.courseId) }}</template>
            </el-table-column>
            <el-table-column prop="score" label="得分" width="100" />
            <el-table-column prop="correctRate" label="正确率" width="100" />
            <el-table-column label="状态" width="110">
              <template #default="scope">
                <el-tag :type="scope.row.examStatus === 'SUBMITTED' ? 'success' : 'warning'">{{ examStatusLabel(scope.row.examStatus) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="分层情况" min-width="180" show-overflow-tooltip>
              <template #default="scope">
                {{ scope.row.subPaperStats?.length ? `子卷 ${scope.row.subPaperStats.length} 个` : '单层试卷' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button
                  v-if="scope.row.examStatus === 'ONGOING'"
                  link
                  type="warning"
                  @click.stop="resumeExam(scope.row)"
                >
                  继续考试
                </el-button>
                <el-button v-else link type="primary" @click.stop="viewRecordDetail(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </section>

        <section class="exam-panel exam-panel--soft exam-panel--side">
          <div class="exam-panel__header">
            <div>
              <div class="exam-panel__title">子卷统计</div>
              <div class="exam-panel__subtitle">分层考试会在这里统计每个子卷的提交情况与得分表现。</div>
            </div>
          </div>
          <el-table :data="recordOverview.subPaperStats || []" size="small" border>
            <el-table-column prop="paperName" label="子试卷" min-width="160" show-overflow-tooltip />
            <el-table-column prop="submittedCount" label="已提交" width="90" />
            <el-table-column prop="skippedCount" label="跳过" width="90" />
            <el-table-column prop="avgScore" label="平均得分" width="110" />
            <el-table-column prop="avgCorrectRate" label="平均正确率" width="120" />
          </el-table>
        </section>
      </div>

      <div v-else class="exam-workbench exam-workbench--wrongs">
        <section class="exam-panel exam-panel--primary">
          <div class="exam-panel__header">
            <div>
              <div class="exam-panel__title">错题摘要</div>
              <div class="exam-panel__subtitle">错题会自动回流到这里，支持后续进入错题本继续回练。</div>
            </div>
          </div>
          <el-table :data="wrongs" size="small" border>
            <el-table-column prop="questionId" label="题目ID" width="90" />
            <el-table-column label="课程" min-width="160"><template #default="scope">{{ courseLabel(scope.row.courseId) }}</template></el-table-column>
            <el-table-column label="知识点" min-width="160"><template #default="scope">{{ knowledgePointLabel(scope.row.knowledgePointId) }}</template></el-table-column>
            <el-table-column prop="wrongCount" label="错误次数" width="100" />
            <el-table-column label="掌握状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.masteryStatus === '1' ? 'success' : 'warning'">{{ scope.row.masteryStatus === '1' ? '已掌握' : '未掌握' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </section>

        <section class="exam-panel exam-panel--soft exam-panel--side">
          <div class="exam-panel__header">
            <div>
              <div class="exam-panel__title">按知识点统计</div>
              <div class="exam-panel__subtitle">帮助你快速聚焦薄弱知识点。</div>
            </div>
          </div>
          <el-table :data="recordOverview.knowledgePointStats || []" size="small" border>
            <el-table-column label="知识点" min-width="160">
              <template #default="scope">{{ scope.row.knowledgePointName || knowledgePointLabel(scope.row.knowledgePointId) }}</template>
            </el-table-column>
            <el-table-column prop="totalCount" label="总题数" width="100" />
            <el-table-column prop="correctCount" label="答对数" width="100" />
          </el-table>
        </section>
      </div>
    </el-card>

    <el-dialog v-model="previewOpen" :title="previewPaperData.paperName || '试卷预览'" width="980px" top="4vh">
      <div class="preview-head">
        <span>{{ courseLabel(previewPaperData.courseId) }}</span>
        <span>{{ previewPaperData.durationMinutes || 0 }} 分钟</span>
        <span>总分 {{ previewPaperData.totalScore || 0 }}</span>
      </div>
      <el-table :data="previewPaperData.questions || []" border class="mt20">
        <el-table-column prop="sortNo" label="序号" width="90" />
        <el-table-column label="题型" width="110">
          <template #default="scope">{{ questionTypeLabel(scope.row.question?.questionType) }}</template>
        </el-table-column>
        <el-table-column label="题干" min-width="360" show-overflow-tooltip>
          <template #default="scope">{{ stripHtml(scope.row.question?.stem) }}</template>
        </el-table-column>
        <el-table-column prop="score" label="分值" width="90" />
      </el-table>
    </el-dialog>

    <el-dialog v-model="recordDetailOpen" title="作答详情" width="1120px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="试卷">{{ currentRecordDetail.record?.paperName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="得分">{{ currentRecordDetail.record?.score || 0 }}</el-descriptions-item>
        <el-descriptions-item label="正确率">{{ currentRecordDetail.record?.correctRate || 0 }}%</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentRecordDetail.record?.examStatus || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="currentRecordDetail.subPaperStats || []" border class="mt20">
        <el-table-column prop="paperName" label="子试卷" min-width="180" />
        <el-table-column prop="paperLevel" label="层级" width="100" />
        <el-table-column prop="answerMode" label="模式" width="100" />
        <el-table-column prop="submittedCount" label="已提交" width="90" />
        <el-table-column prop="skippedCount" label="跳过" width="90" />
        <el-table-column prop="avgScore" label="平均得分" width="110" />
        <el-table-column prop="avgCorrectRate" label="平均正确率" width="120" />
      </el-table>
      <el-collapse class="mt20">
        <el-collapse-item
          v-for="group in groupedRecordAnswers"
          :key="group.questionType"
          :title="`${questionTypeLabel(group.questionType)} ｜ ${group.answers.length} 题 ｜ 正确 ${group.correctCount} 题`"
        >
          <el-table :data="group.answers" border>
            <el-table-column prop="questionId" label="题目ID" width="90" />
            <el-table-column label="题干" min-width="280" show-overflow-tooltip>
              <template #default="scope">{{ stripHtml(scope.row.stem) }}</template>
            </el-table-column>
            <el-table-column prop="userAnswer" label="我的答案" min-width="180" show-overflow-tooltip />
            <el-table-column prop="standardAnswer" label="标准答案" min-width="180" show-overflow-tooltip />
            <el-table-column prop="analysis" label="解析" min-width="220" show-overflow-tooltip />
            <el-table-column label="结果" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isCorrect === '1' ? 'success' : 'danger'">{{ scope.row.isCorrect === '1' ? '正确' : '错误' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="score" label="得分" width="90" />
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from '@/utils/feedback'
import {
  fetchPortalCourseOptions,
  fetchPortalStudentKnowledgePointOptions,
  getExamRecordDetail,
  getExamRecordOverview,
  getExamRuntime,
  getPaperDetail,
  listExamPaper,
  listExamRecord,
  listWrongBook,
  startExam,
} from '@/api/portal'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = usePortalUserStore()
const loadingPaper = ref(false)
const loadingRecord = ref(false)
const previewOpen = ref(false)
const recordDetailOpen = ref(false)
const activeTab = ref('papers')
const startingPaperId = ref<number | null>(null)
const papers = ref<any[]>([])
const records = ref<any[]>([])
const wrongs = ref<any[]>([])
const courseOptions = ref<any[]>([])
const knowledgePointOptions = ref<any[]>([])
const previewPaperData = ref<any>({})
const currentRecordDetail = ref<any>({})
const recordOverview = ref<any>({})

const tabOptions = [
  { label: '可参加考试', value: 'papers' },
  { label: '考试记录', value: 'records' },
  { label: '错题回流', value: 'wrongs' },
]

const availablePapers = computed(() =>
  papers.value.filter((item: any) => {
    if (item.status !== '0') return false
    if (item.publishStatus && item.publishStatus !== 'published') return false
    const now = Date.now()
    const start = item.publishStartTime ? new Date(item.publishStartTime).getTime() : null
    const end = item.publishEndTime ? new Date(item.publishEndTime).getTime() : null
    if (start && now < start) return false
    if (end && now > end) return false
    return true
  }),
)

const bestScore = computed(() => {
  const scores = records.value.map((item: any) => Number(item.score || 0))
  return scores.length ? Math.max(...scores) : 0
})

const unmasteredWrongCount = computed(() =>
  wrongs.value.filter((item: any) => item.masteryStatus !== '1').length,
)
const ongoingRecords = computed(() => records.value.filter((item: any) => item.examStatus === 'ONGOING'))
const latestOngoingRecord = computed(() => ongoingRecords.value[0] || null)
const ongoingRecordMap = computed(() =>
  ongoingRecords.value.reduce((acc: Record<string, any>, item: any) => {
    acc[item.paperId] = item
    return acc
  }, {}),
)
const groupedRecordAnswers = computed(() => {
  const groups = new Map<string, { questionType: string; answers: any[]; correctCount: number }>()
  ;(currentRecordDetail.value.answers || []).forEach((item: any) => {
    const key = item.questionType || 'unknown'
    if (!groups.has(key)) {
      groups.set(key, { questionType: key, answers: [], correctCount: 0 })
    }
    const group = groups.get(key)!
    group.answers.push(item)
    if (item.isCorrect === '1') {
      group.correctCount += 1
    }
  })
  return Array.from(groups.values())
})

function stripHtml(value: string) {
  return String(value || '').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

function paperTypeLabel(type: string) {
  return ({ fixed: '固定', random: '随机', adaptive: '自适应' } as any)[type] || type || '-'
}

function publishStatusLabel(status: string) {
  return ({ draft: '草稿', published: '已发布', archived: '已归档' } as any)[status] || status || '草稿'
}

function questionTypeLabel(type: string) {
  return ({ single: '单选题', multiple: '多选题', judge: '判断题', fill: '填空题', essay: '简答题', material: '材料题', case: '案例题' } as any)[type] || type || '-'
}

function examStatusLabel(status: string) {
  return ({ ONGOING: '进行中', SUBMITTED: '已提交' } as any)[status] || status || '-'
}

function formatDateTime(value: string) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString()
}

function courseLabel(courseId: number | string | undefined) {
  if (!courseId) return '通用试卷'
  const matched = courseOptions.value.find((item: any) => String(item.value) === String(courseId))
  return matched?.label || `课程 ${courseId}`
}

function knowledgePointLabel(knowledgePointId: number | string | undefined) {
  if (!knowledgePointId) return '-'
  const matched = knowledgePointOptions.value.find((item: any) => String(item.value) === String(knowledgePointId))
  return matched?.label || `知识点 ${knowledgePointId}`
}

async function loadPapers() {
  loadingPaper.value = true
  try {
    const res = await listExamPaper({ pageNum: 1, pageSize: 20, status: '0' })
    papers.value = res.rows || []
  } finally {
    loadingPaper.value = false
  }
}

async function loadRecords() {
  loadingRecord.value = true
  try {
    const [recordRes, wrongRes, overviewRes] = await Promise.all([
      listExamRecord({ pageNum: 1, pageSize: 20 }),
      listWrongBook({ pageNum: 1, pageSize: 50 }),
      getExamRecordOverview({}),
    ])
    const recordRows = recordRes.rows || []
    const enrichedRows = await Promise.all(
      recordRows.map(async (item: any) => {
        if (item.examStatus !== 'ONGOING') {
          return item
        }
        try {
          const runtimeRes = await getExamRuntime(item.recordId)
          return {
            ...item,
            ...(runtimeRes.data || {}),
          }
        } catch {
          return item
        }
      }),
    )
    records.value = enrichedRows
    wrongs.value = wrongRes.rows || []
    recordOverview.value = overviewRes.data || {}
  } finally {
    loadingRecord.value = false
  }
}

async function previewPaper(row: any) {
  if (ongoingRecordMap.value[row.paperId]) {
    ElMessage.warning('该试卷已有未完成考试，请直接继续作答')
    return
  }
  const res = await getPaperDetail(row.paperId)
  previewPaperData.value = res.data || {}
  previewOpen.value = true
}

async function beginExam(row: any) {
  if (startingPaperId.value === row.paperId) return
  startingPaperId.value = row.paperId
  try {
    const startRes = await startExam({ paperId: row.paperId })
    const record = startRes.data || {}
    if (!record.recordId) {
      ElMessage.error('考试启动失败，请稍后重试')
      return
    }
    userStore.setOngoingExam(record)
    router.push({
      path: `/student/exams/session/${record.recordId}`,
      query: {
        paperId: String(row.paperId),
        startedAt: String(record.startTime || ''),
      },
    })
  } finally {
    startingPaperId.value = null
  }
}

function resumeExam(row: any) {
  router.push({
    path: `/student/exams/session/${row.recordId}`,
    query: {
      paperId: String(row.paperId),
      startedAt: String(row.startTime || ''),
    },
  })
}

async function viewRecordDetail(row: any) {
  const [recordRes, paperRes] = await Promise.all([
    getExamRecordDetail(row.recordId),
    getPaperDetail(row.paperId),
  ])
  const recordData = recordRes.data || {}
  const paperData = paperRes.data || {}
  const allowReviewAnalysis = paperData.allowReviewAnalysis === '1'
  currentRecordDetail.value = {
    ...recordData,
    paper: paperData,
    allowReviewAnalysis,
    subPaperStats: recordData.subPaperStats || [],
    answers: (recordData.answers || []).map((item: any) => ({
      ...item,
      standardAnswer: allowReviewAnalysis ? item.standardAnswer : '当前试卷未开放标准答案回看',
      analysis: allowReviewAnalysis ? item.analysis : '当前试卷未开放解析回看',
    })),
  }
  recordDetailOpen.value = true
}

function goWrongbook() {
  router.push('/student/wrongbook')
}

onMounted(async () => {
  if (typeof route.query.tab === 'string' && tabOptions.some((item) => item.value === route.query.tab)) {
    activeTab.value = route.query.tab
  }
  const userId = userStore.user?.userId
  const [courseRes, kpRes] = await Promise.allSettled([
    fetchPortalCourseOptions(userId),
    fetchPortalStudentKnowledgePointOptions(userId),
  ])
  courseOptions.value = courseRes.status === 'fulfilled' ? (courseRes.value || []) : []
  knowledgePointOptions.value = kpRes.status === 'fulfilled' ? (kpRes.value || []) : []
  await Promise.all([loadPapers(), loadRecords()])
  const previewPaperId = Number(route.query.paperId || 0)
  if (previewPaperId) {
    const matchedPaper = papers.value.find((item: any) => Number(item.paperId) === previewPaperId)
    if (matchedPaper && !ongoingRecordMap.value[matchedPaper.paperId]) {
      activeTab.value = 'papers'
      await previewPaper(matchedPaper)
    }
  }
  if (route.query.resume === '1' && latestOngoingRecord.value) {
    resumeExam(latestOngoingRecord.value)
  }
})
</script>

<style scoped>
.exam-hub .portal-card {
  overflow: hidden;
}
.exam-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(280px, 0.8fr);
  gap: 18px;
  padding: 16px 18px;
  background: #fff;
  border: 1px solid #d7dee9;
  border-radius: 6px;
}
.exam-hero__eyebrow {
  display: inline-flex;
  padding: 2px 8px;
  border-radius: 4px;
  background: #eef4ff;
  color: #315fca;
  font-size: 12px;
  font-weight: 700;
}
.exam-hero__title {
  margin-top: 12px;
  font-size: 30px;
  font-weight: 800;
  color: var(--portal-text);
}
.exam-hero__desc {
  margin-top: 10px;
  max-width: 760px;
  font-size: 14px;
  line-height: 1.9;
  color: var(--portal-text-secondary);
}
.exam-hero__actions {
  margin-top: 18px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.exam-hero__stats {
  display: grid;
  gap: 14px;
}
.exam-hero__metric {
  padding: 14px;
  border-radius: 4px;
  background: #fafbfd;
  border: 1px solid #d7dee9;
}
.exam-hero__metric span {
  color: var(--portal-text-secondary);
  font-size: 13px;
}
.exam-hero__metric strong {
  display: block;
  margin-top: 8px;
  font-size: 30px;
  color: var(--portal-brand);
}
.exam-tabs-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}
.exam-workbench {
  display: grid;
  grid-template-columns: minmax(0, 1.7fr) minmax(320px, 0.9fr);
  gap: 16px;
}
.exam-panel {
  padding: 16px;
  border-radius: 6px;
  background: #fff;
  border: 1px solid #d7dee9;
}
.exam-panel--primary {
  min-width: 0;
}
.exam-panel--side {
  align-self: start;
}
.exam-ongoing-alert {
  margin-bottom: 14px;
}
.exam-ongoing-alert__content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.exam-panel--soft {
  background: #fafbfd;
}
.exam-panel__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 14px;
}
.exam-panel__title {
  font-size: 16px;
  font-weight: 800;
  color: var(--portal-text);
}
.exam-panel__subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: var(--portal-text-secondary);
}
.exam-paper-grid {
  display: grid;
  gap: 14px;
}
.exam-paper-card {
  padding: 16px;
  border-radius: 6px;
  background: #fff;
  border: 1px solid #d7dee9;
}
.exam-paper-card__head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}
.exam-paper-card__badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}
.exam-paper-card__title {
  font-size: 16px;
  font-weight: 800;
  color: var(--portal-text);
  line-height: 1.6;
}
.exam-paper-card__meta,
.exam-paper-card__stats {
  margin-top: 10px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  color: var(--portal-text-secondary);
  font-size: 12px;
}
.exam-paper-card__actions {
  margin-top: 14px;
  display: flex;
  gap: 12px;
  align-items: center;
}
.exam-paper-card__resume {
  margin-top: 10px;
  font-size: 12px;
  color: #d97706;
}
.exam-runtime-list {
  display: grid;
  gap: 12px;
}
.exam-runtime-card {
  padding: 14px;
  border-radius: 6px;
  background: #fff;
  border: 1px solid #d7dee9;
}
.exam-runtime-card__title {
  font-size: 15px;
  font-weight: 700;
  color: var(--portal-text);
}
.exam-runtime-card__meta,
.exam-runtime-card__stats {
  margin-top: 8px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  color: var(--portal-text-secondary);
  font-size: 12px;
}
.exam-runtime-card__actions {
  margin-top: 10px;
}
@media (max-width: 1100px) {
  .exam-workbench {
    grid-template-columns: 1fr;
  }
}
.preview-head,
.exam-dialog-head {
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
  font-weight: 600;
}
.exam-dialog-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 72vh;
  overflow: auto;
}
.exam-question-card {
  padding: 18px;
  border: 1px solid var(--portal-border);
  border-radius: 6px;
  background: #fff;
}
.exam-question-card__top {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}
.exam-question-title {
  font-size: 16px;
  font-weight: 700;
  line-height: 1.8;
}
.exam-question-score {
  color: var(--portal-text-secondary);
  font-size: 13px;
  white-space: nowrap;
}
.exam-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 12px;
}
@media (max-width: 960px) {
  .exam-hero {
    grid-template-columns: 1fr;
  }
  .exam-question-card__top {
    flex-direction: column;
  }
}
</style>
