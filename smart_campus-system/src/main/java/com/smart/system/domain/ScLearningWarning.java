package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 学情预警对象 sc_learning_warning
 *
 * @author can
 */
public class ScLearningWarning extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long warningId;
    private Long userId;
    private Long courseId;
    private String warningType;
    private String warningLevel;
    private String warningContent;
    private String suggestion;
    private String processStatus;
    private String sourceType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date processTime;

    public Long getWarningId() {
        return warningId;
    }

    public void setWarningId(Long warningId) {
        this.warningId = warningId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getWarningType() {
        return warningType;
    }

    public void setWarningType(String warningType) {
        this.warningType = warningType;
    }

    public String getWarningLevel() {
        return warningLevel;
    }

    public void setWarningLevel(String warningLevel) {
        this.warningLevel = warningLevel;
    }

    public String getWarningContent() {
        return warningContent;
    }

    public void setWarningContent(String warningContent) {
        this.warningContent = warningContent;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("warningId", getWarningId())
                .append("userId", getUserId())
                .append("courseId", getCourseId())
                .append("warningType", getWarningType())
                .append("warningLevel", getWarningLevel())
                .append("warningContent", getWarningContent())
                .append("suggestion", getSuggestion())
                .append("processStatus", getProcessStatus())
                .append("sourceType", getSourceType())
                .append("createTime", getCreateTime())
                .append("processTime", getProcessTime())
                .toString();
    }
}
