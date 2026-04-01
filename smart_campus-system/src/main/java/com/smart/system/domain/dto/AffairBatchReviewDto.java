package com.smart.system.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class AffairBatchReviewDto {
    @NotEmpty(message = "申请列表不能为空")
    private List<Long> requestIds;

    @NotBlank(message = "审核动作不能为空")
    private String actionType;

    private String commentText;

    public List<Long> getRequestIds() {
        return requestIds;
    }

    public void setRequestIds(List<Long> requestIds) {
        this.requestIds = requestIds;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
