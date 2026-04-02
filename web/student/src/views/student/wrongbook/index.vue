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
        <div class="section-header wrongbook-section-header">
          <div class="section-title-block">
            <div class="section-title">错题列表</div>
            <div class="section-subtitle">优先处理未掌握、高频、近期重复出错的题目，并支持直接生成回练卷。</div>
          </div>
          <div class="wrongbook-toolbar">
            <div class="wrongbook-toolbar__meta">
              <span>{{ displayWrongs.length }} 条结果</span>
              <span>{{ selectedWrongCount }} 题已选</span>
            </div>
            <div class="wrongbook-toolbar__actions">
              <el-button plain :disabled="!canOpenSelectedRetry" @click="openRetryDialog('selected')">已选回练</el-button>
              <el-button type="primary" plain @click="openRetryDialog('smart')">智能回练</el-button>
            </div>
          </div>
        </div>

        <el-form :model="filters" class="wrongbook-filters" @submit.prevent>
          <div class="wrongbook-filters__grid">
            <el-form-item label="课程">
              <el-select v-model="filters.courseId" clearable filterable style="width: 100%">
                <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="题库">
              <el-select v-model="filters.catalogId" clearable filterable style="width: 100%">
                <el-option v-for="item in catalogOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="题型">
              <el-select v-model="filters.questionType" clearable style="width: 100%">
                <el-option v-for="item in questionTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="知识点">
              <el-select v-model="filters.knowledgePointId" clearable filterable placeholder="按知识点筛选" style="width: 100%">
                <el-option v-for="item in filteredListKnowledgePointOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="掌握状态">
              <el-select v-model="filters.masteryStatus" clearable style="width: 100%">
                <el-option label="未掌握" value="0" />
                <el-option label="已掌握" value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="关键词">
              <el-input v-model="filters.keyword" placeholder="搜索题干 / 题目ID" style="width: 100%" @keyup.enter="handleSearch" />
            </el-form-item>
          </div>
          <div class="wrongbook-filters__actions">
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </div>
        </el-form>

        <el-table
          v-loading="loading"
          :data="displayWrongs"
          @selection-change="handleWrongSelectionChange"
          @row-click="selectWrong"
          class="custom-table"
          highlight-current-row
          row-key="id"
        >
          <el-table-column type="selection" width="52" />
          <el-table-column label="题目" min-width="360" show-overflow-tooltip>
            <template #default="scope">
              <div class="question-summary">
                <div class="question-summary__meta">
                  <span class="question-summary__id">#{{ scope.row.questionId }}</span>
                  <el-tag effect="plain" size="small">{{ questionTypeLabel(scope.row.questionType) }}</el-tag>
                </div>
                <div class="question-summary__stem">{{ buildStemPreview(scope.row.stem) }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="学习上下文" min-width="240" show-overflow-tooltip>
            <template #default="scope">
              <div class="context-summary">
                <div class="context-summary__line">
                  <span class="context-summary__label">课程</span>
                  <strong>{{ courseLabel(scope.row.courseId) }}</strong>
                </div>
                <div class="context-summary__line">
                  <span class="context-summary__label">知识点</span>
                  <span>{{ knowledgePointLabel(scope.row.knowledgePointId) }}</span>
                </div>
                <div class="context-summary__line">
                  <span class="context-summary__label">题库</span>
                  <span>{{ catalogLabel(scope.row.catalogId) }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="最近错题" min-width="160">
            <template #default="scope">{{ formatDateTime(scope.row.lastWrongTime) }}</template>
          </el-table-column>
          <el-table-column label="错误次数" width="110" align="center">
            <template #default="scope">
              <el-tag :type="wrongCountTagType(scope.row.wrongCount)" effect="light" size="small">
                {{ scope.row.wrongCount || 0 }} 次
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="掌握状态" width="110" align="center">
            <template #default="scope">
              <el-tag :type="masteryStatusTagType(scope.row.masteryStatus)" effect="light" size="small">
                {{ masteryStatusLabel(scope.row.masteryStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="110" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click.stop="openRowRetry(scope.row)">回练</el-button>
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

    <el-dialog
      v-model="retryOpen"
      title="错题回练"
      width="960px"
      top="6vh"
      class="retry-dialog"
      append-to-body
      destroy-on-close
    >
      <div class="retry-dialog__hero">
        <div class="retry-dialog__intro">
          <div class="retry-dialog__title">生成错题回练卷</div>
          <p class="retry-dialog__desc">{{ retryModeDescription }}</p>
        </div>
        <div class="retry-dialog__stats">
          <div class="retry-stat-card">
            <span>预计题数</span>
            <strong>{{ retryEstimatedQuestionCount }}</strong>
          </div>
          <div class="retry-stat-card">
            <span>预计总分</span>
            <strong>{{ retryEstimatedTotalScore }}</strong>
          </div>
          <div class="retry-stat-card">
            <span>预计及格线</span>
            <strong>{{ retryEstimatedPassScore }}</strong>
          </div>
        </div>
      </div>

      <el-form :model="retryForm" label-position="top" class="retry-form">
        <div class="retry-panel">
          <div class="retry-panel__title">1. 选择回练范围</div>
          <div class="retry-panel__hint">{{ retrySelectionHint }}</div>
          <el-form-item label="筛题模式" class="retry-form-item--compact">
            <el-radio-group v-model="retryForm.pickMode">
              <el-radio-button value="selected">已选错题</el-radio-button>
              <el-radio-button value="smart">智能筛题</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-table
            v-if="retryPreviewWrongs.length"
            :data="retryPreviewWrongs"
            size="small"
            border
            class="retry-preview-table"
            max-height="240"
          >
            <el-table-column label="#" width="56" align="center">
              <template #default="scope">{{ scope.$index + 1 }}</template>
            </el-table-column>
            <el-table-column label="题型" width="100">
              <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
            </el-table-column>
            <el-table-column label="题目摘要" min-width="320" show-overflow-tooltip>
              <template #default="scope">{{ buildStemPreview(scope.row.stem) }}</template>
            </el-table-column>
            <el-table-column label="错误次数" width="96" align="center">
              <template #default="scope">{{ scope.row.wrongCount || 0 }}</template>
            </el-table-column>
            <el-table-column label="掌握状态" width="100" align="center">
              <template #default="scope">{{ masteryStatusLabel(scope.row.masteryStatus) }}</template>
            </el-table-column>
            <el-table-column label="推荐值" width="90" align="center">
              <template #default="scope">{{ scope.row._retryScore }}</template>
            </el-table-column>
          </el-table>
        </div>

        <div class="retry-panel">
          <div class="retry-panel__title">2. 基础设置</div>
          <div class="retry-form-grid">
            <el-form-item label="回练名称">
              <el-input v-model="retryForm.paperName" />
            </el-form-item>
            <el-form-item label="所属课程">
              <el-select
                v-model="retryForm.courseId"
                clearable
                filterable
                placeholder="可不选，生成通用回练卷"
                style="width: 100%"
                popper-class="retry-dialog-popper"
                teleported
              >
                <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="回练时长">
              <div class="retry-duration-field">
                <el-input-number v-model="retryForm.durationMinutes" :min="5" :max="180" style="width: 100%" />
                <el-radio-group v-model="retryForm.durationMinutes" class="retry-duration-radios">
                  <el-radio-button v-for="item in durationPresetOptions" :key="item" :value="item">
                    {{ item }} 分钟
                  </el-radio-button>
                </el-radio-group>
              </div>
            </el-form-item>
            <el-form-item label="生成方式">
              <div class="retry-save-mode">
                <el-radio-group v-model="retryForm.savePaper">
                  <el-radio-button :value="false">仅预览</el-radio-button>
                  <el-radio-button :value="true">保存为专属回练卷</el-radio-button>
                </el-radio-group>
                <p>{{ retryForm.savePaper ? '会保存到“我的考试”，方便后续反复回练。' : '只生成预览结果，不会写入试卷列表。' }}</p>
              </div>
            </el-form-item>
          </div>
        </div>

        <div v-if="retryForm.pickMode === 'smart'" class="retry-panel">
          <div class="retry-panel__title">3. 智能筛题规则</div>
          <div class="retry-form-grid">
            <el-form-item label="知识点">
              <el-select
                v-model="retryForm.smartKnowledgePointId"
                clearable
                filterable
                placeholder="按知识点筛题，可留空"
                style="width: 100%"
                popper-class="retry-dialog-popper"
                teleported
              >
                <el-option v-for="item in filteredKnowledgePointOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="最少错次">
              <el-input-number v-model="retryForm.minWrongCount" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
            <el-form-item label="排序方式">
              <el-select
                v-model="retryForm.sortMode"
                style="width: 100%"
                popper-class="retry-dialog-popper"
                teleported
              >
                <el-option label="按未掌握优先" value="unmastered_first" />
                <el-option label="按最近错题优先" value="recent_wrong_first" />
              </el-select>
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="retryOpen = false">取消</el-button>
        <el-button type="primary" :loading="retryLoading" :disabled="retrySubmitDisabled" @click="submitRetry">生成回练卷</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="retryResultOpen"
      title="回练卷预览"
      width="1080px"
      top="6vh"
      class="retry-preview-dialog"
      append-to-body
      destroy-on-close
    >
      <div class="preview-head">
        <div class="preview-head__item">
          <span>试卷名称</span>
          <strong>{{ retryResult.paperName || '-' }}</strong>
        </div>
        <div class="preview-head__item">
          <span>总分</span>
          <strong>{{ retryResult.totalScore || 0 }}</strong>
        </div>
        <div class="preview-head__item">
          <span>及格线</span>
          <strong>{{ retryResult.passScore || 0 }}</strong>
        </div>
        <div class="preview-head__item">
          <span>时长</span>
          <strong>{{ retryResult.durationMinutes || 0 }} 分钟</strong>
        </div>
        <div class="preview-head__item">
          <span>题数</span>
          <strong>{{ retryResult.questions?.length || 0 }} 题</strong>
        </div>
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
import { computed, onMounted, reactive, ref, watch } from 'vue'
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
const selectedWrongIds = ref<number[]>([])

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
  knowledgePointId: undefined,
  masteryStatus: '',
  keyword: '',
})

const highWrongCount = computed(() =>
  wrongs.value.filter((item: any) => Number(item.wrongCount || 0) >= 3).length,
)
const filteredKnowledgePointOptions = computed(() =>
  knowledgePointOptions.value.filter((item: any) => !retryForm.courseId || String(item.courseId || '') === String(retryForm.courseId)),
)
const filteredListKnowledgePointOptions = computed(() =>
  knowledgePointOptions.value.filter((item: any) => !filters.courseId || String(item.courseId || '') === String(filters.courseId)),
)
const selectedWrongCount = computed(() => selectedWrongIds.value.length)
const canOpenSelectedRetry = computed(() => Boolean(selectedWrongCount.value || selectedWrong.value?.id))

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
const displayWrongs = computed(() => {
  const keyword = String(filters.keyword || '').trim().toLowerCase()
  return [...wrongs.value]
    .filter((item: any) => {
      if (!keyword) return true
      const haystack = [
        item.questionId,
        item.stem,
        questionTypeLabel(item.questionType),
        courseLabel(item.courseId),
        catalogLabel(item.catalogId),
        knowledgePointLabel(item.knowledgePointId),
      ].join(' ').toLowerCase()
      return haystack.includes(keyword)
    })
    .sort((left: any, right: any) => {
      const leftMastery = String(left?.masteryStatus || '0') === '1' ? 1 : 0
      const rightMastery = String(right?.masteryStatus || '0') === '1' ? 1 : 0
      if (leftMastery !== rightMastery) return leftMastery - rightMastery
      const wrongDiff = Number(right?.wrongCount || 0) - Number(left?.wrongCount || 0)
      if (wrongDiff !== 0) return wrongDiff
      const rightTime = new Date(right?.lastWrongTime || 0).getTime()
      const leftTime = new Date(left?.lastWrongTime || 0).getTime()
      if (rightTime !== leftTime) return rightTime - leftTime
      return Number(left?.questionId || 0) - Number(right?.questionId || 0)
    })
})
const retryPreviewWrongs = computed(() => {
  if (retryForm.pickMode === 'smart') {
    return buildSmartRetryCandidates(wrongs.value)
  }
  return getSelectedRetryWrongs(wrongs.value)
})
const retryEstimatedQuestionCount = computed(() => retryPreviewWrongs.value.length)
const retryEstimatedTotalScore = computed(() =>
  retryPreviewWrongs.value.reduce((sum: number, item: any) => sum + resolveRetryQuestionScore(item?.questionType), 0),
)
const retryEstimatedPassScore = computed(() => {
  if (!retryEstimatedTotalScore.value) return 0
  return Math.max(1, Math.round(retryEstimatedTotalScore.value * 0.6))
})
const retryModeDescription = computed(() => {
  if (retryForm.pickMode === 'selected') {
    return '从你勾选的错题或当前查看题目中生成一张更短、更聚焦的回练卷，适合针对性复盘。'
  }
  return '按课程、知识点、错误频次和掌握状态智能筛题，生成一张更适合阶段巩固的回练卷。'
})
const retrySelectionHint = computed(() => {
  if (retryForm.pickMode === 'selected') {
    return retryPreviewWrongs.value.length
      ? `将使用 ${retryPreviewWrongs.value.length} 道已选/当前错题生成回练卷。`
      : '请先在列表中勾选错题，或至少选择当前查看的一题。'
  }
  return retryPreviewWrongs.value.length
    ? `当前智能规则预计命中 ${retryPreviewWrongs.value.length} 道错题，已按掌握状态、错次、近期错误与分散度综合推荐。`
    : '当前智能规则还没有命中错题，请调整知识点或最少错次。'
})
const durationPresetOptions = [20, 30, 45, 60]
const retrySubmitDisabled = computed(() => retryLoading.value || !retryPreviewWrongs.value.length)

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

function buildRetryPaperName(courseId?: number | string) {
  const matchedCourse = courseOptions.value.find((item: any) => String(item.value) === String(courseId || ''))
  const coursePrefix = matchedCourse?.label ? `${matchedCourse.label}-` : ''
  const now = new Date()
  const month = `${now.getMonth() + 1}`.padStart(2, '0')
  const day = `${now.getDate()}`.padStart(2, '0')
  return `${coursePrefix}错题回练卷-${now.getFullYear()}-${month}-${day}`
}

function masteryStatusLabel(status: string) {
  return status === '1' ? '已掌握' : '未掌握'
}

function masteryStatusTagType(status: string) {
  return status === '1' ? 'success' : 'warning'
}

function wrongCountTagType(count: number | string | undefined) {
  const value = Number(count || 0)
  if (value >= 5) return 'danger'
  if (value >= 3) return 'warning'
  return 'info'
}

function formatDateTime(value?: string) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  const hours = `${date.getHours()}`.padStart(2, '0')
  const minutes = `${date.getMinutes()}`.padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day} ${hours}:${minutes}`
}

function buildStemPreview(stem?: string) {
  const text = String(stem || '').replace(/\s+/g, ' ').trim()
  if (!text) return '暂无题干摘要'
  return text.length > 52 ? `${text.slice(0, 52)}...` : text
}

function resolveRetryQuestionScore(questionType?: string) {
  const normalizedType = String(questionType || 'single').toLowerCase()
  if (normalizedType === 'judge') return 5
  if (normalizedType === 'single') return 6
  if (normalizedType === 'multiple' || normalizedType === 'fill') return 8
  if (normalizedType === 'essay') return 12
  if (normalizedType === 'material' || normalizedType === 'case') return 15
  return 10
}

function getSelectedRetryWrongs(source: any[]) {
  const selectedIds = selectedWrongIds.value.length
    ? selectedWrongIds.value
    : (selectedWrong.value?.id ? [selectedWrong.value.id] : [])
  const idSet = new Set(selectedIds.map((item: any) => Number(item)))
  return source.filter((item: any) => idSet.has(Number(item.id)))
}

function estimateRetryQuestionMinutes(questionType?: string) {
  const normalizedType = String(questionType || 'single').toLowerCase()
  if (normalizedType === 'judge') return 1
  if (normalizedType === 'single') return 1
  if (normalizedType === 'multiple' || normalizedType === 'fill') return 2
  if (normalizedType === 'essay') return 4
  if (normalizedType === 'material' || normalizedType === 'case') return 5
  return 2
}

function computeRecentRetryBonus(lastWrongTime?: string, sortMode?: string) {
  if (!lastWrongTime) return 0
  const target = new Date(lastWrongTime).getTime()
  if (!target || Number.isNaN(target)) return 0
  const diffDays = Math.max(0, Math.floor((Date.now() - target) / (1000 * 60 * 60 * 24)))
  const base = Math.max(0, 30 - diffDays)
  return sortMode === 'recent_wrong_first' ? base * 2 : base
}

function computeRetryPriority(item: any, sortMode?: string) {
  const wrongCount = Number(item?.wrongCount || 0)
  const masteryBonus = String(item?.masteryStatus || '0') === '0' ? 45 : 8
  const frequencyBonus = Math.min(48, wrongCount * 12)
  const recencyBonus = computeRecentRetryBonus(item?.lastWrongTime, sortMode)
  const knowledgeBonus = item?.knowledgePointId ? 6 : 0
  return masteryBonus + frequencyBonus + recencyBonus + knowledgeBonus
}

function buildSmartRetryCandidates(source: any[]) {
  const filtered = [...source]
    .filter((item: any) => {
      const courseMatch = !retryForm.courseId || String(item.courseId || '') === String(retryForm.courseId)
      const kpMatch = !retryForm.smartKnowledgePointId || String(item.knowledgePointId || '') === String(retryForm.smartKnowledgePointId)
      const countMatch = Number(item.wrongCount || 0) >= Number(retryForm.minWrongCount || 1)
      return courseMatch && kpMatch && countMatch
    })
    .map((item: any) => ({
      ...item,
      _retryScoreBase: computeRetryPriority(item, retryForm.sortMode),
      _retryMinutes: estimateRetryQuestionMinutes(item?.questionType),
    }))

  const minuteBudget = Math.max(10, Math.round(Number(retryForm.durationMinutes || 45) * 0.75))
  const selected: any[] = []
  let usedMinutes = 0

  while (filtered.length) {
    let bestIndex = -1
    let bestScore = -Infinity

    filtered.forEach((item: any, index: number) => {
      const sameTypeCount = selected.filter((selectedItem) => selectedItem.questionType === item.questionType).length
      const sameKnowledgeCount = selected.filter((selectedItem) =>
        String(selectedItem.knowledgePointId || '') === String(item.knowledgePointId || ''),
      ).length
      const durationPenalty = usedMinutes + item._retryMinutes > minuteBudget && selected.length > 0 ? 50 : 0
      const diversityPenalty = sameTypeCount * 6 + sameKnowledgeCount * 10
      const score = item._retryScoreBase - diversityPenalty - durationPenalty

      if (score > bestScore) {
        bestScore = score
        bestIndex = index
      }
    })

    if (bestIndex < 0) break

    const [best] = filtered.splice(bestIndex, 1)
    if (usedMinutes + best._retryMinutes > minuteBudget && selected.length > 0) {
      if (selected.length >= 3) {
        break
      }
    }
    selected.push({
      ...best,
      _retryScore: Math.max(1, Math.round(bestScore)),
    })
    usedMinutes += best._retryMinutes
    if (selected.length >= 24) break
  }

  return selected
}

function handleSearch() {
  getList()
}

function resetFilters() {
  filters.courseId = undefined
  filters.catalogId = undefined
  filters.questionType = ''
  filters.knowledgePointId = undefined
  filters.masteryStatus = ''
  filters.keyword = ''
  selectedWrongIds.value = []
  getList()
}

function handleWrongSelectionChange(selection: any[]) {
  selectedWrongIds.value = selection.map((item: any) => item.id)
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

function openRowRetry(row: any) {
  if (!row) return
  selectedWrong.value = row
  selectedWrongIds.value = [row.id]
  openRetryDialog('selected')
}

function openRetryDialog(mode: 'selected' | 'smart') {
  if (mode === 'selected' && !canOpenSelectedRetry.value) {
    ElMessage.warning('请先在列表中勾选错题，或选择当前查看的一题')
    return
  }
  retryForm.pickMode = mode
  retryForm.courseId = selectedWrong.value?.courseId || filters.courseId
  retryForm.paperName = buildRetryPaperName(retryForm.courseId)
  retryForm.wrongIds = mode === 'selected'
    ? getSelectedRetryWrongs(wrongs.value).map((item: any) => item.id)
    : []
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
      payload.wrongIds = buildSmartRetryCandidates(wrongs.value).map((item: any) => item.id)
      if (!payload.wrongIds.length) {
        ElMessage.warning('当前条件下没有可回练的错题')
        retryLoading.value = false
        return
      }
    } else if (!payload.wrongIds?.length) {
      payload.wrongIds = getSelectedRetryWrongs(wrongs.value).map((item: any) => item.id)
      if (!payload.wrongIds.length) {
        ElMessage.warning('请先在列表中勾选错题，或选择当前查看的一题')
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

watch(
  () => retryForm.pickMode,
  (value) => {
    if (value === 'selected') {
      retryForm.wrongIds = getSelectedRetryWrongs(wrongs.value).map((item: any) => item.id)
      return
    }
    retryForm.wrongIds = []
  },
)

watch(
  () => retryForm.courseId,
  () => {
    const validKnowledgePointIds = new Set(filteredKnowledgePointOptions.value.map((item: any) => String(item.value)))
    if (retryForm.smartKnowledgePointId && !validKnowledgePointIds.has(String(retryForm.smartKnowledgePointId))) {
      retryForm.smartKnowledgePointId = undefined
    }
  },
)

watch(displayWrongs, (list) => {
  const currentId = selectedWrong.value?.id
  if (!currentId || !list.some((item: any) => item.id === currentId)) {
    selectedWrong.value = list[0] || null
  }
  const validIdSet = new Set(list.map((item: any) => Number(item.id)))
  selectedWrongIds.value = selectedWrongIds.value.filter((item) => validIdSet.has(Number(item)))
}, { immediate: true })
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

.wrongbook-section-header {
  align-items: flex-start;
  gap: 16px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.section-title-block {
  min-width: 0;
}

.section-subtitle {
  margin-top: 8px;
  font-size: 13px;
  line-height: 1.6;
  color: #909399;
}

.wrongbook-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.wrongbook-toolbar__meta {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
}

.wrongbook-toolbar__actions {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.wrongbook-filters {
  background: #fafafa;
  padding: 18px 18px 14px;
  border-radius: 6px;
  margin-bottom: 20px;
  border: 1px solid #eef2f7;
}

.wrongbook-filters__grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 14px 16px;
}

.wrongbook-filters :deep(.el-form-item) {
  margin-bottom: 0;
}

.wrongbook-filters :deep(.el-form-item__label) {
  padding-bottom: 8px;
  font-size: 12px;
  font-weight: 700;
  color: #64748b;
}

.wrongbook-filters__actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
}

.custom-table {
  --el-table-border-color: #ebeef5;
  --el-table-header-bg-color: #fafafa;
}

.question-summary {
  display: grid;
  gap: 8px;
}

.question-summary__meta {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.question-summary__id {
  display: inline-flex;
  align-items: center;
  min-height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 700;
}

.question-summary__stem {
  color: #334155;
  line-height: 1.7;
  word-break: break-word;
}

.context-summary {
  display: grid;
  gap: 6px;
}

.context-summary__line {
  display: grid;
  grid-template-columns: 48px minmax(0, 1fr);
  gap: 8px;
  align-items: start;
  font-size: 13px;
  line-height: 1.5;
  color: #475569;
}

.context-summary__label {
  color: #94a3b8;
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
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
  padding: 18px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: linear-gradient(135deg, #f8fbff 0%, #fffaf0 100%);
}

.preview-head__item {
  padding: 14px 16px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(219, 234, 254, 0.9);
  box-shadow: 0 6px 16px rgba(30, 64, 175, 0.04);
}

.preview-head__item span {
  display: block;
  margin-bottom: 8px;
  color: #64748b;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.preview-head__item strong {
  display: block;
  color: #1e293b;
  font-size: 16px;
  line-height: 1.5;
}

:deep(.retry-dialog .el-dialog),
:deep(.retry-preview-dialog .el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.18);
}

:deep(.retry-dialog .el-dialog__header),
:deep(.retry-preview-dialog .el-dialog__header) {
  padding: 18px 22px 12px;
  margin-right: 0;
  border-bottom: 1px solid #eef2f7;
  background: #fff;
}

:deep(.retry-dialog .el-dialog__title),
:deep(.retry-preview-dialog .el-dialog__title) {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

:deep(.retry-dialog .el-dialog__body) {
  padding: 18px 22px 20px;
  background: #fcfdff;
}

:deep(.retry-preview-dialog .el-dialog__body) {
  padding: 18px 22px 20px;
  background: #fcfdff;
}

:deep(.retry-dialog .el-dialog__footer),
:deep(.retry-preview-dialog .el-dialog__footer) {
  padding: 14px 22px 18px;
  border-top: 1px solid #eef2f7;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(10px);
}

:deep(.retry-dialog .el-dialog__headerbtn),
:deep(.retry-preview-dialog .el-dialog__headerbtn) {
  top: 18px;
  right: 18px;
}

:global(.retry-dialog-popper.el-select__popper),
:global(.retry-dialog-popper.el-popper) {
  z-index: 5602 !important;
}

:deep(.retry-dialog .el-radio-group),
:deep(.retry-preview-dialog .el-radio-group) {
  flex-wrap: wrap;
}

:deep(.retry-dialog .el-input__wrapper),
:deep(.retry-dialog .el-select__wrapper),
:deep(.retry-dialog .el-textarea__inner),
:deep(.retry-dialog .el-input-number),
:deep(.retry-preview-dialog .el-input__wrapper),
:deep(.retry-preview-dialog .el-select__wrapper) {
  border-radius: 10px;
}

:deep(.retry-dialog .el-form-item__label),
:deep(.retry-preview-dialog .el-form-item__label) {
  color: #475569;
  font-weight: 700;
}

:deep(.retry-dialog .el-table),
:deep(.retry-preview-dialog .el-table) {
  border-radius: 12px;
  overflow: hidden;
}

.retry-dialog__intro {
  min-width: 0;
}

.retry-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.retry-form :deep(.el-radio-button__inner) {
  padding: 8px 16px;
  font-weight: 700;
}

.retry-form :deep(.el-input-number) {
  width: 100%;
}

.retry-dialog__hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 14px 16px;
  margin-bottom: 16px;
  border-radius: 12px;
  background: #f8fbff;
  border: 1px solid #dbeafe;
}

.retry-dialog__eyebrow {
  display: inline-flex;
  align-self: flex-start;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(30, 64, 175, 0.08);
  color: #1e40af;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.retry-dialog__title {
  font-size: 16px;
  font-weight: 700;
  color: #1e3a8a;
}

.retry-dialog__desc {
  margin: 4px 0 0;
  color: #475569;
  line-height: 1.6;
  font-size: 13px;
}

.retry-dialog__stats {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.retry-stat-card {
  min-width: 112px;
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(219, 234, 254, 0.9);
  box-shadow: 0 4px 12px rgba(30, 64, 175, 0.05);
}

.retry-stat-card span {
  display: block;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 6px;
}

.retry-stat-card strong {
  display: block;
  font-size: 20px;
  line-height: 1;
  color: #1e3a8a;
}

.retry-form {
  display: grid;
  gap: 16px;
}

.retry-panel {
  padding: 18px 20px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #fff;
}

.retry-panel__title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}

.retry-panel__hint {
  margin-top: 6px;
  margin-bottom: 14px;
  font-size: 13px;
  line-height: 1.6;
  color: #64748b;
}

.retry-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px 18px;
}

.retry-form-item--compact {
  margin-bottom: 0;
}

.retry-preview-table {
  margin-top: 14px;
}

.retry-preview-table :deep(.el-table__cell) {
  padding-top: 8px;
  padding-bottom: 8px;
}

.retry-duration-field {
  display: grid;
  gap: 10px;
}

.retry-duration-radios {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.retry-duration-radios :deep(.el-radio-button__inner) {
  border-radius: 999px;
}

.retry-save-mode {
  display: grid;
  gap: 10px;
}

.retry-save-mode p {
  margin: 0;
  font-size: 12px;
  line-height: 1.6;
  color: #64748b;
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

  .wrongbook-filters__grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .preview-head {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 1200px) {
  .wrongbook-workbench {
    grid-template-columns: 1fr;
  }

  .retry-form-grid {
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

  .wrongbook-filters__grid,
  .retry-dialog__stats,
  .preview-head {
    grid-template-columns: 1fr;
  }

  .wrongbook-section-header,
  .wrongbook-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .retry-dialog__hero {
    flex-direction: column;
    align-items: stretch;
  }

  .wrongbook-toolbar__actions {
    width: 100%;
  }

  .wrongbook-toolbar__actions .el-button {
    flex: 1;
  }
}
</style>
