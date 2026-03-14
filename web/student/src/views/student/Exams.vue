<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>我的考试</h3>
      <el-tag type="primary">支持在线作答、成绩回看与错题回流</el-tag>
    </div>

    <section class="portal-kpis" style="margin-top: 0; margin-bottom: 18px;">
      <el-card class="portal-card portal-stat-card"><div class="label">可参加考试</div><div class="value">{{ papers.length }}</div><div class="sub">当前开放试卷数量</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">考试记录</div><div class="value">{{ records.length }}</div><div class="sub">已保存考试记录</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">错题数量</div><div class="value">{{ wrongs.length }}</div><div class="sub">来自考试后的错题积累</div></el-card>
      <el-card class="portal-card portal-stat-card"><div class="label">最高分</div><div class="value">{{ bestScore }}</div><div class="sub">当前考试最好成绩</div></el-card>
    </section>

    <div class="portal-grid portal-grid-2">
      <el-card class="portal-card">
        <template #header><span>可参加考试</span></template>
        <el-table v-loading="loadingPaper" :data="papers">
          <el-table-column prop="paperName" label="试卷名称" min-width="180" />
          <el-table-column prop="paperType" label="类型" width="100" />
          <el-table-column prop="durationMinutes" label="时长" width="90" />
          <el-table-column prop="totalScore" label="总分" width="90" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button link type="primary" @click="beginExam(scope.row)">开始考试</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loadingPaper && papers.length === 0" description="暂无可参加考试" />
      </el-card>

      <el-card class="portal-card">
        <template #header><span>考试记录</span></template>
        <el-table v-loading="loadingRecord" :data="records">
          <el-table-column prop="recordId" label="记录ID" width="90" />
          <el-table-column prop="paperId" label="试卷ID" width="90" />
          <el-table-column prop="score" label="得分" width="100" />
          <el-table-column prop="correctRate" label="正确率" width="100" />
          <el-table-column label="状态" width="110">
            <template #default="scope">
              <el-tag :type="scope.row.examStatus === 'SUBMITTED' ? 'success' : 'warning'">{{ scope.row.examStatus }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button link type="primary" @click="viewAnswer(scope.row)">查看答案</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <div class="portal-grid portal-grid-2 mt20">
      <el-card class="portal-card portal-soft-card">
        <template #header><span>作答详情</span></template>
        <el-table :data="answers">
          <el-table-column prop="questionId" label="题目ID" width="90" />
          <el-table-column prop="userAnswer" label="用户答案" min-width="200" show-overflow-tooltip />
          <el-table-column label="结果" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.isCorrect === '1' ? 'success' : 'danger'">{{ scope.row.isCorrect === '1' ? '正确' : '错误' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分" width="90" />
        </el-table>
        <el-empty v-if="answers.length === 0" description="请选择一条考试记录查看答案" />
      </el-card>

      <el-card class="portal-card portal-soft-card">
        <template #header><span>错题本摘要</span></template>
        <el-table :data="wrongs">
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
        <el-empty v-if="wrongs.length === 0" description="暂无错题数据" />
      </el-card>
    </div>

    <el-dialog v-model="examOpen" :title="currentPaper.paperName || '在线考试'" width="960px" top="4vh">
      <div class="exam-dialog-body" v-if="currentPaper.questions?.length">
        <div class="portal-surface">本次考试共 {{ currentPaper.questions.length }} 题，请认真作答后提交。</div>
        <div v-for="item in currentPaper.questions" :key="item.questionId" class="exam-question-card">
          <div class="exam-question-title">{{ item.sortNo }}. {{ item.question?.stem }}</div>
          <div class="exam-question-score">分值：{{ item.score }}</div>
          <el-radio-group v-if="item.question?.questionType === 'single'" v-model="answerMap[item.questionId]" class="exam-options">
            <el-radio v-for="opt in item.question?.options || []" :key="opt.optionId" :label="opt.optionKey">{{ opt.optionKey }}. {{ opt.optionContent }}</el-radio>
          </el-radio-group>
          <el-checkbox-group v-else-if="item.question?.questionType === 'multiple'" v-model="multiAnswerMap[item.questionId]" class="exam-options">
            <el-checkbox v-for="opt in item.question?.options || []" :key="opt.optionId" :label="opt.optionKey">{{ opt.optionKey }}. {{ opt.optionContent }}</el-checkbox>
          </el-checkbox-group>
          <el-input v-else v-model="answerMap[item.questionId]" type="textarea" rows="3" placeholder="请输入答案" />
        </div>
      </div>
      <template #footer>
        <el-button @click="examOpen = false">取消</el-button>
        <el-button type="primary" @click="submitCurrentExam">提交考试</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getPaperDetail, listExamAnswer, listExamPaper, listExamRecord, listWrongBook, startExam, submitExam } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()
const loadingPaper = ref(false)
const loadingRecord = ref(false)
const examOpen = ref(false)
const papers = ref<any[]>([])
const records = ref<any[]>([])
const answers = ref<any[]>([])
const wrongs = ref<any[]>([])
const currentPaper = ref<any>({})
const currentRecordId = ref<number | undefined>()
const answerMap = ref<Record<string, any>>({})
const multiAnswerMap = ref<Record<string, string[]>>({})

const bestScore = computed(() => {
  const scores = records.value.map((item: any) => Number(item.score || 0))
  return scores.length ? Math.max(...scores) : 0
})

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
  const userId = userStore.user?.userId
  if (!userId) return
  loadingRecord.value = true
  try {
    const [recordRes, wrongRes] = await Promise.all([
      listExamRecord({ pageNum: 1, pageSize: 20, userId }),
      listWrongBook({ pageNum: 1, pageSize: 50, userId }),
    ])
    records.value = recordRes.rows || []
    wrongs.value = wrongRes.rows || []
  } finally {
    loadingRecord.value = false
  }
}

async function beginExam(row: any) {
  const userId = userStore.user?.userId
  if (!userId) return
  const startRes = await startExam({ paperId: row.paperId, userId })
  currentRecordId.value = startRes.data?.recordId
  const res = await getPaperDetail(row.paperId)
  currentPaper.value = res.data || {}
  answerMap.value = {}
  multiAnswerMap.value = {}
  examOpen.value = true
}

async function submitCurrentExam() {
  if (!currentRecordId.value) return
  const answersPayload = (currentPaper.value.questions || []).map((item: any) => ({
    questionId: item.questionId,
    userAnswer:
      item.question?.questionType === 'multiple'
        ? (multiAnswerMap.value[item.questionId] || []).join(',')
        : (answerMap.value[item.questionId] || ''),
  }))
  await submitExam({ recordId: currentRecordId.value, answers: answersPayload })
  ElMessage.success('考试提交成功')
  examOpen.value = false
  await loadRecords()
}

async function viewAnswer(row: any) {
  const res = await listExamAnswer({ pageNum: 1, pageSize: 100, recordId: row.recordId })
  answers.value = res.rows || []
}

onMounted(async () => {
  await Promise.all([loadPapers(), loadRecords()])
})
</script>

<style scoped>
.exam-dialog-body { display: flex; flex-direction: column; gap: 16px; max-height: 72vh; overflow: auto; }
.exam-question-card { padding: 18px; border: 1px solid var(--portal-border); border-radius: 18px; background: #fff; }
.exam-question-title { font-size: 16px; font-weight: 700; line-height: 1.8; }
.exam-question-score { margin-top: 8px; margin-bottom: 12px; color: var(--portal-text-secondary); font-size: 13px; }
.exam-options { display: flex; flex-direction: column; gap: 10px; }
</style>
