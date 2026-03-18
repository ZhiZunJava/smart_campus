import request from '@/utils/request'

export function login(data: { username: string; password: string; code: string; uuid: string }) {
  return request({
    url: '/login',
    method: 'post',
    headers: { isToken: false },
    data,
  })
}

export function register(data: Record<string, any>) {
  return request({
    url: '/register',
    method: 'post',
    headers: { isToken: false },
    data,
  })
}

export function getInfo() {
  return request({ url: '/getInfo', method: 'get' })
}

export function logout() {
  return request({ url: '/logout', method: 'post' })
}

export function getCodeImg() {
  return request({
    url: '/captchaImage',
    method: 'get',
    headers: { isToken: false },
    timeout: 20000,
  })
}

export function createScanLoginSession(clientBaseUrl: string) {
  return request({
    url: '/scan-login/session',
    method: 'post',
    headers: { isToken: false },
    data: { clientBaseUrl },
  })
}

export function getScanLoginStatus(ticket: string) {
  return request({
    url: `/scan-login/status/${ticket}`,
    method: 'get',
    headers: { isToken: false },
  })
}

export function confirmScanLogin(data: { ticket: string; username?: string; password?: string; code?: string; uuid?: string }) {
  return request({
    url: '/scan-login/confirm',
    method: 'post',
    headers: { isToken: false },
    data,
  })
}
