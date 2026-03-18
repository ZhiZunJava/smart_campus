<template>
  <div class="app-container qa-feedback-page">
    <el-form :inline="true" :model="queryParams" class="qa-toolbar">
      <el-form-item label="消息">
        <el-select v-model="queryParams.messageId" filterable clearable placeholder="选择消息" style="width: 320px">
          <el-option v-for="item in messageOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="用户">
        <el-select v-model="queryParams.userId" filterable clearable placeholder="选择用户" style="width: 240px">
          <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="反馈类型">
        <el-select v-model="queryParams.feedbackType" clearable style="width: 180px">
          <el-option label="有帮助" value="helpful" />
          <el-option label="无帮助" value="unhelpful" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="qa-toolbar qa-toolbar--actions">
      <el-button type="primary" plain icon="Plus" @click="handleAdd">新增反馈</el-button>
    </div>

    <el-table v-loading="loading" :data="dataList" class="qa-table">
      <el-table-column label="反馈对象" min-width="320">
        <template #default="{ row }">
          <div class="feedback-card">
            <div class="feedback-card__title">
              <strong>反馈 #{{ row.id }}</strong>
              <el-tag size="small" :type="row.feedbackType === 'helpful' ? 'success' : 'danger'">
                {{ row.feedbackType === 'helpful' ? '有帮助' : '无帮助' }}
              </el-tag>
            </div>
            <div class="feedback-card__meta">
              <span>消息 {{ row.messageId }}</span>
              <span>用户 {{ row.userId }}</span>
            </div>
            <div class="feedback-card__content">{{ row.feedbackContent || '未填写附加说明' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
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

    <el-dialog v-model="open" title="新增反馈" width="680px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="消息">
          <el-select v-model="form.messageId" filterable clearable style="width: 100%">
            <el-option v-for="item in messageOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户">
          <el-select v-model="form.userId" filterable clearable style="width: 100%">
            <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="反馈类型">
          <el-select v-model="form.feedbackType" style="width: 100%">
            <el-option label="有帮助" value="helpful" />
            <el-option label="无帮助" value="unhelpful" />
          </el-select>
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input v-model="form.feedbackContent" type="textarea" rows="5" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addQaFeedback, listQaFeedback, listQaMessage } from '@/api/campus/qa'
import { fetchUserOptions } from '@/api/campus/options'
import { parseTime } from '@/utils/ruoyi'

const loading = ref(false)
const total = ref(0)
const open = ref(false)
const dataList = ref<any[]>([])
const userOptions = ref<any[]>([])
const messageOptions = ref<any[]>([])

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  messageId: undefined,
  userId: undefined,
  feedbackType: '',
})

const form = reactive<any>({
  messageId: undefined,
  userId: undefined,
  feedbackType: 'helpful',
  feedbackContent: '',
})

function formatTime(value?: string) {
  return value ? parseTime(value) || '-' : '-'
}

function resetForm() {
  Object.assign(form, {
    messageId: undefined,
    userId: undefined,
    feedbackType: 'helpful',
    feedbackContent: '',
  })
}

async function getList() {
  loading.value = true
  try {
    const res = await listQaFeedback(queryParams)
    dataList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.messageId = undefined
  queryParams.userId = undefined
  queryParams.feedbackType = ''
  getList()
}

function handleAdd() {
  resetForm()
  open.value = true
}

async function submitForm() {
  await addQaFeedback(form)
  ElMessage.success('提交成功')
  open.value = false
  await getList()
}

async function loadOptions() {
  userOptions.value = await fetchUserOptions()
  const res = await listQaMessage({ pageNum: 1, pageSize: 200 })
  messageOptions.value = (res.rows || []).map((item: any) => ({
    label: `${item.roleType || 'message'} #${item.messageId} - ${(item.content || '').slice(0, 24) || '无内容'}`,
    value: item.messageId,
  }))
}

onMounted(async () => {
  await loadOptions()
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

.feedback-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 4px 0;
}

.feedback-card__title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.feedback-card__title strong {
  color: #172033;
  font-size: 14px;
}

.feedback-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 14px;
  color: #667085;
  font-size: 12px;
}

.feedback-card__content {
  color: #344054;
  font-size: 13px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
