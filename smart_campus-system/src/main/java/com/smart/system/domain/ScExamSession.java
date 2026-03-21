package com.smart.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 考试会话对象 sc_exam_session
 */
public class ScExamSession extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long sessionId;
    private Long rootPaperId;
    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;
    private String sessionStatus;
    private BigDecimal totalScore;
    private BigDecimal correctRate;
    private Integer focusLossCount;
    private Integer flaggedCount;
    private String analysisJson;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getRootPaperId() {
        return rootPaperId;
    }

    public void setRootPaperId(Long rootPaperId) {
        this.rootPaperId = rootPaperId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public BigDecimal getCorrectRate() {
        return correctRate;
    }

    public void setCorrectRate(BigDecimal correctRate) {
        this.correctRate = correctRate;
    }

    public Integer getFocusLossCount() {
        return focusLossCount;
    }

    public void setFocusLossCount(Integer focusLossCount) {
        this.focusLossCount = focusLossCount;
    }

    public Integer getFlaggedCount() {
        return flaggedCount;
    }

    public void setFlaggedCount(Integer flaggedCount) {
        this.flaggedCount = flaggedCount;
    }

    public String getAnalysisJson() {
        return analysisJson;
    }

    public void setAnalysisJson(String analysisJson) {
        this.analysisJson = analysisJson;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("sessionId", getSessionId())
                .append("rootPaperId", getRootPaperId())
                .append("userId", getUserId())
                .append("startTime", getStartTime())
                .append("submitTime", getSubmitTime())
                .append("sessionStatus", getSessionStatus())
                .append("totalScore", getTotalScore())
                .append("correctRate", getCorrectRate())
                .append("focusLossCount", getFocusLossCount())
                .append("flaggedCount", getFlaggedCount())
                .append("analysisJson", getAnalysisJson())
                .toString();
    }
}

