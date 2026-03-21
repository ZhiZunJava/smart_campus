<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" clearable filterable style="width: 220px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="试卷名称"><el-input v-model="queryParams.paperName" style="width: 240px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="发布状态">
        <el-select v-model="queryParams.publishStatus" clearable style="width: 160px">
          <el-option label="草稿" value="draft" />
          <el-option label="已发布" value="published" />
          <el-option label="已归档" value="archived" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增试卷</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="MagicStick" @click="openAssembleDialog">智能组卷</el-button></el-col>
    </el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="试卷ID" prop="paperId" width="90" />
      <el-table-column label="试卷名称" prop="paperName" min-width="220" />
      <el-table-column label="课程" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          {{ courseLabel(scope.row.courseId) }}
        </template>
      </el-table-column>
      <el-table-column label="试卷类型" width="100">
        <template #default="scope">{{ paperTypeLabel(scope.row.paperType) }}</template>
      </el-table-column>
      <el-table-column label="层级" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.paperLevel === 'SUB' ? 'warning' : 'success'" effect="plain">
            {{ scope.row.paperLevel || 'MAIN' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="总分" prop="totalScore" width="90" />
      <el-table-column label="时长" prop="durationMinutes" width="90" />
      <el-table-column label="发布状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.publishStatus === 'published' ? 'success' : scope.row.publishStatus === 'archived' ? 'info' : 'warning'">
            {{ publishStatusLabel(scope.row.publishStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="420" fixed="right">
        <template #default="scope">
          <div class="table-actions">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="success" icon="Promotion" @click="changePublishStatus(scope.row, 'published')">发布</el-button>
          <el-button link type="warning" icon="FolderOpened" @click="changePublishStatus(scope.row, 'archived')">归档</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="1120px" top="5vh" destroy-on-close>
      <div class="step-nav">
        <button v-for="(item, index) in [{ label: '试卷配置' }, { label: '题型区块' }, { label: '内容校对' }]" :key="item.label" type="button" class="step-nav__item" :class="{ 'is-active': paperStep === index }" @click="paperStep = index">
          <span class="step-nav__index">{{ index + 1 }}</span>
          <span>{{ item.label }}</span>
        </button>
      </div>

      <div v-if="paperStep === 0" class="paper-stage">
        <el-row :gutter="16">
          <el-col :span="8"><el-card shadow="never" class="metric-card"><div class="metric-label">题目数</div><div class="metric-value">{{ totalQuestionCount }}</div></el-card></el-col>
          <el-col :span="8"><el-card shadow="never" class="metric-card"><div class="metric-label">总分</div><div class="metric-value">{{ form.totalScore || 0 }}</div></el-card></el-col>
          <el-col :span="8"><el-card shadow="never" class="metric-card"><div class="metric-label">结构概览</div><div class="metric-sub">{{ sectionSummaryText }}</div></el-card></el-col>
        </el-row>
        <el-form :model="form" label-width="96px" class="mt16">
          <el-row :gutter="16">
            <el-col :span="8"><el-form-item label="试卷名称"><el-input v-model="form.paperName" /></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="课程"><el-select v-model="form.courseId" clearable filterable placeholder="可不选，作为通用试卷"><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="试卷类型"><el-select v-model="form.paperType"><el-option label="固定" value="fixed" /><el-option label="随机" value="random" /><el-option label="自适应" value="adaptive" /></el-select></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="考试时长"><el-input-number v-model="form.durationMinutes" :min="1" :max="300" style="width: 100%" /></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="总分"><el-input-number v-model="form.totalScore" :min="0" :max="1000" style="width: 100%" /></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="及格线"><el-input-number v-model="form.passScore" :min="0" :max="1000" style="width: 100%" /></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="发布状态"><el-select v-model="form.publishStatus"><el-option label="草稿" value="draft" /><el-option label="已发布" value="published" /><el-option label="已归档" value="archived" /></el-select></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="开始时间"><el-date-picker v-model="form.publishStartTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="结束时间"><el-date-picker v-model="form.publishEndTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="阅卷方式"><el-select v-model="form.markingMode"><el-option label="自动判分优先" value="auto_first" /><el-option label="人工复核" value="manual_review" /><el-option label="纯人工阅卷" value="manual_only" /></el-select></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="题目顺序"><el-select v-model="form.questionOrderMode"><el-option label="按配置顺序" value="manual" /><el-option label="题型内随机" value="section_random" /><el-option label="全卷随机" value="all_random" /></el-select></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="回看解析"><el-switch v-model="form.allowReviewAnalysisBool" /></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="考试说明"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item></el-col>
          </el-row>
        </el-form>
        <div class="sub-paper-board">
          <div class="section-toolbar">
            <div>
              <div class="panel-title">子试卷配置</div>
              <div class="panel-subtitle">可为当前主试卷拆分基础卷、提升卷、选答卷等子试卷。</div>
            </div>
            <div class="section-toolbar__actions">
              <el-button type="primary" plain icon="Plus" @click="addSubPaper">新增子试卷</el-button>
            </div>
          </div>
          <div v-for="(subPaper, subIndex) in form.subPapers" :key="subPaper.localId" class="section-card">
            <div class="section-card__head">
              <div>
                <div class="section-card__title">{{ subPaper.paperName || `子试卷 ${Number(subIndex) + 1}` }}</div>
                <div class="section-card__meta">
                  {{ subPaper.answerMode === 'OPTIONAL' ? '选答' : '必答' }} · 权重 {{ subPaper.paperWeight }} · {{ subPaper.questions.length }} 题
                </div>
              </div>
              <div class="section-card__actions">
                <el-button link type="primary" icon="Plus" @click="openSubPaperQuestionSelector(Number(subIndex))">添加题目</el-button>
                <el-button link type="danger" icon="Delete" @click="removeSubPaper(Number(subIndex))">删除子试卷</el-button>
              </div>
            </div>
            <el-row :gutter="16" class="mb16">
              <el-col :span="8"><el-input v-model="subPaper.paperName" placeholder="如 基础卷 / 提升卷 / 选答卷" /></el-col>
              <el-col :span="4">
                <el-select v-model="subPaper.answerMode">
                  <el-option label="必答" value="REQUIRED" />
                  <el-option label="选答" value="OPTIONAL" />
                </el-select>
              </el-col>
              <el-col :span="4"><el-input-number v-model="subPaper.paperWeight" :min="0" :max="1000" style="width: 100%" /></el-col>
              <el-col :span="4"><el-input-number v-model="subPaper.sortNo" :min="1" :max="99" style="width: 100%" /></el-col>
              <el-col :span="4"><el-input-number v-model="subPaper.durationMinutes" :min="1" :max="300" style="width: 100%" /></el-col>
            </el-row>
            <el-table :data="subPaper.questions" border>
              <el-table-column label="题目ID" prop="questionId" width="90" />
              <el-table-column label="题型" width="110"><template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template></el-table-column>
              <el-table-column label="题干" min-width="320" show-overflow-tooltip><template #default="scope">{{ stripHtml(scope.row.stem) || '-' }}</template></el-table-column>
              <el-table-column label="分值" width="140">
                <template #default="scope">
                  <el-input-number v-model="scope.row.score" :min="0" :max="100" :step="0.5" controls-position="right" style="width: 100%" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="110">
                <template #default="scope">
                  <el-button link type="danger" icon="Delete" @click="removeSubPaperQuestion(Number(subIndex), Number(scope.$index))">移除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <el-empty v-if="!form.subPapers?.length" description="当前主试卷还没有配置子试卷" />
        </div>
      </div>

      <div v-else-if="paperStep === 1" class="paper-stage">
        <div class="section-toolbar">
          <div>
            <div class="panel-title">题型区块</div>
            <div class="panel-subtitle">先新增单选题、多选题、判断题等区块，再往每个区块里添加题目。</div>
          </div>
          <div class="section-toolbar__actions">
            <el-dropdown @command="handleAddSectionBlock">
              <el-button type="primary" icon="Plus">新增区块</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="item in questionTypeOptions" :key="item.code" :command="item.code">{{ item.label }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        <div v-for="(section, sectionIndex) in paperSections" :key="section.sectionId" class="section-card">
          <div class="section-card__head">
            <div>
              <div class="section-card__title">{{ section.title }}</div>
              <div class="section-card__meta">{{ questionTypeLabel(section.questionType) }} · {{ section.questions.length }} 题 · 小计 {{ sectionSubtotal(section) }} 分</div>
            </div>
            <div class="section-card__actions">
              <el-button link type="primary" icon="Plus" @click="openQuestionSelector(sectionIndex)">添加题目</el-button>
              <el-button link type="danger" icon="Delete" @click="removeSectionBlock(sectionIndex)">删除区块</el-button>
            </div>
          </div>
          <div class="section-score-board mb16">
            <div class="section-score-board__metrics">
              <div class="score-chip">
                <span class="score-chip__label">区块总分</span>
                <el-input-number v-model="section.targetScore" :min="0" :max="1000" :step="1" controls-position="right" style="width: 100%" />
              </div>
              <div class="score-chip">
                <span class="score-chip__label">每题基准分</span>
                <el-input-number v-model="section.defaultScore" :min="0" :max="100" :step="0.5" controls-position="right" style="width: 100%" />
              </div>
              <div class="score-chip score-chip--stat">
                <span class="score-chip__label">当前状态</span>
                <div class="score-chip__value">
                  <el-tag :type="sectionHasCustomScores(section) ? 'warning' : 'success'" effect="plain">
                    {{ sectionHasCustomScores(section) ? '已自定义分值' : '分值统一' }}
                  </el-tag>
                </div>
              </div>
            </div>
            <div class="section-score-board__actions">
              <el-button plain @click="applySectionDefaultScore(sectionIndex)">统一套用每题分</el-button>
              <el-button type="primary" plain @click="distributeSectionScore(sectionIndex)">按区块总分自动分配</el-button>
            </div>
          </div>
          <el-row :gutter="16" class="mb16">
            <el-col :span="10"><el-input v-model="section.title" placeholder="如 单选题 / 多选题 / 判断题" /></el-col>
            <el-col :span="14"><el-input v-model="section.instructions" placeholder="可填写说明，如 每题 5 分，共 10 题；也可留空由系统自动提示" /></el-col>
          </el-row>
          <el-table :data="section.questions" border>
            <el-table-column label="题目ID" prop="questionId" width="90" />
            <el-table-column label="题干" min-width="340" show-overflow-tooltip><template #default="scope">{{ stripHtml(scope.row.stem) || '-' }}</template></el-table-column>
            <el-table-column label="分值" width="140">
              <template #default="scope">
                <el-input-number v-model="scope.row.score" :min="0" :max="100" :step="0.5" controls-position="right" style="width: 100%" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="110"><template #default="scope"><el-button link type="danger" icon="Delete" @click="removeSectionQuestion(sectionIndex, scope.$index)">移除</el-button></template></el-table-column>
          </el-table>
        </div>
        <el-empty v-if="!paperSections.length" description="先新增题型区块后再加题" />
      </div>

      <div v-else class="paper-stage">
        <el-collapse>
          <el-collapse-item v-for="section in paperSections" :key="section.sectionId" :title="`${section.title} ｜ ${section.questions.length} 题 ｜ ${sectionSubtotal(section)} 分`">
            <el-table :data="section.questions" border>
              <el-table-column label="题目ID" prop="questionId" width="90" />
              <el-table-column label="题型" width="110"><template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template></el-table-column>
              <el-table-column label="题干" min-width="360" show-overflow-tooltip><template #default="scope">{{ stripHtml(scope.row.stem) || '-' }}</template></el-table-column>
              <el-table-column label="分值" prop="score" width="90" />
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="paperStep > 0" @click="paperStep--">上一步</el-button>
          <el-button @click="open = false">取消</el-button>
          <el-button v-if="paperStep < 2" type="primary" @click="paperStep++">下一步</el-button>
          <el-button v-else type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="questionSelectorOpen" :title="selectorTitle" width="1100px">
      <el-form :inline="true" :model="questionSelectorQuery" class="mb16">
        <el-form-item label="题型"><el-select v-model="questionSelectorQuery.questionType" clearable style="width: 180px"><el-option v-for="item in questionTypeOptions" :key="item.code" :label="item.label" :value="item.code" /></el-select></el-form-item>
        <el-form-item label="难度"><el-input-number v-model="questionSelectorQuery.difficultyLevel" :min="1" :max="5" /></el-form-item>
        <el-form-item label="题干"><el-input v-model="questionSelectorQuery.stem" style="width: 260px" /></el-form-item>
        <el-form-item><el-button type="primary" icon="Search" @click="loadQuestionBank">筛选</el-button></el-form-item>
      </el-form>
      <el-table v-loading="questionSelectorLoading" :data="questionBankList" @selection-change="handleQuestionBankSelection">
        <el-table-column type="selection" width="55" />
        <el-table-column label="题目ID" prop="questionId" width="90" />
        <el-table-column label="题型" width="110"><template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template></el-table-column>
        <el-table-column label="难度" prop="difficultyLevel" width="90" />
        <el-table-column label="题干" min-width="360" show-overflow-tooltip><template #default="scope">{{ stripHtml(scope.row.stem) }}</template></el-table-column>
      </el-table>
      <template #footer><el-button @click="questionSelectorOpen = false">取消</el-button><el-button type="primary" @click="appendSelectedQuestions">加入区块</el-button></template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="试卷详情" width="1100px">
      <div class="detail-header">
        <span>试卷名称：{{ detail.paperName || '-' }}</span>
        <span>总分：{{ detail.totalScore || 0 }}</span>
        <span>时长：{{ detail.durationMinutes || 0 }} 分钟</span>
        <span>发布状态：{{ publishStatusLabel(detail.publishStatus) }}</span>
        <span>课程：{{ courseLabel(detail.courseId) }}</span>
      </div>
      <el-table :data="detail.subPapers || []" border class="mt16">
        <el-table-column prop="paperName" label="子试卷" min-width="180" />
        <el-table-column prop="answerMode" label="作答模式" width="100" />
        <el-table-column prop="paperWeight" label="权重" width="90" />
        <el-table-column prop="sortNo" label="排序" width="90" />
        <el-table-column prop="durationMinutes" label="时长" width="90" />
        <el-table-column label="题量" width="90">
          <template #default="scope">{{ scope.row.questions?.length || 0 }}</template>
        </el-table-column>
      </el-table>
      <el-table :data="detail.questions || []" border class="mt16">
        <el-table-column label="排序" prop="sortNo" width="90" />
        <el-table-column label="题目ID" prop="questionId" width="90" />
        <el-table-column label="题型" min-width="100"><template #default="scope">{{ questionTypeLabel(scope.row.question?.questionType) }}</template></el-table-column>
        <el-table-column label="分值" prop="score" width="90" />
        <el-table-column label="题干" min-width="360" show-overflow-tooltip><template #default="scope">{{ stripHtml(scope.row.question?.stem) || '-' }}</template></el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="assembleOpen" title="智能组卷" width="1040px" destroy-on-close>
      <el-row :gutter="16" class="mb16">
        <el-col :span="8"><el-card shadow="never" class="metric-card"><div class="metric-label">规则数</div><div class="metric-value">{{ assembleForm.rules.length }}</div></el-card></el-col>
        <el-col :span="8"><el-card shadow="never" class="metric-card"><div class="metric-label">预计题量</div><div class="metric-value">{{ assembleQuestionCount }}</div></el-card></el-col>
        <el-col :span="8"><el-card shadow="never" class="metric-card"><div class="metric-label">预计总分</div><div class="metric-value">{{ assembleTotalScore }}</div></el-card></el-col>
      </el-row>
      <div class="assemble-preset mb16">
        <div class="panel-title">快速模板</div>
        <div class="preset-list">
          <el-button plain @click="applyAssemblePreset('basic')">基础练习卷</el-button>
          <el-button plain @click="applyAssemblePreset('midterm')">期中测验卷</el-button>
          <el-button plain @click="applyAssemblePreset('objective')">客观题强化卷</el-button>
        </div>
      </div>
      <el-form :model="assembleForm" label-width="96px" class="mb16">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="试卷名称"><el-input v-model="assembleForm.paperName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="课程"><el-select v-model="assembleForm.courseId" clearable filterable><el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="试卷类型"><el-select v-model="assembleForm.paperType"><el-option label="随机" value="random" /><el-option label="固定" value="fixed" /><el-option label="自适应" value="adaptive" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="时长"><el-input-number v-model="assembleForm.durationMinutes" :min="1" :max="300" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="发布状态"><el-select v-model="assembleForm.publishStatus"><el-option label="草稿" value="draft" /><el-option label="已发布" value="published" /><el-option label="已归档" value="archived" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="模板"><el-select v-model="assembleForm.strategyTemplateId" clearable filterable @change="applyStrategyTemplate"><el-option v-for="item in strategyTemplateOptions" :key="item.templateId" :label="item.templateName" :value="item.templateId" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="保存试卷"><el-switch v-model="assembleForm.savePaper" /></el-form-item></el-col>
          <el-col :span="16"><el-form-item label="说明"><el-input v-model="assembleForm.remark" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div class="rule-panel">
        <div class="rule-panel__header">
          <div><div class="panel-title">组卷规则</div><div class="panel-subtitle">按题型、数量、难度、知识点和分值自动抽题。</div></div>
          <el-button type="primary" plain icon="Plus" @click="addRule">新增规则</el-button>
        </div>
        <div v-for="(rule, index) in assembleForm.rules" :key="`rule-${index}`" class="rule-card">
          <div class="rule-card__head"><div class="rule-card__title">规则 {{ Number(index) + 1 }}</div><el-button link type="danger" icon="Delete" @click="removeRule(Number(index))">删除</el-button></div>
          <el-row :gutter="16">
            <el-col :span="6"><el-form-item label="题型" label-width="50px"><el-select v-model="rule.questionType"><el-option v-for="item in questionTypeOptions" :key="item.code" :label="item.label" :value="item.code" /></el-select></el-form-item></el-col>
            <el-col :span="4"><el-form-item label="数量" label-width="50px"><el-input-number v-model="rule.count" :min="1" :max="100" /></el-form-item></el-col>
            <el-col :span="4"><el-form-item label="难度" label-width="50px"><el-input-number v-model="rule.difficultyLevel" :min="1" :max="5" /></el-form-item></el-col>
            <el-col :span="6"><el-form-item label="知识点" label-width="60px"><el-select v-model="rule.knowledgePointId" clearable filterable><el-option v-for="item in assembleKnowledgePointOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
            <el-col :span="4"><el-form-item label="分值" label-width="50px"><el-input-number v-model="rule.score" :min="1" :max="100" /></el-form-item></el-col>
          </el-row>
        </div>
      </div>
      <template #footer><el-button @click="assembleOpen = false">取消</el-button><el-button type="primary" :loading="assembleLoading" @click="submitAssemble">开始组卷</el-button></template>
    </el-dialog>

    <el-dialog v-model="assembleResultOpen" title="组卷结果" width="1100px">
      <div class="detail-header">
        <span>试卷名称：{{ assembleResult.paperName || '-' }}</span>
        <span>总分：{{ assembleResult.totalScore || 0 }}</span>
        <span>时长：{{ assembleResult.durationMinutes || 0 }} 分钟</span>
      </div>
      <el-table :data="assembleResult.questions || []" border class="mt16">
        <el-table-column label="排序" prop="sortNo" width="90" />
        <el-table-column label="题目ID" prop="questionId" width="90" />
        <el-table-column label="题型" min-width="100"><template #default="scope">{{ questionTypeLabel(scope.row.question?.questionType) }}</template></el-table-column>
        <el-table-column label="分值" prop="score" width="90" />
        <el-table-column label="题干" min-width="360" show-overflow-tooltip><template #default="scope">{{ stripHtml(scope.row.question?.stem) || '-' }}</template></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addPaperDetail, assemblePaper, delPaperDetail, getPaperDetail, getStrategyTemplate, listPaper, listQuestion, listQuestionTypeMeta, listStrategyTemplate, updatePaperDetail } from '@/api/campus/exam'
import { fetchCourseOptions } from '@/api/campus/options'
import { listKnowledgePoint } from '@/api/campus/teaching'

type PaperSection = {
  sectionId: string
  title: string
  questionType: string
  defaultScore: number
  targetScore: number
  instructions: string
  questions: any[]
}

type SubPaperBlock = {
  localId: string
  paperId?: number
  paperName: string
  answerMode: string
  paperWeight: number
  sortNo: number
  durationMinutes: number
  questions: any[]
}

const loading = ref(false)
const total = ref(0)
const open = ref(false)
const assembleOpen = ref(false)
const detailOpen = ref(false)
const questionSelectorOpen = ref(false)
const questionSelectorLoading = ref(false)
const assembleLoading = ref(false)
const assembleResultOpen = ref(false)
const title = ref('')
const paperStep = ref(0)
const dataList = ref<any[]>([])
const detail = ref<any>({})
const questionBankList = ref<any[]>([])
const selectedIds = ref<number[]>([])
const selectedQuestionBankRows = ref<any[]>([])
const selectorSectionIndex = ref(-1)
const selectorSubPaperIndex = ref(-1)
const selectorMode = ref<'section' | 'subPaper'>('section')
const courseOptions = ref<any[]>([])
const questionTypeOptions = ref<any[]>([])
const knowledgePointOptions = ref<any[]>([])
const strategyTemplateOptions = ref<any[]>([])

const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, courseId: undefined, paperName: '', publishStatus: '' })
const form = reactive<any>({})
const paperSections = ref<PaperSection[]>([])
const assembleForm = reactive<any>({})
const assembleResult = reactive<any>({})
const questionSelectorQuery = reactive<any>({ questionType: '', difficultyLevel: 3, stem: '' })

const selectorTitle = computed(() => {
  const section = paperSections.value[selectorSectionIndex.value]
  return section ? `为「${section.title}」添加题目` : '选择题目'
})
const totalQuestionCount = computed(() => paperSections.value.reduce((sum, item) => sum + item.questions.length, 0))
const sectionSummaryText = computed(() => paperSections.value.map((item) => `${item.title}${item.questions.length}题`).join(' / ') || '暂未配置')
const assembleKnowledgePointOptions = computed(() => knowledgePointOptions.value.filter((item: any) => !assembleForm.courseId || item.courseId === assembleForm.courseId))
const assembleQuestionCount = computed(() => (assembleForm.rules || []).reduce((sum: number, item: any) => sum + Number(item.count || 0), 0))
const assembleTotalScore = computed(() => (assembleForm.rules || []).reduce((sum: number, item: any) => sum + Number(item.count || 0) * Number(item.score || 0), 0))

function stripHtml(value: string) { return String(value || '').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim() }
function questionTypeLabel(type: string) { return questionTypeOptions.value.find((item: any) => item.code === type)?.label || type || '-' }
function paperTypeLabel(type: string) { return ({ fixed: '固定', random: '随机', adaptive: '自适应' } as any)[type] || type || '-' }
function publishStatusLabel(status: string) { return ({ draft: '草稿', published: '已发布', archived: '已归档' } as any)[status] || status || '草稿' }
function courseLabel(courseId: number | string | undefined) {
  if (!courseId) return '通用试卷'
  const matched = courseOptions.value.find((item: any) => String(item.value) === String(courseId))
  return matched?.label || `课程 ${courseId}`
}
function createSection(questionType: string): PaperSection { return { sectionId: `${questionType}-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`, title: questionTypeLabel(questionType), questionType, defaultScore: 5, targetScore: 0, instructions: '', questions: [] } }
function createSubPaper(): SubPaperBlock {
  return {
    localId: `sub-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
    paperName: '',
    answerMode: 'REQUIRED',
    paperWeight: 100,
    sortNo: Number((form.subPapers || []).length || 0) + 1,
    durationMinutes: Number(form.durationMinutes || 60),
    questions: [],
  }
}
function safeParseSectionConfig(sectionConfigJson?: string) { try { const parsed = JSON.parse(sectionConfigJson || '[]'); return Array.isArray(parsed) ? parsed : [] } catch { return [] } }
function rebuildFlatQuestionsFromSections() {
  const flattened: any[] = []
  const sectionConfigs: any[] = []
  let globalSort = 1
  paperSections.value.forEach((section) => {
    sectionConfigs.push({
      title: section.title,
      questionType: section.questionType,
      defaultScore: section.defaultScore,
      targetScore: section.targetScore,
      instructions: section.instructions,
      questionIds: section.questions.map((question: any) => question.questionId),
    })
    section.questions.forEach((question: any) => {
      flattened.push({
        id: question.id,
        questionId: question.questionId,
        score: Number(question.score ?? section.defaultScore ?? 0),
        sortNo: Number(question.sortNo || globalSort),
      })
      globalSort += 1
    })
  })
  form.questions = flattened
  form.sectionConfigJson = JSON.stringify(sectionConfigs)
}
function rebuildSectionsFromQuestions(questions: any[] = [], sectionConfigJson?: string) {
  const sectionConfigs = safeParseSectionConfig(sectionConfigJson)
  if (sectionConfigs.length) {
    const sections = sectionConfigs.map((config: any) => ({
        sectionId: `${config.questionType}-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
        title: config.title || questionTypeLabel(config.questionType),
        questionType: config.questionType || 'single',
        defaultScore: Number(config.defaultScore || 5),
        targetScore: Number(config.targetScore || 0),
        instructions: config.instructions || '',
        questions: [] as any[],
      }))
    questions.forEach((item: any) => {
    const section = sections.find((sectionItem) => sectionItem.questionType === item.questionType && !sectionItem.questions.some((q: any) => q.questionId === item.questionId))
      if (section) {
        section.questions.push({
          id: item.id,
          questionId: item.questionId,
          score: Number(item.score || section.defaultScore),
          sortNo: item.sortNo || section.questions.length + 1,
          stem: item.stem || '',
          questionType: item.questionType || section.questionType,
        })
      }
    })
    sections.forEach((section) => {
      section.targetScore = Number(section.targetScore || sectionSubtotal(section))
    })
    paperSections.value = sections
    return
  }
  const sectionMap = new Map<string, PaperSection>()
  questions.forEach((item: any) => {
    const questionType = item.questionType || 'single'
    if (!sectionMap.has(questionType)) sectionMap.set(questionType, createSection(questionType))
    const section = sectionMap.get(questionType)!
    section.questions.push({
      id: item.id,
      questionId: item.questionId,
      score: Number(item.score || section.defaultScore),
      sortNo: item.sortNo || section.questions.length + 1,
      stem: item.stem || '',
      questionType,
    })
  })
  sectionMap.forEach((section) => {
    section.targetScore = Number(sectionSubtotal(section))
  })
  paperSections.value = Array.from(sectionMap.values())
}
function resetFormData() {
  Object.assign(form, {
    paperId: undefined,
    paperName: '',
    courseId: undefined,
    paperType: 'fixed',
    totalScore: 100,
    durationMinutes: 60,
    publishStatus: 'draft',
    publishStartTime: '',
    publishEndTime: '',
    strategyTemplateId: undefined,
    status: '0',
    remark: '',
    passScore: 60,
    markingMode: 'auto_first',
    questionOrderMode: 'manual',
    allowReviewAnalysisBool: true,
    questions: [],
    sectionConfigJson: '[]',
    subPapers: [],
  })
  paperSections.value = []
  paperStep.value = 0
}
function resetAssembleForm() {
  Object.assign(assembleForm, {
    paperName: '',
    courseId: undefined,
    paperType: 'random',
    durationMinutes: 60,
    publishStatus: 'draft',
    strategyTemplateId: undefined,
    status: '0',
    remark: '',
    savePaper: true,
    rules: [{ questionType: 'single', count: 10, difficultyLevel: 3, knowledgePointId: undefined, score: 5 }],
  })
}
function resetQuery() {
  queryParams.pageNum = 1
  queryParams.courseId = undefined
  queryParams.paperName = ''
  queryParams.publishStatus = ''
  getList()
}
async function getList() {
  loading.value = true
  try {
    const res = await listPaper(queryParams)
    dataList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}
function handleSelectionChange(selection: any[]) {
  selectedIds.value = selection.map((item) => item.paperId)
}
function handleAdd() {
  resetFormData()
  title.value = '新增试卷'
  open.value = true
}
async function handleEdit(row: any) {
  const res = await getPaperDetail(row.paperId)
  const data = res.data || {}
  Object.assign(form, {
    paperId: data.paperId,
    paperName: data.paperName,
    courseId: data.courseId,
    paperType: data.paperType || 'fixed',
    totalScore: Number(data.totalScore || 0),
    durationMinutes: data.durationMinutes || 60,
    publishStatus: data.publishStatus || 'draft',
    publishStartTime: data.publishStartTime || '',
    publishEndTime: data.publishEndTime || '',
    strategyTemplateId: data.strategyTemplateId,
    status: data.status || '0',
    remark: data.remark || '',
    passScore: Number(data.passScore || 60),
    markingMode: data.markingMode || 'auto_first',
    questionOrderMode: data.questionOrderMode || 'manual',
    allowReviewAnalysisBool: data.allowReviewAnalysis === '1',
    questions: (data.questions || []).map((item: any) => ({
      id: item.id,
      questionId: item.questionId,
      score: Number(item.score || 0),
      sortNo: item.sortNo || 1,
      stem: item.question?.stem || '',
      questionType: item.question?.questionType || '',
    })),
    sectionConfigJson: data.sectionConfigJson || '[]',
    subPapers: (data.subPapers || []).map((item: any, index: number) => ({
      localId: `sub-${item.paperId || index}-${Date.now()}`,
      paperId: item.paperId,
      paperName: item.paperName || '',
      answerMode: item.answerMode || 'REQUIRED',
      paperWeight: Number(item.paperWeight || 100),
      sortNo: Number(item.sortNo || index + 1),
      durationMinutes: Number(item.durationMinutes || data.durationMinutes || 60),
      questions: (item.questions || []).map((question: any) => ({
        id: question.id,
        questionId: question.questionId,
        score: Number(question.score || 0),
        sortNo: question.sortNo || 1,
        stem: question.question?.stem || '',
        questionType: question.question?.questionType || '',
      })),
    })),
  })
  rebuildSectionsFromQuestions(form.questions, form.sectionConfigJson)
  paperStep.value = 0
  title.value = '编辑试卷'
  open.value = true
}
async function handleView(row: any) {
  const res = await getPaperDetail(row.paperId)
  detail.value = res.data || {}
  detailOpen.value = true
}
async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确认删除试卷「${row.paperName}」吗？`, '提示', { type: 'warning' })
  await delPaperDetail(row.paperId)
  ElMessage.success('删除成功')
  getList()
}
async function changePublishStatus(row: any, publishStatus: string) {
  const res = await getPaperDetail(row.paperId)
  const data = res.data || {}
  await updatePaperDetail({
    paperId: data.paperId,
    paperName: data.paperName,
    courseId: data.courseId,
    paperType: data.paperType,
    totalScore: data.totalScore,
    durationMinutes: data.durationMinutes,
    publishStatus,
    publishStartTime: publishStatus === 'published' ? new Date().toISOString().slice(0, 19).replace('T', ' ') : data.publishStartTime,
    publishEndTime: data.publishEndTime,
    strategyTemplateId: data.strategyTemplateId,
    passScore: data.passScore,
    markingMode: data.markingMode,
    questionOrderMode: data.questionOrderMode,
    allowReviewAnalysis: data.allowReviewAnalysis,
    sectionConfigJson: data.sectionConfigJson,
    paperLevel: data.paperLevel || 'MAIN',
    answerMode: data.answerMode || 'REQUIRED',
    paperWeight: Number(data.paperWeight || 100),
    status: data.status,
    remark: data.remark,
    questions: (data.questions || []).map((item: any) => ({ id: item.id, questionId: item.questionId, score: Number(item.score || 0), sortNo: item.sortNo || 1 })),
    subPapers: (data.subPapers || []).map((item: any, index: number) => ({
      paperId: item.paperId,
      paperName: item.paperName,
      courseId: item.courseId || data.courseId,
      paperType: item.paperType || data.paperType,
      paperLevel: item.paperLevel || 'SUB',
      answerMode: item.answerMode || 'REQUIRED',
      paperWeight: Number(item.paperWeight || 100),
      sortNo: Number(item.sortNo || index + 1),
      durationMinutes: Number(item.durationMinutes || data.durationMinutes || 60),
      publishStatus,
      publishStartTime: publishStatus === 'published' ? new Date().toISOString().slice(0, 19).replace('T', ' ') : item.publishStartTime || data.publishStartTime,
      publishEndTime: item.publishEndTime || data.publishEndTime,
      passScore: Number(item.passScore || data.passScore || 60),
      markingMode: item.markingMode || data.markingMode,
      questionOrderMode: item.questionOrderMode || data.questionOrderMode,
      allowReviewAnalysis: item.allowReviewAnalysis || data.allowReviewAnalysis,
      status: item.status || data.status,
      remark: item.remark || data.remark,
      questions: (item.questions || []).map((question: any, qIndex: number) => ({
        id: question.id,
        questionId: question.questionId,
        score: Number(question.score || 0),
        sortNo: Number(question.sortNo || qIndex + 1),
      })),
    })),
  })
  ElMessage.success(publishStatus === 'published' ? '发布成功' : '状态已更新')
  getList()
}
function handleAddSectionBlock(questionType: string) {
  paperSections.value.push(createSection(questionType))
}
function removeSectionBlock(index: number) {
  paperSections.value.splice(index, 1)
  recalculateTotalScore()
}
function openQuestionSelector(sectionIndex: number) {
  selectorMode.value = 'section'
  selectorSectionIndex.value = sectionIndex
  selectorSubPaperIndex.value = -1
  const section = paperSections.value[sectionIndex]
  questionSelectorQuery.questionType = section?.questionType || ''
  questionSelectorQuery.stem = ''
  questionSelectorQuery.difficultyLevel = 3
  questionSelectorOpen.value = true
  loadQuestionBank()
}
function openSubPaperQuestionSelector(subIndex: number) {
  selectorMode.value = 'subPaper'
  selectorSubPaperIndex.value = subIndex
  selectorSectionIndex.value = -1
  questionSelectorQuery.questionType = ''
  questionSelectorQuery.stem = ''
  questionSelectorQuery.difficultyLevel = 3
  questionSelectorOpen.value = true
  loadQuestionBank()
}
function removeSectionQuestion(sectionIndex: number, questionIndex: number) {
  paperSections.value[sectionIndex].questions.splice(questionIndex, 1)
  recalculateTotalScore()
}
function addSubPaper() {
  form.subPapers = [...(form.subPapers || []), createSubPaper()]
}
function removeSubPaper(index: number) {
  form.subPapers.splice(index, 1)
}
function removeSubPaperQuestion(subIndex: number, questionIndex: number) {
  form.subPapers[subIndex].questions.splice(questionIndex, 1)
}
function sectionSubtotal(section: PaperSection) {
  return section.questions.reduce((sum, item) => sum + Number(item.score || 0), 0)
}
function sectionQuestionCount(section: PaperSection) {
  return Number(section?.questions?.length || 0)
}
function sectionHasCustomScores(section: PaperSection) {
  if (!sectionQuestionCount(section)) return false
  return section.questions.some((item: any) => Number(item.score || 0) !== Number(section.defaultScore || 0))
}
function syncSectionMeta(section: PaperSection) {
  section.targetScore = Number(sectionSubtotal(section).toFixed(2))
  if (!section.instructions) {
    section.instructions = sectionQuestionCount(section)
      ? `每题 ${Number(section.defaultScore || 0)} 分，共 ${sectionQuestionCount(section)} 题`
      : ''
  }
}
function applySectionDefaultScore(sectionIndex: number) {
  const section = paperSections.value[sectionIndex]
  if (!section) return
  section.questions = section.questions.map((item: any) => ({
    ...item,
    score: Number(section.defaultScore || 0),
  }))
  syncSectionMeta(section)
  recalculateTotalScore()
}
function distributeSectionScore(sectionIndex: number) {
  const section = paperSections.value[sectionIndex]
  if (!section || !sectionQuestionCount(section)) {
    ElMessage.warning('请先为该区块添加题目')
    return
  }
  const count = sectionQuestionCount(section)
  const total = Number(section.targetScore || 0)
  const average = count ? Number((total / count).toFixed(2)) : 0
  let assigned = 0
  section.questions = section.questions.map((item: any, index: number) => {
    if (index === count - 1) {
      const lastScore = Number((total - assigned).toFixed(2))
      return { ...item, score: lastScore >= 0 ? lastScore : 0 }
    }
    assigned += average
    return { ...item, score: average }
  })
  section.defaultScore = average
  if (!section.instructions) {
    section.instructions = `按区块总分 ${total} 分自动分配，共 ${count} 题`
  }
  recalculateTotalScore()
}
function recalculateTotalScore() {
  rebuildFlatQuestionsFromSections()
  const totalScore = (form.questions || []).reduce((sum: number, item: any) => sum + Number(item.score || 0), 0)
  form.totalScore = Number(totalScore.toFixed(2))
  paperSections.value.forEach((section) => {
    section.targetScore = Number(sectionSubtotal(section).toFixed(2))
  })
}
async function loadQuestionBank() {
  questionSelectorLoading.value = true
  try {
    const res = await listQuestion({
      pageNum: 1,
      pageSize: 500,
      courseId: form.courseId || undefined,
      questionType: questionSelectorQuery.questionType || undefined,
      status: '0',
    })
    const keyword = String(questionSelectorQuery.stem || '').trim().toLowerCase()
    const difficulty = questionSelectorQuery.difficultyLevel
    questionBankList.value = (res.rows || []).filter((item: any) => {
      const stemMatch = !keyword || stripHtml(item.stem).toLowerCase().includes(keyword)
      const difficultyMatch = !difficulty || Number(item.difficultyLevel || 0) === Number(difficulty)
      return stemMatch && difficultyMatch
    })
  } finally {
    questionSelectorLoading.value = false
  }
}
function handleQuestionBankSelection(selection: any[]) {
  selectedQuestionBankRows.value = selection
}
function appendSelectedQuestions() {
  if (!selectedQuestionBankRows.value.length) {
    ElMessage.warning('请先选择题目')
    return
  }
  if (selectorMode.value === 'section') {
    const section = paperSections.value[selectorSectionIndex.value]
    if (!section) {
      ElMessage.warning('请选择目标区块')
      return
    }
    const exists = new Set(section.questions.map((item: any) => item.questionId))
    selectedQuestionBankRows.value.forEach((item: any) => {
      if (exists.has(item.questionId)) return
      section.questions.push({
        questionId: item.questionId,
        score: Number(section.defaultScore || 5),
        sortNo: section.questions.length + 1,
        stem: item.stem,
        questionType: item.questionType,
      })
    })
    syncSectionMeta(section)
    recalculateTotalScore()
  } else {
    const subPaper = form.subPapers?.[selectorSubPaperIndex.value]
    if (!subPaper) {
      ElMessage.warning('请选择目标子试卷')
      return
    }
    const exists = new Set((subPaper.questions || []).map((item: any) => item.questionId))
    selectedQuestionBankRows.value.forEach((item: any) => {
      if (exists.has(item.questionId)) return
      subPaper.questions.push({
        questionId: item.questionId,
        score: 5,
        sortNo: subPaper.questions.length + 1,
        stem: item.stem,
        questionType: item.questionType,
      })
    })
  }
  selectedQuestionBankRows.value = []
  questionSelectorOpen.value = false
}
async function submitForm() {
  rebuildFlatQuestionsFromSections()
  const payload = {
    paperId: form.paperId,
    paperName: form.paperName,
    courseId: form.courseId,
    paperType: form.paperType,
    totalScore: form.totalScore,
    durationMinutes: form.durationMinutes,
    publishStatus: form.publishStatus,
    publishStartTime: form.publishStartTime,
    publishEndTime: form.publishEndTime,
    strategyTemplateId: form.strategyTemplateId,
    passScore: form.passScore,
    markingMode: form.markingMode,
    questionOrderMode: form.questionOrderMode,
    allowReviewAnalysis: form.allowReviewAnalysisBool ? '1' : '0',
    sectionConfigJson: form.sectionConfigJson,
    paperLevel: 'MAIN',
    answerMode: 'REQUIRED',
    paperWeight: 100,
    status: form.status,
    remark: form.remark,
    questions: (form.questions || []).map((item: any, index: number) => ({ id: item.id, questionId: item.questionId, score: Number(item.score || 0), sortNo: Number(item.sortNo || index + 1) })),
    subPapers: (form.subPapers || []).map((item: any, index: number) => ({
      paperId: item.paperId,
      paperName: item.paperName,
      courseId: form.courseId,
      paperType: form.paperType,
      paperLevel: 'SUB',
      answerMode: item.answerMode,
      paperWeight: Number(item.paperWeight || 100),
      sortNo: Number(item.sortNo || index + 1),
      durationMinutes: Number(item.durationMinutes || form.durationMinutes || 60),
      publishStatus: form.publishStatus,
      publishStartTime: form.publishStartTime,
      publishEndTime: form.publishEndTime,
      passScore: form.passScore,
      markingMode: form.markingMode,
      questionOrderMode: form.questionOrderMode,
      allowReviewAnalysis: form.allowReviewAnalysisBool ? '1' : '0',
      status: form.status,
      remark: form.remark,
      questions: (item.questions || []).map((question: any, qIndex: number) => ({
        id: question.id,
        questionId: question.questionId,
        score: Number(question.score || 0),
        sortNo: Number(question.sortNo || qIndex + 1),
      })),
    })),
  }
  if (form.paperId) {
    await updatePaperDetail(payload)
    ElMessage.success('修改成功')
  } else {
    await addPaperDetail(payload)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}
function openAssembleDialog() {
  resetAssembleForm()
  assembleOpen.value = true
}
function addRule() {
  assembleForm.rules.push({ questionType: 'single', count: 5, difficultyLevel: 3, knowledgePointId: undefined, score: 5 })
}
function removeRule(index: number) {
  assembleForm.rules.splice(index, 1)
}
function applyAssemblePreset(preset: string) {
  if (preset === 'basic') assembleForm.rules = [{ questionType: 'single', count: 10, difficultyLevel: 2, knowledgePointId: undefined, score: 4 }, { questionType: 'judge', count: 5, difficultyLevel: 2, knowledgePointId: undefined, score: 2 }, { questionType: 'fill', count: 5, difficultyLevel: 3, knowledgePointId: undefined, score: 6 }]
  if (preset === 'midterm') assembleForm.rules = [{ questionType: 'single', count: 12, difficultyLevel: 3, knowledgePointId: undefined, score: 4 }, { questionType: 'multiple', count: 6, difficultyLevel: 3, knowledgePointId: undefined, score: 6 }, { questionType: 'fill', count: 4, difficultyLevel: 3, knowledgePointId: undefined, score: 8 }, { questionType: 'essay', count: 2, difficultyLevel: 4, knowledgePointId: undefined, score: 15 }]
  if (preset === 'objective') assembleForm.rules = [{ questionType: 'single', count: 20, difficultyLevel: 3, knowledgePointId: undefined, score: 3 }, { questionType: 'multiple', count: 10, difficultyLevel: 3, knowledgePointId: undefined, score: 4 }, { questionType: 'judge', count: 10, difficultyLevel: 2, knowledgePointId: undefined, score: 2 }]
}
async function applyStrategyTemplate(templateId: number) {
  if (!templateId) return
  const res = await getStrategyTemplate(templateId)
  const data = res.data || {}
  try {
    const rules = JSON.parse(data.ruleJson || '[]')
    if (Array.isArray(rules)) {
      assembleForm.rules = rules
      ElMessage.success('已套用策略模板')
    }
  } catch {
    ElMessage.warning('模板规则解析失败')
  }
}
async function submitAssemble() {
  assembleLoading.value = true
  try {
    const res = await assemblePaper(assembleForm)
    Object.assign(assembleResult, res.data || { paperName: '', totalScore: 0, durationMinutes: 0, questions: [] })
    assembleOpen.value = false
    assembleResultOpen.value = true
    ElMessage.success('智能组卷完成')
    if (assembleForm.savePaper) getList()
  } finally {
    assembleLoading.value = false
  }
}
async function loadBaseOptions() {
  const [courseRes, typeRes, kpRes, strategyRes] = await Promise.all([
    fetchCourseOptions(),
    listQuestionTypeMeta(),
    listKnowledgePoint({ pageNum: 1, pageSize: 500, status: '0' }),
    listStrategyTemplate({ pageNum: 1, pageSize: 200, status: '0' }),
  ])
  courseOptions.value = courseRes || []
  questionTypeOptions.value = typeRes.data || []
  knowledgePointOptions.value = (kpRes.rows || []).map((item: any) => ({ label: `${item.knowledgeName}（${item.knowledgePointId}）`, value: item.knowledgePointId, courseId: item.courseId }))
  strategyTemplateOptions.value = strategyRes.rows || []
}
onMounted(async () => {
  resetFormData()
  resetAssembleForm()
  await loadBaseOptions()
  await getList()
})
watch(() => paperSections.value, () => { recalculateTotalScore() }, { deep: true })
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.mt16 { margin-top: 16px; }
.paper-stage { min-height: 520px; }
.panel-title { font-size: 15px; font-weight: 700; color: var(--el-text-color-primary); }
.panel-subtitle { margin-top: 4px; font-size: 12px; color: var(--el-text-color-secondary); }
.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
.metric-card { min-height: 104px; background: linear-gradient(180deg, #f8fbff 0%, #f3f7fc 100%); }
.metric-label { color: var(--el-text-color-secondary); font-size: 13px; }
.metric-value { margin-top: 10px; font-size: 30px; font-weight: 700; color: var(--el-color-primary); }
.metric-sub { margin-top: 10px; line-height: 1.7; color: var(--el-text-color-regular); }
.step-nav { display: flex; gap: 8px; margin-bottom: 16px; flex-wrap: wrap; }
.step-nav__item { display: inline-flex; align-items: center; gap: 8px; border: 1px solid var(--el-border-color); background: #fff; border-radius: 6px; padding: 8px 14px; cursor: pointer; transition: all .2s; font-size: 14px; }
.step-nav__item.is-active { color: var(--el-color-primary); border-color: var(--el-color-primary-light-5); background: var(--el-color-primary-light-9); }
.step-nav__index { width: 20px; height: 20px; border-radius: 50%; background: var(--el-fill-color); display: inline-flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 700; }
.step-nav__item.is-active .step-nav__index { background: var(--el-color-primary); color: #fff; }
.section-toolbar, .rule-panel__header { display: flex; justify-content: space-between; align-items: center; gap: 16px; margin-bottom: 16px; }
.section-toolbar__actions, .preset-list, .section-card__actions { display: flex; gap: 12px; flex-wrap: wrap; }
.section-card, .rule-panel, .assemble-preset, .sub-paper-board { margin-top: 8px; padding: 16px; border-radius: 4px; background: linear-gradient(180deg, #f9fbff 0%, #f4f8fc 100%); border: 1px solid var(--el-border-color-lighter); }
.section-card + .section-card, .rule-card + .rule-card { margin-top: 14px; }
.section-card__head, .rule-card__head { display: flex; justify-content: space-between; align-items: center; gap: 16px; margin-bottom: 14px; }
.section-card__title, .rule-card__title { font-size: 16px; font-weight: 700; color: var(--el-text-color-primary); }
.section-card__meta { margin-top: 6px; color: var(--el-text-color-secondary); font-size: 12px; }
.table-actions {
  display: flex;
  flex-wrap: nowrap;
  gap: 8px;
  white-space: nowrap;
}
.section-score-board {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 14px;
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f7faff 100%);
  border: 1px solid #d9e6fb;
}
.section-score-board__metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  flex: 1;
}
.section-score-board__actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.score-chip {
  padding: 12px;
  border-radius: 12px;
  background: #f8fbff;
  border: 1px solid #e1ebfb;
}
.score-chip--stat {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.score-chip__label {
  display: block;
  margin-bottom: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.score-chip__value {
  min-height: 32px;
  display: flex;
  align-items: center;
}
.sub-paper-board {
  margin-top: 16px;
}
.detail-header { display: flex; gap: 24px; flex-wrap: wrap; font-weight: 600; color: var(--el-text-color-primary); }
@media (max-width: 960px) {
  .section-score-board {
    flex-direction: column;
  }
  .section-score-board__metrics {
    grid-template-columns: 1fr;
  }
}
</style>
