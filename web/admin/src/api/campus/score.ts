import request from '@/utils/request'

export function listStudentScore(query: any) {
  return request({ url: '/campus/studentScore/list', method: 'get', params: query })
}

export function getStudentScoreOverview(query: any) {
  return request({ url: '/campus/studentScore/overview', method: 'get', params: query })
}

export function getStudentScore(scoreId: number) {
  return request({ url: `/campus/studentScore/${scoreId}`, method: 'get' })
}

export function addStudentScore(data: any) {
  return request({ url: '/campus/studentScore', method: 'post', data })
}

export function updateStudentScore(data: any) {
  return request({ url: '/campus/studentScore', method: 'put', data })
}

export function batchSaveStudentScore(data: any[]) {
  return request({ url: '/campus/studentScore/batch-save', method: 'post', data })
}

export function initStudentScoreLedger(classCourseId: number) {
  return request({ url: `/campus/studentScore/init/${classCourseId}`, method: 'post' })
}

export function syncStudentScoreExam(classCourseId: number) {
  return request({ url: `/campus/studentScore/sync/${classCourseId}`, method: 'post' })
}

export function publishStudentScore(data: any) {
  return request({ url: '/campus/studentScore/publish', method: 'post', data })
}

export function delStudentScore(ids: number | number[]) {
  return request({ url: `/campus/studentScore/${ids}`, method: 'delete' })
}

export function importStudentScore(file: File, classCourseId: number, updateSupport = true) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('classCourseId', String(classCourseId))
  formData.append('updateSupport', String(updateSupport))
  return request({
    url: '/campus/studentScore/importData',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
