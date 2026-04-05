<template>
  <article
    class="task-card"
    :class="[
      `task-card--${variant}`,
      { 'task-card--urgent': isUrgent }
    ]"
    @click="$emit('click')"
  >
    <div class="task-card__icon-wrap" :class="iconClass">
      <i :class="taskIcon"></i>
    </div>
    <div class="task-card__main">
      <div class="task-card__head">
        <div class="task-card__title">{{ task.title }}</div>
        <span v-if="variant === 'todo' && isUrgent" class="priority-dot"></span>
        <el-tag
          :type="task.tagType || 'info'"
          effect="plain"
          size="small"
        >
          {{ task.tag }}
        </el-tag>
      </div>
      <div class="task-card__desc" v-if="task.desc">{{ task.desc }}</div>
      <div class="task-card__meta" v-if="task.meta?.length">
        <span v-for="meta in task.meta" :key="meta">{{ meta }}</span>
      </div>
      <div v-if="task.recommendationReason" class="task-card__reason">
        {{ task.recommendationReason }}
      </div>
    </div>
    <div class="task-card__actions" @click.stop>
      <el-button
        :type="variant === 'todo' ? 'primary' : 'primary'"
        :plain="variant !== 'todo'"
        @click="$emit('click')"
      >
        {{ task.actionLabel }}
      </el-button>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  task: any
  variant: 'todo' | 'recommended' | 'history'
}>()

defineEmits<{ click: [] }>()

const isUrgent = computed(() => {
  if (props.variant !== 'todo') return false
  const meta = (props.task.meta || []).join(' ')
  return meta.includes('今天') || meta.includes('紧急') || meta.includes('即将') || meta.includes('截止')
})

const ICON_MAP: Record<string, { icon: string; bg: string }> = {
  exam: { icon: 'ri-file-paper-2-line', bg: 'task-icon--warning' },
  resume: { icon: 'ri-play-circle-line', bg: 'task-icon--primary' },
  homework: { icon: 'ri-edit-box-line', bg: 'task-icon--success' },
  practice: { icon: 'ri-flask-line', bg: 'task-icon--info' },
  reading: { icon: 'ri-book-read-line', bg: 'task-icon--info' },
  course: { icon: 'ri-book-open-line', bg: 'task-icon--primary' },
  wrongbook: { icon: 'ri-error-warning-line', bg: 'task-icon--danger' },
  record: { icon: 'ri-file-list-3-line', bg: 'task-icon--info' },
}

const taskActionType = computed(() =>
  String(props.task?.action?.type || props.task?.taskType || '').toLowerCase(),
)

const taskIcon = computed(() =>
  ICON_MAP[taskActionType.value]?.icon || 'ri-task-line',
)

const iconClass = computed(() =>
  ICON_MAP[taskActionType.value]?.bg || 'task-icon--primary',
)
</script>

<style scoped>
.task-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: all 0.25s ease;
  cursor: pointer;
  position: relative;
}

.task-card:hover {
  box-shadow: 0 8px 24px rgba(38, 111, 203, 0.08);
  transform: translateY(-2px);
  border-color: #c6e2ff;
}

.task-card--todo {
  border-left: 4px solid var(--el-color-warning, #e6a23c);
  background: linear-gradient(90deg, var(--el-color-warning-light-9, #fdf6ec) 0%, #fff 10%);
}

.task-card--todo.task-card--urgent {
  border-left-color: var(--el-color-danger, #f56c6c);
  background: linear-gradient(90deg, var(--el-color-danger-light-9, #fef0f0) 0%, #fff 10%);
}

.task-card--history {
  background: #fafafa;
  border-color: #e4e7ed;
}

.task-card--history:hover {
  background: #fff;
}

.task-card__icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 8px;
  font-size: 18px;
  flex-shrink: 0;
}

.task-icon--primary {
  background: rgba(38, 111, 203, 0.1);
  color: #266fcb;
}

.task-icon--success {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.task-icon--warning {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.task-icon--danger {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.task-icon--info {
  background: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.priority-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--el-color-danger, #f56c6c);
  animation: pulse 1.5s ease-in-out infinite;
  flex-shrink: 0;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.task-card__main {
  flex: 1;
  min-width: 0;
}

.task-card__head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.task-card__title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-card__desc {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.task-card__meta {
  display: flex;
  gap: 14px;
  font-size: 12px;
  color: #909399;
  flex-wrap: wrap;
}

.task-card__meta span::before {
  content: '';
  display: inline-block;
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: #c0c4cc;
  margin-right: 6px;
  vertical-align: middle;
}

.task-card__meta span:first-child::before {
  display: none;
}

.task-card__reason {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.5;
  color: var(--brand-primary, #266fcb);
  padding: 4px 8px;
  background: rgba(38, 111, 203, 0.06);
  border-radius: 3px;
  display: inline-block;
}

.task-card__actions {
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .task-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .task-card__main {
    margin-right: 0;
    width: 100%;
  }
  .task-card__actions {
    width: 100%;
  }
  .task-card__actions .el-button {
    width: 100%;
  }
}
</style>
