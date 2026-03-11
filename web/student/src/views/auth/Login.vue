<template>
  <div class="portal-login">
    <div class="login-card">
      <div class="brand">
        <h1>智慧校园学习门户</h1>
        <p>覆盖学生、教师、家长三类端的统一学习入口</p>
      </div>
      <el-form :model="form" class="login-form" @keyup.enter="handleLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入账号" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" size="large" />
        </el-form-item>
        <el-form-item v-if="userStore.captchaEnabled">
          <div class="captcha-row">
            <el-input v-model="form.code" placeholder="验证码" size="large" />
            <img class="captcha-img" :src="userStore.codeUrl" @click="refreshCaptcha" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">登录门户</el-button>
        </el-form-item>
      </el-form>
      <div class="tips">
        <span>登录后将根据角色自动跳转学生端、教师端或家长端，并记住上一次访问的端口身份。</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const userStore = usePortalUserStore()
const loading = ref(false)
const form = reactive({ username: 'admin', password: 'admin123', code: '' })

function resolveRoleHome() {
  const role = userStore.preferredPortalRole
  return `/${role}/dashboard`
}

async function refreshCaptcha() {
  await userStore.loadCaptcha()
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
  } finally {
    loading.value = false
  }
}

onMounted(refreshCaptcha)
</script>

<style scoped>
.portal-login{display:flex;align-items:center;justify-content:center;min-height:100vh;background:linear-gradient(135deg,#0f1f39 0%,#0f4fb5 48%,#26a1ff 100%);padding:24px}
.login-card{width:460px;max-width:100%;background:rgba(255,255,255,.96);border-radius:24px;box-shadow:0 24px 60px rgba(10,20,40,.22);padding:32px}
.brand h1{margin:0 0 10px;font-size:28px;color:#1f2d3d}.brand p{margin:0 0 24px;color:#5b6777;line-height:1.8}
.login-btn{width:100%}.captcha-row{display:grid;grid-template-columns:1fr 120px;gap:12px;width:100%}.captcha-img{width:120px;height:40px;border-radius:10px;border:1px solid #dbe7f5;cursor:pointer}.tips{margin-top:16px;color:#6b7280;font-size:13px;line-height:1.8}
</style>
