export interface AffairGuideStep {
  title: string
  desc: string
}

export interface AffairPresentation {
  categoryCode: string
  title: string
  subtitle: string
  intro: string
  highlightLabel: string
  accentClass: string
  guideSteps: AffairGuideStep[]
  emptyHint: string
  primaryActionLabel: string
}

export const affairPresentationMap: Record<string, AffairPresentation> = {
  ASK_LEAVE: {
    categoryCode: 'ASK_LEAVE',
    title: '请销假服务',
    subtitle: '请假申请、审批结果、销假登记一体化办理',
    intro: '适用于学生事假、病假、公假等场景。审批通过后，可在返校后通过销假登记补齐闭环记录。',
    highlightLabel: '考勤服务',
    accentClass: 'is-attendance',
    emptyHint: '还没有请销假记录，可以先发起请假申请。',
    primaryActionLabel: '发起请假',
    guideSteps: [
      { title: '填写申请', desc: '明确请假类型、请假时间和去向信息，必要时上传附件。' },
      { title: '等待审核', desc: '系统会优先提交给绑定辅导员审核，没有绑定时回退到班主任链路。' },
      { title: '返校销假', desc: '返校后进入销假登记，补充实际返校时间与说明。' },
    ],
  },
  LEAVE_RETURN_SCHOOL: {
    categoryCode: 'LEAVE_RETURN_SCHOOL',
    title: '离返校服务',
    subtitle: '离校申请、返校登记和行程信息同步管理',
    intro: '覆盖离校、返校的申请与确认流程，方便辅导员及时掌握学生去向与返校状态。',
    highlightLabel: '考勤服务',
    accentClass: 'is-attendance',
    emptyHint: '当前还没有离返校记录。',
    primaryActionLabel: '发起离校申请',
    guideSteps: [
      { title: '离校申请', desc: '填写离校日期、目的地与健康状态。' },
      { title: '返校登记', desc: '返校后及时登记返校时间、行程与补充说明。' },
      { title: '动态追踪', desc: '申请结果会同步到消息中心，方便你和辅导员查看。' },
    ],
  },
  SCHOLARSHIP: {
    categoryCode: 'SCHOLARSHIP',
    title: '奖学金服务',
    subtitle: '在这里申请奖学金，随时查看审核进度',
    intro: '国家奖学金、校级奖学金、专项奖学金都可以在这里申请，提交材料后老师会在线审核，结果第一时间通知你。',
    highlightLabel: '资助服务',
    accentClass: 'is-funding',
    emptyHint: '你还没有提交过奖学金申请，有合适的就大胆申报吧。',
    primaryActionLabel: '申请奖学金',
    guideSteps: [
      { title: '准备好材料', desc: '把你的成绩单、荣誉证书等佐证材料整理好，申请时需要上传。' },
      { title: '提交申请', desc: '选择对应的奖学金类型，填写信息并提交，老师会收到通知。' },
      { title: '等结果', desc: '老师审核后你会在消息中心收到通知，也可以随时回来看进度。' },
    ],
  },
  GRANT: {
    categoryCode: 'GRANT',
    title: '助学金服务',
    subtitle: '助学金申请、困难等级申报与资助进度查询',
    intro: '支持国家助学金、校级助学金等资助类型申请，适合按学期进行申报与跟踪。',
    highlightLabel: '资助服务',
    accentClass: 'is-funding',
    emptyHint: '还没有助学金申请记录。',
    primaryActionLabel: '发起助学金申请',
    guideSteps: [
      { title: '填写家庭情况', desc: '如实填写家庭收入、困难等级和申请说明。' },
      { title: '辅导员审核', desc: '辅导员与资助中心按流程完成线上审核。' },
      { title: '查看资助结果', desc: '申请结果支持在线回看与留档。' },
    ],
  },
  LOAN: {
    categoryCode: 'LOAN',
    title: '助学贷款服务',
    subtitle: '助学贷款申请、金额登记与审核进度追踪',
    intro: '覆盖国家助学贷款、生源地贷款等场景，支持在线登记金额与期限信息。',
    highlightLabel: '资助服务',
    accentClass: 'is-funding',
    emptyHint: '还没有助学贷款申请记录。',
    primaryActionLabel: '发起贷款申请',
    guideSteps: [
      { title: '确认贷款类型', desc: '选择国家助学贷款或生源地贷款等类型。' },
      { title: '提交审核', desc: '辅导员与资助中心按流程审核申请信息。' },
      { title: '持续跟踪', desc: '后续可通过记录查看贷款申请状态。' },
    ],
  },
  WORK_STUDY: {
    categoryCode: 'WORK_STUDY',
    title: '勤工助学服务',
    subtitle: '岗位意向申报与岗位审核结果查看',
    intro: '围绕勤工助学岗位申请展开，支持岗位意向、可投入时长和技能特长填写。',
    highlightLabel: '资助服务',
    accentClass: 'is-funding',
    emptyHint: '还没有勤工助学申请记录。',
    primaryActionLabel: '发起岗位申请',
    guideSteps: [
      { title: '填写岗位意向', desc: '说明可参与的岗位方向和每周可投入时间。' },
      { title: '等待审核', desc: '辅导员核验信息后再进入后续安排。' },
      { title: '保留记录', desc: '申请记录会保留在事务页方便回顾。' },
    ],
  },
  POVERTY_IDENTIFICATION: {
    categoryCode: 'POVERTY_IDENTIFICATION',
    title: '贫困认定服务',
    subtitle: '家庭经济困难认定、复核与结果留档',
    intro: '适用于家庭经济困难学生认定与复核，支持提交家庭情况说明和认定佐证。',
    highlightLabel: '资助服务',
    accentClass: 'is-funding',
    emptyHint: '还没有贫困认定申请记录。',
    primaryActionLabel: '发起贫困认定',
    guideSteps: [
      { title: '提交认定材料', desc: '填写家庭信息、困难等级和说明。' },
      { title: '辅导员复核', desc: '辅导员与资助中心会在线复核信息。' },
      { title: '结果存档', desc: '认定结果保留在系统中便于后续资助联动。' },
    ],
  },
  ACTIVITY: {
    categoryCode: 'ACTIVITY',
    title: '活动申请服务',
    subtitle: '活动立项、流程审核与结果追踪',
    intro: '适用于学生活动申请、场地申请和活动执行前的线上审批协同。',
    highlightLabel: '成长发展',
    accentClass: 'is-growth',
    emptyHint: '还没有活动申请记录。',
    primaryActionLabel: '发起活动申请',
    guideSteps: [
      { title: '准备活动信息', desc: '填写活动名称、时间、地点和参与规模。' },
      { title: '指导审核', desc: '指导教师与辅导员按流程审核活动方案。' },
      { title: '跟踪结果', desc: '审批结果可以在页面和消息中心同步查看。' },
    ],
  },
  SUBJECT_COMPETITION: {
    categoryCode: 'SUBJECT_COMPETITION',
    title: '学科竞赛服务',
    subtitle: '竞赛报名、立项与指导流程管理',
    intro: '用于学科竞赛报名、团队申报和指导教师审核，帮助学生完成竞赛全流程记录。',
    highlightLabel: '成长发展',
    accentClass: 'is-growth',
    emptyHint: '还没有学科竞赛申请记录。',
    primaryActionLabel: '发起竞赛申请',
    guideSteps: [
      { title: '填写竞赛信息', desc: '说明竞赛名称、级别、时间和团队信息。' },
      { title: '教师确认', desc: '指导教师在线审核队伍和报名信息。' },
      { title: '留存过程', desc: '竞赛申请记录与审批意见可随时回看。' },
    ],
  },
  INNOVATION_ENTREPRENEURSHIP: {
    categoryCode: 'INNOVATION_ENTREPRENEURSHIP',
    title: '创新创业服务',
    subtitle: '创新创业项目申报与过程跟踪',
    intro: '围绕大学生创新创业项目管理，支持项目申报、指导教师审核和经费需求说明。',
    highlightLabel: '成长发展',
    accentClass: 'is-growth',
    emptyHint: '还没有创新创业项目申请记录。',
    primaryActionLabel: '发起项目申报',
    guideSteps: [
      { title: '明确项目方向', desc: '填写项目名称、项目类型与创新点说明。' },
      { title: '教师与辅导员审核', desc: '系统自动流转给指导教师和辅导员。' },
      { title: '保留项目档案', desc: '申报数据可作为后续项目管理的基础记录。' },
    ],
  },
  COMPREHENSIVE_EVALUATION: {
    categoryCode: 'COMPREHENSIVE_EVALUATION',
    title: '综测测评服务',
    subtitle: '综测申报、佐证提交与结果追踪',
    intro: '用于学生综合测评成果申报，支持志愿时长、竞赛成果和补充说明提交。',
    highlightLabel: '成长发展',
    accentClass: 'is-growth',
    emptyHint: '还没有综测测评申报记录。',
    primaryActionLabel: '发起综测申报',
    guideSteps: [
      { title: '准备成果材料', desc: '整理志愿、竞赛、德育等成果材料。' },
      { title: '提交综测申请', desc: '按学期填写综测信息并提交。' },
      { title: '等待审核确认', desc: '辅导员审核后结果会同步显示。' },
    ],
  },
  TEXTBOOK: {
    categoryCode: 'TEXTBOOK',
    title: '教材管理服务',
    subtitle: '教材领用、补领与征订办理',
    intro: '支持教材领用、补领和征订申请，方便学生在线办理教材相关业务。',
    highlightLabel: '教务服务',
    accentClass: 'is-academic',
    emptyHint: '还没有教材业务申请记录。',
    primaryActionLabel: '发起教材申请',
    guideSteps: [
      { title: '选择办理类型', desc: '根据需要选择教材领用、补领或征订。' },
      { title: '填写课程信息', desc: '补充教材对应课程、数量等必要信息。' },
      { title: '查看办理结果', desc: '审核通过后可在记录中查看办理结果。' },
    ],
  },
  ACADEMIC_STATUS: {
    categoryCode: 'ACADEMIC_STATUS',
    title: '学籍异动服务',
    subtitle: '休学、复学、转专业等学籍业务线上办理',
    intro: '适用于休学、复学、转专业等学籍异动业务，系统支持线上办理和台账留存。',
    highlightLabel: '教务服务',
    accentClass: 'is-academic',
    emptyHint: '还没有学籍异动申请记录。',
    primaryActionLabel: '发起学籍异动',
    guideSteps: [
      { title: '选择异动事项', desc: '按业务模板发起休学、复学、转专业等申请。' },
      { title: '填写异动明细', desc: '按模板要求补充生效日期、原因等关键数据。' },
      { title: '状态联动更新', desc: '审批通过后系统会同步更新当前学籍状态并写入台账。' },
    ],
  },
}

export function resolveAffairPresentation(categoryCode?: string): AffairPresentation | null {
  if (!categoryCode) return null
  return affairPresentationMap[categoryCode] || null
}
