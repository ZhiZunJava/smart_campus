export interface StudentAffairPageConfig {
  categoryCode: string
  slug: string
  title: string
  desc: string
  groupKey: string
  groupLabel: string
}

export const studentAffairPageConfigs: StudentAffairPageConfig[] = [
  { categoryCode: 'ASK_LEAVE', slug: 'ask-leave', title: '请销假', desc: '办理请假、销假和请假进度查询', groupKey: 'attendance', groupLabel: '考勤服务' },
  { categoryCode: 'LEAVE_RETURN_SCHOOL', slug: 'leave-return-school', title: '离返校', desc: '提交离校返校申请与行程信息', groupKey: 'attendance', groupLabel: '考勤服务' },
  { categoryCode: 'SCHOLARSHIP', slug: 'scholarship', title: '奖学金', desc: '奖学金申报、佐证上传与结果查看', groupKey: 'funding', groupLabel: '资助服务' },
  { categoryCode: 'GRANT', slug: 'grant', title: '助学金', desc: '助学金申请与审核进度跟踪', groupKey: 'funding', groupLabel: '资助服务' },
  { categoryCode: 'LOAN', slug: 'loan', title: '助学贷款', desc: '国家助学贷款与生源地贷款申请', groupKey: 'funding', groupLabel: '资助服务' },
  { categoryCode: 'WORK_STUDY', slug: 'work-study', title: '勤工助学', desc: '勤工助学岗位申请与信息维护', groupKey: 'funding', groupLabel: '资助服务' },
  { categoryCode: 'POVERTY_IDENTIFICATION', slug: 'poverty-identification', title: '贫困认定', desc: '家庭经济困难学生认定与复核', groupKey: 'funding', groupLabel: '资助服务' },
  { categoryCode: 'ACTIVITY', slug: 'activity', title: '活动申请', desc: '学生活动立项、申请与流程跟踪', groupKey: 'growth', groupLabel: '成长发展' },
  { categoryCode: 'SUBJECT_COMPETITION', slug: 'subject-competition', title: '学科竞赛', desc: '竞赛报名、立项与指导审核', groupKey: 'growth', groupLabel: '成长发展' },
  { categoryCode: 'INNOVATION_ENTREPRENEURSHIP', slug: 'innovation-entrepreneurship', title: '创新创业', desc: '创新创业项目申报与过程管理', groupKey: 'growth', groupLabel: '成长发展' },
  { categoryCode: 'COMPREHENSIVE_EVALUATION', slug: 'comprehensive-evaluation', title: '综测测评', desc: '综测申报、材料提交与结果查看', groupKey: 'growth', groupLabel: '成长发展' },
  { categoryCode: 'TEXTBOOK', slug: 'textbook', title: '教材管理', desc: '教材领用、补领和征订服务', groupKey: 'academic', groupLabel: '教务服务' },
  { categoryCode: 'ACADEMIC_STATUS', slug: 'academic-status', title: '学籍异动', desc: '休学、复学、转专业等学籍业务办理', groupKey: 'academic', groupLabel: '教务服务' },
]

export function buildStudentAffairPathByCode(categoryCode: string) {
  const match = studentAffairPageConfigs.find((item) => item.categoryCode === categoryCode)
  return match ? `/student/affairs/${match.slug}` : '/student/affairs'
}

export function resolveStudentAffairCode(segment?: string) {
  if (!segment) return ''
  const bySlug = studentAffairPageConfigs.find((item) => item.slug === segment)
  if (bySlug) return bySlug.categoryCode
  const byCode = studentAffairPageConfigs.find((item) => item.categoryCode === segment)
  return byCode?.categoryCode || segment
}
