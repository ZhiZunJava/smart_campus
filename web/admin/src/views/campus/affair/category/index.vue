<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch" class="mb16">
      <el-form-item label="分类名称">
        <el-input v-model="queryParams.categoryName" placeholder="请输入分类名称" clearable @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="服务分组">
        <el-input v-model="queryParams.serviceGroup" placeholder="如 FUNDING / ACADEMIC" clearable @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable placeholder="请选择状态" style="width: 140px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="overview-grid mb16">
      <div class="overview-card">
        <span>分类总数</span>
        <strong>{{ categoryList.length }}</strong>
      </div>
      <div class="overview-card is-success">
        <span>启用分类</span>
        <strong>{{ enabledCount }}</strong>
      </div>
      <div class="overview-card is-warning">
        <span>多角色覆盖</span>
        <strong>{{ multiRoleCount }}</strong>
      </div>
      <div class="overview-card is-info">
        <span>服务分组数</span>
        <strong>{{ serviceGroupCount }}</strong>
      </div>
    </div>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5">
        <el-button v-hasPermi="['campus:affairCategory:add']" type="primary" plain icon="Plus" @click="handleAdd">新增分类</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="categoryList">
      <el-table-column prop="categoryName" label="分类名称" min-width="180" />
      <el-table-column prop="categoryCode" label="分类编码" min-width="180" />
      <el-table-column prop="serviceGroup" label="服务分组" width="140" />
      <el-table-column label="模板数" width="90">
        <template #default="{ row }">{{ categoryMetrics[row.categoryCode]?.templateCount || 0 }}</template>
      </el-table-column>
      <el-table-column label="申请量" width="90">
        <template #default="{ row }">{{ categoryMetrics[row.categoryCode]?.requestCount || 0 }}</template>
      </el-table-column>
      <el-table-column label="待处理" width="90">
        <template #default="{ row }">{{ categoryMetrics[row.categoryCode]?.pendingCount || 0 }}</template>
      </el-table-column>
      <el-table-column label="适用角色" min-width="220">
        <template #default="{ row }">
          <div class="role-tags">
            <el-tag v-for="role in parseRoleScope(row.targetRoleScope)" :key="role" size="small" effect="plain">{{ roleLabel(role) }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="图标" width="100">
        <template #default="{ row }">
          <i :class="row.icon || 'ri-service-line'" style="font-size: 22px; color: #266fcb" />
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'info'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="220" show-overflow-tooltip />
      <el-table-column prop="updateTime" label="更新时间" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button v-hasPermi="['campus:affairCategory:edit']" link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button v-hasPermi="['campus:affairCategory:remove']" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="open" :title="title" width="640px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类编码" prop="categoryCode">
          <el-input v-model="form.categoryCode" placeholder="请输入分类编码，如 ACADEMIC_STATUS" />
        </el-form-item>
        <el-form-item label="服务分组" prop="serviceGroup">
          <el-select v-model="form.serviceGroup" filterable allow-create placeholder="选择或输入分组" style="width: 100%">
            <el-option label="考勤服务 (ATTENDANCE)" value="ATTENDANCE" />
            <el-option label="资助服务 (FUNDING)" value="FUNDING" />
            <el-option label="成长发展 (GROWTH)" value="GROWTH" />
            <el-option label="教务服务 (ACADEMIC)" value="ACADEMIC" />
          </el-select>
        </el-form-item>
        <el-form-item label="适用角色">
          <el-select v-model="targetRoleList" multiple collapse-tags style="width: 100%">
            <el-option label="学生" value="student" />
            <el-option label="教师" value="teacher" />
            <el-option label="辅导员" value="advisor" />
          </el-select>
        </el-form-item>
        <el-form-item label="图标">
          <div class="icon-picker">
            <el-select v-model="form.icon" filterable allow-create placeholder="选择图标" style="width: 100%">
              <el-option v-for="ic in iconOptions" :key="ic" :label="ic" :value="ic">
                <div style="display: flex; align-items: center; gap: 8px">
                  <i :class="ic" style="font-size: 18px" />
                  <span>{{ ic }}</span>
                </div>
              </el-option>
            </el-select>
            <i :class="form.icon || 'ri-service-line'" style="font-size: 24px; color: #266fcb; margin-left: 8px" />
          </div>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addAffairCategory, getAffairCategory, getAffairRequestStatistics, listAffairCategory, listAffairTemplate, removeAffairCategory, updateAffairCategory } from '@/api/campus/affair'

const loading = ref(false)
const submitting = ref(false)
const showSearch = ref(true)
const open = ref(false)
const title = ref('')
const total = ref(0)
const categoryList = ref<any[]>([])
const formRef = ref<any>()
const categoryMetrics = ref<Record<string, any>>({})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  categoryName: '',
  serviceGroup: '',
  status: '',
})

const form = reactive<any>({
  categoryId: undefined,
  categoryName: '',
  categoryCode: '',
  serviceGroup: '',
  targetRoleScope: 'student',
  icon: 'ri-service-line',
  sortOrder: 0,
  status: '0',
  remark: '',
})

const targetRoleList = ref<string[]>(['student'])

const iconOptions = [
  'ri-calendar-check-line', 'ri-map-pin-user-line', 'ri-award-line', 'ri-hand-coin-line',
  'ri-bank-card-line', 'ri-briefcase-4-line', 'ri-flag-2-line', 'ri-trophy-line',
  'ri-book-open-line', 'ri-lightbulb-flash-line', 'ri-exchange-box-line', 'ri-bar-chart-grouped-line',
  'ri-heart-pulse-line', 'ri-service-line', 'ri-file-list-3-line', 'ri-user-settings-line',
]

const enabledCount = computed(() => categoryList.value.filter((item) => item.status === '0').length)
const multiRoleCount = computed(() => categoryList.value.filter((item) => parseRoleScope(item.targetRoleScope).length > 1).length)
const serviceGroupCount = computed(() => new Set(categoryList.value.map((item) => item.serviceGroup).filter(Boolean)).size)

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  categoryCode: [{ required: true, message: '请输入分类编码', trigger: 'blur' }],
}

function parseRoleScope(value?: string) {
  return String(value || '')
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
}

function roleLabel(value: string) {
  const roleMap: Record<string, string> = {
    student: '学生',
    teacher: '教师',
    advisor: '辅导员',
    admin: '管理员',
  }
  return roleMap[value] || value
}

function resetForm() {
  Object.assign(form, {
    categoryId: undefined,
    categoryName: '',
    categoryCode: '',
    serviceGroup: '',
    targetRoleScope: 'student',
    icon: 'ri-service-line',
    sortOrder: 0,
    status: '0',
    remark: '',
  })
  targetRoleList.value = ['student']
  formRef.value?.clearValidate()
}

async function getList() {
  loading.value = true
  try {
    const [res, templateRes, statsRes] = await Promise.all([
      listAffairCategory(queryParams),
      listAffairTemplate({ pageNum: 1, pageSize: 500 }),
      getAffairRequestStatistics(),
    ])
    categoryList.value = res.rows || []
    total.value = res.total || 0
    const templateRows = templateRes.rows || []
    const byCategory = statsRes.data?.byCategory || statsRes.byCategory || []
    const metrics: Record<string, any> = {}
    templateRows.forEach((item: any) => {
      const key = item.categoryCode || ''
      if (!key) return
      if (!metrics[key]) metrics[key] = { templateCount: 0, requestCount: 0, pendingCount: 0 }
      metrics[key].templateCount += 1
    })
    byCategory.forEach((item: any) => {
      const key = item.categoryCode || ''
      if (!key) return
      if (!metrics[key]) metrics[key] = { templateCount: 0, requestCount: 0, pendingCount: 0 }
      metrics[key].requestCount = Number(item.totalCount || 0)
      metrics[key].pendingCount = Number(item.pendingCount || 0)
    })
    categoryMetrics.value = metrics
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(queryParams, { pageNum: 1, pageSize: 10, categoryName: '', serviceGroup: '', status: '' })
  getList()
}

function handleAdd() {
  resetForm()
  title.value = '新增事务分类'
  open.value = true
}

async function handleEdit(row: any) {
  resetForm()
  const res = await getAffairCategory(row.categoryId)
  Object.assign(form, res.data || res)
  targetRoleList.value = String(form.targetRoleScope || 'student').split(',').map((item) => item.trim()).filter(Boolean)
  title.value = '编辑事务分类'
  open.value = true
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确定删除分类“${row.categoryName}”吗？`, '提示', { type: 'warning' })
  await removeAffairCategory(row.categoryId)
  ElMessage.success('删除成功')
  getList()
}

async function submitForm() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    form.targetRoleScope = targetRoleList.value.join(',')
    if (form.categoryId) {
      await updateAffairCategory(form)
      ElMessage.success('修改成功')
    } else {
      await addAffairCategory(form)
      ElMessage.success('新增成功')
    }
    open.value = false
    getList()
  } finally {
    submitting.value = false
  }
}

onMounted(getList)
</script>

<style scoped>
.mb16 {
  margin-bottom: 16px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.overview-card {
  padding: 16px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.overview-card span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.overview-card strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
  color: #0f172a;
}

.overview-card.is-success strong { color: #059669; }
.overview-card.is-warning strong { color: #d97706; }
.overview-card.is-info strong { color: #2563eb; }

.role-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.icon-picker {
  display: flex;
  align-items: center;
  width: 100%;
}
</style>
