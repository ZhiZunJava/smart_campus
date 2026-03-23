<template>
  <div class="portal-page exam-page">
    <section class="exam-hero">
      <div class="hero-content">
        <div class="hero-eyebrow"><i class="ri-dashboard-3-line mr-1"></i>考试中心</div>
        <div class="hero-title">智能评测中心</div>
        <p class="hero-desc">
          在这里统一查看可参加考试、历史成绩、错题回流和答题反馈。每次测评都会沉淀成你后续练习和诊断的依据。
        </p>
        <div class="hero-actions">
          <el-button type="primary" @click="activeTab = 'papers'">开始新测评</el-button>
          <el-button @click="activeTab = 'records'">查看历史记录</el-button>
          <el-button @click="goWrongbook">进入错题本</el-button>
        </div>
      </div>
      <div class="hero-stats">
        <div class="hero-metric">
          <span>最佳成绩</span>
          <strong>{{ bestScore }}</strong>
        </div>
        <div class="hero-metric">
          <span>平均得分</span>
          <strong>{{ recordOverview.avgScore || 0 }}</strong>
        </div>
      </div>
    </section>

    <section class="exam-kpis">
      <div class="exam-kpi-card">
        <div class="kpi-content">
          <div class="kpi-label">可参加考试</div>
          <div class="kpi-value text-primary">{{ availablePapers.length }}</div>
          <div class="kpi-sub">当前开放试卷数量</div>
        </div>
      </div>
      <div class="exam-kpi-card">
        <div class="kpi-content">
          <div class="kpi-label">考试记录</div>
          <div class="kpi-value">{{ records.length }}</div>
          <div class="kpi-sub">最近考试与练习记录</div>
        </div>
      </div>
      <div class="exam-kpi-card">
        <div class="kpi-content">
          <div class="kpi-label">平均正确率</div>
          <div class="kpi-value">{{ recordOverview.avgCorrectRate || 0 }}%</div>
          <div class="kpi-sub">自动统计历史提交结果</div>
        </div>
      </div>
      <div class="exam-kpi-card">
        <div class="kpi-content">
          <div class="kpi-label">未掌握错题</div>
          <div class="kpi-value">{{ unmasteredWrongCount }}</div>
          <div class="kpi-sub">建议优先回练与复盘</div>
        </div>
      </div>
    </section>

    <div class="exam-section-card mt20 pd-5">
      <div class="section-header tabs-header">
        <div class="section-title">智能评测工作台</div>
        <el-segmented v-model="activeTab" :options="tabOptions" class="custom-segmented" />
      </div>

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
                  <el-tag v-if="isWrongRetryPaper(paper)" type="danger" effect="plain">错题回练</el-tag>
                  <el-tag :type="paper.publishStatus === 'published' ? 'success' : 'warning'" effect="plain">
                    {{ publishStatusLabel(paper.publishStatus) }}
                  </el-tag>
                </div>
              </div>
              <div class="exam-paper-card__meta">
                <span class="meta-item">{{ courseLabel(paper.courseId) }}</span>
                <span class="meta-divider"></span>
                <span class="meta-item">{{ paperTypeLabel(paper.paperType) }}</span>
                <span class="meta-divider"></span>
                <span class="meta-item">{{ paper.durationMinutes || 0 }} 分钟</span>
                <span class="meta-divider"></span>
                <span class="meta-item">{{ paperLevelLabel(paper.paperLevel) }}</span>
              </div>
              <div class="exam-paper-card__stats">
                <span class="stat-item">总分 <strong class="stat-value">{{ paper.totalScore || 0 }}</strong></span>
                <span class="stat-item">及格线 <strong class="stat-value">{{ paper.passScore || 60 }}</strong></span>
                <span v-if="paper.subPapers?.length" class="stat-item">子卷 <strong class="stat-value">{{ paper.subPapers.length }}</strong></span>
                <span v-if="Number(paper.maxAttemptCount || 0) > 0" class="stat-item">次数 <strong class="stat-value">{{ attemptSummary(paper) }}</strong></span>
              </div>
              <div v-if="ongoingRecordMap[paper.paperId]" class="exam-paper-card__resume">
                已有未交卷记录，开始时间 {{ formatDateTime(ongoingRecordMap[paper.paperId]?.startTime) }}
              </div>
              <div v-else-if="isPaperAttemptLimitReached(paper)" class="exam-paper-card__resume">
                当前试卷可参加次数已用完
              </div>
              <div v-else-if="isMakeupPaper(paper)" class="exam-paper-card__resume">
                你已参加 {{ attemptCountMap[String(paper.paperId)] }}/{{ paper.maxAttemptCount }} 次，还可补考 {{ Number(paper.maxAttemptCount || 0) - (attemptCountMap[String(paper.paperId)] || 0) }} 次
              </div>
              <div v-else-if="(attemptCountMap[String(paper.paperId)] || 0) > 0" class="exam-paper-card__resume">
                你已参加 {{ attemptCountMap[String(paper.paperId)] }} 次，可再次参加考试
              </div>
              <div class="exam-paper-card__actions">
                <el-button
                  type="primary"
                  :loading="startingPaperId === paper.paperId"
                  :disabled="startingPaperId === paper.paperId || (!ongoingRecordMap[paper.paperId] && isPaperAttemptLimitReached(paper))"
                  @click="beginExam(paper)"
                >
                  {{ ongoingRecordMap[paper.paperId] ? '继续考试' : (isPaperAttemptLimitReached(paper) ? '次数已用完' : (isMakeupPaper(paper) ? '去补考' : ((attemptCountMap[String(paper.paperId)] || 0) > 0 ? '再次考试' : '开始考试'))) }}
                </el-button>
                <el-button
                  link
                  type="primary"
                  :disabled="Boolean(ongoingRecordMap[paper.paperId])"
                  @click="openPreviewPage(paper)"
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
                <span class="meta-item">开始于 {{ formatDateTime(item.startTime) }}</span>
                <span class="meta-divider"></span>
                <span class="meta-item">状态 <el-tag size="small" :type="item.examStatus === 'ONGOING' ? 'warning' : 'info'" style="margin-left: 6px;">{{ examStatusLabel(item.examStatus) }}</el-tag></span>
              </div>
          <div class="exam-runtime-card__stats">
                <span class="stat-item">切屏 <strong class="stat-value">{{ item.session?.focusLossCount || 0 }}</strong></span>
                <span class="stat-item">异常 <strong class="stat-value">{{ item.session?.flaggedCount || 0 }}</strong></span>
                <span class="stat-item">子卷 <strong class="stat-value">{{ item.subPaperStats?.length || 0 }}</strong></span>
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
          <el-table v-loading="loadingRecord" :data="records" @row-click="viewRecordDetail" class="custom-table" style="width: 100%" :header-cell-style="{ background: '#fafafa', color: '#606266', fontWeight: 500 }">
            <el-table-column prop="paperName" label="试卷名称" min-width="180" show-overflow-tooltip />
            <el-table-column label="标记" min-width="110">
              <template #default="scope">
                <el-tag v-if="isWrongRetryPaper(scope.row)" type="danger" effect="plain">错题回练</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="课程" min-width="160" show-overflow-tooltip>
              <template #default="scope">{{ courseLabel(scope.row.courseId) }}</template>
            </el-table-column>
            <el-table-column prop="score" label="得分" min-width="90" />
            <el-table-column prop="correctRate" label="正确率" min-width="100" />
            <el-table-column label="状态" min-width="100">
              <template #default="scope">
                <el-tag :type="scope.row.examStatus === 'SUBMITTED' ? 'success' : 'warning'">{{ examStatusLabel(scope.row.examStatus) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="分层情况" min-width="140" show-overflow-tooltip>
              <template #default="scope">
                {{ scope.row.subPaperStats?.length ? `子卷 ${scope.row.subPaperStats.length} 个` : '单层试卷' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="200">
              <template #default="scope">
                <el-button
                  v-if="scope.row.examStatus === 'ONGOING'"
                  link
                  type="warning"
                  @click.stop="resumeExam(scope.row)"
                >
                  继续考试
                </el-button>
                <template v-else>
                  <el-button link type="primary" @click.stop="viewRecordDetail(scope.row)">查看详情</el-button>
                  <el-button link type="success" @click.stop="openRecordSessionResult(scope.row)">查看答卷</el-button>
                </template>
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
          <el-table :data="recordOverview.subPaperStats || []" class="custom-table" style="width: 100%" :header-cell-style="{ background: '#fafafa', color: '#606266', fontWeight: 500 }">
            <el-table-column prop="paperName" label="子试卷" min-width="140" show-overflow-tooltip />
            <el-table-column prop="submittedCount" label="已提交" min-width="90" />
            <el-table-column prop="skippedCount" label="跳过" min-width="90" />
            <el-table-column prop="avgScore" label="平均得分" min-width="100" />
            <el-table-column prop="avgCorrectRate" label="平均正确率" min-width="110" />
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
          <el-table :data="wrongs" class="custom-table" style="width: 100%" :header-cell-style="{ background: '#fafafa', color: '#606266', fontWeight: 500 }">
            <el-table-column prop="questionId" label="题目ID" min-width="90" />
            <el-table-column label="课程" min-width="160"><template #default="scope">{{ courseLabel(scope.row.courseId) }}</template></el-table-column>
            <el-table-column label="知识点" min-width="160"><template #default="scope">{{ knowledgePointLabel(scope.row.knowledgePointId) }}</template></el-table-column>
            <el-table-column prop="wrongCount" label="错误次数" min-width="100" />
            <el-table-column label="掌握状态" min-width="100">
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
          <el-table :data="recordOverview.knowledgePointStats || []" class="custom-table" style="width: 100%" :header-cell-style="{ background: '#fafafa', color: '#606266', fontWeight: 500 }">
            <el-table-column label="知识点" min-width="160">
              <template #default="scope">{{ scope.row.knowledgePointName || knowledgePointLabel(scope.row.knowledgePointId) }}</template>
            </el-table-column>
            <el-table-column prop="totalCount" label="总题数" min-width="90" />
            <el-table-column prop="correctCount" label="答对数" min-width="90" />
          </el-table>
        </section>
      </div>
    </div>

    <el-dialog v-model="previewOpen" :title="previewPaperData.paperName || '试卷预览'" width="1120px" top="4vh" class="exam-preview-dialog" append-to-body>
      <div class="exam-preview-shell">
        <section class="exam-preview-summary">
          <div class="exam-preview-summary__main">
            <div class="exam-preview-summary__eyebrow">试卷导览</div>
            <h3 class="exam-preview-summary__title">{{ previewPaperData.paperName || '未命名试卷' }}</h3>
            <p class="exam-preview-summary__desc">这里展示试卷结构、题型分布和题目预览，不展示答案、解析与正确项。</p>
          </div>
          <div class="exam-preview-summary__stats">
            <div class="exam-preview-stat">
              <span>课程</span>
              <strong>{{ courseLabel(previewPaperData.courseId) }}</strong>
            </div>
            <div class="exam-preview-stat">
              <span>时长</span>
              <strong>{{ previewPaperData.durationMinutes || 0 }} 分钟</strong>
            </div>
            <div class="exam-preview-stat">
              <span>总分</span>
              <strong>{{ previewPaperData.totalScore || 0 }}</strong>
            </div>
            <div class="exam-preview-stat">
              <span>题目数</span>
              <strong>{{ previewQuestionCount }}</strong>
            </div>
          </div>
        </section>

        <section class="exam-preview-outline mt20">
          <div class="exam-preview-outline__title">题型分布</div>
          <div class="exam-preview-outline__grid">
            <div v-for="group in previewQuestionGroups" :key="group.type" class="exam-preview-outline__card">
              <div class="exam-preview-outline__type">{{ questionTypeLabel(group.type) }}</div>
              <div class="exam-preview-outline__meta">{{ group.questions.length }} 题</div>
              <div class="exam-preview-outline__score">{{ group.totalScore }} 分</div>
            </div>
          </div>
        </section>

        <section class="exam-preview-list mt20">
          <div v-for="group in previewQuestionGroups" :key="group.type" class="exam-preview-group">
            <div class="exam-preview-group__header">
              <div class="exam-preview-group__title">{{ questionTypeLabel(group.type) }}</div>
              <div class="exam-preview-group__meta">{{ group.questions.length }} 题 / {{ group.totalScore }} 分</div>
            </div>
            <div class="exam-preview-question-list">
              <article v-for="item in group.questions" :key="item.questionId" class="exam-preview-question">
                <div class="exam-preview-question__top">
                  <div class="exam-preview-question__index">第 {{ item.sortNo || item.order }} 题</div>
                  <div class="exam-preview-question__score">{{ item.score || 0 }} 分</div>
                </div>
                <div class="exam-preview-question__stem">{{ stripHtml(item.question?.stem) || '题干暂未维护' }}</div>
                <div v-if="(item.question?.options || []).length" class="exam-preview-question__options">
                  <div v-for="opt in item.question?.options || []" :key="opt.optionId || opt.optionKey" class="exam-preview-option">
                    <span class="exam-preview-option__key">{{ opt.optionKey }}.</span>
                    <span class="exam-preview-option__content">{{ opt.optionContent }}</span>
                  </div>
                </div>
              </article>
            </div>
          </div>
        </section>

        <div class="exam-preview-actions mt20">
          <el-button
            v-if="previewPaperData.paperId"
            type="primary"
            :disabled="Boolean(ongoingRecordMap[previewPaperData.paperId]) || isPaperAttemptLimitReached(previewPaperData)"
            @click="beginExam(previewPaperData)"
          >
            {{ ongoingRecordMap[previewPaperData.paperId] ? '继续考试' : (isPaperAttemptLimitReached(previewPaperData) ? '次数已用完' : (isMakeupPaper(previewPaperData) ? '去补考' : ((attemptCountMap[String(previewPaperData.paperId)] || 0) > 0 ? '再次考试' : '开始考试'))) }}
          </el-button>
          <el-button v-if="previewPaperData.paperId" plain @click="previewOpen = false">关闭预览</el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="recordDetailOpen" title="作答详情" width="min(1360px, 96vw)" class="exam-record-detail-dialog" append-to-body>
      <section class="record-detail-hero">
        <div class="record-detail-hero__main">
          <div class="record-detail-hero__eyebrow">答卷概览</div>
          <h3 class="record-detail-hero__title">{{ currentRecordDetail.record?.paperName || '未命名试卷' }}</h3>
          <p class="record-detail-hero__desc">
            {{ currentRecordDetail.allowViewAnswerAnalysis ? '这里展示本次答卷的总体成绩、题型表现与逐题解析。' : '这里展示本次答卷的总体信息，答案与解析未开放。' }}
          </p>
        </div>
        <div class="record-detail-hero__stats">
          <div class="record-detail-hero__stat">
            <span>得分</span>
            <strong>{{ currentRecordDetail.allowViewScoreSummary ? (currentRecordDetail.record?.score || 0) : '暂不开放' }}</strong>
          </div>
          <div class="record-detail-hero__stat">
            <span>正确率</span>
            <strong>{{ currentRecordDetail.allowViewScoreSummary ? `${currentRecordDetail.record?.correctRate || 0}%` : '暂不开放' }}</strong>
          </div>
          <div class="record-detail-hero__stat">
            <span>状态</span>
            <strong>{{ examStatusLabel(currentRecordDetail.record?.examStatus) }}</strong>
          </div>
          <div class="record-detail-hero__stat">
            <span>记录类型</span>
            <strong>{{ isWrongRetryPaper(currentRecordDetail.record) ? '错题回练' : '正式考试' }}</strong>
          </div>
        </div>
      </section>
      <div class="record-detail-actions mt20">
        <el-button type="primary" @click="openRecordSessionResult(currentRecordDetail.record)">查看完整答卷</el-button>
      </div>
      <section v-if="currentRecordDetail.allowViewScoreSummary" class="record-detail-insights">
        <div class="record-detail-insights__card">
          <span>总题数</span>
          <strong>{{ recordAnswerCount }}</strong>
        </div>
        <div class="record-detail-insights__card">
          <span>答对题数</span>
          <strong>{{ recordCorrectCount }}</strong>
        </div>
        <div class="record-detail-insights__card">
          <span>错题数</span>
          <strong>{{ recordWrongCount }}</strong>
        </div>
        <div class="record-detail-insights__card">
          <span>子卷数</span>
          <strong>{{ currentRecordDetail.subPaperStats?.length || 0 }}</strong>
        </div>
      </section>
      <el-table v-if="currentRecordDetail.allowViewScoreSummary" :data="currentRecordDetail.subPaperStats || []" border class="mt20">
        <el-table-column prop="paperName" label="子试卷" min-width="180" />
        <el-table-column label="层级" width="100">
          <template #default="scope">{{ paperLevelLabel(scope.row.paperLevel) }}</template>
        </el-table-column>
        <el-table-column label="模式" width="100">
          <template #default="scope">{{ answerModeLabel(scope.row.answerMode) }}</template>
        </el-table-column>
        <el-table-column prop="submittedCount" label="已提交" width="90" />
        <el-table-column prop="skippedCount" label="跳过" width="90" />
        <el-table-column prop="avgScore" label="平均得分" width="110" />
        <el-table-column prop="avgCorrectRate" label="平均正确率" width="120" />
      </el-table>
      <el-alert v-else type="info" :closable="false" class="mt20" title="当前试卷暂未开放总成绩查看。" />
      <section v-if="currentRecordDetail.allowViewAnswerAnalysis && groupedRecordAnswers.length" class="record-detail-type-summary mt20">
        <div v-for="group in groupedRecordAnswers" :key="group.questionType" class="record-detail-type-summary__card">
          <div class="record-detail-type-summary__title">{{ questionTypeLabel(group.questionType) }}</div>
          <div class="record-detail-type-summary__meta">{{ group.answers.length }} 题</div>
          <div class="record-detail-type-summary__value">正确 {{ group.correctCount }} 题</div>
        </div>
      </section>
      <el-collapse v-if="currentRecordDetail.allowViewAnswerAnalysis" class="mt20">
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
const recordAnswerCount = computed(() => (currentRecordDetail.value.answers || []).length)
const recordCorrectCount = computed(() => (currentRecordDetail.value.answers || []).filter((item: any) => item.isCorrect === '1').length)
const recordWrongCount = computed(() => Math.max(0, recordAnswerCount.value - recordCorrectCount.value))
const attemptCountMap = computed(() =>
  records.value.reduce((acc: Record<string, number>, item: any) => {
    const key = String(item.paperId || '')
    if (!key) return acc
    acc[key] = (acc[key] || 0) + 1
    return acc
  }, {}),
)
const previewQuestionCount = computed(() => (previewPaperData.value.questions || []).length)
const previewQuestionGroups = computed(() => {
  const groups = new Map<string, { type: string; totalScore: number; questions: any[] }>()
  ;(previewPaperData.value.questions || []).forEach((item: any, index: number) => {
    const type = String(item?.question?.questionType || 'unknown')
    if (!groups.has(type)) {
      groups.set(type, { type, totalScore: 0, questions: [] })
    }
    const group = groups.get(type)!
    const normalizedItem = {
      ...item,
      order: index + 1,
    }
    group.questions.push(normalizedItem)
    group.totalScore += Number(item?.score || 0)
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

function paperLevelLabel(level?: string) {
  return ({ MAIN: '主卷', SUB: '子卷' } as Record<string, string>)[String(level || '').toUpperCase()] || level || '-'
}

function answerModeLabel(modeValue?: string) {
  return ({ AUTO: '自动', MANUAL: '手动', ADAPTIVE: '自适应', REQUIRED: '必答', OPTIONAL: '选答' } as Record<string, string>)[String(modeValue || '').toUpperCase()] || modeValue || '-'
}

function attemptSummary(paper: any) {
  const maxAttemptCount = Number(paper?.maxAttemptCount || 0)
  if (maxAttemptCount <= 0) return '不限'
  const usedCount = attemptCountMap.value[String(paper?.paperId || '')] || 0
  return `${usedCount}/${maxAttemptCount}`
}

function isPaperAttemptLimitReached(paper: any) {
  const maxAttemptCount = Number(paper?.maxAttemptCount || 0)
  if (maxAttemptCount <= 0) return false
  const usedCount = attemptCountMap.value[String(paper?.paperId || '')] || 0
  return usedCount >= maxAttemptCount
}

function isMakeupPaper(paper: any) {
  const maxAttemptCount = Number(paper?.maxAttemptCount || 0)
  const usedCount = attemptCountMap.value[String(paper?.paperId || '')] || 0
  return maxAttemptCount > 0 && usedCount > 0 && usedCount < maxAttemptCount
}

function isWrongRetryPaper(paper: any) {
  const name = String(paper?.paperName || '')
  return name.includes('错题回练')
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

function openPreviewPage(row: any) {
  if (ongoingRecordMap.value[row.paperId]) {
    ElMessage.warning('该试卷已有未完成考试，请直接继续作答')
    return
  }
  router.push(`/student/exams/preview/${row.paperId}`)
}

async function beginExam(row: any) {
  if (startingPaperId.value === row.paperId) return
  if (!ongoingRecordMap.value[row.paperId] && isPaperAttemptLimitReached(row)) {
    ElMessage.warning('该试卷可参加次数已用完')
    return
  }
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
  const allowViewScoreSummary = paperData.allowViewScore !== '0'
  const allowViewAnswerAnalysis = paperData.allowReviewAnalysis === '1'
  currentRecordDetail.value = {
    ...recordData,
    paper: paperData,
    allowViewScoreSummary,
    allowViewAnswerAnalysis,
    subPaperStats: recordData.subPaperStats || [],
    answers: (recordData.answers || []).map((item: any) => ({
      ...item,
      standardAnswer: allowViewAnswerAnalysis ? item.standardAnswer : '当前试卷未开放标准答案回看',
      analysis: allowViewAnswerAnalysis ? item.analysis : '当前试卷未开放解析回看',
    })),
  }
  recordDetailOpen.value = true
}

function openRecordSessionResult(row: any) {
  if (!row?.recordId) return
  recordDetailOpen.value = false
  router.push({
    path: `/student/exams/session/${row.recordId}`,
    query: {
      paperId: String(row.paperId || ''),
      mode: 'result',
    },
  })
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
      openPreviewPage(matchedPaper)
    }
  }
  if (route.query.resume === '1' && latestOngoingRecord.value) {
    resumeExam(latestOngoingRecord.value)
  }
})
</script>

<style scoped>
.custom-table {
  --el-table-border-color: #e2e8f0;
  --el-table-header-bg-color: #f8fafc;
}

.exam-page {
  padding: 24px;
}

.exam-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(320px, 0.8fr);
  gap: 24px;
  padding: 32px;
  border-radius: 16px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px -2px rgba(15, 23, 42, 0.05);
  position: relative;
}

.exam-hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3b82f6 0%, #60a5fa 100%);
  border-radius: 16px 16px 0 0;
}

.hero-content {
  display: flex;
  flex-direction: column;
}

.hero-eyebrow {
  display: inline-flex;
  align-self: flex-start;
  padding: 6px 14px;
  border-radius: 999px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 16px;
  letter-spacing: 0.5px;
}

.hero-title {
  font-size: 28px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 12px;
  line-height: 1.3;
}

.hero-desc {
  font-size: 14px;
  line-height: 1.6;
  color: #64748b;
  max-width: 800px;
  margin: 0;
}

.hero-actions {
  margin-top: 24px;
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.hero-actions .el-button {
  padding: 10px 20px;
  font-size: 14px;
  border-radius: 6px;
  height: auto;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.hero-metric {
  background: #f8fafc;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.hero-metric:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.04);
}

.hero-metric span {
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.hero-metric strong {
  font-size: 28px;
  color: #3b82f6;
  font-weight: 800;
  line-height: 1;
}

.exam-kpis {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.exam-kpi-card {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  padding: 20px;
  display: flex;
  align-items: flex-start;
  transition: all 0.2s ease;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.02);
}

.exam-kpi-card:hover {
  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.06);
  transform: translateY(-2px);
  border-color: #cbd5e1;
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
  font-size: 13px;
  color: #64748b;
  font-weight: 600;
}

.kpi-value {
  font-size: 28px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1;
}

.kpi-value.text-primary {
  color: #3b82f6;
}

.kpi-sub {
  font-size: 13px;
  color: #94a3b8;
}

.exam-section-card {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 32px;
  box-shadow: 0 4px 20px -2px rgba(15, 23, 42, 0.05);
}

.tabs-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e2e8f0;
}

.section-title {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
}

.custom-segmented {
  --el-segmented-item-selected-bg-color: #3b82f6;
  --el-segmented-item-selected-color: #ffffff;
  padding: 4px;
  border-radius: 8px;
  background-color: #f1f5f9;
}

.exam-workbench {
  display: grid;
  grid-template-columns: minmax(0, 1.8fr) minmax(340px, 1fr);
  gap: 24px;
}

.exam-panel {
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.exam-panel--side {
  align-self: start;
  background: #f8fafc;
  border-radius: 16px;
  padding: 24px;
  border: 1px solid #f1f5f9;
}

.exam-ongoing-alert {
  margin-bottom: 24px;
  border-radius: 12px;
  border: 1px solid #fde68a;
  background-color: #fffbeb;
}

.exam-ongoing-alert__content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  color: #92400e;
  font-weight: 500;
}

.exam-panel__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.exam-panel__title {
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
}

.exam-panel__subtitle {
  margin-top: 8px;
  font-size: 14px;
  color: #64748b;
}

.exam-paper-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.exam-paper-card {
  padding: 24px;
  border-radius: 16px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.02);
  display: flex;
  flex-direction: column;
}

.exam-paper-card:hover {
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
  transform: translateY(-2px);
  border-color: #cbd5e1;
}

.exam-paper-card__head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.exam-paper-card__badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.exam-paper-card__title {
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.4;
}

.exam-paper-card__meta {
  margin-top: 16px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.meta-item {
  display: inline-flex;
  align-items: center;
}

.meta-divider {
  width: 1px;
  height: 14px;
  background-color: #cbd5e1;
  margin: 0 12px;
}

.exam-paper-card__stats {
  margin-top: 16px;
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  color: #64748b;
  font-size: 14px;
  background: #f8fafc;
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #f1f5f9;
}

.stat-item {
  display: inline-flex;
  align-items: baseline;
  gap: 6px;
}

.stat-value {
  font-size: 16px;
  color: #0f172a;
  font-weight: 800;
}

.exam-paper-card__actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.exam-paper-card__actions .el-button {
  padding: 10px 20px;
  border-radius: 8px;
  height: auto;
}

.exam-paper-card__resume {
  margin-top: 16px;
  font-size: 14px;
  color: #d97706;
  background: #fffbeb;
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #fef3c7;
  font-weight: 500;
}

.exam-runtime-list {
  display: grid;
  gap: 16px;
}

.exam-runtime-card {
  padding: 24px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.02);
}

.exam-runtime-card:hover {
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
  transform: translateY(-2px);
  border-color: #cbd5e1;
}

.exam-runtime-card__title {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
}

.exam-runtime-card__meta {
  margin-top: 12px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.exam-runtime-card__stats {
  margin-top: 16px;
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  color: #64748b;
  font-size: 14px;
  background: #f8fafc;
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #f1f5f9;
}

.exam-runtime-card__actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.preview-head,
.exam-dialog-head {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  font-weight: 600;
  color: #303133;
  font-size: 15px;
  background: #fafafa;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.record-detail-actions {
  display: flex;
  justify-content: flex-end;
}

.exam-record-detail-dialog :deep(.el-dialog__body) {
  max-height: calc(90vh - 110px);
  overflow: auto;
}

.record-detail-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(320px, 0.9fr);
  gap: 20px;
  padding: 22px 24px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fbff 0%, #ffffff 100%);
  border: 1px solid #dbeafe;
}

.record-detail-hero__eyebrow {
  display: inline-flex;
  padding: 4px 12px;
  border-radius: 999px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
}

.record-detail-hero__title {
  margin: 14px 0 10px;
  color: #0f172a;
  font-size: 24px;
  font-weight: 800;
  line-height: 1.35;
}

.record-detail-hero__desc {
  margin: 0;
  color: #64748b;
  font-size: 14px;
  line-height: 1.8;
}

.record-detail-hero__stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.record-detail-hero__stat {
  padding: 16px;
  border-radius: 10px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.record-detail-hero__stat span {
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
}

.record-detail-hero__stat strong {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.record-detail-insights {
  margin-top: 20px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.record-detail-insights__card {
  padding: 16px 18px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.record-detail-insights__card span {
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
}

.record-detail-insights__card strong {
  color: #0f172a;
  font-size: 22px;
  font-weight: 700;
}

.record-detail-type-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.record-detail-type-summary__card {
  padding: 14px 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
}

.record-detail-type-summary__title {
  color: #0f172a;
  font-size: 15px;
  font-weight: 700;
}

.record-detail-type-summary__meta {
  margin-top: 8px;
  color: #64748b;
  font-size: 12px;
}

.record-detail-type-summary__value {
  margin-top: 6px;
  color: #2563eb;
  font-size: 16px;
  font-weight: 700;
}

.exam-preview-shell {
  display: flex;
  flex-direction: column;
}

.exam-preview-summary {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(320px, 0.9fr);
  gap: 20px;
  padding: 24px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fbff 0%, #ffffff 100%);
  border: 1px solid #dbeafe;
}

.exam-preview-summary__eyebrow {
  display: inline-flex;
  padding: 4px 12px;
  border-radius: 999px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
}

.exam-preview-summary__title {
  margin: 14px 0 10px;
  color: #0f172a;
  font-size: 24px;
  line-height: 1.35;
  font-weight: 800;
}

.exam-preview-summary__desc {
  margin: 0;
  color: #64748b;
  line-height: 1.8;
  font-size: 14px;
}

.exam-preview-summary__stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.exam-preview-stat {
  padding: 16px;
  border-radius: 10px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.exam-preview-stat span {
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
}

.exam-preview-stat strong {
  color: #0f172a;
  font-size: 18px;
  line-height: 1.3;
  font-weight: 700;
}

.exam-preview-outline__title {
  color: #0f172a;
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 14px;
}

.exam-preview-outline__grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.exam-preview-outline__card {
  padding: 16px 18px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.03);
}

.exam-preview-outline__type {
  color: #0f172a;
  font-size: 15px;
  font-weight: 700;
}

.exam-preview-outline__meta {
  margin-top: 8px;
  color: #64748b;
  font-size: 13px;
}

.exam-preview-outline__score {
  margin-top: 6px;
  color: #2563eb;
  font-size: 20px;
  font-weight: 800;
}

.exam-preview-group + .exam-preview-group {
  margin-top: 24px;
}

.exam-preview-group__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  gap: 12px;
}

.exam-preview-group__title {
  color: #0f172a;
  font-size: 17px;
  font-weight: 700;
}

.exam-preview-group__meta {
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
}

.exam-preview-question-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.exam-preview-question {
  padding: 18px 20px;
  border-radius: 14px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.03);
}

.exam-preview-question__top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.exam-preview-question__index {
  color: #334155;
  font-size: 13px;
  font-weight: 700;
}

.exam-preview-question__score {
  color: #2563eb;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  border-radius: 999px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 700;
}

.exam-preview-question__stem {
  margin-top: 14px;
  color: #0f172a;
  font-size: 15px;
  line-height: 1.8;
}

.exam-preview-question__options {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.exam-preview-option {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.exam-preview-option__key {
  color: #2563eb;
  font-weight: 700;
  flex-shrink: 0;
}

.exam-preview-option__content {
  color: #334155;
  line-height: 1.6;
}

.exam-preview-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.exam-dialog-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 72vh;
  overflow: auto;
}

.exam-question-card {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fff;
}

.exam-question-card__top {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.exam-question-title {
  font-size: 15px;
  font-weight: 600;
  line-height: 1.6;
  color: #303133;
}

.exam-question-score {
  color: #909399;
  font-size: 14px;
  white-space: nowrap;
}

.exam-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

@media (max-width: 1200px) {
  .exam-kpis {
    grid-template-columns: repeat(2, 1fr);
  }

  .exam-preview-summary {
    grid-template-columns: 1fr;
  }

  .record-detail-hero {
    grid-template-columns: 1fr;
  }

  .record-detail-insights {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1100px) {
  .exam-workbench {
    grid-template-columns: 1fr;
  }
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
