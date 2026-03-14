package com.smart.system.domain.campusvo;

import java.math.BigDecimal;
import java.util.List;
import com.smart.system.domain.ScLearningReport;
import com.smart.system.domain.ScLearningWarning;

public class LearningDiagnosisVo
{
    private Long userId;
    private Long courseId;
    private String riskLevel;
    private String overallSummary;
    private LearningProfileOverviewVo profile;
    private Integer studyRecordCount;
    private Integer totalStudyDurationSeconds;
    private BigDecimal avgProgressRate;
    private Integer examCount;
    private BigDecimal avgExamScore;
    private BigDecimal avgCorrectRate;
    private Integer wrongQuestionCount;
    private Integer pendingWarningCount;
    private Integer recommendationCount;
    private List<String> riskTags;
    private List<String> actionSuggestions;
    private ScLearningWarning latestWarning;
    private ScLearningReport latestReport;
    private List<RecommendationItemVo> recommendations;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    public String getRiskLevel()
    {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel)
    {
        this.riskLevel = riskLevel;
    }

    public String getOverallSummary()
    {
        return overallSummary;
    }

    public void setOverallSummary(String overallSummary)
    {
        this.overallSummary = overallSummary;
    }

    public LearningProfileOverviewVo getProfile()
    {
        return profile;
    }

    public void setProfile(LearningProfileOverviewVo profile)
    {
        this.profile = profile;
    }

    public Integer getStudyRecordCount()
    {
        return studyRecordCount;
    }

    public void setStudyRecordCount(Integer studyRecordCount)
    {
        this.studyRecordCount = studyRecordCount;
    }

    public Integer getTotalStudyDurationSeconds()
    {
        return totalStudyDurationSeconds;
    }

    public void setTotalStudyDurationSeconds(Integer totalStudyDurationSeconds)
    {
        this.totalStudyDurationSeconds = totalStudyDurationSeconds;
    }

    public BigDecimal getAvgProgressRate()
    {
        return avgProgressRate;
    }

    public void setAvgProgressRate(BigDecimal avgProgressRate)
    {
        this.avgProgressRate = avgProgressRate;
    }

    public Integer getExamCount()
    {
        return examCount;
    }

    public void setExamCount(Integer examCount)
    {
        this.examCount = examCount;
    }

    public BigDecimal getAvgExamScore()
    {
        return avgExamScore;
    }

    public void setAvgExamScore(BigDecimal avgExamScore)
    {
        this.avgExamScore = avgExamScore;
    }

    public BigDecimal getAvgCorrectRate()
    {
        return avgCorrectRate;
    }

    public void setAvgCorrectRate(BigDecimal avgCorrectRate)
    {
        this.avgCorrectRate = avgCorrectRate;
    }

    public Integer getWrongQuestionCount()
    {
        return wrongQuestionCount;
    }

    public void setWrongQuestionCount(Integer wrongQuestionCount)
    {
        this.wrongQuestionCount = wrongQuestionCount;
    }

    public Integer getPendingWarningCount()
    {
        return pendingWarningCount;
    }

    public void setPendingWarningCount(Integer pendingWarningCount)
    {
        this.pendingWarningCount = pendingWarningCount;
    }

    public Integer getRecommendationCount()
    {
        return recommendationCount;
    }

    public void setRecommendationCount(Integer recommendationCount)
    {
        this.recommendationCount = recommendationCount;
    }

    public List<String> getRiskTags()
    {
        return riskTags;
    }

    public void setRiskTags(List<String> riskTags)
    {
        this.riskTags = riskTags;
    }

    public List<String> getActionSuggestions()
    {
        return actionSuggestions;
    }

    public void setActionSuggestions(List<String> actionSuggestions)
    {
        this.actionSuggestions = actionSuggestions;
    }

    public ScLearningWarning getLatestWarning()
    {
        return latestWarning;
    }

    public void setLatestWarning(ScLearningWarning latestWarning)
    {
        this.latestWarning = latestWarning;
    }

    public ScLearningReport getLatestReport()
    {
        return latestReport;
    }

    public void setLatestReport(ScLearningReport latestReport)
    {
        this.latestReport = latestReport;
    }

    public List<RecommendationItemVo> getRecommendations()
    {
        return recommendations;
    }

    public void setRecommendations(List<RecommendationItemVo> recommendations)
    {
        this.recommendations = recommendations;
    }
}
