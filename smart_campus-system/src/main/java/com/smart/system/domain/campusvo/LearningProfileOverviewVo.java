package com.smart.system.domain.campusvo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 学习画像概览 VO
 *
 * @author can
 */
public class LearningProfileOverviewVo {
    private Long userId;
    private Long courseId;
    private String abilityLevel;
    private BigDecimal activeScore;
    private BigDecimal concentrationScore;
    private BigDecimal masteryScore;
    private BigDecimal interestScore;
    private BigDecimal riskScore;
    private String portraitTitle;
    private String aiSummary;
    private String learningPattern;
    private Integer aiConfidence;
    private List<String> strengths;
    private List<String> risks;
    private List<String> aiSuggestions;

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

    public String getAbilityLevel() {
        return abilityLevel;
    }

    public void setAbilityLevel(String abilityLevel) {
        this.abilityLevel = abilityLevel;
    }

    public BigDecimal getActiveScore() {
        return activeScore;
    }

    public void setActiveScore(BigDecimal activeScore) {
        this.activeScore = activeScore;
    }

    public BigDecimal getConcentrationScore() {
        return concentrationScore;
    }

    public void setConcentrationScore(BigDecimal concentrationScore) {
        this.concentrationScore = concentrationScore;
    }

    public BigDecimal getMasteryScore() {
        return masteryScore;
    }

    public void setMasteryScore(BigDecimal masteryScore) {
        this.masteryScore = masteryScore;
    }

    public BigDecimal getInterestScore() {
        return interestScore;
    }

    public void setInterestScore(BigDecimal interestScore) {
        this.interestScore = interestScore;
    }

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
    }

    public String getPortraitTitle() {
        return portraitTitle;
    }

    public void setPortraitTitle(String portraitTitle) {
        this.portraitTitle = portraitTitle;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public String getLearningPattern() {
        return learningPattern;
    }

    public void setLearningPattern(String learningPattern) {
        this.learningPattern = learningPattern;
    }

    public Integer getAiConfidence() {
        return aiConfidence;
    }

    public void setAiConfidence(Integer aiConfidence) {
        this.aiConfidence = aiConfidence;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getRisks() {
        return risks;
    }

    public void setRisks(List<String> risks) {
        this.risks = risks;
    }

    public List<String> getAiSuggestions() {
        return aiSuggestions;
    }

    public void setAiSuggestions(List<String> aiSuggestions) {
        this.aiSuggestions = aiSuggestions;
    }
}
