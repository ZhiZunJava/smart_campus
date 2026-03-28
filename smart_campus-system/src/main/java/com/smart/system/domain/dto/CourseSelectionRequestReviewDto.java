package com.smart.system.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CourseSelectionRequestReviewDto {
    @NotNull(message = "申请ID不能为空")
    private Long requestId;
    @NotBlank(message = "审核状态不能为空")
    private String requestStatus;
    @Size(max = 500, message = "审核说明不能超过500字")
    private String reviewRemark;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getReviewRemark() {
        return reviewRemark;
    }

    public void setReviewRemark(String reviewRemark) {
        this.reviewRemark = reviewRemark;
    }
}
