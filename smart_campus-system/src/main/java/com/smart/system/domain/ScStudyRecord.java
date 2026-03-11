package com.smart.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 学习行为记录对象 sc_study_record
 *
 * @author Codex
 */
public class ScStudyRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long recordId;
    private Long userId;
    private Long courseId;
    private Long chapterId;
    private Long resourceId;
    private String behaviorType;
    private Integer durationSeconds;
    private BigDecimal progressRate;
    private String deviceType;
    private String sourcePage;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
    public String getBehaviorType() { return behaviorType; }
    public void setBehaviorType(String behaviorType) { this.behaviorType = behaviorType; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
    public BigDecimal getProgressRate() { return progressRate; }
    public void setProgressRate(BigDecimal progressRate) { this.progressRate = progressRate; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public String getSourcePage() { return sourcePage; }
    public void setSourcePage(String sourcePage) { this.sourcePage = sourcePage; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("userId", getUserId())
            .append("courseId", getCourseId())
            .append("chapterId", getChapterId())
            .append("resourceId", getResourceId())
            .append("behaviorType", getBehaviorType())
            .append("durationSeconds", getDurationSeconds())
            .append("progressRate", getProgressRate())
            .append("deviceType", getDeviceType())
            .append("sourcePage", getSourcePage())
            .append("createTime", getCreateTime())
            .toString();
    }
}
