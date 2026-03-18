package com.smart.system.domain.campusvo;

import java.util.List;
import com.smart.system.domain.ScLearningWarning;

/**
 * 学生端首页概览 VO
 *
 * @author can
 */
public class StudentDashboardVo {
    private Long userId;
    private Long courseId;
    private Integer studyRecordCount;
    private Integer examRecordCount;
    private Integer warningCount;
    private LearningProfileOverviewVo profile;
    private List<RecommendationItemVo> recommendations;
    private List<ScLearningWarning> warnings;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getStudyRecordCount() {
        return studyRecordCount;
    }

    public void setStudyRecordCount(Integer studyRecordCount) {
        this.studyRecordCount = studyRecordCount;
    }

    public Integer getExamRecordCount() {
        return examRecordCount;
    }

    public void setExamRecordCount(Integer examRecordCount) {
        this.examRecordCount = examRecordCount;
    }

    public Integer getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(Integer warningCount) {
        this.warningCount = warningCount;
    }

    public LearningProfileOverviewVo getProfile() {
        return profile;
    }

    public void setProfile(LearningProfileOverviewVo profile) {
        this.profile = profile;
    }

    public List<RecommendationItemVo> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecommendationItemVo> recommendations) {
        this.recommendations = recommendations;
    }

    public List<ScLearningWarning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<ScLearningWarning> warnings) {
        this.warnings = warnings;
    }
}
