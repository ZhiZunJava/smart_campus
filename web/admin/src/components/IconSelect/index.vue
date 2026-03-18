<template>
  <div class="icon-body">
    <el-input
      v-model="iconName"
      class="icon-search"
      clearable
      placeholder="搜索 Remix Icon 名称"
      @clear="handleSearchClear"
      @input="handleSearchInput"
    >
      <template #prefix>
        <el-icon><Search /></el-icon>
      </template>
    </el-input>

    <div ref="scrollContainerRef" class="icon-list" @scroll="handleScroll">
      <div class="icon-grid">
        <button
          v-for="item in visibleIcons"
          :key="item"
          type="button"
          class="icon-card"
          :class="{ active: resolvedActiveIcon === item }"
          @click="selectedIcon(item)"
        >
          <svg-icon :icon-class="item" class-name="icon-card__icon" />
          <span class="icon-card__name">{{ item }}</span>
        </button>
      </div>

      <div v-if="loadingMore" class="icon-loading">加载更多图标中...</div>
      <el-empty v-else-if="!visibleIcons.length" description="没有匹配到图标" :image-size="72" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'
import iconSource from './requireIcons'
import { resolveMenuIcon } from '@/utils/menuIcon'

const PAGE_SIZE = 120

const props = defineProps({
  activeIcon: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['selected'])

const iconName = ref('')
const scrollContainerRef = ref<HTMLElement | null>(null)
const page = ref(1)
const loadingMore = ref(false)

const resolvedActiveIcon = computed(() => resolveMenuIcon(props.activeIcon))
const filteredIcons = computed(() => {
  const keyword = iconName.value.trim().toLowerCase()
  if (!keyword) return iconSource
  return iconSource.filter((item) => item.toLowerCase().includes(keyword))
})

const visibleIcons = computed(() => filteredIcons.value.slice(0, page.value * PAGE_SIZE))

function resetPagination() {
  page.value = 1
  loadingMore.value = false
  nextTick(() => {
    if (scrollContainerRef.value) {
      scrollContainerRef.value.scrollTop = 0
    }
  })
}

function handleSearchInput(): void {
  resetPagination()
}

function handleSearchClear(): void {
  resetPagination()
}

function handleScroll(): void {
  const el = scrollContainerRef.value
  if (!el || loadingMore.value) return
  const nearBottom = el.scrollTop + el.clientHeight >= el.scrollHeight - 80
  const hasMore = visibleIcons.value.length < filteredIcons.value.length
  if (!nearBottom || !hasMore) return
  loadingMore.value = true
  window.setTimeout(() => {
    page.value += 1
    loadingMore.value = false
  }, 16)
}

function selectedIcon(name: string): void {
  emit('selected', name)
  document.body.click()
}

function reset(): void {
  iconName.value = ''
  resetPagination()
}

defineExpose({
  reset
})
</script>

<style lang="scss" scoped>
.icon-body {
  width: 100%;
  padding: 12px;
}

.icon-search {
  margin-bottom: 10px;
}

.icon-list {
  height: 360px;
  overflow: auto;
  padding-right: 4px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px;
}

.icon-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 86px;
  padding: 10px 8px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  background: var(--el-bg-color);
  color: var(--el-text-color-primary);
  cursor: pointer;
  transition: border-color .2s ease, background-color .2s ease, color .2s ease;
}

.icon-card:hover {
  border-color: var(--el-color-primary-light-5);
  background: var(--el-fill-color-extra-light);
}

.icon-card.active {
  background: var(--el-color-primary);
  border-color: var(--el-color-primary);
  color: #ffffff;
}

.icon-card__icon {
  font-size: 18px;
}

.icon-card__name {
  width: 100%;
  font-size: 11px;
  line-height: 1.4;
  text-align: center;
  word-break: break-all;
}

.icon-loading {
  padding: 10px 0 4px;
  text-align: center;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
</style>
