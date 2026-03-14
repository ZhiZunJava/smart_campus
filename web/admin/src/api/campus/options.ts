import { listUser } from '@/api/system/user'
import { listAiModel } from '@/api/campus/ai'
import { listCourse, listGrade, listClass } from '@/api/campus/teaching'

export async function fetchUserOptions(userType?: 'student' | 'teacher' | 'parent' | 'admin') {
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

export async function fetchAiModelOptions(modelType = 'chat') {
  const res = await listAiModel({ pageNum: 1, pageSize: 200, status: '0', modelType })
  return (res.rows || []).map((item: any) => ({
    label: `${item.modelName}（${item.provider}）`,
    value: item.modelId,
    modelName: item.modelName,
    provider: item.provider,
  }))
}
