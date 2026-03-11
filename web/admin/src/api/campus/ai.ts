import request from '@/utils/request'

export function listAiModel(query: any) {
  return request({ url: '/campus/ai/model/list', method: 'get', params: query })
}
export function addAiModel(data: any) {
  return request({ url: '/campus/ai/model', method: 'post', data })
}
export function updateAiModel(data: any) {
  return request({ url: '/campus/ai/model', method: 'put', data })
}
export function delAiModel(ids: number | number[]) {
  return request({ url: `/campus/ai/model/${ids}`, method: 'delete' })
}
export function listAiPrompt(query: any) {
  return request({ url: '/campus/ai/prompt/list', method: 'get', params: query })
}
export function addAiPrompt(data: any) {
  return request({ url: '/campus/ai/prompt', method: 'post', data })
}
export function updateAiPrompt(data: any) {
  return request({ url: '/campus/ai/prompt', method: 'put', data })
}
export function delAiPrompt(ids: number | number[]) {
  return request({ url: `/campus/ai/prompt/${ids}`, method: 'delete' })
}
export function testAiModel(data: any) {
  return request({ url: '/campus/ai/model/test', method: 'post', data })
}
export function chatAiModel(data: any) {
  return request({ url: '/campus/ai/chat', method: 'post', data })
}
export function testAiPrompt(data: any) {
  return request({ url: '/campus/ai/prompt/test', method: 'post', data })
}
export function listAiTask(query: any) {
  return request({ url: '/campus/ai/task/list', method: 'get', params: query })
}
export function getAiTask(taskId: number) {
  return request({ url: `/campus/ai/task/${taskId}`, method: 'get' })
}
