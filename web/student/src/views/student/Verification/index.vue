<template>
  <div class="verification-page">
    <header class="page-header">
      <div class="page-header__left">
        <el-button text bg size="small" @click="$router.back()"><i class="ri-arrow-left-line" /></el-button>
        <div class="page-header__title">
          <h1>学籍核对</h1>
          <p>集中查看当前核对批次、截止时间和个人处理状态，减少漏处理风险。</p>
        </div>
      </div>
      <div class="page-header__right">
        <div class="page-header__hint" v-if="nextDeadlineBatch">
          <i class="ri-alarm-warning-line" />
          <span>最近截止：{{ nextDeadlineBatch.batchName }} · {{ deadlineMeta(nextDeadlineBatch).label }}</span>
        </div>
        <el-button size="small" @click="loadData" :loading="loading"><i class="ri-refresh-line" /> 刷新</el-button>
      </div>
    </header>

    <section class="overview-strip">
      <article class="overview-card">
        <span>当前批次</span>
        <strong>{{ batchStats.total }}</strong>
        <p>可查看并参与的核对任务</p>
      </article>
      <article class="overview-card is-warning">
        <span>待处理</span>
        <strong>{{ batchStats.actionNeeded }}</strong>
        <p>待核对或被驳回后需重新处理</p>
      </article>
      <article class="overview-card is-success">
        <span>已通过</span>
        <strong>{{ batchStats.approved }}</strong>
        <p>审核通过或确认无误的记录</p>
      </article>
      <article class="overview-card is-accent">
        <span>待审核</span>
        <strong>{{ batchStats.reviewing }}</strong>
        <p>已提交修改，等待学校审核</p>
      </article>
    </section>

    <div class="profile-card" v-if="userProfile.realName || userProfile.nickName">
      <div class="profile-card__avatar">
        <img v-if="userProfile.avatarUrl" :src="userProfile.avatarUrl" alt="头像" />
        <div v-else class="profile-card__avatar-placeholder"><i class="ri-user-3-line" /></div>
      </div>
      <div class="profile-card__info">
        <h2 class="profile-card__name">{{ userProfile.realName || userProfile.nickName || '学生用户' }}</h2>
        <div class="profile-card__meta">
          <span v-if="userProfile.studentNo"><i class="ri-hashtag" /> {{ userProfile.studentNo }}</span>
          <span v-if="userProfile.major"><i class="ri-book-open-line" /> {{ userProfile.major }}</span>
          <span v-if="userProfile.className"><i class="ri-group-line" /> {{ userProfile.className }}</span>
          <span v-if="userProfile.gender !== undefined"><i class="ri-user-line" /> {{ userProfile.gender === '0' ? '男' : userProfile.gender === '1' ? '女' : '--' }}</span>
        </div>
        <div class="profile-card__tip">
          <i class="ri-shield-check-line" />
          <span>{{ profileTipText }}</span>
        </div>
      </div>
      <div class="profile-card__stats">
        <div class="stat-item">
          <strong>{{ batchStats.total }}</strong>
          <span>核对批次</span>
        </div>
        <div class="stat-item is-success">
          <strong>{{ batchStats.approved }}</strong>
          <span>已通过</span>
        </div>
        <div class="stat-item is-warning">
          <strong>{{ batchStats.actionNeeded }}</strong>
          <span>待处理</span>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-center">
      <div class="verification-skeleton">
        <div class="verification-skeleton__banner"></div>
        <div class="verification-skeleton__profile"></div>
        <div class="verification-skeleton__table"></div>
      </div>
    </div>

    <template v-else>
      <div class="section-card">
        <div class="section-card__header">
          <div class="section-card__title">
            <h2><i class="ri-list-check-2" /> 核对批次</h2>
            <p>优先处理“待核对”和“已驳回”的批次，避免错过截止时间。</p>
          </div>
          <span class="section-card__badge">{{ batches.length }} 个批次</span>
        </div>

        <div v-if="!batches.length" class="empty-state">
          <i class="ri-file-search-line" />
          <p>暂无进行中的核对批次</p>
        </div>

        <el-table v-else :data="batches" stripe class="batch-table" @row-click="goToDetail">
          <el-table-column prop="batchName" label="批次名称" min-width="180">
            <template #default="{ row }">
              <div class="batch-main-cell">
                <span class="batch-name-link">{{ row.batchName }}</span>
                <span class="batch-main-cell__sub">{{ row.description || '点击进入本批次核对详情' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="我的状态" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="myRecordStatusTag(row)" size="small">{{ myRecordStatusText(row) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="截止时间" width="170" align="center">
            <template #default="{ row }">
              <div class="deadline-cell">
                <span class="time-text">
                  <i class="ri-time-line" /> {{ row.endTime || '无截止时间' }}
                </span>
                <el-tag v-if="deadlineMeta(row).tag" :type="deadlineMeta(row).tag" size="small" effect="plain">
                  {{ deadlineMeta(row).label }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" align="center" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click.stop="goToDetail(row)">
                <template v-if="canEditBatch(row)"><i class="ri-edit-line" /> 去核对</template>
                <template v-else><i class="ri-eye-line" /> 查看</template>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="section-card" v-if="myRecords.length">
        <div class="section-card__header">
          <div class="section-card__title">
            <h2><i class="ri-history-line" /> 我的核对记录</h2>
            <p>保留个人处理历史、审核意见和提交时间，方便回看。</p>
          </div>
          <span class="section-card__badge">{{ myRecords.length }} 条记录</span>
        </div>
        <el-table :data="myRecords" stripe>
          <el-table-column prop="batchName" label="批次名称" min-width="160" show-overflow-tooltip />
          <el-table-column prop="studentNo" label="学号" width="130" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="recordTagType(row.recordStatus)" size="small">{{ recordLabel(row.recordStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="提交时间" width="170" />
          <el-table-column prop="reviewUserName" label="审核人" width="100" />
          <el-table-column prop="reviewComment" label="审核意见" min-width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="goToDetailByRecord(row)"><i class="ri-eye-line" /></el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getVerificationActiveBatches, getVerificationMyRecords, getPortalProfile } from '@/api/portal'

const router = useRouter()
const loading = ref(false)
const batches = ref<any[]>([])
const myRecords = ref<any[]>([])
const userProfile = ref<any>({})

const batchStats = computed(() => {
  const total = batches.value.length
  let approved = 0
  let actionNeeded = 0
  let reviewing = 0
  for (const b of batches.value) {
    const s = b.recordStatus
    if (s === 'APPROVED' || s === 'CONFIRMED') approved++
    else if (s === 'UNDER_REVIEW') reviewing++
    else if (!s || s === 'PENDING' || s === 'REJECTED') actionNeeded++
  }
  return { total, approved, actionNeeded, reviewing }
})

const nextDeadlineBatch = computed(() => {
  return [...batches.value]
    .filter((item: any) => item?.endTime && canEditBatch(item))
    .sort((left: any, right: any) => resolveTimeValue(left.endTime) - resolveTimeValue(right.endTime))[0] || null
})

const profileTipText = computed(() => {
  if (batchStats.value.actionNeeded > 0) {
    return `当前有 ${batchStats.value.actionNeeded} 个批次待你处理，建议优先查看最近截止的任务。`
  }
  if (batchStats.value.reviewing > 0) {
    return `你有 ${batchStats.value.reviewing} 条记录正在审核中，当前只需等待学校处理。`
  }
  if (batchStats.value.approved > 0) {
    return '当前已完成的核对记录会持续保留，便于后续回看审核意见。'
  }
  return '如有新的核对批次发布，系统会在这里集中展示。'
})

function myRecordStatusText(batch: any) {
  const s = batch.recordStatus
  if (!s || s === 'PENDING') return '待核对'
  if (s === 'CONFIRMED') return '已确认'
  if (s === 'UNDER_REVIEW') return '待审核'
  if (s === 'APPROVED') return '已通过'
  if (s === 'REJECTED') return '已驳回'
  return '待核对'
}

function myRecordStatusTag(batch: any) {
  const s = batch.recordStatus
  if (s === 'CONFIRMED') return ''
  if (s === 'UNDER_REVIEW') return 'warning'
  if (s === 'APPROVED') return 'success'
  if (s === 'REJECTED') return 'danger'
  return 'info'
}

function canEditBatch(batch: any) {
  const s = batch.recordStatus
  return !s || s === 'PENDING' || s === 'REJECTED'
}

function resolveTimeValue(value?: string) {
  if (!value) return Number.POSITIVE_INFINITY
  const normalized = String(value).replace(/-/g, '/')
  const time = new Date(normalized).getTime()
  return Number.isFinite(time) ? time : Number.POSITIVE_INFINITY
}

function deadlineMeta(batch: any) {
  const time = resolveTimeValue(batch?.endTime)
  if (!Number.isFinite(time) || time === Number.POSITIVE_INFINITY) {
    return { label: '长期有效', tag: '' }
  }
  const diff = time - Date.now()
  const dayMs = 24 * 60 * 60 * 1000
  const days = Math.ceil(diff / dayMs)
  if (days < 0) return { label: '已截止', tag: 'danger' }
  if (days === 0) return { label: '今日截止', tag: 'danger' }
  if (days <= 3) return { label: `${days}天后截止`, tag: 'warning' }
  return { label: `${days}天后截止`, tag: 'info' }
}

function recordLabel(status?: string) {
  const map: Record<string, string> = { PENDING: '待核对', CONFIRMED: '已确认', MODIFIED: '已修改', UNDER_REVIEW: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }
  return map[status || ''] || status || '-'
}

function recordTagType(status?: string) {
  const map: Record<string, string> = { PENDING: 'info', CONFIRMED: '', UNDER_REVIEW: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[status || ''] || 'info'
}

function goToDetail(batch: any) {
  router.push(`/student/verification/${batch.batchId}`)
}

function goToDetailByRecord(row: any) {
  if (row.batchId) router.push(`/student/verification/${row.batchId}`)
}

async function loadData() {
  loading.value = true
  try {
    const [batchRes, recordRes, profileRes] = await Promise.all([
      getVerificationActiveBatches(),
      getVerificationMyRecords(),
      getPortalProfile(),
    ])
    batches.value = batchRes.data || batchRes || []
    myRecords.value = recordRes.data || recordRes || []
    const pData = profileRes.data || profileRes || {}
    userProfile.value = {
      ...(pData.user || {}),
      ...(pData.profile || {}),
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.verification-page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ===== 页面标题 ===== */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e5eef8;
}

.page-header__left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-header__title h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
}

.page-header__title p {
  margin: 4px 0 0;
  color: #64748b;
  font-size: 13px;
}

.page-header__right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.page-header__hint {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  background: #fff7ed;
  color: #c2410c;
  font-size: 12px;
  font-weight: 700;
}

.overview-strip {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.overview-card {
  padding: 16px 18px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #e5eef8;
}

.overview-card span {
  display: block;
  color: #64748b;
  font-size: 12px;
  font-weight: 700;
}

.overview-card strong {
  display: block;
  margin-top: 10px;
  color: #0f172a;
  font-size: 28px;
  line-height: 1;
  font-weight: 800;
}

.overview-card p {
  margin: 10px 0 0;
  color: #94a3b8;
  font-size: 12px;
  line-height: 1.5;
}

.overview-card.is-warning strong { color: #d97706; }
.overview-card.is-success strong { color: #059669; }
.overview-card.is-accent strong { color: #2563eb; }

/* ===== 个人信息卡片 ===== */
.profile-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 24px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e5eef8;
}

.profile-card__avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: #f1f5f9;
  border: 2px solid #e2e8f0;
}

.profile-card__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-card__avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #94a3b8;
}

.profile-card__info {
  flex: 1;
  min-width: 0;
}

.profile-card__name {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
}

.profile-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.profile-card__meta span {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #64748b;
}

.profile-card__meta i {
  font-size: 14px;
  color: #94a3b8;
}

.profile-card__tip {
  margin-top: 12px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 10px;
  background: #f8fafc;
  color: #475569;
  font-size: 13px;
}

.profile-card__tip i {
  color: #2563eb;
}

.profile-card__stats {
  display: flex;
  gap: 24px;
  flex-shrink: 0;
  padding-left: 24px;
  border-left: 1px solid #f1f5f9;
}

.stat-item {
  text-align: center;
}

.stat-item strong {
  display: block;
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
}

.stat-item span {
  display: block;
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.stat-item.is-success strong { color: #059669; }
.stat-item.is-warning strong { color: #d97706; }

/* ===== Section Card ===== */
.section-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e5eef8;
  overflow: hidden;
}

.section-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #e5eef8;
}

.section-card__title h2 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #334155;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-card__title h2 i {
  font-size: 16px;
  color: #2563eb;
}

.section-card__title p {
  margin: 6px 0 0;
  color: #94a3b8;
  font-size: 12px;
}

.section-card__badge {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
}

/* ===== 批次表格 ===== */
.batch-table {
  cursor: pointer;
}

.batch-main-cell {
  display: grid;
  gap: 4px;
}

.batch-name-link {
  color: #2563eb;
  font-weight: 600;
}

.batch-main-cell__sub {
  color: #94a3b8;
  font-size: 12px;
  line-height: 1.5;
}

.batch-table :deep(.el-table__row:hover) .batch-name-link {
  text-decoration: underline;
}

.deadline-cell {
  display: grid;
  justify-items: center;
  gap: 6px;
}

.time-text {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 13px;
  color: #64748b;
}

.time-text i {
  font-size: 14px;
}

/* ===== Empty ===== */
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #94a3b8;
}

.empty-state i {
  font-size: 42px;
  display: block;
  margin-bottom: 12px;
  color: #cbd5e1;
}

.empty-state p {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}

/* ===== Loading ===== */
.loading-center {
  padding: 0;
}

.verification-skeleton {
  display: grid;
  gap: 16px;
}

.verification-skeleton__banner,
.verification-skeleton__profile,
.verification-skeleton__table {
  border-radius: 12px;
  background: linear-gradient(90deg, #eef4f8 0%, #f8fbfd 50%, #eef4f8 100%);
  background-size: 200% 100%;
  animation: verification-skeleton 1.4s ease-in-out infinite;
}

.verification-skeleton__banner {
  height: 64px;
}

.verification-skeleton__profile {
  height: 132px;
}

.verification-skeleton__table {
  height: 260px;
}

/* ===== 表格通用 ===== */
.section-card :deep(.el-table) {
  --el-table-border-color: transparent;
}

.section-card :deep(.el-table th.el-table__cell) {
  background: #fafbfc;
  font-size: 13px;
  color: #64748b;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .overview-strip {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .page-header,
  .profile-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
  }

  .page-header__right {
    width: 100%;
    justify-content: space-between;
  }

  .profile-card__meta {
    justify-content: flex-start;
  }

  .profile-card__stats {
    padding-left: 0;
    border-left: none;
    padding-top: 14px;
    border-top: 1px solid #f1f5f9;
    width: 100%;
    justify-content: flex-start;
  }
}

@media (max-width: 640px) {
  .verification-page {
    padding: 12px;
  }

  .overview-strip {
    grid-template-columns: 1fr;
  }
}

@keyframes verification-skeleton {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
