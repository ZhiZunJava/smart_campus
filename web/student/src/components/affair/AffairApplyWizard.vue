<template>
  <div class="apply-wizard">
    <!-- Header bar -->
    <div class="wizard-header">
      <div class="header-left">
        <el-button text bg size="small" class="back-btn" @click="$emit('back')">
          <i class="ri-arrow-left-line" /> 返回
        </el-button>
        <div class="header-divider" />
        <div class="header-info">
          <h2 class="wizard-title">{{ template?.templateName || '发起事务申请' }}</h2>
          <span v-if="template?.businessName" class="wizard-subtitle">{{ template.businessName }}</span>
        </div>
      </div>
      <div v-if="currentStep === 0" class="header-progress">
        <span class="progress-text">已填 {{ filledCount }} / {{ totalFieldCount }} 项</span>
        <div class="progress-bar">
          <div class="progress-bar__fill" :style="{ width: progressPercent + '%' }" />
        </div>
      </div>
    </div>

    <!-- Steps -->
    <div class="steps-wrapper">
      <div class="steps-track">
        <div v-for="(s, i) in stepList" :key="i" class="step-node" :class="{ 'is-active': currentStep === i, 'is-done': currentStep > i }">
          <div class="step-node__circle">
            <i v-if="currentStep > i" class="ri-check-line" />
            <span v-else>{{ i + 1 }}</span>
          </div>
          <span class="step-node__label">{{ s }}</span>
        </div>
        <div class="steps-connector">
          <div class="steps-connector__fill" :style="{ width: stepsProgressWidth }" />
        </div>
      </div>
    </div>

    <!-- Step panels with transition -->
    <transition :name="stepDirection" mode="out-in">
      <!-- Step 0: 填写表单 -->
      <section v-if="currentStep === 0" key="form" class="wizard-panel">
        <div class="form-card">
          <div class="form-card__head">
            <div>
              <h3>填写申请信息</h3>
              <p>带 <span class="req-star">*</span> 的为必填项，请认真核对后进入下一步</p>
            </div>
            <el-tag v-if="requiredFieldCount > 0" size="small" effect="plain" round>
              {{ requiredFieldCount }} 项必填
            </el-tag>
          </div>

          <el-form ref="formRef" :model="formState" label-width="120px" class="wizard-form" scroll-to-error>
            <template v-for="group in groupedFormFields" :key="group.title">
              <div v-if="group.title" class="form-group-divider">
                <div class="form-group-bar" />
                <h4 class="form-group-title">{{ group.title }}</h4>
              </div>
              <template v-for="field in group.fields" :key="field.field">
                <el-form-item v-show="isFieldVisible(field)" :label="field.label" :required="field.required"
                  :prop="field.field" :rules="buildRules(field)" class="wizard-field">
                  <!-- input -->
                  <el-input v-if="field.component === 'input'" v-model="formState[field.field]"
                    :placeholder="field.placeholder || `请输入${field.label}`" :disabled="!!field.readOnly"
                    :maxlength="field.maxLength" :show-word-limit="!!field.maxLength" clearable />
                  <!-- textarea -->
                  <el-input v-else-if="field.component === 'textarea'" v-model="formState[field.field]"
                    type="textarea" :rows="4" :placeholder="field.placeholder || `请输入${field.label}`"
                    :disabled="!!field.readOnly" :maxlength="field.maxLength || 500" show-word-limit />
                  <!-- tree-select (dept etc.) -->
                  <el-tree-select v-else-if="field.component === 'tree-select' || field.optionSource === 'dept'"
                    v-model="formState[field.field]" :data="resolveOptions(field)"
                    :props="{ label: 'label', value: 'value', children: 'children' }"
                    filterable clearable check-strictly :render-after-expand="false"
                    style="width: 100%" :disabled="!!field.readOnly" />
                  <!-- select / user-select -->
                  <el-select v-else-if="field.component === 'select' || field.component === 'user-select'"
                    v-model="formState[field.field]" filterable clearable style="width: 100%"
                    :disabled="!!field.readOnly"
                    :placeholder="field.placeholder || `请选择${field.label}`">
                    <el-option v-for="opt in resolveOptions(field)" :key="opt.value" :label="opt.label" :value="opt.value" />
                    <template v-if="!resolveOptions(field).length" #empty>
                      <div class="select-empty">暂无可选项</div>
                    </template>
                  </el-select>
                  <!-- date -->
                  <el-date-picker v-else-if="field.component === 'date'" v-model="formState[field.field]"
                    type="date" value-format="YYYY-MM-DD" style="width: 100%"
                    :disabled="!!field.readOnly" :placeholder="field.placeholder || `选择${field.label}`" />
                  <!-- date-range -->
                  <el-date-picker v-else-if="field.component === 'date-range'" v-model="formState[field.field]"
                    type="daterange" value-format="YYYY-MM-DD" range-separator="至"
                    start-placeholder="开始日期" end-placeholder="结束日期"
                    style="width: 100%" :disabled="!!field.readOnly" />
                  <!-- time -->
                  <el-time-picker v-else-if="field.component === 'time'" v-model="formState[field.field]"
                    value-format="HH:mm" style="width: 220px" :disabled="!!field.readOnly" />
                  <!-- number -->
                  <el-input-number v-else-if="field.component === 'number'" v-model="formState[field.field]"
                    :min="field.min ?? 0" :max="field.max" :step="field.step || 1"
                    controls-position="right" style="width: 220px" :disabled="!!field.readOnly" />
                  <!-- radio -->
                  <el-radio-group v-else-if="field.component === 'radio'" v-model="formState[field.field]" :disabled="!!field.readOnly">
                    <el-radio-button v-for="opt in resolveOptions(field)" :key="opt.value" :value="opt.value">{{ opt.label }}</el-radio-button>
                  </el-radio-group>
                  <!-- checkbox -->
                  <el-checkbox-group v-else-if="field.component === 'checkbox'" v-model="formState[field.field]" :disabled="!!field.readOnly">
                    <el-checkbox v-for="opt in resolveOptions(field)" :key="opt.value" :value="opt.value">{{ opt.label }}</el-checkbox>
                  </el-checkbox-group>
                  <!-- switch -->
                  <el-switch v-else-if="field.component === 'switch'" v-model="formState[field.field]"
                    :disabled="!!field.readOnly" active-text="是" inactive-text="否" />
                  <!-- fallback -->
                  <el-input v-else v-model="formState[field.field]" :disabled="!!field.readOnly" clearable />
                  <div v-if="field.hintText" class="field-hint">
                    <i class="ri-information-line" /> {{ field.hintText }}
                  </div>
                </el-form-item>
              </template>
            </template>

            <div class="form-group-divider form-title-row">
              <div class="form-group-bar" />
              <h4 class="form-group-title">其他信息</h4>
            </div>
            <el-form-item label="申请标题" class="wizard-field">
              <el-input v-model="applyTitle" placeholder="可留空，系统将根据模板自动生成标题" clearable :maxlength="80" show-word-limit />
              <div class="field-hint"><i class="ri-information-line" /> 留空时系统自动按「模板名称 + 日期」生成</div>
            </el-form-item>
          </el-form>
        </div>
      </section>

      <!-- Step: 上传附件 -->
      <section v-else-if="hasAttachment && currentStep === attachmentStepIndex" key="upload" class="wizard-panel">
        <div class="upload-card">
          <div class="upload-card__head">
            <i class="ri-upload-cloud-2-line" />
            <div>
              <h3>上传证明材料</h3>
              <p>支持图片、PDF、Word 等常见格式，单文件不超过 10MB</p>
            </div>
          </div>
          <div class="upload-card__body">
            <el-upload
              :http-request="handleUpload"
              :file-list="uploadFiles"
              drag multiple list-type="text"
              :limit="template?.maxAttachments || 6"
              :accept="template?.acceptFileTypes || ''"
              :on-remove="handleRemoveUpload"
              :on-exceed="handleExceed"
              class="upload-dragger"
            >
              <div class="upload-dragger__inner">
                <i class="ri-inbox-unarchive-line" />
                <p>将文件拖拽到此处，或 <em>点击上传</em></p>
                <span>最多可上传 {{ template?.maxAttachments || 6 }} 个文件</span>
              </div>
            </el-upload>
            <div v-if="uploadFiles.length" class="uploaded-list">
              <div v-for="(file, i) in uploadFiles" :key="i" class="uploaded-item">
                <i :class="fileIcon(file.name)" />
                <span class="uploaded-item__name">{{ file.name }}</span>
                <el-button link type="danger" size="small" @click="removeFile(i)">
                  <i class="ri-delete-bin-line" />
                </el-button>
              </div>
            </div>
            <div v-if="!uploadFiles.length" class="upload-empty">
              <span>未上传任何附件，可跳过此步骤</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Step: 预览确认 -->
      <section v-else-if="currentStep === previewStepIndex" key="preview" class="wizard-panel">
        <div class="preview-card">
          <div class="preview-card__head">
            <i class="ri-file-list-3-line" />
            <div>
              <h3>核对申请信息</h3>
              <p>请仔细核对以下信息，确认无误后点击「确认提交」</p>
            </div>
          </div>

          <div class="preview-card__body">
            <!-- Template info badge -->
            <div class="preview-meta">
              <el-tag effect="plain" size="small" round>{{ template?.templateName }}</el-tag>
              <el-tag v-if="applyTitle" effect="plain" size="small" type="info" round>{{ applyTitle }}</el-tag>
            </div>

            <template v-for="group in groupedFormFields" :key="'p-' + group.title">
              <div v-if="group.title" class="preview-group-title">{{ group.title }}</div>
              <div class="preview-grid">
                <template v-for="field in group.fields" :key="field.field">
                  <div v-if="isFieldVisible(field)" class="preview-item" :class="{ 'is-long': isLongField(field) }">
                    <span class="preview-item__label">{{ field.label }}</span>
                    <strong class="preview-item__value">{{ resolveDisplayValue(field) || '-' }}</strong>
                  </div>
                </template>
              </div>
            </template>

            <!-- Attachments -->
            <div v-if="uploadFiles.length" class="preview-section">
              <div class="preview-section__title">
                <i class="ri-attachment-2" /> 附件 ({{ uploadFiles.length }})
              </div>
              <div class="preview-files">
                <div v-for="file in uploadFiles" :key="file.name" class="preview-file-tag">
                  <i :class="fileIcon(file.name)" />
                  {{ file.name }}
                </div>
              </div>
            </div>

            <!-- Workflow -->
            <div class="preview-section">
              <div class="preview-section__title">
                <i class="ri-route-line" /> 审核流程
              </div>
              <div class="flow-timeline">
                <div class="flow-node flow-node--start">
                  <div class="flow-node__dot" />
                  <div class="flow-node__content">
                    <strong>提交申请</strong>
                    <span>申请人</span>
                  </div>
                </div>
                <div v-for="(step, i) in workflowSteps" :key="i" class="flow-node">
                  <div class="flow-node__dot" />
                  <div class="flow-node__content">
                    <strong>{{ step.stepName }}</strong>
                    <span>{{ resolveStepAssignment(step) }}</span>
                  </div>
                </div>
                <div class="flow-node flow-node--end">
                  <div class="flow-node__dot" />
                  <div class="flow-node__content">
                    <strong>办结</strong>
                    <span>系统自动</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Step: 提交完成 -->
      <section v-else-if="currentStep === doneStepIndex" key="done" class="wizard-panel">
        <div class="done-card">
          <div class="done-anim">
            <div class="done-circle">
              <i class="ri-check-line" />
            </div>
          </div>
          <h2>申请已成功提交</h2>
          <p class="done-desc">您的申请已进入审核流程，请耐心等待</p>
          <div class="done-info">
            <div class="done-info__item">
              <span>申请编号</span>
              <strong>{{ submitResult?.requestNo || '-' }}</strong>
            </div>
            <div class="done-info__item">
              <span>当前节点</span>
              <strong>{{ submitResult?.currentStepName || '审核中' }}</strong>
            </div>
          </div>
          <div class="done-actions">
            <el-button type="primary" size="large" round @click="$emit('view-result', submitResult)">
              <i class="ri-eye-line" /> 查看申请详情
            </el-button>
            <el-button size="large" round @click="$emit('back')">
              <i class="ri-arrow-left-line" /> 返回服务大厅
            </el-button>
          </div>
        </div>
      </section>
    </transition>

    <!-- Footer -->
    <div v-if="currentStep < doneStepIndex" class="wizard-footer">
      <div class="footer-left">
        <span v-if="currentStep === previewStepIndex" class="footer-tip">
          <i class="ri-shield-check-line" /> 提交后可在「我的申请」中查看进度
        </span>
      </div>
      <div class="footer-actions">
        <el-button v-if="currentStep > 0" size="large" round @click="goPrev">
          <i class="ri-arrow-left-s-line" /> 上一步
        </el-button>
        <el-button v-if="currentStep < previewStepIndex" type="primary" size="large" round @click="goNext">
          下一步 <i class="ri-arrow-right-s-line" />
        </el-button>
        <el-button v-if="currentStep === previewStepIndex" type="primary" size="large" round
          :loading="submitting" @click="doSubmit">
          <i class="ri-send-plane-line" /> 确认提交
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from '@/utils/feedback'
import { getPortalAffairOptions, submitPortalAffairRequest, uploadPortalAffairAttachment } from '@/api/portal'

const props = defineProps<{
  portalRole: 'student' | 'teacher'
  template: any
  initialFormData?: Record<string, any>
  initialTitle?: string
}>()

const emit = defineEmits<{
  back: []
  'view-result': [result: any]
}>()

const formRef = ref<any>()
const currentStep = ref(0)
const stepDirection = ref('step-forward')
const submitting = ref(false)
const formState = reactive<Record<string, any>>({})
const applyTitle = ref('')
const uploadFiles = ref<any[]>([])
const submitResult = ref<any>(null)
const optionMap = reactive<any>({ teacherOptions: [], studentStatusOptions: [] })

const hasAttachment = computed(() => props.template?.allowAttachment === '1')
const attachmentStepIndex = computed(() => hasAttachment.value ? 1 : -1)
const previewStepIndex = computed(() => hasAttachment.value ? 2 : 1)
const doneStepIndex = computed(() => hasAttachment.value ? 3 : 2)

const stepList = computed(() => {
  const list = ['填写信息']
  if (hasAttachment.value) list.push('上传材料')
  list.push('核对信息', '提交成功')
  return list
})

const stepsProgressWidth = computed(() => {
  const total = stepList.value.length - 1
  if (total <= 0) return '0%'
  return Math.min(100, (currentStep.value / total) * 100) + '%'
})

const formFields = computed(() => {
  try { return JSON.parse(props.template?.formSchemaJson || '[]') } catch { return [] }
})
const workflowSteps = computed(() => {
  try { return JSON.parse(props.template?.workflowSchemaJson || '[]') } catch { return [] }
})

const totalFieldCount = computed(() => formFields.value.filter((f: any) => !f.readOnly && !f.computeExpr).length)
const requiredFieldCount = computed(() => formFields.value.filter((f: any) => f.required).length)
const filledCount = computed(() => {
  return formFields.value.filter((f: any) => {
    if (f.readOnly || f.computeExpr) return false
    const v = formState[f.field]
    if (v === null || v === undefined || v === '') return false
    if (Array.isArray(v) && !v.length) return false
    return true
  }).length
})
const progressPercent = computed(() => totalFieldCount.value ? Math.round((filledCount.value / totalFieldCount.value) * 100) : 0)

// optionSource alias map: schema-friendly names -> backend option keys
const optionSourceAlias: Record<string, string> = {
  myLeaveRequests: 'leaveOptions',
}

function resolveOptions(field: any) {
  if (Array.isArray(field.options) && field.options.length) return field.options
  if (field.optionSource) {
    const aliasKey = optionSourceAlias[field.optionSource]
    if (aliasKey && optionMap[aliasKey]) return optionMap[aliasKey]
    if (optionMap[field.optionSource + 'Options']) return optionMap[field.optionSource + 'Options']
  }
  return []
}

function buildRules(field: any) {
  if (Array.isArray(field.rules) && field.rules.length) {
    return field.rules.map((r: any) => ({
      ...r,
      pattern: r.pattern ? new RegExp(r.pattern) : undefined,
      trigger: r.trigger || 'blur',
    }))
  }
  if (field.required) {
    const trigger = ['select', 'user-select', 'date', 'date-range', 'radio', 'switch', 'tree-select', 'checkbox'].includes(field.component) ? 'change' : 'blur'
    return [{ required: true, message: `请${trigger === 'change' ? '选择' : '输入'}${field.label}`, trigger }]
  }
  return undefined
}

function isFieldVisible(field: any): boolean {
  if (!field.visibleWhen) return true
  const { field: depField, eq, neq, in: inArr } = field.visibleWhen
  const depVal = formState[depField]
  if (eq !== undefined) return String(depVal) === String(eq)
  if (neq !== undefined) return String(depVal) !== String(neq)
  if (Array.isArray(inArr)) return inArr.map(String).includes(String(depVal))
  return true
}

function isLongField(field: any): boolean {
  return ['textarea'].includes(field.component) || String(formState[field.field] || '').length > 40
}

const groupedFormFields = computed(() => {
  const groups: { title: string; fields: any[] }[] = []
  let currentGroup: { title: string; fields: any[] } | null = null
  for (const field of formFields.value) {
    const groupTitle = field.group || ''
    if (!currentGroup || currentGroup.title !== groupTitle) {
      currentGroup = { title: groupTitle, fields: [] }
      groups.push(currentGroup)
    }
    currentGroup.fields.push(field)
  }
  return groups
})

function computeFields() {
  for (const field of formFields.value) {
    if (!field.computeExpr) continue
    const expr = field.computeExpr as string
    if (expr.startsWith('DATE_DIFF(')) {
      const inner = expr.slice(10, -1)
      const [f1, f2] = inner.split(',').map((s: string) => s.trim())
      const d1 = formState[f1], d2 = formState[f2]
      if (d1 && d2) {
        const diff = Math.ceil((new Date(d2).getTime() - new Date(d1).getTime()) / 86400000) + 1
        formState[field.field] = Math.max(0, diff)
      }
    } else if (expr.startsWith('DATE_RANGE_DIFF(')) {
      const rangeFld = expr.slice(16, -1).trim()
      const range = formState[rangeFld]
      if (Array.isArray(range) && range[0] && range[1]) {
        const diff = Math.ceil((new Date(range[1]).getTime() - new Date(range[0]).getTime()) / 86400000) + 1
        formState[field.field] = Math.max(0, diff)
      }
    }
  }
}

function findLabelInTree(nodes: any[], val: string): string | undefined {
  for (const n of nodes) {
    if (String(n.value) === val) return n.label
    if (Array.isArray(n.children)) {
      const found = findLabelInTree(n.children, val)
      if (found) return found
    }
  }
  return undefined
}

function resolveDisplayValue(field: any) {
  const val = formState[field.field]
  if (val === null || val === undefined || val === '') return ''
  if (field.component === 'date-range' && Array.isArray(val)) return val.join(' ~ ')
  if (field.component === 'switch') return val ? '是' : '否'
  const opts = resolveOptions(field)
  if (field.component === 'checkbox' && Array.isArray(val)) {
    return val.map((v: any) => findLabelInTree(opts, String(v)) || v).join('、')
  }
  return findLabelInTree(opts, String(val)) || val
}

const ROLE_LABELS: Record<string, string> = {
  advisor: '辅导员', admin: '管理员', teacher: '教师', student: '学生',
}

function resolveStepAssignment(step: any) {
  if (step.assignmentType === 'ROLE') return ROLE_LABELS[step.reviewerRole] || step.reviewerRole || '未配置'
  if (step.assignmentType === 'FORM_FIELD') return '由表单指定'
  if (step.assignmentType === 'USER') return '固定审核人'
  return step.assignmentType || '未设置'
}

function fileIcon(name: string) {
  const ext = (name || '').split('.').pop()?.toLowerCase()
  if (['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp'].includes(ext || '')) return 'ri-image-line'
  if (['pdf'].includes(ext || '')) return 'ri-file-pdf-2-line'
  if (['doc', 'docx'].includes(ext || '')) return 'ri-file-word-line'
  if (['xls', 'xlsx'].includes(ext || '')) return 'ri-file-excel-line'
  return 'ri-file-line'
}

async function goNext() {
  stepDirection.value = 'step-forward'
  if (currentStep.value === 0) {
    try { await formRef.value?.validate() } catch { ElMessage.warning('请完成所有必填项'); return }
  }
  currentStep.value++
}

function goPrev() {
  stepDirection.value = 'step-backward'
  currentStep.value--
}

async function doSubmit() {
  submitting.value = true
  try {
    const payload = {
      templateId: props.template.templateId,
      title: applyTitle.value,
      formDataJson: JSON.stringify(formState),
      attachmentJson: JSON.stringify(uploadFiles.value.map((f: any) => f.responseData || f)),
    }
    const res = await submitPortalAffairRequest(props.portalRole, payload)
    submitResult.value = res.data || res
    ElMessage.success('申请已提交')
    stepDirection.value = 'step-forward'
    currentStep.value = doneStepIndex.value
  } catch (e: any) {
    ElMessage.error(e?.message || '提交失败，请稍后再试')
  } finally {
    submitting.value = false
  }
}

async function handleUpload(option: any) {
  try {
    const res = await uploadPortalAffairAttachment(option.file)
    uploadFiles.value.push({
      name: res.originalFilename || option.file.name,
      url: res.url,
      responseData: { name: res.originalFilename || option.file.name, url: res.url, fileName: res.fileName },
    })
    option.onSuccess(res)
  } catch (e: any) {
    ElMessage.error('上传失败: ' + (e?.message || '未知错误'))
    option.onError(e)
  }
}

function handleRemoveUpload(file: any) {
  uploadFiles.value = uploadFiles.value.filter(f => f.url !== file.url && f.name !== file.name)
}

function removeFile(index: number) {
  uploadFiles.value.splice(index, 1)
}

function handleExceed() {
  ElMessage.warning(`最多上传 ${props.template?.maxAttachments || 6} 个文件`)
}

function resetForm() {
  Object.keys(formState).forEach(k => delete formState[k])
  formFields.value.forEach((f: any) => {
    if (props.initialFormData && Object.prototype.hasOwnProperty.call(props.initialFormData, f.field)) {
      formState[f.field] = props.initialFormData[f.field]
      return
    }
    if (f.defaultValue !== undefined && f.defaultValue !== null && f.defaultValue !== '') {
      formState[f.field] = f.component === 'number' ? Number(f.defaultValue) : f.defaultValue
      return
    }
    if (f.component === 'number') { formState[f.field] = 0; return }
    if (f.component === 'checkbox') { formState[f.field] = []; return }
    if (f.component === 'date-range') { formState[f.field] = []; return }
    if (f.component === 'switch') { formState[f.field] = false; return }
    formState[f.field] = ''
  })
  applyTitle.value = props.initialTitle || ''
  uploadFiles.value = []
  submitResult.value = null
  currentStep.value = 0
}

watch(() => [props.template, props.initialFormData, props.initialTitle], () => resetForm(), { immediate: true, deep: true })

let computeTimer: ReturnType<typeof setTimeout> | null = null
watch(formState, () => {
  if (computeTimer) clearTimeout(computeTimer)
  computeTimer = setTimeout(() => computeFields(), 200)
}, { deep: true })

onMounted(async () => {
  const res = await getPortalAffairOptions()
  Object.assign(optionMap, res.data || res)
})
</script>

<style scoped>
/* ============ Layout ============ */
.apply-wizard { display: flex; flex-direction: column; gap: 24px; }

/* ============ Header ============ */
.wizard-header { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.header-left { display: flex; align-items: center; gap: 14px; }
.back-btn { font-weight: 500; }
.header-divider { width: 1px; height: 28px; background: #e2e8f0; }
.header-info { display: flex; flex-direction: column; }
.wizard-title { margin: 0; font-size: 20px; font-weight: 700; color: #0f172a; line-height: 1.3; }
.wizard-subtitle { font-size: 13px; color: #64748b; margin-top: 2px; }
.header-progress { display: flex; flex-direction: column; align-items: flex-end; gap: 6px; min-width: 180px; }
.progress-text { font-size: 12px; color: #64748b; font-variant-numeric: tabular-nums; }
.progress-bar { width: 100%; height: 6px; background: #e2e8f0; border-radius: 3px; overflow: hidden; }
.progress-bar__fill { height: 100%; background: linear-gradient(90deg, #60a5fa, #3b82f6); border-radius: 3px; transition: width .4s cubic-bezier(.4,0,.2,1); }

/* ============ Custom Steps ============ */
.steps-wrapper { padding: 8px 0; }
.steps-track { display: flex; align-items: center; justify-content: center; gap: 0; position: relative; max-width: 640px; margin: 0 auto; }
.step-node { display: flex; flex-direction: column; align-items: center; gap: 8px; flex: 1; position: relative; z-index: 1; }
.step-node__circle {
  width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 700; color: #94a3b8; background: #f1f5f9; border: 2px solid #e2e8f0;
  transition: all .35s cubic-bezier(.4,0,.2,1);
}
.step-node.is-active .step-node__circle { color: #fff; background: #3b82f6; border-color: #3b82f6; box-shadow: 0 0 0 4px rgba(59,130,246,.15); }
.step-node.is-done .step-node__circle { color: #fff; background: #10b981; border-color: #10b981; }
.step-node.is-done .step-node__circle i { font-size: 18px; }
.step-node__label { font-size: 13px; color: #94a3b8; font-weight: 500; transition: color .25s; white-space: nowrap; }
.step-node.is-active .step-node__label { color: #3b82f6; font-weight: 600; }
.step-node.is-done .step-node__label { color: #10b981; }
.steps-connector { position: absolute; top: 18px; left: 18%; right: 18%; height: 3px; background: #e2e8f0; border-radius: 2px; z-index: 0; }
.steps-connector__fill { height: 100%; background: linear-gradient(90deg, #10b981, #3b82f6); border-radius: 2px; transition: width .5s cubic-bezier(.4,0,.2,1); }

/* ============ Step Transition ============ */
.step-forward-enter-active, .step-forward-leave-active,
.step-backward-enter-active, .step-backward-leave-active {
  transition: all .3s cubic-bezier(.4,0,.2,1);
}
.step-forward-enter-from { opacity: 0; transform: translateX(40px); }
.step-forward-leave-to { opacity: 0; transform: translateX(-40px); }
.step-backward-enter-from { opacity: 0; transform: translateX(-40px); }
.step-backward-leave-to { opacity: 0; transform: translateX(40px); }

/* ============ Panel ============ */
.wizard-panel { min-height: 240px; }

/* ============ Form Card ============ */
.form-card { background: #fff; border: 1px solid #e5eef8; border-radius: 20px; overflow: hidden; }
.form-card__head {
  display: flex; align-items: center; justify-content: space-between; gap: 12px;
  padding: 20px 28px; background: linear-gradient(135deg, #f8faff 0%, #f0f7ff 100%); border-bottom: 1px solid #e5eef8;
}
.form-card__head h3 { margin: 0; font-size: 16px; font-weight: 700; color: #1e293b; }
.form-card__head p { margin: 4px 0 0; font-size: 13px; color: #64748b; }
.req-star { color: #ef4444; font-weight: 700; }
.wizard-form { padding: 24px 28px 12px; max-width: 720px; }
.wizard-field { margin-bottom: 22px; }
.field-hint { display: flex; align-items: flex-start; gap: 4px; margin-top: 6px; color: #94a3b8; font-size: 12px; line-height: 1.6; }
.field-hint i { margin-top: 2px; font-size: 14px; flex-shrink: 0; }
.select-empty { padding: 12px; text-align: center; color: #94a3b8; font-size: 13px; }

.form-group-divider { display: flex; align-items: center; gap: 10px; margin: 6px 0 16px; padding-top: 14px; }
.form-group-divider:first-child { padding-top: 0; margin-top: 0; }
.form-group-bar { width: 4px; height: 18px; border-radius: 2px; background: linear-gradient(180deg, #3b82f6, #60a5fa); flex-shrink: 0; }
.form-group-title { margin: 0; font-size: 15px; font-weight: 700; color: #1e293b; letter-spacing: .02em; }
.form-title-row { margin-top: 8px; }

/* ============ Upload Card ============ */
.upload-card { background: #fff; border: 1px solid #e5eef8; border-radius: 20px; overflow: hidden; }
.upload-card__head { display: flex; align-items: center; gap: 14px; padding: 20px 28px; background: linear-gradient(135deg, #f8faff 0%, #f0f7ff 100%); border-bottom: 1px solid #e5eef8; }
.upload-card__head i { font-size: 28px; color: #3b82f6; }
.upload-card__head h3 { margin: 0; font-size: 16px; font-weight: 700; color: #1e293b; }
.upload-card__head p { margin: 4px 0 0; font-size: 13px; color: #64748b; }
.upload-card__body { padding: 28px; }

.upload-dragger :deep(.el-upload) { width: 100%; }
.upload-dragger :deep(.el-upload-dragger) { border: 2px dashed #cbd5e1; border-radius: 16px; background: #fafbfd; transition: border-color .2s, background .2s; padding: 0; }
.upload-dragger :deep(.el-upload-dragger:hover) { border-color: #3b82f6; background: #f0f7ff; }
.upload-dragger :deep(.el-upload-list) { display: none; }
.upload-dragger__inner { padding: 36px 20px; text-align: center; }
.upload-dragger__inner i { font-size: 42px; color: #94a3b8; display: block; margin-bottom: 12px; }
.upload-dragger__inner p { margin: 0 0 6px; font-size: 15px; color: #475569; }
.upload-dragger__inner em { color: #3b82f6; font-style: normal; font-weight: 600; }
.upload-dragger__inner span { font-size: 12px; color: #94a3b8; }

.uploaded-list { display: grid; gap: 8px; margin-top: 20px; }
.uploaded-item {
  display: flex; align-items: center; gap: 10px; padding: 10px 14px; border-radius: 12px;
  background: #f8fafc; border: 1px solid #e2e8f0; transition: box-shadow .15s;
}
.uploaded-item:hover { box-shadow: 0 2px 8px rgba(0,0,0,.04); }
.uploaded-item i { font-size: 18px; color: #3b82f6; flex-shrink: 0; }
.uploaded-item__name { flex: 1; font-size: 13px; color: #334155; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.upload-empty { text-align: center; padding: 20px; color: #94a3b8; font-size: 13px; }

/* ============ Preview Card ============ */
.preview-card { background: #fff; border: 1px solid #e5eef8; border-radius: 20px; overflow: hidden; }
.preview-card__head { display: flex; align-items: center; gap: 14px; padding: 20px 28px; background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%); border-bottom: 1px solid #d1fae5; }
.preview-card__head i { font-size: 28px; color: #10b981; }
.preview-card__head h3 { margin: 0; font-size: 16px; font-weight: 700; color: #065f46; }
.preview-card__head p { margin: 4px 0 0; font-size: 13px; color: #6b7280; }
.preview-card__body { padding: 24px 28px; display: grid; gap: 20px; }

.preview-meta { display: flex; gap: 8px; flex-wrap: wrap; }
.preview-group-title { font-size: 14px; font-weight: 700; color: #475569; padding: 6px 0; border-bottom: 1px solid #f1f5f9; }
.preview-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.preview-item {
  padding: 14px 16px; border-radius: 14px; background: #f8fafc; border: 1px solid #e2e8f0;
  display: flex; flex-direction: column; gap: 6px; transition: box-shadow .15s ease, transform .15s ease;
}
.preview-item:hover { transform: translateY(-1px); box-shadow: 0 4px 16px rgba(15,23,42,.05); }
.preview-item.is-long { grid-column: 1 / -1; }
.preview-item__label { color: #64748b; font-size: 12px; font-weight: 500; }
.preview-item__value { color: #111827; font-size: 14px; line-height: 1.6; word-break: break-all; }

.preview-section { display: grid; gap: 12px; }
.preview-section__title { display: flex; align-items: center; gap: 6px; font-size: 14px; font-weight: 700; color: #475569; }
.preview-section__title i { font-size: 16px; color: #3b82f6; }
.preview-files { display: flex; gap: 8px; flex-wrap: wrap; }
.preview-file-tag {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 6px 14px; border-radius: 10px; background: #eff6ff; color: #1d4ed8; font-size: 13px; font-weight: 500;
}
.preview-file-tag i { font-size: 15px; }

/* Flow timeline */
.flow-timeline { display: flex; flex-wrap: wrap; gap: 0; padding-left: 4px; }
.flow-node { display: flex; align-items: center; gap: 10px; position: relative; padding-right: 32px; }
.flow-node::after {
  content: ''; position: absolute; right: 6px; top: 50%; width: 20px; height: 2px;
  background: #cbd5e1; transform: translateY(-50%);
}
.flow-node:last-child::after { display: none; }
.flow-node__dot { width: 12px; height: 12px; border-radius: 50%; background: #3b82f6; flex-shrink: 0; border: 2px solid #dbeafe; }
.flow-node--start .flow-node__dot { background: #10b981; border-color: #d1fae5; }
.flow-node--end .flow-node__dot { background: #8b5cf6; border-color: #ede9fe; }
.flow-node__content { display: flex; flex-direction: column; }
.flow-node__content strong { font-size: 13px; color: #172033; }
.flow-node__content span { font-size: 11px; color: #94a3b8; }

/* ============ Done Card ============ */
.done-card { text-align: center; padding: 48px 24px; }
.done-anim { margin-bottom: 20px; }
.done-circle {
  width: 80px; height: 80px; border-radius: 50%; margin: 0 auto;
  background: linear-gradient(135deg, #10b981, #34d399); color: #fff;
  display: flex; align-items: center; justify-content: center; font-size: 36px;
  animation: done-pop .5s cubic-bezier(.2,.8,.3,1.2);
  box-shadow: 0 8px 32px rgba(16,185,129,.25);
}
@keyframes done-pop {
  0% { transform: scale(0); opacity: 0; }
  60% { transform: scale(1.15); }
  100% { transform: scale(1); opacity: 1; }
}
.done-card h2 { margin: 0 0 6px; font-size: 22px; color: #065f46; }
.done-desc { margin: 0 0 24px; color: #6b7280; font-size: 14px; }
.done-info { display: inline-grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 28px; text-align: left; }
.done-info__item { padding: 14px 20px; border-radius: 14px; background: #f0fdf4; border: 1px solid #bbf7d0; display: flex; flex-direction: column; gap: 4px; min-width: 160px; }
.done-info__item span { font-size: 12px; color: #6b7280; }
.done-info__item strong { font-size: 15px; color: #065f46; }
.done-actions { display: flex; gap: 12px; justify-content: center; flex-wrap: wrap; }

/* ============ Footer ============ */
.wizard-footer {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 24px; margin: 0 -4px; background: #fff; border-radius: 18px;
  border: 1px solid #e5eef8; box-shadow: 0 -2px 12px rgba(15,23,42,.04);
  position: sticky; bottom: 0; z-index: 10;
}
.footer-left { flex: 1; }
.footer-tip { display: inline-flex; align-items: center; gap: 6px; font-size: 12px; color: #94a3b8; }
.footer-tip i { font-size: 15px; color: #10b981; }
.footer-actions { display: flex; gap: 10px; }

/* ============ Responsive ============ */
@media (max-width: 768px) {
  .wizard-header { flex-direction: column; align-items: flex-start; }
  .header-progress { width: 100%; align-items: stretch; }
  .preview-grid { grid-template-columns: 1fr; }
  .flow-timeline { flex-direction: column; gap: 12px; padding-left: 0; }
  .flow-node { padding-right: 0; }
  .flow-node::after { display: none; }
  .steps-track { gap: 0; }
  .step-node__label { font-size: 11px; }
  .done-info { grid-template-columns: 1fr; }
  .wizard-form { max-width: 100%; }
  .wizard-field :deep(.el-form-item) { display: block; }
  .wizard-field :deep(.el-form-item__label) { display: block; text-align: left; padding-bottom: 4px; }
  .wizard-field :deep(.el-form-item__content) { margin-left: 0 !important; }
  .wizard-field :deep(.el-input),
  .wizard-field :deep(.el-select),
  .wizard-field :deep(.el-date-editor),
  .wizard-field :deep(.el-textarea) { width: 100% !important; max-width: 100%; }
  :deep(.el-dialog) { width: 96vw !important; margin: 2vh auto !important; max-height: 96vh; }
  :deep(.el-drawer) { width: 100% !important; }
  .step-node__circle { width: 30px; height: 30px; font-size: 12px; }
  .steps-connector { top: 15px; }
}
@media (max-width: 640px) {
  .wizard-form { max-width: 100%; padding: 16px; }
  .form-card__head { padding: 16px; }
  .upload-card__head { padding: 16px; }
  .upload-card__body { padding: 16px; }
  .preview-card__body { padding: 16px; }
  .done-card { padding: 32px 16px; }
  .wizard-footer { padding: 12px 16px; flex-direction: column; gap: 10px; }
  .footer-actions { width: 100%; justify-content: center; }
  .wizard-title { font-size: 17px; }
  .wizard-subtitle { font-size: 12px; }
  .form-card__head h3 { font-size: 14px; }
  .upload-card__head h3 { font-size: 14px; }
  .preview-card__head h3 { font-size: 14px; }
  .upload-dragger__inner { padding: 24px 16px; }
  .upload-dragger__inner i { font-size: 32px; }
  .upload-dragger__inner p { font-size: 13px; }
  .done-card h2 { font-size: 19px; }
  .done-circle { width: 64px; height: 64px; font-size: 28px; }
  .apply-wizard { gap: 16px; }
  .preview-item__label { font-size: 11px; }
  .preview-item__value { font-size: 13px; }
}
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after { transition: none !important; animation: none !important; }
}
</style>
