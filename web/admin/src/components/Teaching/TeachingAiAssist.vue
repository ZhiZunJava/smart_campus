<template>
  <section class="teaching-ai-compact">
    <div class="teaching-ai-compact__bar">
      <div class="teaching-ai-compact__title">
        <div class="teaching-ai-compact__icon"><i class="ri-ai-generate-2"></i></div>
        <div>
          <strong>AI 配置助手</strong>
          <p>{{ moduleLabel }}支持生成与优化，默认保持紧凑，避免打断表单录入。</p>
        </div>
      </div>

      <div class="teaching-ai-compact__actions">
        <el-button link type="primary" @click="showAdvanced = !showAdvanced">
          {{ showAdvanced ? '收起高级项' : '高级选项' }}
        </el-button>
        <el-button :loading="loading" @click="runAssist('generate')">
          <i class="ri-sparkling-line"></i>
          一键配置
        </el-button>
        <el-button type="primary" :loading="loading" @click="runAssist('optimize')">
          <i class="ri-magic-line"></i>
          一键优化
        </el-button>
      </div>
    </div>

    <div class="teaching-ai-compact__editor">
      <el-input
        v-model="instruction"
        type="textarea"
        :rows="2"
        resize="none"
        placeholder="补充你的要求，例如：编码更正式、适合教务维护、与当前数据风格保持一致"
      />
    </div>

    <div v-if="showAdvanced" class="teaching-ai-compact__advanced">
      <div class="teaching-ai-compact__field">
        <label>命名风格</label>
        <el-select v-model="namingStyle" placeholder="选择风格">
          <el-option v-for="item in namingOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>
      <div class="teaching-ai-compact__field">
        <label>优先目标</label>
        <el-select v-model="priority" placeholder="选择目标">
          <el-option v-for="item in priorityOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>
      <div class="teaching-ai-compact__field teaching-ai-compact__field--wide">
        <label>快捷要求</label>
        <div class="teaching-ai-compact__chips">
          <button
            v-for="item in quickPrompts"
            :key="item"
            type="button"
            class="teaching-ai-compact__chip"
            @click="appendPrompt(item)"
          >
            {{ item }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="hasResult" class="teaching-ai-compact__result">
      <div class="teaching-ai-compact__result-head">
        <div>
          <div class="teaching-ai-compact__result-title">{{ result.assistantTitle || 'AI 建议' }}</div>
          <p>{{ resultSummary }}</p>
        </div>
        <div class="teaching-ai-compact__result-tools">
          <el-tag v-if="lastActionLabel" type="info" effect="plain">{{ lastActionLabel }}</el-tag>
          <el-button link type="primary" @click="showResult = !showResult">
            {{ showResult ? '收起建议' : '展开建议' }}
          </el-button>
        </div>
      </div>

      <div v-if="result.formDraftKeys.length" class="teaching-ai-compact__draft-tags">
        <el-tag v-for="item in result.formDraftKeys" :key="item" effect="plain" type="success">{{ toLabel(item) }}</el-tag>
      </div>

      <div v-if="showResult" class="teaching-ai-compact__result-body">
        <div v-if="result.highlights.length" class="teaching-ai-compact__group">
          <span>推荐亮点</span>
          <ul>
            <li v-for="item in result.highlights" :key="item">{{ item }}</li>
          </ul>
        </div>
        <div v-if="result.warnings.length" class="teaching-ai-compact__group is-warning">
          <span>注意事项</span>
          <ul>
            <li v-for="item in result.warnings" :key="item">{{ item }}</li>
          </ul>
        </div>
        <div v-if="result.tips.length" class="teaching-ai-compact__group is-tip">
          <span>落地建议</span>
          <ul>
            <li v-for="item in result.tips" :key="item">{{ item }}</li>
          </ul>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { assistTeachingConfig } from '@/api/campus/teachingAi'

const props = defineProps<{
  moduleKey: string
  moduleLabel: string
  formData: Record<string, any>
  selectedRows?: any[]
  availableOptions?: Record<string, any>
}>()

const emit = defineEmits<{
  (e: 'apply', payload: Record<string, any>): void
}>()

const loading = ref(false)
const instruction = ref('')
const namingStyle = ref('standard')
const priority = ref('usable')
const lastActionLabel = ref('')
const showAdvanced = ref(false)
const showResult = ref(true)

const namingOptions = [
  { label: '标准规范', value: 'standard' },
  { label: '正式严谨', value: 'formal' },
  { label: '简洁易读', value: 'simple' },
  { label: '便于教务维护', value: 'maintainable' },
]

const priorityOptions = [
  { label: '优先可用', value: 'usable' },
  { label: '优先规范', value: 'consistent' },
  { label: '优先扩展', value: 'extensible' },
  { label: '优先自动化', value: 'automation' },
]

const quickPrompts = computed(() => [
  '命名规范统一',
  '更适合教务老师维护',
  '尽量减少手工修改',
  '和现有数据风格保持一致',
])

const result = reactive({
  assistantTitle: '',
  highlights: [] as string[],
  warnings: [] as string[],
  tips: [] as string[],
  formDraftKeys: [] as string[],
})

const hasResult = computed(() => {
  return Boolean(result.assistantTitle || result.highlights.length || result.warnings.length || result.tips.length || result.formDraftKeys.length)
})

const resultSummary = computed(() => {
  if (loading.value) return 'AI 正在结合当前表单与上下文生成建议。'
  if (result.formDraftKeys.length) return `已生成 ${result.formDraftKeys.length} 个可回填字段，建议先看结果再决定是否保留。`
  return '这里展示 AI 的简明建议。'
})

const fieldLabelMap: Record<string, string> = {
  gradeId: '年级',
  classId: '班级',
  className: '班级名称',
  deptId: '部门',
  headTeacherId: '班主任',
  courseId: '课程',
  courseName: '课程名称',
  courseCode: '课程编码',
  subjectType: '学科类型',
  studentUserId: '学生',
  termId: '学期',
  termName: '学期名称',
  schoolYear: '学年',
  termCode: '学期编码',
  sortOrder: '排序',
  isCurrent: '当前学期',
  teacherId: '教师',
  weeklyHours: '周学时',
  totalHours: '总课时',
  requiredWeeks: '要求周数',
  arrangedHours: '已排课时',
  teachingClassCode: '教学班代码',
  openDeptId: '开课部门',
  major: '专业',
  campusName: '授课校区',
  assessmentType: '考核方式',
  teachingLanguage: '授课语言',
  prerequisiteCourse: '先修课程',
  taskType: '任务类型',
  requiredFlag: '是否必修',
  courseLevelRequirement: '等级要求',
  credits: '学分',
  courseCategory: '课程类别',
  studentLimit: '人数上限',
  weekDay: '星期',
  startSection: '开始节次',
  endSection: '结束节次',
  classroom: '教室',
  weeksText: '周次',
  status: '状态',
  remark: '备注',
  knowledgeName: '知识点名称',
  difficultyLevel: '难度',
  keyword: '关键词',
  description: '描述',
  parentId: '父级ID',
  chapterName: '章节名称',
  chapterOrder: '排序',
  intro: '简介',
  businessType: '业务类型',
}

function appendPrompt(text: string) {
  instruction.value = instruction.value ? `${instruction.value}；${text}` : text
}

function toLabel(key: string) {
  return fieldLabelMap[key] || key
}

function localizeText(text: string) {
  if (!text) return text
  return Object.entries(fieldLabelMap)
    .sort((a, b) => b[0].length - a[0].length)
    .reduce((result, [key, label]) => result.replaceAll(key, label), text)
}

function buildInstruction() {
  const segments = [
    `命名风格：${namingOptions.find((item) => item.value === namingStyle.value)?.label || '标准规范'}`,
    `优先目标：${priorityOptions.find((item) => item.value === priority.value)?.label || '优先可用'}`,
  ]
  if (instruction.value.trim()) {
    segments.push(`补充要求：${instruction.value.trim()}`)
  }
  return segments.join('；')
}

async function runAssist(actionType: 'generate' | 'optimize') {
  loading.value = true
  try {
    const res = await assistTeachingConfig({
      moduleKey: props.moduleKey,
      actionType,
      userInstruction: buildInstruction(),
      currentForm: props.formData || {},
      selectedRows: props.selectedRows || [],
      availableOptions: props.availableOptions || {},
    })
    const data = res.data || {}
    result.assistantTitle = data.assistantTitle || ''
    result.highlights = (data.highlights || []).map((item: string) => localizeText(item))
    result.warnings = (data.warnings || []).map((item: string) => localizeText(item))
    result.tips = (data.tips || []).map((item: string) => localizeText(item))
    result.formDraftKeys = Object.keys(data.formDraft || {})
    lastActionLabel.value = actionType === 'generate' ? '一键配置' : '一键优化'
    showResult.value = true
    if (data.formDraft && Object.keys(data.formDraft).length) {
      emit('apply', data.formDraft)
      ElMessage.success(actionType === 'generate' ? 'AI 已生成可用配置' : 'AI 已优化当前配置')
    } else {
      ElMessage.info('AI 已返回建议，但没有可直接回填的字段')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.teaching-ai-compact {
  margin-bottom: 16px;
  padding: 14px;
  border: 1px solid #dbe3f0;
  border-radius: 18px;
  background:
    radial-gradient(circle at top left, rgba(55, 121, 178, 0.12), transparent 24%),
    linear-gradient(180deg, #fcfdff 0%, #f5f9ff 100%);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.06);
}

.teaching-ai-compact__bar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
}

.teaching-ai-compact__title {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  min-width: 0;
}

.teaching-ai-compact__icon {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e6eeff 0%, #d9ebff 100%);
  color: #2a5dc4;
  font-size: 18px;
  flex-shrink: 0;
}

.teaching-ai-compact__title strong {
  display: block;
  color: #172033;
  font-size: 15px;
}

.teaching-ai-compact__title p {
  margin: 4px 0 0;
  color: #5c6b83;
  font-size: 12px;
  line-height: 1.7;
}

.teaching-ai-compact__actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.teaching-ai-compact__actions :deep(.el-button i) {
  margin-right: 6px;
}

.teaching-ai-compact__editor {
  margin-top: 12px;
}

.teaching-ai-compact :deep(.el-textarea__inner) {
  min-height: 78px;
  border-radius: 14px;
  background: #fff;
}

.teaching-ai-compact__advanced {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #d7dfed;
}

.teaching-ai-compact__field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.teaching-ai-compact__field--wide {
  grid-column: 1 / -1;
}

.teaching-ai-compact__field label {
  color: #4e6079;
  font-size: 12px;
  font-weight: 700;
}

.teaching-ai-compact__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.teaching-ai-compact__chip {
  border: 1px solid #d7dfed;
  background: #fff;
  color: #36527a;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.teaching-ai-compact__chip:hover {
  border-color: #5c8ef0;
  color: #2453b5;
  background: #f4f8ff;
}

.teaching-ai-compact__result {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #d7dfed;
}

.teaching-ai-compact__result-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.teaching-ai-compact__result-title {
  color: #1f4b91;
  font-size: 14px;
  font-weight: 800;
}

.teaching-ai-compact__result-head p {
  margin: 4px 0 0;
  color: #607188;
  font-size: 12px;
  line-height: 1.7;
}

.teaching-ai-compact__result-tools {
  display: flex;
  align-items: center;
  gap: 8px;
}

.teaching-ai-compact__draft-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.teaching-ai-compact__result-body {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.teaching-ai-compact__group {
  padding: 12px;
  border-radius: 14px;
  background: rgba(255,255,255,0.82);
  border: 1px solid #e2e9f4;
}

.teaching-ai-compact__group span {
  display: block;
  margin-bottom: 6px;
  color: #172033;
  font-size: 12px;
  font-weight: 700;
}

.teaching-ai-compact__group ul {
  margin: 0;
  padding-left: 18px;
  color: #475467;
  font-size: 12px;
  line-height: 1.8;
}

.teaching-ai-compact__group.is-warning span {
  color: #b54708;
}

.teaching-ai-compact__group.is-tip span {
  color: #1570ef;
}

@media (max-width: 900px) {
  .teaching-ai-compact__bar {
    flex-direction: column;
  }

  .teaching-ai-compact__actions {
    width: 100%;
    justify-content: flex-start;
  }

  .teaching-ai-compact__advanced,
  .teaching-ai-compact__result-body {
    grid-template-columns: 1fr;
  }
}
</style>
