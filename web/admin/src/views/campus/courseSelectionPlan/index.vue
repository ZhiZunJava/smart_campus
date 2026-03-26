<template>
  <div class="app-container plan-page">
    <div class="plan-overview">
      <div class="plan-overview__card">
        <span>当前列表计划</span>
        <strong>{{ total }}</strong>
        <p>用于统一管理学生端标准选课、退课和审核申请的开放时段。</p>
      </div>
      <div class="plan-overview__card">
        <span>启用中的计划</span>
        <strong>{{ activePlanCount }}</strong>
        <p>同一学期仅允许一个启用计划，后端已做冲突校验。</p>
      </div>
      <div class="plan-overview__card">
        <span>带申请窗口</span>
        <strong>{{ requestWindowCount }}</strong>
        <p>便于把标准选课和个性化审核流程放在同一个计划里统一管理。</p>
      </div>
    </div>

    <el-form :inline="true" :model="queryParams" class="plan-search">
      <el-form-item label="学期">
        <el-select v-model="queryParams.termId" filterable clearable style="width: 220px">
          <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="计划类型">
        <el-select v-model="queryParams.planType" clearable style="width: 180px">
          <el-option v-for="item in planTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable style="width: 140px">
          <el-option label="启用" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb16">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd">新增计划</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="计划名称" prop="planName" min-width="220" />
      <el-table-column label="计划类型" width="130">
        <template #default="{ row }">
          <el-tag :type="planTypeTagType(row.planType)" effect="plain">{{ planTypeLabel(row.planType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="生效范围" width="140">
        <template #default="{ row }">
          <el-tag :type="planScopeTagType(resolvePlanScope(row))" effect="plain">{{ planScopeLabel(resolvePlanScope(row)) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="学期" prop="termName" min-width="180" />
      <el-table-column label="窗口配置" min-width="260">
        <template #default="{ row }">{{ buildWindowSummary(row) }}</template>
      </el-table-column>
      <el-table-column label="学生端公告" min-width="220" show-overflow-tooltip>
        <template #default="{ row }">{{ row.noticeContent || '未填写公告' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'info'">{{ row.status === '0' ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="open" :title="title" width="1120px">
      <div class="plan-dialog">
        <div class="plan-dialog__main">
          <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
            <div class="plan-section">
              <div class="plan-section__header">
                <strong>基础配置</strong>
                <span>先确定学期、类型和启停状态，再配置各类开放窗口。</span>
              </div>
              <el-row :gutter="16">
                <el-col :span="12">
                  <el-form-item label="计划名称" prop="planName">
                    <el-input v-model="form.planName" placeholder="例如：2025-2026-2 常规选课计划" @input="planNameTouched = true" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="学期" prop="termId">
                    <el-select v-model="form.termId" filterable style="width: 100%" @change="handleTermOrTypeChange">
                      <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="计划类型" prop="planType">
                    <el-select v-model="form.planType" style="width: 100%" @change="handleTermOrTypeChange">
                      <el-option v-for="item in planTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="计划状态" prop="status">
                    <el-radio-group v-model="form.status">
                      <el-radio-button value="0">启用</el-radio-button>
                      <el-radio-button value="1">停用</el-radio-button>
                    </el-radio-group>
                  </el-form-item>
                </el-col>
              </el-row>
            </div>

            <div class="plan-section">
              <div class="plan-section__header">
                <strong>快捷模板</strong>
                <span>快速填充计划类型、公告文案和规则说明，减少重复配置。</span>
              </div>
              <div class="preset-grid">
                <button
                  v-for="item in presetOptions"
                  :key="item.key"
                  type="button"
                  class="preset-card"
                  :class="{ 'is-active': form.planType === item.planType }"
                  @click="applyPreset(item)"
                >
                  <strong>{{ item.title }}</strong>
                  <span>{{ item.desc }}</span>
                </button>
              </div>
            </div>

            <div class="plan-section">
              <div class="plan-section__header">
                <strong>生效范围</strong>
                <span>明确这条计划控制的是选课中心、选课申请，还是两者一起开放。</span>
              </div>
              <div class="scope-grid">
                <button
                  v-for="item in planScopeOptions"
                  :key="item.value"
                  type="button"
                  class="scope-card"
                  :class="{ 'is-active': planScope === item.value }"
                  @click="setPlanScope(item.value)"
                >
                  <strong>{{ item.label }}</strong>
                  <span>{{ item.desc }}</span>
                </button>
              </div>
            </div>

            <div class="plan-section">
              <div class="plan-section__header">
                <strong>开放时间</strong>
                <span>时间段需要成对配置。标准选课、退课和个性化申请可按需分别开放。</span>
              </div>
              <el-row :gutter="16">
                <el-col v-if="showsStandardWindows" :span="24">
                  <el-form-item label="直接选课">
                    <el-date-picker
                      v-model="selectionRange"
                      type="datetimerange"
                      range-separator="至"
                      start-placeholder="选课开始"
                      end-placeholder="选课结束"
                      value-format="YYYY-MM-DD HH:mm:ss"
                      style="width: 100%"
                      clearable
                    />
                  </el-form-item>
                </el-col>
                <el-col v-if="showsStandardWindows" :span="24">
                  <el-form-item label="直接退课">
                    <el-date-picker
                      v-model="dropRange"
                      type="datetimerange"
                      range-separator="至"
                      start-placeholder="退课开始"
                      end-placeholder="退课结束"
                      value-format="YYYY-MM-DD HH:mm:ss"
                      style="width: 100%"
                      clearable
                    />
                  </el-form-item>
                </el-col>
                <el-col v-if="showsRequestWindow" :span="24">
                  <el-form-item label="审核申请">
                    <el-date-picker
                      v-model="requestRange"
                      type="datetimerange"
                      range-separator="至"
                      start-placeholder="申请开始"
                      end-placeholder="申请结束"
                      value-format="YYYY-MM-DD HH:mm:ss"
                      style="width: 100%"
                      clearable
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </div>

            <div class="plan-section">
              <div class="plan-section__header">
                <strong>学生端展示</strong>
                <span>这里的公告和规则会同步展示在学生端选课中心与选课申请页面。</span>
              </div>
              <el-form-item label="选课公告">
                <el-input v-model="form.noticeContent" type="textarea" :rows="4" maxlength="1000" show-word-limit placeholder="例如：请先完成标准选课，特殊场景再提交审核申请。" />
              </el-form-item>
              <el-form-item label="规则说明">
                <el-input v-model="form.ruleContent" type="textarea" :rows="5" maxlength="1500" show-word-limit placeholder="例如：本班必修默认保留，跨班修读需经审核后处理。" />
              </el-form-item>
              <el-form-item label="备注">
                <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可记录本轮面向对象、配置背景等内部说明" />
              </el-form-item>
            </div>
          </el-form>
        </div>

        <aside class="plan-dialog__aside">
          <div class="preview-card">
            <div class="preview-card__head">
              <strong>配置预览</strong>
              <el-tag :type="form.status === '0' ? 'success' : 'info'">{{ form.status === '0' ? '启用' : '停用' }}</el-tag>
            </div>
            <div class="preview-card__item">
              <span>计划名称</span>
              <strong>{{ form.planName || '未填写' }}</strong>
            </div>
            <div class="preview-card__item">
              <span>学期</span>
              <strong>{{ selectedTermLabel }}</strong>
            </div>
            <div class="preview-card__item">
              <span>计划类型</span>
              <strong>{{ planTypeLabel(form.planType) }}</strong>
            </div>
            <div class="preview-card__item">
              <span>生效范围</span>
              <strong>{{ planScopeLabel(planScope) }}</strong>
            </div>
            <div class="preview-card__item">
              <span>已配置窗口</span>
              <strong>{{ configuredWindowCount }} 个</strong>
            </div>
            <div class="preview-card__list">
              <div v-if="showsStandardWindows" class="preview-card__list-item">
                <label>直接选课</label>
                <p>{{ formatRange(selectionRange) }}</p>
              </div>
              <div v-if="showsStandardWindows" class="preview-card__list-item">
                <label>直接退课</label>
                <p>{{ formatRange(dropRange) }}</p>
              </div>
              <div v-if="showsRequestWindow" class="preview-card__list-item">
                <label>审核申请</label>
                <p>{{ formatRange(requestRange) }}</p>
              </div>
            </div>
            <div class="preview-card__hint">
              <p>启用前请确认本学期没有其他启用中的计划，否则后端会阻止保存。</p>
            </div>
          </div>
        </aside>
      </div>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addCourseSelectionPlan, delCourseSelectionPlan, listCourseSelectionPlan, updateCourseSelectionPlan } from '@/api/campus/courseSelectionPlan'
import { fetchTermOptions } from '@/api/campus/options'

type PlanScope = 'ALL' | 'STANDARD' | 'REQUEST'

const planTypeOptions = [
  { label: '常规选课', value: 'GENERAL' },
  { label: '重修选课', value: 'RETAKE' },
  { label: '个性化申请', value: 'PERSONALIZED' },
]

const planScopeOptions: Array<{ label: string; value: PlanScope; desc: string }> = [
  { label: '选课中心 + 选课申请', value: 'ALL', desc: '同时控制标准选课、退课和审核申请。' },
  { label: '仅选课中心', value: 'STANDARD', desc: '只控制标准选课与直接退课。' },
  { label: '仅选课申请', value: 'REQUEST', desc: '只控制个性化选课与退课申请。' },
]

const presetOptions = [
  {
    key: 'general',
    title: '常规选课模板',
    desc: '适合标准选课、退课和审核申请统一开放管理。',
    planType: 'GENERAL',
    scope: 'ALL',
    noticeContent: '请在规定时间内完成标准选课与退课，特殊场景再提交审核申请。',
    ruleContent: '本班必修课默认保留；直接选课、直接退课请在开放时间内办理；超出标准规则请提交选课申请。',
  },
  {
    key: 'retake',
    title: '重修选课模板',
    desc: '适合补修、重修学生统一开放课程调整。',
    planType: 'RETAKE',
    scope: 'STANDARD',
    noticeContent: '本轮主要面向补修、重修学生开放，请按要求选择对应教学班。',
    ruleContent: '请优先通过标准选课完成补修课程选择；跨班协调或容量调整再提交审核申请。',
  },
  {
    key: 'personalized',
    title: '审核申请模板',
    desc: '适合只开放个性化申请、不开放直接办理的场景。',
    planType: 'PERSONALIZED',
    scope: 'REQUEST',
    noticeContent: '本轮仅受理个性化选课与退课申请，审核通过后系统自动处理。',
    ruleContent: '请完整填写申请理由；审核结果会推送到学生端消息中心；未通过可根据说明调整后重新提交。',
  },
]

const formRef = ref<any>()
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const dataList = ref<any[]>([])
const termOptions = ref<any[]>([])
const ids = ref<any[]>([])
const single = ref(true)
const multiple = ref(true)
const open = ref(false)
const title = ref('')
const planNameTouched = ref(false)
const planScope = ref<PlanScope>('ALL')
const selectionRange = ref<string[]>([])
const dropRange = ref<string[]>([])
const requestRange = ref<string[]>([])

const queryParams = reactive<any>({ pageNum: 1, pageSize: 10, termId: undefined, planType: undefined, status: undefined })
const form = reactive<any>({})

const rules: Record<string, any[]> = {
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  termId: [{ required: true, message: '请选择学期', trigger: 'change' }],
  planType: [{ required: true, message: '请选择计划类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择计划状态', trigger: 'change' }],
}

const activePlanCount = computed(() => dataList.value.filter((item: any) => item.status === '0').length)
const requestWindowCount = computed(() => dataList.value.filter((item: any) => item.requestStartTime && item.requestEndTime).length)
const configuredWindowCount = computed(() => {
  const ranges = []
  if (showsStandardWindows.value) {
    ranges.push(selectionRange.value, dropRange.value)
  }
  if (showsRequestWindow.value) {
    ranges.push(requestRange.value)
  }
  return ranges.filter((item: any) => item?.length === 2).length
})
const selectedTermLabel = computed(() => termOptions.value.find((item: any) => item.value === form.termId)?.label || '未选择')
const showsStandardWindows = computed(() => planScope.value === 'ALL' || planScope.value === 'STANDARD')
const showsRequestWindow = computed(() => planScope.value === 'ALL' || planScope.value === 'REQUEST')

function createEmptyForm() {
  return {
    planId: undefined,
    planName: '',
    termId: undefined,
    planType: 'GENERAL',
    selectionStartTime: '',
    selectionEndTime: '',
    dropStartTime: '',
    dropEndTime: '',
    requestStartTime: '',
    requestEndTime: '',
    noticeContent: '',
    ruleContent: '',
    status: '0',
    remark: '',
  }
}

function resetForm() {
  Object.assign(form, createEmptyForm())
  planScope.value = 'ALL'
  selectionRange.value = []
  dropRange.value = []
  requestRange.value = []
  planNameTouched.value = false
}

function fillRangesFromForm() {
  selectionRange.value = form.selectionStartTime && form.selectionEndTime ? [form.selectionStartTime, form.selectionEndTime] : []
  dropRange.value = form.dropStartTime && form.dropEndTime ? [form.dropStartTime, form.dropEndTime] : []
  requestRange.value = form.requestStartTime && form.requestEndTime ? [form.requestStartTime, form.requestEndTime] : []
  planScope.value = resolvePlanScope(form)
}

function syncFormRanges() {
  const selection = showsStandardWindows.value && selectionRange.value?.length === 2 ? selectionRange.value : ['', '']
  const drop = showsStandardWindows.value && dropRange.value?.length === 2 ? dropRange.value : ['', '']
  const request = showsRequestWindow.value && requestRange.value?.length === 2 ? requestRange.value : ['', '']
  ;[form.selectionStartTime, form.selectionEndTime] = selection
  ;[form.dropStartTime, form.dropEndTime] = drop
  ;[form.requestStartTime, form.requestEndTime] = request
}

function planTypeLabel(value?: string) {
  return planTypeOptions.find((item) => item.value === value)?.label || (value || '未分类')
}

function planTypeTagType(value?: string) {
  if (value === 'RETAKE') return 'warning'
  if (value === 'PERSONALIZED') return 'primary'
  return 'success'
}

function planScopeLabel(value?: string) {
  return planScopeOptions.find((item) => item.value === value)?.label || '未区分'
}

function planScopeTagType(value?: string) {
  if (value === 'REQUEST') return 'primary'
  if (value === 'STANDARD') return 'success'
  return 'warning'
}

function resolvePlanScope(source: any): PlanScope {
  const hasSelection = Boolean(source?.selectionStartTime && source?.selectionEndTime)
  const hasDrop = Boolean(source?.dropStartTime && source?.dropEndTime)
  const hasRequest = Boolean(source?.requestStartTime && source?.requestEndTime)
  const hasStandard = hasSelection || hasDrop
  if (hasStandard && hasRequest) return 'ALL'
  if (hasRequest) return 'REQUEST'
  return 'STANDARD'
}

function buildSuggestedName() {
  const termLabel = selectedTermLabel.value === '未选择' ? '' : selectedTermLabel.value
  if (!termLabel) return ''
  return `${termLabel} ${planTypeLabel(form.planType)}计划`
}

function handleTermOrTypeChange() {
  if (!form.planId && !planNameTouched.value) {
    form.planName = buildSuggestedName()
  }
}

function applyPreset(preset: any) {
  form.planType = preset.planType
  planScope.value = preset.scope
  form.noticeContent = preset.noticeContent
  form.ruleContent = preset.ruleContent
  handleTermOrTypeChange()
}

function formatRange(range?: string[]) {
  if (!range || range.length !== 2) return '未配置'
  return `${range[0]} 至 ${range[1]}`
}

function buildWindowSummary(row: any) {
  const items = []
  if (row.selectionStartTime && row.selectionEndTime) items.push('直接选课')
  if (row.dropStartTime && row.dropEndTime) items.push('直接退课')
  if (row.requestStartTime && row.requestEndTime) items.push('审核申请')
  return items.length ? items.join(' / ') : '未配置开放窗口'
}

function setPlanScope(scope: PlanScope) {
  planScope.value = scope
}

async function loadOptions() {
  termOptions.value = await fetchTermOptions()
}

async function getList() {
  loading.value = true
  try {
    const res = await listCourseSelectionPlan(queryParams)
    dataList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(queryParams, { pageNum: 1, pageSize: 10, termId: undefined, planType: undefined, status: undefined })
  getList()
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item: any) => item.planId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  resetForm()
  title.value = '新增选课计划'
  open.value = true
}

function handleUpdate(row?: any) {
  const item = row || dataList.value.find((current: any) => current.planId === ids.value[0])
  if (!item) return
  resetForm()
  Object.assign(form, item)
  fillRangesFromForm()
  planNameTouched.value = true
  title.value = '修改选课计划'
  open.value = true
}

async function submitForm() {
  syncFormRanges()
  if (!configuredWindowCount.value) {
    ElMessage.warning('请至少配置一个开放时间段')
    return
  }
  await formRef.value?.validate()
  if (form.planId) {
    await updateCourseSelectionPlan(form)
    ElMessage.success('修改成功')
  } else {
    await addCourseSelectionPlan(form)
    ElMessage.success('新增成功')
  }
  open.value = false
  await getList()
}

async function handleDelete(row?: any) {
  const target = row?.planId || ids.value
  if (!target || (Array.isArray(target) && !target.length)) return
  await ElMessageBox.confirm('确认删除所选选课计划吗？', '提示', { type: 'warning' })
  await delCourseSelectionPlan(target)
  ElMessage.success('删除成功')
  await getList()
}

watch([selectionRange, dropRange, requestRange], () => {
  syncFormRanges()
})

watch(
  () => open.value,
  (value) => {
    if (value) return
    formRef.value?.clearValidate()
  },
)

onMounted(async () => {
  resetForm()
  await loadOptions()
  await getList()
})
</script>

<style scoped>
.plan-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.plan-overview {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.plan-overview__card {
  padding: 18px 20px;
  border-radius: 18px;
  border: 1px solid #dbe7f5;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.05);
}

.plan-overview__card span {
  display: block;
  color: #64748b;
  font-size: 12px;
  margin-bottom: 8px;
}

.plan-overview__card strong {
  display: block;
  color: #0f172a;
  font-size: 28px;
  line-height: 1.2;
  margin-bottom: 8px;
}

.plan-overview__card p {
  margin: 0;
  color: #64748b;
  font-size: 12px;
  line-height: 1.7;
}

.plan-search,
.mb16 {
  margin-bottom: 16px;
}

.plan-dialog {
  display: grid;
  grid-template-columns: minmax(0, 1.65fr) minmax(280px, 0.85fr);
  gap: 20px;
}

.plan-dialog__main {
  min-width: 0;
}

.plan-section {
  padding: 18px;
  border-radius: 18px;
  border: 1px solid #e5edf7;
  background: #fafcff;
  margin-bottom: 16px;
}

.plan-section__header {
  margin-bottom: 16px;
}

.plan-section__header strong {
  display: block;
  color: #0f172a;
  font-size: 16px;
  margin-bottom: 4px;
}

.plan-section__header span {
  color: #64748b;
  font-size: 12px;
}

.preset-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.scope-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.preset-card {
  text-align: left;
  border: 1px solid #dbe7f5;
  border-radius: 16px;
  padding: 14px;
  background: #ffffff;
  cursor: pointer;
  transition: border-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
}

.preset-card:hover,
.preset-card.is-active {
  border-color: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.08);
}

.preset-card strong {
  display: block;
  color: #0f172a;
  margin-bottom: 6px;
}

.preset-card span {
  color: #64748b;
  font-size: 12px;
  line-height: 1.7;
}

.scope-card {
  text-align: left;
  border: 1px solid #dbe7f5;
  border-radius: 16px;
  padding: 14px;
  background: #ffffff;
  cursor: pointer;
  transition: border-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
}

.scope-card:hover,
.scope-card.is-active {
  border-color: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.08);
}

.scope-card strong {
  display: block;
  color: #0f172a;
  margin-bottom: 6px;
}

.scope-card span {
  color: #64748b;
  font-size: 12px;
  line-height: 1.7;
}

.preview-card {
  position: sticky;
  top: 12px;
  padding: 18px;
  border-radius: 18px;
  border: 1px solid #dbe7f5;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
}

.preview-card__head,
.preview-card__item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.preview-card__head {
  margin-bottom: 18px;
}

.preview-card__head strong,
.preview-card__item strong {
  color: #0f172a;
}

.preview-card__item {
  margin-bottom: 10px;
}

.preview-card__item span,
.preview-card__list-item label {
  color: #64748b;
  font-size: 12px;
}

.preview-card__list {
  display: grid;
  gap: 12px;
  margin-top: 18px;
  padding-top: 18px;
  border-top: 1px solid #edf2f7;
}

.preview-card__list-item p,
.preview-card__hint p {
  margin: 4px 0 0;
  color: #475569;
  font-size: 12px;
  line-height: 1.7;
}

.preview-card__hint {
  margin-top: 18px;
  padding: 12px 14px;
  border-radius: 14px;
  background: #eff6ff;
}

@media (max-width: 1080px) {
  .plan-overview,
  .preset-grid,
  .scope-grid,
  .plan-dialog {
    grid-template-columns: 1fr;
  }
}
</style>
