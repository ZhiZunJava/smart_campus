<template>
  <div class="portal-page settings-page !pb-5">
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
            <Setting v-else-if="tab.value === 'preferences'" />
          </el-icon>
          <span>{{ tab.label }}</span>
        </div>
      </div>

      <div class="settings-content">
        <transition name="el-fade-in-linear" mode="out-in">
          <div v-if="activeTab === 'profile'" key="profile" class="settings-panel">
            <div class="settings-panel-header">
              <h3>个人资料</h3>
              <p>管理您的基本账号信息与联系方式。</p>
            </div>

            <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-position="top" class="settings-form">
              <el-row :gutter="32">
                <el-col :span="12">
                  <el-form-item label="昵称" prop="nickName">
                    <el-input v-model="profileForm.nickName" maxlength="30" placeholder="请输入昵称" size="large" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="真实姓名" prop="realName">
                    <el-input v-model="profileForm.realName" maxlength="30" placeholder="请输入真实姓名" size="large" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="手机号" prop="phonenumber">
                    <el-input v-model="profileForm.phonenumber" maxlength="11" placeholder="请输入手机号" size="large" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="邮箱" prop="email">
                    <el-input v-model="profileForm.email" maxlength="50" placeholder="请输入邮箱" size="large" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>

            <div class="settings-actions">
              <el-button type="primary" size="large" @click="submitProfile" class="submit-btn">保存资料</el-button>
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
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" size="large" />
                </el-form-item>
                <el-form-item class="mt-8">
                  <el-button size="large" @click="resetPasswordForm">重置</el-button>
                  <el-button type="primary" size="large" @click="submitPassword">更新密码</el-button>
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
import { User, Lock, CircleCheckFilled, WarningFilled, Setting } from '@element-plus/icons-vue'

const userStore = usePortalUserStore()
const profileFormRef = ref()
const passwordFormRef = ref()
const activeTab = ref<'profile' | 'security'>('profile')

const tabs = [
  { value: 'profile', label: '个人资料', desc: '基础资料与学习偏好' },
  { value: 'security', label: '账号安全', desc: '密码与安全维护' },
] as const

const activeTabLabel = computed(() => tabs.find((tab) => tab.value === activeTab.value)?.label || '个人资料')

const profileForm = reactive<any>({
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

async function loadProfile() {
  const res = await getPortalProfile()
  const user = res.data?.user || {}
  Object.assign(profileForm, {
    nickName: user.nickName || '',
    realName: user.realName || '',
    phonenumber: user.phonenumber || '',
    email: user.email || '',
  })
}

async function submitProfile() {
  await profileFormRef.value?.validate()
  await updatePortalProfile({ ...profileForm })
  await userStore.refreshUserInfo()
  ElMessage.success('个人资料已更新')
}

function resetPasswordForm() {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

async function submitPassword() {
  await passwordFormRef.value?.validate()
  await updatePortalPassword({
    oldPassword: passwordForm.oldPassword,
    newPassword: passwordForm.newPassword,
  })
  ElMessage.success('密码修改成功')
  resetPasswordForm()
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
  align-items: center;
  padding: 14px 20px;
  border-radius: 8px;
  cursor: pointer;
  color: var(--portal-text-secondary);
  font-size: 15px;
  margin-bottom: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
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

.sidebar-item .el-icon {
  margin-right: 12px;
  font-size: 18px;
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
  margin-bottom: 40px;
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

.w-full {
  width: 100%;
}

.settings-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--portal-text);
  padding-bottom: 8px;
}

.settings-actions {
  margin-top: 40px;
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

.my-8 {
  margin: 32px 0;
}

.mt-8 {
  margin-top: 32px;
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
  }
  
  .security-status-cards {
    flex-direction: column;
  }
  
  .settings-content {
    padding: 24px 0 60px 0;
    border-left: none;
    border-top: 1px solid #ebeef5;
  }
}
</style>
