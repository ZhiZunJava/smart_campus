<template>
   <div class="app-container notice-page">
      <div class="notice-shell">
         <div class="notice-shell__toolbar">
            <div class="notice-shell__headline">
               <div>
                  <h2>通知公告</h2>
                  <p>统一管理系统通知与公告内容，支持筛选、维护与查看发布状态。</p>
               </div>
               <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
            </div>

            <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" class="notice-filter">
               <el-form-item label="公告标题" prop="noticeTitle">
                  <el-input
                     v-model="queryParams.noticeTitle"
                     placeholder="请输入公告标题"
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
                     style="width: 220px"
                     @keyup.enter="handleQuery"
                  />
               </el-form-item>
               <el-form-item label="类型" prop="noticeType">
                  <el-select v-model="queryParams.noticeType" placeholder="公告类型" clearable style="width: 180px">
                     <el-option
                        v-for="dict in sys_notice_type"
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
               >新增</el-button>
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
         <el-table-column label="序号" align="center" prop="noticeId" width="100" />
         <el-table-column
            label="公告标题"
            align="center"
            prop="noticeTitle"
            :show-overflow-tooltip="true"
         />
         <el-table-column label="公告类型" align="center" prop="noticeType" width="100">
            <template #default="scope">
               <dict-tag :options="sys_notice_type" :value="scope.row.noticeType" />
            </template>
         </el-table-column>
         <el-table-column label="状态" align="center" prop="status" width="100">
            <template #default="scope">
               <dict-tag :options="sys_notice_status" :value="scope.row.status" />
            </template>
         </el-table-column>
         <el-table-column label="创建者" align="center" prop="createBy" width="100" />
         <el-table-column label="创建时间" align="center" prop="createTime" width="100">
            <template #default="scope">
               <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
            </template>
         </el-table-column>
         <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template #default="scope">
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:notice:edit']">修改</el-button>
               <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:notice:remove']" >删除</el-button>
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

      <!-- 添加或修改公告对话框 -->
      <el-dialog :title="title" v-model="open" width="780px" append-to-body>
         <el-form ref="noticeRef" :model="form" :rules="rules" label-width="80px">
            <el-row>
               <el-col :span="12">
                  <el-form-item label="公告标题" prop="noticeTitle">
                     <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="公告类型" prop="noticeType">
                     <el-select v-model="form.noticeType" placeholder="请选择">
                        <el-option
                           v-for="dict in sys_notice_type"
                           :key="dict.value"
                           :label="dict.label"
                           :value="dict.value"
                        ></el-option>
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="24">
                  <el-form-item label="状态">
                     <el-radio-group v-model="form.status">
                        <el-radio
                           v-for="dict in sys_notice_status"
                           :key="dict.value"
                           :value="dict.value"
                        >{{ dict.label }}</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
               <el-col :span="24">
                  <el-form-item label="内容">
                    <editor v-model="form.noticeContent" :min-height="192"/>
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
import { listNotice, getNotice, delNotice, addNotice, updateNotice } from "@/api/system/notice"
import type { SysNotice, NoticeQueryParams } from '@/types/api/system/notice'

const { proxy } = getCurrentInstance()
const { sys_notice_status, sys_notice_type } = proxy.useDict("sys_notice_status", "sys_notice_type")

const noticeList = ref<SysNotice[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const ids = ref<number[]>([])
const single = ref<boolean>(true)
const multiple = ref<boolean>(true)
const total = ref<number>(0)
const title = ref<string>("")

const data = reactive({
  form: {} as SysNotice,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    noticeTitle: undefined,
    createBy: undefined,
    status: undefined
  } as NoticeQueryParams,
  rules: {
    noticeTitle: [{ required: true, message: "公告标题不能为空", trigger: "blur" }],
    noticeType: [{ required: true, message: "公告类型不能为空", trigger: "change" }]
  },
})

const { queryParams, form, rules } = toRefs(data)

/** 查询公告列表 */
function getList() {
  loading.value = true
  listNotice(queryParams.value).then(response => {
    noticeList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    noticeId: undefined,
    noticeTitle: undefined,
    noticeType: undefined,
    noticeContent: undefined,
    status: "0"
  }
  proxy.resetForm("noticeRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection: SysNotice[]) {
  ids.value = selection.map(item => item.noticeId!)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加公告"
}

/**修改按钮操作 */
function handleUpdate(row?: SysNotice) {
  reset()
  const noticeId = row?.noticeId || ids.value[0]
  getNotice(noticeId).then(response => {
    form.value = response.data!
    open.value = true
    title.value = "修改公告"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["noticeRef"].validate((valid: boolean) => {
    if (valid) {
      if (form.value.noticeId != undefined) {
        updateNotice(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addNotice(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row?: SysNotice) {
  const noticeIds = row?.noticeId || ids.value
  proxy.$modal.confirm('是否确认删除公告编号为"' + noticeIds + '"的数据项？').then(function() {
    return delNotice(noticeIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

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
