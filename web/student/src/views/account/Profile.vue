<template>
  <div class="portal-page profile-page !pb-5">
    <div class="profile-header-wrapper">
      <div class="profile-header-bg"></div>
      <div class="profile-header-content">
        <div class="profile-avatar-container">
          <div class="profile-avatar">{{ avatarText }}</div>
        </div>
        <div class="profile-user-info">
          <h2>{{ displayName }} <el-tag type="primary" effect="light" round size="small" class="ml-2">{{ userTypeLabel }}</el-tag></h2>
          <p class="profile-user-account">@{{ profile.userName || '--' }}</p>
        </div>
        <div class="profile-header-stats">
          <div class="stat-item">
            <span class="stat-value">{{ profileCompleteness }}<small>%</small></span>
            <span class="stat-label">档案完整度</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ growth.account?.levelCode || 'L1' }}</span>
            <span class="stat-label">成长等级</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ growth.account?.totalPoints || 0 }}</span>
            <span class="stat-label">累计积分</span>
          </div>
        </div>
      </div>
    </div>

    <el-row :gutter="20" class="profile-main-content">
      <el-col :span="16">
        <el-card shadow="never" class="profile-card mb-4" :body-style="{ padding: '0px' }">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon><User /></el-icon>
                <span>基础资料</span>
              </div>
              <el-button type="primary" link @click="$router.push('/account/settings')">编辑资料</el-button>
            </div>
          </template>
          <el-descriptions :column="2" border class="profile-descriptions">
            <el-descriptions-item label="登录账号">{{ profile.userName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="真实姓名">{{ profile.realName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ profile.nickName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ sexLabel }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ profile.phonenumber || '--' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ profile.email || '--' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" class="profile-card mb-4" :body-style="{ padding: '0px' }">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon><Postcard /></el-icon>
                <span>身份与学籍</span>
              </div>
            </div>
          </template>
          <el-descriptions :column="2" border class="profile-descriptions">
            <el-descriptions-item label="用户类型"><el-tag size="small" type="info">{{ profile.userType || '--' }}</el-tag></el-descriptions-item>
            <el-descriptions-item label="学号/工号">{{ profile.studentNo || profile.teacherNo || '--' }}</el-descriptions-item>
            <el-descriptions-item label="专业/学科">{{ profile.major || '--' }}</el-descriptions-item>
            <el-descriptions-item label="入学年份">{{ profile.admissionYear || '--' }}</el-descriptions-item>
            <el-descriptions-item label="角色组" :span="2">{{ roleGroup || '--' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" class="profile-card mb-4" :body-style="{ padding: '0px' }">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon><DataBoard /></el-icon>
                <span>学习偏好</span>
              </div>
            </div>
          </template>
          <div class="learning-profile">
            <div class="lp-item">
              <div class="lp-label">学习风格</div>
              <div class="lp-value">{{ profile.learningStyle || '--' }}</div>
            </div>
            <div class="lp-item">
              <div class="lp-label">学习目标</div>
              <div class="lp-value">{{ profile.learningGoal || '--' }}</div>
            </div>
            <div class="lp-item">
              <div class="lp-label">兴趣标签</div>
              <div class="lp-tags">
                <el-tag v-for="tag in parsedTags" :key="tag" class="mr-2" effect="light" round>{{ tag }}</el-tag>
                <span v-if="!parsedTags.length" class="text-muted">暂无标签</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="never" class="profile-card mb-4 points-card" :body-style="{ padding: '0px' }">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon><Coin /></el-icon>
                <span>积分与成长</span>
              </div>
            </div>
          </template>
          <div class="points-summary">
            <button type="button" class="point-box point-box--button" @click="openGrowthDialog('ledger')">
              <div class="point-title">可用积分</div>
              <div class="point-num text-primary">{{ growth.account?.availablePoints || 0 }}</div>
            </button>
            <button type="button" class="point-box point-box--button" @click="openGrowthDialog('ledger')">
              <div class="point-title">已用积分</div>
              <div class="point-num text-warning">{{ growth.account?.usedPoints || 0 }}</div>
            </button>
          </div>
          
          <div class="recent-ledgers mt-4">
            <div class="sub-title">最近流水</div>
            <el-timeline v-if="(growth.recentLedgers || []).length" class="mt-3">
              <el-timeline-item
                v-for="(item, index) in (growth.recentLedgers || []).slice(0, 5)"
                :key="index"
                :type="item.changePoints > 0 ? 'success' : 'warning'"
                size="normal"
                :timestamp="formatDateTime(item.createTime) || ''"
              >
                <div class="ledger-item">
                  <div class="ledger-main">
                    <span class="ledger-remark">{{ ledgerRemark(item) }}</span>
                    <span class="ledger-meta">{{ ledgerTypeLabel(item.bizType) }}</span>
                  </div>
                  <span class="ledger-point" :class="item.changePoints > 0 ? 'text-success' : 'text-danger'">
                    {{ item.changePoints > 0 ? '+' : '' }}{{ item.changePoints }}
                  </span>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无积分流水" :image-size="60" />
          </div>
        </el-card>

        <el-card shadow="never" class="profile-card mb-4" :body-style="{ padding: '0px' }">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon><Trophy /></el-icon>
                <span>荣誉成就</span>
              </div>
            </div>
          </template>
          <div class="achievement-list">
            <div v-for="item in growth.achievements || []" :key="item.achievementId" class="achievement-item" @click="openGrowthDialog('achievement')">
              <div class="ach-icon"><el-icon><Medal /></el-icon></div>
              <div class="ach-info">
                <div class="ach-title">{{ item.achievementTitle }}</div>
                <div class="ach-desc">{{ item.achievementDesc || '已达成成就' }}</div>
              </div>
            </div>
            <el-empty v-if="!(growth.achievements || []).length" description="暂未解锁成就" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      v-model="growthDialogVisible"
      title="积分与成长详情"
      width="min(1100px, 94vw)"
      append-to-body
      class="growth-detail-dialog"
    >
      <el-tabs v-model="growthDialogTab">
        <el-tab-pane label="积分流水" name="ledger">
          <el-table :data="growth.recentLedgers || []" border class="growth-detail-table">
            <el-table-column label="时间" min-width="180">
              <template #default="scope">{{ formatDateTime(scope.row.createTime) || '--' }}</template>
            </el-table-column>
            <el-table-column label="业务类型" min-width="140">
              <template #default="scope">{{ ledgerTypeLabel(scope.row.bizType) }}</template>
            </el-table-column>
            <el-table-column label="说明" min-width="240" show-overflow-tooltip>
              <template #default="scope">{{ ledgerRemark(scope.row) }}</template>
            </el-table-column>
            <el-table-column label="积分变化" min-width="120">
              <template #default="scope">
                <span :class="Number(scope.row.changePoints || 0) > 0 ? 'text-success' : 'text-danger'">
                  {{ Number(scope.row.changePoints || 0) > 0 ? '+' : '' }}{{ scope.row.changePoints || 0 }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="balanceAfter" label="变化后余额" min-width="120" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="荣誉成就" name="achievement">
          <div v-if="(growth.achievements || []).length" class="achievement-detail-list">
            <article v-for="item in growth.achievements || []" :key="item.achievementId" class="achievement-detail-item">
              <div class="achievement-detail-item__icon"><el-icon><Trophy /></el-icon></div>
              <div class="achievement-detail-item__content">
                <div class="achievement-detail-item__title">{{ item.achievementTitle }}</div>
                <div class="achievement-detail-item__desc">{{ item.achievementDesc || '已达成成就' }}</div>
                <div class="achievement-detail-item__meta">
                  <span>成就编码：{{ item.achievementCode || '--' }}</span>
                  <span>解锁时间：{{ item.earnedTime || '--' }}</span>
                </div>
              </div>
            </article>
          </div>
          <el-empty v-else description="暂未解锁成就" :image-size="80" />
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getPortalGrowthSummary, getPortalProfile } from '@/api/portal'
import usePortalUserStore from '@/store/user'
import { User, Postcard, DataBoard, Coin, Trophy, Medal } from '@element-plus/icons-vue'

const profile = ref<any>({})
const roleGroup = ref('')
const growth = ref<any>({ account: null, achievements: [], recentLedgers: [] })
const userStore = usePortalUserStore()
const growthDialogVisible = ref(false)
const growthDialogTab = ref<'ledger' | 'achievement'>('ledger')

const sexLabel = computed(() => {
  const value = String(profile.value.sex ?? '2')
  if (value === '0') return '男'
  if (value === '1') return '女'
  return '未知'
})

const userTypeLabel = computed(() => {
  const value = String(profile.value.userType || '')
  if (value === 'student') return '学生'
  if (value === 'teacher') return '教师'
  if (value === 'parent') return '家长'
  return value || '--'
})

const displayName = computed(() => profile.value.realName || profile.value.nickName || profile.value.userName || '学习用户')
const avatarText = computed(() => String(displayName.value).slice(0, 1).toUpperCase())

const completionFields = computed(() => [
  profile.value.userName,
  profile.value.nickName,
  profile.value.realName,
  profile.value.phonenumber,
  profile.value.email,
  profile.value.userType,
  profile.value.studentNo,
  profile.value.teacherNo,
  profile.value.major,
  profile.value.admissionYear,
  profile.value.learningGoal,
  profile.value.interestTags,
  profile.value.learningStyle,
])

const profileCompleteness = computed(() => {
  const total = completionFields.value.length
  const filled = completionFields.value.filter((item) => item !== undefined && item !== null && String(item).trim() !== '').length
  return Math.round((filled / total) * 100)
})

const contactCount = computed(() => [profile.value.phonenumber, profile.value.email].filter((item) => item && String(item).trim()).length)
const identityCount = computed(() => [profile.value.studentNo, profile.value.teacherNo, profile.value.major, profile.value.admissionYear].filter((item) => item && String(item).trim()).length)
const portraitCount = computed(() => [profile.value.learningGoal, profile.value.interestTags, profile.value.learningStyle].filter((item) => item && String(item).trim()).length)

const parsedTags = computed(() =>
  String(profile.value.interestTags || '')
    .split(/[，,]/)
    .map((item) => item.trim())
    .filter(Boolean)
)

function openGrowthDialog(tab: 'ledger' | 'achievement') {
  growthDialogTab.value = tab
  growthDialogVisible.value = true
}

function formatDateTime(value?: string | number | Date | null) {
  if (!value) return ''
  const date = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  const hour = `${date.getHours()}`.padStart(2, '0')
  const minute = `${date.getMinutes()}`.padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

function ledgerTypeLabel(value?: string) {
  const map: Record<string, string> = {
    EXAM_SUBMIT: '考试提交奖励',
    TASK_SUBMIT: '任务提交奖励',
    ACHIEVEMENT: '成就奖励',
    SIGN_IN: '签到奖励',
    MANUAL: '人工调整',
  }
  return map[String(value || '').toUpperCase()] || value || '积分变动'
}

function ledgerRemark(item: any) {
  return item?.remark || ledgerTypeLabel(item?.bizType) || '积分变动'
}

onMounted(async () => {
  const [profileRes, growthRes] = await Promise.all([
    getPortalProfile(),
    getPortalGrowthSummary({ userId: userStore.user?.userId }),
  ])
  profile.value = profileRes.data?.user || {}
  roleGroup.value = profileRes.data?.roleGroup || ''
  growth.value = growthRes.data || { account: null, achievements: [], recentLedgers: [] }
})
</script>

<style scoped>
.profile-page {
  padding: 20px;
  background: transparent;
}

.profile-header-wrapper {
  position: relative;
  border-radius: 8px;
  background: #fff;
  border: 1px solid #ebeef5;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
  margin-bottom: 20px;
  padding: 24px;
  display: flex;
  align-items: center;
}

.profile-header-bg {
  display: none;
}

.profile-header-content {
  display: flex;
  align-items: center;
  padding: 0;
  width: 100%;
}

.profile-avatar-container {
  margin-top: 0;
  margin-right: 24px;
  position: relative;
  z-index: 2;
  border: none;
  background: transparent;
}

.profile-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--portal-brand-light);
  color: var(--portal-brand);
  font-size: 28px;
  font-weight: bold;
}

.profile-user-info {
  flex: 1;
  padding-bottom: 0;
}

.profile-user-info h2 {
  margin: 0 0 4px 0;
  font-size: 18px;
  color: var(--portal-text);
  display: flex;
  align-items: center;
  font-weight: 500;
}

.profile-user-account {
  margin: 0;
  color: var(--portal-text-secondary);
  font-size: 13px;
  font-family: Consolas, Monaco, monospace;
}

.profile-header-stats {
  display: flex;
  align-items: center;
  gap: 32px;
  padding-bottom: 0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.stat-value {
  font-size: 24px;
  font-weight: 500;
  color: var(--portal-text);
  line-height: 1.2;
}

.stat-value small {
  font-size: 13px;
  font-weight: normal;
  margin-left: 2px;
}

.stat-label {
  font-size: 12px;
  color: var(--portal-text-secondary);
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: var(--portal-border);
}

.profile-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
  background: #fff;
}

.profile-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #fff;
  border-radius: 8px 8px 0 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--portal-text);
}

.card-title el-icon {
  font-size: 16px;
  color: var(--portal-text-secondary);
}

.profile-descriptions {
  padding: 16px 20px;
}

.profile-descriptions :deep(.el-descriptions__table) {
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
  background: #fff;
}

.profile-descriptions :deep(.el-descriptions__label) {
  width: 120px;
  color: var(--portal-text-secondary);
  background: #f8f9fb;
  padding: 12px 16px;
  font-weight: normal;
}

.profile-descriptions :deep(.el-descriptions__content) {
  color: var(--portal-text);
  padding: 12px 16px;
  font-weight: normal;
  background: #fff;
}

.profile-descriptions :deep(.el-descriptions__cell) {
  border-color: #e4e7ed !important;
}

.learning-profile {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
}

.lp-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.lp-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--portal-text-secondary);
}

.lp-value {
  font-size: 13px;
  color: var(--portal-text);
  line-height: 1.6;
  padding: 12px 16px;
  background: #f8f9fb;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
}

.lp-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.points-summary {
  display: flex;
  gap: 16px;
  padding: 20px 20px 0;
}

.point-box {
  flex: 1;
  padding: 16px;
  background: #f8f9fb;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  text-align: center;
}

.point-box--button {
  appearance: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.point-box--button:hover {
  border-color: #c6e2ff;
  background: #ecf5ff;
}

.point-title {
  font-size: 13px;
  color: var(--portal-text-secondary);
  margin-bottom: 8px;
}

.point-num {
  font-size: 24px;
  font-weight: 500;
}

.text-primary { color: var(--portal-brand); }
.text-warning { color: var(--portal-warning); }
.text-success { color: var(--portal-success); }
.text-danger { color: var(--portal-danger); }

.recent-ledgers {
  padding: 0 20px 20px;
}

.sub-title {
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 16px;
  color: var(--portal-text);
}

.ledger-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-right: 16px;
}

.ledger-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.ledger-remark {
  font-size: 13px;
  color: var(--portal-text);
  line-height: 1.4;
  margin-right: 12px;
}

.ledger-meta {
  font-size: 12px;
  color: var(--portal-text-secondary);
}

.ledger-point {
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
}

.achievement-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
}

.achievement-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8f9fb;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: transform 0.2s;
}

.achievement-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.ach-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 169, 64, 0.15);
  color: #ffa940;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.ach-info {
  flex: 1;
}

.ach-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--portal-text);
  margin-bottom: 4px;
}

.ach-desc {
  font-size: 12px;
  color: var(--portal-text-secondary);
  line-height: 1.4;
}

.growth-detail-dialog :deep(.el-dialog__body) {
  max-height: calc(90vh - 120px);
  overflow: auto;
}

.achievement-detail-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.achievement-detail-item {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  padding: 18px 20px;
  border: 1px solid #e4e7ed;
  border-radius: 10px;
  background: #fff;
}

.achievement-detail-item__icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff7ed;
  color: #fb923c;
  font-size: 20px;
  flex-shrink: 0;
}

.achievement-detail-item__content {
  flex: 1;
  min-width: 0;
}

.achievement-detail-item__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--portal-text);
}

.achievement-detail-item__desc {
  margin-top: 8px;
  font-size: 14px;
  color: var(--portal-text-secondary);
  line-height: 1.7;
}

.achievement-detail-item__meta {
  margin-top: 10px;
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  font-size: 12px;
  color: #909399;
}

.mt-3 { margin-top: 12px; }
.mt-4 { margin-top: 16px; }
.mb-4 { margin-bottom: 20px; }
.ml-2 { margin-left: 8px; }
.mr-2 { margin-right: 8px; }

@media (max-width: 992px) {
  .profile-header-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .profile-avatar-container {
    margin-right: 0;
    margin-bottom: 16px;
  }
  
  .profile-header-stats {
    margin-top: 24px;
    justify-content: center;
  }
  
  .stat-item {
    align-items: center;
  }
}
</style>
