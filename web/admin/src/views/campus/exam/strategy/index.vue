<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="模板名称"><el-input v-model="queryParams.templateName" style="width: 220px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" clearable filterable style="width: 220px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
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
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增模板</el-button></el-col>
    </el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="模板ID" prop="templateId" width="90" />
      <el-table-column label="模板名称" prop="templateName" min-width="180" />
      <el-table-column label="课程ID" prop="courseId" width="100" />
      <el-table-column label="模板类型" prop="templateType" width="120" />
      <el-table-column label="发布状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.publishStatus === 'published' ? 'success' : scope.row.publishStatus === 'archived' ? 'info' : 'warning'">
            {{ publishStatusLabel(scope.row.publishStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="form.templateId ? '编辑组卷策略模板' : '新增组卷策略模板'" width="980px">
      <el-form :model="form" label-width="96px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="模板名称"><el-input v-model="form.templateName" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="课程">
              <el-select v-model="form.courseId" clearable filterable>
                <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8"><el-form-item label="模板类型"><el-input v-model="form.templateType" /></el-form-item></el-col>
          <el-col :span="8">
            <el-form-item label="发布状态">
              <el-select v-model="form.publishStatus">
                <el-option label="草稿" value="draft" />
                <el-option label="已发布" value="published" />
                <el-option label="已归档" value="archived" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="系统状态">
              <el-select v-model="form.status">
                <el-option label="正常" value="0" />
                <el-option label="停用" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="rule-panel">
          <div class="rule-panel__header">
            <div>
              <div class="panel-title">模板规则</div>
              <div class="panel-subtitle">可直接复用到智能组卷页面。</div>
            </div>
            <el-button type="primary" plain icon="Plus" @click="addRule">新增规则</el-button>
          </div>
          <div v-for="(rule, index) in form.rules" :key="`rule-${index}`" class="rule-card">
            <div class="rule-card__head">
              <div class="rule-card__title">规则 {{ Number(index) + 1 }}</div>
              <el-button link type="danger" icon="Delete" @click="removeRule(Number(index))">删除</el-button>
            </div>
            <el-row :gutter="16">
              <el-col :span="6">
                <el-form-item label="题型" label-width="50px">
                  <el-select v-model="rule.questionType">
                    <el-option v-for="item in questionTypeOptions" :key="item.code" :label="item.label" :value="item.code" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="4"><el-form-item label="数量" label-width="50px"><el-input-number v-model="rule.count" :min="1" :max="100" /></el-form-item></el-col>
              <el-col :span="4"><el-form-item label="难度" label-width="50px"><el-input-number v-model="rule.difficultyLevel" :min="1" :max="5" /></el-form-item></el-col>
              <el-col :span="6">
                <el-form-item label="知识点" label-width="60px">
                  <el-select v-model="rule.knowledgePointId" clearable filterable>
                    <el-option v-for="item in currentKnowledgePointOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="4"><el-form-item label="分值" label-width="50px"><el-input-number v-model="rule.score" :min="1" :max="100" /></el-form-item></el-col>
            </el-row>
          </div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="模板详情" width="980px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="模板名称">{{ detail.templateName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="课程ID">{{ detail.courseId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="模板类型">{{ detail.templateType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发布状态">{{ publishStatusLabel(detail.publishStatus) }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detail.rules || []" border class="mt16">
        <el-table-column label="题型">
          <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
        </el-table-column>
        <el-table-column label="数量" prop="count" width="100" />
        <el-table-column label="难度" prop="difficultyLevel" width="100" />
        <el-table-column label="知识点ID" prop="knowledgePointId" width="120" />
        <el-table-column label="分值" prop="score" width="100" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addStrategyTemplate, delStrategyTemplate, getStrategyTemplate, listQuestionTypeMeta, listStrategyTemplate, updateStrategyTemplate } from '@/api/campus/exam'
import { fetchCourseOptions } from '@/api/campus/options'
import { listKnowledgePoint } from '@/api/campus/teaching'

const loading = ref(false)
const total = ref(0)
const open = ref(false)
const detailOpen = ref(false)
const dataList = ref<any[]>([])
const detail = ref<any>({})
const courseOptions = ref<any[]>([])
const questionTypeOptions = ref<any[]>([])
const knowledgePointOptions = ref<any[]>([])

const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, templateName: '', courseId: undefined, publishStatus: '' })
const form = reactive<any>({})

const currentKnowledgePointOptions = computed(() =>
  knowledgePointOptions.value.filter((item: any) => !form.courseId || item.courseId === form.courseId),
)

function questionTypeLabel(type: string) {
  return questionTypeOptions.value.find((item: any) => item.code === type)?.label || type || '-'
}

function publishStatusLabel(status: string) {
  return ({ draft: '草稿', published: '已发布', archived: '已归档' } as any)[status] || status || '草稿'
}

function resetFormData() {
  Object.assign(form, {
    templateId: undefined,
    templateName: '',
    courseId: undefined,
    templateType: 'paper_rule',
    publishStatus: 'draft',
    status: '0',
    rules: [
      { questionType: 'single', count: 10, difficultyLevel: 3, knowledgePointId: undefined, score: 5 },
    ],
  })
}

async function getList() {
  loading.value = true
  try {
    const res = await listStrategyTemplate(queryParams)
    dataList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.templateName = ''
  queryParams.courseId = undefined
  queryParams.publishStatus = ''
  getList()
}

function handleAdd() {
  resetFormData()
  open.value = true
}

async function handleEdit(row: any) {
  const res = await getStrategyTemplate(row.templateId)
  const data = res.data || {}
  Object.assign(form, data, {
    rules: safeParseRules(data.ruleJson),
  })
  open.value = true
}

async function handleView(row: any) {
  const res = await getStrategyTemplate(row.templateId)
  const data = res.data || {}
  detail.value = { ...data, rules: safeParseRules(data.ruleJson) }
  detailOpen.value = true
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确认删除模板「${row.templateName}」吗？`, '提示', { type: 'warning' })
  await delStrategyTemplate(row.templateId)
  ElMessage.success('删除成功')
  getList()
}

function addRule() {
  form.rules.push({ questionType: 'single', count: 5, difficultyLevel: 3, knowledgePointId: undefined, score: 5 })
}

function removeRule(index: number) {
  form.rules.splice(index, 1)
}

function safeParseRules(ruleJson: string) {
  try {
    const parsed = JSON.parse(ruleJson || '[]')
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

async function submitForm() {
  const payload = {
    templateId: form.templateId,
    templateName: form.templateName,
    courseId: form.courseId,
    templateType: form.templateType,
    publishStatus: form.publishStatus,
    status: form.status,
    ruleJson: JSON.stringify(form.rules || []),
  }
  if (form.templateId) {
    await updateStrategyTemplate(payload)
    ElMessage.success('修改成功')
  } else {
    await addStrategyTemplate(payload)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

async function loadOptions() {
  const [courseRes, typeRes, kpRes] = await Promise.all([
    fetchCourseOptions(),
    listQuestionTypeMeta(),
    listKnowledgePoint({ pageNum: 1, pageSize: 500, status: '0' }),
  ])
  courseOptions.value = courseRes || []
  questionTypeOptions.value = typeRes.data || []
  knowledgePointOptions.value = (kpRes.rows || []).map((item: any) => ({
    label: `${item.knowledgeName}（${item.knowledgePointId}）`,
    value: item.knowledgePointId,
    courseId: item.courseId,
  }))
}

onMounted(async () => {
  resetFormData()
  await loadOptions()
  await getList()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.mt16 { margin-top: 16px; }
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
.rule-panel {
  margin-top: 8px;
  padding: 16px;
  border-radius: 16px;
  background: linear-gradient(180deg, #f9fbff 0%, #f4f8fc 100%);
  border: 1px solid var(--el-border-color-lighter);
}
.rule-panel__header,
.rule-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}
.rule-card + .rule-card {
  margin-top: 14px;
}
</style>
