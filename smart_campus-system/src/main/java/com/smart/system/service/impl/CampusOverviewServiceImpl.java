package com.smart.system.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.system.domain.ScCourse;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScLearningProfile;
import com.smart.system.domain.ScLearningRecommendation;
import com.smart.system.domain.ScLearningWarning;
import com.smart.system.domain.ScParentStudentRel;
import com.smart.system.domain.ScQuestionBank;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.ScStudyRecord;
import com.smart.system.domain.campusvo.CampusDashboardVo;
import com.smart.system.domain.campusvo.LearningProfileOverviewVo;
import com.smart.system.domain.campusvo.ParentDashboardVo;
import com.smart.system.domain.campusvo.RecommendationItemVo;
import com.smart.system.domain.campusvo.StudentDashboardVo;
import com.smart.system.domain.campusvo.TeacherDashboardVo;
import com.smart.system.service.ICampusOverviewService;
import com.smart.system.service.IScCourseService;
import com.smart.system.service.IScExamRecordService;
import com.smart.system.service.IScLearningProfileService;
import com.smart.system.service.IScLearningRecommendationService;
import com.smart.system.service.IScLearningWarningService;
import com.smart.system.service.IScParentStudentRelService;
import com.smart.system.service.IScQuestionBankService;
import com.smart.system.service.IScResourceService;
import com.smart.system.service.IScStudyRecordService;

@Service
public class CampusOverviewServiceImpl implements ICampusOverviewService
{
    @Autowired private IScResourceService scResourceService;
    @Autowired private IScStudyRecordService scStudyRecordService;
    @Autowired private IScLearningProfileService scLearningProfileService;
    @Autowired private IScLearningRecommendationService scLearningRecommendationService;
    @Autowired private IScLearningWarningService scLearningWarningService;
    @Autowired private IScExamRecordService scExamRecordService;
    @Autowired private IScCourseService scCourseService;
    @Autowired private IScQuestionBankService scQuestionBankService;
    @Autowired private IScParentStudentRelService scParentStudentRelService;

    @Override
    public CampusDashboardVo getDashboard(Long userId, Long courseId, Integer recommendLimit)
    {
        CampusDashboardVo vo = new CampusDashboardVo();
        vo.setUserId(userId);

        ScResource resourceQuery = new ScResource();
        resourceQuery.setCourseId(courseId);
        resourceQuery.setStatus("0");
        List<ScResource> resources = scResourceService.selectScResourceList(resourceQuery);
        vo.setResourceCount(resources.size());

        ScStudyRecord studyRecordQuery = new ScStudyRecord();
        studyRecordQuery.setUserId(userId);
        studyRecordQuery.setCourseId(courseId);
        List<ScStudyRecord> studyRecords = scStudyRecordService.selectScStudyRecordList(studyRecordQuery);
        vo.setStudyRecordCount(studyRecords.size());

        ScExamRecord examRecordQuery = new ScExamRecord();
        examRecordQuery.setUserId(userId);
        List<ScExamRecord> examRecords = scExamRecordService.selectScExamRecordList(examRecordQuery);
        vo.setExamRecordCount(examRecords.size());

        vo.setProfile(buildProfileOverview(userId, courseId));

        ScLearningWarning warningQuery = new ScLearningWarning();
        warningQuery.setUserId(userId);
        warningQuery.setCourseId(courseId);
        List<ScLearningWarning> warnings = scLearningWarningService.selectScLearningWarningList(warningQuery);
        vo.setWarningCount(warnings.size());
        vo.setWarnings(warnings.isEmpty() ? Collections.emptyList() : warnings.subList(0, Math.min(warnings.size(), 5)));

        List<ScLearningRecommendation> recommendations = scLearningRecommendationService.generateRecommendations(userId, "home", recommendLimit == null ? 5 : recommendLimit);
        vo.setRecommendations(recommendations.stream().map(this::toRecommendationVo).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public StudentDashboardVo getStudentDashboard(Long userId, Long courseId, Integer recommendLimit)
    {
        CampusDashboardVo dashboard = getDashboard(userId, courseId, recommendLimit);
        StudentDashboardVo vo = new StudentDashboardVo();
        vo.setUserId(userId);
        vo.setCourseId(courseId);
        vo.setStudyRecordCount(dashboard.getStudyRecordCount());
        vo.setExamRecordCount(dashboard.getExamRecordCount());
        vo.setWarningCount(dashboard.getWarningCount());
        vo.setProfile(dashboard.getProfile());
        vo.setRecommendations(dashboard.getRecommendations());
        vo.setWarnings(dashboard.getWarnings());
        return vo;
    }

    @Override
    public TeacherDashboardVo getTeacherDashboard(Long teacherId)
    {
        TeacherDashboardVo vo = new TeacherDashboardVo();
        vo.setTeacherId(teacherId);

        ScCourse courseQuery = new ScCourse();
        courseQuery.setTeacherId(teacherId);
        List<ScCourse> courses = scCourseService.selectScCourseList(courseQuery);
        vo.setCourseCount(courses.size());

        ScResource resourceQuery = new ScResource();
        resourceQuery.setUploaderId(teacherId);
        vo.setResourceCount(scResourceService.selectScResourceList(resourceQuery).size());

        ScQuestionBank questionQuery = new ScQuestionBank();
        int questionCount = 0;
        int warningCount = 0;
        for (ScCourse course : courses)
        {
            questionQuery.setCourseId(course.getCourseId());
            questionCount += scQuestionBankService.selectScQuestionBankList(questionQuery).size();

            ScLearningWarning warningQuery = new ScLearningWarning();
            warningQuery.setCourseId(course.getCourseId());
            warningCount += scLearningWarningService.selectScLearningWarningList(warningQuery).size();
        }
        vo.setQuestionCount(questionCount);
        vo.setWarningCount(warningCount);
        vo.setClassCount(courses.size());
        return vo;
    }

    @Override
    public ParentDashboardVo getParentDashboard(Long parentUserId, Long studentUserId, Long courseId)
    {
        if (studentUserId == null)
        {
            ScParentStudentRel relQuery = new ScParentStudentRel();
            relQuery.setParentUserId(parentUserId);
            List<ScParentStudentRel> rels = scParentStudentRelService.selectScParentStudentRelList(relQuery);
            if (!rels.isEmpty())
            {
                studentUserId = rels.get(0).getStudentUserId();
            }
        }

        ParentDashboardVo vo = new ParentDashboardVo();
        vo.setParentUserId(parentUserId);
        vo.setStudentUserId(studentUserId);
        if (studentUserId == null)
        {
            throw new ServiceException("当前家长未绑定学生，无法获取孩子概览信息");
        }

        ScStudyRecord studyRecordQuery = new ScStudyRecord();
        studyRecordQuery.setUserId(studentUserId);
        studyRecordQuery.setCourseId(courseId);
        vo.setStudyRecordCount(scStudyRecordService.selectScStudyRecordList(studyRecordQuery).size());

        ScExamRecord examRecordQuery = new ScExamRecord();
        examRecordQuery.setUserId(studentUserId);
        vo.setExamRecordCount(scExamRecordService.selectScExamRecordList(examRecordQuery).size());

        ScLearningWarning warningQuery = new ScLearningWarning();
        warningQuery.setUserId(studentUserId);
        warningQuery.setCourseId(courseId);
        List<ScLearningWarning> warnings = scLearningWarningService.selectScLearningWarningList(warningQuery);
        vo.setWarningCount(warnings.size());
        vo.setWarnings(warnings.isEmpty() ? Collections.emptyList() : warnings.subList(0, Math.min(warnings.size(), 5)));
        vo.setProfile(buildProfileOverview(studentUserId, courseId));
        return vo;
    }

    private LearningProfileOverviewVo buildProfileOverview(Long userId, Long courseId)
    {
        ScLearningProfile profileQuery = new ScLearningProfile();
        profileQuery.setUserId(userId);
        profileQuery.setCourseId(courseId);
        List<ScLearningProfile> profiles = scLearningProfileService.selectScLearningProfileList(profileQuery);
        if (profiles.isEmpty())
        {
            return null;
        }
        ScLearningProfile profile = profiles.get(0);
        LearningProfileOverviewVo profileVo = new LearningProfileOverviewVo();
        profileVo.setUserId(profile.getUserId());
        profileVo.setCourseId(profile.getCourseId());
        profileVo.setAbilityLevel(profile.getAbilityLevel());
        profileVo.setActiveScore(profile.getActiveScore());
        profileVo.setConcentrationScore(profile.getConcentrationScore());
        profileVo.setMasteryScore(profile.getMasteryScore());
        profileVo.setInterestScore(profile.getInterestScore());
        profileVo.setRiskScore(profile.getRiskScore());
        return profileVo;
    }

    private RecommendationItemVo toRecommendationVo(ScLearningRecommendation recommendation)
    {
        RecommendationItemVo vo = new RecommendationItemVo();
        vo.setRecommendId(recommendation.getRecommendId());
        vo.setBizId(recommendation.getBizId());
        vo.setBizType(recommendation.getBizType());
        vo.setRecommendReason(recommendation.getRecommendReason());
        vo.setRecommendScore(recommendation.getRecommendScore());
        vo.setSceneCode(recommendation.getSceneCode());
        vo.setExpireTime(recommendation.getExpireTime());

        if ("resource".equalsIgnoreCase(recommendation.getBizType()) && recommendation.getBizId() != null)
        {
            ScResource resource = scResourceService.selectScResourceByResourceId(recommendation.getBizId());
            if (resource != null)
            {
                vo.setTitle(resource.getResourceName());
                vo.setResourceType(resource.getResourceType());
                vo.setSummary(resource.getSummary());
            }
        }
        return vo;
    }
}
