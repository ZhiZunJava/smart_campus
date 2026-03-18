<template>
  <div class="app-container">
    <section class="teaching-page-shell">
      <div class="teaching-page-shell__copy">
        <div class="teaching-page-shell__eyebrow">教学基础</div>
        <h2>班级学生</h2>
        <p>按班级维护学生归属关系，支持快速调班、分班与状态校验，为后续班级课程与课表打底。</p>
      </div>
      <div class="teaching-page-shell__stats">
        <div v-for="item in headerStats" :key="item.label" class="teaching-page-shell__stat">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <small>{{ item.tip }}</small>
        </div>
      </div>
    </section>

    <el-form :inline="true" :model="queryParams" class="mb16">
      <el-form-item label="学生"><el-select v-model="queryParams.userId" filterable clearable style="width:240px"><el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="学期"><el-select v-model="queryParams.termId" filterable clearable style="width:220px"><el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="年级"><el-select v-model="queryParams.gradeId" filterable clearable style="width:220px"><el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item label="班级"><el-select v-model="queryParams.classId" filterable clearable style="width:220px"><el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="getList">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">调班/配置</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="学生信息" min-width="260">
        <template #default="{ row }">
          <div class="entity-cell">
            <strong>{{ row.realName || getOptionLabel(studentOptions, row.userId, '学生') }}</strong>
            <span>{{ row.studentNo || `用户ID ${row.userId}` }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="年级" min-width="160">
        <template #default="{ row }">{{ getOptionLabel(gradeOptions, row.gradeId, '年级') }}</template>
      </el-table-column>
      <el-table-column label="班级" min-width="180">
        <template #default="{ row }">{{ getOptionLabel(classOptions, row.classId, '班级') }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">{{ row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="学习目标" prop="learningGoal" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">调班/配置</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="学生">
          <el-select v-model="form.userId" filterable clearable style="width:100%" disabled>
            <el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="年级">
          <el-select v-model="form.gradeId" filterable clearable style="width:100%">
            <el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="form.classId" filterable clearable style="width:100%">
            <el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="open = false">取消</el-button><el-button type="primary" @click="submitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchClassOptions, fetchGradeOptions, fetchTermOptions, fetchUserOptions } from '@/api/campus/options'
import { listClassStudent, updateClassStudent } from '@/api/campus/classStudent'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const open = ref(false)
const title = ref('学生班级配置')
const ids = ref<any[]>([])
const single = ref(true)
const dataList = ref<any[]>([])
const studentOptions = ref<any[]>([])
const gradeOptions = ref<any[]>([])
const classOptions = ref<any[]>([])
const termOptions = ref<any[]>([])

const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, userId: undefined, gradeId: undefined, classId: undefined, termId: undefined })
const form = reactive<any>({ userId: undefined, gradeId: undefined, classId: undefined, status: '0', remark: '' })

const headerStats = computed(() => [
  { label: '当前页学生', value: total.value, tip: '支持按年级和班级筛选' },
  { label: '已分班', value: dataList.value.filter((item: any) => item.classId).length, tip: '用于后续班级课程和课表' },
  { label: '正常状态', value: dataList.value.filter((item: any) => item.status === '0').length, tip: '异常学生可独立停用' },
])

function getOptionLabel(options: any[], value: any, fallback: string) {
  return options.find((item) => item.value === value)?.label || `${fallback} ${value || '-'}`
}

function resetForm() {
  Object.assign(form, { userId: undefined, gradeId: undefined, classId: undefined, status: '0', remark: '' })
}

async function getList() {
  loading.value = true
  try {
    const res = await listClassStudent({ ...queryParams, userType: 'student' })
    dataList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryParams.pageNum = 1
  queryParams.userId = undefined
  queryParams.gradeId = undefined
  queryParams.classId = undefined
  getList()
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.userId)
  single.value = selection.length !== 1
}

function handleUpdate(row?: any) {
  const item = row || dataList.value.find((candidate: any) => candidate.userId === ids.value[0])
  if (!item) return
  resetForm()
  Object.assign(form, {
    userId: item.userId,
    gradeId: item.gradeId,
    classId: item.classId,
    status: item.status || '0',
    remark: item.remark || '',
  })
  open.value = true
}

async function submitForm() {
  await updateClassStudent(form)
  ElMessage.success('班级学生关系已更新')
  open.value = false
  getList()
}

watch(() => queryParams.termId, async (value) => {
  const term = termOptions.value.find((item: any) => item.value === value)
  const schoolYear = term?.label?.match(/（(.+)）/)?.[1]
  gradeOptions.value = await fetchGradeOptions(schoolYear)
  classOptions.value = await fetchClassOptions(schoolYear)
})

async function loadOptions() {
  studentOptions.value = await fetchUserOptions('student')
  termOptions.value = await fetchTermOptions()
  const current = termOptions.value.find((item: any) => item.isCurrent === '1')
  if (current) queryParams.termId = current.value
  const schoolYear = current?.label?.match(/（(.+)）/)?.[1]
  gradeOptions.value = await fetchGradeOptions(schoolYear)
  classOptions.value = await fetchClassOptions(schoolYear)
}

onMounted(async () => {
  await loadOptions()
  await getList()
})
</script>

<style scoped>
.mb16{margin-bottom:16px}
.teaching-page-shell{display:grid;grid-template-columns:minmax(0,1.15fr) minmax(320px,.85fr);gap:16px;margin-bottom:16px;padding:18px;border:1px solid #dbe3f0;border-radius:18px;background:linear-gradient(180deg,#fff 0%,#f7fbff 100%)}
.teaching-page-shell__eyebrow{color:#607188;font-size:12px;font-weight:700;letter-spacing:.08em}
.teaching-page-shell__copy h2{margin:8px 0 10px;color:#172033;font-size:24px;line-height:1.2}
.teaching-page-shell__copy p{margin:0;color:#526076;font-size:13px;line-height:1.8}
.teaching-page-shell__stats{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:10px}
.teaching-page-shell__stat{display:flex;flex-direction:column;gap:6px;padding:14px;border-radius:16px;background:#fff;border:1px solid rgba(219,227,240,.92)}
.teaching-page-shell__stat span,.teaching-page-shell__stat small{color:#667085;font-size:12px}
.teaching-page-shell__stat strong{color:#172033;font-size:22px;line-height:1.1}
.entity-cell{display:flex;flex-direction:column;gap:6px}
.entity-cell strong{color:#172033;font-size:13px}
.entity-cell span{color:#667085;font-size:12px}
@media (max-width: 1100px){.teaching-page-shell{grid-template-columns:1fr}.teaching-page-shell__stats{grid-template-columns:1fr}}
</style>
