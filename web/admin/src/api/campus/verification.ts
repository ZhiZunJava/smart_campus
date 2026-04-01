import request from '@/utils/request'

// ---- 批次管理 ----
export function listVerificationBatch(query: any) {
  return request({ url: '/campus/verification/batch/list', method: 'get', params: query })
}

export function getVerificationBatch(batchId: number) {
  return request({ url: `/campus/verification/batch/${batchId}`, method: 'get' })
}

export function addVerificationBatch(data: any) {
  return request({ url: '/campus/verification/batch', method: 'post', data })
}

export function updateVerificationBatch(data: any) {
  return request({ url: '/campus/verification/batch', method: 'put', data })
}

export function activateVerificationBatch(batchId: number) {
  return request({ url: `/campus/verification/batch/activate/${batchId}`, method: 'put' })
}

export function closeVerificationBatch(batchId: number) {
  return request({ url: `/campus/verification/batch/close/${batchId}`, method: 'put' })
}

export function removeVerificationBatch(batchIds: number | number[]) {
  const value = Array.isArray(batchIds) ? batchIds.join(',') : batchIds
  return request({ url: `/campus/verification/batch/${value}`, method: 'delete' })
}

// ---- 学生记录管理 ----
export function listVerificationRecord(query: any) {
  return request({ url: '/campus/verification/record/list', method: 'get', params: query })
}

export function getVerificationRecord(recordId: number) {
  return request({ url: `/campus/verification/record/${recordId}`, method: 'get' })
}

export function reviewVerificationRecord(data: any) {
  return request({ url: '/campus/verification/record/review', method: 'put', data })
}

export function batchReviewVerificationRecord(data: any) {
  return request({ url: '/campus/verification/record/review/batch', method: 'put', data })
}

export function batchModifyVerificationRecord(data: any) {
  return request({ url: '/campus/verification/record/batch-modify', method: 'put', data })
}
