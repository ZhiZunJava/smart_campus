<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" clearable filterable style="width: 220px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="题型">
        <el-select v-model="queryParams.questionType" clearable style="width: 180px">
          <el-option v-for="item in questionTypeOptions" :key="item.code" :label="item.label" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="题库">
        <el-select v-model="queryParams.catalogId" clearable filterable style="width: 220px">
          <el-option v-for="item in currentCatalogOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable style="width: 140px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="题干">
        <el-input v-model="queryParams.stem" placeholder="支持按题干模糊筛选" style="width: 260px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增题目</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Upload" @click="triggerImport">批量导入</el-button></el-col>
      <el-col :span="1.5"><el-button type="warning" plain icon="Download" @click="handleDownloadTemplate">下载模板</el-button></el-col>
      <el-col :span="1.5"><el-button type="info" plain icon="MagicStick" @click="openAiDialog">AI 出题</el-button></el-col>
      <el-col :span="1.5"><el-button type="primary" plain icon="Clock" @click="openAiTaskDialog">出题任务</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="!selectedIds.length" @click="handleDelete()">批量删除</el-button></el-col>
    </el-row>

    <input ref="fileInputRef" type="file" accept=".xls,.xlsx" class="hidden-input" @change="handleImportFileChange" />

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="题目ID" prop="questionId" width="90" />
      <el-table-column label="课程" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          {{ courseLabel(scope.row.courseId) }}
        </template>
      </el-table-column>
      <el-table-column label="知识点" min-width="150" show-overflow-tooltip>
        <template #default="scope">
          {{ knowledgePointLabel(scope.row.knowledgePointId) }}
        </template>
      </el-table-column>
      <el-table-column label="题型" width="120">
        <template #default="scope">
          <el-tag effect="plain" type="primary">{{ questionTypeLabel(scope.row.questionType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="难度" width="170">
        <template #default="scope">
          <el-rate :model-value="Number(scope.row.difficultyLevel || 0)" disabled show-score score-template="{value}" text-color="#d97706" />
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="题干" min-width="320" show-overflow-tooltip>
        <template #default="scope">
          {{ stripHtml(scope.row.stem) }}
        </template>
      </el-table-column>
      <el-table-column label="答案" prop="answer" min-width="180" show-overflow-tooltip />
      <el-table-column label="来源" prop="source" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="warning" icon="Clock" @click="handleVersionHistory(scope.row)">版本</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="920px" top="6vh" destroy-on-close>
      <div class="step-nav">
        <button v-for="(item, index) in [{ label: '基础信息' }, { label: '题干内容' }, { label: '答案配置' }]" :key="item.label" type="button" class="step-nav__item" :class="{ 'is-active': questionStep === index }" @click="questionStep = index">
          <span class="step-nav__index">{{ index + 1 }}</span>
          <span>{{ item.label }}</span>
        </button>
      </div>

      <el-form v-if="questionStep === 0" :model="form" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="课程">
              <el-select v-model="form.courseId" clearable filterable placeholder="可不选，作为通用题目">
                <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="知识点">
              <el-select v-model="form.knowledgePointId" clearable filterable placeholder="可不选">
                <el-option v-for="item in currentKnowledgePointOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="题库">
              <el-select v-model="form.catalogId" clearable filterable placeholder="可不选，后续可归属多个题库">
                <el-option v-for="item in currentCatalogOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="题型">
              <el-select v-model="form.questionType">
                <el-option v-for="item in questionTypeOptions" :key="item.code" :label="item.label" :value="item.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status">
                <el-option label="正常" value="0" />
                <el-option label="停用" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度">
              <el-input-number v-model="form.difficultyLevel" :min="1" :max="5" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="质量分">
              <el-input-number v-model="form.qualityScore" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="来源">
              <el-input v-model="form.source" placeholder="如教师录入、AI智能出题、教研导入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标签">
              <el-input v-model="form.questionTags" placeholder="如基础题,易错题,阶段测验" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源批次">
              <el-input v-model="form.sourceBatchNo" placeholder="如 2026Q1-MATH-AI-001" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="可补充命题意图、适用班级、来源说明" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div v-else-if="questionStep === 1" class="step-panel">
        <div class="editor-toolbar">
          <span class="panel-subtitle">题干支持富文本编辑；填空题可在编辑器工具栏中直接点击“填空”插入占位符。</span>
        </div>
        <el-form :model="form" label-width="90px">
          <el-form-item label="题干">
            <editor ref="stemEditorRef" v-model="form.stem" :min-height="220" :enable-fill-blank="form.questionType === 'fill'" @custom-action="handleEditorCustomAction" />
          </el-form-item>
          <el-form-item label="解析">
            <el-input v-model="form.analysis" type="textarea" :rows="4" placeholder="请输入题目解析" />
          </el-form-item>
          <el-form-item label="材料">
            <el-input v-model="form.materialContent" type="textarea" :rows="4" placeholder="材料题/案例题可补充背景材料；普通题可留空" />
          </el-form-item>
        </el-form>
      </div>

      <div v-else class="step-panel">
        <el-form :model="form" label-width="90px">
          <el-form-item v-if="!currentTypeRequiresOptions" label="答案">
            <el-input v-model="form.answer" type="textarea" :rows="3" placeholder="主观题填写答案要点；填空题也可在这里补充参考答案" />
          </el-form-item>
          <el-form-item v-else label="答案预览">
            <el-input :model-value="objectiveAnswerPreview" readonly type="textarea" :rows="2" />
          </el-form-item>
        </el-form>

        <div class="option-editor" v-if="currentTypeRequiresOptions">
          <div class="option-editor__header">
            <div>
              <div class="panel-title">选项配置</div>
              <div class="panel-subtitle">当前题型需要维护选项，并标记正确项。</div>
            </div>
            <el-button type="primary" plain icon="Plus" :disabled="form.questionType === 'judge'" @click="addOptionRow">新增选项</el-button>
          </div>
          <el-table :data="form.options" border>
            <el-table-column label="标识" width="100">
              <template #default="scope">
                <el-input v-model="scope.row.optionKey" placeholder="A" />
              </template>
            </el-table-column>
            <el-table-column label="内容" min-width="320">
              <template #default="scope">
                <el-input v-model="scope.row.optionContent" type="textarea" :rows="2" />
              </template>
            </el-table-column>
            <el-table-column label="正确项" width="120">
              <template #default="scope">
                <el-switch v-model="scope.row.isRight" active-value="1" inactive-value="0" @change="handleOptionRightChange(scope.$index)" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button link type="danger" icon="Delete" @click="removeOptionRow(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="questionStep > 0" @click="questionStep--">上一步</el-button>
          <el-button @click="open = false">取消</el-button>
          <el-button v-if="questionStep < 2" type="primary" @click="questionStep++">下一步</el-button>
          <el-button v-else type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="题目详情" width="920px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="题型">{{ questionTypeLabel(detail.questionType) }}</el-descriptions-item>
        <el-descriptions-item label="难度">
          <el-rate :model-value="Number(detail.difficultyLevel || 0)" disabled show-score score-template="{value}" text-color="#d97706" />
        </el-descriptions-item>
        <el-descriptions-item label="课程">{{ courseLabel(detail.courseId) }}</el-descriptions-item>
        <el-descriptions-item label="知识点">{{ knowledgePointLabel(detail.knowledgePointId) }}</el-descriptions-item>
        <el-descriptions-item label="题干" :span="2">
          <div class="rich-preview" v-html="detail.stem || '-'" />
        </el-descriptions-item>
        <el-descriptions-item label="答案" :span="2">{{ detail.answer || '-' }}</el-descriptions-item>
        <el-descriptions-item label="解析" :span="2">{{ detail.analysis || '-' }}</el-descriptions-item>
        <el-descriptions-item label="标签" :span="2">{{ detail.questionTags || '-' }}</el-descriptions-item>
        <el-descriptions-item label="来源批次">{{ detail.sourceBatchNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="来源">{{ detail.source || '-' }}</el-descriptions-item>
        <el-descriptions-item label="材料" :span="2">{{ detail.materialContent || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div class="panel-title mt16">选项列表</div>
      <el-table :data="detail.options || []" border class="mt12">
        <el-table-column label="标识" prop="optionKey" width="90" />
        <el-table-column label="内容" prop="optionContent" min-width="260" />
        <el-table-column label="正确项" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isRight === '1' ? 'success' : 'info'">{{ scope.row.isRight === '1' ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="versionOpen" title="题目版本历史" width="980px">
      <el-alert
        v-if="!versionDetail.item"
        type="warning"
        :closable="false"
        title="当前题目尚未建立版本化映射，可能需要先通过新版录题或编辑后同步。"
      />
      <el-descriptions v-else :column="2" border class="mb16">
        <el-descriptions-item label="题目主键">{{ versionDetail.item?.itemId }}</el-descriptions-item>
        <el-descriptions-item label="默认题库">{{ versionDetail.item?.defaultCatalogId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前版本">{{ versionDetail.item?.currentVersionNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="来源类型">{{ versionDetail.item?.sourceType || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-timeline>
        <el-timeline-item
          v-for="version in versionDetail.versions || []"
          :key="version.versionId"
          :type="version.isCurrent === '1' ? 'primary' : 'info'"
          :timestamp="version.createTime || ''"
          placement="top"
        >
          <div class="version-card">
            <div class="version-card__head">
              <div class="version-card__title">
                V{{ version.versionNo }} · {{ version.changeType || '-' }}
                <el-tag v-if="version.isCurrent === '1'" type="success" effect="plain">当前版本</el-tag>
              </div>
              <div class="version-card__meta">
                <span>知识点 {{ knowledgePointLabel(version.knowledgePointId) }}</span>
                <span>质量分 {{ version.qualityScore || 0 }}</span>
              </div>
            </div>
            <div class="version-card__body">
              <div><strong>题干：</strong>{{ stripHtml(version.stem) || '-' }}</div>
              <div><strong>答案：</strong>{{ version.answer || '-' }}</div>
              <div><strong>解析：</strong>{{ version.analysis || '-' }}</div>
              <div><strong>标签：</strong>{{ version.questionTags || '-' }}</div>
              <div><strong>来源：</strong>{{ version.source || '-' }}</div>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>

    <el-dialog v-model="aiOpen" title="AI 智能出题" width="760px" destroy-on-close>
      <el-form :model="aiForm" label-width="100px">
        <el-form-item label="AI 模型">
          <el-select v-model="aiForm.modelId" clearable filterable>
            <el-option v-for="item in aiModelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="aiForm.courseId" clearable filterable>
            <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标题库">
          <el-select v-model="aiForm.catalogId" clearable filterable>
            <el-option v-for="item in currentCatalogOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="知识点">
          <el-select v-model="aiForm.knowledgePointId" clearable filterable>
            <el-option v-for="item in aiKnowledgePointOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="aiForm.questionType">
            <el-option v-for="item in questionTypeOptions" :key="item.code" :label="item.label" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-input-number v-model="aiForm.difficultyLevel" :min="1" :max="5" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="aiForm.count" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="保存入库">
          <el-switch v-model="aiForm.saveToBank" />
        </el-form-item>
        <el-form-item label="出题要求">
          <el-input v-model="aiForm.userInstruction" type="textarea" :rows="4" placeholder="例如：面向高一数学，强调基础巩固，题干简洁，解析清楚。" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="aiOpen = false">取消</el-button>
        <el-button @click="openAiTaskDialog">查看任务</el-button>
        <el-button type="primary" :loading="aiLoading" @click="submitAiGenerate">提交异步任务</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="aiTaskOpen" title="AI 出题任务" width="980px" destroy-on-close>
      <div class="task-toolbar">
        <el-alert
          v-if="activeAiTaskHint"
          type="info"
          :closable="false"
          class="task-toolbar__alert"
          :title="activeAiTaskHint"
        />
        <div class="task-toolbar__actions">
          <el-select v-model="aiTaskFilterStatus" clearable placeholder="全部状态" style="width: 160px" @change="loadAiTaskList">
            <el-option label="运行中" value="RUNNING" />
            <el-option label="成功" value="SUCCESS" />
            <el-option label="失败" value="FAILED" />
          </el-select>
          <el-button icon="Refresh" @click="loadAiTaskList">刷新</el-button>
        </div>
      </div>
      <el-table v-loading="aiTaskLoading" :data="aiTaskList" border>
        <el-table-column label="任务ID" prop="taskId" width="100" />
        <el-table-column label="课程" min-width="160">
          <template #default="scope">
            {{ courseLabel(taskRequest(scope.row).courseId || scope.row.bizId) }}
          </template>
        </el-table-column>
        <el-table-column label="题型" width="120">
          <template #default="scope">
            {{ taskRequest(scope.row).questionTypeLabel || questionTypeLabel(taskRequest(scope.row).questionType) }}
          </template>
        </el-table-column>
        <el-table-column label="数量" width="90">
          <template #default="scope">
            {{ taskRequest(scope.row).count || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="入库" width="90">
          <template #default="scope">
            {{ taskRequest(scope.row).saveToBank === false ? '否' : '是' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="taskStatusTag(scope.row.taskStatus)">{{ taskStatusLabel(scope.row.taskStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="结果摘要" width="160">
          <template #default="scope">
            {{ aiTaskResultSummary(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column label="进度说明" min-width="260" show-overflow-tooltip>
          <template #default="scope">
            {{ taskResponse(scope.row).message || scope.row.errorMsg || '任务已创建' }}
          </template>
        </el-table-column>
        <el-table-column label="出题要求" min-width="220" show-overflow-tooltip>
          <template #default="scope">
            {{ taskInstruction(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" min-width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="inspectAiTask(scope.row)">日志</el-button>
            <el-button
              link
              type="warning"
              :disabled="scope.row.taskStatus !== 'FAILED'"
              @click="retryAiTask(scope.row)"
            >
              重试
            </el-button>
            <el-button
              link
              type="success"
              :disabled="scope.row.taskStatus !== 'SUCCESS'"
              @click="openAiTaskResult(scope.row)"
            >
              查看结果
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="aiResultOpen" title="AI 出题结果" width="1080px">
      <el-alert
        v-if="aiProgressText"
        type="info"
        :closable="false"
        class="mb16"
        :title="aiProgressText"
      />
      <el-progress v-if="aiLoading || aiProgressPercent > 0" :percentage="aiProgressPercent" :indeterminate="aiLoading" :stroke-width="14" status="success" class="mb16" />
      <el-alert
        v-if="aiResult.rawContent"
        type="success"
        :closable="false"
        class="mb16"
        :title="`共生成 ${aiResult.generatedCount || 0} 题，已入库 ${aiResult.savedCount || 0} 题`"
      />
      <el-collapse>
        <el-collapse-item
          v-for="(item, index) in aiResult.questions || []"
          :key="`${item.questionId || 'preview'}-${index}`"
          :title="`${Number(index) + 1}. ${questionTypeLabel(item.questionType)} ｜ 难度 ${item.difficultyLevel || '-'} ｜ ${stripHtml(item.stem)}`"
          :name="String(index)"
        >
          <el-descriptions :column="2" border>
            <el-descriptions-item label="题干" :span="2">
              <div class="rich-preview" v-html="item.stem || '-'" />
            </el-descriptions-item>
            <el-descriptions-item label="答案" :span="2">{{ item.answer || '-' }}</el-descriptions-item>
            <el-descriptions-item label="解析" :span="2">{{ item.analysis || '-' }}</el-descriptions-item>
          </el-descriptions>
          <el-table :data="item.options || []" border class="mt12">
            <el-table-column label="标识" prop="optionKey" width="90" />
            <el-table-column label="内容" prop="optionContent" min-width="260" />
            <el-table-column label="正确项" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isRight === '1' ? 'success' : 'info'">{{ scope.row.isRight === '1' ? '是' : '否' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saveAs } from 'file-saver'
import {
  addQuestionDetail,
  delQuestion,
  downloadQuestionTemplate,
  getAiGenerateQuestionTask,
  getQuestionDetail,
  importQuestionData,
  listQuestionCatalog,
  listAiGenerateQuestionTask,
  listLegacyQuestionVersions,
  listQuestion,
  listQuestionTypeMeta,
  submitAiGenerateQuestionTask,
  updateQuestionDetail,
} from '@/api/campus/exam'
import { fetchAiModelOptions, fetchCourseOptions } from '@/api/campus/options'
import { listKnowledgePoint } from '@/api/campus/teaching'

const loading = ref(false)
const total = ref(0)
const open = ref(false)
const detailOpen = ref(false)
const versionOpen = ref(false)
const aiOpen = ref(false)
const aiTaskOpen = ref(false)
const aiResultOpen = ref(false)
const aiLoading = ref(false)
const aiTaskLoading = ref(false)
const aiProgressPercent = ref(0)
const aiProgressText = ref('')
const aiTaskFilterStatus = ref('')
const aiTaskList = ref<any[]>([])
const currentAiTaskId = ref<number | null>(null)
const aiTaskPollTimer = ref<number | null>(null)
const title = ref('')
const questionStep = ref(0)
const previousQuestionType = ref('single')
const dataList = ref<any[]>([])
const detail = ref<any>({})
const versionDetail = ref<any>({ item: null, versions: [] })
const questionTypeOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const knowledgePointOptions = ref<any[]>([])
const catalogOptions = ref<any[]>([])
const aiModelOptions = ref<any[]>([])
const selectedIds = ref<number[]>([])
const fileInputRef = ref<HTMLInputElement>()
const stemEditorRef = ref<any>()
const route = useRoute()
const router = useRouter()

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  courseId: undefined,
  catalogId: undefined,
  questionType: '',
  status: '',
  stem: '',
})

const form = reactive<any>({})
const aiForm = reactive<any>({})
const aiResult = reactive<any>({ generatedCount: 0, savedCount: 0, rawContent: '', questions: [] })

const currentKnowledgePointOptions = computed(() =>
  knowledgePointOptions.value.filter((item: any) => !form.courseId || item.courseId === form.courseId),
)

const currentCatalogOptions = computed(() =>
  catalogOptions.value.filter((item: any) => !queryCourseIdContext.value || !item.courseId || item.courseId === queryCourseIdContext.value),
)

const queryCourseIdContext = computed(() => form.courseId || aiForm.courseId || queryParams.courseId)

const aiKnowledgePointOptions = computed(() =>
  knowledgePointOptions.value.filter((item: any) => !aiForm.courseId || item.courseId === aiForm.courseId),
)

const activeAiTaskHint = computed(() => {
  if (!currentAiTaskId.value) return ''
  const currentTask = aiTaskList.value.find((item: any) => Number(item.taskId) === Number(currentAiTaskId.value))
  if (!currentTask) return `正在跟踪任务 #${currentAiTaskId.value}`
  const response = taskResponse(currentTask)
  return `当前任务 #${currentAiTaskId.value} · ${taskStatusLabel(currentTask.taskStatus)}${response.message ? ` · ${response.message}` : ''}`
})

const currentTypeMeta = computed(() =>
  questionTypeOptions.value.find((item: any) => item.code === form.questionType) || null,
)

const currentTypeRequiresOptions = computed(() => Boolean(currentTypeMeta.value?.requiresOptions))

const objectiveAnswerPreview = computed(() => {
  if (!currentTypeRequiresOptions.value) return form.answer || ''
  const rightOptions = (form.options || []).filter((item: any) => item?.isRight === '1')
  if (!rightOptions.length) return ''
  if (form.questionType === 'judge') {
    return rightOptions[0]?.optionContent || rightOptions[0]?.optionKey || ''
  }
  return rightOptions
    .map((item: any) => String(item.optionKey || '').trim().toUpperCase())
    .filter(Boolean)
    .sort()
    .join(',')
})

function stripHtml(value: string) {
  return String(value || '').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

function questionTypeLabel(type: string) {
  return questionTypeOptions.value.find((item: any) => item.code === type)?.label || type || '-'
}

function courseLabel(courseId: number | string | undefined) {
  if (!courseId) return '通用题目'
  const matched = courseOptions.value.find((item: any) => String(item.value) === String(courseId))
  return matched?.label || `课程 ${courseId}`
}

function knowledgePointLabel(knowledgePointId: number | string | undefined) {
  if (!knowledgePointId) return '-'
  const matched = knowledgePointOptions.value.find((item: any) => String(item.value) === String(knowledgePointId))
  return matched?.label || `知识点 ${knowledgePointId}`
}

function taskStatusLabel(status: string) {
  const map: Record<string, string> = {
    RUNNING: '运行中',
    SUCCESS: '成功',
    FAILED: '失败',
  }
  return map[status] || status || '-'
}

function taskStatusTag(status: string) {
  if (status === 'SUCCESS') return 'success'
  if (status === 'RUNNING') return 'warning'
  return 'danger'
}

function parseJsonPayload(payload: any) {
  if (!payload) return {}
  if (typeof payload === 'object') return payload
  try {
    return JSON.parse(payload)
  } catch {
    return {}
  }
}

function taskRequest(task: any) {
  return parseJsonPayload(task?.requestPayload)
}

function taskResponse(task: any) {
  return parseJsonPayload(task?.responsePayload)
}

function taskInstruction(task: any) {
  const instruction = String(taskRequest(task)?.instruction || '').trim()
  return instruction || '未额外补充要求'
}

function aiTaskResultSummary(task: any) {
  const result = taskResponse(task)?.result
  if (!result) {
    return task.taskStatus === 'FAILED' ? '执行失败' : '-'
  }
  return `生成${result.generatedCount || 0} / 入库${result.savedCount || 0}`
}

function defaultOptionsForType(type: string) {
  if (type === 'judge') {
    return [
      { optionKey: 'A', optionContent: '正确', isRight: '1' },
      { optionKey: 'B', optionContent: '错误', isRight: '0' },
    ]
  }
  if (type === 'single' || type === 'multiple') {
    return [
      { optionKey: 'A', optionContent: '', isRight: type === 'single' ? '1' : '0' },
      { optionKey: 'B', optionContent: '', isRight: '0' },
      { optionKey: 'C', optionContent: '', isRight: '0' },
      { optionKey: 'D', optionContent: '', isRight: '0' },
    ]
  }
  return []
}

function buildOptionsForType(nextType: string, currentOptions: any[] = []) {
  if (nextType === 'judge') {
    const trueLabel = currentOptions[0]?.optionContent || '正确'
    const falseLabel = currentOptions[1]?.optionContent || '错误'
    return [
      { optionKey: 'A', optionContent: trueLabel, isRight: '1' },
      { optionKey: 'B', optionContent: falseLabel, isRight: '0' },
    ]
  }
  if (nextType === 'single' || nextType === 'multiple') {
    const base = defaultOptionsForType(nextType)
    return base.map((item, index) => ({
      ...item,
      optionContent: currentOptions[index]?.optionContent || item.optionContent,
      isRight: nextType === 'single'
        ? index === firstRightIndex(currentOptions) ? '1' : item.isRight === '1' && index === 0 ? '1' : '0'
        : currentOptions[index]?.isRight === '1' ? '1' : '0',
    }))
  }
  return []
}

function firstRightIndex(options: any[] = []) {
  const index = options.findIndex((item: any) => item?.isRight === '1')
  return index >= 0 ? index : 0
}

function resetFormData() {
  Object.assign(form, {
    questionId: undefined,
    courseId: undefined,
    catalogId: undefined,
    knowledgePointId: undefined,
    questionType: 'single',
    difficultyLevel: 3,
    stem: '<p></p>',
    answer: 'A',
    analysis: '',
    source: '教师录入',
    questionTags: '',
    materialContent: '',
    sourceBatchNo: '',
    qualityScore: 80,
    status: '0',
    remark: '',
    options: defaultOptionsForType('single'),
  })
  previousQuestionType.value = 'single'
  questionStep.value = 0
}

function resetAiForm() {
  Object.assign(aiForm, {
    modelId: undefined,
    courseId: undefined,
    catalogId: undefined,
    knowledgePointId: undefined,
    questionType: 'single',
    difficultyLevel: 3,
    count: 5,
    userInstruction: '',
    source: 'AI智能出题',
    status: '0',
    saveToBank: true,
  })
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.courseId = undefined
  queryParams.catalogId = undefined
  queryParams.questionType = ''
  queryParams.status = ''
  queryParams.stem = ''
  getList()
}

function normalizeOptionsForSubmit() {
  return (form.options || [])
    .filter((item: any) => item && item.optionContent)
    .map((item: any, index: number) => ({
      optionId: item.optionId,
      optionKey: item.optionKey || String.fromCharCode(65 + index),
      optionContent: item.optionContent,
      isRight: item.isRight === '1' ? '1' : '0',
    }))
}

async function getList() {
  loading.value = true
  try {
    const res = await listQuestion(queryParams)
    const stemKeyword = String(queryParams.stem || '').trim().toLowerCase()
    const rows = res.rows || []
    dataList.value = stemKeyword
      ? rows.filter((item: any) => stripHtml(item.stem).toLowerCase().includes(stemKeyword))
      : rows
    total.value = stemKeyword ? dataList.value.length : (res.total || 0)
  } finally {
    loading.value = false
  }
}

function handleSelectionChange(selection: any[]) {
  selectedIds.value = selection.map((item) => item.questionId)
}

function handleAdd() {
  resetFormData()
  title.value = '新增题目'
  open.value = true
}

async function handleEdit(row: any) {
  const res = await getQuestionDetail(row.questionId)
  const data = res.data || {}
  Object.assign(form, {
    questionId: data.questionId,
    courseId: data.courseId,
    catalogId: data.catalogId,
    knowledgePointId: data.knowledgePointId,
    questionType: data.questionType || 'single',
    difficultyLevel: data.difficultyLevel || 3,
    stem: data.stem || '<p></p>',
    answer: data.answer || '',
    analysis: data.analysis || '',
    source: data.source || '',
    questionTags: data.questionTags || '',
    materialContent: data.materialContent || '',
    sourceBatchNo: data.sourceBatchNo || '',
    qualityScore: data.qualityScore || 0,
    status: data.status || '0',
    remark: data.remark || '',
    options: Array.isArray(data.options) ? data.options.map((item: any) => ({
      optionId: item.optionId,
      optionKey: item.optionKey,
      optionContent: item.optionContent,
      isRight: item.isRight || '0',
    })) : [],
  })
  if (currentTypeRequiresOptions.value && !form.options.length) {
    form.options = buildOptionsForType(form.questionType)
  }
  previousQuestionType.value = form.questionType
  questionStep.value = 0
  title.value = '编辑题目'
  open.value = true
}

async function handleView(row: any) {
  const res = await getQuestionDetail(row.questionId)
  detail.value = res.data || {}
  detailOpen.value = true
}

async function handleVersionHistory(row: any) {
  const res = await listLegacyQuestionVersions(row.questionId)
  versionDetail.value = res.data || { item: null, versions: [] }
  versionOpen.value = true
}

async function handleDelete(row?: any) {
  const ids = row?.questionId ? [row.questionId] : selectedIds.value
  if (!ids.length) {
    ElMessage.warning('请先选择题目')
    return
  }
  await ElMessageBox.confirm(`确认删除 ${ids.length} 道题目吗？`, '提示', { type: 'warning' })
  await delQuestion(ids)
  ElMessage.success('删除成功')
  getList()
}

function addOptionRow() {
  const nextKey = String.fromCharCode(65 + (form.options?.length || 0))
  form.options.push({ optionKey: nextKey, optionContent: '', isRight: '0' })
}

function removeOptionRow(index: number) {
  form.options.splice(index, 1)
}

function handleOptionRightChange(index: number) {
  if (form.questionType === 'single' || form.questionType === 'judge') {
    form.options = (form.options || []).map((item: any, itemIndex: number) => ({
      ...item,
      isRight: itemIndex === index ? item.isRight : '0',
    }))
  }
  if (currentTypeRequiresOptions.value) {
    form.answer = objectiveAnswerPreview.value
  }
}

function insertFillBlank() {
  const count = (String(form.stem || '').match(/data-fill-blank=/g) || []).length + 1
  const blankHtml = `<span class="question-fill-blank" data-fill-blank="${count}">第${count}空</span>`
  const quill = stemEditorRef.value?.getQuill?.()
  if (quill) {
    const range = quill.getSelection(true)
    const index = range?.index ?? quill.getLength()
    quill.clipboard.dangerouslyPasteHTML(index, blankHtml)
    return
  }
  form.stem = `${form.stem || '<p></p>'}${blankHtml}`
}

function handleEditorCustomAction(action: string) {
  if (action === 'fillBlank' && form.questionType === 'fill') {
    insertFillBlank()
  }
}

async function submitForm() {
  const payload = {
    questionId: form.questionId,
    catalogId: form.catalogId,
    courseId: form.courseId,
    knowledgePointId: form.knowledgePointId,
    questionType: form.questionType,
    difficultyLevel: form.difficultyLevel,
    stem: form.stem,
    answer: currentTypeRequiresOptions.value ? objectiveAnswerPreview.value : form.answer,
    analysis: form.analysis,
    source: form.source,
    questionTags: form.questionTags,
    materialContent: form.materialContent,
    sourceBatchNo: form.sourceBatchNo,
    qualityScore: form.qualityScore,
    status: form.status,
    remark: form.remark,
    options: normalizeOptionsForSubmit(),
  }
  if (form.questionId) {
    await updateQuestionDetail(payload)
    ElMessage.success('修改成功')
  } else {
    await addQuestionDetail(payload)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

function triggerImport() {
  fileInputRef.value?.click()
}

async function handleImportFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return
  if (!file.name.toLowerCase().endsWith('.xls') && !file.name.toLowerCase().endsWith('.xlsx')) {
    ElMessage.error('请选择 xls 或 xlsx 文件')
    target.value = ''
    return
  }
  const formData = new FormData()
  formData.append('file', file)
  formData.append('updateSupport', 'true')
  const res = await importQuestionData(formData)
  ElMessage.success(res.data?.message || res.msg || '导入成功')
  target.value = ''
  getList()
}

async function handleDownloadTemplate() {
  const blob = await downloadQuestionTemplate()
  saveAs(blob, 'question_import_template.xlsx')
}

function openAiDialog() {
  resetAiForm()
  aiOpen.value = true
}

function openAiTaskDialog() {
  aiTaskOpen.value = true
  loadAiTaskList()
}

async function loadAiTaskList() {
  aiTaskLoading.value = true
  try {
    const res = await listAiGenerateQuestionTask({
      courseId: aiForm.courseId || queryParams.courseId,
      taskStatus: aiTaskFilterStatus.value || undefined,
    })
    aiTaskList.value = Array.isArray(res.data) ? res.data : []
    const runningExists = aiTaskList.value.some((item: any) => item.taskStatus === 'RUNNING')
    if (runningExists || (currentAiTaskId.value && aiTaskOpen.value)) {
      startAiTaskPolling()
    } else if (!runningExists) {
      stopAiTaskPolling()
    }
  } finally {
    aiTaskLoading.value = false
  }
}

async function inspectAiTask(row: any) {
  const res = await getAiGenerateQuestionTask(row.taskId)
  const detailTask = res.data || {}
  const index = aiTaskList.value.findIndex((item: any) => Number(item.taskId) === Number(detailTask.taskId))
  if (index >= 0) {
    aiTaskList.value.splice(index, 1, detailTask)
  }
  currentAiTaskId.value = detailTask.taskId || row.taskId
  if (detailTask.taskStatus === 'SUCCESS') {
    openAiTaskResult(detailTask)
    return
  }
  if (detailTask.taskStatus === 'FAILED') {
    ElMessage.error(detailTask.errorMsg || taskResponse(detailTask).message || 'AI 出题任务失败')
    return
  }
  ElMessage.info(taskResponse(detailTask).message || '任务仍在运行中')
}

function fillAiResult(result: any) {
  Object.assign(aiResult, { generatedCount: 0, savedCount: 0, rawContent: '', questions: [] }, result || {})
}

async function openAiTaskResult(row: any) {
  const task = row?.responsePayload ? row : (await getAiGenerateQuestionTask(row.taskId)).data
  const response = taskResponse(task)
  if (!response?.result) {
    ElMessage.warning(response?.message || '任务结果暂不可用，请稍后再试')
    return
  }
  fillAiResult(response.result)
  aiProgressPercent.value = 100
  aiProgressText.value = response.message || 'AI 出题完成'
  aiResultOpen.value = true
  currentAiTaskId.value = task.taskId
  if (taskRequest(task).saveToBank) {
    getList()
  }
}

function buildRetryPayloadFromTask(task: any) {
  const payload = taskRequest(task) || {}
  return {
    modelId: payload.modelId,
    courseId: payload.courseId,
    knowledgePointId: payload.knowledgePointId,
    questionType: payload.questionType,
    difficultyLevel: payload.difficultyLevel,
    count: payload.count,
    saveToBank: payload.saveToBank !== false,
    userInstruction: payload.instruction || '',
  }
}

async function retryAiTask(task: any) {
  const payload = buildRetryPayloadFromTask(task)
  aiLoading.value = true
  try {
    aiProgressPercent.value = 15
    aiProgressText.value = `正在重新提交任务 #${task.taskId}...`
    const res = await submitAiGenerateQuestionTask(payload)
    currentAiTaskId.value = res.data?.taskId || null
    await loadAiTaskList()
    startAiTaskPolling()
    ElMessage.success(`已重新提交，新的任务ID：${res.data?.taskId || '-'}`)
  } finally {
    aiLoading.value = false
  }
}

function stopAiTaskPolling() {
  if (aiTaskPollTimer.value) {
    window.clearTimeout(aiTaskPollTimer.value)
    aiTaskPollTimer.value = null
  }
}

function startAiTaskPolling() {
  stopAiTaskPolling()
  aiTaskPollTimer.value = window.setTimeout(async () => {
    if (!aiTaskOpen.value && !currentAiTaskId.value) {
      stopAiTaskPolling()
      return
    }
    await loadAiTaskList()
    const currentTask = currentAiTaskId.value
      ? aiTaskList.value.find((item: any) => Number(item.taskId) === Number(currentAiTaskId.value))
      : null
    if (currentTask?.taskStatus === 'SUCCESS') {
      await openAiTaskResult(currentTask)
      stopAiTaskPolling()
      return
    }
    if (currentTask?.taskStatus === 'FAILED') {
      ElMessage.error(currentTask.errorMsg || taskResponse(currentTask).message || 'AI 出题任务失败')
      stopAiTaskPolling()
      return
    }
    if (aiTaskList.value.some((item: any) => item.taskStatus === 'RUNNING') || currentAiTaskId.value) {
      startAiTaskPolling()
    }
  }, 3000)
}

async function submitAiGenerate() {
  aiLoading.value = true
  try {
    aiProgressPercent.value = 15
    aiProgressText.value = 'AI 出题任务已提交，正在排队...'
    const res = await submitAiGenerateQuestionTask(aiForm)
    currentAiTaskId.value = res.data?.taskId || null
    aiOpen.value = false
    aiTaskOpen.value = true
    await loadAiTaskList()
    startAiTaskPolling()
    ElMessage.success(res.data?.message || 'AI 出题任务已提交')
  } finally {
    aiLoading.value = false
  }
}

async function loadBaseOptions() {
  const [courseRes, typeRes, modelRes, kpRes] = await Promise.all([
    fetchCourseOptions(),
    listQuestionTypeMeta(),
    fetchAiModelOptions('chat'),
    listKnowledgePoint({ pageNum: 1, pageSize: 500, status: '0' }),
  ])
  const catalogRes = await listQuestionCatalog({ pageNum: 1, pageSize: 500, status: '0' })
  const catalogRows = catalogRes.rows || []
  const normalizedCatalogs = catalogRows.map((item: any) => ({
    label: `${item.catalogName}（${item.catalogId}）`,
    value: item.catalogId,
    courseId: item.courseId,
  }))
  courseOptions.value = courseRes || []
  questionTypeOptions.value = typeRes.data || []
  aiModelOptions.value = modelRes || []
  knowledgePointOptions.value = (kpRes.rows || []).map((item: any) => ({
    label: `${item.knowledgeName}（${item.knowledgePointId}）`,
    value: item.knowledgePointId,
    courseId: item.courseId,
  }))
  catalogOptions.value = normalizedCatalogs
}

onMounted(async () => {
  resetFormData()
  resetAiForm()
  await loadBaseOptions()
  await getList()
  if (route.query.openQuestionId) {
    const questionId = Number(route.query.openQuestionId)
    if (questionId) {
      await handleView({ questionId })
      router.replace({ path: route.path, query: { ...route.query, openQuestionId: undefined } })
    }
  }
})

watch(
  () => form.questionType,
  (value) => {
    const nextType = value || 'single'
    const prevType = previousQuestionType.value
    if (nextType !== prevType) {
      if (currentTypeRequiresOptions.value || nextType === 'judge' || nextType === 'single' || nextType === 'multiple') {
        form.options = buildOptionsForType(nextType, form.options || [])
      } else {
        form.options = []
      }
      previousQuestionType.value = nextType
    }
    form.answer = currentTypeRequiresOptions.value ? objectiveAnswerPreview.value : (form.answer || '')
  },
)

watch(
  () => form.options,
  () => {
    if (currentTypeRequiresOptions.value) {
      form.answer = objectiveAnswerPreview.value
    }
  },
  { deep: true },
)

watch(aiTaskOpen, (value) => {
  if (value) {
    loadAiTaskList()
    return
  }
  if (!currentAiTaskId.value) {
    stopAiTaskPolling()
  }
})

onUnmounted(() => {
  stopAiTaskPolling()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.mt12 { margin-top: 12px; }
.mt16 { margin-top: 16px; }
.hidden-input { display: none; }
.step-panel {
  min-height: 420px;
}
.editor-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.option-editor {
  margin-top: 8px;
  padding: 16px;
  border-radius: 14px;
  background: linear-gradient(180deg, #f8fbff 0%, #f4f8fc 100%);
  border: 1px solid var(--el-border-color-lighter);
}
.option-editor__header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 14px;
}
.panel-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}
.panel-subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
.task-toolbar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}
.task-toolbar__alert {
  flex: 1;
}
.task-toolbar__actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.step-nav { display: flex; gap: 8px; margin-bottom: 16px; flex-wrap: wrap; }
.step-nav__item { display: inline-flex; align-items: center; gap: 8px; border: 1px solid var(--el-border-color); background: #fff; border-radius: 6px; padding: 8px 14px; cursor: pointer; transition: all .2s; font-size: 14px; }
.step-nav__item.is-active { color: var(--el-color-primary); border-color: var(--el-color-primary-light-5); background: var(--el-color-primary-light-9); }
.step-nav__index { width: 20px; height: 20px; border-radius: 50%; background: var(--el-fill-color); display: inline-flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 700; }
.step-nav__item.is-active .step-nav__index { background: var(--el-color-primary); color: #fff; }
.rich-preview :deep(p) {
  margin: 0 0 8px;
}
.version-card {
  padding: 14px;
  border-radius: 12px;
  background: linear-gradient(180deg, #f9fbff 0%, #f4f8fc 100%);
  border: 1px solid var(--el-border-color-lighter);
}
.version-card__head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 10px;
}
.version-card__title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  font-size: 15px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}
.version-card__meta {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
.version-card__body {
  display: grid;
  gap: 8px;
  color: var(--el-text-color-regular);
  line-height: 1.8;
}
.rich-preview :deep(.question-fill-blank),
:deep(.ql-editor .question-fill-blank) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 88px;
  padding: 2px 10px;
  margin: 0 4px;
  border-bottom: 2px solid var(--el-color-primary);
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary-dark-2);
  border-radius: 10px;
  font-weight: 700;
}
</style>
