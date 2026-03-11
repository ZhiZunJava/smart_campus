package com.smart.system.domain.campusvo;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 推荐展示 VO
 *
 * @author Codex
 */
public class RecommendationItemVo
{
    private Long recommendId;
    private Long bizId;
    private String bizType;
    private String title;
    private String resourceType;
    private String summary;
    private String recommendReason;
    private BigDecimal recommendScore;
    private String sceneCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    public Long getRecommendId() { return recommendId; }
    public void setRecommendId(Long recommendId) { this.recommendId = recommendId; }
    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getRecommendReason() { return recommendReason; }
    public void setRecommendReason(String recommendReason) { this.recommendReason = recommendReason; }
    public BigDecimal getRecommendScore() { return recommendScore; }
    public void setRecommendScore(BigDecimal recommendScore) { this.recommendScore = recommendScore; }
    public String getSceneCode() { return sceneCode; }
    public void setSceneCode(String sceneCode) { this.sceneCode = sceneCode; }
    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }
}
