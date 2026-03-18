package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

public class ScCourseSchedule extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long scheduleId;
    private Long termId;
    private Long classCourseId;
    private Long classroomId;
    private Integer weekDay;
    private Integer startSection;
    private Integer endSection;
    private String classroom;
    private String classroomName;
    private String buildingName;
    private String campusName;
    private String weeksText;
    private String weeksJson;
    private String status;

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public Long getClassCourseId() {
        return classCourseId;
    }

    public void setClassCourseId(Long classCourseId) {
        this.classCourseId = classCourseId;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(Integer weekDay) {
        this.weekDay = weekDay;
    }

    public Integer getStartSection() {
        return startSection;
    }

    public void setStartSection(Integer startSection) {
        this.startSection = startSection;
    }

    public Integer getEndSection() {
        return endSection;
    }

    public void setEndSection(Integer endSection) {
        this.endSection = endSection;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public String getWeeksText() {
        return weeksText;
    }

    public void setWeeksText(String weeksText) {
        this.weeksText = weeksText;
    }

    public String getWeeksJson() {
        return weeksJson;
    }

    public void setWeeksJson(String weeksJson) {
        this.weeksJson = weeksJson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("scheduleId", getScheduleId())
                .append("termId", getTermId())
                .append("classCourseId", getClassCourseId())
                .append("classroomId", getClassroomId())
                .append("weekDay", getWeekDay())
                .append("startSection", getStartSection())
                .append("endSection", getEndSection())
                .append("classroom", getClassroom())
                .append("classroomName", getClassroomName())
                .append("buildingName", getBuildingName())
                .append("campusName", getCampusName())
                .append("weeksText", getWeeksText())
                .append("weeksJson", getWeeksJson())
                .append("status", getStatus())
                .append("remark", getRemark())
                .toString();
    }
}
