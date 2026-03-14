package com.smart.system.domain.campusvo;

import java.util.List;
import java.util.Map;

public class AnalysisMetaVo
{
    private Map<String, String> riskLevels;
    private Map<String, String> warningProcessStatuses;
    private Map<String, String> warningLevels;
    private Map<String, String> recommendationFeedbackStatuses;
    private List<String> supportedScenes;

    public Map<String, String> getRiskLevels()
    {
        return riskLevels;
    }

    public void setRiskLevels(Map<String, String> riskLevels)
    {
        this.riskLevels = riskLevels;
    }

    public Map<String, String> getWarningProcessStatuses()
    {
        return warningProcessStatuses;
    }

    public void setWarningProcessStatuses(Map<String, String> warningProcessStatuses)
    {
        this.warningProcessStatuses = warningProcessStatuses;
    }

    public Map<String, String> getWarningLevels()
    {
        return warningLevels;
    }

    public void setWarningLevels(Map<String, String> warningLevels)
    {
        this.warningLevels = warningLevels;
    }

    public Map<String, String> getRecommendationFeedbackStatuses()
    {
        return recommendationFeedbackStatuses;
    }

    public void setRecommendationFeedbackStatuses(Map<String, String> recommendationFeedbackStatuses)
    {
        this.recommendationFeedbackStatuses = recommendationFeedbackStatuses;
    }

    public List<String> getSupportedScenes()
    {
        return supportedScenes;
    }

    public void setSupportedScenes(List<String> supportedScenes)
    {
        this.supportedScenes = supportedScenes;
    }
}
