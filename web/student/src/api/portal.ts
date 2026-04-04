import request from '@/utils/request'

export function listUser(query: any) {
  return request({ url: '/system/user/list', method: 'get', params: query })
}

export function getStudentDashboard(params: any) {
  return request({ url: '/campus/portal/student/dashboard', method: 'get', params })
}

export function getPortalHelpCenter() {
  return request({ url: '/campus/portal/help', method: 'get' })
}

export function listPortalNotice(params?: any) {
  return request({ url: '/campus/portal/notice', method: 'get', params })
}

export function getPortalNoticeDetail(noticeId: number) {
  return request({ url: `/campus/portal/notice/${noticeId}`, method: 'get' })
}

export function getPortalMessageCenter(params: any) {
  return request({ url: '/campus/portal/messages', method: 'get', params })
}

export function getPortalMessageOverview(params: any) {
  return request({ url: '/campus/portal/message-center', method: 'get', params })
}

export function getPortalUnreadMessages(params: any) {
  return request({ url: '/campus/portal/messages', method: 'get', params })
}

export function markPortalMessageRead(noticeId: number) {
  return request({ url: `/campus/portal/message-center/${noticeId}/read`, method: 'put' })
}

export function getPortalProfile() {
  return request({ url: '/campus/portal/profile', method: 'get' })
}

export function getPortalGrowthSummary(params: any) {
  return request({ url: '/campus/portal/student/growth-summary', method: 'get', params })
}

export function updatePortalProfile(data: any) {
  return request({ url: '/campus/portal/profile', method: 'put', data })
}

export function updatePortalPassword(data: any) {
  return request({ url: '/campus/portal/profile/password', method: 'put', data })
}

export function getTeacherDashboard(params: any) {
  return request({ url: '/campus/portal/teacher/dashboard', method: 'get', params })
}

export function listPortalTeacherCourses(params: any) {
  return request({ url: '/campus/portal/teacher/my-courses', method: 'get', params })
}

export function getPortalTeacherCourseStudents(params: any) {
  return request({ url: '/campus/portal/teacher/course-students', method: 'get', params })
}

export function getPortalTeacherSchedule(params: any) {
  return request({ url: '/campus/portal/teacher/my-schedule', method: 'get', params })
}

export function getAdvisorDashboard(params: any) {
  return request({ url: '/campus/portal/advisor/dashboard', method: 'get', params })
}

export function listAdvisorStudents(params: any) {
  return request({ url: '/campus/portal/advisor/students', method: 'get', params })
}

export function listAdvisorScores(params: any) {
  return request({ url: '/campus/portal/advisor/scores', method: 'get', params })
}

export function getParentDashboard(params: any) {
  return request({ url: '/campus/portal/parent/dashboard', method: 'get', params })
}

export function listPortalParentChildren(params: any) {
  return request({ url: '/campus/portal/parent/children', method: 'get', params })
}

export function bindParentChild(data: { parentUserId: number; studentNo: string; relationType: string }) {
  return request({ url: '/campus/portal/parent/bind-child', method: 'post', data })
}

export function unbindParentChild(relId: number) {
  return request({ url: `/campus/portal/parent/unbind-child/${relId}`, method: 'delete' })
}

// Parent: search students for binding
export function searchStudentsForBind(params: { parentUserId: number; keyword: string }) {
  return request({ url: '/campus/portal/parent/search-students', method: 'get', params })
}

// Student: parent binding requests
export function getStudentParentRequests(status?: string) {
  return request({ url: '/campus/portal/student/parent-requests', method: 'get', params: { status: status || '' } })
}

export function acceptParentRequest(relId: number) {
  return request({ url: `/campus/portal/student/parent-requests/${relId}/accept`, method: 'put' })
}

export function rejectParentRequest(relId: number) {
  return request({ url: `/campus/portal/student/parent-requests/${relId}/reject`, method: 'put' })
}

export function listPortalParentCourses(params: any) {
  return request({ url: '/campus/portal/parent/child-courses', method: 'get', params })
}

export function getPortalParentSchedule(params: any) {
  return request({ url: '/campus/portal/parent/child-schedule', method: 'get', params })
}

function buildAffairBase(role: string) {
  return `/campus/portal/${role}/affair`
}

export function getPortalAffairOptions() {
  return request({ url: '/campus/portal/affair/options', method: 'get' })
}

export function getPortalAffairServices(role: 'student' | 'teacher') {
  return request({ url: `${buildAffairBase(role)}/services`, method: 'get' })
}

export function getPortalAffairDashboard(role: 'student' | 'teacher' | 'advisor') {
  return request({ url: `${buildAffairBase(role)}/dashboard`, method: 'get' })
}

export function listPortalAffairRequests(role: 'student' | 'teacher') {
  return request({ url: `${buildAffairBase(role)}/request/list`, method: 'get' })
}

export function getPortalAffairRequestDetail(role: 'student' | 'teacher' | 'advisor', requestId: number) {
  return request({ url: `${buildAffairBase(role)}/request/${requestId}`, method: 'get' })
}

export function submitPortalAffairRequest(role: 'student' | 'teacher', data: any) {
  return request({ url: `${buildAffairBase(role)}/request`, method: 'post', data })
}

export function cancelPortalAffairRequest(role: 'student' | 'teacher', requestId: number) {
  return request({ url: `${buildAffairBase(role)}/request/${requestId}/cancel`, method: 'put' })
}

export function listPortalAffairReviewList(role: 'teacher' | 'advisor') {
  return request({ url: `${buildAffairBase(role)}/review/list`, method: 'get' })
}

export function reviewPortalAffairRequest(role: 'teacher' | 'advisor', data: any) {
  return request({ url: `${buildAffairBase(role)}/review`, method: 'put', data })
}

export function batchReviewPortalAffairRequests(role: 'teacher' | 'advisor', data: any) {
  return request({ url: `${buildAffairBase(role)}/review/batch`, method: 'put', data })
}

export function getPortalAffairStatistics(role: string) {
  return request({ url: `/campus/portal/${role}/affair/statistics`, method: 'get' })
}

export function getPortalAffairCategoryDetail(role: string, categoryCode: string) {
  return request({ url: `/campus/portal/${role}/affair/category/${categoryCode}`, method: 'get' })
}

export function getPortalAffairRecentActivity(role: string) {
  return request({ url: `/campus/portal/${role}/affair/recent-activity`, method: 'get' })
}

export function getPortalAffairFrequentTemplates(role: string) {
  return request({ url: `/campus/portal/${role}/affair/frequent-templates`, method: 'get' })
}

export function listPortalWorkStudyJobs() {
  return request({ url: '/campus/portal/student/affair/work-study/jobs', method: 'get' })
}

export function uploadPortalAffairAttachment(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/common/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function listResource(query: any) {
  return request({ url: '/campus/portal/learning/resource/list', method: 'get', params: query })
}

export function getResourceDetail(resourceId: number) {
  return request({ url: `/campus/portal/learning/resource/${resourceId}`, method: 'get' })
}

export function getResourceTree(courseId: number) {
  return request({ url: '/campus/portal/learning/resource/tree', method: 'get', params: { courseId } })
}

export function listResourceAsset(query: any) {
  return request({ url: '/campus/portal/learning/resource/asset/list', method: 'get', params: query })
}

export function listResourceComment(resourceId: number) {
  return request({ url: '/campus/portal/learning/resource/comment/list', method: 'get', params: { resourceId } })
}

export function addResourceComment(data: any) {
  return request({ url: '/campus/portal/learning/resource/comment', method: 'post', data })
}

export function likeResourceComment(commentId: number) {
  return request({ url: `/campus/portal/learning/resource/comment/${commentId}/like`, method: 'post' })
}

export function favoriteResource(resourceId: number) {
  return request({ url: `/campus/portal/learning/resource/${resourceId}/favorite`, method: 'post' })
}

export function unfavoriteResource(resourceId: number) {
  return request({ url: `/campus/portal/learning/resource/${resourceId}/unfavorite`, method: 'post' })
}

export function getFavoriteResourceStatus(resourceId: number) {
  return request({ url: `/campus/portal/learning/resource/${resourceId}/favorite`, method: 'get' })
}

export function listFavoriteResources(query: any) {
  return request({ url: '/campus/portal/learning/resource/favorite/list', method: 'get', params: query })
}

export function rateResource(resourceId: number, ratingScore: number) {
  return request({ url: `/campus/portal/learning/resource/${resourceId}/rate`, method: 'post', params: { ratingScore } })
}

export function shareResource(resourceId: number) {
  return request({ url: `/campus/portal/learning/resource/${resourceId}/share`, method: 'post' })
}

export function getOfficePreview(resource: string) {
  return request({ url: '/common/office/preview', method: 'post', params: { resource } })
}

export function getOfficePreviewStatus(taskId: string) {
  return request({ url: '/common/office/preview/status', method: 'get', params: { taskId } })
}

export function listQaSession(query: any) {
  return request({ url: '/campus/qa/session/list', method: 'get', params: query })
}

export function uploadPortalQaAttachment(file: File, sessionId?: number) {
  const formData = new FormData()
  formData.append('file', file)
  if (sessionId) {
    formData.append('sessionId', String(sessionId))
  }
  return request({
    url: '/campus/qa/attachment/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function deletePortalQaAttachment(attachmentId: number) {
  return request({ url: `/campus/qa/attachment/delete/${attachmentId}`, method: 'post' })
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
  return request({ url: '/campus/portal/exam/paper/list', method: 'get', params: query })
}

export function listQuestionBank(query: any) {
  return request({ url: '/campus/portal/exam/question/list', method: 'get', params: query })
}

export function listExamRecord(query: any) {
  return request({ url: '/campus/portal/exam/record/list', method: 'get', params: query })
}

export function getExamRecordOverview(query: any) {
  return request({ url: '/campus/portal/exam/record/overview', method: 'get', params: query })
}

export function getExamRecordDetail(recordId: number) {
  return request({ url: `/campus/portal/exam/record/${recordId}`, method: 'get' })
}

export function getExamRuntime(recordId: number) {
  return request({ url: `/campus/portal/exam/runtime/${recordId}`, method: 'get' })
}

export function listExamAnswer(query: any) {
  return request({ url: '/campus/exam/answer/list', method: 'get', params: query })
}

export function listWrongBook(query: any) {
  return request({ url: '/campus/portal/exam/wrong/list', method: 'get', params: query })
}

export function getWrongBookOverview(query: any) {
  return request({ url: '/campus/portal/exam/wrong/overview', method: 'get', params: query })
}

export function getWrongBookDetail(wrongId: number) {
  return request({ url: `/campus/portal/exam/wrong/${wrongId}`, method: 'get' })
}

export function createWrongRetryPaper(data: any) {
  return request({ url: '/campus/portal/exam/wrong/retry', method: 'post', data })
}

export function startExam(params: any) {
  return request({ url: '/campus/portal/exam/start', method: 'post', params })
}

export function submitExam(data: any) {
  return request({ url: '/campus/portal/exam/submit', method: 'post', data })
}

export function saveExamDraft(data: any) {
  return request({ url: '/campus/portal/exam/draft/save', method: 'post', data })
}

export function listExamDrafts(recordId: number) {
  return request({ url: `/campus/portal/exam/draft/${recordId}`, method: 'get' })
}

export function submitExamQuestion(data: any) {
  return request({ url: '/campus/portal/exam/question/submit', method: 'post', data })
}

export function submitExamSubPaper(data: any) {
  return request({ url: '/campus/portal/exam/sub-paper/submit', method: 'post', data })
}

export function getPaperDetail(paperId: number) {
  return request({ url: `/campus/portal/exam/paper/${paperId}`, method: 'get' })
}

export function listCourse(query: any) {
  return request({ url: '/campus/course/list', method: 'get', params: query })
}

export function listKnowledgePoint(query: any) {
  return request({ url: '/campus/knowledgePoint/list', method: 'get', params: query })
}

export function listPortalMyCourses(params: any) {
  return request({ url: '/campus/portal/student/my-courses', method: 'get', params })
}

export function listPortalMyClassCourses(params: any) {
  return request({ url: '/campus/portal/student/class-courses', method: 'get', params })
}

export function getPortalCourseDetail(params: any) {
  return request({ url: '/campus/portal/student/course-detail', method: 'get', params })
}

export function getPortalCourseSelectionOptions(params: any) {
  return request({ url: '/campus/portal/student/course-selection/options', method: 'get', params })
}

export function getPortalCourseSelectionStudentCounts(data: any) {
  return request({ url: '/campus/portal/student/course-selection/student-counts', method: 'post', data })
}

export function selectPortalCourse(data: any) {
  return request({ url: '/campus/portal/student/course-selection/select', method: 'post', data })
}

export function dropPortalCourse(data: any) {
  return request({ url: '/campus/portal/student/course-selection/drop', method: 'post', data })
}

export function listPortalPersonalizedSelectionOptions(params: any) {
  return request({ url: '/campus/portal/student/personalized-selection/options', method: 'get', params })
}

export function listPortalPersonalizedSelectionRequests(params: any) {
  return request({ url: '/campus/portal/student/personalized-selection/requests', method: 'get', params })
}

export function createPortalPersonalizedSelectionRequest(data: any) {
  return request({ url: '/campus/portal/student/personalized-selection/request', method: 'post', data })
}

export function cancelPortalPersonalizedSelectionRequest(requestId: number, userId: number) {
  return request({
    url: '/campus/portal/student/personalized-selection/cancel',
    method: 'post',
    params: { userId },
    data: { requestId },
  })
}

export function getPortalTaskCenter(params: any) {
  return request({ url: '/campus/portal/student/tasks', method: 'get', params })
}

export function buildStaticTaskCards(items: any[]) {
  return (items || []).map((item: any, index: number) => ({
    key: item.key || `task-card-${index}`,
    title: item.title || '未命名待办',
    statusText: item.desc || item.statusText || item.tag || '待处理',
    raw: {
      ...item,
      action: item.action || {
        type: 'route',
        path: item.path,
        targetId: item.targetId,
      },
    },
  }))
}

export function markPortalTaskRead(dispatchId: number) {
  return request({ url: `/campus/portal/student/task-dispatch/${dispatchId}/read`, method: 'put' })
}

export function reportPortalTaskFeedback(data: any) {
  return request({ url: '/campus/portal/student/task-feedback', method: 'put', data })
}

export function completePortalTask(dispatchId: number, data?: any) {
  return request({ url: `/campus/portal/student/task-dispatch/${dispatchId}/complete`, method: 'put', data })
}

export function getPortalTaskDetail(dispatchId: number) {
  return request({ url: `/campus/portal/student/task-dispatch/${dispatchId}/detail`, method: 'get' })
}

export function submitPortalTask(dispatchId: number, data: any) {
  return request({ url: `/campus/portal/student/task-dispatch/${dispatchId}/submit`, method: 'put', data })
}

export function uploadPortalTaskAttachment(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/common/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function getPortalMySchedule(params: any) {
  return request({ url: '/campus/portal/student/my-schedule', method: 'get', params })
}

export function getPortalMyClassSchedule(params: any) {
  return request({ url: '/campus/portal/student/class-schedule', method: 'get', params })
}

export function listPortalTermOptions() {
  return request({ url: '/campus/portal/term/options', method: 'get' })
}

export function listStudentTextbookPlans(userId: number) {
  return request({ url: '/campus/portal/student/textbook-plans', method: 'get', params: { userId } })
}

export function listPortalScore(query: any) {
  return request({ url: '/campus/portal/score/list', method: 'get', params: query })
}

export function getPortalScoreOverview(query: any) {
  return request({ url: '/campus/portal/score/overview', method: 'get', params: query })
}

export function getPortalScoreAnalysis(query: any) {
  return request({ url: '/campus/portal/score/analysis', method: 'get', params: query })
}

export function getPortalScoreDetail(query: any) {
  return request({ url: '/campus/portal/score/detail', method: 'get', params: query })
}

export function listPortalCourseOfferings(params: any) {
  return request({ url: '/campus/portal/student/course-offerings', method: 'get', params })
}

export function getPortalCourseOfferingFilterOptions(params?: any) {
  return request({ url: '/campus/portal/student/course-offerings/options', method: 'get', params })
}

export function exportPortalCourseOfferings(data: any) {
  return request({
    url: '/campus/portal/student/course-offerings/export',
    method: 'post',
    data,
    responseType: 'blob',
  })
}

export async function fetchPortalUserOptions(userType?: string) {
  const res = await listUser({ pageNum: 1, pageSize: 200, userType })
  return (res.rows || []).map((item: any) => ({
    label: `${item.nickName || item.userName}（${item.userId}）`,
    value: item.userId,
  }))
}

export async function fetchPortalCourseOptions(userId?: number) {
  try {
    if (!userId) return []
    const res = await listPortalMyCourses({ userId })
    return (res.data || res || []).map((item: any) => ({
      label: `${item.courseName}（${item.courseId}）`,
      value: item.courseId,
    }))
  } catch {
    return []
  }
}

export async function fetchPortalCourseCatalogOptions(userId?: number) {
  try {
    if (!userId) return []
    const res = await listPortalMyCourses({ userId })
    return (res.data || res || []).map((item: any) => ({
      label: `${item.courseName}（${item.courseId}）`,
      value: item.courseId,
    }))
  } catch {
    return []
  }
}

export async function fetchPortalKnowledgePointOptions() {
  try {
    return []
  } catch {
    return []
  }
}

export async function fetchPortalStudentKnowledgePointOptions(userId?: number, courseId?: number, limit = 200) {
  try {
    if (!userId) return []
    const res = await request({
      url: '/campus/portal/student/knowledge-points',
      method: 'get',
      params: { userId, courseId, limit },
    })
    return (res.data || res || []).map((item: any) => ({
      label: item.label || `${item.knowledgeName}（${item.knowledgePointId}）`,
      value: item.knowledgePointId || item.value,
      courseId: item.courseId,
    }))
  } catch {
    return []
  }
}

// ---- 学籍核对 ----
export function getVerificationActiveBatches() {
  return request({ url: '/campus/portal/student/verification/batches', method: 'get' })
}

export function getVerificationInfo(batchId: number) {
  return request({ url: `/campus/portal/student/verification/info/${batchId}`, method: 'get' })
}

export function confirmVerificationNoChange(recordId: number) {
  return request({ url: `/campus/portal/student/verification/confirm/${recordId}`, method: 'put' })
}

export function submitVerificationChanges(data: any) {
  return request({ url: '/campus/portal/student/verification/submit', method: 'post', data })
}

export function getVerificationMyRecords() {
  return request({ url: '/campus/portal/student/verification/my-records', method: 'get' })
}

export function getVerificationRecordDetail(recordId: number) {
  return request({ url: `/campus/portal/student/verification/record/${recordId}`, method: 'get' })
}
