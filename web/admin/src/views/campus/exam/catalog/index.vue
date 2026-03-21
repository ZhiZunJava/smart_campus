<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" clearable filterable style="width: 220px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="题库名称">
        <el-input v-model="queryParams.catalogName" style="width: 240px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="可见范围">
        <el-select v-model="queryParams.visibilityType" clearable style="width: 180px">
          <el-option label="私有" value="PRIVATE" />
          <el-option label="角色共享" value="ROLE_SHARED" />
          <el-option label="班级共享" value="CLASS_SHARED" />
          <el-option label="部门共享" value="DEPT_SHARED" />
          <el-option label="公开" value="PUBLIC" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable style="width: 140px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增题库</el-button></el-col>
    </el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="题库ID" prop="catalogId" width="100" />
      <el-table-column label="题库名称" prop="catalogName" min-width="220" />
      <el-table-column label="课程" min-width="180" show-overflow-tooltip>
        <template #default="scope">{{ courseLabel(scope.row.courseId) }}</template>
      </el-table-column>
      <el-table-column label="可见范围" width="120">
        <template #default="scope">
          <el-tag :type="visibilityTag(scope.row.visibilityType)" effect="plain">
            {{ visibilityLabel(scope.row.visibilityType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="范围数量" width="100">
        <template #default="scope">{{ scope.row.scopes?.length || 0 }}</template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="980px" destroy-on-close>
      <el-form :model="form" label-width="96px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="题库名称"><el-input v-model="form.catalogName" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="课程">
              <el-select v-model="form.courseId" clearable filterable>
                <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="可见范围">
              <el-select v-model="form.visibilityType">
                <el-option label="私有" value="PRIVATE" />
                <el-option label="角色共享" value="ROLE_SHARED" />
                <el-option label="班级共享" value="CLASS_SHARED" />
                <el-option label="部门共享" value="DEPT_SHARED" />
                <el-option label="公开" value="PUBLIC" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status">
                <el-option label="正常" value="0" />
                <el-option label="停用" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item></el-col>
        </el-row>
      </el-form>

      <div class="scope-panel">
        <div class="scope-panel__header">
          <div>
            <div class="panel-title">可见范围</div>
            <div class="panel-subtitle">支持角色、班级、部门或指定用户的可见控制；当前先使用手工录入对象名称与对象ID。</div>
          </div>
          <el-button type="primary" plain icon="Plus" @click="addScope">新增范围</el-button>
        </div>
        <el-table :data="form.scopes" border>
          <el-table-column label="范围类型" width="160">
            <template #default="scope">
              <el-select v-model="scope.row.scopeType">
                <el-option label="角色" value="ROLE" />
                <el-option label="班级" value="CLASS" />
                <el-option label="部门" value="DEPT" />
                <el-option label="用户" value="USER" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="对象ID" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.scopeRefId" />
            </template>
          </el-table-column>
          <el-table-column label="对象名称" min-width="220">
            <template #default="scope">
              <el-input v-model="scope.row.scopeRefName" />
            </template>
          </el-table-column>
          <el-table-column label="权限级别" width="160">
            <template #default="scope">
              <el-select v-model="scope.row.permissionLevel">
                <el-option label="查看" value="VIEW" />
                <el-option label="管理" value="MANAGE" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button link type="danger" icon="Delete" @click="removeScope(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="题库详情" width="980px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="题库名称">{{ detail.catalogName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ courseLabel(detail.courseId) }}</el-descriptions-item>
        <el-descriptions-item label="可见范围">{{ visibilityLabel(detail.visibilityType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status === '0' ? '正常' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div class="panel-title mt16">范围列表</div>
      <el-table :data="detail.scopes || []" border class="mt12">
        <el-table-column label="范围类型" prop="scopeType" width="120" />
        <el-table-column label="对象ID" prop="scopeRefId" width="120" />
        <el-table-column label="对象名称" prop="scopeRefName" min-width="220" />
        <el-table-column label="权限级别" prop="permissionLevel" width="120" />
      </el-table>
      <div class="panel-title mt16">归属题目</div>
      <el-table :data="detailQuestions" border class="mt12">
        <el-table-column label="题目ID" prop="questionId" width="90" />
        <el-table-column label="题型" width="110">
          <template #default="scope">{{ questionTypeLabel(scope.row.questionType) }}</template>
        </el-table-column>
        <el-table-column label="难度" width="90" prop="difficultyLevel" />
        <el-table-column label="题干" min-width="320" show-overflow-tooltip>
          <template #default="scope">{{ stripHtml(scope.row.stem) }}</template>
        </el-table-column>
        <el-table-column label="来源" prop="source" min-width="140" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addQuestionCatalog, delQuestionCatalog, getQuestionCatalog, listQuestion, listQuestionCatalog, updateQuestionCatalog } from '@/api/campus/exam'
import { fetchCourseOptions } from '@/api/campus/options'

const loading = ref(false)
const total = ref(0)
const open = ref(false)
const detailOpen = ref(false)
const title = ref('')
const dataList = ref<any[]>([])
const detail = ref<any>({})
const detailQuestions = ref<any[]>([])
const courseOptions = ref<any[]>([])

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  courseId: undefined,
  catalogName: '',
  visibilityType: '',
  status: '',
})

const form = reactive<any>({})

function resetFormData() {
  Object.assign(form, {
    catalogId: undefined,
    catalogName: '',
    courseId: undefined,
    visibilityType: 'PRIVATE',
    status: '0',
    remark: '',
    scopes: [],
  })
}

function addScope() {
  form.scopes.push({
    scopeType: 'ROLE',
    scopeRefId: '',
    scopeRefName: '',
    permissionLevel: 'VIEW',
    status: '0',
  })
}

function removeScope(index: number) {
  form.scopes.splice(index, 1)
}

function visibilityLabel(type: string) {
  return ({
    PRIVATE: '私有',
    ROLE_SHARED: '角色共享',
    CLASS_SHARED: '班级共享',
    DEPT_SHARED: '部门共享',
    PUBLIC: '公开',
  } as any)[type] || type || '-'
}

function visibilityTag(type: string) {
  if (type === 'PUBLIC') return 'success'
  if (type === 'PRIVATE') return 'info'
  return 'warning'
}

function courseLabel(courseId: number | string | undefined) {
  if (!courseId) return '通用题库'
  const matched = courseOptions.value.find((item: any) => String(item.value) === String(courseId))
  return matched?.label || `课程 ${courseId}`
}

function stripHtml(value: string) {
  return String(value || '').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

function questionTypeLabel(type: string) {
  return ({
    single: '单选题',
    multiple: '多选题',
    judge: '判断题',
    fill: '填空题',
    essay: '简答题',
    material: '材料题',
    case: '案例题',
  } as any)[type] || type || '-'
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.courseId = undefined
  queryParams.catalogName = ''
  queryParams.visibilityType = ''
  queryParams.status = ''
  getList()
}

async function getList() {
  loading.value = true
  try {
    const res = await listQuestionCatalog(queryParams)
    dataList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  resetFormData()
  title.value = '新增题库'
  open.value = true
}

async function handleEdit(row: any) {
  const res = await getQuestionCatalog(row.catalogId)
  const data = res.data || {}
  Object.assign(form, {
    catalogId: data.catalogId,
    catalogName: data.catalogName,
    courseId: data.courseId,
    visibilityType: data.visibilityType || 'PRIVATE',
    status: data.status || '0',
    remark: data.remark || '',
    scopes: (data.scopes || []).map((item: any) => ({
      id: item.id,
      scopeType: item.scopeType,
      scopeRefId: item.scopeRefId,
      scopeRefName: item.scopeRefName,
      permissionLevel: item.permissionLevel || 'VIEW',
      status: item.status || '0',
    })),
  })
  title.value = '编辑题库'
  open.value = true
}

async function handleView(row: any) {
  const [catalogRes, questionRes] = await Promise.all([
    getQuestionCatalog(row.catalogId),
    listQuestion({ pageNum: 1, pageSize: 200, catalogId: row.catalogId, status: '0' }),
  ])
  detail.value = catalogRes.data || {}
  detailQuestions.value = questionRes.rows || []
  detailOpen.value = true
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确认删除题库「${row.catalogName}」吗？`, '提示', { type: 'warning' })
  await delQuestionCatalog(row.catalogId)
  ElMessage.success('删除成功')
  getList()
}

async function submitForm() {
  const payload = {
    catalogId: form.catalogId,
    catalogName: form.catalogName,
    courseId: form.courseId,
    visibilityType: form.visibilityType,
    status: form.status,
    remark: form.remark,
    scopes: (form.scopes || []).map((item: any) => ({
      id: item.id,
      scopeType: item.scopeType,
      scopeRefId: item.scopeRefId,
      scopeRefName: item.scopeRefName,
      permissionLevel: item.permissionLevel,
      status: item.status || '0',
    })),
  }
  if (form.catalogId) {
    await updateQuestionCatalog(payload)
    ElMessage.success('修改成功')
  } else {
    await addQuestionCatalog(payload)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

onMounted(async () => {
  resetFormData()
  courseOptions.value = await fetchCourseOptions()
  await getList()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.mt12 { margin-top: 12px; }
.mt16 { margin-top: 16px; }
.scope-panel {
  margin-top: 16px;
  padding: 16px;
  border-radius: 4px;
  background: linear-gradient(180deg, #f9fbff 0%, #f4f8fc 100%);
  border: 1px solid var(--el-border-color-lighter);
}
.scope-panel__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}
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
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
