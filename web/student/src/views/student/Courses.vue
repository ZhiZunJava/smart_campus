<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>我的课程</h3>
    </div>

    <section class="course-overview">
      <el-card class="portal-card course-overview__hero">
        <div class="course-overview__hero-copy">
          <span class="course-overview__eyebrow">课程总览</span>
          <h4>{{ summaryTermText }}</h4>
          <p>当前学期共 {{ courseList.length }} 门课程，建议优先查看已排课课程与本周上课安排。</p>
          <div class="course-overview__hero-tags">
            <span class="course-overview__tag">总学分 {{ totalCredits }}</span>
            <span class="course-overview__tag">周学时 {{ totalHours }}</span>
            <span class="course-overview__tag">学科 {{ subjectCount }}</span>
          </div>
        </div>
        <div class="course-overview__hero-stats">
          <div class="course-overview__hero-stat">
            <span>已排课程</span>
            <strong>{{ scheduledCourseCount }}</strong>
          </div>
          <div class="course-overview__hero-stat">
            <span>待补排课程</span>
            <strong>{{ unscheduledCourseCount }}</strong>
          </div>
        </div>
      </el-card>

      <div class="course-overview__metrics">
        <el-card class="portal-card course-metric-card course-metric-card--courses">
          <div class="course-metric-card__icon"><i class="ri-book-open-line"></i></div>
          <div class="course-metric-card__body">
            <div class="course-metric-card__label">课程数</div>
            <div class="course-metric-card__value">{{ courseList.length }}</div>
            <div class="course-metric-card__sub">当前学期修读课程</div>
          </div>
        </el-card>

        <el-card class="portal-card course-metric-card course-metric-card--hours">
          <div class="course-metric-card__icon"><i class="ri-time-line"></i></div>
          <div class="course-metric-card__body">
            <div class="course-metric-card__label">总周学时</div>
            <div class="course-metric-card__value">{{ totalHours }}</div>
            <div class="course-metric-card__sub">按班级课程累计</div>
          </div>
        </el-card>

        <el-card class="portal-card course-metric-card course-metric-card--subjects">
          <div class="course-metric-card__icon"><i class="ri-layout-grid-line"></i></div>
          <div class="course-metric-card__body">
            <div class="course-metric-card__label">学科数</div>
            <div class="course-metric-card__value">{{ subjectCount }}</div>
            <div class="course-metric-card__sub">覆盖课程类型</div>
          </div>
        </el-card>
      </div>
    </section>

    <el-card class="portal-card course-list-card">
      <div class="course-filter">
        <el-select v-model="queryParams.termId" filterable clearable placeholder="选择学期" style="width: 240px" @change="loadCourses">
          <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="keyword" clearable placeholder="搜索课程名称或课程编码" style="width: 280px" />
      </div>

      <div class="course-summary-bar">
        <div class="course-summary-bar__term">{{ summaryTermText }}</div>
        <div class="course-summary-bar__credit">总学分：{{ totalCredits }}</div>
      </div>

      <div class="course-sheet">
        <div class="course-sheet__head">
          <div>课程信息</div>
          <div>教学班</div>
          <div>时间地点人员</div>
          <div>人数</div>
          <div>教学材料</div>
        </div>

        <div v-for="row in filteredCourses" :key="`course-${row.id || row.courseId}`" class="course-sheet__row">
          <div class="course-sheet__cell course-sheet__cell--course">
            <button type="button" class="course-title-link" @click="openCourseDetail(row)">{{ row.courseName }}</button>
            <div class="course-meta-line">
              <span><i class="ri-barcode-box-line"></i>{{ row.courseCode || '-' }}</span>
              <span><i class="ri-building-4-line"></i>{{ row.openDeptName || '-' }}</span>
              <span><i class="ri-book-shelf-line"></i>{{ row.courseCategory || row.subjectType || '-' }}</span>
            </div>
            <div class="course-meta-line">
              <span><i class="ri-award-line"></i>{{ row.credits ?? '-' }}学分</span>
              <span><i class="ri-time-line"></i>{{ row.totalHours ?? '-' }}学时</span>
              <span><i class="ri-graduation-cap-line"></i>{{ row.major || '-' }}</span>
              <span><i class="ri-translate-2"></i>{{ row.teachingLanguage || '-' }}</span>
              <span><i class="ri-file-list-3-line"></i>{{ row.assessmentType || '-' }}</span>
            </div>
            <div v-if="row.remark" class="course-remark">备注：{{ row.remark }}</div>
          </div>

          <div class="course-sheet__cell">
            <div class="course-stack-line"><i class="ri-hashtag"></i>{{ row.teachingClassCode || '-' }}</div>
            <div class="course-stack-line muted"><i class="ri-team-line"></i>{{ row.className || '-' }}</div>
          </div>

          <div class="course-sheet__cell course-sheet__cell--schedule">
            <template v-if="(row.scheduleDetails || []).length">
              <div v-for="(item, index) in summarizeSchedules(row.scheduleDetails)" :key="`schedule-${index}`" class="schedule-line">
                <i class="ri-calendar-schedule-line"></i>
                <span>{{ item }}</span>
              </div>
            </template>
            <span v-else class="muted">无排课数据</span>
          </div>

          <div class="course-sheet__cell">
            <div class="course-count-list">
              <div class="course-count-item"><i class="ri-user-line"></i><span>实际：{{ row.actualStudentCount ?? '-' }}</span></div>
              <div class="course-count-item"><i class="ri-group-line"></i><span>上限：{{ row.studentLimit ?? '-' }}</span></div>
            </div>
          </div>

          <div class="course-sheet__cell course-sheet__cell--material">
            <span class="material-pill"><i class="ri-file-paper-2-line"></i>教学大纲</span>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && !filteredCourses.length" description="当前学期暂无课程" />
    </el-card>

    <el-dialog v-model="detailOpen" :title="currentCourse?.courseName || '任务详情'" width="1280px" top="4vh" class="course-detail-dialog">
      <div class="course-detail-header">
          <div class="course-detail-header__meta">
            <span><i class="ri-barcode-box-line"></i>{{ currentCourse?.courseCode || '-' }}</span>
            <span><i class="ri-hashtag"></i>{{ currentCourse?.teachingClassCode || '-' }}</span>
            <span><i class="ri-book-shelf-line"></i>{{ currentCourse?.courseCategory || '-' }}</span>
          </div>
        </div>
      <el-tabs v-model="detailTab">
        <el-tab-pane label="基本信息" name="basic">
          <div class="detail-grid">
            <div class="detail-row"><label>所属业务类型</label><span>{{ currentCourse?.businessType || '-' }}</span><label>所属学期</label><span>{{ currentCourse?.termName || '-' }}</span></div>
            <div class="detail-row"><label>课程代码</label><span>{{ currentCourse?.courseCode || '-' }}</span><label>课程名称</label><span>{{ currentCourse?.courseName || '-' }}</span></div>
            <div class="detail-row"><label>教学班代码</label><span>{{ currentCourse?.teachingClassCode || '-' }}</span><label>学分</label><span>{{ currentCourse?.credits ?? '-' }}</span></div>
            <div class="detail-row detail-row--full"><label>教学班名称</label><span>{{ currentCourse?.className || '-' }}</span></div>
            <div class="detail-row"><label>课程类别</label><span>{{ currentCourse?.courseCategory || '-' }}</span><label>开课部门</label><span>{{ currentCourse?.openDeptName || '-' }}</span></div>
            <div class="detail-row"><label>考核方式</label><span>{{ currentCourse?.assessmentType || '-' }}</span><label>授课校区</label><span>{{ currentCourse?.campusName || '-' }}</span></div>
            <div class="detail-row"><label>授课语言</label><span>{{ currentCourse?.teachingLanguage || '-' }}</span><label>先修课程</label><span>{{ currentCourse?.prerequisiteCourse || '-' }}</span></div>
            <div class="detail-row"><label>任务类型</label><span>{{ currentCourse?.taskType || '-' }}</span><label>是否必修</label><span>{{ requiredLabel(currentCourse?.requiredFlag) }}</span></div>
            <div class="detail-row"><label>课程等级要求</label><span>{{ currentCourse?.courseLevelRequirement || '-' }}</span><label>要求总课时</label><span>{{ currentCourse?.totalHours ?? '-' }}</span></div>
            <div class="detail-row"><label>要求周数</label><span>{{ currentCourse?.requiredWeeks ?? '-' }}</span><label>要求周课时</label><span>{{ currentCourse?.weeklyHours ?? '-' }}</span></div>
            <div class="detail-row"><label>已安排总课时</label><span>{{ currentCourse?.arrangedHours ?? '-' }}</span><label>选课上限</label><span>{{ currentCourse?.studentLimit ?? '-' }}</span></div>
            <div class="detail-row"><label>已选学生数</label><span>{{ currentCourse?.actualStudentCount ?? '-' }}</span><label>备注</label><span>{{ currentCourse?.remark || '无' }}</span></div>
          </div>
          <div class="course-detail-intro">
            <div class="course-detail-intro__label">课程简介</div>
            <div class="course-detail-intro__content">{{ currentCourse?.intro || '暂无课程简介' }}</div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="排课信息" name="schedule">
          <el-table :data="currentCourse?.scheduleDetails || []" max-height="520">
            <el-table-column label="日期" prop="date" width="120" />
            <el-table-column label="教室" prop="classroom" min-width="260" show-overflow-tooltip />
            <el-table-column label="老师" prop="teacherName" min-width="120" />
            <el-table-column label="星期几" prop="weekLabel" width="100" />
            <el-table-column label="开始时间" prop="startTime" width="110" />
            <el-table-column label="结束时间" prop="endTime" width="110" />
            <el-table-column label="学时信息" prop="hours" width="90" />
          </el-table>
          <el-empty v-if="!(currentCourse?.scheduleDetails || []).length" description="当前课程暂无排课信息" />
        </el-tab-pane>
        <el-tab-pane :label="`课程考试（${courseDetail.stats?.paperCount || 0}）`" name="papers">
          <div class="course-tab-hint">仅展示当前课程已配置并开放到课程维度的考试。</div>
          <el-table :data="courseDetail.papers || []" max-height="520">
            <el-table-column prop="paperName" label="试卷名称" min-width="220" show-overflow-tooltip />
            <el-table-column label="类型" width="100">
              <template #default="scope">{{ paperTypeLabel(scope.row.paperType) }}</template>
            </el-table-column>
            <el-table-column prop="durationMinutes" label="时长" width="90" />
            <el-table-column prop="totalScore" label="总分" width="90" />
            <el-table-column label="状态" width="100">
              <template #default="scope">{{ publishStatusLabel(scope.row.publishStatus) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button link type="primary" @click="previewCoursePaper(scope.row)">预览试卷</el-button>
                <el-button link type="success" @click="goExamHub(scope.row)">去考试中心</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!(courseDetail.papers || []).length" description="当前课程暂未配置考试，所以这里为空" />
        </el-tab-pane>
        <el-tab-pane :label="`课程题库（${courseDetail.stats?.questionCount || 0}）`" name="questions">
          <div class="course-tab-hint">这里展示已绑定到当前课程的题目预览；为避免泄题，题干已做半脱敏处理，且不会返回答案、解析与正确项。</div>
          <el-table :data="courseDetail.questions || []" max-height="520" @row-click="openQuestionDetail">
            <el-table-column prop="questionId" label="题目ID" width="90" />
            <el-table-column label="题型" width="100">
              <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
            </el-table-column>
            <el-table-column prop="difficultyLevel" label="难度" width="80" />
            <el-table-column label="题干" min-width="320" show-overflow-tooltip>
              <template #default="scope">{{ stripHtml(scope.row.stem) || '题干暂未维护' }}</template>
            </el-table-column>
            <el-table-column label="来源" min-width="140" show-overflow-tooltip>
              <template #default="scope">{{ scope.row.source || '未标注来源' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button link type="primary" @click.stop="openQuestionDetail(scope.row)">查看预览</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!(courseDetail.questions || []).length" description="当前课程没有绑定题库题目，或题目录入时未关联课程" />
        </el-tab-pane>
        <el-tab-pane label="课程统计" name="stats">
          <div class="course-detail-metrics">
            <el-card class="portal-card course-detail-metric">
              <div class="course-detail-metric__label">课程考试数</div>
              <div class="course-detail-metric__value">{{ courseDetail.stats?.paperCount || 0 }}</div>
            </el-card>
            <el-card class="portal-card course-detail-metric">
              <div class="course-detail-metric__label">课程题目数</div>
              <div class="course-detail-metric__value">{{ courseDetail.stats?.questionCount || 0 }}</div>
            </el-card>
            <el-card class="portal-card course-detail-metric">
              <div class="course-detail-metric__label">考试记录数</div>
              <div class="course-detail-metric__value">{{ courseDetail.stats?.recordCount || 0 }}</div>
            </el-card>
            <el-card class="portal-card course-detail-metric">
              <div class="course-detail-metric__label">平均分</div>
              <div class="course-detail-metric__value">{{ courseDetail.stats?.avgScore || 0 }}</div>
            </el-card>
          </div>
          <el-table :data="courseDetail.records || []" max-height="420" class="mt16">
            <el-table-column prop="paperName" label="考试记录" min-width="220" show-overflow-tooltip />
            <el-table-column prop="score" label="得分" width="90" />
            <el-table-column prop="correctRate" label="正确率" width="100" />
            <el-table-column prop="examStatus" label="状态" width="100" />
            <el-table-column prop="startTime" label="开始时间" min-width="160" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <el-dialog v-model="paperPreviewOpen" :title="paperPreviewData.paperName || '试卷预览'" width="980px" top="4vh">
      <div class="preview-head">
        <span>{{ paperTypeLabel(paperPreviewData.paperType) }}</span>
        <span>{{ paperPreviewData.durationMinutes || 0 }} 分钟</span>
        <span>总分 {{ paperPreviewData.totalScore || 0 }}</span>
      </div>
      <el-table :data="paperPreviewData.questions || []" border class="mt16">
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

    <el-dialog v-model="questionPreviewOpen" :title="`题目预览 #${questionPreview.questionId || '-'}`" width="860px">
      <div class="course-question-preview">
        <div class="course-question-preview__meta">
          <span>题型：{{ questionTypeLabel(questionPreview.questionType) }}</span>
          <span>难度：{{ questionPreview.difficultyLevel || '-' }}</span>
          <span>来源：{{ questionPreview.source || '未标注来源' }}</span>
        </div>
        <div class="course-question-preview__stem">{{ stripHtml(questionPreview.stem) || '题干暂未维护' }}</div>
        <div class="course-question-preview__tips">当前为脱敏预览，不展示答案、解析和正确项。</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { getPaperDetail, getPortalCourseDetail, listPortalMyCourses, listPortalTermOptions } from '@/api/portal'
import { useRoute, useRouter } from 'vue-router'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = usePortalUserStore()
const loading = ref(false)
const courseList = ref<any[]>([])
const termOptions = ref<any[]>([])
const keyword = ref('')
const queryParams = reactive<any>({ termId: undefined })
const detailOpen = ref(false)
const detailTab = ref('basic')
const currentCourse = ref<any>(null)
const courseDetail = ref<any>({})
const paperPreviewOpen = ref(false)
const paperPreviewData = ref<any>({})
const questionPreviewOpen = ref(false)
const questionPreview = ref<any>({})

const filteredCourses = computed(() => {
  const value = keyword.value.trim().toLowerCase()
  if (!value) return courseList.value
  return courseList.value.filter((item: any) =>
    String(item.courseName || '').toLowerCase().includes(value)
    || String(item.courseCode || '').toLowerCase().includes(value),
  )
})
const totalHours = computed(() => courseList.value.reduce((sum: number, item: any) => sum + Number(item.weeklyHours || 0), 0))
const subjectCount = computed(() => new Set(courseList.value.map((item: any) => item.subjectType).filter(Boolean)).size)
const currentTermLabel = computed(() => termOptions.value.find((item: any) => item.value === queryParams.termId)?.label || '全部')
const summaryTermText = computed(() => {
  const current = termOptions.value.find((item: any) => item.value === queryParams.termId)
  return current?.termName || current?.label || currentTermLabel.value
})
const totalCredits = computed(() => {
  return courseList.value.reduce((sum: number, item: any) => sum + Number(item.credits || 0), 0)
})
const scheduledCourseCount = computed(() => courseList.value.filter((item: any) => (item.scheduleDetails || []).length > 0).length)
const unscheduledCourseCount = computed(() => Math.max(courseList.value.length - scheduledCourseCount.value, 0))

function stripHtml(value: string) {
  return String(value || '').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

function requiredLabel(value?: string) {
  if (value === 'Y') return '是'
  if (value === 'N') return '否'
  return value || '-'
}

function questionTypeLabel(type?: string) {
  return ({ single: '单选题', multiple: '多选题', judge: '判断题', fill: '填空题', essay: '简答题', material: '材料题', case: '案例题' } as any)[type || ''] || type || '-'
}

function paperTypeLabel(type?: string) {
  return ({ fixed: '固定', random: '随机', adaptive: '自适应' } as any)[type || ''] || type || '-'
}

function publishStatusLabel(status?: string) {
  return ({ draft: '草稿', published: '已发布', archived: '已归档' } as any)[status || ''] || status || '-'
}

async function openCourseDetail(row: any) {
  currentCourse.value = row
  detailTab.value = 'basic'
  const userId = userStore.user?.userId
  if (userId && row?.id) {
    const res = await getPortalCourseDetail({ userId, classCourseId: row.id })
    courseDetail.value = res.data || {}
    currentCourse.value = courseDetail.value.course || row
  } else {
    courseDetail.value = { course: row, papers: [], questions: [], records: [], stats: {} }
  }
  detailOpen.value = true
}

async function previewCoursePaper(row: any) {
  const res = await getPaperDetail(row.paperId)
  paperPreviewData.value = res.data || {}
  paperPreviewOpen.value = true
}

function goExamHub(row?: any) {
  router.push({
    path: '/student/exams',
    query: row?.paperId ? { paperId: String(row.paperId), tab: 'papers' } : { tab: 'papers' },
  })
}

function openQuestionDetail(row: any) {
  questionPreview.value = row || {}
  questionPreviewOpen.value = true
}

function summarizeSchedules(items: any[]) {
  const seen = new Set<string>()
  return items.reduce((result: string[], item: any) => {
    const weeksText = item.weeksText || '-'
    const sectionText = item.sectionText || `${item.startSectionLabel || item.startSection || '-'}~${item.endSectionLabel || item.endSection || '-'}节`
    const text = `${weeksText} ${item.weekLabel || ''} ${sectionText} ${item.classroom || ''} ${item.teacherName || ''}`.trim()
    if (!text || seen.has(text)) return result
    seen.add(text)
    result.push(text)
    return result
  }, [])
}

async function loadTerms() {
  const res = await listPortalTermOptions()
  termOptions.value = res.data || []
  const current = termOptions.value.find((item: any) => item.isCurrent === '1')
  if (!queryParams.termId && current) {
    queryParams.termId = current.value
  }
}

async function loadCourses() {
  const userId = userStore.user?.userId
  if (!userId) return
  loading.value = true
  try {
    const res = await listPortalMyCourses({ userId, termId: queryParams.termId })
    courseList.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadTerms()
  await loadCourses()
  const openCourseId = Number(route.query.openCourseId || 0)
  if (openCourseId) {
    const matched = courseList.value.find((item: any) => Number(item.id) === openCourseId)
    if (matched) {
      await openCourseDetail(matched)
    }
  }
})
</script>

<style scoped>
.course-overview {
  display: grid;
  grid-template-columns: minmax(0, 1.3fr) minmax(320px, 0.9fr);
  gap: 14px;
  margin-bottom: 18px;
}

.course-overview__hero {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(220px, 0.65fr);
  gap: 16px;
  padding: 0;
  background:
    radial-gradient(circle at top right, rgba(47, 107, 255, 0.14), transparent 28%),
    linear-gradient(135deg, #ffffff 0%, #f5f9ff 62%, #eef4ff 100%);
}

.course-overview__hero :deep(.el-card__body) {
  padding: 18px 20px;
}

.course-overview__hero-copy {
  min-width: 0;
}

.course-overview__eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(47, 107, 255, 0.09);
  color: #2b63ea;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.course-overview__hero-copy h4 {
  margin: 12px 0 8px;
  color: #16345c;
  font-size: 28px;
  line-height: 1.24;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.course-overview__hero-copy p {
  margin: 0;
  max-width: 520px;
  color: #5f728b;
  font-size: 14px;
  line-height: 1.8;
}

.course-overview__hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 10px 0;
}

.course-overview__tag {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(206, 220, 241, 0.95);
  color: #31567f;
  font-size: 13px;
  font-weight: 700;
}

.course-overview__hero-stats {
  display: grid;
  gap: 12px;
  align-content: stretch;
}

.course-overview__hero-stat {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 110px;
  padding: 16px 18px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(212, 223, 240, 0.92);
}

.course-overview__hero-stat span {
  color: #6a7b90;
  font-size: 12px;
}

.course-overview__hero-stat strong {
  margin-top: 8px;
  color: #204f9e;
  font-size: 30px;
  line-height: 1.05;
  font-weight: 800;
}

.course-overview__metrics {
  display: grid;
  gap: 12px;
}

.course-metric-card {
  padding: 0;
  overflow: hidden;
}

.course-metric-card :deep(.el-card__body) {
  display: grid;
  grid-template-columns: 48px minmax(0, 1fr);
  gap: 14px;
  align-items: center;
  padding: 16px 18px;
}

.course-metric-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.course-metric-card__label {
  color: #6a7b90;
  font-size: 12px;
  font-weight: 700;
}

.course-metric-card__value {
  margin-top: 4px;
  color: #17345d;
  font-size: 28px;
  font-weight: 800;
  line-height: 1.08;
}

.course-metric-card__sub {
  margin-top: 6px;
  color: #8a97ab;
  font-size: 12px;
  line-height: 1.6;
}

.course-metric-card--courses .course-metric-card__icon {
  background: linear-gradient(135deg, #e8f0ff 0%, #dce8ff 100%);
  color: #2b63ea;
}

.course-metric-card--hours .course-metric-card__icon {
  background: linear-gradient(135deg, #e7fbfa 0%, #d7f3f1 100%);
  color: #11867c;
}

.course-metric-card--subjects .course-metric-card__icon {
  background: linear-gradient(135deg, #fff4e8 0%, #fde8cf 100%);
  color: #cc7a14;
}

.course-list-card {
  padding-bottom: 6px;
}

.course-summary-bar {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 28px;
  margin-bottom: 12px;
  padding: 2px 0 10px;
}

.course-summary-bar__term {
  display: inline-flex;
  align-items: center;
  position: relative;
  padding-left: 10px;
  color: #172033;
  font-size: 14px;
  font-weight: 700;
  min-height: 20px;
}

.course-summary-bar__term::before {
  content: '';
  position: absolute;
  left: 0;
  top: 2px;
  width: 4px;
  height: 18px;
  border-radius: 999px;
  background: linear-gradient(180deg, #2ac3b6 0%, #179b92 100%);
}

.course-summary-bar__credit {
  display: inline-flex;
  align-items: center;
  color: #172033;
  font-size: 14px;
  font-weight: 700;
  min-height: 20px;
}

.course-filter {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.course-sheet {
  border-top: 1px solid #e7edf5;
}

.course-sheet__head,
.course-sheet__row {
  display: grid;
  grid-template-columns: 2.7fr 1.4fr 3fr 1fr 1fr;
  gap: 20px;
  align-items: start;
}

.course-sheet__head {
  padding: 14px 8px 12px;
  color: #8a94a6;
  font-size: 13px;
  font-weight: 600;
}

.course-sheet__row {
  padding: 14px 8px;
  border-top: 1px solid #edf2f8;
  color: #4d5b72;
  font-size: 14px;
  line-height: 1.8;
}

.course-sheet__cell {
  min-width: 0;
}

.course-sheet__cell--course {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.course-sheet__cell--schedule {
  color: #344256;
}

.course-sheet__cell--material {
  display: flex;
  align-items: center;
}

.course-title-link {
  padding: 0;
  border: none;
  background: transparent;
  color: #0f9f9f;
  font-size: 15px;
  font-weight: 700;
  text-align: left;
  cursor: pointer;
}

.course-title-link:hover {
  color: #0b7b7b;
}

.course-meta-line {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 14px;
  color: #667085;
  font-size: 13px;
}

.course-meta-line span,
.course-stack-line,
.schedule-line {
  display: inline-flex;
  align-items: flex-start;
  gap: 6px;
}

.course-meta-line i,
.course-stack-line i,
.schedule-line i,
.course-detail-header__meta i {
  color: #86a0bd;
  font-size: 14px;
  line-height: 1.5;
  flex: 0 0 auto;
}

.course-remark {
  color: #8893a6;
  font-size: 13px;
}

.course-stack-line {
  color: #5b677a;
  font-size: 14px;
  margin-bottom: 6px;
}

.course-stack-line:last-child {
  margin-bottom: 0;
}

.schedule-line {
  color: #3f4d63;
}

.schedule-line span {
  flex: 1;
}

.material-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 999px;
  color: #7e8a9f;
  background: #f4f7fb;
  font-size: 13px;
}

.course-count-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-top: 2px;
}

.course-count-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #5b677a;
  font-size: 14px;
  line-height: 1.4;
}

.course-count-item i {
  color: #86a0bd;
  font-size: 14px;
  flex: 0 0 auto;
}

.muted {
  color: #98a2b3;
}

@media (max-width: 1100px) {
  .course-overview {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .course-overview__hero {
    grid-template-columns: 1fr;
  }

  .course-overview__hero-copy h4 {
    font-size: 24px;
  }
}

.course-detail-header {
  margin-bottom: 6px;
}

.course-detail-header__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 18px;
  color: #667085;
  font-size: 13px;
}

.detail-grid {
  border: 1px solid #e5ebf3;
  border-bottom: none;
}

.detail-row {
  display: grid;
  grid-template-columns: 180px 1fr 180px 1fr;
}

.detail-row--full {
  grid-template-columns: 180px 1fr;
}

.detail-row label,
.detail-row span {
  min-height: 46px;
  display: flex;
  align-items: center;
  padding: 0 14px;
  border-bottom: 1px solid #e5ebf3;
}

.detail-row label {
  justify-content: flex-end;
  color: #9aa4b2;
  background: #fafcff;
}

.detail-row span {
  color: #374357;
}

.course-detail-intro {
  margin-top: 16px;
  padding: 16px 18px;
  border: 1px solid #e6edf5;
  border-radius: 16px;
  background: #f8fbff;
}

.course-detail-intro__label {
  color: #172033;
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 8px;
}

.course-detail-intro__content {
  color: #526076;
  line-height: 1.8;
  white-space: pre-wrap;
}

.course-tab-hint {
  margin-bottom: 12px;
  color: #7a8798;
  font-size: 12px;
  line-height: 1.7;
}

.course-detail-metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.course-detail-metric {
  padding: 0;
}

.course-detail-metric :deep(.el-card__body) {
  padding: 16px;
}

.course-detail-metric__label {
  color: #6a7b90;
  font-size: 12px;
}

.course-detail-metric__value {
  margin-top: 8px;
  color: #17345d;
  font-size: 26px;
  font-weight: 800;
}

.course-question-preview {
  display: grid;
  gap: 12px;
}

.course-question-preview__meta {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  color: #667085;
  font-size: 13px;
}

.course-question-preview__stem {
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8fbff;
  border: 1px solid #e6edf5;
  color: #374357;
  line-height: 1.8;
}

.course-question-preview__tips {
  color: #7a8798;
  font-size: 12px;
}

@media (max-width: 1200px) {
  .course-sheet__head,
  .course-sheet__row {
    grid-template-columns: 1.9fr 1.1fr 2fr 0.9fr 0.9fr;
    gap: 14px;
  }

  .course-detail-metrics {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
