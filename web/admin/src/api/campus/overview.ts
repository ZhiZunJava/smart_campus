import request from '@/utils/request'

export function getCampusDashboard(params: any) {
  return request({
    url: '/campus/overview/dashboard',
    method: 'get',
    params,
  })
}

export function getStudentDashboard(params: any) {
  return request({
    url: '/campus/portal/student/dashboard',
    method: 'get',
    params,
  })
}

export function getTeacherDashboard(params: any) {
  return request({
    url: '/campus/portal/teacher/dashboard',
    method: 'get',
    params,
  })
}

export function getParentDashboard(params: any) {
  return request({
    url: '/campus/portal/parent/dashboard',
    method: 'get',
    params,
  })
}

export function getAdminDashboard() {
  return request({
    url: '/campus/overview/admin-dashboard',
    method: 'get',
  })
}
