<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="教材名称"><el-input v-model="queryParams.textbookName" clearable style="width:220px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="ISBN"><el-input v-model="queryParams.isbn" clearable style="width:180px" @keyup.enter="getList" /></el-form-item>
      <el-form-item label="教材类别">
        <el-select v-model="queryParams.category" clearable style="width:140px">
          <el-option label="必修" value="必修" /><el-option label="选修" value="选修" /><el-option label="参考书" value="参考书" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable style="width:120px">
          <el-option label="正常" value="0" /><el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="教材" min-width="280">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.textbookName }}</strong>
            <span>{{ row.textbookCode || '未配置编码' }}</span>
            <div class="entity-cell__meta">
              <span v-if="row.isbn">ISBN: {{ row.isbn }}</span>
              <span v-if="row.category">{{ row.category }}</span>
              <span v-if="row.subjectType">{{ row.subjectType }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="作者 / 出版社" min-width="200">
        <template #default="{ row }">
          <div class="maintain-cell">
            <span>{{ row.author || '-' }}</span>
            <span>{{ row.publisher || '-' }}</span>
            <span v-if="row.edition">{{ row.edition }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="单价" width="100" align="right">
        <template #default="{ row }">{{ row.price != null ? `¥${row.price}` : '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="维护信息" min-width="200">
        <template #default="{ row }">
          <div class="maintain-cell">
            <span>创建：{{ row.createBy || '-' }}</span>
            <span>创建时间：{{ formatDateTime(row.createTime) }}</span>
            <span>更新：{{ row.updateBy || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="720px">
      <el-form :model="form" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="教材名称"><el-input v-model="form.textbookName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="教材编码"><el-input v-model="form.textbookCode" placeholder="留空自动生成" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="ISBN"><el-input v-model="form.isbn" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="作者"><el-input v-model="form.author" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="出版社"><el-input v-model="form.publisher" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="版次"><el-input v-model="form.edition" placeholder="如：第3版" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="出版年月"><el-input v-model="form.publishDate" placeholder="如：2024-09" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="单价（元）"><el-input-number v-model="form.price" :min="0" :precision="2" controls-position="right" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="教材类别">
              <el-select v-model="form.category" clearable style="width:100%">
                <el-option label="必修" value="必修" /><el-option label="选修" value="选修" /><el-option label="参考书" value="参考书" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="学科分类"><el-input v-model="form.subjectType" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="正常" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" rows="3" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button @click="open=false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listTextbook, addTextbook, updateTextbook, removeTextbook } from '@/api/campus/affair'

const loading = ref(false), showSearch = ref(true), total = ref(0), open = ref(false), title = ref('')
const ids = ref<any[]>([]), single = ref(true), multiple = ref(true), dataList = ref<any[]>([])
const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, textbookName: '', isbn: '', category: undefined, status: undefined })
const form = reactive<any>({})

function formatDateTime(value: string) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

function resetFormData() {
  Object.assign(form, {
    textbookId: undefined, textbookCode: '', textbookName: '', isbn: '', author: '', publisher: '',
    edition: '', publishDate: '', price: undefined, coverUrl: '', category: '', subjectType: '', status: '0', remark: '',
  })
}

async function getList() {
  loading.value = true
  const res = await listTextbook(queryParams)
  dataList.value = res.rows || []
  total.value = res.total || 0
  loading.value = false
}

function resetQuery() {
  queryParams.pageNum = 1; queryParams.textbookName = ''; queryParams.isbn = ''; queryParams.category = undefined; queryParams.status = undefined
  getList()
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map(i => i.textbookId); single.value = selection.length !== 1; multiple.value = !selection.length
}

function handleAdd() { resetFormData(); title.value = '新增教材'; open.value = true }

function handleUpdate(row?: any) {
  const item = row || dataList.value.find((i: any) => i.textbookId === ids.value[0])
  if (!item) return
  resetFormData(); Object.assign(form, item); title.value = '修改教材'; open.value = true
}

async function submitForm() {
  if (form.textbookId) { await updateTextbook(form); ElMessage.success('修改成功') }
  else { await addTextbook(form); ElMessage.success('新增成功') }
  open.value = false; getList()
}

async function handleDelete(row?: any) {
  const target = row?.textbookId || ids.value
  if (!target || (Array.isArray(target) && !target.length)) return
  await ElMessageBox.confirm('确认删除所选教材吗？', '提示', { type: 'warning' })
  await removeTextbook(target); ElMessage.success('删除成功'); getList()
}

onMounted(() => { resetFormData(); getList() })
</script>

<style scoped>
.mb16{margin-bottom:16px;}
.entity-cell{display:flex;flex-direction:column;gap:6px}
.entity-cell strong{color:#172033;font-size:14px}
.entity-cell span{color:#667085;font-size:12px}
.entity-cell__meta{display:flex;flex-wrap:wrap;gap:6px}
.entity-cell__meta span{
  display:inline-flex;align-items:center;padding:2px 8px;
  border-radius:999px;background:#f3f6fb;color:#4b5565;font-size:12px;
}
.maintain-cell{display:flex;flex-direction:column;gap:4px;color:#667085;font-size:12px;line-height:1.5}
</style>
