package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScParentStudentRel;
import com.smart.system.domain.ScQuestionBank;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.campusvo.CampusDashboardVo;
import com.smart.system.domain.campusvo.ParentDashboardVo;
import com.smart.system.domain.campusvo.StudentDashboardVo;
import com.smart.system.domain.campusvo.TeacherDashboardVo;
import com.smart.system.service.ICampusOverviewService;
import com.smart.system.service.IScClassCourseService;
import com.smart.system.service.IScExamRecordService;
import com.smart.system.service.IScParentStudentRelService;
import com.smart.system.service.IScQuestionBankService;
import com.smart.system.service.IScResourceService;

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
        vo.setCourseCount((int) classCourses.stream().map(ScClassCourse::getCourseId).filter(java.util.Objects::nonNull)
                .distinct().count());
        vo.setClassCount((int) classCourses.stream().map(ScClassCourse::getClassId).filter(java.util.Objects::nonNull)
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
}
