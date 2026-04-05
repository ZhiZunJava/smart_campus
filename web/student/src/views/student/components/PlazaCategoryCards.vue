<template>
  <section class="plaza-categories">
    <button
      v-for="bucket in buckets"
      :key="bucket.type"
      class="category-card"
      :class="{ 'is-active': activeType === bucket.type }"
      @click="$emit('category-click', bucket.type)"
    >
      <i :class="bucket.icon" class="category-icon"></i>
      <span class="category-label">{{ bucket.label }}</span>
      <span class="category-count" :class="{ 'is-zero': bucket.count === 0 }">
        {{ bucket.count }}
      </span>
    </button>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  buckets: Array<{ type: string; label: string; count: number; icon: string }>
  activeType: string
}>()

defineEmits<{
  'category-click': [type: string]
}>()
</script>

<style scoped>
.plaza-categories {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.category-card {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
  background: #fff;
  cursor: pointer;
  transition: all 0.25s ease;
  outline: none;
  font-size: 14px;
  color: #606266;
}

.category-card:hover {
  border-color: #c6e2ff;
  background: #f0f7ff;
  color: #303133;
}

.category-card:focus-visible {
  box-shadow: 0 0 0 2px var(--brand-primary, #266fcb);
}

.category-card.is-active {
  border-color: var(--brand-primary, #266fcb);
  background: var(--el-color-primary-light-9, #ecf5ff);
  color: var(--brand-primary, #266fcb);
  font-weight: 500;
}

.category-icon {
  font-size: 16px;
}

.category-label {
  white-space: nowrap;
}

.category-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 22px;
  height: 22px;
  padding: 0 6px;
  background: var(--el-color-primary-light-9, #ecf5ff);
  color: var(--brand-primary, #266fcb);
  border-radius: 11px;
  font-size: 12px;
  font-weight: 600;
}

.category-count.is-zero {
  background: #f4f4f5;
  color: #909399;
}

.is-active .category-count {
  background: var(--brand-primary, #266fcb);
  color: #fff;
}

@media (max-width: 768px) {
  .plaza-categories {
    overflow-x: auto;
    flex-wrap: nowrap;
    -webkit-overflow-scrolling: touch;
  }
  .category-card {
    flex-shrink: 0;
  }
}
</style>
