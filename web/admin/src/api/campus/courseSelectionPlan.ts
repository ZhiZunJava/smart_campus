import request from '@/utils/request'

export function listCourseSelectionPlan(query: any) {
  return request({
    url: '/campus/courseSelectionPlan/list',
    method: 'get',
    params: query,
  })
}

export function addCourseSelectionPlan(data: any) {
  return request({
    url: '/campus/courseSelectionPlan',
    method: 'post',
    data,
  })
}

export function updateCourseSelectionPlan(data: any) {
  return request({
    url: '/campus/courseSelectionPlan',
    method: 'put',
    data,
  })
}

export function delCourseSelectionPlan(ids: number | number[]) {
  return request({
    url: `/campus/courseSelectionPlan/${ids}`,
    method: 'delete',
  })
}
