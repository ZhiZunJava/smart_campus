<template>
  <div class="affair-timeline">
    <el-timeline>
      <el-timeline-item
        v-for="item in actions"
        :key="item.actionId"
        :timestamp="item.createTime"
        :type="timelineType(item.actionResult || item.actionType)"
        :hollow="item.actionType === 'SUBMIT'"
      >
        <div class="timeline-node">
          <div class="timeline-node__header">
            <strong>{{ item.reviewerName || '系统' }}</strong>
            <el-tag :type="tagType(item.actionResult || item.actionType)" size="small" effect="plain">
              {{ actionLabel(item.actionResult || item.actionType) }}
            </el-tag>
          </div>
          <p v-if="item.stepName" class="timeline-node__step">{{ item.stepName }}</p>
          <p v-if="item.commentText" class="timeline-node__comment">{{ item.commentText }}</p>
        </div>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-if="!actions.length" description="暂无审批记录" :image-size="60" />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  actions: any[]
}>()

function timelineType(action: string) {
  if (action === 'APPROVE') return 'success'
  if (action === 'REJECT') return 'danger'
  if (action === 'CANCEL') return 'info'
  return 'primary'
}

function tagType(action: string) {
  if (action === 'APPROVE') return 'success'
  if (action === 'REJECT') return 'danger'
  if (action === 'CANCEL') return 'info'
  return ''
}

function actionLabel(action: string) {
  const map: Record<string, string> = {
    SUBMIT: '提交申请',
    APPROVE: '审核通过',
    REJECT: '已驳回',
    CANCEL: '已撤回',
  }
  return map[action] || action
}
</script>

<style scoped>
.affair-timeline {
  padding: 4px 0;
}
.timeline-node {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.timeline-node__header {
  display: flex;
  align-items: center;
  gap: 10px;
}
.timeline-node__step {
  margin: 0;
  color: #94a3b8;
  font-size: 12px;
}
.timeline-node__comment {
  margin: 0;
  color: #506173;
  line-height: 1.6;
  background: #f8fafc;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}
</style>
