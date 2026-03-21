package com.smart.system.domain.dto;

import java.util.List;

/**
 * 错题回练组卷 DTO
 */
public class WrongQuestionRetryDto {
    private Long userId;
    private Long courseId;
    private List<Long> wrongIds;
    private List<Long> questionIds;
    private String paperName;
    private Integer durationMinutes;
    private Boolean savePaper;

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

    public List<Long> getWrongIds() {
        return wrongIds;
    }

    public void setWrongIds(List<Long> wrongIds) {
        this.wrongIds = wrongIds;
    }

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Boolean getSavePaper() {
        return savePaper;
    }

    public void setSavePaper(Boolean savePaper) {
        this.savePaper = savePaper;
    }
}
