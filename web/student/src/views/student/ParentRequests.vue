<template>
  <div class="portal-page parent-requests-page">
    <div class="page-header">
      <h2>家长绑定请求</h2>
      <p>以下是家长发来的绑定请求，确认后家长将可以查看您的课程、课表等信息。</p>
    </div>

    <div class="tab-bar">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-btn"
        :class="{ active: activeTab === tab.value }"
        @click="switchTab(tab.value)"
      >
        {{ tab.label }}
        <span v-if="tab.count > 0" class="tab-badge">{{ tab.count }}</span>
      </button>
    </div>

    <div v-if="loading" class="list-loading">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="requestList.length" class="request-card-list">
      <TransitionGroup name="req-list">
        <div v-for="req in requestList" :key="req.relId" class="request-card">
          <div class="request-card__icon">
            <i class="ri-parent-line"></i>
          </div>
          <div class="request-card__info">
            <div class="request-card__name-row">
              <span class="request-card__name">{{ req.parentName }}</span>
              <el-tag
                v-if="req.status === '0'"
                type="warning"
                effect="light"
                size="small"
                round
              >待确认</el-tag>
              <el-tag
                v-else-if="req.status === '1'"
                type="success"
                effect="light"
                size="small"
                round
              >已接受</el-tag>
              <el-tag
                v-else-if="req.status === '2'"
                type="info"
                effect="light"
                size="small"
                round
              >已拒绝</el-tag>
            </div>
            <div class="request-card__meta">
              <span>手机：{{ req.parentPhone || '--' }}</span>
              <span>关系：{{ req.relationType || '--' }}</span>
              <span>请求时间：{{ formatTime(req.createTime) }}</span>
            </div>
          </div>
          <div class="request-card__actions" v-if="req.status === '0'">
            <el-button
              type="primary"
              size="small"
              :loading="req._accepting"
              @click="handleAccept(req)"
            >接受</el-button>
            <el-popconfirm
              title="确认拒绝该家长的绑定请求吗？"
              confirm-button-text="确认拒绝"
              cancel-button-text="取消"
              @confirm="handleReject(req)"
            >
              <template #reference>
                <el-button
                  plain
                  size="small"
                  :loading="req._rejecting"
                >拒绝</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </TransitionGroup>
    </div>

    <el-empty v-else :description="emptyText" :image-size="120" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentParentRequests, acceptParentRequest, rejectParentRequest } from '@/api/portal'

const loading = ref(false)
const requestList = ref<any[]>([])
const activeTab = ref('0')
const allRequests = ref<any[]>([])

const tabs = computed(() => [
  { label: '待确认', value: '0', count: allRequests.value.filter(r => r.status === '0').length },
  { label: '已接受', value: '1', count: allRequests.value.filter(r => r.status === '1').length },
  { label: '已拒绝', value: '2', count: allRequests.value.filter(r => r.status === '2').length },
])

const emptyText = computed(() => {
  if (activeTab.value === '0') return '暂无待确认的绑定请求'
  if (activeTab.value === '1') return '暂无已接受的绑定记录'
  return '暂无已拒绝的记录'
})

function formatTime(val: any) {
  if (!val) return '--'
  try {
    const d = new Date(val)
    const pad = (n: number) => String(n).padStart(2, '0')
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
  } catch {
    return String(val)
  }
}

async function loadRequests() {
  loading.value = true
  try {
    // Load all statuses
    const res = await getStudentParentRequests('')
    allRequests.value = (res.data || []).map((item: any) => ({
      ...item,
      _accepting: false,
      _rejecting: false,
    }))
    filterByTab()
  } finally {
    loading.value = false
  }
}

function filterByTab() {
  requestList.value = allRequests.value.filter(r => r.status === activeTab.value)
}

function switchTab(val: string) {
  activeTab.value = val
  filterByTab()
}

async function handleAccept(req: any) {
  req._accepting = true
  try {
    const res = await acceptParentRequest(req.relId)
    if (res.code === 200) {
      ElMessage.success(res.msg || '已接受绑定请求')
      await loadRequests()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (err: any) {
    ElMessage.error(err?.message || '操作失败')
  } finally {
    req._accepting = false
  }
}

async function handleReject(req: any) {
  req._rejecting = true
  try {
    const res = await rejectParentRequest(req.relId)
    if (res.code === 200) {
      ElMessage.success(res.msg || '已拒绝绑定请求')
      await loadRequests()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (err: any) {
    ElMessage.error(err?.message || '操作失败')
  } finally {
    req._rejecting = false
  }
}

onMounted(loadRequests)
</script>

<style scoped>
.parent-requests-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 28px 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
}

.page-header p {
  font-size: 14px;
  color: #909399;
  margin: 0;
  line-height: 1.6;
}

.tab-bar {
  display: flex;
  gap: 4px;
  margin-bottom: 20px;
  background: #f4f4f5;
  border-radius: 10px;
  padding: 4px;
}

.tab-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 16px;
  border: none;
  background: transparent;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-btn:hover {
  color: #303133;
}

.tab-btn.active {
  background: #fff;
  color: #303133;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.tab-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  background: #e6a23c;
  color: #fff;
}

.tab-btn.active .tab-badge {
  background: #409eff;
}

.list-loading {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #ebeef5;
}

.request-card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.request-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #ebeef5;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.25s ease;
}

.request-card:hover {
  border-color: #c6e2ff;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.08);
}

.request-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #a78bfa, #7c3aed);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  flex-shrink: 0;
}

.request-card__info {
  flex: 1;
  min-width: 0;
}

.request-card__name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.request-card__name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.request-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  font-size: 13px;
  color: #909399;
}

.request-card__actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

/* Transitions */
.req-list-enter-active,
.req-list-leave-active {
  transition: all 0.35s ease;
}
.req-list-enter-from {
  opacity: 0;
  transform: translateY(16px);
}
.req-list-leave-to {
  opacity: 0;
  transform: translateX(-16px);
}

@media (max-width: 600px) {
  .request-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .request-card__meta {
    flex-direction: column;
    gap: 4px;
  }

  .request-card__actions {
    align-self: flex-end;
  }

  .tab-bar {
    flex-wrap: wrap;
  }
}
</style>
