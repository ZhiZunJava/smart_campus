package com.smart.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 题库对象 sc_question_bank
 *
 * @author can
 */
public class ScQuestionBank extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long questionId;
    private Long catalogId;
    private Long courseId;
    private Long chapterId;
    private Long knowledgePointId;
    private String questionType;
    private Integer difficultyLevel;
    private String stem;
    private String answer;
    private String analysis;
    private String source;
    private String questionTags;
    private String materialContent;
    private String sourceBatchNo;
    private BigDecimal qualityScore;
    private String status;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
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

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getQuestionTags() {
        return questionTags;
    }

    public void setQuestionTags(String questionTags) {
        this.questionTags = questionTags;
    }

    public String getMaterialContent() {
        return materialContent;
    }

    public void setMaterialContent(String materialContent) {
        this.materialContent = materialContent;
    }

    public String getSourceBatchNo() {
        return sourceBatchNo;
    }

    public void setSourceBatchNo(String sourceBatchNo) {
        this.sourceBatchNo = sourceBatchNo;
    }

    public BigDecimal getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(BigDecimal qualityScore) {
        this.qualityScore = qualityScore;
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
                .append("questionId", getQuestionId())
                .append("catalogId", getCatalogId())
                .append("courseId", getCourseId())
                .append("chapterId", getChapterId())
                .append("knowledgePointId", getKnowledgePointId())
                .append("questionType", getQuestionType())
                .append("difficultyLevel", getDifficultyLevel())
                .append("stem", getStem())
                .append("answer", getAnswer())
                .append("analysis", getAnalysis())
                .append("source", getSource())
                .append("questionTags", getQuestionTags())
                .append("materialContent", getMaterialContent())
                .append("sourceBatchNo", getSourceBatchNo())
                .append("qualityScore", getQualityScore())
                .append("status", getStatus())
                .toString();
    }
}
