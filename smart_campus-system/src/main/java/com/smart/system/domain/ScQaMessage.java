package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 问答消息对象 sc_qa_message
 *
 * @author Codex
 */
public class ScQaMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long messageId;
    private Long sessionId;
    private String roleType;
    private String content;
    private String referenceSource;
    private Integer tokenCount;
    private Integer latencyMs;
    private String sensitiveFlag;

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getReferenceSource() { return referenceSource; }
    public void setReferenceSource(String referenceSource) { this.referenceSource = referenceSource; }
    public Integer getTokenCount() { return tokenCount; }
    public void setTokenCount(Integer tokenCount) { this.tokenCount = tokenCount; }
    public Integer getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Integer latencyMs) { this.latencyMs = latencyMs; }
    public String getSensitiveFlag() { return sensitiveFlag; }
    public void setSensitiveFlag(String sensitiveFlag) { this.sensitiveFlag = sensitiveFlag; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("messageId", getMessageId())
            .append("sessionId", getSessionId())
            .append("roleType", getRoleType())
            .append("content", getContent())
            .append("referenceSource", getReferenceSource())
            .append("tokenCount", getTokenCount())
            .append("latencyMs", getLatencyMs())
            .append("sensitiveFlag", getSensitiveFlag())
            .append("createTime", getCreateTime())
            .toString();
    }
}
