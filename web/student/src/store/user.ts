import { defineStore } from 'pinia'
import Cookies from 'js-cookie'
import { getCodeImg, getInfo, login, logout } from '@/api/auth'
import { listExamRecord } from '@/api/portal'

const TokenKey = 'Student-Token'
const LastPortalRoleKey = 'Portal-Last-Role'
const DismissedOngoingExamKey = 'Portal-Dismissed-Ongoing-Exam'

interface PortalUserState {
  token: string
  user: any
  roles: string[]
  permissions: string[]
  ongoingExam: any | null
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
    ongoingExam: null,
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
    setOngoingExam(record: any | null) {
      this.ongoingExam = record || null
      if (!record) {
        sessionStorage.removeItem(DismissedOngoingExamKey)
      }
    },
    dismissOngoingExam(recordId?: number | string) {
      if (!recordId) return
      sessionStorage.setItem(DismissedOngoingExamKey, String(recordId))
    },
    isOngoingExamDismissed(recordId?: number | string) {
      if (!recordId) return false
      return sessionStorage.getItem(DismissedOngoingExamKey) === String(recordId)
    },
    async loadOngoingExam() {
      const canUseStudentPortal = this.availablePortalRoles.includes('student')
      if (!this.token || !this.user?.userId || !canUseStudentPortal) {
        this.setOngoingExam(null)
        return null
      }
      try {
        const res = await listExamRecord({ pageNum: 1, pageSize: 10 })
        const ongoing = (res.rows || []).find((item: any) => item.examStatus === 'ONGOING') || null
        this.setOngoingExam(ongoing)
        return ongoing
      } catch {
        this.setOngoingExam(null)
        return null
      }
    },
    async loadCaptcha() {
      const res = await getCodeImg()
      this.captchaEnabled = res.captchaEnabled !== false
      this.codeUrl = `data:image/gif;base64,${res.img}`
      this.uuid = res.uuid || ''
    },
    async loginAction(form: { username: string; password: string; code: string }) {
      const res = await login({ username: form.username.trim(), password: form.password, code: form.code, uuid: this.uuid })
      this.acceptToken(res.token)
    },
    acceptToken(token: string) {
      this.token = token
      Cookies.set(TokenKey, token)
    },
    async loadUserInfo() {
      const res = await getInfo()
      this.user = res.user || null
      this.roles = res.roles || []
      this.permissions = res.permissions || []
      if (!sessionStorage.getItem(LastPortalRoleKey)) {
        this.setPreferredPortalRole(this.availablePortalRoles[0] || 'student')
      }
      await this.loadOngoingExam()
      return res
    },
    async refreshUserInfo() {
      return this.loadUserInfo()
    },
    async logoutAction() {
      await logout()
      this.token = ''
      this.user = null
      this.roles = []
      this.permissions = []
      this.ongoingExam = null
      Cookies.remove(TokenKey)
      sessionStorage.removeItem(LastPortalRoleKey)
      sessionStorage.removeItem(DismissedOngoingExamKey)
    }
  }
})

export default usePortalUserStore
