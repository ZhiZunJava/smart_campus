package com.smart.system.domain.campusvo;

import java.util.List;
import com.smart.system.domain.ScLearningWarning;

/**
 * 校园首页概览 VO
 *
 * @author Codex
 */
public class CampusDashboardVo
{
    private Long userId;
    private Integer resourceCount;
    private Integer studyRecordCount;
    private Integer warningCount;
    private Integer examRecordCount;
    private LearningProfileOverviewVo profile;
    private List<RecommendationItemVo> recommendations;
    private List<ScLearningWarning> warnings;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getResourceCount() { return resourceCount; }
    public void setResourceCount(Integer resourceCount) { this.resourceCount = resourceCount; }
    public Integer getStudyRecordCount() { return studyRecordCount; }
    public void setStudyRecordCount(Integer studyRecordCount) { this.studyRecordCount = studyRecordCount; }
    public Integer getWarningCount() { return warningCount; }
    public void setWarningCount(Integer warningCount) { this.warningCount = warningCount; }
    public Integer getExamRecordCount() { return examRecordCount; }
    public void setExamRecordCount(Integer examRecordCount) { this.examRecordCount = examRecordCount; }
    public LearningProfileOverviewVo getProfile() { return profile; }
    public void setProfile(LearningProfileOverviewVo profile) { this.profile = profile; }
    public List<RecommendationItemVo> getRecommendations() { return recommendations; }
    public void setRecommendations(List<RecommendationItemVo> recommendations) { this.recommendations = recommendations; }
    public List<ScLearningWarning> getWarnings() { return warnings; }
    public void setWarnings(List<ScLearningWarning> warnings) { this.warnings = warnings; }
}
