package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 问答反馈对象 sc_qa_feedback
 *
 * @author Codex
 */
public class ScQaFeedback extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long messageId;
    private Long userId;
    private String feedbackType;
    private String feedbackContent;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFeedbackType() { return feedbackType; }
    public void setFeedbackType(String feedbackType) { this.feedbackType = feedbackType; }
    public String getFeedbackContent() { return feedbackContent; }
    public void setFeedbackContent(String feedbackContent) { this.feedbackContent = feedbackContent; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("messageId", getMessageId())
            .append("userId", getUserId())
            .append("feedbackType", getFeedbackType())
            .append("feedbackContent", getFeedbackContent())
            .append("createTime", getCreateTime())
            .toString();
    }
}
