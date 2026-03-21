import request from '@/utils/request'

export function listResource(query: any) {
  return request({
    url: '/campus/resource/list',
    method: 'get',
    params: query,
  })
}

export function getResource(resourceId: number) {
  return request({
    url: `/campus/detail/resource/${resourceId}`,
    method: 'get',
  })
}

export function getResourceOperationOverview(params: any) {
  return request({
    url: '/campus/resource/operation/overview',
    method: 'get',
    params,
  })
}

export function addResource(data: any) {
  return request({
    url: '/campus/resource',
    method: 'post',
    data,
  })
}

export function updateResource(data: any) {
  return request({
    url: '/campus/resource',
    method: 'put',
    data,
  })
}

export function delResource(resourceIds: number | number[]) {
  return request({
    url: `/campus/resource/${resourceIds}`,
    method: 'delete',
  })
}
