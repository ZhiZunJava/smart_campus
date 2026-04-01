<template>
  <div class="workflow-schema-builder">
    <div class="builder-header">
      <div>
        <h4>审核流程设计</h4>
        <p>点击新增节点后可配置角色审核、固定审核人或表单指定审核人</p>
      </div>
      <el-button type="primary" plain icon="Plus" size="small" @click="addStep">新增节点</el-button>
    </div>

    <div v-if="!steps.length" class="empty-hint">
      <i class="ri-flow-chart" />
      <p>点击“新增节点”添加审核流程节点</p>
    </div>

    <div class="flow-track">
      <div v-for="(step, idx) in steps" :key="step._uid" class="flow-card" :class="{ 'is-active': activeIndex === idx }">
        <div class="flow-card__badge">第 {{ idx + 1 }} 步</div>
        <div class="flow-card__body" @click="toggleExpand(idx)">
          <div class="flow-card__icon">
            <i :class="assignmentIcon(step.assignmentType)" />
          </div>
          <div class="flow-card__info">
            <strong>{{ step.stepName || '未命名节点' }}</strong>
            <span>{{ assignmentLabel(step.assignmentType) }}</span>
            <span v-if="step.assignmentType === 'ROLE'" class="flow-card__detail">角色: {{ roleLabel(step.reviewerRole) }}</span>
            <span v-if="step.assignmentType === 'USER'" class="flow-card__detail">审核人: {{ resolveReviewerLabel(step.reviewerUserId) }}</span>
            <span v-if="step.assignmentType === 'FORM_FIELD'" class="flow-card__detail">字段: {{ resolveFieldLabel(step.reviewerField) }}</span>
          </div>
          <div class="flow-card__actions">
            <el-button link size="small" :disabled="idx === 0" @click.stop="moveStep(idx, -1)"><i class="ri-arrow-left-s-line" /></el-button>
            <el-button link size="small" :disabled="idx === steps.length - 1" @click.stop="moveStep(idx, 1)"><i class="ri-arrow-right-s-line" /></el-button>
            <el-button link type="danger" size="small" @click.stop="removeStep(idx)"><i class="ri-delete-bin-line" /></el-button>
          </div>
        </div>

        <el-collapse-transition>
          <div v-show="activeIndex === idx" class="flow-card__expand">
            <el-form label-width="90px" size="small">
              <el-form-item label="节点编码">
                <el-input v-model="step.stepCode" placeholder="如 advisor_review" />
              </el-form-item>
              <el-form-item label="节点名称">
                <el-input v-model="step.stepName" placeholder="如 辅导员审核" />
              </el-form-item>
              <el-form-item label="分派方式">
                <el-select v-model="step.assignmentType" style="width:100%">
                  <el-option label="角色审核" value="ROLE" />
                  <el-option label="固定审核人" value="USER" />
                  <el-option label="表单指定" value="FORM_FIELD" />
                </el-select>
              </el-form-item>
              <el-form-item v-if="step.assignmentType === 'ROLE'" label="审核角色">
                <el-select v-model="step.reviewerRole" style="width:100%">
                  <el-option label="辅导员" value="advisor" />
                  <el-option label="教师" value="teacher" />
                  <el-option label="管理员" value="admin" />
                </el-select>
              </el-form-item>
              <el-form-item v-if="step.assignmentType === 'USER'" label="固定审核人">
                <el-select v-model="step.reviewerUserId" filterable clearable style="width:100%">
                  <el-option v-for="u in reviewerOptions" :key="u.value" :label="u.label" :value="u.value" />
                </el-select>
              </el-form-item>
              <el-form-item v-if="step.assignmentType === 'FORM_FIELD'" label="表单字段">
                <el-select v-model="step.reviewerField" filterable clearable style="width:100%" placeholder="请选择人员字段">
                  <el-option v-for="field in availableFieldOptions" :key="field.value" :label="field.label" :value="field.value" />
                </el-select>
              </el-form-item>
            </el-form>
          </div>
        </el-collapse-transition>

        <div v-if="idx < steps.length - 1" class="flow-connector"><i class="ri-arrow-right-line" /></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'

const props = defineProps<{
  modelValue: any[]
  reviewerOptions: any[]
  fieldOptions?: Array<{ label: string; value: string }>
}>()

const emit = defineEmits<{
  'update:modelValue': [value: any[]]
}>()

let uidCounter = 0
const steps = ref<any[]>([])
const activeIndex = ref(-1)
const availableFieldOptions = computed(() => props.fieldOptions || [])
const lastSyncedSignature = ref('')
const skipNextExternalSync = ref(false)
let syncTimer: ReturnType<typeof setTimeout> | null = null

function assignmentIcon(type: string) {
  if (type === 'ROLE') return 'ri-team-line'
  if (type === 'USER') return 'ri-user-line'
  if (type === 'FORM_FIELD') return 'ri-file-user-line'
  return 'ri-question-line'
}

function assignmentLabel(type: string) {
  if (type === 'ROLE') return '角色审核'
  if (type === 'USER') return '固定审核人'
  if (type === 'FORM_FIELD') return '表单指定'
  return type
}

function roleLabel(value?: string) {
  if (value === 'advisor') return '辅导员'
  if (value === 'teacher') return '教师'
  if (value === 'admin') return '管理员'
  return value || '-'
}

function resolveReviewerLabel(userId: number | string | undefined) {
  if (!userId) return '-'
  return props.reviewerOptions.find((item: any) => String(item.value) === String(userId))?.label || String(userId)
}

function resolveFieldLabel(fieldKey?: string) {
  if (!fieldKey) return '-'
  return availableFieldOptions.value.find((item) => item.value === fieldKey)?.label || fieldKey
}

function addStep() {
  steps.value.push({
    _uid: ++uidCounter,
    stepCode: '',
    stepName: '',
    assignmentType: 'ROLE',
    reviewerRole: 'advisor',
    reviewerUserId: undefined,
    reviewerField: '',
  })
  activeIndex.value = steps.value.length - 1
  queueSyncToParent()
}

function removeStep(idx: number) {
  steps.value.splice(idx, 1)
  if (activeIndex.value >= steps.value.length) activeIndex.value = steps.value.length - 1
  queueSyncToParent()
}

function moveStep(idx: number, dir: number) {
  const target = idx + dir
  if (target < 0 || target >= steps.value.length) return
  const temp = steps.value[idx]
  steps.value[idx] = steps.value[target]
  steps.value[target] = temp
  activeIndex.value = target
  queueSyncToParent()
}

function toggleExpand(idx: number) {
  activeIndex.value = activeIndex.value === idx ? -1 : idx
}

function buildNormalizedSteps() {
  return steps.value.filter((s) => s.stepCode && s.stepName).map((s) => {
    const result: any = { stepCode: s.stepCode, stepName: s.stepName, assignmentType: s.assignmentType || 'ROLE' }
    if (result.assignmentType === 'ROLE') result.reviewerRole = s.reviewerRole || 'advisor'
    if (result.assignmentType === 'USER') result.reviewerUserId = s.reviewerUserId
    if (result.assignmentType === 'FORM_FIELD') result.reviewerField = s.reviewerField
    return result
  })
}

function queueSyncToParent() {
  if (syncTimer) {
    clearTimeout(syncTimer)
  }
  syncTimer = setTimeout(() => {
    const normalized = buildNormalizedSteps()
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
  steps.value = (schema || []).map((s) => ({
    _uid: ++uidCounter,
    stepCode: s.stepCode || '',
    stepName: s.stepName || '',
    assignmentType: s.assignmentType || 'ROLE',
    reviewerRole: s.reviewerRole || 'advisor',
    reviewerUserId: s.reviewerUserId,
    reviewerField: s.reviewerField || '',
  }))
  if (activeIndex.value >= steps.value.length) {
    activeIndex.value = steps.value.length - 1
  }
}

watch(() => props.modelValue, (val) => { syncFromParent(val) }, { immediate: true, deep: false })
watch(steps, () => queueSyncToParent(), { deep: true })
watch(() => props.modelValue, async () => {
  if (skipNextExternalSync.value) {
    await nextTick()
    skipNextExternalSync.value = false
  }
}, { deep: false })
</script>

<style scoped>
.workflow-schema-builder { border: 1px solid var(--el-border-color-lighter); border-radius: 14px; padding: 20px; background: #fbfdff; }
.builder-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.builder-header h4 { margin: 0 0 4px; font-size: 16px; }
.builder-header p { margin: 0; color: #909399; font-size: 13px; }

.empty-hint { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 40px; color: #94a3b8; }
.empty-hint i { font-size: 36px; margin-bottom: 10px; }

.flow-track { display: flex; gap: 0; flex-wrap: wrap; align-items: flex-start; }
.flow-card { position: relative; min-width: 260px; max-width: 340px; border: 2px solid #e5eef8; border-radius: 16px; background: #fff; transition: border-color .15s; }
.flow-card.is-active { border-color: var(--el-color-primary); }
.flow-card__badge { position: absolute; top: -10px; left: 16px; padding: 2px 10px; border-radius: 10px; background: var(--el-color-primary); color: #fff; font-size: 11px; font-weight: 600; }
.flow-card__body { display: flex; align-items: center; gap: 12px; padding: 18px 16px 12px; cursor: pointer; }
.flow-card__icon { width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; border-radius: 12px; background: #eff6ff; color: var(--el-color-primary); font-size: 20px; flex-shrink: 0; }
.flow-card__info { flex: 1; min-width: 0; }
.flow-card__info strong { display: block; color: #172033; font-size: 14px; }
.flow-card__info span { display: block; color: #94a3b8; font-size: 12px; margin-top: 2px; }
.flow-card__detail { color: #64748b; }
.flow-card__actions { display: flex; gap: 2px; }
.flow-card__expand { padding: 0 16px 16px; }
.flow-connector { display: flex; align-items: center; justify-content: center; width: 32px; color: #cbd5e1; font-size: 18px; align-self: center; }
</style>
