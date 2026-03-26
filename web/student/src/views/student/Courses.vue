<template>
  <div class="portal-page">
    <div class="portal-section-title">
      <h3>{{ isClassMode ? '我的班级课程' : '我的课程' }}</h3>
    </div>

    <section class="course-overview">
      <el-card class="portal-card course-overview__hero">
        <div class="course-overview__hero-copy">
          <span class="course-overview__eyebrow">课程总览</span>
          <h4>{{ summaryTermText }}</h4>
          <p>{{ overviewDescription }}</p>
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
        <div class="course-metric-card course-metric-card--courses">
          <div class="course-metric-card__body">
            <div class="course-metric-card__icon"><i class="ri-book-open-line"></i></div>
            <div class="course-metric-card__content">
              <div class="course-metric-card__label">课程数</div>
              <div class="course-metric-card__value">{{ courseList.length }}</div>
              <div class="course-metric-card__sub">{{ isClassMode ? '当前学期班级默认课程' : '当前学期已选课程' }}</div>
            </div>
          </div>
        </div>

        <div class="course-metric-card course-metric-card--hours">
          <div class="course-metric-card__body">
            <div class="course-metric-card__icon"><i class="ri-time-line"></i></div>
            <div class="course-metric-card__content">
              <div class="course-metric-card__label">总周学时</div>
              <div class="course-metric-card__value">{{ totalHours }}</div>
              <div class="course-metric-card__sub">{{ isClassMode ? '按班级默认课程累计' : '按已选教学班累计' }}</div>
            </div>
          </div>
        </div>

        <div class="course-metric-card course-metric-card--subjects">
          <div class="course-metric-card__body">
            <div class="course-metric-card__icon"><i class="ri-layout-grid-line"></i></div>
            <div class="course-metric-card__content">
              <div class="course-metric-card__label">学科数</div>
              <div class="course-metric-card__value">{{ subjectCount }}</div>
              <div class="course-metric-card__sub">覆盖课程类型</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <div class="portal-card course-list-card !pb-5">
      <div class="course-toolbar">
        <div class="course-toolbar__left">
          <div class="course-summary-badge">
            <i class="ri-calendar-2-line"></i> {{ summaryTermText }}
          </div>
          <div class="course-summary-badge course-summary-badge--highlight">
            <i class="ri-award-line"></i> 总学分：{{ totalCredits }}
          </div>
          <el-button type="primary" plain class="course-selection-entry" @click="openSelectionPage">选课中心</el-button>
        </div>
        <div class="course-filter">
          <el-select v-model="queryParams.termId" filterable clearable placeholder="切换学期" class="filter-select" @change="loadCourses">
            <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-input v-model="keyword" clearable placeholder="搜索课程名称或课程编码..." class="filter-input">
            <template #prefix><i class="ri-search-line"></i></template>
          </el-input>
        </div>
      </div>

      <div class="course-grid" v-if="filteredCourses.length">
        <div v-for="row in filteredCourses" :key="`course-${row.id || row.courseId}`" class="course-card" @click="openCourseDetail(row)">
          <div class="course-card__header">
            <div class="course-category">
              {{ row.courseCategory || row.subjectType || '通用课程' }}
            </div>
            <div class="course-credits"><i class="ri-vip-crown-fill"></i> {{ row.credits ?? '-' }} 学分</div>
          </div>
          
          <div class="course-card__body">
            <h4 class="course-title">{{ row.courseName }}</h4>
            <div class="course-code">
              <i class="ri-barcode-box-line"></i> {{ row.courseCode || '-' }} 
              <span v-if="row.teachingClassCode" class="divider">|</span> 
              {{ row.teachingClassCode || '-' }}
            </div>
            
            <div class="course-tags">
              <span class="course-tag" title="总学时"><i class="ri-time-line"></i> {{ row.totalHours ?? '-' }}学时</span>
              <span class="course-tag" title="考核方式"><i class="ri-file-list-3-line"></i> {{ row.assessmentType || '未定' }}</span>
              <span class="course-tag" v-if="row.teachingLanguage" title="授课语言"><i class="ri-translate-2"></i> {{ row.teachingLanguage }}</span>
              <span class="course-tag" v-if="isClassMode && row.requiredSelection" title="修读要求"><i class="ri-lock-line"></i> 必修保留</span>
              <span class="course-tag" v-if="isClassMode && !row.requiredSelection && row.selected" title="当前状态"><i class="ri-checkbox-circle-line"></i> 已选</span>
            </div>

            <div class="course-schedules">
              <template v-if="(row.scheduleDetails || []).length">
                <div v-for="(item, index) in summarizeSchedules(row.scheduleDetails).slice(0, 2)" :key="`schedule-${index}`" class="course-schedule-item">
                  <i class="ri-map-pin-time-line"></i>
                  <span>{{ item }}</span>
                </div>
                <div v-if="summarizeSchedules(row.scheduleDetails).length > 2" class="course-schedule-more">
                  ...等 {{ summarizeSchedules(row.scheduleDetails).length }} 个排课
                </div>
              </template>
              <div v-else class="course-schedule-empty">
                <i class="ri-calendar-close-line"></i> 暂无排课安排
              </div>
            </div>
          </div>
          
          <div class="course-card__footer">
            <div class="course-students">
              <div class="student-progress">
                <div class="progress-bar" :style="{ width: Math.min(((row.actualStudentCount || 0) / (row.studentLimit || 1)) * 100, 100) + '%' }"></div>
              </div>
              <span><i class="ri-group-line"></i> {{ row.actualStudentCount ?? '-' }} / {{ row.studentLimit ?? '-' }} 人</span>
            </div>
            <button type="button" class="course-action-btn">
              详情 <i class="ri-arrow-right-line"></i>
            </button>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && !filteredCourses.length" description="当前学期暂无课程" class="course-empty" />
    </div>

    <el-dialog
      v-model="detailOpen"
      :title="currentCourse?.courseName || '课程详情'"
      width="min(1100px, 94vw)"
      top="5vh"
      class="course-detail-dialog"
      append-to-body
    >
      <div class="course-detail-header">
        <div class="course-detail-header__meta">
          <span class="meta-tag"><i class="ri-barcode-box-line"></i>{{ currentCourse?.courseCode || '-' }}</span>
          <span class="meta-tag"><i class="ri-hashtag"></i>{{ currentCourse?.teachingClassCode || '-' }}</span>
          <span class="meta-tag meta-tag--primary"><i class="ri-book-shelf-line"></i>{{ currentCourse?.courseCategory || '通用课程' }}</span>
        </div>
      </div>
      <el-alert
        v-if="taskContext.active"
        type="primary"
        :closable="false"
        class="course-task-context"
      >
        <template #title>
          <div class="course-task-context__content">
            <span>本任务相关内容：{{ taskContext.taskTitle || '当前任务' }}，已为你定位到更相关的课程区域。</span>
          </div>
        </template>
      </el-alert>
      <el-tabs v-model="detailTab">
        <el-tab-pane v-if="taskContext.active" label="课程任务" name="task">
          <div class="course-task-panel">
            <div class="course-task-panel__header">
              <div>
                <div class="course-task-panel__title">{{ taskContext.taskTitle || '当前任务' }}</div>
                <div class="course-task-panel__subtitle">这里集中展示这次任务与当前课程的关联信息，方便你继续执行。</div>
              </div>
              <el-tag type="primary" effect="plain">{{ taskTypeLabel(taskContext.taskType) || '课程任务' }}</el-tag>
            </div>
            <div class="course-task-panel__grid">
              <div class="course-task-panel__item">
                <span>任务类型</span>
                <strong>{{ taskTypeLabel(taskContext.taskType) || '--' }}</strong>
              </div>
              <div class="course-task-panel__item">
                <span>最近提交状态</span>
                <strong>{{ taskContext.latestSubmissionStatus || '尚未提交' }}</strong>
              </div>
              <div class="course-task-panel__item">
                <span>截止时间</span>
                <strong class="text-danger">{{ formatDateTime(taskContext.dueTime) || '--' }}</strong>
              </div>
              <div class="course-task-panel__item">
                <span>关联课程</span>
                <strong>{{ currentCourse?.courseName || '--' }}</strong>
              </div>
            </div>
            <div class="course-task-panel__tips">
              <div v-if="taskContext.taskType === 'EXAM'">当前任务关联的是课程考试，已为你在“课程考试”标签中高亮对应试卷。</div>
              <div v-else>当前任务关联的是课程学习内容，建议先查看课程简介、排课与课程要求，再返回任务详情继续提交。</div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="基本信息" name="basic">
          <div v-if="taskContext.active && taskContext.taskType !== 'EXAM'" class="course-task-highlight">
            <div class="course-task-highlight__title">当前任务提示</div>
            <div class="course-task-highlight__desc">
              {{ taskContext.taskTitle || '当前任务' }} 属于 {{ taskTypeLabel(currentCourse?.taskType) || '课程任务' }}，建议先查看课程简介、排课与课程要求，再回到任务详情继续执行。
            </div>
          </div>
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
        </el-tab-pane>
        <el-tab-pane :label="`课程考试（${courseDetail.stats?.paperCount || 0}）`" name="papers">
          <div class="course-tab-hint">仅展示当前课程已配置并开放到课程维度的考试。</div>
          <el-table :data="courseDetail.papers || []" max-height="520" :row-class-name="paperRowClassName">
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
        </el-tab-pane>
        <el-tab-pane label="课程统计" name="stats">
          <div class="course-detail-metrics">
            <div class="course-detail-metric">
              <div class="course-detail-metric__icon"><i class="ri-file-list-3-line"></i></div>
              <div class="course-detail-metric__content">
                <div class="course-detail-metric__label">课程考试数</div>
                <div class="course-detail-metric__value">{{ courseDetail.stats?.paperCount || 0 }}</div>
              </div>
            </div>
            <div class="course-detail-metric">
              <div class="course-detail-metric__icon"><i class="ri-database-2-line"></i></div>
              <div class="course-detail-metric__content">
                <div class="course-detail-metric__label">课程题目数</div>
                <div class="course-detail-metric__value">{{ courseDetail.stats?.questionCount || 0 }}</div>
              </div>
            </div>
            <div class="course-detail-metric">
              <div class="course-detail-metric__icon"><i class="ri-history-line"></i></div>
              <div class="course-detail-metric__content">
                <div class="course-detail-metric__label">考试记录数</div>
                <div class="course-detail-metric__value">{{ courseDetail.stats?.recordCount || 0 }}</div>
              </div>
            </div>
            <div class="course-detail-metric">
              <div class="course-detail-metric__icon"><i class="ri-bar-chart-box-line"></i></div>
              <div class="course-detail-metric__content">
                <div class="course-detail-metric__label">平均分</div>
                <div class="course-detail-metric__value">{{ courseDetail.stats?.avgScore || 0 }}</div>
              </div>
            </div>
          </div>
          <div class="course-detail-table-wrapper">
            <el-table :data="courseDetail.records || []" max-height="420" class="custom-striped-table">
              <el-table-column prop="paperName" label="考试记录" min-width="220" show-overflow-tooltip />
              <el-table-column prop="score" label="得分" width="100">
                <template #default="scope">
                  <span class="score-text">{{ scope.row.score ?? '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="correctRate" label="正确率" width="100" />
              <el-table-column prop="examStatus" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.examStatus === '已完成' ? 'success' : 'info'" size="small">
                    {{ scope.row.examStatus || '未知' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="startTime" label="开始时间" min-width="180" />
              <template #empty>
                <el-empty description="暂无考试记录" :image-size="80" />
              </template>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <el-dialog
      v-model="paperPreviewOpen"
      :title="paperPreviewData.paperName || '试卷预览'"
      width="1120px"
      top="4vh"
      class="course-preview-dialog"
      append-to-body
    >
      <div class="course-exam-preview-shell">
        <section class="course-exam-preview-summary">
          <div class="course-exam-preview-summary__main">
            <div class="course-exam-preview-summary__eyebrow">试卷导览</div>
            <h3 class="course-exam-preview-summary__title">{{ paperPreviewData.paperName || '未命名试卷' }}</h3>
            <p class="course-exam-preview-summary__desc">这里展示试卷结构、题型分布和题目预览，不展示答案、解析与正确项。</p>
          </div>
          <div class="course-exam-preview-summary__stats">
            <div class="course-exam-preview-stat">
              <span>类型</span>
              <strong>{{ paperTypeLabel(paperPreviewData.paperType) }}</strong>
            </div>
            <div class="course-exam-preview-stat">
              <span>时长</span>
              <strong>{{ paperPreviewData.durationMinutes || 0 }} 分钟</strong>
            </div>
            <div class="course-exam-preview-stat">
              <span>总分</span>
              <strong>{{ paperPreviewData.totalScore || 0 }}</strong>
            </div>
            <div class="course-exam-preview-stat">
              <span>题目数</span>
              <strong>{{ paperPreviewQuestionCount }}</strong>
            </div>
          </div>
        </section>

        <section class="course-exam-preview-outline mt16">
          <div class="course-exam-preview-outline__title">题型分布</div>
          <div class="course-exam-preview-outline__grid">
            <div v-for="group in paperPreviewQuestionGroups" :key="group.type" class="course-exam-preview-outline__card">
              <div class="course-exam-preview-outline__type">{{ questionTypeLabel(group.type) }}</div>
              <div class="course-exam-preview-outline__meta">{{ group.questions.length }} 题</div>
              <div class="course-exam-preview-outline__score">{{ group.totalScore }} 分</div>
            </div>
          </div>
        </section>

        <section class="course-exam-preview-list mt16">
          <div v-for="group in paperPreviewQuestionGroups" :key="group.type" class="course-exam-preview-group">
            <div class="course-exam-preview-group__header">
              <div class="course-exam-preview-group__title">{{ questionTypeLabel(group.type) }}</div>
              <div class="course-exam-preview-group__meta">{{ group.questions.length }} 题 / {{ group.totalScore }} 分</div>
            </div>
            <div class="course-exam-preview-question-list">
              <article v-for="item in group.questions" :key="item.questionId" class="course-exam-preview-question">
                <div class="course-exam-preview-question__top">
                  <div class="course-exam-preview-question__index">第 {{ item.sortNo || item.order }} 题</div>
                  <div class="course-exam-preview-question__score">{{ item.score || 0 }} 分</div>
                </div>
                <div class="course-exam-preview-question__stem">{{ stripHtml(item.question?.stem) || '题干暂未维护' }}</div>
                <div v-if="(item.question?.options || []).length" class="course-exam-preview-question__options">
                  <div v-for="opt in item.question?.options || []" :key="opt.optionId || opt.optionKey" class="course-exam-preview-option">
                    <span class="course-exam-preview-option__key">{{ opt.optionKey }}.</span>
                    <span class="course-exam-preview-option__content">{{ opt.optionContent }}</span>
                  </div>
                </div>
              </article>
            </div>
          </div>
        </section>

        <div class="course-exam-preview-actions mt16">
          <el-button v-if="paperPreviewData.paperId" type="primary" @click="goExamHub(paperPreviewData)">去考试中心</el-button>
          <el-button plain @click="paperPreviewOpen = false">关闭预览</el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog
      v-model="questionPreviewOpen"
      :title="`题目预览 #${questionPreview.questionId || '-'}`"
      width="min(860px, 92vw)"
      class="course-question-dialog"
      append-to-body
    >
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
import { getPaperDetail, getPortalCourseDetail, listPortalMyClassCourses, listPortalMyCourses, listPortalTermOptions } from '@/api/portal'
import { useRoute, useRouter } from 'vue-router'
import usePortalUserStore from '@/store/user'

const props = withDefaults(defineProps<{ mode?: 'selected' | 'class' }>(), {
  mode: 'selected',
})

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
const isClassMode = computed(() => props.mode === 'class')
const taskContext = computed(() => ({
  active: route.query.fromTask === '1',
  taskType: String(route.query.taskType || '').toUpperCase(),
  taskTitle: String(route.query.taskTitle || ''),
  paperId: Number(route.query.paperId || 0),
  latestSubmissionStatus: String(route.query.latestSubmissionStatus || ''),
  dueTime: String(route.query.dueTime || ''),
}))
const paperPreviewQuestionCount = computed(() => (paperPreviewData.value.questions || []).length)
const paperPreviewQuestionGroups = computed(() => {
  const groups = new Map<string, { type: string; totalScore: number; questions: any[] }>()
  ;(paperPreviewData.value.questions || []).forEach((item: any, index: number) => {
    const type = String(item?.question?.questionType || 'unknown')
    if (!groups.has(type)) {
      groups.set(type, { type, totalScore: 0, questions: [] })
    }
    const group = groups.get(type)!
    group.questions.push({
      ...item,
      order: index + 1,
    })
    group.totalScore += Number(item?.score || 0)
  })
  return Array.from(groups.values())
})

const filteredCourses = computed(() => {
  const value = keyword.value.trim().toLowerCase()
  if (!value) return courseList.value
  return courseList.value.filter((item: any) =>
    String(item.courseName || '').toLowerCase().includes(value)
    || String(item.courseCode || '').toLowerCase().includes(value)
    || String(item.className || '').toLowerCase().includes(value)
    || String(item.teachingClassCode || '').toLowerCase().includes(value),
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
const overviewDescription = computed(() => {
  if (isClassMode.value) {
    return `当前学期班级默认开设 ${courseList.value.length} 门课程，这里主要用于查看培养方案视角下的班级课程与排课安排。`
  }
  return `当前学期共 ${courseList.value.length} 门课程，建议优先查看已排课课程与本周上课安排。`
})

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

function formatDateTime(value?: string) {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  const hour = `${date.getHours()}`.padStart(2, '0')
  const minute = `${date.getMinutes()}`.padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

function taskTypeLabel(type?: string) {
  return ({ HOMEWORK: '作业任务', EXAM: '考试任务', PRACTICE: '实验任务', READING: '阅读任务', COURSE: '课程任务' } as any)[type || ''] || type || '-'
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

function openSelectionPage() {
  router.push({
    path: '/student/selection',
    query: queryParams.termId ? { termId: String(queryParams.termId) } : {},
  })
}

async function previewCoursePaper(row: any) {
  router.push(`/student/exams/preview/${row.paperId}`)
}

function goExamHub(row?: any) {
  router.push({
    path: '/student/exams',
    query: row?.paperId ? { paperId: String(row.paperId), tab: 'papers' } : { tab: 'papers' },
  })
}

function paperRowClassName({ row }: { row: any }) {
  if (!taskContext.value.active || taskContext.value.taskType !== 'EXAM') return ''
  return Number(row?.paperId || 0) === taskContext.value.paperId ? 'is-related-task-row' : ''
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
    const res = isClassMode.value
      ? await listPortalMyClassCourses({ userId, termId: queryParams.termId })
      : await listPortalMyCourses({ userId, termId: queryParams.termId })
    courseList.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (route.query.termId) {
    queryParams.termId = Number(route.query.termId)
  }
  await loadTerms()
  await loadCourses()
  const requestedTab = String(route.query.tab || '')
  if (['task', 'basic', 'schedule', 'papers', 'questions', 'stats'].includes(requestedTab)) {
    detailTab.value = requestedTab
  }
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
.portal-page {
  padding: 24px 32px;
  display: flex;
  flex-direction: column;
  min-height: 100%;
  background: transparent;
}

.course-overview {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(280px, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.course-overview__hero {
  display: flex;
  flex-direction: column;
  padding: 0;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  box-shadow: none;
}

.course-overview__hero :deep(.el-card__body) {
  padding: 24px;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.course-overview__hero-copy {
  flex: 1;
  min-width: 0;
}

.course-overview__eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: #eff6ff;
  color: #3b82f6;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 16px;
}

.course-overview__hero-copy h4 {
  margin: 0 0 8px;
  color: #0f172a;
  font-size: 24px;
  line-height: 1.3;
  font-weight: 700;
}

.course-overview__hero-copy p {
  margin: 0 0 16px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.6;
}

.course-overview__hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.course-overview__tag {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 12px;
  border-radius: 6px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  color: #475569;
  font-size: 12px;
  font-weight: 500;
}

.course-overview__hero-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px dashed #e2e8f0;
}

.course-overview__hero-stat {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.course-overview__hero-stat span {
  color: #64748b;
  font-size: 12px;
  font-weight: 500;
}

.course-overview__hero-stat strong {
  margin-top: 4px;
  color: #0f172a;
  font-size: 24px;
  line-height: 1;
  font-weight: 700;
}

.course-overview__metrics {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.course-metric-card {
  padding: 0;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: none;
  background: #ffffff;
  flex: 1;
}

.course-metric-card__body {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 16px 20px;
}

.course-metric-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 16px;
  flex-shrink: 0;
}

.course-metric-card__content {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.course-metric-card__label {
  color: #64748b;
  font-size: 12px;
  font-weight: 500;
  margin-bottom: 2px;
}

.course-metric-card__value {
  color: #0f172a;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.1;
}

.course-metric-card__sub {
  color: #94a3b8;
  font-size: 12px;
  margin-top: 4px;
}

.course-metric-card--courses .course-metric-card__icon {
  background: #eff6ff;
  color: #3b82f6;
}

.course-metric-card--hours .course-metric-card__icon {
  background: #ecfdf5;
  color: #10b981;
}

.course-metric-card--subjects .course-metric-card__icon {
  background: #fffbeb;
  color: #f59e0b;
}

.course-list-card {
  padding: 0;
  background: transparent;
  border: none;
  box-shadow: none;
}

.course-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  padding: 20px;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.02);
  margin-bottom: 24px;
}

.course-toolbar__left {
  display: flex;
  gap: 12px;
  align-items: center;
}

.course-summary-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: #f1f5f9;
  color: #334155;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
}

.course-summary-badge--highlight {
  background: #eff6ff;
  color: #2563eb;
}

.course-filter {
  display: flex;
  gap: 12px;
  align-items: center;
}

.course-selection-entry {
  height: 34px;
  border-radius: 10px;
}

.filter-select {
  width: 200px;
}

.filter-input {
  width: 280px;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.course-card {
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.03);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  overflow: hidden;
  cursor: pointer;
  position: relative;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.08);
  border-color: #bfdbfe;
}

.course-card__header {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f8fafc;
  background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
}

.course-category {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
  border-radius: 6px;
  letter-spacing: 0.02em;
}

.course-credits {
  font-size: 13px;
  font-weight: 800;
  color: #334155;
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-credits i {
  color: #f59e0b;
  font-size: 16px;
}

.course-card__body {
  padding: 18px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.course-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #0f172a;
  font-weight: 700;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
}

.course-card:hover .course-title {
  color: #2563eb;
}

.course-code {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.course-code .divider {
  color: #cbd5e1;
  margin: 0 2px;
}

.course-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.course-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #475569;
  font-size: 12px;
  border-radius: 4px;
}

.course-tag i {
  color: #94a3b8;
}

.course-schedules {
  margin-top: auto;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.course-schedule-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px;
  color: #334155;
  line-height: 1.5;
}

.course-schedule-item i {
  color: #6366f1;
  margin-top: 2px;
}

.course-schedule-more {
  font-size: 12px;
  color: #64748b;
  padding-left: 20px;
}

.course-schedule-empty {
  font-size: 13px;
  color: #94a3b8;
  display: flex;
  align-items: center;
  gap: 6px;
}

.course-card__footer {
  padding: 14px 20px;
  border-top: 1px dashed #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafbfc;
}

.course-students {
  display: flex;
  flex-direction: column;
  gap: 6px;
  width: 140px;
}

.student-progress {
  height: 4px;
  background: #e2e8f0;
  border-radius: 2px;
  overflow: hidden;
}

.student-progress .progress-bar {
  height: 100%;
  background: #10b981;
  border-radius: 2px;
  transition: width 0.5s ease;
}

.course-students span {
  font-size: 12px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-action-btn {
  padding: 6px 16px;
  background: transparent;
  border: 1px solid #cbd5e1;
  color: #334155;
  font-size: 13px;
  font-weight: 600;
  border-radius: 6px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: all 0.2s;
}

.course-card:hover .course-action-btn {
  background: #2563eb;
  border-color: #2563eb;
  color: #ffffff;
}

.course-empty {
  padding: 60px 0;
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
  margin-bottom: 20px;
}

.course-detail-dialog :deep(.el-dialog),
.course-preview-dialog :deep(.el-dialog),
.course-question-dialog :deep(.el-dialog) {
  max-width: 94vw;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.12);
  border: 1px solid #e2e8f0;
}

.course-detail-dialog :deep(.el-dialog__header),
.course-preview-dialog :deep(.el-dialog__header),
.course-question-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 24px 32px 16px;
  border-bottom: none;
  background: #ffffff;
}

.course-detail-dialog :deep(.el-dialog__title),
.course-preview-dialog :deep(.el-dialog__title),
.course-question-dialog :deep(.el-dialog__title) {
  color: #0f172a;
  font-size: 20px;
  font-weight: 800;
  line-height: 1.4;
}

.course-detail-dialog :deep(.el-dialog__body) {
  max-height: calc(90vh - 80px);
  padding: 0 32px 32px;
  overflow: auto;
}

.course-preview-dialog :deep(.el-dialog__body),
.course-question-dialog :deep(.el-dialog__body) {
  max-height: calc(88vh - 84px);
  padding: 16px 32px 32px;
  overflow: auto;
}

.course-detail-dialog :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.course-detail-dialog :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #e2e8f0;
}

.course-detail-dialog :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
  color: #64748b;
  padding: 0 20px;
}

.course-detail-dialog :deep(.el-tabs__item.is-active) {
  color: #2563eb;
  font-weight: 700;
}

.course-detail-dialog :deep(.el-tabs__active-bar) {
  background-color: #2563eb;
  height: 3px;
  border-radius: 3px 3px 0 0;
}

.course-detail-dialog :deep(.el-tabs__content) {
  overflow: visible;
}

.course-detail-dialog :deep(.el-table),
.course-preview-dialog :deep(.el-table) {
  width: 100%;
}

.course-detail-header__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.course-task-context {
  margin-bottom: 16px;
}

.course-task-context__content {
  display: flex;
  align-items: center;
  gap: 8px;
  line-height: 1.6;
}

.course-task-panel {
  padding: 20px 22px;
  border-radius: 14px;
  border: 1px solid #dbeafe;
  background: linear-gradient(135deg, #f8fbff 0%, #ffffff 100%);
}

.course-task-panel__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;
}

.course-task-panel__title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.course-task-panel__subtitle {
  margin-top: 6px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.course-task-panel__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.course-task-panel__item {
  padding: 14px 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.course-task-panel__item span {
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
}

.course-task-panel__item strong {
  color: #0f172a;
  font-size: 15px;
  line-height: 1.6;
}

.course-task-panel__tips {
  margin-top: 16px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #eff6ff;
  color: #475569;
  font-size: 13px;
  line-height: 1.8;
}

.meta-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  color: #475569;
  font-size: 13px;
  font-weight: 500;
}

.meta-tag i {
  color: #94a3b8;
  font-size: 15px;
}

.meta-tag--primary {
  background: #eff6ff;
  border-color: #bfdbfe;
  color: #2563eb;
}

.meta-tag--primary i {
  color: #3b82f6;
}

.detail-grid {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.detail-row {
  display: grid;
  grid-template-columns: 160px 1fr 160px 1fr;
  border-bottom: 1px solid #e2e8f0;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row--full {
  grid-template-columns: 160px 1fr;
}

.detail-row label,
.detail-row span {
  min-height: 48px;
  display: flex;
  align-items: center;
  padding: 12px 20px;
  font-size: 14px;
}

.detail-row label {
  justify-content: flex-end;
  color: #64748b;
  background: #f8fafc;
  font-weight: 500;
  border-right: 1px solid #e2e8f0;
}

.detail-row span {
  color: #0f172a;
  background: #ffffff;
  border-right: 1px solid #e2e8f0;
}

.detail-row span:last-child {
  border-right: none;
}

.course-detail-intro {
  margin-top: 24px;
  padding: 20px 24px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #f8fafc;
}

.course-task-highlight {
  margin-bottom: 16px;
  padding: 16px 18px;
  border-radius: 12px;
  border: 1px solid #bfdbfe;
  background: linear-gradient(135deg, #eff6ff 0%, #ffffff 100%);
}

.course-task-highlight__title {
  color: #1d4ed8;
  font-size: 14px;
  font-weight: 700;
}

.course-task-highlight__desc {
  margin-top: 8px;
  color: #475569;
  font-size: 13px;
  line-height: 1.8;
}

.course-detail-intro__label {
  color: #0f172a;
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 12px;
}

.course-detail-intro__content {
  color: #475569;
  line-height: 1.8;
  font-size: 14px;
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
  gap: 16px;
  margin-bottom: 24px;
}

.course-detail-metric {
  padding: 16px 20px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  display: flex;
  align-items: center;
  gap: 16px;
}

.course-detail-metric__icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  background: #eff6ff;
  color: #3b82f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}

.course-detail-metric__content {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.course-detail-metric__label {
  color: #64748b;
  font-size: 12px;
  font-weight: 500;
  margin-bottom: 4px;
}

.course-detail-metric__value {
  color: #0f172a;
  font-size: 22px;
  font-weight: 700;
  line-height: 1;
}

.course-detail-table-wrapper {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
}

.custom-striped-table :deep(.el-table__header-wrapper th) {
  background-color: #f8fafc;
  color: #64748b;
  font-weight: 600;
  font-size: 13px;
  height: 48px;
}

.custom-striped-table :deep(.el-table__row) {
  height: 52px;
}

.custom-striped-table :deep(.el-table__cell) {
  padding: 8px 0;
}

:deep(.el-table__row.is-related-task-row) {
  --el-table-tr-bg-color: #eff6ff;
}

.score-text {
  font-weight: 700;
  color: #f59e0b;
  font-size: 15px;
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

.course-exam-preview-shell {
  display: flex;
  flex-direction: column;
}

.course-exam-preview-summary {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(320px, 0.9fr);
  gap: 20px;
  padding: 24px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fbff 0%, #ffffff 100%);
  border: 1px solid #dbeafe;
}

.course-exam-preview-summary__eyebrow {
  display: inline-flex;
  padding: 4px 12px;
  border-radius: 999px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
}

.course-exam-preview-summary__title {
  margin: 14px 0 10px;
  color: #0f172a;
  font-size: 24px;
  line-height: 1.35;
  font-weight: 800;
}

.course-exam-preview-summary__desc {
  margin: 0;
  color: #64748b;
  line-height: 1.8;
  font-size: 14px;
}

.course-exam-preview-summary__stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.course-exam-preview-stat {
  padding: 16px;
  border-radius: 10px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.course-exam-preview-stat span {
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
}

.course-exam-preview-stat strong {
  color: #0f172a;
  font-size: 18px;
  line-height: 1.3;
  font-weight: 700;
}

.course-exam-preview-outline__title {
  color: #0f172a;
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 14px;
}

.course-exam-preview-outline__grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.course-exam-preview-outline__card {
  padding: 16px 18px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.03);
}

.course-exam-preview-outline__type {
  color: #0f172a;
  font-size: 15px;
  font-weight: 700;
}

.course-exam-preview-outline__meta {
  margin-top: 8px;
  color: #64748b;
  font-size: 13px;
}

.course-exam-preview-outline__score {
  margin-top: 6px;
  color: #2563eb;
  font-size: 20px;
  font-weight: 800;
}

.course-exam-preview-group + .course-exam-preview-group {
  margin-top: 24px;
}

.course-exam-preview-group__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  gap: 12px;
}

.course-exam-preview-group__title {
  color: #0f172a;
  font-size: 17px;
  font-weight: 700;
}

.course-exam-preview-group__meta {
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
}

.course-exam-preview-question-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.course-exam-preview-question {
  padding: 18px 20px;
  border-radius: 14px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.03);
}

.course-exam-preview-question__top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.course-exam-preview-question__index {
  color: #334155;
  font-size: 13px;
  font-weight: 700;
}

.course-exam-preview-question__score {
  color: #2563eb;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  border-radius: 999px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 700;
}

.course-exam-preview-question__stem {
  margin-top: 14px;
  color: #0f172a;
  font-size: 15px;
  line-height: 1.8;
}

.course-exam-preview-question__options {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.course-exam-preview-option {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.course-exam-preview-option__key {
  color: #2563eb;
  font-weight: 700;
  flex-shrink: 0;
}

.course-exam-preview-option__content {
  color: #334155;
  line-height: 1.6;
}

.course-exam-preview-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 1200px) {
  .course-detail-metrics {
    grid-template-columns: 1fr 1fr;
  }

  .course-exam-preview-summary {
    grid-template-columns: 1fr;
  }

  .course-task-panel__grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .course-detail-dialog :deep(.el-dialog),
  .course-preview-dialog :deep(.el-dialog),
  .course-question-dialog :deep(.el-dialog) {
    max-width: 96vw;
  }

  .course-detail-dialog :deep(.el-dialog__header),
  .course-preview-dialog :deep(.el-dialog__header),
  .course-question-dialog :deep(.el-dialog__header) {
    padding: 16px 16px 10px;
  }

  .course-detail-dialog :deep(.el-dialog__body),
  .course-preview-dialog :deep(.el-dialog__body),
  .course-question-dialog :deep(.el-dialog__body) {
    padding: 12px 12px 14px;
  }

  .detail-row,
  .detail-row--full {
    grid-template-columns: 132px minmax(0, 1fr);
  }

  .detail-row label,
  .detail-row span {
    min-height: 42px;
    padding: 10px 12px;
  }

  .detail-row label {
    justify-content: flex-start;
  }

  .course-detail-intro {
    margin-top: 14px;
    padding: 14px;
    border-radius: 12px;
  }
}

@media (max-width: 640px) {
  .course-detail-header__meta {
    gap: 8px 12px;
    font-size: 12px;
  }

  .detail-row,
  .detail-row--full {
    grid-template-columns: 1fr;
  }

  .detail-row label,
  .detail-row span {
    min-height: unset;
  }

  .detail-row label {
    border-bottom: none;
    padding-bottom: 4px;
  }

  .detail-row span {
    padding-top: 0;
    padding-bottom: 12px;
  }

  .course-detail-metrics {
    grid-template-columns: 1fr;
  }

  .course-question-preview__meta {
    flex-direction: column;
    gap: 6px;
  }
}
</style>
