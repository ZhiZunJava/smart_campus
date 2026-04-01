package com.smart.system.domain.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class VerificationBatchReviewDto {
    @NotEmpty(message = "记录列表不能为空")
    private List<Long> recordIds;

    @NotBlank(message = "审核动作不能为空")
    private String actionType;

    private String reviewComment;

    public List<Long> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }
}
