<template>
  <el-dialog
    :model-value="visible"
    title="自定义查询条件"
    width="680px"
    destroy-on-close
    append-to-body
    modal-append-to-body
    class="course-offerings-dialog"
    modal-class="course-offerings-dialog-modal"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="query-config-body">
      <div class="query-config-left">
        <div class="query-config-header">
          <span>全部字段</span>
          <span>
            <el-button link type="primary" size="small" @click="toggleAll(true)">全选</el-button>
            <el-button link type="primary" size="small" @click="invertAll">反选</el-button>
          </span>
        </div>
        <div class="query-config-search">
          <el-input v-model="keyword" clearable placeholder="搜索字段…" />
        </div>
        <el-scrollbar class="query-config-scroll">
          <div class="query-config-list">
            <el-checkbox
              v-for="field in filteredFields"
              :key="field.key"
              v-model="tempKeys[field.key]"
              :disabled="field.key === 'termId'"
            >
              {{ field.label }}
            </el-checkbox>
          </div>
        </el-scrollbar>
      </div>
      <div class="query-config-right">
        <div class="query-config-header">
          <span>已选 ({{ activeCount }})</span>
        </div>
        <el-scrollbar class="query-config-scroll">
          <div class="query-config-selected">
            <div v-for="(field, idx) in selectedFields" :key="field.key" class="selected-item">
              <span class="selected-item__idx">{{ idx + 1 }}</span>
              <el-tag closable @close="tempKeys[field.key] = false">
                {{ field.label }}
              </el-tag>
            </div>
            <span v-if="!selectedFields.length" class="empty-hint">请从左侧选择字段</span>
          </div>
        </el-scrollbar>
      </div>
    </div>
    <template #footer>
      <el-button @click="resetToDefault">恢复默认</el-button>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import type { QueryField } from '../../composables/useFilterOptions'

const props = defineProps<{
  visible: boolean
  allQueryFields: QueryField[]
  activeQueryKeys: string[]
  defaultQueryKeys: string[]
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  save: [keys: string[]]
}>()

const keyword = ref('')
const tempKeys = reactive<Record<string, boolean>>({})

const activeCount = computed(() => Object.values(tempKeys).filter(Boolean).length)

const filteredFields = computed(() => {
  const kw = keyword.value.trim()
  if (!kw) return props.allQueryFields
  return props.allQueryFields.filter(f => f.label.includes(kw) || f.key.includes(kw))
})

const selectedFields = computed(() =>
  props.allQueryFields.filter(f => tempKeys[f.key])
)

function toggleAll(val: boolean) {
  props.allQueryFields.forEach(f => {
    if (f.key === 'termId') { tempKeys[f.key] = true; return }
    tempKeys[f.key] = val
  })
}

function invertAll() {
  props.allQueryFields.forEach(f => {
    if (f.key === 'termId') { tempKeys[f.key] = true; return }
    tempKeys[f.key] = !tempKeys[f.key]
  })
}

function resetToDefault() {
  props.allQueryFields.forEach(f => {
    tempKeys[f.key] = f.key === 'termId' ? true : props.defaultQueryKeys.includes(f.key)
  })
}

function handleSave() {
  const keys = Array.from(
    new Set(['termId', ...props.allQueryFields.filter(f => tempKeys[f.key]).map(f => f.key)])
  )
  emit('save', keys)
}

watch(() => props.visible, (val) => {
  if (val) {
    props.allQueryFields.forEach(f => {
      tempKeys[f.key] = props.activeQueryKeys.includes(f.key)
    })
  } else {
    keyword.value = ''
  }
})
</script>

<style scoped>
.query-config-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  min-height: 320px;
}

.query-config-left,
.query-config-right {
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
}

.query-config-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  font-size: 13px;
  font-weight: 600;
  color: #334155;
}

.query-config-search {
  padding: 12px 14px 0;
}

.query-config-list {
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.query-config-selected {
  padding: 12px 14px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  align-content: start;
  gap: 10px 12px;
}

.query-config-scroll {
  height: 380px;
}

.selected-item {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.selected-item :deep(.el-tag) {
  width: 100%;
  justify-content: space-between;
  overflow: hidden;
}

.selected-item__idx {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(37, 99, 235, 0.12);
  color: #2563eb;
  font-weight: 900;
  font-size: 12px;
}

.empty-hint {
  color: #94a3b8;
  font-size: 13px;
  grid-column: 1 / -1;
}

@media (max-width: 640px) {
  .query-config-body {
    grid-template-columns: 1fr;
  }

  .query-config-selected {
    grid-template-columns: 1fr;
  }
}
</style>
