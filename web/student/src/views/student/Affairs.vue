<template>
  <AffairWorkbench
    portal-role="student"
    :allow-apply="true"
    :allow-review="false"
    :initial-category-code="categoryCode"
    hero-title="一站式学生事务办理"
    hero-subtitle="请销假、离返校、奖助贷、勤工助学、活动申请、竞赛申报、综测测评、贫困认定和学籍异动都集中在这里线上办理，学生可实时查看进度与结果。"
  />
</template>

<script setup lang="ts">
import AffairWorkbench from '@/components/affair/AffairWorkbench.vue'

defineProps<{
  categoryCode?: string
}>()
</script>
