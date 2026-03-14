<template>
  <div class="portal-auth-page">
    <PortalTopBar
      :menu-items="topMenus"
      :active-path="activeMenu"
      :selected-role="selectedRole"
      :role-options-override="publicRoleOptions"
      :show-menu="true"
      :show-role-switch="true"
      :show-auth-action="true"
      :show-user-panel="false"
      @role-change="handleRoleChange"
    />

    <section class="portal-auth-wrap">
      <div class="portal-auth-card">
        <div class="portal-auth-card__visual">
          <div class="portal-auth-card__badge">统一身份认证</div>
          <h2>智慧校园统一门户</h2>
          <p>面向学生、教师、家长与管理员的统一入口，支持按角色进入不同服务视图。</p>
          <div class="portal-auth-card__illustration">
            <div class="circle one"></div>
            <div class="circle two"></div>
            <div class="screen">
              <div class="screen__header"></div>
              <div class="screen__body"></div>
            </div>
          </div>
        </div>

        <div class="portal-auth-card__form">
          <div class="form-title">统一身份认证登录</div>
          <div class="form-tabs">
            <button type="button" class="active">账号登录</button>
            <button type="button">扫码登录</button>
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
                <el-input v-model="form.code" placeholder="请输入验证码" size="large" />
                <img class="captcha-img" :src="userStore.codeUrl" @click="refreshCaptcha" />
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">登录</el-button>
            </el-form-item>
          </el-form>

          <div class="login-bottom">
            <span>将按右上角角色偏好进入对应门户</span>
            <router-link to="/register">立即注册</router-link>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import PortalTopBar from '@/components/PortalTopBar.vue'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const userStore = usePortalUserStore()
const loading = ref(false)
const form = reactive({ username: 'admin', password: 'admin123', code: '' })
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

function resolveRoleHome() {
  const role = userStore.preferredPortalRole
  return `/${role}/dashboard`
}

async function refreshCaptcha() {
  await userStore.loadCaptcha()
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
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  selectedRole.value = userStore.preferredPortalRole
  await refreshCaptcha()
})
</script>

<style scoped>
.portal-auth-page{min-height:100vh;background:#f3f5f8}
.portal-auth-wrap{padding:48px 24px 30px;display:flex;justify-content:center}.portal-auth-card{width:920px;max-width:100%;display:grid;grid-template-columns:1.05fr .8fr;background:rgba(255,255,255,.98);border-radius:12px;overflow:hidden;box-shadow:0 18px 42px rgba(15,35,66,.08);border:1px solid #edf1f6}
.portal-auth-card__visual{padding:42px 48px;background:linear-gradient(180deg,#f8fbff 0%,#f5f8fd 100%)}.portal-auth-card__badge{display:inline-flex;padding:6px 12px;border-radius:999px;background:#eaf3ff;color:#2c86ff;font-size:12px;font-weight:700}.portal-auth-card__visual h2{margin:20px 0 12px;font-size:34px;color:#1a2d4d}.portal-auth-card__visual p{margin:0;color:#728199;line-height:1.9}
.portal-auth-card__illustration{position:relative;height:310px;margin-top:26px;border-radius:24px;background:linear-gradient(135deg,#eef5ff 0%,#f8fbff 100%);overflow:hidden}.circle{position:absolute;border-radius:50%}.circle.one{width:220px;height:220px;left:36px;top:44px;background:rgba(44,134,255,.12)}.circle.two{width:140px;height:140px;right:42px;bottom:36px;background:rgba(48,195,142,.14)}.screen{position:absolute;left:88px;top:54px;width:330px;height:210px;border-radius:18px;background:#0d3558;box-shadow:0 18px 32px rgba(13,53,88,.18)}.screen__header{height:28px;border-radius:18px 18px 0 0;background:#123f67}.screen__body{margin:22px;background:linear-gradient(180deg,#d6ebff 0%,#f6fbff 100%);height:138px;border-radius:14px}
.portal-auth-card__form{padding:52px 52px 40px}.form-title{font-size:34px;color:#26354f;text-align:center}.form-tabs{display:flex;justify-content:center;gap:30px;margin:28px 0 24px}.form-tabs button{border:none;background:transparent;font-size:18px;color:#60728c;cursor:pointer;padding-bottom:8px}.form-tabs button.active{color:#2c86ff;border-bottom:2px solid #2c86ff}
.login-form :deep(.el-input__wrapper){min-height:46px;background:#f5f7fb;box-shadow:none;border:1px solid transparent}.login-form :deep(.el-input__wrapper.is-focus){border-color:#b6d6ff;box-shadow:none}.captcha-row{display:grid;grid-template-columns:1fr 118px;gap:12px;width:100%}.captcha-img{width:118px;height:46px;border-radius:8px;border:1px solid #e4ebf5;cursor:pointer}.login-btn{width:100%;height:46px;border-radius:24px}
.login-bottom{display:flex;justify-content:space-between;align-items:center;margin-top:16px;color:#7d8ba0;font-size:13px}.login-bottom a{color:#2c86ff;text-decoration:none}
@media (max-width: 980px){.portal-auth-card{grid-template-columns:1fr}.portal-auth-card__visual{display:none}.portal-auth-wrap{padding:24px 16px 20px}.portal-auth-card__form{padding:34px 22px 26px}.form-title{font-size:28px}}
</style>
