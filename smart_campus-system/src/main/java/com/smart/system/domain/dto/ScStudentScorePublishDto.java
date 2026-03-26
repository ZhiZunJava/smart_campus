package com.smart.system.domain.dto;

public class ScStudentScorePublishDto {
    private Long classCourseId;
    private Long[] scoreIds;
    private String publishStatus;

    public Long getClassCourseId() {
        return classCourseId;
    }

    public void setClassCourseId(Long classCourseId) {
        this.classCourseId = classCourseId;
    }

    public Long[] getScoreIds() {
        return scoreIds;
    }

    public void setScoreIds(Long[] scoreIds) {
        this.scoreIds = scoreIds;
    }

    public String getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }
}
