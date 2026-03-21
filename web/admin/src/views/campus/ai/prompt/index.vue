<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="模板名称"><el-input v-model="queryParams.templateName" style="width:220px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="业务类型">
        <el-select v-model="queryParams.bizType" style="width:180px" clearable>
          <el-option v-for="item in bizTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16"><el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增模板</el-button></el-col></el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="模板ID" prop="templateId" width="90" />
      <el-table-column label="模板编码" prop="templateCode" width="140" />
      <el-table-column label="模板名称" prop="templateName" min-width="180" />
      <el-table-column label="业务类型" prop="bizType" width="120">
        <template #default="scope">{{ bizTypeLabel(scope.row.bizType) }}</template>
      </el-table-column>
      <el-table-column label="版本" prop="version" width="80" />
      <el-table-column label="状态" width="100"><template #default="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="220"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button><el-button link type="primary" icon="Connection" @click="handleTest(scope.row)">测试</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button></template></el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="form.templateId ? '编辑模板' : '新增模板'" width="760px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="模板编码"><el-input v-model="form.templateCode" /></el-form-item>
        <el-form-item label="模板名称"><el-input v-model="form.templateName" /></el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="form.bizType" placeholder="请选择业务类型">
            <el-option v-for="item in bizTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="版本"><el-input-number v-model="form.version" :min="1" :max="20" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
        <el-form-item label="模板内容"><el-input v-model="form.promptContent" type="textarea" rows="8" placeholder="这里填写系统提示词模板，测试时会作为 system prompt 使用" /></el-form-item>
        <el-form-item label="变量说明">
          <div class="prompt-helper">
            <div class="prompt-helper__title">推荐占位变量</div>
            <div class="prompt-helper__tokens">
              <span v-for="item in promptTokens" :key="item" class="prompt-helper__token">{{ item }}</span>
            </div>
            <div class="prompt-helper__desc">
              出题场景建议至少使用课程名称、知识点名称、题型标签和难度提示，避免模型输出与课程无关的题目。
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>

    <el-dialog v-model="testOpen" title="Prompt 测试" width="760px">
      <el-form label-width="90px">
        <el-form-item label="测试模型">
          <el-select v-model="testForm.modelId" placeholder="请选择模型" filterable style="width: 100%">
            <el-option v-for="item in modelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="测试问题"><el-input v-model="testForm.userPrompt" type="textarea" rows="3" placeholder="输入一段测试问题" /></el-form-item>
        <el-form-item><el-button type="primary" :loading="testLoading" @click="submitTest">开始测试</el-button></el-form-item>
        <el-form-item label="返回结果"><el-input :model-value="testResult" type="textarea" rows="10" readonly /></el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchAiModelOptions } from '@/api/campus/options'
import { addAiPrompt, delAiPrompt, listAiPrompt, testAiPrompt, updateAiPrompt } from '@/api/campus/ai'

const bizTypeOptions = [
  { label: 'AI出题', value: 'exam_question_generate' },
  { label: 'AI异步出题', value: 'exam_question_generate_async' },
  { label: '智能问答', value: 'qa' },
  { label: '学习画像分析', value: 'learning_profile_analysis' },
  { label: '资源分析', value: 'resource_analysis' },
  { label: '教学配置助手', value: 'teaching_admin' },
  { label: '模型测试', value: 'model_test' },
  { label: '通用', value: 'common' },
]

const promptTokens = [
  '{{courseName}}',
  '{{courseCode}}',
  '{{subjectType}}',
  '{{courseIntro}}',
  '{{chapterOutline}}',
  '{{knowledgeName}}',
  '{{knowledgeDescription}}',
  '{{questionTypeLabel}}',
  '{{difficultyHint}}',
]

const loading = ref(false)
const total = ref(0)
const open = ref(false)
const testOpen = ref(false)
const testLoading = ref(false)
const dataList = ref<any[]>([])
const modelOptions = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, templateName: '', bizType: '' })
const form = reactive<any>({ templateId: undefined, templateCode: '', templateName: '', bizType: '', promptContent: '', version: 1, status: '0' })
const testForm = reactive<any>({ templateId: undefined, modelId: undefined, userPrompt: '请介绍一下这个提示词是否工作正常。' })
const testResult = ref('')

async function getList() {
  loading.value = true
  const res = await listAiPrompt(queryParams)
  dataList.value = res.rows || []
  total.value = res.total || 0
  loading.value = false
}

async function loadModelOptions() {
  modelOptions.value = await fetchAiModelOptions('chat')
}

function resetForm() {
  Object.assign(form, {
    templateId: undefined,
    templateCode: '',
    templateName: '',
    bizType: 'exam_question_generate',
    promptContent: '',
    version: 1,
    status: '0'
  })
}

function handleAdd() {
  resetForm()
  if (!form.templateCode && form.bizType === 'exam_question_generate') {
    form.templateCode = 'exam_question_generate_default'
    form.templateName = 'AI出题默认模板'
  }
  open.value = true
}

function handleEdit(row: any) {
  Object.assign(form, row)
  open.value = true
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确认删除该模板吗？', '提示', { type: 'warning' })
  await delAiPrompt(row.templateId)
  ElMessage.success('删除成功')
  getList()
}

function handleTest(row: any) {
  testForm.templateId = row.templateId
  if (!testForm.modelId && modelOptions.value.length) {
    testForm.modelId = modelOptions.value[0].value
  }
  testForm.userPrompt = row.bizType === 'exam_question_generate'
    ? '{"courseId":101,"courseName":"Python程序设计","knowledgeName":"函数与参数传递","questionTypeLabel":"单选题","difficultyHint":"中等难度，兼顾概念理解与基础应用"}'
    : '请介绍一下这个提示词是否工作正常。'
  testResult.value = ''
  testOpen.value = true
}

async function submitTest() {
  testLoading.value = true
  try {
    const res = await testAiPrompt(testForm)
    testResult.value = res.data?.content || '无返回内容'
  } finally {
    testLoading.value = false
  }
}

async function submitForm() {
  if (form.templateId) {
    await updateAiPrompt(form)
    ElMessage.success('修改成功')
  } else {
    await addAiPrompt(form)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

function bizTypeLabel(value: string) {
  return bizTypeOptions.find((item) => item.value === value)?.label || value || '-'
}

onMounted(async () => {
  await loadModelOptions()
  await getList()
})
</script>
<style scoped>.mb16{margin-bottom:16px}</style>
