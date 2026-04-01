package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

public class ScAffairAction extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long actionId;
    private Long requestId;
    private String requestNo;
    private Integer stepIndex;
    private String stepCode;
    private String stepName;
    private String actionType;
    private String actionResult;
    private Long reviewerUserId;
    private String reviewerName;
    private String reviewerRole;
    private String assignmentType;
    private String commentText;
    private String dataSnapshotJson;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public Integer getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(Integer stepIndex) {
        this.stepIndex = stepIndex;
    }

    public String getStepCode() {
        return stepCode;
    }

    public void setStepCode(String stepCode) {
        this.stepCode = stepCode;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionResult() {
        return actionResult;
    }

    public void setActionResult(String actionResult) {
        this.actionResult = actionResult;
    }

    public Long getReviewerUserId() {
        return reviewerUserId;
    }

    public void setReviewerUserId(Long reviewerUserId) {
        this.reviewerUserId = reviewerUserId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerRole() {
        return reviewerRole;
    }

    public void setReviewerRole(String reviewerRole) {
        this.reviewerRole = reviewerRole;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getDataSnapshotJson() {
        return dataSnapshotJson;
    }

    public void setDataSnapshotJson(String dataSnapshotJson) {
        this.dataSnapshotJson = dataSnapshotJson;
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
                .append("actionId", getActionId())
                .append("requestId", getRequestId())
                .append("requestNo", getRequestNo())
                .append("stepIndex", getStepIndex())
                .append("stepCode", getStepCode())
                .append("stepName", getStepName())
                .append("actionType", getActionType())
                .append("actionResult", getActionResult())
                .append("reviewerUserId", getReviewerUserId())
                .append("reviewerName", getReviewerName())
                .append("reviewerRole", getReviewerRole())
                .append("assignmentType", getAssignmentType())
                .append("commentText", getCommentText())
                .append("dataSnapshotJson", getDataSnapshotJson())
                .append("createTime", getCreateTime())
                .append("remark", getRemark())
                .toString();
    }
}
