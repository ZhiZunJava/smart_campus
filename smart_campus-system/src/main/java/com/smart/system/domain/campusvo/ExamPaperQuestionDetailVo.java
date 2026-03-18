package com.smart.system.domain.campusvo;

import java.math.BigDecimal;

/**
 * 试卷题目详情 VO
 *
 * @author can
 */
public class ExamPaperQuestionDetailVo {
    private Long id;
    private Long paperId;
    private Long questionId;
    private BigDecimal score;
    private Integer sortNo;
    private QuestionDetailVo question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public QuestionDetailVo getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDetailVo question) {
        this.question = question;
    }
}
