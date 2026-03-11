import request from '@/utils/request'

export function listLearningProfile(query: any) {
  return request({
    url: '/campus/learningProfile/list',
    method: 'get',
    params: query,
  })
}

export function rebuildLearningProfile(params: any) {
  return request({
    url: '/campus/learningProfile/rebuild',
    method: 'post',
    params,
  })
}

export function listLearningRecommendation(query: any) {
  return request({
    url: '/campus/learningRecommendation/list',
    method: 'get',
    params: query,
  })
}

export function generateLearningRecommendation(params: any) {
  return request({
    url: '/campus/learningRecommendation/generate',
    method: 'post',
    params,
  })
}
