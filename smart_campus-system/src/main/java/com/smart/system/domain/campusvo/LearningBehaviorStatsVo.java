package com.smart.system.domain.campusvo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class LearningBehaviorStatsVo {
    private Integer totalBehaviorCount;
    private Integer totalDurationSeconds;
    private Integer studyDayCount;
    private Integer learnedResourceCount;
    private BigDecimal avgProgressRate;
    private BigDecimal avgExamScore;
    private BigDecimal avgCorrectRate;
    private Integer wrongQuestionCount;
    private Integer completedBehaviorCount;
    private Map<String, Integer> behaviorTypeDistribution;
    private Map<String, Integer> deviceTypeDistribution;
    private List<String> recentActivityTrend;

    public Integer getTotalBehaviorCount() {
        return totalBehaviorCount;
    }

    public void setTotalBehaviorCount(Integer totalBehaviorCount) {
        this.totalBehaviorCount = totalBehaviorCount;
    }

    public Integer getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public void setTotalDurationSeconds(Integer totalDurationSeconds) {
        this.totalDurationSeconds = totalDurationSeconds;
    }

    public Integer getStudyDayCount() {
        return studyDayCount;
    }

    public void setStudyDayCount(Integer studyDayCount) {
        this.studyDayCount = studyDayCount;
    }

    public Integer getLearnedResourceCount() {
        return learnedResourceCount;
    }

    public void setLearnedResourceCount(Integer learnedResourceCount) {
        this.learnedResourceCount = learnedResourceCount;
    }

    public BigDecimal getAvgProgressRate() {
        return avgProgressRate;
    }

    public void setAvgProgressRate(BigDecimal avgProgressRate) {
        this.avgProgressRate = avgProgressRate;
    }

    public BigDecimal getAvgExamScore() {
        return avgExamScore;
    }

    public void setAvgExamScore(BigDecimal avgExamScore) {
        this.avgExamScore = avgExamScore;
    }

    public BigDecimal getAvgCorrectRate() {
        return avgCorrectRate;
    }

    public void setAvgCorrectRate(BigDecimal avgCorrectRate) {
        this.avgCorrectRate = avgCorrectRate;
    }

    public Integer getWrongQuestionCount() {
        return wrongQuestionCount;
    }

    public void setWrongQuestionCount(Integer wrongQuestionCount) {
        this.wrongQuestionCount = wrongQuestionCount;
    }

    public Integer getCompletedBehaviorCount() {
        return completedBehaviorCount;
    }

    public void setCompletedBehaviorCount(Integer completedBehaviorCount) {
        this.completedBehaviorCount = completedBehaviorCount;
    }

    public Map<String, Integer> getBehaviorTypeDistribution() {
        return behaviorTypeDistribution;
    }

    public void setBehaviorTypeDistribution(Map<String, Integer> behaviorTypeDistribution) {
        this.behaviorTypeDistribution = behaviorTypeDistribution;
    }

    public Map<String, Integer> getDeviceTypeDistribution() {
        return deviceTypeDistribution;
    }

    public void setDeviceTypeDistribution(Map<String, Integer> deviceTypeDistribution) {
        this.deviceTypeDistribution = deviceTypeDistribution;
    }

    public List<String> getRecentActivityTrend() {
        return recentActivityTrend;
    }

    public void setRecentActivityTrend(List<String> recentActivityTrend) {
        this.recentActivityTrend = recentActivityTrend;
    }
}
