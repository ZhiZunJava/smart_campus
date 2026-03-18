<template>
  <div class="app-container qa-message-page">
    <el-form :inline="true" :model="queryParams" class="qa-toolbar">
      <el-form-item label="会话">
        <el-select v-model="queryParams.sessionId" filterable clearable placeholder="选择会话" style="width: 280px">
          <el-option v-for="item in sessionOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="queryParams.roleType" clearable placeholder="请选择角色" style="width: 180px">
          <el-option label="用户" value="user" />
          <el-option label="助手" value="assistant" />
          <el-option label="系统" value="system" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="qa-toolbar qa-toolbar--actions">
      <el-button type="primary" plain icon="Plus" @click="handleAdd">新增消息</el-button>
    </div>

    <div class="message-stats">
      <div class="message-stats__card">
        <span>当前列表</span>
        <strong>{{ total }}</strong>
        <small>匹配到的消息数</small>
      </div>
      <div class="message-stats__card">
        <span>用户消息</span>
        <strong>{{ roleStats.user }}</strong>
        <small>问题与追问</small>
      </div>
      <div class="message-stats__card">
        <span>助手消息</span>
        <strong>{{ roleStats.assistant }}</strong>
        <small>AI 生成回复</small>
      </div>
      <div class="message-stats__card">
        <span>总 Token</span>
        <strong>{{ totalTokens }}</strong>
        <small>本页累计</small>
      </div>
    </div>

    <el-table v-loading="loading" :data="dataList" class="qa-table" row-key="messageId">
      <el-table-column label="消息" min-width="360">
        <template #default="{ row }">
          <div class="message-card">
            <div class="message-card__head">
              <div class="message-card__title">
                <span class="message-role" :class="`is-${row.roleType || 'unknown'}`">{{ formatRole(row.roleType) }}</span>
                <strong>#{{ row.messageId }}</strong>
                <span>会话 {{ row.sessionId }}</span>
              </div>
              <span class="message-card__time">{{ formatTime(row.createTime) }}</span>
            </div>
            <div class="message-card__content">{{ row.content || '无消息内容' }}</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="来源 / 模型" min-width="260">
        <template #default="{ row }">
          <div class="source-card">
            <div class="source-card__model">{{ row.modelName || '未记录模型' }}</div>
            <div class="source-card__preview">{{ getReferencePreview(row.referenceSource) }}</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="指标" width="180" align="center">
        <template #default="{ row }">
          <div class="metric-stack">
            <span>Token {{ row.tokenCount ?? 0 }}</span>
            <span>耗时 {{ formatLatency(row.latencyMs) }}</span>
            <span>敏感 {{ row.sensitiveFlag === '1' ? '是' : '否' }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="View" @click="handlePreview(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog v-model="open" title="新增消息" width="760px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="会话">
          <el-select v-model="form.sessionId" filterable clearable style="width: 100%">
            <el-option v-for="item in sessionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleType" style="width: 100%">
            <el-option label="用户" value="user" />
            <el-option label="助手" value="assistant" />
            <el-option label="系统" value="system" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" rows="6" />
        </el-form-item>
        <el-form-item label="引用来源">
          <el-input v-model="form.referenceSource" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="previewOpen" title="消息详情" width="860px" top="10vh" destroy-on-close class="qa-preview-dialog">
      <div class="preview-shell">
        <div class="preview-meta">
          <span class="preview-meta__tag" :class="`is-${previewData.roleType || 'unknown'}`">{{ formatRole(previewData.roleType) }}</span>
          <span>消息ID {{ previewData.messageId || '-' }}</span>
          <span>会话ID {{ previewData.sessionId || '-' }}</span>
          <span>{{ formatTime(previewData.createTime) }}</span>
          <span>Token {{ previewData.tokenCount ?? 0 }}</span>
          <span>耗时 {{ formatLatency(previewData.latencyMs) }}</span>
        </div>

        <section class="preview-panel">
          <div class="preview-panel__header">
            <h3>消息正文</h3>
          </div>
          <div class="preview-content">{{ previewData.content || '无消息内容' }}</div>
        </section>

        <section class="preview-panel">
          <div class="preview-panel__header">
            <h3>来源信息</h3>
          </div>
          <pre class="preview-code">{{ formatReferenceSource(previewData.referenceSource) }}</pre>
        </section>

        <section v-if="previewData.reasoningContent" class="preview-panel">
          <div class="preview-panel__header">
            <h3>思考过程</h3>
          </div>
          <div class="preview-content">{{ previewData.reasoningContent }}</div>
        </section>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addQaMessage, listQaMessage, listQaSession } from '@/api/campus/qa'
import { parseTime } from '@/utils/ruoyi'

const loading = ref(false)
const total = ref(0)
const open = ref(false)
const previewOpen = ref(false)
const dataList = ref<any[]>([])
const previewData = ref<any>({})
const sessionOptions = ref<any[]>([])

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  sessionId: undefined,
  roleType: '',
})

const form = reactive<any>({
  sessionId: undefined,
  roleType: 'assistant',
  content: '',
  referenceSource: '',
  tokenCount: 0,
  latencyMs: 0,
  sensitiveFlag: '0',
})

const roleStats = computed(() => ({
  user: dataList.value.filter((item: any) => item.roleType === 'user').length,
  assistant: dataList.value.filter((item: any) => item.roleType === 'assistant').length,
  system: dataList.value.filter((item: any) => item.roleType === 'system').length,
}))

const totalTokens = computed(() =>
  dataList.value.reduce((sum: number, item: any) => sum + Number(item.tokenCount || 0), 0),
)

function resetForm() {
  Object.assign(form, {
    sessionId: undefined,
    roleType: 'assistant',
    content: '',
    referenceSource: '',
    tokenCount: 0,
    latencyMs: 0,
    sensitiveFlag: '0',
  })
}

function formatRole(value?: string) {
  if (value === 'assistant') return 'AI 助手'
  if (value === 'system') return '系统'
  if (value === 'user') return '用户'
  return value || '未知角色'
}

function formatTime(value?: string) {
  return value ? parseTime(value) || '-' : '-'
}

function formatLatency(value?: number | string) {
  const latency = Number(value || 0)
  if (!latency) return '0 s'
  if (latency < 1000) return `${latency} ms`
  return `${(latency / 1000).toFixed(latency >= 10000 ? 1 : 2)} s`
}

function formatReferenceSource(value?: string) {
  if (!value) return '暂无来源信息'
  try {
    return JSON.stringify(JSON.parse(value), null, 2)
  } catch {
    return value
  }
}

function getReferencePreview(value?: string) {
  const formatted = formatReferenceSource(value)
  return formatted.length > 80 ? `${formatted.slice(0, 80)}...` : formatted
}

async function getList() {
  loading.value = true
  try {
    const res = await listQaMessage(queryParams)
    dataList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function loadSessionOptions() {
  const res = await listQaSession({ pageNum: 1, pageSize: 200 })
  sessionOptions.value = (res.rows || []).map((item: any) => ({
    label: `${item.sessionTitle || `会话 ${item.sessionId}`}（${item.sessionId}）`,
    value: item.sessionId,
  }))
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.sessionId = undefined
  queryParams.roleType = ''
  getList()
}

function handleAdd() {
  resetForm()
  open.value = true
}

async function submitForm() {
  await addQaMessage(form)
  ElMessage.success('新增成功')
  open.value = false
  getList()
}

function handlePreview(row: any) {
  previewData.value = { ...row }
  previewOpen.value = true
}

onMounted(async () => {
  await loadSessionOptions()
  await getList()
})
</script>

<style scoped>
.qa-toolbar {
  margin-bottom: 16px;
}

.qa-toolbar--actions {
  display: flex;
  justify-content: flex-start;
}

.message-stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.message-stats__card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 16px;
  border: 1px solid #dbe3f0;
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f7faff 100%);
}

.message-stats__card span,
.message-stats__card small {
  color: #667085;
  font-size: 12px;
}

.message-stats__card strong {
  color: #172033;
  font-size: 24px;
  line-height: 1.1;
}

.qa-table :deep(.el-table__cell) {
  vertical-align: top;
}

.message-card {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 4px 0;
}

.message-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.message-card__title {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 10px;
  color: #667085;
  font-size: 12px;
}

.message-role {
  display: inline-flex;
  align-items: center;
  height: 24px;
  padding: 0 10px;
  border-radius: 999px;
  font-weight: 700;
}

.message-role.is-user {
  background: #e9f2ff;
  color: #2559c7;
}

.message-role.is-assistant {
  background: #e8f7ee;
  color: #157347;
}

.message-role.is-system {
  background: #fff2d9;
  color: #b26a00;
}

.message-card__title strong {
  color: #172033;
  font-size: 13px;
}

.message-card__time {
  color: #98a2b3;
  font-size: 12px;
  flex-shrink: 0;
}

.message-card__content {
  display: -webkit-box;
  overflow: hidden;
  color: #344054;
  font-size: 13px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.source-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 4px 0;
}

.source-card__model {
  color: #172033;
  font-size: 13px;
  font-weight: 700;
}

.source-card__preview {
  color: #667085;
  font-size: 12px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}

.metric-stack {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: center;
  color: #475467;
  font-size: 12px;
  line-height: 1.5;
}

.qa-preview-dialog :deep(.el-dialog__body) {
  padding-top: 10px;
}

.preview-shell {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.preview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.preview-meta span {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #f5f7fb;
  color: #526076;
  font-size: 12px;
}

.preview-meta__tag.is-user {
  background: #e9f2ff;
  color: #2559c7;
}

.preview-meta__tag.is-assistant {
  background: #e8f7ee;
  color: #157347;
}

.preview-meta__tag.is-system {
  background: #fff2d9;
  color: #b26a00;
}

.preview-panel {
  border: 1px solid #dbe3f0;
  border-radius: 12px;
  background: #fff;
  overflow: hidden;
}

.preview-panel__header {
  padding: 12px 16px;
  border-bottom: 1px solid #edf2f7;
  background: #f8fbff;
}

.preview-panel__header h3 {
  margin: 0;
  color: #172033;
  font-size: 15px;
}

.preview-content,
.preview-code {
  margin: 0;
  padding: 14px 16px;
  color: #344054;
  font-size: 13px;
  line-height: 1.85;
  white-space: pre-wrap;
  word-break: break-word;
}

.preview-code {
  overflow-x: auto;
  font-family: Consolas, Monaco, 'Courier New', monospace;
  background: #fbfcff;
}

@media (max-width: 992px) {
  .message-stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .message-stats {
    grid-template-columns: 1fr;
  }

  .message-card__head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
