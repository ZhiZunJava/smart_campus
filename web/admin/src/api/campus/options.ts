import { listUser } from '@/api/system/user'
import { listCourse, listGrade, listClass } from '@/api/campus/teaching'

export async function fetchUserOptions(userType?: string) {
  const res = await listUser({ pageNum: 1, pageSize: 200, userType })
  return (res.rows || []).map((item: any) => ({
    label: `${item.nickName || item.userName}（${item.userId}）`,
    value: item.userId,
  }))
}

export async function fetchCourseOptions() {
  const res = await listCourse({ pageNum: 1, pageSize: 200 })
  return (res.rows || []).map((item: any) => ({
    label: `${item.courseName}（${item.courseId}）`,
    value: item.courseId,
  }))
}

export async function fetchGradeOptions() {
  const res = await listGrade({ pageNum: 1, pageSize: 200 })
  return (res.rows || []).map((item: any) => ({
    label: `${item.gradeName}（${item.gradeId}）`,
    value: item.gradeId,
  }))
}

export async function fetchClassOptions() {
  const res = await listClass({ pageNum: 1, pageSize: 200 })
  return (res.rows || []).map((item: any) => ({
    label: `${item.className}（${item.classId}）`,
    value: item.classId,
  }))
}
