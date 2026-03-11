package com.smart.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 个性化推荐对象 sc_learning_recommendation
 *
 * @author Codex
 */
public class ScLearningRecommendation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long recommendId;
    private Long userId;
    private String bizType;
    private Long bizId;
    private String recommendReason;
    private BigDecimal recommendScore;
    private String sourceType;
    private String sceneCode;
    private String exposeStatus;
    private String clickStatus;
    private String feedbackStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    public Long getRecommendId() { return recommendId; }
    public void setRecommendId(Long recommendId) { this.recommendId = recommendId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }
    public String getRecommendReason() { return recommendReason; }
    public void setRecommendReason(String recommendReason) { this.recommendReason = recommendReason; }
    public BigDecimal getRecommendScore() { return recommendScore; }
    public void setRecommendScore(BigDecimal recommendScore) { this.recommendScore = recommendScore; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSceneCode() { return sceneCode; }
    public void setSceneCode(String sceneCode) { this.sceneCode = sceneCode; }
    public String getExposeStatus() { return exposeStatus; }
    public void setExposeStatus(String exposeStatus) { this.exposeStatus = exposeStatus; }
    public String getClickStatus() { return clickStatus; }
    public void setClickStatus(String clickStatus) { this.clickStatus = clickStatus; }
    public String getFeedbackStatus() { return feedbackStatus; }
    public void setFeedbackStatus(String feedbackStatus) { this.feedbackStatus = feedbackStatus; }
    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("recommendId", getRecommendId())
            .append("userId", getUserId())
            .append("bizType", getBizType())
            .append("bizId", getBizId())
            .append("recommendReason", getRecommendReason())
            .append("recommendScore", getRecommendScore())
            .append("sourceType", getSourceType())
            .append("sceneCode", getSceneCode())
            .append("exposeStatus", getExposeStatus())
            .append("clickStatus", getClickStatus())
            .append("feedbackStatus", getFeedbackStatus())
            .append("expireTime", getExpireTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
