<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="显示名称"><el-input v-model="queryParams.modelName" style="width:220px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="提供商"><el-input v-model="queryParams.provider" style="width:180px" /></el-form-item>
      <el-form-item label="模型类型">
        <el-select v-model="queryParams.modelType" style="width:160px" clearable>
          <el-option label="Chat" value="chat" />
          <el-option label="Embedding" value="embedding" />
          <el-option label="Rerank" value="rerank" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增模型</el-button></el-col>
    </el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="ID" prop="modelId" width="80" />
      <el-table-column label="显示名称" prop="modelName" min-width="140" />
      <el-table-column label="提供商" prop="provider" width="110" />
      <el-table-column label="默认编码" prop="modelCode" min-width="160" show-overflow-tooltip />
      <el-table-column label="思考编码" prop="reasoningModelCode" min-width="160" show-overflow-tooltip />
      <el-table-column label="能力" min-width="160">
        <template #default="scope">
          <el-tag v-if="scope.row.supportStream === '1'" size="small">Stream</el-tag>
          <el-tag v-if="scope.row.supportReasoning === '1'" size="small" type="warning">思考</el-tag>
          <el-tag v-if="scope.row.supportVision === '1'" size="small" type="success">视觉</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="默认" width="80">
        <template #default="scope"><el-tag :type="scope.row.isDefault === '1' ? 'success' : 'info'">{{ scope.row.isDefault === '1' ? '是' : '否' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="优先级" prop="priority" width="90" />
      <el-table-column label="适用业务" prop="bizTypes" min-width="160" show-overflow-tooltip />
      <el-table-column label="状态" width="100"><template #default="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="primary" icon="Connection" @click="handleTest(scope.row)">测试</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="form.modelId ? '编辑模型' : '新增模型'" width="760px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="显示名称"><el-input v-model="form.modelName" placeholder="如 DeepSeek / 千问 Plus / 智谱 GLM" /></el-form-item>
        <el-form-item label="提供商">
          <el-select v-model="form.provider" @change="handleProviderChange">
            <el-option label="DeepSeek（OpenAI兼容）" value="deepseek" />
            <el-option label="OpenAI" value="openai" />
            <el-option label="豆包" value="doubao" />
            <el-option label="千问" value="qwen" />
            <el-option label="智谱" value="zhipu" />
            <el-option label="自定义兼容协议" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="接口地址"><el-input v-model="form.baseUrl" placeholder="如 https://api.deepseek.com" /></el-form-item>
        <el-form-item label="API Key"><el-input v-model="form.apiKey" show-password /></el-form-item>
        <el-form-item label="模型类型">
          <el-select v-model="form.modelType">
            <el-option label="Chat" value="chat" />
            <el-option label="Embedding" value="embedding" />
            <el-option label="Rerank" value="rerank" />
          </el-select>
        </el-form-item>
        <el-form-item label="默认模型编码"><el-input v-model="form.modelCode" placeholder="如 deepseek-chat / qwen-plus" /></el-form-item>
        <el-form-item label="思考模型编码"><el-input v-model="form.reasoningModelCode" placeholder="如 deepseek-reasoner，可为空" /></el-form-item>
        <el-form-item label="视觉模型编码"><el-input v-model="form.visionModelCode" placeholder="支持图片时填写，可为空" /></el-form-item>
        <el-form-item label="能力开关">
          <el-checkbox v-model="supportStreamSwitch">支持 Stream</el-checkbox>
          <el-checkbox v-model="supportReasoningSwitch">支持深度思考</el-checkbox>
          <el-checkbox v-model="supportVisionSwitch">支持图片理解</el-checkbox>
        </el-form-item>
        <el-form-item label="是否默认"><el-switch v-model="defaultSwitch" inline-prompt active-text="是" inactive-text="否" /></el-form-item>
        <el-form-item label="优先级"><el-input-number v-model="form.priority" :min="0" :max="999" controls-position="right" /><span class="ml12 tip-text">越大越优先</span></el-form-item>
        <el-form-item label="适用业务"><el-input v-model="form.bizTypes" placeholder="如 qa,report,recommend,common" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>

    <el-dialog v-model="testOpen" title="模型测试结果" width="720px">
      <el-form label-width="90px">
        <el-form-item label="提示词"><el-input v-model="testPrompt" type="textarea" rows="3" /></el-form-item>
        <el-form-item><el-button type="primary" :loading="testLoading" @click="submitTest">发送测试</el-button></el-form-item>
        <el-form-item label="返回结果"><el-input :model-value="testResult" type="textarea" rows="10" readonly /></el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addAiModel, delAiModel, listAiModel, testAiModel, updateAiModel } from '@/api/campus/ai'

const loading = ref(false)
const total = ref(0)
const open = ref(false)
const testOpen = ref(false)
const testLoading = ref(false)
const dataList = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, modelName: '', provider: '', modelType: '' })
const form = reactive<any>({ modelId: undefined, modelName: '', provider: '', baseUrl: '', apiKey: '', modelType: 'chat', status: '0', isDefault: '0', priority: 0, bizTypes: '', modelCode: '', reasoningModelCode: '', visionModelCode: '', supportStream: '1', supportReasoning: '0', supportVision: '0' })
const currentTestModelId = ref<number | undefined>()
const testPrompt = ref('请简单介绍一下你当前模型连接是否正常。')
const testResult = ref('')
const defaultSwitch = computed({ get: () => form.isDefault === '1', set: (value: boolean) => { form.isDefault = value ? '1' : '0' } })
const supportStreamSwitch = computed({ get: () => form.supportStream === '1', set: (value: boolean) => { form.supportStream = value ? '1' : '0' } })
const supportReasoningSwitch = computed({ get: () => form.supportReasoning === '1', set: (value: boolean) => { form.supportReasoning = value ? '1' : '0' } })
const supportVisionSwitch = computed({ get: () => form.supportVision === '1', set: (value: boolean) => { form.supportVision = value ? '1' : '0' } })

async function getList() {
  loading.value = true
  const res = await listAiModel(queryParams)
  dataList.value = res.rows || []
  total.value = res.total || 0
  loading.value = false
}

function resetForm() {
  Object.assign(form, { modelId: undefined, modelName: '', provider: '', baseUrl: '', apiKey: '', modelType: 'chat', status: '0', isDefault: '0', priority: 0, bizTypes: '', modelCode: '', reasoningModelCode: '', visionModelCode: '', supportStream: '1', supportReasoning: '0', supportVision: '0' })
}

function handleProviderChange(value: string) {
  if (value === 'deepseek') {
    form.baseUrl = form.baseUrl || 'https://api.deepseek.com'
    form.modelName = form.modelName || 'DeepSeek'
    form.modelCode = form.modelCode || 'deepseek-chat'
    form.reasoningModelCode = form.reasoningModelCode || 'deepseek-reasoner'
    form.supportStream = '1'
    form.supportReasoning = '1'
  }
  if (value === 'openai') {
    form.baseUrl = form.baseUrl || 'https://api.openai.com/v1'
    form.modelName = form.modelName || 'OpenAI'
    form.modelCode = form.modelCode || 'gpt-4o-mini'
    form.supportStream = '1'
    form.supportReasoning = '1'
    form.supportVision = '1'
  }
  if (value === 'doubao') {
    form.baseUrl = form.baseUrl || 'https://ark.cn-beijing.volces.com/api/v3'
    form.modelName = form.modelName || '豆包'
    form.modelCode = form.modelCode || 'doubao-seed-1-6-250615'
    form.supportStream = '1'
  }
  if (value === 'qwen') {
    form.baseUrl = form.baseUrl || 'https://dashscope.aliyuncs.com/compatible-mode/v1'
    form.modelName = form.modelName || '千问'
    form.modelCode = form.modelCode || 'qwen-plus'
    form.supportStream = '1'
    form.supportVision = '1'
  }
  if (value === 'zhipu') {
    form.baseUrl = form.baseUrl || 'https://open.bigmodel.cn/api/paas/v4'
    form.modelName = form.modelName || '智谱'
    form.modelCode = form.modelCode || 'glm-4-plus'
    form.supportStream = '1'
    form.supportVision = '1'
  }
}

function handleAdd() { resetForm(); open.value = true }
function handleEdit(row: any) { Object.assign(form, row); open.value = true }
async function handleDelete(row: any) { await ElMessageBox.confirm('确认删除该模型吗？', '提示', { type: 'warning' }); await delAiModel(row.modelId); ElMessage.success('删除成功'); getList() }
function handleTest(row: any) { currentTestModelId.value = row.modelId; testResult.value = ''; testOpen.value = true }
async function submitTest() { if (!currentTestModelId.value) return; testLoading.value = true; try { const res = await testAiModel({ modelId: currentTestModelId.value, prompt: testPrompt.value }); testResult.value = res.data?.content || '无返回内容' } finally { testLoading.value = false } }
async function submitForm() { if (form.modelId) { await updateAiModel(form); ElMessage.success('修改成功') } else { await addAiModel(form); ElMessage.success('新增成功') } open.value = false; getList() }

getList()
</script>
<style scoped>.mb16{margin-bottom:16px}.ml12{margin-left:12px}.tip-text{font-size:12px;color:#909399}</style>
