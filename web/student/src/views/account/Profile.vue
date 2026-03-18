<template>
  <div class="portal-page profile-page">
    <section class="portal-hero profile-hero">
      <div class="profile-hero__main">
        <div class="portal-hero__badge">Profile Center</div>
        <div class="portal-hero__title">我的档案</div>
        <p class="portal-hero__desc">集中查看当前账号在智慧校园中的身份信息、联系方式、学习画像与账号完整度。</p>

        <div class="profile-hero__identity">
          <div class="profile-avatar">{{ avatarText }}</div>
          <div>
            <strong>{{ displayName }}</strong>
            <span>{{ profile.userName || '--' }}</span>
          </div>
        </div>
      </div>

      <div class="portal-hero__panel profile-hero__panel">
        <div class="portal-hero__metric">
          <span>当前角色</span>
          <strong>{{ userTypeLabel }}</strong>
        </div>
        <div class="portal-hero__metric">
          <span>角色组</span>
          <strong>{{ roleGroup || '--' }}</strong>
        </div>
        <div class="portal-hero__metric">
          <span>档案完整度</span>
          <strong>{{ profileCompleteness }}%</strong>
        </div>
      </div>
    </section>

    <div class="portal-grid portal-grid-3 mt16">
      <section class="portal-card profile-kpi-card">
        <div class="kpi-label">联系方式</div>
        <div class="kpi-value">{{ contactCount }}</div>
        <div class="kpi-sub">已填写手机号、邮箱等联系资料</div>
      </section>
      <section class="portal-card profile-kpi-card">
        <div class="kpi-label">身份字段</div>
        <div class="kpi-value">{{ identityCount }}</div>
        <div class="kpi-sub">学号、工号、专业与入学年份等信息</div>
      </section>
      <section class="portal-card profile-kpi-card">
        <div class="kpi-label">画像字段</div>
        <div class="kpi-value">{{ portraitCount }}</div>
        <div class="kpi-sub">学习目标、兴趣标签与学习风格</div>
      </section>
    </div>

    <div class="portal-grid portal-grid-2 mt16">
      <section class="portal-card">
        <div class="portal-section-title">
          <h3>基础资料</h3>
          <p>账号、联系方式与实名信息</p>
        </div>
        <div class="profile-grid">
          <div class="profile-item"><span>登录账号</span><strong>{{ profile.userName || '--' }}</strong></div>
          <div class="profile-item"><span>昵称</span><strong>{{ profile.nickName || '--' }}</strong></div>
          <div class="profile-item"><span>真实姓名</span><strong>{{ profile.realName || '--' }}</strong></div>
          <div class="profile-item"><span>手机号</span><strong>{{ profile.phonenumber || '--' }}</strong></div>
          <div class="profile-item"><span>邮箱</span><strong>{{ profile.email || '--' }}</strong></div>
          <div class="profile-item"><span>性别</span><strong>{{ sexLabel }}</strong></div>
        </div>
      </section>

      <section class="portal-card">
        <div class="portal-section-title">
          <h3>身份识别</h3>
          <p>用于门户分配与权限识别</p>
        </div>
        <div class="profile-grid">
          <div class="profile-item"><span>用户类型</span><strong>{{ profile.userType || '--' }}</strong></div>
          <div class="profile-item"><span>学号</span><strong>{{ profile.studentNo || '--' }}</strong></div>
          <div class="profile-item"><span>工号</span><strong>{{ profile.teacherNo || '--' }}</strong></div>
          <div class="profile-item"><span>专业 / 学科</span><strong>{{ profile.major || '--' }}</strong></div>
          <div class="profile-item"><span>入学年份</span><strong>{{ profile.admissionYear || '--' }}</strong></div>
          <div class="profile-item"><span>学习风格</span><strong>{{ profile.learningStyle || '--' }}</strong></div>
        </div>
      </section>
    </div>

    <div class="portal-grid mt16">
      <section class="portal-card">
        <div class="portal-section-title">
          <h3>学习画像</h3>
          <p>便于推荐、预警和学习支持服务更精准地匹配</p>
        </div>
        <div class="profile-stack">
          <div class="profile-item profile-item--full">
            <span>学习目标</span>
            <strong>{{ profile.learningGoal || '--' }}</strong>
          </div>
          <div class="profile-item profile-item--full">
            <span>兴趣标签</span>
            <strong>{{ profile.interestTags || '--' }}</strong>
          </div>
          <div class="profile-tags">
            <span v-for="tag in parsedTags" :key="tag" class="profile-tag">{{ tag }}</span>
            <span v-if="!parsedTags.length" class="profile-tag profile-tag--muted">暂无标签</span>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getPortalProfile } from '@/api/portal'

const profile = ref<any>({})
const roleGroup = ref('')

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

onMounted(async () => {
  const res = await getPortalProfile()
  profile.value = res.data?.user || {}
  roleGroup.value = res.data?.roleGroup || ''
})
</script>

<style scoped>
.profile-hero {
  align-items: stretch;
}

.profile-hero__main {
  display: flex;
  flex-direction: column;
}

.profile-hero__panel {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.profile-hero__identity {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 18px;
}

.profile-avatar {
  width: 52px;
  height: 52px;
  border-radius: 6px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, var(--portal-brand-light) 0%, #dfeaff 100%);
  color: var(--portal-brand);
  font-size: 22px;
  font-weight: 800;
}

.profile-hero__identity strong {
  display: block;
  font-size: 18px;
  color: var(--portal-text);
}

.profile-hero__identity span {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: var(--portal-text-secondary);
}

.profile-kpi-card {
  padding: 18px;
}

.kpi-label {
  color: var(--portal-text-secondary);
  font-size: 12px;
}

.kpi-value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 800;
  color: var(--portal-brand);
}

.kpi-sub {
  margin-top: 6px;
  color: var(--portal-text-secondary);
  font-size: 12px;
  line-height: 1.7;
}

.profile-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  padding: 14px;
}

.profile-page .portal-card > .portal-section-title {
  margin-bottom: 0;
  padding: 14px 14px 0;
}

.profile-page .portal-card > .portal-section-title p {
  align-self: center;
}

.profile-stack {
  display: grid;
  gap: 10px;
  padding: 14px;
}

.profile-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px 14px;
  border-radius: 6px;
  background: var(--portal-surface-bg);
  border: 1px solid var(--portal-border);
}

.profile-item span {
  font-size: 12px;
  color: var(--portal-text-secondary);
}

.profile-item strong {
  font-size: 14px;
  color: var(--portal-text);
  line-height: 1.6;
  word-break: break-word;
}

.profile-item--full {
  width: 100%;
}

.profile-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.profile-tag {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 6px;
  background: var(--portal-brand-light);
  color: var(--portal-brand);
  font-size: 12px;
  font-weight: 600;
}

.profile-tag--muted {
  background: var(--portal-surface-bg);
  color: var(--portal-text-secondary);
}

@media (max-width: 1200px) {
  .profile-hero__panel {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }
}
</style>
