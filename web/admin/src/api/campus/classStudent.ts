import request from '@/utils/request'

export function listClassStudent(query: any) {
  return request({
    url: '/campus/classStudent/list',
    method: 'get',
    params: query,
  })
}

export function updateClassStudent(data: any) {
  return request({
    url: '/campus/classStudent',
    method: 'put',
    data,
  })
}
