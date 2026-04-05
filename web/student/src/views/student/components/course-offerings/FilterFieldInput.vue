<template>
  <div
    class="search-field"
    :class="{
      'search-field--range': field.type === 'range',
      'search-field--term': field.key === 'termId',
    }"
  >
    <label>{{ field.label }}</label>
    <div v-if="field.type === 'range'" class="credit-range">
      <el-input-number
        :model-value="creditsMin"
        :min="0"
        :precision="1"
        :step="0.5"
        :controls="false"
        class="credit-range__input"
        placeholder="最小值"
        @update:model-value="$emit('update:creditsMin', $event)"
        @keyup.enter="$emit('search')"
      />
      <span class="credit-range__separator">~</span>
      <el-input-number
        :model-value="creditsMax"
        :min="0"
        :precision="1"
        :step="0.5"
        :controls="false"
        class="credit-range__input"
        placeholder="最大值"
        @update:model-value="$emit('update:creditsMax', $event)"
        @keyup.enter="$emit('search')"
      />
    </div>
    <el-select
      v-else-if="field.type === 'select'"
      :model-value="modelValue"
      :clearable="field.clearable !== false"
      :filterable="field.filterable !== false"
      :placeholder="field.placeholder"
      style="width: 100%"
      popper-class="course-offerings-popper"
      teleported
      @update:model-value="$emit('update:modelValue', $event)"
      @change="field.key === 'termId' ? $emit('termChange') : undefined"
    >
      <el-option
        v-for="option in options"
        :key="`${field.key}-${option.value}`"
        :label="option.label"
        :value="option.value"
      />
    </el-select>
    <el-input
      v-else
      :model-value="modelValue"
      :placeholder="field.placeholder"
      clearable
      @update:model-value="$emit('update:modelValue', $event)"
      @keyup.enter="$emit('search')"
    />
  </div>
</template>

<script setup lang="ts">
import type { QueryField, OptionItem } from '../../composables/useFilterOptions'

defineProps<{
  field: QueryField
  modelValue?: any
  creditsMin?: number
  creditsMax?: number
  options: OptionItem[]
}>()

defineEmits<{
  'update:modelValue': [value: any]
  'update:creditsMin': [value: number | undefined]
  'update:creditsMax': [value: number | undefined]
  search: []
  termChange: []
}>()
</script>
