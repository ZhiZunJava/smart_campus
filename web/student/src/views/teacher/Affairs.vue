<template>
  <AffairWorkbench
    portal-role="teacher"
    :allow-apply="true"
    :allow-review="true"
    :initial-category-code="categoryCode"
    hero-title="教学协同事务中心"
    hero-subtitle="教师可以发起教材、活动、竞赛、创新创业等事务申请，也可以在线审核学生提交的指导教师节点，协同辅导员完成全流程闭环。"
  />
</template>

<script setup lang="ts">
import AffairWorkbench from '@/components/affair/AffairWorkbench.vue'

defineProps<{
  categoryCode?: string
}>()
</script>
