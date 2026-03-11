package com.smart.system.domain.dto;

/**
 * 考试作答提交 DTO
 *
 * @author Codex
 */
public class ExamAnswerSubmitDto
{
    private Long questionId;
    private String userAnswer;

    public Long getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Long questionId)
    {
        this.questionId = questionId;
    }

    public String getUserAnswer()
    {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer)
    {
        this.userAnswer = userAnswer;
    }
}
