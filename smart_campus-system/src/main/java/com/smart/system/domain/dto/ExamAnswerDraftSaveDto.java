package com.smart.system.domain.dto;

/**
 * 考试答案草稿保存 DTO
 */
public class ExamAnswerDraftSaveDto {
    private Long recordId;
    private Long questionId;
    private String userAnswer;
    private Long sessionPaperId;

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
}
