package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 考试行为日志对象 sc_exam_behavior_log
 */
public class ScExamBehaviorLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long sessionId;
    private Long recordId;
    private String behaviorType;
    private Integer behaviorCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date behaviorTime;
    private String behaviorData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(String behaviorType) {
        this.behaviorType = behaviorType;
    }

    public Integer getBehaviorCount() {
        return behaviorCount;
    }

    public void setBehaviorCount(Integer behaviorCount) {
        this.behaviorCount = behaviorCount;
    }

    public Date getBehaviorTime() {
        return behaviorTime;
    }

    public void setBehaviorTime(Date behaviorTime) {
        this.behaviorTime = behaviorTime;
    }

    public String getBehaviorData() {
        return behaviorData;
    }

    public void setBehaviorData(String behaviorData) {
        this.behaviorData = behaviorData;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("sessionId", getSessionId())
                .append("recordId", getRecordId())
                .append("behaviorType", getBehaviorType())
                .append("behaviorCount", getBehaviorCount())
                .append("behaviorTime", getBehaviorTime())
                .append("behaviorData", getBehaviorData())
                .toString();
    }
}
