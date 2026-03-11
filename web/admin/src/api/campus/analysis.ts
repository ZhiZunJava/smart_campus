import request from '@/utils/request'

export function listLearningWarning(query: any) {
  return request({
    url: '/campus/analysis/warning/list',
    method: 'get',
    params: query,
  })
}

export function buildLearningWarning(params: any) {
  return request({
    url: '/campus/analysis/warning/build',
    method: 'post',
    params,
  })
}

export function listLearningReport(query: any) {
  return request({
    url: '/campus/analysis/report/list',
    method: 'get',
    params: query,
  })
}

export function generateLearningReport(params: any) {
  return request({
    url: '/campus/analysis/report/generate',
    method: 'post',
    params,
  })
}
