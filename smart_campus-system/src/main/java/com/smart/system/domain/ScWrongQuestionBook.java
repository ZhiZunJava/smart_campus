package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 错题本对象 sc_wrong_question_book
 *
 * @author can
 */
public class ScWrongQuestionBook extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long questionId;
    private Long courseId;
    private Integer wrongCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastWrongTime;
    private String masteryStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }

    public Date getLastWrongTime() {
        return lastWrongTime;
    }

    public void setLastWrongTime(Date lastWrongTime) {
        this.lastWrongTime = lastWrongTime;
    }

    public String getMasteryStatus() {
        return masteryStatus;
    }

    public void setMasteryStatus(String masteryStatus) {
        this.masteryStatus = masteryStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("questionId", getQuestionId())
                .append("courseId", getCourseId())
                .append("wrongCount", getWrongCount())
                .append("lastWrongTime", getLastWrongTime())
                .append("masteryStatus", getMasteryStatus())
                .toString();
    }
}
