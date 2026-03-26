package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

public class ScCourseSelectionPlan extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long planId;
    private String planName;
    private Long termId;
    private String termName;
    private String planType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date selectionStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date selectionEndTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dropStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dropEndTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date requestStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date requestEndTime;
    private String noticeContent;
    private String ruleContent;
    private String status;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public Date getSelectionStartTime() {
        return selectionStartTime;
    }

    public void setSelectionStartTime(Date selectionStartTime) {
        this.selectionStartTime = selectionStartTime;
    }

    public Date getSelectionEndTime() {
        return selectionEndTime;
    }

    public void setSelectionEndTime(Date selectionEndTime) {
        this.selectionEndTime = selectionEndTime;
    }

    public Date getDropStartTime() {
        return dropStartTime;
    }

    public void setDropStartTime(Date dropStartTime) {
        this.dropStartTime = dropStartTime;
    }

    public Date getDropEndTime() {
        return dropEndTime;
    }

    public void setDropEndTime(Date dropEndTime) {
        this.dropEndTime = dropEndTime;
    }

    public Date getRequestStartTime() {
        return requestStartTime;
    }

    public void setRequestStartTime(Date requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public Date getRequestEndTime() {
        return requestEndTime;
    }

    public void setRequestEndTime(Date requestEndTime) {
        this.requestEndTime = requestEndTime;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
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
                .append("planId", getPlanId())
                .append("planName", getPlanName())
                .append("termId", getTermId())
                .append("termName", getTermName())
                .append("planType", getPlanType())
                .append("selectionStartTime", getSelectionStartTime())
                .append("selectionEndTime", getSelectionEndTime())
                .append("dropStartTime", getDropStartTime())
                .append("dropEndTime", getDropEndTime())
                .append("requestStartTime", getRequestStartTime())
                .append("requestEndTime", getRequestEndTime())
                .append("noticeContent", getNoticeContent())
                .append("ruleContent", getRuleContent())
                .append("status", getStatus())
                .append("remark", getRemark())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
