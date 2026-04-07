<template>
  <div v-loading="pageLoading" class="portal-page settings-page !pb-5">
    <div class="settings-header">
      <h2>账号设置</h2>
      <p>统一管理个人资料、安全信息与使用偏好，定制您的智慧校园体验。</p>
    </div>

    <div class="settings-main-container">
      <div class="settings-sidebar">
        <div
          v-for="tab in tabs"
          :key="tab.value"
          class="sidebar-item"
          :class="{ 'is-active': activeTab === tab.value }"
          @click="activeTab = tab.value"
        >
          <el-icon>
            <User v-if="tab.value === 'profile'" />
            <Lock v-else-if="tab.value === 'security'" />
          </el-icon>
          <div class="sidebar-item__text">
            <span class="sidebar-item__label">{{ tab.label }}</span>
            <span class="sidebar-item__desc">{{ tab.desc }}</span>
          </div>
        </div>
      </div>

      <div class="settings-content">
        <transition name="el-fade-in-linear" mode="out-in">
          <div v-if="activeTab === 'profile'" key="profile" class="settings-panel">
            <div class="settings-panel-header">
              <h3>个人资料</h3>
              <p>管理您的基本账号信息与联系方式。</p>
            </div>

            <!-- User Overview Card -->
            <div class="user-overview-card">
              <div class="user-overview-info">
                <div class="user-overview-name">
                  {{ userStore.user?.nickName || userStore.user?.userName || '用户' }}
                  <el-tag v-if="userStore.preferredPortalRole" size="small" class="role-tag" effect="light">
                    {{ roleLabel }}
                  </el-tag>
                </div>
                <div class="user-overview-meta">
                  <span>账号：{{ userStore.user?.userName || '-' }}</span>
                  <span v-if="userStore.user?.loginDate" class="meta-dot">·</span>
                  <span v-if="userStore.user?.loginDate">上次登录：{{ userStore.user.loginDate }}</span>
                </div>
              </div>
            </div>

            <div class="form-card">
              <div class="form-card__title">基本信息</div>
              <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-position="top" class="settings-form">
                <el-row :gutter="32">
                  <el-col :span="12">
                    <el-form-item label="昵称" prop="nickName">
                      <el-input v-model="profileForm.nickName" maxlength="30" placeholder="请输入昵称" size="large">
                        <template #prefix><el-icon><User /></el-icon></template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="真实姓名" prop="realName">
                      <el-input v-model="profileForm.realName" maxlength="30" placeholder="请输入真实姓名" size="large">
                        <template #prefix><el-icon><Postcard /></el-icon></template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="手机号" prop="phonenumber">
                      <el-input v-model="profileForm.phonenumber" maxlength="11" placeholder="请输入手机号" size="large">
                        <template #prefix><el-icon><Iphone /></el-icon></template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="邮箱" prop="email">
                      <el-input v-model="profileForm.email" maxlength="50" placeholder="请输入邮箱" size="large">
                        <template #prefix><el-icon><Message /></el-icon></template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </div>

            <div class="settings-actions">
              <el-button type="primary" size="large" :loading="profileSaving" class="submit-btn" @click="submitProfile">保存资料</el-button>
            </div>
          </div>

          <div v-else-if="activeTab === 'security'" key="security" class="settings-panel">
            <div class="settings-panel-header">
              <h3>账号安全</h3>
              <p>维护密码强度与登录安全，保护您的账号不受未授权访问。</p>
            </div>

            <div class="security-status-cards mb-4">
              <el-card shadow="never" class="status-card success-card">
                <div class="status-icon success"><el-icon><CircleCheckFilled /></el-icon></div>
                <div class="status-info">
                  <h4>基础防护已启用</h4>
                  <p>您的账号目前处于安全状态</p>
                </div>
              </el-card>
              <el-card shadow="never" class="status-card warning-card">
                <div class="status-icon warning"><el-icon><WarningFilled /></el-icon></div>
                <div class="status-info">
                  <h4>建议定期更新</h4>
                  <p>建议每 3 个月更换一次密码</p>
                </div>
              </el-card>
            </div>

            <el-divider border-style="dashed" class="my-8" />

            <div class="password-section">
              <h4 class="section-title">修改密码</h4>
              <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="120px" label-position="left" class="password-form">
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入当前密码" size="large" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码（至少6位）" size="large" />
                  <!-- Password Strength Indicator -->
                  <div v-if="passwordForm.newPassword" class="password-strength">
                    <div class="strength-bar">
                      <div class="strength-bar__fill" :class="strengthClass" :style="{ width: strengthPercent + '%' }" />
                    </div>
                    <span class="strength-label" :class="strengthClass">{{ strengthLabel }}</span>
                  </div>
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" size="large" />
                </el-form-item>
                <el-form-item class="mt-8">
                  <el-button size="large" @click="resetPasswordForm">重置</el-button>
                  <el-button type="primary" size="large" :loading="passwordSaving" @click="submitPassword">更新密码</el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from '@/utils/feedback'
import usePortalUserStore from '@/store/user'
import { getPortalProfile, updatePortalPassword, updatePortalProfile } from '@/api/portal'
import {
  CircleCheckFilled,
  Iphone,
  Lock,
  Message,
  Postcard,
  User,
  WarningFilled,
} from '@element-plus/icons-vue'

const userStore = usePortalUserStore()
const profileFormRef = ref()
const passwordFormRef = ref()
const activeTab = ref<'profile' | 'security'>('profile')
const pageLoading = ref(false)
const profileSaving = ref(false)
const passwordSaving = ref(false)

const tabs = [
  { value: 'profile', label: '个人资料', desc: '基础资料与联系方式' },
  { value: 'security', label: '账号安全', desc: '密码与安全维护' },
] as const

const roleLabels: Record<string, string> = {
  student: '学生',
  teacher: '教师',
  advisor: '辅导员',
  parent: '家长',
}

const roleLabel = computed(() => roleLabels[userStore.preferredPortalRole] || '用户')

interface ProfileForm {
  nickName: string
  realName: string
  phonenumber: string
  email: string
}

const profileForm = reactive<ProfileForm>({
  nickName: '',
  realName: '',
  phonenumber: '',
  email: '',
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const profileRules = {
  nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phonenumber: [{ pattern: /^$|^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }],
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }],
  confirmPassword: [{
    required: true,
    validator: (_rule: any, value: string, callback: (error?: Error) => void) => {
      if (!value) {
        callback(new Error('请确认新密码'))
        return
      }
      if (value !== passwordForm.newPassword) {
        callback(new Error('两次输入的新密码不一致'))
        return
      }
      callback()
    },
    trigger: 'blur'
  }],
}

/* ===================== Password Strength ===================== */
const passwordStrength = computed(() => {
  const pw = passwordForm.newPassword
  if (!pw) return 0
  let score = 0
  if (pw.length >= 6) score += 1
  if (pw.length >= 10) score += 1
  if (/[a-z]/.test(pw) && /[A-Z]/.test(pw)) score += 1
  if (/\d/.test(pw)) score += 1
  if (/[^a-zA-Z0-9]/.test(pw)) score += 1
  return score
})

const strengthClass = computed(() => {
  const s = passwordStrength.value
  if (s <= 1) return 'weak'
  if (s <= 3) return 'medium'
  return 'strong'
})

const strengthPercent = computed(() => {
  return Math.min(100, (passwordStrength.value / 5) * 100)
})

const strengthLabel = computed(() => {
  const s = passwordStrength.value
  if (s <= 1) return '弱'
  if (s <= 3) return '中'
  return '强'
})

/* ===================== Data Loading ===================== */
async function loadProfile() {
  pageLoading.value = true
  try {
    const res = await getPortalProfile()
    const user = res.data?.user || {}
    Object.assign(profileForm, {
      nickName: user.nickName || '',
      realName: user.realName || '',
      phonenumber: user.phonenumber || '',
      email: user.email || '',
    })
  } catch {
    ElMessage.error('加载个人资料失败')
  } finally {
    pageLoading.value = false
  }
}

async function submitProfile() {
  await profileFormRef.value?.validate()
  profileSaving.value = true
  try {
    await updatePortalProfile({ ...profileForm })
    await userStore.refreshUserInfo()
    ElMessage.success('个人资料已更新')
  } catch {
    ElMessage.error('更新个人资料失败')
  } finally {
    profileSaving.value = false
  }
}

function resetPasswordForm() {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

async function submitPassword() {
  await passwordFormRef.value?.validate()
  passwordSaving.value = true
  try {
    await updatePortalPassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    })
    ElMessage.success('密码修改成功')
    resetPasswordForm()
  } catch {
    ElMessage.error('密码修改失败，请检查旧密码是否正确')
  } finally {
    passwordSaving.value = false
  }
}

onMounted(async () => {
  await loadProfile()
})
</script>

<style scoped>
.settings-page {
  padding: 24px;
  background: transparent;
}

.settings-header {
  margin-bottom: 24px;
}

.settings-header h2 {
  margin: 0 0 8px 0;
  font-size: 22px;
  color: var(--portal-text);
  font-weight: 600;
  letter-spacing: 0.5px;
}

.settings-header p {
  margin: 0;
  color: var(--portal-text-secondary);
  font-size: 14px;
}

.settings-main-container {
  display: flex;
  align-items: flex-start;
  min-height: calc(100vh - 220px);
}

.settings-sidebar {
  width: 240px;
  padding-right: 32px;
  flex-shrink: 0;
  position: sticky;
  top: 24px;
}

.sidebar-item {
  display: flex;
  align-items: flex-start;
  padding: 14px 20px;
  border-radius: 8px;
  cursor: pointer;
  color: var(--portal-text-secondary);
  font-size: 15px;
  margin-bottom: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.sidebar-item:hover {
  background: #f0f2f5;
  color: var(--portal-text);
}

.sidebar-item.is-active {
  background: var(--portal-brand-light, #e6f1fc);
  color: var(--portal-brand);
  font-weight: 500;
}

.sidebar-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  bottom: 8px;
  width: 3px;
  border-radius: 0 3px 3px 0;
  background: var(--portal-brand);
}

.sidebar-item > .el-icon {
  margin-right: 12px;
  font-size: 18px;
  margin-top: 2px;
  flex-shrink: 0;
}

.sidebar-item__text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.sidebar-item__label {
  font-size: 14px;
  line-height: 1.4;
}

.sidebar-item__desc {
  font-size: 12px;
  color: var(--portal-text-secondary);
  opacity: 0.7;
  line-height: 1.3;
}

.sidebar-item.is-active .sidebar-item__desc {
  opacity: 0.85;
  color: var(--portal-brand);
}

.settings-content {
  flex: 1;
  padding: 0 0 80px 40px;
  border-left: 1px solid #ebeef5;
  position: relative;
}

.settings-panel {
  max-width: 800px;
}

.settings-panel-header {
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.settings-panel-header h3 {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: var(--portal-text);
  font-weight: 600;
}

.settings-panel-header p {
  margin: 0;
  font-size: 14px;
  color: var(--portal-text-secondary);
}

/* User Overview Card */
.user-overview-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 24px;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.04), rgba(255, 255, 255, 0.8));
  margin-bottom: 24px;
}

.user-overview-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.user-overview-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--portal-text);
  display: flex;
  align-items: center;
  gap: 10px;
}

.role-tag {
  font-size: 12px;
  border-radius: 4px;
}

.user-overview-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--portal-text-secondary);
  font-size: 13px;
}

.meta-dot {
  color: #dcdfe6;
}

/* Form Card */
.form-card {
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 20px 24px;
  background: #fff;
}

.form-card__title {
  font-size: 15px;
  font-weight: 600;
  color: var(--portal-text);
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f2f5;
}

.settings-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--portal-text);
  padding-bottom: 8px;
}

.settings-form :deep(.el-input__prefix) {
  color: var(--portal-text-secondary);
}

.settings-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-start;
}

.submit-btn {
  padding: 0 32px;
  font-weight: 500;
  border-radius: 8px;
}

/* Security Status Cards */
.security-status-cards {
  display: flex;
  gap: 20px;
}

.status-card {
  flex: 1;
  border-radius: 12px;
  border: 1px solid #ebeef5;
  background: #fdfdfe;
  transition: transform 0.3s, box-shadow 0.3s;
}

.status-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.success-card {
  border-left: 4px solid var(--portal-success, #67c23a);
}

.warning-card {
  border-left: 4px solid var(--portal-warning, #e6a23c);
}

.status-card :deep(.el-card__body) {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 24px;
}

.status-icon {
  font-size: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-icon.success {
  color: var(--portal-success, #67c23a);
}

.status-icon.warning {
  color: var(--portal-warning, #e6a23c);
}

.status-info h4 {
  margin: 0 0 6px 0;
  font-size: 16px;
  color: var(--portal-text);
  font-weight: 600;
}

.status-info p {
  margin: 0;
  font-size: 14px;
  color: var(--portal-text-secondary);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--portal-text);
  margin-bottom: 24px;
}

.password-form {
  max-width: 500px;
}

.password-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--portal-text);
}

/* Password Strength Indicator */
.password-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
  width: 100%;
}

.strength-bar {
  flex: 1;
  height: 4px;
  border-radius: 2px;
  background: #ebeef5;
  overflow: hidden;
}

.strength-bar__fill {
  height: 100%;
  border-radius: 2px;
  transition: width 0.3s, background 0.3s;
}

.strength-bar__fill.weak {
  background: #f56c6c;
}

.strength-bar__fill.medium {
  background: #e6a23c;
}

.strength-bar__fill.strong {
  background: #67c23a;
}

.strength-label {
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.strength-label.weak {
  color: #f56c6c;
}

.strength-label.medium {
  color: #e6a23c;
}

.strength-label.strong {
  color: #67c23a;
}

.my-8 {
  margin: 32px 0;
}

.mt-8 {
  margin-top: 32px;
}

.mb-4 {
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .settings-main-container {
    flex-direction: column;
  }

  .settings-sidebar {
    width: 100%;
    padding: 0 0 16px 0;
    position: static;
    display: flex;
    overflow-x: auto;
  }

  .sidebar-item {
    margin-bottom: 0;
    margin-right: 8px;
    white-space: nowrap;
    padding: 10px 16px;
  }

  .sidebar-item__desc {
    display: none;
  }

  .sidebar-item.is-active::before {
    display: none;
  }

  .security-status-cards {
    flex-direction: column;
  }

  .settings-content {
    padding: 24px 0 60px 0;
    border-left: none;
    border-top: 1px solid #ebeef5;
  }

  .user-overview-card {
    flex-direction: column;
    text-align: center;
  }

  .user-overview-name {
    justify-content: center;
  }

  .user-overview-meta {
    justify-content: center;
    flex-wrap: wrap;
  }

  .settings-form :deep(.el-col) {
    max-width: 100%;
    flex: 0 0 100%;
  }
}

@media (max-width: 640px) {
  .settings-page {
    padding: 14px;
  }

  .settings-header h2 {
    font-size: 18px;
  }

  .settings-header p {
    font-size: 13px;
  }

  .settings-sidebar {
    gap: 4px;
  }

  .sidebar-item {
    padding: 8px 12px;
    font-size: 13px;
  }

  .sidebar-item > .el-icon {
    font-size: 16px;
    margin-right: 8px;
  }

  .settings-content {
    padding: 16px 0 40px 0;
  }

  .settings-panel-header {
    margin-bottom: 20px;
    padding-bottom: 14px;
  }

  .settings-panel-header h3 {
    font-size: 17px;
  }

  .settings-panel-header p {
    font-size: 13px;
  }

  .user-overview-card {
    padding: 14px 16px;
    gap: 14px;
  }

  .user-overview-name {
    font-size: 16px;
    flex-wrap: wrap;
  }

  .user-overview-meta {
    font-size: 12px;
  }

  .form-card {
    padding: 14px 16px;
  }

  .form-card__title {
    font-size: 14px;
    margin-bottom: 14px;
    padding-bottom: 10px;
  }

  .security-status-cards {
    gap: 12px;
  }

  .status-card :deep(.el-card__body) {
    padding: 16px;
    gap: 12px;
  }

  .status-icon {
    font-size: 22px;
  }

  .status-info h4 {
    font-size: 14px;
  }

  .status-info p {
    font-size: 13px;
  }

  .password-form {
    max-width: 100%;
  }

  .password-strength {
    gap: 8px;
  }

  .submit-btn {
    padding: 0 24px;
    width: 100%;
  }

  .settings-actions {
    margin-top: 16px;
  }

  .my-8 {
    margin: 20px 0;
  }

  .mt-8 {
    margin-top: 20px;
  }

  .section-title {
    font-size: 15px;
    margin-bottom: 16px;
  }
}
</style>
