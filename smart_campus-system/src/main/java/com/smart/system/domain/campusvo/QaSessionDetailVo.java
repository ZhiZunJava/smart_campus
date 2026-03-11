package com.smart.system.domain.campusvo;

import java.util.List;
import com.smart.system.domain.ScQaMessage;

/**
 * 问答会话详情 VO
 *
 * @author Codex
 */
public class QaSessionDetailVo
{
    private Long sessionId;
    private Long userId;
    private Long courseId;
    private String sessionTitle;
    private String sourceType;
    private String status;
    private List<ScQaMessage> messages;

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getSessionTitle() { return sessionTitle; }
    public void setSessionTitle(String sessionTitle) { this.sessionTitle = sessionTitle; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<ScQaMessage> getMessages() { return messages; }
    public void setMessages(List<ScQaMessage> messages) { this.messages = messages; }
}
