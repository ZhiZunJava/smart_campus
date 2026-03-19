<template>
  <div class="app-container qa-admin-page">
    <el-form :inline="true" :model="queryParams" class="qa-toolbar">
      <el-form-item label="用户">
        <el-select v-model="queryParams.userId" filterable clearable placeholder="选择用户" style="width: 240px">
          <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" filterable clearable placeholder="选择课程" style="width: 240px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="qa-toolbar qa-toolbar--actions">
      <el-button type="primary" plain icon="Plus" @click="handleAdd">新增会话</el-button>
    </div>

    <el-table v-loading="loading" :data="dataList" class="qa-table" row-key="sessionId">
      <el-table-column label="会话信息" min-width="320">
        <template #default="{ row }">
          <div class="session-card">
            <div class="session-card__title">
              <span>{{ row.sessionTitle || `会话 ${row.sessionId}` }}</span>
              <el-tag size="small" :type="getSourceTagType(row.sourceType)">{{ formatSourceType(row.sourceType) }}</el-tag>
            </div>
            <div class="session-card__meta">
              <span>会话ID {{ row.sessionId }}</span>
              <span>用户ID {{ row.userId || '-' }}</span>
              <span v-if="row.courseId">课程ID {{ row.courseId }}</span>
            </div>
            <div v-if="row.lastMessagePreview || row.contextSummary" class="session-card__preview">
              {{ row.lastMessagePreview || row.contextSummary }}
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="会话状态" width="180" align="center">
        <template #default="{ row }">
          <div class="metric-stack">
            <el-tag size="small" :type="row.status === '0' ? 'success' : 'info'">{{ formatStatus(row.status) }}</el-tag>
            <span class="metric-stack__sub">消息 {{ row.messageCount ?? '-' }} 条</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="时间" min-width="180">
        <template #default="{ row }">
          <div class="metric-stack">
            <span>{{ formatTime(row.lastMessageTime || row.updateTime || row.createTime) }}</span>
            <span class="metric-stack__sub">创建于 {{ formatTime(row.createTime) }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="提示" min-width="180">
        <template #default="{ row }">
          <div class="qa-hint" :class="{ 'is-warning': row.sourceType === 'course' && !row.courseId }">
            {{ getSourceHint(row) }}
          </div>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="View" @click="handleView(row)">详情</el-button>
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

    <el-dialog v-model="open" :title="title" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="用户">
          <el-select v-model="form.userId" filterable clearable style="width: 100%">
            <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="form.courseId" filterable clearable style="width: 100%">
            <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="会话标题">
          <el-input v-model="form.sessionTitle" maxlength="60" show-word-limit />
        </el-form-item>
        <el-form-item label="场景类型">
          <el-select v-model="form.sourceType" style="width: 100%">
            <el-option label="通用问答" value="general" />
            <el-option label="课程问答" value="course" />
            <el-option label="错题答疑" value="wrong_question" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="进行中" value="0" />
            <el-option label="已结束" value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="会话详情" width="1120px" top="5vh" destroy-on-close>
      <div class="detail-shell">
        <div class="detail-overview">
          <div class="overview-card">
            <span class="overview-card__label">会话标题</span>
            <strong>{{ detail.sessionTitle || `会话 ${detail.sessionId || ''}` }}</strong>
          </div>
          <div class="overview-card">
            <span class="overview-card__label">会话归属</span>
            <strong>用户 {{ detail.userId || '-' }}</strong>
            <small>课程 {{ detail.courseId || '未关联' }}</small>
          </div>
          <div class="overview-card">
            <span class="overview-card__label">场景与状态</span>
            <strong>{{ formatSourceType(detail.sourceType) }}</strong>
            <small>{{ formatStatus(detail.status) }}</small>
          </div>
          <div class="overview-card">
            <span class="overview-card__label">消息统计</span>
            <strong>{{ detail.messages?.length || 0 }} 条</strong>
            <small>{{ detail.sourceType === 'course' && !detail.courseId ? '建议补充课程关联或改为通用问答' : '当前语义完整' }}</small>
          </div>
        </div>

        <el-alert
          v-if="detail.sourceType === 'course' && !detail.courseId"
          class="detail-alert"
          type="warning"
          :closable="false"
          title="当前会话标记为课程问答，但未绑定课程ID，后台审查时容易产生语义歧义。"
        />

        <div class="detail-main">
          <section class="detail-panel detail-panel--timeline">
            <div class="detail-panel__header">
              <div>
                <h3>对话时间线</h3>
                <p>按消息顺序查看完整上下文，避免表格截断信息。</p>
              </div>
            </div>

            <el-scrollbar max-height="580px">
              <div v-if="detail.messages?.length" class="timeline">
                <article
                  v-for="(item, index) in detail.messages"
                  :key="item.messageId"
                  class="timeline-item"
                  :class="`is-${item.roleType || 'unknown'}`"
                >
                  <div class="timeline-item__avatar">
                    <i :class="getRoleIcon(item.roleType)"></i>
                  </div>
                  <div class="timeline-item__body">
                    <div class="timeline-item__head">
                      <div class="timeline-item__title">
                        <strong>{{ formatRole(item.roleType) }}</strong>
                        <span>#{{ Number(index) + 1 }}</span>
                      </div>
                      <div class="timeline-item__meta">
                        <span>ID {{ item.messageId }}</span>
                        <span>{{ formatTime(item.createTime) }}</span>
                      </div>
                    </div>

                    <div v-if="item.content" class="timeline-item__content">{{ item.content }}</div>
                    <div v-else class="timeline-item__content timeline-item__content--empty">无内容</div>

                    <div v-if="item.reasoningContent" class="timeline-block">
                      <div class="timeline-block__label">思考过程</div>
                      <div class="timeline-block__content">{{ item.reasoningContent }}</div>
                    </div>

                    <div v-if="item.referenceSource" class="timeline-block">
                      <div class="timeline-block__label">来源</div>
                      <pre class="timeline-block__content timeline-block__content--code">{{ formatReferenceSource(item.referenceSource) }}</pre>
                    </div>

                    <div class="timeline-item__stats">
                      <span>Token {{ item.tokenCount ?? 0 }}</span>
                      <span>耗时 {{ formatLatency(item.latencyMs) }}</span>
                      <span v-if="item.modelName">模型 {{ item.modelName }}</span>
                      <span>敏感标记 {{ item.sensitiveFlag === '1' ? '是' : '否' }}</span>
                    </div>
                  </div>
                </article>
              </div>
              <el-empty v-else :image-size="90" description="暂无消息记录" />
            </el-scrollbar>
          </section>

          <section class="detail-panel detail-panel--summary">
            <div class="detail-panel__header">
              <div>
                <h3>结构化信息</h3>
                <p>用于快速判断这个会话是否归档合理。</p>
              </div>
            </div>

            <div class="summary-list">
              <div class="summary-row">
                <span>场景类型</span>
                <strong>{{ formatSourceType(detail.sourceType) }}</strong>
              </div>
              <div class="summary-row">
                <span>状态</span>
                <strong>{{ formatStatus(detail.status) }}</strong>
              </div>
              <div class="summary-row">
                <span>用户ID</span>
                <strong>{{ detail.userId || '-' }}</strong>
              </div>
              <div class="summary-row">
                <span>课程ID</span>
                <strong>{{ detail.courseId || '未关联' }}</strong>
              </div>
              <div class="summary-row">
                <span>用户消息数</span>
                <strong>{{ getMessageCountByRole('user') }}</strong>
              </div>
              <div class="summary-row">
                <span>助手消息数</span>
                <strong>{{ getMessageCountByRole('assistant') }}</strong>
              </div>
              <div class="summary-row">
                <span>系统消息数</span>
                <strong>{{ getMessageCountByRole('system') }}</strong>
              </div>
              <div class="summary-row">
                <span>总 Token</span>
                <strong>{{ totalTokenCount }}</strong>
              </div>
            </div>

            <div class="summary-note">
              <div class="summary-note__title">审查建议</div>
              <p>{{ getSourceHint(detail) }}</p>
            </div>
          </section>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addQaSession, getQaSessionDetail, listQaSession } from '@/api/campus/qa'
import { fetchCourseOptions, fetchUserOptions } from '@/api/campus/options'
import { parseTime } from '@/utils/ruoyi'

const loading = ref(false)
const total = ref(0)
const open = ref(false)
const detailOpen = ref(false)
const title = ref('')
const dataList = ref<any[]>([])
const detail = ref<any>({})
const userOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  userId: undefined,
  courseId: undefined,
})

const form = reactive<any>({
  userId: undefined,
  courseId: undefined,
  sessionTitle: '',
  sourceType: 'course',
  status: '0',
})

const sourceTypeMap: Record<string, string> = {
  general: '通用问答',
  course: '课程问答',
  wrong_question: '错题答疑',
}

const statusMap: Record<string, string> = {
  '0': '进行中',
  '1': '已结束',
}

const totalTokenCount = computed(() =>
  (detail.value.messages || []).reduce((sum: number, item: any) => sum + Number(item.tokenCount || 0), 0),
)

function resetForm() {
  Object.assign(form, {
    userId: undefined,
    courseId: undefined,
    sessionTitle: '',
    sourceType: 'course',
    status: '0',
  })
}

function formatSourceType(value?: string) {
  return sourceTypeMap[value || ''] || value || '未标记'
}

function formatStatus(value?: string) {
  return statusMap[value || ''] || value || '未知'
}

function getSourceTagType(value?: string) {
  if (value === 'course') return 'success'
  if (value === 'wrong_question') return 'warning'
  return 'info'
}

function formatRole(value?: string) {
  if (value === 'assistant') return 'AI 助手'
  if (value === 'system') return '系统'
  if (value === 'user') return '用户'
  return value || '未知角色'
}

function getRoleIcon(value?: string) {
  if (value === 'assistant') return 'ri-robot-2-line'
  if (value === 'system') return 'ri-settings-3-line'
  return 'ri-user-3-line'
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
  if (!value) return '-'
  try {
    return JSON.stringify(JSON.parse(value), null, 2)
  } catch {
    return value
  }
}

function getSourceHint(row: any) {
  if (row?.sourceType === 'course' && !row?.courseId) {
    return '课程问答未绑定课程ID，建议改为通用问答或补齐课程关联。'
  }
  if (row?.sourceType === 'wrong_question') {
    return '适合带题目、解析或错因上下文，便于回溯错题链路。'
  }
  if (row?.sourceType === 'general') {
    return '适合作为泛化学习咨询，不强依赖课程主数据。'
  }
  return '当前会话结构完整，可直接用于后台审查。'
}

function getMessageCountByRole(role: string) {
  return (detail.value.messages || []).filter((item: any) => item.roleType === role).length
}

async function getList() {
  loading.value = true
  try {
    const res = await listQaSession(queryParams)
    dataList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function loadOptions() {
  userOptions.value = await fetchUserOptions()
  courseOptions.value = await fetchCourseOptions()
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.userId = undefined
  queryParams.courseId = undefined
  getList()
}

function handleAdd() {
  resetForm()
  title.value = '新增会话'
  open.value = true
}

async function submitForm() {
  await addQaSession(form)
  ElMessage.success('新增成功')
  open.value = false
  getList()
}

async function handleView(row: any) {
  const res = await getQaSessionDetail(row.sessionId)
  detail.value = res.data || {}
  detailOpen.value = true
}

onMounted(async () => {
  await loadOptions()
  await getList()
})
</script>

<style scoped>
.qa-admin-page {
  --qa-ink: #172033;
  --qa-muted: #667085;
  --qa-line: #dbe3f0;
  --qa-panel: linear-gradient(180deg, #ffffff 0%, #f6f9ff 100%);
  --qa-panel-soft: #f8fbff;
  --qa-user-bg: #eef5ff;
  --qa-assistant-bg: #ffffff;
  --qa-system-bg: #fff7e8;
}

.qa-toolbar {
  margin-bottom: 16px;
}

.qa-toolbar--actions {
  display: flex;
  justify-content: flex-start;
}

.qa-table :deep(.el-table__cell) {
  vertical-align: top;
}

.session-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 4px 0;
}

.session-card__title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  font-weight: 700;
  color: var(--qa-ink);
  line-height: 1.5;
}

.session-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 14px;
  color: var(--qa-muted);
  font-size: 12px;
}

.session-card__preview {
  display: -webkit-box;
  overflow: hidden;
  color: #3a4a63;
  font-size: 12px;
  line-height: 1.7;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.metric-stack {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  line-height: 1.5;
}

.metric-stack__sub {
  color: var(--qa-muted);
  font-size: 12px;
}

.qa-hint {
  padding: 10px 12px;
  border-radius: 10px;
  background: #f7f9fc;
  color: #42526b;
  font-size: 12px;
  line-height: 1.7;
}

.qa-hint.is-warning {
  background: #fff6ea;
  color: #9a5a00;
}

.detail-shell {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-overview {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.overview-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 16px;
  border: 1px solid var(--qa-line);
  border-radius: 14px;
  background: var(--qa-panel);
}

.overview-card__label {
  color: var(--qa-muted);
  font-size: 12px;
}

.overview-card strong {
  color: var(--qa-ink);
  font-size: 15px;
  line-height: 1.5;
}

.overview-card small {
  color: var(--qa-muted);
  font-size: 12px;
}

.detail-alert {
  margin-bottom: 2px;
}

.detail-main {
  display: grid;
  grid-template-columns: minmax(0, 2.1fr) minmax(280px, 0.9fr);
  gap: 16px;
}

.detail-panel {
  border: 1px solid var(--qa-line);
  border-radius: 16px;
  background: #fff;
  overflow: hidden;
}

.detail-panel__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 20px 14px;
  border-bottom: 1px solid #edf2f7;
  background: var(--qa-panel-soft);
}

.detail-panel__header h3 {
  margin: 0;
  color: var(--qa-ink);
  font-size: 16px;
}

.detail-panel__header p {
  margin: 6px 0 0;
  color: var(--qa-muted);
  font-size: 12px;
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 18px;
}

.timeline-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.timeline-item__avatar {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 18px;
}

.timeline-item.is-user .timeline-item__avatar {
  background: #dceaff;
  color: #2065d1;
}

.timeline-item.is-assistant .timeline-item__avatar {
  background: #e9f7ef;
  color: #14804a;
}

.timeline-item.is-system .timeline-item__avatar {
  background: #fff1d6;
  color: #c97a00;
}

.timeline-item__body {
  flex: 1;
  min-width: 0;
  padding: 14px 16px;
  border: 1px solid var(--qa-line);
  border-radius: 16px;
  background: var(--qa-assistant-bg);
}

.timeline-item.is-user .timeline-item__body {
  background: var(--qa-user-bg);
}

.timeline-item.is-system .timeline-item__body {
  background: var(--qa-system-bg);
}

.timeline-item__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 10px;
}

.timeline-item__title {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.timeline-item__title strong {
  color: var(--qa-ink);
  font-size: 14px;
}

.timeline-item__title span,
.timeline-item__meta {
  color: var(--qa-muted);
  font-size: 12px;
}

.timeline-item__meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px 12px;
}

.timeline-item__content {
  color: #23324d;
  font-size: 13px;
  line-height: 1.85;
  white-space: pre-wrap;
  word-break: break-word;
}

.timeline-item__content--empty {
  color: #98a2b3;
}

.timeline-block {
  margin-top: 12px;
  border-top: 1px dashed #d4dbe7;
  padding-top: 12px;
}

.timeline-block__label {
  margin-bottom: 8px;
  color: #35518a;
  font-size: 12px;
  font-weight: 700;
}

.timeline-block__content {
  margin: 0;
  padding: 12px;
  border-radius: 12px;
  background: rgba(23, 32, 51, 0.04);
  color: #31415e;
  font-size: 12px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}

.timeline-block__content--code {
  overflow-x: auto;
  font-family: Consolas, Monaco, 'Courier New', monospace;
}

.timeline-item__stats {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.timeline-item__stats span {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(23, 32, 51, 0.05);
  color: var(--qa-muted);
  font-size: 12px;
}

.summary-list {
  display: flex;
  flex-direction: column;
  padding: 8px 16px 0;
}

.summary-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 4px;
  border-bottom: 1px solid #edf2f7;
}

.summary-row span {
  color: var(--qa-muted);
  font-size: 12px;
}

.summary-row strong {
  color: var(--qa-ink);
  font-size: 13px;
  text-align: right;
}

.summary-note {
  margin: 16px;
  padding: 16px;
  border-radius: 14px;
  background: linear-gradient(180deg, #f7fbff 0%, #eef5ff 100%);
}

.summary-note__title {
  margin-bottom: 8px;
  color: #254b8b;
  font-size: 13px;
  font-weight: 700;
}

.summary-note p {
  margin: 0;
  color: #42526b;
  font-size: 12px;
  line-height: 1.75;
}

@media (max-width: 1200px) {
  .detail-overview {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .detail-main {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .detail-overview {
    grid-template-columns: 1fr;
  }

  .timeline-item__head {
    flex-direction: column;
    align-items: flex-start;
  }

  .timeline-item__meta {
    justify-content: flex-start;
  }
}
</style>
