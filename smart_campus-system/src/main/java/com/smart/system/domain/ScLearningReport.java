package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 学情报告对象 sc_learning_report
 *
 * @author Codex
 */
public class ScLearningReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long reportId;
    private Long userId;
    private String reportType;
    private String reportContent;
    private String reportJson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date generateTime;

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getReportContent() { return reportContent; }
    public void setReportContent(String reportContent) { this.reportContent = reportContent; }
    public String getReportJson() { return reportJson; }
    public void setReportJson(String reportJson) { this.reportJson = reportJson; }
    public Date getGenerateTime() { return generateTime; }
    public void setGenerateTime(Date generateTime) { this.generateTime = generateTime; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("reportId", getReportId())
            .append("userId", getUserId())
            .append("reportType", getReportType())
            .append("reportContent", getReportContent())
            .append("reportJson", getReportJson())
            .append("generateTime", getGenerateTime())
            .toString();
    }
}
