<template>
  <main class="portal-register-page">
    <PortalTopBar
      :menu-items="topMenus"
      :active-path="activeMenu"
      :selected-role="activeRole"
      :role-options-override="publicRoleOptions"
      :show-menu="false"
      :show-role-switch="true"
      :show-auth-action="true"
      :show-user-panel="false"
      @role-change="handleRoleChange"
    />

    <section class="register-shell">
      <section class="register-card">
        <div class="register-card__top">
          <div>
            <div class="register-card__eyebrow">Account Register</div>
            <h1>统一身份注册</h1>
            <p>按角色完成账号创建，系统将根据身份类型识别对应门户与权限。</p>
          </div>
          <router-link class="register-card__link" to="/login">返回登录</router-link>
        </div>

        <el-tabs v-model="activeRole" stretch class="role-tabs">
          <el-tab-pane v-for="item in roleOptions" :key="item.value" :label="item.label" :name="item.value" />
        </el-tabs>

        <div class="stepper">
          <div
            v-for="(item, index) in stepItems"
            :key="item.key"
            class="stepper-item"
            :class="{ 'is-active': currentStep === index, 'is-done': currentStep > index }"
          >
            <div class="stepper-item__dot">{{ index + 1 }}</div>
            <div class="stepper-item__text">
              <strong>{{ item.title }}</strong>
              <span>{{ item.desc }}</span>
            </div>
          </div>
        </div>

        <div class="register-card__hint">
          {{ currentStepTip }}
        </div>

        <el-form ref="registerRef" :model="form" :rules="currentRules" label-position="top" class="register-form">
          <section v-show="currentStep === 0" class="step-panel">
            <el-row :gutter="10">
              <el-col :xs="24" :sm="12">
                <el-form-item label="登录账号" prop="username">
                  <el-input v-model="form.username" clearable autocomplete="username" placeholder="请输入登录账号" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="form.realName" clearable autocomplete="name" placeholder="请输入真实姓名" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="10">
              <el-col :xs="24" :sm="12">
                <el-form-item label="手机号" prop="phonenumber">
                  <el-input v-model="form.phonenumber" clearable autocomplete="tel" inputmode="tel" placeholder="请输入手机号" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" clearable autocomplete="email" placeholder="邮箱，可选" />
                </el-form-item>
              </el-col>
            </el-row>
          </section>

          <section v-show="currentStep === 1" class="step-panel">
            <el-row v-if="activeRole === 'student'" :gutter="10">
              <el-col :xs="24" :sm="12">
                <el-form-item label="学号" prop="studentNo">
                  <el-input v-model="form.studentNo" clearable placeholder="请输入学号" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="入学年份" prop="admissionYear">
                  <el-input-number v-model="form.admissionYear" class="register-number-input" :min="2000" :max="2099" controls-position="right" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row v-if="activeRole === 'teacher'" :gutter="10">
              <el-col :xs="24" :sm="12">
                <el-form-item label="工号" prop="teacherNo">
                  <el-input v-model="form.teacherNo" clearable placeholder="请输入工号" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="任教学科/方向" prop="major">
                  <el-input v-model="form.major" clearable placeholder="请输入任教学科或方向" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row v-if="activeRole === 'parent'" :gutter="10">
              <el-col :span="24">
                <el-form-item label="关联学生学号" prop="studentNo">
                  <el-input v-model="form.studentNo" clearable placeholder="请输入孩子的学号" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row v-if="activeRole === 'student' || activeRole === 'teacher'" :gutter="10">
              <el-col :span="24">
                <el-form-item :label="activeRole === 'student' ? '学习目标' : '培养方向'" prop="learningGoal">
                  <el-input v-model="form.learningGoal" type="textarea" :rows="3" maxlength="120" show-word-limit placeholder="可选" />
                </el-form-item>
              </el-col>
            </el-row>
          </section>

          <section v-show="currentStep === 2" class="step-panel">
            <el-row :gutter="10">
              <el-col :xs="24" :sm="12">
                <el-form-item label="密码" prop="password">
                  <el-input v-model="form.password" autocomplete="new-password" type="password" show-password placeholder="请输入密码" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="form.confirmPassword" autocomplete="new-password" type="password" show-password placeholder="请再次输入密码" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="10">
              <el-col :xs="24" :sm="12">
                <el-form-item label="性别" prop="sex">
                  <el-select v-model="form.sex" placeholder="请选择性别">
                    <el-option label="男" value="0" />
                    <el-option label="女" value="1" />
                    <el-option label="未知" value="2" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col v-if="captchaEnabled" :xs="24" :sm="12">
                <el-form-item label="验证码" prop="code">
                  <div class="captcha-row">
                    <el-input v-model="form.code" clearable inputmode="numeric" placeholder="验证码" />
                    <img class="captcha-img" :src="codeUrl" alt="验证码" @click="getCode" />
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
          </section>

          <div class="register-actions">
            <el-button v-if="currentStep > 0" class="register-actions__ghost" @click="currentStep -= 1">上一步</el-button>
            <el-button v-if="currentStep < stepItems.length - 1" type="primary" class="register-actions__primary" @click="goNextStep">下一步</el-button>
            <el-button v-else type="primary" class="register-actions__primary" :loading="loading" @click="handleRegister">完成注册</el-button>
          </div>
        </el-form>
      </section>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from '@/utils/feedback'
import { useRouter } from 'vue-router'
import PortalTopBar from '@/components/PortalTopBar.vue'
import usePortalUserStore from '@/store/user'
import { getCodeImg, register } from '@/api/auth'
import loginBackground from '@/assets/img/login-background.jpg'

const router = useRouter()
const userStore = usePortalUserStore()
const registerRef = ref()
const loading = ref(false)
const captchaEnabled = ref(true)
const codeUrl = ref('')
const currentStep = ref(0)
const activeRole = ref<'student' | 'teacher' | 'parent'>((userStore.preferredPortalRole as 'student' | 'teacher' | 'parent') || 'student')
const authBackground = `url(${loginBackground})`

const topMenus = [
  { title: '智慧校园', path: '/login' },
  { title: '服务专区', path: '/register' },
]
const publicRoleOptions = [
  { label: '学生端', value: 'student' },
  { label: '教师端', value: 'teacher' },
  { label: '家长端', value: 'parent' },
]
const activeMenu = ref('/register')
const roleOptions = [
  { label: '学生注册', value: 'student' },
  { label: '教师注册', value: 'teacher' },
  { label: '家长注册', value: 'parent' },
]
const stepItems = [
  { key: 'base', title: '基础信息', desc: '账号与联系方式' },
  { key: 'role', title: '身份资料', desc: '角色识别信息' },
  { key: 'secure', title: '安全确认', desc: '密码与验证码' },
]

const roleGuideMap = {
  student: {
    step1: '请填写基础账号信息，下一步补充学号与入学年份。',
    step2: '学生身份资料将用于课程、资源与成长记录识别。',
  },
  teacher: {
    step1: '请填写基础账号信息，下一步补充工号与任教学科。',
    step2: '教师身份资料将用于教学服务与权限识别。',
  },
  parent: {
    step1: '请填写基础账号信息，下一步补充关联学生学号。',
    step2: '家长身份资料将用于家校互动与报告查看权限识别。',
  },
} as const

const form = reactive({
  username: '',
  realName: '',
  phonenumber: '',
  email: '',
  password: '',
  confirmPassword: '',
  studentNo: '',
  teacherNo: '',
  admissionYear: undefined as number | undefined,
  learningGoal: '',
  major: '',
  sex: '2',
  code: '',
  uuid: '',
})

const validateConfirmPassword = (_rule: any, value: string, callback: (error?: Error) => void) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const baseRules: Record<string, any[]> = {
  username: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
    { min: 2, max: 20, message: '账号长度需在 2 到 20 位之间', trigger: 'blur' },
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phonenumber: [{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, max: 20, message: '密码长度需在 5 到 20 位之间', trigger: 'blur' },
    { pattern: /^[^<>"'|\\]+$/, message: '密码不能包含非法字符', trigger: 'blur' },
  ],
  confirmPassword: [{ required: true, message: '请再次输入密码', trigger: 'blur' }, { validator: validateConfirmPassword, trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}

const currentRules = computed(() => {
  const rules: Record<string, any[]> = { ...baseRules }
  if (activeRole.value === 'student') {
    rules.studentNo = [{ required: true, message: '请输入学号', trigger: 'blur' }]
    rules.admissionYear = [{ required: true, message: '请输入入学年份', trigger: 'change' }]
  }
  if (activeRole.value === 'teacher') {
    rules.teacherNo = [{ required: true, message: '请输入工号', trigger: 'blur' }]
    rules.major = [{ required: true, message: '请输入任教学科或方向', trigger: 'blur' }]
  }
  if (activeRole.value === 'parent') {
    rules.studentNo = [{ required: true, message: '请输入关联学生学号', trigger: 'blur' }]
  }
  if (!captchaEnabled.value) {
    delete rules.code
  }
  return rules
})

const currentStepTip = computed(() => {
  if (currentStep.value === 0) return roleGuideMap[activeRole.value].step1
  if (currentStep.value === 1) return roleGuideMap[activeRole.value].step2
  return '请设置登录密码，并完成验证码校验后提交注册。'
})

watch(activeRole, () => {
  userStore.setPreferredPortalRole(activeRole.value)
  currentStep.value = 0
  form.studentNo = ''
  form.teacherNo = ''
  form.admissionYear = undefined
  form.learningGoal = ''
  form.major = ''
  form.password = ''
  form.confirmPassword = ''
  form.code = ''
  registerRef.value?.clearValidate?.()
})

function handleRoleChange(role: string) {
  activeRole.value = role as 'student' | 'teacher' | 'parent'
}

async function getCode() {
  const res = await getCodeImg()
  captchaEnabled.value = res.captchaEnabled !== false
  if (captchaEnabled.value) {
    codeUrl.value = `data:image/gif;base64,${res.img}`
    form.uuid = res.uuid || ''
  }
}

function buildPayload() {
  return {
    username: form.username.trim(),
    password: form.password,
    userType: activeRole.value,
    realName: form.realName.trim(),
    phonenumber: form.phonenumber.trim(),
    email: form.email.trim(),
    studentNo: form.studentNo.trim(),
    teacherNo: form.teacherNo.trim(),
    admissionYear: form.admissionYear,
    learningGoal: form.learningGoal.trim(),
    major: form.major.trim(),
    sex: form.sex,
    code: form.code.trim(),
    uuid: form.uuid,
  }
}

function getStepFields(step: number) {
  if (step === 0) return ['username', 'realName', 'phonenumber', 'email']
  if (step === 1) {
    if (activeRole.value === 'student') return ['studentNo', 'admissionYear']
    if (activeRole.value === 'teacher') return ['teacherNo', 'major']
    return ['studentNo']
  }
  return captchaEnabled.value ? ['password', 'confirmPassword', 'sex', 'code'] : ['password', 'confirmPassword', 'sex']
}

async function validateStep(step: number) {
  const fields = getStepFields(step)
  if (!fields.length) return true
  try {
    await Promise.all(fields.map((field) => registerRef.value?.validateField(field)))
    return true
  } catch {
    return false
  }
}

async function goNextStep() {
  const valid = await validateStep(currentStep.value)
  if (!valid) return
  currentStep.value += 1
}

function handleRegister() {
  registerRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    loading.value = true
    try {
      await register(buildPayload())
      await ElMessageBox.alert('注册信息已提交成功，请返回登录。', '系统提示', { type: 'success' })
      router.push('/login')
    } catch {
      if (captchaEnabled.value) {
        await getCode()
      }
    } finally {
      loading.value = false
    }
  })
}

getCode().catch(() => {
  ElMessage.warning('验证码加载失败，请稍后重试')
})
</script>

<style scoped>
.portal-register-page {
  min-height: 100dvh;
  display: grid;
  grid-template-rows: auto 1fr;
  overflow: hidden;
  background: v-bind(authBackground) center center / cover no-repeat;
  font-family: 'MiSans', "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
}

.portal-register-page::before {
  content: '';
  position: fixed;
  inset: 58px 0 0 0;
  pointer-events: none;
  background: rgba(255, 255, 255, 0.08);
}

.register-shell {
  min-height: calc(100dvh - 58px);
  padding: 24px 16px 32px;
  display: grid;
  justify-items: center;
  align-items: center;
}

.register-card {
  position: relative;
  z-index: 1;
  width: min(520px, calc(100vw - 32px));
  padding: 28px 28px 24px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(214, 224, 239, 0.96);
  box-shadow: 0 24px 64px rgba(16, 34, 66, 0.12);
  backdrop-filter: blur(18px);
}

.register-card__top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.register-card__top > div {
  min-width: 0;
  flex: 1;
}

.register-card__eyebrow {
  color: #3f73db;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.register-card__top h1 {
  margin: 8px 0 0;
  color: #1b3456;
  font-size: 34px;
  line-height: 1.08;
}

.register-card__top p {
  margin: 10px 0 0;
  color: #667b93;
  font-size: 13px;
  line-height: 1.8;
}

.register-card__link {
  flex-shrink: 0;
  align-self: flex-start;
  padding-top: 8px;
  color: #2a63d3;
  text-decoration: none;
  font-size: 14px;
  font-weight: 700;
  line-height: 1;
  white-space: nowrap;
}

.role-tabs {
  margin-top: 20px;
}

.role-tabs :deep(.el-tabs__header) {
  margin: 0;
}

.role-tabs :deep(.el-tabs__nav-wrap::after) {
  background: #e7eef7;
}

.role-tabs :deep(.el-tabs__item) {
  height: 40px;
  font-size: 14px;
  font-weight: 600;
}

.role-tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 4px;
}

.stepper {
  margin-top: 16px;
  display: grid;
  gap: 8px;
  padding: 4px;
  border-radius: 6px;
  background: rgba(244, 248, 255, 0.92);
  border: 1px solid rgba(222, 232, 246, 0.9);
}

.stepper-item {
  display: grid;
  grid-template-columns: 32px minmax(0, 1fr);
  gap: 10px;
  align-items: center;
  padding: 10px 12px;
  border-radius: 4px;
  transition: background-color 0.2s ease, box-shadow 0.2s ease;
}

.stepper-item__dot {
  display: grid;
  place-items: center;
  width: 32px;
  height: 32px;
  border-radius: 4px;
  background: #eef3fb;
  color: #7a8da4;
  font-size: 13px;
  font-weight: 700;
}

.stepper-item__text strong {
  display: block;
  color: #2b4465;
  font-size: 13px;
}

.stepper-item__text span {
  display: block;
  margin-top: 4px;
  color: #7a8da4;
  font-size: 12px;
}

.stepper-item.is-active {
  background: #ffffff;
  box-shadow: 0 10px 20px rgba(20, 41, 77, 0.06);
}

.stepper-item.is-active .stepper-item__dot,
.stepper-item.is-done .stepper-item__dot {
  background: #2f6bff;
  color: #fff;
}

.register-card__hint {
  margin-top: 14px;
  padding: 8px 12px;
  border-radius: 4px;
  background: #f7f9fd;
  border: 1px solid #e4ebf5;
  color: #58708d;
  font-size: 12px;
  line-height: 1.6;
}

.register-form {
  margin-top: 14px;
}

.step-panel {
  min-height: 190px;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 10px;
}

.register-form :deep(.el-form-item__content) {
  min-width: 0;
}

.register-form :deep(.el-form-item__label) {
  padding-bottom: 6px;
  color: #556a84;
  font-size: 13px;
  font-weight: 600;
}

.register-form :deep(.el-input__wrapper),
.register-form :deep(.el-select__wrapper),
.register-form :deep(.el-textarea__inner) {
  min-height: 40px;
  border-radius: 4px;
  background: #f8fbff;
  border: 1px solid #dfe8f4;
  box-shadow: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
}

.register-form :deep(.el-input__wrapper:hover),
.register-form :deep(.el-select__wrapper:hover),
.register-form :deep(.el-textarea__inner:hover) {
  background: #fff;
  border-color: #c7d8ef;
}

.register-form :deep(.el-input__wrapper.is-focus),
.register-form :deep(.el-select__wrapper.is-focused),
.register-form :deep(.el-textarea__inner:focus) {
  background: #fff;
  border-color: #7baeff;
  box-shadow: 0 0 0 3px rgba(44, 134, 255, 0.12);
}

.register-form :deep(.el-textarea__inner) {
  min-height: 72px;
  resize: vertical;
}

.register-form :deep(.register-number-input) {
  width: 100%;
  display: block;
  overflow: visible;
}

.register-form :deep(.register-number-input .el-input__wrapper) {
  min-height: 40px;
  border-radius: 4px;
  background: #f8fbff;
  border: 1px solid #dfe8f4;
  box-shadow: none;
  padding-right: 38px;
  overflow: visible;
}

.register-form :deep(.register-number-input .el-input-number__increase),
.register-form :deep(.register-number-input .el-input-number__decrease) {
  width: 32px;
  right: 0;
  background: #f2f6fd;
  color: #5e748e;
  border-left: 1px solid #dfe8f3;
  height: 20px;
  line-height: 20px;
}

.register-form :deep(.register-number-input .el-input-number__increase) {
  border-top-right-radius: 4px;
}

.register-form :deep(.register-number-input .el-input-number__decrease) {
  border-bottom-right-radius: 4px;
  border-top: 1px solid #dfe8f3;
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
  height: 40px;
  border-radius: 4px;
  border: 1px solid #d8e3f4;
  background: #fff;
  cursor: pointer;
  object-fit: cover;
  display: block;
}

.register-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.register-actions__ghost,
.register-actions__primary {
  min-width: 104px;
  height: 40px;
  border-radius: 4px;
  font-weight: 700;
}

.register-actions__ghost {
  border-color: #d7e2f2;
  color: #44607f;
}

.register-actions__primary {
  box-shadow: 0 14px 24px rgba(35, 93, 206, 0.16);
}

@media (max-width: 760px) {
  .register-shell {
    padding: 16px 12px 24px;
    justify-items: center;
  }

  .register-card {
    padding: 22px 18px 20px;
    margin-right: 0;
  }

  .register-card__top h1 {
    font-size: 28px;
  }

  .register-card__top {
    gap: 10px;
  }

  .captcha-row {
    grid-template-columns: 1fr;
  }

  .register-actions {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .register-actions__primary:only-child {
    grid-column: 1 / -1;
  }
}

@media (prefers-reduced-motion: reduce) {
  .stepper-item,
  .register-form :deep(.el-input__wrapper),
  .register-form :deep(.el-select__wrapper),
  .register-form :deep(.el-textarea__inner) {
    transition: none !important;
  }
}
</style>
