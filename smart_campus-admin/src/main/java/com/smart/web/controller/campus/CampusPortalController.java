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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.annotation.Log;
import com.smart.common.constant.HttpStatus;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.domain.entity.SysDept;
import com.smart.common.core.domain.entity.SysDictData;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.core.domain.entity.SysUser;
import com.smart.common.core.domain.model.LoginUser;
import com.smart.common.utils.DateUtils;
import com.smart.common.utils.SecurityUtils;
import com.smart.common.utils.StringUtils;
import com.smart.common.utils.poi.ExcelUtil;
import com.smart.common.enums.BusinessType;
import com.smart.framework.web.service.TokenService;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.domain.ScClass;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScCourseSelectionPlan;
import com.smart.system.domain.ScCourseSelectionRequest;
import com.smart.system.domain.ScCourseStudent;
import com.smart.system.domain.ScCourse;
import com.smart.system.domain.ScCourseSchedule;
import com.smart.system.domain.ScExamPaper;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScKnowledgePoint;
import com.smart.system.domain.ScQuestionBank;
import com.smart.system.domain.ScSchoolTerm;
import com.smart.system.domain.ScStudyRecord;
import com.smart.system.domain.ScStudentScore;
import com.smart.system.domain.ScUserAchievement;
import com.smart.system.domain.ScLearningTaskSubmission;
import com.smart.system.domain.ScWrongQuestionBook;
import com.smart.system.domain.ScParentStudentRel;
import com.smart.system.domain.SysNotice;
import com.smart.system.domain.campusvo.PortalCourseOfferingExportVo;
import com.smart.system.domain.dto.CourseSelectionOperateDto;
import com.smart.system.domain.dto.CourseSelectionRequestReviewDto;
import com.smart.system.domain.dto.PortalCourseOfferingExportRequest;
import com.smart.system.service.ICampusOverviewService;
import com.smart.system.service.IScClassCourseService;
import com.smart.system.service.IScClassService;
import com.smart.system.service.IScCourseStudentService;
import com.smart.system.service.IScCourseScheduleService;
import com.smart.system.service.IScCourseSelectionPlanService;
import com.smart.system.service.IScCourseSelectionRequestService;
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
import com.smart.system.service.IScStudyRecordService;
import com.smart.system.service.IScStudentScoreService;
import com.smart.system.service.IScUserGrowthService;
import com.smart.system.service.IScUserProfileService;
import com.smart.system.service.IScWrongQuestionBookService;
import com.smart.system.service.ISysConfigService;
import com.smart.system.service.ISysDeptService;
import com.smart.system.service.ISysDictTypeService;
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
    private ISysDeptService sysDeptService;

    @Autowired
    private ISysDictTypeService sysDictTypeService;

    @Autowired
    private IScUserProfileService scUserProfileService;

    @Autowired
    private IScClassService scClassService;

    @Autowired
    private IScCourseService scCourseService;

    @Autowired
    private IScClassCourseService scClassCourseService;

    @Autowired
    private IScCourseStudentService scCourseStudentService;

    @Autowired
    private IScCourseSelectionRequestService scCourseSelectionRequestService;

    @Autowired
    private IScCourseSelectionPlanService scCourseSelectionPlanService;

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
    private IScStudyRecordService scStudyRecordService;

    @Autowired
    private IScStudentScoreService scStudentScoreService;

    @Autowired
    private IScUserGrowthService scUserGrowthService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.smart.system.service.IScTextbookPlanService scTextbookPlanService;

    @Autowired
    private com.smart.system.service.IScTextbookService scTextbookService;

    /**
     * 学生端 - 获取本学期教材计划列表
     * 根据学生所在年级/班级 + 当前学期匹配教材计划，附带教材详情
     */
    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/textbook-plans")
    public AjaxResult studentTextbookPlans(@RequestParam Long userId) {
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        if (profile == null) {
            return success(Collections.emptyList());
        }
        Long termId = resolveCurrentPortalTermId();
        com.smart.system.domain.ScTextbookPlan query = new com.smart.system.domain.ScTextbookPlan();
        query.setTermId(termId);
        query.setPlanStatus("PUBLISHED");
        List<com.smart.system.domain.ScTextbookPlan> allPlans = scTextbookPlanService.selectScTextbookPlanList(query);
        // 过滤：匹配学生年级/班级 或 全校级计划
        List<Map<String, Object>> result = new ArrayList<>();
        for (com.smart.system.domain.ScTextbookPlan plan : allPlans) {
            boolean gradeMatch = plan.getGradeId() == null || Objects.equals(plan.getGradeId(), profile.getGradeId());
            boolean classMatch = plan.getClassId() == null || Objects.equals(plan.getClassId(), profile.getClassId());
            if (gradeMatch && classMatch) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("planId", plan.getPlanId());
                item.put("termId", plan.getTermId());
                item.put("termName", plan.getTermName());
                item.put("textbookId", plan.getTextbookId());
                item.put("textbookName", plan.getTextbookName());
                item.put("courseName", plan.getCourseName());
                item.put("gradeName", plan.getGradeName());
                item.put("className", plan.getClassName());
                item.put("planQuantity", plan.getPlanQuantity());
                item.put("distributedQuantity", plan.getDistributedQuantity());
                // 附带教材详细信息
                com.smart.system.domain.ScTextbook tb = scTextbookService.selectScTextbookByTextbookId(plan.getTextbookId());
                if (tb != null) {
                    item.put("isbn", tb.getIsbn());
                    item.put("author", tb.getAuthor());
                    item.put("publisher", tb.getPublisher());
                    item.put("edition", tb.getEdition());
                    item.put("price", tb.getPrice());
                    item.put("textbookCode", tb.getTextbookCode());
                }
                result.add(item);
            }
        }
        return success(result);
    }

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
        if (!canOperateStudentSelection(userId)) {
            return error("无权查看其他学生的课程信息");
        }
        termId = termId == null ? resolveCurrentPortalTermId() : termId;
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        if (profile == null || profile.getClassId() == null) {
            return success(Collections.emptyList());
        }

        List<ScClassCourse> classCourses = loadStudentSelectedClassCourses(userId, termId, true);
        Map<Long, List<ScCourseSchedule>> scheduleMap = loadScheduleMap(classCourses);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ScClassCourse classCourse : classCourses) {
            ScSchoolTerm term = classCourse.getTermId() == null ? null
                    : scSchoolTermService.selectScSchoolTermByTermId(classCourse.getTermId());
            List<ScCourseSchedule> schedules = scheduleMap.getOrDefault(classCourse.getId(), Collections.emptyList());
            result.add(buildStudentCourseSelectionItem(classCourse, term, schedules, profile, true));
        }
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/class-courses")
    public AjaxResult studentClassCourses(@RequestParam Long userId, @RequestParam(required = false) Long termId) {
        if (!canOperateStudentSelection(userId)) {
            return error("无权查看其他学生的课程信息");
        }
        termId = termId == null ? resolveCurrentPortalTermId() : termId;
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        if (profile == null || profile.getClassId() == null) {
            return success(Collections.emptyList());
        }
        List<ScClassCourse> classCourses = loadStudentClassCourses(profile.getClassId(), termId);
        Map<Long, List<ScCourseSchedule>> scheduleMap = loadScheduleMap(classCourses);
        Set<Long> selectedIds = loadStudentSelectedClassCourses(userId, termId, true).stream()
                .map(ScClassCourse::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<Map<String, Object>> result = new ArrayList<>();
        for (ScClassCourse classCourse : classCourses) {
            ScSchoolTerm term = classCourse.getTermId() == null ? null
                    : scSchoolTermService.selectScSchoolTermByTermId(classCourse.getTermId());
            List<ScCourseSchedule> schedules = scheduleMap.getOrDefault(classCourse.getId(), Collections.emptyList());
            boolean selected = selectedIds.contains(classCourse.getId())
                    || "Y".equalsIgnoreCase(StringUtils.defaultIfEmpty(classCourse.getRequiredFlag(), "N"));
            result.add(buildStudentCourseSelectionItem(classCourse, term, schedules, profile, selected));
        }
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,admin')")
    @GetMapping("/student/course-offerings")
    public TableDataInfo studentCourseOfferings(
            @RequestParam(required = false) Long termId,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String courseCategory,
            @RequestParam(required = false) Long openDeptId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String campusName,
            @RequestParam(required = false) String requiredFlag,
            @RequestParam(required = false) String assessmentType,
            @RequestParam(required = false) BigDecimal credits,
            @RequestParam(required = false) BigDecimal creditsMin,
            @RequestParam(required = false) BigDecimal creditsMax,
            @RequestParam(required = false) String teachingClassCode,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String businessType,
            @RequestParam(required = false) String teachingLanguage,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        List<Map<String, Object>> allRows = loadStudentCourseOfferings(termId, courseName, courseCode, courseCategory,
                openDeptId, teacherId, campusName, requiredFlag, assessmentType, credits, creditsMin, creditsMax,
                teachingClassCode, className, major, businessType, teachingLanguage);
        int total = allRows.size();
        int fromIndex = Math.min((pageNum - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<Map<String, Object>> pageRows = allRows.subList(fromIndex, toIndex);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(pageRows);
        rspData.setTotal(total);
        return rspData;
    }

    @Log(title = "学生端全校开课查询", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasAnyRoles('student,teacher,admin')")
    @PostMapping("/student/course-offerings/export")
    public void exportStudentCourseOfferings(HttpServletResponse response,
            @RequestBody(required = false) PortalCourseOfferingExportRequest request) {
        PortalCourseOfferingExportRequest exportRequest = request == null ? new PortalCourseOfferingExportRequest() : request;
        List<Map<String, Object>> result = loadStudentCourseOfferings(exportRequest.getTermId(), exportRequest.getCourseName(),
                exportRequest.getCourseCode(), exportRequest.getCourseCategory(), exportRequest.getOpenDeptId(),
                exportRequest.getTeacherId(), exportRequest.getCampusName(), exportRequest.getRequiredFlag(),
                exportRequest.getAssessmentType(), exportRequest.getCredits(), exportRequest.getCreditsMin(),
                exportRequest.getCreditsMax(), exportRequest.getTeachingClassCode(), exportRequest.getClassName(),
                exportRequest.getMajor(), exportRequest.getBusinessType(), exportRequest.getTeachingLanguage());

        Set<Long> classCourseIds = exportRequest.getClassCourseIds() == null
                ? Collections.emptySet()
                : exportRequest.getClassCourseIds().stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        List<PortalCourseOfferingExportVo> exportRows = result.stream()
                .filter(item -> classCourseIds.isEmpty() || classCourseIds.contains(toLong(item.get("id"))))
                .map(this::buildPortalCourseOfferingExportVo)
                .collect(Collectors.toList());

        ExcelUtil<PortalCourseOfferingExportVo> util = new ExcelUtil<>(PortalCourseOfferingExportVo.class);
        List<String> exportFields = exportRequest.getExportFields();
        if (exportFields != null && !exportFields.isEmpty()) {
            util.showColumn(exportFields.toArray(new String[0]));
        }
        util.exportExcel(response, exportRows, "全校开课查询数据");
    }

    private List<Map<String, Object>> loadStudentCourseOfferings(Long termId, String courseName, String courseCode,
            String courseCategory, Long openDeptId, Long teacherId, String campusName, String requiredFlag,
            String assessmentType, BigDecimal credits, BigDecimal creditsMin, BigDecimal creditsMax,
            String teachingClassCode, String className, String major, String businessType, String teachingLanguage) {
        termId = termId == null ? resolveCurrentPortalTermId() : termId;
        ScClassCourse query = new ScClassCourse();
        query.setTermId(termId);
        query.setStatus("0");
        if (StringUtils.isNotEmpty(courseName)) {
            query.setCourseName(courseName);
        }
        if (StringUtils.isNotEmpty(courseCategory)) {
            query.setCourseCategory(courseCategory);
        }
        if (openDeptId != null) {
            query.setOpenDeptId(openDeptId);
        }
        if (teacherId != null) {
            query.setTeacherId(teacherId);
        }
        if (StringUtils.isNotEmpty(campusName)) {
            query.setCampusName(campusName);
        }
        if (StringUtils.isNotEmpty(requiredFlag)) {
            query.setRequiredFlag(requiredFlag);
        }
        if (StringUtils.isNotEmpty(assessmentType)) {
            query.setAssessmentType(assessmentType);
        }
        if (credits != null) {
            query.setCredits(credits);
        }
        if (StringUtils.isNotEmpty(teachingClassCode)) {
            query.setTeachingClassCode(teachingClassCode);
        }
        if (StringUtils.isNotEmpty(className)) {
            query.setClassName(className);
        }
        if (StringUtils.isNotEmpty(major)) {
            query.setMajor(major);
        }
        if (StringUtils.isNotEmpty(businessType)) {
            query.setBusinessType(businessType);
        }
        if (StringUtils.isNotEmpty(teachingLanguage)) {
            query.setTeachingLanguage(teachingLanguage);
        }
        List<ScClassCourse> classCourses = scClassCourseService.selectScClassCourseList(query);
        // Filter by courseCode if provided (matched against sc_course table)
        if (StringUtils.isNotEmpty(courseCode)) {
            final String codeFilter = courseCode.toLowerCase();
            classCourses = classCourses.stream().filter(cc -> {
                if (cc.getCourseId() == null) return false;
                ScCourse course = scCourseService.selectScCourseByCourseId(cc.getCourseId());
                return course != null && course.getCourseCode() != null
                        && course.getCourseCode().toLowerCase().contains(codeFilter);
            }).collect(Collectors.toList());
        }
        Map<Long, List<ScCourseSchedule>> scheduleMap = loadScheduleMap(classCourses);
        ScSchoolTerm currentTerm = scSchoolTermService.selectScSchoolTermByTermId(termId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ScClassCourse classCourse : classCourses) {
            List<ScCourseSchedule> schedules = scheduleMap.getOrDefault(classCourse.getId(), Collections.emptyList());
            result.add(buildStudentCourseItem(classCourse, currentTerm, schedules));
        }
        if (credits != null || creditsMin != null || creditsMax != null) {
            final BigDecimal exactCredits = credits;
            final BigDecimal minCredits = creditsMin;
            final BigDecimal maxCredits = creditsMax;
            result = result.stream()
                    .filter(item -> matchesCreditsRange(item.get("credits"), exactCredits, minCredits, maxCredits))
                    .collect(Collectors.toList());
        }
        return result;
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,admin')")
    @GetMapping("/student/course-offerings/options")
    public AjaxResult studentCourseOfferingFilterOptions(@RequestParam(required = false) Long termId) {
        Long effectiveTermId = termId == null ? resolveCurrentPortalTermId() : termId;
        ScClassCourse query = new ScClassCourse();
        query.setStatus("0");
        query.setTermId(effectiveTermId);
        List<ScClassCourse> classCourses = scClassCourseService.selectScClassCourseList(query);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("teacherOptions", buildCourseOfferingTeacherOptions(classCourses));
        result.put("openDeptOptions", buildCourseOfferingDeptOptions(classCourses));
        result.put("campusOptions", mergeOptionLists(
                buildDictOptions("campus_area_type"),
                buildDistinctTextOptions(classCourses.stream().map(ScClassCourse::getCampusName).collect(Collectors.toList()))));
        result.put("majorOptions", mergeOptionLists(
                buildDictOptions("campus_major_type"),
                buildDistinctTextOptions(classCourses.stream().map(ScClassCourse::getMajor).collect(Collectors.toList()))));
        result.put("courseCategoryOptions", buildDistinctTextOptions(
                classCourses.stream().map(ScClassCourse::getCourseCategory).collect(Collectors.toList())));
        result.put("businessTypeOptions", buildDistinctTextOptions(
                classCourses.stream().map(ScClassCourse::getBusinessType).collect(Collectors.toList())));
        result.put("assessmentTypeOptions", buildDistinctTextOptions(
                classCourses.stream().map(ScClassCourse::getAssessmentType).collect(Collectors.toList())));
        result.put("teachingLanguageOptions", buildDistinctTextOptions(
                classCourses.stream().map(ScClassCourse::getTeachingLanguage).collect(Collectors.toList())));
        return success(result);
    }

    private PortalCourseOfferingExportVo buildPortalCourseOfferingExportVo(Map<String, Object> item) {
        PortalCourseOfferingExportVo vo = new PortalCourseOfferingExportVo();
        vo.setTermName(asString(item.get("termName"), asString(item.get("semesterLabel"))));
        vo.setCourseName(asString(item.get("courseName")));
        vo.setCourseCode(asString(item.get("courseCode")));
        vo.setTeachingClassCode(asString(item.get("teachingClassCode"), asString(item.get("className"))));
        vo.setCredits(formatCourseCredits(item.get("credits")));
        vo.setTeacherName(asString(item.get("teacherName")));
        vo.setOpenDeptName(asString(item.get("openDeptName")));
        vo.setMajor(asString(item.get("major")));
        vo.setClassName(asString(item.get("className")));
        vo.setCourseCategory(asString(item.get("courseCategory")));
        vo.setRequiredLabel(resolveCourseOfferingRequiredLabel(asString(item.get("requiredFlag"))));
        vo.setCampusName(asString(item.get("campusName")));
        vo.setScheduleText(asString(item.get("schedulePlaceText"), asString(item.get("scheduleText"))));
        vo.setSectionSummary(buildSectionSummary(castToMapList(item.get("scheduleDetails"))));
        vo.setAssessmentType(asString(item.get("assessmentType")));
        vo.setTeachingLanguage(asString(item.get("teachingLanguage")));
        vo.setTotalHours((Integer) item.get("totalHours"));
        vo.setWeeklyHours((Integer) item.get("weeklyHours"));
        vo.setStudentCountText(buildStudentCountText(item));
        vo.setMaterialHint(toLong(item.get("courseId")) == null ? "待维护" : "详情页查看资源目录");
        return vo;
    }

    private String buildSectionSummary(List<Map<String, Object>> scheduleDetails) {
        if (scheduleDetails == null || scheduleDetails.isEmpty()) {
            return "";
        }
        LinkedHashSet<String> values = new LinkedHashSet<>();
        for (Map<String, Object> detail : scheduleDetails) {
            if (detail == null) {
                continue;
            }
            String sectionText = StringUtils.defaultIfEmpty(String.valueOf(detail.getOrDefault("sectionText", "")),
                    String.format("%s~%s节",
                            String.valueOf(detail.getOrDefault("startSectionLabel", detail.getOrDefault("startSection", ""))),
                            String.valueOf(detail.getOrDefault("endSectionLabel", detail.getOrDefault("endSection", "")))));
            if (StringUtils.isNotEmpty(sectionText)) {
                values.add(sectionText);
            }
            if (values.size() >= 3) {
                break;
            }
        }
        return String.join("；", values);
    }

    private String buildStudentCountText(Map<String, Object> item) {
        Integer actualStudentCount = (Integer) item.get("actualStudentCount");
        Integer studentLimit = (Integer) item.get("studentLimit");
        return studentLimit != null && studentLimit > 0
                ? String.valueOf(actualStudentCount == null ? 0 : actualStudentCount) + "/" + studentLimit
                : String.valueOf(actualStudentCount == null ? 0 : actualStudentCount);
    }

    private String formatCourseCredits(Object credits) {
        if (credits == null) {
            return "";
        }
        if (credits instanceof BigDecimal) {
            return ((BigDecimal) credits).stripTrailingZeros().toPlainString();
        }
        return String.valueOf(credits);
    }

    private String asString(Object value) {
        return asString(value, "");
    }

    private String asString(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        String text = String.valueOf(value);
        return "null".equalsIgnoreCase(text) ? defaultValue : text;
    }

    private String resolveCourseOfferingRequiredLabel(String requiredFlag) {
        if ("Y".equalsIgnoreCase(requiredFlag)) {
            return "必修";
        }
        if ("N".equalsIgnoreCase(requiredFlag)) {
            return "选修";
        }
        return StringUtils.defaultIfEmpty(requiredFlag, "未标注");
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/course-detail")
    public AjaxResult studentCourseDetail(@RequestParam Long userId, @RequestParam Long classCourseId) {
        if (!canOperateStudentSelection(userId)) {
            return error("无权查看其他学生的课程信息");
        }
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        if (profile == null || profile.getClassId() == null) {
            return success(Collections.emptyMap());
        }
        ScClassCourse classCourse = scClassCourseService.selectScClassCourseById(classCourseId);
        if (classCourse == null || !canStudentPreviewCourse(userId, classCourse)) {
            return success(Collections.emptyMap());
        }

        Map<Long, List<ScCourseSchedule>> scheduleMap = loadScheduleMap(Collections.singletonList(classCourse));
        ScSchoolTerm term = classCourse.getTermId() == null ? null
                : scSchoolTermService.selectScSchoolTermByTermId(classCourse.getTermId());
        boolean selected = scCourseStudentService.findActiveSelection(userId, classCourseId) != null
                || (Objects.equals(profile.getClassId(), classCourse.getClassId())
                && "Y".equalsIgnoreCase(StringUtils.defaultIfEmpty(classCourse.getRequiredFlag(), "N")));
        Map<String, Object> currentCourse = buildStudentCourseSelectionItem(classCourse, term,
                scheduleMap.getOrDefault(classCourse.getId(), Collections.emptyList()), profile, selected);

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
    @GetMapping("/student/course-selection/options")
    public AjaxResult studentCourseSelectionOptions(@RequestParam Long userId,
            @RequestParam(required = false) Long termId) {
        if (!canOperateStudentSelection(userId)) {
            return error("无权查看其他学生的选课信息");
        }
        termId = termId == null ? resolveCurrentPortalTermId() : termId;
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        if (profile == null || profile.getClassId() == null) {
            result.put("selectedCourses", Collections.emptyList());
            result.put("availableCourses", Collections.emptyList());
            result.put("stats", Collections.emptyMap());
            return success(result);
        }
        List<ScClassCourse> selectedClassCourses = loadStudentSelectedClassCourses(userId, termId, true);
        Set<Long> selectedIds = selectedClassCourses.stream()
                .map(ScClassCourse::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<String, List<ScClassCourse>> selectedGroupMap = buildSelectedGroupMap(selectedClassCourses);
        ScCourseSelectionPlan plan = resolveSelectionPlan(termId);
        boolean selectionOpen = isSelectionWindowOpen(plan);
        boolean dropOpen = isDropWindowOpen(plan);
        List<ScClassCourse> availableClassCourses = scCourseStudentService.listAvailableClassCoursesForStudent(userId, termId);
        LinkedHashMap<Long, ScClassCourse> mergedMap = new LinkedHashMap<>();
        for (ScClassCourse item : selectedClassCourses) {
            if (item != null && item.getId() != null) {
                mergedMap.put(item.getId(), item);
            }
        }
        for (ScClassCourse item : availableClassCourses) {
            if (item != null && item.getId() != null) {
                mergedMap.putIfAbsent(item.getId(), item);
            }
        }
        List<ScClassCourse> mergedCourses = new ArrayList<>(mergedMap.values());
        Map<Long, List<ScCourseSchedule>> scheduleMap = loadScheduleMap(mergedCourses);
        List<Map<String, Object>> selectedCourses = new ArrayList<>();
        List<Map<String, Object>> availableCourses = new ArrayList<>();
        for (ScClassCourse classCourse : mergedCourses) {
            ScSchoolTerm term = classCourse.getTermId() == null ? null
                    : scSchoolTermService.selectScSchoolTermByTermId(classCourse.getTermId());
            List<ScCourseSchedule> schedules = scheduleMap.getOrDefault(classCourse.getId(), Collections.emptyList());
            boolean selected = selectedIds.contains(classCourse.getId());
            Map<String, Object> item = buildStudentCourseSelectionItem(classCourse, term, schedules, profile, selected);
            String groupLimitReason = selected ? null : resolveGroupSelectionLimitReason(classCourse, selectedGroupMap);
            if (StringUtils.isNotEmpty(groupLimitReason)) {
                item.put("canSelect", false);
                item.put("actionDisabledReason", groupLimitReason);
            }
            if (!selected && !selectionOpen) {
                item.put("canSelect", false);
                item.put("actionDisabledReason", "当前不在选课开放时间");
            }
            if (selected && Boolean.TRUE.equals(item.get("canDrop")) && !dropOpen) {
                item.put("canDrop", false);
                item.put("actionDisabledReason", "当前不在退课开放时间");
            }
            availableCourses.add(item);
            if (selected) {
                selectedCourses.add(item);
            }
        }
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("selectedCount", selectedCourses.size());
        stats.put("availableCount", availableCourses.size());
        stats.put("requiredCount", availableCourses.stream()
                .filter(item -> Boolean.TRUE.equals(item.get("requiredSelection")))
                .count());
        stats.put("optionalCount", availableCourses.stream()
                .filter(item -> !Boolean.TRUE.equals(item.get("requiredSelection")))
                .count());
        result.put("plan", buildSelectionPlanVm(plan));
        result.put("selectionOpen", selectionOpen);
        result.put("dropOpen", dropOpen);
        result.put("selectedCourses", selectedCourses);
        result.put("availableCourses", availableCourses);
        result.put("stats", stats);
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PostMapping("/student/course-selection/select")
    public AjaxResult studentSelectCourse(@RequestBody CourseSelectionOperateDto dto) {
        if (dto == null || dto.getUserId() == null || dto.getClassCourseId() == null) {
            return error("选课参数不能为空");
        }
        if (!canOperateStudentSelection(dto.getUserId())) {
            return error("无权操作其他学生的选课");
        }
        ScClassCourse classCourse = scClassCourseService.selectScClassCourseById(dto.getClassCourseId());
        ScCourseSelectionPlan plan = resolveSelectionPlan(classCourse == null ? null : classCourse.getTermId());
        if (!isSelectionWindowOpen(plan)) {
            return error("当前不在选课开放时间");
        }
        return success(scCourseStudentService.selectCourse(dto.getUserId(), dto.getClassCourseId(), getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PostMapping("/student/course-selection/drop")
    public AjaxResult studentDropCourse(@RequestBody CourseSelectionOperateDto dto) {
        if (dto == null || dto.getUserId() == null || dto.getClassCourseId() == null) {
            return error("退课参数不能为空");
        }
        if (!canOperateStudentSelection(dto.getUserId())) {
            return error("无权操作其他学生的选课");
        }
        ScClassCourse classCourse = scClassCourseService.selectScClassCourseById(dto.getClassCourseId());
        ScCourseSelectionPlan plan = resolveSelectionPlan(classCourse == null ? null : classCourse.getTermId());
        if (!isDropWindowOpen(plan)) {
            return error("当前不在退课开放时间");
        }
        return success(scCourseStudentService.cancelCourse(dto.getUserId(), dto.getClassCourseId(), getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/personalized-selection/options")
    public AjaxResult studentPersonalizedSelectionOptions(@RequestParam Long userId,
            @RequestParam(required = false) Long termId) {
        if (!canOperateStudentSelection(userId)) {
            return error("无权查看其他学生的选课信息");
        }
        termId = termId == null ? resolveCurrentPortalTermId() : termId;
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        if (profile == null || profile.getClassId() == null) {
            return success(Collections.emptyMap());
        }
        ScCourseSelectionPlan plan = resolveSelectionPlan(termId);
        boolean selectionOpen = isSelectionWindowOpen(plan);
        boolean dropOpen = isDropWindowOpen(plan);
        boolean requestOpen = isRequestWindowOpen(plan);
        List<ScClassCourse> allClassCourses = loadActiveTermClassCourses(termId);
        Map<Long, List<ScCourseSchedule>> scheduleMap = loadScheduleMap(allClassCourses);
        List<ScClassCourse> selectedClassCourses = loadStudentSelectedClassCourses(userId, termId, true);
        Set<Long> selectedIds = selectedClassCourses.stream()
                .map(ScClassCourse::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<String, List<ScClassCourse>> selectedGroupMap = buildSelectedGroupMap(selectedClassCourses);
        ScCourseSelectionRequest requestQuery = new ScCourseSelectionRequest();
        requestQuery.setStudentUserId(userId);
        requestQuery.setTermId(termId);
        List<ScCourseSelectionRequest> allRequests = scCourseSelectionRequestService
                .selectScCourseSelectionRequestList(requestQuery)
                .stream()
                .filter(item -> item.getTargetClassCourseId() != null)
                .collect(Collectors.toList());
        Map<Long, ScCourseSelectionRequest> pendingAddMap = allRequests.stream()
                .filter(item -> "0".equals(item.getRequestStatus()) && "ADD".equalsIgnoreCase(StringUtils.defaultIfEmpty(item.getRequestType(), "ADD")))
                .collect(Collectors.toMap(ScCourseSelectionRequest::getTargetClassCourseId, item -> item,
                        (left, right) -> left, LinkedHashMap::new));
        Map<Long, ScCourseSelectionRequest> pendingDropMap = allRequests.stream()
                .filter(item -> "0".equals(item.getRequestStatus()) && "DROP".equalsIgnoreCase(StringUtils.defaultIfEmpty(item.getRequestType(), "ADD")))
                .collect(Collectors.toMap(ScCourseSelectionRequest::getTargetClassCourseId, item -> item,
                        (left, right) -> left, LinkedHashMap::new));
        List<Map<String, Object>> addOptions = new ArrayList<>();
        List<Map<String, Object>> dropOptions = new ArrayList<>();
        for (ScClassCourse classCourse : allClassCourses) {
            if (classCourse == null || classCourse.getId() == null || selectedIds.contains(classCourse.getId())) {
                if (classCourse == null || classCourse.getId() == null || !selectedIds.contains(classCourse.getId())) {
                    continue;
                }
                ScSchoolTerm term = classCourse.getTermId() == null ? null
                        : scSchoolTermService.selectScSchoolTermByTermId(classCourse.getTermId());
                List<ScCourseSchedule> schedules = scheduleMap.getOrDefault(classCourse.getId(), Collections.emptyList());
                Map<String, Object> item = buildStudentCourseSelectionItem(classCourse, term, schedules, profile, true);
                ScCourseSelectionRequest pendingRequest = pendingDropMap.get(classCourse.getId());
                boolean requiredSelection = Boolean.TRUE.equals(item.get("requiredSelection"));
                boolean standardCanDrop = Boolean.TRUE.equals(item.get("canDrop")) && dropOpen;
                item.put("pendingRequest", pendingRequest != null);
                item.put("pendingRequestId", pendingRequest == null ? null : pendingRequest.getRequestId());
                item.put("requestType", "DROP");
                String requestHint;
                boolean canRequest = false;
                if (pendingRequest != null) {
                    requestHint = "你已提交退课申请，请等待审核结果";
                } else if (requiredSelection) {
                    requestHint = StringUtils.defaultIfEmpty(String.valueOf(item.get("actionDisabledReason")),
                            "本班必修课默认保留，不支持退课申请");
                } else if (standardCanDrop) {
                    requestHint = "当前可直接退课，请优先使用选课中心";
                } else if (!requestOpen) {
                    requestHint = "当前不在个性化申请开放时间";
                } else {
                    requestHint = "可发起个性化退课申请";
                    canRequest = true;
                }
                item.put("requestHint", requestHint);
                item.put("canRequest", canRequest);
                dropOptions.add(item);
                continue;
            }
            ScSchoolTerm term = classCourse.getTermId() == null ? null
                    : scSchoolTermService.selectScSchoolTermByTermId(classCourse.getTermId());
            List<ScCourseSchedule> schedules = scheduleMap.getOrDefault(classCourse.getId(), Collections.emptyList());
            Map<String, Object> item = buildStudentCourseSelectionItem(classCourse, term, schedules, profile, false);
            boolean standardCanSelect = Boolean.TRUE.equals(item.get("canSelect"));
            ScCourseSelectionRequest pendingRequest = pendingAddMap.get(classCourse.getId());
            boolean hasPending = pendingRequest != null;
            String groupLimitReason = resolveGroupSelectionLimitReason(classCourse, selectedGroupMap);
            if (standardCanSelect && selectionOpen && !hasPending) {
                if (StringUtils.isEmpty(groupLimitReason)) {
                    continue;
                }
            }
            item.put("pendingRequest", hasPending);
            item.put("pendingRequestId", hasPending ? pendingRequest.getRequestId() : null);
            item.put("requestStatus", hasPending ? pendingRequest.getRequestStatus() : null);
            item.put("requestStatusLabel", hasPending ? "待审核" : null);
            item.put("requestType", "ADD");
            if (StringUtils.isNotEmpty(groupLimitReason)) {
                item.put("requestHint", groupLimitReason);
                item.put("canRequest", false);
                addOptions.add(item);
                continue;
            }
            String requestHint;
            boolean canRequest = false;
            if (hasPending) {
                requestHint = "你已提交选课申请，请等待审核结果";
            } else if (standardCanSelect && selectionOpen) {
                requestHint = "当前教学班支持直接选课，请优先使用选课中心";
            } else if (!requestOpen) {
                requestHint = "当前不在个性化申请开放时间";
            } else {
                requestHint = "可发起个性化选课申请";
                canRequest = true;
            }
            item.put("requestHint", requestHint);
            item.put("canRequest", canRequest);
            addOptions.add(item);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("plan", buildSelectionPlanVm(plan));
        result.put("requestOpen", requestOpen);
        result.put("selectionOpen", selectionOpen);
        result.put("dropOpen", dropOpen);
        result.put("addOptions", addOptions);
        result.put("dropOptions", dropOptions);
        result.put("stats", rowMap(
                "pendingCount", allRequests.stream().filter(item -> "0".equals(item.getRequestStatus())).count(),
                "approvedCount", allRequests.stream().filter(item -> "1".equals(item.getRequestStatus())).count(),
                "rejectedCount", allRequests.stream().filter(item -> "2".equals(item.getRequestStatus())).count(),
                "withdrawnCount", allRequests.stream().filter(item -> "3".equals(item.getRequestStatus())).count()));
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/personalized-selection/requests")
    public AjaxResult studentPersonalizedSelectionRequests(@RequestParam Long userId,
            @RequestParam(required = false) Long termId) {
        if (!canOperateStudentSelection(userId)) {
            return error("无权查看其他学生的申请");
        }
        termId = termId == null ? resolveCurrentPortalTermId() : termId;
        ScCourseSelectionRequest query = new ScCourseSelectionRequest();
        query.setStudentUserId(userId);
        query.setTermId(termId);
        return success(scCourseSelectionRequestService.selectScCourseSelectionRequestList(query));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PostMapping("/student/personalized-selection/request")
    public AjaxResult studentCreatePersonalizedSelectionRequest(@RequestBody ScCourseSelectionRequest request) {
        if (request == null || request.getStudentUserId() == null || request.getTargetClassCourseId() == null) {
            return error("申请参数不能为空");
        }
        if (!canOperateStudentSelection(request.getStudentUserId())) {
            return error("无权提交其他学生的申请");
        }
        ScClassCourse classCourse = scClassCourseService.selectScClassCourseById(request.getTargetClassCourseId());
        ScCourseSelectionPlan plan = resolveSelectionPlan(classCourse == null ? null : classCourse.getTermId());
        if (!isRequestWindowOpen(plan)) {
            return error("当前不在个性化申请开放时间");
        }
        return success(scCourseSelectionRequestService.createRequest(request, getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PostMapping("/student/personalized-selection/cancel")
    public AjaxResult studentCancelPersonalizedSelectionRequest(@RequestBody CourseSelectionRequestReviewDto dto,
            @RequestParam Long userId) {
        if (dto == null || dto.getRequestId() == null) {
            return error("撤回参数不能为空");
        }
        if (!canOperateStudentSelection(userId)) {
            return error("无权撤回其他学生的申请");
        }
        return success(scCourseSelectionRequestService.cancelRequest(dto.getRequestId(), userId, getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/tasks")
    public AjaxResult studentTasks(@RequestParam Long userId, @RequestParam(required = false) Long termId) {
        List<Map<String, Object>> courseList = castToMapList(studentMyCourses(userId, termId).get("data"));
        Map<Long, Map<String, Object>> courseInfoMap = courseList.stream()
                .filter(item -> asLong(item.get("courseId")) != null)
                .collect(Collectors.toMap(item -> asLong(item.get("courseId")), item -> item, (left, right) -> left, LinkedHashMap::new));

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
        Map<String, Object> taskFeedbackProfile = buildTaskFeedbackProfile(userId);

        Map<Long, Long> examAttemptCountMap = records.stream()
                .filter(item -> item.getPaperId() != null)
                .collect(Collectors.groupingBy(ScExamRecord::getPaperId, LinkedHashMap::new, Collectors.counting()));
        Date now = new Date();

        List<Map<String, Object>> examTasks = papers.stream()
                .filter(item -> item.getPublishEndTime() == null || !item.getPublishEndTime().before(now))
                .map(item -> buildExamTask(item, courseInfoMap.get(item.getCourseId()), examAttemptCountMap.getOrDefault(item.getPaperId(), 0L), now))
                .map(item -> applyBehaviorFeedback(item, taskFeedbackProfile))
                .sorted(taskScoreComparator())
                .limit(12)
                .collect(Collectors.toList());

        List<Map<String, Object>> wrongTasks = wrongs.stream()
                .sorted(Comparator
                        .comparing((ScWrongQuestionBook item) -> "1".equals(item.getMasteryStatus()))
                        .thenComparing(item -> defaultInt(item.getWrongCount()), Comparator.reverseOrder())
                        .thenComparing(ScWrongQuestionBook::getLastWrongTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(item -> buildWrongTask(item, courseInfoMap.get(item.getCourseId()), now))
                .map(item -> applyBehaviorFeedback(item, taskFeedbackProfile))
                .limit(10)
                .collect(Collectors.<Map<String, Object>>toList());

        List<Map<String, Object>> courseTasks = courseList.stream()
                .filter(item -> {
                    Long courseId = asLong(item.get("courseId"));
                    return courseId != null && examCourseIds.contains(courseId);
                })
                .map(item -> buildCourseTask(item, now))
                .map(item -> applyBehaviorFeedback(item, taskFeedbackProfile))
                .sorted(taskScoreComparator())
                .limit(8)
                .collect(Collectors.toList());

        List<Map<String, Object>> manualTasks = scLearningTaskService.selectUserDispatchedTasks(userId, termId).stream()
                .map(this::manualTaskItem)
                .map(item -> applyBehaviorFeedback(item, taskFeedbackProfile))
                .sorted(taskScoreComparator())
                .collect(Collectors.toList());

        List<Map<String, Object>> pendingManualTasks = manualTasks.stream()
                .filter(item -> !"COMPLETED".equals(String.valueOf(item.get("taskTypeCode"))))
                .collect(Collectors.toList());

        List<Map<String, Object>> todoCandidates = new ArrayList<>();
        todoCandidates.addAll(pendingManualTasks);
        List<Map<String, Object>> todoTasks = new ArrayList<>();
        records.stream()
                .filter(item -> "ONGOING".equals(item.getExamStatus()))
                .findFirst()
                .ifPresent(item -> todoCandidates.add(applyBehaviorFeedback(buildResumeTask(item), taskFeedbackProfile)));
        todoCandidates.addAll(examTasks.stream()
                .filter(item -> getTaskScore(item) >= 72)
                .limit(3)
                .collect(Collectors.toList()));
        todoCandidates.addAll(courseTasks.stream()
                .filter(item -> getTaskScore(item) >= 52)
                .limit(2)
                .collect(Collectors.toList()));
        todoTasks.addAll(selectRankedTasks(todoCandidates, 6, bucketLimitMap(
                "manual", 3,
                "resume", 1,
                "exam", 2,
                "course", 1,
                "wrongbook", 0)));

        List<Map<String, Object>> recommendedTasks = new ArrayList<>();
        List<Map<String, Object>> recommendationPool = new ArrayList<>();
        recommendationPool.addAll(pendingManualTasks);
        recommendationPool.addAll(examTasks);
        recommendationPool.addAll(wrongTasks);
        recommendationPool.addAll(courseTasks);
        recommendedTasks.addAll(selectRankedTasks(recommendationPool, 10, bucketLimitMap(
                "resume", 1,
                "manual", 3,
                "exam", 3,
                "wrongbook", 2,
                "course", 2)));

        List<Map<String, Object>> homepageTasks = selectHomepageTasks(todoTasks, recommendedTasks);

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
        stats.put("manualTaskCount", manualTasks.size());
        stats.put("todoCount", todoTasks.size());
        stats.put("recommendedCount", recommendedTasks.size());
        stats.put("homepageCount", homepageTasks.size());
        stats.put("urgentCount", homepageTasks.stream().filter(item -> getTaskScore(item) >= 85).count());
        stats.put("dueSoonCount", pendingManualTasks.stream().filter(item -> "HIGH".equals(item.get("priorityLevel")) || "URGENT".equals(item.get("priorityLevel"))).count());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todoTasks", todoTasks);
        result.put("recommendedTasks", recommendedTasks);
        result.put("homepageTasks", homepageTasks);
        result.put("historyTasks", historyTasks);
        result.put("examTasks", examTasks);
        result.put("wrongTasks", wrongTasks);
        result.put("courseTasks", courseTasks);
        result.put("manualTasks", manualTasks);
        result.put("recommendationGroups", buildRecommendationGroups(recommendedTasks));
        result.put("recommendationSummary", buildTaskSummary(todoTasks, recommendedTasks, homepageTasks));
        result.put("feedbackProfile", taskFeedbackProfile);
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
    @PutMapping("/student/task-feedback")
    public AjaxResult recordStudentTaskFeedback(@RequestBody(required = false) Map<String, Object> payload) {
        LoginUser loginUser = getLoginUser();
        Long userId = loginUser == null ? null : loginUser.getUserId();
        if (userId == null) {
            return error("用户信息不存在");
        }
        String feedbackType = StringUtils.upperCase(StringUtils.trimToEmpty(payload == null ? null : String.valueOf(payload.get("feedbackType"))));
        if (StringUtils.isEmpty(feedbackType)) {
            feedbackType = "OPEN";
        }
        String taskKey = StringUtils.trimToEmpty(payload == null ? null : String.valueOf(payload.get("taskKey")));
        String sourceType = StringUtils.lowerCase(StringUtils.trimToEmpty(payload == null ? null : String.valueOf(payload.get("sourceType"))));
        String page = StringUtils.lowerCase(StringUtils.trimToEmpty(payload == null ? null : String.valueOf(payload.get("page"))));
        if (StringUtils.isEmpty(page)) {
            page = "dashboard";
        }
        ScStudyRecord record = new ScStudyRecord();
        record.setUserId(userId);
        record.setCourseId(asLong(payload == null ? null : payload.get("courseId")));
        record.setResourceId(asLong(payload == null ? null : payload.get("targetId")));
        record.setBehaviorType("TASK_" + feedbackType);
        record.setDurationSeconds(1);
        record.setSourcePage(buildTaskFeedbackSourcePage(page, sourceType, taskKey));
        record.setDeviceType("web");
        scStudyRecordService.recordBehavior(record);
        return success();
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

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,campus_head_teacher,advisor,head_teacher,admin')")
    @GetMapping("/messages")
    public AjaxResult portalMessages(@RequestParam(required = false) Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "12") Integer limit,
            @RequestParam(required = false, defaultValue = "false") Boolean includeRead) {
        LoginUser loginUser = getLoginUser();
        Long effectiveUserId = loginUser == null ? userId : loginUser.getUserId();
        List<Map<String, Object>> result = new ArrayList<>();
        String receiverScope = resolvePortalReceiverScope(loginUser);
        String readFlag = Boolean.TRUE.equals(includeRead) ? null : "0";
        int resultLimit = limit == null || limit <= 0 ? 12 : limit;

        List<SysNotice> notices = noticeService.selectPortalNoticeList(effectiveUserId, receiverScope, "MESSAGE", readFlag, keyword, resultLimit);
        for (SysNotice notice : notices) {
            result.add(toPortalNoticeItem(notice, "MESSAGE"));
        }

        if (StringUtils.equals(receiverScope, "STUDENT")) {
            ScExamRecord recordQuery = new ScExamRecord();
            recordQuery.setUserId(effectiveUserId);
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

            Map<String, Object> growth = scUserGrowthService.buildGrowthSummary(effectiveUserId);
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
        }

        result.sort(Comparator.comparing(item -> parseAnyDate(item.get("createTime")), Comparator.nullsLast(Comparator.reverseOrder())));
        return success(result.stream().limit(resultLimit).collect(Collectors.toList()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,campus_head_teacher,advisor,head_teacher,admin')")
    @GetMapping("/message-center")
    public AjaxResult portalMessageCenter(@RequestParam(required = false) Long userId,
            @RequestParam(required = false) String keyword) {
        LoginUser loginUser = getLoginUser();
        Long effectiveUserId = loginUser == null ? userId : loginUser.getUserId();
        String receiverScope = resolvePortalReceiverScope(loginUser);
        List<SysNotice> unreadMessages = noticeService.selectPortalNoticeList(effectiveUserId, receiverScope, "MESSAGE", null, keyword, 100);
        List<SysNotice> notices = noticeService.selectPortalNoticeList(effectiveUserId, receiverScope, "NOTICE", null, keyword, 8);
        Map<String, Object> stats = noticeService.selectPortalNoticeStats(effectiveUserId, receiverScope);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("messages", unreadMessages.stream().map(item -> toPortalNoticeItem(item, "MESSAGE")).collect(Collectors.toList()));
        result.put("notices", notices.stream().map(item -> toPortalNoticeItem(item, "NOTICE")).collect(Collectors.toList()));
        result.put("stats", stats == null ? Collections.emptyMap() : stats);
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,campus_head_teacher,advisor,head_teacher,admin')")
    @PutMapping("/message-center/{noticeId}/read")
    public AjaxResult markPortalMessageRead(@PathVariable Long noticeId) {
        LoginUser loginUser = getLoginUser();
        return toAjax(noticeService.markNoticeRead(noticeId, loginUser.getUserId(), getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/my-schedule")
    public AjaxResult studentMySchedule(@RequestParam Long userId, @RequestParam(required = false) Long termId) {
        termId = termId == null ? resolveCurrentPortalTermId() : termId;
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        if (profile == null || profile.getClassId() == null) {
            return success(Collections.emptyMap());
        }

        List<ScClassCourse> classCourses = loadStudentSelectedClassCourses(userId, termId, true);
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
    @GetMapping("/student/class-schedule")
    public AjaxResult studentClassSchedule(@RequestParam Long userId, @RequestParam(required = false) Long termId) {
        termId = termId == null ? resolveCurrentPortalTermId() : termId;
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        if (profile == null || profile.getClassId() == null) {
            return success(Collections.emptyMap());
        }
        List<ScClassCourse> classCourses = loadStudentClassCourses(profile.getClassId(), termId);
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
        Long effectiveTermId = resolveCurrentPortalTermId();
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
        if (profile == null || profile.getClassId() == null) {
            return success(Collections.emptyList());
        }
        List<ScClassCourse> classCourses = loadStudentSelectedClassCourses(userId, effectiveTermId, true);
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

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,campus_head_teacher,advisor,head_teacher,admin')")
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
            item.put("termCode", term.getTermCode());
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
            item.put("courseName", resolveDisplayedCourseName(classCourse));
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
            item.put("actualStudentCount", resolveSelectedStudentCount(classCourse));
            item.put("selectedStudentCount", resolveSelectedStudentCount(classCourse));
            item.put("remark", classCourse.getRemark());
            result.add(item);
        }
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @GetMapping("/teacher/course-students")
    public AjaxResult teacherCourseStudents(@RequestParam Long teacherId,
            @RequestParam Long classCourseId,
            @RequestParam(required = false) Long termId) {
        ScClassCourse classCourse = scClassCourseService.selectScClassCourseById(classCourseId);
        if (classCourse == null || !Objects.equals(classCourse.getTeacherId(), teacherId)) {
            return success(Collections.emptyMap());
        }
        List<ScCourseStudent> students = scCourseStudentService.listTeacherCourseStudents(teacherId, classCourseId, termId);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("classCourseId", classCourse.getId());
        summary.put("courseName", resolveDisplayedCourseName(classCourse));
        summary.put("className", classCourse.getClassName());
        summary.put("termName", classCourse.getTermName());
        summary.put("teachingClassCode", classCourse.getTeachingClassCode());
        summary.put("selectedStudentCount", students.size());
        summary.put("studentLimit", classCourse.getStudentLimit());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", summary);
        result.put("items", students.stream().map(item -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", item.getId());
            row.put("studentUserId", item.getStudentUserId());
            row.put("studentName", item.getStudentName());
            row.put("studentNo", item.getStudentNo());
            row.put("classId", item.getClassId());
            row.put("className", item.getClassName());
            row.put("joinTime", item.getJoinTime());
            row.put("status", item.getStatus());
            row.put("remark", item.getRemark());
            return row;
        }).collect(Collectors.toList()));
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

    @PreAuthorize("@ss.hasAnyRoles('campus_head_teacher,advisor,head_teacher,admin')")
    @GetMapping("/advisor/dashboard")
    public AjaxResult advisorDashboard(@RequestParam Long advisorUserId,
            @RequestParam(required = false) Long termId) {
        if (!canOperateAdvisorPortal(advisorUserId)) {
            return error("无权查看其他辅导员的数据");
        }
        Long effectiveTermId = termId == null ? resolveCurrentPortalTermId() : termId;
        List<ScUserProfile> advisorProfiles = loadAdvisorStudentProfiles(advisorUserId);
        List<ScClass> advisorClasses = loadAdvisorClasses(advisorUserId, advisorProfiles);
        List<Map<String, Object>> advisorStudents = buildAdvisorStudentRows(advisorProfiles, advisorClasses, null, null);
        List<ScStudentScore> advisorScores = loadAdvisorScores(advisorProfiles, effectiveTermId, null, null);
        List<ScClassCourse> advisorClassCourses = loadAdvisorClassCourses(advisorClasses, effectiveTermId);

        long passCount = advisorScores.stream().filter(item -> "1".equals(item.getPassFlag())).count();
        long publishedCount = advisorScores.stream().filter(item -> "1".equals(item.getPublishStatus())).count();
        BigDecimal avgScore = averageScoreValue(advisorScores.stream()
                .map(ScStudentScore::getTotalScore)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("advisorUserId", advisorUserId);
        result.put("termId", effectiveTermId);
        result.put("termLabel", resolveTermLabel(effectiveTermId));
        result.put("classCount", advisorClasses.size());
        result.put("studentCount", advisorStudents.size());
        result.put("courseCount", advisorClassCourses.stream().map(ScClassCourse::getCourseId).filter(Objects::nonNull).distinct().count());
        result.put("scoreCount", advisorScores.size());
        result.put("avgScore", avgScore);
        result.put("passCount", passCount);
        result.put("failCount", Math.max(0, advisorScores.size() - passCount));
        result.put("publishedCount", publishedCount);
        result.put("unpublishedCount", Math.max(0, advisorScores.size() - publishedCount));
        result.put("classOptions", buildAdvisorClassOptions(advisorClasses));
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('campus_head_teacher,advisor,head_teacher,admin')")
    @GetMapping("/advisor/students")
    public AjaxResult advisorStudents(@RequestParam Long advisorUserId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String keyword) {
        if (!canOperateAdvisorPortal(advisorUserId)) {
            return error("无权查看其他辅导员的数据");
        }
        List<ScUserProfile> advisorProfiles = loadAdvisorStudentProfiles(advisorUserId);
        List<ScClass> advisorClasses = loadAdvisorClasses(advisorUserId, advisorProfiles);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("classOptions", buildAdvisorClassOptions(advisorClasses));
        result.put("items", buildAdvisorStudentRows(advisorProfiles, advisorClasses, classId, keyword));
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('campus_head_teacher,advisor,head_teacher,admin')")
    @GetMapping("/advisor/scores")
    public AjaxResult advisorScores(@RequestParam Long advisorUserId,
            @RequestParam(required = false) Long termId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String keyword) {
        if (!canOperateAdvisorPortal(advisorUserId)) {
            return error("无权查看其他辅导员的数据");
        }
        Long effectiveTermId = termId == null ? resolveCurrentPortalTermId() : termId;
        List<ScUserProfile> advisorProfiles = loadAdvisorStudentProfiles(advisorUserId);
        List<ScClass> advisorClasses = loadAdvisorClasses(advisorUserId, advisorProfiles);
        List<ScStudentScore> rows = loadAdvisorScores(advisorProfiles, effectiveTermId, classId, keyword);
        long passCount = rows.stream().filter(item -> "1".equals(item.getPassFlag())).count();
        long publishedCount = rows.stream().filter(item -> "1".equals(item.getPublishStatus())).count();
        BigDecimal avgScore = averageScoreValue(rows.stream()
                .map(ScStudentScore::getTotalScore)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("scoreCount", rows.size());
        overview.put("studentCount", rows.stream().map(ScStudentScore::getStudentUserId).filter(Objects::nonNull).distinct().count());
        overview.put("classCount", rows.stream().map(ScStudentScore::getClassId).filter(Objects::nonNull).distinct().count());
        overview.put("avgScore", avgScore);
        overview.put("passCount", passCount);
        overview.put("failCount", Math.max(0, rows.size() - passCount));
        overview.put("publishedCount", publishedCount);
        overview.put("unpublishedCount", Math.max(0, rows.size() - publishedCount));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("termId", effectiveTermId);
        result.put("termLabel", resolveTermLabel(effectiveTermId));
        result.put("classOptions", buildAdvisorClassOptions(advisorClasses));
        result.put("overview", overview);
        result.put("items", rows);
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
    @GetMapping("/parent/children")
    public AjaxResult parentChildren(@RequestParam Long parentUserId) {
        if (!canOperateParentPortal(parentUserId)) {
            return error("无权查看其他家长的数据");
        }
        ScParentStudentRel query = new ScParentStudentRel();
        query.setParentUserId(parentUserId);
        List<ScParentStudentRel> relList = scParentStudentRelService.selectScParentStudentRelList(query);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ScParentStudentRel rel : relList) {
            if (rel == null || rel.getStudentUserId() == null) {
                continue;
            }
            ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(rel.getStudentUserId());
            ScClass scClass = profile == null || profile.getClassId() == null ? null : scClassService.selectScClassByClassId(profile.getClassId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("value", rel.getStudentUserId());
            item.put("studentUserId", rel.getStudentUserId());
            item.put("studentName", resolveProfileName(rel.getStudentUserId(), profile));
            item.put("studentNo", resolveProfileStudentNo(rel.getStudentUserId(), profile));
            item.put("classId", profile == null ? null : profile.getClassId());
            item.put("className", scClass == null ? null : scClass.getClassName());
            item.put("major", profile == null ? null : profile.getMajor());
            item.put("relationType", rel.getRelationType());
            item.put("label", buildParentChildLabel(item));
            result.add(item);
        }
        return success(result);
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

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,campus_head_teacher,advisor,head_teacher,admin')")
    @GetMapping("/notice")
    public AjaxResult noticeList(@RequestParam(required = false, defaultValue = "6") Integer limit,
            @RequestParam(required = false) String keyword) {
        LoginUser loginUser = getLoginUser();
        String receiverScope = resolvePortalReceiverScope(loginUser);
        List<SysNotice> notices = noticeService.selectPortalNoticeList(loginUser.getUserId(), receiverScope, "NOTICE", null, keyword, limit);
        return success(notices.stream().map(item -> toPortalNoticeItem(item, "NOTICE")).collect(Collectors.toList()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,campus_head_teacher,advisor,head_teacher,admin')")
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
        ScUserProfile scProfile = scUserProfileService.selectScUserProfileByUserId(loginUser.getUserId());
        if (scProfile != null) {
            scProfile.setClassName(resolveClassName(scProfile.getClassId()));
        }
        data.put("profile", scProfile);
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
        return StringUtils.defaultIfEmpty(profile == null ? null : profile.getRealName(), student == null ? null : student.getNickName());
    }

    private String resolveProfileStudentNo(Long userId, ScUserProfile profile) {
        SysUser student = userService.selectUserById(userId);
        return StringUtils.defaultIfEmpty(profile == null ? null : profile.getStudentNo(), student == null ? null : student.getUserName());
    }

    private boolean canOperateStudentSelection(Long userId) {
        LoginUser loginUser = getLoginUser();
        Long currentUserId = loginUser == null ? null : loginUser.getUserId();
        return SecurityUtils.isAdmin() || Objects.equals(currentUserId, userId);
    }

    private boolean canOperateAdvisorPortal(Long advisorUserId) {
        LoginUser loginUser = getLoginUser();
        Long currentUserId = loginUser == null ? null : loginUser.getUserId();
        return SecurityUtils.isAdmin() || Objects.equals(currentUserId, advisorUserId);
    }

    private boolean canOperateParentPortal(Long parentUserId) {
        LoginUser loginUser = getLoginUser();
        Long currentUserId = loginUser == null ? null : loginUser.getUserId();
        return SecurityUtils.isAdmin() || Objects.equals(currentUserId, parentUserId);
    }

    private List<ScClass> loadAdvisorHeadTeacherClasses(Long advisorUserId) {
        if (advisorUserId == null) {
            return Collections.emptyList();
        }
        ScClass query = new ScClass();
        query.setHeadTeacherId(advisorUserId);
        query.setStatus("0");
        List<ScClass> classList = scClassService.selectScClassList(query);
        classList.sort(Comparator.comparing(item -> StringUtils.defaultString(item.getClassName())));
        return classList;
    }

    private List<ScUserProfile> loadAdvisorStudentProfiles(Long advisorUserId) {
        if (advisorUserId == null) {
            return Collections.emptyList();
        }
        Map<Long, ScUserProfile> profileMap = new LinkedHashMap<>();

        ScUserProfile directQuery = new ScUserProfile();
        directQuery.setUserType("student");
        directQuery.setStatus("0");
        directQuery.setAdvisorUserId(advisorUserId);
        for (ScUserProfile profile : scUserProfileService.selectScUserProfileList(directQuery)) {
            if (profile == null || profile.getUserId() == null) {
                continue;
            }
            profileMap.put(profile.getUserId(), profile);
        }

        for (ScClass scClass : loadAdvisorHeadTeacherClasses(advisorUserId)) {
            if (scClass == null || scClass.getClassId() == null) {
                continue;
            }
            ScUserProfile fallbackQuery = new ScUserProfile();
            fallbackQuery.setUserType("student");
            fallbackQuery.setClassId(scClass.getClassId());
            fallbackQuery.setStatus("0");
            for (ScUserProfile profile : scUserProfileService.selectScUserProfileList(fallbackQuery)) {
                if (profile == null || profile.getUserId() == null) {
                    continue;
                }
                if (profile.getAdvisorUserId() != null && !Objects.equals(profile.getAdvisorUserId(), advisorUserId)) {
                    continue;
                }
                profileMap.putIfAbsent(profile.getUserId(), profile);
            }
        }

        List<ScUserProfile> profiles = new ArrayList<>(profileMap.values());
        profiles.sort(Comparator.comparing(item -> StringUtils.defaultString(item.getRealName())));
        return profiles;
    }

    private List<ScClass> loadAdvisorClasses(Long advisorUserId, List<ScUserProfile> advisorProfiles) {
        Map<Long, ScClass> classMap = new LinkedHashMap<>();
        for (ScClass scClass : loadAdvisorHeadTeacherClasses(advisorUserId)) {
            if (scClass != null && scClass.getClassId() != null) {
                classMap.put(scClass.getClassId(), scClass);
            }
        }
        if (advisorProfiles != null) {
            for (ScUserProfile profile : advisorProfiles) {
                if (profile == null || profile.getClassId() == null) {
                    continue;
                }
                classMap.computeIfAbsent(profile.getClassId(), classId -> scClassService.selectScClassByClassId(classId));
            }
        }
        List<ScClass> classList = classMap.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
        classList.sort(Comparator.comparing(item -> StringUtils.defaultString(item.getClassName())));
        return classList;
    }

    private List<ScClassCourse> loadAdvisorClassCourses(List<ScClass> advisorClasses, Long termId) {
        if (advisorClasses == null || advisorClasses.isEmpty()) {
            return Collections.emptyList();
        }
        List<ScClassCourse> result = new ArrayList<>();
        for (ScClass scClass : advisorClasses) {
            if (scClass == null || scClass.getClassId() == null) {
                continue;
            }
            ScClassCourse query = new ScClassCourse();
            query.setClassId(scClass.getClassId());
            query.setTermId(termId);
            query.setStatus("0");
            result.addAll(scClassCourseService.selectScClassCourseList(query));
        }
        return result;
    }

    private List<Map<String, Object>> buildAdvisorClassOptions(List<ScClass> advisorClasses) {
        if (advisorClasses == null) {
            return Collections.emptyList();
        }
        return advisorClasses.stream().map(item -> {
            Map<String, Object> option = new LinkedHashMap<>();
            option.put("value", item.getClassId());
            option.put("label", item.getClassName());
            option.put("classId", item.getClassId());
            option.put("className", item.getClassName());
            option.put("deptId", item.getDeptId());
            option.put("deptName", item.getDeptName());
            return option;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildAdvisorStudentRows(List<ScUserProfile> advisorProfiles, List<ScClass> advisorClasses, Long classId, String keyword) {
        if (advisorProfiles == null || advisorProfiles.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, ScClass> classMap = advisorClasses.stream()
                .filter(item -> item != null && item.getClassId() != null)
                .collect(Collectors.toMap(ScClass::getClassId, item -> item, (left, right) -> left, LinkedHashMap::new));
        List<Map<String, Object>> result = new ArrayList<>();
        String keywordText = StringUtils.trimToEmpty(keyword).toLowerCase();
        for (ScUserProfile profile : advisorProfiles) {
            if (profile == null || profile.getUserId() == null) {
                continue;
            }
            if (classId != null && !Objects.equals(profile.getClassId(), classId)) {
                continue;
            }
            ScClass scClass = profile.getClassId() == null ? null : classMap.get(profile.getClassId());
            String studentName = resolveProfileName(profile.getUserId(), profile);
            String studentNo = resolveProfileStudentNo(profile.getUserId(), profile);
            String searchable = String.join(" ",
                    StringUtils.defaultString(studentName),
                    StringUtils.defaultString(studentNo),
                    StringUtils.defaultString(profile.getMajor()),
                    StringUtils.defaultString(scClass == null ? null : scClass.getClassName())).toLowerCase();
            if (StringUtils.isNotEmpty(keywordText) && !searchable.contains(keywordText)) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("studentUserId", profile.getUserId());
            row.put("studentName", studentName);
            row.put("studentNo", studentNo);
            row.put("gender", profile.getGender());
            row.put("major", profile.getMajor());
            row.put("admissionYear", profile.getAdmissionYear());
            row.put("classId", profile.getClassId());
            row.put("className", scClass == null ? null : scClass.getClassName());
            row.put("deptName", scClass == null ? null : scClass.getDeptName());
            row.put("status", profile.getStatus());
            row.put("avatarUrl", profile.getAvatarUrl());
            row.put("advisorUserId", profile.getAdvisorUserId());
            row.put("bindingMode", profile.getAdvisorUserId() == null ? "CLASS_HEAD_TEACHER" : "DIRECT");
            result.add(row);
        }
        result.sort(Comparator.comparing((Map<String, Object> item) -> String.valueOf(item.get("className")))
                .thenComparing((Map<String, Object> item) -> String.valueOf(item.get("studentName"))));
        return result;
    }

    private List<ScStudentScore> loadAdvisorScores(List<ScUserProfile> advisorProfiles, Long termId, Long classId, String keyword) {
        if (advisorProfiles == null || advisorProfiles.isEmpty()) {
            return Collections.emptyList();
        }
        List<ScUserProfile> filteredProfiles = advisorProfiles.stream()
                .filter(item -> classId == null || Objects.equals(item.getClassId(), classId))
                .collect(Collectors.toList());
        if (filteredProfiles.isEmpty()) {
            return Collections.emptyList();
        }
        List<ScStudentScore> result = new ArrayList<>();
        for (ScUserProfile profile : filteredProfiles) {
            if (profile == null || profile.getUserId() == null) {
                continue;
            }
            ScStudentScore query = new ScStudentScore();
            query.setStudentUserId(profile.getUserId());
            query.setTermId(termId);
            query.setStatus("0");
            result.addAll(scStudentScoreService.selectScStudentScoreList(query));
        }
        String keywordText = StringUtils.trimToEmpty(keyword).toLowerCase();
        if (StringUtils.isNotEmpty(keywordText)) {
            result = result.stream().filter(item -> String.join(" ",
                    StringUtils.defaultString(item.getStudentName()),
                    StringUtils.defaultString(item.getStudentNo()),
                    StringUtils.defaultString(item.getCourseName()),
                    StringUtils.defaultString(item.getClassName()),
                    StringUtils.defaultString(item.getTeachingClassCode())).toLowerCase().contains(keywordText))
                    .collect(Collectors.toList());
        }
        result.sort(Comparator.comparing(ScStudentScore::getClassName, Comparator.nullsLast(String::compareTo))
                .thenComparing(ScStudentScore::getStudentName, Comparator.nullsLast(String::compareTo))
                .thenComparing(ScStudentScore::getCourseName, Comparator.nullsLast(String::compareTo)));
        return result;
    }

    private BigDecimal averageScoreValue(List<BigDecimal> scores) {
        if (scores == null || scores.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal score : scores) {
            total = total.add(score == null ? BigDecimal.ZERO : score);
        }
        return total.divide(BigDecimal.valueOf(scores.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    private String resolveTermLabel(Long termId) {
        if (termId == null) {
            return "全部学期";
        }
        ScSchoolTerm term = scSchoolTermService.selectScSchoolTermByTermId(termId);
        return term == null ? String.valueOf(termId) : buildTermLabel(term);
    }

    private String buildParentChildLabel(Map<String, Object> item) {
        String studentName = String.valueOf(item.getOrDefault("studentName", "孩子"));
        String className = String.valueOf(item.getOrDefault("className", ""));
        String relationType = String.valueOf(item.getOrDefault("relationType", ""));
        List<String> parts = new ArrayList<>();
        if (StringUtils.isNotEmpty(className) && !"null".equalsIgnoreCase(className)) {
            parts.add(className);
        }
        if (StringUtils.isNotEmpty(relationType) && !"null".equalsIgnoreCase(relationType)) {
            parts.add(relationType);
        }
        return parts.isEmpty() ? studentName : studentName + " · " + String.join(" / ", parts);
    }

    private List<ScClassCourse> loadStudentSelectedClassCourses(Long userId, Long termId, boolean autoFillRequired) {
        List<ScCourseStudent> selections = scCourseStudentService.listStudentCourseSelections(userId, termId, autoFillRequired);
        List<ScClassCourse> classCourses = new ArrayList<>();
        for (ScCourseStudent item : selections) {
            if (item == null || item.getClassCourseId() == null) {
                continue;
            }
            ScClassCourse classCourse = scClassCourseService.selectScClassCourseById(item.getClassCourseId());
            if (classCourse == null || !"0".equals(StringUtils.defaultIfEmpty(classCourse.getStatus(), "0"))) {
                continue;
            }
            classCourses.add(classCourse);
        }
        return classCourses;
    }

    private List<ScClassCourse> loadStudentClassCourses(Long classId, Long termId) {
        if (classId == null) {
            return Collections.emptyList();
        }
        ScClassCourse query = new ScClassCourse();
        query.setClassId(classId);
        query.setTermId(termId);
        query.setStatus("0");
        return scClassCourseService.selectScClassCourseList(query);
    }

    private List<ScClassCourse> loadActiveTermClassCourses(Long termId) {
        ScClassCourse query = new ScClassCourse();
        query.setTermId(termId);
        query.setStatus("0");
        return scClassCourseService.selectScClassCourseList(query);
    }

    private boolean canStudentPreviewCourse(Long userId, ScClassCourse classCourse) {
        if (classCourse == null || classCourse.getId() == null) {
            return false;
        }
        if (scCourseStudentService.findActiveSelection(userId, classCourse.getId()) != null) {
            return true;
        }
        return scCourseStudentService.listAvailableClassCoursesForStudent(userId, classCourse.getTermId()).stream()
                .anyMatch(item -> Objects.equals(item.getId(), classCourse.getId()));
    }

    private int resolveSelectedStudentCount(ScClassCourse classCourse) {
        if (classCourse == null || classCourse.getId() == null) {
            return 0;
        }
        int count = scCourseStudentService.countActiveByClassCourseId(classCourse.getId());
        if (count == 0
                && "Y".equalsIgnoreCase(StringUtils.defaultIfEmpty(classCourse.getRequiredFlag(), "N"))
                && classCourse.getActualStudentCount() != null) {
            return classCourse.getActualStudentCount();
        }
        return count;
    }

    private Map<String, Object> buildStudentCourseSelectionItem(ScClassCourse classCourse, ScSchoolTerm term,
            List<ScCourseSchedule> schedules, ScUserProfile profile, boolean selected) {
        Map<String, Object> item = buildStudentCourseItem(classCourse, term, schedules);
        int selectedCount = resolveSelectedStudentCount(classCourse);
        boolean requiredSelection = profile != null
                && Objects.equals(profile.getClassId(), classCourse.getClassId())
                && "Y".equalsIgnoreCase(StringUtils.defaultIfEmpty(classCourse.getRequiredFlag(), "N"));
        boolean capacityLimited = classCourse.getStudentLimit() != null && classCourse.getStudentLimit() > 0;
        boolean capacityFull = capacityLimited && selectedCount >= classCourse.getStudentLimit() && !selected;
        item.put("actualStudentCount", selectedCount);
        item.put("selectedStudentCount", selectedCount);
        item.put("selected", selected);
        item.put("requiredSelection", requiredSelection);
        item.put("canSelect", !selected && !capacityFull);
        item.put("canDrop", selected && !requiredSelection);
        item.put("remainingSeats", capacityLimited ? Math.max(classCourse.getStudentLimit() - selectedCount, 0) : null);
        item.put("actionDisabledReason", requiredSelection ? "本班必修课默认保留，不支持退选" : (capacityFull ? "人数已满" : ""));
        return item;
    }

    private Long resolveCurrentPortalTermId() {
        ScSchoolTerm query = new ScSchoolTerm();
        query.setStatus("0");
        List<ScSchoolTerm> terms = scSchoolTermService.selectScSchoolTermList(query);
        ScSchoolTerm current = terms.stream()
                .filter(item -> "1".equals(String.valueOf(item.getIsCurrent())))
                .findFirst()
                .orElse(terms.isEmpty() ? null : terms.get(0));
        return current == null ? null : current.getTermId();
    }

    private ScCourseSelectionPlan resolveSelectionPlan(Long termId) {
        return scCourseSelectionPlanService.resolveActivePlan(termId);
    }

    private boolean isSelectionWindowOpen(ScCourseSelectionPlan plan) {
        return isWindowOpen(plan == null ? null : plan.getSelectionStartTime(), plan == null ? null : plan.getSelectionEndTime());
    }

    private boolean isDropWindowOpen(ScCourseSelectionPlan plan) {
        return isWindowOpen(plan == null ? null : plan.getDropStartTime(), plan == null ? null : plan.getDropEndTime());
    }

    private boolean isRequestWindowOpen(ScCourseSelectionPlan plan) {
        return isWindowOpen(plan == null ? null : plan.getRequestStartTime(), plan == null ? null : plan.getRequestEndTime());
    }

    private boolean isWindowOpen(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }
        Date now = new Date();
        return !now.before(startTime) && !now.after(endTime);
    }

    private Map<String, Object> buildSelectionPlanVm(ScCourseSelectionPlan plan) {
        if (plan == null) {
            return rowMap(
                    "enabled", false,
                    "planName", "暂未配置选课计划",
                    "noticeContent", "",
                    "ruleContent", "",
                    "selectionOpen", false,
                    "dropOpen", false,
                    "requestOpen", false);
        }
        return rowMap(
                "enabled", "0".equals(String.valueOf(plan.getStatus())),
                "planId", plan.getPlanId(),
                "planName", plan.getPlanName(),
                "termId", plan.getTermId(),
                "termName", plan.getTermName(),
                "planType", plan.getPlanType(),
                "noticeContent", plan.getNoticeContent(),
                "ruleContent", plan.getRuleContent(),
                "selectionStartTime", plan.getSelectionStartTime(),
                "selectionEndTime", plan.getSelectionEndTime(),
                "dropStartTime", plan.getDropStartTime(),
                "dropEndTime", plan.getDropEndTime(),
                "requestStartTime", plan.getRequestStartTime(),
                "requestEndTime", plan.getRequestEndTime(),
                "selectionOpen", isSelectionWindowOpen(plan),
                "dropOpen", isDropWindowOpen(plan),
                "requestOpen", isRequestWindowOpen(plan));
    }

    private Map<String, List<ScClassCourse>> buildSelectedGroupMap(List<ScClassCourse> classCourses) {
        Map<String, List<ScClassCourse>> result = new LinkedHashMap<>();
        if (classCourses == null) {
            return result;
        }
        for (ScClassCourse classCourse : classCourses) {
            if (classCourse == null) {
                continue;
            }
            String groupCode = StringUtils.trimToEmpty(classCourse.getSelectionGroupCode());
            if (StringUtils.isEmpty(groupCode)) {
                continue;
            }
            result.computeIfAbsent(groupCode, key -> new ArrayList<>()).add(classCourse);
        }
        return result;
    }

    private String resolveGroupSelectionLimitReason(ScClassCourse classCourse,
            Map<String, List<ScClassCourse>> selectedGroupMap) {
        if (classCourse == null || selectedGroupMap == null) {
            return null;
        }
        String groupCode = StringUtils.trimToEmpty(classCourse.getSelectionGroupCode());
        if (StringUtils.isEmpty(groupCode)) {
            return null;
        }
        List<ScClassCourse> selectedCourses = selectedGroupMap.getOrDefault(groupCode, Collections.emptyList());
        int groupLimit = classCourse.getSelectionGroupLimit() == null || classCourse.getSelectionGroupLimit() <= 0
                ? 1
                : classCourse.getSelectionGroupLimit();
        if (selectedCourses.size() < groupLimit) {
            return null;
        }
        String selectedNames = selectedCourses.stream()
                .map(this::resolveDisplayedCourseName)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.joining("、"));
        String groupName = StringUtils.defaultIfEmpty(StringUtils.trimToEmpty(classCourse.getSelectionGroupName()), groupCode);
        return "专项组【" + groupName + "】最多可选 " + groupLimit + " 门，当前已选《"
                + StringUtils.defaultIfEmpty(selectedNames, "同组课程") + "》";
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
        result.put("courseName", resolveDisplayedCourseName(classCourse));
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
        result.put("courseName", resolveDisplayedCourseName(classCourse));
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
        result.put("studentCount", resolveSelectedStudentCount(classCourse));
        // Fields needed by frontend for specialty / combined-class merging
        result.put("baseCourseName", classCourse.getCourseName());
        result.put("selectionOptionName", classCourse.getSelectionOptionName());
        result.put("selectionGroupCode", classCourse.getSelectionGroupCode());
        result.put("combinedClassCode", classCourse.getCombinedClassCode());
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
        Map<String, Object> scheduleVm = buildScheduleTextVm(classCourse, schedules);
        int selectedCount = resolveSelectedStudentCount(classCourse);
        Integer capacityLimit = classCourse.getStudentLimit();
        Integer capacityPercent = capacityLimit != null && capacityLimit > 0
                ? Math.max(0, Math.min(100, Math.round(selectedCount * 100f / capacityLimit)))
                : null;
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", classCourse.getId());
        item.put("classId", classCourse.getClassId());
        item.put("className", classCourse.getClassName());
        item.put("courseId", classCourse.getCourseId());
        item.put("courseName", resolveDisplayedCourseName(classCourse));
        item.put("baseCourseName", classCourse.getCourseName());
        item.put("selectionOptionName", classCourse.getSelectionOptionName());
        item.put("selectionGroupCode", classCourse.getSelectionGroupCode());
        item.put("selectionGroupName", classCourse.getSelectionGroupName());
        item.put("selectionGroupLimit", classCourse.getSelectionGroupLimit());
        item.put("courseCode", course == null ? null : course.getCourseCode());
        item.put("subjectType", course == null ? null : course.getSubjectType());
        item.put("intro", course == null ? null : course.getIntro());
        item.put("teacherId", classCourse.getTeacherId());
        item.put("teacherName", StringUtils.defaultIfEmpty(classCourse.getTeacherName(),
                resolveTeacherName(classCourse.getTeacherId())));
        item.put("termId", classCourse.getTermId());
        item.put("termName", classCourse.getTermName());
        item.put("schoolYear", term == null ? null : term.getSchoolYear());
        item.put("businessType", classCourse.getBusinessType());
        item.put("teachingClassCode", classCourse.getTeachingClassCode());
        item.put("teachingClassName", resolveTeachingClassName(classCourse));
        item.put("credits", classCourse.getCredits() == null ? estimateCredits(course, classCourse) : classCourse.getCredits());
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
        item.put("actualStudentCount", selectedCount);
        item.put("selectedStudentCount", selectedCount);
        item.put("selectedCount", selectedCount);
        item.put("capacityLimit", capacityLimit);
        item.put("capacityPercent", capacityPercent);
        item.put("remainingSeats", capacityLimit != null && capacityLimit > 0 ? Math.max(capacityLimit - selectedCount, 0) : null);
        item.put("studentCountText", capacityLimit != null && capacityLimit > 0 ? selectedCount + "/" + capacityLimit : String.valueOf(selectedCount));
        item.put("remark", classCourse.getRemark());
        item.put("semesterLabel", term == null ? classCourse.getTermName() : buildTermLabel(term));
        item.put("scheduleVm", scheduleVm);
        item.put("scheduleText", resolveTextBlockValue(scheduleVm.get("dateTimeText")));
        item.put("schedulePlaceText", resolveTextBlockValue(scheduleVm.get("dateTimePlaceText")));
        item.put("scheduleTeacherText", resolveTextBlockValue(scheduleVm.get("dateTimePlacePersonText")));
        item.put("classroom", resolveTextBlockValue(scheduleVm.get("roomSeatText")));
        item.put("scheduleDetails", buildCourseScheduleDetails(classCourse, schedules, term));
        return item;
    }

    private boolean matchesCreditsRange(Object value, BigDecimal exactCredits, BigDecimal minCredits, BigDecimal maxCredits) {
        BigDecimal creditValue = toBigDecimal(value);
        if (creditValue == null) {
            return false;
        }
        if (exactCredits != null) {
            return creditValue.compareTo(exactCredits) == 0;
        }
        if (minCredits != null && creditValue.compareTo(minCredits) < 0) {
            return false;
        }
        if (maxCredits != null && creditValue.compareTo(maxCredits) > 0) {
            return false;
        }
        return true;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private List<Map<String, Object>> buildCourseOfferingTeacherOptions(List<ScClassCourse> classCourses) {
        LinkedHashMap<Long, String> teacherMap = new LinkedHashMap<>();
        for (ScClassCourse classCourse : classCourses) {
            Long teacherId = classCourse.getTeacherId();
            if (teacherId == null) {
                continue;
            }
            String teacherName = StringUtils.defaultIfEmpty(classCourse.getTeacherName(), resolveTeacherName(teacherId));
            if (StringUtils.isEmpty(teacherName)) {
                continue;
            }
            teacherMap.putIfAbsent(teacherId, teacherName);
        }
        return teacherMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> buildOptionItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Map<String, Object>> buildCourseOfferingDeptOptions(List<ScClassCourse> classCourses) {
        Map<Long, String> deptLabelMap = new LinkedHashMap<>();
        SysDept deptQuery = new SysDept();
        deptQuery.setStatus("0");
        for (SysDept dept : sysDeptService.selectDeptList(deptQuery)) {
            if (dept.getDeptId() != null && StringUtils.isNotEmpty(dept.getDeptName())) {
                deptLabelMap.put(dept.getDeptId(), dept.getDeptName());
            }
        }
        LinkedHashSet<Long> deptIds = classCourses.stream()
                .map(ScClassCourse::getOpenDeptId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Long deptId : deptIds) {
            String label = deptLabelMap.get(deptId);
            if (StringUtils.isEmpty(label)) {
                label = classCourses.stream()
                        .filter(item -> Objects.equals(item.getOpenDeptId(), deptId))
                        .map(ScClassCourse::getOpenDeptName)
                        .filter(StringUtils::isNotEmpty)
                        .findFirst()
                        .orElse(null);
            }
            if (StringUtils.isEmpty(label)) {
                continue;
            }
            result.add(buildOptionItem(deptId, label));
        }
        result.sort(Comparator.comparing(item -> String.valueOf(item.get("label"))));
        return result;
    }

    private List<Map<String, Object>> buildDictOptions(String dictType) {
        List<SysDictData> dataList = sysDictTypeService.selectDictDataByType(dictType);
        if (dataList == null || dataList.isEmpty()) {
            return new ArrayList<>();
        }
        return dataList.stream()
                .filter(item -> "0".equals(String.valueOf(item.getStatus())))
                .sorted(Comparator.comparing(item -> Optional.ofNullable(item.getDictSort()).orElse(0L)))
                .map(item -> buildOptionItem(item.getDictValue(), item.getDictLabel()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Map<String, Object>> buildDistinctTextOptions(Collection<String> values) {
        return values == null ? new ArrayList<>() : values.stream()
                .filter(StringUtils::isNotEmpty)
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .sorted()
                .map(value -> buildOptionItem(value, value))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Map<String, Object>> mergeOptionLists(List<Map<String, Object>>... optionGroups) {
        Map<String, Map<String, Object>> merged = new LinkedHashMap<>();
        if (optionGroups == null) {
            return new ArrayList<>();
        }
        for (List<Map<String, Object>> group : optionGroups) {
            if (group == null) {
                continue;
            }
            for (Map<String, Object> item : group) {
                if (item == null) {
                    continue;
                }
                Object value = item.get("value");
                Object label = item.get("label");
                if (value == null || StringUtils.isEmpty(String.valueOf(label))) {
                    continue;
                }
                merged.putIfAbsent(String.valueOf(value), buildOptionItem(value, String.valueOf(label)));
            }
        }
        return new ArrayList<>(merged.values());
    }

    private Map<String, Object> buildOptionItem(Object value, String label) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("value", value);
        item.put("label", label);
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

    private Map<String, Object> buildTaskFeedbackProfile(Long userId) {
        if (userId == null) {
            return rowMap("recentSourceOpens", Collections.emptyMap(), "recentTaskOpenMap", Collections.emptyMap());
        }
        ScStudyRecord query = new ScStudyRecord();
        query.setUserId(userId);
        query.setBehaviorType("TASK_OPEN");
        Date threshold = DateUtils.addDays(new Date(), -7);
        Map<String, Integer> sourceCount = new LinkedHashMap<>();
        Map<String, Long> taskOpenMap = new LinkedHashMap<>();
        scStudyRecordService.selectScStudyRecordList(query).stream()
                .filter(item -> item.getCreateTime() != null && !item.getCreateTime().before(threshold))
                .forEach(item -> {
                    Map<String, String> payload = parseTaskFeedbackSourcePage(item.getSourcePage());
                    String sourceType = StringUtils.defaultIfEmpty(payload.get("sourceType"), "other");
                    String taskKey = StringUtils.defaultIfEmpty(payload.get("taskKey"), "");
                    sourceCount.put(sourceType, sourceCount.getOrDefault(sourceType, 0) + 1);
                    if (StringUtils.isNotEmpty(taskKey) && item.getCreateTime() != null) {
                        long timestamp = item.getCreateTime().getTime();
                        taskOpenMap.put(taskKey, Math.max(taskOpenMap.getOrDefault(taskKey, 0L), timestamp));
                    }
                });
        return rowMap(
                "recentSourceOpens", sourceCount,
                "recentTaskOpenMap", taskOpenMap);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> applyBehaviorFeedback(Map<String, Object> task, Map<String, Object> taskFeedbackProfile) {
        if (task == null || taskFeedbackProfile == null) {
            return task;
        }
        Map<String, Integer> sourceOpens = taskFeedbackProfile.get("recentSourceOpens") instanceof Map
                ? (Map<String, Integer>) taskFeedbackProfile.get("recentSourceOpens")
                : Collections.emptyMap();
        Map<String, Long> taskOpenMap = taskFeedbackProfile.get("recentTaskOpenMap") instanceof Map
                ? (Map<String, Long>) taskFeedbackProfile.get("recentTaskOpenMap")
                : Collections.emptyMap();
        String sourceType = String.valueOf(task.getOrDefault("sourceType", "other"));
        String taskKey = String.valueOf(task.getOrDefault("key", ""));
        int score = getTaskScore(task);
        score += Math.min(sourceOpens.getOrDefault(sourceType, 0) * 3, 12);
        Long latestOpenAt = taskOpenMap.get(taskKey);
        if (latestOpenAt != null) {
            long deltaMs = System.currentTimeMillis() - latestOpenAt;
            if (deltaMs <= 1000L * 60L * 60L * 6L) {
                score -= 6;
            } else if (deltaMs >= 1000L * 60L * 60L * 24L) {
                score += 2;
            }
        }
        task.put("score", score);
        return task;
    }

    private String buildTaskFeedbackSourcePage(String page, String sourceType, String taskKey) {
        return String.format("portal_task|%s|%s|%s",
                StringUtils.defaultIfEmpty(page, "dashboard"),
                StringUtils.defaultIfEmpty(sourceType, "other"),
                StringUtils.defaultIfEmpty(taskKey, ""));
    }

    private Map<String, String> parseTaskFeedbackSourcePage(String sourcePage) {
        if (!StringUtils.startsWith(StringUtils.defaultString(sourcePage), "portal_task|")) {
            return Collections.emptyMap();
        }
        String[] parts = String.valueOf(sourcePage).split("\\|", -1);
        if (parts == null || parts.length < 4) {
            return Collections.emptyMap();
        }
        return rowStringMap(
                "page", parts[1],
                "sourceType", parts[2],
                "taskKey", parts[3]);
    }

    private Map<String, Object> buildExamTask(ScExamPaper paper, Map<String, Object> courseInfo, long attemptCount, Date now) {
        String courseName = courseInfo == null ? null : String.valueOf(courseInfo.get("courseName"));
        boolean courseExam = paper.getCourseId() != null;
        Date startTime = paper.getPublishStartTime();
        Date endTime = paper.getPublishEndTime();
        long hoursToEnd = hoursBetween(now, endTime);
        boolean deadlineSoon = endTime != null && hoursToEnd <= 72;
        boolean windowOpen = endTime == null || !endTime.before(now);
        int score = 58;
        if (courseExam) {
            score += 8;
        }
        if (attemptCount > 0) {
            score += 6;
        }
        if (deadlineSoon) {
            score += 18;
        } else if (endTime != null && hoursToEnd <= 168) {
            score += 10;
        }
        if (startTime != null && !startTime.after(now)) {
            score += 6;
        }
        if (!windowOpen) {
            score -= 20;
        }
        List<String> meta = new ArrayList<>();
        meta.add((paper.getDurationMinutes() == null ? 0 : paper.getDurationMinutes()) + " 分钟");
        meta.add("总分 " + StringUtils.defaultIfEmpty(paper.getTotalScore() == null ? null : paper.getTotalScore().toPlainString(), "0"));
        if (endTime != null) {
            meta.add("截止 " + DateUtils.parseDateToStr("MM-dd HH:mm", endTime));
        }
        if (attemptCount > 0) {
            meta.add("已参加 " + attemptCount + " 次");
        }
        String desc = courseExam
                ? StringUtils.defaultIfEmpty(courseName, "当前课程") + " 已开放考试，建议按课程进度完成。"
                : "开放考试任务，可自由参与。";
        if (deadlineSoon && endTime != null) {
            desc = "考试开放窗口即将结束，建议优先完成。";
        } else if (attemptCount > 0) {
            desc = "你已有历史作答记录，可按需再次参加考试。";
        }
        Map<String, Object> item = taskItem("exam-" + paper.getPaperId(),
                StringUtils.defaultIfEmpty(paper.getPaperName(), "试卷 " + paper.getPaperId()),
                desc,
                attemptCount > 0 ? "再次作答" : "考试任务",
                "EXAM",
                deadlineSoon ? "danger" : "primary",
                deadlineSoon ? "临近截止" : "待处理",
                meta,
                actionItem("exam", paper.getPaperId(), null, rowMap(
                        "paperId", paper.getPaperId(),
                        "paperName", paper.getPaperName(),
                        "courseId", paper.getCourseId(),
                        "courseName", courseName,
                        "publishEndTime", paper.getPublishEndTime(),
                        "maxAttemptCount", paper.getMaxAttemptCount()), "/student/exams"));
        return applyTaskSignals(item, "exam", score, deadlineSoon ? "HIGH" : "MEDIUM",
                deadlineSoon ? "考试窗口临近截止，建议优先处理" : courseExam ? "课程考试与当前学习进度直接相关" : "开放考试可作为阶段性自测",
                deadlineSoon ? "deadline" : "exam");
    }

    private Map<String, Object> buildWrongTask(ScWrongQuestionBook wrong, Map<String, Object> courseInfo, Date now) {
        int wrongCount = defaultInt(wrong.getWrongCount());
        boolean mastered = "1".equals(wrong.getMasteryStatus());
        long hoursFromLastWrong = hoursBetween(wrong.getLastWrongTime(), now);
        boolean recentlyWrong = wrong.getLastWrongTime() != null && hoursFromLastWrong <= 72;
        boolean inCooldown = wrong.getLastWrongTime() != null && hoursFromLastWrong <= 12 && wrongCount < 3 && !mastered;
        int score = 32 + Math.min(wrongCount * 8, 32);
        if (!mastered) {
            score += 18;
        }
        if (recentlyWrong) {
            score += 10;
        }
        if (inCooldown) {
            score -= 18;
        }
        List<String> meta = new ArrayList<>();
        meta.add("错误 " + wrongCount + " 次");
        meta.add(courseInfo == null ? (wrong.getCourseId() == null ? "通用题目" : "课程 " + wrong.getCourseId()) : String.valueOf(courseInfo.get("courseName")));
        if (wrong.getKnowledgePointId() != null) {
            meta.add("知识点 " + wrong.getKnowledgePointId());
        }
        if (wrong.getLastWrongTime() != null) {
            meta.add("最近错于 " + DateUtils.parseDateToStr("MM-dd HH:mm", wrong.getLastWrongTime()));
        }
        String titlePrefix = wrongCount >= 3 ? "高频错题" : "错题回练";
        String desc = !mastered
                ? "这道题仍处于待巩固状态，建议优先回练并复盘解析。"
                : "这道题已标记为已掌握，可按需复习巩固。";
        if (recentlyWrong) {
            desc = "这是你近期刚出错的题目，尽快复盘更容易补齐漏洞。";
        }
        if (inCooldown) {
            desc = "这道题刚进入错题本，先留一点缓冲时间，稍后再回练会更自然。";
        }
        Map<String, Object> item = taskItem("wrong-" + wrong.getId(),
                titlePrefix + " · 题目 " + wrong.getQuestionId(),
                desc,
                wrongCount >= 3 ? "薄弱项" : "错题任务",
                "WRONG_BOOK",
                mastered ? "info" : "warning",
                mastered ? "已掌握" : "待巩固",
                meta,
                actionItem("wrongbook", wrong.getId(), null, rowMap(
                        "wrongId", wrong.getId(),
                        "questionId", wrong.getQuestionId(),
                        "courseId", wrong.getCourseId(),
                        "knowledgePointId", wrong.getKnowledgePointId()), "/student/wrongbook"));
        item.put("homepageEligible", !inCooldown);
        item.put("cooldownActive", inCooldown);
        return applyTaskSignals(item, "wrongbook", score, !mastered && wrongCount >= 3 ? "HIGH" : "MEDIUM",
                inCooldown ? "错题刚产生，先进入观察缓冲期" : recentlyWrong ? "近期连续出错，适合马上回练" : "用于巩固薄弱题型与知识点",
                "wrongbook");
    }

    private Map<String, Object> buildCourseTask(Map<String, Object> courseInfo, Date now) {
        String taskType = (String) courseInfo.get("taskType");
        String taskCode = resolveCourseTaskCode(taskType);
        int score = "EXAM".equals(taskCode) ? 58 : "HOMEWORK".equals(taskCode) ? 50 : 44;
        String priority = "EXAM".equals(taskCode) ? "MEDIUM" : "NORMAL";
        String reason = "EXAM".equals(taskCode) ? "课程已进入考试跟进阶段" : "适合回到课程页继续推进本周学习";
        Map<String, Object> item = taskItem("course-" + courseInfo.get("id"),
                String.valueOf(courseInfo.get("courseName")) + " · " + ("EXAM".equals(taskCode) ? "课程考试跟进" : "课程学习跟进"),
                "该课程已有学习任务沉淀，进入课程详情可继续查看考试、资源与统计概况。",
                resolveCourseTaskLabel(taskType), taskCode, "success", "进行中",
                Arrays.asList(StringUtils.defaultIfEmpty((String) courseInfo.get("courseCode"), "无课程编码"),
                        StringUtils.defaultIfEmpty((String) courseInfo.get("termName"), "当前学期")),
                actionItem("course", asLong(courseInfo.get("id")), null, rowMap(
                        "classCourseId", asLong(courseInfo.get("id")),
                        "courseId", asLong(courseInfo.get("courseId")),
                        "courseName", courseInfo.get("courseName"),
                        "taskType", courseInfo.get("taskType")), "/student/courses"));
        return applyTaskSignals(item, "course", score, priority, reason, "course");
    }

    private Map<String, Object> buildResumeTask(ScExamRecord record) {
        Map<String, Object> item = taskItem("ongoing-" + record.getRecordId(),
                StringUtils.defaultIfEmpty(record.getPaperName(), "试卷 " + record.getPaperId()),
                "你有一场未完成考试，建议优先继续作答。",
                "待办考试", "EXAM_RESUME", "danger", "进行中",
                Collections.singletonList("开始于 " + StringUtils.defaultIfEmpty(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", record.getStartTime()), "-")),
                actionItem("resume", record.getPaperId(), record.getRecordId(), rowMap(
                        "recordId", record.getRecordId(),
                        "paperId", record.getPaperId(),
                        "paperName", record.getPaperName(),
                        "startTime", record.getStartTime()), "/student/exams/session/" + record.getRecordId()));
        return applyTaskSignals(item, "resume", 98, "URGENT", "已有考试进行中，继续作答的收益最高", "resume");
    }

    private Map<String, Object> applyTaskSignals(Map<String, Object> item, String sourceType, int score, String priorityLevel,
            String recommendationReason, String recommendationBucket) {
        item.put("sourceType", sourceType);
        item.put("score", score);
        item.put("priorityLevel", StringUtils.defaultIfEmpty(priorityLevel, "NORMAL"));
        item.put("recommendationReason", recommendationReason);
        item.put("recommendationBucket", recommendationBucket);
        item.put("homepageEligible", Boolean.TRUE);
        return item;
    }

    private Comparator<Map<String, Object>> taskScoreComparator() {
        return Comparator.comparingInt(this::getTaskScore)
                .reversed()
                .thenComparing(item -> String.valueOf(item.getOrDefault("key", "")));
    }

    private int getTaskScore(Map<String, Object> item) {
        Object value = item == null ? null : item.get("score");
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return 0;
        }
    }

    private long hoursBetween(Date start, Date end) {
        if (start == null || end == null) {
            return Long.MAX_VALUE;
        }
        long diff = end.getTime() - start.getTime();
        return Math.abs(diff) / (1000L * 60L * 60L);
    }

    private List<Map<String, Object>> selectRankedTasks(List<Map<String, Object>> tasks, int limit, Map<String, Integer> bucketLimits) {
        if (tasks == null || tasks.isEmpty() || limit <= 0) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> sorted = tasks.stream()
                .filter(Objects::nonNull)
                .sorted(taskScoreComparator())
                .collect(Collectors.toList());
        List<Map<String, Object>> selected = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        Map<String, Integer> bucketCount = new HashMap<>();
        for (Map<String, Object> item : sorted) {
            if (selected.size() >= limit) {
                break;
            }
            if (!seen.add(String.valueOf(item.get("key")))) {
                continue;
            }
            String bucket = String.valueOf(item.getOrDefault("sourceType", item.getOrDefault("recommendationBucket", "other")));
            int bucketLimit = bucketLimits.getOrDefault(bucket, Integer.MAX_VALUE);
            if (bucketLimit <= 0) {
                continue;
            }
            int current = bucketCount.getOrDefault(bucket, 0);
            if (current >= bucketLimit) {
                continue;
            }
            selected.add(item);
            bucketCount.put(bucket, current + 1);
        }
        if (selected.size() >= limit) {
            return selected;
        }
        for (Map<String, Object> item : sorted) {
            if (selected.size() >= limit) {
                break;
            }
            String key = String.valueOf(item.get("key"));
            if (selected.stream().anyMatch(current -> Objects.equals(String.valueOf(current.get("key")), key))) {
                continue;
            }
            if (!Boolean.TRUE.equals(item.getOrDefault("homepageEligible", Boolean.TRUE)) && bucketLimits.containsKey("homepageOnly")) {
                continue;
            }
            selected.add(item);
        }
        return selected;
    }

    private List<Map<String, Object>> selectHomepageTasks(List<Map<String, Object>> todoTasks, List<Map<String, Object>> recommendedTasks) {
        List<Map<String, Object>> source = new ArrayList<>();
        source.addAll(todoTasks);
        source.addAll(recommendedTasks);
        return selectRankedTasks(source, 8, bucketLimitMap(
                "resume", 1,
                "manual", 3,
                "exam", 3,
                "wrongbook", 1,
                "course", 2));
    }

    private Map<String, Integer> bucketLimitMap(Object... values) {
        Map<String, Integer> result = new LinkedHashMap<>();
        if (values == null) {
            return result;
        }
        for (int i = 0; i + 1 < values.length; i += 2) {
            result.put(String.valueOf(values[i]), Integer.parseInt(String.valueOf(values[i + 1])));
        }
        return result;
    }

    private List<Map<String, Object>> buildRecommendationGroups(List<Map<String, Object>> recommendedTasks) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, List<Map<String, Object>>> grouped = recommendedTasks.stream()
                .collect(Collectors.groupingBy(item -> String.valueOf(item.getOrDefault("sourceType", "other")), LinkedHashMap::new, Collectors.toList()));
        grouped.forEach((sourceType, items) -> {
            Map<String, Object> group = new LinkedHashMap<>();
            group.put("key", sourceType);
            group.put("label", resolveTaskGroupLabel(sourceType));
            group.put("count", items.size());
            group.put("desc", resolveTaskGroupDesc(sourceType));
            group.put("topReason", items.stream()
                    .map(item -> String.valueOf(item.getOrDefault("recommendationReason", "")))
                    .filter(StringUtils::isNotEmpty)
                    .findFirst()
                    .orElse(""));
            result.add(group);
        });
        return result;
    }

    private Map<String, Object> buildTaskSummary(List<Map<String, Object>> todoTasks, List<Map<String, Object>> recommendedTasks,
            List<Map<String, Object>> homepageTasks) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todoCount", todoTasks.size());
        result.put("recommendedCount", recommendedTasks.size());
        result.put("homepageCount", homepageTasks.size());
        result.put("urgentCount", homepageTasks.stream().filter(item -> getTaskScore(item) >= 85).count());
        result.put("examCount", homepageTasks.stream().filter(item -> "exam".equals(item.get("sourceType")) || "resume".equals(item.get("sourceType"))).count());
        result.put("wrongbookCount", homepageTasks.stream().filter(item -> "wrongbook".equals(item.get("sourceType"))).count());
        result.put("topReasons", homepageTasks.stream()
                .map(item -> String.valueOf(item.getOrDefault("recommendationReason", "")))
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .limit(3)
                .collect(Collectors.toList()));
        return result;
    }

    private String resolveTaskGroupLabel(String sourceType) {
        if ("resume".equals(sourceType)) {
            return "继续作答";
        }
        if ("manual".equals(sourceType)) {
            return "老师布置";
        }
        if ("exam".equals(sourceType)) {
            return "考试冲刺";
        }
        if ("wrongbook".equals(sourceType)) {
            return "薄弱巩固";
        }
        if ("course".equals(sourceType)) {
            return "课程跟进";
        }
        return "综合推荐";
    }

    private String resolveTaskGroupDesc(String sourceType) {
        if ("resume".equals(sourceType)) {
            return "优先完成进行中的考试与关键任务";
        }
        if ("manual".equals(sourceType)) {
            return "来自老师布置与截止时间驱动的任务";
        }
        if ("exam".equals(sourceType)) {
            return "结合考试窗口、课程关联和作答记录推荐";
        }
        if ("wrongbook".equals(sourceType)) {
            return "围绕错题频次、掌握状态与近期失误推荐";
        }
        if ("course".equals(sourceType)) {
            return "回到课程页继续推进阶段性学习";
        }
        return "融合多维信号生成的推荐任务";
    }

    private Map<String, Object> manualTaskItem(Map<String, Object> source) {
        String completionStatus = String.valueOf(source.getOrDefault("completionStatus", "PENDING"));
        String taskType = String.valueOf(source.getOrDefault("taskType", "COURSE"));
        Long taskId = asLong(source.get("taskId"));
        Long actionTargetId = asLong(source.get("actionTargetId"));
        Long dispatchId = asLong(source.get("dispatchId"));
        String path = StringUtils.defaultIfEmpty((String) source.get("actionPath"), "/student/plaza");
        Date now = new Date();
        Date dueDate = parseAnyDate(source.get("dueTime"));
        List<String> meta = new ArrayList<>();
        if (dueDate != null) {
            meta.add("截止 " + DateUtils.parseDateToStr("yyyy-MM-dd HH:mm", dueDate));
        }
        if (StringUtils.isNotEmpty((String) source.get("priorityLevel"))) {
            meta.add("优先级 " + source.get("priorityLevel"));
        }
        Map<String, Object> item = taskItem("manual-" + taskId + "-" + dispatchId,
                StringUtils.defaultIfEmpty((String) source.get("taskTitle"), "学习任务"),
                StringUtils.defaultIfEmpty((String) source.get("taskDesc"), "请按要求完成任务。"),
                resolveManualTaskLabel(taskType),
                "COMPLETED".equals(completionStatus) ? "COMPLETED" : taskType,
                resolveManualTagType(completionStatus, taskType),
                resolveManualStatusText(completionStatus, source.get("dueTime")),
                meta,
                actionItem(resolveManualActionType(path), actionTargetId, dispatchId, source, "/student/tasks/" + dispatchId));
        int score = 42 + priorityWeight((String) source.get("priorityLevel"));
        if (!"COMPLETED".equalsIgnoreCase(completionStatus) && dueDate != null) {
            long hoursToDue = hoursBetween(now, dueDate);
            if (dueDate.before(now)) {
                score += 28;
            } else if (hoursToDue <= 24) {
                score += 22;
            } else if (hoursToDue <= 72) {
                score += 14;
            }
        }
        if ("COMPLETED".equalsIgnoreCase(completionStatus)) {
            score = 0;
        }
        return applyTaskSignals(item, "manual", score,
                normalizePriorityLevel((String) source.get("priorityLevel")),
                dueDate != null ? "结合截止时间与老师布置优先级推荐" : "老师布置的任务建议按计划推进",
                "manual");
    }

    private int priorityWeight(String priorityLevel) {
        String normalized = normalizePriorityLevel(priorityLevel);
        if ("URGENT".equals(normalized)) {
            return 30;
        }
        if ("HIGH".equals(normalized)) {
            return 20;
        }
        if ("MEDIUM".equals(normalized)) {
            return 10;
        }
        if ("LOW".equals(normalized)) {
            return 2;
        }
        return 6;
    }

    private String normalizePriorityLevel(String priorityLevel) {
        String normalized = StringUtils.upperCase(StringUtils.trimToEmpty(priorityLevel));
        if (StringUtils.isBlank(normalized)) {
            return "NORMAL";
        }
        if (Arrays.asList("P0", "URGENT", "CRITICAL").contains(normalized)) {
            return "URGENT";
        }
        if (Arrays.asList("P1", "HIGH").contains(normalized)) {
            return "HIGH";
        }
        if (Arrays.asList("P2", "MEDIUM").contains(normalized)) {
            return "MEDIUM";
        }
        if (Arrays.asList("P4", "LOW").contains(normalized)) {
            return "LOW";
        }
        return "NORMAL";
    }

    private Map<String, Object> toPortalNoticeItem(SysNotice notice, String defaultCategory) {
        Map<String, Object> item = new LinkedHashMap<>();
        String category = StringUtils.defaultIfEmpty(notice.getBizCategory(), defaultCategory);
        item.put("noticeId", notice.getNoticeId());
        item.put("messageId", "notice-" + notice.getNoticeId());
        item.put("bizCategory", category);
        item.put("messageType", resolvePortalMessageType(notice, category));
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

    private String resolvePortalMessageType(SysNotice notice, String category) {
        if ("NOTICE".equals(category)) {
            return "NOTICE";
        }
        String actionType = StringUtils.defaultIfEmpty(notice.getActionType(), "");
        String actionPath = StringUtils.defaultIfEmpty(notice.getActionPath(), "");
        if (StringUtils.equalsIgnoreCase(actionType, "courseSelectionReview")
                || StringUtils.contains(actionPath, "/student/selection")
                || StringUtils.contains(actionPath, "/student/personalized-selection")) {
            return "COURSE_SELECTION";
        }
        return "SYSTEM_MESSAGE";
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

    private Map<String, String> rowStringMap(String... kv) {
        Map<String, String> row = new LinkedHashMap<>();
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
        if (authorities.contains("ROLE_campus_head_teacher")
                || authorities.contains("ROLE_advisor")
                || authorities.contains("ROLE_head_teacher")) {
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

    private String resolveDisplayedCourseName(ScClassCourse classCourse) {
        if (classCourse == null) {
            return null;
        }
        String baseCourseName = StringUtils.trimToEmpty(classCourse.getCourseName());
        String selectionOptionName = StringUtils.trimToEmpty(classCourse.getSelectionOptionName());
        if (StringUtils.isEmpty(selectionOptionName) || StringUtils.equals(baseCourseName, selectionOptionName)) {
            return StringUtils.defaultIfEmpty(baseCourseName, selectionOptionName);
        }
        if (StringUtils.isEmpty(baseCourseName)) {
            return selectionOptionName;
        }
        return baseCourseName + " / " + selectionOptionName;
    }

    private String resolveTeachingClassName(ScClassCourse classCourse) {
        if (classCourse == null) {
            return null;
        }
        String selectionOptionName = StringUtils.trimToEmpty(classCourse.getSelectionOptionName());
        if (StringUtils.isNotEmpty(selectionOptionName)) {
            return selectionOptionName;
        }
        String className = StringUtils.trimToEmpty(classCourse.getClassName());
        String courseName = StringUtils.trimToEmpty(resolveDisplayedCourseName(classCourse));
        if (StringUtils.isNotEmpty(className) && StringUtils.isNotEmpty(courseName) && !StringUtils.equals(className, courseName)) {
            return className + " · " + courseName;
        }
        return StringUtils.defaultIfEmpty(className, courseName);
    }

    @SuppressWarnings("unchecked")
    private String resolveTextBlockValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            Map<String, Object> block = (Map<String, Object>) value;
            Object text = block.get("text");
            if (text == null) {
                text = block.get("textZh");
            }
            if (text == null) {
                text = block.get("textEn");
            }
            return text == null ? null : String.valueOf(text);
        }
        return String.valueOf(value);
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
