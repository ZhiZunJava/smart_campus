<template>
  <el-dialog
    :model-value="visible"
    title="页面设置"
    width="480px"
    destroy-on-close
    append-to-body
    modal-append-to-body
    class="course-offerings-dialog"
    modal-class="course-offerings-dialog-modal"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="page-settings">
      <div class="ps-section">
        <div class="ps-title">显示列（{{ temp.visibleColumns.length }}/{{ allColumns.length }}）</div>
        <el-checkbox-group v-model="temp.visibleColumns" class="ps-cols">
          <el-checkbox v-for="col in allColumns" :key="col.key" :label="col.key">{{ col.label }}</el-checkbox>
        </el-checkbox-group>
      </div>

      <div class="ps-section">
        <div class="ps-title">列表显示</div>
        <div class="ps-inline">
          <el-checkbox v-model="temp.showIndex">表格序号</el-checkbox>
        </div>
      </div>

      <div class="ps-section">
        <div class="ps-title">显示模式</div>
        <el-radio-group v-model="temp.density" class="ps-radio-group">
          <el-radio-button label="compact">紧凑</el-radio-button>
          <el-radio-button label="medium">适中</el-radio-button>
          <el-radio-button label="loose">宽松</el-radio-button>
        </el-radio-group>
      </div>

      <div class="ps-section">
        <div class="ps-title">列宽策略</div>
        <el-radio-group v-model="temp.columnWidth" class="ps-radio-group">
          <el-radio-button label="fit">适应内容</el-radio-button>
          <el-radio-button label="fill">铺满表格</el-radio-button>
        </el-radio-group>
      </div>

      <div class="ps-section">
        <div class="ps-title">每页行数</div>
        <el-radio-group v-model="temp.pageSize" class="ps-radio-group ps-radio-group--sizes">
          <el-radio-button v-for="s in [20, 50, 100, 200, 500, 1000]" :key="s" :label="s">{{ s }}</el-radio-button>
        </el-radio-group>
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
import { reactive, watch } from 'vue'

export interface PageSettings {
  visibleColumns: string[]
  showIndex: boolean
  columnWidth: 'fit' | 'fill'
  density: 'compact' | 'medium' | 'loose'
  pageSize: number
}

const props = defineProps<{
  visible: boolean
  settings: PageSettings
  allColumns: { key: string; label: string }[]
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  save: [settings: PageSettings]
}>()

const temp = reactive<PageSettings>({
  visibleColumns: [],
  showIndex: false,
  columnWidth: 'fit',
  density: 'medium',
  pageSize: 20,
})

function resetToDefault() {
  Object.assign(temp, {
    visibleColumns: props.allColumns.map(c => c.key),
    showIndex: false,
    columnWidth: 'fit',
    density: 'medium',
    pageSize: 20,
  })
}

function handleSave() {
  emit('save', { ...temp, visibleColumns: [...temp.visibleColumns] })
}

watch(() => props.visible, (val) => {
  if (val) {
    Object.assign(temp, {
      visibleColumns: [...props.settings.visibleColumns],
      showIndex: props.settings.showIndex,
      columnWidth: props.settings.columnWidth,
      density: props.settings.density,
      pageSize: props.settings.pageSize,
    })
  }
})
</script>

<style scoped>
.page-settings {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.ps-section {
  padding: 12px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: #fff;
}

.ps-title {
  font-weight: 900;
  color: #0f172a;
  font-size: 14px;
}

.ps-cols {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px 14px;
}

.ps-inline {
  margin-top: 10px;
}

.ps-radio-group {
  margin-top: 10px;
}

.ps-radio-group :deep(.el-radio-button__inner) {
  min-width: 64px;
}

.ps-radio-group--sizes :deep(.el-radio-button__inner) {
  min-width: 52px;
}
</style>
