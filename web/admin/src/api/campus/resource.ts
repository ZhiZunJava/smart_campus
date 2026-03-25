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
    url: `/campus/resource/${resourceId}`,
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

export function listResourceTree(courseId: number) {
  return request({
    url: '/campus/resource/tree',
    method: 'get',
    params: { courseId },
  })
}

export function initResourceTree(courseId: number) {
  return request({
    url: `/campus/resource/tree/init/${courseId}`,
    method: 'post',
  })
}

export function addResourceNode(data: any) {
  return request({
    url: '/campus/resource/node',
    method: 'post',
    data,
  })
}

export function updateResourceNode(data: any) {
  return request({
    url: '/campus/resource/node',
    method: 'put',
    data,
  })
}

export function delResourceNode(nodeId: number) {
  return request({
    url: `/campus/resource/node/${nodeId}`,
    method: 'delete',
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

export function listResourceAsset(query: any) {
  return request({
    url: '/campus/resource/asset/list',
    method: 'get',
    params: query,
  })
}

export function addResourceAsset(data: any) {
  return request({
    url: '/campus/resource/asset',
    method: 'post',
    data,
  })
}

export function updateResourceAsset(data: any) {
  return request({
    url: '/campus/resource/asset',
    method: 'put',
    data,
  })
}

export function delResourceAsset(assetId: number) {
  return request({
    url: `/campus/resource/asset/${assetId}`,
    method: 'delete',
  })
}

export function listResourceComment(resourceId: number) {
  return request({
    url: '/campus/resource/comment/list',
    method: 'get',
    params: { resourceId },
  })
}

export function addResourceComment(data: any) {
  return request({
    url: '/campus/resource/comment',
    method: 'post',
    data,
  })
}

export function rateResource(resourceId: number, ratingScore: number) {
  return request({
    url: `/campus/resource/${resourceId}/rate`,
    method: 'post',
    params: { ratingScore },
  })
}

export function shareResource(resourceId: number) {
  return request({
    url: `/campus/resource/${resourceId}/share`,
    method: 'post',
  })
}
