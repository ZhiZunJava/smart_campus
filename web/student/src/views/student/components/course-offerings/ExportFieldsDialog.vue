<template>
  <el-dialog
    :model-value="visible"
    title="导出字段"
    width="560px"
    destroy-on-close
    append-to-body
    modal-append-to-body
    class="course-offerings-dialog"
    modal-class="course-offerings-dialog-modal"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="export-intro">
      <div class="export-intro__title">导出说明</div>
      <div class="export-intro__desc">
        导出范围：
        <span class="export-scope">
          {{ selectedCount ? `已选 ${selectedCount} 条` : '全部数据（按当前筛选）' }}
        </span>
      </div>
    </div>

    <div class="export-actions-top">
      <el-button link type="primary" size="small" @click="toggleAll(true)">全选</el-button>
      <el-button link type="primary" size="small" @click="invertAll">反选</el-button>
      <el-button link type="danger" size="small" @click="toggleAll(false)">取消选中</el-button>
      <div class="export-picked">已选 <span class="export-picked__n">{{ selectedFieldCount }}</span> 个字段</div>
    </div>

    <div class="export-groups">
      <div v-for="g in fieldGroups" :key="g.key" class="export-group">
        <div class="export-group__title">{{ g.label }}</div>
        <div class="export-field-grid">
          <el-checkbox v-for="ef in g.fields" :key="ef.key" v-model="fieldSelection[ef.key]">
            {{ ef.label }}
          </el-checkbox>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="exporting" @click="handleExport">
        导出 {{ selectedFieldCount }} 个字段
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, watch } from 'vue'

type ExportField = { key: string; label: string }
type ExportGroup = { key: string; label: string; fields: ExportField[] }

const props = defineProps<{
  visible: boolean
  selectedCount: number
  allExportFields: ExportField[]
  fieldGroups: ExportGroup[]
  exporting: boolean
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  export: [fieldKeys: string[]]
}>()

const fieldSelection = reactive<Record<string, boolean>>({})

const selectedFieldCount = computed(() =>
  props.allExportFields.filter(ef => fieldSelection[ef.key]).length
)

function toggleAll(val: boolean) {
  props.allExportFields.forEach(ef => { fieldSelection[ef.key] = val })
}

function invertAll() {
  props.allExportFields.forEach(ef => { fieldSelection[ef.key] = !fieldSelection[ef.key] })
}

function handleExport() {
  const keys = props.allExportFields.filter(ef => fieldSelection[ef.key]).map(ef => ef.key)
  emit('export', keys)
}

watch(() => props.visible, (val) => {
  if (val) {
    props.allExportFields.forEach(ef => { fieldSelection[ef.key] = true })
  }
})
</script>

<style scoped>
.export-intro {
  padding: 12px 14px;
  border: 1px solid #e2e8f0;
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.06), rgba(37, 99, 235, 0.02));
  border-radius: 10px;
  margin-bottom: 14px;
}

.export-intro__title {
  font-weight: 900;
  color: #0f172a;
  font-size: 14px;
}

.export-intro__desc {
  margin-top: 6px;
  color: #475569;
  font-size: 12px;
}

.export-scope {
  color: #0f172a;
  font-weight: 900;
}

.export-actions-top {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
  align-items: center;
  flex-wrap: wrap;
}

.export-picked {
  margin-left: auto;
  color: #475569;
  font-size: 12px;
}

.export-picked__n {
  color: #0f172a;
  font-weight: 900;
}

.export-groups {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.export-group__title {
  font-weight: 900;
  color: #0f172a;
  font-size: 13px;
  margin: 4px 0 10px;
}

.export-field-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

@media (max-width: 640px) {
  .export-field-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
