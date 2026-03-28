package com.smart.system.domain.dto;

import java.math.BigDecimal;
import java.util.List;

public class PortalCourseOfferingExportRequest {
    private Long termId;
    private String courseName;
    private String courseCode;
    private String courseCategory;
    private Long openDeptId;
    private Long teacherId;
    private String campusName;
    private String requiredFlag;
    private String assessmentType;
    private BigDecimal credits;
    private BigDecimal creditsMin;
    private BigDecimal creditsMax;
    private String teachingClassCode;
    private String className;
    private String major;
    private String businessType;
    private String teachingLanguage;
    private List<Long> classCourseIds;
    private List<String> exportFields;

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public String getRequiredFlag() {
        return requiredFlag;
    }

    public void setRequiredFlag(String requiredFlag) {
        this.requiredFlag = requiredFlag;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    public BigDecimal getCreditsMin() {
        return creditsMin;
    }

    public void setCreditsMin(BigDecimal creditsMin) {
        this.creditsMin = creditsMin;
    }

    public BigDecimal getCreditsMax() {
        return creditsMax;
    }

    public void setCreditsMax(BigDecimal creditsMax) {
        this.creditsMax = creditsMax;
    }

    public String getTeachingClassCode() {
        return teachingClassCode;
    }

    public void setTeachingClassCode(String teachingClassCode) {
        this.teachingClassCode = teachingClassCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getTeachingLanguage() {
        return teachingLanguage;
    }

    public void setTeachingLanguage(String teachingLanguage) {
        this.teachingLanguage = teachingLanguage;
    }

    public List<Long> getClassCourseIds() {
        return classCourseIds;
    }

    public void setClassCourseIds(List<Long> classCourseIds) {
        this.classCourseIds = classCourseIds;
    }

    public List<String> getExportFields() {
        return exportFields;
    }

    public void setExportFields(List<String> exportFields) {
        this.exportFields = exportFields;
    }
}
