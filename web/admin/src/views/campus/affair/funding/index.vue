<template>
  <div class="app-container funding-config-page">
    <div class="hero-card mb16">
      <div>
        <p class="hero-card__eyebrow">资助中心</p>
        <h2>资助事务配置</h2>
        <p>集中配置奖学金、助学金、贫困认定、勤工助学的申请范围、学期、申报时段和公告说明。</p>
      </div>
      <div class="hero-card__stats">
        <div class="hero-stat">
          <span>资助模板</span>
          <strong>{{ fundingTemplates.length }}</strong>
        </div>
        <div class="hero-stat is-success">
          <span>开放中</span>
          <strong>{{ openTemplateCount }}</strong>
        </div>
        <div class="hero-stat is-warning">
          <span>自定义范围</span>
          <strong>{{ customScopeCount }}</strong>
        </div>
      </div>
    </div>

    <el-form :model="queryParams" :inline="true" v-show="showSearch" class="mb16">
      <el-form-item label="资助类型">
        <el-select v-model="queryParams.categoryCode" clearable placeholder="请选择资助类型" style="width: 220px">
          <el-option v-for="item in fundingCategories" :key="item.categoryCode" :label="item.categoryName" :value="item.categoryCode" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable placeholder="请选择状态" style="width: 140px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="loadData">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <right-toolbar v-model:showSearch="showSearch" @queryTable="loadData" />

    <div v-if="fundingTemplates.length" class="template-grid">
      <article v-for="item in fundingTemplates" :key="item.templateId" class="template-card" :class="`is-${fundingTone(item.categoryCode)}`">
        <div class="template-card__head">
          <div>
            <span class="template-card__eyebrow">{{ item.categoryName }}</span>
            <h3>{{ item.templateName }}</h3>
          </div>
          <div class="template-card__badges">
            <el-tag :type="item.status === '0' ? 'success' : 'info'">{{ item.status === '0' ? '正常' : '停用' }}</el-tag>
            <el-tag v-if="['SCHOLARSHIP', 'GRANT'].includes(item.categoryCode) && summarizeScope(item.applyScopeJson).mode === 'ALL'" type="danger" effect="plain">范围过宽</el-tag>
            <el-tag v-if="summarizeRules(item.businessRulesJson, item.categoryCode).riskText" type="warning" effect="plain">{{ summarizeRules(item.businessRulesJson, item.categoryCode).riskText }}</el-tag>
          </div>
        </div>

        <div class="template-card__summary">
          <p><span>申请范围</span>{{ summarizeScope(item.applyScopeJson).label }}</p>
          <p><span>对象摘要</span>{{ summarizeScope(item.applyScopeJson).previewText }}</p>
          <p><span>绑定学期</span>{{ summarizeRules(item.businessRulesJson, item.categoryCode).termLabel }}</p>
          <p><span>申报时段</span>{{ summarizeRules(item.businessRulesJson, item.categoryCode).windowLabel }}</p>
          <p><span>前置条件</span>{{ summarizeRules(item.businessRulesJson, item.categoryCode).prerequisiteLabel }}</p>
          <p><span>资助档次</span>{{ summarizeLevelOptions(item.formSchemaJson) }}</p>
          <p><span>负责审核</span>{{ summarizeWorkflow(item.workflowSchemaJson) }}</p>
        </div>

        <div v-if="summarizeLevelTags(item.formSchemaJson).length" class="template-card__levels">
          <el-tag v-for="tag in summarizeLevelTags(item.formSchemaJson)" :key="tag" size="small" effect="light">{{ tag }}</el-tag>
        </div>

        <div class="template-card__scope-detail">
          <el-tag
            v-for="tag in summarizeScope(item.applyScopeJson).tags"
            :key="tag"
            size="small"
            effect="plain"
          >
            {{ tag }}
          </el-tag>
          <span v-if="!summarizeScope(item.applyScopeJson).tags.length" class="scope-empty">未限制到具体组织，可见即符合角色的学生可申请</span>
        </div>

        <div class="template-card__notice">
          <span>公告说明</span>
          <p>{{ summarizeRules(item.businessRulesJson, item.categoryCode).notice || item.remark || '暂无公告说明' }}</p>
        </div>

        <div class="template-card__footer">
          <el-button type="primary" plain @click="handleEdit(item)">快捷配置</el-button>
          <el-button link type="info" @click="openTemplateEditor(item)">模板编辑器</el-button>
        </div>
      </article>
    </div>
    <el-empty v-else description="暂无资助事务模板" />

    <el-dialog v-model="open" :title="title" width="860px" append-to-body :close-on-click-modal="false">
      <div v-if="currentTemplate" class="edit-shell">
        <section class="edit-card">
          <div class="edit-card__head">
            <div>
              <h3>基础信息</h3>
              <p>{{ currentTemplate.categoryName }} · {{ currentTemplate.templateName }}</p>
            </div>
            <el-tag effect="plain">{{ currentTemplate.categoryName }}</el-tag>
          </div>
          <el-form label-width="110px">
            <el-form-item label="模板状态">
              <el-radio-group v-model="currentTemplate.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="公告说明">
              <el-input v-model="ruleForm.notice" type="textarea" :rows="3" placeholder="请输入资助事务公告说明" />
            </el-form-item>
          </el-form>
        </section>

        <section v-if="levelOptionList.length" class="edit-card">
          <div class="edit-card__head">
            <div>
              <h3>资助档次</h3>
              <p>这里配置老师和学生都能看到的资助档次/认定档次，不用再进表单设计器修改。</p>
            </div>
            <el-button type="primary" plain size="small" @click="addLevelOption">新增档次</el-button>
          </div>
          <div class="level-editor">
            <div v-for="(item, index) in levelOptionList" :key="`${item.value}-${index}`" class="level-row">
              <el-input v-model="item.label" placeholder="显示名称，如 国家奖学金 / 特别困难" />
              <el-input v-model="item.value" placeholder="编码值，如 NATIONAL / LEVEL_A" />
              <el-button link type="danger" @click="removeLevelOption(index)">删除</el-button>
            </div>
          </div>
        </section>

        <section class="edit-card">
          <div class="edit-card__head">
            <div>
              <h3>申请范围</h3>
              <p>控制哪些学生可以发起该资助业务。</p>
            </div>
          </div>
          <el-form label-width="110px">
            <el-form-item label="范围模式">
              <el-radio-group v-model="scopeMode">
                <el-radio value="ALL">全部符合角色学生</el-radio>
                <el-radio value="CUSTOM">自定义学生范围</el-radio>
              </el-radio-group>
            </el-form-item>
            <template v-if="scopeMode === 'CUSTOM'">
              <el-form-item label="院系/部门">
                <el-select v-model="scopeForm.deptIds" multiple filterable collapse-tags style="width: 100%">
                  <el-option v-for="item in deptOptions" :key="item.deptId || item.value" :label="item.deptName || item.label" :value="item.deptId || item.value" />
                </el-select>
              </el-form-item>
              <el-form-item label="年级">
                <el-select v-model="scopeForm.gradeIds" multiple filterable collapse-tags style="width: 100%">
                  <el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
              <el-form-item label="班级">
                <el-select v-model="scopeForm.classIds" multiple filterable collapse-tags style="width: 100%">
                  <el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
              <el-form-item label="指定学生">
                <el-select
                  v-model="scopeForm.userIds"
                  multiple
                  filterable
                  remote
                  :remote-method="searchStudents"
                  :loading="studentSearchLoading"
                  collapse-tags
                  collapse-tags-tooltip
                  placeholder="输入姓名或学号搜索"
                  style="width: 100%"
                >
                  <el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
                <div style="margin-top: 8px; display: flex; gap: 8px;">
                  <el-button size="small" @click="batchDialogVisible = true">批量导入学生</el-button>
                  <span v-if="scopeForm.userIds.length" style="font-size: 12px; color: #94a3b8; line-height: 24px;">已选 {{ scopeForm.userIds.length }} 人</span>
                </div>
              </el-form-item>
            </template>
          </el-form>
        </section>

        <section class="edit-card">
          <div class="edit-card__head">
            <div>
              <h3>业务规则</h3>
              <p>控制绑定学期、申报时段、前置条件和重复提交限制。</p>
            </div>
          </div>
          <el-form label-width="110px">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="绑定学期">
                  <el-select v-model="ruleForm.termId" clearable filterable style="width: 100%">
                    <el-option v-for="item in termOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="待审拦截">
                  <el-switch v-model="ruleForm.requireNoPending" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="申报时段">
                  <el-switch v-model="ruleForm.openWindowEnabled" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="岗位前置">
                  <el-switch v-model="ruleForm.requirePublishedJob" />
                </el-form-item>
              </el-col>
              <template v-if="ruleForm.openWindowEnabled">
                <el-col :span="12">
                  <el-form-item label="时段开始">
                    <el-date-picker v-model="ruleForm.openStartTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择申报开始时间" style="width: 100%" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="时段结束">
                    <el-date-picker v-model="ruleForm.openEndTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择申报截止时间" style="width: 100%" />
                  </el-form-item>
                </el-col>
              </template>
              <el-col :span="24">
                <el-form-item label="前置分类">
                  <el-select v-model="ruleForm.prerequisiteCategoryCodes" multiple filterable collapse-tags style="width: 100%">
                    <el-option v-for="item in categoryOptions" :key="item.categoryCode" :label="item.categoryName" :value="item.categoryCode" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <div class="rule-preview">
                  <span>当前生效说明</span>
                  <p>{{ buildLiveRulePreview() }}</p>
                </div>
              </el-col>
            </el-row>
          </el-form>
        </section>
      </div>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存配置</el-button>
      </template>
    </el-dialog>

    <!-- ── Batch import dialog ── -->
    <el-dialog v-model="batchDialogVisible" title="批量导入学生" width="680px" append-to-body :close-on-click-modal="false">
      <el-tabs v-model="batchImportTab">
        <el-tab-pane label="按班级导入" name="byClass">
          <el-select
            v-model="batchClassIds"
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            placeholder="选择班级"
            style="width: 100%; margin-bottom: 12px;"
          >
            <el-option v-for="item in classOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-button type="primary" :disabled="!batchClassIds.length" @click="importByClass">添加所选班级全部学生</el-button>
        </el-tab-pane>
        <el-tab-pane label="按年级导入" name="byGrade">
          <el-select
            v-model="batchGradeIds"
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            placeholder="选择年级"
            style="width: 100%; margin-bottom: 12px;"
          >
            <el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-button type="primary" :disabled="!batchGradeIds.length" @click="importByGrade">添加所选年级全部学生</el-button>
        </el-tab-pane>
        <el-tab-pane label="粘贴学号" name="byPaste">
          <el-input
            v-model="pastedStudentIds"
            type="textarea"
            :rows="6"
            placeholder="粘贴学号，用逗号、空格或换行分隔"
            style="margin-bottom: 12px;"
          />
          <el-button type="primary" :disabled="!pastedStudentIds.trim()" @click="importByPaste">解析并添加</el-button>
        </el-tab-pane>
      </el-tabs>

      <div v-if="selectedStudentRows.length" style="margin-top: 16px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
          <span style="font-size: 13px; color: #334155;">已选学生（{{ selectedStudentRows.length }} 人）</span>
          <el-button type="danger" link size="small" @click="clearAllSelectedStudents">全部清除</el-button>
        </div>
        <el-table :data="selectedStudentRows" max-height="260" border size="small">
          <el-table-column prop="value" label="学号" width="140" />
          <el-table-column prop="label" label="姓名" />
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row }">
              <el-button type="danger" link size="small" @click="removeSelectedStudent(row.value)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <el-button @click="batchDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAffairTemplate, listAffairCategory, listAffairTemplate, updateAffairTemplate } from '@/api/campus/affair'
import { fetchClassOptions, fetchGradeOptions, fetchTermOptions, fetchUserOptions } from '@/api/campus/options'
import { listDept } from '@/api/system/dept'

const router = useRouter()
const showSearch = ref(true)
const open = ref(false)
const title = ref('')
const submitting = ref(false)
const categoryOptions = ref<any[]>([])
const fundingCategories = ref<any[]>([])
const templateList = ref<any[]>([])
const termOptions = ref<any[]>([])
const deptOptions = ref<any[]>([])
const gradeOptions = ref<any[]>([])
const classOptions = ref<any[]>([])
const studentOptions = ref<any[]>([])
const currentTemplate = ref<any>(null)
const levelOptionList = ref<Array<{ label: string; value: string }>>([])

// ── Batch import state ──
const studentSearchLoading = ref(false)
const batchDialogVisible = ref(false)
const batchImportTab = ref('byClass')
const batchClassIds = ref<any[]>([])
const batchGradeIds = ref<any[]>([])
const pastedStudentIds = ref('')

const queryParams = reactive({
  categoryCode: '',
  status: '',
})

const scopeMode = ref<'ALL' | 'CUSTOM'>('ALL')
const scopeForm = reactive<any>({
  deptIds: [],
  gradeIds: [],
  classIds: [],
  userIds: [],
})
const ruleForm = reactive<any>({
  termId: undefined,
  openWindowEnabled: false,
  openStartTime: '',
  openEndTime: '',
  requireNoPending: true,
  prerequisiteCategoryCodes: [],
  prerequisiteTemplateCodes: [],
  requirePublishedJob: false,
  notice: '',
})

const fundingTemplates = computed(() => {
  const allowed = new Set(['SCHOLARSHIP', 'GRANT', 'POVERTY_IDENTIFICATION', 'WORK_STUDY'])
  return templateList.value.filter((item: any) => allowed.has(item.categoryCode))
})
const openTemplateCount = computed(() => fundingTemplates.value.filter((item: any) => summarizeRules(item.businessRulesJson).open).length)
const customScopeCount = computed(() => fundingTemplates.value.filter((item: any) => summarizeScope(item.applyScopeJson).mode === 'CUSTOM').length)

const categoryNameMap = computed(() => Object.fromEntries(categoryOptions.value.map((item: any) => [item.categoryCode, item.categoryName])))
const deptNameMap = computed(() => Object.fromEntries((deptOptions.value || []).map((item: any) => [String(item.deptId || item.value), item.deptName || item.label])))
const gradeNameMap = computed(() => Object.fromEntries((gradeOptions.value || []).map((item: any) => [String(item.value), item.label])))
const classNameMap = computed(() => Object.fromEntries((classOptions.value || []).map((item: any) => [String(item.value), item.label])))
const studentNameMap = computed(() => Object.fromEntries((studentOptions.value || []).map((item: any) => [String(item.value), item.shortLabel || item.label])))

// ── Selected students table rows (derived from scopeForm.userIds + studentOptions) ──
const selectedStudentRows = computed(() => {
  const allMap = new Map((studentOptions.value || []).map((item: any) => [String(item.value), item]))
  return (scopeForm.userIds || []).map((id: any) => {
    const match = allMap.get(String(id))
    return match ? { value: match.value, label: match.label || match.shortLabel || id } : { value: id, label: id }
  })
})

// ── Remote search for students ──
let searchTimer: ReturnType<typeof setTimeout> | null = null
function searchStudents(query: string) {
  if (searchTimer) clearTimeout(searchTimer)
  if (!query) return
  studentSearchLoading.value = true
  searchTimer = setTimeout(async () => {
    try {
      const res = await fetchUserOptions('student', query)
      // Merge new results into existing options so already-selected items remain visible
      const existingIds = new Set(studentOptions.value.map((item: any) => String(item.value)))
      const newItems = (res || []).filter((item: any) => !existingIds.has(String(item.value)))
      studentOptions.value = [...studentOptions.value, ...newItems]
    } finally {
      studentSearchLoading.value = false
    }
  }, 300)
}

// ── Batch import: by class ──
async function importByClass() {
  if (!batchClassIds.value.length) return
  try {
    const res = await fetchUserOptions('student', '', { classIds: batchClassIds.value })
    const newStudents: any[] = res || []
    mergeStudentsIntoScope(newStudents)
    ElMessage.success(`已添加 ${newStudents.length} 名班级学生`)
    batchClassIds.value = []
  } catch {
    ElMessage.error('按班级导入学生失败')
  }
}

// ── Batch import: by grade ──
async function importByGrade() {
  if (!batchGradeIds.value.length) return
  try {
    const res = await fetchUserOptions('student', '', { gradeIds: batchGradeIds.value })
    const newStudents: any[] = res || []
    mergeStudentsIntoScope(newStudents)
    ElMessage.success(`已添加 ${newStudents.length} 名年级学生`)
    batchGradeIds.value = []
  } catch {
    ElMessage.error('按年级导入学生失败')
  }
}

// ── Batch import: by pasting student IDs ──
async function importByPaste() {
  const raw = pastedStudentIds.value.trim()
  if (!raw) return
  const ids = raw.split(/[\s,;\n]+/).map((s: string) => s.trim()).filter(Boolean)
  if (!ids.length) {
    ElMessage.warning('未解析到有效学号')
    return
  }
  // Deduplicate against existing
  const existingSet = new Set((scopeForm.userIds || []).map(String))
  const newIds = ids.filter((id) => !existingSet.has(id))
  if (!newIds.length) {
    ElMessage.info('所有学号已存在，无需重复添加')
    return
  }
  // Try to resolve names
  try {
    const res = await fetchUserOptions('student', '', { userIds: newIds })
    const resolved: any[] = res || []
    mergeStudentsIntoScope(resolved)
    // For IDs not resolved, add them as raw IDs
    const resolvedSet = new Set(resolved.map((item: any) => String(item.value)))
    const unresolved = newIds.filter((id) => !resolvedSet.has(id))
    if (unresolved.length) {
      scopeForm.userIds.push(...unresolved)
    }
  } catch {
    // Fallback: just add the raw IDs
    scopeForm.userIds.push(...newIds)
  }
  ElMessage.success(`已解析并添加 ${newIds.length} 个学号`)
  pastedStudentIds.value = ''
}

// ── Merge fetched student items into options and scopeForm.userIds ──
function mergeStudentsIntoScope(items: any[]) {
  const existingOptionIds = new Set(studentOptions.value.map((item: any) => String(item.value)))
  const existingUserIds = new Set((scopeForm.userIds || []).map(String))
  for (const item of items) {
    if (!existingOptionIds.has(String(item.value))) {
      studentOptions.value.push(item)
      existingOptionIds.add(String(item.value))
    }
    if (!existingUserIds.has(String(item.value))) {
      scopeForm.userIds.push(item.value)
      existingUserIds.add(String(item.value))
    }
  }
}

// ── Remove / clear selected students ──
function removeSelectedStudent(id: any) {
  scopeForm.userIds = scopeForm.userIds.filter((uid: any) => String(uid) !== String(id))
}

function clearAllSelectedStudents() {
  scopeForm.userIds = []
}

function parseJsonObject(value?: string) {
  try {
    return JSON.parse(value || '{}')
  } catch {
    return {}
  }
}

function summarizeScope(value?: string) {
  const scope = parseJsonObject(value)
  const tags: string[] = []
  const deptIds = Array.isArray(scope.deptIds) ? scope.deptIds : []
  const gradeIds = Array.isArray(scope.gradeIds) ? scope.gradeIds : []
  const classIds = Array.isArray(scope.classIds) ? scope.classIds : []
  const userIds = Array.isArray(scope.userIds) ? scope.userIds : []
  if (deptIds.length) tags.push(`院系 ${deptIds.length} 个`)
  if (gradeIds.length) tags.push(`年级 ${gradeIds.length} 个`)
  if (classIds.length) tags.push(`班级 ${classIds.length} 个`)
  if (userIds.length) tags.push(`指定学生 ${userIds.length} 人`)
  const previewParts = [
    buildPreviewText(deptIds, deptNameMap.value, '院系'),
    buildPreviewText(gradeIds, gradeNameMap.value, '年级'),
    buildPreviewText(classIds, classNameMap.value, '班级'),
    buildPreviewText(userIds, studentNameMap.value, '学生'),
  ].filter(Boolean)
  return {
    mode: tags.length ? 'CUSTOM' : 'ALL',
    label: tags.length ? `自定义范围（覆盖 ${tags.join(' / ')}）` : '全部符合角色学生',
    tags,
    previewText: previewParts.length ? previewParts.join('；') : '未限定院系/年级/班级/具体学生',
  }
}

function summarizeRules(value?: string, categoryCode = '') {
  const rules = parseJsonObject(value)
  const prerequisiteLabel = Array.isArray(rules.prerequisiteCategoryCodes) && rules.prerequisiteCategoryCodes.length
    ? rules.prerequisiteCategoryCodes.map((code: string) => categoryNameMap.value[code] || code).join(' / ')
    : '无前置条件'
  const windowLabel = rules.openWindowEnabled
    ? `${rules.openStartTime || '未设置'} ~ ${rules.openEndTime || '未设置'}`
    : '长期开放'
  let riskText = ''
  if ((categoryCode === 'SCHOLARSHIP' || categoryCode === 'GRANT') && !rules.openWindowEnabled) {
    riskText = '未设时段'
  }
  if (categoryCode === 'GRANT' && !(Array.isArray(rules.prerequisiteCategoryCodes) && rules.prerequisiteCategoryCodes.includes('POVERTY_IDENTIFICATION'))) {
    riskText = '缺认定前置'
  }
  if (categoryCode === 'WORK_STUDY' && !rules.requirePublishedJob) {
    riskText = '未校验岗位'
  }
  return {
    termLabel: rules.termId ? `学期 ${rules.termId}` : '未绑定学期',
    windowLabel,
    prerequisiteLabel,
    notice: rules.notice || '',
    open: !rules.openWindowEnabled || isWindowOpen(rules),
    riskText,
  }
}

function summarizeLevelOptions(formSchemaJson?: string) {
  const fields = Array.isArray(parseJsonObjectArray(formSchemaJson)) ? parseJsonObjectArray(formSchemaJson) : []
  const targetField = fields.find((item: any) => ['scholarshipType', 'grantType', 'hardshipLevel'].includes(item.field))
  if (!targetField) return '未配置档次选项'
  const options = Array.isArray(targetField.options) ? targetField.options : []
  if (!options.length) return '未配置档次选项'
  return options.map((item: any) => item.label).join(' / ')
}

function summarizeLevelTags(formSchemaJson?: string) {
  const fields = Array.isArray(parseJsonObjectArray(formSchemaJson)) ? parseJsonObjectArray(formSchemaJson) : []
  const targetField = fields.find((item: any) => ['scholarshipType', 'grantType', 'hardshipLevel'].includes(item.field))
  return Array.isArray(targetField?.options) ? targetField.options.map((item: any) => item.label) : []
}

function summarizeWorkflow(workflowSchemaJson?: string) {
  const steps = Array.isArray(parseJsonObjectArray(workflowSchemaJson)) ? parseJsonObjectArray(workflowSchemaJson) : []
  if (!steps.length) return '未配置审核链路'
  const roleMap: Record<string, string> = { advisor: '辅导员', teacher: '教师', admin: '管理员', department_head: '系主任', dean: '院长' }
  const assignMap: Record<string, string> = { auto: '自动分配', manual: '手动指定', role: '按角色', fixed: '固定人员' }
  return steps.map((item: any) => {
    if (item.stepName) return item.stepName
    const roleName = roleMap[item.reviewerRole] || item.reviewerRole
    const assignName = assignMap[item.assignmentType] || item.assignmentType
    return roleName || assignName || '审核节点'
  }).join(' → ')
}

function parseJsonObjectArray(value?: string) {
  try {
    return JSON.parse(value || '[]')
  } catch {
    return []
  }
}

function isWindowOpen(rules: any) {
  const now = Date.now()
  const start = rules.openStartTime ? new Date(rules.openStartTime).getTime() : 0
  const end = rules.openEndTime ? new Date(rules.openEndTime).getTime() : Number.MAX_SAFE_INTEGER
  return now >= start && now <= end
}

function resetForms() {
  scopeMode.value = 'ALL'
  levelOptionList.value = []
  Object.assign(scopeForm, { deptIds: [], gradeIds: [], classIds: [], userIds: [] })
  Object.assign(ruleForm, {
    termId: undefined,
    openWindowEnabled: false,
    openStartTime: '',
    openEndTime: '',
    requireNoPending: true,
    prerequisiteCategoryCodes: [],
    prerequisiteTemplateCodes: [],
    requirePublishedJob: false,
    notice: '',
  })
}

function hydrateForms(template: any) {
  resetForms()
  const scope = parseJsonObject(template.applyScopeJson)
  const rules = parseJsonObject(template.businessRulesJson)
  const hasCustom = scope.scopeType === 'CUSTOM' || ['deptIds', 'gradeIds', 'classIds', 'userIds'].some((key) => Array.isArray(scope[key]) && scope[key].length)
  scopeMode.value = hasCustom ? 'CUSTOM' : 'ALL'
  scopeForm.deptIds = Array.isArray(scope.deptIds) ? scope.deptIds : []
  scopeForm.gradeIds = Array.isArray(scope.gradeIds) ? scope.gradeIds : []
  scopeForm.classIds = Array.isArray(scope.classIds) ? scope.classIds : []
  scopeForm.userIds = Array.isArray(scope.userIds) ? scope.userIds : []
  ruleForm.termId = rules.termId ?? undefined
  ruleForm.openWindowEnabled = !!rules.openWindowEnabled
  ruleForm.openStartTime = rules.openStartTime || ''
  ruleForm.openEndTime = rules.openEndTime || ''
  ruleForm.requireNoPending = rules.requireNoPending !== false
  ruleForm.prerequisiteCategoryCodes = Array.isArray(rules.prerequisiteCategoryCodes) ? rules.prerequisiteCategoryCodes : []
  ruleForm.prerequisiteTemplateCodes = Array.isArray(rules.prerequisiteTemplateCodes) ? rules.prerequisiteTemplateCodes : []
  ruleForm.requirePublishedJob = !!rules.requirePublishedJob
  ruleForm.notice = rules.notice || ''
  const levelField = resolveLevelField(template)
  levelOptionList.value = levelField?.options?.map((item: any) => ({
    label: item.label || '',
    value: item.value || '',
  })) || []
}

function buildScopePayload() {
  if (scopeMode.value === 'ALL') {
    return { scopeType: 'ALL', deptIds: [], gradeIds: [], classIds: [], userIds: [] }
  }
  return {
    scopeType: 'CUSTOM',
    deptIds: scopeForm.deptIds || [],
    gradeIds: scopeForm.gradeIds || [],
    classIds: scopeForm.classIds || [],
    userIds: scopeForm.userIds || [],
  }
}

function buildRulesPayload() {
  return {
    termId: ruleForm.termId || null,
    openWindowEnabled: !!ruleForm.openWindowEnabled,
    openStartTime: ruleForm.openStartTime || '',
    openEndTime: ruleForm.openEndTime || '',
    requireNoPending: ruleForm.requireNoPending !== false,
    prerequisiteCategoryCodes: ruleForm.prerequisiteCategoryCodes || [],
    prerequisiteTemplateCodes: ruleForm.prerequisiteTemplateCodes || [],
    requirePublishedJob: !!ruleForm.requirePublishedJob,
    notice: ruleForm.notice || '',
  }
}

async function loadOptions() {
  const [categoryRes, termRes, studentRes, deptRes, gradeRes, classRes] = await Promise.all([
    listAffairCategory({ pageNum: 1, pageSize: 200 }),
    fetchTermOptions(),
    fetchUserOptions('student'),
    listDept(),
    fetchGradeOptions(),
    fetchClassOptions(),
  ])
  categoryOptions.value = categoryRes.rows || []
  fundingCategories.value = (categoryRes.rows || []).filter((item: any) => ['SCHOLARSHIP', 'GRANT', 'POVERTY_IDENTIFICATION', 'WORK_STUDY'].includes(item.categoryCode))
  termOptions.value = termRes
  studentOptions.value = studentRes
  deptOptions.value = Array.isArray(deptRes.data) ? deptRes.data : []
  gradeOptions.value = gradeRes
  classOptions.value = classRes
}

async function loadData() {
  const res = await listAffairTemplate({ pageNum: 1, pageSize: 300, categoryCode: queryParams.categoryCode, status: queryParams.status })
  templateList.value = res.rows || []
}

function resetQuery() {
  queryParams.categoryCode = ''
  queryParams.status = ''
  loadData()
}

async function handleEdit(row: any) {
  const res = await getAffairTemplate(row.templateId)
  currentTemplate.value = res.data || res
  hydrateForms(currentTemplate.value)
  title.value = `资助配置 - ${currentTemplate.value.templateName}`
  open.value = true
}

function openTemplateEditor(row: any) {
  router.push('/campus/affair/template')
}

function buildLiveRulePreview() {
  const categoryLabel = currentTemplate.value?.categoryName || '当前资助事务'
  const scopeSummary = scopeMode.value === 'ALL'
    ? '全部符合角色的学生'
    : `限定在 ${[
        scopeForm.deptIds.length ? `院系 ${scopeForm.deptIds.length} 个` : '',
        scopeForm.gradeIds.length ? `年级 ${scopeForm.gradeIds.length} 个` : '',
        scopeForm.classIds.length ? `班级 ${scopeForm.classIds.length} 个` : '',
        scopeForm.userIds.length ? `指定学生 ${scopeForm.userIds.length} 人` : '',
      ].filter(Boolean).join(' / ')}`
  const prerequisiteSummary = (ruleForm.prerequisiteCategoryCodes || []).length
    ? `需先通过 ${(ruleForm.prerequisiteCategoryCodes || []).map((code: string) => categoryNameMap.value[code] || code).join(' / ')}`
    : '无前置条件'
  const windowSummary = ruleForm.openWindowEnabled
    ? `申报时段为 ${ruleForm.openStartTime || '未设置'} 至 ${ruleForm.openEndTime || '未设置'}`
    : '长期开放'
  return `${categoryLabel}当前对${scopeSummary}开放，${windowSummary}，${prerequisiteSummary}。`
}

function resolveLevelField(template: any) {
  const fields = parseJsonObjectArray(template?.formSchemaJson)
  return fields.find((item: any) => ['scholarshipType', 'grantType', 'hardshipLevel'].includes(item.field)) || null
}

function syncLevelOptionsToTemplate(template: any) {
  const fields = parseJsonObjectArray(template?.formSchemaJson)
  const targetIndex = fields.findIndex((item: any) => ['scholarshipType', 'grantType', 'hardshipLevel'].includes(item.field))
  if (targetIndex >= 0) {
    fields[targetIndex] = {
      ...fields[targetIndex],
      options: levelOptionList.value.filter((item) => item.label && item.value),
    }
    return JSON.stringify(fields)
  }
  return template?.formSchemaJson || '[]'
}

function addLevelOption() {
  levelOptionList.value.push({ label: '', value: '' })
}

function removeLevelOption(index: number) {
  levelOptionList.value.splice(index, 1)
}

function fundingTone(categoryCode: string) {
  if (categoryCode === 'SCHOLARSHIP') return 'scholarship'
  if (categoryCode === 'GRANT') return 'grant'
  if (categoryCode === 'POVERTY_IDENTIFICATION') return 'poverty'
  if (categoryCode === 'WORK_STUDY') return 'work-study'
  return 'default'
}

function buildPreviewText(ids: any[], labelMap: Record<string, string>, prefix: string) {
  if (!ids.length) return ''
  const names = ids.map((id) => labelMap[String(id)]).filter(Boolean)
  const preview = names.slice(0, 2).join(' / ')
  const suffix = names.length > 2 ? ` 等 ${names.length} 项` : ''
  return `${prefix}：${preview || ids.length + ' 项'}${suffix}`
}

async function submitForm() {
  if (!currentTemplate.value) return
  if (ruleForm.openWindowEnabled && (!ruleForm.openStartTime || !ruleForm.openEndTime)) {
    ElMessage.warning('启用申报时段后，请同时配置开始和结束时间')
    return
  }
  const payload = {
    ...currentTemplate.value,
    formSchemaJson: syncLevelOptionsToTemplate(currentTemplate.value),
    applyScopeJson: JSON.stringify(buildScopePayload()),
    businessRulesJson: JSON.stringify(buildRulesPayload()),
  }
  submitting.value = true
  try {
    await updateAffairTemplate(payload)
    ElMessage.success('资助配置已更新')
    open.value = false
    await loadData()
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await loadOptions()
  await loadData()
})
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.funding-config-page { display: grid; gap: 16px; }

/* ── Hero: compact with inline stats ── */
.hero-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  padding: 20px 24px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #dbeafe;
  border-left: 4px solid var(--el-color-primary);
}

.hero-card__eyebrow {
  margin: 0 0 4px;
  color: #1d4ed8;
  font-size: 12px;
  letter-spacing: .12em;
  text-transform: uppercase;
}

.hero-card h2 { margin: 0 0 4px; font-size: 20px; color: #0f172a; }
.hero-card > div:first-child > p { margin: 0; color: #64748b; line-height: 1.6; font-size: 13px; }

.hero-card__stats {
  display: flex;
  gap: 0;
  flex-shrink: 0;
}

.hero-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 24px;
  border-right: 1px solid rgba(0, 0, 0, 0.06);
  background: transparent;
  border-radius: 0;
}
.hero-stat:last-child { border-right: none; padding-right: 0; }
.hero-stat:first-child { padding-left: 0; }

.hero-stat span { display: block; color: #64748b; font-size: 12px; white-space: nowrap; }
.hero-stat strong { display: block; margin-top: 4px; color: #0f172a; font-size: 24px; line-height: 1.2; }
.hero-stat.is-success strong { color: #059669; }
.hero-stat.is-warning strong { color: #d97706; }

/* ── Template grid: tighter cards ── */
.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 14px;
}

.template-card {
  padding: 16px 18px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #e5eef8;
  display: grid;
  gap: 10px;
}

.template-card.is-scholarship { border-color: #fde68a; background: #fff; border-left: 4px solid #eab308; }
.template-card.is-grant { border-color: #bfdbfe; background: #fff; border-left: 4px solid #3b82f6; }
.template-card.is-poverty { border-color: #fecaca; background: #fff; border-left: 4px solid #ef4444; }
.template-card.is-work-study { border-color: #bbf7d0; background: #fff; border-left: 4px solid #22c55e; }

.template-card__head {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: start;
}

.template-card__badges {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.template-card__eyebrow {
  display: block;
  color: #0f766e;
  font-size: 11px;
  letter-spacing: .08em;
  text-transform: uppercase;
  margin-bottom: 4px;
}

.template-card__head h3 { margin: 0; color: #0f172a; font-size: 15px; }

/* Compact summary: 3-4 key lines only */
.template-card__summary p {
  margin: 0;
  padding: 3px 0;
  color: #667085;
  font-size: 13px;
  line-height: 1.6;
  border-bottom: 1px dashed #f1f5f9;
}
.template-card__summary p:last-child { border-bottom: none; }

.template-card__summary span {
  display: inline-block;
  min-width: 64px;
  color: #94a3b8;
  font-size: 12px;
}

/* Notice: compact */
.template-card__notice {
  padding: 8px 12px;
  border-radius: 8px;
  background: #f8fafc;
}
.template-card__notice span {
  display: block;
  color: #94a3b8;
  font-size: 11px;
  margin-bottom: 2px;
}
.template-card__notice p {
  margin: 0;
  color: #667085;
  font-size: 13px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.template-card__scope-detail {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.template-card__levels {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.scope-empty {
  color: #94a3b8;
  font-size: 12px;
  line-height: 1.7;
}

.template-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  padding-top: 8px;
  border-top: 1px solid #f1f5f9;
}

/* ── Edit dialog ── */
.edit-shell {
  display: grid;
  gap: 14px;
}

.edit-card {
  padding: 16px;
  border-radius: 10px;
  border: 1px solid #e5eef8;
  background: #fff;
}

.edit-card__head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: start;
  margin-bottom: 12px;
}

.edit-card__head h3 { margin: 0; font-size: 15px; color: #0f172a; }
.edit-card__head p { margin: 4px 0 0; color: #667085; font-size: 13px; }

.level-editor {
  display: grid;
  gap: 8px;
}

.level-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(160px, 0.8fr) auto;
  gap: 8px;
  align-items: center;
}

.rule-preview {
  margin-top: 4px;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.rule-preview span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.rule-preview p {
  margin: 6px 0 0;
  color: #334155;
  font-size: 13px;
  line-height: 1.6;
}

/* ── Responsive ── */
@media (max-width: 1080px) {
  .hero-card {
    flex-direction: column;
    align-items: stretch;
  }
  .hero-card__stats {
    justify-content: space-around;
    padding-top: 14px;
    border-top: 1px solid rgba(0, 0, 0, 0.06);
  }
}

@media (max-width: 760px) {
  .hero-card__stats {
    flex-wrap: wrap;
    gap: 12px;
  }
  .hero-stat {
    border-right: none;
    padding: 0;
  }
  .level-row {
    grid-template-columns: 1fr;
  }
}
</style>
