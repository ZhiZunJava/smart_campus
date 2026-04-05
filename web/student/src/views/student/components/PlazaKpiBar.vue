<template>
  <section class="plaza-kpis">
    <button
      v-for="kpi in kpis"
      :key="kpi.type"
      class="plaza-kpi-card"
      @click="$emit('kpi-click', kpi.type)"
    >
      <div class="kpi-icon-wrap" :class="kpi.iconBg">
        <i :class="kpi.icon"></i>
      </div>
      <div class="kpi-content">
        <div class="kpi-label">{{ kpi.label }}</div>
        <div class="kpi-value" :class="kpi.valueClass">{{ kpi.value }}</div>
        <div class="kpi-sub">{{ kpi.sub }}</div>
      </div>
      <div class="kpi-arrow">
        <i class="ri-arrow-right-s-line"></i>
      </div>
    </button>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  todoCount: number
  homeworkCount: number
  examCount: number
  challengeCount: number
}>()

defineEmits<{
  'kpi-click': [type: string]
}>()

const kpis = computed(() => [
  { type: 'todo', label: '今日待办', value: props.todoCount, sub: '紧急与即将截止', valueClass: 'text-primary', icon: 'ri-calendar-check-line', iconBg: 'kpi-icon--primary' },
  { type: 'homework', label: '本周作业', value: props.homeworkCount, sub: '需本周内提交', valueClass: '', icon: 'ri-edit-box-line', iconBg: 'kpi-icon--success' },
  { type: 'exam', label: '即将考试', value: props.examCount, sub: '近3天内考试', valueClass: '', icon: 'ri-file-paper-2-line', iconBg: 'kpi-icon--warning' },
  { type: 'challenge', label: '开放挑战', value: props.challengeCount, sub: '不依赖课程即可参与', valueClass: '', icon: 'ri-trophy-line', iconBg: 'kpi-icon--danger' },
])
</script>

<style scoped>
.plaza-kpis {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.plaza-kpi-card {
  background: #fff;
  border-radius: 6px;
  border: 1px solid #ebeef5;
  padding: 18px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
  text-align: left;
  outline: none;
}

.plaza-kpi-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transform: translateY(-2px);
  border-color: #c6e2ff;
}

.plaza-kpi-card:focus-visible {
  box-shadow: 0 0 0 2px var(--brand-primary, #266fcb);
}

.kpi-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  font-size: 20px;
  flex-shrink: 0;
}

.kpi-icon--primary {
  background: rgba(38, 111, 203, 0.1);
  color: #266fcb;
}

.kpi-icon--success {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.kpi-icon--warning {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.kpi-icon--danger {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.kpi-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.kpi-label {
  font-size: 14px;
  color: #606266;
}

.kpi-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.kpi-value.text-primary {
  color: var(--brand-primary, #266fcb);
}

.kpi-sub {
  font-size: 12px;
  color: #909399;
}

.kpi-arrow {
  font-size: 20px;
  color: #c0c4cc;
  transition: color 0.2s;
}

.plaza-kpi-card:hover .kpi-arrow {
  color: var(--brand-primary, #266fcb);
}

@media (max-width: 1200px) {
  .plaza-kpis {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .plaza-kpis {
    grid-template-columns: 1fr;
  }
}
</style>
