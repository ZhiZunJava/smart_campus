package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 问答会话对象 sc_qa_session
 *
 * @author can
 */
public class ScQaSession extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long sessionId;
    private Long userId;
    private Long courseId;
    private String sessionTitle;
    private String sourceType;
    private String status;
    private Integer messageCount;
    private String lastMessagePreview;
    private java.util.Date lastMessageTime;
    private String contextSummary;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
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

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public String getLastMessagePreview() {
        return lastMessagePreview;
    }

    public void setLastMessagePreview(String lastMessagePreview) {
        this.lastMessagePreview = lastMessagePreview;
    }

    public java.util.Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(java.util.Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getContextSummary() {
        return contextSummary;
    }

    public void setContextSummary(String contextSummary) {
        this.contextSummary = contextSummary;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("sessionId", getSessionId())
                .append("userId", getUserId())
                .append("courseId", getCourseId())
                .append("sessionTitle", getSessionTitle())
                .append("sourceType", getSourceType())
                .append("status", getStatus())
                .append("messageCount", getMessageCount())
                .append("lastMessagePreview", getLastMessagePreview())
                .append("lastMessageTime", getLastMessageTime())
                .append("contextSummary", getContextSummary())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
