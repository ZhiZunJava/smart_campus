package com.smart.system.domain.campusvo;

import java.math.BigDecimal;

/**
 * 学习画像概览 VO
 *
 * @author Codex
 */
public class LearningProfileOverviewVo
{
    private Long userId;
    private Long courseId;
    private String abilityLevel;
    private BigDecimal activeScore;
    private BigDecimal concentrationScore;
    private BigDecimal masteryScore;
    private BigDecimal interestScore;
    private BigDecimal riskScore;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getAbilityLevel() { return abilityLevel; }
    public void setAbilityLevel(String abilityLevel) { this.abilityLevel = abilityLevel; }
    public BigDecimal getActiveScore() { return activeScore; }
    public void setActiveScore(BigDecimal activeScore) { this.activeScore = activeScore; }
    public BigDecimal getConcentrationScore() { return concentrationScore; }
    public void setConcentrationScore(BigDecimal concentrationScore) { this.concentrationScore = concentrationScore; }
    public BigDecimal getMasteryScore() { return masteryScore; }
    public void setMasteryScore(BigDecimal masteryScore) { this.masteryScore = masteryScore; }
    public BigDecimal getInterestScore() { return interestScore; }
    public void setInterestScore(BigDecimal interestScore) { this.interestScore = interestScore; }
    public BigDecimal getRiskScore() { return riskScore; }
    public void setRiskScore(BigDecimal riskScore) { this.riskScore = riskScore; }
}
