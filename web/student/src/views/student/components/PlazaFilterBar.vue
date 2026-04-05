<template>
  <div class="plaza-filter-bar">
    <el-input
      :model-value="search"
      @update:model-value="$emit('update:search', $event)"
      placeholder="搜索任务名称..."
      clearable
      class="filter-search"
      :prefix-icon="SearchIcon"
    />
    <el-select
      :model-value="taskType"
      @update:model-value="$emit('update:taskType', $event)"
      placeholder="任务类型"
      clearable
      class="filter-select"
    >
      <el-option
        v-for="opt in typeOptions"
        :key="opt.value"
        :label="opt.label"
        :value="opt.value"
      />
    </el-select>
    <el-select
      :model-value="status"
      @update:model-value="$emit('update:status', $event)"
      placeholder="任务状态"
      clearable
      class="filter-select"
    >
      <el-option
        v-for="opt in statusOptions"
        :key="opt.value"
        :label="opt.label"
        :value="opt.value"
      />
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { Search as SearchIcon } from '@element-plus/icons-vue'

defineProps<{
  search: string
  taskType: string
  status: string
  typeOptions: Array<{ label: string; value: string }>
  statusOptions: Array<{ label: string; value: string }>
}>()

defineEmits<{
  'update:search': [value: string]
  'update:taskType': [value: string]
  'update:status': [value: string]
}>()
</script>

<style scoped>
.plaza-filter-bar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.filter-search {
  flex: 1;
  min-width: 200px;
  max-width: 360px;
}

.filter-select {
  width: 140px;
}

@media (max-width: 768px) {
  .filter-search {
    min-width: 100%;
    max-width: none;
  }
  .filter-select {
    flex: 1;
    min-width: 0;
  }
}
</style>
