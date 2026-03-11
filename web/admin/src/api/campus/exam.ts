import request from '@/utils/request'

export function listQuestion(query: any) {
  return request({ url: '/campus/exam/question/list', method: 'get', params: query })
}
export function addQuestion(data: any) {
  return request({ url: '/campus/exam/question', method: 'post', data })
}
export function updateQuestion(data: any) {
  return request({ url: '/campus/exam/question', method: 'put', data })
}

export function listOption(query: any) {
  return request({ url: '/campus/exam/option/list', method: 'get', params: query })
}
export function addOption(data: any) {
  return request({ url: '/campus/exam/option', method: 'post', data })
}
export function updateOption(data: any) {
  return request({ url: '/campus/exam/option', method: 'put', data })
}
export function delOption(ids: number | number[]) {
  return request({ url: `/campus/exam/option/${ids}`, method: 'delete' })
}

export function listPaper(query: any) {
  return request({ url: '/campus/exam/paper/list', method: 'get', params: query })
}
export function addPaper(data: any) {
  return request({ url: '/campus/exam/paper', method: 'post', data })
}
export function updatePaper(data: any) {
  return request({ url: '/campus/exam/paper', method: 'put', data })
}
export function delPaper(ids: number | number[]) {
  return request({ url: `/campus/exam/paper/${ids}`, method: 'delete' })
}
export function addPaperQuestion(data: any) {
  return request({ url: '/campus/exam/paperQuestion', method: 'post', data })
}

export function listRecord(query: any) {
  return request({ url: '/campus/exam/record/list', method: 'get', params: query })
}
export function listWrong(query: any) {
  return request({ url: '/campus/exam/wrong/list', method: 'get', params: query })
}
export function listAnswer(query: any) {
  return request({ url: '/campus/exam/answer/list', method: 'get', params: query })
}

export function startExam(params: any) {
  return request({ url: '/campus/exam/start', method: 'post', params })
}
export function submitExam(data: any) {
  return request({ url: '/campus/exam/submit', method: 'post', data })
}

export function getQuestionDetail(questionId: number) {
  return request({
    url: `/campus/detail/question/${questionId}`,
    method: 'get',
  })
}

export function getPaperDetail(paperId: number) {
  return request({
    url: `/campus/detail/paper/${paperId}`,
    method: 'get',
  })
}
