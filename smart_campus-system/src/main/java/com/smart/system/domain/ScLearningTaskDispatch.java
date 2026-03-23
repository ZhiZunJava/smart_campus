package com.smart.system.domain;

import java.util.Date;
import com.smart.common.annotation.Excel;
import com.smart.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ScLearningTaskDispatch extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long dispatchId;
    private Long taskId;
    @Excel(name = "用户ID")
    private Long userId;
    @Excel(name = "派发状态")
    private String dispatchStatus;
    @Excel(name = "完成状态")
    private String completionStatus;
    private Date completedTime;
    private Date readTime;
    @Excel(name = "完成说明")
    private String completionRemark;

    public Long getDispatchId() { return dispatchId; }
    public void setDispatchId(Long dispatchId) { this.dispatchId = dispatchId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDispatchStatus() { return dispatchStatus; }
    public void setDispatchStatus(String dispatchStatus) { this.dispatchStatus = dispatchStatus; }
    public String getCompletionStatus() { return completionStatus; }
    public void setCompletionStatus(String completionStatus) { this.completionStatus = completionStatus; }
    public Date getCompletedTime() { return completedTime; }
    public void setCompletedTime(Date completedTime) { this.completedTime = completedTime; }
    public Date getReadTime() { return readTime; }
    public void setReadTime(Date readTime) { this.readTime = readTime; }
    public String getCompletionRemark() { return completionRemark; }
    public void setCompletionRemark(String completionRemark) { this.completionRemark = completionRemark; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("dispatchId", getDispatchId())
                .append("taskId", getTaskId())
                .append("userId", getUserId())
                .append("dispatchStatus", getDispatchStatus())
                .append("completionStatus", getCompletionStatus())
                .append("completedTime", getCompletedTime())
                .append("readTime", getReadTime())
                .append("completionRemark", getCompletionRemark())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
