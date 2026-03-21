package com.smart.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 作答明细对象 sc_exam_answer
 *
 * @author can
 */
public class ScExamAnswer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long answerId;
    private Long sessionId;
    private Long sessionPaperId;
    private Long recordId;
    private Long questionId;
    private String userAnswer;
    private String isCorrect;
    private BigDecimal score;
    private Long knowledgePointId;
    private String questionType;
    private String stem;
    private String standardAnswer;
    private String analysis;

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
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

    public String getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(String isCorrect) {
        this.isCorrect = isCorrect;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Long getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(Long knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getStandardAnswer() {
        return standardAnswer;
    }

    public void setStandardAnswer(String standardAnswer) {
        this.standardAnswer = standardAnswer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("answerId", getAnswerId())
                .append("sessionId", getSessionId())
                .append("sessionPaperId", getSessionPaperId())
                .append("recordId", getRecordId())
                .append("questionId", getQuestionId())
                .append("userAnswer", getUserAnswer())
                .append("isCorrect", getIsCorrect())
                .append("score", getScore())
                .append("knowledgePointId", getKnowledgePointId())
                .append("questionType", getQuestionType())
                .append("stem", getStem())
                .append("standardAnswer", getStandardAnswer())
                .append("analysis", getAnalysis())
                .toString();
    }
}
