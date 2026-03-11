import request from '@/utils/request'

export function listQaSession(query: any) {
  return request({ url: '/campus/qa/session/list', method: 'get', params: query })
}
export function addQaSession(data: any) {
  return request({ url: '/campus/qa/session', method: 'post', data })
}
export function listQaMessage(query: any) {
  return request({ url: '/campus/qa/message/list', method: 'get', params: query })
}
export function addQaMessage(data: any) {
  return request({ url: '/campus/qa/message', method: 'post', data })
}
export function listQaFeedback(query: any) {
  return request({ url: '/campus/qa/feedback/list', method: 'get', params: query })
}
export function addQaFeedback(data: any) {
  return request({ url: '/campus/qa/feedback', method: 'post', data })
}
export function getQaSessionDetail(sessionId: number) {
  return request({ url: `/campus/detail/qa/session/${sessionId}`, method: 'get' })
}
