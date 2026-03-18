import { listUser } from '@/api/system/user'
import { getUser } from '@/api/system/user'
import { listAiModel } from '@/api/campus/ai'
import { listCourse, listGrade, listClass, listSchoolTerm, listClassroom } from '@/api/campus/teaching'

export async function fetchUserOptions(userType?: 'student' | 'teacher' | 'parent' | 'admin') {
  const res = await listUser({ pageNum: 1, pageSize: 200, userType })
  return (res.rows || []).map((item: any) => ({
    label: `${item.nickName || item.userName}（${item.userId}）`,
    value: item.userId,
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
    startDate: item.startDate,
    endDate: item.endDate,
    totalWeeks: item.totalWeeks,
    isCurrent: item.isCurrent,
  }))
}

export async function fetchClassroomOptions() {
  const res = await listClassroom({ pageNum: 1, pageSize: 500, status: '0' })
  return (res.rows || []).map((item: any) => ({
    label: `${item.classroomName} · ${item.buildingName || '未配置楼栋'} · ${item.campusName || '未配置校区'}`,
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
