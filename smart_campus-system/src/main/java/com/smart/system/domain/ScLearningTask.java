package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.common.annotation.Excel;
import com.smart.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ScLearningTask extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long taskId;
    @Excel(name = "任务标题")
    private String taskTitle;
    @Excel(name = "任务类型")
    private String taskType;
    @Excel(name = "业务类型")
    private String bizType;
    private Long bizId;
    private Long courseId;
    private Long classCourseId;
    private Long termId;
    @Excel(name = "任务描述")
    private String taskDesc;
    @Excel(name = "优先级")
    private String priorityLevel;
    @Excel(name = "动作类型")
    private String actionType;
    @Excel(name = "动作路径")
    private String actionPath;
    private Long actionTargetId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dueTime;

    @Excel(name = "状态")
    private String status;
    private String targetScope;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getClassCourseId() { return classCourseId; }
    public void setClassCourseId(Long classCourseId) { this.classCourseId = classCourseId; }
    public Long getTermId() { return termId; }
    public void setTermId(Long termId) { this.termId = termId; }
    public String getTaskDesc() { return taskDesc; }
    public void setTaskDesc(String taskDesc) { this.taskDesc = taskDesc; }
    public String getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(String priorityLevel) { this.priorityLevel = priorityLevel; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getActionPath() { return actionPath; }
    public void setActionPath(String actionPath) { this.actionPath = actionPath; }
    public Long getActionTargetId() { return actionTargetId; }
    public void setActionTargetId(Long actionTargetId) { this.actionTargetId = actionTargetId; }
    public Date getPublishTime() { return publishTime; }
    public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getDueTime() { return dueTime; }
    public void setDueTime(Date dueTime) { this.dueTime = dueTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTargetScope() { return targetScope; }
    public void setTargetScope(String targetScope) { this.targetScope = targetScope; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("taskId", getTaskId())
                .append("taskTitle", getTaskTitle())
                .append("taskType", getTaskType())
                .append("bizType", getBizType())
                .append("bizId", getBizId())
                .append("courseId", getCourseId())
                .append("classCourseId", getClassCourseId())
                .append("termId", getTermId())
                .append("taskDesc", getTaskDesc())
                .append("priorityLevel", getPriorityLevel())
                .append("actionType", getActionType())
                .append("actionPath", getActionPath())
                .append("actionTargetId", getActionTargetId())
                .append("publishTime", getPublishTime())
                .append("startTime", getStartTime())
                .append("dueTime", getDueTime())
                .append("status", getStatus())
                .append("targetScope", getTargetScope())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
