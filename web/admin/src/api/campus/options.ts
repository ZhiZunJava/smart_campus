import { listUser } from '@/api/system/user'
import { getUser } from '@/api/system/user'
import { listRole } from '@/api/system/role'
import { listAiModel } from '@/api/campus/ai'
import { listCourse, listGrade, listClass, listSchoolTerm, listClassroom } from '@/api/campus/teaching'

function joinLocationParts(parts: any[]) {
  const result: string[] = []
  parts.forEach((part) => {
    const text = String(part || '').trim()
    if (!text) return
    if (result.some((item) => item === text || item.includes(text) || text.includes(item))) return
    result.push(text)
  })
  return result.join(' / ')
}

function resolveUserTypeLabel(userType?: string) {
  const labels: Record<string, string> = {
    student: '学生',
    teacher: '教师',
    parent: '家长',
    admin: '管理员',
  }
  return labels[String(userType || '').trim()] || '未分类'
}

export async function fetchUserOptions(
  userType?: 'student' | 'teacher' | 'parent' | 'admin',
  keyword?: string,
  filters?: { classIds?: any[]; gradeIds?: any[]; userIds?: any[] },
) {
  const params: Record<string, any> = { pageNum: 1, pageSize: 200, userType }
  if (keyword) params.keyword = keyword
  if (filters?.classIds?.length) params.classIds = filters.classIds.join(',')
  if (filters?.gradeIds?.length) params.gradeIds = filters.gradeIds.join(',')
  if (filters?.userIds?.length) params.userIds = filters.userIds.join(',')
  const res = await listUser(params)
  return (res.rows || []).map((item: any) => ({
    label: `${item.nickName || item.userName}（${item.userId}） · ${resolveUserTypeLabel(item.userType)}`,
    shortLabel: `${item.nickName || item.userName}（${item.userId}）`,
    value: item.userId,
    userType: item.userType,
    userTypeLabel: resolveUserTypeLabel(item.userType),
    nickName: item.nickName,
    userName: item.userName,
  }))
}

export async function fetchTeachingUserOptions(identity?: 'course_teacher' | 'head_teacher' | 'academic_admin') {
  const res = await listUser({ pageNum: 1, pageSize: 200, userType: identity === 'academic_admin' ? 'admin' : 'teacher' })
  const rows = res.rows || []
  const detailList = await Promise.all(rows.map(async (item: any) => {
    try {
      const detail = await getUser(item.userId)
      const roleIds = detail.roleIds || []
      const postIds = detail.postIds || []
      const roleLabels = (detail.roles || []).filter((role: any) => roleIds.includes(role.roleId)).map((role: any) => role.roleName)
      const postLabels = (detail.posts || []).filter((post: any) => postIds.includes(post.postId)).map((post: any) => post.postName)
      const roleKeys = (detail.roles || []).filter((role: any) => roleIds.includes(role.roleId)).map((role: any) => role.roleKey)
      const postCodes = (detail.posts || []).filter((post: any) => postIds.includes(post.postId)).map((post: any) => post.postCode)
      return {
        ...item,
        roleKeys,
        postCodes,
        roleLabels,
        postLabels,
      }
    } catch {
      return {
        ...item,
        roleKeys: [],
        postCodes: [],
        roleLabels: [],
        postLabels: [],
      }
    }
  }))

  const filtered = detailList.filter((item: any) => {
    if (!identity) return true
    if (identity === 'course_teacher') {
      return item.postCodes.includes('course_teacher') || item.roleKeys.includes('teacher')
    }
    if (identity === 'head_teacher') {
      return item.postCodes.includes('head_teacher') || item.roleKeys.includes('campus_head_teacher')
    }
    if (identity === 'academic_admin') {
      return item.postCodes.includes('academic_admin') || item.roleKeys.includes('campus_academic_admin')
    }
    return true
  })

  return filtered.map((item: any) => ({
    label: `${item.nickName || item.userName}（${item.userId}）${item.postLabels?.length ? ' · ' + item.postLabels.join('/') : ''}`,
    value: item.userId,
    roleKeys: item.roleKeys,
    postCodes: item.postCodes,
    roleLabels: item.roleLabels,
    postLabels: item.postLabels,
  }))
}

export async function fetchRoleOptions(keyword?: string) {
  const res = await listRole({ pageNum: 1, pageSize: 200, roleName: keyword || undefined } as any)
  return (res.rows || []).map((item: any) => ({
    label: `${item.roleName}（${item.roleId}）`,
    shortLabel: item.roleName,
    value: item.roleId,
    roleKey: item.roleKey,
    status: item.status,
  }))
}

export async function fetchCourseOptions(schoolYear?: string) {
  const gradeRes = schoolYear ? await listGrade({ pageNum: 1, pageSize: 200, schoolYear, status: '0' }) : null
  const gradeIds = new Set((gradeRes?.rows || []).map((item: any) => item.gradeId))
  const res = await listCourse({ pageNum: 1, pageSize: 200 })
  const rows = schoolYear ? (res.rows || []).filter((item: any) => gradeIds.has(item.gradeId)) : (res.rows || [])
  return rows.map((item: any) => ({
    label: `${item.courseName}（${item.courseId}）`,
    value: item.courseId,
    gradeId: item.gradeId,
  }))
}

export async function fetchSchoolYearOptions() {
  const res = await listSchoolTerm({ pageNum: 1, pageSize: 200, status: '0' })
  const values = Array.from(new Set((res.rows || []).map((item: any) => item.schoolYear).filter(Boolean)))
  return values.map((item) => ({
    label: item,
    value: item,
  }))
}

export async function fetchGradeOptions(schoolYear?: string) {
  const res = await listGrade({ pageNum: 1, pageSize: 200, schoolYear })
  return (res.rows || []).map((item: any) => ({
    label: `${item.gradeName}（${item.gradeId}）`,
    value: item.gradeId,
    schoolYear: item.schoolYear,
  }))
}

export async function fetchClassOptions(schoolYear?: string, deptId?: number | string) {
  const gradeRes = schoolYear ? await listGrade({ pageNum: 1, pageSize: 200, schoolYear, status: '0' }) : null
  const gradeIds = new Set((gradeRes?.rows || []).map((item: any) => item.gradeId))
  const res = await listClass({ pageNum: 1, pageSize: 200, deptId })
  let rows = res.rows || []
  if (schoolYear) {
    rows = rows.filter((item: any) => gradeIds.has(item.gradeId))
  }
  if (deptId != null && deptId !== '') {
    rows = rows.filter((item: any) => String(item.deptId || '') === String(deptId))
  }
  return rows.map((item: any) => ({
    label: `${item.className}（${item.classId}）${item.deptName ? ' · ' + item.deptName : ''}`,
    value: item.classId,
    gradeId: item.gradeId,
    deptId: item.deptId,
    deptName: item.deptName,
  }))
}

export async function fetchTermOptions() {
  const res = await listSchoolTerm({ pageNum: 1, pageSize: 200, status: '0' })
  return (res.rows || []).map((item: any) => ({
    label: `${item.termName}（${item.schoolYear}）`,
    value: item.termId,
    termName: item.termName,
    termCode: item.termCode,
    schoolYear: item.schoolYear,
    startDate: item.startDate,
    endDate: item.endDate,
    totalWeeks: item.totalWeeks,
    isCurrent: item.isCurrent,
  }))
}

export async function fetchClassroomOptions() {
  const res = await listClassroom({ pageNum: 1, pageSize: 500, status: '0' })
  return (res.rows || []).map((item: any) => ({
    label: joinLocationParts([item.campusName || '未配置校区', item.buildingName || '未配置教学楼', item.classroomName || '未命名教室']),
    value: item.classroomId,
    classroomName: item.classroomName,
    buildingName: item.buildingName,
    campusName: item.campusName,
    capacity: item.capacity,
  }))
}

export async function fetchAiModelOptions(modelType = 'chat') {
  const res = await listAiModel({ pageNum: 1, pageSize: 200, status: '0', modelType })
  return (res.rows || []).map((item: any) => ({
    label: `${item.modelName}（${item.provider}）`,
    value: item.modelId,
    modelName: item.modelName,
    provider: item.provider,
  }))
}
