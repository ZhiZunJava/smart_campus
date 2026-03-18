package com.smart.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 学习画像对象 sc_learning_profile
 *
 * @author can
 */
public class ScLearningProfile extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long profileId;
    private Long userId;
    private Long courseId;
    private String abilityLevel;
    private BigDecimal concentrationScore;
    private BigDecimal activeScore;
    private BigDecimal masteryScore;
    private BigDecimal interestScore;
    private BigDecimal riskScore;
    private Integer profileVersion;
    private String featureJson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastCalculatedTime;

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

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

    public BigDecimal getConcentrationScore() {
        return concentrationScore;
    }

    public void setConcentrationScore(BigDecimal concentrationScore) {
        this.concentrationScore = concentrationScore;
    }

    public BigDecimal getActiveScore() {
        return activeScore;
    }

    public void setActiveScore(BigDecimal activeScore) {
        this.activeScore = activeScore;
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

    public Integer getProfileVersion() {
        return profileVersion;
    }

    public void setProfileVersion(Integer profileVersion) {
        this.profileVersion = profileVersion;
    }

    public String getFeatureJson() {
        return featureJson;
    }

    public void setFeatureJson(String featureJson) {
        this.featureJson = featureJson;
    }

    public Date getLastCalculatedTime() {
        return lastCalculatedTime;
    }

    public void setLastCalculatedTime(Date lastCalculatedTime) {
        this.lastCalculatedTime = lastCalculatedTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("profileId", getProfileId())
                .append("userId", getUserId())
                .append("courseId", getCourseId())
                .append("abilityLevel", getAbilityLevel())
                .append("concentrationScore", getConcentrationScore())
                .append("activeScore", getActiveScore())
                .append("masteryScore", getMasteryScore())
                .append("interestScore", getInterestScore())
                .append("riskScore", getRiskScore())
                .append("profileVersion", getProfileVersion())
                .append("featureJson", getFeatureJson())
                .append("lastCalculatedTime", getLastCalculatedTime())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
