<template>
   <div class="app-container notice-page">
      <div class="notice-shell">
         <div class="notice-shell__toolbar">
            <div class="notice-shell__headline">
               <div>
                  <h2>消息中心</h2>
                  <p>统一管理未读消息与通知公告。学生端“我的待办”来自任务中心，不在这里创建，避免和真实任务重复。</p>
               </div>
               <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
            </div>

            <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" class="notice-filter">
               <el-form-item label="标题" prop="noticeTitle">
                  <el-input
                     v-model="queryParams.noticeTitle"
                     placeholder="请输入消息标题"
                     clearable
                     style="width: 220px"
                     @keyup.enter="handleQuery"
                  />
               </el-form-item>
               <el-form-item label="操作人员" prop="createBy">
                  <el-input
                     v-model="queryParams.createBy"
                     placeholder="请输入操作人员"
                     clearable
                     style="width: 180px"
                     @keyup.enter="handleQuery"
                  />
               </el-form-item>
               <el-form-item label="内容类型" prop="noticeType">
                  <el-select v-model="queryParams.noticeType" placeholder="公告类型" clearable style="width: 150px">
                     <el-option
                        v-for="dict in sys_notice_type"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                     />
                  </el-select>
               </el-form-item>
               <el-form-item label="业务分类" prop="bizCategory">
                  <el-select v-model="queryParams.bizCategory" placeholder="消息分类" clearable style="width: 160px">
                     <el-option v-for="item in bizCategoryOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
               </el-form-item>
               <el-form-item label="接收范围" prop="receiverScope">
                  <el-select v-model="queryParams.receiverScope" placeholder="接收范围" clearable style="width: 160px">
                     <el-option v-for="item in receiverScopeOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
               </el-form-item>
               <el-form-item label="状态" prop="status">
                  <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 140px">
                     <el-option
                        v-for="dict in sys_notice_status"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                     />
                  </el-select>
               </el-form-item>
               <el-form-item class="notice-filter__actions">
                  <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                  <el-button icon="Refresh" @click="resetQuery">重置</el-button>
               </el-form-item>
            </el-form>

            <div class="notice-actions">
               <el-button
                  type="primary"
                  plain
                  icon="Plus"
                  @click="handleAdd"
                  v-hasPermi="['system:notice:add']"
               >新增消息</el-button>
               <el-button
                  type="success"
                  plain
                  icon="Edit"
                  :disabled="single"
                  @click="handleUpdate"
                  v-hasPermi="['system:notice:edit']"
               >修改</el-button>
               <el-button
                  type="danger"
                  plain
                  icon="Delete"
                  :disabled="multiple"
                  @click="handleDelete"
                  v-hasPermi="['system:notice:remove']"
               >删除</el-button>
            </div>
         </div>

         <div class="notice-shell__table">
            <el-table v-loading="loading" :data="noticeList" @selection-change="handleSelectionChange">
               <el-table-column type="selection" width="55" align="center" />
               <el-table-column label="序号" align="center" prop="noticeId" width="88" />
               <el-table-column label="标题" align="center" prop="noticeTitle" min-width="220" :show-overflow-tooltip="true" />
               <el-table-column label="业务分类" align="center" prop="bizCategory" width="110">
                  <template #default="scope">
                     <el-tag :type="bizCategoryTagType(scope.row.bizCategory)" effect="light">
                        {{ bizCategoryLabel(scope.row.bizCategory) }}
                     </el-tag>
                  </template>
               </el-table-column>
               <el-table-column label="内容类型" align="center" prop="noticeType" width="100">
                  <template #default="scope">
                     <dict-tag :options="sys_notice_type" :value="scope.row.noticeType" />
                  </template>
               </el-table-column>
               <el-table-column label="接收范围" align="center" prop="receiverScope" width="110">
                  <template #default="scope">
                     <span>{{ receiverScopeLabel(scope.row.receiverScope) }}</span>
                  </template>
               </el-table-column>
               <el-table-column label="置顶" align="center" prop="pinned" width="80">
                  <template #default="scope">
                     <el-tag :type="scope.row.pinned === '1' ? 'warning' : 'info'" effect="plain">
                        {{ scope.row.pinned === '1' ? '是' : '否' }}
                     </el-tag>
                  </template>
               </el-table-column>
               <el-table-column label="状态" align="center" prop="status" width="90">
                  <template #default="scope">
                     <dict-tag :options="sys_notice_status" :value="scope.row.status" />
                  </template>
               </el-table-column>
               <el-table-column label="发布时间" align="center" prop="publishTime" width="168">
                  <template #default="scope">
                     <span>{{ parseTime(scope.row.publishTime || scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
                  </template>
               </el-table-column>
               <el-table-column label="创建者" align="center" prop="createBy" width="100" />
               <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
                  <template #default="scope">
                     <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:notice:edit']">修改</el-button>
                     <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:notice:remove']">删除</el-button>
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
         </div>
      </div>

      <el-dialog :title="title" v-model="open" width="860px" append-to-body>
         <el-form ref="noticeRef" :model="form" :rules="rules" label-width="100px">
            <el-row :gutter="16">
               <el-col :span="12">
                  <el-form-item label="消息标题" prop="noticeTitle">
                     <el-input v-model="form.noticeTitle" placeholder="请输入消息标题" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="内容类型" prop="noticeType">
                     <el-select v-model="form.noticeType" placeholder="请选择">
                        <el-option
                           v-for="dict in sys_notice_type"
                           :key="dict.value"
                           :label="dict.label"
                           :value="dict.value"
                        />
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="业务分类" prop="bizCategory">
                     <el-select v-model="form.bizCategory" placeholder="请选择业务分类">
                        <el-option v-for="item in bizCategoryOptions" :key="item.value" :label="item.label" :value="item.value" />
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="接收范围" prop="receiverScope">
                     <el-select v-model="form.receiverScope" placeholder="请选择接收范围">
                        <el-option v-for="item in receiverScopeOptions" :key="item.value" :label="item.label" :value="item.value" />
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="24">
                  <div class="notice-form-tip">
                     <strong>{{ bizCategoryHelper.title }}</strong>
                     <span>{{ bizCategoryHelper.desc }}</span>
                  </div>
               </el-col>
               <el-col :span="24" v-if="form.receiverScope === 'CUSTOM'">
                  <el-form-item label="指定用户ID" prop="receiverUserIds">
                     <el-input v-model="form.receiverUserIds" placeholder="多个用户ID请用英文逗号分隔，例如：100,101,102" />
                  </el-form-item>
               </el-col>
               <el-col :span="24">
                  <el-form-item label="摘要" prop="noticeSummary">
                     <el-input
                        v-model="form.noticeSummary"
                        type="textarea"
                        :rows="2"
                        maxlength="255"
                        show-word-limit
                        placeholder="用于学生端列表摘要展示，不填会自动截取正文"
                     />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="状态" prop="status">
                     <el-radio-group v-model="form.status">
                        <el-radio v-for="dict in sys_notice_status" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="是否置顶" prop="pinned">
                     <el-radio-group v-model="form.pinned">
                        <el-radio value="0">否</el-radio>
                        <el-radio value="1">是</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
               <el-col :span="24">
                  <div class="notice-advanced-toggle">
                     <el-switch v-model="showAdvancedAction" />
                     <span>启用高级跳转配置</span>
                     <em>只有需要点击后跳到指定页面时才填写</em>
                  </div>
               </el-col>
               <template v-if="showAdvancedAction">
                  <el-col :span="12">
                     <el-form-item label="动作类型" prop="actionType">
                        <el-input v-model="form.actionType" placeholder="例如 notice / course / custom" />
                     </el-form-item>
                  </el-col>
                  <el-col :span="12">
                     <el-form-item label="动作目标ID" prop="actionTargetId">
                        <el-input-number v-model="form.actionTargetId" :min="0" style="width: 100%" controls-position="right" />
                     </el-form-item>
                  </el-col>
                  <el-col :span="24">
                     <el-form-item label="动作路径" prop="actionPath">
                        <el-input v-model="form.actionPath" placeholder="例如 /student/courses" />
                     </el-form-item>
                  </el-col>
               </template>
               <el-col :span="12">
                  <el-form-item label="发布时间" prop="publishTime">
                     <el-date-picker
                        v-model="form.publishTime"
                        type="datetime"
                        value-format="YYYY-MM-DD HH:mm:ss"
                        placeholder="留空则立即发布"
                        style="width: 100%"
                     />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="失效时间" prop="expireTime">
                     <el-date-picker
                        v-model="form.expireTime"
                        type="datetime"
                        value-format="YYYY-MM-DD HH:mm:ss"
                        placeholder="留空则长期有效"
                        style="width: 100%"
                     />
                  </el-form-item>
               </el-col>
               <el-col :span="24">
                  <el-form-item label="内容">
                     <editor v-model="form.noticeContent" :min-height="220" />
                  </el-form-item>
               </el-col>
            </el-row>
         </el-form>
         <template #footer>
            <div class="dialog-footer">
               <el-button type="primary" @click="submitForm">确 定</el-button>
               <el-button @click="cancel">取 消</el-button>
            </div>
         </template>
      </el-dialog>
   </div>
</template>

<script setup lang="ts" name="Notice">
// @ts-nocheck
import { computed, getCurrentInstance, watch } from 'vue'
import { listNotice, getNotice, delNotice, addNotice, updateNotice } from '@/api/system/notice'
import type { SysNotice, NoticeQueryParams } from '@/types/api/system/notice'

const { proxy } = getCurrentInstance()
const { sys_notice_status, sys_notice_type } = proxy.useDict('sys_notice_status', 'sys_notice_type')

const bizCategoryOptions = [
  { label: '未读消息', value: 'MESSAGE' },
  { label: '通知公告', value: 'NOTICE' },
]

const receiverScopeOptions = [
  { label: '全部用户', value: 'ALL' },
  { label: '学生', value: 'STUDENT' },
  { label: '教师', value: 'TEACHER' },
  { label: '家长', value: 'PARENT' },
  { label: '指定用户', value: 'CUSTOM' },
]

const noticeList = ref<SysNotice[]>([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const showAdvancedAction = ref(false)
const ids = ref<number[]>([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')

const data = reactive({
  form: {} as SysNotice,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    noticeTitle: undefined,
    createBy: undefined,
    noticeType: undefined,
    bizCategory: undefined,
    receiverScope: undefined,
    status: undefined,
  } as NoticeQueryParams,
  rules: {
    noticeTitle: [{ required: true, message: '消息标题不能为空', trigger: 'blur' }],
    noticeType: [{ required: true, message: '内容类型不能为空', trigger: 'change' }],
    bizCategory: [{ required: true, message: '业务分类不能为空', trigger: 'change' }],
    receiverScope: [{ required: true, message: '接收范围不能为空', trigger: 'change' }],
  },
})

const { queryParams, form, rules } = toRefs(data)

const bizCategoryHelper = computed(() => {
  if (form.value.bizCategory === 'MESSAGE') {
    return {
      title: '未读消息建议',
      desc: '适合提醒类内容，学生处理后会从未读列表消失；可以配置跳转路径，也可以只做纯提醒。',
    }
  }
  return {
    title: '通知公告建议',
    desc: '适合面向多人长期展示的公告内容，通常不需要复杂跳转，默认全员或学生可见即可。',
  }
})

function getList() {
  loading.value = true
  listNotice(queryParams.value).then(response => {
    noticeList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  showAdvancedAction.value = false
  form.value = {
    noticeId: undefined,
    noticeTitle: undefined,
    noticeType: '1',
    bizCategory: 'MESSAGE',
    noticeSummary: undefined,
    receiverScope: 'STUDENT',
    receiverUserIds: undefined,
    actionType: 'message',
    actionPath: undefined,
    actionTargetId: undefined,
    pinned: '0',
    publishTime: undefined,
    expireTime: undefined,
    noticeContent: undefined,
    status: '0',
  }
  proxy.resetForm('noticeRef')
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection: SysNotice[]) {
  ids.value = selection.map(item => item.noticeId!)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增消息'
}

function handleUpdate(row?: SysNotice) {
  reset()
  const noticeId = row?.noticeId || ids.value[0]
  getNotice(noticeId).then(response => {
    form.value = response.data!
    if (!form.value.bizCategory) form.value.bizCategory = 'NOTICE'
    if (!form.value.receiverScope) form.value.receiverScope = 'ALL'
    if (!form.value.pinned) form.value.pinned = '0'
    if (!form.value.actionType) form.value.actionType = 'notice'
    showAdvancedAction.value = Boolean(form.value.actionPath || form.value.actionTargetId || (form.value.actionType && form.value.actionType !== 'message' && form.value.actionType !== 'notice'))
    open.value = true
    title.value = '编辑消息'
  })
}

function submitForm() {
  proxy.$refs.noticeRef.validate((valid: boolean) => {
    if (!valid) return
    if (form.value.noticeId != undefined) {
      updateNotice(form.value).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        open.value = false
        getList()
      })
      return
    }
    addNotice(form.value).then(() => {
      proxy.$modal.msgSuccess('新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row?: SysNotice) {
  const noticeIds = row?.noticeId || ids.value
  proxy.$modal.confirm('是否确认删除编号为"' + noticeIds + '"的消息数据项？').then(function() {
    return delNotice(noticeIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function bizCategoryLabel(value?: string) {
  return bizCategoryOptions.find((item) => item.value === value)?.label || '未分类'
}

function bizCategoryTagType(value?: string) {
  if (value === 'TODO') return 'warning'
  if (value === 'MESSAGE') return 'primary'
  return 'success'
}

function receiverScopeLabel(value?: string) {
  return receiverScopeOptions.find((item) => item.value === value)?.label || '全部用户'
}

watch(() => form.value.bizCategory, (value) => {
  if (!value) return
  if (value === 'MESSAGE') {
    form.value.noticeType = '1'
    if (!form.value.receiverScope || form.value.receiverScope === 'ALL') {
      form.value.receiverScope = 'STUDENT'
    }
    if (!form.value.actionType || form.value.actionType === 'notice') {
      form.value.actionType = 'message'
    }
    form.value.pinned = form.value.pinned || '0'
    return
  }
  form.value.noticeType = '2'
  if (!form.value.receiverScope) {
    form.value.receiverScope = 'ALL'
  }
  if (!form.value.actionType || form.value.actionType === 'message') {
    form.value.actionType = 'notice'
  }
  form.value.pinned = form.value.pinned || '0'
})

watch(showAdvancedAction, (enabled) => {
  if (enabled) return
  if (form.value.bizCategory === 'MESSAGE') {
    form.value.actionType = 'message'
  } else if (form.value.bizCategory === 'NOTICE') {
    form.value.actionType = 'notice'
  }
  form.value.actionTargetId = undefined
  form.value.actionPath = undefined
})

getList()
</script>

<style scoped>
.notice-page {
  padding-top: 12px;
}

.notice-shell {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.notice-shell__toolbar,
.notice-shell__table {
  border-radius: 18px;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-lighter);
  box-shadow: 0 18px 36px rgba(148, 163, 184, 0.12);
}

.notice-shell__toolbar {
  padding: 18px 20px 14px;
}

.notice-shell__headline {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
}

.notice-shell__headline h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.notice-shell__headline p {
  margin: 6px 0 0;
  color: var(--el-text-color-regular);
  font-size: 13px;
}

.notice-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 4px 10px;
}

.notice-filter :deep(.el-form-item) {
  margin-bottom: 10px;
}

.notice-filter__actions :deep(.el-button) {
  min-width: 92px;
}

.notice-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 4px;
}

.notice-shell__table {
  padding: 14px 16px 8px;
}

.notice-shell__table :deep(.el-table) {
  --el-table-border-color: var(--el-border-color-lighter);
  --el-table-header-bg-color: var(--el-fill-color-extra-light);
  --el-table-row-hover-bg-color: var(--el-color-primary-light-8);
}

.notice-shell__table :deep(.el-table th.el-table__cell) {
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.notice-shell__table :deep(.el-table td.el-table__cell) {
  color: var(--el-text-color-regular);
}

.notice-shell__table :deep(.pagination-container) {
  padding: 18px 0 6px;
}

.notice-form-tip {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 8px;
  padding: 12px 14px;
  border-radius: 12px;
  background: #f7faff;
  border: 1px solid #dbe7ff;
}

.notice-form-tip strong {
  color: var(--el-text-color-primary);
}

.notice-form-tip span {
  color: var(--el-text-color-regular);
  line-height: 1.7;
  font-size: 13px;
}

.notice-advanced-toggle {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  padding: 10px 14px;
  border-radius: 12px;
  background: #fafbfc;
  border: 1px dashed var(--el-border-color);
  color: var(--el-text-color-primary);
}

.notice-advanced-toggle em {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  font-style: normal;
}

@media (max-width: 992px) {
  .notice-shell__headline {
    flex-direction: column;
  }

  .notice-actions {
    flex-wrap: wrap;
  }
}

html.dark .notice-shell__toolbar,
html.dark .notice-shell__table {
  box-shadow: 0 14px 32px rgba(0, 0, 0, 0.18);
}
</style>
