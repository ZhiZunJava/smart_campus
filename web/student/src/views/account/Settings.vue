<template>
  <div class="portal-page settings-page">
    <section class="portal-hero settings-hero">
      <div>
        <div class="portal-hero__badge">Settings Center</div>
        <div class="portal-hero__title">账号设置</div>
        <p class="portal-hero__desc">统一管理个人资料、安全信息与使用偏好，后续可继续扩展更多配置分组。</p>
      </div>
      <div class="portal-hero__panel">
        <div class="portal-hero__metric">
          <span>当前模块</span>
          <strong>{{ activeTabLabel }}</strong>
        </div>
        <div class="portal-hero__metric">
          <span>配置分组</span>
          <strong>资料 / 安全 / 偏好</strong>
        </div>
      </div>
    </section>

    <section class="portal-card mt16 settings-shell">
      <aside class="settings-sidebar">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          type="button"
          class="settings-sidebar__item"
          :class="{ 'is-active': activeTab === tab.value }"
          @click="activeTab = tab.value"
        >
          <strong>{{ tab.label }}</strong>
          <span>{{ tab.desc }}</span>
        </button>
      </aside>

      <section class="settings-content">
        <section v-if="activeTab === 'profile'" class="settings-panel">
          <div class="settings-panel__head">
            <span class="settings-panel__eyebrow">Profile</span>
            <h3>个人资料</h3>
            <p>完善基础资料、联系信息与学习画像。</p>
          </div>

          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-position="top" class="settings-form">
            <div class="settings-grid">
              <el-form-item label="昵称" prop="nickName">
                <el-input v-model="profileForm.nickName" maxlength="30" />
              </el-form-item>
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="profileForm.realName" maxlength="30" />
              </el-form-item>
              <el-form-item label="手机号" prop="phonenumber">
                <el-input v-model="profileForm.phonenumber" maxlength="11" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="profileForm.email" maxlength="50" />
              </el-form-item>
              <el-form-item label="性别" prop="sex">
                <el-select v-model="profileForm.sex" placeholder="请选择性别">
                  <el-option label="男" value="0" />
                  <el-option label="女" value="1" />
                  <el-option label="未知" value="2" />
                </el-select>
              </el-form-item>
              <el-form-item label="专业 / 学科" prop="major">
                <el-input v-model="profileForm.major" maxlength="50" />
              </el-form-item>
              <el-form-item label="入学年份" prop="admissionYear">
                <el-input v-model="profileForm.admissionYear" maxlength="4" />
              </el-form-item>
              <el-form-item label="学习风格" prop="learningStyle">
                <el-input v-model="profileForm.learningStyle" maxlength="100" />
              </el-form-item>
              <el-form-item class="settings-grid__full" label="学习目标" prop="learningGoal">
                <el-input v-model="profileForm.learningGoal" type="textarea" :rows="3" maxlength="200" show-word-limit />
              </el-form-item>
              <el-form-item class="settings-grid__full" label="兴趣标签" prop="interestTags">
                <el-input v-model="profileForm.interestTags" maxlength="200" placeholder="多个标签请用逗号分隔" />
              </el-form-item>
            </div>
          </el-form>

          <div class="settings-actions">
            <el-button type="primary" @click="submitProfile">保存资料</el-button>
          </div>
        </section>

        <section v-else-if="activeTab === 'security'" class="settings-panel">
          <div class="settings-panel__head">
            <span class="settings-panel__eyebrow">Security</span>
            <h3>账号安全</h3>
            <p>维护密码强度与登录安全，便于后续扩展设备与登录记录配置。</p>
          </div>

          <div class="settings-security">
            <div class="settings-security__summary">
              <div class="security-chip">
                <span>当前状态</span>
                <strong>基础防护已启用</strong>
              </div>
              <div class="security-chip">
                <span>建议操作</span>
                <strong>定期更新密码</strong>
              </div>
            </div>

            <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="top" class="settings-form">
              <div class="settings-grid">
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                </el-form-item>
              </div>
            </el-form>
          </div>

          <div class="settings-actions">
            <el-button @click="resetPasswordForm">重置</el-button>
            <el-button type="primary" plain @click="submitPassword">更新密码</el-button>
          </div>
        </section>

        <section v-else class="settings-panel">
          <div class="settings-panel__head">
            <span class="settings-panel__eyebrow">Preferences</span>
            <h3>使用偏好</h3>
            <p>维护默认角色、显示方式与提醒选项，后续可继续扩展更多系统偏好。</p>
          </div>

          <div class="settings-grid">
            <div class="pref-card">
              <span>默认角色</span>
              <el-select v-model="preferredRole" placeholder="请选择默认角色">
                <el-option label="学生端" value="student" />
                <el-option label="教师端" value="teacher" />
                <el-option label="家长端" value="parent" />
              </el-select>
            </div>
            <div class="pref-card">
              <span>通知提醒</span>
              <div class="pref-card__control">
                <el-switch v-model="notifyEnabled" />
                <small>控制页面内提醒与提示展示</small>
              </div>
            </div>
            <div class="pref-card">
              <span>紧凑模式</span>
              <div class="pref-card__control">
                <el-switch v-model="compactEnabled" />
                <small>减少卡片与内容区留白，提升信息密度</small>
              </div>
            </div>
          </div>

          <div class="settings-actions">
            <el-button type="primary" @click="savePreferences">保存偏好</el-button>
          </div>
        </section>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import usePortalUserStore from '@/store/user'
import { getPortalProfile, updatePortalPassword, updatePortalProfile } from '@/api/portal'

const userStore = usePortalUserStore()
const profileFormRef = ref()
const passwordFormRef = ref()
const activeTab = ref<'profile' | 'security' | 'preferences'>('profile')
const preferredRole = ref(userStore.preferredPortalRole)
const notifyEnabled = ref(localStorage.getItem('portal-notify-enabled') !== '0')
const compactEnabled = ref(localStorage.getItem('portal-compact-enabled') === '1')

const tabs = [
  { value: 'profile', label: '个人资料', desc: '基础资料与学习画像' },
  { value: 'security', label: '账号安全', desc: '密码与安全维护' },
  { value: 'preferences', label: '使用偏好', desc: '角色与界面偏好' },
] as const

const activeTabLabel = computed(() => tabs.find((tab) => tab.value === activeTab.value)?.label || '个人资料')

const profileForm = reactive<any>({
  nickName: '',
  realName: '',
  phonenumber: '',
  email: '',
  sex: '2',
  major: '',
  admissionYear: '',
  learningGoal: '',
  interestTags: '',
  learningStyle: '',
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
    sex: user.sex || '2',
    major: user.major || '',
    admissionYear: user.admissionYear || '',
    learningGoal: user.learningGoal || '',
    interestTags: user.interestTags || '',
    learningStyle: user.learningStyle || '',
  })
}

async function submitProfile() {
  await profileFormRef.value?.validate()
  await updatePortalProfile({
    ...profileForm,
    admissionYear: profileForm.admissionYear ? Number(profileForm.admissionYear) : undefined,
  })
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

function savePreferences() {
  userStore.setPreferredPortalRole(preferredRole.value)
  localStorage.setItem('portal-notify-enabled', notifyEnabled.value ? '1' : '0')
  localStorage.setItem('portal-compact-enabled', compactEnabled.value ? '1' : '0')
  ElMessage.success('使用偏好已保存')
}

onMounted(async () => {
  await loadProfile()
})
</script>

<style scoped>
.settings-hero {
  align-items: stretch;
}

.settings-shell {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  overflow: hidden;
}

.settings-sidebar {
  padding: 16px;
  border-right: 1px solid var(--portal-border);
  background: var(--portal-surface-bg);
  display: grid;
  align-content: start;
  gap: 8px;
}

.settings-sidebar__item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px 14px;
  border-radius: 6px;
  border: 1px solid transparent;
  background: transparent;
  text-align: left;
  cursor: pointer;
  transition: background-color .2s ease, border-color .2s ease, transform .2s ease;
}

.settings-sidebar__item strong {
  font-size: 14px;
  color: var(--portal-text);
}

.settings-sidebar__item span {
  font-size: 12px;
  color: var(--portal-text-secondary);
  line-height: 1.6;
}

.settings-sidebar__item:hover,
.settings-sidebar__item.is-active {
  background: #fff;
  border-color: var(--portal-border);
  transform: translateY(-1px);
}

.settings-content {
  min-width: 0;
  padding: 16px;
}

.settings-panel {
  display: grid;
  gap: 14px;
}

.settings-panel__eyebrow {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  min-height: 24px;
  padding: 0 10px;
  border-radius: 999px;
  background: var(--portal-brand-light);
  color: var(--portal-brand);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: .08em;
  text-transform: uppercase;
}

.settings-panel__head h3 {
  margin: 0;
  font-size: 18px;
  color: var(--portal-text);
}

.settings-panel__head p {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--portal-text-secondary);
  line-height: 1.7;
}

.settings-form {
  margin-top: 2px;
}

.settings-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 14px;
}

.settings-grid__full {
  grid-column: 1 / -1;
}

.settings-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 4px;
}

.settings-security {
  display: grid;
  gap: 14px;
}

.settings-security__summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.security-chip,
.pref-card {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 14px;
  border-radius: 6px;
  background: var(--portal-surface-bg);
  border: 1px solid var(--portal-border);
}

.security-chip span,
.pref-card span {
  font-size: 13px;
  font-weight: 600;
  color: var(--portal-text);
}

.security-chip strong {
  font-size: 18px;
  color: var(--portal-brand);
}

.pref-card__control {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pref-card__control small {
  color: var(--portal-text-secondary);
  line-height: 1.6;
}

@media (max-width: 1100px) {
  .settings-shell {
    grid-template-columns: 1fr;
  }

  .settings-sidebar {
    border-right: none;
    border-bottom: 1px solid var(--portal-border);
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .settings-grid,
  .settings-security__summary,
  .settings-sidebar {
    grid-template-columns: 1fr;
  }
}
</style>
