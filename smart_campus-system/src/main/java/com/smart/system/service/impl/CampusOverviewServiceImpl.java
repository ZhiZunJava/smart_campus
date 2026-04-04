package com.smart.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.core.domain.entity.SysUser;
import com.smart.common.exception.ServiceException;
import com.smart.system.domain.ScClass;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScClassroom;
import com.smart.system.domain.ScCourse;
import com.smart.system.domain.ScCourseSchedule;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScGrade;
import com.smart.system.domain.ScLearningTask;
import com.smart.system.domain.ScParentStudentRel;
import com.smart.system.domain.ScQuestionBank;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.ScSchoolTerm;
import com.smart.system.domain.SysLogininfor;
import com.smart.system.domain.campusvo.CampusDashboardVo;
import com.smart.system.domain.campusvo.ParentDashboardVo;
import com.smart.system.domain.campusvo.StudentDashboardVo;
import com.smart.system.domain.campusvo.TeacherDashboardVo;
import com.smart.system.service.ICampusOverviewService;
import com.smart.system.service.IScClassCourseService;
import com.smart.system.service.IScClassService;
import com.smart.system.service.IScClassroomService;
import com.smart.system.service.IScCourseScheduleService;
import com.smart.system.service.IScCourseService;
import com.smart.system.service.IScExamRecordService;
import com.smart.system.service.IScGradeService;
import com.smart.system.service.IScLearningTaskService;
import com.smart.system.service.IScParentStudentRelService;
import com.smart.system.service.IScQuestionBankService;
import com.smart.system.service.IScResourceService;
import com.smart.system.service.IScSchoolTermService;
import com.smart.system.service.ISysLogininforService;
import com.smart.system.service.ISysUserService;

@Service
public class CampusOverviewServiceImpl implements ICampusOverviewService {
    @Autowired
    private IScResourceService scResourceService;
    @Autowired
    private IScExamRecordService scExamRecordService;
    @Autowired
    private IScClassCourseService scClassCourseService;
    @Autowired
    private IScQuestionBankService scQuestionBankService;
    @Autowired
    private IScParentStudentRelService scParentStudentRelService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IScGradeService scGradeService;
    @Autowired
    private IScClassService scClassService;
    @Autowired
    private IScCourseService scCourseService;
    @Autowired
    private IScClassroomService scClassroomService;
    @Autowired
    private IScSchoolTermService scSchoolTermService;
    @Autowired
    private IScCourseScheduleService scCourseScheduleService;
    @Autowired
    private IScLearningTaskService scLearningTaskService;
    @Autowired
    private ISysLogininforService sysLogininforService;

    @Override
    public CampusDashboardVo getDashboard(Long userId, Long courseId, Integer recommendLimit) {
        CampusDashboardVo vo = new CampusDashboardVo();
        vo.setUserId(userId);

        ScResource resourceQuery = new ScResource();
        resourceQuery.setCourseId(courseId);
        resourceQuery.setStatus("0");
        vo.setResourceCount(scResourceService.selectScResourceList(resourceQuery).size());

        ScExamRecord examRecordQuery = new ScExamRecord();
        examRecordQuery.setUserId(userId);
        vo.setExamRecordCount(scExamRecordService.selectScExamRecordList(examRecordQuery).size());
        return vo;
    }

    @Override
    public StudentDashboardVo getStudentDashboard(Long userId, Long courseId, Integer recommendLimit) {
        CampusDashboardVo dashboard = getDashboard(userId, courseId, recommendLimit);
        StudentDashboardVo vo = new StudentDashboardVo();
        vo.setUserId(userId);
        vo.setCourseId(courseId);
        vo.setExamRecordCount(dashboard.getExamRecordCount());
        return vo;
    }

    @Override
    public TeacherDashboardVo getTeacherDashboard(Long teacherId) {
        TeacherDashboardVo vo = new TeacherDashboardVo();
        vo.setTeacherId(teacherId);

        ScClassCourse classCourseQuery = new ScClassCourse();
        classCourseQuery.setTeacherId(teacherId);
        classCourseQuery.setStatus("0");
        List<ScClassCourse> classCourses = scClassCourseService.selectScClassCourseList(classCourseQuery);
        vo.setCourseCount((int) classCourses.stream().map(ScClassCourse::getCourseId).filter(Objects::nonNull)
                .distinct().count());
        vo.setClassCount((int) classCourses.stream().map(ScClassCourse::getClassId).filter(Objects::nonNull)
                .distinct().count());

        ScResource resourceQuery = new ScResource();
        resourceQuery.setUploaderId(teacherId);
        vo.setResourceCount(scResourceService.selectScResourceList(resourceQuery).size());

        ScQuestionBank questionQuery = new ScQuestionBank();
        int questionCount = 0;
        for (ScClassCourse classCourse : classCourses) {
            questionQuery.setCourseId(classCourse.getCourseId());
            questionCount += scQuestionBankService.selectScQuestionBankList(questionQuery).size();
        }
        vo.setQuestionCount(questionCount);
        vo.setWarningCount(0);
        return vo;
    }

    @Override
    public ParentDashboardVo getParentDashboard(Long parentUserId, Long studentUserId, Long courseId) {
        if (studentUserId == null) {
            ScParentStudentRel relQuery = new ScParentStudentRel();
            relQuery.setParentUserId(parentUserId);
            List<ScParentStudentRel> rels = scParentStudentRelService.selectScParentStudentRelList(relQuery);
            if (!rels.isEmpty()) {
                studentUserId = rels.get(0).getStudentUserId();
            }
        }

        ParentDashboardVo vo = new ParentDashboardVo();
        vo.setParentUserId(parentUserId);
        vo.setStudentUserId(studentUserId);
        if (studentUserId == null) {
            throw new ServiceException("当前家长未绑定学生，无法获取孩子概览信息");
        }

        ScExamRecord examRecordQuery = new ScExamRecord();
        examRecordQuery.setUserId(studentUserId);
        vo.setExamRecordCount(scExamRecordService.selectScExamRecordList(examRecordQuery).size());
        vo.setWarningCount(0);
        return vo;
    }

    @Override
    public CampusDashboardVo getAdminDashboard() {
        CampusDashboardVo vo = new CampusDashboardVo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // ===== 用户统计 =====
        SysUser allUserQuery = new SysUser();
        vo.setUserTotal(sysUserService.selectUserList(allUserQuery).size());

        SysUser studentQuery = new SysUser();
        studentQuery.setUserType("student");
        vo.setStudentCount(sysUserService.selectUserList(studentQuery).size());

        SysUser teacherQuery = new SysUser();
        teacherQuery.setUserType("teacher");
        vo.setTeacherCount(sysUserService.selectUserList(teacherQuery).size());

        SysUser parentQuery = new SysUser();
        parentQuery.setUserType("parent");
        vo.setParentCount(sysUserService.selectUserList(parentQuery).size());

        SysUser adminQuery = new SysUser();
        adminQuery.setUserType("admin");
        vo.setAdminCount(sysUserService.selectUserList(adminQuery).size());

        // ===== 教学统计 =====
        vo.setGradeCount(scGradeService.selectScGradeList(new ScGrade()).size());
        vo.setClassCount(scClassService.selectScClassList(new ScClass()).size());
        vo.setCourseCount(scCourseService.selectScCourseList(new ScCourse()).size());
        vo.setClassroomCount(scClassroomService.selectScClassroomList(new ScClassroom()).size());

        List<ScSchoolTerm> terms = scSchoolTermService.selectScSchoolTermList(new ScSchoolTerm());
        vo.setTermCount(terms.size());
        int enabledCount = 0;
        String currentTermName = null;
        for (ScSchoolTerm term : terms) {
            if ("0".equals(term.getStatus())) {
                enabledCount++;
            }
            if ("1".equals(term.getIsCurrent())) {
                currentTermName = term.getTermName();
            }
        }
        vo.setEnabledTermCount(enabledCount);
        vo.setCurrentTermName(currentTermName != null ? currentTermName : "未设置当前学期");

        // ===== 排课统计 =====
        List<ScClassCourse> classCourses = scClassCourseService.selectScClassCourseList(new ScClassCourse());
        vo.setClassCourseCount(classCourses.size());
        int arranged = 0;
        int pending = 0;
        for (ScClassCourse cc : classCourses) {
            int total = cc.getTotalHours() != null ? cc.getTotalHours() : 0;
            int done = cc.getArrangedHours() != null ? cc.getArrangedHours() : 0;
            if (done > 0) {
                arranged++;
            }
            if (total > done) {
                pending++;
            }
        }
        vo.setArrangedClassCourseCount(arranged);
        vo.setPendingArrangeCount(pending);
        vo.setScheduleCount(scCourseScheduleService.selectScCourseScheduleList(new ScCourseSchedule()).size());

        // ===== 学习与资源 =====
        ScResource resQuery = new ScResource();
        resQuery.setStatus("0");
        vo.setResourceCount(scResourceService.selectScResourceList(resQuery).size());
        vo.setLearningTaskCount(scLearningTaskService.selectScLearningTaskList(new ScLearningTask()).size());
        vo.setQuestionBankCount(scQuestionBankService.selectScQuestionBankList(new ScQuestionBank()).size());

        // ===== 考试记录总数 =====
        List<ScExamRecord> allExamRecords = scExamRecordService.selectScExamRecordList(new ScExamRecord());
        vo.setExamRecordCount(allExamRecords.size());

        // ===== 考试趋势（近 7 天） =====
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -6);
        Date sevenDaysAgo = cal.getTime();

        // 初始化 7 天的日期桶
        Map<String, Integer> examBuckets = new LinkedHashMap<>();
        Calendar iter = Calendar.getInstance();
        iter.setTime(sevenDaysAgo);
        for (int i = 0; i < 7; i++) {
            examBuckets.put(sdf.format(iter.getTime()), 0);
            iter.add(Calendar.DAY_OF_MONTH, 1);
        }

        for (ScExamRecord record : allExamRecords) {
            Date d = record.getCreateTime();
            if (d != null && !d.before(sevenDaysAgo)) {
                String key = sdf.format(d);
                examBuckets.computeIfPresent(key, (k, v) -> v + 1);
            }
        }

        List<CampusDashboardVo.DailyCountVo> examTrend = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : examBuckets.entrySet()) {
            examTrend.add(new CampusDashboardVo.DailyCountVo(entry.getKey(), entry.getValue()));
        }
        vo.setExamTrend(examTrend);

        // ===== 登录趋势（近 7 天） =====
        SysLogininfor loginQuery = new SysLogininfor();
        loginQuery.getParams().put("beginTime", sdf.format(sevenDaysAgo));
        loginQuery.getParams().put("endTime", sdf.format(today) + " 23:59:59");
        List<SysLogininfor> loginRecords = sysLogininforService.selectLogininforList(loginQuery);

        Map<String, Integer> loginBuckets = new LinkedHashMap<>();
        iter.setTime(sevenDaysAgo);
        for (int i = 0; i < 7; i++) {
            loginBuckets.put(sdf.format(iter.getTime()), 0);
            iter.add(Calendar.DAY_OF_MONTH, 1);
        }

        for (SysLogininfor info : loginRecords) {
            Date d = info.getLoginTime();
            if (d != null) {
                String key = sdf.format(d);
                loginBuckets.computeIfPresent(key, (k, v) -> v + 1);
            }
        }

        List<CampusDashboardVo.DailyCountVo> loginTrend = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : loginBuckets.entrySet()) {
            loginTrend.add(new CampusDashboardVo.DailyCountVo(entry.getKey(), entry.getValue()));
        }
        vo.setLoginTrend(loginTrend);

        return vo;
    }
}
