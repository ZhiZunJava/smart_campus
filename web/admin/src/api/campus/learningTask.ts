import request from '@/utils/request'

export function listLearningTask(query: any) {
  return request({
    url: '/campus/learningTask/list',
    method: 'get',
    params: query
  })
}

export function getLearningTask(taskId: number) {
  return request({
    url: `/campus/learningTask/${taskId}`,
    method: 'get'
  })
}

export function addLearningTask(data: any) {
  return request({
    url: '/campus/learningTask',
    method: 'post',
    data
  })
}

export function updateLearningTask(data: any) {
  return request({
    url: '/campus/learningTask',
    method: 'put',
    data
  })
}

export function delLearningTask(taskIds: number | number[]) {
  return request({
    url: `/campus/learningTask/${taskIds}`,
    method: 'delete'
  })
}

export function dispatchLearningTask(taskId: number, userIds: number[]) {
  return request({
    url: `/campus/learningTask/${taskId}/dispatch`,
    method: 'post',
    data: userIds
  })
}

export function dispatchLearningTaskToClass(taskId: number, classId: number) {
  return request({
    url: `/campus/learningTask/${taskId}/dispatch/class/${classId}`,
    method: 'post'
  })
}

export function dispatchLearningTaskToCourse(taskId: number, courseId: number) {
  return request({
    url: `/campus/learningTask/${taskId}/dispatch/course/${courseId}`,
    method: 'post'
  })
}

export function listLearningTaskDispatch(query: any) {
  return request({
    url: '/campus/learningTaskDispatch/list',
    method: 'get',
    params: query
  })
}

export function updateLearningTaskDispatch(data: any) {
  return request({
    url: '/campus/learningTaskDispatch',
    method: 'put',
    data
  })
}

export function listLearningTaskSubmission(query: any) {
  return request({
    url: '/campus/learningTaskSubmission/list',
    method: 'get',
    params: query
  })
}

export function updateLearningTaskSubmission(data: any) {
  return request({
    url: '/campus/learningTaskSubmission',
    method: 'put',
    data
  })
}
