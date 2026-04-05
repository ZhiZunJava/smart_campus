package com.smart.system.domain.campusvo;

import com.smart.common.annotation.Excel;

public class PortalCourseOfferingExportVo {
    @Excel(name = "学期", sort = 10, width = 16, wrapText = true)
    private String termName;

    @Excel(name = "课程名称", sort = 20, width = 24, wrapText = true)
    private String courseName;

    @Excel(name = "课程代码", sort = 30, width = 16, wrapText = true)
    private String courseCode;

    @Excel(name = "教学班", sort = 40, width = 22, wrapText = true)
    private String teachingClassCode;

    @Excel(name = "学分", sort = 50, width = 8)
    private String credits;

    @Excel(name = "授课教师", sort = 60, width = 18, wrapText = true)
    private String teacherName;

    @Excel(name = "开课院系", sort = 70, width = 20, wrapText = true)
    private String openDeptName;

    @Excel(name = "上课专业", sort = 80, width = 18, wrapText = true)
    private String major;

    @Excel(name = "行政班", sort = 90, width = 20, wrapText = true)
    private String className;

    @Excel(name = "课程类别", sort = 100, width = 14, wrapText = true)
    private String courseCategory;

    @Excel(name = "必修选修", sort = 110, width = 10)
    private String requiredLabel;

    @Excel(name = "校区", sort = 120, width = 14, wrapText = true)
    private String campusName;

    @Excel(name = "上课时间地点", sort = 130, width = 44, wrapText = true)
    private String scheduleText;

    @Excel(name = "典型节次", sort = 140, width = 24, wrapText = true)
    private String sectionSummary;

    @Excel(name = "考核方式", sort = 150, width = 14, wrapText = true)
    private String assessmentType;

    @Excel(name = "教学语言", sort = 160, width = 14, wrapText = true)
    private String teachingLanguage;

    @Excel(name = "总学时", sort = 170, width = 8)
    private Integer totalHours;

    @Excel(name = "周学时", sort = 180, width = 8)
    private Integer weeklyHours;

    @Excel(name = "选课人数", sort = 190, width = 14, wrapText = true)
    private String studentCountText;

    @Excel(name = "教学材料", sort = 200, width = 28, wrapText = true)
    private String materialHint;

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
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

    public String getTeachingClassCode() {
        return teachingClassCode;
    }

    public void setTeachingClassCode(String teachingClassCode) {
        this.teachingClassCode = teachingClassCode;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

    public String getRequiredLabel() {
        return requiredLabel;
    }

    public void setRequiredLabel(String requiredLabel) {
        this.requiredLabel = requiredLabel;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public String getScheduleText() {
        return scheduleText;
    }

    public void setScheduleText(String scheduleText) {
        this.scheduleText = scheduleText;
    }

    public String getSectionSummary() {
        return sectionSummary;
    }

    public void setSectionSummary(String sectionSummary) {
        this.sectionSummary = sectionSummary;
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

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public Integer getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(Integer weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public String getStudentCountText() {
        return studentCountText;
    }

    public void setStudentCountText(String studentCountText) {
        this.studentCountText = studentCountText;
    }

    public String getMaterialHint() {
        return materialHint;
    }

    public void setMaterialHint(String materialHint) {
        this.materialHint = materialHint;
    }
}
