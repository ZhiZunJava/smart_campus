<template>
  <main class="portal-auth-page">
    <PortalTopBar
      :menu-items="topMenus"
      :active-path="activeMenu"
      :selected-role="selectedRole"
      :role-options-override="publicRoleOptions"
      :show-menu="false"
      :show-role-switch="true"
      :show-auth-action="true"
      :show-user-panel="false"
      @role-change="handleRoleChange"
    />

    <section class="portal-auth-shell">
      <section class="auth-card">
        <div class="auth-card__top">
          <div>
            <div class="auth-card__eyebrow">Campus Access</div>
            <h1>账号登录</h1>
            <p>统一登录入口，登录后将根据当前角色设置进入对应工作台。</p>
          </div>
          <router-link class="auth-card__link" to="/register">注册</router-link>
        </div>

        <div class="auth-card__hint">
          当前将优先进入 {{ currentRoleLabel }}
        </div>

        <div v-if="!isMobile" class="auth-mode-tabs" role="tablist" aria-label="登录方式切换">
          <button
            type="button"
            :class="{ active: loginMode === 'account' }"
            role="tab"
            :aria-selected="loginMode === 'account'"
            @click="loginMode = 'account'"
          >
            账号登录
          </button>
          <button
            type="button"
            :class="{ active: loginMode === 'scan' }"
            role="tab"
            :aria-selected="loginMode === 'scan'"
            @click="loginMode = 'scan'"
          >
            扫码登录
          </button>
        </div>

        <transition name="auth-panel" mode="out-in">
          <div v-if="loginMode === 'account'" key="account" class="auth-card__body">
            <el-form :model="form" label-position="top" class="login-form" @keyup.enter="handleLogin">
              <el-form-item label="账号">
                <el-input v-model="form.username" clearable autocomplete="username" placeholder="请输入账号" />
              </el-form-item>
              <el-form-item label="密码">
                <el-input v-model="form.password" autocomplete="current-password" type="password" show-password placeholder="请输入密码" />
              </el-form-item>
              <el-form-item v-if="userStore.captchaEnabled" label="验证码">
                <div class="captcha-row">
                  <el-input v-model="form.code" clearable inputmode="numeric" placeholder="验证码" />
                  <img class="captcha-img" :src="userStore.codeUrl" alt="验证码" @click="refreshCaptcha" />
                </div>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登录</el-button>
              </el-form-item>
            </el-form>
          </div>

          <div v-else key="scan" class="scan-panel">
            <div class="scan-panel__code">
              <img v-if="scanQrCode" class="scan-panel__image" :src="scanQrCode" alt="扫码登录二维码" />
              <div v-else class="scan-panel__pixels"></div>
            </div>
            <h3>校园 App 扫码</h3>
            <p>使用手机扫描二维码，打开确认页并完成登录授权。</p>
            <div class="scan-panel__status">{{ scanStatusText }}</div>
            <el-button plain class="scan-panel__refresh" :loading="scanLoading" @click="startScanLogin">刷新二维码</el-button>
          </div>
        </transition>

        <div class="quick-login-divider">
          <span>快速登录</span>
        </div>
        <div class="quick-login-row">
          <button class="quick-login-btn" @click="quickLogin('zhangsan', '123456')">
            <i class="ri-user-3-line"></i>学生端
          </button>
          <button class="quick-login-btn" @click="quickLogin('lilaoshi', '123456')">
            <i class="ri-briefcase-4-line"></i>教师端
          </button>
        </div>

        <div class="auth-card__footer">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </section>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from '@/utils/feedback'
import { useRouter } from 'vue-router'
import PortalTopBar from '@/components/PortalTopBar.vue'
import usePortalUserStore from '@/store/user'
import loginBackground from '@/assets/img/login-background.jpg'
import portalLogo from '@/assets/img/logo.png'
import { createScanLoginSession, getScanLoginStatus } from '@/api/auth'

const router = useRouter()
const userStore = usePortalUserStore()
const loading = ref(false)
const windowWidth = ref(window.innerWidth)
const isMobile = computed(() => windowWidth.value <= 768)
const loginMode = ref<'account' | 'scan'>('account')
const scanLoading = ref(false)
const scanTicket = ref('')
const scanQrCode = ref('')
const scanStatusText = ref('请使用移动设备扫描二维码')
let scanPollTimer: number | null = null
const form = reactive({ username: '', password: '', code: '' })
const topMenus = [
  { title: '智慧校园', path: '/login' },
  { title: '服务专区', path: '/register' },
]
const publicRoleOptions = [
  { label: '学生端', value: 'student' },
  { label: '教师端', value: 'teacher' },
  { label: '家长端', value: 'parent' },
]
const activeMenu = ref('/login')
const selectedRole = ref(userStore.preferredPortalRole)
const currentRoleLabel = computed(() => publicRoleOptions.find((item) => item.value === selectedRole.value)?.label || '学生端')
const authBackground = `url(${loginBackground})`

function resolveRoleHome() {
  const role = userStore.preferredPortalRole
  return `/${role}/dashboard`
}

async function refreshCaptcha() {
  await userStore.loadCaptcha()
}

async function startScanLogin() {
  if (scanLoading.value) return
  scanLoading.value = true
  scanStatusText.value = '正在生成二维码...'
  try {
    const res = await createScanLoginSession(window.location.origin)
    scanTicket.value = res.ticket || ''
    scanQrCode.value = res.qrCode || ''
    scanStatusText.value = '请使用手机扫码并在移动端确认登录'
    beginScanPolling()
  } finally {
    scanLoading.value = false
  }
}

function stopScanPolling() {
  if (scanPollTimer) {
    window.clearInterval(scanPollTimer)
    scanPollTimer = null
  }
}

function beginScanPolling() {
  stopScanPolling()
  if (!scanTicket.value) return
  scanPollTimer = window.setInterval(async () => {
    try {
      const res = await getScanLoginStatus(scanTicket.value)
      const status = res.status || 'PENDING'
      if (status === 'CONFIRMED' && res.token) {
        stopScanPolling()
        userStore.acceptToken(res.token)
        await userStore.loadUserInfo()
        await router.replace(resolveRoleHome())
        ElMessage.success('扫码登录成功')
        return
      }
      if (status === 'EXPIRED') {
        stopScanPolling()
        scanStatusText.value = '二维码已过期，请刷新后重新扫码'
        return
      }
      if (res.username) {
        scanStatusText.value = `已扫码，等待 ${res.username} 确认登录`
      }
    } catch {
      stopScanPolling()
      scanStatusText.value = '扫码状态获取失败，请重新生成二维码'
    }
  }, 2000)
}

function quickLogin(username: string, password: string) {
  form.username = username
  form.password = password
  handleLogin()
}

function handleRoleChange(role: string) {
  selectedRole.value = role
  userStore.setPreferredPortalRole(role)
}

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  if (userStore.captchaEnabled && !form.code) {
    ElMessage.warning('请输入验证码')
    return
  }
  loading.value = true
  try {
    await userStore.loginAction(form)
    await userStore.loadUserInfo()
    await router.replace(resolveRoleHome())
    ElMessage.success('登录成功')
  } catch {
    if (userStore.captchaEnabled) {
      form.code = ''
      await refreshCaptcha()
    }
  } finally {
    loading.value = false
  }
}

function onResize() { windowWidth.value = window.innerWidth }

onMounted(async () => {
  window.addEventListener('resize', onResize)
  selectedRole.value = userStore.preferredPortalRole
  await refreshCaptcha()
})

watch(loginMode, async (mode) => {
  if (mode === 'scan') {
    await startScanLogin()
  } else {
    stopScanPolling()
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  stopScanPolling()
})

// 移动端自动切回账号登录
watch(isMobile, (mobile) => {
  if (mobile && loginMode.value === 'scan') {
    loginMode.value = 'account'
  }
})
</script>

<style scoped>
.portal-auth-page {
  min-height: 100dvh;
  display: grid;
  grid-template-rows: auto 1fr;
  overflow: hidden;
  background: v-bind(authBackground) center center / cover no-repeat;
  font-family: 'MiSans', "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
}

.portal-auth-page::before {
  content: '';
  position: fixed;
  inset: 58px 0 0 0;
  pointer-events: none;
  background: rgba(255, 255, 255, 0.08);
}

.portal-auth-shell {
  min-height: calc(100dvh - 58px);
  padding: 24px 16px 32px;
  display: grid;
  justify-items: center;
  align-items: center;
}

.auth-card {
  position: relative;
  z-index: 1;
  width: min(420px, calc(100vw - 32px));
  padding: 28px 28px 22px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(214, 224, 239, 0.96);
  box-shadow: 0 24px 64px rgba(16, 34, 66, 0.12);
  backdrop-filter: blur(18px);
}

.auth-card__top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.auth-card__top > div {
  min-width: 0;
  flex: 1;
}

.auth-card__brand {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 14px;
  padding: 8px 12px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid #dbe6f5;
  box-shadow: 0 10px 22px rgba(17, 38, 68, 0.08);
}

.auth-card__brand-logo {
  display: block;
  width: 168px;
  max-width: 100%;
  height: auto;
  object-fit: contain;
}

.auth-card__eyebrow {
  color: #3f73db;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.auth-card__top h1 {
  margin: 8px 0 0;
  color: #1b3456;
  font-size: 34px;
  line-height: 1.08;
}

.auth-card__top p {
  margin: 10px 0 0;
  color: #667b93;
  font-size: 13px;
  line-height: 1.8;
}

.auth-card__link,
.auth-card__footer a {
  color: #2a63d3;
  text-decoration: none;
  font-weight: 700;
}

.auth-card__link {
  flex-shrink: 0;
  align-self: flex-start;
  padding-top: 8px;
  font-size: 14px;
  line-height: 1;
  white-space: nowrap;
}

.auth-card__hint {
  margin-top: 18px;
  margin-bottom: 14px;
  padding: 8px 12px;
  border-radius: 4px;
  background: #f7f9fd;
  border: 1px solid #e4ebf5;
  color: #58708d;
  font-size: 12px;
  line-height: 1.6;
}

.auth-mode-tabs {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  padding: 5px;
  border-radius: 6px;
  background: #eef3fb;
}

.auth-mode-tabs button {
  height: 40px;
  border: none;
  border-radius: 4px;
  background: transparent;
  color: #6f8298;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.auth-mode-tabs button.active {
  background: #fff;
  color: #235dce;
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.06);
}

.auth-card__body {
  margin-top: 18px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 12px;
}

.login-form :deep(.el-form-item__content) {
  min-width: 0;
}

.login-form :deep(.el-form-item__label) {
  padding-bottom: 6px;
  color: #556a84;
  font-weight: 600;
  font-size: 13px;
}

.login-form :deep(.el-input__wrapper) {
  min-height: 42px;
  border-radius: 4px;
  background: #f8fbff;
  border: 1px solid #dfe8f4;
  box-shadow: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  background: #fff;
  border-color: #c7d8ef;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  background: #fff;
  border-color: #7baeff;
  box-shadow: 0 0 0 3px rgba(44, 134, 255, 0.12);
}

.captcha-row {
  display: grid;
  width: 100%;
  min-width: 0;
  grid-template-columns: minmax(0, 1fr) 96px;
  gap: 10px;
  align-items: center;
}

.captcha-row > * {
  min-width: 0;
}

.captcha-img {
  width: 100%;
  height: 42px;
  border-radius: 4px;
  border: 1px solid #d8e3f4;
  background: #fff;
  cursor: pointer;
  object-fit: cover;
  display: block;
}

.login-btn {
  width: 100%;
  height: 42px;
  border-radius: 4px;
  font-size: 15px;
  font-weight: 700;
  box-shadow: 0 14px 24px rgba(35, 93, 206, 0.16);
}

.quick-login-divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 16px 0 12px;
}

.quick-login-divider::before,
.quick-login-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e4ebf5;
}

.quick-login-divider span {
  flex-shrink: 0;
  color: #9aacbf;
  font-size: 11px;
  letter-spacing: 0.04em;
}

.quick-login-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.quick-login-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  width: 100%;
  height: 34px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  color: #667b93;
  border: 1px dashed #dbe6f5;
  background: transparent;
  cursor: pointer;
  transition: all 0.18s ease;
}

.quick-login-btn:hover {
  color: #3f73db;
  border-color: #a8c4f0;
  background: #f5f8fe;
}

.quick-login-btn i {
  font-size: 14px;
  line-height: 1;
}

.scan-panel {
  margin-top: 18px;
  display: grid;
  justify-items: center;
  text-align: center;
}

.scan-panel__code {
  position: relative;
  width: 220px;
  height: 220px;
  display: grid;
  place-items: center;
  padding: 10px;
  border-radius: 6px;
  background: #f7f9fd;
  border: 1px solid #dce7f8;
}

.scan-panel__pixels {
  position: absolute;
  inset: 26px;
  border-radius: 4px;
  background-image:
    linear-gradient(90deg, #173455 12px, transparent 12px),
    linear-gradient(#173455 12px, transparent 12px);
  background-size: 24px 24px;
  opacity: 0.16;
}

.scan-panel__finder {
  position: absolute;
  width: 48px;
  height: 48px;
  border: 8px solid #173455;
  border-radius: 4px;
}

.scan-panel__finder--one {
  left: 26px;
  top: 26px;
}

.scan-panel__finder--two {
  top: 26px;
  right: 26px;
}

.scan-panel__finder--three {
  left: 26px;
  bottom: 26px;
}

.scan-panel h3 {
  margin: 18px 0 8px;
  color: #20334f;
  font-size: 22px;
}

.scan-panel p {
  margin: 0;
  color: #6f8298;
  font-size: 13px;
  line-height: 1.8;
}

.scan-panel__image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 4px;
  background: #fff;
}

.scan-panel__status {
  margin-top: 10px;
  min-height: 40px;
  color: #58708d;
  font-size: 12px;
  line-height: 1.7;
}

.scan-panel__refresh {
  margin-top: 4px;
  width: 100%;
  height: 38px;
  border-radius: 4px;
}

.auth-card__footer {
  margin-top: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #7b8ba0;
  font-size: 13px;
}

.auth-panel-enter-active,
.auth-panel-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.auth-panel-enter-from,
.auth-panel-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

/* ─── Tablet (768px) ─── */
@media (max-width: 768px) {
  .portal-auth-page {
    grid-template-rows: auto 1fr;
  }

  .portal-auth-page::before {
    inset: 48px 0 0 0;
  }

  .portal-auth-shell {
    min-height: calc(100dvh - 48px);
    padding: 16px 12px 24px;
    align-items: start;
    padding-top: 24px;
  }

  .auth-card {
    width: 100%;
    max-width: 100%;
    padding: 22px 18px 20px;
    border-radius: 10px;
    box-shadow: 0 16px 48px rgba(16, 34, 66, 0.10);
  }

  .auth-card__top {
    flex-direction: column;
    gap: 6px;
  }

  .auth-card__top h1 {
    font-size: 26px;
  }

  .auth-card__top p {
    font-size: 12px;
    line-height: 1.7;
    margin-top: 6px;
  }

  .auth-card__link {
    padding-top: 0;
    font-size: 13px;
  }

  .auth-card__eyebrow {
    font-size: 11px;
  }

  .auth-card__hint {
    margin-top: 14px;
    margin-bottom: 12px;
    padding: 6px 10px;
    font-size: 11px;
  }

  .auth-card__body {
    margin-top: 14px;
  }

  .login-form :deep(.el-form-item__label) {
    font-size: 12px;
    padding-bottom: 4px;
  }

  .login-form :deep(.el-input__wrapper) {
    min-height: 44px;
  }

  .login-btn {
    height: 44px;
    font-size: 15px;
  }

  .captcha-row {
    grid-template-columns: minmax(0, 1fr) 90px;
  }

  .captcha-img {
    height: 44px;
  }

  .quick-login-divider {
    margin: 14px 0 10px;
  }

  .quick-login-btn {
    height: 38px;
    font-size: 13px;
    gap: 5px;
    border-radius: 6px;
  }

  .quick-login-btn i {
    font-size: 15px;
  }

  .auth-card__footer {
    margin-top: 12px;
    font-size: 12px;
  }
}

/* ─── Small mobile (≤420px) ─── */
@media (max-width: 420px) {
  .portal-auth-shell {
    padding: 12px 8px 20px;
  }

  .auth-card {
    padding: 18px 14px 16px;
    border-radius: 8px;
  }

  .auth-card__top h1 {
    font-size: 22px;
  }

  .auth-card__top p {
    font-size: 11px;
  }

  .quick-login-row {
    gap: 8px;
  }

  .quick-login-btn {
    height: 36px;
    font-size: 12px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .auth-mode-tabs button,
  .auth-panel-enter-active,
  .auth-panel-leave-active {
    transition: none !important;
  }
}
</style>
