<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="学期">
        <el-select v-model="queryParams.termId" filterable clearable style="width: 200px">
          <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="班级课程">
        <el-tree-select
          v-model="queryParams.classCourseId"
          class="score-class-course-select"
          :data="classCourseTreeOptions"
          :props="{ value: 'id', label: 'label', children: 'children', disabled: 'disabled' }"
          :render-content="renderClassCourseNode"
          popper-class="score-class-course-popper"
          value-key="id"
          check-strictly
          clearable
          filterable
          node-key="id"
          placeholder="请选择"
          style="width: 360px"
          @change="handleClassCourseChange"
        >
          <template #label>
            <el-tooltip
              :disabled="!currentClassCourseLabel"
              :content="currentClassCourseLabel"
              placement="top"
              effect="light"
              teleported
              :show-after="250"
            >
              <span class="score-class-course-selected">{{ currentClassCourseShortLabel || currentClassCourseLabel }}</span>
            </el-tooltip>
          </template>
        </el-tree-select>
      </el-form-item>
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" filterable clearable style="width: 220px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="班级">
        <el-select v-model="queryParams.classId" filterable clearable style="width: 220px">
          <el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="学生">
        <el-select v-model="queryParams.studentUserId" filterable clearable style="width: 220px">
          <el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="发布状态">
        <el-select v-model="queryParams.publishStatus" clearable style="width: 160px">
          <el-option label="草稿" value="0" />
          <el-option label="已发布" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="reloadAll">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="score-actions mb16">
      <div class="score-actions__group">
        <el-button type="primary" class="score-actions__btn" @click="handleInitLedger" :disabled="!queryParams.classCourseId">生成台账</el-button>
        <el-button type="warning" class="score-actions__btn" @click="handleSyncExam" :disabled="!queryParams.classCourseId">同步考试成绩</el-button>
        <el-button type="info" plain class="score-actions__btn" @click="openImportDialog" :disabled="!queryParams.classCourseId">导入成绩</el-button>
        <el-button type="success" plain class="score-actions__btn" @click="handlePublish('1')">发布成绩</el-button>
        <el-button plain class="score-actions__btn" @click="handlePublish('0')">取消发布</el-button>
        <el-button type="danger" plain class="score-actions__btn" :disabled="!selectedIds.length" @click="handleDelete()">删除</el-button>
      </div>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="reloadAll" />
    </div>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange" :row-class-name="rowClassName">
      <el-table-column type="selection" width="55" />
      <el-table-column label="学生" min-width="220">
        <template #default="{ row }">
          <div class="readable-cell">
            <strong>{{ row.studentName || `学生 ${row.studentUserId}` }}</strong>
            <span>{{ row.studentNo || `用户ID ${row.studentUserId}` }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="班级课程" min-width="300" show-overflow-tooltip>
        <template #default="{ row }">
          <div class="readable-cell">
            <strong>{{ row.courseName || '-' }}</strong>
            <span>{{ row.termName || '-' }} / {{ row.className || '-' }} / {{ row.teachingClassCode || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="过程分" min-width="220">
        <template #default="{ row }">
          <div class="score-tags">
            <span>平时 {{ row.usualScore ?? 0 }}</span>
            <span>考勤 {{ row.attendanceScore ?? 0 }}</span>
            <span>作业 {{ row.homeworkScore ?? 0 }}</span>
            <span>实验 {{ row.labScore ?? 0 }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="考试分" min-width="180">
        <template #default="{ row }">
          <div class="readable-cell">
            <strong>{{ row.examScore ?? 0 }}</strong>
            <span>同步均分 {{ row.examAvgScore ?? 0 }} / {{ row.examRecordCount || 0 }} 次考试</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="总评" width="120">
        <template #default="{ row }">
          <strong class="score-main">{{ row.totalScore ?? 0 }}</strong>
        </template>
      </el-table-column>
      <el-table-column label="等级" width="90" prop="gradeLevel" />
      <el-table-column label="绩点" width="90" prop="gradePoint" />
      <el-table-column label="排名" width="100">
        <template #default="{ row }">{{ row.rankNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="发布" width="100">
        <template #default="{ row }">
          <el-tag :type="row.publishStatus === '1' ? 'success' : 'info'">{{ row.publishStatus === '1' ? '已发布' : '草稿' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="评语" min-width="220" prop="teacherComment" show-overflow-tooltip />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="success" @click="handlePublish(row.publishStatus === '1' ? '0' : '1', row)">
            {{ row.publishStatus === '1' ? '取消发布' : '发布' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="importOpen" title="导入成绩" width="520px" append-to-body>
      <div class="score-import">
        <el-alert type="info" :closable="false" show-icon>
          <template #title>
            当前导入班级课程：{{ currentClassCourseLabel || '请先选择班级课程' }}
          </template>
        </el-alert>
        <el-upload
          ref="importUploadRef"
          :auto-upload="false"
          :limit="1"
          accept=".xls,.xlsx"
          :on-change="handleImportFileChange"
          :on-remove="handleImportFileRemove"
          :file-list="importFileList"
          drag
          style="width:100%"
        >
          <el-icon style="font-size:36px;color:#8c939d;margin-bottom:8px"><upload-filled /></el-icon>
          <div class="el-upload__text">将成绩文件拖到此处，或<em>点击上传</em></div>
          <template #tip>
            <div class="el-upload__tip">
              仅支持 `.xls` / `.xlsx` 文件。
              <el-button link type="primary" @click.stop="downloadImportTemplate">下载模板</el-button>
            </div>
          </template>
        </el-upload>
        <el-checkbox v-model="importUpdateSupport" class="score-import__checkbox">更新已有成绩记录</el-checkbox>
      </div>
      <template #footer>
        <el-button @click="importOpen = false">取消</el-button>
        <el-button type="primary" :loading="importing" @click="submitImport">开始导入</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="open" :title="title" width="860px">
      <el-form :model="form" label-width="100px">
        <div class="score-quick-tools">
          <div class="score-quick-tools__block">
            <div class="score-quick-tools__title">快捷权重</div>
            <div class="score-quick-tools__actions">
              <el-button
                v-for="item in weightPresetOptions"
                :key="item.key"
                size="small"
                :type="activeWeightPreset === item.key ? 'primary' : 'default'"
                @click="applyWeightPreset(item)"
              >
                {{ item.label }}
              </el-button>
              <el-button size="small" @click="restoreDefaultWeights">恢复默认</el-button>
            </div>
            <div class="score-quick-tools__hint">常用场景可直接切换为只保留平时分和考试分。</div>
          </div>

          <div class="score-quick-tools__block">
            <div class="score-quick-tools__title">快捷处理</div>
            <div class="score-quick-tools__actions">
              <el-button size="small" @click="syncExamScoreFromAvg" :disabled="!Number(form.examAvgScore || 0)">考试分 = 同步均分</el-button>
              <el-button size="small" @click="clearProcessWeights">清空过程权重</el-button>
              <el-button size="small" @click="clearProcessScores">清空过程分</el-button>
              <el-button size="small" @click="fillExamWeightToHundred" :disabled="weightTotal === 100">补足考试权重</el-button>
            </div>
          </div>

          <div class="score-quick-tools__block">
            <div class="score-quick-tools__title">快捷录分</div>
            <div class="score-quick-tools__actions">
              <el-button size="small" @click="applyUsualScoreFromProcessAverage">平时分 = 过程均值</el-button>
              <el-button size="small" @click="applyUsualScoreFromFilledProcessAverage">平时分 = 已填过程均值</el-button>
            </div>
            <div class="score-quick-tools__actions score-quick-tools__actions--compact">
              <el-button
                v-for="item in examScorePresetOptions"
                :key="item.key"
                size="small"
                :type="Number(form.examScore || 0) === item.score ? 'primary' : 'default'"
                @click="applyExamScorePreset(item)"
              >
                {{ item.label }}
              </el-button>
            </div>
            <div class="score-quick-tools__hint">优秀、良好、及格等常用考试分值可以一键套用，不用反复手输。</div>
          </div>

          <div class="score-weight-summary" :class="weightSummaryClass">
            <strong>权重合计 {{ weightTotal.toFixed(2) }}%</strong>
            <span>{{ weightSummaryText }}</span>
            <small>过程均值 {{ processAveragePreview.toFixed(2) }}，已填过程均值 {{ filledProcessAveragePreview.toFixed(2) }}</small>
          </div>
        </div>

        <div class="dialog-grid">
          <el-form-item label="平时分"><el-input-number v-model="form.usualScore" :min="0" :max="100" :precision="2" style="width:100%" /></el-form-item>
          <el-form-item label="平时权重"><el-input-number v-model="form.usualWeight" :min="0" :max="100" :precision="2" style="width:100%" /></el-form-item>
          <el-form-item label="考勤分"><el-input-number v-model="form.attendanceScore" :min="0" :max="100" :precision="2" style="width:100%" /></el-form-item>
          <el-form-item label="考勤权重"><el-input-number v-model="form.attendanceWeight" :min="0" :max="100" :precision="2" style="width:100%" /></el-form-item>
          <el-form-item label="作业分"><el-input-number v-model="form.homeworkScore" :min="0" :max="100" :precision="2" style="width:100%" /></el-form-item>
          <el-form-item label="作业权重"><el-input-number v-model="form.homeworkWeight" :min="0" :max="100" :precision="2" style="width:100%" /></el-form-item>
          <el-form-item label="实验分"><el-input-number v-model="form.labScore" :min="0" :max="100" :precision="2" style="width:100%" /></el-form-item>
          <el-form-item label="实验权重"><el-input-number v-model="form.labWeight" :min="0" :max="100" :precision="2" style="width:100%" /></el-form-item>
          <el-form-item label="考试分"><el-input-number v-model="form.examScore" :min="0" :max="100" :precision="2" style="width:100%" /></el-form-item>
          <el-form-item label="考试权重"><el-input-number v-model="form.examWeight" :min="0" :max="100" :precision="2" style="width:100%" /></el-form-item>
          <el-form-item label="同步均分"><el-input-number v-model="form.examAvgScore" disabled :precision="2" style="width:100%" /></el-form-item>
          <el-form-item label="考试次数"><el-input-number v-model="form.examRecordCount" disabled style="width:100%" /></el-form-item>
          <el-form-item label="加分"><el-input-number v-model="form.bonusScore" :min="0" :max="30" :precision="2" style="width:100%" /></el-form-item>
          <el-form-item label="扣分"><el-input-number v-model="form.penaltyScore" :min="0" :max="30" :precision="2" style="width:100%" /></el-form-item>
        </div>
        <div class="score-preview">
          <div class="score-preview__card"><span>预估总评</span><strong>{{ previewTotal }}</strong></div>
          <div class="score-preview__card"><span>预估等级</span><strong>{{ previewGradeLevel }}</strong></div>
          <div class="score-preview__card"><span>预估绩点</span><strong>{{ previewGradePoint }}</strong></div>
        </div>
        <el-form-item label="教师评语"><el-input v-model="form.teacherComment" type="textarea" rows="4" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, h, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox, ElTooltip } from 'element-plus'
import { delStudentScore, importStudentScore, initStudentScoreLedger, listStudentScore, publishStudentScore, syncStudentScoreExam, updateStudentScore } from '@/api/campus/score'
import { fetchClassOptions, fetchCourseOptions, fetchTermOptions, fetchUserOptions } from '@/api/campus/options'
import { listClassCourse } from '@/api/campus/teaching'
import { deptTreeSelect } from '@/api/system/user'

const { proxy } = getCurrentInstance() as any
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const dataList = ref<any[]>([])
const open = ref(false)
const importOpen = ref(false)
const importing = ref(false)
const importUpdateSupport = ref(true)
const importUploadRef = ref()
const importFileList = ref<any[]>([])
const importRawFile = ref<File | null>(null)
const title = ref('')
const selectedIds = ref<number[]>([])
const termOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const classOptions = ref<any[]>([])
const studentOptions = ref<any[]>([])
const classCourseOptions = ref<any[]>([])
const classCourseRows = ref<any[]>([])
const classCourseTreeOptions = ref<any[]>([])
const deptOptions = ref<any[]>([])
const form = reactive<any>({})
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, termId: undefined, classCourseId: undefined, courseId: undefined, classId: undefined, studentUserId: undefined, publishStatus: '' })

const previewTotal = computed(() => computeWeightedTotal(form))
const previewGradeLevel = computed(() => resolveGradeLevel(previewTotal.value))
const previewGradePoint = computed(() => resolveGradePoint(previewTotal.value).toFixed(2))
const currentClassCourseLabel = computed(() => classCourseOptions.value.find((item: any) => item.value === queryParams.classCourseId)?.label || '')
const currentClassCourseShortLabel = computed(() => classCourseOptions.value.find((item: any) => item.value === queryParams.classCourseId)?.shortLabel || '')
const weightPresetOptions = [
  { key: 'usual-30-exam-70', label: '平时 3 : 考试 7', usual: 30, exam: 70 },
  { key: 'usual-40-exam-60', label: '平时 4 : 考试 6', usual: 40, exam: 60 },
  { key: 'usual-50-exam-50', label: '平时 5 : 考试 5', usual: 50, exam: 50 },
  { key: 'usual-60-exam-40', label: '平时 6 : 考试 4', usual: 60, exam: 40 },
]
const examScorePresetOptions = [
  { key: 'excellent', label: '优秀 95', score: 95 },
  { key: 'good', label: '良好 85', score: 85 },
  { key: 'medium', label: '中等 75', score: 75 },
  { key: 'pass', label: '及格 60', score: 60 },
  { key: 'fail', label: '不及格 50', score: 50 },
]
const weightTotal = computed(() => {
  return Number(form.usualWeight || 0)
    + Number(form.attendanceWeight || 0)
    + Number(form.homeworkWeight || 0)
    + Number(form.labWeight || 0)
    + Number(form.examWeight || 0)
})
const processAveragePreview = computed(() => {
  return averageNumbers([
    Number(form.attendanceScore || 0),
    Number(form.homeworkScore || 0),
    Number(form.labScore || 0),
  ])
})
const filledProcessAveragePreview = computed(() => {
  return averageNumbers(
    [
      form.attendanceScore,
      form.homeworkScore,
      form.labScore,
    ]
      .map((item) => Number(item || 0))
      .filter((item) => item > 0)
  )
})
const activeWeightPreset = computed(() => {
  const attendance = Number(form.attendanceWeight || 0)
  const homework = Number(form.homeworkWeight || 0)
  const lab = Number(form.labWeight || 0)
  if (attendance || homework || lab) return ''
  const usual = Number(form.usualWeight || 0)
  const exam = Number(form.examWeight || 0)
  return weightPresetOptions.find((item) => item.usual === usual && item.exam === exam)?.key || ''
})
const weightSummaryClass = computed(() => {
  if (weightTotal.value === 100) return 'is-balanced'
  if (weightTotal.value < 100) return 'is-under'
  return 'is-over'
})
const weightSummaryText = computed(() => {
  if (weightTotal.value === 100) return '当前总权重为 100%，可直接保存。'
  if (weightTotal.value < 100) return `当前还差 ${(100 - weightTotal.value).toFixed(2)}%，可一键补足到考试权重。`
  return `当前超出 ${(weightTotal.value - 100).toFixed(2)}%，建议减少过程项或考试权重。`
})

function resetForm() {
  Object.assign(form, { scoreId: undefined, usualScore: 0, attendanceScore: 0, homeworkScore: 0, labScore: 0, examScore: 0, examAvgScore: 0, examRecordCount: 0, bonusScore: 0, penaltyScore: 0, usualWeight: 20, attendanceWeight: 10, homeworkWeight: 20, labWeight: 10, examWeight: 40, teacherComment: '', remark: '' })
}

function applyWeightPreset(item: { usual: number; exam: number }) {
  form.usualWeight = item.usual
  form.examWeight = item.exam
  form.attendanceWeight = 0
  form.homeworkWeight = 0
  form.labWeight = 0
}

function restoreDefaultWeights() {
  form.usualWeight = 20
  form.attendanceWeight = 10
  form.homeworkWeight = 20
  form.labWeight = 10
  form.examWeight = 40
}

function syncExamScoreFromAvg() {
  form.examScore = Number(form.examAvgScore || 0)
}

function clearProcessWeights() {
  form.attendanceWeight = 0
  form.homeworkWeight = 0
  form.labWeight = 0
}

function clearProcessScores() {
  form.attendanceScore = 0
  form.homeworkScore = 0
  form.labScore = 0
}

function fillExamWeightToHundred() {
  const otherWeights = Number(form.usualWeight || 0)
    + Number(form.attendanceWeight || 0)
    + Number(form.homeworkWeight || 0)
    + Number(form.labWeight || 0)
  form.examWeight = Math.max(0, Number((100 - otherWeights).toFixed(2)))
}

function applyUsualScoreFromProcessAverage() {
  form.usualScore = Number(processAveragePreview.value.toFixed(2))
}

function applyUsualScoreFromFilledProcessAverage() {
  form.usualScore = Number(filledProcessAveragePreview.value.toFixed(2))
}

function applyExamScorePreset(item: { score: number }) {
  form.examScore = item.score
}

function averageNumbers(values: number[]) {
  if (!values.length) return 0
  const total = values.reduce((sum, item) => sum + item, 0)
  return total / values.length
}

async function loadOptions() {
  const [terms, courses, classes, students, classCourseRes, deptRes] = await Promise.all([
    fetchTermOptions(),
    fetchCourseOptions(),
    fetchClassOptions(),
    fetchUserOptions('student'),
    listClassCourse({ pageNum: 1, pageSize: 500, status: '0' }),
    deptTreeSelect(),
  ])
  termOptions.value = terms
  courseOptions.value = courses
  classOptions.value = classes
  studentOptions.value = students
  classCourseRows.value = classCourseRes.rows || []
  deptOptions.value = Array.isArray(deptRes.data) ? deptRes.data : []
  classCourseOptions.value = classCourseRows.value.map((item: any) => ({
    label: `${item.termName || '未配置学期'} / ${item.className || '未配置班级'} / ${item.courseName || '未命名课程'} / ${item.teachingClassCode || item.id}`,
    shortLabel: `${item.className || '未配置班级'} / ${item.courseName || '未命名课程'} / ${item.teachingClassCode || item.id}`,
    value: item.id,
    raw: item,
  }))
  rebuildClassCourseTree()
}

async function getList() {
  loading.value = true
  try {
    const res = await listStudentScore(queryParams)
    dataList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function reloadAll() {
  await getList()
}

function resetQuery() {
  Object.assign(queryParams, { pageNum: 1, pageSize: 10, termId: undefined, classCourseId: undefined, courseId: undefined, classId: undefined, studentUserId: undefined, publishStatus: '' })
  reloadAll()
}

function handleSelectionChange(selection: any[]) {
  selectedIds.value = selection.map((item: any) => Number(item.scoreId))
}

function openImportDialog() {
  if (!queryParams.classCourseId) {
    ElMessage.warning('请先选择班级课程')
    return
  }
  importOpen.value = true
  importUpdateSupport.value = true
  importRawFile.value = null
  importFileList.value = []
}

function rebuildClassCourseTree() {
  const rows = (classCourseRows.value || []).filter((item: any) => {
    if (queryParams.termId && item.termId !== queryParams.termId) return false
    if (queryParams.courseId && item.courseId !== queryParams.courseId) return false
    if (queryParams.classId && item.classId !== queryParams.classId) return false
    return true
  })
  const deptLabelMap = new Map<string, string>()
  const walkDept = (nodes: any[] = []) => {
    nodes.forEach((node) => {
      deptLabelMap.set(String(node.id), node.label)
      if (node.children?.length) walkDept(node.children)
    })
  }
  walkDept(deptOptions.value || [])

  const deptMap = new Map<string, any>()
  rows.forEach((item: any) => {
    const deptKey = String(item.openDeptId || 'unassigned')
    const classKey = String(item.classId || `class-${item.id}`)
    if (!deptMap.has(deptKey)) {
      deptMap.set(deptKey, {
        id: `dept-${deptKey}`,
        label: deptLabelMap.get(deptKey) || item.openDeptName || '未配置部门',
        displayLabel: deptLabelMap.get(deptKey) || item.openDeptName || '未配置部门',
        tooltip: deptLabelMap.get(deptKey) || item.openDeptName || '未配置部门',
        nodeType: 'dept',
        countValue: 0,
        countLabel: '0',
        disabled: true,
        children: [],
      })
    }
    const deptNode = deptMap.get(deptKey)
    let classNode = deptNode.children.find((node: any) => node.id === `class-${classKey}`)
    if (!classNode) {
      classNode = {
        id: `class-${classKey}`,
        label: item.className || '未配置班级',
        displayLabel: item.className || '未配置班级',
        tooltip: `${deptLabelMap.get(deptKey) || item.openDeptName || '未配置部门'} / ${item.className || '未配置班级'}`,
        nodeType: 'class',
        countValue: 0,
        countLabel: '0门',
        disabled: true,
        children: [],
      }
      deptNode.children.push(classNode)
    }
    classNode.children.push({
      id: item.id,
      label: `${item.courseName || '未命名课程'} / ${item.teachingClassCode || item.id}${item.termName ? ` / ${item.termName}` : ''}`,
      displayLabel: `${item.courseName || '未命名课程'} / ${item.teachingClassCode || item.id}${item.termName ? ` / ${item.termName}` : ''}`,
      tooltip: `${deptLabelMap.get(deptKey) || item.openDeptName || '未配置部门'} / ${item.className || '未配置班级'} / ${item.courseName || '未命名课程'} / ${item.teachingClassCode || item.id}${item.termName ? ` / ${item.termName}` : ''}`,
      nodeType: 'course',
      disabled: false,
      raw: item,
    })
  })
  Array.from(deptMap.values()).forEach((deptNode: any) => {
    let deptCount = 0
    deptNode.children.forEach((classNode: any) => {
      const classCount = classNode.children.length
      classNode.countValue = classCount
      classNode.countLabel = `${classCount}门`
      deptCount += classCount
    })
    deptNode.countValue = deptCount
    deptNode.countLabel = `${deptCount}`
  })
  classCourseTreeOptions.value = Array.from(deptMap.values())
  const currentExists = rows.some((item: any) => item.id === queryParams.classCourseId)
  if (queryParams.classCourseId && !currentExists) {
    queryParams.classCourseId = undefined
  }
}

function renderClassCourseNode(renderHelper: any, { data }: any) {
  const content = h('div', { class: ['score-tree-node', `is-${data.nodeType || 'course'}`] }, [
    h('span', { class: 'score-tree-node__label' }, data.displayLabel || data.label || ''),
    data.countLabel
      ? h('span', { class: 'score-tree-node__count' }, `（${data.countLabel}）`)
      : null,
  ])
  return h(
    ElTooltip,
    {
      content: data.tooltip || data.displayLabel || data.label || '',
      placement: 'right',
      effect: 'light',
      teleported: true,
      showAfter: 250,
    },
    { default: () => content }
  )
}

function handleClassCourseChange(value: any) {
  const target = classCourseOptions.value.find((item: any) => item.value === value)?.raw
  if (!target) return
  queryParams.courseId = target.courseId
  queryParams.classId = target.classId
}

function ensureClassCourseId(targetRow?: any) {
  const classCourseId = targetRow?.classCourseId || queryParams.classCourseId
  if (!classCourseId) {
    ElMessage.warning('请先选择班级课程')
    return undefined
  }
  return Number(classCourseId)
}

async function handleInitLedger() {
  const classCourseId = ensureClassCourseId()
  if (!classCourseId) return
  const res = await initStudentScoreLedger(classCourseId)
  ElMessage.success(`台账生成完成：新增 ${res.data?.insertedCount || 0} 条，同步 ${res.data?.syncedCount || 0} 条`)
  reloadAll()
}

async function handleSyncExam() {
  const classCourseId = ensureClassCourseId()
  if (!classCourseId) return
  const res = await syncStudentScoreExam(classCourseId)
  ElMessage.success(`已同步 ${res.data?.updatedCount || 0} 条成绩记录`)
  reloadAll()
}

async function handlePublish(status: '0' | '1', row?: any) {
  if (!row && !selectedIds.value.length && !queryParams.classCourseId) {
    ElMessage.warning('请先勾选成绩记录，或先选择班级课程')
    return
  }
  await publishStudentScore({ publishStatus: status, scoreIds: row ? [row.scoreId] : selectedIds.value, classCourseId: row ? row.classCourseId : queryParams.classCourseId })
  ElMessage.success(status === '1' ? '成绩已发布' : '已取消发布')
  reloadAll()
}

function handleEdit(row: any) {
  resetForm()
  Object.assign(form, row)
  title.value = `编辑成绩：${row.studentName || row.studentUserId}`
  open.value = true
}

async function submitForm() {
  await updateStudentScore(form)
  ElMessage.success('保存成功')
  open.value = false
  reloadAll()
}

async function handleDelete(row?: any) {
  const target = row ? [row.scoreId] : selectedIds.value
  if (!target.length) return
  await ElMessageBox.confirm('确认删除所选成绩记录吗？', '提示', { type: 'warning' })
  await delStudentScore(target)
  ElMessage.success('删除成功')
  reloadAll()
}

function handleImportFileChange(file: any, fileList: any[]) {
  importRawFile.value = file.raw || null
  importFileList.value = fileList.slice(-1)
}

function handleImportFileRemove() {
  importRawFile.value = null
  importFileList.value = []
}

function downloadImportTemplate() {
  const classCourseId = ensureClassCourseId()
  if (!classCourseId) return
  proxy.download('campus/studentScore/importTemplate', { classCourseId }, `student_score_template_${classCourseId}_${new Date().getTime()}.xlsx`)
}

async function submitImport() {
  const classCourseId = ensureClassCourseId()
  if (!classCourseId) return
  if (!importRawFile.value) {
    ElMessage.warning('请先选择导入文件')
    return
  }
  importing.value = true
  try {
    const res = await importStudentScore(importRawFile.value, classCourseId, importUpdateSupport.value)
    const data = res.data || {}
    const errors = (data.errors || []) as string[]
    importOpen.value = false
    ElMessageBox.alert(
      `导入完成：成功 ${data.successCount || 0} 条，跳过 ${data.skipCount || 0} 条${errors.length ? `<br/><br/>${errors.slice(0, 8).join('<br/>')}` : ''}`,
      '导入结果',
      { dangerouslyUseHTMLString: true }
    )
    reloadAll()
  } finally {
    importing.value = false
  }
}

function computeWeightedTotal(row: any) {
  const usualWeight = Number(row.usualWeight || 0)
  const attendanceWeight = Number(row.attendanceWeight || 0)
  const homeworkWeight = Number(row.homeworkWeight || 0)
  const labWeight = Number(row.labWeight || 0)
  const examWeight = Number(row.examWeight || 0)
  const weightTotal = usualWeight + attendanceWeight + homeworkWeight + labWeight + examWeight
  if (!weightTotal) return 0
  const weighted = (
    Number(row.usualScore || 0) * usualWeight +
    Number(row.attendanceScore || 0) * attendanceWeight +
    Number(row.homeworkScore || 0) * homeworkWeight +
    Number(row.labScore || 0) * labWeight +
    Number(row.examScore || 0) * examWeight
  ) / weightTotal
  return Math.max(0, Number((weighted + Number(row.bonusScore || 0) - Number(row.penaltyScore || 0)).toFixed(2)))
}

function resolveGradeLevel(score: number) {
  if (score >= 95) return 'A+'
  if (score >= 90) return 'A'
  if (score >= 85) return 'B+'
  if (score >= 80) return 'B'
  if (score >= 75) return 'C+'
  if (score >= 70) return 'C'
  if (score >= 60) return 'D'
  return 'F'
}

function resolveGradePoint(score: number) {
  if (score >= 95) return 4.0
  if (score >= 90) return 3.8
  if (score >= 85) return 3.5
  if (score >= 80) return 3.0
  if (score >= 75) return 2.5
  if (score >= 70) return 2.0
  if (score >= 60) return 1.0
  return 0
}

function rowClassName({ row }: any) {
  if (Number(row.totalScore || 0) < 60) return 'score-row-warning'
  if (Number(row.totalScore || 0) >= 90) return 'score-row-excellent'
  return ''
}

watch(() => [queryParams.termId, queryParams.courseId, queryParams.classId], () => {
  rebuildClassCourseTree()
})

onMounted(async () => {
  resetForm()
  await loadOptions()
  await reloadAll()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.score-actions{display:flex;align-items:center;justify-content:space-between;gap:16px;flex-wrap:wrap}
.score-actions__group{display:flex;align-items:center;gap:12px;flex-wrap:wrap}
.score-actions__btn{min-width:112px}
.score-class-course-selected{display:block;max-width:100%;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}
.score-tree-node{display:flex;align-items:center;gap:8px;min-width:0;max-width:100%}
.score-tree-node__label{min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;color:#172033}
.score-tree-node__count{flex:0 0 auto;color:#8a97ab;font-size:12px}
.score-tree-node.is-dept .score-tree-node__label{font-weight:700;color:#243b63}
.score-tree-node.is-class .score-tree-node__label{font-weight:600}
.score-tree-node.is-course .score-tree-node__label{font-size:12px;color:#44556b}
.score-class-course-select :deep(.el-select__selected-item){min-width:0;max-width:100%;display:flex}
.score-class-course-select :deep(.el-select__selection){min-width:0}
.readable-cell{display:flex;flex-direction:column;gap:4px}
.readable-cell strong{color:#172033;font-size:13px}
.readable-cell span{color:#667085;font-size:12px}
.score-tags{display:flex;flex-wrap:wrap;gap:6px}
.score-tags span{padding:4px 8px;border-radius:999px;background:#f5f8fc;color:#526076;font-size:12px}
.score-import{display:grid;gap:16px}
.score-import__checkbox{margin-top:-4px}
.score-quick-tools{display:grid;gap:12px;margin-bottom:14px;padding:14px;border-radius:14px;background:#f8fbff;border:1px solid #dbe8f7}
.score-quick-tools__block{display:grid;gap:8px}
.score-quick-tools__title{color:#172033;font-size:13px;font-weight:700}
.score-quick-tools__actions{display:flex;align-items:center;gap:8px;flex-wrap:wrap}
.score-quick-tools__actions--compact .el-button{min-width:88px}
.score-quick-tools__hint{color:#667085;font-size:12px;line-height:1.6}
.score-weight-summary{display:grid;gap:6px;padding:12px 14px;border-radius:12px;border:1px solid #dbe8f7;background:#fff}
.score-weight-summary strong{color:#172033;font-size:15px}
.score-weight-summary span,
.score-weight-summary small{color:#667085;font-size:12px;line-height:1.6}
.score-weight-summary.is-balanced{border-color:#bfe0cb;background:#f4fbf7}
.score-weight-summary.is-balanced strong{color:#16794d}
.score-weight-summary.is-under{border-color:#f0d59e;background:#fffaf0}
.score-weight-summary.is-under strong{color:#946200}
.score-weight-summary.is-over{border-color:#efc2ba;background:#fff5f3}
.score-weight-summary.is-over strong{color:#bf4b3b}
.score-main{font-size:18px;color:#172033}
.dialog-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:8px 16px}
.score-preview{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:10px;margin:6px 0 18px}
.score-preview__card{padding:14px;border-radius:14px;background:#f6f9fc;border:1px solid #e5edf6}
.score-preview__card span{display:block;color:#667085;font-size:12px}
.score-preview__card strong{display:block;margin-top:6px;color:#172033;font-size:20px}
:global(.score-class-course-popper){min-width:520px!important}
:global(.score-class-course-popper .el-tree){padding:4px 0}
:global(.score-class-course-popper .el-tree-node__content){height:34px;padding-right:12px}
:deep(.score-row-warning){--el-table-tr-bg-color:#fff5f5}
:deep(.score-row-excellent){--el-table-tr-bg-color:#f1fbf4}
@media (max-width: 1200px){.dialog-grid,.score-preview{grid-template-columns:1fr}}
</style>
