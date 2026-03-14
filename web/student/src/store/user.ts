import { defineStore } from 'pinia'
import Cookies from 'js-cookie'
import { getCodeImg, getInfo, login, logout } from '@/api/auth'

const TokenKey = 'Admin-Token'
const LastPortalRoleKey = 'Portal-Last-Role'

interface PortalUserState {
  token: string
  user: any
  roles: string[]
  permissions: string[]
  captchaEnabled: boolean
  codeUrl: string
  uuid: string
}

const usePortalUserStore = defineStore('portal-user', {
  state: (): PortalUserState => ({
    token: Cookies.get(TokenKey) || '',
    user: null,
    roles: [],
    permissions: [],
    captchaEnabled: true,
    codeUrl: '',
    uuid: '',
  }),
  getters: {
    availablePortalRoles(state): string[] {
      const roles = state.roles || []
      const isAdmin = roles.includes('admin') || roles.includes('ROLE_admin')
      if (isAdmin) {
        return ['student', 'teacher', 'parent']
      }
      const result: string[] = []
      if (roles.includes('student') || roles.includes('ROLE_student')) result.push('student')
      if (roles.includes('teacher') || roles.includes('ROLE_teacher')) result.push('teacher')
      if (roles.includes('parent') || roles.includes('ROLE_parent')) result.push('parent')
      if (result.length === 0) result.push('student')
      return result
    },
    preferredPortalRole(): string {
      const saved = sessionStorage.getItem(LastPortalRoleKey)
      if (saved && this.availablePortalRoles.includes(saved)) {
        return saved
      }
      return this.availablePortalRoles[0] || 'student'
    }
  },
  actions: {
    setPreferredPortalRole(role: string) {
      sessionStorage.setItem(LastPortalRoleKey, role)
    },
    async loadCaptcha() {
      const res = await getCodeImg()
      this.captchaEnabled = res.captchaEnabled !== false
      this.codeUrl = `data:image/gif;base64,${res.img}`
      this.uuid = res.uuid || ''
    },
    async loginAction(form: { username: string; password: string; code: string }) {
      const res = await login({ username: form.username.trim(), password: form.password, code: form.code, uuid: this.uuid })
      this.token = res.token
      Cookies.set(TokenKey, res.token)
    },
    async loadUserInfo() {
      const res = await getInfo()
      this.user = res.user || null
      this.roles = res.roles || []
      this.permissions = res.permissions || []
      if (!sessionStorage.getItem(LastPortalRoleKey)) {
        this.setPreferredPortalRole(this.availablePortalRoles[0] || 'student')
      }
      return res
    },
    async logoutAction() {
      await logout()
      this.token = ''
      this.user = null
      this.roles = []
      this.permissions = []
      Cookies.remove(TokenKey)
      sessionStorage.removeItem(LastPortalRoleKey)
    }
  }
})

export default usePortalUserStore
