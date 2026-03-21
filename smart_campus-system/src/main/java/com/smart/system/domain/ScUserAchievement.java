package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

public class ScUserAchievement extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long achievementId;
    private Long userId;
    private String achievementCode;
    private String achievementTitle;
    private String achievementDesc;
    private String status;
    private Integer progressValue;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date earnedTime;

    public Long getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Long achievementId) {
        this.achievementId = achievementId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAchievementCode() {
        return achievementCode;
    }

    public void setAchievementCode(String achievementCode) {
        this.achievementCode = achievementCode;
    }

    public String getAchievementTitle() {
        return achievementTitle;
    }

    public void setAchievementTitle(String achievementTitle) {
        this.achievementTitle = achievementTitle;
    }

    public String getAchievementDesc() {
        return achievementDesc;
    }

    public void setAchievementDesc(String achievementDesc) {
        this.achievementDesc = achievementDesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(Integer progressValue) {
        this.progressValue = progressValue;
    }

    public Date getEarnedTime() {
        return earnedTime;
    }

    public void setEarnedTime(Date earnedTime) {
        this.earnedTime = earnedTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("achievementId", getAchievementId())
                .append("userId", getUserId())
                .append("achievementCode", getAchievementCode())
                .append("achievementTitle", getAchievementTitle())
                .append("status", getStatus())
                .append("progressValue", getProgressValue())
                .append("earnedTime", getEarnedTime())
                .toString();
    }
}
