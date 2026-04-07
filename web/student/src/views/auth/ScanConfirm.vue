<template>
  <main class="scan-confirm-page">
    <section class="scan-confirm-card">
      <div class="scan-confirm-card__brand">
        <img class="scan-confirm-card__brand-logo" :src="portalLogo" alt="教务管理信息系统">
      </div>
      <div class="scan-confirm-card__eyebrow">Scan Confirm</div>
      <h1>确认扫码登录</h1>
      <p v-if="ticket">当前桌面端会话：{{ ticket }}</p>
      <p v-else>未获取到扫码会话，请重新扫描二维码。</p>

      <div v-if="success" class="scan-confirm-card__result scan-confirm-card__result--success">
        已确认登录，请返回桌面端继续操作。
      </div>

      <div v-else-if="ticket" class="scan-confirm-card__form">
        <div class="scan-confirm-card__hint">可直接使用当前已登录账号确认，或输入账号密码完成授权。</div>

        <el-form :model="form" label-position="top" class="scan-confirm-form" @keyup.enter="handleConfirm">
          <el-form-item label="账号">
            <el-input v-model="form.username" clearable autocomplete="username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" autocomplete="current-password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-form-item v-if="captchaEnabled" label="验证码">
            <div class="scan-confirm-card__captcha">
              <el-input v-model="form.code" clearable inputmode="numeric" placeholder="验证码" />
              <img class="scan-confirm-card__captcha-img" :src="codeUrl" alt="验证码" @click="loadCaptcha" />
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="scan-confirm-card__submit" :loading="loading" @click="handleConfirm">确认登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from '@/utils/feedback'
import { confirmScanLogin, getCodeImg } from '@/api/auth'
import portalLogo from '@/assets/img/logo.png'

const route = useRoute()
const ticket = ref<string>(String(route.query.ticket || ''))
const loading = ref(false)
const success = ref(false)
const captchaEnabled = ref(true)
const codeUrl = ref('')
const uuid = ref('')
const form = reactive({
  username: '',
  password: '',
  code: '',
})

async function loadCaptcha() {
  const res = await getCodeImg()
  captchaEnabled.value = res.captchaEnabled !== false
  if (captchaEnabled.value) {
    codeUrl.value = `data:image/gif;base64,${res.img}`
    uuid.value = res.uuid || ''
  }
}

async function handleConfirm() {
  if (!ticket.value) {
    ElMessage.warning('未找到扫码会话')
    return
  }
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  if (captchaEnabled.value && !form.code) {
    ElMessage.warning('请输入验证码')
    return
  }
  loading.value = true
  try {
    await confirmScanLogin({
      ticket: ticket.value,
      username: form.username.trim(),
      password: form.password,
      code: form.code.trim(),
      uuid: uuid.value,
    })
    success.value = true
    ElMessage.success('扫码确认成功')
  } catch {
    if (captchaEnabled.value) {
      form.code = ''
      await loadCaptcha()
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (ticket.value) {
    await loadCaptcha()
  }
})
</script>

<style scoped>
.scan-confirm-page {
  min-height: 100dvh;
  display: grid;
  place-items: center;
  padding: 20px;
  background: var(--portal-bg);
}

.scan-confirm-card {
  width: min(420px, 100%);
  padding: 28px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--portal-border);
  box-shadow: var(--portal-shadow);
}

.scan-confirm-card__brand {
  display: flex;
  justify-content: center;
  margin-bottom: 14px;
}

.scan-confirm-card__brand-logo {
  display: block;
  width: 168px;
  max-width: 100%;
  height: auto;
  object-fit: contain;
}

.scan-confirm-card__eyebrow {
  color: var(--portal-brand);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.scan-confirm-card h1 {
  margin: 8px 0 10px;
  font-size: 30px;
  color: var(--portal-text);
}

.scan-confirm-card p {
  margin: 0;
  color: var(--portal-text-secondary);
  line-height: 1.7;
  font-size: 13px;
}

.scan-confirm-card__form {
  margin-top: 18px;
}

.scan-confirm-card__hint,
.scan-confirm-card__result {
  margin-bottom: 14px;
  padding: 10px 12px;
  border-radius: 4px;
  background: var(--portal-surface-bg);
  border: 1px solid var(--portal-border);
  color: var(--portal-text-secondary);
  font-size: 13px;
}

.scan-confirm-card__result--success {
  margin-top: 18px;
  margin-bottom: 0;
  background: #edf8f2;
  border-color: #cbe8d7;
  color: #1a7b56;
}

.scan-confirm-form :deep(.el-form-item) {
  margin-bottom: 12px;
}

.scan-confirm-form :deep(.el-form-item__label) {
  color: var(--portal-text-secondary);
  font-size: 13px;
  font-weight: 600;
  padding-bottom: 6px;
}

.scan-confirm-form :deep(.el-input__wrapper) {
  min-height: 40px;
  border-radius: 4px;
  background: #f8fbff;
  border: 1px solid #dfe8f4;
  box-shadow: none;
}

.scan-confirm-card__captcha {
  display: grid;
  grid-template-columns: 1fr 108px;
  gap: 10px;
}

.scan-confirm-card__captcha-img {
  width: 108px;
  height: 40px;
  border-radius: 4px;
  border: 1px solid #d8e3f4;
  background: #fff;
  cursor: pointer;
}

.scan-confirm-card__submit {
  width: 100%;
  height: 40px;
  border-radius: 4px;
  font-weight: 700;
}

/* ---- Responsive ---- */
@media (max-width: 768px) {
  .scan-confirm-shell { padding: 16px 12px 24px; }
  .scan-confirm-card { padding: 22px 18px 20px; }
  .scan-confirm-card h1 { font-size: 24px; }
  .scan-confirm-card__logo { height: 56px; }
}

@media (max-width: 640px) {
  .scan-confirm-card { padding: 18px 14px 16px; }
  .scan-confirm-card h1 { font-size: 22px; }
  .scan-confirm-card__captcha { grid-template-columns: 1fr 90px; }
}
</style>
