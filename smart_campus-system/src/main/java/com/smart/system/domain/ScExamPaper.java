package com.smart.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 试卷对象 sc_exam_paper
 *
 * @author can
 */
public class ScExamPaper extends BaseEntity {
    private static final long serialVersionUID = 1L;

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
    private Date publishStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishEndTime;
    private Long strategyTemplateId;
    private BigDecimal passScore;
    private String markingMode;
    private String questionOrderMode;
    private String questionNavigationMode;
    private String allowReviewAnalysis;
    private String antiCheatEnabled;
    private Integer maxFocusLossCount;
    private String autoSubmitOnFocusLossLimit;
    private String allowCopyPaste;
    private Integer maxAttemptCount;
    private String allowViewScore;
    private String sectionConfigJson;
    private String adaptiveRuleJson;
    private String status;

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

    public String getQuestionNavigationMode() {
        return questionNavigationMode;
    }

    public void setQuestionNavigationMode(String questionNavigationMode) {
        this.questionNavigationMode = questionNavigationMode;
    }

    public String getAllowReviewAnalysis() {
        return allowReviewAnalysis;
    }

    public void setAllowReviewAnalysis(String allowReviewAnalysis) {
        this.allowReviewAnalysis = allowReviewAnalysis;
    }

    public String getAntiCheatEnabled() {
        return antiCheatEnabled;
    }

    public void setAntiCheatEnabled(String antiCheatEnabled) {
        this.antiCheatEnabled = antiCheatEnabled;
    }

    public Integer getMaxFocusLossCount() {
        return maxFocusLossCount;
    }

    public void setMaxFocusLossCount(Integer maxFocusLossCount) {
        this.maxFocusLossCount = maxFocusLossCount;
    }

    public String getAutoSubmitOnFocusLossLimit() {
        return autoSubmitOnFocusLossLimit;
    }

    public void setAutoSubmitOnFocusLossLimit(String autoSubmitOnFocusLossLimit) {
        this.autoSubmitOnFocusLossLimit = autoSubmitOnFocusLossLimit;
    }

    public String getAllowCopyPaste() {
        return allowCopyPaste;
    }

    public void setAllowCopyPaste(String allowCopyPaste) {
        this.allowCopyPaste = allowCopyPaste;
    }

    public Integer getMaxAttemptCount() {
        return maxAttemptCount;
    }

    public void setMaxAttemptCount(Integer maxAttemptCount) {
        this.maxAttemptCount = maxAttemptCount;
    }

    public String getAllowViewScore() {
        return allowViewScore;
    }

    public void setAllowViewScore(String allowViewScore) {
        this.allowViewScore = allowViewScore;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("paperId", getPaperId())
                .append("parentPaperId", getParentPaperId())
                .append("paperName", getPaperName())
                .append("courseId", getCourseId())
                .append("paperType", getPaperType())
                .append("paperLevel", getPaperLevel())
                .append("answerMode", getAnswerMode())
                .append("paperWeight", getPaperWeight())
                .append("sortNo", getSortNo())
                .append("totalScore", getTotalScore())
                .append("durationMinutes", getDurationMinutes())
                .append("publishStatus", getPublishStatus())
                .append("publishStartTime", getPublishStartTime())
                .append("publishEndTime", getPublishEndTime())
                .append("strategyTemplateId", getStrategyTemplateId())
                .append("passScore", getPassScore())
                .append("markingMode", getMarkingMode())
                .append("questionOrderMode", getQuestionOrderMode())
                .append("questionNavigationMode", getQuestionNavigationMode())
                .append("allowReviewAnalysis", getAllowReviewAnalysis())
                .append("antiCheatEnabled", getAntiCheatEnabled())
                .append("maxFocusLossCount", getMaxFocusLossCount())
                .append("autoSubmitOnFocusLossLimit", getAutoSubmitOnFocusLossLimit())
                .append("allowCopyPaste", getAllowCopyPaste())
                .append("maxAttemptCount", getMaxAttemptCount())
                .append("allowViewScore", getAllowViewScore())
                .append("sectionConfigJson", getSectionConfigJson())
                .append("adaptiveRuleJson", getAdaptiveRuleJson())
                .append("status", getStatus())
                .toString();
    }
}
