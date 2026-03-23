package com.smart.system.domain.dto;

/**
 * 考试答案草稿保存 DTO
 */
public class ExamAnswerDraftSaveDto {
    private Long recordId;
    private Long questionId;
    private String userAnswer;
    private Long sessionPaperId;
    private Integer focusLossCount;
    private String focusLossSource;
    private Long focusLossOccurredAt;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Long getSessionPaperId() {
        return sessionPaperId;
    }

    public void setSessionPaperId(Long sessionPaperId) {
        this.sessionPaperId = sessionPaperId;
    }

    public Integer getFocusLossCount() {
        return focusLossCount;
    }

    public void setFocusLossCount(Integer focusLossCount) {
        this.focusLossCount = focusLossCount;
    }

    public String getFocusLossSource() {
        return focusLossSource;
    }

    public void setFocusLossSource(String focusLossSource) {
        this.focusLossSource = focusLossSource;
    }

    public Long getFocusLossOccurredAt() {
        return focusLossOccurredAt;
    }

    public void setFocusLossOccurredAt(Long focusLossOccurredAt) {
        this.focusLossOccurredAt = focusLossOccurredAt;
    }
}
