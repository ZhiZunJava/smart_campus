package com.smart.system.domain.dto;

/**
 * 推荐反馈 DTO
 *
 * @author Codex
 */
public class RecommendationFeedbackDto
{
    private Long recommendId;
    private String feedbackStatus;
    private String clickStatus;
    private String exposeStatus;

    public Long getRecommendId()
    {
        return recommendId;
    }

    public void setRecommendId(Long recommendId)
    {
        this.recommendId = recommendId;
    }

    public String getFeedbackStatus()
    {
        return feedbackStatus;
    }

    public void setFeedbackStatus(String feedbackStatus)
    {
        this.feedbackStatus = feedbackStatus;
    }

    public String getClickStatus()
    {
        return clickStatus;
    }

    public void setClickStatus(String clickStatus)
    {
        this.clickStatus = clickStatus;
    }

    public String getExposeStatus()
    {
        return exposeStatus;
    }

    public void setExposeStatus(String exposeStatus)
    {
        this.exposeStatus = exposeStatus;
    }
}
