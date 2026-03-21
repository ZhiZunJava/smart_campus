import request from '@/utils/request'

export function listQuestionCatalog(query: any) {
  return request({ url: '/campus/questionCatalog/list', method: 'get', params: query })
}

export function getQuestionCatalog(catalogId: number) {
  return request({ url: `/campus/questionCatalog/${catalogId}`, method: 'get' })
}

export function addQuestionCatalog(data: any) {
  return request({ url: '/campus/questionCatalog', method: 'post', data })
}

export function updateQuestionCatalog(data: any) {
  return request({ url: '/campus/questionCatalog', method: 'put', data })
}

export function delQuestionCatalog(ids: number | number[]) {
  return request({ url: `/campus/questionCatalog/${ids}`, method: 'delete' })
}

export function listQuestionItem(query: any) {
  return request({ url: '/campus/questionItem/list', method: 'get', params: query })
}

export function getQuestionItem(itemId: number) {
  return request({ url: `/campus/questionItem/${itemId}`, method: 'get' })
}

export function listQuestionItemVersions(itemId: number) {
  return request({ url: `/campus/questionItem/${itemId}/versions`, method: 'get' })
}

export function listLegacyQuestionVersions(questionId: number) {
  return request({ url: `/campus/questionItem/legacy/${questionId}/versions`, method: 'get' })
}

export function listQuestion(query: any) {
  return request({ url: '/campus/exam/question/list', method: 'get', params: query })
}

export function getQuestionDetail(questionId: number) {
  return request({ url: `/campus/exam/question/${questionId}`, method: 'get' })
}

export function listQuestionTypeMeta() {
  return request({ url: '/campus/exam/question/type-meta', method: 'get' })
}

export function addQuestion(data: any) {
  return request({ url: '/campus/exam/question', method: 'post', data })
}

export function updateQuestion(data: any) {
  return request({ url: '/campus/exam/question', method: 'put', data })
}

export function addQuestionDetail(data: any) {
  return request({ url: '/campus/exam/question/detail', method: 'post', data })
}

export function updateQuestionDetail(data: any) {
  return request({ url: '/campus/exam/question/detail', method: 'put', data })
}

export function delQuestion(ids: number | number[]) {
  return request({ url: `/campus/exam/question/${ids}`, method: 'delete' })
}

export function importQuestionData(data: FormData) {
  return request({
    url: '/campus/exam/question/importData',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function downloadQuestionTemplate() {
  return request({
    url: '/campus/exam/question/importTemplate',
    method: 'post',
    responseType: 'blob',
  })
}

export function aiGenerateQuestion(data: any) {
  return request({ url: '/campus/exam/question/ai-generate', method: 'post', data, timeout: 120000 })
}

export function submitAiGenerateQuestionTask(data: any) {
  return request({ url: '/campus/exam/question/ai-generate-task', method: 'post', data, timeout: 15000 })
}

export function listAiGenerateQuestionTask(query: any) {
  return request({ url: '/campus/exam/question/ai-task/list', method: 'get', params: query })
}

export function getAiGenerateQuestionTask(taskId: number) {
  return request({ url: `/campus/exam/question/ai-task/${taskId}`, method: 'get' })
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

export function getPaperDetail(paperId: number) {
  return request({ url: `/campus/exam/paper/${paperId}`, method: 'get' })
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

export function addPaperDetail(data: any) {
  return request({ url: '/campus/exam/paper/detail', method: 'post', data })
}

export function updatePaperDetail(data: any) {
  return request({ url: '/campus/exam/paper/detail', method: 'put', data })
}

export function delPaperDetail(ids: number | number[]) {
  return request({ url: `/campus/exam/paper/detail/${ids}`, method: 'delete' })
}

export function assemblePaper(data: any) {
  return request({ url: '/campus/exam/paper/assemble', method: 'post', data })
}

export function listStrategyTemplate(query: any) {
  return request({ url: '/campus/exam/strategy/list', method: 'get', params: query })
}

export function getStrategyTemplate(templateId: number) {
  return request({ url: `/campus/exam/strategy/${templateId}`, method: 'get' })
}

export function addStrategyTemplate(data: any) {
  return request({ url: '/campus/exam/strategy', method: 'post', data })
}

export function updateStrategyTemplate(data: any) {
  return request({ url: '/campus/exam/strategy', method: 'put', data })
}

export function delStrategyTemplate(ids: number | number[]) {
  return request({ url: `/campus/exam/strategy/${ids}`, method: 'delete' })
}

export function addPaperQuestion(data: any) {
  return request({ url: '/campus/exam/paperQuestion', method: 'post', data })
}

export function listRecord(query: any) {
  return request({ url: '/campus/exam/record/list', method: 'get', params: query })
}

export function getRecordOverview(query: any) {
  return request({ url: '/campus/exam/record/overview', method: 'get', params: query })
}

export function getRecordDetail(recordId: number) {
  return request({ url: `/campus/exam/record/${recordId}`, method: 'get' })
}

export function listWrong(query: any) {
  return request({ url: '/campus/exam/wrong/list', method: 'get', params: query })
}

export function getWrongOverview(query: any) {
  return request({ url: '/campus/exam/wrong/overview', method: 'get', params: query })
}

export function getWrongDetail(wrongId: number) {
  return request({ url: `/campus/exam/wrong/${wrongId}`, method: 'get' })
}

export function createWrongRetryPaper(data: any) {
  return request({ url: '/campus/exam/wrong/retry', method: 'post', data })
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
