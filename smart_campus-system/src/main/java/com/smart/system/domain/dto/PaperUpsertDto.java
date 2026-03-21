package com.smart.system.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.smart.system.config.json.MultiFormatDateDeserializer;

/**
 * 试卷新增/编辑 DTO
 */
public class PaperUpsertDto {
    private Long paperId;
    private Long parentPaperId;
    private String paperName;
    private Long courseId;
    private String paperType;
    private String paperLevel;
    private String answerMode;
    private BigDecimal paperWeight;
    private Integer sortNo;
    private BigDecimal totalScore;
    private Integer durationMinutes;
    private String publishStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private Date publishStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private Date publishEndTime;
    private Long strategyTemplateId;
    private BigDecimal passScore;
    private String markingMode;
    private String questionOrderMode;
    private String allowReviewAnalysis;
    private String sectionConfigJson;
    private String adaptiveRuleJson;
    private String status;
    private String remark;
    private List<PaperQuestionConfigDto> questions;
    private List<PaperUpsertDto> subPapers;

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getParentPaperId() {
        return parentPaperId;
    }

    public void setParentPaperId(Long parentPaperId) {
        this.parentPaperId = parentPaperId;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public String getPaperLevel() {
        return paperLevel;
    }

    public void setPaperLevel(String paperLevel) {
        this.paperLevel = paperLevel;
    }

    public String getAnswerMode() {
        return answerMode;
    }

    public void setAnswerMode(String answerMode) {
        this.answerMode = answerMode;
    }

    public BigDecimal getPaperWeight() {
        return paperWeight;
    }

    public void setPaperWeight(BigDecimal paperWeight) {
        this.paperWeight = paperWeight;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Date getPublishStartTime() {
        return publishStartTime;
    }

    public void setPublishStartTime(Date publishStartTime) {
        this.publishStartTime = publishStartTime;
    }

    public Date getPublishEndTime() {
        return publishEndTime;
    }

    public void setPublishEndTime(Date publishEndTime) {
        this.publishEndTime = publishEndTime;
    }

    public Long getStrategyTemplateId() {
        return strategyTemplateId;
    }

    public void setStrategyTemplateId(Long strategyTemplateId) {
        this.strategyTemplateId = strategyTemplateId;
    }

    public BigDecimal getPassScore() {
        return passScore;
    }

    public void setPassScore(BigDecimal passScore) {
        this.passScore = passScore;
    }

    public String getMarkingMode() {
        return markingMode;
    }

    public void setMarkingMode(String markingMode) {
        this.markingMode = markingMode;
    }

    public String getQuestionOrderMode() {
        return questionOrderMode;
    }

    public void setQuestionOrderMode(String questionOrderMode) {
        this.questionOrderMode = questionOrderMode;
    }

    public String getAllowReviewAnalysis() {
        return allowReviewAnalysis;
    }

    public void setAllowReviewAnalysis(String allowReviewAnalysis) {
        this.allowReviewAnalysis = allowReviewAnalysis;
    }

    public String getSectionConfigJson() {
        return sectionConfigJson;
    }

    public void setSectionConfigJson(String sectionConfigJson) {
        this.sectionConfigJson = sectionConfigJson;
    }

    public String getAdaptiveRuleJson() {
        return adaptiveRuleJson;
    }

    public void setAdaptiveRuleJson(String adaptiveRuleJson) {
        this.adaptiveRuleJson = adaptiveRuleJson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<PaperQuestionConfigDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<PaperQuestionConfigDto> questions) {
        this.questions = questions;
    }

    public List<PaperUpsertDto> getSubPapers() {
        return subPapers;
    }

    public void setSubPapers(List<PaperUpsertDto> subPapers) {
        this.subPapers = subPapers;
    }
}
