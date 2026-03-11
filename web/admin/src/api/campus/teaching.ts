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
