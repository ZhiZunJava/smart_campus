package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

public class ScClassCourse extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long classId;
    private String className;
    private Long courseId;
    private String courseName;
    private Long teacherId;
    private String teacherName;
    private Long termId;
    private String termName;
    private String businessType;
    private String teachingClassCode;
    private java.math.BigDecimal credits;
    private String courseCategory;
    private Long openDeptId;
    private String openDeptName;
    private String major;
    private String campusName;
    private String assessmentType;
    private String teachingLanguage;
    private String prerequisiteCourse;
    private String taskType;
    private String requiredFlag;
    private String courseLevelRequirement;
    private Integer totalHours;
    private Integer requiredWeeks;
    private Integer weeklyHours;
    private Integer arrangedHours;
    private Integer studentLimit;
    private Integer actualStudentCount;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getTeachingClassCode() {
        return teachingClassCode;
    }

    public void setTeachingClassCode(String teachingClassCode) {
        this.teachingClassCode = teachingClassCode;
    }

    public java.math.BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(java.math.BigDecimal credits) {
        this.credits = credits;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

    public Long getOpenDeptId() {
        return openDeptId;
    }

    public void setOpenDeptId(Long openDeptId) {
        this.openDeptId = openDeptId;
    }

    public String getOpenDeptName() {
        return openDeptName;
    }

    public void setOpenDeptName(String openDeptName) {
        this.openDeptName = openDeptName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public String getTeachingLanguage() {
        return teachingLanguage;
    }

    public void setTeachingLanguage(String teachingLanguage) {
        this.teachingLanguage = teachingLanguage;
    }

    public String getPrerequisiteCourse() {
        return prerequisiteCourse;
    }

    public void setPrerequisiteCourse(String prerequisiteCourse) {
        this.prerequisiteCourse = prerequisiteCourse;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getRequiredFlag() {
        return requiredFlag;
    }

    public void setRequiredFlag(String requiredFlag) {
        this.requiredFlag = requiredFlag;
    }

    public String getCourseLevelRequirement() {
        return courseLevelRequirement;
    }

    public void setCourseLevelRequirement(String courseLevelRequirement) {
        this.courseLevelRequirement = courseLevelRequirement;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public Integer getRequiredWeeks() {
        return requiredWeeks;
    }

    public void setRequiredWeeks(Integer requiredWeeks) {
        this.requiredWeeks = requiredWeeks;
    }

    public Integer getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(Integer weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public Integer getArrangedHours() {
        return arrangedHours;
    }

    public void setArrangedHours(Integer arrangedHours) {
        this.arrangedHours = arrangedHours;
    }

    public Integer getStudentLimit() {
        return studentLimit;
    }

    public void setStudentLimit(Integer studentLimit) {
        this.studentLimit = studentLimit;
    }

    public Integer getActualStudentCount() {
        return actualStudentCount;
    }

    public void setActualStudentCount(Integer actualStudentCount) {
        this.actualStudentCount = actualStudentCount;
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
                .append("id", getId())
                .append("classId", getClassId())
                .append("className", getClassName())
                .append("courseId", getCourseId())
                .append("courseName", getCourseName())
                .append("teacherId", getTeacherId())
                .append("teacherName", getTeacherName())
                .append("termId", getTermId())
                .append("termName", getTermName())
                .append("businessType", getBusinessType())
                .append("teachingClassCode", getTeachingClassCode())
                .append("credits", getCredits())
                .append("courseCategory", getCourseCategory())
                .append("openDeptId", getOpenDeptId())
                .append("openDeptName", getOpenDeptName())
                .append("major", getMajor())
                .append("campusName", getCampusName())
                .append("assessmentType", getAssessmentType())
                .append("teachingLanguage", getTeachingLanguage())
                .append("prerequisiteCourse", getPrerequisiteCourse())
                .append("taskType", getTaskType())
                .append("requiredFlag", getRequiredFlag())
                .append("courseLevelRequirement", getCourseLevelRequirement())
                .append("totalHours", getTotalHours())
                .append("requiredWeeks", getRequiredWeeks())
                .append("weeklyHours", getWeeklyHours())
                .append("arrangedHours", getArrangedHours())
                .append("studentLimit", getStudentLimit())
                .append("actualStudentCount", getActualStudentCount())
                .append("status", getStatus())
                .append("remark", getRemark())
                .toString();
    }
}
