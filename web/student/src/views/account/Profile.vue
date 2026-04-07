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
          <!-- Student stats -->
          <template v-if="isStudent">
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-value">{{ growth.honorScore || 0 }}</span>
              <span class="stat-label">荣誉成绩</span>
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
          </template>
          <!-- Parent stats -->
          <template v-if="isParent">
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-value">{{ parentChildren.length }}</span>
              <span class="stat-label">已绑定孩子</span>
            </div>
          </template>
          <!-- Staff stats -->
          <template v-if="isStaff">
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-value">{{ roleGroup || '--' }}</span>
              <span class="stat-label">角色组</span>
            </div>
          </template>
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

        <el-card v-if="!isParent" shadow="never" class="profile-card mb-4" :body-style="{ padding: '0px' }">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon><Postcard /></el-icon>
                <span>{{ isStaff ? '身份与职务' : '身份与学籍' }}</span>
              </div>
            </div>
          </template>
          <el-descriptions :column="2" border class="profile-descriptions">
            <el-descriptions-item label="用户类型"><el-tag size="small" type="info">{{ userTypeLabel }}</el-tag></el-descriptions-item>
            <el-descriptions-item :label="isStaff ? '工号' : '学号'">{{ isStaff ? (profile.teacherNo || '--') : (profile.studentNo || '--') }}</el-descriptions-item>
            <el-descriptions-item :label="isStaff ? '学科方向' : '专业/学科'">{{ profile.major || '--' }}</el-descriptions-item>
            <el-descriptions-item v-if="isStudent" label="入学年份">{{ profile.admissionYear || '--' }}</el-descriptions-item>
            <el-descriptions-item label="角色组" :span="isStudent ? 2 : 1">{{ roleGroup || '--' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- Parent: bound children card in left column -->
        <el-card v-if="isParent" shadow="never" class="profile-card mb-4" :body-style="{ padding: '0px' }">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon><User /></el-icon>
                <span>已绑定孩子</span>
              </div>
              <el-button type="primary" link @click="$router.push('/parent/child-bind')">管理绑定</el-button>
            </div>
          </template>
          <div v-if="parentChildren.length" class="parent-children-list">
            <div v-for="child in parentChildren" :key="child.value" class="parent-child-row">
              <div class="parent-child-avatar">{{ (child.studentName || '?').slice(0, 1) }}</div>
              <div class="parent-child-info">
                <div class="parent-child-name">{{ child.studentName || child.label }}</div>
                <div class="parent-child-meta">{{ child.studentNo || '' }} · {{ child.className || '' }} · {{ child.relationType || '' }}</div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂未绑定孩子" :image-size="60">
            <el-button type="primary" size="small" @click="$router.push('/parent/child-bind')">去绑定</el-button>
          </el-empty>
        </el-card>

      </el-col>

      <el-col :span="8">
        <!-- Student-only: Points & Growth -->
        <el-card v-if="isStudent" shadow="never" class="profile-card mb-4 points-card" :body-style="{ padding: '0px' }">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon><Coin /></el-icon>
                <span>积分与成长</span>
              </div>
            </div>
          </template>
          <div class="growth-level-panel">
            <div class="growth-level-panel__head">
              <div>
                <div class="growth-level-panel__eyebrow">等级进度</div>
                <div class="growth-level-panel__title">{{ levelInfo.currentLevelName || '启航新芽' }}</div>
              </div>
              <div class="growth-level-panel__meta">
                <strong>{{ levelInfo.progressPercent || 0 }}%</strong>
                <span v-if="levelInfo.nextLevelCode">距 {{ levelInfo.nextLevelCode }} 还差 {{ levelInfo.pointsToNextLevel || 0 }} 分</span>
                <span v-else>已达到当前最高等级</span>
              </div>
            </div>
            <div class="growth-level-panel__bar">
              <i :style="{ width: `${levelInfo.progressPercent || 0}%` }"></i>
            </div>
          </div>
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
          <div class="growth-stat-grid">
            <div class="growth-stat-card" @click="openGrowthDialog('honor')">
              <span>提交考试</span>
              <strong>{{ growthStats.submittedExamCount || 0 }}</strong>
            </div>
            <div class="growth-stat-card" @click="openGrowthDialog('honor')">
              <span>平均成绩</span>
              <strong>{{ formatNumber(growthStats.avgExamScore, '0') }}</strong>
            </div>
            <div class="growth-stat-card" @click="openGrowthDialog('honor')">
              <span>高分场次</span>
              <strong>{{ growthStats.excellentExamCount || 0 }}</strong>
            </div>
            <div class="growth-stat-card" @click="openGrowthDialog('honor')">
              <span>满分场次</span>
              <strong>{{ growthStats.perfectExamCount || 0 }}</strong>
            </div>
          </div>
          <div v-if="monthlyPoints.length" class="growth-monthly">
            <div class="sub-title">近 6 个月积分增长</div>
            <div class="growth-monthly__bars">
              <div v-for="item in monthlyPoints" :key="item.month" class="growth-monthly__item">
                <span>{{ item.month }}</span>
                <div class="growth-monthly__track"><i :style="{ height: `${monthlyPointPeak ? Math.max(12, (Number(item.points || 0) / monthlyPointPeak) * 100) : 12}%` }"></i></div>
                <strong>{{ item.points || 0 }}</strong>
              </div>
            </div>
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

        <!-- Student-only: Honors & Achievements -->
        <el-card v-if="isStudent" shadow="never" class="profile-card mb-4" :body-style="{ padding: '0px' }">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon><Trophy /></el-icon>
                <span>荣誉成就与荣誉成绩</span>
              </div>
            </div>
          </template>
          <div class="honor-summary">
            <div class="honor-summary__score" @click="openGrowthDialog('honor')">
              <span>荣誉成绩</span>
              <strong>{{ growth.honorScore || 0 }}</strong>
              <small>由考试表现、成长成就和规范作答共同计算</small>
            </div>
            <div class="honor-summary__records">
              <div class="sub-title">代表性答卷</div>
              <div v-if="honorRecords.length" class="honor-record-list">
                <article v-for="item in honorRecords.slice(0, 3)" :key="item.recordId" class="honor-record-item" @click="openGrowthDialog('honor')">
                  <div class="honor-record-item__main">
                    <strong>{{ item.paperName }}</strong>
                    <span>{{ formatDateTime(item.submitTime) }} · 用时 {{ item.elapsedMinutes || 0 }} 分钟</span>
                  </div>
                  <div class="honor-record-item__side">
                    <div class="honor-record-item__score">{{ formatNumber(item.score, '0') }}</div>
                    <div class="honor-record-item__badges">
                      <el-tag v-for="badge in item.badges || []" :key="badge.label" size="small" effect="plain" :type="honorBadgeType(badge.tone)">
                        {{ badge.label }}
                      </el-tag>
                    </div>
                  </div>
                </article>
              </div>
              <el-empty v-else description="还没有可展示的荣誉答卷" :image-size="60" />
            </div>
          </div>
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

        <!-- Non-student: role info card -->
        <el-card v-if="!isStudent" shadow="never" class="profile-card mb-4" :body-style="{ padding: '20px' }">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <el-icon><Postcard /></el-icon>
                <span>{{ isParent ? '家长信息' : '职业信息' }}</span>
              </div>
            </div>
          </template>
          <div class="role-info-card">
            <div class="role-info-item">
              <span class="role-info-label">用户类型</span>
              <span class="role-info-value"><el-tag size="small" type="info">{{ userTypeLabel }}</el-tag></span>
            </div>
            <div class="role-info-item">
              <span class="role-info-label">角色组</span>
              <span class="role-info-value">{{ roleGroup || '--' }}</span>
            </div>
            <div v-if="isStaff" class="role-info-item">
              <span class="role-info-label">工号</span>
              <span class="role-info-value">{{ profile.teacherNo || '--' }}</span>
            </div>
            <div v-if="isStaff" class="role-info-item">
              <span class="role-info-label">学科方向</span>
              <span class="role-info-value">{{ profile.major || '--' }}</span>
            </div>
            <div v-if="isParent" class="role-info-item">
              <span class="role-info-label">已绑定孩子</span>
              <span class="role-info-value">{{ parentChildren.length }} 位</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      v-if="isStudent"
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
        <el-tab-pane label="荣誉成绩" name="honor">
          <div class="honor-dialog">
            <div class="honor-dialog__summary">
              <div class="honor-dialog__score">
                <span>荣誉成绩</span>
                <strong>{{ growth.honorScore || 0 }}</strong>
              </div>
              <div class="honor-dialog__stats">
                <div class="honor-dialog__stat"><span>提交考试</span><strong>{{ growthStats.submittedExamCount || 0 }}</strong></div>
                <div class="honor-dialog__stat"><span>高分场次</span><strong>{{ growthStats.excellentExamCount || 0 }}</strong></div>
                <div class="honor-dialog__stat"><span>满分场次</span><strong>{{ growthStats.perfectExamCount || 0 }}</strong></div>
                <div class="honor-dialog__stat"><span>平均用时</span><strong>{{ formatNumber(growthStats.avgElapsedMinutes, '0') }} 分钟</strong></div>
              </div>
            </div>
            <el-table v-if="honorRecords.length" :data="honorRecords" border class="growth-detail-table">
              <el-table-column label="答卷" min-width="220" show-overflow-tooltip>
                <template #default="scope">{{ scope.row.paperName }}</template>
              </el-table-column>
              <el-table-column label="得分" min-width="100">
                <template #default="scope">{{ formatNumber(scope.row.score, '0') }}</template>
              </el-table-column>
              <el-table-column label="正确率" min-width="100">
                <template #default="scope">{{ formatNumber(scope.row.correctRate, '0') }}%</template>
              </el-table-column>
              <el-table-column label="用时" min-width="100">
                <template #default="scope">{{ scope.row.elapsedMinutes || 0 }} 分钟</template>
              </el-table-column>
              <el-table-column label="成长积分" min-width="100">
                <template #default="scope">+{{ scope.row.pointsAwarded || 0 }}</template>
              </el-table-column>
              <el-table-column label="荣誉标签" min-width="220">
                <template #default="scope">
                  <el-tag v-for="badge in scope.row.badges || []" :key="badge.label" size="small" effect="plain" :type="honorBadgeType(badge.tone)" class="mr-1">
                    {{ badge.label }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="交卷时间" min-width="170">
                <template #default="scope">{{ formatDateTime(scope.row.submitTime) }}</template>
              </el-table-column>
            </el-table>
            <el-empty v-else description="暂无荣誉成绩数据" :image-size="80" />
            <div v-if="rewardRules.length" class="honor-rule-list">
              <article v-for="item in rewardRules" :key="item.code" class="honor-rule-item">
                <div class="honor-rule-item__head">
                  <strong>{{ item.title }}</strong>
                  <span>+{{ item.points }}</span>
                </div>
                <p>{{ item.desc }}</p>
              </article>
            </div>
          </div>
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
import { getPortalGrowthSummary, getPortalProfile, listPortalParentChildren } from '@/api/portal'
import usePortalUserStore from '@/store/user'
import { User, Postcard, Coin, Trophy, Medal } from '@element-plus/icons-vue'

const profile = ref<any>({})
const roleGroup = ref('')
const growth = ref<any>({ account: null, achievements: [], recentLedgers: [] })
const userStore = usePortalUserStore()
const growthDialogVisible = ref(false)
const growthDialogTab = ref<'ledger' | 'honor' | 'achievement'>('ledger')
const parentChildren = ref<any[]>([])

// Role detection
const userType = computed(() => profile.value?.userType || userStore.preferredPortalRole || 'student')
const isStudent = computed(() => userType.value === 'student')
const isTeacher = computed(() => userType.value === 'teacher')
const isAdvisor = computed(() => userType.value === 'advisor')
const isParent = computed(() => userType.value === 'parent')
const isStaff = computed(() => isTeacher.value || isAdvisor.value)
const levelInfo = computed(() => growth.value.levelInfo || {})
const growthStats = computed(() => growth.value.stats || {})
const monthlyPoints = computed(() => growth.value.monthlyPoints || [])
const honorRecords = computed(() => growth.value.honorRecords || [])
const rewardRules = computed(() => growth.value.rewardRules || [])
const monthlyPointPeak = computed(() => {
  return Math.max(...monthlyPoints.value.map((item: any) => Number(item.points || 0)), 0)
})

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
  if (value === 'advisor') return '辅导员'
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
])

const profileCompleteness = computed(() => {
  const total = completionFields.value.length
  const filled = completionFields.value.filter((item) => item !== undefined && item !== null && String(item).trim() !== '').length
  return Math.round((filled / total) * 100)
})

const contactCount = computed(() => [profile.value.phonenumber, profile.value.email].filter((item) => item && String(item).trim()).length)
const identityCount = computed(() => [profile.value.studentNo, profile.value.teacherNo, profile.value.major, profile.value.admissionYear].filter((item) => item && String(item).trim()).length)

function openGrowthDialog(tab: 'ledger' | 'honor' | 'achievement') {
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

function formatNumber(value: any, fallback = '-') {
  const numeric = Number(value)
  if (Number.isNaN(numeric)) return fallback
  return Number.isInteger(numeric) ? String(numeric) : numeric.toFixed(1)
}

function honorBadgeType(tone?: string) {
  const value = String(tone || '').toLowerCase()
  if (value === 'success') return 'success'
  if (value === 'warning') return 'warning'
  if (value === 'primary') return 'primary'
  return 'info'
}

onMounted(async () => {
  const profileRes = await getPortalProfile()
  profile.value = profileRes.data?.user || {}
  roleGroup.value = profileRes.data?.roleGroup || ''

  // Only load growth data for students
  if (profile.value.userType === 'student' || (!profile.value.userType && userStore.preferredPortalRole === 'student')) {
    try {
      const growthRes = await getPortalGrowthSummary({ userId: userStore.user?.userId })
      growth.value = growthRes.data || { account: null, achievements: [], recentLedgers: [] }
    } catch { /* non-critical */ }
  }

  // Load bound children for parents
  if (profile.value.userType === 'parent' || (!profile.value.userType && userStore.preferredPortalRole === 'parent')) {
    try {
      const childRes = await listPortalParentChildren({ parentUserId: userStore.user?.userId })
      parentChildren.value = (childRes.data || []).filter((c: any) => c.status === '1')
    } catch { /* non-critical */ }
  }
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

.growth-level-panel {
  margin: 20px 20px 0;
  padding: 18px 20px;
  border-radius: 14px;
  background: linear-gradient(135deg, #eff6ff 0%, #ffffff 100%);
  border: 1px solid #dbeafe;
}

.growth-level-panel__head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.growth-level-panel__eyebrow {
  font-size: 12px;
  color: #3b82f6;
  font-weight: 700;
}

.growth-level-panel__title {
  margin-top: 6px;
  font-size: 18px;
  font-weight: 700;
  color: var(--portal-text);
}

.growth-level-panel__meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.growth-level-panel__meta strong {
  font-size: 20px;
  color: #2563eb;
}

.growth-level-panel__meta span {
  font-size: 12px;
  color: var(--portal-text-secondary);
}

.growth-level-panel__bar {
  margin-top: 14px;
  height: 10px;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.18);
  overflow: hidden;
}

.growth-level-panel__bar i {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #2563eb 0%, #60a5fa 100%);
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

.growth-stat-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  padding: 16px 20px 0;
}

.growth-stat-card {
  border: 1px solid #e4e7ed;
  border-radius: 10px;
  background: #fff;
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.growth-stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
}

.growth-stat-card span {
  font-size: 12px;
  color: var(--portal-text-secondary);
}

.growth-stat-card strong {
  font-size: 22px;
  color: var(--portal-text);
}

.growth-monthly {
  padding: 18px 20px 0;
}

.growth-monthly__bars {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 10px;
  align-items: end;
}

.growth-monthly__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.growth-monthly__item span {
  font-size: 11px;
  color: var(--portal-text-secondary);
}

.growth-monthly__track {
  width: 100%;
  height: 92px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  display: flex;
  align-items: flex-end;
  padding: 8px;
}

.growth-monthly__track i {
  display: block;
  width: 100%;
  border-radius: 8px;
  background: linear-gradient(180deg, #60a5fa 0%, #2563eb 100%);
}

.growth-monthly__item strong {
  font-size: 13px;
  color: #2563eb;
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

.honor-summary {
  padding: 20px 20px 0;
}

.honor-summary__score {
  padding: 18px 20px;
  border-radius: 14px;
  background: linear-gradient(135deg, #fff7ed 0%, #ffffff 100%);
  border: 1px solid #fed7aa;
  display: flex;
  flex-direction: column;
  gap: 8px;
  cursor: pointer;
}

.honor-summary__score span {
  font-size: 13px;
  color: #c2410c;
  font-weight: 700;
}

.honor-summary__score strong {
  font-size: 32px;
  color: #9a3412;
  line-height: 1;
}

.honor-summary__score small {
  color: var(--portal-text-secondary);
  line-height: 1.6;
}

.honor-summary__records {
  margin-top: 18px;
}

.honor-record-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.honor-record-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  background: #fff;
  padding: 14px 16px;
  cursor: pointer;
}

.honor-record-item__main {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.honor-record-item__main strong {
  color: var(--portal-text);
  font-size: 14px;
}

.honor-record-item__main span {
  color: var(--portal-text-secondary);
  font-size: 12px;
  line-height: 1.5;
}

.honor-record-item__side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.honor-record-item__score {
  color: #2563eb;
  font-size: 20px;
  font-weight: 700;
}

.honor-record-item__badges {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: flex-end;
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

.honor-dialog {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.honor-dialog__summary {
  display: grid;
  grid-template-columns: minmax(220px, 0.8fr) minmax(0, 1.2fr);
  gap: 16px;
}

.honor-dialog__score,
.honor-dialog__stat {
  padding: 18px 20px;
  border-radius: 14px;
  border: 1px solid #e4e7ed;
  background: #fff;
}

.honor-dialog__score {
  background: linear-gradient(135deg, #fff7ed 0%, #ffffff 100%);
  border-color: #fed7aa;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.honor-dialog__score span,
.honor-dialog__stat span {
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.honor-dialog__score strong {
  color: #9a3412;
  font-size: 34px;
  line-height: 1;
}

.honor-dialog__stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.honor-dialog__stat {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.honor-dialog__stat strong {
  color: var(--portal-text);
  font-size: 20px;
}

.honor-rule-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 14px;
}

.honor-rule-item {
  padding: 16px 18px;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
  background: #fff;
}

.honor-rule-item__head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.honor-rule-item__head strong {
  color: var(--portal-text);
  font-size: 15px;
}

.honor-rule-item__head span {
  color: #2563eb;
  font-weight: 700;
}

.honor-rule-item p {
  margin: 10px 0 0;
  color: var(--portal-text-secondary);
  font-size: 13px;
  line-height: 1.6;
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
.mr-1 { margin-right: 4px; }

@media (max-width: 1200px) {
  .honor-dialog__summary {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 992px) {
  .growth-monthly__bars {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
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

/* Parent children list */
.parent-children-list {
  padding: 12px 20px;
}

.parent-child-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.parent-child-row:last-child {
  border-bottom: none;
}

.parent-child-avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #f7a9c4, #ef668f);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 600;
  font-size: 16px;
  flex-shrink: 0;
}

.parent-child-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--portal-text, #303133);
  margin-bottom: 2px;
}

.parent-child-meta {
  font-size: 12px;
  color: var(--portal-text-secondary, #909399);
}

/* Role info card (non-student right column) */
.role-info-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.role-info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.role-info-label {
  font-size: 13px;
  color: var(--portal-text-secondary, #909399);
}

.role-info-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--portal-text, #303133);
}

@media (max-width: 768px) {
  .profile-page {
    padding: 14px;
  }

  .profile-header-wrapper {
    padding: 18px;
    flex-direction: column;
  }

  .profile-header-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .profile-avatar-container {
    margin-right: 0;
    margin-bottom: 12px;
  }

  .profile-header-stats {
    margin-top: 16px;
    justify-content: center;
    flex-wrap: wrap;
    gap: 16px;
  }

  .stat-item {
    align-items: center;
  }

  .stat-value {
    font-size: 20px;
  }

  .growth-monthly__bars {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .honor-dialog__summary {
    grid-template-columns: 1fr;
  }

  .honor-rule-list {
    grid-template-columns: 1fr;
  }

  .growth-stat-grid {
    grid-template-columns: 1fr;
  }

  .points-summary {
    flex-direction: column;
  }

  .honor-dialog__stats {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .profile-page {
    padding: 10px;
  }

  .profile-header-wrapper {
    padding: 14px;
    margin-bottom: 14px;
  }

  .profile-avatar {
    width: 56px;
    height: 56px;
    font-size: 22px;
  }

  .profile-user-info h2 {
    font-size: 16px;
    flex-direction: column;
    gap: 6px;
  }

  .profile-header-stats {
    gap: 12px;
  }

  .stat-divider {
    height: 24px;
  }

  .stat-value {
    font-size: 18px;
  }

  .stat-label {
    font-size: 11px;
  }

  .profile-descriptions {
    padding: 12px;
  }

  .profile-descriptions :deep(.el-descriptions__label) {
    width: 90px;
    padding: 10px 12px;
    font-size: 12px;
  }

  .profile-descriptions :deep(.el-descriptions__content) {
    padding: 10px 12px;
    font-size: 13px;
  }

  .learning-profile {
    padding: 14px;
  }

  .growth-monthly__bars {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  /* Deep mobile: dialog responsive */
  .growth-detail-dialog :deep(.el-dialog) {
    width: calc(100vw - 24px) !important;
    margin: 12px auto !important;
    max-width: 100%;
    border-radius: 12px;
  }

  .growth-detail-dialog :deep(.el-dialog__body) {
    max-height: calc(90vh - 100px);
    padding: 14px;
  }

  /* Touch-friendly targets */
  .point-box {
    min-height: 40px;
    padding: 14px;
  }

  .growth-stat-card {
    padding: 12px 14px;
    min-height: 40px;
  }

  .achievement-item {
    padding: 10px;
    gap: 10px;
    min-height: 44px;
  }

  .honor-record-item {
    padding: 12px 14px;
    flex-direction: column;
    gap: 10px;
  }

  .honor-record-item__side {
    align-items: flex-start;
    flex-direction: row;
    gap: 12px;
  }

  /* Overflow: el-descriptions on narrow screens */
  .profile-descriptions :deep(.el-descriptions) {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }

  .profile-descriptions :deep(.el-descriptions__table) {
    min-width: 320px;
  }

  /* Honor dialog score */
  .honor-dialog__score strong {
    font-size: 28px;
  }

  .honor-dialog__stat strong {
    font-size: 18px;
  }

  .achievement-detail-item {
    flex-direction: column;
    gap: 12px;
    padding: 14px;
  }

  .achievement-detail-item__title {
    font-size: 15px;
  }

  .achievement-detail-item__desc {
    font-size: 13px;
  }

  .growth-level-panel {
    margin: 14px 12px 0;
    padding: 14px;
  }

  .growth-level-panel__title {
    font-size: 16px;
  }

  .growth-level-panel__meta strong {
    font-size: 18px;
  }

  .growth-monthly {
    padding: 14px 12px 0;
  }

  .growth-monthly__track {
    height: 72px;
  }

  .points-summary {
    padding: 14px 12px 0;
  }

  .growth-stat-grid {
    padding: 12px 12px 0;
  }

  .recent-ledgers {
    padding: 0 12px 14px;
  }

  .honor-summary {
    padding: 14px 12px 0;
  }

  .achievement-list {
    padding: 14px;
    gap: 10px;
  }
}
</style>
