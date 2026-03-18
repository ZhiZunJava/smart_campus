import request from '@/utils/request'

export function listCourseStudent(query: any) {
  return request({
    url: '/campus/courseStudent/list',
    method: 'get',
    params: query,
  })
}

export function addCourseStudent(data: any) {
  return request({
    url: '/campus/courseStudent',
    method: 'post',
    data,
  })
}

export function checkCourseStudentDuplicate(data: any) {
  return request({
    url: '/campus/courseStudent/duplicate-check',
    method: 'post',
    data,
  })
}

export function updateCourseStudent(data: any) {
  return request({
    url: '/campus/courseStudent',
    method: 'put',
    data,
  })
}

export function delCourseStudent(ids: number | number[]) {
  return request({
    url: `/campus/courseStudent/${ids}`,
    method: 'delete',
  })
}
