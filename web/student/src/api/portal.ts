import request from '@/utils/request'

export function listUser(query: any) {
  return request({ url: '/system/user/list', method: 'get', params: query })
}

export function getStudentDashboard(params: any) {
  return request({ url: '/campus/portal/student/dashboard', method: 'get', params })
}

export function getTeacherDashboard(params: any) {
  return request({ url: '/campus/portal/teacher/dashboard', method: 'get', params })
}

export function getParentDashboard(params: any) {
  return request({ url: '/campus/portal/parent/dashboard', method: 'get', params })
}

export function getLearningDiagnosis(params: any) {
  return request({ url: '/campus/analysis/diagnosis', method: 'get', params })
}

export function getLearningWorkbench(params: any) {
  return request({ url: '/campus/analysis/workbench', method: 'get', params })
}

export function getAnalysisMeta() {
  return request({ url: '/campus/analysis/meta', method: 'get' })
}

export function listResource(query: any) {
  return request({ url: '/campus/resource/list', method: 'get', params: query })
}

export function recordStudyBehavior(data: any) {
  return request({ url: '/campus/studyRecord/recordBehavior', method: 'post', data })
}

export function getResourceDetail(resourceId: number) {
  return request({ url: `/campus/detail/resource/${resourceId}`, method: 'get' })
}

export function listRecommendation(query: any) {
  return request({ url: '/campus/learningRecommendation/list', method: 'get', params: query })
}

export function listActiveRecommendation(query: any) {
  return request({ url: '/campus/learningRecommendation/active', method: 'get', params: query })
}

export function feedbackRecommendation(data: any) {
  return request({ url: '/campus/learningRecommendation/feedback', method: 'post', data })
}

export function listQaSession(query: any) {
  return request({ url: '/campus/qa/session/list', method: 'get', params: query })
}

export function addQaSession(data: any) {
  return request({ url: '/campus/qa/session', method: 'post', data })
}

export function updateQaSession(data: any) {
  return request({ url: '/campus/qa/session/update', method: 'post', data })
}

export function deleteQaSession(sessionId: number) {
  return request({ url: `/campus/qa/session/delete/${sessionId}`, method: 'post' })
}

export function listQaMessage(query: any) {
  return request({ url: '/campus/qa/message/list', method: 'get', params: query })
}

export function addQaMessage(data: any) {
  return request({ url: '/campus/qa/message', method: 'post', data })
}

export function askQa(data: any) {
  return request({ url: '/campus/qa/ask', method: 'post', data })
}

export function listQaModelOptions() {
  return request({ url: '/campus/qa/model/options', method: 'get' })
}

export function addQaFeedback(data: any) {
  return request({ url: '/campus/qa/feedback', method: 'post', data })
}

export function listExamPaper(query: any) {
  return request({ url: '/campus/exam/paper/list', method: 'get', params: query })
}

export function listExamRecord(query: any) {
  return request({ url: '/campus/exam/record/list', method: 'get', params: query })
}

export function listExamAnswer(query: any) {
  return request({ url: '/campus/exam/answer/list', method: 'get', params: query })
}

export function listWrongBook(query: any) {
  return request({ url: '/campus/exam/wrong/list', method: 'get', params: query })
}

export function startExam(params: any) {
  return request({ url: '/campus/exam/start', method: 'post', params })
}

export function submitExam(data: any) {
  return request({ url: '/campus/exam/submit', method: 'post', data })
}

export function getPaperDetail(paperId: number) {
  return request({ url: `/campus/detail/paper/${paperId}`, method: 'get' })
}

export function listCourse(query: any) {
  return request({ url: '/campus/course/list', method: 'get', params: query })
}

export async function fetchPortalUserOptions(userType?: string) {
  const res = await listUser({ pageNum: 1, pageSize: 200, userType })
  return (res.rows || []).map((item: any) => ({
    label: `${item.nickName || item.userName}（${item.userId}）`,
    value: item.userId,
  }))
}

export async function fetchPortalCourseOptions() {
  const res = await listCourse({ pageNum: 1, pageSize: 200 })
  return (res.rows || []).map((item: any) => ({
    label: `${item.courseName}（${item.courseId}）`,
    value: item.courseId,
  }))
}

export function listWarning(query: any) {
  return request({ url: '/campus/analysis/warning/list', method: 'get', params: query })
}

export function listReport(query: any) {
  return request({ url: '/campus/analysis/report/list', method: 'get', params: query })
}
