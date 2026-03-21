package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 考试答案草稿对象 sc_exam_answer_draft
 */
public class ScExamAnswerDraft extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long draftId;
    private Long recordId;
    private Long sessionId;
    private Long sessionPaperId;
    private Long questionId;
    private Long userId;
    private String userAnswer;
    private Integer saveCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastSaveTime;
    private String status;

    public Long getDraftId() {
        return draftId;
    }

    public void setDraftId(Long draftId) {
        this.draftId = draftId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSessionPaperId() {
        return sessionPaperId;
    }

    public void setSessionPaperId(Long sessionPaperId) {
        this.sessionPaperId = sessionPaperId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Integer getSaveCount() {
        return saveCount;
    }

    public void setSaveCount(Integer saveCount) {
        this.saveCount = saveCount;
    }

    public Date getLastSaveTime() {
        return lastSaveTime;
    }

    public void setLastSaveTime(Date lastSaveTime) {
        this.lastSaveTime = lastSaveTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("draftId", getDraftId())
                .append("recordId", getRecordId())
                .append("sessionId", getSessionId())
                .append("sessionPaperId", getSessionPaperId())
                .append("questionId", getQuestionId())
                .append("userId", getUserId())
                .append("saveCount", getSaveCount())
                .append("lastSaveTime", getLastSaveTime())
                .append("status", getStatus())
                .toString();
    }
}

