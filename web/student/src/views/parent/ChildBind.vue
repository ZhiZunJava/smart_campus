<template>
  <div class="portal-page child-bind-page">
    <div class="child-bind-header">
      <div class="header-left">
        <h2>孩子绑定管理</h2>
        <p>通过输入孩子的学号发送绑定请求，学生确认后即可查看孩子的课程、课表等信息。</p>
      </div>
      <el-button type="primary" @click="bindDialogVisible = true">
        <i class="ri-add-line"></i> 绑定新孩子
      </el-button>
    </div>

    <div class="child-bind-filter" v-if="childList.length > 0">
      <el-input
        v-model="searchQuery"
        placeholder="搜索姓名或学号"
        clearable
        prefix-icon="Search"
        style="max-width: 240px"
      />
      <el-select v-model="filterRelation" placeholder="全部关系" clearable style="width: 130px">
        <el-option label="父亲" value="父亲" />
        <el-option label="母亲" value="母亲" />
        <el-option label="祖父" value="祖父" />
        <el-option label="祖母" value="祖母" />
        <el-option label="外祖父" value="外祖父" />
        <el-option label="外祖母" value="外祖母" />
        <el-option label="监护人" value="监护人" />
        <el-option label="其他" value="其他" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="全部状态" clearable style="width: 130px">
        <el-option label="待确认" value="0" />
        <el-option label="已确认" value="1" />
        <el-option label="已拒绝" value="2" />
      </el-select>
      <div class="filter-summary">
        共 <strong>{{ filteredChildren.length }}</strong> / {{ childList.length }} 条记录
      </div>
    </div>

    <div v-if="listLoading" class="child-list-loading">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="filteredChildren.length" class="child-card-list">
      <TransitionGroup name="card-list">
        <div v-for="child in filteredChildren" :key="child.id || child.value" class="child-card" :class="'child-card--status-' + (child.status || '0')">
          <div class="child-card__icon">
            <i class="ri-user-heart-line"></i>
          </div>
          <div class="child-card__info">
            <div class="child-card__name-row">
              <span class="child-card__name">{{ child.studentName || child.label }}</span>
              <el-tag
                v-if="child.status === '1'"
                type="success"
                effect="light"
                size="small"
                round
              >已确认</el-tag>
              <el-tag
                v-else-if="child.status === '0' || !child.status"
                type="warning"
                effect="light"
                size="small"
                round
              >待确认</el-tag>
              <el-tag
                v-else-if="child.status === '2'"
                type="danger"
                effect="light"
                size="small"
                round
              >已拒绝</el-tag>
            </div>
            <div class="child-card__meta">
              <span>学号：{{ child.studentNo || '--' }}</span>
              <span>班级：{{ child.className || '--' }}</span>
              <span>专业：{{ child.major || '--' }}</span>
              <span>关系：{{ child.relationType || '--' }}</span>
            </div>
          </div>
          <div class="child-card__actions">
            <!-- Accepted: unbind -->
            <el-popconfirm
              v-if="child.status === '1'"
              title="确认解除与该孩子的绑定关系吗？"
              confirm-button-text="确认解绑"
              cancel-button-text="取消"
              @confirm="handleUnbind(child)"
            >
              <template #reference>
                <el-button type="danger" plain size="small" :loading="child._loading">解除绑定</el-button>
              </template>
            </el-popconfirm>
            <!-- Pending: cancel -->
            <el-popconfirm
              v-else-if="child.status === '0' || !child.status"
              title="确认取消该绑定请求吗？"
              confirm-button-text="确认取消"
              cancel-button-text="返回"
              @confirm="handleCancel(child)"
            >
              <template #reference>
                <el-button type="warning" plain size="small" :loading="child._loading">取消请求</el-button>
              </template>
            </el-popconfirm>
            <!-- Rejected: resend -->
            <el-button
              v-else-if="child.status === '2'"
              type="primary"
              plain
              size="small"
              :loading="child._loading"
              @click="handleResend(child)"
            >重新发送</el-button>
          </div>
        </div>
      </TransitionGroup>
    </div>

    <div v-else-if="childList.length === 0" class="empty-state">
      <div class="empty-state__icon">
        <i class="ri-parent-line"></i>
      </div>
      <h3>暂未绑定孩子账号</h3>
      <p>绑定后可查看孩子的课程、课表和考试等信息</p>
      <el-button type="primary" @click="bindDialogVisible = true">
        <i class="ri-add-line"></i> 立即绑定
      </el-button>
    </div>

    <el-empty v-else description="没有符合筛选条件的记录" :image-size="100" />

    <!-- Bind Dialog -->
    <el-dialog
      v-model="bindDialogVisible"
      title="绑定新孩子"
      width="460px"
      :close-on-click-modal="false"
      append-to-body
      destroy-on-close
    >
      <el-form
        ref="bindFormRef"
        :model="bindForm"
        :rules="bindRules"
        label-width="90px"
        class="bind-form"
        @submit.prevent="handleBind"
      >
        <el-form-item label="选择学生" prop="studentNo">
          <el-select
            v-model="bindForm.studentNo"
            filterable
            remote
            :remote-method="handleStudentSearch"
            :loading="studentSearchLoading"
            placeholder="输入学号或姓名搜索"
            style="width: 100%"
            clearable
            value-key="studentNo"
            :teleported="true"
            popper-class="child-bind-select-popper"
          >
            <el-option
              v-for="stu in studentSearchResults"
              :key="stu.studentNo"
              :value="stu.studentNo"
              :label="`${stu.studentName} (${stu.studentNo})`"
            >
              <div class="student-option">
                <span class="student-option__name">{{ stu.studentName }}</span>
                <span class="student-option__no">{{ stu.studentNo }}</span>
                <span v-if="stu.className" class="student-option__class">{{ stu.className }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="家庭关系" prop="relationType">
          <el-select v-model="bindForm.relationType" placeholder="请选择关系" style="width: 100%" :teleported="true" popper-class="child-bind-select-popper">
            <el-option label="父亲" value="父亲" />
            <el-option label="母亲" value="母亲" />
            <el-option label="祖父" value="祖父" />
            <el-option label="祖母" value="祖母" />
            <el-option label="外祖父" value="外祖父" />
            <el-option label="外祖母" value="外祖母" />
            <el-option label="监护人" value="监护人" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
      </el-form>
      <div class="bind-dialog-hint">
        <i class="ri-information-line"></i>
        绑定请求将发送给学生，学生确认后方可生效。
      </div>
      <template #footer>
        <el-button @click="bindDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="bindLoading" @click="handleBind">发送请求</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { bindParentChild, listPortalParentChildren, unbindParentChild, searchStudentsForBind } from '@/api/portal'
import usePortalUserStore from '@/store/user'

const userStore = usePortalUserStore()

const listLoading = ref(false)
const bindLoading = ref(false)
const childList = ref<any[]>([])
const bindFormRef = ref()
const bindDialogVisible = ref(false)

const searchQuery = ref('')
const filterRelation = ref('')
const filterStatus = ref('')

const studentSearchLoading = ref(false)
const studentSearchResults = ref<any[]>([])

const bindForm = reactive({
  studentNo: '',
  relationType: '',
})

const bindRules = {
  studentNo: [{ required: true, message: '请选择要绑定的学生', trigger: 'change' }],
  relationType: [{ required: true, message: '请选择家庭关系', trigger: 'change' }],
}

let searchTimer: ReturnType<typeof setTimeout> | null = null
function handleStudentSearch(query: string) {
  if (searchTimer) clearTimeout(searchTimer)
  if (!query || query.trim().length < 1) {
    studentSearchResults.value = []
    return
  }
  searchTimer = setTimeout(async () => {
    const parentUserId = userStore.user?.userId
    if (!parentUserId) return
    studentSearchLoading.value = true
    try {
      const res = await searchStudentsForBind({ parentUserId, keyword: query.trim() })
      studentSearchResults.value = res.data || []
    } catch {
      studentSearchResults.value = []
    } finally {
      studentSearchLoading.value = false
    }
  }, 300)
}

const filteredChildren = computed(() => {
  return childList.value.filter(child => {
    const q = searchQuery.value.trim().toLowerCase()
    const matchSearch = !q ||
      (child.studentName || '').toLowerCase().includes(q) ||
      (child.studentNo || '').toLowerCase().includes(q)
    const matchRelation = !filterRelation.value || child.relationType === filterRelation.value
    const matchStatus = !filterStatus.value || (child.status || '0') === filterStatus.value
    return matchSearch && matchRelation && matchStatus
  })
})

async function loadChildren() {
  const parentUserId = userStore.user?.userId
  if (!parentUserId) return
  listLoading.value = true
  try {
    const res = await listPortalParentChildren({ parentUserId })
    childList.value = (res.data || []).map((item: any) => ({ ...item, _loading: false }))
  } finally {
    listLoading.value = false
  }
}

async function handleBind() {
  const valid = await bindFormRef.value?.validate().catch(() => false)
  if (!valid) return

  const parentUserId = userStore.user?.userId
  if (!parentUserId) {
    ElMessage.error('无法获取当前用户信息')
    return
  }

  if (childList.value.length >= 5) {
    ElMessage.warning('最多可绑定 5 位孩子')
    return
  }

  bindLoading.value = true
  try {
    const res = await bindParentChild({
      parentUserId,
      studentNo: bindForm.studentNo.trim(),
      relationType: bindForm.relationType,
    })
    if (res.code === 200) {
      ElMessage.success(res.msg || '绑定请求已发送')
      bindForm.studentNo = ''
      bindForm.relationType = ''
      bindFormRef.value?.resetFields()
      bindDialogVisible.value = false
      await loadChildren()
    } else {
      ElMessage.error(res.msg || '绑定失败')
    }
  } catch (err: any) {
    ElMessage.error(err?.message || '绑定请求失败')
  } finally {
    bindLoading.value = false
  }
}

async function handleUnbind(child: any) {
  const relId = child.id || child.relId
  if (!relId) { ElMessage.error('无法获取绑定关系 ID'); return }
  child._loading = true
  try {
    const res = await unbindParentChild(relId)
    if (res.code === 200) {
      ElMessage.success(res.msg || '解绑成功')
      await loadChildren()
    } else {
      ElMessage.error(res.msg || '解绑失败')
    }
  } catch (err: any) {
    ElMessage.error(err?.message || '解绑请求失败')
  } finally {
    child._loading = false
  }
}

async function handleCancel(child: any) {
  // Cancel pending request — same as unbind (delete the row)
  await handleUnbind(child)
}

async function handleResend(child: any) {
  // Resend rejected request — re-bind with same student
  const parentUserId = userStore.user?.userId
  if (!parentUserId) return
  child._loading = true
  try {
    const res = await bindParentChild({
      parentUserId,
      studentNo: child.studentNo,
      relationType: child.relationType,
    })
    if (res.code === 200) {
      ElMessage.success(res.msg || '请求已重新发送')
      await loadChildren()
    } else {
      ElMessage.error(res.msg || '重新发送失败')
    }
  } catch (err: any) {
    ElMessage.error(err?.message || '操作失败')
  } finally {
    child._loading = false
  }
}

onMounted(loadChildren)
</script>

<style scoped>
.child-bind-page {
  max-width: 860px;
  margin: 0 auto;
  padding: 28px 20px;
}

.child-bind-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 24px;
}

.header-left h2 {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
}

.header-left p {
  font-size: 14px;
  color: #909399;
  margin: 0;
  line-height: 1.6;
}

.child-bind-filter {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 10px;
  border: 1px solid #ebeef5;
  flex-wrap: wrap;
}

.filter-summary {
  margin-left: auto;
  font-size: 13px;
  color: #909399;
}

.filter-summary strong {
  color: #303133;
}

.child-list-loading {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #ebeef5;
}

.child-card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.child-card {
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

.child-card:hover {
  border-color: #c6e2ff;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.08);
  transform: translateY(-2px);
}

.child-card--status-0 {
  border-left: 3px solid #e6a23c;
}

.child-card--status-1 {
  border-left: 3px solid #67c23a;
}

.child-card--status-2 {
  border-left: 3px solid #f56c6c;
}

.child-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f7a9c4, #ef668f);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  flex-shrink: 0;
}

.child-card__info {
  flex: 1;
  min-width: 0;
}

.child-card__name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.child-card__name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.child-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: #909399;
}

.child-card__actions {
  flex-shrink: 0;
}

/* Empty state */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #ebeef5;
  text-align: center;
}

.empty-state__icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  background: linear-gradient(135deg, #f0f5ff, #dbeafe);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: #409eff;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
}

.empty-state p {
  font-size: 14px;
  color: #909399;
  margin: 0 0 24px;
}

/* Student search option */
.student-option {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.student-option__name {
  font-weight: 600;
  color: #303133;
}

.student-option__no {
  color: #909399;
}

.student-option__class {
  color: #b0b3b8;
  font-size: 12px;
}

/* Bind dialog */
.bind-form {
  padding-top: 8px;
}

.bind-dialog-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  background: #fdf6ec;
  border-radius: 8px;
  font-size: 13px;
  color: #e6a23c;
  margin-top: 4px;
}

/* List transitions */
.card-list-enter-active,
.card-list-leave-active {
  transition: all 0.35s ease;
}
.card-list-enter-from {
  opacity: 0;
  transform: translateY(16px);
}
.card-list-leave-to {
  opacity: 0;
  transform: translateX(-16px);
}

@media (max-width: 768px) {
  .child-bind-page {
    padding: 20px 14px;
  }

  .page-header h2 {
    font-size: 18px;
  }

  .child-bind-header {
    flex-direction: column;
    gap: 14px;
  }

  .child-card {
    padding: 14px 16px;
    gap: 12px;
  }

  .child-card__icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
    border-radius: 10px;
  }

  .child-card__name {
    font-size: 14px;
  }

  .child-card__meta {
    font-size: 12px;
    gap: 8px;
  }

  .child-bind-filter {
    padding: 12px 14px;
    gap: 10px;
  }

  .empty-state {
    padding: 40px 16px;
  }

  .empty-state__icon {
    width: 64px;
    height: 64px;
    font-size: 28px;
  }
}

@media (max-width: 600px) {
  .child-bind-page {
    padding: 14px 10px;
  }

  .child-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    padding: 12px 14px;
  }

  .child-card__meta {
    flex-direction: column;
    gap: 4px;
  }

  .child-card__actions {
    align-self: flex-end;
  }

  .child-bind-filter {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-summary {
    margin-left: 0;
  }

  .bind-form :deep(.el-form-item__label) {
    font-size: 13px;
  }
}
</style>

<style>
/* Non-scoped: select dropdown popper inside dialog needs higher z-index */
.child-bind-select-popper {
  z-index: 9999 !important;
}
</style>
