package com.smart.system.domain.dto;

import java.util.List;

/**
 * 子试卷提交 DTO
 */
public class ExamSubPaperSubmitDto {
    private Long recordId;
    private Long paperId;
    private Long sessionPaperId;
    private List<ExamAnswerSubmitDto> answers;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getSessionPaperId() {
        return sessionPaperId;
    }

    public void setSessionPaperId(Long sessionPaperId) {
        this.sessionPaperId = sessionPaperId;
    }

    public List<ExamAnswerSubmitDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ExamAnswerSubmitDto> answers) {
        this.answers = answers;
    }
}
