package com.smart.web.controller.campus;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.domain.entity.SysUser;
import com.smart.common.core.domain.model.LoginUser;
import com.smart.common.utils.DateUtils;
import com.smart.common.utils.SecurityUtils;
import com.smart.common.utils.StringUtils;
import com.smart.framework.web.service.TokenService;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.domain.ScClass;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScCourse;
import com.smart.system.domain.ScCourseSchedule;
import com.smart.system.domain.ScExamPaper;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScKnowledgePoint;
import com.smart.system.domain.ScQuestionBank;
import com.smart.system.domain.ScSchoolTerm;
import com.smart.system.domain.ScUserAchievement;
import com.smart.system.domain.ScLearningTaskSubmission;
import com.smart.system.domain.ScWrongQuestionBook;
import com.smart.system.domain.ScParentStudentRel;
import com.smart.system.domain.SysNotice;
import com.smart.system.service.ICampusOverviewService;
import com.smart.system.service.IScClassCourseService;
import com.smart.system.service.IScClassService;
import com.smart.system.service.IScCourseScheduleService;
import com.smart.system.service.IScCourseService;
import com.smart.system.service.IScExamPaperService;
import com.smart.system.service.IScExamRecordService;
import com.smart.system.service.IScKnowledgePointService;
import com.smart.system.service.IScLearningTaskService;
import com.smart.system.service.IScLearningTaskDispatchService;
import com.smart.system.service.IScLearningTaskSubmissionService;
import com.smart.system.service.IScParentStudentRelService;
import com.smart.system.service.IScQuestionBankService;
import com.smart.system.service.IScSchoolTermService;
import com.smart.system.service.IScUserGrowthService;
import com.smart.system.service.IScUserProfileService;
import com.smart.system.service.IScWrongQuestionBookService;
import com.smart.system.service.ISysConfigService;
import com.smart.system.service.ISysNoticeService;
import com.smart.system.service.ISysUserService;

/**
 * 学生 / 教师 / 家长端聚合接口
 *
 * @author can
 */
@RestController
@RequestMapping("/campus/portal")
public class CampusPortalController extends BaseController {
    @Autowired
    private ICampusOverviewService campusOverviewService;

    @Autowired
    private ISysNoticeService noticeService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IScUserProfileService scUserProfileService;

    @Autowired
    private IScClassService scClassService;

    @Autowired
    private IScCourseService scCourseService;

    @Autowired
    private IScClassCourseService scClassCourseService;

    @Autowired
    private IScCourseScheduleService scCourseScheduleService;

    @Autowired
    private IScSchoolTermService scSchoolTermService;

    @Autowired
    private IScParentStudentRelService scParentStudentRelService;

    @Autowired
    private IScKnowledgePointService scKnowledgePointService;

    @Autowired
    private IScLearningTaskService scLearningTaskService;

    @Autowired
    private IScLearningTaskDispatchService scLearningTaskDispatchService;

    @Autowired
    private IScLearningTaskSubmissionService scLearningTaskSubmissionService;

    @Autowired
    private IScQuestionBankService scQuestionBankService;

    @Autowired
    private IScExamPaperService scExamPaperService;

    @Autowired
    private IScExamRecordService scExamRecordService;

    @Autowired
    private IScWrongQuestionBookService scWrongQuestionBookService;

    @Autowired
    private IScUserGrowthService scUserGrowthService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ObjectMapper objectMapper;

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/dashboard")
    public AjaxResult studentDashboard(@RequestParam Long userId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer recommendLimit) {
        return success(campusOverviewService.getStudentDashboard(userId, courseId, recommendLimit));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/my-courses")
    public AjaxResult studentMyCourses(@RequestParam Long userId, @RequestParam(required = false) Long termId) {
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        if (profile == null || profile.getClassId() == null) {
            return success(Collections.emptyList());
        }

        ScClassCourse query = new ScClassCourse();
        query.setClassId(profile.getClassId());
        query.setTermId(termId);
        query.setStatus("0");
        List<ScClassCourse> classCourses = scClassCourseService.selectScClassCourseList(query);
        Map<Long, List<ScCourseSchedule>> scheduleMap = loadScheduleMap(classCourses);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ScClassCourse classCourse : classCourses) {
            ScCourse course = classCourse.getCourseId() == null ? null
                    : scCourseService.selectScCourseByCourseId(classCourse.getCourseId());
            ScSchoolTerm term = classCourse.getTermId() == null ? null
                    : scSchoolTermService.selectScSchoolTermByTermId(classCourse.getTermId());
            List<ScCourseSchedule> schedules = scheduleMap.getOrDefault(classCourse.getId(), Collections.emptyList());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", classCourse.getId());
            item.put("classId", classCourse.getClassId());
            item.put("className", classCourse.getClassName());
            item.put("courseId", classCourse.getCourseId());
            item.put("courseName", classCourse.getCourseName());
            item.put("courseCode", course == null ? null : course.getCourseCode());
            item.put("subjectType", course == null ? null : course.getSubjectType());
            item.put("intro", course == null ? null : course.getIntro());
            item.put("teacherId", classCourse.getTeacherId());
            item.put("termId", classCourse.getTermId());
            item.put("termName", classCourse.getTermName());
            item.put("schoolYear", term == null ? null : term.getSchoolYear());
            item.put("businessType", classCourse.getBusinessType());
            item.put("teachingClassCode", classCourse.getTeachingClassCode());
            item.put("credits",
                    classCourse.getCredits() == null ? estimateCredits(course, classCourse) : classCourse.getCredits());
            item.put("courseCategory", classCourse.getCourseCategory());
            item.put("openDeptId", classCourse.getOpenDeptId());
            item.put("openDeptName", classCourse.getOpenDeptName());
            item.put("major", classCourse.getMajor());
            item.put("campusName", classCourse.getCampusName());
            item.put("assessmentType", classCourse.getAssessmentType());
            item.put("teachingLanguage", classCourse.getTeachingLanguage());
            item.put("prerequisiteCourse", classCourse.getPrerequisiteCourse());
            item.put("taskType", classCourse.getTaskType());
            item.put("requiredFlag", classCourse.getRequiredFlag());
            item.put("courseLevelRequirement", classCourse.getCourseLevelRequirement());
            item.put("totalHours", classCourse.getTotalHours());
            item.put("requiredWeeks", classCourse.getRequiredWeeks());
            item.put("weeklyHours", classCourse.getWeeklyHours());
            item.put("arrangedHours", classCourse.getArrangedHours());
            item.put("studentLimit", classCourse.getStudentLimit());
            item.put("actualStudentCount", classCourse.getActualStudentCount());
            item.put("remark", classCourse.getRemark());
            item.put("semesterLabel", term == null ? classCourse.getTermName() : buildTermLabel(term));
            item.put("scheduleText", buildScheduleTextVm(classCourse, schedules));
            item.put("scheduleDetails", buildCourseScheduleDetails(classCourse, schedules, term));
            result.add(item);
        }
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/course-detail")
    public AjaxResult studentCourseDetail(@RequestParam Long userId, @RequestParam Long classCourseId) {
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        if (profile == null || profile.getClassId() == null) {
            return success(Collections.emptyMap());
        }
        ScClassCourse classCourse = scClassCourseService.selectScClassCourseById(classCourseId);
        if (classCourse == null || !Objects.equals(classCourse.getClassId(), profile.getClassId())) {
            return success(Collections.emptyMap());
        }

        Map<Long, List<ScCourseSchedule>> scheduleMap = loadScheduleMap(Collections.singletonList(classCourse));
        ScSchoolTerm term = classCourse.getTermId() == null ? null
                : scSchoolTermService.selectScSchoolTermByTermId(classCourse.getTermId());
        Map<String, Object> currentCourse = buildStudentCourseItem(classCourse, term, scheduleMap.getOrDefault(classCourse.getId(), Collections.emptyList()));

        ScExamPaper paperQuery = new ScExamPaper();
        paperQuery.setCourseId(classCourse.getCourseId());
        paperQuery.setStatus("0");
        List<ScExamPaper> papers = scExamPaperService.selectScExamPaperList(paperQuery).stream()
                .filter(item -> !"SUB".equalsIgnoreCase(item.getPaperLevel()))
                .collect(Collectors.toList());
        Set<Long> examCourseIds = papers.stream()
                .map(ScExamPaper::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        ScQuestionBank questionQuery = new ScQuestionBank();
        questionQuery.setCourseId(classCourse.getCourseId());
        questionQuery.setStatus("0");
        List<ScQuestionBank> questions = scQuestionBankService.selectScQuestionBankList(questionQuery);

        ScExamRecord recordQuery = new ScExamRecord();
        recordQuery.setUserId(userId);
        List<ScExamRecord> allRecords = scExamRecordService.selectScExamRecordList(recordQuery);
        Set<Long> coursePaperIds = papers.stream().map(ScExamPaper::getPaperId).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<ScExamRecord> records = allRecords.stream()
                .filter(item -> item.getPaperId() != null && coursePaperIds.contains(item.getPaperId()))
                .collect(Collectors.toList());

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("paperCount", papers.size());
        stats.put("questionCount", questions.size());
        stats.put("recordCount", records.size());
        stats.put("avgScore", averageScore(records));
        stats.put("submittedCount", records.stream().filter(item -> "SUBMITTED".equals(item.getExamStatus())).count());
        stats.put("ongoingCount", records.stream().filter(item -> "ONGOING".equals(item.getExamStatus())).count());
        stats.put("questionTypeCount", questions.stream().map(ScQuestionBank::getQuestionType).filter(StringUtils::isNotEmpty).distinct().count());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("course", currentCourse);
        result.put("papers", papers);
        result.put("questions", questions.stream().limit(20).map(this::maskCourseQuestion).collect(Collectors.toList()));
        result.put("records", records.stream().limit(20).collect(Collectors.toList()));
        result.put("stats", stats);
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/tasks")
    public AjaxResult studentTasks(@RequestParam Long userId, @RequestParam(required = false) Long termId) {
        List<Map<String, Object>> courseList = castToMapList(studentMyCourses(userId, termId).get("data"));

        ScExamPaper paperQuery = new ScExamPaper();
        paperQuery.setStatus("0");
        List<ScExamPaper> papers = scExamPaperService.selectScExamPaperList(paperQuery).stream()
                .filter(item -> !"SUB".equalsIgnoreCase(item.getPaperLevel()))
                .collect(Collectors.toList());
        Set<Long> examCourseIds = papers.stream()
                .map(ScExamPaper::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        ScExamRecord recordQuery = new ScExamRecord();
        recordQuery.setUserId(userId);
        List<ScExamRecord> records = scExamRecordService.selectScExamRecordList(recordQuery);

        ScWrongQuestionBook wrongQuery = new ScWrongQuestionBook();
        wrongQuery.setUserId(userId);
        List<ScWrongQuestionBook> wrongs = scWrongQuestionBookService.selectScWrongQuestionBookList(wrongQuery);

        List<Map<String, Object>> examTasks = papers.stream()
                .limit(10)
                .map(item -> taskItem("exam-" + item.getPaperId(),
                        StringUtils.defaultIfEmpty(item.getPaperName(), "试卷 " + item.getPaperId()),
                        item.getCourseId() == null ? "开放考试任务，可自由参与。" : "课程考试任务，建议按课程进度完成。",
                        "考试任务", "EXAM", "primary", "待处理",
                        Arrays.asList((item.getDurationMinutes() == null ? 0 : item.getDurationMinutes()) + " 分钟",
                                "总分 " + StringUtils.defaultIfEmpty(item.getTotalScore() == null ? null : item.getTotalScore().toPlainString(), "0")),
                        actionItem("exam", item.getPaperId(), null, rowMap(
                                "paperId", item.getPaperId(),
                                "paperName", item.getPaperName(),
                                "courseId", item.getCourseId()), "/student/exams")))
                .collect(Collectors.toList());

        List<Map<String, Object>> wrongTasks = wrongs.stream()
                .limit(6)
                .map(item -> taskItem("wrong-" + item.getId(),
                        "错题回练 · 题目 " + item.getQuestionId(),
                        "建议优先复盘高频错题，再进行错题回练。",
                        "错题任务", "WRONG_BOOK", "warning", "1".equals(item.getMasteryStatus()) ? "已掌握" : "待巩固",
                        Arrays.asList("错误 " + defaultInt(item.getWrongCount()) + " 次",
                                item.getCourseId() == null ? "通用题目" : "课程 " + item.getCourseId()),
                        actionItem("wrongbook", item.getId(), null, rowMap(
                                "wrongId", item.getId(),
                                "questionId", item.getQuestionId(),
                                "courseId", item.getCourseId()), "/student/wrongbook")))
                .collect(Collectors.<Map<String, Object>>toList());

        List<Map<String, Object>> courseTasks = courseList.stream()
                .filter(item -> {
                    Long courseId = asLong(item.get("courseId"));
                    return courseId != null && examCourseIds.contains(courseId);
                })
                .limit(8)
                .map(item -> taskItem("course-" + item.get("id"),
                        String.valueOf(item.get("courseName")) + " · 课程考试跟进",
                        "该课程已有考试相关数据，进入课程详情查看考试与统计概况。",
                        resolveCourseTaskLabel((String) item.get("taskType")), resolveCourseTaskCode((String) item.get("taskType")), "success", "进行中",
                        Arrays.asList(StringUtils.defaultIfEmpty((String) item.get("courseCode"), "无课程编码"),
                                StringUtils.defaultIfEmpty((String) item.get("termName"), "当前学期")),
                        actionItem("course", asLong(item.get("id")), null, rowMap(
                                "classCourseId", asLong(item.get("id")),
                                "courseId", asLong(item.get("courseId")),
                                "courseName", item.get("courseName"),
                                "taskType", item.get("taskType")), "/student/courses")))
                .collect(Collectors.toList());

        List<Map<String, Object>> manualTasks = scLearningTaskService.selectUserDispatchedTasks(userId, termId).stream()
                .map(this::manualTaskItem)
                .collect(Collectors.toList());

        List<Map<String, Object>> todoTasks = new ArrayList<>();
        todoTasks.addAll(manualTasks.stream()
                .filter(item -> !"COMPLETED".equals(String.valueOf(item.get("taskTypeCode"))))
                .limit(4)
                .collect(Collectors.toList()));
        records.stream()
                .filter(item -> "ONGOING".equals(item.getExamStatus()))
                .findFirst()
                .ifPresent(item -> todoTasks.add(taskItem("ongoing-" + item.getRecordId(),
                        StringUtils.defaultIfEmpty(item.getPaperName(), "试卷 " + item.getPaperId()),
                        "你有一场未完成考试，建议优先继续作答。",
                        "待办考试", "EXAM_RESUME", "danger", "进行中",
                        Collections.singletonList("开始于 " + StringUtils.defaultIfEmpty(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", item.getStartTime()), "-")),
                        actionItem("resume", item.getPaperId(), item.getRecordId(), rowMap(
                                "recordId", item.getRecordId(),
                                "paperId", item.getPaperId(),
                                "paperName", item.getPaperName(),
                                "startTime", item.getStartTime()), "/student/exams/session/" + item.getRecordId()))));
        todoTasks.addAll(wrongTasks.stream().limit(2).collect(Collectors.toList()));
        todoTasks.addAll(courseTasks.stream().limit(2).collect(Collectors.toList()));

        List<Map<String, Object>> recommendedTasks = new ArrayList<>();
        recommendedTasks.addAll(manualTasks.stream()
                .filter(item -> !"COMPLETED".equals(String.valueOf(item.get("taskTypeCode"))))
                .limit(2)
                .collect(Collectors.toList()));
        recommendedTasks.addAll(examTasks.stream().limit(2).collect(Collectors.toList()));
        List<Map<String, Object>> topWrongTasks = wrongs.stream().limit(2).map(item -> taskItem("topwrong-" + item.getQuestionId(),
                "高频错题 · " + item.getQuestionId(),
                "这道题在你的历史作答里属于高频失分，建议尽快回练。",
                "薄弱项", "WRONG_BOOK", "warning", "推荐",
                Collections.singletonList("错误 " + defaultInt(item.getWrongCount()) + " 次"),
                actionItem("wrongbook", item.getId(), null, rowMap(
                        "wrongId", item.getId(),
                        "questionId", item.getQuestionId(),
                        "courseId", item.getCourseId()), "/student/wrongbook"))).collect(Collectors.<Map<String, Object>>toList());
        recommendedTasks.addAll(topWrongTasks);
        recommendedTasks.addAll(courseTasks.stream().skip(2).limit(2).collect(Collectors.toList()));

        List<Map<String, Object>> historyTasks = records.stream()
                .filter(item -> "SUBMITTED".equals(item.getExamStatus()))
                .limit(10)
                .map(item -> taskItem("history-" + item.getRecordId(),
                        StringUtils.defaultIfEmpty(item.getPaperName(), "试卷 " + item.getPaperId()),
                        "历史考试记录，可回看作答详情与结果。",
                        "已完成", "EXAM_RECORD", "info", "已提交",
                        Collections.emptyList(),
                        actionItem("record", item.getPaperId(), item.getRecordId(), rowMap(
                                "recordId", item.getRecordId(),
                                "paperId", item.getPaperId(),
                                "paperName", item.getPaperName()), "/student/exams?tab=records")))
                .collect(Collectors.toList());
        historyTasks.addAll(manualTasks.stream()
                .filter(item -> "COMPLETED".equals(String.valueOf(item.get("taskTypeCode"))))
                .collect(Collectors.toList()));

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("examTaskCount", examTasks.size());
        stats.put("wrongTaskCount", wrongTasks.size());
        stats.put("courseTaskCount", courseTasks.size());
        stats.put("todoCount", todoTasks.size());
        stats.put("recommendedCount", recommendedTasks.size());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todoTasks", todoTasks);
        result.put("recommendedTasks", recommendedTasks);
        result.put("historyTasks", historyTasks);
        result.put("examTasks", examTasks);
        result.put("wrongTasks", wrongTasks);
        result.put("courseTasks", courseTasks);
        result.put("manualTasks", manualTasks);
        result.put("stats", stats);
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/growth-summary")
    public AjaxResult studentGrowthSummary(@RequestParam Long userId) {
        return success(scUserGrowthService.buildGrowthSummary(userId));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PutMapping("/student/task-dispatch/{dispatchId}/read")
    public AjaxResult markStudentTaskRead(@PathVariable Long dispatchId) {
        LoginUser loginUser = getLoginUser();
        return toAjax(scLearningTaskDispatchService.markDispatchRead(dispatchId, loginUser.getUserId(), getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PutMapping("/student/task-dispatch/{dispatchId}/complete")
    public AjaxResult completeStudentTask(@PathVariable Long dispatchId, @RequestBody(required = false) Map<String, String> payload) {
        LoginUser loginUser = getLoginUser();
        String completionRemark = payload == null ? null : payload.get("completionRemark");
        return toAjax(scLearningTaskDispatchService.completeDispatch(dispatchId, loginUser.getUserId(), completionRemark, getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/task-dispatch/{dispatchId}/detail")
    public AjaxResult studentTaskDispatchDetail(@PathVariable Long dispatchId) {
        LoginUser loginUser = getLoginUser();
        List<Map<String, Object>> tasks = scLearningTaskService.selectUserDispatchedTasks(loginUser.getUserId(), null);
        Map<String, Object> detail = tasks.stream()
                .filter(item -> Objects.equals(asLong(item.get("dispatchId")), dispatchId))
                .findFirst()
                .orElse(Collections.emptyMap());
        if (detail.isEmpty()) {
            return error("任务不存在或无权查看");
        }
        Map<String, Object> result = new LinkedHashMap<>(detail);
        result.put("submission", scLearningTaskSubmissionService.selectByDispatchId(dispatchId));
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PutMapping("/student/task-dispatch/{dispatchId}/submit")
    public AjaxResult submitStudentTask(@PathVariable Long dispatchId, @RequestBody Map<String, Object> payload) {
        LoginUser loginUser = getLoginUser();
        List<Map<String, Object>> tasks = scLearningTaskService.selectUserDispatchedTasks(loginUser.getUserId(), null);
        Map<String, Object> detail = tasks.stream()
                .filter(item -> Objects.equals(asLong(item.get("dispatchId")), dispatchId))
                .findFirst()
                .orElse(Collections.emptyMap());
        if (detail.isEmpty()) {
            return error("任务不存在或无权提交");
        }
        String submitContent = payload == null ? null : String.valueOf(payload.getOrDefault("submitContent", ""));
        String attachmentUrls = payload == null ? "[]" : String.valueOf(payload.getOrDefault("attachmentUrls", "[]"));
        if (StringUtils.isEmpty(submitContent) && StringUtils.equals(attachmentUrls, "[]")) {
            return error("提交内容不能为空");
        }
        Long taskId = asLong(detail.get("taskId"));
        SysUser currentUser = userService.selectUserById(loginUser.getUserId());
        ScLearningTaskSubmission submission = scLearningTaskSubmissionService.selectByDispatchId(dispatchId);
        if (submission == null) {
            submission = new com.smart.system.domain.ScLearningTaskSubmission();
            submission.setDispatchId(dispatchId);
            submission.setTaskId(taskId);
            submission.setUserId(loginUser.getUserId());
            submission.setSubmitContent(submitContent);
            submission.setAttachmentUrls(attachmentUrls);
            submission.setSubmitTime(new Date());
            submission.setReviewStatus("PENDING");
            submission.setCreateBy(getUsername());
            scLearningTaskSubmissionService.insertScLearningTaskSubmission(submission);
        } else {
            submission.setSubmitContent(submitContent);
            submission.setAttachmentUrls(attachmentUrls);
            submission.setSubmitTime(new Date());
            submission.setReviewStatus("PENDING");
            submission.setUpdateBy(getUsername());
            scLearningTaskSubmissionService.updateScLearningTaskSubmission(submission);
        }
        String completionRemark = StringUtils.defaultIfEmpty(currentUser == null ? null : currentUser.getRealName(), "学生") + "已提交任务";
        scLearningTaskDispatchService.completeDispatch(dispatchId, loginUser.getUserId(), completionRemark, getUsername());
        return success("提交成功");
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/messages")
    public AjaxResult studentMessages(@RequestParam Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "12") Integer limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        String receiverScope = "STUDENT";

        List<SysNotice> notices = noticeService.selectPortalNoticeList(userId, receiverScope, "MESSAGE", "0", keyword, limit);
        for (SysNotice notice : notices) {
            result.add(toPortalNoticeItem(notice, "MESSAGE"));
        }

        ScExamRecord recordQuery = new ScExamRecord();
        recordQuery.setUserId(userId);
        List<ScExamRecord> records = scExamRecordService.selectScExamRecordList(recordQuery);
        records.stream()
                .filter(item -> "ONGOING".equals(item.getExamStatus()))
                .limit(2)
                .forEach(item -> {
                    Map<String, Object> message = new LinkedHashMap<>();
                    message.put("messageId", "exam-ongoing-" + item.getRecordId());
                    message.put("messageType", "EXAM_REMINDER");
                    message.put("messageTitle", "考试进行中提醒");
                    message.put("messageContent", "你有一场未完成考试：" + StringUtils.defaultIfEmpty(item.getPaperName(), "试卷 " + item.getPaperId()));
                    message.put("messageSummary", "未完成考试待继续作答");
                    message.put("createTime", item.getStartTime());
                    message.put("levelType", "WARNING");
                    message.put("actionType", "resumeExam");
                    message.put("actionTarget", item.getRecordId());
                    message.put("bizCategory", "MESSAGE");
                    message.put("readFlag", "0");
                    message.put("paperId", item.getPaperId());
                    result.add(message);
                });

        Map<String, Object> growth = scUserGrowthService.buildGrowthSummary(userId);
        @SuppressWarnings("unchecked")
        List<ScUserAchievement> achievements = (List<ScUserAchievement>) growth.getOrDefault("achievements", Collections.emptyList());
        achievements.stream().limit(3).forEach(item -> {
            Map<String, Object> message = new LinkedHashMap<>();
            message.put("messageId", "achievement-" + item.getAchievementId());
            message.put("messageType", "ACHIEVEMENT");
            message.put("messageTitle", StringUtils.defaultIfEmpty(item.getAchievementTitle(), "成就解锁"));
            message.put("messageContent", StringUtils.defaultIfEmpty(item.getAchievementDesc(), "你已解锁新的成长成就"));
            message.put("messageSummary", StringUtils.defaultIfEmpty(item.getAchievementDesc(), "你已解锁新的成长成就"));
            message.put("createTime", item.getEarnedTime());
            message.put("levelType", "SUCCESS");
            message.put("actionType", "growth");
            message.put("actionTarget", item.getAchievementId());
            message.put("bizCategory", "MESSAGE");
            message.put("readFlag", "0");
            result.add(message);
        });

        result.sort(Comparator.comparing(item -> parseAnyDate(item.get("createTime")), Comparator.nullsLast(Comparator.reverseOrder())));
        return success(result.stream().limit(12).collect(Collectors.toList()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/message-center")
    public AjaxResult studentMessageCenter(@RequestParam Long userId,
            @RequestParam(required = false) String keyword) {
        String receiverScope = "STUDENT";
        List<SysNotice> unreadMessages = noticeService.selectPortalNoticeList(userId, receiverScope, "MESSAGE", null, keyword, 100);
        List<SysNotice> notices = noticeService.selectPortalNoticeList(userId, receiverScope, "NOTICE", null, keyword, 8);
        Map<String, Object> stats = noticeService.selectPortalNoticeStats(userId, receiverScope);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("messages", unreadMessages.stream().map(item -> toPortalNoticeItem(item, "MESSAGE")).collect(Collectors.toList()));
        result.put("notices", notices.stream().map(item -> toPortalNoticeItem(item, "NOTICE")).collect(Collectors.toList()));
        result.put("stats", stats == null ? Collections.emptyMap() : stats);
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PutMapping("/student/message-center/{noticeId}/read")
    public AjaxResult markStudentMessageRead(@PathVariable Long noticeId) {
        LoginUser loginUser = getLoginUser();
        return toAjax(noticeService.markNoticeRead(noticeId, loginUser.getUserId(), getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/my-schedule")
    public AjaxResult studentMySchedule(@RequestParam Long userId, @RequestParam(required = false) Long termId) {
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        if (profile == null || profile.getClassId() == null) {
            return success(Collections.emptyMap());
        }

        ScClassCourse classCourseQuery = new ScClassCourse();
        classCourseQuery.setClassId(profile.getClassId());
        classCourseQuery.setTermId(termId);
        classCourseQuery.setStatus("0");
        List<ScClassCourse> classCourses = scClassCourseService.selectScClassCourseList(classCourseQuery);
        ScSchoolTerm currentTerm = resolveTerm(termId, classCourses);
        Map<String, Object> result = buildSchedulePayload(classCourses, currentTerm);
        result.put("classId", profile.getClassId());
        result.put("gradeId", profile.getGradeId());
        result.put("className", resolveClassName(profile.getClassId()));
        result.put("studentUserId", userId);
        result.put("studentName", resolveProfileName(userId, profile));
        result.put("studentNo", resolveProfileStudentNo(userId, profile));
        result.put("major", profile.getMajor());
        result.put("admissionYear", profile.getAdmissionYear());
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/knowledge-points")
    public AjaxResult studentKnowledgePoints(@RequestParam Long userId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false, defaultValue = "200") Integer limit) {
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        if (profile == null || profile.getClassId() == null) {
            return success(Collections.emptyList());
        }
        ScClassCourse query = new ScClassCourse();
        query.setClassId(profile.getClassId());
        query.setStatus("0");
        List<ScClassCourse> classCourses = scClassCourseService.selectScClassCourseList(query);
        Set<Long> allowedCourseIds = classCourses.stream()
                .map(ScClassCourse::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (courseId != null) {
            if (!allowedCourseIds.contains(courseId)) {
                return success(Collections.emptyList());
            }
            allowedCourseIds = new LinkedHashSet<>(Collections.singleton(courseId));
        }
        if (allowedCourseIds.isEmpty()) {
            return success(Collections.emptyList());
        }
        int safeLimit = limit == null || limit <= 0 ? 200 : Math.min(limit, 500);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Long itemCourseId : allowedCourseIds) {
            ScKnowledgePoint kpQuery = new ScKnowledgePoint();
            kpQuery.setCourseId(itemCourseId);
            kpQuery.setStatus("0");
            List<ScKnowledgePoint> points = scKnowledgePointService.selectScKnowledgePointList(kpQuery);
            for (ScKnowledgePoint point : points) {
                if (result.size() >= safeLimit) {
                    return success(result);
                }
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("label", point.getKnowledgeName() + "（" + point.getKnowledgePointId() + "）");
                item.put("value", point.getKnowledgePointId());
                item.put("knowledgePointId", point.getKnowledgePointId());
                item.put("knowledgeName", point.getKnowledgeName());
                item.put("courseId", point.getCourseId());
                item.put("difficultyLevel", point.getDifficultyLevel());
                item.put("keyword", point.getKeyword());
                item.put("description", point.getDescription());
                result.add(item);
            }
        }
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/term/options")
    public AjaxResult portalTermOptions() {
        ScSchoolTerm query = new ScSchoolTerm();
        query.setStatus("0");
        List<ScSchoolTerm> termList = scSchoolTermService.selectScSchoolTermList(query);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ScSchoolTerm term : termList) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("label", term.getTermName() + "（" + term.getSchoolYear() + "）");
            item.put("value", term.getTermId());
            item.put("termName", term.getTermName());
            item.put("schoolYear", term.getSchoolYear());
            item.put("isCurrent", term.getIsCurrent());
            item.put("startDate", term.getStartDate());
            item.put("endDate", term.getEndDate());
            item.put("totalWeeks", term.getTotalWeeks());
            result.add(item);
        }
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @GetMapping("/teacher/dashboard")
    public AjaxResult teacherDashboard(@RequestParam Long teacherId) {
        return success(campusOverviewService.getTeacherDashboard(teacherId));
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @GetMapping("/teacher/my-courses")
    public AjaxResult teacherMyCourses(@RequestParam Long teacherId, @RequestParam(required = false) Long termId) {
        ScClassCourse query = new ScClassCourse();
        query.setTeacherId(teacherId);
        query.setTermId(termId);
        query.setStatus("0");
        List<ScClassCourse> classCourses = scClassCourseService.selectScClassCourseList(query);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ScClassCourse classCourse : classCourses) {
            ScCourse course = classCourse.getCourseId() == null ? null
                    : scCourseService.selectScCourseByCourseId(classCourse.getCourseId());
            ScSchoolTerm term = classCourse.getTermId() == null ? null
                    : scSchoolTermService.selectScSchoolTermByTermId(classCourse.getTermId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", classCourse.getId());
            item.put("classId", classCourse.getClassId());
            item.put("className", classCourse.getClassName());
            item.put("courseId", classCourse.getCourseId());
            item.put("courseName", classCourse.getCourseName());
            item.put("courseCode", course == null ? null : course.getCourseCode());
            item.put("subjectType", course == null ? null : course.getSubjectType());
            item.put("intro", course == null ? null : course.getIntro());
            item.put("teacherId", classCourse.getTeacherId());
            item.put("teacherName", StringUtils.defaultIfEmpty(classCourse.getTeacherName(),
                    resolveTeacherName(classCourse.getTeacherId())));
            item.put("termId", classCourse.getTermId());
            item.put("termName", classCourse.getTermName());
            item.put("schoolYear", term == null ? null : term.getSchoolYear());
            item.put("weeklyHours", classCourse.getWeeklyHours());
            item.put("studentLimit", classCourse.getStudentLimit());
            item.put("actualStudentCount", classCourse.getActualStudentCount());
            item.put("remark", classCourse.getRemark());
            result.add(item);
        }
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @GetMapping("/teacher/my-schedule")
    public AjaxResult teacherMySchedule(@RequestParam Long teacherId, @RequestParam(required = false) Long termId) {
        ScClassCourse classCourseQuery = new ScClassCourse();
        classCourseQuery.setTeacherId(teacherId);
        classCourseQuery.setTermId(termId);
        classCourseQuery.setStatus("0");
        List<ScClassCourse> classCourses = scClassCourseService.selectScClassCourseList(classCourseQuery);
        ScSchoolTerm currentTerm = resolveTerm(termId, classCourses);
        Map<String, Object> result = buildSchedulePayload(classCourses, currentTerm);
        result.put("teacherId", teacherId);
        result.put("teacherName", resolveTeacherName(teacherId));
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('parent,admin')")
    @GetMapping("/parent/dashboard")
    public AjaxResult parentDashboard(@RequestParam Long parentUserId,
            @RequestParam(required = false) Long studentUserId,
            @RequestParam(required = false) Long courseId) {
        return success(campusOverviewService.getParentDashboard(parentUserId, studentUserId, courseId));
    }

    @PreAuthorize("@ss.hasAnyRoles('parent,admin')")
    @GetMapping("/parent/child-courses")
    public AjaxResult parentChildCourses(@RequestParam Long parentUserId,
            @RequestParam(required = false) Long studentUserId,
            @RequestParam(required = false) Long termId) {
        Long targetStudentId = resolveParentStudentId(parentUserId, studentUserId);
        return studentMyCourses(targetStudentId, termId);
    }

    @PreAuthorize("@ss.hasAnyRoles('parent,admin')")
    @GetMapping("/parent/child-schedule")
    public AjaxResult parentChildSchedule(@RequestParam Long parentUserId,
            @RequestParam(required = false) Long studentUserId,
            @RequestParam(required = false) Long termId) {
        Long targetStudentId = resolveParentStudentId(parentUserId, studentUserId);
        return studentMySchedule(targetStudentId, termId);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/help")
    public AjaxResult helpCenter() {
        Map<String, Object> data = new LinkedHashMap<>();
        List<Map<String, Object>> sections = new ArrayList<>();
        sections.add(helpSection("快速开始", "登录、导航与学习入口",
                helpItem("如何开始使用学习门户？", "登录后可通过顶部菜单进入学习首页、资源中心、智能问答与考试模块。"),
                helpItem("菜单搜索有什么用？", "可直接搜索功能名称，点击结果即可跳转到对应页面。"),
                helpItem("主题色如何切换？", "点击右上角头像，在用户面板中选择校园蓝、珙桐绿、百子莲紫或万寿菊橙。")));
        sections.add(helpSection("智能问答", "问答工作台常见问题",
                helpItem("为什么模型列表为空？", "请确认后端 AI 模型配置已启用，并检查当前账号是否具备访问权限。"),
                helpItem("上传图片后无法提问怎么办？", "请切换到支持视觉能力的模型，或删除图片后仅文本提问。"),
                helpItem("实时输出中断后如何继续？", "停止生成后，输入框会自动带入继续追问提示，可直接继续发送。")));
        sections.add(helpSection("消息与支持", "通知公告与问题反馈",
                helpItem("消息提醒来自哪里？", "消息提醒会聚合系统通知公告，展示最近发布且状态正常的内容。"),
                helpItem("遇到页面异常怎么办？", "可优先刷新页面；若仍异常，请联系管理员并提供操作步骤与截图。")));
        data.put("sections", sections);
        data.put("contact", "如需进一步帮助，请联系学校信息化平台主管老师。");
        return success(data);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/notice")
    public AjaxResult noticeList(@RequestParam(required = false, defaultValue = "6") Integer limit,
            @RequestParam(required = false) String keyword) {
        LoginUser loginUser = getLoginUser();
        String receiverScope = resolvePortalReceiverScope(loginUser);
        List<SysNotice> notices = noticeService.selectPortalNoticeList(loginUser.getUserId(), receiverScope, "NOTICE", null, keyword, limit);
        return success(notices.stream().map(item -> toPortalNoticeItem(item, "NOTICE")).collect(Collectors.toList()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/notice/{noticeId}")
    public AjaxResult noticeDetail(@PathVariable Long noticeId) {
        LoginUser loginUser = getLoginUser();
        SysNotice notice = noticeService.selectNoticeById(noticeId);
        if (notice == null) {
            return error("通知公告不存在");
        }
        noticeService.markNoticeRead(noticeId, loginUser.getUserId(), getUsername());
        return success(toPortalNoticeItem(notice, StringUtils.defaultIfEmpty(notice.getBizCategory(), "NOTICE")));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/profile")
    public AjaxResult profile() {
        LoginUser loginUser = getLoginUser();
        SysUser user = userService.selectUserById(loginUser.getUserId());
        scUserProfileService.fillUserProfile(user);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("user", user);
        data.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
        data.put("postGroup", userService.selectUserPostGroup(loginUser.getUsername()));
        data.put("profile", scUserProfileService.selectScUserProfileByUserId(loginUser.getUserId()));
        return success(data);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @PutMapping("/profile")
    public AjaxResult updatePortalProfile(@RequestBody SysUser user) {
        LoginUser loginUser = getLoginUser();
        SysUser currentUser = userService.selectUserById(loginUser.getUserId());
        currentUser.setNickName(user.getNickName());
        currentUser.setRealName(StringUtils.isNotEmpty(user.getRealName()) ? user.getRealName() : user.getNickName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        currentUser.setLearningGoal(user.getLearningGoal());
        currentUser.setInterestTags(user.getInterestTags());
        currentUser.setLearningStyle(user.getLearningStyle());
        currentUser.setMajor(user.getMajor());
        currentUser.setAdmissionYear(user.getAdmissionYear());
        currentUser.setUpdateBy(getUsername());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser)) {
            return error("修改失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser)) {
            return error("修改失败，邮箱账号已存在");
        }
        if (userService.updateUserProfile(currentUser) <= 0) {
            return error("修改个人资料异常，请联系管理员");
        }

        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(currentUser.getUserId());
        if (profile == null) {
            profile = new ScUserProfile();
            profile.setUserId(currentUser.getUserId());
            profile.setCreateBy(getUsername());
        }
        profile.setRealName(currentUser.getRealName());
        profile.setGender(currentUser.getSex());
        profile.setMajor(currentUser.getMajor());
        profile.setAdmissionYear(currentUser.getAdmissionYear());
        profile.setLearningGoal(currentUser.getLearningGoal());
        profile.setInterestTags(currentUser.getInterestTags());
        profile.setLearningStyle(currentUser.getLearningStyle());
        profile.setUpdateBy(getUsername());
        scUserProfileService.updateScUserProfile(profile);

        SysUser latestUser = userService.selectUserById(currentUser.getUserId());
        scUserProfileService.fillUserProfile(latestUser);
        loginUser.setUser(latestUser);
        tokenService.setLoginUser(loginUser);
        return success("个人资料更新成功");
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @PutMapping("/profile/password")
    public AjaxResult updatePortalPassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        LoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        SysUser user = userService.selectUserById(userId);
        String password = user.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            return error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            return error("新密码不能与旧密码相同");
        }
        newPassword = SecurityUtils.encryptPassword(newPassword);
        if (userService.resetUserPwd(userId, newPassword) > 0) {
            loginUser.getUser().setPwdUpdateDate(DateUtils.getNowDate());
            loginUser.getUser().setPassword(newPassword);
            tokenService.setLoginUser(loginUser);
            return success("密码修改成功");
        }
        return error("修改密码异常，请联系管理员");
    }

    private Map<String, Object> helpSection(String title, String description, Map<String, Object>... items) {
        Map<String, Object> section = new LinkedHashMap<>();
        section.put("title", title);
        section.put("description", description);
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (Map<String, Object> item : items) {
            itemList.add(item);
        }
        section.put("items", itemList);
        return section;
    }

    private Map<String, Object> helpItem(String title, String content) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("title", title);
        item.put("content", content);
        return item;
    }

    private Long resolveParentStudentId(Long parentUserId, Long studentUserId) {
        if (studentUserId != null) {
            return studentUserId;
        }
        ScParentStudentRel query = new ScParentStudentRel();
        query.setParentUserId(parentUserId);
        List<ScParentStudentRel> relList = scParentStudentRelService.selectScParentStudentRelList(query);
        if (relList == null || relList.isEmpty()) {
            throw new IllegalArgumentException("当前家长未绑定学生");
        }
        return relList.get(0).getStudentUserId();
    }

    private String resolveTeacherName(Long teacherId) {
        if (teacherId == null) {
            return null;
        }
        SysUser teacher = userService.selectUserById(teacherId);
        if (teacher == null) {
            return null;
        }
        return StringUtils.isNotEmpty(teacher.getRealName()) ? teacher.getRealName() : teacher.getNickName();
    }

    private String resolveClassName(Long classId) {
        if (classId == null) {
            return null;
        }
        ScClass scClass = scClassService.selectScClassByClassId(classId);
        return scClass == null ? null : scClass.getClassName();
    }

    private String resolveProfileName(Long userId, ScUserProfile profile) {
        SysUser student = userService.selectUserById(userId);
        return StringUtils.defaultIfEmpty(profile.getRealName(), student == null ? null : student.getNickName());
    }

    private String resolveProfileStudentNo(Long userId, ScUserProfile profile) {
        SysUser student = userService.selectUserById(userId);
        return StringUtils.defaultIfEmpty(profile.getStudentNo(), student == null ? null : student.getUserName());
    }

    private Map<String, Object> buildSchedulePayload(List<ScClassCourse> classCourses, ScSchoolTerm currentTerm) {
        Map<Long, List<ScCourseSchedule>> scheduleMap = loadScheduleMap(classCourses);
        List<Map<String, Object>> activities = new ArrayList<>();
        List<Map<String, Object>> remarkLessons = new ArrayList<>();

        for (ScClassCourse classCourse : classCourses) {
            List<ScCourseSchedule> schedules = scheduleMap.getOrDefault(classCourse.getId(), Collections.emptyList());
            Map<String, Object> lessonVm = buildLessonVm(classCourse, schedules, currentTerm);
            if (Boolean.TRUE.equals(lessonVm.get("showInRemark"))) {
                remarkLessons.add(lessonVm);
            }
            for (ScCourseSchedule schedule : schedules) {
                activities.add(buildActivityVm(classCourse, schedule, currentTerm));
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("termId", currentTerm == null ? null : currentTerm.getTermId());
        result.put("currentWeek", resolveCurrentWeek(currentTerm));
        result.put("weekIndices", buildWeekIndices(currentTerm));
        result.put("classCourseCount", classCourses.size());
        result.put("activities", activities);
        result.put("remarkLessons", remarkLessons);
        result.put("timeTableLayout", buildDefaultTimeTableLayout());
        return result;
    }

    private Map<Long, List<ScCourseSchedule>> loadScheduleMap(List<ScClassCourse> classCourses) {
        Map<Long, List<ScCourseSchedule>> scheduleMap = new LinkedHashMap<>();
        for (ScClassCourse classCourse : classCourses) {
            ScCourseSchedule scheduleQuery = new ScCourseSchedule();
            scheduleQuery.setClassCourseId(classCourse.getId());
            scheduleQuery.setTermId(classCourse.getTermId());
            scheduleQuery.setStatus("0");
            scheduleMap.put(classCourse.getId(), scCourseScheduleService.selectScCourseScheduleList(scheduleQuery));
        }
        return scheduleMap;
    }

    private Map<String, Object> buildLessonVm(ScClassCourse classCourse, List<ScCourseSchedule> schedules,
            ScSchoolTerm currentTerm) {
        ScCourse course = classCourse.getCourseId() == null ? null
                : scCourseService.selectScCourseByCourseId(classCourse.getCourseId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", classCourse.getId());
        result.put("code", course == null ? null : course.getCourseCode());
        result.put("remark", classCourse.getRemark());
        result.put("courseId", classCourse.getCourseId());
        result.put("courseName", classCourse.getCourseName());
        result.put("classId", classCourse.getClassId());
        result.put("className", classCourse.getClassName());
        result.put("teacherId", classCourse.getTeacherId());
        result.put("teacherAssignmentString", StringUtils.defaultIfEmpty(classCourse.getTeacherName(),
                resolveTeacherName(classCourse.getTeacherId())));
        result.put("hasSchedule", !schedules.isEmpty());
        result.put("showInRemark", schedules.isEmpty() || StringUtils.isNotEmpty(classCourse.getRemark()));
        return result;
    }

    private Map<String, Object> buildActivityVm(ScClassCourse classCourse, ScCourseSchedule schedule,
            ScSchoolTerm currentTerm) {
        ScCourse course = classCourse.getCourseId() == null ? null
                : scCourseService.selectScCourseByCourseId(classCourse.getCourseId());
        String teacherName = StringUtils.defaultIfEmpty(classCourse.getTeacherName(),
                resolveTeacherName(classCourse.getTeacherId()));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("scheduleId", schedule.getScheduleId());
        result.put("classCourseId", classCourse.getId());
        result.put("courseCode", course == null ? null : course.getCourseCode());
        result.put("courseName", classCourse.getCourseName());
        result.put("courseId", classCourse.getCourseId());
        result.put("classId", classCourse.getClassId());
        result.put("className", classCourse.getClassName());
        result.put("teacherId", classCourse.getTeacherId());
        result.put("teacherName", teacherName);
        result.put("weeksText", schedule.getWeeksText());
        result.put("weeksStr", normalizeWeeksText(schedule.getWeeksText()));
        result.put("weekIndexes", extractWeekIndexes(schedule.getWeeksText(), schedule.getWeeksJson()));
        result.put("weekDay", schedule.getWeekDay());
        result.put("startSection", schedule.getStartSection());
        result.put("endSection", schedule.getEndSection());
        result.put("classroom", schedule.getClassroom());
        result.put("buildingName", schedule.getBuildingName());
        result.put("campus", schedule.getCampusName());
        result.put("startTime", formatSectionTime(schedule.getStartSection(), true));
        result.put("endTime", formatSectionTime(schedule.getEndSection(), false));
        result.put("termId", schedule.getTermId());
        result.put("termName", currentTerm == null ? classCourse.getTermName() : currentTerm.getTermName());
        result.put("remark", schedule.getRemark());
        result.put("courseType", course == null ? null : course.getSubjectType());
        result.put("credits", estimateCredits(course, classCourse));
        result.put("studentLimit", classCourse.getStudentLimit());
        result.put("studentCount", classCourse.getActualStudentCount() == null ? classCourse.getStudentLimit()
                : classCourse.getActualStudentCount());
        return result;
    }

    private Map<String, Object> buildCourseVm(ScCourse course) {
        if (course == null) {
            return null;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", course.getCourseId());
        result.put("nameZh", course.getCourseName());
        result.put("nameEn", course.getCourseName());
        result.put("code", course.getCourseCode());
        result.put("subjectType", course.getSubjectType());
        result.put("intro", course.getIntro());
        return result;
    }

    private Map<String, Object> buildSemesterVm(ScSchoolTerm term) {
        if (term == null) {
            return null;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", term.getTermId());
        result.put("nameZh", term.getTermName());
        result.put("nameEn", term.getTermName());
        result.put("code", term.getTermCode());
        result.put("schoolYear", term.getSchoolYear());
        result.put("startDate", term.getStartDate());
        result.put("endDate", term.getEndDate());
        result.put("enabled", "0".equals(term.getStatus()));
        result.put("countInTerm", true);
        result.put("weekStartOnSunday", false);
        return result;
    }

    private Map<String, Object> buildScheduleTextVm(ScClassCourse classCourse, List<ScCourseSchedule> schedules) {
        String teacherName = StringUtils.defaultIfEmpty(classCourse.getTeacherName(),
                resolveTeacherName(classCourse.getTeacherId()));
        Map<Integer, Map<String, Object>> unitMap = buildLayoutUnitMap();
        String dateTimeText = schedules.stream()
                .map(item -> String.format("%s周 %s %s", normalizeWeeksText(item.getWeeksText()),
                        resolveWeekdayLabel(item.getWeekDay()), buildSectionDisplayText(item, unitMap)))
                .collect(Collectors.joining("; \n"));
        String dateTimePlaceText = schedules.stream()
                .map(item -> String.format("%s周 %s %s %s", normalizeWeeksText(item.getWeeksText()),
                        resolveWeekdayLabel(item.getWeekDay()), buildSectionDisplayText(item, unitMap),
                        resolveScheduleLocation(item)))
                .collect(Collectors.joining("; \n"));
        String dateTimePlacePersonText = schedules.stream()
                .map(item -> String.format("%s周 %s %s %s %s", normalizeWeeksText(item.getWeeksText()),
                        resolveWeekdayLabel(item.getWeekDay()), buildSectionDisplayText(item, unitMap),
                        resolveScheduleLocation(item),
                        StringUtils.defaultIfEmpty(teacherName, "")))
                .collect(Collectors.joining("; \n"));
        String roomSeatText = schedules.stream()
                .map(this::resolveScheduleLocation)
                .distinct()
                .collect(Collectors.joining("; \n"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dateTimeText", buildTextBlock(dateTimeText));
        result.put("dateTimePlaceText", buildTextBlock(dateTimePlaceText));
        result.put("dateTimePlacePersonText", buildTextBlock(dateTimePlacePersonText));
        result.put("roomSeatText", buildTextBlock(roomSeatText));
        return result;
    }

    private String buildSectionDisplayText(ScCourseSchedule schedule, Map<Integer, Map<String, Object>> unitMap) {
        if (schedule == null || schedule.getStartSection() == null) {
            return "-";
        }
        String sectionLabel = resolveSectionRangeLabel(schedule.getStartSection(), schedule.getEndSection(), unitMap);
        String startTime = formatSectionTime(schedule.getStartSection(), true);
        String endTime = formatSectionTime(schedule.getEndSection(), false);
        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            return sectionLabel + "（" + startTime + "-" + endTime + "）";
        }
        return sectionLabel;
    }

    private String resolveSectionRangeLabel(Integer startSection, Integer endSection,
            Map<Integer, Map<String, Object>> unitMap) {
        if (startSection == null) {
            return "-";
        }
        int start = startSection;
        int end = endSection == null ? startSection : endSection;
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        List<String> labels = new ArrayList<>();
        for (int index = start; index <= end; index++) {
            labels.add(resolveUnitLabel(index, unitMap));
        }
        if (labels.isEmpty()) {
            return start + "~" + end + "节";
        }
        return labels.get(0) + "~" + labels.get(labels.size() - 1) + "节";
    }

    private String resolveUnitLabel(Integer section, Map<Integer, Map<String, Object>> unitMap) {
        if (section == null) {
            return "-";
        }
        Map<String, Object> unit = unitMap.get(section);
        if (unit == null) {
            return String.valueOf(section);
        }
        return String.valueOf(unit.getOrDefault("nameZh", section));
    }

    private String resolveScheduleLocation(ScCourseSchedule schedule) {
        if (schedule == null) {
            return "待定教室";
        }
        String location = joinLocation(schedule.getCampusName(), schedule.getBuildingName(),
                StringUtils.defaultIfEmpty(schedule.getClassroomName(), schedule.getClassroom()));
        return StringUtils.isNotEmpty(location) ? location : "待定教室";
    }

    private String buildTermLabel(ScSchoolTerm term) {
        if (term == null) {
            return "-";
        }
        if (StringUtils.isNotEmpty(term.getSchoolYear())) {
            return StringUtils.defaultIfEmpty(term.getTermName(), "-") + "（" + term.getSchoolYear() + "）";
        }
        return StringUtils.defaultIfEmpty(term.getTermName(), "-");
    }

    private Map<String, Object> buildStudentCourseItem(ScClassCourse classCourse, ScSchoolTerm term, List<ScCourseSchedule> schedules) {
        ScCourse course = classCourse.getCourseId() == null ? null
                : scCourseService.selectScCourseByCourseId(classCourse.getCourseId());
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", classCourse.getId());
        item.put("classId", classCourse.getClassId());
        item.put("className", classCourse.getClassName());
        item.put("courseId", classCourse.getCourseId());
        item.put("courseName", classCourse.getCourseName());
        item.put("courseCode", course == null ? null : course.getCourseCode());
        item.put("subjectType", course == null ? null : course.getSubjectType());
        item.put("intro", course == null ? null : course.getIntro());
        item.put("teacherId", classCourse.getTeacherId());
        item.put("termId", classCourse.getTermId());
        item.put("termName", classCourse.getTermName());
        item.put("schoolYear", term == null ? null : term.getSchoolYear());
        item.put("businessType", classCourse.getBusinessType());
        item.put("teachingClassCode", classCourse.getTeachingClassCode());
        item.put("credits", classCourse.getCredits() == null ? estimateCredits(course, classCourse) : classCourse.getCredits());
        item.put("courseCategory", classCourse.getCourseCategory());
        item.put("openDeptName", classCourse.getOpenDeptName());
        item.put("major", classCourse.getMajor());
        item.put("campusName", classCourse.getCampusName());
        item.put("assessmentType", classCourse.getAssessmentType());
        item.put("teachingLanguage", classCourse.getTeachingLanguage());
        item.put("prerequisiteCourse", classCourse.getPrerequisiteCourse());
        item.put("taskType", classCourse.getTaskType());
        item.put("requiredFlag", classCourse.getRequiredFlag());
        item.put("courseLevelRequirement", classCourse.getCourseLevelRequirement());
        item.put("totalHours", classCourse.getTotalHours());
        item.put("requiredWeeks", classCourse.getRequiredWeeks());
        item.put("weeklyHours", classCourse.getWeeklyHours());
        item.put("arrangedHours", classCourse.getArrangedHours());
        item.put("studentLimit", classCourse.getStudentLimit());
        item.put("actualStudentCount", classCourse.getActualStudentCount());
        item.put("remark", classCourse.getRemark());
        item.put("semesterLabel", term == null ? classCourse.getTermName() : buildTermLabel(term));
        item.put("scheduleText", buildScheduleTextVm(classCourse, schedules));
        item.put("scheduleDetails", buildCourseScheduleDetails(classCourse, schedules, term));
        return item;
    }

    private double averageScore(List<ScExamRecord> records) {
        List<Double> scores = records.stream()
                .map(ScExamRecord::getScore)
                .filter(Objects::nonNull)
                .map(BigDecimal::doubleValue)
                .collect(Collectors.toList());
        if (scores.isEmpty()) {
            return 0D;
        }
        double total = scores.stream().reduce(0D, Double::sum);
        return Math.round((total / scores.size()) * 100D) / 100D;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> castToMapList(Object data) {
        if (data instanceof List) {
            return (List<Map<String, Object>>) data;
        }
        return Collections.emptyList();
    }

    private Map<String, Object> taskItem(String key, String title, String desc, String tag, String taskTypeCode, String tagType,
            String status, List<String> meta, Map<String, Object> action) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("key", key);
        item.put("title", title);
        item.put("desc", desc);
        item.put("tag", tag);
        item.put("taskTypeCode", taskTypeCode);
        item.put("tagType", tagType);
        item.put("status", status);
        item.put("meta", meta == null ? Collections.emptyList() : meta);
        item.put("jumpPath", action == null ? null : action.get("path"));
        item.put("action", action);
        return item;
    }

    private Map<String, Object> manualTaskItem(Map<String, Object> source) {
        String completionStatus = String.valueOf(source.getOrDefault("completionStatus", "PENDING"));
        String taskType = String.valueOf(source.getOrDefault("taskType", "COURSE"));
        Long taskId = asLong(source.get("taskId"));
        Long actionTargetId = asLong(source.get("actionTargetId"));
        Long dispatchId = asLong(source.get("dispatchId"));
        String path = StringUtils.defaultIfEmpty((String) source.get("actionPath"), "/student/plaza");
        List<String> meta = new ArrayList<>();
        if (source.get("dueTime") != null) {
            Date dueDate = parseAnyDate(source.get("dueTime"));
            if (dueDate != null) {
                meta.add("截止 " + DateUtils.parseDateToStr("yyyy-MM-dd HH:mm", dueDate));
            }
        }
        if (StringUtils.isNotEmpty((String) source.get("priorityLevel"))) {
            meta.add("优先级 " + source.get("priorityLevel"));
        }
        return taskItem("manual-" + taskId + "-" + dispatchId,
                StringUtils.defaultIfEmpty((String) source.get("taskTitle"), "学习任务"),
                StringUtils.defaultIfEmpty((String) source.get("taskDesc"), "请按要求完成任务。"),
                resolveManualTaskLabel(taskType),
                "COMPLETED".equals(completionStatus) ? "COMPLETED" : taskType,
                resolveManualTagType(completionStatus, taskType),
                resolveManualStatusText(completionStatus, source.get("dueTime")),
                meta,
                actionItem(resolveManualActionType(path), actionTargetId, dispatchId, source, "/student/tasks/" + dispatchId));
    }

    private Map<String, Object> toPortalNoticeItem(SysNotice notice, String defaultCategory) {
        Map<String, Object> item = new LinkedHashMap<>();
        String category = StringUtils.defaultIfEmpty(notice.getBizCategory(), defaultCategory);
        item.put("noticeId", notice.getNoticeId());
        item.put("messageId", "notice-" + notice.getNoticeId());
        item.put("bizCategory", category);
        item.put("messageType", "NOTICE".equals(category) ? "NOTICE" : "SYSTEM_MESSAGE");
        item.put("messageTitle", notice.getNoticeTitle());
        item.put("messageContent", notice.getNoticeContent());
        item.put("messageSummary", StringUtils.defaultIfEmpty(notice.getNoticeSummary(), notice.getNoticeTitle()));
        item.put("noticeType", notice.getNoticeType());
        item.put("createTime", notice.getPublishTime() == null ? notice.getCreateTime() : notice.getPublishTime());
        item.put("publishTime", notice.getPublishTime());
        item.put("expireTime", notice.getExpireTime());
        item.put("levelType", "1".equals(notice.getPinned()) ? "WARNING" : "INFO");
        item.put("actionType", StringUtils.defaultIfEmpty(notice.getActionType(), "notice"));
        item.put("actionPath", notice.getActionPath());
        item.put("actionTarget", notice.getActionTargetId() == null ? notice.getNoticeId() : notice.getActionTargetId());
        item.put("pinned", notice.getPinned());
        item.put("readFlag", StringUtils.defaultIfEmpty(notice.getReadFlag(), "0"));
        item.put("readTime", notice.getReadTime());
        item.put("createBy", notice.getCreateBy());
        return item;
    }

    private Map<String, Object> actionItem(String type, Long targetId, Long recordId, Map<String, Object> row, String path) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("type", type);
        item.put("targetId", targetId);
        item.put("recordId", recordId);
        item.put("path", path);
        item.put("row", row == null ? Collections.emptyMap() : row);
        return item;
    }

    private Map<String, Object> rowMap(Object... kv) {
        Map<String, Object> row = new LinkedHashMap<>();
        if (kv == null) {
            return row;
        }
        for (int i = 0; i + 1 < kv.length; i += 2) {
            row.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return row;
    }

    private String resolvePortalReceiverScope(LoginUser loginUser) {
        if (loginUser == null || loginUser.getAuthorities() == null) {
            return "ALL";
        }
        Set<String> authorities = loginUser.getAuthorities().stream()
                .map(item -> item == null ? "" : item.getAuthority())
                .collect(Collectors.toSet());
        if (authorities.contains("ROLE_student")) {
            return "STUDENT";
        }
        if (authorities.contains("ROLE_teacher")) {
            return "TEACHER";
        }
        if (authorities.contains("ROLE_parent")) {
            return "PARENT";
        }
        if (authorities.contains("ROLE_admin")) {
            return "ADMIN";
        }
        return "ALL";
    }

    private String resolveCourseTaskLabel(String taskType) {
        String code = resolveCourseTaskCode(taskType);
        if ("HOMEWORK".equals(code)) {
            return "作业任务";
        }
        if ("EXAM".equals(code)) {
            return "考试任务";
        }
        if ("PRACTICE".equals(code)) {
            return "实验任务";
        }
        return "课程任务";
    }

    private String resolveCourseTaskCode(String taskType) {
        String normalized = StringUtils.upperCase(StringUtils.trimToEmpty(taskType));
        if (StringUtils.containsAny(normalized, "作业", "HOMEWORK", "ASSIGNMENT")) {
            return "HOMEWORK";
        }
        if (StringUtils.containsAny(normalized, "考试", "测验", "EXAM", "QUIZ", "TEST")) {
            return "EXAM";
        }
        if (StringUtils.containsAny(normalized, "实验", "实践", "上机", "PRACTICE", "LAB")) {
            return "PRACTICE";
        }
        return "COURSE";
    }

    private String resolveManualTaskLabel(String taskType) {
        if ("HOMEWORK".equalsIgnoreCase(taskType)) {
            return "作业任务";
        }
        if ("EXAM".equalsIgnoreCase(taskType)) {
            return "考试任务";
        }
        if ("PRACTICE".equalsIgnoreCase(taskType)) {
            return "实验任务";
        }
        if ("READING".equalsIgnoreCase(taskType)) {
            return "阅读任务";
        }
        return "学习任务";
    }

    private String resolveManualTagType(String completionStatus, String taskType) {
        if ("COMPLETED".equalsIgnoreCase(completionStatus)) {
            return "success";
        }
        if ("EXAM".equalsIgnoreCase(taskType)) {
            return "danger";
        }
        if ("HOMEWORK".equalsIgnoreCase(taskType)) {
            return "warning";
        }
        if ("PRACTICE".equalsIgnoreCase(taskType)) {
            return "primary";
        }
        return "info";
    }

    private String resolveManualStatusText(String completionStatus, Object dueTime) {
        if ("COMPLETED".equalsIgnoreCase(completionStatus)) {
            return "已完成";
        }
        Date dueDate = parseAnyDate(dueTime);
        if (dueDate != null) {
            return "截止 " + DateUtils.parseDateToStr("yyyy-MM-dd HH:mm", dueDate);
        }
        return "待处理";
    }

    private String resolveManualActionType(String path) {
        if (StringUtils.contains(path, "/student/exams/session/")) {
            return "resume";
        }
        if (StringUtils.contains(path, "/student/exams")) {
            return "exam";
        }
        if (StringUtils.contains(path, "/student/wrongbook")) {
            return "wrongbook";
        }
        if (StringUtils.contains(path, "/student/courses")) {
            return "course";
        }
        return "custom";
    }

    private Date parseAnyDate(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value == null) {
            return null;
        }
        try {
            return DateUtils.parseDate(String.valueOf(value));
        } catch (Exception e) {
            return null;
        }
    }

    private Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (Exception e) {
            return null;
        }
    }

    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private Map<String, Object> maskCourseQuestion(ScQuestionBank source) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("questionId", source.getQuestionId());
        item.put("catalogId", source.getCatalogId());
        item.put("courseId", source.getCourseId());
        item.put("chapterId", source.getChapterId());
        item.put("knowledgePointId", source.getKnowledgePointId());
        item.put("questionType", source.getQuestionType());
        item.put("difficultyLevel", source.getDifficultyLevel());
        item.put("stem", maskStem(source.getStem(), source.getQuestionType()));
        item.put("source", source.getSource());
        item.put("questionTags", source.getQuestionTags());
        item.put("materialContent", source.getMaterialContent());
        item.put("sourceBatchNo", source.getSourceBatchNo());
        item.put("qualityScore", source.getQualityScore());
        item.put("status", source.getStatus());
        return item;
    }

    private String maskStem(String stem, String questionType) {
        if (StringUtils.isEmpty(stem)) {
            return stem;
        }
        String plain = stem.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
        String type = StringUtils.defaultIfEmpty(questionType, "").trim().toLowerCase();
        if ("essay".equals(type) || "material".equals(type) || "case".equals(type)) {
            if (plain.length() <= 10) {
                return "题干已脱敏";
            }
            return plain.substring(0, Math.min(8, plain.length())) + " ……（题干已脱敏）";
        }
        if (plain.length() <= 12) {
            return plain.length() <= 4 ? "题干已脱敏" : plain.substring(0, 2) + " ...";
        }
        int prefix = Math.min(10, Math.max(4, plain.length() / 5));
        int suffix = Math.min(6, Math.max(2, plain.length() / 8));
        if (prefix + suffix >= plain.length()) {
            return plain.substring(0, Math.min(4, plain.length())) + " ...";
        }
        return plain.substring(0, prefix) + " ...... " + plain.substring(plain.length() - suffix);
    }

    private List<Map<String, Object>> buildCourseScheduleDetails(ScClassCourse classCourse,
            List<ScCourseSchedule> schedules, ScSchoolTerm term) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (schedules == null || schedules.isEmpty()) {
            return result;
        }
        for (ScCourseSchedule schedule : schedules) {
            List<Integer> weekIndexes = extractWeekIndexes(schedule.getWeeksText(), schedule.getWeeksJson());
            if (weekIndexes.isEmpty()) {
                result.add(buildScheduleDetailItem(classCourse, schedule, "-"));
                continue;
            }
            for (Integer weekIndex : weekIndexes) {
                result.add(buildScheduleDetailItem(classCourse, schedule,
                        calculateScheduleDate(term, weekIndex, schedule.getWeekDay())));
            }
        }
        result.sort(Comparator.comparing(item -> String.valueOf(item.get("date"))));
        return result;
    }

    private Map<String, Object> buildScheduleDetailItem(ScClassCourse classCourse, ScCourseSchedule schedule,
            String date) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("date", date);
        detail.put("weeksText", normalizeWeeksText(schedule.getWeeksText()));
        detail.put("startSection", schedule.getStartSection());
        detail.put("endSection", schedule.getEndSection());
        detail.put("startSectionLabel", resolveUnitLabel(schedule.getStartSection(), buildLayoutUnitMap()));
        detail.put("endSectionLabel", resolveUnitLabel(schedule.getEndSection(), buildLayoutUnitMap()));
        detail.put("sectionText", resolveSectionRangeLabel(schedule.getStartSection(), schedule.getEndSection(), buildLayoutUnitMap()));
        detail.put("campusName", schedule.getCampusName());
        detail.put("buildingName", schedule.getBuildingName());
        detail.put("classroomName", StringUtils.defaultIfEmpty(schedule.getClassroomName(), schedule.getClassroom()));
        detail.put("classroom", joinLocation(schedule.getCampusName(), schedule.getBuildingName(),
                StringUtils.defaultIfEmpty(schedule.getClassroomName(), schedule.getClassroom())));
        detail.put("teacherName", StringUtils.defaultIfEmpty(classCourse.getTeacherName(),
                resolveTeacherName(classCourse.getTeacherId())));
        detail.put("weekLabel", resolveWeekdayLabel(schedule.getWeekDay()));
        detail.put("startTime", formatSectionTime(schedule.getStartSection(), true));
        detail.put("endTime", formatSectionTime(schedule.getEndSection(), false));
        detail.put("hours", Math.max(1, (schedule.getEndSection() == null ? 1 : schedule.getEndSection())
                - (schedule.getStartSection() == null ? 1 : schedule.getStartSection()) + 1));
        return detail;
    }

    private String calculateScheduleDate(ScSchoolTerm term, Integer weekIndex, Integer weekDay) {
        if (term == null || term.getStartDate() == null || weekIndex == null || weekDay == null) {
            return "-";
        }
        LocalDate startDate = convertToLocalDate(term.getStartDate());
        LocalDate firstMondayOfTerm = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate targetDate = firstMondayOfTerm.plusWeeks(Math.max(0, weekIndex - 1L))
                .plusDays(Math.max(0, weekDay - 1L));
        return targetDate.toString();
    }

    private String joinLocation(String... values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            String text = StringUtils.trimToEmpty(value);
            if (StringUtils.isEmpty(text)) {
                continue;
            }
            boolean exists = result.stream().anyMatch(item -> StringUtils.equals(item, text)
                    || StringUtils.contains(item, text) || StringUtils.contains(text, item));
            if (!exists) {
                result.add(text);
            }
        }
        return String.join(" ", result);
    }

    private Map<String, Object> buildTextBlock(String text) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("textZh", StringUtils.isEmpty(text) ? null : text);
        result.put("textEn", StringUtils.isEmpty(text) ? null : text);
        result.put("text", StringUtils.isEmpty(text) ? null : text);
        return result;
    }

    private String buildScheduleWeeksInfo(List<ScCourseSchedule> schedules) {
        return schedules.stream()
                .map(ScCourseSchedule::getWeeksText)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.joining("; "));
    }

    private List<Integer> extractWeekIndexes(String weeksText, String weeksJson) {
        Set<Integer> weekSet = new LinkedHashSet<>();
        if (StringUtils.isNotEmpty(weeksJson)) {
            try {
                List<Integer> values = objectMapper.readValue(weeksJson, new TypeReference<List<Integer>>() {
                });
                weekSet.addAll(
                        values.stream().filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));
                if (!weekSet.isEmpty()) {
                    return new ArrayList<>(weekSet);
                }
            } catch (Exception ignored) {
            }
        }
        for (String segment : normalizeWeekSegments(weeksText)) {
            weekSet.addAll(expandWeekSegment(segment));
        }
        return new ArrayList<>(weekSet);
    }

    private String normalizeWeeksText(String weeksText) {
        if (StringUtils.isEmpty(weeksText)) {
            return "";
        }
        String normalized = weeksText.trim()
                .replace("第", "")
                .replace("周次", "")
                .replace("，", ",")
                .replace("、", ",")
                .replace("；", ";")
                .replace("（", "(")
                .replace("）", ")")
                .replace("～", "~")
                .replace("至", "~")
                .replace("到", "~");
        normalized = normalized.replaceAll("\\s+", "");
        normalized = normalized.replace("周", "");
        normalized = normalized.replace("-", "~");
        return normalized;
    }

    private List<String> normalizeWeekSegments(String weeksText) {
        if (StringUtils.isEmpty(weeksText)) {
            return Collections.emptyList();
        }
        String normalized = weeksText.trim()
                .replace("第", "")
                .replace("周次", "")
                .replace("，", ",")
                .replace("、", ",")
                .replace("；", ",")
                .replace(";", ",")
                .replace("（", "(")
                .replace("）", ")")
                .replace("～", "-")
                .replace("~", "-")
                .replace("至", "-")
                .replace("到", "-");
        normalized = normalized.replaceAll("\\s+", "");
        normalized = normalized.replace("周", "");
        if (StringUtils.isEmpty(normalized)) {
            return Collections.emptyList();
        }
        return Arrays.stream(normalized.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
    }

    private Set<Integer> expandWeekSegment(String segment) {
        Set<Integer> weekSet = new LinkedHashSet<>();
        if (StringUtils.isEmpty(segment)) {
            return weekSet;
        }
        boolean oddOnly = segment.contains("单");
        boolean evenOnly = segment.contains("双");
        String cleaned = segment
                .replace("(单)", "")
                .replace("(双)", "")
                .replace("单周", "")
                .replace("双周", "")
                .replace("单", "")
                .replace("双", "")
                .replace("(", "")
                .replace(")", "");
        if (StringUtils.isEmpty(cleaned)) {
            return weekSet;
        }
        if (cleaned.contains("-")) {
            String[] range = cleaned.split("-");
            if (range.length == 2 && StringUtils.isNumeric(range[0].trim()) && StringUtils.isNumeric(range[1].trim())) {
                int start = Integer.parseInt(range[0].trim());
                int end = Integer.parseInt(range[1].trim());
                if (start > end) {
                    int temp = start;
                    start = end;
                    end = temp;
                }
                int step = (oddOnly || evenOnly) ? 2 : 1;
                if (oddOnly && start % 2 == 0) {
                    start++;
                }
                if (evenOnly && start % 2 != 0) {
                    start++;
                }
                for (int week = start; week <= end; week += step) {
                    weekSet.add(week);
                }
            }
            return weekSet;
        }
        if (StringUtils.isNumeric(cleaned)) {
            int week = Integer.parseInt(cleaned);
            if ((!oddOnly && !evenOnly)
                    || (oddOnly && week % 2 != 0)
                    || (evenOnly && week % 2 == 0)) {
                weekSet.add(week);
            }
        }
        return weekSet;
    }

    private String resolveWeekdayLabel(Integer weekDay) {
        if (weekDay == null) {
            return "星期-";
        }
        switch (weekDay) {
            case 1:
                return "星期一";
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            case 7:
                return "星期日";
            default:
                return "星期" + weekDay;
        }
    }

    private double estimateCredits(ScCourse course, ScClassCourse classCourse) {
        return classCourse.getWeeklyHours() == null ? 0D : Math.max(1D, classCourse.getWeeklyHours() / 2D);
    }

    private String formatSectionTime(Integer section, boolean start) {
        if (section == null) {
            return null;
        }
        Map<Integer, Map<String, Object>> unitMap = buildLayoutUnitMap();
        Map<String, Object> unit = unitMap.get(section);
        if (unit != null) {
            Object raw = start ? unit.get("startTime") : unit.get("endTime");
            return formatClockText(raw);
        }
        return null;
    }

    private Map<String, Object> buildDefaultTimeTableLayout() {
        String configValue = configService.selectConfigByKey("campus.schedule.timeTableLayout");
        if (StringUtils.isNotEmpty(configValue)) {
            try {
                Map<String, Object> compact = objectMapper.readValue(configValue,
                        new TypeReference<LinkedHashMap<String, Object>>() {
                        });
                if (compact.containsKey("u")) {
                    return expandCompactLayout(compact);
                }
                return compact;
            } catch (Exception ignored) {
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", 1);
        result.put("nameZh", "默认课表布局");
        result.put("nameEn", "Default time table layout");
        result.put("enabled", true);
        result.put("courseUnitList", buildCourseUnitList());
        return result;
    }

    private Map<Integer, Map<String, Object>> buildLayoutUnitMap() {
        Map<String, Object> layout = buildDefaultTimeTableLayout();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> units = (List<Map<String, Object>>) layout.getOrDefault("courseUnitList",
                Collections.emptyList());
        Map<Integer, Map<String, Object>> result = new LinkedHashMap<>();
        for (Map<String, Object> unit : units) {
            Integer indexNo = Integer.parseInt(String.valueOf(unit.getOrDefault("indexNo", 0)));
            result.put(indexNo, unit);
        }
        return result;
    }

    private String formatClockText(Object rawTime) {
        if (rawTime == null) {
            return null;
        }
        int value = Integer.parseInt(String.valueOf(rawTime));
        int hour = value / 100;
        int minute = value % 100;
        return String.format("%d:%02d", hour, minute);
    }

    private List<Map<String, Object>> buildCourseUnitList() {
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(buildCourseUnit("1", "Unit 1", 1, 900, 940, "MORNING"));
        result.add(buildCourseUnit("2", "Unit 2", 2, 950, 1030, "MORNING"));
        result.add(buildCourseUnit("3", "Unit 3", 3, 1040, 1120, "MORNING"));
        result.add(buildCourseUnit("4", "Unit 4", 4, 1130, 1210, "MORNING"));
        result.add(buildCourseUnit("5", "Unit 5", 5, 1320, 1400, "AFTERNOON"));
        result.add(buildCourseUnit("6", "Unit 6", 6, 1410, 1450, "AFTERNOON"));
        result.add(buildCourseUnit("7", "Unit 7", 7, 1500, 1540, "AFTERNOON"));
        result.add(buildCourseUnit("8", "Unit 8", 8, 1550, 1630, "AFTERNOON"));
        result.add(buildCourseUnit("9", "Unit 9", 9, 1830, 1910, "EVENING"));
        result.add(buildCourseUnit("10", "Unit 10", 10, 1920, 2000, "EVENING"));
        return result;
    }

    private Map<String, Object> buildCourseUnit(String nameZh, String nameEn, int indexNo, int startTime, int endTime,
            String dayPart) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("nameZh", nameZh);
        result.put("nameEn", nameEn);
        result.put("indexNo", indexNo);
        result.put("startTime", startTime);
        result.put("endTime", endTime);
        result.put("dayPart", dayPart);
        return result;
    }

    private List<Map<String, Object>> buildCourseTablePrintConfigs() {
        Map<String, Object> layout = buildDefaultTimeTableLayout();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> courseUnitList = (List<Map<String, Object>>) layout.getOrDefault("courseUnitList",
                Collections.emptyList());
        Map<String, List<List<Integer>>> grouped = new LinkedHashMap<>();
        for (Map<String, Object> unit : courseUnitList) {
            String dayPart = String.valueOf(unit.getOrDefault("dayPart", "MORNING"));
            int indexNo = Integer.parseInt(String.valueOf(unit.getOrDefault("indexNo", 0)));
            grouped.computeIfAbsent(dayPart, key -> new ArrayList<>());
            List<List<Integer>> current = grouped.get(dayPart);
            if (current.isEmpty() || current.get(current.size() - 1).size() >= 2) {
                current.add(new ArrayList<>());
            }
            current.get(current.size() - 1).add(indexNo);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach(
                (dayPart, unitGroup) -> result.add(buildPrintConfig(resolveDayPartLabel(dayPart), dayPart, unitGroup)));
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> expandCompactLayout(Map<String, Object> compact) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", compact.getOrDefault("id", 1));
        result.put("nameZh", compact.getOrDefault("n", "默认课表布局"));
        result.put("nameEn", compact.getOrDefault("e", "Default time table layout"));
        result.put("enabled", true);
        List<List<Object>> units = (List<List<Object>>) compact.getOrDefault("u", Collections.emptyList());
        result.put("courseUnitList", units.stream().map(unit -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("nameZh", unit.get(0));
            item.put("indexNo", unit.get(1));
            item.put("startTime", unit.get(2));
            item.put("endTime", unit.get(3));
            item.put("dayPart", decodeDayPart(String.valueOf(unit.get(4))));
            return item;
        }).toList());
        return result;
    }

    private String decodeDayPart(String value) {
        return switch (value) {
            case "N" -> "NOON";
            case "A" -> "AFTERNOON";
            case "E" -> "EVENING";
            default -> "MORNING";
        };
    }

    private String resolveDayPartLabel(String dayPart) {
        return switch (dayPart) {
            case "NOON" -> "中午";
            case "AFTERNOON" -> "下午";
            case "EVENING" -> "晚上";
            default -> "上午";
        };
    }

    private Map<String, Object> buildPrintConfig(String nameZh, String nameEn, List<List<Integer>> unitGroup) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("nameZh", nameZh);
        result.put("nameEn", nameEn);
        result.put("unitGroup", unitGroup);
        return result;
    }

    private ScSchoolTerm resolveTerm(Long termId, List<ScClassCourse> classCourses) {
        if (termId != null) {
            return scSchoolTermService.selectScSchoolTermByTermId(termId);
        }
        if (classCourses != null && !classCourses.isEmpty() && classCourses.get(0).getTermId() != null) {
            return scSchoolTermService.selectScSchoolTermByTermId(classCourses.get(0).getTermId());
        }
        return null;
    }

    private List<Integer> buildWeekIndices(ScSchoolTerm term) {
        int totalWeeks = term != null && term.getTotalWeeks() != null && term.getTotalWeeks() > 0 ? term.getTotalWeeks()
                : 20;
        List<Integer> result = new ArrayList<>();
        for (int week = 1; week <= totalWeeks; week++) {
            result.add(week);
        }
        return result;
    }

    private Integer resolveCurrentWeek(ScSchoolTerm term) {
        if (term == null || term.getStartDate() == null) {
            return 1;
        }
        LocalDate startDate = convertToLocalDate(term.getStartDate());
        LocalDate today = LocalDate.now();
        LocalDate endDate = term.getEndDate() != null ? convertToLocalDate(term.getEndDate()) : null;
        int totalWeeks = term.getTotalWeeks() != null && term.getTotalWeeks() > 0 ? term.getTotalWeeks() : 20;
        if (today.isBefore(startDate)) {
            return 1;
        }

        if (endDate != null && today.isAfter(endDate)) {
            return totalWeeks;
        }

        LocalDate firstMondayOfTerm = startDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        long diffDays = ChronoUnit.DAYS.between(firstMondayOfTerm, today);
        int currentWeek = (int) (diffDays / 7) + 1;

        currentWeek = Math.max(1, currentWeek);
        currentWeek = Math.min(totalWeeks, currentWeek);
        return currentWeek;
    }

    // 工具方法：统一Date转LocalDate（避免时区问题）
    private LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        // 显式指定系统时区，避免隐式转换的偏差
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
