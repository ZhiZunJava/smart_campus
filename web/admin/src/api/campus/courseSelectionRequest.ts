import request from '@/utils/request'

export function listCourseSelectionRequest(query: any) {
  return request({
    url: '/campus/courseSelectionRequest/list',
    method: 'get',
    params: query,
  })
}

export function getCourseSelectionRequest(requestId: number) {
  return request({
    url: `/campus/courseSelectionRequest/${requestId}`,
    method: 'get',
  })
}

export function reviewCourseSelectionRequest(data: any) {
  return request({
    url: '/campus/courseSelectionRequest/review',
    method: 'put',
    data,
  })
}

export function batchReviewCourseSelectionRequest(data: any[]) {
  return request({
    url: '/campus/courseSelectionRequest/batchReview',
    method: 'put',
    data,
  })
}
