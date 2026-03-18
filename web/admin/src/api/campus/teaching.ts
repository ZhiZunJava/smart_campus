import request from '@/utils/request'

export function listGrade(query: any) {
  return request({ url: '/campus/grade/list', method: 'get', params: query })
}
export function addGrade(data: any) {
  return request({ url: '/campus/grade', method: 'post', data })
}
export function updateGrade(data: any) {
  return request({ url: '/campus/grade', method: 'put', data })
}
export function delGrade(ids: number | number[]) {
  return request({ url: `/campus/grade/${ids}`, method: 'delete' })
}

export function listClass(query: any) {
  return request({ url: '/campus/class/list', method: 'get', params: query })
}
export function addClass(data: any) {
  return request({ url: '/campus/class', method: 'post', data })
}
export function updateClass(data: any) {
  return request({ url: '/campus/class', method: 'put', data })
}
export function delClass(ids: number | number[]) {
  return request({ url: `/campus/class/${ids}`, method: 'delete' })
}

export function listCourse(query: any) {
  return request({ url: '/campus/course/list', method: 'get', params: query })
}
export function addCourse(data: any) {
  return request({ url: '/campus/course', method: 'post', data })
}
export function updateCourse(data: any) {
  return request({ url: '/campus/course', method: 'put', data })
}
export function generateCourseCode(data: any) {
  return request({ url: '/campus/course/generate-code', method: 'post', data })
}
export function delCourse(ids: number | number[]) {
  return request({ url: `/campus/course/${ids}`, method: 'delete' })
}

export function listCourseChapter(query: any) {
  return request({ url: '/campus/courseChapter/list', method: 'get', params: query })
}
export function addCourseChapter(data: any) {
  return request({ url: '/campus/courseChapter', method: 'post', data })
}
export function updateCourseChapter(data: any) {
  return request({ url: '/campus/courseChapter', method: 'put', data })
}
export function delCourseChapter(ids: number | number[]) {
  return request({ url: `/campus/courseChapter/${ids}`, method: 'delete' })
}

export function listKnowledgePoint(query: any) {
  return request({ url: '/campus/knowledgePoint/list', method: 'get', params: query })
}
export function addKnowledgePoint(data: any) {
  return request({ url: '/campus/knowledgePoint', method: 'post', data })
}
export function updateKnowledgePoint(data: any) {
  return request({ url: '/campus/knowledgePoint', method: 'put', data })
}
export function delKnowledgePoint(ids: number | number[]) {
  return request({ url: `/campus/knowledgePoint/${ids}`, method: 'delete' })
}

export function listSchoolTerm(query: any) {
  return request({ url: '/campus/schoolTerm/list', method: 'get', params: query })
}
export function addSchoolTerm(data: any) {
  return request({ url: '/campus/schoolTerm', method: 'post', data })
}
export function updateSchoolTerm(data: any) {
  return request({ url: '/campus/schoolTerm', method: 'put', data })
}
export function delSchoolTerm(ids: number | number[]) {
  return request({ url: `/campus/schoolTerm/${ids}`, method: 'delete' })
}

export function listClassroom(query: any) {
  return request({ url: '/campus/classroom/list', method: 'get', params: query })
}
export function addClassroom(data: any) {
  return request({ url: '/campus/classroom', method: 'post', data })
}
export function updateClassroom(data: any) {
  return request({ url: '/campus/classroom', method: 'put', data })
}
export function delClassroom(ids: number | number[]) {
  return request({ url: `/campus/classroom/${ids}`, method: 'delete' })
}

export function listClassCourse(query: any) {
  return request({ url: '/campus/classCourse/list', method: 'get', params: query })
}
export function addClassCourse(data: any) {
  return request({ url: '/campus/classCourse', method: 'post', data })
}
export function updateClassCourse(data: any) {
  return request({ url: '/campus/classCourse', method: 'put', data })
}
export function generateTeachingClassCode(data: any) {
  return request({ url: '/campus/classCourse/generate-teaching-code', method: 'post', data })
}
export function delClassCourse(ids: number | number[]) {
  return request({ url: `/campus/classCourse/${ids}`, method: 'delete' })
}

export function listCourseSchedule(query: any) {
  return request({ url: '/campus/courseSchedule/list', method: 'get', params: query })
}
export function addCourseSchedule(data: any) {
  return request({ url: '/campus/courseSchedule', method: 'post', data })
}
export function updateCourseSchedule(data: any) {
  return request({ url: '/campus/courseSchedule', method: 'put', data })
}
export function checkCourseScheduleConflict(data: any) {
  return request({ url: '/campus/courseSchedule/conflict-check', method: 'post', data })
}
export function delCourseSchedule(ids: number | number[]) {
  return request({ url: `/campus/courseSchedule/${ids}`, method: 'delete' })
}

export function autoArrangeCourseSchedule(data: any) {
  return request({ url: '/campus/courseSchedule/auto-arrange', method: 'post', data })
}

export function getTimeTableLayout() {
  return request({ url: '/campus/timeTableLayout/current', method: 'get' })
}

export function updateTimeTableLayout(data: any) {
  return request({ url: '/campus/timeTableLayout/current', method: 'put', data })
}
