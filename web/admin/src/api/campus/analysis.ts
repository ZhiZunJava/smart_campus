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

export function processLearningWarning(warningId: number | string, processStatus = 'PROCESSED') {
  return request({
    url: `/campus/analysis/warning/${warningId}/process`,
    method: 'post',
    params: { processStatus },
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

export function getLearningDiagnosis(params: any) {
  return request({
    url: '/campus/analysis/diagnosis',
    method: 'get',
    params,
  })
}

export function getLearningWorkbench(params: any) {
  return request({
    url: '/campus/analysis/workbench',
    method: 'get',
    params,
  })
}

export function getAnalysisMeta() {
  return request({
    url: '/campus/analysis/meta',
    method: 'get',
  })
}
