package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

public class ScStudentStatusLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long statusLogId;
    private Long requestId;
    private Long studentUserId;
    private String studentName;
    private String studentNo;
    private String beforeStatusCode;
    private String beforeStatusName;
    private String afterStatusCode;
    private String afterStatusName;
    private String businessCode;
    private String businessName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDate;
    private String detailJson;
    private Long operatorUserId;
    private String operatorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getStatusLogId() {
        return statusLogId;
    }

    public void setStatusLogId(Long statusLogId) {
        this.statusLogId = statusLogId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getStudentUserId() {
        return studentUserId;
    }

    public void setStudentUserId(Long studentUserId) {
        this.studentUserId = studentUserId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getBeforeStatusCode() {
        return beforeStatusCode;
    }

    public void setBeforeStatusCode(String beforeStatusCode) {
        this.beforeStatusCode = beforeStatusCode;
    }

    public String getBeforeStatusName() {
        return beforeStatusName;
    }

    public void setBeforeStatusName(String beforeStatusName) {
        this.beforeStatusName = beforeStatusName;
    }

    public String getAfterStatusCode() {
        return afterStatusCode;
    }

    public void setAfterStatusCode(String afterStatusCode) {
        this.afterStatusCode = afterStatusCode;
    }

    public String getAfterStatusName() {
        return afterStatusName;
    }

    public void setAfterStatusName(String afterStatusName) {
        this.afterStatusName = afterStatusName;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getDetailJson() {
        return detailJson;
    }

    public void setDetailJson(String detailJson) {
        this.detailJson = detailJson;
    }

    public Long getOperatorUserId() {
        return operatorUserId;
    }

    public void setOperatorUserId(Long operatorUserId) {
        this.operatorUserId = operatorUserId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("statusLogId", getStatusLogId())
                .append("requestId", getRequestId())
                .append("studentUserId", getStudentUserId())
                .append("studentName", getStudentName())
                .append("studentNo", getStudentNo())
                .append("beforeStatusCode", getBeforeStatusCode())
                .append("beforeStatusName", getBeforeStatusName())
                .append("afterStatusCode", getAfterStatusCode())
                .append("afterStatusName", getAfterStatusName())
                .append("businessCode", getBusinessCode())
                .append("businessName", getBusinessName())
                .append("effectiveDate", getEffectiveDate())
                .append("detailJson", getDetailJson())
                .append("operatorUserId", getOperatorUserId())
                .append("operatorName", getOperatorName())
                .append("createTime", getCreateTime())
                .append("remark", getRemark())
                .toString();
    }
}
