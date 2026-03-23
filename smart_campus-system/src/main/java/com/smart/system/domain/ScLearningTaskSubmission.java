package com.smart.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.smart.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ScLearningTaskSubmission extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long submissionId;
    private Long dispatchId;
    private Long taskId;
    private Long userId;
    private String submitContent;
    private String attachmentUrls;
    private Date submitTime;
    private String reviewStatus;
    private BigDecimal score;
    private String teacherFeedback;

    public Long getSubmissionId() { return submissionId; }
    public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }
    public Long getDispatchId() { return dispatchId; }
    public void setDispatchId(Long dispatchId) { this.dispatchId = dispatchId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSubmitContent() { return submitContent; }
    public void setSubmitContent(String submitContent) { this.submitContent = submitContent; }
    public String getAttachmentUrls() { return attachmentUrls; }
    public void setAttachmentUrls(String attachmentUrls) { this.attachmentUrls = attachmentUrls; }
    public Date getSubmitTime() { return submitTime; }
    public void setSubmitTime(Date submitTime) { this.submitTime = submitTime; }
    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public String getTeacherFeedback() { return teacherFeedback; }
    public void setTeacherFeedback(String teacherFeedback) { this.teacherFeedback = teacherFeedback; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("submissionId", getSubmissionId())
                .append("dispatchId", getDispatchId())
                .append("taskId", getTaskId())
                .append("userId", getUserId())
                .append("submitContent", getSubmitContent())
                .append("attachmentUrls", getAttachmentUrls())
                .append("submitTime", getSubmitTime())
                .append("reviewStatus", getReviewStatus())
                .append("score", getScore())
                .append("teacherFeedback", getTeacherFeedback())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
