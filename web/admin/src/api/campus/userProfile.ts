import request from '@/utils/request'

export function listUserProfile(query: any) {
  return request({
    url: '/campus/userProfile/list',
    method: 'get',
    params: query,
  })
}

export function getUserProfile(profileId: number) {
  return request({
    url: `/campus/userProfile/${profileId}`,
    method: 'get',
  })
}

export function addUserProfile(data: any) {
  return request({
    url: '/campus/userProfile',
    method: 'post',
    data,
  })
}

export function updateUserProfile(data: any) {
  return request({
    url: '/campus/userProfile',
    method: 'put',
    data,
  })
}

export function bindUserProfileAdvisor(data: any) {
  return request({
    url: '/campus/userProfile/bindAdvisor',
    method: 'put',
    data,
  })
}

export function delUserProfile(profileIds: number | number[]) {
  return request({
    url: `/campus/userProfile/${profileIds}`,
    method: 'delete',
  })
}
