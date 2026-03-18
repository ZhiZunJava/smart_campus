package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 问答消息对象 sc_qa_message
 *
 * @author can
 */
public class ScQaMessage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long messageId;
    private Long sessionId;
    private String roleType;
    private String contentType;
    private String content;
    private String referenceSource;
    private String attachmentJson;
    private Long replyToMessageId;
    private String modelName;
    private String reasoningContent;
    private Integer tokenCount;
    private Integer latencyMs;
    private String sensitiveFlag;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReferenceSource() {
        return referenceSource;
    }

    public void setReferenceSource(String referenceSource) {
        this.referenceSource = referenceSource;
    }

    public String getAttachmentJson() {
        return attachmentJson;
    }

    public void setAttachmentJson(String attachmentJson) {
        this.attachmentJson = attachmentJson;
    }

    public Long getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(Long replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getReasoningContent() {
        return reasoningContent;
    }

    public void setReasoningContent(String reasoningContent) {
        this.reasoningContent = reasoningContent;
    }

    public Integer getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(Integer tokenCount) {
        this.tokenCount = tokenCount;
    }

    public Integer getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Integer latencyMs) {
        this.latencyMs = latencyMs;
    }

    public String getSensitiveFlag() {
        return sensitiveFlag;
    }

    public void setSensitiveFlag(String sensitiveFlag) {
        this.sensitiveFlag = sensitiveFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("messageId", getMessageId())
                .append("sessionId", getSessionId())
                .append("roleType", getRoleType())
                .append("contentType", getContentType())
                .append("content", getContent())
                .append("referenceSource", getReferenceSource())
                .append("attachmentJson", getAttachmentJson())
                .append("replyToMessageId", getReplyToMessageId())
                .append("modelName", getModelName())
                .append("reasoningContent", getReasoningContent())
                .append("tokenCount", getTokenCount())
                .append("latencyMs", getLatencyMs())
                .append("sensitiveFlag", getSensitiveFlag())
                .append("createTime", getCreateTime())
                .toString();
    }
}
