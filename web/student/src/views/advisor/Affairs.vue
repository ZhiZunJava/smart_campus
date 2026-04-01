<template>
  <AffairWorkbench
    portal-role="advisor"
    :allow-apply="false"
    :allow-review="true"
    :initial-category-code="categoryCode"
    hero-title="辅导员审核与学籍跟踪"
    hero-subtitle="辅导员端聚合学生事务审核待办，重点覆盖请销假、奖助贷、贫困认定、综测测评以及学籍异动的审批与跟踪，支持在线查看完整申请轨迹。"
  />
</template>

<script setup lang="ts">
import AffairWorkbench from '@/components/affair/AffairWorkbench.vue'

defineProps<{
  categoryCode?: string
}>()
</script>
