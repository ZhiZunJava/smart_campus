<template>
  <div class="portal-auth-page">
    <PortalTopBar
      :menu-items="topMenus"
      :active-path="activeMenu"
      :selected-role="activeRole"
      :role-options-override="publicRoleOptions"
      :show-menu="true"
      :show-role-switch="true"
      :show-auth-action="true"
      :show-user-panel="false"
      @role-change="handleRoleChange"
    />

    <section class="portal-register-wrap">
      <div class="portal-register-card">
        <div class="register-side">
          <div class="register-side__badge">统一身份认证</div>
          <h2>统一注册</h2>
          <p>学生、教师、家长可按对应身份完成注册，管理员仍由后台创建后使用顶部角色切换进入各端。</p>
          <div class="register-side__tips">
            <div>学生：学号 + 入学年份</div>
            <div>教师：工号 + 任教学科/方向</div>
            <div>家长：关联学生学号</div>
          </div>
        </div>

        <div class="register-main">
          <div class="register-title">统一身份注册</div>
          <el-tabs v-model="activeRole" stretch>
            <el-tab-pane v-for="item in roleOptions" :key="item.value" :label="item.label" :name="item.value" />
          </el-tabs>

          <el-form ref="registerRef" :model="form" :rules="currentRules" label-position="top" class="register-form">
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="登录账号" prop="username">
                  <el-input v-model="form.username" placeholder="请输入登录账号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="form.realName" placeholder="请输入真实姓名" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="手机号" prop="phonenumber">
                  <el-input v-model="form.phonenumber" placeholder="请输入手机号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" placeholder="请输入邮箱，可选" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="密码" prop="password">
                  <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入密码" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row v-if="activeRole === 'student'" :gutter="12">
              <el-col :span="12">
                <el-form-item label="学号" prop="studentNo">
                  <el-input v-model="form.studentNo" placeholder="请输入学号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="入学年份" prop="admissionYear">
                  <el-input-number v-model="form.admissionYear" :min="2000" :max="2099" controls-position="right" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row v-if="activeRole === 'teacher'" :gutter="12">
              <el-col :span="12">
                <el-form-item label="工号" prop="teacherNo">
                  <el-input v-model="form.teacherNo" placeholder="请输入工号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="任教学科/方向" prop="major">
                  <el-input v-model="form.major" placeholder="请输入任教学科或方向" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row v-if="activeRole === 'parent'" :gutter="12">
              <el-col :span="24">
                <el-form-item label="关联学生学号" prop="studentNo">
                  <el-input v-model="form.studentNo" placeholder="请输入孩子的学号" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row v-if="activeRole === 'student' || activeRole === 'teacher'">
              <el-col :span="24">
                <el-form-item :label="activeRole === 'student' ? '学习目标' : '培养方向'" prop="learningGoal">
                  <el-input v-model="form.learningGoal" type="textarea" :rows="3" placeholder="请输入目标或方向，可选" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="性别" prop="sex">
                  <el-select v-model="form.sex" placeholder="请选择性别">
                    <el-option label="男" value="0" />
                    <el-option label="女" value="1" />
                    <el-option label="未知" value="2" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="captchaEnabled">
                <el-form-item label="验证码" prop="code">
                  <div class="captcha-row">
                    <el-input v-model="form.code" placeholder="请输入验证码" />
                    <img class="captcha-img" :src="codeUrl" @click="getCode" />
                  </div>
                </el-form-item>
              </el-col>
            </el-row>

            <el-button type="primary" class="submit-btn" :loading="loading" @click="handleRegister">立即注册</el-button>
          </el-form>

          <div class="register-bottom">
            <span>已有账号可直接登录，并在右上角切换角色视角</span>
            <router-link to="/login">返回登录</router-link>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import PortalTopBar from '@/components/PortalTopBar.vue'
import usePortalUserStore from '@/store/user'
import { getCodeImg, register } from '@/api/auth'

const router = useRouter()
const userStore = usePortalUserStore()
const registerRef = ref()
const loading = ref(false)
const captchaEnabled = ref(true)
const codeUrl = ref('')
const activeRole = ref<'student' | 'teacher' | 'parent'>(userStore.preferredPortalRole as 'student' | 'teacher' | 'parent' || 'student')
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

watch(activeRole, () => {
  userStore.setPreferredPortalRole(activeRole.value)
  form.studentNo = ''
  form.teacherNo = ''
  form.admissionYear = undefined
  form.learningGoal = ''
  form.major = ''
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

function handleRegister() {
  registerRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    loading.value = true
    try {
      await register(buildPayload())
      await ElMessageBox.alert(`恭喜你，${roleOptions.find((item) => item.value === activeRole.value)?.label}已提交成功，请返回登录。`, '系统提示', { type: 'success' })
      router.push('/login')
    } catch (error) {
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
.portal-auth-page{min-height:100vh;background:#f3f5f8}.portal-register-wrap{padding:42px 24px 28px;display:flex;justify-content:center}.portal-register-card{width:1120px;max-width:100%;display:grid;grid-template-columns:.82fr 1.18fr;background:rgba(255,255,255,.98);border-radius:12px;overflow:hidden;box-shadow:0 18px 42px rgba(15,35,66,.08);border:1px solid #edf1f6}
.register-side{padding:42px 38px;background:linear-gradient(180deg,#f8fbff 0%,#f3f8ff 100%)}.register-side__badge{display:inline-flex;padding:6px 12px;border-radius:999px;background:#eaf3ff;color:#2c86ff;font-size:12px;font-weight:700}.register-side h2{margin:20px 0 12px;font-size:34px;color:#1a2d4d}.register-side p{margin:0;color:#728199;line-height:1.9}.register-side__tips{margin-top:28px;display:grid;gap:14px}.register-side__tips div{padding:16px 18px;border-radius:14px;background:#fff;border:1px solid #e9eef5;color:#506178}
.register-main{padding:36px 40px 28px}.register-title{font-size:32px;color:#26354f;text-align:center;margin-bottom:10px}.register-form :deep(.el-form-item){margin-bottom:14px}.register-form :deep(.el-input__wrapper),.register-form :deep(.el-textarea__inner){background:#f5f7fb;box-shadow:none}.register-form :deep(.el-input__wrapper.is-focus){box-shadow:none;border-color:#b6d6ff}.captcha-row{display:grid;grid-template-columns:1fr 118px;gap:12px;width:100%}.captcha-img{width:118px;height:40px;border-radius:8px;border:1px solid #e4ebf5;cursor:pointer}.submit-btn{width:100%;height:46px;border-radius:24px;margin-top:10px}.register-bottom{display:flex;justify-content:space-between;align-items:center;margin-top:16px;color:#7d8ba0;font-size:13px}.register-bottom a{color:#2c86ff;text-decoration:none}
@media (max-width: 980px){.portal-register-wrap{padding:24px 16px 20px}.portal-register-card{grid-template-columns:1fr}.register-side{display:none}.register-main{padding:28px 20px 24px}.register-title{font-size:28px}}
</style>
