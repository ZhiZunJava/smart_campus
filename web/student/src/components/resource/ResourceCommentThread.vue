<template>
  <div class="comment-thread">
    <article v-for="item in comments" :key="item.commentId" class="comment-thread__item" :class="{ 'is-reply': depth > 0 }">
      <div class="comment-thread__avatar">
        <img v-if="item.userAvatar" :src="item.userAvatar" alt="avatar">
        <div v-else class="avatar-fallback">{{ getAvatarText(item.userName, item.userId) }}</div>
      </div>
      <div class="comment-thread__content">
        <div class="comment-thread__head">
          <div class="comment-thread__user">
            <strong>{{ item.userName || `用户${item.userId}` }}</strong>
            <span v-if="depth > 0 && replyTargetLabel(item)" class="reply-target">
              回复 <span>{{ replyTargetLabel(item) }}</span>
            </span>
            <span class="comment-time">{{ item.createTime }}</span>
          </div>
          <div class="comment-actions">
            <el-button
              text
              size="small"
              class="comment-action-btn"
              :class="{ 'is-active': item.liked }"
              @click="$emit('like', item)"
            >
              <i :class="item.liked ? 'ri-thumb-up-fill' : 'ri-thumb-up-line'"></i>
              {{ item.likeCount || 0 }}
            </el-button>
            <el-button text size="small" class="comment-action-btn" @click="$emit('reply', item)">
              <i class="ri-chat-1-line"></i>
              回复
            </el-button>
          </div>
        </div>
        <p class="comment-text">{{ item.content }}</p>

        <div v-if="item.replies?.length" class="comment-replies">
          <ResourceCommentThread
            :comments="item.replies"
            :depth="depth + 1"
            :user-name-map="mergedUserNameMap(item)"
            @reply="$emit('reply', $event)"
            @like="$emit('like', $event)"
          />
        </div>
      </div>
    </article>
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'ResourceCommentThread' })

const props = withDefaults(defineProps<{
  comments: any[]
  depth?: number
  userNameMap?: Record<string, string>
}>(), {
  depth: 0,
  userNameMap: () => ({}),
})

defineEmits<{
  (e: 'reply', item: any): void
  (e: 'like', item: any): void
}>()

function getAvatarText(name?: string, userId?: number | string) {
  const source = String(name || (userId ? `用户${userId}` : '访客')).trim()
  return source.slice(0, 1).toUpperCase()
}

function replyTargetLabel(item: any) {
  const parentId = String(item.parentId || '')
  if (!parentId || parentId === '0') return ''
  return props.userNameMap[parentId] || ''
}

function mergedUserNameMap(item: any) {
  return {
    ...props.userNameMap,
    [String(item.commentId)]: item.userName || `用户${item.userId || ''}`,
  }
}
</script>

