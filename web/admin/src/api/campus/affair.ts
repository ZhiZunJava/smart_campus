import request from '@/utils/request'

export function listAffairCategory(query: any) {
  return request({ url: '/campus/affair/category/list', method: 'get', params: query })
}

export function getAffairCategory(categoryId: number) {
  return request({ url: `/campus/affair/category/${categoryId}`, method: 'get' })
}

export function addAffairCategory(data: any) {
  return request({ url: '/campus/affair/category', method: 'post', data })
}

export function updateAffairCategory(data: any) {
  return request({ url: '/campus/affair/category', method: 'put', data })
}

export function removeAffairCategory(categoryIds: number | number[]) {
  const value = Array.isArray(categoryIds) ? categoryIds.join(',') : categoryIds
  return request({ url: `/campus/affair/category/${value}`, method: 'delete' })
}

export function listAffairTemplate(query: any) {
  return request({ url: '/campus/affair/template/list', method: 'get', params: query })
}

export function getAffairTemplate(templateId: number) {
  return request({ url: `/campus/affair/template/${templateId}`, method: 'get' })
}

export function addAffairTemplate(data: any) {
  return request({ url: '/campus/affair/template', method: 'post', data })
}

export function updateAffairTemplate(data: any) {
  return request({ url: '/campus/affair/template', method: 'put', data })
}

export function removeAffairTemplate(templateIds: number | number[]) {
  const value = Array.isArray(templateIds) ? templateIds.join(',') : templateIds
  return request({ url: `/campus/affair/template/${value}`, method: 'delete' })
}

export function listAffairRequest(query: any) {
  return request({ url: '/campus/affair/request/list', method: 'get', params: query })
}

export function getAffairRequest(requestId: number) {
  return request({ url: `/campus/affair/request/${requestId}`, method: 'get' })
}

export function reviewAffairRequest(data: any) {
  return request({ url: '/campus/affair/request/review', method: 'put', data })
}

export function listStudentStatusLog(query: any) {
  return request({ url: '/campus/affair/status/list', method: 'get', params: query })
}

export function getStudentStatusLog(statusLogId: number) {
  return request({ url: `/campus/affair/status/${statusLogId}`, method: 'get' })
}

export function batchReviewAffairRequest(data: any) {
  return request({ url: '/campus/affair/request/review/batch', method: 'put', data })
}

export function exportAffairRequest(data: any) {
  return request({ url: '/campus/affair/request/export', method: 'post', data, responseType: 'blob' })
}

export function getAffairRequestStatistics() {
  return request({ url: '/campus/affair/request/statistics', method: 'get' })
}

export function updateCategorySortOrder(data: any) {
  return request({ url: '/campus/affair/category', method: 'put', data })
}

export function listAffairWorkStudyJob(query: any) {
  return request({ url: '/campus/affair/workStudyJob/list', method: 'get', params: query })
}

export function getAffairWorkStudyJob(jobId: number) {
  return request({ url: `/campus/affair/workStudyJob/${jobId}`, method: 'get' })
}

export function addAffairWorkStudyJob(data: any) {
  return request({ url: '/campus/affair/workStudyJob', method: 'post', data })
}

export function updateAffairWorkStudyJob(data: any) {
  return request({ url: '/campus/affair/workStudyJob', method: 'put', data })
}

export function removeAffairWorkStudyJob(jobIds: number | number[]) {
  const value = Array.isArray(jobIds) ? jobIds.join(',') : jobIds
  return request({ url: `/campus/affair/workStudyJob/${value}`, method: 'delete' })
}

// ---- 教材管理 ----
export function listTextbook(query: any) {
  return request({ url: '/campus/textbook/list', method: 'get', params: query })
}
export function getTextbook(textbookId: number) {
  return request({ url: `/campus/textbook/${textbookId}`, method: 'get' })
}
export function addTextbook(data: any) {
  return request({ url: '/campus/textbook', method: 'post', data })
}
export function updateTextbook(data: any) {
  return request({ url: '/campus/textbook', method: 'put', data })
}
export function removeTextbook(ids: number | number[]) {
  const value = Array.isArray(ids) ? ids.join(',') : ids
  return request({ url: `/campus/textbook/${value}`, method: 'delete' })
}
export function listTextbookOptions() {
  return request({ url: '/campus/textbook/options', method: 'get' })
}

// ---- 教材计划 ----
export function listTextbookPlan(query: any) {
  return request({ url: '/campus/textbook/plan/list', method: 'get', params: query })
}
export function getTextbookPlan(planId: number) {
  return request({ url: `/campus/textbook/plan/${planId}`, method: 'get' })
}
export function addTextbookPlan(data: any) {
  return request({ url: '/campus/textbook/plan', method: 'post', data })
}
export function updateTextbookPlan(data: any) {
  return request({ url: '/campus/textbook/plan', method: 'put', data })
}
export function removeTextbookPlan(ids: number | number[]) {
  const value = Array.isArray(ids) ? ids.join(',') : ids
  return request({ url: `/campus/textbook/plan/${value}`, method: 'delete' })
}
export function getTextbookPlanClaimRecords(planId: number) {
  return request({ url: `/campus/textbook/plan/claim-records/${planId}`, method: 'get' })
}
