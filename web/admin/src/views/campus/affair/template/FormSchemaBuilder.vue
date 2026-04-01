<template>
  <div class="form-schema-builder">
    <div class="builder-layout">
      <!-- 左侧组件面板 -->
      <aside class="component-panel">
        <h4>表单组件</h4>
        <div class="component-list">
          <div v-for="comp in componentTypes" :key="comp.type" class="component-item" draggable="true" @dragstart="onDragStart(comp)" @dragend="onDragEnd" @dblclick="appendComponent(comp)">
            <i :class="comp.icon" />
            <span>{{ comp.label }}</span>
          </div>
        </div>
        <h4 class="component-panel__subhead">系统字段</h4>
        <div class="preset-list">
          <button v-for="preset in systemFieldPresets" :key="preset.field" type="button" class="preset-item" @click="appendSystemField(preset)">
            <strong>{{ preset.label }}</strong>
            <span>{{ preset.field }}</span>
          </button>
        </div>
      </aside>

      <!-- 中间画布 -->
      <main class="canvas-panel" @dragover.prevent @drop="onDrop">
        <div v-if="!fields.length" class="canvas-empty">
          <i class="ri-drag-drop-line" />
          <p>拖拽左侧组件到此处添加表单字段</p>
        </div>
        <div v-for="(field, idx) in fields" :key="field._uid" class="canvas-field" :class="{ 'is-active': activeIndex === idx }" @click="selectField(idx)">
          <div class="canvas-field__header">
            <span class="canvas-field__label">{{ field.label || '未命名字段' }}</span>
            <el-tag size="small" effect="plain">{{ componentLabel(field.component) }}</el-tag>
            <span v-if="field.required" class="required-dot">*</span>
          </div>
          <div class="canvas-field__preview">
            <el-input v-if="field.component === 'input'" disabled :placeholder="field.placeholder || field.label" />
            <el-input v-else-if="field.component === 'textarea'" type="textarea" :rows="2" disabled :placeholder="field.placeholder || field.label" />
            <el-select v-else-if="field.component === 'select' || field.component === 'user-select'" disabled style="width:100%" :placeholder="`选择${field.label}`">
              <el-option v-for="opt in resolvePreviewOptions(field)" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
            <el-date-picker v-else-if="field.component === 'date'" disabled type="date" style="width:100%" :placeholder="field.label" />
            <el-date-picker v-else-if="field.component === 'date-range'" disabled type="daterange" style="width:100%" start-placeholder="开始日期" end-placeholder="结束日期" />
            <el-input-number v-else-if="field.component === 'number'" disabled :min="0" style="width:200px" />
            <el-radio-group v-else-if="field.component === 'radio'" disabled>
              <el-radio v-for="opt in resolvePreviewOptions(field)" :key="opt.value" :value="opt.value">{{ opt.label }}</el-radio>
            </el-radio-group>
            <el-switch v-else-if="field.component === 'switch'" disabled />
            <el-input v-else-if="field.component === 'file'" disabled placeholder="点击上传附件" />
            <el-input v-else disabled :placeholder="field.label" />
            <div v-if="field.optionSource" class="canvas-field__source">
              <el-tag size="small" effect="plain" type="info">数据源: {{ optionSourceLabel(field.optionSource) }}</el-tag>
            </div>
            <div v-if="field.hintText" class="canvas-field__hint">{{ field.hintText }}</div>
          </div>
          <div class="canvas-field__actions">
            <el-button link size="small" :disabled="idx === 0" @click.stop="moveField(idx, -1)"><i class="ri-arrow-up-line" /></el-button>
            <el-button link size="small" :disabled="idx === fields.length - 1" @click.stop="moveField(idx, 1)"><i class="ri-arrow-down-line" /></el-button>
            <el-button link type="danger" size="small" @click.stop="removeField(idx)"><i class="ri-delete-bin-line" /></el-button>
          </div>
        </div>
      </main>

      <!-- 右侧属性面板 -->
      <aside class="property-panel">
        <template v-if="activeField">
          <h4>字段属性</h4>
          <el-form label-width="80px" size="small">
            <el-form-item label="字段Key">
              <el-input v-model="activeField.field" placeholder="如 reason" />
            </el-form-item>
            <el-form-item label="标题">
              <el-input v-model="activeField.label" placeholder="字段标题" />
            </el-form-item>
            <el-form-item label="占位提示">
              <el-input v-model="activeField.placeholder" placeholder="输入框提示文本" />
            </el-form-item>
            <el-form-item label="默认值">
              <el-input v-model="activeField.defaultValue" placeholder="默认值，可选" />
            </el-form-item>
            <el-form-item label="帮助文案">
              <el-input v-model="activeField.hintText" type="textarea" :rows="2" placeholder="字段说明或填写提示" />
            </el-form-item>
            <el-form-item label="组件">
              <el-select v-model="activeField.component" style="width:100%">
                <el-option v-for="comp in componentTypes" :key="comp.type" :label="comp.label" :value="comp.type" />
              </el-select>
            </el-form-item>
            <el-form-item label="必填">
              <el-switch v-model="activeField.required" />
            </el-form-item>
            <el-form-item v-if="['select', 'user-select', 'radio'].includes(activeField.component)" label="选项来源">
              <el-select v-model="activeField.optionSource" clearable filterable allow-create default-first-option style="width:100%" placeholder="选择系统选项来源">
                <el-option v-for="item in optionSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
              <div v-if="activeField.optionSource" class="field-hint">填写选项来源后，静态选项将被忽略</div>
            </el-form-item>
            <el-form-item v-if="['select', 'radio'].includes(activeField.component)" label="静态选项">
              <el-input v-model="activeField.optionsText" type="textarea" :rows="4" placeholder="每行: 标签:值" />
            </el-form-item>
          </el-form>
        </template>
        <div v-else class="property-empty">
          <p>点击左侧字段编辑属性</p>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'

const props = defineProps<{
  modelValue: any[]
}>()

const emit = defineEmits<{
  'update:modelValue': [value: any[]]
}>()

let uidCounter = 0
const fields = ref<any[]>([])
const activeIndex = ref(-1)
const dragType = ref<any>(null)
const lastSyncedSignature = ref('')
const skipNextExternalSync = ref(false)
let syncTimer: ReturnType<typeof setTimeout> | null = null

const componentTypes = [
  { type: 'input', label: '输入框', icon: 'ri-input-method-line' },
  { type: 'textarea', label: '多行文本', icon: 'ri-text' },
  { type: 'select', label: '下拉选择', icon: 'ri-list-check' },
  { type: 'date', label: '日期', icon: 'ri-calendar-line' },
  { type: 'date-range', label: '日期范围', icon: 'ri-calendar-2-line' },
  { type: 'number', label: '数字', icon: 'ri-hashtag' },
  { type: 'user-select', label: '人员选择', icon: 'ri-user-search-line' },
  { type: 'radio', label: '单选按钮', icon: 'ri-radio-button-line' },
  { type: 'switch', label: '开关', icon: 'ri-toggle-line' },
  { type: 'file', label: '文件上传', icon: 'ri-attachment-2' },
]

const optionSourceOptions = [
  { label: '教师列表', value: 'teacher' },
  { label: '辅导员列表', value: 'advisor' },
  { label: '学生列表', value: 'student' },
  { label: '学期列表', value: 'term' },
  { label: '部门列表', value: 'dept' },
  { label: '年级列表', value: 'grade' },
  { label: '班级列表', value: 'class' },
  { label: '学籍状态', value: 'studentStatus' },
  { label: '我的请假记录', value: 'myLeaveRequests' },
  { label: '我的返校登记记录', value: 'myLeaveReturnRequests' },
  { label: '我的课程列表', value: 'myCourses' },
]

const systemFieldPresets = [
  { field: 'termId', label: '学期', component: 'select', optionSource: 'term', placeholder: '请选择学期', required: true },
  { field: 'mentorTeacherId', label: '指导教师', component: 'user-select', optionSource: 'teacher', placeholder: '请选择指导教师', required: false },
  { field: 'advisorUserId', label: '辅导员', component: 'user-select', optionSource: 'advisor', placeholder: '请选择辅导员', required: false },
  { field: 'deptId', label: '院系/部门', component: 'select', optionSource: 'dept', placeholder: '请选择院系/部门', required: false },
  { field: 'gradeId', label: '年级', component: 'select', optionSource: 'grade', placeholder: '请选择年级', required: false },
  { field: 'classId', label: '班级', component: 'select', optionSource: 'class', placeholder: '请选择班级', required: false },
  { field: 'targetStatusCode', label: '目标学籍状态', component: 'select', optionSource: 'studentStatus', placeholder: '请选择学籍状态', required: false },
  { field: 'leaveRequestNo', label: '原请假编号', component: 'select', optionSource: 'myLeaveRequests', placeholder: '请选择已通过的请假申请', required: true, hintText: '仅显示已审批通过且尚未销假的请假记录' },
]

const activeField = computed(() => activeIndex.value >= 0 ? fields.value[activeIndex.value] : null)

function componentLabel(type: string) {
  return componentTypes.find(c => c.type === type)?.label || type
}

function resolvePreviewOptions(field: any) {
  if (Array.isArray(field.options) && field.options.length) return field.options.slice(0, 3)
  return [{ label: '选项1', value: '1' }, { label: '选项2', value: '2' }]
}

function optionSourceLabel(source: string) {
  return optionSourceOptions.find(o => o.value === source)?.label || source
}

function onDragStart(comp: any) { dragType.value = comp }
function onDragEnd() { dragType.value = null }

function createField(comp: any) {
  return {
    _uid: ++uidCounter,
    field: '',
    label: '',
    placeholder: '',
    defaultValue: '',
    hintText: '',
    component: comp.type,
    required: false,
    optionSource: '',
    optionsText: '',
    options: [],
  }
}

function createPresetField(preset: any) {
  return {
    _uid: ++uidCounter,
    field: preset.field,
    label: preset.label,
    placeholder: preset.placeholder || '',
    defaultValue: '',
    hintText: preset.hintText || '',
    component: preset.component || 'input',
    required: !!preset.required,
    optionSource: preset.optionSource || '',
    optionsText: '',
    options: preset.options || [],
  }
}

function appendComponent(comp: any) {
  fields.value.push(createField(comp))
  activeIndex.value = fields.value.length - 1
  queueSyncToParent()
}

function appendSystemField(preset: any) {
  fields.value.push(createPresetField(preset))
  activeIndex.value = fields.value.length - 1
  queueSyncToParent()
}

function onDrop() {
  if (!dragType.value) return
  fields.value.push(createField(dragType.value))
  activeIndex.value = fields.value.length - 1
  dragType.value = null
  queueSyncToParent()
}

function selectField(idx: number) { activeIndex.value = idx }

function removeField(idx: number) {
  fields.value.splice(idx, 1)
  if (activeIndex.value >= fields.value.length) activeIndex.value = fields.value.length - 1
  queueSyncToParent()
}

function moveField(idx: number, dir: number) {
  const target = idx + dir
  if (target < 0 || target >= fields.value.length) return
  const temp = fields.value[idx]
  fields.value[idx] = fields.value[target]
  fields.value[target] = temp
  activeIndex.value = target
  queueSyncToParent()
}

function buildNormalizedFields() {
  return fields.value.filter(f => f.field && f.label).map(f => {
    const result: any = {
      field: f.field,
      label: f.label,
      component: f.component || 'input',
      required: !!f.required
    }
    if (f.placeholder) result.placeholder = f.placeholder
    if (f.defaultValue !== '' && f.defaultValue !== undefined && f.defaultValue !== null) result.defaultValue = f.defaultValue
    if (f.hintText) result.hintText = f.hintText
    if (f.optionSource) result.optionSource = f.optionSource
    const opts = String(f.optionsText || '').split('\n').map(l => l.trim()).filter(Boolean).map(line => {
      const [label, value] = line.split(':')
      return { label: (label || '').trim(), value: (value || label || '').trim() }
    }).filter(o => o.label && o.value)
    if (opts.length) result.options = opts
    return result
  })
}

function queueSyncToParent() {
  if (syncTimer) {
    clearTimeout(syncTimer)
  }
  syncTimer = setTimeout(() => {
    const normalized = buildNormalizedFields()
    const signature = JSON.stringify(normalized)
    lastSyncedSignature.value = signature
    skipNextExternalSync.value = true
    emit('update:modelValue', normalized)
    syncTimer = null
  }, 80)
}

function syncFromParent(schema: any[]) {
  const signature = JSON.stringify(schema || [])
  if (skipNextExternalSync.value && signature === lastSyncedSignature.value) {
    skipNextExternalSync.value = false
    return
  }
  fields.value = (schema || []).map(f => ({
    _uid: ++uidCounter,
    field: f.field || '',
    label: f.label || '',
    placeholder: f.placeholder || '',
    defaultValue: f.defaultValue ?? '',
    hintText: f.hintText || '',
    component: f.component || 'input',
    required: !!f.required,
    optionSource: f.optionSource || '',
    optionsText: Array.isArray(f.options) ? f.options.map((o: any) => `${o.label}:${o.value}`).join('\n') : '',
    options: f.options || [],
  }))
  if (activeIndex.value >= fields.value.length) {
    activeIndex.value = fields.value.length - 1
  }
}

watch(() => props.modelValue, (val) => { syncFromParent(val) }, { immediate: true, deep: false })
watch(fields, () => queueSyncToParent(), { deep: true })
watch(() => props.modelValue, async () => {
  if (skipNextExternalSync.value) {
    await nextTick()
    skipNextExternalSync.value = false
  }
}, { deep: false })
</script>

<style scoped>
.form-schema-builder { border: 1px solid var(--el-border-color-lighter); border-radius: 14px; overflow: hidden; }
.builder-layout { display: grid; grid-template-columns: 180px 1fr 260px; min-height: 420px; }

.component-panel { padding: 16px; background: #f8fafc; border-right: 1px solid #e5e7eb; }
.component-panel h4 { margin: 0 0 12px; font-size: 13px; color: #64748b; }
.component-panel__subhead { margin-top: 18px; }
.component-list { display: grid; gap: 8px; }
.component-item { display: flex; align-items: center; gap: 8px; padding: 10px 12px; border-radius: 10px; background: #fff; border: 1px solid #e2e8f0; cursor: grab; font-size: 13px; color: #334155; transition: border-color .15s, box-shadow .15s; }
.component-item:hover { border-color: var(--el-color-primary); box-shadow: 0 2px 8px rgba(38,111,203,.1); }
.component-item i { font-size: 16px; color: var(--el-color-primary); }
.preset-list { display: grid; gap: 8px; }
.preset-item { border: 1px solid #dbe7f5; border-radius: 10px; background: #fff; padding: 10px 12px; text-align: left; cursor: pointer; transition: border-color .15s, box-shadow .15s; }
.preset-item:hover { border-color: var(--el-color-primary); box-shadow: 0 2px 8px rgba(38,111,203,.1); }
.preset-item strong { display: block; color: #172033; font-size: 13px; }
.preset-item span { display: block; margin-top: 4px; color: #94a3b8; font-size: 12px; }

.canvas-panel { padding: 16px; background: #fff; min-height: 400px; }
.canvas-empty { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; color: #94a3b8; }
.canvas-empty i { font-size: 40px; margin-bottom: 12px; }
.canvas-field { padding: 14px; border-radius: 12px; border: 2px solid transparent; margin-bottom: 10px; cursor: pointer; transition: border-color .15s; position: relative; }
.canvas-field:hover { border-color: #dbeafe; }
.canvas-field.is-active { border-color: var(--el-color-primary); background: #f8fbff; }
.canvas-field__header { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.canvas-field__label { font-weight: 600; color: #172033; }
.required-dot { color: #ef4444; font-weight: 700; }
.canvas-field__preview { pointer-events: none; opacity: .7; }
.canvas-field__source { margin-top: 6px; pointer-events: auto; }
.canvas-field__hint { margin-top: 6px; color: #94a3b8; font-size: 12px; }
.canvas-field__actions { position: absolute; top: 10px; right: 10px; display: flex; gap: 2px; }

.property-panel { padding: 16px; background: #fafbfc; border-left: 1px solid #e5e7eb; overflow-y: auto; }
.property-panel h4 { margin: 0 0 14px; font-size: 13px; color: #64748b; }
.property-empty { display: flex; align-items: center; justify-content: center; height: 100%; color: #94a3b8; }
.field-hint { margin-top: 4px; font-size: 12px; color: #94a3b8; line-height: 1.4; }
</style>
