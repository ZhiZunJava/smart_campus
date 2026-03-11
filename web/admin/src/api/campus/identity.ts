import request from '@/utils/request'

export function listParentStudentRel(query: any) {
  return request({ url: '/campus/parentStudentRel/list', method: 'get', params: query })
}
export function getParentStudentRel(id: number) {
  return request({ url: `/campus/parentStudentRel/${id}`, method: 'get' })
}
export function addParentStudentRel(data: any) {
  return request({ url: '/campus/parentStudentRel', method: 'post', data })
}
export function updateParentStudentRel(data: any) {
  return request({ url: '/campus/parentStudentRel', method: 'put', data })
}
export function delParentStudentRel(ids: number | number[]) {
  return request({ url: `/campus/parentStudentRel/${ids}`, method: 'delete' })
}

export function listLoginRiskEvent(query: any) {
  return request({ url: '/campus/loginRiskEvent/list', method: 'get', params: query })
}
export function getLoginRiskEvent(id: number) {
  return request({ url: `/campus/loginRiskEvent/${id}`, method: 'get' })
}
export function addLoginRiskEvent(data: any) {
  return request({ url: '/campus/loginRiskEvent', method: 'post', data })
}
export function updateLoginRiskEvent(data: any) {
  return request({ url: '/campus/loginRiskEvent', method: 'put', data })
}
export function delLoginRiskEvent(ids: number | number[]) {
  return request({ url: `/campus/loginRiskEvent/${ids}`, method: 'delete' })
}
