import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios'
import { ElMessage, ElMessageBox, ElNotification } from '@/utils/feedback'
import { getToken, removeToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'

type RequestInstance = AxiosInstance & {
  <T = any>(config: AxiosRequestConfig): Promise<T>
}

const resolvedBaseURL = import.meta.env.VITE_APP_BASE_API || (import.meta.env.DEV ? '/dev-api' : '/prod-api')

const service = axios.create({
  baseURL: resolvedBaseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

const request = Object.assign(
  <T = any>(config: AxiosRequestConfig) => service(config) as Promise<T>,
  service
) as RequestInstance

service.interceptors.request.use((config: any) => {
  const isToken = (config.headers || {}).isToken === false
  if (getToken() && !isToken) {
    config.headers['Authorization'] = 'Bearer ' + getToken()
  }
  if (config.method === 'get' && config.params) {
    const search = new URLSearchParams()
    Object.keys(config.params).forEach((key) => {
      const value = config.params[key]
      if (value !== undefined && value !== null && value !== '') {
        search.append(key, value)
      }
    })
    config.url = `${config.url}?${search.toString()}`
    config.params = {}
  }
  return config
})

service.interceptors.response.use(
  (res: any) => {
    if (res.request?.responseType === 'blob' || res.request?.responseType === 'arraybuffer') {
      return Promise.resolve(res.data)
    }
    const code = res.data.code || 200
    const msg = errorCode[code] || res.data.msg || errorCode.default
    if (code === 401) {
      ElMessageBox.confirm('登录状态已过期，请重新登录', '系统提示', {
        confirmButtonText: '重新登录',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeToken()
        location.href = '/'
      })
      return Promise.reject(new Error(msg))
    }
    if (code !== 200) {
      ElNotification.error({ title: msg })
      return Promise.reject(new Error(msg))
    }
    return Promise.resolve(res.data)
  },
  (error: any) => {
    let { message } = error
    if (message === 'Network Error') {
      message = '后端接口连接异常'
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时'
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
