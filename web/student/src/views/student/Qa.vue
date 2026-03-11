<template>
  <div class="portal-page qa-page">
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card class="portal-card">
          <template #header><span>问答会话</span></template>
          <el-button type="primary" size="small" @click="createSession">新建会话</el-button>
          <el-menu class="mt16" :default-active="String(activeSessionId)">
            <el-menu-item v-for="item in sessions" :key="item.sessionId" :index="String(item.sessionId)" @click="selectSession(item)">
              {{ item.sessionTitle || ('会话' + item.sessionId) }}
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="portal-card">
          <template #header><span>消息记录</span></template>
          <div class="message-list">
            <div v-for="item in messages" :key="item.messageId" class="message-item" :class="item.roleType">
              <div class="bubble-role">{{ item.roleType === 'user' ? '我' : item.roleType === 'assistant' ? '助手' : '系统' }}</div>
              <div class="bubble-content">{{ item.content }}</div>
              <div v-if="item.referenceSource" class="bubble-source">来源：{{ item.referenceSource }}</div>
              <div v-if="item.roleType === 'assistant'" class="bubble-actions">
                <el-button link type="primary" @click="feedback(item, 'helpful')">有帮助</el-button>
                <el-button link type="danger" @click="feedback(item, 'unhelpful')">无帮助</el-button>
              </div>
            </div>
          </div>
          <el-input v-model="messageForm.content" type="textarea" rows="4" placeholder="请输入问题，AI 将自动回复" class="mt16" />
          <div class="mt16 actions"><el-button type="primary" :loading="asking" @click="sendMessage">发送消息</el-button></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addQaFeedback, addQaSession, askQa, listQaMessage, listQaSession } from '@/api/portal'
import usePortalUserStore from '@/store/user'
const userStore = usePortalUserStore()
const sessions=ref<any[]>([]), messages=ref<any[]>([]), activeSessionId=ref<number|undefined>()
const messageForm=ref<any>({ content:'' })
const asking = ref(false)
async function loadSessions(){ const userId = userStore.user?.userId; if(!userId) return; const res=await listQaSession({ pageNum:1, pageSize:100, userId }); sessions.value=res.rows||[]; if(!activeSessionId.value && sessions.value.length){ selectSession(sessions.value[0]) } }
async function selectSession(item:any){ activeSessionId.value=item.sessionId; const res=await listQaMessage({ pageNum:1, pageSize:200, sessionId:item.sessionId }); messages.value=res.rows||[] }
async function createSession(){ const userId = userStore.user?.userId; if(!userId){ ElMessage.warning('当前未获取到登录用户'); return } await addQaSession({ userId, courseId:null, sessionTitle:'新问答会话', sourceType:'course', status:'0' }); ElMessage.success('会话创建成功'); loadSessions() }
async function sendMessage(){ const userId = userStore.user?.userId; if(!userId){ ElMessage.warning('当前未获取到登录用户'); return } if(!messageForm.value.content){ ElMessage.warning('请输入内容'); return } asking.value = true; try{ const res = await askQa({ sessionId: activeSessionId.value, userId, question: messageForm.value.content, sessionTitle:'新问答会话' }); activeSessionId.value = res.sessionId || res.data?.sessionId || activeSessionId.value; messageForm.value.content=''; await loadSessions(); if(activeSessionId.value){ await selectSession({ sessionId: activeSessionId.value }) } } finally { asking.value = false } }
async function feedback(item:any, type:string){ await addQaFeedback({ messageId: item.messageId, userId: userStore.user?.userId, feedbackType: type, feedbackContent: '' }); ElMessage.success(type === 'helpful' ? '感谢反馈' : '已记录问题反馈') }
loadSessions()
</script>
<style scoped>
.mt16{margin-top:16px}.actions{display:flex;justify-content:flex-end}.message-list{max-height:540px;overflow:auto;display:flex;flex-direction:column;gap:12px}.message-item{padding:12px 14px;border-radius:14px;max-width:85%}.message-item.user{align-self:flex-end;background:#d5e7ff}.message-item.assistant{align-self:flex-start;background:#f6f8fb}.message-item.system{align-self:center;background:#eef2f6}.bubble-role{font-weight:700;margin-bottom:6px}.bubble-content{line-height:1.8;color:#344054}.bubble-source{margin-top:8px;font-size:12px;color:#7b8794}.bubble-actions{margin-top:6px;display:flex;gap:8px}
</style>
