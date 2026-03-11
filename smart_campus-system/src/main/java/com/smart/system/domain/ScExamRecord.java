package com.smart.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 考试记录对象 sc_exam_record
 *
 * @author Codex
 */
public class ScExamRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long recordId;
    private Long paperId;
    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;
    private BigDecimal score;
    private BigDecimal correctRate;
    private String examStatus;
    private String cheatFlag;
    private String analysisJson;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getSubmitTime() { return submitTime; }
    public void setSubmitTime(Date submitTime) { this.submitTime = submitTime; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public BigDecimal getCorrectRate() { return correctRate; }
    public void setCorrectRate(BigDecimal correctRate) { this.correctRate = correctRate; }
    public String getExamStatus() { return examStatus; }
    public void setExamStatus(String examStatus) { this.examStatus = examStatus; }
    public String getCheatFlag() { return cheatFlag; }
    public void setCheatFlag(String cheatFlag) { this.cheatFlag = cheatFlag; }
    public String getAnalysisJson() { return analysisJson; }
    public void setAnalysisJson(String analysisJson) { this.analysisJson = analysisJson; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("paperId", getPaperId())
            .append("userId", getUserId())
            .append("startTime", getStartTime())
            .append("submitTime", getSubmitTime())
            .append("score", getScore())
            .append("correctRate", getCorrectRate())
            .append("examStatus", getExamStatus())
            .append("cheatFlag", getCheatFlag())
            .append("analysisJson", getAnalysisJson())
            .toString();
    }
}
