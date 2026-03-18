package com.smart.system.domain.campusvo;

import java.util.List;
import com.smart.system.domain.ScLearningWarning;

/**
 * 家长端概览 VO
 *
 * @author can
 */
public class ParentDashboardVo {
    private Long parentUserId;
    private Long studentUserId;
    private Integer studyRecordCount;
    private Integer examRecordCount;
    private Integer warningCount;
    private LearningProfileOverviewVo profile;
    private List<ScLearningWarning> warnings;

    public Long getParentUserId() {
        return parentUserId;
    }

    public void setParentUserId(Long parentUserId) {
        this.parentUserId = parentUserId;
    }

    public Long getStudentUserId() {
        return studentUserId;
    }

    public void setStudentUserId(Long studentUserId) {
        this.studentUserId = studentUserId;
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

    public List<ScLearningWarning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<ScLearningWarning> warnings) {
        this.warnings = warnings;
    }
}
