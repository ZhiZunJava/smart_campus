import request from '@/utils/request'

export function listStudyRecord(query: any) {
  return request({
    url: '/campus/studyRecord/list',
    method: 'get',
    params: query,
  })
}

export function getStudyRecord(recordId: number) {
  return request({
    url: `/campus/studyRecord/${recordId}`,
    method: 'get',
  })
}

export function addStudyRecord(data: any) {
  return request({
    url: '/campus/studyRecord',
    method: 'post',
    data,
  })
}

export function updateStudyRecord(data: any) {
  return request({
    url: '/campus/studyRecord',
    method: 'put',
    data,
  })
}

export function delStudyRecord(recordIds: number | number[]) {
  return request({
    url: `/campus/studyRecord/${recordIds}`,
    method: 'delete',
  })
}
