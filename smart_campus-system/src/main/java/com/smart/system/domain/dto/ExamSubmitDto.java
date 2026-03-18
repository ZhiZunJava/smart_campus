package com.smart.system.domain.dto;

import java.util.List;

/**
 * 提交考试 DTO
 *
 * @author can
 */
public class ExamSubmitDto {
    private Long recordId;
    private List<ExamAnswerSubmitDto> answers;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public List<ExamAnswerSubmitDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ExamAnswerSubmitDto> answers) {
        this.answers = answers;
    }
}
