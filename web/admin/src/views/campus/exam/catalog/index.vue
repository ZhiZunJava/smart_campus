<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="mb16 catalog-query">
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" clearable filterable style="width: 220px">
          <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="年级">
        <el-select v-model="queryParams.gradeId" clearable filterable style="width: 180px">
          <el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="题库名称">
        <el-input v-model="queryParams.catalogName" placeholder="支持模糊搜索" style="width: 240px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="可见范围">
        <el-select v-model="queryParams.visibilityType" clearable style="width: 180px">
          <el-option v-for="item in visibilityTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
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
      <el-table-column label="题库ID" prop="catalogId" width="96" />
      <el-table-column label="题库信息" min-width="280">
        <template #default="scope">
          <div class="catalog-main">
            <div class="catalog-main__title">{{ scope.row.catalogName || '-' }}</div>
            <div class="catalog-main__meta">
              <span>{{ courseLabel(scope.row.courseId) }}</span>
              <span>{{ gradeLabel(scope.row.gradeId) }}</span>
              <span>{{ ownerLabel(scope.row.ownerUserId) }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="可见策略" width="120">
        <template #default="scope">
          <el-tag :type="visibilityTag(scope.row.visibilityType)" effect="plain">
            {{ visibilityLabel(scope.row.visibilityType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="可见对象" min-width="260" show-overflow-tooltip>
        <template #default="scope">
          <div class="scope-summary">
            <span v-if="scopeSummary(scope.row)">{{ scopeSummary(scope.row) }}</span>
            <span v-else class="scope-summary__empty">未配置具体对象</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
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

    <el-dialog v-model="open" :title="title" width="1180px" top="5vh" destroy-on-close class="catalog-dialog">
      <div class="catalog-layout">
        <div class="catalog-layout__main">
          <div class="catalog-panel">
            <div class="catalog-panel__title">基础属性</div>
            <div class="catalog-panel__subtitle">先定义题库的归属、维护人和可见策略，再配置具体对象范围。</div>
            <el-form :model="form" label-position="top" class="catalog-form">
              <div class="catalog-form__grid">
                <el-form-item label="题库名称">
                  <el-input v-model="form.catalogName" placeholder="例如：高一数学阶段测验题库" />
                </el-form-item>
                <el-form-item label="维护人">
                  <el-select v-model="form.ownerUserId" clearable filterable placeholder="可不选，默认按创建人维护">
                    <el-option v-for="item in ownerUserOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
                <el-form-item label="所属课程">
                  <el-select v-model="form.courseId" clearable filterable placeholder="可不选，作为通用题库">
                    <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
                <el-form-item label="所属年级">
                  <el-select v-model="form.gradeId" clearable filterable placeholder="可不选，作为跨年级题库">
                    <el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
                <el-form-item label="可见策略">
                  <el-select v-model="form.visibilityType" placeholder="请选择">
                    <el-option v-for="item in visibilityTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="form.status">
                    <el-option label="正常" value="0" />
                    <el-option label="停用" value="1" />
                  </el-select>
                </el-form-item>
              </div>
              <el-form-item label="备注">
                <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="补充题库用途、适用阶段、维护说明" />
              </el-form-item>
            </el-form>
          </div>

          <div class="catalog-panel">
            <div class="scope-panel__header">
              <div>
                <div class="catalog-panel__title">具体可见对象</div>
                <div class="catalog-panel__subtitle">不再手填对象 ID。可直接选择角色、年级、班级或指定用户。</div>
              </div>
              <el-button type="primary" plain icon="Plus" @click="addScope">新增范围</el-button>
            </div>
            <el-table :data="form.scopes" border>
              <el-table-column label="范围类型" width="150">
                <template #default="scope">
                  <el-select v-model="scope.row.scopeType" @change="handleScopeTypeChange(scope.row)">
                    <el-option label="角色" value="ROLE" />
                    <el-option label="年级" value="GRADE" />
                    <el-option label="班级" value="CLASS" />
                    <el-option label="用户" value="USER" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="对象" min-width="300">
                <template #default="scope">
                  <el-select
                    v-if="scope.row.scopeType === 'ROLE'"
                    v-model="scope.row.scopeRefId"
                    clearable
                    filterable
                    placeholder="选择角色"
                    @change="(value: any) => syncScopeRefMeta(scope.row, value)"
                  >
                    <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                  <el-select
                    v-else-if="scope.row.scopeType === 'GRADE'"
                    v-model="scope.row.scopeRefId"
                    clearable
                    filterable
                    placeholder="选择年级"
                    @change="(value: any) => syncScopeRefMeta(scope.row, value)"
                  >
                    <el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                  <el-select
                    v-else-if="scope.row.scopeType === 'CLASS'"
                    v-model="scope.row.scopeRefId"
                    clearable
                    filterable
                    placeholder="选择班级"
                    @change="(value: any) => syncScopeRefMeta(scope.row, value)"
                  >
                    <el-option v-for="item in classOptionsForScope(scope.row)" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                  <el-select
                    v-else
                    v-model="scope.row.scopeRefId"
                    clearable
                    filterable
                    placeholder="选择用户"
                    @change="(value: any) => syncScopeRefMeta(scope.row, value)"
                  >
                    <el-option v-for="item in userOptionsForScope(scope.row)" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="显示名称" min-width="220">
                <template #default="scope">
                  <el-input v-model="scope.row.scopeRefName" readonly />
                </template>
              </el-table-column>
              <el-table-column label="权限级别" width="150">
                <template #default="scope">
                  <el-select v-model="scope.row.permissionLevel">
                    <el-option label="查看" value="VIEW" />
                    <el-option label="管理" value="MANAGE" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="96">
                <template #default="scope">
                  <el-button link type="danger" icon="Delete" @click="removeScope(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <aside class="catalog-layout__aside">
          <div class="catalog-aside-card">
            <div class="catalog-aside-card__title">策略说明</div>
            <ul class="catalog-aside-card__list">
              <li>私有：仅维护人或创建人使用，适合个人备课题库。</li>
              <li>角色共享：适合同教研组、命题组统一维护。</li>
              <li>年级/班级共享：适合按教学对象精准分发。</li>
              <li>公开：全局可见，适合公共题库或示范题库。</li>
            </ul>
          </div>
          <div class="catalog-aside-card">
            <div class="catalog-aside-card__title">当前摘要</div>
            <div class="catalog-summary">
              <div class="catalog-summary__row"><span>课程</span><strong>{{ courseLabel(form.courseId) }}</strong></div>
              <div class="catalog-summary__row"><span>年级</span><strong>{{ gradeLabel(form.gradeId) }}</strong></div>
              <div class="catalog-summary__row"><span>维护人</span><strong>{{ ownerLabel(form.ownerUserId) }}</strong></div>
              <div class="catalog-summary__row"><span>策略</span><strong>{{ visibilityLabel(form.visibilityType) }}</strong></div>
              <div class="catalog-summary__row"><span>对象</span><strong>{{ form.scopes?.length || 0 }} 个</strong></div>
            </div>
          </div>
        </aside>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="题库详情" width="1080px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="题库名称">{{ detail.catalogName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ courseLabel(detail.courseId) }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ gradeLabel(detail.gradeId) }}</el-descriptions-item>
        <el-descriptions-item label="维护人">{{ ownerLabel(detail.ownerUserId) }}</el-descriptions-item>
        <el-descriptions-item label="可见范围">{{ visibilityLabel(detail.visibilityType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status === '0' ? '正常' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div class="panel-title mt16">范围列表</div>
      <el-table :data="detail.scopes || []" border class="mt12">
        <el-table-column label="范围类型" width="120">
          <template #default="scope">{{ scopeTypeLabel(scope.row.scopeType) }}</template>
        </el-table-column>
        <el-table-column label="对象ID" prop="scopeRefId" width="120" />
        <el-table-column label="对象名称" prop="scopeRefName" min-width="220" />
        <el-table-column label="权限级别" width="120">
          <template #default="scope">{{ permissionLabel(scope.row.permissionLevel) }}</template>
        </el-table-column>
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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addQuestionCatalog, delQuestionCatalog, getQuestionCatalog, listQuestion, listQuestionCatalog, updateQuestionCatalog } from '@/api/campus/exam'
import { fetchClassOptions, fetchCourseOptions, fetchGradeOptions, fetchRoleOptions, fetchUserOptions } from '@/api/campus/options'

const loading = ref(false)
const total = ref(0)
const open = ref(false)
const detailOpen = ref(false)
const title = ref('')
const dataList = ref<any[]>([])
const detail = ref<any>({})
const detailQuestions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const gradeOptions = ref<any[]>([])
const classOptions = ref<any[]>([])
const roleOptions = ref<any[]>([])
const ownerUserOptions = ref<any[]>([])

const visibilityTypeOptions = [
  { label: '私有', value: 'PRIVATE' },
  { label: '角色共享', value: 'ROLE_SHARED' },
  { label: '年级共享', value: 'GRADE_SHARED' },
  { label: '班级共享', value: 'CLASS_SHARED' },
  { label: '部门共享', value: 'DEPT_SHARED' },
  { label: '指定用户', value: 'USER_SHARED' },
  { label: '公开', value: 'PUBLIC' },
]

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  courseId: undefined,
  gradeId: undefined,
  catalogName: '',
  visibilityType: '',
  status: '',
})

const form = reactive<any>({})

const formScopeSummary = computed(() => scopeSummary(form))

function resetFormData() {
  Object.assign(form, {
    catalogId: undefined,
    catalogName: '',
    courseId: undefined,
    gradeId: undefined,
    ownerUserId: undefined,
    visibilityType: 'PRIVATE',
    status: '0',
    remark: '',
    scopes: [],
  })
}

function addScope() {
  form.scopes.push({
    scopeType: 'ROLE',
    scopeRefId: undefined,
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
    GRADE_SHARED: '年级共享',
    CLASS_SHARED: '班级共享',
    DEPT_SHARED: '部门共享',
    USER_SHARED: '指定用户',
    PUBLIC: '公开',
  } as any)[type] || type || '-'
}

function visibilityTag(type: string) {
  if (type === 'PUBLIC') return 'success'
  if (type === 'PRIVATE') return 'info'
  if (type === 'USER_SHARED') return 'warning'
  return 'primary'
}

function scopeTypeLabel(type: string) {
  return ({ ROLE: '角色', GRADE: '年级', CLASS: '班级', USER: '用户', DEPT: '部门' } as any)[type] || type || '-'
}

function permissionLabel(level: string) {
  return ({ VIEW: '查看', MANAGE: '管理' } as any)[level] || level || '-'
}

function courseLabel(courseId: number | string | undefined) {
  if (!courseId) return '通用题库'
  const matched = courseOptions.value.find((item: any) => String(item.value) === String(courseId))
  return matched?.label || `课程 ${courseId}`
}

function gradeLabel(gradeId: number | string | undefined) {
  if (!gradeId) return '不限年级'
  const matched = gradeOptions.value.find((item: any) => String(item.value) === String(gradeId))
  return matched?.label || `年级 ${gradeId}`
}

function ownerLabel(userId: number | string | undefined) {
  if (!userId) return '默认创建人'
  const matched = ownerUserOptions.value.find((item: any) => String(item.value) === String(userId))
  return matched?.shortLabel || matched?.label || `用户 ${userId}`
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

function scopeSummary(target: any) {
  const scopes = target?.scopes || []
  if (!scopes.length) return ''
  return scopes
    .slice(0, 3)
    .map((item: any) => `${scopeTypeLabel(item.scopeType)}:${item.scopeRefName || item.scopeRefId}`)
    .join('；')
    + (scopes.length > 3 ? ` 等 ${scopes.length} 项` : '')
}

function classOptionsForScope(scope: any) {
  const targetGradeId = scope?.linkedGradeId || form.gradeId
  return classOptions.value.filter((item: any) => !targetGradeId || String(item.gradeId || '') === String(targetGradeId))
}

function userOptionsForScope(scope: any) {
  if (scope?.scopeType === 'USER' && scope?.linkedClassId) {
    return ownerUserOptions.value.filter((item: any) =>
      !item.classIds?.length || item.classIds.includes(scope.linkedClassId),
    )
  }
  return ownerUserOptions.value
}

function handleScopeTypeChange(scope: any) {
  scope.scopeRefId = undefined
  scope.scopeRefName = ''
}

function syncScopeRefMeta(scope: any, value: any) {
  const mapByType: Record<string, any[]> = {
    ROLE: roleOptions.value,
    GRADE: gradeOptions.value,
    CLASS: classOptions.value,
    USER: ownerUserOptions.value,
  }
  const matched = (mapByType[scope?.scopeType] || []).find((item: any) => String(item.value) === String(value))
  scope.scopeRefName = matched?.shortLabel || matched?.label || ''
  if (scope?.scopeType === 'GRADE') {
    scope.linkedGradeId = value
  }
  if (scope?.scopeType === 'CLASS') {
    scope.linkedGradeId = matched?.gradeId
    scope.linkedClassId = value
  }
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.courseId = undefined
  queryParams.gradeId = undefined
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
    gradeId: data.gradeId,
    ownerUserId: data.ownerUserId,
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
      linkedGradeId: item.scopeType === 'GRADE' ? item.scopeRefId : undefined,
      linkedClassId: item.scopeType === 'CLASS' ? item.scopeRefId : undefined,
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
    gradeId: form.gradeId,
    ownerUserId: form.ownerUserId,
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

async function loadBaseOptions() {
  const [courseRes, gradeRes, classRes, roleRes, ownerRes] = await Promise.all([
    fetchCourseOptions(),
    fetchGradeOptions(),
    fetchClassOptions(),
    fetchRoleOptions(),
    fetchUserOptions(),
  ])
  courseOptions.value = courseRes || []
  gradeOptions.value = gradeRes || []
  classOptions.value = classRes || []
  roleOptions.value = (roleRes || []).filter((item: any) => item.status === '0')
  ownerUserOptions.value = ownerRes || []
}

onMounted(async () => {
  resetFormData()
  await loadBaseOptions()
  await getList()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.mt12 { margin-top: 12px; }
.mt16 { margin-top: 16px; }

.catalog-main {
  display: grid;
  gap: 6px;
}

.catalog-main__title {
  font-size: 15px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.catalog-main__meta {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.scope-summary {
  color: var(--el-text-color-regular);
  line-height: 1.6;
}

.scope-summary__empty {
  color: var(--el-text-color-secondary);
}

.catalog-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 300px;
  gap: 18px;
  align-items: start;
}

.catalog-layout__main {
  display: grid;
  gap: 16px;
  min-width: 0;
}

.catalog-layout__aside {
  display: grid;
  gap: 16px;
  min-width: 0;
}

.catalog-panel,
.catalog-aside-card {
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(180deg, #f9fbff 0%, #f4f8fc 100%);
  border: 1px solid var(--el-border-color-lighter);
  min-width: 0;
}

.catalog-panel__title,
.catalog-aside-card__title,
.panel-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.catalog-panel__subtitle,
.panel-subtitle {
  margin-top: 4px;
  margin-bottom: 14px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.6;
}

.catalog-form__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px 16px;
}

.catalog-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.catalog-form :deep(.el-input),
.catalog-form :deep(.el-select),
.catalog-form :deep(.el-input-number),
.catalog-form :deep(.el-textarea) {
  width: 100%;
}

.catalog-panel :deep(.el-select),
.catalog-panel :deep(.el-input),
.catalog-panel :deep(.el-input-number),
.catalog-panel :deep(.el-textarea) {
  width: 100%;
  max-width: 100%;
}

.catalog-panel :deep(.el-select__wrapper),
.catalog-panel :deep(.el-input__wrapper),
.catalog-panel :deep(.el-textarea__inner) {
  width: 100%;
  box-sizing: border-box;
}

.scope-panel__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.catalog-aside-card__list {
  margin: 12px 0 0;
  padding-left: 18px;
  color: var(--el-text-color-regular);
  line-height: 1.8;
}

.catalog-summary {
  display: grid;
  gap: 10px;
  margin-top: 12px;
}

.catalog-summary__row {
  display: grid;
  grid-template-columns: 56px minmax(0, 1fr);
  gap: 10px;
  align-items: start;
  font-size: 13px;
}

.catalog-summary__row span {
  color: var(--el-text-color-secondary);
}

.catalog-summary__row strong {
  color: var(--el-text-color-primary);
  line-height: 1.6;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.catalog-dialog .el-dialog) {
  max-width: calc(100vw - 32px);
}

:deep(.catalog-dialog .el-dialog__body) {
  overflow-x: hidden;
}

@media (max-width: 1320px) {
  .catalog-layout,
  .catalog-form__grid {
    grid-template-columns: 1fr;
  }
}
</style>
