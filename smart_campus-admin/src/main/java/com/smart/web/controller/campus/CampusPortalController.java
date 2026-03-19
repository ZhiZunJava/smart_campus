package com.smart.web.controller.campus;

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
import com.smart.system.domain.ScSchoolTerm;
import com.smart.system.domain.ScParentStudentRel;
import com.smart.system.domain.SysNotice;
import com.smart.system.service.ICampusOverviewService;
import com.smart.system.service.IScClassCourseService;
import com.smart.system.service.IScClassService;
import com.smart.system.service.IScCourseScheduleService;
import com.smart.system.service.IScCourseService;
import com.smart.system.service.IScParentStudentRelService;
import com.smart.system.service.IScSchoolTermService;
import com.smart.system.service.IScUserProfileService;
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
    public AjaxResult noticeList(@RequestParam(required = false, defaultValue = "6") Integer limit) {
        SysNotice query = new SysNotice();
        query.setStatus("0");
        List<SysNotice> notices = noticeService.selectNoticeList(query);
        notices.sort(Comparator.comparing(SysNotice::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())));
        List<Map<String, Object>> items = new ArrayList<>();
        int max = Math.max(1, limit == null ? 6 : limit);
        for (SysNotice notice : notices.stream().limit(max).toList()) {
            Map<String, Object> item = new HashMap<>();
            item.put("noticeId", notice.getNoticeId());
            item.put("noticeTitle", notice.getNoticeTitle());
            item.put("noticeType", notice.getNoticeType());
            item.put("noticeContent", notice.getNoticeContent());
            item.put("createTime", notice.getCreateTime());
            items.add(item);
        }
        return success(items);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/notice/{noticeId}")
    public AjaxResult noticeDetail(@PathVariable Long noticeId) {
        return success(noticeService.selectNoticeById(noticeId));
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
